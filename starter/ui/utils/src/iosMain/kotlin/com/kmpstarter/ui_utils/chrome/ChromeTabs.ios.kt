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
import androidx.compose.runtime.retain.retain
import com.kmpstarter.utils.intents.IntentUtils
import org.koin.compose.koinInject

actual class ChromeTabs(private val intentUtils: IntentUtils) {
    actual fun open(url: String): Boolean {
        throw NotImplementedError("Chrome Tabs are only supported on Android")
    }

    actual fun openElseDirect(url: String): Boolean {
        intentUtils.openUrl(url)
        return true
    }
}

@Composable
actual fun rememberChromeTabs(): ChromeTabs {
    val intentUtils: IntentUtils = koinInject()
    return retain {
        ChromeTabs(
            intentUtils = intentUtils,
        )
    }
}