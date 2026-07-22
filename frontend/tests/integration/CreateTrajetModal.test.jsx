import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { CreateTrajetModal } from '@/features/trajets/components/CreateTrajetModal';

vi.mock('@/shared/hooks/useCreateTrajet', () => ({
  useCreateTrajet: () => ({
    loading: false,
    error: null,
    success: false,
    submitTrajet: vi.fn().mockResolvedValue(true),
    reset: vi.fn(),
  }),
}));

const defaultProps = {
  token: 'jwt-token',
  onClose: vi.fn(),
  onSuccess: vi.fn(),
};

describe('CreateTrajetModal', () => {
  beforeEach(() => vi.clearAllMocks());

  it('affiche le titre', () => {
    render(<CreateTrajetModal {...defaultProps} />);
    expect(screen.getByText('Proposer un trajet')).toBeInTheDocument();
  });

  it('affiche les champs du formulaire', () => {
    render(<CreateTrajetModal {...defaultProps} />);
    expect(screen.getByLabelText('Ville de départ')).toBeInTheDocument();
    expect(screen.getByLabelText('Destination')).toBeInTheDocument();
    expect(screen.getByLabelText('Heure de départ')).toBeInTheDocument();
  });

  it('affiche les sélecteurs de type RÉGULIER et PONCTUEL', () => {
    render(<CreateTrajetModal {...defaultProps} />);
    expect(screen.getByText(/Régulier/i)).toBeInTheDocument();
    expect(screen.getByText(/Ponctuel/i)).toBeInTheDocument();
  });

  it('affiche les champs jours/dates par défaut (RÉGULIER)', () => {
    render(<CreateTrajetModal {...defaultProps} />);
    expect(screen.getByText('Jours de récurrence')).toBeInTheDocument();
    expect(screen.getByLabelText('Date de début')).toBeInTheDocument();
    expect(screen.getByLabelText('Date de fin')).toBeInTheDocument();
  });

  it('affiche le champ date ponctuelle si on sélectionne PONCTUEL', () => {
    render(<CreateTrajetModal {...defaultProps} />);
    fireEvent.click(screen.getByText(/Ponctuel/i));
    expect(screen.getByLabelText('Date du trajet')).toBeInTheDocument();
  });

  it('affiche les erreurs de validation si formulaire vide soumis', async () => {
    render(<CreateTrajetModal {...defaultProps} />);
    fireEvent.click(screen.getByText('Publier le trajet →'));
    await waitFor(() => {
      expect(screen.getByText('Ville de départ requise')).toBeInTheDocument();
    });
  });

  it('appelle onClose au clic sur ✕', () => {
    const onClose = vi.fn();
    render(<CreateTrajetModal {...defaultProps} onClose={onClose} />);
    fireEvent.click(screen.getByLabelText('Fermer'));
    expect(onClose).toHaveBeenCalled();
  });

  it('appelle onClose au clic sur l\'overlay', () => {
    const onClose = vi.fn();
    render(<CreateTrajetModal {...defaultProps} onClose={onClose} />);
    const overlay = document.querySelector('.modal-overlay');
    fireEvent.click(overlay);
    expect(onClose).toHaveBeenCalled();
  });

  it('appelle onSuccess après une soumission valide', async () => {
    const onSuccess = vi.fn();
    render(<CreateTrajetModal {...defaultProps} onSuccess={onSuccess} />);

    fireEvent.change(screen.getByLabelText('Ville de départ'), {
      target: { value: 'Granby' },
    });
    fireEvent.change(screen.getByLabelText('Destination'), {
      target: { value: 'Drummondville' },
    });
    fireEvent.change(screen.getByLabelText('Date de début'), {
      target: { value: '2024-09-02' },
    });
    fireEvent.change(screen.getByLabelText('Date de fin'), {
      target: { value: '2025-06-20' },
    });

    fireEvent.click(screen.getByText('Publier le trajet →'));
    await waitFor(() => expect(onSuccess).toHaveBeenCalled());
  });
});
