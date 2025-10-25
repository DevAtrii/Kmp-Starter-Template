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
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.kmpstarter.ui_utils.byte_string.cleanBase64Web
import com.kmpstarter.utils.exception.EnumException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import kotlin.coroutines.cancellation.CancellationException

private fun ImageBitmapCompressFormat.toNativeCompressFormat() = when (this) {
    ImageBitmapCompressFormat.COMMON_WEBP -> EncodedImageFormat.WEBP
    ImageBitmapCompressFormat.COMMON_JPEG -> EncodedImageFormat.JPEG
    ImageBitmapCompressFormat.COMMON_PNG -> EncodedImageFormat.PNG
    ImageBitmapCompressFormat.IOS_BMP -> EncodedImageFormat.BMP
    ImageBitmapCompressFormat.IOS_GIF -> EncodedImageFormat.GIF
    ImageBitmapCompressFormat.IOS_ICO -> EncodedImageFormat.ICO
    ImageBitmapCompressFormat.IOS_WBMP -> EncodedImageFormat.WBMP
    ImageBitmapCompressFormat.IOS_PKM -> EncodedImageFormat.PKM
    ImageBitmapCompressFormat.IOS_KTX -> EncodedImageFormat.KTX
    ImageBitmapCompressFormat.IOS_ASTC -> EncodedImageFormat.ASTC
    ImageBitmapCompressFormat.IOS_DNG -> EncodedImageFormat.DNG
    ImageBitmapCompressFormat.IOS_HEIF -> EncodedImageFormat.HEIF
    else -> EncodedImageFormat.PNG
}


actual fun ImageBitmap.toByteArray(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int,
    androidFormat: ImageBitmapCompressFormat?,
    iosFormat: ImageBitmapCompressFormat?,
): ByteArray? {
    val format = iosFormat?.toNativeCompressFormat() ?: commonFormat.toNativeCompressFormat()
    val skiaBitmap = this@toByteArray.asSkiaBitmap()
    val skiaImage = Image.makeFromBitmap(skiaBitmap)
    return skiaImage.encodeToData(format = format, quality = quality)?.bytes
}


actual suspend fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray): ImageBitmap? {
    return withContext(Dispatchers.Default) {
        val skiaImage = Image.makeFromEncoded(bytes = bytes)
        skiaImage.toComposeImageBitmap()
    }
}

@Throws(exceptionClasses = [EnumException::class, CancellationException::class])
actual suspend fun ImageBitmap.Companion.fromBase64String(base64String: String): ImageBitmap? {
    return withContext(Dispatchers.Default) {
        if (base64String.isBlank()) {
            throw EnumException(Base64Error.BLANK)
        }

        val cleanBase64 = ByteString.cleanBase64Web(input = base64String)
        if (cleanBase64.isBlank()) {
            throw EnumException(Base64Error.BLANK)
        }

        val decodedBytes = cleanBase64.decodeBase64()?.toByteArray()
            ?: throw EnumException(Base64Error.BYTES_NULL)

        if (decodedBytes.size < 4) {
            throw EnumException(Base64Error.BYTES_SMALL)
        }

        ImageBitmap.fromByteArray(bytes = decodedBytes)
    }
}