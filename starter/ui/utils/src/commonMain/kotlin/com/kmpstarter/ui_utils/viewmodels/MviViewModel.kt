/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

package com.kmpstarter.ui_utils.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * A base ViewModel implementation for the MVI (Model-View-Intent) architectural pattern.
 * * This class enforces a unidirectional data flow:
 * 1. **Actions**: User intentions (e.g., clicks, text input) are sent via [onAction].
 * 2. **State**: The UI observes [state] to render the current data.
 * 3. **Events**: One-time UI side-effects (e.g., navigation, toasts) are sent via [uiEvents].
 *
 * @param STATE The data class representing the UI state.
 * @param ACTIONS The sealed class representing user interactions.
 * @param EVENTS The sealed class representing one-time side effects.
 * @param stateTimeoutMillis The delay (in ms) between the last subscriber disappearing
 *
 * ### Example Usage:
 * ```kotlin
data class ProfileState(
val name: String = "",
val isLoading: Boolean = false
)

sealed class ProfileActions {
data class UpdateName(val name: String) : ProfileActions()
data object SaveProfile : ProfileActions()
}

sealed class ProfileEvents {
data class ShowToast(val message: String) : ProfileEvents()
}

class ProfileViewModel : MviViewModel<ProfileState, ProfileActions, ProfileEvents>() {

override val initialState = ProfileState()

override fun onAction(action: ProfileActions) {
when (action) {
is ProfileActions.UpdateName -> {
_state.update { it.copy(name = action.name) }
}

is ProfileActions.SaveProfile -> {
emitEvent(ProfileEvents.ShowToast("Profile Saved!"))
}
}
}
}
 * ```
 */
abstract class MviViewModel<STATE, ACTIONS, EVENTS>(
    stateTimeoutMillis: Long = 5000L,
) : ViewModel() {

    /**
     * Defines the initial state of the UI when the ViewModel is first created.
     */
    abstract val initialState: STATE

    protected val _state by lazy { MutableStateFlow(initialState) }

    /**
     * An observable stream of [STATE]. The UI should collect this to update its components.
     */
    val state = _state.onStart {
        // load init values here
        onStateStart()
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(stateTimeoutMillis),
        initialValue = _state.value
    )

    private val _uiEvents = MutableSharedFlow<EVENTS>(replay = 0)

    /**
     * An observable stream of [EVENTS] for one-time side effects (e.g., SnackBar, Navigation).
     */
    val uiEvents = _uiEvents.asSharedFlow()

    /**
     * The entry point for UI interactions. All user intents must be passed through this function.
     * @param action The specific user interaction to process.
     */
    abstract fun onAction(action: ACTIONS)

    /**
     * Optional lifecycle hook triggered when the [state] flow begins collection.
     * Useful for refreshing data or starting sensors when the UI becomes active.
     */
    open fun onStateStart() {}

    /**
     * Triggers a one-time UI event.
     * @param event The event to be dispatched to the UI.
     */
    protected suspend fun emitEvent(event: EVENTS) {
        _uiEvents.emit(event)
    }

    /**
     * Triggers a one-time UI event inside viewModelScope.
     * @param event The event to be dispatched to the UI.
     */
    protected   fun emitEventInViewModel(event: EVENTS) {
        viewModelScope.launch {
            emitEvent(event = event)
        }
    }
}


sealed class DummyActions {}
sealed class DummyEvents {}