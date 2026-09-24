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