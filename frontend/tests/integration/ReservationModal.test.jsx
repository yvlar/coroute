import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ReservationModal } from '@/features/trajets/components/ReservationModal';

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

const defaultProps = {
  trajet: mockTrajet,
  token: 'jwt-token',
  onClose: vi.fn(),
  onConfirm: vi.fn(),
};

describe('ReservationModal', () => {
  beforeEach(() => vi.clearAllMocks());

  it('affiche les infos du trajet', () => {
    render(<ReservationModal {...defaultProps} />);
    expect(screen.getByText(/Granby/)).toBeInTheDocument();
    expect(screen.getByText(/Drummondville/)).toBeInTheDocument();
    expect(screen.getByText(/07h00/)).toBeInTheDocument();
  });

  it('affiche le prix par place', () => {
    render(<ReservationModal {...defaultProps} />);
    expect(screen.getByText(/8\s*\$/)).toBeInTheDocument();
  });

  it('calcule le total correctement pour 1 place', () => {
    render(<ReservationModal {...defaultProps} />);
    expect(screen.getByText(/8\.00\s*\$/)).toBeInTheDocument();
  });

  it('recalcule le total quand on change le nombre de places', () => {
    render(<ReservationModal {...defaultProps} />);
    const input = screen.getByLabelText(/Nombre de places/i);
    fireEvent.change(input, { target: { value: '2' } });
    expect(screen.getByText(/16\.00\s*\$/)).toBeInTheDocument();
  });

  it('ne dépasse pas le max de places disponibles', () => {
    render(<ReservationModal {...defaultProps} />);
    const input = screen.getByLabelText(/Nombre de places/i);
    fireEvent.change(input, { target: { value: '99' } });
    expect(input).toHaveValue(3); // max = placesRestantes
  });

  it('appelle onConfirm avec le trajet et le nombre de places', async () => {
    defaultProps.onConfirm.mockResolvedValueOnce(undefined);
    render(<ReservationModal {...defaultProps} />);
    fireEvent.click(screen.getByText(/Réserver 1 place/i));
    await waitFor(() =>
      expect(defaultProps.onConfirm).toHaveBeenCalledWith(mockTrajet, 1)
    );
  });

  it('appelle onConfirm avec 2 places si sélectionné', async () => {
    defaultProps.onConfirm.mockResolvedValueOnce(undefined);
    render(<ReservationModal {...defaultProps} />);
    const input = screen.getByLabelText(/Nombre de places/i);
    fireEvent.change(input, { target: { value: '2' } });
    fireEvent.click(screen.getByText(/Réserver 2 places/i));
    await waitFor(() =>
      expect(defaultProps.onConfirm).toHaveBeenCalledWith(mockTrajet, 2)
    );
  });

  it('appelle onClose au clic sur ✕', () => {
    render(<ReservationModal {...defaultProps} />);
    fireEvent.click(screen.getByLabelText('Fermer'));
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('appelle onClose au clic sur l\'overlay', () => {
    render(<ReservationModal {...defaultProps} />);
    const overlay = document.querySelector('.modal-overlay');
    fireEvent.click(overlay);
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('désactive le bouton pendant le chargement', async () => {
    let resolve;
    defaultProps.onConfirm.mockReturnValueOnce(new Promise((r) => { resolve = r; }));
    render(<ReservationModal {...defaultProps} />);
    fireEvent.click(screen.getByText(/Réserver/i));
    expect(screen.getByText('Réservation en cours...')).toBeDisabled();
    resolve();
  });
});