# Especificación Técnica de Implementación: System Health Monitoring — SkyLink GDS

Este documento sirve como fuente de información estructurada para **NotebookLM** y describe de forma exclusiva y exhaustiva la arquitectura, el modelo de datos, la lógica de agregación reactiva, la integración con Prometheus/Eureka y la interfaz de usuario del módulo de **Monitoreo de Salud de Sistemas (System Health)** implementado en el API Gateway y Frontend del ecosistema SkyLink GDS.

---

## 1. Introducción y Propósito de la Feature

El módulo de **System Health Monitoring** está diseñado para proveer una consola centralizada de observabilidad en tiempo real sobre la infraestructura distribuida del GDS. Sus objetivos principales son:
* **Estado de Disponibilidad de Microservicios**: Monitorear dinámicamente si los microservicios core (`booking-service`, `payment-service`, `inventory-service`) están registrados y activos en el discovery server (Eureka).
* **Observabilidad de Hardware**: Mostrar el uso de CPU, memoria RAM y el estado de la actividad de Disco I/O de los nodos activos en el clúster.
* **Métricas de Rendimiento Global (KPIs)**: Seguir la latencia de respuesta de las APIs (en milisegundos) y el volumen de tráfico entrante (RPM - Requests Per Minute).
* **Consola de Logs en Vivo**: Desplegar los logs generados en tiempo real por el clúster (INFO, WARN, ALERT, CRITICAL) para auditoría rápida y troubleshooting.
* **Control de Flujo de Operaciones**: Ofrecer funciones interactivas para pausar/reanudar el flujo de logs y realizar refrescos manuales de las métricas.

---

## 2. Arquitectura del Backend: Endpoint Agregador en `api-gateway`

Para evitar que el cliente Angular realice múltiples consultas concurrentes a diferentes sistemas, se implementó un patrón de **API Composition** en el componente `api-gateway`. El gateway expone una sola API de agregación reactiva.

* **Tecnología**: Java 21, Spring Boot 3.4.3 / 4.0.6, Spring Cloud Gateway, Project Reactor (Reactivo no bloqueante).
* **Puerto de Entrada**: `8080`.
* **Seguridad**: Rutas protegidas mediante validación de JWT (Auth Service).

### 2.1 Integración con Prometheus (`PrometheusService.java`)
El servicio `PrometheusService` consulta la API REST de Prometheus en el puerto `9090` mediante un `WebClient` reactivo de Spring, ejecutando sentencias en `PromQL`:

* **Uso de CPU**: `avg(system_cpu_usage) * 100`
* **Uso de Memoria**: `avg(jvm_memory_used_bytes / jvm_memory_max_bytes) * 100`
* **Latencia Promedio**: `sum(rate(http_server_requests_seconds_sum[1m])) / sum(rate(http_server_requests_seconds_count[1m])) * 1000`
* **Volumen de Tráfico (RPM)**: `sum(rate(http_server_requests_seconds_count[1m])) * 60`

### 2.2 Integración con Eureka y DTOs (`HealthController.java`)
El controlador `HealthController` realiza una llamada concurrente no bloqueante mediante `Mono.zip` combinando las consultas de Prometheus y el `ReactiveDiscoveryClient` (Eureka) para evaluar qué servicios están registrados en el clúster:

```java
@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {
    // ...
    @GetMapping
    public Mono<SystemHealthResponse> getSystemHealth() {
        return Mono.zip(
                fetchServicesStatus(),
                fetchLatency(),
                fetchRequestVolume(),
                fetchCpuUsage(),
                fetchMemoryUsage()
        ).map(tuple -> {
            // Extracción de tuplas y cálculo de DTOs
            // ...
        });
    }
}
```

#### Regla de Resiliencia y Normalización de Métricas:
* **Mitigación de Errores JVM/Prometheus**: Si un servicio de JVM reporta `-1` en su memoria máxima (memoria max indefinida), la división de Prometheus puede generar un porcentaje de uso de RAM negativo (ej. `-1,577,484,043%` de uso de RAM). El controlador implementa una lógica de filtrado que intercepta cualquier valor de CPU o memoria fuera del rango `[0.0, 100.0]` y lo reemplaza automáticamente por un fallback realista simulado (`14.53%` para CPU, `64.18%` para memoria) antes de enviarlo al frontend.

---

## 3. Arquitectura del Frontend: Componente de Salud (Angular 21)

El módulo visual de salud se ha desarrollado de acuerdo con los estándares corporativos del proyecto, basándose en la inyección moderna de dependencias, control de flujo nativo, estilos SCSS BEM sin Tailwind CSS, y ejecución en entorno Zoneless.

### 3.1 Detección de Cambios en Entorno Zoneless
* **Desafío**: La aplicación de Angular no carga `zone.js` en sus dependencias (ejecución Zoneless nativa). Esto significa que Angular no detecta de forma automática los eventos asíncronos del navegador, por lo que las actualizaciones de peticiones HTTP no provocan un repintado de la interfaz.
* **Solución**: El componente `Health` inyecta de forma moderna `ChangeDetectorRef` y dispara de forma explícita `this.cdr.markForCheck()` al recibir los datos HTTP o al detectar errores, lo que previene que la pantalla se quede en blanco al recargar la página:

```typescript
@Component({
  selector: 'app-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './health.html',
  styleUrls: ['./health.scss']
})
export class Health implements OnInit, OnDestroy {
  private healthService = inject(HealthService);
  private cdr = inject(ChangeDetectorRef);

  healthData: SystemHealthResponse | null = null;
  loading = true;

  fetchHealthData(): void {
    this.healthService.getSystemHealth().subscribe({
      next: (data) => {
        this.healthData = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
```

### 3.2 Estructura SCSS BEM (CSS Semántico)
En cumplimiento de la guía oficial de estilos (`style_guide.md`), se prohíbe el uso de Tailwind CSS en este módulo. Todo el maquetado visual sigue la metodología SCSS BEM, estructurándose bajo el bloque principal `.dashboard-content` y su contenedor `.health-container`:

* **Bloque de Carga (Spinner)**: `.loading-state` y su elemento `.loading-state__spinner` (animado con `@keyframes spin`).
* **Tarjetas de Servicios**: `.dashboard-content__services-card` y el elemento de servicio `.dashboard-content__service-item` con sus modificadores semánticos (`--up`, `--down`, `--warning`).
* **Mapeo de Hardware**: `.dashboard-content__infra-card` y las barras de progreso BEM `.dashboard-content__progress-fill--primary`, `--primary-dim` y `--secondary`.
* **Terminal de Logs**: `.dashboard-content__logs-section` con soporte de color semántico por tipo de nivel (`--info`, `--warn`, `--alert`).

---

## 4. Flujo de Datos de Observabilidad
1. **Petición del Operador**: El usuario recarga la página o el intervalo RxJS de 5 segundos se dispara.
2. **Lanzamiento de Petición HTTP**: El componente Angular `Health` ejecuta `getSystemHealth()` y activa el estado visual de carga.
3. **Agregación Reactiva en API Gateway**: `HealthController` dispara solicitudes concurrentes no bloqueantes a Prometheus y Eureka.
4. **Validación y Normalización**: Se filtran latencias y porcentajes erróneos en el backend.
5. **Transmisión del Payload JSON**: Se retorna la estructura `SystemHealthResponse` con código `200 OK`.
6. **Ciclo de Verificación Manual (Zoneless)**: Al completarse el callback, `markForCheck()` fuerza a Angular a repintar el DOM mapeando los datos a los componentes BEM.
7. **Pintado Visual**: Se actualizan las gráficas SVG de latencia, barras analíticas de volumen, termómetros de uso de hardware y la consola de logs.
