# 💥 SwipeAway

**SwipeAway** is a modern Android utility app that helps you declutter your phone by playfully swiping through your installed apps. Rather than digging through sterile system menus, SwipeAway presents a premium, motion-forward decluttering experience for your digital space. 

## ✨ Features

*   **Tinder-style Decluttering:** Swipe to the left to **KEEP** ⚡ an app, and swipe right to **SWIPE AWAY** 🗑️ (add it to your uninstall queue).
*   **Smart Sorting:** Utilizes Android Usage Access metrics to sort your installed apps by "Last Time Used" and "Storage Size", surfacing the gigabytes you've forgotten about.
*   **Batch Uninstall Queue:** Safely review your "Swipe Away" stack before confirming any actions. When you're ready, the app smoothly hands over uninstalls to the native Android manager.
*   **Offline-First & Private:** App lists and swipe decisions are persisted locally on your device within a secure Room database. Your app list never leaves your phone.
*   **Premium Design:** Engineered with modern Jetpack Compose layouts, featuring spring-physics animations, haptic feedback, and a sleek custom palette (Deep Navy, Electric Blue, Bright Cyan, Hot Pink).

## 🛠️ Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material Design 3 elements)
*   **Architecture:** MVVM (Model-View-ViewModel), leveraging `StateFlow`, `collectAsStateWithLifecycle`, and clean application architecture.
*   **Local Storage:** Room Database for persistent swipe histories and Jetpack DataStore for scalable settings preferences.
*   **System Integrations:** Leverages `PACKAGE_USAGE_STATS` for accurate app metrics and local Android `PackageManager` APIs for system syncs tracking installed states.

## 🚀 Building from Source

To build and run SwipeAway locally on your machine, follow these steps:

### Prerequisites
*   [Android Studio](https://developer.android.com/studio) (Latest stable version recommended, e.g., Iguana, Jellyfish, or newer).
*   Java Development Kit (JDK) 17+ (usually bundled with modern Android Studio).
*   An Android physical device or Emulator running Android 8.0 (API level 26) or higher.

### Installation & Run Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/SwipeAway.git
    cd SwipeAway
    ```

2.  **Open the project:**
    *   Launch Android Studio.
    *   Select **Open** and choose the downloaded `SwipeAway` project folder.
    *   Wait a few moments for Gradle to fully sync the project dependencies.

3.  **Build the app:**
    *   You can build the app directly inside Android Studio by clicking the **Make Project** (hammer icon) button in the top toolbar.
    *   Alternatively, use the terminal to generate a Debug APK:
        ```bash
        ./gradlew assembleDebug
        ```

4.  **Run the app:**
    *   Connect your Android device via USB/Wi-Fi or start up a local Android Emulator.
    *   Click the **Run 'app'** (green play triangle) button.
    *   *Permission Note: On your very first launch, the app will prompt you for 'Usage Access'. This is required so SwipeAway can sort apps by time-since-last-use and file size.*

## 📐 Architectural Highlights

*   **Local Synchronization:** The `AppLoader` component interfaces with the Android system's package manager during the application's `onResume` lifecycle block. It intelligently synchronizes newly installed or uninstalled apps locally against the Room `AppSwipeDatabase` to keep your review queue completely up to date.
*   **Dynamic Customization:** Left/Right swipe directions are strictly configurable via user settings, which maps instantly to the UI using a centralized Jetpack `DataStore` mechanism (`SettingsManager`).

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/your-username/SwipeAway/issues) or submit a Pull Request.

## 📝 License

This project is licensed under the MIT License.
