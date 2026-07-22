# Architecture de CoRoute

CoRoute est un monorepo full-stack composé d'un frontend React/TypeScript, d'un
backend Java (JAX-RS/Jersey) et d'une base MongoDB, orchestrés par Docker Compose.

## Vue d'ensemble

```
Navigateur
   │  http://localhost/            (HTML/JS/CSS statiques)
   │  http://localhost/api/...     (appels API)
   ▼
┌─────────────┐   /api/*  →  proxy   ┌──────────────┐        ┌─────────────┐
│  frontend   │ ───────────────────► │   backend    │ ─────► │   mongo     │
│  (Nginx +   │   réseau Docker      │ (Jersey/     │ Morphia│ (MongoDB)   │
│   React)    │   interne            │  Grizzly)    │        │             │
└─────────────┘                      └──────────────┘        └─────────────┘
      :80                                  :8080              interne (pas de port publié)
```

## Frontend

- **React 18 + TypeScript** (build **Vite**), servi en production par **Nginx**.
- Organisation par fonctionnalités : `src/features/*` (auth, trajets, routes) et
  code partagé `src/shared/*` (composants, hooks, services, types, constantes).
- Le client API (`src/shared/services/api.ts`) est typé et centralise la gestion
  des erreurs (`ApiError`), du token JWT (en-tête `Authorization: Bearer`) et de
  la sémantique des réponses (204 → `null`, 201 → JSON si présent sinon `null`).
- Les modèles partagés (`Utilisateur`/`AuthUser`, `Trajet`, `Reservation`,
  `MatchingResponse`, etc.) sont centralisés dans `src/shared/types/trajet.ts`.

## Backend

Architecture en couches (domaine / application / infrastructure) :

- **API (`api.controller`, `api.mapper`)** — ressources JAX-RS et mappeurs
  d'exceptions vers des statuts HTTP.
- **Domaine (`domain.model`, `domain.service`, `domain.exception`)** — entités
  (`Trajet`, `Reservation`, `Utilisateur`), règles métier et services
  (`MatchingService`, `JwtService`, ...).
- **Infrastructure (`repository`, `config`)** — dépôts MongoDB via Morphia,
  configuration HK2, filtres (authentification, CORS), fabrique de `Datastore`.

L'injection de dépendances est assurée par **HK2** (`ApplicationConfig`). Le
serveur HTTP est **Grizzly**.

## MongoDB

- Persistance via **Morphia** (ODM). Les entités `Trajet` (avec ses
  `Reservation` embarquées) et `Utilisateur` sont mappées depuis le paquet
  `io.github.yvlar.coroute.domain.model`.
- La connexion est configurée par `MONGO_URI` / `MONGO_DB` (aucun identifiant
  codé en dur). MongoDB n'expose aucun port publiquement dans la stack Docker.
- **Réservations sans sur-réservation** : `MongoTrajetRepository.reserverAtomiquement`
  effectue une mise à jour conditionnelle et atomique (`findAndModify` avec le
  filtre `placesDisponibles >= nombrePlaces`, puis `$inc` et `$push` dans la même
  opération). L'atomicité au niveau du document empêche deux réservations
  concurrentes de dépasser le nombre de places.

## Authentification

- Inscription : mot de passe haché avec **BCrypt**.
- Connexion : renvoie un **JWT** signé (HMAC) et les informations publiques de
  l'utilisateur (`{ token, utilisateur: { id, nom, email } }`).
- Le secret (`JWT_SECRET`) et l'expiration (`JWT_EXPIRATION_SECONDS`) proviennent
  de l'environnement ; le service valide le secret au démarrage.
- `AuthenticationFilter` vérifie le jeton `Bearer` pour les routes protégées et
  injecte l'identifiant utilisateur dans l'en-tête interne `X-User-Id`.
- Côté frontend, le token est conservé **en mémoire** (pas de `localStorage`, pas
  de refresh token) : rafraîchir la page déconnecte l'utilisateur.

## Communication `/api`

- En production, le navigateur appelle `/api/...`. Nginx (`frontend/nginx/nginx.conf`)
  réécrit `^/api/(.*)` vers `http://backend:8080/$1` sur le réseau Docker interne.
- En développement, le proxy Vite fait la même réécriture vers
  `http://localhost:8080`.
- Le backend applique une politique **CORS** basée sur une liste blanche
  (`CORS_ALLOWED_ORIGINS`) et ajoute `Vary: Origin`.

## Docker

- `docker-compose.yml` (racine) définit `mongo`, `backend` (build `./backend`),
  `frontend` (build `./frontend`) et, sous le profil `dev-tools`, `mongo-express`.
- Healthchecks sur les trois services principaux ; volume persistant pour Mongo ;
  toutes les valeurs sensibles proviennent de `.env`.

## Intégration continue

Trois workflows GitHub Actions ciblant `main`, déclenchés par chemin :

- **backend-ci** — compile, format, Checkstyle, PMD, SpotBugs, tests (unitaires,
  intégration, Testcontainers), JaCoCo, package, build Docker, et OWASP
  Dependency-Check (échoue si CVSS ≥ 7).
- **frontend-ci** — ESLint, Prettier, type-check, tests avec couverture, build,
  build Docker, et `npm audit` bloquant sur les vulnérabilités high/critical.
- **full-stack-ci** — construit les images, démarre la stack, attend les
  healthchecks, vérifie frontend/backend, exécute un test de fumée via `/api`,
  puis arrête proprement (`docker compose down -v`).
