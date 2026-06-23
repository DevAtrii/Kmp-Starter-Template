package com.kmpstarter.core.ui.components

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kmpstarter.feature_core_domain.inference.InferredRecord
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

// Catmull-Rom Spline Interpolation for TRANSIT Smoothing
private fun interpolateCatmullRom(
    p0: RawGpsPoint,
    p1: RawGpsPoint,
    p2: RawGpsPoint,
    p3: RawGpsPoint,
    steps: Int = 8
): List<LatLng> {
    val points = mutableListOf<LatLng>()
    for (i in 0..steps) {
        val t = i.toDouble() / steps
        val t2 = t * t
        val t3 = t2 * t

        val lat = 0.5 * (
            (2.0 * p1.latitude) +
            (-p0.latitude + p2.latitude) * t +
            (2.0 * p0.latitude - 5.0 * p1.latitude + 4.0 * p2.latitude - p3.latitude) * t2 +
            (-p0.latitude + 3.0 * p1.latitude - 3.0 * p2.latitude + p3.latitude) * t3
        )

        val lng = 0.5 * (
            (2.0 * p1.longitude) +
            (-p0.longitude + p2.longitude) * t +
            (2.0 * p0.longitude - 5.0 * p1.longitude + 4.0 * p2.longitude - p3.longitude) * t2 +
            (-p0.longitude + 3.0 * p1.longitude - 3.0 * p2.longitude + p3.longitude) * t3
        )

        points.add(LatLng(lat, lng))
    }
    return points
}

private fun generateSplinePath(points: List<RawGpsPoint>, steps: Int = 8): List<LatLng> {
    if (points.isEmpty()) return emptyList()
    if (points.size < 2) return points.map { LatLng(it.latitude, it.longitude) }

    val result = mutableListOf<LatLng>()
    val padded = mutableListOf<RawGpsPoint>()
    padded.add(points.first())
    padded.addAll(points)
    padded.add(points.last())

    for (i in 1 until padded.size - 2) {
        val p0 = padded[i - 1]
        val p1 = padded[i]
        val p2 = padded[i + 1]
        val p3 = padded[i + 2]

        val segment = interpolateCatmullRom(p0, p1, p2, p3, steps)
        if (i < padded.size - 3) {
            result.addAll(segment.dropLast(1))
        } else {
            result.addAll(segment)
        }
    }
    return result
}

@Composable
actual fun PlatformMapView(
    modifier: Modifier,
    gpsPoints: List<RawGpsPoint>,
    records: List<InferredRecord>
) {
    val context = LocalContext.current

    // Initialize MapLibre
    remember {
        MapLibre.getInstance(context)
    }

    val mapView = remember { MapView(context) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { mv ->
            mv.getMapAsync { maplibreMap ->
                // Use MapLibre demo style which does not require API keys
                val styleUrl = "https://demotiles.maplibre.org/style.json"
                maplibreMap.setStyle(styleUrl) { style ->
                    // 1. Process STAY Records (Water-color blobs with blur)
                    val stayRecords = records.filter { it.type == "STAY" }
                    if (stayRecords.isNotEmpty()) {
                        // Gather points of all STAY locations
                        val stayFeaturesJson = stayRecords.flatMap { record ->
                            record.gpsPoints.map { pt ->
                                """{"type":"Feature","geometry":{"type":"Point","coordinates":[${pt.longitude},${pt.latitude}]}}"""
                            }
                        }.joinToString(",")

                        val stayGeoJson = """{"type":"FeatureCollection","features":[$stayFeaturesJson]}"""
                        
                        // Clean up existing source/layer if any to prevent crashes on update
                        style.getLayer("stay-layer")?.let { style.removeLayer(it) }
                        style.getSource("stay-source")?.let { style.removeSource(it) }

                        val staySource = GeoJsonSource("stay-source", stayGeoJson)
                        style.addSource(staySource)

                        val stayLayer = CircleLayer("stay-layer", "stay-source").apply {
                            setProperties(
                                PropertyFactory.circleColor("#8A55FD"), // STAY Premium Purple
                                PropertyFactory.circleOpacity(0.45f),
                                PropertyFactory.circleRadius(
                                    Expression.interpolate(
                                        Expression.exponential(1.5f),
                                        Expression.zoom(),
                                        Expression.stop(5, 10f),
                                        Expression.stop(12, 25f),
                                        Expression.stop(16, 80f)
                                    )
                                ),
                                PropertyFactory.circleBlur(0.85f) // Watercolour bleed/blur effect
                            )
                        }
                        style.addLayer(stayLayer)
                    }

                    // 2. Process TRANSIT Records (Bezier spline interpolation & Soft-edges)
                    val transitRecords = records.filter { it.type == "TRANSIT" }
                    if (transitRecords.isNotEmpty()) {
                        val transitFeatures = mutableListOf<String>()

                        transitRecords.forEach { record ->
                            val smoothedCoordinates = generateSplinePath(record.gpsPoints)
                            if (smoothedCoordinates.size >= 2) {
                                val coordsJson = smoothedCoordinates.joinToString(",") {
                                    "[${it.longitude},${it.latitude}]"
                                }
                                val mode = record.transportMode ?: "WALK"
                                transitFeatures.add(
                                    """{
                                        "type":"Feature",
                                        "properties":{"mode":"$mode"},
                                        "geometry":{"type":"LineString","coordinates":[$coordsJson]}
                                    }""".trimIndent()
                                )
                            }
                        }

                        if (transitFeatures.isNotEmpty()) {
                            val transitGeoJson = """{"type":"FeatureCollection","features":[${transitFeatures.joinToString(",")}]}"""
                            
                            style.getLayer("transit-layer")?.let { style.removeLayer(it) }
                            style.getSource("transit-source")?.let { style.removeSource(it) }

                            val transitSource = GeoJsonSource("transit-source", transitGeoJson)
                            style.addSource(transitSource)

                            val transitLayer = LineLayer("transit-layer", "transit-source").apply {
                                setProperties(
                                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                    PropertyFactory.lineColor(
                                        Expression.match(
                                            Expression.get("mode"),
                                            Expression.literal("WALK"), Expression.literal("#E0AA3E"),  // Gold
                                            Expression.literal("BICYCLE"), Expression.literal("#10B981"), // Green
                                            Expression.literal("#3B82F6") // Blue for VEHICLE
                                        )
                                    ),
                                    PropertyFactory.lineWidth(
                                        Expression.interpolate(
                                            Expression.exponential(1.5f),
                                            Expression.zoom(),
                                            Expression.stop(5, 2f),
                                            Expression.stop(12, 5f),
                                            Expression.stop(16, 12f)
                                        )
                                    ),
                                    PropertyFactory.lineBlur(0.4f) // Soft edge blur to mask GPS inaccuracy
                                )
                            }
                            style.addLayer(transitLayer)
                        }
                    }

                    // 3. Move camera to fit all coordinates
                    if (gpsPoints.isNotEmpty()) {
                        val boundsBuilder = LatLngBounds.Builder()
                        gpsPoints.forEach { pt ->
                            boundsBuilder.include(LatLng(pt.latitude, pt.longitude))
                        }
                        try {
                            val bounds = boundsBuilder.build()
                            // Apply ease camera to fit the bounding box
                            maplibreMap.easeCamera(
                                CameraUpdateFactory.newLatLngBounds(bounds, 120),
                                1200
                            )
                        } catch (e: Exception) {
                            // Single point fallback
                            val singlePt = gpsPoints.first()
                            maplibreMap.easeCamera(
                                CameraUpdateFactory.newLatLng(LatLng(singlePt.latitude, singlePt.longitude)),
                                1000
                            )
                        }
                    }
                }
            }
        }
    )
}
