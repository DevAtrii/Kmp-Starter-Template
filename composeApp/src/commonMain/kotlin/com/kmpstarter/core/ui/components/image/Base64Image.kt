package com.kmpstarter.core.ui.components.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import com.kmpstarter.core.ui.utils.image.decodeBase64ToImageBitmap

@Composable
fun Base64Image(
    base64String: String,
    modifier: Modifier = Modifier,
    contentDescription: String?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    placeHolder: @Composable () -> Unit = {},
) {
    val base64 = remember(base64String) {
        if (base64String.startsWith("data:image/png;base64,")) {
            base64String.replace("data:image/png;base64,", "")
        } else {
            base64String
        }
    }
    val bitmap = remember(base64) {
        decodeBase64ToImageBitmap(
            base64 = base64
        )
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            alignment = alignment,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
        )
        return
    }
    // show placeholder if bitmap is null
    placeHolder()

}
