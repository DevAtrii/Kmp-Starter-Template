# Changelog

All notable changes to the KMP Starter template will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.1] - 2025-07-21

### 🎉 Added
- **Cupertino UI Components** - Native iOS-style UI components for cross-platform development
  - **CupertinoDropdownMenu** & **CupertinoDropdownItem** - Native dropdown menus
  - **CupertinoSection** & **CupertinoSectionRow** - iOS-style section layouts
  - **CupertinoSwitch** - Native iOS switch component
  - **PillActionButton** & **PillActionsContainer** - iOS-style pill action buttons

### 🔧 Fixed
- **IntentUtils** - Improved cross-platform intent handling with better error handling and fallback mechanisms

## [0.1.1-beta] - 2025-07-17

### 🎉 Added

#### 💰 **RevenueCat Integration - Complete In-App Purchase System**

##### **Core Purchase Infrastructure**
- **RevenueCat SDK Integration** - Full cross-platform in-app purchase support
- **PurchaseViewModel** - Complete purchase state management with MVVM pattern
- **PurchaseDialogViewModel** - Dialog-specific state management with DataStore integration
- **PurchaseState** - Comprehensive state management for subscriptions, products, and errors
- **PurchaseEvent** - Event-driven architecture for purchase actions

##### **UI Components & Screens**
- **PurchaseSubscriptionScreen** - Complete subscription screen with Material 3 design
  - Responsive layout with breakpoints for different screen sizes
  - Vertical product cards with "Best Value" highlighting
  - Feature list display with icons and descriptions
  - Loading states and error handling
  - Cancel timer with circular progress indicator
  - Floating action buttons for purchase and restore

- **PurchaseDiscountDialog** - Special discount offer dialog
  - Timer with flip animation using expiration time
  - Disabled outside/back dismiss for better UX
  - Automatic discount percentage display
  - Integrated with DataStore for persistence

- **SamplePurchaseScreen** - Testing screen with dummy data
  - Complete purchase flow demonstration
  - Dummy product generation for testing
  - All UI states and interactions

##### **Navigation & Routing**
- **PurchasesScreens** - Type-safe navigation destinations
- **PurchasesNavGraph** - Complete navigation setup for purchase flows
- **PurchaseFeature** - Data model for subscription features

##### **Dependency Injection**
- **PurchasesModule** - Koin module for purchase-related dependencies
- **PurchaseViewModel** and **PurchaseDialogViewModel** injection setup

##### **Documentation**
- **RevenueCat Integration Guide** (`docs/revenue-cat.md`) - Comprehensive implementation guide
  - Step-by-step setup instructions
  - RevenueCat dashboard configuration
  - Custom metadata examples
  - Premium feature implementation examples
  - Testing and troubleshooting guide

### 🔧 **Features**

#### **Subscription Management**
- ✅ **Product Loading** - Automatic loading of available subscription products
- ✅ **Product Selection** - Interactive product selection with visual feedback
- ✅ **Purchase Flow** - Complete purchase transaction handling
- ✅ **Restore Purchases** - Cross-platform purchase restoration
- ✅ **Discount Offers** - Special pricing with timer-based expiration
- ✅ **Error Handling** - Comprehensive error states and user feedback

#### **Premium Feature Access**
- ✅ **State-Based Access Control** - Check premium status via `PurchaseState.currentSubscribedProduct`
- ✅ **Event-Driven Architecture** - React to purchase events and state changes
- ✅ **Loading States** - Visual feedback during purchase operations
- ✅ **Success/Error Feedback** - Clear user communication for all outcomes

#### **UI/UX Enhancements**
- ✅ **Responsive Design** - Adapts to different screen sizes (compact/expanded)
- ✅ **Material 3 Design** - Modern design system with proper theming
- ✅ **Smooth Animations** - Scale, fade, and slide animations
- ✅ **Accessibility** - Proper content descriptions and navigation
- ✅ **Loading Indicators** - Circular progress and loading states

### 🏗️ **Architecture**

#### **Clean Architecture Implementation**
- **Presentation Layer** - UI components, ViewModels, and state management
- **Domain Layer** - Business logic and use cases
- **Data Layer** - Repository pattern and data sources
- **DI Layer** - Dependency injection with Koin

#### **State Management**
- **StateFlow** - Reactive state management
- **Event System** - Type-safe event handling
- **Error Handling** - Centralized error management
- **Loading States** - Consistent loading state management

### 📱 **Platform Support**
- ✅ **Android** - Full RevenueCat integration with Google Play
- ✅ **iOS** - Full RevenueCat integration with App Store

### 🧪 **Testing Support**
- **Dummy Data Generation** - Complete test data for development
- **Sample Screens** - Ready-to-use testing interfaces
- **Error Simulation** - Easy error state testing
- **Mock Products** - Test product configurations

### 📚 **Documentation**
- **Complete Setup Guide** - From RevenueCat account to app integration
- **Code Examples** - Real implementation examples from the template
- **Troubleshooting** - Common issues and solutions
- **Best Practices** - Recommended implementation patterns

### 🔗 **Dependencies Added**
- **RevenueCat KMP SDK** - Cross-platform in-app purchase SDK
- **Kotlinx Coroutines** - Asynchronous programming support
- **Koin DI** - Dependency injection for purchase components

### 🎯 **Migration Notes**
- No breaking changes from previous versions
- New purchase feature is completely optional
- Existing authentication and UI components remain unchanged
- All new components follow existing architectural patterns

### 📋 **Quick Start**
1. **Setup RevenueCat** - Follow the [RevenueCat Integration Guide](docs/revenue-cat.md)
2. **Configure Products** - Add subscription products in app stores
3. **Customize Features** - Update feature lists and subscription terms
4. **Test Purchase Flow** - Use sample screens for testing
5. **Enable Premium Features** - Use `PurchaseState.currentSubscribedProduct` for access control

---

## [0.1.0] - 2025-07-15

### 🎉 Initial Release
- **Kotlin Multiplatform** project setup with Android and iOS support
- **Compose Multiplatform** UI framework integration
- **Material 3** design system implementation
- **Authentication** system with Google Sign-In and email/password
- **Koin DI** dependency injection setup
- **Room Database** local data persistence
- **DataStore** preferences storage
- **Navigation** system with type-safe routing
- **Theme Management** with dark mode support
- **Event System** for global state management
- **Snackbar Controller** for notifications
- **Platform Utilities** for cross-platform development 