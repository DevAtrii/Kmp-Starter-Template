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

package com.kmpstarter.ui_utils.files

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import com.kmpstarter.utils.files.StarterFileManager
import com.kmpstarter.utils.starter.ExperimentalStarterApi

@OptIn(ExperimentalStarterApi::class)
@Composable
actual fun rememberStarterFileManager(): StarterFileManager {
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity
    return retain {
        StarterFileManager(
            context = context,
            activity = activity,
        )
    }
}
