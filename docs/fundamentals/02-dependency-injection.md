---
comments: false
icon: lucide/key
---

# Dependency Injection

The Starter Template uses `Koin` for dependency injection.

Each feature/module defines its own Koin module inside the `di` package to keep dependencies modular and scalable.

If you’re creating a custom module, enable Koin using the provided convention plugins:

```kotlin title="build.gradle.kts"
plugins {
    // Koin + Compose (for presentation modules)
    id(libs.plugins.build.koin.compose.get().pluginId)

    // Or Koin Core only (for non-UI modules)
    id(libs.plugins.build.koin.core.get().pluginId)
}
```

## Koin Compose Plugin

Use this in modules that apply **Compose Multiplatform** (e.g., `presentation`).
It includes Koin core + Compose integration.

## Koin Core Plugin

Use this in non-UI modules such as `domain` and `data`.
It provides core Koin functionality without Compose bindings.

---

## Creating a Koin Module

1. Create a `di` package inside your feature module.
2. Add a file like `{FeatureName}Module.kt`.
3. Define your module:

```kotlin title="FeatureNameModule.kt"
import org.koin.dsl.bind
import org.koin.dsl.module

val featureNameDataModule = module {
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class
}
```

---

## Register the Module

Include your module inside `initKoin`:

```kotlin title="composeApp/src/commonMain/kotlin/com/kmpstarter/core/di/InitKoin.kt"
import com.my.feature.di.featureNameDataModule

internal fun initKoin(...) {
    startKoin {
        ...
        modules(
            starterModules,
            kmpAppInitializerModule,
            // Add your feature modules here
            featureNameDataModule,
        )
    }
}
```

!!! note "Note"
    Make sure your feature module is added to the `composeApp` module's `commonMain` dependencies.

---
## Support My Project ☕️

If you find this project useful, consider supporting it by buying me a coffee. Your support will help me to continue working on this project and add more features.

<div >
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="150" />
  </a>
  <a href="https://www.youtube.com/@devatrii" target="_blank">
    <img src="https://img.shields.io/badge/YouTube-DevAtrii-red?style=for-the-badge&logo=youtube&logoColor=white" alt="YouTube Channel" />
  </a>
</div>

