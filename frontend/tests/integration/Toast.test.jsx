import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Toast } from '@/shared/components/Toast/Toast';

describe('Toast', () => {
  it('affiche le message', () => {
    render(<Toast message="Connexion réussie" type="success" onClose={() => {}} />);
    expect(screen.getByText('Connexion réussie')).toBeInTheDocument();
  });

  it('a le rôle alert pour l\'accessibilité', () => {
    render(<Toast message="Erreur" type="error" onClose={() => {}} />);
    expect(screen.getByRole('alert')).toBeInTheDocument();
  });

  it('appelle onClose après 3500ms', async () => {
    vi.useFakeTimers();
    const onClose = vi.fn();
    render(<Toast message="Test" type="info" onClose={onClose} />);
    vi.advanceTimersByTime(3500);
    expect(onClose).toHaveBeenCalled();
    vi.useRealTimers();
  });
});
