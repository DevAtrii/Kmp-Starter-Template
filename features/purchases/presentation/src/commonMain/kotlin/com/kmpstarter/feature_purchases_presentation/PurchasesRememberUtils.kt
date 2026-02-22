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

/**
 * Synchronously observes active purchases as Compose State.
 * Automatically triggers UI recomposition when the user's subscription status changes.
 */
@Composable
fun rememberActiveProducts(): State<List<Product>> {
    val _logics: PurchasesLogics = koinInject()
    return _logics.getCurrentPurchaseStatus().collectAsState(initial = listOf())
}

/**
 * Returns `true` if the user has any active subscription or entitlement.
 * Use this for quick conditional UI checks (e.g., hiding ads or showing premium badges).
 */
@Composable
fun rememberIsProUser(): Boolean {
    return rememberActiveProducts().value.isNotEmpty()
}