---
comments: false
icon: lucide/rocket
---

# Getting Started

## Requirements

- [x] Kotlin 2.3.10
- [x] Java 17
- [x] Gradle 9.0 or later
- [x] Android Studio Otter 2025.2.3 or later
- [x] KMP Plugin for Android Studio
- [x] Xcode 26 or later
- [x] Node.js 18+ (for the CLI)

!!! info "Android Studio Compatibility"
    Starter Template is compatible with Gradle 9.0, you can see list of android studio versions that supports gradle 9.0 <a href="https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility" target="_blank" rel="noopener noreferrer">
    here
    </a>

## Using the CLI

Start a project with the **KMP Starter CLI** ([`@devatrii/starter`](https://www.npmjs.com/package/@devatrii/starter)).

!!! warning "Alpha"
    The CLI is in **alpha**. It generates projects, but you may hit rough edges. If something breaks, please [open a GitHub issue](https://github.com/DevAtrii/Kmp-Starter-Template/issues).

### Install globally

```bash
npm install -g @devatrii/starter
```

This installs the `starter` and `kmp-starter` commands.

### Create a new project

Run interactively (prompts for app name, package, feature, modules, etc.):

```bash
starter create
```

Or pass options directly:

```bash
starter create \
  --name MyApp \
  --package com.example.myapp \
  --feature notes \
  --modules all
```

This generates a zip (and extracts it by default). Open the project in Android Studio, and open `iosApp/iosApp.xcodeproj` in Xcode to run on iOS.

### Other commands

```bash
# Create starter.json in an existing project
starter init

# Include a starter module into an existing project
starter include --module purchases

# Print CLI version
starter -v
```

On the next page, learn about all modules.

!!! note "Web wizard"
    A **web wizard** for generating projects in the browser is also planned. Until then, use the CLI.

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
