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

package com.kmpstarter.core.datastore.theme

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.utils.datastore.AppDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

    val dynamicColor = dataStore.data.map {
        it[PREF_DYNAMIC_COLORS] ?: DEFAULT_DYNAMIC_COLOR_SCHEME
    }


    val themeMode = dataStore.data.map { it: Preferences ->
        ThemeMode.valueOf(it[PREF_THEME] ?: DEFAULT_THEME_MODE.name)
    }


    suspend fun setDynamicColor(value: Boolean) {
        dataStore.edit {
            it[PREF_DYNAMIC_COLORS] = value
        }
    }

    suspend fun setOppositeDynamicColor() {
        dataStore.edit {
            it[PREF_DYNAMIC_COLORS] = !dynamicColor.first()
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit {
            it[PREF_THEME] = themeMode.name
        }
    }

    suspend fun setOppositeTheme() {
        dataStore.edit {
            it[PREF_THEME] =
                if (themeMode.first() == ThemeMode.LIGHT) ThemeMode.DARK.name else ThemeMode.LIGHT.name
        }
    }
}



















