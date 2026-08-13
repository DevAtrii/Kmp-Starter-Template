---
name: kmp-starter-ui
description: Reusable Compose UI kit on the KMP Starter Template — starter/ui/components, starter/ui/utils, and starter/ui/layouts. Know what's already built before writing new UI.
version: 1
author: DevAtrii
license: MIT

---

# UI

Reuse Starter's Compose UI kit. Do not write a component, layout, or Compose helper if Starter already ships one.

Modules (all in `starter/`):

| Module | Accessor | Contents |
| --- | --- | --- |
| `starter/ui/components` | `projects.starter.ui.components` | Reusable composables |
| `starter/ui/utils` | `projects.starter.ui.utils` | Compose utilities, ViewModels, popups, theme, modifiers |
| `starter/ui/layouts` | `projects.starter.ui.layouts` | Full-screen layout composables |

Dep chain: `layouts` → `components` → `ui/utils` → `starter/utils`. Adding a higher module pulls the lower ones in automatically. Non-UI helpers live in `starter/utils` (see the [utils](../utils/SKILL.md) skill).

## components — reusable composables

- **Buttons**: `KmpButton`, `LoadingButton`, `GenerateAiButton`, `GoogleSignInButtonUI`, `TimerCloseButton`, `PillActionButton`, `PillActionsContainer`.
- **Cupertino (iOS style)**: `CupertinoSwitch`, `CupertinoSection`, `CupertinoSectionRow` (value / switch / stepper / dropdown overloads), `CupertinoDropdownMenu`, `CupertinoDropdownItem`, `CupertinoLazyColumn`.
- **Text**: `HeaderText`, `SheetDialogHeader`, `ErrorText`, `FiltrationLabel`, `DividerWithText`, `TextWithButton`.
- **Text fields**: `FormTextField`, `FormPasswordTextField`, `SearchTextField` (use `FieldState` from `starter/utils`).
- **Other**: `StepsProgress`, `SelectableListCard`, `CleanForm`, `CoilImage`, `Base64Image`, `FiltrationChip`, `FadeIn`, `Floating`, `SimpleNavigationTopBar`, `SimpleMediumNavigationTopBar`, `LockOverlay`, `ScrollableColumn`.

## ui/utils — Compose utilities

- **MVI**: `MviViewModel<State, Action, Event>` (always use, never plain `ViewModel`), `Flow.collectInViewModel`, `Channel.sendInViewModel`, `MutableSharedFlow.emitInViewModel`.
- **Side effects**: `ObserveAsEvents`, `LaunchOnce`, `LaunchOnStart`.
- **StateFlow**: `MutableStateFlow.reset`, `SavedStateHandle.getMutableStateFlow` / `getMutableStateFlow2`.
- **Popups**: `BaseDialog`, `DeleteDialog`, `DatePickerDialog`, `BaseBottomSheet`, `SheetState.hideProperly`.
- **Theme**: `Dimens`, `LocalThemeMode`, `ThemeMode.getIcon`, `isAppInDarkTheme`, `getDynamicColorScheme`.
- **Color**: `Color.fromHex`, `Color.toHexString`, `complimentaryColor`, `brightness`, `ColorFilter.lightDarkTint`.
- **Modifiers**: `Modifier.grayScale()`, `Modifier.customOverscroll(...)`.
- **Image**: `ImageBitmap.toByteArray/toBase64String/resize/compress/fromByteArray/fromBase64String`, `ImageVector.asVectorPainter/toImageBitmap/toByteArray/toBase64String`, `decodeBase64ToImageBitmap`.
- **Cross-platform services**: `rememberKeyboardInfo`, `isKeyboardVisible`, `getScreenSize`/`ScreenSizeValue`, `rememberScreenController`, `rememberChromeTabs`, `rememberStarterStoreManager`, `rememberUpdateLauncher`/`AppUpdateProvider`, `rememberStarterFileManager`.
- **DataStore (Compose)**: `remember*DataStore` / `rememberMutable*DataStore`.
- **Preview**: `@AllDevicePreviews`.
- **Focus**: `FocusManager.moveDown/Up/Next/Previous/...`.

## layouts — full-screen states

- `LoadingLayout` — centered spinner, theme-aware dimmed background.
- `EmptyStateWithAction` — hero icon + title + description + CTA button.
- `MeasurableLayout` — internal; exposes `LocalScreenSize` (`Offset`).

## Rules

- **Search before building.** For every new composable/Compose helper, check the three modules first — reuse or extend, never duplicate.
- Components belong in `starter/ui/components` only if reusable; app-specific UI stays in your feature's presentation layer.
- Use `Dimens` for spacing/size, never hardcoded `dp`.
- Use `FieldState` + `FormTextField` for form fields.
- Use `MviViewModel`, `ObserveAsEvents`, and the utils above — don't hand-roll equivalents.
- Color parsing → `Color.fromHex` (the `parseHexColor*` functions are deprecated).

## Reference

- Docs (live site): `https://starter.atherio.dev/ui/components/`, `https://starter.atherio.dev/ui/utils/`, `https://starter.atherio.dev/ui/layouts/`
- Source: `starter/ui/components/`, `starter/ui/utils/`, `starter/ui/layouts/` (package roots `com.kmpstarter.ui_components.*`, `com.kmpstarter.ui_utils.*`, `com.kmpstarter.ui_layouts.*`)
