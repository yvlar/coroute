import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { RoutesFixesSection } from '@/features/routes/components/RoutesFixesSection';

describe('RoutesFixesSection', () => {
  it('affiche le titre de la section', () => {
    render(<RoutesFixesSection onSelectRoute={() => {}} />);
    expect(screen.getByText('Lignes fixes disponibles')).toBeInTheDocument();
  });

  it('affiche toutes les routes mock', () => {
    render(<RoutesFixesSection onSelectRoute={() => {}} />);
    // Les 6 routes mock ont des villes de départ uniques ou des numéros de ligne
    expect(screen.getByText(/LIGNE 01/)).toBeInTheDocument();
    expect(screen.getByText(/LIGNE 06/)).toBeInTheDocument();
  });

  it('appelle onSelectRoute avec la route au clic sur une carte', () => {
    const onSelectRoute = vi.fn();
    render(<RoutesFixesSection onSelectRoute={onSelectRoute} />);
    const buttons = screen.getAllByRole('button');
    fireEvent.click(buttons[0]);
    expect(onSelectRoute).toHaveBeenCalledWith(
      expect.objectContaining({ id: 'L1', depart: 'Granby' })
    );
  });
});
