# BaseBottomSheet

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/ui/bottom_sheets/BaseBottomSheet.kt`

Reusable `ModalBottomSheet` with animated rounded corners and proper dismiss handling.

Usage:
```kotlin
BaseBottomSheet(
    sheetState = sheetState,
    onDismiss = { show = false }
) {
    // sheet content
}
```
Helpers:
- `SheetState.hideProperly(scope, onHidden)` ensures state is updated after hide.
- Corners animate to square when fully expanded.
