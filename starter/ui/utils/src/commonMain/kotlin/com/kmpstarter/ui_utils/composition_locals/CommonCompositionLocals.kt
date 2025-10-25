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

package com.kmpstarter.ui_utils.composition_locals

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.store.KmpInAppReview

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("Please provide LocalNavController")
}

@OptIn(ExperimentalMaterial3Api::class)
val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }

val LocalInAppReview = compositionLocalOf<KmpInAppReview> { error("Provide KMP In App Review") }
