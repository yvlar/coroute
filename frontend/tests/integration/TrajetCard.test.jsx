import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { TrajetCard } from '@/features/trajets/components/TrajetCard';

// Trajet aligné sur MatchingResponse du backend CoRoute
const mockTrajet = {
  id: '11111111-0000-0000-0000-000000000001',
  depart: 'Granby',
  destination: 'Drummondville',
  heure: '07:00:00',
  prixParPassager: 8,
  placesRestantes: 3,
  type: 'REGULIER',
  joursCompatibles: ['LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI'],
  scoreCompatibilite: 95,
  conducteurId: 'aaaa-0001',
  date: null,
  dateDebut: '2024-09-02',
  dateFin: '2025-06-20',
};

describe('TrajetCard', () => {
  it('affiche l\'heure formatée en "07h00"', () => {
    render(<TrajetCard trajet={mockTrajet} onReserve={() => {}} />);
    expect(screen.getByText('07h00')).toBeInTheDocument();
  });

  it('affiche la ville de départ et de destination', () => {
    render(<TrajetCard trajet={mockTrajet} onReserve={() => {}} />);
    expect(screen.getByText(/Granby/)).toBeInTheDocument();
    expect(screen.getByText(/Drummondville/)).toBeInTheDocument();
  });

  it('affiche le prix avec le champ prixParPassager', () => {
    render(<TrajetCard trajet={mockTrajet} onReserve={() => {}} />);
    expect(screen.getByText('8 $')).toBeInTheDocument();
  });

  it('affiche le badge "3 places" pour placesRestantes === 3', () => {
    render(<TrajetCard trajet={mockTrajet} onReserve={() => {}} />);
    expect(screen.getByText('3 places')).toBeInTheDocument();
  });

  it('affiche le badge "1 place" pour placesRestantes === 1', () => {
    const trajet = { ...mockTrajet, placesRestantes: 1 };
    render(<TrajetCard trajet={trajet} onReserve={() => {}} />);
    expect(screen.getByText('1 place')).toBeInTheDocument();
  });

  it('désactive le bouton et affiche "Complet" quand placesRestantes === 0', () => {
    const trajetComplet = { ...mockTrajet, placesRestantes: 0 };
    render(<TrajetCard trajet={trajetComplet} onReserve={() => {}} />);
    const btn = screen.getByRole('button', { name: /Complet/i });
    expect(btn).toBeDisabled();
  });

  it('appelle onReserve avec le trajet au clic sur Réserver', () => {
    const onReserve = vi.fn();
    render(<TrajetCard trajet={mockTrajet} onReserve={onReserve} />);
    fireEvent.click(screen.getByRole('button', { name: /Réserver/i }));
    expect(onReserve).toHaveBeenCalledWith(mockTrajet);
  });

  it('affiche le score de compatibilité si présent', () => {
    render(<TrajetCard trajet={mockTrajet} onReserve={() => {}} />);
    expect(screen.getByText(/95%/)).toBeInTheDocument();
  });

  it('fonctionne aussi avec joursRecurrence (TrajetResponse)', () => {
    const trajetResponse = {
      ...mockTrajet,
      joursCompatibles: undefined,
      joursRecurrence: ['LUNDI', 'VENDREDI'],
    };
    render(<TrajetCard trajet={trajetResponse} onReserve={() => {}} />);
    expect(screen.getByText(/Granby/)).toBeInTheDocument();
  });
});
