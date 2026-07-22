import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Hero } from '@/shared/components/Hero/Hero';

describe('Hero', () => {
  it('affiche les champs de recherche', () => {
    render(<Hero onSearch={() => {}} />);
    expect(screen.getByLabelText('Point de départ')).toBeInTheDocument();
    expect(screen.getByLabelText('Destination')).toBeInTheDocument();
  });

  it('affiche les statistiques', () => {
    render(<Hero onSearch={() => {}} />);
    expect(screen.getByText(/2 400\+/)).toBeInTheDocument();
    expect(screen.getByText(/148/)).toBeInTheDocument();
  });

  it('appelle onSearch avec les valeurs saisies au clic sur Trouver', () => {
    const onSearch = vi.fn();
    render(<Hero onSearch={onSearch} />);

    fireEvent.change(screen.getByLabelText('Point de départ'), {
      target: { value: 'Granby' },
    });
    fireEvent.change(screen.getByLabelText('Destination'), {
      target: { value: 'Drummondville' },
    });
    fireEvent.click(screen.getByText(/Trouver/i));

    expect(onSearch).toHaveBeenCalledWith(
      expect.objectContaining({ depart: 'Granby', destination: 'Drummondville' })
    );
  });

  it('appelle onSearch à la touche Enter sur le champ départ', () => {
    const onSearch = vi.fn();
    render(<Hero onSearch={onSearch} />);
    fireEvent.keyDown(screen.getByLabelText('Point de départ'), { key: 'Enter' });
    expect(onSearch).toHaveBeenCalled();
  });

  it('appelle onSearch à la touche Enter sur le champ destination', () => {
    const onSearch = vi.fn();
    render(<Hero onSearch={onSearch} />);
    fireEvent.keyDown(screen.getByLabelText('Destination'), { key: 'Enter' });
    expect(onSearch).toHaveBeenCalled();
  });

  it('n\'appelle pas onSearch pour une autre touche', () => {
    const onSearch = vi.fn();
    render(<Hero onSearch={onSearch} />);
    fireEvent.keyDown(screen.getByLabelText('Point de départ'), { key: 'Tab' });
    expect(onSearch).not.toHaveBeenCalled();
  });

  it('inclut les jours sélectionnés dans onSearch', () => {
    const onSearch = vi.fn();
    render(<Hero onSearch={onSearch} />);
    fireEvent.click(screen.getByText(/Trouver/i));
    expect(onSearch).toHaveBeenCalledWith(
      expect.objectContaining({ jours: expect.any(Array) })
    );
  });
});
