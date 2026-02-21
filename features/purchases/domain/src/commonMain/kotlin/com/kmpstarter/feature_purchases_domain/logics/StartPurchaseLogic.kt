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

package com.kmpstarter.feature_purchases_domain.logics

import com.kmpstarter.feature_purchases_domain.models.ProductId
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository

/**
 * Initiates a new purchase for the specified product.
 */
class StartPurchaseLogic(
    private val repository: PurchasesRepository,
) {

    suspend operator fun invoke(productId: ProductId): Result<Unit> = repository.startPurchase(productId)

}