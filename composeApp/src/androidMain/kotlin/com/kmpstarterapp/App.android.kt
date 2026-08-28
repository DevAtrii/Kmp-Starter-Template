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

package com.kmpstarterapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmpstarter.utils.StarterAndroidProvider
import com.kmpstarter.utils.logging.Log
import com.kmpstarter.utils.requireActivity

@Composable
actual fun SomeDebugPlatformWork(modifier: Modifier) {
    val activity = runCatching { StarterAndroidProvider.requireActivity() }
    Log.d("TAG","SomeDebugPlatformWork: ${activity.getOrNull()}")
}