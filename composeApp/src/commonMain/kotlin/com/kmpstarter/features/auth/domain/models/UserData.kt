package com.kmpstarter.features.auth.domain.models

import com.kmpstarter.features.auth.domain.enums.SignInMethod
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val name: String? = null,
    val email: String = "",
    val userId: String = "",
    val profilePhoto: String? = null,
    val method: SignInMethod = SignInMethod.EMAIL,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
)
