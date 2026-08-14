---
name: kmp-starter-utils
description: Pure Kotlin helpers on the KMP Starter Template — the starter/utils module (no Compose). Variable/string/int/bool extensions, time & kotlinx-datetime, JSON, FieldState/ComparableState, IntentUtils, Log, DataStore delegates, StarterFileManager, EnumException, ExperimentalStarterApi.
author: DevAtrii
license: MIT

---

# Utils

Reuse Starter's non-UI helpers. Do not write a Kotlin utility if `starter/utils` already ships one.

Accessor: `projects.starter.utils`. Package root `com.kmpstarter.utils.*`. No Compose — safe in ViewModels, repositories, and domain logic.

## Variables

- `String`: `onEmpty(action)`, `onNotEmpty(action)`, `takeIfEmpty(action)`, `takeIfNotEmpty(action)`, `isEmail()`.
- `Int`: `safeDec()` (won't go below 1).
- `Boolean`: `ifTrue(value|action)`, `ifFalse(value|action)`, `toggle()`.
- Generic: `T?.elze(value)` — null-coalescing (`this ?: value`).

## Data & time

- `ByteArray.toBase64()`.
- `currentMillis` (property) / `epochMillis()` / `hoursToMillis(hour)`.

## kotlinx-datetime

- `LocalDate.today()`, `LocalDate.fromLong(millis)`, `LocalDate.millis()`, `LocalDate.toLocalDateTime(...)`.
- `Clock.System.localDateTime()`, `Clock.System.currentInstant()`.
- `Month.length(year)` — days in month, leap-year aware.
- `fromLong`/`millis` take `timeZone` (default system); use `TimeZone.UTC` with Material 3 DatePicker.

## Serialization

- `Any?.toJsonElement()` — recursive conversion to `JsonElement` (nulls, maps, iterables, arrays, sequences, primitives; fallback `.toString()`).

## Data classes

- `FieldState(value, error)` — `@Serializable`; helpers `isEmpty()`, `isValid()`, `updateValue()`, `updateError()`, `reset()`.
- `ComparableState<T>(value, initialValue)` — `hasChanges`, `updateValue()`, `reset()`, `updateAndReset()`, `updateAndResetIfNotError(isError, isSaving, newValue)`. Build with `T.asComparableState()`.

## Other

- `EnumException(reason)` — `IllegalStateException` carrying an enum for `when`-exhaustive errors.
- `@ExperimentalStarterApi` — opt in at call site (`@OptIn(ExperimentalStarterApi::class)`).
- `IntentUtils` — `openUrl`, `openAccessibility`, `shareText`, `writeToClipboard`, `readFromClipboard`, `sendEmail`, `printNativeView`.
- `Log` object — `Log.d/i/w/e(tag?, message)` (tag `null` → `[KMP_STARTER]`).
- DataStore delegates — `AppDataStore.stringDataStore/intDataStore/longDataStore/booleanDataStore/floatDataStore/doubleDataStore/stringSetDataStore/byteArrayDataStore/serializableDataStore` (API: `flow`, `get()`, `set(value)`, `clear()`; `set(null)` removes key). See data skill.
- `StarterFileManager` + `StarterFile` — cross-platform file API (see data skill).
- `utilsModule` — Koin module (includes `commonUtilsModule` + `platformUtilsModule`).

## Rules

- **Search `starter/utils` first** — before writing any string/int/bool extension, date helper, JSON util, or form state class.
- `FieldState` + `ComparableState` are the form-state primitives; use them, don't invent equivalents.
- Opt in to `@ExperimentalStarterApi` where the compiler requires it.
- Color parsing lives in `ui/utils` (`Color.fromHex`), not here.

## Reference

- Docs (live site): `https://starter.atherio.dev/utils/`
- Source: `starter/utils/src/commonMain/kotlin/com/kmpstarter/utils/**`
