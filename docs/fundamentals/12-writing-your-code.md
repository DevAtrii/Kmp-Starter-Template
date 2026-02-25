---
comments: false
icon: lucide/code

---

# Writing Your Own Code

Starter Template is project-agnostic. It does not care what app you are building — it simply provides a clean and scalable structure so you can start building immediately.

Since the template follows **Clean Architecture**, each feature is divided into:

* `data`
* `domain`
* `presentation`

If you are building a **Notes app**, you can convert `your-feature` into a `notes` feature and start implementing your logic.

---

## Feature Structure

You will find the placeholder module inside:

```
features/your-feature/
```

It contains:

```
data/
domain/
presentation/
```

Rename this module to match your feature and begin development.

---

## Refactoring (Recommended)

It is strongly recommended to properly rename `your-feature`.
The placeholder name has no meaning and should be replaced.

---

### Step 0 – Rename Module

Right-click on `your-feature`
→ `Refactor`
→ `Rename`

![Refactor Step 0](../assets/refactor/refactor-0.webp)

---

### Step 1 – Rename Directory

Select **Rename Directory**

![Refactor Step 1](../assets/refactor/refactor-1.webp)

---

### Step 2 – Enter Feature Name

Enter your feature name.

Example: `notes`

![Refactor Step 2](../assets/refactor/refactor-2.webp)

---

### Step 3 – Update settings.gradle.kts

Open `settings.gradle.kts` in the root directory and rename module references.

#### Before

```kotlin title="settings.gradle.kts" linenums="1" hl_lines="3-5"
...
/*Your Feature*/
include(":features:your-Feature:presentation")
include(":features:your-Feature:domain")
include(":features:your-Feature:data")
```

#### After

```kotlin title="settings.gradle.kts" linenums="1" hl_lines="3-5"
...
/*Your Feature*/
include(":features:notes:presentation")
include(":features:notes:domain")
include(":features:notes:data")
```

![Refactor Step 3](../assets/refactor/refactor-3.webp)

---

### Step 4 – Rename Across Project

`your-Feature` is referenced by other modules using project accessors.

Rename it everywhere as shown in the screenshot:

![Refactor Step 4](../assets/refactor/refactor-4.webp)

!!! note "Shortcuts"
    - Windows: ++ctrl+shift+r++
    - Mac: ++cmd+shift+r++

---

### Step 5 – Rename Package (Data Layer)

Right-click:

```
.../data/commonMain/.../feature_your_feature_data
```

→ `Refactor`
→ `Rename`

![Refactor Step 5](../assets/refactor/refactor-5.webp)

---

### Step 6 – Select All Directories

This is important.

![Refactor Step 6](../assets/refactor/refactor-6.webp)

---

### Step 7 – Enter New Package Name

Example:

```
feature_notes_data
```

![Refactor Step 7](../assets/refactor/refactor-7.webp)

---

### Step 8 – Rename Domain Layer

Repeat the same process for the `domain` layer.

![Refactor Step 8](../assets/refactor/refactor-8.webp)

---

### Step 9 – Rename Presentation Layer

Repeat the same process for the `presentation` layer.

![Refactor Step 9](../assets/refactor/refactor-9.webp)

---

### Done

You have successfully refactored the placeholder feature.

Now you can start writing your code inside:

* `data` → repositories, data sources
* `domain` → use cases, models
* `presentation` → UI, ViewModels, state

Follow Clean Architecture principles to keep your feature modular and scalable.

---

## Dependency Injection

Each layer contains a `di/` package for dependency injection.

You can:

* Rename the DI module to match your feature
* Add your repositories and use cases
* It is already included in `initKoin`

You can replicate this structure for every new feature you build.
