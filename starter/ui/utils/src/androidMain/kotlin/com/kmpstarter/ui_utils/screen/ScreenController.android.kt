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

package com.kmpstarter.ui_utils.screen


import android.annotation.SuppressLint
import android.app.Activity
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext

actual class ScreenController(
    private val activity: Activity,
) {
    actual fun setBrightness(value: Float) {
        val params = activity.window.attributes
        params.screenBrightness = value.coerceIn(0f, 1f)
        activity.window.attributes = params
    }

    actual fun getBrightness(): Float {
        return activity.window.attributes.screenBrightness
    }

    actual fun resetBrightness() {
        val params = activity.window.attributes
        params.screenBrightness = BRIGHTNESS_OVERRIDE_NONE
        activity.window.attributes = params
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
actual fun rememberScreenController(): ScreenController {
    val activity = LocalActivity.current ?: LocalContext.current as Activity
    return retain {
        ScreenController(
            activity = activity,
        )
    }
}