# DataStores (Local Storage)

The Starter Template provides a unified **DataStore** setup for local storage in Kotlin Multiplatform projects.
You can use it in both **Compose** and **non-Compose** code for storing simple preferences like `String`, `Int`, `Boolean`, `Long`, or `Set<String>`.

---

## Compose-Friendly Usage

### Immutable State

Use `rememberDataStoreValue` to read values as **immutable state**:

```kotlin title="Compose Example - Immutable" 
val themeMode by rememberStringDataStore("theme_mode", "LIGHT")
val isDynamic by rememberBooleanDataStore("dynamic_colors", false)
```

Other helpers:

```kotlin  
val counter by rememberIntDataStore("counter", 0)
val tags by rememberStringSetDataStore("tags", emptySet())
```

These automatically collect DataStore values as a `State<T>` that updates on changes.

---

### Mutable State

Use `rememberMutableDataStoreState` to **read and write values**:

```kotlin title="Compose Example - Mutable"  
var themeMode by rememberMutableStringDataStore("theme_mode", "LIGHT")
var isDynamic by rememberMutableBooleanDataStore("dynamic_colors", false)

// Update value
themeMode = "DARK"
isDynamic = true

// Remove value by setting it to null
themeMode = null
```

Other helpers:

```kotlin  
var counter by rememberMutableIntDataStore("counter", 0)
var lastSync by rememberMutableLongDataStore("lastSync", 0L)
```

> **Note:** In Compose, setting a mutable DataStore state to `null` will **remove the key** from DataStore.

---

## Non-Compose Usage

```kotlin title="Non-Compose Example" 
class UserDataStore(appDataStore: AppDataStore) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    private val dataStore = appDataStore.dataStore

    val accessTokenFlow = dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY]
    }

    suspend fun setAccessToken(token: String?) {
        dataStore.edit { prefs ->
            if (token == null) prefs.remove(ACCESS_TOKEN_KEY)
            else prefs[ACCESS_TOKEN_KEY] = token
        }
    }
}
```

!!! note "Note"
    `AppDataStore` is a singleton class that is injected into the `UserDataStore` class automatically.



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
