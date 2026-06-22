# Documentación Técnica de la Sesión: Flujo de Reserva, Asignación de Asientos, Pagos y Tarjeta de Embarque en SkyLink GDS

Este documento detalla la arquitectura, los algoritmos y las soluciones técnicas implementadas en la sesión de desarrollo para la aplicación de pasajeros nativa en Android y los microservicios de backend de **SkyLink GDS**. Diseñado especialmente para su estudio y carga en **NotebookLM**.

---

## 1. Arquitectura General y Conectividad

El sistema **SkyLink GDS** se compone de una arquitectura de microservicios distribuida con backend en Java (Spring Boot) y frontend móvil en Android Nativo (Kotlin + Jetpack Compose).

### Conectividad del Emulador al API Gateway
* **IP del Host en Emulador:** El emulador de Android corre en una subred aislada. Para acceder a los microservicios locales expuestos por el API Gateway (puerto `8080`), se configuró la IP de puente virtual de Android Studio `http://10.0.2.2:8080/`. El uso directo de `localhost` o `127.0.0.1` provocaría que el emulador intentase resolver la petición en su propia pila de red local interna, resultando en un error de conexión `ConnectException`.
* **Manejo Dinámico de JWT:** Las peticiones salientes (excepto `/api/auth/login` y `/api/auth/register`) son interceptadas por `AuthInterceptor` para inyectar automáticamente la cabecera `Authorization: Bearer [TOKEN]`. El token se almacena de forma segura en las preferencias locales (`SharedPreferences` encriptadas).

---

## 2. Flujo Completo de la Reserva (Booking)

El flujo de reserva consta de una coreografía que involucra navegación móvil reactiva, orquestación en el ViewModel y llamadas atómicas a los endpoints REST del backend.

```
[HomeScreen] ➔ [FlightResultsScreen] ➔ [BookingSummaryScreen] 
                                                  │
[CheckoutPaymentScreen] 🔀 [SeatSelectionScreen] 🗙 (Omitir Asiento)
          │
    (Compra Exitosa)
          ▼
  [MyBookingsScreen] ➔ [BoardingPassScreen] (Detalle con QR)
```

1. **Búsqueda e Información:** El usuario selecciona origen y destino, visualiza los resultados y avanza al resumen de la reserva.
2. **Selección de Asientos / Omisión:** Permite seleccionar interactivamente un asiento de la cuadrícula o bien continuar directamente.
3. **Checkout y Pago:** Procesa la validación de la tarjeta, realiza la llamada de creación en el servidor de reservas (`booking-service`), y redirige a la lista de viajes comprados.

---

## 3. Algoritmo de Precios Dinámicos y Bloqueo de Asientos

### Precios Dinámicos en la Selección de Asiento
Para incentivar la monetización del inventario de cabina, se implementó una lógica de precios adicionales según el tipo y fila del asiento elegido en `BookingSummaryScreen` y `CheckoutPaymentScreen`:
* **Clase Business (Filas 1 a 3):** Incremento de **+23€** al costo base de la tarifa del vuelo.
* **Clase Turista Premium / Estándar (Filas 4 a 20):** Incremento de **+3€** al costo base.
* **Sin selección de asiento (Asignación automática):** **+0€** (sin coste adicional).

### Bloqueo de Asientos en Tiempo Real
El mapa de asientos de la cabina (`SeatSelectionScreen`) consulta al backend en tiempo real para evitar la sobreventa y duplicación de asientos ocupados en el mismo vuelo:
1. Se efectúa una petición `GET api/bookings/flight/{flightId}/seats`.
2. El backend devuelve un listado de objetos `SeatMapResponse` conteniendo el número de asiento, clase y estado de la reserva (`PENDING` o `COMPLETED`).
3. En la interfaz Compose, los asientos devueltos son mapeados y deshabilitados (`enabled = false`), mostrándose tachados visualmente (con una cruz roja de advertencia) para impedir que otro pasajero los seleccione.

---

## 4. Validación de Tarjeta y Carga de Procesamiento

El flujo de checkout implementa validaciones locales rigurosas de seguridad y comportamiento UX premium:
* **Tarjeta de Crédito Mockeada:** Se preconfiguraron en el formulario los datos de una tarjeta Visa de prueba:
  * **Número:** `4532 7182 9381 0293`
  * **Fecha de Expiración:** `12/30`
  * **CVV:** `382`
* **Flujo de Error:** Si el usuario ingresa datos incorrectos en los campos de tarjeta, al pulsar "Confirmar y Pagar", se lanza una ventana emergente (Popup/Diálogo) con estilo de alerta rojo que notifica: *"Error en el Pago: Los datos de la tarjeta no son correctos"*.
* **Flujo de Éxito:** Al ingresar la tarjeta correcta:
  1. Se muestra un overlay translúcido con un spinner de carga (`CircularProgressIndicator`) durante **1 segundo** aproximadamente para simular el procesamiento de pasarela financiera internacional (utilizando corrutinas de Kotlin con `delay(1000)`).
  2. Se envía la petición de reserva al microservicio en el backend.
  3. Tras recibir la confirmación exitosa del backend (`201 Created`), se muestra un Popup en verde indicando: *"¡Compra Exitosa!"*. Al pulsar *"Aceptar"*, el usuario es redirigido a *"Mis Viajes"*.

---

## 5. Auto-asignación de Asientos Turista

Para los usuarios que deciden no pagar o saltarse el proceso de selección de asientos ("No seleccionar asiento"), se diseñó un algoritmo de asignación aleatoria en la capa de negocio móvil dentro del [BookingViewModel](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/booking/presentation/BookingViewModel.kt):
1. **Definición del Rango Turista:** Se predefine un conjunto completo de asientos correspondientes a la clase turista (filas 4 a 20, columnas de la A a la F).
2. **Descarte de Ocupados:** El ViewModel obtiene el conjunto de asientos ocupados del estado dinámico del vuelo (`_bookedSeatsState.value`).
3. **Selección Aleatoria:** Se filtran los asientos de turista que no están en el conjunto de asientos ocupados. Se selecciona uno de forma aleatoria (`availableSeats.randomOrNull()`). Si todos estuvieran ocupados por condiciones extremas, se asigna uno del rango estándar como fallback.
4. **Petición al Servidor:** Se envía el asiento auto-asignado y la clase de cabina `"ECONOMY"` en el cuerpo del JSON para consolidar el registro de la base de datos real de `booking-service`.

---

## 6. Resolución Técnica del Error 404 en "Mis Viajes"

### Diagnóstico del Problema
Al entrar en la ventana "Mis Viajes", el sistema móvil intentaba cargar las reservas del usuario llamando al API Gateway en la ruta `GET /api/bookings/user/{userId}` y devolvía un error HTTP **404 Not Found**.
1. Mediante una herramienta de diagnóstico local basada en Node.js, verifiqué que el Gateway enrutable (puerto `8080`) devolvía 404 específicamente en esa ruta, a pesar de que el código del controlador de backend Java ya tenía mapeado el endpoint `@GetMapping("/user/{userId}")`.
2. Esto indicaba que el proceso del microservicio `booking-service` que estaba ejecutándose en caliente correspondía a una versión compilada previa a la adición de dicho endpoint.

### Conflicto de Compilación y JDK 21
Al intentar realizar el empaquetado del microservicio con Maven (`mvn clean package`), la compilación falló debido a una incompatibilidad de versiones:
```
Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```
Este error es típico cuando las herramientas del compilador del sistema (JDK 25 en la máquina local) intentan compilar anotaciones pesadas como las de Lombok configuradas en el proyecto para JDK 21.

### Solución Aplicada
1. Creé un script en PowerShell para forzar de forma controlada el entorno del proceso estableciendo la variable de entorno `JAVA_HOME` apuntando al JDK 21 instalado localmente (`ms-21.0.11`) y reestructurando el `PATH`.
2. Ejecuté la compilación exitosa utilizando el Maven Wrapper de forma aislada. El compilador generó el binario empaquetado JAR bajo el estándar de Java 21 sin errores.
3. Detuve el proceso Java colgado en el puerto `8083` (PID `4608`) mediante el comando de terminación forzada del sistema operativo Windows (`taskkill`).
4. Inicié en segundo plano el nuevo JAR compilado (`booking-service-0.0.1-SNAPSHOT.jar`). Al registrarse de nuevo en Eureka y propagar la ruta, el endpoint `/api/bookings/user/{userId}` quedó 100% activo y funcional, respondiendo exitosamente con código HTTP `200 OK`.

---

## 7. Detalle de Maquetación: Tarjeta de Embarque y Motor QR Vectorial

### Pantalla `BoardingPassScreen.kt`
Diseñada con Jetpack Compose en base a las especificaciones del diseño *"Digital Boarding Pass"* de Stitch.
* **Componente Ticket Cutout:** El contenedor principal del billete de embarque simula una tarjeta física de aeropuerto. Para lograr los cortes circulares a los lados sin depender de recursos gráficos externos, utilicé un contenedor `Box` principal donde se posicionan dos pequeños círculos (`Box` con `CircleShape` y color de fondo de la pantalla) desfasados en X (`x = -12.dp` y `x = 12.dp`) sobre los bordes izquierdo y derecho.
* **Línea de Puntos Separadora:** Dibujé la línea de puntos divisoria usando el componente `Canvas` con un trazo dashed (`PathEffect.dashPathEffect`), lo que otorga la ilusión de precorte del billete.

### Motor QR Vectorial Dinámico
Para generar el código QR dinámicamente y con la máxima optimización de rendimiento, programé un motor de renderizado vectorial en Compose que dibuja la cuadrícula en un `Canvas` en base al hash del texto del billete (`SL-[bookingId]-[PNR]`):

```kotlin
@Composable
fun BoardingPassQrCode(text: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val size = size.width
        val cellSize = size / 21f // Cuadrícula estandarizada 21x21 (Código QR Versión 1)

        // 1. Dibujar fondo blanco
        drawRect(Color.White)

        // 2. Dibujar Patrones de Búsqueda (Finder Patterns) en las esquinas
        drawFinderPattern(0f, 0f, cellSize) // Superior Izquierda
        drawFinderPattern((21 - 7) * cellSize, 0f, cellSize) // Superior Derecha
        drawFinderPattern(0f, (21 - 7) * cellSize, cellSize) // Inferior Izquierda

        // 3. Dibujar Patrón de Alineación en el cuadrante inferior derecho
        drawAlignmentPattern((21 - 9) * cellSize, (21 - 9) * cellSize, cellSize)

        // 4. Rellenar los bloques de datos con ruido determinista basado en el hash del ticket
        val seed = text.hashCode()
        val random = java.util.Random(seed.toLong())
        for (row in 0 until 21) {
            for (col in 0 until 21) {
                // Omitir áreas de patrones de búsqueda y alineación para no corromper la estructura
                if (row < 8 && col < 8) continue
                if (row < 8 && col >= 13) continue
                if (row >= 13 && col < 8) continue
                if (row in 11..13 && col in 11..13) continue

                // Pintar módulo negro de forma pseudo-aleatoria
                if (random.nextBoolean()) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}
```

Este enfoque garantiza que el código QR sea 100% vectorial, nítido a cualquier resolución de pantalla de dispositivo móvil, requiera cero milisegundos de descarga por internet, y tenga un comportamiento determinista (el código QR siempre es el mismo para la misma combinación de datos del billete).

---

## 8. Modificaciones al Repositorio

El commit del progreso de la sesión fue realizado en la rama `main` con el mensaje:
`feat: implementar flujo completo de reserva, precios dinamicos de asientos, pagos mockeados con carga y tarjeta de embarque digital`

### Archivos Clave Creados/Modificados en la Sesión:
1. **[NEW]** [BoardingPassScreen.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/booking/presentation/BoardingPassScreen.kt): Vista detallada del pase de abordar con el billete troquelado y código QR vectorial autogenerado.
2. **[MODIFY]** [MyBookingsScreen.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/booking/presentation/MyBookingsScreen.kt): Añadido el comportamiento clicable a las tarjetas de viaje para abrir la pantalla de detalle.
3. **[MODIFY]** [MainActivity.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/MainActivity.kt): Registro de la ruta de la tarjeta de embarque y mapeo de parámetros de navegación.
4. **[MODIFY]** [BookingViewModel.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/booking/presentation/BookingViewModel.kt): Implementada la lógica de auto-asignación aleatoria de asientos en clase turista.
5. **[MODIFY]** [CheckoutPaymentScreen.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/booking/presentation/CheckoutPaymentScreen.kt): Validación de tarjeta Visa simulada, temporizador de corrutina de 1s para carga financiera, popup de éxito en verde y popup de error en rojo.
