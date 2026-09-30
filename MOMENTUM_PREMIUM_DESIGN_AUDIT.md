# Momentum — Premium Design Audit

**Phase 1 artifact. Read-only audit of the existing Momentum Android app.**

This document is a critique and a question set. It is **not** a design system, not a
style guide, and not a chosen visual direction. No application code, resource,
dependency, architecture or design was modified in producing it.

---

## How to read this document

**Provenance.** Emitted verbatim from the verified audit record held in `task.md`
§7 (TASK-007). The audit was produced by the Architect, independently verified by
the Reviewer/Tester (15 quantitative claims: 11 confirmed, 2 refuted, 2 partially
confirmed; 6 of 6 spec-vs-code contradictions confirmed), and then corrected through
two correction passes. The full correction log, including the original wording of
every corrected claim, is **Appendix F** — read it before relying on any number here.

**Observations vs. recommendations.** Findings and recommendations are kept
distinguishable by construction. Sub-sections titled `Recommendations` hold proposals;
everything else is an observation about what the code does. Claims traceable to the
working tree carry a `file:line` pointer. Claims that could not be determined from the
repository are marked `Unknown — not determinable from the repository` and are never
guessed.

**Sub-heading numbers and cross-references.** The `####` sub-heading numbers (for
example `#### 5.3`) are the audit's stable internal identifiers and are preserved
exactly as authored, so that the dense cross-references in the body remain resolvable.
References of the form `§7.N §M.n` point at sub-headings of the source record in
`task.md` §7; references of the form `CP-M`, `HO-M`, `Q-M` point at the numbered items
in sections 21, 22 and 23 below. The convention is defined once, authoritatively, in
**Appendix B**.

**What is deliberately not decided here.** Section 25 recommends a *process* for Step 2
and names no palette, no type stack and no aesthetic. Section 23 holds fourteen open
questions. The largest — whether the Light theme is a designed environment or an
inversion of Dark — is left open on purpose.

---

## Contents

1. Executive Summary
2. Current Product Understanding
3. Existing Design System Audit
4. Visual Identity Audit
5. Typography Audit
6. Color Audit
7. Surface & Depth Audit
8. Layout & Composition Audit
9. Component Audit
10. Habit Experience Audit
11. Tasks Audit
12. Insights Audit
13. Onboarding Audit
14. Navigation Audit
15. Motion Audit
16. Light/Dark Audit
17. Generic Pattern Audit
18. Existing Strengths
19. Radical Redesign Opportunities
20. Screen-by-Screen Verdict
21. Critical Problems
22. Highest-Impact Opportunities
23. Design Questions for Visual Exploration
24. What We Must NOT Change
25. Recommended Direction for Step 2

**Appendices** (source-record material that is not one of the 25 audit sections)

- Appendix A — Slice 1 method and evidence base
- Appendix B — Section map and cross-reference convention
- Appendix C — Slice 1: critical findings carried into Slice 2
- Appendix D — Out-of-slice observations
- Appendix E — Unknowns carried to Step 2
- Appendix F — Correction log

---

## 1. Executive Summary

> **F-6 — new section.** `R7`'s section order begins at "1. Executive Summary" and no sub-section was mapped to it. This is that section. It introduces **no claim not already established elsewhere in §7**, and per `R8` it names **no visual direction, palette or type stack** — the fourteen questions in §7.25 are what Step 2 is for, and settling any of them here would foreclose them.

**What Momentum is.** A premium, offline-first Android companion for one unified concept — the **Activity** — that the user completes over time, differentiated by category, recurrence and target rather than by entity type (`PRODUCT_SPEC.md` §2). Everything lives on the device: no account, no backend, no cloud, no network permission, no tracking. It answers two questions on Home (*"what do I need to do today, and how am I doing?"*), manages ongoing behaviours on Habits, and explains performance over time on Insights and each activity's detail. The data model and the domain engines are sound, pure-Kotlin and well tested; the persistence is honest and the accessibility intent is real.

**The single most important finding.** **The product cannot answer its own central question at a glance, and the cause is typographic rather than structural.** `Type.kt` collapses fifteen Material typography slots into seven sizes with no step between 28 and 20 or between 20 and 16, so the answer to *"how am I doing today?"* — the fraction `"3 of 5 completed"` in the Home Hero — is set in `titleMedium`, which resolves to **Body 16/24 Regular**. It is smaller than the greeting above it, the same size as a row's metadata line, and half the size of the one 40 sp figure in the product, which is Insights' *seven-day* rate. Streak counts render at 12 sp; a habit's live target progress ("5/8 glasses") at 12 sp; five call sites request a `FontWeight.Bold` that the app's `FontFamily` does not register — **though the bundled variable font does contain a 700 instance, so this is a registration gap rather than a missing capability (F-9b)**; `letterSpacing` and `fontFeatureSettings` occur **zero** times app-wide, so every changing number also reflows. The design document requires *"deliberate numeric hierarchy"* (§4) and the app's own type specimen states the intent that the code does not implement. **The emphasis in this product is applied to the least important numbers and withheld from the most important one** (CP-1).

**The top critical problems**, in the order they constrain the product (full statements in §7.23):

- **CP-1 · No numeric layer** — as above. The precondition for almost every other visual fix.
- **CP-2 · The day-completion moment is a snackbar** — the entire celebration for reaching 100% is one `LongPress` haptic plus a toast reading *"All done - momentum secured."*, while `AuroraState.MILESTONE` and `MomentumMotion.reward` exist, are documented, and are unreachable, and the Hero simultaneously displays *"Everything complete. Rest easy."*
- **CP-3 · The reduce-motion control is 13/14 inert** — Settings promises *"Calm animations throughout the app"*; the override is read at exactly one of fourteen call sites. A user who needs it is told it applied and it did not.
- **CP-4 · Light Aurora has no working depth mechanism** — its surface ramp collapses (`surface == surfaceElevated == #FFFFFF` on a `#F7F8FC` canvas), its shadows are black-alpha and therefore invisible, and a 1 px hairline does all the separation. The two "environments" are one design at two luminances.
- **CP-5 · Two progress visuals cannot tell a missed day from a rest day** — the Calendar and the Insights weekly bars both discard a `scheduled` count they already hold, while the one correct three-state mark in the product sits on Habit Detail.
- **CP-6 · The spec'd consistency metrics are computed and invisible** — `missedCount` and `consistencyScore` have zero presentation call sites; `Trend` is surfaced as a lowercased enum identifier, so a user can literally read *"Insufficient data"*.
- **CP-7 · The identity system is defined four times and reaches three of five surfaces** — and `Activity.colorArgb`, the product's own per-activity colour field, is persisted and read by nothing.
- **CP-8 · "Glass everywhere" is the shipped default** — the exact pattern the locked design document forbids as a foundation (§7, §33), applied across nine distinct surface fills against four tokenised, under a material name the app does not implement (`backdropBlur = 0.dp`).
- **CP-9 · Destructive confirmations are translucent** — all five dialogs use the shared `GlassLevel.HERO`, and the app's own `TargetEntryDialogSurfaceTest` asserts that this exact pattern is a defect.
- **CP-10 · The top-bar inset is unenforced on 5 of the 10 screens that carry one** — and three different inset strategies coexist, one of which reserves 240 dp of a possibly-640 dp viewport on Habit Detail.

**The shape of the problem, stated once.** This is not a template that looks generic. §7.3 counted **100 inline alpha literals** across 21 files, **9 surface fills** against 4 tokenised, **7 hardcoded shadows** against a dead elevation token, four date-formatting approaches, three progress-track tokens for one job, four icon-container treatments, six card constructions, two FABs of opposite material, two press-feedback languages, five dialogs with three dismiss verbs, and eight previews naming deleted themes. Momentum looks hand-made in twenty slightly different ways, and **none of the differences is a decision anyone made**. The genericity is a symptom of local re-decision against a design system that specifies — which is why §7.21's highest-leverage item is to invert that dependency, and why §7.24's list is mostly consolidation rather than invention.

**What must not change**, and what is explicitly *not* protected: §7.26 fixes the hard constraints (offline-only, on-device storage, minimal permissions, single module with manual DI, Clean Architecture layering with pure domain code, `java.time`, append-only completion history, the scheduled-opportunity streak rule, reduced motion, and the accessibility floors) and the genuinely locked ADRs. It equally states, in its own block, that the **type scale, the Light/Dark token values, the "Liquid Glass" default treatment, the ten dead tokens, the 771-line `HomeHero` machinery and the single-theme gallery are all open** — several of which the locked design document itself argues *against* keeping. **Nine named strengths survive and are justified individually in §7.20**, led by `CompletionButton`, the manage/act/understand division of labour, and `HabitDetailScreen.DayCell`.

**Screen severities** (§7.22, one of `KEEP` / `EVOLVE` / `REDESIGN` / `REIMAGINE`; none is `KEEP`):
- `REIMAGINE` — **Home / Today**, **Onboarding**
- `REDESIGN` — **Activity Detail**, **Tasks**, **Calendar**, **Create Item sheet**, **Theme Gallery**
- `EVOLVE` — **Habits**, **Habit Detail**, **Activities**, **Creation / Config**, **Settings**, **Reach-a-Target dialog**

**No visual direction has been chosen, and none is chosen here.** `R8` requires §23 to be open questions and §25 to be a *process* recommendation, and the Reviewer verified §7.27 satisfies that: it names no palette, no type stack and no aesthetic, and recommends a process (three directions plus a control, with stated constants, stated variables, required artefacts and required evidence). **The largest open question, and the one Step 2 should answer first, is whether Light Aurora is a designed environment or an inversion of Dark** (§7.25 Q1, §7.26 CP-4). It is left open deliberately. The second is what the product's name is pointing at (§7.25 Q2), given that it rejects fire-streak gamification and ships a fire icon for streaks.

**A closing caution, carried from §7.28.** Every visual judgement in this audit is a **static code-reading judgement, not a pixel observation** — the repository contains no screenshot, mockup, layout reference or Figma link, and the one design concept directory that does exist (`design/aurora-concept/`) is a web font pack that shows nothing about the app's appearance (blocker B2, narrowed by F-9a). `ISSUE-020` is the only measured contrast evidence in the tree. Step 2 should assume the same and should reconcile intent against implementation before it spends any pass on a direction: the product's own documentation disagrees with itself in at least six places (§7.3 §3.4), and a direction designed against the *documented* intent would inherit "glass everywhere" and a 12 sp streak.


---

## 2. Current Product Understanding

> Scope note for the assembler: this section is deliberately limited to what the **foundation** (token layer, identity assets, type, colour, surface, motion, theme) makes the product capable of expressing. Screen roles, flows and per-screen verdicts are Slice 2's §8–§14 and §17–§25.

#### What Momentum is, as a visual product

`docs/product/PRODUCT_SPEC.md` §1–§2 defines one unified **Activity** concept differentiated by category, recurrence and target rather than by entity type, and §10 states the success bar as cold start under 1 s, 60 fps scrolling, TalkBack-traversable flows, 4.5:1 body contrast and 48 dp targets. The repository implements that data model faithfully. What this audit has to answer is a narrower question: **what can the current visual foundation actually say to a user, and what does it currently say instead?**

The honest answer is that the foundation has strong *material* capability and almost no *expressive* capability. Concretely, the complete inventory of the product's identity-bearing assets is:

| Asset | Count | State |
|---|---|---|
| Bundled type family | 1 variable TTF (`res/font/manrope.ttf`, 164,700 bytes) | Used app-wide; see §7.5 for how thinly |
| Photographic identity assets | 6 WebP: `habits_hero_light`, `habits_hero_dark`, `onboarding_screen_01`–`04` | 4 of the 6 are onboarding-only and dark-only |
| Brand mark | 1 launcher vector (`ic_launcher_foreground.xml`) + 1 monochrome variant | Correct adaptive/themed-icon implementation; wrong palette (§7.4) |
| Custom iconography | **0** | Every glyph is Material Icons (`MomentumIcons.kt`, 22 references) |
| Illustration beyond photography | **0** | Every "image" is a gradient, a `Canvas` draw, or one of the six WebPs |
| Wordmark / in-app logo | **0** | The product name appears only as launcher label and as a `Text` in one preview |
| Second typeface | **0** | No display/serif/mono companion |
| Data-ink typeface | **0** | No tabular-figure setting, no dedicated numeric family |

That is the whole visual identity surface. It is not a criticism of ambition — it is the fact the rest of this audit has to be honest about: **Momentum currently has one typeface, one borrowed icon set, six photographs, one mark, and a hand-authored colour ramp.** Everything that makes it feel like Momentum rather than a Material 3 template is carried by Manrope's letterforms, the six photographs, and the word "Aurora".

#### The data the UI has available to visualise, and does not

This matters because it bounds what an identity *could* express. The repository contains at least three pieces of visual identity capability that exist in the data layer and are **read by zero presentation code** (verified by grep over `presentation/**`):

1. **Per-activity colour.** `Activity.colorArgb: Int?` (`domain/model/Activity.kt:120`, documented as *"Null = inherit category/theme accent"*), persisted on the Room entity (`data/local/Entities.kt:37,83`) and round-tripped by `Mappers.kt:28,51,78,87`. **No file under `presentation/` reads `colorArgb`.** The product's own data model promises per-Activity colour identity; the UI has never surfaced it. The only per-item colour that exists is derived from the *icon key* through a hardcoded lookup (`Color.kt:185-194`) and only on three surfaces (§7.4).
2. **A user-selectable theme.** `AppSettings.themeId` + DataStore key `theme_id` + a `ThemeGalleryScreen` + a Settings row. The registry holds **one** entry (`Color.kt:139-141`).
3. **A user reduce-motion override.** `AppSettings.reduceMotionOverride` + DataStore key `reduce_motion` + a Settings switch (`SettingsScreen.kt:169-170`). It reaches exactly **one** of the app's motion call sites (§7.8).

This is not "fake persistence" — it is the reverse: real capability with no visual expression. It is the single most under-used asset in the product.

#### Could someone understand their momentum at a glance?

**No — and the reason is a foundation-level typography fact, not a screen-layout fact.**

Every figure the product can display is set at the *same* type style, at the *same* size, at a *weight the bundled font does not register*:

- `ProgressOrb.kt:179` — the hero percentage: `typography.headlineMedium.copy(fontWeight = FontWeight.Bold)`.
- `MomentumBadges.kt:171` — `StatCard`'s value: `typography.headlineMedium.copy(fontWeight = FontWeight.Bold)`.
- `MomentumBadges.kt:85` — `StreakBadge`'s count: `typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)`, i.e. Caption 12/16.
- `InsightsScreen.kt:176`, `HabitDetailScreen.kt:352`, `CreationFields.kt:724` — also `FontWeight.Bold`.

`headlineMedium` is `TitleStyle` — 20sp / 26sp / SemiBold (`Type.kt:68-73, 108`). And that is the *same* style used for a screen top-bar title (`MomentumComponents.kt:75`), a dialog title (`MomentumComponents.kt:240`, `TargetEntryDialog.kt:109`) and a list-row title (`ActivityCard.kt:125`). So a section heading, a screen title, a dialog title, a habit's name, a statistic and the master progress percentage are all 20sp SemiBold. Meanwhile the streak count — the number the product is named after — is *smaller* (12sp) than the habit's name beside it (20sp).

`docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` §4 states: *"Numbers are first-class UI elements. Progress counts, streaks, targets, durations, percentages, and trends should receive deliberate numeric hierarchy."* There is no deliberate numeric hierarchy in the repository. `letterSpacing` occurs **0** times app-wide; `fontFeatureSettings`/`tabularFigures` occur **0** times. So numerals are proportional Manrope digits that change width as they change value — `62%` → `100%` re-centres inside the orb on every tick.

A better representation of "how am I doing today" would need to communicate, without reading: how far along the day is, what is left, and whether the streak is safe. §7.5 states what KIND of change that requires; §7.10 carries it into Slice 2.

#### What should survive (named, with justification)

These are named concepts/components/interactions, not generalities, and each is load-bearing today:

- **Manrope as the single family.** Bundled variable TTF, offline, pinned to three instances. It is a distinctive, geometric-humanist face with a recognisable `R` and `a`; it is the only typographic identity the product has, and it is correctly bundled per ADR-21. It survives; the *scale* built on it does not.
- **The `ActivityCard` completion affordance** (`CompletionButton.kt`). A 48 dp circular control that is quiet when idle and a solid accent disc when done, with a real `Role.Checkbox`, a spring pop and a haptic. It is the app's one genuinely well-behaved stateful control, and the `contentDescription` that names the *action* rather than the widget (`ActivityCard.kt:188-197`) is a real accessibility decision.
- **The Aurora *state model*** (`MomentumAurora.kt:32-41`). Four named states with a declared intensity floor/ceiling is a good abstraction, and `ProgressOrb`'s draw ordering (halo → lobes → arc, `ProgressOrb.kt:124-172`) is a correctly reasoned solution to "glow must never veil the value". The abstraction survives; its wiring does not (§7.4, §7.8).
- **`MomentumHeroArtwork`'s caller contract** (`MomentumHeroArtwork.kt`). Callers pass band amounts and cannot position the artwork; there is one artwork pair for the whole product; selection is a pure function of theme. That is a genuinely good, cheap-to-maintain art pipeline.
- **The adaptive launcher icon with a monochrome layer** (`mipmap-anydpi-v33/ic_launcher.xml`). Themed-icon support is correctly implemented and is rarer than it should be.
- **Reduced motion is implemented, not merely planned.** Nine composables honour it, and two of them (`ActivityCard.kt:74`, `CompletionButton.kt:65`) switch to `tween(0)` rather than shortening a duration, which is the correct behaviour. The *distribution* of that respect is broken (§7.8), not the idea.
- **The `ThemeGalleryScreen` shell.** Even with one entry, the idea that visual theme is a user-facing, discoverable choice is a product asset — and it is the natural home for whatever Step 2 produces.

---


---

## 3. Existing Design System Audit

#### 3.1 What the token layer actually is

`presentation/theme/` is nine files. Measured surface:

| Token group | Declared | Production call sites | Verdict from the count |
|---|---|---|---|
| `MomentumSpacing` v1 names (`micro`…`exceptional`) | 10 | 83 | In use |
| `MomentumSpacing` legacy aliases (`row`/`group`/`card`/`item`) | 4 | **121** | The alias layer is the *majority* path |
| `MomentumShapes` v1 (`small`…`pill`) | 6 | 17 | Minority path |
| `MomentumRadii` legacy alias | 5 | **59** | The alias layer is the *majority* path |
| `MomentumHeights` | 3 | 15 | In use |
| `MomentumGlass` | 11 | 22 | In use, but 15 further inline alphas sit in the same file |
| `MomentumMotion` | 9 | see §7.8 | Mostly dead |
| `MomentumElevation` | 4 | **0** | Dead (§7.7) |
| `MomentumIcons` | 22 | — | In use |
| `MomentumTypography` | 7 → 15 slots | — | In use, 2 literal overrides |
| `MomentumColors` | 24 | — | 4 tokens effectively dead |

`docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` §34 specifies the target structure with eight `MomentumComponents` sub-packages (Foundation / Interaction / Navigation / Progress / Habits / Tasks / Insights / Feedback). The repository has a flat `presentation/components/` with ten files. This is recorded divergence, not a defect — §34 describes a target, and the target's *governance intent* ("Screens should not directly invent colors, typography, radii, spacing, animation durations, icon families, arbitrary surface treatments") is the part that has measurably failed (§3.3).

#### 3.2 What the design system genuinely gets right (named)

- **One `CompositionLocal` for colour, one registry for themes.** `LocalMomentumColors` + `MomentumThemeDefinition` + `themeById` (`Color.kt:126-144`, `Theme.kt:12-17`). Screens cannot reach a raw hex without going through the token object; there is no `MaterialTheme.colorScheme` shortcut in the semantic layer.
- **The legacy-alias-with-delegation pattern.** `MomentumRadii.card = MomentumShapes.surface` (`Shapes.kt:46-50`), `MomentumDurations.medium = MomentumMotion.standard` (`Motion.kt:57-59`). Aliases delegate to v1 tokens rather than carrying their own values, so there is genuinely one source of values. This is a good migration design and it worked.
- **Zero `Color.Gray`.** Verified: **0 occurrences** anywhere under `presentation/`. The single most common AI-slop colour is absent.
- **Zero hardcoded colours in screens.** Verified: `Color(0x…)` literals in `presentation/` total **67**, and every one is inside the theme layer or the onboarding's self-declared palette: `Color.kt` 49, `Twilight.kt` 14, `OnboardingLiquidGlass.kt` 3, `OnboardingBackgrounds.kt` 1. **No file under `presentation/screens/` other than onboarding contains a raw hex.** This is the cleanest part of the foundation and it should be said plainly.
- **`contentDescription` policy is real.** Icon-only controls in the component layer pass a semantic string or `null` (`MomentumComponents.kt:69`, `MomentumBadges.kt:233`, `MomentumHeroArtwork.kt:116`). `role = Role.Checkbox/Button/Tab` is set on the controls that need it.
- **A 44-function preview suite** (`@Preview` count: 44 across 16 files, 10 of them in `DesignSystemPreviews.kt`), light + dark paired for surfaces, type, controls, shapes and Aurora states. For a design foundation that is unusually thorough.

#### 3.3 Where the token layer is bypassed

These are counts, and they are the substance of this section.

- **100 inline `copy(alpha = <literal>)` expressions** across **21** files. Concentrated where it is most costly: `LiquidGlass.kt` 15, `OnboardingLiquidGlass.kt` 14, `CreationFields.kt` 13, `MomentumBadges.kt` 7. The design system's own glass primitive contains **15** hand-written alphas next to the **3** it does tokenise (`MomentumGlass.borderDark`, `borderLight`, `highlight`).
- **51 `Color.White` / `Color.Black` references**, again concentrated in the design system: `OnboardingLiquidGlass.kt` 14, `LiquidGlass.kt` 10, `HomeHero.kt` 5, `CompletionButton.kt` 3.
- **3 corner radii that are not in the 8/12/16/20/28 vocabulary**: `RoundedCornerShape(26.dp)` and `RoundedCornerShape(18.dp)` in `LiquidGlass.kt:315,337` (the bottom nav bar and its tab), and `RoundedCornerShape(14.dp)` in `HabitConfigScreen.kt:396`. The doc (§6) says *"No random corner radii."* The bottom navigation bar — the most-seen component in the app — uses a radius that does not exist in the design system.
- **0 production uses of `MomentumElevation`** against **7 real `.shadow(` call sites** with hardcoded elevations: `LiquidGlass.kt:216` (10 dp), `:223` (8 dp), `CompletionButton.kt:101` (8 dp), `HabitsScreen.kt:238` (12 dp), `OnboardingLiquidGlass.kt:102,153,248`. The only two references to `MomentumElevation` in the entire repository are `DesignSystemPreviews.kt:279-280`, which *print* its values in a preview string.
- **Tokens defined and never used:** `progressTrack` (both themes, 0 call sites), `auroraMint` (both themes, 0 call sites), `surfaceInteractive` (previews only), `textDisabled` (0 call sites), `surfaceElevated` (1 production call site — `Theme.kt:73`, Light `onError`), `c.textMuted` (2 production call sites, both in `HabitDetailScreen.kt:542,550`).
- **A documented behaviour that does not exist.** `AppBackground.kt:33-38` builds its base with `Brush.verticalGradient(0f to c.backgroundTop, 1f to c.backgroundBottom)` under a KDoc promising *"a deep theme base with a whisper of vertical depth"*. Both `backgroundTop` and `backgroundBottom` are computed aliases of the same `background` value (`Color.kt:59-60`), so both gradient stops are identical and **the vertical depth gradient renders as a flat fill.** The comment describes a depth mechanism the code cannot produce.

#### 3.4 The locked design document vs. the shipped foundation

`MOMENTUM_DESIGN_SYSTEM_V1.md` is marked **Status: Locked** and closes with *"we will treat that as a design-system validation issue, discuss it, and deliberately amend the system rather than introducing one-off exceptions."* The divergences below are the ones that bear on the foundation:

| Doc clause | Shipped reality | Pointer |
|---|---|---|
| §7: *"Glass is a special effect, not the foundation… Not allowed as a default treatment for: every card, every row, navigation, entire screens, every button."* | Glass **is** the foundation. `LiquidGlass.kt` is the design system's core primitive; `LiquidGlassCard` is the default card, `ActivityCard` rows use it, `LiquidGlassBottomBar` navigation uses `GlassLevel.HERO`, and `LiquidGlassButton` is the primary button. | `LiquidGlass.kt:66-82, 158, 189, 311`; `ActivityCard.kt:82-85` |
| §33 rejects *"glass everywhere"* | Same as above. The app **named** the rejection and shipped it. | — |
| §24: *"Dialogs sit on an opaque Momentum surface."* | The design system's own `MomentumDialog` uses `GlassLevel.HERO` — 0.68 translucency. Only the bespoke `TargetEntryDialog` uses the opaque `GlassLevel.DIALOG`. | `MomentumComponents.kt:233-236` vs `TargetEntryDialog.kt:103` |
| §4: *"Numbers… should receive deliberate numeric hierarchy."* | No numeric hierarchy exists. `Bold` is requested at 5 sites; the family registers 400/500/600 only. Streaks render *smaller* than the titles beside them. | §7.5 |
| §28 motion tokens (fast 180 / standard 280 / emphasis 450 / reward 650) | `standard`, `emphasis`, `reward`, `easeInOut`, `springSnappy`, `springGentle` have **zero production call sites**. 8 hardcoded `tween(<int>)` and 8 raw `spring(stiffness=…)` instead. | §7.8 |
| §34: *"Screens should not directly invent… animation durations."* | Directly violated at 16 sites. | §7.8 |
| §5: *"Avoid arbitrary spacing values."* | 6/2/4/10 dp literals inside `MomentumComponents.kt`, `MomentumBadges.kt`, `LiquidGlass.kt`. | `MomentumComponents.kt:59,196,238,268`; `MomentumBadges.kt:73,75,130,167,227,236` |
| §8/§32: *"Material Symbols Rounded"* | The dependency is `androidx.compose.material:material-icons-extended` (`libs.versions.toml:27`) — the **Material Icons** set, not Material Symbols. The naming is inaccurate in three places. | `MomentumIcons.kt:48,98`; `HabitIconTile.kt:31` |
| §8 icon sizes 20/24/28 dp | Actual glyph sizes at call sites include 22 dp and 26 dp, plus a 22 dp `tile` token not in the doc. | `LiquidGlass.kt:304,383`; `MomentumBadges.kt:236`; `Tokens.kt:86` |
| §3 token contract list | The doc lists 18 tokens; the code implements 24. The doc's Light table omits `surfaceInteractive`, `borderSubtle`, `borderStrong`, `progressTrack`, `success`, `warning`, `danger`, `onAccent`. The code adds `surfaceSoft`, which the doc's list omits. | `Color.kt:15-50` vs doc lines 97-121 |
| §35: *"New screens can be assembled from existing Momentum components without inventing new visual patterns."* | Every screen audited in Slice 1's scope needed a new local alpha, radius, or shadow to look right. | §3.3 |

**Also load-bearing for Step 2:** `docs/architecture/decisions/DECISIONS.md` ADR-21 is the master visual decision and it is honest about the state it left behind. It records that `MomentumThemes` holds a single `"aurora"` entry, that the 14 pre-v1 slot names survive as computed aliases, and — explicitly — that *"Settings' Theme row shows one entry until the Settings phase removes/reworks the gallery."* That Settings phase never happened. The single-theme gallery is **recorded deferred debt, not an undiscovered defect**, and should be reported as such. ADR-27 similarly records that *"`HabitIconTile` + `habitIconAccent` are the one icon language for list/detail"* — a lock that only two of the five activity-icon surfaces honour (§7.4).

#### 3.5 The visual test suite is a source-text linter, not a visual test

`app/src/test/**/presentation/**` contains nine files. Their assertion mechanism is `File(...).readText().contains("…")` or `count("…")` against source text, plus WebP byte-header parsing. Examples:

- `MomentumHeroArtworkTest.kt:53-69` asserts the exact strings `"0f to Color.White"`, `"bottomStart to Color.White"`, `"coerceIn(0.10f, 0.95f)"` and that the source contains `to Color.Transparent` **exactly once**.
- `TargetEntryDialogSurfaceTest.kt:37` asserts the *screen's* source contains `"GlassLevel.DIALOG"` and does **not** contain `"GlassLevel.HERO"` — while the design system's shared `MomentumDialog` ships `GlassLevel.HERO`.
- `HomeSectionMotionTest.kt` asserts `count("Modifier.animateItem(") >= 2` and `source.contains("spring(stiffness = Spring.StiffnessMediumLow)")`.
- `QuickAddTest.kt` asserts `nav.contains("if (showCreateItem)")` and `code.contains("liquidGlass(CircleShape, GlassLevel.HERO)")`.

The only instrumented UI test, `app/src/androidTest/.../HomeHeroCompositionTest.kt`, says so in its own header: *"asserts bounds only, never pixels."* There is no `captureToImage`, no golden file, no Paparazzi/Roborazzi dependency in `libs.versions.toml`.

**Observation:** renaming a variable, reformatting a file, or changing an alpha constant will fail these tests without any visual change; conversely, a real visual regression — a colour that stops contrasting, a surface that stops separating from the canvas, a type style applied to the wrong element — passes them all. The suite provides *provenance* protection for specific past defects (which has value, and is why ISSUE-017/019 can be closed) and **zero regression protection for the design system as a whole.**

#### 3.6 Recommendations — design-system layer

*Recommendations are marked as such. These describe the KIND of change; they do not select a direction.*

1. **The alpha system needs a decision, not more discipline.** 100 inline alphas across 21 files is not a lint problem to be fixed by review — it is evidence that the token layer does not model what the UI actually varies (opacity-on-hue). *Kind of change:* either extend the token contract so that the recurring composites (a wash at a fixed alpha over a theme surface, a hairline at a fixed alpha over ink) become named tokens, or accept that alpha is a screen-level material decision and remove the pretense of centralising it. Either is defensible; the current half-state is not.
2. **Kill or wire the dead tokens.** `MomentumElevation` (0 call sites against 7 real shadows), `progressTrack`, `auroraMint`, `surfaceInteractive`, `textDisabled`. *Kind of change:* for elevation, a decision on whether shadows are a tokenised part of the surface system or an ad-hoc per-component flourish — then one or the other. Dead tokens mislead Step 2 into thinking capabilities exist.
3. **The legacy-alias layer is now the majority path** (121 spacing + 59 radii call sites). *Kind of change:* a migration that ends with `MomentumSpacing.group`/`MomentumRadii.card` deleted, so the v1 vocabulary is the only vocabulary. Not urgent, but Step 2 will otherwise design against a half-true token surface.
4. **"Liquid Glass" is a name the code does not implement.** `MomentumGlass.backdropBlur = 0.dp` with the documented rationale that minSdk 26 has no reliable cross-device blur API (`Tokens.kt:139-145`). What ships is translucency + a hairline + a soft shadow. *Kind of change:* a naming decision — either the component family is renamed to describe what it does, or the blur is adopted on the API levels that support it with a documented fallback. This is a Step 1 *terminology* trap: a reader of the codebase will assume a material that is not there.
5. **The design system's own previews do not preview the design system.** `AuroraPreviewShell` renders on a flat `c.background` box (`DesignSystemPreviews.kt:54-60`), so no preview shows the actual production surface stack (ambient Aurora + glass over a gradient canvas). *Kind of change:* give the preview shell the same `AppBackground` + glass stack the app uses, so the gallery is a real regression surface.
6. **Retire the eight previews that reference non-existent themes.** `themeId` values `"sunset"`, `"midnight"`, `"twilight"` (×5) and `"cosmic"` appear in previews at `MomentumBadges.kt:260,272`, `HabitConfigScreen.kt:629,649,673`, `TaskConfigScreen.kt:251,269`, `ThemeGalleryScreen.kt:165`. `MomentumThemes` contains only `"aurora"`, and `themeById` silently falls back (`Color.kt:143-144`). Those previews display Aurora while their names promise something else. This is a *verification* artefact: a future reviewer checking the theme gallery sees previews that cannot fail.
7. **Consider whether source-text tests should keep growing.** They are cheap and they did close real issues, but eight of them now guard implementation *text* in the design system's most-edited files. *Kind of change:* a stated policy for what kind of visual fact is worth a text assertion, and a plan for the small number of facts (glass opacity, type sizes, token values) that could be asserted against the *token objects* rather than the source.

---


---

## 4. Visual Identity Audit

#### 4.1 The brand mark uses a palette the app does not use

`res/drawable/ic_launcher_foreground.xml` — the single most visible identity artefact Momentum owns — draws in:

| Element | Colour in the mark | Nearest token in `AuroraLight` | Nearest token in `AuroraDark` |
|---|---|---|---|
| Progress ring (stroke) | `#A78BFA` | `auroraLavender` `#9278E8` | `auroraLavender` `#B59CFF` |
| Spark (fill) | `#5EEAD4` | `auroraMint` `#55C8AD` | `auroraMint` `#76DFC4` |
| Accent dot (fill) | `#FF8A7A` | `auroraPeach` `#EAA477` | `warning` `#FFC49B` |

The mark is a *fourth* palette: violet / mint / coral, in the Tailwind-400 register, none of which appear verbatim in either theme. The launcher canvas is `#0A0A1A` (`res/values/colors.xml:4`), and its own comment asserts it *"matches AuroraDark.backgroundBottom"* — which is `#080D18` (`Color.kt:103`). It is two steps off in red and blue, and the comment claims a match that is not exact.

**Why this is wrong:** the mark is where a user forms their expectation of the product's colour before the app opens. As shipped, the icon promises violet/mint/coral and the app delivers blue/lavender with a peach and a mint used only as ambient light. **User effect:** the first two seconds of the product are spent reconciling a mismatch. **What KIND of change would solve it:** a decision about which palette is canonical — the mark's or the theme's — and then making the other one derive from it. This is a Step 2 identity decision, not a bug fix; it is flagged because the two artefacts currently disagree and one of them is a hex-literal that nothing references.

A genuine, related strength: the monochrome variant (`ic_launcher_monochrome.xml`) and the API-33 adaptive-icon wiring are correct, so themed-icon support on Android 13+ already works.

#### 4.2 Zero custom iconography

`MomentumIcons.kt:107-132` is 22 references to `Icons.Rounded.*` / `Icons.AutoMirrored.Rounded.*`. The `momentumIcon(key)` mapper (`:74-95`) resolves 20 keys to Material glyphs. `HabitIconTile.kt:31` calls the family *"Material Symbols Rounded"*; the KDoc at `MomentumIcons.kt:98` says the same. The bundled dependency is `androidx.compose.material:material-icons-extended` (`libs.versions.toml:27`) — the **Material Icons** set. The naming is wrong in the docs and in three KDocs; the code is honest about what it imports.

> **CORRECTION (F-9c, Orchestrator-raised) — the relationship between the app and the design concept, established factually.**
> The design concept pack found by the Orchestrator (`design/aurora-concept/fonts.css:219-225`) declares a **second** icon technology alongside Manrope: a `Material Symbols Rounded` `@font-face` at `font-weight: 100 700`, backed by `fonts/f7.woff2` (5,399,668 bytes, verified `wOF2`/`flavor=00010000`), plus a `.material-symbols-rounded` utility class at `font-size: 24px` with `font-feature-settings: 'liga'`. The three positions are therefore:
>
> | | What it actually is | Where it says so |
> |---|---|---|
> | **The app's shipped code** | Material **Icons** via `androidx.compose.material:material-icons-extended` — 39 `Icons.Rounded.*` / `Icons.AutoMirrored.Rounded.*` imports in `MomentumIcons.kt` alone | `libs.versions.toml:27`; `MomentumIcons.kt:3-41` |
> | **The app's own documentation** | *"Material Symbols Rounded"* — three KDocs plus the design doc | `MomentumIcons.kt:48,98`; `HabitIconTile.kt:31`; `MOMENTUM_DESIGN_SYSTEM_V1.md:225,241,728`; `DECISIONS.md:326,346` (ADR-21) |
> | **The design concept pack** | Material **Symbols** Rounded, as a *webfont* with a variable weight axis | `design/aurora-concept/fonts.css:219-225`; `fonts/f7.woff2` |
>
> **The relationship, stated exactly:** the documentation and the concept pack **agree** on Material Symbols Rounded; the **shipped dependency disagrees with both**. So the discrepancy is not "the concept contradicts the design system" — it is that **ADR-21 recorded a decision the implementation never made.** ADR-21's own text says *"`MomentumIcons` establishes Material Symbols Rounded as the single icon family"*, and the same ADR is the source of the design-doc alignment, so the intent is unambiguous and consistently recorded. What exists in the app is a *different, related* Google icon set with a similar name.
>
> **Is this one decision or two competing ones?** One decision, not honoured. The two technologies are near-neighbours (both Google Material, both rounded), which is precisely why the substitution is easy to miss: a reader comparing "Material Symbols" to `Icons.Rounded` will not see a difference, and both are Google's Material lineage. **The practical consequence is not aesthetic but functional**: Material Icons is a static vector set with no weight axis, so the concept pack's `font-weight: 100 700` variable-weight idea — the ability to render the *same* glyph at different optical weights, which is the usual reason to prefer Symbols — **is not available to the app at all**, and cannot be had without changing the icon source.
>
> **This is a finding and a question, not a recommendation.** `R8` forbids settling it here, and whether Momentum should move to Material Symbols, stay on Material Icons, or adopt a custom family is exactly the kind of decision that belongs to Step 2. It is recorded as **§7.25 Q6** and is *not* pre-empted. What §7 can say is only the bounded factual part: the KDocs and the ADR name a family the build does not use, the concept pack anticipates the named family, and the variable-weight capability the concept assumes is absent from the app.
>
> **What remains Unknown:** whether `f7.woff2`'s glyph set actually corresponds to Material Symbols Rounded. The woff2 *container* is verified (`wOF2`, `flavor=00010000`, complete), and the family name comes from the CSS `@font-face` and the class name, but **the woff2 table directory is brotli-compressed and was not parsed** — so the font's own `name` table is `Unknown — not determinable from the repository` without a woff2 decoder. The Orchestrator's characterisation of `f7` as Material Symbols Rounded is therefore **consistent with the CSS but not independently verified from the binary**, and is marked as such. This does not affect any conclusion above: the app does not use it either way.

**Why this is wrong (as identity, not as a rule violation):** the icon set is the highest-frequency visual element in the product — a habit's identity *is* its icon — and it is entirely borrowed. There is no Momentum mark, no custom glyph language, no optical adjustment. **User effect:** every habit looks like a stock Android habit; nothing in the icon tells the user this product has a point of view. **What KIND of change would solve it:** a decision about whether habit identity is carried by a custom icon family, by a custom *treatment* of borrowed glyphs (a distinctive container, a bespoke accent grammar, a per-habit generated mark), or by abandoning icons as the identity carrier in favour of something else. Note the product already has one custom mark; the question is whether it is a family or a singleton.

#### 4.3 Three non-aligned key vocabularies

`momentumIcon()` renders 20 keys. `MomentumIconOptions` (`:52-71`) *offers* 18 to the user. `habitIconAccent()` (`Color.kt:185-194`) colours a *third* key set. Measured divergences:

| Key | Offered in picker | Rendered by `momentumIcon` | Coloured by `habitIconAccent` |
|---|---|---|---|
| `spark` | yes | falls through to `Icons.Rounded.Star` | falls through to `peach` |
| `art` | yes | `Icons.Rounded.Brush` | falls through to `peach` |
| `date` | **no** | `Icons.Rounded.DateRange` | `golden` |
| `streak` | **no** | `Icons.Rounded.LocalFireDepartment` | `amber` |
| `people` | yes | `Icons.Rounded.Person` (singular) | `plum` |
| `list` | yes | `Icons.AutoMirrored.Rounded.List` | `golden` |

Consequences that reach the user:
- The picker offers **Spark** and **Art**; the renderer can only draw **Star** for Spark. A user who picks "Spark" gets a star.
- `art` and `spark` both fall to `peach` in `habitIconAccent` — which is also the fallback for *every unknown key*. So "Art" is colour-indistinguishable from a custom habit with a corrupted key.
- `people` renders a single `Person` for a plural concept.
- "Streak" is offered by no picker yet is a first-class renderer key with a dedicated colour — meaning a habit-type the product considers important is unreachable by the user.

**Why this is wrong:** the three vocabularies are the contract that decides what a habit *looks like*, and they have drifted apart. Every future addition must be made in three places and will be made inconsistently. **User effect:** habit identity is partly arbitrary. **What KIND of change would solve it:** one authoritative key registry (key → glyph, label, colour family, semantic grouping) that the picker, the renderer, the tile and the detail hero all read — i.e. make habit identity a *data* concept rather than three `when` expressions.

#### 4.4 Per-activity colour identity exists on three surfaces out of five

`ADR-27` locks *"`HabitIconTile` + `habitIconAccent` are the one icon language for list/detail."* Measured reality of where an activity's icon appears:

| Surface | Colour source | Accent identity honoured? |
|---|---|---|
| Habits list (`HabitsScreen.kt:300` → `HabitIconTile`) | `habitIconAccent(iconKey)` | yes |
| Habit detail hero (`HabitDetailScreen.kt:322,568` → `HabitIconTile`) | `habitIconAccent(iconKey)` | yes |
| Creation cards (`HabitConfigScreen.kt:299-420`) | `templateAccent(templateId)` — a **parallel** map over template ids, not `habitIconAccent(iconKey)` | yes, but via a different mechanism — see §7.14 §10.5 |
| **Home rows** (`ActivityCard.kt:115-120`) | `tileTint` = `c.primary` (or `c.success` when done) | **no** — flat theme blue |
| **Tasks / Insights / Calendar / Activity detail** | same `ActivityCard`, or bespoke tiles | **no** |

**Why this is wrong:** the same activity is a coloured jewel in three places and a generic blue square in the others. The user cannot learn "purple means meditation" because the association breaks every time they leave the Habits tab. **User effect:** per-habit colour is decoration that does not accumulate meaning; the identity system is invisible. **What KIND of change would solve it:** decide whether per-activity colour is a *system* (then it must be honoured on every surface that shows an activity, and `Activity.colorArgb` should be exposed) or a *local accent* (then it should be removed from the three surfaces that use it, because a partial system is worse than none). Shipping more of it without deciding is the current failure.

> **CORRECTION (F-5, Reviewer-verified MINOR).** The creation-cards row previously read `habitIconAccent(iconKey)`, which contradicted §7.14 §10.5 and was factually wrong. `habitIconAccent`'s only call sites are `HabitIconTile.kt:45`, `HabitDetailScreen.kt:335` and `:343`; `HabitConfigScreen.kt` never calls it — it uses `HabitIconAccents.peach`/`.plum` directly (`:299`, `:310`) and the parallel `templateAccent(templateId)` map (`:411-420`). **The "3 of 5 surfaces" outcome stands**, but the accurate statement is: per-activity accent identity reaches **3 of 5** surfaces, via `habitIconAccent` on **two** (Habits list, Habit Detail hero) and `templateAccent` on **one** (creation cards). §7.10 item 5, §7.19(d) and §7.25 Q5 have been aligned to that wording.

#### 4.5 The Aurora identity is one accent wide

`AuroraLight`/`AuroraDark` declare five accent hues each. Measured production usage of each:

| Token | Light | Dark | Production call sites |
|---|---|---|---|
| `auroraBlue` | `#5C9FEF` | `#78B7FF` | 1 (`MomentumAurora.kt:80`) — but also aliased as `accentPrimary`, `primary`, `progressFill`, `glow`, 54 `c.primary` reads |
| `auroraLavender` | `#9278E8` | `#B59CFF` | 1 (`MomentumAurora.kt:81`); also `accentSecondary`/`c.secondary`, 4 reads |
| `auroraPeach` | `#EAA477` | `#FFC49B` | 1 (`MomentumAurora.kt:82`) |
| `auroraViolet` | `#8064DD` | `#9B7CFF` | 0 direct; reachable only via `accentTertiary`/`c.accent` — 3 reads (`AppBackground.kt:73`, `MomentumBadges.kt:81`, M3 `tertiary`) |
| `auroraMint` | `#55C8AD` | `#76DFC4` | **0** |

So four of the five declared accents are ambient-only or vestigial. Effectively the entire product is rendered in **one** accent hue (blue), with lavender as a secondary, violet as a rare tertiary, and peach/mint invisible.

Compounding this: the "state-responsive Aurora" is wired to **three** screens, and **two of them are pinned to its lowest intensity**:

- `HabitsScreen.kt:149` → `AuroraState.REST` (0.35)
- `HabitDetailScreen.kt:161` → `AuroraState.REST` (0.35)
- `TodayScreen.kt:161-164` → REST / PROGRESS / COMPLETION by progress
- **`AuroraState.MILESTONE` (intensity 1.0) is unreachable in production** — it appears only in `DesignSystemPreviews.kt:304`.

At REST in Dark, `baseAlpha = 0.20 × 0.35 = 0.07`. The two screens that use the Aurora "state-responsive" system are therefore, by construction, running it at its least visible.

Meanwhile the app-wide background, `AppBackground` (`MomentumNavGraph.kt:133`), has **no state parameter at all**. It is three static radial fields at 0.13 (Dark) / 0.16 (Light). The doc's §27 — *"Aurora is state-responsive, not decorative wallpaper"* — is true of a component that three screens use and false of the background the whole app sits on.

#### 4.6 Four colour vocabularies coexist

| Vocabulary | Size | Where it lives | Theme-aware? |
|---|---|---|---|
| Aurora Light/Dark | 24 tokens × 2 | `Color.kt` | yes — it *is* the theme |
| Twilight Journey | 14 constants | `Twilight.kt` | **no** — used only by onboarding |
| `PhotoShade` | 1 constant `#060B18` | `OnboardingBackgrounds.kt:65` | no |
| `HabitIconAccents` | 7 constants | `Color.kt:156-177` | **no** — one set for both themes |

`HabitIconAccents` is the sharpest of these: a single fixed set of seven jewel hues is used identically in Light and Dark. The doc's §3 principle is *"Light and Dark Aurora are two environments of the same design language"* — but habit identity colour does not have two environments; it has one, and it was never designed for either.

And `OnboardingLiquidGlass.kt` adds three more literals on top of Twilight: `#BFE3FF`, `#0A1B2E` (twice). So onboarding is built from **three** uncoordinated sources.

#### 4.7 "Familiar because it is Android" vs. "generic because it is unowned"

Named patterns found in the foundation, with the distinction the brief asks for:

| Pattern | Verdict | Why |
|---|---|---|
| Material ripple on `CompletionButton` (`CompletionButton.kt:113`) and default `clickable` indication on `ActivityCard` (`:86`) while every other control uses `indication = null` + `glassPress` scale | **Familiar but unowned** | Android users expect a ripple; Momentum uses it in two of its most-touched components and a bespoke press-scale everywhere else, so the app contradicts its own interaction language in the two places it matters most |
| Circular 48 dp completion disc with a check | **Familiar and correct** | Named, justified in §7.2 |
| Four-tab bottom bar with icon + label | **Familiar (Android) and deliberate** | ADR-22 locks the four destinations; the glass treatment over it is a real differentiation |
| Rounded glass cards at 16 dp on a 4 dp grid | **Generic because unowned** | The 16 dp card + hairline + translucent fill is the default 2024 Android app surface. Nothing in the card's construction says Momentum. |
| Circular glass icon buttons (`LiquidGlass.kt:311, 290`), 8 dp rounded-square accent tiles (`HabitIconTile.kt:46`, `ActivityCard.kt:94`), 8 dp rounded-square primary tiles (`MomentumBadges.kt:228`) | **Generic because unowned** | Three container treatments for the same idea (an icon on a surface) with three different radii, three different fills and three different sizes (48 / 44 / 52 dp). No rule connects them. |
| `Icons.Rounded.Star` as the universal fallback for an unknown icon key (`MomentumIcons.kt:94`) | **Generic because unowned** | A habit with a corrupted key is silently indistinguishable from "Spark" |
| Decorative-but-unowned gradients | **Present** | The ambient `AppBackground` fields are 0.13-alpha, unreactive, and stack on top of a second ambient system on three screens (§7.7) |
| Stat tiles without meaning | **Present** | `StatCard` renders `value` + `label` + optional `sublabel` with no relationship encoded between them, at the same 20sp as every heading (§7.5) |

#### 4.8 Recommendations — visual identity

1. **Decide what carries identity: the mark, the accent, the artwork, the type, or the icon treatment.** Right now all five are candidates and none dominates. *Kind of change:* a written position on which one or two are load-bearing, so Step 2 has something to be *differentiated from* rather than a set of unrelated artefacts to re-pick. This is the single most useful thing Step 2 can inherit.
2. **Reconcile the mark's palette with the theme's**, in whichever direction is chosen. The launcher icon is 20 lines of vector; nothing references its hexes.
3. **Collapse the three key vocabularies into one registry.** Not a refactor for its own sake — it is the precondition for per-activity colour to mean anything.
4. **Decide whether per-activity colour is a system or a local accent**, then apply it everywhere or nowhere (§4.4). And if it is a system, `Activity.colorArgb` — already in the schema, already mapped, never read — is the obvious hook.
5. **Give `HabitIconAccents` two environments, or stop using it as if it were a theme token.** Seven fixed hues in a theme that has two is a hole in the model.
6. **Wire `AuroraState.MILESTONE` or delete it.** A four-state system whose strongest state is unreachable is a diagram, not a system. If milestones are a product moment (the doc says they are, §21), the state exists for them; if not, the enum is a lie that Step 2 will build on.
7. **If the state-responsive Aurora is a real product idea, make the app-wide background honour it.** Right now the ambient field under every screen is static, and the reactive one is at 0.35 alpha on two of its three screens.

---


---

## 5. Typography Audit

#### 5.1 The font: bundled Manrope, offline, three pinned weights

Verified in code and resources, not assumed:

- `app/src/main/res/font/manrope.ttf` exists — a **single** 164,700-byte TTF. It is the only file in `res/font/`. There is **no** `res/font/*.xml` family resource, so no `FontFamily` XML and no platform-downloadable font provider.
- `Type.kt:36-52` builds `FontFamily` from **three** `Font()` entries, all pointing at the same `R.font.manrope`, distinguished by `variationSettings = FontVariation.Settings(FontVariation.weight(N))` at **400, 500, 600** only.
- ADR-21 records the provenance: a variable TTF from `google/fonts`, OFL-1.1, "fetched at build-authoring time and committed" — fully offline, no remote loading. This satisfies AGENTS §2 and §11.
- ~~**Unknown — not determinable from the repository:** the font binary's actual axis range and glyph coverage. I did not parse the TTF.~~ **RESOLVED (F-9b) — the TTF was parsed directly. See below.**

> **CORRECTION (F-9b, Orchestrator-raised) — the bundled font binary, verified.**
> `app/src/main/res/font/manrope.ttf` (164,700 bytes) was read byte-for-byte. `sfntVersion = 0x00010000` (TrueType outlines; no `CFF2` table). The table directory holds **19** tables, including **`fvar`**, **`gvar`**, **`STAT`** and **`HVAR`** — the four-table signature of a **variable font**. `fvar` v1.0 declares **`axisCount = 1`**, and the single axis record is:
>
> | Axis | Tag | Min | Default | Max | Axis name (nameID 256) |
> |---|---|---|---|---|---|
> | 1 | **`wght`** | **200** | **200** | **800** | "Weight" |
>
> `fvar` also declares **`instanceCount = 7`** (instanceSize 10 = 4 + 2×1 axis), and the seven named instances sit at stored coordinates **200, 300, 400, 500, 600, 700, 800**, confirmed independently by the `name` table:
>
> | Instance | `wght` | Subfamily (nameID) | PostScript name (nameID) |
> |---|---|---|---|
> | 0 | 200 | 257 `ExtraLight` | 258 `Manrope-ExtraLight` |
> | 1 | 300 | 259 `Light` | 260 `Manrope-Light` |
> | 2 | 400 | 261 `Regular` | 262 `Manrope-Regular` |
> | 3 | 500 | 263 `Medium` | 264 `Manrope-Medium` |
> | 4 | 600 | 265 `SemiBold` | 266 `Manrope-SemiBold` |
> | **5** | **700** | **267 `Bold`** | **268 `Manrope-Bold`** |
> | 6 | 800 | 269 `ExtraBold` | 270 `Manrope-ExtraBold` |
>
> The `name` table also gives `nameID 16` (typographic family) = **"Manrope"**, `nameID 17` (typographic subfamily) = **"ExtraLight"**, and `nameID 5` (version) = **"Version 4.505"**. `OS/2.usWeightClass = 200` and `head.macStyle` bold bit = 0, both consistent with a default instance of 200.
>
> **Two consequences, and they matter in opposite directions.**
> **(1) CP-1's cheapest fix is available, fully offline.** The bundled file **contains a 700 instance**, so a fourth `Font()` entry pinned with `FontVariation.Settings(FontVariation.weight(700))` — or simply dropping the `variationSettings` pin so the family follows the requested weight — would make `FontWeight.Bold` render at real 700 with **no new asset, no new dependency, no network fetch and no change to AGENTS §2 or §11**. The five call sites requesting an unregistered `Bold` (§7.5 §7.5.3) could therefore be made correct by a change to `Type.kt` alone. **The Unknown that gated this is closed.**
> **(2) The defect is a *registration* gap, not a *capability* gap — and the audit previously blurred that.** §7.5 §7.5.3 says the family "does not provide" 700. It does; the family *exposes* 3 of 7 named instances. That is a smaller and more fixable problem than a missing font, and the wording is corrected there.
>
> **What the font binary still does not tell us** (retained as `Unknown`, honestly): **glyph coverage.** The `cmap` table is present (1,772 bytes) but the character-to-glyph mapping was not enumerated, so which scripts and which typographic features the shipped subset covers is `Unknown — not determinable from the repository` *without a font-mapping tool*. This matters only to a localisation question, which is out of scope for this audit; it does not gate anything in §7.23.
>
> **The concept pack implies the same thing, stated separately and not used as evidence.** `design/aurora-concept/fonts.css` declares `@font-face` for family `Manrope` at weights **400, 500, 600 and 700**, with **all four weights mapped to the same six subset files** (`f1`–`f6` by unicode-range) — a single file serving four declared weights, which is the web-side signature of a variable font. **That is a corroborating observation about a web pack, not the basis for the answer above**; the Android conclusion rests entirely on the TTF parse. The CSS adds one thing the TTF does not: it declares 700 as an *intended* weight, and it declares **no** 800 — consistent with the design doc §141 (*"Avoid 800/900 weights"*), which is a rule about *usage* and not about the font's capability, since the file carries an 800 instance.

**So: Momentum does not rely on the platform default. Roboto is not in the picture.** That is a genuine, deliberate identity choice and it is correctly executed.

#### 5.2 The type scale as implemented

Seven real styles, mapped onto fifteen Material slots. `Type.kt:54-119`. **No `letterSpacing` is set on any of them** (and `letterSpacing` occurs 0 times in the whole app), and no `fontFeatureSettings`.

| Design token | Size | Line height | Weight | Material slots it fills | `letterSpacing` | Real `FontWeight` registered? |
|---|---|---|---|---|---|---|
| Display | 40sp | 48sp | 600 | `displayLarge`, `displayMedium` | 0 | yes |
| Headline | 28sp | 34sp | 600 | `displaySmall`, `headlineLarge` | 0 | yes |
| Title | 20sp | 26sp | 600 | `headlineMedium`, `headlineSmall`, `titleLarge` | 0 | yes |
| Body | 16sp | 24sp | 400 | `titleMedium`, `bodyLarge` | 0 | yes |
| Body Small | 14sp | 20sp | 400 | `titleSmall`, `bodyMedium` | 0 | yes |
| Label | 13sp | 18sp | 500 | `labelLarge` | 0 | yes |
| Caption | 12sp | 16sp | 500 | `bodySmall`, `labelMedium`, `labelSmall` | 0 | yes |

**Observation — the scale has a hole in the middle.** Between 28sp (Headline) and 20sp (Title) there is nothing. Between 20sp and 16sp there is nothing. A screen title (20sp) and a section heading (16sp) are the only steps available to most of the product, so the hierarchy is forced to be shallow. Fifteen Material slots collapse to seven sizes, and four slots are aliased to the *same* instance in ways that mislead: `titleMedium` is **Body 16/24 Regular**, `bodySmall` is **Caption 12/16 Medium**, `labelMedium` and `labelSmall` are both Caption. A screen author reaching for `MaterialTheme.typography.titleMedium` gets body text; one reaching for `bodySmall` gets a 12sp medium-weight label.

Only **2** literal `fontSize` overrides exist in the entire presentation layer (`HabitConfigScreen.kt:361` = 16.sp, `OnboardingScreen.kt:221` = 17.sp). The scale *is* respected at the size level; it is the *hierarchy* that is broken, not the discipline.

#### 5.3 Numbers: the layer that does not exist

This is the sharpest typography finding in the audit, and it has three independent parts.

**(a) The registered family has no Bold, and the number layer asks for Bold.**
`FontWeight.Bold` is requested at **5** production sites, and 700 is not one of the three registered instances:

| Site | What it styles |
|---|---|
| `ProgressOrb.kt:179` | **the hero progress percentage** — `headlineMedium.copy(fontWeight = FontWeight.Bold)` |
| `MomentumBadges.kt:171` | **`StatCard`'s value** — identical |
| `InsightsScreen.kt:176` | an Insights figure |
| `HabitDetailScreen.kt:352` | a habit figure |
| `CreationFields.kt:724` | a creation field value |

The weight requested for the product's most important numbers is one the **`FontFamily` does not expose**, even though the **bundled file contains it**. Compose will resolve the request to the nearest *registered* instance (600) and then apply synthetic emboldening, because the three `Font()` entries pin `variationSettings` to a fixed `wght` and so cannot be re-instanced. **What the user actually sees at those five sites is an indeterminate weight.** I cannot report the rendered result — no rasterisation was performed — but the *request* is provably outside the registered family, and that is the finding.

> **CORRECTION (F-9b).** The original sentence said the family *"does not provide"* 700. That was wrong in a way that **overstated** the defect. The TTF parse (§7.5 §7.5.1) shows the file is a variable font with `wght` 200–800 and **seven** named instances including a real 700 (`nameID 267` = "Bold", `268` = "Manrope-Bold"). **700 is present in the file and simply not registered in `Type.kt`.** So the correct diagnosis is a **registration gap, not a missing font** — which makes it *more* fixable, not less: one `Font()` entry, or the removal of the `variationSettings` pins, fixes all five sites with no new asset and no network. The severity of the *user-visible* outcome is unchanged; the cost of fixing it is now known.

**(b) Numerals are proportional, not tabular.**
`fontFeatureSettings` / `tabularFigures` occur **0** times. Manrope's default figures are proportional. Every figure in the app therefore changes width as its value changes: the orb's `62%` → `100%` re-centres inside a 96 dp circle on every tick; a streak badge's `9` → `10` reflows; a `StatCard`'s value shifts its neighbours. In an app whose whole premise is watching numbers change, the numbers are the last place where layout stability was considered.

**(c) The size hierarchy actively inverts importance.**
Measured, in the same visual neighbourhood:

| Element | Style | Effective size | Weight |
|---|---|---|---|
| Screen top-bar title (`MomentumComponents.kt:75`) | `headlineMedium` | 20sp | 600 |
| Dialog title (`MomentumComponents.kt:240`) | `titleLarge` | 20sp | 600 |
| List-row title — the habit's name (`ActivityCard.kt:125`) | `titleLarge` | 20sp | 600 |
| **`StatCard` value** (`MomentumBadges.kt:171`) | `headlineMedium` | **20sp** | requested 700 |
| **Hero progress percentage** (`ProgressOrb.kt:179`) | `headlineMedium` | **20sp** | requested 700 |
| Section heading (`MomentumComponents.kt:105`) | `titleMedium` | **16sp** | **400** |
| Settings row title (`MomentumComponents.kt:199`) | `titleMedium` | **16sp** | **400** |
| Supporting metadata (`ActivityCard.kt:133`) | `bodySmall` | **12sp** | **500** |
| **Streak count** (`MomentumBadges.kt:85`) | `labelMedium` | **12sp** | **600** |

Five distinct semantic levels — screen title, dialog title, entity name, statistic, master progress figure — are **one** style. And a section heading (16sp Regular) is typographically identical to a settings row title (16sp Regular). Meanwhile the **streak count, the number the product is named after, is 12sp** — half the size of the habit name printed next to it, and the same size as the metadata line beneath it.

**(d) The design system states the opposite, in its own preview.**
`DesignSystemPreviews.kt:145` renders the sample string *"Body 16/24 — progress, streaks, and targets read first."* at `bodyLarge`. And `:149` renders *"21 day streak · 3 / 5 · 82%"* at `headlineMedium` — 20sp Title — the same style as everything else. So the design system's own specimen page (i) claims figures read at body size while the code sets them at title size, and (ii) demonstrates the collapse it is supposed to prevent.

The doc's §4 is unambiguous: *"Numbers are first-class UI elements… should receive deliberate numeric hierarchy."* The implementation has the opposite of deliberate: it is the accidental consequence of folding fifteen Material slots into seven sizes.

#### 5.4 Where personality is weak, and where it is not

**Weak:**
- **Zero tracking control.** No `letterSpacing` anywhere. This is not neutral: at 12sp/16sp (Caption — used for every metadata line and the streak badge) zero tracking is below the legibility comfort threshold where positive tracking is conventional, and at 40sp/48sp (Display) the absence of negative tracking leaves the headline slightly loose. A system that has a variable font available and uses none of its width axis is leaving its most distinctive asset on the table.
- **No optical-size or width variation.** The `wght` axis is used; nothing else is — and the verified parse (F-9b) confirms the file's **only** axis is `wght` (200–800), so there is no unused optical-size or width axis being left on the table. What *is* being left on the table is the top of the weight range: the file carries 200/300/700/800 as named instances and the app registers 400/500/600. *(The design doc §141 "Avoid 800/900" is a usage rule and is not in question; 700 is the one that matters here.)*
- **Only three weights, all in the middle.** 400/500/600 with no 700 and nothing above means the scale cannot express emphasis — it can only get marginally heavier. "Emphasis" in this product has to be carried by size, colour, or surface, and §5.3 shows size is exhausted at one step.
- **No second family for a distinct register.** There is no display/serif companion, no mono for units or dates, no tabular-figure setting. One family does all jobs, including the job it is worst at (aligned, rapidly-changing figures).
- **The hierarchy is a single step.** With nothing between 28sp and 20sp, and nothing between 20sp and 16sp, and the most important numerals pinned to 20sp, the type system communicates one idea: "this is a 20sp Semibold app."

**Not weak — genuinely strong:**
- Manrope is a real, non-default choice with real character, bundled correctly and offline. It is the product's clearest existing identity asset and it should survive into Step 2.
- The `TypographySample` preview proves the design system *intends* a coherent 7-step scale; the intent is right and the mapping to Material slots is what flattened it.
- Weight is used semantically in the right places even if the values are limited: selected tabs go 500→600 (`LiquidGlass.kt:389`), chips likewise (`MomentumBadges.kt:137`), and completion labels go to 600 (`MomentumBadges.kt:85`). "State is never colour-only" is genuinely implemented at the weight level.

#### 5.5 Recommendations — typography

1. **Add a numeric register.** The doc already requires it; the scale does not provide for it. *Kind of change:* introduce a distinct figure treatment — a larger size step, tabular figures via `fontFeatureSettings = "tnum"`, and a real emphasis weight — and then decide, per figure class, which one owns it (hero percentage, streak count, stat value, inline target `3 / 5`, calendar day figures). This is the highest-leverage single change in the foundation, and it is a *typographic* change, not a layout change.
2. **Resolve the `FontWeight.Bold` request at 5 sites.** ~~Either register the 700 instance the family is presumably capable of, or change the requests to 600~~ — **the first option is now confirmed available (F-9b).** The bundled `manrope.ttf` is a variable font with `wght` 200–800 and a named **700 (Bold)** instance (`nameID 267`), so a fourth `Font()` entry pinned at `FontVariation.weight(700)` — or dropping the `variationSettings` pins so the family follows the requested weight — makes all five sites render at real 700, **with no new asset, no new dependency and no network fetch**, consistent with AGENTS §2 and §11. Until then the product's most important numbers render at a weight nobody chose.
3. **Re-cut the scale around the semantic levels the product actually has.** Five levels currently share one style. *Kind of change:* decide the number of levels (five semantic levels argue for at least four distinct sizes) and re-map the Material slots so `titleMedium` is not body text and `bodySmall` is not a 12sp label — or stop aliasing and use a Momentum-specific typography object.
4. **Turn on tracking where it belongs.** *Kind of change:* a per-token tracking decision, largest at Caption, mildly negative at Display — a real use of the variable font that is already shipped.
5. **Decide whether figures get their own family.** A tabular or mono companion for numbers is a common, defensible answer to "at a glance" and it is a real product decision, not a style preference. Left open for Step 2.
6. **Do not treat "it's already Manrope" as a reason to keep the scale.** The typeface is an asset; the scale built on it is the problem. These are separable.

---


---

## 6. Color Audit

#### 6.1 The token contract, as implemented

`Color.kt:15-50` defines 24 slots. Both environments, verbatim:

| Token | Light Aurora | Dark Aurora | Production call sites |
|---|---|---|---|
| `background` | `#F7F8FC` | `#080D18` | via alias + M3 `background` |
| `surface` | `#FFFFFF` | `#111827` | 21 |
| `surfaceSoft` | `#F1F2FB` | `#151E30` | 3 |
| `surfaceElevated` | `#FFFFFF` | `#172033` | **1** (`Theme.kt:73`) |
| `surfaceInteractive` | `#E8EBF7` | `#1D2940` | **0** (previews only) |
| `textPrimary` | `#172033` | `#F5F7FC` | 57 |
| `textSecondary` | `#596579` | `#AEB8CA` | 74 |
| `textMuted` | `#8993A4` | `#737F95` | **2** |
| `textDisabled` | `#B8BFCA` | `#4D586B` | **0** |
| `success` | `#1F8A70` | `#76DFC4` | 16 |
| `warning` | `#B45309` | `#FFC49B` | 5 |
| `danger` | `#C74545` | `#FF8E8E` | 2 |
| `borderSubtle` | `#E4E7F1` | `#1C2740` | M3 `outlineVariant` only |
| `borderStrong` | `#C7CDDA` | `#2A3854` | M3 `outline` only |
| `progressTrack` | `#E7EAF4` | `#1D2940` | **0** |
| `auroraBlue` → `accentPrimary`/`primary` | `#5C9FEF` | `#78B7FF` | 54 |
| `auroraLavender` → `accentSecondary`/`secondary` | `#9278E8` | `#B59CFF` | 4 |
| `auroraViolet` → `accentTertiary`/`accent` | `#8064DD` | `#9B7CFF` | 3 |
| `auroraMint` | `#55C8AD` | `#76DFC4` | **0** |
| `auroraPeach` | `#EAA477` | `#FFC49B` | 1 |
| `onAccent` → `onPrimary` | `#172033` | `#080D18` | 7 |

#### 6.2 Is this an intentional palette, or a default Material 3 tonal scheme?

**It is emphatically not a Material 3 default.** Measured evidence: the Compose Material 3 baseline `lightColorScheme()` primary is `#6750A4` (a purple); `background` is `#FFFFFBFE`; `surface` is `#FFFFFBFE`; `surfaceVariant` is `#FFE7E0EC`. Nothing in Momentum's palette is inherited from that scheme — the tokens are hand-picked, the neutrals are cool grey-blue (`#172033`, `#F7F8FC`, `#E4E7F1`) rather than M3's purple-leaning neutrals, and the accents are a blue/lavender/violet/mint/peach family rather than a single tonal hue. `app/build.gradle.kts` declares no dynamic-colour dependency and the code never calls `dynamicLightColorScheme`/`dynamicDarkColorScheme`. **The palette is authored, and its authorship is deliberate.** That should be stated plainly, because "it is Material 3" would be the easy and wrong dismissal.

**But it is also not a *system*.** Three measurements:

1. **The Light surface ramp collapses.** Light declares `surface = #FFFFFF` and `surfaceElevated = #FFFFFF` — **the same value.** So Light has **3** distinct surface values, not 4, and the doc's declared depth ladder (`Canvas → Surface → Elevated → Interactive`, §7) cannot be expressed in Light at all. Dark has all 5 distinct. The two environments therefore do not have the same structural capacity, which contradicts §3's *"Light and Dark Aurora are two environments of the same design language."*
2. **The Light tonal ladder is very narrow.** `#F7F8FC` → `#F1F2FB` → `#E8EBF7`. The whole surface range spans a small fraction of the lightness axis. On Dark the equivalent ladder (`#080D18` → `#111827` → `#151E30` → `#172033` → `#1D2940`) is legible as steps. On Light, a surface can only be separated from the canvas by a hairline — which is precisely what happens at runtime (§7.7).
3. **The text ladder is declared in four and used in two.** `textPrimary` 57, `textSecondary` 74, `textMuted` 2, `textDisabled` 0. And `ISSUE-020` records the live consequence: `textSecondary` `#596579` measures **3.11:1** over sampled Light hero artwork, below the 4.5:1 floor the product's own success criteria require. The middle text tier is *already* failing on the one surface where it matters most, and its remedy was explicitly deferred.

#### 6.3 The Material 3 bridge is only partly mapped

`Theme.kt:41-77` builds `darkColorScheme(...)` / `lightColorScheme(...)` passing **15 slots** each: `primary`, `onPrimary`, `secondary`, `onSecondary`, `tertiary`, `background`, `onBackground`, `surface`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`, `error`, `onError`, `outline`, `outlineVariant`.

> **CORRECTION (F-3, Reviewer-verified MAJOR).** The original text said "passing **12 slots** each" and then immediately enumerated **15** names — a self-contradiction in a single paragraph, and the "12" is what propagated. The count is **15**. The structural conclusion below is unchanged and was never in dispute: `ColorScheme` has more slots than that, and every slot not passed keeps the library's compile-time default. The Reviewer noted the *derived* "28 unmapped slots" figure is `ColorScheme` library surface, not repository surface, and it has been removed from §7.10 item 8 accordingly.

Two consequences, stated at the level I can defend:

- **Structural fact (determinable):** the M3 `ColorScheme` surface has substantially more slots than that. Every slot that is not passed keeps the library's compile-time default, which is drawn from the Material 3 baseline palette — a palette that shares no colour with Aurora (§6.2). **Unknown — not determinable from the repository:** the exact resulting hexes, since they are library source. *What is determinable and matters:* any M3 component that reads an unmapped slot will render a colour that is not in Momentum's palette, and the app uses several such components — `LinearProgressIndicator` (`MomentumComponents.kt:268`), `HorizontalDivider` (`:284`), `TextButton` (`:246,249,251`), `IconButton` (`:68`), `ModalBottomSheet` (`MomentumNavGraph.kt:106,362`).
- **Live violation of the stated rule:** `AGENTS.md` §5 forbids using `MaterialTheme.colorScheme.surfaceVariant` as decoration, and the doc's §7 forbids surface tokens as lines. Two production sites do exactly that: `MomentumComponents.kt:275` (`trackColor = c.surfaceVariant` on the linear progress) and `InsightsScreen.kt:254` (`val track = c.surfaceVariant.copy(alpha = 0.55f)`). Meanwhile the correct token — `progressTrack`, defined in both themes specifically for this — is used **zero** times. And `ProgressOrb.kt:112-114` documents in a comment that a surface token read as a stroke over artwork was "approximately 1:1 against the pale light art, i.e. no visible track at all", then works around it by using `textPrimary.copy(alpha = 0.55f)` instead. **Three different tokens are being used to draw progress tracks in three different screens, and the one designed for the job is unused.**

#### 6.4 Semantic state: one hue doing five jobs

`success` has 16 call sites and carries **five unrelated meanings**:

| Meaning | Sites |
|---|---|
| "This occurrence is completed" | `ActivityCard.kt:73,97,106`; `ActivityDetailScreen.kt:207,297`; `TargetEntryDialog.kt:160` |
| "This habit is ACTIVE (vs PAUSED)" | `HabitsScreen.kt:378,383`; `HabitDetailScreen.kt:375,380` — paired against `c.warning` |
| "This streak hit a 7-day milestone" | `MomentumBadges.kt:57,66,81,86` |
| "This calendar day is fully complete" | `CalendarScreen.kt:224` |
| "This trend is positive" | `InsightsScreen.kt:128` |

**Why this is wrong:** `success` is simultaneously the completion colour, the status-pill colour, the reward colour, the calendar-heat colour and the trend-polarity colour. A user who has learned "green means done" (from the completion control) will read the ACTIVE status pill as "done", and the 7-day milestone badge as "you have completed something", when neither is true. The token carries no semantics beyond "positive", which is the opposite of what a *semantic* token layer is for. **User effect:** colour stops being decodable; the user has to read the label every time. **What KIND of change would solve it:** separate the roles — a completion state colour, a status (active/paused) colour pair, and a trend-polarity pair — and accept that this means more tokens than exist today. The doc already says (line 123) *"BUILD and REDUCE do not receive permanent semantic colours"*; the same discipline is not applied to ACTIVE/PAUSED or to trends.

Two related observations:
- `c.warning` is used **only** as the PAUSED half of a status pair (4 sites) plus one trend-negative branch. So the token named "warning" carries no warning in the product.
- **Completion and progress use different hues for the same axis.** The completion control and completed tiles are `success` (a green-teal); the progress ring, the progress fill, every primary action, the selected chip and the orb's halo are `accentPrimary` (blue). The two halves of "how am I doing today" — *how far* and *what's done* — are encoded in two different hue families on the same screen. Whether that is a considered distinction or an accident is not determinable from the repository; that it is *undocumented* is.

#### 6.5 Brand/accent vs. per-item identity

The relationship is currently: **theme accent** on almost everything, **per-item accent** on three surfaces, and **no relationship between them at all.**

- `accentPrimary` (blue) is the single most-used colour in the product — 54 call sites. It is simultaneously: primary CTA fill, progress fill, ring arc, orb halo, selected chip fill, section-header action label, empty-state icon tile, stat sublabel, selected-theme border, and the glow on every glass button.
- Per-item accent (`habitIconAccent`, 7 fixed hues) is used only on `HabitIconTile`, `HabitConfigScreen` cards, and the `HabitDetailScreen` hero.
- The two vocabularies never intersect. `habitIconAccent`'s `cyan #5BB8E8` sits between the theme's blue and lavender; its `lavender #B39DDB` sits between lavender and violet; none of the seven is one of the five theme accents. **The product therefore has one hue doing global work and seven hues doing item work, with no relationship documented or enforced between them.**

**Why this is wrong:** if per-item colour is meaningful, the user should be able to predict it; if it is decorative, it is competing with the accent for attention. Currently a purple "meditation" habit and a violet ambient field are the same family of colour, so the item identity and the chrome are chromatically indistinguishable. **User effect:** per-habit colour reads as noise rather than as a key. **What KIND of change would solve it:** a stated rule for the relationship — either per-item hues are drawn *from* the theme accent family (so the app looks like one palette), or they are deliberately outside it and the chrome is made neutral enough that the items carry the colour. Both are viable; the current state is neither.

#### 6.6 Recommendations — colour

1. **Separate the five meanings currently on `success`.** *Kind of change:* introduce distinct roles for completion / status / reward / trend-polarity. This is the single change that would most increase the user's ability to decode the UI at a glance.
2. **Decide the relationship between the theme accent and per-item accent**, and enforce it. Either derive the seven item hues from the theme's five, or neutralise the chrome. Document the rule in the token layer so new screens cannot diverge.
3. **Fix the Light surface ramp.** `surface` and `surfaceElevated` being the same value means the declared four-level depth model is inexpressible in Light. *Kind of change:* a real Light tonal ladder, or an honest reduction of the model to the levels Light can actually support.
4. **Wire `progressTrack` and stop drawing progress tracks with three different tokens** in three different screens.
5. **Complete the M3 bridge or stop using unmapped M3 components.** 12 of the scheme's slots are passed; the components the app actually uses read several that are not. *Kind of change:* either map the full surface family, or route every M3 component in the app through Momentum-owned wrappers so no unmapped slot is ever read. The second is cheaper and more in keeping with the existing architecture.
6. **Give `warning` a job or rename it.** It is currently "the PAUSED colour".
7. **Re-examine `textSecondary` before Step 2 builds on it.** `ISSUE-020` has it at 3.11:1 over Light hero artwork with the remedy deferred. Any Step 2 direction that places secondary text on imagery inherits this.

---


---

## 7. Surface & Depth Audit

#### 7.1 The glass mechanism, and why it is a dark-mode mechanism

`LiquidGlass.kt:94-135`, `Modifier.liquidGlass`, is the foundation of every surface in the app. The recipe is: optional soft shadow → `clip` → vertical gradient → hairline border, where the fill is `c.surface.copy(alpha = level.fillAlpha)` and the top stop is a "highlight".

`GlassLevel` (`:66-82`) declares four levels:

| Level | Fill alpha | Elevation | Where used |
|---|---|---|---|
| `HERO` | 0.68 | 12 dp | bottom nav bar, dialogs (via `MomentumDialog`), quick add, selected theme card |
| `DIALOG` | 1.00 | 16 dp | `TargetEntryDialog` only |
| `CARD` | 0.55 | hairline | `LiquidGlassCard`/`MomentumCard`, `ActivityCard` (incomplete) |
| `CONTROL` | 0.45 | hairline | `LiquidGlassIconButton`, chips, `ActivityCard` (completed) |

The KDoc at `:56-59` claims *"Every glass surface in the app is one of three hierarchy levels rendered by `liquidGlass`"*. There are four. The KDoc is stale relative to the enum — a small thing, but it is the kind of drift that tells you the document and the code stopped being written together.

**The mechanism in Light.** In Dark, `surface #111827` is *lighter* than `background #080D18`, so a translucent surface fill produces a genuine lift. In Light, `surface` is `#FFFFFF` and `background` is `#F7F8FC` — the surface is already almost the same lightness as the canvas, and a *translucent white* over near-white moves further toward white but by almost nothing. `CARD` at 0.55 over the canvas composites to roughly `#FBFCFE` against a canvas of `#F7F8FC`. **The fill is chromatically invisible in Light.** The only thing separating a Light card from the page is the hairline: `c.textPrimary.copy(alpha = 0.10f)` — a 10%-alpha hairline (`LiquidGlass.kt:109-113`).

So the depth model the doc describes (§7: *"Light Aurora relies primarily on tonal contrast, very soft shadows, and subtle borders"*) resolves, at runtime, to: **in Dark, tonal contrast works; in Light, a one-pixel line does all the work.** There is no tonal contrast available in Light because `surface == surfaceElevated == #FFFFFF` and the canvas is already `#F7F8FC` (§6.2). This is the mechanism-level reason Light Aurora reads flatter than Dark Aurora, and it is structural, not a styling oversight.

The doc's §7 also promises *"very soft shadows"* for Light. `MomentumElevation` is never used, and the shadows that do exist are `Color.Black.copy(alpha = 0.30-0.35f)` ambient with an accent-tinted spot (`LiquidGlass.kt:118-119`, `:219-220`, `CompletionButton.kt:104-105`, `HabitsScreen.kt:240-241`) — i.e. **the same shadow recipe in both themes**, and it is a *dark* shadow, which is invisible on a light canvas by construction. The `MomentumGlass.heroElevation = 12.dp` / `dialogElevation = 16.dp` constants are used by exactly two enum entries.

#### 7.2 How many surface levels actually exist

Not four. Measured:

| Fill value | Where | Origin |
|---|---|---|
| 1.00 | `GlassLevel.DIALOG` | token |
| 0.92 | primary button body (`LiquidGlass.kt:203`) | literal |
| 0.85 | selected `CategoryChip` (`MomentumBadges.kt:107`) | literal |
| 0.68 | `GlassLevel.HERO` | token |
| 0.60 | secondary button body (`LiquidGlass.kt:202`) | literal |
| 0.55 | `GlassLevel.CARD` | token |
| 0.45 | `GlassLevel.CONTROL` | token |
| **0.385** | `EmptyState` (`MomentumBadges.kt:205`, `cardFill * 0.7f`) | derived literal |
| 0.14 | `EmptyState` icon tile, `HabitConfigScreen` accents | literal |
| 0.16 | `ActivityCard`/`HabitIconTile` icon washes | literal |

**Nine distinct fill values across the app; four of them are tokenised.** The `EmptyState` case is the clearest: a component that reaches into the token, multiplies it by `0.7f`, and thereby invents a fifth surface level that nothing else knows about. Meanwhile `StatCard` and `StreakBadge` bypass `liquidGlass` entirely and re-implement the same recipe inline (`MomentumBadges.kt:152-166`, `:53-74`) — so the "one glass primitive" is not one primitive.

#### 7.3 "Liquid Glass" is a name, not a material

`Tokens.kt:139-145` states the reasoning plainly and it is sound: `backdropBlur = 0.dp`, because *"no reliable cross-device blur API exists on our minSdk"*. What ships is translucency + a hairline + a top-edge highlight + a soft shadow. There is no blur anywhere in the app.

This is worth stating carefully rather than as a criticism of the engineers: the *technique* is a legitimate, well-executed approximation, and the KDoc is honest about it. The cost is to **Step 2's vocabulary**. Every file, class and function in the component layer is named `LiquidGlass*` — a material term. A reader (human or agent) forming a mental model of Momentum's surfaces will assume a refracting, depth-sorting material with a physical quality, and will be wrong. The doc's §7 uses "glass" descriptively; the code uses it as a proper noun for something that is a translucent rounded rectangle.

#### 7.4 Two ambient systems, stacked on three screens

`AppBackground` wraps the entire `NavHost` (`MomentumNavGraph.kt:133`) and draws three static radial fields at `ambientDark 0.13` / `ambientLight 0.16`. `AuroraBackground` is additionally used *inside* three destinations:

- `TodayScreen.kt:219` (inside the Scaffold content, over `AppBackground`)
- `HabitsScreen.kt:148`
- `HabitDetailScreen.kt:160`

The two systems are not variants of each other:

| | `AppBackground` | `AuroraBackground` |
|---|---|---|
| Hues | `primary`, `secondary`, `accent` (blue, lavender, **violet**) | `auroraBlue`, `auroraLavender`, `auroraPeach` (blue, lavender, **peach**) |
| Base alpha | 0.13 / 0.16, fixed | 0.20 / 0.16 × state intensity |
| Animated | never | 6 s drift loop |
| State-responsive | no parameter for it | yes (REST/PROGRESS/COMPLETION) |
| Field positions | fixed per screen size | drifting |

So on Home, Habits and Habit Detail the user sees **two stacked ambient light systems in different hues at different intensities**, while on the other eight destinations they see one. That is an observable difference in the background of the three screens that carry the product's signature content — and it is not documented anywhere. It is also the reason Home's background differs in character from Tasks' or Insights' with no design decision behind the difference.

Separately, `AppBackground`'s documented "whisper of vertical depth" does not render (§3.3), and its `c.accent` field resolves to **violet** while `AuroraBackground`'s third field is **peach** — so the two systems' warm anchors are different hues.

#### 7.5 The photographic surface systems

Three distinct photographic surface systems, with three different readability treatments:

| System | Assets | Light variant? | Readability treatment |
|---|---|---|---|
| Hero artwork | `habits_hero_light/dark.webp` | **yes** | 88 dp bottom `DstIn` dissolve; no top fade by ADR-39; `ContentScale.Crop`, bottom-anchored; optional `zoom` + `parallax` on a GPU layer (`MomentumHeroArtwork.kt:130-151`) |
| Onboarding | `onboarding_screen_01`–`04.webp` | **no — dark only** | a five-stop vertical `PhotoShade #060B18` gradient to 0.74 alpha at the bottom (`OnboardingBackgrounds.kt:95-101`) |
| Launcher | `ic_launcher_foreground.xml` | n/a | n/a |

The onboarding's treatment — darkening the artwork by up to 74% to make text legible — is the opposite strategy to the hero's, which dissolves the artwork into the page instead of darkening it. And the onboarding has no Light variant at all, so a user who has chosen Light Aurora meets four near-black photographic screens in their first session (§7.9).

#### 7.6 The orb is a four-layer arc stack

`ProgressOrb.kt:107-173` draws, for any non-zero sweep: a full-circle track, a breathing halo arc at `1.9×` stroke width, a sweep-gradient lobe layer at `1.35×` stroke width rotated to orbit, and finally the crisp accent arc. Four `drawArc` calls, two of them animated by an infinite 6 s transition, on a 96 dp default.

The KDoc opens with *"Compact by default — progress should inform, not dominate"* (`ProgressOrb.kt:42-44`) and then describes *"a living light"*. Those are two different design positions. A permanent infinite animation on the app's primary screen, running whenever any progress exists, is a cost the user pays continuously for a value that changes a few times a day. The draw-order reasoning (halo under the arc, so the glow can never veil the sweep — `:161-172`) is genuinely good engineering and should be preserved in whatever replaces it.

Two token observations: the halo uses `c.glow`, which is an alias of `auroraBlue` (`Color.kt:55`) — the **same hue as the arc it sits under**, so the halo contributes luminance only, not hue. And the track uses `c.textPrimary.copy(alpha = 0.55f)`, a text token used as a line, with a comment explaining why a surface token could not be used (`:207-221`).

#### 7.7 Elevation: two systems, one of them dead

Covered in §3.3 and repeated here because it is the clearest single illustration of the foundation's governance problem: `MomentumElevation` (none 0 / subtle 1 / raised 4 / floating 8) has **0 production call sites**; seven `.shadow(` calls exist with hardcoded elevations of 8, 10 and 12 dp; and the doc's §7 promise of *"very soft shadows"* for Light is served by a black-alpha shadow that is invisible on a light canvas. The design system's own preview prints the token values as a caption, which is why the token looks alive.

#### 7.8 Recommendations — surfaces and depth

1. **Decide how Light Aurora expresses depth, because today it does not.** The glass mechanism is a luminance mechanism; in Light the surface and the canvas are already within a few percent of each other, so a translucent white fill adds nothing and a hairline does everything. *Kind of change:* either a real Light tonal ladder (so translucency has something to reveal) or an explicitly Light-specific surface treatment that does not rely on translucency at all. This is the highest-leverage change in the surface layer because it currently makes half the app's environments look accidental.
2. **Reconcile the two ambient systems.** *Kind of change:* one ambient system with one hue set, either state-responsive everywhere or static everywhere. Today three of eleven destinations look different from the other eight for no stated reason.
3. **Give `AppBackground` a real vertical gradient or delete the claim.** Right now it is a flat fill described as depth.
4. **Close the surface-level gap.** Nine fill values, four tokenised; `EmptyState` derives a fifth level; `StatCard`/`StreakBadge` re-implement the recipe inline. *Kind of change:* either every surface goes through `liquidGlass` with a `GlassLevel`, or the level enum is retired in favour of an explicitly open scale that components are expected to extend — but not both at once.
5. **Rename or implement "Liquid Glass".** A material name for a translucency recipe will mislead Step 2. *Kind of change:* a terminology decision, or a real blur path with a documented fallback.
6. **Make the orb's cost proportional to its importance.** A permanent infinite animation on the primary screen for a value that changes a few times a day is a standing cost. *Kind of change:* animate on change and settle; reserve the continuous loop for an actual milestone moment. This dovetails with §7.8's finding that `AuroraState.MILESTONE` is unreachable — both point at the same missing capability: a real reward moment.
7. **Decide whether the onboarding's darkening strategy or the hero's dissolve strategy is the house approach.** Two artworks, two opposite readability models, one of them with no Light variant.

---


---

## 8. Layout & Composition Audit

#### 8.1 One list, and the Hero that interrupts it

`DASHBOARD_SPECS.md` §4 fixes Home's structure as a single vertically scrollable surface — Hero, Remaining, Completed — with no nested scroller, and §15 requires the Hero to collapse as the list scrolls. The code honours this exactly: one `LazyColumn` (`TodayScreen.kt:230`), the Hero as `item { }` (`:250`), `nestedScroll(heroNestedScroll)` (`:233`), and the collapse driven by one clamped `Float` px value with a `NestedScrollConnection` doing pre-scroll consume and post-scroll restore (`:175-202`). That architecture is correct, well-commented, and directly traceable to the spec. **It should survive.**

The problem is what the Hero is *made of*. `HomeHero.kt` is **771 lines** — larger than the entire token layer (`presentation/theme/` is 8 files) and 3.5x `HabitsHero.kt` (222 lines). It contains five private layout constants (`HeroTopInset` 16 dp, `HeroBottomInset` 16 dp, `HeaderContentGap` 20 dp, `CollapseHeadroom` 72 dp, `HeroRingSize` 96 dp), a `RingBounds` derived box, three custom `drawWithCache` layers (artwork bottom blend, readability veil, screen-space foreground mask), a 24-sample S-curve, a 48-sample veil profile, and five veil anchor constants (`VeilHeaderDark` 0.26 / `VeilHeaderLight` 0.25 / `VeilOpen` 0.20 / `VeilContentDark` 0.32 / `VeilContentLight` 0.30).

**None of those constants is in `MomentumSpacing`.** The design system §5 says *"Avoid arbitrary spacing values unless implementation requires a specific exception"* and §34 lists spacing as something screens must not invent. The Home Hero has invented a private spacing and alpha vocabulary, and the design system has no representation of it.

**The most important observation in this section is the app's own verdict on itself.** `HomeHero.kt:64-66` states: *"the Habits header is the reference composition and works because its copy sits inside the artwork's own Box (ADR-28)."* The app documents `HabitsHero` as the reference implementation, then built `HomeHero` as a three-and-a-half-times-larger, three-layer-mask, five-anchor-constant alternative. Both sit on the same artwork via the same `MomentumHeroArtwork` component. **The two Hero architectures are not two answers to the same question; one is a simplification of the other that was not adopted.**

The KDoc and constants also carry a written history of repeated defect: *"up to 130 dp of void below the message"* (`:166-168`), *"the old anchors jumped from 0.10 in the open band to 0.58/0.62 behind the ring block, and that level change landed exactly between the greeting and the ring, which is what read as a card edge"* (`:673-681`), *"a hole opened under the lifted copy"*, and the current safety margin is stated as *"The open band and the content plateau are now within 0.12 of each other in both themes"* (`:677-679`). A component whose remaining margin is a 0.12 alpha budget on a 0.30 plateau, arrived at through ISSUE-015/016/018 and ADR-31/32/34/35/37/39, is a component that has absorbed disproportionate effort for a *header*. That is a fact about the code, not an opinion about taste, and it is the single most important input to §19.

#### 8.2 The Hero's own typography is a hierarchy inversion

Reading `HomeHero.kt` top to bottom, the type stack of the app's primary screen is:

| Element | Line | Style | Effective size | Weight |
|---|---|---|---|---|
| Date | 347-351 | `labelLarge` | **13 sp** | 500 |
| Greeting | 352-356 | `headlineMedium` | **20 sp** | 600 |
| Orb percentage | `ProgressOrb.kt:179` | `headlineMedium` | **20 sp** | requested 700 (unregistered) |
| "3 of 5 completed" | 425-430 | `titleMedium` | **16 sp** | **400** |
| Motivational message | 431-438 | `bodyMedium` | **14 sp** | 400 |

**What is wrong:** the sentence that answers the screen's own question — "3 of 5 completed" — is set in `titleMedium`, which `Type.kt:111` maps to **Body 16/24 Regular**. It is *body text*. It is smaller than the greeting above it and 8 sp smaller than the design system's Display. The app's title, declared at `DASHBOARD_SPECS.md` §322 as *"How am I doing today?"*, answers that question in regular-weight body copy, beneath a decorative greeting set a full step larger, beneath a date set at label size. The emotional line ("You're building momentum.") is 14 sp; the factual line is 16 sp; the identity line is 20 sp. **Priority runs exactly backwards.**

**Why it is wrong:** §7.5.3 established that the type scale has no step between 28 and 20, nor between 20 and 16. This is the consequence *inside a composition*: the Hero has five semantic levels and the scale offers it two usable ones, so the code reached for the wrong one at the two most important positions.

**User effect:** a user glancing at Home reads "Good morning" as the headline. The number they came for is not the biggest thing in the composition, and it is not visually distinguished from the metadata lines of the cards below it.

**What KIND of change would solve it:** a decision about which *one* element is the screen's headline — the date/greeting pair, the percentage, or the fraction — and a numeric treatment sized to hold that role (§7.5 recommendation 1). Not a colour change, not a spacing change.

#### 8.3 The empty day is a decorative void

`HomeHero.kt:402` and `:440-449`: when `totalCount == 0` **and** `message` is blank, `HeroContent` renders nothing at all. `HomeHeroEmptyPreview` (`:753-771`) confirms this is deliberate: `message = ""`, `progress = 0f`, `totalCount = 0`.

`DASHBOARD_SPECS.md` §13 requires: *"If there are no eligible activities scheduled for today: Do not display empty activity sections. Display a dedicated no-activity state. The state should communicate that there is nothing scheduled for today."* The code does place a no-activity state — but *below* the Hero (`TodayScreen.kt:270-272`, `HomeEmptyState`).

So on a day with nothing scheduled, the top of the screen is: 16 dp inset, a 13 sp date, a 20 sp greeting, 20 dp gap, **a weighted spacer that expands to fill `CollapseHeadroom` = 72 dp**, 16 dp bottom inset — i.e. **a 300 dp-scale photographic Hero containing a date and a greeting and nothing else**, above a card that says "Nothing scheduled today". `HomeEmptyState`'s copy ("When you add a habit or task it shows up here. One small step is enough.") is doing work that belongs at the top of the screen.

**Why it is wrong:** a first-run user, and any user on a rest day, meets the product's largest and most decorated surface before meeting any information. The composition spends its most expensive real estate on its least informative state.

**User effect:** the app looks unfinished on exactly the days it is least needed, and a new user has to scroll past ~300 dp of aurora to learn that they have no activities.

**What KIND of change would solve it:** a rule for what the Hero contains when the Hero has nothing to report — either the Hero shrinks to its text, or the empty state moves into the Hero's own space, or the artwork is withheld entirely on an empty day. Any of the three; the current behaviour is the one option that is not one of them.

#### 8.4 Three different inset strategies, and a measurable misalignment

`MomentumTopBar` applies `MomentumSpacing.screen` (20 dp) internally (`MomentumComponents.kt:60-63`). Where it is placed as an `item {}` **inside** a `LazyColumn` whose `contentPadding` also applies `MomentumSpacing.screen`, the title lands at **40 dp** while every sibling in the same list sits at 20 dp.

| Screen | `MomentumTopBar` at | Effective title inset |
|---|---|---|
| Insights | `:83` as `item {}` inside padded list (padding `:76`) | **40 dp** |
| Calendar | `:107` as `item {}` inside padded list (padding `:100`) | **40 dp** |
| Activity Detail | `:146` as `item {}` inside padded list (padding `:139`) | **40 dp** |
| Settings | `:111` as `item {}` inside padded list (padding `:104`) | **40 dp** |
| Theme Gallery | `:82` as `item {}` inside padded list (padding `:75`) | **40 dp** |
| Habit Config | `:125`, sibling **above** the list opened at `:130` | 20 dp — correct |
| Task Config | `:78`, sibling **above** the list opened at `:83` | 20 dp — correct |
| Activities / Tasks | `:67` in outer `Column` (`:66`), list padded at `:198` | 20 dp — correct |
| Creation Nav (loading + error) | `:55`/`:72` in outer `Column`, list padded at `:57`/`:74` | 20 dp — correct |

**5 of 10 `MomentumTopBar` call sites have the screen title inset 20 dp further than the content it titles.** On those five, the first thing a user sees is visibly out of alignment with everything below it. The other five are correct.

> **CORRECTION (F-2, Reviewer-verified MAJOR).** The original version of this subsection claimed **"7 of 9"** and included Task Config and Habit Config in the 40 dp group, citing their `contentPadding` line numbers as if the top bar sat inside the padded list. The line numbers were real; the *structure inferred from them* was not — in both files `MomentumTopBar` is a sibling **above** the `LazyColumn`, inside the outer `Column(modifier = Modifier.fillMaxSize())` (`HabitConfigScreen.kt:124-130`, `TaskConfigScreen.kt:77-83`). The corrected count is **5 of 10** and the Edit-flow sub-claim below is withdrawn. The *direction* of the finding survives: a real ragged left edge exists, on five screens.

**Withdrawn sub-claim — the Edit flow does not shift margin.** The original text asserted that *"`CreationNav`'s loading and error states place the top bar in an unpadded `Column` (20 dp), while the loaded `HabitConfigScreen`/`TaskConfigScreen` place it inside a padded list (40 dp). The same screen's left margin jumps 20 dp when the data arrives."* This is **refuted**: `HabitConfigScreen` and `TaskConfigScreen` use the *same* outer-`Column` structure as `CreationNav`'s loading and error states, so the Edit flow's title is at 20 dp in every state. There is no mid-flow margin jump, and no user-visible effect of that kind.

Three further inset strategies coexist:

- **Home**: edge-to-edge, `contentPadding top = 0.dp` deliberately (`TodayScreen.kt:234-237`), Hero supplies its own status-bar inset from `WindowInsets.statusBars` (`HomeHero.kt:135-136`).
- **Habit Detail**: overlay chrome floating above the list, with `top = MomentumSpacing.section + MomentumHeights.iconTarget` = 24 + 48 = **72 dp** and `bottom = 168.dp` (`HabitDetailScreen.kt:168-174`) — **240 dp of the viewport reserved for insets** before content, on a device that may be 640 dp tall.
- **Everything else**: Scaffold `contentWindowInsets` plus 20 dp horizontal / 8 dp vertical list padding.

**Why this is wrong:** edge-to-edge behind the status bar is a deliberate, well-argued decision (ADR-33) and it is the *right* one for a screen whose top is artwork. The problem is that it was made per-screen inside a screen file rather than as a shell-level contract, so each screen re-derived its own insets, and five of the ten that place a top bar inside a padded list re-derived them incorrectly.

**User effect:** a 20 dp ragged left edge on five of the screens that carry a top bar; and a habit detail screen where 240 dp of the viewport is chrome before the habit's name. *(Corrected: the "margin jump on the Edit flow" effect previously listed here is withdrawn per F-2.)*

**What KIND of change would solve it:** one screen-shell contract (a top-app-bar inset strategy, a content inset, a floating-chrome clearance) applied at the Scaffold rather than per screen, so alignment is a property of the shell and not of ten independent call sites.

#### 8.5 Three progress representations, in a product whose third principle is progress

Design principle 3 (`MOMENTUM_DESIGN_SYSTEM_V1.md:24`) is *"Progress is Momentum's visual language."* Measured representations of progress in the shipped app:

| Representation | Where | Screens |
|---|---|---|
| `ProgressOrb` — 96 dp ring, 4 stacked arcs, live percentage | `HomeHero` only | **1** |
| `MomentumLinearProgress` — 6 dp rounded bar | Insights category rows | 1 |
| `WeeklyBars` — 96 dp `Canvas`, 7 bars, no values, no axis | Insights | 1 |
| Bare number in a `StatCard` — 20 sp, no track, no scale | Insights, Activity Detail | 2 |
| 14-slot `LazyRow` of unlabelled squares | Activity Detail "Recent history" | 1 |
| 7 x 3-state day cells | Habit Detail "This Week" | 1 |
| 42-cell month grid | Calendar | 1 |

Seven representations. `ProgressOrb` — which `DASHBOARD_SPECS.md` §142 calls *"a centered circular progress indicator"* and the design doc §341 calls *"Momentum's signature component"* — is used on **exactly one screen in the entire app.**

**Why it is wrong (and what made it the default):** the Home Hero needed one very large ring showing one very large number, and the design system responded with a bespoke 262-line component hard-wired to that use (`size: Dp = 96.dp` default, `RingBounds` computed for a 12 % scale headroom, a 4-arc draw order, a `rememberGlowPhase` infinite clock). Because the primitive was built for one caller rather than parameterised as *the* progress primitive, every other progress display in the product had to invent its own. **The signature component is the reason there is no progress language.**

#### 8.6 Density and the list row

`ActivityCard` (`ActivityCard.kt:79-149`) is a single `Row`: 44 dp icon tile, a `Column` of (title / optional schedule label / optional `StreakBadge`), and a 48 dp `CompletionButton`. The design doc §15 requires exactly this. The row is honest and compact.

Two measured problems:

1. **`StreakBadge` sits inside the text column** (`ActivityCard.kt:140-142`), so a card can be 44 dp (no label, no streak), 44+18 (label), or 44+18+22 (label + streak) tall. **Row height varies by content**, so a list of cards is a ragged stack rather than a rhythm. `DASHBOARD_SPECS.md` §20 requires Home to *"remain usable when many activities are eligible"*; nothing reserves a fixed row height.
2. **`ActivityCard`'s card is `fillMaxWidth()` and `liquidGlass(CARD)`**; the Tasks/Activities list then adds a **48 dp action row of unlabelled glass icon buttons beneath every card** (`ActivitiesScreen.kt:226-266`). So the same entity is 44 dp on Home and ~100 dp on Tasks, with the extra weight made of up to two 48 dp circular glass controls that are visually identical to each other and one of which archives. **The Tasks list is roughly twice as heavy as the Home list for the same objects**, and its management controls are the most prominent repeated element on a screen that has no completion affordance of its own.

#### 8.7 Recommendations — layout

1. **Make the Hero shell, not the Hero screen.** *Kind of change:* extract the artwork + copy + readability treatment into one reusable collapsing header with a documented contract, and let Home and Habits both use it. The repo already names `HabitsHero` the reference composition — this is a case where the codebase's own conclusion should be followed rather than re-litigated on the harder screen.
2. **Decide the Hero's headline and size the type to it** (§8.2). This is a typographic decision with a layout consequence, not a layout fix.
3. **Give the empty day a rule** (§8.3).
4. **One inset contract at the Scaffold** (§8.4). Fixing the five 40 dp titles is a one-line change per screen; deciding *where* insets are owned is the structural part.
5. **Make `ProgressOrb` a general primitive or stop calling it the signature component.** As long as one bespoke ring serves one screen, the product has seven progress vocabularies.
6. **Fix row height variance** with a reserved metadata line rather than a conditional one, so a Home list reads as a rhythm.

---


---

## 9. Component Audit

#### 9.1 What the component layer actually is, and who uses it

`presentation/components/` is ten files. The production usage map (call sites, excluding definitions, previews and comments):

| Component | Production call sites | Distinct screens | Note |
|---|---|---|---|
| `MomentumSectionHeader` | **29** | 11 | the real workhorse of the app |
| `CategoryChip` | **20** | 6 | filters *and* two settings enums *and* creation pickers |
| `HabitIconTile` | 13 | 3 | |
| `LiquidGlassIconButton` | 12 | 6 | vs **18 raw `IconButton(`** in 10 files |
| `MomentumTopBar` | 10 | 9 | |
| `EmptyState` | 6 | 4 | |
| `StatCard` | 7 | 2 | |
| `ActivityCard` | 5 | 3 | |
| `StreakBadge` | 4 | 1 (`ActivityCard`) | |
| `ProgressOrb` | 3 | **1** | |
| `AuroraBackground` | 3 | 3 | |
| `MomentumHeroArtwork` | 2 | 2 | |
| `MomentumCard` | **1** | 1 | `LiquidGlassCard` has 1 (its own delegate) |
| `AppBackground` | 1 | 1 (the shell) | |

`MomentumCard` — the component the design system names as *the card* — has **one** production call site (Insights), and is a zero-logic delegate to `LiquidGlassCard` (`MomentumComponents.kt:133-145`). The other card-like surfaces in the app are built by calling `Modifier.liquidGlass(...)` directly and wrapping a local `Column`: `HabitCard` (`HabitsScreen.kt:293`), the Insights category card (`InsightsScreen.kt:161`), the Insights top-activities card (`:195`), `MetaCard` (`HabitDetailScreen.kt:436`), the Create Item options (`CreateItemSheet.kt:100-105`) and the Calendar day cell (`CalendarScreen.kt:238`).

**What is wrong:** the library's names and its usage have diverged. Six distinct card constructions exist; the one with a canonical name is nearly unused. `MomentumCard`/`LiquidGlassCard` being two names for one thing is the visible symptom of a deeper problem: **there is no card primitive, only a surface modifier, and every screen decided for itself what a card is.**

#### 9.2 Component duplication, measured

| Duplication | Evidence |
|---|---|
| Card | `MomentumCard` == `LiquidGlassCard`; 5 further inline constructions |
| Button | `MomentumButton` (4 sites) delegates to `LiquidGlassButton` (4 sites); a third variant in `TargetEntryDialog` (`glassFieldColors()`) and a fourth in `OnboardingLiquidGlass` |
| FAB | `QuickAddFab` (`TodayScreen.kt:368-391`) and `AddHabitFab` (`HabitsScreen.kt:227-269`) — **same 56 dp, opposite materials** (§9.4) |
| Press feedback | `glassPress` (`LiquidGlass.kt:142`) and a hand-rolled `spring(stiffness = StiffnessMedium)` at `OnboardingLiquidGlass.kt:144,240` with a *different* pressed scale (0.96 vs 0.97) |
| Icon container | **4** treatments: 48 dp circle glass, 44 dp 8 dp-square accent, 52 dp 8 dp-square primary, 40 dp 8 dp-square primary |
| Progress track | **3** tokens for one job: `surfaceVariant` (`MomentumComponents.kt:275`), `surfaceVariant@0.55` (`InsightsScreen.kt:254`), `textPrimary@0.55` (`ProgressOrb.kt:116`) — and `progressTrack`, designed for it, used **0** times |
| Label | 29 `MomentumSectionHeader` sites, but the *sheet title* (`CreateItemSheet.kt:71`), the *settings group label* (5x on Settings) and the *screen title* are all the same 16 sp Regular |
| Destructive confirm | 5 `MomentumDialog` sites, **3 different dismiss labels** ("Keep" x2, "Cancel" x2, "Keep my data"), all translucent |
| Date/time format | 4 approaches: `getDisplayName(TextStyle.SHORT, …)` (`InsightsScreen.kt:283`), `ofLocalizedTime(FormatStyle.SHORT)` (`TodayScreen.kt:417`), `ofPattern("MMM yyyy", Locale.getDefault())` (`CalendarScreen.kt:110`), `ofPattern("MMM d yyyy")` with no locale (`ActivityDetailScreen.kt:259-260`), plus a shared `HabitDateFmt` |
| Weekday label | 4 sites: `getDisplayName(SHORT, Locale)` (Insights, correct) vs `dayOfWeek.name.take(1)` (Calendar `:150`, ActivityDetail `:323`, HabitDetail `:547`) |
| Icon-tile gradient | 3 hand-copied copies of `0f to Color.White@0.12/0.14 → 1f to c.primary@0.16` (`ActivityDetailScreen.kt:179-183`, `InsightsScreen.kt:207-211`, and `HabitIconTile.kt:52-57`) |
| Empty state | 4 instances, 4 different Material glyphs as the illustration (`Add`, `Add`, `BarChart`, `Star`) |
| Dialog | 2 implementations (`MomentumDialog` translucent, `TargetEntryDialog` opaque + scrim) with a test asserting the difference |

#### 9.3 Two component languages, side by side

`liquidGlass` and the design system deliberately use `indication = null` plus a 0.97 press-scale (`LiquidGlass.kt:170-178, 255-262, 288-297, 366-373`) — i.e. **the design system suppresses the platform's touch feedback everywhere.** Against that, the app contains:

- **18 raw `IconButton(`** call sites across 10 files, each carrying the default M3 ripple unless overridden. Examples: the Habits overflow menu (`HabitsScreen.kt:326-335`), the Habits card itself (`:295`, default `clickable` indication), Habit Detail's back button (`:219-232`).
- `ripple(bounded = true)` on `CompletionButton` (`CompletionButton.kt:113`).
- Default `clickable(onClick = …)` with no `indication` override on `ActivityCard.kt:86`, `CreateItemSheet.kt:106`, `CalendarScreen.kt:127`, `ActivityDetailScreen.kt:398`.

**Why this is wrong:** the product has suppressed the one feedback mechanism every Android user already understands, and then re-enabled it in the highest-traffic interactive elements. On Home — the screen with the most touch interaction — the row and the completion disc *ripple* while the FAB, the nav bar and the glass cards *scale*. Three different press behaviours inside one screen. **Familiarity has become genericity here because the familiar thing and the own thing are interleaved rather than composed**, so the user gets neither a consistent platform idiom nor a consistent Momentum one.

This is the §7.4.7 row "Material ripple vs press-scale", now with its full measured extent: 18 raw `IconButton`s + 4 un-overridden `clickable`s against 6 glass-press components.

#### 9.4 The two FABs are opposites

| | `QuickAddFab` (Home) | `AddHabitFab` (Habits) |
|---|---|---|
| Size | 56 dp (`TodayScreen.kt:359`) | 56 dp (`HabitsScreen.kt:236`) |
| Surface | `liquidGlass(CircleShape, GlassLevel.HERO)` — translucent | solid `c.primary` gradient 0.95 -> 0.92 |
| Shadow | **none** | 12 dp, accent spot, `Black@0.30` ambient |
| Border | from `liquidGlass` | `White@0.28` hairline |
| Glyph | `MomentumIconSizes.large` = **20 dp**, `c.primary` | `MomentumIconSizes.prominent` = **28 dp**, `c.onPrimary` |
| Feedback | `glassPress` scale | `glassPress` scale |
| Role | the **only** add affordance on Home | the **only** add affordance on Habits |

Two identical-purpose controls, in the two most important screens, one a ghost outline and one a solid glowing disc — and the Home one carries a **smaller** glyph inside the **same** circle. A user switching tabs sees the primary action of the app change material, size and contrast with no reason. `QuickAddTest.kt` asserts Home's is `liquidGlass(CircleShape, GlassLevel.HERO)`; nothing asserts Habits' is solid, so the divergence is not a locked decision — it is two independent implementations.

ADR-27 records that `HABITS_MANAGEMENT_UI_SPEC.md` §3 forbade a second FAB (*"The header `+` opens the existing Create Item flow. Do not introduce a second floating action button."*) and that "prompt won". So the spec's design — a `+` in the header — was replaced by a second FAB, and the spec's header `+` does not exist.

#### 9.5 Components that are genuinely good

Named, with the reason they deserve survival (expanded in §7.20):

- **`CompletionButton`** — 48 dp, `Role.Checkbox`, a spring pop, a `LongPress` haptic on completion, managed ripple indication, and a `contentDescription` that names the *action* (`ActivityCard.kt:188-197`). The only control in the app with a complete, deliberate state machine.
- **`HabitDetailScreen.DayCell`** (`HabitDetailScreen.kt:496-556`) — the only three-state data mark in the product, correctly distinguishing "not scheduled" from "missed" per `HABITS_MANAGEMENT_UI_SPEC.md` §6. It is the template the Calendar and the Insights bars should have been built from.
- **`MetaCard`** (`HabitDetailScreen.kt:427-456`) — the only place in the app where a label is correctly *smaller* than its value (12 sp label, 14 sp SemiBold value). It proves the reversed ordering elsewhere is an accident.
- **`MomentumHeroArtwork`** — a real, documented caller contract; one artwork pair for the whole product; theme-only selection; the KDoc is honest that no blur exists and that `topDissolve` generates no stop.
- **`MomentumTopBar`, `MomentumSectionHeader, MomentumSettingRow, MomentumDivider, EmptyState, CategoryChip`** — consistent, small, correctly-styled, and used 68 times between them. These are the library working as intended.

#### 9.6 Components that are weak or wrong

- **`ProgressOrb`** — 4 stacked arcs and a permanent infinite loop to render "62%" (§7.7.6). Its KDoc says *"Compact by default — progress should inform, not dominate"*; its implementation is a living light. **KDoc and behaviour disagree in the same file.**
- **`MomentumDialog`** — uses `GlassLevel.HERO` (0.68 translucent) despite `GlassLevel.DIALOG` existing precisely for this, and is used for **all five** dialogs in the app — **all five of which are destructive confirmations** (corrected per the Reviewer's C-4; the original text said "four"). `TargetEntryDialogSurfaceTest` proves the team diagnosed this exact defect for the bespoke dialog and fixed it there.
- **`EmptyState`** — takes an `ImageVector?` and every caller passes a Material glyph that names the subject (`BarChart` on Insights, `Star` on Home, `Add` twice). `HABITS_MANAGEMENT_UI_SPEC.md` §2 asks for *"a small calm Aurora illustration"*; the repo has no illustration asset, so the component's `icon` parameter is a stand-in for a capability that does not exist. It also derives a fifth surface level, `cardFill * 0.7f` (§7.7.2).
- **`CategoryChip`** — one pill component serving four unrelated jobs (list filter, dark-mode enum, week-start enum, creation option). Nothing in its design or API distinguishes a filter from a setting.
- **`StreakBadge`** — a **fire emoji's visual equivalent in Material form**: `Icons.Rounded.LocalFireDepartment` for the "streak" key, tinted `amber`, at `labelMedium` 12 sp. The design doc §490-494 says explicitly *"Prefer: 21 day streak — over highly gamified fire/emoji treatments"*, and §33 rejects "fire-streak gamification". ADR-27 records that `HABITS_MANAGEMENT_UI_SPEC.md` §7's example used a fire glyph, that the design system rejects emoji, and that the resolution was "we use the Material fire icon" — i.e. one form of the rejected pattern was substituted for another.
- **`MomentumSectionHeader`** — 29 call sites at 16 sp Regular, also used as a *sheet title* and 5 *settings group labels*. Because it is the app's most-used label and its style is indistinguishable from body text and from its sibling settings-row titles, **the app has 29 section labels and the user cannot see any of them.**

#### 9.7 Recommendations — components

1. **Decide whether `MomentumCard` is the card or the modifier is.** Six constructions exist; pick one and make the other a delegate. *Kind of change:* a single card primitive that takes content and owns fill/border/radius/padding, so screens stop assembling surfaces.
2. **One press-feedback language, chosen deliberately.** Either keep `indication = null` + scale everywhere (and override the 18 raw `IconButton`s), or adopt the platform ripple everywhere (and drop `glassPress`). Adopting the ripple is a legitimate answer for an Android product; the current state, where both are present, is not.
3. **One FAB component**, used by Home, Habits and any future floating action, with one material and one glyph size.
4. **One card-metadata contract.** Four hand-built concatenations (`homeMetadataLabel`, `habitMetadata`, the inline `buildString` in Activities and in Calendar) should become one component that takes discrete fields, so slots can be individually weighted and truncated independently.
5. **One dialog component with one dismiss verb and one surface level.** Three dismiss labels and five translucent dialogs, four of them destructive, is the highest-consequence inconsistency in the component layer.
6. **Reconcile the icon-container family** (four treatments) or state the rule that produces them.
7. **Fix the two things a test already forbids elsewhere:** make `MomentumDialog` opaque, and make `EmptyState` accept something other than a Material glyph — or rename the parameter so it does not promise illustration.

---


---

## 10. Habit Experience Audit

#### 10.1 The division of labour is correct and should be preserved

`HABITS_MANAGEMENT_UI_SPEC.md` §14 fixes the product's clearest rule: *"Habits = manage. Home = act today. Details / Insights = understand over time."* The code honours it structurally, and this is the strongest product decision in the repository:

- **Habits list**: no completion control, no streak, no progress, no history (`HABITS_MANAGEMENT_UI_SPEC.md` §78-79, and `HabitsScreen.kt:272-274` states it in the KDoc). Verified: `HabitsScreen` contains no `CompletionButton` and no `ProgressOrb`.
- **Home**: no management controls beyond the completion disc.
- **Habit Detail**: weekly completion + streaks + configuration, and the identity tile is the only place a habit is enlarged (`HabitIconTile(size = 72.dp, iconSize = prominent)`, `HabitDetailScreen.kt:322-327`).

This is a genuine strength and Step 2 should treat it as a fixed constraint, not a variable.

#### 10.2 But the two habits surfaces show *different data about the same entity* at *smaller* sizes on the detail screen

| | Habits list (`HabitCard`) | Habit Detail |
|---|---|---|
| Name type | `titleMedium` 16 sp SemiBold (`HabitsScreen.kt:308-310`) | `headlineSmall` + unregistered `Bold` (`HabitDetailScreen.kt:349-353`) |
| Icon tile | 44 dp, 8 dp radius, `habitIconAccent` | **72 dp**, 8 dp radius, `habitIconAccent` + a 24 dp custom-edit badge |
| Status | `● Active` / `● Paused`, `warning`/`success` dot, `labelMedium` | same, at `labelLarge` (`:379`) |
| Frequency/Target/Reminder | 3-slot `—`-padded string at `bodySmall` 12 sp | 3 equal-height `MetaCard`s, label 12 sp / value **14 sp** |
| Progress | **none** (correct per spec) | 7-day 3-state strip + `n/m` at **13 sp** |
| Streak | **none** (correct per spec §7) | fire tile + current at **16 sp**, longest at **12 sp** |

The list card presents the habit's configuration in one 12 sp line. The detail screen presents it in three cards with 14 sp values. **The information the user came to the detail screen for is presented in a smaller type size than the habit's name on the list** — and the streak, which the product is named for, is 16 sp on the detail page and 12 sp on the card. This is §7.5.3's collapse showing up as a *cross-screen* inconsistency, and it is the clearest single statement of the numeric-layer problem.

#### 10.3 Paused state: the same mistake, made twice, after it was learned once

`ActivityCard.kt:49-53` documents the rule explicitly:

> *"The row never dims its own text: fading the whole card dropped body-text contrast below 4.5:1 in the light theme (DASHBOARD_SPECS §21, AGENTS §9), so 'quieter' is expressed through the surface instead of an alpha layer."*

Against that, `HabitCard` applies **`.alpha(if (paused) 0.85f else 1f)` to the entire card** (`HabitsScreen.kt:292`), and `HabitDetailScreen` applies the same `.alpha(0.85f)` to the `IdentityHero` (`:178`) and the `MetaCardsRow` (`:187`). `HabitIconTile` even has a `deEmphasized` parameter that dims the *icon* locally (`:54, 61, 70`) — the correct approach — and then the caller dims the whole card anyway.

`HABITS_MANAGEMENT_UI_SPEC.md` §84-90 asks for *"slightly reduced visual emphasis"* and says *"Do not gray out or disable the entire card."* 0.85 on the whole card is greying out the whole card, including a 16 sp name and a 12 sp metadata line.

**Why it is wrong:** the lesson was learned on one screen and not transferred. **User effect:** a paused habit's name and metadata sit at reduced contrast in Light — the exact failure `DASHBOARD_SPECS` §21 forbids — and the user has to open the habit to read it. **What KIND of change would solve it:** one de-emphasis mechanism (surface + icon), applied everywhere, with whole-composite alpha removed from all card rows.

#### 10.4 Two management models for the same two actions

**Habits list** (`HABITS_MANAGEMENT_UI_SPEC.md` §92-104): tap the overflow, get a `DropdownMenu` with Pause/Delete (or Resume/Delete), then a `MomentumDialog`. The menu is a **raw M3 `DropdownMenu`/`DropdownMenuItem`** (`HabitsScreen.kt:336-361`) — M3 surface, M3 shape, M3 item height, default M3 colours.

**Habit Detail** (spec §10, §281-302): a centered bottom floating group `[Pause/Resume] [Edit Habit] [Delete]` as three `LiquidGlassButton`s (`HabitDetailScreen.kt:247-276`), Delete destructive.

The *same two actions* are a dropdown menu on one screen and a bottom button group on the other, with different components, different materials and different interaction models. The spec specifies each screen separately, so this is not a spec violation — but it is a real product inconsistency, and it is a *navigation-model* question rather than a styling one (§14, §19).

The Habit Detail group also does not meet its own spec: §298 asks Delete to be *"visually separated enough to prevent accidental deletion."* It is a 52 dp glass button 8 dp from a 52 dp glass button, same radius, same fill mechanics, separated only by a `c.danger` tint (`LiquidGlass.kt:201, 222-228`).

#### 10.5 The habit's *identity* system, restated with the creation flow included

§7.4 §4.4 found that per-activity accent identity reaches 3 surfaces. Adding the creation flow reveals *why* that number is itself fragile: **a second, independent colour map keyed on a different key space** —

`HabitConfigScreen.kt:410-422` — `templateAccent(templateId)` maps 9 template ids to `HabitIconAccents`, with `else -> peach`.

Cross-referencing the two maps against `HabitTemplates.kt`:

| Template | `templateAccent` | template `iconKey` | `habitIconAccent(iconKey)` | Agree? |
|---|---|---|---|---|
| `build_read` | amber | `learning` | amber | yes |
| `build_exercise` | teal | `fitness` | teal | yes |
| `build_meditate` | lavender | `mind` | lavender | yes |
| `build_water` | cyan | `water` | cyan | yes |
| `build_learn` | golden | `school` | golden | yes |
| `reduce_social` | plum | `people` | plum | yes |
| `reduce_sugar` | amber | `food` | amber | yes |
| `reduce_spending` | golden | (spending) | golden | yes |
| `reduce_screentime` | cyan | — | — | yes |
| `custom_build` / `custom_reduce` | **peach** (`else`) | **`spark`** | **peach** (`else`) | yes |

**They agree today, for all 10 shipped templates — by hand-maintenance, not by derivation.** Two `when` expressions over two different key spaces (`templateId` vs `iconKey`), both falling back to `peach`, with nothing tying them together. Change a template's `iconKey` and its creation-grid colour silently diverges from its list-tile colour, with no test.

And the divergence that *already exists* is the most important one: **the app's own "Custom habit" template ships with `iconKey = "spark"`, which `momentumIcon` cannot draw** (falls through to `Icons.Rounded.Star`, `MomentumIcons.kt:94`) and which `habitIconAccent` does not name (falls through to `peach`). The most consequential choice in the creation flow — "build your own" — is represented by a fallback star in a fallback colour. `HABITS_MANAGEMENT_UI_SPEC.md` §160-163 explicitly distinguishes fixed icons for predefined habits from *editable* icons for custom habits, and the edit affordance is a 24 dp badge over the tile (`HabitDetailScreen.kt:328-347`).

#### 10.6 What data a habit can actually show

`HabitOverview` (`domain/usecase/HabitOverview.kt:37-41`) is the **entire** historical contract for a habit: a 7-day completion window of `{COMPLETED, INCOMPLETE, NOT_SCHEDULED}` plus two counts, `currentStreak`, `longestStreak`. Two consequences, both load-bearing for §19 and §23:

1. **`ComputeHabitOverviewUseCase` receives `completionDates: Set<LocalDate>`** (`:54`) — dates only. `CompletionRecord.value` is discarded at the use-case boundary. **A "drink 8 glasses" habit's detail page cannot show "you averaged 6.2"**, because the per-day actual never leaves the repository. The partial-completion data the product stores and the domain models (`PRODUCT_SPEC.md` §72) is structurally unavailable to this screen. This is a *data shape* constraint, not a UI choice, and it must be visible to anyone proposing a richer habit view.
2. **There is no window parameter.** `weekWindow(today, weekStart)` is hardcoded to 7 days (`:104-112`). A habit detail page cannot show a month, a quarter, or "since I started" without a new use case. The only place in the app showing more than 7 days of history is `ActivityDetailScreen.kt:283` — `state.stats.scheduled.takeLast(14)` — and that is on the *task* detail screen.

#### 10.7 Recommendations — habit experience

1. **Keep the manage/act/understand division.** It is the clearest product decision in the repo (§10.1) and Step 2 should treat it as fixed.
2. **One de-emphasis mechanism** for paused, applied on both habits surfaces, with whole-card alpha removed (§10.3).
3. **One management model** for pause/resume/delete, or a stated reason for two (§10.4).
4. **Derive the accent, don't duplicate it** — one map, one key space (§10.5). And give `spark` a real glyph, because the app's own custom template depends on it.
5. **Decide the data question before designing the habit view** (§10.6): whether a habit's history is 7 days or unbounded, and whether per-day *values* are in scope. This is a product question that must be answered before a layout is drawn, and it belongs in §23.
6. **Raise the detail screen's data types above the list's name type** (§10.2).

---


---

## 11. Tasks Audit

#### 11.1 Where Tasks actually lives

**Tasks is not a distinct surface.** `MomentumNavGraph.kt:222-230` routes `Routes.TASKS` to the shared activities list with a type filter:

```kotlin
composable(Routes.TASKS) {
    ActivitiesRoute(type = ActivityType.TASK, title = "Tasks", ...)
}
```

`ActivitiesScreen.kt` is therefore the Tasks screen, the Activities screen, and (for archived/paused rows) a detail-management surface. `HABITS_MANAGEMENT_UI_SPEC.md` §135 says *"Do not redesign or change the existing Tasks destination as part of Phase 2B"* — so this is a *recorded* shared-surface decision, not an oversight. The nav comment at `MomentumNavGraph.kt:213-214` confirms the intent: *"Habits is the dedicated management screen (Phase 2B / ADR-22). Tasks keeps the shared Activities list."*

**This is the central fact of the Tasks audit, and it has three consequences the user experiences directly.**

#### 11.2 Consequence 1: the empty Tasks tab says "activities"

`ActivitiesScreen.kt:183-195`. The `EmptyState` title is selected by tab, and the `ACTIVE` branch is `"No activities yet"`. On the **Tasks** tab with no tasks, the user reads:

> **No activities yet**
> Create something worth showing up for.
> **[Create activity]**

Three problems, all checkable:
- The word **"activities"** appears on a tab labelled **"Tasks"** — the shared surface's vocabulary leaks through the filter.
- The CTA says **"Create activity"** on the Tasks tab, while the FAB's `contentDescription` says **"Add activity"** (`:77`) and the sheet it opens is titled **"Create item"** with options "Habit" / "Task" (`CreateItemSheet.kt:71-85`). **Four different names for the same action across three surfaces.**
- The body, *"Create something worth showing up for,"* is the only unhedged motivational line in a list context, and it names nothing about the user.

`DASHBOARD_SPECS.md` §211-212 and `HABITS_MANAGEMENT_UI_SPEC.md` §106-114 both require an empty state that is *calm, useful and inviting*. This one is generic and mislabelled.

#### 11.3 Consequence 2: the Tasks list is twice as heavy as the Home list

For every row, `ActivitiesScreen.kt:226-266` renders a right-aligned `Row` of up to two `LiquidGlassIconButton`s — 48 dp each — **below** the card and **outside** it (the card is `ActivityCard`'s own glass; the buttons sit on the page background).

So: Home's task row = one 48 dp completion disc, inside one card. Tasks' row = the same card, plus a 48 dp row of unlabelled circular glass buttons (Pause / Archive, or Resume / Unarchive / Delete) beneath it.

**Why it is wrong:** `DASHBOARD_SPECS.md` §16 is explicit that the completion control is *"the dedicated action for changing completion state"* and that rows communicate state without navigation symbols. Tasks' row has no completion control at all — it has management controls — yet it is the heaviest row in the app. A list of 10 tasks is 10 cards + 10 unlabelled 48 dp glass circles, and **two of those circles (Pause and Archive) are visually identical**, distinguished only by glyph. **User effect:** the management surface of a task is louder than the action surface of a habit, and the two 48 dp circles invite mis-taps with no labels.

**What KIND of change would solve it:** a decision about where task management lives. Either Tasks becomes a *management* surface (like Habits) with a proper overflow and no per-row button bar — which is what the architecture already implies — or the button bar collapses to a single overflow affordance.

#### 11.4 Consequence 3: Tasks carries the app's only search and its only raw text field

`ActivitiesScreen.kt:129-146` is the **only** `OutlinedTextField` with hand-set colours, and the app's only search. It is a generic M3 outlined field with `unfocusedContainerColor` / `focusedContainerColor` / border colours / cursor colour overridden — but **not** `unfocusedTextColor`, `focusedTextColor`, `labelColor`, or `disabledTextColor`. Those fall through to the library defaults of an M3 scheme whose unmapped slots do not belong to the Aurora palette (§7.6.3). **So the only text the user can type in the entire product is coloured by a Material baseline, not by a Momentum ink token.** Design doc §270: *"Use soft tonal fields rather than generic Material outlined fields everywhere."*

The screen then stacks **two visually identical `CategoryChip` rows** — status (Active/Paused/Archived) at `:147-165` and category (All + each) at `:166-182` — with no label distinguishing the axes. `HABITS_MANAGEMENT_UI_SPEC.md` §50 records that for Habits the decision was *"No search or filter controls in the initial release."* Tasks therefore ships the filter surface that Habits was explicitly denied, on the shared component, with no axis labelling.

#### 11.5 The third hard delete, and the archive alternative

`ActivitiesScreen.kt:99-108`: `MomentumDialog(title = "Delete permanently?", body = "History and reminders for this activity are deleted too. Archive keeps history.", dismissLabel = "Keep")`. The body is genuinely good — it names the consequence *and* names the alternative. But the dismiss verb is **"Keep"**, while `HabitsScreen.kt:215` uses **"Cancel"** and `SettingsScreen.kt:281` uses **"Keep my data"** for the same slot.

**The hard-delete governance question.** `vm.deleteHard(id)` is called from three places (`ActivitiesScreen.kt:103`, `HabitsScreen.kt:214`, `ActivityDetailScreen.kt:114`), reaching `container.activityRepository.deleteHard(id)` (`HabitsViewModel.kt:96`). `AGENTS.md` §7 states: *"Deleting an activity soft-deletes (ARCHIVED) by default in v1; hard delete needs explicit UX + ADR."* The UX is present and explicit in all three dialogs. **Whether an ADR authorises the hard-delete path itself is not something this audit can determine** — ADR-25 is titled *"Restore path for archived activities"* and concerns soft delete. I am flagging it as a governance item for the Orchestrator, not as a design finding, and it is carried into §7.28.

#### 11.6 What Tasks gets right

- The shared row means tasks and habits *look* like the same kind of object, which is `MOMENTUM_DESIGN_SYSTEM_V1.md` §514-524 (*"Tasks share Momentum's visual grammar with habits"*) delivered literally.
- `ActivityCard(streak = 0, ...)` (`ActivitiesScreen.kt:218`) correctly suppresses the streak badge for tasks, using the component's own parameter rather than hiding the element.
- Recurrence and category in the card's metadata line (`:219-222`) is genuinely useful on a management list and correctly *not* on Home (`TodayScreen.kt:409-410` documents that decision).
- Three tabs (Active/Paused/Archived) with archive-and-restore as a first-class path is a better model than delete-only, and the dialog names it.

#### 11.7 Recommendations — tasks

1. **Decide what the Tasks tab is.** It is currently a management surface wearing a tab's label. *Kind of change:* either (a) make it a *task-action* surface matching Home's row model with management moved to detail — which the architecture already implies — or (b) accept it as management and give it management affordances (an overflow like Habits, a labelled axis for the filter rows, no button bar under every row). Either is defensible. What is not defensible is a tab that says "Tasks" and speaks about "activities".
2. **Fix the vocabulary.** One noun per surface, one verb per action. *Kind of change:* a copy pass that makes Tasks say Tasks, and one add-action name used everywhere.
3. **Do not ship the app's only text field on unmapped M3 colours.**
4. **Label the two filter axes**, or merge them.

---


---

## 12. Insights Audit

#### 12.1 The screen computes 60 days and draws 7

`InsightsViewModel`: `from = today.minusDays(60)` and `completions.observeInRange(from, today)`; then `last7Days = (0..6).map { ... }`.

`InsightsScreen.kt:252-292` renders `WeeklyBars(days = state.last7Days)` — **7 bars.**

**The screen reads two months of completion history and visualises one week of it.** Precisely: `InsightsViewModel.kt:43` loads 60 days (`:46` `observeInRange(from, today)`), but **the first 30 of those 60 are consumed** by the 30-day rate (`:80` `windowRate(today.minusDays(29), today)`) and by the per-activity 30-day category and top-activity windows (`:98, :112, :119`). **Only days 31–60 are genuinely discarded at the view boundary.** The *narrow* claim is exact and is the one that matters: the chart itself draws 7 bars (`InsightsScreen.kt:153` `WeeklyBars(days = state.last7Days)`, function at `:252`).

> **CORRECTION (F-8, Reviewer-verified MINOR).** The original text said "the data for the other **53 days** … is discarded at the view boundary". That overstated absence: 30 of the 60 are used, and only days 31–60 are dropped. Corrected above.

`MOMENTUM_DESIGN_SYSTEM_V1.md` §578-596 defines Insights as *"historical progress, trends, habit analysis, patterns"* with a permitted vocabulary of *"line, bar, progress, calendar grid, distribution"* and the hierarchy `Question/headline → Key number → Visualisation → Interpretation`. **The screen supplies one level well and one partially.** The key number is there (40 sp, §12.2). The interpretation is *partially* there: `InsightsScreen.kt:121-130` renders a comparison line — `"+$delta pts vs 30-day average"` / `"$delta pts vs 30-day average"` / `"Even with your 30-day average"` — tinted `c.success` when `delta >= 0` and `c.warning` when negative (`:128`). That is a real comparison cue and a one-line interpretation, and it is the **same line** as the `success`/`warning` trend-polarity claim in §7.6 §6.4 — the two should be read together, not as separate findings. What is genuinely absent is the question the screen is answering, any trend *mark*, any distribution, and any explanation of *why* a number is what it is beyond a single delta.

*(Second F-8 correction: the original text asserted "no interpretation … of why a number is what it is" and §12.5 asserted "no comparison cue". Both are overstated — the delta line is exactly a comparison cue. §12.5 has been corrected to match.)*

#### 12.2 The one good number in the product is the least important one

`InsightsScreen.kt:117-119` renders `"${weekRate*100}%"` at `MaterialTheme.typography.displayLarge` = **40 sp SemiBold**.

That is the **only** figure in the entire app set larger than 20 sp. Compare §7.5.3 and §8.2: Home's *today* progress percentage is 20 sp with an out-of-family weight; the streak count is 12 sp; a `StatCard` value is 20 sp; a category rate is 20 sp with an out-of-family weight; the Calendar's "4 of 7 complete" is 12 sp; Habit Detail's "4/7 completed" is 13 sp; `homeMetadataLabel`'s live "5/8 glasses" — the most decision-relevant number in a habit's day — is 12 sp.

**The 7-day rate is 40 sp. Today's completion is 20 sp. The product is 2x more emphatic about the past week than about right now.**

**Why it is wrong:** the design system gives `Display` a size and the screen that reached for it first was an analytics summary. Priority followed opportunity, not meaning. **User effect:** the app's loudest number answers a question the user did not come to ask, and the number they *did* come for is body text. **What KIND of change would solve it:** a numeric hierarchy assigned by *meaning* (now > this week > this month) rather than by *availability of a big style* (§7.5 rec. 1, §22).

#### 12.3 Four data-mark vocabularies on one screen

`InsightsScreen.kt` draws data four different ways in 292 lines:

1. **`WeeklyBars`** (`:252-292`) — `Canvas`, 7 vertical bars, `cornerRadius = barWidth/2` (fully rounded), track `c.surfaceVariant@0.55`, fill `c.primary`, height-proportional, day labels below in `labelMedium`. **No value labels, no axis, no maximum, no gridline.** The user cannot read a number off this chart at all; they can only compare bar heights.
2. **`MomentumLinearProgress`** (`:179`, per category) — 6 dp rounded bar, `trackColor = c.surfaceVariant`, fill `c.primary`, `strokeCap = Round`. `InsightsScreen.kt:254` then builds its *own* track for the *other* visualisation in the same screen.
3. **`StatCard`** (`:135-144`) — no track, no scale, value 20 sp + label 12 sp.
4. **The hero card's percentage** (`:117`) — no mark at all, just a 40 sp number in a `GlassLevel.HERO` card.

Plus, elsewhere: `ProgressOrb` (ring), `DayCell` (3-state circles), the Calendar's 42-cell grid. **The product has no shared data-mark vocabulary**, and the one screen explicitly about analysis is the one with the most variety inside it.

Design doc §596 rejects *"rainbow charts, 3D charts, and decorative visualization"* — and this is none of those. It is four parallel, un-reconciled marks. The failure is not decoration; it is the absence of a mark system.

#### 12.4 The scheduled/empty distinction is available and discarded — twice

`DayRate(date, rate, scheduled)` carries `scheduled: Int`. `WeeklyBars` uses only `rate` (`:269`), so **a day with nothing scheduled and a day with 5 scheduled and 0 completed draw the same empty bar.**

`CalendarScreen.DayCell` has the same shape: `cell.scheduled` is used in the `contentDescription` (`:230`) but the four-branch `wash` (`:222-227`) tests `isSelected`, `fraction >= 1f`, `fraction > 0f`, else transparent. **There is no branch for `scheduled > 0 && fraction == 0`** — a fully-missed day is drawn exactly like a day with nothing scheduled.

**Why this is wrong:** a progress visual that cannot distinguish "I did nothing" from "I had nothing to do" is not a progress visual. `DASHBOARD_SPECS.md` §17-18 makes the distinction foundational (an unscheduled day *"does not break a streak"*), `HABITS_MANAGEMENT_UI_SPEC.md` §193-195 makes it a hard requirement (*"a Mon/Wed/Fri Habit must not make Tue/Thu/Sat/Sun look missed"*), and `HabitDetailScreen.DayCell` implements it correctly. **The one visualisation that gets it right is the one on the least-analytical screen.** **User effect:** a user with a sparse schedule sees an Insights week and a Calendar month full of apparently-blank days and cannot tell slackeriness from rest.

#### 12.5 Category and activity rows: equal-weight, unranked lists

`By category` (`:156-189`): per category, a name at `titleMedium` 16 sp, a rate at `titleMedium` 16 sp + `FontWeight.Bold` (unregistered — fifth such site), a 6 dp bar, and "N of M complete" at `bodySmall`. Categories are listed in the order `live.groupBy { it.categoryId }` produces — **no sort is applied**, so the order is map-iteration order, effectively arbitrary. There is no ranking and no "weakest category" callout.

**Correction (F-8):** the original text here also claimed "no comparison cue". That was overstated. The **screen** does have one comparison cue — the "+N pts vs 30-day average" delta at `InsightsScreen.kt:121-130` — but it belongs to the hero number, not to the category or activity lists. Neither of those two lists has any comparison, ranking or baseline, and that narrower claim is what stands. The delta line is also the same line cited in §7.6 §6.4 for `success`/`warning` trend polarity.

`Most consistent` (`:190-246`): the section name *promises a ranking* and delivers an ordered list — but of the *best* performers only, with no contrast against the worst. `ActivityPerformance(rate, streak)` is available; the screen shows `"68% · 5 in a row"` at 12 sp.

These category icon tiles use `c.primary` (`:224`) at 40 dp — a fifth icon-container treatment, and not `habitIconAccent`, so the same activity is a jewel tile on Habits and a blue tile here (§7.4.4, now 4 surfaces).

**Why it is wrong:** these are the two lists where a *ranking* is the entire value, and neither is visually ranked. **User effect:** "By category" reads as a data dump. The user has to read all of it to learn that one category is worse than another.

#### 12.6 The app's spec'd analytics are computed and never shown

`PRODUCT_SPEC.md` §74-86 lists eight analytics. Measured availability:

| Spec'd metric | Engine | Displayed? |
|---|---|---|
| Daily/weekly/monthly completion (counts + %) | `ComputeActivityStatsUseCase`, `InsightsViewModel` | rate % only, no counts |
| Completion % over arbitrary windows | `rangeStart` param exists | 30 days fixed on detail, 7 on Insights |
| Current streak | `ActivityStats.currentStreak` | yes (detail, Insights) |
| Longest streak | `ActivityStats.longestStreak` | yes |
| Total completions | `ActivityStats.completedCount`, `InsightsUiState.totalCompletions` | yes |
| **Missed occurrences** | `ActivityStats.missedCount: Int` | **no — 0 call sites in `presentation/`** |
| **Consistency score** | `ActivityStats.consistencyScore: Double?`, documented "% of scheduled occurrences hit in last 30 days" | **no — 0 call sites** |
| **Week-over-week trend** | `Trend` enum | as a lowercased enum string only (`ActivityDetailScreen.kt:276`) |

**Two of the eight spec'd headline metrics are computed by a pure, unit-tested, zero-Android-import engine and are not visible on any screen.** `consistencyScore` is *the* metric a consistency product should lead with, and `missedCount` is what makes a calendar or a heat view meaningful. Verified by grep: `missedCount` and `consistencyScore` appear only in `Engines.kt` and in tests.

And `Trend` is a 4-value enum — `IMPROVING, STEADY, DECLINING, INSUFFICIENT_DATA` (`Engines.kt:153`) — rendered as `trend.name.lowercase().replaceFirstChar(Char::titlecase).replace('_',' ')` at `ActivityDetailScreen.kt:276`. So the user can read the literal string **"Insufficient data"** as the value of a row labelled "Trend", and otherwise gets "Improving"/"Declining" with **no magnitude, no window and no visualisation** — the interpretation without the key number or the mark that the doc's §604 hierarchy requires.

#### 12.7 Recommendations — insights

1. **Decide the question Insights exists to answer** and build the screen from `Question → Key number → Mark → Interpretation` (§12.1). Currently it opens with a number and a bar chart.
2. **Show the 60 days it already loads** (or stop loading them). *Kind of change:* a window control and a mark that can express a series, or an honest 7-day framing that says so on screen.
3. **Encode scheduled-vs-empty in every mark** (§12.4). The data is already in `DayRate.scheduled` and `DayCell.scheduled`.
4. **Build one data-mark vocabulary** and use it for the bars, the category rows, the orb and the calendar. `HabitDetailScreen.DayCell` is the existing proof that the app can do three states well.
5. **Surface `consistencyScore` and `missedCount`.** They exist, they are tested, and the product spec names them. Not surfacing a spec'd, computed metric is a product gap, not a design one.
6. **Rank the category list**, or rename the section to something that does not promise a ranking it does not deliver.

---


---

## 13. Onboarding Audit

#### 13.1 The copy is the weakest surface in the product, and it is the first thing the user sees

All four pages (`OnboardingScreen.kt:81-102`):

| # | Headline | Body |
|---|---|---|
| 1 | "A more intentional you." | "Build healthy habits, track progress and create the life you want." |
| 2 | "Track what matters." | "Habits, routines, tasks, medicines and more, all in one calm and powerful app." |
| 3 | "**Stay Consistent**" | "Visual progress and insights help you keep going day after day." |
| 4 | "Ready to build Momentum?" | "Your journey to a brighter tomorrow starts now." |

Named problems, each checkable:

- **"Stay Consistent"** is the only headline that is **Title Case with no terminal punctuation**, among three sentence-case headlines that all end in a full stop or question mark (`:93-94`).
- **"all in one calm and powerful app"** (`:90`) — the app describes *itself*, in third person, inside its own UI, and **"calm and powerful" is a self-contradictory marketing pair**. It is the clearest AI-copy tell in the repository.
- **"create the life you want"** (`:85`) and **"Your journey to a brighter tomorrow starts now."** (`:101`) — stock self-help phrasing. "journey", "brighter tomorrow", "starts now".
- **"Build healthy habits"** (`:85`) is a category claim the product does not make; it is a habit tracker, not a health product.
- **Zero of the four bodies contains a single fact about what the app does.** No mention of the Activity concept, streaks, progress as a mechanic, offline operation, on-device storage, no account, no cloud, no tracking. The product's strongest differentiators are stated **only** in `SettingsScreen.kt:268` — *"Your data stays on your device. No account, no cloud, no tracking — offline by design."* — a screen a first-time user reaches only by choosing Settings, which is behind an icon in the Home Hero's corner.

**Why it is wrong:** onboarding's job is to make the user want the product *and* to set an accurate expectation. This copy does neither: it is generic, internally inconsistent in register, and it spends its entire four-screen budget on moods while the one thing that would make a user choose *this* app over any other habit app — that it is private, offline and has no account — is never mentioned. **User effect:** a user finishes onboarding with a vague sense of wellness and no idea Momentum is offline-first; a user who *would* have chosen it for that reason never learns it exists. **What KIND of change would solve it:** write onboarding against the product's actual differentiators and the Activity concept, in one register, and put the offline/privacy fact in front of the user before they commit.

#### 13.2 The example-seeding path is dead code, not a live defect *(corrected — see §7.29)*

> **CORRECTION (F-1, Reviewer-verified BLOCKER).** The original version of this subsection claimed that *"the final CTA passes `true`"* and that a first-time user would find three activities they did not create. **That claim was false.** It is struck and replaced below. The original error is documented in §7.29 so the correction is auditable rather than silent.

`OnboardingViewModel.finish(seedExamples: Boolean, onDone: () -> Unit)` (`:56-62`) calls `seed()` when `seedExamples` is true, and `seed()` (`:64-72`) would call `container.activityRepository.upsert(...)` for three `Activity` rows — "Drink water" (8 glasses), "Read 20 pages" (20 pages), "Evening walk" — into the real repository.

**Both reachable call sites pass `false`:**
- `OnboardingScreen.kt:174` — `OnboardingGlassSkipButton(onClick = { vm.finish(false, onDone) })`
- `OnboardingScreen.kt:266` — the final CTA, `"Let's Get Started"`, preceded by the comment at `:261-263`: *"Single final CTA. Internally this is the clean 'fresh start' completion path — **no demo activities are ever injected into the user's real data**."*

A repository-wide search for `.finish(` returns only these two onboarding call sites plus `PendingIntent.finish()` in `notification/Reminders.kt:64,133`. **No call site anywhere passes `true`, so `seed()` and the three `Activity` literals are unreachable.**

**What this actually is: a maintenance observation, not a product defect.** A `Boolean` parameter on a `finish()` API that only ever receives one value, guarding a method that can never be called, is dead code with a misleading affordance — a reader of the ViewModel cannot tell from the signature that seeding is disabled. That is worth noting and worth cleaning up eventually.

**What it is *not*:** a consent failure, a privacy issue, or any user-visible behaviour. No user-facing copy, state, or data path is affected. The Reviewer classified B-3 as *refuted* and §6 of `task.md` bars opening an issue from it; **if an issue was opened from the original claim it must be withdrawn** (Orchestrator-owned, already notified).

*Residual risk worth naming without inflating it:* because the parameter exists and the `seed()` body is intact, a future change that passes `true` would ship undisclosed data writes with no copy warning. That is a reason to remove the dead branch, **not** evidence of a present defect.

#### 13.3 Onboarding is a different product: theme-blind, dark-only, its own ink, its own palette

Established in §7.9.18 — zero reads of `MomentumThemeColors` / `LocalMomentumColors` / `isDark` / `darkMode` in `OnboardingScreen.kt` or `OnboardingLiquidGlass.kt`; four WebPs with no Light variant; `Twilight`'s 14 constants plus three local hexes plus `PhotoShade`.

Consequence measured: **a user who has set Light Aurora, or whose phone is in Light mode, walks through four near-black photographic screens and then arrives at a light, cool-grey app.** The app has to hand-patch the platform chrome to cope: `OnboardingScreen.kt:120-132` force-sets `isAppearanceLightStatusBars = false` in a `DisposableEffect` and restores it on dispose. That code is *correct* and well-commented — and it exists precisely because onboarding does not know what theme it is in. It is the clearest single symptom of the theme-blindness.

Two further theme-independent artefacts:

- **The headline colour is not `textPrimary`.** `OnboardingScreen.kt:216` uses `Twilight.Moonlight` (`#FFF4E5`, a warm off-white). Onboarding has its own ink.
- **A bespoke per-page readability exception.** Page 1's headline gets `Shadow(Color.Black.copy(alpha = 0.35f), blurRadius = 12f)` and pages 2-4 get `null` (`:204-208`), justified in the KDoc as "the bright lower lake" on screen 1 only. So the app's first impression has a hand-placed drop shadow on one of four screens, on a font that carries no shadow anywhere else in the product.

#### 13.4 The icon set designed for onboarding is not in the app, and is not a set

`artwork/onboarding/` holds **8** SVGs. Measured:

| File | viewBox | paths | gradients | filters | size |
|---|---|---|---|---|---|
| `onboarding_better_you_icon.svg` | `0 0 1095 1095` | 9 | 0 | 0 | 397 KB |
| `onboarding_focus_icon.svg` | `0 0 1095 1095` | 10 | 0 | 0 | 541 KB |
| `onboarding_habits_icon.svg` | `0 0 1095 1095` | 9 | 0 | 0 | 449 KB |
| `onboarding_stress_icon.svg` | `0 0 1095 1095` | 30 | 0 | 0 | **2.16 MB** |
| `onboarding_wellness_icon.svg` | `0 0 1095 1095` | 8 | 0 | 0 | 602 KB |
| `onboarding_activity_icon.svg` | **`0 0 2048 2048`** | 24 | **36** | 0 | 39 KB |
| `onboarding_health_icon.svg` | **`0 0 96 96`** | 2 | 6 | **2** | **1.9 KB** |
| `onboarding_learning_icon.svg` | **`0 0 96 96`** | 3 | 6 | **2** | **2.1 KB** |

**Is it a coherent set? No, and it is not even one kind of thing:**
- **Three different coordinate systems** (1095², 2048², 96²). An icon set must share a viewBox to be a set.
- **Two entirely different construction techniques.** Five flat illustrations with **zero** gradients and **zero** filters (9-30 paths, 397 KB-2.16 MB) versus two 96x96 miniatures that are 6-gradient/2-filter compositions and only 1.9/2.1 KB. The word "icon" covers both a 2 KB glyph and a 2 MB illustration.
- **Path counts span 2 to 30**, so there is no shared optical weight or complexity.
- `onboarding_stress_icon.svg` is the only negative-worded name in an otherwise aspirational set.

**And none of it ships.** `app/src/main/res/drawable/` contains **no** `onboarding_*_icon.*` file. Zero code references any `.svg` (verified). `OnboardingAssetsTest.kt:66-81` *asserts their absence* — its `obsoleteFiles` list includes `onboarding_activity_icon`, `onboarding_learning_icon`, `onboarding_wellness_icon`, `onboarding_health_icon`, `onboarding_screen2_icons`, `healthy_habits_icon`, `greater_focus_icon`, `less_stress_icon`, `better_you_icon`, and fails if any is present in `res/drawable`. `OnboardingScreen.kt:110-112` explains why: *"Each of the four artworks already contains its scene's illustrations, so no Compose overlay is drawn over them."*

**The full causal chain, provable end to end:** ADR-15 emitted the SVGs as `res/drawable/onboarding_*_icon.xml` VectorDrawables (`DECISIONS.md:143`); ADR-17 migrated onboarding to WebP (`DECISIONS.md:198-218`); the VectorDrawables were deleted; the SVGs were kept *"for provenance"* (`DECISIONS.md:216`); the illustrations were baked into four flattened photographic WebPs. **The consequence is that onboarding can no longer be recoloured, rescaled, re-themed, or given a Light variant, because the vector originals are no longer in the build.** 4.2 MB of vector source sits in the repository and nothing reads it.

This is a *recorded* decision with a *now-defunct* consequence, and it is the root cause of §13.3. It is the strongest example in the audit of an architectural decision quietly removing a capability.

#### 13.5 Motion: the only directional transitions in the product, disagreeing with each other

`OnboardingScreen.kt:144-198` runs **two** `AnimatedContent`s for one page change:

| Layer | Transition | Durations | Slide distance |
|---|---|---|---|
| Backdrop (`:144-160`) | `fadeIn + slideInHorizontally` / `fadeOut + slideOutHorizontally` | 350/350 in, 250/250 out | `it / 4` and `-it / 4` |
| Copy (`:186-198`) | `fadeIn + slideInHorizontally` / `fadeOut` | 300/300 in, 200 out | `it / 6` |

So the artwork and the text of the same page change arrive at **different speeds and from different distances**, and the copy has no slide-out. A parallax of `1f + page * 0.005f` is animated with `tween(600)` (`:138-143`) — a third duration.

**Onboarding is the only screen in the app with directional motion**, and it uses three different durations for one transition, all hardcoded literals (§7.8.10). The rest of the app crossfades uniformly. The product therefore has **two motion languages** — a crossfade and a slide — split by screen, with the slide language itself internally inconsistent.

Reduced motion is honoured properly here: `tween(0)` / `MomentumDurations.fast` swaps and the parallax freezes (`:138, 147-150, 189-192`).

#### 13.6 Recommendations — onboarding

1. **Rewrite the four pages around what the product actually is**, in one register, with the offline/privacy/no-account fact stated before the user commits. (The best existing line in the product — `SettingsScreen.kt:268` — should be an onboarding line.)
2. **Remove the unreachable example-seeding branch** in `OnboardingViewModel.finish` (§13.2), so the signature stops implying a behaviour the app deliberately does not perform. *This is dead-code cleanup, not a consent or product decision — the shipped behaviour is already correct, and the code at `OnboardingScreen.kt:261-263` says so explicitly.* *(Corrected — F-1: the original recommendation here was "Disclose the example seeding", which was based on a false premise and is struck.)*
3. **Decide what onboarding is relative to the theme system** (§13.3): bring it into Aurora (and produce a Light artwork, which the vector sources in `artwork/onboarding/` make possible), or declare it deliberately cinematic and make the hand-off into Home an intentional moment rather than a discontinuity. The current unexamined state is the only option that is not one of these.
4. **Do not rebuild the icon set from scratch — decide whether it is needed at all.** The four WebPs already contain the illustrations. *Kind of change:* either keep the flattened art and delete the 4.2 MB of provenance SVGs, or re-establish them as the source of truth and re-derive per-theme art. The middle state — an unused, incoherent "set" that looks like a design asset in the repo — is the worst of the three because it misleads anyone planning work here.
5. **Make one transition per page change** (§13.5).

---


---

## 14. Navigation Audit

#### 14.1 The destination graph

Eleven routes (`Routes.kt`), one `NavHost` (`MomentumNavGraph.kt:180-351`), four tabs:

```
onboarding --(finish)--> today --> detail/{id} --> edit?activityId={id}
   |                         |                      +-> create/habit/config?intent=X
   |                         +-> settings --> themes
   |                         +-> create/habit --> create/habit/config?intent=X
   |                                                 create/task
   habits --> detail/{id} (HabitDetail) --> edit
   tasks   --> [shared Activities list, type=TASK]
   insights --> calendar --> detail/{id}
   + CreateItemSheet (Habit | Task), shown over any tab
```

Measured properties:

- **One `NavHost`, one `Scaffold`, one `AppBackground`** for all eleven routes (`:133-134`). The bottom bar is shown only on the four tab routes (`:139`) and is a floating glass bar with its own navigation-bar inset (`:144-176`).
- **Pop transitions are not specified.** `enterTransition`/`exitTransition` are set (`:187-196`); `popEnterTransition`/`popExitTransition` are not, so in navigation-compose 2.7.0 they inherit the same specs. **Push and back are visually identical** (§7.8.11).
- **Two Hero routes opt out of the top inset** (`:121-132`, ADR-33) — Home and Habits draw behind the status bar; everything else does not. This is a per-route boolean inside the shell rather than a property of the destination.
- The route graph has 11 destinations and 8 of them are push transitions from a list. Row -> detail -> edit is the app's dominant interaction, and it is entirely un-animated.

#### 14.2 The spec conflict ADR-27 recorded — resolved here

ADR-27 (`DECISIONS.md:519-524`) flags: *"UI spec §4 nav order still conflicts with ADR-22/26 lock (unchanged)."* Having now read both specs, the conflict is **narrower and more specific than "nav order"**, and it resolves cleanly:

| Source | Declared destinations | Status |
|---|---|---|
| `MOMENTUM_DESIGN_SYSTEM_V1.md` §283-292 | Home · Habits · **Tasks** · Insights; "Settings is **not** a bottom navigation destination" | matches code |
| `HABITS_MANAGEMENT_UI_SPEC.md` §124-130 | **Home \| Habits \| Tasks \| Insights**; "Settings is NOT a bottom-navigation destination"; "Do not redesign or change the existing Tasks destination as part of Phase 2B" | matches code |
| ADR-22 / ADR-26 / ADR-27 | Home · Habits · Tasks · Insights | matches code |
| Code (`MomentumNavGraph.kt:84-89`, `:222`) | Home · Habits · Tasks · Insights; Tasks = shared Activities list filtered to TASK | — |
| **`DASHBOARD_SPECS.md` §50-56** | **Home · Habits · Insights · Settings** (no Tasks; Settings *is* primary) | **outlier** |

**So there is one dissenting document, not two.** `DASHBOARD_SPECS.md` §3 is the sole source that puts Settings in the bottom bar and omits Tasks, and it is also the only one of the four whose declared *design language* is **"Momentum — Twilight Journey"** (`DASHBOARD_SPECS.md:7`) rather than Soft Aurora. It is a document written before ADR-21 collapsed the theme registry and before ADR-22 locked four tabs.

**Resolution to record:** the code follows a three-to-one majority (design system §11, the Habits spec §4, and ADR-22/26) and `DASHBOARD_SPECS.md` §3 is **stale documentation, not a live requirement**. It should not be treated as a constraint in Step 2. **This is a documentation defect, not a product decision to revisit** — the correct action is to amend `DASHBOARD_SPECS.md` §3 (and its §7 design-language header) to match ADR-22, which is the Orchestrator's to record, not this audit's.

`DASHBOARD_SPECS.md` also contradicts itself internally on layout, independently of the code: §4 specifies `Header ↓ Hero ↓ Remaining ↓ Completed` with §5 defining a separate Header containing date + greeting, while §6 states the Hero contains *"The date. The greeting. The Settings control. Today's overall progress…"*. The code follows §6 (ADR-31 merged them into one Hero surface, and `HomeHero.kt:59` cites DASHBOARD_SPECS §6). **So §4/§5 of DASHBOARD_SPECS are also stale relative to its own §6.** Both divergences point the same way: the document predates ADR-31.

One further compliance note in the spec's favour: `MOMENTUM_DESIGN_SYSTEM_V1.md` §607 requires Calendar to live inside Insights and not be a navigation destination, and `MomentumNavGraph.kt:238` reaches it only from Insights' header action. **Compliant.**

#### 14.3 Bottom navigation

`LiquidGlassBottomBar` + `LiquidGlassTab` (`LiquidGlass.kt:311-393`). Four tabs, each a `Row` with an icon (filled Rounded when active, Outlined when inactive — a real, correct convention that the design doc §104-105 asked for) and a 12 sp label. Active state: `c.primary@0.18` background, `c.primary@0.40` border, `c.primary` icon, SemiBold label, and a 1.06 scale on the icon (`:341-359`).

Observations:

- **The bar's own geometry is outside the design system**: 26 dp corner radius (`LiquidGlass.kt:315`), 18 dp tab radius (`:337`), 8/6 dp padding (`:320`), 26 dp icon (`:383`, while `MomentumIconSizes.prominent` is 28 dp and `primary` is 24 dp), 2 dp gap (`:376`). ADR-27 locked these values, so they are a *recorded* decision — but they are six literals that exist in no token, on the most-seen component in the app.
- **The tab scale animation (1.06) is the only nav motion**, and it is frozen under reduced motion (`:356`) — correct.
- **Tab labels are 12 sp** (`labelMedium`) — the same size as a card's metadata line and the streak count (§7.5.3). The four primary destinations of the app are labelled in the app's smallest type.
- The bar is `GlassLevel.HERO` (0.68 translucent) — which the design doc §214-219 lists under **"Not allowed as a default treatment for … navigation"**, while `HABITS_MANAGEMENT_UI_SPEC.md` §294-295 asks for navigation to be *"visually integrated with the screen rather than becoming a large floating Material component."* ADR-27 locked the floating glass bar. The doc/ADR tension is real and Step 2 should see it (§7.25).

#### 14.4 Row → detail is the app's most-used transition and it has no spatial model

Measured: `ActivityCard` onClick -> `onOpen` -> `navController.navigate(Routes.detail(it))`; `HabitCard` onClick -> `onOpenHabit`; every row on Calendar, Tasks, Home and Habits. The destination is `fadeIn(200ms)`. The row's icon tile, title and accent colour are all present on the detail screen, and **none of them is animated.**

**Why this is wrong:** a crossfade is the correct choice for a *change of context* (tab -> tab). Row -> detail is a change of *depth* on the same object, and the app has a component that could carry it — the habit's 44 dp accent tile is the same `HabitIconTile` that becomes the 72 dp hero tile (`HabitsScreen.kt:300` -> `HabitDetailScreen.kt:322`). **The product already has a shared-element opportunity built into its own component and is not using it.** **User effect:** the user loses the object they tapped; on a 15-item list, "which one was that" is a real cost. **What KIND of change would solve it:** a shared-element or container-transform treatment for row -> detail (and detail -> edit), which the design doc §9-12 and AGENTS §9 both anticipate. Note this is an *Android-native* answer (Compose shared transitions), not a web pattern.

#### 14.5 `DetailTypeGate` puts a spinner on every detail navigation

`DetailTypeGate.kt:41-47`: a `LaunchedEffect` reads the row from the repository, and while `isHabit == null` it renders **`CircularProgressIndicator(color = …primary)`**.

`Routes.DETAIL` is entered from every row tap on Home, Habits, Tasks and Calendar. **So every detail navigation in the app passes through a Material spinner.** The same gate is also why there are two detail screens for one route; the second is explicitly the "legacy detail body" (`MomentumNavGraph.kt:324`).

Combined with `CreationNav.kt:77` (a second raw `CircularProgressIndicator`, and the only one that does not even pass a colour), the app has exactly two spinners and one of them is on the hottest path. Design doc §568: *"Prefer skeleton/content-aware loading and subtle shimmer over generic spinners."*

**Why this is wrong:** a spinner for a single indexed row read is a category error — the data is either there or the row is gone, and both answers are instant. It also means a detail screen *cannot* be reached with a shared-element transition, because the destination's structure is unknown until after the read. **That is a structural cause of §14.4**, not just a missing animation.

#### 14.6 Two create-routes and one create-screen

`Routes.kt`: `CREATE_INTENT = "create/habit"`, `CREATE_HABIT = "create/habit/config?intent={intent}"`, plus `fun createHabit(intent) = "create/habit/config?intent=${intent.name}"`. `MomentumNavGraph.kt:259-290` registers `CREATE_INTENT` and then `CREATE_HABIT` — **and `CREATE_INTENT` is a literal prefix of `CREATE_HABIT`'s pattern.** Both render `HabitConfigRoute`; `CREATE_INTENT` hardcodes `intent = HabitIntent.BUILD` (`:262`).

So there are **two route constants and one helper function for one screen**, with overlapping route strings and a duplicated registration. `Routes.parseHabitIntent` (`:29-33`) handles the argument defensively (unknown -> BUILD, legacy `"AVOID"` -> REDUCE), which is good — but it is defensive about a route that has two spellings.

**I am not claiming a navigation bug**: navigation-compose matches registered route patterns, and `RoutesTest.kt` exists. I am flagging it as a nav-model smell that Step 2's navigation alternatives should be aware of, and as a candidate for the Reviewer to exercise.

#### 14.7 Onboarding is a route, not a state

`startDestination = if (onboardingDone) Routes.TODAY else Routes.ONBOARDING` (`:182`). `onboardingDone` comes from DataStore. On first launch the `NavHost` starts at `onboarding`, and `OnboardingRoute` calls `navController.navigate(Routes.TODAY) { popUpTo(ONBOARDING) { inclusive = true } }` on finish.

Consequence: `MainActivity.kt:48-50` passes `AppSettings()` (onboardingDone = false) as the initial value, so **a returning user sees a brief flash of the onboarding destination** before DataStore emits. Also, the `CreateItemSheet` is hosted *inside* the Scaffold's content lambda (`:361-373`), so it overlays whatever destination is current — which is correct, and is why §7.9 found its `containerColor` unmapped.

#### 14.8 Recommendations — navigation

1. **Record that `DASHBOARD_SPECS.md` §3 (and §4/§5) are stale**, per §14.2, and stop treating them as constraints. The Orchestrator owns that doc change.
2. **Give row -> detail a spatial model** (§14.4). The shared tile already exists; the transition is the missing piece. *Kind of change:* Compose shared-element/container transitions on the one transition that dominates the app.
3. **Remove the spinner from `DetailTypeGate`** and resolve the destination type from the navigation arguments or the already-loaded list state, so the destination's structure is known before the transition begins (§14.5). This unblocks §14.4.
4. **Decide whether the floating glass bottom bar is the product's navigation model or a Phase 2B artefact.** The design doc forbids glass navigation; ADR-27 locked it. Step 2 should treat this as an open question (§7.25), not a constraint.
5. **Collapse the two create-routes into one** (§14.6).
6. **Move the status-bar inset decision out of the shell's route-matching** and onto the destinations themselves, so "draws behind the status bar" is a property of a screen rather than a string comparison in the NavHost.

---


---

## 15. Motion Audit

#### 7.9 What motion exists, counted

**31 animation call sites across 9 files.** The complete set of animation constructs in `presentation/`:

| File | Sites | What it animates |
|---|---|---|
| `LiquidGlass.kt` | 7 | press scale (×2), tab selected bg / border / icon tint / icon scale |
| `OnboardingLiquidGlass.kt` | 6 | press scale, page indicators, glass reveal |
| `OnboardingScreen.kt` | 5 | two `AnimatedContent` page transitions (fade + horizontal slide), parallax zoom |
| `ProgressOrb.kt` | 4 | sweep spring, halo breath, lobe orbit (infinite) |
| `ActivityCard.kt` | 2 | icon-tile tint on completion |
| `MomentumAurora.kt` | 2 | field drift (infinite) |
| `CreationFields.kt` | 2 | `Expandable` fade + expand/shrink |
| `CompletionButton.kt` | 2 | completion pop scale, plus a static ripple |
| `MomentumNavGraph.kt` | 1 | NavHost crossfade |

Everything else in the product is instant.

#### 7.10 The motion token layer is decorative

`MomentumMotion` (`Motion.kt:17-50`) declares 5 durations, 2 easings and 2 spring specs. Measured production usage:

| Token | Value | Production call sites |
|---|---|---|
| `fast` | 180 ms | 2 (both in `LiquidGlass.kt:338-339`, the nav tab) |
| `standard` | 280 ms | **0** |
| `emphasis` | 450 ms | **0** |
| `reward` | 650 ms | **0** |
| `ambient` | 6000 ms | 2 (`MomentumAurora.kt:72`, `ProgressOrb.kt:201`) — both infinite loops |
| `easeOut` | cubic-bezier | 2 (same two lines) |
| `easeInOut` | cubic-bezier | **0** |
| `springSnappy` | damping 0.8, medium | **0** |
| `springGentle` | no-bouncy, low | **0** |
| `MomentumDurations.*` (legacy alias) | → v1 tokens | 5 — **all in onboarding** |

(The only other references to `standard`/`emphasis`/`reward` are in `DesignSystemPreviews.kt:333-334`, which print them in a caption string, and in the `MomentumDurations` alias definitions themselves.)

**What actually replaced them:** 8 hardcoded `tween(<integer>)` literals — `MomentumNavGraph.kt:190,195` (200, 150), `OnboardingScreen.kt:151-154,191,193-195` (350, 350, 250, 250, 200, 300, 200), `OnboardingScreen.kt:141` (600), `OnboardingLiquidGlass.kt:305,311` (via `MomentumDurations.medium` = 280) — and **8 raw `spring(stiffness = …)` calls** with only two distinct constants (`StiffnessMedium` ×4, `StiffnessMediumLow` ×2), **none of which sets a `dampingRatio`**, so all of them use Compose's `DampingRatioNoBouncy` default. `springSnappy` exists specifically to provide `dampingRatio = 0.8f` and is never used.

Plus `TodayScreen.kt:214,216`: `tween(durationMillis = 220)` and `spring(stiffness = Spring.StiffnessMediumLow)` — and `HomeSectionMotionTest.kt` asserts the *source string* `"spring(stiffness = Spring.StiffnessMediumLow)"` must be present, so that literal is now test-pinned.

**Assessment: there is no coherent motion language.** There is a token file, and a set of unrelated hand-tuned animations. The doc's §34 — *"Screens should not directly invent… animation durations"* — is violated at 16 sites. And the tell is that the one token used consistently is `ambient`, which exists solely for the two decorative infinite loops. The motion system was consumed by decoration, not by interaction.

#### 7.11 Screen transitions and Activity transitions

**NavHost** (`MomentumNavGraph.kt:184-196`): `enterTransition = fadeIn(tween(200, EaseOut))`, `exitTransition = fadeOut(tween(150, EaseInOut))`, both `None` under reduced motion. `popEnterTransition` and `popExitTransition` are **not specified**, so in navigation-compose 2.7.0 they inherit the enter/exit specs — **push and pop are visually identical, and there is no directionality.** There is no shared-element transition, no container transform, no scale, no slide. Every navigation in the app is the same 200/150 ms crossfade. ADR-27 records this as a deliberate "Soft Aurora crossfade" decision, so the *choice* of a crossfade is locked; what is missing is any concept of spatial continuity. Note also that the doc's §5/AGENTS §9 call for "container-transform for screen transitions where it aids continuity" and none exists.

**Activity transitions: none are configured.** `AndroidManifest.xml` has no `android:windowAnimationStyle`; `res/values/themes.xml` is a single line — `<style name="Theme.Momentum" parent="android:Theme.Material.Light.NoActionBar" />` — with no `android:windowActivityTransitions`, no `android:windowContentTransitions`, and no `values-night` counterpart. So the system-level open/close transition is the platform default for a **Light** platform theme, unconnected to the app's own crossfade. Concretely: launching Momentum shows the platform's default light activity animation, and the app's Compose crossfade then plays on top of it. There is no app-owned launch or exit transition. Whether the two compose gracefully in motion is `Unknown — not determinable from the repository`.

#### 7.12 Reduced motion: implemented, but distributed wrongly

`rememberReduceMotion(override)` (`ReduceMotion.kt:10-14`) returns the override if non-null, else `isSystemReduceMotion(context)`, which reads `Settings.Global.ANIMATOR_DURATION_SCALE == 0f` (`:16-23`).

**Two findings:**

**(a) The Settings override reaches exactly one call site.** `settings.reduceMotionOverride` is persisted (`SettingsDataStore.kt:43,55-59`), surfaced by a real Settings switch (`SettingsScreen.kt:169-170`) and written by `SettingsViewModel.kt:30` — and read by presentation in **one** place: `TodayScreen.kt:98`, `rememberReduceMotion(s.reduceMotionOverride)`. There are **14** `rememberReduceMotion` call sites in total; the other **thirteen** pass `null` (system only): `MomentumAurora.kt:64`, `ActivityCard.kt:71`, `CompletionButton.kt:62`, `LiquidGlass.kt:146` and `:335`, `MomentumNavGraph.kt:109`, `HabitConfigScreen.kt:119`, `TaskConfigScreen.kt:76`, `HabitDetailScreen.kt:157`, `HabitsScreen.kt:99`, `OnboardingLiquidGlass.kt:140` and `:236`, `OnboardingScreen.kt:118`.

> **CORRECTION (F-4, Reviewer-verified MINOR).** The original text said *"the other **nine** call sites"* while its own parenthetical then listed 10 named sites plus "all of onboarding" (4) — an internal arithmetic contradiction that summed to 14 while being called 9. The correct figure is **1 of 14**, with **thirteen** passing `null`. The numerator was always right; **the finding is therefore slightly worse than originally stated, not better** (see below). The Reviewer also noted `CompletionLabelTest.kt:97` asserts `rememberReduceMotion(null)` on a `CompletionButton`, so part of the null pattern is test-pinned and would need that test updated alongside any fix.

So a user who turns on "Reduce motion" in Momentum Settings gets it on **Home only** — one call site out of fourteen, or one screen out of the whole product. Still animating: the bottom-nav tab transitions, the NavHost crossfade, every glass press scale, `CompletionButton`'s pop, `ActivityCard`'s tile tint, the `ProgressOrb` halo and lobe orbits, the Aurora drift on three screens, and all four onboarding sites. That is a user-visible accessibility setting that is 13/14 inert, in a product whose own §10 success criteria include TalkBack-traversable flows and whose AGENTS §9 requires always respecting reduced motion.

**(b) The system read is partial.** `ANIMATOR_DURATION_SCALE` is one of three animation scales AOSP exposes. A user (or an OEM power-saver profile) that zeroes `TRANSITION_ANIMATION_SCALE` or `WINDOW_ANIMATION_SCALE` while leaving the animator scale at 1 would not be detected. This is a narrow case, and I am flagging it as a precision limit rather than a defect.

**What is done right:** two components switch to `tween(0)` rather than shortening a duration (`ActivityCard.kt:74`, `CompletionButton.kt:65`) — the correct approach, and `HomeAccessibilityTest.kt` asserts the NavHost and Aurora are wired. `ProgressOrb.kt:125` gates the glow on `sweep > 0.001f` so 0% pays nothing, and `rememberGlowPhase(enabled)` returns a constant when disabled so a disabled glow composes no transition at all. `HomeHero.kt:413-414` passes `glowEnabled = !reduceMotion` and `animate = !reduceMotion`. `HomeAccessibilityTest.kt` asserts `animated = !reduceMotion` reaches the Aurora. That is careful work; it is the *distribution* that is broken.

#### 7.13 Standing animation cost on the primary screen

On Home, with any progress recorded, two infinite transitions run continuously:

1. `AuroraBackground`'s field drift — `MomentumAurora.kt:68-75`, `infiniteRepeatable(tween(6000, LinearEasing))`.
2. `ProgressOrb`'s halo breath + lobe orbit — `ProgressOrb.kt:197-204`, the same 6 s `ambient` token, gated only on `sweep > 0.001f`.

`HomeHero.kt:413` passes `glowEnabled = !reduceMotion`, so a user at rest on Home pays a permanent 6-second loop for a number that changes a few times a day. Neither loop is state-*change*-triggered; both are wall-clock loops. The doc's §27 says *"Aurora is state-responsive"* — a loop that runs identically at 0% and 100% is not state-responsive, it is a screensaver.

#### 7.14 Reward and milestone motion: absent

- `AuroraState.MILESTONE` is unreachable in production (§7.4).
- `MomentumMotion.reward` (650 ms) has 0 call sites.
- No confetti, no particles, no burst, no light flash exists anywhere in the repository.
- The doc's §21 permits *"stronger Aurora, subtle light burst, restrained particles, richer motion"* at milestones; the doc's §19 says *"Normal completion does not trigger confetti"* and *"subtle Aurora response occurs"* at every completion. The only per-completion response that exists is `ActivityCard`'s tile-tint spring, the `CompletionButton` scale pop, and a `HapticFeedbackType.LongPress`.
- The `StreakBadge` does have a milestone treatment — a `success` tint at `streak % 7 == 0` (`MomentumBadges.kt:51`) — which is the *only* place in the product where a milestone is visually distinguished. It is a badge colour change, not a moment.

So the product has a designed vocabulary for reward (`reward` duration, `MILESTONE` state, the doc's §21) and no implementation of it. AGENTS §9 requires "meaningful micro-interactions (completion feedback, streak celebration)". The vocabulary is there; the behaviour is not.

**One oddity worth recording:** `ThemeGalleryScreen.kt:127` fires `HapticFeedbackType.TextHandleMove` on theme selection — a haptic constant for text-cursor movement, used as a generic "selection confirmed" tick. Doc §29 asks for haptics on "completion, milestone, important selection", so the *intent* is right and the *constant* is wrong.

#### 7.15 Recommendations — motion

1. **Make the Settings reduce-motion toggle actually reach the whole app.** *Kind of change:* thread `settings.reduceMotionOverride` into the theme or a CompositionLocal so all ten call sites read one source. This is a small, well-bounded correctness change and it is currently an accessibility gap, not a design question.
2. **Decide whether the motion token layer is real.** Six of its nine members have zero production call sites and 16 hand-written durations have replaced them. *Kind of change:* either adopt the tokens (and delete the literals) or delete the tokens. Leaving both is the worst of the three, because Step 2 will read `Motion.kt` as a specification of what the app does and it is not.
3. **Give the crossfade a spatial model, or accept it as a decision.** Push and pop are currently indistinguishable. *Kind of change:* pop variants (or a directional model), and — if continuity matters — a shared-element treatment for the highest-value transitions (row → detail, list → editor). A crossfade-only system cannot express where the user went.
4. **Replace the two wall-clock loops with change-triggered motion.** *Kind of change:* the orb settles at its new value and stops; the ambient field responds to state transitions rather than drifting forever. This directly serves §27's "state-responsive, not decorative wallpaper" and removes a permanent cost.
5. **Implement the reward vocabulary that already exists, or remove it.** `MILESTONE` and `reward` are a designed capability with no behaviour. Whatever Step 2 does with celebration, it should either use these two hooks or say plainly that they are dead.
6. **Set a damping ratio on every spring.** All 8 raw springs use Compose's no-bounce default; the two spring specs the design system provides for exactly this purpose are unused. *Kind of change:* a small adoption, but it is the difference between "someone chose this feel" and "whatever the default was".
7. **Own the launch transition, or consciously decline to.** No `windowAnimationStyle`, no platform transition config, and a Light platform theme under a Dark Compose app (§7.9).

---


---

## 16. Light/Dark Audit

#### 7.16 How many themes, and how selection works

- **One** theme definition: `MomentumThemes = listOf(MomentumThemeDefinition("aurora", "Aurora", AuroraLight, AuroraDark))` (`Color.kt:139-141`).
- **Two** environments: Light Aurora and Dark Aurora, both fully hand-authored, all 24 tokens each.
- Selection: `MomentumTheme(themeId, darkMode, systemDark = isSystemInDarkTheme())` (`Theme.kt:27-40`) resolves `darkTheme` from `DarkModeSetting.SYSTEM | LIGHT | DARK`, then `MomentumTheme(themeId = settings.themeId, darkMode = settings.darkMode)` is called once, at the root, from `MainActivity.kt:51`.
- `MainActivity` declares `android:configChanges="uiMode"` and re-applies `enableEdgeToEdge()` in `onConfigurationChanged` (`MainActivity.kt:36-39`, manifest line 20) — ADR-23, recorded as a deliberate fix for a NavBackStackEntry crash.
- `MomentumColors.isDark` is **derived at runtime** from the background's luminance: `background.luminance() < 0.25f` (`Color.kt:147-148`).

**A concrete defect in this system.** `AppSettings.themeId` defaults to `"twilight"` (`domain/model/Activity.kt:195`) and the DataStore fallback is also `"twilight"` (`SettingsDataStore.kt:49`) — a theme id that ADR-21 deleted. `themeById` falls back silently (`Color.kt:143-144`), so the app correctly *renders* Aurora. But `ThemeGalleryScreen` computes `selected = theme.id == selectedId` (`ThemeGalleryScreen.kt:87`) → `"aurora" == "twilight"` → **false**. On any install where the user has not tapped a theme in the gallery, the Themes screen shows the only available theme as **not selected**: no checkmark (`ThemeGalleryScreen.kt:156-158`), and the card renders at `GlassLevel.CARD` instead of `GlassLevel.HERO` (`:106-109`). `SettingsScreen.kt:120` shows `themeById(themeId).name`, which correctly reads "Aurora" — so Settings and Themes disagree about what is selected. ADR-21 explicitly deferred the single-entry gallery, so the *one theme* is recorded debt; the *mismatched default id* is not recorded anywhere and is a straightforward bug.

#### 7.17 Is Dark genuinely designed, or derived?

**Partly. The palette is hand-authored; the mechanisms are shared, and several are structurally dark-only.**

**Genuinely designed:** all 24 tokens have independent Light and Dark values. This is not a programmatic inversion.

**But the two environments are the same design with inverted luminance, not two designs.** Measured across the five accent hues:

| Role | Light | Dark | Relationship |
|---|---|---|---|
| `auroraBlue` | `#5C9FEF` | `#78B7FF` | same hue, lighter |
| `auroraLavender` | `#9278E8` | `#B59CFF` | same hue, lighter |
| `auroraViolet` | `#8064DD` | `#9B7CFF` | same hue, lighter |
| `auroraMint` | `#55C8AD` | `#76DFC4` | same hue, lighter |
| `auroraPeach` | `#EAA477` | `#FFC49B` | same hue, lighter |

No hue is introduced, removed or substituted between the environments. No colour role changes. Nothing is expressed in Light that is not expressed in Dark. The doc's §35 acceptance criterion — *"Light and Dark Aurora feel like the same product"* — is met; but the stronger reading of §3's *"two environments of the same design language"* is not, because an environment that cannot express anything the other cannot is one environment with a brightness switch.

**The mechanisms that are structurally dark-only:**

1. **Glass depth (§7.1).** In Dark, a translucent surface over a darker canvas lifts. In Light it does not. So the entire "Liquid Glass" depth model is functionally Dark-only, and Light Aurora gets a hairline where Dark gets a surface.
2. **Shadows.** Every shadow in the app is `Color.Black.copy(alpha = 0.30–0.35f)` ambient with an accent spot (`LiquidGlass.kt:118-119, 219-220`; `CompletionButton.kt:104-105`; `HabitsScreen.kt:240-241`; `OnboardingLiquidGlass.kt:102,153,248`). There is no Light-specific shadow recipe. A black-alpha shadow is invisible on `#F7F8FC` by construction. **Light Aurora has no working shadow at all**, which contradicts the doc's §7 promise of *"very soft shadows"* for Light.
3. **The highlight.** `topLight` is `Color.White.copy(alpha = 0.55f)` in Light and `Color.White.copy(alpha = 0.10f)` in Dark (`LiquidGlass.kt:103-108`) — a white highlight on a near-white surface in Light is also invisible.
4. **Theme branching is rare.** `isDark` is read in only **8** production files, 14 sites: `MomentumAurora.kt`, `AppBackground.kt`, `LiquidGlass.kt`, `MomentumBadges.kt`, `MomentumHeroArtwork.kt`, `HomeHero.kt` (2), plus the definition. Everything else relies on the token values alone, which is the correct architecture — but it means there is almost no place in the codebase where a Light-specific *decision* could live.

**The platform layer is Light-only, permanently.**

- `res/values/themes.xml` declares `Theme.Momentum` with `parent="android:Theme.Material.Light.NoActionBar"`. There is **no `values-night/` directory** (the full `res/` inventory is 19 files; none are night-qualified). ADR-23 records this as intentional: *"Momentum is a pure-Compose activity with no night-qualified resources."*
- Consequences that follow from that decision: the window background is the platform Light background, so it is visible during the pre-first-frame window, behind the Compose content, and in the recents-task snapshot; the system-drawn `ModalBottomSheet` used by `CreateItemSheet` (`MomentumNavGraph.kt:106,362`) is a separate window that does not inherit the Compose background; the IME and any platform-drawn surface use Light platform colours; and there is no `values-night` for `colors.xml` either (so `ic_launcher_background` is a single value regardless of theme).
- `enableEdgeToEdge()` is called with **no arguments** (`MainActivity.kt:38,43`), so `SystemBarStyle.auto` resolves its light/dark mode from `isSystemInDarkTheme()` — the **system** configuration. The app's `DarkModeSetting` has no path to the system bars. ADR-23 describes this re-apply as resolving icon contrast "against the new uiMode", i.e. the ADR treats the system uiMode as the source of truth. **Consequence:** a user who sets Dark Mode to *Light* in Momentum while their system is Dark (or the reverse) gets system-bar icon contrast chosen from the system theme, not the app's. Whether the icons then contrast adequately is `Unknown — not determinable from the repository` without rendering — but the *mechanism* has no app-setting input, which is the finding.

#### 7.18 Onboarding is theme-blind

`OnboardingScreen.kt` and `OnboardingLiquidGlass.kt` were searched for `MomentumThemeColors`, `LocalMomentumColors`, `isDark` and `darkMode`: **0 matches.** Onboarding reads only `Twilight` constants (8 uses across the two files) plus three local hexes (`OnboardingLiquidGlass.kt:117,205,211` — `#BFE3FF`, `#0A1B2E` ×2) and `PhotoShade #060B18` (`OnboardingBackgrounds.kt:65`). Its four background WebPs have **no Light variant** (unlike the hero pair, which does).

So a user who has set Light Aurora — or whose phone is in Light mode — walks through four near-black photographic screens in their first session, and a user who completes onboarding and arrives at Home sees a light, cool-grey app. **The first thing the product shows and the thing it becomes are two different visual products.** Whatever Step 2 decides about onboarding, this is a structural fork, not a styling choice.

#### 7.19 Which components are theme-aware

| Component | Theme mechanism | Notes |
|---|---|---|
| `AppBackground` | `isDark` branch for ambient alpha only | colours are token-driven; the vertical gradient is inert (§3.3) |
| `AuroraBackground` | `isDark` branch for base alpha (0.20 / 0.16) | otherwise token-driven |
| `liquidGlass` / `GlassLevel` | `isDark` branch for border, top highlight, solid handling | the *only* component with a real Light/Dark treatment logic |
| `StreakBadge`, `StatCard`, `EmptyState` | `isDark` branch for border colour | re-implement the recipe inline (§7.2) |
| `MomentumHeroArtwork` | `isDark` selects light/dark WebP | the only asset-level theme response |
| `HabitIconTile` | **not theme-aware** | `Color.White.copy(alpha = 0.14f)` top stop is fixed in both themes; accent hues are a single fixed set |
| `CompletionButton` | token-driven; `Color.White` border at 0.35f fixed | |
| `OnboardingBackdrop`, `OnboardingLiquidGlass` | **not theme-aware at all** | §7.18 |
| `MomentumTheme` → M3 scheme | 15 slots mapped, rest at library defaults | §6.3 |
| Platform window / system bars | `Theme.Momentum` = Light, no `values-night`; `enableEdgeToEdge()` reads system uiMode | §7.17 |

#### 7.20 Live accessibility evidence

`ISSUE-020` (OPEN, Low, deferred): the Home date label at `textSecondary` `#596579` measures **3.11:1** over sampled Light hero artwork, below the 4.5:1 floor; Dark passes at 5.22:1. The recorded root cause is that `textSecondary` is too light against Light artwork under the current veil header alpha, and the recorded decision is that the remedy is the **text token**, not re-inflating the veil. This is the only measured contrast evidence in the repository, and it is a *Light-specific* failure of a *Dark-passing* token — which is the §7.17 pattern in its clearest form: a token that works in one environment and silently fails in the other.

#### 7.21 Recommendations — Light/Dark

1. **Decide whether Light Aurora is a designed environment or an inverted Dark.** At present it is the latter: same hues, same roles, no expression unique to it, and no working depth or shadow mechanism (§7.1, §7.17). *Kind of change:* a position on this, because it determines whether Step 2 explores two directions or one. A product can legitimately choose one; it cannot leave it undecided and then present both in a gallery.
2. **Fix the `theme_id` default.** `"twilight"` in `AppSettings` and the DataStore fallback should be `"aurora"`, or `ThemeGalleryScreen`'s `selected` comparison should resolve through `themeById`. As shipped, the Themes screen misreports the active theme on a fresh install.
3. **Give the Settings reduce-motion toggle an effect beyond Home** — see §7.15.1. It is the same class of problem: an app-level setting that the system-level one silently overrides.
4. **Feed the app's `DarkModeSetting` to the platform layer.** Either add a `values-night` theme, or pass explicit `SystemBarStyle` arguments derived from `settings.darkMode` rather than letting `enableEdgeToEdge()` read the system uiMode. Right now the app's own theme control and the platform's chrome can disagree.
5. **Decide what onboarding is.** *Kind of change:* either bring onboarding into the Aurora token system (and either produce Light artwork or accept that onboarding is deliberately dark and say so), or declare onboarding a separate, deliberately cinematic layer that is *supposed* to feel different and make the hand-off into Home intentional. What cannot stand is the current unexamined state.
6. **Give `HabitIconAccents` two environments.** Seven fixed hues used identically in both is a hole in the model (§7.9.6/§4.6).
7. **Re-examine `textSecondary` in Light before Step 2 places secondary text on imagery.** `ISSUE-020` is open, deferred, and its remedy is a token change that would ripple through 74 call sites — so it should be a Step 2 input, not a Step 2 inheritance.

---


---

## 17. Generic Pattern Audit

Slice 1 produced the foundation-level list (§7.4.7, §7.10). This section extends it with screen-level patterns and adds the distinction `R3` requires: *familiar because it is Android* vs. *generic because it is unowned*. Slice 1's entries are cross-referenced, not restated.

#### 17.1 The pattern, stated once

The most common pattern in this codebase is **not** a visual style. It is a *governance* pattern: **a design system that specifies and a codebase that re-decides.** §3.3 counted 100 inline alpha literals, 9 distinct surface fills, 7 hardcoded shadows against a dead elevation token, 4 date-formatting approaches, 4 weekday-label approaches, 3 progress-track tokens for one job, 4 icon-container treatments, 6 card constructions, 2 FABs, 2 press-feedback languages, 5 dialogs with 3 dismiss verbs, and 8 previews naming deleted themes. Every one of those is a *local* decision the design system had already made, and every one is invisible to a user on its own.

That matters here, because a "generic pattern" audit usually means *the app looks like a template*. Momentum's actual problem is the opposite and more tractable: **it looks hand-made in twenty slightly different ways, and none of the differences is a decision anyone made.** The patterns below are grouped by whether familiarity is doing useful work.

#### 17.2 Familiar because it is Android — and correctly so

These should be defended, not "fixed":

| Pattern | Where | Why familiarity is earning its place |
|---|---|---|
| Circular 48 dp completion disc with a check | `CompletionButton` | The most-touched control in the product. Users reach for a circle and a tick without reading. The app adds a role, an action-naming label, a spring pop and a haptic *on top of* the familiar form. |
| Material ripple on the completion control | `CompletionButton.kt:113` | Correct on its own. The problem is only that it coexists with `glassPress` (§17.3). |
| Four-tab bottom bar with icon + label | `MomentumNavGraph.kt:84-89` | Android's tab grammar, locked by ADR-22. Familiarity here reduces the user's cost of navigating. |
| Filled/outlined icon pair for tab state | `LiquidGlassTab` | The standard active/inactive convention, applied consistently. |
| 48 dp minimum touch target throughout | `MomentumHeights.controlMin` | Not a style choice; a platform contract (AGENTS §9). |
| Back arrow top-left on pushed screens | `MomentumTopBar`, `HabitDetailScreen.kt:219-232` | Universal; costs nothing to satisfy. |
| Month grid + chevrons for a calendar | `CalendarScreen` | Correct and expected. |
| Action rows in a "Details" card | `ActivityDetailScreen.kt:336-381` | The standard iOS/Android settings idiom, and the `supporting` strings make it genuinely informative. |

#### 17.3 Generic because it is unowned

Each entry: **what is wrong -> why -> user effect -> what KIND of change.**

**(a) Card uniformity — the whole screen is one repeated pattern.**
`SettingsScreen.kt` is five consecutive `[16 sp section label] -> [16 dp glass card]`, each holding 1-4 rows separated by 1 px dividers, each row a 16 sp title + 12 sp support + a trailing control. `InsightsScreen.kt` is a top bar, a hero card, a 2-tile row, a header, a card, a header, a card, a header, a card. `ActivityDetailScreen.kt` is a top bar, a card, a header, a 2x2 tile grid, a header, a card, a header, a row of squares, a header, a card.
*Why:* `MomentumSectionHeader` (16 sp Regular) and its sibling content titles are the same style, so the header cannot do its job; and `MomentumCard` is used once, so cards are assembled per screen and end up identical.
*User effect:* the user cannot tell a section boundary from a card boundary from a row boundary. On Settings, five groups read as one undifferentiated scroll.
*Kind of change:* give section headers a distinguishable style, and vary card composition by content role (a "key facts" card is not a "list of actions" card).

**(b) Stat tiles without meaning.**
`StatCard(value, label, sublabel?)` (`MomentumBadges.kt:144-188`) is used 7 times: 2 on Insights, 4 on Activity Detail, 1 in a preview. Every value is 20 sp, every label 12 sp, no track, no scale, no comparison, no direction.
*Why:* `StatCard`'s contract is `String` + `String` + `String?`, so it has no way to express rank, trend, or magnitude relative to anything.
*User effect:* "Current streak 3 / Best streak 12 / Last 30 days 78% / Completions 45" is a grid of four numbers with no hierarchy, so the user reads all four and learns nothing about which one is bad. `MOMENTUM_DESIGN_SYSTEM_V1.md` §600-605 requires `Question/headline -> Key number -> Visualisation -> Interpretation`; `ActivityDetail` supplies none of the first three.
*Kind of change:* a figure component that carries meaning — a key number with a comparison and a mark — not a string pair in a rounded rectangle.

**(c) Uniform chip rows.**
`CategoryChip` is 20 call sites across 6 files doing **four** unrelated jobs: a list filter (Activities status + category, two rows), a settings enum (dark mode), a settings enum (week start), and a creation option (`CreationFields`, `HabitConfigScreen`, `TaskConfigScreen`).
*Why:* the component's contract is `(label, selected, onClick)` — nothing distinguishes a filter from a setting, and two adjacent rows of the same pill with no label is unreadable.
*User effect:* on Tasks, two identical rows of pills with no axis label; on Settings, "System | Light | Dark" looks like a filter on the Habits list. The user guesses.
*Kind of change:* separate the concepts — a filter chip and a segmented option — or label the axes.

**(d) The same idea under four names (icon containers).**
48 dp circle glass / 44 dp 8 dp-square accent / 52 dp 8 dp-square primary / 40 dp 8 dp-square primary.
*Why:* §7.10 item 5 — per-activity accent identity reaches 3 of 5 surfaces (`habitIconAccent` on two, `templateAccent` on one), and each of the other surfaces invented its own tile.
*User effect:* an activity's icon changes size, radius, colour and container five times between its list row, its detail hero, its Insights row and its creation card.
*Kind of change:* one tile component taking a size and a colour source.

**(e) Decorative-but-unowned gradients and glows.**
Two static ambient systems (§7.7.4); an `AppBackground` whose documented vertical gradient renders as a flat fill (§3.3); a `ProgressOrb` that stacks four arcs and runs a permanent loop to render one number; `ArtworkScale = 1.12f` permanent zoom plus 12 dp parallax on Home's artwork; a 12-step veil and a 24-step bottom blend on a header.
*Why:* each was added to solve a *readability or seam* problem (ISSUE-014/015/016/018, ADR-31/32/34/35/37/39), and each outlived the problem. The KDocs record the diagnoses; nothing records a decision to keep the machinery once the seam was gone.
*User effect:* Home runs two infinite animations and three compositing layers to show a date, a greeting and a percentage — a standing cost with no informational return.
*Kind of change:* audit each decorative layer against the defect it was added for, and keep only those that still prevent a real failure.

**(f) Motivational copy as a filler slot.**
Six messages in `TodayViewModel.messageFor` (`:240-251`). Four of the six are pure encouragement with **no reference to the user's data**: "Small steps, unstoppable momentum.", "You're building momentum.", "First win banked. Keep going.", "One more - finish strong."
*Why:* the KDoc claims *"It is never a random quote - it always describes the user's current state"*, but four branches describe a *bucket* of state, not the state. "You're building momentum." fires for every configuration with `done >= 2 && remaining >= 2`, which for an 8-habit user is most of the day. "Small steps, unstoppable momentum." is verbatim the product's own tagline candidate (`PRODUCT_SPEC.md:11`) used as an everyday state line.
*User effect:* the Hero's most prominent non-numeric slot says the same thing all day, so the user stops reading it — while the 16 sp "3 of 5 completed" beneath it, which does change, is set smaller.
*Kind of change:* a message slot that can only be filled with something specific (the streak at risk, the last remaining item, the time, the count), or the slot removed so the composition carries one idea.

**(g) Onboarding marketing copy** — §13.1. Four bodies containing zero product facts, one Title-Case unpunctuated outlier, one self-contradictory adjective pair, and the app describing itself in third person.

**(h) Placeholder and fallback copy.**
- `HomeEmptyState` uses `Icons.Rounded.Star` (`TodayScreen.kt:401`) for "Nothing scheduled today" — and `Star` is also `momentumIcon`'s fallback for a corrupt icon key (`MomentumIcons.kt:94`). The same glyph means "an empty day" and "a habit we failed to read."
- `DASHBOARD_SPECS.md`'s own layout diagram ends mid-sentence: `"Header ↓ Hero ↓ Remaining today ↓ Completed today"` with no closing fence (`:70-80`).
- The app's Custom-habit template ships with `iconKey = "spark"`, which renders as the fallback `Star` (§10.5).
*User effect:* the app's most important empty state and its most important default choice are both represented by a generic star.
*Kind of change:* an owned empty-state mark, and a real glyph for `spark`.

**(i) The design system's own previews misrepresent it.**
8 of 44 previews name deleted themes (§7.3.6); no preview shows the production surface stack (§7.3.5); `CreateItemSheet`'s two previews do not pin `darkMode` and so depend on the IDE's setting.
*Why:* these are verification artefacts; a preview that displays Aurora under the name "midnight" is a false signal to anyone auditing visually.
*User effect:* indirect, but it is how visual regressions get missed.
*Kind of change:* pin `darkMode` and `themeId` in every preview; render previews on the real `AppBackground` stack.

**(j) The product's spec'd analytics are invisible.**
`missedCount` and `consistencyScore` — computed, typed, tested, named in `PRODUCT_SPEC.md` §85-86 — have **0 presentation call sites** (§12.6). `Trend` is surfaced as a lowercased enum name, including the literal **"Insufficient data"** (`ActivityDetailScreen.kt:276`).
*Why:* the screen was built from the four `StatCard`s it could fill easily rather than from the metrics the engine offers.
*User effect:* the user is shown "Improving" with no magnitude, and is never shown the one number that would actually tell them whether they are consistent.
*Kind of change:* surface the metrics. A metric that is computed and hidden is indistinguishable, to the user, from a metric that does not exist.

**(k) Enum names and type names leaking to the UI.**
`ActivityStatus.name.lowercase().replaceFirstChar(Char::titlecase)` (`ActivityDetailScreen.kt:151`), `Trend.name…` (`:276`), `DarkModeSetting.entries.forEach { mode.name.lowercase()… }` (`SettingsScreen.kt:155`). Three places generate user-facing strings from Kotlin identifiers.
*Why:* it is the cheapest possible implementation and it happens to be readable, which is why it survived. It is not localisation, and it will break for any enum with an abbreviation.
*User effect:* today, mostly none. It is a latent defect that surfaces at the first non-English locale or the first enum with a compound name.
*Kind of change:* an explicit label per case, in one place.

**(l) False UI claims.**
`SettingsScreen.kt:166` — Reduce motion's supporting copy is **"Calm animations throughout the app."** §7.8 —7.8.12: the setting reaches **1 of 14** motion call sites; the nav bar, the NavHost crossfade, every glass press, `CompletionButton`, `ActivityCard`, `ProgressOrb`, the Aurora on three screens, and all four onboarding sites ignore it. *(Corrected — F-4.)*
*Why:* the toggle was built and wired to `TodayScreen` only, and its copy was written as if it were universal.
*User effect:* a user who needs reduced motion is told by the app that it has been applied, and is not. For a user with a vestibular disorder this is the most consequential defect in the audit.
*Kind of change:* thread one override through the theme so all ten sites read it — and until then, do not claim it.

#### 17.4 The generalisation

The app's genericity is a symptom of local re-decision, not of a default template. Step 2's most valuable exploration is therefore **not** "make it prettier" but **"decide once, at the level of the system, the things currently decided twenty times"** — which is what §7.21 sets out, what §7.26 protects, and what §7.27 recommends as process.

---


---

## 18. Existing Strengths

Each item names the specific concept, component or interaction, and why it deserves survival. Nine items; all verified by file and line.

**1. `CompletionButton` — the app's one complete stateful control.**
`CompletionButton.kt:54-129`. 48 dp. Idle: hairline ring over a translucent control fill. Done: a solid `primary->secondary` linear-gradient disc with a `White@0.35` border, an 8 dp accent shadow, a spring scale pop, and a `LongPress` haptic. `Role.Checkbox`. The `contentDescription` names the **action**, not the widget — "Complete Workout" / "Mark Workout incomplete" (`ActivityCard.kt:188-197`) — and switches to "Enter today's progress for X" for target-based activities, so the label never promises an instant toggle it cannot deliver.
*Why it deserves survival:* it is the only control in the repository where the visual state, the semantic role, the accessibility label, the haptic and the motion all agree, and where the accessibility label was written as a *promise about behaviour* rather than a description of a shape. That is a level of care that is invisible until it is absent, and it is the correct pattern for every other stateful control Step 2 designs.

**2. The manage / act / understand division of labour.**
`HABITS_MANAGEMENT_UI_SPEC.md` §14, structurally implemented: no completion control, streak, progress or history on the Habits list; no management on Home; history only on detail. Verified — `HabitsScreen.kt` contains no `CompletionButton` and no `ProgressOrb`.
*Why it deserves survival:* it is the clearest product decision in the repository, it is enforced by component choice rather than by convention, and it is what stops the app collapsing into a generic dashboard. Step 2 should treat it as a **fixed constraint**, not a variable — a direction that re-merges these surfaces would be re-solving a problem Momentum already solved.

**3. `HabitDetailScreen.DayCell` — the only three-state data mark in the product.**
`HabitDetailScreen.kt:496-556`. `COMPLETED` = gradient-filled 32 dp circle + check. `INCOMPLETE` = 1 dp ring. `NOT_SCHEDULED` = an 8 dp muted dot in a 32 dp footprint.
*Why it deserves survival:* it is the only place in the app that satisfies `HABITS_MANAGEMENT_UI_SPEC.md` §193-195 — *"Not-scheduled days must be visually distinct from missed/incomplete days. For example, a Mon/Wed/Fri Habit must not make Tue/Thu/Sat/Sun look missed."* It is the difference between a habit tracker that respects rest and one that manufactures guilt. It is also the correct answer to §12.4: the Calendar and the Insights bars have the `scheduled` count available and do not use it, while this component does. **This is the template the product's entire visualisation layer should have been built from**, and the strongest single argument that the problem is consistency, not capability.

**4. `MomentumHeroArtwork` — a real, documented art pipeline.**
`MomentumHeroArtwork.kt:42-153`. One artwork pair for the whole product. Selection is a pure function of the theme, never the clock (`momentumHeroArtworkRes(dark: Boolean)`, asserted by `MomentumHeroArtworkTest`). Callers pass band amounts and **cannot position the artwork**. No tint, no filter, no re-colour — the files are consumed as supplied. The KDoc is honest that `topDissolve` generates no stop (`:66-74`) and that the artwork is decorative and silent to TalkBack.
*Why it deserves survival:* "one artwork pair, theme-selected, callers cannot art-direct" is a very cheap pipeline to maintain and it removed a whole class of decision. That the component also *documents* its own dead parameter rather than hiding it is a mark of quality. Whatever Step 2 does with art direction, this contract is worth keeping.

**5. Manrope, bundled, offline, three pinned instances.**
`res/font/manrope.ttf` (single 164,700-byte variable TTF), `Type.kt:36-52`, ADR-21 (OFL-1.1 from `google/fonts`, committed, "fetched at build-authoring time").
*Why it deserves survival:* it is the product's clearest existing identity asset, it is the only typeface, and it is correctly bundled — no remote loading, satisfying AGENTS §2 and §11. **The typeface is not the problem; the scale built on it is** (§7.5), and those are separable decisions.

**6. `HabitsHero` — the reference collapsing composition, and the app says so itself.**
222 lines, a 300 dp expanded floor, `CollapseHeadroom` 60 dp, a bottom-anchored text block, one linear dissolve, and **text that never changes colour during scroll** because both artwork variants were built for their theme (`:55-57`) — i.e. the decision was made by *building two artworks* rather than tinting one.
*Why it deserves survival:* `HomeHero.kt:64-66` names it as *"the reference composition"*, and it is 3.5x smaller than the alternative built on the harder screen, with no masks, no anchors and no per-page exceptions. It is the proof that the Hero problem was solvable without the machinery.

**7. Reduced motion, implemented rather than promised — in the places that were done.**
`ActivityCard.kt:74` and `CompletionButton.kt:65` switch to `tween(0)` rather than shortening a duration, which is the correct behaviour. `ProgressOrb.kt:89, 125, 193-205` gates the glow on `sweep > 0.001f` so 0% pays nothing, and `rememberGlowPhase(enabled)` composes no transition at all when disabled. `MomentumNavGraph.kt:187-196` swaps in `EnterTransition.None` / `ExitTransition.None` rather than a fast transition. `TodayScreen.kt:213-216` nulls the specs rather than shortening them. `HomeAccessibilityTest.kt` asserts the flag reaches the Hero and the Aurora.
*Why it deserves survival:* the *pattern* is right and it is applied with judgement — a 6-second loop is frozen, a 180 ms tab selection is kept. The failure is distribution (§7.8.12), not approach — so this is extendable, not needing replacement.

**8. Accessibility that goes past the checklist.**
Beyond 48 dp targets and content descriptions: `semantics { heading() }` on Home's section headers (`TodayScreen.kt:282, 306`); `CalendarScreen.kt:228-254` merging a day cell's descendants and composing a real `contentDescription` (*"March 4, 3 of 5 complete"*) with `role = Role.Button` and `onClick` semantics; `ActivityCard`'s action-naming labels; `OnboardingScreen.kt:124-132` restoring the previous system-bar appearance on dispose; `ActivityDetailScreen.kt:398` using `onClickLabel` on action rows; `CompletionButton`'s `Role.Checkbox`; `HomeAccessibilityTest`/`QuickAddTest` asserting targets and roles.
*Why it deserves survival:* the Calendar's day-cell description is the single best piece of accessibility engineering in the repository, and it is the *model* the Habit Detail weekly strip should have followed (§10.6's `DayCell` has no description at all). It shows the team knows how; the gap is coverage, not capability.

**9. The Settings copy, and the offline promise.**
`SettingsScreen.kt:246-272`: *"Everything stays on this device. No account, no cloud, no tracking — offline by design."* / *"Momentum 0.1.0"* / *"Reminders fire on-device. Enable notifications so Momentum can nudge you."* / *"Motivational messages — Gentle encouragement on Today"* — and in the Activities delete dialog, *"Archive keeps history"* (`ActivitiesScreen.kt:101`).
*Why it deserves survival:* this is the only screen whose copy is written to a standard — specific, honest about consequences, and it names the product's real differentiator. It is also, awkwardly, the *only* place the offline promise appears (§13.1). **The writing standard already exists in the codebase**; onboarding's problem is placement, not capability.

---


---

## 19. Radical Redesign Opportunities

Eleven opportunities. Each states **what capability of the current design made the current approach the default**, so the constraint is visible. These are directions for exploration, not proposals to implement.

#### 19.1 Layout: one progress hierarchy instead of a per-screen composition

*Current default and its cause:* the Home Hero needed a header that carries artwork, collapses on scroll, and never shows a seam, and `MomentumHeroArtwork` gave it a 24-sample bottom blend and a 48-sample veil to achieve that. Habits needed the same thing and found a 222-line answer; Home found a 771-line one. Both are "collapsing artwork header", and they are not the same component.
*Opportunity:* one header contract — artwork, a copy slot, a collapse range, a readability model — with the Home and Habits compositions as two instances. The five veil anchors and the two blend profiles become parameters, not per-screen constants. *Radical version:* the header's height is driven by the *content it frames*, so a day with 3 activities and a day with 12 produce genuinely different compositions rather than the same 72 dp of empty aurora (§8.3).

#### 19.2 Layout: one typographic register from Hero to list

*Current default and its cause:* `DASHBOARD_SPECS.md` §1 and §24 forbid history on Home (*"It should not attempt to answer: How have I performed over the last month?"*) and §326-331 fix a single flat ordering. So Home can only be a list, and the only place to put emphasis is the Hero. §8.2 then shows the Hero's emphasis landing on the wrong element.
*Opportunity:* keep the *data* restriction (no analytics on Home) but change the *composition* — a single continuous surface where today's fraction, the day's items and the streak share one typographic register, so the list reads as an extension of the Hero instead of as a different kind of object. The constraint is real and must be respected; the opportunity is compositional, not informational.

#### 19.3 Component architecture: one surface primitive, one press language, one data-mark vocabulary

*Current default and its cause:* `Modifier.liquidGlass` is a *modifier*, not a component, so every screen assembled its own card; `MomentumCard` was added later as a delegate and got one call site; `glassPress` was added to suppress the platform ripple, but 18 raw `IconButton`s and 4 un-overridden `clickable`s were written afterwards. Seven progress representations exist because `ProgressOrb` was parameterised for one caller.
*Opportunity:* invert the dependency — a `Surface` primitive that owns fill level, border, radius, padding and press feedback, with `liquidGlass` as an implementation detail; a `Mark` primitive for data display with a defined state vocabulary (empty / partial / complete / not-applicable); and a press policy applied by the surface rather than chosen per control. **This is the single highest-leverage structural change available**, because it converts ~14 separate §3.3 and §9.2 counts into one decision.

#### 19.4 Navigation: model depth, not just destinations

*Current default and its cause:* `NavHost` transitions are a global crossfade (`:187-196`) and `DetailTypeGate` resolves the destination's structure asynchronously, so the destination is unknown when the transition begins. Both together make a shared-element transition impossible.
*Opportunity:* (a) resolve the detail type from navigation arguments or already-loaded list state, removing the gate; (b) shared-element or container transitions on row -> detail (the habit's 44 dp tile already becomes the 72 dp hero — the relationship exists in the code, unused); (c) a directional model for back vs forward. *Radical version:* Home and Calendar as a *time* axis rather than two destinations — the calendar becomes a scrubber over the same data the Hero summarises, which would make the "60 days loaded, 7 drawn" problem (§12.1) structurally impossible to have.

#### 19.5 Habit visualisation: give the habit detail screen a window and a value

*Current default and its cause:* `ComputeHabitOverviewUseCase` takes `completionDates: Set<LocalDate>` and a hardcoded 7-day `weekWindow`, so a habit's history is exactly one week of booleans. The signature is the constraint.
*Opportunity:* decide whether a habit's history is 7 days or unbounded, and whether per-day *values* are in scope (they exist in `CompletionRecord.value` and are discarded at the use-case boundary). Then: a 7 / 30 / all control, and a mark that can show magnitude — a target-based habit's "5/8" over a month is a different object from a binary habit's checkmarks. *Constraint to respect:* `HABITS_MANAGEMENT_UI_SPEC.md` §177 forbids a bar chart for the *weekly* view, and its stated reason (§179) is *"A Habit can be completed at most once per day"* — which is a claim about the week, not about values.

#### 19.6 Streak visualisation: stop using fire, and make the number the thing

*Current default and its cause:* `StreakBadge` is 12 sp + a fire icon because it is a *badge* — a compact thing that fits in a card's text column. `ProgressOrb` is a circle because a ring is the universal progress form and the design system had no other primitive.
*Opportunity:* the product is named for momentum, and the design doc §488-497 says streaks are *"supporting information, not the emotional center"* and rejects fire/emoji. So: a streak is a **number with a direction** (rising / at risk / broken / none) shown at figure scale, and "at risk" is the only state that earns motion. `AuroraState.MILESTONE` and `MomentumMotion.reward` already exist unused for the moment a streak extends. **This is the clearest single divergence between the product's name and its visual language**, and it is a violation of a locked decision (§7.26), not a matter of taste.

#### 19.7 Progress visualisation: a mark that scales from 6 dp to 96 dp

*Current default and its cause:* `ProgressOrb` is 262 lines hard-wired to a 96 dp ring with a 4-arc draw order; `MomentumLinearProgress` is a 6 dp bar; `WeeklyBars` is a Canvas; `DayCell` is 32 dp circles; the month grid is 42 cells. Five marks because no primitive existed that could serve more than one.
*Opportunity:* one mark family with a shared state vocabulary (empty / partial / complete / not-applicable — the vocabulary `DayCell` already proves) and a size range. Then the Home ring, the Insights bars, the category rows, the calendar cells and the weekly strip become five instances of one idea, and "which day was missed" becomes answerable everywhere (§12.4). **This is also where the strongest existing asset goes: `DayCell` is the prototype.**

#### 19.8 Interaction model: make "momentum at a glance" answerable in one glance

*Current default and its cause:* completion is a per-row 48 dp disc, and the day's fraction lives in a 20 sp percentage inside a 96 dp ring, with the actual fraction "3 of 5" at 16 sp Regular. The three representations were built for three different screens and never reconciled, because no screen owns the concept of "today's state".
*Opportunity:* one answer to "how am I doing today", expressed once, at a size that reflects its importance, with the list as its detail. *Radical version:* the day's state expressed as a **shape** rather than a number — a completed-day silhouette whose notches are the remaining items, so a glance reads "mostly done, two left" without reading digits. `TodaySections` already carries the data and nothing in the architecture prevents this.

#### 19.9 Onboarding: make the first screen argue the actual case

*Current default and its cause:* four cinematic WebP backdrops with a mood line each, because ADR-17 flattened the vector artwork into photography and `OnboardingScreen.kt:110-112` notes the illustrations are already baked in — leaving only copy as a variable, and the copy was written as mood.
*Opportunity:* the product's one real differentiator (offline, no account, on-device) currently appears only in Settings §268. Move it to the front. *Kind of change:* argue the case rather than set a mood; state what the product does, using the Activity concept, in one register. **And decide the theme question** (§13.3): the vector sources exist in `artwork/onboarding/` precisely because ADR-15/17 flattened them, so a Light variant is a build-time possibility, not a commission.

#### 19.10 Surface system: decide whether Light is an environment or an inversion

*Current default and its cause:* the glass recipe is `surface @ alpha` with a hairline, and in Light `surface == surfaceElevated == #FFFFFF` against a `#F7F8FC` canvas — so translucency has nothing to reveal, black-alpha shadows are invisible, and a 1 px hairline does all the separation. The two "environments" are the same five hues at two luminances.
*Opportunity:* this is the open question flagged in §7.25 and §7.28. *Radical version:* if Light is to be a genuinely different environment, it needs a mechanism the Dark one does not use — which means deciding what surfaces are *made of* in Light rather than reusing a translucency recipe built for a dark canvas. §7.26 does not lock this; only ADR-21 (two environments exist) and ADR-23 (no night-qualified resources) touch it, and neither forecloses the question.

#### 19.11 Motion language: change-triggered, not wall-clock

*Current default and its cause:* `MomentumMotion.ambient = 6000` exists for "very slow decorative loops", and two components use it as a continuous clock — `ProgressOrb`'s halo/lobes and `AuroraBackground`'s drift. The result is that Home runs two infinite animations whenever any progress exists, for a number that changes a few times a day.
*Opportunity:* motion on *transition* (value changed -> animate and settle), with continuous ambient reserved for a state the user is actually in. The vocabulary for reward already exists and is unused — `AuroraState.MILESTONE`, `MomentumMotion.reward`, `HapticFeedbackType.Confirm` — and the app's entire day-completion celebration is currently a `LongPress` haptic plus a snackbar reading "All done - momentum secured." (`TodayScreen.kt:85-88`). **The hook exists; the behaviour does not.** Whatever Step 2 decides about celebration, these two tokens are where it plugs in.

---


---

## 20. Screen-by-Screen Verdict

Severity vocabulary is exactly `KEEP` | `EVOLVE` | `REDESIGN` | `REIMAGINE`.

#### Onboarding

**Current role.** Four full-bleed photographic pages (INTENT / TRACK / GROWTH / BEGIN) that set a mood, gate first launch via `onboardingDone`, and — on the final CTA only — seed three example activities into the real repository.

**Current strengths.** The only place in the product with real illustration. `OnboardingBackgrounds.kt:50-56` shows genuine art direction: per-scene `BiasAlignment` chosen from the actual composition (*"INTENT: centered sun over the lake; near-symmetric, protect the glow"*), with a documented reason for each; `subjectAlignment()` is the right abstraction. The `DisposableEffect` that sets and then **restores** system-bar appearance (`:124-132`) is careful platform work. Reduced motion is honoured properly (`:138, 147-150, 189-192`). The Cover-crop + 5-stop `PhotoShade` gradient is a legitimate readability strategy with a stated rationale.

**Current weaknesses.** (1) **Zero product facts in four screens** — no Activity concept, no offline, no privacy, no account (§13.1); the app's best line lives in Settings §268. (2) One Title-Case unpunctuated headline among three sentence-case ones (`:93`). (3) *"all in one calm and powerful app"* — the app describing itself in third person with a contradictory adjective pair. (4) Theme-blind and dark-only, with its own 14-constant palette and its own ink (`Twilight.Moonlight`), forcing a platform-chrome hand-patch (§13.3). (5) Two `AnimatedContent`s per page change disagreeing on duration and slide distance (`:144-198`). (6) A bespoke drop shadow on page 1 only (`:204-208`). (7) The 8-SVG "icon set" in `artwork/onboarding/` is incoherent (3 viewBoxes, 2 techniques, 2 KB-2.16 MB) and entirely unused — the VectorDrawables were deleted and the art flattened into the WebPs (§13.4). (8) *`OnboardingViewModel.finish` carries a `seedExamples` parameter and a `seed()` body that no call site can reach (§13.2) — dead code, not a user-visible defect.*

**Redesign severity.** `REIMAGINE`

**Key opportunity.** Argue the actual case in four screens instead of setting a mood in four screens — starting with the one fact ("nothing leaves this device") that no competitor in the user's consideration set can say.

**Radical possibilities.** Re-derive per-theme onboarding art from the vector sources already in the repo. Replace the mood sequence with a single screen showing the product's real first-run state and one sentence about what it is. Make the example seeding explicit and reversible — or drop it, so the first-run Home is genuinely empty and §8.3's empty-day problem is *designed for* rather than inherited.

---

#### Home / Today

**Current role.** The app's primary daily surface. Answers *"What do I need to do today, and how am I doing?"* One `LazyColumn` with a collapsing artwork Hero, Remaining/Completed sections, a floating Quick Add.

**Current strengths.** The architecture is correct and matches `DASHBOARD_SPECS.md` exactly: one scroll surface, no nested scroller, one clamped px source of truth for the collapse (`:175-202`), sections derived from persisted state with no second copy of "is it done". `animateItem` with `placementSpec` means completing a row makes it **glide** to the other section rather than vanish — and this is the *only* screen in the app with item-level motion. `contentDescription` names the action. Reduced motion nulls the specs rather than shortening them. `ActivityCard` is an honest, compact, well-built row. The `Celebrate` effect proves the team thought about the 100% moment. `homeMetadataLabel` shows *live* target progress ("5/8 glasses"), which is the right thing to show.

**Current weaknesses.** (1) **The Hero's type stack inverts priority**: date 13 sp -> greeting 20 sp -> 20 sp % -> *"3 of 5 completed"* at **16 sp Regular** -> message 14 sp (§8.2). The screen's own answer is body text. (2) **The entire day-completion celebration is a `LongPress` haptic plus a snackbar reading "All done - momentum secured."** (`TodayScreen.kt:85-88`) — while `AuroraState.MILESTONE` and `MomentumMotion.reward` sit unused, and the Hero is simultaneously showing *"Everything complete. Rest easy."* The same news twice within 200 px (§7.8.13). (3) **An empty day renders a ~300 dp Hero containing a date and a greeting and nothing else** (§8.3). (4) The motivational slot is a filler: 4 of 6 messages contain no data and one is the product's own tagline used as an everyday state line (§17.3f). (5) `HomeEmptyState` uses a `Star` — the same glyph as the icon-key fallback (§17.3h). (6) The most decision-relevant number, the live "5/8 glasses", is `bodySmall` 12 sp — the smallest text on the card. (7) `HomeHero.kt` is 771 lines with 5 private layout constants and 3 custom draw layers against `HabitsHero`'s 222, and its own KDoc names Habits as the reference composition (§8.1). (8) Two permanent 6-second infinite animations whenever any progress exists (§7.8.13). (9) `QuickAddFab` is translucent with a 20 dp glyph while Habits' identical-purpose FAB is solid with a 28 dp glyph (§9.4). (10) `Loading` is a bare `Text("Loading today…")` with no type style and no skeleton (`TodayScreen.kt:122-126`), against design doc §568.

**Redesign severity.** `REIMAGINE`

**Key opportunity.** Give "how am I doing today" exactly one representation, at a size and weight that reflects its importance, and let the list be its detail. Everything else on this screen is either decoration or a second opinion.

**Radical possibilities.** Express the day's state as a shape rather than a number (§19.8). Collapse the Hero to content-derived height so an empty day is empty. Make the 100% moment a real moment using the two tokens that already exist for it. Replace the four-photo Hero with one artwork whose crop is driven by the day's state.

---

#### Habits

**Current role.** Management surface for habits. One list, Active then Paused, three-slot metadata, per-item overflow for Pause/Resume/Delete, a floating Add Habit.

**Current strengths.** The most spec-faithful screen in the app. Verified against `HABITS_MANAGEMENT_UI_SPEC.md`: one continuous list with **no** Active/Paused section headers (`:65-69`); status as **text plus a dot, never icon-only** (`:366-388`); an overflow with the correct per-status actions (`:336-361`); three-slot `Frequency · Target · Reminder` with `—` for missing (`:116-125`); delete behind a confirmation naming permanence; no streak, no progress, no history, no template classification on cards; `contentDescription = "Actions for ${activity.title}"`; `HabitIconTile` honouring `habitIconAccent`; correct Active-then-Paused sorting with `priority` as tiebreak. The collapse architecture is the same clean single-px-source pattern as Home's, in 40 fewer lines. `HabitsHero` is the reference composition.

**Current weaknesses.** (1) `.alpha(0.85f)` on the whole card when paused (`:292`) — the exact thing `ActivityCard.kt:49-53` documents as a contrast failure, and §90 of the spec forbids (§10.3). (2) The overflow menu is a **raw M3 `DropdownMenu`** (`:336-361`) — M3 surface, shape and colours — while the card around it is glass. (3) The delete confirmation is **translucent** (`MomentumDialog` -> `GlassLevel.HERO`), and the target dialog's own test asserts that exact pattern is a defect. (4) `AddHabitFab` is solid + shadowed + 28 dp glyph vs Home's translucent 20 dp one (§9.4) — and ADR-27 records the spec forbade this second FAB and "prompt won". (5) The `EmptyState` icon is `Icons.Rounded.Add` (`:172`) — the same glyph as the FAB, adjacent, for the same action. (6) `habitMetadata` is a single pre-joined String, so the three slots cannot be weighted or truncated independently; `maxLines = 1` means a long frequency can push the *reminder* off the end. (7) The accent-tile vocabulary reaches 3 surfaces of 5 (§7.4.4). (8) `AuroraBackground(REST)` — pinned to the lowest intensity (0.35, ~0.07 alpha in Dark), so the screen's "state-responsive Aurora" is static by construction. (9) `runCatching { reminderRepository.forActivity(id) }` per habit inside `combine` (`:70-73`) — one sequential suspend query per row on every emission.

**Redesign severity.** `EVOLVE`

**Key opportunity.** It is already the right screen with the right information architecture; it needs the *component language* brought in line with the rest of the app and its de-emphasis fixed, not a new structure.

**Radical possibilities.** Replace the overflow + M3 dropdown with the same management model used on Habit Detail, so pause/delete are one idea in two places. Make the list sortable or groupable by something the user chooses (currently `priority` then alphabetical, with no control). Let the metadata slots be individually styled so a habit's *target* is legible without opening it.

---

#### Habit Detail

**Current role.** Historical understanding of one habit: identity, key configuration, this week, streaks, full details, a centered bottom management group.

**Current strengths.** The **only** screen that earns `HABITS_MANAGEMENT_UI_SPEC.md`'s full information hierarchy. `DayCell` is the app's best data mark (§7.20 item 3) and the three-state week satisfies §193-195. `MetaCard` is the app's only correctly-ordered label/value pair (12 sp label, 14 sp SemiBold value). Streak shows **current and longest** per §7. `isCustomHabit` mirrors the creation screen's icon gate (`:284-289`), so the edit affordance appears exactly where §160-163 says and not elsewhere. `directionLabel` / `targetLabel` / `trackingLabel` translate BUILD/REDUCE x LIMIT/COMPLETE into plain words. The bottom action group is spec-compliant in *structure* (centered, weighted 1/1.2/1 so Edit is primary).

**Current weaknesses.** (1) **Only one `MomentumSectionHeader` on the whole screen** (`:197`) — the meta cards, weekly card and streak card are unlabelled, while `ActivityDetailScreen` labels every block. The two detail screens have opposite labelling strategies. (2) **"This Week" is 16 sp SemiBold and "4/7 completed" is 13 sp** (`:472-482`) — the answer is smaller than its own label. (3) `"$current day… streak"` is 16 sp and `"Longest: $longest day…"` is **12 sp**, on adjacent lines in the same card (`:571-579`). (4) The habit's **name** is `headlineSmall` + unregistered `Bold` (`:349-353`) — the same 20 sp as a section header. (5) `DayCell` has **no `contentDescription` at all** — the app's only three-state visualisation is invisible to TalkBack, while `CalendarScreen.DayDot` composes a proper one. (6) `.alpha(0.85f)` on the identity hero and meta cards when paused (`:178, 187`). (7) **240 dp of reserved padding** (`:171-173`) — a third of a small viewport before the habit's name. (8) The back button is a **raw `IconButton`** while the edit-icon button 8 dp away is a `LiquidGlassIconButton` (`:219-243`) — two materials, two press behaviours. (9) Delete is a 52 dp glass button 8 dp from Edit, same radius, same fill mechanics — §298 asked for *"visually separated enough to prevent accidental deletion"*. (10) `AuroraBackground(REST)`. (11) `dayOfWeek.name.take(1)` — ambiguous single letters, the third occurrence. (12) `"Just mark it done"` appears as the value in **both** the Target and Tracking cards (`:296-310`); and the value slot mixes quantities ("30 min") with sentences ("Zero is the goal"). (13) `IntrinsicSize.Max` forces a double measure pass for a purely cosmetic goal. (14) `MomentumRadii.card` (legacy alias) via a pointless one-use `RoundedCornerShapeCard()` (`:458-460`).

**Redesign severity.** `EVOLVE`

**Key opportunity.** This is the app's best screen, held back by the numeric layer and by one missing accessibility attribute. Both are foundation-level (§7.5), not screen-level.

**Radical possibilities.** Give it a window control and a value-aware mark (§19.5) — the use-case signature, not the screen, is the constraint. Replace the 240 dp of overlay padding with a shell-level chrome contract. Make the weekly strip the app's canonical three-state mark and propagate its vocabulary to the Calendar and Insights.

---

#### Activity Detail (the task / archived detail surface)

**Current role.** Detail and management for anything that is not an ACTIVE/PAUSED habit — i.e. **tasks and archived rows** (reached via `DetailTypeGate.onOther`). This is the screen the Tasks tab opens.

**Current strengths.** A real top bar with a three-part metadata subtitle and an Edit action. A "Today" card pairing the completion disc with a plain-language state (*"Completed"* / *"Not yet done"*) — the clearest statement of daily status in the app. `DetailActionRow` uses `onClickLabel` so TalkBack announces the action. The Actions card's `supporting` strings are genuinely good: *"Hide from Today, keep history"*, *"Retire without deleting history"*, *"Erases history and reminders"* — and the delete dialog offers the archive alternative (*"Archive keeps history"*).

**Current weaknesses.** (1) **A 2x2 grid of four equal `StatCard`s** with 20 sp values and 12 sp labels, no rank, no trend mark, no interpretation (`:222-249`) — the doc's four-level hierarchy reduced to one. (2) **`missedCount` and `consistencyScore` are computed and never shown** (§12.6). (3) `Trend` is rendered as `trend.name.lowercase()…` (`:276`), so the user can read the literal **"Insufficient data"**, and otherwise gets *"Improving"* with no magnitude, no window, no mark. (4) `DetailRow`'s label and value are **both 16 sp** (`:424-430`) — the label has no `style` at all, so it inherits `bodyLarge`; the densest, most reference-like surface in the app has zero typographic hierarchy between key and value, which `HabitDetail.MetaCard` in the same app gets right. (5) Delete sits in the same card as Archive, same size, same weight, separated only by a `c.error` tint (`:364-379`). (6) *"Recent history"* is 14 unlabelled 40 dp squares with `dayOfMonth` and `take(1)` weekday letters and **no month or year** (`:283-330`) — spanning two months with nothing to tell the user which. (7) A fifth icon-tile treatment (52 dp, `c.primary`), not `habitIconAccent`. (8) A hard-delete path, dismiss verb **"Keep"** while Habits' is "Cancel". (9) Reached through `DetailTypeGate`, so **every** Tasks row tap shows a `CircularProgressIndicator` first (§14.5). (10) Title at 40 dp — Activity Detail is one of the five screens affected (§8.4, corrected per F-2).

**Redesign severity.** `REDESIGN`

**Key opportunity.** It shows four numbers and an enum name where the product has a trend, a magnitude, a missed count and a consistency score. The data exists; the screen does not use it.

**Radical possibilities.** Make this the app's *analysis* surface and merge the Insights role into it, or delete the overlap: Insights answers globally, this answers per-activity, and today they duplicate three concepts in two visual languages. Give it a real time series. Lead with `consistencyScore` and a 30-day mark.

---

#### Tasks

**Current role.** `Routes.TASKS` -> `ActivitiesRoute(type = ActivityType.TASK, title = "Tasks")`. It is the **shared Activities list filtered to tasks** — not a distinct surface. Recorded intent: `HABITS_MANAGEMENT_UI_SPEC.md` §135, *"Do not redesign or change the existing Tasks destination as part of Phase 2B."*

**Current strengths.** Tasks and habits share one row component, delivering `MOMENTUM_DESIGN_SYSTEM_V1.md` §514-524 literally. `ActivityCard(streak = 0, …)` suppresses the badge through the component's own parameter. Recurrence + category in the metadata line is right for a management list. Three tabs with archive-and-restore as first-class. The delete dialog names the archive alternative.

**Current weaknesses.** (1) **An empty Tasks tab says "No activities yet"** (`:186`) and offers "Create activity" (`:191`) — the shared surface's vocabulary leaks through the filter, on a tab labelled Tasks (§11.2). (2) **Four names for one action**: "Add activity" (FAB), "Create activity" (empty state), "Create item" (sheet), and the sheet's own "Habit"/"Task". (3) **Up to two unlabelled 48 dp glass icon buttons below every row** (`:226-266`) — the Tasks row is ~2x the Home row's height and weight, and Pause/Archive are visually identical. No completion control at all, so the app's primary action is absent from the tab that holds one of its two object types. (4) **The app's only `OutlinedTextField`**, with `unfocusedTextColor`/`focusedTextColor`/`labelColor` left to unmapped M3 defaults (§11.4). (5) **Two visually identical `CategoryChip` rows** (status, then category) with no axis label (`:147-182`) — the filter surface that `HABITS_MANAGEMENT_UI_SPEC.md` §50 denied to Habits. (6) Subtitle `"${state.items.size} active"` — a bare count at 12 sp with no pluralisation (`:70-72`). (7) The third hard-delete path, dismiss verb "Keep".

**Redesign severity.** `REDESIGN`

**Key opportunity.** A tab labelled "Tasks" that speaks about "activities", has no completion affordance, and is twice as heavy as Home. It is a management surface wearing a tab's label.

**Radical possibilities.** Merge Tasks into Home (today's tasks already appear there) and give the fourth tab to something the product has data for and no screen uses — `consistencyScore`, `missedCount`, or a 30-day view. Or make Tasks a genuine action surface matching Home's row model, with management moved to detail. **Note the constraint:** `HABITS_MANAGEMENT_UI_SPEC.md` §135 forbids changing Tasks *as part of Phase 2B*; that phase is closed, so this is available — but it is a **product** decision (does Momentum need a separate Tasks tab?) and belongs in §7.25's questions, not in a design answer.

---

#### Calendar

**Current role.** Month grid plus the selected day's activities. Reached only from Insights' header action (`:238`), per the design doc §607. Not a tab.

**Current strengths.** The best accessibility in the repository: `DayDot` merges descendants and composes a real `contentDescription` (*"March 4, 3 of 5 complete"*) with `Role.Button` and `onClick` semantics (`:228-254`). The grid is built from plain `Row`s of 42 weighted cells with a stated engineering reason (`:159-160`: *"not lazy: 42 fixed cells compose cheaply and size responsively, with no nested-scroll measurement hazards"*) — that is a correct call, not laziness. `isToday` gets a 2 dp `primary` ring, a non-colour-only signal. `DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())` is correctly localised, and the KDoc honestly admits *why* it is abbreviated (`:108-109`): *"with the back affordance the full 'September 2026' no longer fits beside the month controls"* — the header is width-starved by its own action row.

**Current weaknesses.** (1) **The month grid cannot distinguish "missed" from "empty"** — the four-branch `wash` (`:222-227`) has no `scheduled > 0 && fraction == 0` case, so a day with 5 scheduled and 0 completed draws identically to a day with nothing scheduled, even though `cell.scheduled` is right there and is used in the accessibility description (§12.4). (2) **Partial days are `primary` blue and complete days are `success` green** (`:224-225`) — blue means "in progress" here and "the accent" everywhere else (§7.6.4). (3) **`.alpha(0.35f)` on out-of-month cells** (`:236`) dims the *day number* (`bodyMedium` 14 sp) to roughly a quarter of its contrast — on 14+ cells that are **interactive and in the accessibility tree**. `ActivityCard.kt:49-53` documents this exact failure class for Home. (4) `day.name.take(1)` (`:150`) — ambiguous weekday letters, and it reads the enum *name* rather than a localised display name, unlike `InsightsScreen.kt:283`. (5) `state.cells.chunked(7)` means a partial final week has cells **wider** than the complete weeks above it (each row is `fillMaxWidth()` with `weight(1f)`), so the grid's column alignment breaks whenever a month doesn't fill 35 cells. (6) The "Recent" list below reuses `ActivityCard`, so the month grid's rows carry a **streak badge** at 12 sp that competes with the day's own completion. (7) The top bar carries back + title + 2 chevrons + a "Today" text button in a 64 dp row — 5 interactive elements and a title already truncated by design. (8) `"$done of ${size} complete"` at 12 sp beneath a 16 sp section header, and `"Nothing scheduled."` is the entire empty state for a selected date. (9) Title at 40 dp — Calendar is one of the five screens affected (§8.4, corrected per F-2).

**Redesign severity.** `REDESIGN`

**Key opportunity.** The one screen in the app that can answer "did I do what I said I would, month to month" — and it cannot distinguish a missed day from a rest day, which is the single question a calendar is for.

**Radical possibilities.** Make the month grid a proper heat/absence map with the three-state vocabulary `DayCell` already proves, using `missedCount` (already computed). Move the month controls out of the app bar into a dedicated calendar header, freeing the title. Use the 60 days Insights already loads.

---

#### Activities (the shared list, reached via Tasks and archived states)

**Current role.** The shared `ActivitiesScreen` that backs the Tasks tab and any unfiltered use; it is also where archived rows are restored or deleted.

**Current strengths.** One continuous list component shared with Home and Calendar, so an activity looks like the same kind of object everywhere (`ActivityCard` is used on 3 screens). A 14-day history strip is available and correct. `completionActionLabel` correctly distinguishes "Enter today's progress for X" from "Complete X" based on the activity's completion method — so a target-based activity never promises a one-tap toggle.

**Current weaknesses.** (1) The search field's own text and label colours fall through to unmapped M3 defaults (§11.4). (2) Two unlabelled filter axes (§11.4). (3) Up to two unlabelled 48 dp management buttons per row, outside the card (§11.3). (4) The hard-delete dialog's dismiss verb is "Keep". (5) The Actions card on `ActivityDetailScreen` puts Delete beside Archive at equal weight.

**Redesign severity.** `EVOLVE`

**Key opportunity.** As the shared substrate for three surfaces, fixing its vocabulary once fixes Tasks and Activity Detail with it.

**Radical possibilities.** Absorb the filter rows into a single labelled filter bar; move per-row management into the same overflow Habits uses; make search a design-system field rather than a raw M3 one.

---

#### Create Item sheet

**Current role.** The `+` -> "Create Item" chooser: two options, Habit and Task, hosted as an M3 `ModalBottomSheet` over any tab.

**Current strengths.** Correctly thin and correctly scoped: *"No creation logic lives here"* (`:45`). The two option cards are real `liquidGlass(CARD)` surfaces with icon + title + a supporting line, and each has a proper `onClickLabel` (`"Create habit"` / `"Create task"`, `:106`). The copy is concrete — *"Build it consistently, one day at a time"* / *"Get it done, once or on repeat"* — and avoids the marketing register of onboarding.

**Current weaknesses.** (1) It is a **raw M3 `ModalBottomSheet`** with no `containerColor`, no custom shape and no glass (`CreateItemSheet.kt:54-59`). M3 sheets read `surfaceContainerLow`, an **unmapped** slot, so the app's only bottom sheet is rendered in a Material baseline surface colour outside the Aurora palette (§7.6.3), with M3's own elevation and scrim. (2) `MomentumSectionHeader(title = "Create item")` (`:71`) is a **section label used as a sheet title**, at the same 16 sp Regular as every in-page section header — so the sheet's title is typographically identical to a divider. (3) The option icons are semantically wrong: `Icons.Rounded.Refresh` (two arrows in a loop = sync/reload) for **Habit**, and `Icons.Rounded.CheckCircle` for **Task** — the same semantics as the app's own completion control. The Habits *tab* icon is `Icons.Rounded.Repeat` (a genuine recurrence glyph), so **the app has two different glyphs for "habit"**. (4) `Modifier.size(28.dp)` literal instead of `MomentumIconSizes.prominent` (which *is* 28 dp) — token drift inside the design system. (5) `.clip(RoundedCornerShape(card))` **then** `.liquidGlass(RoundedCornerShape(card), …)` (`:100-105`) — the same double-clip as `HabitCard`, so it is a copy-paste idiom rather than a one-off. (6) The sheet is a 1-of-2 question sitting in front of a 4-way intent model (`HabitIntent` + templates), so the most interesting decision is deferred past the point of choice.

**Redesign severity.** `REDESIGN`

**Key opportunity.** It is the app's single point of creation — the moment a user decides what Momentum is for — and it is currently the least designed surface in the product: an off-palette Material sheet, titled with a section label, using a reload glyph for the app's core concept.

**Radical possibilities.** Bring it into the glass system as a Momentum sheet. Replace the two-option gate with the actual intent model (BUILD / REDUCE) so the choice happens once, at the right altitude. Give Habit and Task correct, distinct glyphs that agree with the tab bar.

---

#### Habit / Task creation and config

**Current role.** `HabitConfigScreen` (26 KB) and `TaskConfigScreen` (10 KB), reused for both create and edit via `CreationNav`. `HabitConfigScreen` runs intent cards -> template grid -> details -> measurement -> optional fields, with a fixed `CreateCtaBar`.

**Current strengths.** The most substantial *information design* in the app. Four intent-driven section headers — *"What do you want to work on?"*, *"Choose a habit"*, *"Details (optional)"*, *"How will you succeed?"* / *"How much do you want to reduce?"* (`:143, 152, 209, 509, 534`) — and the two REDUCE sub-modes are correctly distinguished by *meaning*, not colour: `"Set a limit"` / `"Completely"` (`:541, 547`) with `"Limit"` vs `"Target amount"` vs `"Just mark it done"` as the tracking label (`:304-310`). `Expandable` correctly gates on `reduceMotion` (`CreationFields.kt:99-109`). `IconChoice` and `SelectionRing` provide a real selection language. `CreationNav` refuses Habit<->Task conversion at the type level (`:30-31`), matching `HABITS_MANAGEMENT_UI_SPEC.md` §265-279. The `templateDisplayLabel` indirection (`:428-443`) keeps short picker labels without breaking stored titles — careful work.

**Current weaknesses.** (1) **Section-header grammar is inconsistent across the app**: the creation screens mix interrogatives (*"When is it due?"*, *"Does it repeat?"*, *"What do you want to work on?"*) with noun phrases (*"Details (optional)"*), with no rule. (2) `"Just mark it done"` is a `labelLarge` **chip** here (`:516`) and a 12 sp/14 sp *card value* on Habit Detail (§10.4 item 12) — one concept, three components. (3) 13 of the app's 100 inline alpha literals live in `CreationFields.kt`, and it carries its own `Expandable` / `Explainer` / `IconChoice` primitives that are not in the component layer. (4) `Switch(` 8 call sites and `OutlinedTextField` across 4 files are raw M3 with partial colour overrides — the same unmapped-slot leak as the search field. (5) `RoundedCornerShape(14.dp)` (`:396`) is the only radius outside the 8/12/16/20/28 vocabulary. (6) `IntrinsicSize`-free but `height(IntrinsicSize.Max)` equivalent patterns and a fixed `CreateCtaBar` create a **fourth bottom-chrome pattern** (bottom nav on 4 tabs, floating FAB on Home/Habits, floating 3-button group on Habit Detail, fixed CTA bar on the 2 creation screens). ~~(7) Title at 40 dp (§8.4).~~ **Struck per F-2** — Habit Config places its top bar as a sibling above the padded list (`HabitConfigScreen.kt:124-130`) and is correct at 20 dp; it is one of the five screens that are *not* affected.

**Redesign severity.** `EVOLVE`

**Key opportunity.** The information design here is the best in the product and should be preserved almost intact; what is missing is a *component language* and a consistent section grammar, both of which are cheap to fix and would remove most of the inconsistency.

**Radical possibilities.** Make the intent choice the first and only gate. Move the optional fields behind a single progressive disclosure so the first screen is four decisions, not twelve. Unify "Just mark it done" / "Reach a target" / "Set a limit" / "Completely" into one tracking-mode component reused on the detail screen.

---

#### Settings

**Current role.** Five groups — Appearance, Notifications, Preferences, Data & privacy, About — reached from the Home Hero's Settings icon. Carries the app's only reduce-motion, motivational-messages, dark-mode and week-start controls, plus a clear-all-data action.

**Current strengths.** **The best-written copy in the product** (§7.20 item 9): *"Everything stays on this device. No account, no cloud, no tracking — offline by design."* / *"Reminders fire on-device. Enable notifications so Momentum can nudge you."* with a real `TextButton` that opens the platform notification settings. The privacy framing is a genuine product asset and it is stated plainly. The "Open notification settings" affordance correctly uses a platform `Intent` and `runCatching` (`:186-197`). The clear-data dialog names the exact consequence (*"All activities, completion history, and reminders will be deleted. This can't be undone."*) and uses a distinct dismiss verb (*"Keep my data"*). It is the only screen that shows the app's version.

**Current weaknesses.** (1) **"Reduce motion — Calm animations throughout the app"** is a **false claim**: the setting reaches 1 of 10 motion call sites (§17.3l). For a user with a vestibular disorder this is the most consequential defect in the audit. (2) **The whole screen is five copies of one pattern**: `[16 sp section label] -> [16 dp glass card]` with 1-4 rows of `titleMedium` + `bodySmall` and a trailing control. The section labels are the same size as the content they label, so the screen has five visually identical groups and no internal hierarchy (§17.3a). (3) **Three control vocabularies on one screen**: `CategoryChip` pills (dark mode, week start), raw M3 `Switch` (reduce motion, motivational), and a `Text` + `KeyboardArrowRight` "Change ›" (theme). Nothing connects them. (4) The same `CategoryChip` is used for a *setting* here and a *filter* on the Tasks list, with no visual distinction (§17.3c). (5) `DarkModeSetting.entries.forEach { mode.name.lowercase()… }` (`:155`) generates a user-facing string from a Kotlin identifier — a third enum-leak site (§17.3k). (6) The Theme row reads `themeById(themeId).name` (`:120`) which correctly shows "Aurora", while the Themes screen it opens reports the theme as **unselected** on a fresh install because `themeId` defaults to the deleted id `"twilight"` (§7.16) — so the two screens disagree. (7) The `MomentumDialog` clear-data confirmation is translucent, like all five. (8) Title at 40 dp — Settings is one of the five screens affected (§8.4, corrected per F-2). (9) `TextButton` for "Open notification settings" and "Clear all local data" are raw M3 with only a colour override — an unbounded-width text button in a glass card, where every other action in the app is a bounded pill or disc.

**Redesign severity.** `EVOLVE`

**Key opportunity.** The screen that carries the product's actual differentiator is also the screen with the flattest hierarchy and one false promise. Fixing the claim and giving the five groups distinguishable structure would make it the strongest screen in the app.

**Radical possibilities.** Make the privacy/offline statement a *first-class* element rather than a paragraph in a card — it is the product's one differentiator. Replace the three control vocabularies with one segmented-option component. Consider a "Your data" surface that shows the user's own counts (how many activities, how many completions) alongside the privacy claim.

---

#### Theme Gallery

**Current role.** A chooser over `MomentumThemes`, which holds exactly one entry. Reached from the Settings Theme row.

**Current strengths.** The *idea* is right and rare: visual theme as a user-facing, discoverable choice. `ThemePreviewCard` is a genuinely thoughtful component — a 56 dp swatch with a `theme.dark` gradient chip and three `theme.light` dots, labelled `"Light · Dark"` (`:134-155`), which honestly communicates that each theme has two environments without a second preview. The haptic is intentional.

**Current weaknesses.** (1) **The registry has one entry** (`Color.kt:139-141`), so the chooser offers one option and one confirmation. ADR-21 records the single-entry state as *deferred* — *"Settings' Theme row shows one entry until the Settings phase removes/reworks the gallery"* — and that phase never came. (2) **The default `themeId` is the deleted id `"twilight"`** (`Activity.kt:195`, `SettingsDataStore.kt:49`), so `selected = theme.id == selectedId` is false on any install where the user has not tapped a theme: **the screen reports the active theme as not selected**, with no checkmark and the card rendered at `CARD` rather than `HERO` — while Settings simultaneously displays "Aurora". (3) `ThemePreviewCard` renders its *labels* in `MomentumThemeColors.current` — the **currently active** theme — not the theme the card represents (`:100, 153`). **A theme gallery can never show a theme in its own context.** (4) The haptic is `HapticFeedbackType.TextHandleMove` (`:127`) — the constant for text-cursor movement, used as a generic "selection confirmed" tick. (5) The `Refresh`-glyph reuse problem appears in the app's own data: this screen is where a second theme would first become visible, and nothing about it would let a user judge a theme except a 56 dp gradient chip. (6) Title at 40 dp — Theme Gallery is one of the five screens affected (§8.4, corrected per F-2). (7) Its preview passes `selectedId = "cosmic"`, another deleted theme id (§7.3.6).

**Redesign severity.** `REDESIGN`

**Key opportunity.** The product has a data-driven theme *architecture* — registry, CompositionLocal, persisted selection, a gallery, a settings row — and a single theme. This is the screen where a genuinely different visual direction becomes a *user-visible product capability* rather than a code change, which makes it the natural landing point for whatever Step 2 produces.

**Radical possibilities.** Widen the concept from *environment* to *identity*: not just Light/Dark but a small set of named visual directions (the ADR-08 "12 launch concepts" idea, superseded by ADR-21), each previewed **in its own theme** on a real surface stack rather than as a gradient chip. Fix the default-id bug first — it is a one-line change and it currently misreports the app's state.

---

#### Reach-a-Target dialog

**Current role.** The numeric entry surface for target-based activities, opened by tapping a completion control on a target-based row.

**Current strengths.** The best-executed dialog in the app, and the only one that is right. It is **opaque** (`GlassLevel.DIALOG`, `dialogFill = 1.0f`) with the top-edge streak blended *into* an opaque stop rather than painted over it (`LiquidGlass.kt:90-93, 103-108`) — so the brightest part of the surface never reveals the layer behind. It applies an explicit window dim (`FLAG_DIM_BEHIND`, `setDimAmount(MomentumGlass.dialogScrim)`) so Home visibly recedes without the dialog being darkened. Its KDoc states the reasoning and the defect it fixes. It is the subject of `TargetEntryDialogSurfaceTest`, which asserts the opaque level, the scrim, the token, the contrast tokens and the presence of real Material buttons. The `"Clear"` action reverses a completion in one action, and above-target values are accepted unclamped — both correct.

**Current weaknesses.** (1) It is **bespoke**: it reads the dialog window through `LocalView.current as? DialogWindowProvider` and calls `window.addFlags/setDimAmount` in a `SideEffect` (`:91-99`). That is a low-level window manipulation no other surface does, and it exists only because `MomentumDialog` never grew the same capability. (2) `MomentumDialog` — the shared dialog, used for the app's four destructive confirmations — remains **translucent** (`GlassLevel.HERO`), and `TargetEntryDialogSurfaceTest:38` asserts that this exact pattern is a defect. **So the app's own test documents the bug, in the other component, uncorrected.** (3) `OutlinedTextField` and 3 `TextButton`s are raw M3, and the buttons are the only unstyled controls in an otherwise glass surface — `TargetEntryDialogSurfaceTest:73-74` asserts `>= 3` `TextButton(` occurrences, so the test *locks in* the inconsistency. (4) It duplicates `MomentumDialog`'s title style (`titleLarge` 20 sp) and its `MomentumDivider` usage. (5) The numeric keyboard is requested but there is no input affordance beyond the platform keyboard, and no visible target/actual relationship in the field itself.

**Redesign severity.** `EVOLVE`

**Key opportunity.** This is the app's reference implementation for a correct modal layer — opaque surface, explicit scrim, contrast-checked content, action-naming labels. Everything the shared dialog should be, and the destructive confirmations are not.

**Radical possibilities.** Promote it to *the* dialog component (opaque + scrim + real action buttons) and migrate all five `MomentumDialog` sites to it, resolving §9.6 and §17.3 at once. A numeric entry showing the target/actual relationship ("5 of 8 glasses") rather than a bare field would make partial progress legible without opening anything.

---


---

## 21. Critical Problems

Ordered by how much each one constrains the product, not by severity score. Each states what is wrong, why it is wrong, what it costs the user, and what KIND of change addresses it.

**CP-1. There is no numeric layer, so the product's central question cannot be answered at a glance.**
`Type.kt:103-119` collapses 15 Material typography slots into 7 sizes with no step between 28 and 20 or between 20 and 16. Consequently: Home's today-progress percentage is 20 sp (`ProgressOrb.kt:179`); the fraction "3 of 5 completed" is **16 sp Regular** (`HomeHero.kt:425-430`); the streak count is 12 sp (`MomentumBadges.kt:85`); a habit's live "5/8 glasses" is 12 sp (`TodayScreen.kt:413-426`); a habit's weekly "4/7" is 13 sp (`HabitDetailScreen.kt:479-482`); and the only 40 sp figure in the app is Insights' *7-day* rate (`InsightsScreen.kt:117-119`). Five call sites request `FontWeight.Bold` (700) from a `FontFamily` registering only 400/500/600, so the most important numbers render at an indeterminate weight. **`letterSpacing` and `fontFeatureSettings` occur 0 times app-wide**, so numerals are proportional and change width as they change value. The design doc §143 requires *"deliberate numeric hierarchy"*; `DesignSystemPreviews.kt:145` even states the intent in the preview that the code contradicts.

> **Feasibility correction (F-9b).** CP-1 previously carried an `Unknown` caveat: the audit had not parsed the TTF, so whether registering a 700 instance was possible — and therefore whether this recommendation was cheap or expensive — was unknown. **It is now verified.** `app/src/main/res/font/manrope.ttf` is a **variable font** (`fvar`/`gvar`/`STAT`/`HVAR` present), single axis **`wght` 200–800**, with **seven named instances including 700 (`nameID 267` "Bold")**. So the weight half of this fix is a `Type.kt` change, fully offline, with no new asset, no new dependency and no network fetch (AGENTS §2, §11). **CP-1 is not a capability problem and never was** — it is a type-scale decision plus a one-line family registration. The Unknown is removed from §7.28; the recommendation stands unchanged apart from being cheaper than stated.
*User effect:* **the answer to "how am I doing today" is body text, and it is smaller than the greeting above it and the same size as a row's metadata.** The app's loudest number describes last week.
*Kind of change:* a numeric treatment — a size step, tabular figures, a real emphasis weight — assigned by meaning, plus a decision about which one element is each screen's headline. This is a foundation change, not a screen change, and it unblocks §22's highest-impact items.

**CP-2. The entire day-completion celebration is a snackbar, while the machinery for a real moment exists and is unused.**
`TodayViewModel.kt:198-200` emits `TodayEffect.Celebrate("All done - momentum secured.")` on the 0->1 transition. `TodayScreen.kt:85-88` consumes it as `haptics.performHapticFeedback(LongPress)` + `snackbar.showSnackbar(...)`. Meanwhile `AuroraState.MILESTONE` (intensity 1.0) has **0 production call sites**, `MomentumMotion.reward` (650 ms) has **0**, the KDoc at `HomeHero.kt:431-438` says completion *"should feel satisfying"* and the design doc §500-510 permits *"stronger Aurora, subtle light burst, restrained particles, richer motion"* at milestones. `HapticFeedbackType.Confirm` exists and is unused; the app uses `LongPress` (a long-press semantic) as a generic event and `TextHandleMove` (a text-cursor semantic) for theme selection. The Hero is simultaneously displaying *"Everything complete. Rest easy."* (`TodayViewModel.kt:242`) — **the same news twice within 200 px, once as a static line and once as a toast.**
*User effect:* the single most satisfying moment the product has is communicated as a notification. The user reaches 100% on the most important day of their streak and the app says "All done" in a bar at the bottom of the screen.
*Kind of change:* implement the reward vocabulary that already exists, or remove the tokens. A change-triggered moment — the orb settling, the streak extending, one deliberate haptic — needs no new architecture.

**CP-3. The Settings reduce-motion toggle promises a whole-app effect and delivers 1 of 14 call sites.** *(Corrected — F-4: was "1 of 10". The correct denominator makes this materially worse, not better — see the note below CP-3.)*
`SettingsScreen.kt:164-173` presents "Reduce motion — **Calm animations throughout the app**", persisted via `DataStore` key `reduce_motion`, and written by `SettingsViewModel.setReduceMotion`. It is read by presentation in **exactly one place**: `TodayScreen.kt:98`, `rememberReduceMotion(s.reduceMotionOverride)`. Of the **14** `rememberReduceMotion` call sites in the product, the other **thirteen** pass `null` (system only): `MomentumAurora.kt:64`, `ActivityCard.kt:71`, `CompletionButton.kt:62`, `LiquidGlass.kt:146` and `:335`, `MomentumNavGraph.kt:109`, `HabitConfigScreen.kt:119`, `TaskConfigScreen.kt:76`, `HabitDetailScreen.kt:157`, `HabitsScreen.kt:99`, `OnboardingLiquidGlass.kt:140` and `:236`, `OnboardingScreen.kt:118`. Still animating: the bottom-nav tab transitions, the NavHost crossfade, every glass press scale, the completion pop, the ActivityCard tile tint, the `ProgressOrb` halo and lobe orbits, the Aurora drift on three screens, and all four onboarding sites.
*User effect:* **a user who needs reduced motion is told by the app it has been applied, and it has not been.** For a user with a vestibular disorder this is the most consequential single defect in the audit. It is also a *truthfulness* failure, which is why it sits in §17 and not only in §16.
*Kind of change:* thread one override through the theme (or a CompositionLocal) so all 14 sites read one source. A bounded, well-understood correctness change. `CompletionLabelTest.kt:97` currently asserts `rememberReduceMotion(null)` on a `CompletionButton` and would need updating with it.
*Strengthened per F-4:* the original text claimed "1 of 10" and enumerated ten sites plus "all of onboarding". The true figure is **1 of 14**, so the setting is inert on **thirteen** call sites rather than nine. Two consequences: the coverage gap is **larger** than originally reported, and the fix touches more sites than originally implied. Neither weakens any other conclusion in this audit; §7.26 item 9, §17.3l, §7.8 §7.8.12 and HO-7 have all been updated to the corrected figure.

**CP-4. Light Aurora has no working depth mechanism, and the two "environments" are one design at two luminances.**
The glass recipe is `c.surface.copy(alpha = level.fillAlpha)` (`LiquidGlass.kt:102`). In Dark, `surface #111827` is lighter than `background #080D18`, so translucency produces a real lift. In Light, `surface == surfaceElevated == #FFFFFF` against a `#F7F8FC` canvas, so a white translucent fill composites to roughly `#FBFCFE` — **invisible**. Every shadow is `Color.Black.copy(alpha = 0.30-0.35f)` with an accent spot (7 sites) — invisible on a light canvas by construction — and `MomentumElevation` has **0 production call sites**. The only thing separating a Light card from the page is `c.textPrimary.copy(alpha = 0.10f)`, a 1 px hairline. The doc §199 promises Light relies on *"tonal contrast, very soft shadows, and subtle borders"*; only the last exists. Across all five accents, the Dark value is the Light value lightened — no hue is introduced, removed or substituted, so no colour role changes between environments.
*User effect:* Light Aurora reads as flat and unfinished in a way Dark does not, and the user is given no indication which is the "real" design. A user who has never seen Dark experiences a different product from one who has.
*Kind of change:* decide whether Light is a designed environment or an inversion of Dark (§7.25 Q1). This is the single largest open question in the audit and it should be answered *before* Step 2 explores directions, because it determines whether a direction is exploring two environments or one.

**CP-5. Two progress vocabularies both fail the same test, and the one that passes it is on the least important screen.**
`CalendarScreen.kt:222-227` has four branches and none tests `cell.scheduled > 0 && fraction == 0`, so **a day with 5 scheduled and 0 completed is drawn identically to a day with nothing scheduled** — even though `cell.scheduled` is used in that cell's accessibility description at `:230`. `InsightsScreen.kt:269` uses only `day.rate`, discarding `DayRate.scheduled`, so the same conflation appears in the weekly bars. `HABITS_MANAGEMENT_UI_SPEC.md` §193-195 makes the distinction a hard requirement; `DASHBOARD_SPECS.md` §17-18 makes it foundational; `HabitDetailScreen.DayCell` (`:496-556`) implements it correctly. Compounding it, `CalendarScreen.kt:236` applies `.alpha(0.35f)` to out-of-month cells, dimming an interactive 14 sp day number to roughly a quarter contrast on 14+ cells in the accessibility tree — the exact failure `ActivityCard.kt:49-53` documents for Home.
*User effect:* **a user with a sparse schedule cannot tell slackeriness from rest**, in the two screens that exist to answer that question. And the two surfaces a user reaches *by scrolling a habit* are the two that get it wrong.
*Kind of change:* one mark family with a four-state vocabulary (empty / partial / complete / not-applicable) that `DayCell` already proves; remove whole-composite alpha from data marks.

**CP-6. The app's spec'd consistency metrics are computed by a tested engine and are invisible.**
`ActivityStats` (`Engines.kt`) carries `missedCount: Int` and `consistencyScore: Double?` — the latter documented as *"% of scheduled occurrences hit in last 30 days"* and named in `PRODUCT_SPEC.md` §86. **Both have 0 call sites under `presentation/`.** `InsightsViewModel` loads **60 days** of completions and `WeeklyBars` draws **7**. `Trend` — a 4-value enum including `INSUFFICIENT_DATA` — is surfaced only as `trend.name.lowercase().replaceFirstChar(Char::titlecase).replace('_',' ')` at `ActivityDetailScreen.kt:276`, so the user can read the literal string *"Insufficient data"*, and otherwise gets *"Improving"* with no magnitude, no window and no mark, in a screen with a 2x2 grid of four equal tiles.
*User effect:* for a product whose name is Momentum, the one number that would actually tell the user whether they are consistent is never shown, and the trend that is shown is a bare word.
*Kind of change:* surface the two metrics and give the trend a key number and a mark. No new architecture — the engine is pure, typed and tested.

**CP-7. The identity system is defined four times, keyed differently, and reaches three of five surfaces.**
Four colour vocabularies: Aurora Light/Dark (24 tokens), `Twilight` (14 constants, onboarding only), `PhotoShade` (1), `HabitIconAccents` (7, one fixed set for both themes) — plus the launcher mark's implicit fifth (`#A78BFA` / `#5EEAD4` / `#FF8A7A`, which appear in **neither** theme). `habitIconAccent(iconKey)` and `templateAccent(templateId)` are two independent `when` expressions over two different key spaces that happen to agree for all 10 shipped templates. The accent reaches `HabitIconTile`, `HabitConfigScreen` and `HabitDetailScreen`; `ActivityCard` (Home, Calendar, Tasks), `ActivityDetailScreen`, `InsightsScreen` and the `CreateItemSheet` all use flat `c.primary`. `MomentumIconOptions` (18 keys offered), `momentumIcon` (20 keys rendered) and `habitIconAccent` (20 keys coloured) are three non-aligned vocabularies: the app's own `custom_build` template ships with `iconKey = "spark"`, which the renderer cannot draw (falls to `Icons.Rounded.Star`) and the colour map does not name (falls to `peach`). `Activity.colorArgb` exists in the domain model, the Room entity and the mappers, and is read by **zero** presentation code.
*User effect:* "purple means meditation" does not accumulate, because the association breaks every time the user leaves the Habits tab; the most important choice in creation is a fallback star in a fallback colour; and the icon the user sees on the launcher promises a palette the app does not use.
*Kind of change:* one authoritative key registry; one decision about whether per-activity colour is a system or a local accent; a decision about whether the mark's palette or the theme's is canonical. **This is a decision, not a refactor** — Step 2 should make it explicitly.

**CP-8. "Glass everywhere" is the shipped default, and the locked design document forbids it.**
`MOMENTUM_DESIGN_SYSTEM_V1.md` §214-219 lists, under **"Not allowed as a default treatment for"**: every card, every row, navigation, entire screens, every button. §33 rejects "glass everywhere". The shipped app: `LiquidGlass.kt` is the design system's core primitive; `LiquidGlassCard` is the default card; `ActivityCard` rows use `liquidGlass`; `LiquidGlassBottomBar` navigation uses `GlassLevel.HERO`; `LiquidGlassButton` is the primary button. Measured: **9 distinct surface fill values** across the app against 4 tokenised; `StatCard` and `StreakBadge` bypass `liquidGlass` and re-implement the recipe inline; `EmptyState` derives a fifth level as `cardFill * 0.7f`; `MomentumCard` — the named card component — has **1** production call site. And "Liquid Glass" names a material the app does not implement: `MomentumGlass.backdropBlur = 0.dp`, with the honest reason that minSdk 26 has no reliable cross-device blur API.
*User effect:* every surface in the product looks like the same surface, so nothing is emphasised; and the depth cue the name promises is absent, so translucency reads as flatness rather than as material.
*Kind of change:* a terminology decision plus a deliberate distribution of surface levels — glass for the few things that earn it, something else for the rest. The design doc already says this; the implementation overrode it.

**CP-9. A destructive confirmation in this product can be translucent, and the app's own test says so.**
All five dialogs are `MomentumDialog`, which uses `GlassLevel.HERO` (0.68 translucent) — and **all five are destructive confirmations**: delete activity (`ActivitiesScreen.kt:99`), delete permanently (`ActivityDetailScreen.kt:110`), delete habit (`HabitsScreen.kt:210`), clear all local data (`SettingsScreen.kt:276`), and the fifth, `HabitDetailScreen.kt:133`. *(Corrected: the original text said "the four destructive ones". The Reviewer verified at C-4 that all five are destructive, which makes the finding slightly stronger.)* `TargetEntryDialogSurfaceTest:37-39` asserts that a dialog "must opt into the dialog surface" (`GlassLevel.DIALOG`) and `assertFalse(dialog.contains("GlassLevel.HERO"))` with the message *"translucent hero glass is what made Home show through"*. That test exists because this exact defect was found and fixed **in the one bespoke dialog**. The shared dialog still ships it. Compounding: three different dismiss verbs across the five ("Keep" x2, "Cancel" x2, "Keep my data"), and the clearest copy in the app — *"Archive keeps history"* — sits inside a panel you can see the list through.
*User effect:* the highest-stakes interactions in the product are the least legible ones, and the copy explaining the consequence competes with the content behind it.
*Kind of change:* promote the correct dialog (opaque + explicit scrim) to the shared component and migrate the five sites. This is a small, bounded, high-consequence change, and a test already describes the target state.

**CP-10. The top-bar inset is unenforced on 5 of the 10 screens that carry one, and three inset strategies coexist.**
`MomentumTopBar` applies `MomentumSpacing.screen` internally (`MomentumComponents.kt:60-63`). On Insights, Calendar, Activity Detail, Settings and Theme Gallery it is an `item {}` **inside** a list whose `contentPadding` also applies 20 dp — so the screen title sits at **40 dp** while every sibling sits at 20 dp. Habit Config, Task Config, Activities and Creation Nav place the top bar as a sibling above the list and are correct at 20 dp. Three inset strategies coexist overall: edge-to-edge with 0 top (Home), overlay chrome reserving 72 dp top + 168 dp bottom (Habit Detail, **240 dp of a possibly-640 dp viewport**), and Scaffold insets everywhere else.
*User effect:* a visibly ragged left edge on five screens, and a habit detail screen where a third of the display is chrome before the habit's name.
*Kind of change:* one screen-shell contract for insets and chrome clearance, applied at the Scaffold.
*(Corrected — F-2: was "seven of nine screens … and the Edit flow's margin jumps". The count is 5 of 10; the Edit-flow margin-jump sub-claim and its user effect are withdrawn, and CP-10 is renumbered.)*

---


---

## 22. Highest-Impact Opportunities

Ordered by leverage against the problems in §7.23. These are *opportunities*, not a plan.

**HO-1. A numeric register, assigned by meaning.** Addresses CP-1, and unblocks the Home Hero, `StatCard`, `ProgressOrb`, the weekly strip, the calendar summary and the live target label simultaneously. *What it makes possible:* the product can finally show "3 of 5" larger than "Good morning", can give the streak a size that matches its importance to the product, and can make a changing number not reflow. *Evidence to bring:* rendered type specimens at the real sizes, plus a contrast measurement of each tier against both canvases and the Light hero artwork (the `ISSUE-020` measurement method). *Constraint:* §7.26 keeps Manrope and the offline bundle.

**HO-2. One mark family with a four-state vocabulary.** Addresses CP-5 and CP-6 and collapses 7 representations into one. *What it makes possible:* "which day did I miss" becomes answerable on Home, Insights, the Calendar, the habit week and the orb, because a not-scheduled day stops looking like a missed one everywhere at once. *Evidence to bring:* the existing `DayCell` is the prototype — bring it forward as the starting point rather than inventing anew. *The single highest-leverage structural item after HO-4.*

**HO-3. A real reward moment, using the hooks that already exist.** Addresses CP-2. *What it makes possible:* the 100% moment becomes the emotional centre the product is named for, using `AuroraState.MILESTONE`, `MomentumMotion.reward`, a deliberate haptic, and change-triggered motion instead of a snackbar. *Evidence to bring:* a motion sequence that respects reduced motion from the first frame, demonstrated on the 0% -> 100% transition. *Note:* `TodayEffect.Celebrate` already exists as a typed one-shot channel — this is a behaviour change, not an architecture change.

**HO-4. One surface primitive, one press policy, one section label.** Addresses CP-8 and CP-9 and collapses roughly 14 measured counts (§3.3, §9.2) into three decisions. *What it makes possible:* a screen can be built from the library without inventing a new alpha, and "is this card the same as that card" stops being a per-screen question. *Evidence to bring:* a component gallery rendered on the real `AppBackground` stack (not a flat box), light and dark, showing the surface levels actually in use.

**HO-5. Surface `consistencyScore` and `missedCount`; give the trend a key number and a mark.** Addresses CP-6. *What it makes possible:* Insights and Activity Detail answer a question rather than listing numbers. *Evidence to bring:* the numbers are already computed and tested — this needs a layout, not an engine. *Constraint:* §7.26 keeps the append-only completion history and the streak rule.

**HO-6. One identity registry, and one decision about per-activity colour.** Addresses CP-7. *What it makes possible:* habit identity becomes learnable; the creation flow's custom template stops being a fallback star; the mark and the theme stop disagreeing. *Evidence to bring:* the key registry rendered as a sheet, so the whole set can be judged at once — and a decision, not a refactor, about whether `Activity.colorArgb` becomes user-facing.

**HO-7. Fix the reduce-motion override and the false claim.** Addresses CP-3. *What it makes possible:* the app stops lying to a user who needs it. *Why it is high-impact despite being small:* it is an accessibility correctness fix, it is bounded, and it currently makes a promise the code does not keep. *Evidence to bring:* all 14 call sites reading one source, plus the claim's copy matching the behaviour. *(Corrected — F-4: was "the ten call sites"; the override reaches 1 of 14. Note also that `CompletionLabelTest.kt:97` asserts `rememberReduceMotion(null)` on a `CompletionButton`, so that test must be updated alongside the fix.)*

**HO-8. A screen-shell inset contract.** Addresses CP-10 and unblocks five screens at once. *What it makes possible:* alignment becomes a property of the shell; the 240 dp Habit Detail reservation becomes a policy rather than a magic number. *Evidence to bring:* a shell with one content variant and the three chrome cases (top bar, floating bar, floating action group) shown side by side. *(Corrected — F-2: the scope is the 5 affected top-bar screens, not seven; the Edit-flow claim is withdrawn.)*

**HO-9. Resolve `DetailTypeGate` and give row -> detail a spatial model.** Addresses §14.4/§14.5 and unlocks the app's most-used transition. *What it makes possible:* the user keeps the object they tapped; the spinner on every detail navigation disappears. *Evidence to bring:* the shared-element relationship already present in the code (44 dp accent tile -> 72 dp hero tile) demonstrated as a transition.

**HO-10. Reuse the correct dialog everywhere.** Addresses CP-9 with almost no design work. *What it makes possible:* all five destructive confirmations become as legible as the target dialog, and the three dismiss verbs become one. *Evidence to bring:* the five sites side by side; a test already states the target state.

**HO-11. Make onboarding argue the case, and decide what it is relative to the theme.** Addresses §13.1/§13.3. *What it makes possible:* the product's one differentiator reaches the user before they commit, and the first-run experience stops being a different product from the app. *Evidence to bring:* the four pages rewritten against real product facts, and a decision on whether a Light onboarding is derivable from the vector sources already in the repo.

**HO-12. Fix the theme gallery's default-id bug and widen the concept.** Addresses §7.16. *What it makes possible:* the screen stops misreporting the active theme on a fresh install, and it becomes the natural place for whatever Step 2 produces to become user-visible. *Evidence to bring:* the one-line default fix, then a decision about whether "theme" means environment or identity.

---


---

## 23. Design Questions for Visual Exploration

These are questions this audit could not answer, derived from the findings above. They are the questions Step 2 must answer by *exploring*, not by deciding in a meeting. They are not derived from any illustrative brief; each names the finding that produced it.

**Q1. Is Light Aurora a designed environment, or an inversion of Dark?** *(This is the open question flagged in Slice 1 and it is the largest one here.)* The two environments share all five accent hues, with the Dark value simply lightened — no hue is introduced, removed or substituted. Light has no working translucency (surface and surfaceElevated are both `#FFFFFF` on a `#F7F8FC` canvas) and no working shadow (every shadow is black-alpha). So Light is, structurally, Dark with the luminance inverted and the glass mechanism inert. *This cannot be answered by inspection*: either answer is defensible and they lead to completely different explorations. If Light is a real environment, one direction explores two surfaces. If it is an inversion, one direction explores one surface and a brightness switch. **Answer this first — it determines what the other questions mean.**

**Q2. What is the product's one idea, and what is the app's name pointing at?** The app is called Momentum. The design doc rejects fire-streak gamification (§20, §33) and the app ships a fire icon for streaks anyway. It rejects emoji, and ADR-27 substituted a Material fire glyph. The day-completion moment is a snackbar. The "momentum" language appears in the tagline, in four of six Hero messages, in one onboarding headline, and nowhere in the product's *mechanics* as a visible idea. **Is momentum a metaphor for the user, a thing the app measures, or a visual behaviour?** If it is a visual behaviour, then the reward moment (CP-2) is the whole product and the rest is scaffolding. If it is a metaphor, the app needs to decide where it lives.

**Q3. Should "today" be a number, a shape, or a place?** HO-1 says the number is currently body text. But a number at 40 sp in a 96 dp ring is a different proposition from a number at 20 sp, and a *shape* — a completed-day silhouette whose notches are the remaining items — is a third. `TodaySections` carries the data for all three. **Which representation survives at a glance without reading digits?** And does it replace the list, or lead it?

**Q4. What is a habit's history — seven days, thirty days, or everything since?** `ComputeHabitOverviewUseCase` takes `Set<LocalDate>` and a hardcoded 7-day window. A "drink 8 glasses" habit cannot show an average because `CompletionRecord.value` never leaves the repository. **This is a data question before it is a design question**, and it changes what a habit detail page can be. Related: `HABITS_MANAGEMENT_UI_SPEC.md` §177 forbids a bar chart for the *weekly* view and its stated reason is that "a habit can be completed at most once per day" — which is a claim about the week, not about values. If per-day values are in scope, a month view is a different object from a week view.

**Q5. Is per-activity colour a system or a local accent?** Per-activity accent identity reaches 3 of 5 activity surfaces — `habitIconAccent` on the Habits list and the Habit Detail hero, `templateAccent` on the creation cards; `Activity.colorArgb` exists in the schema, is mapped, and is read by nothing. **Either honour colour everywhere (and expose `colorArgb`) or remove it from the three surfaces that use it**, because a partial system teaches the user nothing. This is a decision, and the design cannot be drawn until it is made.

**Q6. Does the icon set have to be Material?** Zero custom iconography; 100% `material-icons-extended`. **The KDocs and ADR-21 name "Material Symbols Rounded" and the build uses Material *Icons* — a different set with a similar name** (§7.4 §7.4.2, F-9c). **The design concept pack anticipates Material Symbols Rounded** as a webfont with a `100 700` variable weight axis, which the app's static vector set cannot offer at all. The app has one custom mark (the launcher) in a palette it does not use. **Three sub-questions, none of which this audit answers:** *which* family should the product use — the one ADR-21 chose, the one the concept pack ships, or a custom one; **is habit identity carried by a custom icon family, by a custom *treatment* of borrowed glyphs, or by something that is not an icon at all?**; and is the variable-weight capability the concept assumes worth having, given that the same glyph at different optical weights is a legibility tool rather than an identity one? And separately: is the launcher mark's palette or the theme's palette canonical? **Recorded as a question, not a recommendation — `R8` binds, and the direction is Step 2's.**

**Q7. Is the floating glass bottom bar the product's navigation model or a Phase 2B artefact?** ADR-27 locked it; the design doc forbids glass on navigation (§214-219) and `HABITS_MANAGEMENT_UI_SPEC.md` §294 asks for navigation *"visually integrated with the screen rather than becoming a large floating Material component."* The app has no competing bottom-nav precedent to compare against. **This is a live tension between two locked sources and it is a real design question, not a documentation error.**

**Q8. Does Momentum need a separate Tasks tab?** Tasks is the shared Activities list filtered to `ActivityType.TASK`; it says "activities" on an empty Tasks tab, has no completion affordance, and is twice as heavy as Home. The architecture implies management belongs in Habits and action belongs on Home. `HABITS_MANAGEMENT_UI_SPEC.md` §135 forbade changing Tasks only *as part of Phase 2B*, which is closed. **This is a product question with a navigation-model answer, and it determines whether the app has three or four meaningful destinations.**

**Q9. What is onboarding relative to the app?** Four photographic pages, dark-only, theme-blind, with their own palette and their own ink — and a hand-patched system-bar appearance because of it. The vector sources that would allow a Light variant sit unused in `artwork/onboarding/`. **Is onboarding a cinematic layer that is deliberately *not* the app, or is it the first screen of the app?** If the former, the hand-off into Home should be an intentional moment; if the latter, it needs a Light variant and the Aurora tokens.

**Q10. Where should "surface" come from in Light?** The glass mechanism is a luminance mechanism and it is inert on a light canvas. **Does Light Aurora need a different material entirely — an ink-and-paper treatment, a tonal one, a bordered one — or does it keep translucency and accept a flatter read?** This is a narrower cut of Q1 and the two are best answered together.

**Q11. What is the empty day?** CP-1 and §8.3: on a day with nothing scheduled the app shows a ~300 dp photographic Hero containing a date and a greeting, then the actual no-activity state below it. **Is the Hero the right surface to be empty, or should the day-with-nothing look deliberately different from the day-with-something?** This is the state a brand-new user meets, and a user on a rest day, and it currently looks like a bug.

**Q12. What does a 100% day feel like?** CP-2: `AuroraState.MILESTONE` and `MomentumMotion.reward` exist and are unreachable; the moment is a snackbar. **Is the reward a light change, a sound-free burst, a streak state change, a message, or nothing at all?** The design doc says "calm and rewarding rather than loud" and "no routine confetti" — which rules some options out but leaves most in. Note the tension: AGENTS §9 requires "streak celebration" micro-interactions; the design doc §19 says completion should not be loud. Both can hold only if the celebration is earned by a *milestone* rather than by *completion*.

**Q13. Should the three "themes" become identities?** `MomentumThemes` holds one entry. ADR-08 originally said *"12 launch concepts x light/dark"* and described themes as *"a headline feature"*. ADR-21 superseded that with two environments. **Is "theme" an environment switch or an identity choice?** The gallery, the registry, the DataStore key and the settings row all exist for the second interpretation and currently serve the first.

**Q14. How much of the Hero's machinery is load-bearing?** `HomeHero.kt` is 771 lines with three custom draw layers, a 24-sample blend, a 48-sample veil and five anchor constants, arrived at through ISSUE-015/016/018 and ADR-31/32/34/35/37/39. The app's own KDoc names the 222-line `HabitsHero` the reference composition. **Before exploring a new Hero, someone should establish which of those layers still prevents a defect that has been observed.** This is a question for Step 2 to *answer with evidence*, because answering it wrong means carrying 771 lines of machinery into a new direction.

---


---

## 24. What We Must NOT Change

Every item here is justified by a real constraint — a hard project requirement or a genuinely locked ADR — not by convenience or by the fact that something already exists. Items justified only by the latter belong in §19 as opportunities, and several do.

**Hard project constraints (non-negotiable, `AGENTS.md` §2):**

1. **Offline-only.** No network, backend, account, login, cloud, Firebase, Supabase, REST, auth, analytics SDK, ads or tracking. `AndroidManifest.xml` must never gain `INTERNET`. *Constraint, not preference* — and it is a *strength*: the product's most defensible differentiator is currently in Settings §268 and absent from onboarding (Q9, HO-11). Any direction that implies a backend, a sync affordance, a "sign in", or a share/social feature is out of scope. Note `PRODUCT_SPEC.md` §111-115 already excludes cloud sync, social features and leaderboards as v1 non-goals.
2. **On-device storage only.** Room + DataStore. No new persistence layer, no new dependency that implies one.
3. **Minimal permissions.** Notifications and alarms only. Any direction that implies location, health, contacts or files is out of scope — and note that a *visual* direction must not smuggle in a permission through a feature it implies.
4. **Single `app` module, manual DI via `AppContainer`.** No Hilt/Koin, no multi-module split. `MOMENTUM_FACTORY`-style manual ViewModel wiring (`ViewModels.kt`) stays.
5. **Clean Architecture layering.** `UI -> ViewModel -> Use Case -> Repository -> Local Data Source`. Business logic in use cases and engines, never in composables; domain stays pure Kotlin with zero Android imports (`Engines.kt` and `HabitOverview.kt` both honour this and are unit-tested without a device). **This constrains HO-2 and HO-5 directly**: a richer mark vocabulary or a surfaced `consistencyScore` must not require logic in a composable.
6. **`java.time` for all date logic**, ISO-8601 `yyyy-MM-dd` at layer boundaries, epoch-ms for timestamps. No UTC boundary for "today" — `TodayViewModel.kt:92-106` implements the local-midnight refresh and `DASHBOARD_SPECS.md` §385-393 requires it.
7. **Append-only `CompletionRecord` history.** Never rewrite or delete completion records when an activity is edited; edits apply forward from the edit date. **This constrains HO-5 and Q4**: any "history" visualisation must be built from records that are never mutated, and the new-history-lines feature in ADR-30/§19.4 is a *design* change that does not touch it.
8. **The streak rule.** Only *scheduled* occurrences count; unscheduled days are ignored and never break a streak (`DASHBOARD_SPECS.md` §364-383, `StreakEngine`). This is the product's core promise and it is the reason a "missed day" must look different from a "rest day" (CP-5). Any direction that makes a streak look broken when it is not is a correctness bug, not a style choice.
9. **Reduced motion must always be respected** (`AGENTS.md` §9). The current implementation is 1-of-10 (CP-3) — that is a bug to fix, not a constraint to preserve. The *constraint* is that no direction may introduce motion that cannot be disabled.
10. **Accessibility floors that are contract, not taste:** 48 dp minimum touch targets, 4.5:1 body-text contrast in both environments, dynamic-type-safe layouts (no fixed heights clipping large text), content descriptions for all icon-only controls, state never communicated by colour alone, correct TalkBack order. `PRODUCT_SPEC.md` §120 lists these as v1 success criteria. `ISSUE-020` is an open, deferred instance of the contrast floor.

**Genuinely locked architectural decisions:**

11. **Manual DI and the `AppContainer` graph** (ADR-09). Same as (4); listed because a visual direction that introduces a per-screen theme object could look like a DI change and is not.
12. **`uiMode` changes do not recreate `MainActivity`** (ADR-23). Live-updating Light/Dark in place is locked, and the reason is a real crash. Any direction must keep theme switching in-place — which is a constraint on *how* the theme is delivered, not on *what* the theme looks like.
13. **Data-driven theme architecture** (ADR-08's token-class + CompositionLocal approach, which ADR-21 explicitly retained: *"its token-class + CompositionLocal approach stands"*). A registry + CompositionLocal, not hardcoded per-screen colours and not M3 dynamic colour.
14. **One unified `Activity` concept** with differentiation by category + recurrence + target, not by entity type (`PRODUCT_SPEC.md` §13-30, ADR-19). **This constrains Q5 and any per-type visual language**: Tasks and Habits share a visual grammar by decision (§22 of the design doc), and a direction that gives habits and tasks entirely separate visual languages would re-open a locked decision.
15. **The bottom navigation is four destinations** (ADR-22, ADR-26, design doc §281-295, `HABITS_MANAGEMENT_UI_SPEC.md` §124-130). Three-to-one majority (§14.2). **What is *not* locked is whether the bar is glass** (Q7) — that is open, and ADR-27's locking of the floating glass bar is in tension with the design doc's own prohibition.
16. **Calendar lives inside Insights and is not a navigation destination** (design doc §607, `MOMENTUM_NAV_GRAPH:238`).
17. **Settings is not a bottom-nav destination** (three sources agree).
18. **Guided creation with a type gate** (ADR-20): `CreationNav` refuses Habit<->Task conversion, and the Habit ID is preserved across edits so history stays attached (`HABITS_MANAGEMENT_UI_SPEC.md` §265-279). Any direction that lets a user transform one habit concept into another contradicts this.
19. **Local reminders via `AlarmManager` + `WorkManager`, no FCM** (ADR-07). A direction must not imply push notifications.
20. **No hardcoded colours in screens** (`AGENTS.md` §5). Currently honoured — 0 raw hex outside the theme layer and onboarding. **This is a constraint on how a direction is expressed, not on what it looks like**, and it is the cleanest thing in the foundation (§7.3.2).

**Explicitly NOT protected by this list**, so that Step 2 is not misled:

- The 7-token type scale, the 15->7 slot collapse, the absence of tracking, and the three-weight family (§7.5). The *typeface* is protected; the scale is not.
- The Light/Dark token *values* (§7.9 §7.9.20, Q1). Two environments must exist; what they contain is open.
- "Liquid Glass" as a name or as the default surface treatment (§7.7.3, CP-8). The locked *prohibition* is the design doc §7/§33 one — glass must not be the default for every card, row, navigation, screen and button — so a direction that redistributes surface levels is *closer* to the locked design, not further.
- `AuroraState.MILESTONE`, `MomentumMotion.reward`, `springSnappy`, `springGentle`, `easeInOut`, `progressTrack`, `auroraMint`, `surfaceInteractive`, `textDisabled`, `MomentumElevation` — dead tokens with no production call site (§7.10, §3.3). They are design-system *surface area*, not locked decisions.
- The `HomeHero` machinery (§7.26 Q14). Locked ADRs govern the *outcomes* (no seam, artwork to the window edge, no hard edges) — not the 771-line implementation.
- The single-theme gallery. ADR-21 records the single entry as *deferred*, and the Settings phase that was meant to resolve it never happened. It is recorded debt, not a locked decision.

---


---

## 25. Recommended Direction for Step 2

**This is a process recommendation. It deliberately names no palette, no type stack, and no aesthetic.** §7.25's fourteen questions are what Step 2 is for; naming a direction here would foreclose them, which `R8` forbids and which would make the audit's value the choice rather than the evidence.

#### How many directions

**Three, plus one control.** Three is the minimum that can distinguish a *system* decision from a *taste* decision: with two, every disagreement looks like a preference; with four or more, the comparison collapses into a survey. The **control** is not a fourth direction — it is the current app, rendered and screenshotted in both environments under the same conditions. Without a control, "premium" has nothing to be measured against, and `B2` means the repository has no baseline image at all.

Each direction must be a **complete, internally consistent** proposal across all four: Light, Dark, motion, and one representative data-heavy screen. A direction that is only a Light mood board, or only a Home screen, cannot be judged — §7.16/§7.17 established that this app's problems are systemic and cross-cutting, and a partial direction will hide exactly the defects that matter.

#### What to hold constant across all directions

These are the things that, if they vary, will make the comparison meaningless or will violate §7.26:

- **The whole product scope.** Offline, on-device, no account, no permissions beyond notifications (§7.26 items 1-3).
- **The information architecture as a starting point** — but see below; the *nav model* is explicitly a variable.
- **`ActivityCard` as a row concept and the manage/act/understand division** (§7.20 item 2). If a direction re-merges these surfaces, it is answering a different question than the one Step 2 was asked, and should be presented as such rather than as a variant.
- **Accessibility floors** (§7.26 item 10). A direction that cannot be rendered at large font sizes or read at 4.5:1 has failed, not varied.
- **The `CompletionButton` interaction model** (§7.20 item 1). Its accessibility label promises behaviour; that contract should survive every direction.
- **The same content in every direction.** Same habits, same streaks, same empty day, same 100% day, same sparse schedule, same paused habit, same target-based activity. **The content is the test.** A direction that looks good on a full Home with 8 activities and collapses on the empty day or the 3-activity day has not been evaluated.

#### What to vary

- **Q1 first, and it is not a variable — it is a precondition.** Whether Light is a designed environment or an inversion (§7.26 CP-4). Settle this *before* drawing, because it determines whether a direction is exploring two surfaces or one. **A direction that does not state its answer to Q1 has not been submitted.**
- **The numeric register** (Q3, HO-1): how "3 of 5" is expressed, at what size, and whether a shape replaces a number.
- **The surface system** (Q10, CP-8, HO-4): what a card, a control and a floating layer are *made of* — and how many distinct levels exist and what earns each.
- **The mark vocabulary** (Q2, Q5, Q6, HO-6): how a habit is identified, and whether per-activity colour is a system.
- **The motion language** (Q2, Q12, HO-3): what happens at 0%, at 100%, and at a milestone; what is continuous and what is triggered.
- **The reward moment** (Q12, CP-2): the specific behaviour at the day's completion, honouring "calm and rewarding, not loud" *and* "no routine confetti" — which means it is earned by a milestone, not by every completion.
- **The Hero** (Q14): how much of the current 771-line machinery is retained, with evidence for which layers still prevent an observed defect.
- **Onboarding** (Q9, HO-11): what the first four screens argue, and whether they are the app or a layer above it.
- **The navigation model** (Q7, Q8): the Tasks tab question, and whether the bottom bar is glass.

#### What each direction must produce

Per direction, a single artefact set:

1. **Both environments, at the same content** — a Light and a Dark rendering of the same screens, not one mood and one correction.
2. **A type specimen at real sizes**, showing where the numbers land relative to the headings, plus a contrast measurement per tier against both canvases and the Light hero artwork (the `ISSUE-020` method, since that measurement is the only contrast evidence in the repository).
3. **A surface ladder** — every distinct surface level, what earns it, and what happens to it in Light. §7.7.2 found **9** fill values in the current app against **4** tokenised; a direction should state its own count and justify it.
4. **A mark vocabulary** — the four states (empty / partial / complete / not-applicable) in every form the product uses, rendered, with a sparse-schedule day and a rest day side by side. **This is the single most important artefact in the set**, because it is where CP-5 lives and because `DayCell` already proves the product can do it.
5. **Four states of the same screen**: 0%, mid-day, 100%, and an empty day — plus a paused habit and a target-based activity. §7.10's problems and §8.3's empty-day problem are all state problems.
6. **A motion spec**, not a video: what triggers, what duration, what the reduced-motion behaviour is, and what the milestone moment is. §7.8 found **6 of 9** motion tokens unused against **16** hardcoded durations; a direction should state which of its motions are which.
7. **A one-page statement of what it *removed*.** Given §3.3's 100 inline alphas, 9 fill values, 7 shadows, 4 date formats and 2 press languages, the most honest measure of a direction is what it consolidates. A direction that adds to the surface area should have to justify each addition.

#### What evidence to bring

- **A control**: current app, both environments, the six states above, at the same content. `B2` means this does not exist and must be produced first.
- **Both environments always.** A direction delivered in one environment has not been evaluated against CP-4.
- **Measured, not asserted.** Contrast ratios (ISSUE-020's method exists), the type sizes actually used at each level, the number of distinct surface fills, the number of motion call sites and which token each uses. §7.10's numbers are the baseline; a direction that cannot state its own is not comparable.
- **The four data marks at real data**, including a Mon/Wed/Fri habit so the not-scheduled state is exercised (§12.4), and a category with a zero rate.
- **Large font and reduced-motion renders.** CP-3 and §7.20 item 7 mean both are failure modes, not edge cases.
- **An explicit answer to Q1**, and to whichever of Q2/Q5/Q9 the direction depends on.
- **A statement of what it would break.** The nine source-text visual tests (§3.5) assert specific implementation text; a direction that changes the hero artwork geometry, the dialog surface, or the motion literals will break them. That is worth knowing *in advance* — and it is an argument for replacing those tests with behavioural ones at some point, not an argument to avoid the change.

#### A process caution

The strongest evidence in this audit is that **the product's own documentation disagrees with itself in places** (§3.4: glass-everywhere, dialog opacity, motion tokens, the numeric hierarchy, the icon family name; §14.2: a stale nav spec; §9.2: three dismiss verbs for one slot; §9.6: KDoc versus behaviour in `ProgressOrb`). Every one of those was found by reading code against intent, not by looking at the app. **Step 2 should assume the same is true of the remaining artefacts and should spend its first pass reconciling intent against implementation before it spends any pass on a direction.** A direction designed against the *documented* intent would inherit "glass everywhere" and a 12 sp streak; a direction designed against the *measured* current state would at least know what it is improving.

---


---

## Appendix A. Slice 1 method & evidence base

**What was read in full (token-level, not skimmed).**
`app/build.gradle.kts`; `gradle/libs.versions.toml`; `app/src/main/AndroidManifest.xml`; every file in `app/src/main/res/` (complete inventory, 19 files); **every file in `design/aurora-concept/` (complete inventory, 8 files — `fonts.css` read in full, all seven `.woff2` headers verified) and the `manrope.ttf` binary parsed table-by-table;**
`presentation/theme/{Color,Type,Tokens,Shapes,Elevation,Motion,Theme,Twilight,DesignSystemPreviews}.kt`;
`presentation/ReduceMotion.kt`; `presentation/aurora/MomentumAurora.kt`;
`presentation/components/{LiquidGlass,AppBackground,MomentumComponents,MomentumBadges,MomentumIcons,MomentumHeroArtwork,ProgressOrb,HabitIconTile,ActivityCard,CompletionButton}.kt`;
`presentation/navigation/MomentumNavGraph.kt`; `presentation/screens/today/TargetEntryDialog.kt` (surface half);
`MainActivity.kt`; `domain/model/Activity.kt` (settings/colour fields only); `data/datastore/SettingsDataStore.kt`;
`docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` (whole file — it is 16 KB and the intent baseline for this slice);
`docs/product/PRODUCT_SPEC.md`; `ISSUES.md` (ISSUE-020 detail); `PROJECT_STATUS.md` (F-HOME / F-HABITS rows only).

**Read selectively (grep-then-targeted-read), per the bounded-investigation rule.**
`docs/architecture/decisions/DECISIONS.md` — only ADR-08, ADR-18, ADR-21, ADR-23, ADR-27 were opened (the UI/visual entries named in §5 KNOWLEDGE_REFERENCES); the 39-entry heading list was enumerated with a heading-only scan and the remaining 33 entries were not read.
The nine visual/UX-relevant test files under `app/src/test/.../presentation/**` were read for their assertion shape; the 22 domain/data/notification tests were not read.

**Counts were produced by search, never estimated.** Every number below came from a `Select-String` sweep over `app/src/main/java/com/momentum/app/presentation/**/*.kt` (or the narrower path stated). The sweeps run were: `Color(0x…)` literals; `Color.Gray`; `Color.White|Color.Black`; `copy(alpha = <literal>`; `surfaceVariant`; `fontSize`; `letterSpacing`; `fontFeatureSettings|tabularFigures`; `FontWeight.(Bold|Black|ExtraBold|SemiBold)`; every `animate*AsState|animateFloat|animateDp|animateColorAsState|AnimatedVisibility|AnimatedContent|updateTransition|rememberInfiniteTransition|Crossfade`; `tween(<integer>`; `spring(`; each `MomentumMotion.*` / `springSnappy` / `springGentle` / `MomentumDurations.*` token by name; `rememberReduceMotion`; `AuroraState.`; `habitIconAccent`; `habitIconTile(`; `RoundedCornerShape(<literal>`; `.shadow(`; `MomentumElevation`; legacy vs v1 token aliases; `isDark`; `themeId = "…"`; and per-token usage counts for all 24 colour tokens, all 4 text tokens and all 9 spacing tokens.

**What could NOT be determined, and is therefore marked unknown in the text below.**
- No rendered pixels were inspected. There is **no screenshot, no mockup, no Figma/Sketch/Penpot link, no layout or composition reference, and no visual-regression fixture** in the repository (blocker B2, as narrowed by F-9a — see below). Every judgement about *how the hierarchy reads* is inferred from token values and Compose semantics, not observed. Wherever the distinction matters the text says so.

> **CORRECTION (F-9a, Orchestrator-raised).** The original text of this bullet read *"There is no screenshot, Figma file, design reference or visual-regression fixture in the repository"* — a claim of **total absence**, which is **false**. A design concept directory does exist: **`design/aurora-concept/`**, holding `fonts.css` (7,805 bytes) and `fonts/f1.woff2` … `f7.woff2` (seven WOFF2 files, 2,552–5,399,668 bytes). **What exists:** a web font concept pack — Manrope declared at weights 400/500/600/700 across six unicode-range subsets (latin, latin-ext, vietnamese, greek, cyrillic, cyrillic-ext), plus one `Material Symbols Rounded` variable font at weight axis `100 700`, plus a `.material-symbols-rounded` utility class. All seven files verified as complete `wOF2` containers (`magic='wOF2'`, `flavor=00010000`, declared length == on-disk length). **What does not exist:** anything that shows the app — no screenshot, no mockup, no layout or composition reference, no colour/spacing spec, no Figma/Sketch/Penpot link, no golden file. **Verified unreferenced:** a repo-wide search of `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.json`, `*.md` (excluding `.git`, `build`, `.opencode`, `.agents`) for `aurora-concept`, `design/aurora`, `fonts.css` and `f7.woff2` returns **zero** matches — the pack is read by no build rule, no resource and no document or ADR. **The substantive conclusion is unchanged and still holds: no *rendered* visual reference exists, so every pixel-level judgement in this audit remains unknown.** What was wrong was the claim of *total* absence.

- No build, no emulator, no Robolectric run, no font rasterisation. ~~**Unknown — not determinable from the repository:** whether `res/font/manrope.ttf` is a variable font with a `wght` axis beyond 400–700, and what glyph coverage it has. The file is 164,700 bytes, single, with no companion `font/*.xml` family resource.~~ **RESOLVED by F-9b** — the TTF was parsed directly and is a variable font; see §7.5 §7.5.1 and §7.28.
- **Unknown — not determinable from the repository:** the exact hex values that Material 3 fills into the colour-scheme slots that `MomentumTheme` does not pass (see §7.6). The *structural* fact — that only 15 slots are passed and the remaining library-default slots therefore apply — is determinable and is stated as fact; the specific default values **and the number of unmapped slots** are library source, not repository source, and are not asserted. *(Corrected — F-3: was "only 12 slots are passed"; the count is 15.)*
- `HomeHero.kt` (35 KB) and `HabitsHero.kt` were **not** read line-by-line; they are cited only where a grep result is unambiguous (token usage counts, `Color.White|Black` counts, `isDark` at `HomeHero.kt:54,488`). Anything about hero composition is deferred to Slice 2 and is *not* claimed here.
- `PROJECT_STATUS.md` and `ISSUES.md` were read for context only, per scope.

---


---

## Appendix B. Section map and cross-reference convention

**F-7 — added.** `R7` fixes a 25-section order for `MOMENTUM_PREMIUM_DESIGN_AUDIT.md`. The mapping from this file's sub-headings to that order is defined here, once, so an assembler transcribing §7 does not have to infer it. **The numbering is deliberately not sequential**: foundation (§2–§7) is written first, then Motion (§15) and Light/Dark (§16) because they qualify the foundation, then Layout through Navigation (§8–§14), then the synthesis sections (§17–§25). The Reviewer verified this ordering is correct against `R7`.

| `### 7.x` | Deliverable section (`R7` order) | Heading in `task.md` |
|---|---|---|
| **7.1** | **1. Executive Summary** | *(new — F-6)* |
| 7.0a | *(this map — not a deliverable section)* | Section map & cross-reference convention |
| 7.0 | *(not a deliverable section)* | Slice 1 method & evidence base |
| 7.2 | 2. Current Product Understanding | §7.2 — Current Product Understanding |
| 7.3 | 3. Existing Design System Audit | §7.3 — Existing Design System Audit |
| 7.4 | 4. Visual Identity Audit | §7.4 — Visual Identity Audit |
| 7.5 | 5. Typography Audit | §7.5 — Typography Audit |
| 7.6 | 6. Color Audit | §7.6 — Color Audit |
| 7.7 | 7. Surface & Depth Audit | §7.7 — Surface & Depth Audit |
| 7.15 | 15. Motion Audit | §7.8 — Motion Audit |
| 7.16 | 16. Light/Dark Audit | §7.9 — Light/Dark Audit |
| 7.8 | 8. Layout & Composition Audit | §7.12 — Layout & Composition Audit |
| 7.9 | 9. Component Audit | §7.13 — Component Audit |
| 7.10 | 10. Habit Experience Audit | §7.14 — Habit Experience Audit |
| 7.11 | 11. Tasks Audit | §7.15 — Tasks Audit |
| 7.12 | 12. Insights Audit | §7.16 — Insights Audit |
| 7.13 | 13. Onboarding Audit | §7.17 — Onboarding Audit |
| 7.14 | 14. Navigation Audit | §7.18 — Navigation Audit |
| 7.19 | 17. Generic Pattern Audit | §7.19 — Generic Pattern Audit |
| 7.20 | 18. Existing Strengths | §7.20 — Existing Strengths |
| 7.21 | 19. Radical Redesign Opportunities | §7.21 — Radical Redesign Opportunities |
| 7.22 | 20. Screen-by-Screen Verdict | §7.22 — Screen-by-Screen Verdict |
| 7.23 | 21. Critical Problems | §7.23 — Critical Problems |
| 7.24 | 22. Highest-Impact Opportunities | §7.24 — Highest-Impact Opportunities |
| 7.25 | 23. Design Questions for Visual Exploration | §7.25 — Design Questions for Visual Exploration |
| 7.26 | 24. What We Must NOT Change | §7.26 — What We Must NOT Change |
| 7.27 | 25. Recommended Direction for Step 2 | §7.27 — Recommended Direction for Step 2 |
| 7.28 | *(not a deliverable section)* | Unknowns carried to Step 2 |
| 7.29 | *(not a deliverable section)* | Correction log — Reviewer findings F-1…F-8 and Orchestrator finding F-9 |

**Cross-reference convention used throughout §7.** Two forms appear, and they mean different things:

1. **`§7.N` or `§7.N §M.n`** — a *sub-section heading* in this file. The deliverable number follows after the em dash, e.g. `§7.5 §7.5.3` means "the Typography section, sub-heading 7.5.3". Example: `§7.9 §7.9.18` = the Light/Dark sub-heading 7.9.18.
2. **`§7.N item M`, `CP-M`, `HO-M`, `Q-M`, `R-M`** — a *numbered item* in a list inside that sub-section, not a heading. Examples: `§7.10 item 5` (fifth critical finding carried into Slice 2), `CP-7` (Critical Problem 7), `HO-4` (Highest-Impact Opportunity 4), `Q1` (Design Question 1), `R-2` (design-doc rule).

**Correction to the Reviewer's suggested examples.** The Reviewer proposed re-pointing `§7.9.20` → `§7.9` and `§7.8.13` → `§7.13`. Those re-pointings would be wrong: the deep references such as `§7.9.20` and `§7.8.13` are **not** cross-references at all — they are the *own-subsection numbers* of the section doing the citing, and re-pointing them to a different sub-section would change which text is being discussed. Only the genuinely dead form `§7.10.5` (a heading prefix followed by an item number) needed fixing, and it is now `§7.10 item 5` in the two places it appeared (§7.19(d), and §7.26 item 5's pointer). This convention note is the durable fix and it is what the Reviewer asked for as the alternative.


---

## Appendix C. Slice 1: critical findings carried into Slice 2

Ordered by how much they constrain what Step 2 can meaningfully explore. Each is grounded; none is a design directive.

1. **The numeric layer does not exist.** Figures are 20sp SemiBold with an out-of-family `Bold` request, streaks are 12sp, no tabular figures, no tracking, and the design system's own preview contradicts the code. → §7.5.3. *This is the direct answer to "could someone understand their momentum at a glance": no, and the cause is typographic.*
2. **Light Aurora has no depth mechanism.** The glass fill is chromatically inert on a near-white canvas; the declared `surface == surfaceElevated` collapse removes the tonal ladder; shadows are black-alpha and therefore invisible in Light. Depth in Light is a 1 px hairline. → §7.1, §6.2, §7.17.
3. **"Liquid Glass" is a material name for a translucency recipe, and it is the default treatment for everything** — cards, rows, navigation, buttons, dialogs — which is the exact pattern the locked design doc (§7, §33) forbids. → §3.4, §7.3.
4. **The brand mark's palette (`#A78BFA` / `#5EEAD4` / `#FF8A7A`) appears nowhere in either theme.** → §7.4.1.
5. **Per-activity colour exists in the schema (`Activity.colorArgb`, mapped, persisted) and is read by zero UI code**; the UI's only per-item colour is a hardcoded 20-key lookup, honoured on 3 of 5 activity surfaces — via `habitIconAccent` on two and a parallel `templateAccent` map on the creation cards. → §7.2, §7.4 §4.4, §7.14 §10.5. *(Corrected — F-5: the original attributed all three surfaces to `habitIconAccent`.)*
6. **The motion token layer is decorative** — 6 of 9 members unused, 16 hand-written durations in its place — and the design system's one consistent token (`ambient`, 6 s) is used only for two permanent infinite loops on the primary screen. `AuroraState.MILESTONE` and `MomentumMotion.reward` are both unreachable. → §7.8.10, §7.8.13, §7.8.14.
7. **`success` carries five unrelated meanings** (completion, active status, streak milestone, calendar heat, trend polarity), and completion (green-teal) and progress (blue) are different hues on the same screen. → §7.6.4.
8. **Not every M3 colour-scheme slot is mapped by `MomentumTheme`** — it passes 15, and the unmapped ones keep library defaults from a palette Aurora does not use, while the app uses M3 components that read them. *The number of unmapped slots is `Unknown — not determinable from the repository`: it is `ColorScheme` library surface, not repository source.* → §7.6 §6.3. *(Corrected — F-3: was "the 28 M3 colour-scheme slots".)*
9. **Two ambient background systems in different hues stack on 3 of 11 destinations**, and one of them (the app-wide one) has no state parameter. → §7.7.4.
10. **The app's reduce-motion setting reaches 1 of 14 motion call sites**; the system-bar appearance follows the system uiMode, not the app's theme setting; onboarding ignores the theme entirely and has no Light artwork. → §7.8 §7.8.12 *(Corrected — F-4: was "1 of 10".)*, §7.17, §7.18.
11. **Four colour vocabularies coexist** (Aurora, Twilight, `PhotoShade`, `HabitIconAccents`) plus the mark's implicit fifth. → §7.4.6.
12. **Zero custom iconography; the three key vocabularies (picker / renderer / colour) have drifted apart** so "Spark" renders as a star and "Art" is colour-indistinguishable from a corrupted key. → §7.4.2, §7.4.3.
13. **The theme system ships a gallery with one entry**, and its default `theme_id` is a deleted id, so the gallery misreports selection on a fresh install. Recorded debt (single entry) plus an unrecorded bug (default id). → §7.16.
14. **There is no visual regression protection.** The nine "visual" tests assert source text and WebP byte headers; the one instrumented test asserts bounds and says so. → §7.3.5. *Step 2 should assume no automated safety net for any visual change it evaluates.*
15. **The design system's own previews do not show the production surface stack** (they render on a flat `background` box), and 8 of 44 previews reference deleted theme ids. → §7.3.6.
16. **Every judgement in this section is static code reading.** There are no screenshots, mockups, layout references or Figma links in the repository, and the one design concept directory that does exist — `design/aurora-concept/`, a web font pack — shows nothing about the app's appearance (B2, narrowed by F-9a). Slice 2 should not treat any statement here about *how something reads* as observed fact.

---


---

## Appendix D. Out-of-slice observations

- `HomeHero.kt` is 35 KB — the single largest UI file, larger than the entire token layer — and is the only place `Color.White`/`Black` (5 sites) and `isDark` (2 sites) combine at scale. Screen audit, Slice 2.
- `QuickAddFab` is a 56 dp circular `GlassLevel.HERO` glass FAB on Home (`TodayScreen.kt`, pinned by `QuickAddTest.kt`), and a *second* 56 dp circular glass FAB is pinned bottom-end on Habits (`HabitsScreen.kt:236-242`) — plus an in-list add button is asserted *gone* from Home. Two screens, one pattern, different triggers. Component/screen audit, Slice 2.
- `TodayScreen`'s list uses `Modifier.animateItem()` with `placementSpec`/`fadeInSpec` (§7.8, `HomeSectionMotionTest.kt`); `HabitsScreen`/`ActivitiesScreen`/`CalendarScreen`/`InsightsScreen` do not. Home is the only screen with item-level motion. Screen audit, Slice 2.
- `MomentumCard` (`MomentumComponents.kt:133-145`) is a zero-logic delegate to `LiquidGlassCard`. Two names, one component — a naming ambiguity for anyone (or any agent) reading the component layer. Component audit, Slice 2.
- `CreationFields.kt` is 31 KB and carries 13 of the 100 inline alpha literals plus its own `Expandable`, `Explainer` and `IconChoice` primitives that are not in the component layer. Component audit, Slice 2.
- `calendar` is reachable only from Insights (`MomentumNavGraph.kt:238`), not as a tab — consistent with ADR-22 and the doc's §11/§26. Noted as compliant; navigation audit, Slice 2.
- The `day`-cell and Insights visuals (`CalendarScreen.kt`, `InsightsScreen.kt`) are the only two places in the app drawing data marks, and both use `c.surfaceVariant` as a track (`CalendarScreen.kt` via `MomentumLinearProgress`, `InsightsScreen.kt:254`) — §7.6.3 applies to the whole Insights/Calendar visualization layer. Insights audit, Slice 2.
- `docs/product/features/dashboard/DASHBOARD_SPECS.md` and `HABITS_MANAGEMENT_UI_SPEC.md` are referenced by the ADRs as authoritative and were **not** read in this slice (out of scope for the foundation). Slice 2 should read them before writing §8–§14, since `ADR-27` records that they conflict with `ADR-22`/`ADR-26` on nav order.

---


---

## Appendix E. Unknowns carried to Step 2

Everything that could not be determined from the repository, consolidated from both slices. **Unknown — not determinable from the repository** unless stated otherwise.

**Rendering and perception (blocked by B2 — no screenshots, mockups, layout references or Figma/Sketch/Penpot links exist in the repository; a design *concept* directory does exist, `design/aurora-concept/`, but it is a web font pack and shows nothing about the app's rendered appearance. Narrowed per F-9a — see §7.0. TASK-006 captured some emulator screenshots but they are not in the tree):**
- How any screen actually reads. Every judgement about hierarchy, balance or "premium-ness" in §7.2–§7.27 is inferred from token values and Compose semantics, not observed. `ISSUE-020` is the only measured contrast evidence in the repository (3.11:1 for Light `textSecondary` over hero artwork; 5.22:1 in Dark).
- Whether the Home Hero's veil reads as a card edge at its current 0.12 alpha budget, and whether the 3-layer mask is visible as such.
- Whether Light Aurora's flatness (§7.9, CP-4) is as severe in practice as the token arithmetic implies.
- Whether the five 40 dp top-bar titles (§7.12 §8.4) read as misalignment or as an intentional inset. *(Corrected — F-2: was "seven".)*
- Whether `CalendarScreen`'s `.alpha(0.35f)` out-of-month cells (§7.22) actually fail 4.5:1 — the arithmetic is derivable but the composited value is not.
- Whether the two FABs (§9.4) read as "the same button in two states" or as two different buttons.

**Fonts:**
- ~~The actual `wght` axis range and glyph coverage of `res/font/manrope.ttf`. The TTF binary was not parsed, so whether a 700 instance is available was unknown.~~ **RESOLVED (F-9b) — the Unknown is closed.** The TTF was parsed: it is **Manrope Version 4.505**, a variable font with a single `wght` axis **200–800** and **seven** named instances (200/300/400/500/600/**700**/800), TrueType outlines, default instance 200. **A 700 instance is present in the bundled file**, so the CP-1 weight fix needs no new asset and no network. Full evidence in §7.5 §7.5.1. **What remains Unknown:** **glyph coverage** — the `cmap` table is present (1,772 bytes) but its mapping was not enumerated, so which scripts the shipped subset covers is `Unknown — not determinable from the repository` without a font-mapping tool. It gates nothing in this audit.
- **Also newly recorded (F-9a/F-9c):** the repository contains `design/aurora-concept/` — `fonts.css` plus seven WOFF2 files (six Manrope unicode-range subsets + one `Material Symbols Rounded` variable font, weight axis `100 700`). All seven verified as complete `wOF2` containers, and a repo-wide search confirms **zero** references from code, resources, gradle files, docs or ADRs. It is a **provenance/concept artefact, not a design reference**: it contains no layout, no composition, no colour or spacing specification, and no rendered view of the app. Two consequences are recorded in the body — the F-9b font answer above, and the F-9c icon-system finding in §7.4 §7.4.2. Neither was in scope when the audit was written, because the directory was not found; the Orchestrator's inventory found it.

**Material 3 internals:**
- The exact hex values Material 3 fills into the ~28 `ColorScheme` slots `MomentumTheme` does not pass. The *structural* fact — that unmapped slots keep library defaults from a palette Momentum does not use — is determinable; the specific values are library source, not repository source (§7.6.3).
- Whether any unmapped slot is actually *read* by a component the app uses today. `ModalBottomSheet`'s `containerColor` and the search field's text colours are the two known candidates; whether they are visibly off-palette is not determinable statically.

**Product and governance (for the Orchestrator, not for the audit):**
- **Whether an ADR authorises the hard-delete path.** `deleteHard` is reachable from three dialogs and calls `activityRepository.deleteHard`. `AGENTS.md` §7 says hard delete "needs explicit UX + ADR"; the UX exists, but ADR-25 concerns the *archive* restore path. This audit could not determine whether a hard-delete ADR exists (§11.5).
- ~~**Whether the three seeded onboarding example activities are disclosed anywhere outside the code.**~~ **WITHDRAWN — corrected by F-1.** Both `OnboardingViewModel.finish` call sites pass `false` (`OnboardingScreen.kt:174`, `:266`), so `seed()` is unreachable and no activity is ever seeded. There is nothing to disclose. The residual point is recorded in §13.2 as dead code.
- Whether `DASHBOARD_SPECS.md` §3's Settings-in-the-bottom-bar position is genuinely superseded or simply never updated (§14.2). This audit concluded "stale documentation" on a three-to-one source count; the Orchestrator owns the doc change.

**Not read in either slice (deliberately, per scope and the bounded-investigation rule):**
- `HomeHero.kt` was read in full in Slice 2. `HabitsHero.kt` was read in full in Slice 2. `CreationFields.kt` (31 KB), `HabitConfigScreen.kt` (26 KB), `HabitDetailScreen.kt` (690 lines, read to line 579 of the composable body), `ActivityDetailScreen.kt` (read from line 95), `CreationViewModel.kt`, `TaskConfigScreen.kt` and `OnboardingLiquidGlass.kt` were read **partially or by targeted structure scan**, not in full. Claims about them are limited to what the read portions and the structure scans establish.
- `domain/`, `data/` and `notification/` were read only to establish available data (§7.12, §7.16, §10.6) and one Settings/ViewModel default. **No engine, DAO, repository or notification behaviour was audited** — including whether `missedCount` and `consistencyScore` are *correct*, only that they are computed and not displayed.
- 33 of the 39 ADRs in `DECISIONS.md` were not read (only ADR-08, 18, 21, 22/26/27 by reference, 23, 25 by title, 30-39 by title, and 14-17 via the onboarding-art chain in §13.4).
- 22 of the 31 test files were not read (all domain/data/notification tests). The 9 presentation tests were read for their assertion shape.
- `docs/product/features/create-item/CREATE_ITEM_SPECS.md` was **not** read. §7.22's creation verdicts rest on `HABITS_MANAGEMENT_UI_SPEC.md`, `MOMENTUM_DESIGN_SYSTEM_V1.md`, `DASHBOARD_SPECS.md` and the code. **A creation-flow verdict in §20 should be treated as provisional until `CREATE_ITEM_SPECS.md` is read** — the same class of gap that `DASHBOARD_SPECS.md` and `HABITS_MANAGEMENT_UI_SPEC.md` closed in Slice 2, and the most likely place for another spec/code divergence to be hiding.
- `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` §§38-128 were read in Slice 1; the screen-specific sections were read as part of the whole-file read. `docs/product/DASHBOARD_SPECS.md` was read in full in Slice 2.

**Judgements that are explicitly qualified rather than unknown:**
- The `Route.CREATE_INTENT` / `Route.CREATE_HABIT` prefix relationship (§14.6) is flagged as a nav-model smell. **I am not claiming a navigation bug** — navigation-compose matches registered patterns and `RoutesTest.kt` exists. It is a candidate for the Reviewer to exercise.
- The `templateAccent` / `habitIconAccent` agreement (§10.5) was verified for all 10 shipped templates by cross-reference. It is correct **today**; it is not derived, so it is fragile rather than wrong.
- Whether `create-habit` creating an activity with a *future* start date and a weekly recurrence visibly confuses a first-time user (§7.22, Habit Config) is a **judgement about user comprehension**, not a code fact. It is stated as a design question in that verdict, not as a defect.

---


---

## Appendix F. Correction log

Added so the corrections are auditable rather than silent. F-1…F-8 were applied in response to the Reviewer (`task.md` §10 findings, §11 verification); **F-9 was raised by the Orchestrator after that pass closed**, from a repository inventory the audit had not run, and is logged separately below so it is not confused with the review cycle. All were applied by the Architect to `task.md` §7 only; no application code, resource, doc, ADR, `opencode.json`, `design/**` or issue-registry file was modified. The Reviewer's own record is in §10 and §11 and is unedited.

| # | Severity | What was wrong | What changed | Outcome |
|---|---|---|---|---|
| **F-1** | **BLOCKER** | §13.2 claimed the onboarding final CTA passes `true` and that a first-time user would find three undisclosed activities. **False.** I read `OnboardingViewModel.finish`'s signature and the Skip call site, then *inferred* the CTA's argument from the parameter's name without reading line 266 — and stated the inference as fact. | §7.17 §13.2 rewritten from "active defect" to **dead-code observation**, with both call sites (`:174`, `:266`) and the `:261-263` KDoc quoted. Struck from: the Onboarding verdict weakness (4), §7.17 recommendation 2, and the §7.28 unknown. | Fixed. No user-visible behaviour was ever affected. **An `ISSUE-NNN` opened from the original claim must be withdrawn** (Orchestrator-owned). |
| **F-2** | MAJOR | §8.4 claimed **7 of 9** `MomentumTopBar` call sites land the title at 40 dp, and that the Edit flow's margin jumps 20 dp when data arrives. I quoted real line numbers but inferred the wrong *structure* from them — `HabitConfigScreen`/`TaskConfigScreen` place the top bar as a sibling **above** the padded list. | Corrected to **5 of 10** (Insights, Calendar, Settings, Theme Gallery, Activity Detail). The Edit-flow sub-claim and its user effect are **withdrawn**. Table rebuilt with the structural distinction shown. Propagated to CP-10, HO-8, §7.10 item 10, §8.7 rec. 4, §7.28, and four screen verdicts; the Creation/Config verdict's item (7) is struck. | Fixed. The finding's *direction* survives — a real ragged left edge exists, on five screens. |
| **F-3** | MAJOR | §6.3 said `MomentumTheme` passes **12** `ColorScheme` slots, then enumerated **15** in the same paragraph. The 12 propagated into §7.10 item 8 as "the **28** unmapped slots". | 12 → **15** in §6.3 and in §7.0's unknown list. The self-contradicting enumeration is now consistent. The "28" is removed from §7.10 item 8 and replaced with `Unknown — not determinable from the repository`, since it is `ColorScheme` library surface. | Fixed. The structural conclusion (unmapped slots keep Material baseline defaults) was never in dispute and stands. |
| **F-4** | MINOR | "1 of 10" `rememberReduceMotion` call sites, with a parenthetical enumerating 10 named sites *plus* "all of onboarding" (4) — summing to 14 while being called nine. | Corrected to **1 of 14**, with **thirteen** null-passing sites enumerated. Updated in §7.8 §7.8.12, §7.10 item 10, §17.3(l), §7.22 Settings, CP-3, HO-7, and §7.28. Added an explicit *strengthening* note: the gap is **larger** than originally reported, not smaller. | Fixed. Also added the Reviewer's observation that `CompletionLabelTest.kt:97` test-pins `rememberReduceMotion(null)` and must change with any fix. |
| **F-5** | MINOR | §4.4's table attributed the creation cards' colour to `habitIconAccent(iconKey)`, contradicting §10.5 and factually wrong — `HabitConfigScreen.kt` never calls it. | §4.4's row now reads `templateAccent(templateId)` with a cross-reference to §7.14 §10.5 and a correction note. §7.10 item 5, §7.19(d) and §7.25 Q5 re-worded to "3 of 5 surfaces, via `habitIconAccent` on two and `templateAccent` on one". | Fixed. The 3-of-5 outcome is unchanged. |
| **F-6** | MAJOR | Deliverable **§1 Executive Summary was missing** — 24 of 25 `R7` sections. | Authored **§7.1**, drawing only on claims already established in §7 (principally §7.10 and §7.23). Contains the product in a paragraph, the single most important finding, the ten critical problems, the screen-severity summary, and an explicit statement that no visual direction has been chosen. | Fixed. Names no palette, type stack or aesthetic, per `R8`. |
| **F-7** | MINOR | Sub-section cross-references used a scheme that does not exist (`§7.10.5`). | Added **§7.0a**, a full `### 7.x` → deliverable-section map plus a two-part cross-reference convention. Corrected the one genuinely dead form to `§7.10 item 5`. **Declined** the Reviewer's suggested re-pointing of `§7.9.20` → `§7.9` and `§7.8.13` → `§7.13`: those are the *citing section's own* sub-numbers, not cross-references, and re-pointing them would change which text is under discussion. Reason stated in §7.0a. | Fixed. |
| **F-8** | MINOR | Two absence claims were overstated: "the other **53** days … discarded at the view boundary", and "no interpretation" / "no comparison cue". | §7.16 §12.1 corrected: **30 of the 60** days are consumed by the 30-day rate and per-activity windows; only **days 31–60** are discarded. The "+N pts vs 30-day average" delta at `InsightsScreen.kt:121-130` is now acknowledged as the screen's one real comparison cue, and connected to §7.6 §6.4's `success`/`warning` claim as the same line. §12.5's "no comparison cue" narrowed to the two lists that genuinely lack one. | Fixed. Both claims were directionally right; they are now stated as what is true. |

**Additional correction, not in the Reviewer's list.** §7.23 CP-9 and §9.6 said *"all five dialogs … including the four destructive ones"*. The Reviewer verified (C-4) that **all five are destructive** — delete activity (Activities), delete permanently (Activity Detail), delete habit (Habits), clear all local data (Settings) — and the count of *destructive* dialogs is 5, not 4. Corrected at both sites and in HO-10.

### F-9 — Orchestrator-raised, after the review cycle

Raised from a repository inventory the audit had not run: **`design/aurora-concept/` exists** and the audit never mentions it. F-9 is a **factual gap**, not a re-audit, and the F-8 discipline — narrow the claim to what is true — has been applied to a claim the audit itself made.

| # | What was wrong | What changed | Outcome |
|---|---|---|---|
| **F-9a** | §7.0 asserted a claim of **total absence** of design reference material, and §7.10 item 16, §7.1 and §7.28 leaned on it. **False**: a design concept directory exists. | §7.0's method bullet, §7.10 item 16, §7.1's closing caution and §7.28's rendering heading all now **narrow** the claim: *no screenshot, no mockup, no layout/composition reference, no colour-or-spacing spec, no Figma/Sketch/Penpot link, no golden file*; and `design/aurora-concept/` is named as a **web font concept pack** (`fonts.css` + seven WOFF2 files) that is **verified unreferenced** by a repo-wide search. | Fixed. **The substantive conclusion is unchanged and still holds** — no *rendered* visual reference exists, so every pixel-level judgement remains unknown. The claim of *total* absence was the error, not the conclusion. |
| **F-9b** | The `wght` axis range of the bundled `manrope.ttf` was recorded as `Unknown`, with the note that **CP-1's cheapest fix depended on it**. The TTF had not been parsed. | **`Unknown` closed by direct parse.** `manrope.ttf` is **Manrope Version 4.505**, a **variable font** (`fvar`/`gvar`/`STAT`/`HVAR` present, 19 tables, `sfntVersion 0x00010000`), single axis **`wght` 200–800**, default 200, with **seven named instances** at 200/300/400/500/600/**700**/800 (`nameID 267` = "Bold", `268` = "Manrope-Bold"). Updated at §7.0, §7.5 §7.5.1, §7.5 §7.5.3(a), §7.5 §7.5.5 rec. 2, §7.23 CP-1, §7.1 and §7.28. | **Closed. And it corrects an overstatement in the audit's favour-of-the-defect direction:** §7.5 §7.5.3 said the family *"does not provide"* 700. It does — the file contains it and `Type.kt` simply does not register it. CP-1 is a **registration gap, not a missing capability**, so the fix is a `Type.kt` change with **no new asset, no new dependency, no network fetch** (AGENTS §2, §11). The user-visible severity is unchanged; the cost is now known and lower than stated. **Retained as Unknown: glyph coverage** — `cmap` is present (1,772 bytes) but unmapped; gates nothing. |
| **F-9c** | §7.4 §7.4.2's "zero custom iconography" finding was made without reference to the concept pack, which the Orchestrator notes **expects Material Symbols Rounded**. | §7.4 §7.4.2 now states the three-way relationship factually: the app's **code** uses Material **Icons** (`androidx.compose.material:material-icons-extended`); the app's **documentation** (3 KDocs, the design doc §8/§32, ADR-21) says Material **Symbols** Rounded; the **concept pack** ships Material Symbols Rounded as a webfont at `font-weight: 100 700`. Conclusion recorded: **one decision, not honoured** — ADR-21 recorded a family the build never adopted — and the substantive consequence is functional, not aesthetic (a static vector set has no weight axis, so the concept's variable-weight idea is unavailable to the app). | Fixed, and **deliberately not resolved**. Recorded as **§7.25 Q6** and left open per `R8`; no icon set is recommended. **Unknown:** whether `f7.woff2`'s glyph set is genuinely Material Symbols Rounded — the woff2 container is verified (`wOF2`, `flavor=00010000`, complete) but its brotli-compressed table directory was not parsed, so the font's own `name` table is undetermined. The Orchestrator's characterisation is consistent with the CSS and is marked as not independently verified. |

**A note on how F-9 was found, because it bears on trust in the rest of §7.** The audit inventoried `app/src/main/res/` completely and searched the whole tree for images, but did not inventory **top-level directories** other than the ones it was given, and therefore never saw `design/`. F-1's root cause was citing a line I had not read; F-9a's root cause is a narrower one — *searching for the artefacts I expected rather than enumerating what exists*. Both are recorded here rather than quietly fixed, because the same blind spot could hide something Step 2 needs and this audit did not surface. The Orchestrator's inventory is the reason it was caught; a Reviewer pass over §7 could not have found a directory the audit never claimed to have looked at.

**What was deliberately not changed.** No finding that survived verification was removed or softened. The Reviewer confirmed 11 of 15 quantitative claims and 6 of 6 spec contradictions exact; F-4's correction makes one finding **worse** and is recorded that way, and F-9b's correction makes one finding **cheaper to fix** and is recorded that way too — in both cases stating the direction of the change. §7.25 Q1 (whether Light Aurora is a designed environment or an inverted Dark) remains open, §7.25 Q6 (which icon family) remains open, and §7.27 remains a process recommendation naming no palette, type stack or aesthetic — all three re-checked after editing.

---


---

