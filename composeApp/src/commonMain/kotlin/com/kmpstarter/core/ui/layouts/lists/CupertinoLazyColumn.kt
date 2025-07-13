package com.kmpstarter.core.ui.layouts.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kmpstarter.core.ui.modifiers.customOverscroll
import com.kmpstarter.core.ui.utils.providers.provideNullAndroidOverscrollConfiguration
import com.kmpstarter.core.utils.platform.PlatformType
import com.kmpstarter.core.utils.platform.platformType
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CupertinoLazyColumn(
    modifier: Modifier = Modifier,
    clippingShape: Shape = RectangleShape,
    listModifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    fillMaxSize: Boolean = true,
    content: LazyListScope.() -> Unit,
) {
    var animatedOverscrollAmount by remember { mutableFloatStateOf(0f) }
    CompositionLocalProvider(
        *provideNullAndroidOverscrollConfiguration(),
    ) {
        Box(
            modifier =
            Modifier
                .then(
                    if (platformType == PlatformType.ANDROID)
                        Modifier
                            .customOverscroll(
                                state,
                                onNewOverscrollAmount = { animatedOverscrollAmount = it },
                            ).clip(clippingShape)
                    else
                        Modifier
                )

                .then(modifier),
        ) {
            LazyColumn(
                modifier =
                Modifier
                    .then(
                        if (fillMaxSize) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (platformType == PlatformType.ANDROID) {
                            val value = try {
                                animatedOverscrollAmount.roundToInt()
                            } catch (e: Exception) {
                                0
                            }
                            Modifier.offset { IntOffset(0, value) }
                        } else
                            Modifier
                    )
                    .then(listModifier),
                state = state,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled,
                content = content,
            )
        }
    }
}
