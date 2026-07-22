import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useSearch } from '@/shared/hooks/useSearch';

// Mock du module API — on contrôle ce que matchTrajets retourne
vi.mock('@/shared/services/api', () => ({
  matchTrajets: vi.fn(),
  ApiError: class ApiError extends Error {
    constructor(status, message) {
      super(message);
      this.status = status;
    }
  },
}));

import { matchTrajets } from '@/shared/services/api';

const MOCK_RESULTS = [
  {
    id: 'abc-1',
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
  },
];

describe('useSearch', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('searchResults est null à l\'initialisation', () => {
    const { result } = renderHook(() => useSearch());
    expect(result.current.searchResults).toBeNull();
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('handleSearch appelle matchTrajets avec les enums corrects', async () => {
    matchTrajets.mockResolvedValueOnce(MOCK_RESULTS);
    const { result } = renderHook(() => useSearch());

    await act(async () => {
      result.current.handleSearch({ depart: 'Granby', destination: '', jours: ['Lun', 'Mer'] });
    });

    expect(matchTrajets).toHaveBeenCalledWith({
      depart: 'Granby',
      destination: '',
      jours: ['LUNDI', 'MERCREDI'],
    });
    expect(result.current.searchResults).toEqual(MOCK_RESULTS);
    expect(result.current.loading).toBe(false);
  });

  it('handleSearch met loading à true pendant la requête', async () => {
    let resolve;
    matchTrajets.mockReturnValueOnce(new Promise((r) => { resolve = r; }));
    const { result } = renderHook(() => useSearch());

    act(() => {
      result.current.handleSearch({ depart: 'Granby', destination: '', jours: [] });
    });
    expect(result.current.loading).toBe(true);

    await act(async () => { resolve(MOCK_RESULTS); });
    expect(result.current.loading).toBe(false);
  });

  it('handleSearch gère les erreurs API et met error à jour', async () => {
    const { ApiError } = await import('@/shared/services/api');
    matchTrajets.mockRejectedValueOnce(new ApiError(500, 'Erreur serveur'));

    const { result } = renderHook(() => useSearch());

    await act(async () => {
      result.current.handleSearch({ depart: '', destination: '', jours: [] });
    });

    expect(result.current.error).toBe('Erreur serveur');
    expect(result.current.searchResults).toEqual([]);
  });

  it('resetSearch remet l\'état initial', async () => {
    matchTrajets.mockResolvedValueOnce(MOCK_RESULTS);
    const { result } = renderHook(() => useSearch());

    await act(async () => {
      result.current.handleSearch({ depart: 'Granby', destination: '', jours: [] });
    });
    act(() => { result.current.resetSearch(); });

    expect(result.current.searchResults).toBeNull();
    expect(result.current.error).toBeNull();
    expect(result.current.searchQuery).toEqual({});
  });

  it('handleSelectRoute appelle matchTrajets avec depart+destination de la route', async () => {
    matchTrajets.mockResolvedValueOnce(MOCK_RESULTS);
    const { result } = renderHook(() => useSearch());

    const route = {
      depart: 'Granby',
      destination: 'Drummondville',
      jours: ['Lun', 'Mer', 'Ven'],
    };

    await act(async () => {
      result.current.handleSelectRoute(route);
    });

    expect(matchTrajets).toHaveBeenCalledWith({
      depart: 'Granby',
      destination: 'Drummondville',
      jours: ['LUNDI', 'MERCREDI', 'VENDREDI'],
    });
  });
});
