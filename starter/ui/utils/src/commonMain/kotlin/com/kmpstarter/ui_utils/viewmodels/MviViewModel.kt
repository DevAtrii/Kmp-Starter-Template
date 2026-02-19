/*
 *
 *  *
 *  *  * Copyright (c) 2025
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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<STATE, ACTIONS, EVENTS> : ViewModel() {

    abstract val initialState: STATE
    protected val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<EVENTS>(replay = 0)
    val uiEvents = _uiEvents.asSharedFlow()

    abstract fun onAction(action: ACTIONS)

    protected fun emitEvent(event: EVENTS) {
        viewModelScope.launch {
            _uiEvents.emit(event)
        }
    }
}

/**
  //  EXAMPLE of MviViewModel //
data class MyState(
    val user: String = "",
)

sealed class MyActions {
    data class SetUser(val user: String) : MyActions()
}

sealed class MyEvents {
    data class ShowMessage(val message: String) : MyEvents()
}


class MyViewModel() : MviViewModel<MyState, MyActions, MyEvents>() {
    override val initialState: MyState
        get() = MyState()

    override fun onAction(action: MyActions) {
        when (action) {
            is MyActions.SetUser -> _state.update {
                it.copy(
                    user = action.user
                )
            }
        }
    }
}*/
