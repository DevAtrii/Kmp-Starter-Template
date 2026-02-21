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

package com.kmpstarter.feature_purchases_data.di

import com.kmpstarter.feature_purchases_data.RevenueCatPurchasesRepository
import com.kmpstarter.feature_purchases_domain.repositories.PurchasesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val purchasesDataModule = module {
    singleOf(::RevenueCatPurchasesRepository) bind PurchasesRepository::class
}