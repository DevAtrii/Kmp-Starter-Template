# Core

Welcome to the documentation. This page utilizes advanced Markdown extensions for a professional developer experience.

## Quick Setup

!!! info "Important Note"
Ensure you have the latest Kotlin version installed before proceeding with the `KmpAppInitializer` setup.

### Development Tasks

- [x] Setup Koin modules
- [x] Configure RevenueCat keys
- [ ] Implement custom Auth provider

## Code Implementation

```kotlin
fun initKmpApp() {
    // Standard initialization sequence (1)
    KmpStarter.initApp(apiKey = "MY_KEY")
    initKoin()
}
```

1. This order is critical to prevent DI resolution errors.

## Advanced Features

??? abstract "Architecture Details"
This template follows Clean Architecture principles:
- **Data Layer**: Repositories and DataSources
- **Domain Layer**: UseCases and Models
- **Presentation Layer**: Compose Multiplatform UI


## Glossary

**KMP**
: Kotlin Multiplatform — the core technology behind this template.[^1]

**RevenueCat**
: The service used for handling in-app purchases and subscriptions.

---

[^1]: KMP allows sharing up to 90% of code across Android and iOS.
