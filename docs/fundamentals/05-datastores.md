---
comments: false
icon: lucide/database-backup
---

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

**Recommended:** use **DataStore delegates** from `AppDataStore` inside a ViewModel (or any class). Skip raw `Preferences.Key` + `edit`/`map` boilerplate.

### DataStore Delegates

```kotlin title="ViewModel — DataStore Delegates"
class AuthViewModel(
    appDataStore: AppDataStore,
) : ViewModel() {
    private val accessToken = appDataStore.stringDataStore("access_token")
    private val loginCount = appDataStore.intDataStore("login_count", default = 0)
    private val isLoggedIn = appDataStore.booleanDataStore("is_logged_in", default = false)

    fun login(token: String) {
        viewModelScope.launch {
            accessToken.set(token)
            isLoggedIn.set(true)
            loginCount.set((loginCount.get() ?: 0) + 1)
        }
    }

    fun logout() {
        viewModelScope.launch {
            accessToken.clear()
            isLoggedIn.set(false)
        }
    }

    // Observe
    val tokenFlow = accessToken.flow
}
```

Primitive factories on `AppDataStore`:

| Factory | Type |
| :--- | :--- |
| `stringDataStore` | `String` |
| `intDataStore` | `Int` |
| `longDataStore` | `Long` |
| `booleanDataStore` | `Boolean` |
| `floatDataStore` | `Float` |
| `doubleDataStore` | `Double` |
| `stringSetDataStore` | `Set<String>` |
| `byteArrayDataStore` | `ByteArray` |

API on each delegate: `flow`, `get()`, `set(value)`, `clear()`. Pass `null` to `set` → key removed.

### Serializable objects

Store any `@Serializable` type as JSON:

```kotlin title="ViewModel — Serializable DataStore Delegate"
@Serializable
data class Settings(val darkMode: Boolean = false)

class SettingsViewModel(
    appDataStore: AppDataStore,
) : ViewModel() {
    private val settings = appDataStore.serializableDataStore(
        name = "settings",
        default = Settings(),
    )

    val settingsFlow = settings.flow

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settings.set(Settings(darkMode = enabled))
        }
    }
}
```

### Raw DataStore (not recommended)

Only if you need custom key logic beyond delegates:

```kotlin title="Raw DataStore (legacy style)"
class AuthViewModel(
    appDataStore: AppDataStore,
) : ViewModel() {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    private val dataStore = appDataStore.dataStore

    val accessTokenFlow = dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY]
    }

    fun setAccessToken(token: String?) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                if (token == null) prefs.remove(ACCESS_TOKEN_KEY)
                else prefs[ACCESS_TOKEN_KEY] = token
            }
        }
    }
}
```

!!! tip "Prefer delegates"
    Non-Compose code → `stringDataStore` / `intDataStore` / `serializableDataStore` etc. Less boilerplate, same behavior.

!!! note "Note"
    Inject `AppDataStore` into your ViewModel via Koin (it is a singleton).



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
