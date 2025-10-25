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

package com.kmpstarter.core_db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseProvider(
    private val context: Context,
) {
    actual fun getDatabase():  RoomDatabase.Builder<KmpStarterDatabase> {
        val appCtx = context.applicationContext
        val dbFile = appCtx.getDatabasePath(KmpStarterDatabase.DB_NAME)
        return Room.databaseBuilder<KmpStarterDatabase>(
            context = appCtx,
            name = dbFile.absolutePath
        )
    }
}