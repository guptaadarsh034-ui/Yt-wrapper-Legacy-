# YT Legacy Wrapper

A lightweight, high-performance YouTube wrapper built specifically for **Android 4.4 (KitKat)** devices with **512 MB RAM**.

Modern YouTube apps and standard WebViews cause severe frame drops, stuttering, and Out-Of-Memory (OOM) crashes on low-spec legacy hardware. **YT Legacy Wrapper** addresses this by using a stripped-down client rendering loop, hardware-accelerated WebKit views, and aggressive RAM management to deliver a smooth, 60fps playback experience reminiscent of YouTube on legacy systems.

---

## 🌟 Key Features

* **Low RAM Footprint:** Operates strictly within **~50MB – 80MB of RAM** (designed specifically for 512MB RAM constraints).
* **Hardware Accelerated:** Forces GPU-based WebKit rendering for smooth UI transitions and scrolling.
* **Auto Build via GitHub Actions:** Compiles and generates the APK automatically every time you push code.
* **Zero Google Play Services Dependency:** Runs smoothly on AOSP, custom ROMs, or devices without GApps.
* **Invidious / Lightweight Backend Support:** Prevents heavy, modern JavaScript execution engines from choking legacy CPUs.

---

## 🛠️ Repository Architecture

```text
yt-legacy-wrapper/
├── .github/
│   └── workflows/
│       └── build.yml          # Automated CI/CD compilation pipeline
├── app/
│   ├── build.gradle           # Target API 19 (KitKat) configurations
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/yt/wrapper/
│               └── MainActivity.java   # Optimized WebKit view engine
├── build.gradle               # Root build engine settings
├── README.md
└── settings.gradle
