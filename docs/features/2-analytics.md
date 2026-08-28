---
comments: false
icon: lucide/chart-area
---

# Analytics

The Starter Template provides a **modular, provider-agnostic analytics system** built with Clean Architecture. You track events through type-safe `AppEvent` models while keeping the provider (Mixpanel today) swappable in the data layer.

---

## Setup

1. Open the constants file:

```kotlin title="composeApp/src/commonMain/.../core/AppConstants.kt" linenums="1"
object AppConstants {
    const val MIXPANEL_API_TOKEN = "add-your-mixpanel-token-here"
}
```

2. Replace `"add-your-mixpanel-token-here"` with your Mixpanel project token.

3. Init **after** Koin. Each backend has its own `init*`. Then pass only the providers you want:

```kotlin title="composeApp/src/commonMain/.../core/InitKmpApp.kt"
initKoin(config = koinConfig)

initMixPanel(apiKey = AppConstants.MIXPANEL_API_TOKEN) {
    logging = platform.debug
}
initAnalytics {
    providers(
        MixPanelAnalyticsScope.getProvider(),
        // FirebaseAnalyticsScope.getProvider(),
    )
}
```

Omit Mixpanel from `providers(...)` (and skip `initMixPanel` / `analyticsDataModule`) if you do not want it. A library consumer has full control.

!!! info
    See the [official Mixpanel docs](https://developer.mixpanel.com/docs/quickstart) for generating your token.

---

## Architecture

| Piece | Role |
| :--- | :--- |
| `AppEvent` | Base analytics event (`event` name + optional `properties`) in **analytics domain** |
| `AppEvents` | Your sealed hierarchy of all app events (starter ships one in **core domain**) |
| `EventsTracker` | Interface that sends events to the active provider set |
| `AnalyticsProvider` | One concrete backend. Mixpanel lives in **analytics data**; Firebase would be a future data module |
| `Analytics` / `AnalyticsProviderIds` | Facade + well-known ids in **analytics domain**. Data modules are optional |
| `analyticsDomainModule` | Binds the router as `Analytics` + `EventsTracker` from [initAnalytics] |
| `analyticsDataModule` | Mixpanel SDK wiring. You still pass Mixpanel in `initAnalytics { providers(...) }` |

Recommended approach: keep **one sealed `AppEvents` hierarchy** (core/shared module) so every screen tracks through the same typed models.

---

## 1. Define Events

Extend `AppEvent` with a sealed hierarchy. Starter already provides `AppEvents` in core domain:

```kotlin title="features/core/domain/.../AppEvents.kt"
sealed class AppEvents(
    event: String,
    properties: Map<String, Any>? = null,
) : AppEvent(event, properties) {

    constructor(event: String) : this(event = event, properties = null)

    constructor(
        event: String,
        pair: Pair<String, Any>? = null,
    ) : this(
        event = event,
        properties = if (pair != null) mapOf(pair) else mapOf(),
    )

    data object DummyEvent : AppEvents(
        event = "dummy_event",
    )

    data class TrackTrafficSource(
        val source: String,
    ) : AppEvents(
        event = "onboarding_traffic_source",
        pair = "traffic_source" to source,
    )

    data class OnPurchaseSuccess(
        val productId: String,
    ) : AppEvents(
        event = "purchase_success",
        pair = "product_id" to productId,
    )
}
```

Add a new event as another nested type:

```kotlin
data class SignInSuccess(
    val userId: String,
) : AppEvents(
    event = "sign_in_success",
    pair = "user_id" to userId,
)
```

For multiple properties, pass `properties = mapOf(...)` instead of `pair`.

!!! note
    - Prefer `snake_case` event names.
    - Keep all events in one sealed class for autocomplete and consistency.
    - No need to add methods on `EventsTracker` per event — the type **is** the event.

---

## 2. Track in ViewModel

Inject `EventsTracker` and call `track` with an `AppEvents` instance:

```kotlin title="SignInViewModel.kt" linenums="1"
class SignInViewModel(
    private val eventsTracker: EventsTracker,
) : ViewModel() {

    fun onSignIn(userId: String) {
        viewModelScope.launch {
            eventsTracker.track(
                event = AppEvents.SignInSuccess(userId = userId),
            )
        }
    }
}
```

Another example from onboarding:

```kotlin
eventsTracker.track(
    event = AppEvents.TrackTrafficSource(
        source = selectedTrafficSource ?: "--",
    ),
)
```

!!! note
    - Keep analytics calls in the presentation layer.
    - ViewModel is the best place.

You can still call the string overloads (`track(event)`, `track(event, pair)`, `track(event, properties)`) when needed, but **typed `AppEvent` is preferred**.

Koin binds the same router as both `Analytics` and `EventsTracker`. ViewModels can keep injecting `EventsTracker`. Inject `Analytics` only when you need lookup, combining, or runtime swaps.

---

## Multiple providers

Starter ships Mixpanel as an optional **data** module. Routing lives in **domain**, so ViewModels only need `analytics.domain`. All registered providers start **active**.

### Look up one provider

```kotlin
class SignInViewModel(
    private val analytics: Analytics,
) : ViewModel() {

    fun onSignIn(userId: String) {
        viewModelScope.launch {
            analytics.provider(AnalyticsProviderIds.Mixpanel).track(
                event = AppEvents.SignInSuccess(userId = userId),
            )
        }
    }
}
```

`provider(...)` bypasses global routing. Unknown ids throw `IllegalArgumentException`.

### Combine providers

```kotlin
val mixpanelAndFirebase = analytics.combine(
    AnalyticsProviderIds.Mixpanel,
    AnalyticsProviderId("firebase"),
)
mixpanelAndFirebase.track(AppEvents.DummyEvent)
```

`combine` returns a **fixed** composite. Later `setActiveProviders` does not change it. Duplicate ids fail fast. Empty `combine()` is a no-op tracker.

### Swap at runtime

```kotlin
analytics.setActiveProviders(
    AnalyticsProviderIds.Mixpanel,
    AnalyticsProviderId("firebase"),
)
// existing EventsTracker injections now fan out to both

analytics.setActiveProviders(AnalyticsProviderId("firebase"))
// same instance now routes to Firebase only

analytics.setActiveProviders()
// routed tracking disabled; SDKs stay initialized
```

`isEnabled` is true when **any** active provider is enabled. `hasOptedIn` is true when **every** active provider has opted in (empty set → `true`).

---

## Adding a custom provider

Implement `AnalyticsProvider` in a **data** module (Mixpanel is `analytics/data`; Firebase would be a separate data module). Give it its own `init*` if the SDK needs a token. Pass the instance in `initAnalytics` — do not auto-bind it as the app-wide `EventsTracker`.

```kotlin linenums="1"
class FirebaseEventsTracker : AnalyticsProvider {

    override val id = AnalyticsProviderId("firebase")
    // ... EventsTracker methods
}

fun initFirebaseAnalytics(...) { /* SDK init */ }

fun FirebaseAnalyticsScope.getProvider(): AnalyticsProvider = FirebaseEventsTracker()
```

```kotlin
initFirebaseAnalytics(...)
initMixPanel(apiKey = token) { logging = platform.debug }
initAnalytics {
    providers(
        MixPanelAnalyticsScope.getProvider(),
        FirebaseAnalyticsScope.getProvider(),
    )
}
```

!!! note
    - Domain layer, ViewModels, and Compose code remain unchanged.
    - Switching or combining providers does not require rewriting event definitions.
    - Starter does not ship a Firebase Analytics SDK adapter; the class above is the extension point.

---

??? abstract "Migration"
    Previously Mixpanel was bound as `EventsTracker` in Koin. You now **opt in** by passing providers to `initAnalytics`. ViewModels stay on `EventsTracker` — no screen changes.

    **1. Register domain module**

    Add `analyticsDomainModule` in `initKoin`. Keep `analyticsDataModule` only if you still use Mixpanel:

    ```kotlin title="composeApp/.../core/di/InitKoin.kt"
    analyticsDomainModule,
    analyticsDataModule,
    ```

    **2. Pass providers after Koin**

    Keep `initMixPanel`. Then tell analytics which backends to use:

    ```kotlin title="composeApp/.../core/InitKmpApp.kt"
    initMixPanel(apiKey = AppConstants.MIXPANEL_API_TOKEN) {
        logging = platform.debug
    }
    initAnalytics {
        providers(
            MixPanelAnalyticsScope.getProvider(),
        )
    }
    ```

    Omit Mixpanel from `providers(...)` (and skip `initMixPanel` / `analyticsDataModule`) if you do not want it.

    **3. Custom backends**

    Do not replace `EventsTracker` in Koin. Implement `AnalyticsProvider`, init the SDK yourself, pass it in `initAnalytics`.

    !!! warning
        Call `initAnalytics` after `initKoin` and after each provider's `init*`. `MixPanelAnalyticsScope.getProvider()` needs Koin plus a Mixpanel token.
