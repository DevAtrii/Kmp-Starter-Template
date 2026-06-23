package com.kmpstarter.feature_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kmpstarter.feature_database.entities.GpsPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsPoint(point: GpsPointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsPoints(points: List<GpsPointEntity>)

    @Query("SELECT * FROM gps_traces WHERE dayId = :dayId ORDER BY timestamp ASC")
    fun getGpsPointsForDay(dayId: String): Flow<List<GpsPointEntity>>

    @Query("SELECT * FROM gps_traces WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getGpsPointsForTrip(tripId: String): Flow<List<GpsPointEntity>>

    @Query("SELECT * FROM gps_traces WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getGpsPointsInRange(startTime: Long, endTime: Long): List<GpsPointEntity>

    @Query("SELECT * FROM gps_traces WHERE tripId IS NULL AND dayId IS NULL AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getUnassociatedGpsPoints(startTime: Long, endTime: Long): List<GpsPointEntity>

    @Query("UPDATE gps_traces SET tripId = :tripId, dayId = :dayId WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun associateGpsPointsToDay(tripId: String, dayId: String, startTime: Long, endTime: Long)

    @Query("DELETE FROM gps_traces WHERE tripId = :tripId")
    suspend fun deleteGpsPointsForTrip(tripId: String)
}
