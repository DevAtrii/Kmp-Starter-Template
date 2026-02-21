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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmpstarter.feature_analytics_domain.AppEventsTracker
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
/*

class PurchaseViewModel(
    private val purchasesRepository: PurchasesRepository,
    private val eventsTracker: AppEventsTracker,
) : ViewModel() {

    companion object {
        private const val TAG = "PurchaseViewModel"
    }

    private val _state = MutableStateFlow(PurchaseState())
    val state = _state.onStart {
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val _uiEvents = Channel<CommonUiEvents>(Channel.Factory.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    // jobs
    private var startPurchaseJob: Job? = null
    private var loadProductsJob: Job? = null
    private var getPaywallMetadataJob: Job? = null
    private var loadDiscountProductJob: Job? = null


    init {
        onAction(PurchaseActions.LoadProducts)
    }

    private fun getPaywallMetadata() {
        getPaywallMetadataJob?.cancel()
        getPaywallMetadataJob = viewModelScope.launch {
            purchasesRepository
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


    fun onAction(action: PurchaseActions) {
        when (action) {
            PurchaseActions.LoadProducts -> loadProducts()
            PurchaseActions.StartPurchase -> startPurchase()
            is PurchaseActions.UpdateSelectedProduct -> _state.update {
                it.copy(
                    selectedProduct = action.product
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
            purchasesRepository.startPurchase(productId = selectedProduct.id)
                .onSuccess {
                    _state.update {
                        it.copy(
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
                    _uiEvents.showMessage(message)
                    eventsTracker.trackPurchaseFailure(
                        productId = selectedProduct.id,
                        error = err.message ?: message
                    )
                }
        }
    }

    private fun loadDiscountProduct() {
        loadDiscountProductJob?.cancel()
        loadDiscountProductJob = viewModelScope.launch {
            purchasesRepository.getDiscountProduct()
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
            purchasesRepository.getProducts()
                .onSuccess { products ->
                    loadDiscountProduct()
                    val formattedProducts = products.map {
                        it.formatValues()
                    }
                    Log.i(TAG,"loadProducts: formatted Products: $formattedProducts")
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
                    _uiEvents.showMessage(message)
                    eventsTracker.trackPurchaseProductsFailure(
                        error = err.message ?: message
                    )
                }
        }
    }


    private fun Product.formatValues(): Product {
        val title = this.title
        val description = this.description
        val badge = this.badge

        val newTitle = title.replace("%price%", this.price)
        val newDescription = description.replace("%price%", this.price)
        val newBadge = badge.copy(
            text = badge.text.replace("%price%", this.price)
        )

        return this.copy(
            title = newTitle,
            description = newDescription,
            badge = newBadge
        )
    }


}*/
