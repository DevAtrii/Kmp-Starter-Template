package com.kmpstarter

import androidx.compose.ui.window.ComposeUIViewController
import com.kmpstarter.core.di.initKoin
import com.kmpstarter.core.purchases.initRevenueCat

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        initRevenueCat()
    }
) {
    App()
}