/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

package com.kmpstarter.ui_utils.popups.bottom_sheets

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/** Util for hiding the bottom sheet properly. **/
@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.hideProperly(
    scope: CoroutineScope,
    onHidden: () -> Unit,
) {
    scope.launch {
        hide()
    }.invokeOnCompletion {
        if (!isVisible) {
            onHidden()
        }
    }
}

/** Util for hiding the bottom sheet properly. **/
@OptIn(ExperimentalMaterial3Api::class)
context(scope: CoroutineScope)
fun SheetState.hideProperly(
    onHidden: () -> Unit,
) {
    scope.launch {
        hide()
    }.invokeOnCompletion {
        if (!isVisible) {
            onHidden()
        }
    }
}

/** only set bool var to false in onDismiss */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BaseBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,

    /*Sheet Default props*/
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,

    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    var isFullyExpanded by rememberSaveable {
        mutableStateOf(false)
    }


    val topStart by animateDpAsState(
        targetValue = if (isFullyExpanded) 0.dp else 28.dp
    )

    val topEnd by animateDpAsState(
        targetValue = if (isFullyExpanded) 0.dp else 28.dp
    )


    val shape = remember(topStart, topEnd) {
        RoundedCornerShape(
            topStart = topStart,
            topEnd = topEnd,
        )
    }


    LaunchedEffect(sheetState) {

        snapshotFlow {
            try {
                sheetState.requireOffset()
            } catch (_: Exception) {
                1f
            }
        }
            .collect { offset ->
                isFullyExpanded = offset <= 0
            }
    }


    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            sheetState.hideProperly(
                scope = scope,
                onHidden = onDismiss
            )
        },
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        contentWindowInsets = contentWindowInsets,
        properties = properties,
        content = content
    )
}



















