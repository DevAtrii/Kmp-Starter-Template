---
comments: false
icon: lucide/navigation
---

# Navigation

Starter Template uses **navigation3** with `Koin`.
All navigation logic lives inside the `features/navigation` module.

---

## 1. Define Screens

Example:

```kotlin title="features/navigation/src/commonMain/.../screens/AuthScreens.kt" linenums="1"
@Serializable
sealed class AuthScreens : NavKey {

    @Serializable
    data object SignIn : AuthScreens()

}
```

!!! note "Rules"
_ Must be `@Serializable`
_ Must extend `NavKey` \* Use `sealed class` per feature

---

## 2. Register Screens for Serialization

Add the screen to the back stack configuration:

```kotlin title="features/navigation/src/commonMain/.../StarterBackStack.kt" linenums="1" hl_lines="8"
@Composable
fun rememberStarterBackStack(vararg initialScreens: NavKey): NavBackStack<NavKey> {
    return rememberNavBackStack(
        elements = initialScreens
    ) {
        ...
        // Register new screens
        subclass(AuthScreens.SignIn::class)
    }
}
```

!!! warning "State Restoration"
If a screen is not registered using `subclass(...)`, state restoration will fail.

---

## 3. Define Navigation Routes (Koin)

```kotlin title="composeApp/src/commonMain/.../core/navigation/NavigationModule.kt" linenums="1"
val navigationModule = module {

    navigation<AuthScreens.SignIn> {
        val navigator = StarterNavigator.getCurrent()

        SignInScreen(
            onSignedIn = {
                navigator.navigateTo(StarterScreens.Home)
            }
        )
    }
}
```

!!! note "Custom Module"
You can also create a custom module inside your feature `di` package,
don't forget to include it inside `initKoin`

---

## 4. Navigating Between Screens

Get navigator:

```kotlin
val navigator = StarterNavigator.getCurrent()
```

Available methods:

```kotlin
navigator.navigateTo(route)
navigator.popAndNavigate(route)
navigator.popAllAndNavigate(route)
navigator.navigateOrBringToTop(route)
navigator.navigateUp()
```

Example:

```kotlin
navigator.popAllAndNavigate(StarterScreens.Home)
```

## 5. Changing Initial Screen

You can change the initial (starting) screen from `App.kt`.

By default, navigation starts from `StarterScreens.Splash`.
To change it, update the first parameter of `StarterNavigation`.

```kotlin title="composeApp/src/commonMain/kotlin/com/kmpstarter/App.kt" linenums="1" hl_lines="20"
@Composable
private fun MainApp(
    ...
) {
    ...
    AppUpdateProvider(
        ...
    ) {
        LocaleProvider(
            overrideDefault = StarterLocales.ENGLISH
        ) {
            CompositionLocalProvider(...) {
                ApplicationTheme(
                    ...
                ) {
                    Scaffold(
                        ...
                    ) { _: PaddingValues ->
                        StarterNavigation(
                            StarterScreens.Splash, // Change this
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}
```

For example, to start directly from SignIn:

```kotlin linenums="1"
StarterNavigation(
    AuthScreens.SignIn,
    modifier = Modifier
)
```

---

## Keeping Splash → Onboarding → Your Screen Flow

If you want to keep the default flow:

```
Splash → Onboarding → Your Screen
```

You can control it inside the navigation module.

```kotlin title="composeApp/src/commonMain/.../navigation/NavigationModule.kt" linenums="1" hl_lines="8 19"
val navigationModule = module {
    ...
    navigation<StarterScreens.Splash> { route ->
        ...
        SplashScreen(
            onNavigate = {
                navigator.popAndNavigate(
                    route = StarterScreens.Welcome
                )
            },
            ...
        )
    }
    navigation<StarterScreens.Onboarding> { route ->
        ...
        OnboardingV1Screen(
            onNavigate = {
                navigator.popAndNavigate(
                    route = StarterScreens.Welcome
                )
            }
        )
    }
    ...
}
```

---

!!! abstract "Navigation Flow"
    1. Create screen (extend `NavKey`)
    2. Register in `StarterBackStack`
    3. Add route in `NavigationModule` or custom module
    4. Use `StarterNavigator` to navigate
    5. Change `inital screen` if needed

    Navigation is type-safe, serializable, and works across Compose Multiplatform.
