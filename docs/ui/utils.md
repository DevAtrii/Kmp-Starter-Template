---
comments: false
icon: lucide/component
---

# UI Utils

The `starter/ui/utils` module is the **Compose utility layer** — ViewModel base classes, side-effect helpers, state-flow extensions, popups, theme dimensions, image converters, and cross-platform Compose services.

`components` and `layouts` both build on top of it, so you get all of this for free when you use them.

Add the dependency in your `commonMain` source set:

```kotlin title="build.gradle.kts"
implementation(projects.starter.ui.utils)
```

---

## ViewModels

### MviViewModel

The MVI base class for every screen. It enforces a clean unidirectional flow: **State**, **Actions**, and **Events**.

```kotlin
class HomeViewModel : MviViewModel<HomeState, HomeAction, HomeEvent>() {
    override val initialState get() = HomeState()

    override fun onStateStart() {
        loadData()
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.EnteringName ->
                _state.update { it.copy(name = action.name) }
            HomeAction.SaveButtonClicked ->
                emitEventAsync(HomeEvent.ShowSnackbar("Saved!"))
        }
    }
}
```

Key points:

- `state` — observable `StateFlow`, collected by the UI.
- `uiEvents` — `SharedFlow` for one-time side effects (navigation, snackbars).
- `emitEvent(event)` — `suspend`, use inside a coroutine.
- `emitEventAsync(event)` — launches on `viewModelScope` for you.
- `stateTimeoutMillis` — how long state stays alive after the screen hides (default 5000ms).

See [Mvi View Model](../fundamentals/03-mvi-viewmodel.md) for the full guide.

### Flow / Channel helpers

Launch collection and emission directly on the `viewModelScope` using context receivers:

```kotlin
context(viewModel: ViewModel)
fun <T> Flow<T>.collectInViewModel(collector: FlowCollector<T>)

context(viewModel: ViewModel)
fun <T> Channel<T>.sendInViewModel(value: T)

context(viewModel: ViewModel)
fun <T> MutableSharedFlow<T>.emitInViewModel(value: T)
```

---

## Side Effects

### ObserveAsEvents

A lifecycle-aware event collector — better than `LaunchedEffect` for one-time events, because it pauses when the app is in the background and resumes when it comes back.

```kotlin
ObserveAsEvents(flow = viewModel.uiEvents) { event ->
    when (event) {
        is HomeEvent.ShowSnackbar -> { /* show it */ }
    }
}
```

### LaunchOnce

Like `LaunchedEffect`, but the block runs **only once**, surviving recompositions and configuration changes.

```kotlin
LaunchOnce {
    viewModel.onAction(OnboardingAction.TrackShown)
}
```

### LaunchOnStart

Runs a suspend block every time the composable reaches `STARTED` lifecycle.

```kotlin
LaunchOnStart {
    viewModel.onAction(ScreenAction.Load)
}
```

---

## StateFlow Helpers

### reset

Reset a `MutableStateFlow` from a factory:

```kotlin
_state.reset { HomeState() }
```

### SavedStateHandle delegates

Persist `MutableStateFlow` values straight into `SavedStateHandle`:

```kotlin
class FormViewModel(
    private val savedStateHandle: SavedStateHandle,
) : MviViewModel<FormState, FormAction, FormEvent>() {

    context(this)
    private val name by savedStateHandle.getMutableStateFlow("", key = "name")
    // or getMutableStateFlow2(...) for transient-property control
}
```

`getMutableStateFlow2` adds a `transientProperties` set — those keys are kept fresh from defaults on restore and not persisted.

---

## Composition Locals

### LocalThemeMode

Read the current theme mode (`LIGHT` / `DARK` / `SYSTEM`) anywhere:

```kotlin
val mode = LocalThemeMode.current
```

---

## Theme

### Dimens

A central object of spacing, elevation, and size tokens. Use it instead of hardcoding `dp` values.

```kotlin
val size = Dimens.paddingMedium    // 16.dp
val h = Dimens.buttonHeight        // 50.dp
val r = Dimens.cardRadius          // 16.dp
```

### ThemeMode

- `ThemeMode.getIcon()` — returns a `LightMode` / `DarkMode` / `SettingsBrightness` icon.
- `isAppInDarkTheme()` — composable that reads the stored theme mode and resolves `SYSTEM` against the OS.

### Dynamic Color

`getDynamicColorScheme(darkTheme)` returns a Material You `ColorScheme` on supported platforms (or `null`).

```kotlin
val dynamic = getDynamicColorScheme(darkTheme = isDark)
```

---

## Color

### Color.fromHex

Parse a hex string into a `Color`. Supports `RRGGBB`, `AARRGGBB`, and `RRGGBBAA`.

```kotlin
val color = Color.fromHex("#FF6C63FF")          // ARGB
val safe  = Color.fromHex("#3B82F6", Color.Red) // with fallback
```

### toHexString

```kotlin
val hex = color.toHexString()                    // "#FF3B82F6"
val hex = color.toHexStringWithDefault("#FFFFFF")
```

### complimentaryColor / brightness

```kotlin
val compliment = color.complimentaryColor()
val bright = color.brightness()   // max of r/g/b
```

### ColorFilter.lightDarkTint

A composable that returns a tint matching the current theme (white on dark, black on light) — useful for image/icon tinting that needs to flip with the theme.

```kotlin
Icon(
    painter = ...,
    colorFilter = ColorFilter.lightDarkTint(),
)
```

!!! note "Deprecated helpers"
    The old `parseHexColor*` / `parseToColor` functions are deprecated — use `Color.fromHex` instead.

---

## Keyboard

### rememberKeyboardInfo

Observe the keyboard state cross-platform:

```kotlin
val keyboard = rememberKeyboardInfo()
val visible = keyboard.isVisible
val height = keyboard.height          // dp
val state by keyboard.keyboardState.collectAsState()   // KeyboardState(isVisible, height)
```

### isKeyboardVisible

Quick one-liner for showing/hiding UI based on the IME:

```kotlin
if (isKeyboardVisible()) { /* ... */ }
```

---

## FocusManager

Readable shortcuts for moving focus:

```kotlin
focusManager.moveDown()
focusManager.moveUp()
focusManager.moveNext()
focusManager.movePrevious()
focusManager.moveEnter()
focusManager.moveExit()
focusManager.moveLeft()
focusManager.moveRight()
```

---

## Screen

### getScreenSize / ScreenSizeValue

Get the screen width/height in `Dp`:

```kotlin
val size = getScreenSize()   // ScreenSize(width, height)
val size2 = ScreenSizeValue  // property-style
```

### ScreenController

Control the device brightness:

```kotlin
val controller = rememberScreenController()
controller.setBrightness(0.5f)
val b = controller.getBrightness()
controller.resetBrightness()
```

---

## Chrome Tabs

`rememberChromeTabs()` opens URLs in a custom tab (Android) or in-app browser (iOS):

```kotlin
val tabs = rememberChromeTabs()
tabs.open("https://starter.atherio.dev")
tabs.openElseDirect("https://starter.atherio.dev")
```

---

## Popups

### BaseDialog

A styled `Dialog` wrapper with rounded corners and sensible dismiss defaults. Build your dialogs on top of it.

```kotlin
BaseDialog(onDismiss = { showDialog = false }) {
    // dialog content
}
```

### DeleteDialog

A ready-made Material 3 delete confirmation dialog with an error-colored confirm button.

```kotlin
DeleteDialog(
    title = "Delete item?",
    message = "This action cannot be undone.",
    confirmText = "Delete",
    onConfirmDelete = { /* delete */ },
    onDismiss = { showDialog = false },
)
```

### DatePickerDialog

A Material 3 date picker dialog wired to `kotlinx.datetime.LocalDate`, with a "past or present" selectable-dates default.

```kotlin
DatePickerDialog(
    selectedDate = initialDate,
    onDateSelected = { date -> /* LocalDate */ },
    onDismiss = { showPicker = false },
)
```

### BaseBottomSheet

A `ModalBottomSheet` wrapper that animates its top corners when fully expanded, and hides itself "properly" before calling `onDismiss`.

```kotlin
BaseBottomSheet(
    sheetState = sheetState,
    onDismiss = { showSheet = false },
) {
    // sheet content
}
```

It also ships `SheetState.hideProperly(scope) { onHidden -> }` so you can safely dismiss the sheet and only remove it from composition after it's hidden.

---

## Modifiers

### grayScale

Render any composable in grayscale:

```kotlin
Image(
    painter = ...,
    modifier = Modifier.grayScale(),
)
```

### customOverscroll

An iOS-style overscroll effect for `LazyListState` or `PagerState` (Android). Used internally by `CupertinoLazyColumn`.

```kotlin
Modifier.customOverscroll(
    listState = state,
    onNewOverscrollAmount = { amount -> /* apply offset */ },
)
```

---

## Image Helpers

### ImageBitmap

```kotlin
val bytes = imageBitmap.toByteArray(ImageBitmapCompressFormat.COMMON_PNG)
val b64   = imageBitmap.toBase64String(ImageBitmapCompressFormat.COMMON_WEBP)

val resized = imageBitmap.resize(width = 128, height = 128)

val decoded = ImageBitmap.fromByteArray(bytes)         // suspend
val fromB64 = ImageBitmap.fromBase64String(b64)        // suspend

val compressed = imageBitmap.compress(ImageBitmapCompressFormat.COMMON_JPEG, quality = 60)
```

### ImageVector

```kotlin
val painter = icon.asVectorPainter()
val bitmap  = icon.toImageBitmap(size = Size(64f, 64f))
val bytes   = icon.toByteArray(ImageBitmapCompressFormat.COMMON_PNG)
val b64     = icon.toBase64String(ImageBitmapCompressFormat.COMMON_WEBP)
```

### Base64 decoding

`decodeBase64ToImageBitmap(base64)` turns a Base64 string into an `ImageBitmap` (strips common `data:image/...;base64,` prefixes).

### ByteString

`ByteString.cleanBase64Web(input)` strips data-URL prefixes and whitespace from a Base64 web string.

---

## DataStore (Compose)

Read DataStore values directly in Compose:

```kotlin
val theme by rememberStringDataStore("theme", "LIGHT")     // State<T>
var count by rememberMutableIntDataStore("count", 0)       // MutableState<T>
```

`rememberSerializableDataStore` / `rememberMutableSerializableDataStore` do the same for `@Serializable` objects. See [DataStores](../fundamentals/05-datastores.md).

---

## Previews

### AllDevicePreviews

A single annotation that renders phone, foldable, tablet, and desktop previews:

```kotlin
@AllDevicePreviews
@Composable
private fun MyComposablePreview() {
    MyComposable()
}
```

---

## Store Reviews & Updates

`rememberStarterStoreManager()` and `rememberUpdateLauncher()` live here, powering `AppUpdateProvider`. See [Store Reviews & Updates](../fundamentals/09-store-reviews-and-updates.md).

---

## Files

`rememberStarterFileManager()` gives you a Compose-aware `StarterFileManager` (bound to the Android Activity). See [Starter File Manager](../fundamentals/13-starter-file-manager.md).

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
