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

package com.kmpstarter.core

import androidx.compose.runtime.Stable
import com.kmpstarter.feature_purchases_domain.logics.PurchasesLogics
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** for code highlighting */
@DslMarker
annotation class AppInitializerDsl


/**
 * Orchestrates application-wide startup logic and background task coordination.
 *
 * This class acts as the central hub for "warming up" the app, ensuring that
 * necessary data (like store products) is pre-fetched and user sessions are
 * synchronized across features (like Billing and Auth) before the user interacts
 * with the main UI.
 */
@Stable
@AppInitializerDsl
internal class KmpAppInitializer(
    private val purchasesLogics: PurchasesLogics,
) {

    companion object {
        private const val TAG = "KmpAppInitializer"
    }

    /**
     * Scope for background tasks that should live as long as the application process.
     * Uses [SupervisorJob] so that a failure in one task (e.g., fetching products)
     * doesn't cancel the entire initialization flow.
     */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Entry point for triggering all startup orchestrations.
     * Should be called from the Application class or a dedicated Splash/Bootstrap screen.
     */
    @AppInitializerDsl
    fun initialize() {
        Log.d(TAG, "initialize: Starting app startup sequence...")

        loadUserAndInitAuthTasks()
        onNonAuthSessionStarted()

        Log.d(TAG, "initialize: Startup sequence initiated.")
    }

    /**
     * Triggered when the app enters a "Guest" or unauthenticated state.
     *
     * Use this to initialize features that do not require a user account,
     * such as fetching the general product catalog or analytics for
     * non-registered users.
     */
    private fun onNonAuthSessionStarted() {
        Log.d(TAG, "onNonAuthSessionStarted: Initializing non-auth dependencies...")
        getProducts()
    }

    /**
     * Initializes the logic for identifying and loading the user's profile.
     *
     * This is the bridge between the app startup and the Authentication system.
     * It should be responsible for checking if a valid token exists and
     * determining if the app should proceed as an authenticated or guest session.
     */
    private fun loadUserAndInitAuthTasks() {
        Log.d(TAG, "loadUserAndInitAuthTasks: Checking for existing authenticated session...")
        /* TODO: When auth feature is implemented:
           1. Check local storage for session
           2. If session exists -> onAuthSessionStarted()
           3. If no session -> do nothing (already handled by onNonAuth)
        */
    }

    /**
     * Triggered once a user has successfully logged in or a valid session is restored.
     *
     * Handles tasks that are "User-Aware," such as linking the user's ID to
     * the billing backend (Purchases) or syncing user-specific settings.
     */
    private fun onAuthSessionStarted() {
        Log.i(TAG, "onAuthSessionStarted: User session detected. Synchronizing auth-related tasks...")
        signInUserToPurchases()
    }

    /**
     * Pre-fetches store products and caches them in the backend.
     * This ensures that when the user opens the Paywall, the products load instantly.
     */
    private fun getProducts() {
        Log.d(TAG, "getProducts: Fetching store products to warm up cache...")
        scope.launch {
            purchasesLogics
                .getProducts()
                .onSuccess { products ->
                    Log.i(TAG, "getProducts: Successfully fetched and cached ${products.size} products.")
                }
                .onFailure { err ->
                    Log.e(TAG, "getProducts: Failed to fetch products during startup. Error: ${err.message}", err)
                }
        }
    }

    /**
     * Synchronizes the authenticated user's ID with the billing provider.
     */
    private fun signInUserToPurchases() {
        Log.d(TAG, "signInUserToPurchases: Linking user identity with billing backend...")
        /* TODO: scope.launch {
                purchasesLogics.signInToPurchase(userId = userId)
           }
        */
    }
}




















