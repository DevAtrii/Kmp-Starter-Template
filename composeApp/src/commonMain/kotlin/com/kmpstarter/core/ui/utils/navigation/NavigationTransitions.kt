package com.kmpstarter.core.ui.utils.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

private const val ANIMATION_DURATION = 300

fun getDefaultEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    {
        fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                slideInHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    initialOffsetX = { it / 2 },
                )
    }

fun getDefaultExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    {
        fadeOut(animationSpec = tween(ANIMATION_DURATION)) +
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetX = { -it / 2 },
                )
    }

fun getDefaultPopEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    {
        fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                slideInHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    initialOffsetX = { -it / 2 },
                )
    }

fun getDefaultPopExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    {
        fadeOut(animationSpec = tween(ANIMATION_DURATION)) +
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION),
                    targetOffsetX = { it / 2 },
                )
    }

inline fun <reified T : Any> NavGraphBuilder.appNavComposable(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = getDefaultEnterTransition(),
    noinline exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = getDefaultExitTransition(),
    noinline popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        getDefaultPopEnterTransition(),
    noinline popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = getDefaultPopExitTransition(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content,
    )
}
