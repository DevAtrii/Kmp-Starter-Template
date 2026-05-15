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

@file:OptIn(KoinExperimentalAPI::class)

package com.kmpstarter.feature_navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.kmpstarter.feature_navigation.utils.defaultPopTransitionSpec
import com.kmpstarter.feature_navigation.utils.defaultPredictivePopTransitionSpec
import com.kmpstarter.feature_navigation.utils.defaultTransitionSpec
import com.kmpstarter.feature_navigation.utils.rememberNavBackStack
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@Suppress("NonSkippableComposable")
@Composable
fun StarterNavigation(
    initialScreens: List<NavKey>,
    builderAction: PolymorphicModuleBuilder<NavKey>.() -> Unit,
    entryDecorators: List<NavEntryDecorator<Any>> = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    ),
    transitionSpec: AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        defaultTransitionSpec(),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        defaultPopTransitionSpec(),
    predictivePopTransitionSpec:
    AnimatedContentTransitionScope<Scene<Any>>.(
        @NavigationEvent.SwipeEdge Int,
    ) -> ContentTransform =
        defaultPredictivePopTransitionSpec(),
    modifier: Modifier = Modifier,
) = StarterNavigation(
    initialScreens = initialScreens.toTypedArray(),
    builderAction = builderAction,
    entryDecorators = entryDecorators,
    transitionSpec = transitionSpec,
    popTransitionSpec = popTransitionSpec,
    predictivePopTransitionSpec = predictivePopTransitionSpec,
    modifier = modifier
)

@Suppress("NonSkippableComposable")
@Composable
fun StarterNavigation(
    vararg initialScreens: NavKey,
    builderAction: PolymorphicModuleBuilder<NavKey>.() -> Unit,
    entryDecorators: List<NavEntryDecorator<Any>> = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    ),
    transitionSpec: AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        defaultTransitionSpec(),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform =
        defaultPopTransitionSpec(),
    predictivePopTransitionSpec:
    AnimatedContentTransitionScope<Scene<Any>>.(
        @NavigationEvent.SwipeEdge Int,
    ) -> ContentTransform =
        defaultPredictivePopTransitionSpec(),
    modifier: Modifier = Modifier,
) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(
        elements = initialScreens,
        builderAction = builderAction
    )
    val navigator: StarterNavigator = StarterNavigator.getCurrent()
    val entryProvider = koinEntryProvider()


    LaunchedEffect(backStack) {
        navigator.provideBackStack(backStack)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        onBack = { navigator.navigateUp() },
        entryDecorators = entryDecorators,
        transitionSpec = transitionSpec,
        popTransitionSpec = popTransitionSpec,
        predictivePopTransitionSpec = predictivePopTransitionSpec,
        modifier = modifier
    )

}



















