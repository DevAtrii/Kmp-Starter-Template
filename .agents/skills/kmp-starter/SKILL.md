---
name: kmp-starter-app
description: Build production Android & iOS apps on the KMP Starter Template (Clean Architecture, MVI, Koin, Compose Multiplatform). Use when scaffolding a NEW project via the CLI, or when creating/editing features, screens, modules, or any code inside an EXISTING Kmp-Starter-Template project, or when the user references KMP Starter or the Starter Template.
---

# KMP Starter App

Build and evolve apps **on top of** the KMP Starter Template. Do not reinvent its infrastructure.

> These skills are **project-agnostic** — usable in ANY project built on the Starter Template, not tied to the template repository itself. Paths below refer to the generated project structure (`composeApp/`, `features/`, `starter/`), where `<your-package>` is the app package you chose at scaffold time (e.g. `com.example.myapp`).

## When to use

This skill applies in two situations:

- **New project** — scaffold with the CLI (`starter create`, see docs below), then build features on the generated structure.
- **Existing project** — you are already inside a Kmp-Starter-Template project and need to add a feature, screen, module, or change existing code. Skip scaffolding; go straight to "Phase 1 — Understand the project", then implement.

## Documentation (browse the live site)

Docs are **not** guaranteed to be available locally. When implementation details are unclear, browse the live documentation site — do not assume a local `docs/` folder exists.

- Base URL: `https://starter.atherio.dev`
- Key pages:
  - `https://starter.atherio.dev/getting-started/` — CLI, requirements, adding modules
  - `https://starter.atherio.dev/modules/` — module map
  - `https://starter.atherio.dev/fundamentals/...` — architecture, DI, MVI, Platform, DataStores, Resources, Languages, Navigation, Reviews/Updates, Logging, SPM, Writing Code, File Manager
  - `https://starter.atherio.dev/features/...` — Core, Remote Config, Analytics, Database, Purchases
  - `https://starter.atherio.dev/customization/...` — Metadata, Theming
  - `https://starter.atherio.dev/ui/...` — Components, Utils, Layouts (and `/utils/` for non-UI utils)

Fetch a page with WebFetch (e.g. `https://starter.atherio.dev/getting-started/`) when you need the authoritative details for a concern.

## Load order

Read this file first, then read child skills as each concern arises:

| Child skill | Read when |
| --- | --- |
| [core-rules](core-rules/SKILL.md) | Always. Governs every decision. |
| [architecture](architecture/SKILL.md) | Before touching any feature/layer/module. |
| [mvi](mvi/SKILL.md) | Creating a ViewModel, State, Action, or Event. |
| [koin](koin/SKILL.md) | Registering dependencies or adding a module. |
| [navigation](navigation/SKILL.md) | Adding/registering a screen or nested nav. |
| [data](data/SKILL.md) | Repositories, Logics, DataStore, Room, files. |
| [resources-theme](resources-theme/SKILL.md) | Strings, locales, colors, typography, theme. |
| [ui](ui/SKILL.md) | Reusable UI components, UI utils, layouts. |
| [utils](utils/SKILL.md) | Non-UI Kotlin helpers (variables, time, JSON, FieldState, IntentUtils, Log, DataStore delegates). |
| [platform](platform/SKILL.md) | Platform checks, logging, native/Swift bindings. |
| [starter-features](starter-features/SKILL.md) | Analytics, Remote Config, Purchases, Reviews, Updates, Onboarding. |
| [memory](memory/SKILL.md) | Read at session start; write after significant changes. |

## Priority order

When an implementation decision arises, follow this order. If they conflict, **Starter Template always wins**.

1. Starter Template documentation (live site `https://starter.atherio.dev`)
2. Existing project structure
3. Existing feature implementations
4. Existing core modules (`features/core/*`)
5. General KMP / Compose best practices
6. General Android best practices

## Golden Rule

Before creating any new utility, helper, manager, abstraction, base class, datastore, file manager, navigation system, platform API, wrapper, or common component:

**Search the Starter Template first.** If an equivalent exists — reuse it, extend it, or configure it. Do not duplicate existing infrastructure.

## Memory (persistent per-project)

Keep persistent memory per project under `{skill}/.skill-storage/{project}/` (`memory.md`, plus optional `structure.md`, `decisions.md`, `progress.md`). See the [memory](memory/SKILL.md) child skill for the full system.

- At the **start** of a session, read `.skill-storage/{project}/memory.md` before exploring code.
- After significant changes or at the **end** of a task, write/update memory.

## Phase 1 — Understand the project first

Do not write code until you have:

- [ ] Explored the module structure (`settings.gradle.kts`)
- [ ] Understood navigation (`features/navigation`, `composeApp/core/navigation`)
- [ ] Understood `features/*` layout (data / domain / presentation)
- [ ] Understood `features/core*` (shared foundation)
- [ ] Read existing implementations (e.g. `features/core/*` onboarding slice)
- [ ] Confirmed whether Starter already solves the problem

## Implementation workflow

For every feature:

1. Understand existing implementation.
2. Design data model.
3. Design repository.
4. Implement data layer.
5. Register DI (Koin).
6. Create Logics (only if meaningful).
7. Create ViewModel.
8. Create State.
9. Create Actions.
10. Create Events.
11. Build UI (Screen + Content composables).
12. Wire navigation.
13. Test integration.

## Final checklist

- [ ] Starter architecture followed
- [ ] Existing infrastructure reused, no duplicated utilities
- [ ] MVI correct (State / Action / Event)
- [ ] Koin used, modules registered in `InitKoin`
- [ ] Starter navigation used
- [ ] Starter resources used, strings externalized, no hardcoded colors
- [ ] DataStore used for persistence
- [ ] `Platform` abstraction used for platform checks
- [ ] No UI-only state inside ViewModel
- [ ] Actions: UI → ViewModel; Events: ViewModel → UI (`ObserveAsEvents`)
- [ ] Logics only for meaningful business actions
- [ ] Feature package organization followed (screens, not technical categories)
- [ ] `internal` visibility where possible
- [ ] Heavy work outside Composables
- [ ] Feature modules contain no global navigation
- [ ] App compiles

## Source of truth

- Docs (live site, browse when needed): `https://starter.atherio.dev` — `getting-started/`, `modules/`, `fundamentals/*`, `features/*`, `customization/*`.
- Local docs (if present): `docs/fundamentals/*.md`, `docs/features/*.md`, `docs/customization/*.md`, `docs/getting-started.md`, `docs/modules.md`.
- Code (in the generated project): `composeApp/`, `features/core/*`, `features/navigation/`, `features/your-feature/*`, `starter/*`.
- Canonical feature slice to mirror: `features/core/` onboarding (repository → Logics → ViewModel → screen).
