# Plan: Add 6 Additional Settings to SwipeAway

This plan outlines the addition of at least 6 new customizable settings to the Settings tab, complete with persistence and UI integration.

## Proposed Settings:

1. **Haptic Feedback (Toggle)**
   - **Description**: Enable or disable vibration feedback when swiping cards left or right.
   - **Type**: Boolean (Default: `true`)
   - **UI**: Switch

2. **Sort Order (Options)**
   - **Description**: Change how the stack of pending apps is ordered (e.g., By Size, By Date Last Used, Alphabetical).
   - **Type**: String Enum (Default: `Size`)
   - **UI**: Dropdown or segmented buttons

3. **Theme Preference (Options)**
   - **Description**: Override the application's theme.
   - **Type**: String/Enum (Default: `System`, Options: `System`, `Light`, `Dark`)
   - **UI**: segmented buttons

4. **Show Storage Size in Card (Toggle)**
   - **Description**: Show or hide the storage metric block on the swipe card.
   - **Type**: Boolean (Default: `true`)
   - **UI**: Switch

5. **Show Last Time Used in Card (Toggle)**
   - **Description**: Show or hide the "Last Time Used" metric block on the swipe card.
   - **Type**: Boolean (Default: `true`)
   - **UI**: Switch

6. **Animation Speed (Options)**
   - **Description**: Adjust the speed and springiness of the swipe animations.
   - **Type**: String/Enum (Default: `Default`, Options: `Relaxed`, `Default`, `Snappy`, 'Silly')
   - **UI**: Dropdown or Segmented buttons

## Implementation Steps:

1. **Update Data Layer (`SettingsManager.kt`)**:
   - Add new `Preferences.Key` for each of the 6 settings.
   - Create `Flow` variables mapping to each preference.
   - Add suspend `set...` functions to update these preferences.

2. **Update ViewModel Layer (`SwipeViewModel.kt`)**:
   - Collect the new `SettingsManager` preference flows and expose them as `StateFlow` to the UI via `stateIn`.

3. **Update Settings UI (`AppSwipeApp.kt` -> `SettingsScreen`)**:
   - Add new `Card` blocks under categorized sections (e.g. Visuals, Behaviors, Card Display).
   - Implement `Switch` components for the boolean toggles.
   - Implement custom dropdowns or segmented control buttons for the enums (Sort Order, Theme, Animation Speed).

4. **Integrate Settings into App Components**:
   - **Sort Order**: Update the flow logic in `SwipeViewModel` to apply sorting before emitting `pendingApps`.
   - **Theme Preference**: Apply dynamic `isSystemInDarkTheme()` override in `MainActivity` or root `AppSwipeApp`.
   - **Card Toggles (Size/Last Used)**: Conditionally render the respective metric columns in `SwipeCard`.
   - **Haptic Feedback**: Update `SwipeCard` drag listeners to conditionally fire `.performHapticFeedback`.
   - **Animation Speed**: Adjust the `spring` specs in `SwipeCard` (e.g., `stiffness`) based on the selected setting.
