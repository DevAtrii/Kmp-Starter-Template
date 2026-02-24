# Remote Config

The Starter Template includes a **type-safe Remote Config system** built using Clean Architecture.

It allows you to:

* Define strongly typed keys
* Provide safe defaults
* Deserialize custom JSON objects
* Use values in ViewModels or Compose
* Replace Firebase with your own implementation

---

## Keys


```kotlin title="features/remote_config/domain/.../RemoteConfigKeys.kt" linenums="1"
@Serializable
data class PromoConfig(
    val isEnabled: Boolean = false,
    val discountPercentage: Int = 0,
)

sealed class RemoteConfigKeys<T>(
    val key: String,
    open val defaultValue: T,
    val serializer: KSerializer<T>? = null,
) {

    data class ShowAds(
        override val defaultValue: Boolean = false,
    ) : RemoteConfigKeys<Boolean>(
        key = "show_ads",
        defaultValue = defaultValue
    )

    data class WelcomeText(
        override val defaultValue: String = "Welcome to KMP Starter",
    ) : RemoteConfigKeys<String>(
        key = "welcome_text",
        defaultValue = defaultValue
    )

    data class Promo(
        override val defaultValue: PromoConfig = PromoConfig(),
    ) : RemoteConfigKeys<PromoConfig>(
        key = "promo_config",
        defaultValue = defaultValue,
        serializer = PromoConfig.serializer()
    )
}
```

!!! note
    - Keys must exactly match Firebase console keys.
    - Always provide safe default values.
    - Use a serializer for custom objects.
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


### Example – Enable/Disable Ads
Let’s say you want to remotely enable/disable ads.

#### ViewModel

```kotlin linenums="1"
class HomeViewModel(
    private val getConfig: GetConfigLogic
) : ViewModel() {

    private val _showAds = MutableStateFlow(false)
    val showAds: StateFlow<Boolean> = _showAds

    init {
        viewModelScope.launch {
            _showAds.value =
                getConfig(RemoteConfigKeys.ShowAds())
        }
    }
}
```

!!! success ""
    Now your UI can decide whether to show ads dynamically without releasing a new version.

---

### Example – Running a Promotion

Suppose you want to run a limited-time discount campaign.

```json title="Firebase Remote Config" linenums="1"
{
  "isEnabled": true,
  "discountPercentage": 25
}
```

#### ViewModel

```kotlin
class PromoViewModel(
    private val getConfig: GetConfigLogic
) : ViewModel() {

    private val _promo = MutableStateFlow(PromoConfig())
    val promo: StateFlow<PromoConfig> = _promo

    init {
        viewModelScope.launch {
            _promo.value =
                getConfig(RemoteConfigKeys.Promo())
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

You can directly use Remote Config inside Compose:

```kotlin linenums="1"
@Composable
fun HomeScreen() {
    val showAds by rememberRemoteConfig(
        key = RemoteConfigKeys.ShowAds()
    )

    val promo by rememberRemoteConfig(
        key = RemoteConfigKeys.Promo()
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
    * Updates automatically after fetch
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

Here, `RemoteConfigValue` is a typealias:

```kotlin
typealias RemoteConfigValue = String
```

---

### Local Implementation Example

```kotlin linenums="1"
class LocalRemoteConfigRepository : RemoteConfigRepository {

    private val fakeStorage = mapOf(
        "show_ads" to "true",
        "welcome_text" to "Welcome from Local Config",
        "promo_config" to """
            {"isEnabled":true,"discountPercentage":30}
        """.trimIndent()
    )

    override fun get(key: String): RemoteConfigValue {
        return fakeStorage[key] ?: ""
    }
}
```

Bind it in Koin:

```kotlin linenums="1" title="features/remote_config/data/commonMain/.../di/Module.kt"
single<RemoteConfigRepository> {
    LocalRemoteConfigRepository()
}
```

!!! note ""
    This is useful for:

    - Unit testing
    - Desktop builds
    - CI pipelines
    - Offline development

---

!!! abstract "Summary"

    * Define keys in `RemoteConfigKeys.kt`
    * Always provide default values
    * Use `GetConfigLogic` in ViewModels
    * Use `rememberRemoteConfig` in Compose
    * Replace repository if needed
