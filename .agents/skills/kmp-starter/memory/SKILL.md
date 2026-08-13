---
name: kmp-starter-memory
description: Persistent per-project memory for KMP Starter Template apps. Use when starting work on a project (read memory first), when learning the project structure, or after making significant changes (write/update memory).
---

# Memory

Keep a lightweight, persistent memory of each project so future sessions can resume without re-exploring everything.

## Storage location

Store memory inside the skill directory, keyed by project:

```
{skill}/.skill-storage/{project}/
    memory.md        # REQUIRED — core memory (see below)
    structure.md     # optional — module / feature / screen layout
    decisions.md     # optional — key decisions made + why
    progress.md      # optional — current status, blockers, next steps
```

- `{skill}` = the directory containing this `SKILL.md`.
- `{project}` = a stable, filesystem-safe slug for the project (lowercase, hyphens). Derive from the app/package name (e.g. `com.example.myapp` → `myapp`, or the app name `notes-app`). Use the **same slug** every session for the same project.

## When to read

At the start of any session on a project:

1. Compute the `{project}` slug.
2. If `{skill}/.skill-storage/{project}/memory.md` exists, read it **before** exploring code.
3. Read `structure.md` / `decisions.md` / `progress.md` only when relevant to the task.

If memory does not exist, create it after "Phase 1 — Understand the project".

## What to store in `memory.md`

Keep it concise and factual. Use this template:

```markdown
# Project Memory — {App Name}

## What it is
[One or two sentences describing the app and its purpose.]

## Goal
[What the app is trying to achieve / deliver.]

## Target audience
[Who it is for.]

## Special requirements
- [Any non-default constraints, integrations, backend, privacy, platforms, languages.]

## Key facts
- Package: `{your-package}`
- Features enabled: [database / purchases / analytics / remote config / notifications / locale ...]
- Min SDK / iOS target: ...

## Current state
[What is built, what is in progress, known gaps.]
```

Add/update sections only as needed. Do not dump entire file trees; summarize.

## What to store in `structure.md`

A high-level map of the code you actually depend on:

- Module layout (`settings.gradle.kts` includes, notable `features/*`).
- Which screens exist and their route classes (e.g. `AppScreens.Home`).
- Which feature slices exist and where (data / domain / presentation).
- Any custom infrastructure that was added (not part of Starter).

## What to store in `decisions.md`

One line per non-obvious decision: what was chosen and why (e.g. "used `StarterFileManager` cache instead of Downloads because files are temporary", "did not add a Logic for X because it's a one-line repository call").

## When to write / update

Write or update memory when:

- A new feature, screen, or module is added/removed.
- A significant architectural decision is made.
- Project metadata changes (package, enabled features).
- The task ends with unfinished work (record it in `progress.md`).

Keep updates small and additive. Prefer editing an existing file over creating many files.

## Rules

- Never store secrets, API keys, or tokens in memory files.
- Keep `{project}` slug stable across sessions.
- Memory is a convenience, not a source of truth — always verify against the actual codebase when a decision matters.
