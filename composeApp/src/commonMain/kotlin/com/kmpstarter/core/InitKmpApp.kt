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
import com.kmpstarter.feature_purchases.initRevenueCat
import com.kmpstarter.feature_remote_config_domain.RemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

fun initKmpApp(
    koinConfig: KoinAppDeclaration? = null,
) {
    KmpStarter.initApp(
        revenueCatApiKey = AppConstants.REVENUE_CAT_API_KEY,
        mixPanelApiKey = AppConstants.MIXPANEL_API_TOKEN
    )
    initKoin(config = koinConfig)
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



















