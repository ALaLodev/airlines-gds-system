---
name: Aviation Consumer Experience
colors:
  surface: '#fcf8ff'
  surface-dim: '#dcd6f4'
  surface-bright: '#fcf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f1ff'
  surface-container: '#f0ebff'
  surface-container-high: '#ebe5ff'
  surface-container-highest: '#e5dffd'
  on-surface: '#1b192e'
  on-surface-variant: '#464651'
  inverse-surface: '#302e44'
  inverse-on-surface: '#f3eeff'
  outline: '#767682'
  outline-variant: '#c6c5d3'
  surface-tint: '#4e57a9'
  primary: '#4b54a6'
  on-primary: '#ffffff'
  primary-container: '#646dc1'
  on-primary-container: '#fffbff'
  inverse-primary: '#bdc2ff'
  secondary: '#57633a'
  on-secondary: '#ffffff'
  secondary-container: '#d8e5b2'
  on-secondary-container: '#5c673e'
  tertiary: '#526300'
  on-tertiary: '#ffffff'
  tertiary-container: '#697c18'
  on-tertiary-container: '#fcffe2'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dfe0ff'
  primary-fixed-dim: '#bdc2ff'
  on-primary-fixed: '#010965'
  on-primary-fixed-variant: '#353e8f'
  secondary-fixed: '#dbe8b5'
  secondary-fixed-dim: '#bfcc9b'
  on-secondary-fixed: '#151f01'
  on-secondary-fixed-variant: '#404b25'
  tertiary-fixed: '#d6ed7d'
  tertiary-fixed-dim: '#bad064'
  on-tertiary-fixed: '#181e00'
  on-tertiary-fixed-variant: '#3f4c00'
  background: '#fcf8ff'
  on-background: '#1b192e'
  surface-variant: '#e5dffd'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  base: 8px
  margin-mobile: 16px
  margin-tablet: 24px
  gutter: 16px
  touch-target: 48px
  card-padding: 20px
---

## Brand & Style

The design system is a premium, consumer-centric adaptation of enterprise aviation logic, optimized for the modern traveler. It follows **Material Design 3 (Material You)** principles with an **expressive** color variant, emphasizing personalization, energy, and high-impact motion. 

The aesthetic is **Corporate Modern with a Lifestyle edge**: it balances the rigorous safety and precision of aviation with the fluid, welcoming nature of luxury hospitality. The UI relies on expansive white space, "breathable" layouts, and high-impact semantic signaling to guide users through complex travel itineraries with ease.

**Key Brand Pillars:**
- **Precision:** Clean lines and structured data hierarchy.
- **Fluidity:** Transition-heavy interactions and organic, rounded shapes.
- **Hospitality:** Warm surface tones and approachable, legible typography.
- **Clarity:** Distinct color coding for status (On-time, Delayed, Gate Change).

## Colors
This design system utilizes the **Material Design 3 Expressive palette**. The core is shifted from traditional navy to a more contemporary, vibrant palette that maintains professionalism while feeling more dynamic and lifestyle-oriented.

- **Primary (#6770C4):** A soft, modern periwinkle-blue. Used for key actions, branding, and active states. 
- **Secondary (#707C51):** A muted sage green used for organic selection states and secondary navigation elements.
- **Tertiary (#6C7F1B):** An olive-gold accent used for lifestyle callouts and highlighting premium features.
- **Neutral (#78748E):** A sophisticated slate used to derive surface tones and containers, reducing eye strain while providing a warm, grounded backdrop.
- **Semantic Vibrancy:** Status colors are saturated and bold. A "Delayed" status uses high-contrast signaling that is unmistakable even in low-light airport environments.

## Typography
We use **Plus Jakarta Sans** across all levels to provide a modern, geometric, yet friendly appearance. It offers excellent legibility at small sizes (gate numbers, seat codes) while appearing sophisticated in large display formats.

**Hierarchy Rules:**
- **Case:** Use sentence case for all headlines and labels to maintain an approachable tone. 
- **Tracking:** Tighten tracking slightly on Display and Headline styles for a more "premium" editorial feel. Increase tracking on Label styles for functional clarity.
- **Dynamic Scaling:** Headlines scale down by 15% on mobile devices to prevent excessive line-wrapping in dense travel itineraries.

## Layout & Spacing
The layout follows a **fluid 4-column grid for mobile** and an **8-column grid for tablet**. 

- **Vertical Rhythm:** Built on an 8px baseline grid. All heights for inputs, buttons, and list items must be multiples of 8.
- **Safety Zones:** Maintain a minimum 16px horizontal margin on all mobile screens.
- **Touch Targets:** In line with accessibility standards, no interactive element should be smaller than 48x48px, even if the visual representation is smaller.
- **Contextual Reflow:** In flight-tracking views, the layout prioritizes vertical stacking. On tablets, use a split-pane view (List on left, Detail on right) to maximize screen real estate.

## Elevation & Depth
In this design system, depth is communicated through **Tonal Elevation** and **Soft Ambient Shadows**.

- **Level 0 (Surface):** The lowest layer. Uses the base surface color derived from the neutral palette.
- **Level 1 (Cards):** Resting state for primary content. Uses a subtle tonal shift and a 4px blur shadow (5% opacity) to provide a soft lift.
- **Level 2 (Active/Pressed):** Increased tonal contrast and an 8px blur shadow (10% opacity).
- **Glass Effects:** For overlay navigation bars or fixed bottom containers, use a backdrop-blur (20px) with 80% opacity of the surface color to maintain context.

## Shapes
We embrace **extra-large corner radii (Pill-shaped)**. This softens the technical nature of aviation data and makes the app feel more consumer-friendly and organic.

- **Small Components (Chips, Tooltips):** 8px (rounded-sm).
- **Medium Components (Buttons, Input Fields):** 16px (rounded-md).
- **Large Components (Cards, Modals, Bottom Sheets):** 24px - 32px (rounded-lg/xl).
- **Full Rounded:** Search bars and FABs should always be fully pill-shaped.

## Components

### Buttons
Primary buttons use the Expressive Primary color (#6770C4) with white text. They are high-height (56px) with 16px rounded corners. Secondary buttons use the Secondary Tonal Container.

### Elevated Cards
Cards are the primary container for flight info. They feature a 24px corner radius and a subtle 1px border in a slightly darker tonal shade than the card background to ensure definition on all screen brightness levels.

### Chips
Used for flight status (e.g., "Boarding", "Delayed"). These are pill-shaped. They use the semantic color palette at 10% opacity for the background and 100% opacity for the text/icon to ensure high legibility and a modern look.

### Input Fields
Filled style only (per MD3). They feature a thick bottom indicator on focus and use the 16px corner radius on the top edges. The background should be slightly darker than the surface it sits on.

### Bottom Sheets
The primary interaction pattern for selection (Seat Selection, Date Picking). These feature a "grabber" handle at the top and 32px rounded top corners. They should dim the background by 40% to focus the user's attention.

### Progress Indicators
Linear progress bars for "Flight Progress" use a rounded track with the Secondary sage green (#707C51), providing a smooth, organic visualization of time remaining.