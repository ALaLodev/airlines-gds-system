# SkyLink Pasajeros — Documentación Técnica de Registro y Autenticación (para NotebookLM)

Este documento detalla la arquitectura, las dependencias y la configuración de la pantalla de **Registro de Usuario** y el sistema de **Autenticación Social (Google/Firebase)** implementado en la aplicación móvil de pasajeros de **SkyLink GDS**. Está diseñado para ser procesado por NotebookLM para servir de base de conocimiento del proyecto.

---

## 1. Mapeo con el Backend GDS (Spring Boot)

El módulo de registro conecta la aplicación móvil con el microservicio `auth-service` del backend mediante la API Gateway (puerto `8080`).

### Entidad de Base de Datos (`User`)
La tabla de base de datos MySQL correspondiente en el backend tiene la siguiente estructura de campos:
* **`id`** (`long`, Auto-generado)
* **`email`** (`String`, Único, No nulo)
* **`password`** (`String`, Encriptado con BCrypt, No nulo)
* **`role`** (`Role` enum: `ROLE_CUSTOMER`, `ROLE_AGENCY`, `ROLE_ADMIN`)

> [!NOTE]
> El diseño visual de la interfaz de registro de pasajeros incluye un campo de **Nombre Completo (Full Name)**. Dado que la entidad `User` del backend no almacena este campo actualmente, la app lo muestra como campo visual del formulario pero **no lo envía** a la base de datos para mantener compatibilidad con las llamadas del backend.

### Estructura de Petición y Respuesta (DTOs Planos)
Se eliminaron envoltorios innecesarios (envelopes) dado que el backend devuelve objetos JSON planos directamente:

#### `RegisterRequest` (Enviado a `POST /api/auth/register`)
```json
{
  "email": "usuario@ejemplo.com",
  "password": "miPasswordSeguro123",
  "role": "ROLE_CUSTOMER"
}
```
*El rol es hardcodeado automáticamente como `"ROLE_CUSTOMER"` para todos los registros realizados desde la aplicación móvil de pasajeros.*

#### `RegisterResponse` / `AuthResponse` (Recibido desde el servidor)
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "message": "Usuario creado con éxito en el sistema GDS"
}
```

---

## 2. Arquitectura de Código Móvil (MVVM + Clean Architecture)

La implementación sigue de forma estricta las capas desacopladas del proyecto:

### Capa de Datos (Data Layer)
* **`AuthApi.kt`**: Define los endpoints de Retrofit para llamadas HTTP.
* **`AuthRepository.kt`**: Define el contrato del repositorio.
* **`AuthRepositoryImpl.kt`**: Implementa la lógica de red. Administra las respuestas HTTP y almacena de forma segura el token JWT en las `EncryptedSharedPreferences` del dispositivo tras un registro o login exitoso.

### Capa de Dominio (Domain Layer)
* **`RegisterUseCase.kt`**: Caso de uso que encapsula la lógica de negocio para crear una nueva cuenta con correo y contraseña.
* **`SignInWithGoogleUseCase.kt`**: Caso de uso encargado de gestionar el flujo de autenticación mediante el token de proveedor de Google.

### Capa de Presentación (Ui Layer)
* **`AuthState.kt`**: Representa el estado inmutable de la pantalla (cargando, éxito del registro, error devuelto).
* **`AuthViewModel.kt`**: ViewModel de Dagger Hilt que procesa las interacciones de UI de forma asíncrona mediante Corrutinas y expone el estado reactivo mediante `StateFlow`.
* **`RegisterScreen.kt`**: Interfaz declarativa en **Jetpack Compose** que dibuja el formulario adaptado al diseño de Stitch, maneja validaciones locales (campos vacíos, formato de correo, contraseñas coincidentes) y reacciona a los cambios de estado del ViewModel.

---

## 3. Integración de Firebase y Google Sign-In

Para permitir el inicio de sesión y registro social, se ha configurado la infraestructura básica de Firebase:

### Dependencias de Gradle
Se añadieron las siguientes librerías en `app/build.gradle.kts`:
* **Plugin de Google Services**: `com.google.gms.google-services`
* **Firebase BOM**: Plataforma para control de versiones compatible de Firebase.
* **Firebase Auth**: SDK de autenticación (`firebase-auth-ktx`).
* **Play Services Auth**: SDK para inicio de sesión de Google (`play-services-auth`).

### Flujo de Autenticación de Google
1. La UI lanza el Intent de inicio de sesión de Google a través de `rememberLauncherForActivityResult`.
2. El usuario selecciona su cuenta y la app obtiene un **ID Token** de Google.
3. El repositorio usa este ID Token para autenticar la sesión en **Firebase Auth**.
4. Tras validarse con Firebase, la app extrae el correo electrónico del usuario y lo registra en el backend de SkyLink GDS bajo una contraseña social segura determinista. Si el usuario ya estaba registrado en el backend, inicia sesión de forma transparente.
5. Si la app se ejecuta sin una configuración real de Firebase, entra en un **Mock Fallback** que genera un token simulado para permitir pruebas completas de UI y navegación en entornos de desarrollo locales.

---

## 4. Políticas de Seguridad de Red (Tráfico Cleartext / HTTP)

Por defecto, Android (a partir de la versión 9) bloquea la comunicación en texto plano (`http://`). Debido a que el backend de GDS se ejecuta localmente y se accede desde el emulador mediante la dirección `http://10.0.2.2:8080/`, fue necesario configurar una excepción de seguridad:

### `network_security_config.xml`
Se creó un archivo de configuración de red en los recursos XML de la app:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```
Este archivo se enlazó en el manifiesto (`AndroidManifest.xml`) mediante el atributo `android:networkSecurityConfig`. Esto garantiza que la app pueda comunicarse de manera local mediante HTTP únicamente con el emulador y localhost, manteniendo el bloqueo para dominios externos en producción.
