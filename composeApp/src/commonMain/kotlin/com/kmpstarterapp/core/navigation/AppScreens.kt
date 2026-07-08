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
    data object Purchases : AppScreens()
}