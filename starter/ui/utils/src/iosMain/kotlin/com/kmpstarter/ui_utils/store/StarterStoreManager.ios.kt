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
import com.kmpstarter.utils.logging.Log
import platform.StoreKit.SKStoreReviewController

actual class StarterStoreManager {
    @Throws(exceptionClasses = [Exception::class])
    actual suspend fun askForReview() {
        SKStoreReviewController.requestReview()
    }

    actual suspend fun checkAppUpdate(
        launcher: UpdateLauncher,
        force: Boolean,
        onUpdateUnAvailable: () -> Unit,
        onUpdateAvailable: () -> Unit,
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    ) {
        onUpdateUnAvailable()
        Log.d(tag = null, "checkAppUpdate: ios doesn't have a native inAppUpdate api [SKIP]")
    }


}

@Composable
actual fun rememberStarterStoreManager(): StarterStoreManager {
    return retain {
        StarterStoreManager()
    }
}

