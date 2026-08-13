---
name: kmp-starter-core-rules
description: Non-negotiable rules for building on the KMP Starter Template — priority order, Golden Rule, terminology, state vs UI-state boundaries, visibility, demo removal, snackbars, and UI weight. Read before writing any code.
---

# Core Rules

## Priority order (Starter always wins)

1. Starter Template documentation (live site `https://starter.atherio.dev`)
2. Existing project structure
3. Existing feature implementations
4. Existing core modules (`features/core/*`)
5. General KMP / Compose best practices
6. General Android best practices

When these conflict, the higher item wins. Prefer consistency over creativity.

## Golden Rule

Before creating any new utility, helper, manager, abstraction, base class, datastore, file manager, navigation system, platform API, wrapper, or common component — **search the Starter Template first**. Reuse → extend → configure. Never duplicate.

## Terminology (Starter wins over other frameworks)

| Other frameworks call it | Rename to |
| --- | --- |
| Intent | Action |
| Effect | Event |
| Mutation | Action |
| UseCase | Logic |

Use `Action` for UI → ViewModel, `Event` for ViewModel → UI, `Logic` for use cases. Keep this naming everywhere.

## State guidelines

`State` holds **application data**, not transient UI state.

**Store in State:** user, files, notes, selected image bytes, loading, filters, repository data, search query, pagination.

**Never store in State:** dialog visibility, snackbar visibility, bottom-sheet visibility, dropdown expanded state, animation states, focus states. These live inside Composables.

## Visibility

Mark anything not required outside a module `internal`. Only expose the public API other modules need.

## Snackbars

Do not show a snackbar for every successful action. If the UI already communicates the result, skip it. Snackbars are for errors, important feedback, and undo actions.

## UI weight

Keep Composables lightweight — render state, send actions, observe events. Heavy work belongs in ViewModels or Logics.

## Remove demo content (only)

Remove Starter demo content: Welcome Screen, Starter File Manager demo, sample placeholder screens, unused demo resources. Keep the infrastructure; remove only demo content.
