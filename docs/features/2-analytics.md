---
comments: false
icon: lucide/chart-area
---

# Analytics

The Starter Template provides a **modular, provider-agnostic analytics system** built with Clean Architecture. You track events through type-safe `AppEvent` models while keeping providers swappable in the data layer.

Starter ships two optional backends: **Mixpanel** and **Firebase Analytics** (added in **0.6.0**). Routing lives in domain. You opt in by passing providers to `initAnalytics`.

---

## Setup

Init **after** Koin. Each backend has its own `init*`. Then pass only the providers you want:

```kotlin title="composeApp/src/commonMain/.../core/InitKmpApp.kt"
initKoin(config = koinConfig)

initMixPanel(apiKey = AppConstants.MIXPANEL_API_TOKEN) {
    logging = platform.debug
}
initFirebaseAnalytics {
    enabled = true
}
initAnalytics {
    enableInstallAttribution = true
    providers(
        MixPanelAnalyticsScope.getProvider(),
        FirebaseAnalyticsScope.getProvider(),
    )
}
```

Omit a backend from `providers(...)` (and skip its `init*` / Koin data module) if you do not want it. A library consumer has full control.

`enableInstallAttribution = true` runs Play Install Referrer once on Android (`install_attribution` event + UTM user properties). iOS no-ops. Default `false`.

Register the matching Koin modules:

```kotlin title="composeApp/.../core/di/InitKoin.kt"
analyticsDomainModule,
analyticsDataModule,            // Mixpanel
analyticsFirebaseDataModule,    // Firebase — 0.6.0
```

```kotlin title="composeApp/build.gradle.kts"
implementation(projects.features.analytics.domain)
implementation(projects.features.analytics.data)           // Mixpanel
implementation(projects.features.analytics.dataFirebase)   // Firebase — 0.6.0
```

### Mixpanel

1. Open the constants file:

```kotlin title="composeApp/src/commonMain/.../core/AppConstants.kt" linenums="1"
object AppConstants {
    const val MIXPANEL_API_TOKEN = "add-your-mixpanel-token-here"
}
```

2. Replace `"add-your-mixpanel-token-here"` with your Mixpanel project token.

3. Call `initMixPanel` after Koin, then pass `MixPanelAnalyticsScope.getProvider()` in `initAnalytics`.

!!! info
    See the [official Mixpanel docs](https://developer.mixpanel.com/docs/quickstart) for generating your token.

### Firebase Analytics

!!! info "Added in 0.6.0"
    Firebase Analytics lives in `features/analytics/data-firebase`. No Mixpanel-style API token. The SDK reads your Firebase project from platform config files.

1. Add Firebase to the app (same files Remote Config already uses):

    * Android: `androidApp/google-services.json` plus the `google-services` plugin
    * iOS: `iosApp/iosApp/GoogleService-Info.plist`

2. Call `initFirebaseAnalytics` after Koin. Configure optional GitLive options, then pass `FirebaseAnalyticsScope.getProvider()` in `initAnalytics`.

```kotlin title="composeApp/src/commonMain/.../core/InitKmpApp.kt"
initFirebaseAnalytics {
    enabled = true
    sessionTimeoutInterval = 30.minutes
    defaultEventParameters = mapOf("app_flavor" to "prod")
    analyticsStorage = FirebaseAnalytics.ConsentStatus.GRANTED
}
```

| Option | Default | Role |
| :--- | :--- | :--- |
| `enabled` | `true` | Collection on/off (`setAnalyticsCollectionEnabled`) |
| `sessionTimeoutInterval` | `30.minutes` | Session timeout |
| `defaultEventParameters` | empty `Map<String, String>` | Attached to every event |
| `adPersonalization` | unset | Consent (`AD_PERSONALIZATION`) |
| `adStorage` | unset | Consent (`AD_STORAGE`) |
| `adUserData` | unset | Consent (`AD_USER_DATA`) |
| `analyticsStorage` | unset | Consent (`ANALYTICS_STORAGE`) |

Unset consent fields are skipped. `flush()` is a no-op — Firebase has no manual flush.

User properties are **not** part of init. Call `EventsTracker.setUserProperty` (or `setUserProperties`) after `initAnalytics`.

Look up this backend with `StarterAnalyticsProviderIds.Firebase`. Impl class: `FirebaseStarterAnalyticsProvider`.

!!! info
    See the [Firebase Analytics docs](https://firebase.google.com/docs/analytics) and [GitLive Firebase KMP](https://firebaseopensource.com/projects/gitliveapp/firebase-kotlin-sdk/).

#### Debug (Android)

Enable [DebugView](https://firebase.google.com/docs/analytics/debugview) so events show in the Firebase console in near real-time (without this, events can take hours). Replace `com.kmpstarter` with your `applicationId` if you changed the package:

```bash
adb shell setprop debug.firebase.analytics.app com.kmpstarter
```

Verbose FA logs:

```bash
adb shell setprop log.tag.FA VERBOSE
adb shell setprop log.tag.FA-SVC VERBOSE
adb logcat -v time -s FA FA-SVC
```

Disable debug mode:

```bash
adb shell setprop debug.firebase.analytics.app .none
```

---

## Architecture

| Piece | Role |
| :--- | :--- |
| `AppEvent` | Base analytics event (`event` name + optional `properties`) in **analytics domain** |
| `AppEvents` | Your sealed hierarchy of all app events (starter ships one in **core domain**) |
| `EventsTracker` | Interface that sends events / user id / user properties to the active provider set |
| `StarterAnalyticsProvider` | One concrete backend (`EventsTracker` + `id`). Mixpanel: **analytics data**. Firebase: `FirebaseStarterAnalyticsProvider` in **analytics data-firebase** (0.6.0). Swift still sees `AnalyticsProvider` via `@ObjCName` |
| `Analytics` / `StarterAnalyticsProviderId` / `StarterAnalyticsProviderIds` | Facade + well-known ids (`Mixpanel`, `Firebase`) in **analytics domain**. Data modules are optional |
| `analyticsDomainModule` | Binds the router as `Analytics` + `EventsTracker` from [initAnalytics] |
| `analyticsDataModule` | Mixpanel SDK wiring. You still pass Mixpanel in `initAnalytics { providers(...) }` |
| `analyticsFirebaseDataModule` | Firebase SDK wiring (0.6.0). You still pass Firebase in `initAnalytics { providers(...) }` |

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

Set a user id and user properties on the same tracker (fans out to active providers):

```kotlin
eventsTracker.setUserId(userId)
eventsTracker.setUserProperty(key = "plan", value = "pro")
eventsTracker.setUserProperties(
    values = mapOf(
        "plan" to "pro",
        "locale" to "en",
    ),
)
```

`setUserProperty` / `setUserProperties` take `String` keys and values. Mixpanel maps them to people properties. Firebase maps them to `setUserProperty`.

Koin binds the same router as both `Analytics` and `EventsTracker`. ViewModels can keep injecting `EventsTracker`. Inject `Analytics` only when you need lookup, combining, or runtime swaps.

---

## Multiple providers

Starter ships Mixpanel and Firebase as optional **data** modules. Routing lives in **domain**, so ViewModels only need `analytics.domain`. All registered providers start **active**.

### Look up one provider

```kotlin
class SignInViewModel(
    private val analytics: Analytics,
) : ViewModel() {

    fun onSignIn(userId: String) {
        viewModelScope.launch {
            analytics.provider(StarterAnalyticsProviderIds.Mixpanel).track(
                event = AppEvents.SignInSuccess(userId = userId),
            )
            analytics.provider(StarterAnalyticsProviderIds.Firebase).track(
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
    StarterAnalyticsProviderIds.Mixpanel,
    StarterAnalyticsProviderIds.Firebase,
)
mixpanelAndFirebase.track(AppEvents.DummyEvent)
```

`combine` returns a **fixed** composite. Later `setActiveProviders` does not change it. Duplicate ids fail fast. Empty `combine()` is a no-op tracker.

### Swap at runtime

```kotlin
analytics.setActiveProviders(
    StarterAnalyticsProviderIds.Mixpanel,
    StarterAnalyticsProviderIds.Firebase,
)
// existing EventsTracker injections now fan out to both

analytics.setActiveProviders(StarterAnalyticsProviderIds.Firebase)
// same instance now routes to Firebase only

analytics.setActiveProviders()
// routed tracking disabled; SDKs stay initialized
```

`isEnabled` is true when **any** active provider is enabled. `hasOptedIn` is true when **every** active provider has opted in (empty set → `true`).

---

## Adding a custom provider

Implement `StarterAnalyticsProvider` in a **data** module. Mixpanel is `analytics/data`. Firebase is `analytics/data-firebase` (`FirebaseStarterAnalyticsProvider`). Give it its own `init*` if the SDK needs a token. Pass the instance in `initAnalytics` — do not auto-bind it as the app-wide `EventsTracker`.

```kotlin linenums="1"
class PostHogEventsTracker : StarterAnalyticsProvider {

    override val id = StarterAnalyticsProviderId("posthog")
    // ... EventsTracker methods, including setUserProperty
}

fun initPostHog(...) { /* SDK init */ }

fun PostHogAnalyticsScope.getProvider(): StarterAnalyticsProvider = PostHogEventsTracker()
```

```kotlin
initMixPanel(apiKey = token) { logging = platform.debug }
initFirebaseAnalytics { }
initPostHog(...)
initAnalytics {
    providers(
        MixPanelAnalyticsScope.getProvider(),
        FirebaseAnalyticsScope.getProvider(),
        PostHogAnalyticsScope.getProvider(),
    )
}
```

!!! note
    - Domain layer, ViewModels, and Compose code remain unchanged.
    - Switching or combining providers does not require rewriting event definitions.
    - Use `StarterAnalyticsProviderIds.Mixpanel` / `StarterAnalyticsProviderIds.Firebase` for the shipped backends. Custom backends pick their own `StarterAnalyticsProviderId`.

---

??? abstract "Migration"
    Previously Mixpanel was bound as `EventsTracker` in Koin. You now **opt in** by passing providers to `initAnalytics`. ViewModels stay on `EventsTracker` — no screen changes.

    **1. Register domain module**

    Add `analyticsDomainModule` in `initKoin`. Keep `analyticsDataModule` only if you still use Mixpanel. Add `analyticsFirebaseDataModule` if you use Firebase (0.6.0):

    ```kotlin title="composeApp/.../core/di/InitKoin.kt"
    analyticsDomainModule,
    analyticsDataModule,
    analyticsFirebaseDataModule,
    ```

    **2. Pass providers after Koin**

    Keep each backend's `init*`. Then tell analytics which backends to use:

    ```kotlin title="composeApp/.../core/InitKmpApp.kt"
    initMixPanel(apiKey = AppConstants.MIXPANEL_API_TOKEN) {
        logging = platform.debug
    }
    initFirebaseAnalytics { }
    initAnalytics {
        providers(
            MixPanelAnalyticsScope.getProvider(),
            FirebaseAnalyticsScope.getProvider(),
        )
    }
    ```

    Omit a backend from `providers(...)` (and skip its `init*` / data module) if you do not want it.

    **3. Custom backends**

    Do not replace `EventsTracker` in Koin. Implement `StarterAnalyticsProvider`, init the SDK yourself, pass it in `initAnalytics`.

    **4. Type rename**

    `AnalyticsProvider` → `StarterAnalyticsProvider` (Swift still `AnalyticsProvider` via `@ObjCName`). `AnalyticsProviderId` / `AnalyticsProviderIds` → `StarterAnalyticsProviderId` / `StarterAnalyticsProviderIds`. Firebase class: `FirebaseStarterAnalyticsProvider`.

    User properties: `EventsTracker.setUserProperty(key, value)` / `setUserProperties(map)`. Removed from `initFirebaseAnalytics`.

    !!! warning
        Call `initAnalytics` after `initKoin` and after each provider's `init*`. `MixPanelAnalyticsScope.getProvider()` needs Koin plus a Mixpanel token. `FirebaseAnalyticsScope.getProvider()` needs Koin plus `analyticsFirebaseDataModule`.
