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
    - Must be `@Serializable`
    - Must extend `NavKey` \* Use `sealed class` per feature

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
## Nested Navigation

Starter Template provides utilities for creating nested navigation. This is very useful when you have a screen (like `MainScreen`) that has its own internal navigation, like Bottom Navigation.

---

### Step 1: Define Your Nested Items
First, create a data class for your items and define the list of screens.

```kotlin title="MainScreen" linenums="1"
private data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: NavKey,
)

fun MainScreen(
    ...
) {
    val items = listOf(
        BottomNavItem(
            Res.string.nested_nav_home.toActualString(),
            Icons.Filled.Home,
            Icons.Outlined.Home,
            NestedScreens.Home
        ),
        BottomNavItem(
            Res.string.nested_nav_history.toActualString(),
            Icons.AutoMirrored.Filled.List,
            Icons.AutoMirrored.Outlined.List,
            NestedScreens.History
        ),
        ...
    )
}
```

!!! note "Defining Routes"
    We've already covered creating routes above. You should define your `NestedScreens` sealed class the same way.

---

### Step 2: Register Nested Screens in Navigation Module
Now, tell Koin how to show these nested screens.

```kotlin title="composeApp/src/commonMain/.../navigation/NavigationModule.kt"
val navigationModule = module {
    ...
    navigation<NestedScreens.Home> { route ->
        HomeScreen()
    }

    navigation<NestedScreens.History> { route ->
        HistoryScreen()
    }
    ...
}
```

!!! tip "Optional: Custom Module"
    You can also create a custom navigation module inside your feature's `di` package. Don't forget to include it in `initKoin`. It's a good practice to name it like `featureNameNestedNavModule`.

---

### Step 3: Initialize the Nested Backstack
In your parent screen (`MainScreen`), you need to manage the backstack for the nested navigation. We use the `rememberNavBackStack` utility from the `feature_navigation` module because it provides a much simpler syntax.

```kotlin title="MainScreen" linenums="1"
val backStack = rememberNavBackStack(NestedScreens.Home) {
    subclass(NestedScreens.Home::class)
    subclass(NestedScreens.History::class)
    ...
}
val entryProvider = koinEntryProvider()
val currentScreen = backStack.lastOrNull() ?: NestedScreens.Home
```

!!! note "Navigator is Optional"
    Creating a separate `Navigator` is usually optional for nested navigation. Since the parent screen (`MainScreen`) is usually only responsible for switching between these nested screens, you can just manipulate the `backStack` directly.

---

### Step 4: Setup UI and NavDisplay
Finally, complete your UI using `Scaffold` and `NavDisplay`.

``` kotlin title="MainScreen" linenums="1"
Scaffold(
        modifier = modifier,
        bottomBar = {
            MyBottomNav(
                onItemClick = { item ->
                     if (currentScreen != item.screen) {
                        val index = backStack.indexOf(item.screen)
                        if (index != -1) {
                            backStack.removeAt(index)
                        }
                        backStack.add(item.screen)
                    }
                    // or just use backStack.add(item.screen)
                }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            entryProvider = entryProvider,
            transitionSpec = defaultTransitionSpec(),
            popTransitionSpec = defaultPopTransitionSpec(),
            predictivePopTransitionSpec = defaultPredictivePopTransitionSpec(),
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLast()
                }
            }
        )
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
