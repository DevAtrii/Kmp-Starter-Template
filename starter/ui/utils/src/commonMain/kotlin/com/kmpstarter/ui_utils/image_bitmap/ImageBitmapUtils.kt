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

package com.kmpstarter.ui_utils.image_bitmap

import androidx.compose.ui.graphics.ImageBitmap
import com.kmpstarter.utils.byte_array.toBase64
import com.kmpstarter.utils.exception.EnumException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

// enums
enum class Base64Error {
    BLANK,
    BYTES_NULL,
    BYTES_SMALL,
}


/**
 * Supported image compression formats for ImageBitmap conversion.
 *
 * This enum provides platform-specific and common image formats for converting
 * ImageBitmap objects to byte arrays or base64 strings.
 */
enum class ImageBitmapCompressFormat {
    // Common formats supported across all platforms
    /** WebP format - efficient compression, good quality */
    COMMON_WEBP,

    /** JPEG format - lossy compression, smaller file size */
    COMMON_JPEG,

    /** PNG format - lossless compression, supports transparency */
    COMMON_PNG,

    // Android-specific formats
    /** Android WebP lossless - maintains original quality */
    ANDROID_WEBP_LOSSLESS,

    /** Android WebP lossy - smaller file size with quality trade-off */
    ANDROID_WEBP_LOSSY,

    // iOS-specific formats
    /** iOS Bitmap format */
    IOS_BMP,

    /** iOS GIF format - supports animation */
    IOS_GIF,

    /** iOS ICO format - icon format */
    IOS_ICO,

    /** iOS WBMP format - wireless bitmap */
    IOS_WBMP,

    /** iOS PKM format - ETC1 texture compression */
    IOS_PKM,

    /** iOS KTX format - Khronos texture format */
    IOS_KTX,

    /** iOS ASTC format - adaptive scalable texture compression */
    IOS_ASTC,

    /** iOS DNG format - digital negative */
    IOS_DNG,

    /** iOS HEIF format - high efficiency image format */
    IOS_HEIF;
}


// exceptions


// functions

/**
 * Converts an ImageBitmap to a ByteArray using the specified compression format.
 *
 * This function provides platform-specific image compression capabilities.
 * Use common formats (COMMON_*) for cross-platform compatibility, or platform-specific
 * formats for optimized results on each platform.
 *
 * @param commonFormat The compression format to use (required)
 * @param quality Compression quality from 0 to 100 (default: 100 for best quality)
 * @param androidFormat Platform-specific format for Android (optional, overrides commonFormat on Android)
 * @param iosFormat Platform-specific format for iOS (optional, overrides commonFormat on iOS)
 * @return ByteArray representation of the image, or null if conversion fails
 *
 * @example
 * ```kotlin
 * val imageBitmap: ImageBitmap = // ... your image
 * val bytes = imageBitmap.toByteArray(
 *     commonFormat = ImageBitmapCompressFormat.COMMON_JPEG,
 *     quality = 80
 * )
 * ```
 */
expect fun ImageBitmap.toByteArray(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int = 100,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): ByteArray?

suspend fun ImageBitmap.compress(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int = 50,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): ImageBitmap? {
    return withContext(Dispatchers.Default) {
        val imageBitmap = this@compress
        val byteArray = imageBitmap.toByteArray(
            commonFormat = commonFormat,
            quality = quality,
            androidFormat = androidFormat,
            iosFormat = iosFormat,
        )
        if (byteArray != null)
            ImageBitmap.fromByteArray(byteArray)
        else null
    }
}

suspend fun ImageBitmap.Companion.compressByteArray(
    bytes: ByteArray,
    commonFormat: ImageBitmapCompressFormat,
    quality: Int = 50,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): ByteArray? {
    return withContext(Dispatchers.Default) {
        val imageBitmap = fromByteArray(bytes) ?: return@withContext null
        val byteArray = imageBitmap.toByteArray(
            commonFormat = commonFormat,
            quality = quality,
            androidFormat = androidFormat,
            iosFormat = iosFormat,
        )
        byteArray
    }
}

suspend fun ImageBitmap.Companion.compressByteArray(
    bytes: ByteArray,
    commonFormat: ImageBitmapCompressFormat,
    targetMaxKb: Int = 100,
    minQuality: Int = 10,
    step: Int = 5,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): ByteArray? = withContext(Dispatchers.Default) {
    val imageBitmap = fromByteArray(bytes) ?: return@withContext null

    var quality = 100
    var compressed = imageBitmap.toByteArray(
        commonFormat = commonFormat,
        quality = quality,
        androidFormat = androidFormat,
        iosFormat = iosFormat,
    ) ?: return@withContext null

    // Reduce quality until size fits or we hit minQuality
    while ((compressed.size / 1024) > targetMaxKb && quality > minQuality) {
        quality -= step
        val newCompressed = imageBitmap.toByteArray(
            commonFormat = commonFormat,
            quality = quality,
            androidFormat = androidFormat,
            iosFormat = iosFormat,
        )
        if (newCompressed == null) break
        compressed = newCompressed
    }

    compressed
}

/**
 * Converts an ImageBitmap to a Base64 encoded string.
 *
 * This function first converts the ImageBitmap to a ByteArray using the specified
 * compression format, then encodes it as a Base64 string. Useful for storing
 * images as strings or sending them over network requests.
 *
 * @param commonFormat The compression format to use (required)
 * @param quality Compression quality from 0 to 100 (default: 100 for best quality)
 * @param androidFormat Platform-specific format for Android (optional, overrides commonFormat on Android)
 * @param iosFormat Platform-specific format for iOS (optional, overrides commonFormat on iOS)
 * @return Base64 encoded string representation of the image, or null if conversion fails
 *
 * @example
 * ```kotlin
 * val imageBitmap: ImageBitmap = // ... your image
 * val base64String = imageBitmap.toBase64String(
 *     commonFormat = ImageBitmapCompressFormat.COMMON_WEBP,
 *     quality = 90
 * )
 * // Result: "data:image/webp;base64,UklGRiIAAABXRUJQVlA4..."
 * ```
 */
suspend fun ImageBitmap.toBase64String(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int = 100,
    androidFormat: ImageBitmapCompressFormat? = null,
    iosFormat: ImageBitmapCompressFormat? = null,
): String? {
    return toByteArray(
        commonFormat = commonFormat,
        quality = quality,
        androidFormat = androidFormat,
        iosFormat = iosFormat
    )?.toBase64()
}


expect suspend fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray): ImageBitmap?

@Throws(EnumException::class, CancellationException::class)
expect suspend fun ImageBitmap.Companion.fromBase64String(base64String: String): ImageBitmap?
















