package com.kmpstarter.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kmpstarter.feature_core_domain.inference.InferredRecord
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint

@Composable
actual fun PlatformMapView(
    modifier: Modifier,
    gpsPoints: List<RawGpsPoint>,
    records: List<InferredRecord>
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text("iOS MapView Placeholder (MapLibre)", color = Color.White)
    }
}
