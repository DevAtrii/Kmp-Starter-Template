package com.kmpstarter.feature_database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trips",
    indices = [Index(value = ["deletedAt"])]
)
data class TripEntity(
    @PrimaryKey
    val id: String, // UUID String
    val title: String,
    val startDate: Long, // UTC Timestamp (ms)
    val endDate: Long,   // UTC Timestamp (ms)
    val deletedAt: Long? = null // Soft delete timestamp (ms)
)

@Entity(
    tableName = "days",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tripId"]),
        Index(value = ["deletedAt"])
    ]
)
data class DayEntity(
    @PrimaryKey
    val id: String, // UUID String
    val tripId: String, // FK to trips
    val date: Long, // Date timestamp (ms)
    val summary: String? = null,
    val deletedAt: Long? = null
)
