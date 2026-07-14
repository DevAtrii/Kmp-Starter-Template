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

package com.kmpstarter.ui_utils.compose

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Preview(name = "Phone", device = Devices.PHONE, showSystemUi = true, showBackground = true)
@Preview(name = "Foldable", device = Devices.FOLDABLE, showSystemUi = true, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showSystemUi = true, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showSystemUi = true, showBackground = true)
annotation class AllDevicePreviews