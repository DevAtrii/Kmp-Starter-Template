package com.kmpstarter.core.events

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.utils.datastore.AppDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OnBoardingEvents(
    appDataStore: AppDataStore,
) {
    companion object {
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