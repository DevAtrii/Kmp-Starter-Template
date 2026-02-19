@file:OptIn(ExperimentalLayoutApi::class)

package com.kmpstarter.ui_components.image


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class CoilImageType {
    NORMAL,
    SVG
}


@Composable
private fun ImageBuilder(
    modifier: Modifier = Modifier,
    url: String,
    headers: Map<String, String> = emptyMap(),
    imageType: CoilImageType = CoilImageType.NORMAL,
    colorFilter: ColorFilter? = null,
    crossFade: Boolean = true,
    placeHolder: DrawableResource? = null,
    placeholderModifier: Modifier = Modifier,
    showProgressBar: Boolean = true,
    onLoading: (@Composable () -> Unit)? = null,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val imageRequest: ImageRequest = ImageRequest.Builder(LocalPlatformContext.current)
        .httpHeaders(
            headers = NetworkHeaders.Builder().apply {
                headers.forEach {
                    this.add(
                        key = it.key,
                        value = it.value
                    )
                }
            }.build()
        )
        .diskCacheKey(key = url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .data(url)
        .crossfade(crossFade)
        .run {
            if (imageType == CoilImageType.SVG) {
                decoderFactory(SvgDecoder.Factory())
            }
            build()
        }




    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        contentScale = contentScale,
        colorFilter = colorFilter,
        modifier = modifier,
        loading = {
            if (onLoading != null) {
                onLoading.invoke()
                return@SubcomposeAsyncImage
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (placeHolder == null && showProgressBar) {
                    CircularProgressIndicator(
                        modifier = placeholderModifier
                    )
                } else {
                    if (placeHolder != null)
                        Image(
                            modifier = placeholderModifier,
                            painter = painterResource(placeHolder),
                            contentDescription = "image placeholder"
                        )
                }
            }
        },
    )
}


@Composable
fun CoilImage(
    modifier: Modifier = Modifier,
    url: String,
    headers: Map<String, String> = emptyMap(),
    colorFilter: ColorFilter? = null,
    imageType: CoilImageType = CoilImageType.NORMAL,
    crossFade: Boolean = true,
    placeHolder: DrawableResource? = null,
    showProgressBar: Boolean = true,
    onLoading: (@Composable () -> Unit)? = null,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    ImageBuilder(
        modifier = modifier,
        url = url,
        headers = headers,
        colorFilter = colorFilter,
        imageType = imageType,
        crossFade = crossFade,
        placeHolder = placeHolder,
        showProgressBar = showProgressBar,
        contentDescription = contentDescription,
        contentScale = contentScale,
        onLoading = onLoading
    )

}
















