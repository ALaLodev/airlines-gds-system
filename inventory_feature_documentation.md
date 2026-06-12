# Especificación Técnica de Implementación: Seat Inventory Management — SkyLink GDS

Este documento sirve como fuente de información estructurada para **NotebookLM** y describe de forma exclusiva y exhaustiva la arquitectura, el modelo de datos, la lógica analítica, el flujo reactivo y la interfaz de usuario del módulo de **Gestión de Inventario de Asientos (inventory-service)** implementado en la plataforma SkyLink GDS.

---

## 1. Introducción y Propósito de la Feature

El módulo de **Seat Inventory Management** (Gestión de Inventario de Asientos) está diseñado para monitorear y optimizar la capacidad de cabina en tiempo real de toda la flota comercial. Sus objetivos principales son:
* **Control de Ocupación (Load Factor)**: Proveer visibilidad instantánea del porcentaje de asientos vendidos frente a la capacidad total desglosada por clase de tarifa.
* **Optimización de Ingresos (Yield Management)**: Proyectar la demanda y evaluar el valor financiero del inventario no vendido para implementar estrategias de precios dinámicos (Surge Pricing).
* **Gestión de Riesgo y Alertas**: Detectar preventivamente sobreventas (overbooking) o picos críticos de ocupación que superen el 90%.
* **Override Manual**: Permitir a los analistas de ingresos de la aerolínea ajustar manualmente el volumen de reservas o forzar el cierre de ventas de un vuelo.

---

## 2. Arquitectura del Backend: Microservicio `inventory-service`

El backend está desarrollado como un microservicio independiente y aislado, integrado en el ecosistema de Spring Cloud.

* **Tecnología**: Java 21, Spring Boot 4.0.6, Spring Security, y Spring Data JPA.
* **Registro y Descubrimiento**: Actúa como cliente de Eureka (`INVENTORY-SERVICE`) para registrarse en el servidor de descubrimiento en el puerto `8761`.
* **Configuración Centralizada**: Consume sus propiedades del `config-server` (puerto `8888`).
* **Base de Datos Dedicada**: Administra su propio esquema en la base de datos `inventory_db` (puerto `3306`), garantizando que ningún otro microservicio realice lecturas o escrituras directas sobre su tabla.

### 2.1 Modelo de Datos (`Inventory.java`)

La entidad JPA `Inventory` mapea el estado del inventario a la tabla `inventories`:

```java
@Entity
@Table(name = "inventories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false, unique = true)
    private Long scheduleId; // ID del itinerario original en flight-service

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(name = "aircraft_type", nullable = false)
    private String aircraftType;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status; // ON_SALE, LIMITED, EARLY_BOOKING, CLOSED

    @Column(name = "base_fare", nullable = false)
    private Double baseFare;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats; // Asientos totales menos reservas de todas las clases

    // Desglose de Cabina Turista (Economy)
    @Column(name = "economy_total", nullable = false)
    private Integer economyTotal;
    @Column(name = "economy_booked", nullable = false)
    private Integer economyBooked;

    // Desglose de Cabina Turista Premium (Premium)
    @Column(name = "premium_total", nullable = false)
    private Integer premiumTotal;
    @Column(name = "premium_booked", nullable = false)
    private Integer premiumBooked;

    // Desglose de Cabina Business
    @Column(name = "business_total", nullable = false)
    private Integer businessTotal;
    @Column(name = "business_booked", nullable = false)
    private Integer businessBooked;

    // Desglose de Primera Clase (First) - Admite nulos si la aeronave no tiene esta cabina
    @Column(name = "first_total")
    private Integer firstTotal;
    @Column(name = "first_booked")
    private Integer firstBooked;
}
```

---

## 3. APIs Expuestas y Seguridad

El microservicio expone sus endpoints bajo la raíz `/api/inventories/**` protegidos mediante autenticación basada en JWT.

### 3.1 Seguridad Interna (`JwtAuthenticationFilter` + `SecurityConfig`)
* El microservicio cuenta con un filtro personalizado (`JwtAuthenticationFilter`) que extrae la cabecera `Authorization: Bearer <token>` transmitida por el API Gateway.
* Valida la firma del token utilizando la clave secreta compartida (`jwt.secret`) obtenida del servidor de configuración centralizado.
* Si el token es válido, inyecta los roles del usuario en el `SecurityContextHolder` de Spring.
* La configuración de seguridad (`SecurityConfig`) restringe todas las peticiones a usuarios autenticados.

### 3.2 Endpoints REST (`InventoryController.java`)

| Método | Endpoint | Descripción | Parámetros de Consulta / Body |
|---|---|---|---|
| `GET` | `/api/inventories` | Recupera la lista paginada de inventarios de vuelos. | `page`, `size`, `search` (número de vuelo/aeropuertos), `status` (filtro de estado) |
| `GET` | `/api/inventories/{id}` | Recupera el inventario específico de un vuelo por su ID. | Ninguno |
| `GET` | `/api/inventories/metrics` | Retorna los KPI consolidados globales de inventarios. | Ninguno |
| `GET` | `/api/inventories/alerts` | Retorna advertencias analíticas sobre la capacidad. | Ninguno |
| `PUT` | `/api/inventories/{id}` | Realiza un ajuste manual de reservas o estados de venta. | Body JSON: `InventoryUpdateRequest` |

---

## 4. Lógica de Negocio y Analíticas (`InventoryService.java`)

La clase `InventoryService` calcula dinámicamente indicadores agregados de rendimiento (KPI) escaneando el estado de todos los registros:

### 4.1 Métricas Consolidadas (KPIs)
* **Vuelos Activos (Active Flights)**: Cuenta el total de vuelos que **no** están en estado `CLOSED`.
* **Factor de Ocupación Promedio (Avg. Load Factor)**: Suma el porcentaje individual de ocupación de cada vuelo (`(asientos_reservados / asientos_totales) * 100`) y calcula el promedio ponderado del sistema.
* **Valor Comercial No Vendido (Unsold Inventory Value)**: Calcula el dinero potencial remanente multiplicando la cantidad de asientos libres totales de cada vuelo por su respectiva tarifa base (`baseFare`), consolidando una cifra global.

### 4.2 Lógica del Motor de Alertas
El servicio genera una lista de notificaciones en tiempo real evaluando límites críticos:
* **Alerta Crítica (CRITICAL)**: Si alguna clase supera su límite (`booked > total`), emite una alerta roja: *"A cabin class is overbooked. Immediate action required."*
* **Advertencia (WARNING)**: Si la ocupación global del vuelo supera el `90%`, emite una alerta amarilla: *"Load factor above 90%. Consider price surge recommendation."* recomendando un incremento de precio para el algoritmo de Dynamic Pricing.
* **Estado Operativo (INFO)**: Si no existen alarmas, emite un estado de tranquilidad: *"All inventory levels are within normal parameters."*

---

## 5. Integración Reactiva mediante Mensajería (Kafka)

Para asegurar la consistencia eventual entre la creación de reservas y la disponibilidad real en cabina sin generar acoplamientos rígidos entre servicios, se implementa una cola de mensajería asíncrona:

1. **Flujo Saga**:
   * Cuando se emite un pago en `payment-services`, se publica un evento en el topic de Kafka `payment-events`.
   * El consumidor `InventoryConsumer.java` en `inventory-service` escucha activamente este topic en el grupo `inventory-group`.
2. **Procesamiento de Pago (`updateInventory`)**:
   ```java
   @KafkaListener(topics = "payment-events", groupId = "inventory-group")
   public void updateInventory(PaymentResultEvent event) {
       if ("SUCCESS".equals(event.getPaymentStatus())) {
           log.info("🪑 Reduciendo inventario para el vuelo: {}", event.getScheduleId());
           Optional<Inventory> inventoryOpt = inventoryRepository.findByScheduleId(event.getScheduleId());
           
           if (inventoryOpt.isPresent()) {
               Inventory inventory = inventoryOpt.get();
               // Reducción atómica de asiento disponible
               inventory.setAvailableSeats(inventory.getAvailableSeats() - 1);
               inventoryRepository.save(inventory);
               log.info("✅ Asiento restado con éxito. Quedan {} disponibles.", inventory.getAvailableSeats());
           }
       }
   }
   ```

---

## 6. Frontend: Panel de Control en Angular

El frontend provee una experiencia visual premium de administración basada en el Stitch Design System (Bento Grid):

### 6.1 Módulos y Componentes
* **Servicio (`inventory.service.ts`)**: Modula las peticiones HTTP agregando los parámetros de paginación y cabeceras JWT de forma transparente mediante el `authInterceptor`.
* **Template (`inventory.html`)**: Estructura la visualización en tres secciones principales:
  1. *Tarjetas de Métricas*: 4 tarjetas con diseño Bento Grid que muestran los vuelos activos, el factor de carga, el valor no vendido y el estado operativo del motor de ingresos (con un indicador de pulso animado).
  2. *Live Inventory Grid*: Tabla paginada y ordenada. Cada celda de clase contiene una barra de progreso que indica el porcentaje de reservas. La barra adopta colores dinámicos: verde para baja ocupación (`<50%`), amarilla para ocupación en progreso (`50%-80%`) y roja para ocupación crítica (`>80%`).
  3. *Yield & Alertas*: Muestra la predicción de rendimiento de 24 horas y el buzón de notificaciones en tiempo real.
* **Estilos (`inventory.scss`)**: Emplea la metodología BEM, transiciones de escala al pasar el mouse por encima y tooltips detallados en hover.

### 6.2 Corrección de Renderizado CSS (Flexbox Height Fix)
* **Incidencia**: Las barras del gráfico de Yield Prediction no se mostraban (altura en 0px) a pesar de tener un porcentaje asignado dinámicamente (ej. `height: 40%`).
* **Causa**: El contenedor individual de cada barra (`&__chart-bar-wrapper`) no tenía una altura de referencia, por lo que Flexbox colapsaba su altura a `0px`.
* **Solución**: Se inyectó `height: 100%;` y `justify-content: flex-end;` en el contenedor para permitir a la barra calcular su porcentaje sobre la altura real del gráfico (240px) y anclar su base al eje inferior del gráfico.

---

## 7. Inicialización de Datos de Prueba (DataLoader)

Si la base de datos se encuentra vacía al iniciar el backend, `DataLoader.java` siembra 6 vuelos transcontinentales con variaciones estratégicas de ocupación para validar todas las alertas visuales y de negocio del sistema:

1. **SL-402 (LHR → JFK)**: A350-1000, ocupación moderada-alta, en venta.
2. **SL-118 (DXB → SYD)**: B787-9, ocupación casi al límite (todas las plazas First ocupadas: 6/6), estado limitado.
3. **SL-952 (HND → CDG)**: A350-900, sin Primera Clase (First N/A), estado de reserva anticipada (bajo factor de carga).
4. **SL-615 (MAD → MIA)**: B777-300ER, ocupación balanceada.
5. **SL-788 (SIN → LAX)**: A380-800, factor de ocupación global superior al **91%**, lo que genera automáticamente una **Alerta de Advertencia** por alta demanda.
6. **SL-330 (FRA → NRT)**: B787-10, ocupación media.
