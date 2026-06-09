# SkyLink GDS — Backend

Java 21, Spring Boot 3.4.3 / 4.0.6, Spring Cloud 2024.0.0 / 2025.1.1.

## Architecture rules

- **Database-per-Service**: Each service owns its MySQL schema. No cross-service JOINs.
- **Saga Choreography (Kafka)**: Booking → `booking-events` → Payment → `payment-events` → Booking + Inventory. Initial state always `PENDING`.
- **API Composition**: `booking-service` uses OpenFeign (`AuthClient`, `FlightClient`) to assemble dashboard DTOs in memory.
- **Perimeter Security**: `api-gateway` validates JWT. Internal services trust gateway but still validate roles.

## Boot order

1. `config-server` (8888) — profile `native`, search `file:///.../config`
2. `discovery-server` (8761)
3. `api-gateway` (8080)
4. Business services (any order, ports 8081–8085)

## Port map

| Service | Port | Database |
|---|---|---|
| config-server | 8888 | — |
| discovery-server | 8761 | — |
| api-gateway | 8080 | — |
| auth-service | 8081 | gds_auth_db |
| flight-service | 8082 | flights_db |
| booking-service | 8083 | bookings_db |
| payment-services | 8084 | payments_db |
| inventory-service | 8085 | inventory_db |

## Known quirks

- **Version mismatch**: `auth-service`, `api-gateway`, `booking-service`, `config-server`, `discovery-server`, `flight-service` use Boot **3.4.3** / Spring Cloud **2024.0.0**. `inventory-service` and `payment-services` use Boot **4.0.6** / Spring Cloud **2025.1.1**.
- **Config import**: `auth-service` uses non-optional `spring.config.import=configserver:...` and `spring.main.allow-bean-definition-overriding=true`. All other services use `optional:configserver:...`.
- **Inventory config format**: `inventory-service` uses `application.yaml`. All others use `application.properties`.
- **Cross-service compile dependency**: `payment-services/pom.xml` declares a compile-scope dependency on `booking-service` (anti-pattern — model duplication would be cleaner).
- **JWT secret**: Hardcoded as `TuClaveSecretaSuperLargaParaFirmarLosTokensGDS2026` in `config/api-gateway.yaml` and `config/auth-service.yaml`.
- **JDBC URLs**: Must include `createDatabaseIfNotExist=true` so Hibernate auto-creates DBs with `ddl-auto: update`.
- **Kafka poison-pill protection**: Configure `spring.json.trusted.packages=*`, `spring.json.use.type.headers=false`, and `ErrorHandlingDeserializer` in consumer YAMLs.

## Kafka topics

| Topic | Producer | Consumer(s) | Key payload |
|---|---|---|---|
| `booking-events` | `booking-service` | `payment-services` | `BookingCreatedEvent` |
| `payment-events` | `payment-services` | `booking-service`, `inventory-service` | `PaymentResultEvent` |

## Code conventions

- **DI**: `@RequiredArgsConstructor` (constructor injection). No `@Autowired` on fields.
- **DTOs/events**: Prefer Java 21 `records`. Domain entities (JPA) use Lombok `@Data`/`@Builder`.
- **Exceptions**: `@RestControllerAdvice` per service for business exceptions.
- **Logging**: `@Slf4j`; log Kafka event processing start/end with PNR.
- **REST envelope**: Paginated responses use `PaginatedResponse<T>` record: `{ data, currentPage, pageSize, totalElements, totalPages, isLast }`.
- **Gateway routes** use `AuthenticationFilter` + optional `CircuitBreaker` + optional `RequestRateLimiter` filters.

## Build

```bash
# From any service directory:
mvn clean package -DskipTests
```

CI finds all `pom.xml` and runs the same command per service.

## Detailed reference

See `backend_standards.md` for full resilience config, rate limiting values, and network topology.
