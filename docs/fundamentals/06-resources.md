# Resources

The Starter Template provides a **centralized `resources` module** to store shared assets for your KMP project.
You can include it in any module by adding the dependency in your `commonMain` source set:

```kotlin title="build.gradle.kts"  
implementation(projects.features.resources)
```
This keeps all resources **centralized**, reduces duplication, and allows all modules to access the same assets consistently.

---

## Folder Structure

```text
features/resources/src/commonMain/composeResources/
│
├─ values/
│   └─ strings.xml
├─ values-en/
│   └─ strings.xml
├─ drawable/
│   └─ compose.xml
├─ font/
│   └─ CustomFont-Regular.ttf
│
└─ files/
    └─ any-other-resources  
```

For more information on managing multiplatform resources, check the official documentation: <a href="https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources-usage.html" target="_blank">Compose Multiplatform Resources Usage</a>


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
