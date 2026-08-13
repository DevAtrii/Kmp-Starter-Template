---
name: kmp-starter-mvi
description: How to implement MVI on the KMP Starter Template — State, Actions, Events, and the MviViewModel base class (initialState, onAction, onStateStart, emitEvent/emitEventAsync).
version: 1
author: DevAtrii
license: MIT

---

# MVI

Every ViewModel extends Starter's `MviViewModel<State, Action, Event>` from:

```
com.kmpstarter.ui_utils.viewmodels.MviViewModel
```

Do **not** write plain `ViewModel` classes. Always subclass `MviViewModel`. Each ViewModel consists of State, Actions, Events. Keep terminology exact — no Intent/Effect/Mutation.

## Shape

```kotlin
data class HomeState(
    val name: String = "",
    val isLoading: Boolean = false,
)

sealed class HomeAction {
    data class EnteringName(val name: String) : HomeAction()
    data object SaveButtonClicked : HomeAction()
}

sealed class HomeEvent {
    data class ShowSnackbar(val message: String) : HomeEvent()
}

class HomeViewModel : MviViewModel<HomeState, HomeAction, HomeEvent>() {
    override val initialState get() = HomeState()

    override fun onStateStart() {
        loadData()
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.EnteringName -> _state.update { it.copy(name = action.name) }
            HomeAction.SaveButtonClicked -> emitEventAsync(HomeEvent.ShowSnackbar("Saved!"))
        }
    }
}
```

## Key rules

- `initialState` **must** use `get()`: `override val initialState get() = State()`. A non-`get()` initializer crashes.
- Never read `SavedStateHandle` inside `initialState`. Read it in `init {}`, then `_state.update { ... }`.
- `onAction(action)` — single entry point for all user interactions; `when` dispatch here.
- `onStateStart()` — lifecycle hook, runs when UI starts collecting `state`; load initial data here.
- Update state with `_state.update { it.copy(...) }`.

## Constructor params into state

`MviViewModel` supports constructor args too — do not switch to a plain `ViewModel` for parameterized screens.

```kotlin
class NotesViewModel(private val noteId: String) : MviViewModel<NotesState, NotesAction, NotesEvent>() {
    override val initialState get() = NotesState()

    init {
        _state.update { it.copy(noteId = noteId) }
    }
}
```

Pass nav params via `parameterOf(...)` + `koinViewModel { parametersOf(...) }` (see koin skill).

## Emitting events

Always use the functions provided by `MviViewModel`:

- `emitEvent(event)` — `suspend`; use inside an existing coroutine.
- `emitEventAsync(event)` — non-suspend; launches in `viewModelScope`. Prefer for one-offs.
- `emitEventInViewModel` is **deprecated** (renamed to `emitEventAsync`).

Never hand-roll your own `Channel`/`SharedFlow` event stream — `MviViewModel.uiEvents` already exposes one (`SharedFlow`, `replay = 0`).

## Observing events in UI

```kotlin
import com.kmpstarter.ui_utils.side_effects.ObserveAsEvents

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvents) { event ->
        when (event) {
            is HomeEvent.ShowSnackbar -> SnackbarController.sendMessage(event.message)
        }
    }

    // render state, send actions
}
```

`ObserveAsEvents` is lifecycle-aware (won't fire in background); prefer it over `LaunchedEffect`.

## Reference

`features/core/presentation/.../viewmodels/OnboardingViewModel.kt` is the canonical example (State/Actions/Events, Logics, `EventsTracker`, `emitEvent`).
