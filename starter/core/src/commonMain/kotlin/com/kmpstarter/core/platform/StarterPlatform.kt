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

package com.kmpstarter.core.platform

import com.kmpstarter.utils.starter.ExperimentalStarterApi

/**
 * Platform-specific application context.
 *
 * On Android, this maps to [android.content.Context].
 * It represents application-level context and must not be cast to [android.app.Activity].
 *
 * On iOS, this represents the platform-specific context required by the implementation.
 */
expect abstract class AndroidContext

/**
 * Platform-specific Activity.
 *
 * On Android, this maps to [android.app.Activity].
 *
 * This type should only be used when an Activity instance is required.
 * Do not obtain it by casting [AndroidContext], since application context
 * is not an Activity.
 */
@ExperimentalStarterApi
expect class AndroidActivity

data class IosVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
)

data class AppInfo(
    val version: Int,
    val versionName: String,
    val appName: String,
)

sealed class Platform(
    open val osVersion: Int,
    open val debug: Boolean,
    open val appInfo: AppInfo,
    val isDynamicColorSupported: Boolean,
) {

    data class Ios(
        override val osVersion: Int,
        override val debug: Boolean,
        override val appInfo: AppInfo,
        val exactVersion: IosVersion,
    ) : Platform(
        osVersion = osVersion,
        debug = debug,
        appInfo = appInfo,
        isDynamicColorSupported = false
    )

    data class Android @OptIn(ExperimentalStarterApi::class) constructor(
        override val osVersion: Int,
        override val debug: Boolean,
        override val appInfo: AppInfo,
        val activity: AndroidActivity,
    ) : Platform(
        osVersion = osVersion,
        debug = debug,
        appInfo = appInfo,
        isDynamicColorSupported = osVersion >= 31 // android 12
    )
}


expect val platform: Platform

val Platform.isIos: Boolean
    get() = this is Platform.Ios
val Platform.isAndroid: Boolean
    get() = this is Platform.Android

inline fun Platform.ifAndroid(crossinline action: (Platform.Android) -> Unit) {
    if (this.isAndroid) action(this as Platform.Android)
}

inline fun Platform.ifIos(crossinline action: (Platform.Ios) -> Unit) {
    if (this.isIos) action(this as Platform.Ios)
}