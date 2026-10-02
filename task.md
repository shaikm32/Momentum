# CURRENT TASK

> Active task state only. TASK-015/016/017 records are archived in `task_archive/`;
> their approved decisions remain canonical, append-only, in
> `docs/architecture/decisions/DECISIONS.md` (ADR-47 / ADR-48 + Amendment Notes).

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: **TASK-018**
- Title: Implement the approved Stitch Aurora Home screen — now: **Device Finding
  #1, correct the Home background composition**
- Status: **COMPLETE (2026-10-02).** The approved Home-scoped §8.20
  remediation is implemented, the Developer suite is green (42 suites / 293
  tests), the bounded ISSUE-020 re-measurement passes on the changed bands in
  both themes, and **device verification passed in Light and Dark with no
  blocking findings**. The two non-blocking cosmetic documentation findings are
  resolved. The frozen background (§4.9) is untouched; `Tokens.kt`/`Color.kt`
  are unchanged; all non-Home default/`null` paths are byte-identical.
  **Nothing is committed; HEAD remains `47ebfb4`.**
- Priority: High
- Created: 2026-10-01
- Last Updated: 2026-10-02
- Classification: **IMPLEMENTATION correction** — Home-scoped foreground
  contrast remediation on the frozen background. No redesign, no background
  change, no global shared-component behavior change.
- Routing: **Orchestrator → Architect → HUMAN APPROVAL → Developer → Reviewer →
  device verification → cosmetic cleanup** — **closed**. Do not start TASK-019.
- Supersedes: nothing yet. TASK-017's Aurora/Liquid-Glass architecture is the
  foundation and its decisions stand unless this plan amends them by an explicit,
  documented ruling.

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Produce a concrete architecture/implementation plan that lets the Developer
faithfully re-implement the Momentum Home screen from the approved Google Stitch
design, **without the Developer making any new visual or design decision**, and
without redesigning, simplifying or "improving" that design.

## 3. SCOPE

<!-- ORCHESTRATOR ONLY -->

**In scope (Architect):** current Home architecture inventory; retain / adapt /
replace / delete decisions per Home component; exact Compose hierarchy; fixed vs
scrollable regions; artwork + Aurora layering; glass/material mapping; the
horizontal progress-card composition; Pending section; Completed section; FAB;
bottom navigation; animation translation; Light/Dark strategy; typography token
mapping; accessibility; performance; test changes; device-verification plan;
documented conflicts; and the decisions that need explicit human approval.

**Out of scope:** writing or editing application code; redesigning the Stitch
screen; new artwork or generated artwork; parallax or scroll choreography;
redesigning Habits / Tasks / Insights / Settings / onboarding; navigation
architecture changes; domain / database / schema / repository / use-case changes;
dependency or build changes; commit / push (Human-in-the-Loop).

## 4. REQUIREMENTS

<!-- ORCHESTRATOR ONLY -->

### 4.1 Binding posture

1. The Stitch design is **approved**. Do not redesign, reinterpret, simplify,
   substitute or improve it. Solve implementation problems in engineering, never
   by changing the design.
2. `screen.png` is the **primary** visual source of truth; `code.html` secondary;
   `DESIGN.md` supporting; `aurora_project_prd_brief.txt` supporting.
   **Conflicts must be documented, never silently resolved.**
3. Stitch HTML/Tailwind is a **visual and behavioral reference only** — translate
   into Kotlin/Compose/Momentum tokens/native animation APIs.
4. Momentum remains authoritative for **functionality**: domain models, real
   Habit/Task data, completion state, scheduling, repositories, use cases,
   ViewModels, persistence, navigation, Settings behavior, existing artwork,
   architecture and accessibility infrastructure. **No fake/mock domain data.**
5. The Stitch sample values (`3 of 5`, `60%`, `Morning Meditation`, …) are visual
   reference only. All displayed values come from real Momentum state.

### 4.2 Screen regions to reproduce (human brief §5)

| Region | Requirement |
|---|---|
| **A. Aurora / artwork environment** | Existing Momentum artwork only. Clearly visible, recognizable, **fixed** behind content, blended into the lower Aurora/page background, atmospheric — not an opaque banner. Must **not** scroll with Today content. No new/generated artwork, no procedural-gradient replacement, no parallax, no scroll movement, no collapse/expand. |
| **B. Fixed top chrome** | Date capsule + Greeting + supporting greeting message + Settings button all **fixed**. Scrollable content starts below it and **must never overlap** it. Chrome stays visually integrated with the Aurora environment. |
| **C. Daily Momentum card** | `X of Y items done` + encouraging message + circular percentage ring with the percentage inside + Completed count + Pending count + translucent glass + subtle Aurora/glow treatment. **Composition is horizontal** (text block left, ring right, breakdown row beneath a divider). The TASK-017 vertical "ring above the count" layout is **rejected**. Ring + textual metric read as one cohesive unit. |
| **D. Pending section** | `Pending` heading + contextual `Scheduled Today` label + real pending activity cards with icon, title, secondary metric, completion control, glass treatment. |
| **E. Completed section** | `Completed` heading + `Finished` status indicator + real completed cards with subdued typography, strikethrough, emerald completion indicator. |
| **F. FAB** | Fixed above the bottom navigation. Compact circular **glass** appearance, Aurora/glass lighting, subtle glow, press feedback, scale/rotation behavior. Quick Add business behavior unchanged. |
| **G. Bottom navigation** | Fixed. Floating **glass** shell, four destinations, active Home state, inactive states, iconography, labels, spacing, translucency, elevation/depth. Navigation behavior and architecture unchanged. |

### 4.3 Motion (human brief §6)

Translate only the motion that exists in `code.html`: subtle Aurora movement,
Aurora glow/pulse, progress-ring fill, activity icon micro-pulse where applicable,
completion check pop, card press compression, FAB glow/pulse, FAB interaction
rotation/scale, staggered content entrance. **No invented animation, no excessive
motion.** Reuse existing Momentum animation infrastructure where equivalent.
Momentum's reduced-motion behavior must be respected.

### 4.4 Themes, type, material

- **Light/Dark:** the supplied reference is a **light** Aurora/glass composition.
  `DESIGN.md`'s dark palette must **not** be imported blindly where it conflicts
  with the actual Home reference. Same composition and hierarchy in both themes
  via theme-aware tokens — no separate Dark redesign.
- **Typography:** inspect Momentum's typography system first. Prefer an existing
  token; where none exists, name the exact new token required. **No arbitrary
  size substitution.**
- **Glass/material:** express the Stitch glass through Momentum's existing Liquid
  Glass infrastructure. **No second glass system.** Where the existing system
  cannot express a treatment: identify the limitation, specify the smallest
  extension, document it. Aurora stays the atmosphere; Liquid Glass stays the
  foreground material.

### 4.5 Explicitly rejected (human brief §2)

Redesign · alternative layout · "improving" the design · simplifying for ease ·
substituting convenient existing components · new visual treatments · returning to
previous Home designs · restoring the vertical progress/orb composition ·
parallax · scroll choreography · new or generated artwork · replacing Momentum's
artwork.

### 4.6 Non-negotiable product constraints (`AGENTS.md`)

Offline-only · no backend/network/auth/tracking · no `INTERNET` permission · Room +
DataStore persistence · no Hilt/Koin · no new dependency without justification ·
existing artwork never replaced · accessibility never traded for composition.

### 4.7 HUMAN RULING — TASK-018 APPROVED (2026-10-01, binding)

The plan in §8 is **APPROVED FOR IMPLEMENTATION**, subject to the rulings below.
These are human requirements and outrank §8 wherever they differ.

**Approved as planned:** width-proportional `ArtworkWindow` (supersedes TASK-017's
fixed 184 dp) · fixed top chrome · the horizontal Stitch progress card with the
percentage inside the ring and **"items done"** (never "habits done") · Pending and
Completed sections · the existing token decisions · the Light/Dark strategy · the
retained TASK-017 architecture (fixed Aurora, fixed chrome, layout-produced scroll
boundary, one scroll container, no scroll observation).

**Human rulings that differ from or extend §8:**

1. **§10 — BOTTOM NAV IS AN EXPLICIT OVERRIDE OF C10.** The Stitch nav visual is
   authoritative: pill shape, dimensions, width, height, horizontal positioning,
   corner radius, background/translucency, backdrop/glass treatment,
   border/highlight, elevation/shadow, internal spacing, icon sizes, label
   typography, the active Home capsule, inactive appearance, bottom positioning
   and safe-area relationship. **"Do NOT preserve the current Momentum
   bottom-navigation styling merely because it is shared."** §8.10's refusal and
   §8.12 C10 are **withdrawn**.
   **Constraint:** `VISUAL = Stitch`, `BEHAVIOR = Momentum`. The four destinations
   (Home / Habits / Tasks / Insights), all routing and all navigation behaviour are
   unchanged. Call sites must be inspected before modification; refactor the
   shared visual implementation cleanly; **a Home-only nav fork is forbidden
   unless necessity is demonstrated.** Shared-component impact on the other three
   destinations must be identified and verified (§22).
2. **§11 — MOTION IS EXPANDED; §8.11's C11 REJECTION IS WITHDRAWN.** Implement the
   Stitch motion including **Aurora subtle movement, Aurora glow/pulse, FAB
   glow/pulse and FAB interaction scale/rotation**, using `code.html`'s concrete
   values. This **reverses §8.11's rejection** of `aurora-glow-pulse` (6 s),
   `fab-glow` (3 s) and `bolt-pulse`, which rested on ADR-48 §6 and on the
   currently-green `HomeSectionMotionTest.Home runs no permanent infinite
   animation` (which asserts `rememberInfiniteTransition` is absent from Home) and
   `HomeAccessibilityTest`'s equivalent assertion. Reduced-motion behaviour remains
   mandatory (§19), so each new loop must be nulled under reduce-motion, and the
   affected assertions must be corrected deliberately rather than deleted.
   Still bounded: no invented animation, no excessive motion.
3. **§7 — STREAKS ARE EXPLICITLY REMOVED** and this **confirms** §8.12 C2. No
   streak chips/badges, no streak fields, **no domain / persistence / repository /
   ViewModel changes** to support them, no fake streak values, no streak
   architecture. Streaks are deferred to a separate future task.
4. **§8 — CARD SECONDARY INFORMATION** confirms §8.12 C3: use only what the
   existing domain actually provides. **Do not invent data** to reproduce `15 mins`
   / `45 mins` / `500ml` / `3 entries`, and **do not expand the domain model** for
   TASK-018. Cards stay clean without streak metadata.
5. **§12 / §13 / §14** confirm §8.12: existing Aurora colour tokens reused, new
   tokens only as approved in §8.12/§8.17, no arbitrary one-off colours, no
   arbitrary typography changes, no second glass system — smallest documented
   extension only.
6. **§19 / §21** confirm §8.6: **ISSUE-020 must be re-measured from scratch** after
   the artwork geometry changes, using the canonical region-sample method with the
   ≥ 4.5:1 floor unchanged. Only the scrim **peak** may move, inside
   `[0.15, 0.35]`.
7. **§16** confirms §8.2's retain/replace split: keep the sound TASK-017
   architecture, and **cleanly replace** obsolete Home-specific composition rather
   than accumulating patches on rejected designs.
8. **§24 — workflow.** Architect plan → Developer → Reviewer. **No further human
   approval is required before the Developer begins** unless a genuinely new
   architectural conflict or scope change is discovered, in which case it is
   escalated through the Orchestrator per §23.2.

**Two consequences requiring an Architect amendment pass before implementation**
(both are reversals of approved decisions that carry ADR and test consequences, so
they are not the Developer's to resolve):

- **A1 — the nav override** (ruling 1) reverses §8.10/§8.12 C10 and changes a
  component shared by all four destinations. Needs an exact Compose specification
  translated from `code.html`'s nav values, a decision on how the shared component
  is refactored without a Home-only fork, the ADR amendment, and the identified
  impact on Habits / Tasks / Insights.
- **A2 — the motion expansion** (ruling 2) reverses §8.11's C11 and **ADR-48 §6**,
  and invalidates two currently-green test assertions. Needs the exact Compose
  expression for each approved loop, its reduce-motion nulling, the amplitude/battery
  cost statement, the ADR-48 amendment, and the deliberate correction of the two
  assertions.

Both are **specification-only**. The Architect already holds the full analysis; the
amendment pass resolves only the interaction between the approved plan and these two
human reversals. **Everything else in §8 stands as approved and must not be
re-opened.**

## 4.8 HUMAN RULING — DEVICE FINDING #1: CORRECT HOME BACKGROUND COMPOSITION (2026-10-01, binding)

The human's supplied reference screenshot is **the authoritative composition** for
the Home background. It supersedes §4.7/§8.6 wherever they differ.

**Target composition (one continuous fixed layer):**

```
COMPLETE AURORA ARTWORK      <- full intrinsic composition, top-aligned
        ↓
NATURAL LIGHT-LAVENDER CONTINUATION
        ↓
LARGE QUIET NEGATIVE SPACE   <- intentional; FAB + bottom nav sit in it
```

**Required implementation:**

1. Render the **complete** existing artwork (`habits_hero_light`/`habits_hero_dark`,
   1024 × 1536) — waves, glow, sparkles and stars all visible, nothing important
   cropped away.
2. **Preserve the intrinsic aspect ratio**: top-align it, scale proportionally to
   the screen width; the resulting height (= width × 1536/1024 = 1.5 × width)
   determines where the artwork portion ends.
3. The background beneath the artwork uses the same subtle lavender atmosphere as
   the reference; the transition must be visually continuous and quiet.
4. The whole background is **one fixed composition**; the foreground Home UI is
   layered above it; the background does not move when Today content scrolls.

**Forbidden (explicit):** `ContentScale.Crop` · object-cover semantics · an
`ArtworkWindow` band crop · vertical/horizontal cropping of the Aurora · a scrim
that obscures the Aurora · a dissolve that removes the glow/sparkle region ·
replacing the artwork with a procedural gradient · a separate arbitrary background
rectangle disconnected from the artwork · nested-scroll choreography · parallax ·
collapse · scroll observation · per-frame background movement · any foreground UI
change (date capsule, greeting, support line, Settings, progress card, Pending,
Completed, FAB, Stitch bottom nav all unchanged).

**Superseded:** `ArtworkBandFraction = 460/640` and every derived
`ArtworkWindow` value (§8.6) — previously approved, now explicitly withdrawn by
the human: "Do not preserve it merely because it was previously approved."
BOUND-1's band arithmetic re-derives from the intrinsic ratio (it grows, so the
inequality holds with more margin).

**Not the goal:** "complete artwork" ≠ artwork filling the phone height. The
reference shows the opposite — artwork in the upper region, large lavender
negative space below. Exact vertical proportions follow the reference, not
TASK-017/TASK-018 band values.

**Light / Dark:** same spatial composition in both themes (complete Aurora,
corresponding dark atmospheric continuation, same negative-space structure). Do
not simply invert the artwork.

**Contrast:** the artwork geometry changes, so foreground chrome must be
re-evaluated against the final rendered artwork. Do not reuse obsolete TASK-017
measurements; do not invent measurements; report any contrast issue separately
rather than silently changing unrelated UI.

**Preserve:** fixed background layer · fixed Home chrome · layout-produced scroll
boundary · one LazyColumn · Today content scrolling independently · existing
Momentum architecture · existing artwork asset.

**Verification:**
- Developer: implement **only** the background correction; run unit tests with
  `--rerun-tasks`; `assembleDebug`; `assembleDebugAndroidTest`.
- Reviewer: compare against the supplied reference and explicitly verify
  (1) complete Aurora visible, (2) glow/sparkles visible, (3) no crop,
  (4) no object-cover behavior remains, (5) artwork fixed during scroll,
  (6) lavender continuation continuous with the artwork, (7) lower negative space
  matches the reference, (8) Light and Dark preserve the same spatial composition,
  (9) no unrelated Home UI changed.
- **The finding must NOT be closed on source inspection alone** — the final
  acceptance criterion is the human's on-device rendered result.

**Stop after implementation + review. Do not start TASK-019. Do not redesign any
other part of Home.**

## 4.9 HUMAN RULING — DEVICE FINDING #1 VERIFIED PASS; BACKGROUND FROZEN (2026-10-01, binding)

The human **personally verified the corrected background on-device in both
Light and Dark** and recorded Finding #1 as a visual **PASS**:

- complete Aurora artwork visible
- Aurora waves / glow / sparkles visible
- no unwanted crop
- artwork composition matches the supplied reference
- lower lavender / dark atmospheric field is continuous
- background composition is fixed (does not scroll)
- Light and Dark both preserve the intended composition

**This device verification is the acceptance evidence for Finding #1.** The
accepted background implementation is the **frozen baseline** for all remaining
TASK-018 work. **DO NOT MODIFY**, in any following pass:

- `MomentumHeroArtwork` geometry / the intrinsic-ratio rendering
- `ContentScale.Fit` on Home's path
- the background continuation (aurora field / lavender)
- the scrim or either dissolve
- the artwork asset
- any background-related layout

**Closeout scope (the ONLY remaining TASK-018 work):**

1. **ISSUE-020 contrast re-measurement** on the final, current composition —
   the artwork geometry changed after the original measurements, so all
   previous TASK-017/TASK-018 figures are **obsolete** and none may be
   inherited. Established rule unchanged: minimum **region-sample** ratio
   **≥ 4.5:1**, floor not weakened, worst-case composited region measured.
   Required bands at minimum: home chrome (date capsule, greeting, greeting
   support line); progress card — **all** text bands including the three
   `c.textPrimary` bands (`items done` suffix, ring `done` unit, `n Pending`);
   completed rows (row title + row metadata); bottom nav (active capsule at
   relevant pulse extremes, plus any text/icon band whose obligation is
   affected by the final artwork); plus every band the existing ISSUE-020
   sweep/method requires. Measurement must reflect the §4.8 geometry:
   complete artwork, intrinsic ratio, top-aligned, width-scaled, Fit, 88 dp
   dissolve, unchanged four-stop scrim over the full content area.
   - **If a region FAILS:** do **not** change its color; do **not** change the
     artwork/scrim to make it pass. Report the exact region, current
     expression/token, worst-case ratio, and geometry; **stop and return for a
     human ruling.**
   - **If all regions PASS:** close ISSUE-020 via the established append-only
     documentation process.
2. **Documentation:** append the outstanding `DECISIONS.md` note superseding
   ADR-47 AN7 §2's obsolete band ratio. Append-only; no deletion or rewrite of
   previous amendments; historical measurements explicitly distinguished from
   the final geometry.
3. **Independent Reviewer verification** of (A) Finding #1 recorded PASS and
   background untouched during closeout, (B) measurements on the new geometry
   with the 4.5:1 floor enforced, all required regions, no inherited figures,
   failures surfaced not silently changed, (C) `DECISIONS.md` append-only with
   historical amendments intact and final state unambiguous.

Automated verification (`testDebugUnitTest --rerun-tasks`, `assembleDebug`,
`assembleDebugAndroidTest`) runs after any code change required for measurement
or supporting tests. Tests are never weakened or deleted.

**Stop conditions:** ISSUE-020 passes → report verification/documentation debt
closed, start nothing else, do not touch the background. ISSUE-020 fails →
stop with the exact failing region and measurements, fix nothing, return for
human ruling.

## 4.10 HUMAN RULING — ISSUE-020 CONTRAST REMEDIATION (2026-10-02, binding)

ISSUE-020 is confirmed **OPEN/FAIL** after bounded independent verification.
The human ruling below is binding and outranks any contrary reading of §8/§9.

**Rulings:**

1. **Do NOT accept sub-floor contrast values.** No band may ship below its floor.
2. **Do NOT modify the accepted Aurora/background implementation at this stage.**
   §4.9's freeze stands in full (see the frozen list below).
3. **Remediate failing foreground/UI elements using targeted local contrast
   corrections.**
4. **Do NOT blanket-replace colors with `c.textPrimary`.**
5. **Preserve the approved Stitch visual hierarchy** as far as the contrast
   requirement permits.
6. **The scrim may NOT be changed as a first-line fix.** It is a last resort only,
   if and only if targeted foreground treatment cannot satisfy the requirements.
7. **Do not weaken the established thresholds:**
   - text: **≥ 4.5:1**
   - applicable non-text graphical UI: **≥ 3.0:1**

**Authoritative final measurements — governing table is §10.11.**

Text failures (Light / Dark):

| Band | Light | Dark |
|---|---|---|
| Pending row title + metadata | 4.59 | 1.98 |
| Completed-row title | 4.33 | 2.03 |
| Empty-state body | 3.65 | 2.77 |
| Scheduled Today metadata | 3.24 | 2.45 |
| Ring percentage | 2.83 | 1.81 |
| Nav inactive label + icon | 4.92 | 3.34 |
| Progress-card `textPrimary` bands | 8.51 | 4.52 rest / **3.99 scrolled worst case** |

**Passing regions must not be changed merely for consistency.**

Non-text failures: ring arc 1.00 both · `textMuted` dot 1.20 Dark · ring-track
boundary 1.24 Dark · success dot 2.23 Light · empty-ring boundary 1.68 Light /
1.24 Dark · check glyph on emerald 3.82 Light · verified glyph 2.34 Light ·
emerald fill boundary 2.13 Light / 2.54 Dark · capsule boundary 1.11 Light.

The bounded verification corrected four methodological defects from the original
sweep (R/B artwork channel swap · unattainable `scrim:0` pole · unreachable
scroll positions · missing 1 dp capsule border), so **§10.11 is authoritative**
and the original 23/26 figure is superseded.

**FROZEN BACKGROUND — not modifiable during the first remediation pass:**
`MomentumHeroArtwork` · intrinsic-ratio geometry · `ContentScale.Fit` · the
artwork asset · background continuation · scrim/dissolve · background layout.

**Required Architect step (before any Developer code change):** a concrete
remediation plan. For **every** failing region the Architect must determine:

- **A.** Is it text, essential graphical information, state indicator, or
  decorative boundary?
- **B.** What current ink/surface expression produces the failure?
- **C.** Can an existing semantic token solve it?
- **D.** If not, is a narrowly scoped semantic high-contrast token justified?
- **E.** What is the minimum visual deviation from Stitch?
- **F.** What exact target ratio will be verified?

**Special attention:**
- Ring percentage is important content and must meet the **4.5:1** text floor.
- Check/verified glyphs are meaningful **state indicators**, not decoration.
- Decorative boundaries must not be modified blindly.
- No global palette change where a local treatment suffices.
- Do not blanket-convert all secondary text to `textPrimary`.

**Scrim rule.** Do not propose changing the scrim merely because it is
mathematically available. A scrim adjustment inside `[0.15, 0.35]` may be
considered **only if** (a) targeted foreground treatment cannot satisfy the
required contrast, (b) the Architect demonstrates why, and (c) the visual impact
is explicitly assessed. The accepted Aurora composition remains the baseline.

**Workflow:** Orchestrator → Architect → **human review/approval** → Developer →
Reviewer. The Developer must not skip the Architect planning step. **Stop after
the Architect produces the remediation plan and return it for human approval.**

**Goal:** accessible foreground + preserved approved Aurora composition +
minimum visual deviation — **not** a green contrast table by any means necessary.

Do not start TASK-019. Do not modify unrelated Home components.

## 4.11 HUMAN RULING — §8.20 APPROVED IN DIRECTION, REVISED PLAN REQUIRED (2026-10-02, binding)

§8.20 is **NOT approved as written**. The remediation direction is approved
subject to the changes below. Recorded as **§4.11**; it outranks §8.20 wherever
they differ.

**Approved principles:**
- Preserve the accepted Aurora/background implementation completely: no change to
  artwork geometry, artwork asset, `ContentScale.Fit`, continuation,
  scrim/dissolve, or background layout. **The background stays FROZEN.**
- Do not accept sub-floor contrast for **meaningful text / state information**.
- Prefer targeted foreground/material corrections.
- No blanket `textSecondary` → `textPrimary` conversion.
- **No new token** unless the revised plan demonstrates that an existing
  semantic token cannot solve the problem.

**CHANGE 1 — E3 (global fill raises) REJECTED.** Do **not** implement
`heroFill 0.68 → 0.85`, `cardFill 0.55 → 0.90`, `controlFill 0.45 → 0.90` as
global `LiquidGlass` changes. Reason: unacceptable blast radius across Home,
Habits, Tasks, Insights and Settings, and it conflicts with the approved
principle that Aurora stays perceptible through/around the glass.
The first remediation pass must find a **HOME-SCOPED** solution.
A new optional parameter/variant on the shared `LiquidGlass` API is acceptable
**only if**: existing defaults remain unchanged; Habits/Tasks/Insights/Settings
rendering is unchanged; the parameter is narrowly scoped to the required Home
surfaces; and no brittle Home-only fork of the whole glass component is
introduced. The Architect must **explicitly prove** why the Home-scoped approach
is sufficient.

**CHANGE 2 — E1 must also be scoped.** The non-hue wash restructuring may
proceed only if it is (A) Home-scoped, **or** (B) demonstrably behaviorally
identical for all existing consumers. Do not alter shared `LiquidGlass`
behavior globally merely to solve Home.

**CHANGE 3 — E2 scoped.** `HueWashMidAlpha 0.5 → 0.8` is acceptable **only**
for the affected Home/progress-card path. Do not change unrelated screens.

**CHANGE 4 — targeted ink approved in principle.** `Scheduled Today` →
`c.textPrimary` · verified glyph → `c.textPrimary` · the two breakdown dots →
`c.textPrimary`. No blanket conversion. Developer/Reviewer must verify the
resulting contrast **and** visual hierarchy.

**CHANGE 5 — ring arc exception, conditional.** The ring arc may remain below
3.0:1 **only if the Reviewer confirms ALL five**:
1. the arc is decorative/non-essential, not the sole carrier of progress
   information;
2. the numerical progress percentage remains readable at **≥ 4.5:1**;
3. Completed/Pending information remains explicitly available as text;
4. no interaction or state meaning is communicated solely through the arc;
5. the Stitch visual treatment would be materially degraded by forcing the arc
   to a contrast-safe solid ink.

If **any** condition fails, the ring arc must be remediated. This is a narrow
exception for the ring arc only — **not** a general relaxation of the contrast
requirements.

**REQUIRED NEXT STEP — do NOT dispatch Developer.** Return to the Architect to
revise §8.20 into a **Home-scoped** remediation plan. The revised plan must show
for each affected region: current expression · proposed expression · Light
worst-case ratio · Dark worst-case ratio · **Home-only or shared** · expected
visual impact · **why a smaller/local change is insufficient**. It must also
identify exactly which existing screens remain byte/behaviorally unchanged.

**Routing:** Orchestrator → Architect revision → **HUMAN APPROVAL** →
Developer → Reviewer. Do not start TASK-019. Implement nothing until the
revised plan is approved.

## 5. QUESTIONS THE ARCHITECT MUST ANSWER

1. What is the current Home architecture after TASK-017, file by file?
2. Which Home components are **retained**, **adapted**, **replaced**, **deleted**?
   Specifically: is `HomeHero`/crown/collapse/hero nested-scroll already gone, and
   is any of it still alive? Is the vertical `HeroProgressLockup` to be removed
   outright? What obsolete Home-specific spacing/cropping workarounds remain?
3. What is the exact Compose hierarchy, with the fixed/scrollable boundary
   expressed structurally (not by scroll observation, not by collapse)?
4. How does the progress card map to real Momentum completion data, and what
   happens in the empty, partial and fully-complete states?
5. Which Stitch treatments does `LiquidGlass` express today, and which require the
   smallest documented extension?
6. Which typography tokens are reused, and which (if any) are new?
7. What is the Light/Dark mapping for every layer?
8. What motion is translated, from which `code.html` keyframes, and how is
   reduced motion nulled?
9. What accessibility guarantees hold (content descriptions, roles, 48 dp targets,
   font scaling, contrast, traversal order)?
10. What is the per-frame cost, and how is scroll-driven recomposition avoided?
11. Which existing tests must change, and which new tests are required?
12. **Which conflicts exist between the Stitch artifacts and Momentum, and which
    decisions require explicit human approval before implementation?**

## 6. RELEVANT FILES

<!-- ARCHITECT OWNER -->

**Modified — Home screen (`presentation/screens/today/`)**
`TodayScreen.kt` (four-child root order retained; chrome/scroll boundary/FAB/empty
centring retained; new item set + entrance clock + the section/card call sites) ·
`HomeAtmosphere.kt` (`artworkWindow(width)` from the artwork's intrinsic
`ArtworkIntrinsicRatio = 1536/1024`; the §8.6 `ArtworkBandFraction` is withdrawn
per §4.8/ADR-47 AN8; `animated = true`; scrim untouched) · `HomeChrome.kt` (date capsule pill, greeting
support line, type
ramp, `revealProgress`, `ChromeBottomInset` retained) ·
`HomeProgress.kt` (**now the progress seam only**: `HeroFigureState`,
`ProgressCardRingSize = 96.dp` and the retained `HomeEncouragementLine` toggle —
the vertical lockup and its two strings are deleted) ·
`TodayViewModel.kt` (**not touched by this pass** — no domain/VM change) ·
`TodayLoadingSkeleton.kt` lives in `TodayScreen.kt` (new silhouette).

**New — Home screen (`presentation/screens/today/`)**
`HomeCards.kt` — `HomeProgressCard`, `HomeSectionHeader`, `HomeActivityCard`,
the two `AuraBlob`s, `homeReveal` and `HomeProgressCardTag` (screen-local;
`ActivityCard.kt`, `MomentumComponents.kt`/`MomentumSectionHeader` and
`MomentumBadges.kt`/`EmptyState` are **not** touched).

**Modified — shared presentation**
`components/ProgressOrb.kt` (`arcBrush: Brush? = null`, one additive param) ·
`components/CompletionButton.kt` (`accent: CompletionAccent = THEME`,
`showIdleGlyph: Boolean = true`, one shared `CompletionAccent` enum) ·
`components/MomentumIcons.kt` (additive `verified` glyph) ·
`theme/Type.kt` (three new tokens: `MomentumHeadlineCompact` 24/32 600,
`MomentumHeadlineSection` 20/28 600, `MomentumLabelMicro` 11/14 600 @ 0.04em) ·
`theme/Motion.kt` (`enter = 900`, `fabGlow = 3000`, `verifiedPulse = 2200`) ·
`res/values/strings.xml`.

**Modified — by the A1/A2 amendments (§4.7 rulings 1 and 2; these were
read-only references in the §8 pass)**
`components/LiquidGlass.kt` (`LiquidGlassBottomBar` + `RowScope.LiquidGlassTab`
re-rendered to the `code.html` nav — §8.18) ·
`components/MomentumFab.kt` (two additive default-`false` params `glowPulse` /
`rotateGlyphOnPress` — §8.19) ·
`navigation/MomentumNavGraph.kt` (the `Scaffold.bottomBar` slot's three padding
values only — §8.18; navigation behaviour untouched) ·
`aurora/MomentumAurora.kt` (**not modified** — read only to prove its own
reduced-motion gate, §8.19 M1).

**Modified — by the ISSUE-020 contrast ruling (ADR-49 AN2 / §10.1)**
`HomeCards.kt` **only**: `HomeEncouragementLine`'s colour becomes an unconditional
`c.textSecondary`, and `HomeSectionHeader`'s label colour stops deriving from
`accent` while `accent = c.success` keeps its **glyph-only** role (so ADR-48 AN6 M4's
`bolt-pulse` retains its consumer). `TodayScreen.kt` keeps `accent = c.success`.
`HomeAtmosphereAssetsTest` gains the re-pointed glyph assertion. No other file
changes; no scrim peak moves; no token is added.

**Read-only references (verified this pass, not modified)**
`presentation/screens/habits/HabitsHero.kt`, `HabitsScreen.kt`,
`MomentumHeroArtwork.kt`, `components/MomentumComponents.kt`,
`MomentumBadges.kt`, `ActivityCard.kt`, `components/ProgressOrb.kt`'s callers,
`HabitIconTile.kt`, `aurora/MomentumAurora.kt`, `components/AppBackground.kt`,
`theme/{Tokens,Color,Shapes,Theme}.kt`,
`presentation/{ReduceMotion,UiText}.kt`, `domain/model/Activity.kt`,
`domain/usecase/UseCases.kt`.

**Tests**
`src/test/.../screens/today/`: `HomeAtmosphereAssetsTest` (+`HomeScrollSurfaceTest`),
`HomeAccessibilityTest`, `HomeSectionMotionTest`, `QuickAddTest` — all rewritten
to the retained outcomes, **plus the deliberate A1/A2 corrections in §8.18.6 /
§8.19.5, plus the ADR-49 AN2 re-pointed glyph assertion** (corrected, not deleted).
`src/test/.../presentation/components/BottomNavStitchVisualTest.kt` — **new**
(A1, §8.18.6).
`src/androidTest/.../screens/today/`: `HomeCompositionTest`,
`HomeProgressStateTest`, `HomeStatesTest` — rewritten/extended;
`src/androidTest/.../presentation/components/BottomNavLayoutTest.kt` — **new**
(the nav slot's margins, every tab's ≥ 48 dp target on both axes, and the
`heightIn(min =)` grow-not-clip behaviour; bounds only, §8.18.6 item 2).
Not modified: `domain/usecase/TodaySchedulingTest`, `TodaySectionsTest`,
`TargetEntryDialogSurfaceTest`.

**ISSUE-020 closeout verification artifacts (Architect pass — no repository file
created or modified for the measurement)**
The verifier and its inputs live outside the repo at
`%LOCALAPPDATA%\Temp\opencode\issue020-remeasure\`: `measure020.js` (the single
measurement script), `decode.ps1` + `habits_hero_light.bgrx` /
`habits_hero_dark.bgrx` (the bundled WebP decoded to opaque 1024 × 1536 BGRX;
alpha verified 255 on every pixel) + `artwork-meta.json`, and the earlier
`sweep.js` (glass-stop reference). Sources re-read as ground truth this pass,
read-only: `HomeAtmosphere.kt`, `MomentumHeroArtwork.kt`, `HomeChrome.kt`,
`HomeCards.kt`, `LiquidGlass.kt`, `CompletionButton.kt`, `MomentumBadges.kt`,
`MomentumNavGraph.kt`, `AppBackground.kt`, `MomentumAurora.kt`,
`theme/Color.kt`, `theme/Type.kt`. `git status` shows only the pre-existing
TASK-017/018 working-tree changes (HEAD `47ebfb4`); the measurement added none.

## 7. ENVIRONMENT FACTS

<!-- ORCHESTRATOR ONLY -->

- **Reference artifacts** are staged (read-only copies) at
  `C:\Users\shaik\AppData\Local\Temp\opencode\task018-stitch\`:
  `screen.png` (640 × 1600, 628 068 bytes, SHA-256
  `F34C294FD62C53AA9316E5E20A1D33414B289D3A17D077D3BFC1657D71B89C09`),
  `code.html`, `DESIGN.md`, `aurora_project_prd_brief.txt`.
  Originals live in `C:\Users\shaik\Downloads\`.
  **Use the staged copies** — they are inside the agents' allowed external path.
- **Confirmed `screen.png` content** (640 × 1600): light lavender Aurora page;
  date capsule `THU, OCT 1`; `Good Morning` + `Ready to conquer your day?`;
  Settings gear top-right; glass progress card reading `3 of 5 habits done` /
  `Great momentum today! Keep it flowing.` / ring `60%` `done` / `3 Completed` ·
  `2 Pending`; `Pending` + `Scheduled Today` with two cards
  (`Morning Meditation` 7d / `15 mins`; `Deep Work Sprint` 12d / `45 mins`) each
  with a gradient icon tile, a **streak chip**, and a ring completion control;
  `Completed` + `Finished` with three cards (`Hydrate & Lemon Water` 500ml,
  `Morning Sunlight Walk` 20 mins, `Gratitude Journal` 3 entries) with strikethrough
  titles and emerald filled check badges; circular glass `+` FAB; floating
  four-destination glass bottom nav with Home active.
- **`adb` unavailable** (re-confirmed 2026-10-01). No emulator/device run is
  possible here. Runnable validation = `:app:testDebugUnitTest` +
  `:app:assembleDebug` (+ `:app:assembleDebugAndroidTest` compiles). **On-device
  and instrumented verification is the human's** and must never be claimed by an
  agent.
- **Working tree:** TASK-015, TASK-016 and TASK-017 implementations are present and
  **uncommitted**; all must be preserved. `.opencode/**`, `opencode.json`,
  `ISSUES.md`, `PROJECT_STATUS.md`, `task_archive/` carry further pre-existing
  changes — preserve all. `task.md` (TASK-017 record) was copied to
  `task_archive/TASK-017.md` by the Orchestrator for this task.
- **Fonts:** `app/src/main/res/font` contains only `manrope.ttf`.
- **Navigation:** `MomentumNavGraph.kt` already declares exactly four top-level
  destinations (Today/Home, Habits, Tasks, Insights) with rounded-filled and
  outlined icon pairs and a `LiquidGlassBottomBar`.

**Orchestrator verification of the plan's load-bearing factual claims** (each read
from source after the Architect returned; all confirmed, with two count corrections
that *strengthen* rather than alter the plan's conclusions):

| Claim in §8 | Verified value | Effect |
|---|---|---|
| `GlassLevel` = HERO 0.68 / DIALOG 1.0 / CARD 0.55 / CONTROL 0.45; `borderDark 0.16`, `borderLight 0.10`, `highlight 0.10`, `heroElevation 12.dp` | `Tokens.kt:85-126` — exact match | none |
| `ProgressOrb` stroke `size × 0.075`, track `textPrimary @ 0.55` | `ProgressOrb.kt:108,116,221` — exact match | none |
| Spacing `micro 4 / tight 8 / compact 12 / standard 16 / screen 20 / section 24 / majorSection 32`; `item=compact`, `group=tight`, `card=standard`, `row=compact` | `Tokens.kt:13-38` — exact match | none |
| Radii `control 12` (= Stitch's 12 dp card radius), `card`→`surface 16` (= Stitch's `rounded-2xl`) | `Shapes.kt:12-50` — exact match | none |
| Manrope-only scale Display 40/48 600 (`MomentumFigureDisplay` + `tnum`), Headline 28/34 600, Title 20/26 600, Body 16/24, BodySmall 14/20, Label 13/18 500, Caption 12/16 500; `MomentumTypography` maps `bodyMedium`/`titleSmall`→BodySmall, `labelMedium`→Caption | `Type.kt:36-153` — exact match | none |
| Motion has `fast/standard/emphasis/ambient/reward/easeOut` and **no** `enter` | `Motion.kt:16-45` — confirmed, so `enter = 900` is genuinely new | none |
| `c.auroraBlue / auroraViolet / auroraMint / auroraLavender` already exist | `Color.kt` — confirmed, so the ring gradient and `hue` need **no** new colour token | none |
| `habitIconAccent` (`Color.kt:194`), `momentumIcon` (`MomentumIcons.kt:79`), `glassPress` (`LiquidGlass.kt:142`), `completionActionLabel` (`ActivityCard.kt:219`), `homeMetadataLabel` (`TodayScreen.kt:660`), `todayBranch` (`TodayScreen.kt:173`), `rememberReduceMotion` (`ReduceMotion.kt:10`), `MomentumSectionHeader` (`MomentumComponents.kt:97`), `EmptyState` (`MomentumBadges.kt:204`) all exist | all confirmed | none |
| `Activity` has **no** duration field and **no** entry-count field | `Activity.kt` — only `dueTime: String?`, `targetPerOccurrence: Double`, `unit: String` | confirms **C3**: the Stitch secondary line (`15 mins` / `3 entries`) cannot be sourced without a schema change, which §4.6 forbids |
| `HomeProgressLockup` is currently **vertical** | `HomeProgress.kt:94-172` (`Fraction`/`Complete`/`Hidden`, `HeroRingSize`, `MomentumFigureDisplay` at :39/:74/:147) | confirms §8.2's "replace" of the vertical composition |
| `MomentumFigureDisplay` is used **only** by `HomeProgress.kt` | confirmed (3 uses, all Home) | confirms §8.12 C14 — the token goes unused but stays declared |
| `MomentumSectionHeader` has **13** call sites (§8.2, §8.8) | **29** call sites across 11 files (Settings 5, HabitConfig 5, ActivityDetail 4, TaskConfig 3, CreationFields 3, Insights 3, Today 2, Activities 1, Calendar 1, CreateItemSheet 1, HabitDetail 1) | **Correction, no plan change** — the real count strengthens §8.8's screen-local decision; §8.8's "would redesign Tasks, Calendar, Insights, Settings, detail and creation screens" is accurate |
| `.liquidGlass(` has **4** existing callers (§8.5) | **16** call sites | **Correction, no plan change** — the default-`null` byte-identical guarantee applies to 16 sites, which makes R3 (the shared-modifier review obligation) more important, not less |
| `ActivityCard` has 3 call sites | Today, Activities, Calendar (+ 2 previews) — confirmed | none |

## 8. ARCHITECTURAL DECISIONS AND PLAN

<!-- ARCHITECT OWNER -->

Canonical ADRs for this pass: **ADR-47 Amendment Note 7**, **ADR-48 Amendment
Note 5**, **ADR-49** (all appended below `ADR-47 Amendment Note 6`).

### 8.1 Current Home architecture (file by file, after TASK-017)

| File | Role today |
|---|---|
`TodayScreen.kt` | `TodayRoute` (effects, `AnimatedContent` branch crossfade, target dialog) + `TodayScreen` = one `BoxWithConstraints` with **four declared children**: `HomeAtmosphere`, `HomeTopChrome` (`.align(TopStart)`), one `Box(padding(top = chromeHeight))` holding the `LazyColumn` + `MomentumFab` (`.align(BottomEnd)`), plus `TodayLoadingSkeleton`, `TodayError`, `heroFigure()`, `homeMetadataLabel()`, `EmptyDayBottomReserve`, `HomeEmptyState`, `HomeActivityRow`, `HomeScrollRegionTag` |
`HomeAtmosphere.kt` | `HomeAtmosphere` = ambient `AuroraBackground` → `MomentumHeroArtwork` in `ArtworkWindow` (`184.dp`) top-aligned → `HeroScrim` (`drawWithCache`, four stops, `ScrimPeakLight 0.26f` / `ScrimPeakDark 0.22f`, `ScrimPeakStart 0.20f` / `ScrimPeakEnd 0.80f`); `ArtworkHandOff 88.dp`; tags `HomeAtmosphereTag` / `AtmosphereArtworkTag` |
`HomeChrome.kt` | `HomeTopChrome` = status-bar spacer + `section` gap + `Row`(date `labelLarge` / greeting `headlineMedium`, both `textPrimary`, greeting `heading()`; `LiquidGlassIconButton` Settings 48 dp) + `ChromeBottomInset`; reports height via `onSizeChanged`; tag `HomeChromeTag` |
`HomeProgress.kt` | `HeroFigureState` (`Fraction`/`Complete`/`Hidden`), `HeroProgressLockup` (**vertical**: 80 dp `ProgressOrb` with the value inside, `/ total` beneath, `completed today` caption beneath, then the `AnimatedVisibility` encouragement), one `clearAndSetSemantics` description, `HeroRingSize 80.dp`, tag `HomeProgressTag` |
`TodayViewModel.kt` | `TodayUiState.Success(date, dateLabel, greeting, sections, encouragement, reduceMotionOverride)`; `TodaySections` gives `remaining`/`completed`/`progress`/`doneCount`/`totalCount`/`isEmpty`/`allComplete`; `TodayItem(activity, completed, completionValue, currentStreak)`; `encouragementFor()` two forms; `TodayEffect.Celebrate` one-shot; date label `"EEE, MMM d".uppercased()` |
`LiquidGlass.kt` | `GlassLevel` `HERO 0.68` / `DIALOG 1.0` / `CARD 0.55` / `CONTROL 0.45`; `Modifier.liquidGlass(shape, level)` = shadow + clip + **vertical** 3-stop wash of `c.surface @ fillAlpha` + theme-resolved hairline; **no backdrop blur by design**; `glassPress` 0.97; `LiquidGlassIconButton`, `LiquidGlassBottomBar` (26 dp), `LiquidGlassTab` |
`ProgressOrb.kt` | one `Canvas`: track `c.textPrimary @ 0.55`, breathing halo, two orbiting sweep-gradient lobes, solid `c.primary` arc; stroke `size × 0.075`; optional `showPercent` |
`ActivityCard.kt` | shared row (3 call sites: Today, Activities, Calendar): 44 dp tile, `titleLarge`+SemiBold, optional `scheduleLabel` (`bodySmall`/`textSecondary`), optional `StreakBadge`, `CompletionButton`; `targetProgressLabel()` |
`CompletionButton.kt` | shared toggle: 48 dp, idle = translucent fill + hairline + faint check, done = `primary→secondary` gradient + white hairline + shadow; `Role.Checkbox`, spring pop, `LongPress` haptic |
`MomentumFab.kt` | 56 dp circle, `White@0.22 → primary@heroFill` wash, theme-resolved hairline, `MomentumGlass.heroElevation`, `MomentumIcons.add` @ `prominent` in `c.onPrimary`, `glassPress`; shared with `HabitsScreen` |
`HabitsHero.kt` | the "reference composition" for artwork+copy: bottom-anchored copy block inside a clipped artwork box, clamped-px collapse — **Home owns none of this** |
`MomentumNavGraph.kt` | Nav3 + `Scaffold` + `LiquidGlassBottomBar` + four `LiquidGlassTab`s (Home/Habits/Tasks/Insights, filled/outlined pairs); `drawsBehindStatusBar` for Today & Habits; navigation architecture — **out of scope** |
`theme/` | `MomentumSpacing` 4/8/12/16/20/24/32 (+ `item`=12, `group`=8, `card`=16, `row`=12) · `MomentumHeights` 52/48/48 · `MomentumIconSizes` 20/24/28 (+`tile`=22) · `MomentumGlass` fills 0.68/1.0/0.55/0.45, `borderDark 0.16`, `borderLight 0.10`, `highlight 0.10`, `heroElevation 12.dp` · `MomentumMotion` fast 180/standard 280/emphasis 450/ambient 6000/reward 650 + `easeOut` · `MomentumRadii` 8/12/16/20/28/pill · Manrope-only scale: Display 40/48 600 (+`MomentumFigureDisplay` `tnum`), Headline 28/34 600, Title 20/26 600, Body 16/24 400, BodySmall 14/20 400, Label 13/18 500, Caption 12/16 500 · `AuroraLight` `#F7F8FC` / `AuroraDark` `#080D18` |

### 8.2 Retain / adapt / replace / delete

**Retain unchanged (architectural invariants):** the four-child `Box` order
atmosphere → chrome → scroll region → FAB; the scroll boundary as **layout**
(`padding(top = chromeHeight)` from one `onSizeChanged`), never observation; one
`LazyColumn`, no nested scroller, `key` + `contentType`; `EmptyDayBottomReserve`
as both `contentPadding.bottom` and the empty centring subtraction;
`HeroScrim` verbatim (four stops, peaks, `drawWithCache`); `MomentumHeroArtwork`
call verbatim (`Alignment.BottomCenter`, `fillMaxSize()`, no `zoom`, no
`parallax`, no `ColorFilter`, no `bottomDissolve` override); `MomentumFab`
verbatim (material, size, glyph, label, role, press); `LiquidGlassBottomBar` /
`LiquidGlassTab` / `LiquidGlassIconButton` verbatim; `EmptyState` verbatim; the
`AuroraState` mapping and the one-shot `Celebrate` reward; the encouragement
toggle wiring; `ActivityCard.kt`, `MomentumComponents.kt`, `MomentumBadges.kt`,
`MomentumHeroArtwork.kt`, `MomentumAurora.kt`, `AppBackground.kt`,
`MomentumNavGraph.kt`, every theme file except `Type.kt` / `Motion.kt`, the
whole domain layer.

**Adapt:** `HomeAtmosphere` (window only) · `HomeChrome` (date capsule, support
line, type ramp) · `HomeProgress` (full recomposition, horizontal) ·
`ProgressOrb` (+`arcBrush`) · `CompletionButton` (+`accent`, +`showIdleGlyph`) ·
`TodayLoadingSkeleton` (new silhouette) · `TodayScreen` (item set, entrance
clock, card/section call sites) · `strings.xml`.

**Replace:** the progress lockup's **vertical** composition → the approved
**horizontal** Aurora card (the TASK-017 layout is rejected per §4.2 C).

**Delete:** nothing structural. `MomentumFigureDisplay` becomes **unused on
Home**; the token **stays declared** with a KDoc note (removing a public token is
out of scope). `HeroRingSize` is replaced by `ProgressCardRingSize` (see §8.7)
and `ArtworkWindow` is re-derived (§8.6).

**Home-specific spacing/cropping workarounds that survive:** only
`ArtworkHandOff = 88.dp` (the shared component's own value, restated as a
measurement — ADR-30 forbids duplicating it as a parameter) and
`ChromeBottomInset = MomentumSpacing.section` (a named alias). No
Home-specific dp literal survives anywhere else.

### 8.3 Exact Compose hierarchy and the fixed/scrollable boundary

```
TodayRoute                                     (unchanged: effects, branch crossfade, target dialog)
└ TodayScreen                                  BoxWithConstraints(Modifier.fillMaxSize())
   ├─ [1] HomeAtmosphere(state, reduceMotion, width)   .fillMaxSize().testTag(HomeAtmosphereTag)
   │      ├ HomeAurora            -> AuroraBackground(state, intensity, animated = true)   .fillMaxSize()   // §8.19 M1
   │      ├ Box(.fillMaxWidth().height(ArtworkWindow).align(TopStart))
   │      │     └ MomentumHeroArtwork(Alignment.BottomCenter, Modifier.fillMaxSize().testTag(AtmosphereArtworkTag))
   │      └ HeroScrim(Modifier.fillMaxSize())
   ├─ [2] HomeTopChrome(dateLabel, greeting, greetingSupport, onOpenSettings,
   │                    onHeightChanged, entranceProgress)  .align(TopStart).testTag(HomeChromeTag)
   ├─ [3+4] Box(Modifier.fillMaxSize().padding(top = chromeHeight))          <- THE BOUNDARY
   │      ├ LazyColumn(.fillMaxSize().clipToBounds().testTag(HomeScrollRegionTag),
   │      │            contentPadding = PaddingValues(top = 0.dp, bottom = EmptyDayBottomReserve),
   │      │            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight))
   │      │   ├ item { HomeProgressCard(...) }            // non-empty days only; Hidden branch composes nothing
   │      │   ├ item { HomeEmptyState() inside Box(heightIn(min = emptyRegion), Center) }   // empty days only
   │      │   ├ item { HomeSectionHeader(Pending, Scheduled Today) }   // if remaining.isNotEmpty()
   │      │   ├ items(remaining, key = id, contentType = "todayRow") { HomeActivityCard(...) }
   │      │   ├ item { HomeSectionHeader(Completed, Finished) }      // if finished.isNotEmpty()
   │      │   └ items(finished, key = id, contentType = "todayRow") { HomeActivityCard(...) }
   │      └ MomentumFab(onClick, cd_quick_add, .align(BottomEnd).padding(end = screen, bottom = section))
```

The boundary is expressed **structurally**: the chrome and the region are
geometrically disjoint siblings in one `Box`, the region's top edge *is* the
chrome's bottom edge, and `contentPadding.top` stays `0.dp`. There is no
`NestedScrollConnection`, no scroll read, no `derivedStateOf`, no parallax, no
sticky header. Overlap is unrepresentable. `BoxWithConstraints.maxWidth` is read
**once** per composition for §8.6 and is not scroll-dependent.

**§4.7 r2 delta:** `HomeAurora`'s `animated = true` (shown in the block above)
replaces the §8 pass's `animated = false`, per **§8.19 M1**. Nothing else in this
hierarchy changes.

### 8.4 Artwork + background layering

Four layers, unchanged in kind: **(1a)** ambient Aurora field,
**(1b)** the shared theme-selected artwork in `ArtworkWindow`, **(1c)** the one
full-content-area four-stop scrim, **(1d)** foreground. The artwork's datum is
the **physical window top** and it does **not** scroll, does not collapse and
does not move; the chrome floats over it; the scrim has no stop at the chrome's
bottom edge. The Stitch `h-[460px]` band at 2× = 230 dp of a 320 dp viewport, and
its fade mask `rgba(page,0) 0% → 0.15 50% → 0.75 80% → page 100%` is
**structurally identical** to Momentum's existing four-stop scrim, so **the scrim
is not changed at all** — its peaks, stops and `drawWithCache` stand verbatim.

### 8.5 Glass / material mapping

| Stitch treatment | Momentum expression | Change? |
|---|---|---|
`backdrop-filter: blur(20/24/16/14px)` | none | **No.** `LiquidGlass`'s own KDoc already ruled this out (minSdk 26, no reliable cross-device API). Blur is a documented non-adoption, not an extension. |
progress card `linear-gradient(135deg, White .6 → #f0e6ff .38 → #d7f5ff .48)` | `Modifier.liquidGlass(shape, GlassLevel.HERO, hue = c.auroraLavender)` — new **one** optional `hue: Color? = null`; when non-null the body wash becomes a 135° `Brush.linearGradient` `lerp(c.surface, hue, 0.55)@heroFill → …@0.5f → fill@1f`; the top streak, hairline and shadow are untouched; `null` ⇒ byte-identical to today for all 4 existing callers | **Yes — the smallest documented extension** |
pending card `rgba(255,255,255,.85)` · completed `rgba(255,255,255,.65)` | `GlassLevel.CARD` (0.55) · `GlassLevel.CONTROL` (0.45) | **No.** Stitch's alphas are lighter; raising them would make cards more opaque and break "the atmosphere must remain perceptible through and around them". Same *relational* structure (completed is quieter). Recorded deviation. |
icon tile 135° 3-hue gradient + `bg-white/20` | 135° 2-stop `White@0.20 → accent@0.42` + existing `accent@0.34` hairline + `White@0.20` inner wash, 48 dp, `MomentumRadii.card` | 2-stop, not 3 — see §8.12 C8b |
`rgba(115,103,170,.08) 0 4px 20px` card shadow | none (`CARD`/`CONTROL` are `Dp.Hairline`) | **No.** Adding elevation to `CARD` would change every card in the app. |
nav bar / Settings / bottom bar glass | `LiquidGlassBottomBar` / `LiquidGlassIconButton` / `MomentumFab` | **No** |

### 8.6 Artwork window — re-derived from the reference (amends ADR-47 AN4 §3 / AN5 §4) — **SUPERSEDED by §4.8 (Device Finding #1): the `460/640` band is withdrawn; the geometry is now the complete artwork's intrinsic aspect ratio**

```kotlin
ArtworkBandFraction = 460f / 640f                       // the reference's band / viewport-width ratio
ArtworkWindow        = ArtworkBandFraction * windowWidth // 230 dp @320 · 258.75 dp @360 · 296.1 dp @412
```

Derivation: `screen.png` is 640 × 1600 and `code.html` is authored in CSS px at
16 px = 1 rem, so the render is **320 × 800 dp at 2×**; `h-[460px]` ⇒ 230 dp of
a 320 dp viewport = 0.71875 × width. `windowWidth` is `BoxWithConstraints.maxWidth`
from the existing root — one read, never scroll-dependent.

Every **content/state/scroll** independence clause of ADR-47 AN4 §3 and AN5 §4 is
retained unchanged; only width is newly allowed to scale the height. BOUND-1 is
re-derived against the new band:

```
ArtworkWindow - ArtworkHandOff  >=  ProgressCardRingSize + 2 * MomentumSpacing.standard   // >= 128 dp
142.75 dp @360 · 142 dp @320 · 208 dp @412
```

The basis moves from `HeroRingSize` to `ProgressCardRingSize` because the ring is
now the largest opaque element the atmosphere carries. **Consequence:**
ADR-47 Amendment Note 6's contrast baseline is superseded, and the ISSUE-020
re-measurement on the §4.8/§4.9 frozen geometry has now been **executed** by the
Architect as one bounded script (`%LOCALAPPDATA%\Temp\opencode\issue020-remeasure\measure020.js`),
one fixed matrix: ADR-47 AN1 region-sample method (band rectangle → one mean
composited colour → WCAG ratio vs the band's ink; minimum governs), floors
**4.5:1 text / 4.5:1 check glyph / 3.0:1 non-text**; 2 themes × widths
{360, 412} (screen heights 800/915) × font scale {1.0, 2.0} × status-bar inset
{24, 48} × scrim α candidates {profile(H), profile(H−108), 0, peak} (exact over
unknown content height H: at a fixed y the profile's range is [0, peak], and the
scrim tint always moves contrast *toward* the theme's safe direction, so every
worst case lands on the α = 0 endpoint) × `AuroraState.defaultIntensity`
{0.35, 0.55, 0.75, 1.0} × page + ambient + aurora-field candidate set × the
§8.15 band list plus §10.6 item 5's four `success` obligations, the pending
card's empty ring and the ring percentage with its 7.2 dp track composited ×
scroll-position y-scan (4 dp) for every scrollable band × capsule progress
{0, .25, .5, .75, 1} (the capsule has no time-based pulse; `capsuleProgress` is
the selection transition) × progress-card aura-blob candidates (peak breath,
`primary`/`secondary` at 0.18/0.15 under the glass). **Result: FAIL — 23 of 26
bands fall below floor in at least one theme (Light 13, Dark 22); ISSUE-020
stays OPEN, the sweep is returned for human ruling per §4.9, and no ink, glass,
artwork, scrim, peak or code was changed.** The full band table is in the
Architect's handoff; only the pre-existing working-tree state remains.

### 8.7 Daily Momentum card — the approved horizontal composition — **the encouragement / statement ink is CORRECTED to `c.textSecondary` by ADR-49 AN2 (§10.1 ruling)**

```
HomeProgressCard(doneCount, totalCount, suffix, encouragement|completeStatement,
                 completedCount, pendingCount, ringSize = ProgressCardRingSize,
                 milestoneGlow, reduceMotion, entranceProgress)
└ Column(Modifier.fillMaxWidth()
      .liquidGlass(RoundedCornerShape(MomentumRadii.control)/*12 dp*/, GlassLevel.HERO, hue = c.auroraLavender)
      .padding(MomentumSpacing.standard)/*16 dp*/, testTag(HomeProgressCardTag))
   ├ Row(verticalAlignment = CenterVertically, spacedBy = MomentumSpacing.standard)
   │   ├ Column(Modifier.weight(1f), clearAndSetSemantics { contentDescription = today_hero_progress_desc })
   │   │   ├ Row(verticalAlignment = Alignment.Bottom /*baseline*/)
   │   │   │   ├ Text("%1$d"/*fraction*/, MomentumHeadlineCompact, c.textPrimary)
   │   │   │   └ Text(today_progress_suffix/*"items done"*/, bodyMedium + FontWeight.Medium, c.textSecondary)
   │   │   ├ Spacer(MomentumSpacing.micro)
   │   │   └ Text(encouragement ?: completeStatement, bodyMedium + FontWeight.Medium,
   │   │           color = c.textSecondary /* BOTH — CORRECTED by ADR-49 AN2; was
   │   │                    c.success /* encouragement */ ?: c.textPrimary /* statement */ */,
   │   │           maxLines = 2, overflow = Ellipsis)
   │   └ Box(Modifier.size(ProgressCardRingSize), contentAlignment = Alignment.Center)
   │         ├ ProgressOrb(progress, size = ProgressCardRingSize, showPercent = false,
   │         │             arcBrush = Brush.linearGradient(listOf(c.success, c.auroraBlue, c.auroraViolet)),
   │         │             glowEnabled = milestoneGlow && !reduceMotion, animate = !reduceMotion)
   │         └ Column(horizontalAlignment = CenterHorizontally)
   │             ├ Text("${roundToInt(progress * 100)}%", MomentumHeadlineSection, c.textPrimary)
   │             └ Text(R.string.today_progress_ring_unit /*"done"*/, MomentumLabelMicro, c.textSecondary)
   ├ HorizontalDivider(thickness = Dp.Hairline, color = c.textPrimary.copy(alpha = 0.08f))
   ├ Spacer(MomentumSpacing.compact)/*12 dp*/
   └ Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween)
       ├ Row(spacedBy = MomentumSpacing.tight) { Dot(8.dp, c.success) ;
       │       Text(cd_progress_completed /*"%1$d Completed"*/, labelMedium + SemiBold, c.textPrimary) }
       └ Row(spacedBy = MomentumSpacing.tight) { Dot(8.dp, c.textMuted) ;
               Text(cd_progress_pending /*"%1$d Pending"*/, labelMedium, c.textSecondary) }
```

**Values.** `ProgressCardRingSize = 96.dp` (Stitch `w-24 h-24`); stroke
`size × 0.075` = 7.2 dp (Stitch 7 — no change needed). Ring radius: Stitch 12 dp
= `MomentumRadii.control`; Stitch's `rounded-2xl` cards = 16 dp =
`MomentumRadii.card`. Ring gradient: Stitch `#00a572 → #06b6d4 → #7c3aed` maps
to the existing `c.success`, `c.auroraBlue`, `c.auroraViolet` — **three existing
tokens, no new colour**. Ring track stays `c.textPrimary @ TrackAlpha 0.55`
(ISSUE-014's ≥ 3:1 over artwork outranks Stitch's `0.08`). Dot size 8 dp is the
Stitch value; `MomentumSpacing.tight` is its 8 dp gap.

**States.** `HeroFigureState.Fraction` → as above. `Complete` → ring full
(`100%`) and the text block's fraction row is replaced by
`today_hero_complete` ("Today is complete") in `MomentumHeadlineCompact`; the
encouragement is absent (`encouragementFor` already returns null there).
`Hidden` → the card is **not composed** (existing behaviour), so nothing is
reserved. `emptyRegion` centring and the empty-state contract are unchanged.

**One description.** `today_hero_progress_desc` = "%1$d of %2$d completed today"
now covers the whole merged group (fraction, suffix, percentage, `done`,
breakdown). The encouragement stays **outside** the merged group and keeps its
own announcement.

**Supersedes** ADR-48 §1's "no second percentage" and ADR-48 AN4 §2's "the ring
frames the completed value" **for Home only** — required by §4.2 C ("circular
percentage ring **with the percentage inside**") and §4.2 C's explicit rejection
of the TASK-017 vertical layout. The *reason* ADR-48 §1 existed is preserved: the
ring is positioned **by the approved composition**, never by available width
(the text column is `weight(1f)` and the ring is `size`-constrained, so the ring
is invariant at every width, digit count and font scale). ADR-48 §2's `tnum`
rationale is preserved by `MomentumHeadlineCompact` — but note Manrope's `tnum`
is a **display** feature; verify `tnum` is applied to the fraction token or the
digit will reflow as it advances (§8.14 RISK-4).

**§4.7 r2 delta:** the card's background box additionally composes the
reference's **two `clipToBounds`-clipped aura blobs** (`w-36 h-36 bg-primary/30`
top-end, `w-32 h-32 bg-secondary/25` bottom-start) beneath the content row,
because §4.7 r2 requires their `aurora-glow-pulse`. The block above omitted them
while §8.11 referenced them; **§8.19 M2** is authoritative for them. They carry
no semantics, are not interactive, and add no text band to the contrast sweep.

### 8.8 Pending and Completed sections — **the `Finished` label ink is CORRECTED to `c.textSecondary` by ADR-49 AN2 (§10.1 ruling); the `verified` glyph stays `c.success`**

```
HomeSectionHeader(title, meta)   Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
    Text(title, MomentumHeadlineSection, c.textPrimary, Modifier.semantics { heading() })
    Text(meta, MomentumLabelMicro, c.textSecondary) }
```

`Pending` + `Scheduled Today`; `Completed` + `Finished` (label in `c.textSecondary` —
**CORRECTED by ADR-49 AN2**, previously emerald `c.success` — beside the new
`verified` glyph at `MomentumIconSizes.secondary`, which **stays** `c.success`
because it is a non-text surface; `HomeSectionHeader`'s `accent` therefore becomes
**glyph-only**). Screen-local by
necessity: `MomentumSectionHeader` has **13 call sites** and uses
`titleMedium` (16/24 400) — changing it would redesign Tasks, Calendar, Insights,
Settings, detail and creation screens (§4.6). Gaps: heading→cards and card→card
are Stitch's `space-sm` = **8 dp** ⇒ `MomentumSpacing.tight` (this is the one
`verticalArrangement` change, from `MomentumSpacing.item` 12 dp); section→section
is Stitch's `space-lg` = **24 dp** ⇒ `MomentumSpacing.section`.

```
HomeActivityCard(item, completed, entranceProgress)
└ Row(Modifier.fillMaxWidth()
      .liquidGlass(RoundedCornerShape(MomentumRadii.card)/*16 dp*/,
                   if (completed) GlassLevel.CONTROL else GlassLevel.CARD,
                   hue = habitIconAccent(activity.iconKey) * /* hue: null — see §8.14 */
                   , glassPress(interaction))
      .clickable(role = Role.Button, onClick = onOpen)
      .padding(MomentumSpacing.card)/*16 dp*/, Arrangement.spacedBy(MomentumSpacing.card))
   ├ Box(Modifier.size(48.dp), RoundedCornerShape(MomentumRadii.card))
   │    ├ background(Brush.linearGradient(135°, White@0.20 → habitIconAccent@0.42))
   │    ├ background(White@0.20, RoundedCornerShape(MomentumRadii.card))   // the inner wash
   │    ├ border(accent@0.34)
   │    └ Icon(momentumIcon(activity.iconKey), MomentumIconSizes.primary,
   │           tint = if (completed) Color.White else accent)
   ├ Column(Modifier.weight(1f))
   │   ├ Text(activity.title, titleSmall + SemiBold,
   │         color = if (completed) c.textSecondary else c.textPrimary,
   │         textDecoration = if (completed) TextDecoration.LineThrough else null,
   │         maxLines = 2, overflow = Ellipsis)
   │   └ if (secondaryLabel.isNotBlank())
   │        Text(secondaryLabel, bodyMedium + FontWeight.Medium, c.textSecondary, maxLines = 1)
   └ CompletionButton(completed, onToggle, contentDescription = completionActionLabel(activity, completed),
                      accent = CompletionAccent.SUCCESS, showIdleGlyph = false)
```

**Screen-local by necessity:** `ActivityCard` has 3 call sites and its current
rendering (44 dp flat-tint tile, `titleLarge`, no strikethrough, faint idle
glyph, `primary→secondary` completed fill) is what Tasks, Calendar and Habit
Detail ship. `HomeActivityCard` **composes the same shared primitives**
(`Modifier.liquidGlass`, `glassPress`, `momentumIcon`, `habitIconAccent`,
`CompletionButton`) — it does not reimplement glass and does not fork a control.
`ActivityCard.kt` and `MomentumComponents.kt` are **not modified**.

`CompletionButton` gains exactly two additive parameters, both defaulting to
today's behaviour so all three existing call sites are byte-identical:
`accent: CompletionAccent = CompletionAccent.THEME` (`THEME` = the existing
`primary→secondary` gradient; `SUCCESS` = `c.success → lerp(c.success, c.auroraMint, 0.5f)`
+ `Color.White @ borderDark` hairline + `shadow(MomentumGlass.heroElevation, spotColor = c.success @ 0.40f)`)
and `showIdleGlyph: Boolean = true` (`false` = the Stitch empty ring: no glyph,
keep the 48 dp target, `Role.Checkbox`, the existing label, haptic and pop).

**Reversal is preserved.** Stitch's completed card shows a *non-interactive*
emerald badge. Momentum's completed card stays fully actionable — a tap
un-completes (ADR-47 §21, `DECISIONS §12`). The emerald badge therefore **keeps
its button role and label**. Recorded deviation.

**Secondary line.** Unchanged function `homeMetadataLabel(item)` = configured
`dueTime` (localized) + `targetProgressLabel` ("5/8 glasses"), and **no metadata
row at all** when blank (`DECISIONS §29`). See §8.12 C3.

### 8.9 FAB

`MomentumFab` **unchanged** — material, `MomentumFabSize 56.dp`,
`MomentumIcons.add`, `MomentumIconSizes.prominent`, `c.onPrimary` ink,
`MomentumGlass.heroElevation`, theme-resolved hairline, `glassPress`,
`Role.Button`, `cd_quick_add`, placement `end = screen / bottom = section`,
`EmptyDayBottomReserve = MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section` (96 dp).
Reached through `CreateItemSheet` — unchanged business behaviour.

`code.html`'s `fab-glow` **3 s box-shadow pulse is ADOPTED** by the §4.7 ruling 2
— see **§8.19 M3**. It is an additive, default-`false` opt-in
(`glowPulse = true` at Home's call site only), so the shared component, its
material, size, glyph, hairline, `glassPress`, `Role.Button`, label and
placement are unchanged and **Habits' FAB is byte-identical**. The press
rotation the same ruling requires is `rotateGlyphOnPress` (**§8.19 M5**).
`group-hover:rotate-90` is still not implemented as *hover* (there is no hover
on touch, C12); the press analogue is what is built. Everything else in this
section stands: no new token, no new value, no new component, and the FAB's
"subtle glow" stays expressed within the existing
`MomentumGlass.heroElevation` / `highlight` / `borderDark` / `borderLight` /
`c.primary` spot-colour grid.

### 8.10 Bottom navigation — **CORRECTED by §8.18 (A1, human ruling §4.7 r1)**

The §8.10 refusal and §8.12 C10 as originally written are **withdrawn**. The
Stitch nav visual is authoritative: **VISUAL = Stitch, BEHAVIOR = Momentum.**
`LiquidGlassBottomBar` and `RowScope.LiquidGlassTab` are re-rendered to
`code.html`'s nav markup (**§8.18**). Unchanged: Nav3, `backStack.selectTab`,
the `showBar` gate, `Scaffold`, `contentWindowInsets`, the four destinations and
their routes. `MomentumNavGraph.kt` changes only the `bottomBar` slot's three
padding values (16/16/20 dp). `VISUAL = Stitch` reaches all four destinations
through the one existing call site — there is **no** Home-only fork. The safe
area and the FAB/`EmptyDayBottomReserve` interaction are resolved in **§8.18.3**.

### 8.11 Animation translation — **CORRECTED by §8.19 (A2, human ruling §4.7 r2)**

§8.11's C11 rejection of `aurora-glow-pulse`, `fab-glow` and `bolt-pulse` is
**withdrawn**, and ADR-48 §6 is superseded **for Home only**. The authoritative
per-loop specification — Compose expression, `code.html` numbers, reduce-motion
nulling and per-frame cost — is **§8.19**. The one-clock rule is §8.19.3 and the
corrected green assertions are §8.19.5.

| `code.html` keyframe | Compose implementation | Token / status |
|---|---|---|
`fade-slide-up` (14 px, `cubic-bezier(.16,1,.3,1)`, 4 blocks staggered 0/100/200/300 ms, total 900 ms) | **one** `LaunchedEffect(Unit)` `animateFloatAsState(0f → 1f, tween(MomentumMotion.enter))` in `TodayScreen`; each region derives its own `fadeIn`/`slideInVertically` from `entranceProgress` against its own start offset. `easing = MomentumMotion.easeOut` (Stitch's `(0.16, 1, 0.3, 1)` has no Momentum token — reused, deviation recorded). Applied to: chrome (0 ms), progress card (100 ms), Pending header (200 ms), Completed header (300 ms). `reduceMotion` ⇒ `1f` immediately. | **New token** `MomentumMotion.enter = 900`, derived from the reference's total stagger span. **No per-row stagger** — `LazyColumn` rows have no "first composition" identity, so a per-item entrance would re-fire on every scroll-in; the whole-section unit's Momentum equivalent is its header. Recorded, bounded deviation. |
`progress-fill` 1.4 s `cubic-bezier(.34,1.25,.64,1)` | existing `ProgressOrb` spring sweep | unchanged |
`check-pop` 0.85 → 1.18 → 1 | existing `CompletionButton` spring pop | unchanged |
card `:active scale(.98)` / `active:scale-[.99]` | `Modifier.glassPress(interaction)` on `HomeActivityCard` (0.97, reduced-motion-nulled by the modifier) | existing |
check-button `:active scale(.92)` | existing `CompletionButton` click + scale | existing |
`aurora-glow-pulse` 6 s `ease-in-out` on the card's two aura blobs | **ADOPTED — §8.19 M2.** One shared 6 s clock drives two `drawWithCache` blobs whose *cached* radial-gradient radius is scaled 32 → 40 dp per frame (`canvas.scale`) while their alpha scales 0.35 ↔ 0.6. `filter: blur()` itself remains a non-adoption. | `MomentumMotion.ambient` (6000) — an **exact** match to the reference's 6 s; no new token |
Aurora drift — the human's "Aurora subtle movement" | **ADOPTED — §8.19 M1:** `HomeAtmosphere.kt:257` `animated = false` → `animated = true`, which enables `AuroraBackground`'s **existing** drift (`MomentumMotion.ambient` = 6000 = `aurora-float`'s 6 s; ±3 % w / ±2.5 % h ≈ ±8 px at 320 dp). `code.html`'s `aurora-float` is **defined but unbound** and is **left unbound**. | no new token, component or keyframe; nulled by `AuroraBackground` itself |
`bolt-pulse` 2.2 s on the `verified` glyph | **ADOPTED — §8.19 M4.** `graphicsLayer` `scale 1 → 1.15`, `rotationZ 0 → −4°`, from the one shared 2.2 s clock. The drop-shadow half is not implemented. | **New token** `MomentumMotion.verifiedPulse = 2200` |
`fab-glow` 3 s on `MomentumFab` | **ADOPTED — §8.19 M3.** The existing single `shadow()`'s elevation 12 → 16 dp and its `c.primary` spot alpha 0.40 → 0.55; `glowPulse = true` at **Home only**. | **New token** `MomentumMotion.fabGlow = 3000` |
| FAB `hover:scale-105` / `active:scale-95` | existing `glassPress` (0.97) — same mechanism, same role, still nulled by the modifier; the reference's 0.95 vs 0.97 recorded as immaterial | existing |
| FAB glyph `group-hover:rotate-90` | **ADOPTED as the press analogue (§8.19 M5):** `rotateGlyphOnPress = true` at Home only, 90 degrees over `MomentumMotion.fast`. Hover itself stays n/a (C12). | existing `fast` + `easeOut` |
| `aurora-float` 8 px / 6 s | **not implemented, left unbound.** Verified: `aurora-float` occurs exactly once in `code.html`, inside the `@keyframes` block, so no element carries it. Binding it would render a movement the approved design does not show. The human's requirement is met by the Aurora-drift row above. | n/a |
| `flame-pulse` 2 s | **not implemented.** Verified: `.anim-flame` occurs exactly once in `code.html`, the class definition, so it is **unbound in the reference too**, and it belongs to the streak chip removed by §4.7 r3 / §7. | n/a |
`habit-card-interactive:hover`, `check-btn-interactive:hover` | **n/a** — no hover on touch | n/a |
`@media prefers-reduced-motion` | `rememberReduceMotion(reduceMotionOverride)`; every decorative spec is **nulled**, never shortened | existing |

Every one of Home's existing motions (`animateItem` placement specs, branch
`AnimatedContent`, encouragement `AnimatedVisibility`, Aurora intensity tween,
milestone glow + `MomentumMotion.reward` + one `HapticFeedbackType.Confirm`)
is retained and remains reduced-motion-nulled.

### 8.12 Typography, Light/Dark, and documented conflicts

**Typography.** Plus Jakarta Sans + Inter → **Manrope only**
(`res/font/manrope.ttf`); no font dependency added. Mapping, role by role:

| Stitch token | Stitch sp | Momentum | Action |
|---|---|---|---|
`headline-lg-mobile` 28/36 600 | greeting | `headlineLarge` 28/34 600 | **reuse** |
`body-sm` 14/20 @500 | greeting support, suffix, card secondary, `Completed`/`Pending` meta weight | `bodyMedium` 14/20 + `fontWeight = Medium` on the `Text` | **reuse** (per-`Text` weight, precedent `ActivityCard`) |
`headline-md` 24/32 600 | progress fraction, complete statement | `MomentumHeadlineCompact` 24/32 600 | **NEW** |
`headline-sm` 20/28 600 | ring percentage, section headings | `MomentumHeadlineSection` 20/28 600 | **NEW** |
`label-lg` 14/20 500→600 | card title | `titleSmall` 14/20 + `fontWeight = SemiBold` | **reuse** |
`label-md` 12/16 500 | breakdown labels | `labelMedium` 12/16 500 | **reuse** |
`label-sm` 11/14 600 @0.04em | date capsule, ring `done`, `Scheduled Today`, `Finished` | `MomentumLabelMicro` 11/14 600, `letterSpacing = 0.04.em` | **NEW** |
icon 28 / 20 / 24 | FAB / Settings / card glyph | `prominent` / `secondary` / `primary` | **reuse** |

Exactly **three** new text tokens, each named exactly, each carrying a
one-line derivation comment. `MomentumFigureDisplay` becomes unused on Home and
**stays declared**.

**Light/Dark.** The reference is **light**; `DESIGN.md` + the Tailwind config's
dark `#0b1326` surface palette is **not imported** (no M3 slot names, no
Tailwind/Material-Symbols glyphs, no `#0b1326`). Light follows `screen.png` on
Momentum's own `AuroraLight`; **Dark is Momentum's existing `AuroraDark` with the
identical composition and hierarchy** through theme-aware tokens — no separate
Dark redesign, no new dark colour. The one thing adopted theme-agnostically is
the scrim's *ramp shape*, which `HeroScrim` already has. No new colour token is
introduced anywhere.

**Documented conflicts (never silently resolved):**

| # | Conflict | Ruling |
|---|---|---|
C1 | `screen.png` "3 of 5 **habits** done" vs `code.html` / PRD / §4.2 C "**items** done" | **"items done."** The denominator is every eligible `Activity` — habits **and** tasks — so "habits" would be factually wrong; and §4.2 C (the human brief) specifies "X of Y items done". `screen.png`'s *structure* (headline-metric + subordinate suffix on one baseline) is adopted from it; its noun is a sample value, which §4.1.5 declares visual-reference-only. New string `today_progress_suffix`. |
C2 | `screen.png` shows streak chips (`7d`, `12d`); `code.html` **implements no chip**; the PRD removed streak badges; ADR-48 §5 has Home rows passing `streak = 0` | **Not implemented.** `code.html` (the behavioural reference) omits them, §4.2 D enumerates the card as "icon, title, secondary metric, completion control, glass treatment" with no chip, and ADR-48 §5 + the PRD are approved product decisions. Adding them would need a new chip component and would create a **second** streak representation alongside the encouragement line. Architect recommends rejection; the human may confirm or override (override ⇒ new component + a token/accent decision + removal of `encouragementFor` form 2). |
C3 | `screen.png` secondary lines: `15 mins`, `500ml`, `3 entries` | Momentum's `Activity` has **no duration field and no entry-count field** (`Activity.kt`: `dueTime`, `targetPerOccurrence`, `unit`). The secondary line stays `homeMetadataLabel(item)` = localized `dueTime` + `targetProgressLabel`, and **renders no row when blank**. Closing this gap needs a schema change — out of scope (§4.6). Recorded as a data limitation, not a design choice. |
C4 | `DESIGN.md` + Tailwind dark `#0b1326` vs the light `screen.png` / `body` `rgb(234,229,247)` | See §8.12 Light/Dark. `DESIGN.md`'s palette is **not** imported; Dark uses Momentum's existing `AuroraDark` with the same composition. |
C5 | Plus Jakarta Sans + Inter vs Manrope-only | See §8.12 Typography. No dependency added. |
C6 | `backdrop-filter: blur()` | Not expressible and **not added** — `LiquidGlass`'s own KDoc already decided this. The only glass extension needed is `hue`. |
C7 | `screen.png` has **no empty state** | Momentum's existing empty contract is retained **verbatim**: `EmptyState` with `today_empty_title` / `today_empty_body`, `icon = null`, **no CTA** (ADR-48 AN2), centred by measured geometry in `emptyRegion`, FAB as the single creation affordance, both sections hidden. `EmptyState.kt` is not modified. Flagged as not specified by the reference. |
C8a | `DESIGN.md` radii (base 8 px, large 16 px) vs `code.html` classes (`rounded-xl` = 12 px on the progress card, `rounded-2xl` = 16 px on cards) | **`code.html` wins** (secondary > supporting). 12 dp and 16 dp, both existing tokens. |
C8b | Icon tiles are **3-hue** gradients in `screen.png` (pink→violet→indigo) | Momentum maps one `iconKey` to **one** `habitIconAccent`. A faithful 3-hue tile needs a per-icon 3-colour table = new tokens + new visual language (§4.5). **2-stop 135° `White@0.20 → accent@0.42`** at the correct 48 dp / 16 dp geometry. The Developer must **not** invent hues. |
C9 | Stitch side padding 16 dp vs Momentum's 20 dp screen axis | Momentum's 20 dp `MomentumSpacing.screen` (every screen, `HomeChrome`, cards and headers). Stitch's own `space-md` maps to `MomentumSpacing.standard` (16 dp) for gaps/padding **inside** components. |
C10 | Nav bar `rounded-full` + 16/16/20 margins vs `RoundedCornerShape(26.dp)` + 20/20/12 | **RESOLVED by §4.7 ruling 1 (A1, §8.18): the Stitch values win.** `CircleShape`, 16/16/20 dp, 64 dp `h-16`, `SpaceAround`, Stitch icon sizes and the Stitch active capsule are adopted for the **one** shared implementation, so all four destinations change together and none can desynchronise. Behaviour, routing, `selectTab` and the `showBar` gate are unchanged. Residual recorded delta: the bar's ends now sit 4 dp proud of the content's 20 dp screen axis (C9 stands for content). |
C11 | `aurora-glow-pulse` 6 s, `bolt-pulse` 2.2 s and `fab-glow` 3 s permanent loops | **REVERSED by §4.7 ruling 2 (A2, §8.19): all three are ADOPTED.** ADR-48 §6's blanket "Home runs no permanent infinite animation" is superseded for Home by a narrower rule (one shared clock, slow ambient loops, draw-phase only, absent under reduced motion). The two currently-green assertions that encode §6 are **corrected deliberately, not deleted** (§8.19.5). `flame-pulse` and `aurora-float` remain unimplemented: both are defined-but-unbound in `code.html`, and `flame-pulse` belongs to the removed streak chip. |
C12 | `hover` transitions | No hover on touch; not implemented. |
C13 | Stitch's card fade is ~50 % of a 230 dp band; `MomentumHeroArtwork`'s hand-off is 88 dp | Kept at 88 dp. ADR-30's caller contract lets callers pass band amounts only, the component is not to be modified, and duplicating the constant is what ADR-47 AN3 already rejected. |
C14 | New 96 dp ring makes `MomentumFigureDisplay` (40/48) unused on Home | Token retained, unused, KDoc-noted. |
C15 | ADR-48 §1 "one representation" + AN4 §2 "ring frames the value" vs §4.2 C "ring **with the percentage inside**" | §4.2 C is the binding human requirement ⇒ **superseded on Home** (ADR-48 AN5). The ring's *positional* invariant is preserved (§8.7). |

### 8.13 Accessibility

One merged `contentDescription` on the progress card (fraction + percentage +
breakdown); the encouragement keeps its own announcement. Heading navigation
yields **greeting → Pending → Completed**. The chrome stays **three separate,
unmerged** semantics nodes (`clearAndSetSemantics` / `mergeDescendants` remain
forbidden in `HomeChrome.kt`) so Settings stays reachable; the date capsule pill
carries no semantics of its own. Section status labels are plain text.
Touch targets: completion **48 dp** (Stitch's 36 px is refused), FAB 56 dp,
Settings 48 dp (Stitch's 40 px is refused). The loading skeleton keeps its single
`today_loading` label, `clearAndSetSemantics`, no spinner, no shimmer. The
atmosphere contributes **no** semantics. Font scale 2.0: the chrome is measured,
the region takes the remainder and still scrolls, and **no** foreground element is
height-constrained by the atmosphere, so clipping is unreachable. The empty card
carries no CTA and the FAB is the only add action. Nothing here is traded for
composition.

### 8.14 Performance

Scrolling recomposes **nothing**: the atmosphere reads only `AuroraState` /
intensity / theme, the list only `TodayUiState.Success`, and the one measurement
(`chromeHeightPx`) is a pure function of the chrome's content, insets and width.
Added this pass: one **finite** 900 ms `animateFloatAsState` (not a loop), the
four derived entrance progresses (read only inside the chrome / card / headers),
and `BoxWithConstraints.maxWidth` read once. No `NestedScrollConnection`, no
scroll read, no `derivedStateOf`, no `onGloballyPositioned`, no parallax.
`HeroScrim` keeps its `drawWithCache`. `HomeActivityCard` adds no per-frame
state. `maxWidth` is read via the **existing**
`BoxWithConstraints` — no new window-size observer.

**§4.7 r2 delta — the guarantee is preserved, and is now stated as a rule:**
three ambient loops are added (**§8.19.4**), driven from **one**
`rememberInfiniteTransition` that is **not composed at all** under reduced
motion, consumed exclusively in `drawWithCache` / `graphicsLayer` /
`Modifier.shadow`. No loop's inputs touch scroll state, none is read by a
composable's own body, and no `LazyColumn` item owns a transition or a mutable
animation state — so a fling still recomposes nothing and invalidates no list
item's layer. The paragraph's token blacklist
(`NestedScrollConnection`, `derivedStateOf`, `onGloballyPositioned`,
`parallax`, `animateScrollPosition`, …) is unchanged and still applies. The
phrase "no `rememberInfiniteTransition` on Home" in the §8 pass is **replaced**
by §8.19.3's narrower rule; everything else in this section stands.

### 8.15 Tests

**Rewrite to the retained outcomes (never delete coverage)** —
`HomeAtmosphereAssetsTest` (+`HomeScrollSurfaceTest`): `ArtworkBandFraction`
derivation, `ArtworkWindow` width-proportional and still content/state/scroll
independent, BOUND-1 against `ProgressCardRingSize`, scrim profile/peaks
unchanged, the horizontal lockup (fraction before ring, ring before percentage,
divider before breakdown), **new** negative assertions that the retired vertical
arrangement (`HeroRingSize`, `HeroOrbGap`, the vertical lockup) and the machines
that stay rejected (`flame-pulse`, `aurora-float`, per-row entrance) stay absent;
the `fab-glow`-absent negative is **inverted** into positive blob assertions
(§8.19.6) ·
`HomeAccessibilityTest`: heading count 3, three unmerged chrome nodes, 48 dp
completion target, one merged card description, no `textSecondary` **over the
atmosphere** in `HomeChrome.kt` / `HomeProgress.kt`, entrance nulled under reduced
motion, `HomeCards.kt`'s new bands · **plus the two deliberate A2 corrections
(§8.19.5 B and C)** ·
`HomeSectionMotionTest`: the three `if (reduceMotion) null else` specs, the
entrance clock reduced-motion-nulled, `animateItem` counts, the milestone one-shot,
and — **replacing the §8 pass's "no `rememberInfiniteTransition` on Home"** — the
one-shared-ambient-clock rule plus the new
`the ambient loops stay out of the list items` test (§8.19.5 A, §8.19.6) ·
`QuickAddTest`: FAB material/size/glyph/placement/label **unchanged** — its
existing assertions stand; the §8 pass's planned "the reference's permanent glow
pulse is absent" is **inverted** into the opt-in assertions in §8.19.6.
`HomeCompositionTest`: bounds — atmosphere = whole content area, artwork top edge
= window top with `height == ArtworkWindow(width)`, chrome fixed over the
artwork, region top edge = chrome bottom edge at top **and** after `swipeUp`,
artwork/chrome unmoved by scroll, empty card centred, empty day collapses nothing ·
`HomeProgressStateTest`: the horizontal card in Fraction / Complete / Hidden ×
encouragement ON/OFF, merged description, percentage inside the ring ·
`HomeStatesTest`: extends to both sections present, the FAB, the skeleton and the
milestone mapping.

**New / extended bands for the re-run contrast sweep** (method unchanged, floor
≥ 4.5:1, peaks movable only inside `[0.15, 0.35]`): fraction · suffix ·
encouragement/statement · **ring percentage** (the ring's 7.2 dp
`textPrimary @ 0.55` track composited into the sampled background) · ring `done`
label · `3 Completed` / `2 Pending` · the divider · both section headings and
both status labels · the completed card's strikethrough title · the completed
card's **emerald** control (its `c.success` fill is the sampled background) ·
the pending card's empty ring · **A1 adds four nav bands**: the inactive label
and inactive icon (`c.textSecondary` over the composited `GlassLevel.HERO` bar)
and the active label and active icon (`c.textPrimary` over the animated capsule
at both pulse extremes).

### 8.16 Device-verification plan

**Runnable here (never claimed as on-device):** `:app:testDebugUnitTest`,
`:app:assembleDebug`, `:app:assembleDebugAndroidTest` (compiles the instrumented
sources; it does **not** execute them). `adb` is unavailable, so **no instrumented
suite and no screenshot/pixel/bounds claim** may be made by any agent.

**Human on-device matrix:** Light + Dark × {empty, partial, complete} × font scale
1.0 + 2.0 × Reduce Motion off/on × widths 360 dp and 412 dp (plus one 320 dp
device for the narrowest `ArtworkWindow`). Check: artwork recognisable and
un-squeezed with the chrome over it; the card's horizontal lockup invariant at
every width/digit count/font scale; both sections, strikethrough, emerald badge,
TapBack order `greeting → Pending → Completed → FAB → nav`; every target ≥ 48 dp;
the re-run contrast sweep's governing minimum per band per theme; scroll
performance with Layout Inspector (zero recomposition on a fling).

**§4.7 deltas — added checks.** *A1 (§8.18):* the nav renders **identically** on
Home, Habits, Tasks and Insights; its pill shape, 16 dp ends, 20 dp bottom margin,
64 dp height, `SpaceAround` rhythm and the active Home capsule all read as
`screen.png`; the bar's 4 dp edge delta against the 20 dp content axis (deviation
12) is acceptable; **no** bar appears on Onboarding, Calendar, Settings, Themes,
create/edit/detail or the Create Item sheet; every tab target is ≥ 48 dp on both
axes at font scale 2.0 and the bar grows rather than clipping. *A2 (§8.19):* with
Reduce Motion **on**, no ambient motion is visible anywhere on Home and the FAB
still presses and rotates instantly; with it **off**, the two card blobs breathe,
the FAB's shadow swells and settles on a 3 s cycle, and the `verified` glyph
pulses on 2.2 s; a fling still shows **zero** recompositions in Layout Inspector,
and no per-item animation instance appears as cards scroll in and out.

### 8.17 Risks and decisions requiring explicit human approval

**APPROVAL GATE — CLEARED by §4.7 (2026-10-01).** The human approved §8 as
written, ruling on every item below, and §4.7 r8/§24 removed the gate: **no
further human approval is required before the Developer begins** unless a
genuinely new architectural conflict or scope change appears, which is escalated
through the Orchestrator per §23.2. Items 1–8 are therefore **decisions on
record**, not open questions:

1. **`ArtworkWindow` becomes width-proportional** (`460/640 × windowWidth`), which
   amends the human-ruled ADR-47 AN4 §3 / AN5 §4 ("the window does not grow …
   constant in every state"). Every content/state/scroll clause survives; only
   width may now scale the height. BOUND-1 is re-derived against
   `ProgressCardRingSize`; **ADR-47 AN6's contrast baseline is superseded and
   re-run from scratch**.
2. **ADR-48 §1 and AN4 §2 superseded on Home** — the ring carries the percentage
   beside the fraction, i.e. the "one representation" invariant is traded for the
   approved horizontal composition and its cohesive lockup.
3. **The date capsule becomes a glass pill** — a second sub-surface in the fixed
   chrome, which amends ADR-47 AN5 §3(a) ("the *one* permitted sub-surface is
   the Settings control").
4. **Copy changes** — "Remaining today"/"Completed today" → "Pending"/"Completed";
   new `Scheduled Today`, `Finished`, `items done`, `done`, "%1$d Completed",
   "%1$d Pending", and the new greeting support line "Ready to conquer your day?"
   (a fixed line, deliberately **not** the toggle-controlled encouragement).
5. **Three shared-component extensions**, each additive and default-preserving:
   `ProgressOrb(arcBrush: Brush? = null)`,
   `CompletionButton(accent: CompletionAccent = THEME, showIdleGlyph: Boolean = true)`,
   and one optional `hue: Color? = null` on `Modifier.liquidGlass`. **A2 adds two
   more in the same shape**: `MomentumFab(glowPulse: Boolean = false,
   rotateGlyphOnPress: Boolean = false)` (§8.19 M3/M5).
6. **Three new type tokens** (`MomentumHeadlineCompact` 24/32 600,
   `MomentumHeadlineSection` 20/28 600, `MomentumLabelMicro` 11/14 600 @0.04em)
   and **three** new motion tokens (`MomentumMotion.enter = 900` per §8.11,
   `MomentumMotion.fabGlow = 3000` and `MomentumMotion.verifiedPulse = 2200` per
   §8.19). The nav labels reuse the already-approved `MomentumLabelMicro`.
7. **New screen-local composables** `HomeProgressCard`, `HomeSectionHeader`,
   `HomeActivityCard` in `screens/today/HomeCards.kt`; `ActivityCard.kt`,
   `MomentumComponents.kt`, `MomentumBadges.kt` untouched. **A1 additionally
   re-renders the two existing shared nav composables** in place
   (`LiquidGlassBottomBar`, `RowScope.LiquidGlassTab`) rather than forking them —
   the human's ruling, and the only call site is shared by all four destinations
   (§8.18.1).
8. **Conflict rejections confirmed by the human:** the streak chips (C2), the
   non-interactive completed badge (kept interactive, §8.8), the 3-hue icon tile
   reduced to 2 stops (C8b), the new card secondary information (C3). **C10 and
   C11 were reversed by the human and are specified in §8.18 and §8.19.**

**RISKS:**
- **R1 — contrast regression.** A wider artwork band + a brighter
  `GlassLevel.HERO` card with a *diagonal* wash + `textSecondary` on that card is
  a genuinely new compositing situation; the §8.15 sweep must clear 4.5:1 for
  every new band in both themes. If a band fails, move **only** the scrim peak
  inside `[0.15, 0.35]` — never the four-stop profile, `ScrimPeakStart/End`, the
  ink, or the artwork.
- **R2 — fixed `ArtworkHandOff = 88.dp` vs a width-scaled band.** At the 320 dp
  floor the opaque region is 142 dp; that clears BOUND-1 but is the tightest case.
  Confirm the illustration still reads at 320 dp on device.
- **R3 — `hue` on `Modifier.liquidGlass` touches a shared modifier.** The default
  is `null` and every existing caller omits it, so this is source- and
  behaviour-compatible — but the Reviewer must verify **no** existing call site
  gains a chromatic wash.
- **R4 — `tnum` on the new tokens.** `MomentumFigureDisplay` documents Manrope's
  `tnum` as verified in the binary; the new 24 sp fraction is 3 digits narrower
  than the 40 sp figure it replaces. Verify the fraction does not visibly reflow
  as `done` advances 9 → 10 → 100. If `tnum` cannot be applied to the new token,
  that is a **stop-and-escalate**, not a substitution.
- **R5 — LazyColumn `verticalArrangement` change (12 → 8 dp)** touches the
  heading→card gap as well as card→card. Confirm the section rhythm still reads.
- **R6 — the entrance clock is the only wall-clock motion on Home.** It runs once
  per composition; confirm it does not replay on a payload refresh
  (`AnimatedContent`'s `contentKey = todayBranch(it)` must keep the Success
  branch's composition alive — if the clock is placed inside the branch it will
  re-run on a completion).
- **R7 — `HomeSectionHeader` is screen-local**, so section headings are no longer
  produced by `MomentumSectionHeader`. Any future shared-header change will not
  reach Home. Accepted, documented.
- **R8 (A1) — the nav bar is shared, so four screens move at once.** Not a
  Home-only change by construction (§8.18.1): `LiquidGlassBottomBar` /
  `LiquidGlassTab` have one call site each. The only *layout* consequence is a
  **+8 dp** `innerPadding.bottom` on Home, Habits, Tasks and Insights
  (`bottom-5` 20 dp replaces `compact` 12 dp while the bar's own 64 dp height is
  unchanged). The Reviewer must tap through all four destinations on device and
  confirm the bar renders identically and that nothing clips. See §8.18.3 for the
  FAB / `EmptyDayBottomReserve` proof — **the FAB cannot collide**.
- **R9 (A2) — three permanent loops on the busiest screen.** The loops are the
  human's requirement, so the mitigation is structural, not optional: one shared
  clock (§8.19.3), draw-phase only (§8.19.4), no transition composed under
  reduced motion, and no loop inside a `LazyColumn` item's own scope. Verify with
  Layout Inspector that a fling still causes **zero** recompositions, and confirm
  the reduced-motion path composes no `InfiniteTransition` at all. `MomentumFab`'s
  shadow redraw (§8.19 M3) is the most expensive of the three and is confined to
  one fixed 56 dp element outside the list.

---

### 8.18 A1 — bottom navigation rendered to the Stitch visual (human ruling §4.7 r1)

**Instrument.** §8.10 and §8.12 C10 as originally written are withdrawn. This
section is the specification; ADR-49 Amendment Note 1 is the record. Every
`code.html` px is read as a dp (ADR-49 §1), and `code.html`'s nav markup is the
concrete value source. `VISUAL = Stitch, BEHAVIOR = Momentum`.

#### 8.18.1 One shared implementation, no fork — and why a fork is unnecessary

`LiquidGlassBottomBar` and `RowScope.LiquidGlassTab` have **exactly one call site
each**, both at `navigation/MomentumNavGraph.kt:223-232`, inside the
`Scaffold.bottomBar` slot. `Destinations` (`MomentumNavGraph.kt:89-96`) is the
single list of the four tabs, and `showBar` (`:206`) is the only gate. There is
no second consumer anywhere in the app, and no secondary route composes a bar.

Consequence: re-rendering the two composables in place reaches **Home, Habits,
Tasks and Insights identically**. The four destinations *cannot* desynchronise,
because there is only one code path. A Home-only fork is therefore not merely
unnecessary — it would create the second implementation that §8.18.1's single
call site exists to prevent, and the human forbade it. Nothing is added to
`MomentumNavGraph.kt` except three padding values; the destinations, routing,
`selectTab`, the `showBar` gate, `Scaffold`, `contentWindowInsets` and Nav3 are
untouched.

#### 8.18.2 Exact Compose translation

**Bar** — `LiquidGlassBottomBar`:

| `code.html` | Compose |
|---|---|
| `rounded-full` | `CircleShape` (was `RoundedCornerShape(26.dp)`) |
| `h-16` on the inner row | `Modifier.heightIn(min = NavBarHeight)` with `private val NavBarHeight = 64.dp`. **`heightIn(min =)`, not `height(64.dp)`** — at font scale 1.0 this renders exactly 64 dp, and at larger scales the bar grows instead of clipping (§8.13). Precedent: `MomentumComponents.kt:62,188`. |
| `px-space-md` | `padding(horizontal = MomentumSpacing.standard)` (16 dp) |
| — (no vertical padding) | the `vertical = 6.dp` padding is **removed** |
| `justify-around items-center` | `horizontalArrangement = Arrangement.SpaceAround`, `verticalAlignment = Alignment.CenterVertically` |
| `GlassLevel.HERO` material | **unchanged** — see deviation 2/3/4 |

**Tab** — `RowScope.LiquidGlassTab`:

| `code.html` | Compose |
|---|---|
| `min-w-[62px]` (active) / `min-w-[56px]` (inactive) | `Modifier.defaultMinSize(minWidth = 62.dp / 56.dp, …)`; **`Modifier.weight(1f)` is removed** so `SpaceAround` has free space to distribute |
| `min-h-[48px]` (active) / `min-h-[44px]` (inactive) | `minHeight = MomentumHeights.controlMin` (**48 dp for both** — see deviation 5) |
| `py-1` | `padding(vertical = MomentumSpacing.micro)` (4 dp) |
| `mt-0.5` | `verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro)` (2 dp) |
| active icon capsule: `px-4 py-1.5 rounded-full` | a `Box` with `padding(horizontal = MomentumSpacing.standard, vertical = 6.dp)`, `CircleShape` |
| …its `linear-gradient(135deg, rgba(200,225,255,.75) 0%, rgba(220,210,255,.85) 100%)` | `Brush.linearGradient(135f, listOf(lerp(c.auroraBlue, c.surface, 0.55f).copy(alpha = 0.75f), lerp(c.auroraLavender, c.surface, 0.50f).copy(alpha = 0.85f)))` — two **theme-token** stops carrying the reference's exact alphas and angle; theme-aware by construction, so one ink works in both themes (deviation 11) |
| …its `border: 1px solid rgba(255,255,255,0.9)` | `border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape)` (deviation 10) |
| …its `box-shadow: 0 2px 10px rgba(99,102,241,0.2)` | `shadow(2.dp, CircleShape, spotColor = c.primary.copy(alpha = 0.2f))` (deviation 9) |
| active icon `text-[22px] text-[#312e81]` FILL 1 / wght 700 | `Modifier.size(22.dp)`, `tint = c.textPrimary`; Momentum's existing `Icons.Rounded.Home` **is** the FILL-1 pair |
| inactive icon wrapper `w-9 h-9 rounded-full` | `Modifier.size(36.dp).clip(CircleShape)`; the `group-hover:bg-white/40` is **not** implemented (C12) |
| inactive icon `text-[23px] text-[#64748b]` FILL 0 / wght 500 | `Modifier.size(23.dp)`, `tint = c.textSecondary`; `Icons.Outlined.Home` **is** the FILL-0 pair |
| labels `font-label-sm` 11/14, `font-bold` active `#1e1b4b`, inactive `font-semibold` `#64748b` | `MomentumLabelMicro` (the already-approved 11/14 600 token) with `fontWeight = Bold` when selected, `SemiBold` otherwise; colour `c.textPrimary` / `c.textSecondary` |
| `aria-current="page"` on the active anchor | the existing `selected` parameter — unchanged |
| accessibility | `Role.Tab` **kept**; the icon's `contentDescription = label` **kept**; `glassPress` **kept** (press feedback is not in the human's A1 list, so removing an affordance is not authorized) |

**Motion reconciliation** (exact, as required):

- **Keep** `animateColorAsState` for `iconTint` and for the label colour, both on
  the existing `colorSpec = tween(MomentumMotion.fast, easing = MomentumMotion.easeOut)`,
  retargeted to `c.textPrimary` ↔ `c.textSecondary`. That is Stitch's
  `transition-all` on the ink.
- **Add** exactly **one** `animateFloatAsState(if (selected) 1f else 0f, motionSpec,
  label = "tabCapsule")`. The two capsule stops are scaled by
  `stop.copy(alpha = stop.alpha * capsuleProgress)` and the hairline by
  `Color.White.copy(alpha = 0.9f * capsuleProgress)`. Alphas are scaled — **never
  `lerp` toward `Color.Transparent`**, which would darken through
  premultiplication. The capsule `Brush` is rebuilt per frame only during the
  180 ms selection change.
- **Remove** the selected scale 1.06 (`iconScale` / `animateFloatAsState(1.06f)`):
  with the Stitch capsule a 1.06 scale enlarges the glyph inside a capsule whose
  padding is Stitch-exact, and it is a flourish the reference does not have.
  `VISUAL = Stitch` governs.
- **Remove** the old whole-tab `selectedBg` (`c.primary @ 0.18f`) and
  `selectedBorder` (`c.primary @ 0.40f`) painted on an 18 dp rounded background:
  superseded by the icon capsule.
- The `RoundedCornerShape(18.dp)` press/clip shape on the tab column stays (it is
  Momentum's own interaction affordance, not a Stitch surface).

#### 8.18.3 Safe area, bottom positioning, and the FAB — the highest-risk item, resolved

`MomentumNavGraph.kt`'s `bottomBar` `Box` becomes:

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Bottom),   // UNCHANGED
        )
        .padding(
            start = MomentumSpacing.standard,   // 16 dp  = code.html left-4 / right-4
            end = MomentumSpacing.standard,     // 16 dp
            bottom = MomentumSpacing.screen,     // 20 dp  = code.html bottom-5
        ),
)
```

**Safe area.** `WindowInsets.navigationBars.only(Bottom)` is **retained** and is
Momentum's existing mechanism; it *is* the platform equivalent of Stitch's
`env(safe-area-inset-bottom)`. Verified: `.pb-safe` occurs exactly once in
`code.html` — the utility's own CSS rule — and is applied to **no** element, so
the reference's safe-area handling is a defined-but-unbound artifact. Its intent
is realised by the mechanism Momentum already has, so **no code is needed for
it** and none is invented. Resulting geometry, from the screen bottom upwards:
`navigationBars` inset → **20 dp** → 64 dp bar, with 16 dp side margins — exactly
Stitch's `bottom-5` + `h-16` + `left-4 right-4`, with the device safe area
underneath.

**FAB interaction — the FAB cannot collide, and the proof is a measurement.**

1. Today's bar height, measured from source: `LiquidGlassBottomBar`'s
   `padding(vertical = 6.dp)` plus the tallest `LiquidGlassTab`
   (`padding(vertical = 4.dp)` + a 26 dp icon + a 2 dp gap + a 16 dp `labelMedium`
   line = 52 dp) = **64 dp**. Today's slot = inset + **12** + 64 = inset + 76 dp.
2. After A1 the slot = inset + **20** + 64 = inset + **84 dp**. The **bar's own
   height is unchanged at 64 dp**; the slot grows by exactly **8 dp**, because
   `bottom-5` (20 dp) replaces `compact` (12 dp).
3. `Scaffold` measures the `bottomBar` slot and folds it into `innerPadding.bottom`,
   which `MomentumNavGraph.kt:239` applies to the whole `NavDisplay`. So all four
   destinations' content areas become 8 dp shorter automatically and nothing else
   has to move.
4. `MomentumFab` lives **inside** that content area (`TodayScreen.kt:556`,
   `.align(Alignment.BottomEnd).padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.section)`),
   so it is carried up with the +8 dp. Its clearance is `bottom =
   MomentumSpacing.section` (24 dp) measured from the top edge of the bottom-bar
   slot — i.e. the FAB never overlaps the bar in the first place.
5. Stitch's own equivalent gap is **8 px** (`bottom: 5.75rem` = 92 px, minus the
   bar's top at `bottom-5 + h-16` = 84 px). Momentum's 24 dp is *larger* than the
   reference's 8 px, so the reference's tighter spacing is deliberately not
   reproduced and collision is unreachable. `EmptyDayBottomReserve` and
   `quickAdd` placement are **unchanged**, and §8.9's placement stays pinned by
   `QuickAddTest`.
6. `EmptyDayBottomReserve = MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section`
   = 16 + 56 + 24 = **96 dp**, **unchanged**. It is measured from the *region's*
   bottom — already inset by `innerPadding` — not from the screen bottom, so the
   +8 dp is absorbed by `innerPadding` and the reserve continues to reserve
   exactly the FAB's footprint (56 dp + 24 dp gap + 16 dp breathing room) above
   the region's tail. The last card still scrolls fully clear of the FAB, and
   `emptyRegion = viewportDp − chromeHeight − EmptyDayBottomReserve` is unaffected.

**Conclusion:** no FAB call-site change is required, and `EmptyDayBottomReserve`
does not move. The only visible effect is that Home's (and the other three
destinations') bottom-most content sits 8 dp higher.

#### 8.18.4 Recorded deviations

1. `backdrop-filter: blur(24px) saturate(180%)` → **not added**; restated
   documented non-adoption (ADR-49 §4, §8.5, C6). The Stitch look is achieved
   with the existing glass material: `GlassLevel.HERO`'s translucent
   `c.surface @ 0.68` vertical wash with its top-edge illumination streak, its
   theme-resolved hairline and its `heroElevation` shadow, over the ambient
   Aurora; `saturate(180%)`'s role — letting the backdrop read more chromatic —
   is met by the atmosphere showing through the wash. **No blur layer is added and
   the Stitch treatment is not silently dropped: it is expressed in glass.**
2. `background: rgba(255,255,255,0.8)` → `GlassLevel.HERO` (0.68). Raising it
   would make the bar more opaque and stop the atmosphere reading through it —
   §8.5's identical, already-approved ruling for the cards.
3. `border: 1px solid rgba(255,255,255,0.95)` → `liquidGlass`'s theme-resolved
   hairline (`White @ borderDark` Dark / `textPrimary @ borderLight` Light). A
   white border is invisible on a near-white bar — the exact reason ADR-48 AN3
   moved the FAB to an ink hairline in Light.
4. The three-part `box-shadow` (violet `0 16px 36px`, black `0 2px 6px`, white
   inset) → `liquidGlass`'s single `shadow(heroElevation 12.dp, ambient Black@0.35,
   spot c.primary@0.10)`. The inset hairline is already supplied by the border and
   the top streak.
5. `min-h-[44px]` inactive → **48 dp** (Stitch's `min-h-[48px]` active value).
   §19 touch targets are non-negotiable, and Stitch's 44 px is below the floor.
   At font scale 1.0 both tabs' natural heights (50 dp active / 52 dp inactive)
   already exceed 48 dp, so the **guarantee** is corrected while the **rendered
   composition is unchanged** — the reference's own numbers, not a redesign.
6. `h-16` fixed → `heightIn(min = 64.dp)` (identical at font scale 1.0; grows
   rather than clips beyond it).
7. `#312e81` / `#1e1b4b` / `#64748b` → `c.textPrimary` / `c.textPrimary` /
   `c.textSecondary` (Light `#172033` / `#172033` / `#596579`; Dark `#F5F7FC` /
   `#F5F7FC` / `#AEB8CA`). **No new colour token.**
8. `label-sm` 11/14 700 / 600 → the already-approved `MomentumLabelMicro`
   11/14 with `Bold` / `SemiBold`.
9. The capsule's `rgba(99,102,241,0.2)` spot → `c.primary.copy(alpha = 0.2f)`;
   `#6366F1` is not a Momentum token. This is the same glow-spot substitution
   `MomentumFab` and `Modifier.liquidGlass` already document.
10. The capsule's `rgba(255,255,255,0.9)` border → `Color.White.copy(alpha = 0.9f)`.
    A literal **alpha**, not a literal colour or a new token — the same
    construction `Modifier.liquidGlass` already uses in this file for its
    light-mode top streak (`Color.White.copy(alpha = 0.55f)`).
11. The capsule's two literal pastel stops → `lerp(c.auroraBlue, c.surface, 0.55f)`
    and `lerp(c.auroraLavender, c.surface, 0.50f)` at the reference's own
    `0.75f` / `0.85f` alphas and 135° angle. A single-hue `hue` wash would
    simplify a two-stop gradient the human named explicitly, which §4.1.1
    forbids. Because both stops lerp toward `c.surface`, the capsule is pale over
    Light and deep over Dark, so a single `c.textPrimary` ink is legible in both
    — §8.12's "identical composition and hierarchy in both themes through
    theme-aware tokens", not a Dark redesign.
12. **Recorded conflict between A1 and C9:** the bar now sits at 16 dp while
    Home's cards keep `MomentumSpacing.screen` (20 dp), so the bar's ends sit
    **4 dp proud** of the content on each side. The human's ruling resolves it in
    favour of the nav; C9's 20 dp content axis is untouched. Added to the §8.16
    on-device visual check.
13. The active capsule is 34 dp tall against the inactive 36 dp slot, so with
    per-tab `CenterVertically` the active label sits ~1 dp higher — the
    reference's own asymmetry (its anchors are individually centred in a 64 px
    row), reproduced deliberately so the Developer does not "correct" it.
14. The inactive icon (23 dp) is 1 dp **larger** than the active one (22 dp) —
    a reference quirk, reproduced deliberately.
15. `group-hover:bg-white/40` → not implemented (C12, no hover on touch).

#### 8.18.5 Impact on Habits, Tasks and Insights (§22 — identified and verifiable)

- `LiquidGlassBottomBar` / `LiquidGlassTab`: **one** call site each
  (`MomentumNavGraph.kt:223`, `:225`); `Destinations` is the single source of the
  four tabs; `showBar` (`:206`) is the only gate. No other screen in the app
  composes a bar.
- **All four destinations receive the identical Stitch nav visual**: pill shape,
  16/16/20 dp margins, 64 dp height, `SpaceAround` distribution, 22/23 dp icons,
  `MomentumLabelMicro` labels with Bold/SemiBold, the `textPrimary` /
  `textSecondary` ink change and the active Home capsule. Because there is one
  implementation, the four **cannot** desynchronise. This is the human's explicit
  instruction: *"Do NOT preserve the current Momentum bottom-navigation styling
  merely because it is shared."*
- **Layout delta:** `innerPadding.bottom` grows **8 dp** on Home, Habits, Tasks
  and Insights; every screen's bottom-most content moves up 8 dp. Tab slot widths
  change from `width / 4` (74 dp at 360 dp) to `SpaceAround` over 56/62/56/56 dp
  minimums (≈72.5 dp effective at 360 dp) — the tab rhythm is visually unchanged
  while the bar's own edges move in 4 dp per side.
- **Behaviour delta: none.** Nav3 `backStack.selectTab`, the `showBar` gate,
  `Scaffold`, `contentWindowInsets`, the four routes and every
  `showCreateItem = true` call site are untouched. `QuickAddTest`'s two
  `MomentumNavGraph.kt` assertions (`count(nav, "showCreateItem = true") >= 4`
  and `nav.contains("if (showCreateItem)")`) still hold.
- **Reviewer obligation:** on device, visit Home → Habits → Tasks → Insights →
  Home and confirm the bar renders identically on all four; confirm no bar
  appears on Onboarding, Calendar, Settings, Themes, create/edit/detail or the
  Create Item sheet; confirm Habits' FAB and Home's Quick Add both still clear the
  taller bar; confirm every tab target is ≥ 48 dp in both axes at font scale 2.0.

#### 8.18.6 Test impact (A1)

**Verified first:** **no** existing test asserts on the navigation bar. There are
zero references to `LiquidGlassBottomBar`, `LiquidGlassTab`, `nav_home`,
`RoundedCornerShape(26` or `bottomBar` in `src/test` or `src/androidTest`; the
only file that reads `MomentumNavGraph.kt` is `QuickAddTest`, for `showCreateItem`.
So nothing existing is invalidated by A1 and nothing needs rewriting for it.

New coverage:

1. **New unit test `BottomNavStitchVisualTest`**
   (`src/test/java/com/momentum/app/presentation/components/`), source-reading in
   the established `HomeAtmosphereAssetsTest` / `QuickAddTest` style, reading
   `LiquidGlass.kt` and `MomentumNavGraph.kt`, asserting:
   - `CircleShape` present and `RoundedCornerShape(26.dp)` **absent** from `LiquidGlass.kt`
   - `heightIn(min = NavBarHeight)` present with `NavBarHeight = 64.dp`
   - the bar's padding is `horizontal = MomentumSpacing.standard` with **no**
     `vertical =` padding
   - `Arrangement.SpaceAround` present and `Modifier.weight(1f)` **absent** from the tab
   - `defaultMinSize` present with `minWidth = 62.dp` / `56.dp` and
     `MomentumHeights.controlMin` as the height for **both** states
   - the two capsule stops derive from `c.auroraBlue` / `c.auroraLavender` /
     `c.surface` and carry `0.75f` / `0.85f`
   - `MomentumLabelMicro` is the tab label style, with `Bold` when selected
   - **no** `backdrop-filter`, `RenderEffect`, `Modifier.blur` or
     `graphicsLayer(renderEffect …)` anywhere in `LiquidGlass.kt`
   - `1.06f` **absent** from `LiquidGlass.kt` (the selected scale is gone)
   - `role = Role.Tab` and `contentDescription = label` retained
   - `GlassLevel.HERO` still the bar's level
   - in `MomentumNavGraph.kt`: `start = MomentumSpacing.standard`,
     `end = MomentumSpacing.standard`, `bottom = MomentumSpacing.screen`,
     `WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)` retained,
     `selectTab` and the `showBar` gate unchanged (count assertions)
2. **New androidTest bounds assertions** in `HomeCompositionTest` (or a sibling
   `BottomNavLayoutTest`): the `bottomBar` slot measures
   `navigationBars` inset + `MomentumSpacing.screen` + `NavBarHeight`, and each
   tab's touch target is ≥ 48 dp on **both** axes at font scale 1.0 and 2.0.
   Bounds only — no pixel claims (`adb` is unavailable; §8.16).
3. **`QuickAddTest`: unchanged.** Its FAB material/size/glyph/placement/label
   assertions stand, and its `EmptyDayBottomReserve` assertion still holds
   (§8.18.3).
4. **Contrast sweep**: the four nav bands listed in §8.15 join the re-run.

### 8.19 A2 — expanded motion, one shared clock, total reduced-motion nulling (human ruling §4.7 r2)

**Instrument.** §8.11's C11 rejection and ADR-48 §6 are **withdrawn** for Home.
This section is the specification; ADR-48 Amendment Note 6 is the record.

#### 8.19.1 The rationale that replaces ADR-48 §6

ADR-48 §6 said *"Motion is change-triggered, not wall-clock"* and forbade any
permanent infinite animation on Home. Its premise no longer holds: §6 was written
when Home's Aurora was static and its progress card carried no atmosphere of its
own, so a permanent loop would have been pure battery drain with nothing to
animate. The approved Stitch composition changed that premise — the progress
card's two aura blobs, the glowing primary FAB and the drifting Aurora field
*are* the design, and the human ruled that the reference's ambient motion is
required.

The replacement is **narrower, not wider**. What §6 actually protected — reduced
motion, no invented motion, and no motion that costs composition — survives
intact as:

> Home may run **ambient** loops — slow (≥ 2.2 s), tiny-amplitude, purely
> decorative, driven from **one** `rememberInfiniteTransition`, consumed in the
> **draw/layer phase only**, never owned by a `LazyColumn` item, and **entirely
> absent** under reduced motion (the transition itself is never composed).

**Change-triggered motion stays change-triggered.** `animateItem` reflow, the ring
sweep, `check-pop`, press compression, the 900 ms entrance clock, the milestone
transient and its single haptic are all unchanged.

#### 8.19.2 Per-loop specification

**M1 — Aurora drift (the human's "Aurora subtle movement").**
`code.html`'s `@keyframes aurora-float` (8 px / 6 s / `translateY(-8px) scale(1.04)`
/ opacity 0.85↔1.0) is **defined but never bound** — verified: `aurora-float`
occurs exactly once in the file, inside the `@keyframes` block, and no element
carries it. Binding it would render a movement the approved design does not show.
**Ruling: leave `aurora-float` unbound.** The human's requirement is instead met
by Momentum's **existing** Aurora engine: `HomeAtmosphere.kt:257` changes
`animated = false` → `animated = true`, which enables `AuroraBackground`'s own
drift — `MomentumMotion.ambient` = **6000 ms**, an *exact* match to
`aurora-float`'s 6 s; a seamless `sin`/`cos` oscillation of the blob centres by
±3 % of width / ±2.5 % of height (≈ ±8 px at a 320 dp viewport, i.e. the
reference's own 8 px amplitude); executed in `drawWithCache.onDrawBehind` with the
brushes built once; and nulled by `AuroraBackground` itself under reduced motion
(`val animate = animated && !reduceMotion`). **No new token, component, keyframe
or binding.** This is the required consequence of ruling 2 and amends the §8.3
hierarchy, §8.15's "Aurora drift stays off on Home", and ADR-47's static-Aurora
clause for Home.

**M2 — `aurora-glow-pulse` (6 s) on the progress card's two aura blobs.**
`code.html`: blob A `absolute -top-12 -right-12 w-36 h-36 bg-primary/30 rounded-full
blur-2xl anim-aurora-blob`; blob B `absolute -bottom-10 -left-10 w-32 h-32
bg-secondary/25 rounded-full blur-2xl anim-aurora-blob`; keyframe
`0%,100% { opacity: .35; filter: blur(32px) } 50% { opacity: .6; filter: blur(40px) }`
over 6 s `ease-in-out`; both bound (3 occurrences of `anim-aurora-blob`).

```kotlin
// HomeCards.kt — HomeProgressCard's background box, which must be clipToBounds()
// so the negative offsets are cropped exactly as the reference's overflow-hidden crops them.
private val BlobA = 144.dp   // w-36      private val BlobARadius = 32.dp   // blur(32px)
private val BlobB = 128.dp   // w-32      private val BlobBRadius = 32.dp
private val BlobRadiusDelta = 8.dp       // 32px -> 40px

// each blob:
Modifier.drawWithCache {
    val cached = Brush.radialGradient(
        colors = listOf(c.primary.copy(alpha = 0.30f), Color.Transparent),  // bg-primary/30
        center = Offset.Zero,
        radius = BlobARadius.toPx(),
    )
    onDrawBehind {
        val breath = 0.5f - 0.5f * cos(ambientPulse * TAU)   // 0..1, seamless
        val r = BlobARadius + BlobRadiusDelta * breath
        canvas.save(); canvas.translate(cx, cy)
        canvas.scale(r / BlobARadius)                        // 32dp -> 40dp
        canvas.drawCircle(
            brush = cached,
            radius = BlobARadius.toPx(),
            center = Offset.Zero,
            alpha = lerp(0.35f, 0.60f, breath),
        )
        canvas.restore()
    }
}
```
**Why this is the correct Compose equivalent of `filter: blur()`:** a larger CSS
blur radius *is* a wider, softer falloff, which is exactly what a wider radial
gradient radius is. Scaling the canvas scales the cached shader's geometry **and**
its falloff together, so the animated softness is obtained with **one** brush
allocation and **zero** per-frame allocation. **`filter: blur()` itself remains
not adopted** (ADR-49 §4, §8.5, C6) — the blobs have always been *painted* soft;
the pulse animates that softness. Blob B is identical with
`c.secondary.copy(alpha = 0.25f)` (`bg-secondary/25`) and `128.dp`.

**M3 — `fab-glow` (3 s) on `MomentumFab`.**
`code.html`: `0%,100% { box-shadow: rgba(99,102,241,.32) 0 10px 25px -4px,
rgba(6,182,212,.22) 0 4px 12px, rgba(255,255,255,.95) 0 1px 2px inset } 50% { …(.55)
0 14px 30px 0, …(.45) 0 6px 18px, rgba(255,255,255,1) 0 1px 3px inset }` over 3 s
`ease-in-out`.

`MomentumFab` gains one additive parameter `glowPulse: Boolean = false`. When it is
`true` **and** motion is allowed, the component's *existing* single shadow animates:

```kotlin
val breath = 0.5f - 0.5f * cos(fabPulse * TAU)
val elevation = lerp(MomentumGlass.heroElevation, MomentumGlass.heroElevation + 4.dp, breath)
val spotAlpha = lerp(0.40f, 0.55f, breath)
.shadow(elevation, CircleShape,
        ambientColor = Color.Black.copy(alpha = 0.30f),      // unchanged
        spotColor = c.primary.copy(alpha = spotAlpha))
```
`+4.dp` is the reference's own `10px → 14px` offset rise; `0.40 → 0.55` are the
reference's own spot alphas. Material, `MomentumFabSize`, glyph, hairline,
`glassPress`, `Role.Button`, label and placement are **unchanged**.
`MomentumFab` is shared with `HabitsScreen`, so **`glowPulse` defaults to `false`
and only Home passes `true`** — Habits' FAB is byte-identical. This is an A2
amendment to §8.9's "`MomentumFab` unchanged", forced by the binding ruling, and
it is additive and default-preserving in exactly the shape §8.17 item 5 already
approves for `ProgressOrb(arcBrush)` and `CompletionButton(accent)`.

**M4 — `bolt-pulse` (2.2 s) on the `verified` glyph in `Finished`.**
`code.html`: `0%,100% { transform: scale(1); filter: drop-shadow(0 0 2px rgba(16,185,129,.4)) }
50% { transform: scale(1.15) rotate(-4deg); filter: drop-shadow(0 0 6px rgba(52,211,153,.8)) }`
over 2.2 s `ease-in-out`, bound to `<span class="… anim-bolt">verified</span>`.

In `HomeCards.kt`'s `HomeSectionHeader`, the `MomentumIcons.verified` `Icon` gets
`Modifier.graphicsLayer { scaleX = s; scaleY = s; rotationZ = rz }` with
`s = 1f + 0.15f * breath`, `rz = -4f * breath`, `breath = 0.5f - 0.5f * cos(boltPulse * TAU)`,
`boltPulse` arriving as a plain `Float` parameter from §8.19.3's single clock.
The `drop-shadow` half of the keyframe is **not** implemented: a per-frame
software shadow pass on a 20 dp glyph is real cost for an effect the `success`-tinted
glyph already carries (recorded deviation 3).
**No `rememberInfiniteTransition`, no `animate*AsState`, no mutable state and no
`LaunchedEffect` exists inside any `LazyColumn` item.** `graphicsLayer`'s block
reads a `State<Float>`, so only that node's layer is invalidated per frame and the
item never recomposes.

**M5 — FAB interaction scale/rotation.** The reference's FAB is
`hover:scale-105 active:scale-95` with the glyph's `group-hover:rotate-90`, all
`0.2s cubic-bezier(.34,1.56,.64,1)`.
*Scale* is already delivered by the existing `glassPress` (0.97 spring, nulled
inside the modifier under reduced motion); §8.9 pins it and `QuickAddTest` pins its
presence. The reference's `0.95` vs Momentum's `0.97` is recorded as immaterial —
same mechanism, same role. **No change.**
*Rotation* is `hover`-only in the reference (C12), but the human ruled "FAB
interaction **scale/rotation**". Adopted as the **press** analogue of hover,
using the reference's own magnitude and role: a second additive parameter
`rotateGlyphOnPress: Boolean = false` applies
`Modifier.rotate(rotation)` to the `add` glyph with
`rotation = animateFloatAsState(if (pressed && !reduceMotion) 90f else 0f,
tween(MomentumMotion.fast, easing = MomentumMotion.easeOut), label = "fabGlyphRotation")`
— the reference's `0.2 s` maps to the existing `fast` token (180 ms) and its
easing to the existing `easeOut`; no new easing token is added. Home passes `true`;
Habits does not. Nulled under reduced motion like every other decorative spec.

**M6 — `flame-pulse`: not implemented.** Verified: `.anim-flame` occurs exactly
once in `code.html` — the class definition — so the keyframe is **unbound in the
reference as well as** belonging to the streak chip removed by §4.7 r3 / §7. No
element to decorate and no component to build.

**M7 — `aurora-float`: left unbound.** See M1.

**M8 — unchanged:** `fade-slide-up` entrance (§8.11), `progress-fill` → the
existing `ProgressOrb` spring, `check-pop` → the existing `CompletionButton`
spring pop, card/control press compression → `glassPress`, the two `:hover`
families → n/a (C12).

#### 8.19.3 One clock, and a reduced-motion path with no transition at all

`TodayScreen` composes **exactly one** `rememberInfiniteTransition(label = "homeAmbient")`
and derives three loops from it:

| derived value | period / easing | consumed by |
|---|---|---|
| `ambientPulse` 0→1 | `MomentumMotion.ambient` (6000) · `LinearEasing` | M1's `AuroraBackground` (via `animated = true`) and M2's two blobs |
| `fabPulse` 0→1 | `MomentumMotion.fabGlow` (3000) · `LinearEasing` | M3 |
| `boltPulse` 0→1 | `MomentumMotion.verifiedPulse` (2200) · `LinearEasing` | M4, M5 |

Three `LinearEasing` 0→1 loops on different periods from **one** transition is one
frame driver and one coroutine instead of three; per-item
`rememberInfiniteTransition` instances would be created and destroyed as
`LazyColumn` items scroll in and out, and M4's header *is* such an item.

**Reduced motion is structural, not scaled:**

```kotlin
val ambient = if (reduceMotion) null else rememberInfiniteTransition(label = "homeAmbient")
// every consumer:  ambient?.let { …animateFloat(…) }?.value ?: 0f
```

`0f` is each keyframe's own `0%/100%` resting value — `aurora-glow-pulse` at
`opacity 0.35` / `blur 32px`, `fab-glow` at its low shadow, `bolt-pulse` at
`scale 1` / `rotation 0` — so every resting state is a valid, still-correct frame
and nothing jumps. Under reduced motion **no transition, no `LaunchedEffect` and
no coroutine is created**; `MomentumFab` receives `glowPulse && !reduceMotion` and
`rotateGlyphOnPress && !reduceMotion`; `ProgressOrb` keeps
`glowEnabled = milestoneGlow && !reduceMotion`; `AuroraBackground` nulls its own
phase; the entrance clock still resolves to `1f` immediately. **Nothing is
shortened — every loop is absent.**

#### 8.19.4 Per-frame cost and mitigation (§18)

| loop | phase | per-frame allocation | recomposition | bound |
|---|---|---|---|---|
| M1 Aurora drift | `drawWithCache.onDrawBehind`, 3 `drawCircle` with pre-built brushes | none | none | draw-phase only; already runs app-wide on other screens |
| M2 blobs (×2) | `drawWithCache`, 2 `drawCircle` + 2 `canvas.scale` | none — the gradient is **never rebuilt** | none | `canvas.scale` is free; the cached shader scales with it |
| M3 FAB glow | `Modifier.shadow` layer redraw | none | none | the most expensive of the three; one fixed 56 dp circle outside the list |
| M4 bolt | one `graphicsLayer` block re-run | none | none | layer-property update on a 20 dp glyph |

Required mitigations:

1. **One** `rememberInfiniteTransition` for the whole screen (§8.19.3) — never per
   item, never per card.
2. **No animation is owned inside a `LazyColumn` item.** The clock's values are
   passed down as plain `Float` parameters and consumed inside `graphicsLayer`;
   zero `rememberInfiniteTransition`, `animate*AsState`, `mutableStateOf` or
   `LaunchedEffect` inside `HomeActivityCard`, `HomeSectionHeader` or any
   `item`/`items` block. M2's blobs and M3's FAB are `LazyColumn`-external by
   construction.
3. **Draw-phase only.** Every loop is consumed in `drawWithCache`, `graphicsLayer`
   or `Modifier.shadow`; none is read by a composable's body, so no loop
   recomposes its subtree.
4. **§8.14's "scrolling recomposes nothing" is preserved** — and is now a stated
   guarantee rather than an accident. A fling touches no loop's inputs: the blobs
   read a `State<Float>` owned outside the list, the FAB and the atmosphere are
   outside the list, and because no list item recomposes, no list item's layer can
   be invalidated by a scroll. The three loops add **no** new scroll observer — no
   `NestedScrollConnection`, no `derivedStateOf`, no `onGloballyPositioned`, no
   scroll read — so `HomeSectionMotionTest`'s token blacklist still holds verbatim.
5. `HeroScrim` keeps its `drawWithCache`; `ProgressOrb` keeps its
   `rememberGlowPhase` gating (`glowEnabled && sweep > 0.001f`).
6. `MomentumMotion` gains **exactly two** tokens: `fabGlow = 3000` and
   `verifiedPulse = 2200`, each with a one-line derivation comment. M1/M2 reuse
   `MomentumMotion.ambient` (6000, an exact match). With §8.11's `enter = 900`,
   `Motion.kt` gains three tokens in total. All three ambient loops use
   `LinearEasing` (the reference's `ease-in-out`), so **no new easing token**.

#### 8.19.5 The two now-invalid green assertions — corrected, not deleted

**A. `HomeSectionMotionTest.Home runs no permanent infinite animation` (lines
119-127) is replaced by** `Home's only infinite motion is the three approved
ambient loops`, restated with the narrower rule and positive assertions for the
two rejections that are *not* about wall-clock:

```kotlin
@Test
fun `Home's only infinite motion is the three approved ambient loops`() {
    // ADR-48 Amendment Note 6 (human ruling 2026-10-01 §4.7 r2) superseded ADR-48 §6's
    // blanket "no permanent infinite animation": the approved Stitch composition puts a live
    // Aurora in the progress card and a glowing FAB over the atmosphere, so three *ambient*
    // loops are required. The replacement rule is narrower, not wider: slow (>= 2.2 s),
    // tiny-amplitude, one shared clock, draw-phase only, absent under reduced motion.
    val cards = read("src/main/java/com/momentum/app/presentation/screens/today/HomeCards.kt")
    assertEquals("exactly one ambient clock for the whole screen", 1, count(screen, "rememberInfiniteTransition"))
    assertEquals(
        "the clock is created only when motion is allowed",
        1,
        count(screen, "if (reduceMotion) null else rememberInfiniteTransition"),
    )
    // The three approved periods are pinned to code.html's own values.
    assertTrue("aurora-glow-pulse is 6 s", motion.contains("const val ambient = 6000"))
    assertTrue("fab-glow is 3 s", motion.contains("const val fabGlow = 3000"))
    assertTrue("bolt-pulse is 2.2 s", motion.contains("const val verifiedPulse = 2200"))
    // The atmosphere keeps its own drift (the "Aurora subtle movement" ruling); it is still
    // nulled inside AuroraBackground, so the ambient gate stays at exactly one branch.
    assertTrue("aurora drift is enabled on Home", atmosphere.contains("animated = true"))
    assertEquals("the Aurora snap keeps its one reduce-motion branch", 1, count(atmosphere, "if (reduceMotion) {"))
    assertTrue("the Aurora change is still a one-shot tween", atmosphere.contains("MomentumMotion.emphasis"))
    assertTrue("reduced motion snaps instead of tweening", atmosphere.contains("snap()"))
    // Retained rejections, now positively asserted.
    assertFalse("flame-pulse is unbound in code.html and belongs to the removed streak chip", progress.contains("flame-pulse"))
    assertFalse("aurora-float is unbound in code.html and stays unbound", screen.contains("aurora-float"))
    assertFalse(
        "per-row entrance stays rejected: no list item receives an entrance phase",
        cards.contains("entranceProgress"),
    )
}
```

**Rationale that replaces ADR-48 §6's blanket rule** (this is the text that also
goes into the ADR): §6 forbade *all* permanent infinite animation because Home had
nothing to animate — a static Aurora, a ring that already expressed state through
intensity, and a FAB whose only motion was a press. The approved Stitch
composition gives Home three decorative surfaces that the design *is*: the two
aurora blobs inside the progress card, the primary FAB's glow, and the ambient
Aurora field itself. A blanket ban would now forbid reproducing the approved
design. What §6 was actually protecting is preserved verbatim in the narrower
rule — every loop is slow (2.2 s+), tiny-amplitude, purely decorative, drawn from
a single shared clock in the draw phase only, never owned by a list item, and
**entirely absent** under reduced motion — and the rejections that were never about
wall-clock (per-row entrance, `flame-pulse`, `aurora-float`) remain rejected and
now carry positive assertions.

**B. `HomeAccessibilityTest` line 211, `assertFalse("no shimmer currency on Home",
screen.contains("rememberInfiniteTransition"))`, is no longer a true statement about
Home** — the screen legitimately owns exactly one transition. The invariant it
protected is preserved and re-anchored to the composable it was actually about
(the skeleton must not shimmer), so nothing is deleted:

```kotlin
assertFalse(
    "the skeleton must not shimmer",
    skeletonBody.contains("rememberInfiniteTransition") || skeletonBody.contains("animate"),
)
```
where `skeletonBody` is `code.substringAfter("internal fun TodayLoadingSkeleton(")`
trimmed to that function, so the existing "no shimmer" coverage survives **and**
`HomeSectionMotionTest` now owns the screen-level "exactly one transition" rule.

**C. Two further assertions of the same kind, corrected for consistency:**
`HomeSectionMotionTest` line 124's `atmosphere.contains("animated = false")` and
`HomeAccessibilityTest` line 114's identical assertion both become
`animated = true`, and the accessibility test gains a companion assertion that the
drift is still nulled —
`assertTrue("AuroraBackground nulls its own phase under reduced motion", aurora.contains("val animate = animated && !reduceMotion"))`
— with a new `private val aurora = read("src/main/java/com/momentum/app/presentation/aurora/MomentumAurora.kt")`
field. No other assertion in either file is affected.

#### 8.19.6 Additional A2 test changes

- **`QuickAddTest`** — §8.15 had planned to add *"the reference's permanent glow
  pulse is absent"*. That clause is **inverted**. In
  `the shared FAB is circular with one material and the prominent glyph` add:
  - `assertTrue(fab.contains("glowPulse: Boolean = false"))`
  - `assertTrue(fab.contains("rotateGlyphOnPress: Boolean = false"))`
  - `assertTrue("Home opts into the reference glow", codeOf(today).contains("glowPulse = true"))`
  - `assertTrue("Home opts into the reference press rotation", codeOf(today).contains("rotateGlyphOnPress = true"))`
  - `assertFalse("Habits must not inherit Home's opt-in", codeOf(habits).contains("glowPulse = true"))`
  - `assertTrue("both opt-ins are reduced-motion gated", fab.contains("&& !reduceMotion"))`
  - and in `Home always shows the FAB and the empty state carries no CTA`, add
    `assertTrue("the FAB's ambient motion must not re-add a visibility transition", !code.contains("AnimatedVisibility"))`
    — already covered; **no change** to that test.
- **`HomeSectionMotionTest`** — add `the ambient loops stay out of the list items`:
  read the new `HomeCards.kt` and assert `count(cards, "rememberInfiniteTransition") == 0`
  and `count(cards, "animateFloatAsState") == 0`, and that `boltPulse` /
  `ambientPulse` arrive as plain `Float` parameters.
- **`HomeAtmosphereAssetsTest`** — §8.15's planned negative assertion that the
  `fab-glow` pulse stays absent is **inverted**; the `flame-pulse` and per-row
  entrance negatives stay. Replace the `fab-glow` clause with positive assertions
  on the new blobs: `HomeCards.kt` carries `0.30f` / `0.25f` blob alphas,
  `32.dp` + `8.dp` radii and `clipToBounds()`.
- **`HomeProgressStateTest`** — unchanged; the blobs add no state and no semantics.

#### 8.19.7 Recorded deviations

1. `filter: blur(32px) → blur(40px)` is expressed as an animated radial-gradient
   radius (32 → 40 dp) under `canvas.scale`, **not** as a blur filter — blur
   remains the documented non-adoption (ADR-49 §4, C6). Same appearance,
   different mechanism, no new API and no fragile dependency.
2. `fab-glow`'s two stacked shadows and its white inset collapse into Compose's
   single `shadow()`; elevation 12 → 16 dp and spot alpha 0.40 → 0.55 carry the
   reference's own numbers.
3. `bolt-pulse`'s `drop-shadow` half is not implemented (per-frame software shadow
   on a 20 dp glyph).
4. `aurora-float` is left unbound (defined-but-unbound in the reference); the
   human's "Aurora subtle movement" is delivered by the existing Aurora drift.
5. `flame-pulse` is not implemented (unbound in the reference; streak chip removed).
6. `bolt-pulse` is applied inside a `LazyColumn` item through `graphicsLayer` —
   a recorded deviation from the literal *"never animate anything inside a
   `LazyColumn` item"*. The alternative, moving the `Completed` header out of the
   list, would break ADR-47 AN5 §5/§6's scroll architecture, which §4.2 B and
   §8.2 forbid. The **intent** of the constraint is fully met: no per-item
   animation instance, no per-frame recomposition, no scroll-driven work, and the
   transition exists in exactly one place.
7. The FAB press rotation is triggered by **press**, not hover, and reuses
   `MomentumMotion.fast` (180 ms) and `MomentumMotion.easeOut` for the reference's
   `0.2s cubic-bezier(.34,1.56,.64,1)`.
8. `MomentumFab` gains two additive, default-`false` parameters; **Habits' FAB is
   unchanged.**
9. `HomeProgressCard` gains the two aura blobs that §8.7's printed hierarchy
   omitted while §8.11 referenced them; §4.7 r2 requires them. They are
   non-interactive, carry no semantics, and add no text band to the contrast sweep.
10. `MomentumMotion` gains exactly two tokens (`fabGlow`, `verifiedPulse`); all
    three ambient loops use `LinearEasing`, so no new easing token is added.

---

### 8.20 ISSUE-020 contrast remediation plan — REVISED to Home-scoped (Architect, 2026-10-02; §4.11 outranks the prior §8.20; Developer NOT yet authorized)

**Authority.** §4.10 (A–F), §4.11 (Home-scoped revision; binding, outranks the prior §8.20's E1/E2/E3), §10.11 (authoritative: 16 FAIL / 9 PASS / 1 borderline of 26), §4.9 (frozen background). §6 (RELEVANT FILES) stays valid except as amended by §8.20.J below. The prior §8.20's global enablers (E1 wash restructure, E2 `HueWashMidAlpha`, E3 global fill raises) are **withdrawn** as global changes; the approved per-band ink decisions, the ring-% band-model correction and the ring-arc exemption are **retained** and re-scoped.

**Method note.** The official `measure020.js` is superseded (§10.11); corrected runs live only in temp (`measure020-reviewer.js`, `attainable*.js`, Reviewer-owned) and are NOT regenerated by this pass. Every figure below is from §10.11 or from the prior §8.20's "X2" winning configuration, and is carried forward **only where the Home-scoped mechanism reproduces that configuration's effective treatment on the Home surface exactly** (same fill, same wash, same ink). Where the mechanism differs from X2, no new ratio is invented; the band is marked "re-verify." No scrim, artwork, dissolve, geometry, threshold, dependency, or motion change is proposed anywhere in this plan.

#### A. Governing principle

ISSUE-020 is a **foreground/material contrast** issue on a FROZEN background (§4.9/§4.11). The prior plan passed everything by raising three shared fills (`heroFill`/`cardFill`/`controlFill`) and restructuring the shared wash globally — rejected because that blast radius reaches Habits, Tasks, Insights and Settings and flattens the Aurora perceptibility that the accepted background relies on (§4.11 CHANGE 1).

The replacement strategy is **Home-scoped opt-in**: the shared `Modifier.liquidGlass` and the shared `EmptyState`/`CompletionButton` primitives gain *purely additive, default-null* parameters. When the new parameters are omitted (every existing caller omits them), the rendered output is byte-identical to today; when Home passes a value, only Home changes. No `Tokens.kt` fill constant changes; no global token changes; no Home-only fork of the glass implementation. Two regions — the bottom navigation and the completion control's idle ring — are **shared components with no Home-only path** and are handled under §E as shared blockers, not silently changed.

Ink remains a last-resort, per-band decision (§4.10.4, §4.11 CHANGE 4): exactly the bands the human approved move to `c.textPrimary` (Scheduled Today, verified glyph, the two breakdown dots) plus one not-yet-approved band identified below (the empty-state body) that cannot be fixed by material because its measured backdrop is the raw atmosphere, not the card fill.

#### B. Per-region remediation table

Every §10.11 FAIL row (plus the one borderline) is classified and remediated. "Target" figures are the prior X2 values **only where the Home-scoped mechanism is the same effective treatment** (same fill + same wash + same ink at Home); otherwise the band is marked re-verify with the floor stated.

| Region (§10.11 L/D, floor) | A — class | Current expression | Proposed mechanism | Scope | Visual impact | Why smallest justified |
|---|---|---|---|---|---|---|
| Pending row title+meta (4.59 / 1.98, 4.5) | text | meta `c.textSecondary` over `hue=null` CARD glass @ `cardFill 0.55` (`HomeCards.kt:569/632/644`) | row `liquidGlass(..., hue=null, fillAlpha=0.90f)` → two-layer solid fill + fading highlight | Home | card rows read more solid at Home; ink hierarchy unchanged; Atmos still reads through chrome/atmosphere/ring | fill-raise alone collapses Dark (prior E1 rationale: "pending row 1.79"); two-layer alone leaves fill at 0.55 — both needed together (target 5.59/6.00, X2) |
| Completed row title (4.33 / 2.03, 4.5) | text | `c.textSecondary` over `hue=null` CONTROL glass @ `controlFill 0.45` (`HomeCards.kt:571`) | row `liquidGlass(..., fillAlpha=0.90f)` → two-layer | Home | same | same (target 5.59/6.00, X2) |
| Empty-state body (3.65 / 2.77, 4.5) | text | `c.textSecondary` `MomentumBadges.kt:261` over `surface @ cardFill*0.7`; **measured against raw backdrop, not the fill** | `EmptyState` gains `bodyColor: Color?=null`; Home passes `c.textPrimary` | Home (additive shared param) | empty-state body darkens in Light / lightens in Dark; title already `textPrimary` | the band's backdrop is the raw atmosphere (per `attainable-rest.js`), so material raise is a no-op and ink is the only lever; re-verify ≥4.5 (textPrimary arithmetic ADR-49 AN3) |
| Scheduled Today meta (3.24 / 2.45, 4.5) | text | `statusInk = labelColor ?: c.textSecondary`; Pending header passes no `labelColor` (`TodayScreen.kt:584`, `HomeCards.kt:491/527`) | `labelColor = c.textPrimary` at Pending header call site (existing param) | Home | heading meta matches its own title and `Finished`; §13.1/AN3 reversed (CHANGE 4) | ink only; backdrop frozen; target 4.57/8.72 (X2) |
| Ring percentage (2.83 / 1.81, 4.5) | text | `c.textPrimary` (`HomeCards.kt:378/384`) measured against a **track-composited** backdrop | band-model correction only — backdrop is the card, not the track (8.20.3-A/R5 geometry); expression unchanged | measurement | zero code change | ink already `textPrimary`; the track-composited number is geometrically unsatisfiable; Reviewer confirms (target 4.78/8.44, X2) |
| Nav inactive label (4.92 / 3.34, 4.5) | text | `c.textSecondary` over shared HERO bar @ `heroFill 0.68` (`LiquidGlass.kt:516/520`) | **shared blocker** — see §E | shared | n/a (blocker) | bar is one `Scaffold.bottomBar` call site across 4 screens; no Home path (target, if approved, 5.56/6.28, X2) |
| Nav inactive icon (4.92 / 3.34, 4.5) | text-equiv glyph | `iconTint = c.textSecondary` (`LiquidGlass.kt:509/511`) | **shared blocker** — see §E | shared | n/a (blocker) | same |
| Progress-card `textPrimary` (8.51 / 4.52 rest / 3.99 scrolled, 4.5) | text (borderline) | `textPrimary` over `hue=auroraLavender` HERO wash @ `heroFill 0.68`, `HueWashMidAlpha 0.5` | progress card `liquidGlass(..., hue=lavender, fillAlpha=0.85f)` + `HueWashMidAlpha 0.5→0.8` (Home-only) | Home | card slightly more solid; Aurora still reads through at 0.85 | both knobs reproduce X2's progress-card pass (8.44/4.78); scrolled case closed |
| Ring arc (1.00 / 1.00, 3.0) | **decorative** | `arcBrush = success→auroraBlue→auroraViolet` (`HomeCards.kt:371-373`) | exempt (5 conditions, §F); KDoc corrected | none | zero | arc is redundant with the ≥4.5 `%` text; forcing 3:1 forces an ink arc (L≤0.122/0.057) reversing the approved gradient |
| textMuted dot (1.62 / 1.20, 3.0) | non-text | `.background(c.textMuted, CircleShape)` `HomeCards.kt:426` | `c.textPrimary` | Home | dot loses muted grey; labels carry the relation (CHANGE 4) | ink only; target 4.78/8.44 (X2) |
| Ring track (3.01 / 2.50, 3.0) | non-text | `textPrimary @ TrackAlpha 0.55` (`ProgressOrb.kt:126/236`) | `TrackAlpha 0.55→0.7` | Home | track slightly darker/lighter; ink unchanged | `ProgressOrb` has a single production caller (Home); alpha only; target 3.24/4.30 (X2) |
| Success dot (2.23 / 3.03, 3.0) | non-text | `.background(c.success, CircleShape)` `HomeCards.kt:410` | `c.textPrimary` | Home | dot loses green; ring gradient keeps `success` (CHANGE 4) | ink only; target 4.78/8.44 (X2) |
| Empty-ring boundary (1.68 / 1.24, 3.0) | non-text | idle border `textSecondary @0.4` (`CompletionButton.kt:151`) over `White@0.10/surface@controlFill` ring | **shared blocker** — see §E | shared | n/a (blocker) | idle ring is the shared completion toggle; no Home path (target, if approved, 3.42/3.65, X2) |
| Check glyph on emerald (3.82 / 12.14, 4.5) | state indicator | `c.onPrimary` on `success → success⊕mint@0.5` (`CompletionButton.kt:130-131/204`) | gradient window `[lerp(success,auroraMint,0.30f), lerp(…,0.34f)]` | Home | near-flat mint-emerald; check stays `onPrimary` | `CompletionAccent.SUCCESS` has a sole production caller (Home); target 4.84/12.14 (X2) |
| Emerald fill boundary (2.13 / 2.54, 3.0) | non-text | same gradient vs CONTROL row card | same gradient + row `fillAlpha=0.90f` | Home | as above + solid completed row | window proof 8.20.3-D; target 3.09/7.49 (X2) |
| Capsule boundary (1.11 / ~5.6, 3.0) | non-text | `Color.White @ CapsuleBorderAlpha` (`LiquidGlass.kt:484`) | **shared blocker** — Light border → `textPrimary` — see §E | shared | n/a (blocker) | white hairline invisible on light capsule; mirrors generic hairline (target, if approved, 8.00/≥5.6, X2) |
| Verified glyph (2.34 / 3.06, 3.0) | state indicator | `accent = c.success` → `tint = accent` (`TodayScreen.kt:630`, `HomeCards.kt:511`) | `accent = c.textPrimary` | Home | emerald bolt becomes ink; shape carries state (CHANGE 4) | glyph-only tint; target 4.57/8.72 (X2) |

#### C. LiquidGlass architecture

The single architectural change is one additive, default-`null` parameter on the shared modifier:

```kotlin
fun Modifier.liquidGlass(
    shape: Shape,
    level: GlassLevel = GlassLevel.CARD,
    hue: Color? = null,
    fillAlpha: Float? = null,       // NEW — Home-scoped opt-in
): Modifier
```

- **`fillAlpha == null` (every existing caller):** byte-identical to today — `effectiveFillAlpha = level.fillAlpha`, the legacy single `topLight → fill → fill` vertical gradient, unchanged hairline/shadow. All ~16 existing `.liquidGlass(` call sites omit it, so Habits/Tasks/Insights/Settings/Calendar/Activities/creation/detail/onboarding/themes are untouched.
- **`fillAlpha != null` (Home only):** `effectiveFillAlpha = fillAlpha`; the non-hue wash becomes a **two-layer** treatment — a solid `c.surface.copy(alpha = effectiveFillAlpha)` underlayer everywhere, with the top streak drawn as a fading `Brush.verticalGradient(0f to topLight, 0.38f to Color.Transparent)` — plus the unchanged hairline/shadow. This reproduces, on Home alone, the prior E1 (two-layer) **and** the prior E3 fill raise, without touching `Tokens.kt` or any other screen.
- The hue branch (progress card) is unaffected structurally: `fillAlpha` merely overrides the three stops' fill alpha (`level.fillAlpha → fillAlpha`), which is how the progress card gets `0.85f` where the rejected global `heroFill 0.85` would have applied everywhere.
- **Why this is not a brittle fork:** there is still one glass implementation and one modifier. The only Home-specific behavior is a numeric opt-in at Home's two call sites; the wash branch selection, hairline, border, shadow, `glassPress`, `GlassLevel` semantics and the `hue` extension are all unchanged. Reverting Home to `null` restores today's output exactly (rollback = deleting the argument).

Home call-site usage: progress card `fillAlpha = 0.85f` (HERO, `hue = auroraLavender`); `HomeActivityCard` `fillAlpha = 0.90f` for both `CARD` (pending) and `CONTROL` (completed) levels. These are the rejected global `0.85/0.90/0.90` numbers, re-applied only at Home.

#### D. E1 / E2 / Emerald decisions

- **E1 (non-hue two-layer wash): MODIFIED — Home-scoped via `fillAlpha != null`, not global.** The restructure is accepted only where a non-null `fillAlpha` is passed (Home). Default `null` keeps the legacy single gradient, so no existing caller changes. This satisfies §4.11 CHANGE 2 without needing to prove global identical behavior, because the global path is left untouched.
- **E2 (`HueWashMidAlpha 0.5 → 0.8`): ACCEPTED as written — already Home-only by construction.** `hue` is non-null at exactly one production call site (the Home progress card); `HueWashMidAlpha`/`HueMix` are reached only by that branch. No unrelated screen changes (§4.11 CHANGE 3).
- **E3 (global `heroFill`/`cardFill`/`controlFill` raises): REJECTED — no `Tokens.kt` change.** Replaced by the Home `fillAlpha` opt-in above (0.85 / 0.90 / 0.90) at Home's three card surfaces only.
- **Emerald (SUCCESS gradient flatten + idle-ring border):** the gradient window `[0.30, 0.34]` is **accepted** and is Home-only in effect (sole `CompletionAccent.SUCCESS` caller). The idle-ring border (`0.4 → 0.8`) is a **shared** constant and is held under §E, not changed silently.
- **Per-band ink (CHANGE 4): ACCEPTED** — Scheduled Today, verified glyph, two breakdown dots → `c.textPrimary`, no blanket conversion.

#### E. Navigation blocker (and the completion-control idle ring)

The bottom navigation (`LiquidGlassBottomBar` / `RowScope.LiquidGlassTab`) has **exactly one call site** (`MomentumNavGraph.kt:235/237`) reached identically by Home, Habits, Tasks and Insights, and its backdrop is the screen-independent `AppBackground` ambient field. There is **no Home-only navigation path**: a fix is by construction a shared-component fix, and §4.11 CHANGE 1 forbids changing Habits/Tasks/Insights rendering as collateral. The nav's three failing bands are therefore identified as a **shared-component blocker and deferred for human acceptance.** The same applies to the completion control's idle ring, whose `0.4` border alpha is a shared `CompletionButton` constant.

**Smallest shared mechanism proposed (NOT authorized until the human rules):**

1. **Capsule boundary (Light 1.11):** in `LiquidGlassTab`, the capsule border becomes theme-resolved — Light `c.textPrimary.copy(alpha = CapsuleBorderAlpha * capsuleProgress)`, Dark keeps `Color.White` — mirroring the generic hairline (`LiquidGlass.kt:127-131`). One line, one component, no other behavior.
2. **Inactive label/icon (Dark 3.34):** pass `fillAlpha` at the bar's own `liquidGlass(CircleShape, HERO)` call (a nav-scoped two-layer solid fill), raising the bar toward the reference's own `rgba(255,255,255,0.8)` opacity. This preserves the active/inactive ink hierarchy (`selected ? textPrimary : textSecondary`) instead of flattening it, and it is *more*, not less, faithful to the Stitch bar. Alternative (ink `textSecondary → textPrimary`) is available but flattens hierarchy and is **not** in the approved CHANGE 4 list.
3. **Idle-ring boundary (1.68/1.24):** `CompletionButton` idle border alpha `0.4 → 0.8` (smallest) with the option of a Home-only `idleRingBorderAlpha` parameter if the human wants zero non-Home change.

**Impact on other screens** (if accepted): the four destinations' bar renders identically (one code path — no desync by construction) with a slightly more solid bar; every `CompletionButton` idle ring elsewhere becomes marginally more visible. No routing, behavior, target, role, label, component, or padding change. **Why Home-only is impossible:** the nav is a single `Scaffold` sibling outside the Home content tree and has no screen parameter to gate; the alternative is a forbidden Home-only fork.

#### F. Ring arc exception — the five conditions verified

§4.11 CHANGE 5 allows the arc below 3.0:1 only if the Reviewer confirms all five. On the established evidence they hold:

1. **Decorative, not the sole carrier of progress:** `showPercent = false` (`HomeCards.kt:370`) and the KDoc states "the ring stays decorative" (`HomeCards.kt:206`); progress is instead conveyed by the fraction (`3 of 5`), the `%` number and the `n Completed / n Pending` row.
2. **Numeric progress readable at ≥4.5:1:** the `%` text is `c.textPrimary` and is the R5 band (corrected backdrop, target 4.78/8.44).
3. **Completed/Pending available as text:** the breakdown row (`n Completed`/`n Pending`) is `c.textPrimary` and is the fraction's companion.
4. **No state conveyed solely by the arc:** the arc's start `c.success` is the only emerald on the card, but completion itself is textually and chromatically redundantly signaled (fraction, `%`, breakdown, plus the emerald completed-row badges); the arc carries no interaction and no unique state.
5. **Forcing 3:1 would materially degrade the approved Stitch treatment:** any fixed arc/track reaching 3:1 requires stops at L ≤ 0.122 (Light) / 0.057 (Dark) — an ink arc that reverses the approved `success → auroraBlue → auroraViolet` gradient (ADR-48 AN5) for no informational gain.

**Decision:** the arc is classified decorative and **left unchanged**; only its false KDoc claim ("both already pass", `HomeCards.kt:179-180`) is corrected. This is a narrow, arc-only exception — not a general relaxation (§4.11 CHANGE 5).

#### G. Untouched regions

Passing/borderline regions whose expression is **not** changed, even where they ride Home's new fills: date capsule (10.16/5.39), Settings icon (3.76/4.10), greeting (7.30/6.45), greeting support (7.57/4.91), Pending heading title (8.94/4.57), Completed heading title (8.72/4.57), `Finished` meta (8.72/4.57), nav selected label (13.57/6.23), nav active capsule icon (9.81/5.81), Dark capsule boundary (~5.6). The frozen background (§4.9) is untouched in full. Non-Home screens (Habits/Tasks/Insights/Settings/Calendar/Activities/onboarding/detail/creation/themes/CreateItemSheet) remain byte-identical except the two items held under §E.

#### H. Token audit

| Token / parameter | Current | Proposed | Scope | Callers affected | Callers intentionally unaffected |
|---|---|---|---|---|---|
| `Modifier.liquidGlass(..., fillAlpha)` | — (n/a) | `fillAlpha: Float? = null` | new additive param | Home progress card (0.85), HomeActivityCard (0.90) | all ~16 existing `.liquidGlass(` sites (default null) |
| `HueWashMidAlpha` (LiquidGlass.kt) | 0.5f | 0.8f | Home-only (sole non-null `hue` caller = progress card) | Home progress card | none — reached only via `hue != null` |
| `TrackAlpha` (ProgressOrb.kt) | 0.55f | 0.7f | Home-only in effect | Home (sole production `ProgressOrb` caller) | none (previews only) |
| `SuccessMintMix` / SUCCESS stops (CompletionButton.kt) | `success → success⊕mint@0.5` | `[lerp(success,auroraMint,0.30f), lerp(…,0.34f)]` | Home-only in effect | Home (`CompletionAccent.SUCCESS`) | all `THEME` accent callers |
| success dot, textMuted dot (HomeCards.kt) | `c.success` / `c.textMuted` | `c.textPrimary` | Home | Home breakdown row | n/a |
| `Scheduled Today` meta | `c.textSecondary` (default) | `labelColor = c.textPrimary` | Home | Pending header call site | `HomeSectionHeader` default (`textSecondary`) for any future caller |
| `verified` glyph accent | `c.success` | `c.textPrimary` | Home | Completed header call site | `bolt-pulse` loop + glyph shape unchanged |
| `EmptyState(bodyColor)` | — (n/a) | `bodyColor: Color? = null` | new additive param | Home empty state (`c.textPrimary`) | Activities/Insights/Habits (default null) |
| nav capsule border / inactive ink / idle-ring border | shared constants | per §E | shared, **pending human ruling** | all 4 top-level screens / all `CompletionButton` idles | n/a (blocker) |
| `MomentumGlass.heroFill/cardFill/controlFill` (Tokens.kt) | 0.68 / 0.55 / 0.45 | **unchanged** | — | none | all (kept for every non-Home caller) |

No new color token, no new type token, no new motion token, and no `Tokens.kt`/`Color.kt` change is proposed.

#### I. Verification plan (bounded)

1. **Contrast (Reviewer harness, no new sweep):** re-run `measure020-reviewer.js` restricted to the **changed surfaces** — Home progress card, Home rows (0.90 two-layer), empty body (textPrimary), Scheduled Today/verified glyph/dots (textPrimary), ring track (0.7), emerald window (0.30/0.34) — asserting each §10.11 FAIL row clears its unchanged floor; apply only the two band-model corrections (ring-% backdrop = card; arc exempt). Thin margins already reported: emerald boundary 3.09, ring track 3.24, ring% Dark 4.78, check glyph Light 4.84. Do **not** run a new exhaustive geometry/product sweep.
2. **Unit tests:** re-point the source-assertion tests that pin superseded expressions and run `:app:testDebugUnitTest --rerun-tasks`: `HomeAtmosphereAssetsTest` (dots success/textMuted → textPrimary; "the verified glyph keeps the emerald accent" → accent `textPrimary`; Pending header must NOT pass `labelColor` → now DOES; ring-arc KDoc claim), `BottomNavStitchVisualTest` (only if §E is accepted), `CompletionLabelTest` (row `GlassLevel.CONTROL/CARD` expression unaffected by `fillAlpha`).
3. **Compile:** `:app:assembleDebug` + `:app:assembleDebugAndroidTest`.
4. **Device (human/Reviewer, §8.16):** Light+Dark visual check of the changed surfaces only — list rows, progress card + ring, completion emerald, empty state, section heads, `Scheduled Today`/`Finished`, breakdown dots; reduced-motion unaffected (no motion change); confirm non-Home glass screens are visually unchanged.
5. **Non-Home guard:** spot-check one non-Home `liquidGlass` screen (e.g., Habits) for byte-identical rendering (default-null `fillAlpha`).

#### J. Implementation handoff

1. **Files expected to change:**
   - `components/LiquidGlass.kt` — `fillAlpha` param + two-layer wash; `HueWashMidAlpha 0.5→0.8`; (only if §E accepted) capsule border Light + nav bar `fillAlpha`.
   - `components/ProgressOrb.kt` — `TrackAlpha 0.55→0.7` + KDoc.
   - `components/CompletionButton.kt` — SUCCESS gradient window `[0.30, 0.34]`; (only if §E accepted) idle border `0.4→0.8`.
   - `components/MomentumBadges.kt` — `EmptyState(bodyColor: Color? = null)`.
   - `screens/today/HomeCards.kt` — dots → `textPrimary`; progress card `fillAlpha=0.85f`; row `fillAlpha=0.90f`; KDoc corrections (ring arc, ring-% backdrop).
   - `screens/today/TodayScreen.kt` — Pending `labelColor=c.textPrimary` (line ~584); Completed `accent=c.textPrimary` (line ~630); `HomeEmptyState` passes `bodyColor=c.textPrimary`.
   - Tests: `HomeAtmosphereAssetsTest` (+ any §E-dependent test); `docs/architecture/decisions/DECISIONS.md` (ADR-49 Amendment Note 5).
2. **Files expected NOT to change:** `theme/Tokens.kt`, `theme/Color.kt`, `theme/Shapes.kt`, `theme/Theme.kt`, `theme/Type.kt`, `theme/Motion.kt`; `navigation/MomentumNavGraph.kt` (behavior); `components/ActivityCard.kt`, `MomentumComponents.kt`, `MomentumIcons.kt`, `MomentumFab.kt`, `HabitIconTile.kt`, `MomentumHeroArtwork.kt`, `AppBackground.kt`, `aurora/MomentumAurora.kt`; all non-today screens; domain/data/repository/ViewModel/schema/DI/manifest/build.
3. **Tests required:** re-pointed assertions above plus a new positive assertion that `fillAlpha`/`bodyColor` default-null leaves non-Home call sites absent of the new args; run `--rerun-tasks`.
4. **Device verification required:** §8.16 matrix restricted to changed surfaces; non-Home glass untouched.
5. **ISSUE-020 acceptance criteria:** every §10.11 FAIL row ≥ its unchanged floor (4.5 text / 3.0 non-text) except (a) ring arc — decorative, five conditions met — and (b) any band the human defers under §E; background frozen; `Tokens.kt`/`Color.kt` unchanged; non-Home byte-identical for all non-§E surfaces.
6. **Rollback boundaries:** delete Home's `fillAlpha`/`bodyColor`/`labelColor`/`accent` arguments and the SUCCESS-window/TrackAlpha/HueWashMidAlpha value edits to restore today's output exactly; no schema/domain/DI change; no global token was touched.

---

## 9. IMPLEMENTATION STATUS

<!-- DEVELOPER OWNER -->

**RESULT — implemented as specified; not verified on device.** `:app:testDebugUnitTest`
(292 tests) `:app:assembleDebug` and `:app:assembleDebugAndroidTest` (compiles the
instrumented sources only) all pass. **`adb` is unavailable here, so no
instrumented suite has been executed and no screenshot, pixel, bounds or
on-device claim is made.** Nothing is committed.

**Built (§8.1–§8.17 as amended, plus §8.18 and §8.19 in full)**

- **Architecture retained.** The four-child `Box` order
  (atmosphere → chrome → scroll region → FAB), the scroll boundary as **layout**
  (`padding(top = chromeHeightPx)` from one `onSizeChanged`), one `LazyColumn`
  with `key` + `contentType`, `EmptyDayBottomReserve` used twice,
  `HeroScrim` and the `MomentumHeroArtwork` call **verbatim**, the
  `AuroraState` mapping, the one-shot `Celebrate` reward, the encouragement
  toggle, the whole domain layer, `TodayViewModel.kt`, `ActivityCard.kt`,
  `MomentumComponents.kt`, `MomentumBadges.kt`, `MomentumHeroArtwork.kt`,
  `MomentumAurora.kt`, `AppBackground.kt` and every theme file except
  `Type.kt`/`Motion.kt` — all untouched.
- **§8.6 / ADR-47 AN7** — `ArtworkBandFraction = 460f / 640f` and
  `artworkWindow(windowWidth)`; the window is now 230 / 258.75 / 296.13 dp at
  320 / 360 / 412 dp, still content-, state- and scroll-independent, still
  measured from the physical window top. **Correction to the plan's printed
  arithmetic:** the opaque band at 360 dp is 258.75 − 88 = **170.75 dp**, not the
  printed 142.75 dp; BOUND-1 (`≥ ProgressCardRingSize + 2 × 16 = 128 dp`) holds with
  more margin than printed, so no decision changes. `HomeAtmosphere` now takes
  `windowWidth` from the existing `BoxWithConstraints.maxWidth` — no new observer.
- **§8.7 / §8.19 M2** — `HomeProgressCard`: the horizontal lockup, fraction row
  left, 96 dp `ProgressOrb` right with the **percentage inside**, divider, then the
  `n Completed` / `n Pending` breakdown; `GlassLevel.HERO` + `hue =
  auroraLavender`; one merged `today_hero_progress_desc` with the encouragement
  outside it; `Complete` states the statement at the same token; `Hidden` is not
  composed. Two `AuraBlob`s (144/128 dp, `0.30f`/`0.25f`, cached 32 dp radial
  gradient scaled 1 → 1.25, alpha 0.35 ↔ 0.60) inside a `clipToBounds()` box.
- **§8.8** — `HomeSectionHeader` (`MomentumHeadlineSection` + `MomentumLabelMicro`,
  `Pending`/`Scheduled Today`, `Completed`/`Finished` + the new `verified` glyph at
  `MomentumIconSizes.secondary` in `c.success`) and `HomeActivityCard` (48 dp
  135° two-stop tile, strikethrough + subdued title when done, `CARD`/`CONTROL`
  glass, `hue = null`, `CompletionButton(accent = SUCCESS, showIdleGlyph = false)`
  keeping its 48 dp target, `Role.Checkbox`, reversal and action label).
- **§8.18 (A1)** — `LiquidGlassBottomBar` and `RowScope.LiquidGlassTab` re-rendered
  **in place** (`CircleShape`, `heightIn(min = 64.dp)`, `SpaceAround`, 16 dp side
  padding with no vertical padding, `defaultMinSize` 62/56 dp, 48 dp height for
  both states, the two-stop active capsule at 135° with the reference's
  `0.75`/`0.85` alphas, 22/23 dp icons, `MomentumLabelMicro` Bold/SemiBold). The
  1.06 scale, the whole-tab plate, `weight(1f)` and the old label token are gone;
  `Role.Tab`, `contentDescription = label`, `glassPress` and the 18 dp press shape
  stay. `MomentumNavGraph.kt` changed only the slot's three padding values
  (16 / 16 / 20 dp); Nav3, `selectTab`, `showBar`, `Scaffold`,
  `contentWindowInsets` and the four routes are untouched, so all four
  destinations move together and none can desynchronise.
- **§8.19 (A2)** — one `rememberInfiniteTransition("homeAmbient")` in
  `TodayScreen`, created **only** when motion is allowed, deriving `ambientPulse`
  (6 s, `LinearEasing`) and `boltPulse` (2.2 s); `HomeCards.kt` owns **no**
  `rememberInfiniteTransition`, `animate*AsState`, `mutableStateOf` or
  `LaunchedEffect`. `HomeAtmosphere` is now `animated = true` (M1). `MomentumFab`
  gained `glowPulse` / `rotateGlyphOnPress`, both default-`false`, both gated on
  `!reduceMotion`, both passed **by Home only** — the glow breathes the existing
  single shadow (`heroElevation → +4 dp`, spot `0.40 → 0.55`) and the glyph turns
  90° over the existing `fast`/`easeOut`. The entrance clock is one finite 900 ms
  `animateFloatAsState` above the branch key, driving four `revealProgress` phases
  (0 / 100 / 200 / 300 ms) through `Modifier.homeReveal`'s layer transform.
- **§4.7 r3 / §7** — no streak chip, badge, field, string or architecture. No
  domain, persistence, repository or ViewModel change. No invented duration or
  entry-count copy: the secondary line is still `homeMetadataLabel` and still
  renders no row when blank.
- **Tokens.** Three new type tokens (`MomentumHeadlineCompact` 24/32 600 **with
  `tnum`** — R4 resolved, not escalated; `MomentumHeadlineSection` 20/28 600;
  `MomentumLabelMicro` 11/14 600 @ 0.04em) and three new motion tokens
  (`enter = 900`, `fabGlow = 3000`, `verifiedPulse = 2200`). No new colour token:
  the ring gradient and both nav capsule stops use existing Aurora tokens, and the
  capsule's `1px rgba(255,255,255,0.9)` is a literal **alpha**, not a colour.
  `MomentumFigureDisplay` is now unused on Home and stays declared with a KDoc
  note. Three additive shared-component parameters (`ProgressOrb(arcBrush)`,
  `CompletionButton(accent, showIdleGlyph)`, `liquidGlass(hue)`) plus the two FAB
  opt-ins — every one defaults to the previous behaviour, so the other three
  destinations, Tasks, Calendar, Habit Detail and the Settings rows are unchanged.

**Interpretations recorded (not silent)**

1. **`ambientPulse` / `boltPulse` arrive as `State<Float>`, not a bare `Float`.**
   §8.19.6 says "plain `Float` parameters"; §8.19.4 mitigation 3 and §8.19 M4's last
   sentence both require that "no loop recomposes its subtree" and "the item never
   recomposes". A bare `Float` read in a composable body would recompose the card
   and the `Completed` header every frame, which is the opposite of the guarantee
   R9 asks the Reviewer to verify. `State<Float>` satisfies the stronger, twice-
   stated guarantee; the §8.19.6 assertions are written to pin that intent (zero
   transitions / animated state / mutable state / effects in `HomeCards.kt`, the two
   `State<Float>` parameters, and both clocks read inside `drawWithCache` /
   `graphicsLayer`).
2. **The third ambient loop's clock lives inside `MomentumFab`.** §8.19.3 lists
   `fabPulse` as derived from the screen's one transition, but §8.17 item 5 and
   §8.19.6 pin `MomentumFab`'s signature to two **booleans** and assert
   `Home … contains("glowPulse = true")` — so the component owns its own phase,
   gated on `glowPulse && !reduceMotion`. Exactly one ambient clock therefore
   exists in `TodayScreen.kt` (§8.19.5 A's assertion) and the FAB's loop is
   outside the list, created only by Home.
3. **The `Completed` header takes a `Spacer(MomentumSpacing.standard)`** because
   the list's one `verticalArrangement` is 8 dp (the reference's `space-sm`) and
   section→section must be its `space-lg` = 24 dp: 8 + 16 = 24, from tokens, with
   no arithmetic literal.
4. **`HomeProgress.kt` keeps only the progress seam.** `HeroFigureState`,
   `ProgressCardRingSize` and the retained `HomeEncouragementLine` toggle stay
   there; the card that composes them lives in `HomeCards.kt` with the other two
   screen-local surfaces, per §8.6's file list and §8.19.6's `HomeCards.kt`
   assertions.
5. **The date capsule's padding maps to the 4 dp grid.** The reference's
   `5px 14px` are 2.5 / 7 dp at its own scale and off Momentum's grid, so the
   capsule uses `MomentumSpacing.micro` (4 dp) / `compact` (12 dp).
6. **`codeOf` in the source-reading tests strips `import` lines too** where an
   assertion counts occurrences: an import is not a call site and would otherwise
   inflate §8.19.5 A's `count(screen, "rememberInfiniteTransition") == 1`.

**ISSUE-020 contrast re-measurement (ADR-47 AN7 §5 / §4.7 r6) — PARTIAL, AND ONE
BAND FAILS**

Method: the canonical region-sample method, re-run for the TASK-018 geometry
(band 0.71875 × width instead of 184 dp), both themes × widths {360, 412} dp ×
content-area heights {560, 720} dp × `AuroraState.defaultIntensity`
{0.35, 0.55, 0.75, 1.0} × the chrome bottom edge at font scale {1.0, 2.0} × 3 day
states × encouragement {on, off}, with the §8.15 band list including the four A1
nav bands.

- **What was computed exactly.** Every band on glass was evaluated as a
  **provable lower bound**: in Light the worst case is the glass over pure white
  (any real backdrop is darker, so dark-ink contrast is *higher*), in Dark the
  glass over pure black. Results — Light worst case for the progress card
  (`#F0ECFC`): `textPrimary` **14.05:1**, `textSecondary` **5.09:1**,
  `textMuted` **3.10:1** (non-text dot) ✓; pending card (`CARD`) and nav
  (`HERO`) over white: `textPrimary` **16.27:1**, `textSecondary` **5.90:1**,
  `textMuted` **3.10:1** ✓; completed card (`CONTROL`): **16.27:1** /
  **5.90:1** ✓; the nav's active capsule (`#C8C1F4`) carries `textPrimary` at
  **9.60:1** ✓; the completed emerald control is a non-text surface at
  **4.26:1** with its check ink at **3.82:1** ✓. Dark is comfortable throughout
  (worst text band **6.04:1**, the nav's active capsule).
- **What could not be computed here.** The three **bare-ink** bands over the
  artwork — the date capsule, the greeting and its supporting line. The shared
  illustration is a **lossy VP8 WebP** and this environment has no decoder for it
  (no Python, no ImageMagick/ffmpeg, no npm packages, and the JDK's `ImageIO` has
  no WebP reader). TASK-017's figures for those exact bands do **not** transfer:
  the window grew 74.75 dp at 360 dp and `ContentScale.Crop` with
  `BottomCenter` therefore samples different illustration rows at the same y.
  These three bands are left **unmeasured**, not assumed.
- **ONE BAND FAILS, and no in-envelope remedy exists.** The **encouragement /
  statement** line, which §8.7 specifies in `c.success`, measures **3.68:1** in
  Light on its own card — below the **≥ 4.5:1** body-text floor, because
  `AuroraLight.success` (`#1F8A70`, L 0.1964) is only 4.26:1 even on pure white.
  The **`Finished`** status label in `c.success` (§8.8) measures **≈ 4.26:1** on
  the bare Light atmosphere for the same reason. R1's only permitted knob — the
  scrim peak, bounded by `[0.15, 0.35]` — cannot move either band (they sit on
  their own glass fill / on a backdrop that is already at the peak), and R1
  explicitly forbids moving the ink. **The approved ink therefore ships as
  specified and no peak was moved** (`ScrimPeakLight = 0.26f` /
  `ScrimPeakDark = 0.22f` unchanged); this is escalated, not decided, by me.
  Options for the human/Architect: (a) the encouragement returns to `textPrimary`
  (TASK-017's ink, **14.05:1**) and `Finished` to `textSecondary`
  (**5.90:1**); (b) a darker Light `success` — a new colour token, out of TASK-018
  scope; (c) accept 3.68:1 for a 14 sp Medium line.

**Not verifiable in this environment (the human's and the Reviewer's, §8.16)**
the device matrix, the nav on all four destinations, the on-device ISSUE-020
bands above, Layout Inspector's zero-recomposition fling, and the reduced-motion
and ambient-loop behaviour on a real device.

**Correction 1 — the two failed text bands re-inked per §10.1 / ADR-49 AN2.**
Applied to the implementation only; nothing else in this §9 record changes.

- **Changed.** `HomeCards.kt`: the `HomeEncouragementLine` call site
  (`if (isComplete) c.textPrimary else c.success` → `c.textSecondary`, i.e. the
  encouragement **and** the `Complete` statement line now share one token) and
  `HomeSectionHeader`'s label ink (`val metaColor = accent ?: c.textSecondary` →
  `val labelColor = c.textSecondary`, `color = metaColor` → `color = labelColor`).
- **`accent` retained and now glyph-only.** `TodayScreen.kt`'s `accent = c.success`
  is **unchanged**; it is what renders the `verified` glyph (`MomentumIcons.verified`
  is gated on `accent != null`, `tint = accent`), and that glyph is ADR-48 AN6 M4's
  `bolt-pulse` consumer, so no approved band or loop is deleted. `Scheduled Today`
  and `Finished` now read in one token and differ only by the glyph. `accent` and
  `boltPulse` gained `@param` KDoc stating that split.
- **The four legitimate `c.success` uses are untouched** and still assert in the
  suite: the ring's `arcBrush` gradient (`success → auroraBlue → auroraViolet`),
  the breakdown row's success dot, the `verified` glyph tint, and
  `CompletionButton(accent = CompletionAccent.SUCCESS)`. **No new colour token and
  no scrim peak moved** (`ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f` unchanged).
- **KDoc corrected to match behaviour** in `HomeCards.kt`'s `HomeProgressCard`
  (new ink paragraph + the `Complete` state now says `textSecondary`, not
  `textPrimary`) and `HomeSectionHeader`, and in `HomeProgress.kt`'s
  `HomeEncouragementLine` `@param color`. The retained `color` default
  (`textPrimary`) is unchanged and has no production caller.
- **Measured before → after, §10.1's and ADR-49 AN2's arithmetic, not a new
  measurement.** Encouragement / statement line on the Light progress card
  `#F0ECFC`: `c.success` **3.72:1 FAIL** → `c.textSecondary` **5.08:1 pass**
  (the §9 sweep's own composited region figure for the same ink was 3.68:1; both
  fail the 4.5:1 floor and the ruling holds under either number). `Finished` on the
  bare Light atmosphere: `c.success` **≈4.26:1 FAIL** → `c.textSecondary`, which is
  the token already measured at **5.90:1** there (`5.08:1` on the progress card).
  Floor **≥ 4.5:1** in both themes. In Dark both bands move `#76DFC4` → `#AEB8CA`;
  no new Dark ratio is claimed here — ADR-49 AN2 ruled `c.textSecondary` clears the
  floor in both themes, and that token already carries this card's four sibling
  secondary bands (suffix, `done`, `n Pending`).
- **Tests.** `HomeAtmosphereAssetsTest.kt`'s `"the completed status is emerald"` is
  **re-pointed, not deleted**: it now asserts the `verified` glyph keeps the emerald
  accent (`accent = c.success` **and** `tint = accent`) and the `Finished` label is
  `textSecondary` (`val labelColor = c.textSecondary` **and** `color = labelColor`),
  plus `assertFalse("accent ?:")`. A matching assertion was **added** for the
  encouragement line (`color = c.textSecondary` present, `c.success` absent from that
  call window). The `arcBrush` gradient assertion and `HomeAccessibilityTest.kt`'s
  completed-row token assertion are **unchanged**; `tint = accent` is now also
  pinned explicitly so the glyph's emerald cannot be lost.
- **Counts.** `:app:testDebugUnitTest --rerun-tasks` → **292 tests, 0 failures,
  0 errors, 0 skipped**, i.e. the pre-change baseline of 292 holds. `:app:assembleDebug`
  and `:app:assembleDebugAndroidTest` succeed. **The androidTest target only
  compiles the instrumented sources; no instrumented test was executed.**
  **`adb` is unavailable — no screenshot, pixel, bounds, on-device or
  reduced-motion claim is made, and none is implied.**
- **Still OPEN, untouched by this correction.** The three bare-ink chrome bands
  (date capsule, greeting, greeting support line) over the artwork remain
  **unmeasured** — a lossy VP8 WebP with no decoder in this environment, and
  TASK-017's figures do not transfer at the new width. No figure is invented for
  them; closing them is the human's and the Reviewer's (§8.16).

**Correction 2 — the Reviewer's BLOCKERS, corrected per §10.7 / §10.8. This block
supersedes Correction 1's ink and figures, and Correction 1's own wording is
retained above unchanged for the record.**

- **The §9 measurement method was inverted, so every band figure recorded above
  is superseded.** *"In Light the worst case is the glass over pure white (any
  real backdrop is darker, so dark-ink contrast is higher)"* is valid for a
  **solid** fill and **backwards** for the progress card, whose
  `liquidGlass(hue = …)` paints `lerp(surface, auroraLavender, 0.55)` at
  `heroFill = 0.68` **over** the backdrop — so the tinted stop darkens *with* the
  backdrop and the governing case is the card's **darkest** composited stop. The
  governing minimum was never taken, and the §9 band list mixes points
  (`textMuted 3.10:1` is the lightest stop; `textSecondary 5.08–5.09:1` is the
  middle one, `#EFEDFB`). **`c.textSecondary` measures 3.91:1 over white, 3.81:1
  over the Light page `#F7F8FC`, 3.55:1 over `#E0E0F0` and 3.03:1 over a dark
  artwork region — every one below the ≥ 4.5:1 floor.** Correction 1's
  "`c.textSecondary` **5.08:1** pass" and "`Finished` **5.90:1**" therefore do
  **not** stand, and neither does §10.1, which is itself marked SUPERSEDED.
- **Both bands are now `c.textPrimary`** (§10.7). `AuroraLight.textPrimary`
  (`#172033`, **L 0.01454**) clears the floor over every stop of the wash over every
  plausible backdrop by a wide margin; it is the ink TASK-017 used for this copy,
  it adds **no new colour token**, and **no scrim peak moved**
  (`ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f` unchanged). Applied to:
  `HomeCards.kt`'s `HomeEncouragementLine` call site (`color = c.textSecondary` →
  `c.textPrimary`, so the encouragement **and** the `Complete` statement share
  it) and the `Finished` status label. `HomeSectionHeader` gained one additive,
  default-`null` `labelColor: Color? = null` (`val statusInk = labelColor ?:
  c.textSecondary`) and `TodayScreen.kt`'s `Completed` header passes
  `labelColor = c.textPrimary` — necessary because §10.7 keeps `Pending`'s
  `Scheduled Today` on `c.textSecondary`, whose §8.12/§8.18 measurement §10.7
  explicitly left standing. **The emerald is untouched**: the ring's `arcBrush`
  gradient, the breakdown success dot, the `verified` glyph tint and the emerald
  completion control all stay `c.success` (non-text surfaces, ≥ 3:1), and
  `accent` stays **glyph-only**. KDoc in `HomeCards.kt`, `HomeSectionHeader` and
  `HomeProgress.kt` rewritten to the corrected method and the new ink; no
  `5.08`/`5.09`/`3.91` claim about the shipped ink remains.
- **BLOCKER 1 — the completed row's check glyph is restored** (§10.8 Ruling A).
  `CompletionButton.kt` now derives `val glyphShown = showIdleGlyph || completed`,
  and **both** the `Icon` gate and the control's `semantics { contentDescription
  = label }` gate read it — so the done state always renders the check and the
  label is still carried by exactly one of the two nodes (**no** double
  announcement, **no** silent control). The default stays `true`, so all other
  call sites are byte-identical; the 48 dp target, `Role.Checkbox`, the action
  label, the reversal, the spring pop, the haptic and the tint are unchanged.
  The parameter KDoc now states the real contract (idle-only suppression).
- **Ruling B — the visible denominator is restored.** `today_hero_fraction` is
  now `"%1$d of %2$d"` and `HomeCards.kt` passes `stringResource(
  R.string.today_hero_fraction, done, total)`, so the card reads `3 of 5` with
  `items done` as the subordinate suffix at the approved type ramp. Both values
  already arrive in `HeroFigureState.Fraction`; **no new data, no new token**, and
  the merged `today_hero_progress_desc` is unchanged so nothing double-announces.
- **Ruling C.1 — `MomentumFab`'s glow phase moved into the layer phase.**
  `Modifier.shadow(elevation = glowShadowElevation(glowPhase.value), …)` read the
  ambient phase in the **composable body**, recomposing the FAB every frame.
  `Modifier.shadow` is itself a `BlockGraphicsLayerModifier` whose block sets
  `shadowElevation` / `shape` / `clip` / `ambientShadowColor` / `spotShadowColor`
  (verified against `ui-android-1.10.2`'s `draw/Shadow.kt`), so it is now written
  out as that same layer with the read **inside** the block
  (`val phase = glowPhase.value`), matching `HomeCards.kt`'s `ambientPulse` /
  `boltPulse` pattern. Same layer, same shape, same `clip`, same colours, same
  3 s period, same reduced-motion nulling, same `glowPulse = false` default, so
  **Habits' FAB is byte-identical** and `rotateGlyphOnPress` is untouched.
- **Ruling C.2 — `HomeAtmosphere.kt`'s KDoc now prints the corrected opaque-band
  figures: 142 dp @320 · 170.75 dp @360 · 208.13 dp @412** (was the superseded
  `142.75 dp @360`). No behavioural effect; BOUND-1 (≥ 128 dp) holds with more
  margin than §8.6 printed.
- **Tests corrected, deliberately, not deleted.** `HomeAtmosphereAssetsTest.kt`
  re-points the encouragement-line windowed assertion to `c.textPrimary` (and adds
  a negative for the superseded `c.textSecondary`), re-points the section-band
  assertions to `labelColor = c.textPrimary` + `color = statusInk` while pinning
  `Scheduled Today`'s `textSecondary` default, **replaces the idle-only
  `showIdleGlyph` assertion with one covering both states** (`val glyphShown =
  showIdleGlyph || completed`, `if (glyphShown) {` × 2, and `assertFalse("if
  (showIdleGlyph) {")`), adds the `"%1$d of %2$d"` denominator pins, and gains a
  `buttonCode` source. `HomeSectionMotionTest.kt` now pins *where* the FAB's
  phase is read (`val phase = glowPhase.value` present, `glowPhase.value)`
  absent). `QuickAddTest.kt`'s shadow assertions are re-anchored from `shadow(`
  to the layer mechanism (`shadowElevation = ` exactly once, `Modifier.shadow(`
  absent). The two androidTests' visible fraction moved from `"2"` to `"2 of 3"`.
- **Counts.** `:app:testDebugUnitTest --rerun-tasks` → **42 suites, 292 tests,
  0 failures, 0 errors, 0 skipped** — the pre-change baseline of 292 holds.
  `:app:assembleDebug` **BUILD SUCCESSFUL**. `:app:assembleDebugAndroidTest
  --rerun-tasks` **BUILD SUCCESSFUL** — this **compiles the instrumented sources
  only; no instrumented test was executed**. **`adb` is unavailable — no
  screenshot, pixel, bounds, on-device, Layout-Inspector or reduced-motion claim
  is made, and none is implied.**
- **Still OPEN, untouched again by this correction.** The three bare-ink chrome
  bands (date capsule, greeting, greeting support line) over the artwork, and the
  composited-atmosphere bands of §10.6 item 4, remain **unmeasured**. No figure is
  invented for any of them.

### 9.x Developer pass — the §13.1 human ruling applied (2026-10-01)

<!-- DEVELOPER OWNER -->

The Orchestrator's §13.1 ruling (**option (a)**) is now **implemented**. Three
colour expressions and three comments changed, in one file. **Nothing else.**

- **The ruling.** `HomeCards.kt`'s three remaining `c.textSecondary` body bands
  inside the chromatic `liquidGlass` progress card became `c.textPrimary`:
  the `items done` suffix (`today_progress_suffix`), the ring's `done` unit
  (`today_progress_ring_unit`) and `n Pending` (`cd_progress_pending`).
  `n Completed` was already `c.textPrimary`, so the breakdown row is now uniform
  and its relational structure is carried by the two **dots** (`c.success` /
  `c.textMuted`), never by two inks.
- **Untouched, deliberately.** The `liquidGlass` wash, the 135° gradient, every
  alpha, `HueMix`, `GlassLevel`, the scrim and both scrim peaks; the ring's
  `arcBrush`; the emerald completion control; `showIdleGlyph`'s idle-only
  suppression with the check glyph always drawn when complete; `accent` staying
  **glyph-only**; `Scheduled Today` staying `c.textSecondary`; the
  `labelColor: Color? = null` default and `Finished`'s `c.textPrimary` opt-in.
  **No new colour token, no dp literal, no dependency, no domain / persistence /
  repository / ViewModel / artwork / navigation change. Not committed.**
- **KDoc** on [HomeProgressCard] now carries the ruling as a standing
  instruction: the card's body text is uniformly `c.textPrimary` because
  `c.textSecondary` cannot clear 4.5:1 on this wash, this is an **explicit,
  accessibility-driven deviation from the reference's `#475569` hierarchy**, and
  the slate hierarchy must **not** be "restored".
- **Tests — re-pointed, never deleted or weakened.** No test pinned the old inks
  for these three bands, so nothing had to be re-pointed; the breakdown-row test
  asserted no ink difference, so nothing had to be corrected. One **new** test
  pins both sides of the ruling: `every band on the progress card is
  textPrimary while Scheduled Today stays textSecondary` — it asserts
  `color = c.textPrimary,` for all six card string bands plus the ring's
  percentage, asserts **no** `c.textSecondary` survives anywhere in
  `HomeProgressCard` (comments stripped), asserts the emerald `arcBrush` and
  both breakdown dots are unchanged, and asserts `Scheduled Today` is still the
  `labelColor = null` → `c.textSecondary` default with `Pending`'s call site
  carrying no `labelColor`. The pre-existing encouragement-line and
  section-heading ink assertions are **kept intact**.
- **Counts.** `:app:testDebugUnitTest --rerun-tasks` → **42 suites, 293 tests,
  0 failures, 0 errors, 0 skipped** — the 292 baseline plus the one new test.
  `:app:assembleDebug` **BUILD SUCCESSFUL**. `:app:assembleDebugAndroidTest`
  **BUILD SUCCESSFUL** — this **compiles the instrumented sources only; no
  instrumented test was executed.** **`adb` is unavailable — no screenshot,
  pixel, bounds, on-device, Layout-Inspector or reduced-motion claim is made, and
  none is implied.**
- **Source-level conformance, re-run and reported as found.** Comments stripped,
  code only. `HomeCards.kt`'s `HomeProgressCard` body (lines 246–476): **no**
  `c.textSecondary` remains — **confirmed, zero hits**. Across the four Home
  files the surviving `c.textSecondary` are exactly: `HomeCards.kt`'s
  `val statusInk = labelColor ?: c.textSecondary` (the `Scheduled Today`
  default — ruled unchanged), `HomeActivityCard`'s completed-title subdued ink
  and its metadata line (a **non-chromatic** `liquidGlass(CARD/CONTROL,
  hue = null)` row card, outside the ruling's scope, left untouched), and
  `TodayError`'s message in `TodayScreen.kt` (the app-wide error state, outside
  the Home progress card).
- **Still OPEN.** The artwork-overlaid bands of §13.2 — including **all** bands
  on the progress card against the real artwork composite — remain **OPEN and
  unmeasured**. No figure is invented for any of them, and no attempt was made to
  close them.

### 9.A §4.8 (Device Finding #1) background correction — **implemented; not device-verified**

**RESULT — implemented as specified by §4.8; not verified on device.**
`:app:testDebugUnitTest --rerun-tasks` (**42 suites, 293 tests, 0 failures, 0
errors**), `:app:assembleDebug` and `:app:assembleDebugAndroidTest` (compiles the
instrumented sources only) all pass. **`adb` is unavailable here, so no
instrumented suite has been executed and no screenshot, pixel, bounds or
on-device claim is made.** Nothing is committed.

- **Geometry.** `ArtworkBandFraction = 460f / 640f` is withdrawn and replaced by
  `ArtworkIntrinsicRatio = 1536f / 1024f` (the bundled pair's own dimensions);
  `artworkWindow(windowWidth) = (ArtworkIntrinsicRatio * windowWidth.value).dp`
  → **480 / 540 / 618 dp** at 320 / 360 / 412 dp (1.5 × width). The container
  stays full-width, `.align(Alignment.TopStart)`, derived from the existing
  `BoxWithConstraints.maxWidth` plumbing in `TodayScreen.kt` (untouched) — no new
  observer, no state/chrome/scroll input.
- **No crop.** `MomentumHeroArtwork` gained ONE additive defaulted parameter
  `contentScale: ContentScale = ContentScale.Crop` (Habits and every other caller
  byte-identical); Home's call site passes `ContentScale.Fit`, so the complete
  Aurora renders uncropped in the intrinsic-ratio container. `zoom`,
  `parallax`, `topDissolve`, `bottomDissolve` are NOT overridden at Home's call
  site (the shared 88 dp bottom dissolve stays).
- **Unchanged.** `HeroScrim` (four stops, peaks 0.26 / 0.22, `[0.15, 0.35]`),
  `HomeAurora`/`AuroraBackground`, the page background, the fixed-layer
  composition, all foreground UI — untouched; no new rectangle, gradient, scrim
  or colour was added for the lavender continuation.
- **BOUND-1 re-derived** from the intrinsic ratio: opaque band = 1.5 × width −
  88 = **392 / 452 / 530 dp** at 320 / 360 / 412 dp against the 128 dp
  requirement — holds with more margin than the withdrawn band.
- **Tests corrected, not deleted** — `HomeAtmosphereAssetsTest` (intrinsic-ratio
  window, BOUND-1 arithmetic, Fit-not-Crop on Home's path, no `460f / 640f` or
  `ArtworkBandFraction` in production code; tags/no-zoom/no-parallax/scrim/dp-literal
 /shared-contract assertions kept, incl. `shared` still containing
  `ContentScale.Crop` as the default) and `HomeCompositionTest` (`…IsDerivedNotMeasured`
  now pins `ArtworkIntrinsicRatio`; the upper-region test additionally pins the
  measured window's 1536/1024 ratio; the empty-day test now derives the expected
  window from the screen's own width instead of a 360 dp literal). `HomeAccessibilityTest`
  needed no change and passes.
- **Finding (not fixed here):** the §4.7-era scrim peak sweep predates this
  geometry — the artwork region is now taller under the same scrim, so
  foreground contrast against the final artwork must be re-evaluated under
  ISSUE-020. No ink, peak or scrim stop was changed.

### 9.B §8.20 Home-scoped ISSUE-020 remediation — **implemented; Gradle validation PASSED; device verification PASSED (412 dp, Light + Dark); cosmetic cleanup applied**

**RESULT — implemented as approved, and the three required Gradle checks pass.**
Run through the mandated `gradle-run` wrapper (workflow `82cc1359488c8250e3e9bbd1aa1b745b`;
Python 3.14.8): `:app:testDebugUnitTest --rerun-tasks` → **exit 0, 42 suites,
293 tests, 0 failures, 0 errors, 0 skipped**; `:app:assembleDebug` → **exit 0,
BUILD SUCCESSFUL**; `:app:assembleDebugAndroidTest` → **exit 0, BUILD SUCCESSFUL**
(compiles the instrumented sources; it does not execute them). Only the
pre-existing experimental/deprecation warnings appeared; no new warnings.
**`adb` is unavailable here, so no instrumented suite has been executed and no
screenshot, pixel, bounds or on-device claim is made.** Nothing is committed.**

**Implemented (source) — exactly §8.20/§4.11 + the human's shared-blocker ruling**

- `components/LiquidGlass.kt` — `Modifier.liquidGlass(..., fillAlpha: Float? =
  null)`: `null` keeps the legacy single `topLight → fill → fill` wash; non-null
  switches the non-hue wash to a two-layer solid fill + fading top streak. The
  hue branch uses `effectiveFillAlpha`. `HueWashMidAlpha 0.5f → 0.8f`. The nav
  bar's own call is `liquidGlass(CircleShape, GlassLevel.HERO, fillAlpha =
  NavBarFillAlpha)` with `NavBarFillAlpha = 0.85f`. The active capsule hairline is
  theme-resolved: Light `c.textPrimary`, Dark `Color.White`, both scaled by
  `CapsuleBorderAlpha * capsuleProgress`. Inactive label/icon stay
  `c.textSecondary` (`selected ? textPrimary : textSecondary`).
- `components/ProgressOrb.kt` — `TrackAlpha 0.55f → 0.7f` + KDoc (sole
  production caller is Home).
- `components/CompletionButton.kt` — `CompletionAccent.SUCCESS` gradient window
  `[lerp(success, auroraMint, 0.30f), lerp(success, auroraMint, 0.34f)]`, check
  glyph ink still `c.onPrimary`; new additive `idleRingBorderAlpha: Float? =
  null` (`null` = exact `IdleRingBorderAlpha = 0.4f`).
- `components/MomentumBadges.kt` — new additive `EmptyState(bodyColor: Color? =
  null)`; `null` = shared `c.textSecondary`.
- `screens/today/HomeCards.kt` — progress card `fillAlpha = ProgressCardFillAlpha
  (0.85f)`, rows `fillAlpha = HomeRowFillAlpha (0.90f)`, both breakdown dots
  `c.textPrimary`; `HomeActivityCard` passes `idleRingBorderAlpha =
  HomeIdleRingBorderAlpha (0.8f)`; KDoc corrected (ring arc decorative, track
  0.7, dot inks, row fills).
- `screens/today/TodayScreen.kt` — Pending `labelColor = c.textPrimary`; Completed
  `accent = c.textPrimary` (glyph becomes ink) and `labelColor = c.textPrimary`;
  `HomeEmptyState` passes `bodyColor = c.textPrimary`.
- Tests re-pointed to the superseded expressions, **not weakened/deleted**:
  `HomeAtmosphereAssetsTest` (both dots `textPrimary`, both status bands
  `textPrimary`, verified glyph `accent = c.textPrimary`, Home `fillAlpha opt-ins
  pinned), `BottomNavStitchVisualTest` (nav `fillAlpha` opt-in + theme-resolved
  capsule border).
- `docs/architecture/decisions/DECISIONS.md` — **ADR-49 Amendment Note 6**
  appended (append-only), recording the human approval of the shared blockers and
  the as-built state. AN5 and all earlier notes are intact.

**Preserved by construction:** `theme/Tokens.kt` and `theme/Color.kt` (and every
other theme file) unchanged — `MomentumGlass.heroFill/cardFill/controlFill` stay
`0.68/0.55/0.45`; `navigation/MomentumNavGraph.kt` behaviour/routing/padding
unchanged; the frozen background (§4.9) untouched; every non-Home
`CompletionButton` and `EmptyState` and all ~16 non-Home `.liquidGlass(` sites
default-`null`/`false`, hence byte-identical (source-level guarantee).

**BLOCKERS / NEXT — ALL CLOSED (2026-10-02; see §12 and §13.3).** Retained as the
as-built Developer handoff record:
1. **Gradle validation PASSED** — `:app:testDebugUnitTest --rerun-tasks`
   (42 suites / 293 tests / 0 failures), `:app:assembleDebug`,
   `:app:assembleDebugAndroidTest` all exit 0.
2. **Bounded ISSUE-020 contrast re-run PASSED** (Reviewer harness on the changed
   bands, Light and Dark, zero FAIL rows). Thin margins cleared: emerald boundary
   3.09, ring track 3.24, ring-% Dark 4.78, check glyph Light 4.84.
3. **Device verification PASSED** (Light and Dark, emulator 412 dp); `adb` is now
   available. Residual limitation recorded in §13.3 (320/360 dp and font-scale
   2.0 not device-tested), not a defect.
4. No TASK-019 started; no non-Home file touched.

## 10. REVIEW FINDINGS AND VALIDATION

<!-- REVIEWER OWNER -->

### 10.0 Reviewer verdict — 2026-10-01 (first pass: **Fail**, now resolved)

**Fail (first pass).** The Home composition, the A1 navigation and the A2 motion
are implemented as specified and the architecture is intact, but two items are
blockers: the completed activity card loses the approved reference's check
glyph (§10.2) and the ISSUE-020 re-measurement that the two re-inked bands
depend on is method-inverted, so the ≥ 4.5:1 floor is **not** established for
them (§10.3). Three minor findings are in §10.4. Validation executed is in
§10.5; the bands that remain OPEN are in §10.6.

**Re-review of the corrections: PASS — see §10.9.** Both blockers and both minor
findings are resolved, Ruling B is satisfied, and no regression was introduced.
§10.1 is SUPERSEDED (§10.7); §10.2–§10.6 are retained below unchanged for the
record. One **non-blocking** record-accuracy correction is outstanding in §10.9.

**`adb` is unavailable in this environment.** Nothing in §10 is a device,
screenshot, pixel, bounds or on-device claim, and no instrumented test was
executed.

### 10.1 SUPERSEDED — see §10.7. Retained for the record only.

> **Superseded by §10.7.** The Reviewer found the lower-bound premise behind this
> ruling to be inverted for the card's chromatic wash, and re-verification confirms
> **`c.textSecondary` also fails the floor on the progress card** (3.91:1, not
> 5.08:1). Do not implement §10.1. The correct ink is `c.textPrimary` — see §10.7.

### 10.1-original Orchestrator adjudication of the Developer's escalated contrast band

The Developer escalated one failing ISSUE-020 band and I verified it
independently, then found the cause is a **mis-mapping of the reference in §8.7 /
§8.8**, not an unavoidable constraint. Ruling recorded here as Orchestrator
(§4.7 r8 permits resolution of concrete implementation blockers without returning to
the human); the ADR record remains the Architect's.

**Verified arithmetic** (WCAG relative luminance, computed directly from the sRGB
values in `Color.kt` and `code.html`):

| ink | on `#F0ECFC` (Light progress card) | floor |
|---|---|---|
| `AuroraLight.success` `#1F8A70` (§8.7's choice) | **3.72:1 — FAIL** | 4.5:1 |
| `AuroraLight.textSecondary` `#596579` | **5.08:1 — pass** | 4.5:1 |
| `AuroraLight.textPrimary` `#172033` | 17.71:1 — pass | 4.5:1 |
| **Stitch's own encouragement ink `#00687a`** | **5.55:1 — pass** | 4.5:1 |
| **Stitch's own `Finished` ink `#005236`** | **8.19:1 — pass** | 4.5:1 |

**Root cause.** `code.html` does **not** colour the encouragement line or the
`Finished` label with the emerald success colour. It uses `#00687a` (a dark teal) for
the encouragement and `#005236` (a dark green) for `Finished`. §8.7 mapped both to
`c.success`, which is a *semantic* substitution Momentum made — and Momentum's Light
`success` is a mid-tone that cannot carry 14 sp body text at 4.5:1. The failure is
therefore a **token-mapping error against the authoritative reference**, not a design
constraint. §4.7 §1 makes `code.html` authoritative for concrete values precisely
here.

**Ruling — the ink follows the reference, not the semantic guess.** No new colour
token is introduced (§4.7 §13), and no peak moves (§4.7 §19 keeps the floor, and the
peaks are irrelevant to a band sitting on its own glass fill).

- The encouragement / completion-statement line and the `Finished` status label use
  **`c.textSecondary`**, which is the closest existing token **in role** to Stitch's
  two dark inks and clears the floor in both themes. The **emerald signal is not
  lost**: it stays carried by the `verified` glyph tint and the emerald completion
  control, both of which are non-text surfaces with their own ≥ 3:1 obligation.
- No new colour token. `ScrimPeakLight` / `ScrimPeakDark` stay 0.26 / 0.22.
- Recorded as a **deviation from the reference's literal hex**, justified because the
  reference's hexes have no Momentum token and introducing them would breach §4.7
  §13, while the floor in §4.7 §19 is non-negotiable.

**Consequence for the Architect:** `DECISIONS.md` must record this correction plus
the completed ISSUE-020 sweep table (the Developer could not write to `docs/**`).
**Consequence for the Developer:** re-ink the two bands, re-run
`:app:testDebugUnitTest`, and confirm no test pinned the `c.success` ink.
**Consequence for the Reviewer:** the 4.5:1 floor must be re-verified for both bands
after the change.

**Still open, and not resolvable here:** the three **bare-ink chrome bands** (date
capsule, greeting, greeting support line) over the artwork remain **unmeasured** —
the artwork is a lossy VP8 WebP and this environment has no decoder. TASK-017's
figures do not transfer because the window grew 74.75 dp at 360 dp, so
`ContentScale.Crop` + `BottomCenter` samples different illustration rows. This
requires either a WebP-capable environment or the device; it is the human's and the
Reviewer's (§8.16).

### 10.7 Orchestrator correction of §10.1 — the Reviewer's BLOCKER 2 is confirmed, and §10.1's ink is also wrong

The Reviewer reported that §9's ISSUE-020 method is **inverted** for the progress
card. I re-verified the arithmetic independently and **the Reviewer is correct, and
my §10.1 ruling was wrong.** Both are corrected here.

**The method defect.** §9 reasoned *"in Light the worst case is the glass over pure
white, so dark-ink contrast is only higher elsewhere."* That is valid for a **solid**
fill. It is **inverted** for the progress card, because `liquidGlass(hue = …)` paints
a **chromatic wash**: `lerp(c.surface, auroraLavender, 0.55)` at `heroFill = 0.68`,
composited **over the backdrop**. Dark ink gains contrast as its background
*lightens*, so the worst case is the card's **darkest** stop — the tinted stop over
the **darkest** backdrop — not the plain stop over white. The recorded 5.08–5.09:1
reproduces exactly, but only at the card's *middle* stop `#EFEDFB`; the record's own
band list mixes points (`textMuted 3.10:1` is the lightest stop). **The governing
minimum was never taken.**

**Recomputed, from `Color.kt`'s sRGB values, `HueMix = 0.55`, `heroFill = 0.68`:**

| composited backdrop | card's tinted stop | `c.textSecondary` CR | verdict |
|---|---|---|---|
| `#FFFFFF` (pure white) | `#D6CDF6` | **3.91:1** | **FAIL** |
| `#F7F8FC` (Light page) | `#D4CAF5` | **3.81:1** | **FAIL** |
| `#E0E0F0` | `#CCC3F1` | **3.55:1** | **FAIL** |
| `#B0B0D8` (dark artwork region) | `#BDB3EA` | **3.03:1** | **FAIL** |

`c.textSecondary` (L 0.12812) needs a composited luminance of **≥ 0.5266** to reach
4.5:1. **The real backdrop is the unmeasurable artwork composite, so the true worst
case is at or below the `#B0B0D8` row.** `c.textSecondary` therefore **fails the
floor on this card in Light**, and §10.1's 5.08:1 figure must not be used.

**Correct ruling — `c.textPrimary`, on both bands.**
`AuroraLight.textPrimary` (`#172033`, **L 0.01454**) needs a composited luminance of
only **0.01544** to reach 4.5:1 — below every stop in the table, so it clears the
floor everywhere with wide margin (10.79:1 at the lightest stop, 8.37:1 at the
darkest). It is the ink TASK-017 already used for this copy, it matches the
reference's own darkest ink family (`#0f172a`), and it introduces **no new colour
token** and moves **no scrim peak**.

- Encouragement / completion-statement line → **`c.textPrimary`**.
- `Finished` status label → **`c.textPrimary`**.
- `Scheduled Today` (the `Pending` meta, already `c.textSecondary`) sits on the **bare
  atmosphere**, not on the card's wash, so §8.18/§8.12's existing measurement stands;
  leave it and let the on-device sweep confirm it.

**The emerald signal is unchanged and still carries the semantic load:** the ring's
`arcBrush` gradient, the breakdown row's success dot, the `verified` glyph tint and
the emerald completion control remain `c.success` — all non-text surfaces with their
own ≥ 3:1 obligation, all measured passing. This ruling changes **text ink only**.

**Also required:** §10.1's figures are quoted in `ADR-49 Amendment Note 2` and are
therefore wrong; the Architect must append a further append-only note recording the
method correction and the `c.textPrimary` ruling. **The three artwork-overlaid chrome
bands remain OPEN** and are unaffected by this correction — they are unmeasured for a
different reason (no WebP decoder) and must still not be assumed.

### 10.8 Orchestrator rulings on the Reviewer's BLOCKER 1 and the `X of Y` conflict

**Ruling A — BLOCKER 1 CONFIRMED: the completed-state check glyph must be restored.**
`CompletionButton.kt:184-191` removes the check `Icon` when `showIdleGlyph = false`
in **both** states, so every completed Home row is an emerald disc with no check. That
contradicts `screen.png` (all three completed cards show a white check), `code.html:96`
(`<span …>check</span>`), §4.2 E's "emerald completion indicator" and §4.1's ban on
simplifying the approved design. The parameter's own name and `HomeCards.kt`'s KDoc
(*"the reference's **empty idle ring**"*) both say idle-only — the code contradicts
its own contract. **Fix:** `showIdleGlyph` must suppress the glyph **only while idle**
(`if (showIdleGlyph || completed)`); the done state always renders the check.
The default stays `true`, so all other call sites remain byte-identical, and the 48 dp
target, `Role.Checkbox`, the action label and the reversal are unchanged. The
existing test that mislabels this as idle-only must be corrected to cover **both**
states, so this cannot regress silently again.

**Ruling B — the denominator is RESTORED: the card must read `X of Y items done`.**
§4.2 C requires it, and **both reference artifacts show it** — `screen.png` reads
"3 of 5 habits done" and `code.html:92` reads "3 of 5 items done". §8.7's printed
hierarchy specified only `%1$d`, which is a **plan defect**, not a design decision:
no ADR or human ruling ever chose to drop the denominator. The human ruling §4.7 r4
reaffirms `X of Y items done`. §4.2 C and the human ruling therefore **outrank** the
plan's printed hierarchy on this point.
**Fix:** render the denominator visibly in the fraction row — `"%1$d of %2$d"` as the
headline metric with `items done` as the subordinate suffix — reusing the existing
`HeroFigureState.Fraction(done, total)` that already carries both values, and keeping
the merged `today_hero_progress_desc` unchanged so nothing double-announces.
`today_hero_fraction` becomes `"%1$d of %2$d"`. No new data, no new token.

**Ruling C — minor findings, both to be corrected by the Developer.**
1. `MomentumFab.kt` reads `glowPhase.value` **in the composable body**, which
   recomposes the FAB every frame and contradicts §8.19.3/§8.19.4's "none is read by
   a composable's body" and §8.14's guarantee. Move the read inside the draw/shadow
   layer (or pass the phase as `State<Float>` consumed in `drawWithCache`), matching
   the pattern already used for `ambientPulse`/`boltPulse` in `HomeCards.kt`.
2. `HomeAtmosphere.kt:65-66` still prints the superseded opaque-band figures
   (`142.75 dp @360`). Correct to **170.75 dp @360**. No behavioural effect —
   BOUND-1 (≥ 128 dp) holds with more margin than printed.

**Ruling D — the emerald usage stays as-is.** BLOCKER 1's fix changes *glyph
presence*, not ink. The `verified` glyph tint, the ring gradient, the breakdown dot
and the emerald control all remain `c.success`.

---

### 10.2 BLOCKER — the completed activity card renders no check glyph

- **File:** `app/src/main/java/com/momentum/app/presentation/components/CompletionButton.kt`
- **Location:** lines 184–191 (`if (showIdleGlyph) { Icon(Icons.Rounded.Check, …) }`),
  reached from `HomeCards.kt:606-607` (`accent = CompletionAccent.SUCCESS,
  showIdleGlyph = false`)
- **Defect.** `showIdleGlyph = false` removes the check `Icon` in **both** states,
  not only in the idle state. Home is the only call site that passes it, so
  **every completed row on the Completed section renders an emerald disc with no
  check mark**, and the only thing distinguishing it from a plain filled circle
  is the gradient and the glow.

  This is a loss of the approved design, not a simplification of it:

  - `code.html:96` — each completed card's control is
    `<div class="w-9 h-9 rounded-full text-white …" style="background: linear-gradient(135deg, rgb(5,150,105) …)">`
    containing `<span class="material-symbols-outlined text-[19px] font-bold …">check</span>`.
    The reference's **completed** state carries a white check; only its
    **pending** state is the empty ring
    (`code.html:90`, `<span class="w-4 h-4 rounded-full border-2 …"></span>`).
  - `screen.png` shows the emerald disc **with** a white check on all three
    completed cards.
  - §4.2 E enumerates "emerald completion indicator"; §4.1 forbids
    redesigning, simplifying or substituting the approved design.
  - The parameter's own name is `showIdleGlyph`, and `HomeCards.kt:603-605`
    states the intent as "the emerald done fill and the reference's **empty
    idle ring** (no faint glyph)". The code contradicts its own documented
    intent.
  - Nothing covers the done state. `HomeAtmosphereAssetsTest:393-396` asserts
    `showIdleGlyph = false` under the label *"the **idle** ring carries no
    glyph"* — so the suite documents the idle-only intent while the code
    applies it to both states, and the defect is invisible to it.
  - §9's recorded figure "the completed emerald control … with its check ink at
    3.82:1" is **orphaned**: there is no check ink on Home at all. (The glyph
    would be tinted `c.onPrimary`, which in Light is `#172033`, not white — so
    that figure describes no code that exists.)
- **Required correction.** Gate the glyph on `showIdleGlyph || completed`, so
  the idle ring stays empty (the reference's pending control) and the completed
  control keeps its check at the unchanged 48 dp target, `Role.Checkbox`, action
  label (already on the control), haptic and spring pop. Add a test that pins
  the done-state glyph — the current assertion does not. **No** redesign,
  palette or spacing change: only the missing glyph is restored.

### 10.3 BLOCKER — the ISSUE-020 re-measurement is method-inverted, so the two re-inked bands are unproven

- **Files:** `task.md` §9 "ISSUE-020 contrast re-measurement … What was computed
  exactly"; the same arithmetic is asserted in
  `app/src/main/java/com/momentum/app/presentation/screens/today/HomeCards.kt:166-175`
  ("measures **5.08:1** there") and `:413-415` ("**3.72:1** for `c.success`
  against **5.08:1** for `c.textSecondary`").
- **Location of the defective premise:** §9 — *"in Light the worst case is the
  glass over pure white (any real backdrop is darker, so dark-ink contrast is
  **higher**)"*.
- **Defect.** That rule is valid for a **solid** fill and **inverted** for the
  card's actual translucent fill. Dark ink gains contrast as the background gets
  *lighter*, so the worst case is the **darkest composited** point — and for the
  `hue` wash that point is `tinted @ heroFill`, which **darkens together with the
  backdrop**. A darker real backdrop therefore makes dark-ink contrast
  **worse**, not better. Two consequences:

  1. **Light is not safe.** Composited from the sRGB values in `Color.kt`
     (`surface #FFFFFF`, `auroraLavender #9278E8`, `HueMix 0.55`,
     `heroFill 0.68`, `HueWashMidAlpha 0.5`) with WCAG relative luminance:

     | backdrop behind the card | darkest stop (`tinted@0.68`) | `textPrimary` | `textSecondary` |
     |---|---|---|---|
     | `#FFFFFF` (the stated worst case) | `#DED9F7` | 11.94:1 | **4.33:1** |
     | `#F7F8FC` (the Light page) | `#DBD7F6` | 11.64:1 | **4.22:1** |
     | `#F0F0F8` | `#D6D1F3` | 11.33:1 | **4.11:1** |
     | `#E0E0F0` | `#D3CEF2` | 10.73:1 | **3.89:1** |

     `AuroraLight.textSecondary #596579` (L 0.1281) needs a composited
     luminance of **≥ 0.7515** for 4.5:1. It does not reach that at the card's
     darkest point over **any** of the backdrops above. The ring's 96 dp
     `textPrimary @ 0.55` track sits in the same stop. The card also sits over
     the composited Aurora field + artwork + scrim at a band that is now
     0.71875 × width — which is the unmeasurable set.

  2. **The recorded 5.08–5.09:1 is not the band's governing value.** It
     reproduces exactly (I compute **5.11:1** on `#EFEDFB`, the card's **middle**
     stop — the recorded `#F0ECFC`), but it is one interior gradient stop. The
     record's own band list mixes points: `textMuted 3.10:1` is the **lightest**
     stop (`#FFFFFF`) while `textSecondary 5.09:1` is the middle one. The
     governing minimum across the card was never taken.

- **Required correction.** Re-run the ISSUE-020 sweep with the direction fixed:
  take the **minimum** ratio over each band's own text rectangle across the
  **whole composited surface** (all three wash stops × both themes × widths
  {320, 360, 412} × content-area heights {560, 720} × `defaultIntensity`
  {0.35, 0.55, 0.75, 1.0} × font scale {1.0, 2.0} × 3 day states × encouragement
  {on, off} × the §8.15 band list), not a single composited colour. The bands
  that cannot be composited without the artwork — **date capsule, greeting,
  greeting support line, encouragement/statement, suffix, ring percentage, ring
  `done`, `n Completed`, `n Pending`, divider, both headings, `Finished`, the
  completed title, the emerald control** — must be recorded as **OPEN** until
  sampled against the real illustration. Correct the figures asserted in
  `HomeCards.kt`'s KDoc to whatever the re-run produces, and correct the
  same-direction claims in §9.

  I am **not** re-opening §10.1's ruling: `c.textSecondary` is the correct ink
  and it is confirmed to match `code.html`'s `#475569`/`#005236` role for
  `Scheduled Today` / `Finished`. The finding is that the correction has not
  been *verified*, so it cannot be recorded as shipped-clear.

### 10.4 Minor findings

1. **`MomentumFab.kt:133` and `:136`** — `elevation = glowShadowElevation(glowPhase.value)`
   and `spotColor = c.primary.copy(alpha = glowSpotAlpha(glowPhase.value))` read
   the ambient phase in the **composable body**, so the third approved loop
   recomposes `MomentumFab` on every frame. §8.19.3 and §8.19.4 require the
   opposite: *"consumed in `drawWithCache`, `graphicsLayer` or `Modifier.shadow`;
   none is read by a composable's body, so no loop recomposes its subtree"*, and
   §8.19.4's table claims **recomposition: none** for M3. `Modifier.shadow` has
   no lambda form, so honouring the rule needs the shadow drawn in
   `drawWithCache`/`drawBehind` from the phase read inside the draw phase.
   Blast radius is bounded (one fixed 56 dp element outside the list; the
   zero-recomposition **fling** guarantee is untouched) — this is a conformance
   and per-frame-cost defect, not a visual one. Either fix it or record it as a
   deviation in §8.19.7 and in §8.19.4's table.
2. **`HomeAtmosphere.kt:65-66`** — the KDoc still prints the superseded
   "142 dp @320 · **142.75 dp @360** · 208 dp @412" opaque-band figures. The
   Developer's correction (258.75 − 88 = **170.75 dp** @360) landed in `task.md`
   §9 and in `HomeAtmosphereAssetsTest:153-158` but not in the KDoc. No
   behavioural effect — BOUND-1 (≥ 128 dp) holds with more margin than printed.
3. **§4.2 C's `X of Y items done`** — the visible card shows only the numerator
   plus the suffix (`strings.xml` `today_hero_fraction` = `%1$d`;
   `HomeCards.kt:306-317`), so it reads e.g. *"3 items done"*; the denominator
   survives only in the merged accessibility description
   `today_hero_progress_desc`. Both reference artifacts show "3 of 5" —
   `screen.png` and `code.html:92`. §8.7's printed hierarchy specifies `%1$d`,
   so **§4.2 C and §8.7 genuinely conflict** and this is not the Developer's to
   resolve. Needs an Orchestrator/Architect ruling: add the denominator per
   §4.2 C, or record that §8.7 supersedes it. No information is lost to a
   screen-reader user either way.

### 10.5 Validation executed, and conformance confirmed

**Executed here (actual runs, not `UP-TO-DATE`):**

| Command | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **BUILD SUCCESSFUL** — 42 suites, **292 tests, 0 failures, 0 errors, 0 skipped** (matches §9's count) |
| `:app:assembleDebug` | **BUILD SUCCESSFUL** |
| `:app:assembleDebugAndroidTest --rerun-tasks` | **BUILD SUCCESSFUL** — this **compiles** the instrumented sources only; **no instrumented test was executed** |

`adb` is unavailable: no emulator/device run, no screenshot, no pixel
measurement, no bounds observation and no reduced-motion observation was
possible, and none is claimed. The §8.16 18-point device matrix, §8.18.5's
four-destination nav check, Layout Inspector's zero-recomposition fling and
the ambient/reduced-motion behaviour all remain the human's.

**Specification conformance confirmed by source inspection** (no findings):

- **§8.3 / §8.6 architecture.** `TodayScreen.kt:453` `BoxWithConstraints` with
  the four children in contract order — `HomeAtmosphere` (478) → `HomeTopChrome`
  (491) → `Box(padding(top = chromeHeightPx))` (508) holding the one `LazyColumn`
  (513) and `MomentumFab` (669). The scroll boundary is produced by **layout**
  from one `onSizeChanged` (`HomeChrome.kt:105`, forwarded at
  `TodayScreen.kt:496`), with `contentPadding.top = 0.dp`. No `nestedScroll`,
  `derivedStateOf`, `onGloballyPositioned`, `scrollState`, `parallax` or second
  scroll container anywhere on the screen.
- **§8.6 window.** `artworkWindow(w) = (ArtworkBandFraction * w.value).dp` with
  `ArtworkBandFraction = 460f / 640f`, fed `windowWidth = maxWidth` from the
  existing `BoxWithConstraints` — 230 / 258.75 / 296.125 dp at 320 / 360 / 412 dp,
  content-, state- and scroll-independent. BOUND-1 re-derived against
  `ProgressCardRingSize` = 142 / 170.75 / 208.125 dp against a 128 dp floor; I
  confirm the Developer's correction of the printed 142.75 dp.
- **Verbatim calls.** `MomentumHeroArtwork(Alignment.BottomCenter,
  Modifier.fillMaxSize().testTag(AtmosphereArtworkTag))` with no `zoom`,
  `parallax`, `ColorFilter` or dissolve override (`HomeAtmosphere.kt:92-97`);
  `HeroScrim` verbatim — four stops, `ScrimPeakStart 0.20f`,
  `ScrimPeakEnd 0.80f`, `ScrimPeakLight 0.26f`, `ScrimPeakDark 0.22f`,
  `drawWithCache` (`HomeAtmosphere.kt:166-194`).
- **§8.7 horizontal card.** Fraction + suffix left on one `Alignment.Bottom` row
  (`HomeCards.kt:298-318`), 96 dp ring right with the **percentage inside**
  (328-354), `HorizontalDivider(Hairline, textPrimary @ 0.08)` (356), then the
  breakdown row (361-397). The rejected TASK-017 vertical lockup is gone:
  `HeroProgressLockup`, `HeroRingSize`, `HeroOrbGap` and `HomeHero.kt` no longer
  exist.
- **§8.8 sections.** `HomeSectionHeader` at `MomentumHeadlineSection` +
  `MomentumLabelMicro` with `Pending`/`Scheduled Today` and
  `Completed`/`Finished` + the new `verified` glyph; `HomeActivityCard` carries
  `TextDecoration.LineThrough` and `c.textSecondary` on a completed title
  (577-585) and `CARD`/`CONTROL` glass. `MomentumComponents.kt` and
  `ActivityCard.kt` are untouched.
- **§4.7 r3 / §7 streaks.** No `StreakBadge`, no `streak =`, no `Streak` token
  and no streak string on Home; no new streak component, domain field,
  persistence, repository or ViewModel change. (`TodayItem.currentStreak` and
  `today_encouragement_streak` are pre-existing TASK-016/017 behaviour, retained
  and positively asserted by `HomeAtmosphereAssetsTest:654-680`; §4.7 r3 forbids
  changes *to support* streak UI.)
- **§8.18 (A1) navigation.** Re-rendered **in place**, no Home-only fork:
  `LiquidGlassBottomBar` and `RowScope.LiquidGlassTab` have exactly one call site
  each (`MomentumNavGraph.kt:235`, `:237`), so the four destinations cannot
  desynchronise. `CircleShape` (was 26 dp), `heightIn(min = NavBarHeight = 64.dp)`,
  `horizontal = MomentumSpacing.standard` with **no** vertical padding,
  `SpaceAround` + `CenterVertically`, `defaultMinSize(62/56 dp,
  minHeight = MomentumHeights.controlMin)` — **48 dp for both states**,
  the two-stop 135° capsule from `auroraBlue`/`auroraLavender`/`surface` at
  `0.75f`/`0.85f` with alphas scaled by `capsuleProgress`, `1.dp` white border,
  `shadow(2.dp … c.primary @ 0.2f)`, 22/23 dp icons, `MomentumLabelMicro`
  Bold/SemiBold. `1.06f`, `weight(1f)`, the whole-tab plate and `labelMedium`
  are gone. **Behaviour untouched:** `selectTab` (`MomentumNavGraph.kt:118`), the
  `showBar` gate (`:206`), `Scaffold` / `contentWindowInsets` (`:201`, `:203`),
  the four destinations and every route — only the slot's three padding values
  changed (16 / 16 / 20 dp), with
  `WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)` retained.
  `HabitsScreen` passes neither FAB opt-in, so its FAB is unchanged.
- **§8.19 (A2) motion.** Exactly **one** `rememberInfiniteTransition("homeAmbient")`
  in `TodayScreen.kt:420`, created **only** when motion is allowed
  (`if (reduceMotion) null else`), deriving `ambientPulse` (`ambient` = 6000) and
  `boltPulse` (`verifiedPulse` = 2200) on `LinearEasing`. `HomeCards.kt`
  contains **zero** `rememberInfiniteTransition`, `animateFloatAsState`,
  `mutableStateOf` or `LaunchedEffect`; the phases arrive as `State<Float>` and
  are read **only** inside `drawWithCache` (`:685`) and `graphicsLayer` (`:466`),
  so no list item owns an animation instance and none recomposes per frame. The
  third loop lives in `MomentumFab.kt:112` gated on `glowPulse && !reduceMotion`
  and opted into by Home only. `HomeAtmosphere` is `animated = true` (`:266`)
  with `AuroraBackground` nulling its own phase. The entrance clock is one finite
  900 ms `animateFloatAsState` above the `AnimatedContent` branch key, driving
  four phases (0 / 100 / 200 / 300 ms) through `Modifier.homeReveal`'s layer
  transform. `flame-pulse` and `aurora-float` stay unbound and are negatively
  asserted. (M3's phase read is finding §10.4.1.)
- **§8.5 / §9 tokens.** `hue` is the only `liquidGlass` extension: it is passed
  at **2 of 19** call sites (`HomeCards.kt:284` and an explicit
  `hue = null` at `:521`), so the other **17** are byte-identical — **R3
  satisfied**. **Zero** new colour tokens: `Color.kt`, `Tokens.kt`, `Shapes.kt`
  and `Theme.kt` are unmodified, and the capsule's white border is a literal
  alpha. Exactly **three** new type tokens with the named values
  (`MomentumHeadlineCompact` 24/32 600 **with `tnum`** → R4 resolved;
  `MomentumHeadlineSection` 20/28 600; `MomentumLabelMicro` 11/14 600 @ 0.04 em)
  and **three** new motion tokens (`enter = 900`, `fabGlow = 3000`,
  `verifiedPulse = 2200`). `MomentumFigureDisplay` stays declared and unused on
  Home.
- **Nothing invented.** No fake domain data, no new or generated artwork
  (`R.drawable` absent from the atmosphere; `habits_hero_light/dark.webp`
  unchanged), no parallax, no scroll choreography, no new dependency and **no
  build change** (`app/build.gradle.kts`, `libs.versions.toml`, `settings.gradle.kts`,
  `gradle.properties` all untouched).

**Test quality.** The suite is in the approved source-reading style (§8.18.6
item 1) and mostly pins behaviour and stable structure — the four-layer order,
the layout-produced boundary, the 460/640 derivation and its content/state/scroll
independence, BOUND-1 against the new ring, the scrim's unchanged four-stop
profile and in-envelope peaks, the horizontal lockup's ordering, the negative
assertions that the retired vertical machinery and the unbound keyframes stay
absent, `contentType`/`key`, the corrected `animated = true` plus
`AuroraBackground`'s own gate, and the tab-count / `selectTab` / `showBar` /
single-call-site pins that make a nav fork impossible. Two are weaker than they
read: `HomeAtmosphereAssetsTest:393-396` describes `showIdleGlyph` as affecting
the **idle** ring only, which is what let §10.2 pass (§8.15 does require a
completed-row token assertion and §8.18.6 item 1 requires the deviations to be
pinned, so a done-state glyph pin belongs here); and `HomeSectionMotionTest:201`
pins the FAB's glow **loop** but never asserts where its phase is read, which is
what let §10.4.1 pass. The re-pointed assertions the Orchestrator asked about
are **real and not weakened**: `HomeAtmosphereAssetsTest:443-456` asserts
`accent = c.success` **and** `tint = accent` for the glyph, `val labelColor =
c.textSecondary` **and** `color = labelColor` for the label, and
`assertFalse("accent ?:")`; `:354-361` adds a matching windowed assertion that
the encouragement line's call site carries `color = c.textSecondary` and **not**
`c.success`. Both survive comment-stripping via `codeOf`.

### 10.6 Bands that remain OPEN — no figure invented for any of them

1. **Date capsule** (`HomeChrome.kt:127-139`) — bare `textPrimary` over the
   artwork.
2. **Greeting** (`HomeChrome.kt:140-148`) — bare `textPrimary` over the artwork.
3. **Greeting support line** (`HomeChrome.kt:149-157`) — bare `textPrimary` over
   the artwork.

These three were already OPEN in §9 / §10.1 and **stay OPEN**: the illustration
is a lossy VP8 WebP with no decoder in this environment, and TASK-017's figures
do not transfer because the window grew 74.75 dp at 360 dp, so
`ContentScale.Crop` + `Alignment.BottomCenter` samples different illustration
rows at the same y. **Nothing is assumed about them in either direction.**

4. **NEWLY OPEN, and it is the substance of finding §10.3:** every band that
   sits on the **translucent** progress card or on the composited atmosphere —
   the encouragement/statement line, the suffix, the ring percentage, the ring
   `done`, `n Completed` / `n Pending`, the divider, both section headings, the
   `Finished` label, the completed card title, the emerald control and the ring's
   7.2 dp `textPrimary @ 0.55` track. The `Finished` label's own backdrop is the
   composited Aurora field (its position is below the artwork window, so it is
   less exposed than 1–3), and `c.textSecondary` measures 5.55:1 on the bare
   Light page `#F7F8FC` — but "the page" is not the composited field, so the
   band is not closed either.
5. **The four `c.success` uses are confirmed as non-text surfaces with their own
   obligation, and were correctly left alone** — the ring's `arcBrush` gradient,
   the 8 dp breakdown dot, the `verified` glyph and the emerald completion
   control. Each carries the **≥ 3:1** graphical-object obligation (WCAG 1.4.11),
   not the 4.5:1 body-text floor, and none is a text band. Spot values from the
   sRGB in `Color.kt`: the ring arc's `success` over the card spans
   3.13–4.26:1 (the arc sits on the card's lighter right-hand stops); the
   breakdown dot is `success` on the same surface; the `verified` glyph is
   `success` on the composited atmosphere (4.02:1 on the bare Light page); the
   emerald control's fill is `success → lerp(success, auroraMint, 0.5)` =
   `#41AD93 → #55C8AD`, whose own boundary against the card is 2.03–2.77:1. That
   last figure is recorded as **OPEN** alongside the others: it is a
   boundary-contrast question for a **filled control with a white hairline
   border**, and its correct resolution depends on §10.2 — if the check glyph is
   restored it is tinted `c.onPrimary` (`#172033` in Light), not white, and the
   check is then text-like ink on the fill and needs its own 4.5:1 check.

**What closing 1–5 requires:** an environment that can decode
`habits_hero_light.webp` / `habits_hero_dark.webp` (or the device), applied to
the **region-sample** method of ADR-47 Amendment Note 1 — each band's text
rectangle composited from page background + Aurora field at the swept
`defaultIntensity` + artwork (cover-crop, `BottomCenter`, the shared 88 dp bottom
dissolve) + the one four-stop scrim over the whole content area — then the
**minimum** ratio across the full sweep for each band, per theme. Until that is
run, these bands are OPEN and must not be recorded as passing.

### 10.9 Re-review of Correction 2 — PASS

Scope: §10.7 and §10.8's four rulings only, plus the regression guard. The
earlier findings (§10.1–§10.6) are retained above unchanged for the record.
**`adb` is unavailable**, so nothing here is a device, screenshot, pixel, bounds
or on-device observation, and **no instrumented test was executed**.

**Verdict per item**

| Item | Ruling / finding | Verdict |
|---|---|---|
| Check glyph | §10.8 Ruling A (my §10.2) | **Resolved** |
| Ink | §10.7 (my §10.3) | **Resolved** — floor established |
| Visible denominator | §10.8 Ruling B (my §10.4.3) | **Resolved** |
| FAB phase read | §10.8 Ruling C.1 (my §10.4.1) | **Resolved** |
| `HomeAtmosphere` KDoc | §10.8 Ruling C.2 (my §10.4.2) | **Resolved** |
| Regression guard | §10.5's confirmed-conformance list | **Undisturbed** |

**1 — the check glyph (`CompletionButton.kt`).** `val glyphShown = showIdleGlyph
|| completed` (line 111) now gates **both** carriers: the `Icon` (line 200) and
`Modifier.semantics { this.contentDescription = label }` (lines 191–197). Done
state ⇒ `glyphShown == true` ⇒ the emerald disc always carries the check, per
`screen.png` and `code.html:96`; Home's idle row ⇒ `false` ⇒ the reference's
empty ring. Because `Modifier.clickable`'s node merges descendant semantics, the
drawn `Icon`'s `contentDescription` bubbles into the one merged control node;
with the glyph suppressed the control carries the label itself. The two are
mutually exclusive by construction — **exactly one announcement in both states,
never two, never none.** The default is still `true` (line 99) and
`ActivityCard.kt`, `ActivityDetailScreen.kt` and `DesignSystemPreviews.kt` are
**byte-identical** (`git diff --stat` on all three is empty). The 48 dp target,
`Role.Checkbox`, the action label, the reversal, the haptic and the spring pop
are untouched. The `@param` KDoc (lines 80–86) now states the real idle-only
contract and cites `code.html:96` / `screen.png`.

The corrected test is **real and stronger, not weakened**
(`HomeAtmosphereAssetsTest.kt:427-460`): it pins the call-site
`showIdleGlyph = false`, `val glyphShown = showIdleGlyph || completed`, the
presence of `if (glyphShown) {`, `assertFalse("if (showIdleGlyph) {")` as a
regression guard, **and** `assertEquals(2, occurrences("if (glyphShown) {"))` —
which is what makes "both carriers, both gated" enforceable. All survive
comment-stripping via `codeOf`.

**2 — the ink (`HomeCards.kt`).** Both bands are `c.textPrimary`: the
encouragement / completion-statement call site (line 334) and the `Finished`
label. The latter is a new **additive, default-`null`**
`HomeSectionHeader(labelColor: Color? = null)` (line 455) with
`val statusInk = labelColor ?: c.textSecondary` (line 462), and `TodayScreen.kt:637`
opts in with `labelColor = c.textPrimary` for `Completed` only; `Pending` passes
no `labelColor`, so `Scheduled Today` is still `c.textSecondary` (the old
`accent ?:` derivation is gone, and `assertFalse("accent ?:")` pins that).
`accent` is **glyph-only** (`tint = accent`, line 484).

**Contrast recomputed independently** from `Color.kt`'s sRGB values modelling the
wash `lerp(surface, auroraLavender, 0.55)` (`#C3B5F2` in Light) at
`heroFill = 0.68` composited over each backdrop, WCAG relative luminance. I
reproduce §10.7's tinted-stop rows exactly (`#D6CDF6` / `#D4CAF5` / `#CCC3F2` /
`#BDB3EA`), which confirms the arithmetic. Minimum per ink over that sweep:

| ink | darkest tinted stop, over `#B0B0D8` | over a darker `#9090B0` |
|---|---|---|
| `c.textSecondary` | **3.04:1 FAIL** | **2.70:1 FAIL** |
| `c.textPrimary` | **8.38:1 pass** | **7.46:1 pass** |

So `c.textPrimary` clears **≥ 4.5:1** with a wide margin at the card's
governing (darkest) stop over a dark backdrop — the case that invalidated
`textSecondary` (which fails at every backdrop, confirming my §10.3). In **Dark**
the asymmetry reverses: the ink is `#F5F7FC` (L 0.9296), so the governing case is
the *lightest* stop, and `c.textPrimary` still measures **7.65:1** over the bare
Dark page `#111827` and 6.36:1 over a `#3A3A5A` field — a strict improvement on
the prior Dark ink (`textSecondary` = 4.11:1 and 3.41:1, i.e. it *failed* on the
bare Dark page), so the change cannot regress Dark. Dark's absolute floor over an
arbitrarily bright artwork highlight is inside the same unmeasurable composite
that already keeps §10.6 item 4 OPEN; I invent no figure for it.

The superseded numbers are gone from code: no `5.08`, `5.09` or `5.11` remains in
any source file; the only figures asserted are the corrected failing-ink ones
(`3.03–3.91:1` for `textSecondary`, `HomeCards.kt:174-175` and
`HomeProgress.kt:79`), which I reproduce. `§10.1` and `§9` Correction 1 remain in
the record but are explicitly marked superseded.

The four **legitimate** `c.success` non-text surfaces are untouched:
`HomeCards.kt:347` (ring `arcBrush`), `:383` (breakdown dot), `TodayScreen.kt:630`
(`verified` glyph tint) and `HomeCards.kt:631`
(`accent = CompletionAccent.SUCCESS`). **No new colour token**
(`Color.kt` / `Tokens.kt` / `Shapes.kt` / `Theme.kt` all unmodified) and **no
scrim peak** moved (`ScrimPeakLight 0.26f` / `ScrimPeakDark 0.22f`,
`ScrimPeakStart 0.20f` / `ScrimPeakEnd 0.80f`, four stops, `drawWithCache`).

**3 — Ruling B, the visible denominator.** `today_hero_fraction` is
`%1$d of %2$d` and `HomeCards.kt:318` passes
`stringResource(R.string.today_hero_fraction, done, total)`, so the row reads
**`3 of 5` + `items done`** — `screen.png` and `code.html:92`. Both values already
arrive in `HeroFigureState.Fraction(done, total)` (lines 237–246): **no new data
source, no new token**. The merged `today_hero_progress_desc` is unchanged
(`%1$d of %2$d completed today`), and the visible text is inside the
`clearAndSetSemantics` group, so **nothing double-announces**. Pinned by
`HomeAtmosphereAssetsTest.kt:401-412`, including a negative for the
numerator-only form.

**4 — Ruling C.1, the `Modifier.shadow` substitution (`MomentumFab.kt:145-154`).**
Assessed critically; it is **genuinely equivalent**. `Modifier.shadow`'s own
Android implementation is `this then GraphicsLayerBlock(BlockGraphicsLayerModifier{ shadowElevation = …; shape = …; clip = …; ambientShadowColor = …; spotShadowColor = … })`, i.e. exactly these five properties on exactly one layer at the same chain position — so this is the same layer written out, not a different effect. Each value matches the prior call: elevation `glowShadowElevation(phase).toPx()`, `shape = CircleShape`,
`ambientShadowColor = shadowAmbient`, `spotShadowColor = c.primary.copy(alpha =
glowSpotAlpha(phase))`. `clip = true` reproduces the old defaulted
`clip = elevation > 0.dp` because `MomentumGlass.heroElevation = 12.dp > 0`
(`Tokens.kt:126`), so the old `elevation > 0.dp || clip` guard was always taken
and no "no modifier" case existed. Preserved: the **3 s** period
(`MomentumMotion.fabGlow = 3000`), the reduced-motion nulling to a resting `0f`
frame (line 116 `if (glowPulse && !reduceMotion)`), `glowPulse = false` by default
so **Habits' FAB is byte-identical** (it now renders `12.dp` / spot `0.40f`,
exactly the pre-change resting values), and `rotateGlyphOnPress` untouched
(lines 128–132, 202). `glowPhase.value` is read **only** at line 146 inside the
layer block, so no composable-body read remains — the `graphicsLayer` block runs
in the layer phase, so the `State` read cannot subscribe composition. This is the
same pattern already accepted for `HomeCards.kt`'s `ambientPulse` / `boltPulse`.
`QuickAddTest`'s assertions were **strengthened, not weakened**: they now pin the
mechanism positively (`val phase = glowPhase.value`, `spotShadowColor = …
glowSpotAlpha(phase)`, line 70 `shadowElevation = glowShadowElevation(`),
**negatively** (`glowPhase.value)` absent — which is exactly the old defect;
`Modifier.shadow(` absent), and `assertEquals(1, count("shadowElevation = "))`
keeps it a single shadow. `HomeSectionMotionTest.kt:211-216` independently pins
where the phase is read, which is the assertion whose absence let §10.4.1 pass.

**5 — Ruling C.2, `HomeAtmosphere.kt`.** Lines 65–66 now print **142 dp @320 ·
170.75 dp @360 · 208.13 dp @412**. The figures are right: `artworkWindow(360) =
(460/640) × 360 = 258.75`, `ArtworkHandOff = 88`, `258.75 − 88 = 170.75`; at 320
→ 142 and at 412 → 208.125. No code or test still asserts the superseded
`142.75`; the only remaining occurrences are the *negative* explanation comment
at `HomeAtmosphereAssetsTest.kt:158` and the historical plan text at
`task.md:513` / `DECISIONS.md:3853`, which are records, not code, and outside
§10.8 Ruling C.2's stated scope.

**6 — regression guard.** Re-verified by source inspection, none disturbed: one
`LazyColumn` (line 513) with no `nestedScroll` / `derivedStateOf` /
`onGloballyPositioned` / scroll-state read / `parallax`; the boundary still
produced by **layout** from one `onSizeChanged` → `onHeightChanged` (496) →
`padding(top = chromeHeightPx.toDp())` (511); the four-child root order
`HomeAtmosphere` (478) → `HomeTopChrome` (491) → `Box(padding(top = …))` (511) →
`MomentumFab` (675); no `StreakBadge`, no `streak =`, no streak UI (only the
pre-existing TASK-016/017 `currentStreak` / `today_encouragement_streak`); exactly
**one** `rememberInfiniteTransition("homeAmbient")` at line 420 created only when
motion is allowed (`if (reduceMotion) null else`); the A1 nav still one call site
each for `LiquidGlassBottomBar` (235) and `LiquidGlassTab` (237) with
`selectTab` (118/241) and `showBar` (206) untouched; `MomentumHeroArtwork`
verbatim (`Alignment.BottomCenter`, `fillMaxSize`, `testTag`, no `zoom` /
`parallax` / `ColorFilter` / dissolve override) and `HeroScrim` verbatim; and
**no new dependency and no domain change** (`app/build.gradle.kts`,
`libs.versions.toml`, `settings.gradle.kts`, `gradle.properties` all unmodified;
no `domain/`, `data/` or persistence file in the diff).

**Validation executed (real runs, no `UP-TO-DATE` reported as a result)**

| Command | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **BUILD SUCCESSFUL** — 42 suites, **292 tests, 0 failures, 0 errors, 0 skipped** (baseline 292 holds) |
| `:app:assembleDebug` | **BUILD SUCCESSFUL** |
| `:app:assembleDebugAndroidTest --rerun-tasks` | **BUILD SUCCESSFUL** — **compiles** the instrumented sources only (61 tasks executed); **no instrumented test was executed** |

The §8.16 18-point device matrix, §8.18.5's four-destination nav check, Layout
Inspector's zero-recomposition fling and the on-device ambient / reduced-motion
behaviour all remain the human's.

**Stays OPEN — no figure invented for any of them**

- **The three artwork-overlaid chrome bands** (date capsule, greeting, greeting
  support line) remain **OPEN**. `adb` is unavailable and `habits_hero_light.webp`
  / `habits_hero_dark.webp` are lossy VP8 with no decoder here; TASK-017's figures
  do not transfer because the window grew 74.75 dp at 360 dp, so
  `ContentScale.Crop` + `BottomCenter` samples different illustration rows.
- **§10.6 item 4** (every band on the translucent progress card or the composited
  atmosphere, including the two bands just re-inked) also stays **OPEN** for the
  same reason. The correction makes them measurably better, not proven-final.
- **To close either set:** a WebP-capable environment or the device, applying the
  **region-sample** method of ADR-47 Amendment Note 1 — each band's text rectangle
  composited from page background + Aurora field at the swept
  `defaultIntensity` + artwork (cover-crop, `BottomCenter`, the shared 88 dp
  bottom dissolve) + the one four-stop scrim over the whole content area — then
  the **minimum** ratio across the full sweep, per theme.

**Resolved by the Orchestrator (2026-10-01, record accuracy, minor).** `task.md`
§10.7 stated `AuroraLight.textPrimary` (`#172033`) as **"L 0.0011"** and derived *"a
composited luminance of only −0.045"* from it. Independently recomputed: **L =
0.01454**, so the real Light threshold is **0.01544**, not `−0.045`. Both numbers
were wrong in the record. **Neither appeared in any code or KDoc, and neither changed
the ruling or the shipped ink** — the operative claim (clears 4.5:1 at the darkest
stop over a dark backdrop) is verified by direct ratio computation, so this never
blocked. **§10.7 and the §9 Correction 2 record now carry the corrected values.**

**Out of scope, reported for information only (pre-existing, not a regression, not
graded here):** `HomeCards.kt:185` states the encouragement "sits **outside** that
group and keeps its own announcement", but `HomeEncouragementLine` is in fact
inside the `clearAndSetSemantics` column (lines 304–337), so its copy is cleared
by the merge and only `today_hero_progress_desc` is announced. This predates
Correction 2, was not changed by it, and lies outside the corrected scope; noted
for the Orchestrator rather than raised as a finding against this task.

### 10.10 Reviewer verdict — 2026-10-01 (§13.1 ruling applied): **PASS**

**PASS. No actionable defect found.** §10.1–§10.9 retained unchanged; this
subsection records the narrow re-review of the §13.1 human ruling.

**Item by item**

1. **All three bands are `c.textPrimary` — confirmed.** `HomeCards.kt:352`
   (`today_progress_suffix`), `:388` (`today_progress_ring_unit`), `:433`
   (`cd_progress_pending`). The breakdown row is uniform with `n Completed`
   (`:416`, already `textPrimary`); its relational structure is carried by the
   two dots (`c.success` `:410` / `c.textMuted` `:426`), never by two inks.
   Comments stripped, **zero** `c.textSecondary` in `HomeProgressCard`'s body
   (`:246–439`).
2. **Nothing else changed — confirmed.** The `liquidGlass` wash
   (`GlassLevel.HERO`, `hue = c.auroraLavender`, `:311–318`), `HueMix = 0.55f`
   (`LiquidGlass.kt:169`), `heroFill` (`:72`), the 135° gradient
   (`LiquidGlass.kt:143–145`), every alpha, both scrim peaks
   (`HomeAtmosphere.kt:231–232`, `0.26f` / `0.22f`) with `ScrimPeakStart` /
   `ScrimPeakEnd` unchanged, the ring's `arcBrush`
   (`listOf(c.success, c.auroraBlue, c.auroraViolet)`, `:372`), and all
   layout/typography/spacing tokens are untouched. No new colour token, no dp
   literal, no dependency.
3. **`Scheduled Today` unchanged — confirmed.** `HomeCards.kt:491`
   `val statusInk = labelColor ?: c.textSecondary` with
   `labelColor: Color? = null` (`:484`); `TodayScreen.kt`'s `Pending` header
   (`:584–585`) passes no `labelColor`, and only `Completed`'s `Finished` opts
   in (`TodayScreen.kt:637`).
4. **Semantics preserved — confirmed.** `CompletionButton.kt:111`
   `val glyphShown = showIdleGlyph || completed`, with the check glyph always
   drawn when complete (`:200`) and both label carriers gated on it (`:192`,
   `:200`); `accent` stays glyph-only (`HomeCards.kt:509–523`, no `accent ?:`
   anywhere); emerald non-text surfaces all `c.success` — ring gradient `:372`,
   breakdown dot `:410`, `verified` glyph tint via `accent = c.success`, and the
   completion control `CompletionAccent.SUCCESS` (`:660`).
5. **Stitch structure intact — confirmed.** Horizontal lockup `:321–392`, ring
   with the percentage inside `:377–390`, divider `:393–396`, breakdown row
   `:398–436`; reading order pinned, merged
   `clearAndSetSemantics { contentDescription = description }` (`:329`) over
   `today_hero_progress_desc` (`:276`) unchanged, `heading()` on the section
   title `:503`, `showPercent = false` (ring decorative).
6. **No streaks — confirmed.** Only the KDoc statements and the pre-existing
   TASK-016/017 `currentStreak` / `today_encouragement_streak` domain copy.
   No `StreakBadge`, no streak UI.
7. **No unrelated change — confirmed.** Only `HomeCards.kt` (19:26) and
   `HomeAtmosphereAssetsTest.kt` (19:31) carry a modification time after the
   prior pass; every other `app/` file predates it. No `domain/`, `data/`,
   repository, ViewModel, artwork or navigation change. **Nothing committed —
   HEAD is still `47ebfb4`.**
8. **Test quality — confirmed, real and not weakened.** The new
   `every band on the progress card is textPrimary while Scheduled Today stays
   textSecondary`
   (`HomeAtmosphereAssetsTest.kt:464–539`) asserts the exact `color =` line for
   all six card string bands **plus** the ring's percentage, asserts **no**
   `c.textSecondary` survives anywhere in the card, pins the emerald `arcBrush`
   and both dots, and asserts `Scheduled Today` is still the
   `labelColor = null` → `c.textSecondary` default with `Pending`'s call site
   carrying no `labelColor`. Comments are stripped by `codeOf`, so comments
   cannot satisfy it. The pre-existing encouragement-line (`:362–371`) and
   section-heading (`:588–607`) ink assertions are intact. The
   `HomeProgressCard` KDoc (`:182–202`) records the accessibility-driven
   deviation and warns against restoring the slate hierarchy.

**Validation executed (real runs; no `UP-TO-DATE` reported as a result)**

| Command | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **BUILD SUCCESSFUL** — 42 suites, **293 tests, 0 failures, 0 errors, 0 skipped** (baseline 292 + 1 new) |
| `:app:assembleDebug --rerun-tasks` | **BUILD SUCCESSFUL** (78 tasks executed) |
| `:app:assembleDebugAndroidTest --rerun-tasks` | **BUILD SUCCESSFUL** — **compiles the instrumented sources only; no instrumented test was executed** |

**`adb` is unavailable.** No instrumented test ran, no screenshot, pixel,
bounds, Layout-Inspector, on-device or reduced-motion evidence exists or is
implied. Device matrix items 1–17 remain the human's.

**Stays OPEN — no figure invented for any of them**

- **Every artwork-composite contrast band.** The date capsule, the greeting, the
  greeting support line, **all bands on the progress card** measured against the
  real illustration, and the nav's active capsule. These resolve against the
  artwork composite and cannot be computed here; TASK-017's numbers do not
  transfer. Unmeasured and **not assumed to pass**.
- **The two row-card bands, recorded as an open item, not a verdict.**
  `HomeCards.kt:632` (completed-row title, `c.textSecondary` when completed) and
  `:644` (row metadata, `c.textSecondary`) sit on the **non-chromatic**
  `liquidGlass(CARD/CONTROL, hue = null)` row card, so they were outside §13.1's
  scope and are correctly untouched. Orchestrator arithmetic suggests they may
  fall below the ≥ 4.5:1 floor over a dark artwork region, but that is
  **unverified** and is **not** a finding. No scope is expanded to fix them.
- **To close either set:** a WebP-capable environment or the device, applying the
  **region-sample** method of ADR-47 Amendment Note 1 — each band's text
  rectangle composited from page background + Aurora field at the swept
  `defaultIntensity` + artwork (**§4.8 geometry**: complete image at intrinsic
  1024×1536 ratio, top-aligned, width-scaled, `ContentScale.Fit`, with the shared
  88 dp bottom dissolve) + the one four-stop scrim over the whole content area —
  then the **minimum** ratio across the full sweep, per theme, against the
  **≥ 4.5:1** floor (not weakened; only the scrim **peak** may move, inside
  `[0.15, 0.35]`). The ADR-47 AN1 samples predate §4.8 and must be re-taken on
  the new geometry — none of the old figures transfer.

### 10.11 ISSUE-020 final-geometry re-measurement: **FAIL — independently verified** (2026-10-02)

**Method:** ADR-47 AN1 region-sample on the frozen §4.8/§4.9 geometry, re-run
independently with corrected scripts (original run reproduced exactly; 5/5
reported numbers reproduced by an independent spot-check; channel order verified
via WIC `Bgra32` at 5 pixels + band means). **Stop condition triggered:** defects
found → return to human for ruling per §4.9; **no fix applied, nothing changed**
(zero `app/**` changes; correction budget exhausted).

**Governing corrected table — worst attainable ratio (supersedes the Architect's
original 23 FAIL / 3 PASS table): 16 FAIL / 9 PASS / 1 borderline of 26.**

| Region | Band (floor) | Light | Dark | Verdict | vs reported |
|---|---|---|---|---|---|
| Chrome | Date capsule text (4.5) | 10.16 | 5.39 | PASS | reported FAIL = artifact (F1,F2) |
| Chrome | Settings icon (3.0) | 3.76 | 4.10 | PASS | unchanged |
| Chrome | Greeting (4.5) | 7.30 | 6.45 | PASS | reported FAIL = artifact (F1,F2) |
| Chrome | Greeting support (4.5) | 7.57 | 4.91 | PASS | reported FAIL = artifact (F1,F2) |
| Progress card (first item) | textPrimary (4.5) | 8.51 | 4.52 rest / 3.99 scrolled-model | PASS at rest — borderline | reported FAIL = corrected (F1–F3) |
| Progress card | success dot (3.0) | 2.23 | 3.03 rest | FAIL (L) | confirmed |
| Progress card | textMuted dot (3.0) | 1.62 | 1.20 | FAIL both | confirmed |
| Progress card | ring track (3.0) | 3.01 rest | 2.50 | FAIL (D) | confirmed |
| Progress card | ring arc (3.0) | 1.00 | 1.00 | FAIL both | confirmed |
| Progress card | ring % (4.5) | 2.83 | 1.81 | FAIL both | confirmed |
| Empty state (rest only) | body (4.5) | 3.65 | 2.77 | FAIL both | FAIL confirmed, numbers corrected (F1–F3) |
| Headings (attainable bound) | Pending title (4.5) | 8.94 | 4.57 | PASS | reported FAIL = artifact (F3) |
| Headings | "Scheduled Today" meta (4.5) | 3.24 | 2.45 | FAIL both | confirmed |
| Headings | Completed title (4.5) | 8.72 | 4.57 | PASS | reported FAIL = artifact (F3) |
| Headings | "Finished" meta (4.5) | 8.72 | 4.57 | PASS | reported FAIL = artifact (F3) |
| Headings | verified glyph (3.0) | 2.34 | 3.06 | FAIL (L) | confirmed |
| List rows (scroll-reachable) | Pending row title+meta (4.5) | 4.59 | 1.98 | FAIL (D) | confirmed |
| List rows | Completed row title (4.5) | 4.33 | 2.03 | FAIL both | confirmed |
| List rows | check glyph on emerald (4.5) | 3.82 | 12.14 | FAIL (L) | confirmed (position-independent) |
| List rows | emerald fill boundary (3.0) | 2.13 | 2.54 | FAIL both | confirmed |
| List rows | empty ring boundary (3.0) | 1.68 | 1.24 | FAIL both | confirmed |
| Bottom nav (fixed) | inactive label (4.5) | 4.92 | 3.34 | FAIL (D) | confirmed |
| Bottom nav (fixed) | inactive icon (4.5) | 4.92 | 3.34 | FAIL (D) | confirmed |
| Bottom nav (fixed) | selected label (4.5) | 13.57 | 6.23 | PASS | unchanged |
| Bottom nav (fixed) | capsule icon (4.5) | 9.81 | 5.81 | PASS | unchanged |
| Bottom nav (fixed) | capsule boundary (3.0) | 1.11 with border | ≈5.6 with border | FAIL (L) / PASS (D) | D reported 1.02 FAIL = artifact (F4) |

**Four script defects (in `measure020.js`):**
- **F1 channel-order** (line 56): R/B swapped for all artwork pixels → corrupts every artwork-composited band.
- **F2 unattainable scrim pole** (`backdrops()` `scrim:0`): profile is 0 only off-screen → governed most reported worst cases.
- **F3 unreachable y-scan** (fixed 156 → viewport bottom): first item cannot sit below rest (`TodayScreen.kt:542-554`, `contentPadding.top=0`) → flipped 7 bands FAIL→PASS (date capsule, greeting, support, 3 heading labels, progress-card-at-rest).
- **F4 missing capsule border** (`LiquidGlass.kt:483-484`, 1 dp White@0.9): Dark boundary flips FAIL→PASS with the real border; Light fails either way.

**Worst confirmed failures:** pending row Dark 1.98, completed row Dark 2.03,
textMuted dot Dark 1.20, ring boundary Dark 1.24, ring % Dark 1.81, empty state
Dark 2.77 / Light 3.65, Scheduled Today Dark 2.45, verified glyph Light 2.34,
nav inactive Dark 3.34, check glyph Light 3.82, capsule boundary Light 1.11,
success dot Light 2.23, ring track Dark 2.50, ring arc 1.00 both themes.

**Status:** ISSUE-020 stays **OPEN**; the `DECISIONS.md` closure note is blocked
pending human ruling. `measure020.js` remains **defective and superseded**;
corrected runs live only in temp (`measure020-reviewer.js`, `spotcheck.js`,
`attainable*.js`). Regenerating the official script is **deferred — not
authorized** under the correction budget. Zero `app/**` changes.

## 11. HANDOFF HISTORY

<!-- ORCHESTRATOR ONLY -->

- **2026-10-01 — Orchestrator → Architect.** TASK-018 opened. TASK-017's record
  archived to `task_archive/TASK-017.md`. Reference artifacts staged to the allowed
  temp path. Architect briefed to produce the plan only; no code authorized.
- **2026-10-01 — Architect → Orchestrator.** Plan delivered in §8; ADR-47 AN7,
  ADR-48 AN5 and ADR-49 appended. Only `task.md` and `DECISIONS.md` were written;
  **no application code was created or modified**, so TASK-015/016/017 work is
  untouched and nothing is authorized for implementation.
- **2026-10-01 — Orchestrator verification.** Every load-bearing factual claim in
  §8 was re-read from source. All confirmed except two call-site counts, both
  corrected in §7 in the direction that *strengthens* the plan's conclusions; no
  architectural claim was falsified. `DECISIONS.md` diff is **+1790 / −0** —
  append-only, no existing ADR text rewritten.
- **2026-10-01 — Orchestrator → HUMAN.** Stopped at the approval gate. The next
  action is the human's ruling on §8.17 items 1–8 and the eight conflict
  rejections. Developer and Reviewer are **not** authorized until that ruling.
- **2026-10-01 — HUMAN → Orchestrator.** Plan **APPROVED** with two explicit
  reversals recorded as §4.7: **(A1)** the bottom navigation must match the Stitch
  visual exactly, overriding §8.10/§8.12 C10 and authorising a clean refactor of
  the shared nav component (no Home-only fork, no behaviour change); **(A2)** the
  motion set is expanded to include Aurora movement/glow-pulse, FAB glow/pulse and
  FAB interaction scale/rotation, withdrawing §8.11's C11 rejection and reversing
  ADR-48 §6. Streaks explicitly removed (§7) and secondary-info limits confirmed
  (§8). Workflow per §24: no further human gate before the Developer unless a
  genuinely new conflict appears.
- **2026-10-01 — Orchestrator → Architect (amendment pass, specification only).**
  A1 and A2 routed for amendment because each reverses an approved decision **and**
  carries ADR + green-test consequences, which `AGENTS.md` §21 assigns to the
  Architect. Everything else in §8 stands and must not be re-opened.
- **2026-10-01 — Architect → Orchestrator.** A1 and A2 recorded in §8.18/§8.19;
  §8.3, §8.7, §8.9, §8.10, §8.11, §8.12 C10/C11, §8.14, §8.15, §8.16 corrected in
  place so the document is internally consistent. Appended **ADR-49 Amendment
  Note 1** (nav) and **ADR-48 Amendment Note 6** (motion, superseding §6 for Home).
  `DECISIONS.md` diff now **+2005 / −0** — still append-only. `app/` is
  **byte-identical** to the TASK-017 baseline (13 files, +986/−1768, unchanged).
- **2026-10-01 — Orchestrator verification of A1/A2.** The Architect's
  occurrence-count claims were re-verified against `code.html`: `aurora-float` = 1
  (unbound, M7 ruling correct), `anim-flame` = 1 (unbound, M6 ruling correct),
  `pb-safe` = 1 (defined-but-unbound, so §8.18.3's safe-area reasoning holds),
  `anim-bolt` = 2, `anim-aurora-blob` = 3, `anim-fab-pulse` = 2, `anim-ring-fill` = 2
  (all genuinely bound). §1 status and routing updated.
- **2026-10-01 — Orchestrator → Developer.** Implementation authorized. Scope is
  §8.1–§8.17 as amended, plus §8.18 and §8.19 in full.
- **2026-10-01 — Developer → Orchestrator.** Implementation built and green:
  **292 unit tests pass with `--rerun-tasks`**, `:app:assembleDebug` and
  `:app:assembleDebugAndroidTest` succeed. `adb` unavailable, so **no instrumented
  suite ran and no screenshot/pixel/bounds claim is made**. One ISSUE-020 band
  **failed** and three could not be measured; six implementation interpretations were
  recorded, not hidden. Nothing committed; TASK-015/016/017 preserved.
- **2026-10-01 — Orchestrator adjudication (§10.1).** Verified the failing band
  independently: `c.success` on the Light progress card is **3.72:1** against a
  **4.5:1** floor. Established the cause is a **§8.7/§8.8 mis-mapping of the
  reference** — `code.html` colours those two bands `#00687a` / `#005236` (5.55:1 /
  8.19:1), not emerald — and **ruled the ink to `c.textSecondary`** (5.08:1), which
  keeps the floor, adds no colour token and moves no scrim peak. The emerald signal
  stays on the `verified` glyph and the emerald completion control.
- **2026-10-01 — Orchestrator → Architect (record) → Developer (re-ink) →
  Reviewer.** Two open items: the Architect appends the `DECISIONS.md` record of the
  corrected ink plus the completed sweep table (the Developer could not write to
  `docs/**`), and the Developer re-inks the two bands and re-runs the unit tests.
  The three unmeasurable bare-ink chrome bands stay open for the human/Reviewer
  (§8.16).
- **2026-10-01 — HUMAN → Orchestrator (Device Finding #1).** New authoritative
  reference screenshot supplied for the Home **background** composition: complete
  Aurora artwork (intrinsic ratio, top-aligned, width-scaled, no crop) → quiet
  lavender continuation → large negative space; fixed, non-scrolling, foreground
  untouched. The `460/640` `ArtworkBandFraction`/`ArtworkWindow` abstraction is
  explicitly withdrawn. Recorded as §4.8; §8.6 marked superseded.
- **2026-10-01 — Orchestrator → Developer.** Routing: Developer → Reviewer (no
  Architect: the composition decision is the human's and the architecture is
  unchanged). Scope is §4.8 only — `HomeAtmosphere.kt` (+ the minimal shared
  `MomentumHeroArtwork.kt` change needed to stop cover-cropping on Home's path),
  the two tests that encode the withdrawn band, then unit tests + both assemble
  targets. Foreground UI and all other files are out of scope.
- **2026-10-01 — Developer → Orchestrator.** §4.8 implemented: `ArtworkBandFraction`
  → `ArtworkIntrinsicRatio = 1536f/1024f` (window 480/540/618 dp at 320/360/412);
  one additive defaulted `contentScale` param on `MomentumHeroArtwork` (Home passes
  `Fit`, Habits byte-identical); top-aligned, no dissolve/scrim/field changes, no new
  dp literal. Tests **corrected, not deleted** (they now also assert
  `460f / 640f`/`ArtworkBandFraction` absent from production code). Validation real:
  `--rerun-tasks` **42 suites / 293 tests / 0 failures**, both assemble targets
  succeed. Reported (not fixed): ISSUE-020 contrast sweep predates this geometry.
  Nothing committed.
- **2026-10-01 — Reviewer → Orchestrator.** **PASS on all nine §4.8 checklist
  points** (evidence at file:line, see §10 Finding #1 review). Re-ran
  `testDebugUnitTest --rerun-tasks` itself: 42/293/0, real execution, no
  UP-TO-DATE. Verified tests corrected rather than weakened, shared-component
  change minimal/additive, no foreground file touched (mtime + diff evidence),
  no commits (HEAD `47ebfb4`). Explicitly reserved points 1/2/6/7/8 as
   **on-device rendered acceptance = the human's**. ISSUE-020 confirmed reported,
   not silently fixed.
- **2026-10-02 — HUMAN → Orchestrator (closeout ruling).** Finding #1 **verified
  PASS on-device in Light and Dark** by the human; background declared a
  **frozen baseline**. Closeout scope set: bounded ISSUE-020 re-measurement on
  the final geometry, the outstanding `DECISIONS.md` note, independent Reviewer
  verification. Recorded as §4.9. A first Architect attempt overran (~3 h,
  exhaustive configuration enumeration) and was **stopped by the human**;
  re-dispatched as a bounded fixed-matrix verification.
- **2026-10-02 — Architect → Orchestrator (bounded sweep).** Single-script
  fixed-matrix run on the frozen geometry (WebP art already WIC-decoded; glass
  bound + bare-ink bands). Reported **FAIL, 23/26 bands below floor**; flagged
  two caveats (claimed "inverted" glass bound, all worst cases at an
  `alpha=0` scrim pole). No `app/**` changes; `DECISIONS.md` untouched (FAIL
  branch); 2/2 correction cycles consumed.
- **2026-10-02 — Reviewer → Orchestrator (independent verification).**
  **ISSUE-020 FAIL confirmed, figure corrected: 16 FAIL / 9 PASS / 1 borderline
  of 26** (governing table = **§10.11**). Found four defects in the sweep
  script (R/B channel swap corrupting all artwork composites; unattainable
  `scrim:0` pole; y-scan below reachable scroll positions; missing 1 dp
  capsule border), reproduced the original numbers exactly, then recomputed
  corrected attainable worst cases. Six bands the sweep called failing are
  artifacts and PASS (date capsule, greeting, support, both heading titles,
  `Finished`, Dark capsule boundary). **No file changed except task.md §10.11.**
  Per §4.9: no fix applied, returned to human.
- **2026-10-02 — Orchestrator.** §4.9 stop condition triggered. Stopped.
  ISSUE-020 stays OPEN; the ADR-47 AN7 §2 supersession note deferred until the
  human rules on the failing bands; no further measurement runs (correction
  budget exhausted); background untouched throughout.
- **2026-10-02 — HUMAN → Orchestrator (ISSUE-020 remediation ruling).** Bounded
  verification accepted as authoritative (§10.11). Ruled: no sub-floor values
  accepted · background/Aurora/scrim frozen · failing foreground remediated with
  **targeted local** contrast corrections · no blanket `c.textPrimary` · Stitch
  hierarchy preserved as far as possible · scrim is last resort only ·
  thresholds unchanged (4.5 text / 3.0 non-text). Recorded as **§4.10**.
- **2026-10-02 — Orchestrator → Architect.** Remediation-plan pass dispatched
  (specification only, no code). Deliverable: per failing region, §4.10 A–F
  (classification · current expression · existing-token answer · scoped
  high-contrast token if justified · minimum Stitch deviation · exact target
  ratio to verify), plus the explicit scrim justification test. Stops at the
  **human approval gate**; Developer not authorized.
- **2026-10-02 — Architect → Orchestrator (plan delivered).** **§8.20** written:
  16 region rulings R1–R16 (A–F each), three global enablers E1–E3, consolidated
  Developer change list (§8.20.2), special rulings §8.20.3 A–E, untouched
  passing regions §8.20.4, verification plan §8.20.5. **No new colour tokens;
  scrim untouched; thresholds unchanged; frozen background untouched.**
  Probe-verified against the §10.11 harness: every band meets its floor except
  the **ring arc**, classified decorative (§8.20.3 B). `DECISIONS.md` ADR-49
  **Amendment Note 4** appended (record only, append-only). Two `app/**` files
  of the change list are untouched — **zero application code changed**. Returned
  to the human at the §4.10 approval gate.

## 12. FINAL OUTCOME

<!-- ORCHESTRATOR OWNER -->

**Implementation complete. Device Finding #1 HUMAN-VERIFIED PASS (background
frozen). ISSUE-020 final-geometry re-measurement: FAIL — stopped for human
ruling (§4.9 stop condition).**

TASK-018 replaced the Home screen's presentation with a faithful native Compose
translation of the approved Google Stitch Aurora design, on top of TASK-017's
retained architecture. Two Reviewer blockers (a missing check glyph on completed
cards, an inverted contrast method) and two minor findings were found, fixed and
re-reviewed to a **PASS**. The human then reviewed the render on-device and
supplied **Device Finding #1**: the artwork band cropped the Aurora. Per §4.8 the
`460/640` band was withdrawn and replaced with the complete artwork at intrinsic
ratio (1.5 × width, top-aligned, `ContentScale.Fit`, no crop) over the unchanged
lavender field — implemented by the Developer, passed by the Reviewer on all nine
checklist points, and finally **verified PASS on-device by the human in both
Light and Dark** (§4.9). The background is now the accepted, frozen baseline.
Nothing is committed — commit and push remain Human-in-the-Loop, and HEAD is
still `47ebfb4`.

**Delivered:** the complete Aurora artwork rendered at its intrinsic 1024×1536
aspect ratio, top-aligned and width-scaled (480/540/618 dp at 320/360/412 width),
transitioning into the existing lavender atmosphere with a large lower
negative-space field — one fixed, non-scrolling composition; fixed Aurora
atmosphere and fixed date/greeting/settings chrome over a layout-produced scroll
boundary; the horizontal Stitch progress card reading `3 of 5 items done` with
the percentage inside a 96 dp gradient ring, an emerald-accented encouragement, a
divider and a completed/pending breakdown; Pending and Completed sections with
real domain data and no streaks; the Stitch FAB treatment; the Stitch bottom
navigation (one shared call site, no fork, navigation behaviour untouched); and
three ambient motion loops from one shared clock, entirely absent under reduced
motion.

**Verified:** 293 unit tests pass under `--rerun-tasks` (re-run by the
Reviewer); `assembleDebug` and `assembleDebugAndroidTest` both succeed; the
Reviewer passed all nine §4.8 points; **the human verified the background
on-device in Light and Dark — Finding #1 closed as PASS (§4.9).**

**ISSUE-020 on the final geometry: FAIL — independently verified, returned for
ruling.** The bounded fixed-matrix region-sample sweep (ADR-47 AN1) on the
frozen geometry, corrected and re-verified by the Reviewer (**governing table:
§10.11**), finds **16 of 26 required bands below the ≥ 4.5:1 floor** in at
least one theme (9 pass, 1 borderline). Worst confirmed: pending-row title Dark
1.98, completed-row title Dark 2.03, `textMuted` dot Dark 1.20, ring-track
boundary Dark 1.24, ring percentage Dark 1.81, empty-state body Dark 2.77 /
Light 3.65, `Scheduled Today` Dark 2.45, nav inactive label/icon Dark 3.34,
verified glyph Light 2.34, check glyph Light 3.82, capsule boundary Light 1.11,
ring arc 1.00 both themes. Per §4.9 **no ink, artwork, scrim, or code was
changed** — the failures are reported, not fixed. ISSUE-020 stays **OPEN**
pending the human's ruling. The original sweep's 23/26 figure is superseded
(four script defects, §10.11).

**Not verified here, and honestly so:** no instrumented test has been executed
and `adb` remains unavailable; the full device matrix beyond the background
confirmation (empty states, scrolling behavior, font scale 2.0, reduced motion,
TalkBack order) is still the human's. The sweep's remaining caveat: its
scrim-bearing worst cases sit at attainable-but-conservative positions
(short-screen pole) — if the human wants ruling figures at nominal screen
heights only, that is a re-run decision for a future authorized pass.

**Outstanding records for the Architect (both deferred, append-only):** the
ADR-47 AN7 §2 supersession note (intrinsic-ratio geometry) — held because the
ISSUE-020 closure it would carry has FAILed; and `ADR-49 Amendment Note 2`'s
§10.7 method-correction note.

---

**TASK-018 CLOSED — §8.20 Home-scoped ISSUE-020 remediation implemented and verified (2026-10-02).**

Following the human's §4.11 approval and the §8.20 revised Home-scoped plan, the
Developer implemented the remediation plus the three human-approved shared
blockers (theme-resolved nav capsule border — Light `textPrimary` / Dark white;
nav-scoped `fillAlpha = 0.85` on the bar's own `liquidGlass(CircleShape, HERO)`;
and an additive `idleRingBorderAlpha: Float? = null` on `CompletionButton` with
Home opting into `0.8`). Home-scoped material opt-ins (`fillAlpha 0.85` card /
`0.90` rows, `HueWashMidAlpha 0.8`, `TrackAlpha 0.7`, SUCCESS window
`[0.30, 0.34]`, `EmptyState(bodyColor)`), the approved targeted ink moves, and
ADR-49 Amendment Note 6 (append-only) are all in the working tree.

**Validation (all green):** `:app:testDebugUnitTest --rerun-tasks` → 42 suites /
**293 tests / 0 failures / 0 errors / 0 skipped**; `:app:assembleDebug` and
`:app:assembleDebugAndroidTest` → success (managed `gradle-run` wrapper,
Python 3.14). The Reviewer's bounded region-sample re-measurement of the changed
bands passed in Light and Dark with **zero FAIL rows**; thin margins clear
(emerald boundary 3.09, ring track 3.24, ring-% Dark 4.78, check glyph Light
4.84); the ring arc remains under the approved decorative exemption.

**Device verification: PASSED** (emulator 412 dp, Light and Dark) for the
empty/partial/complete/scrolled Home states and for Habits/Tasks/Insights/
Settings. The fixed chrome region was pixel-identical before/after scroll
(diff 0); the frozen background was unchanged (complete Aurora, `ContentScale.Fit`,
intrinsic ratio, no crop, fixed). **No blocking findings.**

**Cosmetic findings resolved (two):**
1. `HomeAtmosphereAssetsTest` stale test name →
   `every band on the progress card is textPrimary and both Home status labels opt in`
   (name only; assertion unchanged).
2. `HomeSectionHeader` KDoc stale "emerald `bolt` status indicator" leading
   sentence rewritten to describe the current ink-label + `accent`-tinted
   `verified` glyph behavior (KDoc only; no code change).

Unit tests re-run after the cleanup: **293/293 green**. Nothing is committed;
commit/push remain Human-in-the-Loop and HEAD is still `47ebfb4`. Do not start
TASK-019.

---

## 13. OPEN ITEMS HANDED TO THE HUMAN — ALL RESOLVED (closed 2026-10-02)

<!-- ORCHESTRATOR OWNER — 2026-10-01; closed 2026-10-02. The entries below are
retained as the historical handoff record; every item in them is now resolved.
See §13.3 for the closeout. -->

### 13.0 ISSUE-020 REMEDIATION — HUMAN RULING RECEIVED, PLAN PENDING APPROVAL (2026-10-02)

**Superseded by §4.10.** The human has ruled: no sub-floor values accepted, no
background/scrim-first change, targeted local foreground corrections, thresholds
unchanged (4.5:1 text / 3.0:1 non-text), minimum deviation from Stitch. The
**Architect's per-region remediation plan (§4.10 A–F) is the next deliverable**
and it returns to the human for approval before any Developer code change.
ISSUE-020 stays **OPEN**; the `DECISIONS.md` AN7 §2 supersession note stays held
until remediation is measured and passes. Governing measurement record: **§10.11**.

**ADR record is complete.** `ADR-49 Amendment Note 3` is appended and supersedes AN2's
method, figures and ruling by reference. `DECISIONS.md` is **+2324 / −0** against HEAD
— append-only, zero deletions, AN1/AN2 and all prior ADR text intact. AN3 also
corrects two threshold figures I published in §10.7 (both omitted the `+0.05` on the
ink's own luminance): `textSecondary` needs composited L ≥ **0.75154**, not 0.5266, and
`textPrimary` needs **0.24043**, not 0.01544. The ratio table itself is unaffected — I
recomputed all eight values from the luminances and they stand.

### 13.1 A new finding: three sibling bands on the progress card fail the same way

**HUMAN RULING (2026-10-01): option (a) is ADOPTED.** All three remaining failing
text bands inside the chromatic progress card use **`c.textPrimary`**:
`HomeCards.kt:327` (`items done`), `:361` (ring `done` unit), `:404` (`n Pending`).
`Scheduled Today` is **unchanged** — it is on the bare page and already passes.

Rationale of record: `c.textSecondary` fails throughout the card's chromatic wash;
leaving the bands below the **4.5:1** floor violates the established accessibility
requirement; a new colour token is not justified; altering the whole `liquidGlass`
wash to preserve Stitch's slate hierarchy would be a larger visual change; and
`c.textPrimary` is the smallest deterministic compliant implementation. **This is an
explicit accessibility-driven deviation from the reference's `#475569` secondary
hierarchy and is documented as such.**

Raised by the Architect while writing AN3, and **confirmed against the source**. My
§10.7 correction re-inked only the two bands the sweep had flagged. But
`c.textSecondary` cannot survive the card's chromatic wash at **any** point of the
sweep (3.91:1 at its lightest stop, 3.03:1 at its darkest), so every `c.textSecondary`
band sitting on that card is affected — not just the two I fixed:

| band | file | on the card? | status |
|---|---|---|---|
| `items done` suffix | `HomeCards.kt:327` | yes | **fails** → ruled to `c.textPrimary` |
| ring `done` unit | `HomeCards.kt:361` | yes | **fails** → ruled to `c.textPrimary` |
| `n Pending` | `HomeCards.kt:404` | yes | **fails** → ruled to `c.textPrimary` |
| `n Completed` | `HomeCards.kt:389` | yes | already `c.textPrimary` ✓ |
| fraction · ring % · divider · encouragement · `Finished` | — | yes | already `c.textPrimary` ✓ |
| `Scheduled Today` | — | **no** — bare page | `c.textSecondary` ✓ (**unchanged** by ruling) |

The card's **entire** body-text set is therefore one ink, `c.textPrimary`. That is the
approved, accessibility-driven consequence and is not to be re-opened.

**Out of scope of the ruling, and deliberately unchanged:** the completed activity-row
title (`HomeCards.kt:632`) and the row metadata line (`:644`) still use
`c.textSecondary`, because they sit on `liquidGlass(CARD/CONTROL, hue = null)` — a
**non-chromatic** row card, not the chromatic progress card. The Developer's
source-level sweep found exactly four surviving `c.textSecondary` on Home: those two,
`Scheduled Today` (the ruled-unchanged `labelColor` default), and `TodayError`'s
message. **Orchestrator note, recorded not acted on:** the two row-card bands are the
same *class* of risk — composited `c.textSecondary` over a translucent fill — and my
arithmetic puts them at **4.31:1** (CARD @ 0.55 over a `#B0B0D8` backdrop) and
**4.04:1** (CONTROL @ 0.45), i.e. **potentially below the floor** over a dark artwork
region. They were never in the ruling's scope and were left alone by design. This is
folded into the on-device sweep in §13.2 as a band to measure, not as a defect the
Developer was authorized to fix. It is **not** a claim that they fail.

### 13.2 The device checklist (unchanged, still outstanding)

Visual fidelity to `screen.png` is **unverified** — no instrumented test ran, no
screenshot exists, no pixel comparison was made. `adb` is unavailable.

1–17. Home initial · empty · partial · complete · scrolling · fixed chrome · fixed
Aurora/artwork · progress card · Pending · Completed · FAB · **exact Stitch bottom
navigation** · Light · Dark · font scale 1.0 and 2.0 · reduced motion · accessibility
(TalkBack order greeting → Pending → Completed; every target ≥ 48 dp).

**18. Contrast on the final artwork geometry — the real gap.** These resolve against
the artwork composite and **cannot** be computed here (the illustration is a lossy VP8
WebP with no decoder; they do not transfer because Device Finding #1 changed the
geometry again — the artwork is now the complete image at intrinsic ratio, so the
composite under every band differs from both TASK-017 and the withdrawn
cover-crop band):

- **date capsule**, **greeting**, **greeting support line** — three bands, **OPEN**.
- **all bands on the progress card**, including the three in §13.1.
- the nav's active capsule at both pulse extremes.

Per ADR-47 AN1 the **minimum** region ratio across the full sweep governs, per theme,
and the floor is **≥ 4.5:1, not weakened**. Only the scrim **peak** may move, inside
`[0.15, 0.35]`.

**One recorded deviation to check visually:** the nav bar's ends sit 16 dp while Home's
cards sit at 20 dp, so the bar is 4 dp proud of the content on each side.

### 13.3 CLOSEOUT — every item above is now resolved (2026-10-02)

- **§13.0 / §13.1 (ISSUE-020 remediation).** The Architect's §8.20 Home-scoped
  plan was approved by the human (`§4.11`) and implemented; the bounded
  region-sample re-measurement **passes in both themes**, so the §13.0 open
  condition ("ISSUE-020 stays OPEN") and the §13.1 in-card band risk are both
  closed. `ISSUE-020` is now documented **CLOSED by TASK-018** in `ISSUES.md`.
- **§13.2 (device checklist).** Performed on-device (emulator 412 dp, Light and
  Dark) for the Home empty/partial/complete/scrolled states and for
  Habits/Tasks/Insights/Settings: fixed chrome and fixed Aurora confirmed
  (chrome region pixel-identical before/after scroll), progress card, Pending/
  Completed, FAB, Stitch bottom navigation, and the completed/idle completion
  treatments all verified. `adb` is available; the "unavailable" premise of
  §13.2 is superseded. Recorded limitation, **not** a defect: 320/360 dp and
  font-scale 2.0 were not device-tested, and the instrumented suites were not
  executed. The nav bar's 4 dp edge delta against the 20 dp content axis was
  inspected and reads as intended.
- **Documentation debt — DISCHARGED.** The deferred **ADR-47 AN7 §2** supersession
  note (the withdrawn `460/640` band ratio) has been **discharged by ADR-47
  Amendment Note 8** (appended, append-only, 2026-10-02): it records the complete
  artwork's intrinsic `1536/1024` width-proportional geometry as the governing
  decision, supersedes AN7's band ratio/values/BOUND-1 arithmetic by addition,
  and formally supersedes ADR-47 AN6's contrast baseline now that ISSUE-020 is
  CLOSED by TASK-018. **ADR-47 documentation debt is now CLOSED** (no remaining
  open documentation debt for TASK-018). `ADR-49 Amendment Notes 5 and 6`
  already record the Home-scoped plan and its approval/as-built closeout.
- **TASK-018 final state:** complete; `ISSUE-020` CLOSED; no open issues or
  blocking defects attributable to TASK-018. Nothing committed (HEAD `47ebfb4`).