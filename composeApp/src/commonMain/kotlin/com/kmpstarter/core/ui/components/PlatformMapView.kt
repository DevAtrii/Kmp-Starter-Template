package com.kmpstarter.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmpstarter.feature_core_domain.inference.InferredRecord
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint

@Composable
expect fun PlatformMapView(
    modifier: Modifier = Modifier,
    gpsPoints: List<RawGpsPoint>,
    records: List<InferredRecord>
)
