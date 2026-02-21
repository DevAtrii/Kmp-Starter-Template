package com.kmpstarter.feature_purchases_domain.repositories

import com.kmpstarter.feature_purchases_domain.PurchaseException
import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.models.ProductId
import kotlinx.coroutines.flow.StateFlow


/**
 * Represents the current status of the billing flow.
 * Use this to drive UI loading indicators, success overlays, or error dialogs.
 */
sealed class PurchasesBackendState {
    /** The default state. No active purchase process is running. */
    data object Idle : PurchasesBackendState()

    /** Triggered when a purchase is initiated but not yet finalized by the store. */
    data object Purchasing : PurchasesBackendState()

    /**
     * Successfully completed a purchase.
     * @param product The specific [Product] that was successfully acquired.
     */
    data class Purchased(val product: Product) : PurchasesBackendState()

    /**
     * Encapsulates a failure during any part of the purchase or restoration flow.
     * @param e The [PurchaseException] containing the failure reason.
     */
    data class Error(val e: PurchaseException) : PurchasesBackendState()
}

/**
 * Core interface for handling In-App Purchases (IAP).
 * This abstracts the underlying store implementation (e.g., RevenueCat, Google Play Billing, or StoreKit).
 */
interface PurchasesRepository {

    /**
     * A reactive stream of the current [PurchasesBackendState].
     * Observers should collect this to update the UI based on the purchase lifecycle.
     */
    val purchasesState: StateFlow<PurchasesBackendState>

    /**
     * Synchronizes the local user identity with the billing provider's backend.
     *
     * Call this method immediately after a user successfully logs into your application.
     * This ensures that all entitlements, active subscriptions, and purchase history
     * are correctly associated with the specific [userId] across different devices.
     *
     * If this is not called, purchases may remain tied to an anonymous device ID,
     * making it impossible to restore them if the user switches platforms or clears app data.
     *
     * @param userId The unique identifier for the user (usually from your Auth provider like Firebase or a custom backend).
     * @return A [Result] indicating success or containing a failure exception if the sync fails.
     */
    suspend fun signIn(userId: String): Result<Unit>

    /** Fetches the full list of available products from the store. */
    suspend fun getProducts(): Result<List<Product>>

    /** Fetches a specifically marked "discounted" or "promotional" product. */
    suspend fun getDiscountProduct(): Result<Product>

    /**
     * Launches the store's purchase flow for the given [productId].
     * @param productId The unique identifier of the product (SKU).
     */
    suspend fun startPurchase(productId: ProductId): Result<Unit>

    /** Retrieves configuration data used to render the Paywall UI (e.g., colors, imagery, text). */
    suspend fun getPaywallMetadata(): Result<PaywallMetadata>

    /**
     * Synchronizes previous purchases from the store to the current user account.
     * @return The most significant [Product] restored (e.g., the highest tier subscription).
     */
    suspend fun restorePurchases(): Result<List<Product>>
}



















