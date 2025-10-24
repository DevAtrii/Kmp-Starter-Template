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

package com.kmpstarter.core.notifications.alarmee

import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration

expect fun createAlarmeePlatformConfiguration(): AlarmeePlatformConfiguration


object AppNotifications {

    const val CHANNEL_MAIN = "kmp_starter_main_channel"


}