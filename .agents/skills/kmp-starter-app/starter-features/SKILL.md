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
- Navigate to `StarterScreens.Purchases` (in the template's own screens) or build your own paywall via `starterDefaultPaywallV1()`.
- Paywall metadata (FAQs, reviews, version) comes from RevenueCat; products format `%price%` via `Product.formatValues()`.

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
