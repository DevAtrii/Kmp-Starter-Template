/*
 *
 *  *
 *  *  * Copyright (c) 2026
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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kmpstarter.ui_utils.byte_string.cleanBase64Web
import com.kmpstarter.utils.exception.EnumException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.ByteString
import java.io.ByteArrayOutputStream
import kotlin.coroutines.cancellation.CancellationException

private fun ImageBitmapCompressFormat.toAndroidCompressFormat() = when (this) {
    ImageBitmapCompressFormat.COMMON_WEBP -> Bitmap.CompressFormat.WEBP
    ImageBitmapCompressFormat.COMMON_JPEG -> Bitmap.CompressFormat.JPEG
    ImageBitmapCompressFormat.COMMON_PNG -> Bitmap.CompressFormat.PNG
    ImageBitmapCompressFormat.ANDROID_WEBP_LOSSLESS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSLESS
    else
        Bitmap.CompressFormat.WEBP

    ImageBitmapCompressFormat.ANDROID_WEBP_LOSSY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSY
    else
        Bitmap.CompressFormat.WEBP

    else -> Bitmap.CompressFormat.PNG
}

actual fun ImageBitmap.toByteArray(
    commonFormat: ImageBitmapCompressFormat,
    quality: Int,
    androidFormat: ImageBitmapCompressFormat?,
    iosFormat: ImageBitmapCompressFormat?,
): ByteArray? {

    val format =
        androidFormat?.toAndroidCompressFormat() ?: commonFormat.toAndroidCompressFormat()
    val androidBitmap = this@toByteArray.asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    androidBitmap.compress(/* format = */ format, /* quality = */
        quality, /* stream = */
        stream
    )
    return stream.toByteArray()

}


actual suspend fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray): ImageBitmap? {
    return withContext(Dispatchers.Default) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        bitmap?.asImageBitmap()
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
        val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        ImageBitmap.fromByteArray(imageBytes) ?: throw EnumException(Base64Error.BYTES_NULL)
    }
}



















