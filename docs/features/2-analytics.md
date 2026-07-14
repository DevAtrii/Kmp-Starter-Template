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

!!! info
    See the [official Mixpanel docs](https://developer.mixpanel.com/docs/quickstart) for generating your token.

---

## Architecture

| Piece | Role |
| :--- | :--- |
| `AppEvent` | Base analytics event (`event` name + optional `properties`) in **analytics domain** |
| `AppEvents` | Your sealed hierarchy of all app events (starter ships one in **core domain**) |
| `EventsTracker` | Interface that sends events to the provider |

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

---

## Replacing Analytics Provider

To swap Mixpanel with another provider:

1. Implement the `EventsTracker` interface in the **data layer**.
2. Update your Koin module to provide your implementation.

!!! note
    - Domain layer, ViewModels, and Compose code remain unchanged.
    - Switching providers does not require rewriting event definitions.

### Dummy Local Implementation Example

```kotlin linenums="1"
class DummyEventsTracker : EventsTracker {

    override val isEnabled: Boolean
        get() = true

    override suspend fun track(event: AppEvent) {
        println("Tracked event: ${event.event} with properties ${event.properties}")
    }

    override suspend fun track(event: String) {
        println("Tracked event: $event")
    }

    override suspend fun track(event: String, pair: Pair<String, Any>?) {
        println("Tracked event: $event with ${pair?.first}=${pair?.second}")
    }

    override suspend fun track(event: String, properties: Map<String, Any>?) {
        println("Tracked event: $event with properties $properties")
    }

    override suspend fun setUserId(userId: String) {
        println("Set userId: $userId")
    }

    override suspend fun optIn() {
        println("Opted in")
    }

    override suspend fun optOut() {
        println("Opted out")
    }

    override suspend fun toggleOptInOut() {
        println("Toggled opt-in/out")
    }

    override suspend fun hasOptedIn(): Boolean = true
    override suspend fun flush() {}
    override suspend fun reset() {}
}
```
