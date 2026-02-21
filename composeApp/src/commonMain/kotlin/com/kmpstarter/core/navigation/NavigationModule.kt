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

package com.kmpstarter.core.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import com.kmpstarter.core.ui.screens.WelcomeScreen
import com.kmpstarter.feature_core_presentation.screens.OnboardingV1Screen
import com.kmpstarter.feature_core_presentation.screens.SplashScreen
import com.kmpstarter.feature_navigation.StarterNavigator
import com.kmpstarter.feature_navigation.di.navigationCoreModule
import com.kmpstarter.feature_navigation.screens.StarterScreens
import com.kmpstarter.feature_purchases.presentation.ui_main.screens.SamplePurchaseScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    includes(navigationCoreModule)


    navigation<StarterScreens.Welcome> { route ->
        val navigator = StarterNavigator.getCurrent()
        WelcomeScreen(
            onGetStartedClick = {
                navigator.navigateTo(
                    route = StarterScreens.Purchases
                )
            }
        )
    }

    navigation<StarterScreens.Splash> { route ->
        val navigator = StarterNavigator.getCurrent()
        SplashScreen(
            onNavigate = {
                navigator.popAndNavigate(
                    route = StarterScreens.Welcome
                )
            },
            onNavigateToOnboarding = {
                navigator.popAndNavigate(
                    route = StarterScreens.Onboarding
                )
            }
        )
    }
    navigation<StarterScreens.Onboarding> { route ->
        val navigator = StarterNavigator.getCurrent()
        OnboardingV1Screen(
            onNavigate = {
                navigator.popAndNavigate(
                    route = StarterScreens.Welcome
                )
            }
        )
    }

    navigation<StarterScreens.Purchases> { route ->
        val navigator = StarterNavigator.getCurrent()
        SamplePurchaseScreen(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeContent),
            onDismiss = {
                navigator.navigateUp()
            }
        )
    }

}



















