package com.kmpstarter.core.utils.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    require(
        value = context is Context,
        lazyMessage = { "Context object is required." }
    )
    return CreateDataStore.getDataStore(
        producePath = {
            context.filesDir
                .resolve(dataStoreFileName)
                .absolutePath
        }
    )
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppDataStore(private val context: Context) {
    actual val dataStore: DataStore<Preferences>
        get() = createDataStore(context = context)

}