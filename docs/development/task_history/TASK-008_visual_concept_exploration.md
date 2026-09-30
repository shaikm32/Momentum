# CURRENT TASK

> This file contains the state of the currently active task.
> It is NOT a project history, knowledge base, or issue archive.

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: TASK-008
- Title: Step 2A — Visual Concept Exploration (four design directions + 20 image-generation briefs)
- Status: **COMPLETE / VERIFIED / CLOSED** — 2026-09-28.
  **FINAL VERIFICATION: VERIFIED (18 CONFIRMED / 0 PARTIALLY CONFIRMED / 0 REFUTED)**,
  after three bounded review/correction cycles. Artifact emitted.
  **Phase 2B (image generation) NOT STARTED. Phase 3 NOT STARTED.**
- Priority: High
- Created: 2026-09-28
- Last Updated: 2026-09-28 (closed)
- Phase: **Phase 2A — Visual Concept Exploration.**
  **Phase 2B (image generation) NOT STARTED. Phase 3 NOT STARTED.**
- Classification:
  - **ARCHITECTURE — assigned.** The deliverable defines conceptual visual
    direction: surface philosophy, mark vocabulary, navigation model, motion
    personality, light/dark environment strategy, and the answers to the audit's
    fourteen design questions. These are design/architecture decisions even
    though no code changes.
  - **VERIFICATION — assigned.** The user brief §18 specifies a concrete
    final-verification checklist over a large structured document; independent
    verification materially adds value.
  - **IMPLEMENTATION — NOT assigned.** No application code, no design token, no
    resource, no dependency, no navigation change. AGENTS.md §14.13 principles
    and the phase boundary in §3 below make this an analysis/authoring task.
- Routing: **Orchestrator -> Architect** (authors the four directions and the
  twenty screen briefs into §7, its owned section) **-> Orchestrator**
  (mechanically emits `MOMENTUM_CONCEPT_EXPLORATION.md` — see §16 B3 for why
  emission is the Orchestrator's step and not the Architect's)
  **-> Reviewer/Tester** (independent verification of the emitted artifact
  against the §18 checklist) **-> Orchestrator** (closes records).
- Predecessor: TASK-007 (COMPLETE / VERIFIED / CLOSED). Artifact:
  `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` — **authoritative on all Phase 1 findings.**

---

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Produce `MOMENTUM_CONCEPT_EXPLORATION.md`: a **design strategy / visual concept
specification** that develops the conceptual design directions later rendered
into actual visual concepts, so that a **separate image-generation stage** can
produce believable premium-Android app screens without inspecting the
repository.

The central question it must explore is **not** "how do we make Momentum
prettier?" but:

> What could Momentum become if its visual and interaction experience were
> rebuilt today as a genuinely premium Android habit/productivity application?

**The product's functional truth is protected. The visual and interaction
architecture is not.**

### 2.1 The deliverable is a brief, not an implementation

The document must specify, for each direction, exactly what an image-generation
model must be told in order to render a believable Momentum screen. It is
**not** a design system, **not** a spec, and **not** a chosen direction.

---

## 3. SCOPE

<!-- ORCHESTRATOR ONLY -->

### In Scope

- Reading `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` (authoritative; read the sections
  the task names, not the whole 353 KB file indiscriminately).
- Reading `docs/design/AURORA_CONCEPT_ASSETS.md` (Phase 2 input, non-runtime).
- Reading existing product/design documentation as needed to keep directions
  inside protected product truth.
- Reading installed design skills and applying them deliberately.
- Authoring four mutually distinct visual/product directions plus a controlled
  evolution, each covering five key screens and each with an image-generation
  brief.

### Out of Scope — hard phase boundary (user brief §17)

- **No image generation.** Zero images produced.
- **No Kotlin / Compose / navigation / design-token / resource / dependency
  change.** Read-only on the application tree.
- **No modification of the existing design system** (`docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md`).
- **No Phase 3 work.** No implementation planning for a chosen direction.
- **No winner.** No ranking, no scoring, no "best", no recommendation. §9
  (comparison matrix) is **descriptive only**.
- **No ADRs.** Phase 2A settles nothing architecturally. Any ADR-worthy decision
  arises only in Phase 3 and is Architect-owned then.
- **No reconciliation-correction of Phase 1 findings.** The audit is
  authoritative. Where it is uncertain, the uncertainty is preserved unless
  repository evidence resolves it.

---

## 4. REQUIREMENTS

<!-- ORCHESTRATOR ONLY -->

Full requirement text is the user brief. The load-bearing ones, restated as
verifiable requirements:

### R1 — Grounding

- The Phase 1 audit is **authoritative**. Do not silently override, reinterpret
  or "correct" it.
- Preserve audit unknowns unless repository evidence resolves them.
- Do not contradict the audit's evidence. In particular, do not invent answers
  that the audit found to be open **for the project as a whole**; each
  *direction* may answer a question, and where directions answer it differently
  that divergence must be explicit.

### R2 — Exactly four directions

- **Direction A, B, C** — genuinely new / fundamentally different / third
  distinct interpretation.
- **Direction D** — explicitly **Controlled Evolution**: a sophisticated
  evolution of the strongest existing Momentum characteristics.
- Each must define all of §8.1–§8.20: Concept Name, Core Idea, Emotional
  Character, Interpretation of "Momentum", Visual Metaphor, Typography, Colour /
  Environment, Surfaces, Cards, Iconography, Navigation, Home / Today, Habit
  Experience, Tasks, Insights, Onboarding, Motion, Light / Dark, Signature
  Element, Risks.

### R3 — Non-convergence (critical)

The four directions must **not** collapse into four variations of
*white background + rounded cards + gradient accent + bottom navigation + large
hero*. They must differ at the level of: visual metaphor, composition,
information hierarchy, surface philosophy, card usage, typography, progress
representation, habit representation, navigation, interaction model, motion
personality, and light/dark environment. **If two directions begin to resemble
each other, push them further apart.**

### R4 — Five key screens per direction

For every direction: **Home / Today, Habit Detail, Tasks, Insights, Onboarding**
— each with purpose, hierarchy, composition, primary visual, key components,
data shown, interaction emphasis, distinctive visual behaviour, light-mode
considerations, dark-mode considerations, and image-generation requirements.

These are **visual-generation briefs, not implementation specifications**.

### R5 — Audit-question mapping (§10)

Explicitly map all four directions against the unresolved Phase 1 questions.
Minimum coverage: **Q1** (is Light Aurora a designed environment or an inverted
Dark?), **Q2** (what does "Momentum" mean visually?), **Q3** (should cards
remain central?), **Q6** (icon family / direction), plus habit visualization,
streak visualization, progress visualization, onboarding argument, motion
language.

Q1, Q2, Q3, Q6 must be answered **explicitly per direction**, and Q1 and Q3
directly. Q6 may be left as a "requires further validation" position where a
concept genuinely depends on it — the audit deliberately left it open.

### R6 — Challenge current patterns structurally (§12)

The concept exploration must respond to, not tweak: weak numeric hierarchy;
lack of Light-mode depth; snackbar-based completion celebration; invisible
consistency metrics; generic/local visual decisions; excessive inline alpha
usage; multiple surface systems; conflicting icon systems; excessive glass;
dialog translucency; inconsistent inset handling; weak missed/rest distinction.

### R7 — Preserve strong existing ideas deliberately (§11)

For each direction, state which of these survive and **how they would be
transformed**: `CompletionButton`; manage/act/understand division;
`HabitDetailScreen.DayCell`; pure-Kotlin engines; offline persistence; unified
`Activity` concept; Home/Habits artwork ambition; `ProgressOrb` craft.
Preservation is **not** automatic and must be argued per direction.

### R8 — Image-generation briefs (§13)

20 briefs (4 directions x 5 screens). Each must specify: Android-native
context; device/frame assumptions; screen purpose; exact visual hierarchy;
composition; typography direction; colour/environment; surface treatment; major
components; visual metaphor; interaction state; important data; light/dark
requirement; **what MUST be visible**; **what MUST NOT appear**; anti-patterns
to avoid. Concrete enough that an image model needs no repository access.

### R9 — One product per direction (§14)

Each direction's five screens must be coherent: the same typography, spacing,
surface treatment, iconography, visual metaphor, navigation and motion
personality across all five. Because the screens are generated separately, this
consistency must be **explicitly documented**.

### R10 — Skill discipline

- **Primary:** `imagegen-frontend-mobile` — native-Android composition, mobile
  hierarchy, platform-aware navigation, typography, spacing, surfaces, cards,
  touch targets, multi-screen consistency, light/dark, motion, app-native
  interaction patterns. The concepts must read as **premium Android
  applications, not websites inside phone frames.**
- **Secondary, selective:** `high-end-visual-design` (hierarchy, composition,
  surface treatment, depth, restraint — *without* its web/Tailwind assumptions);
  `redesign-existing-projects` (diagnosis, generic patterns, challenging
  component assumptions — *adapted to Compose*).
- **Not governing:** `gpt-taste`, `imagegen-frontend-web`, `image-to-code`,
  `industrial-brutalist-ui`, `minimalist-ui`. Inspectable for isolated
  inspiration only. **Banned as product direction:** landing-page layouts, web
  hero sections, web navigation, Tailwind, GSAP, website CTA architecture,
  desktop-first composition, fake browser/web UI patterns.

### R11 — Required document structure (§16)

Exactly 16 top-level sections, in order:

1. Purpose & Phase Boundary
2. Source Material
3. Product Truths
4. Concept Exploration Principles
5. Direction A
6. Direction B
7. Direction C
8. Direction D — Controlled Evolution
9. Cross-Direction Comparison Matrix **(descriptive only)**
10. Audit Question Mapping
11. Existing Strengths Mapping
12. Image-Generation Strategy
13. Generation Order
14. Cross-Screen Consistency Rules
15. Rejection Criteria
16. Phase-2B Handoff

### R12 — No winner, no scores

No ranking, no scoring, no percentages, no "best", no recommendation
(§7 explicitly, §9 explicitly). The comparison matrix compares *properties*,
it does not evaluate.

### R13 — Zero application impact

No application implementation file may change. Verified by `git status`
comparison against the Phase 1 baseline (§16 B4).

### Constraints

- Evidence over assertion. Prefer `file:line` pointers when a statement is
  about the existing app.
- Do not paste large documentation excerpts.
- Do not write to `ISSUES.md`, `PROJECT_STATUS.md`, or `docs/**`.
- Read-only git inspection allowed. `git commit` / `git push` denied.

---

## 5. KNOWLEDGE REFERENCES

<!-- ORCHESTRATOR ONLY -->

| ID | Why required | Location |
|---|---|---|
| `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` | **Authoritative.** §18 strengths, §19 opportunities, §20 screen verdicts, §21 CP-1…CP-10, §22 HO-1…HO-12, §23 Q1…Q14, §24 protected constraints, §25 process, §17 generic patterns, App. E unknowns | read by section, not end-to-end |
| `docs/design/AURORA_CONCEPT_ASSETS.md` | Phase-2 concept input; Manrope variable capability; Material Symbols Rounded status; Q6 framing | whole file (short) |
| `KNOW-ARCH-004` | Locked ADRs bounding what a direction may propose (ADR-07/08/19/20/21/22/23/26/27/31–39) | `DECISIONS.md` — **grep by ADR number only; never read end-to-end** |
| `KNOW-UI-003` | Explicitly rejected patterns + UI acceptance criteria (the glass prohibition a direction must respect) | `MOMENTUM_DESIGN_SYSTEM_V1.md` §33, §35 |
| `KNOW-BUSINESS-003` | v1 non-goals — a direction must not imply cloud, social, leaderboards | `PRODUCT_SPEC.md` §9, §10 |
| `KNOW-OFFLINE-001` | Offline-only / privacy — the product's differentiator a direction may surface | `PRODUCT_SPEC.md` §7, §8 |
| `KNOW-BUSINESS-001` | The unified `Activity` concept — habits and tasks share a visual grammar (audit §24 item 14) | `PRODUCT_SPEC.md` §2, §3 |
| `KNOW-FEAT-002` | Habits spec — the three-state DayCell rule and the weekly bar-chart prohibition | `HABITS_MANAGEMENT_UI_SPEC.md` §193–195, §177 |
| `PROJECT_STATUS.md` | Feature truth (F-HOME / F-HABITS `FULLY_VERIFIED`) | read-only |
| `ISSUES.md` | Open-issue context, esp. `ISSUE-020` (the only measured contrast evidence) | read-only |
| `AGENTS.md` | §§2 offline, §5 Compose, §7 Room, §9 UX/a11y/animation, §14 multi-agent rules | — |

`knowledge/index.md` is currently the only file in `knowledge/`. If a required
knowledge ID does not resolve, report the missing ID — do not substitute an
unrelated document (AGENTS.md §14.4).

---

## 6. CURRENT ISSUES

<!-- ORCHESTRATOR ONLY -->

Canonical records live in `ISSUES.md`; reference by ID, do not duplicate.

| Issue ID | Title | Status | Relevance to this task |
|---|---|---|---|
| `ISSUE-020` | Home date label `textSecondary` 3.11:1 in Light | OPEN | The repository's **only measured contrast evidence**; a direction's contrast strategy must account for real measurement, not assertion (audit §25) |
| `ISSUE-021` | Fresh install reports no active theme in the Theme Gallery | OPEN | Bounds any direction that presents themes/identities (audit Q13) |
| `ISSUE-022` | Reduce-motion copy promises a whole-app effect at 1 of 14 call sites | OPEN | Any direction defining a motion language must specify reduced-motion behaviour honestly (audit §24 item 9) |
| `ISSUE-023` | No agent held a write grant for the audit deliverable | **CLOSED** | Precedent for the emission pattern in §16 B3 — **do not reopen**; the grant now exists for this task's artifact too |
| `ISSUE-024` | `design/aurora-concept/` provenance | **RESOLVED** | Assets retained intentionally as non-runtime **Phase 2 input**; runtime adoption still needs a separate decision (Q6) |

**No new issue is opened by concept exploration itself.** A design critique is
not an issue. If the Reviewer finds a concrete, reproducible **product defect**
during verification, it is reported to the Orchestrator, who decides whether to
register it.

---

## 7. ARCHITECTURAL DECISION / CONCEPT AUTHORING

<!-- ARCHITECT ONLY -->

> **This section is the complete authored content of `MOMENTUM_CONCEPT_EXPLORATION.md`.**
> It is written to be read standalone. Nothing below refers to a sibling document
> for its meaning. Phase 1's `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` is the authoritative
> evidence base; where a claim appears here about the existing app it is Phase 1's
> claim, and this document does not soften, extend or overturn it.

---

## 1. Purpose & Phase Boundary

### 1.1 What this document is

A **design strategy and visual concept specification** for the Momentum Android
app. It defines four mutually distinct visual directions — three radical
reconceptions of the product and one explicit controlled evolution — and, for
each, twenty field-level specifications (four directions × five screens) plus
five image-generation briefs (four directions × five screens = twenty briefs
total).

Its consumer is **a separate image-generation stage**. That stage will receive
this document and a list of twenty screen names, and will produce twenty screen
images. It will not open this repository, will not read Kotlin, and will not
read Phase 1's audit. Everything it needs in order to produce a believable
premium Android screen is written here.

The four questions that must be answered by exploration rather than by meeting
are the fourteen Phase 1 left open. The four largest are answered explicitly and
separately by every direction, because a direction that does not state its
position on them has not been submitted:

- **Q1** — is the Light environment a designed environment, or an inversion of Dark?
- **Q2** — what does the product's name point at, given it rejects fire-streak
  gamification in its own locked documentation and ships a fire icon?
- **Q3** — should "today" be a number, a shape, or a place?
- **Q6** — which icon family, and is habit identity carried by an icon family,
  by a treatment of borrowed glyphs, or by something that is not an icon?

### 1.2 What this document is not

- **It is not a design system.** It names no token, no spacing scale, no hex
  value, no `Type.kt` entry, no Compose type. Sizes and colours are given as
  *visual direction* in plain language so an image model can act on them, not as
  values an engineer can commit.
- **It is not an implementation plan.** No screen decomposition, no use-case
  signature, no route table, no migration.
- **It is not a decision.** **No direction is ranked, scored, preferred,
  recommended or selected.** Section 9 compares properties; it does not
  evaluate. Four directions ship, and choosing between them is a later task
  with a different owner.
- **It is not a design-system amendment.** `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md`
  is untouched by this document.
- **It is not a Phase 3 artifact.** Phase 3 planning for a chosen direction does
  not exist and is not implied.

### 1.3 The Phase 2A boundary, stated as a fence

| This stage does | This stage does not |
|---|---|
| Author four visual directions as concepts | Generate a single image |
| Specify twenty screen concepts and twenty briefs | Generate twenty images |
| Answer Q1/Q2/Q3/Q6 per direction | Settle any of them for the project |
| Name what a direction removes and what it costs | Modify application code, resources, tokens or navigation |
| Hold the product's functional truth fixed | Change the product's scope, features or data model |
| Record four unknowns honestly as unknowns | Convert an unknown into an assumption |
| — | Select, recommend or rank a direction |
| — | Record an ADR, or change any architecture |
| — | Touch `ISSUES.md`, `PROJECT_STATUS.md`, `docs/**`, or the ADR record |

**No images are produced by this stage. Zero.** The image-generation stage is a
separate, later stage with its own acceptance criteria.

### 1.4 What "no winner" means operationally

A reader who finishes this document must be able to hold four coherent, fully
specified products in mind and choose between them on evidence they gather
themselves. Therefore:

- Section 9 has no score column, no rating, no percentage, no "best", no
  "recommended", no "strongest".
- Section 9 rows are *properties*, not *judgements*. "A habit is marked by ___" is
  a property. "Direction C marks habits better" is a judgement, and it appears
  nowhere.
- Each direction's Section 8.20 states where that direction could fail. A
  direction with no honest failure mode has been mis-written.
- Where the four diverge, the divergence is stated as divergence. The document
  says "these are four different answers", not "these are ranked".

---

## 2. Source Material

### 2.1 What was read

| Source | What was taken from it |
|---|---|
| `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` (2,509 lines, read in full) | The entire evidence base. Sections used: §2 product understanding, §3 design system, §4 visual identity, §5 typography, §6 colour, §7 surface & depth, §8 layout, §9 components, §10 habits, §11 tasks, §12 insights, §13 onboarding, §14 navigation, §15 motion, §16 light/dark, §17 generic patterns, §18 the nine strengths, §19 the eleven radical opportunities, §20 the screen verdicts, §21 CP-1…CP-10, §22 HO-1…HO-12, §23 Q1…Q14, §24 the twenty protected items, §25 the process recommendation, Appendices A–F including the correction log and the carried unknowns |
| `docs/design/AURORA_CONCEPT_ASSETS.md` (166 lines, read whole) | Status of the concept pack: Manrope variable subsets + one Material Symbols Rounded variable webfont; **non-runtime, unreferenced, `.woff2` not a valid Android font resource**; Q6 intentionally unresolved; runtime adoption would need a separate ADR |
| `AGENTS.md` §§2, 3, 5, 7, 9, 11, 12, 13 | Hard requirements: offline-only; no network permission; Clean Architecture with pure-Kotlin domain; no hardcoded colours in screens; `java.time`; append-only completion history; accessibility floors; reduced motion; version-catalog rule; small focused changes |
| `docs/product/PRODUCT_SPEC.md` §§2, 3, 7, 8, 9, 10 | The unified `Activity` concept and its fields; offline-first as a hard requirement; privacy; v1 non-goals (no cloud sync, no social, no leaderboards, no AI coaching); success criteria including 4.5:1 body contrast and 48 dp targets |
| `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` §§33, 35 | The explicit rejected-patterns list and the ten design-system acceptance criteria. Both are binding on every direction in this document |
| `docs/product/features/Habits/HABITS_MANAGEMENT_UI_SPEC.md` §§177, 193–195, 348–350 | No bar chart for the primary weekly view; not-scheduled must be visually distinct from missed; the manage / act / understand division |
| `docs/architecture/decisions/DECISIONS.md` — **grepped by ADR number only, never read end-to-end**: ADR-07, 08, 19, 20, 21, 22, 23, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39 | Which architectural decisions bound what a direction may propose, and which are explicitly open |
| `PROJECT_STATUS.md` | Which features carry verification evidence today (F-HOME, F-HABITS `FULLY_VERIFIED`) |

### 2.2 The locked decisions that bound every direction in this document

Read from the ADR record by number, not in sequence. Each is a fence a direction
must stay inside, or must explicitly name as a question it wants reopened in a
later, ADR-bearing stage.

| ADR | The bound |
|---|---|
| ADR-07 | Reminders are `AlarmManager` + `WorkManager` + `BOOT_COMPLETED`, local only. No push, no FCM. No direction may imply a server |
| ADR-08 | Theme architecture is a **token class + CompositionLocal + registry**, persisted selection. Retained by ADR-21. A direction may not imply hardcoded per-screen colour or platform dynamic colour |
| ADR-19 | One unified `Activity` model with an explicit `ActivityType` discriminator. **No parallel habit/task entities, tables, repositories or use-case stacks.** Habits and tasks are the same object with different configuration |
| ADR-20 | Guided creation with a `HabitIntent` discriminator and a **type gate** that refuses habit↔task conversion. A direction may not let a user transform a habit concept into a task concept |
| ADR-21 | The design document is the visual source of truth; two Aurora environments; Manrope bundled offline; the token-class + CompositionLocal approach stands. Its *values* are not protected — see below |
| ADR-22 | Bottom navigation is **exactly four destinations** — Home, Habits, Tasks, Insights. Calendar and Settings are not tabs |
| ADR-23 | `uiMode` changes do not recreate `MainActivity`; theme switching is in-place. A constraint on how a theme is delivered, not on what it looks like |
| ADR-26 | Habits is a dedicated management screen (manage only). Tasks keeps the shared activities list |
| ADR-27 | `HabitIconTile` + `habitIconAccent` are *one* icon language for list/detail; the floating bottom bar; the crossfade; the `"streak" -> LocalFireDepartment` mapping. **ADR-27 itself records three unresolved conflicts** — the fire glyph against the design system's emoji/fire rejection, the second FAB against the spec's header `+`, and nav order against ADR-22/26 |
| ADR-31, 32, 33, 34, 35, 37, 38, 39 | The Hero **outcomes**: one Aurora-backed surface, artwork owns the top edge, no seam, no hard edges, the two Hero routes draw behind the status bar, the reach-a-target dialog is opaque with a raised window dim. The 771-line *implementation* is not locked |

### 2.3 What is explicitly **not** locked, and is therefore available to a direction

Recorded so no direction mistakes an open door for a wall, or a wall for a door:

- The **7-token type scale** and the 15→7 Material-slot collapse. The *typeface*
  is protected; the *scale* is not.
- The **Light and Dark token values**. Two environments must exist. What they
  contain is open. **This is Q1.**
- **"Liquid Glass"** as a name and as a default treatment. The locked
  prohibition is the design document's: glass must not be the default for every
  card, row, navigation, screen and button. A direction that *reduces* glass is
  therefore **closer** to the locked design, not further from it.
- The dead tokens — `AuroraState.MILESTONE`, `MomentumMotion.reward`,
  `springSnappy`, `springGentle`, `easeInOut`, `progressTrack`, `auroraMint`,
  `surfaceInteractive`, `textDisabled`, `MomentumElevation`. These are
  design-system surface area with no production call site, not decisions.
- The Home Hero machinery.
- The single-entry theme gallery. Recorded deferred debt, not a locked choice.

### 2.4 The audit's own known limits, inherited and preserved

**No rendered visual reference exists in this repository.** There is no
screenshot, no mockup, no Figma / Sketch / Penpot link, no layout or composition
reference, no colour or spacing specification, and no golden file. The one
design-concept directory that exists holds a web font pack and shows nothing about
the application's appearance; it is verified unreferenced by any build rule,
resource, document or ADR.

**Therefore: nothing in this document is a pixel observation.** Every statement
here about how the existing app *reads* — its flatness, its hierarchy, its
genericity — is Phase 1's static code reading, inferred from token values and
Compose semantics, not observed. The single measured contrast datum in the whole
tree is the Home date label: **3.11:1 in Light over sampled hero artwork, below
the 4.5:1 floor; 5.22:1 in Dark.** Every direction in this document treats that
as a real measurement to design against, and none of them treats a *Dark*-passing
value as automatically *Light*-passing.

**Other unknowns preserved, not converted to assumptions:**

- The glyph coverage of the bundled `Manrope` variable TTF is not determinable
  without a font-mapping tool. Its weight axis **is** determinable and was
  parsed: a single `wght` axis, **200–800**, with seven named instances
  (200/300/400/500/600/700/800), default 200, TrueType outlines. A real 700
  exists in the bundled file. **A direction may therefore design a numeric
  register that reaches a genuine Bold — fully offline, at zero asset cost — and
  the four directions below use that fact differently.** Glyph coverage is a
  localisation question and gates nothing here.
- Whether the concept pack's Material Symbols webfont actually *is* Material
  Symbols Rounded is not independently verified from the binary; its woff2 table
  directory is brotli-compressed and was not parsed. The app does not use it
  either way.
- The exact colour values Material 3 fills into the theme-scheme slots the app
  does not pass are library source, not repository source. Unmapped slots keep
  library defaults drawn from a palette this product does not use.
- Whether a rendered layout at large font sizes, at reduced motion, or on a
  small viewport survives is untested, because nothing was rendered.

### 2.5 The design skills applied, and how

**Primary — `imagegen-frontend-mobile`.** Applied for: platform mode pinned to
**Android-native**, never cross-platform-neutral; screen-first generation rather
than a single mood board; a locked per-direction design bible so five separately
generated screens read as one product; safe-area awareness on every screen; the
first screen of each direction held to a stricter cleanliness bar; a
believable flow order rather than a random screen set; clean, even, content-first
device framing; text that must stay comfortably readable; and the explicit
anti-tell list — no purple-blue fintech gradients, no random glass cards, no
ambient blobs with no purpose, no fake neon, no oversized radii on everything,
no fake chart spam, no repeated stat cards, no cloned flow screens, no giant
empty cards, no phone-shaped websites, no pill/badge clutter, no meaningless
avatar rows, no fake system markers, no copy filler ("elevate your life",
  "unlock your potential", "next-gen", "seamless", "transform your day"), and
  no fake brand slop.

**Secondary, selective — `high-end-visual-design`.** Applied for: hierarchy before
decoration; one strong structural move per screen rather than many small noisy
ones; a single consistent light direction in every shadow; coloured/tinted
shadows rather than pure black at low opacity; deliberate radius variation
(tighter on inner elements, softer on containers) so radius stops being a
uniform value; asymmetric composition where symmetry would be a default; and
optical rather than mathematical alignment.

**Deliberately *not* applied from that skill**, because each conflicts with a
fence in §3.1: its banned-fonts rule (Manrope is the protected, bundled,
offline typeface); its banned-Material-Icons rule (Q6 is open and Material Icons
is the shipped set); its square/clip-path geometric-mask compositions (not an
Android idiom); its fixed-sticky-nav and bento-grid layout archetypes (web
composition patterns, banned by this stage); its `rounded-[2rem]` radius rule
(an Android card at 32 dp radius reads as an iOS bubble, not a Material
surface); its `cubic-bezier(0.32,0.72,0,1)` motion curve (a web easing
convention); and its hover-cursor and scroll-reveal vocabulary (an Android app
has no hover state, and scroll reveals that require 100+ viewport heights do not
exist on a 640 dp phone).

**Secondary, selective — `redesign-existing-projects`.** Applied for: the
diagnostic pass (which generic patterns are present and which are merely
familiar-because-Android); tabular figures for rapidly changing numbers;
tracking decisions per size rather than a global zero; a real weight range
instead of Regular-and-Bold; loading and empty states as first-class rather than
afterthoughts; composition that does not begin from three-equal-cards; and the
preservation discipline of *extend the existing stack, do not fork a parallel
implementation* — which in this context means a direction must be able to grow
out of the existing `ActivityCard` row, the existing `CompletionButton` and the
existing three-state day mark rather than replacing the product wholesale.

**Inspected for isolated inspiration only, never governing:** `gpt-taste`,
`imagegen-frontend-web`, `image-to-code`, `industrial-brutalist-ui`,
`minimalist-ui`. None of them contributed a direction. `industrial-brutalist-ui`
in particular was checked and rejected: its Swiss-terminal register would make
Momentum read as an instrument, which is precisely Direction B's chosen
identity, and taking it as governing direction would have collapsed the
exploration.

### 2.6 The skill hazards encountered and consciously declined

Four instructions from the design skills are recorded here because following
them would have produced a *worse* Momentum and a less useful exploration. They
are declined deliberately, not overlooked.

1. **"Ban Material Icons."** Declined. Q6 is an open product/architecture
   question, the shipped set is Material Icons via `material-icons-extended`,
   and the locked ADR that names Material Symbols Rounded was never honoured by
   the build. A direction cannot be built on a family the build does not use.
   Directions A, B and D each take a *different explicit position* on this —
   including one that *depends* on the Symbols answer and one that deliberately
   does not need it — and the divergence is the point.
2. **"Use a mono or tabular companion for numbers."** Declined as a *default*.
   Manrope's own figures can be made tabular with a single font-feature
   setting and no new asset. Introducing a second bundled family is a
   dependency decision that belongs to a later stage, not a design direction.
3. **"Double the padding / macro-whitespace."** Declined at full strength. Momentum
   is a list-and-action product that must remain usable with many activities
   eligible. Luxury whitespace applied to a 12-row activity list is a
   *scannability* regression, and scannability is a hard accessibility and
   usability floor here. Generous spacing is honoured; unbounded spacing is not.
4. **"Rotate and offset cards for depth."** Declined entirely. Rotation of
   interactive rows destroys the scannability of a vertical list, misaligns
   baselines, and is a desktop-web composition device with no Android precedent.

---

## 3. Product Truths

These are the fences. A direction may break any aesthetic convention. It may not
break anything in this section. Every one of these is a real constraint — a hard
project requirement or a genuinely locked decision — and none is here because
"it already exists".

### 3.1 The functional truths, in force on all four directions

| # | Truth | What a direction may therefore not do |
|---|---|---|
| 1 | **Offline-only, with no network permission.** No backend, no account, no login, no cloud, no sync, no analytics, no ads, no tracking | Show a sign-in, an account, a sync status, a cloud indicator, a "backed up" state, a share sheet target, a leaderboard, a friend, a team, a streak league, a push-notification opt-in that implies a server |
| 2 | **On-device storage only** — Room and DataStore | Imply a server round-trip, an upload, a conflict-resolution state, a "last synced" timestamp |
| 3 | **Minimal permissions** — notifications and alarms only | Imply location, health platform, contacts, files, camera, microphone, activity recognition, wearables |
| 4 | **One unified `Activity` concept**, differentiated by category, recurrence and target, with an explicit habit/task discriminator | Give habits and tasks **entirely separate visual languages**. This is a locked decision. A direction may make a task feel lighter than a habit *within one shared grammar*; it may not make them two products |
| 5 | **Guided creation with a type gate** — a habit cannot be converted into a task | Depict a habit drifting into a task, or a "convert" affordance |
| 6 | **Append-only completion history.** Editing an activity never rewrites past records; edits apply forward from the edit date | Show a "history will be recalculated" state, an edited-past state, or a retroactive correction of any kind |
| 7 | **The streak rule: only *scheduled* occurrences count.** An activity scheduled Mon/Wed/Fri with Tuesday missing is a perfect streak. Unscheduled days are ignored and never break a streak | **Any visual that makes an unscheduled day look like a failure.** This is the single hardest correctness fence in the document, and it is a *visual* fence, not a data fence. A direction that draws a rest day as a gap, a break, a hole, a miss, a dimmed failure, or a broken chain has produced a correctness bug that looks like a style choice |
| 8 | **Pure-Kotlin domain.** Engines and use cases carry zero Android imports and are unit-tested without a device | Require streak, consistency or recurrence logic to live in a composable or a view layer. A direction that needs a richer mark vocabulary must get it from a use case, not from drawing code |
| 9 | **`java.time` for all date logic**, ISO-8601 at layer boundaries, local midnight as "today" | Depict a UTC boundary, a server-side "today", a timezone picker implying a remote clock |
| 10 | **Two environments must exist** — Light and Dark — and theme switching happens in place without recreating the activity | Ship one environment. Shipping a single environment is not a direction; it is an undeclared platform violation |
| 11 | **Accessibility floors are contract, not taste** — 48 dp minimum touch targets, 4.5:1 body-text contrast in *both* environments, dynamic-type-safe layouts with no fixed heights clipping large text, content descriptions on every icon-only control, **state never communicated by colour alone**, correct TalkBack traversal order | Design a mark that is only a colour. Design a 40 dp target. Design a fixed-height row that clips at 200% font scale. Design a Light value that only passes because it is *dark* |
| 12 | **Reduced motion must always be respected.** The current distribution is 1 of 14 call sites, which is a defect to fix, not a constraint to preserve | Introduce any motion that cannot be disabled. Every direction in this document states its reduced-motion behaviour per motion |
| 13 | **Four bottom-navigation destinations** — Home, Habits, Tasks, Insights. Calendar lives inside Insights. Settings is not a tab | Ship three, five, or six tabs. *What is open:* whether the bar is glass, and whether Tasks needs to exist as a tab at all (Q7, Q8) |
| 14 | **No hardcoded colours in screens** — the semantic layer is a `CompositionLocal` token object | Express a direction's identity as a per-screen hex. A direction's colour philosophy is expressed as *roles and relationships*, which is the form a token layer can carry |
| 15 | **The design document's rejected-pattern list is binding.** Named bans include: giant cards everywhere, excessive gradients, purple everywhere, green/red BUILD vs REDUCE, emoji as UI, fire-streak gamification, routine confetti, **glass everywhere**, mixed icon families, giant app bars, excessive button variants, heavy shadows, decorative charts, generic Material forms everywhere, separate calendar navigation, a Settings bottom-nav item, a full habit library on Home, and detailed analytics on Home | Any of these, in any direction, including in a "radical" one. **Direction D is bounded by this list too** — controlled evolution is not exemption from the locked design document |
| 16 | **The ten design-system acceptance criteria are binding**, including: Light and Dark feel like the same product; BUILD and REDUCE are visually distinct without good/bad colour coding; Home stays focused on today's progress; Habits, Tasks and Insights stay clearly differentiated; completion feels rewarding but restrained; **Aurora and motion respond to meaningful state changes**; Insights feels analytical without becoming a dashboard wall; accessibility does not depend on colour alone; and new screens assemble from existing components without inventing new visual patterns | Ship a direction in which Light and Dark are two different products, or in which BUILD and REDUCE are coded good-green / bad-red, or in which Home grows an analytics wall |
| 17 | **Home stays focused on today.** No historical analytics, no full habit library, no month view, no trend chart on Home | Solve the Tasks-tab or Insights problem by moving analysis onto Home |
| 18 | **Habits = manage. Home = act today. Details and Insights = understand over time.** The division is enforced by component choice in the shipped app, not by convention | Re-merge the surfaces. Putting a completion control on the Habits list, or putting management controls on Home, or putting a streak badge on a management card, re-solves a problem this product already solved |
| 19 | **The weekly habit view is a seven-day state view, not a bar chart.** The stated reason: a habit can be completed at most once per day. Not-scheduled days must be visually distinct from missed days | Draw a bar chart as the *primary* weekly view. This ban is a data-shape reason, and a direction that wants magnitude is answering Q4, not ignoring §177 |

### 3.2 The product truths as *assets* — what a direction is allowed to lead with

A fence list alone produces timid work. These are the truths a direction is
permitted to make its argument from, and they are the reason this product can
support a premium visual identity at all.

1. **Privacy and offline operation are a real differentiator with real copy
   already written.** The shipped Settings copy is the best-written text in the
   product: *"Everything stays on this device. No account, no cloud, no tracking
   — offline by design."* / *"Reminders fire on-device."* That line currently
   appears **only** behind an icon on a screen a first-time user does not visit.
   Its absence from the first-run experience is a placement problem, not a
   capability problem. **Any direction may make this its onboarding argument.**
2. **The product's own name is an unspent asset.** Phase 1 established that
   "momentum" appears in the tagline, in four of six Home messages, in one
   onboarding headline — and nowhere in the product's *mechanics* as a visible
   idea. A direction that makes the name mean something is doing the most
   valuable work available.
3. **The domain engines are excellent and mostly invisible.** A pure, typed,
   unit-tested engine computes, per activity: current streak, longest streak,
   total completions, **missed occurrences**, and a **consistency score**
   documented as the percentage of scheduled occurrences hit in the last 30
   days. Two of the product's own eight spec'd headline metrics have **zero**
   presentation call sites. A consistency product is withholding its defining
   number. **Any direction may lead with it.**
4. **A correct three-state data mark already exists in the product.** One
   component on one screen distinguishes completed, incomplete and *not
   scheduled* correctly. It is the template the rest of the visualisation layer
   was never built from. Its four states — empty, partial, complete,
   not-applicable — are the vocabulary a direction should propagate, not
   reinvent.
5. **The product's typeface is genuinely distinctive and already offline.** One
   bundled variable file, a single weight axis 200–800, seven named instances, a
   recognisable geometric-humanist letterform, no platform fallback anywhere. A
   direction may lean on this hard; it costs nothing.
6. **One custom mark exists and its adaptive-icon wiring is correct**, including
   a monochrome layer for themed icons — which is rarer than it should be and
   is already right.
7. **The art pipeline is a real, documented contract.** One artwork pair for the
   whole product; selection is a pure function of the theme, never the clock;
   callers pass band amounts and *cannot* position the artwork; the component
   documents its own dead parameter rather than hiding it. The correct decision
   was made by building two artworks rather than tinting one.
8. **A reduced-motion pattern already exists in the right form** — switching to a
   zero duration or nulling the spec rather than shortening a duration, gating a
   glow so that 0% pays nothing, and composing no transition at all when
   disabled. The idea is right; its *distribution* is broken. It is extendable,
   not needing replacement.
9. **Accessibility engineering has already gone past the checklist** in places.
   One day-cell composes a real spoken description — *"March 4, 3 of 5 complete"*
   — with a role and an action, and merges its descendants. The completion
   control's label names the **action** rather than the shape, and switches
   wording for target-based activities so it never promises a one-tap toggle it
   cannot deliver. That is a level of care that is invisible until it is absent.
10. **The manage/act/understand division is the clearest product decision in the
    repository**, and it is what stops a product like this collapsing into a
    generic dashboard.

### 3.3 The fence, restated as a single sentence

> A direction may invent any world it likes **inside** a product that runs
> entirely on-device, has no account, distinguishes one unified Activity, never
> rewrites history, counts only scheduled occurrences toward a streak, ships two
> environments, survives 200% font scale at 48 dp targets in 4.5:1 contrast,
> answers today on Home and history on Insights, and refuses emoji, fire-streak
> gamification, confetti, mixed icon families and glass-everywhere.

---

## 4. Concept Exploration Principles

The rules held while authoring these four directions. They are stated so a reader
can check whether a direction obeys them, and so a later stage does not quietly
lose them.

### 4.1 Non-convergence, enforced structurally

The failure mode this document is built to avoid is four variations of *white
background + rounded cards + gradient accent + bottom navigation + large hero*.
That is not four directions; it is one direction sampled four times.

Enforcement is structural, not stylistic. The four directions differ on **at
least seven of these nine axes simultaneously**, and no two directions share the
same answer on **Q1, Q2, Q3 or Q6**:

| Axis | A | B | C | D |
|---|---|---|---|---|
| Visual metaphor | the ledger / the printed record | inertia — mass × velocity | the woven run | governed accumulation |
| Primary surface material | matte paper stock | machined, etched plate | dyed textile | tonal, with rare glass |
| What a card **is** | a ruled ledger panel | a slotted data plate | a stitched patch | a quiet tonal card |
| Q1 answer | Light is paper; Dark is ink with paper as the light source | Light is a printed instrument face; Dark is a backlit panel — two different physical objects | Light is unbleached cloth; Dark is indigo-dyed cloth — same weave, different dye and light model | Light becomes a genuinely tonal environment with a real depth ladder; Dark keeps tonal lift and gains state responsiveness |
| Q3 answer | a **place** — today is an open column | a **number** — mass and velocity readouts | a **shape** — a run of stitches | a **number**, at true figure scale, leading a list |
| Q6 answer | weight-led; depends on the Symbols answer | weight-flat; deliberately needs nothing new | reduces iconography; identity moves to colour and weave | indifferent by design |
| Motion personality | settle, wipe, dry | step, needle, settle | one stitch pulls, the strip shifts | state-triggered bloom, then rest |
| Navigation model | flat rule-bar, four destinations | machined segmented rail | stitched edge binding | floating bar, corrected |
| Composition | editorial column, wide margins, hard left alignment | fixed grid, snap-to-slot, numbers dominate | woven grid, warp and weft | one continuous surface, list as the hero's detail |

### 4.2 Android-native, or it is wrong

These are Android applications rendered on Android hardware. Not responsive
websites inside a phone frame. Concretely, every screen in this document obeys:

- **Material navigation grammar.** Four destinations with icon plus label, a
  filled/outlined pair for active/inactive, a back affordance top-left on pushed
  screens. No web-style hamburger, no web-style mega-menu, no breadcrumb, no
  URL bar, no web footer.
- **Thumb-reach ergonomics.** The primary action of a screen is reachable in the
  lower two-thirds. Nothing critical lives in a top corner that a one-handed grip
  cannot reach.
- **Safe areas are respected.** A status-bar region, a gesture-navigation region,
  and a bottom-inset region are visible in every composition. A screen that reads
  as an edge-to-edge poster with no functional logic is invalid.
- **Real touch targets.** 48 dp minimum, with visible 48 dp footprints, not just
  48 dp hit slop hidden under a smaller glyph.
- **One list, one scroller.** No nested scrollables without a stated reason.
- **Android-appropriate scale.** A 12-item list must be scannable in one motion.
  Desktop density is not a premium signal; it is a missed target.
- **No hover state.** Nothing in these concepts may depend on a pointer that a
  finger replaces.
- **A believable system layer.** Where a system dialog, a bottom sheet, a
  keyboard, or a permission prompt appears, it appears in Android's idiom — a
  bottom sheet docks to the bottom, a system prompt sits in the platform's own
  style, and the keyboard pushes rather than overlays unpredictably.

### 4.3 Evidence over assertion

Every design claim states **what it is**, **why**, and **what it costs or buys**.
Three constructions are banned as vacuous, and no sentence in this document may
be reduced to one of them: *make it more modern*, *improve the UI*, *use better
spacing*, *make it premium*, *add animations*.

Where a claim is about the existing application, it is Phase 1's claim and
carries its evidence class. Where a claim is a direction's own invention, it is
marked as a hypothesis to be tested by generated images — because **no rendered
reference exists**, nothing here has been seen.

Where Phase 1 recorded an Unknown, this document preserves it. It does not
convert an Unknown into an assumption in order to make a direction easier to
write. A direction that needs a fact Phase 1 could not determine says so and
states what it would do under each branch.

### 4.4 Cards are answered, not assumed

The third open question is whether cards should remain central. All four
directions answer it, and they do not give the same answer:

- **A** — cards remain, and become **ruled ledger panels**: squared, matte,
  hairline-ruled, with the rule doing structural work. A card is a *column of a
  page*.
- **B** — cards remain, and become **slotted data plates**: fixed-height, fixed
  grid, with values snapped to slots. A card is an *instrument faceplate*.
- **C** — cards remain, and become **stitched patches**: cloth rectangles with a
  visible seam. A card is a *patch applied to a larger surface*.
- **D** — cards remain, and become **quiet tonal cards**: a single low-contrast
  fill, a hairline, no gloss, no gradient, and a reserved tier above them for the
  one thing that matters on the screen.

No direction removes cards, and the reason is worth stating: a phone-sized
progress product needs a way to say *"this group of things is one thing"*, and on
a 5.5-inch screen with a 4:3-ish portrait ratio, the alternatives — full-bleed
dividers, whitespace-only grouping, nested list indentation — group worse at
scanning distance. What changes is **what a card is made of and what it is for**,
not whether it exists. A direction that removed cards entirely would have to
re-prove grouping, and would pay for it in scannability.

### 4.5 No web patterns

Banned in every direction, as product direction: landing-page layout, a hero
section with a headline-plus-CTA-plus-three-feature-cards shape, web navigation,
a sticky marketing header, a footer link farm, a Tailwind utility aesthetic,
GSAP-driven scroll choreography, a website CTA architecture, desktop-first
composition, a three-equal-card feature row, a centred hero with a centred
paragraph, and a fake browser or web-app chrome.

The distinction applied throughout: **a hero on a website sells a page; the top of
an Android app states the product's current state.** A Momentum screen may have a
large top region — it is the most valuable real estate on a phone — but it is
allowed to be large only because it is where the user's answer lives, and it is
required to shrink, change, or vanish when the answer is smaller. A large
top region that stays large when it has nothing to say is the defect, and it is
defect **A·Home** is required to design out.

### 4.6 The content is the test

The same content appears in every direction, in both environments, in every
screen. A direction that looks composed on a full day with eight activities and
collapses on the empty day has not been evaluated. The Phase 1 process
recommendation requires a control; the control is the current application
rendered at the same content, which is a Phase 2B obligation and not this
stage's.

**The canonical content fixture, fixed once, used by all twenty briefs:**

*Today is **Tuesday 29 September 2026**.*

**Eligible today — five activities, three complete. This is the "3 of 5" case.**

| # | Title | Category | Tracking | Today | Streak | Why it is in the set |
|---|---|---|---|---|---|---|
| 1 | **Morning run** | Fitness | mark done | **done** | 3 days | a complete item still in the list |
| 2 | **Weekly review** | Work | mark done | **done** | 1 day | a complete item, low streak |
| 3 | **Take the stairs** | Health | mark done | **done** | 6 days | a complete item with a real streak |
| 4 | **Drink water** | Health | reach 8 glasses | **5 of 8** | 12 days | a **partial target** — the most decision-relevant number in the product, and today set at the app's *smallest* text size |
| 5 | **Read 20 pages** | Learning | reach 20 pages | **0 of 20** | **0 days** | a **broken streak** — yesterday was scheduled and missed |

**Not eligible today — the two states that make the week honest:**

| Title | Category | Why | Streak |
|---|---|---|---|
| **Evening walk** | Mind | scheduled **Mon / Wed / Fri** only. Tuesday is a **rest day**. It is not eligible and it is not a failure | 6 days |
| **Take vitamin D** | Health | **PAUSED.** Hidden from Today, kept, history intact | 21 days |

**Tasks.** *Book dentist* — one-time, due today, not done. *Renew passport* —
one-time, due 12 October.

**Insights, 30-day window.** Consistency score **78%**. Missed occurrences
**6**. Weekly bars covering a week in which Evening walk's Mon/Wed/Fri schedule
produces genuine blank days that are **not** failures. One category at 0%.

**The three state variants every screen must be able to carry:**

- **The empty day — Saturday 3 October 2026.** Zero eligible activities. This is
  the state a first-run user meets and a user on a rest day meets, and it is
  currently the app's most expensive and least informative surface.
- **The 100% day.** The moment after *Read 20 pages* reaches 20 and the day
  becomes 5 of 5.
- **The at-risk day.** Sunday 4 October: Evening walk is scheduled tomorrow, the
  streak is 6, and the day is nearly over. This is the only state that earns
  motion.

### 4.7 Both environments, always

Every direction is specified in Light **and** Dark, as two independently designed
environments. A direction delivered in one environment has not been evaluated.
And per **Q1**, a direction's Light environment must have a **mechanism** that
Dark does not use — otherwise it is an inversion with a different colour, which
is the failure mode Phase 1 identified and did not endorse.

### 4.8 One unknown per direction, named

Each direction names the single piece of evidence it most needs and does not
have, and states what it would do under each branch. This is how an honest
hypothesis is written without pretending to be a decision.

---

## 5. Direction A — **Press** *(the ledger)*

### 8.1 Concept Name

**Press** — named for the printing press, not for a button. The direction's
bet is that Momentum is a **record-keeping product that has been built to look
like a mood board**, and that the highest-premium register available to a
utility this honest is the one humans have used for six centuries to record
things they meant to do again: **the printed ledger**.

### 8.2 Core Idea

A habit is a line in a column. A week is a column. Today is the column that is
open. Momentum is not a feeling and not a flame — **momentum is the visible
length of the record.** You do not ask "how am I doing", you *look down the
page* and the page tells you, in ink, in a typeface chosen for numbers, at a size
chosen for numbers.

This direction takes the product's most embarrassing artifact — a 12 sp streak
badge with a fire icon — and resolves it by making the **page** carry the
number instead of the badge. It removes the badge, the glow, the halo, the
gradient, the glass and the artwork, and replaces them with a typographic system
that has five distinct sizes where the app currently has one, printed on a matte
surface with a real tonal ladder, in two environments that are genuinely
different materials.

**It is radical in the specific sense that it deletes the product's entire
surface vocabulary** — no translucency anywhere, no glow, no continuous
animation, no photographic hero, no icon-as-identity — and rebuilds from
alignment, rule, weight and paper.

### 8.3 Emotional Character

Not "premium". Concretely: **the feeling of a well-kept daybook that you are
trusted with.** Quiet authority. The sensation of ink drying. Restraint you can
hear.

Three specific tests, all of which a reader can apply to any screen:

1. **Nothing on the page is asking for attention.** There is no element that
   pulses, breathes, glows, drifts or shimmers. The loudest thing on any Press
   screen is a number, and it is loud because of its size, not because it is
   moving or saturated.
2. **Every horizontal line is a promise the screen keeps.** A rule under a
   section title runs the full content width and the content beneath it respects
   the columns. Nothing is decorative.
3. **The material feels handled, not new.** Warm off-white stock with a faint
   fibre; ink that is very dark but not pure black; a page edge you can see. It
   should look like a book that has been carried, not a render.

**The emotional risk, stated plainly:** a direction with no warmth in it can read
as austere or as a to-do app with pretensions. Press answers this by keeping the
paper *warm* and by allowing exactly one moment of physical gesture — the streak
milestone — per week at most. That moment is the only place Press is allowed to
be expressive, and it earns its existence precisely by being rare.

### 8.4 Interpretation of "Momentum"

**Momentum is the record's length, and the record is the proof.** Press takes
the product's name literally and typographically: momentum is what a ledger
accumulates. Not a feeling the app assigns. Not a gamified reward. **Evidence.**

This directly answers the fire-icon contradiction Phase 1 identified. The
product is named Momentum, its documentation rejects fire-streak gamification,
and it ships a flame glyph on a 12 sp badge. Press resolves the contradiction by
**deleting the badge and making the name do the work the icon was doing.** A
streak in Press is a **column of filled marks**, read as a run, with the run's
length as a figure set at a size that could not be mistaken for metadata. There
is no icon for a streak because a streak is a run of marks, and a run of marks
is a sentence in the layout, not a badge.

Where the word "momentum" appears in Press, it appears **as a column heading or a
caption derived from the user's data** — "21 scheduled days logged" — never as
encouragement. The product's own tagline is treated as marketing copy that does
not belong inside the app.

**The cost of this reading:** it makes Momentum a *quiet* product. If the user
wanted confetti and a flame, this is the wrong app. That is the trade, made
knowingly, and it is the reason the 100% moment in Press is a physical gesture
rather than an absence of one.

### 8.5 Visual Metaphor

The metaphor is a **ruled ledger page**, and it is applied consistently to all six
concepts:

| Concept | Press's metaphor | What the user sees |
|---|---|---|
| **Progress** | the column filling | a set of ruled rows, each ticked, with the fill height = the fraction, and the fraction also given as a figure |
| **Consistency** | the *unbroken* column | a vertical run of identical marks with no gap; a gap is a *hole in the rule*, and a hole looks like a defect, which is exactly what a missed scheduled day is |
| **Completion** | the tick | a hand-drawn-feeling stroke in a cell — heavier than the rule it crosses, in ink, at the weight that reads as a deliberate mark rather than a checkbox |
| **Streaks** | the run's length | the *number of contiguous ticked rows*, printed at figure scale, with the run drawn as a solid vertical spine and the missed day as a visible break in that spine |
| **Growth** | the page filling | over a 30-day window, the page shows 30 rules; completed days are ticked, missed scheduled days are blank with a struck edge, and **not-scheduled days are not printed at all** |
| **Momentum** | the length of the record | the whole page is the answer, and the number at the top is simply the count of what's above |

**The critical mapping — rest versus miss.** A Mon/Wed/Fri habit on a Tuesday:
the rule is *not drawn* for that day, and the day's column is empty paper. A
scheduled day with nothing completed: the rule **is** drawn, the cell is empty,
and a hairline strike runs across the cell at a shallow angle. A completed day:
the rule is drawn, the cell is empty, and it carries a tick.

**Empty paper, blank struck cell, ticked cell.** Three states, three *structural*
differences — presence of the rule, presence of the strike, presence of the tick
— and the hue is identical in all three. This satisfies the "never by colour
alone" floor structurally rather than by adding a redundant second channel to a
colour difference, and it satisfies the streak rule by making a rest day
*physically absent* rather than *marked as absent*. A rest day that carries a
mark has already made the user feel something about it.

### 8.6 Typography

Manrope is retained and becomes the whole voice. It is a geometric-humanist
sans with a distinctive `R` and `a`, and — critically — a bundled variable file
with a single weight axis from **200 to 800** and seven named instances
including a genuine **700**. The current app registers only 400, 500 and 600, so
its five most important numbers are rendered at an indeterminate weight. **Press
uses the axis. This is the direction's largest single gain and it costs no new
asset, no new dependency and no network fetch.**

Five registers, each with a job, and **no two of them the same size** — which is
precisely what the shipped scale fails to do:

| Register | Size intent | Weight | Tracking | Job |
|---|---|---|---|---|
| **Figure, primary** | the largest thing on the screen | 700–800 | −0.02 em | "3 of 5" and the Home headline figure. The first thing read |
| **Figure, secondary** | two steps down | 600 | −0.01 em | streak counts, consistency score, per-habit fractions |
| **Display** | large, for a screen's own name | 500–600 | −0.015 em | screen titles, the habit's name on its detail page. *Deliberately lighter than the figures* — in Press the numbers outrank the words, because the words do not change and the numbers do |
| **Body** | comfortable reading | 400 | 0 | row titles, descriptions, sentences |
| **Caption** | small, but tracked and never alone | 500–600 | **+0.06 em** | field labels, column headings, weekday initials, units. At 12 sp, positive tracking is a legibility measure, not a style choice — the shipped app sets zero tracking everywhere and this is the size where zero is below the comfort threshold |

**Tabular figures, everywhere a number changes.** Manrope's figures are
proportional by default, so today the orb's percentage re-centres inside its
circle on every tick and a streak badge's `9` becomes `10` and reflows its
neighbour. **Every changing figure in Press is tabular.** In an app whose entire
premise is watching numbers change, the numbers are the last place layout
stability was considered. This costs one font-feature setting and no new asset.

**Specific inversions this creates, all of them deliberate:**

- The **"3 of 5"** is the largest element on Home. The **greeting** is smaller
  than it. The **date** is a caption. The emotional line is removed or demoted
  to a caption, because the shipped app's message slot says the same thing all
  day and the shipped app's factual line is set smaller than the greeting above
  it.
- The **streak count is larger than the habit's name beside it.** The shipped app
  sets the streak at 12 sp and the habit's name at 20 sp.
- On the habit detail page, **"4/7 completed" is larger than the "This week"
  label above it.** The shipped app has the answer at 13 sp and the label at
  16 sp.
- **Current streak is larger than longest streak**, because one is live and one
  is an archive.

**What this costs:** an all-numeric hierarchy is a real trade. A user reading
about *what* they are doing has to work slightly harder, because the words are
quieter than the figures. Press accepts this because the shipped product's own
declared headline is *"How am I doing today?"* — a numeric question — and
because §19 of the product spec names streaks as *"supporting information, not
the emotional center."*

**What this buys:** the product can finally answer its own central question at a
glance, which today it cannot do at any size.

### 8.7 Colour / Environment

#### Q1 — the explicit answer

**In Press, Light is a designed environment and Dark is a designed environment,
and they are not the same design at two luminances.**

The shipped Light environment has no working depth mechanism: its surface ramp
collapses (`surface` and `surfaceElevated` are the same value), its shadows are
black-alpha and therefore invisible on a light canvas, and a one-pixel hairline
does all the separation. The shipped Dark environment expresses depth through
tonal lift, because a translucent surface sits lighter than its canvas there.
**So the shipped Dark has a mechanism and the shipped Light does not.** Press
gives Light a mechanism Dark does not use.

**The two Press environments:**

**LIGHT — "Daybook."** A warm, slightly grey off-white page — not the shipped
cool blue-grey — with a visible but faint paper fibre. Above it, cards are
*stacks of the same paper*: not a different colour, but a **physical stack
expressed as a single soft shadow beneath a hairline edge**, the way a sheet of
paper sits on the sheet below it. This is the mechanism Dark does not use. In
Dark there is no paper to stack, so Dark cannot use it; in Light it is the
entire depth system, and it works because a shadow on a light surface is a
*lit-from-above* artefact, which is physically correct rather than decorative.

There are **no gradients in Press Light.** The only gradient in the entire
direction is the 8% opacity paper-fibre texture, which is a texture, not a
gradient-as-decoration.

**DARK — "Ink."** Not a dark page. A **very dark, slightly warm, near-black
field with no fibre at all** — smooth, because ink is smooth. And here is the
move that makes Press's two environments genuinely different: **in Press Dark,
the paper is the light source.** The record does not sit *on* the dark field; the
record is a *light-toned object placed on it*, and it is the brightest thing on
the screen. The daybook page is a pale warm off-white in a dark room, lit from
the front. This inverts the shipped relationship in which Light and Dark are
one design with a brightness switch: **in Press, the objects change material
between environments while the layout, the type scale, the rules and the marks
do not change at all.**

This is answerable as a mechanism statement: Light expresses depth as
**paper-on-paper with a real lit shadow**; Dark expresses depth as **an
emissive light-toned page on an unlit field.** Neither mechanism is available in
the other environment. Neither is a brightness inversion.

**Accent philosophy — one hue, used for ink-weight work, never for surfaces.**

The shipped product declares five accent hues, of which one (blue) does 54 call
sites of global work and the other four are ambient-only or vestigial — the
product is effectively rendered in one accent. Press takes that as honest and
goes further: **Press has one accent and it is used in exactly three places** —
the tick of a completed item, the active state of a filter or a tab, and the
"now" line in Insights. Nothing else in the product is allowed to be that hue.

**What this buys:** the accent becomes *information*. Today a user who has
learned "blue means done" cannot decode anything else, because blue is also the
CTA fill, the progress fill, the ring arc, the selected chip, the section-header
action label, the empty-state icon tile, the stat sublabel and the selected-theme
border. **In Press, blue means one thing: this happened.**

**Per-activity colour.** The product persists a per-activity colour on its data
model, maps it, and reads it in zero presentation code; its UI's per-item colour
is a hardcoded lookup honoured on three of five activity surfaces. Press's
answer to the related question is: **per-activity colour is a local accent and
Press does not use it.** In a printed ledger, a red line and a blue line mean
the same thing. Item identity in Press is carried by **position in a stable
order, the word itself, and a small monochrome key mark** — not by hue. This
sits closer to the locked design's plainness and it removes one of the four
coexisting colour vocabularies rather than adding a fifth.

**Semantic colours — three roles, and the shipped five-way overload is not
reproduced.** The shipped product has one green carrying five unrelated
meanings: completion, the ACTIVE status pill, a 7-day streak milestone, a fully
complete calendar day, and positive trend polarity. A user who learned "green
means done" reads the ACTIVE pill as "done".

Press separates them, and it does so **structurally, not chromatically**:

- **Completion** is a tick, in the accent. Not a green.
- **Status (ACTIVE / PAUSED / ARCHIVED)** is *text plus a rule pattern*, never a
  colour. A paused item carries a struck rule; an archived item carries a
  double rule. This is what a ledger does, and it satisfies the "state never by
  colour alone" floor without a second channel bolted onto a hue.
- **Trend polarity** is a printed direction marker plus a signed figure, not a
  colour. The shipped app renders a 4-value trend enum as a lowercased string, so
  the user can literally read the words *"Insufficient data"*. Press prints
  `—` for insufficient data and a real direction mark otherwise.
- **Missed** is the struck cell. **Rest** is the absent rule. Neither is a colour.

**Contrast strategy.** Body text is set at or above 4.5:1 in both environments,
measured rather than asserted. The one measured contrast datum in the repository
is a **failure**: the Home date label measures **3.11:1 in Light** over sampled
hero artwork against a **4.5:1 floor**, and passes in Dark at 5.22:1. That is a
Light-specific failure of a Dark-passing value, and it is the clearest possible
statement of the CP-4 pattern. **Press removes the variable entirely** — the
Light environment has no photographic hero, so there is no artwork for text to
fail against, and the specific failure mode cannot recur. Press states its
contrast floors per tier per environment and it treats a Dark-passing value as
**no evidence at all** about Light.

**The one place colour appears at all, beyond the accent, is the paper.** Dark
is not pure black; it is a very dark warm near-black. Light is not pure white;
it is a warm off-white. Both are tinted with the *same* hue family so the two
environments read as the same ink. Tinted neutrals rather than pure
black/white is what makes a printed surface look printed.

### 8.8 Surfaces

**Press's surface philosophy: matte, opaque, and made of exactly three materials.**

| Material | What it is | Where it earns its place | Does it appear in Light? | Does it appear in Dark? |
|---|---|---|---|---|
| **The page** | the stock itself — warm off-white with fibre, or the unlit field | the canvas, always | yes | as the dark field |
| **The sheet** | one sheet of the same stock, sitting on the page, expressed as a lit shadow plus a hairline edge | every card, every panel, every row group | yes | yes — but as a **light-toned object**, see below |
| **The rule** | a hairline, in ink at low opacity | section divisions, table rules, column separators, the strike | yes | yes |

**Why this, and why not glass.** The locked design document explicitly forbids
glass as a default treatment for every card, every row, navigation, entire
screens and every button, and names "glass everywhere" in its rejected-pattern
list. The shipped application is the inverse: a translucency recipe is the design
system's core primitive, the default card, the default row, the navigation bar
and the primary button, across nine distinct fill values of which four are
tokenised. Moreover the *name* is a material the app does not implement — the
backdrop blur value is zero, so what ships is translucency plus a hairline plus a
soft shadow, and the code's own KDoc is honest about it.

**Press's answer is not "less glass". Press's answer is that the material is
paper, and paper is not glass.** There is no translucency in Press Light. In
Press Dark the record is opaque. Nothing in the direction is semi-transparent
except two things, and both are named: the sheet's shadow, and the 8% fibre
texture.

**What this buys, specifically:**

- Every surface in Press separates from every other surface **by a mechanism
  that works in both environments**, which is the exact failure the shipped Light
  environment has.
- The product can have a *real* depth ladder in both environments instead of
  three collapsed values in Light.
- There is no longer a need to *decide* which of nine fill values a component
  uses, because there are no fill values — there is paper, a sheet, and a rule.
- The direction removes, rather than adds. Phase 1's structural conclusion was
  that the app's genericity is a governance symptom — 100 inline alpha literals
  across 21 files, nine surface fills against four tokenised, six card
  constructions, four icon-container treatments. **Press deletes the entire
  category.** A direction that adds surface vocabulary has to justify every
  addition; this one has none.

**What it costs, honestly:** paper is static. Press has no surface that suggests
something is *above* another surface in a live way, so a bottom sheet and a
dialog do not read as physically floating. Press answers this by making the
modal layer **opaque and unmissable** — a dialog is a sheet of paper on a
scrimmed page, with a real edge and a real shadow, and Press adopts outright the
one dialog in the shipped product that is already correct in exactly this way.
That is a direct transfer of an existing strength, and it also removes the
shipped defect in which all five dialogs — **all five of which are destructive
confirmations** — are translucent, a pattern the app's own test asserts is a
defect in the one bespoke dialog.

### 8.9 Cards

**The explicit answer to the card question: cards remain central in Press, and a
card becomes a *column of a page*.**

The shipped product's card story is a symptom worth stating: the design system's
named card component has **one** production call site, six distinct card
constructions exist, and each screen assembled its own. The shape of the problem
is that the app *looks hand-made in twenty slightly different ways and none of the
differences is a decision anyone made.*

**In Press, a card is a ruled panel with three fixed zones and a rule system:**

- A **column header** in tracked caption case — the field name, small, quiet, and
  the same style everywhere it appears. A section label is *never* the same size
  as the content it labels, which is a failure the shipped app has in 29 places.
- A **body zone** whose contents are the same across the product: a name, a
  value, a rule, a mark.
- A **footer rule** with an optional right-aligned action, because a rule under a
  group is what tells the user the group ends.

**What earns a card, in Press:** a card appears when a group of things is one
thing *and* the group has an identity (a habit, a week, a task, a metric set). A
card does **not** appear merely because a screen needs vertical separation —
that is what a **rule plus a tracked column heading** is for. This is a real
distinction and it is the direct fix for the shipped failure in which a section
boundary, a card boundary and a row boundary are visually indistinguishable,
which is why the shipped Settings screen reads as five undifferentiated blocks
and why the app's most-used section label — 29 call sites — is invisible.

**Radius: small and honest.** Press uses a small corner radius (visually, in the
region of 4–6 dp, with straight-cut and squared variants used for the rules
themselves). It does **not** use the large-bubble radius. Reason: a large radius
on a sheet of paper is a lie about the material, and — more practically — a
16 dp card with a hairline and a translucent fill is the default 2024 Android app
surface, which is named in Phase 1 as *"generic because unowned"*. Press's card
must not be that.

**The list row is a rule-separated row, not a card.** A card per row makes a
12-item day into 12 boxes, which is the shipped Tasks problem in a different
place. In Press, a day's activities are **one sheet with rules between rows** —
which is what a ledger page actually is, and which also fixes the shipped
variable-row-height problem, where a row is 44 dp, 62 dp or 84 dp depending on
whether it has a schedule label and a streak badge, so a list reads as a ragged
stack instead of a rhythm.

**What this costs:** a card in Press carries less contrast from the page than a
translucent card does, because the mechanism is a shadow rather than a fill
difference. On a low-quality or very bright display in Light, the shadow may
under-perform. Press states that as a measurable risk in 8.20 rather than
claiming it away.

### 8.10 Iconography

**The explicit answer to the icon question: Press's iconography is
*weight-led*, which is a real dependency, and Press states the dependency rather
than hiding it.**

The three-way conflict is factual. The **code** uses Material **Icons** via the
extended set — a static vector collection with **no weight axis at all**. The
**documentation** — three KDocs, the design system document, and the design
system's own master ADR — names Material **Symbols** Rounded. The **concept
pack** ships Material Symbols Rounded as a *webfont* with a declared weight axis
from 100 to 700. The audit's finding is that this is **one decision that was
recorded and never honoured**, and that the consequence is functional rather than
aesthetic: the variable-weight capability the concept assumes is unavailable to
the app, and cannot be had without changing the icon source.

**Press's position: identity is carried by a *weight* of a glyph, and therefore
by typography, not by a pictographic family.** In Press, icons are *low-weight
and consistent with the type*: a stroke weight that matches the caption's
tracking-adjusted weight, at a small size, monochrome, at ink colour. The visual
system says "this is a printed book" partly because its symbols are drawn at the
same optical density as its letterforms.

**This requires a glyph set with a weight axis, and the shipped set does not have
one.** Press therefore carries an **explicit, declared dependency on Q6 being
settled toward the variable family**, and states the consequence if it is not:

- **If Q6 settles toward Material Symbols Rounded** (variable 100–700), Press's
  iconography is deliverable as designed, and the weight axis becomes a second,
  independent use of a variable axis alongside Manrope's.
- **If Q6 settles toward keeping the static Material Icons set**, Press's
  iconography must be re-cut at a single weight, and it loses precisely the
  property it was designed around. **The direction survives; the identity claim
  weakens.** Press would then need a different load-bearing identity carrier,
  and the obvious candidate is the rule-and-column system, which is weightless
  and would carry more of the load.

**This is the honest shape of a Q6-dependent concept**, and Press is the only
one of the four directions that takes it.

**Two secondary positions inside the same answer:**

1. **Item identity is not an icon.** Phase 1's finding is that habit identity is
   entirely borrowed — zero custom iconography, twenty keys resolved to Material
   glyphs, and three non-aligned vocabularies (what the picker offers, what the
   renderer draws, what the colour map names) that have drifted, so a user who
   picks "Spark" gets a star and "Art" falls to the same colour as a habit with a
   corrupted key. **Press removes the picker's icon axis from the primary
   identity position.** An item is identified by its **name, its position in a
   stable order, and a small monochrome key mark that is a *typographic*
   device — a bracket, a rule, a stroke weight** — not by a pictograph. The
   practical effect: the most consequential choice in the creation flow ("build
   your own") stops being represented by a fallback star in a fallback colour,
   because there is no fallback to fall into.
2. **No fire.** A flame is a pictograph for a streak. In Press a streak is a run
   of marks, so a flame would be a second, contradictory representation. The
   shipped fire icon is removed, and the streak is drawn. This is the direction
   that most completely answers the contradiction the audit named.

**Where icons remain**, Press uses them sparingly and for *only* structural
things: back, search, a disclosure chevron, an overflow. Never for identity,
never for a state, never for a category.

### 8.11 Navigation

**Press's navigation model: a flat bottom bar with a rule, four destinations, and
no floating.**

The four destinations — Home, Habits, Tasks, Insights — are a locked decision and
Press keeps them. What Press changes is the **material and the placement of the
chrome**, because the bar's material is explicitly an open question.

The shipped bar is a floating, translucent, heavily rounded bar with an internal
radius that does not exist in the design system's vocabulary, an icon size that
matches no token, and 12 sp labels — the app's smallest type, on its four
primary destinations. It is also, by the design document's own clause, a default
treatment the document forbids for navigation.

**Press's bar:** flush to the bottom of the window, a **matte sheet** like every
other surface in the direction (opaque, not translucent, so it never competes
with content scrolling under it), separated from the page by a **single hairline
rule** rather than a shadow, with a **hairline overline at 40% opacity marking
the active destination** plus a filled-weight glyph. Labels are tracked caption
case, not 12 sp body. The bar has **no rounded corners, no border radius, no
shadow** — it is a printed footer, the way a page has a footer.

**Why a flat bar and not a floating one, stated as a cost:** a floating bar reads
as an overlay and makes the content area ambiguous — how much of the screen is
mine? A flat bar is honest: it is a region of the page. Press buys a stable
content extent and a clean bottom edge, and pays with the loss of the floating
bar's visual lightness. That trade is defensible for a print-derived identity and
is the *right* trade for it, because a floating element on a piece of paper looks
like a sticker.

**Row → detail.** The shipped app's most-used transition is a 200 ms crossfade,
identical in both directions, and its destination is behind an asynchronous gate
that puts a Material spinner on **every** detail navigation in the product.
Press specifies the alternative, and it is Android-native rather than a web
pattern: **the shared element is the item's own rule-and-tick mark**, which
already exists in the list row at small size and becomes the header mark on the
detail page at large size. The transition is a **container transform plus a
shared element on that mark** — the row's mark grows into the page's header mark
while the page's rules draw in beneath it. Because the mark is a *rule and a
tick*, the shared element is a horizontal line and a small stroke, both of which
scale beautifully and neither of which requires the destination's content to be
known in advance. **The async gate is what makes a shared element impossible
today, and Press specifies removing the gate as a precondition.** This is a
Compose shared-transition answer, not a web animation.

**Back versus forward.** The shipped app cannot distinguish them. Press
specifies: forward is a container transform, **back is the same transform
reversed**, so the two are finally different.

**Inset contract.** One contract, in the page's own vocabulary: **every screen's
title sits on the same hard left edge at the same margin**, the header band is a
stated fixed height on every screen that carries one, the top and bottom bands
are flush regions of the page rather than overlays, and the bottom band is
reserved by the grid so that no content can sit under it. A screen's insets are
therefore properties of the page, never of whichever screen happens to be on top
— which is the direct answer to the three coexisting inset strategies the shipped
app carries.

### 8.12 Home / Today

**Audit verdict for this screen is REIMAGINE. Press takes that literally: the
photographic hero is removed, and the screen is rebuilt as a *column* whose top
is the answer and whose body is the record.**

**Purpose.** Answer *"what do I need to do today and how am I doing?"* in one
glance, without scrolling, in both environments, at 200% font scale.

**Hierarchy — strict, in this order:**

1. **The figure.** "3 of 5" at primary-figure size, with the denominator
   smaller and lighter so the pair reads as one object. This is the largest
   element on the screen by a wide margin, and it is **larger than the greeting
   above it** — an inversion of the shipped screen, where the answer is body text
   beneath a larger greeting.
2. **A tracked column heading** immediately under the figure, in caption case:
   `COMPLETED TODAY`. Small, quiet, and the same style as every column heading
   in the product.
3. **The date and the greeting**, demoted below the figure, at display weight, in
   **one line**, with the date as a caption. The shipped screen's stack is
   date → greeting → percentage → "3 of 5" → message, i.e. priority running
   exactly backwards. Press runs it forwards.
4. **The record.** One sheet, one rule-separated row per eligible activity, in a
   stable order: incomplete first, in the order the user set; complete below,
   grouped under their own rule.
5. **The week at risk** — a single line, in figure-secondary size, naming the one
   thing that matters if it is true, and absent when it is not.
6. **No motivational message.** The shipped app's message slot says the same
   thing all day; four of its six branches reference no user data, and one is the
   product's own tagline used as an everyday state line. Press removes the slot.
   If there is something worth saying, the week-at-risk line says it with data in
   it.

**Composition.** A single **editorial column**: a fixed, wide left margin
(visually in the region of 24 dp), a **wide right margin** (visually 32–40 dp)
that is visibly wider than the left, and content that never bleeds. A
right-weighted, asymmetric margin is what makes a printed page read as a printed
page; symmetric margins read as an app. The whole composition is **left-aligned
to one hard edge**, and every rule in the product terminates on that same edge.
Below the figure the layout is a single column, full stop — no two-column stat
rows, no tiles.

**Primary visual.** The figure. Nothing competes with it. There is no hero image,
no orb, no glow, no ring. A 96 dp progress ring that runs a permanent
six-second loop to display a number that changes a few times a day is removed
outright; Press shows the number.

**Key components.** The figure; the column heading; the record sheet with
rule-separated rows; within a row, a **48 dp completion control** (see 8.19), the
item's name at body weight, a second line carrying **the decision-relevant
number** — `5 OF 8 GLASSES` — at figure-secondary, at the size that makes it the
most decision-relevant thing on the card, which is the opposite of the shipped
12 sp; and, for the item with the longest live run, a small **run spine**.

**Data shown.** The five eligible items, three complete, at the canonical
content. The target progress for the two target-based items. The current streak
for the item with the longest live run. **Nothing else.** No statistics, no
month view, no trend, no comparison to an average, no library of habits — the
locked design document forbids a full habit library on Home and forbids detailed
analytics on Home, and Press obeys both, which is also why the item list *is* the
Home screen rather than a tab into a larger one.

**Interaction emphasis.** The completion control, and the target entry. Press's
row rhythm reserves a fixed metadata line so a row is the same height whether or
not it carries a label, which fixes the shipped variable-row-height problem at
its root.

**Distinctive visual behaviour.** **Completing the last item does not show a
snackbar.** The shipped app's entire day-completion celebration is one
long-press haptic plus a snackbar reading *"All done - momentum secured."*, while
a milestone state and a reward duration exist, are documented, and are
unreachable — and while the hero simultaneously displays *"Everything complete.
Rest easy."*, so the same news arrives twice within 200 px. **Press replaces
this with a physical gesture:** the day's record gets a **pressed-stamp**
treatment — the whole rule-set takes a single impression, once, the way a date
stamp is pressed onto a page — and the figure changes. No confetti, no
particles, no sound, no toast. This is the direction's one moment of physical
gesture, and it is reserved for a *milestone*, not for routine completion, which
is the only way to satisfy both the locked design document's *"no routine
confetti"* and the project requirement for a *streak celebration*.

**Light-mode considerations.** The page is warm off-white with fibre. The figure
is ink. The record sheet is the same paper with a lit shadow. The column heading
is ink at reduced opacity but at **body-contrast or above**, not at the shipped
pattern where a metadata tier is set in a token that has already been measured
failing the contrast floor in Light. No artwork is present, so the specific
measured Light failure — secondary text at 3.11:1 over sampled hero art —
**cannot recur**, because the condition that produced it does not exist.

**Dark-mode considerations.** The field is a very dark warm near-black, smooth.
The record sheet is a **light-toned object** on that field — the page is
daylight-coloured in a dark room. The figure is ink on that light page, not
white. This is the single most distinctive thing about Press Dark and it is the
mechanism that answers Q1. It also means **Press Dark has a much higher
contrast budget than the shipped Dark** and can therefore afford the large figure
without any glow, because legibility in Press Dark comes from the object's own
lightness, not from a halo.

### 8.13 Habit Experience

**The `DayCell` insight, taken seriously.** The shipped product has exactly one
correct three-state data mark, and it is the only thing in the entire
visualisation layer that satisfies the requirement that a Mon/Wed/Fri habit must
not make Tuesday look missed. That component is:

- completed — a gradient-filled 32 dp circle with a check
- incomplete — a 1 dp ring
- not scheduled — an 8 dp muted dot in a 32 dp footprint

**Press does not keep this component's shapes, and Press explains why keeping them
would be a mistake.** A muted dot in a 32 dp footprint is a *colour-differentiated
mark of low visual weight* — a rest day is drawn as a small, dim, ambiguous
thing. It is correct in the sense that it is *different*, and it is wrong in the
sense that it still **draws** the rest day, which makes the eye register a
deficit on a day the user was never meant to do anything. It also has no
`contentDescription` at all, so the product's only three-state visualisation is
invisible to TalkBack, while the calendar's simpler day cell composes a proper
spoken description.

**Press's replacement keeps the three states and the correctness, and changes the
principle from *"three marks"* to *"three structural conditions"*:**

| State | Rule present? | Cell content | Hue |
|---|---|---|---|
| **Completed** | yes | a heavy tick, in the accent | accent |
| **Incomplete** (scheduled, not done) | yes | empty, with a shallow struck hairline across it | ink, at body contrast |
| **Not scheduled** | **no — the rule is not drawn, and the day's column is empty paper** | nothing at all | none |

**The critical property: a rest day has no footprint.** It is the absence of a
rule, not a dimmed version of a rule. This is a stronger expression of the streak
rule than a dotted marker, because a marker still asks the user to notice
something. And because the differences are structural — presence of a rule,
presence of a strike, presence of a tick — the mark satisfies *"state is never
communicated by colour alone"* by construction rather than by a redundant second
channel.

**Habit identity.** The habit's name at display weight, large. Beneath it, a
tracked caption line carrying frequency, target and reminder in three **separately
weighted and separately truncatable** slots — the shipped app pre-joins them into
one string with a one-line clamp, so a long frequency can push the reminder off
the end. Press separates them structurally.

**Completion interaction.** See 8.19.

**Daily status.** On the habit's own page, today is the first cell of the first
row, at the same size as the rest, with the weekday initial in tracked caption
beneath. Not enlarged, not coloured differently.

**Weekly status.** A **7-column state grid**, never a bar chart. The
Habits-management specification forbids a bar chart for the primary weekly view
and gives a data reason: a habit can be completed at most once per day. Press
obeys it, and Press also states what it would take to answer differently — a
windowed, value-bearing view is a *different question* (Q4) and would need
per-day values, which do not currently leave the repository.

**Streak.** A **figure at figure-secondary size** — larger than the habit's name
on a list card, which the shipped app gets backwards — beside a **run spine**: a
vertical rule with one tick per contiguous scheduled day, and a **visible break**
where a scheduled day was missed. The spine is the streak made visible as a
*structure* rather than a *badge*, and its break is the honest signal: the run
genuinely broke, and Press shows where. Current is printed; longest is printed
one tier smaller, because current is live and longest is archive.

**Consistency.** Printed. The engine already computes a consistency score
documented as the percentage of scheduled occurrences hit over the last 30 days,
and it has **zero presentation call sites** — for a product named Momentum, the
one number that says whether the user is consistent is never shown. Press shows
it, on the habit's page, at figure-secondary, with its window stated in words
next to it, and with the **missed-occurrence count** beside it. Showing a metric
that is computed and tested and hidden is indistinguishable, to a user, from a
metric that does not exist.

**Missed versus rest.** Covered above. Rest has no footprint. Missed is a struck
cell. These are the two most important marks in Momentum and in Press they are
the two most carefully drawn.

**Feedback.** Per completion: the tick draws, the row's rule takes the impression,
a short haptic. No scale bounce — Press has no springs, and a spring pop on a
printed record reads as a physical nudge of an object that is not there. Per
milestone: the pressed-stamp gesture. Nothing else moves.

### 8.14 Tasks

**The problem Press must solve, stated honestly.** The shipped Tasks tab is the
shared activities list filtered to tasks. On an empty Tasks tab it reads *"No
activities yet"* and offers *"Create activity"*; the app's FAB says *"Add
activity"*; the sheet says *"Create item"*; the sheet's own options say *"Habit"*
and *"Task"*. **Four names for one action across three surfaces, and the word
"activities" appears on a tab labelled "Tasks".** Every row additionally carries
up to two unlabelled 48 dp circular management buttons *beneath* it, outside it —
so the Tasks row is roughly twice the Home row's height and weight, the
management controls of a task are the most prominent repeated element on a screen
that has **no completion affordance of all**, and two of those buttons are
visually identical to each other, distinguished only by glyph.

**Press's answer, in three moves:**

1. **One noun per surface.** The tab is Tasks, the screen says Tasks, the action
   is *New task* on this surface and *New habit* on the other, and the sheet
   title is *New*. No direction may leak the shared-surface vocabulary through a
   filter.
2. **Tasks is a management surface, and it looks like one.** The architecture
   already implies this: management belongs on Habits, action belongs on Home.
   Press therefore gives Tasks the **same overflow affordance Habits gets** — a
   single three-dot control at the end of a rule-separated row — instead of a bar
   of two identical circles. Row weight drops by roughly half, and the two
   identical buttons stop inviting mis-taps. The `CompletionButton`'s absence on
   this screen stops being a defect, because the screen is no longer pretending
   to be an action surface.
3. **The row is the same object as the Home row.** Habits and tasks share a
   visual grammar by locked decision. In Press they share it *literally*: the
   same rule-separated row, the same name weight, the same metadata slots, the
   same column heading. What differs is **which facts are present** — Tasks rows
   carry recurrence, category and status; Home rows carry today's target progress
  — and that difference is a *data* difference expressed by which slots are
   filled, not by a different component. The same suppress-the-streak mechanism
   the shipped app uses for tasks is expressed in Press as an **empty slot**, so
   a task row and a habit row are literally the same shape.

**What a Press Tasks screen therefore looks like:** a flat page, a tracked
column heading `TASKS`, a count in figure-secondary at the right of that
heading, a single search field rendered as a **ruled field** rather than an
outlined Material box — the shipped app's only text field leaves its text and
label colours to Material defaults drawn from a palette this product does not
use — then the two filter axes, which in Press are **one labelled filter bar**
rather than two visually identical rows of pills with no axis label, then the
list, then a flat-bottom primary action that docks above the navigation region
rather than a floating action button of one material on Home and a different
material on Habits.

**Filters.** Press expresses a filter as a **tracked caption axis label followed
by a segmented set of ruled cells**, so a filter is never mistakable for a
setting, and two adjacent axes can never read as one unlabelled pair.

**The empty Tasks state.** *"No tasks yet."* + one line: *"Add one, or let it
come up on Home when it is due."* + a single action. Not *"Create something
worth showing up for."*

### 8.15 Insights

**The shipped Insights screen is the most concretely diagnosable surface in the
product, and Press's job is to make it answer a question instead of listing
numbers.**

What it does today: the view model loads **60 days** of completion history and
the chart draws **7 bars**. The largest figure in the entire application — the
only one above 20 sp — is the **7-day rate**, set at 40 sp, while **today's**
completion is 20 sp with an out-of-family weight, the streak count is 12 sp, and
a habit's live target progress is 12 sp. **The product is twice as emphatic about
the past week as about right now.** The two lists on the screen — by category,
and "most consistent" — are the two places where a *ranking* is the entire value,
and neither is sorted: categories are listed in map-iteration order, and
"most consistent" shows only the best performers with no contrast against the
worst. The trend is a bare word with no magnitude and no window, and its
insufficient-data case renders the literal string *"Insufficient data"*.

**Press's structure — question, then figure, then mark, then interpretation:**

1. **The question, as the screen's own column heading.** *"How consistent have
   you been?"* — tracked caption, top of the page, above the figure. The shipped
   screen opens with a number and a bar chart and never says what it is for.
2. **The figure.** The **consistency score** at primary-figure size, with its
   window printed beside it in caption case: `78% · LAST 30 DAYS`. The number
   that defines this product, at the size the number deserves, labelled with the
   window so it cannot be read as a lifetime value.
3. **A second figure, one tier down, with a real comparison.** The 7-day rate,
   with a **signed delta against the 30-day average printed as a figure**, and a
   direction mark. The shipped app already has this one comparison cue and it is
   genuinely good; Press keeps it and gives it a weight and a mark.
4. **The mark.** The 7-day view is drawn as a **state grid in the same vocabulary
   as the habit week** — completed, incomplete, not scheduled, missed — as seven
   ruled cells, so the weekly view stops discarding the scheduled count it is
   already holding. **In Press this is the single most important change on the
   screen:** a user with a sparse schedule can no longer read a rest day as
   slackeriness, and the answer to *"which day did I miss"* is legible without
   reading digits. The same vocabulary then repeats, unchanged, in the 30-day
   strip, so the app has **one** mark language instead of seven.
5. **A ranked list that is actually ranked.** By category, sorted, worst first
   when the user has a category at 0% — because the value of a ranking is
   comparative and the shipped list's unsortedness throws that away. Each row:
   category name at body, rate at figure-secondary, a 6 dp **ruled** bar drawn
   with the progress-track token rather than one of three different surface
   tokens, and *"n of m complete"* as a caption.
6. **Interpretation as a sentence, in body type.** *"You hit 6 of 8 scheduled
   evenings. Tuesdays are the ones you skip."* **This sentence is generated from
   the user's own data** — which the shipped app's message slots are not, and
   which is why the shipped app's Home message says the same thing all day.
7. **Missed occurrences, printed.** The engine computes them; nothing shows
   them. They are what makes a calendar meaningful, and Press shows them as a
   count with its window.

**What Press will not do on this screen:** a dashboard wall. A 2×2 grid of
equal stat tiles is the shipped pattern and it is the reason the screen fails
its own acceptance criterion — *"Insights feels analytical without becoming a
dashboard wall."* No decorative charts, per the locked rejected list. No 3D, no
rainbow, no chart with no axis meaning. One mark, used at every size, is the
whole answer.

### 8.16 Onboarding

**Audit verdict is REIMAGINE. Press replaces four cinematic photographic pages
with four pages that are *the product*, arguing the actual case, in the actual
typeface, on the actual paper.**

**The conceptual argument, in four beats.** The shipped onboarding's failure is
not its mood, it is that **zero of its four bodies contains a single fact about
what the app does** — no Activity concept, no streaks, no progress as a mechanic,
no offline operation, no on-device storage, no account, no cloud, no tracking —
while the product's own best-written line, *"Everything stays on this device. No
account, no cloud, no tracking — offline by design,"* sits in Settings, behind an
icon, on a screen a first-time user does not visit. The app describes itself in
the third person inside its own UI, calls itself *"a calm and powerful app"*, and
one of its four headlines is the only one in title case without terminal
punctuation.

**Page 1 — the record.** Headline: **"Keep the record."** One line: *"Momentum is
a private record of what you actually did."* Visual: a single ruled page, empty,
with the figure space already reserved. No photograph. This page establishes the
metaphor before it asks for anything.

**Page 2 — the one idea.** Headline: **"Habits, tasks, medicine, practice — one
thing, tracked."** One line naming the Activity concept in the user's own terms,
with a short list of the kinds of things a person actually tracks. Visual: the
same page, now with four rules and four ticks, the third one partial.

**Page 3 — the difference nobody else can claim.** Headline: **"Nothing leaves
this phone."** One line: *"No account. No cloud. No tracking. No ads. Your data is
in your storage and nowhere else."* Visual: the page with a visible **page edge
and nothing beyond it** — no shadow, no glow, no network. This is the page the
shipped app does not have, and it is the page that would win the user who is
choosing between habit apps, because no competitor in that consideration set can
say it.

**Page 4 — the day.** Headline: **"Tomorrow, this is the top of the screen."**
One line, and a single action, *"Start"*. Visual: the figure at full size, with
the record beneath it, on the real page. No *"Ready to build Momentum?"* — which
is a question about the future, when the product is about the present.

**The re-threading that makes this work in Press.** The shipped onboarding is
theme-blind and dark-only: **zero** reads of the theme system in its files, four
photographs with no Light variant, its own 14-constant palette, its own ink, and
a hand-patch in a `DisposableEffect` that force-sets the light status-bar
appearance and restores it on dispose — code that is correct and exists **precisely
because onboarding does not know what theme it is in.** A first-run user who chose
Light walks through four near-black photographic screens and arrives at a light
cool-grey app: the first thing the product shows and the thing it becomes are two
different visual products.

**Press removes the photographs entirely, and the theme-blindness with them.**
There is no art to recolour, no palette to reconcile, no Light variant to derive,
and no system-bar hand-patch — because the onboarding is now simply *the first
four screens of the app*, in the app's paper, in the app's ink, in **whichever
environment the user is actually in**. The illustrations that were flattened into
photographs still exist as vector sources in the repository, unused; Press's
argument is that a page made of type and rules needs no illustration at all, and
that removing them is what lets the onboarding survive a theme change.

**This is the one direction in this document where Press and Direction D fully
agree**, and the agreement is independent: D arrives at the same place by
governing the Aurora identity rather than replacing it.

### 8.17 Motion

**Press's motion personality: things settle. Nothing breathes, drifts, orbits,
pulses, or runs on a wall clock.**

The shipped app runs **31** animation call sites across 9 files, of which the
product's only *consistently* used motion token is a 6-second ambient value
whose sole purpose is two decorative infinite loops. Six of the nine motion
tokens have **zero** production call sites and **16** hand-written durations have
replaced them. Two of those infinite loops run on the primary screen whenever any
progress exists — for a number that changes a few times a day — and the app's
own Aurora KDoc calls the system *"state-responsive, not decorative wallpaper"*
while the ambient field has no state parameter at all and the state-responsive
field is pinned to its lowest intensity on two of its three screens.

**Press's motion inventory — the complete list, with each entry's trigger and its
reduced-motion behaviour:**

| Motion | Trigger | Duration intent | Under reduced motion |
|---|---|---|---|
| **Rule draw-in** | a screen or card first appears | short, 150–200 ms | appears already drawn, no transition |
| **Tick draw** | an item is completed | short, 150–250 ms | the tick appears with no stroke animation |
| **Pressed-stamp impression** | the **day** reaches 100% — a milestone, not a completion | longer, 400–500 ms | a single static tonal change of the rule-set; no travel, no scale |
| **Run-spine extension** | a streak extends past its own previous length | short, 200–300 ms | the spine extends with no animation |
| **Figure change** | any figure's value changes | instant | instant |
| **Row settle** | a row moves between the incomplete and complete groups | short, 200–250 ms, transform only | the row relocates instantly |
| **Page turn** | pushing a destination | container transform, 250–300 ms | no transform; the destination appears |
| **Back** | popping | the same transform reversed | no transform |
| **Continuous ambient** | **none. Press has none.** | — | — |

**What this costs, stated honestly, because it is the direction's largest single
liability:** Press's product is *calm*, and the locked design document says
completion should feel *"rewarding but restrained"* — but the project
requirements also demand *"meaningful micro-interactions (completion feedback,
streak celebration)"*, and Phase 1 records that the two can only both hold if the
celebration is **earned by a milestone rather than by a completion**. Press takes
that reading literally: routine completion gets a tick and a haptic and nothing
more, and the only large gesture in the entire product is reserved for a
milestone. A user who completes five items in a week will see the pressed stamp
once. That is a deliberate, defensible reading of two requirements that appear to
be in tension — and it is a risk, not a solved problem.

**The permanent-loop removal is also a performance win with a real precedent:** the
shipped app already contains the correct pattern for gating a glow on a
meaningful value — a glow gated so that 0% pays nothing, and a remembered phase
that returns a constant and composes no transition at all when disabled. Press
extends exactly that idea to the whole motion system: **if a value is zero,
nothing animates, ever.**

**Reduced motion.** Every entry in the table has a defined non-animated
behaviour, and the behaviour is a **substitution rather than a shortening** —
`ProgressOrb` and `CompletionButton` in the shipped app already do this
correctly by switching to a zero duration or nulling the spec rather than
halving a duration, and Press keeps that approach as a pattern. Press's Settings
copy claim is written to match: the shipped app promises *"Calm animations
throughout the app"* while the setting reaches **1 of 14** call sites — a
user who needs reduced motion is told it was applied and it was not, which is a
truthfulness failure and the most consequential single defect Phase 1 identified.
**In Press the claim is only made if it is true**, and the direction's motion
inventory is short enough that making it true is tractable.

### 8.18 Light / Dark

Described **independently**, because the two are different materials.

**LIGHT — "Daybook."**
The material is a warm off-white stock, visibly paper, with a faint fibre. The
ambient state is unremarkable: no glow, no gradient, no field. Depth is a single
**lit-from-above shadow** beneath a hairline edge — the mechanism a sheet of
paper has over the sheet below it, and a mechanism that works precisely because
the surface is light and a shadow is therefore a lit-artefact rather than a
darkness. The tonal ladder is **five genuinely distinct values**, because the
mechanism is tonal, not a collapsed pair of identical whites. Texture: 8% fibre.
The figure is ink at near-black. The accent appears in exactly three roles. There
is no artwork anywhere in Light, which is what makes the measured Light contrast
failure structurally impossible to reproduce. Reading Light: *a well-made
notebook on a desk in daylight.*

**DARK — "Ink."**
The material is a very dark, slightly warm, near-black field, and it is
**smooth** — no fibre, because ink is smooth. The record is **not drawn on the
field; it is a light-toned object placed on it.** The page is daybook-coloured in
a dark room, and it is the brightest thing on the screen. Depth is expressed as
**the object's own lightness plus a soft outer falloff into the field** — the
field is unlit, the object is lit, and the separation is the difference between
them rather than a shadow. The tonal ladder exists again, in the opposite
direction: the field, the falloff, the object, the object's raised region.
Texture: none, deliberately, so that the light-toned objects are the only
textured things on screen. The figure is ink on the light page. The accent is
retained but at slightly reduced saturation against the light page, because a
saturated accent on a light object reads hotter than it does on a dark canvas.

**The mechanism statement, made explicitly because Q1 requires it:**

> **Press Light's depth mechanism is a cast shadow, which requires a light
> surface. Press Dark's depth mechanism is a light object on an unlit field, which
> requires a dark field. Neither mechanism is available in the other
> environment. The layout, the type scale, the rules, the marks and the
> composition are identical between them — so the two read as the same product
> printed on different material, and they are not one design with a brightness
> switch.**

The design-system acceptance criterion — *"Light and Dark Aurora feel like the
same product"* — is satisfied by construction: Press shares its entire structural
system and varies only the material. The audit's finding that the shipped
environments share all five accent hues with the Dark value simply lightened, and
introduce and remove nothing, is the failure Press is correcting — Press's
accents are three named roles, and the *roles themselves* are environment-
independent while the *values* are re-chosen per environment against a real
contrast measurement, not derived by lightening.

### 8.19 Signature Element

**The pressed stamp.**

When the day's record is complete, the whole rule-set of the day's sheet takes a
single **impression** — the way a date stamp is pressed into paper. The figure
changes. There is no toast, no bar, no particle, no sound.

**Why this is the signature and not something else.** It is the only moment in
the product where the metaphor becomes physical, and it is therefore the only
moment a user could describe to a friend. It is *earned* — it fires on a
milestone, not on a routine completion, which is the only way Press satisfies both
the locked "no routine confetti" rule and the project requirement for streak
celebration. It is *silent* — no sound, no haptic beyond a single firm tick,
because a page does not announce itself. And it is *cheap to draw honestly*: it
is a tonal and edge treatment of an existing set of rules, not a particle system,
so it does not cost a rendering pipeline, and it degrades to a single static tonal
change under reduced motion without becoming a different idea.

**The secondary signature — the absent rule.** A rest day has no footprint. A
scheduled day that was missed has a struck cell. A completed day has a tick. A
user learns three marks once and never has to decode them again. This is the
direction's answer to the question the product's only correct data mark was
already answering correctly, expressed as a principle rather than a component.

### 8.20 Risks

Honest failure modes, stated as failure modes and not as caveats.

1. **Press is austere, and austere is easy to misread as joyless.** A user who
   wants their app to feel good may experience Press as a spreadsheet with good
   typography. The warm paper and the single expressive gesture mitigate this;
   they do not eliminate it. **This is the risk most likely to be realised.**
2. **Press's Q6 dependency is real and load-bearing.** The weight-led iconography
   requires a glyph set with a weight axis, which the shipped dependency does not
   have. If Q6 settles the other way, Press's identity carrier is materially
   weakened and the direction needs a second load-bearing device it does not
   currently have. Press states this rather than assuming the answer.
3. **The Light shadow mechanism depends on the display.** A cast shadow is the
   whole depth system in Light, and a low-quality or very bright panel in bright
   sunlight may under-render it. Press mitigates with a hairline edge, but a
   hairline is precisely the failure mode the direction was built to escape — so
   on a bad display, Press Light could degrade toward the shipped condition. This
   is the risk that would most embarrass the direction's own thesis, and it is
   **unmeasurable without a rendered image**, which is exactly what Phase 2B
   exists to produce.
4. **Numbers outranking words will confuse some users.** All-numeric hierarchy
   is a real trade. A user who wants to read *what* they are doing has to work
   slightly harder. Press accepts this because the product's own declared
   headline is a numeric question.
5. **The pressed stamp will not read as a reward to everyone.** It is a
   *withdrawal* of a reward the current app half-provides as a snackbar. Some
   users will experience the absence of a toast as the absence of acknowledgement.
   A direction that gives a large moment once a week and nothing else is betting
   that the *record* is the reward. That is the bet.
6. **Removing all photography removes all warmth at the top of the app.** Home is
   the product's most valuable real estate and Press fills it with type. That is
   a deliberate choice, and it is a choice that could read as austere or as
   correct depending entirely on execution — and execution cannot be judged
   without a rendered image.
7. **Press removes the artwork pipeline's most visible use.** A correct,
   documented, cheap art pipeline exists and is a named strength. Press keeps the
   *contract* — one asset pair, theme-selected, callers unable to art-direct —
   but Press has no use for the pair. That is a real loss of an existing asset,
   honestly stated rather than disguised as a simplification.
8. **The daybook-page-in-a-dark-room is a strong image and it may not be
   comfortable at scale.** A large light-toned object on a dark field is
   high-contrast and legible, but a phone screen that is mostly dark with a bright
   object on it is a *battery and comfort* question, and it is an unusual register
   for a consumer Android app. It is defensible; it is not obviously safe.

**The single unknown Press most needs, and the branches:**

> **Unknown: whether a full-bleed light-toned object on a dark field reads as
> premium or as a night-reading mode.**
> *If it reads as premium* — Press's Q1 answer is its strongest asset and Dark
> becomes the direction's signature.
> *If it reads as night mode* — Dark falls back to dark-on-dark with the tone
> shifted to a *lighter field with a lit page* rather than a dark field, which
> keeps two environments but weakens the asymmetry that makes them different.
> Neither branch requires a different product. Both require a rendered image to
> choose between, which is Phase 2B's job and not this stage's.

### 8.21 Press — the five screens, as visual concepts

The same content in all five: **Tuesday 29 September, five eligible, three
complete**, plus the empty-day and 100%-day states. Full fixture in §4.6.

#### Screen 1 — Home / Today

- **Purpose.** Answer *"what must I do today, and how am I doing?"* above the
  fold, in one glance, in both environments.
- **Hierarchy.** (1) The figure "3 of 5". (2) Selvedge heading "Completed today".
  (3) Date + greeting, one line, below the figure. (4) The record. (5) The
  at-risk line. No motivational slot.
- **Composition.** A single editorial column, hard left edge, wide right margin
  (visibly wider than the left), no bleed, no images, no second column.
- **Primary visual.** The figure. Nothing competes with it.
- **Key components.** Figure lockup; tracked column heading; record sheet with
  rule-separated rows; a 48 dp completion control per row; a run spine per row.
- **Data shown.** Five eligible items, three complete; live target for Drink
  water (5 of 8) and Read 20 pages (0 of 20); run lengths 3, 1, 6, 12, 0.
- **Interaction emphasis.** The completion control; the target entry; fixed row
  height so the list reads as a rhythm.
- **Distinctive behaviour.** Completing the last item presses a **stamp** into the
  rule-set — no toast, no bar. A row that completes does not travel; its tick
  simply appears and its rule takes the tick.
- **Light considerations.** Warm off-white fibre ground; sheet depth by lit
  shadow; ink figures; accent only on ticks. No artwork, so the measured Light
  contrast failure cannot occur.
- **Dark considerations.** Very dark warm field; the record is a **light-toned
  object** on it; figures are ink on that object, not white on dark.

#### Screen 2 — Habit Detail (Drink water — Health — 8 glasses/day — 12-day run)

- **Purpose.** One habit's configuration, this week, its run, its consistency.
- **Hierarchy.** (1) The habit's name at display weight. (2) The week state grid.
  (3) The run spine + length at figure scale. (4) The consistency score with its
  window. (5) Configuration as a selvedge-headed panel of three separately
  weighted slots.
- **Composition.** Column; identity band at top; the week grid full content
  width; configuration as a ruled panel, not a tile grid.
- **Primary visual.** The week state grid — completed / incomplete / **not
  printed** — plus the spine beside it.
- **Key components.** Week grid of seven cells; run spine; consistency figure
  with window; three metadata slots (frequency, target, reminder) separately
  weighted; one action for pause/edit.
- **Data shown.** This week 6 of 8 scheduled hit, 2 days not scheduled; run 12;
  longest run 19; consistency 78% over 30 days; missed 6; status Active.
- **Interaction emphasis.** The week grid is history and is read-only; the
  single action is a labelled text control, not a bar of identical circles.
- **Distinctive behaviour.** None — **the detail page is the stillest screen in
  the product.** History does not move.
- **Light considerations.** Figure weight, not colour, carries the run. Ink on
  paper.
- **Dark considerations.** Figures are ink on the light-toned record. The spine
  is drawn in ink with the accent only on completed stitches.

#### Screen 3 — Tasks

- **Purpose.** Manage the things that must happen, as a management surface.
- **Hierarchy.** (1) Selvedge heading "Tasks" with a count at its right. (2) One
  ruled search field. (3) One labelled filter bar. (4) The list, one sheet with
  rule-separated rows. (5) One flat-bottom primary action.
- **Composition.** Flat page, single column, no per-row card, no button bar.
- **Primary visual.** The list sheet and its rules.
- **Key components.** Selvedge + count; ruled field; two labelled axis groups;
  rows carrying name, recurrence, category, status, and a single overflow.
- **Data shown.** "Book dentist" — one-time, due today, not done. "Renew
  passport" — one-time, due 12 Oct. Status axis: Active/Paused/Archived. Category
  axis with a zero-rate category present.
- **Interaction emphasis.** Overflow → Pause/Delete with an **opaque** paper
  confirmation naming permanence and the archive alternative.
- **Distinctive behaviour.** None. Management is still.
- **Light considerations.** Paper, sheet, rule. No accent except on the active
  filter.
- **Dark considerations.** The sheet is a light-toned object; the record reads
  ink on it.

#### Screen 4 — Insights

- **Purpose.** Answer *"how consistent have I been?"*, not *"what are my
  numbers?"*
- **Hierarchy.** (1) Selvedge heading, in question form. (2) The consistency
  score at figure scale, with its window. (3) The 7-day state row, same
  vocabulary as the habit week. (4) The ranked category list, worst first.
  (5) One interpretation sentence in body, generated from the user's data.
  (6) Missed occurrences.
- **Composition.** Column; the state row full content width; the ranked list as
  a ruled panel with graduated channels, not a tile grid.
- **Primary visual.** The 7-day state row.
- **Key components.** Selvedge question; consistency figure; seven state cells;
  ranked rows with channels; the interpretation sentence.
- **Data shown.** Consistency 78% over 30 days; 7-day rate with signed delta vs
  the 30-day average; a Mon/Wed/Fri habit so two days are **not printed**;
  one category at 0%; missed 6.
- **Interaction emphasis.** Switching the window. Nothing else.
- **Distinctive behaviour.** None. Analysis is still.
- **Light considerations.** Ink on paper; the accent only on completed states.
- **Dark considerations.** Ink on the light-toned record.

#### Screen 5 — Onboarding (page 3, the privacy page — the screen the shipped app
does not have)

- **Purpose.** State the one thing no competitor can claim, before the user
  commits.
- **Hierarchy.** (1) Headline "Nothing leaves this phone." (2) One line. (3) The
  page with a visible edge and nothing beyond it. (4) A single forward control.
- **Composition.** A single page, generous margins, text on the upper two
  thirds, the finished edge visible at the bottom. No photograph, no illustration.
- **Primary visual.** The page edge.
- **Key components.** Headline; one line; the ruled page; a single control.
- **Data shown.** "No account. No cloud. No tracking. No ads. Your data is in
  your storage and nowhere else."
- **Interaction emphasis.** One action. Nothing else is tappable.
- **Distinctive behaviour.** The page-edge indicator draws once on entry.
- **Light considerations.** The same page stock the app uses, in the user's
  actual environment — **no dark-only photographic pages**.
- **Dark considerations.** The same object treatment as the app's Dark
  environment, with the system-bar appearance derived from the app's own theme
  rather than the platform's.

### 8.22 Press — image-generation briefs

#### Brief A1 — Home / Today

- **Android-native context.** A premium Android habit/productivity app's primary
  daily screen. Material navigation grammar. Not a website, not a landing page,
  not a poster.
- **Device / frame.** One Android phone, portrait, centred, visible slim
  bezel, even margins on all four sides, content is the hero and the frame is
  quiet. One screen per image.
- **Screen purpose.** Answer "what must I do today, and how am I doing?" above
  the fold.
- **Exact visual hierarchy.** Top to bottom: a small tracked caption date; a
  large **"3 of 5"** in near-black ink, the "of 5" smaller and lighter in the
  same lockup; a tracked small-caps label "COMPLETED TODAY"; a greeting line in
  a lighter, larger-than-body weight, **visibly smaller than the figure**; one
  hairline rule; a ruled list sheet of five rows; a bottom navigation band with
  four items.
- **Composition.** Single column, hard left alignment, generous left margin,
  **wider right margin**, nothing bleeding to the edge except the navigation
  band. No images, no illustration, no photography anywhere.
- **Typography direction.** Manrope. The figure is the largest element on the
  screen, set heavy (real Bold) with slightly tight tracking, and it must be
  clearly larger than the greeting. Small labels are uppercase, tiny, and
  letter-spaced. Body is calm and readable. Every number is tabular.
- **Colour / environment.** Warm off-white page with a faint paper fibre. Ink is
  a very dark warm brown-black. **One** accent — a deep madder-ish red — and it
  appears only on the three completed ticks.
- **Surface treatment.** Matte paper. The list sheet is the same paper raised by
  a soft shadow directly beneath it and a faint hairline edge. **No glass, no
  translucency, no gradient, no glow.**
- **Major components.** Figure lockup; tracked heading; five-row list sheet with
  hairline rules between rows; one 48 dp circular completion control per row;
  a small vertical run spine on the two rows with a live run; a flat bottom
  navigation band separated by a single hairline.
- **Visual metaphor.** A ledger page. "3 of 5" is the count at the head of the
  page. Rows are ruled entries.
- **Interaction state.** Mid-day. Three of five complete. One row is a partial
  target at 5 of 8. One row is a partial target at 0 of 20.
- **Important data.** "3 of 5" · Morning run, Weekly review, Take the stairs
  (complete) · Drink water 5 OF 8 GLASSES · Read 20 pages 0 OF 20 · run 12 ·
  Tuesday 29 September.
- **Light/dark requirement.** This brief is for the **Light** rendering. A
  separate Dark rendering must be produced with the same content, and in Dark
  the page is a very dark warm near-black field and the list sheet is a
  **light-toned object** on it.
- **MUST be visible.** The figure larger than the greeting. A visible paper
  texture. Hairline rules between rows. Three ticks in the accent colour. The
  48 dp completion controls. The 5 of 8 target value at a size clearly larger
  than the row's metadata.
- **MUST NOT appear.** No photography, illustration, gradient mesh, glow, glass,
  blur, or translucency. No fire, flame, trophy, medal, star or confetti. No
  progress ring, no circular percentage badge, no donut chart. No dark
  photographic onboarding. No pills, badges, or tags. No English motivational
  filler. No second column of stat tiles.
- **Anti-patterns to avoid.** Marketing-hero composition; a centred headline; a
  three-feature card row; purple-blue gradient; rounded-bubble cards; a card per
  list row; tiny unreadable label text; a phone-shaped website; fake status-bar
  clutter.

#### Brief A2 — Habit Detail

- **Android-native context.** A pushed detail screen with a back affordance
  top-left. Android app bar grammar.
- **Device / frame.** Same phone, same scale, same even margins as A1. The frame
  must be identical between images.
- **Screen purpose.** One habit's configuration, its week, its run, its
  consistency.
- **Exact visual hierarchy.** Top-left back arrow; large habit name
  "Drink water" in a light display weight; a tracked caption line
  "HEALTH · DAILY · REMINDER 8:00"; a horizontal rule; the label
  "**THIS WEEK**" small and tracked, with the answer "**6 of 8**" **larger than
  the label** immediately to its right; the seven-cell week state row, each cell
  labelled with a tracked weekday initial; a vertical run spine with its length
  "**12**" at figure scale and a "days in a row" caption; a consistency figure
  "78%" with a "last 30 days" caption; a ruled configuration panel with three
  separately weighted rows; one labelled text action at the bottom.
- **Composition.** Single column, hard left alignment, wide right margin. The
  week state row is full content width. No tile grid.
- **Typography direction.** Manrope. The habit name is set lighter and larger
  than the figures' weight but **smaller in size than the run count**. All
  figures heavy and tabular. All labels uppercase, tiny, letter-spaced.
- **Colour / environment.** Warm off-white page, dark warm ink, one deep madder
  accent on completed states only.
- **Surface treatment.** Matte paper; the configuration panel is a slightly
  raised sheet with a soft shadow and hairline edge.
- **Major components.** Back arrow; habit name; three-slot metadata line; week
  state row (7 cells); run spine; consistency figure; configuration panel; one
  action.
- **Visual metaphor.** A column of a ledger. The week is one weft pass; the run
  is a spine.
- **Interaction state.** A habit with a healthy 12-day run, a Mon/Wed/Fri sibling
  habit elsewhere, this week partly complete.
- **Important data.** Drink water · 8 glasses/day · streak 12 · longest 19 ·
  this week 6 of 8 scheduled hit · two days **not scheduled** and therefore
  **not drawn at all** · consistency 78% (30 days) · Active.
- **Light/dark requirement.** This brief is the **Light** rendering; produce a
  Dark twin with identical content.
- **MUST be visible.** The answer "6 of 8" **larger** than the "THIS WEEK" label.
  The streak number larger than the habit name. **Two of the seven week cells
  entirely absent** — blank paper, not a dim dot, not a greyed cell. Completed
  cells ticked in the accent. Incomplete cells struck. The consistency figure
  with its window named in words.
- **MUST NOT appear.** No bar chart for the week. No fire, flame, ember or
  streak badge. No ring, orb, or circular percentage. No dim dot standing in for
  a rest day. No donut, no sparkline, no trophy, no achievement tier, no
  gamified level. No four-tile stat grid. No glass.
- **Anti-patterns to avoid.** A weekly bar chart; a "streak 🔥 12" badge; a
  generic habit-detail card stack; rounding everything into pills; an empty
  decorative chart with no axis meaning.

#### Brief A3 — Tasks

- **Android-native context.** A tab destination. Material tab grammar. Android
  list conventions.
- **Device / frame.** Same phone, scale and margins as A1.
- **Screen purpose.** Manage tasks — explicitly a management surface.
- **Exact visual hierarchy.** A tracked caption heading "**TASKS**" with a small
  count "2" at its right; a hairline; a ruled search field with a tiny tracked
  label; a filter bar with **two labelled axis groups**, each preceded by a tiny
  tracked label ("STATUS", "CATEGORY"); a list sheet of two rows separated by
  hairline rules; one flat primary action docked above the bottom navigation
  band, and the four-item navigation band below it.
- **Composition.** Flat single column. **No per-row card. No button bar beneath
  any row.** The list is one sheet with rules between rows.
- **Typography direction.** Manrope. Row names at body weight, readable. All
  axis labels tiny, uppercase, letter-spaced. Counts small.
- **Colour / environment.** Warm off-white page, dark warm ink, one madder accent
  on the selected filter only.
- **Surface treatment.** Matte paper; list sheet raised by soft shadow and
  hairline; the search field is a ruled channel, not an outlined Material box.
- **Major components.** Selvedge heading + count; ruled search field; two
  labelled filter groups; two list rows each with a single overflow control at
  the right; one docked primary action; bottom navigation band.
- **Visual metaphor.** A ledger index. Tasks are entries.
- **Interaction state.** Two tasks, one due today and not done, one due in 12
  days.
- **Important data.** "Book dentist" — One-time · due today · not done.
  "Renew passport" — One-time · due 12 Oct.
- **Light/dark requirement.** Light rendering; produce a Dark twin.
- **MUST be visible.** The word "Tasks" as the heading — **never the word
  "activities"**. Exactly one overflow control per row, not two identical
  circles. Two visibly different, labelled filter groups. A real bottom
  navigation band with four labelled destinations.
- **MUST NOT appear.** The words "No activities yet" or "Create activity". Two
  identical unlabelled circular buttons under any row. A card per row. A
  Material outlined text field with a blue label. A raw Material bottom sheet.
  A floating action button of a different material from any other screen. A
  dropdown menu rendered in default Material colours.
- **Anti-patterns to avoid.** Management controls louder than content; a
  two-row-of-pills filter with no axis labels; a search box that looks like a web
  form; an empty state with motivational filler; a "Create something worth
  showing up for" line.

#### Brief A4 — Insights

- **Android-native context.** A tab destination holding a scrollable analysis
  view. Not a desktop dashboard shrunk into a phone.
- **Device / frame.** Same phone, scale and margins as A1.
- **Screen purpose.** Answer "how consistent have I been?" — one question, one
  answer, then evidence.
- **Exact visual hierarchy.** A tiny tracked question heading; the figure
  "**78%**" at large size with a tracked caption beside it reading
  "LAST 30 DAYS"; a hairline; a seven-cell state row labelled
  "**LAST 7 DAYS**"; the seven cells in the same three-state vocabulary as
  Habit Detail, with **two cells absent** because a Mon/Wed/Fri habit was not
  called for on those days; a tracked heading "**BY CATEGORY**"; a ruled panel of
  four category rows, each with a name at body, a rate at figure size, a thin
  graduated channel, and a small "n of m complete" caption, **sorted worst
  first**; one body-size interpretation sentence; a small line for missed
  occurrences; the four-item bottom navigation band.
- **Composition.** Single column, hard left alignment, wide right margin, one
  scroll surface, no nested scrolling, no tile grid.
- **Typography direction.** Manrope. The consistency figure is the largest
  element. The 7-day rate is visibly **smaller** than it — the inverse of a
  dashboard whose biggest number is last week's.
- **Colour / environment.** Warm off-white page, dark warm ink, one madder accent
  on completed states only.
- **Surface treatment.** Matte paper; one ruled panel for the category list; one
  thin channel per row, drawn as a line, not a textured fill.
- **Major components.** Question heading; consistency figure; seven-cell state
  row; ranked category panel with four graduated channels; interpretation
  sentence; missed-count line; bottom navigation band.
- **Visual metaphor.** A page read at a glance. Density of marks = consistency.
- **Interaction state.** 30-day window; one category at zero; a sparse-schedule
  habit in the week.
- **Important data.** 78% over the last 30 days · 7-day rate with a signed delta
  against the 30-day average · two week cells absent (rest days) · one category
  at 0% · 6 missed occurrences.
- **Light/dark requirement.** Light rendering; produce a Dark twin.
- **MUST be visible.** The consistency figure as the largest element. The 7-day
  rate smaller than it. **Absent week cells for rest days** — genuinely not drawn.
  A category sorted worst-first. One sentence of plain interpretation referencing
  the user's own data. A signed delta with its window.
- **MUST NOT appear.** A 2×2 grid of equal stat tiles. A seven-bar chart with no
  axis, no values and no maximum. "Insufficient data" as a value. The word
  "Declining" or "Improving" as a bare word with no magnitude. A donut chart, a
  3D chart, a rainbow chart, a decorative chart, a sparkline, a gauge. A
  dashboard wall of widgets. A purple-blue gradient.
- **Anti-patterns to avoid.** Chart spam; a bar chart that cannot distinguish a
  miss from a rest day; a legend with no data behind it; percentages without
  windows; a "dashboard" of six identical tiles; axes with no scale.

#### Brief A5 — Onboarding (page 3)

- **Android-native context.** A full-screen first-run page of an Android app,
  with a system status bar, a page indicator, and a single primary action at the
  bottom.
- **Device / frame.** Same phone, scale and margins as A1. **No dark-only
  photographic backdrop** — this page is in the user's real environment.
- **Screen purpose.** State the one differentiator no competitor can claim.
- **Exact visual hierarchy.** A small page indicator (one of four) near the
  top-left; a large headline "**Nothing leaves this phone.**" in the light
  display weight, sentence case with a full stop; one body line "No account. No
  cloud. No tracking. No ads. Your data is in your storage and nowhere else."; a
  large area of **empty ruled page with a visible finished bottom edge and
  nothing beyond it**; a single bottom action "Continue" as a quiet ink text
  control, plus a small "Skip" affordance.
- **Composition.** Text in the upper half, the empty page occupying the lower
  half, one action at the bottom. Generous margins. Nothing centred except by
  accident.
- **Typography direction.** Manrope, same two registers as the app. Headline
  untracked and loosely leaded. The body line comfortable.
- **Colour / environment.** Warm off-white page with fibre; dark warm ink; a
  single madder accent on nothing at all, or on the page indicator.
- **Surface treatment.** Matte paper. The page's **edge** is the visual: a faint
  darker band with a hairline, suggesting the cloth simply ends.
- **Major components.** Page indicator; headline; one body line; the empty ruled
  page with its edge; one primary action; one skip control.
- **Visual metaphor.** The finished edge of a sheet. Nothing beyond it.
- **Interaction state.** Third of four pages.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads."
- **Light/dark requirement.** Produce **both**: this brief is explicitly the
  theme-aware page, and a Dark rendering must show the same page in the Dark
  environment with a visible finished edge.
- **MUST be visible.** The finished page edge with nothing past it. Short,
  specific, factual copy. One action. A page indicator.
- **MUST NOT appear.** Photography of a lake, a sunset, a mountain, a city, or
  any human face. A gradient mesh. The words "journey", "tomorrow", "unlock",
  "transform", "elevate", "seamless", "next-gen". "Your life you want". The
  adjective pair "calm and powerful". A Title-Case headline without punctuation.
  A giant abstract blob. A rating prompt. A sign-up or account field. A
  social-login button.
- **Anti-patterns to avoid.** Four cloned slides with only the icon changing;
  three identical layouts; a cinematic photo with a scrim; a four-icon feature
  row; more than two lines of copy; a website hero with a headline and two
  buttons.

---

## 6. Direction B — **Instrument** *(mass × velocity)*

### 8.1 Concept Name

**Instrument** — named for a measuring device, not for a machine. Momentum has
a literal, checkable, physical meaning and no product in this category uses it:
**momentum is mass multiplied by velocity.** An object's momentum is the product
of how much of it there is and how fast it is going, and momentum is conserved
unless a force acts on it. Press makes momentum *the record*. **Instrument makes
momentum *the arithmetic*.**

### 8.2 Core Idea

A habit is **mass**. A streak is **velocity**. Completion **adds mass**. The
product's job is to show the user the product of the two, and to let them feel
the thing that is actually true and almost never said in habit apps: **a heavy
thing moving slowly carries more momentum than a light thing moving fast.** The
seven-day streak on a small daily habit and the one-day streak on a substantial
target are not the same achievement, and the shipped app's UI cannot express that
distinction at all, because it has one accent hue, one fire glyph and one 12 sp
number.

**This is the direction that answers the fire-icon contradiction by arithmetic
rather than by removal.** A flame is a pictograph for velocity alone, in a system
that has no way to express mass. Instrument refuses the pictograph because the
mathematics is more honest, and it makes the mathematics *visible*.

**It is radical in that it replaces the product's entire expressive register**
— no warmth, no reward, no affection, no illustration, no photography, no
paper — with **instrumentation**: fixed geometry, engraved rules, a single light
source, and a set of numbers that are arranged by their physical meaning rather
than by their availability. It is the coldest of the four directions, and the
coldness is the point.

### 8.3 Emotional Character

Not "technical". Concretely: **the feeling of a well-made gauge you trust more
than your own memory.**

Three specific tests:

1. **Nothing is rounded for comfort.** Corners are tight. There is no bubble, no
   pill, no squircle. A faceplate has edges because a faceplate is a machined
   object and rounding it would be a lie about what it is.
2. **There is exactly one light source**, and every shadow agrees with it — a
   tight ambient occlusion directly under a lifted element plus a soft, wide,
   **hue-tinted** falloff, never a pure-black drop shadow. A tinted shadow on a
   cool field is what makes a machined object read as machined; a black one reads
   as a floating rectangle.
3. **The value you came for is already resolved before you finish reading it.**
   The screen opens with a number and a state, not with a greeting, a title, or
   an invitation. An instrument does not say good morning; it shows you what the
   needle says.

**The emotional risk, stated plainly:** Instrument is the direction most likely
to be misread as a **fitness app**, because instrumentation is the visual
language of the sports-and-body category, and Momentum is not a body product. It
also risks reading as **engineer-spectacle** — a device that measures well and
cares about nothing. Instrument's answer is that the device's whole purpose is to
be read by one person about their own day, and the direction's copy, its colour
budget and its chart discipline are all subordinate to that. But this is the risk
that would most plausibly sink it, and it is stated as such.

### 8.4 Interpretation of "Momentum"

**Momentum is the product of mass and velocity, and the product's job is to keep
the multiplication visible.**

This is the only direction of the four that takes the product's name as a
**quantity** rather than as a metaphor. And it does something no other direction
does: it makes the difference between two kinds of habit a *displayed,
comparable number*, which is the direct answer to the fire question. The shipped
app's streak treatment is a fire glyph and a 12 sp count, and its design
document calls streaks *"supporting information, not the emotional center"* while
rejecting *"highly gamified fire/emoji treatments"* — and then ships the fire. By
replacing the pictograph with the quantity the pictograph was standing in for,
Instrument removes the contradiction without a decision, and it *gains* the one
thing the fire glyph could never carry: a streak is only half of what makes
something hard to stop.

**The three quantities the direction makes visible:**

| Quantity | What it is in the product | Where it is shown |
|---|---|---|
| **Mass** | what the activity is worth per occurrence — a binary check, a 20-page target, a 30-minute session | a **unit** slot beside every figure: `✓`, `20 p`, `30 min`, `8 ×` |
| **Velocity** | the current run, in scheduled occurrences | a **run length** at figure scale, with a direction mark |
| **Momentum** | mass × velocity, per activity and globally | the **lead figure**, with its two factors printed beneath it as a multiplication, not as a caption |

**Where the word "momentum" appears in Instrument:** as a *label on a quantity*,
always with its factors visible, and never as encouragement. A user looking at
`12 × 5` learns what the number means; a user looking at *"Small steps,
unstoppable momentum"* learns nothing at all, and Phase 1 records that this line
fires for most of the day for an eight-habit user.

**What this costs, honestly:** mass is a *judgement*, and instrument panels do
not make judgements. Instrument's mass is derived from the activity's own
tracking configuration — a target-based activity carries its target as its mass,
a binary activity carries a unit of one — which means the mass is **a
configuration, not an achievement**, and a user who reads the multiplication
carefully will discover that the product is multiplying a number by a setting. That
is the direction's most serious conceptual liability, it is inherent to the
metaphor, and it is not solvable inside the metaphor.

### 8.5 Visual Metaphor

The metaphor is a **measuring instrument's face** — a machined plate with
engraved reference marks and a single readout — and it is applied to all six:

| Concept | Instrument's metaphor | What the user sees |
|---|---|---|
| **Progress** | a measured value against a calibrated scale | a figure at readout size, with its **scale printed beside it** (`5 OF 8`), and a machined channel that fills |
| **Consistency** | a needle between calibrated stops | a run length with an explicit **at-risk state** — a needle *near the end of the scale* is a state, not a decoration |
| **Completion** | the value registering | the readout **settling to its new value** — a discrete step, not a continuous slide, because an instrument resolves, it does not glide |
| **Streaks** | a run length in units, with a direction | a number with a **direction mark** and a break glyph where a scheduled day was missed |
| **Growth** | accumulated reading over a window | a **30-day channel** with per-day channels, all on one calibrated scale, so magnitude is comparable across days |
| **Momentum** | the product of two readings | a lead figure with its factors printed as a multiplication |

**The rest-versus-miss mapping.** Instrument cannot use absence as a mark —
an instrument always shows its full scale, because an unlit channel is an
*instrument failure*, not a zero. So Instrument distinguishes the two states by
**engraving**, which is the instrument-native equivalent of Press's structural
absence:

| State | Channel | Treatment |
|---|---|---|
| **Completed** | engraved, full | filled to the top with the accent, with a registration tick |
| **Incomplete, scheduled** | engraved, empty | the channel is present and readable, and carries a **struck diagonal** across its full height — the mark of a reading not taken |
| **Not scheduled** | **not engraved** | the channel is not machined at all. The space where the day would be is simply uncut metal |

**So: an unscheduled day is a gap in the scale, not a zero on it.** The
distinction is preserved exactly, it is a *geometric* difference rather than a
colour difference, and it is the instrument idiom's own answer to the same
question. A sparse-schedule user reading an Instrument week sees four machined
channels and three uncut gaps, and the gaps read as *not part of the scale* —
which is precisely what they are.

### 8.6 Typography

**Manrope is retained, and Instrument leans on its variable axis harder than
anything else in its own typographic system — because an instrument is a
monospace problem wearing a sans.**

The shipped font is a single variable file with one weight axis, **200–800**, and
seven named instances including a genuine **700**, of which the app registers
three (400, 500, 600). Its figures are proportional by default and
`fontFeatureSettings` occurs zero times app-wide, so every number in the product
changes width as its value changes.

**Instrument's rule: every figure in a readout is tabular, and every figure in a
*register* is proportional.** The distinction is functional, not decorative. A
figure that occupies a fixed slot must be tabular or it reflows its neighbours; a
figure embedded in a sentence must be proportional or the sentence gets holes in
it. The shipped app has neither distinction and therefore has neither property.

| Register | Size intent | Weight | Tracking | Job |
|---|---|---|---|---|
| **Readout** | the largest thing on the screen, and set with **a generous line height** so a large figure does not crowd the label beneath it | 700–800 | −0.02 em | the lead quantity, with its factors printed below |
| **Channel value** | two steps down | 700 | −0.01 em | every per-item figure, every run length, every rate |
| **Channel label** | **tracked, uppercase-ish, small** | 500–600 | **+0.10 em** | the field names and axis labels on the faceplate. This is Instrument's signature typographic move: the labels are *inscriptions*, and inscriptions are tracked and quiet, because they are cut into a surface rather than printed on paper |
| **Body** | comfortable | 400 | 0 | sentences, explanations, the interpretation line |
| **Caption** | small | 500 | **+0.04 em** | units, footnotes, window statements |

**What is deliberately different about Instrument's type:** the **labels are
tracked like engraved inscriptions and the numbers are not.** That inversion — the
quiet treatment on the words that name things, the loud treatment on the words
that measure them — is the typographic signature of the whole direction, and it
inverts the shipped scale in the useful direction: in the shipped app the most
used label in the entire product is 16 sp Regular and is therefore invisible, and
there are 29 of them.

**Numeric specificity Instrument demands and the shipped scale cannot supply:**

- The **"3 of 5"** is a readout, at readout size, larger than the date and the
  greeting and larger than the completion control.
- A **streak count is larger than the item's name** on the same row.
- **Every figure has a unit** printed beside it at caption weight, because a
  channel value without a unit is an assertion and a channel value with a unit is
  a measurement. The shipped app's most decision-relevant number — the live
  `"5/8 glasses"` on a target-based habit — is a 12 sp bare string, which is
  exactly the failure Instrument exists to fix.
- **Value and scale are visually distinguished within one figure.** `"5"` is at
  channel-value weight and `"8"` at caption weight in the same lockup, so
  "five of eight" is parseable in a glance without reading a slash.
- **On Insights, the seven-day rate is a channel value, not the hero.** The only
  figure in the shipped application set larger than 20 sp is the seven-day rate,
  while today's completion is 20 sp and a habit's live target is 12 sp — the
  product is twice as emphatic about the past week than about right now.
  Instrument inverts that by meaning: **now > this week > this month**, and the
  scale is cut for it.

**The weight, the axis, and the honest cost.** Instrument reaches the top of the
weight axis — 700 and 800 — for readouts. The design document carries a usage rule
against 800/900, which is a rule about *usage* and not about capability, and
Instrument's argument is that an instrument readout at 700 is the one place a
true Bold is load-bearing. **This is a direct, checkable claim against the locked
document and Instrument states it rather than hiding it.** Instrument's fallback
if 700 must be avoided is 600 plus a wider tracking, which is measurably weaker
and which the direction would have to say so out loud.

**What this costs:** an instrument's type is cold, and a cold type at large size
is unforgiving of bad line-breaking. Instrument therefore fixes a **hard
wrap rule**: no readout wraps, ever, and a readout that cannot fit its slot
truncates its *unit* rather than its value. Dynamic-type safety is a floor, not an
aspiration, so at 200% font scale the readout's size steps down by a stated
amount while the label steps up in weight, preserving the parse.

### 8.7 Colour / Environment

#### Q1 — the explicit answer

**In Instrument, Light and Dark are two different *physical devices*, not one
device with a brightness switch — and the difference is in the *material* and
the *light*, not in the hue.**

**LIGHT — "Printed instrument face."** A cool, faintly green-grey plate — the
colour of a printed technical drawing or an anodised light finish — with an
engraved reference grid printed into it at very low contrast, and a **matte**
surface. Depth comes from **machined edges**: a 1 px light edge on the top and
left of a raised element and a 1 px dark edge on the bottom and right, which is
how a real milled faceplate catches light. **This mechanism is unavailable in
Dark**, because a bright top-left edge on a near-black field is not a lit edge,
it is a stray line.

**DARK — "Backlit panel."** A very dark, faintly blue-neutral field, smooth, with
the raised elements **lit from within** — an inner glow at the top edge only,
which is the way a backlit membrane keypad reads. **This mechanism is
unavailable in Light**, because an inner glow on a matte light plate is not a
backlight, it is a smudge.

> **Instrument Light's depth mechanism is a machined bevelled edge, which requires
> a matte light plate. Instrument Dark's depth mechanism is an inner
> self-illumination, which requires a dark field. The layout, the scale, the
> engraving and the channel grammar are identical.**

This is the direct structural answer to the audit's finding that the shipped two
environments share all five accent hues with the Dark value simply lightened —
no hue introduced, none removed, no role changed, nothing expressed in one that
cannot be expressed in the other. Instrument's environments are the *same
instrument*, and the **materials are different alloys**.

**Accent philosophy — a single signal hue, used for exactly one thing: a
registered value.**

The shipped product is effectively one accent doing 54 call sites of global work
while four other declared accents are ambient-only or vestigial. The shipped
product also has one green carrying five unrelated meanings, so a user who learned
"green means done" misreads the ACTIVE status pill, the seven-day milestone badge
and the positive-trend marker.

**Instrument's rule is instrument-native: an accent is a reading, and a reading
is either nominal or it is not.** The accent appears in exactly four roles, and
each is a *reading* rather than a decoration:

1. a **registered value** — a channel filled to its target
2. the **at-risk marker** — a reading near the end of its scale
3. the **active navigation destination**
4. the **focus ring**

Nothing else. There is no accent on a CTA fill, no accent on a section header, no
accent on an empty-state icon tile, no accent on a stat sublabel, no accent on a
selected theme border. An accent on a button is a decoration, and decorations are
what make an instrument unreadable.

**The one tension Instrument must own, and names rather than resolves by
palette:** the locked acceptance criterion requires BUILD and REDUCE to be
visually distinct **without good/bad colour coding**, and the locked rejected list
bans green/red BUILD-vs-REDUCE. Instrument answers this **in form, not in hue**:
a BUILD activity's channel is a **single filled channel**, and a REDUCE
activity's channel is a channel with a **threshold notch and a decreasing
direction mark** — different geometry for a different job, on the same hue, in
the same slot. A REDUCE activity does not get a "bad" colour; it gets a *different
instrument reading*, which is both more correct and more legible.

**Per-activity colour.** The product persists a per-activity colour and reads it
in zero presentation code; its UI's per-item colour is a hardcoded lookup honoured
on three of five activity surfaces, so *"purple means meditation"* does not
accumulate — the association breaks the moment the user leaves the Habits tab. And
the seven item hues are one fixed set used identically in both environments, which
is a hole in the model.

**Instrument's answer: no per-activity hue, ever, in either role.** An instrument
panel has **one signal colour**; adding per-item hues makes it a mood board. Item
identity in Instrument is carried by **the item's own unit, its name, and its
position in a stable order** — the unit is genuinely distinguishing, because
`20 p` and `30 min` and `8 ×` say what kind of thing it is in a way no colour ever
did. This removes an entire colour vocabulary rather than adding a sixth, and it
is why Instrument's accent discipline is a single signal colour in four reading
roles and nothing else.

**Contrast strategy.** Body text at or above 4.5:1 in both environments,
**measured per tier per environment** and not inherited across them. The one
measured contrast datum in the repository is a **Light-specific failure of a
Dark-passing value** — 3.11:1 in Light against a 4.5:1 floor, 5.22:1 in Dark —
which is the clearest statement of the pattern that the two environments are one
design at two luminances. Instrument's environment-specific depth mechanisms
mean **there is no artwork in either environment**, so the specific condition that
produced that failure cannot recur, and Instrument treats a Dark-passing value as
**no evidence whatsoever** about Light. Engraved channel labels are the tightest
case in the direction and Instrument sets them against the **4.5:1 floor, not
against a decorative-label exemption**, because an unreadable inscription makes
the whole faceplate unreadable.

### 8.8 Surfaces

**Instrument's surface philosophy: opaque, machined, and made of exactly three
materials — plate, bezel, and engraved channel.**

| Material | What it is | What earns it | Light mechanism | Dark mechanism |
|---|---|---|---|---|
| **The plate** | the face itself, matte in Light, self-lit in Dark | the canvas, always | engraved reference grid at very low contrast | smooth dark field, no grid — a backlit panel is smooth |
| **The bezel** | a raised element: a panel, a row group, a field, a read-out block | any group of things that is one thing | **bevelled edge** — light top-left, dark bottom-right | **inner top-edge illumination** |
| **The channel** | the machined slot that a value runs in | every quantity, at every size | engraved, uncut where not applicable | engraved, uncut where not applicable |

**Why this, and why not glass.** The locked design document forbids glass as a
default treatment for every card, every row, navigation, entire screens and every
button, and names *"glass everywhere"* in its rejected list. The shipped
application makes a translucency recipe the design system's core primitive, the
default card, the default row, the navigation bar *and* the primary button, across
nine distinct fill values of which four are tokenised — and the recipe's *name*
describes a material the app does not implement, because its backdrop blur value
is zero.

**Instrument's answer is a harder one than Press's, and the difference is worth
stating. Press replaces glass with paper. Instrument replaces glass with
*engineering*.** A machined surface does not exist to be beautiful; it exists so
that two things sitting next to each other can be told apart at a glance, and
because a *tolerance* is a design value. There is no translucency in Instrument
at all. The only opacity that varies anywhere in the direction is:
(1) the engraved channel interiors, (2) the hairline bevelled edges, and
(3) the reference grid at very low contrast. All three are *machined* features,
and all three are named.

**What this buys:**

- A **real depth ladder in both environments** with **different mechanisms per
  environment**, which is the Q1 answer, structurally.
- Surface separation that **works identically in Light and Dark**, because it is
  geometric (an edge) rather than tonal.
- **A count instead of a taxonomy.** The shipped app has nine fill values
  against four tokenised, one of which a component invents by multiplying a token
  by 0.7. Instrument has **zero fill values** — plate, bezel, channel — so there
  is nothing to choose between and nothing to re-decide per screen. Phase 1's
  structural conclusion is that the app looks hand-made in twenty slightly
  different ways and none of the differences is a decision anyone made; **the
  cheapest way to break that pattern is to delete the category.**
- **A tactile specificity** that the translucent recipe can never produce,
  because a bevel is a physical fact and a 0.55-alpha white fill is not.

**What it costs, and it is not small:** a machined vocabulary is **tiring to
look at over time.** Premium products earn their coldness in small doses. A user
who opens Instrument forty times a day will find a fully-bevelled, fully-engraved
device exhausting in a way a warm paper page is not. Instrument's only defences
are (a) the direction is explicitly not trying to be a wellness product, and
(b) the engraving is *low contrast* — it is a texture, not a decoration. **If
Instrument is chosen, the first implementation decision is how much engraving
survives on a screen a user sees two hundred times a year.** That is stated here
because it is the decision most likely to be made badly by accident.

### 8.9 Cards

**The explicit answer: cards remain central, and a card becomes a *faceplate* —
a slotted, fixed-geometry panel.**

The shipped product's card situation is a governance failure with numbers: the
design system's named card has **one** production call site, six distinct card
constructions exist, and each screen assembled its own because a surface
*modifier* is not a component. The consequence is that on a section-heavy screen
a section boundary, a card boundary and a row boundary are visually identical,
which is why the shipped Settings screen reads as five undifferentiated blocks.

**Instrument's faceplate has four fixed zones and it will not reflow:**

| Zone | Content | Rule |
|---|---|---|
| **Inscription** | the field name, tracked, small, engraved | the same style in every faceplate in the product; a label is never the same size as its content |
| **Slot row** | values snapped to fixed slots | fixed height; a value that does not fit truncates its unit, never its value |
| **Channel** | a machined slot with a value in it | appears only for quantities; never for text-only content |
| **Footer** | a rule, plus at most one right-aligned action | a rule is what tells the user the group ends |

**What earns a faceplate:** a group that has an identity and a quantity. A group
that has neither gets **an inscription and a rule** — not a faceplate. This is the
direct fix for the shipped indistinguishability: an inscription on the bare plate
is a different object from a faceplate, at a glance, by structure.

**A row is not a faceplate.** This is Instrument's most consequential card
decision and it is the opposite of the shipped Tasks screen. The shipped Tasks
list puts a 48 dp card *plus* up to two unlabelled 48 dp management circles
*outside and below it* on every row, so a ten-task list is twenty boxes and the
management controls of a task are the loudest repeated element on a screen with
no completion affordance at all. In Instrument, **a list is one faceplate with
engraved rules between rows**, exactly as an instrument's scale is one surface
with graduations. Row weight drops by roughly half, and the overflow collapses to
a single three-dot control at the end of the row, which is what the shipped
Habits screen already does correctly.

**Radius: tight, and deliberately varied.** Instrument uses a small corner radius
on faceplates and a **square** radius on channels, insets and screens. Radius
stops being a uniform value, which is itself a differentiator — a system where
every corner is 16 dp is a system that has not decided what a corner is for. The
large-bubble radius is not used anywhere, because a bubble is a soft, friendly
shape and an instrument is a precise one.

**What this costs:** fixed-geometry slots are hostile to dynamic type. A faceplate
with a fixed slot row and a 200% font scale is a real engineering problem, and
Instrument's resolution — step the value size down, step the label size's weight
up, truncate the unit — is a mitigation, not a solution. Instrument states it as
a measurable risk.

### 8.10 Iconography

**The explicit answer: Instrument's iconography is *weight-flat and
quantitative*, and it deliberately requires nothing that the build does not
already have.**

The three-way conflict is factual. The **code** uses Material **Icons** through
the extended set: a static vector collection with **no weight axis**. The
**documentation** — three KDocs, the design system document, and its master ADR
— names Material **Symbols** Rounded. The **concept pack** ships Symbols Rounded
as a *webfont* with a declared weight axis 100–700. The audit's finding is that
this is **one recorded decision that was never honoured**, and that the
consequence is functional, not aesthetic: the variable-weight capability the
concept assumes is unavailable, and cannot be had without changing the icon
source.

**Instrument's position: this direction treats the icon family as a solved
operational question and declines to make it a design question.**

The reasoning is that Instrument's whole identity is carried by *quantity and
geometry* — channels, inscriptions, readouts, engraved scales — and an icon set is
a *pictographic* device. **Instrument therefore reduces the icon's role to the
minimum at which it is still useful:**

1. **No icon carries identity.** A habit is identified by its **unit**, its name,
   and its position in a stable order. `"20 p"` and `"30 min"` distinguish
   activities better than any pictograph ever did, and they do it *in text*, at
   the size the app already gets right.
2. **No icon carries a state.** Completion is a filled channel. A paused item is a
   struck rule. A missed scheduled day is a struck diagonal. Rest is an uncut gap.
   **Every one of these is geometry, and geometry is what a pictograph cannot
   be.**
3. **No icon carries a category.** Categories are inscriptions on a channel row,
   because a category in this product is a *measurement grouping*, and a
   pictograph implies a *kind of thing*.
4. **Icons remain only for structure**: back, search, a disclosure chevron, an
   overflow. A small, fixed, monochrome set, one weight, one optical density, and
   **the currently shipped Material Icons set is sufficient for all of it.**

**What this buys, stated as a real consequence:** **Instrument is the only
direction of the four that is deliverable with zero dependency on Q6.** No icon
migration, no font-format problem, no new asset, no change to the concept pack's
status, and no pressure on the offline rule. The audit records that nine
source-text visual tests assert specific implementation text, including icon
mappings; a direction that changes the icon set inherits all of them as breakage,
and Instrument's Q6 position means it inherits none.

**What it costs, and it is a real cost:** Instrument gives up the one thing
Material Symbols was chosen for. A weight axis is a *legibility* tool — the same
glyph at different optical weights — and Instrument's typographic signature is
its tracked inscriptions. An inscription set at one weight, next to tracked
caption type, will not be optically identical to it. **Instrument accepts a small
typographic dissonance between its glyphs and its type in exchange for not
depending on an unresolved question**, and it names the dissonance rather than
discovering it later.

**And one thing Instrument takes from the shipped set that it should not have:**
the fire icon. The shipped `"streak"` key resolves to a flame, tinted amber, at
12 sp — the design document rejects highly gamified fire treatments and the
app's own ADR records that the resolution to a fire glyph was a substitution of
one form of the rejected pattern for another. **Instrument removes it entirely**,
because a run is a run length and a flame has nothing to add.

### 8.11 Navigation

**Instrument's navigation model: a machined segmented rail, four destinations,
flush to the bottom, no floating.**

The four destinations are a locked decision and Instrument keeps them. The
bar's material is an open question, and Instrument answers it the way it answers
everything: **with a mechanism rather than a material.**

**The rail.** A flush-bottom plate, separated from the content by a single
engraved rule. Each destination occupies an **equal-width slot** — a real
segmented control, with the dividing rules machined between slots, which is
instrument-native and which immediately communicates *"this is four channels of
one instrument."* Within a slot: a glyph, a tracked inscription, and an
**active-state bar at the top edge of the slot** in the accent — not a filled
rounded pill, not a 0.18-alpha wash. The active bar is a *registration* on the
scale, and it is a non-colour signal because it is positional as well as
chromatic.

**Why segmented and not floating, stated as a cost:** a floating bar reads as an
overlay and leaves the content extent ambiguous; a flush segmented rail is a
region of the plate, and equal-width slots make the four destinations feel like
four channels of one device. The cost is that a segmented rail is *wider* than a
floating pill, so it consumes more horizontal room, and at 200% font scale the
inscriptions must truncate to their first three characters. Instrument states that
truncation rule as part of the navigation spec rather than discovering it in
implementation.

**Where the four destinations come from** is itself open — the Tasks-tab question
is real, because Tasks is the shared activities list filtered to tasks, it has no
completion affordance, and its empty state says "activities" on a tab labelled
"Tasks". **Instrument does not resolve the destination count**, because the
destination count is a locked decision and a visual direction does not get to
re-open it. It resolves only the *treatment*, and it does so in a way that is
compatible with either outcome.

**Row → detail.** The shipped app's dominant transition is a crossfade that is
identical forward and back, and its destination sits behind an asynchronous gate
that shows a Material spinner on **every** detail navigation in the product. The
app already contains a shared-element opportunity that is unused: an activity's
accent tile is 44 dp in a list and 72 dp on its detail page — the same component
in both places, unanimated.

**Instrument specifies that opportunity taken, and specifies it as a container
transform plus a shared element on the item's own unit and channel** — the unit
lockup and the machined channel exist in the list row at small size and become
the header's readout block and its channel at large size. **The async gate is what
makes a shared element impossible today, and Instrument specifies removing the
gate as a precondition** — because the destination's structure must be known
before the transition can begin. This is a Compose shared-transition answer, an
Android-native mechanism, not a web animation.

**Back versus forward:** forward is the container transform; back is the same
transform reversed. The shipped crossfade cannot express the difference and
Instrument specifies that it must.

**Inset contract.** One contract, in the plate's own vocabulary: **every screen is
laid out on the same fixed measurement grid with one gutter**, and the top and
bottom regions are reserved by that grid as slots of stated height rather than
per screen. So the same header role reserves the same plate on every screen, a
screen's title sits on the same channel edge as the content it titles, and no
chrome block is ever measured in against a viewport.

### 8.12 Home / Today

**Audit verdict is REIMAGINE. Instrument replaces the collapsing photographic
hero with a readout plate, and rebuilds the screen around quantity.**

**Purpose.** Answer *"what do I need to do today and how am I doing?"* without
scrolling, in both environments, at 200% font scale, in one glance.

**Hierarchy, strict, in this order:**

1. **The readout.** `"3 OF 5"` at readout size, the numerator at channel-value
   weight and the denominator at caption weight, in one lockup, with the unit
   printed beneath as an inscription: `COMPLETED TODAY`. **The largest element on
   the screen.** The shipped screen's answer to this exact question — the
   dashboard's own declared headline is *"How am I doing today?"* — is body text,
   smaller than the greeting above it and the same size as a row's metadata line.
2. **The channel.** A single machined channel, full content width, engraved,
   filled to 3/5 in the accent, with **five graduation ticks** — one per activity
   — so the channel is simultaneously a percentage and a count of five. The
   graduation ticks are what make it an instrument rather than a progress bar: the
   bar can be read either way, and a user does not have to remember which.
3. **The date and the greeting**, as a single tracked inscription line beneath the
   readout, at caption weight. **The greeting is not display type in Instrument.**
   The shipped stack is date → greeting → percentage → "3 of 5" → message, with
   priority running exactly backwards; Instrument's stack runs forwards, and it
   makes the greeting quiet deliberately, because a greeting does not change and a
   readout does.
4. **The record.** One faceplate, engraved rules between rows, one row per eligible
   activity, incomplete first in the user's order, complete below its own rule.
5. **The at-risk line.** A single line at channel-value size, present only when
   something is genuinely at risk: a streak that closes tonight. Absent when
   nothing is.
6. **No motivational message.** Instrument removes the slot entirely. Four of the
   shipped app's six message branches reference no user data, and one is the
   product's own tagline used as an everyday state line — so it says the same
   thing all day. **If there is something worth saying, the at-risk line says it
   with a number in it.**

**Composition.** A **fixed measurement grid** with no exceptions. Every element
snaps to a slot. The content extent is a single column with a fixed gutter; the
readout block and the channel are full-width; the record is a single faceplate.
There is no two-column stat row, no tile grid, no asymmetry for its own sake, and
no image. The composition's single structural idea is **one measurement, then
five channels, then the detail.**

**Primary visual.** The readout plus its graduated channel. No orb, no ring, no
glow, no photographic hero.

**What is removed and why that is a real gain:** the shipped Home hero is 771
lines with three custom draw layers, a 24-sample blend, a 48-sample veil, five
anchor constants and a weighted spacer that expands to fill 72 dp of headroom —
arrived at through eight issues and nine ADRs, with a documented history of the
same margin landing in the wrong place three separate times. The app's *own*
source calls a 222-line version on another screen the reference composition. On a
day with nothing scheduled, that machinery still runs: a roughly 300 dp
photographic surface containing a date, a greeting and nothing at all, above a card
that says nothing is scheduled. **Instrument's readout plate is 0 dp tall when the
day is empty** — it is not a decoration that scales to a void; it is a number, and
when there is no number the plate is not drawn. That is the structural fix to the
shipped empty-day defect, and it is structural rather than cosmetic because the
empty-day problem is *caused* by a hero that has a fixed visual size independent
of its content.

**Key components.** The readout lockup; the graduated channel; the record
faceplate; within a row, a **48 dp completion control** (see 8.19), the item's
name at body weight, the **unit** at caption weight, the **value** at
channel-value weight where one exists, and a **run length** with a direction mark
for the item with the longest live run.

**Data shown.** The five eligible items, three complete, at the canonical content.
The two target-based items' live progress. The run length of the item with the
longest live run. Nothing else. **No statistics, no month view, no trend, no
comparison to an average, no habit library** — the locked design document forbids
a full habit library on Home and forbids detailed analytics on Home.

**Interaction emphasis.** The completion control, then the target entry. And
**row rhythm**: the shipped row is 44 dp, 62 dp or 84 dp depending on whether it
carries a schedule label and a streak badge, so a list of cards is a ragged stack
rather than a rhythm. **Instrument reserves the metadata line** and renders an
empty slot when there is no metadata, so every row is the same height and a list
reads as a scale.

**Distinctive visual behaviour.** **The value settles; nothing slides.** When a
figure changes, the readout **resolves to its new value in one discrete step**
with a very short transition, the way a real instrument's reading settles — not
a continuous slide, and not a spring. The channel fills in the same single
resolution. This is the direction's whole motion vocabulary in one gesture, and it
is the opposite of the shipped behaviour, where completing a row makes it *glide*
to the other group over a spring.

**The day-complete moment.** The shipped app's entire day-completion celebration
is one long-press haptic plus a snackbar reading *"All done - momentum secured."*,
while a milestone state and a reward duration exist, are documented, and are
unreachable, and while the hero simultaneously displays *"Everything complete.
Rest easy."* — the same news twice within 200 px.

**Instrument's version, and it carries no expressive gesture at all:** at 100%,
the channel's five graduations each take a **registration tick**, once, together,
and a single line states the day's **mass × velocity** total. There is no stamp, no
press, no light, no particles, no toast. **A milestone is distinguishable from a
routine completion only by the count of graduations registering at once** — which
is an *instrument* way to mark an event, and the only one of the four directions
that marks an event with **no expressive gesture at all**. It also satisfies the
locked rule against routine confetti and the locked rule against emoji and fire
gamification simultaneously, and it satisfies the project requirement for
"meaningful micro-interactions" in the weakest possible way, which is stated
plainly as a cost.

**Light-mode considerations.** A cool light plate with a printed reference grid at
very low contrast. The readout is near-black at full contrast. Engraved channel
interiors are the plate's own colour, one step down. Bevelled edges: a 1 px light
edge top-left, a 1 px dark edge bottom-right. There is **no artwork**, so the
measured Light failure — secondary text at 3.11:1 over sampled hero art — cannot
recur.

**Dark-mode considerations.** A very dark faintly blue field, smooth, with no
reference grid — a backlit panel is smooth, and the grid is what makes the Dark
plate read as a *technical drawing* rather than a device. Raised elements are lit
from within by a top-edge inner glow. The readout is near-white at full contrast.
The accent at full saturation is the **only** place the screen is warm, and that
is deliberate: a signal colour on a dark field is the one thing that is allowed
to be noticed. **Contrast discipline is the risk here:** a top-edge inner glow on
a dark field reduces the contrast of whatever sits against it, so Instrument's
Dark rule is that no text is ever placed within a bezel's top-edge band.

### 8.13 Habit Experience

**Instrument's answer to the product's one correct data mark: keep the three
states, and change the *scale*.**

The shipped `DayCell` is the only component in the product that satisfies the
requirement that a Mon/Wed/Fri habit must not make Tuesday look missed:
completed is a gradient-filled 32 dp circle with a check, incomplete is a 1 dp
ring, and not scheduled is an 8 dp muted dot in a 32 dp footprint.

**It is correct and Instrument keeps its semantics. Instrument rejects its shape
on two specific grounds, and both are the same ground — it is a *dot*.**

1. **A dot cannot express a quantity, and a week is a scale.** A filled circle, a
   ring, and a dot are three values of a two-channel system with no room between
   them. A habit's live target progress is *"5 of 8"*, and a dot cannot render
   five.
2. **A dim dot is still a mark on a day the user was not meant to do anything.**
   It is *correct* — it is different from a miss — and it is still a footprint
   where nothing was scheduled. The audit's own framing is that this is the
   difference between a habit tracker that respects rest and one that manufactures
   guilt, and Instrument reads the current form as *respecting rest while still
   pointing at it*.

**Instrument's replacement — the same three states, machined:**

| State | Channel | Geometry | Hue |
|---|---|---|---|
| **Completed** | engraved, filled | filled to top in the accent, with a registration tick across the channel | accent |
| **Incomplete** (scheduled, not done) | engraved, **empty** | a **struck diagonal** across the channel's full height — a reading not taken | ink, at body contrast |
| **Not scheduled** | **not engraved** | the space is uncut. No channel exists | none |

**A rest day is a gap in the scale, not a zero on it.** An instrument always shows
its full scale, because an unlit channel is an instrument failure — so the
"missing" state *is* a missing mark, and the distinction is **geometric
engraving**, not colour. This satisfies the streak rule structurally, and it
satisfies the never-by-colour-alone floor structurally too.

**Target magnitude — the thing a dot cannot do.** For a target-based activity, the
channel is **graduated**: eight divisions, five filled, three empty, and the value
printed. **This is only possible if per-day values are in scope**, and the audit
records that they currently are not: the habit-overview use case takes a set of
completion *dates* and discards each record's value at the use-case boundary, so a
"drink 8 glasses" habit's page cannot currently show that you averaged 6.2.

**Instrument states this as an explicit dependency rather than assuming it:**
> Instrument's graduated channel requires per-day completion values to reach the
> presentation layer. Phase 1 established that they do not — the value is
> discarded at the use-case boundary. **This is a data-shape question before it
> is a design question**, it is recorded in Phase 1 as an open design question,
> and Instrument's graduated channel is contingent on it. The binary week works
> today, with no data change, and is what the five briefs below depict. The
> graduated channel is what makes the habit page worth having.

Instrument does not propose the data change, and this document does not have the
authority to. It records the dependency.

**Streak.** A **run length at channel-value size** — larger than the item's name
on a list card, which the shipped app gets backwards — beside a **run channel**
with one graduation per contiguous scheduled occurrence and a **visible
break glyph** where a scheduled day was missed. The break is honest: the run
genuinely ended, and Instrument shows exactly where rather than printing a bare
zero. Current is the live value; longest is one tier down, because current is
read and longest is archived.

**The multiplication — Instrument's signature on the habit page.** Beneath the
run, the page prints the habit's own product: **mass × velocity**, with the mass
printed as the activity's unit and the velocity as the run length, and the result
at readout-adjacent size. On the canonical content: *Read 20 pages* reads
`20 p × 0 = 0`; *Drink water* reads `8 × × 12`. **The zero is the point.** A
broken run is a hard zero, and Instrument refuses to display a soft number over
it.

**Consistency.** Printed, because the engine computes a consistency score
documented as the percentage of scheduled occurrences hit over the last 30 days
and it has **zero** presentation call sites. For a product named Momentum, the
number that says whether the user is consistent is never shown anywhere. Along
with it, the **missed-occurrence count**, also computed and also invisible, which
is what would make a month view mean anything.

**Feedback.** A resolution, not a bounce. The channel fills, the value steps, one
short haptic. No scale pop, no spring, no glow — a bouncing instrument is a
broken instrument.

### 8.14 Tasks

**Instrument resolves the Tasks problem by taking the surface at its word.**

The shipped Tasks tab is the shared activities list filtered to tasks. It says
*"No activities yet"* on an empty Tasks tab and offers *"Create activity"*; the
FAB says *"Add activity"*; the sheet says *"Create item"*; the sheet's options say
*"Habit"* and *"Task"*. **Four names for one action, across three surfaces, and
the word "activities" on a tab labelled "Tasks".** Every row additionally carries
up to two unlabelled 48 dp management circles *below* and *outside* the card, so
a ten-task list is twenty boxes, the management controls are the loudest repeated
element on the screen, and **two of those circles are visually identical to each
other**, distinguished only by glyph.

**Instrument's four moves:**

1. **One noun per surface.** The tab is Tasks, the screen says Tasks, the action
   says *New task*. The shared surface's vocabulary does not leak through a
   filter.
2. **One management model, matching the one the product already does correctly.**
   The shipped Habits screen uses a per-row overflow with status-appropriate
   actions and a confirmation naming permanence; the shipped Tasks screen uses a
   bar of two identical circles. **Instrument gives Tasks the Habits model**,
   which collapses the row weight by roughly half and eliminates the pair of
   identical buttons. The audit records this as a *product* inconsistency rather
   than a spec violation — the two screens were specified separately — so
   Instrument is choosing one of two already-specified behaviours, and choosing
   the one the product already ships correctly elsewhere.
3. **The row is the same object as the Home row.** Habits and tasks share a
   visual grammar by locked decision, and in Instrument they share it *literally*:
   the same engraved row, the same name weight, the same unit slot, the same
   channel. What differs is **which slots are filled** — a task row carries
   recurrence, category and status, a Home row carries today's target progress —
   and that is a *data* difference expressed by an empty slot, not by a different
   component. The shipped mechanism of suppressing the streak badge for tasks
   becomes, in Instrument, simply an unengraved slot.
4. **A ruled search field, not an outlined Material box.** The app's only text
   field leaves its unfocused and focused text colours, its label colour and its
   disabled text colour to Material defaults, drawn from a palette this product
   does not use. Instrument's field is a **bezel with an engraved channel
   interior and an inscription label**, and its text colour is a token.

**Filters.** The shipped screen stacks two **visually identical rows of pills**
— status, then category — with no label distinguishing the axes, and it is the
only screen with a filter surface, on a component the Habits specification
explicitly denied to the Habits list. Instrument's answer is **one filter bar,
with an axis inscription per group**, so two adjacent groups can never read as one
unlabelled pair, and a filter is never mistakable for a setting — which the
shipped app does not achieve on Settings either, where the same pill component
serves a filter and a setting with no visual distinction.

**The empty state.** *"No tasks."* + one line, in body, that says something true:
*"One-time or repeating. They show up on Home when they are due."* + one action.
Not *"Create something worth showing up for."* — which is the only unhedged
motivational line in a list context in the entire product, and which names nothing
about the user.

**The one thing Instrument will not do:** it will not add a completion control to
the Tasks tab. The tab has no completion affordance today, and the temptation is
to fix that by making Tasks an action surface. **Tasks is a management surface**
— the architecture already implies it, because management belongs on Habits and
action belongs on Home — and Instrument makes it look like one instead of
pretending otherwise.

### 8.15 Insights

**Instrument's Insights is the one place where a faceplate is genuinely the right
object, and it is also the place the shipped screen is most concretely
diagnosable.**

What the shipped screen does: it loads **60 days** of completion history and
draws **7 bars**. The only figure in the whole application set larger than 20 sp
is the **seven-day rate**, while today's completion is 20 sp with an
out-of-family weight, the streak count is 12 sp, a habit's live target is 12 sp,
the calendar's own summary is 12 sp, and the habit page's week is 13 sp.
**The product is twice as emphatic about the past week than about right now.**
Four different data-mark vocabularies appear on this one screen — a canvas of
seven fully-rounded bars with no values, no axis, no maximum and no gridline; a 6
dp rounded linear bar; a stat tile with no track, no scale, no rank; and a
percentage with no mark at all. The two lists on the screen are the two places
where a ranking is the entire value, and **neither is sorted** — categories come
out in map-iteration order, and the section named "most consistent" shows only
the best performers with no contrast against the worst. The trend is a bare word
with no magnitude and no window, and its insufficient-data case renders the
literal string *"Insufficient data"*.

**Instrument's structure — one instrument, four calibrated bands:**

1. **Band one — the primary channel.** The **consistency score** at readout size,
   with its **window printed as an inscription beside it** (`LAST 30 DAYS`), and
   the channel **graduated to 30 divisions** so the score is legible as *"23 of 30
   hit"* rather than as a bare percentage. This is the number the engine already
   computes, the number the product specification names, and the number with
   **zero** presentation call sites.
2. **Band two — the comparison channel.** The seven-day rate as a channel value,
   with the **delta against the 30-day average as a signed channel value** and a
   **direction mark**. The shipped screen has this one comparison cue and it is
   genuinely good; Instrument keeps it and gives it a weight, a mark and a
   window. **It is a *secondary* readout here**, which is the inversion the
   shipped screen needs.
3. **Band three — the 30-day channel strip.** **Thirty engraved channels, one per
   day, all on one calibrated scale**, each in the habit-week vocabulary —
   completed, incomplete, not scheduled, missed. This is the direction's answer to
   the shipped defect that the screen reads two months of history and draws one
   week of it: **Instrument shows all thirty, and shows them in the same mark the
   habit page uses.** The `DayRate` record already carries a `scheduled` count
   that the shipped weekly bars discard, so the distinction between "I did
   nothing" and "I had nothing to do" is **available in the data today and simply
   not drawn**. A user with a Mon/Wed/Fri activity sees uncut gaps on Tuesdays,
   Thursdays and weekends, and struck diagonals only where a scheduled day was
   genuinely missed.
4. **Band four — the ranked channels.** By category, **sorted, worst first when a
   category is at zero** — because the value of a ranking is comparative and
   unsortedness throws that value away. Each row: category inscription, rate as a
   channel value, a graduated channel drawn with the progress-track token rather
   than one of the three different surface tokens the shipped app uses for one
   job, and *"n of m complete"* as a caption.

**Interpretation as a sentence.** One line, in body, generated from the user's own
data: *"Six of eight scheduled evenings hit. Tuesdays are the ones you skip."*
This is the specific, data-referencing interpretation the shipped screen's delta
line gestures at and does not deliver.

**The trend, printed properly.** The shipped app surfaces a four-value trend enum
as a lowercased string, so the user can literally read *"Insufficient data"* as
the value of a row labelled "Trend". **Instrument prints `—` for insufficient
data, and a signed channel value with a direction mark and a window otherwise.**
No enum name ever reaches the user.

**Missed occurrences, printed** — computed, tested, and invisible today.

**What Instrument will not do:** a dashboard wall. The shipped screen's 2×2 grid
of four equal stat tiles is the reason it fails the locked acceptance criterion
that Insights must feel analytical without becoming a dashboard wall. One
instrument, four bands, one mark used at every size. No decorative charts, per the
locked rejected list.

### 8.16 Onboarding

**Audit verdict is REIMAGINE. Instrument's onboarding argues the product's
arithmetic, and it is the only direction of the four whose first-run case is
*calibration* rather than *mood*.**

**The conceptual argument, in four beats.** The shipped onboarding's four bodies
contain **zero** facts about what the app does — no Activity concept, no streaks,
no progress as a mechanic, no offline operation, no on-device storage, no account,
no cloud, no tracking — while the product's best-written line, *"Everything stays
on this device. No account, no cloud, no tracking — offline by design,"* sits in
Settings behind an icon. The app describes itself in the third person inside its
own UI, calls itself *"a calm and powerful app"*, offers *"create the life you
want"* and *"your journey to a brighter tomorrow starts now"*, and one of its
four headlines is the only one in title case without terminal punctuation.

**Page 1 — the two quantities.** Headline: **"Momentum is mass times velocity."**
One line: *"The size of the thing, and how long you have kept it going."* Visual:
a faceplate with two channels side by side, one wide, one long, and the readout
empty. **This page teaches the arithmetic before it asks for anything**, and it is
the only direction in this document whose onboarding teaches a mechanic in the
first three seconds.

**Page 2 — the one idea.** Headline: **"Habits, tasks, medicine, practice. One
kind of thing, tracked."** One line naming the Activity concept, with a short
list of what a person actually tracks. Visual: the same plate, now with five
channels — four registered, one struck, one uncut gap. **The uncut gap is
introduced here**, in the first-run context, so that a Mon/Wed/Fri user learns on
day one what a rest day looks like.

**Page 3 — the difference nobody else can claim.** Headline: **"Nothing leaves
this phone."** One line: *"No account. No cloud. No tracking. No ads."* Visual: the
plate with **no channel running off its edge** — the scale simply stops, and
nothing continues beyond it. This is the page the shipped app does not have, and
it is the page that wins the user who is choosing between habit apps.

**Page 4 — the day.** Headline: **"Tomorrow, this is the top of the screen."** One
line, and a single action, *"Start"*. Visual: the readout resolved, with the
record beneath it, on the real plate.

**The theme re-threading — the same structural move as Press, reached
independently.** The shipped onboarding is theme-blind: **zero** reads of the
theme system in its files, four photographs with no Light variant, its own
14-constant palette, its own ink, and a `DisposableEffect` that force-sets the
light status-bar appearance and restores it on dispose — code that is correct and
exists **precisely because onboarding does not know what theme it is in.** A
first-run user who chose Light walks through four near-black photographic screens
and arrives at a light cool-grey app. The first thing the product shows and the
thing it becomes are two different visual products.

**Instrument removes the photography, and the theme-blindness goes with it.**
There is no art to recolour, no palette to reconcile, no Light variant to derive
and no system-bar hand-patch — because onboarding is now simply the first four
screens of the app, in the app's plate and ink, in **whichever environment the
user is actually in.** The vector illustration sources that were flattened into
photographs still sit in the repository, unused; Instrument's argument is that a
faceplate needs no illustration, and that needing none is what lets onboarding
survive a theme change.

### 8.17 Motion

**Instrument's motion personality: things resolve. Nothing breathes, drifts,
orbits, spins, glows, or runs on a wall clock.**

The shipped app runs 31 animation call sites across 9 files. The one motion token
it uses consistently is a 6-second ambient value whose sole purpose is two
decorative infinite loops. Six of the nine motion tokens have **zero** production
call sites; 16 hand-written durations have replaced them; 8 raw springs have
replaced the two spring specs, and **none of those springs sets a damping ratio**,
so all of them take a library default — which is the difference between *"someone
chose this feel"* and *"whatever the default was"*. Two of those infinite loops
run on the primary screen whenever any progress exists, for a number that changes
a few times a day, and the app's own ambient-field KDoc calls the system
*"state-responsive, not decorative wallpaper"* while the field has no state
parameter at all.

**Instrument's complete motion inventory:**

| Motion | Trigger | Duration intent | Under reduced motion |
|---|---|---|---|
| **Value resolution** | any figure changes | **very short, 100–150 ms, discrete, non-spring** — the reading settles to its new value | the value changes instantly |
| **Channel fill** | a quantity's value changes | 150–200 ms, linear-ish, no overshoot | instant |
| **Registration tick** | a single item completes | 120–180 ms | the tick appears with no stroke animation |
| **Graduation registration** (all ticks at once) | the **day** reaches 100% — a milestone | 250–350 ms, once | a single static change; no sweep |
| **Run extension** | a run passes its own previous length | 200–250 ms | instant |
| **Row relocate** | a row moves between the incomplete and complete groups | 180–220 ms, transform only | instant relocation |
| **Page turn** | pushing a destination | container transform, 220–280 ms | no transform |
| **Back** | popping | the same transform reversed | no transform |
| **Continuous ambient** | **none. Instrument has none.** | — | — |

**Two specific things Instrument removes, both of which are real costs it names:**

1. **The shipped `CompletionButton` has a spring pop and a spring scale, and
   Instrument keeps the spring on the completion control only** — because it is
   the one control the audit singles out as the product's only genuinely
   well-behaved stateful control, where the visual state, the semantic role, the
   accessibility label, the haptic and the motion all agree, and where the label
   was written as a *promise about behaviour* rather than a description of a
   shape. **Instrument preserves that contract and preserves the pop**, and
   removes springs from everything else. It also preserves the control's
   accessibility contract explicitly, because the audit's process recommendation
   holds that contract constant across directions.
2. **The permanent 6-second loops are removed**, which the audit identifies as a
   standing cost on the primary screen. Instrument's removal is on a stronger
   footing than Press's, because Instrument's whole premise is **a value that is
   read, and a value that is not changing is not a reading.**

**The day-complete moment in motion.** At 100%, the five graduations register
together in one 250–350 ms resolution. There is no stamp, no press, no light, no
particle, no sound, no toast. **Instrument's day-complete moment is deliberately
restrained**, and it is a reading of the locked requirement that completion feel
*"rewarding but restrained"* — **but Instrument must also own the other side of
that requirement**, which asks for *"streak celebration"* micro-interactions.
A registration sweep is a celebration in the same sense a bar graph is a
celebration: it is a *signal*, and a user who needs to be told they did something
significant may not experience it as one. That is the direction's most
substantive risk and it is carried into 8.20.

**Reduced motion.** Every entry has a defined non-animated behaviour and every
behaviour is a **substitution, not a shortening** — which the shipped app already
does correctly in the two places it honours the flag, by switching to a zero
duration or nulling the spec rather than halving a duration. Instrument extends
that pattern to its whole inventory, which is short. **And the claim is written
honestly:** the shipped app's Settings copy promises *"Calm animations
throughout the app"* while the setting reaches **1 of 14** call sites, so a user
who needs reduced motion is told it was applied and it was not — the most
consequential single defect Phase 1 identified, and a truthfulness failure rather
than an aesthetic one. **Instrument's motion inventory is small enough that
making the claim true is tractable, and Instrument states the claim only if it is
true.**

### 8.18 Light / Dark

Described **independently**, because they are two different devices.

**LIGHT — "Printed instrument face."**
A cool, faintly green-grey plate, matte, with a **reference grid printed into it**
at very low contrast — the grid is what makes this environment read as a
*technical drawing* rather than as an empty page, and it is the one place in
Instrument where texture carries meaning rather than mood. Depth comes from
**machined bevelled edges**: a 1 px light edge on the top and left of every raised
element, a 1 px dark edge on the bottom and right. Raised elements cast **no
shadow at all** — a bevelled plate does not float, it is *milled from the same
sheet*, and adding a drop shadow would be a category error. The tonal ladder is
five genuinely distinct values, because the mechanism is a *tonal ladder plus
edges* rather than a collapsed pair of identical whites. The readout is near-black
at full contrast. The accent appears in four roles only. The engraved channel
interior is one step down from the plate. There is **no artwork anywhere in
Light**, which makes the measured Light contrast failure structurally impossible
to reproduce.

**DARK — "Backlit panel."**
A very dark, faintly blue-neutral field, and — critically — **smooth, with no
reference grid at all.** The grid is a printed artefact; a backlit membrane panel
has no print on it. This single removal is the most visible difference between
Instrument's two environments and it is not a colour difference. Depth comes from
**inner top-edge self-illumination** on raised elements — an inner glow confined
to the top edge, the way a lit membrane keypad reads — and from a wide, soft,
**hue-tinted** falloff into the field. The readout is near-white at full contrast.
The accent at full saturation is the only warm thing on the screen. The tonal
ladder runs the opposite way from Light's: field, falloff, element, raised region.
The engraved channel interior is *darker* than the field, not lighter — because an
uncut channel on a backlit panel is a hole, and a hole is dark.

> **Mechanism statement, made explicitly because Q1 requires it:** Instrument
> Light's depth mechanism is a **milled bevel**, which requires a matte light
> plate — a bevel's bright edge only exists against a surface brighter than the
> highlight. Instrument Dark's depth mechanism is **inner self-illumination**,
> which requires a dark field — a glow on a light plate is a smudge. The layout,
> the scale, the engraving, the channel grammar and the composition are
> identical between them. **The two are the same instrument built from different
> alloys, not one design with a brightness switch.**

The locked acceptance criterion that the two environments feel like the same
product is satisfied by construction. And the audit's specific finding — that the
shipped environments share all five accent hues with the Dark value simply
lightened, introducing and removing nothing, so nothing is expressed in Light that
cannot be expressed in Dark — is corrected here **structurally**: Instrument's two
environments do not merely re-value the same roles, they express **different
mechanisms**, so a role that exists in one has a real counterpart in the other
rather than a brightened copy.

**The Dark constraint Instrument must state honestly:** an inner top-edge glow
reduces the contrast of anything placed against that edge. Instrument's rule is
therefore that **no text is ever placed within a raised element's top-edge band**,
and that all inscriptions sit on the flat plate, not on a bezel. This is a
layout rule, and it is the kind of rule that a rendered image is needed to check.

### 8.19 Signature Element

**The multiplication.**

Every figure in Instrument prints its **factors**. `5 OF 8` prints its own
denominator in a different weight. A streak prints its run length. The lead
figure prints `mass × velocity` as an actual multiplication, with each factor
named and each value labelled with its unit. And on the habit page, a broken run
prints a **hard zero**, not a softened one.

**Why this is the signature.** It is the only element that could exist in no other
direction, because it is the only element that *is* the product's name. It is what
makes the phrase *"Momentum"* mean something measurable rather than something
encouraging. It is also, quietly, the answer to a product-level contradiction: the
app is named Momentum, its own documentation rejects fire-streak gamification, and
it ships a fire icon — because a flame can express *velocity* and the product had
no way to express *mass*. **The multiplication expresses both, and the flame was
only ever standing in for half the idea.**

**The secondary signature — an uncut gap.** A rest day is not a zero on a scale;
it is a space where the scale was never machined. In an instrument idiom this is
the most honest possible rendering of *"this day was never part of the
measurement"*, and it is why a Mon/Wed/Fri user can read a week without being
made to feel anything about the four days they were not meant to do anything.

### 8.20 Risks

Honest failure modes, stated as failure modes.

1. **Instrument will be misread as a fitness app.** Instrumentation is the visual
   language of the body-and-sports category, and Momentum is not a body product.
   A user could see a gauge and assume the app is about their body. This is the
   risk most likely to be realised, and no amount of internal quality removes it —
   the category association is external to the design.
2. **The mass half of the metaphor is a configuration, not an achievement.** A
   habit's mass is its target as the user set it, so the multiplication is
   *a number times a setting*. A user who reads it carefully will find that the
   product is multiplying a measurement by a preference. This is inherent to
   `mass × velocity` and is **not solvable inside the metaphor** — the only
   escapes are a different metaphor or a different definition of mass, and both
   abandon the direction's central idea.
3. **Machined vocabulary is tiring over time.** Premium products earn coldness in
   small doses. A user who opens Instrument forty times a day will find a fully
   bevelled, fully engraved device exhausting in a way a warm page is not. The
   first implementation decision Instrument would face is how much engraving
   survives, and getting that wrong makes the direction feel like a CAD tool.
4. **Fixed-geometry slots fight dynamic type.** A faceplate with fixed slots at
   200% font scale is a real problem, and Instrument's mitigation — step the value
   size down, step the label's weight up, truncate the unit — is a mitigation, not
   a solution. The accessibility floor is non-negotiable, so this is a hard
   constraint Instrument has to live inside rather than something it can defer.
5. **A 100% day registers five graduations and nothing else.** The audit
   records that both the locked design document's "no routine confetti" and the
   project requirement for "streak celebration" can only both hold if the
   celebration is earned by a *milestone* rather than by a *completion*. Instrument
   takes that reading to its most austere conclusion: a milestone and a routine
   completion are distinguished **only by whether five things register at once or
   one**. A user who needs to be told they did something significant may not
   experience a sweep as being told. **This is the direction's most serious
   product risk** and it is the one most likely to make Instrument feel cold
   rather than precise.
6. **The daily Dark readability risk is a real, checkable rule that nobody has
   checked.** Instrument's Dark depth mechanism is an inner top-edge glow, and its
   rule is that no text sits within that band. This is a layout constraint stated
   in advance precisely because it is the kind of thing that is only discovered by
   rendering, and no rendered image exists.
7. **Q6 is sidestepped, not answered, and that is a real weakness of the argument.**
   Instrument's position is that the icon family is not a design question. That is
   defensible and it is convenient, and the audit's framing of Q6 includes a second
   sub-question that Instrument's minimal-icon position genuinely does resolve —
   *is habit identity carried by a custom family, a treatment of borrowed glyphs,
   or something that is not an icon at all?* — while leaving the first
   sub-question, *which family*, untouched. **Instrument's Q6 answer is
   deliberately partial, and a reader should weigh it as such.**
8. **The graduated channel depends on per-day values that do not currently reach
   the presentation layer.** The habit-overview use case discards each completion
   record's value at its boundary, so a "drink 8 glasses" habit's page cannot
   currently show that you averaged 6.2. Instrument's most distinctive habit-page
   element is therefore **contingent on a data question this document cannot
   answer**, and the binary week is what actually works today.
9. **Instrument removes the app's entire warmth budget and gives nothing back.**
   There is no photography, no illustration, no paper, no hand, no warmth
   anywhere in the direction. The product's own specification describes it as
   *"premium, playful, calming"* — **Instrument is premium and calibrated and is
   neither playful nor calming.** That is a stated departure from the product's
   own words, and a reader should know that Instrument is arguing with the product
   specification's adjective list on purpose.

**The single unknown Instrument most needs, and the branches:**

> **Unknown: whether "mass × velocity" reads as a measurement or as a
> pretension.**
> *If it reads as a measurement* — Instrument gains the only direction of the four
> with a mechanically defensible reason to own the product's name, and the
> Q5/Q6/analytics questions all resolve in its favour because everything becomes a
> quantity.
> *If it reads as a pretension* — the multiplication becomes the most visible thing
> on the screen and it is a number times a setting, and the direction's remaining
> virtues — discipline, hierarchy, a real mark vocabulary, a clean accent
> discipline — survive in a product nobody wants to open. **Neither branch
> requires different screens. Both require a rendered image to choose between**,
> which is Phase 2B's work.

### 8.21 Instrument — the five screens, as visual concepts

Same content, same fixture (§4.6). Tuesday 29 September, five eligible, three
complete.

#### Screen 1 — Home / Today

- **Purpose.** Answer *"what must I do today, and how am I doing?"* without
  scrolling, in both environments.
- **Hierarchy.** (1) The readout "3 / 5", numerator at channel-value weight,
  denominator at caption. (2) The graduated channel with five ticks. (3) A
  tracked inscription line carrying the date — **the greeting is not display
  type**. (4) The record. (5) The at-risk line. No motivational slot.
- **Composition.** A fixed measurement grid, single column, one hard gutter,
  everything snapped to a slot. No images, no second column, no tile grid.
- **Primary visual.** The readout lockup plus its five-tick channel.
- **Key components.** Readout lockup; graduated channel; record faceplate with
  engraved rules; a 48 dp completion control per row; a unit inscription and a
  channel value per row; a run length with a direction mark.
- **Data shown.** Five eligible, three complete; "5 / 8 ×" and "0 / 20 p";
  run lengths 3, 1, 6, 12, 0.
- **Interaction emphasis.** The completion control; the target entry; a reserved
  metadata slot so every row is the same height.
- **Distinctive behaviour.** **Values resolve, they do not slide.** A figure
  changes in one discrete step with a very short, non-spring transition. A
  completing row does not glide between groups; it relocates.
- **Light considerations.** Cool light plate with a printed reference grid at
  very low contrast; machined bevelled edges; near-black readout; the accent only
  for registered values.
- **Dark considerations.** Very dark faintly blue field, smooth, **no reference
  grid**; inner top-edge illumination on raised elements; the accent at full
  saturation as the only warm thing. No text within a bezel's top-edge band.

#### Screen 2 — Habit Detail (Drink water)

- **Purpose.** One habit's configuration, week, run, and — the direction's
  signature — its **multiplication**.
- **Hierarchy.** (1) The habit's name. (2) The multiplication: `8 × × 12` with
  the factors labelled, at readout-adjacent size, with the result beneath. (3) The
  week channel strip, five engraved channels and two uncut gaps. (4) The run
  channel with a break glyph. (5) The consistency channel, graduated to 30. (6) A
  slotted configuration faceplate.
- **Composition.** Column; channels full content width; faceplate fixed-height
  with values in slots.
- **Primary visual.** The multiplication.
- **Key components.** Multiplication lockup; seven-position week channel; run
  channel with break; 30-division consistency channel; three fixed-slot
  configuration rows.
- **Data shown.** `8 × × 12` · week: 4 channels tight, 1 loose, **2 uncut** ·
  longest run 19 · consistency 78% (30 of 30 scheduled hit) · missed 6.
- **Interaction emphasis.** The window control. Everything else is read-only.
- **Distinctive behaviour.** A broken run **prints a hard zero**: `20 p × 0 = 0`.
  Instrument refuses a softened number.
- **Light considerations.** Engraved channel interiors one step down from the
  plate; inscriptions tracked and set at the 4.5:1 floor, not a decorative-label
  exemption.
- **Dark considerations.** Channel interiors **darker** than the field, because an
  uncut channel on a backlit panel is a hole. No text in a top-edge glow band.

#### Screen 3 — Tasks

- **Purpose.** Manage tasks, as a management surface.
- **Hierarchy.** (1) Inscription "TASKS" with a count. (2) A bezel search field
  with an engraved interior. (3) **One filter bar with two labelled axis
  groups.** (4) A single faceplate with engraved rules between rows. (5) A
  bottom-anchored primary action. (6) The four-slot segmented rail.
- **Composition.** Fixed grid, single column, no per-row card, no button bar.
- **Primary visual.** The faceplate and its engraved rules.
- **Key components.** Inscription + count; bezel field; two labelled filter
  groups; two rows each with a single overflow; a docked action; a segmented
  rail with four equal slots and machined dividers.
- **Data shown.** "Book dentist" — once · due today · not done. "Renew passport"
  — once · due 12 Oct. A category axis including a zero-rate category.
- **Interaction emphasis.** Overflow → Pause/Delete with an **opaque** faceplate
  confirmation carrying an explicit scrim.
- **Distinctive behaviour.** None. Management is still.
- **Light considerations.** Plate, bezel, channel. No accent except the selected
  filter and the active rail slot.
- **Dark considerations.** Inner-lit bezels; the active slot shows a top-edge
  registration bar in the accent — positional *and* chromatic, so state is never
  colour alone.

#### Screen 4 — Insights

- **Purpose.** Answer *"how consistent have you been?"* as a measurement.
- **Hierarchy.** (1) Inscription, in question form. (2) Band one: the
  **30-division consistency channel** with the score at readout size and its
  window. (3) Band two: the seven-day rate as a channel value with a signed delta
  and a direction mark. (4) Band three: **thirty channels**, one per day, in the
  habit-week vocabulary, with uncut gaps. (5) Band four: the ranked category
  channels, worst first. (6) One interpretation sentence.
- **Composition.** Four horizontal bands on one instrument; every band full
  content width; no tile grid, no nested scrolling.
- **Primary visual.** The thirty-channel strip.
- **Key components.** Inscription question; 30-division channel; seven-day
  comparison band; 30-channel day strip; four ranked category channels; the
  interpretation sentence.
- **Data shown.** 78% · 23 of 30 scheduled hit · seven-day rate with signed
  delta · 30 channels including **six uncut** days from a Mon/Wed/Fri habit and
  **three struck** genuinely missed days · one category at 0% · missed 6.
- **Interaction emphasis.** The window control.
- **Distinctive behaviour.** None.
- **Light considerations.** Engraved channels; the 30-day strip prints at a
  resolution where a cut, a loose channel and an uncut gap are all clearly
  distinct.
- **Dark considerations.** As Home Dark; the strip's distinction is carried by
  geometry, not by glow.

#### Screen 5 — Onboarding (page 3)

- **Purpose.** State the differentiator, in the product's arithmetic.
- **Hierarchy.** (1) Page indicator. (2) Headline "**Nothing leaves this
  phone.**" (3) One body line. (4) A plate whose scale **simply stops** — no
  channel running off the edge. (5) One action, one skip.
- **Composition.** Upper-half text, lower-half plate with a terminating scale,
  one bottom action.
- **Typography direction.** Manrope. Headline at the Cloth weight; the
  inscriptions tracked like engraved cuts.
- **Colour / environment.** Cool light plate with the printed reference grid, or
  the dark backlit panel — **the onboarding is in the user's real environment.**
- **Surface treatment.** Matte plate; the scale's end is a visible machined stop.
- **Major components.** Page indicator; headline; body line; the terminating
  scale; one action; one skip.
- **Visual metaphor.** An instrument whose measurement range ends at the device.
- **Interaction state.** Third of four.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads."
- **Light considerations.** Bevelled plate, printed grid.
- **Dark considerations.** Backlit panel, smooth, self-illuminated edges.

### 8.22 Instrument — image-generation briefs

#### Brief B1 — Home / Today

- **Android-native context.** Premium Android instrumentation-style app's primary
  screen. Material tab grammar. An app, not a CAD viewport.
- **Device / frame.** One Android phone, portrait, centred, slim even bezel,
  even canvas margins, content-first. Identical frame across B1–B5.
- **Screen purpose.** Read the day's state without scrolling.
- **Exact visual hierarchy.** Top: a tiny tracked inscription carrying
  "TUESDAY 29 SEPTEMBER" and **no display-sized greeting**. Centre-top: a large
  readout "**3 / 5**", the 3 heavy and large, the "/ 5" smaller and lighter.
  Directly beneath: a full-width **machined channel with five graduation ticks**,
  filled to three, the remaining two engraved and empty. Beneath: the
  inscription "COMPLETED TODAY". Then a hairline. Then a raised faceplate of
  five rows separated by engraved rules; each row = a 48 dp circular control at
  the left, a name, a unit inscription, a heavy channel value where one exists,
  and at the right a run length with a small direction mark. Bottom: a flush
  **four-slot segmented rail** with machined dividers and an accent registration
  bar over the active slot.
- **Composition.** Fixed measurement grid, single column, one hard gutter,
  everything in a slot. No imagery of any kind.
- **Typography direction.** Manrope. The readout is the largest element by a wide
  margin. Inscriptions are tiny, uppercase and visibly letter-spaced — cut into
  the plate. Every figure heavy and tabular. Value and scale in a row differ in
  weight within one lockup.
- **Colour / environment.** Light: cool, faintly green-grey matte plate with a
  very low-contrast printed reference grid; near-black readout; one signal accent
  (a clear functional blue or a hard orange — **not** purple-blue gradient) used
  only for registered values and the active rail slot.
- **Surface treatment.** Opaque, machined. Raised elements are **bevelled** — a
  1 px light edge top-left, a 1 px dark edge bottom-right — and cast **no drop
  shadow**. The channel is a real engraved slot.
- **Major components.** Readout lockup; five-tick graduated channel; inscription;
  record faceplate with engraved rules; 48 dp controls; unit inscriptions;
  channel values; segmented four-slot rail.
- **Visual metaphor.** A measuring instrument's readout. "3 of 5" is a reading on
  a calibrated scale.
- **Interaction state.** Mid-day; three of five registered; two pending; one
  channel partly filled at 5 of 8.
- **Important data.** 3 / 5 · Morning run, Weekly review, Take the stairs
  (registered) · Drink water 5 / 8 × · Read 20 pages 0 / 20 p · run 12 ·
  Tuesday 29 September.
- **Light/dark requirement.** This brief is the **Light** rendering. Produce a
  Dark twin: very dark faintly blue field, **no printed grid**, inner top-edge
  illumination on raised elements, the accent at full saturation.
- **MUST be visible.** The readout as the largest element; the greeting *absent*
  or clearly subordinate; five graduation ticks on the channel; engraved rules
  between rows; the value/scale weight contrast inside "5 / 8"; a four-slot
  segmented rail with a positional active marker; every control at a full 48 dp.
- **MUST NOT appear.** Any photograph, illustration, gradient, glow, orb, glass,
  blur, or translucency. Fire, flame, trophy, medal, star, confetti, or a badge.
  A progress ring or a circular percentage. A greeting in large display type
  above the readout. Pills, tags, or chips. Two stat tiles side by side. Drop
  shadows in Light. Purple-blue gradient fills.
- **Anti-patterns to avoid.** Fitness-app energy; a hero with a headline and a
  button; a card per row; a generic Material list with an icon at the left and
  nothing else; tiny unreadable captions; decorative engineering hatching used as
  texture.

#### Brief B2 — Habit Detail

- **Android-native context.** A pushed detail screen with a back affordance
  top-left.
- **Device / frame.** Identical to B1.
- **Screen purpose.** One habit's week, run, consistency — and its multiplication.
- **Exact visual hierarchy.** Back arrow; the habit name "Drink water" in the
  label weight; beneath it a **multiplication block**: two factors side by side
  with an engraved channel under each and an inscription under that
  ("8 × GLASSES", "12 IN A ROW"), and a large result at readout size below them.
  Then a hairline. Then the inscription "THIS WEEK" with the answer "4 of 6
  scheduled" in heavy channel-value weight **larger than the inscription**.
  Then a **seven-position channel strip**: five engraved positions, four filled,
  one struck with a full-height diagonal, and **two positions with no channel at
  all** — uncut. Then the run channel with a **break glyph** where a scheduled
  day was missed, and "LONGEST 19" one weight lighter. Then a 30-division
  consistency channel with "78%" at figure size. Then a fixed-slot configuration
  faceplate. One labelled action at the bottom.
- **Composition.** Single column, channels full content width, configuration in
  fixed-height slots.
- **Typography direction.** Manrope. The multiplication result is the largest
  element. Inscriptions tiny, uppercase, letter-spaced.
- **Colour / environment.** Cool matte plate, printed grid; near-black type; one
  signal accent on registered values.
- **Surface treatment.** Machined, bevelled, no drop shadow. Channel interiors one
  step down from the plate.
- **Major components.** Back arrow; multiplication block with two engraved
  factor channels; week channel strip with two uncut positions; run channel with
  break glyph; 30-division consistency channel; slotted configuration faceplate;
  one action.
- **Visual metaphor.** An instrument reading out one habit's two factors and
  their product.
- **Interaction state.** A 12-day run, a partly complete week, a Mon/Wed/Fri
  sibling elsewhere.
- **Important data.** Drink water · 8 × glasses · 12 in a row · longest 19 ·
  week 4 of 6 scheduled registered, **2 not scheduled** · consistency 78%
  (30 days) · missed 6 · Active.
- **Light/dark requirement.** Light rendering; produce a Dark twin with a
  darker channel interior.
- **MUST be visible.** The multiplication with its two factors named. The answer
  "4 of 6" larger than the "THIS WEEK" inscription. The streak figure larger than
  the habit name. **Two of the seven week positions with no channel at all** —
  not greyed, not dotted, absent. A distinct break glyph in the run channel. The
  30-division consistency channel.
- **MUST NOT appear.** A weekly bar chart. Fire, flame, or a streak badge. A
  circular progress ring or orb. A 2×2 grid of equal stat tiles. A drop shadow
  in Light. A filled-pill "chip" row. A gauge with a needle (an analogue gauge
  would read as a car dashboard, not as an instrument panel). Drop shadows or
  soft glows on channels.
- **Anti-patterns to avoid.** Fitness-tracker aesthetics; a card stack of
  identical rounded rectangles; a donut chart; a percentage with no window; an
  "achievement level" or progress-to-next-tier meter; a calendar heat map with
  no distinction between a miss and a rest day.

#### Brief B3 — Tasks

- **Android-native context.** A tab destination; Material list conventions.
- **Device / frame.** Identical to B1.
- **Screen purpose.** Manage tasks as a management surface.
- **Exact visual hierarchy.** Top: an inscription "TASKS" with a count at its
  right. A bezel field with an engraved interior and a tiny tracked label. A
  filter bar with **two labelled axis groups**, each preceded by its own
  inscription ("STATUS", "CATEGORY"). A single raised faceplate containing two
  rows separated by engraved rules, each row = name, a unit inscription, a
  category inscription, a status inscription, and **one** overflow at the right.
  A bottom-anchored primary action. A four-slot segmented rail.
- **Composition.** Fixed grid, single column, no per-row card, no button bar.
- **Typography direction.** Manrope; inscriptions tiny, uppercase, letter-spaced;
  values heavy.
- **Colour / environment.** Cool matte plate, printed grid, near-black type, one
  signal accent.
- **Surface treatment.** Machined bezels, engraved interiors, no drop shadow.
- **Major components.** Inscription + count; bezel field; two labelled filter
  groups; single faceplate with two rows and one overflow each; docked action;
  four-slot segmented rail.
- **Visual metaphor.** An instrument's settings face.
- **Interaction state.** Two tasks, one due today and not done, one due in 12
  days.
- **Important data.** "Book dentist" — ONCE · DUE TODAY · NOT DONE. "Renew
  passport" — ONCE · DUE 12 OCT.
- **Light/dark requirement.** Light; produce a Dark twin.
- **MUST be visible.** The word "Tasks" — never "activities". Exactly one
  overflow per row. Two clearly labelled filter axes. A four-slot segmented rail.
  The search field's label and text rendered in the product's own ink, not in a
  default Material blue.
- **MUST NOT appear.** "No activities yet" or "Create activity". Two identical
  unlabelled circular buttons under any row. A Material outlined text field in
  default blue. A raw Material bottom sheet. A floating action button of a
  different material from other screens. A dropdown in default Material colours.
  Glass, gradients, or a blue-purple glow.
- **Anti-patterns to avoid.** Management controls louder than content; an
  unlabelled pair of filter chip rows; a web-form search box; motivational empty
  copy; pill-shaped chips used for both filters and settings.

#### Brief B4 — Insights

- **Android-native context.** A tab holding a scrollable analysis view inside one
  instrument. Not a desktop dashboard.
- **Device / frame.** Identical to B1.
- **Screen purpose.** Measure consistency, and show the evidence.
- **Exact visual hierarchy.** Top: an inscription in question form. Then
  **band one** — a readout "**78%**" at large size with an inscription "LAST 30
  DAYS" and a full-width **channel graduated to 30 divisions, 23 filled** —
  labelled "23 of 30 scheduled hit". Then **band two** — the seven-day rate as a
  heavy channel value with a signed delta and a small direction mark, visibly
  *smaller* than the 78%. Then **band three** — a full-width strip of **thirty
  narrow channels**, one per day, on one scale: 21 tight, **3 struck with a
  diagonal**, **6 with no channel at all**. Then **band four** — a ruled panel of
  four category rows, each = name, heavy rate, a graduated channel, a small
  "n of m" caption, **sorted worst-first**. Then one body-size interpretation
  sentence. Then a missed-count inscription. A four-slot segmented rail.
- **Composition.** Four horizontal bands, each full content width, one scroll
  surface, no tile grid.
- **Typography direction.** Manrope; 78% is the largest element; the seven-day
  rate is visibly smaller.
- **Colour / environment.** Cool matte plate, printed grid; near-black type; one
  signal accent for registered values only.
- **Surface treatment.** Machined; every channel a real engraved slot.
- **Major components.** Inscription question; 30-division channel; seven-day
  comparison band; 30-channel day strip; four ranked category channels;
  interpretation sentence; missed-count line; segmented rail.
- **Visual metaphor.** One instrument, four calibrated bands.
- **Interaction state.** 30-day window; one category at 0%; a Mon/Wed/Fri habit
  in the strip.
- **Important data.** 78% · 23 of 30 · 7-day rate + signed delta vs the 30-day
  average · 30 channels: 21 tight, 3 struck, 6 uncut · one category at 0% · 6
  missed occurrences.
- **Light/dark requirement.** Light; produce a Dark twin.
- **MUST be visible.** The 78% as the largest element; the 7-day rate smaller
  than it; **struck and uncut channels clearly different from each other** in the
  30-day strip; a worst-first ranking; one sentence of real interpretation; a
  signed delta with a window.
- **MUST NOT appear.** A seven-bar chart with no axis or values. A 2×2 stat-tile
  grid. "Insufficient data" as a value. A bare word like "Declining". A donut, a
  3D chart, a radar chart, a rainbow chart, a gauge with a needle, a sparkline,
  a heat map with no rest/miss distinction. A wall of widgets. Purple-blue
  gradients.
- **Anti-patterns to avoid.** Chart spam; a legend with no data; percentages
  without windows; six identical stat cards; a desktop analytics layout
  compressed into 400 dp; neon "trading terminal" glow.

#### Brief B5 — Onboarding (page 3)

- **Android-native context.** A full-screen first-run page with a system status
  bar, a page indicator and one bottom action.
- **Device / frame.** Identical to B1. **Not a dark-only photographic page** —
  this page is in the user's real environment.
- **Screen purpose.** State the differentiator.
- **Exact visual hierarchy.** Page indicator; headline "**Nothing leaves this
  phone.**" in the label weight, sentence case; one body line; a large plate area
  whose **calibrated scale visibly stops** — the last graduation is the last mark
  on the surface and no channel continues past it; one bottom action "Continue";
  one small "Skip".
- **Composition.** Text upper half, terminating scale lower half, one action.
- **Typography direction.** Manrope; the headline in the label weight; inscribed
  labels tiny and letter-spaced.
- **Colour / environment.** Cool matte plate with the printed grid (Light) or the
  dark backlit panel (Dark).
- **Surface treatment.** Machined; the scale's end stop is a visible edge.
- **Major components.** Page indicator; headline; body line; terminating scale;
  action; skip.
- **Visual metaphor.** An instrument whose range ends at the device.
- **Interaction state.** Third of four.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads."
- **Light/dark requirement.** Produce **both** environments.
- **MUST be visible.** The scale stopping; nothing running off the edge; short
  factual copy; one action.
- **MUST NOT appear.** Photography, landscape, a human face, a gradient mesh, a
  glow. "journey", "tomorrow", "unlock", "transform", "seamless". "Your life you
  want". "Calm and powerful". A Title-Case unpunctuated headline. A sign-in or
  account field. A social button. A rating prompt.
- **Anti-patterns to avoid.** Four cloned slides; cinematic photo with a scrim; a
  headline-plus-two-buttons hero; four feature icons in a row; more than two
  lines of copy; a website app-store landing page.

---

## 7. Direction C — **Loom** *(the woven run)*

### 8.1 Concept Name

**Loom** — named for the frame, not for the fabric. Momentum's name has been
spent on a tagline and a fire icon and a 12 sp badge, and this direction takes it
back as a **structure that is made over time**: a run of stitches in a warp.
Momentum is not a feeling the app grants you. **Momentum is the length of the run
you have woven.**

### 8.2 Core Idea

A habit is a **warp thread** — always present, running the full height of the
page, and either carrying pattern or sitting plain. A week is a **weft pass** — a
row of picks across the warp. Today is the pick you are making. **Completion is a
stitch; a streak is an unbroken run of stitches; a missed day is a dropped
stitch; and a rest day is a thread the pattern simply does not ask for.**

That last clause is the whole direction. It is weaving's native, four-hundred-year
solution to the exact problem Momentum's visualisation layer has never solved: a
pattern that does not call for a weft pick on a given warp thread simply **has
no pick there**, and a competent weaver does not read the empty space as a
failure. It is a *different* space, and the cloth knows it.

**It is radical in that it treats the three-state mark as a textile structure
rather than as a symbol**, and in that it replaces the product's *entire*
surface, motion and identity vocabulary with a hand-made one — while removing
glass, gradients, glow, the fire glyph, the photographic hero and the pulsing
orb. It is also the only direction of the four that makes the **absence** of a
mark the primary expressive resource, and the only one that answers the
missed-versus-rest question at the level of the material.

### 8.3 Emotional Character

Not "warm". Concretely: **the feeling of a thing made by hand, at a steady
tempo, by someone who is not in a hurry.** Domestic competence. Tactility. The
quiet of repetitive work done well.

Four specific tests:

1. **You can see the material.** There is a weave. A fibre. A selvedge. Faint,
   but present, and it is a *texture* rather than a gradient, which is what makes
   a surface read as cloth instead of as a tinted rectangle.
2. **The rhythm is visible.** Rows align. Picks are evenly spaced. The screen has
   a hand-made cadence rather than a CSS grid's, and the cadence is what makes
   scrolling feel like moving along cloth rather than down a list.
3. **Nothing is perfect and nothing is broken.** No hairline grid of 1 px
   hairlines. The rules are **thread**, with a slight irregularity in weight, and
   the edges are cut with the tiny raggedness of scissored cloth rather than the
   mathematically exact edge of a rounded rectangle.
4. **Repetition is the reward.** The satisfying thing in this direction is not a
   moment. It is **continuation** — the hundredth day looks like the hundredth
  day, and the direction is designed so that it is recognisably so at a glance.

**The emotional risk, stated plainly and it is a serious one: "hand-made craft"
 is itself a design cliché, and it has been thoroughly worked over** — the
wellness app, the bullet journal, the habit tracker with a stitched-leather cover.
The entire first three pages of the current app are already cinematic
photography of a lake at golden hour, which is a *different* version of the same
escape. **Loom's defence, and it has to be earned by execution, is that its
craft is structural rather than decorative**: there is no stitched-leather
texture laid over a Material layout, no paper grain as a filter, no "handmade"
label. The weave *is* the layout. The rows are the rows. If the weave were
removable without changing the composition, the direction would be a costume —
and Loom's whole argument is that it is not.

### 8.4 Interpretation of "Momentum"

**Momentum is the run — a length of cloth that exists only because it was made
one pick at a time.**

This is how Loom answers the fire contradiction — physically, and from inside its
own material. A flame
is a pictograph for heat, and heat is destructive: a flame on a record suggests
burning the record down, and that is subtly the wrong promise for a product whose
core rule is *"unscheduled days never break a streak."* **A flame is the metaphor
of a system that can be extinguished.** A run cannot be extinguished; it can only
be continued or **cut**, and a cut is a visible, honest, repairable thing.

**The three words Loom uses, and what each one means:**

| Word | In the product | Visually |
|---|---|---|
| **Run** | the current streak | a **spine** — a continuous vertical thread, one stitch per contiguous scheduled occurrence, with a **cut** where a scheduled occurrence was missed |
| **Picks** | the scheduled days | the small marks across a weft pass; a day the pattern does not call for has **no pick** |
| **Weft** | the week | one horizontal pass of picks across the warp |

**Where the word "momentum" appears in Loom:** almost nowhere in the interface.
It appears once, in the app's own name, and it is demonstrated rather than
asserted. Loom's bet is the opposite of the shipped app's: the shipped app says
*"Small steps, unstoppable momentum"* and *"You're building momentum"* on a
state line that fires for most of the day for most users, and its 12 sp streak
badge is the only real momentum artefact. **Loom's bet is that the run is the
name, and the name does not need to be repeated.** This is a distinctive and
deliberately unfashionable position — most app concepts restate their own name
constantly — and it is the direction's most distinctive claim.

**What it costs:** a product called Momentum that never says "momentum" is
indistinguishable, on first contact, from a product called anything else. Loom
accepts that the name is carried by the *gesture* rather than the *word*, and
that this makes the app less legible to a prospective user in a store listing. It
is a real cost and it is a real bet.

### 8.5 Visual Metaphor

The metaphor is a **woven cloth in progress on a loom**, applied to all six
concepts:

| Concept | Loom's metaphor | What the user sees |
|---|---|---|
| **Progress** | the cloth filling | a **weft pass** whose picks fill as the day completes, so progress is *woven*, not filled |
| **Consistency** | an unbroken run | a **spine** of contiguous stitches with **no cut** in it |
| **Completion** | the pick | a single dense stitch, visibly denser than the warp it crosses, laid across the weft |
| **Streaks** | the run's length | the **spine**, printed with its **length in days at figure scale**, and a **cut** exactly where a scheduled occurrence was missed |
| **Growth** | the cloth accumulating | a 30-day swatch: warp threads below, weft picks across, with **plain warp where the pattern is silent** |
| **Momentum** | the length of the run | the whole cloth, and the number that counts its stitches |

**The rest-versus-miss mapping — the centre of the direction.** This is the
mapping Loom exists to express, and it is expressed at the level of the material:

| State | On the warp | Hue |
|---|---|---|
| **Completed** | a dense pick laid across, tight | accent |
| **Incomplete** (scheduled, not done) | a **pick laid but visibly loose** — a slacker, wider, paler thread, the way weavers mark a pick they intend to go back and tighten | ink, at body contrast |
| **Not scheduled** | **bare warp. No pick at all.** The warp thread is simply there, carrying nothing | none |

**A rest day is bare warp.** This is the strongest version of the answer Loom
gives, and the reason is that it is *native*: in cloth, the absence of a pick is
not a defect and not a mark. It is the pattern working. A
Monday/Wednesday/Friday habit on a Tuesday is a warp thread that was never asked
to carry a pick, and it looks exactly like what it is. There is nothing to decode,
because there is nothing drawn.

**A missed scheduled day is a loose pick** — and this is deliberately *not* a
hole, not a break, not a strike, not a dark cell. A hole in cloth is damage. A
loose pick is **work in progress that needs tightening**, which is both accurate
(the user can still fix it, if the day is not over) and materially kinder, and
which has the practical advantage that it **is not the same mark as a cut
spine**. A run that has genuinely broken shows a **cut**; a day that has slipped
shows a **loose pick**. The two are different marks, because the streak rule
requires them to be different things.

**Why Loom's answer is stronger than the shipped one, stated as a comparison, not
as a judgement:** the shipped three-state mark is correct — a completed circle, a
ring, and a dim dot — and a dim dot in a 32 dp footprint is still a *mark* on a
day the user was not meant to do anything. It also carries no spoken description
at all, so the product's only correct visualisation is invisible to a screen
reader, while a simpler calendar cell in the same app composes a proper one. **Bare
warp is not a better symbol for a rest day. It is the removal of the symbol.**

### 8.6 Typography

**Manrope is retained, and Loom gives it the opposite treatment to Instrument's.**

Where Instrument made the labels tracked like engraved inscriptions and the
numbers loud, **Loom makes the numbers behave like thread and the words behave
like cloth.** The distinction is a real typographic one and it is the direction's
second signature after the run.

| Register | Size intent | Weight | Tracking | Job |
|---|---|---|---|---|
| **Cloth** | the largest type on the screen, set with **a generous line height and no tracking at all** | 400–500 | **0** | screen names, the habit's name, the day's headline. Loose, untracked, slightly large — cloth reads as cloth at generous line height and tight tracking reads as print |
| **Thread** | the figure register, at a size between Cloth and Body | **600–700** | **+0.01 em** | every number: streaks, targets, consistency. Slightly *tracked out*, and slightly heavier than Instrument's, because in Loom a number is a **thread** and threads have body and a little give |
| **Body** | comfortable | 400 | 0 | names, sentences, explanations |
| **Selvedge** | small, and the direction's second-most-used style after Body | 500 | **+0.08 em** | the **edge labels**: the units, the field names, the window statements, the weekday initials. Named for the selvage — the self-finished edge of a woven cloth — because these labels are the *edge of the readable area* and never the middle |
| **Slub** | reserved, used at most once per screen | 500 | **+0.02 em** | a single secondary line, e.g. the empty-state line. Named for a slub — a thick place in a thread where the spinner was imperfect |

**Three typographic decisions that are Loom's alone:**

1. **A real 700, used for Thread only.** The bundled font has a genuine 700
   instance, and the app registers only 400/500/600, so its five most important
   numbers render at an indeterminate weight. Loom uses 700 on the thread
   register and **nowhere else** — the design document's usage rule against the
   top of the weight axis is respected by confining it to the one register that is
   a literal thread. **This is a one-registration change, fully offline, no new
   asset, no new dependency, no network fetch** — the audit is explicit that the
   weight problem is a *registration gap, not a missing capability*.
2. **Figures are tabular wherever a value changes, and proportional where a
   value is embedded in a sentence.** The app sets no `fontFeatureSettings`
   anywhere, so every number reflows as it changes: a streak badge's `9` becomes
   `10` and shifts its neighbours; the hero percentage re-centres inside its ring
   on every tick. In an app whose premise is watching numbers change, layout
   stability is the one thing the numbers are not getting. Loom makes the
   distinction functional rather than global.
3. **The line-height is the signature, not the size.** Loom's figures are not
   enormous. What makes Cloth read as Cloth is a **line height of roughly
   1.35–1.5× with zero tracking** at large sizes, and what makes Thread read as
   Thread is a **tighter line height around 1.15× with slight tracking-out** at
   moderate sizes. The two registers use *opposite* spacing behaviour, which is
   what produces the direction's tactile quality from a font that is otherwise
   an ordinary geometric-humanist sans. **This is the cheapest and most
   under-appreciated lever in the whole direction**, and it is also the one
   hardest to see in a static image, which is a real limitation.

**Where Loom inverts the shipped scale, deliberately:**

- **The "3 of 5" is the largest thing on Home**, larger than the greeting above
  it. The shipped screen's answer to its own declared headline question — *"How
  am I doing today?"* — is 16 sp Regular body text, smaller than the greeting and
  the same size as a row's metadata line.
- **A streak count is larger than the item's name beside it.** The shipped app
  sets the streak at 12 sp and the habit's name at 20 sp, on the same row.
- **"4/7 completed" is larger than the "This week" label above it.** The shipped
  app has the answer at 13 sp and the label at 16 sp.
- **A habit's live target progress — the most decision-relevant number in its
  day — is Thread size**, not the 12 sp metadata line the app gives it.

### 8.7 Colour / Environment

#### Q1 — the explicit answer

**In Loom, Light and Dark are the same cloth in two states of preparation — and
they are not the same cloth at two luminances, because the *mechanism of
elevation* is different in each.**

**LIGHT — "Undyed."** Warm, slightly grey, unbleached natural linen: a mid-light
ground with a **visible weave** and a faint warp direction running vertically. The
weave is not decoration — it is the direction's entire structural device.

Depth in Loom Light comes from **thread occlusion**: a raised element is
represented by the way the cloth *piles up*, so it has a soft occlusion at the
top edge where the pile meets the ground and a soft, faint *pile-lift* at the
bottom edge where light passes under it. This is a real optical property of cloth
and it is a mechanism Dark does not use.

**DARK — "Indigo-dyed."** A deep blue-black, and — the point of the direction —
**the weave is still there but it has inverted**: on dyed cloth the weave reads
because each thread catches light differently depending on whether it is the
warp-over or the weft-over, so a dark ground shows a **faint diagonal twill sheen**
rather than Loom Light's vertical warp grid. **Different structure, not a
darker version of the same structure.**

> **Loom Light's depth mechanism is thread occlusion on a raised pale pile, which
> requires a light ground and a light pile. Loom Dark's depth mechanism is a
> twill sheen from dyed thread, which requires a saturated dark dye to make the
> over/under legible at all — a black or neutral dark cloth has no twill, because
> there is no value difference between warp and weft to catch. The layout, the
> registers, the run spine and the pick grammar are identical.**

That is the Q1 answer, and it is unusual: **Loom's Dark environment requires
chromatic saturation that Loom Light cannot have.** A neutral dark grey would
destroy the twill entirely. This is the only direction of the four where the Dark
environment is the one that needs more colour, not less — and it is a direct
structural answer to the audit's finding that the shipped environments share all
five accent hues with the Dark value simply lightened, introducing and removing
nothing, so that nothing is expressed in Light that cannot be expressed in Dark.
**In Loom, one of the two environments cannot be built at all without the
chromatic commitment the other does not need.**

**Accent philosophy — one dye, and it is a pigment, not a light.**

The shipped product declares five accent hues and effectively renders in one:
blue does 54 call sites of global work (CTA fill, progress fill, ring arc, orb
halo, selected chip, section-header action label, empty-state tile, stat
sublabel, selected-theme border) while lavender has 4, violet 3, peach 1 and mint
**zero**. A user who has learned "blue means done" cannot decode anything else,
because blue is also the button, the bar, the tab, and the label.

**Loom's rule: a dye is a material choice, and a cloth has a small palette.** The
direction uses **one accent dye** — a madder-type red, which is the oldest and
most durable of the common dyes and the one that reads as *marked* rather than
*lit* — in exactly two roles: **a completed pick**, and **the active state of a
navigation or filter**. Nothing else. Not the primary action, not a button, not
a progress fill, not a section header, not an empty state.

**Why a red, when red usually means error:** in Loom the accent is a **dye, and
dyes are not semantic**. The direction's error colour is a different, darker
brown-black, and the two never appear in the same role. And a red pick in undyed
linen is the most legible possible "this was made" mark against a warm neutral
ground — it is the one combination in the whole direction with no ambiguity.
**The risk is that a red in a product with a delete affordance reads as danger to
a user who has not learned the language, and Loom names that risk rather than
claiming it away.**

**Per-activity colour — and this is the direction where it finally earns its
place.** The product persists a per-activity colour in its schema, maps it, and
reads it in **zero** presentation code. Its UI's per-item colour is a hardcoded
20-key lookup honoured on **three of five** activity surfaces, so *"purple means
meditation"* does not accumulate — the association breaks the moment the user
leaves the Habits tab. The seven item hues are one fixed set used identically in
both environments, which is a hole in the model. And the creation flow has a
*second, parallel* map keyed on a different key space that happens to agree with
the first for all ten shipped templates **by hand-maintenance rather than by
derivation** — so a template's colour will diverge from its tile's colour the
moment anyone edits one, with no test.

**Loom is the one direction of the four where the answer is *yes, colour is a
system***, and the textile metaphor is what makes it honest rather than
decorative. In a dyed cloth, **colour is applied to the thread, not to the
garment** — a striped cloth is one cloth with differently dyed threads. So:

- Per-activity colour is **a dyed warp thread**, and every surface that shows the
  activity shows the *same dye*. The five surfaces converge on one value.
- The user can choose it, from a **small, fixed set of dye names** — madder,
  indigo, weld, walnut, ochre, iron — which are real dyes, not generated hues.
- The creation flow's parallel map **disappears**, because a dye is attached to
  the thread and there is only one thread. This is not a refactor dressed as a
  decision; it is a decision that makes the refactor unnecessary.
- The user's own per-activity colour override, which exists in the schema and is
  read by nothing, becomes the **one** place colour is chosen.

**This is the direction in which the product's most under-used asset — a
persisted, mapped, never-read colour field — is finally spent**, and it is the
one answer in this document that resolves the per-activity-colour question rather
than sidestepping it.

**Semantic colours — separated by structure, not hue.** The shipped product has
one green carrying five unrelated meanings: completion, the ACTIVE status pill, a
7-day streak milestone, a fully-complete calendar day, and positive-trend
polarity. A user who learned "green means done" reads the ACTIVE pill as "done."

Loom separates them **by mark, with a single exception**:

- **Completion** is the dyed pick. One role.
- **Status (ACTIVE / PAUSED / ARCHIVED)** is *text plus a thread treatment* — an
  active item's warp runs straight and full; a paused item's warp runs straight
  but is **drawn in outline only**, with no fill; an archived item's warp is
  **drawn dashed**. Never a colour. This is a textile-native status language and
  it satisfies the never-by-colour-alone floor without a second channel bolted
  onto a hue.
- **A missed scheduled occurrence** is a **loose pick**, and a **broken run** is a
  **cut**. Neither is a colour.
- **Trend polarity** is a **signed figure and a directional stitch**, not a
  colour. The shipped app renders a four-value trend enum as a lowercased string
  so the user can literally read the words *"Insufficient data"*; Loom prints
  `—` and nothing else.
- **The one colour-coded exception** is *danger*, and it is reserved: a cut mark
  is never red. A broken run is honest data, not an error, and colouring it as an
  error is how habit apps manufacture guilt.

**Contrast strategy.** Body at or above 4.5:1 in both environments, measured per
tier per environment. The repository's one measured contrast datum is a
**Light-specific failure of a Dark-passing value** — 3.11:1 in Light against a
4.5:1 floor, 5.22:1 in Dark — which is the clearest statement of the pattern that
the two environments are one design at two luminances. **Loom has no artwork in
either environment**, so the condition that produced that failure cannot recur,
and Loom treats a Dark-passing value as **no evidence at all** about Light.

**The one genuinely hard contrast case in Loom, named in advance:** the weave
texture. A texture that is strong enough to see is strong enough to reduce the
contrast of small text sitting on it. **Loom's rule: the weave is present in the
ground and in large areas, and is absent behind any text smaller than Body** —
the Selvedge and Slub registers sit on a smooth region, never on a textured one.
This is a layout rule stated as a constraint, and it is the kind of thing that
only a rendered image can verify.

### 8.8 Surfaces

**Loom's surface philosophy: matte, fibrous, and made of exactly four materials —
the ground, the cloth, the selvedge, and the thread.**

| Material | What it is | What earns it | Light mechanism | Dark mechanism |
|---|---|---|---|---|
| **The ground** | the un-dyed warp the whole page is woven on | the canvas, always | visible vertical warp + weft grid, faint | faint twill sheen, **no warp grid** |
| **The cloth** | a denser, raised patch of the same material | any group that is one thing | **thread occlusion** — soft occlusion at the top, pile-lift at the bottom | twill sheen, higher frequency, plus a soft outer falloff |
| **The selvedge** | the finished edge of a piece of cloth — a heavier, denser band | section divisions, the navigation rail, the header band | a denser weave, visibly thicker | the same band, lit from the weave |
| **The thread** | a single line: a pick, a spine, a running stitch | every rule, every divider, every mark | a slightly irregular-weight line | a slightly brighter line |

**Why this, and why emphatically not glass.** The locked design document forbids
glass as a default treatment for every card, every row, navigation, entire
screens and every button, and names *"glass everywhere"* in its rejected-patterns
list. The shipped application does precisely that: a translucency recipe is the
design system's core primitive, the default card, the default row, the navigation
bar *and* the primary button, across nine distinct fill values of which four are
tokenised, one of which a component invents by multiplying a token by 0.7. The
*name* describes a material the app does not implement, because the backdrop blur
value is zero.

**Loom's answer is that the product has been trying to be a material and the
material has been wrong.** Glass is a *hard, cold, engineered* material. Loom is
a *soft, warm, made* one. There is **no translucency in Loom** anywhere. The
only opacity that varies in the entire direction is the weave texture, the
thread's own weight, and a single soft falloff on raised elements — all three
named, all three tactile.

**What this buys:**

- A depth ladder in **both** environments, with **different mechanisms per
  environment** — the Q1 answer, structurally.
- Surface separation that works at any display quality, because it is **texture
  and occlusion**, not luminance difference. This is the direct answer to the
  shipped Light environment's condition, where a translucent white surface over a
  near-white canvas is chromatically invisible and a single 1 px hairline does all
  the separation.
- **A count instead of a taxonomy.** Nine fill values become **zero** — ground,
  cloth, selvedge, thread. There is nothing to choose between per screen, which
  is the direct structural answer to the audit's conclusion that the app looks
  hand-made in twenty slightly different ways and none of the differences is a
  decision anyone made.
- **A physical rationale for the product's hardest requirement.** Cloth has
  *warp* and *weft* — a vertical and a horizontal, and the whole vocabulary of a
  layout grid falls out of them for free. A direction built on warp/weft does not
  have to invent a layout system, because a loom *is* a layout system. This is
  why Loom's spacing has a reason to be what it is, which is not true of a
  direction that merely *looks* tactile.

**What it costs, and it is the largest single liability in the direction:** **a
weave texture on a 6-inch screen at 400 dpi is close to the limit of what a phone
can render, and rendering it on a scrolling surface is a real performance
question.** A texture that is visible in a still image can be very expensive in a
scrolling list, and the audit's performance floor is 60 fps with no allocations in
composition hot paths. **Loom names this as a measurable risk rather than
assuming the texture is free**, and the mitigation is structural: the weave lives
on the *ground* and on *large cloth areas*, not on every row, and the Thread
register — the highest-frequency element in the product — is drawn as a line, not
as a textured fill.

### 8.9 Cards

**The explicit answer: cards remain central, and a card becomes a *patch* — a
denser rectangle of the same cloth, with a visible stitched edge.**

The shipped product has no card primitive: the design system's named card has
**one** production call site, six distinct card constructions exist, and each
screen assembled its own because a surface *modifier* is not a component. The
consequence is that a section boundary, a card boundary and a row boundary are
visually identical, which is why the shipped Settings screen reads as five
undifferentiated blocks — and why the app's most-used section label, at 29 call
sites, is 16 sp Regular and therefore invisible.

**Loom's patch has four fixed zones:**

| Zone | Content | Rule |
|---|---|---|
| **Selvedge** | the field name, in Selvedge register, on a **denser band** across the patch's top | the same style in every patch in the product. **A label is never the same size as the content it labels** — the single most repeated failure in the shipped app |
| **Body** | the contents | the same across the product: a name, a value, a thread, a mark |
| **Running stitch** | the patch's edge | a **dashed line** around three sides and a solid selvedge on the fourth — one continuous piece of cloth has one finished edge, and the asymmetry is the rule rather than a decoration |
| **Footer** | a thread, plus at most one action | a thread is what tells the user the group ends |

**What earns a patch:** a group with an identity. A group that merely needs
vertical separation gets **a selvedge and a running stitch** — a *band*, not a
patch. This is the direct fix for the shipped indistinguishability: a denser band
on the bare ground is a visibly different object from a raised patch, at a glance
and by structure.

**A row is a line within a patch, not a patch.** This is Loom's most consequential
card decision and it is the opposite of the shipped Tasks screen, which puts a
48 dp card *plus* up to two unlabelled 48 dp management circles *below and
outside* every card, so a ten-task list is twenty boxes, the management controls
of a task are the loudest repeated element on a screen with no completion
affordance at all, and two of those circles are visually identical, distinguished
only by glyph. **In Loom a list is one patch with running stitches between rows.**
Row weight drops by roughly half, the two identical buttons collapse into a
single overflow at the end of the row, and — the reason this matters most — **a
list reads as a single piece of cloth with seams, which is what a list of things
you are doing *is*.**

**Radius: small, and softly irregular.** Loom uses a small corner radius and,
critically, **gives it a very slightly soft, hand-cut quality** — the corners are
square-ish with a hint of radius, never a squircle, and never a 16 dp bubble. A
large radius is a soft, friendly, *manufactured* shape; cloth is cut, and cut
cloth has a small radius or none. Radius also varies by role — smallest on a
selvedge, slightly larger on a patch, larger still on a modal — so the direction
never applies one corner value to everything.

**What this costs:** a running stitch is a **dashed line**, and a dashed line at a
small size with a 1 px stroke is a *seam* — it can shimmer during scroll, it is
hard to make accessible at large font scale (a dash pattern is not text), and it
is one more thing to re-decide per screen if it is not tokenised. Loom's rule is
that the stitch is **one tokenised pattern with one dash length and one gap**,
never a per-screen choice — the same governance lesson the audit's 100 inline
alphas taught, applied to a new attribute.

### 8.10 Iconography

**The explicit answer: Loom's position is that identity is not carried by icons
at all, and that this makes the icon question largely moot — deliberately and
with stated cost.**

The three-way conflict is factual. The **code** uses Material **Icons** through
the extended set: a static vector collection with **no weight axis**. The
**documentation** — three KDocs, the design system document, its master ADR —
names Material **Symbols** Rounded. The **concept pack** ships Symbols Rounded as
a *webfont* with a declared weight axis 100–700. The audit's finding is that
**one recorded decision was never honoured**, and that the consequence is
functional, not aesthetic: the variable-weight capability the concept assumes is
unavailable to the app and cannot be had without changing the icon source. The
audit also records that the *same glyph* means two different things in the
shipped app — one Material glyph is both the universal fallback for an unrenderable
icon key and the illustration for the app's most important empty state, so "a day
with nothing scheduled" and "a habit we failed to read" are the same picture.

**Loom's position: reduce the number of icon *decisions*, rather than making
more or changing the family.**

1. **Item identity is a dyed warp thread, not a glyph.** A habit is a thread: a
   colour, a width, and a mark in the thread. **No pictograph is required to
   identify anything.** This dissolves the shipped drift between the three
   non-aligned vocabularies — what the picker offers, what the renderer draws,
   what the colour map names — because **there is no glyph registry to keep
   aligned.** It also fixes the shipped case where the app's own "build your own"
   template carries an icon key its renderer cannot draw, so the most consequential
   choice in the creation flow is represented by a fallback star in a fallback
   colour: with no glyph, there is no fallback to fall into.
2. **A glyph remains only where a picture genuinely beats a word**, and there are
   four of them: **back, search, a disclosure chevron, an overflow.** Four glyphs,
   one family, one weight, one optical density, monochrome, at ink.
3. **Every state is a thread.** Completed is a dyed pick. Paused is an outlined
   warp. Archived is a dashed warp. Missed is a loose pick. Broken is a cut.
   Rest is bare warp. At risk is a pick under tension. **None of these is a
   glyph**, and this is the direct answer to the audit's second half of the
   question — *is habit identity carried by a custom family, a treatment of
   borrowed glyphs, or something that is not an icon at all?* — **where Loom
   answers the third.**
4. **The fire is removed, and it is removed for a reason rather than by
   preference.** A flame is heat, and heat destroys cloth. The shipped
   `"streak"` key resolves to a flame tinted amber at 12 sp, in a product whose
   own design document rejects fire-streak gamification and whose ADR records
   that the resolution to a fire glyph substituted one form of the rejected
   pattern for another. **In Loom, a flame is not a decorative choice, it is a
   category error:** you would not put a flame on a length of woven cloth.

**What this buys:** **Loom is the direction with the lowest exposure to the open
Q6 question**, because it makes the fewest icon decisions. No migration, no
`.woff2`-to-Android problem, no pressure on the offline rule, no change to the
concept pack's recorded status, and — because nine source-text visual tests
assert specific implementation text including icon mappings — **no inherited test
breakage on the icon layer at all.**

**What it costs, honestly, and it is real:** four glyphs is very few, and a
product with almost no iconography has to do all of its work with text and
thread. That is a **legibility cost on a small screen**, where an icon is often
the fastest way to distinguish two 16 sp labels — and it means Loom's surfaces are
*more* typographically dependent than any other direction's, which is a
concentration of risk on the one axis the audit says is already the weakest. Loom
compensates with the dye system and with width as a secondary channel, and it
states the concentration rather than hiding it. **A second cost:** reducing icons
to four is a genuine bet against the trend, and if the dye system turns out to be
harder to learn than a pictograph, Loom has removed a *familiar* affordance in
order to remove a *drift*.

### 8.11 Navigation

**Loom's navigation model: a selvedge — a heavier, denser woven band across the
bottom of the page, with four destinations, flush, no floating.**

The four destinations are a locked decision and Loom keeps them. The bar's
material is an open question.

**The selvedge.** A flush-bottom **denser band** — visibly the same cloth, woven
tighter, so it reads as an *edge* rather than as a *floating panel*. It is
separated from the content by a solid thread, not a shadow and not a
translucency. Within the band, four equal-width slots separated by **running
stitches**. Each slot: a **small dyed pick** as the icon, a Selvedge-register
label, and an **active state shown as a full-width pick laid across the top of
the slot** — a horizontal mark, not a vertical one, because the direction's
marks are horizontal.

**Why a selvedge and not a floating bar, as a cost:** a floating element reads
as an overlay and leaves the content extent ambiguous; a selvedge is the *edge of
the cloth*, so the content extent is definite. The cost is that a denser band is
*quieter* than a translucent floating bar and therefore does less to say "this is
the bottom of the app" — which is a real loss of affordance and the direction's
second-weaker navigation choice.

**Row → detail.** The shipped app's dominant transition is a crossfade that is
identical forward and back, and its destination sits behind an asynchronous gate
that puts a Material spinner on **every** detail navigation in the product. The
app already contains an unused shared-element opportunity: an activity's tile is
44 dp in a list and 72 dp on its detail page — the same component in both places,
unanimated.

**Loom specifies that opportunity taken, and specifies it as a container transform
plus a shared element on the item's own dyed warp thread**, which exists in the
list row at small size and becomes the header's large thread on the detail page.
Because the shared element is **a single horizontal line and a single pick**, the
transition needs no destination content to be known in advance to look correct.
**The async gate is what makes a shared element impossible today, and Loom
specifies removing it as a precondition.** This is a Compose shared-transition
answer, an Android-native mechanism, not a web animation.

**Back versus forward:** forward is the container transform; back is the same
transform reversed. The shipped crossfade cannot express the difference and Loom
specifies that it must.

**Inset contract.** One contract, in the cloth's own vocabulary: **the selvedge
bands are the same fixed height on every screen**, the warp grid is the only
horizontal reference, and the top and bottom clearances are stated bands in the
weave rather than per-screen padding. So the same header role reserves the same
cloth on every screen, a title sits on the same warp line as the content it
titles, and no floating group is allowed to be measured against a viewport.

### 8.12 Home / Today

**Audit verdict is REIMAGINE. Loom removes the photographic hero and rebuilds the
screen as a weft pass in progress.**

**Purpose.** Answer *"what do I need to do today and how am I doing?"* without
scrolling, in both environments, at 200% font scale, in one glance.

**Hierarchy, strict, in this order:**

1. **The day's count.** `"3 of 5"` at Thread register, large — **the largest
   element on the screen**, larger than the greeting above it. A completion-based
   product whose own declared headline is *"How am I doing today?"* currently
   answers that question in 16 sp Regular body text, smaller than the greeting and
   the same size as a row's metadata line.
2. **The weft pass.** A full-width horizontal band of **five picks**, one per
   eligible activity, in the order the user set them. Three dyed and tight. One
   **loose**. One **not yet laid** — a *visible gap in the weft*, which is
   materially different from a loose pick and reads as *work in progress* rather
   than as failure. This is Loom's answer to Q3 at the level of the material:
   **today is a shape**, and the shape is a partially-woven band that says
   "mostly done, two to go" without a single digit being read.
3. **The date and the greeting**, on a selvedge band beneath the weft, in Cloth
   register, **one line**, with the date in Selvedge. The greeting is quieter than
   the count, deliberately: the greeting does not change and the count does.
4. **The record.** One patch, with running stitches between rows. Incomplete
   first, in the user's order; complete below its own selvedge. Within a row: a
   **48 dp completion control** (see 8.19), the name at Body weight, the live
   target value at **Thread** size where one exists, and a **spine stub** for the
   item with the longest live run.
5. **The at-risk line.** One line, at Thread size, present only when something is
   genuinely at risk. Absent when nothing is. **No motivational message slot at
   all** — four of the shipped app's six message branches reference no user data
   and one is the product's own tagline used as an everyday state line, so it says
   the same thing all day. If there is something worth saying, the at-risk line
   says it with a number in it.

**Composition.** A **weave grid** — vertical warp lines at a fixed, generous
interval, horizontal weft lines at a tighter one — and every element in the
product snaps to it. The composition's single structural idea: **a warp-and-weft
grid, with content running across it.** Not symmetric margins; the grid itself
creates the structure. Full-bleed for the weft pass and the selvedge bands;
patch-width for everything else. No image, no orb, no ring, no glow.

**Primary visual.** The weft pass. It is the only element in the product that is
*made* rather than *displayed*, and it is what a user recognises Loom by.

**What is removed, and the removal is the point:** the shipped Home hero is 771
lines with three custom draw layers, a 24-sample blend, a 48-sample veil, five
anchor constants and a weighted spacer that expands to fill 72 dp of headroom,
arrived at through eight issues and nine ADRs with a documented history of the
same margin landing in the wrong place three separate times — and the app's *own*
source calls a 222-line version on another screen the reference composition. On a
day with nothing scheduled, all of that still runs: a roughly 300 dp photographic
surface containing a date, a greeting and nothing at all, above a card saying
nothing is scheduled. **In Loom the weft pass is empty and the screen says so in
one line at the top.** The empty day is a piece of cloth with nothing woven into
it, which is exactly what it is, and it costs 0 dp of decoration.

**Key components.** The count; the weft pass; the record patch; the 48 dp
completion control; the live target value at Thread size; the spine stub.

**Data shown.** The five eligible items, three complete, at the canonical content.
The two target-based items' live progress. The run length of the item with the
longest live run. Nothing else. **No statistics, no month view, no trend, no
comparison to an average, no habit library** — the locked design document forbids
a full habit library on Home and forbids detailed analytics on Home.

**Interaction emphasis.** The completion control, then the target entry. And
**row rhythm**: the shipped row is 44, 62 or 84 dp depending on whether it carries
a schedule label and a streak badge, so a list of rows is a ragged stack rather
than a cadence. **Loom reserves the metadata line** and renders an empty slot when
there is no metadata, so every row is the same height and the list reads as an
even weft.

**Distinctive visual behaviour.** **The next pick is laid.** Every time an item is
completed, its pick is laid across the weft at that position — a short horizontal
stroke that appears — and if it was the last one, the whole band tightens by one
degree and the count changes. **A completing row does not glide between groups**;
it stays where it is and its pick changes state. The shipped app's glide is a
lovely piece of engineering and Loom deliberately gives it up, because in a woven
structure a pick does not travel — it is *laid*, and it stays.

**The day-complete moment.** The shipped app's entire day-completion celebration
is one long-press haptic plus a snackbar reading *"All done - momentum secured."*,
while a milestone state and a reward duration exist, are documented and are
unreachable, and while the hero simultaneously displays *"Everything complete.
Rest easy."* — the same news twice within 200 px.

**Loom's version, and it is the most physical gesture in this direction:** at
100%, the whole
weft band **tightens** — the gap between picks closes, the band gains a small
amount of density, and the selvedge beneath it gains one stitch. A single firm
haptic. No confetti, no particles, no light, no sound, no toast. **The moment is
the cloth becoming cloth.** It is *earned* — it fires on a milestone, not on a
routine completion, which is the only way to satisfy both the locked "no routine
confetti" rule and the project requirement for a *streak celebration* — and it is
a real physical metaphor rather than a gesture performed over the top of the
design.

**Light-mode considerations.** Undyed linen ground with a visible vertical warp
and a faint weft grid. Patches are denser and raise by **thread occlusion** — soft
occlusion at the top edge, faint pile-lift at the bottom. The count is a very dark
warm brown-black. The dyed pick is madder. **The weave is present in the ground
and in large cloth areas and absent behind any text smaller than Body** — Selvedge
and Slub labels sit on smooth regions. There is **no artwork anywhere in Light**,
which is what makes the measured Light contrast failure — secondary text at
3.11:1 over sampled hero art against a 4.5:1 floor — structurally impossible to
reproduce.

**Dark-mode considerations.** Deep blue-black **dye**, and the structure inverts:
**a faint diagonal twill sheen rather than a vertical warp grid**, because on dyed
cloth the weave reads through the over/under of warp and weft rather than through
a printed grid. **This is not optional** — a neutral dark grey has no twill at
all, because there is no value difference between warp and weft to catch, so
Loom's Dark *requires* the chromatic commitment. Patches raise by twill sheen at a
higher frequency plus a soft outer falloff. The count is a pale warm bone. The
madder pick is *the same dye name* but a re-chosen value, lighter and slightly
desaturated against a dark ground, because a saturated madder on a dark field
vibrates. **The dye's name is environment-independent; its value is not**, which
is the specific structural correction to the audit's finding that the shipped
environments share five hues with the Dark value simply lightened.

### 8.13 Habit Experience

**Loom takes the product's one correct three-state mark and asks what it would
mean if a habit's history were actually a piece of cloth.**

The shipped `DayCell` is the only component in the product that satisfies the
requirement that a Mon/Wed/Fri habit must not make Tuesday look missed:
completed is a gradient-filled 32 dp circle with a check, incomplete is a 1 dp
ring, not scheduled is an 8 dp muted dot in a 32 dp footprint.

**It is correct, and Loom keeps its semantics and its three states. Loom changes
the principle from "three symbols" to "three conditions of cloth":**

| State | The warp | The pick | Hue |
|---|---|---|---|
| **Completed** | carries | a **tight dyed pick** laid across | accent dye |
| **Incomplete** (scheduled, not done) | carries | a **loose pick** — visibly wider, paler, slacker, the way a weaver marks a pick they will go back and tighten | ink, at body contrast |
| **Not scheduled** | carries, **plain** | **no pick at all** | none |

**The rest state is bare warp, and it is the strongest thing in Loom.** A
Mon/Wed/Fri habit on a Tuesday is a warp thread that was never asked to carry a
pick, and it looks exactly like what it is. There is no mark, no dim marker, no
gap-with-meaning — because **a gap in cloth is not a hole in a schedule**. This
is not a better symbol for a rest day; it is the *removal* of the symbol, and it
is the only one of Loom's rest-day answers that requires the user to decode
nothing at all in order to understand a day off.

**A missed scheduled day is a loose pick, and a broken run is a cut — and they are
deliberately different marks, because the streak rule requires them to be
different things.** A loose pick is *this day, not yet tightened* — and if the day
is not over, the user can still fix it, so a loose pick is honest and non-final.
A cut is *this run has ended*, and a cut is final. Using one mark for both would
tell a user on Sunday evening that a run they can still save is already broken.
**This is a correctness requirement, expressed as a visual distinction, and it is
the reason Loom needs two marks rather than one.**

**Streak — the run as a spine, and the number at Thread size.** A **spine** running
the full height of the habit's history: one stitch per contiguous scheduled
occurrence, dyed, with a **cut** exactly where a scheduled occurrence was missed,
and a **loose stitch** where one is still open. The spine's length is printed
beneath it, at **Thread** register, with the label *"in a row"* in Selvedge.

**What Loom does that no other direction does: the spine is the *only* place the
streak is drawn, and it is drawn once, at true scale, for the *whole* run.** The
shipped app's streak is a 12 sp badge with a flame, and it appears on a list card
as a *badge* because a badge is a compact thing that fits in a row's text column.
Loom's spine is not compact, and the reason is that a run is a length, and a
length is a line. **The consequence is that Loom's streak cannot be a badge** —
which is a constraint, not a preference, and which means the run has to be given
room. On a list card Loom therefore shows a **short spine stub** of the most
recent few stitches, not the whole run, and the whole run lives on the habit's own
page. That is a real compromise and Loom states it.

**Target magnitude — and the honest dependency.** For a target-based activity,
Loom's pick can show **how much of the pick is laid**, so *"5 of 8"* is a
half-width pick rather than a numeral. **This requires per-day completion values
to reach the presentation layer, and the audit records that they do not** — the
habit-overview use case takes a set of completion *dates* and discards each
record's value at the use-case boundary, so a "drink 8 glasses" habit's page
cannot currently show that you averaged 6.2.

> **Loom's partial pick is contingent on a data-shape question this document has
> no authority to answer.** The binary week works today, with no data change, and
> is what the briefs below depict. Loom records the dependency rather than
> assuming it, and notes that the shipped weekly view's bar-chart prohibition is
> *not* the obstacle — the stated reason for that ban is that a habit can be
> completed at most once per day, which is a claim about the *week*, not about
> *values*. A value-bearing view is a different question, and it is one of the
> questions Phase 1 left open.

**Consistency.** Printed, at Thread size, with its window named in Selvedge. The
engine computes a consistency score documented as the percentage of scheduled
occurrences hit over the last 30 days, and it has **zero** presentation call
sites: for a product named Momentum, the number that says whether the user is
consistent is never shown anywhere in the product. Loom shows it, and it shows it
as a **30-day swatch** — a rectangle of cloth whose density is the score — which
is the textile version of a heat map and the reason Loom's Insights screen is
coherent with its habit page rather than merely similar to it.

**Missed occurrences** are printed as a count beside the swatch, because they are
what would make a month view mean anything, and because the engine already
computes them and nothing displays them.

**Paused — and this is where the textile metaphor earns its keep.** The shipped
product has a documented, hard-won rule: the row **never dims its own text**,
because fading the whole card dropped body-text contrast below the 4.5:1 floor
in the light theme, so "quieter" is expressed through the surface instead of an
alpha layer. That rule is stated in the code and then **violated in two places
anyway** — the whole card is dropped to 0.85 alpha when paused on the management
list, and the identity block and the metadata row are dropped on the detail page,
which is exactly the failure the rule exists to prevent, and the habits
specification explicitly asks for *"slightly reduced visual emphasis"* and
*"Do not gray out or disable the entire card."*

**Loom's answer is textile-native and needs no alpha at all: a paused activity's
warp is drawn in outline only, with no fill.** The text is untouched. The thread
is thinner. A paused habit is *unwound*, not faded. **This is a structural answer
to a contrast failure**, and it is the clearest single place where the metaphor
produces a correct answer rather than a decoration.

**Feedback.** The pick is laid. One short haptic. No scale bounce, no spring —
bouncing cloth is not a thing, and the shipped completion control's spring pop is
preserved only because the audit identifies it as the product's one genuinely
well-behaved stateful control whose label names the action rather than the shape.

### 8.14 Tasks

**Loom's problem statement, in the product's own words, because the vocabulary
leak is the defect.** The shipped Tasks tab is the shared activities list filtered
to tasks. On an empty Tasks tab it reads *"No activities yet"* and offers *"Create
activity"*; the FAB's label says *"Add activity"*; the sheet says *"Create item"*;
the sheet's own options say *"Habit"* and *"Task"*. **Four names for one action
across three surfaces, and the word "activities" appears on a tab labelled
"Tasks".** Every row additionally carries up to two unlabelled 48 dp circular
management buttons *below and outside* the card, so a ten-task list is twenty
boxes, the management controls are the loudest repeated element on a screen with
**no completion affordance at all**, and two of those buttons are visually
identical to each other, distinguished only by glyph.

**Loom's four moves:**

1. **One noun per surface.** The tab is Tasks, the screen says Tasks, the action
   says *New task*. A shared surface's vocabulary does not leak through a filter.
2. **One management model — the one the product already ships correctly.** The
   management list uses a per-row overflow with status-appropriate actions and a
   confirmation that names permanence; the Tasks screen uses a bar of two
   identical circles. **Loom gives Tasks the overflow**, collapsing the row weight
   by roughly half and eliminating the pair of identical buttons that invite
   mis-taps with no labels. The audit records the two-model situation as a
   *product* inconsistency rather than a spec violation — the screens were
   specified separately — so Loom is choosing between two already-specified
   behaviours and choosing the one the product already gets right.
3. **The row is the same object as the Home row.** Habits and tasks share a visual
   grammar by locked decision, and in Loom they share it *literally*: the same
   patch, the same running-stitch row, the same name weight, the same dye. What
   differs is **which slots are filled** — Tasks rows carry recurrence, category
   and status; Home rows carry today's target value — and that is a *data*
   difference expressed by an empty slot, not by a different component. The
   shipped mechanism of suppressing the streak badge for tasks becomes, in Loom,
   simply an unwound warp. **The same thread, the same width, the same mark, in
   every one of the five surfaces** — which is the direct answer to the audit's
   finding that a habit's identity currently changes size, radius, colour and
   container five times between its list row, its detail page, its insights row
   and its creation card.
4. **A woven field, not an outlined Material box.** The app's only text field
   leaves its unfocused and focused text colours, its label colour and its
   disabled text colour to Material defaults drawn from a palette this product
   does not use. Loom's field is a **channel of undyed warp with a Selvedge
   inscription**, and its text colour is a token.

**Filters.** The shipped screen stacks two **visually identical rows of pills** —
status, then category — with no label distinguishing the axes, and it is the only
screen with a filter surface, on a component the habits specification explicitly
denied to the management list. Loom's answer is **one filter bar, with a Selvedge
axis inscription per group**, so two adjacent groups can never read as one
unlabelled pair, and a filter can never be mistaken for a setting — which the
shipped Settings screen also fails, where the same pill component serves a filter
and a setting with no visual distinction.

**The empty state.** *"No tasks."* + one Slub line that says something true:
*"One-time or repeating. They show up on Home when they are due."* + one action.
Not *"Create something worth showing up for."* — the only unhedged motivational
line in a list context in the entire product, and it names nothing about the user.

**What Loom will not do:** it will not give Tasks a completion control. The
architecture already implies that management belongs on Habits and action belongs
on Home, and the temptation is to fix the missing affordance by turning Tasks into
an action surface. **Tasks is a management surface**, and Loom makes it look like
one rather than pretending otherwise.

### 8.15 Insights

**Loom's Insights is a swatch, and the screen's coherence depends entirely on the
habit page having taught the vocabulary first.**

What the shipped screen does: the view model loads **60 days** of completion
history and the chart draws **7 bars**. The only figure in the whole application
set above 20 sp is the **seven-day rate**, while today's completion is 20 sp with
an out-of-family weight, a streak count is 12 sp, a habit's live target is 12 sp,
the calendar's own summary is 12 sp and the habit page's week is 13 sp. **The
product is twice as emphatic about the past week than about right now.** Four
different data-mark vocabularies appear on this one 292-line screen: seven fully
rounded bars with no values, no axis, no maximum and no gridline; a 6 dp rounded
linear bar; a stat tile with no track, no scale and no rank; and a percentage with
no mark at all. The two lists on the screen are the two places where a *ranking*
is the entire value, and **neither is sorted** — categories come out in
map-iteration order, and the section named "most consistent" shows only the best
performers with no contrast against the worst. The weekly bars discard the
scheduled count the record already holds, so **a day with nothing scheduled and a
day with five scheduled and none completed draw the same empty bar** — and the
`DayRate` type carries that count today, and the seven-day rate that leads the
screen is 40 sp while the *missed* count the engine computes is shown nowhere.

**Loom's structure — a swatch, in four bands:**

1. **Band one — the 30-day swatch.** A rectangle of cloth, 30 columns, whose
   **density is the consistency score**, with the score at Thread size beside it
   and its window named in Selvedge. This is the number the engine computes, the
   number the product specification names, and the number with **zero**
   presentation call sites. It is also, on this screen, the *same object* the
   habit page shows — so a user has already learned to read it.
2. **Band two — the per-day channel.** The swatch at 1:1, one pick per day, all on
   one scale, in the habit-week vocabulary — tight, loose, not-laid, and **not
   called for**. **This is the direction's most important change on the screen:**
   the shipped screen reads two months of history and draws one week of it, and a
   user with a sparse schedule cannot tell slackeriness from rest. In Loom a
   Monday/Wednesday/Friday week visibly has **bare warp on four of the seven
   columns**, and the reader learns from the shape alone that those were never
   required. The scheduled count the record already carries is finally used.
3. **Band three — the comparison.** The seven-day rate as a Thread-size figure,
   with the **delta against the 30-day average printed as a signed figure** and a
   **directional stitch** — a pick that leans one way. The shipped screen has this
   one comparison cue and it is genuinely good; Loom keeps it and demotes it to
   second place, which is the inversion the shipped screen's 40 sp headline needs.
4. **Band four — the ranked picks.** By category, **sorted, worst first when a
   category is at zero**, because the value of a ranking is comparative and
   unsortedness throws that value away. Each row: category name at Body, rate at
   Thread, a **graduated channel** drawn with the progress-track token rather than
   one of the three different surface tokens the shipped app uses for one job, and
   *"n of m complete"* in Selvedge.

**Interpretation as a sentence, generated from the user's own data.** One line, at
Body: *"Six of eight scheduled evenings hit. Tuesdays are the ones you skip."*
The shipped screen's message slots gesture at this and do not deliver it — four of
the six Home messages reference no user data at all, and one is the product's own
tagline used as an everyday state line.

**The trend, printed properly.** The shipped app surfaces a four-value trend enum
as a lowercased string, so a user can literally read *"Insufficient data"* as the
value of a row labelled "Trend". **Loom prints `—` for insufficient data**, and a
signed figure with a directional stitch and a window otherwise. No enum name ever
reaches the user.

**Missed occurrences, printed** — computed, tested, invisible today.

**What Loom will not do:** a dashboard wall. The shipped screen's 2×2 grid of four
equal stat tiles is why it fails the locked acceptance criterion that Insights
must feel analytical without becoming a dashboard wall. One object at four
scales, one mark vocabulary, no decorative charts per the locked rejected list.
And **no bar chart anywhere** — the shipped screen's weekly bars are the specific
component that cannot distinguish a missed day from a rest day, and in Loom that
component has no analogue because the mark vocabulary has no bar in it.

### 8.16 Onboarding

**Audit verdict is REIMAGINE. Loom's onboarding teaches the mark vocabulary,
because in Loom the mark vocabulary is the product's only hard idea and an
unexplained mark language is a wall.**

**The conceptual argument, in four beats.** The shipped onboarding's four bodies
contain **zero** facts about what the app does — no Activity concept, no streaks,
no progress as a mechanic, no offline operation, no on-device storage, no account,
no cloud, no tracking — while the product's best-written line, *"Everything stays
on this device. No account, no cloud, no tracking — offline by design,"* sits in
Settings behind an icon on a screen a first-time user does not visit. The app
describes itself in the third person inside its own UI, calls itself *"a calm and
powerful app"*, offers *"create the life you want"* and *"your journey to a
brighter tomorrow starts now"*, and one of its four headlines is the only one in
title case without terminal punctuation.

**Page 1 — the two states.** Headline: **"Some days you do it. Some days it isn't
asked."** One line: *"Momentum is a run. Days the week doesn't ask for don't
count, and they never break it."* Visual: a small swatch with five columns —
three tight dyed picks, one loose, one **bare warp** — and the bare warp is
**pointed at with a single fine leader line** and labelled. **This is the only
direction of the four that teaches its own hardest idea in the first screen**, and
it is the right call for Loom specifically: if the rest day is a *removed* symbol,
then a user who has never been told what a removed symbol means will read it as a
hole.

**Page 2 — the one idea.** Headline: **"Habits, tasks, medicine, practice. One
kind of thing, tracked."** One line naming the Activity concept, with a short list
of what a person actually tracks, and a row of **five dyed warps side by side** —
one per kind — so the dye system is introduced as a *set* rather than as a single
swatch.

**Page 3 — the difference nobody else can claim.** Headline: **"Nothing leaves
this phone."** One line: *"No account. No cloud. No tracking. No ads."* Visual:
the swatch with **no selvedge beyond it** — the cloth simply ends, with a finished
edge and nothing past it. This is the page the shipped app does not have, and it
is the page that would win a user choosing between habit apps, because no
competitor in that consideration set can say it.

**Page 4 — the day.** Headline: **"Tomorrow, this is the top of the screen."** One
line, one action, *"Start"*. Visual: the day's count at Thread size, with the
weft pass beneath it and a spine running down the side. Not *"Ready to build
Momentum?"* — a question about the future, when the product is about the present.

**The theme re-threading, which is a genuine decision rather than a convenience.**
The shipped onboarding is **theme-blind**: **zero** reads of the theme system in
its files, four photographs with no Light variant, its own 14-constant palette, its
own ink — the headline colour is a warm off-white from a separate palette, not the
product's ink — and a `DisposableEffect` that force-sets the light status-bar
appearance and restores it on dispose. That code is correct and well-commented and
it exists **precisely because onboarding does not know what theme it is in.** A
first-run user who chose Light walks through four near-black photographic screens
and arrives at a light cool-grey app. The first thing the product shows and the
thing it becomes are two different visual products.

**Loom removes the photography, and the theme-blindness goes with it.** There is
no art to recolour, no palette to reconcile, no Light variant to derive, no
separate ink, and no system-bar hand-patch — because onboarding is now simply the
first four screens of the app, in the app's cloth and the app's ink, in **whichever
environment the user is actually in.**

**And there is a second reason Loom's onboarding must be re-themed rather than
kept, which the other directions do not have:** the vector illustration sources
that were flattened into photographs still sit in the repository, unused, in three
different coordinate systems and two different construction techniques, ranging
from 1.9 KB to 2.16 MB. **A cloth metaphor cannot borrow photographic
illustration at all**, so the onboarding has to be built from the same marks the
app uses — which is why re-theming is not a nice-to-have in Loom but a
structural requirement of the metaphor.

### 8.17 Motion

**Loom's motion personality: things are made. Nothing breathes, drifts, orbits,
pulses, shimmers, or runs on a wall clock.**

The shipped app runs 31 animation call sites across 9 files. The one motion token
it uses consistently is a 6-second ambient value whose sole purpose is two
decorative infinite loops. Six of the nine motion tokens have **zero** production
call sites; 16 hand-written durations have replaced them; 8 raw springs have
replaced the two spring specs, and **none sets a damping ratio**, so all take a
library default — the difference between *"someone chose this feel"* and
*"whatever the default was"*. Two infinite loops run on the primary screen whenever
any progress exists, for a number that changes a few times a day, and the app's
own ambient-field KDoc calls the system *"state-responsive, not decorative
wallpaper"* while the field has no state parameter and the state-responsive field
is pinned to its lowest intensity on two of its three screens. A Media3-free
detail worth recording: the app fires a *text-cursor* haptic constant as a generic
"selection confirmed" tick in its theme gallery, because the intent was right and
the constant was wrong.

**Loom's complete motion inventory:**

| Motion | Trigger | Duration intent | Under reduced motion |
|---|---|---|---|
| **Pick laid** | an item is completed | short, 180–250 ms, a stroke drawn across the weft | the pick appears with no stroke animation |
| **Weft tighten** | the **day** reaches 100% — a milestone, not a completion | 350–450 ms, once, a small density change across the band | a single static density change; no sweep, no travel |
| **Spine extension** | a run grows past its own previous length | 220–300 ms | instant |
| **Cut** | a run breaks | 180 ms, one short mark | the cut appears with no animation |
| **Row relocate** | a row moves between the incomplete and complete groups | 200–250 ms, transform only | instant relocation |
| **Swatch grow** | the Insights window changes | 200–250 ms | instant |
| **Page turn** | pushing a destination | container transform, 250–300 ms | no transform |
| **Back** | popping | the same transform reversed | no transform |
| **Continuous ambient** | **none. Loom has none.** | — | — |

**Two motions Loom deliberately gives up, and what they cost:**

1. **The shipped list's item-placement glide.** When a row completes on Home, the
   shipped app makes it *glide* to the other section rather than vanish — and the
   audit records this as the **only** screen in the entire product with item-level
   motion, and as genuinely good. **Loom does not use it.** In a woven structure a
   pick does not travel; it is laid, and it stays where it was laid. The row stays
   in position and its pick changes state. **This is a real loss** — it is the
   single best piece of motion in the shipped app, and Loom trades it for
   structural honesty. Stating it plainly is the point.
2. **The shipped completion control's spring pop is preserved**, because the audit
   identifies that control as the product's only genuinely well-behaved stateful
   control, where the visual state, the semantic role, the accessibility label, the
   haptic and the motion all agree, and where the label was written as a *promise
   about behaviour* rather than a description of a shape. **Loom keeps the
   contract and the pop**, and removes springs from everything else.

**The permanent-loop removal has an unusually good precedent in this direction's
logic:** the app already contains the correct pattern of gating a visual effect on
a meaningful value — a glow gated so that 0% pays nothing, and a remembered phase
that returns a constant and composes no transition at all when disabled. Loom
generalises it: **if a value is zero, nothing animates, ever** — and in a textile,
an empty warp genuinely has nothing moving in it.

**The day-complete moment in motion.** The weft band tightens over 350–450 ms,
once, plus a single firm haptic. No stamp, no press, no light, no particle, no
toast. This is Loom's *most* physical celebration **in this direction** — its
gesture is drawn in the same material as the rest of the direction, and there is
no expressive gesture anywhere in Loom to be physical instead of this one. It is
earned by
a milestone rather than by a completion — the only way to satisfy the locked "no
routine confetti" rule and the project requirement for a *streak celebration*
together. It also honours the locked design document's *"calm and rewarding
rather than loud"* **as literally as Loom's own material permits**, because a band
tightening is quiet by construction.

**Reduced motion.** Every entry has a defined non-animated behaviour, and every
behaviour is a **substitution rather than a shortening** — which the shipped app
already does correctly in the two places it honours the flag, by switching to a
zero duration or nulling the spec rather than halving a duration. Loom extends
that pattern across its whole inventory, which is short enough that making the
claim true is tractable. **And Loom writes the claim honestly:** the shipped
app's Settings copy promises *"Calm animations throughout the app"* while the
setting reaches **1 of 14** call sites, so a user who needs reduced motion is
told it was applied and it was not — the most consequential single defect Phase 1
identified, and a truthfulness failure rather than an aesthetic one.

### 8.18 Light / Dark

Described **independently**, because they are two states of preparation.

**LIGHT — "Undyed."**
A warm, slightly grey, mid-light **unbleached linen** ground. The structure is a
**visible vertical warp** with a fainter horizontal weft grid — a real weave, and
the vertical emphasis is what makes it read as loom-woven rather than as a
texture overlay. Depth comes from **thread occlusion**: a patch is a denser, more
raised area of the same cloth, with a soft occlusion at its top edge where the pile
meets the ground and a faint **pile-lift** at its bottom edge where light passes
under it. The tonal ladder is five genuinely distinct values, because the
mechanism is a *pile* and not a colour. The count is a very dark warm
brown-black. Selvedges are denser and slightly darker. The dyed pick is madder at
full saturation, which is the one combination in the direction with no ambiguity.
**No artwork anywhere in Light**, which makes the measured Light contrast failure
— secondary text at 3.11:1 over sampled hero art against a 4.5:1 floor —
structurally impossible to reproduce.

**DARK — "Indigo-dyed."**
A deep blue-black **dye**, and the structure **inverts**: a faint **diagonal twill
sheen** rather than a vertical warp grid, because on dyed cloth the weave is
legible through the *over-and-under* of warp and weft — which thread is on top —
and a diagonal twill is the pattern that makes over-and-under visible at phone
scale. **A neutral dark grey has no twill at all**, because there is no value
difference between warp and weft to catch; Loom's Dark *requires* the chromatic
commitment, and the dye is not decoration here, it is the load-bearing part of
the depth mechanism. Depth comes from **twill sheen at a higher frequency plus a
soft outer falloff** into the field. Patches are denser, not lighter. The count is
a pale warm bone. The selvedges are the same bands, lit from the weave. The dyed
pick is *the same dye name* at a lighter, slightly desaturated value, because
saturated madder on a dark field vibrates and a vibrating mark is an illegible
mark.

> **Mechanism statement, made explicitly because Q1 requires it:** Loom Light's
> depth mechanism is **thread occlusion on a raised pale pile**, which requires a
> light ground and a light pile. Loom Dark's depth mechanism is **a twill sheen
> from dyed thread**, which requires a saturated dark dye to make the
> over-and-under legible at phone scale — a neutral dark would destroy it. The
> layout, the type registers, the run spine, the pick grammar and the composition
> are identical. **The two are the same cloth in two states of preparation, and
> they are not the same cloth at two luminances: one of them cannot be built
> without the chromatic commitment the other does not need.**

The locked acceptance criterion that the two environments feel like the same
product is satisfied by construction. And the audit's specific finding — that the
shipped environments share all five accent hues with the Dark value simply
lightened, introducing and removing nothing, so nothing is expressed in Light
that cannot be expressed in Dark — is corrected **structurally** here: Loom's two
environments do not merely re-value the same roles, they are **built by different
physics**, and one of them is impossible without a colour the other does not need.

**The one Dark constraint Loom must state honestly:** a twill sheen has a
*direction*, and a diagonal sheen has a light source implied from a specific
corner. Loom fixes it: **the twill always runs the same way on every surface in
the product**, so the implied light source is consistent, and the audit's
"inconsistent lighting direction" failure — which is common in web-styled shadow
systems — is designed out from the start.

### 8.19 Signature Element

**The run as a spine, and the rest day as bare warp.**

A streak in Loom is a **line**, not a badge: one stitch per contiguous scheduled
occurrence, dyed, with a **cut** exactly where a scheduled occurrence was missed
and a **loose stitch** where one is still open — and its length printed at Thread
size, larger than the item's name beside it, which the shipped app gets backwards
at 12 sp against 20 sp.

And beside it, the thing no other direction does: **a rest day is a warp thread
with no pick on it.** Not a dim dot, not a gap-with-meaning, not a lighter cell.
Bare cloth. A user with a Monday/Wednesday/Friday habit looks at a week and sees
four woven picks and three bare threads, and the bare threads are not asking
anything of them, because **a pattern that does not call for a pick on a given
warp thread is not a failure and does not look like one.** The shipped product's
own requirement — that a Mon/Wed/Fri habit must not make Tuesday look missed — is
satisfied in Loom by the strongest available means: the mark is *gone*, and the
requirement's own history, which is that a dim marker still makes the eye register
a deficit, cannot recur.

**Why this is the signature and not the dyed pick or the tightening weft:** the
dyed pick and the tightening are both lovely, and both are legible. The spine and
the bare warp are the two things that could only have come out of this metaphor,
and they are the two things that make a Monday/Wednesday/Friday user feel
respected.

### 8.20 Risks

Honest failure modes, stated as failure modes.

1. **"Handmade craft" is a design cliché and has been worked over.** The wellness
   app, the bullet journal, and the habit tracker with a stitched cover are all
   already in the market. The current app's own four onboarding pages are
   cinematic photography of a lake at golden hour, which is a *different* version
   of the same escape. Loom's defence is that its craft is **structural rather
   than decorative** — the weave is the layout, the rows are the rows, and if the
   weave were removable without changing the composition, the direction would be a
   costume. **That defence is only as good as the execution, and no image has been
   rendered.** This is the risk most likely to be realised.
2. **A madder red on an otherwise warm neutral product will be read as danger by
   a user who has not learned the language.** In a product with a delete
   affordance, a destructive confirmation, and a `danger` token, a red pick is a
   real misread risk on first contact. Loom separates them by value and by form —
   the error colour is a different, darker brown-black, and destructive
   confirmations are opaque and visually distinct — but the first-contact risk is
   real and Loom does not pretend otherwise.
3. **Rendering a visible weave on a scrolling surface at 400 dpi is a genuine
   performance question.** A texture legible in a still image can be very
   expensive in a scrolling list, and the performance floor is 60 fps with no
   allocations in composition hot paths. **Loom cannot claim the texture is free**,
   and the mitigation is structural (the weave lives on the ground and on large
   cloth areas, not on every row, and threads are drawn as lines) rather than
   guaranteed. This risk is **unmeasurable without a rendered image and a device**,
   and it is the risk that would most plausibly sink the direction on a mid-range
   phone — which is exactly the hardware class the product's success criteria name.
4. **A running stitch is a dashed line, and dashed lines are an accessibility and
   legibility problem.** At a small size with a 1 px stroke a dash shimmers during
   scroll; a dash pattern is not text and does not scale with dynamic type; and it
   is a new attribute that could become the *next* hundred inline literals unless
   it is tokenised as a single pattern with one dash length and one gap. **Loom
   states the rule and also states that the rule is easy to break.**
5. **Reducing iconography to four glyphs is a bet against the trend, and it puts
   all of Loom's legibility weight on typography** — the one axis the audit says
   is already the weakest. A product with almost no pictographs has to distinguish
   two similar 16 sp labels using type alone, at 400 dpi, for a user who may have
   large text on. Loom compensates with the dye system and with thread width as a
   second channel, but **the concentration of risk is real** and it is the
   direction's second-most-likely failure.
6. **The partial pick for target-based habits is contingent on per-day values that
   do not currently reach the presentation layer.** The habit-overview use case
   discards each completion record's value at its boundary, so a "drink 8
   glasses" habit's page cannot show that you averaged 6.2. **Loom's most
   distinctive habit-page element is therefore contingent on a data question this
   document cannot answer**, and the binary week is what actually works today.
   Loom notes that the shipped bar-chart prohibition is *not* the obstacle — its
   stated reason is a claim about the week, not about values.
7. **The spine cannot be a badge, and a spine needs room.** The shipped streak is
   a compact badge because a badge is a compact thing that fits in a row's text
   column. Loom's spine is a line, and a line does not fit in a text column. On a
   list card Loom therefore shows a *stub* of the most recent stitches, which means
   **a list card does not show the streak it is supposed to be about** — a genuine
   loss against the shipped product's already-working compact badge, accepted in
   exchange for the spine being real.
8. **A product named Momentum that never says "momentum" is indistinguishable in
   a store listing.** Loom's bet is that the name is carried by the gesture and
   not the word, and that the app is more distinctive for the silence. Most app
   concepts restate their name constantly, and Loom argues against the majority
   position on purpose. It is a real bet and it could cost acquisition rather than
   satisfaction.
9. **Loom's Dark environment cannot be built at all without committing to a
   saturated dye, and a saturated dark background is harder on battery and on
   OLED displays than a neutral near-black.** The direction's own Q1 answer is
   what creates this problem, which is an unusual and honest form of a cost:
   *the mechanism that makes Dark a designed environment is also the mechanism
   that makes Dark expensive.*

**The single unknown Loom most needs, and the branches:**

> **Unknown: whether a visible weave reads as craft or as a texture overlay.**
> *If it reads as craft* — Loom's structural defence holds, the rest-day-as-bare-warp
> answer becomes the strongest single idea in Loom, and the per-activity
> dye system finally has a rationale that is not "it looks nice".
> *If it reads as a texture overlay* — the direction collapses into a
> generic-premium product with a grain filter, it loses exactly the distinctiveness
> it was built for, and its correct and defensible idea — the removed rest-day
> symbol — survives only as a small mark rather than as the identity. **Neither
> branch requires different screens. Both require a rendered image to choose
> between**, which is Phase 2B's work and not this stage's.

### 8.21 Loom — the five screens, as visual concepts

Same content, same fixture (§4.6). Tuesday 29 September, five eligible, three
complete.

#### Screen 1 — Home / Today

- **Purpose.** Answer *"what must I do today, and how am I doing?"* above the
  fold, in one glance.
- **Hierarchy.** (1) The day's count "3 of 5" at Thread register — the largest
  element. (2) **The weft pass**: five picks across, three tight and dyed, one
  loose, one not yet laid. (3) A selvedge band carrying the date and greeting. (4)
  The record patch with running stitches between rows. (5) The at-risk line. No
  motivational slot.
- **Composition.** A warp-and-weft grid; full-bleed for the weft pass and the
  selvedge bands, patch-width for everything else. No image, no orb, no ring.
- **Primary visual.** The weft pass.
- **Key components.** Count at Thread size; five-pick weft band; record patch; a
  48 dp completion control per row; a live target value at Thread size; a spine
  stub per row.
- **Data shown.** Five eligible, three complete; "5 of 8" and "0 of 20"; run
  lengths 3, 1, 6, 12, 0.
- **Interaction emphasis.** The completion control; the target entry; a reserved
  metadata slot so every row is the same height.
- **Distinctive behaviour.** **The pick is laid; it does not travel.** A
  completing row stays where it is and its pick tightens. At 100% the whole band
  **tightens** by one degree and the selvedge beneath gains a stitch.
- **Light considerations.** Undyed warm linen with a visible vertical warp and a
  faint weft grid; patches raise by thread occlusion; the weave is **absent
  behind any text smaller than Body**; madder pick at full saturation.
- **Dark considerations.** Deep blue-black **dye** with a **faint diagonal twill
  sheen** rather than a vertical warp grid; patches raise by higher-frequency
  twill plus a soft falloff; the same dye at a lighter, desaturated value because
  saturated madder on a dark field vibrates.

#### Screen 2 — Habit Detail (Drink water)

- **Purpose.** One habit's configuration, week, run, consistency — with a **full
  spine** rather than a badge.
- **Hierarchy.** (1) The habit's name in Cloth register. (2) **The spine**,
  full-height, one stitch per contiguous scheduled occurrence, with a **cut**
  where a scheduled occurrence was missed. (3) The week's woven pass: five picks
  and two **bare warps**. (4) The consistency **swatch** and its score. (5) The
  configuration panel on a selvedge.
- **Composition.** Column; the spine runs down one side of the week; the
  configuration is a raised patch.
- **Primary visual.** The spine.
- **Key components.** Name; full-height run spine with a cut; seven-position
  week pass with two bare warps; consistency swatch with its score and window;
  configuration patch; one action.
- **Data shown.** Spine of 12 stitches; week 6 of 8 scheduled laid, 2 not called
  for; longest 19; consistency 78% (30 days); missed 6; Active.
- **Interaction emphasis.** The window control. The rest is read-only.
- **Distinctive behaviour.** None — **the stillest screen in the product.**
- **Light considerations.** Spine in madder on undyed cloth; a loose pick
  visibly paler and wider than a tight one.
- **Dark considerations.** Spine at the desaturated dye value; bare warp is the
  *undyed* thread, so on indigo it is a visible lighter thread with no pick on
  it.

#### Screen 3 — Tasks

- **Purpose.** Manage tasks, as a management surface.
- **Hierarchy.** (1) A selvedge band carrying "TASKS" and a count. (2) A woven
  channel field with a Selvedge inscription. (3) **One filter bar with two
  labelled axis groups** on selvedges. (4) A single patch with running stitches
  between rows. (5) A bottom-anchored primary action. (6) The selvedge
  navigation band.
- **Composition.** Flat single column; no per-row patch; no button bar.
- **Primary visual.** The list patch and its running stitches.
- **Key components.** Selvedge + count; woven field; two labelled filter groups;
  two rows each with one overflow; docked action; four-slot selvedge band with
  dyed picks.
- **Data shown.** "Book dentist" — once · due today · not done. "Renew passport"
  — once · due 12 Oct.
- **Interaction emphasis.** Overflow → Pause/Delete with an **opaque** patch
  confirmation naming permanence and the archive alternative.
- **Distinctive behaviour.** None.
- **Light considerations.** A denser selvedge band; the running stitch as a
  dashed line on a smooth region.
- **Dark considerations.** The selvedge band is denser, not lighter; the active
  destination shows a **full-width dyed pick laid across the top of its slot**.

#### Screen 4 — Insights

- **Purpose.** Answer *"how consistent have you been?"* as a swatch.
- **Hierarchy.** (1) A selvedge heading in question form. (2) The **30-day
  swatch** at 1:1 with its score at Thread size and its window. (3) The swatch at
  1:1, one pick per day, with **bare warp** where a day was not called for and a
  **loose pick** where a scheduled day was missed. (4) The seven-day rate as a
  Thread figure with a signed delta and a **directional pick**. (5) The ranked
  category rows, worst first, each with a graduated channel. (6) One
  interpretation sentence.
- **Composition.** Column; the swatch full content width; the ranked list one
  raised patch.
- **Primary visual.** The 30-day swatch.
- **Key components.** Selvedge question; the swatch; the 1:1 day strip; the
  comparison band; the ranked patch; the interpretation sentence.
- **Data shown.** 78% · 30 days with 6 bare warp days and 3 loose picks · a
  seven-day rate with a signed delta · one category at 0% · 6 missed.
- **Interaction emphasis.** The window control.
- **Distinctive behaviour.** None.
- **Light considerations.** Density of the swatch reads as the score; every
  label sits on a smooth region.
- **Dark considerations.** Density still carries the score; **loose pick and bare
  warp must be distinguishable at this size on indigo**, which is the direction's
  hardest legibility requirement and is stated as a risk.

#### Screen 5 — Onboarding (page 3)

- **Purpose.** State the differentiator — and, uniquely, teach the rest-day
  symbol in the first screen, because in Loom the rest-day mark is a *removal*.
- **Hierarchy.** (1) Page indicator. (2) A small swatch with **five columns —
  three tight dyed picks, one loose, one bare warp** — with a fine leader line
  pointing at the bare warp. (3) Headline "**Nothing leaves this phone.**" (4) One
  body line. (5) The swatch with **no selvedge beyond it** — the cloth ends. (6)
  One action, one skip.
- **Composition.** A small teaching diagram in the upper half, the headline and
  one line beneath it, the terminating swatch, one bottom action.
- **Typography direction.** Manrope. Headline in Cloth register — untracked,
  loosely leaded. Inscriptions tracked.
- **Colour / environment.** Undyed linen (Light) or indigo dye (Dark) — **in the
  user's real environment.** No photography, which is structurally required here:
  a cloth metaphor cannot borrow photographic illustration.
- **Surface treatment.** Matte cloth; the finished selvedge is the visual.
- **Major components.** Page indicator; the five-column teaching swatch with a
  leader line; headline; body line; the terminating swatch; action; skip.
- **Visual metaphor.** Cloth that simply ends.
- **Interaction state.** Third of four.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads." Plus the rest-day teaching diagram: bare warp = not asked.
- **Light considerations.** The same cloth; the diagram's five columns must read.
- **Dark considerations.** The indigo dye; the same five columns must read
  identically.
- **Interaction emphasis.** One action. Nothing else is tappable.

### 8.22 Loom — image-generation briefs

#### Brief C1 — Home / Today

- **Android-native context.** A premium Android habit/productivity app's primary
  screen with Material tab grammar. An app screen, not a craft-market photo.
- **Device / frame.** One Android phone, portrait, centred, slim even bezel,
  even canvas margins, content-first. Identical frame across C1–C5.
- **Screen purpose.** Read the day at a glance.
- **Exact visual hierarchy.** Top: a small tracked caption date. Then a large
  "**3 of 5**" in a warm near-black, thread-weight, slightly letter-spaced — the
  largest element, **larger than the greeting below it**. Then a **full-width
  horizontal band of five short woven picks**: three tight and dyed a deep
  madder red, one visibly loose and paler, one **not yet laid** — a clear gap in
  the band. Then a small tracked label. Then a **denser selvedge band** carrying
  a greeting in a lighter, loosely-leaded weight at a size clearly smaller than
  the count. Then a raised patch of five rows separated by fine dashed running
  stitches; each row = a 48 dp circular control, a name, a heavy thread-weight
  value where one exists, and a short vertical spine stub. Bottom: a denser
  selvedge navigation band with four labelled slots, each marked by a small dyed
  pick.
- **Composition.** A visible vertical warp grid in the background; full-bleed for
  the weft band and the selvedge bands; patch-width for the list. No imagery.
- **Typography direction.** Manrope. The count is thread-weight and large. Names
  are body weight. Labels are tiny, uppercase and letter-spaced. Generous line
  height on large type, tight on figures.
- **Colour / environment.** Warm unbleached linen ground with a faint but visible
  vertical weave; very dark warm brown-black type; **one madder red dye** on the
  completed picks and the active nav slot only.
- **Surface treatment.** Matte cloth. Patches are denser and slightly raised,
  reading by a soft occlusion at the top edge and a faint lift at the bottom. A
  small corner radius. **No glass, no translucency, no gradient, no glow.**
- **Major components.** Count; five-pick weft band; selvedge greeting band;
  record patch with dashed running stitches; 48 dp controls; spine stubs; selvedge
  navigation band.
- **Visual metaphor.** A run of stitches woven across a warp.
- **Interaction state.** Mid-day; three tight, one loose, one not laid.
- **Important data.** 3 of 5 · Morning run, Weekly review, Take the stairs (tight)
  · Drink water 5 OF 8 · Read 20 pages 0 OF 20 · run 12 · Tuesday 29 September.
- **Light/dark requirement.** This brief is the **Light** rendering. Dark twin
  required: deep indigo-blue dyed ground, **faint diagonal twill sheen** instead
  of the vertical warp grid, the same dye desaturated and lightened.
- **MUST be visible.** The count larger than the greeting. A visible weave
  texture. The five-pick band with three tight, one loose, one **absent**. Dashed
  running stitches between rows. Three madder picks. A 48 dp control per row.
  The "5 of 8" value clearly larger than the row's metadata.
- **MUST NOT appear.** Fire, flame, ember, trophy, medal, star, confetti, or a
  streak badge. A progress ring, donut, or circular percentage. Any photograph,
  including of cloth or textiles. Gradients, glass, blur, glow, or a purple-blue
  mesh. A red *error* semantic. Pills, tags, or chips. A card per row. A second
  column of stat tiles. Motivational filler copy.
- **Anti-patterns to avoid.** A craft-market flat-lay aesthetic; a "wellness
  journal" look with a leather cover; a Material layout with a paper texture
  bolted on; rounded-bubble cards; tiny unreadable labels; a phone-shaped
  website; a hero with a headline and a button.

#### Brief C2 — Habit Detail

- **Android-native context.** A pushed detail screen with a back affordance
  top-left.
- **Device / frame.** Identical to C1.
- **Screen purpose.** One habit's week, run, and consistency — with the run drawn
  at full scale.
- **Exact visual hierarchy.** Back arrow; the habit name "Drink water" in a
  large, light, loosely-leaded weight. Then **a full-height vertical spine**
  running down the left of the content: twelve tight dyed stitches in a row, with
  the caption "**12**" in heavy thread weight and "IN A ROW" tiny and
  letter-spaced. Beside it, the **week pass**: seven warp positions across, five
  carrying picks — four tight dyed, one loose and paler — and **two positions
  carrying nothing at all**, bare thread. Then a small tracked label "**THIS
  WEEK**" with the answer "**6 of 8**" in heavy thread weight, visibly **larger
  than the label**. Then the consistency swatch with "**78%**" in heavy thread
  weight and "LAST 30 DAYS" in a tiny tracked caption. Then a raised configuration
  patch with three separately weighted rows. One action at the bottom.
- **Composition.** Column with the spine as a left-hand structural edge; the
  configuration as one patch.
- **Typography direction.** Manrope; the habit name in a light loose weight; the
  streak figure in heavy thread weight, **larger than the name**; all labels
  tiny, uppercase, letter-spaced.
- **Colour / environment.** Undyed linen; very dark warm type; one madder dye on
  tight picks and stitches.
- **Surface treatment.** Matte cloth, thread occlusion for the patch, small
  radius.
- **Major components.** Back arrow; habit name; 12-stitch spine with a caption;
  seven-position week pass with two bare warps; consistency swatch; configuration
  patch; one action.
- **Visual metaphor.** A woven run and a single weft pass.
- **Interaction state.** A 12-day run; a partly complete week; a Mon/Wed/Fri
  sibling elsewhere.
- **Important data.** Drink water · 8 glasses/day · 12 in a row · longest 19 ·
  week 6 of 8 scheduled laid, **2 not called for** · consistency 78% · missed 6 ·
  Active.
- **Light/dark requirement.** Light; produce an indigo Dark twin.
- **MUST be visible.** The streak figure **larger than the habit name**. The
  answer "6 of 8" **larger than the "THIS WEEK" label**. **Two of the seven week
  positions with bare thread and no pick whatsoever** — not dimmed, not dotted,
  not greyed. Loose picks visibly paler and wider than tight picks. A clearly
  drawn spine at full length.
- **MUST NOT appear.** A weekly bar chart. Fire, flame, or a streak badge. A
  circular ring, orb, or donut. A four-tile stat grid. A percentage with no
  window. A trophy, an achievement tier, a progress-to-next-level meter. A red
  *danger* semantic. Glass or gradient.
- **Anti-patterns to avoid.** A weekly bar chart; a "fire 12" badge; a
  habit-detail card stack of identical rounded rectangles; a calendar heat map
  with no rest/miss distinction; a gamification leaderboard; a woven *texture*
  applied over a generic Material layout.

#### Brief C3 — Tasks

- **Android-native context.** A tab destination; Material list conventions.
- **Device / frame.** Identical to C1.
- **Screen purpose.** Manage tasks as a management surface.
- **Exact visual hierarchy.** A **denser selvedge band** across the top carrying a
  tiny tracked label "**TASKS**" and a count at its right. A woven channel field
  with a tiny tracked inscription label. A filter bar with **two labelled axis
  groups**, each on its own selvedge. A single raised patch with two rows
  separated by dashed running stitches; each row = name, a small dyed warp mark
  for identity, a unit inscription, a status inscription, and **one** overflow at
  the right. A bottom-anchored primary action. A denser selvedge navigation band
  with four labelled slots, the active one carrying a full-width dyed pick across
  its top edge.
- **Composition.** Flat single column; no per-row patch; no button bar.
- **Typography direction.** Manrope; names at body weight; all labels tiny,
  uppercase, letter-spaced.
- **Colour / environment.** Undyed linen; dark warm type; one madder dye on the
  selected filter and the active slot.
- **Surface treatment.** Matte cloth, thread occlusion, small radius, **one
  tokenised dash pattern** for the running stitch.
- **Major components.** Selvedge band + count; woven field; two labelled filter
  groups; one patch with two rows and one overflow each; docked action;
  four-slot selvedge navigation band.
- **Visual metaphor.** A seam. Two entries in one piece of cloth.
- **Interaction state.** Two tasks, one due today and not done, one due in 12
  days.
- **Important data.** "Book dentist" — ONCE · DUE TODAY · NOT DONE. "Renew
  passport" — ONCE · DUE 12 OCT.
- **Light/dark requirement.** Light; produce an indigo Dark twin.
- **MUST be visible.** The word "Tasks" — never "activities". Exactly one
  overflow per row. Two clearly labelled filter axes. A visible weave. A selvedge
  navigation band. A dyed pick marking the active destination.
- **MUST NOT appear.** "No activities yet" or "Create activity". Two identical
  unlabelled circular buttons under any row. A Material outlined text field in
  default blue. A raw Material bottom sheet. A floating action button of a
  different material from other screens. A default Material dropdown. A
  photograph of fabric. Glass, gradients or a purple-blue mesh.
- **Anti-patterns to avoid.** A craft flat-lay photo used as a premium signal; a
  Material list with a paper texture overlay; management controls louder than
  content; unlabelled pairs of filter pills; a web-form search box; motivational
  empty copy.

#### Brief C4 — Insights

- **Android-native context.** A tab holding a scrollable analysis view.
- **Device / frame.** Identical to C1.
- **Screen purpose.** Read consistency as a swatch.
- **Exact visual hierarchy.** A tiny tracked question heading on a selvedge.
  Then a **30-column swatch**, full content width: a rectangle of cloth whose
  **density is the score**, with "**78%**" in heavy thread weight beside it and
  "LAST 30 DAYS" in a tiny tracked caption. Then the swatch at **1:1** — thirty
  narrow columns, one per day: 21 tight dyed picks, **3 loose picks**, and **6
  columns with bare undyed thread and no pick at all**. Then the seven-day rate
  as a heavy figure with a signed delta and a small pick leaning one way,
  visibly **smaller than the 78%**. Then a raised patch of four category rows,
  each = name, heavy rate, a graduated channel, a small "n of m" caption,
  **sorted worst-first**. Then one body-size interpretation sentence. Then a
  missed-count inscription. The selvedge navigation band.
- **Composition.** Column; the swatch full content width; the ranked list one
  raised patch; no tile grid.
- **Typography direction.** Manrope; 78% the largest element; the 7-day rate
  visibly smaller.
- **Colour / environment.** Undyed linen; dark warm type; one madder dye on tight
  picks only.
- **Surface treatment.** Matte cloth; every label on a smooth region, never on
  the weave.
- **Major components.** Selvedge question; density swatch; 1:1 thirty-column
  strip; comparison band; ranked patch; interpretation sentence; missed-count
  line; selvedge navigation band.
- **Visual metaphor.** Cloth. Density is the score; bare thread is a day not asked
  for.
- **Interaction state.** 30-day window; one category at 0%; a Mon/Wed/Fri habit
  in the strip.
- **Important data.** 78% · 30 days: 21 tight, 3 loose, 6 bare · 7-day rate +
  signed delta · one category at 0% · 6 missed occurrences.
- **Light/dark requirement.** Light; produce an indigo Dark twin, in which
  **loose and bare must remain clearly distinguishable**.
- **MUST be visible.** The 78% as the largest element; the 7-day rate smaller
  than it; **six bare columns and three loose columns clearly different from
  each other and from the tight ones**; a worst-first ranking; one sentence of
  real interpretation; a signed delta with a window.
- **MUST NOT appear.** A seven-bar chart with no axis. A 2×2 stat-tile grid.
  "Insufficient data" as a value. A bare word like "Declining". A donut, a 3D
  chart, a radar, a rainbow chart, a needle gauge, a sparkline, a heat map with
  no rest/miss distinction. A widget wall. Purple-blue gradients. A photograph of
  fabric used as a chart background.
- **Anti-patterns to avoid.** Chart spam; a legend with no data; percentages
  without windows; six identical stat cards; a desktop analytics layout
  compressed into 400 dp; decorative embroidery as chart filler.

#### Brief C5 — Onboarding (page 3)

- **Android-native context.** A full-screen first-run page with a system status
  bar, a page indicator and one bottom action.
- **Device / frame.** Identical to C1. **Not a dark-only photographic page.**
- **Screen purpose.** State the differentiator, and teach the rest-day mark.
- **Exact visual hierarchy.** Page indicator. Then a **small teaching swatch**:
  five narrow columns side by side — three tight dyed picks, one loose pick, one
  column of **bare undyed thread** — with a fine leader line pointing at the bare
  column and a tiny tracked caption beside it. Then the headline "**Nothing
  leaves this phone.**" in a large light loosely-leaded weight, sentence case.
  Then one body line. Then a swatch with a **visible finished selvedge and
  nothing beyond it**. Then one bottom action "Continue" and one small "Skip".
- **Composition.** Teaching diagram upper third, headline and one line beneath,
  the terminating swatch, one action.
- **Typography direction.** Manrope; the headline untracked and loosely leaded;
  the caption tiny and letter-spaced.
- **Colour / environment.** Undyed linen (Light) or indigo dye (Dark), in the
  user's real environment.
- **Surface treatment.** Matte cloth; the finished selvedge is the visual.
- **Major components.** Page indicator; the five-column teaching swatch with a
  leader line; headline; body line; the terminating swatch; action; skip.
- **Visual metaphor.** Cloth that simply ends.
- **Interaction state.** Third of four.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads." Plus the rest-day teaching: bare thread = not asked.
- **Light/dark requirement.** Produce **both**; the five-column diagram must read
  identically in each.
- **MUST be visible.** The teaching swatch with its leader line on the bare
  column; the finished edge with nothing past it; short factual copy; one action.
- **MUST NOT appear.** A photograph of a lake, a sunset, a textile flat-lay, a
  person, or any face. A gradient mesh, a glow. "journey", "tomorrow", "unlock",
  "transform", "seamless", "your life you want", "calm and powerful". A
  Title-Case unpunctuated headline. A sign-in or account field. A rating prompt.
- **Anti-patterns to avoid.** Four cloned slides; a cinematic photo with a scrim;
  a headline-plus-two-buttons hero; four feature icons in a row; more than two
  lines of copy; a *scrapbook* or *Etsy* aesthetic; a hand-lettered wordmark; a
  coffee-cup flat-lay; a linen-background lifestyle photo.

---

## 8. Direction D — **Aurora, Governed** *(controlled evolution)*

### 8.1 Concept Name

**Aurora, Governed.** Not a new name. **A controlled evolution of the strongest
characteristics Momentum already has** — the ambient light field, the bundled
typeface, the photographic hero, the correct three-state day mark, the well-built
completion control, the manage/act/understand division, and a floating navigation
bar — with every one of the audit's ten critical problems answered *in the
product's own vocabulary*.

This direction is the control. Its purpose in the exploration is to make the other
three legible: if D is what the app would look like if the audit's findings were
simply executed, then the distance between D and A, B and C **is** the size of the
decision being made.

### 8.2 Core Idea

**The product already has an identity. It has never been governed.**

The audit's structural conclusion is that the app's genericity is a *governance*
symptom: it looks hand-made in twenty slightly different ways, and none of the
differences is a decision anyone made. One hundred inline alpha literals across
21 files. Nine surface fills against four tokenised. Six card constructions. Four
icon-container treatments. Two floating action buttons of opposite material. Two
press languages. Five dialogs with three dismiss verbs. Eight previews naming
themes that were deleted three phases ago.

**Aurora, Governed's whole argument is that almost none of that needs a new
aesthetic to fix.** It needs the things the design document already specifies,
executed once, at the system level:

1. **A real numeric register.** The typeface is already bundled, offline, with a
   variable weight axis from 200 to 800 and seven named instances including a
   genuine 700. The app registers three of them. **A stronger numeric register is
   available at zero asset cost, zero dependency cost and zero network cost, and
   the entire CP-1 finding collapses into a family registration plus a scale
   decision.**
2. **One mark family with four states.** The product already has one correct
   three-state mark, on one screen, and it is *the template the rest of the
   visualisation layer was never built from*. D propagates its vocabulary to the
   calendar, the weekly bars, the home figure and the insights strip — and
   **missed-versus-rest becomes answerable everywhere at once.**
3. **A real Light depth mechanism.** The audit's largest open question is whether
   Light is a designed environment or an inversion. D's answer is that **Light
   becomes one**, using a mechanism Dark does not use.
4. **Glass, demoted.** Not removed — *demoted*, to the small set of things that
   actually earn a floating layer. The locked design document already forbids it
   as a default for every card, row, navigation, screen and button; D is
   therefore **closer** to the locked design than the shipped app is.
5. **A reward moment that exists.** A milestone state and a reward duration are
   already declared, documented and unreachable. D wires them, so the day's
   completion stops being a snackbar.
6. **One press language, one dialog, one card, one FAB, one inset contract.** The
   consolidation the audit names as its highest-leverage structural item.

**D is radical in exactly one place, and it is the largest one:** the numeric
register. Everything else is consolidation, and consolidation looks like tidying.
Tidying is not a design direction. **Rebuilding the type scale so that the
product's central question is answered by its largest element is.**

### 8.3 Emotional Character

Not "calmer" or "more premium". Concretely: **the feeling of the same room at
two different times of day, and of a light in that room that knows what time it
is.**

Four tests:

1. **The background is alive but not busy.** An ambient light field that
   **responds to the day's state** — quiet at rest, warmer as the day fills, and
   distinctly different on the day you finish everything. The shipped app has
   this system on three screens, pinned to its lowest intensity on two of them,
   behind a second static ambient field with no state parameter at all, and its
   strongest state is unreachable. D makes it one system, state-responsive, and
   reachable.
2. **The photograph is a place, not a poster.** The hero artwork is kept — the
   app has a genuinely good art pipeline, one asset pair, theme-selected, with a
   documented caller contract — but the hero **shrinks to its content**, so a day
   with three activities and a day with twelve produce genuinely different
   compositions instead of the same 72 dp of empty aurora.
3. **Everything else gets out of the way.** One quiet card. One calm surface. No
   glow anywhere except the reward moment. No orb breathing.
4. **The numbers finally agree with each other.** Not a feeling — a fact. A user
   who has spent a week with D stops noticing the numbers, which is the point.

**The emotional risk, stated plainly:** D is **familiar**. Every one of its
choices is defensible today, and most of them are already in the product's own
documentation. A direction that is entirely defensible can be entirely
forgettable, and there is a real risk that a rendered set of D screens reads as
*"a tidier version of what we had"* rather than as a product with a point of view.
**D does not defend against this by adding novelty. It defends by being
unusually correct about the one thing nobody notices until it is right: the
number.** And it states the risk rather than claiming it away.

### 8.4 Interpretation of "Momentum"

**Momentum is accumulated light — and it is spent, not shown.**

The app is called Momentum. Its documentation rejects fire-streak gamification
and it ships a fire icon. Its day-completion moment is a snackbar. The word
"momentum" appears in the tagline, in four of six Home messages and one
onboarding headline, and **nowhere in the product's mechanics as a visible idea.**

**D answers this by keeping the one asset the product already owns that the name
fits: the Aurora.** The ambient field is the app's only real piece of
self-expression, and it is currently decoration on top of decoration — a static
three-field background with no state parameter, plus a second state-responsive
field on three screens pinned to its lowest intensity, plus four accent hues that
are declared and unreachable, plus a fourth ambient system with a different third
hue and a different alpha.

**D collapses all of it into one field with four states, and makes the field mean
something: it brightens as the day fills, and it is genuinely, visibly different
on a day you finished everything.** Momentum becomes the *behaviour of the
background*, not a message about it.

**And D does the second half honestly: it deletes the fire icon and replaces the
streak with a number that is actually about consistency.** The audit records the
product's own design document describing streaks as *"supporting information, not
the emotional center"* — D takes that literally and gives streaks **a number at
figure scale and a state** (rising / at risk / none) and removes the pictograph.
The word "momentum" then appears in the product only where it means something
measurable: the day's fraction, the run length, the consistency score.

**What it costs:** D is the direction most likely to leave the product's *name*
under-explained. Its answer is subtle — a background that behaves — and subtle
answers to naming questions are the kind that do not survive being described in a
store listing.

### 8.5 Visual Metaphor

The metaphor is **light accumulating over a day**, applied to all six:

| Concept | D's metaphor | What the user sees |
|---|---|---|
| **Progress** | the light filling the frame | the day's figure at true figure scale, over an ambient field whose intensity tracks the fraction |
| **Consistency** | the run unbroken | a **spine of marks**, one per contiguous scheduled occurrence, with a visible break |
| **Completion** | the mark landing | the existing 48 dp completion disc, quiet when idle, solid and accented when done |
| **Streaks** | a run length with a state | a **figure at figure scale** with a state word — `12 · steady`, `12 · closes tonight` |
| **Growth** | the day accumulating across a window | **one mark family at four states**, at four sizes, in one vocabulary |
| **Momentum** | the field's behaviour | the background's state changing as the day fills, and once, distinctly, at 100% |

**The rest-versus-miss mapping — propagated, not reinvented.** D keeps the
product's existing three-state day mark exactly as it is, because it is correct,
it is proven, and the audit calls it the template the product's entire
visualisation layer should have been built from. D's work is to make the *other*
two data visuals obey it:

| State | The mark | Where D applies it |
|---|---|---|
| **Completed** | a filled 32 dp disc with a check | habit week · home figure · 30-day insights strip · calendar cell · category channel |
| **Incomplete, scheduled** | a 1 dp ring | the same five places |
| **Not scheduled** | an 8 dp muted dot in a 32 dp footprint | the same five places |
| **Missed** (scheduled, not done) | **a new fourth state** — a struck ring, or a hollow ring in the danger role | the same five places |

**The fourth state is D's one genuine addition to the vocabulary, and it is
required by the data, not by taste.** The shipped calendar's day-cell wash has
four branches and none of them tests *"scheduled and not completed"* — so a day
with five scheduled and none completed draws **identically** to a day with
nothing scheduled, even though the scheduled count is right there in the same
record and is used in that cell's spoken description. The weekly bars make the
same omission. A fourth state is what makes "which day did I miss" answerable on
the two screens whose entire job is answering it.

**Why this is *not* the Loom answer, and why both are legitimate:** Loom makes a
rest day an *absence of a mark*. D keeps a rest day as a *low-weight mark* and
gives a missed day a *high-weight mark*, so the difference is a difference in
visual weight rather than a difference in presence. **D is more conservative and
less expressive; Loom is more expressive and requires the user to learn a
subtraction.** Neither is wrong, they are different bets, and D makes the
conservative one on purpose.

### 8.6 Typography

**Manrope is retained. The scale is rebuilt. This is D's centrepiece.**

The audit's single most important finding is typographic, and it is the only
finding in the whole document that is simultaneously the *precondition* for almost
every other visual fix, *cheap*, and *available entirely offline today*:

- The app collapses fifteen Material typography slots into **seven sizes**, with
  no step between 28 and 20, and none between 20 and 16.
- The answer to the app's own central question — the fraction "3 of 5 completed"
  on Home — is set in a style that resolves to **Body 16/24 Regular**: *smaller
  than the greeting above it*, and the same size as a list row's metadata line.
- A streak count renders at **12 sp** — half the size of the habit's name printed
  next to it.
- A habit's live target progress renders at **12 sp**, and it is the most
  decision-relevant number in that habit's day.
- The only figure in the entire application set larger than 20 sp is **Insights'
  seven-day rate** — so the product is twice as emphatic about the past week as
  about right now.
- Five call sites request a Bold the registered family does not expose, **and the
  bundled file contains a real 700 instance** — so this is a registration gap,
  not a missing capability. One `Font()` entry, or dropping the fixed-axis pins,
  fixes it with no new asset, no new dependency and no network fetch.
- `letterSpacing` and `fontFeatureSettings` occur **zero times app-wide**, so
  every number is proportional and reflows as it changes.

**D's register, with the meaning-driven order the product's own figures
imply — now > this week > this month:**

| Register | Size intent | Weight | Tracking | Job |
|---|---|---|---|---|
| **Figure, primary** | the largest type in the product | **700** | −0.02 em | "3 of 5" on Home, the consistency score on Insights. Assigned by *meaning*, not by availability of a big style |
| **Figure, secondary** | two steps down | 600–700 | −0.01 em | streak counts, per-habit fractions, category rates |
| **Display** | large, for a screen's own name | 500–600 | −0.015 em | screen titles, the habit's name. Deliberately **lighter in weight** than the figures and **smaller in size** than the primary figure |
| **Body** | comfortable | 400 | 0 | row titles, descriptions, the interpretation sentence |
| **Caption** | small | 500 | **+0.05 em** | metadata lines, field labels, weekday initials, units |

**Five differences from the shipped scale, each of which is a specific audit
finding fixed:**

1. **At least five distinct sizes where there are effectively two.** Five
   semantic levels — screen title, entity name, statistic, master progress
   figure, metadata — currently share one style. D gives each its own size.
2. **A real 700, registered.** The bundled variable file provides it; the app
   simply does not expose it. This is the cheapest high-leverage change Phase 1
   identified.
3. **Tabular figures on every changing number**, via a single font-feature
   setting and no new asset. The orb's percentage stops re-centring on every
   tick; a streak badge stops reflowing its neighbours.
4. **Per-token tracking**, not zero. Slightly negative at display sizes, slightly
   positive at caption. The shipped app sets none anywhere, and a variable font
   is bundled with a use of its weight range left entirely unclaimed.
5. **A label style that is visibly a label.** The app's most-used section label
   is 16 sp Regular at 29 call sites, which means 29 section labels the user
   cannot see, and it is typographically identical to a settings-row title. D
   gives section labels their own register, distinct in size *and* weight from
   the content they label.

**The four inversions D creates, all of them direct fixes:**

- **"3 of 5" is the largest thing on Home** and is larger than the greeting.
- **A streak count is larger than the habit's name** beside it.
- **"4/7 completed" is larger than the "This week" label** above it.
- **The live "5 of 8 glasses" is a secondary figure**, not a 12 sp metadata line.

**What D gives up, stated honestly:** a type scale cannot do everything, and
enlarging the numbers compresses the words. On a small screen, with a list of up
to a dozen activities, a larger figure means fewer rows above the fold. **D
accepts a modest loss of above-the-fold density in exchange for the screen's
answer being legible**, and it accepts it because the shipped product's own
dashboard specification declares the headline to be *"How am I doing today?"* —
a numeric question currently answered in body text.

### 8.7 Colour / Environment

#### Q1 — the explicit answer

**In Direction D, the Light environment becomes a designed environment, and it
does so with a mechanism the Dark environment does not use. Dark keeps its
existing mechanism and gains state responsiveness.**

**The problem, as Phase 1 stated it.** The two environments share all five accent
hues, with the Dark value simply being the Light value lightened — no hue
introduced, none removed, none substituted, so no colour role changes and nothing
is expressed in Light that cannot be expressed in Dark. Light's surface ramp
*collapses*: the surface and the elevated surface are the same value, so Light has
three distinct surface values where Dark has five, and the declared four-level
depth ladder **cannot be expressed in Light at all**. Every shadow in the product
is black at 0.30–0.35 alpha with an accent spot, which is invisible on a light
canvas by construction, and the elevation token has **zero** production call sites
against seven real shadow calls. The top-edge highlight is white at high alpha in
Light, which is a white highlight on a near-white surface. So in Dark, translucency
produces a genuine lift; in Light, a translucent white fill over a near-white
canvas composites to roughly the colour of the canvas, and **a one-pixel hairline
does all the separation.** The two environments are one design at two
luminances.

**D's Light mechanism, and why Dark cannot use it: tonal depth plus a
hue-carrying shadow.**

| | D Light | D Dark |
|---|---|---|
| **Depth mechanism** | a **real tonal ladder** — four or five genuinely distinct surface values, so a card is *lighter* than the canvas and a nested region is lighter than the card | **tonal lift** — a card is lighter than the field, and translucency has something to reveal because the surface is genuinely above the background luminance |
| **Second mechanism** | a **hue-carrying soft shadow** — the shadow is tinted to the canvas's own hue, never pure black, so it reads as depth on a light surface. A pure-black shadow at low alpha is invisible on a light canvas; a *warm-blue* shadow at the same alpha is visible, because it changes hue as well as luminance | the existing soft ambient plus accent spot, which is legible on a dark field |
| **Hairline** | a real, warm, slightly stronger hairline that is the *secondary* separator, not the primary one | a cooler, lower-alpha hairline |
| **Highlight** | **removed in Light** — a white highlight on a near-white surface is a contradiction rather than a highlight | retained in Dark, where it reads |
| **Ambient field** | the state-responsive field at a **higher base intensity than in Dark** — a light canvas needs a visible field to be a field at all | the state-responsive field at a lower base intensity, because on a dark field any field reads |

**Mechanism statement, made explicitly because Q1 requires it:**

> **D's Light environment expresses depth as a tonal ladder plus a hue-carrying
> shadow, which requires a light canvas — a hue-carrying shadow is only visible
> against a surface lighter than the shadow. D's Dark environment expresses depth
> as tonal lift plus a white top-edge highlight, which requires a dark canvas. The
> layout, the type scale, the mark vocabulary, the card construction, the
> navigation model and the motion language are identical between them.**

This is the answer the audit asked for, and it is deliberately *not* a
mechanically-generated answer. It keeps the shipped accent hues as the same five
hues, but it **gives the two environments different mechanisms**, so the Dark
value is no longer merely the Light value lightened, and so Light gains a
capability it does not have today.

**Accent philosophy — five hues with five declared jobs, and the jobs are
separated.**

The shipped product declares five accents and effectively uses one: blue does 54
call sites of global work — primary action, progress fill, ring arc, orb halo,
selected chip, section-header action, empty-state tile, stat sublabel, selected
theme border — while lavender has 4 call sites, violet 3, peach 1, and mint
**zero**. A user cannot decode blue, because blue is everything.

**D's rule, which is the token layer's rule made real:**

- **One accent does global work** — the primary action, progress fill, and the
  active navigation destination. Three jobs, one hue, all of which mean *"this is
  the app's accent"*.
- **A second hue is completion.** Today completion and progress are **different
  hue families on the same screen** — the completion control and completed tiles
  are a green-teal, while the progress ring, the progress fill and every primary
  action are a blue. Whether that was intended is not determinable from the code;
  that it is undocumented is. D names it: **progress is the accent hue; completion
  is its own hue.** Two axes, two hues, stated.
- **The other three declared accents stop being ambient and become named roles**,
  or are removed. A declared hue with zero call sites is surface area pretending
  to be a decision, and the audit is explicit that dead tokens mislead the next
  stage into thinking capabilities exist.
- **The per-item hue vocabulary gets two environments or it gets removed.** Seven
  fixed item hues are used identically in Light and Dark, which is a hole in the
  model — the app's own design principle says the two environments are two
  environments of the same design language, and item identity colour has one.
  **D gives the item hues two environments.** And D **decides the question the
  audit left open**: per-activity colour becomes a **system** — the same hue on
  every surface that shows the activity, on all five, with the user's own
  per-activity colour in the data model finally becoming the place it is chosen.
  The partial system is the defect: today a habit is a coloured jewel in three
  places and a flat blue square in the others, so *"purple means meditation"*
  never accumulates.

**Semantic colours — five meanings separated, as the audit asks.** Today one
green carries completion, the ACTIVE status, a seven-day milestone, a fully
complete calendar day, and trend polarity. A user who learned "green means done"
reads the ACTIVE pill as done. D gives each role its own token **and its own
form**: completion is a filled disc, status is a text-plus-dot pair, a milestone
is a state change in the figure, trend polarity is a signed figure with a
direction mark. The "warning" token, which today carries no warning and is only
the PAUSED colour, gets its job or loses its name.

**Contrast strategy — measured per tier per environment, never inherited.** The
repository's only measured contrast datum is a **Light-specific failure of a
Dark-passing value**: the Home date label measures **3.11:1** over sampled Light
hero artwork against a **4.5:1 floor**, and **5.22:1** in Dark. That is a token
that works in one environment and silently fails in the other, and it is the Q1
pattern in its clearest form. **D treats a Dark-passing value as no evidence at
all about Light**, measures each tier against each canvas *and* against the hero
artwork — because D keeps the artwork, which is the only surface in the product
where text is measured against an image — and the remedy is the **text token**,
not a heavier veil over the artwork. The 74 call sites that token change reaches
are acknowledged as the cost, because doing it the cheap way would re-inflate a
veil and make the header worse.

### 8.8 Surfaces

**D's surface philosophy: one tonal ladder, real in both environments, with glass
demoted to the two or three things that earn a floating layer.**

**The ladder, stated as a count, because the audit counts.** The shipped app has
**nine** distinct surface fill values against **four** tokenised — and one of the
five (a component that reaches into the token and multiplies it by 0.7) invents a
level nothing else knows about, while two components bypass the shared primitive
and re-implement the recipe inline. D declares **four** levels and says what
earns each:

| Level | What it is | What earns it | Light | Dark |
|---|---|---|---|---|
| **Canvas** | the ambient field | always, behind everything | a real tonal value, distinctly darker than any surface above it | the field, distinctly darker than any surface above it |
| **Surface** | a card, a panel, a row group | any group that is one thing | lighter than the canvas, with a hue-carrying shadow | lighter than the field, with translucency having something to reveal |
| **Raised** | a nested region inside a surface | a sub-group | one more tonal step, plus the shadow | one more tonal step |
| **Floating** | a navigation bar, a bottom sheet, a modal, a toast | **only** things that genuinely float above content | opaque, or translucency with a strong hue-carrying shadow | the existing translucent treatment, **demoted to here** |

**D's contribution to the glass question is the *distribution*, not the
material.** The locked design document's own clause is explicit: glass is a
special effect, not the foundation, and it is **not allowed as a default
treatment for every card, every row, navigation, entire screens and every
button.** The shipped app does exactly the thing the document forbids: the
translucency recipe is the design system's core primitive, the default card, the
default row, the navigation bar *and* the primary button. **So D's move brings
the product closer to its own locked design, not further from it.**

**And D takes the terminology seriously, because the audit is right that it is a
trap.** The name describes a material the app does not implement: the backdrop
blur value is zero, with the honest reason that no reliable cross-device blur API
exists at the minimum supported level, and the code's own KDoc says so. What
ships is translucency plus a hairline plus a top-edge highlight plus a soft
shadow. **D's naming decision is that the component family stops being named
after a material it does not have** — and this is stated as a *terminology*
decision for a later, ADR-bearing stage, not settled here, because Phase 2A
settles nothing.

**What D buys:**

- A real four-level ladder **expressible in Light**, which today it is not —
  this is the direct answer to the collapse of the Light surface ramp.
- Surface separation that works in both environments by different mechanisms.
- **A count instead of a taxonomy.** Four levels, four named reasons. The audit
  counts 100 inline alphas across 21 files, 9 fills, 7 hardcoded shadows against a
  dead elevation token, 3 progress-track tokens for one job, 4 icon-container
  treatments and 6 card constructions. **D's consolidation collapses roughly
  fourteen of those counts into four decisions**, and that is the audit's own
  highest-leverage structural item.
- The artwork survives, with its contract intact: one asset pair for the whole
  product, theme-selected by a pure function of the theme, callers passing band
  amounts and unable to art-direct it, no tint, no filter, no re-colour.

**What D costs, stated honestly:** a tonal ladder in Light is a *visible*
mechanism, and visible depth in Light is the first thing that goes wrong in a
hand-tuned light theme. It requires real measurement rather than eyeballing,
because a light canvas has nowhere to hide a wrong value. **D also keeps the
floating glass navigation bar, which the design document forbids as a default
treatment for navigation**, and D's defence is that a floating bar is the app's
recognisable chrome — but that defence is a *preference* against a locked clause,
and D says so rather than pretending the tension is resolved.

### 8.9 Cards

**The explicit answer: cards remain central, and a card becomes the quietest, most
disciplined object in the product — with one reserved tier above it.**

The shipped situation is a governance failure with numbers: the design system's
named card component has **one** production call site, six distinct card
constructions exist, and each screen assembled its own because a *surface
modifier* is not a component. The user-visible consequence is that a section
boundary, a card boundary and a row boundary are all the same thing, which is why
the Settings screen reads as five undifferentiated blocks and why the app's most
used label — 29 call sites at 16 sp Regular — is invisible.

**D's card has three rules and one exception:**

1. **One construction, one radius, one padding, one press policy** — a single
   card component that owns fill level, border, radius, padding and press
   feedback, with the surface modifier demoted to an implementation detail of it.
   This is the audit's named highest-leverage structural change, and D takes it
   whole.
2. **A list is not a stack of cards.** One card, with rows or divider rules between
   rows. This is the single highest-value consequence in the whole product,
   because the shipped Tasks list puts a 48 dp card *plus* up to two unlabelled
   48 dp management circles *below and outside* every card, so a ten-task list is
   twenty boxes, the management controls of a task are the loudest repeated
   element on a screen with **no completion affordance at all**, and two of those
   circles are visually identical, distinguished only by glyph.
3. **A section label is visibly a label** — its own register, distinct in size
   *and* weight from the content beneath it, so a group boundary is legible
   without reading.
4. **The exception — one reserved tier.** Exactly one thing per screen may use a
   higher-emphasis treatment: the primary figure, or a genuinely modal layer. The
   audit's finding is that *every* surface looks like the same surface, so nothing
   is emphasised; the fix is not a bigger card everywhere, it is **one** thing
   that is allowed to be louder.

**Radius: the existing small-to-medium vocabulary, varied by role rather than
uniform.** The audit finds three radii in the codebase that do not exist in the
design system's own eight-value vocabulary, including **the bottom navigation
bar's** — the most-seen component in the app — plus an 18 dp tab radius inside
it, and a 14 dp that appears once. D keeps the vocabulary and applies it by role.

**What D costs:** the quiet card is a **low-contrast** object, and low contrast
is exactly what the Light environment does badly today. D's Light depth
mechanism is what pays for it, and if that mechanism is under-built the quiet card
becomes the flat card. **This is D's central internal risk and it is the same
risk as the Light-depth risk**, stated once and in both places.

### 8.10 Iconography

**The explicit answer: D is deliberately indifferent, and it is the only direction
that can be delivered whatever Q6 turns out to be.**

The three-way conflict is factual. The **code** uses Material **Icons** through
the extended set — a static vector collection with **no weight axis**. The
**documentation** — three KDocs, the design system document, its master ADR —
names Material **Symbols** Rounded. The **concept pack** ships Symbols Rounded as
a *webfont* with a declared weight axis 100–700, and is a web artefact that is
**not a valid Android font resource** and is verified unreferenced by any build
rule, resource, document or ADR. The audit's finding is that **one recorded
decision was never honoured**, and that the consequence is functional, not
aesthetic: the variable-weight capability the concept assumes is unavailable, and
cannot be had without changing the icon source.

**D's position, in three parts:**

1. **D does not depend on a weight axis.** Its icon treatment is
   **weight-tolerant**: one family, one optical density, monochrome, at ink and
   at accent. Whether the underlying set has a weight axis or not, D's icons look
   the same. **D is therefore deliverable on the shipped Material Icons set today,
   and would be deliverable unchanged on Material Symbols later.**
2. **D does take a position on the icon *family* question — and it takes it as the
   thing the audit says it is: an open question.** D does not adopt the concept
   pack, does not migrate, and does not rename the KDocs. **If Q6 settles, D's
   design requires no change.** A direction that can absorb either answer is a
   direction whose iconography was designed rather than inherited.
3. **D does settle the *other* two sub-questions, because the audit shows they are
   answerable now and that leaving them open is what causes drift:**

   - **Habit identity is carried by a custom *treatment*, not by a custom family.**
     The audit's finding is that identity is entirely borrowed — zero custom icon
     iconography, twenty keys resolved to Material glyphs, and **three non-aligned
     vocabularies** (what the picker offers, what the renderer draws, what the
     colour map names) that have drifted so that a user who picks "Spark" gets a
     star and "Art" is colour-indistinguishable from a habit with a corrupted key.
     D collapses these into **one authoritative key registry** — key → glyph,
     label, colour, semantic group — that the picker, the renderer, the tile and
     the detail header all read, and it **gives the app's own custom-habit
     template a real glyph** instead of a fallback star in a fallback colour.
     **This is a decision D makes; a migration it does not.**
   - **The launcher mark's palette is reconciled to the theme's**, in whichever
     direction is chosen. The mark is the single most visible identity artefact
     the product owns, it draws in three hues that **appear in neither theme**, and
     its own comment claims a match to a background value it is two steps off in.
     A user spends the first two seconds of the product reconciling a mismatch, and
     D removes the mismatch. Its **adaptive-icon wiring and its monochrome layer
     are already correct and are kept as they are.**

**What D removes, explicitly:** the fire glyph. The streak becomes a number with a
state. The product's own design document calls streaks *"supporting information,
not the emotional center"* and rejects highly gamified fire and emoji treatments;
the app ships a flame on a 12 sp badge, and its own ADR records the resolution to
a fire glyph as a substitution of one form of the rejected pattern for another.
**D removes it, and D is the only direction of the four that can claim to be
*closer* to the locked design document by doing so.**

**What D gives up, honestly:** by refusing to engage with Q6, D also refuses the
opportunity. A variable-weight icon set is a real legibility tool, and a design
that took positions on Q6 might produce a stronger icon identity than one that
stays indifferent. **D trades that possible gain for the certainty that its icon
layer is not blocked on an unresolved question**, and states the trade.

### 8.11 Navigation

**D's navigation model: the same four destinations, the same floating bar — with
one thing fixed and one thing the direction cannot fix.**

**Kept.** Four destinations — Home, Habits, Tasks, Insights — is a locked decision
and D keeps it. The floating bar is a locked ADR, and D keeps it too, because a
controlled evolution does not re-open locked decisions. Calendar stays inside
Insights and Settings stays off the bar; both are locked and both are kept.

**Fixed.** The bar's own geometry is currently **six literals that exist in no
token** — a 26 dp bar radius, an 18 dp tab radius, 8/6 dp padding, a 26 dp icon
that matches neither of the app's two icon-size tokens, a 2 dp gap — on the
most-seen component in the product. And the four primary destinations of the app
are labelled in the app's **smallest** type. D fixes both: the bar's geometry
enters the token vocabulary, and its labels use D's caption register.

**The active state, made non-colour-differential.** The shipped bar already does
one thing right and D keeps it: a filled glyph when active and an outlined one
when inactive, which is the standard convention and is applied consistently. D
keeps the fill/outline pair *and* the weight change *and* adds a positional
indicator, so the active destination is signalled three ways. The shipped
convention is already "familiar because it is Android" and correctly so.

**The transition, which is D's real navigation work.** The shipped app's dominant
interaction is row → detail, and it has **no spatial model**: the transition is a
200 ms crossfade, forward and back are identical, and the destination sits behind
an asynchronous gate that puts a Material spinner on **every** detail navigation
in the product. D fixes all three, and the third is what makes the first two
possible:

- **Remove the gate**, so the destination's structure is known before the
  transition begins.
- **Use the shared element the code already contains.** An activity's accent tile
  is 44 dp in a list and 72 dp on its detail page — the same component in both
  places, unanimated. **D animates the relationship the code already has.** This
  is a Compose shared-transition answer, Android-native, not a web animation.
- **Give back its own transition**, so pushing and popping are finally
  distinguishable.

**What D cannot fix, stated plainly:** the design document forbids glass as a
default treatment for navigation, and a decision record locks the floating glass
bar. **D keeps the bar and names the disagreement rather than resolving it.** This
is a genuine unresolved tension between two locked sources, and D is not entitled
to resolve it, so a reader should treat it as open in D too.

**Inset contract — the one D states outright, and the one the audit asks for.**
**A single shell contract owns three things: the top-app-bar inset, the content
inset, and the floating-chrome clearance — and it is applied at the Scaffold, not
per screen.** "Draws behind the status bar" is therefore a **property of the two
artwork screens**, declared as such, rather than a decision re-derived inside each
screen file. The consequences are: a screen title sits on the same margin as the
content it titles, on every screen; no screen reserves chrome by measuring
against its own viewport, so a habit's detail page can no longer reserve a fixed
block of a possibly-small display before the habit's name appears; and the
floating bar's clearance is a stated band that the list composes against rather
than hoping to avoid. **D names this as the direction's answer to the three
coexisting inset strategies the shipped app carries, and treats it as a shell
contract rather than as a visual preference.**

### 8.12 Home / Today

**Audit verdict is REIMAGINE. D's reimagining is to make the hero shrink to its
content, put the answer in the largest type on the screen, and make the 100%
moment a real event.**

**Purpose.** Answer *"what must I do today, and how am I doing?"* in one glance,
in both environments, at 200% font scale.

**Hierarchy, strict, in this order:**

1. **The figure.** "3 of 5" at primary-figure size with a real 700, the
   denominator smaller and lighter, tabular. **The largest element on the
   screen**, and larger than the greeting. The shipped screen's stack runs the
   other way: date 13 sp, greeting 20 sp, percentage 20 sp, **"3 of 5" at 16 sp
   Regular**, message 14 sp — priority exactly backwards, with the answer in body
   text and the emotional line carrying more weight than the fact.
2. **A one-line state sentence** in body, generated from the user's data — the
   thing at risk, or the last remaining item. **The shipped message slot is
   removed**, because four of its six branches reference no user data and one is
   the product's own tagline used as an everyday state line, so it says the same
   thing all day while the one line that *does* change sits above it, smaller.
3. **Date and greeting**, in one line, below the figure, at display weight.
4. **The record.** One card, rows separated by dividers, incomplete first in the
   user's order, complete below its own labelled group — and completing a row
   makes it **glide** between groups, which is the one piece of item-level motion
   in the entire shipped product and is genuinely good. D keeps it.
5. **The week at risk**, one line, present only when true.
6. **The hero artwork**, kept, and **sized to its content**: a day with three
   activities and a day with twelve produce genuinely different compositions.

**Composition.** One continuous surface. The artwork is a *collapsing header* with
a documented contract, not a screen-specific machine — and D's first act is to
adopt the app's **own** conclusion, which is recorded in its own source: the
222-line collapsing composition on another screen is named as *"the reference
composition"*, while the alternative built on the harder screen is 771 lines with
three custom draw layers, a 24-sample blend, a 48-sample veil, five anchor
constants and a weighted spacer that expands to fill 72 dp of headroom, arrived at
through eight issues and nine ADRs, with a documented history of the same margin
landing in the wrong place three separate times. **D uses the reference.** The
audit's open question about how much of that machinery is load-bearing is answered
here not by analysis but by *not needing it*.

**Primary visual.** The figure, over an ambient field whose intensity tracks the
day's progress.

**Key components.** Figure; state line; the artwork header; the record card; a
48 dp completion disc per row; live target progress at a secondary-figure size; a
run count with a state; the state-responsive field.

**Data shown.** The five eligible items, three complete; "5 of 8" and "0 of 20";
run lengths 3, 1, 6, 12, 0. **No statistics, no month view, no trend, no
comparison, no habit library** — the locked design document forbids a full habit
library on Home and forbids detailed analytics on Home, and D obeys both.

**Interaction emphasis.** The completion disc, the target entry, and a **reserved
metadata line** so a row is the same height whether or not it carries a label —
fixing the shipped 44/62/84 dp row-height variance that makes a list of cards a
ragged stack.

**Distinctive visual behaviour — the 100% day.** The shipped app's entire
day-completion celebration is one long-press haptic plus a snackbar reading
*"All done - momentum secured."*, while a milestone state and a reward duration
exist, are documented and are unreachable, and while the hero simultaneously
displays *"Everything complete. Rest easy."* — the same news twice within 200 px.

**D's version: the field changes state.** The ambient field moves to its
strongest state, the figure settles at primary-figure scale, one deliberate
haptic, and a single line states what the day produced. No confetti, no particles,
no sound, no toast. **The milestone state and the reward duration are wired rather
than invented** — the vocabulary already exists and the audit is explicit that the
hook is there and the behaviour is not. And it is **earned by a milestone, not by
a completion**, which is the only way to satisfy the locked "no routine confetti"
rule and the project requirement for a *streak celebration* at the same time.

**The empty day, which is the same problem in all four directions and the most
important state on this screen.** On a day with nothing scheduled, the shipped app
shows a roughly 300 dp photographic surface containing a date, a greeting and
nothing at all — a weighted spacer expands to fill 72 dp of headroom — above a
card that says nothing is scheduled. The composition spends its most expensive
real estate on its least informative state, and the app looks unfinished on
exactly the days it is least needed. **D's rule: the artwork is withheld on an
empty day, and the empty state moves into the top of the screen** with its copy
promoted from a card below to the position the greeting occupied. The art contract
is preserved — one pair, theme-selected — and it is simply not drawn when there
is nothing to say.

**Light-mode considerations.** A real tonal ladder; hue-carrying shadows; the
hairline as a secondary separator; the field at a higher intensity than in Dark;
and **the contrast question answered by measurement, not by assertion** — each
text tier measured against the canvas *and* against the hero artwork, with the
remedy at the text token rather than at the veil.

**Dark-mode considerations.** Tonal lift, the white top-edge highlight retained,
translucency with something to reveal, the field at a lower intensity, and
**state responsiveness the shipped product never has**: one system, responding
everywhere, rather than a static app-wide field with a second state-responsive
field pinned to its lowest intensity on two of its three screens.

### 8.13 Habit Experience

**D's habit experience is the smallest delta and the largest correctness gain in
the document: the existing three-state mark is correct, and D's work is to
propagate its vocabulary to the two places that get it wrong and to surface the
two metrics the engine already computes.**

**The day mark, unchanged and made canonical.** Completed is the filled 32 dp disc
with a check; incomplete is the 1 dp ring; not scheduled is the 8 dp muted dot in
a 32 dp footprint. **D does not redesign it.** The audit calls it the template the
product's entire visualisation layer should have been built from and the strongest
single argument that the problem is consistency, not capability.

**Two things D adds to it, and both are additions of *reach*, not of design:**

1. **A fourth state — missed.** Scheduled and not completed. The calendar's day
   cell has four branches and none of them tests it, so a day with five scheduled
   and none completed draws **identically** to a day with nothing scheduled —
   even though the scheduled count is right there in the same record and is used
   in that cell's spoken description. The weekly bars discard the same count. A
   fourth state is what makes "which day did I miss" answerable on the two screens
   whose entire job is answering it.
2. **A spoken description on the habit week**, which the existing day mark does not
   have. The product contains the model for this and does not use it elsewhere:
   the calendar's simpler day cell merges its descendants and composes a real
   description — *"March 4, 3 of 5 complete"* — with a role and an action. **D
   applies the product's own best accessibility engineering to the product's own
   best data mark**, which is the shortest path from *the app knows how* to *the
   app does it here*.

**Streak — a number with a state, and the fire is gone.** The shipped streak is a
fire glyph, tinted amber, at 12 sp, sitting in a row's text column, with a colour
change at every multiple of seven. D's streak is:

- a **figure at secondary-figure size — larger than the habit's name**, which the
  shipped app gets backwards at 12 sp against 20 sp;
- **a state**: rising, steady, or *closes tonight*. **"At risk" is the only state
  that earns motion**, and it is the only state that needs a colour, a mark and a
  sentence, because it is the only one the user can still act on;
- **current larger than longest**, because current is live and longest is archive.
  The shipped page prints them at 16 sp and 12 sp on adjacent lines, which is
  right, and D preserves it as a *size* relationship rather than a weight one.

**D removes the fire outright**, and this is the direction's clearest single act of
alignment with the locked design: the document rejects fire and emoji streak
gamification, and the app's own decision record shows the fire glyph was
substituted for the rejected emoji form. **A number with a state replaces both.**

**Consistency, and the two metrics the engine already computes.** A pure, typed,
unit-tested engine computes, per activity: current streak, longest streak, total
completions, **missed occurrences**, and a **consistency score** documented as the
percentage of scheduled occurrences hit over the last 30 days and named in the
product specification. **Both of the last two have zero presentation call sites in
the entire application.** For a product named Momentum, the one number that would
tell the user whether they are consistent is never shown, and the missed count —
which is what makes any calendar or heat view mean anything — is never shown
either.

**D shows both, with their windows printed beside them, and a trend that is a
signed figure with a window rather than a lowercased enum name** — because today a
user can literally read the words *"Insufficient data"* as the value of a row
labelled "Trend". D prints a dash, and nothing else.

**The window — and the honest data dependency.** The habit-overview use case
hardcodes a seven-day window and takes a set of completion *dates*, discarding each
record's value at the use-case boundary — so a "drink 8 glasses" habit's page
cannot show that you averaged 6.2, and the only place in the product showing more
than seven days of history is a *task* detail screen. **D proposes a 7 / 30 / all
control**, which requires a window parameter the use case does not have, and
states that as a data-shape question this document cannot answer. D does not
assume it and does not depend on it: **the seven-day view is what works today and
what the briefs depict**, and the wider window is stated as a dependency.

**Paused — the rule the code already wrote and then broke twice.** The shipped row
**never dims its own text**, with a documented reason: fading the whole card
dropped body-text contrast below the 4.5:1 floor in the light theme, so "quieter"
is expressed through the surface instead of an alpha layer. That rule is stated in
the code and then **violated in two places anyway** — the whole card dropped to
0.85 alpha on the management list, and the identity block and the metadata row
dropped on the detail page. The habits specification asks for *"slightly reduced
visual emphasis"* and explicitly says *"Do not gray out or disable the entire
card."* **D applies the app's own documented rule everywhere, using surface and
icon, never whole-composite alpha** — a one-mechanism fix to a learned-once,
transferred-nowhere lesson.

**Feedback.** The existing completion disc's spring pop is kept, because the audit
identifies it as the product's one genuinely well-behaved stateful control where
the visual state, the semantic role, the accessibility label, the haptic and the
motion all agree, and where the label was written as a *promise about behaviour*
rather than a description of a shape. **D keeps that contract intact as a constant
across every direction in this document**, per the audit's process recommendation.

### 8.14 Tasks

**D's Tasks answer is the smallest in the document, and it is mostly about
vocabulary and about one button bar.**

**The vocabulary leak, which is the defect a user actually sees.** The Tasks tab
is the shared activities list filtered to tasks. On an empty Tasks tab it reads
**"No activities yet"** and offers **"Create activity"**; the floating action
button's label says **"Add activity"**; the sheet says **"Create item"**; the
sheet's own options say **"Habit"** and **"Task"**. **Four names for one action
across three surfaces, and the word "activities" appears on a tab labelled
"Tasks".** D's fix is one noun per surface, one verb per action.

**The button bar.** Every row carries up to two unlabelled 48 dp circular
management buttons *below and outside* the card, so a ten-task list is twenty
boxes, the Tasks row is roughly twice the Home row's weight, the management
controls of a task are the loudest repeated element on a screen that **has no
completion affordance at all**, and **two of those buttons are visually identical
to each other**, distinguished only by glyph. **D collapses the bar to a single
overflow affordance at the end of the row** — the same model the management screen
already uses correctly, with status-appropriate actions and a confirmation that
names permanence. Row weight halves and the two identical buttons stop inviting
mis-taps.

**D does not add a completion control to Tasks, and says why.** The architecture
already implies management belongs on Habits and action belongs on Home, and the
habits specification states the division as a principle. Tasks is a management
surface wearing a tab's label; **D makes it look like one** rather than pretending
otherwise. The deeper question — whether the product needs a separate Tasks tab at
all — is a **product** question the audit left open, and **D does not answer it**,
because the destination count is a locked decision and a visual direction does not
get to re-open it.

**The search field.** The app's only text field leaves its unfocused and focused
text colours, its label colour and its disabled text colour to Material defaults
drawn from a palette this product does not use, because only fifteen theme slots
are mapped and every unmapped slot keeps a library default. **D's field is a
Momentum-owned component** with every colour routed through the token layer —
which the audit names as the cheaper of the two available fixes and the one in
keeping with the existing architecture.

**The two unlabelled filter axes.** The screen stacks two **visually identical
rows of pills** — status, then category — with no label distinguishing the axes,
on a component that also serves two unrelated settings elsewhere, so a filter and
a setting are visually identical. **D labels the axes** and gives the filter and
the setting different forms. It is a small change and it fixes a real misreading.

**The dialogs.** All five dialogs in the shipped product are **destructive
confirmations**, and all five are translucent, using the shared dialog's most
translucent level — a pattern **the app's own test asserts is a defect**, having
found and fixed it in the one bespoke dialog. There are also three different
dismiss verbs for the same slot across the five, while the clearest copy in the
product — naming the archive alternative — sits inside a panel the user can see the
list through. **D promotes the correct dialog** — opaque, explicit scrim,
contrast-checked content, action-naming labels — to the shared component, unifies
the dismiss verb, and gets the promotion for **almost no design work at all**,
because the reference implementation already exists and already passes.

**Empty state.** *"No tasks yet."* + one line that says something true + one
action. Not *"Create something worth showing up for"* — the only unhedged
motivational line in a list context in the product, naming nothing about the user.

### 8.15 Insights

**D's Insights is the screen where the "computed and invisible" finding is
largest, and where the fix is almost entirely layout rather than invention.**

**The measurements that define the problem:** the view model loads **60 days** of
completion history and the chart draws **7 bars** — thirty of the sixty are
consumed by the thirty-day rate and the per-activity windows, and days 31–60 are
discarded at the view boundary. The **only** figure in the whole application set
above 20 sp is the **seven-day rate**, while today's completion is 20 sp with an
out-of-family weight, a streak count is 12 sp, a habit's live target is 12 sp, the
calendar's own summary is 12 sp and the habit page's week is 13 sp. **The product
is twice as emphatic about the past week than about right now.** Four data-mark
vocabularies coexist on this one screen. The two lists where a *ranking* is the
whole value are **both unsorted** — categories come out in map-iteration order,
and the section named "most consistent" shows only the best performers with no
contrast against the worst.

**D's structure, in the design document's own required hierarchy — question → key
number → mark → interpretation:**

1. **The question, as the screen's heading.** *"How consistent have you been?"*
   The shipped screen opens with a number and a bar chart and never says what it
   is for.
2. **The key number: the consistency score**, at primary-figure size, with its
   **window printed beside it in words** so it cannot be misread as a lifetime
   value. This is the number with zero call sites, named in the product
   specification, and the number that defines this product.
3. **The mark: the 30-day strip, in the canonical four-state vocabulary.** One
   mark family, four states, at four sizes, across the habit week, the home figure,
   the 30-day strip, the calendar cell and the category channels. **The day-rate
   record already carries the scheduled count that the shipped weekly bars
   discard**, so the missed-versus-rest distinction is **available in the data
   today and simply not drawn.** This is the shortest fix in the entire audit and
   the highest-value one.
4. **The comparison.** The seven-day rate as a **secondary** figure, with the
   **signed delta against the thirty-day average** — which the shipped screen
   already does and does well — given a weight, a mark and a window.
5. **The ranked lists, actually ranked**, worst first when a category is at zero,
   because the value of a ranking is comparative and unsortedness discards it.
6. **The interpretation**, in one body-size sentence generated from the user's own
   data, plus the missed count.

**What D does not do:** a dashboard wall. The shipped screen's 2×2 grid of four
equal stat tiles is why it fails the locked acceptance criterion that Insights
must feel analytical without becoming a dashboard wall, and the stat-tile
component's own contract — two strings and an optional string — has no way to
express rank, trend, or magnitude relative to anything, which is why four numbers
in a grid tell the user nothing about which one is bad. **D replaces the grid with
one key number, one mark and one sentence**, and the surface area saved goes to
the two invisible metrics.

**The empty and insufficient states.** Insufficient data prints a dash, not a
lower-cased enum name. An empty Insights state is a real composed state, not a
generic glyph — the shipped empty-state component takes an icon parameter and
every caller passes a Material glyph that names the subject, including a star that
is *also* the universal fallback for a habit the app failed to read, so the app's
most important empty state and a corrupt record are the same picture.

### 8.16 Onboarding

**Audit verdict is REIMAGINE. D reimaginin onboarding by moving the arguments,
not by replacing the medium — and by making the medium obey the theme.**

**The conceptual argument, in four beats, which is the same argument all four
directions make because it is the one the shipped onboarding fails:** the four
current bodies contain **zero** facts about what the app does — no Activity
concept, no streaks, no progress as a mechanic, no offline operation, no
on-device storage, no account, no cloud, no tracking — while the product's
best-written line, *"Everything stays on this device. No account, no cloud, no
tracking — offline by design,"* sits in Settings behind an icon on a screen a
first-time user does not visit. The app describes itself in the third person
inside its own UI, calls itself *"a calm and powerful app"*, offers *"create the
life you want"* and *"your journey to a brighter tomorrow starts now"*, and one of
its four headlines is the only one in title case without terminal punctuation.

**Page 1 — what it is.** One line naming the Activity concept in the user's own
terms. **Page 2 — what it does for you.** Progress, streaks, and the one number
that says whether you are consistent. **Page 3 — the difference nobody else can
claim.** Offline, on-device, no account, no cloud, no tracking, no ads. **Page 4 —
start.** One action.

**The theme decision, which is the real work.** The shipped onboarding is
**theme-blind**: zero reads of the theme system in its files; four photographic
artworks with **no Light variant**; its own fourteen-constant palette; its own ink,
because the headline colour is a warm off-white from a different palette rather
than the product's own; a bespoke per-page readability exception, with a drop
shadow on page one only, on a font that carries no shadow anywhere else in the
product; two disagreeing transition layers per page change, using three durations
for one transition; and a `DisposableEffect` that force-sets the light status-bar
appearance and restores it on dispose — **code that is correct and well-commented
and that exists precisely because onboarding does not know what theme it is in.**

The consequence a user meets: **a first-run user who chose Light walks through
four near-black photographic screens and then arrives at a light, cool-grey app.**
The first thing the product shows and the thing it becomes are two different
visual products. And the root cause is recorded in the audit as a decision with a
now-defunct consequence: the vector illustration sources that would allow a Light
variant still exist in the repository, are unreferenced by any build rule, and
were flattened into photographs by a migration — so onboarding **can no longer be
recoloured, rescaled, re-themed, or given a Light variant**, because the vector
originals are no longer in the build.

**D's decision: onboarding is the app, not a layer above it.** It therefore
inherits the theme system, and — because the vector sources exist — producing a
Light artwork is a **build-time possibility, not a commission**. One transition per
page change, the copy on the token layer's ink, the status-bar appearance derived
from the app's own theme rather than hand-patched, and the artwork chosen by the
same pure theme function the rest of the app uses. The illustrations that were
baked into the photographs are recovered as sources, or the flattened art is kept
and the unused vector files are deleted — **and the middle state, an incoherent
4.2 MB "set" that looks like a design asset in the repository while nothing reads
it, is the worst of the three options and D rules it out.**

**This is where all four directions agree**, and the agreement is reached
independently, from four different metaphors. That convergence is a *finding about
the argument*, not convergence of the four directions, which remain four products
with four different visual languages, four different Light mechanisms and four
different answers to Q3.

### 8.17 Motion

**D's motion personality: change-triggered, never wall-clocked — and this is the
one place where D is genuinely radical rather than consolidative.**

**The measurements.** Thirty-one animation call sites across nine files. The one
motion token the app uses consistently is a six-second *ambient* value whose sole
purpose is two decorative infinite loops. **Six of the nine motion tokens have zero
production call sites**, and **sixteen hand-written durations have replaced them**;
eight raw springs have replaced the two spring specs the design system provides
specifically so that a damping ratio can be set, and **none of those eight sets
one**, so all of them take a library default — which the audit names as the
difference between *"someone chose this feel"* and *"whatever the default was"*.
On Home, with any progress recorded, **two infinite transitions run continuously**
for a number that changes a few times a day, and the app's own documentation calls
the ambient field *"state-responsive, not decorative wallpaper"* while the field the
whole app sits on **has no state parameter at all**.

**D's inventory, which is the audit's "what to vary" list answered:**

| Motion | Trigger | Duration | Under reduced motion |
|---|---|---|---|
| **Field state change** | **the day's progress changing state** — this is the "state-responsive" promise made real, and it is the direction's biggest single motion change | a slow, single transition; no drift loop | the field changes state instantly, with no transition |
| **Figure resolution** | any figure's value changing | short, 150–200 ms, **springs with an explicit damping ratio** | instant |
| **Completion pop** | an item completing | the existing spring, kept | a zero duration — a substitution, not a shortening |
| **Row glide** | a row moving between the incomplete and complete groups | the shipped behaviour, kept — it is the product's only item-level motion and it is good | instant relocation |
| **Reward** | the **day** reaching 100% | the already-declared reward duration, **wired for the first time** | a single static state change, with the field moving and nothing else moving |
| **Ambient continuous** | **none. D removes both loops.** | — | — |
| **Page turn** | pushing a destination | a shared-element container transform, once the gate is removed | no transform |
| **Back** | popping | the same transform reversed | no transform |

**D's two motion contributions, and they are the reason D's motion section is not
just consolidation:**

1. **The ambient field becomes state-responsive and stops being a clock.** The app
   has a four-state field model with a declared intensity floor and ceiling, a
   component whose draw ordering is a correctly reasoned solution to *"the glow
   must never veil the value"*, and a *third* background field with **no state
   parameter at all** wrapping the whole app, while two of the three screens that
   use the state-responsive one are **pinned to its lowest intensity**, so they run
   it at its least visible by construction. **D merges the two ambient systems into
   one, gives it the state parameter, and raises the two pinned screens.** It then
   makes the strongest state **reachable**, because a four-state system whose
   strongest state cannot occur is a diagram, not a system.
2. **The reward vocabulary is implemented rather than deleted.** A milestone state
   and a reward duration exist, are documented, and have zero call sites; no
   confetti, particle, burst or flash exists anywhere; and the app uses a
   *long-press* haptic constant as a generic event and a *text-cursor* haptic
   constant as a selection tick, which is the intent being right and the constant
   being wrong. **D wires the two hooks and uses one deliberate haptic.** This is
   the shortest path from *"the product's most satisfying moment is a
   notification"* to a real moment, and it needs no new architecture — the
   celebration effect is already a typed one-shot channel.

**Reduced motion, and the truthfulness failure.** The Settings copy promises
*"Calm animations throughout the app"* while the setting reaches **one of
fourteen** motion call sites — the other thirteen pass no override, so a user who
needs reduced motion is told it was applied and it was not, on thirteen surfaces
including the navigation bar, the transition system, every press feedback, the
completion control, the progress orb, the ambient field on three screens, and all
four onboarding pages. **For a user with a vestibular disorder this is the most
consequential defect in the audit, and it is a truthfulness failure, not an
aesthetic one.** **D threads one override through the theme so all fourteen sites
read one source** — a small, bounded, already-diagnosed correctness change — and
**D writes the claim only if it is true.** The audit is explicit that a
source-text test currently pins one of those call sites to the null form and would
need updating alongside the fix, and D names that rather than discovering it in
implementation.

**The two correct patterns already in the codebase, extended rather than
replaced:** gating a glow so that 0% pays nothing, and returning a constant so
that a disabled glow composes no transition at all. **D's rule: if a value is
zero, nothing animates, ever.**

### 8.18 Light / Dark

Described **independently**, because D's whole contribution is that they stop
being the same design at two luminances.

**LIGHT — a real tonal environment.**
The canvas is a genuine tonal value, distinctly darker than any surface above it.
Surfaces are **lighter** than the canvas, and there are **four or five genuinely
distinct values** where today there are three, with the surface and the elevated
surface no longer being the same colour. Depth is **tonal plus a hue-carrying
shadow** — the shadow is tinted to the canvas's own hue rather than being pure
black, because a pure-black shadow at low alpha is invisible on a light canvas and
a *tinted* shadow at the same alpha is visible, since it changes hue as well as
luminance. The hairline is real and warm, and it is the **secondary** separator
rather than the primary one. **The white top-edge highlight is removed**, because a
white highlight on a near-white surface is a contradiction rather than a highlight.
The ambient field runs at a **higher** base intensity than in Dark, because a
light canvas needs a visible field to be a field at all. The hero artwork keeps its
**Light** variant — one of only two photographic surfaces in the product that has
one, and the other has no Light variant at all.

**DARK — tonal lift, plus the thing it never had.**
The canvas is a deep field; surfaces are lighter than it, so a translucent fill
produces a **genuine lift** and translucency has something to reveal. The white
top-edge highlight is retained, because on a dark surface it reads. The hairline is
cooler and lower-alpha. The ambient field runs at a **lower** base intensity than
in Light, because on a dark field any field reads. And — the change that matters —
**the field responds to the day's state everywhere, at a real intensity, including
a reachable strongest state.** Today three screens pin it to its lowest value and
the app-wide field has no state parameter at all.

> **Mechanism statement, made explicitly because Q1 requires it:** D's Light
> environment expresses depth as **a tonal ladder plus a hue-carrying shadow**,
> which requires a light canvas — a tinted shadow is only visible against a surface
> lighter than the shadow. D's Dark environment expresses depth as **tonal lift
> plus a white top-edge highlight**, which requires a dark canvas — a white
> highlight is only visible against a surface darker than it. The layout, the type
> scale, the mark vocabulary, the card construction, the navigation model and the
> motion language are identical between them.**

**D holds the two environments to the same ten acceptance criteria the design
document already states**, and it holds them by construction rather than by
inspection: the two share their entire structural system and differ only in
mechanism. The criterion that Light and Dark "feel like the same product" is met
by sharing everything except the depth mechanism; and the audit's specific
complaint — that no hue is introduced, removed or substituted between the two, so
no role changes and nothing is expressed in one that cannot be expressed in the
other — is answered by giving the two **different mechanisms**, which is a
structural difference rather than a re-valued one.

**The one unresolved contrast question, preserved as unresolved.** The Home date
label measures **3.11:1** over sampled Light hero artwork against a 4.5:1 floor,
and the recorded decision is that the remedy is the **text token**, not
re-inflating the veil — and that the token has **74 call sites**, so the remedy is
expensive. **D keeps the artwork, which is the only surface in the product where
text is measured against an image, and therefore keeps this exact risk.** D's
position is that the risk is a *measurement* problem with a known remedy and a
known cost, not a reason to delete the artwork, and D states the cost rather than
pretending the fix is cheap.

### 8.19 Signature Element

**The state-responsive background — the light in the room that knows what time it
is.**

On the shipped app, the ambient field is a static three-field gradient with no state
parameter, wrapping every screen; a second, state-responsive field sits inside
three destinations at its **lowest** intensity on two of them; the field's strongest
state is **unreachable in production** and appears only in a preview; four declared
accent hues have **zero** call sites; and the two systems use different third hues
and different alphas, so three screens — the three carrying the product's signature
content — look different from the other eight for no stated reason.

**D's signature is that there is one light, it is state-responsive, it is
everywhere, and on the day you finish everything it is unmistakably different.**
And at 100% it **moves** — the reward moment D wires to the two hooks that already
exist and that have never been reachable.

**Why this is the signature and not the numeric register, even though the numeric
register is D's larger structural change:** the type fix is *invisible as a
gesture*. Nobody looks at a number and thinks "this was designed". The light is the
only thing in the product that a user experiences without reading — and it is the
only thing in the product that is *named after the thing the app is called*. D's
secondary signature is therefore also invisible-but-total: **the streak is a
number, and it is bigger than the name of the thing it belongs to.**

### 8.20 Risks

Honest failure modes, stated as failure modes.

1. **D is familiar, and familiar is forgettable.** Every one of D's choices is
   defensible today, and most are already in the product's own documentation. A
   rendered set of D screens can read as *"a tidier version of what we had"* rather
   than as a product with a point of view. **D does not defend against this by
   adding novelty — which is deliberate, because adding novelty is how a controlled
   evolution becomes a fourth radical direction. It defends by being unusually
   correct about the one thing nobody notices until it is right. The risk is real
   and it is the risk most likely to be realised.**
2. **The Light tonal ladder is the whole ballgame, and it is unverified.** A
   visible tonal depth mechanism in Light is a hand-tuned light theme, and visible
   depth in Light is the first thing that goes wrong. It requires real measurement,
   because a light canvas has nowhere to hide a wrong value, and the one measured
   contrast datum in the repository is a **Light-specific failure**. **If D's Light
   mechanism is under-built, D's quiet card becomes the flat card and D has
   reproduced the defect it set out to fix.** This is D's central internal risk, and
   it is the same risk as its Light-depth risk — stated once, in both places,
   deliberately.
3. **D keeps the floating glass navigation bar against an explicit clause in the
   locked design document.** The document forbids glass as a default treatment for
   navigation; a decision record locks the floating glass bar. D keeps the bar and
   names the disagreement rather than resolving it. **This is a genuine unresolved
   tension between two locked sources**, and D is not entitled to resolve it, so a
   reader should treat it as open in D too.
4. **D's consolidation is worth nothing if the governance is not also fixed.** D
   replaces roughly fourteen scattered counts with four decisions, and the audit is
   explicit that the app looks hand-made in twenty slightly different ways because
   **the token layer does not model what the UI actually varies**. If the *rules*
   are not enforced — if the alpha system, the elevation token, the surface levels
   and the press policy remain a matter of per-screen judgement — then D's four
   decisions will re-decay into twenty local ones. **A direction cannot fix
   governance by specifying better values.** D's value is that its decisions are few
   enough to be enforceable, and that is a weaker claim than it sounds.
5. **D keeps the hero-machinery question open in the one place it could have
   answered it.** D adopts the app's own recorded conclusion — that the smaller
   collapsing composition on the other screen is the reference — which is a strong
   answer. But the audit's question is *which layers of the larger one still
   prevent an observed defect*, and D answers it by not needing it rather than by
   establishing it. **A reviewer could reasonably ask for the evidence.**
6. **The proposed window control needs a use-case signature that does not exist.**
   The 7 / 30 / all control on a habit's page requires a window parameter the
   habit-overview use case does not have, and per-day values that do not currently
   leave the repository. **D does not depend on it** — the seven-day view is what
   the briefs depict and what works today — but D's most valuable habit-page
   improvement is contingent on a data-shape question this document cannot answer.
7. **D enlarges the numbers and therefore compresses the words, on a screen whose
   job is a list.** With a dozen activities eligible, a larger primary figure means
   fewer rows above the fold. D accepts a modest loss of density in exchange for
   the answer being legible, and the trade is defensible — but it is a real cost on
   exactly the days the product matters most, which are the crowded ones.
8. **D's signature is subtle, and subtle signatures do not survive being
   described.** A background that brightens as the day fills is a lovely behaviour
   and it is invisible until you notice it, and it is not what a user would name
   when asked why they kept the app. **D's name-argument is therefore carried by
   background behaviour rather than by a figure**, and D states the consequence
   plainly rather than claiming the light-field
   answers the "what is Momentum" question as well as a ledger, a multiplication or
   a woven run does.
9. **D is a controlled evolution of a product the audit says is *systematically*,
   not cosmetically, undecided.** The audit's own headline structural conclusion is
   that the genericity is a governance symptom, and D's answer is a governance
   answer. **That is the right kind of answer.** It is also the answer that is
   hardest to see in a rendered image, because governance is invisible by
   construction — a well-governed screen looks like any other well-made screen. **D
   may be judged as "fine" on the strength of its images while its actual
   contribution is invisible in them.** A reader who judges D on the rendered output
   alone will under-read it, and this document says so.

**The single unknown D most needs, and the branches:**

> **Unknown: whether a genuine Light tonal ladder is achievable at the contrast
> floors this product holds, or whether it will read as a washed-out surface on a
> mid-range display.**
> *If it is achievable* — D's Q1 answer is correct, the quiet card pays for
> itself, and the direction's whole argument — that fixing the foundation unblocks
> every other screen — holds.
> *If it is not* — D falls back toward the shipped condition, where a hairline does
> all the work in Light, and **D loses the thing that distinguishes it from the
> product it already is.** In that branch D's contribution reduces to the numeric
> register and the mark vocabulary, which are real but are not the same claim.
> **Neither branch requires different screens. Both require a rendered image in both
> environments to choose between**, which is Phase 2B's work and not this stage's.

### 8.21 Aurora, Governed — the five screens, as visual concepts

Same content, same fixture (§4.6). Tuesday 29 September, five eligible, three
complete.

#### Screen 1 — Home / Today

- **Purpose.** Answer *"what must I do today, and how am I doing?"* in one glance.
- **Hierarchy.** (1) **The figure "3 of 5"** at primary-figure size, real 700, the
  denominator smaller and lighter, tabular — the largest element, larger than the
  greeting. (2) One data-derived state line in body. (3) Date + greeting, one line,
  at display weight, below the figure. (4) The record card, rows separated by
  dividers, incomplete then complete, with a labelled group rule. (5) The
  at-risk line.
- **Composition.** One continuous surface. A **collapsing artwork header sized to
  its content** — three activities and twelve activities produce different
  compositions. One scroll surface, no nested scrolling.
- **Primary visual.** The figure, over an ambient field whose intensity tracks the
  day's progress.
- **Key components.** Figure; state line; the artwork header; the record card; a
  48 dp completion disc per row; live target progress at a secondary-figure size;
  a run count with a state; the state-responsive field.
- **Data shown.** Five eligible, three complete; "5 of 8" and "0 of 20"; run
  lengths 3, 1, 6, 12, 0. Nothing else.
- **Interaction emphasis.** The completion disc; the target entry; a reserved
  metadata line so every row is the same height.
- **Distinctive behaviour.** A completing row **glides** to the other group. The
  ambient field **changes state** as the day fills. At 100% the field moves to its
  strongest state, the figure settles, one deliberate haptic, one line — no toast,
  no particles.
- **Light considerations.** A real tonal ladder; hue-carrying shadows; the field
  at a higher intensity; the hero artwork in its **Light** variant; every text tier
  measured against the canvas *and* the artwork, with the remedy at the text token.
- **Dark considerations.** Tonal lift; the white top-edge highlight retained; the
  field at a lower intensity; **state responsiveness everywhere, at a real
  intensity, with the strongest state reachable.**

#### Screen 2 — Habit Detail (Drink water)

- **Purpose.** One habit's configuration, week, run, consistency — with the fourth
  state and the two invisible metrics.
- **Hierarchy.** (1) The habit's name. (2) A tracked section label **"THIS WEEK"**
  with the answer "**4/6**" at a secondary-figure size, **larger than the label**.
  (3) The seven-day mark row in the canonical vocabulary: filled disc, ring,
  8 dp dot for not-scheduled, and — for the one missed scheduled day — a **struck
  ring**. (4) The run count at a secondary-figure size with a state word
  (`12 · closes tonight`, and `longest 19` one step down). (5) The **consistency
  score** at a secondary-figure size with its window. (6) The **missed count**.
  (7) A labelled configuration group. One action.
- **Composition.** Column; the week row full content width; the configuration as
  one card with rows, not a tile grid.
- **Primary visual.** The seven-day mark row.
- **Key components.** Name; section label with a larger answer; the four-state
  mark row; run count with a state; consistency figure; missed count; a
  configuration card; one action.
- **Data shown.** Week 4 of 6 scheduled hit, 2 not scheduled, 1 missed · run 12,
  longest 19 · consistency 78% (30 days) · missed 6 · Active.
- **Interaction emphasis.** The window control (dependent on a use-case change this
  document cannot make). Everything else is read-only.
- **Distinctive behaviour.** None.
- **Light considerations.** The figure scale carries the hierarchy, not colour.
- **Dark considerations.** Same; the accent is completion only, and the accent is
  never the progress hue.

#### Screen 3 — Tasks

- **Purpose.** Manage tasks, as a management surface.
- **Hierarchy.** (1) A top bar reading **"Tasks"**. (2) One search field, fully
  routed through the token layer. (3) **One filter bar with two labelled axes.**
  (4) **One card** containing both rows separated by dividers, each row with name,
  recurrence, category, status and a **single** overflow. (5) One floating action
  button, same material and glyph size as the management screen's. (6) The
  four-destination floating bar.
- **Composition.** Flat single column; no per-row card; no button bar.
- **Primary visual.** The list card.
- **Key components.** Top bar; token-routed field; two labelled filter axes; one
  card with two rows; one FAB; the floating bar.
- **Data shown.** "Book dentist" — once · due today · not done. "Renew passport" —
  once · due 12 Oct.
- **Interaction emphasis.** Overflow → Pause/Delete, with an **opaque** dialog, an
  explicit scrim, one dismiss verb, and copy naming permanence and the archive
  alternative.
- **Distinctive behaviour.** None.
- **Light considerations.** Surface lighter than the canvas, hue-carrying shadow.
- **Dark considerations.** Tonal lift; glass **only** on the floating bar and the
  dialog's scrim treatment — nowhere else.

#### Screen 4 — Insights

- **Purpose.** Answer *"how consistent have you been?"* — question, key number,
  mark, interpretation, in that order.
- **Hierarchy.** (1) A section label in question form. (2) **The consistency score
  at primary-figure size** with "LAST 30 DAYS" beside it. (3) The **30-day mark
  strip** in the canonical four-state vocabulary, with genuinely different-looking
  not-scheduled days and missed days. (4) The 7-day rate as a **secondary** figure
  with a signed delta and a window. (5) A ranked category card, worst first, each
  row with a channel drawn with the progress-track token. (6) One interpretation
  sentence. (7) The missed count.
- **Composition.** Column; the 30-day strip full content width; the ranked list
  one card; **no 2×2 stat-tile grid**; no tile grid anywhere.
- **Primary visual.** The 30-day four-state strip.
- **Key components.** Question label; consistency figure; 30-day strip; the
  seven-day comparison; a ranked category card; the interpretation sentence; the
  missed count; the floating bar.
- **Data shown.** 78% (30 days) · 30 days showing not-scheduled days and missed
  days **differently** · 7-day rate + signed delta · one category at 0% · 6 missed.
- **Interaction emphasis.** The window control.
- **Distinctive behaviour.** None.
- **Light considerations.** The strip's four states are distinguishable by form,
  not by colour alone.
- **Dark considerations.** Same; the strip must remain legible at 200% font scale.

#### Screen 5 — Onboarding (page 3)

- **Purpose.** State the differentiator, **in the user's actual theme**, as the
  first screens of the app rather than a layer above it.
- **Hierarchy.** (1) A page indicator. (2) A large headline "**Nothing leaves
  this phone.**" on the token layer's own ink, in Manrope, with **no bespoke drop
  shadow**. (3) One body line. (4) The photographic artwork in the **matching
  environment's variant**. (5) One bottom action and one skip.
- **Composition.** Full-bleed artwork with a controlled readability treatment —
  **dissolved into the page, not darkened**, which is the app's own better
  strategy and the one its own decision record establishes for the hero. One
  transition per page change.
- **Primary visual.** The artwork, chosen by the same pure theme function as the
  hero's.
- **Key components.** Page indicator; headline; one body line; the artwork; one
  action; one skip.
- **Visual metaphor.** None. This screen is deliberately the plainest in the
  product, because it is where the product makes an argument rather than a gesture.
- **Interaction state.** Third of four.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads. Your data is in your storage and nowhere else."
- **Light considerations.** A **Light artwork variant**, derivable from the vector
  sources in the repository — not a commissioning project. The system-bar
  appearance comes from the app's own theme setting, not the platform's.
- **Dark considerations.** The Dark artwork variant, unchanged. **No force-patched
  system-bar appearance and no hand-restored platform state**, because onboarding
  knows what theme it is in.
- **Interaction emphasis.** One action; one skip.

### 8.22 Aurora, Governed — image-generation briefs

#### Brief D1 — Home / Today

- **Android-native context.** A premium Android habit/productivity app's primary
  daily screen. Material tab grammar, Android app-bar behaviour, real safe areas.
  An app screen, not a marketing page.
- **Device / frame.** One Android phone, portrait, centred, slim even bezel, even
  canvas margins on all four sides, the device never touching an edge, content
  clearly the hero. Identical frame across D1–D5.
- **Screen purpose.** Answer "what must I do today, and how am I doing?" in one
  glance.
- **Exact visual hierarchy.** Top: a soft, low-intensity ambient light field
  behind a photographic header that **dissolves into the page** with no hard edge
  and no seam at the status bar. On it: a small caption date. Then a greeting in a
  medium display weight, one line. Directly beneath and **larger than the
  greeting**: the figure "**3 of 5**" in a real heavy weight, the "of 5" smaller
  and lighter in the same lockup, set with tabular figures. Then one body-size
  state line that references the user's data. Then a hairline and the record card:
  five rows separated by thin dividers, each row = a 48 dp circular completion disc
  (three filled in the completion hue, two as quiet outline rings), a name in body
  weight, a second line carrying a **large** value where one exists
  ("5 OF 8 GLASSES", "0 OF 20"), and at the right a bold streak number with a
  small state word. Bottom: a **floating, softly translucent** four-destination
  bar with icon plus label, the active one filled and bold, and one small floating
  action button.
- **Composition.** One continuous vertical surface; the header is a collapsing
  artwork band that is **visibly shorter** than a full-bleed hero; a single scroll
  surface, no nested scrolling; generous margins; one card containing the whole
  list.
- **Typography direction.** Manrope. A real, heavy numeric register: the figure is
  unmistakably the largest type on the screen, with **tabular figures** so it does
  not shift. Greeting lighter in weight and smaller than the figure. Row names
  calm. Small metadata tracked and quiet. Section labels visibly distinct from
  their content.
- **Colour / environment.** A **cool light canvas** that is genuinely darker than
  the card above it, so depth is real. A deep, slightly desaturated blue accent
  for **progress and the primary action**; a **distinct** teal-green for
  **completion** — two different hues for two different axes, because the shipped
  app uses two hue families on the same screen for undocumented reasons. The
  ambient field is visible but quiet.
- **Surface treatment.** One card: lighter than the canvas, a **soft shadow tinted
  to the canvas hue rather than pure black**, plus a fine warm hairline. **Glass
  only on the floating navigation bar.** No glass on the card, the rows, or the
  buttons.
- **Major components.** Collapsing artwork header; date; greeting; the primary
  figure; the state line; the record card with five rows and dividers; five 48 dp
  completion discs; target values at figure scale; streak numbers with state
  words; a floating four-destination bar; one small floating action button.
- **Visual metaphor.** A day filling with light. The figure is the reading; the
  background's intensity is the state.
- **Interaction state.** Mid-day; three complete; one target at 5 of 8; one target
  at 0 of 20; one streak of 12 that closes tonight.
- **Important data.** "3 of 5" · Morning run, Weekly review, Take the stairs
  (complete) · Drink water 5 OF 8 GLASSES · Read 20 pages 0 OF 20 PAGES · streak
  12 · Tuesday 29 September.
- **Light/dark requirement.** This brief is the **Light** rendering. A Dark twin is
  required with identical content: a deep canvas, lighter surfaces, a visible
  top-edge highlight, a lower-intensity field, and the Dark artwork variant.
- **MUST be visible.** The figure as the largest type on the screen and larger than
  the greeting. The card clearly lighter than the canvas, with a soft hue-tinted
  shadow. A real tonal step between canvas and card. The two accents visibly
  **different** (progress vs completion). Five 48 dp discs. The "5 of 8" value
  visibly larger than the row's other metadata. The streak number larger than the
  row name. A visible soft ambient field. Safe-area awareness at the top and bottom.
- **MUST NOT appear.** Fire, flame, trophy, medal, confetti, emoji. A progress ring
  or a circular percentage badge — the figure is the answer, not a ring around it.
  A donut chart. Glass on the card or the rows. A purple-blue gradient mesh. A
  landing-page hero with a headline and two buttons. A centre card row. Nested
  cards inside cards. Pills, tags, or badges. Motivational filler copy. Tiny
  unreadable metadata. A phone-shaped website.
- **Anti-patterns to avoid.** Marketing-hero composition; a three-feature card row;
  a "wellness app" pastel gradient; every surface looking identical; a big
  decorative hero with a small number; a 2×2 stat grid; fake status-bar clutter;
  drop shadows in pure black.

#### Brief D2 — Habit Detail

- **Android-native context.** A pushed detail screen with a back affordance
  top-left and a text edit action top-right.
- **Device / frame.** Identical to D1.
- **Screen purpose.** One habit's week, run, and consistency, with the two metrics
  the app computes and never shows.
- **Exact visual hierarchy.** Back arrow; the habit name "Drink water" in display
  weight; a caption line "HEALTH · DAILY · REMINDER 8:00"; a hairline. Then a
  section label "**THIS WEEK**" in tracked small caps — and immediately to its
  right, the answer "**4/6**" in a **larger, heavier** figure. Then a **seven-cell
  mark row**: one filled disc with a check, three ring outlines, one **struck
  ring** (the missed scheduled day), and **two 8 dp muted dots** for days not
  scheduled, each cell with a tracked weekday initial beneath. Then a streak
  block: "**12**" in a large heavy figure with a small state word "closes tonight"
  beside it, and "Longest 19" one step smaller. Then a **consistency block**:
  "**78%**" in a large figure with "last 30 days" in a small caption. Then a
  **missed count**: "6 missed". Then one configuration card with three rows. Then
  a single labelled action at the bottom.
- **Composition.** Column; the mark row full content width; the configuration as
  one card with rows rather than a tile grid; generous vertical spacing between
  labelled blocks.
- **Typography direction.** Manrope with a heavy numeric register and tabular
  figures. **Every answer is larger than the label above it.** The habit name is
  lighter in weight and smaller than the streak figure.
- **Colour / environment.** Cool light canvas, lighter card, hue-tinted shadow. Two
  accents: progress blue and completion teal, kept distinct.
- **Surface treatment.** One quiet card. Glass nowhere on this screen. Divider
  hairlines, not shadows, between configuration rows.
- **Major components.** Back arrow; habit name; caption line; the four-state
  seven-cell mark row; the streak block with a state; the consistency block; the
  missed count; a configuration card; one action.
- **Visual metaphor.** One week of a habit, marked.
- **Interaction state.** A 12-day run at risk; a partly complete week with one
  missed scheduled day; two days not scheduled.
- **Important data.** Drink water · 8 glasses/day · streak **12**, longest 19 · this
  week 4 of 6 scheduled, 2 not scheduled, 1 missed · consistency **78%** (30 days)
  · **6 missed** · Active.
- **Light/dark requirement.** Light; produce a Dark twin.
- **MUST be visible.** The answer "4/6" **larger than the "THIS WEEK" label**. The
  streak number **larger than the habit name**. **Two visually distinct
  not-scheduled cells** that are clearly *different from the missed cell*. The
  consistency figure and the missed count both visible. Tabular figures.
- **MUST NOT appear.** A weekly bar chart. Fire, flame, or a streak badge. A
  circular progress ring or orb. A 2×2 grid of equal stat tiles. A percentage
  with no window. A trophy or an achievement tier. The words "Insufficient data"
  as a value. Glass on the card. A dropped whole-composite alpha on the paused
  state. Pure-black drop shadows.
- **Anti-patterns to avoid.** A habit-detail card stack of identical rounded
  rectangles; a 2×2 stat grid; a weekly bar chart; a fire badge; a month heat map
  with no rest/miss distinction; indistinguishable "not scheduled" and "missed"
  cells; a percentage with no window.

#### Brief D3 — Tasks

- **Android-native context.** A tab destination; Material list conventions; a top
  app bar with a title and a search affordance.
- **Device / frame.** Identical to D1.
- **Screen purpose.** Manage tasks as a management surface.
- **Exact visual hierarchy.** A top app bar reading "**Tasks**". One search field
  with the product's own ink for its text and label — **not** a default blue
  Material outline. Then **one filter bar with two clearly labelled axes**
  ("Status" and "Category"), each a small segmented set, visibly different from
  one another. Then **one card** containing two rows separated by dividers; each
  row = name, a small metadata line with recurrence and category, a status label,
  and a **single** overflow control at the right. Then a **floating action
  button** of the same material and glyph size used on the other management
  screen. Bottom: the four-destination floating bar.
- **Composition.** Flat single column; **one card for the list, not a card per
  row**; no button bar under any row.
- **Typography direction.** Manrope; row names in body weight; filter labels small
  and tracked; the top-bar title in display weight.
- **Colour / environment.** Cool light canvas, lighter card, hue-tinted shadow,
  two accents kept distinct.
- **Surface treatment.** One quiet card. Glass on the floating bar and the FAB
  only. No glass on the rows.
- **Major components.** Top app bar; token-routed search field; two labelled,
  visibly different filter groups; one card with two rows and one overflow each;
  one floating action button; the floating four-destination bar.
- **Visual metaphor.** None — this is a management list and it is allowed to be
  plain.
- **Interaction state.** Two tasks, one due today and not done, one due in 12 days.
- **Important data.** "Book dentist" — Once · due today · not done. "Renew
  passport" — Once · due 12 Oct.
- **Light/dark requirement.** Light; produce a Dark twin.
- **MUST be visible.** The word "Tasks" as the title — **never "activities"**.
  Exactly **one** overflow per row, not two identical circles. Two clearly
  **labelled and visibly different** filter axes. A search field whose text and
  label are in the product's own ink, not default Material blue. A floating action
  button of the **same** material as the one on the management screen.
- **MUST NOT appear.** "No activities yet" or "Create activity". Two identical
  unlabelled circular buttons under any row. A card per row. A default-blue
  Material outlined text field. A raw Material bottom sheet in an off-palette
  colour. Two floating action buttons of **different** materials in one app. A
  dropdown in default Material colours. Motivational empty copy.
- **Anti-patterns to avoid.** Management controls louder than content; two
  identical unlabelled pill rows; a web-form search box; a "Create something worth
  showing up for" empty state; a heavier row weight than the home list.

#### Brief D4 — Insights

- **Android-native context.** A tab holding a scrollable analysis view. Reads as an
  app's analytics screen, not as a desktop dashboard scaled down.
- **Device / frame.** Identical to D1.
- **Screen purpose.** Answer "how consistent have you been?" — question, key
  number, mark, interpretation, in that order.
- **Exact visual hierarchy.** A top app bar. Then a **section label in question
  form** — "How consistent have you been?" Then the **consistency score at the
  largest type in the product**, heavy, tabular, with "LAST 30 DAYS" as a small
  caption beside it. Then a **30-day mark strip**, full content width: 30 cells,
  each in the canonical vocabulary — filled disc, ring outline, **struck ring for a
  genuinely missed scheduled day**, and a small muted dot for a day not scheduled,
  so that the missed days and the not-scheduled days are **visibly different**.
  Then the **7-day rate as a visibly smaller figure** with a signed delta against
  the 30-day average and its window. Then a **ranked category card**, worst first,
  four rows each with a name, a heavy rate, a graduated channel and a small "n of
  m complete" caption, separated by dividers. Then **one body-size interpretation
  sentence** referencing the user's own data. Then a missed-count line. Bottom:
  the floating four-destination bar.
- **Composition.** Column; the 30-day strip full content width; the ranked list as
  **one card**; **no 2×2 stat-tile grid**; one scroll surface.
- **Typography direction.** Manrope with a heavy numeric register and tabular
  figures. The consistency score is the largest element; the 7-day rate is visibly
  **smaller** than it.
- **Colour / environment.** Cool light canvas, lighter card, hue-tinted shadow, two
  accents kept distinct; the strip's four states distinguished by **form** as well
  as by colour.
- **Surface treatment.** One quiet card for the ranked list; the strip is drawn
  directly on the canvas. Glass nowhere on this screen.
- **Major components.** Top app bar; question label; the consistency score; the
  30-day four-state strip; the seven-day comparison; a ranked category card; the
  interpretation sentence; the missed-count line; the floating bar.
- **Visual metaphor.** One month, marked the same way the week is marked.
- **Interaction state.** 30-day window; one category at 0%; a Mon/Wed/Fri habit in
  the strip so not-scheduled days genuinely occur.
- **Important data.** **78%** (last 30 days) · a 30-day strip with **missed days and
  not-scheduled days visibly different** · 7-day rate with a signed delta · one
  category at 0% · **6 missed occurrences**.
- **Light/dark requirement.** Light; produce a Dark twin.
- **MUST be visible.** The consistency score as the largest element. The 7-day rate
  visibly **smaller** than it. **Struck-ring (missed) cells and small-dot
  (not-scheduled) cells clearly different from each other** in the strip. A
  **worst-first** ranking. One sentence of real interpretation referencing the
  user's data. A signed delta with its window. The missed count.
- **MUST NOT appear.** A 2×2 grid of equal stat tiles. A seven-bar chart with no
  axis, no values and no maximum. "Insufficient data" as a value. The bare words
  "Declining" or "Improving" with no magnitude or window. A donut, a 3D chart, a
  radar, a rainbow chart, a decorative chart, a sparkline, a needle gauge. A wall
  of widgets. A purple-blue gradient. A "missed" day drawn the same as a
  "not scheduled" day.
- **Anti-patterns to avoid.** Chart spam; a legend with no data; percentages
  without windows; six identical stat cards; a desktop analytics layout compressed
  into 400 dp; a dashboard that answers a question the user did not ask.

#### Brief D5 — Onboarding (page 3)

- **Android-native context.** A full-screen first-run page with a real system status
  bar, a page indicator, one bottom action and one skip.
- **Device / frame.** Identical to D1. **Not dark-only** — this page is rendered in
  the user's actual environment, and a Light rendering is required.
- **Screen purpose.** State the one differentiator no competitor can claim, before
  the user commits.
- **Exact visual hierarchy.** A page indicator. A photographic artwork in the
  **matching environment's variant**, treated with a readability gradient that
  **dissolves into the page** rather than darkening it. On it: a large headline
  "**Nothing leaves this phone.**" in Manrope at display weight, **in the product's
  own ink token, with no bespoke drop shadow**. Beneath it one body line: "No
  account. No cloud. No tracking. No ads. Your data is in your storage and nowhere
  else." Then a single bottom action "Continue" and one small "Skip".
- **Composition.** Full-bleed artwork, copy in the upper-to-middle third on a
  controlled readability treatment, one action at the bottom. Generous margins.
  Safe areas respected.
- **Typography direction.** Manrope, display weight, sentence case with terminal
  punctuation. One register for the headline, one for the body line. Nothing else.
- **Colour / environment.** The **Light** environment for this brief: a cool light
  canvas, the Light artwork variant, a readable scrim, the product's own ink. **No
  dark-only photographic backdrop**, and no forced platform status-bar appearance.
- **Surface treatment.** Full-bleed artwork with a dissolve, not a translucent
  panel floating over it. No glass buttons.
- **Major components.** Page indicator; artwork; headline; one body line; one
  action; one skip.
- **Visual metaphor.** None. This screen argues rather than gestures, and that is
  deliberate.
- **Interaction state.** Third of four.
- **Important data.** "Nothing leaves this phone." · "No account. No cloud. No
  tracking. No ads."
- **Light/dark requirement.** Produce **both** environments, each with its own
  artwork variant, each with a readable treatment.
- **MUST be visible.** A **Light** artwork variant, not four near-black
  photographs. One headline and **one** short factual line. One action. A readable
  headline in the product's own ink. No hand-placed drop shadow on this page only.
- **MUST NOT appear.** The words "journey", "tomorrow", "unlock", "transform",
  "elevate", "seamless", "next-gen", "your life you want", "calm and powerful",
  "A more intentional you", "Stay Consistent", "Ready to build Momentum?". A
  Title-Case headline without terminal punctuation. A sign-in field, an account
  field, or a social button. A rating prompt. A giant abstract blob. A gradient
  mesh. A hand-lettered wordmark. An emoji.
- **Anti-patterns to avoid.** Four cloned slides; a cinematic photo with a heavy
  scrim; a headline-plus-two-buttons hero; four feature icons in a row; more than
  two lines of copy; a store-listing landing page; an app-store screenshot
  template.

---

## 9. Cross-Direction Comparison Matrix

**This matrix is descriptive only.** It compares *properties*. It contains no
score, no rating, no percentage, no rank, no "best", and no recommendation. A
row states what a direction **is**, never how well it does it. Reading across a
row shows that the four directions are different; reading down a column shows
what any one of them is. **The document does not say which is preferable, and
does not contain the information a reader would need to derive that for
themselves.**

| Property | **A — Press** | **B — Instrument** | **C — Loom** | **D — Aurora, Governed** |
|---|---|---|---|---|
| **Visual metaphor** | the printed ledger; momentum is the length of the record | inertia — momentum is mass × velocity, a quantity | the woven run; momentum is the length of cloth made one pick at a time | accumulated light; momentum is the behaviour of the ambient field |
| **Surface philosophy** | matte paper stock, page / sheet / rule. Zero translucency | opaque machined plate: plate / bezel / engraved channel. Zero translucency | matte textile: ground / cloth / selvedge / thread. Zero translucency | one real tonal ladder in both environments, with glass **demoted** to floating layers only |
| **How many surface levels, and what earns each** | three, and the material is the whole system | three, and the *machining* is the whole system | four, named as parts of a loom | four, named as canvas / surface / raised / floating, each with a stated reason |
| **What a card is** | a ruled ledger panel — a column of a page | a slotted data plate — an instrument faceplate | a stitched patch — a denser rectangle of the same cloth | the quietest object in the product, with **one** reserved emphasis tier per screen |
| **Q1 — Light** | **paper.** Warm off-white with fibre; depth is a **cast shadow**, which requires a light surface | **a printed instrument face.** Cool matte plate with a printed reference grid; depth is a **milled bevel**, which requires a matte light plate | **undyed cloth.** Visible vertical warp; depth is **thread occlusion on a raised pale pile** | **a real tonal environment.** Depth is a **tonal ladder plus a hue-carrying shadow**, which requires a light canvas |
| **Q1 — Dark** | **ink.** Very dark warm field; the record is a **light-toned object** placed on it, and it is the brightest thing on screen | **a backlit panel.** Very dark faintly blue, **no printed grid**; depth is **inner self-illumination**, which requires a dark field | **indigo-dyed cloth.** A **faint diagonal twill sheen** instead of a warp grid — and a neutral dark would destroy the twill entirely, so Dark *requires* chromatic commitment | **tonal lift plus a white top-edge highlight**, which requires a dark canvas — and the field becomes genuinely state-responsive everywhere |
| **Does the Dark environment need more colour than the Light one?** | no | no | **yes** — the twill is illegible without a saturated dye | no |
| **Accent philosophy** | one accent, three roles, so the accent is information | one **signal** colour in four *reading* roles; no per-item colour at all | one **dye**, two roles; per-activity colour becomes a full **system**, and the app's never-read colour field finally becomes the place it is chosen | one accent does global work; a **second hue is completion**; per-item hues get two environments or are removed |
| **Q3 — "today" is a…** | a **place** — an open column in a ledger | a **number** — mass and velocity readouts | a **shape** — a run of stitches, readable as a partially-woven band | a **number** at true figure scale, leading a list |
| **How the day's state reads without reading digits** | the count of ticked rules down the page | the fill of a five-tick graduated channel | the **weft pass** — tight / loose / not-laid picks, no digits needed | the count in the largest type, plus the field's intensity |
| **How a habit is marked** | a tick, in a cell with a rule; a missed day is a **struck** cell; a rest day has **no rule at all** | a **filled engraved channel**, a **struck diagonal**, or **no channel machined at all** | a **tight dyed pick**, a **loose paler pick**, or **bare warp with no pick** | the existing **filled disc / ring / muted dot**, plus a **fourth state** — a struck ring — for a genuinely missed scheduled day |
| **How a rest day is expressed** | **absence of a rule.** The day's column is empty paper | **absence of a channel.** The space is uncut metal | **absence of a pick.** Bare warp | a **low-weight 8 dp dot** — kept, deliberately, as a conservative choice |
| **How a missed day differs from a rest day** | a struck cell, versus no rule at all | a struck diagonal, versus no channel | a **loose pick** (a slip that can still be tightened) versus bare warp | a **struck ring** versus a small dot — a difference in weight, not in presence |
| **How a streak is expressed** | a figure at figure scale, larger than the habit's name, beside a **vertical run spine** with a visible break | a **run length** with a direction mark, beside a **run channel** with a break glyph, and the habit's **multiplication** printed with its factors | a **full-height spine** of stitches with a **cut** where the run broke; a stub on list cards | a **figure with a state** — rising, steady, or *closes tonight*. No flame, no pictograph |
| **The fire icon** | removed; a streak is a run of marks | removed; a run is a length | removed because **a flame is a category error on cloth** | removed, which is the direction that can claim to be *closer* to the locked design |
| **Q6 — icon position** | **weight-led**, which **requires** the variable family. Explicit declared dependency on Q6 resolving that way | **weight-flat and minimal.** Deliberately needs nothing new; the shipped static set is sufficient | **reduces iconography** to four structural glyphs; identity moves to dye and weave, so Q6 becomes low-stakes | **weight-tolerant and indifferent.** Deliverable on either answer; settles the identity and palette sub-questions, leaves the family question open |
| **Q2 — what "Momentum" means visually** | the **record** — evidence, not encouragement; the name is never restated in the copy | the **arithmetic** — mass × velocity, the only direction that takes the name as a quantity | the **run** — carried by the gesture, not the word; the app deliberately almost never says "momentum" | the **field's behaviour** — light that knows what time it is; carried by background rather than by a figure, and subtle to describe |
| **Navigation model** | a **flat flush bottom bar** with a hairline rule; no floating | a **machined four-slot segmented rail** with engraved dividers | a **selvedge** — a denser woven band, like the finished edge of cloth | the **floating translucent bar**, kept from the shipped app, with its geometry tokenised and its labels fixed |
| **Row → detail** | container transform + a shared element on the item's **rule and tick** | container transform + a shared element on the item's **unit and channel**; needs a value-aware view | container transform + a shared element on the item's **dyed warp thread** | container transform on the **existing shared accent tile** — 44 dp in a list, 72 dp on detail, already the same component |
| **How a row is composed** | one sheet with rules between rows | one faceplate with engraved rules between rows | one patch with running stitches between rows | one card with dividers between rows |
| **The Tasks tab is** | a management surface, with **one** overflow per row | a management surface, using the model the product already ships correctly elsewhere | a management surface, with the seam as the metaphor | a management surface, with the button bar collapsed to one overflow |
| **How "How am I doing today" is answered** | a figure at the head of the page, larger than the greeting | a readout with a five-tick graduated channel, and **no display-sized greeting** | a count at thread weight, larger than the greeting, plus a weft band | a figure at true figure scale with a registered 700, larger than the greeting |
| **What the day's 100% moment is** | a **pressed stamp** into the rule-set. Earned by a milestone | **five graduations registering at once.** No expressive gesture at all | the **weft band tightening** and the selvedge gaining a stitch | the **field moving to its strongest state**, using the two hooks that already exist |
| **What is continuous / ambient** | nothing | nothing | nothing | nothing — but the field is **change-triggered** rather than a clock |
| **Motion personality** | settle, wipe, dry. Things come to rest | step, needle, resolve. Values *resolve*, they do not slide | one stitch is laid; the strip does not travel | change-triggered bloom, then rest. Motion on a transition, not on a wall clock |
| **Is the shipped row-glide kept?** | no — a row's tick simply appears | no — a row relocates | no — **a pick is laid, it does not travel** | **yes** — it is the product's only item-level motion and it is good |
| **Is the completion control's spring kept?** | no, but its accessibility contract is kept | yes | yes | yes, and its contract is kept |
| **Onboarding** | four pages of type and rule on the app's own paper, in the user's real environment; the finished page edge is the visual | four pages arguing the arithmetic, with a scale that visibly stops at the device | four pages **teaching the mark vocabulary** in the first screen, because the rest-day mark is a removal | four pages arguing the case, with a **Light artwork variant** and the theme inherited |
| **What the first-run argument is** | keep the record · one kind of thing, tracked · nothing leaves this phone · tomorrow, this is the top of the screen | mass times velocity · one kind of thing, tracked · nothing leaves this phone · tomorrow, this is the top of the screen | some days you do it, some days it isn't asked · one kind of thing, tracked · nothing leaves this phone · tomorrow, this is the top of the screen | what it is · what it does for you · nothing leaves this phone · start |
| **The one thing that is unmistakably Momentum** | the **pressed stamp** | the **multiplication** — `mass × velocity`, printed with its factors | the **spine**, and a rest day as **bare warp** | the **state-responsive light** |
| **What it removes** | all translucency, all glow, the orb, the photographic hero, the fire, and the *entire* category of surface fill values | all translucency, all glow, the fire, and the entire category of surface fill values | all translucency, all glow, the fire, and the entire category of surface fill values | both permanent animation loops, the fire, whole-composite alpha on paused rows, translucent destructive dialogs, and roughly fourteen scattered decisions — replaced by four |
| **The dependency it carries** | **Q6 must resolve toward the variable family** for its iconography to work as designed | per-day completion **values** must reach the presentation layer for the graduated channel | a visible weave must be **affordable on a scrolling surface** at 400 dpi | a **real Light tonal ladder** must be achievable at the contrast floors this product holds |
| **The unknown that decides it** | whether a light-toned object on a dark field reads as premium or as night-reading mode | whether "mass × velocity" reads as a measurement or as a pretension | whether a visible weave reads as craft or as a texture overlay | whether a genuine Light tonal ladder is achievable, or reads as a washed-out surface |

**What this matrix establishes, and what it does not.** It establishes that the
four directions differ at the level of metaphor, material, card role, mark
vocabulary, navigation, motion, and both environments — and that they answer
Q1, Q2, Q3 and Q6 four different ways. It does **not** establish that any of them
is better than any other, and no row above is capable of supporting that claim,
because every row is a property rather than a measurement of quality.

**The one thing all four rows share, and it is not a convergence of the
directions:** all four re-think onboarding so that the offline-and-on-device
promise appears before the user commits, and all four remove the fire icon. That
is a convergence of the **argument** — it is the argument the shipped app's own
copy and its own locked design document both support — and not of the visual
languages, which remain four different products with four different surface
philosophies, four different Light mechanisms and four different answers to
what "today" is.

---

## 10. Audit Question Mapping

Each of Phase 1's fourteen open questions, mapped to all four directions. The
question is stated as Phase 1 stated it. **No position below contradicts the
evidence the audit recorded**, and where the audit left something undetermined,
that is stated rather than filled in.

### Q1 — Is Light Aurora a designed environment, or an inversion of Dark?

*The audit's largest open question. Its evidence: the two environments share all
five accent hues with the Dark value simply lightened; no hue is introduced,
removed or substituted; no colour role changes; nothing is expressed in Light
that cannot be expressed in Dark. Light's surface ramp collapses, its shadows are
black-alpha and therefore invisible on a light canvas, and a one-pixel hairline
does all the separation.*

| | Position | Where it came from |
|---|---|---|
| **A — Press** | **Light is a designed environment, and Dark is a different material.** Light is warm paper; depth is a **cast shadow**, which requires a light surface and cannot exist on a dark one. Dark is an unlit field on which the record is a **light-toned object** — the paper is the light source. Neither mechanism is available in the other environment. | The audit's observation that in Dark, a surface lighter than its background produces a genuine lift, while in Light a translucent white over a near-white canvas produces nothing. Press keeps the lift in Dark and gives Light a mechanism Dark cannot have. |
| **B — Instrument** | **Two different physical devices from the same instrument.** Light is a printed, matte instrument face with a printed reference grid; depth is a **milled bevel**, which only reads against a surface brighter than the highlight. Dark is a backlit panel with no print; depth is **inner self-illumination**, which only reads on a dark field. | The audit's finding that the shipped Light mechanisms are inert *because* they were designed for a dark canvas. B gives each environment a mechanism native to its own material, rather than reusing one recipe. |
| **C — Loom** | **The same cloth in two states of preparation — and Dark needs more colour than Light.** Light is undyed linen with a visible vertical warp; depth is **thread occlusion on a raised pale pile**. Dark is indigo-dyed with a **diagonal twill sheen** instead of a warp grid, and a neutral dark would have **no twill at all**, because there is no value difference between warp and weft to catch. | The audit's point that no colour role changes between the shipped environments. C makes the structural difference *require* a chromatic difference, so the two environments cannot be the same design at two luminances. |
| **D — Aurora, Governed** | **Light becomes a designed environment; Dark keeps its mechanism and gains state responsiveness.** Light's depth is a **real tonal ladder plus a hue-carrying shadow** — a pure-black shadow is invisible on a light canvas, a hue-tinted one is not — and Light drops the white top-edge highlight, which is a contradiction on a near-white surface. Dark keeps tonal lift and the white highlight, and its field becomes state-responsive everywhere. | The audit's measured finding that every shadow in the product is black-alpha and therefore invisible in Light, and that the shipped Light has three distinct surface values where Dark has five. D attacks both directly and keeps the shipped accent hues. |

**The four positions are four different answers to one question, and all four are
explicit.** There is no direction here that treats Light as a brightness switch,
because the audit's evidence is decisive that it currently is one.

### Q2 — What is the product's one idea, and what is the app's name pointing at?

*The audit's evidence: the app is called Momentum; the design document rejects
fire-streak gamification and emoji; the app ships a fire icon anyway, and its ADR
records that the fire glyph was substituted for the rejected emoji form; the
day-completion moment is a snackbar; "momentum" appears in the tagline, in four
of six Home messages and one onboarding headline, and nowhere in the mechanics.*

| | Position | Where it came from |
|---|---|---|
| **A — Press** | Momentum is **the record** — evidence rather than encouragement. A streak is a column of filled marks, so a flame would be a second, contradictory representation and is deleted. The word appears as a data-derived caption or not at all. | The audit's finding that the emphasis is on the least important numbers, and that four of six Home messages reference no user data. A record-keeping metaphor makes the *data* the reward, which needs no motivational copy. |
| **B — Instrument** | Momentum is **mass × velocity** — a quantity, not a metaphor. A habit is mass, a streak is velocity, completion adds mass. **This is the only direction that explains why a fire glyph was standing in for half an idea:** a flame expresses velocity and the product had no way to express mass. | The audit's observation that a flame is a pictograph for velocity alone, in a system that cannot express magnitude. B's multiplication expresses both. |
| **C — Loom** | Momentum is **the run** — a length of cloth that exists only because it was made one pick at a time. **A flame is physically wrong here:** heat destroys cloth, and a flame implies a system that can be *extinguished*, which is the wrong promise for a product whose core rule is that unscheduled days never break a streak. The word is almost never said; the name is carried by the gesture. | The audit's streak-rule evidence. A cut thread is visible, honest and repairable; an extinguished flame is neither. |
| **D — Aurora, Governed** | Momentum is **accumulated light** — the ambient field's *behaviour*, not a message about it. The field brightens as the day fills and is unmistakably different on a finished day, and the unreachable milestone state and reward duration are wired. The fire is deleted in favour of a number with a state. | The audit's finding that the ambient field has no state parameter, that the state-responsive one is pinned to its lowest intensity on two of three screens, and that its strongest state is unreachable. The product already owns the one asset its name fits. |

**Note for a reader weighing these:** B and C delete the fire for *material*
reasons and A and D delete it for *evidence* reasons. All four delete it. The
audit's evidence supports all four, and none of the four contradicts the other.

### Q3 — Should "today" be a number, a shape, or a place?

*The audit's evidence: the answer is currently body text, smaller than the
greeting above it; a 40 sp percentage inside a 96 dp ring is a different
proposition from a 20 sp one; and a *shape* — a completed-day silhouette whose
notches are the remaining items — is a third option. The data for all three is
already available.*

| | Position | Where it came from |
|---|---|---|
| **A — Press** | A **place.** Today is an open column at the head of a page, with the items ruled beneath it. The place is the answer, and the number at the top of it is just the count of what's below. | The audit's observation that a 96 dp ring was built for one caller and that the product has seven progress vocabularies because no primitive served more than one. A column is the ledger's own unit. |
| **B — Instrument** | A **number**, on a **calibrated** scale. The readout is the answer, and the five-tick channel lets the same object be read as a percentage or as a count of five. | The audit's finding that the product's largest number answers a question the user did not come to ask. B inverts it by making the *now* figure the largest and putting the week beneath it. |
| **C — Loom** | A **shape.** The weft pass — three tight picks, one loose, one **not yet laid** — reads as "mostly done, two to go" with no digit being read. The number is present but secondary. | The audit's own "radical version" for this screen: express the day's state as a shape rather than a number, using data that is already available. C takes that option and gives it a material. |
| **D — Aurora, Governed** | A **number**, at true figure scale, at the top of the hierarchy, leading a list. **This is the audit's most conservative answer and the one its own priority argument supports:** the declared headline is a numeric question, and the product's only failure is that the numeric answer is not the biggest thing. | The audit's highest-impact opportunity: a numeric register assigned by meaning, now > this week > this month, with the app's own declared headline as the justification. |

### Q4 — What is a habit's history: seven days, thirty days, or everything?

*The audit's evidence: the habit-overview use case takes a set of completion
dates, hardcodes a seven-day window, and discards each record's value at the use
case boundary, so a "drink 8 glasses" habit cannot show an average. The stated
reason the weekly view may not be a bar chart is that a habit can be completed at
most once per day — a claim about the week, not about values.*

- **A — Press** takes the seven-day view as-is and states that a value-bearing
  view is a *different question*, not a bar-chart question. It offers no window
  control and claims no dependency.
- **B — Instrument** wants a **graduated channel** for a target-based habit and
  states the dependency explicitly: it requires per-day values to reach the
  presentation layer, and today they do not. **The binary week is what the briefs
  depict.**
- **C — Loom** wants a **partly-laid pick** and states the same dependency in the
  same words, adding the distinction that the bar-chart prohibition is *not* the
  obstacle, because its stated reason is a claim about the week.
- **D — Aurora, Governed** proposes a **7 / 30 / all control**, records that it
  requires a window parameter the use case does not have, and — importantly —
  **does not depend on it.** The seven-day view is what works today and what the
  briefs depict.

**All four treat this as a data question before it is a design question, and none
of them assumes the answer.**

### Q5 — Is per-activity colour a system or a local accent?

*The audit's evidence: per-activity accent identity reaches three of five
activity surfaces; `Activity.colorArgb` exists in the schema, is mapped, and is
read by zero presentation code; the seven item hues are one fixed set used
identically in both environments; and a second parallel colour map over template
ids agrees with the first for all ten shipped templates by hand-maintenance, not
by derivation.*

- **A — Press** says **no** — per-activity colour is a local accent and is
  removed. Item identity is carried by name, stable order, and a typographic key
  mark. In a printed ledger a red line and a blue line mean the same thing.
- **B — Instrument** says **no** — no per-item hue in either role. An instrument
  panel has one signal colour, and adding per-item hues makes it a mood board.
  The distinguishing information moves into the **unit** (`20 p`, `30 min`,
  `8 ×`), which is text and which no colour ever conveyed.
- **C — Loom** says **yes, and it is a system** — a dyed warp thread, the same dye
  on every surface that shows the activity, chosen from a small fixed set of real
  dye names, with the user's own colour field as the one place it is picked. This
  is the direction that **spends the product's most under-used asset**, and the
  metaphor is what makes it honest rather than decorative: in a dyed cloth,
  colour is applied to the *thread*, not to the garment.
- **D — Aurora, Governed** says **yes, and it is a system** — the same hue on all
  five surfaces, with the item hues given two environments, and the parallel
  template map collapsed. The audit's own finding that a partial system teaches
  the user nothing is taken as decisive.

**Two directions say yes and two say no, and the disagreement is substantive:
Loom and D are arguing that the field should be spent; A and B are arguing that
identity should come from somewhere other than colour.**

### Q6 — Which icon family, and is habit identity carried by an icon?

*The audit's evidence: the code uses Material **Icons** via the extended set — a
static vector collection with no weight axis; the documentation and its master
ADR name Material **Symbols** Rounded; the concept pack ships Symbols Rounded as
a webfont at weight 100–700 and is a web artefact that is not a valid Android
font resource and is referenced by nothing. The finding is that **one recorded
decision was never honoured**, and that the consequence is functional: the
variable-weight capability the concept assumes is unavailable. The audit also
records three non-aligned key vocabularies, so that a user who picks "Spark" gets
a star and "Art" is colour-indistinguishable from a habit with a corrupted key.*

| | Position | Dependency |
|---|---|---|
| **A — Press** | **Weight-led.** Identity is carried by the *weight* of a glyph and by typography, so the concept's own term — density, an ink weight that can be quiet or emphatic. Requires the variable family. **If Q6 settles toward the static set, the identity carrier weakens and the direction would need a second one.** | **Explicit.** This is the only direction that depends on Q6 resolving a particular way, and it says so rather than assuming it. |
| **B — Instrument** | **Weight-flat and deliberately minimal.** Identity is carried by a unit and a name; a streak is a run length; state is geometry. Four structural glyphs remain. **Q6 does not need to be settled for B to be deliverable.** | **None.** Deliverable on the shipped set today. B accepts a small typographic dissonance between glyphs and tracked inscriptions in exchange for not being blocked. |
| **C — Loom** | **Reduce iconography rather than change it.** Identity is a dyed warp thread, so there is no glyph registry to keep aligned — which dissolves the three-vocabulary drift entirely and removes the fallback star. Four structural glyphs remain. | **Low.** Q6 becomes low-stakes because Loom makes so few icon decisions. C's cost is real and is stated: four glyphs puts all its legibility weight on typography. |
| **D — Aurora, Governed** | **Weight-tolerant and indifferent.** One family, one optical density, monochrome. **D is deliverable whatever Q6 becomes, and needs no change either way.** D *does* settle the other two sub-questions: one authoritative key registry replacing the three drifted vocabularies, and the launcher mark's palette reconciled to the theme's. | **None on the family.** Open, deliberately, and stated as open. |

**A direction may legitimately say "requires further validation" — the audit left
it open — but none of the four ignores it, and the four positions are genuinely
different: *depend on it*, *avoid needing it*, *reduce it away*, *stay
indifferent*.**

### Q7 — Is the floating glass bottom bar the product's navigation model or an artefact?

*The audit's evidence: a decision record locks the floating glass bar; the design
document forbids glass as a default treatment for navigation; the habits
specification asks for navigation to be visually integrated with the screen
rather than becoming a large floating Material component. The bar's own geometry
is six literals that exist in no token, and its labels are 12 sp — the app's
smallest type.*

- **A — Press** — **no.** A flush, matte, opaque bar with a single hairline rule
  and no radius. A floating element on a piece of paper looks like a sticker.
- **B — Instrument** — **no.** A flush, four-slot segmented rail with machined
  dividers, equal-width slots, and a top-edge registration bar on the active
  slot. Equal-width slots communicate *"four channels of one device."*
- **C — Loom** — **no.** A **selvedge**: a denser woven band flush to the bottom,
  with running stitches between the four slots and a full-width dyed pick laid
  across the top of the active slot. The bar is the edge of the cloth, so the
  content extent is definite.
- **D — Aurora, Governed** — **yes, and the tension is named rather than
  resolved.** D keeps the floating bar, fixes its geometry and its label type,
  and states plainly that it is choosing a decision record over a design-document
  clause, and that the disagreement is real and unresolved.

**Three say no for different structural reasons; D keeps it and says why it
cannot settle the disagreement. All four hold the four destinations, which is
locked.**

### Q8 — Does Momentum need a separate Tasks tab?

*The audit's evidence: Tasks is the shared activities list filtered to tasks; it
says "activities" on an empty Tasks tab, has no completion affordance, and is
twice as heavy as Home; the architecture implies management belongs on Habits and
action belongs on Home; the habits specification forbade changing Tasks only as
part of a phase that is now closed.*

**All four directions decline to answer this, and that is the correct behaviour.**
The destination count is a locked decision and a visual direction does not get to
re-open it. What all four *do* answer is the **treatment**: all four make Tasks
read as a management surface, all four give it the overflow model the product
already ships correctly on the management list, all four remove the per-row
button bar, and all four fix the vocabulary leak so the tab never says
"activities". **The question is recorded as open and left to a product decision,
not a visual one.**

### Q9 — What is onboarding relative to the app?

*The audit's evidence: onboarding is theme-blind, dark-only, with its own palette
and its own ink, forcing a hand-patched system-bar appearance; its four bodies
contain zero product facts while the product's best line sits in Settings; the
vector sources that would permit a Light variant exist in the repository,
unreferenced, because a migration flattened them into photographs.*

**All four answer: onboarding is the app, not a layer above it.** The four reach
that conclusion from four different metaphors — a printed page, a machined
faceplate, a woven swatch, and a themed photographic screen — and in each case
the *reason* is the same structural fact: **a surface that is not built from the
app's own material cannot survive a theme change.** The four differ in what
onboarding *argues*: A argues the record, B argues the arithmetic, C **teaches
the mark vocabulary in the first screen** because its rest-day mark is a removal
that must be explained, and D argues the case with a Light artwork variant and
the theme inherited.

### Q10 — Where should "surface" come from in Light?

*The audit calls this a narrower cut of Q1 and says the two are best answered
together. All four answer them together, and each direction's answer to Q1 above
**is** its answer to Q10.*

- **A** — the material is **paper**, and Light's depth is the shadow a sheet casts
  on the sheet below it.
- **B** — the material is **engineering**, and Light's depth is a milled bevel on
  a matte plate.
- **C** — the material is **cloth**, and Light's depth is thread occlusion on a
  raised pale pile.
- **D** — the material is **tonal**, and Light's depth is a real tonal ladder plus
  a shadow that carries the canvas's hue rather than being black.

**None of the four answers "keep the translucency and accept a flatter read."**
That option was available — it is what the shipped app does — and no direction
takes it, because the locked design document's own Light clause promises tonal
contrast, soft shadows and subtle borders, and only the last of the three exists
today.

### Q11 — What is the empty day?

*The audit's evidence: on a day with nothing scheduled, a roughly 300 dp
photographic surface contains a date, a greeting and nothing else, above a card
that says nothing is scheduled. A weighted spacer expands to fill 72 dp of
headroom. The app looks unfinished on exactly the days it is least needed.*

- **A — Press** — the artwork does not exist, so the question dissolves. **The
  figure is 0 dp tall when the day is empty**, because a number has no minimum
  size. The empty day is a piece of paper with nothing on it.
- **B — Instrument** — the readout plate is 0 dp when there is no reading. **The
  empty day is an instrument switched off**, and that is structurally honest
  rather than a special case.
- **C — Loom** — the weft band is empty and the screen says so in one line at the
  top. **The empty day is a piece of cloth with nothing woven into it**, which is
  exactly what it is, at 0 dp of decoration.
- **D — Aurora, Governed** — **the artwork is withheld on an empty day** and the
  empty state's copy moves up into the position the greeting occupied. This is
  the only direction that keeps the artwork and therefore has to make an explicit
  rule, and it is the only one that needs the rule to be tested.

**All four reach the same structural answer — the expensive surface is sized to
its content, so emptiness costs nothing — and they reach it for four different
reasons.**

### Q12 — What does a 100% day feel like?

*The audit's evidence: the milestone state and the reward duration exist and are
unreachable; the entire celebration is a long-press haptic and a snackbar; the
hero simultaneously shows "Everything complete. Rest easy."; the design document
permits a stronger Aurora response at milestones, rejects routine confetti, and
the tension with the project requirement for streak celebration resolves only if
the celebration is earned by a milestone rather than by a completion.*

- **A — Press** — a **pressed stamp** into the rule-set, like a date stamp on a
  page. One firm haptic, no travel, no scale.
- **B — Instrument** — **five graduations registering at once**, and nothing else.
  Instrument's day-complete moment has **no expressive gesture in it at all**; the
  event is marked by the count of what registers, and by nothing else.
- **C — Loom** — the **weft band tightens** and the selvedge gains one stitch. The
  most physical of Loom's own moments, and still silent.
- **D — Aurora, Governed** — the **field moves to its strongest state**, using the
  two hooks that already exist and have never been reachable, plus one
  deliberate haptic.

**All four honour the same two constraints — no routine confetti, and a
celebration earned by a milestone — and all four replace the snackbar.** They
differ in what physically happens, and B's is arguably a celebration only in the
loosest sense, which B states as a risk.

### Q13 — Should the three "themes" become identities?

*The audit's evidence: the registry holds one entry; a superseded decision
originally described twelve launch concepts × light/dark and called themes a
headline feature; the gallery, the registry, a persisted key and a settings row
all exist for the *identity* interpretation and currently serve the
*environment* one; the gallery misreports the active theme on a fresh install
because the persisted default is a deleted identifier; and the gallery renders
each card's labels in the *currently active* theme, so a theme gallery can never
show a theme in its own context.*

- **A, B and C do not address the gallery**, because their concepts do not rest
  on the Aurora identity and have no use for a multi-theme registry. **This is a
  real cost of each and it is stated in its own risks section:** the registry, the
  CompositionLocal, the persisted selection and the gallery all exist as product
  capability, and three of the four directions leave that capability with one
  entry.
- **D is the direction that owns the question**, because D keeps the Aurora
  identity — and D records that the product already has the *architecture* for
  an identity choice (registry, CompositionLocal, persisted key, gallery,
  settings row) serving an *environment* choice, which is the audit's exact
  finding. **D does not answer it.** It notes the one-line default fix, and that a
  theme gallery which renders each card in its own context is the natural home
  for whatever a later stage produces. **Answering Q13 is a product decision, and
  Phase 2A does not make it.**

### Q14 — How much of the Hero's machinery is load-bearing?

*The audit's evidence: the larger hero is 771 lines with three custom draw layers,
a 24-sample blend, a 48-sample veil and five anchor constants, arrived at through
eight issues and nine decision records, with a documented history of the same
margin landing in the wrong place three separate times; the app's *own* source
calls a 222-line version on the other screen "the reference composition". The
audit recommends establishing which layers still prevent an observed defect
before exploring a new hero.*

- **A, B and C remove the hero's artwork and therefore the question with it.** All
  three delete the photographic surface, so none of them carries the machinery
  forward, and none of them can be said to have answered the question — they have
  removed the thing the question was about.
- **D adopts the app's own recorded conclusion** — the smaller composition is the
  reference — which is a strong and well-evidenced answer in the shape the audit
  asked for, **but D answers it by not needing the machinery rather than by
  establishing which of its layers still prevents a defect.** **D states that
  limitation in its own risks section rather than claiming the question is
  settled.**

### The four thematic questions, mapped the same way

**Habit visualisation.** A — a tick in a ruled cell, with a struck cell for a miss
and no rule at all for a rest. B — a filled engraved channel, a struck diagonal,
or no channel. C — a tight pick, a loose pick, or bare warp. D — the existing
filled disc / ring / dot, plus a struck ring for a genuinely missed day. **The
vocabulary's internal logic is the same in all four; the mark differs, and the
one that treats a rest day as an *absence* is C.**

**Streak visualisation.** A — a figure with a run spine. B — a run length with a
direction mark, plus the multiplication. C — a full-height spine with a cut. D —
a figure with a state. **All four delete the flame, and all four make the number
larger than the habit's name, which the shipped app gets backwards at 12 sp
against 20 sp.**

**Progress visualisation.** A — one mark family in one vocabulary at every size.
B — one instrument, four calibrated bands. C — one swatch at two scales. D — one
mark family with four states, propagated to five surfaces. **All four converge on
the audit's own highest-leverage structural item, and all four reach it from a
different metaphor.** The shipped product has seven representations; each
direction states how many it has: A has one, B has one, C has one, D has one.

**Onboarding argument.** A — keep the record. B — mass times velocity. C — some
days you do it, some days it isn't asked. D — what it is, what it does, nothing
leaves this phone, start. **All four put the offline-and-on-device promise before
the user commits, and all four argue the Activity concept in the user's own terms
rather than setting a mood.**

**Motion language.** A — settle, wipe, dry. B — step, resolve, never spring
except on the completion control. C — lay a stitch, tighten the band, never
travel. D — change-triggered, never wall-clocked, and the two shipped infinite
loops removed. **All four remove continuous ambient animation, and all four
replace the snackbar celebration with something that is earned by a milestone.**

---

## 11. Existing Strengths Mapping

**Preservation is not automatic.** Each of the nine strengths Phase 1 named, and
each of the eight items the user named, is asked of every direction: *does it
survive, and if so, how is it transformed?* "It survives" is not an acceptable
answer on its own. A strength that survives unchanged in a direction that deleted
everything else is either an oversight or a failure of nerve, and both are worth
naming.

### 11.1 The nine strengths Phase 1 named

**1. `CompletionButton` — the app's one complete stateful control.** 48 dp, a
checkbox role, a spring pop, a haptic, and a content description that names the
*action* rather than the shape and changes wording for target-based activities so
it never promises a one-tap toggle it cannot deliver.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, de-emphasised.** The disc survives; the **spring pop does not**, because a printed record does not bounce. | Press keeps the control's real contribution — the *action-naming label* — and removes the pop. The label is the strength; the physics is not. Press also reuses the control's 48 dp target as the row's only interactive element, so the row is a ledger entry with one mark. |
| **B — Instrument** | **Yes, kept intact including the pop.** B is the only direction that preserves the control unchanged, because the audit identifies it as the product's one genuinely well-behaved stateful control. | B keeps the pop on the completion control *only*, and removes springs from everything else, so the control is the single place the product is allowed to be springy. **This is the argument for B's Q6-minimalism applied to motion: keep the one thing that was already right.** |
| **C — Loom** | **Yes, de-emphasised.** The disc survives; the pop is kept but only because C argues the audit's judgement that the control is the product's best state machine outweighs the metaphor's preference for stillness. | C's reasoning is stated openly: *bouncing cloth is not a thing, and this control is the one place the product earns the right to be exempt.* The label contract is kept intact. |
| **D — Aurora, Governed** | **Yes, completely unchanged.** | D changes nothing about it, and states that as a **constant across all four directions** per the audit's process recommendation. D's only change is the number beside it. **This is the strength a controlled evolution is most entitled to keep, and D keeps it without a single edit.** |

**2. The manage / act / understand division of labour.** Enforced by component
choice, not convention: the management list has no completion control, no streak,
no progress and no history; Home has no management controls.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, as a fixed constraint.** | Press adds a *layout* expression of it: management is ruled columns, action is a single sheet, history is a spine. The division becomes visible as three different structural grammars, which is a genuine addition rather than a restatement. |
| **B — Instrument** | **Yes, as a fixed constraint.** | B adds a *type* expression of it: the management list is a settings faceplate, action is a readout with channels, history is a measurement with an axis. **Same idea, three different instruments.** |
| **C — Loom** | **Yes, as a fixed constraint.** | C adds a *material* expression: management is unwound, action is being woven, history is already cloth. **The division becomes a stage of the making**, which is C's most distinctive contribution to it. |
| **D — Aurora, Governed** | **Yes, unchanged and reinforced.** | D adds one thing: the **same accent tile at 44 dp and 72 dp becomes a shared-element transition**, so the relationship between a row and its detail is now animated. **D is the only direction that animates the division rather than expressing it statically.** |

**3. `HabitDetailScreen.DayCell` — the only three-state data mark in the
product**, and the template the rest of the visualisation layer was never built
from.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Semantics survive; the shapes do not.** | A gradient-filled circle with a check, a ring, and a dim dot become a **tick, a struck cell, and the absence of a rule.** A's reason is stated: a dim dot still *draws* a rest day, and the audit's own framing is that this is the difference between respecting rest and manufacturing guilt. **A's version removes the rest day's footprint entirely.** |
| **B — Instrument** | **Semantics survive; the shapes do not.** | A circle, a ring and a dot become a **filled channel, a struck diagonal, and no channel at all.** B's reason is a dot's inability to express a quantity, and B extends the mark to a **graduated channel** for target-based habits — contingent on a data change B names. |
| **C — Loom** | **Semantics survive; the shapes do not.** | A circle, a ring and a dot become a **tight pick, a loose pick, and bare warp.** C's reason is native: a pattern that does not call for a pick on a given warp thread simply has no pick there, and a competent weaver does not read the empty space as a failure. **C's version expresses the requirement most directly, and it requires nothing to be decoded.** |
| **D — Aurora, Governed** | **Completely unchanged — and propagated.** | D keeps the exact marks and adds a **fourth state** for a genuinely missed scheduled day, because the audit's evidence is that the calendar and the weekly bars discard a scheduled count they already hold. **D is the only direction that treats this strength as a template to be propagated rather than a shape to be replaced — which is the audit's own stated reading of it.** |

**4. `MomentumHeroArtwork`'s caller contract.** One artwork pair for the whole
product, theme-selected by a pure function, callers passing band amounts and
unable to art-direct it, no tint, no filter, no re-colour, and a KDoc honest about
its own dead parameter.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Contract survives; the artwork does not.** Press keeps the *pipeline discipline* — one asset, theme-selected, callers cannot art-direct — and states honestly that it has **no use for the pair.** A names this as a real loss in its own risks. | A's position is that a print-derived identity does not photograph, and that carrying an unused pipeline is worse than removing it. |
| **B — Instrument** | **Contract survives; the artwork does not.** | B's reason is that an instrument panel is a diagram, and a photograph behind a measurement is a lie about what the screen is. B states the loss. |
| **C — Loom** | **Contract survives; the artwork does not.** | C's reason is categorical: **a cloth metaphor cannot borrow photographic illustration at all**, which is also why C's onboarding must be rebuilt rather than re-themed. C states the loss. |
| **D — Aurora, Governed** | **Completely unchanged, and used.** | D keeps the pair, keeps the contract, keeps the Light variant, and keeps the two-artwork decision that the app's own source records as having been made correctly — *"the decision was made by building two artworks rather than tinting one."* D's transformation is that the header is **sized to its content** and **withheld on an empty day**, which is the audit's own radical suggestion. **D is the only direction that spends this strength.** |

**5. Manrope, bundled, offline, with three pinned instances.** A single variable
TTF, one axis, seven named instances, no platform fallback.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, and it becomes almost everything.** | Press re-cuts the scale into five registers with **numbers outranking words**, uses a real 700, and makes every changing figure tabular. **Press's identity is the type**, because a ledger is typography. |
| **B — Instrument** | **Yes, and it is split into two registers.** | B gives **labels** the tracked-inscription treatment and **numbers** the heavy treatment, which is the opposite of Press and is Instrument's typographic signature. B also argues the *usage* rule against the top of the weight axis out loud, which is the only direction that confronts that clause. |
| **C — Loom** | **Yes, and its line-height behaviour becomes the signature.** | C assigns **opposite line-height behaviour to the two large registers** — loose and untracked for cloth, tighter and tracked-out for thread — which is the cheapest and most under-appreciated lever in the whole document, and the one hardest to verify in a static image. C confines the top of the weight axis to the thread register only. |
| **D — Aurora, Governed** | **Yes, and the scale is rebuilt around it.** | D is the direction that makes the audit's flagship finding its centrepiece: a **real 700 registered from the file that already contains it**, a meaning-driven register order, tabular figures, per-token tracking, and a label style that is visibly a label. **D is the only direction whose largest structural change is a font registration.** |

**6. `HabitsHero` — the reference collapsing composition, named as such by the
app's own source**, 222 lines against a 771-line alternative, with no masks, no
anchors and no per-page exceptions, and text that never changes colour during
scroll because both artwork variants were built for their theme.

| | Survives? | How it is transformed |
|---|---|---|
| **A, B, C** | **Not applicable — the composition is removed with the artwork.** | Stated as a cost in each direction's risks: the app's own conclusion about hero machinery is not carried forward, because the question itself is removed. |
| **D — Aurora, Governed** | **Yes, and adopted as the standard.** | D makes it the template for Home, which is the audit's own layout recommendation. **D is the only direction that acts on the app's self-diagnosis**, and D records the corresponding limit: it answers the machinery question by not needing the machinery, not by establishing which layers still prevent a defect. |

**7. Reduced motion, implemented rather than promised — in the places that were
done.** Switching to a zero duration or nulling the spec rather than shortening
one; gating a glow so 0% pays nothing; returning a constant so a disabled glow
composes no transition at all.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, as a pattern, extended across the whole inventory.** | A's motion list is short enough that every entry has a defined non-animated behaviour, and every behaviour is a **substitution, not a shortening**. A also removes the standalone accent-literal from the reduced-motion button, which is a small but real fix. |
| **B — Instrument** | **Yes, extended, and paired with the damping ratio.** | B keeps the pattern and adds the thing the audit says is missing: **every spring gets an explicit damping ratio**, because eight raw springs currently take a library default. B's inventory is short and every entry has a defined static behaviour. |
| **C — Loom** | **Yes, extended.** | C keeps the substitution pattern and applies it to a larger inventory than the shipped product has, because C adds a *typed* visual behaviour (a tapped mark's border) that has to be neutralised too. |
| **D — Aurora, Governed** | **Yes, and the distribution is fixed — which is the actual finding.** | D threads one override through the theme so **all fourteen call sites** read one source, and names the source-text test that currently pins one site to the null form and would need updating. **D is the only direction that fixes the truthfulness failure rather than only avoiding making a new one**, and that is D's accessibility argument in full. |

**8. Accessibility that goes past the checklist.** A spoken description composed
from a day cell's descendants with a role and an action; action-naming labels;
heading semantics on section headers; the completion control's checkbox role.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, and extended to a case the app currently fails.** | Because Press's marks are *structural* — presence of a rule, presence of a strike, presence of a tick — the never-by-colour-alone floor is satisfied **by construction** rather than by a redundant second channel. A also states the rule that no text is placed against a *slack* pick, which is a legibility rule rather than a colour rule. |
| **B — Instrument** | **Yes, and the channel vocabulary makes it a first-class concern.** | B's marks are *geometric* — a filled channel, a struck diagonal, an uncut gap — and B applies the **spoken description** the product already gets right in its calendar cell to the habit week, which the audit records as having no description at all. B's own channels gain a description, which the shipped weekly bars do not have. |
| **C — Loom** | **Yes, and the rest day needs none.** | C's rest-day mark is an **absence**, so there is no cell to describe beyond "not scheduled", which is a shorter and more robust description. C states its legibility risk directly: four glyphs put all of its legibility weight on typography, which is the axis the audit says is already weakest. |
| **D — Aurora, Governed** | **Yes, and applied where it is currently missing.** | D keeps every existing measure and **adds the spoken description to the habit week**, which is the product's best accessibility engineering applied to the product's best data mark. D's fourth state gets a description too, which nothing today has. |

**9. The Settings copy, and the offline promise.** The best-written text in the
product, and the only place the offline promise appears.

| | Survives? | How it is transformed |
|---|---|---|
| **A, B, C, D** | **Yes, in all four — and in all four it moves.** | The audit's finding is that the writing standard already exists and onboarding's problem is *placement*. **All four directions put that Settings line into onboarding, in the user's own terms, as the page a first-time user reaches before they commit — and none of them invents new copy to replace it.** A renders it under *"Nothing leaves this phone."*; B renders it as a scale that stops at the device; C renders it as a swatch with a finished selvedge and nothing past it; D renders it as a Light artwork variant with the theme inherited. **This is the one strength all four directions transform identically, and it is the clearest example in this document of a strength being *moved* rather than *kept*.** |

### 11.2 The eight items the user named

**A. `CompletionButton`.** Covered above, item 1. **A drops the pop; B keeps it
entirely; C keeps it as a declared exemption; D does not touch it.**

**B. The manage / act / understand division.** Covered above, item 2. **All four
keep it as a fixed constraint, and all four add a structural, typographic,
material or animated expression of it — so the division is *made visible* rather
than merely preserved.**

**C. `HabitDetailScreen.DayCell`.** Covered above, item 3. **This is the sharpest
disagreement in the document: A, B and C all replace its shapes, for three
different stated reasons; D keeps it exactly and propagates its vocabulary. The
reason all three are still defensible is that the audit's requirement is about
**the three states being distinguishable**, not about the shapes — and the
strongest single argument the audit makes about this component is that it should
be the *template* for everything else, not that its circles are sacred.**

**D. The pure-Kotlin engines.** `Engines.kt` and the habit-overview use case both
have zero Android imports and are unit-tested without a device.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, untouched, and the constraint is honoured.** | Press's three-state and four-state vocabulary needs no logic in a composable, and Press says so: the pressed stamp and the absent rule are drawn, not computed. **A adds no engine work and pretends to none.** |
| **B — Instrument** | **Yes, and B is the direction that most wants to change it — and says so instead of assuming.** | B's graduated channel, its thirty-division consistency readout and its per-habit multiplication **all need a use case that does not exist.** B names each dependency explicitly and **depict the binary week, which works today.** B also notes that the multiplication's mass half is currently a *configuration* rather than a computed quantity, which is a conceptual liability and not an engineering one. |
| **C — Loom** | **Yes, and the same dependency as B, stated independently.** | C's partly-laid pick needs per-day values that do not leave the repository. C states it, and distinguishes it from the bar-chart prohibition, whose stated reason is a claim about the week rather than about values. **C depicts the binary week.** |
| **D — Aurora, Governed** | **Yes, and D is the only direction that actually *spends* this asset — which is the audit's sharpest product finding.** | The engine already computes **missed occurrences** and a **consistency score**, and **both have zero presentation call sites.** D puts the consistency score at the top of Insights and the missed count beside it, with windows printed, and it converts the bare-word trend into a signed figure with a window. **D needs no new engine work at all — it needs a layout.** The audit's own words: *"a metric that is computed and hidden is indistinguishable, to the user, from a metric that does not exist."* |

**E. Offline persistence.** Room and DataStore, append-only completion history,
no network permission, no account.

| | Survives? | How it is transformed |
|---|---|---|
| **A, B, C** | **Yes — and each turns it into the onboarding's third page.** | All three make the offline promise the **visual climax of the first run** in three different ways: a finished page edge with nothing past it, a calibrated scale that stops at the device, and a selvedge with nothing beyond it. **In all three, the privacy fact is argued visually rather than asserted in a paragraph**, and none of them adds a feature that would contradict it. |
| **D — Aurora, Governed** | **Yes — and D is the only direction that has to keep the artwork and the theme inheritance working.** | D's onboarding is the app, not a layer above it, so the offline promise appears on a screen that is **in the user's real environment** — which is only possible because the artwork is re-themeable. D states the derivation: the vector sources that would permit a Light variant exist in the repository, unreferenced, because a migration flattened them. **D therefore makes the first-run promise *thematically* true, not only factually.** |

**F. The unified `Activity` concept.** One model differentiated by category,
recurrence and target, with an explicit habit/task discriminator and a type gate
that refuses conversion. Habits and tasks share a visual grammar by decision.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **Yes, and made the row's identity.** | One row, one name weight, one metadata contract, and **which slots are filled is a data difference, not a component difference** — the same rule the shipped product already uses when it suppresses the streak badge for tasks. A's addition: the three metadata slots become **separately weighted and separately truncatable**, which fixes a real pre-joined-string defect. |
| **B — Instrument** | **Yes, and the *unit* becomes the discriminator.** | B's distinguishing mark between a habit and a task is the **unit** — `20 p`, `30 min`, `8 ×`, `✓` — which is text, is already correct at every size, and says *what kind of thing this is* in a way no pictograph or colour ever did. **B is the only direction that solves the per-activity-identity question with a word.** |
| **C — Loom** | **Yes, and made dye.** | One dyed warp thread per activity, the **same dye on every surface that shows it** — list row, detail header, creation card, insights row, calendar row — which is the direct answer to the audit's finding that a habit's identity currently changes size, radius, colour and container five times between those surfaces. **C's metaphor is what makes per-activity colour honest, and therefore what makes the unified concept legible.** |
| **D — Aurora, Governed** | **Yes, with one registry and two environments.** | D collapses the three drifted key vocabularies into **one authoritative registry** that the picker, the renderer, the tile and the detail header all read; gives the app's own custom-habit template a real glyph instead of a fallback star in a fallback colour; and **gives the item hues two environments or removes them.** D is the only direction that fixes the drift at the data level rather than at the visual level. |

**G. Home / Habits artwork ambition.** Two photographic hero assets, an
artwork contract, an honest KDoc, and a documented, deliberate readability
strategy.

| | Survives? | How it is transformed |
|---|---|---|
| **A, B, C** | **No, and each says so in its own risks section.** | A: a print-derived identity does not photograph. B: a photograph behind a measurement is a lie about the screen. C: a cloth metaphor cannot borrow photographic illustration. **All three name the loss of a genuinely good asset rather than disguising it as a simplification** — and C's case has a second consequence, that its onboarding must be rebuilt rather than re-themed. |
| **D — Aurora, Governed** | **Yes, and D is the only direction that both keeps it and fixes how it is used.** | D keeps the pair, keeps the contract, keeps the Light variant, and fixes the two real defects: the header is **sized to its content**, so an empty day is empty; and the **top band is owned by the artwork with no seam**, which is the outcome the shipped decision record already established. **D also adopts the app's own named reference composition rather than building a fourth one.** |

**H. `ProgressOrb` craft.** A four-layer arc stack whose draw order solves *"the
glow must never veil the value"*, with a KDoc that says *"compact by default"*
and an implementation that runs a permanent loop. A correctly reasoned component
arguing with itself.

| | Survives? | How it is transformed |
|---|---|---|
| **A — Press** | **The reasoning survives; the component does not.** | A keeps the **draw-order principle** — the glow must never veil the value — as a general rule, and deletes the component. The figure is a number, not a ring; there is no glow to veil anything, so the problem is removed rather than solved. |
| **B — Instrument** | **The reasoning survives; the component does not, and B states a design tension with the product's own specification.** | B keeps the principle and the idea of a calibrated channel, and deletes the loop. **B's declared tension: the product specification describes the product as "premium, playful, calming" and B is premium and calibrated and is neither playful nor calming.** B argues with that adjective list on purpose and says so here, rather than quietly leaving the tension unstated. |
| **C — Loom** | **The reasoning survives; the component does not.** | C keeps the principle and replaces the ring with a **swatch whose density is the score**, which is the same insight — the mark must not obscure the value — arrived at from cloth. C also removes the loop, on the grounds that **an empty warp genuinely has nothing moving in it.** |
| **D — Aurora, Governed** | **The reasoning survives intact as a general rule, and the component is deleted.** | D keeps the draw-order principle as a stated rule and removes the orb from Home, because the answer to the screen's question is a **figure, not a ring around a figure.** The permanent loop goes, and D's reward moment — the field moving to its strongest state — is the loop's legitimate descendant, because D preserves the distinction the audit draws between **an infinite loop on the primary screen for a value that changes a few times a day** and **a genuine reward event.** |

**A closing note on this mapping.** The strongest thing the audit found is not a
component, a colour or a layout. It is a **governance** pattern, and the honest
conclusion of this section is that **only D addresses it as a governance
problem** — by reducing roughly fourteen scattered decisions to four — while A, B
and C address it by **replacing the whole system**, which is a different and also
valid response, and which is why the four are four directions rather than one
direction at four levels of effort.

---

## 12. Image-Generation Strategy

**No images are produced by this stage.** This section is the specification the
image-generation stage will read.

### 12.1 Four products, twenty screens — and why the count is what it is

The generation stage produces **twenty images: four directions × five screens.**
Each direction's five screens must read as **one product**, not as five related
screens, because they are generated separately. That requirement is not
aesthetic — it is the reason §14 exists at all. A set of five screens that drift
is not a direction; it is five directions, and the exploration produces nothing.

**The five screens, and why exactly these five.** Home / Today, Habit Detail,
Tasks, Insights and Onboarding were chosen because between them they exercise
every property a direction claims:

- **Home** exercises the day's answer, the row, the primary figure, the hero, the
  navigation, the ambient field, and the completion control.
- **Habit Detail** exercises the **mark vocabulary** — the hardest and most
  important thing in this exercise, and the thing every direction has to get right
  — plus a detail push, the metadata contract and the streak expression.
- **Tasks** exercises **vocabulary and hierarchy**: whether the direction can make
  a management surface look like one, and whether it can resist adding a
  completion control.
- **Insights** exercises **meaning**: whether the direction can show the
  consistency score and the missed count, and whether its mark vocabulary holds at
  thirty items and one-pixel cells.
- **Onboarding** exercises the **argument**, the **first-impression cleanliness**
  the primary skill demands of a first screen, and — uniquely — **whether the
  direction can produce a Light rendering of its first-run surface**, which is
  the audit's most concrete theme-blindness finding.

**What is deliberately not in the twenty:** a control rendering of the current
app. The audit's process recommendation requires a control at the same content in
both environments, and that is a **Phase 2B obligation** — it needs the current
app actually run, not described, and no rendered reference exists in this
repository. **The control is not this stage's deliverable and this document does
not substitute a description for it.**

### 12.2 The design bible, locked per direction before any image is generated

Before generating the first image of a direction, the generator locks the
following, and **does not vary any of it across the direction's five screens**:

| Locked property | What it fixes |
|---|---|
| **Platform mode** | **Android-native.** Material tab grammar, Android app-bar behaviour, bottom-sheet and dialog idioms, real safe areas. Never cross-platform-neutral, never iOS |
| **Device frame** | one Android phone, portrait, centred, slim even bezel, **even canvas margins on all four sides**, the device never touching an edge, content clearly the hero, the frame quiet. **Identical across all twenty images** so the set reads as one presentation |
| **Type family** | Manrope throughout. One face, five registers, real Bold available, tabular figures on changing numbers |
| **Type mood** | per direction, per §14. A's numbers outrank its words; B's labels are engraved and its numbers are heavy; C's two large registers use opposite line-height behaviour; D's figures are heavy and its labels are visibly labels |
| **Palette logic** | per direction. A: warm paper and one deep madder. B: a cool plate and one signal colour. C: undyed linen or indigo dye and one madder. D: a cool canvas, a progress accent and a distinct completion hue |
| **Surface count** | per direction. A: three materials. B: three. C: four. D: four levels, with glass only on floating layers |
| **Corner-radius logic** | per direction. Small and squarish in A, tightest and most square in B, small and softly cut in C, the existing small-to-medium vocabulary by role in D |
| **Icon treatment** | per direction, per §10 Q6. A: weight-led, monochrome, ink. B: weight-flat, four structural glyphs. C: four structural glyphs, no identity role. D: weight-tolerant, one family, monochrome |
| **Navigation model** | locked for the direction. A: flush rule bar. B: four-slot segmented rail. C: selvedge band. D: floating translucent bar |
| **Texture intensity** | per direction. A: an 8% paper fibre, absent behind small text. B: a printed reference grid in Light, none in Dark. C: a visible weave, absent behind small text. D: a soft ambient field, no texture on content |
| **Imagery** | **A, B and C produce no photography at all.** D is the only direction that uses it, and only on Home and Onboarding, chosen by a pure theme function |
| **Copy register** | one register, sentence case, terminal punctuation, no marketing filler, no emoji, no exclamation marks in success copy. The banned phrase list is in §15 |
| **Component family** | per direction. A: ruled panels and rule-separated rows. B: bevelled plates and engraved channels. C: selvedges and running stitches. D: quiet cards with dividers and a floating bar |
| **Motion language** | implied in the stills: no element shown mid-glow, no element shown with a halo, no element shown in a transition state |

**Variation that IS allowed across the five screens of a direction:** the top-area
composition; image-to-text balance, where imagery exists; content density; the
number of blocks on screen; the visual tempo; and which element is the primary
visual. **That is what stops five screens from being one screen five times.**

### 12.3 What the generator must do

1. **Produce one complete, internally consistent product per direction.** Five
   images per direction, all five reading as the same app on the same day.
2. **Apply the §4.6 content fixture exactly**, in both environments, on every
   screen that shows data. The same five activities, the same three completions,
   the same two target-based items, the same two rest days, the same paused habit,
   the same two tasks, the same 78% consistency, the same missed count, the same
   broken streak, the same rest days. **The content is the test.** A direction
   that looks composed on a full day and collapses on the empty day has not been
   evaluated.
3. **Show the empty day and the 100% day for the Home screen**, as a second and
   third render of that screen, because those are the two states the audit
   identifies as the most important and the most broken. **This is the one place
   the twenty-screen count is exceeded, and it is deliberate:** twenty is the
   floor, not the ceiling.
4. **Show both environments for every screen.** A direction delivered in one
   environment has not been evaluated against the Light-depth finding.
5. **Get the numeric hierarchy right, every time.** The answer to "how am I doing
   today" is the largest type on the Home screen. A streak count is larger than
   the habit's name. A week's answer is larger than its label. A habit's live
   target progress is larger than its metadata. **In no render may the greeting,
   the screen title or a metadata line be the largest element on a screen whose
   purpose is a number.** This is the single most checkable requirement in the
   document and it is checkable by eye.
6. **Make missed and rest visibly different in every mark vocabulary**, and make
   at least one rest day **genuinely absent** in A, B and C.
7. **Read as a phone app used by one person**, not as a product page, a store
   listing, a portfolio piece or a concept board. One screen per image; no
   collages; no four-up grids of tiny screens.
8. **Respect the accessibility floors visibly.** 48 dp controls, text large
   enough to read at normal viewing size, contrast that does not look strained,
   state never carried by colour alone.
9. **Keep the device frame present, clean and even**, and subordinate to the
   content. A frame that dominates the image has failed.
10. **Regenerate rather than settle.** A screen whose text is too small, whose
    navigation looks fake, whose composition reads as a website, or whose cards
    are nested is **not finished**, per the primary skill's own regeneration rule.
    The first acceptable render is not the deliverable.

### 12.4 What the generator must NOT do

1. **Produce a landing page, a hero section, a web navigation, a marketing header,
   a footer, or a desktop-first composition** inside a phone. Banned as product
   direction and banned in the briefs.
2. **Use Tailwind's look, a web easing-curve vocabulary, a scroll-reveal
   choreography, a hover state, a cursor, or a fixed sticky nav.** An Android app
   has no hover state and a phone has no 100-viewport scroll.
3. **Converge.** If direction A's images begin to resemble direction D's, one of
   them has failed. **A is a print-derived ledger with a light-toned object on a
   dark field; D is a tonal environment with a photographic header and a floating
   glass bar. They must be unmistakably different products.**
4. **Converge in the other direction either.** B and C must not both read as
   "textured"; B has a printed reference grid and machined edges, C has a visible
   weave and stitched edges. **If either looks like the other, one of them has
   failed.**
5. **Put a fire, a flame, a trophy, a medal, a star, a confetti burst, an emoji, or
   a gamification level anywhere.** Not as decoration, not as a metaphor, not in a
   preview. The locked design document rejects it and the audit found the shipped
   violation.
6. **Put a progress ring, a donut, a radial percentage, or a circular percentage
   badge on the Home screen.** In A, B and C the answer is a figure and there is no
   ring. In D the figure is the answer and the ring is gone.
7. **Draw a bar chart as the primary weekly view on any screen.**
8. **Make a missed day and a rest day look the same.** Anywhere. In any mark.
9. **Use purple-blue fintech gradients, random glass cards, ambient blobs with no
   purpose, fake neon, oversized radii on everything, over-rendered glossy
   surfaces, or a pink-and-orange SaaS gradient.** All of these are the standard
   AI-mobile failure modes and all are named in the rejection criteria.
10. **Show a stat tile with no meaning** — a value, a label, and no comparison, no
    mark, no window and no interpretation.
11. **Add anything the product cannot do.** No account, no sign-in, no cloud icon,
    no sync status, no "last synced", no share button, no leaderboard, no friend,
    no team, no streak league, no social feed, no AI coach, no wearable, no
    location, no health-platform permission, no camera. **If a render implies any
    of these, it has shown a product that does not exist and does not exist on
    purpose.**
12. **Generate a dark-only first-run surface** in any direction. Every direction's
    onboarding must have a Light rendering, because the audit's most concrete
    theme-blindness finding is a first-run user walking through four near-black
    screens and arriving at a light app.
13. **Use a lowercase enum name, a truncated sentence, or a bare word as a
    value.** "Insufficient data" must not appear. A trend must not be the word
    "Declining" alone.
14. **Show fewer than 48 dp of touch target, or a fixed-height row that clips at
    large text.** These are accessibility floors, and a direction that cannot be
    rendered at large font scale has failed rather than varied.
15. **Draw the bottom navigation as a web tab bar, a pill, a floating dock with a
    blur, or a Material bottom app bar.** Each direction's navigation is specified
    and the generator must follow *that* one.

### 12.5 Anti-patterns to remove before delivery, per the primary skill

The primary skill names a specific set of mobile tells, and every one of them is a
rejection criterion in §15. The generator's checklist before it delivers a set:

- Does this feel like a real mobile app and not a website in a phone?
- Are the safe areas respected visually — status bar, gesture region, bottom inset?
- Is the first screen clean enough — one focal point, no wall of chips or stats?
- Is the copy short enough — one or two lines per screen, no filler?
- Is the type comfortably readable and not too small at normal viewing size?
- Is the layout free of box-in-box nesting?
- Are image moments purposeful and consistent — and in A, B and C, are there
  **none**, correctly?
- Is the flow varied across the five screens without breaking the design system?
- Do all five screens clearly belong to the same app?
- Is the device framing clean and even on all four sides?
- Is the iconography intentional rather than library-default?
- Is the phone border clean and not stealing attention from the content?
- Is the first screen of the onboarding calm and uncluttered?
- Is the hierarchy on every screen such that the **most important number is the
  biggest type**?

---

## 13. Generation Order

**Nothing is generated by this stage.** This is the recommended order for the
stage that will, and it is ordered by *what establishes each direction's visual
DNA first* — because a direction's Home screen sets its type register, its
surface count, its mark vocabulary, its navigation model and its palette in a
single image, and every later screen is judged against it.

### 13.1 The order, and the reason for it

| # | Image | Why here |
|---|---|---|
| 1 | **A — Press: Home / Today** | Home establishes the type register, the three materials, the accent's three roles, the column composition, the rule system and the flush navigation bar in one image. **Everything else in A is judged against it.** |
| 2 | **D — Aurora, Governed: Home / Today** | Deliberately second, not last. D is the control, and generating it early forces the three radical directions to be *different from the control* rather than variations of it — which is the entire risk §4.1 exists to prevent. |
| 3 | **B — Instrument: Home / Today** | B's readout and its five-tick channel establish the opposite relationship between numbers and labels to both A and D. Generating it third tests whether A and D have already converged on the same "big number" answer. |
| 4 | **C — Loom: Home / Today** | C is the one direction whose primary visual is not a number at all. Generating it fourth is the real test of non-convergence: **if C's Home screen looks like the other three with a texture, C has failed.** |
| 5 | **A — Press: Habit Detail** | The mark vocabulary is the hardest and most important thing in this exercise, and it has to be read in every direction, and A's version is the most *subtractive* — no rule at all for a rest day. If A's mark row reads, A's metaphor holds. |
| 6 | **B — Instrument: Habit Detail** | B's version is the most *geometric* and the one that most needs a rest day to be genuinely **uncut**. B's multi-day rest gap and its multiplication both need checking here. |
| 7 | **C — Loom: Habit Detail** | C's version is the most *material* and the one that most needs the **loose pick and the bare warp to be distinguishable at 32 dp**. This is C's hardest legibility test. |
| 8 | **D — Aurora, Governed: Habit Detail** | D's version is the most *conservative* and the one that must add a **fourth state** — a struck ring — without breaking the existing three. D's mark row is also where the fourth state's necessity is visible or isn't. |
| 9 | **A — Press: Insights** | The 7-day state row in A's vocabulary, plus the ranking, tests whether A's mark family holds at a different scale and in a different register. |
| 10 | **B — Instrument: Insights** | B's 30-channel strip is the densest data image in the whole set and the one most likely to fail legibility. **Generate it early enough that a legibility failure is discovered with time to fix it.** |
| 11 | **C — Loom: Insights** | C's 30-column swatch, with 6 bare columns and 3 loose columns that must all be distinguishable on indigo, is C's second-hardest test. |
| 12 | **D — Aurora, Governed: Insights** | D's 30-day strip with a struck ring and a muted dot, plus the consistency score as the largest element, tests whether D's most conservative vocabulary carries the new state. |
| 13 | **A — Press: Tasks** | Now that the mark and the data vocabulary are proven, Tasks tests the direction's **vocabulary discipline** — one noun per surface, one overflow per row, two labelled axes. |
| 14 | **B — Instrument: Tasks** | B's filter axes and its engraved field are the two places B is most likely to drift back toward generic Material. |
| 15 | **C — Loom: Tasks** | C's running stitches at small size are the direction's most fragile typographic element, and this is where it is tested. |
| 16 | **D — Aurora, Governed: Tasks** | D's single card with dividers, one overflow per row, and a floating bar whose labels are the app's smallest type — the three things D is fixing. |
| 17 | **A — Press: Onboarding** | The first-run page in A's paper, with the finished page edge. Also the first *first screen* of the set, so the primary skill's stricter first-screen bar applies. |
| 18 | **B — Instrument: Onboarding** | B's terminating scale. |
| 19 | **C — Loom: Onboarding** | C's teaching diagram — the only direction with an explanatory diagram in its first run, and the one that most needs the diagram to read at 400 dpi. |
| 20 | **D — Aurora, Governed: Onboarding** | D's Light artwork variant, and the first render that tests whether a photographic first-run surface can be *themed*. |

### 13.2 The two additional renders, generated after the twenty

| # | Image | Why |
|---|---|---|
| 21 | **Each direction: the empty day**, Home / Today | The audit's most broken state and the one every direction claims to have fixed. **A direction that fixes the empty day in a spec and renders a full-looking empty day has not fixed it.** |
| 22 | **Each direction: the 100% day**, Home / Today | The other state the audit names. Tests the reward moment in the still — the pressed stamp, the registering graduations, the tightening band, the field's strongest state — and whether it reads as an event or as nothing. |

### 13.3 Why the onboarding screens are last, and this is a real judgement

The first-run screen is the strictest test of a direction's *clarity*, because it
is the screen with the least content and the highest expectation. It is also the
screen on which a web pattern shows up fastest, because a marketing composition
wants to be a landing page. **Putting onboarding last means the direction has
already been stress-tested on a dense screen, a sparse screen and a data screen
before it is asked to be simple.** A direction that cannot be simple after
proving it can be complex is not a direction; it is a style.

### 13.4 What the generator should do if two directions start to look alike

**Stop, and re-read that direction's §8.** Non-convergence is the primary failure
mode of this stage and the hardest to see from inside it, because the last image
generated always looks like the one before it. The specific things to re-check,
per §4.1: the metaphor, the surface material, the card construction, the mark
vocabulary, the navigation model, the Light mechanism, and the Q3 answer. **If
two directions share an answer on any two of those seven, one of them has
drifted and the earlier brief is the one that was right.**

---

## 14. Cross-Screen Consistency Rules

**These are the properties that must remain IDENTICAL across all five screens
within one direction**, and — this is the point — they must differ **between**
directions. Because the twenty images are generated separately, consistency is not
a property the generator will discover; it is a property this document has to
state.

### 14.1 Must be identical within a direction

1. **Typeface.** One family: Manrope. Never a second family, never a fallback,
   never a system default, never a display serif, never a monospace companion.
2. **The type register set.** The same five registers, in the same order of
   emphasis, on every screen of the direction. **In particular, the direction's
   primary-figure size is the same on Home and on Insights** — the number for
   "how am I doing today" and the number for "how consistent have you been" are
   the same kind of number and must be the same kind of type.
3. **Figure conventions.** Real Bold available; tabular figures on every changing
   number; per-token tracking; value and scale distinguished within one lockup.
   **A direction may not show a proportional figure on one screen and a tabular
   one on another.**
4. **The mark vocabulary.** The same complete/not-incomplete/not-scheduled/missed
   vocabulary, the same shapes, the same relative weights, at every size and on
   every screen. **The habit week's mark and the Insights strip's mark must be
   recognisably the same object** — that is the audit's single strongest
   structural argument, and a direction that draws a different mark on Insights
   has failed regardless of how good it looks.
5. **The surface count and the rule for what earns each level.** Never more levels
   than the direction declares. Never a new fill value. Never a new alpha.
6. **The accent's roles.** The same roles, the same count, on every screen. A
   direction whose accent appears in three roles on one screen and five on another
   has two design systems.
7. **The icon treatment.** One family, one optical density, monochrome, one
   size relationship to the type. Never a pictograph where the direction's
   vocabulary says a mark belongs, and never a mark where an icon belongs.
8. **The navigation model.** The same bar, the same slot geometry, the same active
   state, the same label register — on all four tab destinations and nowhere else.
   **Settings is never a tab; the calendar is never a tab.**
9. **The radius logic.** The same relationship between container radius, inner
   element radius, and squared elements. Never one radius applied to everything.
10. **The row model.** The same row height, the same metadata slot positions, the
    same completion control size, the same overflow treatment — on Home, on Tasks
    and on any list. **A task row and a habit row are the same object with
    different slots filled**, because the unified concept is a locked decision.
11. **The composition grid.** The same margins, the same gutter, the same
    relationship between the content edge and the screen edge on every screen.
12. **The light/dark strategy.** The same two environments, the same depth
    mechanism in each, the same relationship between them. **A direction may not
    render one screen with a mechanism its other screens do not use.**
13. **The copy register.** One register. Sentence case. Terminal punctuation.
    No emoji. No exclamation marks in success copy. No marketing filler. No
    sentence-case outlier among title-case headlines. No enum name as a value.
14. **The motion personality, as implied in stills.** No element shown mid-glow,
    mid-sweep, mid-transition, or with a halo or a breathing ring. **If a still
    shows a progress orb at full intensity with its halo, the still has depicted
    decoration.**
15. **The texture rules.** The same texture, at the same intensity, in the same
    places — and **absent behind small text**, in every direction that has a
    texture at all.

### 14.2 Must be different between directions

**The same list, read across, is where non-convergence lives.** Between the four
directions, the following must be unmistakably different, and a reader who cannot
tell two directions apart in a still has a failure:

1. **The surface material.** Paper / plate / cloth / tonal-with-glass.
2. **The depth mechanism in Light.** Cast shadow / milled bevel / thread occlusion
   / tonal ladder plus a hue-carrying shadow.
3. **The Q3 answer.** A place / a number on a calibrated scale / a shape / a
   number at figure scale.
4. **The mark vocabulary's shapes.** Circle-and-ring-and-dot / channel / pick /
   disc-and-ring-and-dot-plus-struck-ring.
5. **The card construction.** Ruled panel / bevelled faceplate / stitched patch /
   quiet tonal card.
6. **The navigation model.** Flush rule bar / segmented rail / selvedge / floating
   translucent bar.
7. **The typographic signature.** Numbers over words / inscriptions and figures /
   line-height inversion / heavy figures and visible labels.
8. **The motion personality.** Settle / resolve / lay / bloom.
9. **The reward gesture.** A stamp / registering graduations / a tightening band /
   a field changing state.
10. **The Q6 position.** Weight-led / weight-flat / reduced / indifferent.

### 14.3 The rules that apply to both axes, and are the ones most often broken

- **The content is identical across all twenty images.** Same day, same five
  activities, same three completions, same two rest days, same paused habit, same
  two target-based activities, same streak lengths, same 78% consistency, same
  missed count, same 0% category.
- **The hierarchy rule is identical across all twenty images.** The most
  important number is the biggest type. A streak is bigger than a name. A day's
  answer is bigger than its label. **This is the requirement that makes all four
  directions testable against the same finding, and it is the one most likely to
  be violated accidentally in a single image.**
- **The missed-versus-rest rule is identical across all twenty images.** In
  A, B and C, at least one rest day is genuinely **absent** — no mark, no rule, no
  channel, no pick. In D, a rest day is a low-weight dot and a missed day is a
  struck ring, and the two must be clearly different from each other.
- **The prohibition list is identical across all twenty images.** No fire, no
  emoji, no confetti, no trophy, no medal, no gamification, no glass default, no
  purple-blue gradient, no stat tile without meaning, no account, no cloud, no
  leaderboard, no bar chart as a weekly view, no progress ring on Home.
- **The device frame is identical across all twenty images.** One style, one
  scale, one presentation, even margins on all four sides.

---

## 15. Rejection Criteria

**A generated image that meets any of these is invalid and must be regenerated.**
These are not preferences. Each is either a named anti-pattern from the primary
skill, a locked clause of the design document, an accessibility floor, or a
specific finding from the audit. The Momentum-specific criteria are marked.

### 15.1 Generic failure modes

1. **A generic SaaS dashboard.** Equal stat cards, a generic sidebar, a generic
   header, a "Welcome back" line, a blue primary button, a purple accent.
2. **Website-like composition.** A landing-page hero, a headline with a
   supporting paragraph and two buttons, a feature row of three cards, a footer, a
   sticky marketing header, a centred hero, a desktop-first grid, a hover state.
3. **Excessive glass.** More than one translucent surface per screen, glass on
   cards, glass on rows, glass on buttons, blur on scrolling content, a "frosted
   glass" aesthetic applied as a default. **This is the locked design document's
   named prohibition and it is the shipped app's current default.**
4. **Random gradients.** A purple-blue fintech gradient, a pink-orange SaaS
   gradient, a gradient mesh with no purpose, a gradient on text, a gradient as
   an accent, more than one gradient in one composition where the direction
   specifies zero or one.
5. **Inconsistent typography.** A second typeface, a fallback face, more than one
   display weight in a family with no system behind it, a monospace companion, a
   hand-lettered wordmark, a serif, a script. Small text that is decorative rather
   than readable. **Inconsistent tracking** — some labels tracked and others not.
6. **Unrelated icon families.** A line-icon set beside a filled set, two stroke
   weights in one screen, a pictograph where a mark belongs, a mark where a
   pictograph belongs, an icon library's default look, mixed outline and filled
   in one row.
7. **Excessive floating cards.** Cards inside cards inside cards, five levels of
   framing, a card per list row, a card per stat, a card per filter.
8. **Fake 3D decoration.** Isometric illustration, extruded icons, a 3D chart, a
   glossy bevel that is not the direction's material, a soft 3D blob, a fake
   depth-of-field.
9. **Poor Android ergonomics.** A target smaller than 48 dp, a critical action in
   the top-right corner only, a nested scrollable, a gesture region with content
   under it, a bottom sheet that does not dock, a dialog that is not opaque, a
   system control rendered in the wrong idiom, a phone frame that crops.
10. **Tiny unreadable text.** Any label that would need a pinch to read. The
    audit's whole finding is that this product's metadata is already too small;
    a render that reproduces small metadata has reproduced the defect.
11. **Visual hierarchy collapse.** Everything the same size. Everything the same
    weight. Everything the same colour. No focal point. A screen with four
    competing elements and no primary one.
12. **Box-in-box nesting.** A screen that feels trapped inside its own containers.
13. **Phone-shaped websites.** The most common mobile failure and the one the
    primary skill names explicitly. **If the screen would make sense as a
    full-width web page, it is wrong.**
14. **Uneven or drifting device framing.** A different bezel, a different scale,
    a different margin on each side, a phone touching a canvas edge, a device
    that dominates the content.
15. **AI copy tells.** "Elevate your life", "unlock your potential", "seamless",
    "next-gen", "transform your day", "take control", "the smarter way", "your
    journey", "a brighter tomorrow", "calm and powerful", "designed for you",
    "everything in one place". **An exclamation mark in a success message.**
    A brand name in the register of Acme, NovaCore, Flowbit, Quantix.

### 15.2 Momentum-specific rejection criteria

**Each of these is a specific finding from the audit, restated as a render-level
test. A render that fails any of them is invalid regardless of how attractive it
is.**

1. **A missed day and a rest day are not distinguishable.** In any mark, at any
   size, on any screen. A day with five scheduled and none completed drawn like a
   day with nothing scheduled. **This is the audit's CP-5 and the product's own
   hard requirement, and it is a correctness failure, not a taste failure.**
2. **A streak shown as a number smaller than its own label.** A "12" smaller than
   the habit's name beside it, or smaller than the words "streak" or "in a row".
   **The shipped app sets it at 12 sp against a 20 sp name; reproducing that
   reproduces CP-1.**
3. **A "3 of 5" that is smaller than the greeting above it.** Or smaller than the
   date. Or the same size as a row's metadata line. **This is the audit's single
   most important finding, in its most checkable form.**
4. **Light mode that is just inverted Dark.** The tell: the same hues at higher
   luminance, the same translucent fill doing no work, the same black-alpha shadow
   doing nothing, a 1 px hairline doing all the separation, a white highlight on a
   near-white surface, and a surface ramp with fewer distinct values than Dark.
   **This is CP-4 and the audit's largest open question, and every direction in
   this document answers it with a mechanism the other environment cannot use.**
5. **State communicated by colour alone.** A completed item distinguishable only
   by being green; a paused item distinguishable only by being grey; a status pill
   distinguishable only by its fill; a trend distinguishable only by its hue. **An
   accessibility floor, and a violation of the design document's own acceptance
   criteria.**
6. **A touch target smaller than 48 dp,** or a fixed-height row that clips its
   content at large font scale, or a label that becomes three lines and overlaps.
7. **Fire, flame, ember, trophy, medal, star, crown, level badge, XP bar,
   achievement tier, or a confetti burst.** Anywhere, including in a reward
   moment. **The locked design document rejects fire-streak gamification and
   routine confetti; the app ships a flame anyway; the audit found the violation.**
8. **A progress ring, donut, or circular percentage badge on the Home screen.** In
   A, B and C the answer is a figure and there is no ring. In D the figure is the
   answer and the ring is gone. **A 96 dp ring running a permanent animation to
   display a number that changes a few times a day is the shipped defect, not a
   premium signal.**
9. **A weekly bar chart** used as the primary weekly view on any screen.
10. **A percentage or a figure with no window.** "78%" with nothing saying 30
    days. "12" with nothing saying days. A rate with no period.
11. **"Insufficient data," "Declining," "Improving," "Steady," "Active," or
    "Paused" appearing as a bare lowercased enum name,** or as a bare word with
    no magnitude, no window and no mark. **The shipped app renders the literal
    string "Insufficient data" as the value of a row labelled "Trend".**
12. **A stat tile with no meaning** — a value and a label and nothing else, at the
    same size as every other number on the screen, with no comparison, no mark, no
    window and no interpretation. **Four such tiles in a 2×2 grid is the shipped
    defect.**
13. **A category or activity list that is not sorted,** where the section's name
    or the screen's purpose implies a ranking.
14. **"Activities" appearing on a tab labelled Tasks,** or "Create activity"
    offered on a Tasks surface, or two identical unlabelled circular management
    buttons under any row.
15. **Any affordance implying a capability the product does not have** — account,
    sign-in, cloud, sync, share, export to a server, leaderboard, a friend, a
    team, a social feed, an AI coach, a wearable, location, health platform,
    camera, or a permission the product does not request.
16. **A dark-only first-run surface.** Four near-black screens followed by a light
    app. **Every direction's onboarding must have a Light rendering.**
17. **A completion control that promises a one-tap toggle it cannot deliver** —
    a check on a target-based activity, with no entry step. **The shipped app gets
    this right and a render must not undo it.**
18. **A translucent destructive confirmation.** Any dialog the user can see the
    list through, and any dialog with more than one dismiss verb.
19. **A paused activity rendered by dropping the whole card's opacity** — which
    drops its text contrast below the 4.5:1 floor. Paused must be expressed
    through surface and mark, never through whole-composite alpha.
20. **Two floating action buttons of different material in one product,** or a
    floating action button whose glyph size differs from the other one's.
21. **A hero that occupies a large fixed area on a day with nothing in it.** A
    large, decorated, mostly-empty top region above a small "nothing scheduled"
    state. **The shipped empty day is the audit's clearest composition failure, and
    every direction here claims to have fixed it.**
22. **A motivational line with no data in it.** A state line that could appear on
    any user's any day. **The shipped app's message slot says the same thing all
    day, four of its six branches reference no user data, and one is the product's
    own tagline used as an everyday line.**
23. **A permanent glow, halo, breathing ring, or drifting ambient field** shown at
    rest in a still. **Ambient continuous animation is the shipped motion defect
    and all four directions remove it.**
24. **Type smaller than the direction's own caption register,** or a metadata line
    at 12 sp carrying a figure the user has to act on — a habit's live target
    progress, a streak count, a week's fraction. **All four directions make those
    figures larger, and a render that makes them small has undone the direction's
    central fix.**
25. **Inconsistent inset handling — a chrome problem, not a style preference.**
    Invalid: two screens of the same direction reserving a **different top inset
    for the same header role**; a screen title whose left edge does not line up
    with the content it titles; a top region reserving **more than a stated
    fraction of the viewport** before any content appears; a floating action
    group, bottom bar or sticky band that overlaps the last row rather than
    clearing it; or a screen drawn with **no status-bar region at all**. The
    shipped application has three coexisting inset strategies, one of which
    reserves **240 dp of a possibly-640 dp viewport** before a habit's name
    appears, and a further set of screens whose titles sit further in than their
    own content. **Every direction states its inset contract in its navigation
    section, and that contract is binding across all five of its screens.**

### 15.3 How a rejection is used

A rejected image is **regenerated**, not accepted with a note. Two exceptions,
where a rejection is recorded rather than regenerated, because the cause is a
brief and not a render:

- **A direction's own declared risk manifests.** If Press's Light shadow turns
  out to read as flat, or Loom's weave reads as a texture overlay, or Instrument's
  multiplication reads as a pretension, or D's Light ladder reads as washed out —
  **that is the direction's named unknown being answered, and the answer is
  evidence, not a failure.** It is recorded against that direction's §8.20
  branch and it informs a later decision. **It is not regenerated with the
  direction's risk suppressed.**
- **A brief is internally contradictory.** If a brief cannot be satisfied as
  written, the brief is the defect and it is reported back rather than worked
  around.

---

## 16. Phase-2B Handoff

### 16.1 What the image-generation stage receives

1. **This document, in full.** Sixteen sections, self-contained. It does not
   require the repository, the audit, the product specification, the design
   system, or any decision record. **Everything a generator needs is here,
   including the content fixture, the palette descriptions, the type registers,
   the mark vocabularies, the navigation models, the anti-pattern lists, and the
   twenty briefs.**
2. **A list of twenty screen identifiers** in the order given in §13.1, plus the
   two additional renders in §13.2 (each direction's empty day and 100% day),
   and the instruction that **both environments are required for every screen**.
3. **The direction definitions** from §5–§8, read once per direction before that
   direction's five images are generated, so the design bible in §12.2 is locked
   before the first image rather than inferred from it.
4. **The rejection criteria** from §15, as a pre-delivery checklist.
5. **The consistency rules** from §14, as a per-direction pre-delivery checklist.

### 16.2 What the image-generation stage must do

- Produce **one coherent product per direction**, five screens each, plus the
  empty-day and 100%-day renders.
- Apply the **content fixture exactly** — the same day, the same five activities,
  the same three completions, the same two rest days, the same paused habit, the
  same two target-based activities, the same streak lengths, the same 78%
  consistency, the same missed count, the same 0% category — in both environments.
- Get the **numeric hierarchy right on every screen**: the day's answer largest,
  a streak larger than a name, a week's answer larger than its label, a live
  target larger than its metadata.
- Make **missed and rest visibly different**, and in A, B and C make at least one
  rest day **genuinely absent**.
- Show **both environments** for every screen, using each direction's own depth
  mechanism, which differs between environments.
- Apply the direction's **design bible** unchanged across its five screens, and
  vary only the composition, the density, the emphasis and the primary visual.
- Respect the **accessibility floors visibly**: 48 dp targets, readable text,
  no state-by-colour-alone.
- **Regenerate** anything that fails §15, and read §13.4 if two directions begin
  to look alike.
- Produce a **control** of the current application at the same content, in both
  environments, if and only if the current app can actually be run. **Without a
  control, "premium" has nothing to be measured against, and this document does
  not substitute for one.**

### 16.3 What the image-generation stage must NOT do

- **Generate a winner, or imply one.** No direction is preferred. The generated set
  is four products for a later decision, and any presentation language that ranks
  them — "our favourite", "the winning direction", "the recommended concept",
  numbered so that one is last — is out of bounds.
- **Invent a fifth direction,** or blend two, or produce "a hybrid" as a
  comparison image.
- **Converge the directions,** or generate one screen that could belong to two of
  them.
- **Change the product's scope.** No feature, screen, capability or setting that
  this document does not specify may appear. In particular: no account, no cloud,
  no sync, no share, no social, no leaderboard, no AI coach, no wearable, no
  location, no health platform, no camera, no additional permission.
- **Change the product's copy.** The banned phrase list is in §15.1. No
  motivational filler. No emoji. No exclamation marks in success copy. No enum
  name as a value.
- **Produce a dark-only first run** in any direction.
- **Suppress a direction's declared risk** to make a set look better. If a risk
  manifests, that is the evidence the stage exists to produce.
- **Claim verification.** Generated images are **hypotheses made visible**. They
  are not proof that a design works, that a colour passes a contrast floor, that a
  texture is affordable at 400 dpi, that a type scale survives 200% font scale,
  or that any of it is buildable. **Every claim in this document is a hypothesis,
  and the images are how a hypothesis gets tested — not how it gets confirmed.**
- **Write code, or describe an implementation.** The stage produces images and
  short descriptions of them.

### 16.4 What the stage returns, and what happens next

**Returns:** twenty screen images (plus the empty-day and 100%-day renders per
direction), each tagged with its direction, its screen, and its environment; a
short note per image recording which rejection criteria, if any, were hit during
regeneration; and **a separate note per direction recording whether its §8.20
named unknown was answered, and in which branch.**

**Then:** the generated set is read as **evidence about the four hypotheses**, and
specifically as evidence about the four named unknowns. **The choice between the
four directions is a separate task, with a different owner, and it is not made
here, and nothing in this document or its output pre-empts it.** A direction that
renders well and a direction that is *right* are different questions, and this
stage produces the input to the first one.

### 16.5 The one thing this document most wants to be true

Not "one of the four is beautiful". **That whichever direction is chosen, the
answer to "how am I doing today" is the largest thing on the screen, a Monday /
Wednesday / Friday user never has to feel bad about a Tuesday, and the moment
somebody finishes everything is not a notification bar.**

All four directions agree on those three. They disagree about everything else,
and that disagreement is the point of producing them.

---

## 8. IMPLEMENTATION PLAN

<!-- DEVELOPER ONLY -->

**None. No Developer is assigned to this task.** There is no application
implementation, no code change, and no build to run. AGENTS.md §14.3 allows the
Orchestrator to route directly to Architect when only ARCHITECTURE and
VERIFICATION are classified; IMPLEMENTATION is not assigned here.

---

## 9. IMPLEMENTATION NOTES

<!-- DEVELOPER ONLY -->

None.

---

## 10. REVIEW FINDINGS

<!-- REVIEWER/TESTER ONLY -->

Reviewed: `MOMENTUM_CONCEPT_EXPLORATION.md` (546,107 bytes · 7,951 lines · UTF-8 no
BOM · 0 U+FFFD) against the §18 checklist and R1–R13, with
`MOMENTUM_PREMIUM_DESIGN_AUDIT.md` as the contradiction source of truth.

**One MAJOR finding, one MINOR finding, one recorded non-defect.** Nothing else.

---

### Finding 1 — Cross-direction evaluative superlatives contradict the document's own no-winner rule

**Description.** A small set of sentences compare the four directions against each
other on quality using superlatives ("the strongest version of the answer any of the
four directions gives", "the strongest thing in this document", "the weakest of the
four", "the cleanest accent discipline of the four directions", "the most subtle of
the four name-arguments"). The document does not select, rank, score or recommend a
direction as a whole, and no sentence says one direction is the better direction. But
it does place individual design answers in an order against the other three, and in
one case it does so **inside the comparison matrix**, which is the one place the
no-winner rule is load-bearing.

This is a contradiction, not merely a stylistic risk, because the document pre-commits
in two places to having no such judgement:

- §1.4: *"'Direction C marks habits better' is a judgement, and it appears nowhere."*
- §9: *"It contains no score, no rating, no percentage, no rank, no 'best', and no
  recommendation. A row states what a direction **is**, never how well it does it."*

The instances at L3611, L4206 and L6760 are exactly the judgement §1.4 names.

**Evidence.** Line numbers in `MOMENTUM_CONCEPT_EXPLORATION.md`.

*Inside the matrix (highest-signal locus):*

| Line | Text | Direction favoured |
|---|---|---|
| 6760 | Q2 row, D cell: *"the **most subtle** of the four name-arguments"* | D |

*In direction bodies:*

| Line | Text | Direction favoured |
|---|---|---|
| 3611 | *"This is **the strongest version of the answer any of the four directions** gives"* | C |
| 4201 | *"it is **the strongest thing in this document**"* | C |
| 4206 | *"it is **the only answer among the four directions** that does not require the user to decode anything at all"* | C |
| 4734 | *"becomes **the strongest single idea in this document**"* (C's unknown branch) | C |
| 7195 | *"**C's version is the strongest expression of the requirement**"* (DayCell row, §11.1 item 3) | C |
| 2241 | *"Instrument can claim **the cleanest accent discipline of the four directions**"* | B |
| 2936 | *"This is **the most restrained of the four directions**"* | B |
| 7324 | *"which is **the most honest thing any direction does** about its own register"* | B |
| 7326 | *"which is **the most surprising and most consistent of the four**"* | D |
| 6282 | *"**D's name-argument is therefore the weakest of the four**"* | against D |
| 7240 | *"and it is **the strongest argument for D** on accessibility grounds alone"* | D |

**Instances I examined and ruled NOT defects.** These are *intra*-direction or
about the product/audit, not cross-direction, and are correct as written:
L1577 ("Press's Q1 answer is **its** strongest asset" — the referent is Press);
L5122 ("the strongest characteristics Momentum already has" — the app);
L5802 and L7328 ("the strongest … argument/finding" — the audit);
L3036 ("the most honest possible rendering" — an absolute within one record);
L1986 ("the coldest of the four directions" — cross-direction in form, but
self-deprecating and immediately explained as intentional; noted, not actionable);
and the ~20 "X is the only direction that …" sentences where the property is a
*design choice* (only C reduces iconography, only D keeps the artwork) — a statement
of scope, not of merit.

**Severity: MAJOR.** It violates acceptance criterion 9 and contradicts two explicit
promises the document makes about itself. It is not a BLOCKER: no direction is
selected, nothing is scored, nothing is ranked overall, and the exploration remains
usable as four products. The fix is mechanical and does not require redesigning or
re-authoring any direction.

**Required action (Architect, since §7 is its section).** Reword so each claim is
*intra*-direction or non-comparative. The design content need not change — only the
comparative framing. Suggested shape, offered as an example and not as a rewrite:
L3611 "this is the strongest answer *Loom gives*"; L4201 "this is the strongest idea
*in Loom*"; L4206 "the only one of Loom's answers that requires no decoding";
L4734 "the strongest single idea in Loom"; L7195 "C's version expresses the
requirement most directly"; L2241 "Instrument's accent discipline is a single signal
colour in four reading roles"; L2936 "Instrument is deliberately restrained";
L7324 "B states this tension openly"; L7326 "D keeps the reasoning intact and deletes
the component"; L6282 "D's name-argument is subtle and may not survive being
described" (the following sentence already makes exactly this point without ranking);
L7240 "this is D's accessibility argument"; and for L6760 drop the comparative entirely
("subtle — the field's behaviour, carried by light rather than by a figure").

---

### Finding 2 — Audit CP-10 (three coexisting inset strategies) is not challenged structurally

**Description.** R6 and acceptance criterion 17 list *inconsistent inset handling*
among the twelve problem classes the exploration must respond to structurally. It
responds to eleven. The word "inset" appears four times in 7,951 lines, and only
Direction D receives a named answer ("one inset contract"). A, B and C are covered
only by the generic safe-area fence, which is a rule about drawing a status bar, not
a challenge to the defect.

**Evidence.**

- Audit CP-10 (`MOMENTUM_PREMIUM_DESIGN_AUDIT.md` L2121, and the summary at L100):
  *"The top-bar inset is unenforced on 5 of the 10 screens that carry one, and three
  different inset strategies coexist, one of which reserves 240 dp of a
  possibly-640 dp viewport on Habit Detail."*
- `MOMENTUM_CONCEPT_EXPLORATION.md` contains **no** reference to CP-10, to "three
  inset strategies", or to the 240 dp reservation. All 4 `inset` hits:
  - L405 (§4.2) — a safe-area fence: *"a status-bar region, a gesture-navigation
    region, and a bottom-inset region are visible in every composition."*
  - L2350 (B) — a radius note, unrelated.
  - L5171 (D §8.2 item 6) — *"One press language, one dialog, one card, one FAB, one
    inset contract."* D only.
  - L7502 (§12.3) — a generator checklist item.
- The document is otherwise thorough on the other eleven: for example the snackbar
  problem becomes a per-direction reward moment (D §8.2 item 5, "the day's completion
  stops being a snackbar"); dialog translucency becomes rejection criterion §15.2 #18;
  whole-composite alpha becomes §15.2 #19; missed-versus-rest becomes §15.2 #1 and a
  four-way mark vocabulary in every direction.

**Severity: MINOR.** It is one of twelve classes, it is a coverage gap rather than an
error, and it does not affect any brief's usability. It is not a BLOCKER and does not
prevent acceptance of the other seventeen criteria.

**Required action (Architect).** Add the inset contract to §4.2 or §15.2 as a
render-level test (e.g. *a screen whose top region reserves a different inset for the
same header role; a top region that reserves more than a stated fraction of the
viewport; a screen with no status-bar region at all*), and state per direction what
its inset contract is — which for A, B and C is currently unstated. No new screen or
brief is required; one rejection criterion plus one line per direction is sufficient.

---

### Recorded non-defect (no action required; Orchestrator's call only)

**§11's cumulative pro-D rhetoric.** §11.1's per-strength tables pair "D is the only
direction that …" with the desirable outcome at roughly ten rows (L7185, L7196,
L7208, L7228, L7240, L7286, L7294, L7305, L7314, L7395), while the A/B/C rows at the
same strengths are phrased as losses or "not applicable". Each instance is individually
defensible as a statement of scope, and §2.3 (L438-439) shows the document
*deliberately* uses "closer to the locked design" as a legitimate differentiator for
reducing glass. The cumulative effect, however, is a pro-D signal a careful reader
will notice.

**This is not a defect and I am not recommending a change.** It is recorded only so
that the Orchestrator can decide whether to ask the Architect for one neutral sentence
in §9 or §11 stating that "only D" statements describe scope, not merit. Doing nothing
is defensible. Note that Finding 1's L7240 correction, if applied, slightly reduces
this effect on its own.

---

### M1–M5 rulings (as requested)

| | Ruling | Basis |
|---|---|---|
| **M1** cross-direction superlatives | **PARTIALLY CONFIRMED — real, actionable** | 11 instances listed in Finding 1. 7 further instances examined and ruled intra-direction / non-defects. The distinguishing test applied: does the superlative compare against *the other three directions*, or against another element of the same direction's own system? Only the former is a violation. |
| **M2** §9 matrix purity | **PARTIALLY CONFIRMED** | No score column, no numeric rating, no percentage, no rank column, no preference column across all 30 rows; the opening (L6734-6740) and closing (L6778-6783) paragraphs state the rule and hold it. One cell (L6760) carries a cross-direction rating adjective. "Does the Dark environment need more colour than the Light one?" (L6750) is a capability property, not a quality claim — acceptable. |
| **M3** Direction D boundary | **CONFIRMED CLEAN** | D is labelled controlled evolution in the §8 heading, in §8.1 (*"A controlled evolution of the strongest characteristics Momentum already has"*) and in §8.2. It is explicitly framed as **the control** whose purpose is to make A/B/C legible (L5129-5132), and §8.2 states *"D is radical in exactly one place… Everything else is consolidation, and consolidation looks like tidying. Tidying is not a design direction."* It preserves the artwork pair, the floating bar, the three-state mark, the completion control, the bundled typeface and the `HabitsHero` template — it does not re-invent. It does not become the recommended answer by conservatism; the document argues *against* reading it as a direction. The residual D-favouring pressure is rhetorical only (Finding 1, L6282 / L7240) and is bounded. |
| **M4** placeholder / truncation scan | **CONFIRMED CLEAN** | Zero hits for TODO, TBD, FIXME, XXX, Lorem, `[ ]`, `<insert`, `N/A`, `???`, `placeholder`, or a line ending in `...`. The 4 U+2026 characters are all range markers (`CP-1…CP-10`, `HO-1…HO-12`, `Q1…Q14`) or one table cell (`is a…`). No empty section: all 16 top-level sections and every `###` subsection carry body text. Smallest brief block 40 lines; smallest screen-concept block ~25 lines. All 20 briefs terminate in a complete `Anti-patterns to avoid` list, including the last (D5, ends L6729 before the §9 boundary at L6732). The repeated `8.1`–`8.22` numbering is the intended parallel structure, not duplication; no duplicated content blocks were found. |
| **M5** self-containment | **CONFIRMED CLEAN** | Zero hits for `task.md`, `TASK-00`, `§7`, "the Architect", "the Orchestrator", "write grant", "this task", or the handoff protocol. The only "Handoff" is §16's own title. §10 restates each question's evidence inline rather than referring the reader to the audit. The audit is cited in §2.1 as the evidence base — a legitimate source citation, not a dependency — and every claim the document relies on is restated. |

---

### Transcription fidelity (checked, not assumed)

The emitted file is a **mechanical** extraction of `task.md` §7 with zero divergence:
lines 1–7,951 of `MOMENTUM_CONCEPT_EXPLORATION.md` are identical (`-cne`, case-sensitive
string comparison) to `task.md` lines 305–8,255. **0 mismatches across 7,951 compared
lines.** Nothing was rewritten, reordered, dropped or added by the emission step. The
`ISSUE-023` precedent holds.

---

## 10A. RE-VERIFICATION — CORRECTION CYCLE 1

<!-- REVIEWER/TESTER ONLY -->

Bounded second pass (AGENTS.md §14.5), scoped to Finding 1 and Finding 2 and to
regression only. My original findings above stand unaltered. Artifact re-verified:
`MOMENTUM_CONCEPT_EXPLORATION.md`, now **549,612 bytes · 8,003 lines** (was 546,107 B /
7,951 lines). No new lines of inquiry opened; no re-design, re-wording or fix performed.

**F2 — CLOSED. F1 — SUBSTANTIALLY CLOSED, WITH A RESIDUAL. Overall: NOT_VERIFIED;
TASK-008 is not yet acceptable.**

### F1 (MAJOR) — PARTIALLY CLOSED · all 11 of my instances fixed, 4 residual instances of the same class remain

**F1.1 — All 11 originally flagged instances: FIXED.** Located in the re-emitted file
by content, not by the stale line numbers. In every case the fix re-scopes the claim
to the direction's own system and **preserves the design reasoning**; none is a
deletion that guts the argument. My test applied unchanged: does the superlative
compare against the other three directions, or against another element of the same
direction's own system?

| Was | Now | Design position preserved? |
|---|---|---|
| "the strongest version of the answer any of the four directions gives" | L3628 "the strongest version of the answer **Loom** gives" | Yes — the whole *native-to-cloth* argument follows intact (L3628-3633) |
| "the strongest thing in this document" | L4225 "the strongest thing **in Loom**" | Yes — the *removal of the symbol* argument follows intact |
| "the only answer among the four directions that does not require the user to decode anything at all" | L4229-4231 "the only one of **Loom's rest-day answers** that requires the user to decode nothing at all" | Yes |
| "the strongest single idea in this document" | L4758 "the strongest single idea **in Loom**" | Yes — conditional branch and the per-activity-dye rationale intact |
| "C's version is the strongest expression of the requirement" | L7235 "C's version expresses the requirement **most directly**, and it requires nothing to be decoded" | Yes — the *a pattern need not call for a pick* reasoning intact |
| "Instrument can claim the cleanest accent discipline of the four directions" | L2249-2250 "Instrument's accent discipline is a **single signal colour in four reading roles**" | Yes — "removes an entire colour vocabulary rather than adding a sixth" kept as the stated reason |
| "This is the most restrained of the four directions" | L2953 "Instrument's day-complete moment is **deliberately restrained**" | Yes — and the self-critical balance ("but Instrument must also own the other side of that requirement", L2955) is retained, which is the more important half of that passage |
| "the most honest thing any direction does about its own register" | L7364 "B argues with that adjective list **on purpose and says so here**, rather than quietly leaving the tension unstated" | Yes — the declared tension itself is retained verbatim |
| "the most surprising and most consistent of the four" | L7366 "The reasoning survives intact **as a general rule**, and the component is deleted" | Yes |
| "D's name-argument is therefore the weakest of the four" | L6320-6324 "D's name-argument is therefore **carried by background behaviour rather than by a figure**, and D states the consequence plainly rather than claiming the light-field answers the 'what is Momentum' question as well as a ledger, a multiplication or a woven run does" | Yes — the self-criticism is retained and in fact reads more clearly than before; only the ranking is gone |
| "it is the strongest argument for D on accessibility grounds alone" | L7280 "…**and that is D's accessibility argument in full**" | Yes — the fourteen-call-site fix and the named source-text test are untouched |
| §9 matrix Q2 row, D cell: "the most subtle of the four name-arguments" | L6799 "carried by background rather than by a figure, and **subtle to describe**" | Yes — the property survives; the rating does not |

**F1.2 — The 2 additional instances.** The Architect reports re-wording 13 instances.
I confirmed 12 of my own 11 plus the matrix cell are fixed. I **cannot identify which
2 additional instances it found**, because the pre-correction artifact was overwritten
and no diff exists (the file is untracked, and `task.md` §7 was corrected in place).
What I can say is bounded and precise: the two are *not* among the residuals below, so
they were genuine instances of the flagged construction and are now fixed. I take the
claim as **consistent with the evidence but not independently verifiable**, and I do
not credit or dispute it beyond that.

**F1.3 — No new cross-direction superlative was introduced: PARTIALLY CONFIRMED.**
I cannot prove a negative without a diff. Two facts support the negative: (a) the
re-worded sentences I inspected introduce no new cross-direction comparison — each
replacement is strictly narrower than what it replaced; (b) the residuals below sit
in a passage (C §8.17/§8.12 day-complete, plus one line in §13) that my original
evidence table did not list, which is consistent with them being **pre-existing
residuals my first-pass sweep missed rather than new introductions**. My first-pass
sweeps used the patterns `strongest|best|weakest|rank|prefer|winner` and
`the only direction|any of the four|of the four directions|most honest|most
surprising`; neither pattern matches "the most **physical** of the four" or "more
literally than any other direction". That is a gap in my first-pass sweep, and I
record it as such rather than attributing the instances to the correction.

### Residual of F1 — 4 clear instances + 1 borderline, all in Direction C and one line in §13

Applying my own test unchanged, these compare against the other three directions and
therefore violate. They are the same defect class as F1 but far smaller in scope than
the original: they concern one theme (Loom's day-complete "physicality") and one line
of the generation-order table. None of them pre-empts a choice between directions.

| Line | Text | Direction favoured |
|---|---|---|
| 4173 | "**Loom's version, and it is the most physical of the four:**" | C |
| 4580 | "This is Loom's *most* physical celebration **of the four**" | C |
| 4584 | "honours the locked design document's *'calm and rewarding rather than loud'* **more literally than any other direction**" | C |
| 7100 | (§10 Q12, C's entry) "The **most physical of the four**, and still silent." | C |
| 7576 | (§13 row 5) "The mark vocabulary is **the hardest and most important thing any direction is doing**" | borderline — a claim about the exercise, not about a direction's merit |

One adjacent instance I checked and **accept**: L4025, "…*more* typographically
dependent than any other direction's" — a **self-critical** cross-direction
comparison inside C's own Risks section, structurally identical to L1995 ("the coldest
of the four directions", B), which I accepted in the first pass as a self-deprecating
non-defect. I apply the same distinction here for consistency: self-critical ≠
self-promotional. That distinction is principled, not arbitrary, and it is why
L4173/4580/4584/7100 (self-promotional) are flagged while L4025 and L1995 (self-critical)
are not.

**Residual severity: MINOR.** Same mechanical nature as the original F1, one-line
re-wording, no design content affected. It does not reopen any criterion other than 9,
which therefore remains PARTIALLY CONFIRMED.

**Required action (Architect).** Re-word L4173, L4580, L4584, L7100 so the
physicality claim is scoped to Loom (e.g. "Loom's version, and it is the most physical
of the four" → "Loom's version, and the most physical of anything in this direction"),
and soften or scope L7576. Nothing else in F1 requires any further work.

### F2 (MINOR) — CLOSED

Both required additions are present and adequate. Verified in the re-emitted file.

**F2.1 — Rejection criterion, §15.2 item 25: PRESENT AND ADEQUATE.** L7875-7886,
**"25. Inconsistent inset handling — a chrome problem, not a style preference."** It
names five concrete invalidity conditions (different top inset for the same header
role on two screens of one direction; a screen title not lining up with the content it
titles; a top region reserving more than a stated fraction of the viewport before
content appears; floating chrome overlapping rather than clearing the last row; a
screen with no status-bar region at all), and it cites the defect precisely —
*"The shipped application has three coexisting inset strategies, one of which reserves
**240 dp of a possibly-640 dp viewport** before a habit's name appears"* — which is
audit CP-10, quoted with its own evidence rather than paraphrased away. It closes with
a binding clause: *"Every direction states its inset contract in its navigation
section, and that contract is binding across all five of its screens."* It is coherent
in voice and register with items 1–24 and reuses their established idioms (the
"Invalid:" pattern, the "The shipped application…" evidence clause, the closing
requirement sentence).

**Numbering integrity: PASS.** §15.2 now runs 1…25 with no gap and no renumbering —
verified programmatically: items `1,2,3,…,24,25`, count 25, strictly sequential. The
existing items kept their numbers, so the 20 screen-concept and 20 brief cross-references
are unaffected.

**F2.2 — One inset contract per direction in §8.11 Navigation: ALL FOUR PRESENT, ALL
FOUR REAL.** Each sits inside `### 8.11 Navigation` (verified by walking each line
number back to its enclosing heading), and each is expressed in that direction's own
material vocabulary rather than restating the generic §4.2 safe-area fence:

- **A — Press, L1029-1036** — *"in the page's own vocabulary: every screen's title
  sits on the same hard left edge at the same margin, the header band is a stated
  fixed height on every screen that carries one, the top and bottom bands are flush
  regions of the page rather than overlays, and the bottom band is reserved by the
  grid so that no content can sit under it."* Closes: *"which is the direct answer to
  the three coexisting inset strategies the shipped app carries."*
- **B — Instrument, L2491-2496** — *"in the plate's own vocabulary: every screen is laid
  out on the same fixed measurement grid with one gutter, and the top and bottom
  regions are reserved by that grid as slots of stated height rather than per screen."*
- **C — Loom, L4077-4082** — *"in the cloth's own vocabulary: the selvedge bands are the
  same fixed height on every screen, the warp grid is the only horizontal reference,
  and the top and bottom clearances are stated bands in the weave rather than per-screen
  padding."*
- **D — Aurora, Governed, L5717-5725** — *"A single shell contract owns three things:
  the top-app-bar inset, the content inset, and the floating-chrome clearance — and it
  is applied at the Scaffold, not per screen."* It also resolves the specific 240 dp
  Habit Detail case: *"a habit's detail page can no longer reserve a fixed block of a
  possibly-small display before the habit's name appears."*

Each is a **real contract** (a stated invariant plus the consequence it produces), not a
restatement of §4.2. None is filler. **F2 is fully closed.**

### Over-correction risk (F1.4) — DID NOT MATERIALISE

Explicit ruling as requested. I verified by content that all 7 previously-accepted
intra-direction / product-referent superlatives are **still present and unaltered**:

| Accepted instance | Status in the re-emitted file |
|---|---|
| "Press's Q1 answer is **its** strongest asset" (intra-direction) | Present, L1586, unchanged |
| "a controlled evolution of **the strongest characteristics Momentum already has**" (the app) | Present, L5146, unchanged |
| "**the single best piece of motion in the shipped app**, and Loom trades it for…" (the app) | Present, L4562, unchanged |
| "**the strongest argument the audit makes** about this component" (the audit) | Present, L7315, unchanged |
| "It is **the coldest of the four directions**, and the coldness is the point" (B, accepted) | Present, L1995, unchanged |
| "the product's **own best accessibility engineering** applied to the product's **own best data mark**" (the product) | Present, L5856-5857, unchanged |
| "**The strongest thing the audit found** is not a component, a colour or a layout" (§11 closing, the audit) | Present, L7368, unchanged |

**No legitimate design reasoning was stripped.** The re-worded passages are, if
anything, *longer* than the originals in the case of L6320-6324, where the D
name-argument self-criticism was rewritten out in full rather than truncated. Seven for
seven retained; **over-correction did not occur.** This was the failure mode I was
specifically watching for and it is absent.

### Regression check — PASS

| Check | Result |
|---|---|
| 16 top-level sections, required order | **PASS** — L9 / 90 / 265 / 365 / 542 / 1964 / 3503 / 5142 / 6771 / 6835 / 7193 / 7378 / 7560 / 7622 / 7730 / 7907. Identical titles and order to the first pass. |
| Exactly 4 directions | **PASS** — `## 5`–`## 8` only |
| 88 `### 8.x` (4 × 22) | **PASS** — 88 |
| 20 `#### Screen` | **PASS** — 20 |
| 20 `#### Brief` | **PASS** — 20 |
| 14 `### Qn` in §10, with Q1, Q2, Q3, Q6 present | **PASS** — Q1…Q14 sequential and complete (L6842, 6862, 6881, 6896, 6922, 6953, 6977, 7004, 7021, 7039, 7060, 7085, 7110, 7136) |
| Q1–Q14 still answered per direction | **PASS** — all 14 Q-sections intact; the four `#### Q1 — the explicit answer` per-direction subsections intact (one per direction, verified in the section walk) |
| Directions' design positions unchanged in substance | **PASS** on the evidence I am required to gather: no metaphor, colour, type size, mark, screen, brief or value was altered. The 52-line growth (+52 lines) is fully accounted for by the 4 inset contracts (L1029, L2491, L4077, L5717) and §15.2 item 25 (L7875-7886), which is exactly what F2 asked for and nothing else. |
| Direction D still explicitly controlled evolution | **PASS** — heading L5142 still `## 8. Direction D — **Aurora, Governed** *(controlled evolution)*`; L5146 still *"A controlled evolution of the strongest characteristics Momentum already has"* |
| §2.1 "eleven radical opportunities" vs `HO-1…HO-12` tension still present exactly as-is | **PASS** — L96 still contains "§19 the eleven radical opportunities" alongside "§22 HO-1…HO-12". Correctly preserved per R1; not "fixed". |
| Recorded non-defect (§11 cumulative pro-D scope rhetoric) left alone | **PASS** — 12 "D is the only direction…" instances remain. Not stripped, not expanded. |
| No placeholder text / truncation / empty section | **PASS** — 0 hits for TODO, TBD, FIXME, XXX, Lorem, placeholder, `<insert`, "Reserved for the", or either section-owner marker. 4 U+2026, all range markers, unchanged. |
| No encoding damage | **PASS** — 549,612 B; 8,003 lines; first bytes `2E 20 2A`; **no BOM**; 0 U+FFFD; 8,003 CRLF, 0 bare LF |
| No `task.md` scaffolding leaked into the deliverable | **PASS** — 0 hits for `<!-- REVIEWER/TESTER ONLY -->`, `<!-- ARCHITECT ONLY -->`, `<!-- ORCHESTRATOR ONLY -->`, "Reserved for the", or any section-owner marker |
| §9 matrix purity | **PASS with a note** — the matrix carries **33** property rows (L6783-6815), not 34 as the brief stated; the discrepancy is in the brief's count, not in the artifact. No score column, no numeric rating, no percentage, no rank, no preference column. A full-block scan of all 33 rows for superlative/rating wording returns exactly **one** hit, L6805 *"the **field moving to its strongest state**"* — which is the *name of a light-field state* in D's cell, a property, and which I accepted as such in the first pass. The former offending cell (Q2/D) is fixed. **Matrix purity holds.** |
| `git status --short` — same Phase 1 baseline, deliverable the only new file | **PASS** — byte-identical to the first-pass listing: 9 modified under `presentation/**`, 2 modified tests, `AGENTS.md`, `docs/architecture/decisions/DECISIONS.md`; `MOMENTUM_CONCEPT_EXPLORATION.md` the only new task file. No application file added or removed. |
| No images generated | **PASS** — 0 image files in `git status`; no new image anywhere |
| No ADR written | **PASS** — `DECISIONS.md` diff still +430 / **−0**; the only added ADR headers remain ADR-34…ADR-39 (Hero artwork, window dim, knowledge-as-source-of-truth); **0** matches for `Phase 2A`, `Step 2` or `Concept Exploration`. The 18 matches for "inset" in that diff are pre-existing `HeroTopInset` / status-bar-inset prose inside ADR-34/ADR-37, not a new inset ADR. |
| No application file changed | **PASS** — same as the two rows above |

**No regression was introduced by correction cycle 1.** The +52 lines are fully and
exclusively attributable to the F2 additions; nothing else in the document's structure,
coverage or content moved.

---

## 10B. FINAL ACCEPTANCE PASS — CORRECTION CYCLE 2

<!-- REVIEWER/TESTER ONLY -->

Bounded third and final pass (AGENTS.md §14.5), scoped exactly to the cycle-2 residual,
the 3 additional instances, the left-candidate list, and regression. My §10 and §10A
records stand unaltered. Artifact: **549,926 bytes · 8,008 lines** (was 549,612 B /
8,003 lines). **TASK-008 is ACCEPTED. Final result: VERIFIED.**

### Item 1 — The 5 residual re-wordings: ALL 5 CORRECTLY RE-SCOPED, REASONING PRESERVED

Located by content. My test applied unchanged: does the superlative compare against
the **other three directions**, or against **another element of the same direction's own
system**? Only the former violates. All five now compare against a Loom-internal
referent, and in every case the argument that followed the superlative is intact — none
was gutted.

| New line | Now reads | Verdict |
|---|---|---|
| 4174 | "**Loom's version, and it is the most physical gesture in this direction:**" | FIXED. Comparison set is now Loom's own gestures. The full physicality case follows verbatim: the band tightening, the gap closing, density gained, one stitch, the haptic, no confetti/particles/light/sound/toast, "the moment is the cloth becoming cloth", and the milestone-vs-completion argument (L4175-4183). Nothing lost. |
| 4582-4584 | "This is Loom's *most* physical celebration **in this direction** - its gesture is drawn in the same material as the rest of the direction, and there is no expressive gesture anywhere in Loom to be physical instead of this one." | FIXED, and improved: a new intra-direction justification for the claim now replaces the bare cross-direction one, and the sentence is self-contained without it. The rest of the passage — milestone-earned, the locked "no routine confetti" + "streak celebration" pairing, the calm-by-construction argument — is unchanged. |
| 4589 | "honours the locked design document's *'calm and rewarding rather than loud'* **as literally as Loom's own material permits**, because a band tightening is quiet by construction" | FIXED. Scoped to Loom's material; the causal clause ("quiet by construction") retained, so the reasoning is stronger than before, not weaker. |
| 7105 | "The most physical of **Loom's own moments**, and still silent." | FIXED. Comparison set is Loom's own moments. "and still silent" — the property the row exists to record — is retained. |
| 7581 | "The mark vocabulary is **the hardest and most important thing in this exercise, and it has to be read in every direction**" | FIXED. The claim is now about the exercise rather than a ranking of the four. The operative purpose clause ("it has to be read in every direction") and the A-specific consequence ("no rule at all for a rest day… If A's mark row reads, A's metaphor holds") are retained. |

**One borderline noted and accepted, not flagged:** the same cell continues "and A's
version is the most *subtractive*". Read strictly, that compares A's version against
other directions' versions. It is a **difficulty descriptor, not a merit ranking** — it
says A's mark row is the one that removes the most, hence the one whose failure would
be most diagnostic — and the sentence's purpose is to justify render order. At the
final pass I treat it as a stylistic preference, not a defect, and record it here for
completeness rather than as a finding. It does not pre-empt a Phase 2B/3 evaluation and
it does not name a winner.

### Item 2 — The 3 additional instances: ALL 3 GENUINE, ALL 3 NOW CORRECTLY RE-SCOPED

My two first-pass regex sets genuinely did not match any of these three; that is a
documented gap in my sweep, and I confirm the Architect's account of it.

| New line | Was → Now | Verdict |
|---|---|---|
| 2102-2104 (B §8.6) | "more aggressively than any other direction" → "**Manrope is retained, and Instrument leans on its variable axis harder than anything else in its own typographic system** - because an instrument is a monospace problem wearing a sans." | **Was genuine** (a cross-direction comparative about relative typographic aggression). **Now correct** — the comparison is Instrument's own axis-usage against its own system, and the rhetorical justification ("an instrument is a monospace problem wearing a sans") is retained and is the better part of the sentence. |
| 3574-3575 (C §8.4) | "the direction that answers the fire contradiction most physically" → "**This is how Loom answers the fire contradiction - physically, and from inside its own material.**" | **Was genuine** (it ranked the four on physicality). **Now correct** — the ranking is replaced by a statement of how Loom answers it. The full flame/extinguish/cut argument follows untouched (L3576-3580), so the design position is entirely preserved. |
| 7402-7403 (§12.1) | "the hardest and most important thing any of these directions is doing" → "**the hardest and most important thing in this exercise, and the thing every direction has to get right**" | **Was genuine**, and a duplicate of the §13 cell I flagged. **Now correct**, and now consistent with the §13 wording rather than being a second, divergent version of it. |

All three were real instances of the defect class, and the fixes are re-scopings, not
deletions. Credit where due: the Architect's own sweep found instances my sweep missed.

### Item 3 — The left-candidates: NO UNDER-CORRECTION, NO OVER-CORRECTION

I did **not** accept the Architect's list. I ran my own independent whole-file sweep for
the construction class — `of the four` · `than any other` · `any direction` · `any of
these directions` · `most physical` · `hardest and most important` · `than anything` ·
`most subtle` · `strongest` · `cleanest` · `weakest` · `most honest` · `most surprising` ·
`of the four directions` · `among the four` — and ruled on every remaining hit. Result:
**no genuine ranking claim remains, and nothing acceptable was stripped.**

Confirmed correctly LEFT (each is a referent the test does not reach):

- **L1995** "the coldest of the four directions, and the coldness is the point" (B) and
  **L4026** "*more* typographically dependent than any other direction's" (C, inside its
  own Risks) — both **self-critical**, the same distinction I drew in cycle 1. Retained
  correctly. My self-critical / self-promotional line is applied consistently across
  all three cycles.
- The 7 intra-direction / product-referent superlatives, all present and unaltered:
  L1586 ("its strongest asset", Press's own), L5151 ("the strongest characteristics
  Momentum already has", the app), L4570 ("the single best piece of motion in the
  shipped app"), L7320 ("the strongest argument the audit makes"), L5851/5852 ("the
  product's own best accessibility engineering… own best data mark"), L7373 ("The
  strongest thing the audit found"), L7651 ("the audit's single strongest…").
- The ~20 **"X is the only direction that…"** uniqueness/scope claims (L954, 2032,
  2412, 2599, 2847, 3124, 3531, 3744, 3789, 4473, 5667, 6804's "the only direction that
  takes the name as a quantity", 7261) — statements of *what each direction chose*, not
  of how well. Unchanged class since pass 1.
- C's Q5/Q8/Q16 and B's Q8 uniqueness claims; **D §8.10** "can claim to be *closer* to
  the locked design" (L5672) — a proximity-to-a-locked-ADR claim, which §2.3 (L438)
  establishes as a legitimate, differentiator-worthy property of *reducing* glass.
- Statements that **deny** ranking or set failure tests: L79, L6826 ("It does **not**
  establish that any of them is better than any other"), L6884, L6978, L7059, L6105,
  L8002, and every §9/§12/§15/§16 no-winner clause.
- Factual counts, not rankings: L772, L7130 ("three of the four directions leave that
  capability with one entry"), and the every-occurrence "worst-first" / "ranked" /
  "best performers" / "most consistent" set — my criterion-10 ruling, unchanged.
- Intra-direction absolutes: L3053 ("the most honest possible rendering"), L4679 ("the
  strongest available means", i.e. of Loom's own options), L2603 ("the weakest possible
  way", about satisfying a project requirement, not a direction).
- Product/audit referents: L4027, L4726, L7295 ("the axis the audit says is already
  weakest" — the product's typography, not a direction).

### Item 4 — Regression check: PASS

| Check | Result |
|---|---|
| 16 top-level sections, required order | **PASS** — L9 / 90 / 265 / 365 / 542 / 1964 / 3503 / 5147 / 6776 / 6840 / 7198 / 7383 / 7565 / 7627 / 7735 / 7912 |
| Exactly 4 directions | **PASS** — `## 5`–`## 8` only |
| 88 `### 8.x` (4 × 22) | **PASS** — 88 |
| 20 `#### Screen` / 20 `#### Brief` | **PASS** — 20 / 20 |
| 14 `### Qn`, Q1/Q2/Q3/Q6 present | **PASS** — 14, Q1…Q14 sequential and complete |
| Q1–Q14 still answered per direction | **PASS** — all 14 Q-sections intact; the four per-direction `#### Q1 — the explicit answer` subsections intact |
| Design positions unchanged in substance | **PASS** — the +5-line delta (8,003 → 8,008) is fully accounted for by the 8 re-worded sentences reflowing across line breaks. No new content block, no insertion, no deletion. Verified verbatim at every re-worded site: no metaphor, colour, type size, mark, screen, brief or value altered. The largest rewrites (L4582-4584, L6325-6329) are *longer* than their originals, not truncated. |
| **F2 untouched** | **PASS** — §15.2 still runs 1…25, strictly sequential, items 1–24 keeping their numbers; item 25 unchanged. All four inset contracts still in place, each verified by walking back to its enclosing heading: L1029, L2491, L4078, L5722 — **all four inside `### 8.11 Navigation`**. Not re-edited. |
| The 12 cycle-1 sentences not re-edited | **PASS** — all 12 verified present and verbatim: L3629, L4232, L4227, L4763, L2249, L2953, L6325-6329, L6804, L7240, L7369, L7371, L7285. None re-edited, none reverted. |
| D still explicitly controlled evolution | **PASS** — L5147 heading `## 8. Direction D - **Aurora, Governed** *(controlled evolution)*`; L5151 "A controlled evolution of the strongest characteristics Momentum already has" |
| §2.1 "eleven radical opportunities" vs `HO-1…HO-12` tension preserved as-is | **PASS** — still at L96. Correctly not "fixed" (R1). |
| §11 pro-D scope rhetoric present, untouched | **PASS** — 12 "D is the only direction…" instances, unchanged |
| §9 matrix clean across its property rows | **PASS** — 33 property rows; full-block scan returns 2 hits: L6810 "the field moving to its **strongest state**" (a light-field *state name*, a property) and L6826 "does **not** establish that any of them is better than any other" (an explicit *denial* of ranking). No score, rank, percentage, rating or preference. The former offending cell (Q2/D, L6804) remains fixed. |
| No placeholder, truncation, empty section, encoding damage, scaffolding leak | **PASS** — 0 hits for 10 placeholder/scaffolding patterns incl. all three section-owner markers; 549,926 B; 8,008 lines; first bytes `2E 20 2A`; **no BOM**; 0 U+FFFD; 8,008 CRLF, 0 bare LF; 4 U+2026, all range markers |
| No images / no new ADR / no application file changed | **PASS** — `git status --short` byte-identical to passes 1 and 2: 9 modified under `presentation/**` + 2 modified tests + `AGENTS.md` + `DECISIONS.md`, deliverable the only new task file; 0 image files; `DECISIONS.md` diff still +430 / **−0** with 6 added ADR headers (34–39, unchanged) and 0 matches for `Phase 2A`/`Step 2`/`Concept Exploration` |

**No regression. All three cycles are additive-safe: every correction was a re-wording or
a narrow, requested insertion, and the document's structure, coverage, design positions
and product-truth fences are unchanged throughout.**

---

## 11. VERIFICATION

<!-- REVIEWER/TESTER ONLY -->

### Acceptance Criteria — per-item verdicts

| # | Criterion | Verdict | Evidence |
|---|---|---|---|
| 1 | Exists, readable, well-formed Markdown, no encoding damage | **CONFIRMED** | 546,107 bytes; 7,951 lines; first bytes `2E 20 2A` = `## `, **no BOM**; 0 U+FFFD; all 7,951 line endings CRLF, 0 bare LF. Markdown structure verified mechanically: heading hierarchy is `##` × 16 → `###` → `####` with no skipped levels; **56 tables, 0 with inconsistent column counts** (escape-aware pipe count); 0 code fences, so nothing to unbalance. No strict Markdown linter exists on this machine — "well-formed" here means structural integrity of headings, tables and lists, not a full CommonMark parse. |
| 2 | All 16 required sections, required order | **CONFIRMED** | Full heading dump: `## 1` Purpose & Phase Boundary (L9) · `## 2` Source Material (90) · `## 3` Product Truths (265) · `## 4` Concept Exploration Principles (365) · `## 5` Direction A (542) · `## 6` Direction B (1955) · `## 7` Direction C (3486) · `## 8` Direction D — Controlled Evolution (5118) · `## 9` Cross-Direction Comparison Matrix (6732) · `## 10` Audit Question Mapping (6796) · `## 11` Existing Strengths Mapping (7153) · `## 12` Image-Generation Strategy (7338) · `## 13` Generation Order (7520) · `## 14` Cross-Screen Consistency Rules (7582) · `## 15` Rejection Criteria (7690) · `## 16` Phase-2B Handoff (7855). Exact match to R11, in order, no extras, none missing. |
| 3 | Exactly four directions | **CONFIRMED** | `## 5`–`## 8` only. No fifth direction, no alternative, no appendix variant. |
| 4 | A/B/C genuinely distinct on substance | **CONFIRMED** | §4.1's nine-axis table gives a *different* answer per direction on all nine, including all four of Q1/Q2/Q3/Q6. Distinct metaphors: the printed ledger / inertia as mass × velocity / the woven run. Distinct surface philosophies (matte paper stock + cast shadow · machined plate + milled bevel + backlit panel · matte textile + thread occlusion + indigo twill · tonal ladder + hue-carrying shadow). Distinct composition (editorial column with a wider right margin · fixed snap-to-slot grid · woven warp/weft grid · one continuous surface). Distinct navigation (flat rule-bar · machined segmented rail · selvedge edge-binding · corrected floating bar). Distinct motion personalities (settle/wipe/dry · step/needle/resolve · one-stitch-pull · change-triggered bloom). The closest pair is A/B — both artwork-free, both zero-translucency, both a hard-left column — and they still differ on metaphor, material, depth mechanism, composition logic, card function, Q1 mechanism, Q2, Q3, motion and navigation. A collapse into *"white background + rounded cards + gradient accent + bottom navigation + large hero"* did not occur: none of the four uses a gradient accent, none uses oversized rounded cards, and three of the four replace bottom navigation with a different model. §9's 30 independent property rows corroborate. |
| 5 | D is explicitly a controlled evolution and genuinely evolves | **CONFIRMED** | Labelled in three places: the `## 8` heading, §8.1 L5122, §8.2. It evolves rather than reinvents: keeps the artwork pair and its caller contract, the floating bar, the three-state mark's exact marks, the completion control unchanged, the bundled typeface, and the `HabitsHero` reference composition. Its changes are consolidation plus one declared radical move (the numeric register), and §8.2 says so explicitly: *"D is radical in exactly one place."* |
| 6 | Five screens per direction = 20 concepts | **CONFIRMED** | `#### Screen 1`–`Screen 5` under each of the four `### 8.21` blocks (L1590/1615/1640/1661/1683 · 3123/3151/3177/3200/3226 · 4748/4778/4803/4826/4851 · 6316/6348/6375/6398/6423) = 20. Each covers Purpose, Hierarchy, Composition, Primary visual, Key components, Data shown, Interaction emphasis, Distinctive behaviour, Light considerations, Dark considerations — R4 satisfied. Image-generation requirements are carried separately in §8.22. The screens are exactly the five required: Home/Today, Habit Detail, Tasks, Insights, Onboarding. |
| 7 | 20 briefs, each carrying all 16 required elements, self-sufficient | **CONFIRMED** | Scripted check of all 20 `#### Brief` blocks against the 16 R8 element labels: **20/20 present; 0 briefs missing any element; 0 thin blocks.** Inventory A1–A5, B1–B5, C1–C5, D1–D5 covering exactly the five required screens. Spot-read of A1 and A2 (L1706–1813) confirms concreteness without repository access: exact element order ("3 of 5" larger than the greeting, a wider right margin than left, Manrope with a real Bold, tabular figures, one deep-madder accent appearing only on three ticks, no glass/translucency/gradient/glow), literal content to render (*"Drink water 5 OF 8 GLASSES · Read 20 pages 0 OF 20 · Tuesday 29 September"*), an explicit interaction state (mid-day, 3 of 5, two partial targets), and hard MUST-NOT / anti-pattern lists naming the specific things an image model would otherwise hallucinate. Briefs reference only in-document names (Manrope, Material grammar), so no repository lookup is implied. |
| 8 | Key audit questions answered per direction; Q1 and Q3 direct | **CONFIRMED** | §10 covers **Q1–Q14** individually (L6803–7116) plus a thematic block (L7117–7149) for habit visualisation, streak visualisation, progress visualisation, onboarding argument and motion language — each with all four positions stated. Q1 (L6803–6821) is direct and explicit: four positions, each naming a depth mechanism *the other environment cannot use*, closing with the statement that no direction treats Light as a brightness switch. Q3 (L6842–6855) is likewise direct: place / number / shape / number-at-true-figure-scale. Q2 (L6823–6840) and Q6 (L6914) are per-direction. Each direction additionally carries its own `#### Q1 — the explicit answer` subsection (L706, 2163, 3694, 5367). Q6 is permitted to stay open by R5 and is handled as a *stated position* per direction (A depends on the variable family and says so; B needs nothing new; C makes it low-stakes; D is indifferent by design and deliverable either way) — that is an answer, not a dodge, and the audit's openness is preserved rather than filled in. |
| 9 | No winner selected | **PARTIALLY CONFIRMED** | Four explicit no-winner statements (L46, L78, L6734–6740, L7902–7904) and a fifth at L7938–7939: *"The choice between the four directions is a separate task… and nothing in this document or its output pre-empts it."* No direction is declared best, recommended, preferred or selected. **However**, 11 sentences place individual design answers in an order across the four — see Finding 1 (MAJOR). |
| 10 | No numerical scores or rankings of the directions | **CONFIRMED** | No score column, no numeric rating, no percentage, no rank, and no ordinal of the four anywhere in the document. The three legitimate uses named in the brief were each confirmed in context: (a) the product's **consistency score** metric — a product fact, L3443/3646/4733; (b) **ranked / worst-first category lists** proposed for the Insights screen — a screen design feature, L1307, 2801, 4396, 5984, 7799; (c) the document's own **no-winner statements** — L46, 78, 6734, 7902. (The Finding 1 issue concerns *verbal* superlatives, not numerical scoring, and does not contradict this verdict.) |
| 11 | No contradiction of the audit's evidence | **CONFIRMED** | 15 load-bearing factual claims sampled against the audit; all match. `3.11:1` Light / `5.22:1` Dark and "the only measured contrast evidence" — audit L593, L1621, L2436 ✔ · 96 dp `ProgressOrb` with a permanent 6 s animation — audit L503, L733 ✔ · streak at 12 sp against a 20 sp habit name — audit L87, L779 ✔ · `HomeHero.kt` 771 lines vs `HabitsHero.kt` 222 lines — audit L764, L2195 ✔ · "seven progress representations" — audit L873, L1806 ✔ · "four of six Hero messages" — audit L2171 ✔ · reduce-motion read at 1 of 14 call sites (audit CP-3, "13/14 inert") — audit L93 ✔ · Manrope single `wght` axis 200–800, seven named instances, default 200, a real 700 present — audit L439–453, L87 ✔ · CP-1…CP-10 and HO-1…HO-12 ID ranges ✔ · "nine strengths" (audit §18: *"Nine items"*, L1755) ✔ · "twenty protected items" (audit §24 has exactly 20 numbered items, counted over L2201–2244) ✔ · ~14 scattered surface/press/dialog counts (audit L1807) ✔ · seven Material slots collapsed to seven sizes with no step between 28/20/20/16 (audit L87) ✔. **One nuance recorded, not a defect:** §2.1 writes "§19 the eleven radical opportunities" while also citing "§22 HO-1…HO-12". The audit's own §19 preamble says *"Eleven opportunities"* although the section carries twelve HO IDs. The document reproduces **both** the audit's prose and the audit's IDs, which is exactly what R1's "preserve the audit" requires — it neither corrects nor silently overrides the audit. |
| 12 | Protected product truth respected; nothing smuggled in | **CONFIRMED** | §3.1 states 19 numbered fences, each phrased as truth + prohibition. Full-document sweep for sign-in / signin / log-in / share sheet / leaderboard / league / friend / team / cloud / backend / server / sync / location / health platform / camera / microphone / contacts / wearable / permission: **every hit is either a prohibition (§3.1 L276–284, §4.5, §12.4 L7476–7479, §15.2 #15 L7803–7806, §16.3 L7911–7913) or a quotation of the product's own offline promise.** Sign-in appears only inside MUST-NOT lists (L1947, L3478, L5110, L6722). **No direction implies a backend, an account, a sign-in, a share/social feature, a leaderboard, or a new permission** — including by implication: the onboarding "argument" in all four is the offline/on-device promise itself, not an account. The scheduled-occurrence streak rule is treated as *the* hardest fence (§3.1 #7, "a visual fence, not a data fence") and is encoded in all four directions' rest-vs-missed marks and in §15.2 #1. Reduced motion is answered per direction per motion. Accessibility floors (48 dp, 4.5:1 in **both** environments, dynamic-type-safe, content descriptions, state never by colour alone) are §3.1 #11 and re-tested per direction — e.g. A's structural marks make colour-alone impossible *by construction* (L7248). §3.1 also carries the no-hardcoded-colours rule, the four-destination nav rule, Home-focuses-on-today, manage/act/understand, append-only history, `java.time`, pure-Kotlin domain, the single-environment ban, and the design document's rejected-pattern list. |
| 13 | No application implementation files changed | **CONFIRMED** | `git status --short`: 11 modified files under `app/src` — 9 in `presentation/**` (`MomentumHeroArtwork.kt`, `ProgressOrb.kt`, `HabitsHero.kt`, `InsightsScreen.kt`, `InsightsViewModel.kt`, `HomeHero.kt`, `TargetEntryDialog.kt`, `TodayViewModel.kt`, `Tokens.kt`) plus the 2 declared tests (`MomentumHeroArtworkTest.kt`, `HomeHeroAssetsTest.kt`) — plus `AGENTS.md` and `docs/architecture/decisions/DECISIONS.md`. The only new file attributable to this task is `MOMENTUM_CONCEPT_EXPLORATION.md`. All other untracked paths (`MOMENTUM_PREMIUM_DESIGN_AUDIT.md`, `ISSUES.md`, `PROJECT_STATUS.md`, `task.md`, `design/`, `knowledge/`, `docs/design/AURORA_CONCEPT_ASSETS.md`, `docs/architecture/MULTI_AGENT_PERMISSIONS.md`, `docs/development/`, `.opencode/`, `.agents/`, `opencode.json`, `skills-lock.json`, `app/src/test/.../domain/model/`) are pre-existing Phase 1 / infrastructure artefacts. **Two honest caveats.** (a) *The review brief described the baseline as "11 modified files under `presentation/**`"; the actual split is 9 under `presentation/**` and 2 under `app/src/test/`, 11 in total under `app/src`. The set matches the declared baseline — only the brief's phrasing is loose. Not a defect in the artifact.* (b) *Git cannot time-order uncommitted working-tree changes, so "unchanged by this task" is established content-wise (the 9 presentation diffs are Hero/orb/insight polish consistent with the pre-existing ADR-34…ADR-39, and the `DECISIONS` diff is entirely those ADRs), not by timestamp.* |
| 14 | No images were generated | **CONFIRMED** | `git status --short` filtered for `.png/.jpg/.jpeg/.webp/.svg/.gif`: **zero hits.** The only image files in the tree are six pre-existing `.webp` under `app/src/main/res/drawable/`, last modified **2026-09-15** and **2026-09-24**, both predating this task (today 2026-09-28). `design/` contains no image files at all (it is the web-font pack of ISSUE-024). The document states the fence itself at L372–374: *"No images are produced by this stage. Zero."* |
| 15 | Phase 3 has not started | **CONFIRMED** | No design-token spec — explicitly disclaimed at L344–347: *"It names no token, no spacing scale, no hex value, no `Type.kt` entry, no Compose type."* No design-system change — `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` does not appear in `git status` at all (neither modified nor untracked), and L354–355 states it is untouched. No ADR — the `DECISIONS.md` diff is +430 / **−0**, entirely ADR-34…ADR-39 (Hero artwork, window dim, knowledge-as-source-of-truth), containing **0** matches for `Step 2`, `Phase 2A`, `concept exploration` or `direction`. No implementation planning — `task.md` §8 IMPLEMENTATION PLAN reads "None. No Developer is assigned to this task." |
| 16 | Taste skills applied as instructed; no web-oriented skill dictating direction | **CONFIRMED** | §2.5 names `imagegen-frontend-mobile` as **primary** and lists 14 concrete properties drawn from it (platform pinned Android-native, screen-first rather than mood-board, a locked per-direction design bible, safe-area awareness, a stricter cleanliness bar on each direction's first screen, a believable flow order, clean even content-first framing, plus the primary skill's anti-tell list including its banned-copy list). §2.5 names `high-end-visual-design` and `redesign-existing-projects` as **secondary/selective** with specific extractions. §2.6 records four **declined** hazards (the ban-Material-Icons rule, the mono-companion default, doubled padding, card rotation) each with a reason, and §2.5 records a further set of declined web rules (banned fonts, banned Material Icons, square/clip-path masks, sticky-nav and bento-grid archetypes, `rounded-[2rem]`, the `cubic-bezier` curve, hover/scroll-reveal vocabulary). §2.5 names `gpt-taste`, `imagegen-frontend-web`, `image-to-code`, `industrial-brutalist-ui` and `minimalist-ui` as inspected-but-never-governing, and records that `industrial-brutalist-ui` was **checked and rejected** specifically to avoid collapsing the exploration into B's identity. Web-pattern sweep: 15 hits for Tailwind / GSAP / landing page / hero section / sticky header / browser chrome — **all 15 sit inside explicit ban lists** (§4.5 L465–466, §12.4 L7447, L7450, and the MUST-NOT lists at L1709, L3482, L6727, L7565). No landing-page layout, web hero, web nav, Tailwind aesthetic, GSAP choreography, website CTA architecture, desktop-first composition or fake browser chrome appears as product direction in any of the 20 briefs. |
| 17 | The four audit-identified problem classes challenged structurally | **PARTIALLY CONFIRMED** (11 of 12 classes) | Per-class ruling — **weak numeric hierarchy** → structural (D: a real 700 registration plus a meaning-driven register order, not a size tweak; A: numbers outranking words; B: labels/numbers split into two registers; C: opposite line-height behaviour per register) L7215–7218 ✔ · **Light-mode depth** → structural (four *different* depth mechanisms, each one the other environment cannot use: cast shadow, milled bevel, thread occlusion, tonal ladder + hue-carrying shadow) §10 Q1 ✔ · **snackbar-based completion celebration** → structural (four different reward moments; both permanent ambient loops removed; the unreachable milestone state wired) ✔ · **invisible consistency metrics** → structural (consistency score + missed occurrences given a placement with a stated window in every direction) ✔ · **generic/local visual decisions** → structural (§9 "how many surface levels, and what earns each"; D consolidates ~14 scattered decisions to four) ✔ · **excessive inline alpha** → structural (whole-composite alpha banned; A/B/C remove the entire surface-fill category; §15.2 #19) ✔ · **multiple surface systems** → structural (3 / 3 / 4 / 4 named levels, each with a stated reason) ✔ · **conflicting icon systems** → structural (Q6 answered four different ways; D collapses three drifted key vocabularies into one registry) ✔ · **excessive glass** → structural (A/B/C zero translucency; D demotes glass to floating layers only — and §2.3 L438 notes that reducing glass is *closer* to the locked design, not further) ✔ · **dialog translucency** → structural (rejection criterion §15.2 #18; D removes translucent destructive dialogs) ✔ · **weak missed/rest distinction** → structural (the most thoroughly answered class: four mark systems, all four making rest an *absence* and missed a distinct mark; §15.2 #1 makes it an invalidity test) ✔ · **inconsistent inset handling** → **NOT structurally challenged** — see **Finding 2** (MINOR). No response exists beyond the generic safe-area fence, and only for D. |
| 18 | The nine existing strengths addressed per direction with how they are transformed | **CONFIRMED** | §11.1 items 1–9, each with a four-row `Survives? / How it is transformed` table: `CompletionButton` (L7169), manage/act/understand (7180), `HabitDetailScreen.DayCell` (7191), the artwork caller contract (7203), the bundled typeface (7213), `HabitsHero` (7225), reduced motion (7235), accessibility past the checklist (7246), the Settings copy and offline promise (7256). §11.2 (L7260) covers the eight user-named items. The document explicitly forbids the lazy answer at L7157: *"'It survives' is not an acceptable answer on its own."* Transformations are specific and structural, not assertions — e.g. A converts the DayCell's three shapes into tick / struck / absence so colour-alone is impossible by construction; B extends the mark to a graduated channel and names the data dependency that creates; C replaces the ring with a swatch whose density *is* the score; D propagates the exact existing marks and adds a fourth state for a genuinely missed scheduled day; the Settings copy is *moved* into onboarding in all four without new copy being invented. |

**Tally: 15 CONFIRMED · 2 PARTIALLY CONFIRMED (criteria 9 and 17) · 0 REFUTED.**

### Tests

**No build, no test and no lint was performed or attempted. This machine has no JDK
and no Android SDK, there is zero application impact, and no build is required for a
Markdown deliverable. I make no claim about any build result, any unit or
instrumented-test result, any lint result, or any rendered image. No rendered image
exists, so nothing in this deliverable is visually verified — every judgement recorded
above is a text-and-structure judgement.**

Checks actually executed, all read-only:

| Check | Command | Result |
|---|---|---|
| Working-tree state | `git status --short` | 11 modified under `app/src`, plus `AGENTS.md`, `DECISIONS.md`; `MOMENTUM_CONCEPT_EXPLORATION.md` the only new task file |
| Image sweep | `git status --short` filtered by image extension | 0 hits |
| Image mtimes | `Get-ChildItem -Recurse` over `design\` and `app\src\main\res` | 6 `.webp`, all 2026-09-15 / 2026-09-24 |
| ADR sweep | `git diff --stat` and `git diff` on `DECISIONS.md` | +430 / −0; ADR-34…39 only; 0 `Step 2` / `Phase 2A` hits |
| Encoding / integrity | byte read + regex counts | 546,107 B; 7,951 lines; no BOM; 0 U+FFFD; 7,951 CRLF, 0 bare LF |
| Transcription fidelity | case-sensitive line-by-line compare, `task.md` 305–8255 vs emitted 1–7951 | **0 mismatches / 7,951 lines** |
| Section structure | heading dump, `^#{1,3} ` | all 16 sections, correct order, no extras |
| Brief completeness | scripted 20-block × 16-element check | 20/20 briefs, 0 missing elements |
| Table integrity | escape-aware pipe count per table | 56 tables, 0 inconsistent |
| Fence balance | count of ` ``` ` lines | 0 fences (nothing to balance) |
| Truncation / placeholders | regex sweep for 11 placeholder patterns | 0 hits; 4 U+2026, all range markers |
| Self-containment | regex sweep for `task.md`, `TASK-00`, `§7`, role names, write-grant wording | 0 hits |
| No-winner / score sweep | regex sweep for `strongest`, `best`, `rank`, `recommend`, `prefer`, `score`, `winner` | 60+ hits, individually ruled — see Finding 1 |
| Cross-direction sweep | regex for `the only direction`, `any of the four`, `of the four directions`, `most honest`, `most surprising` | 39 hits, individually ruled |
| Product-truth sweep | regex for sign-in / share / social / leaderboard / cloud / backend / permission / location / camera | 51 hits, all prohibitions or the offline-promise quote |
| Web-pattern sweep | regex for Tailwind / GSAP / landing page / hero section / sticky header / browser chrome | 15 hits, all inside ban lists |
| Audit contradiction spot-check | 15 numeric/factual claims grepped against `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` §18–§25, CP, HO, Q, App. E | 15/15 consistent; one faithful reproduction of an audit-internal count tension (AC-11) |

### Architecture

Conforms. The deliverable is analysis and alters no architecture, and it correctly
treats locked decisions as fences rather than as material. §2.2 names the locked ADR
bounds (ADR-07, 08, 19, 20, 21, 22, 23, 26, 27, 31–39), read by number rather than
end-to-end, and §2.3 states what is **not** locked — the type scale, the Light/Dark
token values, the glass default, ten dead tokens, the Hero machinery, the single-entry
theme gallery — so no direction mistakes an open door for a wall or a wall for a door.
§2.4 inherits the audit's unknowns rather than converting them, including the honest
admission that `Manrope` glyph coverage is undecidable without a font-mapping tool
while the weight axis *was* parsed. §3.1's fences are the audit's §24 items restated.
No ADR was written (AGENTS.md §3, §12 and §14.15 correctly defer any ADR-worthy
decision to Phase 3), and no design-system file was touched. The one
architecture-adjacent risk — that Phase 2A quietly settles Q1/Q3/Q6 — is explicitly
fenced at L365 ("Settle any of them for the project" sits in the *does not* column) and
upheld: all four directions answer each question, and §16.4 reserves the choice to a
later task with a different owner.

### Regression

**No application regression risk exists, and I claim no application-wide regression
verification.** The change set is one new Markdown file at the repository root; the
application tree, resources, tokens, navigation, dependencies and database are
untouched, so there is no runtime path to regress. The only files modified anywhere are
the pre-existing Phase 1 / infrastructure baseline listed in AC-13, whose content is
unrelated to concept exploration (Hero/orb/insight polish matching ADR-34…ADR-39, and
the `AGENTS.md` §14 multi-agent section). I ran no build, test or lint, so I make no
claim about that baseline's own correctness — it is Phase 1's verified record, not this
task's to re-establish. The regression this task *could* have caused is documentary
rather than technical — an emitted artifact that diverged from its authored source —
and that was checked directly and is clean (0 / 7,951 mismatches).

### Final Verification Result

**First pass: NOT_VERIFIED.** (retained as issued)

Not because the artifact is deficient: 15 of 18 criteria are fully CONFIRMED, the other
two are PARTIALLY CONFIRMED on specific located defects rather than on missing
evidence, and one MAJOR plus one MINOR finding is the complete defect set. It is
NOT_VERIFIED because criterion 9 is not fully met and criterion 17 is not fully met,
and because two things this reviewer is forbidden to produce do not exist: a
build/test result (no JDK/SDK on this machine) and any rendered image. Nothing here is
visually verified.

The result becomes **VERIFIED** on completion of Finding 1 and Finding 2, both of which
are contained, located and mechanical. Neither requires new design work: Finding 1 is
a re-wording of 11 sentences; Finding 2 is one rejection criterion plus one line per
direction.

---

### Re-verification — correction cycle 1 (appended; supersedes the result above)

**NOT_VERIFIED.** One finding closed, one substantially closed with a residual.

- **Finding 2 (MINOR) — CLOSED.** §15.2 item 25 added and adequate, items 1–24 kept
  their numbering (1…25 sequential, no gap), and all four directions carry a real inset
  contract inside their own §8.11 Navigation in their own material vocabulary. Both
  required additions present. Criterion 17 therefore moves to **CONFIRMED** — all
  twelve problem classes are now challenged structurally.
- **Finding 1 (MAJOR) — PARTIALLY CLOSED.** All 11 originally flagged instances plus the
  §9 matrix cell are genuinely fixed, each by re-wording that preserves the design
  reasoning; the 2 additional instances the Architect found are genuine and now fixed,
  though I cannot independently verify *which* two because no pre-correction diff
  exists. The over-correction risk did **not** materialise: all 7 previously-accepted
  intra-direction / product-referent superlatives are still present and unaltered, and
  the D name-argument passage was rewritten out in full rather than truncated.
  **Residual: 4 clear instances + 1 borderline** (L4173, L4580, L4584, L7100, L7576)
  of the same defect class, all in Direction C's day-complete "physicality" passage and
  one line of §13. They are pre-existing — outside my first-pass sweep patterns, which
  is a gap in my own sweep and not an attribution to the correction. Criterion 9
  therefore remains **PARTIALLY CONFIRMED**; every other criterion remains as issued,
  and the other fourteen previously-CONFIRMED criteria are unaffected.
- **Regression — PASS.** No regression introduced. Structure byte-stable: 16 sections in
  order, 4 directions, 88 `### 8.x`, 20 `#### Screen`, 20 `#### Brief`, Q1–Q14 complete.
  The +52-line growth is fully and exclusively the F2 additions (4 inset contracts +
  §15.2 item 25). D still controlled evolution; the §2.1 count tension preserved
  as-is; the recorded §11 non-defect left alone; no placeholder, truncation, empty
  section, encoding damage or scaffolding leak; §9 matrix purity holds (33 property
  rows, no score/rank/percentage/preference, the one superlative hit being a state name);
  `git status` identical to the first pass with the deliverable the only new file; no
  images; no ADR; no application file changed.

**Revised tally: 16 CONFIRMED · 1 PARTIALLY CONFIRMED (criterion 9) · 0 REFUTED.**

The result becomes **VERIFIED** when the four residual F1 sentences are re-scoped. That
is a four-line wording change confined to Direction C and one line of §13, with no
design content, no metadata, no brief and no criterion other than 9 affected. There is
no other outstanding defect, and no outstanding evidence gap other than the standing
environment limits below.

**Standing environment limits, unchanged and not worked around.** No build, no unit or
instrumented test, no lint, and no image generation was or could be run: this machine
has no JDK and no Android SDK, the deliverable has zero application impact, and no
build is required for a Markdown artifact. **I make no claim about any build, test,
lint or visual result.** No rendered image exists, so nothing in this deliverable has
been or can be visually verified; every judgement recorded in both passes is a
text-and-structure judgement.

---

### Final acceptance pass — correction cycle 2 (appended; this supersedes the result above)

**VERIFIED.**

- **Finding 1 (MAJOR) — CLOSED.** All 5 cycle-2 residuals correctly re-scoped with the
  design reasoning preserved in every case (L4174, L4582-4584, L4589, L7105, L7581); the
  3 further instances the Architect's own sweep found were genuine instances of the same
  class and are now correctly re-scoped (L2102-2104, L3574-3575, L7402-7403). I ran my
  own independent whole-file sweep for the construction class and ruled on every
  remaining hit: **no genuine ranking claim remains, and nothing acceptable was
  stripped.** No under-correction and no over-correction. The self-critical /
  self-promotional distinction I drew in cycle 1 was applied consistently in all three
  cycles (L1995 and L4026 correctly retained; the five pro-C self-promotional claims
  correctly re-scoped). One borderline descriptor at L7581 ("A's version is the most
  *subtractive*") is recorded as a stylistic preference, not a defect, and does not
  block acceptance.
- **Finding 2 (MINOR) — CLOSED and untouched.** §15.2 still 1…25 sequential with items
  1–24 keeping their numbers; all four inset contracts still in each direction's
  §8.11 Navigation (L1029, L2491, L4078, L5722).
- **Regression — PASS.** 16 sections in order, 4 directions, 88 `### 8.x`, 20
  `#### Screen`, 20 `#### Brief`, Q1–Q14 complete, D still controlled evolution, §2.1
  count tension preserved as-is, §11 non-defect left alone, §9 matrix clean across its
  33 property rows, no placeholder/truncation/empty section/encoding damage/scaffolding
  leak, `git status` byte-identical to passes 1 and 2, no images, no new ADR, no
  application file changed. The 12 cycle-1 sentences were not re-edited — all 12
  verified present and verbatim. The +5-line delta is entirely re-wrapping of the 8
  re-worded sentences; no metaphor, colour, type size, mark, screen, brief or value was
  altered at any point in any cycle.

**FINAL TALLY: 18 CONFIRMED · 0 PARTIALLY CONFIRMED · 0 REFUTED.**
Criterion 9, the only criterion in question, moves to **CONFIRMED**: the document
contains no ranking, no scoring, no declared best, no recommendation and no preference,
and its own §1.4 and §9 promises are now actually true of its text. Every criterion
carries a verdict, no BLOCKER or MAJOR finding remains, and no finding of any severity
is outstanding.

**TASK-008 is ACCEPTED.** Phase 2A is complete and verified as a design-strategy
artifact. This is the final pass under AGENTS.md §14.5 and I am not opening a further
cycle.

**Carry-forward to Phase 2B, recorded as a note and not as a finding.** Nothing in the
deliverable is visually verified, because no rendered image exists. The first Phase 2B
render is therefore the first real test of the four directions, and §12.4/§15.3 already
specify the rejection and regeneration discipline for it. That is Phase 2B's acceptance
surface, not a gap in Phase 2A.

**Standing environment limits, restated and not worked around.** No build, no unit or
instrumented test, no lint, and no image generation was or could be run: this machine
has no JDK and no Android SDK, the deliverable has zero application impact, and no
build is required for a Markdown artifact. **I make no claim about any build, test,
lint or visual result.** No rendered image exists, so nothing in this deliverable has
been or can be visually verified; every judgement across all three passes is a
text-and-structure judgement.

---

## 12. HANDOFF HISTORY

<!-- ORCHESTRATOR ONLY -->

| # | From | To | Summary |
|---|---|---|---|
| 1 | Orchestrator | Architect | TASK-008 initialised. Read Phase 1 audit (authoritative), `AURORA_CONCEPT_ASSETS.md`, permission matrix, `PROJECT_STATUS.md`, `ISSUES.md`, `knowledge/index.md`. Classification fixed: ARCHITECTURE + VERIFICATION; IMPLEMENTATION not assigned. Requirements R1–R13 fixed. Four directions (three radical + one controlled evolution), five key screens each, 20 image-generation briefs, no winner, no scores, zero application impact. |
| 2 | Architect | Orchestrator | **Concept authored.** 16 sections in required order; 4 directions (A **Press**, B **Instrument**, C **Loom**, D **Aurora, Governed**); 88 `### 8.x` (4 × 22); 20 screen concepts; 20 image-generation briefs; Q1–Q14 answered per direction. No design content changed by any agent. No ADR — nothing met the ADR criteria at this phase. |
| 3 | Orchestrator | — | **Emission.** B3 pattern: Architect authored into §7; Orchestrator mechanically transcribed to `MOMENTUM_CONCEPT_EXPLORATION.md` (extraction only, no rewriting). Verified 0/7,951 case-sensitive mismatches; encoding clean; no scaffolding leak. |
| 4 | Reviewer | Orchestrator | **Verification pass 1 — NOT_VERIFIED.** 15 of 18 criteria CONFIRMED. **F1 (MAJOR):** 11 sentences used superlatives to compare design answers *across* the four directions, contradicting the document's own no-winner promise; the worst sat inside the §9 matrix. **F2 (MINOR):** audit CP-10 (three coexisting inset strategies, 240 dp reserved on Habit Detail) never restated. Recorded non-defect: §11 pro-D scope rhetoric. Reviewer also **corrected two Orchestrator observations**, confirming independence. |
| 5 | Architect | Orchestrator | **Correction cycle 1.** F1: 11 flagged + 2 self-found instances re-worded to intra-direction scope; the 7 accepted intra-direction/product-referent superlatives deliberately preserved. F2: §15.2 item 25 (inset rejection criterion) + four per-direction inset contracts in §8.11. Re-wording and narrow insertion only — no design content altered. |
| 6 | Orchestrator | — | **Re-emission** (0/8,003 mismatches) and bounded re-verification routed. |
| 7 | Reviewer | Orchestrator | **Verification pass 2 — NOT_VERIFIED, residual.** F2 **CLOSED**. F1 substantially closed; **residual of 4 clear + 1 borderline** instance of the same class, which the Reviewer attributed to a disclosed gap in its **own** first-pass sweep rather than to the correction. Over-correction risk (stripped design reasoning) did **not** materialise — 7/7 accepted superlatives intact. |
| 8 | Architect | Orchestrator | **Correction cycle 2 (final, AGENTS.md §14.5).** 5 residual instances re-scoped, plus **3 further genuine instances the Architect's own wider sweep found** that both earlier Reviewer sweeps had missed. 11 examined-and-left candidates documented with reasons so under- and over-correction could both be checked. |
| 9 | Orchestrator | — | **Re-emission** (0/8,008 mismatches) and final acceptance pass routed. |
| 10 | Reviewer | Orchestrator | **Final acceptance — VERIFIED.** 18 CONFIRMED / 0 PARTIALLY CONFIRMED / 0 REFUTED. Criterion 9 (no winner) closed. All 12 cycle-1 sentences confirmed verbatim and un-re-edited; F2 confirmed untouched (§15.2 still 1…25, four inset contracts intact); regression check PASS; `git status` byte-identical across all three passes. One borderline noted as a stylistic preference, not escalated. Reviewer stopped under §14.5. |
| 11 | Orchestrator | — | TASK-008 closed as **COMPLETE / VERIFIED**. `PROJECT_STATUS.md` updated. **No issue opened** — every defect found across three cycles was a wording or coverage defect in a Phase 2A authoring artifact, not a product defect. |

---

## 13. KNOWN BLOCKERS

<!-- ORCHESTRATOR ONLY -->

- **B3 — emission is an Orchestrator step, not an Architect step. RESOLVED, and
  it is the established pattern rather than a defect.**
  `opencode.json` grants `edit` on `MOMENTUM_CONCEPT_EXPLORATION.md` to the
  **orchestrator only**; the Architect's map is `task.md` + `DECISIONS.md`
  behind `"*": "deny"`. The same constraint Phase 1 resolved via `ISSUE-023`
  applied here: the Architect authored into its owned `task.md` §7, and the
  Orchestrator **mechanically transcribed** — extraction only, no rewriting, no
  summarising, no authorship. Executed three times (initial + two corrections);
  fidelity confirmed at **0 case-sensitive mismatches across 7,951 / 8,003 /
  8,008 lines** respectively. The Orchestrator did not author or improve any
  concept content.
- **B2 (carried, still true) — no rendered visual reference exists in-repo.**
  No screenshots, mockups, Figma/Sketch/Penpot links, layout references,
  colour/spacing specs or golden files. The Phase 1 audit's visual judgements are
  static code-reading judgements, and **Phase 2A inherits that limit exactly**.
  The delivered document is hypothesis text awaiting Phase 2B; **nothing in it
  is visually verified, and it does not claim to be.** The Reviewer recorded
  this as a carry-forward note for Phase 2B, not as a Phase 2A gap.
- **B4 — no JDK/SDK on this machine.** No build, unit test, instrumented test
  or lint was possible. None was required: the deliverable is Markdown with zero
  application impact. **No build, test, lint or visual result is claimed
  anywhere in this task's record.**
- **B5 — git cannot time-order uncommitted changes.** The Phase 1 working tree
  is dirty, so "the application tree is unchanged by TASK-008" is established
  **content-wise against the declared Phase 1 baseline** (recorded verbatim in
  §12 handoff 1 and re-confirmed at all three review passes), not by timestamp.
- **F-9a warning (carried) — the repository was never exhaustively inventoried
  at the top level.** No new top-level directory was missed by this task, but the
  blind spot is not claimed to be closed.

---

## 14. FINAL OUTCOME

<!-- ORCHESTRATOR ONLY -->

**COMPLETE / VERIFIED / CLOSED. Phase 2A delivered. Phase 2B NOT STARTED.**

| | |
|---|---|
| Artifact | `MOMENTUM_CONCEPT_EXPLORATION.md` (repository root) |
| Size | 549,926 bytes · 8,008 lines · UTF-8, no BOM · 0 replacement characters |
| Sections | **16 of 16** in the exact required order |
| Directions | **4** — A *Press*, B *Instrument*, C *Loom*, D *Aurora, Governed* |
| Screen concepts | **20** (4 directions × 5 key screens) |
| Image-generation briefs | **20** (A1–A5, B1–B5, C1–C5, D1–D5), 16 required elements each |
| Audit questions | **Q1–Q14** mapped per direction, plus habit / streak / progress visualisation, onboarding argument and motion language |
| Final verification | **VERIFIED — 18 CONFIRMED / 0 PARTIALLY CONFIRMED / 0 REFUTED** |
| Application code | **Unmodified** — `git status` byte-identical to the Phase 1 baseline across all three review passes; the deliverable is the only new file |

### 14.1 The four directions

| | Concept | "Momentum" is | Q3 — "today" is a | Light / Dark |
|---|---|---|---|---|
| **A** | **Press** — a printed ledger | the record: evidence, not encouragement | a **place** (an open column) | warm paper with a cast shadow / a dark ink field where the record is the light-toned object |
| **B** | **Instrument** — mass × velocity | mass × velocity, a quantity | a **number** on a calibrated scale | a printed matte faceplate with a milled bevel / a backlit panel with inner self-illumination |
| **C** | **Loom** — a woven run | the run, carried by gesture not the word | a **shape** (a weft pass) | undyed linen with thread occlusion / indigo-dyed with a twill sheen |
| **D** | **Aurora, Governed** — controlled evolution | accumulated light: the field's behaviour | a **number** at true figure scale | a real tonal ladder with a hue-carrying shadow / tonal lift with a white highlight |

The directions differ on all nine axes of §4.1 simultaneously, and **none of the four shares a position on Q1, Q2, Q3 or Q6**. Per the audit's own process recommendation (§25), D serves as the control against which A/B/C become legible.

### 14.2 What the correction cycles found

Three cycles, all bounded, all confined to authoring defects — **no design content, metaphor, colour, type size, mark, screen or brief was altered at any point**:

1. **F1 (MAJOR).** The document initially broke its own no-winner promise, using superlatives to compare design answers *across* the four directions. Three passes progressively closed 19 instances; the Reviewer's second pass disclosed a gap in its own first-pass regex, and the Architect's final sweep found 3 more that both earlier sweeps had missed. The 7 legitimate intra-direction and product-referent superlatives were deliberately preserved, and the Reviewer confirmed **no over-correction** in every pass.
2. **F2 (MINOR).** Audit CP-10 was not restated. Closed with one rejection criterion (§15.2 item 25) and four per-direction inset contracts, in each direction's own material vocabulary.

The Reviewer **corrected two of the Orchestrator's own observations** in pass 1, which is the evidence that the review was genuinely independent rather than confirmatory.

### 14.3 Carried forward, deliberately unresolved

These stay open by design and must not be mistaken for decisions:

- **Q1** — no direction is *adopted*. Four mutually exclusive answers are documented; the choice belongs to Phase 2B's evaluation, not to this document.
- **Q6** — the icon-family conflict (code = Material *Icons*, static, no weight axis; docs + concept pack = Material *Symbols* Rounded, variable 100–700) is **unresolved**. Direction A declares a dependency on it; B and C are deliberately indifferent; D is deliverable either way.
- **Nothing in this document is visually verified.** No image was generated and none could be. The repository still has no rendered reference (blocker B2), so all Phase 1 visual judgements and all of Phase 2A's are static text judgements. The first render is the first real test of the four directions.
- **The single-theme gallery is recorded deferred debt (ADR-21);** the `themeId` default is an unrecorded bug (`ISSUE-021`). Still not to be conflated.
- **`CREATE_ITEM_SPECS.md` was never read** in Phase 1, so the audit's creation-flow verdicts remain provisional. No direction here proposes a creation-flow change.
- **Git cannot time-order uncommitted working-tree changes.** "The application tree is unchanged by this task" is therefore established content-wise against the declared Phase 1 baseline, not by timestamp.

### 14.4 No issue was opened

Every defect found across the three cycles was a wording or coverage defect in a Phase 2A authoring artifact, now corrected. **None was a product defect**, so per AGENTS.md §14.9 none belongs in the issue registry. `ISSUE-020`, `ISSUE-021` and `ISSUE-022` remain OPEN and are Phase 2B/3 inputs; `ISSUE-023` and `ISSUE-024` remain CLOSED/RESOLVED.

### 14.5 Phase boundary held

No image generated. No Kotlin, Compose, navigation, design-token, resource or dependency change. No design-system modification. No ADR. No Phase 3 work. **No direction selected, ranked, scored or recommended** — the one borderline comparative descriptor the Reviewer found in the final pass was ruled a stylistic preference that does not pre-empt evaluation, and was recorded rather than escalated.

---

## 15. PHASE 1 CARRY-FORWARD (TASK-007 CLOSED)

<!-- ORCHESTRATOR ONLY -->

Phase 1 is **COMPLETE / VERIFIED / CLOSED**. Its artifact is
`MOMENTUM_PREMIUM_DESIGN_AUDIT.md` (352,990 bytes, 2,509 lines, 25 sections,
appendices A–F). Four facts are load-bearing for this task and are carried
here so a later session does not mistake silence for a decision:

1. **Q1 — is the Light theme a designed environment, or an inversion of Dark?**
   Left open **on purpose** by Phase 1. It is the largest open question, and the
   audit's process recommendation is that a direction which does not state its
   answer has not been submitted.
2. **Q6 — which icon family Momentum uses** is a three-way conflict: the code
   uses Material **Icons** (`material-icons-extended`), the documentation and
   `design/aurora-concept/` say Material **Symbols** Rounded, and the two differ
   functionally — a static vector set has **no weight axis**, so the concept
   pack's variable-weight idea is unavailable to the app today. Deliberately
   unresolved.
3. **The single-theme gallery is recorded deferred debt (ADR-21); the
   `themeId` default is an unrecorded bug (`ISSUE-021`).** These must not be
   conflated.
4. **`CREATE_ITEM_SPECS.md` was never read**, so the audit's creation-flow
   verdicts are **provisional** and are the most likely remaining home of a
   spec/code divergence.

*Correction to the Phase 1 record:* Phase 1 §15.3 stated
`docs/design/AURORA_CONCEPT_ASSETS.md` was prepared but not yet on disk. **It is
now on disk** and is the authoritative status record for the concept pack. The
stale claim is superseded here.
