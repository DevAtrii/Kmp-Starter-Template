<p align="center">
  <img src="docs/assets/logo.webp" alt="KMP Starter Template Logo" width="160"/>
</p>

<h1 align="center">KMP Starter Template</h1>

<p align="center">
  Project-agnostic, production-ready <b>Kotlin Multiplatform</b> starter for Android & iOS.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/iOS-12+-000000?style=for-the-badge&logo=apple&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-7+-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Version-0.4.0-6C63FF?style=for-the-badge" />
</p>

<p align="center">
  <a href="https://devatrii.github.io/Kmp-Starter-Template/">
    <img src="https://img.shields.io/badge/READ%20DOCUMENTATION-Click%20Here-6C63FF?style=for-the-badge" alt="Read Documentation"/>
  </a>
</p>

---

## Overview

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

```bash
git clone https://github.com/DevAtrii/Kmp-Starter-Template.git starter-app
```

Open in Android Studio (KMP Plugin required).
Open `iosApp/iosApp.xcodeproj` in Xcode to run on iOS.

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