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

package com.kmpstarter.feature_purchases_domain.logics

import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesBackendState
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCurrentPurchaseStatusLogic(
    private val repository: PurchasesRepository,
) {

    companion object {
        private const val TAG = "GetCurrentPurchaseStatusLogic"
    }

    operator fun invoke( ): Flow<List<Product>> = flow {
        val restoredProducts = repository.restorePurchases().getOrNull()
        if (restoredProducts != null && restoredProducts.isNotEmpty())
            emit(restoredProducts)


        repository.purchasesState.collect { state ->
            when (state) {
                is PurchasesBackendState.Error -> Log.e(TAG,"invoke: started purchase but failed: ${state.e}")
                PurchasesBackendState.Idle -> Unit
                is PurchasesBackendState.Purchased -> emit(listOf(state.product))
                PurchasesBackendState.Purchasing -> Log.d(TAG,"invoke: purchasing...")
            }
        }


    }

}



















