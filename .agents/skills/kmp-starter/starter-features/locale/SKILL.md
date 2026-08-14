---
name: kmp-starter-feature-locale
description: The KMP Starter Template localization system — StarterLocales, StarterLocale, LocaleProvider, and the locale selector UI in features/locale.
author: DevAtrii
license: MIT

---

# Locale (Localization)

In-app language switching with RTL/LTR support. Module: `features/locale`.

## Where things live

| Piece | Path |
| --- | --- |
| `StarterLocales` (registry) / `StarterLocale` | `features/locale/src/commonMain/.../StarterLocale.kt` |
| `LocaleProvider` | `features/locale/src/commonMain/.../LocaleProvider.kt` |
| `LocalAppLocale` (expect/actual) | `.../LocalAppLocale.kt` (+ `.android.kt`, `.ios.kt`) |
| `rememberStarterActiveLocale` / `rememberStarterLocaleDataStore` | `.../LocaleProvider.kt` |
| `LocaleSelectorDropdown` / `LocaleSelectorContainer` | `.../ui/LocaleSelectorDropdown.kt`, `LocaleSelectorContainer.kt` |

## Data model

```kotlin
data class StarterLocale(
    val emoji: String,                 // e.g. "🇵🇰"
    val langCode: String,              // "en", "ur", "es", "es_AR"
    val displayName: StringResource,   // localized label (Res.string.lang_*)
    val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
)
```

`StarterLocales` is a registry:

```kotlin
object StarterLocales {
    val DEFAULT = StarterLocale("🇺🇸", "en", Res.string.lang_en)
    val locales get() = _locales.toSet()
    fun add(locale: StarterLocale)
    fun add(locales: Iterable<StarterLocale>)
    fun findBy(langCode: String): StarterLocale?
}
```

## Register locales + provide them

In `App.kt` (composeApp), define supported locales and wrap the app with `LocaleProvider`:

```kotlin
val supportedLocales = setOf(
    StarterLocale("🇵🇰", "ur", Res.string.lang_ur, LayoutDirection.Rtl),
    StarterLocale("🇮🇳", "hi", Res.string.lang_hi),
    StarterLocale("🇪🇸", "es", Res.string.lang_es),
)

LocaleProvider(
    locales = supportedLocales,
    overrideDefault = StarterLocales.findBy("en"),
) {
    // app content — provides LocalAppLocale + LocalLayoutDirection
}
```

`LocaleProvider` resolves the active locale with priority: **User preference → `overrideDefault` → System locale → `DEFAULT`**. It provides both `LocalAppLocale` (lang code) and `LocalLayoutDirection` (RTL/LTR) to the tree. The selection persists to DataStore.

## Locale selector UI

Drop in `LocaleSelectorDropdown` (Cupertino-style) where users pick a language:

```kotlin
LocaleSelectorDropdown(
    label = Res.string.choose_language,
    isLast = true,
)
```

`LocaleSelectorContainer` wraps the stateful selection; `LocaleSelectorDropdown` lists `StarterLocales.locales` and writes the choice back to the DataStore.

## Rules

- Add languages as `Res.string.lang_*` resources + `StarterLocale` entries (see resources-theme skill).
- Use `LocaleProvider` for resolution — don't manage locale state manually.
- Respect `layoutDirection` for RTL languages.
- No parallel localization system.

## Reference

- Docs: `https://starter.atherio.dev/fundamentals/07-multiple-languages/`
- Source: `features/locale/*`
