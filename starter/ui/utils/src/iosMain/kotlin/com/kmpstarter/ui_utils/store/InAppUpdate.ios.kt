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

package com.kmpstarter.ui_utils.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain

@Composable
actual fun rememberUpdateLauncher(): UpdateLauncher {
    // dummy provider
    return retain {
        object : UpdateLauncher {
            override fun provide(
                onUpdated: () -> Unit,
                onUpdateFailure: () -> Unit,
            ): PlatformLauncher {
                return Unit
            }
        }
    }
}