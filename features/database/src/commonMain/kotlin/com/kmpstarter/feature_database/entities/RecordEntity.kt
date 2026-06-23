package com.kmpstarter.feature_database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["dayId"]),
        Index(value = ["parentId"]),
        Index(value = ["deletedAt"])
    ]
)
data class RecordEntity(
    @PrimaryKey
    val id: String, // UUID String
    val dayId: String, // FK to days
    val parentId: String? = null, // Self-referencing FK for hierarchy
    
    // Core Type: "STAY", "TRANSIT", "POINT"
    val type: String, 
    
    // Common properties
    val title: String? = null,
    val locationName: String? = null,
    val deletedAt: Long? = null,
    
    // STAY / TRANSIT (RangeRecord) specific
    val startTime: Long? = null, // UTC timestamp (ms)
    val endTime: Long? = null,   // UTC timestamp (ms)
    val geoBounds: String? = null, // GeoJSON String representing Polygon or LineString
    val transportMode: String? = null, // "WALK", "SUBWAY", "BUS", "CAR", etc. (for TRANSIT)
    
    // POINT (PointRecord) specific
    val timestamp: Long? = null, // UTC timestamp (ms) for the point record
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: String? = null, // "PHOTO", "PURCHASE", "NOTE", "FOOD", etc.
    val source: String? = null,   // "MANUAL", "PHOTO_EXIF", "GPS_AUTO"
    val exifData: String? = null,  // JSON string of EXIF metadata
    val mediaUrls: String? = null  // Delimited string of media file paths/URLs (e.g. "path1;path2")
)
