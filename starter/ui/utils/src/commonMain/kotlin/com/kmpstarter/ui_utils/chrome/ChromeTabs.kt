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

package com.kmpstarter.ui_utils.chrome

import androidx.compose.runtime.Composable

expect class ChromeTabs {
    fun open(url: String): Boolean

    fun openElseDirect(url: String): Boolean
}


@Composable
expect fun rememberChromeTabs(): ChromeTabs