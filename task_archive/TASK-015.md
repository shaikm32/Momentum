# CURRENT TASK

> State of the currently active task. Not a history, knowledge base, or issue archive.
> Closed task records (TASK-011 modernization, TASK-012 baseline profile, TASK-014
> prototype cleanup) are preserved in git history; only the current task state lives here.

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: TASK-015
- Title: Home REIMAGINE — final Home visual & interaction concept and specification
- Status: **COMPLETE — implementation verified (Reviewer PASS, no actionable
  defects).** Human-approved §8 + ADR-47/ADR-48 (+ Amendment Notes 1)
  implemented; runnable validation green (JVM 261/0; `assembleDebug` +
  `assembleDebugAndroidTest`); the Dark-scrim conflict was resolved via
  ADR-47 Amendment Note 1 (region-sample acceptance method). Instrumented
  execution is unavailable (no device) and is not claimed. Not committed
  (Human-in-the-Loop).
- Priority: High
- Created: 2026-09-30
- Last Updated: 2026-09-30
- Classification:
  - **DESIGN / ARCHITECTURE — required** — Home visual + interaction concept and
    specification → ADR(s) required where the concept changes an established
    decision (AGENTS.md §4, §21).
  - **IMPLEMENTATION** — deferred until the concept is approved (NOT authorized yet).
  - **VERIFICATION** — Reviewer validates the implementation against the approved
    concept and existing architecture (later phase).
- Routing: **Orchestrator → Architect → (human approval gate) → Developer → Reviewer.**
- Source of truth (in priority order):
  1. This task brief (human requirement, this session).
  2. `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` §7.22 (Home severity = `REIMAGINE`) and
     the Home/Today finding block (§§8.1–8.3, 7.8.13, 7.14, 7.15).
  3. `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` (Locked) — Soft Aurora language.
  4. Existing ADRs in `docs/architecture/decisions/DECISIONS.md` (esp. ADR-22,
     28–35, 37–39) and `docs/product/features/dashboard/DASHBOARD_SPECS.md`.
  5. Current implementation under `app/src/main/java/com/momentum/app/presentation/**`.

---

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Establish the **final Home visual and interaction concept** and an
**implementation-ready specification** that makes Home a focused *Today*
experience answering **"What matters for me today, and what should I do next?"**
— not a dashboard. The concept must be coherent with the established Soft Aurora
design system, grounded in what actually exists in the codebase and domain, and
must resolve the concrete defects the existing Home audit found. No production
code is written until the concept is approved.

---

## 3. SCOPE

<!-- ORCHESTRATOR ONLY -->

**In scope (Architect deliverable):**
- A single, concrete Home concept statement (primary purpose, emotional tone,
  what Home is and is not).
- A visual + interaction specification addressing at minimum: primary purpose;
  information hierarchy; visual composition; today's habits/tasks representation;
  completion / incomplete states; progress treatment; streak treatment (if
  appropriate); primary and secondary actions; empty state; all-complete state;
  day-progression behavior; interaction model; animation/motion principles;
  light/dark behavior; accessibility; relationship to Tasks and Insights; what
  existing Home elements are retained / removed / redesigned; required
  domain/state changes; navigation implications; implementation boundaries;
  acceptance criteria.
- Explicit decisions and rejected alternatives where useful.
- ADR entries for any genuine architecture/product-direction shift (Architect
  owns `DECISIONS.md`).

**Out of scope:**
- Implementing the concept (deferred until approval).
- Redesigning the rest of the product (Habits, Insights, Tasks, Calendar,
  Settings, Onboarding) beyond documenting Home's relationship to Tasks/Insights
  and any strictly required shared-component contract.
- Inventing domain capabilities Momentum does not have; adding dependencies;
  networking; schema rewrites.
- Changing offline/privacy guarantees, bottom-nav composition (ISSUE-002), or
  other locked decisions without an explicit, justified ADR.
- Commit / push (Human-in-the-Loop).

---

## 4. REQUIREMENTS

<!-- ORCHESTRATOR ONLY -->

1. The concept must answer the user's framing: *"What matters for me TODAY, and
   what should I do next?"* It must not become a dashboard of every useful metric.
2. Ground every decision in the real Home implementation and the real domain
   state models (open the files; do not design from the audit's prose alone).
3. Use the installed Taste/design skills during exploration to reason about
   hierarchy, composition, density, motion, emotional tone and product taste —
   select only the relevant ones, and do not blindly apply all of them.
4. Be coherent with Soft Aurora: atmospheric color/lighting that supports
   meaning/state, not decoration; both themes intentional; reuse existing tokens
   and components where appropriate. Do **not** reinterpret Soft Aurora as
   "cover the screen in gradients/glows."
5. Challenge weak assumptions and explicitly call out conflicts with established
   product direction (e.g. DASHBOARD_SPECS staleness vs code; ISSUE-002 bottom
   nav; ISSUE-005 streak treatment; ISSUE-006 artwork strategy; ISSUE-020 light
   contrast; ISSUE-022 reduced-motion scope).
6. The specification must be concrete enough for a Developer to implement and a
   Reviewer to verify — specific values, states, and boundaries, not vague UX
   suggestions. Numeric tokens must trace to a source (design system or a
   recorded derivation), never be invented silently.
7. State explicitly what is retained, removed, and redesigned, naming the actual
   files/components.
8. State required domain/state changes, or explicitly state "none required".

---

## 5. KNOWLEDGE REFERENCES

- `KNOW-FEAT-001` — `docs/product/features/dashboard/DASHBOARD_SPECS.md`
  (Home behavior; note: older than ADR-31 — treat as historical where it
  conflicts with the locked design system or current ADRs).
- `KNOW-UI-001`, `KNOW-UI-002`, `KNOW-UI-003` — `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md`.
- `KNOW-ARCH-001`…`004` — `docs/architecture/ARCHITECTURE.md`,
  `docs/architecture/decisions/DECISIONS.md`.
- `KNOW-DATA-002` — activity edit semantics (append-only completion history).
- `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` — Home/Today (§§8.1–8.3, 7.8.13, 7.14,
  7.15, 7.22, 7.26) and opportunities HO-1…HO-4.
- ADRs: ADR-22 (bottom nav + Home redesign), ADR-28 (Habits header reference),
  ADR-29…ADR-35 (Phase 2C Home Hero), ADR-37/38/39 (hero edge/veil/dialog).
- Relevant issues: ISSUE-002, ISSUE-005, ISSUE-006, ISSUE-007, ISSUE-020,
  ISSUE-021, ISSUE-022.

---

## 6. KEY FILES (grounding for the Architect)

Current Home surface:
- `app/src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt`
- `app/src/main/java/com/momentum/app/presentation/screens/today/HomeHero.kt`
- `app/src/main/java/com/momentum/app/presentation/screens/today/TodayViewModel.kt`
- `app/src/main/java/com/momentum/app/presentation/screens/today/TargetEntryDialog.kt`
- `app/src/main/java/com/momentum/app/presentation/components/ActivityCard.kt`,
  `ProgressOrb.kt`, `MomentumBadges.kt`, `MomentumHeroArtwork.kt`,
  `MomentumComponents.kt`, `EmptyState` (in `MomentumComponents.kt`),
  `LiquidGlass.kt`, `HabitIconTile.kt`
- `app/src/main/java/com/momentum/app/presentation/aurora/MomentumAurora.kt`
- `app/src/main/java/com/momentum/app/presentation/screens/habits/HabitsHero.kt`
  (the audit's named "reference composition")
- `app/src/main/java/com/momentum/app/presentation/theme/**` (Color, Type, Tokens,
  Shapes, Motion, Theme)
- `app/src/main/java/com/momentum/app/presentation/navigation/MomentumNavGraph.kt`,
  `Routes.kt`
- Domain/state: `app/src/main/java/com/momentum/app/domain/usecase/**`
  (`GetTodayScheduleUseCase`, `TodayItem`, `TodaySections`, `partitionTodayItems`),
  `domain/engine/CompletionEngine.kt`, `domain/model/**`.
- Tests that currently pin Home contracts: `HomeHeroAssetsTest`,
  `HomeScrollSurfaceTest`, `HomeSectionMotionTest`, `HomeAccessibilityTest`,
  `QuickAddTest`, `TodaySectionsTest`, `TodaySchedulingTest`,
  `androidTest/.../HomeHeroCompositionTest.kt`.

---

## 7. CURRENT ISSUES CARRIED

- **ISSUE-002** (High) — bottom-nav composition specified three ways; unresolved.
  Home = a bottom-nav destination (ADR-22). Do not silently re-decide navigation.
- **ISSUE-005** (Medium) — streak visual treatment conflict (design system rejects
  fire/emoji gamification). Home currently shows a streak badge via `ActivityCard`.
- **ISSUE-006** (Medium) — Aurora artwork programmatic vs bundled; Home uses the
  bundled hero artwork pair.
- **ISSUE-020** (Low) — Home date label `textSecondary` measures 3.11:1 in Light
  (below 4.5:1). A type/token remedy is expected to be in scope.
- **ISSUE-022** (High) — reduce-motion override reaches Home only (1 of 14 call
  sites); accessibility correctness, not taste.
- **Audit CP-1** — Home's "3 of 5 completed" renders at Body 16 Regular, smaller
  than the greeting; numeric hierarchy is inverted. Directly relevant.
- **Audit Q8** — task.md states the separate Tasks tab is a **product** question;
  address Home's relationship to Tasks, do not unilaterally remove the tab.

### Product ruling received (human, 2026-09-30)

- **Encouragement is retained; the Settings toggle remains functional.** The
  Home encouragement line is **ON by default** and controlled by the existing
  `AppSettings.motivationalMessagesEnabled` toggle. When disabled it is removed
  **completely** with natural layout reflow — no reserved/empty space, no
  placeholder. The ON and OFF compositions must each be independently
  intentional. This **supersedes ADR-48 §4's removal of the slot** and resolves
  the previously flagged conflict: the Architect must amend ADR-48 (append-only)
  and redesign the line's content so it is state-specific rather than filler
  (audit §17.3f).

---

## 8. ARCHITECTURAL DECISIONS AND PLAN

<!-- ARCHITECT OWNER -->

**Status:** Concept **approved**; premium refinement pass complete (see
"Refinement" below + ADR-48 Amendment Note 1); implementation in place and
validated (§9). The R3 / AC 9 / AC 17 acceptance-measurement reconciliation is
recorded in "Refinement" item 13 and ADR-47 Amendment Note 1.

### Relevant Files

**Primary (rewrite/edit):**
- `presentation/screens/today/HomeHero.kt` — rebuilt on ADR-47.
- `presentation/screens/today/TodayScreen.kt` — hierarchy, states, loading, FAB.
- `presentation/screens/today/TodayViewModel.kt` — replace filler `messageFor`
  with state-specific `encouragementFor`; expose hero inputs; milestone signal.
- `presentation/theme/Type.kt` — add `MomentumFigureDisplay` (derived; see below).
- `presentation/theme/Motion.kt` — add `MomentumMotion.reward = 650` (DS §28).
- `presentation/components/MomentumFab.kt` — **new** shared FAB.
- `presentation/screens/habits/HabitsScreen.kt` — **FAB call site only** (ADR-47 §6).
- `res/values/strings.xml` — add the hero + encouragement strings and replace the
  generic `today_message_*` set (list in §18).

**Read-only (reuse, do not modify):** `presentation/components/ActivityCard.kt`,
`CompletionButton.kt`, `ProgressOrb.kt` (used via existing params),
`MomentumComponents.kt` (`EmptyState`, `MomentumSectionHeader`),
`MomentumHeroArtwork.kt`, `presentation/aurora/MomentumAurora.kt` (Home passes
`animated = false`), `domain/usecase/UseCases.kt` (`TodaySections`,
`partitionTodayItems`), `domain/engine/CompletionEngine.kt`,
`presentation/theme/Tokens.kt`, `Motion.kt`, `Shapes.kt`,
`presentation/navigation/MomentumNavGraph.kt`, `Routes.kt`.

**Tests to update (keep the retained outcome, drop the removed machinery):**
`HomeHeroAssetsTest`, `HomeScrollSurfaceTest`, `HomeSectionMotionTest`,
`HomeAccessibilityTest`, `QuickAddTest`, `androidTest/HomeHeroCompositionTest`.
**Tests to add:** progress-hierarchy, empty-day-height, milestone/reward,
loading-skeleton, "no infinite animation on Home".

### Architecture / Implementation Plan

#### 1. Home concept

Home is a **focused Today experience**, not a dashboard. It answers one question
— *"What matters for me today, and what should I do next?"* — as a single
scroll: **today's progress, stated once**, followed by **today's actionable
rows**. Emotional tone: *calm focus at rest, quiet reward at completion,
momentum framed as progress and continuity — never guilt, never gamification.*

**Home is:** an action surface (today's eligible habits **and** tasks as rows,
with completion as the primary interaction).
**Home is not:** an analytics dashboard; a habit library; a calendar; a history
screen; a management screen; a second copy of Tasks or Insights.

#### 2. Information hierarchy (top → bottom)

1. **Identity** — date (eyebrow) + greeting (time-of-day).
2. **Today's progress — exactly one representation** (ADR-48):
   - `0 < done < total`: fraction **`done / total`** at the figure style
     (Display 40/48, 600) + caption **"completed today"**; the ring is the
     visualization only (`showPercent = false`).
   - `= 100%`: the statement **"Today is complete"** replaces the fraction; the
     ring is full.
   - `total == 0` (empty): **no progress block**.
3. **Remaining today** — section header + rows (the "what next").
4. **Completed today** — section header + quieter rows.
5. **Quick Add** — floating; suppressed on an empty day.

#### 3. Visual composition

- **One scroll surface** (`LazyColumn`), Hero as item 0, all rows and both
  section headers items of it (retain; ADR-28/29/31/32).
- **One Aurora-backed Hero** (retain ADR-31/33/37/39 outcomes): full-bleed
  artwork from the physical window top edge; date/greeting top-anchored under the
  status bar; progress block below the greeting on the same surface; Settings as
  a floating `LiquidGlassIconButton` top-right.
- **Readability = one constant scrim + the artwork's bottom dissolve** (ADR-47
  §2), replacing the sampled veil. Scrim = a single page-background
  (`c.background`) vertical gradient: alpha 0 at the Hero's top edge, rising to
  one capped peak behind the header and progress block — peak **≤ 0.35** —
  then handing off to the artwork bottom dissolve. Hero copy is `textPrimary`
  (ADR-47 §7). *Derived value note:* the 0.35 cap is a **derived** ceiling, not
  a design-system constant; basis = the audit's "keep peaks ≤ 0.65" floor and
  ISSUE-020's method, lowered because the copy is now `textPrimary`. The
  implementation must **measure** the date/greeting/fraction against the light
  and dark artwork and confirm ≥ 4.5:1 (Record the measured ratio in the
  Developer handoff.)
- **Rows:** shared `ActivityCard` (12 dp radius, `GlassLevel.CARD`; completed →
  `GlassLevel.CONTROL` + success tile). Home passes `streak = 0` (ADR-48 §5) and
  `scheduleLabel = homeMetadataLabel(item)` as today (time + live target progress).
- **Section headers:** keep the shared `MomentumSectionHeader` unchanged
  (its global label hierarchy is a separate, out-of-scope issue). Headers keep
  `semantics { heading() }`.
- **Empty day:** compact header only (see §5).

Height rule (Hero): `height = statusBarInset + HeroTopInset + headerHeight +
HeaderProgressGap + progressHeight + HeroBottomInset`, content-driven. When
`totalCount == 0`, `progressHeight = 0` **and** no collapse headroom and no
weighted spacer are added, so the Hero is header-height. Whitespace tokens:
reuse `MomentumSpacing` (`screen`, `standard`, `tight`, `compact`, `section`)
where they map; only genuinely Hero-specific values stay private with a
one-line derivation comment (ADR-47 §8).

#### 4. Numeric hierarchy (CP-1)

- The fraction is the screen's headline: **Display token (40 sp / 48 lh / 600)**
  — the locked scale's largest token (DS §4). Greeting stays `headlineMedium`
  (Title 20 sp / 600) and date stays `labelLarge` (Label 13 sp / 500); both are
  now *below* the figure.
- **One numeric only**: remove the ring's percentage
  (`ProgressOrb(showPercent = false)`); there is no second fraction and no
  "remaining" count in the Hero (the section header + list carry "what next").
- **Figure style:** add `MomentumFigureDisplay` to `Type.kt` =
  Display token + `FontFeatureSettings("tnum")` **if** Manrope exposes tabular
  figures; otherwise the Display token unchanged (value is centred, so width
  changes are invisible). *Derivation basis:* Display token is specified (DS §4);
  tabular figures are **demanded-not-specified** and must be verified at
  implementation. No new weight is registered (600 is the scale's Display
  weight); the 700-registration question belongs to audit HO-1 and is not taken.

#### 5. States

- **Incomplete (`0 < done < total`):** fraction + "completed today" + ring
  (partial, no inner number, `glowEnabled = false`); Aurora `PROGRESS`.
- **All-complete (`done == total > 0`):** "Today is complete" (DS §14) + full
  ring; Remaining section hidden; Aurora steady `COMPLETION`; the transient
  `MILESTONE` moment fires once on the `0 -> 1` transition (ADR-48 §3).
- **Empty (`total == 0`):** slim Aurora header (date + greeting + Settings), no
  progress block, no collapse headroom; `HomeEmptyState` immediately below
  (`EmptyState` with `icon = null`, existing title/body/CTA strings). **Quick Add
  FAB hidden** so the empty CTA is the single add affordance. Aurora `REST`.
- **Loading:** static skeleton (Hero header band + 2 ghost rows) in theme
  surface tokens; a single "Loading today" label; decorative shapes have
  `contentDescription = null`. No spinner, no shimmer required.
- **Error:** unchanged (`TodayError`); styling may adopt the loading state's
  spacing but no new copy.

#### 6. The completion moment (CP-2 / HO-3)

- `TodayEffect.Celebrate` remains the typed one-shot signal but is **no longer
  shown as a snackbar**. The screen maps it to a transient milestone:
  - Aurora → `AuroraState.MILESTONE` for `MomentumMotion.reward` (650 ms), then
    `COMPLETION`.
  - `ProgressOrb` one-shot `glowEnabled = true` for the same window (then back
    to static).
  - **One** `HapticFeedbackType.Confirm` (replaces the `LongPress` semantic).
  - No particles/confetti/sound (DS §19/§21/§33).
- Reduced motion: skip the transient (no glow); the figure/statement change
  instantaneously; the haptic still fires.

#### 7. Streaks (ISSUE-005)

Home rows pass `streak = 0`, so `ActivityCard` suppresses the badge. Streaks
remain on Habit Detail/Insights; `MomentumBadges.kt` is **not** modified and
ISSUE-005's visual treatment is deferred to its own surface.

#### 8. Encouragement line (retained; state-specific — supersedes ADR-48 §4)

Per the human product ruling (§7), the line is **retained, ON by default**, and
controlled by `AppSettings.motivationalMessagesEnabled`. When OFF it is removed
**completely with natural reflow** — no reserved space, no placeholder. Content
is redesigned to be a pure function of live state (full spec in "Refinement" §8):
`allComplete`/`isEmpty` → no line; a remaining item with `currentStreak >= 2` →
"Keep your {n}-day streak going."; otherwise → "Next up: {title}" (highest-
priority remaining item). No tagline, no bucket lines, no progress number
(ADR-48 §1's one representation stands). The Settings toggle is therefore
**functional again**; no Settings change is required. Recorded in ADR-48
Amendment Note 1.

#### 9. Day progression

Keep the existing local-midnight refresh (`refreshTick`) and the time-band
greeting (`5–11 / 12–16 / 17–21 / else`). Aurora maps
`empty → REST`, `0 < p < 1 → PROGRESS`, `p == 1 → COMPLETION`, with the transient
`MILESTONE` on the `0→1` transition only.

#### 10. Interaction model (unchanged contracts)

Row tap → activity detail; completion disc → toggle (or open target dialog for
target-based activities); Quick Add → Create Item (Habit/Task); Settings → from
the Hero header. No row management on Home. Sections fixed as Remaining →
Completed; empty sections never rendered. Keep `Modifier.animateItem` reflow.

#### 11. Motion

- Principle: **motion on change, never on a clock.**
- Home runs **no `rememberInfiniteTransition`**: the Aurora is static on Home
  (`animated = false`; state is expressed by intensity) and `ProgressOrb` has
  `glowEnabled = false` except the one-shot milestone.
- Adopt `MomentumMotion.standard` (280 ms) for row reflow + fade and the progress
  sweep (replacing the inline `220`); `MomentumMotion.reward` for the milestone.
  **`reward` must be added** — it does not exist in `Motion.kt` today (verified;
  see ADR-48 Amendment Note 1) — value 650, derived from DS §28. Press feedback
  stays `glassPress`. Hero collapse stays layout-only (height 1:1 with scroll).
- The full, per-trigger motion specification (purpose, timing token, interruption,
  reduced motion) is "Refinement" §6; where §11 and that table differ, the table
  wins.
- Reduced motion: null every decorative spec (`null` specs, not shortened),
  freeze the sweep, skip the milestone glow; haptics remain.

#### 12. Light / dark

Both themes use the identical composition; artwork is theme-selected
(ADR-30). Hero copy is `textPrimary` (fixes ISSUE-020). Completed rows stay
full-opacity with `CONTROL` fill + success tile (never alpha the text). Skeleton
uses theme surface tokens. No hardcoded colours.

#### 13. Accessibility

≥ 48 dp targets (`CompletionButton` 48, FAB 56, Settings 48); both section
headers `semantics { heading() }`; completion control keeps its action-naming
label and `Role.Checkbox`; the progress block exposes **one** merged semantics
description ("N of M completed today") so the figure is not double-read and the
ring is decorative; completion is signalled by section membership + control, not
colour alone; content-driven Hero height keeps large font sizes unclipped.

#### 14. Relationship to Tasks and Insights (document only)

Home = act today (today's habits **and** tasks appear here as rows); Tasks =
shared management/filter surface (`ActivitiesScreen`); Insights =
history/analysis (Calendar lives inside Insights). **Do not** change bottom-nav
composition (ISSUE-002) or remove the Tasks tab (audit Q8 is a product
question) → raise Q8 to the Orchestrator if a decision is needed.

#### 15. Retained / removed / redesigned

**Retained:** one scroll surface + Hero-as-item + clamped-px collapse; Remaining/
Completed ordering + empty-section hiding; `ActivityCard`, `CompletionButton`,
`MomentumSectionHeader`, `EmptyState`, `AuroraBackground`,
`MomentumHeroArtwork`, `ProgressOrb` (existing params), `TodaySections`/
`partitionTodayItems`, `CompletionEngine`, MVI (`TodayEvent`/`TodayUiState`/
`TodayEffect`), midnight refresh, greeting, per-row glide.
**Removed:** the Hero's sampled 48-step veil + 5 `Veil*` anchors + open-band
valley; ring scroll-scale; artwork parallax/overscan; 24-step S-curve; fixed
empty-day `CollapseHeadroom`/weighted void; ring percentage; motivational
message + 100% snackbar; Home streak badge; `Star` empty glyph; the bare
loading `Text`; the divergent `QuickAddFab`.
**Redesigned:** `HomeHero` (constant scrim, content-height, one-figure block,
milestone); `TodayScreen` (states, skeleton, FAB hidden on empty); the FAB
(shared `MomentumFab`); loading state.

#### 16. Required domain/state changes

**No domain, repository, Room or DataStore change is required.**
Presentation-only additions: a derived `heroState` (from `TodaySections`), the
retained-but-redesigned `encouragementFor` message path, a transient milestone
tick local to the screen, and the `MomentumMotion.reward` token.
`AppSettings.motivationalMessagesEnabled` is unchanged and **functional** per the
human ruling (ADR-48 Amendment Note 1).

#### 17. Navigation implications

**None.** Home stays `Routes.TODAY`; Settings remains a pushed route from the
header; Quick Add opens the existing `showCreateItem`; rows navigate to detail.
No new routes; nav graph untouched.

#### 18. Implementation boundaries

Touch only `presentation/screens/today/**`,
`presentation/components/MomentumFab.kt` (new), `presentation/theme/Type.kt`
(the one derived style), `res/values/strings.xml`, and the **FAB call site** in
`presentation/screens/habits/HabitsScreen.kt`. Do **not** modify `domain/**`,
`data/**`, `notification/**`, navigation, `MomentumHeroArtwork`, `AuroraBackground`
internals, `ActivityCard`, `CompletionButton`, `ProgressOrb` internals, other
screens, dependencies, or `opencode.json`. Do not commit/push.

**Strings:** add `today_hero_fraction` ("%1$d / %2$d"),
`today_hero_progress_label` ("completed today"), `today_hero_complete`
("Today is complete"), `today_hero_progress_desc`
("%1$d of %2$d completed today"), `today_encouragement_streak`
("Keep your %1$d-day streak going."), `today_encouragement_next`
("Next up: %1$s"). **Replace** the six generic `today_message_*` strings (no
longer referenced); remove `today_celebrate_all_done` and `hero_progress_count`.
(The "Today is complete" / "completed today" wording is specified by DS §14; the
encouragement wording is the Architect's state-specific copy under the human
ruling — ADR-48 Amendment Note 1.)

#### 19. ADRs recorded

`ADR-47` (Home concept + Hero simplification) and `ADR-48` (one progress
representation + completion moment). Both appended to `DECISIONS.md`; no old ADR
is rewritten.

#### 20. Acceptance criteria (Developer + Reviewer)

1. **One progress representation:** Home renders one numeric representation of
   today's progress. Source check: no `showPercent = true` on Home; no `%`
   progress text; no duplicate fraction/"remaining" count in the Hero.
2. **Hierarchy:** the fraction uses the Display-based figure style; the greeting
   is ≤ Title; the Hero places no `textSecondary` over artwork.
3. **Empty day:** with `totalCount == 0`, the Hero adds no collapse headroom and
   no weighted spacer; the empty state is directly beneath; the Quick Add FAB is
   not rendered; no `Star` glyph.
4. **Completion moment:** `AuroraState.MILESTONE` and `MomentumMotion.reward` are
   referenced by Home; one `HapticFeedbackType.Confirm` and **no** snackbar on
   `0→1`.
5. **No permanent animation:** no `rememberInfiniteTransition` is reachable on
   Home; reduced motion nulls every decorative spec.
6. **FAB:** one shared `MomentumFab`, used by Home and Habits, with one material
   and the `prominent` glyph; `QuickAddTest` updated.
7. **Loading:** no bare `Text("Loading today…")`; a skeleton with one accessible
   label exists.
8. **Streak:** Home rows pass `streak = 0`; `MomentumBadges.kt` unchanged.
9. **ISSUE-020:** date/greeting/figure use `textPrimary`; Developer reports a
   **measured** ≥ 4.5:1 against both theme artworks (ISSUE-020 region-sample
   method — Refinement item 13).
10. **Both themes** render the composition; no hardcoded colours.
11. **Accessibility:** 48 dp targets; two heading semantics; reduced motion;
    one merged progress description; large-font render does not clip.
12. **Tests:** the six named tests are updated to the retained outcomes and pass
    with the new assertions; `assembleDebug` green.
13–18. Added by the premium refinement pass — see "Refinement" §12 below.

#### 21. Rejected alternatives (summary; full rationale in ADR-47/48)

- Keep the 771-line Hero and only fix the number (leaves the central finding and
  the 0.12-alpha margin).
- Adopt `HabitsHero` wholesale (bottom-anchor all copy, delete the scrim + mask):
  conflicts with the approved `DECISIONS` §5 / DS §12 top greeting and
  reintroduces the camera-cutout defect ADR-35 fixed.
- Withhold the artwork / solid header (discards the signature atmosphere for no
  measured benefit).
- Percentage as the sole headline (loses the DS §14 fraction).
- Day-as-shape (audit §19.8): needs a new mark primitive (HO-2), touches other
  screens — out of scope.
- Remove the motivational slot entirely (ADR-48 §4's original choice): **rejected
  by the human ruling** (`task.md` §7) — the line is retained, ON by default, and
  removed completely only when the toggle is OFF. See ADR-48 Amendment Note 1
  and "Refinement" §8.

### Refinement (premium design pass) — TASK-015 second Architect pass

**Precedence:** where this section and §8's earlier subsections differ, **this
section wins**. The differences are itemized in item 2.

**Skills actually loaded and used.** Taste: `design-taste-frontend`,
`minimalist-ui`, `high-end-visual-design`. Compose (Chris Banes):
`using-chrisbanes-skills` (routing), `compose-component-design` (+ its
`slot-apis` reference), `compose-state-and-effects` (+ `side-effects`),
`compose-animations` (+ `animated-content`), `compose-performance`
(+ `deferred-reads`), `compose-ui-testing-patterns`.

**Design read (`design-taste-frontend` §0).** "Reading this as: a premium
consumer utility app for a self-tracking individual, with a calm/minimalist
language, leaning toward the locked Soft Aurora design system." Inferred dials:
`DESIGN_VARIANCE 5`, `MOTION_INTENSITY 3`, `VISUAL_DENSITY 2`. Where a skill
default conflicts with the locked design system (e.g. its "no glassmorphism",
"avoid gradients", "no pills" defaults), **the design system wins** (source
order); what is taken from the skills is restraint, hierarchy, rhythm, motivated
motion and state completeness — not their web-specific material bans.

#### 1. Retained unchanged

- The Home concept, purpose and tone; focused Today, not a dashboard.
- One scroll surface; Hero as item 0; clamped-px collapse via
  `NestedScrollConnection`; Remaining → Completed ordering; empty-section hiding.
- The `done / total` progress headline at Display scale via
  `MomentumFigureDisplay` (ADR-48 §1–2), `ProgressOrb(showPercent = false)`.
- The existing artwork pair and `MomentumHeroArtwork` contract (theme-selected,
  no tint, no new/procedural artwork); ADR-47's Hero simplification outcomes.
- No permanent infinite animation on Home; change-triggered motion.
- The 100% moment as transient `AuroraState.MILESTONE` + reward + one haptic,
  no snackbar (ADR-48 §3; token correction below).
- Streaks removed from Home rows (ADR-48 §5); shared `MomentumFab` direction.
- `ActivityCard`, `CompletionButton`, `MomentumSectionHeader`, `EmptyState`,
  `TodaySections`/`partitionTodayItems`, `CompletionEngine`, MVI, midnight
  refresh, greeting, presentation-only scope, navigation untouched.

#### 2. What was refined

- **R1 — One left axis.** The Hero's progress block becomes a
  `Row(verticalAlignment = CenterVertically)`:
  `Column(weight(1f)) { figure; caption }` + `ProgressOrb` trailing — start-
  aligned with the date/greeting on `MomentumSpacing.screen` (20 dp). Supersedes
  §3's centered stack and §2's "ring below" composition.
- **R2 — Ring 80 dp, derived.** `HeroRingSize = 80.dp`
  (= Display line height 48 dp + 2 × `MomentumSpacing.standard` 16 dp).
  Supersedes the unsourced 96 dp / `RingBounds`. The ring is a supporting mark;
  the figure is the hero object.
- **R3 — One constant scrim with two per-theme peaks.** Fixed 4-stop
  `c.background` gradient (`0f→0`, `0.20f→peak`, `0.80f→peak`, `1f→0`), peak =
  `ScrimPeakLight` / `ScrimPeakDark` (**final, measured: 0.26 / 0.22**; bounded
  [0.15, 0.35]). Supersedes §3's single ≤0.35 cap and the ADR-35 anchor set.
  Acceptance measurement = ISSUE-020 region sample (item 13; ADR-47 Amendment
  Note 1).
- **R4 — Aurora intensity is tweened on state change** over
  `MomentumMotion.emphasis` (450 ms) through a private `HomeAurora` wrapper;
  drift stays off. Supersedes §11's "static intensity" (state still static at
  rest; only the *change* animates).
- **R5 — Encouragement retained and redesigned** (human ruling; §8 above and
  item 8 below). Supersedes §8's removal.
- **R6 — Full motion specification** (item 6). Adopts `MomentumMotion` tokens;
  **adds `MomentumMotion.reward = 650`** (it does not exist today — verified;
  ADR-48 Amendment Note 1).
- **R7 — Loading skeleton matches the refined composition** (header lines +
  progress row + two row ghosts) and loading→content is one `AnimatedContent`
  fade keyed by branch. Supersedes §5's generic skeleton note.
- **R8 — Error recovery shows the skeleton while retrying** (local flag), then
  the same fade.
- **R9 — Empty state is the single card on the page**; FAB hidden; no `Star`.
  (Confirms §5; adds the one-card rationale.)
- **R10 — FAB appear/disappear is animated** (`fast`); press feedback stays
  `glassPress`.
- **R11 — Performance constraints** (item 10): the collapse read moves out of
  `TodayScreen` into `HomeHero`; the Aurora intensity animation is localized;
  no per-frame allocations.
- **R12 — Testing requirements** (item 11): the six named tests are rewritten
  around retained outcomes; new plain state-driven UI tests are added.

#### 3. Why each refinement improves the experience

- **R1** removes the "centered number under a left-aligned header" axis mismatch;
  one vertical axis gives the Hero rhythm and makes the reading order obvious
  (date → greeting → figure → line), while the trailing ring keeps the
  composition asymmetric rather than a generic centered stack. The progress row
  is also shorter than the stacked version, so more of the list is visible.
- **R2** stops a decorative mark out-weighing the message: at 96 dp the ring was
  the largest object on the screen; at 80 dp the Display figure is unambiguously
  first, and the derivation removes an invented constant.
- **R3** keeps the Aurora atmospheric instead of ornamental: one profile, two
  constants, measured. It removes the five-anchor machinery ADR-47 deleted and
  still guarantees the contrast floor.
- **R4** makes the completion feel alive without a clock: the Aurora *responds*
  to the state change instead of jumping or drifting.
- **R5** turns a filler slot into the only place Home speaks to the user in a
  sentence — and makes it say something true about today.
- **R6** makes every animation answer "what does it communicate?" and gives the
  Developer one table instead of scattered literals.
- **R7/R8** complete the interaction-state cycle (loading/error/content) so the
  screen never shows a bare string or a frozen error.
- **R9** applies "a card earns its place": on an empty day the empty state is
  the content, so it is the one surface; there is one add affordance, not two.
- **R10** makes the FAB's presence a state change, not a pop.
- **R11** keeps the premium feel from costing frames: the scroll path no longer
  invalidates composition, and the removed veil machinery removes per-size
  allocations.
- **R12** keeps the design verifiable: tests pin outcomes (hierarchy, states,
  reflow, motion) instead of the machinery that was deleted.

#### 4. Taste skill(s) per refinement

| Refinement | Taste skill that informed it |
|---|---|
| R1 one axis | `design-taste-frontend` (anti-center bias at VARIANCE > 4; "does the eye know where to go first"); `minimalist-ui` (typographic hierarchy) |
| R2 ring size | `design-taste-frontend` (hero font-scale discipline: plan the message size and the mark size together); `high-end-visual-design` (restraint) |
| R3 scrim | `high-end-visual-design` (atmospheric depth, not a plate; readability without flattening); `design-taste-frontend` (mandatory contrast check) |
| R4 Aurora tween | `high-end-visual-design` (no instant state changes; motion choreography); `design-taste-frontend` (motion must be motivated; no perpetual loops) |
| R5 encouragement copy | `design-taste-frontend` (copy self-audit: plain, specific language; one register; no AI filler); `minimalist-ui` (copy is not decoration) |
| R6 motion spec | `high-end-visual-design` (choreography); `minimalist-ui` (quiet sophistication); `design-taste-frontend` (reduced motion mandatory) |
| R7/R8 loading/error | `design-taste-frontend` §4.5 (full state cycles; skeletal loaders matching the final layout; no generic spinners) |
| R9 empty state | `design-taste-frontend` §4.4 (cards only when elevation communicates hierarchy) + no-duplicate-CTA-intent rule |
| R10 FAB | `high-end-visual-design` (press physics); `design-taste-frontend` (tactile `scale(0.98)`) |
| R11/R12 | no taste claim (technical) |
| Overall | `design-taste-frontend` §1 dials + §11 theme lock (one theme, no section inversion); `minimalist-ui` (color scarcity, macro-whitespace rhythm) |

#### 5. Chris Banes skill(s) per refinement

| Refinement | Compose skill that informed it |
|---|---|
| R1/R2 (Hero structure, trailing ring) | `compose-component-design` (`slot-apis`: the component owns structure and spacing; constrained regions stay primitives) |
| R3 (scrim draw) | `compose-performance` (`deferred-reads`: draw-time reads; cached brushes; no per-frame list rebuild) |
| R4 (Aurora tween) | `compose-animations` (`animate*AsState` for one value) + `compose-performance` (localize the read to its consumer; the one-shot cheap-value exception) |
| R5 (line show/hide) | `compose-animations` (`AnimatedVisibility` for show/hide with enter/exit) + `compose-component-design` (nullable optional content so the container **and its spacing** disappear) |
| R6 (motion table) | `compose-animations` (API-choice table; `animateItem` specs; spring interruption) + `compose-state-and-effects` (`side-effects`: key the milestone reset by the semantic input) |
| R7/R8 (loading/error) | `compose-animations` (`animated-content`: `AnimatedContent` with a branch `contentKey` so a payload refresh does not re-animate) + `compose-state-and-effects` (`side-effects`: one-shot retry flag in a keyed effect) |
| R9/R10 (FAB) | `compose-animations` (`AnimatedVisibility`) |
| R11 (performance) | `compose-performance` (`deferred-reads`: frame-rate state read in layout/draw, kept at its lowest owner; measure one transition before deeper repair) + `compose-component-design` (modifier at the root; no flag matrix) |
| R12 (tests) | `compose-ui-testing-patterns` (plain state-driven UI tests; semantics first; deterministic controlled state; rewrite removed-machinery tests around retained outcomes) |
| Routing | `using-chrisbanes-skills` |

#### 6. Final motion specification

Principle: **motion communicates state or change; nothing runs on a clock.**
No `rememberInfiniteTransition` is reachable on Home.

| # | Trigger | Purpose | API / timing | Interruption | Reduced motion |
|---|---|---|---|---|---|
| 1 | Initial Home appearance (Loading → Success) | Reveal the day once, calmly | `AnimatedContent` (branch-keyed) `fadeIn(tween(standard, easeOut)) togetherWith fadeOut(tween(fast))`; no per-element cascade | Retargets if the branch changes | `EnterTransition.None` / `ExitTransition.None` |
| 2 | Progress value change (a completion, a target edit) | Show the day's progress moving | `ProgressOrb`'s internal `animateFloatAsState(spring(StiffnessMediumLow))`; `animate = !reduceMotion` | Spring retargets to the new value | `animate = false` (instant, state still visible) |
| 3 | Habit completion (the control itself) | Acknowledge the tap | Existing `CompletionButton` pop + `ActivityCard` tile tint (unchanged) | Existing | Existing `tween(0)` |
| 4 | Incomplete → complete row move | Show the row relocating, not vanishing | `Modifier.animateItem(fadeInSpec = tween(standard), placementSpec = spring(StiffnessMediumLow), fadeOutSpec = tween(fast))` | `animateItem` retargets | All specs `null` (instant relocation) |
| 5 | Final item completion | Same row move as (4) + the milestone below | As (4), no artificial delay before (6) | As (4) | As (4) |
| 6 | 100% milestone / reward | Mark the day's completion once | Aurora intensity → MILESTONE over `tween(emphasis, easeOut)` then back over `emphasis`; ring `glowEnabled = true` for `reward` (650); one haptic (`Confirm` if available, else `LongPress`) | Keyed by `milestoneTick`; a re-trigger restarts; an un-complete does not cancel the transient | Skip the Aurora tween and glow; haptic still fires; statement changes instantly |
| 7 | Encouragement appears / disappears (toggle, or a state change that starts/stops a rule) | Make the optional line's reflow readable, never reserved | `AnimatedVisibility(enter = fadeIn(tween(standard, easeOut)) + expandVertically(tween(standard)), exit = fadeOut(tween(fast)) + shrinkVertically(tween(fast)))` | Retargets | `EnterTransition.None` / `ExitTransition.None` — instant, still no reserved space |
| 8 | Empty state | None — the slim Hero + empty block render statically | — | — | Same |
| 9 | Loading → content / content → error | Keep the screen's identity while the branch changes | As (1), same `contentKey` per branch; a Success payload refresh (a completion) does **not** animate | Retargets | None |
| 10 | Error recovery (Try again) | Acknowledge the retry | Local `retrying` flag → skeleton; on Success the (9) fade plays; on failure the error returns via (9) | New retry re-enters skeleton | None |
| 11 | Quick Add appear / disappear (populated ↔ empty) | Make the FAB's presence a state change | `AnimatedVisibility(fadeIn(tween(fast)) + scaleIn(), fadeOut(tween(fast)) + scaleOut())`; press = `glassPress` (existing) | Retargets | None |
| 12 | Hero collapse (scroll) | Layout, not flourish | Height tracks scroll 1:1 (existing clamped px); no decorative interpolation (ring scale/parallax removed) | Continuous by construction | Identical (already layout) |

#### 7. Final artwork / Aurora specification

- **Artwork:** `MomentumHeroArtwork`, `Alignment.BottomCenter`,
  `ContentScale.Crop`, opaque from the Hero's top edge (ADR-39;
  `topDissolve` generates no stop), `bottomDissolve` = the component's shared
  default (88 dp) — Home deletes its private `BottomDissolve` /
  `BottomBlendStart` / `BottomBlendTail` / `BottomBlendSteps` and uses the
  shared contract. No `zoom`, no `parallax` (parameters not passed).
- **Scrim (R3):** one full-size layer above the artwork, `drawWithCache`,
  tinted `c.background`; fixed stops `0f→0`, `0.20f→peak`, `0.80f→peak`,
  `1f→0`; `peak` = `ScrimPeakLight` (0.26) / `ScrimPeakDark` (0.22), bounded
  [0.15, 0.35]. *Derivation:* the old content plateau was 0.30 (Light) /
  0.32 (Dark) against `textSecondary`; the refined copy is `textPrimary`, whose
  relative luminance is ~2.6× lower, so a lower plateau suffices; the audit's
  ceiling is 0.65 and ISSUE-020's measurement method is the acceptance test.
  Final values set by measurement: 0.26 Light / 0.22 Dark (item 13; ADR-47
  Amendment Note 1).
- **Relationship to the progress headline:** the figure sits on the artwork's
  quiet lower-mid band (the same pixels `HabitsHero` anchors its copy to); the
  artwork's brighter upper region sits behind the header only. No copy is placed
  on the artwork's busiest/highest-contrast region.
- **Content-aware height:**
  `height = statusBarInset + HeroTopInset + headerHeight + HeaderProgressGap +
  progressRowHeight + encouragementHeight(0 if none) + HeroBottomInset +
  (CollapseHeadroom only if hasProgress)`. No fixed floor, no weighted spacer.
  Empty day = slim header. Encouragement OFF removes its height completely.
- **Artwork when encouragement is OFF:** unchanged; the Hero simply ends
  `HeroBottomInset` below the progress row. No reserved band.
- **Effects now unnecessary (removed):** artwork zoom/parallax, the bottom
  S-curve, the veil anchors/valley, the ring scroll-scale, the ring's continuous
  glow, and the Aurora drift on Home. **Retained:** the screen-space top mask
  (ADR-35, camera-cutout safety) and the artwork's own bottom dissolve.
- **Aurora:** `REST` / `PROGRESS` / `COMPLETION` mapping unchanged; `MILESTONE`
  transient on the `0→1` transition; intensity tweened on change (R4); drift
  off; no new Aurora art. `AuroraState` remains the only state vocabulary.

#### 8. Final encouragement ON/OFF behavior

**Toggle:** `AppSettings.motivationalMessagesEnabled`, ON by default; the
existing Settings control is unchanged and truthful again.

**ON rules (first match wins; only when the toggle is ON):**
1. `sections.allComplete || sections.isEmpty` → **no line**.
2. Any remaining item with `currentStreak >= 2` → **"Keep your {n}-day streak
   going."** (`{n}` = greatest `currentStreak` among `remaining`; tie → first in
   schedule order). String `today_encouragement_streak`.
3. Otherwise (`remaining.isNotEmpty()`) → **"Next up: {title}"**
   (`{title}` = `remaining.first().activity.title`, the highest-priority
   remaining item). String `today_encouragement_next`.

**OFF:** `Success.encouragement == null`; the Hero renders no line and no space
for it (the `AnimatedVisibility` child and its spacing are absent). Composition:
date → greeting → progress row → bottom inset.

**Why the content is state-specific:** every form is a pure function of live
data; form 2 is the only streak reference on Home (rows are streak-free,
ADR-48 §5) and appears only when a streak is genuinely at stake; form 3 names the
next action. No branch describes a bucket of state, repeats the tagline, or
duplicates the DS §14 fraction — ADR-48 §1's "one representation" holds because
neither form carries a progress number.

**Both compositions are independently intentional:** ON = identity → progress →
one specific line; OFF = identity → progress. Each has a clear first read (the
figure) and a clear second read (the line, or the list); neither has reserved
dead space.

**Typography:** line = `bodyMedium` (14/20 400), `textPrimary`, `maxLines = 2`,
ellipsis; gap above = `MomentumSpacing.standard` (16 dp). Caption "completed
today" = `labelLarge` (13/18 500), `textPrimary`; figure = `MomentumFigureDisplay`;
date = `labelLarge`; greeting = `headlineMedium`. At 100% the statement "Today is
complete" uses `headlineLarge` (Headline 28/34 600) and the caption is absent.

**Accessibility:** the line is plain copy (not a heading), announced after the
merged progress description; it is not a control.

#### 9. Light/dark requirements per element

| Element | Light | Dark | Requirement |
|---|---|---|---|
| Artwork | `habits_hero_light` | `habits_hero_dark` | Theme-selected only (ADR-30); no tint/filter |
| Scrim | `c.background` @ `ScrimPeakLight` | @ `ScrimPeakDark` | All Hero copy measured ≥ 4.5:1; the per-theme peak is the **one allowed asymmetry** (Light artwork is higher-luminance/busier) |
| Aurora | REST/PROGRESS/COMPLETION/MILESTONE same intensities | same | Internal base alpha already differs (0.16/0.20); intentional, not an inversion |
| Hero copy (date, greeting, figure, caption, encouragement) | `textPrimary` | `textPrimary` | ≥ 4.5:1; **no `textSecondary` over artwork** (ISSUE-020 remedy) |
| Ring | track `textPrimary@0.55` (existing), arc `primary` | same | Track ≥ 3:1 against artwork (existing measured) |
| Rows | `GlassLevel.CARD`; completed `CONTROL` + `success` tile | same | No whole-row alpha; Light separation via hairline (existing) |
| Section headers | shared component, `textPrimary` | same | Unchanged |
| Empty state | card + `primary` CTA text | same | CTA text ≥ 4.5:1 (measure both) |
| FAB | solid `primary` + white hairline + shadow | same | Light shadow is invisible (known global CP-4); the solid fill + hairline carry separation — accepted, no new shadow recipe |
| Skeleton | `surface` / `surfaceSoft` blocks | same | Visible in both; no shimmer |
| Milestone | intensity 1.0 | same | Same behaviour both themes |

#### 10. Performance constraints

- **The collapse must not recompose `TodayScreen`.** Pass the collapse state
  into `HomeHero` (a `State<Float>` or `() -> Float` provider) and read it inside
  `HomeHero`; the screen body must not read it. Within the Hero, read frame-rate
  values in `layout`/`draw` lambdas (`graphicsLayer {}`, `drawWithContent`)
  where the property allows; the **height** read stays in `HomeHero`'s
  composition (it must change layout) and is the accepted residual. If profiling
  a scroll transition shows material cost, implement the height with a
  measure-phase lambda — `deferred-reads`'s repair direction — and re-measure;
  do not prescribe the deeper rewrite without that evidence.
- **Stability:** `TodayUiState.Success` stays `@Immutable`; rows receive
  immutable `TodayItem` + stable lambdas; do not add `@Stable` annotations
  without the compiler stability report.
- **Allocation:** the scrim is one cached brush; the artwork mask is one cached
  gradient; the 48-stop per-size `buildList` is gone. No per-frame object
  creation in draw.
- **Aurora intensity (R4)** is animated inside a private `HomeAurora` composable
  so only it recomposes during the one-shot 450 ms — the `deferred-reads`
  one-shot cheap-value exception; not a per-scroll cost.
- **`AnimatedContent`** is branch-keyed so a Success payload refresh (a
  completion) does not replay the loading fade; `LazyColumn` items stay keyed.
- **Effects:** one `LaunchedEffect(milestoneTick)`; one keyed retry reset; no new
  `snapshotFlow`; no `LaunchedEffect(Unit)` beyond the existing initial refresh;
  no `rememberInfiniteTransition`.
- **Component API:** `HomeHero(dateLabel, greeting, figureState, encouragement:
  String?, collapseProgress, reduceMotion, onOpenSettings, onCollapseRangePx,
  modifier)` — primitives + one nullable primitive (the encouragement is a
  design-system-constrained line, so it stays a primitive rather than a slot);
  the progress row is a private composable; `MomentumFab(onClick,
  contentDescription, modifier)` owns its glyph and material.
- **State ownership:** `TodayScreen` owns `collapsePx`, `milestoneTick`,
  `retrying`; the VM owns data; `HomeHero` owns no app state and is previewable
  with sample state.

#### 11. Testing requirements

**Rewrite the six named tests around retained outcomes (do not delete):**
- `HomeHeroAssetsTest` → artwork is the Hero's first layer and fills it; no
  page-background band above the artwork; copy sits on the artwork; the Hero
  ends with its content (no dead space); empty day has no progress block and no
  reserved space; no procedural-artwork tokens; theme-only artwork selection.
- `HomeScrollSurfaceTest` → one `LazyColumn`; Hero is one item; sections in
  order; empty sections hidden; no second scroller.
- `HomeSectionMotionTest` → `animateItem` on rows/headers; reduced motion nulls
  specs (not shortens); fixed ordering.
- `HomeAccessibilityTest` → two heading semantics; 48 dp targets; reduced motion
  reaches Hero/Aurora/list; rows grouped by completion.
- `QuickAddTest` → shared `MomentumFab`; circular; labelled; ≥ 48 dp; opens
  Create Item; not rendered on an empty day.
- `HomeHeroCompositionTest` (instrumented) → artwork == Hero bounds; all copy
  inside the artwork; no top band; Hero bottom within 56 dp of the last visible
  copy; empty-day Hero is slim.

**Add plain, state-driven UI tests (`compose-ui-testing-patterns`):**
- Hero branches — incomplete / complete / empty × encouragement ON/OFF:
  assert the fraction, "completed today", "Today is complete", the encouragement
  text by string, the merged progress description, and that with OFF the
  progress row's bottom sits `HeroBottomInset` above the Hero bottom (no
  reserved line space).
- Empty — FAB absent, empty CTA present, no `Star` glyph.
- Milestone — the transient path exists and the reduced-motion path is
  instant (source/behaviour check).
- Loading — skeleton present, no spinner, one accessible label.

**Determinism:** render controlled state with `setContent` (no ViewModel/DI);
fixed strings; `reduceMotion = true` where bounds are asserted; `mainClock` only
if needed. **Do not add screenshot tooling** (a build change, out of scope); the
visual contract is pinned by bounds + semantics. No test may pin removed
machinery constants (`Veil*`, `BottomBlend*`, `RingScaleHeadroom`,
`HeroRingSize` as 96, `CollapseHeadroom` on the empty day).

#### 12. Updated acceptance criteria

13. **Encouragement ON:** in the incomplete state the Hero shows exactly one of
    the two state-specific lines; at 100% and on an empty day it shows none.
14. **Encouragement OFF:** the line is absent with **no reserved space** —
    bounds-verified (progress row bottom → Hero bottom == `HeroBottomInset`),
    and both ON/OFF compositions render correctly.
15. **One axis:** date, greeting, figure/caption and the encouragement are
    start-aligned on the 20 dp screen axis; the ring is the trailing element of
    the progress row.
16. **Motion:** no `rememberInfiniteTransition` on Home; every motion in item 6
    uses its named token; reduced motion behaves exactly as item 6 specifies;
    `MomentumMotion.reward` exists and is used for the milestone.
17. **Aurora/scrim:** Aurora intensity is tweened (`emphasis`) on state change
    with drift off; scrim peak is per-theme and all Hero copy is **measured**
    ≥ 4.5:1 (values reported; ISSUE-020 region-sample method — item 13).
18. **Performance:** `TodayScreen`'s composition does not read the collapse
    value; `HomeHero` is previewable and state-driven; no per-frame `buildList`
    or per-frame draw allocation.
19. **Tests:** the six rewritten tests and the new plain UI tests pass; no test
    pins removed machinery; `assembleDebug` green.

#### 13. R3 / AC 9 / AC 17 reconciliation — final peaks and the acceptance measurement (Architect, 2026-09-30)

AC 9 (§20.9) and AC 17 (item 12.17) govern exactly as written. Their
**measurement method** is the repo's canonical **ISSUE-020 region-sample
method** — the method R3's derivation already names as the acceptance test —
not a per-pixel minimum over the artwork's high-frequency detail. The per-pixel
minimum is informational only: in Dark it would need ≈ 0.53, outside the
approved [0.15, 0.35] envelope and against "readability without flattening".

**Method.** For each copy element (date, greeting, figure, caption,
encouragement; a wrapped line is sampled per line), take the text line's
rectangle; composite the Hero as rendered (the shared artwork with its `DstIn`
bottom dissolve over the screen backdrop, then the one 4-stop scrim;
source-over per sRGB channel, per-theme peak); reduce the rectangle to one
effective background color (per-channel mean); compute the WCAG 2.x contrast
ratio against `textPrimary`; the minimum region ratio across the measured sweep
(heights 104–480 dp; widths 360/412 dp; 1–2 line header; 0–2 line
encouragement; both artworks) governs, reported per element and theme. Where a
device render exists, sample the same rectangles from the screenshot (ink
pixels excluded; ISSUE-020 sampled an adjacent same-band rectangle).

**Final values:** `ScrimPeakLight = 0.26f`, `ScrimPeakDark = 0.22f`; stops
unchanged (`0f→0`, `0.20f→peak`, `0.80f→peak`, `1f→0`). Light clears even the
per-pixel sweep (5.07:1). Dark recorded worst pixel 1.25:1, p95 ≈ 4.6:1, mean
≈ 9.7:1 over the expanded-header band (quiet progress band ~16.9:1), so its
region means clear the floor with margin. The Developer re-measures, reports
the per-element region values in §9, and updates the `HomeHero.kt` constants
KDoc (comment only); the Reviewer reproduces the measurement. Recorded in
ADR-47 Amendment Note 1; no code, stop, peak, layer, artwork or build change.

---

## 9. IMPLEMENTATION STATUS

<!-- DEVELOPER OWNER -->

**Status: IMPLEMENTED — validation green; AC 9 / AC 17 closed by the region
measurement (ADR-47 Amendment Note 1; values at the end of this section).**

Implemented the approved §8 refinement (precedence applied) + ADR-47/ADR-48
(+ Amendment Note 1). No build/dependency/navigation/domain changes; the only
cross-screen touch is the Habits FAB call site.

- `HomeHero.kt` rebuilt: shared `MomentumHeroArtwork` (bottom-center; no
  zoom/parallax/private dissolves), one constant `c.background` scrim
  (`drawWithCache`, stops 0 → 0.20 → peak → 0.80 → 0; `ScrimPeakLight = 0.26f`
  / `ScrimPeakDark = 0.22f`), retained screen-space top mask (ADR-35),
  content-aware height (`CollapseHeadroom` only when there is progress; empty
  day = slim header, no weighted void), R1 left-axis progress row with the
  trailing 80 dp `ProgressOrb(showPercent = false)`, `MomentumFigureDisplay`
  fraction + "completed today" or the "Today is complete" statement, one
  merged `today_hero_progress_desc` (`clearAndSetSemantics`), optional
  state-specific encouragement with no reserved space when absent, one-shot
  milestone glow. All Hero copy `textPrimary` (ISSUE-020). No infinite
  animation.
- `TodayViewModel.kt`: `encouragementFor(sections, motivationalMessagesEnabled)`
  (streak form, else "Next up: {title}"; none at 100%/empty/toggle OFF);
  `Success.message` → `encouragement`; `Celebrate` payload-free; retry sets
  `retrying` so the stream emits a real `Loading` (Error→Loading→Error/Success
  cannot be de-duplicated); payload/midnight refresh never flash the skeleton.
- `TodayScreen.kt`: static `internal TodayLoadingSkeleton` (one `today_loading`
  label, no spinner/shimmer), branch-keyed `AnimatedContent` fade,
  `HomeAurora` intensity tweened on `MomentumMotion.emphasis` (drift off;
  `snap()` under reduced motion), milestone window = one
  `LaunchedEffect(milestoneTick)` for `MomentumMotion.reward` (650) + one
  `HapticFeedbackType.Confirm`, no snackbar; rows `streak = 0`;
  `animateItem` on rows/headers with `standard`/`fast` (null under reduced
  motion); empty day hides the FAB and drops `Star`; shared `MomentumFab` in
  `AnimatedVisibility`; collapse state passed to the Hero as `State<Float>`
  (screen composition never reads it).
- New `presentation/components/MomentumFab.kt` (56 dp, solid primary, white
  hairline, soft shadow, `MomentumIcons.add` @ `prominent`, `onPrimary`,
  `glassPress`, `Role.Button`). `HabitsScreen.kt` call site swapped; dead
  private FAB + imports removed.
- `Type.kt`: `MomentumFigureDisplay` (Display 40/48/600 + `tnum` — the tag is
  present in `manrope.ttf`, verified in the binary). `Motion.kt`:
  `MomentumMotion.reward = 650`.
- `strings.xml`: added the four `today_hero_*` + two `today_encouragement_*`
  strings; removed `hero_progress_count`, `today_celebrate_all_done`, all six
  `today_message_*` (no remaining references).

**Validation executed** (2026-09-30, no device/emulator available):

- `:app:testDebugUnitTest` — **green, actually executed** (261 tests, 0
  failures), incl. the rewritten `HomeHeroAssetsTest`/`HomeScrollSurfaceTest`,
  `HomeSectionMotionTest`, `HomeAccessibilityTest`, `QuickAddTest`.
- `:app:assembleDebug` — green. `:app:assembleDebugAndroidTest` — green
  (rewritten `HomeHeroCompositionTest` + new `HomeHeroStateTest`,
  `HomeStatesTest` compile; instrumented execution is impossible here and is
  not claimed).
- Artwork contrast, per-pixel sweep (WIC decode of both 1024×1536 WebPs,
  per-row scrim compositing, WCAG relative luminance) — **informational only**
  under the accepted measurement: Light @ 0.26 → worst copy pixel 5.07:1 vs
  `#172033`; Dark @ 0.22 → worst pixel 1.25:1 vs `#F5F7FC` (p95 ≈ 4.6:1,
  mean ≈ 9.7:1 over that band). The acceptance values are the region-sample
  record below.

**CLOSED — AC 9 / AC 17 region measurement (ADR-47 Amendment Note 1).** The
acceptance measurement is the ISSUE-020 region-sample method (§8 "Refinement"
item 13): each copy element's line rectangle, the Hero composited as rendered
(artwork + 88 dp `DstIn` dissolve over the screen backdrop, then the 4-stop
scrim, source-over per sRGB channel, per-theme peak), reduced to one
per-channel mean background, measured against `textPrimary`; the minimum
across the sweep governs. Developer re-measurement (2026-09-30; sweep: heights
104–480 dp, widths 360/412 dp, greeting 1–2 lines, encouragement 0–2 lines,
both artworks):

| Element | Light min (mean bg) | Dark min (mean bg) |
|---|---|---|
| date | 6.63:1 (141, 162, 240) | 4.74:1 (124, 93, 186) |
| greeting | 8.33:1 (178, 180, 246) | 5.86:1 (103, 81, 171) |
| figure | 10.46:1 (206, 203, 246) | 17.13:1 (15, 16, 52) |
| caption | 10.14:1 (201, 200, 247) | 17.71:1 (12, 12, 45) |
| encouragement | 11.68:1 (219, 215, 247) | 18.14:1 (9, 11, 32) |
| statement | 10.75:1 (209, 206, 247) | 17.27:1 (15, 15, 50) |

Per-theme minimum: **Light 6.63:1**, **Dark 4.74:1** — both ≥ 4.5:1. No
constant, stop or peak changed (`ScrimPeakLight = 0.26f`,
`ScrimPeakDark = 0.22f` stand); `HomeHero.kt`'s constants KDoc replaced with
the Architect's final block (comment only). The per-pixel minimum remains
informational.

---

## 10. REVIEW FINDINGS AND VALIDATION

<!-- REVIEWER OWNER -->

**Review status: PASS — no actionable defects.** Independent verification
completed 2026-09-30 against ADR-47/ADR-48 (+ Amendment Notes 1), §8 +
"Refinement" (precedence applied), AC 1–19.

**Acceptance checked (AC 1–19):**
- AC 1–8: one progress representation (`showPercent = false`, no percent text,
  no duplicate count); `MomentumFigureDisplay` fraction, greeting ≤ Title, all
  Hero copy `textPrimary`; empty day slim (no headroom / weighted spacer, FAB
  hidden, no `Star`); milestone = `AuroraState.MILESTONE` +
  `MomentumMotion.reward` + one `HapticFeedbackType.Confirm`, no snackbar; no
  `rememberInfiniteTransition` in Home code; shared `MomentumFab` used by Home
  and the Habits call site (one material, `prominent` glyph); skeleton replaces
  the bare loading text; rows `streak = 0`, `MomentumBadges.kt` untouched.
- AC 9 / AC 17 (method per ADR-47 Amendment Note 1): independently recomputed
  all 12 reported region-sample pairs (WCAG 2.x, `textPrimary` #172033 Light /
  #F5F7FC Dark) — every ratio reproduces exactly; per-theme minima **Light
  6.63:1 / Dark 4.74:1** ≥ 4.5:1; peaks `0.26/0.22` stand.
- AC 10–16, 18: both themes, no hardcoded screen colours; targets ≥ 48 dp
  (FAB 56); two heading semantics; one merged `today_hero_progress_desc`;
  content-driven height (measured header/content, no screen floor); one-axis
  progress row (figure column weighted, trailing 80 dp ring);
  encouragement rules match Amendment Note 1 (none at 100 %/empty; streak ≥ 2
  line; else "Next up: {title}"); reduced motion nulls/skips every decorative
  spec; `TodayScreen` composition never reads the collapse value; Hero
  previewable/state-driven; scrim brush cached.
- AC 12 / 19: all six named tests rewritten around retained outcomes
  (`HomeHeroAssetsTest` incl. `HomeScrollSurfaceTest`, `HomeSectionMotionTest`,
  `HomeAccessibilityTest`, `QuickAddTest`, instrumented
  `HomeHeroCompositionTest`); new `HomeHeroStateTest`/`HomeStatesTest` present.
  No test pins removed machinery (`Veil*`, `BottomBlend*`, `RingScaleHeadroom`,
  96 dp ring, empty-day `CollapseHeadroom`).

**Validation performed (Reviewer, independently):**
- `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
  :app:assembleDebugAndroidTest` — BUILD SUCCESSFUL; test result XML =
  **261 tests / 0 failures / 0 errors / 0 skipped**, including the five JVM
  Home suites. `assembleDebugAndroidTest` compiles the rewritten + new
  instrumented tests; instrumented execution is impossible here (no
  device/`adb`) and is not claimed.
- Scope/working tree: only authorized files changed
  (`presentation/screens/today/**`, new `MomentumFab.kt`, `theme/Type.kt`,
  `theme/Motion.kt`, `strings.xml`, `HabitsScreen.kt` FAB call site, tests,
  `task.md`, and the append-only ADR-47/48 + Amendment Notes in
  `DECISIONS.md`); no domain/data/navigation/build/dependency change; the
  pre-existing `.opencode/**` + `opencode.json` edits are preserved
  (timestamps predate implementation).

**Required corrections:** none.


---

## 11. HANDOFF HISTORY

<!-- ORCHESTRATOR ONLY -->

- Orchestrator: task established from the human brief. Repository inspected
  (design system, Home audit, current Home implementation, ADRs, issues). Workflow
  set: Architect → human approval gate → Developer → Reviewer. No implementation
  authorized.
- Orchestrator → Architect: design/specification brief delivered (see §12).
- Architect → Orchestrator: **COMPLETE.** Concept + specification written to §8
  (§1 concept … §21 rejected alternatives; §20 acceptance criteria). **ADR-47**
  (Home is a focused Today surface; Hero simplified in place — one surface, one
  constant readability scrim, one bottom hand-off, content-driven height) and
  **ADR-48** (one progress representation at Display scale; genuine restrained
  100% moment; motivational filler and Home streaks removed) appended to
  `DECISIONS.md`. No domain/schema/navigation change required. Concrete blockers:
  human approval gate; flagged product conflict over the motivational-messages
  toggle (see §7); Hero contract tests must be rewritten to the retained outcomes.
  No code written.
- Orchestrator: **HALTED at the human approval gate** — implementation is not
  authorized until the §8 specification + ADR-47/ADR-48 are approved.
- Human → Orchestrator: **approval in principle** of the concept and product
  direction, with one product ruling (encouragement retained, toggle functional,
  disabled = complete removal + natural reflow — see §7) and a directive for a
  final skill-informed premium refinement pass before implementation.
- Orchestrator → Architect: refinement brief delivered (see §12.2). §1 status
  updated; §9/§10 remain unauthorised.
- Architect → Orchestrator: **REFINEMENT COMPLETE.** §8 gained
  "Refinement (premium design pass)" (12 required outputs) and superseded
  earlier subsections were reconciled (§8 encouragement, §11 motion, §16, §18
  strings, §20 pointer, §21); precedence rule stated ("Refinement" wins where it
  differs). ADR-48 **Amendment Note 1** appended (append-only): encouragement
  retained, ON by default, functional toggle; state-specific copy
  (`today_encouragement_streak` / `today_encouragement_next`); and a factual
  correction — `MomentumMotion.reward` does **not** exist and is added as 650,
  derived from DS §28. Skills actually loaded and used: Taste
  `design-taste-frontend`, `minimalist-ui`, `high-end-visual-design`; Compose
  `using-chrisbanes-skills` routing + `compose-component-design` (`slot-apis`),
  `compose-state-and-effects` (`side-effects`), `compose-animations`
  (`animated-content`), `compose-performance` (`deferred-reads`),
  `compose-ui-testing-patterns`; each refinement is attributed in §8 items 4–5.
- Orchestrator: **HALTED at the human approval gate** — the refined
  specification (§8) + ADR-47/ADR-48 (+ Amendment Note 1) are complete; no
  implementation is authorized and no Developer handoff has occurred.
- Human → Orchestrator: **approved** the refined §8 specification and
  ADR-47/ADR-48 (+ Amendment Note 1); implementation authorized.
- Orchestrator → Developer: implementation authorized. Pre-flight inspection
  confirmed **zero application-code changes** from the stopped prior attempt
  (`git status`/`git diff`: only `task.md`, `DECISIONS.md`,
  `.opencode/agents/*`, `opencode.json` modified — all preserved). Developer
  brief delivered: §8 + "Refinement" (precedence rule), ADR-47/ADR-48 + Note 1,
  no build/dependency changes, no device available (instrumented tests are
  written + compiled, not executed; runnable validation = JVM tests +
  `assembleDebug`).
- Developer → Orchestrator: **implementation complete; one measured conflict
  escalated.** All TASK-015 files implemented; `:app:testDebugUnitTest`
  executed 261/0; `:app:assembleDebug` and `:app:assembleDebugAndroidTest`
  green. Escalation: AC 9's measured ≥ 4.5:1 could not be met for Dark under
  R3's fixed stops + `[0.15, 0.35]` peak using a per-pixel minimum.
- Orchestrator → Architect: narrow reconciliation brief delivered (AC 9/AC 17
  vs R3; measured evidence; canonical ISSUE-020 method).
- Architect → Orchestrator: **decision complete.** AC 9/AC 17 keep their
  wording; the acceptance measurement is the ISSUE-020 region-sample method;
  peaks `0.26`/`0.22` and stops stand final. Recorded append-only
  (ADR-47 Amendment Note 1) + task.md §8 "Refinement" item 13. No human
  approval required (no approved numeric/envelope change).
- Developer → Orchestrator: reconciliation applied — HomeHero scrim KDoc
  replaced (comment-only), region sweep re-run: Light min 6.63:1, Dark min
  4.74:1 (both ≥ 4.5:1); `:app:assembleDebug` green; §9 closed record.
- Orchestrator → Reviewer: verification brief delivered (AC 1–19; six
  rewritten + new androidTest state tests; scope/working-tree preservation;
  reproduce the region measurement; instrumented execution unavailable — no
  device).
- Reviewer → Orchestrator: **PASS — no actionable defects.** AC 1–19 checked;
  the region-sample ratios were independently recomputed (all 12 reproduce;
  Light min 6.63:1 / Dark min 4.74:1); `:app:testDebugUnitTest
  :app:assembleDebug :app:assembleDebugAndroidTest` executed BUILD SUCCESSFUL
  (261/0); scope and working-tree preservation confirmed; §10 written.
- Orchestrator: **task closed — COMPLETE/verified.** Registries updated:
  `ISSUES.md` ISSUE-020 CLOSED (Home-scoped remedy); `PROJECT_STATUS.md`
  F-HOME → PARTIALLY_VERIFIED (on-device/instrumented run pending; no device
  in env). Commit/push remain Human-in-the-Loop.

---

## 12. ORCHESTRATOR BRIEF TO ARCHITECT

<!-- ORCHESTRATOR ONLY -->

**Objective.** Produce the final Home concept and an implementation-ready
specification (see §2–§4). This is a design/architecture task; do not write
application code.

**Required deliverables.**
1. A clearly defined Home concept (purpose, tone, what it is / is not).
2. A concrete visual + interaction specification covering every dimension in §3.
3. Explicit decisions and rejected alternatives.
4. Implementation boundaries.
5. Acceptance criteria suitable for Developer and Reviewer.
6. Required domain/state changes stated, or explicitly "none".
7. ADR(s) in `DECISIONS.md` for genuine decisions (e.g. Home's composition
   model, the type/numeric hierarchy, the Hero contract, all-complete state).
8. Fill `task.md` §8 with the plan; do not touch §9/§10/§11.

**Mandatory constraints.**
- Soft Aurora, Locked design system; reuse existing tokens/components where
  appropriate; state any new token and its derivation basis (per the ISSUE-025
  discipline: never present a derived value as if the source specified it).
- Preserve offline/privacy, append-only completion history, reduced motion,
  TalkBack, ≥48dp targets, both themes intentional.
- Do not invent domain capabilities. If a concept needs data the domain lacks,
  either design within what exists or record a concrete, minimal domain addition
  with its justification and migration implication.
- Challenge weak assumptions; call out conflicts with established product
  direction rather than silently overriding it.

**Stop condition.** Return the STATUS handoff when §8 and any ADR(s) are written.
Do not proceed to implementation — a human approval gate follows.

---

### 12.2 REFINEMENT BRIEF — premium design pass (TASK-015, second Architect pass)

<!-- ORCHESTRATOR ONLY -->

**Objective.** With the concept approved in principle, perform one final
skill-informed refinement pass so the Home specification is exceptionally
polished, premium, cohesive and intentional. This is **not** a restart: keep the
approved direction (§8), refine it. Do not write application code.

**Product direction that is LOCKED (do not re-litigate).**
- Home is a focused "Today" experience, not a dashboard.
- One scroll: today's progress stated once, followed by today's actionable rows.
- `done / total` remains the primary progress headline.
- The existing Home artwork is retained and is **not** recreated procedurally;
  no new/large artwork assets for Home, Habits, Tasks, Insights or Settings.
- Hero machinery is simplified per ADR-47.
- Soft Aurora remains the language; Aurora supports meaning/atmosphere, not
  decoration. No permanent infinite animation on Home; motion is
  change-triggered. The 100% moment uses the existing Aurora milestone/reward/
  haptic behavior. Streaks remain removed from Home rows. The shared Momentum
  FAB remains the intended cross-screen direction.
- **Encouragement (human ruling, supersedes ADR-48 §4):** ON by default,
  controlled by the existing `AppSettings.motivationalMessagesEnabled` toggle;
  when disabled it is removed **completely** with natural reflow — no reserved
  space, no placeholder. ON and OFF compositions must each feel intentional.
- TASK-015 remains presentation-focused; no unnecessary domain/schema/navigation
  changes.

**Mandated skill usage (substantive, not decorative).**
1. **Taste/design skills** — load and apply the relevant ones (e.g.
   `high-end-visual-design`, `minimalist-ui`, `design-taste-frontend`; select
   only what is relevant, use judgment) to critically evaluate: visual
   hierarchy, composition, spacing/rhythm, typography, density, color
   relationships, Soft Aurora treatment, depth/layering, artwork integration,
   visual restraint, interaction quality, micro-interactions, motion
   choreography, perceived quality/premium feel, empty state, loading state,
   all-complete state, accessibility, light/dark coherence.
2. **Chris Banes skills** — load `using-chrisbanes-skills` first to route, then
   only the focused skills the actual Home design needs (expected:
   `compose-component-design`, `compose-state-and-effects`,
   `compose-animations`, `compose-performance`, `compose-ui-testing-patterns`).
   If the `skill` tool is unavailable to you, read the skill files directly
   from `.opencode/skills/**` (Taste + compose-kotlin) and the chrisbanes cache
   path under `C:\Users\shaik\.cache\opencode\packages\`.

**Premium experience test (apply to every major element).** Does it earn its
place? Is hierarchy immediate? Does the eye know where to go first? Is there
unnecessary visual competition? Does spacing create rhythm, not dead space?
Does the artwork support rather than compete? Does Aurora feel atmospheric, not
ornamental? Does motion communicate state/change, not spectacle? Does the
screen feel calm with energy? Is it intentional in both themes? Does removing
optional content produce a genuinely good composition? Is it coherent at
different content lengths/states? Are transitions smooth and believable? Are
accessibility and reduced-motion respected? **Challenge §8 wherever a skill
reveals a concrete improvement.**

**Required outputs (update §8 — add a "Refinement" section; amend ADR-48
append-only for the encouragement ruling; append new ADRs only for genuine new
decisions):**
1. What was retained unchanged.
2. What was refined.
3. Why each refinement improves the experience.
4. Which Taste skill(s) informed each meaningful visual/UX refinement.
5. Which Chris Banes skill(s) informed each meaningful Compose/interaction
   refinement.
6. Final motion specification (concrete behavior for: initial Home appearance;
   progress changes; habit completion; incomplete→complete transitions; final
   item completion; 100% milestone/reward; encouragement appearing/
   disappearing; empty state; loading→content transition; error recovery; FAB
   where relevant). Each: purpose, timing token, interruption behavior,
   reduced-motion behavior.
7. Final artwork/Aurora specification (positioning, crop/window-edge,
   readability over artwork, scrim/dissolve, relationship to the progress
   headline, light/dark treatment, content-aware height, behavior when
   encouragement is disabled, any effect now deemed unnecessary).
8. Final encouragement ON/OFF behavior (copy quality — state-specific, not
   filler; both compositions independently intentional).
9. Light/dark mode requirements per element (not a simple inversion).
10. Performance constraints (recomposition, stability, allocation, drawing,
    animation work, skippability, component API simplicity, state ownership,
    unnecessary effects) — the simplest implementation preserving the premium
    experience.
11. Testing requirements (meaningful behavior/outcomes; existing tests that
    reference removed Hero machinery rewritten around retained outcomes, not
    deleted).
12. Updated acceptance criteria if necessary.

**Quality bar for the output.** No vague statements ("more polished"). Every
recommendation must be specific enough for the Developer to implement or the
Reviewer to verify. Any new numeric value must state its derivation basis (never
present a derived value as specified). Keep §9/§10 untouched.

**Stop condition.** Return the STATUS handoff with §8 updated and ADR(s)
amended/appended. Do not implement code; the human approval gate still applies.
