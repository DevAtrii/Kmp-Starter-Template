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

package com.kmpstarter.ui_utils.byte_string

import okio.ByteString

fun ByteString.Companion.cleanBase64Web(input: String): String {
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