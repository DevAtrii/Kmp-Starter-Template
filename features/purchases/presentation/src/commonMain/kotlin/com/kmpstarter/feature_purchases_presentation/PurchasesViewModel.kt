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

import androidx.lifecycle.viewModelScope
import com.kmpstarter.core.KmpStarter
import com.kmpstarter.feature_analytics_domain.AppEventsTracker
import com.kmpstarter.feature_purchases_domain.logics.PurchasesLogics
import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.ui_utils.viewmodels.MviViewModel
import com.kmpstarter.utils.intents.IntentUtils
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PurchasesViewModel(
    private val purchasesLogics: PurchasesLogics,
    private val eventsTracker: AppEventsTracker,
    private val intentUtils: IntentUtils,
) : MviViewModel<PurchasesState, PurchasesActions, PurchasesEvents>() {

    companion object Companion {
        private const val TAG = "PurchaseViewModel"
    }


    override val initialState: PurchasesState
        get() = PurchasesState()


    // jobs
    private var startPurchaseJob: Job? = null
    private var restorePurchasesJob: Job? = null
    private var loadProductsJob: Job? = null
    private var getPaywallMetadataJob: Job? = null
    private var loadDiscountProductJob: Job? = null


    private fun getPaywallMetadata() {
        getPaywallMetadataJob?.cancel()
        getPaywallMetadataJob = viewModelScope.launch {
            purchasesLogics
                .getPaywallMetadata()
                .onSuccess { paywallMetadata: PaywallMetadata ->
                    Log.i(TAG, "getPaywallMetadata: meta data loaded")
                    _state.update {
                        it.copy(
                            paywallMetadata = paywallMetadata
                        )
                    }
                }
                .onFailure { err ->
                    Log.e(
                        TAG,
                        "getPaywallMetadata: unable to load paywall meta data ${err.message}"
                    )
                }
        }
    }


    override fun onAction(action: PurchasesActions) {
        when (action) {
            PurchasesActions.LoadProducts -> loadProducts()
            PurchasesActions.RestorePurchases -> restorePurchases()
            PurchasesActions.StartPurchase -> startPurchase()
            is PurchasesActions.UpdateSelectedProduct -> _state.update {
                it.copy(
                    selectedProduct = action.product
                )
            }

            PurchasesActions.OnPrivacyPolicyClick -> viewModelScope.launch {
                intentUtils.openUrl(
                    url = KmpStarter.PRIVACY_POLICY
                )
            }

            PurchasesActions.OnTermsOfUseClick -> viewModelScope.launch {
                intentUtils.openUrl(
                    url = KmpStarter.TERMS_OF_USE
                )
            }
        }
    }

    private fun restorePurchases() {
        restorePurchasesJob?.cancel()
        restorePurchasesJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isRestoring = true
                )
            }
            purchasesLogics
                .restorePurchases()
                .onSuccess { activeProducts ->
                    _state.update {
                        it.copy(
                            activeProducts = activeProducts,
                            isRestoring = false
                        )
                    }
                }.onFailure { err ->
                    _state.update {
                        it.copy(
                            isRestoring = false
                        )
                    }
                    val message = err.getPurchaseExceptionMessage()
                    emitEvent(PurchasesEvents.ShowMessage(message))
                    eventsTracker.trackPurchaseRestoreFailure(
                        error = err.message ?: "--"
                    )
                }
        }
    }

    private fun startPurchase() {
        startPurchaseJob?.cancel()
        startPurchaseJob = viewModelScope.launch {
            val selectedProduct = _state.value.selectedProduct ?: return@launch
            _state.update {
                it.copy(
                    isPurchasing = true
                )
            }
            purchasesLogics.startPurchase(productId = selectedProduct.id)
                .onSuccess { product ->
                    _state.update {
                        it.copy(
                            activeProducts = it.activeProducts + product,
                            isPurchased = true,
                            isPurchasing = false
                        )
                    }
                    eventsTracker.trackPurchaseSuccess(selectedProduct.id)
                }.onFailure { err ->
                    _state.update {
                        it.copy(
                            isPurchased = false,
                            isPurchasing = false
                        )
                    }
                    val message = err.getPurchaseExceptionMessage()
                    emitEvent(PurchasesEvents.ShowMessage(message))
                    eventsTracker.trackPurchaseFailure(
                        productId = selectedProduct.id,
                        error = err.message ?: "--"
                    )
                }
        }
    }

    private fun loadDiscountProduct() {
        loadDiscountProductJob?.cancel()
        loadDiscountProductJob = viewModelScope.launch {
            purchasesLogics.getDiscountProduct()
                .onSuccess { product ->
                    Log.i(TAG, "loadDiscountProduct: discountProduct loaded: $product")
                    _state.update {
                        it.copy(
                            discountProduct = product.formatValues()
                        )
                    }
                }.onFailure { err ->
                    Log.e(
                        TAG,
                        "loadDiscountProduct: failed to load discount product: ${err.message}"
                    )
                }
        }
    }

    private fun loadProducts() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            purchasesLogics.getProducts()
                .onSuccess { products ->
                    loadDiscountProduct()
                    val formattedProducts = products.map {
                        it.formatValues()
                    }
                    Log.i(TAG, "loadProducts: formatted Products: $formattedProducts")
                    _state.update {
                        it.copy(
                            products = formattedProducts,
                            isLoading = false,
                            selectedProduct = formattedProducts.lastOrNull()
                        )
                    }
                    getPaywallMetadata()
                }.onFailure { err ->
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    val message = err.getPurchaseExceptionMessage()
                    emitEvent(PurchasesEvents.ShowMessage(message))
                    eventsTracker.trackPurchaseProductsFailure(
                        error = err.message ?: "--"
                    )
                }
        }
    }


}