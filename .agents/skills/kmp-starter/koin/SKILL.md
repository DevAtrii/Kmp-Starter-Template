---
name: kmp-starter-koin
description: Dependency injection with Koin on the KMP Starter Template — one module per layer, plugin selection, singleOf bindings, registration in InitKoin, viewModelOf/parameterized koinViewModel, and scoped navigators + ViewModels for nested nav.
---

# Koin (Dependency Injection)

## Modules per layer

Create one Koin module per layer when appropriate: `data`, `domain`, `presentation`.

```kotlin
// data
val featureNameDataModule = module {
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class
}
```

- `singleOf(::Impl)` — construct a singleton.
- `bind Interface::class` — bind impl to interface.

## Plugin selection (in `build.gradle.kts`)

- **Presentation** modules (Compose): use `koin-compose` plugin.
- **Data / domain** modules (non-UI): use `koin-core` plugin.

The generated `features/your-feature/*` modules already apply the right plugin. Match them.

## Register the module

Register new modules in `composeApp/src/commonMain/kotlin/<your-package>/core/di/InitKoin.kt` inside `initKoin { modules(...) }`, and ensure the module is a dependency of `composeApp`'s `commonMain`.

```kotlin
internal fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            starterModules,
            kmpAppInitializerModule,
            /* Add Modules Here */
            featureNameDataModule,
            featureNameDomainModule,
            featureNamePresentationModule,
        )
    }
}
```

## ViewModel with constructor params

```kotlin
// module
viewModelOf(::ExportViewModel)                 // no params — auto-resolved
viewModel { (fileManager: StarterFileManager) -> // explicit params
    ExportViewModel(fileManager = fileManager)
}
```

```kotlin
// screen
val viewModel: ExportViewModel = koinViewModel()                          // no params
val vm: ExportViewModel = koinViewModel { parametersOf(fileManager) }     // positional
val vm2: NotesViewModel = koinViewModel { parameterArrayOf(sharedVm) }    // pass as array
```

- `viewModelOf(::X)` — concise; Koin resolves constructor args from the graph.
- `viewModel { (a, b) -> ... }` + `parametersOf(a, b)` / `parameterArrayOf(...)` — pass runtime values (nav args, Activity-bound `StarterFileManager`, a shared ViewModel).

Use `parameterOf(...)` when passing values into ViewModels from navigation or the composable.

## Scoped navigator + ViewModels (nested nav)

For a feature with its own navigation graph, bind the navigator and ViewModels to a Koin **scope** so they live/die with the feature.

```kotlin
internal data object EditorScope {
    private val QUALIFIER = qualifier<EditorScope>()
    private val ID = QUALIFIER.toString()

    @Composable
    fun getOrCreateScope() = getKoin().getOrCreateScope(scopeId = ID, qualifier = QUALIFIER)
}

val editorNavigationModule = module {
    scope<EditorScope> {
        scopedOf(::EditorNavigator) bind BaseNavigator::class

        navigation<EditorScreens.Dashboard> { route ->
            val navigator = EditorNavigator.getCurrent()
            val vm: EditorDashboardViewModel = koinViewModel { parameterArrayOf(sharedVm) }
            EditorDashboardScreen(viewModel = vm, onNavigate = { navigator.navigateTo(...) })
        }
    }
}
```

```kotlin
internal class EditorNavigator : BaseNavigator() {
    companion object {
        @Composable
        fun getCurrent(): EditorNavigator =
            koinInject(scope = EditorScope.getOrCreateScope())
    }
}
```

Wire the scope in the parent screen: `rememberKoinScope(scope = EditorScope.getOrCreateScope())`, then wrap children in `KoinScope<EditorScope>(scopeID = scope.id) { ... }`. Use `koinEntryProvider<Any>(scope)` for the nested `NavDisplay`.

## Rules

- Reuse existing Koin modules whenever possible.
- Create new modules only when necessary.
- Domain Logics are registered like any other singleton (`singleOf(::CheckIsOnboardedLogic)`).

## Reference

- `features/core/data/.../di/CoreDataModule.kt` — `singleOf ... bind`
- `features/core/domain/.../di/CoreDomainModule.kt` — Logics + aggregate `OnboardingLogics`
- `composeApp/.../core/di/InitKoin.kt` — registration point
