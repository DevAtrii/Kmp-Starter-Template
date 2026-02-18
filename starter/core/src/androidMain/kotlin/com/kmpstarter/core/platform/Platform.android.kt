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

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.os.Build
import com.kmpstarter.core.KmpStarter


actual val platform: Platform
    get() {
        val activity = (KmpStarter.PLATFORM_HOST as Activity)
        val flags = activity.applicationContext.applicationInfo.flags
        val isDebug = (flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return Platform.Android(
            osVersion = Build.VERSION.SDK_INT,
            debug = isDebug,
            appInfo = getAppInfo(activity = activity)
        )
    }


private fun getAppInfo(activity: Activity): AppInfo {
    val context = activity.applicationContext
    val packageManager = context.packageManager
    val packageInfo = packageManager.getPackageInfo(context.packageName, 0)

    val versionCode = if (Build.VERSION.SDK_INT >= 28) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode
    }

    val appName = context.applicationInfo.loadLabel(packageManager).toString()

    return AppInfo(
        version = versionCode,
        versionName = packageInfo.versionName ?: "",
        appName = appName
    )
}


