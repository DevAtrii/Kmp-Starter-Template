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

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthScreens {
    @Serializable
    data object Root : AuthScreens()




    @Serializable
    data object SignIn : AuthScreens()

    @Serializable
    data object SignUp : AuthScreens()
}
