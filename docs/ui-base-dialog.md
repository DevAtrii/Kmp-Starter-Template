# BaseDialog

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/ui/dialogs/BaseDialog.kt`

Simple reusable dialog container that wraps Material 3 `Dialog` with consistent width and shape.

Usage:
```kotlin
BaseDialog(onDismiss = onDismiss) {
    // dialog content
}
```
Notes:
- Width: `fillMaxWidth(0.9f)` with `usePlatformDefaultWidth = false`
- Shape: `RoundedCornerShape(20.dp)`; color from `MaterialTheme.colorScheme.surfaceContainerLowest`
