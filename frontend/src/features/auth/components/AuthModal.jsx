import { useState } from 'react';
import { ApiError } from '@/shared/services/api';

/**
 * Modal d'authentification.
 * onSuccess reçoit le hook useAuth login/register — la logique API est dans useAuth.
 */
export function AuthModal({ mode, onClose, onLogin, onRegister }) {
  const [form, setForm] = useState({ nom: '', email: '', motDePasse: '' });
  const [isLogin, setIsLogin] = useState(mode === 'login');
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.email) {
      e.email = 'Courriel requis';
    }
    if (!form.motDePasse || form.motDePasse.length < 6) {
      e.motDePasse = 'Minimum 6 caractères';
    }
    if (!isLogin && !form.nom) {
      e.nom = 'Nom requis';
    }
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) {
      return;
    }
    setLoading(true);
    setServerError('');
    try {
      if (isLogin) {
        await onLogin({ email: form.email, motDePasse: form.motDePasse });
      } else {
        await onRegister({ nom: form.nom, email: form.email, motDePasse: form.motDePasse });
      }
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          setServerError('Identifiants incorrects.');
        } else if (err.status === 409) {
          setServerError('Un compte existe déjà avec cet email.');
        } else {
          setServerError(err.message || 'Une erreur est survenue.');
        }
      } else {
        setServerError('Impossible de joindre le serveur.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleOverlayKeyDown = (e) => {
    if (e.key === 'Escape') {
      onClose();
    }
  };

  const field = (key, label, type = 'text', placeholder = '') => (
    <div className="modal-field">
      <label className="modal-label" htmlFor={key}>
        {label}
      </label>
      <input
        id={key}
        className="modal-input"
        type={type}
        placeholder={placeholder}
        value={form[key]}
        onChange={(e) => setForm({ ...form, [key]: e.target.value })}
        autoComplete={key === 'motDePasse' ? (isLogin ? 'current-password' : 'new-password') : key}
        disabled={loading}
      />
      {errors[key] && <span style={{ color: '#EB5757', fontSize: '0.75rem' }}>{errors[key]}</span>}
    </div>
  );

  return (
    <div
      className="modal-overlay"
      role="presentation"
      onClick={handleOverlayClick}
      onKeyDown={handleOverlayKeyDown}
    >
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <button className="modal-close" onClick={onClose} aria-label="Fermer" disabled={loading}>
          ✕
        </button>

        <div id="modal-title" className="modal-title">
          {isLogin ? 'Bon retour !' : 'Créer un compte'}
        </div>
        <div className="modal-sub">
          {isLogin
            ? 'Connectez-vous pour accéder à vos trajets.'
            : 'Rejoignez la communauté AllerRetour.'}
        </div>

        {!isLogin && field('nom', 'Nom complet', 'text', 'Marie Tremblay')}
        {field('email', 'Courriel', 'email', 'vous@exemple.ca')}
        {field('motDePasse', 'Mot de passe', 'password', '••••••••')}

        {serverError && (
          <div
            style={{
              color: '#b91c1c',
              background: 'rgba(235,87,87,0.1)',
              border: '1px solid rgba(235,87,87,0.3)',
              borderRadius: 8,
              padding: '10px 14px',
              fontSize: '0.85rem',
              marginBottom: '0.5rem',
            }}
          >
            {serverError}
          </div>
        )}

        <button className="modal-submit" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Chargement...' : isLogin ? 'Se connecter →' : 'Créer mon compte →'}
        </button>

        <div className="modal-switch">
          {isLogin ? (
            <>
              Pas encore de compte ?{' '}
              <button
                type="button"
                onClick={() => {
                  setIsLogin(false);
                  setServerError('');
                }}
              >
                S&apos;inscrire
              </button>
            </>
          ) : (
            <>
              Déjà un compte ?{' '}
              <button
                type="button"
                onClick={() => {
                  setIsLogin(true);
                  setServerError('');
                }}
              >
                Se connecter
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
