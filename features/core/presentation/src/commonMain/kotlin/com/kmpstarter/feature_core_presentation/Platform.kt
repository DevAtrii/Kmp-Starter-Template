package com.kmpstarter.feature_core_presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

expect fun platform(): String

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview( showBackground = true)
@Composable
private fun HelloPlatform(){
    Text(
        modifier = Modifier.padding(16.dp),
        text = "Hello ${platform()}",
        style = MaterialTheme.typography.headlineLargeEmphasized,
        fontStyle = FontStyle.Italic,
        fontFamily = FontFamily.Monospace,
    )
}