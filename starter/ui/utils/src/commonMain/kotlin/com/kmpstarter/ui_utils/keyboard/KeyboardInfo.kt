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
import kotlinx.coroutines.flow.StateFlow
@Composable
expect fun rememberKeyboardInfo(): KeyboardInfo

expect class KeyboardInfo {

    /** Current keyboard height in dp */
    val height: Float

    /** Whether keyboard is visible */
    val isVisible: Boolean

    /** Observe keyboard changes as StateFlow */
    val keyboardState: StateFlow<KeyboardState>
}

/** Optional: encapsulate keyboard info as a data class */
data class KeyboardState(
    val isVisible: Boolean,
    val height: Float
)