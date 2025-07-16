package com.kmpstarter.core.datastore.common

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.kmpstarter.core.utils.common.currentMillis
import com.kmpstarter.core.utils.datastore.AppDataStore
import com.kmpstarter.core.utils.logging.Log
import kotlinx.coroutines.flow.map

class CommonDataStore(
    private val appDataStore: AppDataStore,
) {
    companion object {
          val PREF_INSTALL_MILLIS = longPreferencesKey("install_millis_")
    }

    val installMillis = appDataStore.dataStore.data.map { pref ->
        val millis = pref[PREF_INSTALL_MILLIS]
        if (millis == null)
            appDataStore.dataStore.edit { mutablePreferences ->
                mutablePreferences[PREF_INSTALL_MILLIS] = currentMillis()
            }
        val millisToReturn = millis ?: currentMillis()
        Log.d("installMillis","installMillis: $millis current ${currentMillis()} return: $millisToReturn")
        millisToReturn
    }

}