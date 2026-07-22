# 🚌 AllerRetour — Frontend
![CI](https://github.com/yvlar/allerretour-frontend/actions/workflows/ci.yml/badge.svg)
![CI](https://github.com/yvlar/allerretour-frontend/actions/workflows/ci.yml/badge.svg)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-70%25-green)
![ESLint](https://img.shields.io/badge/ESLint-passing-brightgreen)
![TypeScript](https://img.shields.io/badge/TypeScript-strict-blue)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED)

> Interface React du projet CoRoute — Covoiturage domicile-travail en région québécoise. GLO-2004 · Université Laval.

---

## ✨ Fonctionnalités

- 🔍 **Recherche intelligente** — Matching de trajets par ville, destination et jours de la semaine
- 🚗 **Proposition de trajets** — Formulaire conducteur (ponctuel ou régulier)
- 📅 **Réservation de places** — Sélection du nombre de places avec calcul du total
- 🔐 **Authentification JWT** — Inscription, connexion, token en mémoire
- 🗺️ **Lignes fixes** — Affichage des routes populaires en style transit
- 🐳 **Docker ready** — Stack complète en une commande

---

## 🛠️ Stack technique

| Couche | Technologie |
|---|---|
| UI | React 18 |
| Langage | JavaScript + TypeScript 5.5 (nouveaux modules) |
| Build | Vite 5 |
| Style | CSS Variables globales |
| Client API | `fetch` natif centralisé |
| Auth | JWT Bearer — hook `useAuth` (mémoire) |
| Linting | ESLint 9 (react, hooks, a11y, security) |
| Formatage | Prettier 3 |
| Tests | Vitest 2 + React Testing Library |
| Couverture | V8 (seuil 70%) |
| Git hooks | Husky + lint-staged |
| Conteneur | Docker multi-stage (Node → nginx) |
| CI | GitHub Actions |

---

## 🚀 Démarrage rapide

### Prérequis

- Node.js ≥ 20
- Docker Desktop
- Le backend [coroute-api](https://github.com/yvlar/coroute-api) dans le dossier parent

### Structure attendue

```
Documents/
├── coroute-full/           ← backend Maven (avec son Dockerfile)
└── allerretour-frontend/   ← ce projet
```

### Lancer avec Docker (recommandé)

```bash
git clone https://github.com/yvlar/allerretour-frontend.git
cd allerretour-frontend
docker compose up --build
```

| Service | URL | Description |
|---|---|---|
| Frontend | http://localhost | Interface React (nginx) |
| API | http://localhost:8080 | Backend JAX-RS CoRoute |
| Mongo Express | http://localhost:8081 | Interface admin MongoDB |

### Lancer en local (développement)

```bash
npm install --legacy-peer-deps
npm run dev
# → http://localhost:5173
```

> Le backend doit tourner sur `:8080` — le proxy Vite redirige automatiquement.

---

## 📡 Flux principaux

### 1. Recherche de trajets

```
UI → GET /api/trajets/match?depart=Granby&jours=LUNDI&jours=MERCREDI
   ← MatchingResponse[] avec scoreCompatibilite
```

### 2. Inscription + Connexion

```
UI → POST /api/utilisateurs/inscription  { nom, email, motDePasse }
UI → POST /api/utilisateurs/connexion    { email, motDePasse }
   ← { token: "eyJhbGciOiJIUzI1NiJ9..." }
```

### 3. Créer un trajet (conducteur)

```
UI → POST /api/trajets
     Authorization: Bearer <token>
     { depart, destination, heure, placesDisponibles, prixParPassager, type, joursRecurrence, dateDebut, dateFin }
   ← 201 Created
```

### 4. Réserver une place

```
UI → POST /api/trajets/{id}/reservations
     Authorization: Bearer <token>
     { nombrePlaces: 2 }
   ← 201 Created
```

---

## 🔑 Enums backend

```
JourSemaine : LUNDI  MARDI  MERCREDI  JEUDI  VENDREDI  SAMEDI  DIMANCHE
TrajetType  : PONCTUEL  REGULIER
Heure       : format ISO "07:00:00" → affiché "07h00" dans l'UI
```

---

## 🏗️ Architecture

```
src/
├── features/
│   ├── auth/
│   │   └── components/AuthModal.jsx        ← Connexion / Inscription
│   ├── trajets/
│   │   ├── components/
│   │   │   ├── TrajetCard.jsx              ← Carte résultat de recherche
│   │   │   ├── ResultsSection.jsx          ← Liste des résultats
│   │   │   ├── CreateTrajetModal.tsx       ← Formulaire création (TypeScript)
│   │   │   └── ReservationModal.tsx        ← Formulaire réservation (TypeScript)
│   │   └── utils/
│   │       ├── trajetStatus.js             ← Couleurs selon placesRestantes
│   │       └── formatters.js              ← "07:00:00" → "07h00"
│   └── routes/
│       └── components/RoutesFixesSection.jsx
├── shared/
│   ├── components/   Navbar · Hero · Toast · JoursPicker
│   ├── constants/    colors.js · jours.js (labels ↔ enums)
│   ├── hooks/        useAuth · useSearch · useToast · useCreateTrajet
│   ├── services/     api.js (client fetch centralisé)
│   └── types/        trajet.ts (types alignés sur les DTOs Java)
├── styles/global.css
├── App.jsx
└── main.jsx
```

---

## 🧪 Tests et qualité

### Lancer les tests

```bash
# Local
npm run test

# Docker
docker compose run --rm test

# Avec rapport de couverture
docker compose run --rm test npm run test:coverage
```

### Couverture

| Type | Fichiers testés |
|---|---|
| **Unitaires** | `trajetStatus` · `formatters` · `jours` · `useToast` · `useSearch` · `useAuth` |
| **Intégration** | `TrajetCard` · `AuthModal` · `ReservationModal` · `Toast` · `api` |

### Analyse de code

```bash
# ESLint — 0 warning toléré
npm run lint

# Prettier
npm run format:check

# TypeScript strict
npm run type-check

# Audit sécurité npm
npm run audit:security
```

### Standards de qualité

| Outil | Objectif |
|---|---|
| **ESLint** | react, hooks, jsx-a11y, security — 0 warning |
| **Prettier** | Formatage uniforme |
| **TypeScript** | Strict — nouveaux modules en `.ts/.tsx` |
| **Vitest + RTL** | Couverture minimale 70% |
| **npm audit** | Aucune vulnérabilité `high` |

---

## 🔄 Pipeline CI/CD

Le pipeline GitHub Actions se déclenche à chaque **push** et **pull request** sur `main` et `develop`.

```
push / PR
    │
    ├── 1. Lint & Format ──── ESLint + Prettier check
    │
    ├── 2. Tests & Coverage ── Vitest (seuil 70%) + upload rapport
    │       (nécessite lint ✅)
    │
    ├── 3. Sécurité ────────── npm audit --audit-level=high
    │
    ├── 4. Build ───────────── npm run build + upload dist/
    │       (nécessite lint ✅ + test ✅)
    │
    └── 5. Docker Build ─────── Build image nginx (main seulement)
            (nécessite build ✅)
```

---

## 🐳 Docker Compose

```yaml
services:
  mongo:          # MongoDB 7.0 sur :27017
  api:            # Backend JAX-RS sur :8080
  frontend:       # React + nginx sur :80
  mongo-express:  # Interface web MongoDB sur :8081
  test:           # Runner Vitest (docker compose run --rm test)
```

### Variables d'environnement

| Variable | Défaut | Description |
|---|---|---|
| `VITE_API_URL` | `/api` | Base URL de l'API (proxifiée) |
| `MONGO_ROOT_USER` | `admin` | Utilisateur MongoDB |
| `MONGO_ROOT_PASSWORD` | `password` | Mot de passe MongoDB |
| `MONGO_DB` | `coroute` | Nom de la base de données |

---

## 📁 Structure du projet

```
allerretour-frontend/
├── src/                  ← Code source React
├── tests/                ← Tests unitaires et intégration
├── nginx/nginx.conf       ← Config nginx + proxy /api/
├── .github/workflows/    ← CI/CD GitHub Actions
├── Dockerfile            ← Multi-stage : Node builder → nginx
├── Dockerfile.test       ← Image dédiée aux tests
├── docker-compose.yml    ← Stack complète
├── vite.config.js        ← Alias @/ + proxy Vite
├── tsconfig.json         ← Config TypeScript
├── eslint.config.js      ← ESLint 9 flat config
└── package.json
```

---


## 📄 Licence

MIT License — voir [LICENSE](LICENSE)