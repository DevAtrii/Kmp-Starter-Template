package com.kmpstarter.core.ui.components.material_cupertino.switchs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

/**
 * iOS-inspired switch component using Material 3 colors.
 * 
 * @param checked Whether the switch is checked/on
 * @param onCheckedChange Callback when the switch state changes
 * @param modifier Modifier for styling
 * @param enabled Whether the switch is enabled
 * @param colors Colors for the switch states
 * @param trackShape Shape of the track
 * @param thumbShape Shape of the thumb
 * @param trackWidth Width of the track
 * @param trackHeight Height of the track
 * @param thumbSize Size of the thumb
 * @param animationDuration Duration of the animation in milliseconds
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    trackShape: Shape = RoundedCornerShape(50),
    thumbShape: Shape = CircleShape,
    trackWidth: Dp = 47.dp,
    trackHeight: Dp = 21.dp,
    thumbSize: Dp = 19.dp,
    animationDuration: Int = 150,
) {
    val density = LocalDensity.current
    
    // Calculate positions
    val trackWidthPx = with(density) { trackWidth.toPx() }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val thumbOffset = (trackWidthPx - thumbSizePx) / 2.1f
    
    // Animated thumb position
    val thumbOffsetX by animateFloatAsState(
        targetValue = if (checked) thumbOffset else -thumbOffset,
        animationSpec = tween(durationMillis = animationDuration),
        label = "thumb_offset"
    )
    
    // Colors based on state
    val trackColor = if (enabled) {
        if (checked) colors.checkedTrackColor else colors.uncheckedTrackColor
    } else {
        if (checked) colors.disabledCheckedTrackColor else colors.disabledUncheckedTrackColor
    }
    
    val thumbColor = if (enabled) {
        if (checked) colors.checkedThumbColor else colors.uncheckedThumbColor
    } else {
        if (checked) colors.disabledCheckedThumbColor else colors.disabledUncheckedThumbColor
    }
    
    val borderColor = if (enabled) {
        if (checked) colors.checkedBorderColor else colors.uncheckedBorderColor
    } else {
        if (checked) colors.disabledCheckedBorderColor else colors.disabledUncheckedBorderColor
    }
    

    
    Box(
        modifier = modifier
            .size(trackWidth, trackHeight)
            .clickable(
                enabled = enabled && onCheckedChange != null
            ) {
                onCheckedChange?.invoke(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        // Track
        Box(
            modifier = Modifier
                .size(trackWidth, trackHeight)
                .clip(trackShape)
                .background(trackColor)
                .then(
                    if (borderColor != Color.Unspecified) {
                        Modifier.background(
                            color = borderColor,
                            shape = trackShape
                        )
                    } else {
                        Modifier
                    }
                )
        )
        
        // Thumb
        Box(
            modifier = Modifier
                .size(thumbSize)
                .offset(
                    x = with(density) { thumbOffsetX.toDp() }
                )
                .shadow(
                    elevation = 1.dp,
                    shape = thumbShape,
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
                .clip(thumbShape)
                .background(thumbColor)

        )
    }
}

/**
 * iOS-inspired switch with default Material 3 colors and iOS-style dimensions.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    CupertinoSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            checkedBorderColor = Color.Unspecified,
            checkedIconColor = Color.White,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFE5E5EA),
            uncheckedBorderColor = Color.Unspecified,
            uncheckedIconColor = Color.White,
            disabledCheckedThumbColor = Color.White.copy(alpha = 0.6f),
            disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            disabledCheckedBorderColor = Color.Unspecified,
            disabledCheckedIconColor = Color.White.copy(alpha = 0.6f),
            disabledUncheckedThumbColor = Color.White.copy(alpha = 0.6f),
            disabledUncheckedTrackColor = Color(0xFFE5E5EA).copy(alpha = 0.6f),
            disabledUncheckedBorderColor = Color.Unspecified,
            disabledUncheckedIconColor = Color.White.copy(alpha = 0.6f)
        )
    )
}

@Composable
@Preview( )
fun CupertinoSwitchPreview() {
    androidx.compose.material3.MaterialTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // Enabled switches
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text("Off:")
                CupertinoSwitch(
                    checked = false,
                    onCheckedChange = {}
                )
            }
            
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text("On:")
                CupertinoSwitch(
                    checked = true,
                    onCheckedChange = {}
                )
            }
            
            // Disabled switches
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text("Disabled Off:")
                CupertinoSwitch(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false
                )
            }
            
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text("Disabled On:")
                CupertinoSwitch(
                    checked = true,
                    onCheckedChange = {},
                    enabled = false
                )
            }
        }
    }
}