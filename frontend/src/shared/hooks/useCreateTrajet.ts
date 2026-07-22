import { useState, useCallback } from 'react';
import { createTrajet, ApiError } from '@/shared/services/api';
import type { TrajetCreateRequest } from '@/shared/types/trajet';

interface UseCreateTrajetReturn {
  loading: boolean;
  error: string | null;
  success: boolean;
  submitTrajet: (data: TrajetCreateRequest, token: string) => Promise<boolean>;
  reset: () => void;
}

/**
 * Hook gérant la création d'un trajet via POST /trajets.
 * Retourne loading, error, success et la fonction submitTrajet.
 */
export function useCreateTrajet(): UseCreateTrajetReturn {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const submitTrajet = useCallback(
    async (data: TrajetCreateRequest, token: string): Promise<boolean> => {
      setLoading(true);
      setError(null);
      setSuccess(false);

      try {
        await createTrajet(data, token);
        setSuccess(true);
        return true;
      } catch (err) {
        const message =
          err instanceof ApiError
            ? err.message
            : 'Impossible de créer le trajet. Vérifiez votre connexion.';
        setError(message);
        return false;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const reset = useCallback(() => {
    setError(null);
    setSuccess(false);
  }, []);

  return { loading, error, success, submitTrajet, reset };
}
