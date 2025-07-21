package com.kmpstarter.core.ui.components.material_cupertino.dropdown

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.kmpstarter.core.ui.layouts.lists.ScrollableColumn
import com.kmpstarter.theme.Dimens

// Animation constants for iOS-style animations
private const val ExpandedScaleTarget = 1.0f
private const val ClosedScaleTarget = 0.8f
private const val ExpandedAlphaTarget = 1.0f
private const val ClosedAlphaTarget = 0.0f

/**
 * iOS-inspired dropdown menu container.
 * 
 * @param expanded Whether the dropdown is expanded/visible
 * @param onDismissRequest Callback when the dropdown should be dismissed
 * @param modifier Modifier for styling
 * @param backgroundColor Background color of the dropdown
 * @param shape Shape of the dropdown container
 * @param elevation Elevation/shadow of the dropdown
 * @param content Content of the dropdown menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxScope.CupertinoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(12.dp),
    elevation: Dp = 8.dp,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    // Animation state management using MutableTransitionState
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = expanded

    if (expandedState.currentState || expandedState.targetState) {
        val positionProvider = remember {
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    // Position below the anchor with some offset
                    val x = anchorBounds.left
                    val y = anchorBounds.bottom + 4
                    
                    // Ensure popup doesn't go off screen
                    val adjustedX = if (x + popupContentSize.width > windowSize.width) {
                        windowSize.width - popupContentSize.width - 16
                    } else {
                        x
                    }
                    
                    val adjustedY = if (y + popupContentSize.height > windowSize.height) {
                        anchorBounds.top - popupContentSize.height - 4
                    } else {
                        y
                    }
                    
                    return IntOffset(adjustedX, adjustedY)
                }
            }
        }

        Popup(

            onDismissRequest = onDismissRequest,
            popupPositionProvider = positionProvider,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            CupertinoDropdownMenuContent(
                expandedState = expandedState,
                modifier = modifier,
                backgroundColor = backgroundColor,
                shape = shape,
                elevation = elevation,
                content = content
            )
        }
    }
}

/**
 * iOS-inspired dropdown menu content with sophisticated animations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.CupertinoDropdownMenuContent(
    expandedState: MutableTransitionState<Boolean>,
    modifier: Modifier,
    backgroundColor: Color,
    shape: Shape,
    elevation: Dp,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    // Menu open/close animation using updateTransition
    val transition = updateTransition(expandedState, "CupertinoDropDownMenu")
    
    // iOS-style animation specs
    val scaleAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    val alphaAnimationSpec = tween<Float>(durationMillis = 200)
    
    val scale by transition.animateFloat(transitionSpec = { scaleAnimationSpec }) { expanded ->
        if (expanded) ExpandedScaleTarget else ClosedScaleTarget
    }
    
    val alpha by transition.animateFloat(transitionSpec = { alphaAnimationSpec }) { expanded ->
        if (expanded) ExpandedAlphaTarget else ClosedAlphaTarget
    }
    
    // Transform origin for iOS-style scaling
    val transformOrigin = remember { TransformOrigin(0.5f, 0.0f) }
    
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                this.transformOrigin = transformOrigin
            }
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = shape
            )
           ,
        color = backgroundColor,
        shape = shape
    ) {
        ScrollableColumn(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            content()
        }
    }
}

/**
 * iOS-inspired dropdown menu with default Material 3 styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxScope.CupertinoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    CupertinoDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp,
        content = content
    )
}

/**
 * CupertinoDropdownMenu that positions itself relative to an anchor element.
 * 
 * @param expanded Whether the dropdown is expanded/visible
 * @param onDismissRequest Callback when the dropdown should be dismissed
 * @param anchorContent The anchor element that triggers the dropdown
 * @param modifier Modifier for styling
 * @param backgroundColor Background color of the dropdown
 * @param shape Shape of the dropdown container
 * @param elevation Elevation/shadow of the dropdown
 * @param content Content of the dropdown menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxScope.CupertinoDropdownMenuBox(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchorContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(12.dp),
    elevation: Dp = 8.dp,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    var anchorBounds by remember { mutableStateOf(IntRect.Zero) }
    
    Box(modifier = modifier) {
        // Anchor content
        Box(
            modifier = Modifier.onGloballyPositioned { coordinates ->
                anchorBounds = coordinates.boundsInWindow().toIntRect()
            }
        ) {
            anchorContent()
        }
        
        // Dropdown menu
        CupertinoDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier,
            backgroundColor = backgroundColor,
            shape = shape,
            elevation = elevation,
            content = content
        )
    }
}

/**
 * Extension function to convert Rect to IntRect
 */
private fun Rect.toIntRect(): IntRect {
    return IntRect(
        left = left.toInt(),
        top = top.toInt(),
        right = right.toInt(),
        bottom = bottom.toInt()
    )
}

