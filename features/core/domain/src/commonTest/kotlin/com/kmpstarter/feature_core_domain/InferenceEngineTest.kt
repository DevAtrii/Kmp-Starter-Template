package com.kmpstarter.feature_core_domain

import com.kmpstarter.feature_core_domain.inference.DbscanClustering
import com.kmpstarter.feature_core_domain.inference.GpsSanitizer
import com.kmpstarter.feature_core_domain.inference.InferenceEngine
import com.kmpstarter.feature_core_domain.inference.KalmanFilter
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InferenceEngineTest {

    @Test
    fun testKalmanFilterSmoothing() {
        val filter = KalmanFilter()
        
        // Feed constant points
        val pt1 = filter.filter(37.5665, 126.9780, 5.0f, 1000L)
        val pt2 = filter.filter(37.5665, 126.9780, 5.0f, 2000L)
        
        assertEquals(37.5665, pt1.latitude)
        assertEquals(37.5665, pt2.latitude)

        // Feed a sudden jump, the filter should smooth it out
        val pt3 = filter.filter(37.5690, 126.9810, 5.0f, 3000L)
        // 37.5690 is not fully reached immediately due to filter inertia
        assertTrue(pt3.latitude < 37.5690)
    }

    @Test
    fun testGpsSanitizerFiltersSpeedJumps() {
        val sanitizer = GpsSanitizer()
        
        val startTime = 1719176400000L // 0s
        val points = listOf(
            RawGpsPoint(startTime, 37.5665, 126.9780, 5f), // Start
            RawGpsPoint(startTime + 2000L, 37.5666, 126.9781, 5f), // Normal step (2s later)
            RawGpsPoint(startTime + 4000L, 38.5665, 127.9780, 5f), // INSANE JUMP! (~140 km distance in 2s -> ~250,000 km/h)
            RawGpsPoint(startTime + 6000L, 37.5667, 126.9782, 5f)  // Back to normal
        )

        val sanitized = sanitizer.sanitize(points)
        
        // The insane jump at index 2 should have been filtered out completely
        assertEquals(3, sanitized.size)
        // Check that the coordinates reflect the normal progression rather than the jump
        assertTrue(sanitized[1].latitude < 37.6)
        assertTrue(sanitized[2].latitude < 37.6)
    }

    @Test
    fun testDbscanClusteringDetectsStays() {
        val clustering = DbscanClustering(epsMeters = 50.0, minDurationMs = 600000L) // 10 mins STAY
        
        val startTime = 1719176400000L
        val points = mutableListOf<RawGpsPoint>()
        
        // 1. STAY at Spot A (Seoul City Hall) for 15 minutes (0 to 900,000ms)
        for (i in 0..15) {
            points.add(
                RawGpsPoint(
                    timestamp = startTime + (i * 60000L), // Every 1 minute
                    latitude = 37.5665 + (i * 0.00001), // Minor micro movements (within 2-3 meters)
                    longitude = 126.9780 + (i * 0.00001),
                    accuracy = 5f
                )
            )
        }

        val clusters = clustering.cluster(points)
        assertEquals(1, clusters.size)
        assertEquals(startTime, clusters[0].startTime)
        assertEquals(startTime + 900000L, clusters[0].endTime)
    }

    @Test
    fun testInferenceEngineCombinesStayAndTransit() {
        val engine = InferenceEngine()
        val startTime = 1719176400000L
        val points = mutableListOf<RawGpsPoint>()

        // 1. STAY at Spot A (0 to 12 minutes = 720,000ms)
        for (i in 0..12) {
            points.add(RawGpsPoint(startTime + (i * 60000L), 37.5665, 126.9780, 5f))
        }

        // 2. WALK to Spot B (12 to 16 minutes)
        // distance is about 370 meters in 4 minutes -> ~5.5 km/h (WALK)
        points.add(RawGpsPoint(startTime + (13 * 60000L), 37.5675, 126.9790, 5f))
        points.add(RawGpsPoint(startTime + (14 * 60000L), 37.5685, 126.9800, 5f))
        points.add(RawGpsPoint(startTime + (15 * 60000L), 37.5695, 126.9810, 5f))
        points.add(RawGpsPoint(startTime + (16 * 60000L), 37.5698, 126.9813, 5f))

        // 3. STAY at Spot B (16 to 30 minutes = 14 minutes stay)
        for (i in 16..30) {
            points.add(RawGpsPoint(startTime + (i * 60000L), 37.5698, 126.9813, 5f))
        }

        val result = engine.process(points)
        
        // Should infer 3 records: STAY -> TRANSIT -> STAY
        assertEquals(3, result.records.size)
        
        assertEquals("STAY", result.records[0].type)
        assertEquals("TRANSIT", result.records[1].type)
        assertEquals("WALK", result.records[1].transportMode) // Inferred as WALK
        assertEquals("STAY", result.records[2].type)
    }
}
