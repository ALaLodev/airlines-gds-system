# SkyLink GDS - Guía Oficial de Estilos del Sistema de Diseño (B2B SaaS)

Esta guía documenta los componentes visuales, tipografías y la paleta de colores institucional del ecosistema frontend de SkyLink GDS. Toda hoja de estilo `.scss` generada para el proyecto debe cumplir rigurosamente con esta especificación.

## 1. Paleta de Colores Semántica (Tokens SCSS)

Las siguientes variables representan la identidad visual de la marca y la gestión de estados del Back-Office. Se priorizan los fondos claros con un alto contraste en las tipografías para mitigar la fatiga visual del operador.

```scss
// ==========================================================================
// Colores de Lienzo, Superficies y Capas (Material Design 3 Scale)
// ==========================================================================
$bg-canvas: #fcf8ff;                  // Fondo base de toda la aplicación (Body background)
$surface-card: #ffffff;               // Fondo puro de tarjetas independientes, tablas y módulos analíticos
$surface-container-low: #f6f1ff;       // Contenedores sutiles de baja prioridad (ej. fondo de cabeceras de tabla o perfil)
$surface-container: #f0ebff;           // Fondos inactivos, componentes de entrada de datos o menús laterales
$surface-container-high: #ebe5ff;      // Separadores rígidos, bordes destacados y botones secundarios inactivos

// ==========================================================================
// Tipografía, Textos y Contraste
// ==========================================================================
$on-surface: #322d56;                 // Texto principal de alta prioridad, títulos h1/h2 y códigos PNR
$on-surface-variant: #5f5a86;         // Texto secundario, descripciones cortas, migas de pan y etiquetas de formulario

// ==========================================================================
// Bordes, Líneas de Guía y Separadores
// ==========================================================================
$outline-variant: #b3acde;             // Borde perimetral fino estándar de 1px para tarjetas e inputs

// ==========================================================================
// Identidad de Marca (Brand Primary Colors)
// ==========================================================================
$primary-indigo: #4f57aa;             // Color maestro de la marca. Aplicado en botones activos, texto PNR y enlaces focales
$primary-dim: #424b9d;                // Tono oscurecido para transiciones de estados reactivos (:hover, :active)
$on-primary: #fbf8ff;                 // Contraste de texto de alta legibilidad sobre fondos oscuros o primarios

// ==========================================================================
// Estados Semánticos Operacionales (Alertas y Confirmaciones)
// ==========================================================================
$success: #6c7f1b;                    // Éxito operacional (Estados COMPLETED, Sesión 2FA Segura, Transacción exitosa)
$success-bg: #e9f7c2;                 // Fondo sutil con 10% de opacidad para píldoras indicadoras de éxito
$error: #ac3149;                      // Acciones destructivas (Estados CANCELLED, Botón de Logout, Errores HTTP Críticos)
$error-bg: rgba(172, 49, 73, 0.1);    // Fondo tintado sutil para destacar incidencias operacionales