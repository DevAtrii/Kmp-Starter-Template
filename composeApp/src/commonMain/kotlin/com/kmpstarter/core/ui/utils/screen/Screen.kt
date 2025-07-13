package com.kmpstarter.core.ui.utils.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

data class ScreenSize(
    val width: Dp,
    val height: Dp,
)

@Composable
expect fun getScreenSize(): ScreenSize


val ScreenSizeValue: ScreenSize
    @Composable
    get() = getScreenSize()