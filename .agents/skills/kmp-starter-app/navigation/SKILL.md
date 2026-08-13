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

When a feature owns multiple internal screens (e.g. a bottom-nav root with several tabs), it defines its own navigation graph inside the feature. Isolate everything in a `nested_navigation` package.

### 1. Define screens

A sealed class in the feature's nested-nav package (`@Serializable`, extend `NavKey`):

```kotlin
@Serializable
sealed class FeatureScreens : NavKey {
    @Serializable data object Home : FeatureScreens()
    @Serializable data object History : FeatureScreens()
    @Serializable data object Settings : FeatureScreens()
}
```

### 2. Create the Koin scope

A scope object + `getOrCreateScope()` so the nested navigator (and any scoped deps) live/die with the feature:

```kotlin
internal object FeatureNavScope {
    private val QUALIFIER = qualifier<FeatureNavScope>()
    private val ID = QUALIFIER.toString()

    @Composable
    fun getOrCreateScope() = getKoin().getOrCreateScope(scopeId = ID, qualifier = QUALIFIER)
}
```

### 3. Create the nested navigator

Extend `BaseNavigator` and add a `getCurrent()` companion that injects from the scope:

```kotlin
internal class FeatureNestedNavigator : BaseNavigator() {
    companion object {
        @Composable
        fun getCurrent(): FeatureNestedNavigator =
            koinInject(scope = FeatureNavScope.getOrCreateScope())
    }
}
```

To signal the parent (e.g. "open a screen owned by the app, with args"), expose events from the navigator itself:

```kotlin
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _events = MutableSharedFlow<FeatureNestedNavigationEvents>(replay = 0)
    val events: SharedFlow<FeatureNestedNavigationEvents> = _events.asSharedFlow()

    fun emitEvent(event: FeatureNestedNavigationEvents) {
        scope.launch { _events.emit(event) }
    }

internal sealed class FeatureNestedNavigationEvents {
    data class OnNavigateToDetail(val id: String) : FeatureNestedNavigationEvents()
    data object OnNavigateToPurchases : FeatureNestedNavigationEvents()
}
```

### 4. Register routes in a Koin module

One module per feature graph, `scope<FeatureNavScope> { scoped { Navigator() } + navigation<Screen> { ... } }`:

```kotlin
val featureNestedNavigationModule = module {
    scope<FeatureNavScope> {
        scoped { FeatureNestedNavigator() }
        navigation<FeatureScreens.Home> {
            val navigator = FeatureNestedNavigator.getCurrent()
            HomeScreen(
                onNavigateToDetail = { id ->
                    navigator.emitEvent(FeatureNestedNavigationEvents.OnNavigateToDetail(id))
                },
                onNavigateToPurchases = {
                    navigator.emitEvent(FeatureNestedNavigationEvents.OnNavigateToPurchases)
                },
            )
        }
        navigation<FeatureScreens.History> { HistoryScreen() }
        navigation<FeatureScreens.Settings> {
            val navigator = FeatureNestedNavigator.getCurrent()
            SettingsScreen(
                onNavigateToPurchases = {
                    navigator.emitEvent(FeatureNestedNavigationEvents.OnNavigateToPurchases)
                },
            )
        }
    }
}
```

### 5. Root screen

The root composable holds the back stack, wires the navigator, and observes navigator events to bubble navigation up to the app:

```kotlin
@Composable
fun FeatureRootScreen(
    onNavigateToDetail: (id: String) -> Unit,
    onNavigateToPurchases: () -> Unit,
) {
    FeatureRootScreenContent()

    val navigator = FeatureNestedNavigator.getCurrent()
    ObserveAsEvents(flow = navigator.events) { event ->
        when (event) {
            is FeatureNestedNavigationEvents.OnNavigateToDetail ->
                onNavigateToDetail(event.id)
            FeatureNestedNavigationEvents.OnNavigateToPurchases -> onNavigateToPurchases()
        }
    }
}

@Composable
private fun FeatureRootScreenContent() {
    val koinScope = rememberKoinScope(scope = FeatureNavScope.getOrCreateScope())
    val entryProvider = koinEntryProvider<Any>(scope = koinScope)
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(FeatureScreens.Home) {
        subclass(FeatureScreens.Home::class)
        subclass(FeatureScreens.History::class)
        subclass(FeatureScreens.Settings::class)
    }

    val navigator = FeatureNestedNavigator.getCurrent()
    LaunchedEffect(backStack) { navigator.provideBackStack(backStack) }

    Scaffold(bottomBar = {
        // tab bar; each tab -> navigator.navigateOrBringToTop(item.route)
    }) { innerPaddings ->
        NavDisplay(
            modifier = Modifier.padding(bottom = innerPaddings.calculateBottomPadding()),
            backStack = backStack,
            entryProvider = entryProvider,
            transitionSpec = Nav3Transitions.horizontalSlideParallax(),
            popTransitionSpec = Nav3Transitions.horizontalSlideParallaxPop(),
            predictivePopTransitionSpec = Nav3Transitions.predictiveHorizontalSlideParallax(),
        )
    }
}
```

Key points:

- Use `rememberKoinScope(scope = FeatureNavScope.getOrCreateScope())` + `koinEntryProvider<Any>(scope = koinScope)` — the entry provider is scoped, so screen-level ViewModels resolve inside the feature scope.
- Bind navigator with `scoped { FeatureNestedNavigator() }` in the scope; retrieve with `getCurrent()` (which injects from the same scope).
- Tab switching uses `navigator.navigateOrBringToTop(route)` (single instance per tab).
- Bubble "leave the feature" actions up via navigator `events` + `ObserveAsEvents` — feature modules never touch app navigation directly.
- Transitions via `Nav3Transitions` (`horizontalSlideParallax` / `fade` / etc.). Pick per platform if needed (`platform.isAndroid` / `platform.isIos`).

Register the feature's `*NestedNavigationModule` in `InitKoin` (see koin skill).

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
