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

package com.kmpstarter.core.di

import com.kmpstarter.core.datastore.di.dataStoreModule
import com.kmpstarter.core.events.di.eventsModule
import com.kmpstarter.core.navigation.navigationModule
import com.kmpstarter.core_db.di.databaseModule
import com.kmpstarter.feature_analytics.di.analyticsModule
import com.kmpstarter.feature_notifications_core.notificationsCoreModule
import com.kmpstarter.feature_notifications_local.notificationsLocalModule
import com.kmpstarter.feature_notifications_push.notificationsPushModule
import com.kmpstarter.feature_purchases.di.purchasesModule
import com.kmpstarter.feature_remote_config_data.di.remoteConfigDataModule
import com.kmpstarter.feature_remote_config_domain.di.remoteConfigDomainModule
import com.kmpstarter.feature_resources.di.resourceModule
import com.kmpstarter.utils.di.utilsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

internal fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            utilsModule,
            databaseModule,
            eventsModule,
            dataStoreModule,
            purchasesModule,
            analyticsModule,
            navigationModule,
            remoteConfigDataModule,
            remoteConfigDomainModule,
            resourceModule,
            /*Notification Modules*/
            notificationsCoreModule,
            notificationsLocalModule,
            notificationsPushModule,
            /*Todo add modules here*/
        )
    }
}