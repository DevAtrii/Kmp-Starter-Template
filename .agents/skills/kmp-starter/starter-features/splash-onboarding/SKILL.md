---
name: kmp-starter-feature-splash-onboarding
description: The KMP Starter Template Splash and Onboarding flow — SplashViewModel, OnboardingViewModel, OnboardingLogics, and where to customize slides.
author: DevAtrii
license: MIT

---

# Splash & Onboarding

Starter ships Splash and Onboarding in the core presentation module. Customize them; don't replace the system.

## Where things live

| Piece | Path |
| --- | --- |
| `SplashViewModel` / `SplashScreen` | `features/core/presentation/.../viewmodels/SplashViewModel.kt`, `.../screens/SplashScreen.kt` |
| `OnboardingViewModel` / `OnboardingV1Screen` | `features/core/presentation/.../viewmodels/OnboardingViewModel.kt`, `.../screens/OnboardingV1Screen.kt` |
| `OnboardingLogics` | `features/core/domain/.../logics/OnboardingLogics.kt` |
| `CheckIsOnboardedLogic` / `SetOnboardedLogic` | `features/core/domain/.../logics/onboarding/*.kt` |
| `rememberIsOnboarded()` | `features/core/presentation/.../utils/CoreRememberUtils.kt` |

## Splash

`SplashViewModel` extends `MviViewModel<SplashState, MviActions, SplashEvents>`. On `onStateStart()` it checks onboarding, waits a random duration, then emits `SplashEvents.OnFinish`:

```kotlin
override fun onStateStart() {
    checkIfOnboarded()
    onSplashStarted()
}

private suspend fun onSplashFinished() {
    emitEvent(SplashEvents.OnFinish)
    onboardingLogics.setOnboarded(true) // if using auth, set it in the auth flow instead
}
```

Navigation reacts to `OnFinish` — typically to Onboarding if not onboarded, else to the home screen.

## Onboarding

`OnboardingViewModel` is a full MVI example (`OnboardingState` / `OnboardingActions` / `OnboardingEvents`):

- `OnboardingState` — `selectedTrafficSource`, `currentSlide`, `totalSlides`, `isOnboarded`.
- `OnboardingActions` — `OnStepIncrement`, `OnStepDecrement`, `OnFinish`, `OnTrafficSourceChange`.
- `OnboardingEvents` — `Finish` (emitted after `setOnboarded(true)` + tracking `AppEvents.TrackTrafficSource`).

```kotlin
private fun onFinish() {
    setOnboarded(true)
    viewModelScope.launch {
        eventsTracker.track(
            event = AppEvents.TrackTrafficSource(
                source = _state.value.selectedTrafficSource ?: "--",
            ),
        )
        emitEvent(OnboardingEvents.Finish)
    }
}
```

Onboarding state is persisted via `OnboardingLogics.checkIsOnboarded()` / `setOnboarded(value)` (DataStore-backed). Compose helper `rememberIsOnboarded()` observes it.

## Customizing

- Change slides in `OnboardingV1Screen` (title, image, slide count via `totalSlides`).
- Need a different layout? Add a **custom slide** inside the onboarding screen rather than replacing the onboarding system or navigation.
- Wire slide transitions to `currentSlide` in state; increment/decrement via actions.

## Rules

- Reuse the MVI structure (State/Actions/Events) shown by `OnboardingViewModel` — it's the canonical feature slice.
- Persist onboarding through `OnboardingLogics`, not a parallel DataStore key.
- Keep analytics for onboarding (`TrackTrafficSource`) in the ViewModel.

## Reference

- Docs: `https://starter.atherio.dev/features/` → Core
- Source: `features/core/presentation/.../screens/`, `features/core/domain/.../logics/onboarding/`
