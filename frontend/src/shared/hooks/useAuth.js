import { useState, useCallback } from 'react';
import { connecter, inscrire, ApiError } from '@/shared/services/api';

/**
 * Hook gérant l'authentification : inscription, connexion, déconnexion.
 * Le token JWT est conservé en mémoire (pas de localStorage).
 */
export function useAuth() {
  const [user, setUser] = useState(null); // { email, prenom, initiales }
  const [token, setToken] = useState(null); // JWT string

  const login = useCallback(async ({ email, motDePasse }) => {
    const data = await connecter({ email, motDePasse });
    const prenom = email.split('@')[0];
    const initiales = prenom.substring(0, 2).toUpperCase();
    setToken(data.token);
    setUser({ email, prenom, initiales });
    return { prenom };
  }, []);

  const register = useCallback(
    async ({ nom, email, motDePasse }) => {
      await inscrire({ nom, email, motDePasse });
      // Connexion automatique après inscription
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
