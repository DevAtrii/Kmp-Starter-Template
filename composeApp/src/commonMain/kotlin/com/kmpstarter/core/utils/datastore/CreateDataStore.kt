package com.kmpstarter.core.utils.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "settings.preferences_pb"

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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






















