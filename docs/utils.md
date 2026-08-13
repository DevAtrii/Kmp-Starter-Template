---
comments: false
icon: lucide/wrench
---

# Utils

The `starter/utils` module holds **pure Kotlin helpers** you'll reach for in every layer of the app — string/boolean/int extensions, date-time utilities, JSON conversion, form state classes, and a few cross-platform services.

There's **no Compose here**, so everything works in ViewModels, repositories, and domain logic too.

Add the dependency in your `commonMain` source set:

```kotlin title="build.gradle.kts"
implementation(projects.starter.utils)
```

---

## Variable Helpers

### String

```kotlin
"hello".onEmpty { println("empty!") }        // runs action if empty
"hello".onNotEmpty { println("not empty!") } // runs action if not empty

"".takeIfEmpty { "fallback" }      // returns "fallback"
"hi".takeIfNotEmpty { "value" }    // returns "value"

"user@example.com".isEmail()       // true
```

### Int

```kotlin
5.safeDec()   // 4
1.safeDec()   // 1 (won't go below 1)
```

### Boolean

```kotlin
true.ifTrue("yes")    // "yes"
false.ifTrue("yes")   // null

true.ifFalse { doThing() }   // runs action only if false

val flag = true
flag.toggle()   // false
```

### Generic (elze)

`elze` is a null-coalescing operator spelled differently (a Kotlin-friendly `?:`):

```kotlin
val name: String? = null
val displayName = name elze "Guest"   // "Guest"
```

---

## ByteArray

Encode bytes to Base64:

```kotlin
val bytes: ByteArray = "hello".encodeToByteArray()
val base64 = bytes.toBase64()
```

---

## Time

```kotlin
val now = currentMillis       // Long property
val alsoNow = epochMillis()   // same thing as a function
val ms = hoursToMillis(2)     // 7_200_000
```

---

## kotlinx-datetime

Extensions on top of `kotlinx.datetime` for painless `LocalDate` / `Clock` handling.

```kotlin
// Today
val today = LocalDate.today()

// From a timestamp (millis)
val date = LocalDate.fromLong(timestampMillis)

// Back to millis
val millis = date.millis()

// Current local date-time / instant
val now = Clock.System.localDateTime()
val instant = Clock.System.currentInstant()

// Days in a month (handles leap years)
val days = Month.February.length(2024)   // 29
```

`LocalDate.fromLong` and `LocalDate.millis` accept a `timeZone` (defaults to system). Use `TimeZone.UTC` when pairing with Material 3's DatePicker.

---

## kotlinx-serialization

Convert any supported Kotlin value into a `JsonElement` recursively:

```kotlin
val data = mapOf("name" to "Atrii", "age" to 30)
val element = data.toJsonElement()   // JsonObject

listOf(1, 2, 3).toJsonElement()      // JsonArray
"hello".toJsonElement()              // JsonPrimitive
```

Handles nulls, maps, iterables, arrays, primitives, and falls back to `.toString()` for anything else.

---

## Data Classes

### FieldState

A tiny state holder for form fields — keeps the value and its error together.

```kotlin
@Serializable
data class FieldState(
    val value: String = "",
    val error: String = "",
)

val email = FieldState()

email.isEmpty()                  // true
email.updateValue("a@b.com")     // new FieldState
email.updateError("Invalid")     // new FieldState
email.isValid()                  // not empty && no error
email.reset()                    // back to FieldState()
```

It's `@Serializable`, so you can drop it straight into your MVI state.

### ComparableState

Tracks a value against its **initial** value so you know when the user made changes (e.g. to enable a "Save" button).

```kotlin
val state = "Atrii".asComparableState()

state.hasChanges          // false
state.updateValue("Bob").hasChanges   // true
state.reset()             // initialValue becomes current value → hasChanges false
state.updateAndReset("x") // set value & reset in one go
```

`updateAndResetIfNotError(isError, isSaving, newValue)` is the special one for syncing a form with a shared ViewModel — it won't clobber unsaved edits while saving or on error.

---

## EnumException

Throw an `IllegalStateException` that carries an enum reason, useful for `when`-exhaustive error handling:

```kotlin
throw EnumException(SignInError.InvalidCredentials)
```

---

## ExperimentalStarterApi

Some starter APIs are marked `@ExperimentalStarterApi`. You must opt in at the call site:

```kotlin
@OptIn(ExperimentalStarterApi::class)
fun doStuff() { /* ... */ }
```

---

## IntentUtils (cross-platform links)

A small `expect class` for common OS intents — open URLs, share text, clipboard, email, and printing.

```kotlin
val intents = koinInject<IntentUtils>()   // or platform-specific constructor

intents.openUrl("https://starter.atherio.dev")
intents.shareText("Check this out!")
intents.writeToClipboard("copied text")
val clip = intents.readFromClipboard()
intents.sendEmail("hi@example.com", "Subject", "Body")
```

`openUrl` and `openAccessibility` return a `Boolean` (success / failure).

---

## Logging

Use the `Log` object for consistent logging everywhere:

```kotlin
Log.d(null, "Debug message")
Log.i("MyTag", "Info message")
Log.w(null, "Warning")
Log.e(null, "Error", "extra", "args")
```

Pass `null` for the default `[KMP_STARTER]` tag. See [Logging](../fundamentals/10-logging.md) for the full guide (including IDE live templates).

---

## DataStore

`starter/utils` provides the non-Compose DataStore layer:

- `AppDataStore` — the injected singleton wrapper.
- `createDataStore()` / `CreateDataStore` — platform file setup.
- **Delegates** — `stringDataStore`, `intDataStore`, `serializableDataStore`, etc.

```kotlin
class AuthViewModel(appDataStore: AppDataStore) : ViewModel() {
    private val token = appDataStore.stringDataStore("token")
    // token.set("..."), token.get(), token.flow, token.clear()
}
```

See [DataStores](../fundamentals/05-datastores.md) for the complete guide.

---

## File Management

`StarterFileManager` (and the deprecated `KmpFileManager`) live here, with typed file metadata via `StarterFile`.

See [Starter File Manager](../fundamentals/13-starter-file-manager.md) for full usage.

---

## Koin Module

The utils module ships an (empty-by-default) Koin module you can include:

```kotlin
startKoin {
    modules(utilsModule)
}
```

`utilsModule` includes `commonUtilsModule` and the platform-specific `platformUtilsModule`.

---

## Support My Project ☕️

If you find this project useful, consider supporting it by buying me a coffee. Your support will help me to continue working on this project and add more features.

<div >
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="150" />
  </a>
  <a href="https://www.youtube.com/@devatrii" target="_blank">
    <img src="https://img.shields.io/badge/YouTube-DevAtrii-red?style=for-the-badge&logo=youtube&logoColor=white" alt="YouTube Channel" />
  </a>
</div>
