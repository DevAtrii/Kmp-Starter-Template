package com.kmpstarter.features.auth.presentation.ui_main.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.kmpstarter.features.auth.presentation.ui_main.screens.SignInScreen
import com.kmpstarter.features.auth.presentation.ui_main.screens.SignUpScreen
import com.kmpstarter.core.ui.utils.navigation.appNavComposable

fun NavGraphBuilder.authNavGraph(
    scaffoldModifier: Modifier,
) {

    navigation<AuthScreens.Root>(
        startDestination = AuthScreens.SignIn,
    ) {
        appNavComposable<AuthScreens.SignIn> {
            SignInScreen(
                modifier = scaffoldModifier,
            )
        }

        appNavComposable<AuthScreens.SignUp> {
            SignUpScreen(
                modifier = scaffoldModifier,
            )
        }
    }
}