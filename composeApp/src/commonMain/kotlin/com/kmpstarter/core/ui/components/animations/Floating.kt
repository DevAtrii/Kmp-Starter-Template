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

package com.kmpstarter.core.ui.components.animations

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos

/**
 * Floating animation component that creates a smooth 2D floating effect.
 * Content moves in a gentle figure-8 pattern with oscillating motion.
 *
 * @param modifier Modifier for styling
 * @param enabled Whether the animation is enabled
 * @param durationMillis Duration of one complete floating cycle
 * @param amplitudeX Horizontal floating amplitude in dp
 * @param amplitudeY Vertical floating amplitude in dp
 * @param rotationAmplitude Maximum rotation angle in degrees
 * @param scaleAmplitude Scale variation amplitude (0.0 to 1.0)
 * @param content Content to be animated
 */
@Composable
fun Floating(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    durationMillis: Int = 5000,
    amplitudeX: Float = 6f,
    amplitudeY: Float = 12f,
    rotationAmplitude: Float = 1.5f,
    scaleAmplitude: Float = 0.03f,
    content: @Composable () -> Unit,
) {
    if (!enabled) {
        Box(modifier = modifier) {
            content()
        }
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating_animation")
    
    // Primary animation for X movement (sine wave)
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_progress"
    )
    
    // Secondary animation for Y movement (cosine wave, different phase)
    val secondaryProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween((durationMillis * 1.3f).toInt()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_secondary"
    )
    
    // Tertiary animation for rotation and scale
    val tertiaryProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween((durationMillis * 0.8f).toInt()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_tertiary"
    )
    
    // Calculate floating transformations
    val angleRadians = animationProgress * 2 * PI.toFloat()
    val secondaryAngle = secondaryProgress * 2 * PI.toFloat()
    val tertiaryAngle = tertiaryProgress * 2 * PI.toFloat()
    
    // Create figure-8 like movement pattern
    val translationX = amplitudeX * sin(angleRadians) * cos(secondaryAngle * 0.5f)
    val translationY = amplitudeY * cos(angleRadians * 0.7f) * sin(secondaryAngle * 0.8f)
    
    // Gentle rotation
    val rotation = rotationAmplitude * sin(tertiaryAngle * 0.6f)
    
    // Subtle scale breathing effect
    val scale = 1f + (scaleAmplitude * sin(tertiaryAngle * 0.4f))
    
    Box(
        modifier = modifier
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY
                this.rotationZ = rotation
                this.scaleX = scale
                this.scaleY = scale
            }
    ) {
        content()
    }
}