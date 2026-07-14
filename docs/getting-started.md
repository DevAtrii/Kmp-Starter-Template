---
comments: false
icon: lucide/rocket
---

# Getting Started

<p>
  <img src="https://img.shields.io/npm/v/%40devatrii%2Fstarter?style=for-the-badge&label=CLI&color=CB3837" alt="CLI version"/>
  <img src="https://img.shields.io/maven-central/v/io.github.devatrii/starter-core?style=for-the-badge&label=Maven&color=6C63FF" alt="Maven version"/>
</p>

## Requirements

- [x] Kotlin 2.4.0
- [x] Java 17
- [x] Gradle 9.0 or later
- [x] Android Studio Otter 2025.2.3 or later
- [x] KMP Plugin for Android Studio
- [x] Xcode 26 or later
- [x] Node.js 18+ (for the CLI)

!!! info "Android Studio Compatibility"
    Starter Template is compatible with Gradle 9.0, you can see list of android studio versions that supports gradle 9.0 <a href="https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility" target="_blank" rel="noopener noreferrer">
    here
    </a>

## Using the CLI

Start a project with the **KMP Starter CLI** ([`@devatrii/starter`](https://www.npmjs.com/package/@devatrii/starter)).

!!! warning "Alpha"
    The CLI is in **alpha**. It generates projects, but you may hit rough edges. If something breaks, please [open a GitHub issue](https://github.com/DevAtrii/Kmp-Starter-Template/issues).

### Install globally

```bash
npm install -g @devatrii/starter
``` 
 <img src="https://img.shields.io/npm/v/%40devatrii%2Fstarter?style=for-the-badge&label=CLI&color=CB3837" alt="CLI version"/>

This installs the `starter` and `kmp-starter` commands.

### Create a new project

Run interactively (prompts for app name, package, feature, modules, etc.):

```bash
starter create
```

Or pass options directly:

```bash
starter create \
  --name MyApp \
  --package com.example.myapp \
  --feature notes \
  --modules all
```

This generates a zip (and extracts it by default). Open the project in Android Studio, and open `iosApp/iosApp.xcodeproj` in Xcode to run on iOS.

### Other commands

```bash
# Create starter.json in an existing project
starter init

# Include a starter module into an existing project
starter include --module purchases

# Print CLI version
starter -v
```

---

## Add libraries to an existing project

Use published Maven artifacts (`io.github.devatrii`) when you already have a KMP app and only want starter modules.

### Option A — CLI `include` (recommended)

From your project root:

```bash
# Once per repo (creates starter.json)
starter init

# Add a module (wires catalog + dependency)
starter include --module purchases
# or: starter include --module analytics-data
```

### Option B — Manual

1. Add the version + library entries to `gradle/libs.versions.toml`:   <img src="https://img.shields.io/maven-central/v/io.github.devatrii/starter-core?style=for-the-badge&label=Maven&color=6C63FF" alt="Maven version"/>

```toml
[versions]
starter = "x.x.x"

[libraries]
# Starter
starter-core = { module = "io.github.devatrii:starter-core", version.ref = "starter" }
starter-utils = { module = "io.github.devatrii:starter-utils", version.ref = "starter" }
starter-native-bindings = { module = "io.github.devatrii:starter-native-bindings", version.ref = "starter" }
starter-ui-utils = { module = "io.github.devatrii:starter-ui-utils", version.ref = "starter" }
starter-ui-components = { module = "io.github.devatrii:starter-ui-components", version.ref = "starter" }
starter-ui-layouts = { module = "io.github.devatrii:starter-ui-layouts", version.ref = "starter" }

# Features
starter-feature-navigation = { module = "io.github.devatrii:feature-navigation", version.ref = "starter" }
starter-feature-analytics-data = { module = "io.github.devatrii:feature-analytics-data", version.ref = "starter" }
starter-feature-analytics-domain = { module = "io.github.devatrii:feature-analytics-domain", version.ref = "starter" }
starter-feature-core-data = { module = "io.github.devatrii:feature-core-data", version.ref = "starter" }
starter-feature-core-domain = { module = "io.github.devatrii:feature-core-domain", version.ref = "starter" }
starter-feature-core-presentation = { module = "io.github.devatrii:feature-core-presentation", version.ref = "starter" }
starter-feature-locale = { module = "io.github.devatrii:feature-locale", version.ref = "starter" }
starter-feature-remote-config-data = { module = "io.github.devatrii:feature-remote-config-data", version.ref = "starter" }
starter-feature-remote-config-domain = { module = "io.github.devatrii:feature-remote-config-domain", version.ref = "starter" }
starter-feature-remote-config-presentation = { module = "io.github.devatrii:feature-remote-config-presentation", version.ref = "starter" }
starter-feature-purchases-data = { module = "io.github.devatrii:feature-purchases-data", version.ref = "starter" }
starter-feature-purchases-domain = { module = "io.github.devatrii:feature-purchases-domain", version.ref = "starter" }
starter-feature-purchases-presentation = { module = "io.github.devatrii:feature-purchases-presentation", version.ref = "starter" }
starter-feature-notifications-core = { module = "io.github.devatrii:feature-notifications-core", version.ref = "starter" }
starter-feature-notifications-local = { module = "io.github.devatrii:feature-notifications-local", version.ref = "starter" }
starter-feature-notifications-push = { module = "io.github.devatrii:feature-notifications-push", version.ref = "starter" }
```

2. Depend on them from your app / feature module:

```kotlin
implementation(libs.starter.core)
implementation(libs.starter.feature.purchases.domain)
implementation(libs.starter.feature.purchases.data)
implementation(libs.starter.feature.purchases.presentation)
```

3. Register the feature’s **Koin modules** in your `startKoin { modules(...) }` (e.g. `purchasesDataModule`, `purchasesDomainModule`, `purchasesPresentationModule`).

!!! warning "Don't forget Koin"
    Adding a Gradle dependency alone is not enough. Every starter feature ships its own Koin module(s) — you **must** include them in `initKoin` / `startKoin`, or injections will fail at runtime.

---

On the next page, learn about all modules.

!!! note "Web wizard"
    A **web wizard** for generating projects in the browser is also planned. Until then, use the CLI.

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
