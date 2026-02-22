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

package com.kmpstarter.feature_purchases_data

import com.kmpstarter.feature_purchases_data.RevenueCatPurchasesRepository.Companion.SUBSCRIPTION_OFFER_IDENTIFIER_KEY
import com.kmpstarter.feature_purchases_data.mappers.toProduct
import com.kmpstarter.feature_purchases_domain.PurchaseException
import com.kmpstarter.feature_purchases_domain.PurchaseExceptionReason
import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.models.ProductId
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesBackendState
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository
import com.kmpstarter.utils.kotlinx_serialization.toJsonElement
import com.kmpstarter.utils.logging.Log
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.ktx.awaitRestore
import com.revenuecat.purchases.kmp.models.CacheFetchPolicy
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.Store
import com.revenuecat.purchases.kmp.models.StoreProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject


@Serializable
private data class ProductMetaData(
    val title: String = "",
    val description: String = "",
    val badge: String = "",
    val badgeBg: String? = null,
    val discountPercentage: Int = 0,
    val isTrial: Boolean = false,
)

@Serializable
private data class SubscriptionOfferMetaData(
    @SerialName("subscription_swaps")
    val swaps: Map<String, List<String>> = emptyMap(),
)


class RevenueCatPurchasesRepository(
    private val purchases: Purchases,
) : PurchasesRepository {

    companion object {
        private const val TAG = "RevenueCatPurchasesRepository"
        private const val KEY_REMOTE_PRODUCT_IDS = "purchasesProductIds"
        private const val KEY_PRODUCTS_META_DATA = "products_meta_data"
        private const val KEY_PAYWALL_META_DATA = "paywall_meta_data"
        private const val DISCOUNT_OFFER_IDENTIFIER_KEY = "discount_offer"
        private const val SUBSCRIPTION_OFFER_IDENTIFIER_KEY = "subscriptions_offer"
    }

    private var discountProduct: Product? = null
    private var products: List<Product>? = null
    private var discountStoreProduct: StoreProduct? = null
    private val json = Json {
        ignoreUnknownKeys = true
    }


    private val _purchasesState =
        MutableStateFlow<PurchasesBackendState>(PurchasesBackendState.Idle)
    override val purchasesState: StateFlow<PurchasesBackendState>
        get() = _purchasesState.asStateFlow()

    override suspend fun signIn(userId: String): Result<Unit> {
        return runCatching {
            val result = purchases.awaitLogIn(
                newAppUserID = userId
            )

            if (!result.created)
                throw PurchaseException(reason = PurchaseExceptionReason.UserNotSignedIn)
        }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        try {
            if (products != null)
                return Result.success(products!!)
            val (offerings, currentOffer, storeProducts) =
                awaitGetStoreProducts()

            Log.i(TAG, "getProducts: currentOffer: ${currentOffer.identifier}")


            val metadataRawMap = currentOffer.metadata[KEY_PRODUCTS_META_DATA] as? Map<*, *>

            products = storeProducts.map {
                val productMetaData = getProductMetaData(
                    metadataRawMap = metadataRawMap,
                    productId = it.id
                ) ?: ProductMetaData()
                Log.i(TAG, "getProducts: metaData for ${it.id}: $productMetaData")
                it.toProduct(
                    title = productMetaData.title,
                    description = productMetaData.description,
                    badge = productMetaData.badge,
                    badgeBg = productMetaData.badgeBg,
                    discountPercentage = productMetaData.discountPercentage,
                    isTrial = productMetaData.isTrial
                )
            }

            swapActiveSubscriptionsProducts()

            return Result.success(products!!).also {
                // Extract discount offer id from RevenueCat metadata
                val discountOfferIdentifier = currentOffer.getMetadataString(
                    key = DISCOUNT_OFFER_IDENTIFIER_KEY,
                    default = ""
                )
                loadDiscountProduct(
                    discountOfferIdentifier = discountOfferIdentifier,
                    offerings = offerings
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(PurchaseException(reason = PurchaseExceptionReason.ProductNotFound))
        }
    }

    override suspend fun getDiscountProduct(): Result<Product> {
        val product = if (discountProduct == null) loadDiscountProduct() else discountProduct
        Log.i(TAG, "getDiscountProduct: discount product $product")
        if (product == null)
            return Result.failure(PurchaseException(reason = PurchaseExceptionReason.ProductNotFound))
        return Result.success(product)
    }


    override suspend fun startPurchase(productId: ProductId): Result<Product> {
        val storeProduct =
            if (productId == discountStoreProduct?.id) discountStoreProduct else getStoreProduct(
                id = productId
            )
        if (storeProduct == null)
            return Result.failure(PurchaseException(reason = PurchaseExceptionReason.ProductNotFound))


        try {
            _purchasesState.update {
                PurchasesBackendState.Purchasing
            }
            val result = purchases.awaitPurchase(storeProduct = storeProduct)
            Log.d(tag = TAG, "startPurchase: result $result")
            Log.d(tag = TAG, "startPurchase: customerInfo ${result.customerInfo}")
            Log.d(tag = TAG, "startPurchase: storeTransaction ${result.storeTransaction}")
            // Validate purchase success by checking transaction ID
            val trxId = result.storeTransaction.transactionId
            val isPurchased = !(trxId.isNullOrEmpty())

            when {
                isPurchased -> {
                    Log.d(
                        tag = TAG,
                        "Purchase successful, premium entitlement found $trxId"
                    )
                    val product = products?.first {
                        it.id == productId
                    }!!
                    _purchasesState.update {
                        PurchasesBackendState.Purchased(product)
                    }
                    return Result.success(product)
                }

                else -> {
                    Log.e(
                        tag = TAG,
                        "Purchase failed, no premium entitlement found $trxId"
                    )
                    throw PurchaseException(PurchaseExceptionReason.PurchaseFailed("Purchase failed, no premium entitlement found"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val message = if (e is PurchasesException) e.underlyingErrorMessage else e.message
            val exception = e as? PurchaseException
                ?: PurchaseException(
                    PurchaseExceptionReason.PurchaseFailed(message = message ?: "")
                )
            _purchasesState.update {
                PurchasesBackendState.Error(exception)
            }
            return Result.failure(exception)
        }
    }

    override suspend fun getPaywallMetadata(): Result<PaywallMetadata> {
        return try {
            val (_, currentOffer, _) = awaitGetStoreProducts()

            // 1. Get the raw map
            val metadataMap = currentOffer.metadata[KEY_PAYWALL_META_DATA] as? Map<*, *>
                ?: error("Paywall metadata is missing")

            val jsonString = json.encodeToString(metadataMap.toJsonElement())

            // 3. Decode from that String
            val paywallMetadata = json.decodeFromString<PaywallMetadata>(jsonString)

            if (paywallMetadata.faqs.isEmpty() && paywallMetadata.reviews.isEmpty()) {
                error("Metadata collections are empty")
            }

            Result.success(paywallMetadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restorePurchases(): Result<List<Product>> {
        return runCatching {
            val customerInfo = purchases.awaitRestore()
            val activeSubscriptions =
                customerInfo.activeSubscriptions.ifEmpty {
                    throw PurchaseException(
                        reason = PurchaseExceptionReason.RestoreFailed(
                            "No active subscriptions found"
                        )
                    )
                }


            val (allOfferings, _, _) = awaitGetCurrentOffer()
            val allOfferingsProductsMetadata = allOfferings.all.values.flatMap { offering ->
                val metadataRawMap =
                    offering.metadata[KEY_PRODUCTS_META_DATA] as? Map<*, *>
                val allProducts = offering.availablePackages.map {
                    it.storeProduct
                }

                val productsMetaData = allProducts.map { product ->
                    val productMetaData = getProductMetaData(
                        metadataRawMap = metadataRawMap,
                        productId = product.id
                    )

                    product.id to productMetaData
                }

                productsMetaData
            }
            val purchasedStoreProducts = allOfferings.all.values
                .flatMap { value ->
                    value.availablePackages.map {
                        it.storeProduct
                    }
                }
                .filter { product ->
                    product.id in activeSubscriptions
                }

            val restoredProducts = purchasedStoreProducts.map { product ->
                val metadata = allOfferingsProductsMetadata.firstOrNull { (productId, _) ->
                    productId == product.id
                }?.second ?: ProductMetaData()


                product.toProduct(
                    title = metadata.title,
                    description = metadata.description,
                    badge = metadata.badge,
                    badgeBg = metadata.badgeBg,
                    discountPercentage = metadata.discountPercentage,
                    isTrial = metadata.isTrial
                )
            }
            restoredProducts
        }
    }

    private suspend fun getCustomerActiveSubscriptions(): Set<String> {
        val customer = purchases.awaitCustomerInfo(fetchPolicy = CacheFetchPolicy.CACHED_OR_FETCHED)
        val subscriptions = customer.activeSubscriptions
        return subscriptions
    }

    /**
     * Dynamically updates the [products] list by replacing currently active subscriptions with
     * their eligible "swap" alternatives defined in the offering metadata.
     *
     * This function performs the following steps:
     * 1. Fetches the user's active subscriptions and the latest offerings.
     * 2. Parses the `subscription_offer` metadata to identify defined swap paths (e.g., swapping 'Active Pro' for 'InActive Pro').
     * 3. Handles store-specific product ID formatting (using '.' for Test Store and ':' for Production).
     * 4. Filters out swap candidates that the user already possesses.
     * 5. Replaces the active product in the list with one or more swap products while
     * preserving the original `basePlanId` (e.g., if a user has 'basic:monthly', it swaps to 'pro:monthly').
     * 6. Falls back to original product metadata if specific metadata for the swap product is missing.
     *
     * @throws Exception if fetching offerings fails (logged and caught internally).
     * @see SUBSCRIPTION_OFFER_IDENTIFIER_KEY
     * @see toJsonElement
     */
    private suspend fun swapActiveSubscriptionsProducts() {
        val activeSubscriptions = getCustomerActiveSubscriptions()
        if (activeSubscriptions.isEmpty()) {
            Log.d(
                TAG,
                "swapActiveSubscriptionsProducts: user doesn't have any active subscription, #activeSubscriptions=$activeSubscriptions"
            )
            return
        }

        // We need the Subscriptions Offer to get the swap products
        val offerings = try {
            purchases.awaitOfferings()
        } catch (e: Exception) {
            Log.e(TAG, "swapActiveSubscriptionsProducts: Failed to get offerings", e)
            return
        }

        val subscriptionsOffer = offerings.all[SUBSCRIPTION_OFFER_IDENTIFIER_KEY]
        if (subscriptionsOffer == null) {
            Log.d(TAG, "swapActiveSubscriptionsProducts: Subscriptions offer not found")
            return
        }

        val subscriptionOfferMetaDataRawMap = subscriptionsOffer.metadata as? Map<*, *>
        if (subscriptionOfferMetaDataRawMap == null) {
            Log.e(TAG, "swapActiveSubscriptionsProducts: Subscriptions offer metadata not found")
            return
        }

        val subscriptionOfferMetaData = try {
            val jsonElement = subscriptionOfferMetaDataRawMap.toJsonElement()
            json.decodeFromJsonElement<SubscriptionOfferMetaData>(jsonElement)
        } catch (e: Exception) {
            Log.e(TAG, "swapActiveSubscriptionsProducts: Failed to parse metadata", e)
            return
        }

        val currentProducts = products?.toMutableList() ?: run {
            Log.d(TAG, "swapActiveSubscriptionsProducts: products list is null")
            return
        }

        val store = purchases.store
        val separator = when (store) {
            Store.TEST_STORE -> "."
            else -> ":"
        }


        val activeSubscriptionsIds = activeSubscriptions.map { it.split(separator)[0] }
        Log.i(
            TAG,
            "swapActiveSubscriptionsProducts: #store=${purchases.store.name}, activeSubscriptionIds=$activeSubscriptionsIds, activeSubscriptions=$activeSubscriptions,"
        )
        var hasChanges = false

        val swappedBasePlans = mutableListOf<String>()

        activeSubscriptions.forEach { activeSub ->
            Log.d(TAG, "swapActiveSubscriptionsProducts: processing activeSub=$activeSub")

            // Sub format: subscriptionId:basePlanId (e.g. pro:monthly)
            val parts = activeSub.split(separator)
            val subId = parts[0]
            val basePlanId = parts.getOrNull(1)

            if (basePlanId == null) {
                Log.w(
                    TAG,
                    "swapActiveSubscriptionsProducts: skipping $activeSub → missing basePlanId, #parts=$parts"
                )
                return@forEach
            }

            if (!subscriptionOfferMetaData.swaps.containsKey(subId)) {
                Log.d(
                    TAG,
                    "swapActiveSubscriptionsProducts: no swaps defined for subId=$subId"
                )
                return@forEach
            }

            val targetSwapIds = subscriptionOfferMetaData.swaps[subId].orEmpty()
            Log.d(
                TAG,
                "swapActiveSubscriptionsProducts: swap candidates for $subId → $targetSwapIds"
            )

            // Filter out swaps that are currently active
            val validSwapIds = targetSwapIds.filterNot { it in activeSubscriptionsIds }
            Log.d(
                TAG,
                "swapActiveSubscriptionsProducts: validSwapIds after filtering → $validSwapIds"
            )

            if (validSwapIds.isEmpty()) {
                Log.d(
                    TAG,
                    "swapActiveSubscriptionsProducts: no valid swaps left for subId=$subId"
                )
                return@forEach
            }

            val activeProductIndex = currentProducts.indexOfFirst { it.id == activeSub }
            if (activeProductIndex == -1) {
                Log.w(
                    TAG,
                    "swapActiveSubscriptionsProducts: active product $subId not found in currentProducts"
                )
                return@forEach
            }

            val currentProduct = currentProducts[activeProductIndex]
            Log.i(
                TAG,
                "swapActiveSubscriptionsProducts: replacing product=${currentProduct.id} " +
                        "at index=$activeProductIndex with swaps=$validSwapIds"
            )

            currentProducts.removeAt(activeProductIndex)
            hasChanges = true

            validSwapIds.forEachIndexed { index, swapId ->
                if (basePlanId in swappedBasePlans) {
                    Log.i(
                        TAG,
                        "swapActiveSubscriptionsProducts: basePlanId=$basePlanId is already swapped"
                    )
                    return@forEachIndexed
                }
                val p = currentProducts[activeProductIndex]
                val swapProductId = "$swapId$separator$basePlanId"
                Log.d(
                    TAG,
                    "swapActiveSubscriptionsProducts: resolving swapProductId=$swapProductId,#pId=${p.id}"
                )

                val swapPackage = subscriptionsOffer.availablePackages.firstOrNull {
                    it.storeProduct.id == swapProductId
                }

                if (swapPackage == null) {
                    Log.w(
                        TAG,
                        "swapActiveSubscriptionsProducts: swap product not found → $swapProductId"
                    )
                    return@forEachIndexed
                }

                val metadataRawMap =
                    subscriptionsOffer.metadata[KEY_PRODUCTS_META_DATA] as? Map<*, *>

                var productMetaData = getProductMetaData(
                    metadataRawMap = metadataRawMap,
                    productId = swapPackage.storeProduct.id
                )

                if (productMetaData == null) {
                    Log.d(
                        TAG,
                        "swapActiveSubscriptionsProducts: metadata missing for $swapProductId, " +
                                "falling back to active product metadata"
                    )

                    productMetaData = ProductMetaData(
                        title = currentProduct.title,
                        description = currentProduct.description,
                        badge = currentProduct.badge.text,
                        badgeBg = currentProduct.badge.backgroundColor,
                        discountPercentage = currentProduct.discountPercentage,
                        isTrial = currentProduct.isTrial
                    )
                }

                val swapProduct = swapPackage.storeProduct.toProduct(
                    title = productMetaData.title,
                    description = productMetaData.description,
                    badge = productMetaData.badge,
                    badgeBg = productMetaData.badgeBg,
                    discountPercentage = productMetaData.discountPercentage,
                    isTrial = productMetaData.isTrial
                )

                currentProducts.add(activeProductIndex + index, swapProduct)
                swappedBasePlans.add(basePlanId)
                Log.i(
                    TAG,
                    "swapActiveSubscriptionsProducts: inserted swap product=${swapProduct.id} " +
                            "at index=${activeProductIndex + index}"
                )
            }
        }

        if (hasChanges) {
            Log.i(
                TAG,
                "swapActiveSubscriptionsProducts: products updated successfully"
            )
            products = currentProducts
        } else {
            Log.i(
                TAG,
                "swapActiveSubscriptionsProducts: no changes applied"
            )
        }

    }

    private fun loadDiscountProduct(
        discountOfferIdentifier: String,
        offerings: Offerings,
    ): Product? {

        val discountOffer = offerings.all[discountOfferIdentifier] ?: return null

        discountStoreProduct =
            discountOffer.availablePackages[0].storeProduct

        val metadataRawMap = discountOffer.metadata[KEY_PRODUCTS_META_DATA] as? Map<*, *>


        val productMetaData = getProductMetaData(
            metadataRawMap = metadataRawMap,
            productId = discountStoreProduct!!.id
        ) ?: ProductMetaData()

        discountProduct = discountStoreProduct!!.toProduct(
            title = productMetaData.title,
            description = productMetaData.description,
            badge = productMetaData.badge,
            badgeBg = productMetaData.badgeBg,
            discountPercentage = productMetaData.discountPercentage,
            isTrial = productMetaData.isTrial
        )
        Log.i(TAG, "loadDiscountProduct: $discountProduct\n$productMetaData")
        return discountProduct
    }

    private suspend fun loadDiscountProduct(): Product? {
        return try {
            val (offerings, currentOffer, _) = awaitGetCurrentOffer()
            val discountOfferIdentifier = currentOffer.getMetadataString(
                key = DISCOUNT_OFFER_IDENTIFIER_KEY,
                default = ""
            )
            loadDiscountProduct(
                discountOfferIdentifier = discountOfferIdentifier,
                offerings = offerings
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun getProductMetaData(
        metadataRawMap: Map<*, *>?,
        productId: ProductId,
    ): ProductMetaData? {
        Log.i(TAG, "getProductMetaData: rawJson $metadataRawMap")

        if (metadataRawMap.isNullOrEmpty()) return null

        return try {
            // 1. Convert the entire raw map to a JsonObject using the extension
            val jsonObject = metadataRawMap.toJsonElement().jsonObject

            // 2. Decode the specific product ID's data directly into the data class
            // This replaces the manual 'as? String' and 'as? Int' calls
            val productJson = jsonObject[productId] ?: return null

            val productMetaData = json.decodeFromJsonElement<ProductMetaData>(productJson)

            Log.i(TAG, "getProductMetaData: Success for $productId: $productMetaData")
            productMetaData
        } catch (e: Exception) {
            Log.e(TAG, "getProductMetaData: Failed to parse for $productId", e)
            null
        }
    }

    private suspend fun getStoreProduct(id: ProductId): StoreProduct? {
        return try {
            val (offerings, _, products) = awaitGetStoreProducts()

            products.firstOrNull { it.id == id }
                ?: findProductInOfferings(offerings, id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun findProductInOfferings(
        offerings: Offerings,
        id: ProductId,
    ): StoreProduct? {
        return offerings.all.values
            .asSequence()
            .flatMap { it.availablePackages.asSequence() }
            .map { it.storeProduct }
            .firstOrNull { it.id == id }
    }


    private suspend fun awaitGetStoreProducts(): Triple<Offerings, Offering, List<StoreProduct>> {
        val (offerings, currentOffer, availablePackages) =
            awaitGetCurrentOffer()
        val products = availablePackages.map {
            it.storeProduct
        }

        if (products.isEmpty())
            throw Exception("No products found for current offering")
        return Triple(offerings, currentOffer, products)
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
        val currentOffer = offerings.current ?: throw Exception("No current offer found")

        val availablePackages = currentOffer.availablePackages
        if (availablePackages.isEmpty())
            throw Exception("No Available packages found")
        return Triple(offerings, currentOffer, availablePackages)
    }

}