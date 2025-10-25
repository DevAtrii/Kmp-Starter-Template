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

package com.kmpstarter.ui_components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TimerCloseButton(
    modifier: Modifier = Modifier,
    timeMillis: Long = 5000L,
    icon: ImageVector = Icons.Default.Close,
    progressTint: Color = MaterialTheme.colorScheme.outline.copy(
        alpha = 0.3f
    ),
    iconTint: Color = progressTint,
    onClick: () -> Unit,
) {

    var currentTime by rememberSaveable {
        mutableLongStateOf(timeMillis)
    }
    val progress by animateFloatAsState(
        targetValue = currentTime.toFloat() / timeMillis.toFloat(),
        animationSpec = TweenSpec(durationMillis = 1000, easing = LinearEasing)
    )


    LaunchedEffect(Unit) {
        while (currentTime > -1000) {
            currentTime -= 1000
            delay(1000L)
        }
    }




    AnimatedContent(
        modifier = modifier,
        targetState = currentTime > -1000,
        transitionSpec = {
            scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
        },
        label = "timer_close_button_animation"
    ) { isShowingProgress ->
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isShowingProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    progress = {
                        progress
                    },
                    color = progressTint
                )
            } else {
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Close",
                        tint = iconTint
                    )
                }
            }
        }
    }

}

