# Brand Design Standards: SwipeAway

All future agents modifying the SwipeAway codebase MUST adhere strictly to these signature branding and interface rules to maintain product premium design qualities.

## App Concept & Positioning
SwipeAway is a modern Android utility app that helps users declutter their phones by swiping through installed apps. It should feel like a premium consumer app with playful, motion-forward, and satisfying startup energy, rather than a dry technical system tool.

## Key Design Rules
- Avoid Google-style red, yellow, green, and blue as primary flat colors.
- Avoid dull, muted teal/coral schemes.
- Use rounded cards, large corners, soft drop shadows, and vibrant gradients sparingly but confidently.

---

## 🎨 Color Palette Reference

| Name | Color Token | Hex Code | Usage |
| :--- | :--- | :--- | :--- |
| **Deep Navy** | `DeepNavy` | `#07112F` | Primary text and dark elements |
| **Electric Blue** | `ElectricBlue` | `#1B5CFF` | Primary CTA, keep buttons, core brand element |
| **Bright Cyan** | `BrightCyan` | `#18D9E6` | Secondary accent highlights, drag motion cues |
| **Hot Pink** | `HotPink` | `#FF0A7A` | Uninstall actions, swipe-away badges, brand accent |
| **White** | `White` | `#FFFFFF` | Page canvas backgrounds and light containers |
| **Soft Cyan** | `SoftCyanBackground` | `#E8FCFF` | Review screen highlight, active filters, cards |
| **Soft Pink** | `SoftPinkBackground` | `#FFE8F3` | Uninstall queues and swipe danger warnings |
| **Muted Blue** | `MutedBlueSurface` | `#EEF4FF` | Active background overlays and selection pills |
| **Text Gray** | `TextGray` | `#667085` | Subtext and secondary labels |
| **Border Gray** | `BorderGray` | `#D0D5DD` | Inactive borders, thin outlines |
| **Danger Red** | `DangerRed` | `#F04438` | ONLY on native system uninstalls / destructive confirm |
| **Success Green** | `SuccessGreen` | `#12B76A` | Done state indicators, positive statistics |

---

## 📐 Shape & Spacing Language
- **Card Corners**: Use rounded sheets with `24.dp` corners.
- **Pill Buttons**: Use maximum padding and rounded edges with `50` or `999.dp` corner scales.
- **Grids**: Standard Material 3 padding with standard margins (`16.dp`).

---

## ⚡ Interaction Models & Gestures
1. **Dynamic Swiping**: The swipe direction is dynamically configurable via the `isRightSwipeUninstall` state (resolved from `SettingsManager` using Jetpack `DataStore<Preferences>`). Agents must conditionally bind UI states like swipe text, action buttons, and color schemes against this flag using conditional logic in the Compose UI.
   - For the **Keep** direction: Accent color is **Electric Blue / Bright Cyan**, Active Overlay copy is `"KEEP ⚡"`.
   - For the **Swipe Away (Queue)** direction: Accent color is **Hot Pink**, Active Overlay copy is `"SWIPE AWAY 🗑️"`.
2. **Animations**: Spring curves with gentle bounces (`Spring.DampingRatioMediumBouncy`, `Spring.StiffnessLow`).
3. **Haptics & Feedback**: Trigger quick vibration feeds on choices.

---

## 🏗️ Architecture & Data Flow
- **Local Persistence & Sync**: The app uses a `Room` database (`AppSwipeDatabase`, `SwipeRecordDao`) to persist the app list state across syncs. `AppLoader.getInstalledLauncherApps()` syncs against the `Room` manifest during the `ON_RESUME` lifecycle event.
- **Preferences System**: State is split into `DataStore` in `SettingsManager` for configuration (like dynamic swipe directions), and legacy `SharedPreferences` in `SwipeRepository` (like `isIgnoreSystemApps`). 
- **State Management**: All UI states resolve through `SwipeViewModel`, which exposes `StateFlow` streams (e.g., `pendingApps`, `queuedApps`). These should be collected in Jetpack Compose via `collectAsStateWithLifecycle()`. New features should avoid exposing raw `MutableStateFlow` directly to the view.

---

## 🧭 App Navigation Structure
- The app layout uses a single-activity architecture utilizing Bottom Navigation with 4 primary tabs via `Crossfade`: **Swipe**, **Queue**, **Insights**, and **Settings**. 
- Agents **must** respect this existing `AppTab` enum paradigm when adding new features, rather than creating separate Activities or complex layered Navigation setups.

---

## 🛡️ Tone & Voice Principles
- Keep descriptions human, clear, and reassuring.
- **DO NOT** use aggressive language like "Nuke", "Destroy", "Purge", or "Delete forever" anywhere in the app labels.
- **DO** use clear actions: "Keep", "Swipe Away", "Review", "Undo", "Continue", "Done", "Uninstall Native".

---

## 🛠️ Integration Patterns & Capabilities
- **System Permissions & Metrics**: The app relies on `PACKAGE_USAGE_STATS` (Usage Access) via `PermissionUtils` for sorting by metrics like "Last Time Used" and "Storage Size". Any UI displaying time-based app usage must provide a graceful fallback or prompt to open settings (`PermissionUtils.launchUsageAccessSettings`) if the permission is denied.
- **Google Play Inline Install & Intent Handlers**:
   - Intent generation is safely modularized inside `SwipeViewModel.buildInlineInstallIntent()` and `launchFallbackStore()`.
   - Handoff uninstall events map directly to `SwipeViewModel.handoffUninstall(context, record)`.
   - Agents must keep intent payload assembly strictly in the ViewModel constraints, while the Compose UI simply triggers the context-bound execution.
