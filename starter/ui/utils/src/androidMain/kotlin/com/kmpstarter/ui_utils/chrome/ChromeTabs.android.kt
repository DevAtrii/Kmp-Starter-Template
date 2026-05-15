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

package com.kmpstarter.ui_utils.chrome

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.kmpstarter.core.platform.printStackTracesIfDebug
import com.kmpstarter.utils.intents.IntentUtils
import org.koin.compose.koinInject

actual class ChromeTabs(
    private val activity: Activity,
    private val intentUtils: IntentUtils,
) {
    actual fun open(url: String): Boolean {
        return try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .build()

            customTabsIntent.launchUrl(activity, url.toUri())
            true
        } catch (e: Exception) {
            e.printStackTracesIfDebug()
            false
        }
    }

    actual fun openElseDirect(url: String): Boolean {
        if (open(url)) {
            return true
        }

        intentUtils.openUrl(url)
        return true
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
actual fun rememberChromeTabs(): ChromeTabs {
    val activity = LocalActivity.current ?: LocalContext.current as Activity
    val intentUtils: IntentUtils = koinInject()
    return retain {
        ChromeTabs(
            activity = activity,
            intentUtils = intentUtils,
        )
    }
}