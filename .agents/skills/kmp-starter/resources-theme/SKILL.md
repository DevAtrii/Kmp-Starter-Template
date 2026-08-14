---
name: kmp-starter-resources-theme
description: Resources and theming on the KMP Starter Template — centralized resources module, string externalization, localization, theme via ThemeDataStore, and colors/typography.
author: DevAtrii
license: MIT

---

# Resources & Theme

## Centralized resources

Resources live in `features/resources/src/commonMain/composeResources/` (`values/strings.xml`, `drawable/`, `font/`, `files/`). Add the dependency `implementation(projects.features.resources)` to use them.

Always use resource files. Never hardcode UI strings or colors.

After adding/removing a resource, regenerate the typed accessors with the Gradle task:

```
./gradlew :features:resources:generateAccessors
```

This produces `Res.string.*`, `Res.drawable.*`, etc. If `Res.*` doesn't resolve after adding a resource, run this task first.

## String conversion

- In Composables: `Res.string.foo.toActualString()` (or `toActualString(args)`).
- In ViewModels: `getString(...)`.

Import `toActualString` from `com.kmpstarter.ui_utils.resources`.

## Localization (multiple languages)

- Languages defined in `features/resources/.../locale/StarterLocales.kt` (an enum: `langCode`, `displayName`, `layoutDirection`).
- Add the display name string + a `values-<code>/strings.xml` folder per language.
- Prebuilt dropdown: `LocaleSelectorDropdown(isLast = true)`; custom UI: `LocaleSelectorContainer { args -> ... }`.

## Theme

Use Starter's `ThemeDataStore` for theme mode. Do not use `isSystemInDarkTheme()` for application theme decisions.

- Read current mode via `LocalThemeMode.current` / collect `themeDataStore.themeMode`.
- `isAppInDarkTheme()` / `themeMode.isInDarkTheme(isSystemInDarkTheme())` determines dark.
- Defaults: `ThemeDataStore.DEFAULT_THEME_MODE` (`LIGHT`/`DARK`/`SYSTEM`) and `DEFAULT_DYNAMIC_COLOR_SCHEME`.

## Colors & typography

- Colors: `composeApp/.../theme/Color.kt` (light + dark schemes).
- Typography: `composeApp/.../theme/Type.kt`; font files in `features/resources/.../composeResources/font/`.

No hardcoded colors — reference the theme scheme.

## Reference

- Docs: `https://starter.atherio.dev/fundamentals/06-resources/`, `https://starter.atherio.dev/fundamentals/07-multiple-languages/`, `https://starter.atherio.dev/customization/1-theme/`
- Code: `starter/ui/utils/.../resources/StringResources.kt`
