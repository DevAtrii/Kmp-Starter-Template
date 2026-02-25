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

import com.kmpstarter.feature_purchases_domain.models.Product


sealed class PurchasesActions {
    data class UpdateSelectedProduct(val product: Product?) : PurchasesActions()
   data  object StartPurchase : PurchasesActions()
    data object LoadProducts : PurchasesActions()
    data object RestorePurchases : PurchasesActions()
    data object OnPrivacyPolicyClick : PurchasesActions()
    data object OnTermsOfUseClick : PurchasesActions()

}