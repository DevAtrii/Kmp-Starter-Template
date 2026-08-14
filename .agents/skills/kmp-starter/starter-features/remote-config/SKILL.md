---
name: kmp-starter-feature-remote-config
description: The KMP Starter Template type-safe Remote Config system — RemoteConfigKey sealed keys, GetConfigLogic, rememberRemoteConfig, safe defaults, and swapping Firebase for a local implementation.
author: DevAtrii
license: MIT

---

# Remote Config

Type-safe Remote Config built with Clean Architecture. Define typed keys, provide safe defaults, deserialize custom JSON, use values in ViewModels or Compose. Firebase-backed by default; swap `RemoteConfigRepository` for a local/test impl.

## Where things live

| Piece | Module | Path |
| --- | --- | --- |
| `RemoteConfigKey<T>` (base) | remote_config domain | `features/remote_config/domain/.../RemoteConfigKey.kt` |
| `GetConfigLogic` | remote_config domain | `features/remote_config/domain/.../logics/GetConfigLogic.kt` |
| `RemoteConfigRepository` (interface) | remote_config domain | `features/remote_config/domain/.../RemoteConfigRepository.kt` |
| `FirebaseRemoteConfigRepository` | remote_config data | `features/remote_config/data/.../FirebaseRemoteConfigRepository.kt` |
| `rememberRemoteConfig` | remote_config presentation | `features/remote_config/presentation/.../RememberConfig.kt` |

## 1. Define keys (one sealed hierarchy)

Extend `RemoteConfigKey<T>`. Keep one sealed `ConfigKeys` hierarchy per app:

```kotlin
@Serializable
data class PromoConfig(
    val isEnabled: Boolean = false,
    val discountPercentage: Int = 0,
)

sealed class ConfigKeys<T>(
    key: String,
    defaultValue: T,
    serializer: KSerializer<T>? = null,
) : RemoteConfigKey<T>(key = key, defaultValue = defaultValue, serializer = serializer) {

    data class ShowAds(
        override val defaultValue: Boolean = false,
    ) : RemoteConfigKey<Boolean>(key = "show_ads", defaultValue = defaultValue)

    data class WelcomeText(
        override val defaultValue: String = "Welcome to KMP Starter",
    ) : RemoteConfigKey<String>(key = "welcome_text", defaultValue = defaultValue)

    data class Promo(
        override val defaultValue: PromoConfig = PromoConfig(),
    ) : RemoteConfigKey<PromoConfig>(
        key = "promo_config",
        defaultValue = defaultValue,
        serializer = PromoConfig.serializer(),
    )

    data class MinimumVersion(
        override val defaultValue: Int = 36,
    ) : RemoteConfigKey<Int>(key = "minimum_version", defaultValue = defaultValue)
}
```

Rules:
- Override `defaultValue` so defaults stay at the call site.
- Keys must exactly match Firebase console keys.
- Always provide safe defaults.
- Pass `serializer` for `@Serializable` custom objects (primitives don't need one).

## 2. Use in a ViewModel

Inject `GetConfigLogic` and invoke it with a key instance (it's an `operator fun invoke`):

```kotlin
class HomeViewModel(
    private val getConfig: GetConfigLogic,
) : MviViewModel<HomeState, HomeActions, HomeEvents>() {

    init {
        viewModelScope.launch {
            // getConfig(ConfigKeys.ShowAds()) returns Boolean
            _state.update { it.copy(showAds = getConfig(ConfigKeys.ShowAds())) }
        }
    }
}
```

`GetConfigLogic` waits for initialization; on failure it returns the key's default. Primitives (`Boolean`/`Int`/`Long`/`Double`/`Float`/`String`) are coerced from the raw string; custom objects are decoded via their `serializer`.

## 3. Use in Compose

```kotlin
@Composable
fun HomeScreen() {
    val showAds by rememberRemoteConfig(key = ConfigKeys.ShowAds())
    val promo by rememberRemoteConfig(key = ConfigKeys.Promo())

    if (showAds) AdBanner()
    if (promo.isEnabled) Text("Discount: ${promo.discountPercentage}%")
}
```

Starts with the default, updates after fetch. `rememberRemoteConfig` uses `koinInject()` internally.

## 4. Initialization

Already wired in the app entry point by default; requires Firebase. `RemoteConfig.init(minimumFetchInterval, fetchTimeout)` controls fetch cadence.

## 5. Swap the provider (local/test)

Implement `RemoteConfigRepository` in `features/remote_config/data/`, bind in `remoteConfigDataModule`:

```kotlin
class LocalRemoteConfigRepository : RemoteConfigRepository {
    private val storage = mapOf(
        "show_ads" to "true",
        "promo_config" to """{"isEnabled":true,"discountPercentage":30}""",
    )
    override fun get(key: String): RemoteConfigValue = storage[key] ?: ""
}
```

Useful for unit tests, desktop builds, CI, offline dev.

## Rules

- Do **not** introduce a parallel config system.
- One sealed key hierarchy; every screen reads config through the same typed models.
- Register `remoteConfigDataModule` + `remoteConfigDomainModule` in `InitKoin` (see koin skill).

## Reference

- Docs: `https://starter.atherio.dev/features/` → Remote Config
- Source: `features/remote_config/*`
