# DatePickerDialog

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/ui/dialogs/DatePickerDialog.kt`

Material 3 date picker dialog with hooks for immediate selection and confirmation.

Usage:
```kotlin
DatePickerDialog(
    selectedDate = selected,
    onDatePicked = { temp = it },
    onDateSelected = { selected = it },
    onDismiss = { show = false }
)
```
Notes:
- `onDatePicked` is called whenever the picker value changes (via `LaunchedEffect`).
- Pass custom `SelectableDates` if needed.
