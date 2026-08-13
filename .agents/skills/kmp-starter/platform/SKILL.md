---
name: kmp-starter-platform
description: Platform-specific behavior on the KMP Starter Template — the Platform class, logging, native iOS bindings, and SPM dependency integration.
version: 1
author: DevAtrii
license: MIT

---

# Platform

## Platform class

Use the `Platform` abstraction for platform-specific behavior (`com.kmpstarter.core.platform`).

```kotlin
if (platform.debug) { /* debug-only: verbose logging, lower remote-config interval, disable analytics */ }

val osVersion = platform.osVersion
val versionName = platform.appInfo.versionName
val appName = platform.appInfo.appName

if (platform.isAndroid) { /* Android */ }
if (platform.isIos) { /* iOS */ }

platform.ifAndroid { android -> /* Android-only */ }
platform.ifIos { ios -> /* iOS-only; ios.exactVersion.major/minor/patch */ }

if (platform.isDynamicColorSupported) { /* Android 12+ */ }
```

Prefer `Platform` over scattering raw `expect/actual` checks across the codebase.

## Logging

Use `com.kmpstarter.utils.logging.Log`:

```kotlin
Log.d(TAG, "Debug message")
Log.i(TAG, "Info message")
Log.w(TAG, "Warning message")
Log.e(TAG, "Error message")
Log.d(null, "uses default tag")
```

Pass `null` for the default tag; supports multiple items: `Log.d(TAG, "User:", userId, "Action:", name)`.

## Native iOS bindings (Swift → Kotlin)

Add Swift code in `starter/native/bindings/src/swift/interop/`. Classes/methods must be `@objc public`. Resync Gradle after adding. Call from `iosMain`:

```swift
@objc public class HelloKotlin: NSObject {
    @objc public static func sayHello(str: String) { print("Hello \(str)") }
}
```

```kotlin
import interop.HelloKotlin
HelloKotlin.sayHelloWithStr("DevAtrii")
```

## SPM dependencies (iOS)

Use the `spm-for-kmp` plugin. Configure per iOS target:

```kotlin
target.swiftPackageConfig(cinteropName = "interop") {
    dependency {
        val dep = libs.ios.someSwift.get()
        remotePackageVersion(url = uri(dep.group), products = { add(dep.name) }, version = dep.version!!)
    }
}
```

Manage versions/names in `gradle/libs.versions.toml`.

## Reference

- Docs: `https://starter.atherio.dev/fundamentals/03-platform-class/`, `https://starter.atherio.dev/fundamentals/10-logging/`, `https://starter.atherio.dev/fundamentals/04-native-bindings/`, `https://starter.atherio.dev/fundamentals/11-swift-package-manager/`
