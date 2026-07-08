/*
 *
 *  *
 *  *  * Copyright (c) 2026
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

package com.kmpstarter.feature_core_data.repositories

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kmpstarter.feature_core_domain.repositories.OnboardingRepository
import com.kmpstarter.utils.datastore.AppDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OnboardingRepositoryImpl(
    appDataStore: AppDataStore,
) : OnboardingRepository {

    private val dataStore = appDataStore.dataStore

    companion object {
        private val isOnboardedKey = booleanPreferencesKey("is_onboarded")
    }

    override suspend fun isOnboarded(): Boolean {
        return dataStore.data.map {
            it[isOnboardedKey] ?: false
        }.first()
    }

    override suspend fun setOnboarded(value: Boolean) {
        dataStore.edit {
            it[isOnboardedKey] = value
        }
    }
}