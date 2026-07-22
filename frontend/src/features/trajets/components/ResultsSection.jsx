import { COLORS } from '@/shared/constants/colors';
import { TrajetCard } from './TrajetCard';

export function ResultsSection({ results, query, onReserve, loading, error }) {
  if (loading) {
    return (
      <div className="section">
        <div className="empty-state">
          <div className="empty-icon">🔍</div>
          <div className="empty-title">Recherche en cours...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="section">
        <div className="empty-state">
          <div className="empty-icon">⚠️</div>
          <div className="empty-title">Erreur de recherche</div>
          <p style={{ fontSize: '0.875rem' }}>{error}</p>
        </div>
      </div>
    );
  }

  if (!results || results.length === 0) {
    return (
      <div className="section">
        <div className="empty-state">
          <div className="empty-icon">🛣️</div>
          <div className="empty-title">Aucun trajet trouvé</div>
          <p style={{ fontSize: '0.875rem' }}>Essayez d&apos;autres villes ou ajustez vos jours.</p>
        </div>
      </div>
    );
  }

  const titre =
    query.depart && query.destination
      ? `${query.depart} → ${query.destination}`
      : 'Tous les trajets disponibles';

  return (
    <div className="section">
      <div className="section-header">
        <div>
          <div className="section-num">● Résultats</div>
          <div className="section-title">{titre}</div>
          <div className="section-subtitle">
            {results.length} trajet{results.length > 1 ? 's' : ''} correspondant
            {results.length > 1 ? 's' : ''}
          </div>
        </div>
      </div>

      <div className="road-divider">
        <div className="road-dot" style={{ background: COLORS.green }} />
        <div className="road-dash" />
      </div>

      <div className="legend-row" aria-label="Légende des disponibilités">
        <div className="legend-item">
          <div className="legend-dot" style={{ background: COLORS.green }} />
          Disponible
        </div>
        <div className="legend-item">
          <div className="legend-dot" style={{ background: COLORS.yellow }} />
          Dernière place
        </div>
        <div className="legend-item">
          <div className="legend-dot" style={{ background: COLORS.red }} />
          Complet
        </div>
      </div>

      <div className="results-grid" role="list">
        {results.map((t) => (
          <TrajetCard key={t.id} trajet={t} onReserve={onReserve} />
        ))}
      </div>
    </div>
  );
}
