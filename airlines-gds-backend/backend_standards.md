# SkyLink GDS - Guía Oficial de Estilos y Estándares del Backend

Este documento unifica la topología de red, contratos de mensajería y políticas de resiliencia del ecosistema de microservicios de SkyLink GDS.

## 1. Mapa de Puertos y Topología de Red (Service Discovery)

Todos los servicios corren bajo la red virtualizada de Docker (`gds-network`) y se descubren dinámicamente mediante Eureka. Las direcciones físicas nunca se configuran de forma estática entre servicios; se utiliza la abstracción `lb://`.

| Microservicio | Puerto Físico | Tipo de Acceso | Responsabilidad Core |
| :--- | :--- | :--- | :--- |
| `config-server` | `8888` | Privado Interno | Servidor de configuración centralizado en repositorio local. |
| `eureka-server` | `8761` | Privado Interno | Service Registry ("Páginas Amarillas") para balanceo de carga. |
| `api-gateway` | `8080` | **Público Único** | Puerta de entrada, validación de JWT y Redis Rate Limiting. |
| `auth-service` | `8081` | Protegido por GW | Base de datos de agencias (`auth_db`), login con BCrypt y emisión de JWT. |
| `flight-service` | `8082` | Protegido por GW | Administración de aerolíneas, rutas y horarios (`flights_db`). |
| `booking-service` | `8083` | Protegido por GW | Ciclo de vida de reservas (`bookings_db`), generación de PNR aleatorio (6 chars). |
| `inventory-service`| `8084` | Protegido por GW | Custodia reactiva de asientos disponibles (`inventory_db`). |
| `payment-service` | `8085` | Protegido por GW | Pasarela de transacciones mock simulando cobros (`payments_db`). |

## 2. Contrato de Mensajería Asíncrona (Apache Kafka)

### Tópicos Clave:
- `booking-events`: Canal donde viajan las transacciones de reservas.

### Blindaje contra la "Píldora Envenenada" (Poison Pill):
Para evitar bucles infinitos de reintentos en la consola cuando un microservicio no puede interpretar el DTO de otro servicio debido a discrepancias en el paquete Java, la configuración en el `Config Server` debe obligar a:
1. **Ignorar las cabeceras de tipo:** `spring.json.trusted.packages=*`
2. **Deserializadores de Emergencia:** Configurar de forma explícita la clase `ErrorHandlingDeserializer` en el YAML para desviar los mensajes corruptos a una cola muerta (DLQ) sin congelar el hilo del consumidor.

## 3. Resiliencia y Control de Tráfico Perimetral

### Circuit Breaker (Resiliencia en Cascada):
Las rutas críticas expuestas en el `api-gateway` hacia servicios transaccionales deben contar con filtros de resiliencia de *Resilience4j* (`bookingCircuitBreaker`). 
- **Política de Degradación (Fallback):** Si un servicio cae (ej. `booking-service`), el Gateway debe interceptar el desbordamiento en $0\text{ ms}$ y retornar un payload de respaldo controlado con un código HTTP `503 Service Unavailable`, garantizando que la UI no sufra bloqueos por *Timeout*.

### Rate Limiting (Protección DDoS):
El Gateway implementa un filtro `RequestRateLimiter` apoyado sobre una infraestructura de **Redis** utilizando el algoritmo *Token Bucket*.
- `replenishRate`: 1 token por segundo.
- `burstCapacity`: 2 tokens de ráfaga máxima.
Al superar el umbral, el Gateway estrangula la IP emisora respondiendo con el código estándar **HTTP 429 Too Many Requests**.

## 4. Conexiones JDBC Locales (Entornos Epímeros)
Dado que los contenedores de Docker de MySQL pueden ser destruidos o recreados en caliente en el entorno local (`docker-compose down`), todas las cadenas de conexión URL en los archivos `.yml` del Config Server deben incluir obligatoriamente el parámetro reactivo:
`?createDatabaseIfNotExist=true`
Esto mitiga la excepción crítica `Unable to determine Dialect` de Hibernate, forzando a que Spring Boot levante las bases de datos vacías y autogenere las tablas mediante `ddl-auto: update` de forma transparente.