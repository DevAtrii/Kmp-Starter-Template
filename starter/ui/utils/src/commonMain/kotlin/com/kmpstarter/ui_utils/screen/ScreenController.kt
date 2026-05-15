package com.kmpstarter.ui_utils.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect class ScreenController {

    fun setBrightness(value: Float)
    fun getBrightness(): Float

    fun resetBrightness()

}



@Composable
expect fun rememberScreenController(): ScreenController