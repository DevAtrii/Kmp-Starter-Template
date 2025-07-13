package com.kmpstarter.core.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseProvider {
    fun getDatabase(): RoomDatabase.Builder<KmpStarterDatabase>
}

fun getKmpDatabase(
    databaseProvider: DatabaseProvider,
): KmpStarterDatabase {
    val builder = databaseProvider.getDatabase()
    return builder
//        .addMigrations(MIGRATIONS)
//        .fallbackToDestructiveMigrationOnDowngrade()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}