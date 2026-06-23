package com.kmpstarter.feature_core_domain.inference

import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.PI

data class RawGpsPoint(
    val timestamp: Long, // Epoch ms
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float? = null,
    val altitude: Double? = null
)

class GpsSanitizer(
    private val maxAccuracyLimit: Float = 100.0f, // Filter out points worse than 100m
    private val maxSpeedKmh: Double = 250.0 // Filter out jumps exceeding 250 km/h
) {

    fun sanitize(points: List<RawGpsPoint>): List<RawGpsPoint> {
        if (points.isEmpty()) return emptyList()

        // 1. Accuracy Filter
        val accuracyFiltered = points.filter { it.accuracy <= maxAccuracyLimit }
        if (accuracyFiltered.isEmpty()) return emptyList()

        // 2. Speed Anomaly Filter
        val speedFiltered = mutableListOf<RawGpsPoint>()
        speedFiltered.add(accuracyFiltered[0])

        for (i in 1 until accuracyFiltered.size) {
            val prev = speedFiltered.last()
            val curr = accuracyFiltered[i]
            
            val dt = (curr.timestamp - prev.timestamp).toDouble() / 1000.0 // seconds
            if (dt <= 0.0) continue

            val distance = haversineDistance(prev.latitude, prev.longitude, curr.latitude, curr.longitude)
            val speedMps = distance / dt
            val speedKmh = speedMps * 3.6

            if (speedKmh <= maxSpeedKmh) {
                speedFiltered.add(curr)
            }
        }

        // 3. Kalman Filter Smoothing
        val kalmanFilter = KalmanFilter()
        val smoothed = speedFiltered.map { pt ->
            val res = kalmanFilter.filter(pt.latitude, pt.longitude, pt.accuracy, pt.timestamp)
            pt.copy(
                latitude = res.latitude,
                longitude = res.longitude,
                accuracy = res.accuracy
            )
        }

        // 4. Moving Average (Weighted window of size 3) for micro-jitter damping
        return applyMovingAverage(smoothed)
    }

    private fun applyMovingAverage(points: List<RawGpsPoint>): List<RawGpsPoint> {
        if (points.size < 3) return points
        val result = mutableListOf<RawGpsPoint>()
        result.add(points.first())

        for (i in 1 until points.size - 1) {
            val p0 = points[i - 1]
            val p1 = points[i]
            val p2 = points[i + 1]

            // Weighted average: 25% prev, 50% current, 25% next
            val avgLat = (p0.latitude * 0.25) + (p1.latitude * 0.50) + (p2.latitude * 0.25)
            val avgLng = (p0.longitude * 0.25) + (p1.longitude * 0.50) + (p2.longitude * 0.25)

            result.add(p1.copy(latitude = avgLat, longitude = avgLng))
        }
        result.add(points.last())
        return result
    }

    companion object {
        fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 6371000.0 // Earth radius in meters
            val dLat = (lat2 - lat1) * PI / 180.0
            val dLon = (lon2 - lon1) * PI / 180.0
            
            val lat1Rad = lat1 * PI / 180.0
            val lat2Rad = lat2 * PI / 180.0

            val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                    cos(lat1Rad) * cos(lat2Rad) *
                    sin(dLon / 2.0) * sin(dLon / 2.0)
            
            val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
            return r * c
        }
    }
}
