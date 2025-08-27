# UI Utilities & Side Effects

## LaunchOnce
File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/ui/side_effects/LaunchOnce.kt`

`LaunchOnce` runs a suspend block only once, surviving recompositions and configuration changes.

Signature:
```kotlin
@Composable
fun LaunchOnce(block: suspend () -> Unit)
```

How it works:
- Uses `rememberSaveable` to keep `hasRun` across configuration changes
- Triggers with `LaunchedEffect(Unit)` and guards execution with `hasRun`

Example:
```kotlin
LaunchOnce {
    viewModel.loadInitialData()
}
```

When to use:
- One-time initializations for a screen
- Fetch bootstrap data without re-running on recomposition
- Avoids common mistakes like `LaunchedEffect(true)`
