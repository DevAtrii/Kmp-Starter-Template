package com.kmpstarter.core.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseProvider {
    actual fun getDatabase(): RoomDatabase.Builder<KmpStarterDatabase> {
        val documentDirectory = documentDirectory()
        val dbFile = documentDirectory + "/${KmpStarterDatabase.DB_NAME}"
        return Room.databaseBuilder<KmpStarterDatabase>(
            name = dbFile,
        )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}