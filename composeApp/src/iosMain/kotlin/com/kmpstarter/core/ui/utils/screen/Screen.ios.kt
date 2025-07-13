package com.kmpstarter.core.ui.utils.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.UIKit.UIScreen

@Composable
actual fun getScreenSize() = remember {
    val screen = UIScreen.mainScreen
    ScreenSize(
        width = CGRectGetWidth(screen.bounds).dp,
        height = CGRectGetHeight(screen.bounds).dp
    )
}
