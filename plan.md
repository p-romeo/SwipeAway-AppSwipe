# AGENTS.md Update Plan

Based on a deeper exploration of the SwipeAway codebase and its recent architectural changes, here is the expanded plan to update `AGENTS.md` to ensure future agents have accurate context on the app's structure, patterns, and dynamic features:

## 1. Update "Interaction Models & Gestures" for Configurability
- **Current Problem:** The file rigidly dictates that "Swipe Left = Keep" and "Swipe Right = Swipe Away."
- **Proposed Update:** Acknowledge that the swipe direction is dynamically configurable via `isRightSwipeUninstall` state. Require agents to utilize this dynamic state variable rather than hardcoding the colors and labels. 
- *Implementation detail found:* `SwipeViewModel.isRightSwipeUninstall` resolves from `SettingsManager`, which uses Jetpack `DataStore<Preferences>`. Future agents must bind UI states like swipe text, action buttons, and color schemes conditionally against this flag (e.g., using swapping ternary evaluations).

## 2. Introduce "Architecture & Data Flow" (New Section)
- **Current Problem:** Agents have no guidance on how state and data are managed.
- **Proposed Update:** Add guidelines documenting:
  - **Local Persistence & Sync:** The app relies on a `Room` database (`AppSwipeDatabase`, `SwipeRecordDao`) to persist the app list state across syncs. Explain the usage of `AppLoader.getInstalledLauncherApps()` syncing against the `Room` manifest during the `ON_RESUME` lifecycle event.
  - **Preferences System:** State is split into `DataStore` in `SettingsManager` for dynamic swipe directions, and legacy `SharedPreferences` in `SwipeRepository` for `isIgnoreSystemApps`. 
  - **State Management:** All UI states resolve through `SwipeViewModel` exposing `StateFlow` streams (`pendingApps`, `queuedApps`, etc.), collected in Jetpack Compose via `collectAsStateWithLifecycle()`. Mention that new features should avoid exposing raw `MutableStateFlow` directly to the view.

## 3. Document the App Navigation Structure
- **Current Problem:** Structure is vaguely referenced without concrete routing details.
- **Proposed Update:** Solidify the app layout explicitly as a single-activity architecture utilizing Bottom Navigation with 4 primary tabs via `Crossfade`: **Swipe**, **Queue**, **Insights**, and **Settings**. Mandate that agents respect this existing `AppTab` enum paradigm when adding new features, rather than creating separate Activities or complex layered Navigation setups.

## 4. Add "System Permissions & Metrics" under Integration Patterns
- **Current Problem:** Doesn't explain how vital metrics like "Last Time Used" and "Storage Size" are fetched.
- **Proposed Update:** Document the app's reliance on `PACKAGE_USAGE_STATS` (Usage Access) via `PermissionUtils` for sorting. Instruct agents that any UI displaying time-based app usage must provide a graceful fallback or prompt to open settings (`PermissionUtils.launchUsageAccessSettings`) if the permission is denied.

## 5. Refine "Google Play Inline Install" & Intent Handlers
- **Current Problem:** The current rules list raw UI-specific `ActivityResultLauncher` logic which is outdated.
- **Proposed Update:** Map out the new abstraction:
  - Intent generation is safely modularized inside `SwipeViewModel.buildInlineInstallIntent()` and `launchFallbackStore()`.
  - Handoff uninstall events map directly to `SwipeViewModel.handoffUninstall(context, record)`. 
  - Instruct agents to keep intent payload assembly strictly in the ViewModel constraints, while the Compose UI simply triggers the context-bound execution.
