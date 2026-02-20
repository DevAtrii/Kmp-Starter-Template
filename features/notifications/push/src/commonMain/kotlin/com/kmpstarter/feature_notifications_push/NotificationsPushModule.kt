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

package com.kmpstarter.feature_notifications_push

import com.kmpstarter.feature_notifications_core.createAlarmeePlatformConfiguration
import com.tweener.alarmee.MobileAlarmeeService
import com.tweener.alarmee.createAlarmeeMobileService
import dev.gitlive.firebase.Firebase
import org.koin.dsl.module

val notificationsPushModule = module {
    single {
        val alarmeeService = createAlarmeeMobileService()
        alarmeeService.initialize(
            platformConfiguration = createAlarmeePlatformConfiguration(),
            firebase = Firebase
        )

        alarmeeService
    }

    single {
        val alarmeeService = get<MobileAlarmeeService>()
        alarmeeService.push
    }
}