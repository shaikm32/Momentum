# Momentum — Architectural Decisions (append-only)

> New decisions are new entries. Never silently edit old ones — supersede explicitly.

## ADR-01 — Jetpack Compose for UI
- **Decision:** Compose (Material 3) for all UI, single-activity (`MainActivity`).
- **Context:** Modern declarative UI, needed for expressive custom components/animation.
- **Options:** Views/XML vs Compose vs cross-platform (Flutter/RN).
- **Chosen:** Compose — native, M3 support, best animation story; team (AI+human) velocity.
- **Consequences:** Kotlin-only UI; previews replace layout editor; learn custom-draw for charts.

## ADR-02 — Room for structured persistence
- **Decision:** Room over raw SQLite / SQLDelight / manual files.
- **Context:** Relational data (activities ↔ completions ↔ reminders) with observed queries.
- **Options:** Room, SQLDelight, raw SQLite.
- **Chosen:** Room — first-class `Flow`, KSP, schema export, migration support, largest talent pool.
- **Consequences:** Must maintain entities, migrations, schema JSON in `app/schemas/`.

## ADR-03 — DataStore (Preferences) for settings
- **Decision:** DataStore Preferences for theme id, dark mode, week start, flags.
- **Context:** Small async key-value settings; SharedPreferences is legacy/blocking.
- **Options:** SharedPreferences vs DataStore.
- **Chosen:** DataStore — coroutine-native, type-safe, no main-thread blocking.
- **Consequences:** Async reads; repositories expose settings as `Flow`.

## ADR-04 — Clean Architecture + MVVM, single module
- **Decision:** UI → VM → UseCase → Repository → Room/DataStore; one `:app` module.
- **Context:** Need testability + AI-session navigability without build complexity.
- **Options:** Single vs multi-module; MVP/MVI vs MVVM.
- **Chosen:** Single-module Clean + MVVM — standard, boring, testable. Engines pure Kotlin.
- **Consequences:** Discipline required to keep layers honest; revisit modules only via ADR.

## ADR-05 — Offline-first, no backend
- **Decision:** Zero network dependency; all data on-device; no `INTERNET` permission.
- **Context:** Privacy product; core value (tracking) needs no server.
- **Options:** Local-only vs BaaS (Firebase/Supabase) vs custom backend.
- **Chosen:** Local-only. Future sync (if ever) is additive and needs its own ADR.
- **Consequences:** No auth/analytics/crash SDKs; backup = local export (later); Room is source of truth.

## ADR-06 — Recurrence as type-tagged rule, computed occurrences
- **Decision:** Store `recurrenceType` + JSON `recurrencePayload`; compute scheduled
  dates in `RecurrenceEngine`; never one DB row per future occurrence.
- **Context:** Recurring activities need unbounded schedules + editable rules + history truth.
- **Options:** (a) materialized occurrence rows, (b) iCal RRULE strings, (c) tagged rule + engine.
- **Chosen:** (c) — storage-efficient, editable, testable. RRULE rejected: over-general,
  poor monthly-clamp/DST fit, harder picker mapping.
- **Consequences:** Engine must be exhaustive + covered; recurrence edits apply forward
  (history immutable); new types need tag + engine + payload + tests + UI.

## ADR-07 — Notifications: AlarmManager + WorkManager, no FCM
- **Decision:** `AlarmManager` (exact fire) + `WorkManager` (resilient resync) +
  `BOOT_COMPLETED` receiver; notification actions incl. mark-complete.
- **Context:** One-time/recurring/multiple reminders, reboot recovery, offline.
- **Options:** AlarmManager-only vs WorkManager-only vs FCM (rejected: needs network/account).
- **Chosen:** Combination — exactness where the user asked for it, resilience everywhere.
- **Consequences:** Must handle exact-alarm permission deed (`SCHEDULE_EXACT_ALARM`),
  Doze, re-keying on activity edits; full implementation in Phase 9.

## ADR-08 — Data-driven theme architecture
- **Decision:** `MomentumColors` token class + CompositionLocal; theme registry keyed by id;
  selection persisted in DataStore; 12 launch concepts × light/dark.
- **Context:** Themes are a headline feature, must be consistent + extensible.
- **Options:** Hardcoded per-screen colors vs M3 dynamic color vs token registry.
- **Chosen:** Token registry — full control of gradients/glow/chart colors M3 can't express.
- **Consequences:** No hardcoded colors in screens (lint by review); new theme = data entry + previews.

## ADR-09 — Manual DI via AppContainer
- **Decision:** Hand-written `AppContainer` in `MomentumApp`; no Hilt/Koin.
- **Context:** Small graph; DI frameworks add KSP/build cost + magic for little gain now.
- **Options:** Manual vs Hilt vs Koin.
- **Chosen:** Manual — explicit, zero deps, AI-friendly navigation.
- **Consequences:** Factories handwritten; migrate only if graph pain justifies it (new ADR).

## ADR-10 — Bootstrap toolchain (2026-09-10, this machine)
- **Decision:** Target `compileSdk/targetSdk 35`, `minSdk 26`; AGP `8.7.3`,
  Gradle `8.9`, Kotlin `2.0.21`, Compose BOM `2024.09.00`, JDK 17.
- **Context:** Bootstrap machine (Win11, no JDK/SDK/Gradle — Git only), so versions
  chosen from known-compatible stable sets, not local inspection.
- **Options:** Latest-alpha vs proven-stable trio.
- **Chosen:** Proven-stable trio above (AGP 8.7.x ↔ Gradle 8.9 ↔ JDK 17 verified).
- **Consequences:** First open in Android Studio provisions JDK 17 + SDK 35 +
  `gradle-wrapper.jar`; then run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest`.

## ADR-11 — Hand-written recurrence codec, no serialization library
- **Decision:** `RecurrenceCodec` encodes/decodes the type-tagged rule with
  hand-written builders/parsers instead of adding kotlinx-serialization.
- **Context:** Payloads are tiny (`days`, `interval`, `dayOfMonth`); a library
  adds size/build cost for one small job.
- **Options:** kotlinx-serialization vs `org.json` (Android-only, untestable on
  JVM) vs hand-written codec.
- **Chosen:** Hand-written — pure Kotlin, JVM-unit-testable, lenient by design
  (unknown future tags → Daily, malformed payloads → rule defaults).
- **Consequences:** Adding a recurrence type must extend the codec + tests.

## ADR-12 — Single reminder per activity in v1 UI
- **Decision:** The editor exposes one local reminder (time `HH:mm`); the
  `reminders` table already supports N rows per activity.
- **Context:** Spec asks for multiple reminders "where useful"; one covers the
  reminder use case simply while the scheduling architecture proves itself.
- **Options:** Full multi-reminder editor now vs single now, schema ready for N.
- **Chosen:** Single now — smaller UI, same scheduler/receiver/worker path.
- **Consequences:** Multi-reminder UI is additive later; no schema change needed.

## ADR-13 — Bounded completion windows for streak computation
- **Decision:** Today/Insights/Calendar read completions over trailing windows
  (730 days for Today streaks, 60 days for Insights, visible grid for Calendar)
  instead of unbounded full-history queries.
- **Context:** Streaks need history back to `startDate`, but unbounded queries
  into Compose state violate the performance budget.
- **Options:** Full-history observe vs bounded windows.
- **Chosen:** Bounded windows — streaks beyond 730 days display 730+; documented
  limitation, revisit only with measured need (paged history query).
- **Consequences:** `scheduledInRange` callers must pass explicit windows;
  never `SELECT *` unbounded into UI state.

## ADR-14 — Transaction boundaries + no destructive migration fallback
- **Decision:** (1) Multi-step writes run in explicit Room transactions at the
  owning boundary: `ActivityRepository.saveWithReminders` (activity +
  reminder-set replacement) and `deleteHard` (reminder cleanup + activity
  delete) via `db.withTransaction`; the cross-aggregate user reset lives in
  the single-purpose `UserDataReset` class. AlarmManager work stays outside
  transactions (non-transactional by nature; callers cancel before DB writes,
  launch-time resync re-arms). (2) `fallbackToDestructiveMigration()` removed
  with no replacement.
- **Context:** P0 audit proved REPLACE-upsert wiped history via cascades; P1
  audit found non-atomic save/delete/reset paths and an armed wipe-on-mismatch.
  Schema is version 1 since inception and the app is unreleased, so no
  historical schema exists to migrate — removing the fallback loses nothing
  and converts silent data loss into a loud startup failure.
- **Options:** Granular repo calls from ViewModels vs owned transactions;
  destructive fallback vs fail-fast vs invented migrations.
- **Chosen:** Owned transactions (proven by rollback/co-commit Robolectric
  tests); fail-fast with zero migrations (nothing to migrate yet).
- **Consequences:** Future schema changes must bump the version and add a real
  `Migration` + `MigrationTestHelper` data-preservation test; `upsert` stays
  single-row (last-writer-wins is benign there); no new DAO may use REPLACE on
  a row with FK children (see `ActivityDao` KDoc).

## ADR-15 — Onboarding art: supplied PNG/SVG sources, VectorDrawable output
- **Decision:** The five onboarding backgrounds are used exactly as supplied
  (`res/drawable/onboarding_bg_*.png`, rendered `ContentScale.Crop`
  edge-to-edge); the eight supplied SVGs are kept intact as sources in
  `artwork/onboarding/` and consumed as `res/drawable/onboarding_*_icon.xml`
  VectorDrawables. Raw `.svg` files must NOT live in `res/drawable`
  (breaks `mergeDebugResources`: "file name must end with .xml or .png").
- **Context:** The previous onboarding drew all scenery procedurally on Canvas
  and used Material icons. The revamp brief mandates the supplied artwork as
  source of truth — but verbatim conversion of five auto-traced icons
  (up to 2.1 MB SVG / 271 KB single path strings) hard-fails aapt2
  (`STRING_TOO_LARGE`), so the app cannot build.
- **Options:** Verbatim paths (does not build) vs high-res rasterization
  (banned by brief) vs new dependency for runtime SVG (banned without ADR,
  violates offline/simplicity rules) vs visually-lossless geometric
  optimization.
- **Chosen:** Optimization with a measured bound: cubic flattening (0.2u) +
  RDP (0.9u) on the 1095-unit viewBox, i.e. max deviation <1 unit ≈ 0.07dp
  at the 80dp display size (sub-pixel, invisible); subpath count, topology,
  fill-rule (`evenOdd`), fills, hairline strokes, aspect and transparency
  preserved; `onboarding_activity_icon` carried over verbatim with its linear
  gradients mapped to VectorDrawable `<gradient>` (minSdk 26); one
  full-bleed opaque near-white export-artifact rect dropped (would render as
  a white square); blur filters omitted (no VectorDrawable equivalent — glow
  comes from the glass composition). Converter: `artwork` SVG → Node script
  (opencode temp, rerunnable) → `res/drawable/*.xml`.
- **Consequences:** `res/drawable` keeps the recommended flat layout
  (5 PNG + 8 XML vectors); future icon refreshes re-run the conversion from
  `artwork/onboarding/`; no networking, no new dependencies, no persistence
  or navigation changes (onboarding still completes via
  `OnboardingViewModel.finish`).

## ADR-16 — Onboarding Continue crash: empty gradients + NaN paths; subject-aware cover
- **Root cause (crash):** two fatal defects in generated onboarding vectors,
  both triggered when page 2 composes (page 1 shows no vectors; Skip never
  composes page 2 — matching the report exactly). (1) The SVG→XML converter
  parsed `<stop>` attributes order-dependently while the supplied SVGs list
  `stop-opacity` before `stop-color`, so all 9 `<gradient>` blocks in
  `onboarding_activity_icon.xml` were emitted with zero `<item>` stops; an
  empty gradient throws during drawable inflation. (2) The same converter's
  coordinate reader consumed tokens off-by-one, shifting every pair and
  emitting `NaN` for every y in the five simplified icons; Compose's
  `PathParser` throws `IllegalArgumentException("Unknown command for: N")`.
- **Fix:** converter parses stops order-independently (all 9 gradients now
  carry their real 2 stops); reader fixed and all five icons regenerated
  (max string 27 KB, total deviation bound ≈ 2.2 viewport units ≈ 0.16dp at
  80dp — invisible); guarded by `OnboardingAssetsTest` (empty-gradient +
  Compose-parse checks for all 8 icons).
- **Background (over-crop):** `ContentScale.Crop` is kept — it is provably the
  minimum-magnification scale that still covers every edge (no bars, no
  distortion possible otherwise). Two changes: per-scene `subjectAlignment()`
  chosen from the inspected artwork so unavoidable crop on mismatched aspects
  sacrifices the least important region (e.g. BENEFITS protects the
  full-width orb over top clouds; FINAL protects the hiker side), and the
  parallax zoom capped 1.00–1.04 → 1.00–1.02. PNGs untouched.
- **Consequences:** no copy/flow/glass/animation/a11y/architecture changes;
  runtime page-by-page verification still requires a working device/emulator
  (the Pixel_8 AVD freezes mid-boot on this machine).

## ADR-17 — Onboarding WebP migration + Screen 2 floating cluster
- **Decision:** Backgrounds and icons are the Android-Studio-converted WebPs
  (`onboarding_0{1..5}_*`, `onboarding_*_icon`, plus renamed benefit set).
  `OnboardingBackgrounds` points at the WebP IDs with Crop, per-scene
  subject alignment, and parallax unchanged (verified identical dimensions:
  836×1881/836×1882/941×1672). Screen 2's rigid 2×2 grid became a centered
  asymmetric floating cluster (per-tile dp nudges, 80dp tiles, labels kept);
  copy/indicators/CTA/Skip untouched.
- **Context:** The brief's `onboarding_bg_*.webp` names do not exist on disk —
  actual files omit the `bg` infix; code follows the files. The benefit WebP
  `healthy_Habits_icon.webp` shipped with a capital H (aapt rejects it), so
  it was filesystem-renamed to `healthy_habits_icon.webp`, art bytes
  untouched. Screen 4/3 layouts are unchanged; only their drawable IDs were
  re-pointed (old vectors are gone, so the re-point is mandatory, not a
  redesign). The vector-era `OnboardingAssetsTest` was rewritten to guard
  the WebP contract (RIFF/WEBP magic, background dimensions, no stale
  PNG/XML/SVG, lowercase names).
- **Consequences:** APK shrank 29.6 → 21 MB. The old SVG sources stay in
  `artwork/onboarding/` for provenance. Visual sign-off of the Screen 2
  cluster needs a human on-device pass.

## ADR-18 — Twilight Journey: 4-screen onboarding + centralized palette
- **Decision:** (1) Onboarding is exactly 4 screens on the supplied
  `onboarding_screen_01..04` full-screen WebPs (Crop + per-scene subject
  alignment + capped parallax kept; no Compose overlays — screens 2/3 carry
  baked illustrations/cards, so overlaying would duplicate them). Copy is
  the brief's 4-screen set verbatim; the 5-screen copy, illustration
  composables, dead glass pieces, and 14 obsolete drawables are removed
  (files + code references, guarded by `OnboardingAssetsTest`). Skip,
  Continue, indicator (now driven by `Pages.size` = 4), persistence,
  animations, and accessibility are untouched. (2) Twilight palette
  centralized in `theme/Twilight.kt` (exact brief hexes + documented slot
  mapping) as a first-class gallery theme and the new default for fresh
  installs (persisted user choices are never migrated); shared primitives
  retuned centrally (`AppBackground` vertical depth; shared glass shadow
  0.12 → 0.10; onboarding glass cyan glow → golden whisper, copy text to
  Moonlight). No per-screen redesigns, no typography-scale changes.
- **Context:** Brief filename `onboarding_bg_*.webp` does not exist; code
  follows the on-disk `onboarding_screen_0X` names (852×1846 ×3, 950×1655).
  Per-screen content recoloring (charts, lists, dialogs) is deliberately
  deferred: centralized tokens already flow wherever screens read theme
  slots; touching every screen would violate the thin-slice rule.
- **Consequences:** APK 21 → 19.9 MB. Screens keep working on all 9 gallery
  themes; Twilight Light is warm paper for light-mode users. Remaining
  migration (charts, icon tints, empty states) is future work once the
  Twilight default is visually approved on-device.

## ADR-19 — Explicit ActivityType (HABIT/TASK) on the unified Activity
- **Decision:** One unified `Activity` model/table gains an explicit
  `ActivityType` discriminator (`HABIT`/`TASK`), persisted as
  `activities.activityType` (Room v2, `MIGRATION_1_2` with `DEFAULT 'HABIT'`).
  Recurrence stays a separate concept — type is never inferred from it
  (`HABIT + ONCE` and `TASK + DAILY` are both legal). No parallel
  Habit/Task entities, tables, repositories, or use-case stacks.
- **Context:** Slice 1 (`+ → Create Item → Habit / Task → Create`) needs the
  product distinction without forking the proven Activity pipeline
  (editor → `saveWithReminders()` → Room → Today/detail read-back).
- **Options:** Separate tables/stacks vs recurrence-inferred kind vs explicit
  discriminator.
- **Chosen:** Explicit discriminator — smallest change; v1 rows default to
  HABIT (the generic editor's schedule-first semantics); creation seeds
  Habit → DAILY default, Task → ONCE default, both still editable.
- **Consequences:** Task-specific behavior (no streaks, due-time, no-due-date)
  is intentionally deferred — Tasks currently render like habits. A future
  kind-dependent behavior change reads this column; no further migration.

## ADR-20 — Guided creation flow: habit intent + task due time (Room v3)
- **Decision:** (1) New `HabitIntent` discriminator (`BUILD`/`REDUCE`/`AVOID`)
  persisted as `activities.habitIntent` (`TEXT NOT NULL DEFAULT 'BUILD'`,
  Room v3, `MIGRATION_2_3`). (2) New nullable `activities.dueTime` (`HH:mm`,
  task-only display concept — reminders stay separate per ADR-12). (3) One
  unified `CreationViewModel` + one `Activity` model/table/repository: no
  Habit/Task tables, stacks, or duplicate VMs. (4) New routes
  `create/habit` (intent chooser) → `create/habit/config?intent=` →
  `create/task`; `edit?activityId=` loads the row and shows the
  kind-appropriate config with type/intent locked. The legacy
  `add?activityType=` route and the giant `ActivityEdit` form are removed.
- **Context:** The complete creation flow needs intent captured at creation
  and preserved on edit. Docs define two habit directions (Build vs
  Reduce/Avoid) with two tracking modes each; the flow exposes three intents
  with the mapping Reduce→Limit (stay at/below value+unit) and
  Avoid→Avoid-completely (zero, no amount, no daily check-in). Build
  Binary-vs-Target is derived from `target`/`unit` (Binary ⟺ 1.0 + blank),
  so no second column is needed. Task due time ("due at 8 PM") vs reminder
  ("nudge at 6 PM") are separate concepts per CREATE_ITEM_SPECS §4.4–4.5.
- **Options:** Separate Habit/Task entities vs intent column; second column
  for tracking mode vs derivation; due-time column vs deferral; separate
  Habit/Task VMs vs one VM.
- **Chosen:** Single intent column + derived tracking mode (smallest clean
  extension); nullable due-time column (display-only, no scheduler change);
  one VM branching on type/intent (creation and edit share the save
  pipeline: `saveWithReminders` + `scheduleForActivity`, unchanged).
- **Consequences:** Pre-v3 rows default to BUILD (schedule-first semantics,
  same reasoning as ADR-19); TASK rows store an ignored intent. "No due
  date" tasks map to startDate=today (true dateless tasks need an
  occurrence-model change — open). Repeating tasks run indefinitely (no end
  date). The v3 schema JSON is hand-written on a machine without JDK/Room
  (identity hash placeholder) — the next Android Studio/CI build regenerates
  the canonical hash; commit whatever it writes. Habit Templates
  (Sleep/Water/Exercise/Read/Meditate) remain future work: creation covers
  custom habits + tasks only. Avoid-streak/violation semantics are unchanged
  (completion infrastructure untouched by design).

## ADR-21 — Design System v1.0 foundation: two Aurora environments, Manrope, semantic tokens
- **Decision:** `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` is the visual source
  of truth. (1) The multi-theme gallery collapses to two first-class
  environments — Light Aurora / Dark Aurora — chosen by the existing
  `DarkModeSetting` (system / light / dark); `MomentumThemes` holds a single
  `"aurora"` entry and any legacy persisted `theme_id` resolves to it through
  `themeById` (no DataStore migration; Settings/gallery UI untouched this
  phase). Supersedes the multi-concept half of ADR-08; its token-class +
  CompositionLocal approach stands. (2) `MomentumColors` is rewritten to the
  v1 semantic contract (background/surface/soft/elevated/interactive, text
  tiers, accent + Aurora palette, success/warning/danger, borders, progress);
  the 14 pre-v1 slots (`primary`, `accent`, `error`, `glow`,
  `backgroundTop/Bottom`, …) survive as computed aliases so unmigrated
  screens keep compiling with no behavior change. (3) Manrope is bundled as a
  variable TTF (`app/src/main/res/font/manrope.ttf`, OFL-1.1 from
  google/fonts, fetched at build-authoring time and committed) driving the
  exact 7-token type scale across every Material typography slot — fully
  offline, no remote font loading. (4) New token files `MomentumShapes`,
  `MomentumElevation`, `MomentumMotion` (fast 180 / standard 280 / emphasis
  450 / reward 650 / ambient 6000 ms + easings/springs); legacy
  `MomentumRadii`, old `MomentumSpacing` names, and `MomentumDurations`
  delegate to the v1 tokens so there is one source of values. (5)
  `AuroraBackground(state, intensity, animated)` is the reusable Aurora
  primitive (REST/PROGRESS/COMPLETION/MILESTONE), drawn programmatically
  with cached radial-gradient brushes and disabled under reduced motion.
  (6) `MomentumIcons` establishes Material Symbols Rounded as the single
  icon family; the `momentumIcon(key)` habit-icon mapper is unchanged until
  its screen phase.
- **Context:** Phase 1 of the production UI redesign — foundation only.
  Individual screens must not be redesigned, and product behavior,
  navigation, and data layers must not change in this phase.
- **Consequences:** The 8 non-Aurora gallery palettes and `TwilightDark` /
  `TwilightLight` entries are deleted; `object Twilight` color constants
  remain because onboarding artwork references them. Settings' Theme row
  shows one entry until the Settings phase removes/reworks the gallery.
  `AppBackground` / `LiquidGlass` render with Aurora values through the
  legacy aliases.   Screen-by-screen migration proceeds against the design doc
  in later phases (Progress Hero, Activity Row, nav redesign, etc.). AGENTS
  §10's "no JDK on this machine" note is now stale — Gradle builds and unit
  tests run locally (verified: `gradlew :app:assembleDebug
  :app:testDebugUnitTest` BUILD SUCCESSFUL).

## ADR-22 — Phase 2A: locked bottom navigation (Home · Habits · Tasks · Insights) and Home redesign
- **Decision:** (1) Bottom navigation is exactly four destinations — Home
  (`today` route, relabelled from "Today"), Habits (`habits`), Tasks
  (`tasks`), Insights (`insights`) — with Material Symbols Rounded icons
  rendered outlined when inactive and filled when active (§8, §11). Calendar
  and Settings are removed from the bottom bar. (2) The former single
  "Activities" destination is replaced by Habits and Tasks: both are the
  existing `ActivitiesScreen` + `ActivitiesViewModel` pinned to an
  `ActivityType` filter and title — no parallel screens, no duplicated list
  logic (consistent with ADR-19's unified Activity). (3) Settings is opened
  from Home's top-right header action (§12); Calendar/history is opened from
  Insights' top-right action (§26). Both are pushed secondary routes with
  back affordances, so neither needs a tab. (4) The floating bottom bar now
  applies `WindowInsets.navigationBars` bottom padding itself — the M3
  `Scaffold` KDoc explicitly expects the bottom-bar slot to handle its own
  insets, and the custom glass bar previously had none. (5) Home is
  restructured as HomeHeader → TodayProgressHero → TodaySummary →
  TodayActivityList (rows = shared `ActivityCard`/Activity Row), over a
  state-responsive `AuroraBackground` (REST when empty/idle, PROGRESS once
  anything is done, COMPLETION at 100%; MILESTONE stays event-only). The
  progress hero uses the hero shape (28 dp) + HERO glass level; the empty
  state is one calm, action-oriented card with no streak-loss language.
- **Context:** Phase 2A of the production UI redesign (app shell + Home only)
  against `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md`. Product navigation is
  locked by the design system; the running app still had the legacy
  Today / Activities / Insights / Calendar / Settings bar. Habits, Tasks,
  Insights, Settings, and Create screens themselves are NOT redesigned in
  this phase — only navigated to.
- **Consequences:** `Routes.ACTIVITIES` is gone (nothing external referenced
  it); `Routes.HABITS`/`Routes.TASKS` are new. Room schema, repositories,
  use cases, engines, completion semantics, and reminder logic are untouched
  — this is a presentation/navigation change only. Settings and Calendar
  screens gained a back action because they are no longer tabs. A future
  Habits/Tasks screen redesign replaces the shared screen's innards, not the
  routes or type filter.

## ADR-23 — uiMode changes no longer recreate MainActivity (P0 nav-crash fix)
- **Decision:** `MainActivity` declares `android:configChanges="uiMode"` and
  re-applies `enableEdgeToEdge()` from `onConfigurationChanged`. Light/Dark
  changes update the running activity in place instead of recreating it.
  navigation-compose stays on 2.7.0.
- **Context:** Phase 2A verification reported
  `IllegalStateException: You cannot access the NavBackStackEntry's ViewModels
  after the NavBackStackEntry is destroyed` (around `MomentumNavGraph.kt:147`,
  i.e. destination content calling `momentumViewModel` → `viewModel()`) when
  the system uiMode toggled while navigating. Root cause is lifecycle, not
  navigation logic: a uiMode configuration change destroys `MainActivity`,
  which destroys every `NavBackStackEntry` and its `ViewModelStore` while the
  destination composition can still target those entries; `NavBackStackEntry
  .getViewModelStore` then throws (same class as the known issue in
  issuetracker 254645321 / accompanist #1487). Momentum is a pure-Compose
  activity with no night-qualified resources; theme already live-updates via
  `isSystemInDarkTheme()` + DataStore, so recreation buys nothing for uiMode.
  The edge-to-edge re-apply resolves status/nav-bar icon contrast against the
  new uiMode (the default `enableEdgeToEdge()` detection runs only at call
  time). No exception is caught or suppressed — the destroying lifecycle event
  is removed for this configuration.
- **Consequences:** Toggling Light/Dark mid-navigation keeps the nav stack,
  destination ViewModels, and in-flight work intact (also closes the window
  where a uiMode recreation could cancel an uncommitted completion toggle —
  see ADR-24). Rotation/locale/fontScale still recreate the activity as
  normal Android behavior. Upgrading nav-compose was considered and rejected:
  the crashing guard lives in `NavBackStackEntry` itself, the Compose runtime
  fixes for this crash class are already in BOM 2024.09.00, and no
  2.7.x/2.8.x release notes claim to fix config-change entry teardown — the
  app-specific trigger (recreation) is what this change removes.

## ADR-24 — Missing `MorningWalk` completion: no persistence bug (P1 finding, no code change)
- **Decision:** Completion flow left unchanged. The observed loss is not a
  persistence, ordering, or transaction defect; it is attributed to
  test-state contamination around the ADR-23 crash window and to intentional
  toggle semantics.
- **Context:** Traced end-to-end: UI (`CompletionButton` → `TodayViewModel/
  ActivityDetailViewModel/ActivitiesViewModel.toggle*`) →
  `ToggleCompletionUseCase` → `RoomCompletionRepository` → `CompletionDao`
  (`insertIgnore`/`updateValue`/`delete`, single atomic statements, unique on
  `(activityId, occurrenceDate)`). Committed rows survive process death and
  crashes (Room/SQLite WAL); the only cascade path is the confirmed
  `deleteHard`, and `setStatus`/archive/edit never touch
  `completion_records` (proved by `ActivityHistoryPreservedTest`). UI state is
  derived from Room flows, never optimistic, so a row the user saw completed
  must have been committed — and a committed row cannot vanish in a
  navigation crash. The realistic explanations for `MorningWalk`'s absence:
  (a) a second tap after the crash un-completed it — toggling is by design
  (`ToggleCompletionUseCaseTest: toggle flips state`); the Habits/Tasks list
  passes `completed = false` to `ActivityCard` (pre-existing display gap, not
  introduced in Phase 2A), so an already-completed habit renders unchecked
  there and a verification tap deletes the record; and/or (b) a toggle
  launched immediately before the crash had its `viewModelScope` cancelled
  before the insert committed — the write never happened and the UI never
  showed it complete. `DarkCheck` persisted because it was committed and not
  toggled again.
- **Consequences:** No schema or use-case change. The hardcoded
  `completed = false` on the Habits/Tasks list is recorded as a known display
  gap (showing real per-today completion there needs list-level completion
  data — a screens-phase change, deliberately out of scope here).

## ADR-25 — Restore path for archived activities (P2)
- **Decision:** Archived activities are restorable from two places, both via
  the existing `activityRepository.setStatus`: the detail screen's Actions
  card gains a **Restore** row (`Icons.Rounded.Unarchive`, shown only when
  `status == ARCHIVED`), and the list's Archived tab gains a Restore icon
  button next to Delete (`ActivitiesViewModel.restore`). Restore sets
  `status = ACTIVE` — the same state Resume uses — and reschedules reminders
  exactly like any transition to ACTIVE.
- **Context:** Archive was previously a one-way door in the UI (Delete was
  the only action on archived items) even though the persistence model is a
  reversible status column. Prior status is not stored, so restoring to
  ACTIVE (not PAUSED) is the defined behavior: archived items reappear on
  Today.
- **Consequences:** No schema migration, no new repository methods, no
  change to delete/archive semantics. Completions and reminders are untouched
  by restore (status-only UPDATE; regression-tested by
  `ActivityHistoryPreservedTest.archiving and restoring keeps completion
  history`). Delete remains confirmed and hard.

## ADR-26 — Phase 2B: dedicated Habits management screen + Habit Details
- **Decision:** (1) `Routes.HABITS` opens a dedicated `HabitsRoute` /
  `HabitsViewModel` management list (ACTIVE then Paused, no search/filter,
  no completion, overflow Pause/Resume/Delete) per
  `docs/product/features/Habits/HABITS_MANAGEMENT_DECISIONS.md` and the
  Soft Aurora UI spec. Tasks keeps the shared `ActivitiesScreen` type filter
  (ADR-22's shared list remains for Tasks). (2) `Routes.DETAIL` gains a
  one-shot `DetailTypeGate`: HABIT with status ACTIVE/PAUSED opens
  `HabitDetailRoute` / `HabitDetailViewModel` (weekly completion, streaks,
  bottom Pause/Edit/Delete group, no daily toggle); tasks and ARCHIVED habits
  keep the legacy `ActivityDetailRoute` body. (3) New domain use case
  `ComputeHabitOverviewUseCase` computes a status-independent 7-day week
  (honoring `SettingsRepository.weekStart`) with denominator = scheduled days
  this week (user decision: completed / scheduled, future scheduled days
  included; not-scheduled days never count against) plus current/longest
  streaks via `StreakEngine` from `startDate..today`. (4) Predefined icon
  lock after rename requires a persisted template id: Room schema **v4**
  adds nullable `activities.habitTemplateId` (`MIGRATION_3_4`); save stamps
  `CreationForm.templateId`; load prefers the column and falls back to
  `matchTemplate` for pre-v4 rows. Custom template ids also store and unlock
  the icon via `templateById(...).isCustom`.
- **Context:** Phase 2B product constraints forbid archive in the habit
  lifecycle UI (Active ↔ Paused → Delete), forbid daily completion on Habits
 /Details, and require a seven-day (not bar-chart) weekly view with an
  explicit not-scheduled state. Bottom nav stays Home · Habits · Tasks ·
  Insights (ADR-22); the UI spec §4 line `Home | Habits | Insights |
  Settings` conflicts with that lock and was not applied — flagged for
  product follow-up, not silently changed.
- **Consequences:** New packages `presentation/screens/habits` +
  `HabitDetail*`; `LiquidGlassButton` gains a `destructive` parameter;
  schema export `app/schemas/.../4.json` must be committed with the
  migration test (`Migration3To4Test`); `ActivitiesRoute` is now Tasks-only
  (and still reachable for unfiltered internal use). Weekly/streak logic is
  unit-tested (`ComputeHabitOverviewTest`); list/detail VMs use the
  Robolectric + real `AppContainer` pattern.

## ADR-27 — Phase 2B Soft Aurora visual correction pass (no behavior change)
- **Decision:** Restyle only the Phase 2B surfaces to the finalized Soft
  Aurora language without touching domain, lifecycle, completion ownership,
  weekly denominator, streak math, templates, Create Item, Home/Tasks/
  Insights/Settings, or bottom-nav architecture. (1) `HabitsScreen` gets a
  full-bleed `AuroraBackground(REST)`, title-only `MomentumTopBar`, 8dp
  list spacing, shared `HabitIconTile` with accent `habitIconAccent`, a
  three-slot `Frequency · Target · Reminder` metadata row (`—` for missing),
  and one floating 56dp circular Add Habit FAB (BottomEnd). (2)
  `HabitDetailScreen` gets centered Aurora hero (shared tile, status pill,
  custom-icon edit affordance via `templateById(...)?.isCustom`), three
  equal-height key-config meta cards, gradient `Check` weekly circles +
  ring incomplete + faint not-scheduled dot, amber `streak` fire tile, a
  read-only "Details" card (Direction · Tracking · Start · End · Notes),
  and 168dp LazyColumn bottom padding under the floating Pause/Edit/Delete
  group. (3) `LiquidGlassBottomBar`/`LiquidGlassTab` compact (6dp bar
  padding, 48dp min tab, 26dp icon, animated selection at
  `MomentumMotion.fast`). (4) `NavHost` crossfade: enter
  `fadeIn(200ms easeOut)`, exit `fadeOut(150ms easeInOut)`, `None` under
  reduced motion; bottom-bar inset uses `MomentumSpacing.compact`.
  Icons gain `"streak" -> LocalFireDepartment`.
- **Context:** UI spec + design-system polish pass for Habits/Details;
  prompt mandated a bottom-right FAB and no screenshots were available in
  context (text descriptions + code only).
- **Flagged conflicts (not silently resolved):** `HABITS_MANAGEMENT_DECISIONS.md`
  referenced by ADR-26 does not exist in the repo; UI spec §3 forbids a
  second FAB (header `+`) while this session's prompt requires one — prompt
  won; UI spec §7 shows a 🔥 streak glyph while the design system rejects
  emoji (we use the Material fire icon); UI spec §4 nav order still
  conflicts with ADR-22/26 lock (unchanged).
- **Consequences:** Shared `HabitIconTile` + `habitIconAccent` are the one
  icon language for list/detail; `HabitsViewModelTest` asserts the 3-slot
  metadata; verification `:app:assembleDebug :app:testDebugUnitTest` green —
  157 tests, 0 failures.

## ADR-28 — Habits header: collapsing hero on the bundled Aurora artwork
- **Decision:** The Habits screen's plain-text `MomentumTopBar` is replaced by
  a self-contained `HabitsHero` rendered from the two production Aurora
  illustrations supplied for this screen — `habits_hero_light.webp` /
  `habits_hero_dark.webp` (renamed from the delivered uppercase filenames to
  resource-safe lowercase) — selected by the active theme and drawn exactly as
  supplied: no tint, `ColorFilter`, recolor, or brightness change. There is no
  programmatic artwork; the first iteration's token-derived Canvas scene
  (sky/sun/ridges/lake/shore/trail) was removed completely, along with its
  drawing helpers and `smoothstep` color-window logic.
  Composition: the hero spans the full width with `ContentScale.Crop`,
  bottom-anchored, so the illustration's quiet lower third — where the centered
  title + description always sit, at every collapse height — stays pinned under
  the copy while the crop recedes upward as the header shrinks. An offscreen
  `BlendMode.DstIn` alpha mask dissolves the artwork's top and bottom edges
  into whatever is behind it (screen background + ambient Aurora), so the hero
  never ends on a hard edge and no tinted scrim rectangle is painted over the
  artwork. Both variants are built for their theme, so the copy uses theme ink
  (`textPrimary`/`textSecondary`) throughout — no color lerp, no mid-scroll
  color switch. That same mask is the readability treatment: the lower band
  fades toward the theme canvas exactly where the text sits.
  Scroll: the route owns ONE clamped px source of truth
  (`collapsePx ∈ [0, maxCollapsePx]`) behind a `NestedScrollConnection` —
  negative deltas consume into collapse pre-scroll (the list reflows under the
  shrinking header 1:1 with the finger), positive leftover at list-top
  restores post-scroll. The hero reports its measured range upward as a pure
  function of its text block — never of `progress` itself, which would feed
  back into progress and jitter mid-drag — and the route re-clamps
  `collapsePx` whenever that range changes (font-scale changes included). No
  Boolean expanded/collapsed states, no independent settle animations: drag,
  fling, and direction reversals are bounded arithmetic on the same value.
  Height lerps continuously with progress; typography stays on constant
  `headlineLarge`/`bodyMedium` slots (no font-size lerp, no relayout jumps).
  Under reduced motion the hero still has no time-based animation — height,
  crop, and dissolve keep tracking scroll 1:1 — and the one decorative
  interpolation (the text's hero inset) is frozen. §32's "prefer
  Compose-generated assets" guidance yields to supplied production artwork for
  this hero, matching the onboarding precedent for artwork the product
  mandates. The FAB, habit cards, list ordering, empty state, data/VM layers,
  and navigation are untouched; `MomentumTopBar` itself stays (other screens
  still use it).
- **Context:** Design iteration on the Phase 2B Habits screen against the
  final supplied Aurora illustrations (light/dark pair) and the collapsing-hero
  mock (expanded artwork → continuous scroll collapse → compact
  title/description header). The shell already pads routes via Scaffold
  `innerPadding`, so the hero needs no status-bar inset of its own; the top
  dissolve is what keeps the artwork from reading as a rectangle under the
  status bar. No new engine/use-case logic ships with this change, so unit
  tests are pure file checks: `HabitsHeroAssetsTest` guards the asset contract
  (both WebPs valid at native 1024×1536, resource-safe lowercase names, no
  legacy variants) and asserts `HabitsHero.kt` consumes them as images with no
  procedural-landscape drawing code left behind; previews cover the component
  contract (expanded/collapsed × light/dark + reduced motion).
- **Consequences:** `presentation/screens/habits/HabitsHero.kt` is an
  image-based hero; assets live at `res/drawable/habits_hero_light.webp` and
  `habits_hero_dark.webp`; `HabitsScreen.kt` route owns the collapse px state,
  its clamping, and the nested-scroll connection. The empty state has no
  scroll, so the hero stays expanded there (accepted). The UI spec §Header now
  documents the bundled artwork and drops the never-implemented header `+`
  line — creation entry remains the FAB; the §3 wording conflict was already
  flagged in ADR-27. Verification `:app:assembleDebug
  :app:testDebugUnitTest`.
## ADR-29 - Phase 2C: Home completion, collapsing Hero and Quick Add
- **Decision:**
  - **One completion rule, one engine.** `domain/engine/CompletionEngine.kt` is
    the only definition of "done": `actual >= target`, where target-based
    activity is derived (`targetPerOccurrence != 1.0 || unit.isNotBlank()`) and
    a default target with no unit is "Just Mark it Done". Home, detail screens,
    streaks and analytics all call it; there is no second copy in the UI layer
    and no `<=` "limit exceeded" branch. Reversal is the same rule evaluated
    against the new value, so both directions recalculate identically.
  - **Home derives sections in the domain layer.** `UseCases.kt` exposes
    `TodaySections` + `partitionTodayItems(items)`; `TodayUiState.Success`
    carries `sections` and the screen reads `state.remaining` / `state.completed`
    with derived `progress` / `doneCount` / `totalCount`. The UI never filters
    or counts completion itself (`HomeSectionsSourceGuardTest`).
  - **One scroll surface.** Header, Hero and both sections are items of a single
    `LazyColumn` with `contentPadding` horizontal 0 (the Hero is full-bleed) and
    per-row `padding(horizontal = screen)`; a clamped `collapsePx` behind a
    `NestedScrollConnection` drives the Hero, and `Modifier.animateItem` glides
    rows between sections so a completion relocates rather than disappears
    (`fadeInSpec`/`placementSpec` are nulled under reduced motion).
  - **Hero.** `domain/engine/HeroAtmosphere.kt` maps local time to
    DAWN/DAY/SUNSET/NIGHT (300/480/1020/1110 min) and waits to the next
    boundary; the four bundled `home_hero_{dawn,day,sunset,night}.webp` are
    drawn with `ContentScale.Crop` under a `DstIn` edge dissolve, a theme-tinted
    scrim (no glass plate) for copy readability, and a measured compact height
    (HabitsHero pattern, no fixed heights). The progress ring and its count are
    hidden when `totalCount == 0` so an empty day shows no misleading 0%; the
    artwork is `contentDescription = null`.

    > **Superseded in part by ADR-30.** The `HeroAtmosphere.kt` clock
    > engine and the four `home_hero_*` illustrations were deleted; the Home
    > Hero now draws the shared theme-selected artwork pair through
    > `MomentumHeroArtwork`. The ring, scrim, measured-height and dissolve
    > decisions in this bullet stand unchanged.
  - **Completed rows lose contrast, not text.** The first iteration dimmed the
    whole card with `alpha(0.6)`, which dropped body text to ~2.3:1 against the
    light theme surface (AGENTS §9 requires 4.5:1). The row now stays at full
    opacity and "quieter" is expressed with `GlassLevel.CONTROL` (0.45 fill)
    plus the success-tinted tile; section membership and the TalkBack action
    label are the non-colour completion signals.
  - **Accessibility is label-driven.** `completionActionLabel(activity,
    completed)` names the action with the activity's own title ("Complete
    Workout" / "Mark Workout incomplete"; target activities say they open the
    value dialog), both section headers carry `semantics { heading() }`, and
    `CompletionButton` / `ActivityCard` freeze their micro-interpolations under
    reduced motion while the state change stays visible.
  - **Quick Add replaces the in-list add row.** A 56 dp circular glass `+`
    (`QuickAddFab`, Home-owned) is anchored `BottomEnd` immediately above the
    bottom navigation and opens the existing `showCreateItem` Habit/Task
    chooser; the list reserves bottom content padding so the last row scrolls
    clear of it. The `"+ New activity"` list item was removed so ordering is
    exactly Hero -> Remaining today -> Completed today. The Scaffold itself
    gains no `floatingActionButton` - Quick Add belongs to Home only.
- **Context:** Phase 2C of `docs/product/features/dashboard/DASHBOARD_SPECS.md`
  against `docs/product/features/dashboard/DECISIONS.md`. The Hero needed the
  supplied time-of-day illustrations (ADR-28 precedent: product-mandated
  artwork wins over procedural art), and the row-grouping change had to keep
  the data-driven state model (§22) while fixing an accessibility defect found
  during Slice 6. No new dependencies; no networking, DI or module changes.
- **Consequences:** new/changed sources `CompletionEngine.kt`,
  `HeroAtmosphere.kt`, `HomeHero.kt`, `TargetEntryDialog.kt`,
  `TodayViewModel.kt`, `TodayScreen.kt`, `UseCases.kt`, `ActivityCard.kt`,
  `CompletionButton.kt`, `ActivityDetailScreen.kt`. Unit tests grew 180 -> 239
  (completion, scheduling + midnight re-evaluation guard, sections, atmosphere,
  hero assets/scroll surface, motion, Quick Add, accessibility) and the asset
  contract for the four hero WebPs is asserted as file checks.
  Verification `:app:assembleDebug :app:testDebugUnitTest` green - 239 tests,
  0 failures.

## ADR-30 - Phase 2C revision: one theme-selected hero artwork pair, opaque Reach a Target dialog
- **Decision:**
  - **One artwork pair, selected by the theme only.**
    `presentation/components/MomentumHeroArtwork.kt` exposes
    `momentumHeroArtworkRes(dark: Boolean)` and a single shared
    `@Composable MomentumHeroArtwork(...)`; both the Home Hero and the Habits
    header draw from it. Selection maps Light theme -> bundled
    `habits_hero_light.webp`, Dark theme -> `habits_hero_dark.webp`. The assets
    keep their original names and native 1024x1536 dimensions so the existing
    asset contract (`HabitsHeroAssetsTest`) still holds; only call sites
    changed, so nothing had to be renamed or duplicated.
  - **The clock is out of the artwork.** `domain/engine/HeroAtmosphere.kt`,
    `HeroAtmosphereTest` and the four `home_hero_{dawn,day,sunset,night}.webp`
    files are deleted. Time-aware copy stays where it belongs - the greeting in
    `TodayViewModel.greeting()`. Nothing in the shared artwork component nor in
    `HomeHero.kt` reads `LocalTime`, and both are guarded by file checks.
  - **The component owns the composition.** `ContentScale.Crop`,
    bottom-anchored alignment (the quiet lower third sits behind copy), an
    offscreen `DstIn` top/bottom dissolve pinned to the container bounds, and
    the `zoom` / `parallax` parameters applied *inside* the mask; artwork is
    `contentDescription = null`. Screens only pass dissolve bands and motion
    amounts, so Home and Habits cannot drift apart again.
  - **Home hero composition.** The greeting / time-of-day branch left
    `HomeHero.kt`; top dissolve is 20 dp and bottom 64 dp so no dead band opens
    under the greeting; scrim alphas are unchanged (0.86 dark / 0.60 light)
    because dropping them in the light theme pushes body text below the 4.5:1
    floor; the header and the hero became one `LazyColumn` item in
    `TodayScreen.kt`, which is what removes the bare-surface gap.
    > **Superseded in part by ADR-31.** Sharing one `LazyColumn` item was not
    > enough: the header was still a separate composable outside the artwork's
    > Box, and the 0.86/0.60 scrim erased the artwork everywhere it mattered, so
    > only a thin unscrimmed strip of Aurora survived. The Hero now owns the
    > header and content itself and the blanket scrim is replaced by a
    > copy-shaped veil. Artwork selection, the theme-only rule, the dissolve
    > mechanism and the artwork contract in this bullet stand.
  - **Reach a Target sits on an opaque surface.** New `GlassLevel.DIALOG`
    (`MomentumGlass.dialogFill = 1.0f`, `dialogElevation` 16 dp, `dialogScrim`
    0.45) plus an opaque top-stop branch in `liquidGlass`, so the top-edge
    streak is baked into `c.surface` (dark) or a white-lerped stop (light)
    instead of being painted translucent over the backdrop. Compose draws no
    dialog scrim of its own (b/159900354), so `TargetEntryDialog` dims the
    window behind it with `FLAG_DIM_BEHIND` +
    `window.setDimAmount(MomentumGlass.dialogScrim)`, obtained by casting
    `LocalView.current` to `DialogWindowProvider`. Only this dialog changes;
    `MomentumDialog` keeps its surface and is flagged for review.
- **Context:** Phase 2C visual QA on top of ADR-29. Four near-identical dark
  illustrations meant three quarters of the day showed artwork that did not
  match the active theme, Home opened with a band of bare surface between the
  greeting and the art, and the Reach a Target dialog was built on translucent
  Hero glass so the Home hero showed through the target input. Product
  direction is one Aurora world in two themes (Light/Dark), not four clock
  states, so dashboard spec sections 6, 6.1, 10.2 and dashboard decisions 7,
  25, 37 were rewritten together with the code. No new dependencies, no
  network, no DI or module change; ADR-28's "product-mandated artwork wins"
  precedent still holds - the artwork is unified, not replaced. ADR-29's Hero
  bullet is marked superseded in part rather than edited.
- **Consequences:** new `presentation/components/MomentumHeroArtwork.kt`;
  changed `HomeHero.kt`, `HabitsHero.kt`, `TodayScreen.kt`, `LiquidGlass.kt`,
  `Tokens.kt`, `TargetEntryDialog.kt`; deleted `HeroAtmosphere.kt`,
  `HeroAtmosphereTest.kt` and the four `home_hero_*` WebPs. No engine logic is
  left in the hero, so tests are pure file/function checks:
  `HomeHeroAssetsTest` (6) + `HomeScrollSurfaceTest` (3),
  `HabitsHeroAssetsTest` (3), `MomentumHeroArtworkTest` (4),
  `TargetEntryDialogSurfaceTest` (4). Unit tests now 244, 0 failures.
  Verification `:app:assembleDebug :app:testDebugUnitTest`.

## ADR-31 - Home Hero is one Aurora-backed surface (composition fix)
- **Decision:**
  - **The whole Home Hero is one surface.** `HomeHero` now owns *all* of the
    Hero's foreground — date, greeting, Settings, progress ring, completion
    count and motivational message — as children of a single clipped `Box`.
    `TodayScreen` no longer renders a separate `HomeHeader`; it passes
    `dateLabel`, `greeting` and `onOpenSettings` into the Hero. The artwork is
    the Box's **first** layer at `Modifier.fillMaxSize()`, so the Aurora is the
    Hero's background rather than a strip that begins under a header.
  - **Habits is the reference, and stays untouched.** `HabitsHero` is the
    already-correct composition (artwork first, copy above it, no plate) and
    keeps its own 44 dp / 88 dp dissolve bands and its un-veiled treatment.
    Home gets its own layout configuration inside the same shared artwork
    component; the two screens are explicitly not forced to share dimensions,
    and `HabitsHeroAssetsTest` now guards that.
  - **The blanket veil is gone.** Home previously painted a 0.86 (dark) / 0.60
    (light) full-width scrim behind the centred content box. Because the
    artwork survived only in the ~20 dp unscrimmed band at the Hero's top, the
    screen read as "header -> thin Aurora strip -> plain progress area", and
    the artwork was technically present while being visually absent. The
    replacement `HeroReadabilityVeil` is a single vertical gradient tinted with
    the page background whose stops follow the measured copy: strongest behind
    the header, dropping to 0.10 in the open band between greeting and ring,
    back up behind ring/count/message, transparent at the bottom edge. Peaks are
    asserted to stay <= 0.65 so the artwork can never be flattened again, and
    `0.86f` is asserted to be absent.
  - **Fades only at the Hero's own edges.** `TopDissolve` is 20 dp and sits
    entirely above the header copy (`HeroTopInset` 36 dp), because it is the
    blend where the artwork meets the page background above the Hero - the same
    mechanism the reference header uses. Nothing fades between the greeting and
    the ring. `BottomDissolve` is 72 dp and lands *after* the message
    (`HeroBottomInset` 72 dp), melting the artwork into the list.
  - **Hero geometry follows its content.** Compact height is
    `topInset + header + gap + content + bottomInset` from the two measured
    blocks, so the Hero is never clipped and dynamic type simply makes it
    taller; expanded is `max(screen * 0.46 clamped to 340..520 dp,
    compact + 88 dp)`. The veil's stops are computed from the same measurements
    plus the Hero's own height, so the readability shape tracks the content
    exactly instead of being a fixed rectangle. Scroll behaviour, the single
    `LazyColumn`, the collapse px contract and reduced-motion freezes are
    unchanged.
    > **Superseded in part by ADR-32.** The 36 dp top inset, the 20 dp top
    > ramp and the screen-percentage height floor each produced exactly the
    > blank band and dead space this ADR was meant to avoid. Home now uses a
    > 16 dp inset on the artwork, no top ramp, a flush top content padding, one
    > weighted spacer above the content, and a purely content-driven height.
    > The one-surface structure, the copy-shaped veil, the scroll behaviour and
    > the artwork contract in this bullet stand.
- **Context:** Phase 2C visual QA rejected the first ADR-30 composition. The
  defect was structural, not tonal: two adjacent regions (a header and a hero)
  plus a flattening scrim produced a screen whose Aurora read as a small strip
  instead of a Hero background. Habits had been correct all along precisely
  because its copy lives *inside* the artwork's Box. No new dependencies, no
  network, no data or domain changes; the artwork assets, the shared component,
  ADR-28's "product artwork wins" precedent and the reach-a-target dialog fix
  (ADR-30) are all untouched.
- **Consequences:** `HomeHero.kt` rewritten (one Box, three layers, new
  `HeroHeader` / `HeroContent` / `HeroReadabilityVeil`); `TodayScreen.kt` lost
  `HomeHeader` and its previews; `MomentumHeroArtwork` unchanged (Home passes its
  own bands). `build.gradle.kts` is back to its previous state - the layout
  contract is asserted by an **instrumented** bounds test
  (`app/src/androidTest/.../HomeHeroCompositionTest.kt`, run with
  `:app:connectedDebugAndroidTest`) because file checks cannot see geometry, and
  a JVM Robolectric Compose test was rejected: enabling
  `isIncludeAndroidResources` loads the real `MomentumApp` and broke six
  existing database tests. File tests grew to cover the new structure
  (`HomeHeroAssetsTest` 9, `HomeScrollSurfaceTest` 3, `HabitsHeroAssetsTest` 4);
  JVM suite 248 tests, 0 failures; `assembleDebug` and
  `compileDebugAndroidTestKotlin` green with no warnings.
  **Not verified on a device**: the emulator would not boot in the authoring
  environment (QEMU stalls before the guest starts under both
  `swiftshader_indirect` and `guest` GPU modes), so the visual pass in ADR-30's
  successor checklist is still owed a screenshot review in Android Studio.

## ADR-32 - Home Hero: artwork owns the top edge, Hero ends with its content
- **Decision:**
  - **No band above the date.** Home's artwork now starts at full strength on
    the Hero's own top edge. `TopDissolve` is `0.dp`, `HeroTopInset` dropped
    from 36 dp to 16 dp, and the scroll surface's top `contentPadding` is
    `0.dp` so nothing pushes the Hero down from the top of the surface. The
    inset now costs no Aurora, because it sits *on* the artwork: date, greeting
    and Settings all sit directly on Aurora. Previously the stack was
    16 dp padding + 36 dp inset + a 20 dp ramp, which is what produced the
    blank band above the greeting.
  - **No void below the message.** The Hero's Column has exactly one weighted
    spacer and it sits **above** the content block, so the Hero's extra height
    becomes the open Aurora band between greeting and ring while the message
    always ends `HeroBottomInset` (40 dp) above the bottom edge.
    `BottomDissolve` drops 72 dp -> 40 dp to match, so the ramp begins right
    under the message and the activity section continues straight out of it.
  - **Height is purely content-driven.** `expanded = compact + CollapseHeadroom`
    (72 dp); the `screenHeightDp * 0.46` clamp, `ExpandedFloor` and
    `ExpandedCeiling` are gone. A screen-percentage floor could only ever inflate
    the Hero with empty background, which is the dead space this ADR removes.
  - **The veil starts transparent.** `HeroReadabilityVeil`'s first stop is the
    page background at **zero** alpha, rising to the header strength only where
    the date begins. The artwork's top edge is therefore untouched - the band is
    fixed by geometry, not by darkening the Aurora - while the date, greeting,
    ring, count and message keep their contrast. The content block is located
    from the Hero's bottom edge (`h - bottomInset - content`), matching the new
    Column exactly.
  - **A zero dissolve band is now a supported contract.** `MomentumHeroArtwork`
    treats `topDissolve = 0.dp` as "no fade on this edge" instead of coercing it
    to a 5% visible minimum, and builds the ramp stops conditionally. Habits
    passes 44 dp / 88 dp and is therefore **bit-for-bit unchanged**; this is a
    per-screen parameter choice, not a change to the reference header, which
    keeps its own bands, composition, spacing, height and un-veiled treatment.
- **Context:** ADR-31 made the Home Hero structurally one surface, and the
  Aurora composition worked, but emulator review found two remaining visual
  defects: a blank band between the top of the Hero and the artwork, and a large
  empty region between the message and the activity section. Both came from
  values rather than architecture - a top inset plus a ramp plus list padding,
  and a centre-weighted column plus a screen-percentage height floor. No
  architecture, artwork, asset, Habits, dialog or data change is involved.
- **Consequences:** `HomeHero.kt` (geometry, constants, veil shape),
  `TodayScreen.kt` (top `contentPadding` 0), `MomentumHeroArtwork.kt` (honours a
  zero band). `HomeHeroAssetsTest` 9 -> 10, with the old "copy sits below the
  top ramp" test replaced by the actual visual contract - no top ramp, small top
  inset, message above a short bottom ramp, no weighted spacer below the
  content, no screen-percentage height - plus a new test that the scroll surface
  keeps the Hero flush; `MomentumHeroArtworkTest` 4 -> 5 for the zero-band
  contract; `HabitsHeroAssetsTest` still guards the unchanged Habits bands. The
  instrumented `HomeHeroCompositionTest` grew to 7 tests, adding bounds
  assertions that the date sits within 32 dp of the artwork's top edge and that
  no more than 56 dp follows the message. JVM suite 250 tests, 0 failures;
  `assembleDebug` and `compileDebugAndroidTestKotlin` green with no warnings.
  Still unverified on a device - see the open ROADMAP item; run
  `:app:connectedDebugAndroidTest` and screenshot Home in both themes.


## ADR-33 - Status bar: the two Hero routes draw behind it, everyone else keeps the inset
- **Decision:**
  - **The band was never a scrim.** `enableEdgeToEdge()` was already in place, so
    the status bar is transparent; what showed through it was `AppBackground`'s
    dark `backgroundTop`. The gap came from the navigation-level `Scaffold`, whose
    default `contentWindowInsets` (`ScaffoldDefaults.contentWindowInsets` =
    `WindowInsets.systemBarsForVisualComponents`) puts the status bar into
    `innerPadding.top`, and `Modifier.padding(innerPadding)` on the `NavHost`
    pushed every route below it. Nothing was drawn wrong - the Hero simply never
    reached the physical top of the window.
  - **Drop only the top half of that inset, and only for the Hero routes.**
    `MomentumNavGraph` now narrows the Scaffold's `contentWindowInsets` to
    `WindowInsets.systemBars.only(Bottom + Horizontal)` for `Routes.TODAY` and
    `Routes.HABITS` and leaves `ScaffoldDefaults.contentWindowInsets` everywhere
    else, so non-Hero screens keep their headers clear of the bar and the bottom
    nav keeps its inset on every route.
  - **Home is the one screen that gains an inset.** `HomeHero` prepends
    `topInset = WindowInsets.statusBars + HeroTopInset (16.dp)` to its Column and
    feeds the same value into `compactHeight` and the veil's `headerStart`, so the
    foreground is pixel-identical to before (measured: first text row 262 px before
    and after) while the artwork alone grows by the bar height. ADR-32's claim
    holds again - the date sits on Aurora, not on page background - because
    `statusBars` lands *inside* the artwork.
  - **Habits is untouched.** `HabitsHero.kt` did not change: artwork, 44 dp top
    dissolve, height, internal spacing, content positioning and composition are
    exactly as ADR-32 left them. The whole hero moves up by the status-bar height
    (title row 728 -> 592 px = 136 px = 51.8 dp on the Pixel_7 420 dpi emulator),
    which is all "artwork owns the top edge" can mean for a screen that does not
    opt into its own inset.
  - **No new scrim, dissolve, mask or top padding was added**, and no artwork
    asset, Hero crop, ring, message, Activity spacing, Target dialog, navigation
    structure or bottom-nav behaviour changed.
- **Context:** ADR-32's device pass was left open because the authoring
  environment's emulator would not boot. With a working AVD (QEMU stalls under
  `swiftshader_indirect`; `-gpu host` boots) the band reappeared in screenshots:
  rows 0..~135 px of both Home and Habits were `AppBackground`, because the
  window edge and the Hero edge were 51.8 dp apart. Status-bar icon appearance
  was verified correct in both themes - light icons over the dark Aurora, dark
  icons over the light Aurora, no overlay added.
- **Consequences:** `MomentumNavGraph.kt` (route-scoped `contentWindowInsets`)
  and `HomeHero.kt` (status-bar safe inset folded into `topInset`) are the whole
  diff; `HabitsHero.kt`, `TodayScreen.kt` and the artwork components are
  unchanged. Verified with four emulator screenshots - Home and Habits in light
  and dark - plus a scrolled Home (hero collapses with the artwork still filling
  the top, no band, no overlap). `HomeHeroCompositionTest` 7/7 green on device
  after two prerequisite fixes: backticked method names were renamed because D8
  rejects spaces in a `SimpleName` below DEX 040 (minSdk 26), which meant the
  suite could not even be dexed before this ADR; and the "date within 32 dp of the
  artwork's top edge" assertion was replaced by the actual edge-to-edge contract -
  `artwork.top <= 0.dp` and `statusBars <= dateGap <= statusBars + 32.dp` - since
  the date now legitimately sits `statusBars + HeroTopInset` (67.8 dp on the test
  device) below the artwork's top. JVM suite 250 tests, 0 failures; `assembleDebug`
  green. This closes the device pass left open at the end of ADR-32.
