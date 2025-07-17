# 🚀 KMP Starter

![KMP-Starter-Template](https://github.com/user-attachments/assets/bcf5bd62-9d5f-4d6c-9d2a-d8de6e76a3eb)

A modern, production-ready **Kotlin Multiplatform** starter template with Material 3 design, utils, and clean architecture.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.8.2-orange.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Material 3](https://img.shields.io/badge/Material%203-Latest-green.svg)](https://m3.material.io/)

<div align="center">
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="150" />
  </a>
  <a href="https://www.youtube.com/@devatrii" target="_blank">
    <img src="https://img.shields.io/badge/YouTube-DevAtrii-red?style=for-the-badge&logo=youtube&logoColor=white" alt="YouTube Channel" />
  </a>
</div>

## ✨ Features

### 🔐 **Authentication** ✅ 80% Complete
- **Google Sign-In** - OAuth 2.0 authentication with Google
- **Email/Password** - Traditional authentication system
- **Password Reset** - Email-based password recovery
- **Account Deletion** - Email-based account deletion

### 🎨 **UI & Design**
- **Material 3 Design System** - Modern, adaptive design with dark mode support
- **Compose Multiplatform** - Shared UI across Android & iOS
- **Custom Components** - Reusable UI components with iOS-inspired design
- **Smooth Animations** - Spring animations and smooth transitions
- **Theme Management** - Dynamic theme switching with persistence

### 🏗️ **Architecture & DI**
- **Koin Dependency Injection** - Clean architecture with proper DI
- **MVVM+MVI Pattern** - ViewModels with StateFlow for reactive UI
- **Repository Pattern** - Clean data layer separation
- **Modular Structure** - Organized packages for scalability

### 💾 **Data & Storage**
- **Room Database** - Local data persistence with type-safe queries
- **DataStore Preferences** - Key-value storage for settings
- **Network Utilities** - Request state management and error handling
- **Coroutines & Flow** - Asynchronous programming with reactive streams

### 🎯 **Events & Navigation**
- **Event System** - Centralized event management
- **Snackbar Controller** - Global snackbar notifications
- **Navigation** - Type-safe & Global navigation with transitions
- **Theme Events** - Reactive theme management

### 💰 **Monetization**
- **RevenueCat Integration** - In-app purchases and subscriptions
  - [📖 RevenueCat Setup Guide](docs/revenue-cat.md) - Complete implementation guide

### 🛠️ **Utilities & Tools**
- **Platform Detection** - Cross-platform utilities
- **Logging System** - Structured logging across platforms
- **Time Utilities** - timemillis handling
- **Screen Size Detection** - Responsive design utilities

## 📁 Project Structure

```
composeApp/src/commonMain/kotlin/com/kmpstarter/
├── core/
│   ├── ui/                    # UI Components & Screens
│   │   ├── components/        # Reusable UI components
│   │   ├── screens/          # Screen implementations
│   │   ├── layouts/          # Custom layout components
│   │   ├── modifiers/        # Custom modifiers
│   │   ├── dialogs/          # Dialog components
│   │   └── bottom_sheets/    # Bottom sheet components
│   ├── events/               # Event Management
│   │   ├── controllers/      # Event controllers
│   │   ├── navigator/        # Navigation system
│   │   └── utils/           # Event utilities
│   ├── db/                  # Database Layer
│   │   ├── di/             # Database DI modules
│   │   └── *.kt            # Database entities & DAOs
│   ├── di/                 # Dependency Injection
│   │   ├── CoreModule.kt   # Core DI module
│   │   └── InitKoin.kt     # Koin initialization
│   ├── utils/              # Utilities
│   │   ├── common/         # Common utilities
│   │   ├── datastore/      # DataStore implementation
│   │   ├── logging/        # Logging utilities
│   │   ├── network_utils/  # Network utilities
│   │   └── platform/       # Platform-specific utils
│   ├── purchases/          # RevenueCat integration
│   └── AppConstants.kt     # App constants
├── starter_feaures/        # Completed features implemented by StarterTemplate i.e auth etc
├── theme/                  # Material 3 theming
└── App.kt                 # Main app entry point
```

## 🚀 Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/DevAtrii/Kmp-Starter-Template.git
   cd KMP-Starter-Template
   ```
 
2. **Run the app**
   ```bash
   # Android
   ./gradlew androidApp:assembleDebug
   
   # iOS
   ./gradlew iosApp:assembleXCFramework
   ```
 

## 📱 Supported Platforms

- **Android** - API 24+ (Android 7.0+)
- **iOS** - iOS 13.0+

## 🛠️ Tech Stack

- **Kotlin Multiplatform** - Cross-platform development
- **Compose Multiplatform** - Shared UI framework
- **Material 3** - Design system
- **Koin** - Dependency injection
- **Room** - Local database
- **DataStore** - Preferences storage
- **Coroutines & Flow** - Asynchronous programming
- **RevenueCat** - In-app purchases
- **Kotlinx Serialization** - JSON serialization

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**Built by DevAtrii with ❤️ using Kotlin Multiplatform & Compose**