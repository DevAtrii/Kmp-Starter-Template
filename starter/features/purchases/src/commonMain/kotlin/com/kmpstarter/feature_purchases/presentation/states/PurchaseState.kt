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

package com.kmpstarter.feature_purchases.presentation.states

import com.revenuecat.purchases.kmp.models.StoreProduct

data class PurchaseState(
    val isLoading: Boolean = false,

    // products
    val products: List<StoreProduct> = listOf(),
    val selectedProduct: StoreProduct? = null,
    val currentSubscribedProduct: StoreProduct? = null,
    val discountProduct: StoreProduct? = null,
    val discountPercentage: Double = 0.0,

    val isPurchaseComplete: Boolean = false,

    // errors
    val restoreError: String = "",
    val error: String = "",
)
