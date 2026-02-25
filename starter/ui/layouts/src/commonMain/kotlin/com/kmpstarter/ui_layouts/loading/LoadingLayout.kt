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

package com.kmpstarter.ui_layouts.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.ui_utils.composition_locals.LocalThemeMode

@Composable
fun LoadingLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = when (LocalThemeMode.current) {
        ThemeMode.DARK -> Color.Black
        ThemeMode.LIGHT -> Color.White
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) Color.Black else Color.White
    }.copy(alpha = 0.5f),
    sizeModifier: Modifier = Modifier.fillMaxSize(),
) {
    Box(
        modifier = modifier.then(sizeModifier).then(
            if (backgroundColor == null)
                Modifier
            else
                Modifier.background(backgroundColor)
        ).clickable {},
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

