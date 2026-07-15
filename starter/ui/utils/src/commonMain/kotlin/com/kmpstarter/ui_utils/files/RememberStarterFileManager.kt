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

package com.kmpstarter.ui_utils.files

import androidx.compose.runtime.Composable
import com.kmpstarter.utils.files.StarterFileManager
import com.kmpstarter.utils.starter.ExperimentalStarterApi

/**
 * Returns a [StarterFileManager] bound to the current Compose host.
 *
 * On Android, this supplies the current [androidx.activity.ComponentActivity], which is
 * required for [StarterFileManager.saveFileIn]. Do not use the Koin singleton for
 * `saveFileIn`; inject or call other methods from Koin when activity is not needed.
 */
@OptIn(ExperimentalStarterApi::class)
@Composable
expect fun rememberStarterFileManager(): StarterFileManager
