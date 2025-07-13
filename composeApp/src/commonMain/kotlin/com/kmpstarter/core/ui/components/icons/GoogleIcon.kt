package com.kmpstarter.core.ui.components.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kmpstarter.composeapp.generated.resources.Res
import kmpstarter.composeapp.generated.resources.ic_google
import org.jetbrains.compose.resources.painterResource


@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(
            resource = Res.drawable.ic_google
        ),
        contentDescription = "Google Icon",
        modifier = modifier,
    )
}
