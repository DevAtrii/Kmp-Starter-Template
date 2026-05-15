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


import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import platform.UIKit.UIScreen

actual class ScreenController {
    private var brightnessBeforeOverride: Double? = null

    actual fun setBrightness(value: Float) {
        if (brightnessBeforeOverride == null) {
            brightnessBeforeOverride = UIScreen.mainScreen.brightness
        }
        UIScreen.mainScreen.brightness =
            value.toDouble().coerceIn(0.0, 1.0)
    }

    actual fun getBrightness(): Float {
        return UIScreen.mainScreen.brightness.toFloat()
    }

    actual fun resetBrightness() {
        brightnessBeforeOverride?.let { previous ->
            UIScreen.mainScreen.brightness = previous
            brightnessBeforeOverride = null
        }
    }
}

@Composable
actual fun rememberScreenController(): ScreenController {
    return retain {
        ScreenController()
    }
}