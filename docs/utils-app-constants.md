# AppConstants

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/AppConstants.kt`

Centralized constants for the app.

```kotlin
object AppConstants {
    const val IS_DEBUG = true // set false in prod
    const val REVENUE_CAT_API_KEY = "goog"
    const val GOOGLE_WEB_CLIENT_ID = "...apps.googleusercontent.com"
}

expect val APPSTORE_URL: String
```

- `IS_DEBUG`: Feature toggles and logging gate. Switch to `false` for production builds.
- `APPSTORE_URL`: Platform-specific value provided per target.
