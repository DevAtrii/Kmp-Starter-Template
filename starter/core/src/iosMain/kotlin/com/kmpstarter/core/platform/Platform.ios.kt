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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
actual val platform: Platform
    get() {
        val version = NSProcessInfo.processInfo.operatingSystemVersion
        val isDebug = kotlin.native.Platform.isDebugBinary
        return version.useContents {
            Platform.Ios(
                osVersion = this.majorVersion.toInt(),
                exactVersion = IosVersion(
                    major = this.majorVersion.toInt(),
                    minor = this.minorVersion.toInt(),
                    patch = this.patchVersion.toInt()
                ),
                debug = isDebug,
                appInfo = getAppInfo()
            )
        }
    }


private fun getAppInfo(): AppInfo {

    val bundle = NSBundle.mainBundle
    val infoDict = bundle.infoDictionary!!

    val versionName = infoDict["CFBundleShortVersionString"] as? String ?: ""
    val versionString = infoDict["CFBundleVersion"] as? String ?: "0"
    val appName = infoDict["CFBundleDisplayName"] as? String
        ?: infoDict["CFBundleName"] as? String
        ?: ""

    return AppInfo(
        version = versionString.toIntOrNull() ?: 0,
        versionName = versionName,
        appName = appName
    )
}