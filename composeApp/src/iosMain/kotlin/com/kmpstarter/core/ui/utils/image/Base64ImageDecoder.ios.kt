package com.kmpstarter.core.ui.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import okio.ByteString.Companion.decodeBase64
import org.jetbrains.skia.Image

actual fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
    return try {
        // Validate input
        if (base64.isBlank()) {
            return null
        }

        // Clean and validate base64 string
        val cleanBase64 = cleanBase64String(base64)
        if (cleanBase64.isBlank()) {
            return null
        }

        // Decode base64
        val decodedBytes = cleanBase64.decodeBase64()?.toByteArray()
            ?: return null

        // Validate decoded data
        if (decodedBytes.size < 4) {
            return null // Too small to be a valid image
        }

        // Create image
        val skiaImage = Image.makeFromEncoded(decodedBytes)
        skiaImage.toComposeImageBitmap()
    } catch (e: Exception) {
        println("Base64 decode error: ${e.message}")
        null
    }
}

private fun cleanBase64String(input: String): String {
    return input.trim()
        .let { str ->
            when {
                str.startsWith("data:image/png;base64,") -> str.substringAfter("data:image/png;base64,")
                str.startsWith("data:image/jpeg;base64,") -> str.substringAfter("data:image/jpeg;base64,")
                str.startsWith("data:image/jpg;base64,") -> str.substringAfter("data:image/jpg;base64,")
                str.startsWith("data:image/gif;base64,") -> str.substringAfter("data:image/gif;base64,")
                str.startsWith("data:image/webp;base64,") -> str.substringAfter("data:image/webp;base64,")
                else -> str
            }
        }
        .replace(" ", "")
        .replace("\n", "")
        .replace("\r", "")
}





















