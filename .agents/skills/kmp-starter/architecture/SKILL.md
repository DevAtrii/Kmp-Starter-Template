---
name: kmp-starter-architecture
description: Clean Architecture module and package layout for KMP Starter Template features — data/domain/presentation layers, core dependency rule, screen-based package organization, the Screen vs Content composable split, and dialog/bottom-sheet (BaseDialog/BaseBottomSheet) conventions.
version: 1
author: DevAtrii
license: MIT

---

# Architecture

## Layers

Every feature follows Clean Architecture with three layers:

```
features/{FeatureName}/
    data/         # repositories, data sources, DI
    domain/       # Logics (use cases), repositories (interfaces), models
    presentation/ # UI, ViewModels, State, Actions, Events
```

- `data` → implementations of domain interfaces.
- `domain` → the blueprint: Logics, repository interfaces, models.
- `presentation` → Compose UI + MVI.

## Module map (from `settings.gradle.kts`)

- `composeApp` — shared Compose code, glues all modules.
- `androidApp` — Android host app.
- `features/{feature}/*` — one module per feature, split into layers.
- `features/core/*` — shared foundation (data/domain/presentation).
- `features/database`, `navigation`, `resources`, `analytics`, `purchases`, `remote_config`, `notifications`, `locale` — infra features.
- `starter/*` — reusable starter modules (core, utils, ui utils, ui components, ui layouts, native bindings).

## Core dependency rule

All features may depend on `core`. `core` must **never** depend on a feature.

```
features/* → core   (correct)
core → features/*   (wrong)
```

Put shared code in `features/core/*` only when multiple features need it: shared auth, common DataStores, base repos/Logics, splash, onboarding, shared UI, global helpers.

## Feature structure

A placeholder module lives at `features/your-feature/`. Rename it to your feature (e.g. `notes`) and develop there. Refer to `https://starter.atherio.dev/fundamentals/12-writing-your-code/` for the rename steps (`settings.gradle.kts` + project accessors + package rename).

## Package organization — by screens

Organize by **screen**, not technical category. Mirror the same package structure across layers:

```
presentation/notes/{list,details,edit}/_components/  _utils/
domain/notes/{list,details,edit}/
data/notes/{list,details,edit}/
```

- Keep related code close together.
- Promote code upward only after multiple consumers exist.
- Shared infra inside a feature uses `_` prefix: `_components`, `_utils`, `_navigation`, `_mappers`.

## Screen vs Content composable

Split every screen into two parts:

1. **Screen Composable** — the "brain": gets data from the ViewModel, handles navigation callbacks, observes UI events, knows where data comes from (DI / nav params).
2. **Content Composable** — display only: takes `state` and callbacks like `onAction`. No ViewModel or data-source knowledge.

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onTaskComplete: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    ObserveAsEvents(flow = viewModel.uiEvents) { event ->
        when (event) {
            is HomeEvents.ShowSnackbar -> SnackbarController.sendMessage(event.message)
            HomeEvents.OnTaskComplete -> onTaskComplete()
        }
    }
    HomeScreenContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun HomeScreenContent(state: HomeState, onAction: (HomeActions) -> Unit) {
    Scaffold { /* build UI from state + onAction */ }
}
```

This makes screens previewable, testable, and reusable.

## Dialogs & bottom sheets

Always build on Starter's popup primitives. Do **not** call raw `Dialog` / `ModalBottomSheet` directly.

- `starter/ui/utils/.../ui_utils/popups/dialogs/BaseDialog.kt`
- `starter/ui/utils/.../ui_utils/popups/bottom_sheets/BaseBottomSheet.kt`

### BaseDialog

```kotlin
BaseDialog(onDismiss = { showDialog = false }) {
    // content; already wrapped in a themed Surface (90% width, rounded corners)
}
```

`BaseDialog` wraps `Dialog` + a `Surface` (`fillMaxWidth(0.9f)`, `RoundedCornerShape(20.dp)`, `surfaceContainerLowest`). Default `DialogProperties` dismiss on back press and outside click.

### BaseBottomSheet

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheet(
    sheetState: SheetState,
    onResult: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(sheetState = sheetState, onDismiss = onDismiss) {
        // ColumnScope content
    }
}
```

- Requires a `SheetState` — create with `rememberModalBottomSheetState(skipPartiallyExpanded = true)`.
- Dismiss cleanly with the `hideProperly` util (runs `onDismiss` only after the hide animation finishes):

```kotlin
val scope = rememberCoroutineScope()
// inside a click handler:
scope.launch { sheetState.hideProperly { onDismiss() } }
```

- In the screen, drive visibility with a boolean: `var showX by rememberSaveable { mutableStateOf(false) }`; `onDismiss = { showX = false }`. Never store sheet/dialog visibility in `State` (see core-rules).

### Placement convention

- One-off dialogs → `.../_components/dialogs/`
- One-off bottom sheets → `.../_components/sheets/`
- Keep the wrapper (popup shell) separate from its `*Content` composable, mirroring the Screen/Content split. Keep transient popup state (text fields, picker state) inside the popup composable, not the ViewModel.

## Reusable Starter UI

Reuse these instead of hand-rolling equivalents (Golden Rule):

- `starter/ui/components` — `CupertinoSection`, `CupertinoSectionRow`, `PillActionButton`, `PillActionsContainer`, `CoilImage`, `ScrollableColumn`, `CupertinoDropdownMenu`, `FormTextField`, `LoadingButton`, `SearchTextField`, `FiltrationChip`, etc.
- `starter/ui/layouts` — `LoadingLayout`, `EmptyStateWithAction`.
- `starter/ui/utils` — `Dimens` (spacing/sizing), `VerticalSpacer`, `toActualString()`.

Reference the canonical onboarding slice in `features/core/` for layout and composable structure.

## Reference implementation

Mirror the onboarding slice in `features/core/`:

- `features/core/domain/.../repositories/OnboardingRepository.kt` — interface
- `features/core/data/.../repositories/OnboardingRepositoryImpl.kt` — impl
- `features/core/domain/.../logics/` — Logics
- `features/core/presentation/.../viewmodels/OnboardingViewModel.kt` — MVI
