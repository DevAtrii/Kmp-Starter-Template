package com.kmpstarter.feature_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kmpstarter.feature_database.entities.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Update
    suspend fun updateRecord(record: RecordEntity)

    @Query("UPDATE records SET deletedAt = :deletedAt WHERE id = :recordId")
    suspend fun softDeleteRecord(recordId: String, deletedAt: Long)

    @Query("SELECT * FROM records WHERE dayId = :dayId AND deletedAt IS NULL ORDER BY startTime ASC, timestamp ASC")
    fun getActiveRecordsForDay(dayId: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE id = :recordId LIMIT 1")
    suspend fun getRecordById(recordId: String): RecordEntity?

    @Query("SELECT * FROM records WHERE parentId = :parentId AND deletedAt IS NULL ORDER BY startTime ASC, timestamp ASC")
    suspend fun getChildRecords(parentId: String): List<RecordEntity>

    @Query("SELECT * FROM records WHERE dayId = :dayId AND type = :type AND deletedAt IS NULL ORDER BY startTime ASC")
    fun getRecordsByType(dayId: String, type: String): Flow<List<RecordEntity>>
}
