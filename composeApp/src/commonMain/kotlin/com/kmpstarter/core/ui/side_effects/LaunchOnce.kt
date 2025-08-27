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

package com.kmpstarter.core.ui.side_effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Like [LaunchedEffect] but runs [block] only once,
 * surviving recompositions and configuration changes.
 */
@Composable
fun LaunchOnce(
    block: suspend () -> Unit,
) {
    var hasRun by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (!hasRun) {
            block()
            hasRun = true
        }
    }
}