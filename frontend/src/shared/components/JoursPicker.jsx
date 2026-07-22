import { JOURS_LABELS } from '@/shared/constants/jours';

export function JoursPicker({ selected, onChange }) {
  const toggle = (jour) => {
    if (selected.includes(jour)) {
      onChange(selected.filter((j) => j !== jour));
    } else {
      onChange([...selected, jour]);
    }
  };

  return (
    <div className="jours-row" role="group" aria-label="Sélection des jours">
      {JOURS_LABELS.map((jour) => (
        <button
          key={jour}
          type="button"
          className={`jour-pill ${selected.includes(jour) ? 'active' : ''}`}
          onClick={() => toggle(jour)}
          aria-pressed={selected.includes(jour)}
        >
          {jour}
        </button>
      ))}
    </div>
  );
}
