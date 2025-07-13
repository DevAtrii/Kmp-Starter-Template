package com.kmpstarter.core.events

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class ThemeEvents(
    appDataStore: AppDataStore,
) {
    companion object {
        private val PREF_THEME = stringPreferencesKey("theme_mode")
        private val PREF_DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        private val PREF_ONBOARDED = booleanPreferencesKey("onboarded")
    }

    private val dataStore = appDataStore.dataStore
    private var editThemeJob: Job? = null
    private var editDynamicColorJob: Job? = null
    private var editOnboardedJob: Job? = null

    val dynamicColor = dataStore.data.map {
        it[PREF_DYNAMIC_COLORS] ?: false
    }


    val isOnboarded = dataStore.data.map {
        it[PREF_ONBOARDED] ?: false
    }


    val themeMode = dataStore.data.map { it: Preferences ->
        ThemeMode.valueOf(it[PREF_THEME] ?: ThemeMode.LIGHT.name)
    }


    fun setIsOnboarded(value: Boolean) {
        editOnboardedJob?.cancel()
        editOnboardedJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_ONBOARDED] = value
            }
        }
    }

    fun setDynamicColor(value: Boolean) {
        editDynamicColorJob?.cancel()
        editDynamicColorJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_DYNAMIC_COLORS] = value
            }
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        editThemeJob?.cancel()
        editThemeJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_THEME] = themeMode.name
            }
        }
    }
    fun setOppositeTheme() {
        editThemeJob?.cancel()
        editThemeJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_THEME] = if (themeMode.first() == ThemeMode.LIGHT) ThemeMode.DARK.name else ThemeMode.LIGHT.name
            }
        }
    }
}

@Composable
fun isAppInDarkTheme(
    themeEvents: ThemeEvents = koinInject(),
): Boolean {
    val currentThemeMode by themeEvents.themeMode.collectAsState(
        initial = ThemeMode.LIGHT
    )
    if (currentThemeMode == ThemeMode.SYSTEM && isSystemInDarkTheme())
        return true
    return currentThemeMode == ThemeMode.DARK
}



















