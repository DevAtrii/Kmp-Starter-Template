<p align="center">
  <img src="docs/assets/logo.webp" alt="KMP Starter Template Logo" width="160"/>
</p>

<h1 align="center">KMP Starter Template</h1>

<p align="center">
  Project-agnostic, production-ready <b>Kotlin Multiplatform</b> starter for Android & iOS.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/iOS-12+-000000?style=for-the-badge&logo=apple&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-7+-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Node.js-18%2B-5FA04E?style=for-the-badge&logo=nodedotjs&logoColor=white" />
    <img src="https://img.shields.io/npm/v/%40devatrii%2Fstarter?style=for-the-badge&label=CLI&color=CB3837" />
  <img src="https://img.shields.io/maven-central/v/io.github.devatrii/starter-core?style=for-the-badge&label=Version&color=6C63FF" />
</p>

<p align="center">
  <a href="https://devatrii.github.io/Kmp-Starter-Template/">
    <img src="https://img.shields.io/badge/READ%20DOCUMENTATION-Click%20Here-6C63FF?style=for-the-badge" alt="Read Documentation"/>
  </a>
</p>

---

## Overview

<img width="1536" height="1024" alt="ChatGPT Image May 5, 2026, 06_24_13 PM" src="https://github.com/user-attachments/assets/375ae461-367a-49fd-87ed-98cea7427a31" />



KMP Starter Template is a multi-module boilerplate built with:

* Clean Architecture (data / domain / presentation)
* Compose Multiplatform
* Koin (DI)
* RevenueCat (In-App Purchases)
* Mixpanel (Analytics)
* Remote Config
* Room Database
* DataStore
* Modular Navigation
* Notifications
* Logging
* Platform utilities

It removes the repetitive setup work (analytics, purchases, remote config, etc.) so you can focus on building your app.

---

## Architecture

Each feature is isolated in its own module:

```text
features/
  analytics/
  core/
  database/
  navigation/
  purchases/
  remoteconfig/
  your-feature/
```

Every feature follows:

```text
data/
domain/
presentation/
```

You can easily swap implementations (e.g., replace Mixpanel with PostHog) by changing the data layer.

---

## Getting Started

Start with the **CLI** — [`@devatrii/starter`](https://www.npmjs.com/package/@devatrii/starter) <img src="https://img.shields.io/npm/v/%40devatrii%2Fstarter?style=for-the-badge&label=Version&color=CB3837" />. It scaffolds a full project under your app name and package, rewires modules and namespaces for you, and typically saves **2–3 hours** of manual rename and setup work.

The CLI is in **alpha** — it generates projects successfully, but expect rough edges. If you hit an issue, please [report it on GitHub Issues](https://github.com/DevAtrii/Kmp-Starter-Template/issues).

**Requirements:** Node.js 18+, Java 17

```bash
# Install globally
npm install -g @devatrii/starter

# Interactive create (prompts for name, package, modules, etc.)
starter create

# Or with flags
starter create \
  --name MyApp \
  --package com.example.myapp \
  --feature notes \
  --modules all

# Init starter.json in an existing project
starter init

# Add a starter module to an existing project
starter include --module purchases

# Print CLI version
starter -v
```

Commands: `create`, `init`, `include`, `version` (`-v` / `--version`).

Open the generated project in Android Studio (KMP Plugin required).
Open `iosApp/iosApp.xcodeproj` in Xcode to run on iOS.

---

## Libraries

We are working to ship **KMP Starter** as published Maven libraries so existing projects can adopt modules incrementally (starter utilities, UI, and feature layers) without cloning the full template.

Artifacts are published under **`io.github.devatrii`**. They are not fully stable yet—APIs and module boundaries may change between releases.


**Example `libs.versions.toml` for consumers:**   <img src="https://img.shields.io/maven-central/v/io.github.devatrii/starter-core?style=for-the-badge&label=Version&color=6C63FF" />


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

**Example dependency:**

```kotlin
implementation(libs.starter.core)
implementation(libs.starter.feature.purchases.presentation)
```

Catalog aliases use dots in Kotlin (`starter-core` → `libs.starter.core`). Adjust names to match your catalog if you prefer different aliases.

---

## License & Usage

You are free to:

* Use this template in personal or commercial projects
* Modify it
* Ship apps built with it

You are **not allowed** to:

* Resell this template (partially or fully) as another boilerplate
* Repackage and distribute it as a competing starter template (paid)

See the full license here:
[https://github.com/DevAtrii/Kmp-Starter-Template/blob/main/LICENSE](https://github.com/DevAtrii/Kmp-Starter-Template/blob/main/LICENSE)


----


<p align="center">
  <a href="https://devatrii.github.io/Kmp-Starter-Template/">
    <img src="https://img.shields.io/badge/READ%20DOCUMENTATION-Click%20Here-6C63FF?style=for-the-badge" alt="Read Documentation"/>
  </a>
</p>
