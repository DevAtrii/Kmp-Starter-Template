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

package com.kmpstarter.starter_features.auth.presentation.events

import com.kmpstarter.starter_features.auth.presentation.ui_main.navigation.AuthScreens
import dev.gitlive.firebase.auth.FirebaseUser


sealed class AuthEvents {
    data object SignIn : AuthEvents()

    data object StartSignInWithGoogle : AuthEvents()

    data class OnSignedInWithGoogle(
        val firebaseUser: FirebaseUser?=null
    ) : AuthEvents()

    data object SignUp : AuthEvents()

    data object SignOut : AuthEvents()

    data object ForgetPassword : AuthEvents()

    data object ChangePassword : AuthEvents()

    data object ResetPasswordsInState : AuthEvents()

    data object DeleteAccount : AuthEvents()

    data class Navigate(
        val route: AuthScreens,
        val popUpTo: AuthScreens? = null,
        val inclusive: Boolean = false,
    ) : AuthEvents()

    data class NavigateOutsideFeature(
        val route: Any,
        val popUpTo: Any? = null,
        val inclusive: Boolean = false,
    ) : AuthEvents()
}
