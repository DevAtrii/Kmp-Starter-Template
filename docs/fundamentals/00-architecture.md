# Architecture

KMP Starter Template follows Clean Architecture principles:

- **Data Layer**: Actual implementations of `domain layer` like Repositories Impl and DataSources
- **Domain Layer**: aka Blurprint layer, contains UseCases (Logics), Repositories and Models
- **Presentation Layer**: Compose UI, States, Actions, Events, ViewModels & Interaction with `domain layer`

!!! info "Logics == UseCases"
    i just don't like the name `usecases` so i renamed it to `logics`, don't get confused when you see `logics` like:

    - `GetConfigLogic`
    - `SignInToPurchasesLogic`

    they are usecases, i just renamed them to `logics`.


## Core Module
There's a module inside `features` called `core` & this contains all 3 layers of `clean architecture`. Whenever you see `core` module in Starter Template it's mean it can be implemented by all it's siblings.

This is very important to understand that `core` feature should only be implemented by it's siblings or sibling's children. like all `features` can implement `core` module

`Core` module contains code that can be shared between modules such as:

- Shared Auth Logic for getting userId
- Onboarding
- Splash

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
