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


expect class StarterStoreManager {
    /** on android it throws exception & for ios it skips*/
    @Throws(exceptionClasses = [Exception::class])
    suspend fun askForReview()


    suspend fun checkAppUpdate(
        launcher: UpdateLauncher,
        force: Boolean,
        onUpdateUnAvailable: () -> Unit,
        onUpdateAvailable: () -> Unit,
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    )

}



@Composable
expect fun rememberStarterStoreManager(): StarterStoreManager





















