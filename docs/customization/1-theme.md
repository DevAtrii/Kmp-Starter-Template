---
comments: false
icon: lucide/palette
---

# Theming


## Changing Colors
You can change colors in `Color.kt` file.
``` kotlin title="composeApp/src/commonMain/kotlin/com/kmpstarter/theme/Color.kt"
/*Light Color Scheme*/
val primaryLight = Color(0xFF3B82F6)
val onPrimaryLight = Color(0xFFFFFFFF)
...

/*Dark Color Scheme*/
val primaryDark = Color(0xFF60A5FA)
val onPrimaryDark = Color(0xFF1E3A8A)
...
```

Changing all colors one by 1 would be tedious, so you can use <a href="https://material-foundation.github.io/material-theme-builder/" target="_blank">Material Theme Builder</a> to generate all the colors for you.

- Generate Theme
- Export Theme
- Copy the colors to `Color.kt` file.

## Changing Typography
You can customise typography inisde `Type.kt` file.
``` kotlin title="composeApp/src/commonMain/kotlin/com/kmpstarter/theme/Type.kt"

@Composable
fun getAppFontFamily() = FontFamily(
    ...
    Font(
        resource = Res.font.manrope_light,
        weight = FontWeight.Thin
    ),
    ...
)
```
if you want to change font family, just add your font files into `features/resources/src/commonMain/composeResources/font/` folder and update the `Type.kt` file.

``` txt title="features/resources/src/commonMain/composeResources/font/"
root
│
├─ features
│  └─ resources
│     └─ src
│        └─ commonMain
│           └─ composeResources
│              └─ font  ← 📌 Add your font files here
│                 ├─ YourFont-Regular.ttf
│                 ├─ YourFont-Medium.ttf
│                 └─ YourFont-Bold.ttf
│
└─ composeApp
   └─ src
      └─ commonMain
         └─ kotlin
            └─ com
               └─ kmpstarter
                  └─ theme
                     └─ Type.kt   ← Update font family here
```

## Changing Default Appearance
if you want to change default theme mode or dynamic colors you can do so in `ThemeDataStore.kt` file.

``` kotlin title="starter/core/src/commonMain/kotlin/com/kmpstarter/core/datastore/theme/ThemeDataStore.kt"
class ThemeDataStore(
    ...
) {
    companion object {
        ...
        val DEFAULT_THEME_MODE = ThemeMode.LIGHT // change theme here
        const val DEFAULT_DYNAMIC_COLOR_SCHEME = false // change dynamic color scheme here
    }
    ...
}
```
- `ThemeMode.LIGHT`  
- `ThemeMode.DARK`  
- `ThemeMode.SYSTEM`  

## ThemeMode
Compose by default provide `isSystemInDarkTheme()` function to check if the system is in dark mode or not, but since we are using datastore to store the theme mode, we need to use `LocalThemeMode` enum to handle the theme mode.

``` kotlin
@Composable
fun StarterComposable() {
    val themeMode = LocalThemeMode.current
    val isDark = themeMode.isInDarkTheme(isSystemInDarkTheme())
}
```


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
