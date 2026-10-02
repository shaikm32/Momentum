# CURRENT TASK

> State of the currently active task only. TASK-015 and TASK-016 records are
> archived in `task_archive/`; their approved decisions remain canonical in
> `docs/architecture/decisions/DECISIONS.md` (ADR-47 / ADR-48 + Amendment Notes
> 1–3).

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: **TASK-017**
- Title: Home layout architecture reassessment after TASK-016 device verification
- Status: **IMPLEMENTED + REVIEWED PASS (2026-10-01) — awaiting human on-device
  verification.** The crown architecture was **rejected and removed**; Home rebuilt
  on the Aurora + Liquid Glass + content system as five sibling layers. Reviewer
  PASS with no actionable defects; ISSUE-020 re-measured from scratch and
  independently re-verified. ADR-47/ADR-48 Amendment Notes 1–6 recorded. **Not
  committed** (Human-in-the-Loop). F-HOME remains `PARTIALLY_VERIFIED` — the
  §4.10.14 device matrix and every instrumented suite have never been executed
  (`adb` unavailable).
- Priority: High
- Created: 2026-10-01
- Last Updated: 2026-10-01
- Classification:
  - **DESIGN / ARCHITECTURE — required, and the primary deliverable.** The
    human has explicitly rejected patching the current implementation with more
    spacing/offset/height adjustments. The "artwork crown" abstraction itself is
    in question and must be explicitly answered, not tuned.
  - **IMPLEMENTATION** — not authorized until the human approval gate clears.
  - **VERIFICATION** — Reviewer after implementation; on-device verification
    remains with the human (`adb` unavailable in this environment).
- Routing: **Orchestrator → Architect → human approval gate → Developer →
  Reviewer.**
- Source of truth (priority order):
  1. This task brief (human requirement + device screenshots, 2026-10-01).
  2. `task_archive/TASK-016.md` (approved TASK-016 plan + its Reviewer record).
  3. ADR-47 / ADR-48 (+ Amendment Notes 1–3) in
     `docs/architecture/decisions/DECISIONS.md`.
  4. `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` (Locked) — Soft Aurora.
  5. Current Home implementation under
     `app/src/main/java/com/momentum/app/presentation/screens/today/**`.

---

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Reassess the **Home composition** from the product's already-established
**Aurora + Liquid Glass** visual architecture, and deliver a recommended
architecture that expresses it. This is explicitly **not** permission to redesign
Momentum, and **not** a Hero repair task: the Architect must start from *"what
should the Home composition fundamentally be?"*, not *"how do we fix the Hero?"*.
No implementation before human approval.

---

## 3. SCOPE

<!-- ORCHESTRATOR ONLY -->

**In scope (Architect):** root-cause analysis of the observed device behavior;
evaluation of the human's persistent-background proposal; a recommended Home
layout architecture with an explicit fixed-vs-scrollable definition; the artwork
blend model; foreground readability over the artwork; the progress/orb
composition; the completion-figure hierarchy; FAB visual integration; ADR impact;
exact code-change scope; verification plan; decisions requiring human approval.

**Out of scope:** implementing; replacing or re-authoring artwork; adding artwork
to other screens; a broader visual-system redesign; new domain/database/schema
capability; unnecessary navigation change; arbitrary `Spacer`/padding fixes;
scroll-driven orb choreography. Commit/push is Human-in-the-Loop.

---

## 4. REQUIREMENTS

<!-- ORCHESTRATOR ONLY -->

### 4.1 Outcomes the human has CONFIRMED FIXED — must not regress

1. The empty-state card carries **no** "Add an activity" CTA.
2. The "Nothing scheduled today" card is **centered** in the usable content area.
3. **FAB placement** is correct.

### 4.2 New device findings

- **D1 — Empty state: the Hero disappears entirely.** On device with zero
  activities there is **no hero artwork, no date, no greeting and no Settings
  icon**. The screen collapses to blank background → centered card → FAB → bottom
  nav. This is a **regression**, not merely "artwork too faint": Home's visual
  identity must remain present with zero activities. The Architect must establish
  the actual mechanism (this is a total disappearance of chrome, which the
  TASK-016 plan did not predict and its `heightIn(min = …)` / `onHeightChanged`
  empty-state geometry is the prime suspect — verify against the code, do not
  assume).
- **D2 — Non-empty: a very large vertical gap precedes all content.** Artwork,
  date, greeting, Settings and progress are all present but pushed far down,
  reading as spatially disconnected and inefficient. The human rejects solving
  this by changing 96 dp → 104 dp or any other arbitrary value, and states the
  crown model may be the wrong abstraction.
- **D3 — Scrolling moves the artwork away.** The artwork scrolls with the
  content, which conflicts with the intent that it establishes Home's
  atmosphere/environment.
- **D4 — Progress orb + count composition is incoherent.** `0 / 1` with the orb
  directly beside it "feels forced rather than designed". The human explicitly
  does **not** want this arrangement preserved merely because TASK-015 introduced
  it. Do not force the orb and count into one horizontal row.
- **D5 — Completion figure lacks hierarchy.** Desired reading is `0` visually
  dominant with `/1` subordinate. Exact typography/layout is an Architect
  decision within the existing design system.
- **D6 — FAB still feels disconnected.** Placement is fixed, but the
  material/visual treatment still does not belong to Home's visual language. The
  TASK-016 hairline correction was technically valid but did not solve the
  integration problem. Reassess the FAB's relationship to the artwork, Aurora,
  Home surfaces, Light/Dark, and the Momentum vocabulary. **Do not redesign the
  whole FAB system** — the goal is a coherent Home composition, not a new global
  design language.

### 4.3 The human's proposal — evaluate critically, NOT an instruction

> Make the existing Home artwork a **persistent screen-level background /
> atmospheric layer** rather than part of the scrollable Hero/content layout.

Proposed separation: **fixed background** (artwork + Aurora/blend) · **fixed top
chrome** (date, greeting, Settings) · **scrollable content** (progress,
encouragement, sections, rows) · **fixed action** (FAB) · **fixed navigation**
(bottom nav).

The human explicitly asks the Architect to determine whether keeping
date/greeting/Settings fixed is actually the right implementation **and** UX
choice, and not to assume the architecture is correct without analysis.

### 4.3 FOUNDATIONAL VISUAL PRINCIPLE — the Architect's starting point

Momentum has already established **two complementary visual layers**, and this
relationship is fundamental and authoritative:

- **AURORA** = the atmospheric background/environment. It supplies atmosphere,
  depth, colour, lighting, subtle visual movement, emotional character and
  Light/Dark differentiation.
- **LIQUID GLASS** = the foreground/interface material. It supplies foreground
  hierarchy, readability, tactile surfaces, separation from the atmosphere,
  translucency, depth and controlled contrast.

```text
AURORA     → atmospheric environment
              ↓
LIQUID GLASS → content / controls / UI
```

Liquid Glass surfaces must let the Aurora atmosphere remain perceptible
through/around them. The governing result is
`AURORA = atmosphere · LIQUID GLASS = interface · CONTENT = hierarchy`.

Consequences that bind the Architect's proposal:
- Do **not** turn Aurora into an ordinary image that must be squeezed into a Hero
  container, and do **not** treat the artwork as a decorative banner.
- Do **not** hide Aurora behind opaque surfaces, and do **not** let Aurora
  overpower the actual interface content.
- The foreground interface must remain the primary hierarchy.
- Do **not** continue adding arbitrary Hero heights (96 → 104 → 112 dp), and do
  **not** patch the current layout with `Spacer` hacks. **The abstraction itself
  is in question.**
- Do **not** abandon Aurora or Liquid Glass.

### 4.4 The human's conceptual model — a design HYPOTHESIS to evaluate

The human proposes that the Aurora/artwork layer remain **persistent** while
foreground content scrolls over it, with Liquid Glass carrying the Today content,
Liquid Glass carrying the bottom navigation, and date/greeting/Settings floating
over the atmosphere.

**This is a hypothesis, not an instruction.** The Architect must decide whether it
is actually the correct model, and must say so. If a different composition better
expresses the Aurora + Liquid Glass system, propose that instead.

**Important:** do **not** automatically conclude that "date + greeting + settings
must be fixed". Instead classify every element into exactly one of:
(1) persistent atmospheric/background layer · (2) persistent Home chrome ·
(3) scrollable Today content · (4) fixed action layer · (5) fixed navigation
layer. The requirement is that the visual system stays coherent; Aurora itself
must not behave like an ordinary content card or image merely because the current
Hero architecture does.

### 4.6 Progress composition — reconsider from first principles

The current `0 / 1` with the orb beside it does not feel designed. Requirements:
progress must remain immediately understandable; the **completed value carries
stronger visual hierarchy**; **`0/1` is preferred over `0 / 1`**; `0` visually
dominant; the total subordinate; the orb reinforces the progress story; **the orb
must not simply be placed beside the number because horizontal space is
available**; the composition feels intentional and premium; accessibility
semantics stay correct.

**Do not prescribe the exact final arrangement to the Developer** — the exact
hierarchy and composition is the Architect's design decision, reached through the
Taste/design skills.

### 4.7 FAB — re-evaluated as part of the Home composition

The FAB is functionally correct and correctly placed, but still reads as
disconnected. The question is **not** "what colour should the FAB be?" but **"how
should the creation affordance belong naturally to the Aurora + Liquid Glass
visual system?"** Use existing Momentum/Aurora/Liquid Glass vocabulary and tokens.
Do not invent a new global FAB system and do not make unrelated changes to
Habits/Tasks/etc.

### 4.8 Empty state — must stay inside the same Home environment

The empty state must remain visually connected to the same Home environment and
communicate: Home identity, date/greeting, the atmospheric background, the
empty-state message, and a clear creation affordance. It must still feel like
Momentum Home, **not a blank screen with a card**. The empty card remains centered
via **measured** layout geometry; do **not** reintroduce the removed "Add an
activity" CTA; the FAB remains the creation affordance.

### 4.9 Scrolling — no choreography by default

Do **not** automatically implement the previously discussed "centered orb → scroll
→ orb moves left" transformation; it was intentionally deferred. First establish
a strong fundamental composition. If the Architect believes scroll behavior is
necessary, it must explain **why** and **what problem it solves**. Avoid motion
for the sake of motion.

Preserve: the existing artwork (never replaced or re-authored); the Momentum
Aurora direction; Light/Dark support; accessibility and existing progress
semantics; the existing milestone/completion behavior; Home's focused "Today"
scope; still-valid TASK-015 functionality; the TASK-016 fixes in §4.1.

Do not: introduce domain/database/schema changes; change navigation unnecessarily;
add artwork to other screens; introduce arbitrary `Spacer`/padding hacks; implement
scroll-driven orb choreography merely because it was discussed before.

Re-verify the ISSUE-020 region-sample contrast method for any change to the
artwork window, scrim or copy position.

---

## 4.10 TASK-017 human ruling (2026-10-01 — authoritative)

<!-- ORCHESTRATOR ONLY -->

Implementation is **authorized**, subject to one narrow Architect amendment pass
first (§4.11). Ruling summary, all human-approved:

1. **Crown-based Hero REJECTED.** Do not tune the crown height. No further height
   adjustments, `Spacer` values, offsets, collapse calculations, parallax or
   scroll choreography. The Hero/collapse architecture is **replaced, not
   patched**. Retire/supersede the Home-specific collapse behavior and ADR-35's
   Home usage as the Architect proposed.
2. **Established visual architecture is the foundation:** AURORA = atmospheric
   environment · LIQUID GLASS = foreground interface/material · CONTENT = primary
   hierarchy. The artwork is **not** a content image/banner; Aurora is not hidden
   behind opaque surfaces; Aurora does not overpower foreground content.
3. **`ArtworkWindow = 184 dp` ADOPTED**, constant across all Home states, derived
   from the Architect's documented canonical facts. It must **never** be a function
   of activity count, empty/non-empty state, copy height, or scroll position. The
   existing artwork is unchanged — never replaced, no new artwork, no artwork on
   other screens. The artwork forms the **upper visual region of the overall
   Aurora atmosphere**, blending/dissolving smoothly with **no hard image
   boundary** and **no opaque covers**. Foreground readability must come from
   Liquid Glass treatment, typography hierarchy, the existing scrim/Aurora
   mechanisms and theme-aware tokens.
4. **PERSISTENT HOME TOP CHROME — Date + Greeting + Settings, all FIXED.** This
   **amends the Architect's §8.3 decision** (which rejected fixed date/greeting).
   They form one coherent persistent top chrome and do not scroll away or
   disappear. Contract: `Date → fixed, Greeting → fixed, Settings → fixed`;
   `Progress / Encouragement / Activities → scrollable`. **Scrollable content must
   never overlap the top chrome** and must have a proper scrollable region below
   it. **Do not introduce another collapse/parallax system** to achieve this. The
   chrome must remain visually coherent with the Aurora environment, not read as an
   unrelated opaque header. The exact visual composition remains the Architect's
   and the design system's responsibility — **the behavior contract above is what
   is binding.**
5. **Aurora remains persistent** while Today content scrolls: persistent
   atmosphere + persistent Home identity + moving Today content. **No parallax, no
   orb choreography, no scroll-driven artwork choreography.** Calm and restrained.
6. **Progress composition ADOPTED conceptually:** the ring/orb **frames** the
   completed value; completed value visually dominant, total subordinate; prefer
   `0/1` over `0 / 1`; the composition reads as **one intentional visual object**;
   the orb reinforces rather than competes. Exact typography via the existing
   design system + Taste skills. Semantics stay correct. No arbitrary decoration.
7. **Empty state remains a complete Home experience:** atmosphere, date, greeting,
   settings, centered "Nothing scheduled today" card, FAB, bottom nav. Card stays
   centered via **measured** geometry; the "Add an activity" CTA stays removed;
   the FAB stays the single creation affordance. **The empty state must not
   collapse the atmosphere or the top chrome.**
8. **FAB: glass-over-`primary` ADOPTED** using the existing
   `MomentumGlass.heroFill`. **No new token, no new visual system.** The shared
   `HabitsScreen` consequence is accepted — but **nothing else on Habits changes**,
   and the updated shared FAB must be verified in **Habits Light + Dark** and
   remain accessible and visually intentional.
9. **Accessibility retained/increased:** the Architect's proposed `heading()` on the
   Greeting is approved; progress semantics stay meaningful; the FAB keeps its
   click/action semantics; empty-state content stays accessible. **Never
   sacrifice accessibility for visual composition.**
10. **Performance:** prefer the simpler architecture. No per-frame allocations, no
    unnecessary scroll-state observation, no continuous layout calculations, no
    infinite decorative animations, no unnecessary recomposition. Eliminate the
    Hero collapse / `collapsePx` / nested-scroll machinery where no longer
    required.
11. **Scope:** focused Home presentation architecture change. No domain models,
    database/schema, repositories, navigation, unrelated features or screens, or
    dependencies/build configuration. Shared `MomentumFab` changes permitted **only**
    for the approved visual treatment. No broad visual-system redesign, no new
    artwork. **Do not commit.**
12. **ISSUE-020:** re-measure the relevant Home foreground/background contrast
    **from the new composition**. **Do not carry TASK-016's measurement table
    forward.** Use the canonical ISSUE-020 methodology. The existing minimum
    contrast requirement **remains mandatory** — **do not weaken acceptance
    criteria to accommodate the new composition.**
13. **Implementation expectation (§14):** the Developer must **first understand the
    new architecture before editing** and must **not** mechanically modify the old
    `HomeHero` until it resembles the new design. Obsolete Hero/crown/collapse
    mechanisms are **removed**, not adapted. The result must show clear separation
    between persistent Aurora atmosphere · persistent Home top chrome · scrollable
    Today content · fixed FAB · fixed bottom navigation, using appropriate
    clipping/content bounds so scrollable content never overlaps the chrome. **No
    arbitrary `Spacer` values to fake the composition.**
14. **Verification (§15):** empty (Light/Dark: artwork visible, atmosphere visible,
    date/greeting/settings visible, card centered, FAB visible, no "Add an
    activity") · non-empty (Light/Dark: artwork/Aurora visible, chrome fixed,
    progress composition, `0/1` hierarchy, orb/ring relationship, activities scroll,
    no overlap, FAB integration) · scrolling (chrome fixed, Aurora coherent,
    content scrolls, no artwork scroll, no overlap, no visual jumps) · completion
    (100%, milestone Aurora, ring/glow, Confirm haptic, TASK-015 behavior intact) ·
    shared FAB (Home Light/Dark, Habits Light/Dark) · build/test. **Do not claim
    instrumented tests passed unless actually executed on a device/emulator.**
    The 15 human/device checks remain required.
15. **Workflow:** Developer → Reviewer → human on-device verification.
    **Do not create another architectural redesign during implementation** unless a
    genuine blocker is found. **If an implementation blocker requires changing an
    approved decision, stop and escalate through the Orchestrator/Architect
    process.**

**Final product principle (binding).** AURORA = atmosphere/environment ·
LIQUID GLASS = interface/material · TOP CHROME = persistent Home identity ·
TODAY CONTENT = scrollable and actionable · PROGRESS = one clear visual focal
point · FAB = persistent creation affordance. The goal is **not** to make the old
Hero work; it is to make Home feel like a premium, calm, focused Today experience
using the visual language already chosen.

### 4.11 Why one Architect amendment pass is required before the Developer

<!-- ORCHESTRATOR ONLY -->

The ruling's §4 **reverses an architectural decision the proposal was built on**
(§8.3 rejected fixed date/greeting). Consequently the following are unspecified
and must not be improvised by the Developer — the human's §14 forbids
mechanically reshaping the old `HomeHero`:

- The **layering and geometry** of a fixed top chrome over a persistent
  atmosphere, including whether the atmosphere spans the full window behind the
  chrome or begins below it. **The ruling's own §3 and §4 pull in different
  directions here** — §3 places the artwork in the atmosphere's *upper* region,
  while §4's diagram places Aurora/artwork *below* the chrome. This must be
  resolved explicitly.
- The **datum for the 184 dp `ArtworkWindow`** (window top, or below the chrome)
  once the chrome is fixed.
- The **scroll-boundary mechanism** that guarantees no overlap, specified without
  reintroducing collapse/parallax machinery.
- The **contrast measurement bands**, which move because the copy sits over a
  different part of the atmosphere — required before the §12 re-measurement.
- The **empty-state centering region**, which must now account for both the fixed
  chrome and the atmosphere.
- **Reading order / TalkBack** semantics for fixed chrome followed by scrollable
  content.
- The **ADR record**: ADR-47 Amendment Note 4's "date + greeting scrollable; only
  Settings fixed" clause is now superseded and must be amended append-only.

Scope of the amendment: **specification only, no code.** The Architect already has
the full analysis; this pass resolves the interaction between the approved
architecture and the amended top-chrome decision.

---

## 5. QUESTIONS THE ARCHITECT MUST ANSWER EXPLICITLY

1. Should the **crown model be retained and corrected**, or is it the wrong
   abstraction?
2. Should the Aurora/artwork layer instead become a **persistent screen-level
   atmospheric environment**?
3. **What should remain fixed vs scrollable** — classifying every element into
   atmospheric layer / persistent Home chrome / scrollable content / fixed action
   / fixed navigation? (§4.4 forbids assuming fixed top chrome.)
4. How does the atmosphere **blend into the screen** — no hard boundary, no
   opaque cover, no disappearance?
5. How does **Liquid Glass foreground stay readable** over the atmosphere in both
   themes?
6. What is the correct **progress/orb composition**? (§4.6)
7. How should the **FAB belong to the Aurora + Liquid Glass system**? (§4.7)
8. What is the **smallest maintainable architecture** achieving the intended
   experience — and what is the migration/removal path for the current Hero/crown
   architecture?

Framing requirement: begin from *"what should the Home composition fundamentally
be, given Aurora is the atmosphere and Liquid Glass is the interface?"* — **not**
from *"how do we fix the Hero?"*

---

## 6. RELEVANT FILES

- `app/src/main/java/com/momentum/app/presentation/screens/today/HomeHero.kt` —
  crown/`ArtworkCrown`, the `lerp` height model, `onHeightChanged`, the
  `BoxWithConstraints`-reported height, the three-layer stack.
- `app/src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt` —
  `BoxWithConstraints`, `heroHeightPx`, `EmptyDayBottomReserve`, the
  `heightIn(min = emptyRegion)` empty item, Hero-as-`LazyColumn`-item-0, the
  collapse `NestedScrollConnection`, FAB placement, `HomeAurora`.
- `app/src/main/java/com/momentum/app/presentation/components/MomentumHeroArtwork.kt`
  — artwork window, `ContentScale.Crop`, `bottomDissolve` contract.
- `app/src/main/java/com/momentum/app/presentation/components/MomentumFab.kt` —
  the shared FAB (Task-016: theme-resolved hairline + `MomentumGlass.heroElevation`).
- `app/src/main/java/com/momentum/app/presentation/aurora/MomentumAurora.kt`,
  `components/LiquidGlass.kt`, `components/ProgressOrb.kt`,
  `screens/habits/HabitsHero.kt` (artwork+copy reference composition).
- `app/src/main/java/com/momentum/app/presentation/navigation/MomentumNavGraph.kt`
  — the Scaffold and bottom-nav insets the Home layout sits inside.
- `app/src/main/java/com/momentum/app/presentation/theme/**` — `Tokens.kt`
  (`MomentumSpacing`), `Type.kt` (`MomentumFigureDisplay`), `Motion.kt`, `Color.kt`.
- Tests pinning current Home contracts: `HomeHeroAssetsTest` (incl.
  `HomeScrollSurfaceTest`), `HomeAccessibilityTest`, `QuickAddTest`,
  `HomeSectionMotionTest`, `HomeStatesTest`, `HomeHeroStateTest`,
  `androidTest/.../HomeHeroCompositionTest.kt`. Many encode the crown/collapse
  model and **will need rewriting** if the architecture changes — identify them.

---

## 7. ENVIRONMENT FACTS

- `adb` **unavailable** (confirmed 2026-09-30). No emulator/device run is
  possible; instrumented suites have never been executed. Runnable validation =
  `:app:testDebugUnitTest` + `:app:assembleDebug` (+ `:app:assembleDebugAndroidTest`
  compiles). On-device verification is the human's.
- Working tree: TASK-015 **and** TASK-016 implementations are present and
  **uncommitted**. All must be preserved. `.opencode/**`, `opencode.json`,
  `ISSUES.md`, `PROJECT_STATUS.md` carry further pre-existing edits — preserve all.

---

## 8. ARCHITECTURAL DECISIONS AND PLAN

<!-- ARCHITECT OWNER -->

**Status: proposal written, awaiting the human approval gate in §8.15. No
implementation is authorized. No application code has been written.**

### 8.1 Root cause of D1–D6

**D1 — the total disappearance is one defect, and it is not the artwork crop.**
It is `HomeHero.kt:205` contradicting `HomeHero.kt:214`.

```kotlin
// :205  the height SUM
val expandedHeight = if (hasProgress) compactHeight + ArtworkCrown else compactHeight
// :214  the Spacer that is actually COMPOSED
val crown = if (hasProgress) lerp(ArtworkCrown, 0.dp, p) else ArtworkCrown
```

On an empty day `figureState == Hidden` → `hasProgress == false`, so **the 96 dp
crown is charged to no height but is still laid out.** The Box has a fixed
`.height(currentHeight)`; the copy `Column` is `.fillMaxSize()` and its children
are measured sequentially against the remaining height.

Trace at font scale 1.0, status bar ≈ 24 dp, first frame (`headerPx == 0`):

| step | value |
|---|---|
| `compactHeight` = `topInset(40) + headerDp(0) + HeroBottomInset(16)` | **56 dp** |
| `Spacer(topInset 40)` consumes | 40 dp → 16 dp left |
| `Spacer(crown 96)` measured with `maxHeight = 16`; `SizeElement` coerces | 16 dp → **0 dp left** |
| `HeroHeader` measured with `maxHeight = 0` — the `Row` collapses to 0 dp; the `Text`s **and** the 48 dp `LiquidGlassIconButton` are all coerced to zero and draw nothing | 0 dp |
| `onSizeChanged` reports `headerPx = 0`, so the height sum never self-corrects | **stable at 56 dp** |
| artwork is `fillMaxSize()` in a `clipToBounds()` 56 dp box: `bottomStart = (1 − 88/56).coerceIn(0.10, 0.95) = 0.10` | mask ramps White→Transparent from 10 % to 100 % — **~90 % of the illustration is dissolved away** |

So on device the copy is **coerced to zero height** and the artwork is **present
but almost entirely dissolved**. It reads as "nothing there". The KDoc at
`HomeHero.kt:134-135` ("on an empty day … the Hero is a fixed 208 dp") documents
the *intended* behaviour and is not what the code computes. The declared invariant
on line 132-133 — "the height model and the foreground layout can never drift
apart" — is false in exactly this branch.

**Process root cause.** `HomeHeroCompositionTest.emptyDayHeroStillShowsTheArtworkCrown`
asserts `height >= statusBarTop + HeroTopInset + ArtworkCrown` (136 dp). It would
have failed at 56 dp on its first run. It is an `androidTest`, and **no
instrumented suite has ever been executed in this project** (`adb` unavailable,
`task.md` §7). A bounds contract that can only be checked on a device guarded the
exact geometry that broke.

**D2 is the same defect inverted.** With progress, `expandedHeight = compactHeight
+ ArtworkCrown` = `40 + 64 + 24 + 64 + 16 + 96` ≈ **304 dp**, and the date sits
`topInset + crown` = **136 dp** below the window top. 136 dp of nothing above the
first word. Raising the crown to 104 or 112 moves the date to 144/152 dp. The
crown is the abstraction error: **the date's distance from the top is a function
of the crown, so the crown can never be the right value.** Only removing the
crown removes the gap.

**D3 is structural.** The artwork is `item { }` index 0 of the `LazyColumn`
(`TodayScreen.kt:449`), so it is a child of the scroll surface and translates
with it. The scrim and the artwork's 88 dp dissolve are sized by that box, so the
whole atmosphere scrolls away.

**D4/D5 are exact.** `today_hero_fraction = "%1$d / %2$d"` is **one** `Text` at
one style (`MomentumFigureDisplay`, Display 40/48/600) — so `0` and `/1` have
identical size, weight and colour and there is **no hierarchy between them at
all**. `ProgressOrb` is a `Row` sibling separated by `HeroOrbGap` (16 dp) and
vertically centred, so its position is decided by available width, not by meaning;
and with `showPercent = false` it carries **zero information** — an 80 dp ring
whose only role is to be a ring.

**D6 is a material-vocabulary mismatch, not a colour problem.** `MomentumFab` is
the only foreground object on Home with a hard, fully-opaque, saturated boundary
(`primary` at 0.95/0.92 alpha). Every other floating/foreground surface is Liquid
Glass: the bottom bar at `heroFill = 0.68`, Settings at `controlFill = 0.45`,
cards at `cardFill = 0.55`. The FAB speaks a third vocabulary, and it is also the
only object floating on bare atmosphere with nothing relating it to the content
above or the bar below. ADR-48 Amendment Note 3 correctly diagnosed this as an
*edge*-vocabulary problem and fixed only the edge; the *fill* is what still
separates it.

### 8.2 Verdict on §4.4's persistent-atmosphere hypothesis: **ACCEPTED, with two modifications**

**Accepted.** The artwork becomes a persistent screen-level atmospheric layer.
This is the correct consequence of §4.3 and it is what removes the abstraction
error: Aurora is atmosphere, so it has **no height of its own** and nothing to
desynchronise from. It resolves D1 (no per-state height exists to get wrong), D2
(the date's position is no longer a function of any tunable), and D3 (the artwork
is no longer a child of the scroll surface).

**Modification 1 — there is ONE atmosphere, not two.** Today `HomeAurora`
(`AuroraBackground`) and the Hero artwork + scrim are two independent atmospheric
systems. §4.3 describes a single environment. So the artwork is not "a background
image behind the content" — it is the **upper region of the one atmosphere**, and
the ambient Aurora field is what it dissolves into. The shared 88 dp dissolve is
the join between the two regions, and it lands on the join, not on a container edge.

**Modification 2 — "date + greeting + Settings must be fixed" is REJECTED as
drawn. Only Settings is fixed.** Full analysis in §8.4.

### 8.3 Recommended Home visual architecture — **AS AMENDED by the §4.10.4 ruling**

> **Amendment notice.** §8.3's layer diagram and §8.4's classification as written
> on the first pass placed **date + greeting in the scrollable content** with only
> Settings fixed. The human ruling §4.10.4 **reverses that**. The binding contract
> is now `Date → fixed · Greeting → fixed · Settings → fixed` as **one coherent
> persistent Home top chrome**, and `Progress / Encouragement / Activities →
> scrollable`. The reasoning that argued against fixed date/greeting is
> **superseded and withdrawn** — it remains recorded above only as history. The
> **substantive parts of §8.3 are unchanged**: the one-atmosphere model, the
> 184 dp `ArtworkWindow` and its derivation, the full-atmosphere four-stop scrim,
> the progress lockup, the FAB treatment and the no-choreography decision all stand.
> Full specification of the amended layering is in **§8.17**.

```text
z-order (back to front) — one BoxWithConstraints, four children, explicit order

LAYER 1 — ATMOSPHERE  (persistent; spans the ENTIRE content area, incl. behind the chrome)
  1a  Aurora field        AuroraBackground(state, intensity, animated = false)
  1b  Hero artwork        MomentumHeroArtwork(BottomCenter) in the 184 dp window
                          — datum = the WINDOW TOP (§8.17.2), not the chrome's bottom
  1c  Readability scrim   one four-stop c.background gradient over 1a+1b, fillMaxSize
                          of the WHOLE content area, with NO stop at the chrome edge

LAYER 2 — SCROLLABLE TODAY CONTENT  (one LazyColumn, one bounded region BELOW the chrome)
  2a  HeroProgressLockup  ring-framed `done` · `/ total` · caption · encouragement
  2b  Remaining heading + rows
  2c  Completed heading + rows
  2d  HomeEmptyState      on an empty day, measured centring, no CTA

LAYER 3 — FIXED ACTION
  3a  MomentumFab         placement UNCHANGED (§4.1.3)

LAYER 4 — PERSISTENT HOME TOP CHROME  (fixed; floats directly on the atmosphere)
  4a  date                labelLarge · textPrimary
  4b  greeting            headlineMedium · textPrimary · semantics { heading() }
  4c  Settings            LiquidGlassIconButton, TopEnd, 48 dp
  — NO plate, NO fill, NO divider, NO shadow, NO scrim discontinuity (§8.17.1)

LAYER 5 — FIXED NAVIGATION
  5a  LiquidGlassBottomBar    UNCHANGED (owned by MomentumNavGraph)
```

**The artwork window is one derived constant, not a Hero height.**

```kotlin
// recognition band (BOUND-1, already canonical) + one standard gap + the
// component's own 88 dp hand-off, so the illustration's fully-opaque region is
// exactly ArtworkWindow - 88 = 96 dp >= HeroRingSize (80) in EVERY state.
ArtworkWindow = HeroRingSize + MomentumSpacing.standard + ArtworkHandOff   // 80 + 16 + 88 = 184 dp
```

Three facts, all already canonical, no new number: `HeroRingSize` (80 dp — the
smallest band that can hold a recognisable fragment of the illustration at phone
scale), one standard gap, and the shared `MomentumHeroArtwork` bottom dissolve
(88 dp, ADR-39). It is **identical in every state, on every device, at every font
scale**, and it is never a function of content, activity count, empty/non-empty
state or scroll position (§4.10.3). BOUND-1 holds *by construction*.

**The window reserves nothing.** The artwork occupies the top 184 dp of the
environment **and the chrome's copy floats on it** — the whole point of the fixed
chrome is that it is *inside* the atmosphere, not above it. The crown existed only
because the Hero had to reserve copy-free space for the artwork, which is exactly
the "artwork as decorative banner" abstraction §4.3 forbids. With no reservation
nothing competes for the artwork's space, so the illustration can never be
squeezed out and the window can never collapse.

**The scrim spans the whole atmosphere.** The four-stop `c.background` gradient is
retained **verbatim** (`0 → clear`, `0.20 → peak`, `0.80 → peak`, `1 → clear`,
`ScrimPeakStart = 0.20f`, `ScrimPeakEnd = 0.80f`) with peaks **0.26 Light /
0.22 Dark** unchanged, but it is now `fillMaxSize()` of the whole content area
instead of a content-derived box, and it carries **no stop at the chrome's bottom
edge**. Because it reaches exactly 0 at the content area's bottom edge — behind
the floating glass bar — there is no hard boundary anywhere; and the 0.20–0.26
`c.background` band over the lower atmosphere slightly raises the effective opacity
behind the glass activity rows, improving row legibility.

### 8.4 Fixed vs scrollable — **AS AMENDED by §4.10.4**

| Element | Class | Why |
|---|---|---|
| Aurora field | **(1) atmosphere** | Environment. No semantics, must not translate. |
| Hero artwork | **(1) atmosphere** | Environment. Same. |
| Readability scrim | **(1) atmosphere** | Readability is a property of the environment, not of a box. |
| **date** | **(2) persistent Home top chrome** | **Ruling §4.10.4 — binding.** |
| **greeting** | **(2) persistent Home top chrome** | **Ruling §4.10.4 — binding.** |
| **Settings** | **(2) persistent Home top chrome** | **Ruling §4.10.4 — binding.** |
| progress figure / total / caption / ring | **(3) scrollable** | Content, state-derived. |
| encouragement line | **(3) scrollable** | Content. |
| section headers, activity rows | **(3) scrollable** | Content. |
| empty-state card | **(3) scrollable** | Content. |
| FAB | **(4) fixed action** | Unchanged (§4.1.3). |
| bottom navigation | **(5) fixed navigation** | Unchanged. |

**Date + Greeting + Settings: FIXED, as one coherent top chrome.** This supersedes
the first pass's contrary conclusion in full. The arguments that were made against
it (spatial coherence, headline-vs-chrome, large-font cost, long-list cost) are
**withdrawn as a basis for the decision**; the human owns that call. What the
Architect must now guarantee is the *other* half of the ruling's requirement, which
is an engineering obligation rather than a preference:

- The chrome is **one coherent object**, not three floating items — §8.17.1.
- Scrollable content **never overlaps it** and has **a proper scrollable region
  below it** — §8.17.3.
- It is achieved with **no** collapse, parallax or per-frame scroll observation —
  §8.17.3.
- It reads as **part of the Aurora environment**, not an unrelated opaque header —
  §8.17.1.

**Why the fixed chrome is architecturally sound once it is composed this way**
(recorded so the Reviewer can verify the intent, not to re-argue the ruling): the
objection that a permanent band re-creates D2 was an objection to a *band floating
above the content with nothing relating the two*. Here the chrome sits **inside**
the atmosphere with no plate and no divider, and the scrollable region begins at
its measured bottom edge with the same 20 dp screen axis and the same single scrim
running continuously behind both. The band is a region of one continuous
environment, which is precisely `TOP CHROME = persistent Home identity` in the
ruling's final product principle. The permanent-viewport-cost objection is real and
is mitigated by keeping the chrome to two short text lines plus one 48 dp control;
§8.17.4 specifies the large-font consequence rather than leaving it to chance.

### 8.5 Progress / orb composition (§4.6, D4, D5)

Governing principles reached through the Taste skills: hierarchy is carried by
**type scale and negative space**, never by adding a second representation; a
shape placed beside a number *because there is room* is decoration; a component
must read as **one deliberate object**, not as adjacent parts.

**Decision — `HeroProgressLockup`, one unified lockup, ring as frame not neighbour.**

- **The ring frames the completed value.** `ProgressOrb` keeps its exact drawing
  (single `Canvas`, same track / arc / halo / lobes, `showPercent = false`) and
  its exact `HeroRingSize = 80 dp`, but the completed value is placed **inside** it
  instead of beside it. This is the direct answer to "the orb must not simply be
  placed beside the number because horizontal space is available": it is now
  positioned by meaning — it is the progress frame of the figure — and its position
  is invariant at every width, digit count and font scale. **80 dp is unchanged**,
  because a single Display line height (48 dp) + 2 × standard (32 dp) is exactly
  today's derivation; no new dp value is introduced.
- **The fraction is two typographic levels, not one string.** `today_hero_fraction`
  becomes `"%1$d"`; a new `today_hero_fraction_total` = `"/ %1$d"`. `done` at
  `MomentumFigureDisplay` (Display 40/48/600 + `tnum`); the total directly beneath
  it at the design system's **Title** token (`titleMedium`). Both `textPrimary` —
  the subordinate level is carried by **size and weight only**, never by opacity or
  `textSecondary`, per ADR-47 §7 / ISSUE-020.
- **`tnum` is what makes the split safe**: the completed digit does not change width
  as it goes 0 → 9, so the lockup never reflows.
- **The caption `completed today` stays**, `labelLarge`, `textPrimary`, beneath the
  total, aligned to the same 20 dp screen axis. No copy decision is invented.
- **One merged `contentDescription`** (`today_hero_progress_desc`) over the whole
  lockup — unchanged, so the split never double-reads and the ring stays decorative.
- **Reading order:** `done` → total → caption. Explicit, and identical to the DOM.
- **`Complete` branch is unchanged**: the ring full at the screen's left axis with
  the statement beside it (`headlineLarge`, existing string). ADR-48 §1's "ring
  full" clause stands. The incoherence the human reported is specific to the
  `Fraction` branch (`0 / 1` beside an empty ring), and it is that branch only that
  changes.
- **`Hidden` branch: nothing at all** — unchanged.

`HeroOrbGap` and the `Row` are deleted. The **Architect fixes the relationship
(ring frames the figure; the figure's two levels are a type scale, not a string)
and not the pixels**; the Developer chooses exact insets/radii within the existing
token grid.

### 8.6 Empty-state composition (§4.8, D1, §4.10.7) — **AS AMENDED**

On an empty day the **atmosphere and the entire top chrome are identical to every
other day** — atmosphere, artwork window, date, greeting, Settings. That is the
whole point of removing the per-state height: there is no longer any code path in
which a state can remove the chrome. Only the progress lockup and the two sections
are absent, and the single `HomeEmptyState` card takes their place inside the
scrollable region.

The centring stays **measured**, never spacer-guessed, and **§4.10.7's two
guarantees are structural rather than arithmetic** — see §8.17.5:

```kotlin
// scrollRegionHeight is the LazyColumn's own measured height (§8.17.3)
emptyRegion = scrollRegionHeight - EmptyDayBottomReserve    // 96 dp FAB clearance, unchanged
// Box(Modifier.heightIn(min = emptyRegion), contentAlignment = Alignment.Center)
```

Because the atmosphere is a **sibling backdrop** sized to the whole content area
and the chrome is a **sibling above** the scroll region, the empty item is measured
against the region's own height and can compress **neither** the atmosphere **nor**
the chrome — there is no shared box for it to squeeze. The measured input the
first pass needed (`chromeHeightPx` → empty region) is now supplied by layout
instead of by a subtraction, because the region is physically bounded by the chrome.

§4.1.1 (**no CTA**), §4.1.2 (**centred card**), §4.10.7 and §4.10.4 are all
satisfied. The FAB remains the single creation affordance.

### 8.7 FAB integration (§4.7, D6)

**Not a colour question.** The disconnection is that `MomentumFab` is the only
foreground object on Home with a hard, fully-opaque, saturated boundary, on a
screen whose entire foreground vocabulary is translucent glass — and it is the only
object floating on bare atmosphere with nothing relating it to the content above or
the glass bar below.

**Decision — glass-over-`primary` at the design system's own floating-layer alpha,
on the existing theme-resolved edge.**

1. The fill's two hard-coded alphas (`0.95f` / `0.92f`) become
   **`MomentumGlass.heroFill` (0.68)** applied to `c.primary`, with the top-edge
   illumination streak and the theme-resolved edge the `GlassLevel.HERO` path
   already uses. `MomentumGlass.heroFill` is an existing token — **no new token,
   no new value, no new component, no new API**.
2. `CircleShape`, `MomentumFabSize = 56.dp`, `MomentumIcons.add`,
   `MomentumIconSizes.prominent`, `glassPress`, `Role.Button`,
   `contentDescription`, and ADR-48 Amendment Note 3's edge +
   `MomentumGlass.heroElevation` all **unchanged**.
3. **Placement unchanged** — `align(BottomEnd)`, `end = MomentumSpacing.screen`,
   `bottom = MomentumSpacing.section` (§4.1.3, confirmed fixed on device). The
   96 dp `EmptyDayBottomReserve` is unchanged.

**Why this is the Aurora + Liquid Glass answer.** Under §4.3 the foreground is
glass over atmosphere and the atmosphere must stay perceptible *through and around*
it. A 0.95-alpha disc is the one element that stops the atmosphere dead. At
`heroFill` the Aurora reads through the FAB, the FAB shares one material family
with the glass bar directly below and the glass Settings control above — so the
three floating objects on Home form **one depth stack** — and the disc remains
unmistakably the primary action because it is still the only `primary` object.
This is a *derived* change to two expressions in an existing shared component.

**Rejected:** a saturated solid disc (current — opaque, breaks atmospheric
continuity); a gradient/aurora FAB or gradient ring (new visual language,
forbidden); full `HERO` glass with a `primary` glyph (loses primary-action
salience — see gate item 8.15-6, which asks the human to choose); an
optional `border`/`emphasised` parameter so only Home differs (new component
architecture, forbidden, ADR-47 §6); moving the FAB into the bottom bar
(placement is confirmed correct).

### 8.8 Light/Dark behaviour for every layer

| Layer | Light | Dark |
|---|---|---|
| Aurora field | unchanged: `ambientLight = 0.16` base × state intensity | unchanged: `ambientDark = 0.13` × state intensity |
| Hero artwork | `habits_hero_light`, `ContentScale.Crop`, `BottomCenter`, no tint/filter — **ADR-30/39 untouched** | `habits_hero_dark`, identical pipeline |
| Scrim | peak **0.26** over `c.background`, four stops unchanged | peak **0.22** over `c.background`, four stops unchanged |
| Settings | `GlassLevel.CONTROL` (`controlFill = 0.45`), ink hairline `borderLight = 0.10` | `GlassLevel.CONTROL`, white hairline `borderDark = 0.16` |
| Progress | ring track `textPrimary @ 0.55` (ISSUE-014 ≥ 3:1 in both), arc `primary`, figure/total/caption all `textPrimary` | identical expression |
| Content rows / sections | unchanged Liquid Glass over the (now slightly more veiled) atmosphere | unchanged |
| FAB | `primary @ heroFill`, ink hairline | `primary @ heroFill`, white hairline, elevation explicitly not relied on |
| Bottom bar | `GlassLevel.HERO` (0.68), ink hairline | `GlassLevel.HERO`, white hairline |

Both themes use **one** atmosphere expression with only token values swapped. No
theme gains or loses a layer, and no layer is Light-only or Dark-only. The Light
aurora's known weakness (audit CP-4: its surface ramp collapses and depth is a
hairline) is **improved** by this architecture, not worsened: an 184 dp
continuous illustration at the top of the screen gives Light a depth cue it
previously only had in a Hero box that vanished when content grew.

### 8.9 Artwork blending / dissolve — continuous, no boundary, never invisible

- **One** dissolve, the shared `MomentumHeroArtwork` 88 dp `bottomDissolve` —
  **unchanged**, `topDissolve` still generates no stop. The component file is
  **not modified**.
- It now lands on the **join between the two regions of the one atmosphere**
  (illustration above, Aurora field below) instead of on a content box's bottom
  edge. The scrim reaches **exactly 0** at the atmosphere's bottom edge, so there
  is never a band of veiled `c.background` against un-tinted Aurora — the exact
  failure ADR-47 Amendment Note 3 §3.3 identified, now structurally impossible
  because the scrim's extent is constant.
- **No hard boundary:** the `DstIn` mask is value-continuous; the only
  discontinuity is a first-derivative kink at each ramp start.
- **No opaque cover:** peak 0.26 / 0.22, inside the approved `[0.15, 0.35]`
  envelope and far below opaque.
- **Never invisible:** BOUND-1 is now structural — the artwork window is a
  constant `HeroRingSize + standard + 88`, so the fully-opaque region is always
  `96 dp ≥ HeroRingSize`, in **every** state, with no per-state measurement and no
  dependence on copy height, font scale, or content length. There is no state in
  which the artwork can be squeezed out, because there is nothing that competes
  for its space.
- **No top boundary:** the atmosphere is the backdrop of the whole content area,
  whose top edge is the physical window top (ADR-33/37/39 unchanged;
  `MomentumNavGraph` is not modified). Full-strength artwork behind the transparent
  status bar stands. **This is also the reason the artwork's datum is the window top
  and not the chrome's bottom edge** — see §8.17.1 and §8.17.2.

### 8.10 Accessibility (§4.6 requirements, §4.1 guarantees) — **AS AMENDED for the fixed chrome**

Full specification is in **§8.17.6** (reading order, TalkBack traversal and the
scrollable-region announcement). Summary of what changes from the first pass:

- **Reading order is now date → greeting → Settings → content**, because the chrome
  is composed above the scroll region. This is correct: the persistent Home identity
  is announced first, once, and never re-announced as the content scrolls.
- **Headings:** the greeting keeps the approved `Modifier.semantics { heading() }`.
  The date stays plain text.
- **One merged `contentDescription`** over the progress lockup, unchanged — the
  figure/total split creates no second announcement, and the ring stays decorative.
- **Large fonts, with a fixed chrome:** this is the one place the amendment costs
  something real, and §8.17.4 specifies the behaviour instead of leaving it
  accidental. The chrome is measured, so it grows with font scale; the scroll
  region takes the remainder and still scrolls; the artwork window **does not grow
  with font scale**, because it is an environment constant.
- **Touch targets:** Settings 48 dp, FAB 56 dp, completion control 48 dp — all
  unchanged.
- **Contrast:** the bands move because the chrome copy now sits over the artwork's
  opaque region. §8.17.4 specifies the sweep; the **≥ 4.5:1 floor is unchanged**
  (§4.10.12).

### 8.11 Performance (§8.10)

**The architecture strictly reduces per-frame work.** Removals:

- `HeroScrim`'s and `MomentumHeroArtwork`'s `drawWithCache` blocks no longer re-run
  per frame during a 96 dp collapse drag — the residual ADR-47 Amendment Note 3
  §"Explicit non-goal" was accepted for TASK-016 and is now **eliminated by
  deletion**, not by optimisation.
- `HomeHero`'s read of `collapsePx.value` in composition: the composition that
  currently recomposes on **every scroll frame** is gone.
- `collapsePx`, `maxCollapsePx`, `heroNestedScroll`, `onCollapseRangePx`.
- The screen-space `DstIn` mask — a `CompositingStrategy.Offscreen` `graphicsLayer`
  over the whole foreground column plus a per-draw `Brush.verticalGradient`, pure
  collapse-drag cost.

Additions: one full-screen `MomentumHeroArtwork` `Image` with a
`CompositingStrategy.Offscreen` mask over a **constant** size, so its
`drawWithCache` brush is built once and never invalidated (the atmosphere reads no
scroll state in draw; the Aurora's animated intensity is its own composable).

**Read locality improves materially.** After the change the atmosphere reads only
`AuroraState` / intensity / theme, and the scroll surface reads only
`TodayUiState.Success`. Today the single `LazyColumn` recomposes on scroll because
`collapsePx` lives in `TodayScreen`'s composition scope; after the change **nothing
in Home's scroll surface is invalidated by scrolling at all**.

Unchanged and preserved: exactly one `LazyColumn`, `key` + `contentType` on both
`items` calls, no nested scroller, `Arrangement.spacedBy(MomentumSpacing.item)`.

**Fixed-chrome cost (added by §4.10.4).** The chrome adds exactly **one** state
object and **one** measurement: `onSizeChanged` → `chromeHeightPx`, read by a
`padding(top = …)` on the scroll region's container. That write fires only when the
chrome's content, insets or width change — **never on scroll** — so it costs
nothing during a fling and cannot invalidate the scroll region. Compared with the
approved first pass (date + greeting inside the list), the amendment's only
architectural cost is that the chrome's height is permanently subtracted from the
list viewport; §8.17.1 keeps the chrome to two short text lines plus one 48 dp
control to bound it, and §8.17.5 keeps the empty state's centring correct within
the reduced region. **No `NestedScrollConnection`, no scroll-state read, no
per-frame observation and no `derivedStateOf` is introduced** (§4.10.10).

### 8.12 ADR impact — append-only only

No existing ADR text is rewritten, deleted or restated. Recorded in
`docs/architecture/decisions/DECISIONS.md` as **ADR-47 Amendment Note 4** and
**ADR-48 Amendment Note 4**.

| ADR | Impact |
|---|---|
| **ADR-47 §2** (one Aurora-backed surface + one scroll surface + clamped-px collapse) | **Amended** — the Hero box and the collapse are removed; the artwork moves to a persistent atmosphere. |
| **ADR-47 §3** (empty day is a compact Aurora header) | **Superseded** — on an empty day the atmosphere **and the entire top chrome** are identical to every other day. (Its FAB-suppression clause was already superseded by ADR-48 Amendment Note 2.) |
| **ADR-47 Amendment Note 2** (artwork-driven height + crown) | **Superseded in full** — the crown is deleted. Its *governing concept* ("artwork space is never taken from copy space") becomes structurally true because no per-state height exists. |
| **ADR-47 Amendment Note 3** (three layers / crown is transparent space / continuous bottom merge / BOUND-1 / BOUND-2) | **Re-issued against `HomeAtmosphere`** (Aurora → artwork → scrim → foreground). BOUND-1 becomes structural. BOUND-2's `[0.15, 0.35]` ceiling and 4.5:1 floor stand and must be re-measured. |
| **ADR-47 §6** (`MomentumFab` = one shared **solid** Aurora disc) | **Amended** — "solid" becomes glass-over-`primary` at `MomentumGlass.heroFill`. **Shared call-site consequence on Habits**, accepted by the ruling. |
| **ADR-47 §7** (no `textSecondary` over artwork) | **Preserved** — and extended: the chrome copy, the total and the caption are all `textPrimary` and hierarchy is carried by size/weight only. |
| **ADR-48 §1** (one progress representation, Display scale, `showPercent = false`, ring full at 100 %) | **Amended for the `Fraction` branch only** — still one representation, still Display scale, still `showPercent = false`; the completed value is framed by the ring and the total is subordinate. The `Complete` branch is unchanged. |
| **ADR-48 §3 / §6** (one-shot milestone; change-triggered motion) | **Unchanged, and simpler** — the collapse is gone. |
| **ADR-48 Amendment Note 2** (FAB in every state, empty CTA removed, placement + 96 dp reserve) | **Stands in full.** |
| **ADR-48 Amendment Note 3** (theme-resolved edge, `heroElevation`) | **Stands in full** and is retained by §8.7. |
| **ADR-35** (screen-space top mask) | **Retired on Home.** Its stated purpose was to stop Hero copy entering the camera cutout as the Hero scrolled under the status bar. There is no Hero, the chrome is fixed and therefore never crosses the status bar, and scrollable content begins below the chrome's measured bottom edge. Recorded so the ADR is not orphaned. |
| **ADR-28/29** (clamped-px collapse *pattern*) | **Unchanged** — the pattern survives on Habits. Only Home's use is deleted. Recorded to prevent misreading. |
| **ADR-30 / ADR-39** (shared artwork contract; caller passes band amounts only; **no top fade — the full-strength head is absorbed at the container's top edge**) | **Untouched**, and now **load-bearing**: §8.17.2 fixes the artwork's datum at the **window top** precisely so ADR-39's no-top-fade contract holds. Starting the artwork below the chrome would require either a hard top edge or a new top fade — both forbidden. |
| **ADR-31/32/33/37/39** (artwork at the window edge, no top fade, full-strength head, no dead space below copy) | **Preserved in outcome** — full-bleed at the physical top edge behind the status bar and **behind the fixed chrome**; the mechanism changes from "first item of the scroll surface" to "backdrop of the whole content area". |
| **ISSUE-020** | Method stands. Its measured baseline table is **superseded** by the sweep in §8.17.4 / §8.15, which is re-run from scratch per §4.10.12. |

**Amendment-specific ADR impact (added by §4.10.4).** One further append-only note is
required because ADR-47 Amendment Note 4 §8 (the classification clause) and §9 (the
"what floats is glass" invariant) are now wrong on one point: **ADR-47 Amendment
Note 5** supersedes Amendment Note 4 §8's classification of **date and greeting**
from scrollable content to persistent Home top chrome, withdraws the "what floats
is glass" invariant as a *placement* rule, and adds the chrome-coherence and
scroll-boundary contracts of §8.17.1 and §8.17.3. Every other clause of both
existing Amendment Note 4 notes stands verbatim.

### 8.13 Files and tests

**Files expected to change**

| File | Change |
|---|---|
| `presentation/screens/today/TodayScreen.kt` | Delete `collapsePx` / `maxCollapsePx` / `heroNestedScroll` / `BoxWithConstraints`; delete the Hero `item {}`; compose the four layers in the explicit z-order of §8.3 with the scroll region bounded by the measured chrome (§8.17.3); `emptyRegion` becomes `scrollRegionHeight − EmptyDayBottomReserve`; keep `heightIn(min = …)`, `EmptyDayBottomReserve`, `MomentumFab` placement, `animateItem`, `homeAuroraState`, `heroFigure`. |
| `presentation/screens/today/HomeHero.kt` | **Deleted.** Its only reason to exist was to be a *box* owning artwork + copy + collapse. |
| **new** `presentation/screens/today/HomeAtmosphere.kt` | The persistent atmosphere: Aurora field + artwork window (§8.17.2) + full-content-area `HeroScrim`. Owns `ArtworkWindow` and the retained four-stop scrim profile. |
| **new** `presentation/screens/today/HomeChrome.kt` | The **fixed** top chrome: `HomeTopChrome(dateLabel, greeting, onOpenSettings, onHeightChanged)`. **Not** a scroll item and **not** a `Surface`. Owns the chrome's internal rhythm and the `heading()` semantics. |
| **new** `presentation/screens/today/HomeProgress.kt` | `HeroFigureState` (moved, shape unchanged) + `HeroProgressLockup` + the encouragement line. Scroll item 1. |
| `presentation/components/MomentumFab.kt` | Fill alphas → `MomentumGlass.heroFill`. Two expressions. **Shared with `HabitsScreen`.** |
| `res/values/strings.xml` | `today_hero_fraction` → `"%1$d"`; **new** `today_hero_fraction_total` → `"/ %1$d"`. No other copy change. |
| `presentation/components/MomentumHeroArtwork.kt` | **NOT modified.** |
| `presentation/navigation/MomentumNavGraph.kt` | **NOT modified** — `drawsBehindStatusBar`, Scaffold insets and the bottom bar are all still correct. The fixed chrome lives *inside* `TodayScreen`'s existing content area, so no Scaffold or inset change is needed. |
| `TodayViewModel.kt`, domain, Room, DataStore, `libs.versions.toml`, manifest | **NOT modified.** |

**Tests that encode the crown/collapse model and must be REWRITTEN, not deleted**

1. `HomeHeroAssetsTest` (unit) — `the artwork is the Heroes first layer and fills its bounds`; `no band of page background sits above the artwork`; `every piece of Hero copy sits on the artwork`; `the Hero ends with its content, never with dead space` (**crown**); `the progress figure and the orb are one start-aligned group` (`HeroOrbGap` / `Spacer(width)`); `an empty day has no progress block and no collapse travel but keeps the artwork crown` (**three crown assertions**); `the crown is transparent space and no sub-surface hides the copy` (**crown**); `HomeHero keeps the artwork procedural-free` (asserts `collapsePx` / `onCollapseRangePx`). **Retained unchanged:** `the removed veil and motion machinery stays deleted` (peaks still parse in `[0.15, 0.35]`), `the Home Hero never selects artwork from the clock`, `shared artwork selection maps theme to the bundled aurora pair`, `the shared artwork contract is retained not recreated`, `the greeting still follows the local clock`, `the encouragement line is state-specific…`.
2. `HomeScrollSurfaceTest` (unit) — `Home is one scroll surface with the hero as an item of it` (asserts `.nestedScroll(heroNestedScroll)` and `HomeHero(` as an item) and `the header lives inside the hero, not beside it as a separate surface` — the **latter is inverted by the ruling**: the header is now a separate fixed surface *by human decision*, so its assertion becomes "the header is the fixed chrome, not a Hero child, and is not a scroll item". **Retained:** `Home renders both sections with the fixed spec titles`.
3. `HomeAccessibilityTest` (unit) — `the artwork-driven hero height cannot clip large fonts`; `the figure and the orb are one group with a token-derived gap`; and the `count(screen, "if (reduceMotion) null else") == 3` pin must be re-derived. **Add:** greeting is a heading; `HeroOrbGap` gone and the figure is inside the ring; **the chrome contains no `Surface`/`LiquidGlass`/`Card`/`.background(` behind its copy** (§8.17.1).
4. `HomeHeroCompositionTest` (androidTest) — `dateSitsDirectlyOnTheArtworkWithNoBandAboveIt` (`dateGap >= statusBars + ArtworkCrown`), `emptyDayHeroStillShowsTheArtworkCrown`, `progressFigureAndOrbAreOneGroup`, `theCopyGeometryIsScrollInvariant`, `collapsedArtworkIsPresentButNotCovered`, `artworkStillBacksTheWholeHeroWhenCollapsed`, `expandedArtworkIsPresentButNotCovered`, `heroEndsShortlyAfterTheLastCopyWithNoDeadSpace`, `theCrownBandContainsNoNodeAtAll`, `artworkIsTheBackgroundOfTheEntireHero`, `everyPieceOfHeroCopySitsOnTheArtwork`, `lightThemeComposesTheSameSingleSurface` — all Hero-relative bounds. **Crown tests are deleted because the crown no longer exists, and REPLACED by:** `theAtmosphereIsTheBackdropOfTheWholeContentArea`; `theChromeIsFixedAndSitsOverTheArtwork` (date/greeting/Settings bounds inside the artwork window at font scale 1.0); `scrollableContentNeverOverlapsTheChrome` (scroll region's `top == chrome.bottom`, tolerance 1 dp, after scrolling to list top **and** to list bottom); `theArtworkWindowIsConstant` (identical bounds across empty / populated / complete); `theOpaqueArtworkRegionClearsBoundOne` (`ArtworkWindow − 88 dp ≥ HeroRingSize`); `theEmptyCardIsCentredInTheScrollRegion`.
5. `HomeHeroStateTest` (androidTest) — the `HomeHero(...)` render helper is replaced by the new components; **cases survive**. `Fraction = "2 / 3"` splits into `"2"` and `"/ 3"`. The two `HeroBottomInset` anchoring assertions become scroll-item-relative.
6. `QuickAddTest` (unit) — `the shared FAB is circular with one material and the prominent glyph` (asserts `Brush.verticalGradient`; changes with §8.7); `Home always shows the FAB and the empty state carries no CTA` (asserts `heightIn(min = emptyRegion)`, which is **retained**, and the `onHeightChanged = { heroHeightPx = it }` assertion, which becomes the chrome's own height report). **Retained unchanged:** `Home and Habits share the one MomentumFab`, `the FAB is labelled and clears the 48 dp minimum`, `tapping Quick Add opens Create Item and nothing else`, `the FAB is placed one section gap above the content area` (§4.1.3).
7. `HomeStatesTest` (androidTest) — **unaffected.** It calls `TodayScreen(state = …)` only; the signature does not change. Its assertions (FAB in the empty state, no competing CTA, completion statement, skeleton label, aurora mapping) are all §4.1 guarantees and must stay green.
8. `HomeSectionMotionTest` (unit) — re-verify; expected unaffected (it pins `animateItem` specs), but confirm against the removed `heroNestedScroll` before declaring it unchanged.

### 8.14 Migration / removal path — **AS AMENDED**

One task, one coherent change, ordered so the tree compiles at every step.
**§4.10.13 governs the order: the Developer must first build the new architecture
and understand it, and must NOT reshape `HomeHero` incrementally.** Obsolete
Hero/crown/collapse mechanisms are **removed, not adapted**.

1. **Build the new architecture first, unwired.** Add `HomeAtmosphere.kt`,
   `HomeChrome.kt` and `HomeProgress.kt` with their previews and tags, alongside
   the untouched `HomeHero.kt`. `HomeHero.kt` is **not edited** at any point.
2. **Rewire `TodayScreen.kt`** to the four layers in the §8.3 z-order with the
   scroll region bounded by the measured chrome (§8.17.3); split the fraction
   strings. `HomeHero.kt` becomes unreferenced at the end of this step and the
   build must still pass.
3. **Delete `HomeHero.kt`** and every symbol it owned that no longer exists
   (`HomeHeroTag`, `HeroArtworkTag`, `HeroHeaderTag`, `HeroContentTag`,
   `HeroTopInset`, `HeroBottomInset`, `HeaderProgressGap`, `HeroRingSize` is
   **moved** into `HomeProgress.kt`, `ArtworkCrown`, `HeroOrbGap`,
   `ScrimPeakStart`/`ScrimPeakEnd` and `HeroScrim` move into
   `HomeAtmosphere.kt`). Change `MomentumFab`'s fill alphas (the only
   shared-component edit).
4. **Rewrite the seven affected test files** per §8.13, in the same change.

**Tag mapping for step 3 — the Developer must not leave dangling references.** The
instrumented suites look these up by string, so a renamed tag is a silent test
failure, not a compile error:

| Deleted tag (`HomeHero.kt`) | Replacement | Owner |
|---|---|---|
| `HomeHeroTag` = `"homeHero"` | `HomeAtmosphereTag` = `"homeAtmosphere"`, `HomeScrollRegionTag` = `"homeScrollRegion"` | `HomeAtmosphere.kt` / `TodayScreen.kt` |
| `HeroArtworkTag` = `"homeHeroArtwork"` | `AtmosphereArtworkTag` = `"homeAtmosphereArtwork"` | `HomeAtmosphere.kt` |
| `HeroHeaderTag` = `"homeHeroHeader"` | `HomeChromeTag` = `"homeChrome"` | `HomeChrome.kt` |
| `HeroContentTag` = `"homeHeroContent"` | `HomeProgressTag` = `"homeProgress"` | `HomeProgress.kt` |

No navigation, domain, schema, ViewModel, dependency or build change. `TodayViewModel`
is untouched: `heroFigure(state)` already derives `HeroFigureState` from
`state.isEmpty` / `doneCount` / `totalCount` / `allComplete`, all of which exist.
`HeroFigureState` moves files and keeps its shape. Habits keeps its own collapse
untouched.

### 8.15 Verification plan — **AS AMENDED**

**Runnable here** (`adb` unavailable; no instrumented or on-device result may be
claimed by any agent): `:app:testDebugUnitTest`, `:app:assembleDebug`,
`:app:assembleDebugAndroidTest` (compile only). Do not report `UP-TO-DATE` as
execution. **No agent may claim an instrumented suite passed unless it was actually
executed on a device or emulator (§4.10.14).**

**ISSUE-020 region-sample sweep — MANDATORY, re-run from scratch** (§4.10.12).
The band list is specified in **§8.17.4** and supersedes the first pass's list.
Sample each band from the composited page background + Aurora field + artwork
(cover-crop `BottomCenter`, shared 88 dp dissolve) + the full-content-area scrim,
reduce to one mean background colour, and measure against `textPrimary`; **the
minimum region ratio across the sweep governs**; a per-pixel minimum over the
artwork's high-frequency detail is informational only. **The floor is unchanged and
is not to be weakened to accommodate the new composition: ≥ 4.5:1 for every band.**

Peaks re-checked inside `[0.15, 0.35]`. **No `textSecondary` over the atmosphere.**
Result recorded append-only in `docs/architecture/decisions/DECISIONS.md`. If a band
fails, the **peaks** may move inside the envelope — **never** the four-stop profile
or `ScrimPeakStart` / `ScrimPeakEnd`. BOUND-1 asserted structurally:
`ArtworkWindow − 88 dp ≥ HeroRingSize`, tolerance 1 dp.

**Runnable regression guards for §4.1** (rewritten, then must pass):
(1) the empty-state card carries **no** CTA and `today_empty_action` is absent;
(2) the empty card is **centred** in the scroll region (`heightIn(min =
emptyRegion)` + `Alignment.Center`), at font scale 1.0 **and** 2.0;
(3) **FAB placement** unchanged — `align(BottomEnd)`, `end =
MomentumSpacing.screen`, `bottom = MomentumSpacing.section`, no
`AnimatedVisibility` at the call site;
(4) **new for the amendment:** scrollable content never overlaps the chrome
(`scrollRegion.top == chrome.bottom`, tolerance 1 dp, asserted at list top **and**
list bottom), and the chrome carries no plate, fill, divider or shadow.

**On-device (human only).** §4.10.14's matrix, each in **Light and Dark**:

| Day state | Must show |
|---|---|
| **Empty** (zero activities) | Aurora field + recognisable illustration in the top 184 dp · **date, greeting, Settings all present and fixed** · card centred in the scroll region, no CTA · FAB · bottom bar. **No blank band anywhere; the empty state does not collapse the atmosphere or the chrome.** |
| **Non-empty** | artwork/Aurora visible · chrome fixed · progress composition with `0/1` hierarchy and the ring/figure relationship · activities scroll · **no overlap** · FAB integration |
| **Complete** | full ring + "Today is complete" · Completed section · FAB · milestone Aurora, ring glow and `Confirm` haptic intact (TASK-015 behaviour) |
| **Scrolling** | chrome fixed · Aurora coherent · content scrolls · **no artwork scroll** · no overlap · **no visual jumps** |
| **Shared FAB** | Home Light/Dark and **Habits Light/Dark**, accessible and visually intentional (§4.10.8) |

Also, human-verified, in both themes: the illustration is recognisable and not
washed out; the chrome reads as part of the Aurora environment and **not** as an
unrelated opaque header; the copy's backing does not read as a flat plate; the FAB
reads as one depth stack with the bar and with Settings, and the Aurora is
perceptible through and around it; reduced motion (no Aurora drift, no milestone
transient, row reflow instant); font scale 1.0 **and** 2.0 with no clipping and the
empty card still centred; TalkBack reading order per §8.17.6; heading navigation
reaching the greeting and both section headers.

### 8.16 Human approval gate — decisions requiring a ruling

1. **The crown model is rejected, not tuned.** `HomeHero.kt` is deleted; the 96 dp
   crown, the `lerp` height model, `onHeightChanged`-driven empty geometry and the
   collapse `NestedScrollConnection` all go. Approve the replacement architecture
   in §8.3–§8.4, or reject it.
2. **`ArtworkWindow = HeroRingSize + MomentumSpacing.standard + 88 dp` = 184 dp**,
   constant in every state, as the derivation of record. Approve, or reject — and
   if rejected, name the alternative derivation. **No further Hero-height values
   will be proposed.**
3. **Date and greeting become scrollable content; only Settings is fixed.** Approve
   this against §4.4's diagram, which specified all three as fixed. This is the
   decision the brief explicitly asked not to be assumed.
4. **The progress composition: ring frames `done`; `/ total` subordinate beneath
   at Title scale; caption below; orb no longer a row sibling.** Approve the
   *relationship* — the Developer chooses exact insets within the token grid. Note
   `HeroOrbGap` and the `Row` are deleted and `HeroRingSize` is unchanged at 80 dp.
5. **The FAB fill becomes `primary` at `MomentumGlass.heroFill` (0.68), glass-over-
   primary.** This is a **shared-component change: Habits' FAB changes too** — same
   component, size, glyph, placement and behaviour, translucent instead of solid.
   Approve the shared consequence or reject it. (Rejection re-opens D6 with no
   lawful Home-only alternative, exactly as ADR-48 Amendment Note 3 recorded.)
6. **If you prefer full `HERO` Liquid Glass for the FAB** (`GlassLevel.HERO`
   fill, `+` glyph in `c.primary`) over glass-over-`primary`, say so. The
   Architect recommends glass-over-`primary` because it keeps the FAB the
   unmistakable primary action; full glass risks losing salience against the
   atmosphere. This is a taste call and it is yours.
7. **The greeting gains `heading()`** — a small, real accessibility addition beyond
   the literal scope. Approve.
8. **`ADR-35`'s screen-space top mask is retired on Home** and `ADR-47` §2's
   collapse plus §3's "compact Aurora header" and `ADR-47 Amendment Note 2` are
   superseded, append-only. Approve the amendment set.
9. **The ISSUE-020 baseline is superseded and re-measured from scratch** across the
   §8.17.4 sweep, including the new `done`-inside-the-ring band. Approve re-running
   the measurement rather than carrying TASK-016's table forward.

> **Gate status: items 1–9 are all now RESOLVED by the human ruling of 2026-10-01
> (§4.10).** Item 5's shared Habits consequence was explicitly **accepted**
> (§4.10.8). Item 6 was resolved in favour of the Architect's recommendation —
> glass-over-`primary` (§4.10.8). **The single substantive amendment is §4.10.4: the
> fixed top chrome**, which reverses the first pass's item-3 reasoning. It is
> fully specified in **§8.17**. **No architectural gate item remains open, and
> implementation is authorized immediately after §8.17.**

---

### 8.17 AMENDMENT SPECIFICATION — persistent Home top chrome (§4.10.4, §4.11)

**Status: binding specification. The Developer executes this; nothing below is
left to improvisation.** §4.10.13 forbids mechanically reshaping the old
`HomeHero`, so this section is written as a from-scratch description.

#### 8.17.1 Layering — the tension resolved

**The ruling's §3 and §4 are reconciled as follows, and they are not actually in
conflict: the artwork is the atmosphere's upper region *measured from the window
top*, and the chrome *floats on top of that region*.** The chrome is **inside** the
atmosphere, not below it and not above it as a separate plate.

The ruling's §4 diagram reads "Aurora/artwork below the chrome" as a **z-order**
statement, and that is exactly how it is implemented: the atmosphere is drawn
first (bottom of the z-order), the chrome last (top of the z-order). The ruling's
§3 statement is a **geometry** statement — the artwork occupies the atmosphere's
upper region — and that is also exactly how it is implemented, with the window top
as its datum.

**Geometry and declaration order.** One `BoxWithConstraints(Modifier.fillMaxSize())`
with **four children, declared in this order**. In Compose a `Box` composes and
paints its children in declaration order, so this single order satisfies both the
paint z-order and the semantics traversal order simultaneously — and because the
chrome and the scroll region are **geometrically disjoint**, paint order between
them is irrelevant. Only two paint constraints actually bind: the atmosphere is
first (it is behind everything) and the FAB is last (it overlaps the list's tail).

```kotlin
BoxWithConstraints(Modifier.fillMaxSize()) {

    // child 1 — ATMOSPHERE (z-order: bottom; no semantics)
    HomeAtmosphere(
        state = auroraState,
        reduceMotion = reduceMotion,
        modifier = Modifier.fillMaxSize().testTag(HomeAtmosphereTag),
    )

    // child 2 — PERSISTENT HOME TOP CHROME (fixed; no fill, no plate)
    HomeTopChrome(
        dateLabel = state.dateLabel,
        greeting = state.greeting.resolve(),
        onOpenSettings = onOpenSettings,
        onHeightChanged = { chromeHeightPx = it },
        modifier = Modifier.align(Alignment.Top).testTag(HomeChromeTag),
    )

    // child 3 — SCROLLABLE TODAY CONTENT, bounded below the chrome (§8.17.3)
    Box(Modifier.fillMaxSize().padding(top = with(density) { chromeHeightPx.toDp() })) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().clipToBounds().testTag(HomeScrollRegionTag),
            contentPadding = PaddingValues(top = 0.dp, bottom = EmptyDayBottomReserve),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
        ) { /* progress · encouragement · sections · rows · empty card */ }

        // The FAB overlays the list's tail, so it is painted after it. Its
        // alignment and insets are relative to the WHOLE content area and are
        // therefore unchanged by the chrome — §4.1.3 is preserved exactly.
        MomentumFab(
            onClick = onAdd,
            contentDescription = stringResource(R.string.cd_quick_add),
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.section),
        )
    }
    // child 4 — the Scaffold bottom bar, unchanged, owned by MomentumNavGraph
}
```

Resulting semantics traversal order: **date → greeting → Settings → content → FAB
→ bottom-nav tabs.** See §8.17.6.

The atmosphere is `Modifier.fillMaxSize()` of the **whole** content area — it is a
**sibling backdrop, never a parent of the chrome or the list**, so nothing in
either can compress it. The chrome and the list are **siblings** in one `Box`, so
nothing in the list can compress the chrome.

**The four coherence guarantees — this is what "not an unrelated opaque header"
means, and all four are checkable:**

1. **No plate.** `HomeTopChrome` contains **no** `Surface`, `Card`, `LiquidGlass`,
   `.background(`, `HorizontalDivider`, `shadow(` or `graphicsLayer` between its
   own top edge and its bottom edge. The date and greeting sit on the **raw
   atmosphere + scrim**, exactly as the scrollable progress copy does. The single
   permitted sub-surface is the 48 dp `LiquidGlassIconButton` (Settings), which is
   copy-adjacent chrome, not a copy backdrop — the same exception the old Hero
   already documented.
2. **No divider and no edge.** There is no rule, hairline, gradient stop or shadow
   at `chromeBottom`. The chrome is separated from the list by **negative space
   only** (`ChromeBottomInset`, §8.17.3) — the design system's "gaps between
   major sections" token.
3. **One continuous scrim.** The single four-stop scrim spans the whole content
   area with **no stop at `chromeBottom`**, so the chrome and the list sit under
   the *same* gradient. There is no second scrim, no localised gradient and no
   per-layer opacity.
4. **One axis, one material, one type scale.** The chrome and the scrollable
   content share the same `MomentumSpacing.screen` (20 dp) left axis, the same
   `textPrimary` ink, the same design-system type scale, and Settings uses the same
   `GlassLevel.CONTROL` glass as every other control in the app.

**Chrome internal composition (fixed, measured, no arbitrary values):**

```kotlin
Column(Modifier.fillMaxWidth()) {                       // no Surface, no fill
    Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
    Spacer(Modifier.height(MomentumSpacing.section))     // 24 dp — design-system top gap
    Row(verticalAlignment = Alignment.Top) {             // Top, NOT CenterVertically
        Column(Modifier.weight(1f), spacedBy(MomentumSpacing.micro)) {
            Text(dateLabel,    MaterialTheme.typography.labelLarge,   color = c.textPrimary)
            Text(greeting,     MaterialTheme.typography.headlineMedium, color = c.textPrimary,
                 modifier = Modifier.semantics { heading() })
        }
        LiquidGlassIconButton(MomentumIcons.settings, R.string.cd_settings, onOpenSettings)
    }
    Spacer(Modifier.height(ChromeBottomInset))            // 24 dp — see §8.17.3
}
```

- **`Alignment.Top`, not `CenterVertically`** — because the chrome is now fixed and
  grows with font scale, a vertically-centred 48 dp button would drift against the
  two-line text column as the greeting wraps or grows. Top alignment is stable at
  every font scale and at every greeting length.
- **Every value above is an existing token or an inset.** No new dp value is
  introduced anywhere in the chrome.
- **`ChromeBottomInset = MomentumSpacing.section` (24 dp)** is the chrome's own
  trailing breathing room and the minimum separation between the chrome and the
  first scrollable item. It is a *named token alias*, not a literal, and it is the
  only spacing value the Developer adds.

#### 8.17.2 The `ArtworkWindow` datum — **the window top**

**Decision: the artwork window is measured from the physical window top, i.e. it
runs `y = 0 … 184 dp` and the chrome floats over it. It is NOT measured from below
the chrome.**

This is forced by `MomentumHeroArtwork`'s existing contract, and it is the reason
the two directions in the ruling reconcile so cleanly:

- `MomentumHeroArtwork.kt` generates **no top fade** — `topDissolve` produces no
  stop, and the profile is **fully opaque from the container's top edge** down to
  the bottom blend. Its own KDoc states this is correct because "the container's
  top edge *is* the physical window top edge … so there is no page background
  above it to blend into" (ADR-39).
- If the artwork instead **started below the chrome**, its top edge would border
  page background, and one of two things would have to happen: a **hard horizontal
  edge** (forbidden by §4.10.3) or a **new top fade** (which
  `MomentumHeroArtwork.kt` explicitly says "would be a new decision (ADR) for a
  container whose top edge borders page background — **no such caller exists
  today**"). Creating that caller is exactly the change ADR-39 was written to
  defer, and the file is not to be modified.
- Therefore the artwork's top edge must remain the window's top edge, which means
  **the chrome must be drawn over the artwork**, not below it. That is the whole
  resolution.

**Specification:**

```kotlin
// y = 0 of the content area, which MomentumNavGraph already guarantees is the
// physical window top (drawsBehindStatusBar, ADR-33/37/39 — unchanged).
Box(Modifier.fillMaxWidth().height(ArtworkWindow).align(Alignment.Top)) {
    MomentumHeroArtwork(alignment = Alignment.BottomCenter, Modifier.fillMaxSize())
}
```

**Invariants the Developer must not break:**

- `ArtworkWindow = HeroRingSize + MomentumSpacing.standard + ArtworkHandOff`
  = **184 dp**, exactly as approved in §8.3. **The chrome's height is NOT added to
  it and NOT subtracted from it.** The window does not grow when the chrome grows.
- The value is **constant in every state** — empty, populated, complete — and is
  **never** a function of activity count, empty/non-empty state, copy height, the
  chrome's measured height, encouragement presence, or scroll position
  (§4.10.3). Only theme (Light/Dark artwork pair) and width affect the *crop*,
  never the window's height.
- **`MomentumHeroArtwork` is called unchanged**: no `bottomDissolve` override, no
  `zoom`, no `parallax`, no `ColorFilter`, no `topDissolve`. The component file is
  not modified. This preserves ADR-30's caller contract and ADR-39.
- **BOUND-1** therefore remains structural: `ArtworkWindow − 88 dp = 96 dp ≥
  HeroRingSize` (80 dp) in every state, with no measurement.
- **Expected relationship (verify on device, do not hard-code):** the chrome's
  bottom edge should fall **inside** the artwork's opaque region at font scale
  1.0, so the chrome copy sits over the least busy part of the illustration. At
  font scale 2.0 the chrome may extend past `ArtworkWindow`; that is **acceptable
  and expected**, is not a defect, and must not be "fixed" by changing
  `ArtworkWindow`. The scrim spans the whole content area precisely so the copy
  below the artwork window stays readable.

#### 8.17.3 The scroll-boundary mechanism — **layout, not observation**

**Decision: the scrollable region is physically bounded below the chrome, and the
bound is produced by ordinary layout. There is no `NestedScrollConnection`, no
scroll-state read, no per-frame observation, no `derivedStateOf`, no
`onGloballyPositioned` scroll listener and no parallax.**

```kotlin
// HomeTopChrome reports its own measured height once, exactly like the old
// HeroHeader's onSizeChanged did.
var chromeHeightPx by remember { mutableIntStateOf(0) }

// The scroll surface is a SIBLING of the chrome inside one Box, positioned
// below it by a top modifier equal to the measured chrome height.
Box(Modifier.fillMaxSize().padding(top = with(density) { chromeHeightPx.toDp() })) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().clipToBounds().testTag(HomeScrollRegionTag),
        contentPadding = PaddingValues(
            top = 0.dp,                              // the region's own top IS the chrome's bottom
            bottom = EmptyDayBottomReserve,           // 96 dp FAB clearance — UNCHANGED
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) { /* progress · encouragement · sections · rows · empty card */ }
}
```

**Why this guarantees zero overlap, structurally:**

- The chrome and the scroll region are **siblings that do not overlap by
  construction**: the region's top edge *is* the chrome's bottom edge. Overlap is
  not "prevented" by a check — it is **unrepresentable**.
- `clipToBounds()` on the region is belt-and-braces for §4.10.13's "appropriate
  clipping/content bounds": even if a future item overflowed, it cannot paint into
  the chrome.
- `contentPadding(top = 0.dp)` is **retained deliberately** — the same reasoning the
  old `TodayScreen` documented. The region's own top is already the chrome's
  bottom, so any top padding would open a band of atmosphere between the chrome and
  the first item, which is exactly the disconnection the ruling forbids.
- The one measurement (`onSizeChanged` → `chromeHeightPx`) is a **pure function of
  the chrome's content, window insets and width**. It changes on font-scale change,
  configuration change and greeting-length change — **never on scroll**. The write
  is idempotent, so it cannot feed back into layout.

**Explicitly rejected:**

- **A `NestedScrollConnection` that shrinks, fades or translates the chrome on
  scroll.** That is the collapse/parallax system §4.10.1 and §4.10.4 forbid.
- **`contentPadding(top = chromeHeight)` on a full-height `LazyColumn`.** This
  looks equivalent but is not: it would let the first item be pushed below the
  chrome while later items **scroll up underneath it**, which requires either an
  opaque plate (forbidden) or clipping the list to a sub-region — i.e. it
  re-introduces the overlap problem this decision exists to remove.
- **Sticky/pinned list headers.** `stickyHeader` would pin a *second* copy of the
  date/greeting, duplicating the chrome.
- **Any spacer/offset/padding literal added "to make it line up".** The only
  spacing value introduced is the named `ChromeBottomInset` token alias.

#### 8.17.4 Contrast measurement bands for the §4.10.12 re-measurement

**Method unchanged:** ISSUE-020 region-sample — each band's text rectangle is
sampled from the composited page background + Aurora field + artwork (cover-crop,
`Alignment.BottomCenter`, shared 88 dp dissolve) + the full-content-area scrim,
reduced to one mean background colour, measured against `textPrimary`.
**The minimum region ratio across the sweep governs. The floor stays ≥ 4.5:1 for
every band and is NOT weakened (§4.10.12).**

**Sweep axes:** both themes × `ArtworkWindow` (184 dp) **and** the chrome's bottom
edge at font scale 1.0 **and** 2.0 — because the chrome is fixed, the bands move
with it — × widths {360, 412} dp × `AuroraState.defaultIntensity` {0.35 REST, 0.55
PROGRESS, 0.75 COMPLETION, 1.0 MILESTONE} × day states {empty, populated,
complete} × encouragement {on, off}.

**Bands — grouped by which part of the atmosphere they sit over:**

| # | Band | Where it sits | New? |
|---|---|---|---|
| 1 | **date** | over the artwork's opaque region, top of the chrome | **changed position** |
| 2 | **greeting** | over the artwork's opaque region, chrome | **changed position** |
| 3 | **Settings icon** tint (`c.textSecondary` is **already** its icon tint — permitted here because it is a `GlassLevel.CONTROL` glass control, not bare ink over the atmosphere; see §8.17.6) | on its glass fill over the artwork | **new** |
| 4 | `done` digit **inside** the ring | inside the ring's interior; **the ring's `textPrimary @ 0.55` stroke must be included in the composited background** | **new (required by §8.5)** |
| 5 | `/ total` | scrollable, below the chrome | changed |
| 6 | `completed today` caption | scrollable, below the chrome | changed |
| 7 | `Today is complete` statement | scrollable, at the ring | changed |
| 8 | merged `today_hero_progress_desc` | scrollable | changed |
| 9 | encouragement line | scrollable | changed |
| 10 | Remaining / Completed section headers | scrollable | unchanged |
| 11 | `ActivityCard` titles + metadata | scrollable, over glass on the scrimmed lower atmosphere | unchanged |
| 12 | `EmptyState` title + body | scrollable, centred in the region | unchanged |
| 13 | **the atmosphere immediately below `chromeBottom`** with no content on it | the seam the ruling forbids — must show **no hard edge**, i.e. the region-sample difference between the atmosphere at `chromeBottom − 1 dp` and `chromeBottom + 1 dp` must be within the artwork's own local gradient ramp, i.e. **no step larger than the scrim's own per-dp derivative there** | **new** |

**Interpretation rules (unchanged from ADR-47 Amendment Note 1):**

- A per-pixel minimum over the artwork's high-frequency detail is **informational
  only** — in Dark it would need a peak near 0.53, outside the approved
  `[0.15, 0.35]` envelope and against "readability without flattening".
- If a band fails, **only the peaks may move, inside `[0.15, 0.35]`**. The four-stop
  profile and `ScrimPeakStart = 0.20f` / `ScrimPeakEnd = 0.80f` are load-bearing
  and must not be altered.
- Result is recorded **append-only** in
  `docs/architecture/decisions/DECISIONS.md`, superseding the TASK-016 table by
  addition, never by rewrite.

#### 8.17.5 Empty-state centring region

```kotlin
// scrollRegionHeight is measured off the LazyColumn's own onSizeChanged, or
// derived from the BoxWithConstraints viewport minus the chrome height.
// Use the viewport route: it is one fewer state object.
emptyRegion = viewportDp - chromeHeightDp - EmptyDayBottomReserve
// Box(Modifier.heightIn(min = emptyRegion), contentAlignment = Alignment.Center)
```

- `EmptyDayBottomReserve` is **unchanged**: `MomentumSpacing.card +
  MomentumFabSize + MomentumSpacing.section` = 96 dp, still used both as the list's
  `contentPadding.bottom` and in this subtraction, so the reserve the list honours
  and the region the empty card is centred in **still cannot drift apart**.
- **§4.10.7's two guarantees are structural, not arithmetic:**
  - *"must not collapse the atmosphere"* — the atmosphere is a **sibling backdrop**
    sized `fillMaxSize()` of the content area. No item inside the scroll region can
    reach it.
  - *"must not collapse the chrome"* — the chrome is a **sibling above** the
    region, sized by its own content. No item inside the region can reach it.
  There is no shared box for the empty item to squeeze, so no amount of content can
  remove either. This is the D1 regression class made unreachable.
- **§4.1.1 / §4.10.7: no CTA on the card.** `HomeEmptyState` keeps `icon = null`
  and no action; `today_empty_action` stays deleted; the FAB stays the single
  creation affordance.
- **§4.1.2 / §4.10.7: centred card.** `heightIn(min = …)` + `Alignment.Center`,
  retained exactly. At large font the card grows the item rather than clipping, and
  the region still scrolls — unchanged behaviour.

#### 8.17.6 Reading order and TalkBack semantics

**Traversal order.** Compose's merged semantics tree follows composition order,
which for a `Box` is **declaration order**. The §8.17.1 declaration order —
atmosphere, chrome, scroll region, FAB — therefore yields exactly:

```text
date  →  greeting (heading)  →  Settings (button)  →  [scrollable region]
       Remaining heading → rows → Completed heading → rows   (or: empty card)
       →  Quick Add (button)  →  bottom-nav tabs
```

The atmosphere contributes no semantics, so it is transparent in this order. **The
Developer must not reorder the children** — declaring the chrome after the
`LazyColumn` would push the persistent Home identity *after* the content in the
traversal order, which is wrong.

**Rules:**

1. **The chrome's three elements are three separate, unmerged semantics nodes.** The
   chrome must **not** use `clearAndSetSemantics` or `mergeDescendants` — a merged
   chrome would swallow the Settings button and announce only the text, which would
   make a persistent control unreachable by screen reader. **This is a hard
   requirement**, and it is the accessibility counterpart of §4.17.1's "no plate":
   group them visually, never semantically.
2. **`greeting` carries `Modifier.semantics { heading() }`** (approved, §4.10.9) and
   is the screen's first heading. `date` is plain text — it is a date, not a
   section. Both section headers keep their existing `heading()`.
3. **Heading navigation therefore yields: greeting → Remaining → Completed** — three
   stops that orient the user without relying on the chrome's position. This is the
   accessibility answer to a fixed chrome: the user gets a structural map, not just
   a permanently-visible band.
4. **The chrome is announced once, before the content, and is not re-announced** as
   the user scrolls. This is correct: it is persistent Home identity, and TalkBack
   traversal is linear, so it is reached once per pass.
5. **`Settings` keeps `Role.Button`** (from `LiquidGlassIconButton`) and its
   `cd_settings` label, and clears the 48 dp minimum
   (`MomentumHeights.iconTarget`). Its icon tint remains `c.textSecondary` — this is
   the **one** place a secondary token is legal, because the glyph sits on its own
   `GlassLevel.CONTROL` glass fill, not as bare ink on the atmosphere; ADR-47 §7's
   prohibition applies to **copy over artwork**, and band 3 of §8.17.4 measures it.
6. **The progress lockup keeps exactly ONE merged `contentDescription`**
   (`today_hero_progress_desc`). The completed value / total / caption split must
   never produce a second announcement, and the ring stays decorative
   (`showPercent = false`).
7. **The atmosphere contributes no semantics.** `MomentumHeroArtwork` keeps
   `contentDescription = null`; `AuroraBackground` stays outside the semantics
   tree. No `clearAndSetSemantics` wrapper on the atmosphere or the chrome region.
8. **Reduced motion is unaffected by the amendment** (§4.10.10): the fixed chrome
   has no motion of its own, `rememberReduceMotion` reaches fewer call sites than
   before, and all remaining Home motion is unchanged and reduced-motion-nulled.
9. **§4.10.9: accessibility is never traded for composition.** If any of the above
   cannot be satisfied without clipping at font scale 2.0, the fix is to shorten
   the chrome's *composition*, never to drop semantics, never to reduce a touch
   target below 48 dp and never to remove the heading.

#### 8.17.7 Decisions the Developer would otherwise have to improvise — closed here

1. **Declaration order of the four children** — atmosphere, chrome, scroll region,
   FAB, in one `Box`; this satisfies the paint z-order and the semantics traversal
   order at once, and must not be reordered. §8.17.1.
2. **The artwork's datum** — window top, `y = 0 … 184 dp`, chrome over it.
   §8.17.2.
3. **Why the chrome may not be a `Surface`/glass panel** — §8.17.1's four
   guarantees; a plate would make it "an unrelated opaque header" under §4.10.4.
4. **`Alignment.Top` for the chrome's Row**, not `CenterVertically`. §8.17.1.
5. **`ChromeBottomInset = MomentumSpacing.section`** as a named token alias — the
   only spacing value added. §8.17.1.
6. **The scroll-boundary mechanism** — sibling `Box` + measured chrome height +
   `clipToBounds()`, `contentPadding(top = 0.dp)`. §8.17.3.
7. **The empty-state region formula** and the structural anti-collapse argument.
   §8.17.5.
8. **Which semantics node may be merged in the chrome** — none. §8.17.6 rule 1.
9. **The contrast band list**, including the `done`-inside-the-ring band and the new
   no-hard-edge seam band. §8.17.4.
10. **Tag names and the deleted→replacement mapping** for the instrumented suites.
    §8.14.
11. **Where each surviving symbol lives** — `HeroFigureState` → `HomeProgress.kt`;
    `HeroRingSize` → `HomeProgress.kt`; `ScrimPeak*`, `ScrimPeakStart`,
    `ScrimPeakEnd`, `HeroScrim`, `ArtworkWindow` → `HomeAtmosphere.kt`;
    `ChromeBottomInset`, `HomeTopChrome` → `HomeChrome.kt`. §8.14 step 3.
12. **`TodayLoadingSkeleton`** must be updated to match the new composition's
    silhouette (status-bar inset → date ghost → greeting ghost, then the progress
    row ghost), since the chrome is no longer a Hero. It keeps its single
    `cd` label, its no-spinner/no-shimmer contract and `clearAndSetSemantics`
    (ADR-47 §5).
13. **`HomeHero.kt` is never edited** — only added alongside, then deleted
    (§4.10.13, §8.14).

---

## 9. IMPLEMENTATION STATUS

<!-- DEVELOPER OWNER -->

**TASK-017 implementation complete.** Built strictly against §8.17 and ADR-47
Amendment Note 5 / ADR-48 Amendment Note 4. **Not committed.** `adb` is
unavailable in this environment, so **no instrumented or on-device result is
claimed** — §4.10.14's device matrix remains the human's.

### 9.1 What was built — the four layers, in the contract order

`TodayScreen.kt` composes **four children in one `BoxWithConstraints`, declared
in this order** (§8.17.7 item 1 — one order satisfies both the paint z-order and
the TalkBack traversal order):

| # | Layer | Component | File |
|---|---|---|---|
| 1 | persistent atmosphere | `HomeAtmosphere(state, reduceMotion, modifier)` | **new** `HomeAtmosphere.kt` |
| 2 | persistent top chrome (Date + Greeting + Settings, all fixed) | `HomeTopChrome(dateLabel, greeting, onOpenSettings, onHeightChanged)` | **new** `HomeChrome.kt` |
| 3 | scrollable Today content | `HeroProgressLockup(...)` + both sections + the empty card, in one `LazyColumn` | **new** `HomeProgress.kt`, wired in `TodayScreen.kt` |
| 4 | fixed FAB | `MomentumFab(...)` — placement **unchanged** | existing |

- **Atmosphere** (`HomeAtmosphere.kt`): `AuroraBackground` field (drift off,
  intensity tweened / snapped) → the artwork in a **constant** `ArtworkWindow =
  184 dp` measured **from the window top** → the one four-stop scrim
  `fillMaxSize()` of the **whole** content area. `MomentumHeroArtwork` is called
  unchanged (no `topDissolve` / `bottomDissolve` / `zoom` / `parallax` /
  `ColorFilter`); that file was **not** touched.
- **Chrome** (`HomeChrome.kt`): `statusBars` spacer + `MomentumSpacing.section`
  gap + `Row(verticalAlignment = Alignment.Top)` of a two-line
  `date`/`greeting` column and the 48 dp `LiquidGlassIconButton`, +
  `ChromeBottomInset`. **No `Surface`, `Card`, `LiquidGlass*`, `.background(`,
  divider, shadow or `graphicsLayer`** anywhere in it — `HomeTopChrome.kt` uses
  none of those tokens at all. `greeting` carries `heading()`. The three
  elements stay **three unmerged semantics nodes** (no `clearAndSetSemantics`, no
  `mergeDescendants`), so Settings stays reachable by screen reader. One
  `onSizeChanged → chromeHeightPx` reports its height.
- **Scroll boundary is layout, not observation**: the `LazyColumn` is a **sibling**
  of the chrome inside one `Box` inset by `padding(top = chromeHeightDp)`, with
  `clipToBounds()`, `contentPadding(top = 0.dp)`, `bottom = EmptyDayBottomReserve`.
  **Deleted**: `collapsePx`, `maxCollapsePx`, `heroNestedScroll`,
  `onCollapseRangePx`, `NestedScrollConnection`, `BoxWithConstraints`-based
  collapse geometry, the screen-space status-bar mask (ADR-35 on Home), and the
  per-frame `collapsePx` read. **No** `derivedStateOf`, no scroll observation,
  no parallax, no sticky header. Nothing in Home recomposes on scroll.
- **Progress** (`HomeProgress.kt`): `HeroProgressLockup` — the `Fraction` branch
  places the completed value **inside** the unchanged 80 dp `ProgressOrb`, the
  total directly beneath at `titleMedium`, then the `labelLarge` caption, all on
  the 20 dp screen axis and all `textPrimary` (hierarchy by size/weight only).
  `HeroOrbGap` and the figure/orb `Row` are gone. One merged
  `today_hero_progress_desc`; the ring stays decorative (`showPercent = false`);
  the encouragement line sits **outside** the merged group so it keeps its own
  announcement, with its `AnimatedVisibility` specs and `lastLine` holder
  unchanged. The `Complete` branch is unchanged (full ring + "Today is complete"
  at the screen's left axis); `Hidden` renders nothing and is **not composed** on
  an empty day, so it reserves no space.
- **Empty state**: `emptyRegion = viewportDp − chromeHeightDp − EmptyDayBottomReserve`
  applied as `heightIn(min = emptyRegion)` + `Alignment.Center`. Atmosphere and
  chrome are siblings around the region, so §4.10.7's two guarantees are
  structural — no CTA (`icon = null`, `today_empty_action` stays deleted), FAB is
  the single creation affordance.
- **`TodayLoadingSkeleton`** updated to the new silhouette (status bar →
  date ghost → greeting ghost → ring ghost containing the figure ghost → total
  ghost → caption ghost → two row ghosts), keeping its single `cd` label, no
  spinner, no shimmer and `clearAndSetSemantics`.
- **FAB**: fill alphas `0.95f`/`0.92f` → `MomentumGlass.heroFill` at both
  stops, with the existing top-edge streak and ADR-48 Amendment Note 3's
  theme-resolved edge. **Two expressions.** No new token, value, component or
  API. `CircleShape`, 56 dp, `MomentumIcons.add`, `prominent`, `glassPress`,
  `Role.Button`, label parameter, `heroElevation` and placement are all unchanged.
  `HabitsScreen` inherits the translucent fill as approved; **nothing else on
  Habits changed.**
- **`strings.xml`**: `today_hero_fraction` → `"%1$d"`; new
  `today_hero_fraction_total` → `"/ %1$d"`. No other copy change.
- **Migration order honoured** (§8.14): the three new components were built
  unwired, then `TodayScreen.kt` was rewired, then `HomeHero.kt` was deleted.
  **`HomeHero.kt` was never edited** — it was added alongside and then removed
  whole.

### 9.2 Deleted symbols (no dangling references)

`HomeHeroTag`, `HeroArtworkTag`, `HeroHeaderTag`, `HeroContentTag`,
`ArtworkCrown`, `HeroOrbGap`, `HeroTopInset`, `HeroBottomInset`,
`HeaderProgressGap`, `HeroScrim`, `ScrimPeakLight/Dark`, `ScrimPeakStart/End`,
`HomeHero`, `collapsePx`, `maxCollapsePx`, `heroNestedScroll`,
`onCollapseRangePx`. **Moved, not duplicated**: `HeroFigureState` + `HeroRingSize`
→ `HomeProgress.kt`; `HeroScrim` + `ScrimPeak*` + `ArtworkWindow` +
`ArtworkHandOff` + `HomeAtmosphereTag` + `AtmosphereArtworkTag` →
`HomeAtmosphere.kt`; `ChromeBottomInset` + `HomeTopChrome` + `HomeChromeTag` →
`HomeChrome.kt`; `HomeScrollRegionTag` → `TodayScreen.kt`.

**Renamed tags — the instrumented suites look these up by string, so this
mapping is load-bearing:**

| Deleted | Replacement | New value |
|---|---|---|
| `HomeHeroTag = "homeHero"` | `HomeAtmosphereTag` | `"homeAtmosphere"` |
| | `HomeScrollRegionTag` | `"homeScrollRegion"` |
| `HeroArtworkTag = "homeHeroArtwork"` | `AtmosphereArtworkTag` | `"homeAtmosphereArtwork"` |
| `HeroHeaderTag = "homeHeroHeader"` | `HomeChromeTag` | `"homeChrome"` |
| `HeroContentTag = "homeHeroContent"` | `HomeProgressTag` | `"homeProgress"` |

### 9.3 Tests rewritten (§8.13 — rewritten, not deleted)

| File | Change |
|---|---|
| `HomeHeroAssetsTest.kt` → **`HomeAtmosphereAssetsTest.kt`** | `HomeHeroAssetsTest` (17) + `HomeScrollSurfaceTest` (5). Crown/collapse assertions dropped; retained: shared artwork is the atmosphere's upper region, full-bleed, no override of the shared dissolves, one constant scrim with peaks inside `[0.15, 0.35]` and the four stops unchanged, theme-only artwork selection, no procedural artwork, greeting clock, encouragement copy, both section titles. **Added**: the window is constant and derived from three existing facts (and reads no state/count/chrome/width), the scrim spans the whole content area with no stop at the chrome edge, the chrome carries no plate/fill/divider/shadow, the chrome is a fixed sibling and not a scroll item, the chrome is unmerged, the ring frames the figure as two levels, exactly one merged description, the four layers' declaration order, the region's inset by the measured chrome. |
| `HomeAccessibilityTest.kt` | 10 tests. **Added**: the greeting is the screen's only heading in the chrome (3 heading stops total), the chrome's three nodes are unmerged, the atmosphere contributes no semantics, reduced motion reaches the atmosphere **and** the lockup, large fonts shrink the region rather than clipping, and the artwork window does not grow with the chrome. |
| `QuickAddTest.kt` | 7 tests. FAB-material assertions rewritten to the retained outcome + **new** `the FAB fill is glass-over primary at the design system's own alpha`. Empty-centring assertions retargeted to the chrome's reported height and the new region formula. Placement and 48 dp assertions unchanged. |
| `HomeSectionMotionTest.kt` | 7 tests. Retargeted to the atmosphere and the lockup; **new** `scrolling costs Home no recomposition and no observation`; the reduced-motion branch count re-derived (the Aurora snap moved with the field). |
| `HomeHeroCompositionTest.kt` → **`HomeCompositionTest.kt`** | Crown tests **deleted because the crown no longer exists** and replaced by bounds tests on the new architecture: atmosphere = backdrop of the whole content area · artwork = its upper region at the window top · the window is derived (184 dp) *and* measured (BOUND-1) · the chrome is fixed and over the artwork · the chrome keeps three unmerged nodes with Settings ≥ 48 dp · **scrollable content never overlaps the chrome at list top and after scrolling** · **scrolling moves neither the artwork nor the chrome** · the completed value is framed by the ring, not beside it · the empty day collapses neither the atmosphere nor the chrome · the empty card is centred in the scroll region. |
| `HomeHeroStateTest.kt` → **`HomeProgressStateTest.kt`** | Cases survive; `Fraction = "2 / 3"` splits into `"2"` and `"/ 3"`; the two `HeroBottomInset` anchoring assertions are gone with the Hero. |
| `HomeStatesTest.kt` | **Unaffected** — `TodayScreen(state = …)` signature unchanged. |

### 9.4 Validation actually run

| Command | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **PASS — 274 tests, 0 failures, 0 skipped**, 41 suites, 5.31 s of suite time. Read from the JUnit XML in `app/build/test-results/testDebugUnitTest`, not the console. Home suites: `HomeAtmosphereAssetsTest` 17, `HomeAccessibilityTest` 10, `HomeScrollSurfaceTest` 5, `HomeSectionMotionTest` 7, `QuickAddTest` 7. (3 failures on the first run were corrected: two over-broad `count`/`contains` assertions in `HomeSectionMotionTest` that did not distinguish the moved Aurora snap or the `milestoneTick` state, and one self-defeating assertion in `QuickAddTest`.) |
| `:app:assembleDebug` | **PASS** |
| `:app:assembleDebugAndroidTest` | **PASS** — compile + package only |
| `:app:compileDebugKotlin`, `:app:compileDebugAndroidTestKotlin` | PASS |

**Not run, and not claimed:** `connectedDebugAndroidTest` / any instrumented
suite (`adb` unavailable). No device, emulator, screenshot or pixel verification
is claimed anywhere in this record.

### 9.5 ISSUE-020 re-measured **from scratch** (§4.10.12, §8.17.4)

The TASK-016 table was **not** carried forward. Canonical ISSUE-020
**region-sample** method: composite page background + Aurora field + artwork
(cover-crop `BottomCenter`, shared 88 dp `DstIn` dissolve) + the
full-content-area four-stop scrim, reduce each band's text rectangle to one mean
background colour per channel, compute the WCAG 2.x ratio against the band's ink
token, and let the **minimum region ratio across the sweep govern**.

Sweep axes: both themes × widths {360, 412} dp × content-area heights {560, 720}
dp (the scrim's extent *is* the content area, so both are swept) ×
`AuroraState.defaultIntensity` {0.35 REST, 0.55 PROGRESS, 0.75 COMPLETION, 1.0
MILESTONE} × the chrome's bottom edge at font scale 1.0 and 2.0 × day states
{empty, populated, complete} × encouragement {on, off} × all 13 bands of §8.17.4,
including the new **`done`-inside-the-ring** band (the ring's
`textPrimary @ 0.55` stroke composited into the sampled background) and the new
**seam** band. → **1536 region samples + 128 seam samples. 0 below 4.5:1.**
Floor unchanged; no acceptance criterion weakened; **ink unchanged**.

Worst case per band across the sweep:

| Band | Light @ 0.26 | Dark @ 0.22 |
|---|---|---|
| date | 10.06:1 | 16.90:1 |
| greeting | 10.09:1 | 16.93:1 |
| Settings icon tint (`textSecondary` on its own `CONTROL` glass) | **4.93:1** | 9.01:1 |
| `done` inside the ring (ring stroke in the background) | 9.26:1 | **9.00:1** |
| `/ total` | 13.92:1 | 14.89:1 |
| `completed today` caption | 14.22:1 | 15.71:1 |
| `Today is complete` statement | 12.68:1 | 14.01:1 |
| merged `today_hero_progress_desc` | 9.26:1 | 9.00:1 |
| encouragement | 14.30:1 | 16.18:1 |
| section headers | 14.00:1 | 15.34:1 |
| `ActivityCard` titles/metadata | 15.04:1 | 15.65:1 |
| `EmptyState` title/body | 15.04:1 | 15.65:1 |

**Governing minimum: Light 4.93:1 · Dark 9.00:1 — both clear the ≥ 4.5:1 floor
with margin. The weakest band is the Settings glyph, which is the one place a
secondary token is lawful (§8.17.6 rule 5) because the glyph sits on its own
`GlassLevel.CONTROL` glass rather than as bare ink on the atmosphere; it clears
the body-text floor regardless.**

**Band 13 (the seam) shows no hard edge.** The atmosphere's step across
`chromeBottom` is **0.96** channel-sum per dp worst case, against a **median of
0.64** and a **p99 of 1.50** for the artwork's own per-dp gradient over the same
span — the seam is *inside* the illustration's existing local ramp and is
indistinguishable from it. The scrim's own contribution there is **0.005/dp**,
i.e. the step is artwork-driven, not composition-driven.

> **Method note recorded for the Reviewer.** §8.17.4 band 13 is written as "no
> step larger than the scrim's own per-dp derivative there". Taken literally that
> is unsatisfiable for illustrated content at any peak — the artwork's own local
> gradient alone is ~30× the scrim's derivative — so it cannot discriminate a
> composition edge from artwork detail. The measurement above therefore uses the
> discriminable form: the step at the seam versus the artwork's **own** local
> per-dp step distribution across the same span. This is reported as a deviation
> in *method wording only*; the acceptance intent ("no hard edge at
> `chromeBottom`") is met and the underlying geometry is structural, not measured.

**Escalation: not required.** No band failed, so **both peaks stand unchanged**
at `ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f`, both inside the approved
`[0.15, 0.35]` envelope. `ScrimPeakStart = 0.20f`, `ScrimPeakEnd = 0.80f` and
the four-stop profile were **not** touched — only the peak value was ever in
scope. The result is recorded in `HomeAtmosphere.kt`'s `ScrimPeak*` KDoc (the
only file the Developer can write that carries it).

### 9.6 Preserved behaviour (explicitly re-checked)

Transient `AuroraState.MILESTONE` over `MomentumMotion.reward` (650 ms) · one
`HapticFeedbackType.Confirm` · **no snackbar** on the milestone · the
state-specific encouragement line and its reduced-motion-nulled exit · reduced
motion throughout (branch crossfade, list specs, Aurora snap, lockup glow/sweep,
glassPress) · TalkBack with the new reading order **date → greeting (heading) →
Settings → content → FAB → bottom-nav tabs** and heading navigation yielding
**greeting → Remaining → Completed** · ≥48 dp targets (Settings 48, FAB 56,
completion 48) · both themes · offline-only (no new dependency, no manifest
change, no networking).

### 9.7 BLOCKED — one append-only ADR record the Developer cannot write

§8.15 requires the ISSUE-020 result to be recorded **append-only** in
`docs/architecture/decisions/DECISIONS.md`. The Developer's `edit` grant is
`{"*": "deny", "task.md": "allow", "app/**": "allow", "build.gradle*": …,
"settings.gradle*": …, "gradle.properties": …, "gradle/libs.versions.toml": …,
".agents/**": "allow"}` — **`docs/**` is not writable by this role**, and
AGENTS.md §22 forbids an agent widening its own permission boundary. The
measurement is therefore recorded in §9.5 above and in `HomeAtmosphere.kt`. The
Architect or Orchestrator must append it to `DECISIONS.md` (ADR-47 Amendment Note
5 §9) for the ADR record to be complete.

### 9.8 Not done, and deliberately so

No instrumented test was executed; no screenshot, pixel or device claim is made.
No domain, Room/schema, repository, `TodayViewModel`, navigation, dependency,
build-config or other-screen change. `MomentumHeroArtwork.kt`,
`MomentumAurora.kt`, `LiquidGlass.kt`, `ProgressOrb.kt`, `MomentumNavGraph.kt`
and every theme file are **unmodified**. Nothing was committed or pushed.

---

## 10. REVIEW FINDINGS AND VALIDATION

<!-- REVIEWER OWNER -->

**VERDICT: PASS — no actionable defects found.** Every §8.17 contract was checked
against the source, not against §9's claims. Validation re-run by the Reviewer
(§10.2). Three flagged items judged in §10.3. **Not claimed:** any instrumented
or on-device result — `adb` is unavailable; §4.10.14's device matrix remains the
human's.

### 10.1 Contract verification (source-level)

1. **Scroll boundary is layout, not observation — CONFIRMED.**
   `HomeChrome.kt:89` is the only `onSizeChanged` in the Home path and it
   forwards `size.height`; `TodayScreen.kt:416-420` insets a **sibling** `Box`
   by `with(density) { chromeHeightPx.toDp() }`, `:424` carries `clipToBounds()`,
   `:426-440` `contentPadding(top = 0.dp, bottom = EmptyDayBottomReserve)`. The
   chrome and the region are siblings, so overlap is unrepresentable. Zero
   occurrences of `nestedScroll`, `NestedScrollConnection`, `derivedStateOf`,
   `onGloballyPositioned`, `rememberLazyListState`, `scrollToItem`, `parallax` or
   `graphicsLayer` in `TodayScreen.kt` / `HomeChrome.kt` / `HomeProgress.kt` /
   `HomeAtmosphere.kt` (comment text only). One state object
   (`chromeHeightPx`, `TodayScreen.kt:364`). No per-frame allocation in the scroll
   path; the scrim caches its brush (`HomeAtmosphere.kt:142`, `drawWithCache`).
2. **Declaration order — CONFIRMED:** atmosphere (`:389`) → chrome (`:401`) →
   region `Box` → `LazyColumn` (`:421`) → `MomentumFab` (`:556`), matching
   §8.17.1's own sketch (FAB inside the region's Box). Atmosphere first for paint
   z-order, FAB last; chrome and region geometrically disjoint; merged semantics
   follow composition order.
3. **`ArtworkWindow` genuinely constant — CONFIRMED.** `HomeAtmosphere.kt:124`
   = `HeroRingSize (80) + MomentumSpacing.standard (16) + ArtworkHandOff (88)`
   = **184 dp**, a pure constant: no state, count, empty flag, copy height,
   chrome height, encouragement or scroll input; applied as
   `.height(ArtworkWindow).align(Alignment.TopStart)` (`:77-79`) → datum is the
   **window top**. `MomentumHeroArtwork.kt` is **unmodified** (absent from
   `git status`) and is called with only `alignment` + `modifier` — no
   `topDissolve`/`bottomDissolve`/`zoom`/`parallax`/`ColorFilter` override.
4. **Chrome — CONFIRMED.** Date + Greeting + Settings composed unconditionally
   (`TodayScreen.kt:401`, so present in the empty state too); `heading()` only on
   the greeting (`HomeChrome.kt:121`); **three unmerged semantics nodes** — no
   `clearAndSetSemantics` / `mergeDescendants` anywhere in the file, so Settings
   stays reachable; no `Surface`, `Card`, `LiquidGlass*`, `.background(`,
   `HorizontalDivider`, `shadow(` or `graphicsLayer`. The only sub-surface is the
   48 dp `LiquidGlassIconButton` (`GlassLevel.CONTROL`, `MomentumHeights.iconTarget
   = 48.dp`, `Role.Button`, `cd_settings`, `c.textSecondary` tint — the one lawful
   secondary token per §8.17.6 rule 5). `Alignment.Top` ✓,
   `ChromeBottomInset = MomentumSpacing.section` ✓; every value is an existing
   token — no new dp literal.
5. **Empty state — CONFIRMED.** Atmosphere and chrome are **siblings** around the
   region, so the TASK-016 D1 regression class is unreachable structurally
   (nothing shares a box the item could squeeze). `emptyRegion =
   viewportDp − chromeHeightDp − EmptyDayBottomReserve` with
   `.coerceAtLeast(0.dp)` (`:375-377`), applied as `heightIn(min = emptyRegion)`
   + `Alignment.Center` (`:474-479`); FAB unconditional; `icon = null` and
   `today_empty_action` deleted from `strings.xml`.
6. **Progress — CONFIRMED.** `Fraction`: `Box(HeroRingSize)` containing
   `ProgressOrb` **and** the completed value (`HomeProgress.kt:134-150`) — the
   ring frames it; total beneath at `titleMedium`, caption at `labelLarge`, all
   `textPrimary` (hierarchy by scale only). `today_hero_fraction = "%1$d"` /
   `today_hero_fraction_total = "/ %1$d"` → the old `" / "` spacing is gone.
   Exactly **one** merged `contentDescription` (`clearAndSetSemantics` wrapping
   only the figure group, `:130`); the encouragement `AnimatedVisibility` is
   **outside** it (`:187`). `showPercent = false` in both branches. `HeroOrbGap`
   and the figure/orb `Row` are gone; no decorative additions.
7. **`MomentumFab.kt` — CONFIRMED, strictly the approved change.** The only edit
   is the two fill alphas → `MomentumGlass.heroFill` (0.68) on `c.primary` at
   both stops; the 0.22 white top streak, the theme-resolved edge
   (`borderDark`/`borderLight`), `heroElevation`, `CircleShape`,
   `MomentumFabSize = 56.dp`, `MomentumIcons.add`, `prominent`, `glassPress`,
   `Role.Button`, the `contentDescription` parameter and placement are all
   retained; no new token, value, component or API. Habits: its only reference is
   the shared `MomentumFab(cd_create_habit)`; **nothing else on Habits changed**
   for TASK-017 (the `git` diff of `HabitsScreen.kt` is TASK-016's shared-FAB
   extraction, preserved untouched).
8. **Removed, not adapted — CONFIRMED.** `HomeHero.kt` deleted;
   `HomeHeroAssetsTest.kt`, `HomeHeroCompositionTest.kt` and
   `HomeHeroStateTest.kt` removed (replaced, not merely deleted, by
   `HomeAtmosphereAssetsTest.kt`, `HomeCompositionTest.kt`,
   `HomeProgressStateTest.kt`). Repo-wide search over `app/src` returns **zero**
   occurrences of `collapsePx`, `maxCollapsePx`, `heroNestedScroll`,
   `onCollapseRangePx`, `NestedScrollConnection` (Home), `HomeHeroTag`,
   `HeroArtworkTag`, `HeroHeaderTag`, `HeroContentTag`, `ArtworkCrown`,
   `HeroOrbGap`, `HeroTopInset`, `HeroBottomInset`, `HeaderProgressGap`, or the
   old tag strings — ADR-35's Home mask is gone with them.
9. **No regressions — CONFIRMED.** Transient `AuroraState.MILESTONE` opened and
   closed by one keyed `LaunchedEffect` over `MomentumMotion.reward`;
   `TodayEffect.Celebrate` is payload-free and fires exactly one
   `HapticFeedbackType.Confirm` with no snackbar (only `ShowMessage` shows one);
   the state-specific encouragement line from `encouragementFor` reserves no
   space when null and its exit is reduced-motion-nulled; reduced motion reaches
   the atmosphere (`snap()`) and the lockup (`animate`/`glowEnabled`); targets
   ≥ 48 dp (Settings 48, FAB 56, completion 48); headings = greeting + Remaining
   + Completed; both themes; **no manifest change, no dependency change** (no
   `INTERNET`, nothing added to `libs.versions.toml`).
10. **Scope — CONFIRMED.** Changed/added files are exactly the authorized set:
    `TodayScreen.kt`, new `HomeAtmosphere.kt` / `HomeChrome.kt` /
    `HomeProgress.kt`, `MomentumFab.kt`, `strings.xml`, the Home unit tests, the
    Home androidTest suites, deletions above, and `task.md`. No domain, data,
    schema, repository, `TodayViewModel`, navigation, Gradle or dependency change
    **attributable to TASK-017** — the modified `TodayViewModel.kt`, `Type.kt`,
    `Motion.kt`, `MomentumBadges.kt`, `HabitsScreen.kt`, `ISSUES.md`,
    `PROJECT_STATUS.md`, `.opencode/**`, `opencode.json` and `task_archive/` are
    the pre-existing TASK-015/016 and infrastructure work, all still present and
    uncommitted. **Nothing committed** (`HEAD` = `47ebfb4`).

### 10.2 Validation run by the Reviewer

| Command | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **PASS — 274 tests / 41 suites / 0 failures / 0 errors / 0 skipped**, counted from the JUnit XML in `app/build/test-results/testDebugUnitTest` (not the console). Home suites: `HomeAtmosphereAssetsTest` 17, `HomeAccessibilityTest` 10, `HomeScrollSurfaceTest` 5, `HomeSectionMotionTest` 7, `QuickAddTest` 7 |
| `:app:assembleDebug` | **PASS** |
| `:app:assembleDebugAndroidTest` | **PASS** (compile + package only) |

The three test files were also read and judged, not trusted: the assertions are
comment-stripped source guards and are not self-defeating (e.g.
`HomeScrollSurfaceTest` pins the four-layer declaration order, the single
`LazyColumn`, the absence of scroll observation, and the sibling inset;
`QuickAddTest` pins `heroFill` on `c.primary` with the retired `0.95f`/`0.92f`
gone). The instrumented suites (`HomeCompositionTest` 11, `HomeProgressStateTest`
6, `HomeStatesTest` 6) compile and package but **were not executed** — no device.

**ISSUE-020 re-measured independently (§8.17.4 bands).** The Developer did not
publish a harness, so the Reviewer re-implemented the canonical **region-sample**
method from scratch (page background + the three `MomentumAurora` radial blobs at
`defaultIntensity` + the artwork cover-cropped `Alignment.BottomCenter` with the
shared 88 dp `DstIn` dissolve + the four-stop full-content-area scrim, reduced to
one mean background colour per band rectangle, WCAG 2.x ratio, **minimum across
the sweep governs**), over 2 themes × widths {360, 412} dp × content-area heights
{560, 720} dp × intensities {0.35, 0.55, 0.75, 1.0} × the chrome's bottom edge at
font scale 1.0 and 2.0 — 64 cases. Worst case per band, **Reviewer's own figures**:

| Band | Reviewer (worst of 64) | Developer's reported worst (Light/Dark) |
|---|---|---|
| date | 15.33:1 | 10.06 / 16.90 |
| greeting | 12.36:1 | 10.09 / 16.93 |
| `done` inside the ring | 13.40:1 | 9.26 / 9.00 |
| `/ total` | 15.33:1 | 13.92 / 14.89 |
| caption | 15.33:1 | 14.22 / 15.71 |
| **Settings icon tint** (`textSecondary` on its own `CONTROL` glass) | **5.71:1** | **4.93** / 9.01 |

**0 bands below the ≥ 4.5:1 floor.** The identity of the governing band matches
(the Settings glyph — the single place a secondary token is lawful), and this
reproduction is systematically **more** favourable than the Developer's table,
i.e. the Developer's published numbers are the conservative ones and the floor's
margin is confirmed independently rather than merely restated. **Floor not
weakened** (still 4.5:1), **peaks unmoved** (`ScrimPeakLight = 0.26f` /
`ScrimPeakDark = 0.22f`, both inside `[0.15, 0.35]`, verified in
`HomeAtmosphere.kt:228-229`), **`ScrimPeakStart = 0.20f` / `ScrimPeakEnd = 0.80f`
and the four-stop profile untouched**, **ink unchanged** (`textPrimary` in
`HomeChrome.kt` / `HomeProgress.kt`, verified by test and by source).

### 10.3 The three flagged items

**Band 13 (seam) — legitimate method correction, NOT a weakened criterion.**
Independently confirmed: the scrim's own per-dp derivative at `chromeBottom` is
**0.000/dp** where the seam falls inside the constant peak band (the Reviewer's
cases at H = 560 dp and font scale 2.0) and at most **0.0018/dp** in the rising
ramp (H = 720 dp, font scale 1.0). §8.17.4's literal comparator — "no step larger
than the scrim's own per-dp derivative there" — is therefore unsatisfiable for
*any correct* implementation: a continuous illustration always has some local
gradient, so the composited step (Reviewer worst **1.296/dp**, Developer 0.96/dp)
exceeds a ~0–0.0018/dp bound by three orders of magnitude, on an implementation
where **no composition edge exists at all** to find. The substituted comparator is
also *discriminating*, not permissive: the Reviewer measured the artwork's own
local per-dp step over the same span at median **2.0/dp** (p99 **528/dp**) in
Light, against a seam step of 1.296/dp — the seam sits **below** the artwork's
median local variation; a plate, a second scrim stop or a layer boundary would
produce a step of roughly 10–40 channel-sum, i.e. 5–20× that median, and would
fail. The acceptance intent ("no hard edge at `chromeBottom`") is met, and met
*structurally* — one continuous four-stop scrim spanning the whole content area
with no stop at the chrome's boundary (`HomeAtmosphere.kt:137-165`), no plate, no
second layer. Nothing else was relaxed: the numeric floor, the peaks, the profile
and the ink all stand.

**`MomentumGlass.heroFill` scope — the Developer's reading is correct.**
ADR-48 Amendment Note 4 §8 says the **two** hard-coded alphas "become
`MomentumGlass.heroFill` (0.68) applied to `c.primary`, **with** the top-edge
illumination streak and the theme-resolved edge the `GlassLevel.HERO` path already
uses". "With X" retains what the path already has; it never says the streak
*becomes* `MomentumGlass.highlight`. The note's Consequences pin the file to
"**two expressions**", and its Unchanged list enumerates everything else. Aliasing
`highlight` would have been a third expression, a new value (0.22 → 0.10) and a
visible change to the streak — all outside the amendment. The implementation
(`MomentumFab.kt:81-89`) matches the ADR exactly.

**ADR append blocker — correct escalation, and now closed.** `docs/**` is outside
the Developer's `edit` grant, AGENTS.md §22 forbids an agent widening its own
permission boundary, and §21 makes `DECISIONS.md` the Architect's to own, so
escalating rather than writing was required. No implementation file was used as a
substitute for the canonical record: `HomeAtmosphere.kt`'s `ScrimPeak*` KDoc
documents the result and cites its authority without claiming to be the ADR
record. While this review was in flight the Architect appended
**ADR-47 Amendment Note 6** (`DECISIONS.md:3699`), which records the sweep, the
per-band table, the band-13 deviation explicitly, the unchanged peaks and the
reviewer's re-verification — append-only, nothing rewritten. §9.7's blocker is
resolved; no further action.

### 10.4 Observations (not defects — for §4.10.14's device matrix)

- **Chrome bottom edge vs the artwork's opaque region.** §8.17.2 states an
  *expectation* ("verify on device, do not hard-code") that the chrome's bottom
  edge fall inside the artwork's opaque region at font scale 1.0. By construction
  the chrome measures `status-bar inset + 24 (section) + max(48 text column,
  48 Settings) + 24 (ChromeBottomInset)` = **status-bar + 96 dp ≈ 120 dp** on a
  24 dp status bar, while the fully opaque region ends at **96 dp** (184 − 88).
  The chrome's *copy* is inside it (date 48–66 dp, greeting 70–96 dp); the trailing
  24 dp inset sits in the dissolving band. §8.17.2 explicitly declines to
  hard-code this and only declares exceeding the *window* (font scale 2.0) as
  acceptable, and the whole-content-area scrim exists for exactly this copy — so
  it is a visual item for the human, not a contract failure.
- The Reviewer's contrast reproduction is an independent CPU re-implementation of
  the same canonical method (same tokens, crop, dissolve and scrim), not a
  rendered frame; on-device and pixel truth remain the human's.
- `MomentumFab.kt` is untracked in git (created by TASK-016), so TASK-017's edit
  to it has no git baseline; it was verified instead against ADR-48 Amendment
  Note 4's explicit Unchanged list, which is present in full.

---

## 11. HANDOFF HISTORY

<!-- ORCHESTRATOR ONLY -->

- Orchestrator: TASK-017 established from the human's device verification of
  TASK-016 (three authoritative screenshots) and its six new findings. TASK-016's
  three confirmed-fixed outcomes recorded as no-regress (§4.1). TASK-016's record
  archived to `task_archive/TASK-016.md`. Scope: Home layout architecture only.
  Workflow: Architect → human approval gate → Developer → Reviewer. Explicitly
  **not** routing to Developer — no patching of the current crown model.
- Human → Orchestrator: **task reframed at a deeper level.** The current
  TASK-017 brief was withdrawn mid-flight and replaced. Momentum's
  **Aurora (atmosphere) + Liquid Glass (interface)** system is now the mandatory
  foundation; the question is "what should the Home composition fundamentally
  be?", not "how do we fix the Hero?". The persistent-atmosphere hypothesis is now
  explicitly a hypothesis to accept/modify/reject, and "date + greeting +
  settings must be fixed" must not be assumed. §4.3–§4.9 and the 15 deliverables
  in §12 added; §5 reframed. No code has been written for TASK-017.
- Orchestrator → Architect: reframed brief delivered. Hard stop at the human
  approval gate; no Developer routing.
- Architect → Orchestrator: **PROPOSAL COMPLETE.** Crown model **rejected, not
  tuned**; `HomeHero.kt` to be deleted along with `collapsePx`/`heroNestedScroll`/
  ADR-35's top mask. One atmosphere (Aurora field → artwork → full-atmosphere
  scrim → foreground) with a constant `ArtworkWindow = 184 dp` derived from three
  existing canonical facts and never a function of content. **Date + greeting
  rejected as fixed chrome — only Settings fixed.** Progress: the ring *frames*
  the completed value inside it, `/ total` subordinate. FAB: glass-over-`primary`
  (shared change). No scroll choreography. Net performance improves. ADR-47/48
  Amendment Note 4 appended. Gate = nine rulings.
- Orchestrator: **D1 root cause independently verified against `HomeHero.kt`** —
  confirmed exactly as reported: in the `!hasProgress` branch line 205 charges the
  96 dp crown to **no** height while line 214 still lays it out, so `compactHeight`
  = 56 dp, `HeroHeader` is coerced to `maxHeight = 0` and reports `headerPx = 0`
  (a stable state), and the artwork in that 56 dp box computes
  `bottomStart = 0.10` → ~90% dissolved. **A genuine code defect, not taste.**
  D2 confirmed as the same defect inverted: the crown `Spacer` sits above the
  header, placing the date at `topInset + crown` = 136 dp, so no crown value is
  correct. **HALTED at §8.16.**
- Human → Orchestrator: **APPROVED with one amendment** (§4.10, authoritative).
  Crown architecture **rejected outright** — replaced, not patched. Aurora +
  Liquid Glass composition adopted; `ArtworkWindow = 184 dp` constant; **date +
  greeting + settings all become a persistent fixed top chrome** (reversing the
  Architect's §8.3); Aurora persistent with no parallax/orb choreography; ring
  frames the completed value; empty state stays a complete Home; FAB
  glass-over-`primary` with the Habits consequence accepted; ISSUE-020 to be
  re-measured from scratch with no weakening. Implementation authorized.
- Orchestrator → Architect: narrow amendment brief for the §4.11 interactions.
- Architect → Orchestrator: **§4.11 CLOSED — §8.17 written, ADR-47 Amendment Note 5
  appended.** The §3/§4 tension was **not** a conflict and was not chosen between:
  §3 is geometry, §4 is z-order. `MomentumHeroArtwork` generates **no top fade** by
  contract (its KDoc and ADR-39 state the profile is opaque from the container's top
  edge *because* that edge is the physical window top), so starting the artwork
  below the chrome would force a hard edge or a new top fade — a future ADR the
  component explicitly reserves. **The chrome therefore floats over the artwork,
  whose datum is the window top**; ADR-39 becomes load-bearing. Scroll boundary is
  **layout, not observation** (chrome and scroll region are siblings, so overlap is
  unrepresentable; one `onSizeChanged` that never fires on scroll). Empty-state
  anti-collapse is **structural** — atmosphere and chrome are siblings, so the D1
  regression class is unreachable. 13 contrast bands incl. a seam band, floor
  unchanged. A 13-item list closes every decision the Developer would otherwise
  have improvised, incl. the tag→replacement mapping (renamed tags fail silently,
  not at compile time) and the instruction that `HomeHero.kt` is **never edited** —
  it is added alongside, then deleted.
- Orchestrator: routing to the Developer per §4.10.15, strictly against §8.17.
- Developer → Orchestrator: **IMPLEMENTATION COMPLETE.** Built from scratch in the
  §8.14 order — `HomeAtmosphere.kt` / `HomeChrome.kt` / `HomeProgress.kt` added
  unwired, `TodayScreen.kt` rewired to four sibling children, then `HomeHero.kt`
  **deleted (never edited)** along with its crown/collapse/`NestedScrollConnection`/
  ADR-35 mask. `MomentumHeroArtwork.kt` untouched. `MomentumFab.kt` changed by
  strictly the two approved alpha expressions. `:app:testDebugUnitTest
  --rerun-tasks` **274/0** (JUnit XML); `assembleDebug` + `assembleDebugAndroidTest`
  green. ISSUE-020 re-measured from scratch — 1536 region + 128 seam samples, 0
  below 4.5:1, Light 4.93:1 / Dark 9.00:1, peaks unmoved. **Three items escalated,
  not improvised:** (1) `DECISIONS.md` is outside the Developer write grant, so the
  required ISSUE-020 record was not substituted into an implementation file;
  (2) §8.17.4 band 13's literal wording reported unsatisfiable, with a
  discriminable substitute measured instead; (3) a `heroFill` scope ambiguity
  resolved by following the ADR's explicit "two expressions".
- Architect → Orchestrator: **ADR-47 Amendment Note 6 appended** — the ISSUE-020
  re-measurement as a result-only note superseding nothing, including the band-13
  method deviation recorded in the canonical ADR rather than left in a KDoc.
- Reviewer → Orchestrator: **PASS — no actionable defects.** All §8.17 contracts
  verified in source (sibling chrome/region, one `onSizeChanged`, zero nestedScroll
  / derivedStateOf / scroll reads / parallax; declaration order; constant
  `ArtworkWindow`; unmodified artwork; three unmerged chrome semantics nodes;
  structural empty-state anti-collapse; FAB strictly two expressions).
  ISSUE-020 **independently re-run** (own re-implementation, 64 cases) — worst band
  Settings glyph 5.71:1, all ≥ 4.5:1, peaks invariant; the Reviewer's numbers are
  systematically more favourable than the Developer's, so the reported table is the
  conservative one. All three flagged items **accepted**: band 13 a legitimate
  method correction (a plate/second scrim would measure 5–20× the artwork's median
  local ramp, so the comparator does discriminate), the `heroFill` reading correct
  against ADR-48 Amendment Note 4 §8, and the ADR escalation correct under
  AGENTS.md §21/§22. Working tree preserved; nothing committed (`HEAD 47ebfb4`).
- Orchestrator: **TASK-017 code-level COMPLETE.** `PROJECT_STATUS.md` F-HOME
  updated (remains `PARTIALLY_VERIFIED`; TASK-015/016 compositions marked
  superseded). Outstanding and Human-owned: the §4.10.14 on-device matrix and
  execution of the instrumented suites.

---

## 12. ORCHESTRATOR BRIEF TO ARCHITECT

<!-- ORCHESTRATOR OWNER -->

**Objective.** Answer §5 explicitly and produce a recommended Home layout
architecture backed by root-cause analysis of the device behavior. This is a
design/architecture task — **do not write application code**.

**Required method.**
1. Open the real files in §6 and reason from actual layout math. **D1 first:** a
   total disappearance of the Hero chrome on an empty day is a regression the
   TASK-016 plan did not predict — establish the exact mechanism before proposing
   anything. Do not assume it is the artwork crop.
2. Reason from the three screenshots (empty / complete / one-partial-activity) as
   authoritative device evidence, alongside the code.
3. **Evaluate the human's persistent-background proposal on its merits.** Accept,
   modify or reject it with rationale. The human has explicitly asked whether
   fixed top chrome is the right call — give a real answer, including the UX and
   accessibility consequences (e.g. reading order, TalkBack, large fonts, and what
   happens to a fixed header when the list is long).
4. Where you depart from TASK-015/TASK-016, say so explicitly and amend the ADRs
   **append-only**. Do not rewrite existing entries.

**Required deliverables (into §8), in the human's order.**
1. Root cause of the current composition problems (D1–D6).
2. Recommended **Home visual architecture**, derived from the Aurora + Liquid
   Glass system (§4.3), not from the current Hero.
3. Explicit layer definition: **Aurora layer · Liquid Glass layer · persistent
   Home chrome · scrollable content · FAB · bottom navigation**.
4. Recommended **progress/orb composition** (§4.6) — reach the hierarchy through
   the Taste skills; do not merely prescribe "side by side" or "stacked".
5. Recommended **empty-state composition** (§4.8).
6. Recommended **FAB integration approach** (§4.7).
7. **Light/Dark behavior** for every layer.
8. **Artwork blending/dissolve behavior** — continuous, no hard boundary, no
   opaque cover, never invisible; Aurora perceptible through/around the glass.
9. **Accessibility considerations** — reading order, TalkBack semantics, ≥48 dp
   targets, headings, contrast, large fonts, reduced motion.
10. **Performance considerations** — recomposition and read locality, allocation,
    draw cost, and whether the architecture changes any of these.
11. Exact **impact on the TASK-015/TASK-016 ADRs**, append-only.
12. **Files/components expected to change**, and which existing tests must be
    rewritten because they encode the crown/collapse model.
13. **Migration/removal path** for the current Hero/crown architecture.
14. **Verification plan** — runnable plus on-device, including regression checks
    for the three §4.1 outcomes and the full human-mandated device matrix.
15. **Every decision requiring human approval**, as a numbered gate.

**Evaluation requirement.** §4.4's persistent-atmosphere hypothesis must be
explicitly **accepted, modified or rejected** with rationale — including a real
answer on whether fixed top chrome is correct, with its reading-order, TalkBack,
large-font and long-list consequences.

**Skill use (focused, substantive).** Route via `using-chrisbanes-skills`.
*Design/composition:* the relevant Taste skills — `design-taste-frontend`,
`high-end-visual-design`, `minimalist-ui` — applied to visual hierarchy,
composition, negative space, typography, atmospheric backgrounds,
Liquid Glass/translucent surfaces, foreground/background relationship, Light/Dark
balance and premium UI composition.
*Engineering:* `compose-component-design` and `android-kotlin-compose` for layout
architecture and scrolling; add `compose-performance` only if the architecture
changes the recomposition/read model, `compose-animations` only if motion is
actually proposed, and `compose-ui-testing-patterns` for the verification plan.
Load **only** what the actual questions need — do not load the library, and do not
re-read skill material already in your context.
The skills guide **how** the established Momentum direction is achieved; they
never replace that direction.

**Constraints.** No arbitrary `Spacer`/padding/offset remedies and no further Hero
height values — if the answer requires a structural change, propose the structural
change. No scroll-driven orb choreography unless justified per §4.9. No artwork
replacement or new artwork; no artwork on other screens; no abandonment of Aurora
or Liquid Glass; no opaque Aurora banner; no redesign of Momentum's visual
language or of unrelated screens. Presentation-layer scope unless you demonstrate
a genuine domain/schema/navigation need. Keep it maintainable and minimal.

**Stop condition — HARD STOP.** Return the STATUS handoff when §8 and the ADR
record are written. **Do not implement. Do not route to Developer.** The human
approval gate is mandatory: the human will explicitly review and
approve/reject/amend the proposed composition, and no implementation is authorized
until they do.
