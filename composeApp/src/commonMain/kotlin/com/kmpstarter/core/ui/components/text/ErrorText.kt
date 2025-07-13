package com.kmpstarter.core.ui.components.text

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ErrorText(error: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = error,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.error
    )
}