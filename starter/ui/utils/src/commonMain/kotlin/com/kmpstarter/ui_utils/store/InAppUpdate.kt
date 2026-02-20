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

@Suppress("ExperimentalAnnotationRetention")
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is experimental for the App Update flow and may change in future versions, bugs or crashes can occur please report on Github"
)
@Retention(AnnotationRetention.BINARY) // Corrected: Ensures enforcement across module boundaries
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR
)
annotation class ExperimentalAppUpdateApi


typealias PlatformLauncher = Any

interface UpdateLauncher {
    fun provide(
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    ): PlatformLauncher
}

@Composable
@ExperimentalAppUpdateApi
expect fun rememberUpdateLauncher(): UpdateLauncher


@Composable
@ExperimentalAppUpdateApi
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


















