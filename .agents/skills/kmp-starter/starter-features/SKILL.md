---
name: kmp-starter-features
description: How to reuse the KMP Starter Template's built-in feature modules — Analytics, Remote Config, Purchases, Database, Store Reviews & Updates, Splash/Onboarding, Notifications, and Locale. Each feature has a dedicated child skill.
author: DevAtrii
license: MIT

---

# Starter Features

Reuse Starter implementations. Do not replace them unless explicitly requested.

Each built-in feature has a dedicated child skill with full detail, file locations, and code examples. Read the relevant one before touching that feature.

| Feature | Child skill | When to read |
| --- | --- | --- |
| Analytics | [analytics](analytics/SKILL.md) | Adding/tracking events, combining providers, runtime swaps |
| Remote Config | [remote-config](remote-config/SKILL.md) | Typed config keys, feature flags |
| Purchases (RevenueCat) | [purchases](purchases/SKILL.md) | Paywalls, entitlements, IAP, metadata |
| Database (Room) | [database](database/SKILL.md) | Entities, DAOs, migrations |
| Store Reviews & Updates | [store-reviews-updates](store-reviews-updates/SKILL.md) | Review prompts, in-app updates |
| Splash & Onboarding | [splash-onboarding](splash-onboarding/SKILL.md) | Startup flow, onboarding slides |
| Notifications (alarmee) | [notifications](notifications/SKILL.md) | Local/scheduled/push notifications |
| Locale | [locale](locale/SKILL.md) | In-app language switching, RTL |

## Quick reference

| Feature | Key entry points |
| --- | --- |
| Analytics | `AppEvents` (sealed, core domain) + `EventsTracker` / `Analytics` |
| Remote Config | `ConfigKeys<T>` (sealed) + `GetConfigLogic` / `rememberRemoteConfig` |
| Purchases | `PurchasesViewModel` / `PurchasesLogics`, `rememberIsProUser()`, `AppScreens.Purchases`, `StarterPaywallDefaults.kt`, `publish/purchases.json` |
| Database | `KmpStarterDatabase`, `KmpStarterDatabaseMigrations`, entities + DAOs |
| Store Reviews & Updates | `rememberStarterStoreManager()`, `AppUpdateProvider(force = ...)` |
| Splash & Onboarding | `SplashViewModel`, `OnboardingViewModel`, `OnboardingLogics` |
| Notifications | `StarterNotificationsManager` (schedule/immediate/cancel), alarmee modules |
| Locale | `StarterLocales`, `LocaleProvider`, `LocaleSelectorDropdown` |

## Rules (apply to all features)

- Keep analytics calls in the presentation layer (ViewModel).
- Feature modules already ship Koin modules — register them in `InitKoin` (see koin skill).
- Do not introduce parallel analytics/config/purchase/file/notification/locale systems.

## Reference

- Docs: `https://starter.atherio.dev/features/` (Core, Remote Config, Analytics, Database, Purchases), `https://starter.atherio.dev/fundamentals/09-store-reviews-and-updates/`, `https://starter.atherio.dev/fundamentals/07-multiple-languages/`
- Source: `features/core/*`, `features/analytics/*`, `features/remote_config/*`, `features/purchases/*`, `features/database/*`, `features/notifications/*`, `features/locale/*`
