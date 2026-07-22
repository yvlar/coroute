# CoRoute — Frontend

Interface web de **CoRoute** : React 18 + TypeScript, build Vite, servie en
production par Nginx. Ce paquet fait partie du [monorepo CoRoute](../README.md).

## Développement

```bash
npm ci
npm run dev          # http://localhost:5173 (proxy /api -> http://localhost:8080)
```

## Scripts

| Commande                 | Rôle                                   |
| ------------------------ | -------------------------------------- |
| `npm run dev`            | Serveur de développement Vite (HMR)    |
| `npm run build`          | Build de production (`dist/`)          |
| `npm run lint`           | ESLint (0 warning toléré)              |
| `npm run format:check`   | Vérification Prettier                  |
| `npm run type-check`     | Vérification TypeScript (strict)       |
| `npm run test`           | Tests Vitest                           |
| `npm run test:coverage`  | Tests avec rapport de couverture       |
| `npm run audit:security` | `npm audit --audit-level=high`         |

## Structure

```
src/
├── features/        # auth, trajets, routes (composants + logique par domaine)
├── shared/
│   ├── components/  # Navbar, Hero, Toast, ...
│   ├── hooks/       # useAuth, useSearch, useCreateTrajet, useToast
│   ├── services/    # api.ts (client HTTP typé)
│   ├── types/       # modèles partagés (Trajet, Reservation, ...)
│   └── constants/   # jours, couleurs
└── styles/          # global.css
tests/               # unit/ et integration/
nginx/               # configuration Nginx de production (proxy /api)
```

## Configuration

`VITE_API_URL` (défaut `/api`) définit la base des appels API. Voir
[`.env.example`](.env.example) et le [`.env.example` racine](../.env.example).

## API

Le client API et le contrat sont décrits dans [`../docs/api.md`](../docs/api.md)
et [`../docs/openapi.yaml`](../docs/openapi.yaml).
