package com.kmpstarter.core.utils.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    return CreateDataStore.getDataStore(
        producePath = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            requireNotNull(documentDirectory).path + "/$dataStoreFileName"
        }
    )
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppDataStore {
    actual val dataStore: DataStore<Preferences>
        get() = createDataStore()

}