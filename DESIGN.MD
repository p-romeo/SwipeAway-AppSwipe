---
name: Vibrant Declutter
colors:
  surface: '#faf8ff'
  surface-dim: '#d0d8ff'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f2ff'
  surface-container: '#ebedff'
  surface-container-high: '#e3e7ff'
  surface-container-highest: '#dce1ff'
  on-surface: '#101a38'
  on-surface-variant: '#434656'
  inverse-surface: '#262f4e'
  inverse-on-surface: '#eff0ff'
  outline: '#737688'
  outline-variant: '#c3c5d9'
  surface-tint: '#004de9'
  primary: '#0045d2'
  on-primary: '#ffffff'
  primary-container: '#1b5cff'
  on-primary-container: '#edeeff'
  inverse-primary: '#b6c4ff'
  secondary: '#006970'
  on-secondary: '#ffffff'
  secondary-container: '#4af2ff'
  on-secondary-container: '#006c73'
  tertiary: '#a8004e'
  on-tertiary: '#ffffff'
  tertiary-container: '#d50064'
  on-tertiary-container: '#ffeaed'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b6c4ff'
  on-primary-fixed: '#001550'
  on-primary-fixed-variant: '#003ab3'
  secondary-fixed: '#7bf4ff'
  secondary-fixed-dim: '#1edbe8'
  on-secondary-fixed: '#002022'
  on-secondary-fixed-variant: '#004f54'
  tertiary-fixed: '#ffd9e0'
  tertiary-fixed-dim: '#ffb1c3'
  on-tertiary-fixed: '#3f0019'
  on-tertiary-fixed-variant: '#8f0041'
  background: '#faf8ff'
  on-background: '#101a38'
  surface-variant: '#dce1ff'
typography:
  display-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.5px
  headline-md:
    fontFamily: Be Vietnam Pro
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.2px
  title-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: 0px
  body-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  label-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-caps:
    fontFamily: Be Vietnam Pro
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 1.0px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-mobile: 1rem
  gutter: 1rem
  stack-offset: 0.5rem
  section-gap: 1.5rem
  unit: 4px
---

## Brand & Style

The design system is a high-energy, gamified interpretation of Material 3 principles. It transforms the administrative task of storage management into a tactile, rewarding experience through physics-based interactions and a "pebble" aesthetic. The personality is athletic, clean, and responsive, emphasizing speed and efficiency.

The visual style is **Corporate / Modern** with a **Tactile** twist. It uses generous whitespace, hyper-rounded containers, and bouncy animation logic to create a UI that feels physically present. High-saturation functional colors are balanced against soft, warm surfaces to maintain an inviting and accessible atmosphere.

## Colors

The palette is functionally driven, using high-contrast interactive markers against soft background containers.

- **Interactive Primary:** Electric Blue (`#1B5CFF`) is reserved for "Keep" actions, primary buttons, and active navigational states.
- **Interactive Tertiary:** Hot Pink (`#FF0A7A`) signals destructive intent, identifying the "Swipe Away" queue and batch uninstall triggers.
- **Functional Accents:** Success Green and Danger Red are used for semantic metrics (e.g., active vs. stale apps).
- **Surface Roles:** The system uses "Soft" variants of primary and tertiary colors (`#EEF4FF`, `#E8FCFF`, `#FFE8F3`) for large banners and container backgrounds to reduce visual fatigue while maintaining brand consistency.
- **Typography:** Deep Navy (`#07112F`) is used for all primary text to provide a softer, more premium contrast than pure black.

## Typography

The typography system uses **Be Vietnam Pro** to convey a contemporary, approachable, and slightly athletic feel. 

- **Display & Headlines:** Use heavy weights (Bold/700) and tight tracking to create punchy, confident titles.
- **Body & Labels:** Use standard weights with generous tracking to ensure legibility during rapid scanning of app lists and metrics.
- **Semantic Metadata:** Small Caps are used for system-level headers (e.g., "STORAGE", "CATEGORY") to distinguish them from user-generated app names.

## Layout & Spacing

This design system employs a **Fluid Grid** optimized for mobile-first interaction. 

- **Touch Areas:** All interactive targets maintain a minimum height of 40px, with primary actions (like the Swipe buttons) scaled to 64px for maximum thumb accessibility.
- **Rhythm:** A 4px baseline grid governs all spacing. Vertical gaps between list items and sections range from 12px (within a group) to 24px (between distinct layout blocks).
- **Margins:** A consistent 16px (1rem) margin is applied to the left and right of all screens to ensure content remains within the "safe zone" of mobile bezels.

## Elevation & Depth

Visual hierarchy is established through **Ambient Shadows** and **Tonal Layers**. 

- **Primary Surfaces:** Main cards and interactive elements use diffused, low-opacity shadows (Blur 8-12px, Y-offset 4px) to appear lifted above the background.
- **Stacked Cards:** In the swipe interface, depth is simulated by scaling and vertical offsets. The active card has the highest elevation, while cards underneath are progressively scaled down (3% per layer) and use a lower-intensity shadow.
- **Interactive Depth:** Floating action buttons and the active "Swipe" card use a 12dp elevation during gestures to emphasize their physical independence from the grid.

## Shapes

The shape language is organic and "soft-touch," avoiding sharp angles to reinforce the friendly, gamified nature of the product.

- **Cards:** Main surface containers (Swipe cards, Insights blocks) use a 24px-32px radius.
- **Buttons:** Sequential action buttons and navigation indicators use pill-shaped (50% or "full") roundedness.
- **Tags/Chips:** Small category tags use an 8px radius to remain distinct from the larger interactive containers.

## Components

### Buttons
- **Primary FABs:** Circular (64px) with 4dp elevation. "Keep" (Blue background/icon) and "Delete" (Pink background/icon) should be positioned at the bottom for thumb reach.
- **Sequential Action:** Full-width pill button (50px height) in solid Tertiary (Hot Pink) with bold white text.
- **Pill Selectors:** Outlined capsules for secondary actions like "Keep" in list views, using a 1px border and medium weight text.

### 'Swipe' Cards
- **Structure:** 24px corner radius with a 1.5px subtle outline.
- **Top Row:** App Category Chip (Left) and "System" tag (Right).
- **Center:** 160px container with 16px radius, tinted 12% category color, holding a centered app icon with 6dp shadow.
- **Metadata:** A horizontal divider separates the app identity from storage metrics. Use `label-caps` for "STORAGE" and "LAST USED" headers.

### List Items
- **Queue Cards:** White elevated cards (16px radius). Icons are circular (44px). Primary app titles are bold, with secondary category/size details in a smaller weight.
- **Inline Actions:** Every list item should include a full-width "Uninstall" button at its base (10px corners) to allow immediate individual processing within the queue.

### Insights & Charts
- **Dashboard Cards:** 20px rounded cards with 1px border. 
- **Progress Bars:** 12px tall with 6px rounded caps. Use multi-colored segments (Primary Blue, Tertiary Pink, Neutral Gray) to visualize data distribution.
- **Category Rows:** Expandable cards with 12px radius. Active categories should have a 1px border colored to the specific category's theme.