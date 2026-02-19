package com.kmpstarter.ui_utils.keyboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
fun isKeyboardVisible(): Boolean {
    val imeInsets = WindowInsets.ime
    val imeVisible = imeInsets.getBottom(LocalDensity.current) > 0
    return imeVisible
}
