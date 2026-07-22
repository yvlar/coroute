import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useAuth } from '@/shared/hooks/useAuth';

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

import { connecter, inscrire } from '@/shared/services/api';

describe('useAuth', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('user et token sont null à l\'initialisation', () => {
    const { result } = renderHook(() => useAuth());
    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
  });

  it('login appelle connecter et stocke l\'utilisateur réel du backend', async () => {
    connecter.mockResolvedValueOnce({
      token: 'jwt-abc-123',
      utilisateur: { id: 'u-1', nom: 'Test Utilisateur', email: 'test@exemple.ca' },
    });

    const { result } = renderHook(() => useAuth());
    await act(async () => {
      await result.current.login({ email: 'test@exemple.ca', motDePasse: 'secret123' });
    });

    expect(connecter).toHaveBeenCalledWith({
      email: 'test@exemple.ca',
      motDePasse: 'secret123',
    });
    expect(result.current.token).toBe('jwt-abc-123');
    expect(result.current.user).toMatchObject({
      id: 'u-1',
      nom: 'Test Utilisateur',
      email: 'test@exemple.ca',
    });
    // Le prénom n'est plus dérivé du courriel.
    expect(result.current.user.prenom).toBeUndefined();
  });

  it('login dérive les initiales depuis le nom réel', async () => {
    connecter.mockResolvedValueOnce({
      token: 'tok',
      utilisateur: { id: 'u-2', nom: 'Marie Curie', email: 'marie@exemple.ca' },
    });
    const { result } = renderHook(() => useAuth());

    await act(async () => {
      await result.current.login({ email: 'marie@exemple.ca', motDePasse: 'pass123' });
    });

    expect(result.current.user.initiales).toBe('MC');
  });

  it('register appelle inscrire puis connecter', async () => {
    inscrire.mockResolvedValueOnce(null);
    connecter.mockResolvedValueOnce({
      token: 'jwt-xyz',
      utilisateur: { id: 'u-3', nom: 'Marie Tremblay', email: 'marie@exemple.ca' },
    });

    const { result } = renderHook(() => useAuth());
    await act(async () => {
      await result.current.register({
        nom: 'Tremblay',
        email: 'marie@exemple.ca',
        motDePasse: 'pass123',
      });
    });

    expect(inscrire).toHaveBeenCalledWith({
      nom: 'Tremblay',
      email: 'marie@exemple.ca',
      motDePasse: 'pass123',
    });
    expect(connecter).toHaveBeenCalled();
    expect(result.current.token).toBe('jwt-xyz');
  });

  it('logout remet user et token à null', async () => {
    connecter.mockResolvedValueOnce({
      token: 'tok',
      utilisateur: { id: 'u-4', nom: 'Alex Roy', email: 'a@b.ca' },
    });
    const { result } = renderHook(() => useAuth());

    await act(async () => {
      await result.current.login({ email: 'a@b.ca', motDePasse: 'pass123' });
    });
    act(() => { result.current.logout(); });

    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
  });

  it('login propage l\'erreur si connecter échoue', async () => {
    const { ApiError } = await import('@/shared/services/api');
    connecter.mockRejectedValueOnce(new ApiError(401, 'Identifiants incorrects.'));

    const { result } = renderHook(() => useAuth());

    await expect(
      act(async () => {
        await result.current.login({ email: 'x@y.ca', motDePasse: 'wrong' });
      })
    ).rejects.toThrow('Identifiants incorrects.');
  });
});
