---
comments: false
icon: lucide/dock
---

# Native Bindings

We already covered the **SPM plugin** on the previous page.
If you want to create **native iOS bindings**, you can do so by adding Swift code inside the `starter/native/bindings/src/swift/interop/` folder.

Example:

```swift title="starter/native/bindings/src/swift/interop/HelloKotlin.swift" 
import Foundation

@objc public class HelloKotlin: NSObject {
    
    @objc public static func sayHello(str: String) {
        print("Hello from Swift: \(str)")
    }
    
}
```

!!! note "Important"
    Make sure your classes and methods are public and annotated with `@objc`, like in the example above. This is required for Kotlin/Native interop.

---

## Sync Gradle

After adding or updating Swift bindings, resync Gradle:

```
File → Sync Project with Gradle Files
```

---

## Usage in `iosMain`

You can now call your Swift class directly from Kotlin:

```kotlin title="composeApp/src/iosMain/kotlin/.../Testing.kt"  
import interop.HelloKotlin

fun sayHello() {
    HelloKotlin.sayHelloWithStr("DevAtrii")
}
```

---

## Notes

* You can implement bindings in other modules, but it’s recommended to keep them inside the `starter/native/bindings/` module for simplicity.
* This allows seamless interaction between Swift and Kotlin in your KMP project.


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