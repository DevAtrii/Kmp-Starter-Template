package com.kmpstarter.core.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import com.kmpstarter.core.ui.components.PlatformMapView
import com.kmpstarter.feature_core_domain.inference.InferredRecord
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint

// Mock data model for UI representation
data class TimelineItem(
    val id: String,
    val type: String, // "STAY" or "TRANSIT" or "POINT"
    val title: String,
    val detail: String,
    val startMinute: Int, // 0 to 1440 minutes in a day
    val endMinute: Int,
    val depth: Int = 0, // hierarchy depth
    val category: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(onBackClick: () -> Unit) {
    // 1. Zoom and Scale State (Pinch-to-zoom)
    var scale by remember { mutableStateOf(1.0f) }
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(0.6f, 3.5f)
    }

    // Realistic Mock GPS Points for Shibuya-Omotesando Area
    val mockGpsPoints = remember {
        listOf(
            RawGpsPoint(1710000000000 + 0, 35.658034, 139.701636, 10f),
            RawGpsPoint(1710000000000 + 10000, 35.6583, 139.7013, 12f),
            RawGpsPoint(1710000000000 + 20000, 35.6586, 139.7010, 8f),
            RawGpsPoint(1710000000000 + 60000, 35.6592, 139.7003, 15f),
            RawGpsPoint(1710000000000 + 120000, 35.6593, 139.7004, 10f),
            RawGpsPoint(1710000000000 + 180000, 35.6591, 139.7002, 9f),
            RawGpsPoint(1710000000000 + 240000, 35.6608, 139.6979, 7f),
            RawGpsPoint(1710000000000 + 300000, 35.6607, 139.6978, 6f),
            RawGpsPoint(1710000000000 + 360000, 35.6580, 139.7016, 20f),
            RawGpsPoint(1710000000000 + 370000, 35.6600, 139.7050, 25f),
            RawGpsPoint(1710000000000 + 380000, 35.6620, 139.7090, 30f),
            RawGpsPoint(1710000000000 + 390000, 35.6652, 139.7125, 20f),
            RawGpsPoint(1710000000000 + 450000, 35.6622, 139.7130, 8f),
            RawGpsPoint(1710000000000 + 510000, 35.6621, 139.7129, 11f),
            RawGpsPoint(1710000000000 + 600000, 35.6623, 139.7131, 5f)
        )
    }

    val mockRecords = remember {
        listOf(
            InferredRecord(
                id = "rec1",
                type = "STAY",
                startTime = 1710000000000,
                endTime = 1710000000000 + 300000,
                title = "시부야 쇼핑 스트리트",
                locationName = "시부야역 근방",
                gpsPoints = mockGpsPoints.subList(0, 8)
            ),
            InferredRecord(
                id = "rec2",
                type = "TRANSIT",
                startTime = 1710000000000 + 300000,
                endTime = 1710000000000 + 400000,
                title = "지하철 이동 (한조몬선)",
                transportMode = "VEHICLE",
                gpsPoints = mockGpsPoints.subList(8, 12)
            ),
            InferredRecord(
                id = "rec3",
                type = "STAY",
                startTime = 1710000000000 + 400000,
                endTime = 1710000000000 + 600000,
                title = "블루보틀 커피 아오야마",
                locationName = "아오야마 3초메",
                gpsPoints = mockGpsPoints.subList(12, 15)
            )
        )
    }

    // 2. Timeline List State (Mock data demonstrating hierarchy and stay/transit)
    var items by remember {
        mutableStateOf(
            listOf(
                TimelineItem("t1", "TRANSIT", "도보 이동", "평균 속도: 4.8 km/h", 480, 520, 0, "WALK"),
                TimelineItem("t2", "STAY", "시부야 쇼핑 스트리트", "시부야구 우다가와초 일대 배회", 520, 800, 0),
                // Nested child record under t2 (hierarchy)
                TimelineItem("t3", "STAY", "돈키호테 시부야점", "기념품 구매", 580, 700, 1),
                TimelineItem("t4", "POINT", "돈키호테 구매 기록 🛍️", "결제 금액: ¥12,500", 630, 630, 2, "PURCHASE"),
                TimelineItem("t5", "TRANSIT", "지하철 이동 (한조몬선)", "시부야역 ➔ 오모테산도역", 800, 840, 0, "SUBWAY"),
                TimelineItem("t6", "STAY", "블루보틀 커피 아오야마", "라떼 브레이크", 840, 960, 0),
                TimelineItem("t7", "POINT", "커피 사진 ☕", "EXIF 촬영 위치 일치", 900, 900, 1, "PHOTO")
            )
        )
    }

    val longestStayId = remember(items) {
        items.filter { it.type == "STAY" }
            .maxByOrNull { it.endMinute - it.startMinute }
            ?.id
    }

    val scrollState = rememberScrollState()
    val baseHourHeight = 90.dp
    val totalHeight = baseHourHeight * 24 * scale

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RAW 24시간 계층 타임라인", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // MapView at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                PlatformMapView(
                    gpsPoints = mockGpsPoints,
                    records = mockRecords,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .transformable(state = transformableState) // Pinch to zoom gesture listener
            ) {
                // Scrollable Timeline
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Hour Scale Axis (Left side)
                    HourScaleAxis(hourHeight = baseHourHeight * scale)

                    // Timeline Blocks Layer (Right side)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(totalHeight)
                            .padding(end = 16.dp)
                    ) {
                        // Draw backgrounds lines
                        HourGridLines(hourHeight = baseHourHeight * scale)

                        // Draw GPS raw data speed-based tick lines
                        GpsTickLines(
                            gpsPoints = mockGpsPoints,
                            totalHeight = totalHeight,
                            scale = scale
                        )

                        // Render STAY / TRANSIT Blocks
                        items.forEach { item ->
                            val topOffset = (item.startMinute / 1440f) * totalHeight.value
                            val blockHeight = ((item.endMinute - item.startMinute) / 1440f) * totalHeight.value

                            if (item.type != "POINT") {
                                var mutableStartMin by remember(item.id) { mutableStateOf(item.startMinute) }
                                var mutableEndMin by remember(item.id) { mutableStateOf(item.endMinute) }
                                
                                val startOffsetTime = (mutableStartMin / 1440f) * totalHeight.value
                                val currentBlockHeight = ((mutableEndMin - mutableStartMin) / 1440f) * totalHeight.value

                                InteractiveBlock(
                                    item = item,
                                    topOffset = startOffsetTime,
                                    height = currentBlockHeight,
                                    isLongestStay = item.id == longestStayId,
                                    onTimeRangeChange = { deltaMin ->
                                        mutableStartMin = (mutableStartMin + deltaMin).coerceIn(0, 1440)
                                        mutableEndMin = (mutableEndMin + deltaMin).coerceIn(mutableStartMin + 5, 1440)
                                    }
                                )
                            } else {
                                // Point markers (Pins)
                                PointPinMarker(
                                    item = item,
                                    topOffset = topOffset
                                )
                            }
                        }
                    }
                }

                // Floating Controls displaying Zoom Info
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "줌 배율: ${(scale * 100).roundToInt()}% (두 손가락 핀치로 조절)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun HourScaleAxis(hourHeight: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier
            .width(55.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 0..24) {
            val hourStr = if (i < 10) "0$i" else "$i"
            Box(
                modifier = Modifier
                    .height(hourHeight),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "$hourStr:00",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun HourGridLines(hourHeight: androidx.compose.ui.unit.Dp) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in 0..24) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                    )
            )
        }
    }
}

@Composable
fun InteractiveBlock(
    item: TimelineItem,
    topOffset: Float,
    height: Float,
    isLongestStay: Boolean = false,
    onTimeRangeChange: (Int) -> Unit
) {
    val depthOffset = (item.depth * 20).dp
    val blockColor = if (item.type == "STAY") {
        if (isLongestStay) {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFB189FF), Color(0xFF6E2BFA)) // STAY Premium Light Purple to Deep Purple
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(Color(0xFF8A55FD), Color(0xFF5F25E5)) // STAY Premium Purple gradient
            )
        }
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)) // TRANSIT Premium Blue gradient
        )
    }

    var dragAccumulator = 0f

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .offset(x = depthOffset, y = topOffset.dp)
            .height(height.coerceAtLeast(40f).dp)
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .graphicsLayer {
                clip = true
            }
            .background(blockColor, RoundedCornerShape(12.dp))
            .border(
                width = if (isLongestStay) 1.5.dp else 0.5.dp,
                color = if (isLongestStay) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f), // Gold border for 1st Stay
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) {
                // Dynamic time drag adjuster gesture
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    dragAccumulator += dragAmount.y
                    if (dragAccumulator > 15f) {
                        onTimeRangeChange(15) // Move range 15 mins forward
                        dragAccumulator = 0f
                    } else if (dragAccumulator < -15f) {
                        onTimeRangeChange(-15) // Move range 15 mins backward
                        dragAccumulator = 0f
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.type == "STAY") Icons.Default.LocationOn else Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (isLongestStay) {
                Text(
                    text = "👑",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Column {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = if (isLongestStay) FontWeight.ExtraBold else FontWeight.Bold,
                    fontSize = if (isLongestStay) 14.sp else 13.sp
                )
                if (height >= 50f) {
                    Text(
                        text = "${formatMinute(item.startMinute)} - ${formatMinute(item.endMinute)}  |  ${item.detail}${if (isLongestStay) " (최장 체류)" else ""}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PointPinMarker(
    item: TimelineItem,
    topOffset: Float
) {
    val depthOffset = (item.depth * 20 + 8).dp
    val pinColor = when (item.category) {
        "PHOTO" -> Color(0xFFEF4444) // Photo Red
        "PURCHASE" -> Color(0xFFF59E0B) // Purchase Gold
        else -> Color(0xFF10B981) // Other Green
    }

    Row(
        modifier = Modifier
            .offset(x = depthOffset, y = topOffset.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(pinColor, CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
        ) {
            Text(
                text = item.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatMinute(totalMinutes: Int): String {
    val hrs = totalMinutes / 60
    val mins = totalMinutes % 60
    val hrsStr = if (hrs < 10) "0$hrs" else "$hrs"
    val minsStr = if (mins < 10) "0$mins" else "$mins"
    return "$hrsStr:$minsStr"
}

@Composable
fun GpsTickLines(
    gpsPoints: List<RawGpsPoint>,
    totalHeight: androidx.compose.ui.unit.Dp,
    scale: Float
) {
    if (gpsPoints.size < 2) return
    val startTimestamp = 1710000000000L
    val startMinute = 480f // 08:00 AM

    Box(modifier = Modifier.fillMaxSize()) {
        for (i in 1 until gpsPoints.size) {
            val pt1 = gpsPoints[i - 1]
            val pt2 = gpsPoints[i]

            // Calculate distance (Haversine formula in KMP-compatible Double math)
            val dLat = (pt2.latitude - pt1.latitude) * kotlin.math.PI / 180.0
            val dLon = (pt2.longitude - pt1.longitude) * kotlin.math.PI / 180.0
            val a = sin(dLat / 2).pow(2.0) + cos(pt1.latitude * kotlin.math.PI / 180.0) * cos(pt2.latitude * kotlin.math.PI / 180.0) * sin(dLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val distMeters = 6371000.0 * c

            val timeSec = (pt2.timestamp - pt1.timestamp) / 1000.0
            val speedKmh = if (timeSec > 0) (distMeters / timeSec) * 3.6 else 0.0

            // Speed-based transparency formula:
            // Under 2km/h -> opacity 0.95 (dense staying / walking slowly)
            // Over 100km/h -> opacity 0.15 (very fast transit)
            // Linear interpolation in between
            val opacity = when {
                speedKmh <= 2.0 -> 0.95f
                speedKmh >= 100.0 -> 0.15f
                else -> 0.95f - ((speedKmh.toFloat() - 2.0f) / 98.0f) * 0.8f
            }

            // Map GPS point timestamp to timeline minuteOfDay (starting from 08:00 AM)
            val elapsedMins = (pt2.timestamp - startTimestamp) / 60000f
            val ptMinute = startMinute + elapsedMins
            val topOffset = (ptMinute / 1440f) * totalHeight.value

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = topOffset.dp)
                    .height(1.dp)
                    .background(Color(0xFFEF4444).copy(alpha = opacity)) // Rose red GPS raw point line
            )
        }
    }
}
