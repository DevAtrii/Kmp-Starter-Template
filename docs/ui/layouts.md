---
comments: false
icon: lucide/layout-grid
---

# Layouts

The `starter/ui/layouts` module provides **higher-level, reusable layout composables** — full-screen states like loading and empty states, plus a measurable layout that exposes the screen size.

They're built on top of `ui-utils` and `components`, so they stay theme-aware and consistent with the rest of the starter.

Add the dependency in your `commonMain` source set:

```kotlin title="build.gradle.kts"
implementation(projects.starter.ui.layouts)
```

---

## LoadingLayout

A full-screen overlay with a centered loading indicator and an optional dimmed background.

```kotlin
if (isLoading) {
    LoadingLayout()
}
```

It's fully customizable:

```kotlin
LoadingLayout(
    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    color = MaterialTheme.colorScheme.primary,
    onClick = { /* block taps behind */ },
    sizeModifier = Modifier.fillMaxSize(),
)
```

!!! note "Theme aware"
    The default background automatically resolves `LIGHT` / `DARK` / `SYSTEM` from `LocalThemeMode`, so the dimmed overlay always looks right.

---

## EmptyStateWithAction

A centered empty-state with a hero icon, title, description, and a call-to-action button. Perfect for empty lists, "no results", or onboarding gaps.

```kotlin
EmptyStateWithAction(
    title = "No files yet",
    description = "Upload your first file to get started.",
    buttonText = "Upload",
    heroIcon = Icons.Outlined.UploadFile,
    onClick = { /* navigate to upload */ },
)
```

Defaults to a search-off icon if you don't pass `heroIcon`.

---

## MeasurableLayout

An internal layout used to measure the available screen size and expose it via `LocalScreenSize`.

```kotlin
val size = LocalScreenSize.current   // Offset(width, height)
```

You rarely call `MeasurableLayout` directly — it powers layout measurement behind the scenes. Reach for it when you need raw available size in a custom layout.

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
