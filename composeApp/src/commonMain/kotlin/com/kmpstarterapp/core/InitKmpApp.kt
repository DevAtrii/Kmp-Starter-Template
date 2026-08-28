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

package com.kmpstarterapp.core

import com.kmpstarter.core.platform.platform
import com.kmpstarter.feature_analytics_data.MixPanelAnalyticsScope
import com.kmpstarter.feature_analytics_data.getProvider
import com.kmpstarter.feature_analytics_data.initMixPanel
import com.kmpstarter.feature_analytics_domain.initAnalytics
import com.kmpstarter.feature_purchases_data.initRevenueCat
import com.kmpstarter.feature_remote_config_domain.RemoteConfig
import com.kmpstarter.utils.logging.StarterLogger
import com.kmpstarterapp.core.di.initKoin
import com.revenuecat.purchases.kmp.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds


/**
 * The global initialization entry point for the KMP application.
 * * This function MUST be called at the earliest possible stage of the platform
 * lifecycle (e.g., [android.app.Application.onCreate] on Android or
 * [UIApplicationDelegate] on iOS).
 *
 * ### Execution Order & Dependencies:
 * 1. **KmpStarter**: Bootstraps core keys (RevenueCat, Mixpanel). Must be first.
 * 2. **Koin**: Initializes the Dependency Injection graph.
 * 3. **AppInitializer**: Triggers background tasks (Fetching products, etc.).
 * 4. **Services**: Finalizes specific configurations for Billing and Remote Config.
 *
 * @param koinConfig Optional platform-specific Koin configuration (e.g., providing
 * the Android Context or iOS-specific modules).
 */
fun initKmpApp(
    koinConfig: KoinAppDeclaration? = null,
) {
    // 1. Dependency Injection Setup
    // Starts Koin. This must happen before any 'inject()' or 'get()' calls.
    initKoin(config = koinConfig)

    // 2. Feature-Specific Initialization (YOU CAN INIT OTHER STUFF HERE)
    // Configures platform-specific billing and remote toggle logic.
    initLogger()
    initRevenueCat(
        apiKey = AppConstants.REVENUE_CAT_API_KEY,
    ) {
        logLevel = if (platform.debug) LogLevel.DEBUG else LogLevel.ERROR
    }
    initAppAnalytics()
    initRemoteConfig()
}

private fun initAppAnalytics() {
    /*initialize all providers before calling initAnalytics{}*/
    initMixPanel(
        apiKey = AppConstants.MIXPANEL_API_TOKEN
    ) {
        logging = platform.debug
    }

    /*registering all providers*/
    initAnalytics {
        providers(
            MixPanelAnalyticsScope.getProvider(),
            /*Dummy Analytics provider to showcase multiple providers feature*/
            // StarterAnalyticsProvider.create(),
        )
    }
}

private fun initLogger() {
    StarterLogger.setLogLevel(
        if (platform.debug) StarterLogger.LogLevel.VERBOSE else StarterLogger.LogLevel.OFF
    )
}


private fun initRemoteConfig() {
    CoroutineScope(Dispatchers.IO).launch {
        RemoteConfig.init(
            minimumFetchInterval = if (platform.debug) 1.seconds else 1.hours
        )
    }
}



















