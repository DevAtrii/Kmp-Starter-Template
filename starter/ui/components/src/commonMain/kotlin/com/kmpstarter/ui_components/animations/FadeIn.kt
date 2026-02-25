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

package com.kmpstarter.ui_components.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay


object FadeInTokens {
    const val DELAY_1 = 100L
    const val DELAY_2 = 150L
    const val DELAY_3 = 200L
    const val DELAY_4 = 250L
    const val DELAY_5 = 300L
    const val DELAY_6 = 350L
    const val DELAY_7 = 400L
    const val DELAY_8 = 450L
}

/**
 * A modifier that applies a fade-in animation with optional delay.
 *
 * The composable will be hidden for [delayMillis] duration, then fade in over [duration] milliseconds.
 *
 * @param delayMillis Delay before starting the fade-in animation (in milliseconds)
 * @param duration Duration of the fade-in animation (in milliseconds)
 * @return A composable that wraps content with animated visibility
 */
@Composable
fun FadeIn(
    modifier: Modifier = Modifier,
    delayMillis: Long = 0L,
    duration: Int = 1000,
    content: @Composable () -> Unit,
) {

    var isVisible by rememberSaveable { mutableStateOf(delayMillis == 0L) }

    // Handle delay before showing the content
    LaunchedEffect(Unit) {
        if (delayMillis > 0L) {
            delay(delayMillis)
            isVisible = true
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = duration
            )
        )
    ) {
        content()
    }

}

