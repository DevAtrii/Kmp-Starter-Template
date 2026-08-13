---
name: kmp-starter-app
description: Build production Android & iOS apps on the KMP Starter Template (Clean Architecture, MVI, Koin, Compose Multiplatform). Use when scaffolding a NEW project via the CLI, or when creating/editing features, screens, modules, or any code inside an EXISTING Kmp-Starter-Template project, or when the user references KMP Starter or the Starter Template.
version: 1
author: DevAtrii
license: MIT
---

# KMP Starter App

Build and evolve apps **on top of** the KMP Starter Template. Do not reinvent its infrastructure.

> These skills are **project-agnostic** — usable in ANY project built on the Starter Template, not tied to the template repository itself. Paths below refer to the generated project structure (`composeApp/`, `features/`, `starter/`), where `<your-package>` is the app package you chose at scaffold time (e.g. `com.example.myapp`).

## When to use

This skill applies in two situations:

- **New project** — scaffold with the CLI (`starter create`, see docs below), then build features on the generated structure.
- **Existing project** — you are already inside a Kmp-Starter-Template project and need to add a feature, screen, module, or change existing code. Skip scaffolding; go straight to "Phase 1 — Understand the project", then implement.

## Documentation (search + read via script)

Docs are **not** guaranteed to be available locally. Use the bundled `search-docs.py` script to search, browse, and read the live docs — it caches `https://starter.atherio.dev/search.json` for 5 minutes under `.skill-storage/docs.json`, so you query offline after the first fetch. Run it from the skill directory:

```bash
python3 search-docs.py "koin module"                 # search docs by keywords
python3 search-docs.py "koin" --max 5                # limit result count
python3 search-docs.py "koin" --refresh              # force refetch, then search
python3 search-docs.py --get "koin/"                 # read a full page (all sections)
python3 search-docs.py --get "koin/#scopes"          # read a single section
python3 search-docs.py --sitemap                     # print full page -> section map
python3 search-docs.py --sitemap resource            # filter sitemap to paths matching "resource"
python3 search-docs.py --json "koin"                 # machine-readable JSON results
```

### How to use it

1. **Don't know where something lives?** Search keywords: `search-docs.py "resource accessor"`. It ranks by title → path → section → body text and prints readable snippets + URLs.
2. **Know the page but need details?** Read it whole: `search-docs.py --get "fundamentals/06-resources/"`.
3. **Exploring what's available?** Print the map: `search-docs.py --sitemap`, or narrow it: `search-docs.py --sitemap purchase`.
4. **Stale results?** The cache lasts 5 minutes; force a refresh with `--refresh` if you suspect the docs changed.
5. **Need raw data to script against?** Use `--json`.

Key page locations (use with `--get`):

- `getting-started/` — CLI, requirements, adding modules
- `modules/` — module map
- `fundamentals/...` — architecture, DI, MVI, Platform, DataStores, Resources, Languages, Navigation, Reviews/Updates, Logging, SPM, Writing Code, File Manager
- `features/...` — Core, Remote Config, Analytics, Database, Purchases
- `customization/...` — Metadata, Theming
- `ui/...` — Components, Utils, Layouts (and `utils/` for non-UI utils)

**Do not use WebFetch to browse the docs.** Always search/read through `search-docs.py`.

## Map the project (scan-project)

Before exploring a project by hand, run the bundled `scan-project.py` — it reads the project structure and writes a concise map for the memory system (see the [memory](memory/SKILL.md) skill).

```bash
python3 scan-project.py                          # scan cwd, derive project slug
python3 scan-project.py --root /path/to/app      # scan a specific project dir
python3 scan-project.py --project my-app         # explicit slug
python3 scan-project.py --print                  # print the map, don't write a file
python3 scan-project.py --json                   # machine-readable JSON
```

What it captures: module tree (`:starter/*`, `:features/*`, other), feature slices (data/domain/presentation), Navigation3 `NavKey` routes, wired Koin modules, and key versions from `libs.versions.toml`. Output goes to `{skill}/.skill-storage/{project}/structure.md`.

## Search the source code (search-code)

When you need to see how the Starter Template itself implements something (a class, function, `libs.versions.toml` entry), use `search-code.py` — it downloads the public repo (default: latest `main`) and searches its source. Do **not** rely on a local `starter/` folder in the generated project; it may not exist or be stale.

```bash
python3 search-code.py MviViewModel --types class           # find a class
python3 search-code.py onAction --types function --kdocs    # function + its KDoc
python3 search-code.py --types class --kdocs-depth 3        # class + members to depth 3
python3 search-code.py Foo --types all --relations          # parent/super/usage refs
python3 search-code.py --list-modules                       # modules from settings.gradle.kts
python3 search-code.py --get-version koin                   # version from libs.versions.toml
python3 search-code.py --get-library starter-core           # library coordinate
python3 search-code.py --get-plugin kotlin-multiplatform    # plugin coordinate
python3 search-code.py --list-versions                      # remote repo tags
python3 search-code.py --version 0.5.7 ...                  # pin a version (else latest)
python3 search-code.py MviViewModel --dir .                 # search the CURRENT project instead
python3 search-code.py onAction --dir /path/to/app          # search any local project dir
```

Key flags: `--types` (class/interface/object/function/property/typealias/enum/annotation/sealed/data/`all`), `--kdocs`, `--kdocs-depth 1..5|-1`, `--relations`, `--include-inheritor`, `--only-inheritor`, `--modules`, `--extensions` (default `kt,kts,toml`), `--in-body`, `--json`, `--clear-codes [V...]`. First run downloads the source (cached under `.skill-storage/codes/{version}/`); later runs are instant.

Use `--dir` (default `.`) to search the **current working project** source instead of the downloaded template — it ignores this skill's own files (`.agents/skills/kmp-starter/*`) so you only see project code.

## Determine the project's Starter version

To pin `search-code.py --version <v>` to the version a project was built with, resolve it from `starter.json` (project root, fields `packageName`, `starterVersion`, `mode`):

1. Read the project's `starter.json`.
2. If `mode` is `"LIB"` (lib mode) — read `gradle/libs.versions.toml` → `[versions]` → `starter = "x.x.x"`; that's the version.
3. Otherwise (module mode) — read `starterVersion` directly from `starter.json`.

Use that value as `search-code.py --version <v>`. Without `--version`, the script defaults to latest `main`.

## Update these skills (update-skills)

Keep the skills themselves up to date from the public repo. `update-skills.py` fetches the latest skill files, compares the parent `version` (frontmatter field in this file), and updates local files when the remote version is newer. Local-only state (`.skill-storage/`) is preserved.

```bash
python3 update-skills.py               # check + update if remote is newer
python3 update-skills.py --check       # report only, don't write
python3 update-skills.py --force       # update regardless of version
python3 update-skills.py --version 0.5.7   # pin a branch/tag (default main)
```

Version comparison reads `version:` from the top of `SKILL.md`; identical or older remote versions are ignored. Author/`license` are declared in each skill's frontmatter (`author: DevAtrii`, `license: MIT`).

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

- [ ] Run `python3 scan-project.py --project {slug}` to map the project
- [ ] Read `.skill-storage/{slug}/structure.md` (modules, features, nav, Koin)
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

- Docs (live site, query via `search-docs.py`): `https://starter.atherio.dev` — `getting-started/`, `modules/`, `fundamentals/*`, `features/*`, `customization/*`.
- Local docs (if present): `docs/fundamentals/*.md`, `docs/features/*.md`, `docs/customization/*.md`, `docs/getting-started.md`, `docs/modules.md`.
- Code (in the generated project): `composeApp/`, `features/core/*`, `features/navigation/`, `features/your-feature/*`, `starter/*`.
- Canonical feature slice to mirror: `features/core/` onboarding (repository → Logics → ViewModel → screen).
