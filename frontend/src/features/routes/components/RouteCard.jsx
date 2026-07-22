import { JOURS_LABELS } from '@/shared/constants/jours';

export function RouteCard({ route, onSelect }) {
  return (
    <button
      className="route-card"
      onClick={() => onSelect(route)}
      aria-label={`Ligne ${route.num} : ${route.depart} vers ${route.destination}`}
    >
      <div className="route-ligne">LIGNE {route.num}</div>
      <div className="route-trajet">
        <span className="route-ville">{route.depart}</span>
        <span className="route-arrow">→</span>
        <span className="route-ville">{route.destination}</span>
      </div>
      <div className="route-meta">
        <div className="route-meta-item">⏰ {route.heureDepart}</div>
      </div>
      <div className="route-jours">
        {JOURS_LABELS.slice(0, 5).map((j) => (
          <span key={j} className={`route-jour ${route.jours.includes(j) ? 'actif' : 'inactif'}`}>
            {j}
          </span>
        ))}
      </div>
      <div className="route-footer">
        <div className="route-count">
          <strong>{route.trajetCount}</strong> conducteurs actifs
        </div>
        <div className="route-prix">
          ~{route.prixMoyen}$ <span>/ trajet</span>
        </div>
      </div>
    </button>
  );
}
