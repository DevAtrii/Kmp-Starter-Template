# kotlinx-datetime Utils

Files:
- `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/kotlinx_datetime/Clock.kt`
- `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/kotlinx_datetime/LocalDate.kt`

## API

- `Clock.System.localDateTime(timeZone = TimeZone.currentSystemDefault()): LocalDateTime`
  - Convenience to obtain current `LocalDateTime` with a given timezone.

- `LocalDate.Companion.fromLong(timestamp, timeZone = TimeZone.currentSystemDefault()): LocalDate`
  - Converts epoch millis to `LocalDate` using provided timezone (use `UTC` for Material DatePicker).

- `LocalDate.millis(timeZone = TimeZone.currentSystemDefault()): Long`
  - Converts `LocalDate` to epoch millis by composing a `LocalDateTime` at the provided timezone.

- `LocalDate.Companion.today(timeZone = TimeZone.currentSystemDefault()): LocalDate`
  - Current date in the given timezone.

- `LocalDate.toLocalDateTime(hour, minute, second, nanosecond): LocalDateTime`
  - Builds a `LocalDateTime` for this date with provided clock defaults.

- `Month.length(year: Int): Int`
  - Returns number of days in month for the specific year (handles leap years).

## Examples

```kotlin
val now = Clock.System.localDateTime()
val today = LocalDate.today()
val date = LocalDate.fromLong(System.currentTimeMillis(), TimeZone.UTC)
val millis = today.millis(TimeZone.UTC)
```

## Notes
- For Material3 `DatePicker`, prefer `UTC` for consistent conversion.
