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

package com.kmpstarter.ui_utils.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Deprecated(
    "This method is deprecated. it's not working as expected. use Color.fromHex instead",
    ReplaceWith("Color.fromHex(hexString, defaultColor)")
)
fun parseHexColor(hexColor: String, fallback: Color = Color.Gray): Color {
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

        else -> fallback
    }
}

@Deprecated(
    "This method is deprecated. it's not working as expected. use Color.fromHex instead",
    ReplaceWith("Color.fromHex(hexString, defaultColor)")
)
fun parseHexColorOrNull(hexColor: String): Color? {
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

        else -> null
    }
}

@Deprecated(
    "This method is deprecated. it's not working as expected. use Color.fromHex instead",
    ReplaceWith("Color.fromHex(hexString, defaultColor)")
)
fun parseHexColor(hexColor: String?, fallback: Color = Color.Gray): Color? {
    if (hexColor == null)
        return null
    return parseHexColor(hexColor, fallback)
}


@Deprecated(
    "This method is deprecated. it's not working as expected. use Color.fromHex instead",
    ReplaceWith("Color.fromHex(hexString, defaultColor)")
)
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

fun Color.Companion.fromHex(
    hexString: String?,
    defaultColor: Color = Color.Unspecified,
): Color? {
    if (hexString == null)
        return null
    return fromHex(hexString, defaultColor)
}

fun Color.Companion.fromHex(
    hexString: String,
    defaultColor: Color = Color.Unspecified,
): Color {
    val cleaned = hexString
        .trim()
        .removePrefix("#")
        .uppercase()

    // Supported formats:
    // RGB  (RRGGBB)
    // ARGB (AARRGGBB)
    // RGBA (RRGGBBAA)
    return try {
        when (cleaned.length) {
            6 -> { // RRGGBB
                val r = cleaned.substring(0, 2).toInt(16)
                val g = cleaned.substring(2, 4).toInt(16)
                val b = cleaned.substring(4, 6).toInt(16)
                Color(r, g, b)
            }

            8 -> {
                // Could be AARRGGBB or RRGGBBAA — detect by assuming alpha first
                val a = cleaned.substring(0, 2).toInt(16)
                val r = cleaned.substring(2, 4).toInt(16)
                val g = cleaned.substring(4, 6).toInt(16)
                val b = cleaned.substring(6, 8).toInt(16)

                // If alpha looks like it's at the end, swap
                if (a == 0xFF && cleaned.endsWith("FF")) {
                    val rr = cleaned.substring(0, 2).toInt(16)
                    val gg = cleaned.substring(2, 4).toInt(16)
                    val bb = cleaned.substring(4, 6).toInt(16)
                    val aa = cleaned.substring(6, 8).toInt(16)
                    Color(rr, gg, bb, aa)
                } else {
                    Color(r, g, b, a)
                }
            }

            else -> defaultColor
        }
    } catch (_: Throwable) {
        defaultColor
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

fun Color.toHexStringWithDefault(defaultColor: String = "#FFFFFF"): String {
    return toHexString() ?: defaultColor
}

fun Color.complimentaryColor(): Color {
    return Color(
        red = 1 - red,
        green = 1 - green,
        blue = 1 - blue,
        alpha = 1f
    )
}

fun Color.brightness(): Float {
    val max = maxOf(red, green, blue)
    return max
}


val Color.brightness: Float get() = brightness()



















