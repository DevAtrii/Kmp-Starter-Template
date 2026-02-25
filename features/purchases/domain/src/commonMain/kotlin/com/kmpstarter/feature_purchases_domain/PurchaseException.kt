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

package com.kmpstarter.feature_purchases_domain

/**
 * A specialized exception thrown during billing operations.
 * @param reason The machine-readable [PurchaseExceptionReason] for the failure.
 */
class PurchaseException(val reason: PurchaseExceptionReason) :
    IllegalStateException("Purchase failed with reason: $reason")

/**
 * Categorized reasons for billing failures.
 */
sealed class PurchaseExceptionReason {
    /** The requested product ID does not exist in the store configuration. */
    data object ProductNotFound : PurchaseExceptionReason()

    /** The purchase flow was interrupted or denied by the store. */
    data class PurchaseFailed(val message: String) : PurchaseExceptionReason()

    /** The restoration process failed or no valid previous purchases were found. */
    data class RestoreFailed(val message: String) : PurchaseExceptionReason()

    /** User authentication is required by the backend before a purchase can proceed. */
    data object UserNotSignedIn : PurchaseExceptionReason()
}