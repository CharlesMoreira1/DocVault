[![AGP](https://img.shields.io/badge/AGP-9.x-blue?style=flat)](https://developer.android.com/studio/releases/gradle-plugin)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2-blueviolet?style=flat)](https://kotlinlang.org)

# DocVault

DocVault is a secure document manager for Android. The idea is simple... a place to store sensitive documents... images and PDFs, without worrying about them sitting unprotected in your gallery or file system. Everything is encrypted at rest, biometric authentication guards access, and the app blocks screenshots to prevent accidental exposure.

It's not trying to be a full-featured cloud document suite. It's a focused, offline-first vault for the documents you actually care about keeping private.

## Key Features

- **Encrypted storage** - all files are encrypted with AES-256-GCM using the Android KeyStore. Nothing is stored in plaintext.
- **Biometric authentication** - fingerprint or face unlock required to open or delete any document.
- **Camera capture with watermarking** - photos taken in-app are automatically watermarked with GPS coordinates, reverse-geocoded address, and timestamp.
- **PDF viewer** - multi-page PDFs are rendered natively via Android's PdfRenderer API.
- **Image viewer** - zoomable image viewing with Telephoto.
- **Access log** - the last 10 access timestamps are tracked per document.
- **Screenshot protection** - FLAG_SECURE is set globally to prevent screen capture.
- **Filter by type** - quickly filter your vault to show only images or only PDFs.

## Architecture

DocVault follows Clean Architecture layered with MVVM on the presentation side, organized across multiple Gradle modules:

```
app/                      → entry point, navigation, DI setup
core/common/              → biometric helper, encryption facade, watermark helper
core/designsystem/        → theme, shared Compose components
feature/home/             → document list screen (ViewModel + Compose UI)
feature/detail/           → document detail/viewer screen
feature/document/domain/  → use cases, domain models, repository interface
feature/document/data/    → repository impl, DataStore persistence, mappers
plugins/                  → custom Gradle convention plugins
```

State is managed with `StateFlow` for UI state and `Channel` for one-shot effects (navigation, toasts). No third-party state management library... just the standard Kotlin/Jetpack tools.

## Tech Stack

- **Language** - [Kotlin](https://kotlinlang.org/) with Coroutines and Flow
  - [Kotlinx Immutable Collections](https://github.com/Kotlin/kotlinx.collections.immutable) for stable list types in Compose
- **UI** - [Jetpack Compose](https://developer.android.com/jetpack/compose) (BOM 2026.*) + [Material 3](https://m3.material.io/)
- **Navigation** - [Navigation Compose](https://developer.android.com/jetpack/androidx/releases/navigation)
- **Dependency Injection** - [Koin](https://insert-koin.io/)
- **Persistence** - [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) (document metadata as JSON)
- **Serialization** - [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) JSON
- **Security** - Android KeyStore + AES-256-GCM, [Biometric API](https://developer.android.com/jetpack/androidx/releases/biometric)
- **Image zoom** - [Telephoto Zoomable](https://github.com/saket/telephoto)
- **Permissions** - [Accompanist Permissions](https://google.github.io/accompanist/permissions/)
- **Logging** - [Timber](https://github.com/JakeWharton/timber)
- **Build** - Gradle Kotlin DSL, Version Catalog, custom convention plugins

## Requirements

- Android 9.0+ (API 28)
- Device with biometric hardware (fingerprint or face recognition)

