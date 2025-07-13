package com.kmpstarter.core.ui.utils.image

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}