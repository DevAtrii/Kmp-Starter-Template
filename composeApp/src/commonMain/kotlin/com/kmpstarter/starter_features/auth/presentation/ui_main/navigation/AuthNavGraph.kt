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

package com.kmpstarter.starter_features.auth.presentation.ui_main.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.kmpstarter.core.ui.utils.navigation.appNavComposable
import com.kmpstarter.starter_features.auth.presentation.ui_main.screens.SignInScreen
import com.kmpstarter.starter_features.auth.presentation.ui_main.screens.SignUpScreen

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