---
name: kmp-starter-create-fresh-app
description: Research and plan a brand-new app before scaffolding it on the KMP Starter Template. Gather project info, research the market (stores, Reddit, ASO tools), finalize features + value proposition, get user approval, and persist everything to memory.
author: DevAtrii
license: MIT

---

# Create a Fresh App (Research → Plan → Approve)

Use when the user wants to **create a brand-new app** (or just **research/validate an app idea**). Do the thinking and market research *before* scaffolding with the CLI or writing code.

Do **not** jump straight into `starter create` or feature building. Research first, get approval, then proceed.

## When to use

- User says "create an app for X", "I want to build a … app", "make me a …".
- User asks to research/validate an app idea or a niche.
- User asks "is this app idea good?" / "what features should my app have?".

## Phase 0 — Collect project information

Build this profile. If the user already gave some of it, fill it in; ask for the rest.

```markdown
## Project Information

**App Name:** {{App Name}}
**Short Description:** {{Short Description}}
**Primary Feature:** {{Primary Feature}}
**Sub Features:** {{Sub Features}}
**Detailed Description:** {{Detailed Description}}
**Target Audience:** {{Target Audience}}
```

**Ask the user** for anything missing (name, what it does, core feature, audience). Keep it lightweight — don't interrogate; a rough one-liner is enough to start.

## Phase 1 — Research (if info is thin)

If the idea is vague or you need to validate direction, research before proposing features. Use the tools available:

| Source | Tool | What it gives you |
| --- | --- | --- |
| Web search | WebSearch / WebFetch | Market size, trends, competitors, user complaints |
| Play Store / App Store | browser tool, WebFetch store pages | Competitor features, reviews, ratings, screenshots |
| Reddit / forums | WebSearch / WebFetch | Real user pain points, complaints, feature requests |
| ASO / keyword research | ASO MCP tools (`inspect_keyword`, `run_niche_analysis`, `run_autocomplete`, `top_charts`, `list_competitors`) | Search demand, keyword difficulty, niche opportunity, competitor rankings |
| Deep research | research MCP tools (e.g. Gemini deep research) | Structured synthesis |

Always research **competitors** and **user pain points** — those drive the value proposition more than the raw idea.

### ASO tools (if available)

If the user has an ASO provider (e.g. `applyra`) connected, use it:

- `run_niche_analysis(topic=..., store, country, lang)` — discover keyword clusters + opportunity scores + an app concept suggestion.
- `inspect_keyword(keyword, store, country, lang)` — difficulty, traffic, KEI, top-20 ranking apps, related keywords.
- `run_autocomplete(prefix, ...)` — what users actually type in store search.
- `top_charts(store, country, category, collection)` — see the leaderboard + gaps.
- `list_competitors` / `add_competitor` — track and compare against rivals.

**Save the extracted keywords + ASO findings into memory** (see Phase 3) so they're available when the app is built and published.

## Phase 2 — Reason & propose

From the research, produce a clear proposal. Reason about:

1. **Problem** — what pain does it solve?
2. **Target audience** — who, concretely (not "everyone").
3. **Value** — why is this better than what exists? The differentiator.
4. **Features** — finalized list, split into:
   - **MVP / primary** (must ship first)
   - **Secondary / later** (sub-features, growth)
5. **Monetization** (if relevant) — subscription, one-time, ads, freemium.

Present the final answer to the user as:

- **What you researched** (sources, competitors, pain points, ASO keywords).
- **Features finalized** (primary vs sub).
- **Value it brings** (problem → solution → why it wins).

**Stop and wait for approval.** Do not scaffold or write code until the user confirms.

## Phase 3 — Persist to memory

After approval (or even before, once research is done), save everything using the [memory](../memory/SKILL.md) system under `{skill}/.skill-storage/{project}/`:

- `memory.md` — the "Project Information" profile, goal, target audience, finalized features, value proposition.
- `decisions.md` — feature priority decisions + why.
- If ASO was run, save a dedicated section/file (e.g. `aso.md`) with **keywords**, difficulty/traffic scores, niche clusters, competitor list, and the chosen app concept.

Derive the `{project}` slug from the app/package name (see the memory skill). Never store secrets/keys.

Then, when the user is ready to build, return to the parent skill flow: `starter create` → then "Phase 1 — Understand the project".

## Rules

- Never scaffold/write code before approval.
- Don't invent competitor facts — research, don't assume.
- Keep the target audience concrete and the MVP scoped small.
- Save all research + ASO output to memory; don't redo it later.

## Reference

- Parent flow: `../SKILL.md` (scaffold + build phases).
- Memory system: `../memory/SKILL.md`.
- ASO (if connected): the user's ASO MCP tools.
