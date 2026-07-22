import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useCreateTrajet } from '@/shared/hooks/useCreateTrajet';

vi.mock('@/shared/services/api', () => ({
  createTrajet: vi.fn(),
  ApiError: class ApiError extends Error {
    status: number;
    constructor(status: number, message: string) {
      super(message);
      this.status = status;
    }
  },
}));

import { createTrajet } from '@/shared/services/api';

const mockPayload = {
  depart: 'Granby',
  destination: 'Drummondville',
  heure: '07:00:00',
  placesDisponibles: 3,
  prixParPassager: 8,
  type: 'REGULIER' as const,
  joursRecurrence: ['LUNDI', 'MERCREDI'] as const,
  dateDebut: '2024-09-02',
  dateFin: '2025-06-20',
};

describe('useCreateTrajet', () => {
  beforeEach(() => vi.clearAllMocks());

  it('état initial : loading false, error null, success false', () => {
    const { result } = renderHook(() => useCreateTrajet());
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
    expect(result.current.success).toBe(false);
  });

  it('submitTrajet retourne true et met success à true en cas de succès', async () => {
    (createTrajet as ReturnType<typeof vi.fn>).mockResolvedValueOnce(null);
    const { result } = renderHook(() => useCreateTrajet());

    let ok: boolean;
    await act(async () => {
      ok = await result.current.submitTrajet(mockPayload, 'jwt-token');
    });

    expect(ok!).toBe(true);
    expect(result.current.success).toBe(true);
    expect(result.current.error).toBeNull();
    expect(result.current.loading).toBe(false);
  });

  it('submitTrajet retourne false et met error en cas d\'ApiError', async () => {
    const { ApiError } = await import('@/shared/services/api');
    (createTrajet as ReturnType<typeof vi.fn>).mockRejectedValueOnce(
      new ApiError(400, 'Données invalides')
    );
    const { result } = renderHook(() => useCreateTrajet());

    let ok: boolean;
    await act(async () => {
      ok = await result.current.submitTrajet(mockPayload, 'jwt-token');
    });

    expect(ok!).toBe(false);
    expect(result.current.error).toBe('Données invalides');
    expect(result.current.success).toBe(false);
  });

  it('submitTrajet retourne false avec message générique pour erreur réseau', async () => {
    (createTrajet as ReturnType<typeof vi.fn>).mockRejectedValueOnce(new Error('Network error'));
    const { result } = renderHook(() => useCreateTrajet());

    let ok: boolean;
    await act(async () => {
      ok = await result.current.submitTrajet(mockPayload, 'jwt-token');
    });

    expect(ok!).toBe(false);
    expect(result.current.error).toBe('Impossible de créer le trajet. Vérifiez votre connexion.');
  });

  it('reset remet error et success à leur valeur initiale', async () => {
    (createTrajet as ReturnType<typeof vi.fn>).mockResolvedValueOnce(null);
    const { result } = renderHook(() => useCreateTrajet());

    await act(async () => {
      await result.current.submitTrajet(mockPayload, 'jwt-token');
    });
    expect(result.current.success).toBe(true);

    act(() => {
      result.current.reset();
    });

    expect(result.current.success).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('loading passe à true pendant la requête', async () => {
    let resolve: (v: null) => void;
    (createTrajet as ReturnType<typeof vi.fn>).mockReturnValueOnce(
      new Promise((r) => {
        resolve = r;
      })
    );
    const { result } = renderHook(() => useCreateTrajet());

    act(() => {
      result.current.submitTrajet(mockPayload, 'jwt-token');
    });
    expect(result.current.loading).toBe(true);

    await act(async () => {
      resolve!(null);
    });
    expect(result.current.loading).toBe(false);
  });
});
