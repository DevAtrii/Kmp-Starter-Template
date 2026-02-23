# Metadata

This page include changing app icon, changing version, changing package name & change app name docs.

## Changing App Version
``` toml title="gradle/libs.versions.toml"
[versions]
...
app-version-major = "1"
app-version-minor = "0"
app-version-patch = "0"
...
```
We use a `MAJOR.MINOR.PATCH` scheme:

* **Major** → major changes, total revamp
* **Minor** → small features or changes
* **Patch** → bug fixes

Version code is generated as:

```
(versionMajor * 10000) + (versionMinor * 100) + versionPatch
```

Example: `2.5.0 → 25000`

On iOS, `AppConfig.xcconfig` is automatically synced from `gradle/libs.versions.toml` using `Run Script` in Build Phases inside xCode.
The first build updates the file; subsequent builds use the updated version.



## Platform Specific
### Android
#### App Icon
You can generate app icon using <a href="https://icon.kitchen/" target="_blank">IconKitchen</a>. just upload your app icon and it will generate all the required icons for you.

??? abstract "IconKitchen"
    ![IconKitchen](../assets/icon-kitchen.png)

After exporting the icon you will get a zip file, extract it and copy all files from `IconKitchen-Output/android/res/*` to `androidApp/src/main/res/` folder.
!!! note "Important"
    Make sure to overwrite all files in `androidApp/src/main/res/` folder.

Done your androidApp icon has been changed

##### Removing Monochrome Mode
IconKitchen by default enables monochrome mode, but if your icon isn't png it can cause app rejection on playstore, To disable `monochrome` mode remove the following line:
```xml title="androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" hl_lines="5"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
  <background android:drawable="@mipmap/ic_launcher_background"/>
  <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
  <monochrome android:drawable="@mipmap/ic_launcher_monochrome"/>
</adaptive-icon>
```

#### App Name
``` xml title="androidApp/src/main/res/values/strings.xml"
<resources>
    ...
    <string name="app_name">KmpStarter</string>
    ...
</resources>
```
just change the `KmpStarter` to your app name.

#### Package Name
``` kotlin title="androidApp/build.gradle.kts"
android {
    ...
    defaultConfig {
        ...
        applicationId = "com.kmpstarter"
        ...
    }
}
```
change the `com.kmpstarter` to your package name
### IOS
#### App Name
just use the xcode to change the app name:
![changing app name](../assets/ios-app-name.png)
#### Package Name
``` xcconfig title="iosApp/AppConfig.xcconfig"
PRODUCT_BUNDLE_IDENTIFIER=com.kmpstarter.nativeapp
```
change the `com.kmpstarter.nativeapp` to your package name
!!! warning "Warning"
    make sure to only change the package name through `AppConfig.xcconfig`, changing it through xcode UI might cause some issues

#### App Icon
You can create an app icon using <a href="https://developer.apple.com/icon-composer/" target="_blank">IconComposer</a>. With IconComposer you'll get Apple famous Liquid Glass effect as well.

##### Step 0
Create App Icon using IconComposer
![Exported Icons](../assets/ios-icon-0.png)
##### Step 1
Export The App Icon, copy the following settings to export all varients
![Exported Icons](../assets/ios-icon-1.png)
you'll see exported icons like this:
![Exported Icons](../assets/ios-icon-2.png)

##### Step 2
Open Xcode & goto `Assets` inside `iosApp` target.
![Exported Icons](../assets/ios-icon-3.png)
Drag all the coresponding icons to each varient block
like: 

- `Default -> Any Apperance`
- `Dark -> Dark Apperance`
- `Tinted -> Tinted Apperance`

![Exported Icons](../assets/ios-icon-4.png)

Done your iosApp icon has been changed!


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
