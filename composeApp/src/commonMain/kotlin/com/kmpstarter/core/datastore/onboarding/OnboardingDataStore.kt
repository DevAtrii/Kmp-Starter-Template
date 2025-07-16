package com.kmpstarter.core.datastore.onboarding

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kmpstarter.core.utils.datastore.AppDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OnboardingDataStore(
    appDataStore: AppDataStore,
) {
    companion object Companion {
        private val PREF_ONBOARDED = booleanPreferencesKey("onboarded")
    }

    private val dataStore = appDataStore.dataStore
    private var editOnboardedJob: Job? = null


    val isOnboarded = dataStore.data.map {
        it[PREF_ONBOARDED] ?: false
    }


    fun setIsOnboarded(value: Boolean) {
        editOnboardedJob?.cancel()
        editOnboardedJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_ONBOARDED] = value
            }
        }
    }
}