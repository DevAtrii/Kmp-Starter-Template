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

package com.kmpstarter.feature_navigation

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject


class StarterNavigator : BaseNavigator() {
    companion object {
        @Composable
        fun getCurrent(): StarterNavigator {
            return koinInject()
        }
    }
}