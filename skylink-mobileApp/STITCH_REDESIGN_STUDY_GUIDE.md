# Study Guide: Android Passenger App Redesign (Stitch Match)

This document is a comprehensive guide to the modifications, architecture, design systems, and troubleshooting steps applied during the passenger application redesign for **SkyLink GDS**. You can upload this document to **NotebookLM** for studying, reviewing, and prompting.

---

## 1. Context & Objectives

The goal was to align the Android Passenger App's visual experience with the designs created in **Stitch** for:
1. **Login Screen ("Login de Usuario"):** Fixing typographies to use `Plus Jakarta Sans`, adding high-quality vector icons for Google and Apple social sign-in buttons, and matching layout dimensions/paddings.
2. **Home Screen ("Flight Search & Explore"):** Re-building the entire screen in Jetpack Compose to match the detailed, premium bento-style design from the Stitch dashboard.

All components adhere to the Material Design 3 (Material You) Expressive Palette defined in `DESIGN.md`.

---

## 2. Fixing Downloadable Fonts (`Plus Jakarta Sans`)

### The Problem
The app uses downloadable fonts via Google Font Provider. However, the custom fonts were not rendering on the emulator. 
The configuration in `Type.kt` referenced `R.array.com_google_android_gms_fonts_certs` which was originally missing. We added `font_certs.xml`, but it contained mock placeholders (`MOCK_DEV_CERTIFICATE_HASH`). At runtime, Android's font manager validates the provider's signature against these hashes. Because the mock hashes did not match the actual Google Play Services signing certificates, the font downloads were rejected, falling back to system sans-serif.

### The Solution
We updated `font_certs.xml` with Google's official developer (debug) and production (release) certificate hashes:
- **Location:** [font_certs.xml](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/res/values/font_certs.xml)
- **Implementation:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <array name="com_google_android_gms_fonts_certs">
        <item>@array/com_google_android_gms_fonts_certs_dev</item>
        <item>@array/com_google_android_gms_fonts_certs_prod</item>
    </array>
    <string-array name="com_google_android_gms_fonts_certs_dev">
        <item>MIIEqDCCA5CgAwIBAgIJANWFuGx90071MA0GCSqGSIb3...[Dev Hash]...oGChZxmQ+nBli+gwYMzM1vAkP+aayLe0a1EQimlOalO762r0GXO0ks+UeXde2Z4e+8S/pf7pITEI/tP+MxJTALw9QUWEv9lKTk+jkbqxbsh8nfBUapfKqYn0eidpwq2AzVp3juYl7//fKnaPhJD9gs=</item>
    </string-array>
    <string-array name="com_google_android_gms_fonts_certs_prod">
        <item>MIIEQzCCAyugAwIBAgIJAMLgh0ZkSjCNMA0GCSqGSIb3...[Prod Hash]...w0lLO74UwLDYKqs6Tm8/yzKkEu116FmH4rkaymUIE0P9KaMftGlMexFlaYjzmB2OxZyl6euNXEsQH8gjwyxCUKRJNexBiGcCEyj6z+a1fuHHvkiaai+KL8W1EyNmgjmyy8AW7P+LLlkR+ho5zEHatRbM/YAnqGcFh5iZBqpknHf1SKMXFh4dd239FJ1jWYfbMDMy3NS5CTMQ2XFI1MvcyUTdZPErjQfTbQe3aDQsQcafEQPD+nqActifKZ0Np0IS9L9kR/wbNvyz6ENwPiTrjV2KRkEjH78ZMcUQXg0L3BYHJ3lc69Vs5Ddf9uUGGMYldX3WfMBEmh/9iFBDAaTCK</item>
    </string-array>
</resources>
```
With these verified hashes, Android Studio fetches and caches `Plus Jakarta Sans` successfully, rendering the custom typography on all text views styled with `MaterialTheme.typography`.

---

## 3. Social Sign-in Buttons & Vector Assets

### The Problem
The social button icons (Google and Apple) in `LoginScreen.kt` were originally drawn using Android's `Canvas` class. The shapes looked crude and did not match the official brand assets.

### The Solution
1. **Asset Creation:**
   - Google already had its vector asset at `ic_google.xml`.
   - We created [ic_apple.xml](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/res/drawable/ic_apple.xml) inside `res/drawable/` containing the official Apple SVG path coordinates in Android's Vector XML format.
2. **Implementation:**
   - We refactored `SocialButton` to load these resources using `painterResource(id = R.drawable.ic_...)` and passed them directly to the `Icon` composable.
   - Deleted the complex, legacy canvas-drawn `GoogleLogo` and `AppleLogo` composables, keeping the codebase clean.

---

## 4. Redesigning the Login Screen ("Login de Usuario")

We refactored [LoginScreen.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/auth/presentation/LoginScreen.kt) to match the Stitch HTML structure exactly:
- **Title Block:** Standardized spacing, sizes, and alignments. Incorporated the `FlightTakeoff` icon and `SkyLink` logo using `FontWeight.SemiBold`.
- **Text Hierarchy:** Welcome messages and description paragraphs were mapped to their corresponding typography styles (`headlineMedium` and `bodyLarge`).
- **Form Inputs:**
  - Standardized on **Material Design 3 Filled style TextFields**.
  - Enabled animations where the placeholder text floats to the top when the field receives focus.
  - Set the corner radius to `16.dp` on the top edges (matching the medium component specifications in `DESIGN.md`).
- **Action Buttons:**
  - Styled the primary "Sign In" button with `16.dp` rounded corners and height of `56.dp`.
  - Configured the Google and Apple buttons with a white surface background, a subtle border (`outlineVariant`), and `16.dp` rounded corners.

---

## 5. Rebuilding the Home Screen ("Flight Search & Explore")

The screen was built from scratch in [HomeScreen.kt](file:///d:/Proyectos/airlines-gds-system/skylink-mobileApp/app/src/main/java/com/alalodev/skylink/features/home/presentation/HomeScreen.kt) matching the layout of the Stitch "Flight Search & Explore" screen:

### Dynamic Image Loading with Coil
Since the design relies on high-resolution, public travel destination images, we added the **Coil Compose** library (`implementation("io.coil-kt:coil-compose:2.6.0")`) to `app/build.gradle.kts`. This allows asynchronous network image loading using the `AsyncImage` composable.

### Components Structure
1. **Custom TopAppBar:** Displays the flight takeoff brand logo, "SkyLink" title, and a profile avatar.
2. **Hero Section:**
   - Features a full-width container showcasing a coastline background image.
   - Applies a vertical gradient overlay (`Brush.verticalGradient`) to transition from transparent to the base surface color to ensure high legibility of the foreground components.
3. **Bento Flight Search Card:**
   - **Trip Selector:** Switcher for Round Trip / One Way styled as chips.
   - **Search Inputs:** "From", "To", "Dates", and "Travelers" are styled using custom read-only `TextField`s with animatable labels and trailing icons. Each occupies its own full-width row to ensure clean vertical alignment.
   - **Airport Swap Button:** A circular button with `Icons.Default.SwapVert` positioned centered between "From" and "To".
   - **Action Button:** A fully pill-shaped search button.
4. **Quick Services Bento Grid:**
   - A 2x2 grid representing *Manage Trip*, *Check-In*, *Flight Status*, and *Help Center*.
   - Each card features a circular icon wrapper using the secondary brand container color (`secondaryContainer` at 20% opacity) and modern icons.
5. **Explore Destinations Carousel:**
   - Uses a horizontal `LazyRow` to scroll through cards representing Tokyo, the Swiss Alps, and Rome.
   - Each card applies a semantic gradient overlay utilizing colors from the palette (`tertiary`, `primary`, and `secondary` at 85% opacity at the bottom) to make text stand out.
6. **BottomNavigationBar:**
   - A responsive bar holding active and inactive states for Search, My Trips, Alerts, and Profile.

---

## 6. Development & Build Troubleshooting

### Compilation Errors (JDK Serialization & jlink)
- **Problem:** Running `./gradlew assembleDebug` failed with errors pointing to a missing `jlink` executable inside an old IDE extension folder.
- **Root Cause:** Gradle was using a cached/configured JDK path from a previously uninstalled extension directory.
- **Solution:** 
  1. We located the reliable, bundled **JetBrains Runtime (JBR)** JDK that comes with Android Studio at:
     `C:\Program Files\Android\Android Studio\jbr`
  2. We stopped the active Gradle daemons running on the incorrect JVM:
     ```powershell
     ./gradlew --stop
     ```
  3. We rebuilt and installed the app by explicitly setting the `JAVA_HOME` environment variable:
     ```powershell
     $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
     ./gradlew installDebug
     ```

### Deploying & Launching on the Emulator
We verified that the connected emulator `emulator-5554` (Pixel 8) was online using `adb devices`, and ran the following command to deploy and start the application:
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; ./gradlew installDebug; & "C:\Users\Anton\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.alalodev.skylink/com.alalodev.skylink.MainActivity
```

---

## 7. Key Architecture & Clean Code Compliance
- **Compose Only:** Views do not use XML templates or data binding. Everything is composed of functional declarative views.
- **BEM & Token Styling:** Paddings, spacing, colors, and shapes directly reference the tokens defined in `DESIGN.md` (e.g. `16.dp` padding for mobile margins, `24.dp` for card rounded corners).
- **AutoMirrored Icons:** Handled deprecations by replacing standard icons with auto-mirrored variants (e.g. `Icons.AutoMirrored.Filled.ArrowForward` and `Icons.AutoMirrored.Filled.AirplaneTicket`).
- **Hardware Enveloped Responses:** Kept code compliant with backend GDS guidelines for future JWT authentication and network interceptor integration.
