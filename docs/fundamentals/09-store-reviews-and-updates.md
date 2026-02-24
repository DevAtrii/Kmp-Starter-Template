---
comments: false
icon: lucide/play
---

# Store Reviews & Updates

Starter Template provides a simple way to **request store reviews** and **check for app updates** using `StarterStoreManager`.

All functionality can be used in Composables via `rememberStarterStoreManager()`.

---

## Requesting a Review

Prompt users for a review after key actions, like onboarding completion:

```kotlin title="InAppReview Example" linenums="1" hl_lines="8"
@Composable
fun Onboarding(viewModel:OnboardingViewModel) {
    val state by viewModel.state.collectAsState()
    val storeManager = rememberStarterStoreManager()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            storeManager.askForReview()
        }
    }
    ...
}
```

!!! warning "Platform Behavior"
    - On Android, `askForReview()` may throw an exception if the review dialog is unavailable.

---

## Checking for App Updates (Android Only)

Manual check example:

```kotlin linenums="1"
@Composable
fun ManualUpdateCheck(force: Boolean) {
    val storeManager = rememberStarterStoreManager()
    val updateLauncher = rememberUpdateLauncher()

    LaunchedEffect(force) {
        storeManager.checkAppUpdate(
            launcher = updateLauncher,
            force = force,
            onUpdateUnAvailable = { println("No update available") },
            onUpdateAvailable = { println("Update available") },
            onUpdated = { println("App updated") },
            onUpdateFailure = { println("Update failed") },
        )
    }
}
```

!!! danger "Android Only"
    - `checkAppUpdate()` only works on Android because iOS does not provide an in-app update API like Google Play Store.
    - It's not recommended to call this directly in your app unless you have a specific reason to do so; instead, use `AppUpdateProvider` for automatic update handling.

---

## AppUpdateProvider

Wrap your app to automatically check for updates:

```kotlin linenums="1"
@Composable
fun MyApp( ) {
    val storeManager = rememberStarterStoreManager()
    val updateLauncher = rememberUpdateLauncher()

    AppUpdateProvider(force = true) {
        StarterNavigation(StarterScreens.Splash)
    }
}
```

!!! abstract "How It Works"
    1. `AppUpdateProvider` wraps your app Composable.
    2. It calls `checkAppUpdate()` internally on Android.
    3. You can handle forced or optional updates automatically.
    4. iOS calls are skipped since in-app updates are not supported.

---

### Summary

* Use `askForReview()` after onboarding or key actions.
* Avoid calling `checkAppUpdate()` manually; use `AppUpdateProvider`.
* `AppUpdateProvider` ensures automatic, safe update checks on Android and skips on iOS.
