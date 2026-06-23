package com.kmpstarter.feature_core_domain.inference

import kotlin.math.sqrt

class KalmanFilter(private val minAccuracy: Float = 1.0f) {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var variance: Double = -1.0 // Negative indicates uninitialized
    private var lastTimeStamp: Long = 0

    fun reset() {
        variance = -1.0
    }

    fun filter(
        newLat: Double,
        newLng: Double,
        newAccuracy: Float,
        timeStamp: Long
    ): FilteredResult {
        var accuracy = newAccuracy
        if (accuracy < minAccuracy) {
            accuracy = minAccuracy
        }

        // Initialize state
        if (variance < 0.0) {
            latitude = newLat
            longitude = newLng
            variance = (accuracy * accuracy).toDouble()
            lastTimeStamp = timeStamp
            return FilteredResult(latitude, longitude, sqrt(variance).toFloat())
        }

        val dt = (timeStamp - lastTimeStamp).toDouble() / 1000.0 // in seconds
        lastTimeStamp = timeStamp

        if (dt > 0.0) {
            // Assume 3.0 m/s^2 typical max speed variance for system noise (Q)
            variance += dt * 3.0 * 3.0
        }

        // Kalman Gain (K)
        val r = (accuracy * accuracy).toDouble() // Measurement variance
        val k = variance / (variance + r)

        // Update state
        latitude += k * (newLat - latitude)
        longitude += k * (newLng - longitude)
        variance = (1.0 - k) * variance

        return FilteredResult(latitude, longitude, sqrt(variance).toFloat())
    }

    data class FilteredResult(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float
    )
}
