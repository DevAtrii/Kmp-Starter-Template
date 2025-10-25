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

package com.kmpstarter.feature_purchases.presentation.events

import com.revenuecat.purchases.kmp.models.StoreProduct

sealed class PurchaseEvent {
    data object GetProducts: PurchaseEvent()
    data object RestorePurchase: PurchaseEvent()
    data class StartPurchase(val product: StoreProduct) : PurchaseEvent()

}