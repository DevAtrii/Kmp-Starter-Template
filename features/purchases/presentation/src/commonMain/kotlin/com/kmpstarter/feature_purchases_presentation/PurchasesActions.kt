package com.kmpstarter.feature_purchases_presentation

import com.kmpstarter.feature_purchases_domain.models.Product


sealed class PurchasesActions {
    data class UpdateSelectedProduct(val product: Product?) : PurchasesActions()
   data  object StartPurchase : PurchasesActions()
    data object LoadProducts : PurchasesActions()
    data object RestorePurchases : PurchasesActions()
    data object OnPrivacyPolicyClick : PurchasesActions()
    data object OnTermsOfUseClick : PurchasesActions()

}