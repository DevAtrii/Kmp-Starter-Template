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

package com.kmpstarter.starter_features.auth.domain.repository

import com.kmpstarter.core.utils.network_utils.RequestState
import com.kmpstarter.starter_features.auth.domain.enums.SignInMethod
import com.kmpstarter.starter_features.auth.domain.models.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Flow<RequestState<Boolean>>

    suspend fun signIn(
        email: String,
        password: String,
    ): Flow<RequestState<Boolean>>

    suspend fun createUserInDatabase(
        userData: UserData,
    )

    suspend fun checkIfUserAlreadyExistInDatabase(): Boolean

    suspend fun signOut(): Flow<RequestState<SignInMethod>>

    suspend fun forgetPassword(email: String): Flow<RequestState<Boolean>>

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    ): Flow<RequestState<Boolean>>

    suspend fun deleteAccount(password: String): Flow<RequestState<Boolean>>
}
