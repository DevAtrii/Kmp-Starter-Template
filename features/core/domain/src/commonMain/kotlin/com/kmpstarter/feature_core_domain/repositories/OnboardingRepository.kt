package com.kmpstarter.feature_core_domain.repositories

interface OnboardingRepository {

    suspend fun isOnboarded(): Boolean

    suspend fun setOnboarded(value: Boolean)


}