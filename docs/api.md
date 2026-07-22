# API CoRoute

Base URL : `http://localhost:8080/` (accès direct) ou `/api` via le proxy Nginx/Vite.

Authentification : les routes protégées exigent un en-tête
`Authorization: Bearer <JWT>` obtenu à la connexion. L'identifiant utilisateur est
extrait du token par le serveur (en-tête interne `X-User-Id`, non fourni par le client).

Toutes les entrées/sorties sont en `application/json`. La description complète et
formelle se trouve dans [`openapi.yaml`](openapi.yaml).

## Utilisateurs

### `POST /utilisateurs/inscription`

Crée un compte.

- Corps : `{ "nom": string, "email": string, "motDePasse": string (min 6) }`
- `201 Created` (sans corps) — succès
- `400 Bad Request` — corps invalide
- `409 Conflict` — un compte existe déjà pour ce courriel

### `POST /utilisateurs/connexion`

Authentifie un utilisateur et renvoie un JWT.

- Corps : `{ "email": string, "motDePasse": string }`
- `200 OK` :
  ```json
  {
    "token": "eyJhbGciOi...",
    "utilisateur": { "id": "uuid", "nom": "Marc Tremblay", "email": "marc@coroute.ca" }
  }
  ```
- `400 Bad Request` — corps invalide
- `401 Unauthorized` — identifiants invalides

## Trajets

### `GET /trajets`

Liste des trajets, avec filtres optionnels.

- Query : `depart`, `destination`, `date` (`YYYY-MM-DD`) — tous optionnels
- `200 OK` — `TrajetResponse[]`

### `GET /trajets/{trajetId}`

- `200 OK` — `TrajetResponse`
- `404 Not Found` — trajet inexistant

### `POST /trajets` 🔒

Crée un trajet (conducteur = utilisateur authentifié).

- Corps : `TrajetCreateRequest`
- `201 Created` — en-tête `Location` vers le trajet créé
- `400 Bad Request` — corps invalide
- `401 Unauthorized` — token absent/invalide

### `DELETE /trajets/{trajetId}` 🔒

Supprime un trajet (propriétaire seulement).

- `204 No Content` — succès
- `401 Unauthorized`
- `403 Forbidden` — non-propriétaire
- `404 Not Found`

## Réservations

### `POST /trajets/{trajetId}/reservations` 🔒

Réserve des places (opération atomique, sans sur-réservation).

- Corps : `{ "nombrePlaces": number (min 1) }`
- `201 Created` — en-tête `Location` vers la réservation créée
- `400 Bad Request` — corps invalide
- `401 Unauthorized`
- `404 Not Found` — trajet inexistant
- `409 Conflict` — places insuffisantes

### `DELETE /trajets/{trajetId}/reservations/{reservationId}` 🔒

Annule une réservation (passager propriétaire seulement).

- `204 No Content`
- `401 Unauthorized`
- `403 Forbidden` — réservation d'un autre passager
- `404 Not Found`

### `GET /trajets/{trajetId}/reservations` 🔒

Liste les réservations d'un trajet (conducteur seulement).

- `200 OK` — `ReservationResponse[]`
- `401 Unauthorized`
- `403 Forbidden` — non-conducteur
- `404 Not Found`

## Matching

### `GET /trajets/match`

Trajets compatibles avec un besoin.

- Query : `depart`, `destination`, `jours` (répété : `?jours=LUNDI&jours=MARDI`)
- `200 OK` — `MatchingResponse[]`

## Schémas

### TrajetCreateRequest

| Champ              | Type              | Contraintes                    |
| ------------------ | ----------------- | ------------------------------ |
| `depart`           | string            | obligatoire                    |
| `destination`      | string            | obligatoire                    |
| `date`             | string `YYYY-MM-DD`| trajet ponctuel               |
| `heure`            | string `HH:mm:ss` | obligatoire                    |
| `placesDisponibles`| number            | ≥ 1                            |
| `prixParPassager`  | number            | > 0                            |
| `type`             | `PONCTUEL`\|`REGULIER` | obligatoire               |
| `joursRecurrence`  | JourSemaine[]     | trajet régulier                |
| `dateDebut`        | string `YYYY-MM-DD`| trajet régulier               |
| `dateFin`          | string `YYYY-MM-DD`| trajet régulier               |

### TrajetResponse

`id`, `depart`, `destination`, `date`, `heure`, `placesRestantes`,
`prixParPassager`, `conducteurId`, `type`, `joursRecurrence`, `dateDebut`, `dateFin`.

### MatchingResponse

`id`, `depart`, `destination`, `heure`, `prixParPassager`, `placesRestantes`,
`type`, `joursCompatibles`, `scoreCompatibilite`, `conducteurId`, `date`,
`dateDebut`, `dateFin`.

### ReservationResponse

`id`, `passagerId`, `nombrePlaces`.

### JourSemaine (enum)

`LUNDI`, `MARDI`, `MERCREDI`, `JEUDI`, `VENDREDI`, `SAMEDI`, `DIMANCHE`.
