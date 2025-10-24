/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.core.events.enums;

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.compositionLocalOf

enum class ThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System");

    fun toComposableBoolean(isSystemInDarkTheme: Boolean) =
        when (this) {
            LIGHT -> false
            DARK -> true
            SYSTEM -> isSystemInDarkTheme
        }
}

@OptIn(ExperimentalMaterial3Api::class)
val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }

