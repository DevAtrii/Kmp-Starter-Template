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

package com.kmpstarter.utils

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

/**
 * `androidx.startup` initializer that captures [Application] into [StarterAndroidProvider].
 *
 * Registered in `starter/utils` `AndroidManifest.xml` via `androidx.startup.InitializationProvider`.
 * Runs at process start — no call from `Application.onCreate` required.
 *
 * If you add your own [Initializer] that needs [Application] or the current [android.app.Activity],
 * depend on this class:
 *
 * ```kotlin
 * override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
 *     StarterApplicationProvider::class.java
 * )
 * ```
 *
 * Keep `InitializationProvider` in the app manifest. To drop a different initializer
 * (e.g. WorkManager), remove only that `<meta-data>` with `tools:node="remove"`.
 * See [StarterAndroidProvider.requireApplication].
 */
class StarterApplicationProvider : Initializer<Unit> {

    override fun create(context: Context) {
        StarterAndroidProvider.application = context.applicationContext as Application
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}