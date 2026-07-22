import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ResultsSection } from '@/features/trajets/components/ResultsSection';

const mockTrajet = {
  id: '11111111-0000-0000-0000-000000000001',
  depart: 'Granby',
  destination: 'Drummondville',
  heure: '07:00:00',
  prixParPassager: 8,
  placesRestantes: 3,
  type: 'REGULIER',
  joursCompatibles: ['LUNDI', 'MERCREDI'],
  scoreCompatibilite: 90,
  conducteurId: 'user-1',
  date: null,
  dateDebut: '2024-09-02',
  dateFin: '2025-06-20',
};

describe('ResultsSection', () => {
  it('affiche l\'état de chargement', () => {
    render(
      <ResultsSection results={null} query={{}} onReserve={() => {}} loading={true} error={null} />
    );
    expect(screen.getByText('Recherche en cours...')).toBeInTheDocument();
  });

  it('affiche l\'état d\'erreur', () => {
    render(
      <ResultsSection
        results={null}
        query={{}}
        onReserve={() => {}}
        loading={false}
        error="Erreur de connexion"
      />
    );
    expect(screen.getByText('Erreur de recherche')).toBeInTheDocument();
    expect(screen.getByText('Erreur de connexion')).toBeInTheDocument();
  });

  it('affiche l\'état vide si results est un tableau vide', () => {
    render(
      <ResultsSection results={[]} query={{}} onReserve={() => {}} loading={false} error={null} />
    );
    expect(screen.getByText('Aucun trajet trouvé')).toBeInTheDocument();
  });

  it('affiche l\'état vide si results est null', () => {
    render(
      <ResultsSection results={null} query={{}} onReserve={() => {}} loading={false} error={null} />
    );
    expect(screen.getByText('Aucun trajet trouvé')).toBeInTheDocument();
  });

  it('affiche les résultats quand il y en a', () => {
    render(
      <ResultsSection
        results={[mockTrajet]}
        query={{ depart: 'Granby', destination: 'Drummondville' }}
        onReserve={() => {}}
        loading={false}
        error={null}
      />
    );
    expect(screen.getByText('07h00')).toBeInTheDocument();
    expect(screen.getByText('8 $')).toBeInTheDocument();
  });

  it('affiche le titre avec depart → destination', () => {
    render(
      <ResultsSection
        results={[mockTrajet]}
        query={{ depart: 'Granby', destination: 'Drummondville' }}
        onReserve={() => {}}
        loading={false}
        error={null}
      />
    );
    expect(screen.getByText('Granby → Drummondville')).toBeInTheDocument();
  });

  it('affiche "Tous les trajets disponibles" quand la query est vide', () => {
    render(
      <ResultsSection
        results={[mockTrajet]}
        query={{}}
        onReserve={() => {}}
        loading={false}
        error={null}
      />
    );
    expect(screen.getByText('Tous les trajets disponibles')).toBeInTheDocument();
  });

  it('affiche le bon nombre de résultats', () => {
    render(
      <ResultsSection
        results={[mockTrajet, { ...mockTrajet, id: '2' }]}
        query={{ depart: 'Granby', destination: 'Drummondville' }}
        onReserve={() => {}}
        loading={false}
        error={null}
      />
    );
    expect(screen.getByText(/2 trajets correspondants/)).toBeInTheDocument();
  });

  it('passe onReserve aux TrajetCards', () => {
    const onReserve = vi.fn();
    render(
      <ResultsSection
        results={[mockTrajet]}
        query={{}}
        onReserve={onReserve}
        loading={false}
        error={null}
      />
    );
    expect(screen.getByRole('button', { name: /Réserver/i })).toBeInTheDocument();
  });
});
