import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { RouteCard } from '@/features/routes/components/RouteCard';

const mockRoute = {
  id: 'L1',
  num: '01',
  depart: 'Granby',
  destination: 'Drummondville',
  heureDepart: '07h00',
  trajetCount: 12,
  prixMoyen: 8,
  jours: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven'],
};

describe('RouteCard', () => {
  it('affiche le numéro de ligne', () => {
    render(<RouteCard route={mockRoute} onSelect={() => {}} />);
    expect(screen.getByText(/LIGNE 01/)).toBeInTheDocument();
  });

  it('affiche les villes de départ et destination', () => {
    render(<RouteCard route={mockRoute} onSelect={() => {}} />);
    expect(screen.getByText('Granby')).toBeInTheDocument();
    expect(screen.getByText('Drummondville')).toBeInTheDocument();
  });

  it('affiche l\'heure de départ', () => {
    render(<RouteCard route={mockRoute} onSelect={() => {}} />);
    expect(screen.getByText(/07h00/)).toBeInTheDocument();
  });

  it('affiche le nombre de conducteurs', () => {
    render(<RouteCard route={mockRoute} onSelect={() => {}} />);
    expect(screen.getByText('12')).toBeInTheDocument();
  });

  it('affiche le prix moyen', () => {
    render(<RouteCard route={mockRoute} onSelect={() => {}} />);
    expect(screen.getByText(/~8\$/)).toBeInTheDocument();
  });

  it('appelle onSelect avec la route au clic', () => {
    const onSelect = vi.fn();
    render(<RouteCard route={mockRoute} onSelect={onSelect} />);
    fireEvent.click(screen.getByRole('button'));
    expect(onSelect).toHaveBeenCalledWith(mockRoute);
  });

  it('les jours actifs sont marqués actif', () => {
    render(<RouteCard route={mockRoute} onSelect={() => {}} />);
    const lun = screen.getByText('Lun');
    expect(lun.className).toContain('actif');
  });

  it('les jours inactifs sont marqués inactif', () => {
    const routeSansWeekend = { ...mockRoute, jours: ['Lun', 'Mer', 'Ven'] };
    render(<RouteCard route={routeSansWeekend} onSelect={() => {}} />);
    const mar = screen.getByText('Mar');
    expect(mar.className).toContain('inactif');
  });
});
