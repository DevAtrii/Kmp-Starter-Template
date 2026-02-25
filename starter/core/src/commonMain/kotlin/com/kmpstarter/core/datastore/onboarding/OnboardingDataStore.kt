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

package com.kmpstarter.core.datastore.onboarding

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kmpstarter.utils.datastore.AppDataStore
import kotlinx.coroutines.flow.map

class OnboardingDataStore(
    appDataStore: AppDataStore,
) {
    companion object Companion {
        private val PREF_ONBOARDED = booleanPreferencesKey("onboarded")
    }

    private val dataStore = appDataStore.dataStore


    val isOnboarded = dataStore.data.map {
        it[PREF_ONBOARDED] ?: false
    }


    suspend fun setIsOnboarded(value: Boolean) {
        dataStore.edit {
            it[PREF_ONBOARDED] = value
        }
    }
}