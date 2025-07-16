package com.kmpstarter.core.purchases.presentation.events

import com.revenuecat.purchases.kmp.models.StoreProduct

sealed class PurchaseEvent {
    data object GetProducts: PurchaseEvent()
    data object RestorePurchase: PurchaseEvent()
    data class StartPurchase(val product: StoreProduct) : PurchaseEvent()

}