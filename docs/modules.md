---
comments: false
icon: lucide/layers
---

# Modules

Starter templates follow multimodule architecture. each feature has its own module. this helps in keeping the codebase organized and makes it easier to maintain.

## Structure

whenever you feel lost just come and look at this beautiful table

| Module | Description | Path |
| --- | --- | --- |
| `androidApp` | contains android app code | `androidApp/` |
| `composeApp` | contains compose multiplatform shared code, glue all other modules | `composeApp/` |
| `analytics` | contains `data`, `domain` & `presentation` submodules related to analytics | `features/analytics/*` |
| `core` | contains `data`, `domain` & `presentation` submodules, put code that you want to share between all features | `features/core/*` |
| `database` | room database setup, `migrations` & `entities` etc | `features/database` |
| `navigation` | contains navigation3 related code such as `navigator`, `backstack`, `screens` etc | `features/navigation` |
| `notifications` | contains `core`, `local` & `push` submodules - setup for notifiations using `alarmee` library | `features/notifications/*` |
| `purchases` | contains `data`, `domain` & `presentation` submodules related to in app purchases | `features/purchases/*` |
| `remoteconfig` | contains `data`, `domain` & `presentation` submodules related to remote config | `features/remoteconfig/*` |
| `resources` | contains all the resources such as , `strings`, `images` etc, include other code related to resources such as localization, string res etc | `features/resources/*` |
| `your-feature` | start writing your code here | `features/your-feature/*` |
| `utils` | contains utility code that can be used throughout the app | `starter/utils/` |
| `core` | implements `utils`, contains core logic related to starter template | `starter/core/` |
| `bindings` | contains ios bindings, you can add ios specific bindings here | `starter/native/bindings//` |
| `utils` | implements `starter/core`, contains utilities related to ui (CMP) | `starter/ui/utils/` |
| `components` | implements `ui utils`, contains reusable ui components | `starter/ui/components/` |
| `layouts` | implements `components`, contains reusable ui layouts | `starter/ui/layouts/` |


### Starter Modules
Starter Modules are the backbone of the template, they are the modules that all features implement. if you want to include any module into your module you can use project accessors like:
``` kotlin
commonMain {
    implementation(projects.starter.core)
}
```
all other modules that a starter module depends on will also be automatically added

### Feature Modules
You can add or remove feature module based on your requirements. Later in each feature module documentation you will find how to add or remove it properly, Because each module has it's own `koin module` that needs to be removed from `initKoin` function inside `composeApp` module.

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
