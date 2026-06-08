# TunnelVoice

TunnelVoice is an Android application designed to help users manage and play WhatsApp voice notes efficiently while maintaining privacy. It allows users to listen to voice messages without triggering read receipts (blue ticks) in WhatsApp. It specifically addresses the needs of users with multiple WhatsApp accounts, providing a unified or togglable interface to access voice messages from different account directories.

## Features

- **Privacy Focused**: Listen to WhatsApp voice notes without sending read receipts (blue ticks), as the app accesses the files directly from the device storage.
- **Dual WhatsApp Support**: Automatically detects and handles voice notes from two different WhatsApp accounts (using the standard Android media path).
- **Voice Note Discovery**: Scans WhatsApp media directories to find and list `.opus` and other voice message files.
- **Modern UI**: Built with **Jetpack Compose** and **Material 3**, featuring a clean, responsive interface.
- **Date Grouping**: Organizes voice notes by "Today", "Yesterday", and specific dates for easy navigation.
- **Audio Playback**: Integrated playback controls with real-time progress tracking using Android's `MediaPlayer`.
- **Firebase Integration**: Includes anonymous authentication via Firebase Auth.
- **Dynamic Theming**: Support for Material 3 dynamic color and theme.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Authentication**: Firebase Auth
- **Build System**: Gradle (Kotlin DSL)

## Getting Started

### Prerequisites

- Android Studio Ladybug or newer.
- Android device or emulator running Android 8.0 (API level 26) or higher.
- WhatsApp installed (for the voice note directories to exist).

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ByteJoseph/tunnelvoice.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on your device/emulator.

### Permissions

The app requires the following permissions to function:
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` (for older Android versions)
- `MANAGE_EXTERNAL_STORAGE` (for Android 11+)
- `INTERNET` (for Firebase integration)

## Project Structure

- `app/src/main/java/app/bytejoseph/tunnelvoice/`:
    - `data/`: Contains `AuthManager` for Firebase.
    - `models/`: Data models like `VoiceNotes` and `TabItem`.
    - `ui/`: Compose components, themes, and screens.
    - `util/`: Constants and utility classes.
    - `VoiceViewModel.kt`: Main business logic and state management for audio files and playback.
    - `MainActivity.kt`: Entry point of the application.

## Build and Test

To build the project:
```bash
./gradlew assembleDebug
```

To run unit tests:
```bash
./gradlew testDebugUnitTest
```

## License

[MIT License](LICENSE) (Replace with your actual license if applicable)
