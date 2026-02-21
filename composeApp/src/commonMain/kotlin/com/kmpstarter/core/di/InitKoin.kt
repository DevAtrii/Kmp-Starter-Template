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
import com.kmpstarter.feature_analytics_data.di.analyticsDataModule
import com.kmpstarter.feature_core_data.di.coreDataModule
import com.kmpstarter.feature_core_domain.di.coreDomainModule
import com.kmpstarter.feature_core_presentation.di.corePresentationModule
import com.kmpstarter.feature_database.di.databaseModule
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
            /*Feature: Core*/
            coreDataModule,
            coreDomainModule,
            corePresentationModule,
            utilsModule,
            databaseModule,
            eventsModule,
            dataStoreModule,
            purchasesModule,
            /*Feature: Analytics*/
            analyticsDataModule,
            navigationModule,
            remoteConfigDataModule,
            remoteConfigDomainModule,
            resourceModule,
            /*Feature: Notifications*/
            notificationsCoreModule,
            notificationsLocalModule,
            notificationsPushModule,
            /*Todo add modules here*/
        )
    }
}