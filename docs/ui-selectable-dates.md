# SelectableDates (PastOrPresentSelectableDates)

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/ui/utils/date_picker/SelectableDates.kt`

Utility for constraining selectable dates in `DatePicker`.

Behavior:
- Allows today, past dates, and tomorrow (one day ahead).
- `isSelectableYear` allows years up to the current year.

Use with `DatePickerDialog`:
```kotlin
DatePickerDialog(
    selectableDates = PastOrPresentSelectableDates,
    onDateSelected = { /* ... */ },
    onDismiss = { /* ... */ }
)
```
