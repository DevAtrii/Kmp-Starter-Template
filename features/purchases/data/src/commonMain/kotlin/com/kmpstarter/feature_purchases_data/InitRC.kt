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

package com.kmpstarter.feature_purchases_data

import com.kmpstarter.core.KmpStarter
import com.kmpstarter.core.platform.platform
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration


fun initRevenueCat() {
    Purchases.logLevel = if (platform.debug) LogLevel.DEBUG else LogLevel.ERROR
    Purchases.configure(
        PurchasesConfiguration.Builder(
            apiKey = KmpStarter.REVENUE_CAT_API_KEY,
        ).build(),
    )
}
