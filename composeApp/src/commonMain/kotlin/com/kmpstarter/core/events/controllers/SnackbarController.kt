package com.kmpstarter.core.events.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SnackbarEvent(
    val message: String,
    val dismissPrevious: Boolean = true,
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

    suspend fun sendMessage(message: String?, dismissPrevious: Boolean = true) {
        message ?: return
        _events.send(
            SnackbarEvent(
                message = message,
                dismissPrevious = dismissPrevious
            )
        )
    }

    suspend fun dismiss(
    ) {

        sendEvent(
            event = SnackbarEvent(
                message = "",
                dismissPrevious = true
            )
        )

    }

    suspend fun sendAlert(
        message: String?,
        dismissPrevious: Boolean = true,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    ) {
        message ?: return
        scope.launch {
            sendEvent(
                event = SnackbarEvent(
                    message = message,
                    dismissPrevious = dismissPrevious
                )
            )
        }
    }

    suspend fun sendAlert(
        message: String?,
        dismissPrevious: Boolean = true,
        actionName: String? = null,
        action: suspend () -> Unit = {},
    ) = withContext(Dispatchers.IO) {
        message ?: return@withContext
        sendEvent(
            event = SnackbarEvent(
                message = message,
                dismissPrevious = dismissPrevious,
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



















