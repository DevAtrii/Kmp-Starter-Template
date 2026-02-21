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

data class PurchasesLogics(
    val getDiscountProduct: GetDiscountProductLogic,
    val getPaywallMetadata: GetPaywallMetadataLogic,
    val getProducts: GetProductsLogic,
    val restorePurchases: RestorePurchasesLogic,
    val signInToPurchase: SignInToPurchaseLogic,
    val startPurchase: StartPurchaseLogic,
    val getCurrentPurchaseStatus: GetCurrentPurchaseStatusLogic,
)
