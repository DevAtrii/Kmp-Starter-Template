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

package com.kmpstarter.feature_notifications_core

import android.app.NotificationManager
import com.kmpstarter.feature_resources.R
import com.tweener.alarmee.channel.AlarmeeNotificationChannel
import com.tweener.alarmee.configuration.AlarmeeAndroidPlatformConfiguration
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration

actual fun createAlarmeePlatformConfiguration(): AlarmeePlatformConfiguration {
    return AlarmeeAndroidPlatformConfiguration(
        notificationIconResId = R.drawable.ic_notification,
        notificationChannels = listOf(
            AlarmeeNotificationChannel(
                id =  AppNotifications.CHANNEL_MAIN_ID,
                name = AppNotifications.CHANNEL_MAIN_NAME,
                importance = NotificationManager.IMPORTANCE_HIGH
            ),
        )
    )
}