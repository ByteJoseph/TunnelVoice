<div align="center">
  <img src="https://github.com/ByteJoseph/ByteJoseph/blob/main/.github/logo-modified.png?raw=true" width="70" height="70" />
  <h1>TunnelVoice</h1>

  <p>
    <strong>TunnelVoice</strong> is an Android app for secretly listening to WhatsApp voice notes without sending blue ticks.
  </p>
</div>
<hr>
<table align="center">
  <tr>
    <td align="center">
      <img
        src="https://github.com/ByteJoseph/ByteJoseph/blob/main/.github/Screenshot_2026-06-09-10-13-00-52_996a1916517e51e2c96a6a3d5f414af2.jpg?raw=true"
        width="250"
        alt="Screenshot 1"
      />
    </td>
    <td align="center">
      <img
        src="https://github.com/ByteJoseph/ByteJoseph/blob/main/.github/Screenshot_2026-06-09-10-14-23-80_996a1916517e51e2c96a6a3d5f414af2.jpg?raw=true"
        width="250"
        alt="Screenshot 2"
      />
    </td>
  </tr>
  <tr>
    <td align="center"><b>Dark Mode</b></td>
    <td align="center"><b>Light Mode</b></td>
  </tr>
</table>

## Features

- Listen to voice notes without sending blue ticks

- Support for two WhatsApp accounts

- Automatically finds WhatsApp voice notes

- Simple, modern interface

- Voice notes grouped by date

- Built-in audio player

- Anonymous Firebase sign-in

- Material 3 dynamic theming

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build System**: Gradle (Kotlin DSL)

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
4. Run the app on your device.

### Permissions

The app requires the following permissions to function:
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` (for older Android versions)
- `MANAGE_EXTERNAL_STORAGE` (for Android 11+)

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

[MIT License](LICENSE)
