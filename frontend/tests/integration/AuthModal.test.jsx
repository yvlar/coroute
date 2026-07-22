import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AuthModal } from '@/features/auth/components/AuthModal';

// Mock de l'API pour ne pas faire de vraies requêtes
vi.mock('@/shared/services/api', () => ({
  connecter: vi.fn(),
  inscrire: vi.fn(),
  ApiError: class ApiError extends Error {
    constructor(status, message) {
      super(message);
      this.status = status;
    }
  },
}));

const defaultProps = {
  mode: 'login',
  onClose: vi.fn(),
  onLogin: vi.fn(),
  onRegister: vi.fn(),
};

describe('AuthModal — mode connexion', () => {
  beforeEach(() => vi.clearAllMocks());

  it('affiche "Bon retour !" en mode login', () => {
    render(<AuthModal {...defaultProps} />);
    expect(screen.getByText('Bon retour !')).toBeInTheDocument();
  });

  it('affiche les champs courriel et mot de passe', () => {
    render(<AuthModal {...defaultProps} />);
    expect(screen.getByLabelText(/Courriel/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Mot de passe/i)).toBeInTheDocument();
  });

  it('n\'affiche pas le champ Nom en mode login', () => {
    render(<AuthModal {...defaultProps} />);
    expect(screen.queryByLabelText(/Nom/i)).not.toBeInTheDocument();
  });

  it('appelle onClose au clic sur ✕', () => {
    render(<AuthModal {...defaultProps} />);
    fireEvent.click(screen.getByLabelText('Fermer'));
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('appelle onClose au clic sur l\'overlay', () => {
    render(<AuthModal {...defaultProps} />);
    const overlay = screen.getByRole('dialog').parentElement;
    fireEvent.click(overlay);
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('n\'appelle pas onLogin si formulaire vide', async () => {
    render(<AuthModal {...defaultProps} />);
    fireEvent.click(screen.getByText(/Se connecter →/i));
    await waitFor(() => expect(defaultProps.onLogin).not.toHaveBeenCalled());
  });

  it('affiche une erreur de validation si courriel vide', async () => {
    render(<AuthModal {...defaultProps} />);
    fireEvent.click(screen.getByText(/Se connecter →/i));
    await waitFor(() => expect(screen.getByText('Courriel requis')).toBeInTheDocument());
  });

  it('appelle onLogin avec email + motDePasse si formulaire valide', async () => {
    defaultProps.onLogin.mockResolvedValueOnce(undefined);
    render(<AuthModal {...defaultProps} />);

    fireEvent.change(screen.getByLabelText(/Courriel/i), {
      target: { value: 'test@exemple.ca' },
    });
    fireEvent.change(screen.getByLabelText(/Mot de passe/i), {
      target: { value: 'secret123' },
    });
    fireEvent.click(screen.getByText(/Se connecter →/i));

    await waitFor(() =>
      expect(defaultProps.onLogin).toHaveBeenCalledWith({
        email: 'test@exemple.ca',
        motDePasse: 'secret123',
      })
    );
  });

  it('affiche un message d\'erreur 401 retourné par le serveur', async () => {
    const { ApiError } = await import('@/shared/services/api');
    defaultProps.onLogin.mockRejectedValueOnce(new ApiError(401, 'Identifiants incorrects.'));

    render(<AuthModal {...defaultProps} />);
    fireEvent.change(screen.getByLabelText(/Courriel/i), { target: { value: 'a@b.ca' } });
    fireEvent.change(screen.getByLabelText(/Mot de passe/i), { target: { value: 'wrongpass' } });
    fireEvent.click(screen.getByText(/Se connecter →/i));

    await waitFor(() =>
      expect(screen.getByText('Identifiants incorrects.')).toBeInTheDocument()
    );
  });
});

describe('AuthModal — mode inscription', () => {
  beforeEach(() => vi.clearAllMocks());

  it('affiche "Créer un compte" en mode register', () => {
    render(<AuthModal {...defaultProps} mode="register" />);
    expect(screen.getByText('Créer un compte')).toBeInTheDocument();
  });

  it('affiche le champ Nom complet en mode inscription', () => {
    render(<AuthModal {...defaultProps} mode="register" />);
    expect(screen.getByLabelText(/Nom complet/i)).toBeInTheDocument();
  });

  it('appelle onRegister avec nom, email, motDePasse', async () => {
    defaultProps.onRegister.mockResolvedValueOnce(undefined);
    render(<AuthModal {...defaultProps} mode="register" />);

    fireEvent.change(screen.getByLabelText(/Nom complet/i), { target: { value: 'Marie T.' } });
    fireEvent.change(screen.getByLabelText(/Courriel/i), { target: { value: 'marie@ex.ca' } });
    fireEvent.change(screen.getByLabelText(/Mot de passe/i), { target: { value: 'motpasse123' } });
    fireEvent.click(screen.getByText(/Créer mon compte →/i));

    await waitFor(() =>
      expect(defaultProps.onRegister).toHaveBeenCalledWith({
        nom: 'Marie T.',
        email: 'marie@ex.ca',
        motDePasse: 'motpasse123',
      })
    );
  });

  it('affiche une erreur 409 "compte existe déjà"', async () => {
    const { ApiError } = await import('@/shared/services/api');
    defaultProps.onRegister.mockRejectedValueOnce(new ApiError(409, 'Conflit'));

    render(<AuthModal {...defaultProps} mode="register" />);
    fireEvent.change(screen.getByLabelText(/Nom complet/i), { target: { value: 'Test' } });
    fireEvent.change(screen.getByLabelText(/Courriel/i), { target: { value: 'a@b.ca' } });
    fireEvent.change(screen.getByLabelText(/Mot de passe/i), { target: { value: 'pass123' } });
    fireEvent.click(screen.getByText(/Créer mon compte →/i));

    await waitFor(() =>
      expect(screen.getByText('Un compte existe déjà avec cet email.')).toBeInTheDocument()
    );
  });
});
