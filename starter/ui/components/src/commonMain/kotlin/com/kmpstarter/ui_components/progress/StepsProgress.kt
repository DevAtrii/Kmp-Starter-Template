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

package com.kmpstarter.ui_components.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kmpstarter.ui_utils.theme.Dimens


/**
 * OnBoarding progress indicator with animated progress lines between circular step indicators.
 *
 * @param modifier Modifier for styling
 * @param steps Total number of steps
 * @param currentStep Current active step (1-based)
 * @param completedStepIcon Icon to show for completed steps
 * @param activeStepColor Color for completed and current step circles
 * @param activeProgressColor Color for completed progress lines
 * @param inActiveStepColor Color for incomplete step circles
 * @param inActiveProgressColor Color for incomplete progress lines
 */
@Composable
  fun StepsProgress(
    modifier: Modifier = Modifier,
    steps: Int,
    currentStep: Int,
    completedStepIcon: ImageVector = Icons.Default.CheckCircle,
    activeStepColor: Color = MaterialTheme.colorScheme.primary,
    activeProgressColor: Color = MaterialTheme.colorScheme.primary,
    inActiveStepColor: Color = MaterialTheme.colorScheme.outlineVariant,
    inActiveProgressColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    require(steps > 0) { "Steps must be greater than 0" }
    require(currentStep in 1..steps) { "Current step must be between 1 and $steps" }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(steps) { index ->
            val stepNumber = index + 1
            val isCompleted = stepNumber <= currentStep
            val isActive = stepNumber == currentStep

            // Step circle
            StepCircle(
                isCompleted = isCompleted,
                isActive = isActive,
                activeColor = activeStepColor,
                inactiveColor = inActiveStepColor,
                completedIcon = if (stepNumber < currentStep) completedStepIcon else null
            )

            // Progress line (except for the last step)
            if (index < steps - 1) {
                val progressValue = when {
                    stepNumber < currentStep -> 1f // Completed
                    stepNumber == currentStep -> 0f // Current step, no progress yet
                    else -> 0f // Future steps
                }

                AnimatedProgressLine(
                    modifier = Modifier.weight(1f),
                    progress = progressValue,
                    activeColor = activeProgressColor,
                    inactiveColor = inActiveProgressColor
                )
            }
        }
    }
}

/**
 * Individual step circle indicator
 */
@Composable
private fun StepCircle(
    isCompleted: Boolean,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    completedIcon: ImageVector? = null,
) {
    val circleColor = if (isCompleted || isActive) activeColor else inactiveColor

    Box(
        modifier = Modifier.size(Dimens.iconSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(Dimens.iconSize)
        ) {
            drawCircle(
                color = circleColor,
                radius = size.minDimension / 2f
            )
        }

        // Show completed icon for completed steps (not current step)
        AnimatedVisibility(
            visible = completedIcon != null && isCompleted && !isActive,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            completedIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Animated progress line between steps using Material 3 LinearProgressIndicator
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedProgressLine(
    modifier: Modifier = Modifier,
    progress: Float,
    activeColor: Color,
    inactiveColor: Color,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 500
        ),
        label = "ProgressLineAnimation"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp),
        color = activeColor,
        gapSize = (-15).dp,
        drawStopIndicator = {},
        trackColor = inactiveColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}

