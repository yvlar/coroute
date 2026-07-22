import { COLORS } from '@/shared/constants/colors';
import { MOCK_ROUTES } from '../data/mockRoutes';
import { RouteCard } from './RouteCard';

export function RoutesFixesSection({ onSelectRoute }) {
  return (
    <section className="section" aria-labelledby="routes-title">
      <div className="section-header">
        <div>
          <div className="section-num">⬛ Routes populaires</div>
          <h2 id="routes-title" className="section-title">
            Lignes fixes disponibles
          </h2>
          <div className="section-subtitle">
            Des trajets réguliers comme des lignes de transport — sans l&apos;autobus.
          </div>
        </div>
      </div>

      <div className="road-divider" aria-hidden="true">
        <div className="road-dot" style={{ background: COLORS.red }} />
        <div className="road-dot" style={{ background: COLORS.green }} />
        <div className="road-dot" style={{ background: COLORS.green }} />
        <div className="road-dash" />
      </div>

      <div className="routes-grid">
        {MOCK_ROUTES.map((r) => (
          <RouteCard key={r.id} route={r} onSelect={onSelectRoute} />
        ))}
      </div>
    </section>
  );
}
