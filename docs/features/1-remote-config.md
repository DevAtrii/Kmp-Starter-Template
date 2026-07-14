---
comments: false
icon: lucide/bolt
---

# Remote Config

The Starter Template includes a **type-safe Remote Config system** built with Clean Architecture.

It allows you to:

* Define strongly typed keys via `RemoteConfigKey`
* Provide safe defaults
* Deserialize custom JSON objects
* Use values in ViewModels or Compose
* Replace Firebase with your own implementation

---

## Architecture

| Piece | Role |
| :--- | :--- |
| `RemoteConfigKey<T>` | Base contract for a single remote config entry (key + default + optional serializer) |
| Your sealed keys | App-owned hierarchy of all config keys (e.g. `ConfigKeys`) |
| `GetConfigLogic` | Reads a typed value for a `RemoteConfigKey` |
| `rememberRemoteConfig` | Compose helper that observes a key |

Recommended approach: keep **one sealed key hierarchy** in your app/core module so every screen reads config through the same typed models.

---

## 1. Define Keys

Extend `RemoteConfigKey`. Example:

```kotlin title="Your app / core module"
@Serializable
data class PromoConfig(
    val isEnabled: Boolean = false,
    val discountPercentage: Int = 0,
)

sealed class ConfigKeys<T>(
    key: String,
    defaultValue: T,
    serializer: KSerializer<T>? = null,
) : RemoteConfigKey<T>(
    key = key,
    defaultValue = defaultValue,
    serializer = serializer,
) {

    data class ShowAds(
        override val defaultValue: Boolean = false,
    ) : RemoteConfigKey<Boolean>(
        key = "show_ads",
        defaultValue = defaultValue,
    )

    data class WelcomeText(
        override val defaultValue: String = "Welcome to KMP Starter",
    ) : RemoteConfigKey<String>(
        key = "welcome_text",
        defaultValue = defaultValue,
    )

    data class Promo(
        override val defaultValue: PromoConfig = PromoConfig(),
    ) : RemoteConfigKey<PromoConfig>(
        key = "promo_config",
        defaultValue = defaultValue,
        serializer = PromoConfig.serializer(),
    )

    data class MinimumVersion(
        override val defaultValue: Int = 36,
    ) : RemoteConfigKey<Int>(
        key = "minimum_version",
        defaultValue = defaultValue,
    )
}
```

!!! note
    - Override `defaultValue` so defaults stay at the call site.
    - Keys must exactly match Firebase console keys.
    - Always provide safe defaults.
    - Pass `serializer` for `@Serializable` custom objects.

??? abstract "Initialization"
    Remote Config must be initialized at app startup:

    ```kotlin linenums="1"
    RemoteConfig.init(
        minimumFetchInterval = 1.hours,
        fetchTimeout = 2.minutes,
    )
    ```

    This is already called in the entry point by default.

    !!! warning
        Make sure Firebase is integrated correctly because the default implementation uses Firebase Remote Config internally.

---

## 2. Use in ViewModel

Inject `GetConfigLogic` and pass a key instance:

### Enable/Disable Ads

```kotlin linenums="1"
class HomeViewModel(
    private val getConfig: GetConfigLogic,
) : ViewModel() {

    private val _showAds = MutableStateFlow(false)
    val showAds: StateFlow<Boolean> = _showAds

    init {
        viewModelScope.launch {
            _showAds.value = getConfig(ConfigKeys.ShowAds())
        }
    }
}
```

!!! success ""
    UI can show/hide ads remotely without a new release.

### Running a Promotion

Firebase JSON for `promo_config`:

```json title="Firebase Remote Config" linenums="1"
{
  "isEnabled": true,
  "discountPercentage": 25
}
```

```kotlin
class PromoViewModel(
    private val getConfig: GetConfigLogic,
) : ViewModel() {

    private val _promo = MutableStateFlow(PromoConfig())
    val promo: StateFlow<PromoConfig> = _promo

    init {
        viewModelScope.launch {
            _promo.value = getConfig(ConfigKeys.Promo())
        }
    }
}
```

!!! success ""
    Now you can:

    * Enable/disable the campaign remotely
    * Change discount percentage
    * Avoid app updates for marketing changes

---

## Compose Usage

```kotlin linenums="1"
@Composable
fun HomeScreen() {
    val showAds by rememberRemoteConfig(
        key = ConfigKeys.ShowAds(),
    )

    val promo by rememberRemoteConfig(
        key = ConfigKeys.Promo(),
    )

    Column {
        if (showAds) {
            AdBanner()
        }

        if (promo.isEnabled) {
            Text("Discount: ${promo.discountPercentage}%")
        }
    }
}
```

!!! success ""
    * Starts with default value
    * Updates after fetch
    * Supports primitives & custom types

---

## Custom Implementation

If you don’t want Firebase (or for testing), create your own repository in:

```
features/remote_config/data/
```

### Interface

```kotlin
interface RemoteConfigRepository {
    fun get(key: String): RemoteConfigValue
}
```

`RemoteConfigValue` is a typealias for `String`.

### Local Implementation Example

```kotlin linenums="1"
class LocalRemoteConfigRepository : RemoteConfigRepository {

    private val fakeStorage = mapOf(
        "show_ads" to "true",
        "welcome_text" to "Welcome from Local Config",
        "promo_config" to """
            {"isEnabled":true,"discountPercentage":30}
        """.trimIndent(),
    )

    override fun get(key: String): RemoteConfigValue {
        return fakeStorage[key] ?: ""
    }
}
```

Bind it in Koin:

```kotlin linenums="1" title="features/remote_config/data/.../di/Module.kt"
single<RemoteConfigRepository> {
    LocalRemoteConfigRepository()
}
```

!!! note ""
    Useful for:

    - Unit testing
    - Desktop builds
    - CI pipelines
    - Offline development

---

!!! abstract "Summary"

    * Define keys as `RemoteConfigKey` subclasses (e.g. sealed `ConfigKeys`)
    * Always provide default values
    * Use `GetConfigLogic` in ViewModels
    * Use `rememberRemoteConfig` in Compose
    * Replace `RemoteConfigRepository` if needed
