---

comments: false
icon: lucide/cat
---

# Purchases

Starter Template uses **RevenueCat** to handle:

* Subscriptions
* One-time purchases
* Dynamic paywalls
* Remote metadata (FAQs, reviews, layout versions)
* Multi-product support

It follows Clean Architecture and is fully integrated with Analytics and Navigation.

---

## 1. Setup RevenueCat

Add your API keys inside:

```kotlin title="composeApp/src/commonMain/.../core/AppConstants.kt" linenums="1" hl_lines="3-8"
object AppConstants {

    private const val RC_TEST_STORE_API_KEY = "test_api_key"

    private val RC_PROD_API_KEY = when (platform) {
        is Platform.Android -> "goog_android_api_key"
        is Platform.Ios -> "apple_api_key"
    }

    val REVENUE_CAT_API_KEY =
        if (platform.debug) RC_TEST_STORE_API_KEY
        else RC_PROD_API_KEY
}
```

Replace:

* `goog_android_api_key` → Google Play key
* `apple_api_key` → App Store key
* `test_api_key` → RevenueCat test key

!!! note "Official Docs"
    Generate API keys from RevenueCat dashboard:
    [https://www.revenuecat.com/docs/getting-started/quick-start](https://www.revenuecat.com/docs/getting-started/quick-start)

---

## 2. RevenueCat Dashboard Setup

Inside RevenueCat:

1. Create products (weekly, yearly, etc.)
2. Create an Offering `(make it default)`
3. Add Store Listing Metadata (for paywall)

You can define:

* FAQs
* Reviews
* Paywall version
* Product titles/descriptions
* Badges
* Discount labels

Example metadata stored in RevenueCat:

```json title="RevenueCat Paywall Metadata Example" linenums="1"
{
  "paywall_meta_data": {
    "version": 1,
    "faqs": [
      {
        "question": "Does it support Android & iOS?",
        "answer": "Yes, fully shared KMP setup."
      }
    ],
    "reviews": [
      {
        "author": "Alex",
        "rating": 5.0,
        "review": "Saved weeks of setup time."
      }
    ]
  },
  "products_meta_data": {
    "kmp_pro.yearly": {
      "title": "Yearly Pro",
      "description": "Best value for %price%",
      "badge": "Save 50%"
    }
  }
}
```

!!! note
    `%price%` is automatically replaced using `Product.formatValues()`.

??? abstract "Full Sample" 
    ``` json title="Metadata"
    {
    "discountOffer": "kmp_starter_early_bird_discount",
    "paywall_meta_data": {
        "version": 1,
        "faqs": [
        {
            "question": "What is included in the KMP Starter Template?",
            "answer": "You get a production-ready Kotlin Multiplatform project with pre-configured Compose UI, Koin DI, RevenueCat Billing, and a robust startup orchestration system."
        },
        {
            "question": "Does it support both Android and iOS?",
            "answer": "Yes! The template is built for 100% shared logic across platforms, including shared UI using Compose Multiplatform for maximum code reuse."
        },
        {
            "question": "Can I use this for commercial projects?",
            "answer": "Absolutely. Once you purchase a plan, you can use the template to launch your own apps on the Play Store and App Store without any hidden royalties."
        },
        {
            "question": "How do I get updates to the template?",
            "answer": "Premium members get access to our private repository where we regularly push updates for the latest Kotlin and Compose versions."
        },
        {
            "question": "Is there support for integrating my own backend?",
            "answer": "Yes, the template is designed with a clean architecture (Repository pattern), making it easy to swap the mock data with your Firebase, Supabase, or Ktor backend."
        },
        {
            "question": "What is the refund policy?",
            "answer": "We offer a 24-hour 'no questions asked' refund if the template doesn't meet your technical requirements."
        }
        ],
        "reviews": [
        {
            "author": "Alex Rivera",
            "profilePictureUrl": "https://placehold.co/150x150/6366f1/ffffff?text=AR&font=roboto",
            "rating": 5.0,
            "review": "Saved me at least 40 hours of boilerplate setup. The billing integration alone is worth the price. Truly a plug-and-play solution for KMP."
        },
        {
            "author": "Sarah Chen",
            "profilePictureUrl": "https://placehold.co/150x150/ec4899/ffffff?text=SC&font=roboto",
            "rating": 5.0,
            "review": "The most stable KMP template I've found. Clean architecture, easy to follow, and the dependency injection setup is top-notch. Highly recommended!"
        },
        {
            "author": "MobileDev Guru",
            "profilePictureUrl": "https://placehold.co/150x150/10b981/ffffff?text=MG&font=roboto",
            "rating": 4.5,
            "review": "Solid foundation. It handles the 'boring' parts of app dev so I can focus on building unique features. Great documentation too."
        },
        {
            "author": "Jordan Smith",
            "profilePictureUrl": "https://placehold.co/150x150/f59e0b/ffffff?text=JS&font=roboto",
            "rating": 5.0,
            "review": "I launched my first iOS app in record time thanks to this starter. The shared UI logic works like a charm. Best investment this year."
        }
        ]
    },
    "products_meta_data": {
        "kmp_pro.weekly": {
        "title": "Weekly Pro",
        "description": "Full access to the template with weekly updates for %price%.",
        "badge": null
        },
        "kmp_pro.yearly": {
        "title": "Yearly Pro",
        "description": "Best for long-term projects. Get the template for %price% per year.",
        "badge": "Save 50%"
        }
    }
    }
    ```

---

## 3. Using PurchasesViewModel

The template provides a ready-to-use:

```
PurchasesViewModel
```

It handles:

* Loading products
* Starting purchase
* Restoring purchases
* Loading discount product
* Loading paywall metadata
* Tracking analytics events

You can directly use this ViewModel in your Purchases screen.

!!! warning   
    Use this only when you are implementing your own purchases screen, otherwise navigate to `Starter.Purchases` screen

!!! note
    viewModel also handles the analytics events using `AppEventsTracker`

---

## 4. Navigating to Purchases Screen

Use `StarterNavigator`:

```kotlin title="Example Navigation" linenums="1"
val navigator = StarterNavigator.getCurrent()

WelcomeScreen(
    onGetStartedClick = {
        navigator.navigateTo(
            route = StarterScreens.Purchases
        )
    }
)
```

Purchases screen automatically handles:

* Loading products
* Starting purchase
* Restoring purchases
* Loading discount product
* Loading paywall metadata
* Showing discount dialog

---

## 5. Checking Active Subscription

Use these Compose helpers:

```kotlin title="Active Products" linenums="1"
@Composable
fun rememberActiveProducts(): State<List<Product>>
```

```kotlin title="Is Pro User" linenums="1"
@Composable
fun rememberIsProUser(): Boolean
```

Example:

```kotlin title="Hide Ads Example" linenums="1"
val isPro = rememberIsProUser()

if (!isPro) {
    AdBanner()
}
```

---

## 6. Using PurchasesLogics (Custom ViewModel)

If you don’t want to use `PurchasesViewModel`, you can inject:

```
PurchasesLogics
```

It provides:

* `getProducts`
* `startPurchase`
* `restorePurchases`
* `getPaywallMetadata`
* `getCurrentPurchaseStatus`
* `getDiscountProduct`

This allows you to create your own custom ViewModel.

---

## 7. Paywall Metadata Models

The paywall is fully dynamic:

```kotlin title="PaywallMetadata.kt" linenums="1"
@Serializable
data class PaywallMetadata(
    val faqs: Set<Faq> = emptySet(),
    val reviews: Set<Review> = emptySet(),
    val version: Int = 1
)
```

You can:

* Change layout remotely using `version`
* A/B test paywalls
* Update FAQs without app release
* Add social proof using reviews

---

## 8. Product Model

Products are UI-ready:

```kotlin title="Product.kt" linenums="1"
@Serializable
data class Product(
    val id: ProductId,
    val title: String,
    val description: String,
    val badge: ProductBadge,
    val price: String,
    val isTrial: Boolean,
    val discountPercentage: Int = 0,
)
```

It automatically formats dynamic placeholders like `%price%`.

---

## 9. Restore Purchases

Handled automatically inside:

```
PurchasesViewModel -> restorePurchases()
```

or you can use 

```kotlin title="Restore Purchases"
val purchasesLogics = PurchasesLogics()
purchasesLogics.restorePurchases()
```

---

## 10. Advanced Customization

You can:

* Add sign-in before purchase
* Attach userId using RevenueCat
* Add intro discounts
* A/B test layouts
* Control discounts dynamically

---

## Important Notes

!!! warning
    Make sure your products are correctly configured in:
    - Google Play Console
    - Apple App Store Connect
    - RevenueCat Dashboard

!!! note
    Purchases feature is fully modular. You can modify UI freely without touching purchase logic.

---

## Summary

!!! abstract "How It Works"
    1. Add RevenueCat API keys  
    2. Configure products & offerings in dashboard  
    3. Define paywall metadata (FAQs, reviews, layout version)  
    4. Use `PurchasesViewModel` or `PurchasesLogics`  
    5. Navigate using `StarterNavigator`  
    6. Check subscription state using `rememberIsProUser()`  

    The entire purchase system is production-ready, analytics-enabled, and fully dynamic.
    
