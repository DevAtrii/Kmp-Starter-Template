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

import com.tweener.alarmee.AlarmeeService
import com.tweener.alarmee.createAlarmeeService
import org.koin.dsl.module

val notificationsCoreModule = module {
    single {
        val alarmeeService = createAlarmeeService()
        alarmeeService.initialize(
            platformConfiguration = createAlarmeePlatformConfiguration(),
        )

        alarmeeService
    }
    single {
        val alarmeeService = get<AlarmeeService>()
        alarmeeService.local
    }
}