# YT Legacy

A small Android app for Android 4.4 (API 19) and low-memory devices. It has a lightweight direct-MP4 downloader and a separate YouTube mobile-site browser.

## Features

- Downloads direct HTTP/HTTPS MP4 file URLs using Android's built-in DownloadManager.
- Shows a local download list with current status and cancel controls.
- Uses Android's system download notification for progress/completion.
- Saves downloads to the public `Movies/YT Legacy` directory so media scanners, Gallery apps, and built-in video players can discover them.
- Includes a separate browser that opens `https://m.youtube.com/`.
- Keeps hardware acceleration enabled and avoids additional runtime libraries or a forced WebView hardware layer.

## Important download limitation

This is **not** a YouTube stream extractor. A YouTube watch-page URL (including `m.youtube.com`) is not a direct MP4 download URL, and this app deliberately does not extract or bypass protected/streaming YouTube URLs. Use direct MP4 links for videos you own or are authorized to download. The built-in Gallery/player can play the file only if the downloaded MP4 uses codecs supported by the device (commonly H.264 video and AAC audio).

## Size and compatibility

The release build enables code shrinking and resource shrinking. The project intentionally avoids third-party libraries and keeps the downloader UI native for lower memory use. An APK near 1 MiB is a goal, not a guarantee: Android framework compatibility, WebView access to `m.youtube.com`, and the remaining app code all have a size cost. Android APKs are executable packages, not HTML files. The YouTube screen uses the Android 4.4 system WebView; the downloader screen uses native Java views to reduce runtime overhead.

## Build

Push to `main`, `master`, or `legacy-kitkat-optimization`, or run the GitHub Actions workflow manually. Download the `YouTube-KitKat-512MB-Optimized-Release` artifact from a successful run. The workflow builds the minified release variant and reports its APK size against a 1 MiB target; if the app cannot fit without sacrificing functionality or compatibility, it reports that rather than failing the build.

The project compiles against API 30 with minimum Android API 19, using Android Gradle Plugin 4.1.3, Gradle 6.8.3, and JDK 11.
