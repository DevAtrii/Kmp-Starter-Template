---
comments: false
icon: lucide/log-in
---

# KMP App (Entry Point)

`initKmpApp()` is the global bootstrap for the KMP application.  
It initializes core SDK keys, Koin DI, billing, and remote config.

You can add your own platform-independent initializations inside this function (analytics, logging, crash reporting, etc.) to keep all startup logic in one place.


```kotlin title="composeApp/src/commonMain/kotlin/com/kmpstarter/core/InitKmpApp.kt"
fun initKmpApp(
...
) {
    ...
    initRevenueCat()
    initRemoteConfig()
    // add your own initializations here
}
```

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
