# Tunnel Voice

Tunnel Voice is an Android application designed to let users listen to WhatsApp voice messages without leaving any trace, such as the "blue ticks" (read receipts). It directly accesses the local WhatsApp media storage to play voice notes independently of the WhatsApp app.

## Features

- **Invisible Listening:** Listen to voice messages without notifying the sender.
- **Multi-Account Support:** Automatically detects and supports dual WhatsApp accounts.
- **Organized View:** Messages are grouped by date (Today, Yesterday, etc.) for easy navigation.
- **Built-in Player:** Simple and intuitive audio playback controls.
- **Material 3 Design:** Modern and clean user interface built with Jetpack Compose.

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Design System:** Material 3
- **Architecture:** MVVM (ViewModel, State)
- **Local Storage:** Accesses Android media directories

## Permissions

To function correctly, Tunnel Voice requires the following permissions:
- **All Files Access (MANAGE_EXTERNAL_STORAGE):** Necessary to read WhatsApp voice notes from the Android media folder on Android 11+.
- **Storage Read/Write:** For older Android versions.

## How it works

The app scans the default WhatsApp media directories:
- Single account: `/Android/media/com.whatsapp/whatsapp/Media/WhatsApp Voice Notes`
- Dual accounts: `/Android/media/com.whatsapp/WhatsApp/accounts`

It fetches the files directly from these folders, allowing you to play them without triggering WhatsApp's internal "played" status.

## Development

The project is built using Gradle.

### Prerequisites
- Android Studio Iguana or newer
- JDK 17

### Build
```bash
./gradlew assembleDebug
```

---
*Note: This app is for personal use.*
