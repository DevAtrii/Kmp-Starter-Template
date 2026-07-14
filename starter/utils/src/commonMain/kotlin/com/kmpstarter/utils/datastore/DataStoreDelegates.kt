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

package com.kmpstarter.utils.datastore


import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Type-safe DataStore delegate for serializable objects.
 */
class SerializableDataStoreDelegate<T>(
    private val appDataStore: AppDataStore,
    private val key: Preferences.Key<String>,
    private val serializer: KSerializer<T>,
    private val default: T,
    private val json: Json = Json.Default,
) {

    private val defaultJson = json.encodeToString(serializer, default)

    /**
     * Observe value changes.
     */
    val flow: Flow<T> = appDataStore.dataStore.data.map { preferences ->
        decode(preferences[key])
    }

    /**
     * Read current value.
     */
    suspend fun get(): T {
        return decode(
            appDataStore.dataStore.data
                .map { it[key] }
                .first()
        )
    }

    /**
     * Save value.
     */
    suspend fun set(value: T) {
        appDataStore.dataStore.edit {
            it[key] = json.encodeToString(serializer, value)
        }
    }

    /**
     * Remove stored value.
     */
    suspend fun clear() {
        appDataStore.dataStore.edit {
            it.remove(key)
        }
    }

    private fun decode(raw: String?): T {
        return runCatching {
            json.decodeFromString(
                serializer,
                raw ?: defaultJson
            )
        }.getOrDefault(default)
    }
}

/**
 * Creates a serializable DataStore delegate.
 */
inline fun <reified T> AppDataStore.serializableDataStore(
    name: String,
    default: T,
    json: Json = Json.Default,
): SerializableDataStoreDelegate<T> {
    return SerializableDataStoreDelegate(
        appDataStore = this,
        key = stringPreferencesKey(name),
        serializer = serializer<T>(),
        default = default,
        json = json
    )
}