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

import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository

/**
 * Syncs the local user with the billing provider using their userId.
 */
class SignInToPurchaseLogic(
    private val repository: PurchasesRepository,
) {

    suspend operator fun invoke(userId: String): Result<Unit> = repository.signIn(userId)

}