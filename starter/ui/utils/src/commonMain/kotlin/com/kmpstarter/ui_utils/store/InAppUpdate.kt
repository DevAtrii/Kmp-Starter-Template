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
import androidx.compose.runtime.LaunchedEffect
import com.kmpstarter.utils.logging.Log
import com.kmpstarter.utils.starter.ExperimentalStarterApi


typealias PlatformLauncher = Any

interface UpdateLauncher {
    fun provide(
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    ): PlatformLauncher
}

@Composable
@ExperimentalStarterApi
expect fun rememberUpdateLauncher(): UpdateLauncher


@Composable
@ExperimentalStarterApi
fun AppUpdateProvider(
    force: Boolean,
    content: @Composable () -> Unit,
) {
    val storeManager = rememberStarterStoreManager()
    val updateLauncher = rememberUpdateLauncher()


    LaunchedEffect(force) {
        storeManager.checkAppUpdate(
            launcher = updateLauncher,
            force = force,
            onUpdateUnAvailable = {
                Log.i(tag = null, "AppUpdateProvider: onUpdateUnAvailable")
            },
            onUpdateAvailable = {
                Log.i(tag = null, "AppUpdateProvider: onUpdateAvailable")
            },
            onUpdated = {
                Log.i(tag = null, "AppUpdateProvider: onUpdated")
            },
            onUpdateFailure = {
                Log.i(tag = null, "AppUpdateProvider: onUpdateFailure")
            },
        )
    }
    content()
}


















