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

package com.kmpstarterapp.core.navigation

import androidx.navigation3.runtime.NavKey
import com.kmpstarter.feature_navigation.StarterNavigator
import kotlinx.serialization.Serializable

@Serializable
sealed class AppScreens : NavKey {
    @Serializable
    data object Welcome : AppScreens()

    @Serializable
    data object Splash : AppScreens()

    @Serializable
    data object Onboarding : AppScreens()

    @Serializable
    data class Purchases(val onNavigate: (StarterNavigator) -> Unit = { it.navigateUp() }) :
        AppScreens()
}