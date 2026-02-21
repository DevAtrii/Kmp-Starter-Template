package com.kmpstarter.ui_utils.keyboard

import android.graphics.Rect
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class KeyboardInfo(private val view: View) {

    private val _keyboardState = MutableStateFlow(KeyboardState(false, 0f))
    actual val keyboardState: StateFlow<KeyboardState> get() = _keyboardState

    actual val height: Float get() = _keyboardState.value.height
    actual val isVisible: Boolean get() = _keyboardState.value.isVisible

    init {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keyboardHeightPx = screenHeight - rect.bottom
            val keyboardHeightDp = keyboardHeightPx / view.resources.displayMetrics.density
            val visible = keyboardHeightPx > screenHeight * 0.15

            if (_keyboardState.value.height != keyboardHeightDp || _keyboardState.value.isVisible != visible) {
                _keyboardState.value = KeyboardState(visible, keyboardHeightDp)
            }
        }
    }
}

@Composable
actual fun rememberKeyboardInfo(): KeyboardInfo {
    val view = LocalView.current
    val keyboardInfo = remember(view) { KeyboardInfo(view) }
    return remember  { keyboardInfo }
}
