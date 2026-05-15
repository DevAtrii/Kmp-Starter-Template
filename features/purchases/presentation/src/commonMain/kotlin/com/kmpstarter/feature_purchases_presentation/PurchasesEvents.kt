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

import com.kmpstarter.feature_purchases_domain.models.ProductId

sealed class PurchasesEvents {

    data class OnProductsLoadFailure(val exception: Throwable) : PurchasesEvents()
    data class OnPurchaseFailure(val exception: Throwable) : PurchasesEvents()
    data class OnRestoreFailure(val exception: Throwable) : PurchasesEvents()
    data class OnPurchaseSuccess(val productId: ProductId) : PurchasesEvents()
}