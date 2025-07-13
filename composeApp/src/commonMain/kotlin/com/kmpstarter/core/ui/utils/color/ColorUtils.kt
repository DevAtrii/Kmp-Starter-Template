package com.kmpstarter.core.ui.utils.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Parses a hex color string to Compose Color.
 * Avoids using platform-specific android.graphics.Color in commonMain.
 */
fun parseHexColor(hexColor: String): Color {
    val cleanHex = hexColor.removePrefix("#")
    return when (cleanHex.length) {
        6 -> {
            val colorInt = cleanHex.toLong(16)
            Color(
                red = ((colorInt shr 16) and 0xFF) / 255f,
                green = ((colorInt shr 8) and 0xFF) / 255f,
                blue = (colorInt and 0xFF) / 255f,
                alpha = 1f
            )
        }

        8 -> {
            val colorInt = cleanHex.toLong(16)
            Color(
                red = ((colorInt shr 24) and 0xFF) / 255f,
                green = ((colorInt shr 16) and 0xFF) / 255f,
                blue = ((colorInt shr 8) and 0xFF) / 255f,
                alpha = (colorInt and 0xFF) / 255f
            )
        }

        else -> Color.Gray // Fallback color
    }
}


fun parseToColor(hexString: String?, defaultColor: Color = Color.Unspecified): Color {
    hexString ?: return defaultColor
    try {
        val hexString = if (!hexString.startsWith("#")) "#$hexString" else hexString
        require(hexString.startsWith("#") && (hexString.length == 7 || hexString.length == 9)) {
            "Invalid hex color format. Expected format is #RRGGBB or #AARRGGBB"
        }

        val colorInt = hexString.substring(1).toLong(16)
        val hasAlpha = hexString.length == 9

        return if (hasAlpha) {
            Color(colorInt.toInt())
        } else {
            Color(colorInt.toInt() or 0xFF000000.toInt())
        }
    } catch (e: Exception) {
        return defaultColor
    }
}

fun Color.toHexString(): String? {
    if (this == Color.Unspecified)
        return null
    val hex = this.toArgb().toHexString(
        format = HexFormat.UpperCase
    )
    return hex
}
