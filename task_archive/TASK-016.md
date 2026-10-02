# CURRENT TASK

> State of the currently active task only. TASK-015's full approved specification
> (concept, plan, ADRs, validation) was archived to `task_archive/TASK-015.md`
> when TASK-015 closed; its approved decisions remain canonical in
> `docs/architecture/decisions/DECISIONS.md` (ADR-47 / ADR-48 + Amendment Note 1).

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: **TASK-016**
- Title: Home visual corrections after on-device verification (TASK-015 follow-up)
- Status: **IMPLEMENTED + REVIEWED PASS (2026-09-30) — awaiting human on-device
  verification.** Implementation authorized and completed against §8 as approved;
  Reviewer FAIL on round 1 (1 blocker + 2 minor) closed by one correction round and
  re-verified PASS. Runnable validation green. **Not committed** (Human-in-the-Loop).
  F-HOME remains `PARTIALLY_VERIFIED` — the 15 mandated on-device checks and every
  instrumented suite have not been executed (`adb` unavailable). The 96 → 104 dp
  crown escalation is gated solely on the human's device check.
- Priority: High
- Created: 2026-09-30
- Last Updated: 2026-09-30
- Classification:
  - **DESIGN / ARCHITECTURE — required.** The human brief reverses two approved
    TASK-015 decisions (FAB hidden on an empty day; the empty-state CTA) and
    reports that the approved Hero presentation does not render its artwork on
    device. Both need an explicit decision + append-only ADR amendment
    (AGENTS.md §4, §21).
  - **IMPLEMENTATION** — not authorized until the human approval gate clears.
  - **VERIFICATION** — Reviewer verifies after implementation; on-device
    verification is performed by the human (no `adb`/device in this environment —
    confirmed 2026-09-30).
- Routing: **Orchestrator → Architect → human approval gate → Developer →
  Reviewer.**
- Source of truth (priority order):
  1. This task brief (human requirement, 2026-09-30, with device screenshots).
  2. TASK-015 approved plan + validation — `task_archive/TASK-015.md`; ADR-47 /
     ADR-48 (+ Amendment Note 1) in `docs/architecture/decisions/DECISIONS.md`.
  3. `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` (Locked) — Soft Aurora language.
  4. Current Home implementation under
     `app/src/main/java/com/momentum/app/presentation/screens/today/**`.
  5. `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` (Home/Today findings).

---

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Correct the **root causes** of the Home layout/composition defects observed on a
real device after TASK-015, in both the empty and non-empty states, without
regressing any approved TASK-015 behavior. The deliverable is an
implementation-ready visual/layout plan — not a set of offset tweaks.

---

## 3. SCOPE

<!-- ORCHESTRATOR ONLY -->

**In scope (Architect):** diagnose and specify the root cause of each observed
defect, then specify the corrected composition — Home Hero artwork presentation
(both states), the empty-state composition, the non-empty Hero hierarchy and
progress/orb composition, the FAB's presence and visual treatment, and the
resulting light/dark + accessibility + motion requirements. ADRs (append-only)
for the decisions that change TASK-015 outcomes.

**Out of scope:** replacing or generating Home artwork; redesigning Habits,
Tasks, Insights, Settings, Onboarding; domain / Room / DataStore / navigation /
dependency / build changes; broader visual redesign of the app; commit or push
(Human-in-the-Loop).

---

## 4. REQUIREMENTS

<!-- ORCHESTRATOR ONLY -->

### 4.1 Empty state (zero activities)

- **E1 — Hero artwork is invisible on device in both Light and Dark.** It must be
  visibly rendered, occupy an intentional hero region appropriate to the
  artwork's proportions, and merge into the surrounding Home background. **The
  existing artwork is retained — do not replace it and do not author new
  artwork.** Diagnose *why* it currently disappears (root cause, not a
  workaround) before specifying the fix.
- **E2 — The "Nothing scheduled today" card sits too high**, directly under the
  greeting, making the screen top-heavy. It must be visually centered in the
  usable Home content area, remain the focal point, and stay correctly related
  to the Hero above and the bottom navigation below. **Arbitrary `Spacer`
  values are not an acceptable solution** — specify the layout geometry.
- **E3 — The FAB is missing on an empty day.** It must be present with zero
  activities and remain the primary creation affordance. *(Reverses TASK-015
  R9 / ADR-48.)*
- **E4 — Remove the "Add an activity" CTA from the empty-state card
  completely.** The card communicates state; the FAB is the single creation
  affordance. No two competing CTAs. *(Reverses the TASK-015 empty-state CTA
  and its retention in `EmptyState`.)*

### 4.2 Non-empty state

- **N1 — Hero / progress composition.** On device the artwork is only partially
  visible at the top, and the orb reads as separated from the progress
  information with an awkward gap. The existing artwork must be intentionally
  visible and integrated into the Hero; the orb and progress information must
  read as **one coherent composition**; remove the excessive gap while keeping
  intentional spacing.
- **N2 — Greeting → progress gap.** A very large empty vertical region separates
  the greeting from the progress count + orb, disconnecting the composition.
  Specify the correct visual hierarchy and layout geometry. **Do not pad or
  un-pad until the screenshot looks better.**
- **N3 — FAB visual treatment.** The FAB appears only once activities exist and
  reads as disconnected; its color/treatment is not integrated. It must be
  intentional across both states, belong to the existing Momentum / Aurora
  visual language, work in Light **and** Dark, and preserve existing
  functionality. **Prefer existing design tokens and components over introducing
  a new visual language.** *(Consistent with E3: one FAB treatment, both
  states.)*

### 4.3 Direction under evaluation — not a requirement

The human raised a possible scroll-driven alternative (initially orb-above /
count, then on scroll the orb moves left and the progress information aligns
right). This is explicitly **not** assumed. Prefer the simpler static
composition **if it can be made excellent**. If the Architect concludes the
scroll transformation is genuinely justified, it must be **proposed explicitly
as a decision in the plan** (with rationale, cost, and motion spec) for human
approval **before** implementation — never introduced silently during
implementation.

### 4.4 Preserved decisions and constraints

- TASK-015's approved architecture and behavior stand unless this brief
  explicitly changes them: Home remains a focused "Today" experience; the
  existing Home artwork is intentional and retained; Aurora is an atmospheric /
  meaningful system, not decoration; the progress orb remains part of Home;
  progress information stays accessible and semantically correct; the 100%
  completion / milestone behavior (transient `AuroraState.MILESTONE`,
  `MomentumMotion.reward`, one `HapticFeedbackType.Confirm`, no snackbar) must
  not regress; reduced motion, TalkBack, ≥ 48 dp targets, both themes, and
  offline-only guarantees are mandatory.
- **No domain, schema, database, or navigation change** unless the Architect
  demonstrates one is genuinely required, with justification.
- Soft Aurora / the locked design system governs. Reuse existing tokens and
  components. Any new numeric value must state its derivation basis (ISSUE-025
  discipline) — never present a derived value as specified.
- Re-verify ISSUE-020 (Hero copy contrast, region-sample method) for any change
  to the scrim, artwork window, or Hero height.
- Scope discipline: presentation-layer corrections on Home only. Do not
  opportunistically fix unrelated findings; report them instead.

### 4.5 TASK-016 human ruling (2026-09-30 — authoritative)

<!-- ORCHESTRATOR ONLY -->

**Decision 1 — Hero artwork / height model: APPROVED.** The artwork-crown model
stands exactly as specified in §8.2: crown **96 dp**, empty Hero ≈ **208 dp**,
collapsed ≈ **216 dp**, expanded ≈ **312 dp**. The crown must be derived from
existing design tokens, not introduced as a magic number. The governing concept:
**artwork space is reclaimed first during collapse — never greeting / progress /
content space.**

**Critical Hero visual clarification (binding on the §8.2 implementation).**
The Hero artwork is **not** a separate illustration band sitting above the
content. It is the Hero's **background / atmospheric layer**, and the copy
**foregrounds over it**:

```text
BACKGROUND      existing Home artwork (retained; never replaced or re-authored)
ATMOSPHERE      existing Aurora / scrim / blend treatment
FOREGROUND      greeting · progress figure · progress orb · encouragement
```

Binding requirements:
1. Foreground copy **may** sit over the artwork and **must visually dominate** it.
2. Greeting, progress figure/count and progress orb each remain clearly readable.
3. All Hero copy satisfies the existing contrast/readability requirement (§8.6,
   ISSUE-020 region-sample method).
4. The artwork remains **recognizable and visually present** — the fix must not
   re-create the E1 failure by fading it out.
5. Readability must **not** be solved by covering the artwork with an opaque
   layer.
6. The artwork must **blend/dissolve continuously** into the surrounding Home
   background. **No hard boundary** between the artwork region and the solid page
   background. The bottom merge is load-bearing; verify it on device.
7. The result must read as `CONTENT = primary hierarchy / ARTWORK = supporting
   atmosphere / AURORA+BLEND = the transition between them`.
8. Existing Light and Dark treatments stay intentional; no new global visual
   language.

**Decision 2 — Empty state + FAB: APPROVED.** The FAB is present in **all** Home
states (empty, partially populated, fully populated). The empty-state card no
longer contains an "Add an activity" action; the FAB is the single creation
affordance. The card communicates state only — "Nothing scheduled today" plus its
supporting message. Centering uses the §8.4 **measured** geometry approach; **no**
arbitrary `Spacer` values and **no** hard-coded screen percentages.

**Decision 3 — Scroll transformation: APPROVED AS REJECTED / DEFERRED for
TASK-016.** Do not implement the orb-left / count-right scroll transformation. Do
not introduce scroll-position-driven orb transformation, additional scroll state,
interpolation logic, or additional choreography. §8.12 stands. May be reconsidered
in a future task based on actual device evaluation.

**Decision 4 — FAB visual treatment: AMENDED (supersedes §8.5's "material NOT
changed" and RISK-1).** The FAB is **not** redesigned and no new visual language
is introduced, but the observed visual disconnection **must not be ignored**. A
**narrowly scoped visual-integration adjustment using existing tokens and
established visual vocabulary is authorized**, as follows.
*Allowed:* existing Momentum colour tokens; existing Aurora treatment; existing
elevation/surface treatment; existing shape/motion vocabulary; Light/Dark-specific
token adjustments **already supported by the design system**.
*Not allowed:* a new FAB visual system; an unrelated global FAB redesign; new
component architecture; arbitrary hard-coded colours; unrelated changes to other
screens.
The **primary** fix remains FAB placement/availability (§8.5). Any material
adjustment must stay small and directly serve integrating the FAB with Home.
*Note:* the Architect declined this as RISK-1; the human has now authorized it
within the stated limits, so RISK-1 is **closed by ruling** and the adjustment must
be specified.

**Crown escalation: 96 dp → 104 dp is deferred, not pre-emptive.** Implement
**96 dp**. Do **not** proactively raise it. On-device verification decides. If the
artwork is still inadequately visible on device, the only sanctioned escalation is
`HeroRingSize + MomentumSpacing.section` = **104 dp**, re-measured per §8.6. No
other crown size may be introduced without returning through the decision process.

**Architecture / scope (confirmed).** TASK-015's architecture and all unrelated
behavior are preserved. Presentation-layer focused. No changes to domain, database
or schema, repositories, navigation, dependencies, build configuration, or
unrelated screens. Home is not redesigned beyond the explicitly approved
corrections. **Do not commit** — commit remains Human-controlled.

**§8.15.3 shared-call-site consequence: APPROVED (human, 2026-09-30).** The
`HabitsScreen` FAB hairline change is accepted as a necessary consequence of the
shared-component correction. RISK-1 is closed.

**Binding directive on `MomentumFab.kt` (human, 2026-09-30).** The shared
component change is **strictly** the theme-resolved hairline
(`MomentumGlass.borderDark` / `MomentumGlass.borderLight`) plus the existing
`MomentumGlass.heroElevation` correction, exactly as specified in §8.15. The
Developer must **not** "improve", refactor, extend or restyle anything else in
`MomentumFab` while in that file — no fill/gradient/stop change, no shape, size,
glyph, motion, semantics, API or component-architecture change, no new token. Any
further change to that file requires returning through the decision process.

**Verification (human-mandated, 15 items).** Empty+Light · Empty+Dark · one
activity · multiple activities · partial completion · complete state · Light+Dark
· FAB availability · FAB visual integration · Hero artwork visibility · Hero
artwork blending/dissolve · foreground readability over the artwork ·
progress/orb composition · accessibility · existing TASK-015 milestone behavior.
The artwork **must be visually inspected on-device**. No instrumented or device
test may be claimed as passing unless it was actually executed (`adb` is
unavailable in this environment).

---

## 5. KNOWLEDGE / REFERENCE INDEX

- `task_archive/TASK-015.md` — TASK-015 approved concept, plan, acceptance
  criteria, measured contrast values, and validation record.
- `docs/architecture/decisions/DECISIONS.md` — ADR-47 (Home concept + Hero
  simplification), ADR-48 (one progress representation + completion moment),
  ADR-48 Amendment Note 1 (encouragement retained), ADR-30/31/33/35/37/39
  (artwork/Hero), ADR-22 (bottom nav), ADR-28/29/32.
- `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` — Soft Aurora (Locked): §4 type
  scale, §12–14 header/progress/completion, §19–21/§28/§33 motion, §7/§12
  spacing and depth.
- `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` — Home/Today findings (§§7.8.13, 7.14,
  7.15, 7.22, 8.1–8.3, 17.3f) and opportunities HO-1…HO-4.
- `ISSUES.md` — ISSUE-002 (bottom nav), ISSUE-005 (streaks), ISSUE-006
  (artwork strategy), ISSUE-020 (Home contrast — CLOSED by TASK-015, method
  still canonical), ISSUE-022 (reduced motion).
- `docs/product/features/dashboard/DASHBOARD_SPECS.md` — historical where it
  conflicts with the locked design system or current ADRs.

---

## 6. RELEVANT FILES (grounding — open the files)

- `app/src/main/java/com/momentum/app/presentation/screens/today/HomeHero.kt` —
  the Hero: content-driven height, `MomentumHeroArtwork`
  (`Alignment.BottomCenter`, shared 88 dp `bottomDissolve`), the 4-stop
  `c.background` scrim (`ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f`),
  `HeroTopInset`/`HeroBottomInset`/`HeaderProgressGap`/`CollapseHeadroom`,
  the R1 progress row (weighted figure column + trailing 80 dp
  `ProgressOrb(showPercent = false)`), merged progress semantics, milestone
  glow.
- `app/src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt`
  — empty branch (`HomeEmptyState`), FAB `AnimatedVisibility` gated on
  `!state.isEmpty`, content padding, `HomeAurora` intensity, milestone effect.
- `app/src/main/java/com/momentum/app/presentation/components/MomentumHeroArtwork.kt`
  — artwork window, `ContentScale`, `bottomDissolve` contract (read-only unless
  the Architect proves a change is required and it is approved).
- `app/src/main/java/com/momentum/app/presentation/components/MomentumFab.kt` —
  the shared FAB (56 dp, solid `primary`, white hairline, soft shadow).
- `app/src/main/java/com/momentum/app/presentation/components/MomentumBadges.kt`
  — `EmptyState` (title/body/icon/actionLabel/onAction), used by Home's
  `HomeEmptyState` and by `ActivitiesScreen`, `HabitsScreen`, `InsightsScreen`.
- `app/src/main/java/com/momentum/app/presentation/components/ProgressOrb.kt`,
  `ActivityCard.kt`, `LiquidGlass.kt`.
- `app/src/main/java/com/momentum/app/presentation/screens/habits/HabitsHero.kt`
  — the reference composition for artwork + copy integration.
- `app/src/main/java/com/momentum/app/presentation/theme/**` — `Tokens.kt`
  (`MomentumSpacing`), `Type.kt` (`MomentumFigureDisplay`), `Motion.kt`,
  `Shapes.kt`, `Color.kt`.
- Tests currently pinning Home contracts: `HomeHeroAssetsTest` (incl.
  `HomeScrollSurfaceTest`), `HomeSectionMotionTest`, `HomeAccessibilityTest`,
  `QuickAddTest`, `HomeHeroStateTest`, `HomeStatesTest`,
  `androidTest/.../HomeHeroCompositionTest.kt`.

---

## 7. ENVIRONMENT FACTS

- `adb` is **not available** in this environment (confirmed 2026-09-30). No
  emulator/device run is possible. Runnable validation = `:app:testDebugUnitTest`
  + `:app:assembleDebug` (+ `:app:assembleDebugAndroidTest` compiles the
  instrumented suite). **Do not claim instrumented or on-device verification
  unless it was actually executed**; the human performs on-device verification.
- Working tree: TASK-015's implementation is present and **uncommitted**. All of
  it must be preserved. `.opencode/**` and `opencode.json` also carry
  pre-existing unrelated edits — preserve them.

---

## 8. ARCHITECTURAL DECISIONS AND PLAN

<!-- ARCHITECT OWNER -->

> **STATUS: complete and awaiting the human approval gate. Nothing in this
> section is implemented.** All of it reverses approved TASK-015 decisions
> (ADR-47 §3, ADR-48's FAB/CTA outcome, and the "content-driven Hero height"
> clause), so implementation is **not** authorized until the human rules on it.
> Approval points are listed in §8.12.

### 8.1 Root-cause analysis (reasoned from the current layout math)

All numbers below assume the current source, a 360 dp-wide window (the
`HabitsHero` reference width), a 24 dp status-bar inset, and the bundled
1024×1536 Aurora pair (`app/src/main/res/drawable/habits_hero_{light,dark}.webp`).
The shared artwork is cover-cropped by **width** (`MomentumHeroArtwork.kt` →
`ContentScale.Crop`, `Alignment.BottomCenter`), so at 360 dp the scaled
illustration is **360 × 540 dp** and the visible slice is a pure function of the
container's `height / width` ratio.

| # | Finding | Root cause | Producing TASK-015 clause |
|---|---------|-----------|--------------------------|
| **E1** | Artwork invisible on an empty day | The Hero's height is derived **from copy only**. With `HeroFigureState.Hidden` the height is `topInset + header + HeroBottomInset` = 40 + 56 + 16 ≈ **112 dp**. At 360 dp that shows only the bottom 112/540 = **21 %** of the illustration (rows 0.79 → 1.0 — the illustration's quiet lower third, which the shared component's own KDoc says is deliberately featureless). The shared `bottomDissolve = 88.dp` then computes `bottomStart = 1 − 88/112 = 0.215`, so **78 % of the visible window is dissolved away**; only ~25 % of the window (≈ 28 dp) carries any meaningful artwork alpha. What survives is a flat tint indistinguishable from the page background. | "Content-aware height" (`HomeHero.kt` §"Content-aware height") + "no headroom and no weighted spacer" on an empty day, combined with reusing the shared **absolute** 88 dp bottom dissolve in a container only 112 dp tall. |
| **E2** | Empty card too high | The empty state is simply the next `LazyColumn` `item` after the Hero with `Arrangement.spacedBy(MomentumSpacing.item)`. Nothing distributes the leftover height, so with a ~112 dp Hero the card lands ~136 dp from the window top on a ~700 dp content area. It is *not* a padding value that is too small; **there is no centering geometry at all**. | ADR-47 §3: "The no-activity state sits immediately beneath it." |
| **E3** | FAB missing on an empty day | `AnimatedVisibility(visible = !state.isEmpty)` — a deliberate gate. | TASK-015 R9 / ADR-47 §3 ("the Quick Add FAB is suppressed on the empty day"). |
| **E4** | Two competing CTAs | `HomeEmptyState(onAdd = onAdd)` renders `EmptyState(actionLabel = R.string.today_empty_action, onAction = onAdd)`. | TASK-015 R9 / ADR-47 §3 ("one add CTA"). |
| **N1a** | Artwork only partly visible at the top | Same root cause as E1, at a larger size. Populated expanded Hero = 40 + 56 + 20 + 80 + 16 + 72 = **284 dp** → visible slice = illustration rows 0.47 → 1.0. The illustration's structure sits **above** that slice, so the artwork reads as a shape cut off by the Hero's top edge rather than as an image. The 88 dp dissolve then removes the bottom third of what is left. | Same clause as E1 — the height is copy-derived, so it only works out by accident on a populated day and is ~2.5× too small on an empty day. |
| **N1b** | Orb separated from the progress figure | In `HeroProgressBlock`, `Column(modifier = Modifier.weight(1f))` stretches the figure column to the full remaining row width (320 − 80 = **240 dp** at 360 dp) while its text is start-aligned, and the orb is pushed to the trailing edge. The gap between them is therefore 100–200 dp and varies with the figure's digit count. The "one left axis (R1)" comment describes an intent the `weight` defeats. | R1's "weighted figure column + trailing 80 dp `ProgressOrb`". |
| **N2** | Very large empty region between greeting and progress | `CollapseHeadroom = 72.dp` is added to the Hero's height **between** the header and the progress block, and the body is `Spacer(Modifier.weight(1f))` — so the greeting→progress distance is `20 dp + 0…72 dp = 20…92 dp`, changing with scroll. Because of N1a that region contains no visible artwork, so it reads as a void rather than as the intended "open Aurora band". | R11's collapse model, which places the headroom **between** the copy blocks. |
| **N3** | FAB reads as disconnected | Two contributors: (a) the FAB's **presence** is a state change (`AnimatedVisibility` on `!isEmpty`), so it reads as an event rather than a persistent control; (b) it floats 12 dp (`MomentumSpacing.compact`) above the content area's bottom edge, i.e. one *card-padding* gap below a glass bottom bar, and the list reserves only `card + FabSize + compact` = 84 dp. The **material** (`MomentumFab`) is already the approved shared Solid-Aurora disc. | ADR-47 §6 (material, unchanged) + R10 ("its presence is a state change"). |

**Convergent diagnosis.** E1, N1a and N2 are one defect: *the Hero's height is
a function of its copy, while the artwork is a function of the container's
aspect ratio.* When the copy is sparse the container collapses to ~112 dp and
the artwork system — a bottom-anchored cover-crop with an absolute 88 dp
dissolve — has nothing left to show. The collapse headroom then adds up to
92 dp of extra height **between the two copy blocks**, where, because the
artwork is absent, it is pure void. Fixing the height model fixes all three
at once.

### 8.2 Corrected Hero geometry (Decision 1 — ADR-47 Amendment Note 2)

The Hero's foreground becomes **one top-anchored module**; the Hero's
flexible height becomes an **artwork crown** above that module.

```text
Hero box (clipToBounds, height = lerp(expanded, compact, p))
├── MomentumHeroArtwork(BottomCenter, fillMaxSize)        [unchanged call]
├── HeroScrim(fillMaxSize)                                [unchanged call]
└── Column(fillMaxSize, horizontal padding = MomentumSpacing.screen)
    ├── Spacer(statusBarInset + HeroTopInset + crown)     ← the artwork crown
    ├── HeroHeader(date, greeting, Settings)
    ├── Spacer(HeaderProgressGap = MomentumSpacing.section)   [only when hasProgress]
    ├── HeroProgressBlock(figure/label + orb + encouragement) [only when hasProgress]
    └── Spacer(HeroBottomInset)
```

**Height model (exact):**

```kotlin
compactHeight  = topInset + headerDp +
    (if (hasProgress) HeaderProgressGap + contentDp else 0.dp) + HeroBottomInset
expandedHeight = if (hasProgress) compactHeight + ArtworkCrown else compactHeight
crown          = if (hasProgress) lerp(ArtworkCrown, 0.dp, p) else ArtworkCrown
currentHeight  = lerp(expandedHeight, compactHeight, p)
```

`crown` is computed from `p`, not fixed, because the box is `clipToBounds()`;
the crown and the box shrink 1:1 so the copy never moves relative to the
Hero's **bottom** edge and the existing `heroEndsShortlyAfterTheLastCopy`
contract still holds exactly (`artwork.bottom − block.bottom == HeroBottomInset`).

**Constants (every value carries its derivation; nothing is invented):**

| Constant | Value | Derivation basis |
|---|---|---|
| `HeroTopInset` | `16.dp` | unchanged (TASK-015) |
| `HeroBottomInset` | `16.dp` | unchanged (TASK-015) |
| **`ArtworkCrown`** (replaces `CollapseHeadroom`) | **`HeroRingSize + MomentumSpacing.standard` = `96.dp`** | The Hero must show at least one progress-orb diameter of clear, copy-free artwork — `HeroRingSize` (80 dp) is the largest opaque element the Hero already owns — plus one standard gap. Below 96 dp the crop window falls to the illustration's quiet lower third (the E1 failure mode). |
| **`HeaderProgressGap`** | **`MomentumSpacing.section` = `24.dp`** (was `20.dp`) | The date/greeting block and the progress block are two regions of one module, not two screens, so the design system's "gaps between major sections" token applies. Now **constant** — it is no longer inflated by scroll, which is the whole of N2. |
| **`HeroOrbGap`** (new) | **`MomentumSpacing.standard` = `16.dp`** | The figure column and the orb are sibling elements of one row, not controls inside one compound control, so the "standard gaps" token applies — one step above `MomentumSpacing.tight` (8 dp, inside compound controls) and one below `MomentumSpacing.section`. |
| `HeroRingSize` | `80.dp` | unchanged (TASK-015 / R2) |
| `ScrimPeakStart/End`, `ScrimPeakLight/Dark` | `0.20f / 0.80f`, `0.26f / 0.22f` | **Values retained, mandatory re-measurement** — see §8.6. The copy has moved down the frame, so the TASK-015 measurements do **not** transfer. |

**Resulting geometry (360 dp window, 24 dp status bar):**

| State | Hero height | Visible illustration slice | Fraction of window at full artwork strength |
|---|---|---|---|
| Empty day | 40 + 96 + 56 + 16 = **208 dp** | rows 0.61 → 1.0 (39 %, was 21 %) | 58 % (was 23 %) |
| Populated, expanded | 40 + 96 + 56 + 24 + 80 + 16 = **312 dp** | rows 0.42 → 1.0 (58 %, was 53 % at 284 dp) | 72 % |
| Populated, collapsed | 216 dp (travel = 96 dp, was 72 dp) | rows 0.60 → 1.0 | 59 % |

The crown additionally guarantees **96 dp of unobstructed, fully-opaque
artwork directly beneath the status bar** in every state — the place where the
illustration is strongest and where no copy has to bridge it. That is the
single change that makes the artwork read as an image rather than a tint
(E1/N1a) **and** it removes the artwork-free band between greeting and
progress (N2), because that band no longer exists: the greeting→progress
distance is now a fixed 24 dp.

> **Bounded unknown (declared, not assumed).** The vertical position of the
> illustration's luminous band inside the 1024×1536 frame **cannot be measured
> in this environment** — there is no WebP decoder available (`adb`, Python,
> ImageMagick, ffmpeg, `cwebp`/`dwebp` are all absent). The fix above does not
> depend on that answer: it makes the window large enough and guarantees a
> full-strength band above the copy. If the human's device check shows the
> artwork still does not read at a 208 dp empty-day Hero, the **only** sanctioned
> escalation is `ArtworkCrown = HeroRingSize + MomentumSpacing.section` =
> 104 dp (same derivation, one token step up), re-measured per §8.6. No new
> artwork, no re-crop anchor change, no component change.

### 8.3 Progress + orb composition (N1b)

Root cause is `Modifier.weight(1f)`, not spacing. The fix is to **remove the
weight** and give the pair one bounded, token-derived gap:

```kotlin
Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Column {                                  // ← weight(1f) removed: content width
        // fraction + caption, or the complete statement
    }
    Spacer(Modifier.width(HeroOrbGap))        // 16.dp
    ProgressOrb(progress, HeroRingSize, showPercent = false,
                glowEnabled = milestoneGlow && !reduceMotion, animate = !reduceMotion)
}
```

The figure column and the orb become a single start-aligned unit on the Hero's
20 dp screen axis — one left axis, shared with the date and the greeting. The
figure text and the orb are then adjacent at a fixed 16 dp, so the composition
no longer depends on the digit count, the device width, or the trailing edge.
The trailing region of the progress row becomes artwork again, which is the
correct Soft Aurora behaviour (the row must not read as a filled bar). The
merged `today_hero_progress_desc` semantics and `showPercent = false` are
unchanged.

### 8.4 Empty state (E2 + E4)

**Final copy set after removing the CTA** — **unchanged**, no product decision
needed:

- `today_empty_title` = "Nothing scheduled today"
- `today_empty_body` = "When you add a habit or task it shows up here. One small step is enough."
  (it states the state; it is not an action label)
- `today_empty_action` = "Add an activity" → **removed** from `strings.xml`
  (it becomes unreferenced; it must not remain as dead copy).

**Exact `EmptyState` call shape** (`TodayScreen.kt`):

```kotlin
@Composable
private fun HomeEmptyState(modifier: Modifier = Modifier) {
    EmptyState(
        title = stringResource(R.string.today_empty_title),
        body = stringResource(R.string.today_empty_body),
        modifier = modifier.padding(horizontal = MomentumSpacing.screen),
        icon = null,
    )
}
```

`EmptyState` becomes CTA-optional (**justified cross-boundary change** — one
shared component, additive defaults, all three other call sites use named
arguments and are therefore source-compatible, no visual change anywhere else):

```kotlin
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
)   // the TextButton renders only when actionLabel != null && onAction != null
```

**Centering geometry (E2) — measured, not guessed.** The empty branch's slot
fills the region between the Hero's bottom edge and the top of the bottom
navigation (the Scaffold's `innerPadding` already ends above the navigation —
`MomentumNavGraph.kt` L239), and the card is centred in it:

```kotlin
BoxWithConstraints {                        // at the TodayScreen root
    val viewportDp = maxHeight
    // heroHeight is reported by HomeHero via the new onHeightChanged parameter
    val emptyRegion = viewportDp - heroHeightPx.toDp() - EmptyDayBottomReserve
    LazyColumn(contentPadding = …) {
        item { HomeHero(..., onHeightChanged = { heroHeightPx = it }) }
        if (state.isEmpty) item {
            Box(Modifier.heightIn(min = emptyRegion), contentAlignment = Alignment.Center) {
                HomeEmptyState()
            }
        }
        …
    }
}
```

- `EmptyDayBottomReserve` = the same `MomentumSpacing.card + MomentumFabSize +
  MomentumSpacing.section` expression the `LazyColumn` already uses for
  `contentPadding.bottom` — one named constant, used in both places, so the
  card's centring region and the list's scroll reserve can never drift.
- Free space is divided **equally above and below the card**, so the card is
  optically centred. At 360×800 with the navigation bar: Hero 0–208, region
  208–704 (496 dp), card ≈ 140 dp → ≈ 178 dp clear above and below.
- `heightIn(min = …)`, **not** `height(…)`: at large font scales the card makes
  the item grow rather than clip, and the `LazyColumn` can still scroll.
- **No feedback loop.** `heroHeight` is a pure function of `figureState`,
  `encouragement`, the window insets and the container width; none of those
  depend on `emptyRegion`, and the write is idempotent.
- **No `Spacer` constants, no percentage paddings** (the brief forbids them, and
  they are not size-independent).
- Rejected alternatives, recorded: `Arrangement.Center`/spacer arithmetic on
  the `LazyColumn` (forbidden, and wrong at other window sizes);
  `Modifier.fillParentMaxHeight` on the item (measures against the container's
  max height, **not** the space left after the Hero, so it overflows the
  viewport); moving the empty branch out of the single `LazyColumn` (breaks the
  one-scroll-surface contract for no gain).

### 8.5 FAB (E3 + N3) — one treatment, both states (ADR-48 Amendment Note 2)

- **Always present.** `AnimatedVisibility(visible = !state.isEmpty)` is removed;
  `MomentumFab` is composed unconditionally. *Root-cause correction:* the FAB's
  presence was a **state** change, which is what made it read as an event that
  arrives rather than as a control that is always there. (ADR-48 Amendment
  Note 2.)
- **Placement:** `align(BottomEnd)`, `padding(end = MomentumSpacing.screen,
  bottom = MomentumSpacing.section)`. Derivation: the FAB is a floating element
  over the list and must not read as part of the glass bottom bar, so it sits
  one **section** gap (`MomentumSpacing.section`, "gaps between major sections")
  above the content area's bottom edge instead of one card-padding gap.
- **Reservation:** `contentPadding.bottom = MomentumSpacing.card +
  MomentumFabSize + MomentumSpacing.section` (16 + 56 + 24 = **96 dp**, was 84 dp).
  Derivation: one standard card padding + the FAB diameter + a full section gap,
  so the last row always clears the FAB by a section gap and is never
  half-covered.
- **Material: `MomentumFab` is NOT changed.** It is the approved shared Solid
  Aurora disc (ADR-47 §6), built from existing tokens. Replacing it with a
  glass or gradient treatment would be a **new visual language** and is
  explicitly out of scope for this pass. See §8.11 RISK-1 — if the human still
  judges the material disconnected after the placement fix, that is a **separate
  decision** requiring its own ADR, not something this task may absorb.
- **No functional change:** `onAdd` still opens Create Item
  (`showCreateItem = true`); `cd_quick_add` = "Quick Add" is unchanged.

### 8.6 Light / Dark and contrast (ISSUE-020 re-verification — mandatory)

- Every Hero token stays `textPrimary`; **no `textSecondary` over artwork**
  (ADR-47 §7). No ink change.
- `ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f`, `ScrimPeakStart = 0.20f`,
  `ScrimPeakEnd = 0.80f` are **retained as starting values** but **must be
  re-measured**, because the artwork window and the Hero height both changed
  and the copy now sits lower in the frame relative to the illustration.
- **Canonical method** (unchanged from TASK-015 §8 / the `ScrimPeak*` KDoc):
  for each copy element take its text rectangle, sample the composited artwork +
  scrim over it, reduce to one mean background colour, measure against
  `textPrimary`; the **minimum region ratio across the sweep governs**. A
  per-pixel minimum is informational only.
- **Sweep:** both themes × Hero heights {208 dp empty, 216 dp collapsed, 312 dp
  expanded} × widths {360, 412} dp × figure states {Fraction, Complete} ×
  encouragement {on, off}. Bands: date, greeting, fraction/label, complete
  statement, merged description, encouragement.
- **Acceptance:** every region sample ≥ **4.5:1**. If any sample fails, raise
  the peak **within the already-approved `[0.15, 0.35]` envelope** (Light toward
  `0.32f`, Dark toward `0.30f`) and re-measure. If it still fails, **escalate
  to the Orchestrator** — do not change the ink, do not change
  `MomentumHeroArtwork`, do not change the copy position.
- Measured values go into the `ScrimPeak*` KDoc and the Developer handoff (§9).
- Light/Dark otherwise: the artwork is theme-selected only (ADR-30); the scrim
  peak is per-theme; the FAB material is theme-token-driven and unchanged; the
  empty card is the shared `EmptyState` glass card and is unchanged.

### 8.7 Motion and reduced motion

- **New motion introduced by this pass: none.**
- The crown is `lerp`ed from the screen-owned `collapsePx` value — 1:1 with the
  drag, no `Animatable`, no spec, no clock. Identical behaviour under reduced
  motion, because there is no time-based component.
- The screen-space top mask (ADR-35) is retained unchanged and still covers
  `[statusBar, statusBar + HeroTopInset]`; with the crown above the copy the
  date now enters that band later in the collapse, which is the correct order.
- Removing the FAB's `AnimatedVisibility` **removes** motion (it previously used
  `EnterTransition.None`/`ExitTransition.None` under reduced motion). Strictly
  less motion; not a regression.
- Empty-state centring is pure layout — no motion.
- **100 % / milestone behaviour must not regress:** transient
  `AuroraState.MILESTONE` for `MomentumMotion.reward`, one-shot ring glow gated
  by `glowEnabled = milestoneGlow && !reduceMotion`, exactly one
  `HapticFeedbackType.Confirm`, no snackbar, encouragement line unchanged
  (ADR-48 §3 + Amendment Note 1). None of these touch the changed geometry.

### 8.8 Accessibility

- **Targets:** FAB 56 dp, Settings 48 dp, completion controls 48 dp — all
  unchanged.
- **TalkBack:** the empty-state CTA button is **removed from the tree**; the
  single remaining add affordance on an empty day is the FAB
  (`cd_quick_add` = "Quick Add"), so a screen-reader user hears exactly **one**
  add action (previously also one, now without the visual duplication).
- **Semantics:** the progress row keeps its single merged
  `today_hero_progress_desc` description (`clearAndSetSemantics`); `ProgressOrb`
  keeps none. Section headers keep `heading()`.
- **Reading order, empty day:** date → greeting → Settings → "Nothing scheduled
  today" card → Quick Add → bottom navigation.
- **Reading order, populated:** date → greeting → Settings → progress (one
  merged description) → section headings → rows.
- **Contrast:** §8.6.
- **Large font scale:** the compact Hero height remains a sum of **measured**
  blocks plus one *fixed* crown, so copy cannot clip; `heightIn(min = …)` makes
  the empty card grow instead of clip. **`screenHeightDp` must not appear in
  `HomeHero.kt`** (ISSUE-025 discipline + `HomeAccessibilityTest`).
- Offline-only is untouched: no dependency, permission, network or data change.

### 8.9 Implementation boundaries

**Files to modify (4):**

1. `app/src/main/java/com/momentum/app/presentation/screens/today/HomeHero.kt`
   — crown + height model + `HeaderProgressGap` → `MomentumSpacing.section` +
   `HeroOrbGap` + removal of the progress row's `weight(1f)` + new
   `onHeightChanged: (Int) -> Unit` parameter + updated KDoc + re-measured
   `ScrimPeak*` KDoc.
2. `app/src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt`
   — `BoxWithConstraints` + measured hero height + `EmptyDayBottomReserve` +
   centred empty item + `HomeEmptyState` without `onAdd` + always-visible FAB +
   FAB placement + updated `contentPadding.bottom` + unchanged skeleton/milestone
   code.
3. `app/src/main/java/com/momentum/app/presentation/components/MomentumBadges.kt`
   — `EmptyState`'s CTA becomes optional (defaults only). **Justification:**
   this is a shared component and duplicating the card locally inside `today/`
   would fork its material. Three existing call sites (`ActivitiesScreen`,
   `HabitsScreen`, `InsightsScreen`) pass named arguments and are unaffected.
4. `app/src/main/res/values/strings.xml` — delete `today_empty_action`.

**Explicitly NOT touched:** `MomentumHeroArtwork.kt` (ADR-30/39 contract stands —
the artwork call keeps `Alignment.BottomCenter`, `fillMaxSize()`, the shared
`topDissolve`/`bottomDissolve`, no zoom, no parallax),
`MomentumFab.kt`, `ProgressOrb.kt`, `HabitsHero.kt`, `theme/**`,
`TodayViewModel.kt`, domain/Room/DataStore, navigation, Gradle, dependencies.

**Required domain / state changes: NONE.** `TodayUiState.Success` already
carries `isEmpty`, `doneCount`, `totalCount`, `allComplete`, `progress`,
`dateLabel`, `greeting`, `encouragement`. `heroFigure()` and `homeAuroraState()`
are unchanged. `onHeightChanged` is presentation-local measured geometry held by
the screen — the same accepted pattern as the existing `onCollapseRangePx`.

### 8.10 Tests

No new build tooling. Bounds + semantics only, never pixels.

| Test | Action |
|---|---|
`HomeHeroAssetsTest` (unit) | **Update** `the Hero ends with its content, never with dead space` — the weighted spacer must now sit **above** `HeroHeader`, not between it and `HeroProgressBlock`. **Update** `an empty day has no progress block…` — the empty day now carries `ArtworkCrown`; assert `lerp(ArtworkCrown, 0.dp, p)` and that the empty day has no collapse travel. **Add:** `HeroOrbGap` present; **no** `Modifier.weight(1f)` between the figure column and `ProgressOrb`; no screen-percentage floor; `HeroRingSize = 80.dp` still. **Unchanged:** artwork-first-layer, no-top-band, no-veil-machinery, theme-only selection.
`HomeAccessibilityTest` (unit) | **Update** `the content-driven hero height cannot clip large fonts` — the compact sum must still exist and `ArtworkCrown` must be the only addition; still ≥ 2 `onSizeChanged`; still no `screenHeightDp`. **Add:** `HeroOrbGap` is `MomentumSpacing.standard`; `Modifier.width(` present.
`QuickAddTest` (unit) | **Rewrite** `Home hides the FAB on an empty day and drops the star glyph` → `Home always shows the FAB and the empty state carries no CTA`: assert no `AnimatedVisibility` at the FAB call site, `MomentumFab(` present, `HomeEmptyState(` takes no `onAdd`, `strings.xml` has no `today_empty_action`, the list still reserves room (new expression), no `Star` glyph. Retain the shared-FAB, 56 dp, `cd_quick_add` and nav assertions unchanged. **Amendment B (§8.15.4): additionally** replace the assertion `fab.contains("Color.White.copy(alpha = 0.28f)")` with `fab.contains("MomentumGlass.borderDark")` + `fab.contains("MomentumGlass.borderLight")` + `fab.contains("MomentumGlass.heroElevation")` and `assertFalse(fab.contains("Color.White.copy(alpha = 0.28f)"))`. All other material assertions (`CircleShape`, add glyph, `prominent`, `Brush.verticalGradient`, `shadow(`, `glassPress`, `Role.Button`) stay unchanged.
`HomeScrollSurfaceTest` (unit) | **No change required** — still exactly one `LazyColumn`, still one `HomeHero(`, still `if (state.isEmpty)`. Verified against §8.4.
`HomeHeroCompositionTest` (androidTest) | **Rewrite** `emptyDayHeroIsASlimHeaderWithNoProgressBlock` → `emptyDayHeroStillShowsTheArtworkCrown`: `artworkBounds == heroBounds`, `HeroContentTag` absent, hero height ≥ `statusBar + HeroTopInset + ArtworkCrown` (tolerance 1 dp) and ≤ that + header + `HeroBottomInset` + 1 dp. **Update** `dateSitsDirectlyOnTheArtworkWithNoBandAboveIt`: assert `artwork.top <= 0.dp` still (no page background above the artwork) and `dateGap >= statusBars + ArtworkCrown`. `heroEndsShortlyAfterTheLastCopyWithNoDeadSpace` **stays valid unchanged**. **Add** `progressFigureAndOrbAreOneGroup`: `orb.left − figure.right <= HeroOrbGap + 1.dp` and `figure.left == heroLeft + MomentumSpacing.screen`. **Add** `theCopyGeometryIsScrollInvariant`: header→progress gap equals `HeaderProgressGap` at `p = 0` and at `p = 1`. **Add (Amendment A, §8.14.3 BOUND-1):** `artworkIsPresentButNotCovered` — hero height − 88 dp ≥ `HeroRingSize` in empty / expanded / collapsed, **and** the crown rectangle contains **no** text node, no content-description node and no semantics node (the crown is space).
`HomeStatesTest`, `HomeHeroStateTest`, `HomeSectionMotionTest` | **No change required** — state derivation, milestone transient and section motion are untouched. Stated explicitly so the Reviewer knows it was checked, not skipped.

**Runnable validation (no `adb` in this environment):**
`:app:testDebugUnitTest`, `:app:assembleDebug`, `:app:assembleDebugAndroidTest`.
On-device verification stays with the human.

### 8.11 Risks and residual items

- **RISK-1 — CLOSED by human ruling (2026-09-30) and now specified.** The FAB
  material is no longer frozen: §8.15 specifies the narrowly scoped,
  existing-token visual-integration adjustment (theme-resolved hairline +
  `MomentumGlass.heroElevation`). One item remains inside it and is **not**
  Architect discretion: the **shared `HabitsScreen` consequence** requires the
  human's explicit approval (§8.15.3). No other FAB risk is open.
- **RISK-2 — reclassified from an open Architect question to a DEFERRED,
  evidence-gated decision (human ruling, 2026-09-30).** The artwork's luminous
  region still cannot be measured in this environment (no WebP decoder), but the
  ruling is explicit: implement **96 dp**, do **not** proactively raise it, and
  let on-device verification decide. If the artwork is still inadequately
  visible on device, the **only** sanctioned escalation is
  `HeroRingSize + MomentumSpacing.section` = 104 dp, re-measured per §8.6. No
  other crown size may be introduced without returning through the decision
  process. `BOUND-1` (§8.14.3) is the derived guard that makes the 96 dp value
  defensible in the meantime.
- **RISK-3 (contrast):** the scrim may need to move within `[0.15, 0.35]` after
  re-measurement. Contained, pre-bounded by §8.6. Note that
  `ScrimPeakEnd = 0.80f` and the 4-stop profile are **load-bearing** for the
  continuous bottom merge (§8.14.2) and must not be changed as part of a
  contrast tweak — only the peak *value* may move, inside the envelope.
- **RISK-4:** `HomeHeroCompositionTest` runs only on a device/emulator, which
  this environment does not have; it can be **compiled** but not executed here.
  Its rewrite must be reviewed by reading.
- **RISK-5 (new, recorded not fixed):** both `HeroScrim` and
  `MomentumHeroArtwork`'s `drawWithCache` blocks re-run per frame during the
  collapse drag (96 dp of travel, two `Brush` allocations per frame). This is
  **pre-existing and already accepted** by TASK-015 as the residual of a height
  that must change layout. Explicitly a **non-goal** for this pass (§8.14.5) so
  it is not mistaken for a new defect or "fixed" out of scope.

### 8.12 Scroll-driven transformation — REJECTED for this pass (explicit decision)

**Recommendation: reject. Ship the static composition first.**

Rationale:
1. The scroll already *is* the transformation. The crown opens and closes with
   the drag — that is the human's idea (a Hero that re-composes as you scroll)
   expressed through the **already-approved** single px source of truth, one
   `NestedScrollConnection`, no new state, no clock, and reduced motion already
   correct. A second, independent orb-translation animation would add a second
   interpolation, a second reduced-motion semantic ("may the orb move?"), and a
   second thing that can desynchronise from the collapse.
2. It is only evaluable *against* a static composition that is already good.
   The static composition is what §8.2–§8.5 delivers; judging a scroll
   transformation before seeing it would invert the order.
3. It fights the approved arrangement. ADR-47 keeps the greeting top-anchored
   and the progress below it; the transformation the human sketched (orb above →
   orb left / progress right) would reverse that reading order mid-scroll.
4. It is not needed to fix any of E1–E4 / N1–N3. All seven findings are fixed
   statically.

This rejection is recorded in ADR-48 Amendment Note 2 so it is not silently
reintroduced. It may be raised again as a **new** proposal with its own
rationale, cost analysis and motion spec, and it would then require the human
approval gate like any other new behaviour. **It is not authorized by this
task.**

### 8.13 Human approval gate — ruling outcome (2026-09-30) + the remaining amendment review

**The human ruled on 2026-09-30 (`task.md` §4.5, authoritative). Decisions 1, 2
and 3 are APPROVED. Decision 4 is AMENDED. Implementation is still NOT
authorized: §8.14 and §8.15 must be reviewed and accepted before the Developer
starts.**

| Point | Ruling | State |
|---|---|---|
| **§8.2** — artwork-driven Hero height, crown above the copy, empty Hero ≈ 208 dp | **APPROVED** | Binding. Crown stays **96 dp**, derived from existing tokens. The governing concept is recorded: **artwork space is reclaimed first during collapse — never greeting / progress / content space.** |
| **§8.4 + §8.5** — FAB present in all Home states; empty-state CTA removed; measured centring, no `Spacer` values, no screen percentages | **APPROVED** | Binding. `task.md` §4.5 restates it as the product requirement. |
| **§8.12** — scroll-driven transformation | **APPROVED AS REJECTED / DEFERRED** | Binding. No orb-left / count-right transformation, no extra scroll state, no interpolation logic, no extra choreography. May be reconsidered in a future task from real device evaluation. |
| **§8.13 item 4 / RISK-1** — FAB material | **AMENDED → now specified in §8.15** | A narrowly scoped, existing-token visual-integration adjustment is **authorized and now specified**: the theme-resolved hairline + `MomentumGlass.heroElevation`. **RISK-1 is CLOSED by ruling.** No new FAB visual system, no global FAB redesign, no new component architecture, no arbitrary hard-coded colours, no unrelated screen changes. |
| **New — Hero layer / atmosphere contract** | **NEW, BINDING** | Specified in **§8.14**: three-layer z-order, the crown is transparent space (not an image band), the artwork is L0 BACKGROUND and the copy is L2 FOREGROUND over it, foreground dominates via type hierarchy and negative space (never an opaque cover), the shared 88 dp dissolve + 4-stop scrim are **verified** to give a continuous merge with no hard boundary in both themes at 208/216/312 dp, ADR-39's "no top fade" is verified still correct, and the two failure bounds (BOUND-1 artwork presence, BOUND-2 no washed-out foreground) carry derived Reviewer assertions. `MomentumHeroArtwork.kt` and the scrim profile are **not** changed — §8.9's boundaries stand. |
| **Crown escalation 96 → 104 dp** | **DEFERRED, not pre-emptive** | Implement **96 dp**. Do not proactively raise it. On-device verification decides. If the artwork is still inadequately visible on device, the **only** sanctioned escalation is `HeroRingSize + MomentumSpacing.section` = 104 dp, re-measured per §8.6. No other crown size may be introduced without returning through the decision process. |

**Gate status: the human's review of §8.14 + §8.15 is still mandatory before any
implementation is authorized.** Specifically outstanding:

1. **§8.15.3** — explicit approval of the single shared-call-site consequence
   (Habits' FAB hairline changes too), with no compliant alternative available.
2. **§8.14** — acceptance of the three-layer contract, the verified
   continuous-merge verdict (no `MomentumHeroArtwork` / scrim change), and the two
   failure bounds.
3. Acknowledgement that **RISK-1 is closed**, and that **RISK-2 is now a
   deferred on-device decision** (96 dp first, 104 dp only if device evidence
   demands it) rather than an open Architect question.

**Human-mandated on-device verification (15 items, §4.5):** empty+Light ·
empty+Dark · one activity · multiple activities · partial completion · complete
state · Light+Dark · FAB availability · FAB visual integration · Hero artwork
visibility · Hero artwork blending/dissolve · foreground readability over the
artwork · progress/orb composition · accessibility · existing TASK-015 milestone
behaviour. The artwork **must be visually inspected on device**. No instrumented
or device test may be claimed as passing unless it was actually executed
(`adb` is unavailable in this environment).

On acceptance: Orchestrator clears §9, Developer implements §8.2/§8.3/§8.4/
§8.5/§8.14/§8.15 exactly, Reviewer verifies against §8.10 + §8.6 + §8.14.3 +
§8.15.4, and the human performs the on-device verification.

---

### 8.14 Amendment A — the Hero layer / atmosphere contract (binding implementation clarification, `task.md` §4.5)

> Added by the Architect on the human ruling of 2026-09-30. **Decisions 1–3 are
> approved and unchanged.** This section specifies *what the §8.2 crown means in
> layer terms* and *how the continuous blend is guaranteed and verified*. It does
> not re-derive the crown model, and it changes no approved number.

#### 8.14.1 Layer contract and z-order (binding — the Developer must not read the crown as an image band)

The Hero's `Box` has **three** layers, drawn in this order. Nothing else is
drawn inside the Hero.

```text
Box(HomeHero, height = currentHeight, .clipToBounds())
│
├─ L0 BACKGROUND    MomentumHeroArtwork(Alignment.BottomCenter, Modifier.fillMaxSize())
│                   • full-bleed: fills the Hero's ENTIRE box; its top edge IS
│                     the physical window top edge (ADR-33/39) — unchanged call.
│                   • clipped by: the Hero's clipToBounds().
│                   • merges into the page background at: the Hero's BOTTOM
│                     edge, via the shared 88 dp bottom dissolve (§8.14.2).
│
├─ L1 ATMOSPHERE    HeroScrim(Modifier.fillMaxSize())
│                   • 4-stop page-background gradient, unchanged:
│                     0f→clear, ScrimPeakStart(0.20f)→peak,
│                     ScrimPeakEnd(0.80f)→peak, 1f→clear.
│                   • NEVER opaque: peak 0.26 Light / 0.22 Dark, inside the
│                     approved [0.15, 0.35] envelope. This is a TINT, not a
│                     cover (ruling requirement 5).
│                   • merges into the page background at the same bottom edge
│                     (α → exactly 0 there).
│
└─ L2 FOREGROUND    Column(Modifier.fillMaxSize()
│                         .padding(horizontal = MomentumSpacing.screen)
│                         .graphicsLayer(CompositingStrategy.Offscreen)
│                         .drawWithContent { … ADR-35 screen-space top mask … })
      │
      ├─ Spacer(statusBarInset + HeroTopInset)      transparent — no child
      ├─ Spacer(crown)                               transparent — no child  ← §8.2
      ├─ HeroHeader(dateLabel · greeting · Settings)
      ├─ Spacer(MomentumSpacing.section)            only when hasProgress
      ├─ HeroProgressBlock(figure/caption · orb · [encouragement])  only when hasProgress
      └─ Spacer(HeroBottomInset)
```

Five binding statements that make the ruling unambiguous:

1. **The crown contains no child.** It is *space*, not a layer: no image, no
   card, no panel, no gradient band, no divider, no rule, no glyph. Its only
   purpose is to reserve the vertical band in which **L0 and L1 are the sole
   visible content**, so the artwork is seen unobstructed and is the only thing
   occupying that band. A `Spacer(crown)` with nothing inside it is the correct
   implementation; anything drawn there is a defect.
2. **There is no sub-surface behind any foreground copy anywhere in the Hero.**
   Not a card, not `LiquidGlass`, not `Surface`, not a solid `surface` fill, not
   a scrim rectangle. The single exception is the existing
   `LiquidGlassIconButton` (the Settings control) inside the header row — it is
   existing, approved, and 48 dp, and it is *copy-adjacent chrome*, not a copy
   backdrop.
3. **Foreground dominance is achieved with type hierarchy and negative space,
   not with a cover.** `MomentumFigureDisplay` (Display 40/48/600) > greeting
   (`headlineMedium`) > date and caption (`labelLarge`), all `textPrimary`, plus
   the crown and the 24 dp section gap as negative space. Nothing is boxed.
4. **The artwork is never re-tinted, zoomed, re-cropped, or replaced.** The
   `MomentumHeroArtwork` call is byte-identical to today's:
   `Alignment.BottomCenter`, `Modifier.fillMaxSize().testTag(HeroArtworkTag)`,
   shared `topDissolve`/`bottomDissolve`, no `zoom`, no `parallax`, no
   `ColorFilter`.
5. **`MomentumHeroArtwork.kt` is NOT modified** (see §8.14.2 for the
   verification that proves it does not need to be), and the scrim profile is
   **not** modified. §8.9's boundaries stand unchanged.

#### 8.14.2 The continuous bottom merge — verified against the actual component, both themes, all heights

**Verdict: the current shared 88 dp `bottomDissolve` + the 4-stop scrim already
deliver a continuous merge with no hard boundary in both themes at 208 / 216 /
312 dp (and at ~368 / ~272 dp when the encouragement line is present). No
correction is required, and none is made.** This was read from the source, not
assumed:

1. **The artwork's alpha reaches exactly 0 at the Hero's bottom edge.**
   `MomentumHeroArtwork.HeroArtworkImage` builds the profile with
   `Brush.verticalGradient(colorStops = arrayOf(0f to White, bottomStart to White,
   1f to Transparent), startY = 0f, endY = h)` and applies it with
   `drawRect(brush = dissolve, blendMode = BlendMode.DstIn)`, where `h` is the
   measured `size.height`. The last stop sits at `endY = h` with full
   transparency, so at the Hero's bottom edge the artwork contributes **nothing**.
   `bottomStart = (1f − 88.dp/h)` = 0.577 / 0.593 / 0.718 for h = 208 / 216 /
   312 — every value lies inside the component's own `coerceIn(0.10f, 0.95f)`
   clamp, so the ramp is never truncated and never degenerates into an abrupt
   step.
2. **Theme-independent.** `DstIn` reads **alpha only**; the profile runs
   `Color.White → Color.Transparent` and the component's own KDoc states the mask
   colour carries no hue. Light and Dark therefore share one identical alpha
   profile, and the merge cannot differ between themes.
3. **The scrim also reaches exactly 0 at the same edge** (`1f to clear`). This is
   **load-bearing and must not be changed.** `HeroScrim` is a `Box(fillMaxSize)`
   inside the clipped Hero, so if the scrim ended at its peak it would leave a
   full-width band of `c.background` at alpha 0.26 (Light) / 0.22 (Dark) sitting
   directly against the un-tinted Aurora field below the Hero — i.e. a **visible
   hard horizontal edge at 26 %/22 % opacity across the whole screen width.**
   The existing 4-stop profile is what prevents that. `ScrimPeakEnd = 0.80f` is
   therefore **retained exactly**, and no correction is applied to it.
4. **Nothing below the Hero changes identity.** Below the Hero's bottom edge the
   same `AuroraBackground` continues (`HomeAurora(Modifier.fillMaxSize())` sits
   behind the whole `LazyColumn`), and the first list item draws over it with its
   own surface. No layer changes opacity at the boundary.
5. **No top boundary is ever created — ADR-39's "no top fade" stays correct.**
   `clipToBounds()` clips at the Hero's top edge, but that edge is pinned to
   `y = 0` of the scroll surface (ADR-33). The Hero is the first `item` of the
   `LazyColumn`, so collapsing shrinks its height from its **bottom** and moves
   the dissolve hand-off *upward* while the top edge stays at the window top;
   when the list scrolls, the Hero's top leaves through the **window** edge, which
   is not a page-background boundary. **No top fade is added and none is needed.**
6. **The only profile discontinuity is a first-derivative kink** at each ramp
   start (the artwork's at `h − 88`, the scrim's at `0.80h`). Both ramps are
   value-continuous, so neither is a hard boundary.

**One finding recorded, deliberately not "fixed":** the scrim's bottom ramp
`[0.80h → h]` **overlaps** the artwork's dissolve `[h − 88 → h]` by 41.6 dp at
h = 208, 43.2 dp at 216, 62.4 dp at 312 and 73.6 dp at 368. In that overlap both
layers fade at once, so the artwork's *apparent* hand-off is compounded rather
than a single 88 dp ramp. This is **not** a hard edge and it does not violate the
ruling — a clean, slightly faster hand-off into the list is desirable, and ADR-47
requires the hand-off. It is, however, the exact mechanism by which artwork
presence could regress toward the E1 failure, so it is the stated **derivation**
for BOUND-1 below and for the §8.6 sweep heights.

Changing it was considered and **rejected**: tiling the ramps exactly would
require duplicating the component's 88 dp default into `HomeHero` as a second
source of truth (violating §8.9's boundary and ADR-30's caller contract) *and*
would invalidate the contrast baseline ADR-47 Amendment Note 1 fixed as final,
for a benefit the ruling does not ask for (the ruling's requirement 6 is about
the **boundary**, and points 1–6 above show the boundary is already correct).

#### 8.14.3 The two failure bounds and how the implementation stays inside them

**BOUND-1 — Artwork must NOT become invisible (E1 regression guard).**

*Derivation.* The artwork's fully-opaque region inside the Hero is exactly
`[0, h − 88]`; the remaining 88 dp is the hand-off and is *supposed* to fade to
the page background. Ruling requirement 4 ("recognizable and visually present")
is satisfied if the Hero always presents at least **one `HeroRingSize` (80 dp) of
unobstructed, fully-opaque artwork**, because 80 dp is the smallest band on a
phone that can hold a recognizable fragment of the illustration, and it is the
same element the crown is derived from. Two consequences:

- **Window check:** `h − 88 ≥ HeroRingSize` in every state.
  208 − 88 = **120 ≥ 80** ✔ (margin 40 dp) · 216 − 88 = **128 ≥ 80** ✔ (margin 48)
  · 312 − 88 = **224 ≥ 80** ✔ (margin 144) · 368 − 88 = **280** ✔ (encouragement
  variant) · 272 − 88 = **184** ✔ (collapsed with encouragement).
- **Crown check (the binding one):** the crown is the only band where artwork
  presence is independent of where the copy sits, because L2 has no child there.
  Requirement: `ArtworkCrown ≥ HeroRingSize`. **96 = 80 + 16** ✔ (margin 16 dp =
  `MomentumSpacing.standard`). The deferred escalation
  `HeroRingSize + MomentumSpacing.section` = 104 dp is the **only** sanctioned
  change and is **not** pre-emptive (ruling, "Crown escalation").
- **Code tripwire:** the artwork window is `h − DefaultBottomDissolve`; it must
  **never** be derived from `HeroRingSize`. `ArtworkCrown` is the only new
  constant this pass introduces.

*Reviewer assertions.*
- **Runnable / instrumentable (bounds + structure — `HomeHeroCompositionTest`):**
  (a) the Hero's height minus 88 dp is `≥ HeroRingSize` in all three states
     (empty, populated expanded, populated collapsed), tolerance 1 dp;
  (b) `artworkBounds == heroBounds` in every state — L0 is genuinely full-bleed;
  (c) **the crown band contains no node at all**: assert that within the crown
     rectangle there is no text node, no content-description node and no
     semantics node (a direct, automatable proxy for "the crown is space");
  (d) `ScrimPeakLight`/`ScrimPeakDark` still parse inside `[0.15, 0.35]`.
- **Human, on device only — not claimable as a test:** the Aurora illustration
  must be *recognisable* inside the 96 dp crown band immediately below the
  status-bar reserve, in **both** themes (ruling verification items 10/11). `adb`
  is unavailable in this environment, so no instrumented or on-device check may
  be reported as executed unless it actually was.

**BOUND-2 — Foreground must NOT be washed out and the artwork must NOT be covered.**

*Derivation.* Ruling requirement 5 forbids solving readability with an opaque
layer, and ADR-47 §2 + ADR-47 Amendment Note 1 already bound the scrim peak to
`[0.15, 0.35]` (currently 0.26 Light / 0.22 Dark). The enforceable ceiling is
therefore **exactly the existing peak ceiling** — nothing may raise the peak
beyond 0.35, and nothing may place any opaque surface behind the copy inside the
Hero. The floor is the existing ISSUE-020 acceptance floor: **≥ 4.5:1**.

*Reviewer assertions.*
- **Runnable (§8.6 sweep, runnable in part):** every copy element's text
  rectangle, both themes × heights **{208, 216, 272, 312, 368} dp** × widths
  **{360, 412} dp** × `{Fraction, Complete}` × encouragement `{on, off}`,
  region-sampled against `textPrimary`, must clear **≥ 4.5:1**. The encouragement
  line and the collapsed states are now explicitly in the sweep because the
  compounded hand-off (§8.14.2) and the content-dependent Hero height both move
  the copy relative to the artwork.
- **Runnable (structural, `HomeHeroAssetsTest` / `HomeAccessibilityTest`):**
  `HomeHero.kt` contains no `textSecondary`; and between `HeroHeader(` and
  `HeroProgressBlock(` there is **no** `LiquidGlass`, no `Card`, no `Surface`,
  and no `background(` call — the Hero's only `background`/brush draws are the
  artwork (L0) and the scrim (L1).
- **Runnable (bounds, instrumented):** every copy node's bounds lie **inside**
  `artworkBounds` in every state and both themes (existing assertion, retained).
- **Human, on device only:** the illustration must still be visible *behind and
  around* the copy — the copy's backing must not read as a flat plate
  (verification item 12).

#### 8.14.4 Exact foreground-over-artwork geometry — all three states

At a 360 dp window with a 24 dp status bar. `header ≈ 56 dp`,
`progressRow = 80 dp` (= `HeroRingSize`), `encouragementBlock ≈ 56 dp` when
present. All heights are **measured content sums**, so they shift with font
scale — the *model* is binding, the absolute totals are illustrative.

| Region | **Empty** (h ≈ 208) | **Populated, expanded** (h ≈ 312 / 368) | **Populated, collapsed** (h ≈ 216 / 272) |
|---|---|---|---|
| 0 → 24 | status-bar reserve. L0 α = 1; L1 ramping up. **No copy.** | identical | identical |
| 24 → 136 | **CROWN — 96 dp.** L0 + L1 (L1 at peak from 41.6). **No copy, no node.** | **CROWN — 96 dp.** same | **crown = 0** — L2's first child starts at 40 |
| 40 / 136 → +56 | header: date + greeting + Settings (all over L0/L1) | same | header at 40 → 96 |
| +24 | **absent** | section gap 192 → 216 | section gap 96 → 120 |
| +80 | **absent — no progress block, no orb, no reserved space** | progress row 216 → 296 (figure + caption · orb) | progress row 120 → 200 |
| +56 | **absent** | encouragement 296 → 352 **when present**, else absent | encouragement 200 → 256 **when present**, else absent |
| last 16 | `HeroBottomInset` 192 → 208 | 296 → 312 | 200 → 216 |

**The encouragement line, explicitly:**
- **Yes — the encouragement line is over the artwork** in every state where it
  exists. It is a child of `HeroProgressBlock`, which is a child of L2, inside
  the clipped Hero box. This is unchanged from TASK-015 and is required by the
  ruling: it must **not** be lifted onto the page background, because that would
  put a solid surface between the artwork region and the page background and
  would create exactly the hard boundary the ruling forbids.
- Its backing is the **compounded zone**: below `0.80h` the scrim is ramping to
  transparent while the artwork is inside its 88 dp dissolve. Its region sample
  is therefore *more* favourable than the progress row's — but it is still
  measured explicitly in §8.6, and `maxLines = 2` + ellipsis are unchanged so
  the Hero can never grow unbounded.
- It appears/disappears via `AnimatedVisibility` (fade + expand/shrink, none
  under reduced motion) with **no reserved space** when absent — unchanged. When
  it toggles, the Hero's height changes, so `onHeightChanged` re-fires (it is a
  measured sum, so this is automatic) and the empty-day centring region of §8.4
  is unaffected because the empty state never shows the line.

**Unchanged in this sub-layer:** `LiquidGlassIconButton` (Settings, top-trailing
of the header row, 48 dp target), the merged
`today_hero_progress_desc` semantics on the progress row, `showPercent = false`,
`textPrimary` throughout, the milestone glow, and the ADR-35 screen-space mask.

#### 8.14.5 Motion, accessibility and performance for this sub-layer

- **Motion: none new.** The crown's collapse is the existing 1:1 px `lerp`; the
  artwork's alpha profile and the scrim's gradient are **static geometry**, not
  animated, so reduced motion behaves identically (there is no time-based
  component to disable). Recorded so the Reviewer does not look for a
  reduced-motion branch here.
- **Accessibility: unchanged.** One merged progress description; the orb carries
  no semantics; Settings 48 dp; TalkBack reading order (empty: date → greeting →
  Settings → empty card → Quick Add; populated: date → greeting → Settings →
  progress → sections) is unchanged by this sub-layer.
- **Performance — explicit non-goal.** Both `HeroScrim` and
  `MomentumHeroArtwork`'s `drawWithCache` blocks re-run whenever the Hero's
  height changes, i.e. per frame during the collapse drag (96 dp of travel, two
  cached `Brush` allocations). This is **pre-existing and already accepted**
  (TASK-015 recorded it as "the accepted residual" because the height must
  change layout). The Developer must **not** optimise it in this pass — it is
  outside the ruling and would expand scope. Noted so it is not mistaken for a
  new defect.

---

### 8.15 Amendment B — FAB visual integration (supersedes §8.5's "material NOT changed"; closes RISK-1)

> Added by the Architect on the human ruling of 2026-09-30. **§8.5's placement
> and availability spec stands unchanged and remains the primary fix.** This
> section adds the narrowly scoped visual-integration adjustment the ruling
> authorised, and rules on RISK-1 rather than leaving it open.

#### 8.15.1 Diagnosis of the disconnection (concrete, both themes, from the current `MomentumFab.kt`)

Home's FAB sits between two things that both define themselves with an **edge**:
the **Aurora artwork / page background** behind and above it, and the **glass
bottom navigation bar** directly below it. Both of those use the design system's
edge vocabulary (`MomentumGlass.borderDark` / `borderLight` hairlines plus a
`MomentumGlass.highlight` top streak). `MomentumFab` does not use it, and the
mismatch is theme-asymmetric:

1. **Light — the FAB has effectively no edge.** The hairline is
   `Color.White.copy(alpha = 0.28f)`: white, at 28 %, on a light-violet
   `primary` disc, over a near-white page. `MomentumGlass.borderLight`'s own KDoc
   says "white is invisible" — that is precisely why every *other* light surface
   in the app (`EmptyState`, `LiquidGlass`, the bottom bar) separates with an
   **ink** hairline (`c.textPrimary.copy(alpha = MomentumGlass.borderLight)`).
   The FAB is the only light surface using a white edge, so in Light it reads as
   a soft blob whose only separator is a black ambient shadow.
2. **Dark — the shadow cannot separate anything, and the edge is over-strong.**
   `shadow(elevation = 12.dp, ambientColor = Color.Black.copy(alpha = 0.30f), …)`
   over `c.background` in Dark is a black shadow on a near-black page: invisible
   by construction. Dark surfaces in this app are separated by an **edge**, not a
   shadow. Meanwhile the FAB's 0.28 white hairline is the strongest white edge
   in the dark theme — the glass bar, the cards and the dialogs all use 0.16 —
   so the FAB does not match its neighbours in the theme where it already reads.
3. **Both themes — the elevation is a magic number.** The literal `12.dp`
   duplicates `MomentumGlass.heroElevation = 12.dp`, which exists precisely for
   floating layers.

So the disconnection is an **edge-vocabulary** problem, not a fill, shape, size
or glyph problem. The fill gradient, the 56 dp circular shape, the `prominent`
(28 dp) add glyph, the press feedback and `Role.Button` are all correct and are
the approved shared identity from ADR-47 §6.

#### 8.15.2 Ruling on Amendment B — the smallest concrete adjustment, in existing tokens only

**Ruled: placement (§8.5) is the primary fix and stays exactly as specified. The
material needs exactly ONE visible change — the hairline becomes the design
system's theme-resolved edge — plus one token substitution with zero visual
effect. Nothing else in `MomentumFab` changes.**

```kotlin
// MomentumFab.kt — the ONLY two edits
.shadow(
    elevation = MomentumGlass.heroElevation,        // was 12.dp — identical value,
                                                      //   now tokenised (no visual change)
    shape = shape,
    ambientColor = Color.Black.copy(alpha = 0.30f),  // unchanged
    spotColor = c.primary.copy(alpha = 0.40f),       // unchanged
)
.border(
    BorderStroke(
        Dp.Hairline,
        if (c.isDark) {
            Color.White.copy(alpha = MomentumGlass.borderDark)     // 0.16 white
        } else {
            c.textPrimary.copy(alpha = MomentumGlass.borderLight)  // 0.10 ink
        },
    ),
    shape,
)
```

- **This is the pattern `EmptyState` already uses** for its own hairline
  (`MomentumBadges.kt` L210–217), so the FAB adopts existing, proven vocabulary
  rather than introducing any.
- **Light: white 0.28 → ink `textPrimary` @ 0.10.** The FAB gains a real,
  theme-correct edge on a light page and now reads with the same edge weight as
  the glass bottom bar directly beneath it. **This is the integration fix.**
- **Dark: white 0.28 → white 0.16.** Slightly softer, and now identical to every
  other dark surface in the app. Dark loses no separation because Dark never had
  a working shadow to lose — its separator is the edge, which the system value
  supplies.
- **No new token, no new value, no new component, no new API, no new visual
  language, no hard-coded colour added** (the change *removes* the only
  remaining hard-coded hairline alpha). The fill gradient, its stops, the shape,
  `MomentumFabSize = 56.dp`, `MomentumIconSizes.prominent`, `glassPress` and
  `Role.Button` are untouched.
- **Rejected alternatives, recorded:** (a) *placement-only* — I do **not** rule
  for it; the Light-theme invisible edge is a real, diagnosed defect that
  placement cannot fix, and the ruling authorises the fix; (b) a glass/liquid
  FAB or a gradient ring — that is a new visual system and is forbidden;
  (c) adding an optional `border`/`emphasised` parameter to `MomentumFab` so
  Home could differ from Habits — that is new component architecture and is
  forbidden by the ruling, which is why §8.15.3's shared consequence is stated
  for approval instead.

#### 8.15.3 Shared call-site consequence — explicit approval required

`MomentumFab.kt` is shared with `HabitsScreen` (`cd_create_habit`). The hairline
change therefore affects **Habits' FAB as well**: in Light it gains the same ink
hairline, in Dark its white hairline becomes 0.16. Nothing else on Habits
changes — same component, same 56 dp, same glyph, same placement, same
behaviour.

- **This is not an "unrelated change to another screen"** — it is the direct,
  unavoidable consequence of fixing a shared component, and it makes the two
  screens consistent with each other and with every other surface.
- **It cannot be scoped to Home without forbidden changes** (a new parameter or
  a duplicated component), so there is no compliant alternative to approve.
- **The human must explicitly approve this consequence.** If it is refused, the
  only lawful outcomes are (i) accept placement-only and re-open RISK-1, or
  (ii) a future task that owns `MomentumFab` as a component. The Architect's
  recommendation is to approve: the change makes Habits *more* consistent, not
  less.
- `QuickAddTest`'s existing assertion `fab.contains("Color.White.copy(alpha = 0.28f)")`
  pins the old literal and **must be updated** (see §8.10).

#### 8.15.4 Light / Dark requirements and per-theme assertions

| | **Light** | **Dark** |
|---|---|---|
| Edge | `c.textPrimary` @ `MomentumGlass.borderLight` (0.10), `Dp.Hairline` | `Color.White` @ `MomentumGlass.borderDark` (0.16), `Dp.Hairline` |
| Separator mechanism | edge (primary) + ambient shadow (secondary) | edge (primary); the shadow is **not** relied on |
| Separation from the glass bottom bar below | same edge vocabulary as the bar | same edge value as the bar |
| Separation from the Aurora artwork / page behind | ink edge reads on a near-white page | white edge reads on a near-black page |
| Fill / shape / glyph | unchanged (approved ADR-47 §6 identity) | unchanged |

*Reviewer assertions.*
- **Runnable (source-level, `QuickAddTest`):** `MomentumFab.kt` contains
  `MomentumGlass.borderDark` **and** `MomentumGlass.borderLight` **and**
  `MomentumGlass.heroElevation`; and it **no longer** contains
  `Color.White.copy(alpha = 0.28f)`. (Replace the existing literal assertion.)
- **Runnable (source-level, unchanged assertions that must still hold):**
  `CircleShape`, `MomentumIcons.add`, `MomentumIconSizes.prominent`,
  `Brush.verticalGradient` fill, `shadow(`, `glassPress(interaction)`,
  `role = Role.Button`, `MomentumFabSize = 56.dp`, `contentDescription: String`.
- **Runnable (placement, `TodayScreen.kt`):** `end = MomentumSpacing.screen` and
  `bottom = MomentumSpacing.section`; list reservation
  `MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section`; FAB
  composed **unconditionally** (no `AnimatedVisibility` at the call site).
- **Human, on device only (ruling verification items 8/9):** in **Light**, the
  FAB reads as a defined disc against the near-white page and its edge reads with
  the same weight as the bottom bar's edge; in **Dark**, the FAB's rim is visible
  without any need for the page behind it to be brighter. Not claimable as a test
  here — `adb` is unavailable.

---

## 9. IMPLEMENTATION STATUS

<!-- DEVELOPER OWNER -->

**Status: COMPLETE.** §8 implemented exactly as specified, including Amendment A
(§8.14) and Amendment B (§8.15). No commit, no push.

### 9.1 What changed
| File | Change |
|---|---|
| `screens/today/HomeHero.kt` | Artwork-driven height model (§8.2): `ArtworkCrown = HeroRingSize + MomentumSpacing.standard` (96 dp) replaces `CollapseHeadroom`; `crown = lerp(ArtworkCrown, 0.dp, p)`; `HeaderProgressGap → MomentumSpacing.section`; new `HeroOrbGap = MomentumSpacing.standard`; the weighted spacer **removed**; the crown inserted as an empty `Spacer(Modifier.height(crown))` **above** `HeroHeader` (§8.14.1); the progress figure `Column` loses `Modifier.weight(1f)` and is joined to `ProgressOrb` by `Spacer(Modifier.width(HeroOrbGap))` (§8.3); new `onHeightChanged: (Int) -> Unit` reported from `onSizeChanged`; KDoc rewritten for the three-layer contract; `ScrimPeak*` KDoc re-measured. |
| `screens/today/TodayScreen.kt` | Root is now `BoxWithConstraints`; measured `heroHeightPx`; one named `EmptyDayBottomReserve = MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section` (96 dp) used as **both** `contentPadding.bottom` and the empty card's centring region (`emptyRegion`); the empty item is `Box(Modifier.heightIn(min = emptyRegion), contentAlignment = Alignment.Center)`; `HomeEmptyState(modifier)` takes **no** `onAdd`; `MomentumFab` composed **unconditionally** with `align(BottomEnd)` and `padding(end = screen, bottom = section)`; dead `AnimatedVisibility`/`scaleIn`/`scaleOut` imports removed. |
| `components/MomentumBadges.kt` | `EmptyState`'s CTA is **optional** via additive defaults only (`actionLabel: String? = null`, `onAction: (() -> Unit)? = null`); the `TextButton` renders only when both are non-null. The three other call sites pass named arguments and are visually unchanged. |
| `res/values/strings.xml` | `today_empty_action` deleted (now unreferenced). |
| `components/MomentumFab.kt` | **Exactly the two §8.15 edits and nothing else**: `elevation = MomentumGlass.heroElevation` (same 12 dp value) and the theme-resolved hairline (`Color.White.copy(alpha = MomentumGlass.borderDark)` in Dark, `c.textPrimary.copy(alpha = MomentumGlass.borderLight)` in Light) replacing the hard-coded `Color.White.copy(alpha = 0.28f)`. Fill gradient, stops, `CircleShape`, `MomentumFabSize = 56.dp`, `MomentumIconSizes.prominent`, `glassPress`, `Role.Button`, API and KDoc identity all untouched. |

Tests: `HomeHeroAssetsTest` (updated 2, added 2), `HomeAccessibilityTest`
(updated 1, added 1), `QuickAddTest` (rewritten FAB test + rewritten
availability test with the Amendment B token assertions),
`HomeHeroCompositionTest` (rewritten empty-day test, updated date test, added
`progressFigureAndOrbAreOneGroup`, `theCopyGeometryIsScrollInvariant`,
`theCrownBandContainsNoNodeAtAll` and the three BOUND-1 state tests),
`HomeHeroStateTest` (one required `onHeightChanged = {}` argument for the new
signature). `HomeScrollSurfaceTest`, `HomeStatesTest` untouched as specified.

### 9.2 §8.6 contrast re-measurement — the scrim peaks STAND

Method: the canonical ISSUE-020 **region-sample** measurement, re-run for the
new geometry. Both bundled 1024×1536 WebPs were decoded (WIC) and composited per
copy band as *page background + Aurora field* (`AuroraState.defaultIntensity` for
each state) *+ hero artwork* (cover-crop, `Alignment.BottomCenter`, the shared
88 dp bottom `DstIn` dissolve) *+ the 4-stop page-background scrim*; each band
reduced to one mean background colour and measured against `textPrimary`.

Sweep: both themes × heights {208, 216, 272, 312, 368} dp × widths {360, 412} dp
× 4 Aurora intensities × bands {date, greeting, fraction, caption, complete
statement, merged description, encouragement} = **480 region samples,
0 below 4.5:1.**

| Theme @ peak | Governing (worst) region sample | Where |
|---|---|---|
| **Light @ 0.26** | **8.73:1** | date, 368 dp @ 412 dp, mean `#B3BAF6` |
| **Dark @ 0.22** | **6.29:1** | date, 368 dp @ 360 dp, mean `#584EAF` |

Worst band per theme/height (region mean):

| Height @ width | Light | Dark |
|---|---|---|
| 208 (empty) @ 360 | date 10.75 · greeting 12.10 | date 17.22 · greeting 16.26 |
| 216 (collapsed) @ 360 | date 9.97 · greeting 11.36 · fraction 11.03 · caption 11.93 · complete 11.42 · merged 11.83 · encouragement 13.92 | date 16.47 · greeting 16.95 · fraction 17.32 · caption 16.44 · complete 17.16 · merged 17.09 · encouragement 14.76 |
| 272 (collapsed + line) @ 360 | 11.05 · 9.97 · 11.31 · 10.75 · 11.58 · 11.71 · 11.95 | 11.21 · 15.52 · 17.14 · 17.66 · 17.29 · 17.23 · 16.81 |
| 312 (expanded) @ 360 | 8.87 · 10.59 · 10.72 · 11.31 · 11.22 · 11.32 · 11.07 | 10.64 · 11.24 · 16.34 · 17.07 · 16.45 · 16.42 · 17.61 |
| 368 (expanded + line) @ 360 | 9.02 · 8.94 · 10.31 · 9.99 · 10.29 · 10.32 · 11.54 | **6.29** · 9.50 · 12.68 · 15.83 · 13.78 · 13.89 · 16.93 |

412 dp is equal or better in every case. The governing sample is the **date line
on the tallest Hero** — the copy position furthest down the illustration, which
is the expected worst case now that the copy sits lower in the frame.

**No escalation was required.** `ScrimPeakLight = 0.26f` / `ScrimPeakDark =
0.22f` **stand unchanged**, inside the approved `[0.15, 0.35]` envelope, with
1.8:1 (Dark) and 4.2:1 (Light) of margin over the ISSUE-020 floor. The ink, the
`MomentumHeroArtwork` call and the copy position were **not** touched;
`ScrimPeakStart = 0.20f` / `ScrimPeakEnd = 0.80f` and the 4-stop profile were not
touched (load-bearing for the continuous bottom merge, RISK-3). RISK-3 is
**closed — not realised**. Values recorded in the `ScrimPeak*` KDoc.

### 9.3 BOUND-1 / BOUND-2 (runnable assertions, §8.14.3)

- **BOUND-1 window check** `h − 88 dp ≥ HeroRingSize (80 dp)`:
  208 → 120 (margin 40) · 216 → 128 (48) · 272 → 184 (104) · 312 → 224 (144) ·
  368 → 280 (200). **All pass.**
- **BOUND-1 crown check** `ArtworkCrown (96) ≥ HeroRingSize (80)` → margin
  16 dp = `MomentumSpacing.standard`. **Pass.**
- **BOUND-2 ceiling** scrim peaks `0.26 / 0.22 ≤ 0.35`, and no `LiquidGlass`,
  `Card`, `Surface` or `background(` call exists between `HeroHeader(` and
  `HeroProgressBlock(`; `HomeHero.kt` still contains no `textSecondary`.
  **Pass.**
- `HomeHero.kt` contains no `screenHeightDp`; `screenHeightDp` appears in
  neither file.
- The crown band is asserted to contain **no** text node, **no**
  content-description node and **no** semantics node
  (`theCrownBandContainsNoNodeAtAll`).

### 9.4 Deviations from §8.10 — one, recorded

`HomeSectionMotionTest` was listed as "no change required". It failed after the
implementation: it asserts `count(screen, "if (reduceMotion) {") >= 3`, and the
**third** occurrence was the Quick Add FAB's `AnimatedVisibility`, which §8.5/§8.7
**requires** to be removed (§8.7: "Removing the FAB's `AnimatedVisibility`
**removes** motion … Strictly less motion; not a regression"). §8.7 and the
§8.10 test list are in tension; the approved behavioural decision (§8.7) was
followed and the assertion's bound lowered `3 → 2` with the reason recorded
inline. The assertion's actual subject is unchanged: the remaining two
reduced-motion branches (the branch crossfade and the Aurora intensity snap) still
*replace* their spec rather than shorten it. No other "no change required" test
was touched.

### 9.5 Validation actually run

| Command | Result |
|---|---|
| `:app:testDebugUnitTest` (`--rerun-tasks`) | **PASS — 264 tests, 0 failures, 0 skipped**, actually executed (XML results read back and counted). |
| `:app:assembleDebug` | **PASS** — APK packaged. |
| `:app:assembleDebugAndroidTest` | **PASS** — instrumented APK packaged (**compiled only**). |
| `:app:compileDebugAndroidTestKotlin` | **PASS** (run while iterating). |

`adb` is unavailable in this environment: **no instrumented test was executed and
no on-device verification is claimed.** `HomeHeroCompositionTest`,
`HomeHeroStateTest` and `HomeStatesTest` were compiled and reviewed by reading
only (RISK-4 stands). The 15 human-mandated on-device checks (§4.5) — including
Hero artwork visibility, blending, and FAB visual integration in both themes —
remain outstanding and are the human's to perform. The sanctioned escalation if
the artwork is still inadequately visible on device is
`HeroRingSize + MomentumSpacing.section` = 104 dp, re-measured per §8.6; it was
**not** pre-emptively applied.

### 9.6 Reviewer FAIL — correction record (round 1)

Reviewer returned **FAIL** with three concrete defects. All three are fixed; no
production behaviour changed except the BLOCKER-1 test assertion itself.

| # | Defect | Fix |
|---|---|---|
| **BLOCKER-1** | `HomeStatesTest.anEmptyDayHidesTheFabAndShowsTheSingleEmptyAction` (L78–85) still asserted the behaviour §4.5 Decision 2 reversed: `onNodeWithContentDescription("Quick Add").assertDoesNotExist()` and `onNodeWithText("Add an activity").assertIsDisplayed()`. | Renamed to `anEmptyDayKeepsTheFabAndDropsTheCompetingEmptyCta` and rewritten to the approved behaviour, in the file's established style: the `Quick Add` FAB **is** `assertIsDisplayed().assertHasClickAction()`, the empty card still shows "Nothing scheduled today", and "Add an activity" `assertDoesNotExist()`. The class KDoc was corrected to match (it described the old "empty day's single affordance"). |
| **MINOR-1** | `TodayScreen.kt` L3–4: duplicate `import androidx.compose.animation.AnimatedContent` left by this pass's dead-import cleanup. | One import deleted; the file now has exactly one. |
| **MINOR-2** | `QuickAddTest` was missing §8.15.4's FAB **placement** assertions (the code was correct but unguarded). | Added `the FAB is placed one section gap above the content area`, asserting `.align(Alignment.BottomEnd)`, `end = MomentumSpacing.screen`, `bottom = MomentumSpacing.section`, and that the retired `bottom = MomentumSpacing.compact` gap does not return. It scopes its substring to after `MomentumFab(`, so the `contentPadding` expression elsewhere in the file cannot satisfy it. |

A repo-wide sweep confirms **no** remaining stale assertion of the reversed
behavior: the only occurrences of `Add an activity` / `today_empty_action` /
`visible = !state.isEmpty` anywhere in `app/src` are the three new **negative**
assertions that guard against its return.

**Correction cycles used: 1 of 2** (AGENTS.md §11).

Re-validation after the fixes:

| Command | Result |
|---|---|
| `:app:testDebugUnitTest` (`--rerun-tasks`) | **PASS — 265 tests, 0 failures, 0 errors, 0 skipped**, actually executed. Count confirmed by parsing all 41 JUnit XML result files, not the console summary (264 → 265: the new placement test executed and passed; `QuickAddTest` now has 6 tests). |
| `:app:assembleDebug` | **PASS** — APK packaged. |
| `:app:assembleDebugAndroidTest` | **PASS — compiles and packages only.** |

`adb` remains unavailable: the rewritten `HomeStatesTest` is an **instrumented**
test and was **not executed**. Its compile is verified; its runtime behavior is
not claimed and remains part of the human's on-device verification.

---

## 10. REVIEW FINDINGS AND VALIDATION

<!-- REVIEWER OWNER -->

**Status: PASS (correction round closed — see §10.6).** The first pass returned
FAIL with 1 blocker and 2 minor; all three are corrected and re-verified.
`adb` is unavailable, so **nothing on device or on an emulator was executed**
(RISK-4 stands): the corrected instrumented test is **compiled and packaged
only**, its runtime behaviour is unverified here, and the 15 human checks
(§4.5) remain outstanding.

### 10.1 Findings (as first reported; resolutions in §10.6)

**BLOCKER-1 — the empty-day instrumented test still asserts the reversed
behaviour; it will fail on device.**

- **File:** `app/src/androidTest/java/com/momentum/app/presentation/screens/today/HomeStatesTest.kt`
- **Location:** lines 78–85, `anEmptyDayHidesTheFabAndShowsTheSingleEmptyAction`
- **Defect:** two assertions contradict §8.5/E3 and §8.4/E4 and are guaranteed
  to fail:
  - L82 `compose.onNodeWithContentDescription("Quick Add").assertDoesNotExist()`
    — the FAB is now composed unconditionally (`TodayScreen.kt:552`), so the
    node exists.
  - L84 `compose.onNodeWithText("Add an activity").assertIsDisplayed()` — the
    empty-state CTA was removed and `today_empty_action` deleted from
    `strings.xml`; the node cannot exist.
  - The test name itself ("hides the Fab") states the superseded TASK-015
    outcome. §8.10 listed `HomeStatesTest` as "no change required"; that line
    was written before the reversal and is superseded by §4.5 Decision 2. It is
  the only production behaviour in this task that a test contradicts.
  - `assembleDebugAndroidTest` passes because the assertions are literal
  strings, so compilation cannot catch it. Only running the suite exposes it —
  which is why this must be fixed before the human's device run, not after.
- **Required correction:** rename and rewrite that single test to the approved
  behaviour — on an empty day the "Quick Add" FAB **is** displayed and has a
  click action, and "Add an activity" **does not** exist — then re-run
  `:app:assembleDebugAndroidTest`. No other test in the file contradicts §8.
  (`HomeHeroStateTest` needed only the mechanical `onHeightChanged = {}`
  argument for the new signature — same category, no behaviour issue.)

**MINOR-1 — duplicate import introduced by this pass's dead-import cleanup.**

- **File:** `app/src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt`
- **Location:** lines 3–4
- **Defect:** `import androidx.compose.animation.AnimatedContent` appears twice.
- **Required correction:** delete one of the two lines.

**MINOR-2 — §8.15.4's runnable FAB-placement assertions are missing.**

- **File:** `app/src/test/java/com/momentum/app/presentation/screens/today/QuickAddTest.kt`
- **Location:** `Home always shows the FAB and the empty state carries no CTA`
- **Defect:** the assertion set covers the reserve and the unconditional
  composition but not §8.15.4's placement guard
  (`end = MomentumSpacing.screen`, `bottom = MomentumSpacing.section` at the
  FAB call site). The code is correct (`TodayScreen.kt:556–560`); it is
  simply unguarded, so a future change to `MomentumSpacing.compact` there would
  pass silently.
- **Required correction:** add two assertions on the code-stripped
  `TodayScreen.kt` pinning `align(Alignment.BottomEnd)` and
  `padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.section)`.

### 10.2 Flagged items — verdicts

**(a) `HomeSectionMotionTest`'s `if (reduceMotion) {` bound lowered 3 → 2 —
ACCEPTED. Legitimate consequence, coverage not meaningfully weakened.**

§8.7 and §8.5 require the FAB's `AnimatedVisibility` to be removed, and §8.7
states that removal is "strictly less motion; not a regression". The third
occurrence the assertion counted *was* that FAB branch, so the count could not
stay at 3 without failing a correct implementation. The assertion's subject is
untouched: both surviving branches (branch crossfade, Aurora intensity snap)
still *replace* their spec rather than shorten it, the bound remains a real
lower bound (it would fail at 1), and the same test independently pins
`count("if (reduceMotion) null else") == 3` plus `EnterTransition.None` /
`ExitTransition.None`. No action.

**(b) The 360 dp two-line "Today is complete" — ACCEPTABLE consequence of the
approved geometry; not a layout defect; no new decision required. It is a gap
in §8.14.4's illustrative table and it is a human device-check item.**

- **Why it wraps:** available width is `360 − 2×20 (screen) − 16 (HeroOrbGap)
  − 80 (HeroRingSize)` = **224 dp**; `headlineLarge` (Headline 28/34, SemiBold)
  sets "Today is complete" at roughly 250 dp.
- **No clip, no overflow:** the `Text` carries no `maxLines`/`overflow`, so it
  wraps and the figure `Column` becomes ~68 dp (2 × 34 line height) — still
  **less than the 80 dp orb**, and the `Row` is `CenterVertically`. The
  progress row therefore stays exactly 80 dp, `contentDp` is unchanged, and the
  Hero heights remain 208 / 216 / 272 / 312 / 368. The crown, the artwork
  window and every BOUND-1 margin (§10.4) are unaffected. At large font scale
  the figure column *can* exceed 80 dp, and then `contentDp` is **measured**,
  so the Hero grows — still no clip, which is what §8.8 requires.
- **Accessibility:** no impact. The progress `Row` carries
  `clearAndSetSemantics { contentDescription = description }`, so the statement
  is not announced as text; TalkBack hears exactly the merged
  `today_hero_progress_desc`. No heading is affected.
- **Contrast:** no impact. Both lines fall inside the swept "complete
  statement" band rectangle, which my independent recomputation puts at
  ≥10:1 in both themes (§10.3) — the statement sits lower in the compounded
  hand-off zone, which is the more favourable side.
- **Gap to record:** §8.14.4's table assumed `progressRow = 80 dp
  (= HeroRingSize)`; that assumption holds for the Hero height but not for the
  statement's own line count at 360 dp. The **model** (§8.14.4's own caveat:
  "the model is binding, the absolute totals are illustrative") is satisfied, so
  no Architect action is required. Add "complete state at 360 dp — confirm the
  two-line statement reads as intended" to the human's device pass.

### 10.3 Validation actually performed

| Check | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **PASS** — genuinely re-executed. 41 JUnit XML suites, **264 tests, 0 failures, 0 errors, 0 skipped** (results written 01:39:32, read back at 01:39:40; counts taken from the XML, not the console). |
| `:app:assembleDebug` | **PASS** — `app-debug.apk` present (24.5 MB, 01:29), UP-TO-DATE against the 01:39 recompile. |
| `:app:assembleDebugAndroidTest` | **PASS** — **compiled + packaged only**; `app-debug-androidTest.apk` present (01:29). No instrumented test executed. |
| §8.6 region-sample sweep — **recomputed independently** | **PASS.** Both bundled 1024×1536 WebPs decoded (WIC), then per sample: page background + the static `AuroraBackground` field (three radial blobs, all four `defaultIntensity` values) → cover-cropped artwork (`Alignment.BottomCenter`, width-governed) with the shared 88 dp `DstIn` bottom dissolve → the 4-stop page-background scrim; each copy band reduced to one mean colour and measured against `textPrimary`. **576 region samples** (2 themes × widths {360, 412} × intensities {0.35, 0.55, 0.75, 1.0} × heights {208, 216, 272, 312, 368} × bands {date, greeting, fraction, caption, complete, encouragement}), at two Aurora screen heights (800 and 672 — insensitive). **0 below 4.5:1.** Worst **Light 10.10:1**, worst **Dark 13.12:1**; the governing band is the **date line** in both themes, as expected now that the copy sits lower in the frame. My absolute values are higher than the Developer's 8.73 / 6.29 (different band rectangles and Aurora modelling), but both computations clear the ISSUE-020 floor by a wide margin, so the acceptance holds. |
| Peaks / scrim profile not moved | **Confirmed from source:** `ScrimPeakLight = 0.26f`, `ScrimPeakDark = 0.22f`, `ScrimPeakStart = 0.20f`, `ScrimPeakEnd = 0.80f`, 4-stop profile — all unchanged and inside `[0.15, 0.35]`. RISK-3 is **not realised**. |
| BOUND-1 / BOUND-2 | **Pass.** Window check `h − 88 ≥ HeroRingSize (80)`: 208→120, 216→128, 272→184, 312→224, 368→280. Crown check `96 ≥ 80` (margin 16 = `MomentumSpacing.standard`). The runnable assertions do enforce what they claim: `assertOneRingOfArtwork` computes `height − 88` and requires `≥ HeroRingSize − 1.dp` in empty / expanded / collapsed; `theCrownBandContainsNoNodeAtAll` builds the crown rect from `heroBounds.top → header.top` and requires no text, content-description or semantics node to overlap it; `HomeHeroAssetsTest` additionally requires no `Box(`/`Image`/`background`/`Surface`/`Card`/`LiquidGlass`/`HorizontalDivider` in the crown band and no `LiquidGlass`/`Card(`/`Surface(`/`.background(` between `HeroHeader(` and `HeroProgressBlock(`; `HomeAccessibilityTest` requires no `textSecondary` in comment-stripped Hero code. |
| Geometry / three-layer contract | **Pass.** `ArtworkCrown = HeroRingSize + MomentumSpacing.standard` = 96 dp (token-derived, no magic number); the crown is a genuinely empty `Spacer(Modifier.height(crown))` placed **above** `HeroHeader`; crown and box shrink 1:1 (`lerp(ArtworkCrown, 0.dp, p)` against `lerp(expandedHeight, compactHeight, p)`), so `artwork.bottom − block.bottom == HeroBottomInset` holds exactly at every `p`; `HeaderProgressGap = MomentumSpacing.section` = 24 dp constant; **no** `Spacer(Modifier.weight(1f))` anywhere in the Hero body; exactly three layers (artwork → scrim → copy) with no card/panel/`LiquidGlass`/divider/opaque surface behind any copy; the `MomentumHeroArtwork` call is unchanged (`Alignment.BottomCenter`, `fillMaxSize()`, shared dissolves, no zoom/parallax/`ColorFilter`); **no top fade added**; ADR-35 screen-space mask untouched. |
| §8.3 | **Pass.** `Modifier.weight(1f)` removed from the progress figure column; `Spacer(Modifier.width(HeroOrbGap))` with `HeroOrbGap = MomentumSpacing.standard`; merged `today_hero_progress_desc` via `clearAndSetSemantics` and `showPercent = false` intact; `MomentumFigureDisplay` retained for the fraction. |
| §8.4 | **Pass.** Measured geometry only — no `Spacer` constant forcing centring, no hard-coded screen percentage, `heightIn(min = emptyRegion)` (not `height`), `HomeEmptyState` takes no `onAdd`, `today_empty_action` deleted, and `EmptyState`'s CTA is optional by additive defaults only (`actionLabel: String? = null`, `onAction: (() -> Unit)? = null`, `TextButton` rendered only when both are non-null). The three other call sites (`HabitsScreen`, `ActivitiesScreen`, `InsightsScreen`) all pass both arguments by name → source- and render-compatible. |
| §8.5 / §8.15 FAB | **Pass.** `MomentumFab` composed unconditionally (no `AnimatedVisibility` at the call site); `align(Alignment.BottomEnd)`; `padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.section)`; one named `EmptyDayBottomReserve = MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section` = 96 dp used as **both** `contentPadding.bottom` and the centring region, so they cannot drift. |
| `MomentumFab.kt` — "strictly the two §8.15 edits" | **Pass.** The file is untracked, so no textual diff exists against a baseline; I verified it structurally against its TASK-015 origin (`HabitsScreen.AddHabitFab`, which TASK-015 replaced with the shared component). Every element is byte-for-byte the origin's: `CircleShape`, `size(56.dp)` via `MomentumFabSize`, the `Brush.verticalGradient` fill with the same three stops (0.22 / 0.95 / 0.92), `ambientColor = Color.Black.copy(alpha = 0.30f)`, `spotColor = c.primary.copy(alpha = 0.40f)`, `glassPress(interaction)`, `clickable(role = Role.Button)`, `MomentumIcons.add` at `MomentumIconSizes.prominent`, `contentDescription: String`. The only deltas are the two **sanctioned** §8.15 expressions (`elevation = MomentumGlass.heroElevation`, same 12 dp value; and the theme-resolved hairline `MomentumGlass.borderDark` / `borderLight`, replacing the hard-coded `Color.White.copy(alpha = 0.28f)`), TASK-015's own parameterisation (`MomentumFabSize`, `contentDescription`), and a comment recording the reason. **No fill/gradient/stop, shape, size, glyph, motion, semantics, API, component-architecture or new-token change.** The `HabitsScreen` call-site consequence is exactly §8.15.3. |
| Regression checks | **Pass.** 100% milestone: transient `AuroraState.MILESTONE` via `homeAuroraState`, `MomentumMotion.reward` (650 ms) window, one-shot ring glow (`glowEnabled = milestoneGlow && !reduceMotion`), exactly one `HapticFeedbackType.Confirm`, no snackbar (the `Celebrate` branch has no snackbar call). Encouragement line: state-specific, `AnimatedVisibility` with `EnterTransition.None`/`ExitTransition.None` under reduced motion and **no reserved space** when absent. Reduced motion preserved on every branch. Touch targets: FAB 56 dp, `LiquidGlassIconButton` 48 dp, completion control 48 dp. Heading semantics: exactly two `.semantics { heading() }`. Both themes exercised by every source-level assertion. Offline-only: `AndroidManifest.xml` declares only `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`, `SCHEDULE_EXACT_ALARM` — **no `INTERNET`**; **no Gradle/dependency change** (`git diff` over `*.gradle.kts`, `settings.gradle.kts`, `gradle/**` is empty). |
| Scope / working tree | **Pass.** TASK-016's footprint, established from file mtimes, is exactly the 5 authorised production files (`HomeHero.kt`, `TodayScreen.kt`, `MomentumBadges.kt`, `MomentumFab.kt` per Amendment B, `strings.xml`) + 6 test files + `DECISIONS.md` + `task.md`. `TodayViewModel.kt`, `theme/**`, `MomentumHeroArtwork.kt`, `ProgressOrb.kt`, `HabitsHero.kt`, navigation, domain, Room/DataStore and all Gradle files are untouched by this pass. TASK-015's uncommitted work and the unrelated `.opencode/**` / `opencode.json` edits are preserved; **nothing committed** (`HEAD` still `47ebfb4`, `git stash list` empty). |

### 10.4 Observations — recorded, not defects

1. **First-frame centring transient (empty day).** `heroHeightPx` starts at `0`, so the first measured frame gives the empty item `min = viewport − 96` and the correct `viewport − heroHeight − 96` arrives one frame later — a single-frame upward jump of roughly 100 dp at 360×800 when entering an empty day. This is inherent to the mechanism §8.4 prescribes (`onHeightChanged` + `heightIn`), the value converges idempotently, and nothing clips. The minimal in-place fix, if the human sees a flicker, is to report `currentHeight` from composition instead of from `onSizeChanged` (same single source of truth, no `Spacer`, no percentage). **Human device check; no change ordered.**
2. **12 dp centring offset.** §8.4's formula does not account for `Arrangement.spacedBy(MomentumSpacing.item)` between the Hero and the empty item, so the card is centred ~6 dp above the exact midpoint and the empty day's list is 12 dp scrollable. The implementation matches the approved formula verbatim and the offset is ~1 % of a ~496 dp region. Device check.
3. **Dark per-pixel minimum is 3.58:1** at 360 dp. §8.6 designates the per-pixel minimum as **informational only** (the region sample governs), and this is consistent with the `ScrimPeak*` KDoc's own note that a Dark per-pixel floor would need a peak near 0.53 — outside the approved envelope and against "readability without flattening". Not a defect; recorded so it is not later mistaken for one.
4. **§8.10's "no change required" list was inaccurate for two files**: `HomeStatesTest` actually had to change (BLOCKER-1) and `HomeHeroStateTest` needed one signature argument. §8.10's list was written before the reversal it governs.

### 10.5 Reviewer note on tooling

The `gradle-run` skill's wrapper could not be used: its prerequisite
(`python3` + `scripts/gradle_run.py`) is unavailable in this environment — only
the Microsoft Store alias exists and it exits with "Python was not found". Per
the explicit validation instruction, Gradle was therefore run directly
(`--console=plain`), and the unit-test result was confirmed from the JUnit XML
rather than the console summary. No full build log was surfaced.

### 10.6 Correction round — verification (Reviewer, 2026-10-01)

**Verdict: PASS.** All three reported defects are corrected, the corrections are
correctly scoped, and no production behaviour changed. Nothing else was
re-litigated.

**Scope of the round.** Exactly four files carry mtimes after the first pass:
`TodayScreen.kt`, `QuickAddTest.kt`, `HomeStatesTest.kt`, `task.md`. Every
other file in the tree — including `HomeHero.kt`, `MomentumBadges.kt`,
`MomentumFab.kt`, `strings.xml`, the ViewModel, `theme/**`, navigation,
domain/data and all Gradle files — is untouched since 01:03. `HEAD` is still
`47ebfb4` and `git stash list` is empty: **nothing committed**, TASK-015's
uncommitted work and the unrelated `.opencode/**` / `opencode.json` edits are
still preserved.

**BLOCKER-1 — resolved.**
`androidTest/.../HomeStatesTest.kt` L79–90 is now
`anEmptyDayKeepsTheFabAndDropsTheCompetingEmptyCta` and asserts the approved
§4.5 Decision 2 behaviour: `onNodeWithContentDescription("Quick Add")` →
`assertIsDisplayed().assertHasClickAction()`; "Nothing scheduled today"
displayed; `onNodeWithText("Add an activity").assertDoesNotExist()`. The test
name, the inline rationale and the class KDoc (L32–38) all describe the
current behaviour. The rest of the file is unchanged (L92–152 ≡ the original
L87–147, verified line for line). No stale assertion of the reversed behaviour
survives anywhere in `app/src`: the only two remaining occurrences of the
retired strings are the *negative* assertion in `HomeStatesTest.kt:89` and the
`strings.xml`-contains-`today_empty_action` **false** assertion in
`QuickAddTest.kt:143`. **Runtime behaviour is not verified** — `adb` is
unavailable; see the compile evidence below.

**MINOR-1 — resolved.** `TodayScreen.kt` is now 689 lines (was 690) with 89
import lines: exactly one `AnimatedContent` import remains, and a
case-sensitive duplicate-import scan of the whole file is clean. The file is
exactly one line shorter and everything from `package` onward is byte-shifted
by one, i.e. the duplicate import line was deleted and **nothing else in the
file changed** — no behavioural effect of any kind.

**MINOR-2 — resolved, and the scoping is real (not a false pass).** The new
`QuickAddTest.the FAB is placed one section gap above the content area` scopes
with `codeOf(today).substringAfter("MomentumFab(")`, then asserts
`.align(Alignment.BottomEnd)`, `end = MomentumSpacing.screen` and
`bottom = MomentumSpacing.section`, plus a negative guard on
`bottom = MomentumSpacing.compact`. Verified by character offsets in
`TodayScreen.kt`: the first `MomentumFab(` is the **call site** at offset
24896 (the `MomentumFab` / `MomentumFabSize` imports carry no `(`), and the
three asserted patterns occur **exactly once each**, at offsets 25059, 25333
and 25363 — all inside the FAB's own modifier chain. The `contentPadding`
expression (offset 27874) contains `MomentumSpacing.section` but reads
`bottom = EmptyDayBottomReserve`, so it **cannot** satisfy the padding
assertion; `bottom = MomentumSpacing.compact` occurs 0 times, so the negative
guard is live. The only residual looseness is that the substring runs to
end-of-file rather than to the call's closing paren — a future edit *after* the
FAB could in principle match. Accepted: uniqueness is what makes the assertion
meaningful today, and the code is correct.

**Validation re-run after the corrections.**

| Command | Result |
|---|---|
| `:app:testDebugUnitTest --rerun-tasks` | **PASS** — genuinely re-executed. JUnit XML read back (not the console): **41 suites, 265 tests, 0 failures, 0 errors, 0 skipped** (264 + the one new `QuickAddTest` placement test; results written 01:49:39, read at 01:49:50). `QuickAddTest` suite = 6 tests, 0 failures, including `the FAB is placed one section gap above the content area`. |
| `:app:assembleDebug` | **PASS** — `app-debug.apk` (01:45:59). |
| `:app:assembleDebugAndroidTest` | **PASS — compiled + packaged only.** `HomeStatesTest.class` recompiled at 01:45:59 (after the 01:44:53 source edit) and `app-debug-androidTest.apk` packaged at 01:46:00; both assemble tasks UP-TO-DATE against the 01:49 recompile. A string scan of the compiled class confirms it carries `anEmptyDayKeepsTheFabAndDropsTheCompetingEmptyCta` and **not** `anEmptyDayHidesTheFabAndShowsTheSingleEmptyAction`. **No instrumented test was executed; its runtime behaviour is unverified in this environment.** |

**Still outstanding (unchanged, not Reviewer-verifiable here):** the human's 15
mandated on-device checks (§4.5) and the runtime outcome of
`HomeStatesTest.anEmptyDayKeepsTheFabAndDropsTheCompetingEmptyCta`.

---

## 11. HANDOFF HISTORY

<!-- ORCHESTRATOR ONLY -->

- Orchestrator: TASK-016 established from the human brief + three device
  screenshots (empty/Dark, empty/Light-equivalent, one-activity/Dark). TASK-015
  closed; its full record archived to `task_archive/TASK-015.md` and its
  decisions remain canonical in `DECISIONS.md`. Scope limited to Home
  presentation corrections. Workflow: Architect → human approval gate →
  Developer → Reviewer. `adb` confirmed unavailable, so on-device verification
  remains with the human.
- Orchestrator → Architect: brief delivered (§12); required real-file root-cause
  analysis, append-only ADR amendments, mandatory human gate.
- Architect → Orchestrator: **COMPLETE — awaiting human approval.** One convergent
  root cause for E1/N1a/N2 (Hero height derived from copy, artwork derived from
  the container aspect ratio → ~112 dp empty Hero shows only the illustration's
  featureless bottom 21 %, then 78 % of that is dissolved). Plan: artwork **crown**
  above a top-anchored, scroll-invariant copy module (ADR-47 Amendment Note 2);
  `weight(1f)` removed + `HeroOrbGap` for one left axis; FAB always present and the
  empty-state CTA removed (ADR-48 Amendment Note 2); empty card centred by
  **measured** geometry (`heightIn(min = …)` + `Center`), no `Spacer` constants; FAB
  material deliberately unchanged; scroll transformation explicitly **rejected** for
  this pass; ISSUE-020 region-sample re-measurement made mandatory. Files for the
  Developer: 4 (`HomeHero.kt`, `TodayScreen.kt`, `MomentumBadges.kt` for the
  optional `EmptyState` CTA, `strings.xml`). No domain/schema/navigation change.
  Tests: 3 rewritten + `HomeHeroCompositionTest` updated; 4 explicitly unchanged.
- Orchestrator: **HALTED at the human approval gate** — §8.13 requires a human
  ruling on four points. No Developer handoff.
- Human → Orchestrator: **APPROVED with two amendments** (recorded verbatim in
  §4.5). Decision 1 approved as specified; Decision 2 approved; Decision 3
  approved as rejected/deferred; Decision 4 **amended** — a narrowly scoped FAB
  visual-integration adjustment using existing tokens is now authorized, closing
  RISK-1. New binding requirement: the **Hero layer contract** (artwork as
  background/atmosphere, copy foreground and dominant, continuous blend with no
  hard boundary, no opaque cover, and no re-fading the artwork). Crown escalation
  to 104 dp deferred to post-implementation device verification.
- Orchestrator → Architect: amendment brief delivered. §8 as approved stands
  **except** the Hero layering specification (new) and the FAB visual-integration
  adjustment (new). Implementation remains unauthorized until both are written and
  the amendment is reviewed.
- Architect → Orchestrator: **AMENDMENT COMPLETE.** Amendment A (§8.14,
  ADR-47 Amendment Note 3): the three-layer z-order is binding and **the crown is
  transparent space, not a layer** — an empty `Spacer`, so nothing can ever be
  drawn there; foreground dominance comes from type hierarchy + negative space,
  never an opaque cover; the existing shared 88 dp `bottomDissolve` + 4-stop scrim
  were **verified from the component** to already deliver a continuous merge with
  no hard boundary at 208/216/312 dp in both themes — **no artwork or scrim
  change, no top fade**; `ScrimPeakEnd = 0.80f` confirmed load-bearing. Two derived
  bounds added with runnable assertions: **BOUND-1** artwork must not be invisible
  (`h − 88 ≥ HeroRingSize`, crown `≥ HeroRingSize`), **BOUND-2** copy must not be
  washed out (scrim `[0.15, 0.35]` ceiling, ISSUE-020 `≥ 4.5:1` floor).
  Amendment B (§8.15, ADR-48 Amendment Note 3): ruled **not placement-only** — the
  Light-theme hairline is genuinely invisible (`Color.White.copy(alpha = 0.28f)`
  hard-coded at `MomentumFab.kt:67`, against `LiquidGlass.kt:110-112`'s own "white
  is invisible" note), so the fix adopts the theme-resolved
  `borderDark`/`borderLight` edge already used by `EmptyState`, plus
  `MomentumGlass.heroElevation` (same 12 dp value). Verified by the Orchestrator
  against the source. **Zero new tokens, values, API or visual language.** RISK-1
  closed by ruling.
- Orchestrator: **HALTED at the §8.15.3 gate.** `MomentumFab` is shared, so the
  Habits FAB hairline also changes (Light gains the ink edge, Dark 0.28 → 0.16).
  The Architect confirms this cannot be scoped to Home without forbidden changes.
  Human decision required; no Developer handoff.
- Human → Orchestrator: **FINAL RULING — APPROVED.** §8.15.3 accepted; crown 96 dp
  with 104 dp held as a device-verification-only escalation; scroll transformation
  stays rejected; explicit binding directive added to §4.5 that `MomentumFab.kt`
  may be changed **strictly** by the theme-resolved hairline + existing elevation
  correction, with no other "improvement" to that file.
- Orchestrator → Developer: implementation authorized with the approved plan, the
  §8.14/§8.15 constraints, and the `MomentumFab.kt` directive.
- Developer → Orchestrator: **IMPLEMENTATION COMPLETE.** All 14 specified changes
  implemented across 5 source files + 6 test files. `:app:testDebugUnitTest`
  **actually executed** with `--rerun-tasks` — 264 tests / 0 failures / 0 skipped
  (confirmed by reading the JUnit XML, not by the console summary);
  `:app:assembleDebug` PASS; `:app:assembleDebugAndroidTest` PASS (compile/package
  only). §8.6 region-sample sweep re-run over the widened set — **480 samples, 0
  below 4.5:1**; governing minima Light **8.73:1** / Dark **6.29:1**; peaks
  `0.26`/`0.22` unchanged. BOUND-1 (`h − 88` = 120/128/184/224/280, all ≥ 80) and
  BOUND-2 verified. `MomentumFab.kt` changed by exactly the two §8.15 expressions.
  Two items reported rather than silently absorbed: (i) `HomeSectionMotionTest`'s
  `if (reduceMotion) {` bound was lowered 3 → 2 because §8.5/§8.7 requires removing
  the FAB `AnimatedVisibility` that the third occurrence counted — §8.7 and the
  §8.10 "no change required" list were in tension; the behavioural decision was
  followed and the reason recorded inline; (ii) at 360 dp the `headlineLarge`
  "Today is complete" statement is wider than the 224 dp left after the orb gap
  and will wrap to two lines — no clip or overflow (the Hero grows), covered by the
  contrast sweep, but a device-verification item. One correction cycle used.
  **No instrumented or on-device verification claimed — `adb` unavailable.**
- Orchestrator → Reviewer: verification brief delivered (full §8 verification,
  recompute §8.6 independently, plus explicit verdicts on the two Developer-flagged
  items and on the §4.5 `MomentumFab.kt` directive).
- Reviewer → Orchestrator: **FAIL — 1 blocker + 2 minor defects.** **BLOCKER-1:**
  `HomeStatesTest.kt:78-85` `anEmptyDayHidesTheFabAndShowsTheSingleEmptyAction`
  still asserts the reversed behavior (`Quick Add` must not exist; `Add an
  activity` must be displayed) and would fail on device — `assembleDebugAndroidTest`
  cannot catch it because the strings are literals. §8.10's "no change required"
  entry for that file predates the reversal and is superseded by §4.5 Decision 2.
  **MINOR-1:** duplicate `AnimatedContent` import, `TodayScreen.kt:3-4`.
  **MINOR-2:** `QuickAddTest` lacks §8.15.4's two FAB placement assertions. Both
  flagged items **accepted**: the `HomeSectionMotionTest` 3→2 bound is a legitimate
  consequence of §8.5/§8.7 (both surviving branches still replace their spec, and
  the complementary `count("if (reduceMotion) null else") == 3` plus both
  `*Transition.None` checks are asserted independently); the 360 dp two-line
  "Today is complete" is an acceptable consequence of the approved geometry, not a
  defect — no clip/overflow, semantics merged to one description, contrast
  unaffected. Reviewer independently recomputed §8.6: **576 samples, 0 below
  4.5:1**, worst Light 10.10:1 / worst Dark 13.12:1, peaks unmoved.
  `:app:testDebugUnitTest --rerun-tasks` **264/0** (XML read back);
  `assembleDebug` + `assembleDebugAndroidTest` PASS. `MomentumFab.kt` verified
  structurally — only the two sanctioned expressions. Nothing committed.
- Orchestrator: routing the three concrete defects to the Developer for correction,
  then back to the Reviewer for verification of the corrections only. No restart,
  no reopened decisions.
- Developer → Orchestrator: **CORRECTIONS APPLIED.** BLOCKER-1: `HomeStatesTest`'s
  empty-day test renamed to `anEmptyDayKeepsTheFabAndDropsTheCompetingEmptyCta` and
  rewritten to the approved outcome (FAB displayed + click action; "Add an
  activity" absent), class KDoc corrected. MINOR-1: duplicate import removed.
  MINOR-2: `QuickAddTest` placement assertions added, scoped to the substring after
  `MomentumFab(` so the unrelated `contentPadding` expression cannot satisfy them.
  `:app:testDebugUnitTest --rerun-tasks` **265/0** (JUnit XML parsed);
  `assembleDebug` + `assembleDebugAndroidTest` green (compile/package only).
- Reviewer → Orchestrator: **PASS — correction round closed.** BLOCKER-1, MINOR-1 and
  MINOR-2 all verified resolved and correctly scoped; the MINOR-2 assertion proven
  not a false pass (first `MomentumFab(` is the call site at char 24896; the three
  placement tokens occur exactly once each, all inside its modifier chain). No stale
  reversed-behavior assertion anywhere in `app/src`. **No production behavior
  change** in the round — only the three test/import files carry new mtimes;
  `HomeHero.kt`, `MomentumBadges.kt`, `MomentumFab.kt`, `strings.xml`, the
  ViewModel, `theme/**`, navigation and all Gradle files untouched. Nothing
  committed (`HEAD 47ebfb4`, empty stash).
- Orchestrator: **TASK-016 code-level COMPLETE.** `PROJECT_STATUS.md` F-HOME updated
  (stays `PARTIALLY_VERIFIED`). Outstanding and Human-owned: the 15 mandated
  on-device checks, execution of the instrumented suites, and the 96 → 104 dp crown
  escalation decision.

---

## 12. ORCHESTRATOR BRIEF TO ARCHITECT

<!-- ORCHESTRATOR ONLY -->

**Objective.** Diagnose the root cause of each observed Home defect (E1–E4,
N1–N3) in the current implementation, then write an implementation-ready plan
that corrects the composition. This is a design/architecture task — do not
write application code.

**Required method.**
1. Open the real files in §6 and reason from the actual layout math, not from
   the screenshots' appearance alone. For **E1** in particular, establish exactly
   why the artwork does not render (Hero height vs. artwork window vs.
   `ContentScale.Crop` vs. the shared 88 dp `bottomDissolve` vs. the scrim
   stops) before specifying anything.
2. Separate *root cause* from *symptom* for every finding, and state which
   TASK-015 plan clause produced it.
3. Where a finding reverses an approved TASK-015 decision, record the reversal
   explicitly and amend the relevant ADR **append-only** (ADR-47 / ADR-48
   Amendment Note 2 or a new ADR) — do not rewrite existing entries.

**Required deliverables (into §8).**
1. Root-cause analysis per finding (E1–E4, N1–N3).
2. The corrected composition, specified concretely: Hero artwork window/height
   per state, artwork↔copy relationship, scrim (re-measure per ISSUE-020's
   region-sample method if it changes), Hero content geometry, the empty-state
   card's centering geometry, the progress + orb composition, and the FAB
   (presence in both states + material/treatment using existing tokens).
3. Empty-state card: final copy set after removing the CTA, and the exact
   `EmptyState` call shape.
4. Light/Dark requirements per element.
5. Motion and reduced-motion requirements for any new behavior.
6. Accessibility requirements (targets, semantics, reading order, contrast).
7. Implementation boundaries: exact files touched; justification if anything
   outside `presentation/screens/today/**` must change.
8. Required domain/state changes, or explicitly "none".
9. Tests: which existing Home tests must be updated and what new assertions
   are required (no new build tooling; bounds + semantics, not screenshots).
10. Acceptance criteria specific enough for the Developer to implement and the
    Reviewer to verify.
11. Explicit statement on the scroll-transformation question: recommended, with
    rationale — or rejected, with the static solution that makes it unnecessary.
    If recommended, it needs its own decision + ADR + human approval.

**Mandated skill use (substantive).** Load `using-chrisbanes-skills` to route,
then only the focused skills the work actually needs (expected:
`compose-component-design`, `android-kotlin-compose` for layout/geometry,
`compose-performance` if the Hero's read/allocation model changes,
`compose-ui-testing-patterns` for the test plan) plus the relevant Taste skills
(`design-taste-frontend`, `high-end-visual-design`, `minimalist-ui` — select
only what is relevant). Where a skill default conflicts with the locked design
system, the design system wins.

**Stop condition.** Return the STATUS handoff when §8 and the ADR
amendments/entries are written. Do not implement code — a human approval gate
follows, and it is **mandatory** here (the plan reverses approved decisions and
may propose a scroll transformation).
