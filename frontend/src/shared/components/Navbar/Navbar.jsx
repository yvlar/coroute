export function Navbar({ user, onLogin, onRegister, onLogout, onHome, onCreateTrajet }) {
  return (
    <nav className="nav" role="navigation" aria-label="Navigation principale">
      <button className="nav-logo" onClick={onHome} aria-label="Retour à l'accueil">
        Aller<span>Retour</span>
      </button>

      <div className="nav-right">
        {user ? (
          <div className="nav-user">
            <button
              className="nav-btn create"
              onClick={onCreateTrajet}
              aria-label="Proposer un trajet"
            >
              + Proposer un trajet
            </button>
            <div className="nav-avatar" aria-hidden="true">
              {user.initiales}
            </div>
            <span style={{ color: 'rgba(255,255,255,0.7)' }}>{user.prenom}</span>
            <button className="nav-btn" onClick={onLogout} style={{ marginLeft: 4 }}>
              Déconnexion
            </button>
          </div>
        ) : (
          <>
            <button className="nav-btn" onClick={onCreateTrajet}>
              + Proposer un trajet
            </button>
            <button className="nav-btn" onClick={onLogin}>
              Connexion
            </button>
            <button className="nav-btn primary" onClick={onRegister}>
              S&apos;inscrire
            </button>
          </>
        )}
      </div>
    </nav>
  );
}
