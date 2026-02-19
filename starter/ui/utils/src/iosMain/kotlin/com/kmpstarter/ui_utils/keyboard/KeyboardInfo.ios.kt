package com.kmpstarter.ui_utils.keyboard

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

actual class KeyboardInfo {
    actual val height: Float
        get() = TODO("Not yet implemented")
    actual val isVisible: Boolean
        get() = TODO("Not yet implemented")
    actual val keyboardState: StateFlow<KeyboardState>
        get() = TODO("Not yet implemented")
}

@Composable
actual fun rememberKeyboardInfo(): KeyboardInfo {
    TODO("Not yet implemented")
}