import { useState, useCallback } from 'react';
import { connecter, inscrire, ApiError } from '@/shared/services/api';
import type { AuthUser } from '@/shared/types/trajet';

/**
 * Hook gérant l'authentification : inscription, connexion, déconnexion.
 *
 * Le token JWT est conservé en mémoire uniquement (jamais dans localStorage).
 * Conséquence assumée dans cette version : un rafraîchissement de la page
 * déconnecte l'utilisateur (aucun refresh token n'est implémenté pour l'instant).
 *
 * Les informations utilisateur affichées proviennent directement du backend
 * (aucune donnée n'est déduite du courriel).
 */

interface Credentials {
  email: string;
  motDePasse: string;
}

interface RegisterData {
  nom: string;
  email: string;
  motDePasse: string;
}

interface UseAuthReturn {
  user: AuthUser | null;
  token: string | null;
  login: (credentials: Credentials) => Promise<AuthUser>;
  register: (data: RegisterData) => Promise<AuthUser>;
  logout: () => void;
}

/** Dérive des initiales à partir du nom réel de l'utilisateur. */
function initialesDepuisNom(nom: string): string {
  const parts = nom.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) {
    return '?';
  }
  if (parts.length === 1) {
    return parts[0].substring(0, 2).toUpperCase();
  }
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

export function useAuth(): UseAuthReturn {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);

  const login = useCallback(async ({ email, motDePasse }: Credentials): Promise<AuthUser> => {
    const data = await connecter({ email, motDePasse });
    const authUser: AuthUser = {
      id: data.utilisateur.id,
      nom: data.utilisateur.nom,
      email: data.utilisateur.email,
      initiales: initialesDepuisNom(data.utilisateur.nom),
    };
    setToken(data.token);
    setUser(authUser);
    return authUser;
  }, []);

  const register = useCallback(
    async ({ nom, email, motDePasse }: RegisterData): Promise<AuthUser> => {
      await inscrire({ nom, email, motDePasse });
      // Connexion automatique après inscription.
      return login({ email, motDePasse });
    },
    [login]
  );

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
  }, []);

  return { user, token, login, register, logout };
}

export { ApiError };
