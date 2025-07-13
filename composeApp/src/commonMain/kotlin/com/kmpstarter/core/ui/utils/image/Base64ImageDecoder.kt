package com.kmpstarter.core.ui.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.io.encoding.Base64

// todo improve this if error occurs using coroutines
expect fun decodeBase64ToImageBitmap(base64: String): ImageBitmap?


fun ByteArray.toBase64(): String {
    return Base64.encode(
        source = this,
    )
}
