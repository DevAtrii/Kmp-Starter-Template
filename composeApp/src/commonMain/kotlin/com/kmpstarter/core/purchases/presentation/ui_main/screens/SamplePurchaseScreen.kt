package com.kmpstarter.core.purchases.presentation.ui_main.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kmpstarter.core.purchases.presentation.models.PurchaseFeature
import com.kmpstarter.core.utils.logging.Log
import com.revenuecat.purchases.kmp.models.Period
import com.revenuecat.purchases.kmp.models.PeriodUnit
import com.revenuecat.purchases.kmp.models.PresentedOfferingContext
import com.revenuecat.purchases.kmp.models.Price
import com.revenuecat.purchases.kmp.models.ProductCategory
import com.revenuecat.purchases.kmp.models.ProductType
import com.revenuecat.purchases.kmp.models.PurchasingData
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreProductDiscount
import com.revenuecat.purchases.kmp.models.SubscriptionOption
import com.revenuecat.purchases.kmp.models.SubscriptionOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SamplePurchaseScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    // Dummy data for testing
    val dummyProducts = remember {
        listOf(
            createDummyProduct(
                id = "basic_monthly",
                title = "Basic Plan",
                price = "$4.99",
                description = "Perfect for getting started"
            ),
            createDummyProduct(
                id = "pro_monthly",
                title = "Pro Plan",
                price = "$9.99",
                description = "Most popular choice"
            ),
            createDummyProduct(
                id = "premium_monthly",
                title = "Premium Plan",
                price = "$19.99",
                description = "For power users"
            )
        )
    }

    val dummyFeatures = remember {
        listOf(
            PurchaseFeature(
                icon = Icons.Default.Code,
                label = "Cross-Platform Development",
                description = "Build for Android and iOS with single codebase"
            ),
            PurchaseFeature(
                icon = Icons.Default.Palette,
                label = "Material 3 Design",
                description = "Modern, adaptive design system"
            ),
            PurchaseFeature(
                icon = Icons.Default.Storage,
                label = "Room Database",
                description = "Local data persistence with type-safe queries"
            ),
            PurchaseFeature(
                icon = Icons.Default.Tune,
                label = "Dependency Injection",
                description = "Clean architecture with Koin DI"
            ),
            PurchaseFeature(
                icon = Icons.Default.Bolt,
                label = "Coroutines & Flow",
                description = "Reactive programming with Kotlin"
            ),
            PurchaseFeature(
                icon = Icons.Default.Star,
                label = "RevenueCat Integration",
                description = "In-app purchases and subscriptions"
            )
        )
    }

    val dummyTerms = remember {
        listOf(
            "Subscription automatically renews unless auto-renew is turned off at least 24 hours before the end of the current period",
            "Account will be charged for renewal within 24 hours prior to the end of the current period",
            "Subscriptions may be managed by the user and auto-renewal may be turned off by going to the user's Account Settings after purchase",
            "Any unused portion of a free trial period, if offered, will be forfeited when the user purchases a subscription to that publication",
            "Prices may vary by location. Subscriptions automatically renew unless auto-renew is turned off at least 24 hours before the end of the current period"
        )
    }

    val discountProduct = remember {
        createDummyProduct(
            id = "special_offer",
            title = "Special Offer",
            price = "$7.99",
            description = "Limited time 20% off"
        )
    }

    var selectedProduct by remember { mutableStateOf(dummyProducts[0]) } // Default to Pro Plan
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    SamplePurchaseScreenContent(
        modifier = modifier,
        selectedProduct = selectedProduct,
        features = dummyFeatures,
        products = dummyProducts,
        subscriptionTerms = dummyTerms,
        discountProduct = discountProduct,
        discountPercentage = 20.0,
        cancelTimerTimeMillis = 10000L, // 10 seconds
        isLoading = isLoading,
        onRestoreClick = {
            // Handle restore purchase
        },
        onPurchaseClick = {
            scope.launch {
                Log.d(
                    tag = "SamplePurchaseScreen",
                    message = "Purchasing ${selectedProduct.id}"
                )
                isLoading = true
                delay(2000L)
                isLoading = false
            }
        },
        onProductSelected = { product ->
            selectedProduct = product
        },
        onDismiss = onDismiss
    )
}

@Composable
private fun SamplePurchaseScreenContent(
    modifier: Modifier = Modifier,
    selectedProduct: StoreProduct,
    features: List<PurchaseFeature>,
    products: List<StoreProduct>,
    subscriptionTerms: List<String>,
    discountProduct: StoreProduct?,
    discountPercentage: Double?,
    cancelTimerTimeMillis: Long,
    isLoading: Boolean,
    onRestoreClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onDismiss: () -> Unit,
    onProductSelected: (StoreProduct) -> Unit,
) {
    PurchaseSubscriptionScreenContent(
        modifier = modifier,
        selectedProduct = selectedProduct,
        features = features,
        products = products,
        subscriptionTerms = subscriptionTerms,
        discountProduct = discountProduct,
        discountPercentage = discountPercentage,
        cancelTimerTimeMillis = cancelTimerTimeMillis,
        isLoading = isLoading,
        onRestoreClick = onRestoreClick,
        onPurchaseClick = onPurchaseClick,
        onProductSelected = onProductSelected,
        onDismiss = onDismiss
    )
}

private fun createDummyProduct(
    id: String,
    title: String,
    price: String,
    description: String,
): StoreProduct {
    return object : StoreProduct {
        override val title: String = title
        override val price: Price = Price(
            amountMicros = when (price) {
                "$4.99" -> 4990000L
                "$9.99" -> 9990000L
                "$19.99" -> 19990000L
                "$7.99" -> 7990000L
                else -> 9990000L
            },
            currencyCode = "USD",
            formatted = price
        )
        override val id: String = id
        override val category: ProductCategory? = ProductCategory.SUBSCRIPTION
        override val type: ProductType = ProductType.UNKNOWN
        override val localizedDescription: String? = description
        override val period: Period? = Period(
            value = Random.nextInt(1, 12),
            unit = when (Random.nextInt(1, 3)) {
                1 -> PeriodUnit.MONTH
                2 -> PeriodUnit.YEAR
                else -> PeriodUnit.DAY
            }
        )
        override val defaultOption: SubscriptionOption? = null
        override val discounts: List<StoreProductDiscount> = emptyList()
        override val introductoryDiscount: StoreProductDiscount? = null
        override val presentedOfferingContext: PresentedOfferingContext? = null
        override val purchasingData: PurchasingData = object : PurchasingData {
            override val productId: String
                get() = id
            override val productType: ProductType
                get() = ProductType.UNKNOWN

        }
        override val subscriptionOptions: SubscriptionOptions? = null
    }
}

