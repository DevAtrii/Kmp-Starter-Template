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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kmpstarter.utils.datastore.AppDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject


@Composable
fun <T> rememberMutableDataStoreState(
    key: Preferences.Key<T>,
    defaultValue: T?,
): MutableState<T?> {
    val appDataStore: AppDataStore = koinInject()
    val scope = rememberCoroutineScope()

    val state by remember(key) {
        appDataStore.dataStore.data.map { it[key] ?: defaultValue }
    }.collectAsState(initial = defaultValue)

    return remember(state) {
        object : MutableState<T?> {
            override var value: T?
                get() = state
                set(newValue) {
                    scope.launch {
                        appDataStore.dataStore.edit { preferences ->
                            if (newValue == null)
                                preferences.remove(key)
                            else
                                preferences[key] = newValue
                        }
                    }
                }

            override fun component1(): T? = value
            override fun component2(): (T?) -> Unit = { value = it }
        }
    }
}


@Composable
fun rememberMutableIntDataStore(name: String, default: Int?) =
    rememberMutableDataStoreState(intPreferencesKey(name), default)

@Composable
fun rememberMutableStringDataStore(name: String, default: String?) =
    rememberMutableDataStoreState(stringPreferencesKey(name), default)

@Composable
fun rememberMutableBooleanDataStore(name: String, default: Boolean?) =
    rememberMutableDataStoreState(booleanPreferencesKey(name), default)

@Composable
fun rememberMutableLongDataStore(name: String, default: Long?) =
    rememberMutableDataStoreState(longPreferencesKey(name), default)


@Composable
inline fun <reified T> rememberMutableSerializableDataStore(
    name: String,
    default: T,
    noinline provideJson: () -> Json = { Json.Default },
): MutableState<T> {
    val json = remember(provideJson) { provideJson() }

    val defaultJson = remember(json, default) {
        json.encodeToString(default)
    }

    val rawJson = rememberMutableStringDataStore(
        name = name,
        default = defaultJson
    )

    return remember(rawJson, json) {
        object : MutableState<T> {
            override var value: T
                get() = runCatching {
                    json.decodeFromString<T>(rawJson.value ?: defaultJson)
                }.getOrDefault(default)
                set(value) {
                    rawJson.value = json.encodeToString(value)
                }

            override fun component1(): T = value

            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

















