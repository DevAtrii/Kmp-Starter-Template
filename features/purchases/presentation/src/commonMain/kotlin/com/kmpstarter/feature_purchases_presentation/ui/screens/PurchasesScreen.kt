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

package com.kmpstarter.feature_purchases_presentation.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.models.ProductId
import com.kmpstarter.feature_purchases_presentation.PurchasesActions
import com.kmpstarter.feature_purchases_presentation.PurchasesEvents
import com.kmpstarter.feature_purchases_presentation.PurchasesViewModel
import com.kmpstarter.feature_purchases_presentation.ui.PurchasesDiscountV1Dialog
import com.kmpstarter.feature_purchases_presentation.ui.screens.paywalls.PaywallV1
import com.kmpstarter.feature_purchases_presentation.ui.screens.paywalls.PaywallV1UiContent
import com.kmpstarter.ui_utils.side_effects.LaunchOnce
import com.kmpstarter.ui_utils.side_effects.ObserveAsEvents
import com.kmpstarter.utils.logging.Log
import org.koin.compose.viewmodel.koinViewModel

sealed class Paywalls {

    data class V1(
        val ui: PaywallV1UiContent,
    ) : Paywalls()
}

@Composable
fun PurchasesScreen(
    paywall: Paywalls,
    viewModel: PurchasesViewModel = koinViewModel(),
    onNavigate: () -> Unit,
    onProductsLoadFailure: (Throwable) -> Unit = {},
    onPurchaseFailure: (Throwable) -> Unit = {},
    onRestoreFailure: (Throwable) -> Unit = {},
    onPurchaseSuccess: (ProductId) -> Unit = {},
) {
    LaunchOnce {
        viewModel.onAction(PurchasesActions.LoadProducts)
    }
    ObserveAsEvents(flow = viewModel.uiEvents) { event ->
        when (event) {
            is PurchasesEvents.OnProductsLoadFailure -> onProductsLoadFailure(event.exception)
            is PurchasesEvents.OnPurchaseFailure -> onPurchaseFailure(event.exception)
            is PurchasesEvents.OnPurchaseSuccess -> onPurchaseSuccess(event.productId)
            is PurchasesEvents.OnRestoreFailure -> onRestoreFailure(event.exception)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    var showDiscountDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(state.isPurchased) {
        if (state.isPurchased) {
            onNavigate()
        }
    }

    PurchasesPaywallContent(
        paywall = paywall,
        paywallMetadata = state.paywallMetadata,
        selectedProduct = state.selectedProduct,
        products = state.products,
        isProductsLoading = state.isLoading,
        isRestoring = state.isRestoring,
        isPurchasing = state.isPurchasing,
        onProductSelected = { product ->
            viewModel.onAction(PurchasesActions.UpdateSelectedProduct(product = product))
        },
        onGetProductsClick = {
            viewModel.onAction(PurchasesActions.LoadProducts)
        },
        onRestoreClick = {
            viewModel.onAction(PurchasesActions.RestorePurchases)
        },
        onPrivacyClick = {
            viewModel.onAction(PurchasesActions.OnPrivacyPolicyClick)
        },
        onTermsClick = {
            viewModel.onAction(PurchasesActions.OnTermsOfUseClick)
        },
        onPurchaseClick = {
            viewModel.onAction(PurchasesActions.StartPurchase)
        },
        onCloseClick = {
            if (state.isPurchasing) return@PurchasesPaywallContent

            if (state.discountProduct != null) {
                showDiscountDialog = true
            } else {
                onNavigate()
            }
        },
    )

    if (showDiscountDialog) {
        val product = state.discountProduct ?: return
        Log.i("ScreenPurchase", "PurchasesV1Screen: $product")
        PurchasesDiscountV1Dialog(
            isLoading = state.isPurchasing,
            price = product.price,
            discountPercentage = "${product.discountPercentage}%",
            onPurchaseClick = {
                viewModel.onAction(action = PurchasesActions.UpdateSelectedProduct(product = product))
                viewModel.onAction(action = PurchasesActions.StartPurchase)
            },
            onDismiss = {
                if (state.isPurchasing) return@PurchasesDiscountV1Dialog

                viewModel.onAction(
                    action = PurchasesActions.UpdateSelectedProduct(product = state.products.lastOrNull()),
                )
                showDiscountDialog = false
                onNavigate()
            },
        )
    }
}

@Composable
private fun PurchasesPaywallContent(
    paywall: Paywalls,
    paywallMetadata: PaywallMetadata,
    products: List<Product>,
    selectedProduct: Product? = null,
    isProductsLoading: Boolean,
    isRestoring: Boolean,
    isPurchasing: Boolean,
    onPurchaseClick: () -> Unit,
    onProductSelected: (Product) -> Unit,
    onGetProductsClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    when (paywall) {
        is Paywalls.V1 ->
            PaywallV1(
                ui = paywall.ui,
                paywallMetadata = paywallMetadata,
                products = products,
                selectedProduct = selectedProduct,
                isProductsLoading = isProductsLoading,
                isRestoring = isRestoring,
                isPurchasing = isPurchasing,
                onPurchaseClick = onPurchaseClick,
                onProductSelected = onProductSelected,
                onGetProductsClick = onGetProductsClick,
                onRestoreClick = onRestoreClick,
                onPrivacyClick = onPrivacyClick,
                onTermsClick = onTermsClick,
                onCloseClick = onCloseClick,
            )

        /* add more paywalls here */
    }
}
