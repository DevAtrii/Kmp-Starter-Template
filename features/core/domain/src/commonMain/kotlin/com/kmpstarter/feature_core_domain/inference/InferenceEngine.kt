package com.kmpstarter.feature_core_domain.inference

import kotlin.math.sqrt

// Light representation of inferred records returned by the engine
data class InferredRecord(
    val id: String,
    val type: String, // "STAY" or "TRANSIT"
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val locationName: String? = null,
    val transportMode: String? = null, // "WALK", "BICYCLE", "VEHICLE"
    val geoBounds: String? = null, // GeoJSON string represent bounds or paths
    val gpsPoints: List<RawGpsPoint>
)

data class InferenceResult(
    val sanitizedPoints: List<RawGpsPoint>,
    val records: List<InferredRecord>
)

class InferenceEngine(
    private val sanitizer: GpsSanitizer = GpsSanitizer(),
    private val clustering: DbscanClustering = DbscanClustering()
) {

    fun process(rawPoints: List<RawGpsPoint>): InferenceResult {
        if (rawPoints.isEmpty()) {
            return InferenceResult(emptyList(), emptyList())
        }

        // 1. Sanitize raw GPS data (speed jumps, kalman smooth, moving avg)
        val sanitized = sanitizer.sanitize(rawPoints)
        if (sanitized.isEmpty()) {
            return InferenceResult(emptyList(), emptyList())
        }

        // 2. Identify STAY clusters
        val clusters = clustering.cluster(sanitized)
        val records = mutableListOf<InferredRecord>()

        if (clusters.isEmpty()) {
            // No stay detected, the whole trip is a single TRANSIT
            val transitRecord = createTransitRecord(sanitized, 0, sanitized.size - 1)
            transitRecord?.let { records.add(it) }
            return InferenceResult(sanitized, records)
        }

        // 3. Segment into STAYs and TRANSITs alternately
        var lastProcessedIndex = 0

        for (clusterIndex in clusters.indices) {
            val cluster = clusters[clusterIndex]
            
            // Find index boundary of this cluster in the sanitized list
            val clusterStartIdx = sanitized.indexOfFirst { it.timestamp == cluster.startTime }
            val clusterEndIdx = sanitized.indexOfFirst { it.timestamp == cluster.endTime }

            if (clusterStartIdx == -1 || clusterEndIdx == -1) continue

            // A. If there is space before this cluster, create a TRANSIT record
            if (clusterStartIdx > lastProcessedIndex) {
                val transitRecord = createTransitRecord(sanitized, lastProcessedIndex, clusterStartIdx - 1)
                transitRecord?.let { records.add(it) }
            }

            // B. Create a STAY record
            val stayPoints = sanitized.subList(clusterStartIdx, clusterEndIdx + 1)
            records.add(
                InferredRecord(
                    id = generateSimpleId(),
                    type = "STAY",
                    startTime = cluster.startTime,
                    endTime = cluster.endTime,
                    title = "방문 장소",
                    locationName = "위도: ${formatDouble(cluster.centerLatitude)}, 경도: ${formatDouble(cluster.centerLongitude)}",
                    geoBounds = buildPolygonGeoJson(cluster),
                    gpsPoints = stayPoints
                )
            )

            lastProcessedIndex = clusterEndIdx + 1
        }

        // C. If there are leftover points after the last STAY, create a final TRANSIT
        if (lastProcessedIndex < sanitized.size) {
            val transitRecord = createTransitRecord(sanitized, lastProcessedIndex, sanitized.size - 1)
            transitRecord?.let { records.add(it) }
        }

        return InferenceResult(sanitized, records)
    }

    private fun createTransitRecord(
        points: List<RawGpsPoint>,
        startIdx: Int,
        endIdx: Int
    ): InferredRecord? {
        val segment = points.subList(startIdx, endIdx + 1)
        if (segment.size < 2) return null

        val startTime = segment.first().timestamp
        val endTime = segment.last().timestamp
        val dtSeconds = (endTime - startTime).toDouble() / 1000.0

        if (dtSeconds <= 0.0) return null

        // Calculate total distance & speed profile
        var totalDistance = 0.0
        for (i in 0 until segment.size - 1) {
            totalDistance += GpsSanitizer.haversineDistance(
                segment[i].latitude, segment[i].longitude,
                segment[i + 1].latitude, segment[i + 1].longitude
            )
        }

        val avgSpeedMps = totalDistance / dtSeconds
        val avgSpeedKmh = avgSpeedMps * 3.6

        // Infer transport mode based on speed
        val mode = when {
            avgSpeedKmh < 8.0 -> "WALK"
            avgSpeedKmh < 25.0 -> "BICYCLE"
            else -> "VEHICLE"
        }

        val modeLabel = when (mode) {
            "WALK" -> "도보 이동"
            "BICYCLE" -> "자전거 이동"
            else -> "차량/대중교통 이동"
        }

        return InferredRecord(
            id = generateSimpleId(),
            type = "TRANSIT",
            startTime = startTime,
            endTime = endTime,
            title = modeLabel,
            transportMode = mode,
            geoBounds = buildLineStringGeoJson(segment),
            gpsPoints = segment
        )
    }

    private fun generateSimpleId(): String {
        // Simple fallback pseudo-UUID implementation for multiplatform compatibility without extra dependencies
        val chars = "abcdef0123456789"
        val builder = StringBuilder()
        for (i in 0 until 32) {
            builder.append(chars[(0 until chars.length).random()])
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                builder.append("-")
            }
        }
        return builder.toString()
    }

    private fun formatDouble(value: Double): Double {
        return (value * 10000.0).toInt() / 10000.0 // 4 decimal places
    }

    private fun buildPolygonGeoJson(cluster: DbscanClustering.GpsCluster): String {
        // Build simple bounding box Polygon GeoJSON
        return """{"type":"Polygon","coordinates":[[[${cluster.minLongitude},${cluster.minLatitude}],[${cluster.maxLongitude},${cluster.minLatitude}],[${cluster.maxLongitude},${cluster.maxLatitude}],[${cluster.minLongitude},${cluster.maxLatitude}],[${cluster.minLongitude},${cluster.minLatitude}]]]}"""
    }

    private fun buildLineStringGeoJson(points: List<RawGpsPoint>): String {
        val coords = points.joinToString(",") { "[${it.longitude},${it.latitude}]" }
        return """{"type":"LineString","coordinates":[$coords]}"""
    }
}
