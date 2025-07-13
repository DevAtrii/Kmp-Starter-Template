package com.kmpstarter.core.ui.utils.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
actual fun getScreenSize() = ScreenSize(
    width = LocalConfiguration.current.screenWidthDp.dp,
    height = LocalConfiguration.current.screenHeightDp.dp
)
