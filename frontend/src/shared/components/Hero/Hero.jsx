import { useState } from 'react';
import { JoursPicker } from '@/shared/components/JoursPicker';

const STATS = [
  { num: '2 400+', label: 'Usagers actifs' },
  { num: '148', label: 'Routes régulières' },
  { num: '38$', label: 'Économies / semaine' },
  { num: '-62%', label: 'CO₂ vs solo' },
];

export function Hero({ onSearch }) {
  const [depart, setDepart] = useState('');
  const [destination, setDestination] = useState('');
  const [jours, setJours] = useState(['Lun', 'Mar', 'Mer', 'Jeu', 'Ven']);

  const handleSearch = () => onSearch({ depart, destination, jours });

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <section className="hero" aria-label="Recherche de trajets">
      <div className="hero-badge">🌿 Covoiturage domicile-travail en région</div>
      <h1 className="hero-title">
        Votre trajet du quotidien,
        <br />
        <span>simplifié.</span>
      </h1>
      <p className="hero-sub">
        Trouvez un compagnon de route fixe pour vos trajets ruraux récurrents. Comme une ligne de
        bus, mais en covoiturage.
      </p>

      <div className="search-card" role="search">
        <div className="search-field">
          <label className="search-label" htmlFor="search-depart">
            Point de départ
          </label>
          <input
            id="search-depart"
            className="search-input"
            placeholder="ex. Granby"
            value={depart}
            onChange={(e) => setDepart(e.target.value)}
            onKeyDown={handleKeyDown}
            autoComplete="off"
          />
        </div>
        <div className="search-field">
          <label className="search-label" htmlFor="search-destination">
            Destination
          </label>
          <input
            id="search-destination"
            className="search-input"
            placeholder="ex. Drummondville"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            onKeyDown={handleKeyDown}
            autoComplete="off"
          />
        </div>
        <button className="search-submit" onClick={handleSearch}>
          Trouver →
        </button>
      </div>

      <JoursPicker selected={jours} onChange={setJours} />

      <div className="stats-row" aria-label="Statistiques AllerRetour">
        {STATS.map((s) => (
          <div key={s.label} className="stat-item">
            <div className="stat-num">{s.num}</div>
            <div className="stat-label">{s.label}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
