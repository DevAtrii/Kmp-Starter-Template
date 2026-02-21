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

import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository

/**
 * Retrieves configuration metadata used to render the paywall UI.
 */
class GetPaywallMetadataLogic(
    private val repository: PurchasesRepository,
) {

    suspend operator fun invoke(): Result<PaywallMetadata> = repository.getPaywallMetadata()

}