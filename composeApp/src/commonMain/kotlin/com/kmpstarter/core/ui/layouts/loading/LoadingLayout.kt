package com.kmpstarter.core.ui.layouts.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = if (isSystemInDarkTheme()) {
        Color.Black.copy(
            alpha = 0.5f,
        )
    } else {
        Color.White.copy(
            alpha = 0.5f,
        )
    },
) {
    Box(
        modifier =
        modifier
            .fillMaxSize()
            .then(
                if (backgroundColor == null)
                    Modifier
                else
                    Modifier.background(backgroundColor)
            ).clickable {},
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
