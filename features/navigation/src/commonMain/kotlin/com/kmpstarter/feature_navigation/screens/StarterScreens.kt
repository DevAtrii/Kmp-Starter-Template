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

package com.kmpstarter.feature_navigation.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


// define your screens inside screens package

@Serializable
sealed class StarterScreens : NavKey {
    @Serializable
    data object Welcome : StarterScreens()

    @Serializable
    data object Splash : StarterScreens()

    @Serializable
    data object Onboarding : StarterScreens()

    @Serializable
    data object Purchases : StarterScreens()
}