# Especificación Técnica de Implementación: Vuelos y Agencias — SkyLink GDS

Este documento sirve como fuente de información estructurada para **NotebookLM** y describe de forma exhaustiva la arquitectura, el modelo de datos, la API y la interfaz de usuario de las funcionalidades de **Vuelos (Flight-Service)** y **Agencias (Agency-Service)** implementadas en la plataforma SkyLink GDS.

---

## 1. Arquitectura General del Sistema

SkyLink GDS es un sistema de distribución global de aerolíneas diseñado como un **Monorepo** con una arquitectura de microservicios en el backend y una aplicación web moderna basada en componentes en el frontend:

- **Backend**: Microservicios basados en **Java 21** y **Spring Boot 3.4.3 / 4.0.6**, coordinados por **Spring Cloud (Config Server y Discovery Server)** y expuestos de manera centralizada a través de **API Gateway**.
- **Frontend**: Aplicación SPA basada en **Angular 21**, **TypeScript 5.9** y estilos encapsulados con **SCSS (arquitectura BEM)**.
- **Acceso Único**: Toda la comunicación del frontend se realiza a través de `api-gateway` en el puerto `8080`, el cual propaga la identidad del usuario mediante cabeceras de tokens JWT firmados.

---

## 2. Feature: Vuelos (`flight-service`)

El módulo de vuelos gestiona la creación, visualización y filtrado de itinerarios y rutas operativas de la aerolínea.

### 2.1 Backend: Microservicio `flight-service`

Es el encargado de administrar el ciclo de vida de los vuelos de la aerolínea.

#### A. Modelo de Datos (`Flight.java`)
Representa un vuelo programado. Se mapea a la tabla `flights` en su base de datos MySQL correspondiente:

- `id` (Long, PK): Identificador único auto-generado.
- `flightNumber` (String): Código único de vuelo (ej. *LA2410*, *IB3202*).
- `origin` (String): Código IATA del aeropuerto de origen (ej. *MAD*, *JFK*).
- `destination` (String): Código IATA del aeropuerto de destino (ej. *MIA*, *CDG*).
- `departureTime` (LocalDateTime): Fecha y hora de salida.
- `arrivalTime` (LocalDateTime): Fecha y hora de llegada.
- `price` (Double): Precio base del pasaje.
- `availableSeats` (Integer): Número de asientos disponibles (por defecto *150* o *180* en cabina estándar).

#### B. Endpoints de la API (`FlightController.java`)
Expuestos a través del gateway en la ruta `/api/flights/**`:

| Método | Endpoint | Descripción | Seguridad |
|---|---|---|---|
| `GET` | `/api/flights` | Obtiene la lista completa de vuelos programados. | Permitido a usuarios autenticados. |
| `POST` | `/api/flights` | Crea y programa un nuevo vuelo. | Restringido a administradores / aerolíneas. |

#### C. Control de Estados y Negocio
El estado del vuelo (*On-Time*, *Delayed*, *Cancelled*) se deriva dinámicamente mediante la codificación del número de vuelo para asegurar consistencia visual en la simulación sin sobrecargar la base de datos de estados transitorios:
- **Cancelled**: Si la suma de los caracteres ASCII del número de vuelo es divisible entre `9`.
- **Delayed**: Si es divisible entre `6`.
- **On-Time**: Para el resto de los casos (estado por defecto).

---

### 2.2 Frontend: Módulo de Itinerarios (`Flights`)

Ubicado en `src/app/pages/flights/`, se encarga de proveer una vista interactiva para el personal administrativo.

#### A. Métricas en Tiempo Real (Bento Grid)
- **Rutas Activas**: Cálculo dinámico de la cantidad de pares origen-destino únicos programados.
- **Vuelos Hoy**: Cantidad de vuelos cuya fecha de salida coincide con la fecha actual del sistema.
- **On-Time Performance (OTP)**: Porcentaje de vuelos cuyo estado derivado es *On-Time*.

#### B. Filtros y Búsqueda Interactiva
- **Buscador predictivo**: Filtra la lista instantáneamente por número de vuelo, ciudad de origen o ciudad de destino.
- **Selectores de Categoría**: Pestañas de filtrado rápido para ver:
  - *Todos los vuelos (ALL)*.
  - *En el aire (IN_AIR)*: Vuelos cuya hora de salida ya pasó y su hora de llegada es futura en relación con la hora actual.
  - *Demorados (DELAYED)*.
- **Paginación del Lado del Cliente**: Segmenta la lista en páginas ajustables (tamaño de página predeterminado: 5).

#### C. Modal de Programación (Schedule Flight)
Formulario reactivo integrado para añadir nuevos vuelos con validación de datos que interactúa de manera directa con el endpoint `POST` de `flight-service`.

---

## 3. Feature: Agencias (`agency-service`)

Este módulo proporciona a los operadores del GDS y a los administradores del sistema un control completo y analítico sobre las agencias de viajes asociadas que emiten pasajes a través de SkyLink.

### 3.1 Backend: Microservicio `agency-service`

Microservicio dedicado creado desde cero para aislar el dominio de agencias.

#### A. Modelo de Datos (`Agency.java`)
Define las propiedades y rendimiento operativo de cada agencia en la tabla `agencies` de la base de datos `agency_db`:

- `id` (Long, PK): Identificador único auto-generado.
- `agencyName` (String): Nombre comercial de la agencia (ej. *Viajes El Corte Inglés*).
- `iataCode` (String): Código numérico de acreditación IATA de 7 dígitos.
- `city` (String) / `country` (String): Ubicación geográfica.
- `region` (Enum): Región de operación (`EMEA`, `APAC`, `AMER`).
- `contactName` (String) / `contactEmail` (String): Datos de contacto de la persona responsable.
- `status` (Enum): Estado operativo de la agencia (`ACTIVE` o `SUSPENDED`).
- `bookings30d` (Integer): Cantidad acumulada de reservas creadas por la agencia en los últimos 30 días.
- `complianceRate` (Double): Ratio de cumplimiento normativo y financiero de la agencia (0 a 100%).

#### B. Endpoints de la API (`AgencyController.java`)
Expuestos a través del gateway en la ruta `/api/agencies/**`:

| Método | Endpoint | Descripción | Parámetros de Consulta |
|---|---|---|---|
| `GET` | `/api/agencies` | Lista paginada y filtrable de agencias. | `page`, `size`, `search`, `status`, `region` |
| `GET` | `/api/agencies/{id}` | Recupera la información de una agencia específica. | Ninguno |
| `POST` | `/api/agencies` | Registra una nueva agencia asociada. | Body JSON (`AgencyRequest`) |
| `PUT` | `/api/agencies/{id}` | Actualiza los datos o el estado de una agencia. | Body JSON (`AgencyRequest`) |
| `DELETE` | `/api/agencies/{id}` | Remueve una agencia del directorio GDS. | Ninguno |
| `GET` | `/api/agencies/metrics` | Retorna los KPI generales para las tarjetas métricas. | Ninguno |
| `GET` | `/api/agencies/top-performers` | Lista de las principales agencias por reservas. | Ninguno |

#### C. Seguridad y Mapeo JWT
El microservicio implementa un filtro de seguridad personalizado (`JwtAuthenticationFilter`) que extrae la cabecera `Authorization` enviada por el API Gateway, valida que contenga un token JWT válido y establece el contexto de seguridad de Spring Security (`SecurityContextHolder`) con los roles del usuario.

#### D. Base de Datos e Inicialización (Seeder)
Para entornos de desarrollo, el microservicio incluye un `DatabaseSeeder` que inicializa automáticamente 5 agencias estratégicas con datos de prueba realistas distribuidos entre regiones:
- 3 en la región **EMEA** (para validar el correcto agrupamiento regional).
- 1 en **APAC**.
- 1 en **AMER**.

---

### 3.2 Frontend: Panel de Control de Agencias (`Agencies`)

Ubicado en `src/app/pages/agencies/`, implementa una interfaz de usuario avanzada y pulida basada en los principios del Stitch Design System.

#### A. Dashboard Bento Grid (Metric Cards)
El diseño presenta cuatro tarjetas de indicadores visuales con marcas de agua ampliadas en un 50% (`225px`) para mejorar el impacto estético:
1. **Total Agencies**: Muestra el total de agencias registradas con su tendencia de crecimiento porcentual.
2. **Active Bookings**: Volumen actual de reservas de las agencias de viaje.
3. **Revenue (MTD)**: Ingresos generados en el mes en curso (Month-To-Date) formateado a moneda internacional.
4. **Compliance Rate**: Porcentaje promedio de cumplimiento global con indicador visual.

#### B. Barra de Filtros y Tabla Paginada
- Búsqueda integrada por texto que consulta el servidor de manera paginada.
- Desplegables interactivos para filtrar el listado en tiempo real por **Región** y **Estado**.
- Tabla que detalla las estadísticas individuales de cada agencia:
  - Iniciales generadas automáticamente en un avatar de color contrastante.
  - Barra de progreso interactiva para representar el volumen relativo de reservas.
  - Indicadores semáforo para los ratios de cumplimiento normativo.
  - Etiquetas dinámicas (Pills) de estado (`ACTIVE` en verde, `SUSPENDED` en rojo).
  - Acceso directo a la edición o eliminación de la agencia.

#### C. Modal de Reporte de Ranking de Rendimiento (View Ranking Report)
Un modal diseñado a medida con una estética premium oscura y cristalina (Glassmorphism), accesible mediante la barra superior. Contiene:
- **Resumen Estadístico**: Indicadores clave consolidados (Líder global, Promedio de reservas, Cumplimiento medio).
- **Análisis por Región**: Desglose interactivo que compara las regiones (EMEA, APAC, AMER) mostrando:
  - Número de agencias activas en cada zona.
  - Reservas totales de la región.
  - Ratio de cumplimiento promedio por territorio.
- **Leaderboard Global Interactivo**:
  - Clasificación de todas las agencias del sistema ordenadas inicialmente de mayor a menor volumen de reservas.
  - Cabeceras interactivas que permiten reordenar la tabla dinámicamente por **Nombre de Agencia**, **Reservas (30d)** o **Compliance (%)**.
  - Reconocimiento visual mediante medallas de **Oro, Plata y Bronce** en la columna de posición para el Top 3 de agencias con mejor desempeño.
  - Indicador de cumplimiento y barra de progreso de reservas en cada fila para facilitar el escaneo rápido de los resultados.
  - Botón de cierre en forma de "X" personalizado en color gris/rojo ubicado en la esquina superior derecha, manteniendo la interfaz despejada y limpia.

---

## 4. Flujo de Datos e Integración de Red

1. **Petición del Frontend**: El cliente de Angular (`HttpClient`) ejecuta peticiones hacia `http://localhost:8080/api/flights` o `http://localhost:8080/api/agencies`.
2. **Puerta de Enlace (API Gateway)**: Recibe la solicitud en el puerto `8080`. Evalúa el token JWT en las cabeceras. Si es válido, enruta la solicitud al microservicio destino registrado en el Discovery Server Eureka (`flight-service` o `agency-service`).
3. **Procesamiento de Negocio**:
   - `flight-service` consulta la base de datos `flight_db` en el puerto de base de datos correspondiente y retorna la lista de vuelos.
   - `agency-service` consulta `agency_db` para recuperar las agencias, paginar los resultados según los parámetros, o calcular las agregaciones de los reportes de ranking.
4. **Respuesta Estructurada**: Se devuelve la información en formato JSON hacia el cliente de Angular, el cual utiliza la estrategia de detección de cambios de Angular para renderizar instantáneamente los datos en pantalla sin parpadeos de carga.
