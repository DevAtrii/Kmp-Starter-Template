---
name: kmp-starter-native-app
description: Build a KMP Starter app with NO shared UI — Compose on Android, native SwiftUI on iOS. Create an ios-runtime module exported to the iOS app via SKIE, start Koin once in Swift, and build previewable MVI SwiftUI screens.
author: DevAtrii
license: MIT

---

# Native App (No Shared UI)

Use when the user wants **separate UIs per platform**: Compose Multiplatform on Android, **native SwiftUI** on iOS. Shared code is still KMP — domain, data, database, DI — but there is **no shared UI** between Android and iOS.

- **Android** — uses the existing `composeApp` module as-is.
- **iOS** — native SwiftUI app, backed by a thin `ios-runtime` Kotlin module that exports shared logic (no Compose).

## Architecture overview

```
shared (KMP):  starter/*  features/*  (data + domain + database — NO presentation/Compose)
      │                              │
      ▼                              ▼
composeApp (Android, Compose)    ios-runtime (KMP framework)  →  iosApp (SwiftUI)
```

The iOS binary must **not** contain any Compose UI code. Only domain/data/database modules are linked into the iOS framework.

## 1. Create the `ios-runtime` module

Add a new Gradle module `ios-runtime` (KMP, iOS targets only) and register it in `settings.gradle.kts`:

```kotlin
include(":iosRuntime")
```

It should:
- Target `iosArm64()` + `iosSimulatorArm64()` with a `framework { baseName = "iosRuntime"; isStatic = true }`.
- Depend on the **non-UI** modules only: `starter/core`, `features/core/domain`, `features/core/data`, `features/database`, `features/your-feature/domain`, `features/your-feature/data`, `starter/utils`, etc. **Do not** include `composeApp`, `starter/ui/*`, or any `*-presentation` module.
- Export those dependencies (`api(...)`) so their types surface in the generated Swift API.

### Apply SKIE

Add the [SKIE](https://github.com/touchlab/SKIE) Gradle plugin to the `ios-runtime` module (the module that builds the Xcode framework). It enhances the Kotlin→Swift export (Flow interop, better enums/optionals/suspending functions).

```kotlin
plugins {
    // ...
    id("co.touchlab.skie") version "0.10.14"
}
```

Disable SKIE analytics:

```kotlin
skie {
    analytics {
        enabled.set(false)
    }
}
```

> The plugin must be applied only in the module(s) that create the Xcode framework. It instruments everything exported in that framework (including dependency types). See [SKIE installation](https://skie.touchlab.co/installation) and [configuration](https://skie.touchlab.co/configuration).

## 2. Wire the Xcode build phase to `iosRuntime`

Update the "Run Script" / `embedAndSignAppleFrameworkForXcode` build phase in **all iOS targets** so the Swift app imports `iosRuntime` instead of `ComposeApp`:

```
cd "$SRCROOT/.."
./gradlew :iosRuntime:embedAndSignAppleFrameworkForXcode
```

Then in Swift:

```swift
import iosRuntime
```

## 3. Bootstrap — start Koin once

Create a thin bootstrap object (like `SharedRuntime`) in `ios-runtime` (`.../shared/SharedRuntime.kt`). It starts Koin once and exposes repositories/logics to Swift.

Key rules for Swift-friendliness (learned from a real Starter project):
- **Start Koin once**, guard with `@Volatile` so repeated calls are no-ops.
- Kotlin **default params are invisible to Swift** — expose no-arg wrappers and CSV string factories instead of Kotlin `List`/`Map` constructors.
- Prefer **primitive/`String`/`ByteArray`** return types over complex Kotlin generics where possible.

```kotlin
@OptIn(ExperimentalStarterApi::class)
object SharedRuntime {
    @Volatile private var started = false

    fun start(config: KoinAppDeclaration? = null) {
        if (started) return
        startKoin {
            config?.invoke(this)
            modules(
                utilsModule,
                databaseModule,
                coreDataModule,
                coreDomainModule,
                yourFeatureDataModule,
                yourFeatureDomainModule,
            )
        }
        started = true
    }

    fun isDebug(): Boolean = platform.debug
    fun platform(): Platform.Ios = platform as Platform.Ios

    fun yourRepository(): YourRepository = KoinPlatform.getKoin().get()
    fun yourLogic(): YourLogic = KoinPlatform.getKoin().get()

    // Swift-friendly factory: CSV strings instead of Kotlin lists
    fun makeThing(name: String, tagsCsv: String, count: Int): Thing =
        Thing(name = name, tags = tagsCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() })
}
```

In `iosApp`/`iOSApp.swift`, start Koin **once** at launch (in `App.init()` or the `AppDelegate`):

```swift
import SwiftUI
import iosRuntime

@main
struct iOSApp: App {
    init() {
        SharedRuntime.shared.start(config: nil)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## 4. iOS screen structure — Screen + Root + ViewModel

Mirror the Compose `Screen` / `Content` split with native SwiftUI. Each feature folder holds:

```
FeatureX/
    HomeScreen.swift        # pure, previewable view
    HomeRootScreen.swift    # wires ViewModel + lifecycle + sheets/alerts
    HomeViewModel.swift     # @MainActor ObservableObject, MVI
```

### `HomeScreen` — dumb, previewable

Pure SwiftUI, primitives + closures only, no ViewModel. Add `#Preview` so it previews in Xcode:

```swift
import SwiftUI

struct HomeScreen: View {
    let greeting: String
    let items: [HomeItem]
    let isLoading: Bool
    let onStart: () -> Void
    let onOpen: (Int) -> Void

    var body: some View {
        List {
            Section {
                Button(action: onStart) { Label("New", systemImage: "plus") }
            }
            Section {
                if isLoading {
                    ProgressView()
                } else if items.isEmpty {
                    ContentUnavailableView("Nothing here", systemImage: "tray")
                } else {
                    ForEach(items) { item in
                        Button { onOpen(item.id) } label: { Text(item.title) }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .navigationTitle(greeting)
    }
}

#Preview("Home") {
    NavigationStack {
        HomeScreen(
            greeting: "Hello, Jamie",
            items: [HomeItem(id: 1, title: "First")],
            isLoading: false,
            onStart: {},
            onOpen: { _ in }
        )
    }
}
```

### `HomeViewModel` — MVI (State / Actions / Events)

`@MainActor final class ... : ObservableObject`. Follow the Starter MVI semantics in Swift:

- **State** — `@Published private(set)` properties (the screen's state).
- **Actions** — methods the UI calls (`openEditor`, `save`, ...).
- **Events** — one-shot UI signals (presented via sheets/alerts bindings, or a published `event` + `ConsumedEvent` pattern).

```swift
import SwiftUI
import iosRuntime

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var greeting: String = "Hello"
    @Published private(set) var items: [HomeItem] = []
    @Published private(set) var isLoading = true
    @Published var errorMessage: String?

    private var observeTask: Task<Void, Never>?

    func start() {
        refresh()
        observeTask?.cancel()
        observeTask = Task { await observe() }
    }

    func stop() {
        observeTask?.cancel()
        observeTask = nil
    }

    func open(_ id: Int) { /* action */ }

    private func refresh() {
        let runtime = SharedRuntime.shared
        greeting = runtime.greeting()
    }

    private func observe() async {
        let repo = SharedRuntime.shared.yourRepository()
        for await data in repo.observeItems() {   // SKIE turns Flow into AsyncSequence
            if Task.isCancelled { return }
            items = data.map { HomeItem(id: Int($0.id), title: $0.title) }
            isLoading = false
        }
    }
}
```

### `HomeRootScreen` — wire it together

Owns the `@StateObject` ViewModel, calls `start()`/`stop()`, and presents sheets/alerts from ViewModel state:

```swift
struct HomeRootScreen: View {
    @StateObject private var viewModel = HomeViewModel()

    var body: some View {
        HomeScreen(
            greeting: viewModel.greeting,
            items: viewModel.items,
            isLoading: viewModel.isLoading,
            onStart: { viewModel.open(0) },
            onOpen: { viewModel.open($0) }
        )
        .task { viewModel.start() }
        .onDisappear { viewModel.stop() }
        .alert("Error", isPresented: Binding(
            get: { viewModel.errorMessage != nil },
            set: { if !$0 { viewModel.errorMessage = nil } }
        )) {
            Button("OK", role: .cancel) { viewModel.errorMessage = nil }
        } message: {
            Text(viewModel.errorMessage ?? "")
        }
    }
}
```

## Rules

- **No shared UI.** Android = `composeApp` (Compose), iOS = native SwiftUI.
- The iOS framework must export **no Compose** code — only data/domain/database modules.
- Apply SKIE only in the framework-building module (`ios-runtime`); disable its analytics.
- Start Koin **exactly once** in the iOS app entry point.
- Every screen gets a previewable, dumb `Screen` view + a `Root` that wires the ViewModel.
- Follow the **Apple Human Interface Guidelines** for SwiftUI views (native `List`, `NavigationStack`, `.insetGrouped`, `ContentUnavailableView`, SF Symbols, large titles).
- Keep MVI: `@Published` state, action methods, one-shot events via bindings.
- Bridge Swift-unfriendly Kotlin (default params, `List`/`Map`) with explicit wrappers in `SharedRuntime`.

## Reference

- SKIE: [https://github.com/touchlab/SKIE](https://github.com/touchlab/SKIE) · [installation](https://skie.touchlab.co/installation) · [configuration](https://skie.touchlab.co/configuration)
- MVI semantics: `../mvi/SKILL.md`
- Koin wiring: `../koin/SKILL.md`
- Feature slices (data/domain): `../architecture/SKILL.md`
