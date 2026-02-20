/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Author: Athar Gul
 *  * GitHub: https://github.com/DevAtrii
 *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *
 *  * All rights reserved.
 *
 *
 */

package com.kmpstarter.feature_notifications_core

import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration

expect fun createAlarmeePlatformConfiguration(): AlarmeePlatformConfiguration


object AppNotifications {
    const val CHANNEL_MAIN_ID = "kmp_starter_main_channel"
    const val CHANNEL_MAIN_NAME = "KMP Starter"
}