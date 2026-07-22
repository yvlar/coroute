# 🚗 CoRoute

![Backend CI](https://github.com/yvlar/coroute/actions/workflows/backend-ci.yml/badge.svg)
![Frontend CI](https://github.com/yvlar/coroute/actions/workflows/frontend-ci.yml/badge.svg)
![Full-stack CI](https://github.com/yvlar/coroute/actions/workflows/full-stack-ci.yml/badge.svg)

> **CoRoute** — Le covoiturage du quotidien au Québec. Monorepo full-stack
> réunissant une API REST Java (JAX-RS/Jersey + MongoDB) et une interface web
> React/TypeScript.

---

## 1. Présentation

CoRoute est une plateforme de covoiturage qui met en relation conducteurs et
passagers pour des trajets ponctuels ou réguliers. Les conducteurs publient des
trajets, les passagers recherchent et réservent des places, et un moteur de
_matching_ propose les trajets les plus compatibles selon l'origine, la
destination et les jours de la semaine.

## 2. Fonctionnalités principales

- 🔐 Authentification par JWT (inscription, connexion, mots de passe hachés BCrypt)
- 🚗 Publication, recherche, filtrage et suppression de trajets
- 🎫 Réservation et annulation de places, **sans sur-réservation** (mise à jour atomique)
- 🔎 Moteur de _matching_ trajet ↔ besoin (origine, destination, jours)
- 🗄️ Persistance MongoDB via Morphia
- 🐳 Déploiement conteneurisé (Docker Compose) clé en main

## 3. Architecture du monorepo

```
coroute/
├── backend/            # API REST Java (JAX-RS/Jersey, Grizzly, HK2, Morphia, MongoDB)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/{main,test}/java/io/github/yvlar/coroute/...
├── frontend/           # Interface React + TypeScript (Vite)
│   ├── package.json
│   ├── Dockerfile / Dockerfile.test
│   ├── nginx/nginx.conf
│   └── src/ tests/
├── docs/               # architecture.md, api.md, openapi.yaml
├── .github/workflows/  # backend-ci, frontend-ci, full-stack-ci
├── docker-compose.yml  # stack mongo + backend + frontend
├── .env.example
└── README.md
```

Voir [`docs/architecture.md`](docs/architecture.md) pour le détail.

## 4. Stack frontend

| Couche      | Technologie                              |
| ----------- | ---------------------------------------- |
| UI          | React 18                                 |
| Langage     | TypeScript (strict) + JSX                |
| Build       | Vite                                     |
| Tests       | Vitest + Testing Library (couverture v8) |
| Qualité     | ESLint + Prettier                        |
| Service web | Nginx (image de production)              |

## 5. Stack backend

| Couche      | Technologie                                   |
| ----------- | --------------------------------------------- |
| API         | JAX-RS (Jersey 4) sur Grizzly                 |
| Injection   | HK2                                           |
| Persistance | MongoDB + Morphia ODM                         |
| Sécurité    | JWT (jjwt), BCrypt                            |
| Build       | Maven (Java 21)                               |
| Qualité     | Checkstyle, PMD, SpotBugs, google-java-format |
| Tests       | JUnit 5, Mockito, Jersey Test, Testcontainers |
| Couverture  | JaCoCo                                        |
| Dépendances | OWASP Dependency-Check                        |

## 6. Démarrage avec Docker

```bash
cp .env.example .env
# Renseigner au minimum MONGO_ROOT_USER, MONGO_ROOT_PASSWORD
# et un JWT_SECRET (>= 32 caractères).

docker compose build
docker compose up -d
```

- Frontend : http://localhost/
- API (accès direct) : http://localhost:8080/
- API depuis le navigateur : via le proxy `/api` de Nginx

Outils de développement optionnels (Mongo Express, protégé par authentification) :

```bash
docker compose --profile dev-tools up -d   # http://localhost:8081
```

Arrêt et nettoyage :

```bash
docker compose down -v
```

## 7. Démarrage séparé en développement

**Backend**

```bash
cd backend
export JWT_SECRET="dev-only-secret-de-32-caracteres-minimum"
export MONGO_URI="mongodb://localhost:27017"   # ou une instance Mongo authentifiée
mvn exec:java   # démarre sur http://localhost:8080
```

**Frontend**

```bash
cd frontend
npm ci
npm run dev      # http://localhost:5173, proxy /api -> http://localhost:8080
```

## 8. Variables d'environnement

Toutes les variables sont documentées dans [`.env.example`](.env.example).

| Variable                 | Rôle                                            |
| ------------------------ | ----------------------------------------------- |
| `MONGO_ROOT_USER`        | Utilisateur root MongoDB                         |
| `MONGO_ROOT_PASSWORD`    | Mot de passe root MongoDB                        |
| `MONGO_DB`               | Nom de la base (défaut `coroute`)               |
| `MONGO_URI`              | URI de connexion utilisée par le backend        |
| `JWT_SECRET`             | Secret HMAC (**obligatoire, ≥ 32 caractères**)  |
| `JWT_EXPIRATION_SECONDS` | Durée de validité du token (défaut `86400`)     |
| `CORS_ALLOWED_ORIGINS`   | Origines autorisées, séparées par des virgules  |
| `NVD_API_KEY`            | Clé API NVD pour OWASP Dependency-Check (CI)     |
| `VITE_API_URL`           | Base de l'API côté frontend (défaut `/api`)     |

## 9. Commandes de test

**Backend**

```bash
cd backend
mvn clean verify           # compile, qualité, tests (unitaires + intégration + Testcontainers), couverture
mvn dependency-check:check # analyse OWASP des dépendances (NVD_API_KEY recommandée)
```

> Les tests Testcontainers (`InMongo*`) nécessitent un démon Docker en cours d'exécution.

**Frontend**

```bash
cd frontend
npm run lint
npm run format:check
npm run type-check
npm run test:coverage
npm run build
```

## 10. Structure des dossiers

Voir la section 3 et [`docs/architecture.md`](docs/architecture.md).

## 11. Sécurité

- Aucun secret n'est versionné : JWT, identifiants MongoDB et clé NVD proviennent
  de l'environnement. Copier `.env.example` en `.env` (non suivi par Git).
- Le backend **refuse de démarrer** si `JWT_SECRET` est absent ou trop court.
- CORS est restreint à une liste blanche (`CORS_ALLOWED_ORIGINS`) ; aucune
  origine n'est autorisée par défaut.
- MongoDB n'est pas exposé publiquement ; Mongo Express est réservé au profil
  `dev-tools` et protégé par authentification.
- ⚠️ **Clé NVD à révoquer** : une clé API NVD avait été committée en clair dans
  le `pom.xml`. Elle a été retirée du code, mais **doit être révoquée
  manuellement** auprès du NIST et remplacée par une nouvelle clé stockée dans le
  secret GitHub `NVD_API_KEY`.
- Dans cette version, le token JWT est conservé **en mémoire** côté frontend
  (pas de `localStorage`, pas de refresh token) : **rafraîchir la page
  déconnecte l'utilisateur**.

## 12. Contribution

1. Créer une branche à partir de `main`.
2. Respecter le formatage : `mvn com.spotify.fmt:fmt-maven-plugin:format` (backend)
   et `npm run format` (frontend).
3. S'assurer que les workflows CI passent (backend, frontend, full-stack).
4. Ouvrir une pull request vers `main`.

## 13. Licence

Distribué sous licence MIT. Voir [`LICENSE`](LICENSE).
