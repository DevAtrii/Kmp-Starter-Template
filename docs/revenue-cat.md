# RevenueCat Integration Guide

> **Note:** This documentation is AI-generated and provides a comprehensive guide for implementing RevenueCat in the KMP Starter template.

This guide will help you implement in-app purchases and subscriptions in your KMP Starter app using RevenueCat.

## 📋 Table of Contents

1. [What is RevenueCat?](#what-is-revenuecat)
2. [Prerequisites](#prerequisites)
3. [Setup RevenueCat Dashboard](#setup-revenuecat-dashboard)
4. [Configure Your App](#configure-your-app)
5. [Add Products & Offerings](#add-products--offerings)
6. [Implement in Your App](#implement-in-your-app)
7. [Testing](#testing)
8. [Troubleshooting](#troubleshooting)

## 🎯 What is RevenueCat?

RevenueCat is a service that simplifies in-app purchases and subscriptions. It handles:
- Cross-platform purchase management (iOS, Android, Web)
- Subscription analytics and insights
- A/B testing for pricing
- Customer management
- Revenue tracking

## ✅ Prerequisites

Before starting, make sure you have:
- [RevenueCat Account](https://app.revenuecat.com/) (Free tier available)
- Apple Developer Account (for iOS)
- Google Play Console Account (for Android)
- KMP Starter template project

## 🏗️ Setup RevenueCat Dashboard

### Step 1: Create RevenueCat Account

1. Go to [RevenueCat Dashboard](https://app.revenuecat.com/)
2. Sign up for a free account
3. Create a new project

### Step 2: Add Your Apps

1. **For iOS:**
   - Add your iOS app bundle ID (e.g., `com.yourcompany.yourapp`)
   - Upload your App Store Connect API key

2. **For Android:**
   - Add your Android package name (e.g., `com.yourcompany.yourapp`)
   - Upload your Google Play Console service account JSON

### Step 3: Get Your API Key

1. Go to **Project Settings** → **API Keys**
2. Copy your **Public API Key** (starts with `goog_` for Google Play or `appl_` for App Store)

## ⚙️ Configure Your App

### Step 1: Update App Constants

Open `composeApp/src/commonMain/kotlin/com/kmpstarter/core/AppConstants.kt`:

```kotlin
object AppConstants {
    // Replace with your actual RevenueCat API key
    const val REVENUE_CAT_API_KEY = "goog_YOUR_API_KEY_HERE"
    const val GOOGLE_WEB_CLIENT_ID = "your-google-client-id"
}
```

### Step 2: Initialize RevenueCat

The template already includes RevenueCat initialization in `InitRevenueCat.kt`:

```kotlin
fun initRevenueCat() {
    Purchases.logLevel = LogLevel.DEBUG
    Purchases.configure(
        PurchasesConfiguration.Builder(
            apiKey = AppConstants.REVENUE_CAT_API_KEY,
        ).build(),
    )
}
```

This is automatically called when your app starts.

## 🛍️ Add Products & Offerings

### Step 1: Create Products in App Stores

**For Google Play Console:**
1. Go to **Monetization** → **Products** → **Subscriptions**
2. Create subscription products (e.g., `premium_monthly`, `premium_yearly`)
3. Set pricing and billing periods

**For App Store Connect:**
1. Go to **Features** → **In-App Purchases**
2. Create subscription products with the same IDs as Google Play
3. Set pricing and subscription groups

### Step 2: Create RevenueCat Offerings

1. Go to **Products** → **Offerings** in RevenueCat
2. Create a new offering (e.g., "default")
3. Add your subscription products to the offering
4. Set one as the "current" offering

### Step 3: Configure Discount Offers (Optional)

To enable discount offers (like "50% off for new users"):

1. **Create Discount Offering:**
   - Go to **Products** → **Offerings**
   - Create a new offering (e.g., "discount_offer")
   - Add your discount products

2. **Add Custom Metadata:**
   - In your main offering, add metadata:
     - Key: `discountOffer` → Value: `discount_offer` (your discount offering ID)
   - In your discount offering, add metadata:
     - Key: `discountPercentage` → Value: `50.0` (discount percentage)

**Example JSON Metadata Format in currentOffer:**
```json
{
    "discountOffer": "discount_offer"
}
```

**Example JSON Metadata Format in discountOffer:**
```json
{
  "discountPercentage": "50.0"
}
```

The template automatically reads these metadata values in the `PurchaseViewModel`:
```kotlin
// Extract discount offer information from RevenueCat metadata
val discountOfferIdentifier = currentOffer.getMetadataString(
    key = DISCOUNT_OFFER_IDENTIFIER_KEY,
    default = ""
)
val discountOffer = offerings.all[discountOfferIdentifier]
val discountPercentage = discountOffer?.getMetadataString(
    key = DISCOUNT_OFFER_PERCENTAGE_KEY,
    default = "0.0"
)?.toDoubleOrNull() ?: 0.0
```

## 💻 Implement in Your App

### Step 1: Use the Purchase Screen

The template provides a complete purchase screen. Navigate to it:

```kotlin
// In your navigation
navController.navigate(PurchasesScreens.SubscriptionScreen)
```

### Step 2: Customize Features

Update the features list in your purchase screen:

```kotlin
val features = listOf(
    PurchaseFeature(
        icon = Icons.Default.Star,
        label = "Premium Features"
    ),
    PurchaseFeature(
        icon = Icons.Default.Code,
        label = "Advanced Tools"
    ),
    PurchaseFeature(
        icon = Icons.Default.Security,
        label = "Priority Support"
    )
)
```

### Step 3: Add Subscription Terms

```kotlin
val subscriptionTerms = listOf(
    "Auto-renewable subscription",
    "Cancel anytime",
    "Billed monthly/yearly",
    "No refunds for partial periods"
)
```

### Step 4: Handle Purchase Results

The template automatically handles:
- ✅ Purchase success/failure
- ✅ Restore purchases
- ✅ Loading states
- ✅ Error messages
- ✅ Discount offers

### Step 5: Enable Premium Features

Use the `PurchaseState` to check if user has an active subscription:

```kotlin
@Composable
fun YourScreen() {
    val viewModel: PurchaseViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    
    // Check if user has premium access
    val hasPremiumAccess = state.currentSubscribedProduct != null
    
    if (hasPremiumAccess) {
        // Show premium features
        PremiumFeaturesSection()
    } else {
        // Show upgrade prompt
        UpgradePromptSection()
    }
}

@Composable
fun PremiumFeaturesSection() {
    Column {
        Text("🎉 Premium Features Unlocked!")
        Text("• Unlimited storage")
        Text("• Custom themes")
        Text("• Priority support")
        Text("• Advanced analytics")
    }
}

@Composable
fun UpgradePromptSection() {
    Column {
        Text("Upgrade to Premium")
        Text("Get access to all features")
        Button(
            onClick = {
                // Navigate to purchase screen
                navController.navigate(PurchasesScreens.SubscriptionScreen)
            }
        ) {
            Text("Upgrade Now")
        }
    }
}
```

### Step 6: Handle Purchase Events

The template provides these purchase events:

```kotlin
// Get available products
viewModel.onEvent(PurchaseEvent.GetProducts)

// Start a purchase
viewModel.onEvent(PurchaseEvent.StartPurchase(selectedProduct))

// Restore previous purchases
viewModel.onEvent(PurchaseEvent.RestorePurchase)

// Listen to state changes
val state by viewModel.state.collectAsState()

// Check purchase completion
if (state.isPurchaseComplete) {
    // Purchase successful - unlock features
    showSuccessMessage("Welcome to Premium!")
}

// Check for errors
if (state.error.isNotEmpty()) {
    showErrorMessage(state.error)
}

// Check restore errors
if (state.restoreError.isNotEmpty()) {
    showErrorMessage("Restore failed: ${state.restoreError}")
}
```

## 🧪 Testing

### Step 1: Test Accounts

**Google Play:**
1. Add test accounts in Google Play Console
2. Use test accounts on your device

**App Store:**
1. Use Sandbox test accounts
2. Sign out of your real Apple ID on test device

### Step 2: Test Purchase Flow

1. Run your app
2. Navigate to purchase screen
3. Select a subscription
4. Complete purchase flow
5. Verify purchase success

### Step 3: Test Restore

1. Complete a test purchase
2. Uninstall app
3. Reinstall app
4. Use "Restore Purchases" button
5. Verify subscription is restored

## 🔧 Troubleshooting

### Common Issues

**"No products found" error:**
- ✅ Check your RevenueCat API key
- ✅ Verify products are added to offerings
- ✅ Ensure products are approved in app stores

**"Purchase failed" error:**
- ✅ Use test accounts
- ✅ Check app store configuration
- ✅ Verify product IDs match

**"Restore not working":**
- ✅ Complete a test purchase first
- ✅ Use same account for purchase and restore
- ✅ Check RevenueCat dashboard for customer info

### Debug Mode

The template includes debug logging. Check logs for:
- Product loading
- Purchase attempts
- Error messages
- Customer information

## 📚 Additional Resources

- [RevenueCat Documentation](https://docs.revenuecat.com/)
- [RevenueCat Dashboard](https://app.revenuecat.com/)
- [Google Play Console](https://play.google.com/console)
- [App Store Connect](https://appstoreconnect.apple.com/)

## 🎉 Next Steps

After implementing RevenueCat:

1. **Analytics:** Monitor purchases in RevenueCat dashboard
2. **A/B Testing:** Test different pricing strategies
3. **Customer Management:** Handle subscription changes
4. **Revenue Tracking:** Monitor your app's revenue

## 🔍 Understanding PurchaseState

The `PurchaseState` contains all the information you need to manage subscriptions:

```kotlin
data class PurchaseState(
    val isLoading: Boolean = false,                    // Loading state
    val products: List<StoreProduct> = listOf(),       // Available products
    val selectedProduct: StoreProduct? = null,         // Currently selected product
    val currentSubscribedProduct: StoreProduct? = null, // User's active subscription
    val discountProduct: StoreProduct? = null,         // Available discount product
    val discountPercentage: Double = 0.0,              // Discount percentage
    val isPurchaseComplete: Boolean = false,           // Purchase success flag
    val restoreError: String = "",                     // Restore error message
    val error: String = "",                            // General error message
)
```

**Key Properties:**
- `currentSubscribedProduct != null` → User has active premium
- `isPurchaseComplete == true` → Purchase just completed
- `discountProduct != null` → Discount offer available
- `isLoading == true` → Purchase operation in progress

## 💡 Tips

- **Start Simple:** Begin with one subscription tier
- **Test Thoroughly:** Always test on both platforms
- **Monitor Analytics:** Use RevenueCat's built-in analytics
- **Handle Edge Cases:** Plan for network issues and errors
- **User Experience:** Make purchase flow smooth and clear

---

**Need Help?** Check the [RevenueCat Community](https://community.revenuecat.com/) or [KMP Starter Issues](https://github.com/DevAtrii/Kmp-Starter-Template/issues). 