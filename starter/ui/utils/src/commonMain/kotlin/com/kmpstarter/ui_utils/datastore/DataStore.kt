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

package com.kmpstarter.ui_utils.datastore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.kmpstarter.utils.datastore.AppDataStore
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject


@Composable
fun <T> rememberDataStoreValue(
    key: Preferences.Key<T>,
    defaultValue: T,
): State<T> {
    val appDataStore: AppDataStore = koinInject()

    val flow = remember(key) {
        appDataStore.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    return flow.collectAsState(initial = defaultValue)
}

@Composable
fun rememberIntDataStore(name: String, default: Int) =
    rememberDataStoreValue(intPreferencesKey(name), default)

@Composable
fun rememberStringDataStore(name: String, default: String) =
    rememberDataStoreValue(stringPreferencesKey(name), default)

@Composable
fun rememberBooleanDataStore(name: String, default: Boolean) =
    rememberDataStoreValue(booleanPreferencesKey(name), default)

@Composable
fun rememberStringSetDataStore(name: String, default: Set<String>) =
    rememberDataStoreValue(stringSetPreferencesKey(name), default)

















