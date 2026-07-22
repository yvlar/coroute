import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { connecter, inscrire, matchTrajets, createReservation, ApiError } from '@/shared/services/api';

// Mock fetch global
const mockFetch = vi.fn();
global.fetch = mockFetch;

function mockResponse(status, body) {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    json: () => Promise.resolve(body),
  });
}

function mockNoContent() {
  return Promise.resolve({ ok: true, status: 204, json: () => Promise.reject() });
}

describe('API — utilisateurs', () => {
  beforeEach(() => vi.clearAllMocks());

  it('connecter envoie POST /utilisateurs/connexion et retourne le token', async () => {
    mockFetch.mockReturnValueOnce(mockResponse(200, { token: 'jwt-xyz' }));

    const result = await connecter({ email: 'a@b.ca', motDePasse: 'pass' });

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('/utilisateurs/connexion'),
      expect.objectContaining({ method: 'POST' })
    );
    expect(result.token).toBe('jwt-xyz');
  });

  it('inscrire envoie POST /utilisateurs/inscription et retourne null pour 204', async () => {
    mockFetch.mockReturnValueOnce(mockNoContent());
    const result = await inscrire({ nom: 'Test', email: 'a@b.ca', motDePasse: 'pass123' });
    expect(result).toBeNull();
  });

  it('lance ApiError avec le status HTTP en cas d\'erreur', async () => {
    mockFetch.mockReturnValueOnce(
      mockResponse(401, { message: 'Identifiants incorrects.' })
    );

    await expect(connecter({ email: 'x@y.ca', motDePasse: 'wrong' })).rejects.toMatchObject({
      status: 401,
      message: 'Identifiants incorrects.',
    });
  });
});

describe('API — matching trajets', () => {
  beforeEach(() => vi.clearAllMocks());

  it('matchTrajets construit correctement les query params avec plusieurs jours', async () => {
    mockFetch.mockReturnValueOnce(mockResponse(200, []));

    await matchTrajets({ depart: 'Granby', destination: 'Drummondville', jours: ['LUNDI', 'MERCREDI'] });

    const url = mockFetch.mock.calls[0][0];
    expect(url).toContain('depart=Granby');
    expect(url).toContain('destination=Drummondville');
    expect(url).toContain('jours=LUNDI');
    expect(url).toContain('jours=MERCREDI');
  });

  it('matchTrajets sans jours n\'ajoute pas le param jours', async () => {
    mockFetch.mockReturnValueOnce(mockResponse(200, []));

    await matchTrajets({ depart: 'Granby', destination: '', jours: [] });

    const url = mockFetch.mock.calls[0][0];
    expect(url).not.toContain('jours=');
  });
});

describe('API — réservations', () => {
  beforeEach(() => vi.clearAllMocks());

  it('createReservation envoie le token JWT en header Authorization', async () => {
    mockFetch.mockReturnValueOnce(mockNoContent());

    await createReservation('trajet-id-1', { nombrePlaces: 1 }, 'mon-jwt-token');

    const headers = mockFetch.mock.calls[0][1].headers;
    expect(headers.Authorization).toBe('Bearer mon-jwt-token');
  });

  it('createReservation envoie POST sur /trajets/{id}/reservations', async () => {
    mockFetch.mockReturnValueOnce(mockNoContent());

    await createReservation('trajet-42', { nombrePlaces: 2 }, 'tok');

    const url = mockFetch.mock.calls[0][0];
    expect(url).toContain('/trajets/trajet-42/reservations');
  });
});
