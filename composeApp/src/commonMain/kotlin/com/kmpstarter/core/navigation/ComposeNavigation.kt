package com.kmpstarter.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kmpstarter.core.events.navigator.interfaces.Navigator
import com.kmpstarter.core.events.navigator.utils.handleNavigationAction
import com.kmpstarter.core.events.utils.ObserveAsEvents
import com.kmpstarter.core.navigation.nav_graphs.appNavGraph
import com.kmpstarter.core.navigation.screens.StarterScreens
import com.kmpstarter.core.ui.composition_locals.LocalNavController
import org.koin.compose.koinInject


@Composable
fun ComposeNavigation(
    scaffoldModifier: Modifier = Modifier,
    navigator: Navigator = koinInject(),
    navController: NavHostController = rememberNavController(),
) {
    NavigationSideEffects(navigator, navController)

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = StarterScreens.Root
        ) {

            appNavGraph(
                scaffoldModifier = scaffoldModifier
            )

        }
    }
}

@Composable
private fun NavigationSideEffects(
    navigator: Navigator,
    navController: NavHostController,
) {
    ObserveAsEvents(
        flow = navigator.navigationActions
    ) { action ->
        navController.handleNavigationAction(
            action = action
        )
    }
}