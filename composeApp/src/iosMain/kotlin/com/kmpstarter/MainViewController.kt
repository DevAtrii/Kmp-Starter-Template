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

package com.kmpstarter

import androidx.compose.ui.window.ComposeUIViewController
import com.kmpstarter.core.initKmpApp


fun mainViewController() = ComposeUIViewController(
    configure = {
        initKmpApp()
    }
) {
    App()
}