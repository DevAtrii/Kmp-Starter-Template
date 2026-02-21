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

import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository

/**
 * Fetches a discounted or promotional product if available.
 */
class GetDiscountProductLogic(
    private val repository: PurchasesRepository,
) {

    suspend operator fun invoke(): Result<Product> = repository.getDiscountProduct()

}