/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

    data class Android(
        override val osVersion: Int,
        override val debug: Boolean,
        override val appInfo: AppInfo,
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

inline fun Platform.ifAndroid(crossinline action: () -> Unit) {
    if (this.isAndroid) action()
}

inline fun Platform.ifIos(crossinline action: () -> Unit) {
    if (this.isIos) action()
}