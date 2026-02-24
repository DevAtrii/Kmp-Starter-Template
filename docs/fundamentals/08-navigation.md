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
    * Must be `@Serializable`
    * Must extend `NavKey`
    * Use `sealed class` per feature

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

---

!!! abstract "Navigation Flow"
    1. Create screen (extend `NavKey`)
    2. Register in `StarterBackStack`
    3. Add route in `NavigationModule` or custom module
    4. Use `StarterNavigator` to navigate

    Navigation is type-safe, serializable, and works across Compose Multiplatform.

