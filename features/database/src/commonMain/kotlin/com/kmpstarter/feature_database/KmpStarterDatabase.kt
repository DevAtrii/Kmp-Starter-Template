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

package com.kmpstarter.feature_database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.kmpstarter.feature_database.dao.GpsPointDao
import com.kmpstarter.feature_database.dao.RecordDao
import com.kmpstarter.feature_database.dao.TripDao
import com.kmpstarter.feature_database.entities.DayEntity
import com.kmpstarter.feature_database.entities.GpsPointEntity
import com.kmpstarter.feature_database.entities.RecordEntity
import com.kmpstarter.feature_database.entities.TripEntity

@Database(
    entities = [
        TripEntity::class,
        DayEntity::class,
        GpsPointEntity::class,
        RecordEntity::class
    ],
    version = KmpStarterDatabase.DB_VERSION,
    exportSchema = false
)
@ConstructedBy(KmpStarterDatabaseConstructor::class)
abstract class KmpStarterDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "raw_trip_record.db"
        const val DB_VERSION = 1
    }

    abstract fun tripDao(): TripDao
    abstract fun gpsPointDao(): GpsPointDao
    abstract fun recordDao(): RecordDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object KmpStarterDatabaseConstructor : RoomDatabaseConstructor<KmpStarterDatabase> {
    override fun initialize(): KmpStarterDatabase
}



















