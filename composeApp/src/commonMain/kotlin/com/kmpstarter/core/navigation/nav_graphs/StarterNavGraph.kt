package com.kmpstarter.core.navigation.nav_graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.kmpstarter.core.navigation.screens.StarterScreens
import com.kmpstarter.core.ui.screens.WelcomeScreen
import com.kmpstarter.core.ui.utils.navigation.appNavComposable

fun NavGraphBuilder.starterNavGraph(
    scaffoldModifier: Modifier,
) {

    navigation<StarterScreens.Root>(
        startDestination = StarterScreens.WelcomeScreen,
    ) {
        appNavComposable<StarterScreens.WelcomeScreen> {
            WelcomeScreen(
                modifier = scaffoldModifier
            )
        }
    }
}




















