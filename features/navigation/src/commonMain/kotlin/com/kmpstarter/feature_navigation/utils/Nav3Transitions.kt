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

package com.kmpstarter.feature_navigation.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

object Nav3Transitions {

    const val DEFAULT_DURATION = 300

    private fun enterFade(
        enabled: Boolean,
        duration: Int,
    ) = if (enabled) {
        fadeIn(animationSpec = tween(duration))
    } else {
        EnterTransition.None
    }

    private fun exitFade(
        enabled: Boolean,
        duration: Int,
    ) = if (enabled) {
        fadeOut(animationSpec = tween(duration))
    } else {
        ExitTransition.None
    }

    /**
     * iOS-like horizontal navigation with parallax effect.
     */
    fun horizontalSlideParallax(
        enableFade: Boolean = false,
        animationDuration: Int = DEFAULT_DURATION,
        parallaxFactor: Float = 0.15f,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = { it }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = {
                            -(it * parallaxFactor).toInt()
                        }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Back navigation for [horizontalSlideParallax].
     */
    fun horizontalSlideParallaxPop(
        enableFade: Boolean = false,
        animationDuration: Int = DEFAULT_DURATION,
        parallaxFactor: Float = 0.15f,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = {
                    -(it * parallaxFactor).toInt()
                }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = { it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Predictive back gesture with parallax.
     */
    fun predictiveHorizontalSlideParallax(
        enableFade: Boolean = false,
        animationDuration: Int = DEFAULT_DURATION,
        parallaxFactor: Float = 0.12f,
    ): AnimatedContentTransitionScope<Scene<Any>>.(
        @NavigationEvent.SwipeEdge Int,
    ) -> ContentTransform =
        { _ ->

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = {
                    -(it * parallaxFactor).toInt()
                }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = { it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Standard horizontal push transition.
     */
    fun horizontalSlide(
        enableFade: Boolean = true,
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = { it }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = { -it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Standard horizontal pop transition.
     */
    fun horizontalSlidePop(
        enableFade: Boolean = true,
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = { -it }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = { it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Predictive back gesture for standard horizontal navigation.
     */
    fun predictiveHorizontalSlide(
        enableFade: Boolean = true,
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.(
        @NavigationEvent.SwipeEdge Int,
    ) -> ContentTransform =
        { _ ->

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = { -it }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = { it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Bottom sheet / modal enter transition.
     */
    fun verticalSlideUp(
        enableFade: Boolean = true,
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInVertically(
                animationSpec = spec,
                initialOffsetY = { it }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutVertically(
                        animationSpec = spec,
                        targetOffsetY = { -it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Bottom sheet / modal dismiss transition.
     */
    fun verticalSlideDown(
        enableFade: Boolean = true,
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInVertically(
                animationSpec = spec,
                initialOffsetY = { -it }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutVertically(
                        animationSpec = spec,
                        targetOffsetY = { it }
                    ) + exitFade(enableFade, animationDuration))
        }

    /**
     * Fade only transition.
     */
    fun fade(
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            fadeIn(
                animationSpec = tween(animationDuration)
            ) togetherWith
                    fadeOut(
                        animationSpec = tween(animationDuration)
                    )
        }

    /**
     * Horizontal slide with custom offsets.
     */
    fun horizontalSlideCustom(
        enterOffsetMultiplier: Float = 1f,
        exitOffsetMultiplier: Float = -1f,
        enableFade: Boolean = true,
        animationDuration: Int = DEFAULT_DURATION,
    ): AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        {

            val spec = tween<IntOffset>(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )

            (slideInHorizontally(
                animationSpec = spec,
                initialOffsetX = {
                    (it * enterOffsetMultiplier).toInt()
                }
            ) + enterFade(enableFade, animationDuration)) togetherWith
                    (slideOutHorizontally(
                        animationSpec = spec,
                        targetOffsetX = {
                            (it * exitOffsetMultiplier).toInt()
                        }
                    ) + exitFade(enableFade, animationDuration))
        }
}