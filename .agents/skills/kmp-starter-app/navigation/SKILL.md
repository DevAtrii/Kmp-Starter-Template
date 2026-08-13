---
name: kmp-starter-navigation
description: Navigation on the KMP Starter Template using navigation3 — AppScreens, polymorphic registration, AppNavigationModule routes, StarterNavigator, nested navigation (scoped navigators, Nav3Transitions, entry decorators), and ResultStore.
---

# Navigation

Uses **navigation3** + Koin. Global navigation lives in `composeApp/core/navigation`.

## 1. Define screens

`composeApp/src/commonMain/kotlin/<your-package>/core/navigation/AppScreens.kt`:

```kotlin
@Serializable
@Stable
sealed class AppScreens : NavKey {
    @Serializable
    data object Home : AppScreens()
}
```

Rules: must be `@Serializable`; must extend `NavKey`; one `sealed class` per feature. Mark `@Stable` too. Group many screens into nested `sealed class`es (e.g. `App`, `Web`, `Plugins`) to keep one hierarchy.

## 2. Register for serialization

In `composeApp/.../App.kt`, `AppConfig.navigationPolymorphicBuilder`:

```kotlin
val navigationPolymorphicBuilder: PolymorphicModuleBuilder<NavKey>.() -> Unit = {
    subclass(AppScreens.Home::class)
    // ... every screen
}
```

Missing registration → state restoration fails.

## 3. Define route (Koin)

`composeApp/src/commonMain/kotlin/<your-package>/core/navigation/AppNavigationModule.kt`:

```kotlin
val appNavigationModule = module {
    includes(navigationCoreModule)

    navigation<AppScreens.Home> { route ->
        val navigator = StarterNavigator.getCurrent()
        HomeScreen(onTaskComplete = { navigator.navigateUp() })
    }
}
```

## 4. Navigate

```kotlin
val navigator = StarterNavigator.getCurrent()

navigator.navigateTo(route)            // push
navigator.popAndNavigate(route)        // pop current, then push
navigator.popAllAndNavigate(route)     // clear stack, then push
navigator.navigateOrBringToTop(route)  // single instance, bring to top
navigator.navigateUp()                 // pop
navigator.remove(route)                // remove a destination
```

`StarterNavigator` extends `BaseNavigator` (`features/navigation/.../BaseNavigator.kt`) and is provided by Koin via `navigationCoreModule`.

## 5. Change initial screen

`composeApp/.../App.kt` → `StarterNavigation(AppScreens.Splash, ...)`. First argument is the start screen.

## Rules

- Feature modules **never** contain app navigation logic; they expose composables only.
- `composeApp` owns navigation.
- ViewModels **never** navigate directly; screens call navigator from callbacks.

## Nested navigation

When a feature owns multiple internal screens (e.g. a bottom-nav `MainScreen`):

1. Create a `Screens` sealed class (same `@Serializable`/`@Stable`/`NavKey` rules).
2. Create a Feature `Navigator` extending `BaseNavigator` (bind it `scopedOf(::X) bind BaseNavigator::class` in a Koin scope — see koin skill).
3. Create a Navigator Koin module using `navigation<Screens.X> { route -> ... }`.
4. In the parent screen, use `rememberNavBackStack(initial) { subclass(...) }` + `koinEntryProvider()` + `NavDisplay`, then call `navigator.provideBackStack(backStack)`.
5. Keep nested navigation isolated inside that feature.
6. Use `rememberSaveableStateHolderNavEntryDecorator()` + `rememberViewModelStoreNavEntryDecorator()` for state restoration; use `Nav3Transitions.*` for animated transitions.

```kotlin
val backStack = rememberNavBackStack(NestedScreens.Home) {
    subclass(NestedScreens.Home::class)
    subclass(NestedScreens.History::class)
}
val entryProvider = koinEntryProvider()

LaunchedEffect(backStack) { navigator.provideBackStack(backStack) }

Scaffold(bottomBar = { /* switch backStack */ }) { padding ->
    NavDisplay(
        modifier = Modifier.padding(padding),
        backStack = backStack,
        entryProvider = entryProvider,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = Nav3Transitions.horizontalSlideParallax(),
        popTransitionSpec = Nav3Transitions.horizontalSlideParallaxPop(),
        predictivePopTransitionSpec = Nav3Transitions.predictiveHorizontalSlideParallax(),
        onBack = { if (backStack.size > 1) backStack.removeLast() },
    )
}
```

`Nav3Transitions` (in `features/navigation/.../utils/Nav3Transitions.kt`) also offers `verticalSlideUp/Down`, `fade`, `horizontalSlide`, `horizontalSlideCustom`.

Register nested routes in `AppNavigationModule.kt` (or a feature `di` module named `featureNameNestedNavModule`).

## Flow

1. Create screen (`@Serializable @Stable sealed class : NavKey`)
2. Register in `navigationPolymorphicBuilder`
3. Add route in `AppNavigationModule` (or custom module)
4. Use `StarterNavigator`
5. Change initial screen if needed

## Screen-to-screen results (ResultStore)

To send a result back or signal a parent without a direct nav-arg (e.g. "open the drawer" from a child), use `ResultStore` (`features/navigation/.../utils/ResultStore.kt`).

```kotlin
val resultStore = rememberResultStore()
ProvideResultStore(resultStore) { ... }   // in the parent

// child: set a result
val store = LocalResultStore.current
scope.launch { store.setResult("open_drawer", true) }

// parent: observe it
ObserveAsEvents(store.observeResult<Boolean>("open_drawer")) { open ->
    if (open) drawerState.open()
}
```

Use a constant string key per result. Results are ephemeral (not saved across process death).

## Reference

- `features/navigation/.../BaseNavigator.kt`, `StarterNavigator.kt`, `StarterNavigation.kt`
- `composeApp/.../core/navigation/AppScreens.kt`, `AppNavigationModule.kt`
- Docs: `https://starter.atherio.dev/fundamentals/08-navigation/`
