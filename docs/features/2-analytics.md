---
comments: false
icon: lucide/chart-area
---

# Analytics

The Starter Template provides a **modular, provider-agnostic analytics system** built with Clean Architecture. You can track events, purchases, user behavior, and more while keeping your implementation flexible.

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

## 1. Define Event Keys

1: Inside the `companion object`, define your event key:

```kotlin linenums="1" title="features/analytics/domain/.../AppEventsTracker.kt"
companion object {
    const val KEY_SIGN_IN_SUCCESS = "sign_in_success"
}
```

2: Add a corresponding `track` function in the interface:

```kotlin linenums="1" title="features/analytics/domain/.../AppEventsTracker.kt"
suspend fun trackSignInSuccess(userId: String)
```

!!! note
    - Use descriptive function names starting with `track`.
    - Keep keys consistent and unique across the app.

??? abstract "Full Example"
    ```kotlin title="features/analytics/domain/.../AppEventsTracker.kt"
    interface AppEventsTracker {

    // events names
    companion object {
        const val KEY_ONBOARDING_TRAFFIC_SOURCE = "traffic_source"

        const val KEY_PURCHASE_SUCCESS = "purchase_success"
        const val KEY_PURCHASE_FAILURE = "purchase_failure"
        const val KEY_PURCHASE_RESTORE_FAILURE = "purchase_restore_failure"
        const val KEY_PURCHASE_PRODUCTS_FAILURE = "purchase_products_failure"

    }

    // onboarding
    suspend fun trackTrafficSource(source: String)

    // purchases
    suspend fun trackPurchaseSuccess(productId: String)
    suspend fun trackPurchaseFailure(productId: String, error: String)
    suspend fun trackPurchaseProductsFailure(error: String)
    suspend fun trackPurchaseRestoreFailure(error: String)

    }
    ```
    

---

## 2. Implement the Event

Implement the new function using the existing `EventsTracker`:

```kotlin linenums="1" title="features/analytics/data/.../AppEventsTrackerImpl.kt"
override suspend fun trackSignInSuccess(userId: String) {
    eventsTracker.track(
        event = AppEventsTracker.KEY_SIGN_IN_SUCCESS,
        pair = "userId" to userId
    )
}
```
??? abstract "Full Example"
    ```kotlin linenums="1" title="features/analytics/data/.../AppEventsTrackerImpl.kt"
    class AppEventsTrackerImpl(
        private val eventsTracker: EventsTracker,
    ) : AppEventsTracker {
        override suspend fun trackTrafficSource(source: String) {
            eventsTracker.track(
                event = AppEventsTracker.KEY_ONBOARDING_TRAFFIC_SOURCE,
                pair = "source" to source
            )
        }

        override suspend fun trackPurchaseSuccess(productId: String) {
            eventsTracker.track(
                event = AppEventsTracker.KEY_PURCHASE_SUCCESS,
                pair = "productId" to productId
            )
        }

        override suspend fun trackPurchaseFailure(productId: String, error: String) {
            eventsTracker.track(
                event = AppEventsTracker.KEY_PURCHASE_FAILURE,
                properties = mapOf(
                    "productId" to productId,
                    "error" to error
                )
            )
        }

        override suspend fun trackPurchaseProductsFailure(error: String) {
            eventsTracker.track(
                event = AppEventsTracker.KEY_PURCHASE_PRODUCTS_FAILURE,
                pair = "error" to error
            )
        }

        override suspend fun trackPurchaseRestoreFailure(error: String) {
            eventsTracker.track(
                event = AppEventsTracker.KEY_PURCHASE_RESTORE_FAILURE,
                pair = "error" to error
            )
        }
    }
    
    ```

---

## 3. Use in ViewModel

```kotlin linenums="1" title="SignInViewModel.kt"
class SignInViewModel(
    private val appEventsTracker: AppEventsTracker
) : ViewModel() {

    fun onSignIn(userId: String) {
        viewModelScope.launch {
            appEventsTracker.trackSignInSuccess(userId)
        }
    }
}
```

!!! note
    - Keep the analytics calling inside presentation layer
    - Best place is viewModel

---

## Replacing Analytics Provider

* To swap Mixpanel with another provider:

  1. Implement the `EventsTracker` interface in the **data layer**.
  2. Update your Koin module to provide your implementation.

!!! note
    - Domain layer, ViewModels, and Compose code remain unchanged.
    - This allows switching providers without rewriting app logic.


### Dummy Local Implementation Example

```kotlin linenums="1"
class DummyEventsTracker : EventsTracker {

    override val isEnabled: Boolean
        get() = true

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

