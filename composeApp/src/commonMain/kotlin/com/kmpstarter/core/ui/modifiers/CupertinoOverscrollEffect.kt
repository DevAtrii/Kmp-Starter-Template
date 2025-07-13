package com.kmpstarter.core.ui.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.sign

// Overrides to handle fetching the orientation from LazyListState or PagerState
@Composable
fun Modifier.customOverscroll(
    listState: LazyListState,
    onNewOverscrollAmount: (Float) -> Unit,
    animationSpec: SpringSpec<Float> = spring(stiffness = Spring.StiffnessLow),
) = customOverscroll(
    orientation = remember { listState.layoutInfo.orientation },
    onNewOverscrollAmount = onNewOverscrollAmount,
    animationSpec = animationSpec,
)

@Composable
fun Modifier.customOverscroll(
    pagerState: PagerState,
    onNewOverscrollAmount: (Float) -> Unit,
    animationSpec: SpringSpec<Float> = spring(stiffness = Spring.StiffnessLow),
) = customOverscroll(
    orientation = remember { pagerState.layoutInfo.orientation },
    onNewOverscrollAmount = onNewOverscrollAmount,
    animationSpec = animationSpec,
)

@Composable
fun Modifier.customOverscroll(
    orientation: Orientation,
    onNewOverscrollAmount: (Float) -> Unit,
    animationSpec: SpringSpec<Float> = spring(stiffness = Spring.StiffnessLow),
): Modifier {
    val overscrollAmountAnimatable = remember { Animatable(0f) }

    var length by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        snapshotFlow { overscrollAmountAnimatable.value }.collect {
            onNewOverscrollAmount(
                // Change the multiplier to increase or decrease the strength of the value
                CustomEasing.transform(it / (length * 1.5f)) * length,
            )
        }
    }

    val scope = rememberCoroutineScope()

    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                private fun calculateOverscroll(available: Offset): Float {
                    val previous = overscrollAmountAnimatable.value
                    val newValue =
                        previous +
                            when (orientation) {
                                Orientation.Vertical -> available.y
                                Orientation.Horizontal -> available.x
                            }
                    return when {
                        previous > 0 -> newValue.coerceAtLeast(0f)
                        previous < 0 -> newValue.coerceAtMost(0f)
                        else -> newValue
                    }
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    scope.launch {
                        overscrollAmountAnimatable.snapTo(targetValue = calculateOverscroll(available))
                    }
                    return Offset.Zero
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity,
                ): Velocity {
                    val availableVelocity =
                        when (orientation) {
                            Orientation.Vertical -> available.y
                            Orientation.Horizontal -> available.x
                        }
                    overscrollAmountAnimatable.animateTo(
                        targetValue = 0f,
                        initialVelocity = availableVelocity,
                        animationSpec = animationSpec,
                    )
                    return available
                }

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (overscrollAmountAnimatable.value != 0f && source != NestedScrollSource.SideEffect) {
                        scope.launch {
                            overscrollAmountAnimatable.snapTo(calculateOverscroll(available))
                        }
                        return available
                    }

                    return super.onPreScroll(available, source)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    val availableVelocity =
                        when (orientation) {
                            Orientation.Vertical -> available.y
                            Orientation.Horizontal -> available.x
                        }

                    if (overscrollAmountAnimatable.value != 0f && availableVelocity != 0f) {
                        var consumedVelocity = availableVelocity
                        val previousSign = overscrollAmountAnimatable.value.sign
                        val predictedEndValue =
                            exponentialDecay<Float>().calculateTargetValue(
                                initialValue = overscrollAmountAnimatable.value,
                                initialVelocity = availableVelocity,
                            )
                        if (predictedEndValue.sign == previousSign) {
                            overscrollAmountAnimatable.animateTo(
                                targetValue = 0f,
                                initialVelocity = availableVelocity,
                                animationSpec = animationSpec,
                            )
                        } else {
                            try {
                                overscrollAmountAnimatable.animateDecay(
                                    initialVelocity = availableVelocity,
                                    animationSpec = exponentialDecay(),
                                ) {
                                    if (value.sign != previousSign) {
                                        consumedVelocity -= velocity
                                        scope.launch {
                                            overscrollAmountAnimatable.snapTo(0f)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // e will probably always be a MutationInterruptedException
                                // You could throw e if it isn't just to be absolutely sure
                            }
                        }

                        return when (orientation) {
                            Orientation.Vertical -> Velocity(0f, consumedVelocity)
                            Orientation.Horizontal -> Velocity(consumedVelocity, 0f)
                        }
                    }

                    return super.onPreFling(available)
                }
            }
        }
    return this
        .onSizeChanged {
            length =
                when (orientation) {
                    Orientation.Vertical -> it.height.toFloat()
                    Orientation.Horizontal -> it.width.toFloat()
                }
        }
        .nestedScroll(nestedScrollConnection)
}

val CustomEasing: Easing = CubicBezierEasing(0.5f, 0.5f, 1.0f, 0.25f)
