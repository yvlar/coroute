import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '@/App';

vi.mock('@/shared/services/api', () => ({
  matchTrajets: vi.fn().mockResolvedValue([]),
  connecter: vi.fn(),
  inscrire: vi.fn(),
  createReservation: vi.fn(),
  ApiError: class ApiError extends Error {
    constructor(status, message) {
      super(message);
      this.status = status;
    }
  },
}));

import { matchTrajets, connecter, inscrire } from '@/shared/services/api';

describe('App', () => {
  beforeEach(() => vi.clearAllMocks());

  it('affiche la navbar et le hero au chargement', () => {
    render(<App />);
    expect(screen.getByRole('navigation')).toBeInTheDocument();
    expect(screen.getByRole('search')).toBeInTheDocument();
  });

  it('affiche les routes fixes par défaut', () => {
    render(<App />);
    expect(screen.getByText('Lignes fixes disponibles')).toBeInTheDocument();
  });

  it('affiche les résultats après une recherche', async () => {
    matchTrajets.mockResolvedValueOnce([
      {
        id: 'abc-1',
        depart: 'Granby',
        destination: 'Drummondville',
        heure: '07:00:00',
        prixParPassager: 8,
        placesRestantes: 3,
        type: 'REGULIER',
        joursCompatibles: ['LUNDI'],
        scoreCompatibilite: 90,
        conducteurId: 'user-1',
        date: null,
        dateDebut: '2024-09-02',
        dateFin: '2025-06-20',
      },
    ]);

    render(<App />);
    fireEvent.click(screen.getByText(/Trouver/i));

    await waitFor(() => {
      expect(screen.queryByText('Lignes fixes disponibles')).not.toBeInTheDocument();
    });
    expect(screen.getByText('07h00')).toBeInTheDocument();
  });

  it('affiche l\'état vide si la recherche ne retourne rien', async () => {
    matchTrajets.mockResolvedValueOnce([]);
    render(<App />);
    fireEvent.click(screen.getByText(/Trouver/i));
    await waitFor(() => {
      expect(screen.getByText('Aucun trajet trouvé')).toBeInTheDocument();
    });
  });

  it('ouvre le modal de connexion au clic sur Connexion', () => {
    render(<App />);
    fireEvent.click(screen.getByText('Connexion'));
    expect(screen.getByRole('dialog')).toBeInTheDocument();
  });

  it('ferme le modal de connexion au clic sur ✕', () => {
    render(<App />);
    fireEvent.click(screen.getByText('Connexion'));
    fireEvent.click(screen.getByLabelText('Fermer'));
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });

  it('affiche un toast et connecte l\'utilisateur après login réussi', async () => {
    connecter.mockResolvedValueOnce({ token: 'jwt-abc' });
    render(<App />);
    fireEvent.click(screen.getByText('Connexion'));

    fireEvent.change(screen.getByLabelText('Courriel'), {
      target: { value: 'marie@exemple.ca' },
    });
    fireEvent.change(screen.getByLabelText('Mot de passe'), {
      target: { value: 'secret123' },
    });
    fireEvent.click(screen.getByText(/Se connecter →/i));

    await waitFor(() => {
      expect(screen.getByText(/Bienvenue, marie/i)).toBeInTheDocument();
    });
  });

  it('affiche un toast d\'erreur si Réserver sans être connecté', async () => {
    matchTrajets.mockResolvedValueOnce([
      {
        id: 'abc-1',
        depart: 'Granby',
        destination: 'Drummondville',
        heure: '07:00:00',
        prixParPassager: 8,
        placesRestantes: 3,
        type: 'REGULIER',
        joursCompatibles: ['LUNDI'],
        scoreCompatibilite: 90,
        conducteurId: 'user-1',
        date: null,
        dateDebut: '2024-09-02',
        dateFin: '2025-06-20',
      },
    ]);
    render(<App />);
    fireEvent.click(screen.getByText(/Trouver/i));

    await waitFor(() => {
      expect(screen.getByText('07h00')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole('button', { name: /Réserver/i }));
    expect(screen.getByText(/Connectez-vous pour réserver/i)).toBeInTheDocument();
  });

  it('resetSearch revient à la vue des routes fixes', async () => {
    matchTrajets.mockResolvedValueOnce([]);
    render(<App />);
    fireEvent.click(screen.getByText(/Trouver/i));
    await waitFor(() => {
      expect(screen.getByText('Aucun trajet trouvé')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByLabelText("Retour à l'accueil"));
    expect(screen.getByText('Lignes fixes disponibles')).toBeInTheDocument();
  });
});
