---
name: kmp-starter-feature-store-reviews-updates
description: Store reviews and in-app updates on the KMP Starter Template — StarterStoreManager (askForReview, checkAppUpdate), rememberStarterStoreManager, and AppUpdateProvider.
author: DevAtrii
license: MIT

---

# Store Reviews & Updates

Request store reviews and check for app updates via `StarterStoreManager`. Lives in the UI utils store package (`starter/ui/utils/.../store/`), exposed as a Compose `expect`/`actual`.

## Where things live

| Piece | Path |
| --- | --- |
| `StarterStoreManager` (expect class) | `starter/ui/utils/src/commonMain/.../store/StarterStoreManager.kt` |
| `rememberStarterStoreManager()` | same file |
| `AppUpdateProvider` / `rememberUpdateLauncher` | `starter/ui/utils/src/commonMain/.../store/InAppUpdate.kt` |
| Platform impls | `StarterStoreManager.android.kt` / `.ios.kt` |

## Request a review

Prompt after key actions (e.g. onboarding completion):

```kotlin
@Composable
fun Onboarding(viewModel: OnboardingViewModel) {
    val storeManager = rememberStarterStoreManager()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) storeManager.askForReview()
    }
}
```

- Android: `askForReview()` may throw if the review dialog is unavailable.
- iOS: uses the native review API.

## Check for updates (Android only)

```kotlin
@Composable
fun ManualUpdateCheck(force: Boolean) {
    val storeManager = rememberStarterStoreManager()
    val updateLauncher = rememberUpdateLauncher()

    LaunchedEffect(force) {
        storeManager.checkAppUpdate(
            launcher = updateLauncher,
            force = force,
            onUpdateUnAvailable = { /* no update */ },
            onUpdateAvailable = { /* update available */ },
            onUpdated = { /* updated */ },
            onUpdateFailure = { /* failed */ },
        )
    }
}
```

`checkAppUpdate()` is Android-only (iOS has no in-app update API). Prefer `AppUpdateProvider` over manual checks.

## Automatic updates — `AppUpdateProvider`

Wrap the app so updates are checked automatically:

```kotlin
@Composable
fun MyApp() {
    AppUpdateProvider(force = true) {
        StarterNavigation(AppScreens.Splash)
    }
}
```

`AppUpdateProvider` calls `checkAppUpdate()` internally on Android and skips iOS. `force = true` blocks usage until the minimum version is installed.

## Rules

- Use `askForReview()` after onboarding or key actions (not randomly).
- Avoid manual `checkAppUpdate()`; use `AppUpdateProvider`.
- No new update/review subsystem.

## Reference

- Docs: `https://starter.atherio.dev/fundamentals/09-store-reviews-and-updates/`
- Source: `starter/ui/utils/src/commonMain/.../store/*`
