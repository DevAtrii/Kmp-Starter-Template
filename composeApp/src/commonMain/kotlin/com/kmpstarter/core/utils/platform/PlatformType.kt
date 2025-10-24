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

package com.kmpstarter.core.utils.platform

enum class PlatformType {
    IOS,
    ANDROID
}


expect val platformType: PlatformType


val PlatformType.isIos: Boolean
    get() = this == PlatformType.IOS
val PlatformType.isAndroid: Boolean
    get() = this == PlatformType.ANDROID

inline fun PlatformType.ifAndroid(crossinline action: () -> Unit) {
    if (this.isAndroid) action()
}

inline fun PlatformType.ifIos(crossinline action: () -> Unit) {
    if (this.isIos) action()
}