package com.kmpstarter.ui_utils.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.ui_utils.composition_locals.LocalThemeMode

@Composable
fun ColorFilter.Companion.lightDarkTint() = tint(
    color = when(LocalThemeMode.current ){
        ThemeMode.LIGHT -> Color.Black
        ThemeMode.DARK -> Color.White
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) Color.White else Color.Black
    }
)