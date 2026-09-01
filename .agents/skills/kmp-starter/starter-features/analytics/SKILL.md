---
name: kmp-starter-feature-analytics
description: The KMP Starter Template analytics system — AppEvent/EventsTracker, Analytics routing, Mixpanel + Firebase providers, combining providers, and runtime swaps.
author: DevAtrii
license: MIT

---

# Analytics

Modular, provider-agnostic analytics. Track type-safe `AppEvent` models. Routing lives in **domain**. You pass providers in `initAnalytics { providers(...) }` after each backend's own `init*`. Mixpanel and Firebase Analytics are opt-in.

Kotlin types are `StarterAnalyticsProvider` / `StarterAnalyticsProviderId` / `StarterAnalyticsProviderIds`. Swift still sees `AnalyticsProvider` via `@ObjCName`.

## Where things live

| Piece | Module | Path |
| --- | --- | --- |
| `AppEvent` (base) | analytics domain | `features/analytics/domain/.../AppEvent.kt` |
| `EventsTracker` / `Analytics` / `StarterAnalyticsProvider` | analytics domain | `features/analytics/domain/...` |
| `StarterAnalyticsProviderId` / `StarterAnalyticsProviderIds` | analytics domain | `features/analytics/domain/.../StarterAnalyticsProviderId.kt` |
| Router + `analyticsDomainModule` | analytics domain | `features/analytics/domain/.../AnalyticsRouter.kt` |
| Mixpanel `EventsTrackerImpl` + `analyticsDataModule` | analytics data | `features/analytics/data/...` |
| `FirebaseStarterAnalyticsProvider` + `analyticsFirebaseDataModule` | analytics data-firebase | `features/analytics/data-firebase/...` |
| `AppEvents` (sealed) | core domain | `features/core/domain/.../AppEvents.kt` |

## 1. Define events (one sealed hierarchy)

Keep **one** sealed `AppEvents` hierarchy in core domain. Each nested type **is** an event:

```kotlin
sealed class AppEvents(
    event: String,
    properties: Map<String, Any>? = null,
) : AppEvent(event, properties) {

    constructor(event: String) : this(event, properties = null)

    constructor(event: String, pair: Pair<String, Any>? = null) : this(
        event = event,
        properties = if (pair != null) mapOf(pair) else mapOf(),
    )

    data object DummyEvent : AppEvents(event = "dummy_event")

    data class OnPurchaseSuccess(
        val productId: String,
    ) : AppEvents(
        event = "purchase_success",
        pair = "product_id" to productId,
    )

    data class OnPurchaseFailure(
        val productId: String,
        val error: String,
    ) : AppEvents(
        event = "purchase_failure",
        properties = mapOf("product_id" to productId, "error" to error),
    )
}
```

Rules:
- Prefer `snake_case` event names.
- Single property → `pair`; multiple → `properties = mapOf(...)`.
- Do **not** add a method to `EventsTracker` per event — the type is the event.

## 2. Track from the ViewModel

Inject `EventsTracker`, call `track(...)` with an `AppEvents` instance:

```kotlin
class SignInViewModel(
    private val eventsTracker: EventsTracker,
) : MviViewModel<SignInState, SignInActions, SignInEvents>() {

    fun onSignIn(userId: String) {
        viewModelScope.launch {
            eventsTracker.track(
                event = AppEvents.SignInSuccess(userId = userId),
            )
            eventsTracker.setUserId(userId)
            eventsTracker.setUserProperty(key = "plan", value = "pro")
            eventsTracker.setUserProperties(
                values = mapOf("plan" to "pro", "locale" to "en"),
            )
        }
    }
}
```

Keep analytics calls in the presentation layer (ViewModel is best). Typed `AppEvent` is preferred over the string overloads `track(event)`, `track(event, pair)`, `track(event, properties)`.

`EventsTracker` also exposes `setUserId`, `setUserProperty` / `setUserProperties` (`String` keys + values), `optIn`/`optOut`/`toggleOptInOut`/`hasOptedIn`, `flush`, `reset`.

Koin binds the same router as `Analytics` and `EventsTracker`. Inject `Analytics` only for lookup, `combine`, or `setActiveProviders`.

## 3. Setup (Mixpanel token + Firebase)

Set the Mixpanel token in `composeApp/.../core/AppConstants.kt`. Firebase uses platform config (`google-services.json` / `GoogleService-Info.plist`), not an API token. Wire **after** Koin:

```kotlin
object AppConstants {
    const val MIXPANEL_API_TOKEN = "add-your-mixpanel-token-here"
}

initMixPanel(apiKey = AppConstants.MIXPANEL_API_TOKEN) {
    logging = platform.debug
}
initFirebaseAnalytics {
    enabled = true
    sessionTimeoutInterval = 30.minutes
    defaultEventParameters = mapOf("app_flavor" to "prod")
    analyticsStorage = FirebaseAnalytics.ConsentStatus.GRANTED
}
initAnalytics {
    providers(
        MixPanelAnalyticsScope.getProvider(),
        FirebaseAnalyticsScope.getProvider(),
    )
}
```

`initFirebaseAnalytics` options: `enabled`, `sessionTimeoutInterval`, `defaultEventParameters: Map<String, String>`, consent fields. **No** `userProperties` on init — use `setUserProperty` after `initAnalytics`.

Skip a backend (and its data module) if the consumer does not want it.

Lookups: `StarterAnalyticsProviderIds.Mixpanel` / `.Firebase`. Custom id: `StarterAnalyticsProviderId("posthog")`.

## 4. Multiple providers

All providers passed to `initAnalytics` start active. `setActiveProviders` reroutes the **same** injected instance; SDKs stay alive.

```kotlin
val analytics: Analytics = koinInject()

analytics.provider(StarterAnalyticsProviderIds.Mixpanel).track(AppEvents.DummyEvent)

analytics.combine(
    StarterAnalyticsProviderIds.Mixpanel,
    StarterAnalyticsProviderIds.Firebase,
).track(AppEvents.DummyEvent)

analytics.setActiveProviders(
    StarterAnalyticsProviderIds.Mixpanel,
    StarterAnalyticsProviderIds.Firebase,
)
analytics.setActiveProviders() // disable routed tracking
```

Add a backend by implementing `StarterAnalyticsProvider`, giving it its own `init*` if needed, and passing it in `initAnalytics`:

```kotlin
initMixPanel(apiKey = token)
initFirebaseAnalytics { }
initAnalytics {
    providers(
        MixPanelAnalyticsScope.getProvider(),
        FirebaseAnalyticsScope.getProvider(),
    )
}
```

Do **not** auto-register Mixpanel (or Firebase) as `EventsTracker` in Koin.

## Rules

- Do **not** introduce a parallel analytics system.
- Keep calls in the presentation layer.
- Register `analyticsDomainModule` in `InitKoin`. Call `initAnalytics` after Koin + provider inits. Mixpanel: `analyticsDataModule` + `initMixPanel` + pass `getProvider()` only if you want it. Firebase: `analyticsFirebaseDataModule` + `initFirebaseAnalytics` + pass `FirebaseAnalyticsScope.getProvider()`.

## Reference

- Docs: `https://starter.atherio.dev/features/` → Analytics
- Source: `features/analytics/*`, `features/core/domain/.../AppEvents.kt`
