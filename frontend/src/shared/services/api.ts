/**
 * Client API CoRoute (typé).
 *
 * Base URL configurée via VITE_API_URL (défaut : /api → proxy Vite/Nginx vers :8080).
 * Auth : JWT Bearer token, conservé en mémoire via useAuth.
 *
 * Endpoints backend :
 *   POST   /utilisateurs/inscription
 *   POST   /utilisateurs/connexion
 *   GET    /trajets?depart=&destination=&date=
 *   GET    /trajets/{id}
 *   POST   /trajets                              (auth)
 *   DELETE /trajets/{id}                         (auth)
 *   POST   /trajets/{id}/reservations            (auth)
 *   DELETE /trajets/{id}/reservations/{resId}    (auth)
 *   GET    /trajets/{id}/reservations            (auth)
 *   GET    /trajets/match?depart=&destination=&jours=LUNDI&jours=MARDI
 */

import type {
  ErrorResponse,
  JourSemaine,
  MatchingResponse,
  ReservationCreateRequest,
  ReservationResponse,
  TokenResponse,
  TrajetCreateRequest,
  TrajetResponse,
} from '@/shared/types/trajet';

const BASE: string = import.meta.env.VITE_API_URL ?? '/api';

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

/**
 * Wrapper fetch avec gestion centralisée des erreurs et du token JWT.
 *
 * Sémantique des corps de réponse :
 *   - 204 No Content        → toujours null
 *   - 201 Created           → parse le JSON s'il existe, sinon null (corps vide)
 *   - autres réponses 2xx   → parse le JSON normalement
 */
async function request<T>(
  path: string,
  options: RequestInit = {},
  token: string | null = null
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...((options.headers as Record<string, string> | undefined) ?? {}),
  };

  const res = await fetch(`${BASE}${path}`, { ...options, headers });

  if (!res.ok) {
    let message = `Erreur ${res.status}`;
    try {
      const body = (await res.json()) as Partial<ErrorResponse> & { error?: string };
      message = body.message ?? body.error ?? message;
    } catch {
      // réponse non-JSON (corps vide, etc.)
    }
    throw new ApiError(res.status, message);
  }

  if (res.status === 204) {
    return null as T;
  }

  if (res.status === 201) {
    const text = await res.text();
    return text ? (JSON.parse(text) as T) : (null as T);
  }

  return (await res.json()) as T;
}

// ─── Utilisateurs ───────────────────────────────────────────────────────────

/** Inscription d'un nouvel utilisateur (201 Created, sans corps → null). */
export const inscrire = (data: { nom: string; email: string; motDePasse: string }): Promise<null> =>
  request<null>('/utilisateurs/inscription', { method: 'POST', body: JSON.stringify(data) });

/** Connexion — retourne le JWT et l'utilisateur authentifié. */
export const connecter = (data: { email: string; motDePasse: string }): Promise<TokenResponse> =>
  request<TokenResponse>('/utilisateurs/connexion', {
    method: 'POST',
    body: JSON.stringify(data),
  });

// ─── Trajets ────────────────────────────────────────────────────────────────

/** Recherche de trajets par matching (endpoint principal de recherche UI). */
export const matchTrajets = ({
  depart = '',
  destination = '',
  jours = [],
}: {
  depart?: string;
  destination?: string;
  jours?: JourSemaine[];
}): Promise<MatchingResponse[]> => {
  const qs = new URLSearchParams();
  if (depart) {
    qs.set('depart', depart);
  }
  if (destination) {
    qs.set('destination', destination);
  }
  jours.forEach((j) => qs.append('jours', j)); // ?jours=LUNDI&jours=MARDI
  return request<MatchingResponse[]>(`/trajets/match?${qs.toString()}`);
};

/** Liste tous les trajets (sans matching). */
export const findAllTrajets = ({
  depart = '',
  destination = '',
  date = '',
}: {
  depart?: string;
  destination?: string;
  date?: string;
} = {}): Promise<TrajetResponse[]> => {
  const qs = new URLSearchParams();
  if (depart) {
    qs.set('depart', depart);
  }
  if (destination) {
    qs.set('destination', destination);
  }
  if (date) {
    qs.set('date', date);
  }
  return request<TrajetResponse[]>(`/trajets?${qs.toString()}`);
};

/** Détail d'un trajet. */
export const getTrajet = (trajetId: string): Promise<TrajetResponse> =>
  request<TrajetResponse>(`/trajets/${trajetId}`);

/** Crée un trajet (conducteur authentifié). 201 Created, corps vide → null. */
export const createTrajet = (data: TrajetCreateRequest, token: string): Promise<null> =>
  request<null>('/trajets', { method: 'POST', body: JSON.stringify(data) }, token);

/** Supprime un trajet (conducteur propriétaire seulement). */
export const deleteTrajet = (trajetId: string, token: string): Promise<null> =>
  request<null>(`/trajets/${trajetId}`, { method: 'DELETE' }, token);

// ─── Réservations ─────────────────────────────────────────────────────────────

/** Crée une réservation sur un trajet. */
export const createReservation = (
  trajetId: string,
  data: ReservationCreateRequest,
  token: string
): Promise<null> =>
  request<null>(
    `/trajets/${trajetId}/reservations`,
    { method: 'POST', body: JSON.stringify(data) },
    token
  );

/** Annule une réservation. */
export const cancelReservation = (
  trajetId: string,
  reservationId: string,
  token: string
): Promise<null> =>
  request<null>(`/trajets/${trajetId}/reservations/${reservationId}`, { method: 'DELETE' }, token);

/** Liste les réservations d'un trajet (conducteur seulement). */
export const getReservations = (trajetId: string, token: string): Promise<ReservationResponse[]> =>
  request<ReservationResponse[]>(`/trajets/${trajetId}/reservations`, {}, token);
