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

export interface TokenResponse {
  token: string;
}

// ─── Erreur API ───────────────────────────────────────────────────────────────

export interface ErrorResponse {
  message: string;
}

// ─── Utilisateur (état local frontend) ───────────────────────────────────────

export interface Utilisateur {
  email: string;
  prenom: string;
  initiales: string;
}
