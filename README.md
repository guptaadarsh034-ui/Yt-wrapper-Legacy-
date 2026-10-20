# YT Legacy Wrapper

A small Android WebView wrapper intended for Android 4.4 (API 19) devices, including low-memory phones. This is an experimental compatibility project, not a port of Google's proprietary YouTube APK.

## Current approach

- Minimum Android version: API 19 (Android 4.4).
- Java-only app with no third-party runtime libraries.
- Uses the device's built-in WebView and hardware acceleration where supported.
- Keeps DOM storage enabled because mobile websites may rely on it; disables the WebView database API.
- Uses normal WebView caching rather than clearing cache on every launch.
- Avoids requesting `largeHeap` and avoids forcing an extra hardware layer.
- Handles back navigation, pause/resume, state restoration, and WebView cleanup.
- GitHub Actions builds a debug APK and uploads it as an artifact.

## Important limitations

Modern YouTube may not work reliably in Android 4.4's old WebView. Sign-in, video playback, codecs, TLS support, and website compatibility depend on the device firmware and YouTube's current website. Smooth playback or a specific RAM footprint cannot be guaranteed without testing on the target device. The app does not currently implement a separate Invidious backend.

## Build

Push to `main`, `master`, or `legacy-kitkat-optimization`, or run the workflow manually from GitHub Actions. Download the `YouTube-KitKat-512MB-APK` artifact from a successful workflow run.

The project uses Android Gradle Plugin 4.1.3, Gradle 6.8.3, and JDK 11. It compiles against API 30 while retaining API 19 as the minimum supported Android version.
