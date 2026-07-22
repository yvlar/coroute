import { JOURS_LABELS, JOUR_ENUM_TO_LABEL } from '@/shared/constants/jours';
import { getTrajetStatus } from '../utils/trajetStatus';
import { formatHeure } from '../utils/formatters';

/**
 * Carte représentant un trajet individuel dans la liste des résultats.
 * Compatible avec TrajetResponse et MatchingResponse du backend CoRoute.
 *
 * Champs attendus (MatchingResponse) :
 *   id, depart, destination, heure (ISO "07:00:00"), prixParPassager,
 *   placesRestantes, joursCompatibles ([JourSemaine enum]), conducteurId,
 *   scoreCompatibilite, type
 */
export function TrajetCard({ trajet, onReserve }) {
  // MatchingResponse utilise joursCompatibles ; TrajetResponse utilise joursRecurrence
  const joursEnum = trajet.joursCompatibles ?? trajet.joursRecurrence ?? [];
  const joursLabels = joursEnum.map((e) => JOUR_ENUM_TO_LABEL[e]).filter(Boolean);

  const status = getTrajetStatus(trajet.placesRestantes);

  return (
    <div className="trajet-card" role="listitem">
      <div className="trajet-accent" style={{ background: status.color }} />
      <div className="trajet-body">
        <div className="trajet-main">
          <div className="trajet-heure">{formatHeure(trajet.heure)}</div>
          <div className="trajet-info">
            <div className="trajet-villes">
              {trajet.depart} <span style={{ color: status.color }}>→</span> {trajet.destination}
            </div>
            <div className="trajet-meta-row">
              <span className="trajet-conducteur" title={`ID: ${trajet.conducteurId}`}>
                🧑‍✈️ Conducteur
              </span>
              {trajet.scoreCompatibilite !== undefined && (
                <span className="trajet-note">✓ {trajet.scoreCompatibilite}% compatible</span>
              )}
              <div className="trajet-jours-mini" aria-label="Jours de disponibilité">
                {JOURS_LABELS.slice(0, 5).map((label) => (
                  <div
                    key={label}
                    className="trajet-jour-dot"
                    style={{
                      background: joursLabels.includes(label) ? status.color : '#dde8dd',
                    }}
                    title={label}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>

        <div className="trajet-right">
          <span
            className="trajet-badge"
            style={{ background: status.badgeBg, color: status.badgeColor }}
          >
            {status.label}
          </span>
          <div className="trajet-prix-block">
            <div className="trajet-prix-big">{trajet.prixParPassager} $</div>
            <div className="trajet-prix-sub">par trajet</div>
          </div>
          <button
            className="reserve-btn"
            onClick={() => onReserve(trajet)}
            disabled={trajet.placesRestantes === 0}
            style={{ background: trajet.placesRestantes === 0 ? '#ccc' : status.color }}
            aria-label={
              trajet.placesRestantes === 0
                ? 'Complet'
                : `Réserver ${trajet.depart} → ${trajet.destination}`
            }
          >
            {trajet.placesRestantes === 0 ? 'Complet' : 'Réserver →'}
          </button>
        </div>
      </div>
    </div>
  );
}
