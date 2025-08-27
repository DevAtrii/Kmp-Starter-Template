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

package com.kmpstarter.starter_features.auth.data.repository

import com.kmpstarter.core.firebase.auth.AuthUtils.firebaseUserId
import com.kmpstarter.core.firebase.auth.AuthUtils.isLoggedIn
import com.kmpstarter.core.utils.logging.log
import com.kmpstarter.core.utils.network_utils.RequestState
import com.kmpstarter.starter_features.auth.domain.models.UserData
import com.kmpstarter.starter_features.auth.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl : AuthRepository {
    companion object {
        private const val ERR_GENERIC = "Something Went Wrong"
        private const val FIRESTORE_USERS = "users"
        private const val TAG = "AuthRepositoryImpl"
        private val firebaseAuth: FirebaseAuth = Firebase.auth
        private val firestore: FirebaseFirestore = Firebase.firestore
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ) = flow {
        emit(RequestState.Loading)
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val userId = result.user?.uid ?: return@flow
            val userData =
                UserData(
                    name = name.trim(),
                    email = email,
                    userId = userId,
                )
            createUserInDatabase(userData)
            emit(RequestState.Success(true))
        } catch (e: Exception) {
            emit(RequestState.Error(e.message ?: ERR_GENERIC))
        }
    }

    override suspend fun signIn(email: String, password: String) = flow {
        emit(RequestState.Loading)
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            emit(RequestState.Success(true))
        } catch (e: Exception) {
            emit(RequestState.Error(e.message ?: ERR_GENERIC))
        }
    }

    override suspend fun createUserInDatabase(
        userData: UserData,
    ) = firestore.collection(FIRESTORE_USERS).document(userData.userId)
        .set(userData)

    override suspend fun checkIfUserAlreadyExistInDatabase() = firestore.collection(FIRESTORE_USERS)
        .document(firebaseUserId)
        .get().data<UserData?> {} != null

    override suspend fun signOut() = flow {
        emit(RequestState.Loading)
        try {
            val signInMethod = getSignInMethod()
            firebaseAuth.signOut()
            emit(RequestState.Success(signInMethod))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RequestState.Error(e.message ?: ERR_GENERIC))
        }

    }

    override suspend fun forgetPassword(email: String) = flow {
        emit(RequestState.Loading)
        try {
            firebaseAuth.sendPasswordResetEmail(email = email)
            emit(RequestState.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RequestState.Error(e.message ?: ERR_GENERIC))
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) = flow {
        emit(RequestState.Loading)
        if (!isLoggedIn) {
            emit(RequestState.Error("User is not signed in"))
            return@flow
        }
        val user = firebaseAuth.currentUser!!
        val email = user.email
        log.i {
            "$TAG changePassword: $email"
        }
        if (email == null) {
            emit(RequestState.Error("User email not found"))
            return@flow
        }
        if (email.isEmpty()) {
            emit(RequestState.Error("Email address not found"))
            return@flow
        }
        try {
            val credential = EmailAuthProvider.credential(email, oldPassword)
            user.reauthenticate(credential)
            user.updatePassword(newPassword)
            emit(RequestState.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RequestState.Error(e.message ?: ERR_GENERIC))
        }
    }

    override suspend fun deleteAccount(password: String) = flow {
        emit(RequestState.Loading)
        val user = firebaseAuth.currentUser
        if (user == null) {
            emit(RequestState.Error("User is not signed in"))
            return@flow
        }

        // Get current user's email
        val email = user.email
        if (email.isNullOrEmpty()) {
            emit(RequestState.Error("User email not found"))
            return@flow
        }

        try {
            val credential = EmailAuthProvider.credential(email, password)
            user.reauthenticate(credential)
            firestore.collection(FIRESTORE_USERS).document(user.uid).delete()
            user.delete()
            emit(RequestState.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RequestState.Error(e.message ?: ERR_GENERIC))
        }
    }

    // my methods
    private suspend fun getSignInMethod() = firestore.collection(FIRESTORE_USERS)
        .document(firebaseUserId)
        .get().data<UserData>().method

}



















