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

package com.kmpstarter.ui_utils.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.retain.retain
import com.kmpstarter.utils.starter.ExperimentalStarterApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSValue
import platform.UIKit.CGRectValue
import platform.UIKit.UIKeyboardFrameEndUserInfoKey
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIKeyboardWillShowNotification

@ExperimentalStarterApi
actual class KeyboardInfo {

    private val _keyboardState = MutableStateFlow(KeyboardState(false, 0f))
    actual val keyboardState: StateFlow<KeyboardState> get() = _keyboardState

    actual val height: Float get() = _keyboardState.value.height
    actual val isVisible: Boolean get() = _keyboardState.value.isVisible

    @OptIn(ExperimentalForeignApi::class)
    private val showObserver = NSNotificationCenter.defaultCenter.addObserverForName(
        name = UIKeyboardWillShowNotification,
        `object` = null,
        queue = null
    ) { notification ->
        val userInfo = notification?.userInfo
        val keyboardFrame = userInfo?.get(UIKeyboardFrameEndUserInfoKey) as? NSValue
        val height = keyboardFrame?.CGRectValue?.useContents { size.height } ?: 0.0

        _keyboardState.value = KeyboardState(true, height.toFloat())
    }

    private val hideObserver = NSNotificationCenter.defaultCenter.addObserverForName(
        name = UIKeyboardWillHideNotification,
        `object` = null,
        queue = null
    ) {
        _keyboardState.value = KeyboardState(false, 0f)
    }

    /**
     * Removes the observers from NSNotificationCenter to prevent leaks.
     */
    fun dispose() {
        NSNotificationCenter.defaultCenter.removeObserver(showObserver)
        NSNotificationCenter.defaultCenter.removeObserver(hideObserver)
    }
}

@ExperimentalStarterApi
@Composable
actual fun rememberKeyboardInfo(): KeyboardInfo {
    val keyboardInfo = retain { KeyboardInfo() }

    DisposableEffect(Unit) {
        onDispose {
            keyboardInfo.dispose()
        }
    }

    return keyboardInfo
}