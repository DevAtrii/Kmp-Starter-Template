---
name: kmp-starter-features
description: How to reuse the KMP Starter Template's built-in feature modules — Analytics, Remote Config, Purchases, Store Reviews & Updates, Splash/Onboarding, and Notifications.
---

# Starter Features

Reuse Starter implementations. Do not replace them unless explicitly requested.

## Analytics

- Define events in a sealed `AppEvents` hierarchy (`features/core/domain/.../AppEvents.kt`) extending `AppEvent`.
- Track via `EventsTracker.track(event = AppEvents.SomeEvent(...))` from ViewModels.
- Prefer `snake_case` event names; keep events in one sealed class; the type **is** the event.
- Provider (Mixpanel) swappable in the data layer; set token in `composeApp/.../core/AppConstants.kt` (`MIXPANEL_API_TOKEN`).

## Remote Config

- Define typed keys in a sealed `ConfigKeys<T>` hierarchy extending `RemoteConfigKey<T>` (key, default, optional serializer).
- Use `GetConfigLogic` in ViewModels, `rememberRemoteConfig(key = ConfigKeys.X())` in Compose.
- Always provide safe defaults; `@Serializable` objects need a `serializer`.
- Firebase-backed by default; swap `RemoteConfigRepository` for local/test impl if needed.

## Purchases (RevenueCat)

- Set keys in `AppConstants.kt` (`REVENUE_CAT_API_KEY`, debug/prod split).
- Use `PurchasesViewModel` (or `PurchasesLogics` for custom VMs).
- Check entitlement: `rememberIsProUser()`, `rememberActiveProducts()`.

### Navigate to the paywall

Navigate to `AppScreens.Purchases` — the screen is registered in `composeApp/src/commonMain/kotlin/<your-package>/core/navigation/AppScreens.kt` (e.g. `data object Purchases : AppScreens()`). Do **not** invent a `StarterScreens.Purchases`.

### Customize the paywall

`composeApp/src/commonMain/kotlin/<your-package>/core/navigation/StarterPaywallDefaults.kt` is the single place to edit paywall strings, hero image, and feature list:

- `starterDefaultPaywallV1()` — builds `Paywalls.V1` from `Res.string.paywall_v1_*` + `Res.drawable.*`.
- `starterDefaultPaywallV1Features()` — the `List<PurchaseFeature>` (icon + string) shown on the paywall.
- Change the referenced `Res.string.paywall_v1_*` / drawable to your app's own resources (see resources-theme skill; regenerate accessors with `:features:resources:generateAccessors`).

### Paywall metadata — `publish/purchases.json`

`publish/purchases.json` is the source you update for your project's RevenueCat **offering metadata**. `RevenueCatPurchasesRepository` reads these keys from the current offering (`features/purchases/data/.../RevenueCatPurchasesRepository.kt`), decoded against the domain models in `features/purchases/domain/.../models/`:

- `paywall_meta_data` → `PaywallMetadata` (`faqs`, `reviews`, `version`). `GetPaywallMetadataLogic` feeds `PurchasesViewModel`.
- `products_meta_data` → per-product metadata keyed by store product id (`title`, `description`, `badge`, `badgeBg`, `discountPercentage`, `isTrial`), decoded into `Product`/`ProductBadge`.
- `discount_offer` → identifier of the discount offering (maps to `DISCOUNT_OFFER_IDENTIFIER_KEY`).
- Product ids in `products_meta_data` **must** match your Google Play / App Store SKUs (e.g. `kmp_pro.weekly`, `kmp_pro.yearly`).

So: edit `publish/purchases.json` to reflect your products/FAQs/reviews, then mirror that JSON into the RevenueCat offering's metadata so the app reads it at runtime. Products format `%price%` via `Product.formatValues()`.

## Store Reviews & Updates

- `rememberStarterStoreManager()` → `askForReview()` after key actions.
- `AppUpdateProvider(force = ...)` wraps the app to auto-check updates (Android only).
- Avoid manual `checkAppUpdate()` unless necessary.

## Splash & Onboarding

Starter provides Splash and Onboarding (`features/core/presentation/.../screens/`). Customize them. If onboarding needs a different layout, add a custom slide instead of replacing the onboarding system.

## Notifications

Starter ships `features/notifications` (core/local/push) using the `alarmee` library. Reuse; register its Koin modules.

## Rules

- Keep analytics calls in the presentation layer (ViewModel).
- Feature modules already ship Koin modules — register them in `InitKoin` (see koin skill).
- Do not introduce parallel analytics/config/purchase/file systems.

## Reference

- Docs: `https://starter.atherio.dev/features/` (Core, Remote Config, Analytics, Database, Purchases), `https://starter.atherio.dev/fundamentals/09-store-reviews-and-updates/`
- `features/core/domain/.../AppEvents.kt`, `features/purchases/*`, `features/remote_config/*`, `features/analytics/*`
