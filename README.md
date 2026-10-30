# YT Legacy

**A lightweight YouTube mobile-site wrapper and direct-MP4 downloader for legacy Android devices.**

YT Legacy is designed with Android 4.4.4 KitKat (API 19) and low-memory devices in mind. It keeps the Android app itself small and uses the device's built-in WebView to open YouTube's mobile website.

> **Compatibility note:** This project is not the original native YouTube app and does not contain its proprietary playback engine. YouTube may change its website or require browser features that Android 4.4's WebView does not support. Shorts and long-video playback must be tested on the target device; smooth playback is not guaranteed, especially on devices with 512 MB RAM.

## Features

- **YouTube mobile browser** — opens `https://m.youtube.com/` inside the app.
- **One-tap Shorts entry** — opens `https://m.youtube.com/shorts/`.
- **Compact browser toolbar** — Back, Home, Shorts, Reload, and Next controls.
- **Fullscreen video support** — handles the WebView's custom fullscreen video view where the site and device support it.
- **Page-loading indicator** — shows WebView page load progress.
- **Low-memory handling** — asks WebView to release reclaimable memory when Android reports memory pressure, and cleans up the WebView when the screen is destroyed.
- **Direct MP4 downloader** — uses Android's built-in `DownloadManager`, shows download status, and lets users cancel active downloads.
- **Download notifications and history** — Android handles transfer notifications; the app remembers its download IDs.
- **Gallery-friendly destination** — saves supported files under `Movies/YT Legacy`.
- **No third-party runtime libraries** — native Android views and platform APIs help keep dependencies down.
- **Android 4.4+ target** — minimum SDK/API 19.

## Download limitations

The downloader accepts **direct HTTP/HTTPS MP4 file URLs** only. It does not extract YouTube streams or download videos from YouTube watch-page, Shorts, `googlevideo.com`, or other protected/streaming URLs. Download only videos you own or are authorized to save.

A downloaded MP4 will play only if its audio/video codecs are supported by the device's built-in player. H.264 video with AAC audio is a common compatibility choice for older Android hardware.

## Performance and APK size

The release build enables code shrinking and resource shrinking. The project avoids additional runtime libraries and keeps the downloader interface native. The browser still relies on Android's system WebView, which can use significant RAM and may not support today's YouTube site.

**A 5 MB APK is a size target, not a promise of better performance.** Increasing APK size does not automatically add a native video engine, make YouTube compatible with KitKat, or improve buffering. The best video quality and playback smoothness depend on the website, network, device decoder, available memory, and video format.

## Build the release APK

1. Open the [GitHub Actions builds](https://github.com/guptaadarsh034-ui/Yt-wrapper-Legacy-/actions).
2. Select a successful **Build Android 4.4 APK** run for the `legacy-kitkat-optimization` branch.
3. Under **Artifacts**, download `YouTube-KitKat-512MB-Optimized-Release`.
4. Extract the ZIP to get the APK.

The workflow builds the minified release variant and reports the APK size. A successful CI build confirms compilation and packaging, **not** playback compatibility on a physical KitKat phone.

## Build environment

- Compile SDK: Android API 30
- Minimum SDK: Android API 19 (Android 4.4 KitKat)
- Android Gradle Plugin: 4.1.3
- Gradle: 6.8.3
- JDK: 11

## Current development version

The development branch is working toward **YT Legacy 1.7**. It includes the Shorts entry point and compact browser navigation controls alongside the earlier fullscreen, progress, and memory-handling changes. Check the Actions run and test on real hardware before treating playback as verified.

## Contributing / bug reports

When reporting a problem, include the Android version, device RAM, whether the problem affects Shorts or long videos, the exact error shown, and whether the page loads before playback fails. Do not include passwords, account tokens, or private URLs.
