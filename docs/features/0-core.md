---
comments: false
icon: lucide/archive
---

# Core

The **Core** module contains shared logic used across multiple features.
It follows Clean Architecture and is divided into:

* `data`
* `domain`
* `presentation`

---

## What Goes in Core?

Add logic that needs to be reused across features, such as:

* Shared auth logic (e.g., getting `userId`)
* Common DataStores (user token)
* Base repositories or use cases
* Splash logic
* Onboarding flow
* Shared UI components
* Global helpers

If multiple features need it, it belongs in `core`.

---

## Dependency Rule

All features can depend on `core`, but `core` must never depend on features.

Correct:

```text
features/* → core
```

Wrong:

```text
core → features/*
```

---

## Example

If you create:

```text
features/notes/
```

Then:

* `notes/data` → can use `core/data`
* `notes/domain` → can use `core/domain`
* `notes/presentation` → can use `core/presentation`

---

Core is your shared foundation.
Features build on top of it.
