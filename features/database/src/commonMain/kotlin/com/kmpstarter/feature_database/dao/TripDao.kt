package com.kmpstarter.feature_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kmpstarter.feature_database.entities.DayEntity
import com.kmpstarter.feature_database.entities.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("UPDATE trips SET deletedAt = :deletedAt WHERE id = :tripId")
    suspend fun softDeleteTrip(tripId: String, deletedAt: Long)

    @Query("SELECT * FROM trips WHERE deletedAt IS NULL ORDER BY startDate DESC")
    fun getActiveTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: DayEntity)

    @Query("SELECT * FROM days WHERE tripId = :tripId AND deletedAt IS NULL ORDER BY date ASC")
    fun getDaysForTrip(tripId: String): Flow<List<DayEntity>>

    @Query("SELECT * FROM days WHERE id = :dayId LIMIT 1")
    suspend fun getDayById(dayId: String): DayEntity?

    @Query("UPDATE days SET deletedAt = :deletedAt WHERE id = :dayId")
    suspend fun softDeleteDay(dayId: String, deletedAt: Long)
}
