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

package com.kmpstarter.starter_features.auth.domain.models

import com.kmpstarter.core.utils.common.currentMillis
import com.kmpstarter.starter_features.auth.domain.enums.SignInMethod
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val name: String? = null,
    val email: String = "",
    val userId: String = "",
    val profilePhoto: String? = null,
    val method: SignInMethod = SignInMethod.EMAIL,
    val createdAt: Long = currentMillis,
)
