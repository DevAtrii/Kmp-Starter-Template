package com.kmpstarter.core.ui.layouts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout

val LocalScreenSize = staticCompositionLocalOf { Offset(-1f, -1f) }

@Composable
internal fun MeasurableLayout(
    modifier: Modifier = Modifier,
    onSizeChanged: (Offset) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Layout(
        modifier = modifier.fillMaxSize(),
        content = content,
        measurePolicy = { measurables, constraints ->
            // Use the max width and height from the constraints
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val offset = Offset(width.toFloat(), height.toFloat())
            LocalScreenSize.provides(offset)
            onSizeChanged(offset)

            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }

            layout(width, height) {
                var yPosition = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = yPosition)
                    yPosition += placeable.height
                }
            }
        }
    )
}