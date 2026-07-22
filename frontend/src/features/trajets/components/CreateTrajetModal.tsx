import React, { useState } from 'react';
import { useCreateTrajet } from '@/shared/hooks/useCreateTrajet';
import { JOURS_LABELS, JOUR_LABEL_TO_ENUM } from '@/shared/constants/jours';
import type { TrajetCreateRequest, TrajetType, JourSemaine } from '@/shared/types/trajet';

// ─── Types locaux ─────────────────────────────────────────────────────────────

interface FormState {
  depart: string;
  destination: string;
  heure: string;
  placesDisponibles: string;
  prixParPassager: string;
  type: TrajetType;
  // PONCTUEL
  date: string;
  // REGULIER
  joursSelectionnes: string[]; // labels UI ex: ['Lun', 'Mer']
  dateDebut: string;
  dateFin: string;
}

interface FormErrors {
  depart?: string;
  destination?: string;
  heure?: string;
  placesDisponibles?: string;
  prixParPassager?: string;
  date?: string;
  jours?: string;
  dateDebut?: string;
  dateFin?: string;
}

interface CreateTrajetModalProps {
  token: string;
  onClose: () => void;
  onSuccess: () => void;
}

// ─── Valeur initiale du formulaire ───────────────────────────────────────────

const INITIAL_FORM: FormState = {
  depart: '',
  destination: '',
  heure: '07:00',
  placesDisponibles: '3',
  prixParPassager: '10',
  type: 'REGULIER',
  date: '',
  joursSelectionnes: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven'],
  dateDebut: '',
  dateFin: '',
};

// ─── Composant ────────────────────────────────────────────────────────────────

export function CreateTrajetModal({ token, onClose, onSuccess }: CreateTrajetModalProps) {
  const [form, setForm] = useState<FormState>(INITIAL_FORM);
  const [errors, setErrors] = useState<FormErrors>({});
  const { loading, error: serverError, submitTrajet } = useCreateTrajet();

  // ── Validation ──────────────────────────────────────────────────────────────

  const validate = (): boolean => {
    const e: FormErrors = {};

    if (!form.depart.trim()) {
      e.depart = 'Ville de départ requise';
    }
    if (!form.destination.trim()) {
      e.destination = 'Destination requise';
    }
    if (!form.heure) {
      e.heure = 'Heure requise';
    }

    const places = parseInt(form.placesDisponibles, 10);
    if (isNaN(places) || places < 1) {
      e.placesDisponibles = 'Minimum 1 place';
    }

    const prix = parseFloat(form.prixParPassager);
    if (isNaN(prix) || prix <= 0) {
      e.prixParPassager = 'Prix doit être positif';
    }

    if (form.type === 'PONCTUEL') {
      if (!form.date) {
        e.date = 'Date requise pour un trajet ponctuel';
      }
    } else {
      if (form.joursSelectionnes.length === 0) {
        e.jours = 'Sélectionnez au moins un jour';
      }
      if (!form.dateDebut) {
        e.dateDebut = 'Date de début requise';
      }
      if (!form.dateFin) {
        e.dateFin = 'Date de fin requise';
      }
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  // ── Soumission ──────────────────────────────────────────────────────────────

  const handleSubmit = async () => {
    if (!validate()) {
      return;
    }

    // Construire le payload selon le type du backend
    const heureIso = form.heure.length === 5 ? `${form.heure}:00` : form.heure;

    const payload: TrajetCreateRequest = {
      depart: form.depart.trim(),
      destination: form.destination.trim(),
      heure: heureIso,
      placesDisponibles: parseInt(form.placesDisponibles, 10),
      prixParPassager: parseFloat(form.prixParPassager),
      type: form.type,
    };

    if (form.type === 'PONCTUEL') {
      payload.date = form.date;
    } else {
      payload.joursRecurrence = form.joursSelectionnes
        .map((label) => JOUR_LABEL_TO_ENUM[label] as JourSemaine)
        .filter(Boolean);
      payload.dateDebut = form.dateDebut;
      payload.dateFin = form.dateFin;
    }

    const ok = await submitTrajet(payload, token);
    if (ok) {
      onSuccess();
    }
  };

  // ── Toggle jour ─────────────────────────────────────────────────────────────

  const toggleJour = (jour: string) => {
    setForm((prev) => ({
      ...prev,
      joursSelectionnes: prev.joursSelectionnes.includes(jour)
        ? prev.joursSelectionnes.filter((j) => j !== jour)
        : [...prev.joursSelectionnes, jour],
    }));
  };

  const handleOverlay = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleOverlayKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Escape') {
      onClose();
    }
  };

  // ── Champ générique ─────────────────────────────────────────────────────────

  const field = (
    id: keyof FormState,
    label: string,
    type = 'text',
    placeholder = '',
    extra?: React.InputHTMLAttributes<HTMLInputElement>
  ) => (
    <div className="modal-field">
      <label className="modal-label" htmlFor={id}>
        {label}
      </label>
      <input
        id={id}
        className="modal-input"
        type={type}
        placeholder={placeholder}
        value={form[id] as string}
        onChange={(e) => setForm((prev) => ({ ...prev, [id]: e.target.value }))}
        disabled={loading}
        {...extra}
      />
      {errors[id as keyof FormErrors] && (
        <span style={{ color: '#EB5757', fontSize: '0.75rem' }}>
          {errors[id as keyof FormErrors]}
        </span>
      )}
    </div>
  );

  // ── Render ──────────────────────────────────────────────────────────────────

  return (
    <div
      className="modal-overlay"
      role="presentation"
      onClick={handleOverlay}
      onKeyDown={handleOverlayKeyDown}
    >
      <div
        className="modal create-trajet-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-trajet-title"
      >
        <button className="modal-close" onClick={onClose} aria-label="Fermer" disabled={loading}>
          ✕
        </button>

        <div id="create-trajet-title" className="modal-title">
          Proposer un trajet
        </div>
        <div className="modal-sub">Renseignez les informations de votre trajet.</div>

        {/* ── Départ / Destination ── */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
          {field('depart', 'Ville de départ', 'text', 'ex. Granby')}
          {field('destination', 'Destination', 'text', 'ex. Drummondville')}
        </div>

        {/* ── Heure / Places / Prix ── */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px' }}>
          {field('heure', 'Heure de départ', 'time', '', { step: 300 })}
          {field('placesDisponibles', 'Places', 'number', '3', { min: '1', max: '8' })}
          {field('prixParPassager', 'Prix ($)', 'number', '10', { min: '0', step: '0.5' })}
        </div>

        {/* ── Type de trajet ── */}
        <div className="modal-field">
          <label className="modal-label">Type de trajet</label>
          <div className="type-selector">
            {(['REGULIER', 'PONCTUEL'] as TrajetType[]).map((t) => (
              <button
                key={t}
                type="button"
                className={`type-btn ${form.type === t ? 'active' : ''}`}
                onClick={() => setForm((prev) => ({ ...prev, type: t }))}
                disabled={loading}
              >
                {t === 'REGULIER' ? '🔁 Régulier' : '📅 Ponctuel'}
              </button>
            ))}
          </div>
        </div>

        {/* ── Champs PONCTUEL ── */}
        {form.type === 'PONCTUEL' && (
          <div className="modal-field">
            <label className="modal-label" htmlFor="date">
              Date du trajet
            </label>
            <input
              id="date"
              className="modal-input"
              type="date"
              value={form.date}
              onChange={(e) => setForm((prev) => ({ ...prev, date: e.target.value }))}
              disabled={loading}
            />
            {errors.date && (
              <span style={{ color: '#EB5757', fontSize: '0.75rem' }}>{errors.date}</span>
            )}
          </div>
        )}

        {/* ── Champs REGULIER ── */}
        {form.type === 'REGULIER' && (
          <>
            <div className="modal-field">
              <label className="modal-label">Jours de récurrence</label>
              <div className="jours-selector">
                {JOURS_LABELS.map((jour) => (
                  <button
                    key={jour}
                    type="button"
                    className={`jour-chip ${form.joursSelectionnes.includes(jour) ? 'active' : ''}`}
                    onClick={() => toggleJour(jour)}
                    disabled={loading}
                    aria-pressed={form.joursSelectionnes.includes(jour)}
                  >
                    {jour}
                  </button>
                ))}
              </div>
              {errors.jours && (
                <span style={{ color: '#EB5757', fontSize: '0.75rem' }}>{errors.jours}</span>
              )}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <div className="modal-field">
                <label className="modal-label" htmlFor="dateDebut">
                  Date de début
                </label>
                <input
                  id="dateDebut"
                  className="modal-input"
                  type="date"
                  value={form.dateDebut}
                  onChange={(e) => setForm((prev) => ({ ...prev, dateDebut: e.target.value }))}
                  disabled={loading}
                />
                {errors.dateDebut && (
                  <span style={{ color: '#EB5757', fontSize: '0.75rem' }}>{errors.dateDebut}</span>
                )}
              </div>
              <div className="modal-field">
                <label className="modal-label" htmlFor="dateFin">
                  Date de fin
                </label>
                <input
                  id="dateFin"
                  className="modal-input"
                  type="date"
                  value={form.dateFin}
                  onChange={(e) => setForm((prev) => ({ ...prev, dateFin: e.target.value }))}
                  disabled={loading}
                />
                {errors.dateFin && (
                  <span style={{ color: '#EB5757', fontSize: '0.75rem' }}>{errors.dateFin}</span>
                )}
              </div>
            </div>
          </>
        )}

        {/* ── Erreur serveur ── */}
        {serverError && <div className="server-error-banner">{serverError}</div>}

        {/* ── Submit ── */}
        <button className="modal-submit" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Publication en cours...' : 'Publier le trajet →'}
        </button>
      </div>
    </div>
  );
}
