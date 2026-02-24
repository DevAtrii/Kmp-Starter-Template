# Metadata

This page includes documentation for changing the app icon, version, package name, and app name.

---

## Changing App Version

```toml title="gradle/libs.versions.toml" linenums="1"
[versions]
...
app-version-major = "1"
app-version-minor = "0"
app-version-patch = "0"
...
```

We use a `MAJOR.MINOR.PATCH` scheme:

* **Major** → major changes, complete revamp
* **Minor** → small features or updates
* **Patch** → bug fixes

Version code is generated as:

```
(versionMajor * 10000) + (versionMinor * 100) + versionPatch
```

Example: `2.5.0 → 25000`

On iOS, `AppConfig.xcconfig` is automatically synced from `gradle/libs.versions.toml` using a **Run Script** in Build Phases inside Xcode.
The first build updates the file; subsequent builds use the updated version.

---

## Platform Specific

### Android

#### App Icon

You can generate an app icon using <a href="https://icon.kitchen/" target="_blank">IconKitchen</a>. Just upload your app icon, and it will generate all required icons.

After exporting, you will get a zip file. Extract it and copy all files from `IconKitchen-Output/android/res/*` to the `androidApp/src/main/res/` folder.

!!! note "Important"
    Make sure to overwrite all files in `androidApp/src/main/res/`.

Your Android app icon is now updated.

##### Removing Monochrome Mode

IconKitchen enables monochrome mode by default. If your icon isn't PNG, it can cause Play Store rejection.
To disable `monochrome` mode, remove the following line:

```xml title="androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" hl_lines="5" linenums="1"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
  <background android:drawable="@mipmap/ic_launcher_background"/>
  <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
  <monochrome android:drawable="@mipmap/ic_launcher_monochrome"/>
</adaptive-icon>
```

---

#### App Name

```xml title="androidApp/src/main/res/values/strings.xml" linenums="1" hl_lines="3"
<resources>
    ...
    <string name="app_name">KmpStarter</string>
    ...
</resources>
```

Change `KmpStarter` to your app name.

---

#### Package Name

```kotlin title="androidApp/build.gradle.kts" linenums="1" hl_lines="4-6"
android {
    ...
    defaultConfig {
        ...
        applicationId = "com.kmpstarter"
        ...
    }
}
```

Change `com.kmpstarter` to your package name.

---

### iOS

#### App Name

Use Xcode to change the app name:
![changing app name](../assets/ios-app-name.png)

---

#### Package Name

```xcconfig title="iosApp/AppConfig.xcconfig"
PRODUCT_BUNDLE_IDENTIFIER=com.kmpstarter.nativeapp
```

Change `com.kmpstarter.nativeapp` to your package name.

!!! warning "Warning"
    Always change the package name through `AppConfig.xcconfig`. Changing it through the Xcode UI may cause issues.

---

#### App Icon

You can create an app icon using <a href="https://developer.apple.com/icon-composer/" target="_blank">IconComposer</a>, which also gives the Apple Liquid Glass effect.

##### Step 0

Create the app icon using IconComposer:
![IconComposer](../assets/ios-icon-0.webp)

##### Step 1

Export the app icon with the following settings to generate all variants:
![Export Settings](../assets/ios-icon-1.webp)
You’ll see exported icons like this:
![Exported Icons](../assets/ios-icon-2.webp)

##### Step 2

Open Xcode and go to `Assets` inside the `iosApp` target:
![Assets](../assets/ios-icon-3.webp)
Drag all corresponding icons to each variant block, e.g.:

* `Default -> Any Appearance`
* `Dark -> Dark Appearance`
* `Tinted -> Tinted Appearance`

![Icons](../assets/ios-icon-4.webp)

Your iOS app icon is now updated.

---

## Support My Project ☕️

If you find this project useful, consider supporting it by buying me a coffee. Your support helps me continue working on this project and add more features.

<div>
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="150" />
  </a>
  <a href="https://www.youtube.com/@devatrii" target="_blank">
    <img src="https://img.shields.io/badge/YouTube-DevAtrii-red?style=for-the-badge&logo=youtube&logoColor=white" alt="YouTube Channel" />
  </a>
</div>
