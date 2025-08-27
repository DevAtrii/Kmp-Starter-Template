/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.core.purchases.di

import com.kmpstarter.core.purchases.presentation.viewmodels.PurchaseDialogViewModel
import com.kmpstarter.core.purchases.presentation.viewmodels.PurchaseViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val purchasesModule = module {
    viewModelOf(::PurchaseDialogViewModel)
    singleOf(::PurchaseViewModel)
}