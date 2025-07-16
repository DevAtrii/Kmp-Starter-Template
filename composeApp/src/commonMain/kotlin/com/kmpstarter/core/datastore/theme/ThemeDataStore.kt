package com.kmpstarter.core.datastore.theme

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

class ThemeDataStore(
    appDataStore: AppDataStore,
) {
    companion object {
        private val PREF_THEME = stringPreferencesKey("theme_mode")
        private val PREF_DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")

        val DEFAULT_THEME_MODE = ThemeMode.LIGHT
        const val DEFAULT_DYNAMIC_COLOR_SCHEME = false
    }

    private val dataStore = appDataStore.dataStore
    private var editThemeJob: Job? = null
    private var editDynamicColorJob: Job? = null

    val dynamicColor = dataStore.data.map {
        it[PREF_DYNAMIC_COLORS] ?: DEFAULT_DYNAMIC_COLOR_SCHEME
    }


    val themeMode = dataStore.data.map { it: Preferences ->
        ThemeMode.valueOf(it[PREF_THEME] ?: DEFAULT_THEME_MODE.name)
    }


    fun setDynamicColor(value: Boolean) {
        editDynamicColorJob?.cancel()
        editDynamicColorJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_DYNAMIC_COLORS] = value
            }
        }
    }

    fun setOppositeDynamicColor() {
        editDynamicColorJob?.cancel()
        editDynamicColorJob = CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                it[PREF_DYNAMIC_COLORS] = !dynamicColor.first()
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
                it[PREF_THEME] =
                    if (themeMode.first() == ThemeMode.LIGHT) ThemeMode.DARK.name else ThemeMode.LIGHT.name
            }
        }
    }
}

@Composable
fun isAppInDarkTheme(
    themeDataStore: ThemeDataStore = koinInject(),
): Boolean {
    val currentThemeMode by themeDataStore.themeMode.collectAsState(
        initial = ThemeDataStore.DEFAULT_THEME_MODE
    )
    if (currentThemeMode == ThemeMode.SYSTEM && isSystemInDarkTheme())
        return true
    return currentThemeMode == ThemeMode.DARK
}



















