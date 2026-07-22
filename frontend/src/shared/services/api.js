/**
 * Client API CoRoute.
 *
 * Base URL configurée via VITE_API_URL (défaut : /api → proxy Vite/Nginx vers :8080).
 * Auth : JWT Bearer token, stocké en mémoire via useAuth.
 *
 * Endpoints backend (base : http://localhost:8080/) :
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

const BASE = import.meta.env.VITE_API_URL ?? '/api';

/**
 * Wrapper fetch avec gestion centralisée des erreurs et du token JWT.
 * @param {string} path
 * @param {RequestInit} options
 * @param {string|null} token
 */
async function request(path, options = {}, token = null) {
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const res = await fetch(`${BASE}${path}`, { ...options, headers });

  if (!res.ok) {
    // Tenter de parser le message d'erreur du backend (ErrorResponse)
    let message = `Erreur ${res.status}`;
    try {
      const body = await res.json();
      message = body.message ?? body.error ?? message;
    } catch {
      // réponse non-JSON (ex. 204 No Content)
    }
    throw new ApiError(res.status, message);
  }

  // 204 No Content → pas de body JSON
  if (res.status === 204 || res.status === 201) {
    return null;
  }

  return res.json();
}

export class ApiError extends Error {
  /** @param {number} status @param {string} message */
  constructor(status, message) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

// ─── Utilisateurs ───────────────────────────────────────────────────────────

/**
 * Inscription d'un nouvel utilisateur.
 * @param {{ nom: string, email: string, motDePasse: string }} data
 * @returns {Promise<null>} 201 Created — pas de body
 */
export const inscrire = (data) =>
  request('/utilisateurs/inscription', { method: 'POST', body: JSON.stringify(data) });

/**
 * Connexion — retourne le JWT.
 * @param {{ email: string, motDePasse: string }} data
 * @returns {Promise<{ token: string }>}
 */
export const connecter = (data) =>
  request('/utilisateurs/connexion', { method: 'POST', body: JSON.stringify(data) });

// ─── Trajets ────────────────────────────────────────────────────────────────

/**
 * Recherche de trajets par matching (endpoint principal pour la recherche UI).
 * Les jours doivent être des valeurs enum : LUNDI, MARDI, etc.
 * @param {{ depart?: string, destination?: string, jours?: string[] }} params
 * @returns {Promise<MatchingResponse[]>}
 */
export const matchTrajets = ({ depart = '', destination = '', jours = [] }) => {
  const qs = new URLSearchParams();
  if (depart) {
    qs.set('depart', depart);
  }
  if (destination) {
    qs.set('destination', destination);
  }
  jours.forEach((j) => qs.append('jours', j)); // ?jours=LUNDI&jours=MARDI
  return request(`/trajets/match?${qs.toString()}`);
};

/**
 * Liste tous les trajets (sans matching).
 * @param {{ depart?: string, destination?: string, date?: string }} params
 * @returns {Promise<TrajetResponse[]>}
 */
export const findAllTrajets = ({ depart = '', destination = '', date = '' } = {}) => {
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
  return request(`/trajets?${qs.toString()}`);
};

/**
 * Détail d'un trajet.
 * @param {string} trajetId
 * @returns {Promise<TrajetResponse>}
 */
export const getTrajet = (trajetId) => request(`/trajets/${trajetId}`);

/**
 * Crée un trajet (conducteur authentifié).
 * @param {TrajetCreateRequest} data
 * @param {string} token
 * @returns {Promise<null>} 201 Created — Location header contient l'URL
 */
export const createTrajet = (data, token) =>
  request('/trajets', { method: 'POST', body: JSON.stringify(data) }, token);

/**
 * Supprime un trajet (conducteur propriétaire seulement).
 * @param {string} trajetId
 * @param {string} token
 * @returns {Promise<null>}
 */
export const deleteTrajet = (trajetId, token) =>
  request(`/trajets/${trajetId}`, { method: 'DELETE' }, token);

// ─── Réservations ────────────────────────────────────────────────────────────

/**
 * Crée une réservation sur un trajet.
 * @param {string} trajetId
 * @param {{ nombrePlaces: number }} data
 * @param {string} token
 * @returns {Promise<null>}
 */
export const createReservation = (trajetId, data, token) =>
  request(
    `/trajets/${trajetId}/reservations`,
    { method: 'POST', body: JSON.stringify(data) },
    token
  );

/**
 * Annule une réservation.
 * @param {string} trajetId
 * @param {string} reservationId
 * @param {string} token
 * @returns {Promise<null>}
 */
export const cancelReservation = (trajetId, reservationId, token) =>
  request(`/trajets/${trajetId}/reservations/${reservationId}`, { method: 'DELETE' }, token);

/**
 * Liste les réservations d'un trajet (conducteur seulement).
 * @param {string} trajetId
 * @param {string} token
 * @returns {Promise<ReservationResponse[]>}
 */
export const getReservations = (trajetId, token) =>
  request(`/trajets/${trajetId}/reservations`, {}, token);

/**
 * @typedef {{ id: string, depart: string, destination: string, date: string,
 *   heure: string, placesRestantes: number, prixParPassager: number,
 *   conducteurId: string, type: 'PONCTUEL'|'REGULIER',
 *   joursRecurrence: string[], dateDebut: string, dateFin: string }} TrajetResponse
 *
 * @typedef {{ id: string, depart: string, destination: string, heure: string,
 *   prixParPassager: number, placesRestantes: number, type: string,
 *   joursCompatibles: string[], scoreCompatibilite: number,
 *   conducteurId: string, date: string, dateDebut: string, dateFin: string }} MatchingResponse
 *
 * @typedef {{ id: string, passagerId: string, nombrePlaces: number }} ReservationResponse
 */
