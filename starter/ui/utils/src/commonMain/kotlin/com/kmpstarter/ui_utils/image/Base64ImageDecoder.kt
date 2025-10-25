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

package com.kmpstarter.ui_utils.image

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.io.encoding.Base64

// todo improve this if error occurs using coroutines
expect fun decodeBase64ToImageBitmap(base64: String): ImageBitmap?


