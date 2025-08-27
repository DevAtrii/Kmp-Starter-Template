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

package com.kmpstarter.core.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class StarterScreens {
    @Serializable
    data object Root : StarterScreens()

    @Serializable
    data object WelcomeScreen : StarterScreens()
}