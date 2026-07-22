import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Navbar } from '@/shared/components/Navbar/Navbar';

const baseProps = {
  user: null,
  onLogin: vi.fn(),
  onRegister: vi.fn(),
  onLogout: vi.fn(),
  onHome: vi.fn(),
  onCreateTrajet: vi.fn(),
};

describe('Navbar', () => {
  it('affiche les boutons Connexion et S\'inscrire quand non connecté', () => {
    render(<Navbar {...baseProps} />);
    expect(screen.getByText('Connexion')).toBeInTheDocument();
    expect(screen.getByText(/S'inscrire/i)).toBeInTheDocument();
  });

  it('n\'affiche pas Déconnexion quand non connecté', () => {
    render(<Navbar {...baseProps} />);
    expect(screen.queryByText('Déconnexion')).not.toBeInTheDocument();
  });

  it('affiche le prénom et les initiales quand connecté', () => {
    const user = { prenom: 'Marie', initiales: 'MA' };
    render(<Navbar {...baseProps} user={user} />);
    expect(screen.getByText('Marie')).toBeInTheDocument();
    expect(screen.getByText('MA')).toBeInTheDocument();
  });

  it('affiche Déconnexion quand connecté', () => {
    const user = { prenom: 'Marie', initiales: 'MA' };
    render(<Navbar {...baseProps} user={user} />);
    expect(screen.getByText('Déconnexion')).toBeInTheDocument();
  });

  it('appelle onLogin au clic sur Connexion', () => {
    const onLogin = vi.fn();
    render(<Navbar {...baseProps} onLogin={onLogin} />);
    fireEvent.click(screen.getByText('Connexion'));
    expect(onLogin).toHaveBeenCalled();
  });

  it('appelle onRegister au clic sur S\'inscrire', () => {
    const onRegister = vi.fn();
    render(<Navbar {...baseProps} onRegister={onRegister} />);
    fireEvent.click(screen.getByText(/S'inscrire/i));
    expect(onRegister).toHaveBeenCalled();
  });

  it('appelle onHome au clic sur le logo', () => {
    const onHome = vi.fn();
    render(<Navbar {...baseProps} onHome={onHome} />);
    fireEvent.click(screen.getByLabelText("Retour à l'accueil"));
    expect(onHome).toHaveBeenCalled();
  });

  it('appelle onLogout au clic sur Déconnexion', () => {
    const onLogout = vi.fn();
    const user = { prenom: 'Marie', initiales: 'MA' };
    render(<Navbar {...baseProps} user={user} onLogout={onLogout} />);
    fireEvent.click(screen.getByText('Déconnexion'));
    expect(onLogout).toHaveBeenCalled();
  });

  it('appelle onCreateTrajet au clic sur Proposer un trajet (connecté)', () => {
    const onCreateTrajet = vi.fn();
    const user = { prenom: 'Marie', initiales: 'MA' };
    render(<Navbar {...baseProps} user={user} onCreateTrajet={onCreateTrajet} />);
    fireEvent.click(screen.getByLabelText('Proposer un trajet'));
    expect(onCreateTrajet).toHaveBeenCalled();
  });
});
