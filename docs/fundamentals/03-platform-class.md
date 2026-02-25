---
comments: false
icon: lucide/signpost
---

# Platform Class

The `Platform` class provides platform-specific information inside shared code in a clean and type-safe way.

It exposes:

- [x] OS version
- [x] Debug state
- [x] App information (version code, version name, app name)
- [x] Dynamic color support

---

## Debug Check (Recommended Usage)

You can easily detect whether the app is running in debug mode:

```kotlin
if (platform.debug) {
    // Debug-only logic (logging, test flags, mock configs)
}
```

This is useful for:

* Enabling verbose logging
* Reducing Remote Config fetch interval
* Disabling analytics in debug builds

---

## OS Version

Access the platform OS version:

```kotlin
val osVersion = platform.osVersion
```

### Android

* Returns `Build.VERSION.SDK_INT`

### iOS

* Returns major OS version as `Int`
* Full version is available via `exactVersion`

```kotlin
platform.ifIos { ios->
    val major = ios.exactVersion.major
    val minor = ios.exactVersion.minor
    val patch = ios.exactVersion.patch
}
```

---

## App Info

```kotlin
val versionCode = platform.appInfo.version
val versionName = platform.appInfo.versionName
val appName = platform.appInfo.appName
```

`AppInfo` contains shared metadata for both platforms.

---

## Platform Types

`Platform` is a sealed class with two implementations:

### Android

```kotlin
Platform.Android(
    osVersion: Int,
    debug: Boolean,
    appInfo: AppInfo
)
```

* `isDynamicColorSupported = osVersion >= 31` (Android 12+)

---

### iOS

```kotlin
Platform.Ios(
    osVersion: Int,
    debug: Boolean,
    appInfo: AppInfo,
    exactVersion: IosVersion
)
```

* Provides detailed iOS version via `exactVersion`
* Dynamic color is not supported

---

## Platform Checks

```kotlin
if (platform.isAndroid) {
    // Android logic
}

if (platform.isIos) {
    // iOS logic
}
```

Or use helpers:

```kotlin
platform.ifAndroid { android->
    // Android-only code
}

platform.ifIos { ios->
    // iOS-only code
}
```

---

## Dynamic Color Check

```kotlin
if (platform.isDynamicColorSupported) {
    // Enable dynamic colors (Android 12+)
}
```

This keeps shared code platform-aware, centralized, and clean without scattering platform checks across the codebase.


---
## Support My Project ☕️

If you find this project useful, consider supporting it by buying me a coffee. Your support will help me to continue working on this project and add more features.

<div >
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="150" />
  </a>
  <a href="https://www.youtube.com/@devatrii" target="_blank">
    <img src="https://img.shields.io/badge/YouTube-DevAtrii-red?style=for-the-badge&logo=youtube&logoColor=white" alt="YouTube Channel" />
  </a>
</div>