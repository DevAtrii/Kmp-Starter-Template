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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "settings.preferences_pb"

/**
 * Application DataStore wrapper.
 *
 * Example:
 * ```
 * // Primitive values
 * val username = appDataStore.stringDataStore("username")
 * val age = appDataStore.intDataStore("age")
 * val isLoggedIn = appDataStore.booleanDataStore("logged_in")
 *
 * // Serializable objects
 * @Serializable
 * data class UserSettings(
 *     val notifications: Boolean = true,
 *     val theme: String = "System",
 * )
 *
 * val settings = appDataStore.serializableDataStore(
 *     name = "settings",
 *     default = UserSettings()
 * )
 *
 * settings.set(
 *     UserSettings(theme = "Dark")
 * )
 * ```
 */
expect class AppDataStore {
    val dataStore: DataStore<Preferences>
}

object CreateDataStore {
    private val lock = SynchronizedObject()
    private lateinit var dataStore: DataStore<Preferences>
    fun getDataStore(producePath: () -> String): DataStore<Preferences> {
        return synchronized(lock) {
            if (::dataStore.isInitialized) {
                dataStore
            } else {
                PreferenceDataStoreFactory.createWithPath(
                    produceFile = { producePath().toPath() }
                ).also { dataStore = it }
            }
        }
    }
}

expect fun createDataStore(context: Any? = null): DataStore<Preferences>






















