package com.kmpstarter.core.purchases.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.kmpstarter.core.purchases.presentation.events.PurchaseEvent
import com.kmpstarter.core.purchases.presentation.states.PurchaseState
import com.kmpstarter.core.utils.logging.Log
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.StoreProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PurchaseViewModel : ViewModel() {

    private var getProductsJob: Job? = null
    private val purchases = Purchases.sharedInstance
    private var startPurchaseJob: Job? = null

    private val _state = MutableStateFlow(PurchaseState())
    val state = _state.asStateFlow()

    init {
        getProducts()
    }

    companion object {
        private const val TAG = "PurchaseViewModel"

        private const val DISCOUNT_OFFER_IDENTIFIER_KEY = "discountOffer"
        private const val DISCOUNT_OFFER_PERCENTAGE_KEY = "discountPercentage"
    }

    fun onEvent(event: PurchaseEvent) {
        when (event) {
            is PurchaseEvent.StartPurchase -> startPurchase(
                product = event.product
            )

            PurchaseEvent.GetProducts -> getProducts()
            PurchaseEvent.RestorePurchase -> TODO()
        }
    }


    private fun getProducts() {
        getProductsJob?.cancel()
        getProductsJob = CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = "",
                    restoreError = "",
                )
            }
            try {
                val offerings = purchases.awaitOfferings()
                val currentOffer = offerings.current
                if (currentOffer == null)
                    throw Exception("No current offer found")

                val availablePackages = currentOffer.availablePackages
                if (availablePackages.isEmpty())
                    throw Exception("No Available packages found")

                val products = availablePackages.map {
                    it.storeProduct
                }

                if (products.isEmpty())
                    throw Exception("No products found for current offering")

                // getting discount offer product
                val discountOfferIdentifier = currentOffer.getMetadataString(
                    key = DISCOUNT_OFFER_IDENTIFIER_KEY,
                    default = ""
                )
                val discountOffer = offerings.all[discountOfferIdentifier]
                val discountPercentage = discountOffer?.getMetadataString(
                    key = DISCOUNT_OFFER_PERCENTAGE_KEY,
                    default = "0.0"
                )?.toDoubleOrNull() ?: 0.0
                val discountProduct = discountOffer?.availablePackages?.get(0)?.storeProduct

                _state.update {
                    it.copy(
                        products = products,
                        discountProduct = discountProduct,
                        discountPercentage = discountPercentage,
                        isLoading = false,
                        error = "",
                        restoreError = "",
                        selectedProduct = products[0]
                    )
                }
            } catch (e: PurchasesException) {
                Log.e(
                    tag = TAG,
                    "Unable to get products error code: ${e.code}\n" +
                            "Error cause: ${e.underlyingErrorMessage}"
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        discountProduct = null,
                        discountPercentage = 0.0,
                        products = listOf(),
                        selectedProduct = null,
                        error = e.message,
                        restoreError = ""
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    tag = TAG,
                    "Unable to get products error message: ${e.message}"
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = listOf(),
                        discountProduct = null,
                        discountPercentage = 0.0,
                        selectedProduct = null,
                        error = e.message ?: "Unknown error",
                        restoreError = ""
                    )
                }
            }
        }
    }


    private fun startPurchase(
        product: StoreProduct,
    ) {
        startPurchaseJob?.cancel()
        startPurchaseJob = CoroutineScope(Dispatchers.Main).launch {
            Log.d(tag = TAG, "Awaiting purchase result")
            try {
                _state.update {
                    it.copy(
                        isLoading = true,
                        error = "",
                        restoreError = "",
                    )
                }
                val result = purchases.awaitPurchase(product)
                Log.d(tag = TAG, "startPurchase: result $result")
                Log.d(tag = TAG, "startPurchase: customerInfo ${result.customerInfo}")
                Log.d(tag = TAG, "startPurchase: storeTransaction ${result.storeTransaction}")

                /*Todo improve the payment success detecting logic*/
                val isPurchased = !(result.storeTransaction.transactionId.isNullOrEmpty())
                // Check purchase result
                when {
                    isPurchased -> {
                        Log.d(
                            tag = TAG,
                            "Purchase successful, premium entitlement found ${result.storeTransaction.transactionId}"
                        )
                        _state.update {
                            it.copy(
                                isPurchaseComplete = true,
                                isLoading = false,
                                currentSubscribedProduct = product
                            )
                        }
                    }

                    else -> {
                        Log.e(
                            tag = TAG,
                            "Purchase failed, no premium entitlement found ${result.storeTransaction.transactionId}"
                        )
                        _state.update {
                            it.copy(
                                isPurchaseComplete = false,
                                isLoading = false,
                            )
                        }
                    }
                }

            } catch (e: PurchasesException) {
                Log.e(tag = TAG, "RevenueCat purchase error ${e.underlyingErrorMessage}")
            } catch (e: Exception) {
                Log.e(tag = TAG, "Unexpected purchase error", e)
            } finally {
                Log.d(tag = TAG, "Purchase flow completed")
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }


    fun onSelectedProductChange(product: StoreProduct) = _state.update {
        it.copy(
            selectedProduct = product
        )
    }

    fun clearErrorState() {
        _state.update {
            it.copy(
                error = "",
                restoreError = ""
            )
        }
    }

    fun clearPurchaseState() {
        _state.update {
            it.copy(
                isPurchaseComplete = false
            )
        }
    }
}















