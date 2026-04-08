---
comments: false
icon: lucide/view
---

# Mvi View Model

when you are making screens in CMP, it's best to use `MviViewModel` from `ui-utils` module. it helps you to manage everything in one place & follows MVI pattern properly. 

if you have a very simple screen maybe you don't need it but actually any screen can become complex later, so i always use it for all my screens.

## Basic Overview
MVI is simple, it just has 3 main things:

1. **State**: Data that shows on UI (like text, loading state, etc)
2. **Actions**: What user does on screen (clicks, typing text)
3. **Events**: Side effects that happen once (Navigation, SnackBars, simple alerts)

---

## Simple Example

here is a basic example of how you can use it:

```kotlin
// define state
data class HomeState(
    val name: String = "",
    val isLoading: Boolean = false
)

// define actions user can take
sealed class HomeAction {
    data class EnteringName(val name: String) : HomeAction()
    object SaveButtonClicked : HomeAction()
}

// define events (one-time things)
sealed class HomeEvent {
    data class ShowSnackbar(val message: String) : HomeEvent()
}

// implement viewmodel
// stateTimeoutMillis = 5000L (5 seconds) by default. 
// it's mean how long state stays alive after screen is hidden
class HomeViewModel : MviViewModel<HomeState, HomeAction, HomeEvent>(
    stateTimeoutMillis = 5_000L 
) {

    override val initialState get() = HomeState()

    // it runs when UI starts showing this screen
    override fun onStateStart() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // delay(2000) // simulate network call
            _state.update { it.copy(isLoading = false, name = "Starter Template") }
        }
    }

    override fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.EnteringName -> {
                _state.update { it.copy(name = action.name) }
            }
            HomeAction.SaveButtonClicked -> {
                // save logic here...
                emitEventInViewModel(HomeEvent.ShowSnackbar("Saved successfully!"))
            }
        }
    }
}
```

## Functions Breakdown

### 0. stateTimeoutMillis
when you hide the app or go to another screen, the state doesn't die immediately. it waits for some time. by default it is 5 seconds. 

you can change it in constructor:
```kotlin
class MyViewModel : MviViewModel<State, Action, Event>(stateTimeoutMillis = 10_000L) // 10 seconds
```

### 1. initialState
this is very important, you define how screen looks when it first starts. Always use `get()` to initialize it otherwise it'll throw error & your app will crash.

```kotlin
override val initialState get() = HomeState()
```

### 2. onAction(action)
all user interactions should come here. inside this function you use `when` to handle different actions. it's mean your logic is not scattered everywhere.

```kotlin
override fun onAction(action: HomeAction) {
    when(action) {
        is HomeAction.SaveButtonClicked -> { /* handle click */ }
    }
}
```

### 3. onStateStart()
this is a lifecycle hook. it runs automatically when UI starts observing the state. you can use it to load initial data like from API or Database.

```kotlin
override fun onStateStart() {
    loadData() // perfect place for this
}
```

### 4. emitEvent() & emitEventInViewModel()
when you want to show Snackbar or Navigate, don't update state. instead use events.

- `emitEvent(event)`: it is `suspend` function, use it if you are already inside a coroutine.
- `emitEventInViewModel(event)`: use this for most cases, it will launch coroutine and send event for you.

```kotlin
// inside onAction
emitEventInViewModel(HomeEvent.ShowSnackbar("Hello!"))
```

## Listening to Events in UI

when your Viewmodel sends an event (like a Snackbar or Navigation), you need to catch it in your UI. for this [Phillip Lackner](https://www.youtube.com/@PhilippLackner) made a special helper called `ObserveAsEvents`. 

it is better than using `LaunchedEffect` because it is lifecycle aware. it's mean it won't trigger if your app is in background, it waits until app comes back to foreground.

### UI Example
here is how you can use it in your Composable screen:

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // listening to events
    ObserveAsEvents(flow = viewModel.uiEvents) { event ->
        when(event) {
            is HomeEvent.ShowSnackbar -> {
                // show your snackbar here...
                println("Event Received: ${event.message}")
            }
        }
    }

    // your UI logic...
    Column {
        Text(text = state.name)
        Button(onClick = { viewModel.onAction(HomeAction.SaveButtonClicked) }) {
            Text("Save")
        }
    }
}
```

using `ObserveAsEvents` makes your code look super clean & easy to manage events.