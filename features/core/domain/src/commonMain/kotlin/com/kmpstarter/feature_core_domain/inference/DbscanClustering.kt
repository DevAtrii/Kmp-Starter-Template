package com.kmpstarter.feature_core_domain.inference

class DbscanClustering(
    private val epsMeters: Double = 50.0,        // Radius limit for stationary state
    private val minDurationMs: Long = 600000L    // Minimum duration required to consider as STAY (10 minutes)
) {

    fun cluster(points: List<RawGpsPoint>): List<GpsCluster> {
        if (points.size < 2) return emptyList()
        
        val clusters = mutableListOf<GpsCluster>()
        var i = 0

        while (i < points.size) {
            var j = i + 1
            
            // Collect points that remain within 'epsMeters' distance from the start point
            while (j < points.size) {
                val distance = GpsSanitizer.haversineDistance(
                    points[i].latitude, points[i].longitude,
                    points[j].latitude, points[j].longitude
                )
                if (distance > epsMeters) {
                    break
                }
                j++
            }

            val duration = points[j - 1].timestamp - points[i].timestamp

            // If the user stayed in this spot long enough, record it as a cluster
            if (duration >= minDurationMs) {
                val clusterPoints = points.subList(i, j)
                clusters.add(
                    GpsCluster(
                        startTime = points[i].timestamp,
                        endTime = points[j - 1].timestamp,
                        points = clusterPoints
                    )
                )
                i = j // Jump to the end of this cluster
            } else {
                i++
            }
        }
        return clusters
    }

    data class GpsCluster(
        val startTime: Long,
        val endTime: Long,
        val points: List<RawGpsPoint>
    ) {
        val centerLatitude: Double
            get() = points.map { it.latitude }.average()
        val centerLongitude: Double
            get() = points.map { it.longitude }.average()
            
        val minLatitude: Double
            get() = points.minOf { it.latitude }
        val maxLatitude: Double
            get() = points.maxOf { it.latitude }
        val minLongitude: Double
            get() = points.minOf { it.longitude }
        val maxLongitude: Double
            get() = points.maxOf { it.longitude }
    }
}
