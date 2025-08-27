/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.core.utils.image_vector

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.kmpstarter.core.utils.byte_array.toBase64
import com.kmpstarter.core.utils.image_bitmap.ImageBitmapCompressFormat
import com.kmpstarter.core.utils.image_bitmap.toByteArray
import kotlin.math.roundToInt

/**
 * Converts an ImageVector to a VectorPainter for optimized rendering.
 * 
 * This function creates a cached VectorPainter from an ImageVector, which improves
 * performance by avoiding repeated vector parsing and rendering.
 *
 * @return A VectorPainter that can be used for efficient vector rendering
 * 
 * @example
 * ```kotlin
 * val starIcon = Icons.Default.Star
 * val painter = starIcon.asVectorPainter()
 * Icon(painter = painter, contentDescription = "Star")
 * ```
 */
@Composable
fun ImageVector.asVectorPainter() = rememberVectorPainter(this)

/**
 * Converts a VectorPainter to an ImageBitmap.
 * 
 * This function renders a VectorPainter to a raster ImageBitmap, allowing vector
 * graphics to be converted to bitmap format for further processing or storage.
 *
 * @param density The density to use for rendering (default: 1f)
 * @param layoutDirection The layout direction for rendering (default: Ltr)
 * @param size The size to render the vector at (default: intrinsic size)
 * @param config The ImageBitmap configuration to use (default: Argb8888)
 * @return An ImageBitmap representation of the vector
 * 
 * @example
 * ```kotlin
 * val painter = Icons.Default.Star.asVectorPainter()
 * val bitmap = painter.toImageBitmap(
 *     size = Size(100f, 100f),
 *     density = Density(2f)
 * )
 * ```
 */
fun VectorPainter.toImageBitmap(
    density: Density = Density(density = 1f),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    size: Size = intrinsicSize,
    config: ImageBitmapConfig = ImageBitmapConfig.Argb8888,
): ImageBitmap {
    val image = ImageBitmap(
        width = size.width.roundToInt(),
        height = size.height.roundToInt(),
        config = config
    )
    val canvas = Canvas(image)
    CanvasDrawScope().draw(
        density = density,
        layoutDirection = layoutDirection,
        canvas = canvas,
        size = size
    ) {
        draw(size = this.size)
    }
    return image
}

/**
 * Converts an ImageVector directly to an ImageBitmap.
 * 
 * This function combines the creation of a VectorPainter and its conversion to
 * ImageBitmap in a single step. Useful when you need to convert vector icons
 * to bitmap format for processing or storage.
 *
 * @param density The density to use for rendering (default: 1f)
 * @param layoutDirection The layout direction for rendering (default: Ltr)
 * @param size The size to render the vector at (default: intrinsic size)
 * @param config The ImageBitmap configuration to use (default: Argb8888)
 * @return An ImageBitmap representation of the vector
 * 
 * @example
 * ```kotlin
 * val starIcon = Icons.Default.Star
 * val bitmap = starIcon.toImageBitmap(
 *     size = Size(64f, 64f),
 *     density = Density(2f)
 * )
 * ```
 */
@Composable
fun ImageVector.toImageBitmap(
    density: Density = Density(density = 1f),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    size: Size? = null,
    config: ImageBitmapConfig = ImageBitmapConfig.Argb8888,
): ImageBitmap {
    val imageVector = rememberVectorPainter(
        image = this
    )
    return imageVector.toImageBitmap(
        density = density,
        layoutDirection = layoutDirection,
        size = size ?: imageVector.intrinsicSize,
        config = config
    )
}

/**
 * Converts an ImageVector to a ByteArray using the specified compression format.
 * 
 * This function first converts the ImageVector to an ImageBitmap, then compresses
 * it to a ByteArray. Useful for storing vector icons as compressed data or
 * sending them over network requests.
 *
 * @param commonFormat The compression format to use (required)
 * @param quality Compression quality from 0 to 100 (default: 100 for best quality)
 * @param density The density to use for rendering (default: 1f)
 * @param layoutDirection The layout direction for rendering (default: Ltr)
 * @param size The size to render the vector at (default: intrinsic size)
 * @param config The ImageBitmap configuration to use (default: Argb8888)
 * @param androidFormat Platform-specific format for Android (optional, overrides commonFormat on Android)
 * @param iosFormat Platform-specific format for iOS (optional, overrides commonFormat on iOS)
 * @return ByteArray representation of the vector, or null if conversion fails
 * 
 * @example
 * ```kotlin
 * val starIcon = Icons.Default.Star
 * val bytes = starIcon.toByteArray(
 *     commonFormat = ImageBitmapCompressFormat.COMMON_PNG,
 *     quality = 100,
 *     size = Size(128f, 128f)
 * )
 * ```
 */
@Composable
fun ImageVector.toByteArray(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int = 100,
    density: Density = Density(density = 1f),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    size: Size? = null,
    config: ImageBitmapConfig = ImageBitmapConfig.Argb8888,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): ByteArray? {
    val imageBitmap = this.toImageBitmap(
        density = density,
        layoutDirection = layoutDirection,
        size = size,
        config = config
    )
    return imageBitmap.toByteArray(
        commonFormat = commonFormat,
        quality = quality,
        androidFormat = androidFormat,
        iosFormat = iosFormat
    )
}

/**
 * Converts an ImageVector to a Base64 encoded string.
 * 
 * This function converts an ImageVector to a Base64 string by first rendering it
 * to an ImageBitmap, then compressing and encoding it. Useful for storing vector
 * icons as strings or embedding them in JSON/XML data.
 *
 * @param commonFormat The compression format to use (required)
 * @param quality Compression quality from 0 to 100 (default: 100 for best quality)
 * @param density The density to use for rendering (default: 1f)
 * @param layoutDirection The layout direction for rendering (default: Ltr)
 * @param size The size to render the vector at (default: intrinsic size)
 * @param config The ImageBitmap configuration to use (default: Argb8888)
 * @param androidFormat Platform-specific format for Android (optional, overrides commonFormat on Android)
 * @param iosFormat Platform-specific format for iOS (optional, overrides commonFormat on iOS)
 * @return Base64 encoded string representation of the vector, or null if conversion fails
 * 
 * @example
 * ```kotlin
 * val starIcon = Icons.Default.Star
 * val base64String = starIcon.toBase64String(
 *     commonFormat = ImageBitmapCompressFormat.COMMON_WEBP,
 *     quality = 90,
 *     size = Size(64f, 64f)
 * )
 * // Result: "UklGRiIAAABXRUJQVlA4..."
 * ```
 */
@Composable
fun ImageVector.toBase64String(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int = 100,
    density: Density = Density(density = 1f),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    size: Size? = null,
    config: ImageBitmapConfig = ImageBitmapConfig.Argb8888,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): String? {
    return this.toByteArray(
        commonFormat = commonFormat,
        quality = quality,
        density = density,
        layoutDirection = layoutDirection,
        size = size,
        config = config,
        androidFormat = androidFormat,
        iosFormat = iosFormat
    )?.toBase64()
}

















