---
comments: false
icon: lucide/globe
---

# Multiple Languages

The Starter Template supports **multiple languages** via the centralized `resources` module.
You can add new languages and manage them consistently across the app.

---

## Adding a New Language

Languages are defined in:

```
features/resources/src/commonMain/.../locale/StarterLocales.kt
```

Example: continuing with **French**:

```kotlin title="StarterLocales.kt"
enum class StarterLocales(
    val emoji: String,
    val langCode: String,
    val displayName: StringResource,
    val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
) {
    ...
    FRANCE("🇫🇷", "fr", Res.string.lang_fr);
    ...
}
```

Also add the display name in your strings:

```xml title="features/resources/src/commonMain/composeResources/values/strings.xml"
<string name="lang_fr">Français</string>
```

Then create a folder for the language:

```
features/resources/src/commonMain/composeResources/values-fr/strings.xml
```

And add all localized strings there.

For a full list of language codes, see the <a href="https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes" target="_blank">List of ISO 639 language codes</a> page.

---

## Using Locale Selector

### Prebuilt Dropdown

You can add a language selector in your Compose UI easily using the prebuilt dropdown:

```kotlin title="Usage Example"
CupertinoSection(title = "Locale") {
    LocaleSelectorDropdown(isLast = true)
}
```

This automatically displays all supported locales, and persists the selection using DataStore.

---

### Custom Locale Selector

You can also create a custom selector with your own UI:

```kotlin title="Custom Selector Example"
@Composable
fun CustomLocaleSelector() {
    LocaleSelectorContainer { args ->
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.wrapContentSize()) {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }  
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = args.currentLocale.emoji)
                Text(text = args.currentLocale.displayName.toActualString(), modifier = Modifier.padding(start = 8.dp))
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }  
            ) {
                StarterLocales.entries.forEach { locale ->
                    DropdownMenuItem(
                        text = { 
                            Text("${locale.emoji} ${locale.displayName.toActualString()}") 
                        },
                        onClick = {
                            args.onLocaleSelected(locale)  
                            expanded = false            
                        }
                    )
                }
            }
        }
    }
}
```

`LocaleSelectorContainer` lets you use custom layout for your language selector while still persisting the choice.

---

## Advance

Use these helpers to persist the selected locale:

```kotlin title="LocaleDataStore.kt"
internal val localeKey = stringPreferencesKey("app_locale")

@Composable
fun rememberMutableStarterLocaleDataStore(default: StarterLocales?): MutableState<String?> { ... }

@Composable
fun rememberStarterActiveLocale(overrideDefault: StarterLocales? = null): StarterLocales { ... }
```

* `rememberMutableStarterLocaleDataStore` → mutable access to store the selected language
* `rememberStarterLocaleDataStore` → read-only state
* `rememberStarterActiveLocale` → returns the active locale using user preference, fallback, or system default

---

This setup allows you to **add new languages easily**, use either a **prebuilt or custom dropdown selector**, and persist the selected locale across app launches.


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
