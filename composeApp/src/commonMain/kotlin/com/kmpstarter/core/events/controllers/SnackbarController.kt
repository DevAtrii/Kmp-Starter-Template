package com.kmpstarter.core.events.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit,
)

object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }

    suspend fun sendMessage(message: String?) {
        message ?: return
        _events.send(
            SnackbarEvent(
                message = message
            )
        )
    }

    fun sendAlert(
        message: String?,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    ) {
        message ?: return
        scope.launch {
            sendEvent(
                event = SnackbarEvent(
                    message = message,
                )
            )
        }
    }

    suspend fun sendAlert(
        message: String?,
        actionName: String? = null,
        action: suspend () -> Unit = {},
    ) {
        message ?: return
        sendEvent(
            event = SnackbarEvent(
                message = message,
                action = actionName?.let {
                    SnackbarAction(
                        name = actionName,
                        action = action
                    )
                }
            )
        )
    }
}



















