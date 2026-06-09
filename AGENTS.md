# SkyLink GDS — Airlines GDS System

Monorepo with two independent subprojects (no shared root config):

| Subproject | Stack | Location | Build |
|---|---|---|---|
| Backend | Java 21, Spring Boot 3.4.3 / 4.0.6, Spring Cloud 2024.0.0 / 2025.1.1 | `airlines-gds-backend/` | `mvn clean package -DskipTests` (per service dir) |
| Frontend | Angular 21, TypeScript 5.9, Vitest, SCSS | `airlines-gds-frontend/` | `npm start` / `ng build` |

## Quick start — backend

```bash
# 1. Infrastructure
cd airlines-gds-backend/infrastructure && docker compose up -d
# 2. Start services in order:
#    config-server (8888) → discovery-server (8761) → api-gateway (8080) → business services
```

## Quick start — frontend

```bash
cd airlines-gds-frontend && npm start
# → http://localhost:4200
```

## Repo structure

```
airlines-gds-backend/
  config/            → Centralized YAML config (served by config-server)
  infrastructure/    → Docker Compose (MySQL, Redis, Kafka, Zipkin, Prometheus, Grafana)
  services/          → 8 microservices, each own pom.xml
  AGENTS.md          → Backend architecture, quirks, conventions
airlines-gds-frontend/
  AGENTS.md          → Frontend conventions (standalone components, SCSS BEM, Vitest)
```

## CI

`.github/workflows/ci.yaml` on push/PR to main/master: finds all `pom.xml` and runs `mvn clean package -DskipTests` per service.

## Key constraints

- `api-gateway` (port 8080) is the only public entry point. All frontend traffic routes through it.
- Kafka saga flow: `booking-service` → `booking-events` → `payment-services` → `payment-events` → `booking-service` + `inventory-service`
- Each backend service owns its MySQL database. No cross-service JOINs.
- See each subproject's `AGENTS.md` for detailed conventions and gotchas.
