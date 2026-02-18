@file:OptIn(KoinExperimentalAPI::class)

package com.kmpstarter.feature_navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kmpstarter.feature_navigation.utils.defaultPopTransitionSpec
import com.kmpstarter.feature_navigation.utils.defaultPredictivePopTransitionSpec
import com.kmpstarter.feature_navigation.utils.defaultTransitionSpec
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@Suppress("NonSkippableComposable")
@Composable
fun StarterNavigation(
    modifier: Modifier = Modifier,

    ) {
    val backStack: NavBackStack<NavKey> = rememberStarterBackStack()
    val navigator: StarterNavigator = StarterNavigator.getCurrent()
    val entryProvider = koinEntryProvider()


    LaunchedEffect(backStack) {
        navigator.provideBackStack(backStack)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        onBack = { navigator.navigateUp() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = defaultTransitionSpec(),
        popTransitionSpec = defaultPopTransitionSpec(),
        predictivePopTransitionSpec = defaultPredictivePopTransitionSpec(),
        modifier = modifier
    )

}



















