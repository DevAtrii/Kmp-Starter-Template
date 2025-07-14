package com.kmpstarter.core.firebase.auth

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
}