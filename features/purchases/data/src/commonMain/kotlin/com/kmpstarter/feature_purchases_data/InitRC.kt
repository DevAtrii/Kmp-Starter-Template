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

import com.kmpstarter.core.platform.platform
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration


fun initRevenueCat(
    apiKey: String,
    block: Purchases.Companion.() -> Unit = {},
) {
    Purchases.configure(
        PurchasesConfiguration.Builder(
            apiKey = apiKey,
        ).build(),
    )
    Purchases.apply(block)
}
