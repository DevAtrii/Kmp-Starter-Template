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

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import org.koin.mp.KoinPlatform


actual val platform: Platform
    get() {
        val context = KoinPlatform.getKoin().get<Context>()
        val flags = context.applicationContext.applicationInfo.flags
        val isDebug = (flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return Platform.Android(
            osVersion = Build.VERSION.SDK_INT,
            debug = isDebug,
            appInfo = getAppInfo(context = context)
        )
    }


private fun getAppInfo(context: Context): AppInfo {
    val applicationContext = context.applicationContext
    val packageManager = applicationContext.packageManager
    val packageInfo = packageManager.getPackageInfo(applicationContext.packageName, 0)

    val versionCode = if (Build.VERSION.SDK_INT >= 28) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode
    }

    val appName = applicationContext.applicationInfo.loadLabel(packageManager).toString()

    return AppInfo(
        version = versionCode,
        versionName = packageInfo.versionName ?: "",
        appName = appName
    )
}


