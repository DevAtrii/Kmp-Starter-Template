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

package com.kmpstarter.feature_core_domain

import com.kmpstarter.feature_analytics_domain.AppEvent

sealed class AppEvents(
    event: String,
    properties: Map<String, Any>? = null,
) : AppEvent(event, properties) {

    constructor(
        event: String,
    ) : this(
        event = event,
        properties = null,
    )

    constructor(
        event: String,
        pair: Pair<String, Any>? = null,
    ) : this(
        event = event,
        properties = if (pair != null) mapOf(pair) else mapOf(),
    )

    data object DummyEvent : AppEvents(
        event = "dummy_event",
    )

    // onboarding
    data class TrackTrafficSource(
        val source: String,
    ) : AppEvents(
        event = "onboarding_traffic_source",
        pair = "traffic_source" to source,
    )

    // purchases
    data class OnPurchaseSuccess(
        val productId: String,
    ) : AppEvents(
        event = "purchase_success",
        pair = "product_id" to productId,
    )

    data class OnPurchaseFailure(
        val productId: String,
        val error: String,
    ) : AppEvents(
        event = "purchase_failure",
        properties = mapOf(
            "product_id" to productId,
            "error" to error,
        ),
    )

    data class OnPurchaseProductsLoadFailure(
        val error: String,
    ) : AppEvents(
        event = "purchase_products_failure",
        pair = "error" to error,
    )

    data class OnPurchaseRestoreFailure(
        val error: String,
    ) : AppEvents(
        event = "purchase_restore_failure",
        pair = "error" to error,
    )
}