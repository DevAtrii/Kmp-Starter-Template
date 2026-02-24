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

import com.kmpstarter.core.KmpAppInitializer
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
import com.kmpstarter.feature_purchases_data.di.purchasesDataModule
import com.kmpstarter.feature_purchases_domain.di.purchasesDomainModule
import com.kmpstarter.feature_purchases_presentation.di.purchasesPresentationModule
import com.kmpstarter.feature_remote_config_data.di.remoteConfigDataModule
import com.kmpstarter.feature_remote_config_domain.di.remoteConfigDomainModule
import com.kmpstarter.feature_resources.di.resourceModule
import com.kmpstarter.feature_your_feature_data.di.featureYourDataModule
import com.kmpstarter.feature_your_feature_domain.di.featureYourDomainModule
import com.kmpstarter.feature_your_feature_presentation.di.featureYourPresentationModule
import com.kmpstarter.utils.di.utilsModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val starterModules = module {
    includes(
        /*Starter Core Modules*/
        coreDataModule,
        coreDomainModule,
        corePresentationModule,
        utilsModule,
        eventsModule,
        dataStoreModule,
        /*Feature: Database*/
        databaseModule,
        /*Feature: Purchases*/
        purchasesDataModule,
        purchasesDomainModule,
        purchasesPresentationModule,
        /*Feature: Analytics*/
        analyticsDataModule,
        /*Feature: Navigation*/
        navigationModule,
        /*Feature: RemoteConfig*/
        remoteConfigDataModule,
        remoteConfigDomainModule,
        resourceModule,
        /*Feature: Notifications*/
        notificationsCoreModule,
        notificationsLocalModule,
        notificationsPushModule,
    )
}

private val kmpAppInitializerModule = module {
    singleOf(::KmpAppInitializer)
}

internal fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            starterModules,
            kmpAppInitializerModule,
            /* Add Modules Here */
            featureYourDataModule,
            featureYourDomainModule,
            featureYourPresentationModule
        )
    }
}



















