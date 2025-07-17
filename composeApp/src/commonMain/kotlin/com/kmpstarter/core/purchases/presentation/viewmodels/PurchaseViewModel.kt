package com.kmpstarter.core.purchases.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.kmpstarter.core.purchases.presentation.events.PurchaseEvent
import com.kmpstarter.core.purchases.presentation.states.PurchaseState
import com.kmpstarter.core.utils.logging.Log
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.ktx.awaitRestore
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
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

/**
 * ViewModel responsible for managing in-app purchase operations using RevenueCat.
 * 
 * This ViewModel handles:
 * - Fetching available subscription products from RevenueCat
 * - Processing purchase transactions
 * - Restoring previous purchases
 * - Managing discount offers and special pricing
 * - Tracking purchase state and loading states
 * 
 * Example usage:
 * ```
 * val viewModel: PurchaseViewModel = koinViewModel()
 * 
 * // Start a purchase
 * viewModel.onEvent(PurchaseEvent.StartPurchase(product))
 * 
 * // Restore purchases
 * viewModel.onEvent(PurchaseEvent.RestorePurchase)
 * 
 * // Get available products
 * viewModel.onEvent(PurchaseEvent.GetProducts)
 * ```
 * 
 * @see PurchaseEvent for available actions
 * @see PurchaseState for current state information
 */
class PurchaseViewModel : ViewModel() {

    private var getProductsJob: Job? = null
    private val purchases = Purchases.sharedInstance
    private var startPurchaseJob: Job? = null
    private var restorePurchaseJob: Job? = null

    private val _state = MutableStateFlow(PurchaseState())
    val state = _state.asStateFlow()

    init {
        getProducts()
    }

    companion object {
        private const val TAG = "PurchaseViewModel"

        // Metadata keys for discount offers in RevenueCat
        private const val DISCOUNT_OFFER_IDENTIFIER_KEY = "discountOffer"
        private const val DISCOUNT_OFFER_PERCENTAGE_KEY = "discountPercentage"
    }

    /**
     * Handles purchase-related events and delegates to appropriate functions.
     * 
     * @param event The purchase event to handle
     * 
     * Example:
     * ```
     * viewModel.onEvent(PurchaseEvent.StartPurchase(product))
     * viewModel.onEvent(PurchaseEvent.RestorePurchase)
     * viewModel.onEvent(PurchaseEvent.GetProducts)
     * ```
     */
    fun onEvent(event: PurchaseEvent) {
        when (event) {
            is PurchaseEvent.StartPurchase -> startPurchase(
                product = event.product
            )

            PurchaseEvent.GetProducts -> getProducts()
            PurchaseEvent.RestorePurchase -> restorePurchase()
        }
    }

    /**
     * Restores previous purchases from the app store.
     * 
     * This function:
     * 1. Calls RevenueCat's restore API to get customer information
     * 2. Checks for active subscriptions in the customer's account
     * 3. Matches active subscriptions with available packages in the current offering
     * 4. Updates the state with the restored subscription information
     * 
     * If no active subscriptions are found or if there's an error, the state is updated
     * with appropriate error messages.
     * 
     * Example:
     * ```
     * // Called when user taps "Restore Purchases" button
     * viewModel.onEvent(PurchaseEvent.RestorePurchase)
     * ```
     */
    private fun restorePurchase() {
        restorePurchaseJob?.cancel()
        restorePurchaseJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerInfo = purchases.awaitRestore()
                val activeSubscriptions = customerInfo.activeSubscriptions.ifEmpty {
                    throw Exception("No active subscriptions found")
                }
                val (_, _, availablePackages) = awaitGetCurrentOffer()
                if (availablePackages.isEmpty())
                    throw Exception("No available packages found in current offer")
                val currentPackages = availablePackages.filter {
                    it.storeProduct.id in activeSubscriptions
                }

                if (currentPackages.isEmpty())
                    throw Exception("No current active package found in current Offer")

                if (currentPackages.size > 1)
                    Log.d(
                        tag = TAG,
                        message = "Multiple active packages found in current offer ids: ${currentPackages.map { it.storeProduct.id }}"
                    )
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentSubscribedProduct = currentPackages[0].storeProduct,
                    )
                }

            } catch (e: PurchasesException) {
                Log.e(
                    tag = TAG,
                    "Unable to restore purchases error code: ${e.code}\n" +
                            "Error cause: ${e.underlyingErrorMessage}"
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        discountProduct = null,
                        discountPercentage = 0.0,
                        products = listOf(),
                        currentSubscribedProduct = null,
                        selectedProduct = null,
                        restoreError = e.message,
                        error = ""
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    tag = TAG,
                    "Unable to get restore purchases error message: ${e.message}"
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = listOf(),
                        discountProduct = null,
                        discountPercentage = 0.0,
                        selectedProduct = null,
                        currentSubscribedProduct = null,
                        error = "",
                        restoreError = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }


    /**
     * Fetches available subscription products from RevenueCat.
     * 
     * This function:
     * 1. Retrieves the current offering from RevenueCat
     * 2. Extracts available subscription products
     * 3. Processes discount offers if configured in RevenueCat metadata
     * 4. Updates the state with products and discount information
     * 
     * The function handles both RevenueCat-specific errors (PurchasesException) and
     * general exceptions, updating the state accordingly.
     * 
     * Example:
     * ```
     * // Called automatically on ViewModel initialization
     * // or manually when user refreshes products
     * viewModel.onEvent(PurchaseEvent.GetProducts)
     * ```
     */
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
                val (offerings, currentOffer, availablePackages) = awaitGetCurrentOffer()

                val products = availablePackages.map {
                    it.storeProduct
                }

                if (products.isEmpty())
                    throw Exception("No products found for current offering")

                // Extract discount offer information from RevenueCat metadata
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

    /**
     * Retrieves the current offering and its packages from RevenueCat.
     * 
     * @return Triple containing:
     * - All available offerings
     * - Current offering
     * - List of available packages in the current offering
     * 
     * @throws Exception if no current offering is found or no packages are available
     * 
     * Example:
     * ```
     * val (offerings, currentOffer, packages) = awaitGetCurrentOffer()
     * val products = packages.map { it.storeProduct }
     * ```
     */
    @Throws(Exception::class)
    private suspend fun awaitGetCurrentOffer(): Triple<Offerings, Offering, List<Package>> {
        val offerings = purchases.awaitOfferings()
        val currentOffer = offerings.current
        if (currentOffer == null)
            throw Exception("No current offer found")

        val availablePackages = currentOffer.availablePackages
        if (availablePackages.isEmpty())
            throw Exception("No Available packages found")
        return Triple(offerings, currentOffer, availablePackages)
    }


    /**
     * Initiates a purchase transaction for the specified product.
     * 
     * This function:
     * 1. Sets loading state to true
     * 2. Calls RevenueCat's purchase API with the selected product
     * 3. Validates the purchase result by checking transaction ID
     * 4. Updates the state with purchase completion status
     * 5. Handles errors and ensures loading state is reset
     * 
     * Purchase success is determined by the presence of a valid transaction ID.
     * 
     * @param product The StoreProduct to purchase
     * 
     * Example:
     * ```
     * // Called when user confirms purchase
     * viewModel.onEvent(PurchaseEvent.StartPurchase(selectedProduct))
     * ```
     */
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

                // Validate purchase success by checking transaction ID
                val isPurchased = !(result.storeTransaction.transactionId.isNullOrEmpty())
                
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


    /**
     * Updates the currently selected product in the state.
     * 
     * @param product The product to set as selected
     * 
     * Example:
     * ```
     * // Called when user selects a different subscription plan
     * viewModel.onSelectedProductChange(selectedProduct)
     * ```
     */
    fun onSelectedProductChange(product: StoreProduct) = _state.update {
        it.copy(
            selectedProduct = product
        )
    }

    /**
     * Clears all error states from the purchase state.
     * 
     * Example:
     * ```
     * // Called when user dismisses error messages
     * viewModel.clearErrorState()
     * ```
     */
    fun clearErrorState() {
        _state.update {
            it.copy(
                error = "",
                restoreError = ""
            )
        }
    }

    /**
     * Resets the purchase completion state.
     * 
     * Example:
     * ```
     * // Called when user starts a new purchase flow
     * viewModel.clearPurchaseState()
     * ```
     */
    fun clearPurchaseState() {
        _state.update {
            it.copy(
                isPurchaseComplete = false
            )
        }
    }
}















