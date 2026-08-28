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

package com.kmpstarter.feature_purchases_domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseTransaction(
    val product: Product,
    val amount: Double,
    val amountMicros:Long,
    val transactionId: String,
    val currency: String,
)
