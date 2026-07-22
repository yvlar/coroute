// ─── Enums (miroir exact des enums Java du backend) ──────────────────────────

export type JourSemaine =
  | 'LUNDI'
  | 'MARDI'
  | 'MERCREDI'
  | 'JEUDI'
  | 'VENDREDI'
  | 'SAMEDI'
  | 'DIMANCHE';

export type TrajetType = 'PONCTUEL' | 'REGULIER';

// ─── Requests (miroir de TrajetCreateRequest.java) ────────────────────────────

export interface TrajetCreateRequest {
  depart: string;
  destination: string;
  heure: string; // "HH:mm:ss" ex: "07:00:00"
  placesDisponibles: number; // Min 1
  prixParPassager: number; // Positive
  type: TrajetType;
  // PONCTUEL
  date?: string; // "YYYY-MM-DD"
  // REGULIER
  joursRecurrence?: JourSemaine[];
  dateDebut?: string; // "YYYY-MM-DD"
  dateFin?: string; // "YYYY-MM-DD"
}

export interface ReservationCreateRequest {
  nombrePlaces: number; // Min 1
}

// ─── Responses (miroir de TrajetResponse.java / MatchingResponse.java) ────────

export interface TrajetResponse {
  id: string;
  depart: string;
  destination: string;
  date: string | null;
  heure: string;
  placesRestantes: number;
  prixParPassager: number;
  conducteurId: string;
  type: TrajetType;
  joursRecurrence: JourSemaine[];
  dateDebut: string | null;
  dateFin: string | null;
}

export interface MatchingResponse {
  id: string;
  depart: string;
  destination: string;
  heure: string;
  prixParPassager: number;
  placesRestantes: number;
  type: TrajetType;
  joursCompatibles: JourSemaine[];
  scoreCompatibilite: number;
  conducteurId: string;
  date: string | null;
  dateDebut: string | null;
  dateFin: string | null;
}

export interface ReservationResponse {
  id: string;
  passagerId: string;
  nombrePlaces: number;
}

// ─── Utilisateur renvoyé par le backend (miroir de UtilisateurResponse.java) ──

export interface UtilisateurResponse {
  id: string;
  nom: string;
  email: string;
}

export interface TokenResponse {
  token: string;
  utilisateur: UtilisateurResponse;
}

// ─── Erreur API ───────────────────────────────────────────────────────────────

export interface ErrorResponse {
  message: string;
}

// ─── Utilisateur authentifié (état local frontend) ────────────────────────────
// Construit à partir des données réelles du backend (aucune dérivation à partir
// du courriel). Les initiales sont dérivées du nom réel.

export interface AuthUser {
  id: string;
  nom: string;
  email: string;
  initiales: string;
}
