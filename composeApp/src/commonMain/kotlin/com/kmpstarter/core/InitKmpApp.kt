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

package com.kmpstarter.core

import com.kmpstarter.core.di.initKoin
import com.kmpstarter.core.platform.platform
import com.kmpstarter.feature_purchases_data.initRevenueCat
import com.kmpstarter.feature_remote_config_domain.RemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform
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
    // 1. Core SDK Configuration
    // Sets up the fundamental API keys used by the underlying library modules.
    KmpStarter.initApp(
        revenueCatApiKey = AppConstants.REVENUE_CAT_API_KEY,
        mixPanelApiKey = AppConstants.MIXPANEL_API_TOKEN
    )

    // 2. Dependency Injection Setup
    // Starts Koin. This must happen before any 'inject()' or 'get()' calls.
    initKoin(config = koinConfig)


    // 3. Feature-Specific Initialization (YOU CAN INIT OTHER STUFF HERE)
    // Configures platform-specific billing and remote toggle logic.
    initRevenueCat()
    initRemoteConfig()
}



private fun initRemoteConfig() {
    CoroutineScope(Dispatchers.IO).launch {
        RemoteConfig.init(
            minimumFetchInterval = if (platform.debug) 1.seconds else 1.hours
        )
    }
}



















