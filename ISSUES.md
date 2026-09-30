# Issues

## Purpose

Canonical registry of project issues.

Issues remain in this file after resolution so their history and stable IDs are preserved.

This file is not a task log or general project notes file.

## Status Definitions

- `OPEN` — issue is unresolved.
- `IN_PROGRESS` — work is actively addressing the issue.
- `BLOCKED` — issue cannot currently be resolved because of a dependency or missing decision.
- `CLOSED` — issue has been resolved and verified.

## Issue Registry

| Issue ID | Status | Severity | Title | Source | Related Feature | Resolution |
|---|---|---|---|---|---|---|
| `ISSUE-001` | BLOCKED | High | Developer has no write grant on any knowledge-migration target | TASK-005 Phase 0 | Knowledge system | Pending human grant |
| `ISSUE-002` | OPEN | High | Bottom-navigation composition is specified three different ways | TASK-005 C-01 | Home, Habits, navigation | Needs human ruling |
| `ISSUE-003` | OPEN | High | Activity field set: unified concept vs typed v4 schema | TASK-005 C-02 | Activity model, data | Needs human ruling |
| `ISSUE-004` | OPEN | High | Soft-delete (ARCHIVED) vs hard delete for habits | TASK-005 C-03 | Habits, data | Needs human ruling |
| `ISSUE-005` | OPEN | Medium | Streak visual treatment: rejected fire-gamification vs 🔥 glyph in specs | TASK-005 C-04 | Habits UI | Needs human ruling |
| `ISSUE-006` | OPEN | Medium | Aurora artwork: programmatic Compose vs bundled assets | TASK-005 C-05 | Home, Habits UI | Needs human ruling |
| `ISSUE-007` | OPEN | Medium | Calendar as navigation destination vs as route only | TASK-005 C-06 | Navigation, Insights | Needs human ruling |
| `ISSUE-008` | OPEN | Low | Recurrence type count: 9 kinds vs "all 8 recurrence types" | TASK-005 C-07 | Business rules, testing | Handled procedurally; confirm |
| `ISSUE-009` | OPEN | Low | `PRODUCT_SPEC_V0.md` is a byte-identical duplicate of `PRODUCT_SPEC.md` | TASK-005 C-08 | Documentation hygiene | Needs human decision |
| `ISSUE-010` | OPEN | Low | `PROTOTYPE_ROADMAP.md` is a stale prefix of `ROADMAP.md` | TASK-005 C-09 | Documentation hygiene | Needs human decision |
| `ISSUE-011` | OPEN | Low | `dashboard/README.md` names four files that do not exist | TASK-005 C-10 | Documentation hygiene | Needs human decision |
| `ISSUE-012` | OPEN | Low | Several `docs/` files cite stale intra-doc paths | TASK-005 C-11 | Documentation hygiene | Needs user-initiated fix |
| `ISSUE-013` | OPEN | Low | ADR-21 and the design system still self-declare as source of truth | TASK-005 C-12 | Authority model | Superseded by ADR-36; amendment pending |
| `ISSUE-014` | CLOSED | High | Home Hero: Aurora artwork dominates the foreground, ring/count/message not readable | TASK-006 issue 1 | Home Hero | Fixed + verified (TASK-006, Reviewer A4(c)) |
| `ISSUE-015` | CLOSED | High | Home Hero: visible seam/gap around the date/greeting area | TASK-006 issue 3 | Home Hero | Fixed + verified (TASK-006, Reviewer A4(d)/(e)) |
| `ISSUE-016` | CLOSED | High | Blank/solid band between status bar and Aurora Hero on Home | TASK-006 issue 7 | Home Hero, system bars | Not reproducible; guard retained + verified (TASK-006, Reviewer A4(a)) |
| `ISSUE-017` | CLOSED | High | Habits: top-band/status-bar regression introduced by the Home fix | TASK-006 issue 9 | Habits Hero, system bars | Fixed via ADR-39/§7.7 + verified (TASK-006, Reviewer A4(h)) |
| `ISSUE-018` | CLOSED | Medium | Excessive gap between Home Hero and "Remaining today" | TASK-006 issue 8 | Home layout | Fixed + verified (TASK-006, Reviewer A4(f)) |
| `ISSUE-019` | CLOSED | Medium | Reach a Target dialog too transparent to read against Home | TASK-006 issue 2 | Home, target completion | Fixed via ADR-38 + verified (TASK-006, Reviewer A4(g)) |
| `ISSUE-020` | OPEN | Low | Home date label (`textSecondary`) measures 3.11:1 in Light, below the 4.5:1 floor | TASK-006 Reviewer Finding 2 | Home Hero | Deferred — MINOR; remedy is the text token (task.md §7.5), needs separate task |
| `ISSUE-021` | OPEN | Medium | Fresh install reports no active theme in the Theme Gallery | TASK-007 | Theme Gallery | Needs human ruling |
| `ISSUE-022` | OPEN | High | Settings reduce-motion copy promises a whole-app effect it does not deliver | TASK-007 | Settings, motion | Needs human ruling |
| `ISSUE-023` | CLOSED | Medium | Orchestrator deliverable write grant missing in `opencode.json` | TASK-007 | Multi-agent infrastructure | Fixed + verified |
| `ISSUE-024` | CLOSED | Low | Unreferenced 5.4 MB design concept pack with no recorded provenance | TASK-007 | Repository governance | Resolved — first **retained** as non-runtime Phase-2 input (provenance limits recorded); **final disposition: the pack was REMOVED** — the exploratory concept assets are no longer required. Production Aurora assets untouched |
| `ISSUE-025` | CLOSED | High | Every colour hex and type `sp` in the Phase 2B Round 1 plan is derived, not quoted — the source document contains none | TASK-009 Architect disclosure | Phase 2B prototypes | **CLOSED as moot** (TASK-014/ADR-46): closure criterion was ratifying tokens *when a direction is selected*; the human ruled all four directions obsolete, so it is unselectable. Disclosure retained as historical record |
| `ISSUE-026` | CLOSED | Medium | Phase 2B prototype composables will ship in the release APK as unreachable dead code | TASK-009 Architect disclosure | Release build, Phase 2B | **CLOSED as resolved by removal** (TASK-014/ADR-46): the prototypes were deleted. Note the premise was invalidated earlier — the "accepted cost" had already ceased to exist at TASK-011 Phase F, when R8 (`isMinifyEnabled = true`) began stripping the 47 unreachable classes |
| `ISSUE-027` | CLOSED | Low | B renders `unitLabel` and `unitWord` together, so a row reads "Read 20 pages · p · pages" | TASK-009 Reviewer Finding 11 | Phase 2B prototype | **CLOSED as resolved by removal** (TASK-014/ADR-46): the defect lived only in `InstrumentHome.kt:217-230`, now deleted. Not "fixed" — the subject no longer exists |
| `ISSUE-028` | OPEN | Low | Migration-test v1 DDL fixture incomplete vs `schemas/<db>/1.json`; masked by package-scoped Robolectric LEGACY sqlite workaround | TASK-011 Phase A | Room migrations, unit tests | Fixture DDL fix pending (test source) |
| `ISSUE-029` | OPEN | High | Onboarding re-shows on every cold start — start destination resolved before first DataStore emission | TASK-011 Reviewer Finding 1 | Onboarding, navigation | Needs a loaded-state gate; not a TASK-011 regression |

## Issue Details

### ISSUE-001 — Developer cannot write any knowledge-migration target

- Issue ID: `ISSUE-001`
- Status: BLOCKED
- Priority: High
- Area: Agent infrastructure / permissions
- Feature: Knowledge system migration (TASK-005)
- Problem: `opencode.json` grants the Developer `edit` on `task.md` and `app/**`
  only. TASK-005 Phases 4-7 require writing 20 chunk files across 7 new
  `knowledge/` subdirectories, rebuilding `knowledge/index.md`, amending
  `AGENTS.md`, updating four `.opencode/agents/*.md` files, and adding
  back-references under `docs/**`. The Architect can write only `task.md` and
  `docs/architecture/decisions/DECISIONS.md`; the Reviewer only `task.md`; the
  Orchestrator only `task.md`, `PROJECT_STATUS.md` and `ISSUES.md`. No agent can
  perform the migration.
- Expected behavior: the Developer holds a write grant covering the
  documentation targets this task requires, and the human-readable matrix
  agrees with the technical config.
- Evidence: `opencode.json` `permission.edit` for `developer` is
  `{"*": "deny", "task.md": "allow", "app/**": "allow"}`. The Orchestrator's own
  `edit` of `docs/development/task_history/TASK-004_multi_agent_permissions.md`
  was refused with the rule list `edit: {"*": "deny", "task.md": "allow",
  "PROJECT_STATUS.md": "allow", "ISSUES.md": "allow"}`. The Architect
  independently reported the same blocker on completing Phases 1-3.
  `docs/architecture/MULTI_AGENT_PERMISSIONS.md` §3.1 (lines 87-90) declares
  `knowledge/**` read-only for all four agents, confirming the rule is intended
  policy, not an oversight in one file.
- Root cause: the permission matrix was sized for TASK-004, which touched only
  agent infrastructure. It has no documentation-migration grant, and the task
  rules forbid agents from widening their own permissions.
- Fix: PENDING HUMAN DECISION. Escalated in the TASK-005 report. Minimum grant
  to unblock: Developer `edit` on `knowledge/**`, `AGENTS.md`,
  `.opencode/agents/*.md`, `docs/**`. `opencode.json` and
  `docs/architecture/MULTI_AGENT_PERMISSIONS.md` §3.1 must be changed together so
  the technical and human-readable matrices agree. Constraints: preserve the
  `git commit*` / `git push*` denies, and keep `"*": "deny"` as the first key so
  last-match-wins cannot invert intent.
- Verification: Not verifiable until the grant exists. Verification will be: a
  Developer write to one `knowledge/` path succeeds, and `git diff` over
  `docs/` still shows no unauthorized change.
- Resolution: None.

### ISSUE-002 — Bottom-navigation composition specified three ways

- Issue ID: `ISSUE-002` (C-01)
- Status: OPEN
- Priority: High
- Area: UI / navigation
- Feature: Home dashboard, Habits, global navigation
- Problem: three incompatible bottom-nav compositions are documented.
  `MOMENTUM_DESIGN_SYSTEM_V1.md:281-294` and
  `HABITS_MANAGEMENT_UI_SPEC.md:122-133` specify
  `Home | Habits | Tasks | Insights` and state "Settings is NOT a
  bottom-navigation destination". `DASHBOARD_SPECS.md:48-59` and
  `dashboard/DECISIONS.md:39-51` specify `Home | Habits | Insights | Settings`.
  `ARCHITECTURE.md:80-84` lists `Today, Activities, Insights, Calendar, Settings`.
- Expected behavior: exactly one authoritative bottom-nav composition.
- Evidence: the four file/line citations above. Both sides are marked
  `Finalized` / `Locked`. `ADR-22` and `ROADMAP.md:72-74` assert the
  Tasks variant as "locked".
- Root cause: `DASHBOARD_SPECS.md` §3 and `dashboard/DECISIONS.md` decision 2
  were never superseded when the design system adopted the Tasks variant, and
  no ADR records the supersession.
- Fix: human ruling required. Migrating either variant into an authoritative
  chunk would freeze a guess.
- Verification: not started.
- Resolution: None. Knowledge chunks will not state a composition; the affected
  chunk carries a `CONFLICT (C-01)` block instead.

### ISSUE-003 — Activity field set: unified concept vs typed v4 schema

- Issue ID: `ISSUE-003` (C-02)
- Status: OPEN
- Priority: High
- Area: Domain model / data
- Feature: Activity model
- Problem: `PRODUCT_SPEC.md:32-48` and `ARCHITECTURE.md:43-49` describe the v1
  Activity without `activityType`, `habitIntent`, `dueTime` or
  `habitTemplateId`, and `PRODUCT_SPEC.md:13-30` states "one unified concept …
  not separate systems". `DATA_MODEL.md:16-43` defines all four columns and
  cites ADR-19 and ADR-20.
- Expected behavior: one authoritative v1 Activity field set.
- Evidence: the citations above. `DATA_MODEL.md` is the newer, schema-authoritative
  document, but `PRODUCT_SPEC.md` is the product authority under a different
  `KNOW-*` ID.
- Root cause: the product spec was never amended after the v4 schema change.
- Fix: human ruling required. Amending a product specification is a product
  decision, not a documentation cleanup.
- Verification: not started.
- Resolution: None. `KNOW-BUSINESS-001` carries the product-spec field table;
  `KNOW-BUSINESS-TYPES-001` carries the v4 type model; both carry a
  `CONFLICT (C-02)` block.

### ISSUE-004 — Soft-delete (ARCHIVED) vs hard delete for habits

- Issue ID: `ISSUE-004` (C-03)
- Status: OPEN
- Priority: High
- Area: Data / business rules
- Feature: Habits lifecycle, data retention
- Problem: `AGENTS.md` §7, `DATA_MODEL.md:80-84`, `PRIVACY_AND_SECURITY.md:25-26`,
  `PRODUCT_SPEC.md:48` and ADR-25 all require soft delete via `ARCHIVED`, and
  `DATA_MODEL.md:81` states hard delete "needs ADR".
  `HABITS_MANAGEMENT_DECISIONS.md:36-46` states "There is no Archive concept in
  the Habit management model", and `ROADMAP.md:80` says "Hard delete; no
  archive".
- Expected behavior: one deletion semantic, or an explicit ADR scoping a
  Habits-specific exception.
- Evidence: the citations above. Both sides are marked `Finalized`.
- Root cause: the Habits feature decision was written without an ADR scoping it
  as an exception to the data model.
- Fix: human ruling required.
- Verification: not started.
- Resolution: None. `KNOW-DATA-002` and `KNOW-FEAT-HABITS-LIFECYCLE-001` both
  carry a `CONFLICT (C-03)` block.

### ISSUE-005 — Streak visual treatment

- Issue ID: `ISSUE-005` (C-04)
- Status: OPEN
- Priority: Medium
- Area: UI
- Feature: Habits UI
- Problem: `MOMENTUM_DESIGN_SYSTEM_V1.md:486-496` and `:740-762` prefer a
  "21 day streak" treatment and explicitly reject fire-streak gamification and
  emoji used as UI. `HABITS_MANAGEMENT_UI_SPEC.md:208-213` and
  `HABITS_MANAGEMENT_DECISIONS.md:517-522` render `🔥 12 day streak`.
- Expected behavior: one streak treatment.
- Evidence: the citations above. The design system is `Locked`; both feature
  documents are `Finalized`.
- Root cause: the 🔥 glyph is most likely a stale illustration that was never
  removed from the specs, but that has never been confirmed.
- Fix: human ruling required. Propagating the glyph would place an explicitly
  rejected pattern on the authoritative path.
- Verification: not started.
- Resolution: None. The Habits streak chunk carries a `CONFLICT (C-04)` block
  and does NOT propagate the glyph.

### ISSUE-006 — Aurora artwork: programmatic vs bundled

- Issue ID: `ISSUE-006` (C-05)
- Status: OPEN
- Priority: Medium
- Area: UI / assets
- Feature: Home, Habits UI
- Problem: `MOMENTUM_DESIGN_SYSTEM_V1.md:611-640` and `:717-736` require Aurora
  to be "generated in Compose wherever practical".
  `HABITS_MANAGEMENT_UI_SPEC.md:26-33` requires two bundled images "used exactly
  as supplied … No programmatic/generated artwork", and ADR-30 through ADR-35
  implement bundled artwork.
- Expected behavior: one Aurora strategy, or an explicit scope split.
- Evidence: the citations above. ADR-30 to ADR-35 are the newest records, but
  the `Locked` design system was never amended.
- Root cause: the ADR trail moved to bundled artwork without updating the
  design-system text that `KNOW-UI-AURORA-001` would inherit.
- Fix: human ruling required.
- Verification: not started.
- Resolution: None. The Aurora chunk carries a `CONFLICT (C-05)` block.

### ISSUE-007 — Calendar as destination vs route

- Issue ID: `ISSUE-007` (C-06)
- Status: OPEN
- Priority: Medium
- Area: UI / navigation
- Feature: Navigation, Insights
- Problem: `MOMENTUM_DESIGN_SYSTEM_V1.md:578-607` and `:740-762` state Calendar
  is "not a separate navigation destination". `ARCHITECTURE.md:80-84` lists
  `Calendar` in the `NavHost`, and `ROADMAP.md:52-53` records a shipped
  Calendar screen.
- Expected behavior: disambiguated vocabulary for "destination".
- Evidence: the citations above. The two sources likely mean bottom-nav tab
  versus route/screen, but no document says so.
- Root cause: the word "destination" is used ambiguously across the two
  documents and was never disambiguated.
- Fix: human ruling required. Entangled with `ISSUE-002`.
- Verification: not started.
- Resolution: None.

### ISSUE-008 — Recurrence type count

- Issue ID: `ISSUE-008` (C-07)
- Status: OPEN
- Priority: Low
- Area: Business rules / testing
- Feature: Recurrence, testing strategy
- Problem: `PRODUCT_SPEC.md:50-62` defines 9 recurrence kinds and
  `DATA_MODEL.md:32` defines 9 tags, while `ROADMAP.md:27-29` and
  `TESTING_STRATEGY.md:10` refer to "all 8 recurrence types".
- Expected behavior: a single authoritative count, or an explicit statement that
  `custom` is a reserved extension point excluded from the count.
- Evidence: the citations above. `PRODUCT_SPEC.md:62` calls `custom` a "reserved
  extension point", which plausibly explains 9 vs 8, but no document states this.
- Root cause: a count was asserted in prose rather than derived from the enum.
- Fix: handled procedurally for now — knowledge chunks enumerate the recurrence
  rule kinds and deliberately state no count, so the ambiguity cannot propagate
  into an authoritative chunk. Human confirmation requested.
- Verification: the Reviewer must confirm no migrated chunk asserts a count.
- Resolution: None.

### ISSUE-009 — Duplicate product spec

- Issue ID: `ISSUE-009` (C-08)
- Status: OPEN
- Priority: Low
- Area: Documentation hygiene
- Feature: Product specification
- Problem: `docs/product/PRODUCT_SPEC.md` and
  `docs/product/prototype/PRODUCT_SPEC_V0.md` are byte-identical (both 4,966
  bytes, SHA-256
  `1692141675B6B55B848BA7076627E9301E1A777671694A125C149070F1EB00EB`) and both
  present as the product specification.
- Expected behavior: one canonical product specification path.
- Evidence: identical size and hash. Three existing `KNOW-BUSINESS-*` IDs name
  the non-prototype path.
- Root cause: a prototype copy was retained after the spec was promoted.
- Fix: human decision. Migrating from the non-prototype path is safe, but
  deleting a `docs/` file is out of scope for TASK-005.
- Verification: not started.
- Resolution: None.

### ISSUE-010 — Stale prototype roadmap

- Issue ID: `ISSUE-010` (C-09)
- Status: OPEN
- Priority: Low
- Area: Documentation hygiene
- Feature: Roadmap
- Problem: `docs/product/prototype/PROTOTYPE_ROADMAP.md` is identical to
  `docs/product/ROADMAP.md` lines 1-83 and then stops, so it reports PHASE 11 as
  the last phase while `ROADMAP.md` continues through PHASE 2A/2B/2C.
- Expected behavior: one roadmap, or the prototype copy clearly marked as a
  frozen snapshot.
- Evidence: line-level comparison as above.
- Root cause: the prototype copy was frozen at PHASE 11 and never removed.
- Fix: human decision. Roadmap phase status is deliberately not migrated into
  `knowledge/`, so no chunk is affected.
- Verification: not started.
- Resolution: None.

### ISSUE-011 — Dashboard README names non-existent files

- Issue ID: `ISSUE-011` (C-10)
- Status: OPEN
- Priority: Low
- Area: Documentation hygiene
- Feature: Dashboard feature docs
- Problem: `docs/product/features/dashboard/README.md:5-10` names
  `brainstorming.md`, `spec.md`, `decisions.md` and
  `recommended-doc-structure.md`. None exist. The real files are
  `DASHBOARD_SPECS.md` and `DECISIONS.md`, and the brainstorming note lives at
  `docs/product/brainstorming_notes/DASHBOARD_BRAINSTORMING.md`.
- Expected behavior: a README that matches the folder.
- Evidence: the citations above.
- Root cause: the README predates a folder restructure and was never updated.
- Fix: human decision. TASK-005 Phase 7 is user-initiated only, so the
  Orchestrator did not silently rewrite it.
- Verification: not started.
- Resolution: None.

### ISSUE-012 — Stale intra-doc paths

- Issue ID: `ISSUE-012` (C-11)
- Status: OPEN
- Priority: Low
- Area: Documentation hygiene
- Feature: Multiple documents
- Problem: several documents cite paths that do not exist —
  `PRODUCT_SPEC.md:64` and `ARCHITECTURE.md:49,89` cite `docs/DATA_MODEL.md`,
  `PRODUCT_SPEC.md:71` and `ARCHITECTURE.md:121` cite `docs/DECISIONS.md` and
  `docs/ROADMAP.md`, and `ARCHITECTURE.md`/`PRODUCT_SPEC.md` cite
  `docs/ARCHITECTURE.md`. The real paths are under `docs/architecture/`,
  `docs/architecture/decisions/` and `docs/product/`.
- Expected behavior: internal cross-references resolve.
- Evidence: the citations above.
- Root cause: the `docs/` tree was reorganized without updating cross-links.
- Fix: user-initiated. The `Source:` lines of every migrated chunk must use the
  real paths so the knowledge base is correct regardless of the stale links.
- Verification: the Reviewer must confirm no chunk `Source:` line cites a
  non-existent path.
- Resolution: None.

### ISSUE-013 — Superseded self-declared authorities

- Issue ID: `ISSUE-013` (C-12)
- Status: OPEN
- Priority: Low
- Area: Authority model
- Feature: Design system, ADR record
- Problem: `MOMENTUM_DESIGN_SYSTEM_V1.md` §1 line 12 and `ADR-21` line 302 each
  declare themselves the source of truth. ADR-36 now establishes that
  `knowledge/` is authoritative for implementation-relevant requirements and
  `docs/` is supporting context.
- Expected behavior: no document claims authority that ADR-36 has superseded.
- Evidence: the citations above plus ADR-36 at
  `docs/architecture/decisions/DECISIONS.md:1058`.
- Root cause: ADR-21 predates the knowledge-authority decision.
- Fix: user-initiated. The Architect owns `DECISIONS.md` and cannot amend
  ADR-21 as a side effect; the design system is `Locked`.
- Verification: the Reviewer must confirm ADR-21 was not modified and that the
  conflict is recorded rather than silently resolved.
- Resolution: None.

### ISSUE-014 — Home Hero artwork dominates the foreground

- Issue ID: `ISSUE-014`
- Status: CLOSED
- Priority: High
- Area: UI / Home Hero
- Feature: Home dashboard (Phase 2C)
- Problem: the Aurora artwork visually dominates the foreground content; the
  progress ring is not clearly visible/readable against it, so the ring,
  completion count and motivational message do not hold their required
  contrast over the art.
- Expected behavior: the ring, count and message stay clearly readable while
  still visually belonging to the Aurora Hero (DASHBOARD_SPECS §6;
  AGENTS §9 contrast ≥ 4.5:1 for body text in both themes).
- Evidence: user report TASK-006 (issue 1). Measured on device: `ProgressOrb`
  track = `surfaceVariant(=surfaceSoft) @0.6` — Light `#F1F2FB` over pale
  artwork ≈ 1:1 luminance (0 % ring invisible); ADR-35's glow lobe brush was
  painted over the primary arc.
- Root cause: a surface token used as a stroke over artwork, and glow draw
  order veiling the arc.
- Fix: `ProgressOrb.kt` track → `c.textPrimary.copy(alpha = TrackAlpha)` with
  `TrackAlpha = 0.55f` (§7 example 0.25 measured ≈1.6:1, fails ≥3:1; 0.55
  satisfies it); draw order → track → halo → lobes → primary arc.
- Verification: CLOSED. Reviewer A4(c) on emulator, both themes: ring track
  3.36:1 / 3.39:1 Light, 5.80:1 Dark (≥3:1); count 10.47:1 / 17.91:1;
  message 4.71:1 / 9.64:1 (≥4.5:1). `task.md` §10/§11.
- Resolution: Deviation `TrackAlpha 0.55` approved by Reviewer against §7's
  binding constraint.

### ISSUE-015 — Seam around the Home Hero date/greeting area

- Issue ID: `ISSUE-015`
- Status: CLOSED
- Priority: High
- Area: UI / Home Hero
- Feature: Home dashboard (Phase 2C)
- Problem: the Aurora artwork does not blend naturally with the Home content;
  there is an obvious visual gap/seam around the date/greeting area, so the
  Hero reads as artwork placed behind a separate card rather than one
  continuous surface.
- Expected behavior: one continuous visual surface; no visible seam around the
  greeting (DASHBOARD_SPECS §6, ADR-31, ADR-32).
- Evidence: user report TASK-006 (issue 3). Architect measured (task.md §7):
  piecewise-veil step hypothesis disproved (copy-free column scan shows no
  tonal step); the real cause is the anchor level change `VeilOpen 0.10` →
  `VeilContentLight 0.62` / `VeilContentDark 0.58` landing between greeting
  and ring (light column 202 → 237 brightness across y=380→536).
- Root cause: veil anchor level gap reading as a plate edge (DASHBOARD_SPECS
  §6 forbids it).
- Fix: ADR-35's sampled-profile mechanism kept; anchors retuned — `VeilOpen
  0.20`, `VeilContentLight/Dark 0.30/0.32`, `VeilHeaderLight/Dark 0.25/0.26`
  (open→content delta 0.10 Light / 0.12 Dark ≤ 0.12).
- Verification: CLOSED. Reviewer A4(d): copy-free margins smooth, no
  discontinuity; A4(e): artwork textured behind the ring (R 191–230 vs page
  247). Both themes. `task.md` §10/§11.
- Resolution: `VeilOpen 0.20` deviation (vs §7's 0.18 example) approved by
  Reviewer — 0.18 would breach the ≤0.12 delta in Dark.

### ISSUE-016 — Blank band between status bar and Aurora Hero (Home)

- Issue ID: `ISSUE-016`
- Status: CLOSED
- Priority: High
- Area: System bars / edge-to-edge / Home Hero
- Feature: Home dashboard (Phase 2C)
- Problem: a visible blank/solid band sits between the Android status-bar area
  and the Aurora Hero; the artwork does not extend behind the transparent
  status bar as ADR-33 requires.
- Expected behavior: Aurora extends behind the transparent status bar, while
  foreground copy still clears the status-bar icons and camera cutout; no
  arbitrary Hero offset may be used to compensate for the layout.
- Evidence: user report TASK-006 (issue 7). Architect measured on
  `emulator-5554` (both themes, empty/populated, rest/scrolled, cutout on/off):
  Home y=0 is artwork in every state (`AFBBFD`/`1C207A`/`95A5F9`), date box
  `[53,178]` = `statusBars + HeroTopInset`; no edge discontinuity. Hypothesis
  "shared window/inset config strips the top" disproved; the one alternative
  contradicted by the y=0 samples.
- Root cause: not reproducible on the current tree — the report likely
  pre-dates the tree or misattributes the Habits defect (ISSUE-017). D0 rule 4
  (ADR-37/ADR-39) now guarantees artwork at the window edge on both routes.
- Fix: no Home code change; `HomeHeroCompositionTest`'s `artwork.top <= 0.dp`
  retained as the regression guard.
- Verification: CLOSED. Reviewer A4(a): Home y=0 artwork Light Δ −55…−91 /
  Dark Δ B +49…+58 vs background, empty AND populated, rest AND scrolled;
  screenshot evidence in `task.md` §11.
- Resolution: Guard-based closure; a repeat report must state theme,
  population and scroll offset.

### ISSUE-017 — Habits top-band/status-bar regression

- Issue ID: `ISSUE-017`
- Status: CLOSED
- Priority: High
- Area: System bars / edge-to-edge / Habits Hero
- Feature: Habits management screen (Phase 2B)
- Problem: a previous Home fix introduced the same top-band/system-bar problem
  into Habits; the Habits Aurora composition must retain its existing spacing,
  artwork treatment and visual behavior.
- Expected behavior: Habits looks as it did before the Home/edge-to-edge work,
  with correct behavior at the status bar in both themes.
- Evidence: user report TASK-006 (issue 9). Architect measured (task.md §7):
  `HabitsHero` `TopDissolve = 44.dp` (115 px) measured from the container top
  while ADR-33 moved the hero up by 136 px, so the dissolve sat inside the
  status-bar zone — `b_light.png` y=0 `DCE9F8` ≈ `AppBackground`, full artwork
  only from y≈120. The first D0 rule 4 implementation then still started the
  mask `0f → Transparent` (Reviewer Finding 1: Habits y=0 Δ ≤ 6/255 Light,
  +4…+8 Dark), and its fix attempt proved the original rule-4 wording
  self-contradictory (§9 "Fix attempt (Handoff 4) — BLOCKED").
- Root cause: top dissolve drawn inside the status-bar area, plus a
  self-contradictory D0 rule 4 wording (fixed by re-statement, not code).
- Fix: **ADR-39 / task.md §7.7** — the full-strength head absorbs the caller's
  top band: `MomentumHeroArtwork` renders α=1 from the container top and draws
  NO top fade (stops `0→White, bottomStart→White, 1→Transparent`); the
  component reads no insets for masking (D0 rule 3: safe area is the copy's
  job); `topDissolve` stays in the signature (ADR-30) but generates no
  stop/dip/offset. `HabitsHero.kt` composition untouched.
- Verification: CLOSED. Reviewer re-verified independently on emulator both
  themes, empty and populated: Habits y=0 artwork Light Δ(−55.0,−63.4,+1.3) /
  Dark Δ(+64.6,+54.6,+160.6) vs background; no seam over y=125..145 (≤2.16);
  bottom fade intact; status-bar icons 7.87:1–15.90:1; Habits copy top y=554
  ≥ 136; Home pixel-identical to pre-fix build; 259/0 unit tests, 7/7
  instrumentation. `task.md` §10/§11.
- Resolution: Decision defect escalated to the Architect per §4 and re-stated
  (ADR-39, append-only); implementation and verification complete.

### ISSUE-018 — Excessive gap between Home Hero and "Remaining today"

- Issue ID: `ISSUE-018`
- Status: CLOSED
- Priority: Medium
- Area: UI / Home layout
- Feature: Home dashboard (Phase 2C)
- Problem: after the Hero content, the "Remaining today" section has too much
  empty space; the activity content should follow the Hero naturally.
- Expected behavior: activity content continues straight out of the Hero's
  fade; no artificial Hero height or bottom void (ADR-32, DASHBOARD_SPECS §4).
- Evidence: user report TASK-006 (issue 8). Architect measured: message bottom
  y=981 → "Remaining today" top y=1118 = 137 px ≈ 52 dp, composed of
  `HeroBottomInset = 40.dp` + `MomentumSpacing.item` (12 dp); the artwork's
  blend died 16 dp above the Hero edge, so the last 52 dp were page background.
- Root cause: oversized bottom inset + blend tail, not Hero content height.
- Fix: `HomeHero.kt` — `HeroBottomInset 40→16.dp`, `BottomBlendStart 72→48.dp`,
  `BottomBlendTail 16→0.dp`; `TodayScreen.kt` `spacedBy` untouched (list
  rhythm). Hero content itself not compressed.
- Verification: CLOSED. Reviewer A4(f): message bottom y=981 → section top
  y=1055 = 74 px = **28.2 dp ≤ 32 dp**, artwork still fading to page
  background at y≈1025. `task.md` §10/§11.
- Resolution: One unit assertion in `HomeHeroAssetsTest` deliberately
  re-pointed to the new geometry (Reviewer confirmed it still guards the
  contract).

### ISSUE-019 — Reach a Target dialog too transparent

- Issue ID: `ISSUE-019`
- Status: CLOSED
- Priority: Medium
- Area: UI / dialog surface
- Feature: Home, target completion (Phase 2C)
- Problem: the target-entry/completion dialog is too transparent; dialog
  content and controls are difficult to distinguish from the screen behind it.
- Expected behavior: the dialog is sufficiently opaque/readable while
  preserving the app's glass visual language; no redesign
  (DASHBOARD_SPECS §10.2, ADR-30, AGENTS §9).
- Evidence: user report TASK-006 (issue 2). Architect measured: the panel is
  opaque (`dialogFill = 1.0f`, `GlassLevel.DIALOG`) — the perceived
  transparency is the weak window dim (`dialogScrim = 0.45f` per ADR-30) plus
  a low-alpha field outline and bare `TextButton`s, so the sheet does not
  separate from the page behind it (`dlg.png`).
- Root cause: insufficient window dim + ghost field outline (not panel fill).
- Fix: **ADR-38** — `MomentumGlass.dialogScrim 0.45f → 0.72f` (`Tokens.kt`) +
  visible semantic field outline in `TargetEntryDialog.kt`
  (`unfocusedIndicatorColor = textSecondary@0.8`, `focusedIndicatorColor =
  primary`). Panel fill/elevation/layout/glass language unchanged; no
  redesign.
- Verification: CLOSED. Reviewer A4(g) both themes: scrim measured 0.72 Light
  / 0.715 Dark, panel fill opaque, field outline visible, all copy readable;
  screenshots in `task.md` §11.
- Resolution: Recorded as ADR-38 (append-only) before implementation.

### ISSUE-020 — Home date label contrast below floor in Light theme

- Issue ID: `ISSUE-020`
- Status: OPEN
- Priority: Low
- Area: UI / Home Hero text token
- Feature: Home dashboard (Phase 2C)
- Problem: the date label (`textSecondary`) renders `#596579` over sampled
  background `(181,183,246)` = **3.11:1**, below the 4.5:1 body-text floor
  (DASHBOARD_SPECS §6; task.md §7.4). Dark passes at 5.22:1.
- Expected behavior: body text ≥ 4.5:1 in both themes.
- Evidence: TASK-006 Reviewer Finding 2, `A4a_home_light_rest.png`,
  glyphs in `[53,178][256,224]`, background sampled `x 270..340, y 178..224`.
  Whether newly introduced (via `VeilHeaderLight 0.30 → 0.25`) or long-standing
  is uncertain — the pre-change tree would need a rebuild to tell. Not in
  A4(c)'s enumeration (ring/count/message all pass), so it does not block
  TASK-006.
- Root cause: `textSecondary` token too light against the Light artwork under
  the current veil header alpha.
- Fix: deferred — per task.md §7.5 the remedy is the TEXT TOKEN, never
  re-inflating the veil. Needs a separate task to choose the token and check
  it against other `textSecondary` usages.
- Verification: PENDING (separate task).
- Resolution: None. Deferred by Reviewer decision during TASK-006 close-out;
  recorded here so it is not lost.

### ISSUE-021 — Fresh install reports no active theme in the Theme Gallery

- Issue ID: `ISSUE-021`
- Status: OPEN
- Priority: Medium
- Area: Settings / theme selection
- Feature: Theme Gallery (single-theme build)
- Problem: on a fresh install the Theme Gallery renders **no selection state** —
  the app's only theme reads as unselected. The cause is that
  `SettingsDataStore` defaults `theme_id` to `"twilight"`, but `MomentumThemes`
  contains no `twilight` entry, so no gallery item's id matches the stored
  preference.
- Expected behavior: a fresh install shows the app's active theme as selected.
- Evidence: TASK-007 — Architect finding (`SettingsDataStore` default vs.
  `MomentumThemes` entries), independently **CONFIRMED** by the Reviewer as
  reproducible candidate defect B-1 in `task.md` §10/§11. Bounded to a single
  literal.
- Root cause: a default value that references a theme id which no longer
  exists, left behind when the theme set was reduced to one.
- Fix: align the default with a real `MomentumThemes` id (or treat an unknown
  stored id as "fall back to default" at the selection site). **Must not** be
  bundled with the single-theme gallery question below.
- Verification: PENDING — requires a fresh-install run of `ThemeGalleryScreen`
  showing the active theme selected. Not verified in TASK-007 (audit-only, no
  emulator run).
- Resolution: None.
- **Do not conflate with:** the single-theme gallery itself. That is
  *recorded deferred debt* in ADR-21 and is a product decision for Step 2/3.
  This issue is only the unrecorded one-line default bug.

### ISSUE-022 — Settings reduce-motion copy promises a whole-app effect it does not deliver

- Issue ID: `ISSUE-022`
- Status: OPEN
- Priority: High
- Area: Settings / accessibility / motion
- Feature: Settings → motion preference
- Problem: Settings offers a "Calm animations throughout the app" toggle. The
  stored `reduceMotionOverride` value is read at **1 of 14**
  `rememberReduceMotion` call sites; the other thirteen pass `null` and honour
  only the system-level setting. A user with a vestibular disorder is told the
  preference applied app-wide, and it did not.
- Expected behavior: a stated user preference is either honoured everywhere it
  is claimed to apply, or the copy is corrected to state its real scope.
  AGENTS §9 requires reduced motion to be respected; a toggle that silently
  does nothing also fails basic correctness of a visible control.
- Evidence: TASK-007 — Architect finding (motion call-site sweep), corrected to
  the 1-of-14 denominator by the Reviewer (F-4), who confirmed it
  independently as reproducible candidate defect B-2. The null-passing pattern
  is additionally pinned by `CompletionLabelTest.kt:97`, so it is entrenched
  rather than accidental.
- Root cause: the motion token layer and its call sites were built
  independently of the preference plumbing; the copy describes the intent
  rather than the implementation.
- Fix: route the stored override through the shared reduce-motion source
  (and through the currently dead `MomentumMotion` token layer), or narrow the
  copy to its true scope. **Accessibility-correctness, not taste** — do not
  defer this as a design critique.
- Verification: PENDING — requires a run with the override enabled confirming
  the previously animated surfaces stop animating. Not verified in TASK-007
  (audit-only, no emulator run).
- Resolution: None.

### ISSUE-023 — Orchestrator deliverable write grant missing (`opencode.json`)

- Issue ID: `ISSUE-023`
- Status: CLOSED
- Priority: Medium
- Area: Multi-agent infrastructure
- Feature: Agent operating system
- Problem: `opencode.json` denies `edit` on `*` for all four agents. The
  Orchestrator may write only `task.md`, `PROJECT_STATUS.md` and `ISSUES.md`;
  the Architect and Reviewer only `task.md` (+ the ADR file for the Architect);
  the Developer only `task.md` and `app/**`. Therefore **no agent can create
  `MOMENTUM_PREMIUM_DESIGN_AUDIT.md`** at the repository root, which the
  TASK-007 deliverable requires.
- Expected behavior: an agent that is required to produce a root-level
  documentation deliverable holds a matching write grant.
- Evidence: TASK-007 blocker B1. Confirmed by a deliberate write probe, which
  was rejected by the permission layer with the exact deny rule.
- Root cause: the permissions matrix encodes a code-only assumption — that all
  agent deliverables are code changes or `task.md` content — and has no path
  for documentation artefacts.
- Fix: a human adds a grant to the orchestrator `edit` map in `opencode.json`
  (e.g. `"MOMENTUM_PREMIUM_DESIGN_AUDIT.md": "allow"`, or a broader
  `docs/**` / `*.md` grant if root-level design documents are expected
  routinely). The Orchestrator is also denied write on `opencode.json`, so it
  cannot self-apply this.
- Verification: **PASSED.** The human applied the grant (`opencode.json`
  orchestrator `edit` map, line 17: `"MOMENTUM_PREMIUM_DESIGN_AUDIT.md":
  "allow"`). `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` was then created and verified:
  exists (352,990 bytes, 2,509 lines, UTF-8 without BOM, 0 replacement
  characters); all 25 `R7` sections present in the required order; `####`
  sub-heading count identical to the source record (133 = 133); the 13
  screen-verdict severities use only the allowed vocabulary
  (`REIMAGINE` ×2, `REDESIGN` ×5, `EVOLVE` ×6); `CP-1…CP-10`, `HO-1…HO-12`
  and `Q1…Q14` all present; 14 `Unknown` markers preserved; and zero
  `task.md` scaffolding (`ARCHITECT ONLY`, `IMPLEMENTATION PLAN`,
  `HANDOFF HISTORY`, `FINAL OUTCOME`) leaked into the artifact. `git status`
  confirms no application file was touched by the emission.
- Resolution: **RESOLVED.** Grant applied by the human; artifact emitted from
  the verified audit record in `task.md` §7, reordered per the §7.0a map with
  the six non-deliverable source blocks preserved as Appendices A–F so the
  method, the section map, the carried findings, the out-of-scope
  observations, the unknowns and the full correction log survive alongside the
  critique. Phase 1 closed.

### ISSUE-024 — Unreferenced 5.4 MB design concept pack with no recorded provenance

- Issue ID: `ISSUE-024`
- Status: **RESOLVED**
- Priority: Low
- Area: Repository / design assets / governance
- Feature: Multi-agent infrastructure (asset provenance)
- Problem: `design/aurora-concept/` contains `fonts.css` plus seven `.woff2`
  subsets (2,552 B – 5,399,668 B) declaring Manrope at weights 400/500/600/700
  and one `Material Symbols Rounded` variable font at `100 700`. It is
  **referenced by no file in the repository**, and no ADR explains it — unlike
  `artwork/onboarding/*.svg`, which ADR-15/ADR-17 document.
- Expected behavior: a design artefact that materially affects implementation
  decisions is either referenced, documented, or removed. AGENTS §12 requires
  decisions future sessions need to know to be recorded.
- Evidence: TASK-007 correction **F-9**. Repo-wide search across
  `*.kt` / `*.kts` / `*.xml` / `*.toml` / `*.json` / `*.md` returns **zero**
  references. All seven woff2 files verified as complete `wOF2` /
  `00010000` containers. Its two material effects: it exposed that
  `app/src/main/res/font/manrope.ttf` is a **variable font** (`wght` 200–800,
  named instances at 300/400/500/600/700/800 — verified by parsing the TTF
  `fvar`/`name` tables, not inferred from the CSS), which turns the audit's
  CP-1 from "missing capability" into "700 is available and simply not
  registered"; and it shows the concept anticipated **Material Symbols
  Rounded** while the build uses Material **Icons** (`material-icons-extended`)
  and `MOMENTUM_DESIGN_SYSTEM_V1.md` §8/§32 plus ADR-21 say Symbols — i.e. one
  recorded decision never honoured in code.
- Root cause: the artefact was created during design exploration and never
  either wired in, documented, or cleaned up; the audit missed it because the
  inventory enumerated `app/src/main/res/` completely but never enumerated
  top-level directories.
- Fix: **do not delete before Step 2 concludes.** Step 2's visual concept
  exploration is exactly what this pack is for, and it is the only design
  *intent* evidence in the repository. Record its provenance (is it intent or
  an abandoned exploration?), and record the Manrope variable-font axis range
  in `docs/architecture/decisions/DECISIONS.md` — ADR-21 currently says only
  "a variable TTF" without the axis range, and that verified fact is not
  cheaply re-derivable.
- Verification: **PASSED** (static, repository-only). Phase-1 closure
  investigation, 2026-09-28. All factual questions closed:
  - **Not consumed by the Android application.** Reference count for
    `aurora-concept | fonts.css | woff2 | material-symbols` is **0** in
    `app/src/main`, `app/src/test`, `app/src/androidTest`, `docs/`, `artwork/`,
    `gradle/`, `app/build.gradle.kts`, `build.gradle.kts`, `settings.gradle.kts`,
    `opencode.json`, `AGENTS.md` and `.gitignore`. `Type.kt:36–49` builds
    `FontFamily` from three `Font(R.font.manrope, …)` entries only, all
    resolving to the single bundled `app/src/main/res/font/manrope.ttf`
    (164,700 B, git-tracked). No `assets` / `sourceSets` / web pipeline exists
    in any build file.
  - **Not required by the current implementation**, for three independent
    reasons: **format** — `.woff2` is a web font format and is not a valid
    Android font resource (Android accepts `.ttf`/`.otf`), so these files
    cannot be consumed by Compose as they stand; **redundancy** — `f1`–`f6` are
    web-delivered Manrope subsets (19-table signature matching the bundled
    variable TTF's `fvar`/`gvar`/`STAT`/`HVAR`) and therefore duplicate font
    capability the app already bundles offline; **non-use** — `f7` is not the
    icon family the application uses.
  - **File identity.** `fonts.css` (7,805 B) is a **web CSS artifact** — Google
    Fonts CSS API v2 output declaring Manrope at 400/500/600/700 across six
    subsets plus Material Symbols Rounded at `100 700`, with a
    `.material-symbols-rounded` class. Its only possible consumer is an
    HTML/CSS page; the repository contains **0** HTML files and this is its
    only `.css` file, so no consumer exists. `f1`–`f6` (74,972 B total) are
    Manrope subsets; `f7.woff2` (5,399,668 B, **98.5%** of the pack) is
    Material Symbols Rounded, 18 tables, 15,224,444 B uncompressed. All seven
    are complete `wOF2` / `00010000` containers with header length equal to
    on-disk length. *Caveat:* the woff2 `name` tables are brotli-compressed and
    were not parsed, so `f7`'s identity rests on the CSS declaration plus its
    uncompressed size, not on an independent read of the font's own name
    record.
  - **Material Symbols Rounded is a concept asset only.** The application's
    icon implementation is unchanged and remains Material **Icons** via
    `material-icons-extended` (`app/build.gradle.kts:66`, 100
    `import androidx.compose.material.icons.*` statements). The documentation /
    concept disagreement on icon family is **intentionally left open as a
    Phase-2 decision** and is not settled by this issue.
  - **Git state.** `git ls-files` returns nothing for the path and
    `git log --all -- design/` is empty: never committed, and **untracked but
    not gitignored** — no `.gitignore` rule covers `design/`, `woff2` or `ttf`.
  - **Timing.** All eight files were written on 2026-09-28 within a 9-second
    window (`f1`–`f6` at 16:32:30; `fonts.css` and `f7` at 16:32:39), after the
    last commit (`bd1bafb`, 2026-09-26) and after TASK-006's code work. No
    agent in this system can write `design/**` (all four agents hold
    `edit: *: deny` with narrow grants), so the files were introduced outside
    the agent workflow.
- Resolution: **RESOLVED — retained as a non-runtime Phase-2 input.**
  `design/aurora-concept/` is **retained intentionally as a Phase-2 visual
  concept input**, and the following are now on record:
  1. It is **not** consumed by the Android application and is **not** required
     by the current implementation.
  2. `f1`–`f6` are web-delivered Manrope subsets that duplicate the already
     bundled Manrope capability (`app/src/main/res/font/manrope.ttf`).
  3. `f7` is Material Symbols Rounded and is **not** an Android runtime
     dependency; the application's icon implementation is unchanged.
  4. `fonts.css` is a **web CSS artifact**, not an Android resource.
  5. The directory **must not be treated as an application/runtime asset**
     without an explicit future design/implementation decision — adopting any
     part of it at runtime requires a separate decision recorded in
     `docs/architecture/decisions/DECISIONS.md` by the Architect, since ADRs
     are Architect-owned.
  6. Its purpose, scope and provenance are documented in
     `docs/design/AURORA_CONCEPT_ASSETS.md`.
  7. **Do not run a blanket `git add -A` while these assets remain untracked.**
     The directory is untracked and not ignored, so a blanket add would commit
     5.23 MB of undocumented binaries — the exact condition this issue
     recorded. Either add the assets together with their documentation, or add
     a `.gitignore` rule.
  **Provenance limits (stated explicitly so nothing here is over-claimed).**
  Repository-only evidence establishes *what* the files are and that they are
  unreferenced; it does **not** establish the original author's intent. The
  `fonts.css` structure is consistent with Google Fonts CSS API v2 delivery and
  ADR-21 records the already-bundled Manrope as "OFL-1.1 from google/fonts",
  but the exact upstream version, URL and acquisition route of these files
  were **not** established and are recorded as unknown. The Phase-2 retention
  rationale is the reason for keeping the assets as concept input going
  forward — it is **not** a claim about proven historical provenance.
  **Carried forward, deliberately unresolved:** the icon-family conflict
  (code = Material Icons, documentation and this concept pack = Material
  Symbols Rounded) is a Phase-2 decision, recorded as open question Q6 in
  `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` §23.
  No ADR number was created: this records the status of an existing non-runtime
  asset directory and takes no architectural decision, so the repository's ADR
  criteria (AGENTS §14.15) are not met.

- **SUPERSEDING DISPOSITION (final state — supersedes the retention resolution
  above, which is retained above as the historical record of that decision).**
  The Phase-2 visual concept exploration was subsequently abandoned: the four
  direction prototypes were deleted (ADR-46 / TASK-014) and the exploration was
  closed without a direction being selected. `design/aurora-concept/` has now
  been **REMOVED** from the repository — the exploratory concept assets are no
  longer required. Final facts:
  1. `design/aurora-concept/` **does not exist**. Its 8 files (`fonts.css` plus
     `f1`–`f7` `.woff2`) are no longer present.
  2. It was **never** consumed by the Android application at any point, so its
     removal changes no runtime behaviour and no APK content.
  3. **Production Aurora is untouched**: `MomentumAurora.kt`,
     `MomentumHeroArtwork.kt`, `HomeHero.kt`, `HabitsHero.kt`, `Color.kt` and the
     bundled `habits_hero_light/dark.webp` all remain in place and unchanged.
  4. The document `docs/design/AURORA_CONCEPT_ASSETS.md` is retained and has been
     corrected to record the removal rather than claim the files still exist.
  5. The unresolved icon-family conflict (code = Material Icons vs the concept
     pack's Material **Symbols** Rounded) remains an open product question and is
     **not** resolved by this removal — the concept pack's evidence is gone, so
     Q6 in `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` §23 now stands on its own.
  6. The blanket-`git add -A` hazard recorded in item 7 above no longer applies;
     the assets are gone rather than merely untracked.

### ISSUE-025 — Phase 2B Round 1 palette and type scale are derived, not quoted

- Issue ID: `ISSUE-025`
- Status: **CLOSED as moot (TASK-014 / ADR-46)**
- Priority: High
- Area: Design / visual fidelity / verification method
- Feature: F-CONCEPT-2B (Phase 2B Round 1 visual concept prototypes)
- Problem: `MOMENTUM_CONCEPT_EXPLORATION.md` specifies each direction's visual
  direction **in prose only**. It contains **zero hex colour values and zero `sp`
  type sizes** across all 8,008 lines. The Architect's approved implementation
  plan (TASK-009 `task.md` §7) therefore contains an unavoidable interpretive act:
  every hex value and every `sp` size in it is a **derivation** from that prose,
  anchored on the project's existing scale (`MOMENTUM_DESIGN_SYSTEM_V1.md` §4) and
  measured against each direction's own contrast floor.
- Expected behavior: a reviewer comparing a prototype against its source-of-truth
  brief must be able to distinguish "the brief specifies this" from "the brief
  implies this, and the implementer chose a value". Otherwise fidelity claims
  are unfalsifiable, because the document can never be used to refute a number.
- Evidence: Architect verification, TASK-009 §7.2 — repository-wide grep of
  `MOMENTUM_CONCEPT_EXPLORATION.md` returns 0 hex-pattern matches and 0 `sp`
  matches. Per-direction computed contrast for the derived light palettes:
  A date caption **6.55:1**; B inscription **8.40:1** (7.19:1 on the channel
  interior); C body text **7.96:1**; D date caption **4.60:1** on canvas — the
  last clears the 4.5:1 floor only marginally, which is D's own stated point.
- Root cause: an upstream document-authoring gap, not an implementation defect.
  The Phase 2A artifact was written as a *brief for an image generator*, where
  prose intents are sufficient; it was not written as a *build specification*.
  Phase 2B implementation therefore inherits specification gaps that image
  generation would have absorbed silently and unimplementably.
- Fix: none applied to the source document — `MOMENTUM_CONCEPT_EXPLORATION.md` is
  authoritative and is not edited by TASK-009. Instead, the disclosure is carried
  structurally: (a) the derivation basis and every per-direction contrast figure
  are recorded in `task.md` §7.2; (b) the plan states that Reviewer must judge
  hex/`sp` against the **prose intent**, not against a list the document never
  contained; (c) the four resulting screenshots in `docs/phase2b/round1/` are
  comparative evidence of visual language, **not** approved design tokens.
- Verification: **PENDING.** Requires Reviewer/Tester evidence under TASK-009
  §11 that each direction is recognisable from visual language alone and that
  each derived palette meets its own contrast floor as rendered on the device.
  Closing this issue is **not** equivalent to the prototypes being correct — it
  closes only the *provenance of the numbers*.
- Resolution: **OPEN.** Carried forward deliberately. Must be settled before any
  direction is selected for production: whichever direction wins will need its
  derived palette and type scale ratified as real design tokens, at which point
  the specification gap becomes a production risk rather than a prototype risk.

### ISSUE-026 — Phase 2B prototypes will ship in the release APK as dead code

- Issue ID: `ISSUE-026`
- Status: **CLOSED as resolved by removal (TASK-014 / ADR-46)**
- Priority: Medium
- Area: Architecture / build hygiene / release
- Feature: F-CONCEPT-2B (Phase 2B Round 1 visual concept prototypes)
- Problem: TASK-009 §3.1 mandates that prototype Kotlin live under
  `app/src/main/java/com/momentum/app/presentation/exploration/phase2b/`, and the
  release build has `isMinifyEnabled = false` (`app/build.gradle.kts` release
  block). The consequence is that all four prototype composables, their palettes,
  font registrations and textures are compiled into the **release** APK even
  though the only entry point is a debug-source-set activity that does not exist
  in release. The code is unreachable but not removed.
- Expected behavior: exploration code is either excluded from release builds or
  explicitly accepted as a known, time-boxed cost.
- Evidence: Architect decision, TASK-009 §7.1(b) and ADR-40. Alternatives were
  evaluated and rejected for TASK-009: an instrumentation-only host lives in
  `app/src/androidTest/**`, which §3.1 does not permit and which cannot capture
  the real status bar the safe-area criteria require; a Robolectric JVM render
  needs `androidx.compose.ui:ui-test` added to `testImplementation`, which would
  be a new dependency.
- Root cause: the task's isolation rule mandates `app/src/main` for the prototype
  source while correctly forbidding production navigation changes. The two
  constraints together make release exclusion impossible without either
  relocating the prototypes or adding a build-variant split.
- Fix: **none in TASK-009.** Deliberately deferred, not silently worked around.
  The prototypes are temporary by construction (Round 1 of 2, no direction
  selected). Record the cost, and delete the entire
  `presentation/exploration/` tree plus the debug source set when the exploration
  concludes.
- Verification: **PENDING.** Requires Reviewer/Tester evidence that the release
  manifest merge contains no `Phase2BPreviewActivity` (the Developer is required
  to prove this *before* writing direction code) — that the entry point is
  debug-only. The dead-code weight in the release APK is accepted and quantified
  in the issue's resolution rather than eliminated.
- Resolution: **TECHNICAL HALF SATISFIED; STILL OPEN.** The debug-only proof the
  issue required was produced and independently re-verified **twice** — once
  before any direction code was written, and again in the final verification
  pass with `--rerun`. Both merged **release** manifests contain 0 ×
  `Phase2BPreviewActivity`, 0 × the string `exploration`, and exactly 1 activity
  (`MainActivity`); the merged **debug** manifest contains the activity. The
  entry point is provably debug-only. What remains open is the accepted cost
  itself: `isMinifyEnabled = false` means the four prototype composables remain
  in the release APK as unreachable code. To be closed when the Phase 2B
  exploration ends (direction selected, or exploration abandoned) and
  `presentation/exploration/` plus `app/src/debug/` are removed — or when a
  build-variant split is introduced, which would require a new ADR.

### ISSUE-027 — B duplicates the unit word in its row inscription

- Issue ID: `ISSUE-027`
- Status: **CLOSED as resolved by removal (TASK-014 / ADR-46)**
- Priority: Low
- Area: Presentation / Phase 2B prototype
- Feature: F-CONCEPT-2B (Phase 2B Round 1 visual concept prototypes)
- Problem: `exploration/phase2b/instrument/InstrumentHome.kt:217–230` renders both
  `unitLabel` and `unitWord` from the fixture. Fixture row 2 carries
  `title = "Read 20 pages"`, `unitLabel = "p"`, `unitWord = "pages"`, so the row
  renders **"Read 20 pages · p · pages"** — the word twice.
- Expected behavior: a unit inscription that names the unit once. Brief B1 asks
  for "a unit inscription"; §15.2.10 rejects a bare figure with nothing saying
  what it counts.
- Evidence: TASK-009 §11, Reviewer **Finding 11** (final pass), by direct read of
  `Phase2BFixture.kt:72/77/78` and `InstrumentHome.kt:217–230`. Visible in
  `docs/phase2b/round1/B-instrument-home.png` as the "· p · pages" run beside the
  row title.
- Root cause: **collateral of a fix, not an original defect.** Reviewer
  `Finding 10` (MINOR) required B's unit inscriptions to stop reading as glyph
  noise, and the Developer's remedy added the fixture's `unitWord` without
  removing the pre-existing `unitLabel`. Round 1 had no `unitWord` on this row
  before that fix.
- Fix: render `unitWord` alone **or** `unitLabel` alone, not both. One-line
  change in `InstrumentHome.kt`. Not applied in TASK-009 because it was raised in
  the final verification pass, which explicitly recorded it as non-blocking and
  not grounds to reopen the task.
- Verification: **NOT VERIFIED.** No change has been made and no re-render exists.
- Resolution: **OPEN — Round 2 backlog.** Non-blocking; it does not affect the
  TASK-009 verdict, and re-capturing B is not required for Round 1 to stand.

### ISSUE-028 — Migration-test v1 DDL fixture incomplete; masked by Robolectric LEGACY workaround

- Issue ID: `ISSUE-028`
- Status: **OPEN**
- Priority: Low
- Area: Testing / Room migrations
- Feature: Room migration regression tests
- Problem: The three migration-test classes' hand-written v1 DDL fixture omits
  `CREATE INDEX` statements present in `app/schemas/<db>/1.json` (at least
  `index_completion_records_activityId`, `index_completion_records_occurrenceDate`,
  and likely a reminders index). Under Robolectric's NATIVE sqlite mode the
  post-migration schema validation truthfully fails against that incomplete
  fixture. Phase A of TASK-011 (Room 2.7.1) surfaced this by enabling NATIVE on
  Windows (Room 2.7.1's cross-thread connection pool is incompatible with
  Robolectric's per-thread LEGACY pointer tracking), then worked around it with a
  package-scoped `app/src/test/resources/com/momentum/app/data/local/robolectric.properties`
  (`sqliteMode=LEGACY`) covering only `com.momentum.app.data.local` tests.
- Expected behavior: the migration tests validate against a fixture that matches
  real v1 installs, so NATIVE mode can be used uniformly and schema validation is
  strict everywhere.
- Evidence: TASK-011 Phase A Developer handoff (two correction cycles: 7
  `UncaughtExceptionsBeforeTest` under NATIVE, then 3 migration-test failures
  exposing the fixture gap), Reviewer Phase A–C checkpoint Finding 6 (assessed
  package-scoped workaround as acceptable, not masking product regressions).
- Root cause: pre-existing test fixture drift — the DDL was written by hand and
  never kept in sync with the committed schema JSONs; LEGACY mode previously
  hid the mismatch.
- Fix (pending, test source only): complete the v1 fixture DDL to match
  `schemas/<db>/1.json`, confirm the three migration tests pass under NATIVE,
  then delete the package-scoped `robolectric.properties` file. Real product
  migrations are unaffected (they only `ALTER activities`); no schema files may
  be edited.
- Verification: **PENDING** — the three migration test classes green with the
  properties file removed.
- Resolution: **OPEN.** Reported during TASK-011 Phase A; deferred as out of
  modernization scope (test-source fix, no product behavior).

### ISSUE-029 — Onboarding re-shows on every cold start

- Issue ID: `ISSUE-029`
- Status: **OPEN**
- Priority: High
- Area: Navigation / start destination / DataStore
- Feature: Onboarding, app start
- Problem: completing onboarding routes the user to Today, but the **next cold
  start shows onboarding again**. The Nav3 start key is resolved from
  `rememberNavBackStack(if (onboardingDone) Routes.Today else Routes.Onboarding)`
  (`presentation/navigation/MomentumNavGraph.kt:159`) while `onboardingDone` comes
  from `collectAsStateWithLifecycle(AppSettings())` in `MainActivity.kt:63`, whose
  initial value is `AppSettings()` with `onboardingDone = false` until DataStore's
  first emission arrives. The back stack is created once, so the pre-emission
  default wins and the persisted flag never re-seeds the start destination.
- Expected behavior: a user who has completed onboarding is never shown
  onboarding again, including after a force-stop / cold start.
- Evidence: reproduced on the debug APK during TASK-011 final verification —
  complete onboarding → Today → force-stop → cold start → onboarding page 1. The
  flag itself persists correctly (`momentum_settings.preferences_pb` contains
  `onboarding_done … 01`). **Not a modernization regression**: the pre-migration
  `HEAD` (`bd1bafb`) contains the byte-identical
  `NavHost(startDestination = if (onboardingDone) …)` construct and the identical
  `collectAsState(initial = AppSettings())` seed; the Nav3 translation preserved
  the existing behavior exactly.
- Root cause: the start destination is derived from asynchronously loaded
  preference state that is consumed exactly once, at back-stack construction
  time, with a default that equals "onboarding not done".
- Fix (not in TASK-011 — modernization scope; a product-visible behavior change):
  gate on a loaded state — render nothing until the first DataStore emission, or
  correct the start key once the real value arrives, e.g.
  `LaunchedEffect(onboardingDone) { if (onboardingDone) backStack.reset(Routes.Today) }`
  (Nav3's per-entry reset/transition API per ADR-42). The persisted flag needs no
  change.
- Verification: **PENDING** — requires a cold start after completing onboarding
  showing Today directly.
- Resolution: **OPEN.** Found by the Reviewer during TASK-011 final verification;
  explicitly not a regression, and deliberately not fixed inside the
  modernization (scope discipline, AGENTS §9).

## Issue Rules

- Every issue receives a stable `ISSUE-NNN` identifier.
- Never reuse an issue ID.
- Keep both OPEN and CLOSED issues in this registry.
- Do not create duplicate issues for the same underlying problem.
- Issues must contain evidence.
- The Orchestrator owns issue lifecycle updates.
- Agents may report issues but must not independently close them.
