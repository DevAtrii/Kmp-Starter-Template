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

package com.kmpstarter.core.firebase.auth

import com.kmpstarter.core.AppConstants
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth


object AuthUtils {
    val isLoggedIn: Boolean by lazy {
        Firebase.auth.currentUser != null
    }

    val firebaseUserId by lazy {
        Firebase.auth.currentUser!!.uid
    }

    val firebaseUserIdOrNull by lazy {
        Firebase.auth.currentUser?.uid
    }

    fun initGoogleAuthProvider(){
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = AppConstants.GOOGLE_WEB_CLIENT_ID))
    }
}