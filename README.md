# YT Legacy Wrapper

A small Android WebView wrapper intended for Android 4.4 (API 19) devices, including low-memory phones. This is an experimental compatibility project, not a port of Google's proprietary YouTube APK.

## Smoothness and low-memory tuning

- Hardware acceleration is enabled at both application and activity level in the manifest.
- Does not force a separate hardware layer for the WebView; extra off-screen textures can increase graphics-memory pressure on 512 MB devices.
- Hides scrollbars and disables overscroll effects to reduce visual clutter and edge-effect work.
- Disables the WebView drawing cache and avoids extra runtime libraries.
- Uses the normal WebView cache; does not clear it on every launch.
- Uses the API 19 WebView render-priority hint where supported. Newer WebView implementations may ignore this deprecated hint.
- Does not request `largeHeap`; it keeps DOM storage for site compatibility and disables the legacy WebView database API.
- Handles back navigation, pause/resume, state restoration, and WebView cleanup.

## Important limitations

Modern YouTube may not work reliably in Android 4.4's old WebView. Sign-in, video playback, codecs, TLS support, website compatibility, and frame rate depend on device firmware, GPU drivers, memory pressure, and YouTube's current website. These settings are tuning, not a guarantee of a specific frame rate or RAM footprint; real-device testing is required. The app does not currently implement a separate Invidious backend.

## Build

Push to `main`, `master`, or `legacy-kitkat-optimization`, or run the workflow manually from GitHub Actions. Download the `YouTube-KitKat-512MB-APK` artifact from a successful workflow run.

The project uses Android Gradle Plugin 4.1.3, Gradle 6.8.3, and JDK 11. It compiles against API 30 while retaining API 19 as the minimum supported Android version.
