# TunnelVoice

**TunnelVoice** is an Android app for listening to WhatsApp voice notes without sending blue ticks.

TunnelVoice is designed to be lightweight, private-first, and fast, helping users review voice notes with a smooth modern experience while preserving message-read privacy behavior.

## Features

- Listen to voice notes without sending blue ticks
- Support for two WhatsApp accounts
- Automatically finds WhatsApp voice notes
- Simple, modern interface
- Voice notes grouped by date
- Built-in audio player
- Anonymous Firebase sign-in
- Material 3 dynamic theming
- Manual in-app refresh to immediately load newly received voice notes without reopening the app

## Why TunnelVoice?

- **Privacy-focused usage**: Built around the goal of listening to voice notes discreetly.
- **Fast access**: Automatically scans relevant WhatsApp voice note folders.
- **Dual-account friendly**: Supports environments where two WhatsApp account directories are available.
- **Modern Android UX**: Powered by Jetpack Compose and Material 3 components.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Authentication**: Firebase Auth
- **Build System**: Gradle (Kotlin DSL)
- **Audio Handling**: Android `MediaPlayer`

## Getting Started

### Prerequisites

- Android Studio Ladybug or newer.
- Android device or emulator running Android 8.0 (API level 26) or higher.
- WhatsApp installed (for the voice note directories to exist).

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ByteJoseph/TunnelVoice.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on your device/emulator.

### First Launch Experience

On first launch, TunnelVoice requests the storage permissions needed to read WhatsApp voice note directories.  
After permissions are granted, return to the app and tap the Refresh action to index and display available voice notes.

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

To run lint checks:
```bash
./gradlew lint
```

## Reliability Notes

- Voice note lists are resolved from WhatsApp media directories at runtime.
- Playback is managed using lifecycle-aware ViewModel state.
- Date labels include contextual names such as **Today** and **Yesterday** for faster scanning.

## Contribution

Contributions are welcome. Please keep pull requests focused, tested, and aligned with the existing Kotlin + Compose architecture.

## License

[MIT License](LICENSE)
