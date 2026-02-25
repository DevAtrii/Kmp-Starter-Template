---
comments: false
icon: lucide/apple

---

# Swift Package Manager (SPM) for KMP

The Starter Template uses the <a href="https://spmforkmp.eu/" target="_blank">`spm-for-kmp`</a> plugin to integrate Swift Package Manager dependencies in Kotlin Multiplatform projects.
This is currently the **best alternative to the Cocoapods KMP plugin**, allowing you to:

* Add Swift packages directly to KMP targets
* Create Kotlin bindings for Swift/Objective-C libraries
* Export Objective-C libraries directly to Kotlin

For full documentation and advanced usage, open the <a href="https://spmforkmp.eu/" target="_blank">plugin site</a>  in a new tab.

---

## Basic Integration

```kotlin title="build.gradle.kts" 
@file:OptIn(ExperimentalSpmForKmpFeature::class)

import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.github.frankois944.spmForKmp.utils.ExperimentalSpmForKmpFeature

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.spm.kmp)
    ...
}
```

Define iOS targets and configure SPM packages:

```kotlin title="Kotlin Multiplatform Setup" 

listOf(
    iosArm64(),
    iosSimulatorArm64()
).forEach { target ->
    target.swiftPackageConfig(cinteropName = "interop") {
        dependency {
            val mixPanel = libs.ios.mixpanel.swift.get()
            remotePackageVersion(
                url = uri(mixPanel.group),
                products = { add(mixPanel.name) },
                version = mixPanel.version!!,
            )
        }
    }
}
```

---

## Recommended: Use `libs.versions.toml`

To manage SPM versions and names consistently:

```toml title="libs.versions.toml"  
[versions]
# iOS dependencies via spm-for-kmp
ios-mixpanel-swift = "5.2.0"

[libraries]
ios-mixpanel-swift = { name = "Mixpanel", group = "https://github.com/mixpanel/mixpanel-swift.git", version.ref="ios-mixpanel-swift" }
```

This ensures you have a single source of truth for all iOS dependencies across KMP modules.

---

## Notes

* You can add multiple SPM packages using the same `swiftPackageConfig` block.
* Use Kotlin bindings to interact with Swift code seamlessly.
* Export Objective-C libraries directly to Kotlin if needed.

This setup keeps iOS dependencies clean, versioned, and fully integrated with your KMP project.


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