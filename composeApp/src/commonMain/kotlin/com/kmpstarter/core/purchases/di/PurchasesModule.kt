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