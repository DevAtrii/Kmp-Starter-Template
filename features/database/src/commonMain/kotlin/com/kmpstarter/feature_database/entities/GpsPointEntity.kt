package com.kmpstarter.feature_database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gps_traces",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["tripId"]),
        Index(value = ["dayId"])
    ]
)
data class GpsPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long, // UTC epoch time (ms)
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float? = null,
    val altitude: Double? = null,
    val tripId: String? = null,
    val dayId: String? = null
)
