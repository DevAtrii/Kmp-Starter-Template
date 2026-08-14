---
name: kmp-starter-feature-purchases
description: The KMP Starter Template purchases system (RevenueCat) — setup, PurchasesViewModel vs PurchasesLogics, paywall navigation + customization, publish/purchases.json metadata, and entitlement checks.
author: DevAtrii
license: MIT

---

# Purchases (RevenueCat)

Subscriptions, one-time purchases, dynamic paywalls, and remote metadata via RevenueCat. Clean Architecture, fully integrated with Analytics and Navigation.

## Where things live

| Piece | Module | Path |
| --- | --- | --- |
| `PurchasesRepository` (interface) | purchases domain | `features/purchases/domain/.../repositories/PurchasesRepository.kt` |
| `Product`, `PaywallMetadata`, `Faq`, `Review`, `ProductBadge` | purchases domain | `features/purchases/domain/.../models/*.kt` |
| `PurchasesLogics` (+ per-action logics) | purchases domain | `features/purchases/domain/.../logics/*.kt` |
| `RevenueCatPurchasesRepository` | purchases data | `features/purchases/data/.../RevenueCatPurchasesRepository.kt` |
| `PurchasesViewModel` | purchases presentation | `features/purchases/presentation/.../PurchasesViewModel.kt` |
| `rememberIsProUser` / `rememberActiveProducts` | purchases presentation | `features/purchases/presentation/.../PurchasesRememberUtils.kt` |

## 1. Setup RevenueCat

Add API keys in `composeApp/.../core/AppConstants.kt` (debug/prod split, Android vs iOS):

```kotlin
object AppConstants {
    private const val RC_TEST_STORE_API_KEY = "test_api_key"

    private val RC_PROD_API_KEY = when (platform) {
        is Platform.Android -> "goog_android_api_key"
        is Platform.Ios -> "apple_api_key"
    }

    val REVENUE_CAT_API_KEY =
        if (platform.debug) RC_TEST_STORE_API_KEY else RC_PROD_API_KEY
}
```

In the RevenueCat dashboard: create products (weekly/yearly/...), create an Offering (make it default), and add Store Listing Metadata (FAQs, reviews, paywall version, product titles/descriptions/badges/discount labels).

## 2. Check entitlement (Compose)

```kotlin
val isPro = rememberIsProUser()              // Boolean — any active entitlement
val products = rememberActiveProducts().value // List<Product> — active products

if (!isPro) AdBanner()
```

`rememberIsProUser()` = `rememberActiveProducts().value.isNotEmpty()`.

## 3. Navigate to the paywall

Navigate to `AppScreens.Purchases` — registered in `composeApp/src/commonMain/kotlin/<your-package>/core/navigation/AppScreens.kt` (e.g. `data object Purchases : AppScreens()`). Do **not** invent `StarterScreens.Purchases`.

```kotlin
navigator.navigateTo(AppScreens.Purchases)
```

The screen auto-handles: loading products, starting purchase, restoring purchases, loading discount product, loading paywall metadata, and showing the discount dialog.

## 4. Customize the paywall

`composeApp/src/commonMain/kotlin/<your-package>/core/navigation/StarterPaywallDefaults.kt` is the single place to edit paywall strings, hero image, and feature list:

- `starterDefaultPaywallV1()` — builds `Paywalls.V1` from `Res.string.paywall_v1_*` + `Res.drawable.*`.
- `starterDefaultPaywallV1Features()` — the `List<PurchaseFeature>` (icon + string) shown.
- Swap the referenced `Res.string.paywall_v1_*` / drawable for your own resources (see resources-theme skill; regenerate accessors with `:features:resources:generateAccessors`).

## 5. Paywall metadata — `publish/purchases.json`

`publish/purchases.json` is the source you update for your project's RevenueCat **offering metadata**. `RevenueCatPurchasesRepository` reads these keys from the current offering, decoded against the domain models:

- `paywall_meta_data` → `PaywallMetadata` (`faqs`, `reviews`, `version`). `GetPaywallMetadataLogic` feeds `PurchasesViewModel`.
- `products_meta_data` → per-product metadata keyed by store product id (`title`, `description`, `badge`, `badgeBg`, `discountPercentage`, `isTrial`), decoded into `Product`/`ProductBadge`.
- `discountOffer` → identifier of the discount offering (maps to `DISCOUNT_OFFER_IDENTIFIER_KEY`).
- Product ids in `products_meta_data` **must** match your Google Play / App Store SKUs (e.g. `kmp_pro.weekly`, `kmp_pro.yearly`).

So: edit `publish/purchases.json` to reflect your products/FAQs/reviews, then mirror that JSON into the RevenueCat offering's metadata. `%price%` placeholders are formatted via `Product.formatValues()`.

Example:

```json
{
  "discountOffer": "kmp_starter_early_bird_discount",
  "paywall_meta_data": {
    "version": 1,
    "faqs": [
      { "question": "Does it support Android & iOS?", "answer": "Yes, fully shared KMP setup." }
    ],
    "reviews": [
      { "author": "Alex", "rating": 5.0, "review": "Saved weeks of setup time." }
    ]
  },
  "products_meta_data": {
    "kmp_pro.weekly": { "title": "Weekly Pro", "description": "Full access for %price%.", "badge": null },
    "kmp_pro.yearly": { "title": "Yearly Pro", "description": "Best value for %price%.", "badge": "Save 50%" }
  }
}
```

## 6. Custom ViewModel — `PurchasesLogics`

If you don't want `PurchasesViewModel`, inject `PurchasesLogics` and build your own VM. It bundles:

- `getProducts`, `startPurchase`, `restorePurchases`, `getPaywallMetadata`, `getCurrentPurchaseStatus`, `getDiscountProduct`, `signInToPurchase`

Call `signIn(userId)` (via `SignInToPurchaseLogic`) right after the user logs in so entitlements are tied to the account and restorable across devices.

## 7. Pro-gating — recommend what to lock

When planning a paid tier, **look at the app and recommend features that can be Pro-gated**. Keep the **core experience free**; gate the power/advanced features that only engaged users want.

Heuristics:

- **Free (core)**: the primary value — the thing that gets a user hooked and makes the app usable on day one.
- **Pro (gate)**: advanced controls, higher limits, power features, saving/export, customization, and any feature users "level up" into.

Example — a web-to-PDF app gates: extra page sizes (A3/A2/A1/A0), custom margins, custom CSS/JS, orientation, image/JS/cache/user-agent web settings, char + bookmark limits. The basic A4 convert stays free.

Only implement pro-gating when the user asks. When they do, propose the split first (free vs Pro), get agreement, then implement.

### Remote-config-driven gating (only if asked)

You can combine **Purchases + Remote Config** so the *set of gated features* is remotely tunable — change what's locked without a release. Do this **only when the user explicitly asks for remote-config gating**; recommend it, but don't build it unprompted.

Pattern (from a real Starter project):

1. **Define the gate config** as `RemoteConfigKey`s in `features/core/domain/.../ConfigKeys.kt` — a serializable model describing what non-Pro users are limited on, plus paywall trigger positions:

```kotlin
@Serializable
enum class NonProWebSettings { LOAD_IMAGES, JAVASCRIPT, CACHE, USER_AGENT }

@Serializable
data class NonProUserLimits(
    val htmlCharLimit: Int = 1_000,
    val lockedWebSettings: List<NonProWebSettings> = listOf(
        NonProWebSettings.LOAD_IMAGES,
        NonProWebSettings.JAVASCRIPT,
        NonProWebSettings.CACHE,
        NonProWebSettings.USER_AGENT,
    ),
    val orientationLocked: Boolean = true,
)

sealed class ConfigKeys<T>(
    key: String,
    defaultValue: T,
    serializer: KSerializer<T>? = null,
) : RemoteConfigKey<T>(key = key, defaultValue = defaultValue, serializer = serializer) {

    data object NonProLimits : ConfigKeys<NonProUserLimits>(
        key = "non_pro_limits",
        defaultValue = NonProUserLimits(),
        serializer = NonProUserLimits.serializer(),
    )
}
```

2. **Centralize the decision** in a pure `ProFeatureGate` object (`features/core/domain/.../pro/ProFeatureGate.kt`) that takes `isPro` + the remote limits and returns a boolean/enum — keeps gating logic out of every screen:

```kotlin
object ProFeatureGate {
    fun htmlCharLimit(isPro: Boolean, limits: NonProUserLimits): Int =
        if (isPro) Int.MAX_VALUE else limits.htmlCharLimit

    fun requiresProWebSetting(
        isPro: Boolean,
        limits: NonProUserLimits,
        setting: NonProWebSettings,
    ): Boolean = !isPro && setting in limits.lockedWebSettings
}
```

3. **Read at the call site**: `isPro` from `rememberIsProUser()` and `limits` from `rememberRemoteConfig(ConfigKeys.NonProLimits())`, then route through `ProFeatureGate`. When a gated feature is hit, navigate to `AppScreens.Purchases`.

This keeps the paywall trigger and the gate list both remotely configurable (change limits or lock/unlock features from Firebase without shipping).

## 8. Domain models

```kotlin
@Serializable
data class Product(
    val id: ProductId,            // typealias ProductId = String (SKU)
    val title: String,
    val description: String,
    val badge: ProductBadge,
    val price: String,
    val isTrial: Boolean,
    val discountPercentage: Int = 0,
) {
    fun formatValues(): Product  // replaces %price% in title/description/badge
}

@Serializable
data class PaywallMetadata(
    val faqs: Set<Faq> = emptySet(),
    val reviews: Set<Review> = emptySet(),
    val version: Int = 1,        // remotely control layout / A/B test
)
```

## Rules

- Products must be configured in Google Play Console, App Store Connect, **and** RevenueCat.
- UI is modular — modify paywall UI freely without touching purchase logic.
- Register `purchasesDataModule` + `purchasesDomainModule` + `purchasesPresentationModule` in `InitKoin` (see koin skill).
- Don't duplicate the purchase system.
- Keep the **core free**; recommend pro-gating only advanced/power features.
- Recommend remote-config gating, but only implement it (Purchases + Remote Config pro-gating) when explicitly asked.

## Reference

- Docs: `https://starter.atherio.dev/features/` → Purchases, `https://starter.atherio.dev/features/` → Remote Config
- Source: `features/purchases/*`, `composeApp/.../core/navigation/StarterPaywallDefaults.kt`, `publish/purchases.json`
