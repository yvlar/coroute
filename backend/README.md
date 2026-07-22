# CoRoute — Backend

API REST de **CoRoute** : Java 21, JAX-RS (Jersey 4) sur Grizzly, HK2, Morphia et
MongoDB. Ce module fait partie du [monorepo CoRoute](../README.md).

## Développement

```bash
export JWT_SECRET="dev-only-secret-de-32-caracteres-minimum"
export MONGO_URI="mongodb://localhost:27017"
mvn exec:java          # démarre sur http://localhost:8080
```

Le service **refuse de démarrer** si `JWT_SECRET` est absent ou trop court
(≥ 32 caractères requis).

## Build et qualité

```bash
mvn clean verify           # compile, Checkstyle, PMD, SpotBugs, tests, JaCoCo, package
mvn com.spotify.fmt:fmt-maven-plugin:format   # formater le code
mvn dependency-check:check # analyse OWASP (NVD_API_KEY recommandée)
```

> Les tests Testcontainers (`InMongo*`) nécessitent un démon Docker.

## Configuration (variables d'environnement)

| Variable                 | Rôle                                           |
| ------------------------ | ---------------------------------------------- |
| `MONGO_URI`              | URI de connexion MongoDB                        |
| `MONGO_DB`               | Nom de la base (défaut `coroute`)              |
| `JWT_SECRET`             | Secret HMAC (obligatoire, ≥ 32 caractères)     |
| `JWT_EXPIRATION_SECONDS` | Durée de validité du token (défaut `86400`)    |
| `CORS_ALLOWED_ORIGINS`   | Origines autorisées, séparées par des virgules |
| `NVD_API_KEY`            | Clé API NVD pour OWASP Dependency-Check         |

## Structure des paquets

```
io.github.yvlar.coroute
├── api.controller     # ressources JAX-RS
├── api.mapper         # mappeurs d'exceptions -> statuts HTTP
├── config             # HK2, filtres (auth, CORS), Mongo, Datastore
├── domain.model       # entités (Trajet, Reservation, Utilisateur, ...)
├── domain.service     # services métier (Matching, Jwt, ...)
├── domain.exception   # exceptions métier
├── dto.request        # objets de requête
├── dto.response       # objets de réponse
└── repository         # dépôts (Mongo + en mémoire)
```

Contrat d'API : [`../docs/api.md`](../docs/api.md) et [`../docs/openapi.yaml`](../docs/openapi.yaml).
