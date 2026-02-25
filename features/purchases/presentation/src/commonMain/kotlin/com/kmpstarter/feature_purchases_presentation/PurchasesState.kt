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

package com.kmpstarter.feature_purchases_presentation

import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.models.Product


data class PurchasesState(
    val products: List<Product> = listOf(),
    val activeProducts : List<Product> = listOf(),
    val paywallMetadata: PaywallMetadata = PaywallMetadata(),
    val selectedProduct: Product? = null,
    val discountProduct: Product? = null,
    val isLoading: Boolean = false,
    val isRestoring: Boolean = false,
    val isPurchasing: Boolean = false,
    val isPurchased: Boolean = false,
)
