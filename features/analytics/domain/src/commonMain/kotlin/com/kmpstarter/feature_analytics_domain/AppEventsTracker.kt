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

package com.kmpstarter.feature_analytics_domain

/** contains all events related to current app*/
interface AppEventsTracker {

    // events names
    companion object {
        const val KEY_ONBOARDING_TRAFFIC_SOURCE = "traffic_source"

        const val KEY_PURCHASE_SUCCESS = "purchase_success"
        const val KEY_PURCHASE_FAILURE = "purchase_failure"
        const val KEY_PURCHASE_RESTORE_FAILURE = "purchase_restore_failure"
        const val KEY_PURCHASE_PRODUCTS_FAILURE = "purchase_products_failure"

    }

    // onboarding
    suspend fun trackTrafficSource(source: String)

    // purchases
    suspend fun trackPurchaseSuccess(productId: String)
    suspend fun trackPurchaseFailure(productId: String, error: String)
    suspend fun trackPurchaseProductsFailure(error: String)
    suspend fun trackPurchaseRestoreFailure(error: String)

}



















