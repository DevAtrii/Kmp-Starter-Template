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

package com.kmpstarter.core.di

import com.kmpstarter.core.datastore.di.dataStoreModule
import com.kmpstarter.core.db.di.databaseModule
import com.kmpstarter.core.events.di.eventsModule
import com.kmpstarter.core.ktor.di.ktorModule
import com.kmpstarter.core.notifications.di.notificationsModule
import com.kmpstarter.core.purchases.di.purchasesModule
import com.kmpstarter.core.utils.di.utilsModule
import org.koin.dsl.module

val coreModule = module {
    includes(
        utilsModule,
        databaseModule,
        eventsModule,
        dataStoreModule,
        purchasesModule,
        ktorModule,
        notificationsModule
    )
}