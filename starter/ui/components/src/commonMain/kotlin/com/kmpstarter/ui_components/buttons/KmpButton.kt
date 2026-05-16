package com.kmpstarter.ui_components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmpstarter.ui_utils.theme.Dimens

@Composable
fun KmpButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight),
    ) {
        Text(text = label)
    }

}