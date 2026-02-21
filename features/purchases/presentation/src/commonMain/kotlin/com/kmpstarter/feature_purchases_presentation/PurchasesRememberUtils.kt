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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.kmpstarter.feature_purchases_domain.logics.PurchasesLogics
import com.kmpstarter.feature_purchases_domain.models.Product
import org.koin.compose.koinInject

@Composable
fun rememberPurchasedProducts(): State<List<Product>> {
    val _logics: PurchasesLogics = koinInject()
    return _logics.getCurrentPurchaseStatus().collectAsState(initial = listOf())
}


@Composable
fun rememberIsProUser(): Boolean {
    return rememberPurchasedProducts().value.isNotEmpty()
}

