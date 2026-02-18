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

package com.kmpstarter.feature_navigation.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

private const val ANIMATION_DURATION = 300

/**
 * Default transition spec for forward navigation (pushing a new screen).
 * Slides in from the right with fade in, slides out to the left with fade out.
 */
fun defaultTransitionSpec(): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        slideInHorizontally(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetX = { -it }
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Default transition spec for backward navigation (popping the current screen).
 * Slides in from the left with fade in, slides out to the right with fade out.
 */
fun defaultPopTransitionSpec(): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        slideInHorizontally(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetX = { -it }
        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetX = { it }
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Default transition spec for predictive pop (swipe gesture).
 * Similar to pop transition but optimized for swipe gestures.
 */
fun defaultPredictivePopTransitionSpec(): AnimatedContentTransitionScope<Scene<Any>>.(
    @NavigationEvent.SwipeEdge Int,
) -> ContentTransform =
    { swipeEdge ->
        slideInHorizontally(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetX = { -it }
        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetX = { it }
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Slide transition from bottom to top (for modal-like screens).
 */
fun slideUpTransitionSpec(): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        slideInVertically(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetY = { it }
        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                slideOutVertically(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetY = { -it }
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Slide transition from top to bottom (for dismissing modal-like screens).
 */
fun slideDownPopTransitionSpec(): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        slideInVertically(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetY = { -it }
        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                slideOutVertically(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetY = { it }
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Fade-only transition (subtle, no sliding).
 */
fun fadeTransitionSpec(): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Horizontal slide transition with custom offset multiplier.
 * @param enterOffsetMultiplier Multiplier for enter slide offset (default: 1.0 = full width)
 * @param exitOffsetMultiplier Multiplier for exit slide offset (default: -1.0 = full width to left)
 */
fun customSlideTransitionSpec(
    enterOffsetMultiplier: Float = 1f,
    exitOffsetMultiplier: Float = -1f,
): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        slideInHorizontally(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetX = { (it * enterOffsetMultiplier).toInt() }
        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetX = { (it * exitOffsetMultiplier).toInt() }
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }

/**
 * Custom transition spec with configurable duration.
 * @param durationMillis Duration of the transition animation
 */
fun customDurationTransitionSpec(
    durationMillis: Int = ANIMATION_DURATION,
): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
    {
        slideInHorizontally(
            animationSpec = tween(durationMillis),
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = tween(durationMillis)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(durationMillis),
                    targetOffsetX = { -it }
                ) + fadeOut(animationSpec = tween(durationMillis))
    }

