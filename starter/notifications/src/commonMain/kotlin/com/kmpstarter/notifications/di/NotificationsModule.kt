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

package com.kmpstarter.notifications.di

import com.kmpstarter.core.notifications.alarmee.KmpNotificationsManager
import com.kmpstarter.notifications.alarmee.KmpNotificationsManagerImpl
import com.kmpstarter.notifications.alarmee.createAlarmeePlatformConfiguration
import com.tweener.alarmee.AlarmeeService
import com.tweener.alarmee.createAlarmeeService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notificationsModule = module {
    single {
        val alarmeeService = createAlarmeeService()
        alarmeeService.initialize(platformConfiguration = createAlarmeePlatformConfiguration())

        alarmeeService
    }
    single {
        val alarmeeService = get<AlarmeeService>()
        alarmeeService.local
    }

    singleOf(::KmpNotificationsManagerImpl).bind<KmpNotificationsManager>()
}