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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmpstarter.core.events.controllers.SnackbarController
import com.kmpstarter.ui_utils.side_effects.LaunchOnce
import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_presentation.PurchasesViewModel
import com.kmpstarter.feature_purchases_presentation.PurchasesActions
import com.kmpstarter.feature_purchases_presentation.PurchasesEvents
import com.kmpstarter.feature_purchases_presentation.ui.PurchasesDiscountV1Dialog
import com.kmpstarter.feature_purchases_presentation.ui.screens.paywalls.PaywallV1
import com.kmpstarter.ui_layouts.empty.EmptyStateWithAction
import com.kmpstarter.ui_utils.side_effects.ObserveAsEvents
import com.kmpstarter.utils.logging.Log
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PurchasesScreen(
    viewModel: PurchasesViewModel = koinViewModel(),
    onNavigate: () -> Unit,
) {
    LaunchOnce {
        viewModel.onAction(PurchasesActions.LoadProducts)
    }
    ObserveAsEvents(flow = viewModel.uiEvents) { event ->
        when (event) {
            is PurchasesEvents.ShowMessage -> {
                val message = getString(event.resource)
                SnackbarController.sendAlert(message = message)
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showDiscountDialog by remember {
        mutableStateOf(false)
    }



    LaunchedEffect(state.isPurchased) {
        if (state.isPurchased) {
            onNavigate()
        }
    }



    PurchasesV1ScreenContent(
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
            if (state.isPurchasing)
                return@PurchasesV1ScreenContent

            if (state.discountProduct != null)
                showDiscountDialog = true
            else
                onNavigate()
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
                if (state.isPurchasing)
                    return@PurchasesDiscountV1Dialog

                viewModel.onAction(action = PurchasesActions.UpdateSelectedProduct(product = state.products.lastOrNull()))
                showDiscountDialog = false
                onNavigate()
            },
        )
    }
}

/**
 * Composable that displays the purchases/paywall screen based on the provided metadata version.
 *
 * This function delegates to the appropriate paywall UI (e.g., [PaywallV1]) depending on
 * the `paywallMetadata.version`.
 *
 * @param paywallMetadata Metadata containing version and configuration for the paywall.
 * @param products List of available in-app products to display.
 * @param selectedProduct The currently selected product, if any.
 * @param isProductsLoading True if the product list is currently loading.
 * @param isRestoring True if purchase restoration is in progress.
 * @param isPurchasing True if a purchase is in progress.
 * @param onPurchaseClick Callback invoked when the user taps the purchase button.
 * @param onProductSelected Callback invoked when the user selects a product.
 * @param onGetProductsClick Callback invoked to refresh or fetch products.
 * @param onRestoreClick Callback invoked when the user taps restore purchases.
 * @param onPrivacyClick Callback invoked when the user taps the privacy policy link.
 * @param onTermsClick Callback invoked when the user taps the terms & conditions link.
 * @param onCloseClick Callback invoked when the user closes the paywall screen.
 */
@Composable
private fun PurchasesV1ScreenContent(
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


    when (paywallMetadata.version) {
        1 -> PaywallV1(
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
            onCloseClick = onCloseClick
        )

        /* add more paywalls here*/

        else -> InvalidVersion()
    }

}


@Composable
private fun InvalidVersion(modifier: Modifier = Modifier) {
    EmptyStateWithAction(
        title = "Invalid Version",
        description = "Please enter valid version number in your purchases dashboard",
        buttonText = "----",
        onClick = { },
        modifier = modifier
    )
}