---
comments: false
icon: lucide/puzzle
---

# UI Components

The Starter Template ships a bunch of **ready-made Compose Multiplatform components** inside the `starter/ui/components` module.

They cover common screens you'll build over and over — buttons, text fields, dialogs headers, cards, chips, animations, and iOS-style (Cupertino) widgets. Everything is theme-aware, so they pick up your `MaterialTheme` colors automatically.

Add the dependency in your `commonMain` source set:

```kotlin title="build.gradle.kts"
implementation(projects.starter.ui.components)
```

!!! note "Depends on UI Utils"
    `components` implements `ui-utils`, so you get all the `ui-utils` helpers too (spacers, `Dimens`, popups, etc.) without extra wiring.

---

## Buttons

### KmpButton

A full-width, uniform-height button. Uses `Dimens.buttonHeight` (50dp) so every primary action in your app looks consistent.

```kotlin
KmpButton(
    enabled = true,
    label = "Continue",
    onClick = { /* ... */ },
)
```

### LoadingButton

A button that swaps its content for a spinner while a task is running. It disables itself while loading so the user can't double-tap.

```kotlin
LoadingButton(
    enabled = true,
    text = "Save",
    loadingText = "Saving…",
    isLoading = saving,
    onClick = { viewModel.onAction(FormAction.Save) },
)
```

### GenerateAiButton

A "Google Blue" button with a sparkle icon that animates into a spinner while generating. Pass `onDisabledClick` to react when it's tapped while disabled.

```kotlin
GenerateAiButton(
    isGenerating = generating,
    text = "Generate",
    generatingText = "Generating…",
    onClick = { /* start generation */ },
)
```

### GoogleSignInButtonUI

A bordered "Continue with Google" button that animates its width down to a circular spinner while loading.

```kotlin
GoogleSignInButtonUI(
    isLoading = signingIn,
    icon = googleLogo, // Painter
    onGoogleSignInClick = { /* trigger sign in */ },
)
```

### TimerCloseButton

Shows a circular countdown that turns into a close button when the timer finishes. Handy for "dismiss in X seconds" banners or ads.

```kotlin
TimerCloseButton(
    timeMillis = 5000L,
    onClick = { /* close */ },
)
```

---

## Material Cupertino (iOS style)

### PillActionButton

A pill-shaped `FilledTonalButton` with an icon on the left and text on the right.

```kotlin
PillActionButton(
    text = "Share",
    icon = Icons.Default.Share,
    onClick = { /* share */ },
)
```

### PillActionsContainer

A rounded pill `Surface` that groups several actions in a row with spacing.

```kotlin
PillActionsContainer {
    PillActionButton(text = "Like", icon = Icons.Default.ThumbUp, onClick = {})
    PillActionButton(text = "Save", icon = Icons.Default.Bookmark, onClick = {})
}
```

### CupertinoSwitch

An iOS-inspired toggle switch using Material 3 colors. Two overloads: the full one lets you tweak size/shape/colors, the short one gives sensible iOS defaults.

```kotlin
var isOn by remember { mutableStateOf(false) }

CupertinoSwitch(
    checked = isOn,
    onCheckedChange = { isOn = it },
)
```

---

## Progress

### StepsProgress

An onboarding-style progress bar: circular step indicators joined by animated progress lines. `currentStep` is **1-based**.

```kotlin
StepsProgress(
    steps = 4,
    currentStep = 2,
)
```

Completed steps show a check icon; the current step is highlighted with the primary color.

---

## Sections & Rows

### CupertinoSection

An iOS "grouped list" section: an uppercase header, a rounded surface container, and an optional description below.

```kotlin
CupertinoSection(
    title = "Account",
    description = "Manage your profile settings",
) {
    CupertinoSectionRow(/* ... */)
    CupertinoSectionRow(/* ... */)
}
```

### CupertinoSectionRow

One row inside a section. It comes in a few overloads depending on what you need on the right side:

**Plain value**

```kotlin
CupertinoSectionRow(
    label = "Username",
    value = "@atrii",
    icon = Icons.Default.Person,
    isLast = true,
)
```

**Switch**

```kotlin
CupertinoSectionRow(
    label = "Dark mode",
    icon = Icons.Default.DarkMode,
    isSwitchChecked = darkMode,
    onSwitchChange = { darkMode = it },
)
```

**Stepper**

```kotlin
CupertinoSectionRow(
    label = "Quantity",
    icon = Icons.Default.AddShoppingCart,
    value = quantity,
    onIncrement = { quantity++ },
    onDecrement = { quantity-- },
)
```

**Dropdown** (works together with `CupertinoDropdownMenu`)

```kotlin
var expanded by remember { mutableStateOf(false) }

CupertinoSectionRow(
    label = "Language",
    value = selectedLanguage,
    icon = Icons.Default.Language,
    isExpanded = expanded,
    onExpandedChange = { expanded = it },
) {
    CupertinoDropdownItem(text = "English", onClick = { expanded = false })
    CupertinoDropdownItem(text = "Spanish", onClick = { expanded = false })
}
```

---

## Dropdowns

### CupertinoDropdownMenu

An iOS-style dropdown popup with spring scale/fade animations. Must be used inside an `ExposedDropdownMenuBox` scope.

```kotlin
ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = it },
) {
    // anchor
    CupertinoDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        CupertinoDropdownItem(text = "Option A", onClick = { expanded = false })
        CupertinoDropdownItem(text = "Option B", onClick = { expanded = false })
    }
}
```

### CupertinoDropdownItem

A single item. Mark it `isSelected` to show a checkmark on the right, and `showDivider` to draw a divider below.

```kotlin
CupertinoDropdownItem(
    text = "Dark",
    isSelected = true,
    leadingIcon = Icons.Default.DarkMode,
    showDivider = true,
    onClick = { /* select */ },
)
```

---

## Text

### HeaderText

A centered screen header with a big primary title and a subtitle.

```kotlin
HeaderText(
    title = "Welcome back",
    subtitle = "Sign in to continue",
)
```

### SheetDialogHeader

A header row with a title (plus optional description) and a circular close button. Ideal for dialogs and bottom sheets.

```kotlin
SheetDialogHeader(
    label = "Filter",
    description = "Narrow down results",
    onCloseClicked = { /* dismiss */ },
)
```

### ErrorText

A small, error-colored label for form or field errors.

```kotlin
ErrorText(error = "Invalid email address")
```

### FiltrationLabel

A subtle label for grouping/filtering sections.

```kotlin
FiltrationLabel(text = "Sort by")
```

### DividerWithText

Text centered between two horizontal dividers — the classic "or continue with" separator.

```kotlin
DividerWithText(text = "or")
```

### TextWithButton

A line of text with an inline text button (like "Don't have an account? Sign up").

```kotlin
TextWithButton(
    text = "Don't have an account?",
    buttonLabel = "Sign up",
    onClick = { /* navigate */ },
)
```

---

## Cards

### SelectableListCard

A selectable card with an animated check indicator. The container and border change when selected.

```kotlin
SelectableListCard(
    isSelected = selected,
    onClick = { selected = !selected },
) {
    Text("Option one")
    // more content
}
```

---

## Forms

### CleanForm

A titled form section container. Use it to group related fields with consistent spacing.

```kotlin
CleanForm(title = "Personal info") {
    FormTextField(/* ... */)
    FormPasswordTextField(/* ... */)
}
```

---

## Images

### CoilImage

An async image loader backed by Coil 3, with SVG support, caching, custom headers, and a loading placeholder/progress.

```kotlin
CoilImage(
    url = "https://example.com/avatar.jpg",
    placeHolder = Res.drawable.placeholder,
    contentScale = ContentScale.Crop,
)

// SVG
CoilImage(
    url = "https://example.com/logo.svg",
    imageType = CoilImageType.SVG,
)
```

### Base64Image

Renders an image from a Base64 string. It strips the `data:image/png;base64,` prefix automatically and shows a placeholder if decoding fails.

```kotlin
Base64Image(
    base64String = "iVBORw0KGgoAAAANSUhEUg...",
    contentDescription = "Profile picture",
) {
    // placeholder on failure
}
```

---

## Text Fields

### FormTextField

A full-width outlined field with built-in error display and IME-action handling. It works with a `FieldState` (from `starter/utils`) or with plain `value` + `error` strings.

```kotlin
FormTextField(
    value = nameState,            // FieldState
    onValueChange = { viewModel.onAction(FormAction.NameChanged(it)) },
    label = "Full name",
    imeAction = ImeAction.Next,
    onImeAction = { viewModel.onAction(FormAction.Next) },
)
```

!!! tip "safeImeAction"
    By default `safeImeAction = true`, so the IME action won't fire while the field is empty or has an error. Pass `clearFocusOnImeAction = true` to dismiss the keyboard on `Done`.

### FormPasswordTextField

Same as `FormTextField` but with a lock icon, a show/hide visibility toggle, and password masking.

```kotlin
FormPasswordTextField(
    value = passwordState,
    onValueChange = { viewModel.onAction(FormAction.PasswordChanged(it)) },
    label = "Password",
    imeAction = ImeAction.Go,
    onImeAction = { viewModel.onAction(FormAction.Submit) },
)
```

### SearchTextField

A search field with a search icon, a clear button, and a `Search` IME action.

```kotlin
SearchTextField(
    value = query,
    onValueChange = { query = it },
    label = "Search",
    placeholder = "Type to search…",
    onSearch = { /* perform search */ },
)
```

---

## Lists

### CupertinoLazyColumn

A `LazyColumn` with iOS-style overscroll (on Android), a clipping shape, and automatic focus clearing while scrolling.

```kotlin
CupertinoLazyColumn(
    contentPadding = PaddingValues(16.dp),
) {
    items(data) { item ->
        // item UI
    }
}
```

### ScrollableColumn

A simpler scrollable column for when you don't need a full lazy list.

```kotlin
ScrollableColumn {
    // normal Column content
}
```

---

## Animations

### FadeIn

Fades its content in, optionally after a delay. `FadeInTokens` provides pre-set delay constants for staggering.

```kotlin
FadeIn(delayMillis = FadeInTokens.DELAY_1) {
    Text("Hello")
}
```

### Floating

A gentle figure-8 floating animation with optional rotation and scale "breathing". Great for hero icons or decorative elements.

```kotlin
Floating(
    amplitudeX = 6f,
    amplitudeY = 12f,
) {
    Image(painter = star, contentDescription = null)
}
```

---

## Top Bars

### SimpleNavigationTopBar

A `TopAppBar` with a title, navigation icon, and action slots.

```kotlin
SimpleNavigationTopBar(
    title = "Settings",
    onNavigationClick = { navigator.navigateUp() },
    actions = {
        IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null) }
    },
)
```

### SimpleMediumNavigationTopBar

The medium variant for a taller, more prominent header.

```kotlin
SimpleMediumNavigationTopBar(
    title = "Settings",
    onNavigationClick = { navigator.navigateUp() },
)
```

---

## Chips

### FiltrationChip

A `FilterChip` with animated leading/trailing icons. When selected, it shows a remove ("X") button by default.

```kotlin
FiltrationChip(
    label = "Recent",
    selected = selected,
    leadingIcon = Icons.Default.Schedule,
    onRemoveFilter = { /* clear filter */ },
    onClick = { selected = !selected },
)
```

---

## Other

### LockOverlay

A translucent overlay with a lock icon, used to cover locked/premium content.

```kotlin
LockOverlay(onClick = { /* prompt unlock */ })
```

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
