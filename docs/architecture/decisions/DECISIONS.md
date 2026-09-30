# Momentum â€” Architectural Decisions (append-only)

> New decisions are new entries. Never silently edit old ones â€” supersede explicitly.

## ADR-01 â€” Jetpack Compose for UI
- **Decision:** Compose (Material 3) for all UI, single-activity (`MainActivity`).
- **Context:** Modern declarative UI, needed for expressive custom components/animation.
- **Options:** Views/XML vs Compose vs cross-platform (Flutter/RN).
- **Chosen:** Compose â€” native, M3 support, best animation story; team (AI+human) velocity.
- **Consequences:** Kotlin-only UI; previews replace layout editor; learn custom-draw for charts.

## ADR-02 â€” Room for structured persistence
- **Decision:** Room over raw SQLite / SQLDelight / manual files.
- **Context:** Relational data (activities â†” completions â†” reminders) with observed queries.
- **Options:** Room, SQLDelight, raw SQLite.
- **Chosen:** Room â€” first-class `Flow`, KSP, schema export, migration support, largest talent pool.
- **Consequences:** Must maintain entities, migrations, schema JSON in `app/schemas/`.

## ADR-03 â€” DataStore (Preferences) for settings
- **Decision:** DataStore Preferences for theme id, dark mode, week start, flags.
- **Context:** Small async key-value settings; SharedPreferences is legacy/blocking.
- **Options:** SharedPreferences vs DataStore.
- **Chosen:** DataStore â€” coroutine-native, type-safe, no main-thread blocking.
- **Consequences:** Async reads; repositories expose settings as `Flow`.

## ADR-04 â€” Clean Architecture + MVVM, single module
- **Decision:** UI â†’ VM â†’ UseCase â†’ Repository â†’ Room/DataStore; one `:app` module.
- **Context:** Need testability + AI-session navigability without build complexity.
- **Options:** Single vs multi-module; MVP/MVI vs MVVM.
- **Chosen:** Single-module Clean + MVVM â€” standard, boring, testable. Engines pure Kotlin.
- **Consequences:** Discipline required to keep layers honest; revisit modules only via ADR.

## ADR-05 â€” Offline-first, no backend
- **Decision:** Zero network dependency; all data on-device; no `INTERNET` permission.
- **Context:** Privacy product; core value (tracking) needs no server.
- **Options:** Local-only vs BaaS (Firebase/Supabase) vs custom backend.
- **Chosen:** Local-only. Future sync (if ever) is additive and needs its own ADR.
- **Consequences:** No auth/analytics/crash SDKs; backup = local export (later); Room is source of truth.

## ADR-06 â€” Recurrence as type-tagged rule, computed occurrences
- **Decision:** Store `recurrenceType` + JSON `recurrencePayload`; compute scheduled
  dates in `RecurrenceEngine`; never one DB row per future occurrence.
- **Context:** Recurring activities need unbounded schedules + editable rules + history truth.
- **Options:** (a) materialized occurrence rows, (b) iCal RRULE strings, (c) tagged rule + engine.
- **Chosen:** (c) â€” storage-efficient, editable, testable. RRULE rejected: over-general,
  poor monthly-clamp/DST fit, harder picker mapping.
- **Consequences:** Engine must be exhaustive + covered; recurrence edits apply forward
  (history immutable); new types need tag + engine + payload + tests + UI.

## ADR-07 â€” Notifications: AlarmManager + WorkManager, no FCM
- **Decision:** `AlarmManager` (exact fire) + `WorkManager` (resilient resync) +
  `BOOT_COMPLETED` receiver; notification actions incl. mark-complete.
- **Context:** One-time/recurring/multiple reminders, reboot recovery, offline.
- **Options:** AlarmManager-only vs WorkManager-only vs FCM (rejected: needs network/account).
- **Chosen:** Combination â€” exactness where the user asked for it, resilience everywhere.
- **Consequences:** Must handle exact-alarm permission deed (`SCHEDULE_EXACT_ALARM`),
  Doze, re-keying on activity edits; full implementation in Phase 9.

## ADR-08 â€” Data-driven theme architecture
- **Decision:** `MomentumColors` token class + CompositionLocal; theme registry keyed by id;
  selection persisted in DataStore; 12 launch concepts Ã— light/dark.
- **Context:** Themes are a headline feature, must be consistent + extensible.
- **Options:** Hardcoded per-screen colors vs M3 dynamic color vs token registry.
- **Chosen:** Token registry â€” full control of gradients/glow/chart colors M3 can't express.
- **Consequences:** No hardcoded colors in screens (lint by review); new theme = data entry + previews.

## ADR-09 â€” Manual DI via AppContainer
- **Decision:** Hand-written `AppContainer` in `MomentumApp`; no Hilt/Koin.
- **Context:** Small graph; DI frameworks add KSP/build cost + magic for little gain now.
- **Options:** Manual vs Hilt vs Koin.
- **Chosen:** Manual â€” explicit, zero deps, AI-friendly navigation.
- **Consequences:** Factories handwritten; migrate only if graph pain justifies it (new ADR).

## ADR-10 â€” Bootstrap toolchain (2026-09-10, this machine)
- **Decision:** Target `compileSdk/targetSdk 35`, `minSdk 26`; AGP `8.7.3`,
  Gradle `8.9`, Kotlin `2.0.21`, Compose BOM `2024.09.00`, JDK 17.
- **Context:** Bootstrap machine (Win11, no JDK/SDK/Gradle â€” Git only), so versions
  chosen from known-compatible stable sets, not local inspection.
- **Options:** Latest-alpha vs proven-stable trio.
- **Chosen:** Proven-stable trio above (AGP 8.7.x â†” Gradle 8.9 â†” JDK 17 verified).
- **Consequences:** First open in Android Studio provisions JDK 17 + SDK 35 +
  `gradle-wrapper.jar`; then run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest`.

## ADR-11 â€” Hand-written recurrence codec, no serialization library
- **Decision:** `RecurrenceCodec` encodes/decodes the type-tagged rule with
  hand-written builders/parsers instead of adding kotlinx-serialization.
- **Context:** Payloads are tiny (`days`, `interval`, `dayOfMonth`); a library
  adds size/build cost for one small job.
- **Options:** kotlinx-serialization vs `org.json` (Android-only, untestable on
  JVM) vs hand-written codec.
- **Chosen:** Hand-written â€” pure Kotlin, JVM-unit-testable, lenient by design
  (unknown future tags â†’ Daily, malformed payloads â†’ rule defaults).
- **Consequences:** Adding a recurrence type must extend the codec + tests.

## ADR-12 â€” Single reminder per activity in v1 UI
- **Decision:** The editor exposes one local reminder (time `HH:mm`); the
  `reminders` table already supports N rows per activity.
- **Context:** Spec asks for multiple reminders "where useful"; one covers the
  reminder use case simply while the scheduling architecture proves itself.
- **Options:** Full multi-reminder editor now vs single now, schema ready for N.
- **Chosen:** Single now â€” smaller UI, same scheduler/receiver/worker path.
- **Consequences:** Multi-reminder UI is additive later; no schema change needed.

## ADR-13 â€” Bounded completion windows for streak computation
- **Decision:** Today/Insights/Calendar read completions over trailing windows
  (730 days for Today streaks, 60 days for Insights, visible grid for Calendar)
  instead of unbounded full-history queries.
- **Context:** Streaks need history back to `startDate`, but unbounded queries
  into Compose state violate the performance budget.
- **Options:** Full-history observe vs bounded windows.
- **Chosen:** Bounded windows â€” streaks beyond 730 days display 730+; documented
  limitation, revisit only with measured need (paged history query).
- **Consequences:** `scheduledInRange` callers must pass explicit windows;
  never `SELECT *` unbounded into UI state.

## ADR-14 â€” Transaction boundaries + no destructive migration fallback
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
  historical schema exists to migrate â€” removing the fallback loses nothing
  and converts silent data loss into a loud startup failure.
- **Options:** Granular repo calls from ViewModels vs owned transactions;
  destructive fallback vs fail-fast vs invented migrations.
- **Chosen:** Owned transactions (proven by rollback/co-commit Robolectric
  tests); fail-fast with zero migrations (nothing to migrate yet).
- **Consequences:** Future schema changes must bump the version and add a real
  `Migration` + `MigrationTestHelper` data-preservation test; `upsert` stays
  single-row (last-writer-wins is benign there); no new DAO may use REPLACE on
  a row with FK children (see `ActivityDao` KDoc).

## ADR-15 â€” Onboarding art: supplied PNG/SVG sources, VectorDrawable output
- **Decision:** The five onboarding backgrounds are used exactly as supplied
  (`res/drawable/onboarding_bg_*.png`, rendered `ContentScale.Crop`
  edge-to-edge); the eight supplied SVGs are kept intact as sources in
  `artwork/onboarding/` and consumed as `res/drawable/onboarding_*_icon.xml`
  VectorDrawables. Raw `.svg` files must NOT live in `res/drawable`
  (breaks `mergeDebugResources`: "file name must end with .xml or .png").
- **Context:** The previous onboarding drew all scenery procedurally on Canvas
  and used Material icons. The revamp brief mandates the supplied artwork as
  source of truth â€” but verbatim conversion of five auto-traced icons
  (up to 2.1 MB SVG / 271 KB single path strings) hard-fails aapt2
  (`STRING_TOO_LARGE`), so the app cannot build.
- **Options:** Verbatim paths (does not build) vs high-res rasterization
  (banned by brief) vs new dependency for runtime SVG (banned without ADR,
  violates offline/simplicity rules) vs visually-lossless geometric
  optimization.
- **Chosen:** Optimization with a measured bound: cubic flattening (0.2u) +
  RDP (0.9u) on the 1095-unit viewBox, i.e. max deviation <1 unit â‰ˆ 0.07dp
  at the 80dp display size (sub-pixel, invisible); subpath count, topology,
  fill-rule (`evenOdd`), fills, hairline strokes, aspect and transparency
  preserved; `onboarding_activity_icon` carried over verbatim with its linear
  gradients mapped to VectorDrawable `<gradient>` (minSdk 26); one
  full-bleed opaque near-white export-artifact rect dropped (would render as
  a white square); blur filters omitted (no VectorDrawable equivalent â€” glow
  comes from the glass composition). Converter: `artwork` SVG â†’ Node script
  (opencode temp, rerunnable) â†’ `res/drawable/*.xml`.
- **Consequences:** `res/drawable` keeps the recommended flat layout
  (5 PNG + 8 XML vectors); future icon refreshes re-run the conversion from
  `artwork/onboarding/`; no networking, no new dependencies, no persistence
  or navigation changes (onboarding still completes via
  `OnboardingViewModel.finish`).

## ADR-16 â€” Onboarding Continue crash: empty gradients + NaN paths; subject-aware cover
- **Root cause (crash):** two fatal defects in generated onboarding vectors,
  both triggered when page 2 composes (page 1 shows no vectors; Skip never
  composes page 2 â€” matching the report exactly). (1) The SVGâ†’XML converter
  parsed `<stop>` attributes order-dependently while the supplied SVGs list
  `stop-opacity` before `stop-color`, so all 9 `<gradient>` blocks in
  `onboarding_activity_icon.xml` were emitted with zero `<item>` stops; an
  empty gradient throws during drawable inflation. (2) The same converter's
  coordinate reader consumed tokens off-by-one, shifting every pair and
  emitting `NaN` for every y in the five simplified icons; Compose's
  `PathParser` throws `IllegalArgumentException("Unknown command for: N")`.
- **Fix:** converter parses stops order-independently (all 9 gradients now
  carry their real 2 stops); reader fixed and all five icons regenerated
  (max string 27 KB, total deviation bound â‰ˆ 2.2 viewport units â‰ˆ 0.16dp at
  80dp â€” invisible); guarded by `OnboardingAssetsTest` (empty-gradient +
  Compose-parse checks for all 8 icons).
- **Background (over-crop):** `ContentScale.Crop` is kept â€” it is provably the
  minimum-magnification scale that still covers every edge (no bars, no
  distortion possible otherwise). Two changes: per-scene `subjectAlignment()`
  chosen from the inspected artwork so unavoidable crop on mismatched aspects
  sacrifices the least important region (e.g. BENEFITS protects the
  full-width orb over top clouds; FINAL protects the hiker side), and the
  parallax zoom capped 1.00â€“1.04 â†’ 1.00â€“1.02. PNGs untouched.
- **Consequences:** no copy/flow/glass/animation/a11y/architecture changes;
  runtime page-by-page verification still requires a working device/emulator
  (the Pixel_8 AVD freezes mid-boot on this machine).

## ADR-17 â€” Onboarding WebP migration + Screen 2 floating cluster
- **Decision:** Backgrounds and icons are the Android-Studio-converted WebPs
  (`onboarding_0{1..5}_*`, `onboarding_*_icon`, plus renamed benefit set).
  `OnboardingBackgrounds` points at the WebP IDs with Crop, per-scene
  subject alignment, and parallax unchanged (verified identical dimensions:
  836Ã—1881/836Ã—1882/941Ã—1672). Screen 2's rigid 2Ã—2 grid became a centered
  asymmetric floating cluster (per-tile dp nudges, 80dp tiles, labels kept);
  copy/indicators/CTA/Skip untouched.
- **Context:** The brief's `onboarding_bg_*.webp` names do not exist on disk â€”
  actual files omit the `bg` infix; code follows the files. The benefit WebP
  `healthy_Habits_icon.webp` shipped with a capital H (aapt rejects it), so
  it was filesystem-renamed to `healthy_habits_icon.webp`, art bytes
  untouched. Screen 4/3 layouts are unchanged; only their drawable IDs were
  re-pointed (old vectors are gone, so the re-point is mandatory, not a
  redesign). The vector-era `OnboardingAssetsTest` was rewritten to guard
  the WebP contract (RIFF/WEBP magic, background dimensions, no stale
  PNG/XML/SVG, lowercase names).
- **Consequences:** APK shrank 29.6 â†’ 21 MB. The old SVG sources stay in
  `artwork/onboarding/` for provenance. Visual sign-off of the Screen 2
  cluster needs a human on-device pass.

## ADR-18 â€” Twilight Journey: 4-screen onboarding + centralized palette
- **Decision:** (1) Onboarding is exactly 4 screens on the supplied
  `onboarding_screen_01..04` full-screen WebPs (Crop + per-scene subject
  alignment + capped parallax kept; no Compose overlays â€” screens 2/3 carry
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
  0.12 â†’ 0.10; onboarding glass cyan glow â†’ golden whisper, copy text to
  Moonlight). No per-screen redesigns, no typography-scale changes.
- **Context:** Brief filename `onboarding_bg_*.webp` does not exist; code
  follows the on-disk `onboarding_screen_0X` names (852Ã—1846 Ã—3, 950Ã—1655).
  Per-screen content recoloring (charts, lists, dialogs) is deliberately
  deferred: centralized tokens already flow wherever screens read theme
  slots; touching every screen would violate the thin-slice rule.
- **Consequences:** APK 21 â†’ 19.9 MB. Screens keep working on all 9 gallery
  themes; Twilight Light is warm paper for light-mode users. Remaining
  migration (charts, icon tints, empty states) is future work once the
  Twilight default is visually approved on-device.

## ADR-19 â€” Explicit ActivityType (HABIT/TASK) on the unified Activity
- **Decision:** One unified `Activity` model/table gains an explicit
  `ActivityType` discriminator (`HABIT`/`TASK`), persisted as
  `activities.activityType` (Room v2, `MIGRATION_1_2` with `DEFAULT 'HABIT'`).
  Recurrence stays a separate concept â€” type is never inferred from it
  (`HABIT + ONCE` and `TASK + DAILY` are both legal). No parallel
  Habit/Task entities, tables, repositories, or use-case stacks.
- **Context:** Slice 1 (`+ â†’ Create Item â†’ Habit / Task â†’ Create`) needs the
  product distinction without forking the proven Activity pipeline
  (editor â†’ `saveWithReminders()` â†’ Room â†’ Today/detail read-back).
- **Options:** Separate tables/stacks vs recurrence-inferred kind vs explicit
  discriminator.
- **Chosen:** Explicit discriminator â€” smallest change; v1 rows default to
  HABIT (the generic editor's schedule-first semantics); creation seeds
  Habit â†’ DAILY default, Task â†’ ONCE default, both still editable.
- **Consequences:** Task-specific behavior (no streaks, due-time, no-due-date)
  is intentionally deferred â€” Tasks currently render like habits. A future
  kind-dependent behavior change reads this column; no further migration.

## ADR-20 â€” Guided creation flow: habit intent + task due time (Room v3)
- **Decision:** (1) New `HabitIntent` discriminator (`BUILD`/`REDUCE`/`AVOID`)
  persisted as `activities.habitIntent` (`TEXT NOT NULL DEFAULT 'BUILD'`,
  Room v3, `MIGRATION_2_3`). (2) New nullable `activities.dueTime` (`HH:mm`,
  task-only display concept â€” reminders stay separate per ADR-12). (3) One
  unified `CreationViewModel` + one `Activity` model/table/repository: no
  Habit/Task tables, stacks, or duplicate VMs. (4) New routes
  `create/habit` (intent chooser) â†’ `create/habit/config?intent=` â†’
  `create/task`; `edit?activityId=` loads the row and shows the
  kind-appropriate config with type/intent locked. The legacy
  `add?activityType=` route and the giant `ActivityEdit` form are removed.
- **Context:** The complete creation flow needs intent captured at creation
  and preserved on edit. Docs define two habit directions (Build vs
  Reduce/Avoid) with two tracking modes each; the flow exposes three intents
  with the mapping Reduceâ†’Limit (stay at/below value+unit) and
  Avoidâ†’Avoid-completely (zero, no amount, no daily check-in). Build
  Binary-vs-Target is derived from `target`/`unit` (Binary âŸº 1.0 + blank),
  so no second column is needed. Task due time ("due at 8 PM") vs reminder
  ("nudge at 6 PM") are separate concepts per CREATE_ITEM_SPECS Â§4.4â€“4.5.
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
  occurrence-model change â€” open). Repeating tasks run indefinitely (no end
  date). The v3 schema JSON is hand-written on a machine without JDK/Room
  (identity hash placeholder) â€” the next Android Studio/CI build regenerates
  the canonical hash; commit whatever it writes. Habit Templates
  (Sleep/Water/Exercise/Read/Meditate) remain future work: creation covers
  custom habits + tasks only. Avoid-streak/violation semantics are unchanged
  (completion infrastructure untouched by design).

## ADR-21 â€” Design System v1.0 foundation: two Aurora environments, Manrope, semantic tokens
- **Decision:** `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` is the visual source
  of truth. (1) The multi-theme gallery collapses to two first-class
  environments â€” Light Aurora / Dark Aurora â€” chosen by the existing
  `DarkModeSetting` (system / light / dark); `MomentumThemes` holds a single
  `"aurora"` entry and any legacy persisted `theme_id` resolves to it through
  `themeById` (no DataStore migration; Settings/gallery UI untouched this
  phase). Supersedes the multi-concept half of ADR-08; its token-class +
  CompositionLocal approach stands. (2) `MomentumColors` is rewritten to the
  v1 semantic contract (background/surface/soft/elevated/interactive, text
  tiers, accent + Aurora palette, success/warning/danger, borders, progress);
  the 14 pre-v1 slots (`primary`, `accent`, `error`, `glow`,
  `backgroundTop/Bottom`, â€¦) survive as computed aliases so unmigrated
  screens keep compiling with no behavior change. (3) Manrope is bundled as a
  variable TTF (`app/src/main/res/font/manrope.ttf`, OFL-1.1 from
  google/fonts, fetched at build-authoring time and committed) driving the
  exact 7-token type scale across every Material typography slot â€” fully
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
- **Context:** Phase 1 of the production UI redesign â€” foundation only.
  Individual screens must not be redesigned, and product behavior,
  navigation, and data layers must not change in this phase.
- **Consequences:** The 8 non-Aurora gallery palettes and `TwilightDark` /
  `TwilightLight` entries are deleted; `object Twilight` color constants
  remain because onboarding artwork references them. Settings' Theme row
  shows one entry until the Settings phase removes/reworks the gallery.
  `AppBackground` / `LiquidGlass` render with Aurora values through the
  legacy aliases.   Screen-by-screen migration proceeds against the design doc
  in later phases (Progress Hero, Activity Row, nav redesign, etc.). AGENTS
  Â§10's "no JDK on this machine" note is now stale â€” Gradle builds and unit
  tests run locally (verified: `gradlew :app:assembleDebug
  :app:testDebugUnitTest` BUILD SUCCESSFUL).

## ADR-22 â€” Phase 2A: locked bottom navigation (Home Â· Habits Â· Tasks Â· Insights) and Home redesign
- **Decision:** (1) Bottom navigation is exactly four destinations â€” Home
  (`today` route, relabelled from "Today"), Habits (`habits`), Tasks
  (`tasks`), Insights (`insights`) â€” with Material Symbols Rounded icons
  rendered outlined when inactive and filled when active (Â§8, Â§11). Calendar
  and Settings are removed from the bottom bar. (2) The former single
  "Activities" destination is replaced by Habits and Tasks: both are the
  existing `ActivitiesScreen` + `ActivitiesViewModel` pinned to an
  `ActivityType` filter and title â€” no parallel screens, no duplicated list
  logic (consistent with ADR-19's unified Activity). (3) Settings is opened
  from Home's top-right header action (Â§12); Calendar/history is opened from
  Insights' top-right action (Â§26). Both are pushed secondary routes with
  back affordances, so neither needs a tab. (4) The floating bottom bar now
  applies `WindowInsets.navigationBars` bottom padding itself â€” the M3
  `Scaffold` KDoc explicitly expects the bottom-bar slot to handle its own
  insets, and the custom glass bar previously had none. (5) Home is
  restructured as HomeHeader â†’ TodayProgressHero â†’ TodaySummary â†’
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
  this phase â€” only navigated to.
- **Consequences:** `Routes.ACTIVITIES` is gone (nothing external referenced
  it); `Routes.HABITS`/`Routes.TASKS` are new. Room schema, repositories,
  use cases, engines, completion semantics, and reminder logic are untouched
  â€” this is a presentation/navigation change only. Settings and Calendar
  screens gained a back action because they are no longer tabs. A future
  Habits/Tasks screen redesign replaces the shared screen's innards, not the
  routes or type filter.

## ADR-23 â€” uiMode changes no longer recreate MainActivity (P0 nav-crash fix)
- **Decision:** `MainActivity` declares `android:configChanges="uiMode"` and
  re-applies `enableEdgeToEdge()` from `onConfigurationChanged`. Light/Dark
  changes update the running activity in place instead of recreating it.
  navigation-compose stays on 2.7.0.
- **Context:** Phase 2A verification reported
  `IllegalStateException: You cannot access the NavBackStackEntry's ViewModels
  after the NavBackStackEntry is destroyed` (around `MomentumNavGraph.kt:147`,
  i.e. destination content calling `momentumViewModel` â†’ `viewModel()`) when
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
  time). No exception is caught or suppressed â€” the destroying lifecycle event
  is removed for this configuration.
- **Consequences:** Toggling Light/Dark mid-navigation keeps the nav stack,
  destination ViewModels, and in-flight work intact (also closes the window
  where a uiMode recreation could cancel an uncommitted completion toggle â€”
  see ADR-24). Rotation/locale/fontScale still recreate the activity as
  normal Android behavior. Upgrading nav-compose was considered and rejected:
  the crashing guard lives in `NavBackStackEntry` itself, the Compose runtime
  fixes for this crash class are already in BOM 2024.09.00, and no
  2.7.x/2.8.x release notes claim to fix config-change entry teardown â€” the
  app-specific trigger (recreation) is what this change removes.

## ADR-24 â€” Missing `MorningWalk` completion: no persistence bug (P1 finding, no code change)
- **Decision:** Completion flow left unchanged. The observed loss is not a
  persistence, ordering, or transaction defect; it is attributed to
  test-state contamination around the ADR-23 crash window and to intentional
  toggle semantics.
- **Context:** Traced end-to-end: UI (`CompletionButton` â†’ `TodayViewModel/
  ActivityDetailViewModel/ActivitiesViewModel.toggle*`) â†’
  `ToggleCompletionUseCase` â†’ `RoomCompletionRepository` â†’ `CompletionDao`
  (`insertIgnore`/`updateValue`/`delete`, single atomic statements, unique on
  `(activityId, occurrenceDate)`). Committed rows survive process death and
  crashes (Room/SQLite WAL); the only cascade path is the confirmed
  `deleteHard`, and `setStatus`/archive/edit never touch
  `completion_records` (proved by `ActivityHistoryPreservedTest`). UI state is
  derived from Room flows, never optimistic, so a row the user saw completed
  must have been committed â€” and a committed row cannot vanish in a
  navigation crash. The realistic explanations for `MorningWalk`'s absence:
  (a) a second tap after the crash un-completed it â€” toggling is by design
  (`ToggleCompletionUseCaseTest: toggle flips state`); the Habits/Tasks list
  passes `completed = false` to `ActivityCard` (pre-existing display gap, not
  introduced in Phase 2A), so an already-completed habit renders unchecked
  there and a verification tap deletes the record; and/or (b) a toggle
  launched immediately before the crash had its `viewModelScope` cancelled
  before the insert committed â€” the write never happened and the UI never
  showed it complete. `DarkCheck` persisted because it was committed and not
  toggled again.
- **Consequences:** No schema or use-case change. The hardcoded
  `completed = false` on the Habits/Tasks list is recorded as a known display
  gap (showing real per-today completion there needs list-level completion
  data â€” a screens-phase change, deliberately out of scope here).

## ADR-25 â€” Restore path for archived activities (P2)
- **Decision:** Archived activities are restorable from two places, both via
  the existing `activityRepository.setStatus`: the detail screen's Actions
  card gains a **Restore** row (`Icons.Rounded.Unarchive`, shown only when
  `status == ARCHIVED`), and the list's Archived tab gains a Restore icon
  button next to Delete (`ActivitiesViewModel.restore`). Restore sets
  `status = ACTIVE` â€” the same state Resume uses â€” and reschedules reminders
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

## ADR-26 â€” Phase 2B: dedicated Habits management screen + Habit Details
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
  lifecycle UI (Active â†” Paused â†’ Delete), forbid daily completion on Habits
 /Details, and require a seven-day (not bar-chart) weekly view with an
  explicit not-scheduled state. Bottom nav stays Home Â· Habits Â· Tasks Â·
  Insights (ADR-22); the UI spec Â§4 line `Home | Habits | Insights |
  Settings` conflicts with that lock and was not applied â€” flagged for
  product follow-up, not silently changed.
- **Consequences:** New packages `presentation/screens/habits` +
  `HabitDetail*`; `LiquidGlassButton` gains a `destructive` parameter;
  schema export `app/schemas/.../4.json` must be committed with the
  migration test (`Migration3To4Test`); `ActivitiesRoute` is now Tasks-only
  (and still reachable for unfiltered internal use). Weekly/streak logic is
  unit-tested (`ComputeHabitOverviewTest`); list/detail VMs use the
  Robolectric + real `AppContainer` pattern.

## ADR-27 â€” Phase 2B Soft Aurora visual correction pass (no behavior change)
- **Decision:** Restyle only the Phase 2B surfaces to the finalized Soft
  Aurora language without touching domain, lifecycle, completion ownership,
  weekly denominator, streak math, templates, Create Item, Home/Tasks/
  Insights/Settings, or bottom-nav architecture. (1) `HabitsScreen` gets a
  full-bleed `AuroraBackground(REST)`, title-only `MomentumTopBar`, 8dp
  list spacing, shared `HabitIconTile` with accent `habitIconAccent`, a
  three-slot `Frequency Â· Target Â· Reminder` metadata row (`â€”` for missing),
  and one floating 56dp circular Add Habit FAB (BottomEnd). (2)
  `HabitDetailScreen` gets centered Aurora hero (shared tile, status pill,
  custom-icon edit affordance via `templateById(...)?.isCustom`), three
  equal-height key-config meta cards, gradient `Check` weekly circles +
  ring incomplete + faint not-scheduled dot, amber `streak` fire tile, a
  read-only "Details" card (Direction Â· Tracking Â· Start Â· End Â· Notes),
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
  referenced by ADR-26 does not exist in the repo; UI spec Â§3 forbids a
  second FAB (header `+`) while this session's prompt requires one â€” prompt
  won; UI spec Â§7 shows a ðŸ”¥ streak glyph while the design system rejects
  emoji (we use the Material fire icon); UI spec Â§4 nav order still
  conflicts with ADR-22/26 lock (unchanged).
- **Consequences:** Shared `HabitIconTile` + `habitIconAccent` are the one
  icon language for list/detail; `HabitsViewModelTest` asserts the 3-slot
  metadata; verification `:app:assembleDebug :app:testDebugUnitTest` green â€”
  157 tests, 0 failures.

## ADR-28 â€” Habits header: collapsing hero on the bundled Aurora artwork
- **Decision:** The Habits screen's plain-text `MomentumTopBar` is replaced by
  a self-contained `HabitsHero` rendered from the two production Aurora
  illustrations supplied for this screen â€” `habits_hero_light.webp` /
  `habits_hero_dark.webp` (renamed from the delivered uppercase filenames to
  resource-safe lowercase) â€” selected by the active theme and drawn exactly as
  supplied: no tint, `ColorFilter`, recolor, or brightness change. There is no
  programmatic artwork; the first iteration's token-derived Canvas scene
  (sky/sun/ridges/lake/shore/trail) was removed completely, along with its
  drawing helpers and `smoothstep` color-window logic.
  Composition: the hero spans the full width with `ContentScale.Crop`,
  bottom-anchored, so the illustration's quiet lower third â€” where the centered
  title + description always sit, at every collapse height â€” stays pinned under
  the copy while the crop recedes upward as the header shrinks. An offscreen
  `BlendMode.DstIn` alpha mask dissolves the artwork's top and bottom edges
  into whatever is behind it (screen background + ambient Aurora), so the hero
  never ends on a hard edge and no tinted scrim rectangle is painted over the
  artwork. Both variants are built for their theme, so the copy uses theme ink
  (`textPrimary`/`textSecondary`) throughout â€” no color lerp, no mid-scroll
  color switch. That same mask is the readability treatment: the lower band
  fades toward the theme canvas exactly where the text sits.
  Scroll: the route owns ONE clamped px source of truth
  (`collapsePx âˆˆ [0, maxCollapsePx]`) behind a `NestedScrollConnection` â€”
  negative deltas consume into collapse pre-scroll (the list reflows under the
  shrinking header 1:1 with the finger), positive leftover at list-top
  restores post-scroll. The hero reports its measured range upward as a pure
  function of its text block â€” never of `progress` itself, which would feed
  back into progress and jitter mid-drag â€” and the route re-clamps
  `collapsePx` whenever that range changes (font-scale changes included). No
  Boolean expanded/collapsed states, no independent settle animations: drag,
  fling, and direction reversals are bounded arithmetic on the same value.
  Height lerps continuously with progress; typography stays on constant
  `headlineLarge`/`bodyMedium` slots (no font-size lerp, no relayout jumps).
  Under reduced motion the hero still has no time-based animation â€” height,
  crop, and dissolve keep tracking scroll 1:1 â€” and the one decorative
  interpolation (the text's hero inset) is frozen. Â§32's "prefer
  Compose-generated assets" guidance yields to supplied production artwork for
  this hero, matching the onboarding precedent for artwork the product
  mandates. The FAB, habit cards, list ordering, empty state, data/VM layers,
  and navigation are untouched; `MomentumTopBar` itself stays (other screens
  still use it).
- **Context:** Design iteration on the Phase 2B Habits screen against the
  final supplied Aurora illustrations (light/dark pair) and the collapsing-hero
  mock (expanded artwork â†’ continuous scroll collapse â†’ compact
  title/description header). The shell already pads routes via Scaffold
  `innerPadding`, so the hero needs no status-bar inset of its own; the top
  dissolve is what keeps the artwork from reading as a rectangle under the
  status bar. No new engine/use-case logic ships with this change, so unit
  tests are pure file checks: `HabitsHeroAssetsTest` guards the asset contract
  (both WebPs valid at native 1024Ã—1536, resource-safe lowercase names, no
  legacy variants) and asserts `HabitsHero.kt` consumes them as images with no
  procedural-landscape drawing code left behind; previews cover the component
  contract (expanded/collapsed Ã— light/dark + reduced motion).
- **Consequences:** `presentation/screens/habits/HabitsHero.kt` is an
  image-based hero; assets live at `res/drawable/habits_hero_light.webp` and
  `habits_hero_dark.webp`; `HabitsScreen.kt` route owns the collapse px state,
  its clamping, and the nested-scroll connection. The empty state has no
  scroll, so the hero stays expanded there (accepted). The UI spec Â§Header now
  documents the bundled artwork and drops the never-implemented header `+`
  line â€” creation entry remains the FAB; the Â§3 wording conflict was already
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
    light theme surface (AGENTS Â§9 requires 4.5:1). The row now stays at full
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
  the data-driven state model (Â§22) while fixing an accessibility defect found
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
    Hero's foreground â€” date, greeting, Settings, progress ring, completion
    count and motivational message â€” as children of a single clipped `Box`.
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


## ADR-34 - Phase 2C polish: an empty day says nothing, the artwork hands over seamlessly, one scroll signal composes the Hero, and the ring carries a travelling sheen
- **Decision:**
  - **An empty day carries no line at all.** `TodayViewModel.messageFor` returns
    `""` when `total == 0` instead of "A fresh page - add your first activity.".
    Nothing to report, nothing to say: the No-Activity card below the Hero is the
    only copy, so the artwork runs straight into an explicit empty state and the
    last remaining text-over-artwork case disappears.
  - **The artwork reaches zero *before* the Hero's bottom edge.** The Hero draws
    an eased alpha profile into `MomentumHeroArtwork`'s existing offscreen layer
    (`DstIn`, so the artwork is multiplied, never covered): flat at full strength
    until `BottomBlendStart` (72 dp) above the edge, then 8 samples of an S-curve
    to zero with a flat `BottomBlendTail` (16 dp) still to go. The readability
    veil clears over the same tail. The artwork's own linear `BottomDissolve`
    (40 dp) is taken over, not competed with. Both profiles are flat at their ends
    so the join has no slope kink, and because alpha is already zero at the edge
    the Hero's last strip is literally the ambient page background that continues
    below it - measured: largest adjacent-row step across the Hero bottom (y=654)
    is inside the +/-3 noise band, against the ~5/row step this replaced.
  - **One scroll signal composes the collapsed Hero, and nothing else moves.**
    `collapseProgress` -> `p` -> `travel` drives the ring's translate+scale and the
    description's translate+lift together, so they always travel as one unit.
    Geometry stays a pure function of *measured* blocks: `contentWidth`,
    `descriptionWidth` and `descriptionHeight` are reported through
    `onSizeChanged` and are independent of `p`; no height is driven by `p`
    (that would be a scroll feedback loop), only `graphicsLayer` translations.
    Side-by-side is gated by `MinSideBySideGap` (16 dp) of measured clearance -
    below it the description keeps its own row instead of crowding the ring -
    and the lift lands the description on the ring's centre line. Under reduced
    motion `travel` is 0, so the collapsed state is reached by layout, not by
    animation.
  - **The ring's sheen is the arc colour lifted toward white, not `glow`.**
    `glow`, `accentPrimary`, `progressFill` and `primary` are all `auroraBlue`, so
    painting `glow` over the arc was painting the arc on itself - measured
    172 -> 172 luminance. The travelling band is now
    `lerp(primary, White, 0.62)` at 0.42 peak alpha, a ~130 deg smooth band that
    makes one linear revolution every `MomentumMotion.ambient` (6 s). It is
    rendered by rotating the canvas by the spot angle while counter-shifting the
    arc's local `startAngle`, so the *geometry* stays exactly on the completed
    sweep and the *gradient* moves - nothing spills onto the unfinished track.
    The brush is remembered (ShaderBrush caches its shader), so the loop
    allocates nothing per frame; it is not composed at 0% or under reduced motion.
  - **Flagged conflict:** lifting the description onto the ring's centre line
    leaves artwork below the copy in the collapsed state, which relaxes ADR-32's
    literal "the Hero ends with its content". The Phase 2C composition brief wins
    for this session; ADR-32's wording should be read as "the Hero's content
    block is the last thing it *lays out*", not a prohibition on the artwork
    continuing underneath at p > 0.
- **Context:** Phase 2C asked for the four finishing moves on Home: no artwork
  text on an empty day, no visible artwork-to-content seam, a collapse that reads
  as one composed transformation rather than two parts sliding, and a ring that
  looks alive. The seam was measured first: the previous alpha profile reached
  zero exactly at the Hero's bottom edge with non-zero slope, so the row average
  jumped at the boundary. The sheen was designed against the theme and only
  measured afterwards, which is how the `glow == primary` identity was caught.
- **Consequences:** `HomeHero.kt` (blend constants, artwork `drawWithCache`
  mask, veil tail, measured block sizes, collapse composition, empty preview),
  `TodayViewModel.kt` (`messageFor`), `ProgressOrb.kt` (`rememberGlowPhase`,
  `glowBrush`, the rotated draw). `MomentumHeroArtwork.kt` and `HabitsHero.kt`
  are untouched. `HomeHeroAssetsTest` still forbids procedural-artwork tokens in
  the Hero (its `smoothstep` guard forced the blend's comment to say "S-curve" -
  the guard stays, the code does not draw artwork). JVM suite 250 tests, 0
  failures; `assembleDebug` green. Verified on the Pixel_7 AVD: collapse
  interpolates continuously (ring x-centre 540 -> 338 -> 259 -> 179 px over
  0/120/180/300 px swipes) and reverses to byte-identical bounds; sheen peak
  travels 345 deg -> 180 deg -> 0 deg across 2 s frames at 100%, sits only inside
  the 0..180 deg half at 50% (peak 192 vs base 172), is absent at 0% and is
  uniformly absent when `animator_duration_scale = 0`. Device notes: a minimized
  emulator window starves `screencap` (frames tens of minutes stale while
  `uiautomator dump` stays current) - restore the window before trusting pixels.

## ADR-35 - Phase 2C second polish pass: the content block is rigid, the veil is one sampled profile, foreground is masked in screen space, and the ring glow breathes
- **Decision:**
  - **The content block no longer lifts, travels or flips side-by-side.**
    Supersedes ADR-34's "one scroll signal composes the collapsed Hero" (the
    `travel`/`descriptionLiftPx`/`MinSideBySideGap` scheme). Ring, "N of M
    completed" and the message now scroll 1:1 with the Hero; collapse is height
    compression plus ring scale, nothing else. The lift was the source of the
    ring/description overlap and of the two parts sliding against each other, and
    because no geometry is driven by `p` anymore there is nothing left to keep in
    sync - the gap between greeting and ring is layout, not an animated offset.
  - **The ring is laid out in a reserved box and scaled inside it.**
    `RingBounds = HeroRingSize * (1f + RingScaleHeadroom)` (~108 dp) with
    `graphicsLayer { scaleX/scaleY = ringScale }` on `ProgressOrb`, so the scale
    can never eat the `MomentumSpacing.tight` gap. Measured collapsed gap
    (greeting bottom -> ring top): 158 px ~ 60 dp, against the ~130 dp this pass
    started from.
  - **The readability veil is sampled once into 48 monotone stops.** Anchors are
    derived first and provably ordered (`rise <= hold <= dip1 <= dip2 <=
    contentTop <= contentEnd <= clearAt`), the header/content valley depth is
    proportional to the live band width squared (a fully collapsed ~20 dp band
    degenerates to a plain eased rise - no ghost dip), and the profile is
    evaluated through one shared `ease01` S-curve into `VeilSteps` stops. The
    artwork's bottom dissolve uses the same S-curve over 24 samples ending on the
    same `BottomBlendTail`, so artwork and veil die on one profile at one
    boundary: measured adjacent-row step across the Hero-bottom zone is below one
    brightness unit per 4 px, all larger deltas are copy rows.
  - **Foreground never enters the cutout: one screen-space DstIn mask, no band.**
    The copy layer renders offscreen and is masked with a gradient whose stops
    come from the *live* status-bar inset relative to the Hero's
    `positionInRoot().y`: transparent through the status-bar band, ramping over
    `HeroTopInset` (16 dp), opaque below; a no-op branch while the Hero is below
    the band, a full-transparent branch once it is above it. Artwork and veil are
    deliberately unmasked, so ADR-33's edge-to-edge top is untouched - the mask
    hides copy, never art. Measured: with the date's box scrolled to y=103 the
    date box contains 0 dark pixels (light theme) and the greeting ramps in;
    same behaviour in dark.
  - **The Habits compact hero bottom-anchors below the status bar.** Its
    `compactHeight` now starts at `WindowInsets.statusBars` + 16 dp, so fast
    scroll pins the title to exactly `statusBars + HeroTopInset` (measured
    `[427,178]` on the AVD) with 0 foreground pixels in the cutout zone in both
    themes. The expanded floor (300 dp) and the artwork are unchanged.
  - **The ring glow is a breathing halo plus a two-lobe orbiting sheen.**
    Supersedes ADR-34's single travelling sheen. One `glowPhase` drives a full
    circle halo stroke (alpha `GlowHaloBase +- GlowHaloBreath`, width x1.9) and
    the rotation of a primary+secondary lobe brush (both lifted 0.62 toward
    white, stops first == last), drawn with the same rotate + counter-shifted
    `startAngle` mechanism over the unchanged sweep geometry; brush remembered,
    no per-frame allocation, composed only when `glowEnabled && sweep > 0.001`.
    Under reduced motion (in-app toggle + `animator_duration_scale = 0`) the ring
    is pixel-static: 0 px differ across 3 frames 1.5 s apart; after restoring,
    15.9k px differ per 1.5 s again.
- **Context:** The second observation pass on Home + Habits reported seven
  problems: artwork/content seam, the huge gap under the collapsed Hero,
  non-uniform scroll motion, ring/description overlap, foreground entering the
  camera cutout on both routes, weak dark/light artwork coherence, and a ring
  glow that did not read as alive. Verification this pass was done on the AVD by
  pairing every `uiautomator dump` with a screenshot and asserting the pixels
  against the dump bounds, because raw frames taken during input churn lag by
  seconds.
- **Consequences:** `HomeHero.kt` (rigid `HeroContent`, `RingBounds`,
  sampled veil, `ease01`, `BottomBlendSteps`, screen-space mask via
  `onGloballyPositioned` + `drawWithContent`), `HabitsHero.kt` (status-bar inset
  in `compactHeight`), `ProgressOrb.kt` (`GlowHalo*`, `GlowTau`, two-lobe brush);
  `MomentumHeroArtwork.kt` untouched (it already owns its `DstIn` edge).
  Contract tests unchanged and green: `./gradlew :app:testDebugUnitTest` 250
  tests, 0 failures; `:app:connectedDebugAndroidTest` HomeHeroCompositionTest 7
  tests, 0 failures; `assembleDebug` green. Verified on the Pixel_7 AVD across
  both themes: collapse/scroll/reverse pairs restore rest bounds exactly;
  glow pixel-diff 8.5k-16.9k px per 1.4 s at 50%/100% and 0 at 0% or reduced
  motion; glow states captured at 0/25/50/100%. Device notes: enabling the
  cutout emulation overlay (`cmd overlay enable
  com.android.internal.display.cutout.emulation.emu01`) is what keeps the status
  bar at 136 px - without it the system reports 74 px and   the mask band shrinks
  with it; if taps die while `dumpsys window` still shows focus, the
  InputDispatcher desync needs `adb root; adb shell stop; adb shell start`.

## ADR-36 â€” `knowledge/` is the authoritative source of truth; `docs/` is supporting context
- **Decision:**
  - **`knowledge/` is authoritative** for implementation-relevant product
    rules, business rules, design decisions and architectural constraints.
  - **`docs/` is demoted** to supporting detail, history and context. An agent
    MUST NOT treat a `docs/` document as authoritative over `knowledge/`, and
    `docs/` MUST NOT become a second source of truth for a rule `knowledge/`
    states.
  - **`knowledge/index.md` is a routing map only** â€” TASK -> DOMAIN -> KNOW-\*
    ID -> file + section. It MUST NOT restate chunk content.
  - **`knowledge/` is HUMAN-MAINTAINED.** An agent MUST NOT silently rewrite,
    regenerate or re-summarize a chunk from `docs/` or any other artifact, and
    MUST NOT author missing knowledge to unblock itself. Missing knowledge is
    reported to the Orchestrator, never invented.
  - **Conflicts are recorded and escalated, never silently resolved.** A chunk
    spanning two sides of a conflict carries a `> **CONFLICT (C-nn):**` block
    naming source A, source B, the subject, and
    `Status: ESCALATED â€” unresolved by design`. The block never states a
    resolution.
  - **Propagation is user-initiated.** An agent may propagate a knowledge change
    into `docs/`, into an ADR record, or into an index only when the user
    explicitly asks and names the changed `KNOW-*` ID. Propagation is never a
    side effect of another task.
  - **No content is deleted from `docs/`.** This ADR adds authority, it does not
    move or remove documents.
  - **Structure.** 7 directories, 20 topic files, 53 chunks. The retrieval unit
    is a **chunk** â€” a logical section inside a topic file addressed by a stable
    `KNOW-*` ID. The ID is the retrieval key; the file is only the container.
  - **Chunk template** (mandatory): `## KNOW-<FAMILY>[-<TOPIC>]-<NNN> <Title>`,
    authoritative content faithful to the `Source:`, `- **When required:**`
    (mandatory), `- **Source:**` (mandatory), `- **Related:**` (mandatory where
    siblings exist).
  - **Granularity rule** (all six must hold, else FAIL): one independently
    answerable question per chunk; actionable when read alone with no sibling
    chunk and no `docs/` file open; 30â€“120 content lines, hard ceiling 160, hard
    floor 12; at most 12 chunks per file; exactly one evaluable `When required`
    trigger; faithful to the source â€” no requirement weakened into a vague
    summary, and no assertion the source does not support.
  - **ID scheme.** Families stay `ARCH`, `BUSINESS`, `UI`, `DATA`, `OFFLINE`,
    `TEST`, `FEAT`. The 20 pre-migration IDs
    (`KNOW-ARCH-001`..`004`, `KNOW-BUSINESS-001`..`003`, `KNOW-UI-001`..`003`,
    `KNOW-DATA-001`..`003`, `KNOW-OFFLINE-001`..`003`, `KNOW-TEST-001`,
    `KNOW-FEAT-001`..`003`) are **permanent legacy IDs and MUST NOT be
    renamed** â€” they are already referenced by `task.md` handoffs and by
    `docs/development/MULTI_AGENT_SYSTEM.md` Â§19. New chunks use the sub-family
    form `KNOW-<FAMILY>-<TOPIC>-<NNN>`, whose `TOPIC` token maps 1:1 onto the
    chunk's home file, so the ID alone tells an agent which file to open.
    Retention policy: extend a valid existing ID in place; on a split the
    retained half keeps the legacy ID and the new half gets a sub-family ID; a
    fully-subsumed ID is merged onto the surviving chunk and retained there.
    Renaming and deletion of IDs are not permitted.
  - **`docs/architecture/decisions/DECISIONS.md` is NOT migrated.** It remains the
    append-only canonical ADR record, owned solely by the Architect per
    `AGENTS.md` Â§14.15. `knowledge/architecture/adr-index.md` carries only the
    path, the append-only rule, the ownership rule, the `grep`-by-number
    retrieval instruction, and a pointer to this ADR. No ADR text is copied into
    `knowledge/`.
  - **Brainstorming notes are NOT migrated.** `docs/product/brainstorming_notes/`
    documents *rejected* options and must not sit on the authoritative retrieval
    path. It remains the provenance of record for the Home and Create Item
    chunks.
  - **Phase status is NOT migrated.** `docs/product/ROADMAP.md`,
    `PROJECT_STATUS.md` and `ISSUES.md` are status, not knowledge.
- **Context:** `AGENTS.md` Â§14.2 (pre-existing, uncommitted) declared `docs/`
  "remains the authoritative home" and forbade moving or duplicating content
  into `knowledge/`, which is the exact inverse of the user-directed authority
  model this ADR records. Phase 0 found `knowledge/` holding a single 56-line
  pointer table of 20 `KNOW-*` IDs and **zero** chunk files, while `docs/`
  held 23 files: 11 referenced, and 12 implementation-relevant sources â€” including
  37 Home product decisions, 26 Habits product constraints, 7 Create Item
  decisions, and 19 of the design system's 35 sections â€” **unreachable** through
  the index. The gap was a reachability failure, not a content failure: the
  knowledge existed but no agent could route to it. Separately, the
  `knowledge/` concept was itself incoherent â€” `knowledge/index.md` pointed into
  `docs/`, so `knowledge/` was not a source of truth at all, only a lookup table
  over documents that were the real authority.
- **Options:**
  1. Keep `docs/` authoritative, invest only in a better index.
  2. Point `knowledge/index.md` at `docs/` sections and accept `grep` over large
     documents as the retrieval mechanism.
  3. Make `knowledge/` authoritative, chunked and human-maintained, with `docs/`
     demoted to context and conflicts escalated rather than resolved.
- **Chosen:** Option 3. Option 1 and 2 both leave the failure in place: agents
  keep reading 500â€“1500-line documents, the un-referenced 12 sources stay
  unreachable, and nothing states which source wins when two disagree. Option 3
  makes authority explicit, makes retrieval addressable, and makes the
  human-maintained boundary enforceable â€” an agent is structurally unable to
  silently rewrite a rule it is supposed to be following.
- **Consequences:**
  - `AGENTS.md` Â§14.2 and Â§14.8 must be amended to match this ADR. The exact
    replacement text is recorded in `task.md` Â§7.7. `AGENTS.md` is pre-existing
    dirty (+441 lines, all of Â§14) and every one of those lines must survive;
    only the two replacements may be applied. The Architect cannot perform this
    amendment â€” `AGENTS.md` is outside its write grant.
  - Mirrored knowledge-authority text in
    `docs/architecture/MULTI_AGENT_PERMISSIONS.md` (Â§3.1, Â§9) and
    `docs/development/MULTI_AGENT_SYSTEM.md` (Â§19, Â§35) requires the same
    amendment. `docs/architecture/MULTI_AGENT_PERMISSIONS.md` Â§3.1 currently
    states `knowledge/**` is read-only for **all four** agents, which is the
    mechanical cause of the TASK-005 permission blocker. That is a permission
    change and therefore a **human decision**; this ADR does not grant it.
  - Self-declared source-of-truth claims in
    `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` Â§1 and in ADR-21 are superseded
    for rule text. Amending them is user-initiated propagation, not migration.
  - Twelve conflicts (C-01â€¦C-12) are recorded and **left unresolved** in
    `task.md` Â§7.9, including bottom-navigation composition, the v4 Activity
    field set vs. the product spec, soft-delete vs. hard-delete semantics, the
    streak ðŸ”¥ example that contradicts the locked design system, and the
    programmatic-vs-bundled Aurora asset strategy. Affected chunks carry the
    conflict block until a human rules.
  - Two stale `docs/` duplicates remain in the tree pending a user hygiene
    decision: `docs/product/prototype/PRODUCT_SPEC_V0.md` is byte-identical to
    `docs/product/PRODUCT_SPEC.md`, and
    `docs/product/prototype/PROTOTYPE_ROADMAP.md` is a stale prefix of
    `docs/product/ROADMAP.md`.
  - Migration must be non-destructive: `git diff` over `docs/` shows only the
    two authorized `AGENTS.md` replacements plus user-authorized back-references.
    The migration reads `docs/`; it never writes it.
  - **The dominant risk is transcription drift**, not structure: 53 chunks drawn
    from an 826-line locked design system and a 532-line feature spec is a large
    manual transcription. The control is the `task.md` Â§14 migration audit, which
    must demonstrate that no implementation-relevant item was lost or weakened.
  - The `KNOW-*` ID space becomes durable public identifiers. Once a chunk ID is
    referenced by an agent prompt, a `task.md` handoff, or an ADR, it is frozen
    in the same way an ADR number is.

## ADR-37 - Hero artwork is full-strength through the status-bar inset; caller dissolves start below it

- **Decision:**
  - **One rule for both Hero routes.** `MomentumHeroArtwork` renders at full
    strength from its container's top edge **through the status-bar inset**; a
    caller-supplied `topDissolve` band begins **below** that inset and completes
    inside the Hero. The component reads `WindowInsets.statusBars` itself.
    Callers keep passing only `topDissolve`/`bottomDissolve` amounts, so ADR-30's
    contract ("screens only pass dissolve bands and motion amounts") is unchanged
    - only the origin of the top band moves.
  - **The shared system-bar contract is restated as four rules** (ADR-33 is
    confirmed, not altered): (1) `enableEdgeToEdge()` lives in `MainActivity`
    (`onCreate` + `onConfigurationChanged`) and themes carry no `statusBarColor`;
    (2) `MomentumNavGraph.drawsBehindStatusBar` narrows the Scaffold's
    `contentWindowInsets` to `systemBars.only(Bottom + Horizontal)` for
    `Routes.TODAY` / `Routes.HABITS` only; (3) foreground copy inside a Hero
    takes the status-bar inset itself (Home `HeroTopInset`, Habits `statusBars`
    in `compactHeight` - ADR-35) and no screen ever pushes *artwork* down with
    padding, offset or scrim; (4) artwork is full-strength through the bar and any
    dissolve starts below it (this ADR).
  - **Home needs no change, and that is the finding for ISSUE-016.** `HomeHero`
    passes `TopDissolve = 0.dp`. On the current tree, measured on the Pixel_7 AVD
    in light and dark, empty and populated, at rest and scrolled, cutout overlay
    on and off: Home `y=0` is artwork (`AFBBFD` light, `1C207A` dark, `95A5F9`
    scrolled), the date box is `[53,178]` = `statusBars(136 px) + HeroTopInset(42
    px)`, and row means across seven edge columns show no discontinuity other
    than the status-bar icons. ISSUE-016 is therefore **not reproducible on this
    tree**; rules 1-4 are what keep it that way, and `HomeHeroCompositionTest`'s
    `artwork.top <= 0.dp` stays as the regression guard.
  - **Habits is fixed inside the shared component, not in `HabitsHero`.** ADR-33
    moved the whole Habits Hero up by the bar height while its `TopDissolve =
    44.dp` stayed measured from the container top, so the 115 px band landed
    *inside* the 136 px status-bar zone: measured rows `0..~115` read `DCE9F8`
    (approximately `AppBackground #F7F8FC`) with full artwork only from
    `y ~ 120` - the reported pale band (ISSUE-017). Moving the band origin below
    `statusBars` restores the exact pre-ADR-33 on-screen geometry (dissolve
    `136..251 px`), i.e. ADR-28/ADR-32's Habits appearance, with `HabitsHero.kt`
    untouched.
- **Context:** TASK-006 (ISSUE-015 - ISSUE-019) reported a blank band above the
  Home Hero and the same regression on Habits. Static inspection showed the
  configuration (`MainActivity.enableEdgeToEdge`, route-scoped
  `contentWindowInsets`, `TodayScreen` `contentPadding.top = 0.dp`) is correct;
  device measurement confirmed Home and isolated the defect to the dissolve band
  that the shared component measures from the container top. Only two screens
  call `MomentumHeroArtwork` (`HomeHero`, `HabitsHero`), so the rule can live in
  the component with no collateral callers.
- **Consequences:** `MomentumHeroArtwork.kt` gains a status-bar-aware top-dissolve
  origin; `HabitsHero.kt`, `HomeHero.kt`, `MomentumNavGraph.kt`, `MainActivity.kt`
  and the artwork assets are unchanged by this decision. No new scrim, mask or
  fixed offset is introduced, and ADR-30/31/32/33/35 are not superseded.
  Verification: Habits in both themes must show full-strength artwork at `y=0`
  with the dissolve starting at `statusBars`; Home must be pixel-unchanged.

## ADR-38 - Reach a Target: window dim raised to 0.72, field outline made visible; panel stays opaque

- **Decision:**
  - **The dialog panel stays opaque.** `MomentumGlass.dialogFill = 1.0f`,
    `GlassLevel.DIALOG` and `dialogElevation = 16.dp` are unchanged, and the
    `liquidGlass` top-stop branch keeps the sheet solid (verified on device:
    `TargetEntryDialogSurfaceTest` and pixel sampling both show an opaque panel).
  - **`MomentumGlass.dialogScrim` is raised from `0.45f` to `0.72f`.** This
    amends the value ADR-30 recorded alongside its opaque-dialog decision; the
    decision itself (opaque panel + dim) stands. The dim is applied through the
    existing `window.setDimAmount(MomentumGlass.dialogScrim)` path - no new
    scrim composable, no per-screen hack.
  - **The target field gains a visible outline** using semantic tokens (a real
    `outline`/`primary` alpha, not a ghost border), and the Cancel/Save controls
    keep their existing 48 dp targets. No redesign, no new component, no change
    to the dialog's glass fill or elevation.
- **Context:** ISSUE-019 reported the Reach a Target dialog as "too transparent".
  The panel was measured opaque, so the transparency the user sees is the weak
  `0.45` window dim plus a barely-there field outline and bare text buttons,
  which leave the sheet visually merging with the Home screen behind it
  (`DASHBOARD_SPECS` 10.2, AGENTS 9). ADR-30 names `dialogScrim = 0.45f`
  explicitly, so changing it requires a new append-only entry rather than a
  silent edit.
- **Consequences:** `presentation/theme/Tokens.kt` (`dialogScrim`) and
  `TargetEntryDialog.kt` (field outline only). `LiquidGlass.kt`, the
  dialog's structure, the opaque-panel test and ADR-30 are unchanged. Verification:
  the dialog must be clearly separated from the screen behind it in both themes
  while still reading as glass, and `TargetEntryDialogSurfaceTest` must stay green.

## ADR-39 - Hero artwork at the window edge: no top fade; the full-strength head absorbs the caller's top band (restates ADR-37 rule 4)

- **Decision:**
  - **`MomentumHeroArtwork` draws no top fade.** The mask's stop list becomes
    `0.000 -> White (alpha 1)`, `bottomStart -> White (alpha 1)`,
    `1.000 -> Transparent (alpha 0)` for every production caller - full strength
    from the container's top edge down to the bottom blend, where
    `bottomStart = (1 - bottomDissolvePx / h).coerceIn(0.10f, 0.95f)` (the
    component's existing no-fade branch, now the only branch used). On the
    reference device (420 dpi, Habits expanded `h ~= 787 px`,
    `bottomDissolve = 88.dp = 231 px`) that is `0.000 -> 1.0` (y = 0 px),
    `0.706 -> 1.0` (y ~= 556 px), `1.000 -> 0.0` (y = 787 px): Habits is full
    artwork across both the status-bar zone `0..136` and the former band zone
    `136..251`. The component stops reading `WindowInsets.statusBars` for
    masking; the status-bar safe area remains the foreground copy's job (ADR-37
    rule 3 / ADR-35).
  - **`topDissolve` stays in the API and keeps ADR-30's caller shape; its
    meaning is "declared band, absorbed at the window edge."** Callers still
    pass only band amounts and cannot position the artwork. The component
    generates **no stop** from `topDissolve` - no transparent stop, no dip, no
    offset, no padding. Habits' `44.dp` means "the top band is declared but
    absorbed; artwork is at full strength through it" - never a fade start, a
    dip depth, or an offset. Home's `0.dp` keeps its meaning and renders
    bit-identically to the current tree. A real top fade would only be correct
    for a container whose top edge borders page background; no such caller
    exists today (only `HomeHero` and `HabitsHero`), and one appearing later is
    a new decision (ADR), not a default.
  - **ADR-37 is amended in part, append-only.** ADR-37's rules 1-3, its
    ISSUE-016 finding and its ISSUE-017 root-cause / shared-component decision
    stand. Superseded by this entry: ADR-37 rule 4 ("full strength through the
    inset; the caller's band begins below it" / "the component reads
    `WindowInsets.statusBars` itself"), the ADR-37 ISSUE-017 expected geometry
    ("dissolve `136..251 px` - byte-for-byte the pre-ADR-33 on-screen look"),
    and its verification clause "with the dissolve starting at `statusBars`".
    The verification becomes: Habits shows full-strength artwork at `y=0` in
    both themes with **no** top fade (the only fade is at the bottom, below the
    copy), no stripe at `136..251`, rows at the inset boundary smooth; Home
    stays pixel-unchanged.
- **Context:** TASK-006 (ISSUE-017) - the Developer's bounded H1 -> H2 -> STOP
  escalation (task.md section 9, "Fix attempt (Handoff 4) - BLOCKED") proved
  ADR-37 rule 4 admits no compliant stop list: alpha = 1 on `[0, 136 px]`
  combined with a caller band beginning transparent at `136+` steps by about
  `(+65,+52,-1)` Light / `(-13,-11,-73)` Dark in one pixel row (20-30 %
  brightness, a visible seam); an opaque-start dip shows `AppBackground` as a
  band between the status-bar area and the hero body at a depth specified
  nowhere (ISSUE-017 relocated, invented geometry); and "byte-for-byte
  pre-ADR-33" means `AppBackground` at `y 0..136` - ADR-33's own Context
  records that band, and `git show 2c88b31` (one file, `MomentumNavGraph.kt`;
  `HabitsHero.kt` untouched) confirms the hero box used to start below the bar,
  so it contradicts ADR-37's own "artwork at y=0" and requirement 7. The
  approved image-era Habits hero (`git show 8c45c64`) blended its 44 dp band
  into that background band - a blend target ADR-33 removed - while
  `DASHBOARD_SPECS.md` section 6 already locks the full-strength top ("no band
  of plain page background above the date ... the only fade is below the
  message"). The only seam-free, stripe-free, artwork-at-y=0 profile is
  alpha = 1 across the whole top region, so the caller's declared band is
  absorbed there. Only two screens call the component, both window-top, so the
  rule lives in the component with no collateral callers and no new technique
  (no pixel sampling, no brightness curves, no screen-coordinate positioning,
  no fixed offsets).
- **Consequences:** `MomentumHeroArtwork.kt` loses the status-bar read, the
  `topEnd` branch and `TopDissolveMax`, and its doc comment is trued up;
  `HabitsHero.kt`, `HomeHero.kt`, `MomentumNavGraph.kt`, `MainActivity.kt` and
  the artwork assets are unchanged, and ADR-30's caller contract and ADR-33's
  edge-to-edge geometry are untouched. `MomentumHeroArtworkTest`'s branch-shape
  assertion (`if (topDissolve <= 0.dp)` / `if (topEnd > 0f)`, whose comment says
  Habits keeps the full dissolve profile) is re-pointed to this contract - the
  only test this decision changes; `HabitsHeroAssetsTest` (`TopDissolve =
  44.dp`) and `HomeHeroAssetsTest` (`TopDissolve = 0.dp`) stay green. Risks: the
  Habits crop's top pixels differ from Home's, so A4(h) must be re-verified for
  continuity (fail signature: flat `AppBackground`, delta <= 6/255) and for
  status-bar icon contrast in both themes. Verification: Habits `y=0` artwork in
  Light and Dark, no stripe at `136..251`, smooth rows across `135/136/137`,
  Home control unchanged, collapse behaviour (A4(i)) unchanged.

## ADR-40 - Phase 2B Round 1 is device-rendered Compose prototypes, isolated under `presentation/exploration/phase2b` with a debug-source-set-only entry point

- **Decision:**
  - **Phase 2B is redefined from an image-generation stage into an on-device
    Compose prototype stage.** TASK-008 defined Phase 2B as generating twenty
    screen images; the governing user brief for TASK-009 defines Phase 2B Round 1
    as four isolated Jetpack Compose visual prototypes of the Home/Today screen
    (A - Press, D - Aurora Governed, B - Instrument, C - Loom) plus a
    developer-only preview screen plus four captured screenshots. The user brief
    governs (AGENTS.md preamble). The redefinition is recorded here rather than
    applied as a silent edit to `docs/development/task_history/TASK-008_*.md` or
    to the Phase-2B sections of `MOMENTUM_CONCEPT_EXPLORATION.md` (section 16),
    both of which stay as written. `MOMENTUM_CONCEPT_EXPLORATION.md` remains the
    sole source of visual truth; only its *output medium* changed. The previously
    rejected generated images and the 5.4 MB concept pack (ISSUE-024) are **not**
    references.
  - **Package location: `com.momentum.app.presentation.exploration.phase2b`.**
    The user brief's `ui/exploration/phase2b` is a conceptual location; the brief
    itself directs the implementer to the repository's actual convention. This
    project has **no `ui/` segment** - presentation packages are
    `presentation/{aurora,components,navigation,screens,theme}` - so
    `presentation.exploration.phase2b` is the only path that both follows
    KNOW-ARCH-001 and preserves the `exploration/phase2b` separation the brief
    requires.
  - **No production surface changes.** No edit to `MomentumNavGraph`,
    `MainActivity`, `app/src/main/AndroidManifest.xml`, `AppContainer`,
    `presentation/theme/**`, `presentation/components/**`, `res/drawable/*`,
    `res/font/*`, `res/values/*`, the Room schema, the version catalog, or any
    Gradle file. The only new production locations are
    `presentation/exploration/phase2b/**` and `res/drawable-nodpi/phase2b/**`.
  - **The preview entry point lives in the `debug` source set only.** A new
    `app/src/debug/AndroidManifest.xml` declares a `Phase2BPreviewActivity`
    (`androidx.activity.ComponentActivity`) whose only job is to host the
    A/D/B/C switcher; its Kotlin file is
    `app/src/debug/java/com/momentum/app/presentation/exploration/phase2b/preview/Phase2BPreviewActivity.kt`.
    It is launched by
    `adb shell am start -n com.momentum.app/com.momentum.app.presentation.exploration.phase2b.preview.Phase2BPreviewActivity --es direction A`.
    **No debug source set exists today, so this creates one; it needs no
    `build.gradle.kts` change** because AGP defines `debug` from the build type
    and `debugImplementation` already carries `compose.ui.tooling` and
    `compose.ui.test.manifest`. Rejected alternatives: an instrumentation-only
    host is not a task-3.1-allowed path (`app/src/androidTest/**` is not listed)
    and forces the capture to come from `captureToImage()` rather than from the
    real window; a Robolectric JVM render would require adding a Compose test
    dependency that is not in `testImplementation`, which the task's
    no-new-dependency constraint forbids. Accepted cost: the prototype
    composables and `res/drawable-nodpi/phase2b/**` **do ship inside the release
    APK as unreachable dead code**, because task section 3.1 mandates the
    prototype package under `app/src/main` and the release build has
    `isMinifyEnabled = false`. They are deleted by removing two directories when a
    direction is chosen.
  - **Exactly one shared artefact: an immutable content fixture.** A single
    `Phase2BFixture` object is the only code the four prototypes share, and it
    carries **content only** - date, greeting, completion counts, the five
    eligible activities with titles, categories, tracking kinds, today's state,
    run lengths, unit labels and *dye names* - never a colour, size, radius or
    dp. Directions that spend the colour field (C and D, per section 10 Q5) map
    dye *names* to their own values inside their own token file; A and B never map
    them. **No composable, layout, modifier, token or `TextStyle` is shared
    between directions**, because a shared visual component is precisely the
    convergence failure section 4.1 of the source document exists to prevent.
  - **Prototype typography does not touch `presentation/theme/Type.kt`.** Each
    direction declares its own `FontFamily` over the bundled variable
    `res/font/manrope.ttf` (verified: one `wght` axis, min 200, max 800, **default
    200**, plus `tnum` and `pnum` present in GSUB) registering the five instances
    each direction's register table needs, and applies
    `FontFeatureSettings("tnum")` per changing figure. Pinning
    `variationSettings` on every registration is mandatory because the file's
    default instance is weight 200.
  - **Screenshots are `adb exec-out screencap -p` from the AVD, in one session,
    under one fixed configuration**, written to `docs/phase2b/round1/`. A single
    capture contract (device `Pixel_7`, 1080x2400 at 420 dpi, `font_scale 1.0`,
    system dark mode forced off, animations scaled to 0, gesture-navigation
    insets in their default state) applies to all four. Real window rendering is
    chosen deliberately over a JVM or `captureToImage()` render because sections
    4.2 and 15.2.25 of the source document make the **status-bar region part of
    the composition**, and only a real window produces it.
  - **Colour values are derived, not quoted.**
    `MOMENTUM_CONCEPT_EXPLORATION.md` contains **no hex value anywhere**
    (verified by grep across all 8,008 lines); it specifies palettes
    qualitatively. The exact per-direction Light palettes recorded in `task.md`
    section 7 are therefore the Architect's derivation from each direction's
    8.7/8.18 prose, measured against that direction's own contrast floor, and are
    part of the hypothesis being tested - not a quotation of the source document.
    Reviewers must judge them against the prose, not against a hex list the
    document never contained.
  - **ISSUE-006 is not answered by this entry.** D1's photographic header
    references the **already-bundled** `R.drawable.habits_hero_light` read-only -
    no copy, no recolour, no new asset, nothing in `res/drawable/` touched - which
    is the bundled side of ISSUE-006's question adopted *provisionally for the
    prototype only*. ISSUE-006 stays **OPEN**; the programmatic-versus-bundled
    ruling belongs to the stage that adopts a direction. Generating a new
    photographic asset is **out of scope** for Round 1.
  - **Dark is not built.** Each direction's palette is a `data class` whose
    constructor takes every role, provided through a `CompositionLocal` and a
    `PrototypeEnvironment` enum. Round 1 supplies the `LIGHT` value only. Adding
    Dark is one new instance plus one provider branch, with **no composable
    change**; no direction may branch on environment inside a composable.
- **Context:** `task.md` section 1.1 records the TASK-008/TASK-009 scope conflict
  and the AGENTS.md preamble makes the user prompt govern. Task section 4.2
  records that there is no `ui/` package segment, that `res/drawable-nodpi/` does
  not exist, that `manrope.ttf` is a single file, and that Robolectric 4.14.1 plus
  `androidx.test:core` are present while Compose test artefacts are not.
  ISSUE-006 is OPEN and needs a human ruling; ISSUE-024 makes the concept pack
  unusable as a reference. One fixture plus four isolated direction packages is
  the only shape that satisfies section 4.1 (non-convergence), section 14.2 (must
  be different between directions) and section 16.3 (must not converge)
  simultaneously while keeping content identical per section 4.6.
- **Consequences:** New: `presentation/exploration/phase2b/**`,
  `app/src/debug/AndroidManifest.xml`, the debug `Phase2BPreviewActivity`, and
  `res/drawable-nodpi/phase2b/**` if a direction needs a prototype-only asset
  (Round 1 needs none: A, B and C are pure Compose, and D references the existing
  bundled hero). Unchanged and provably so: every production source file, the
  production manifest, all production tokens and components, the schema, and
  every Gradle and version-catalog file. Verification: `gradlew :app:assembleDebug
  --offline` and `gradlew :app:testDebugUnitTest --offline` green; the release
  manifest merge must not contain `Phase2BPreviewActivity`; `git status` must show
  the pre-existing uncommitted production modifications listed in `task.md`
  section 3.1 untouched; and the four PNGs in `docs/phase2b/round1/` must be
  captured in one `adb` session under the fixed configuration above. Removal cost
  when a direction is selected: delete two directories and the debug source set.

### ADR-40 Addendum 1 â€” correction cycle 1 (Architect, 2026-09-28)

Appended under AGENTS.md Â§14.15 after the Reviewer's `NOT_VERIFIED` verdict on
the Round-1 render. **No clause above is edited or removed.** The sub-clauses
below are additions to ADR-40; where they touch an earlier ADR-40 bullet, the
earlier bullet's *decision* stands and only its measured consequence is
superseded.

- **Addendum 1.1 â€” Section 4.1's Q3 non-convergence clause is unsatisfiable as
  written, and the reconciliation governs.** `MOMENTUM_CONCEPT_EXPLORATION.md`
  section 4.1 (line 379) requires that "no two directions share the same answer
  on **Q1, Q2, Q3 or Q6**". The same document's own Q3 table (lines
  6896-6899) assigns **A = a place, B = a number on a calibrated scale, C = a
  shape, D = a number at true figure scale**. **B and D therefore share a Q3
  answer in the document that mandates the rule.** This is a property of the
  source document, not an implementation defect; both renders do deliver their
  distinct answers. **Ruling:** the clause is unsatisfiable as literally
  written, and the governing reading is that the shared *noun* ("a number") is a
  category while the *answers* differ in kind â€” **B's is an instrument's
  measurement, read against a five-tick graduated channel; D's is the subject
  itself, at true figure scale, over a photographic header, with no scale and no
  graduations.** Consequences: (a) the non-convergence threshold is
  **unaffected** â€” section 4.1 requires seven of nine axes to differ
  simultaneously, and the Reviewer measured seven of nine four-way distinct and
  visible in the render, with only Q3 three-way; (b) Q1 and Q6 remain
  two-way-clean; (c) **Round 2 must not** engineer the overlap away by changing
  B's or D's figure treatment; (d) the selecting stage must weigh B against D on
  the other eight axes and must not read the source's Q3 table as an instruction
  to make either of them non-numeric.

- **Addendum 1.2 â€” D's Light header uses the shipped asset as-is, and the
  locked design system's "purple everywhere" entry is a bounded Round-1
  exception.** `MOMENTUM_CONCEPT_EXPLORATION.md` section 8.8 (lines 5551-5553)
  mandates the asset and its contract â€” "one asset pair for the whole product -
  no tint, no filter, no re-colour" â€” and section 10 (line 6880) states "the
  product already owns the one asset its name fits". Section 3.1 truth 15
  (line 290) binds direction D to the locked design document, whose rejection
  list (`docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` section 33, lines 745-746)
  opens with "excessive gradients" and "purple everywhere". **The source is
  internally inconsistent here.** D1's literal prohibition (line 6563, "a
  purple-blue gradient *mesh*") is **not** breached: the band is a bundled
  photograph, not an authored mesh, and nothing is tinted, filtered or
  re-coloured. **Ruling:** the shipped `R.drawable.habits_hero_light` is used
  **as-is** for the Light comparison. A different Light asset is **not
  available** â€” the source specifies exactly one asset pair, so substituting one
  would require generating a new photographic asset, which is out of scope
  (ISSUE-006 OPEN, ISSUE-024 unusable) and would itself breach section 8.8's
  "one asset pair for the whole product". **The exception is bounded by three
  assertable conditions:** (i) the asset is confined to the header band, at
  most **14.4 %** of the viewport; (ii) **no direction-owned colour role outside
  the band may fall in the violet hue range 270-320 degrees**; (iii) the asset is
  unmodified â€” no tint, no filter, no re-colour, no second asset, and nothing
  added to or changed in `res/drawable/`. **Scope: this is a Round-1 comparison
  exception only. It is not a product ruling.** If a direction is selected and D
  ships, the exception must be re-litigated against the locked section 33 list at
  that stage, together with **ISSUE-006** (programmatic versus bundled
  artwork), which remains **OPEN**.

- **Addendum 1.3 â€” the 48 dp completion control: the container governs, except
  where a brief names the mark.** Every direction's completion control sits in a
  real `Modifier.size(48.dp)` box with a `contentDescription`, so the 48 dp
  touch-target and semantics floor of the locked design system is met in all
  four directions. The **drawn** marks were smaller, and four briefs place a
  48 dp figure in a MUST BE VISIBLE list. **Ruling:** where a brief says "48 dp
  **control**" (A1 line 1761, B1 lines 3280/3299/3314, C1 lines 4925/4941/4952)
  the 48 dp is the **container** and the drawn mark is a direction-owned token;
  where a brief names the **mark** (D1 lines 6542 and 6558, "Five **48 dp
  discs**", both MUST BE VISIBLE) the **drawn mark is 48 dp**. Per direction: A
  24 dp ring, B a 48 dp circle, C a 48 dp circle, D a 48 dp disc â€” all inside the
  unchanged 76 dp row. A blanket "the control governs" rule would leave D failing
  its own MUST BE VISIBLE list; a blanket "the mark governs" rule would force
  A's hairline ring to 48 dp, which is a category error in A's material
  vocabulary and is not asked for by A's brief.

- **Addendum 1.4 â€” D's header band is 132 dp, sized to its content, and the
  ambient field's geometry is re-specified so it is measurable.** D1 (lines
  7253, 7359) states the header "is sized to its content"; the Round-1 band was
  208 dp, which is sized to nothing in particular, and it left no room for the
  card, D's single 56 dp FAB and the floating bar to coexist at a 411 x 914 dp
  viewport. **Ruling:** the band is **132 dp** (full-strength artwork
  `y = 0-48`, single-axis dissolve `y = 48-132`), the date caption is re-seated
  to the full-strength region, the 76 dp row and the 56 dp FAB are unchanged, and
  the expected FAB-to-card clearance becomes **+8.4 dp**. The ambient field is
  re-specified to `progressAccent` at **alpha 0.12**, centred at 50 % width /
  **100 %** band height with radius **0.45 x bandHeight**, drawn **behind** the
  opaque band so no clip is needed and a clean lower half-disc is exposed on bare
  canvas; the expected measurable result is **>= 1.15:1** against the canvas. The
  field is **never** drawn over the artwork, so section 8.8's asset contract is
  preserved absolutely and the `aurora/` package still contains exactly two
  `Brush` call sites. A second palette role, `captionInkOnArtwork`, carries the
  caption's ink over the artwork, so **Dark remains addable with zero composable
  edits** â€” the change is a role in the   palette `data class`, not a branch in a
  composable. All colour and size values involved are the Architect's
  **derivations** under **ISSUE-025**; the source document contains no hex and no
  `sp` value anywhere.

### ADR-40 Addendum 2 â€” correction cycle 2 (Architect, 2026-09-28)

Appended under AGENTS.md Â§14.15 after the Developer's measured report on
**correction cycle 1** (`task.md` Â§9.11, Â§9.17, Â§9.18, Â§9.21â€“Â§9.23).
**No clause above is edited or removed.** The sub-clauses below are additions to
ADR-40; where they supersede an earlier ADR-40 bullet, the earlier bullet's
*decision* stands and only its measured consequence or a false coordinate is
superseded. All colour and size values below are the Architect's **derivations**
under **ISSUE-025**.

- **Addendum 2.1 â€” Â§4.1's non-convergence test is scored on the source's own
  nine named axes, and a shared 48 dp control silhouette does not reduce the
  count. Addendum 1.3 stands in full; no mark is to be shrunk.** Section 4.1
  (lines 377â€“379) states the test as a fraction of a **closed, enumerated list**:
  "The four directions differ on **at least seven of these nine axes
  simultaneously**", where the nine are the nine rows of the table at lines
  381â€“391 â€” *visual metaphor, primary surface material, what a card is, Q1
  answer, Q3 answer, Q6 answer, motion personality, navigation model,
  composition*. **Completion-control silhouette is not among them.** The Phase-2B
  plan's own cross-direction table carries two extra rows the source does not â€”
  "Mark vocabulary" and "Accent" â€” as *additional* guards; they are not axes of
  the Â§4.1 test and contribute nothing to the count. **Ruling: the count is a
  property of the method, not of the render, and it is unchanged.** Eight of the
  nine axes remain four-way distinct (all but Q3, ruled in Addendum 1.1), and
  seven of those eight are also observable in a static still â€” the same 7-of-9 the
  Reviewer measured. Addendum 1.3's mark sizes (A 24 dp ring, B 48 dp circle,
  C 48 dp circle, D 48 dp disc) are therefore **confirmed and must not be
  revisited** on convergence grounds. **There is no source-internal
  contradiction between Briefs B1/C1/D1 and section 4.1**: section 4.1 asks for
  distinct *answers* on nine named axes, and none of those nine is answered by a
  control's outline. Both requirements are satisfied at once. **Forward guard,
  binding on Round 2 and on any later correction cycle:** *no* direction may
  reduce a 48 dp drawn mark below 48 dp, and *no* direction may change a
  brief-mandated 48 dp circle's silhouette, **for the purpose of restoring
  mark-vocabulary differentiation**. The remedy space for any genuine mark
  convergence is **interior treatment and hue** â€” never a size or shape a brief
  states. A cold-read confusion between two of B, C and D must be escalated as a
  finding on one of the source's nine axes, not as a request to shrink a MUST BE
  VISIBLE control. The Developer may keep recording the shared silhouette as a
  live observation; it is accurate and it stays on the record, but it is not a
  section 4.1 breach.

- **Addendum 2.2 â€” D's header band is 116 dp, the ladder is closed, and the
  8 dp FAB clearance is restored. Addendum 1.4's 132 dp and its +8.4 dp
  prediction are superseded.** Cycle 1 measured the clearance ladder as âˆ’67.6 dp
  at 208 dp, âˆ’4.1 dp at 132 dp, and **+3.81 dp** at the 124 dp fallback. The
  +3.81 dp figure is the Developer's and is accepted, and the residual shortfall
  is **the Architect's arithmetic error**: section 7.4(D) item 6 specifies "a
  labelled group divider" â€” a 26 dp group-label block â€” and the cycle-1 table
  priced the card at the Reviewer's Round-1 **measured** extent of 399.0 dp
  without adding it. The card is 410 dp as built, and that 10.9 dp is the entire
  shortfall. The card is not shortened, because the 76 dp row and the group label
  are both fixed. **Ruling: the third step is `headerHeight = 116.dp`**, with
  artwork at full strength over `y = 0-44` (unchanged), the single-axis dissolve
  over `y = 44-116` (72 dp, `0.0 -> 1.0`, `dissolveHeight = 72.dp`), and
  everything else untouched â€” row height 76 dp, the 56 dp FAB, the 1 px
  dividers, the 48 dp containers, all spacing tokens. The ambient field's
  parameters (`progressAccent`, `alpha 0.12f`, centre 50 % width / 100 % band
  height, radius `0.45 x bandHeight`) are unchanged; the radius becomes 52.2 dp
  and the exposed half-disc `y = 116-168.2`, and it is **still drawn behind the
  band, still unclipped, still never over the artwork**. The band is
  **12.7 %** of a 411 x 914 dp viewport, which still satisfies D1 line 6523
  ("visibly shorter than a full-bleed hero") and still satisfies the
  **<= 14.4 %** bound of Addendum 1.2 condition (i). **Expected clearance
  +11.8 dp** (predicted; the Developer measures and records). **The ladder is
  now closed at 116 dp.** If the measured clearance there is still below 8 dp it
  is a defect to be reported, not a fourth step to be taken. A clearance of
  3.81 dp was rejected as an acceptance because it is a single-device coincidence
  and not a viewport-robust contract, and because the FAB carries a
  hue-carrying shadow whose blur bleeds across a gap smaller than the blur.

- **Addendum 2.3 â€” the header date caption's position is a device-derived rule,
  not a coordinate. The cycle-1 coordinates `y = 32-48` and `28-44` are
  withdrawn as false, and so is the "full-strength artwork" requirement.** Those
  coordinates assumed a 24 dp status bar; the measured status bar on the capture
  device is **51.81 dp** (`dumpsys window displays`,
  `InsetsSource id=a01a0000 type=statusBars frame=[0,0][1080,136]`), so they
  were physically impossible there. **Ruling â€” the governing rule is the one
  cycle 1 stated as a constraint, and it is what the Developer implemented:** the
  caption's line-box top clears the **measured** status-bar bottom by
  **>= 8 dp**, implemented as `statusBarsPadding()` plus an 8 dp top pad, so the
  clearance is 8.0 dp by construction on any device. **No absolute `y` may be
  specified for it in the plan.** On the capture device the resulting line box is
  `y = 59.81-75.81 dp` with ink at `64.0-72.4 dp`; those are measurements, not
  specifications. The cycle-1 requirement that the caption sit "entirely on
  full-strength artwork" is **withdrawn**: it is unsatisfiable on any device
  whose status-bar inset plus 8 dp plus the line height exceeds the full-strength
  region. The requirement that replaces it is the one that actually governs
  legibility â€” **measured minimum local contrast >= 4.5:1** â€” and the caption is
  *deliberately permitted to sit inside the dissolve ramp*, because the ramp
  mixes canvas in, which lightens the substrate and raises contrast against a
  dark ink. Measured minimum local contrast is **9.13:1**. Because the ramp
  begins at `0.0`, the full-strength boundary is not a colour discontinuity and
  the "no seam at the status bar" criterion is satisfied by construction.

- **Addendum 2.4 â€” the header's dissolve ramp is drawn below the copy. The
  draw order is normative, not incidental.** The cycle-1 implementation applied
  the ramp with `Modifier.drawWithContent { drawContent(); drawRect(ramp) }` on
  the band container, which composites the ramp over **everything the band
  contains â€” the artwork and the copy**. At Round 1's geometry the caption sat
  above the ramp so it was never exercised; at the corrected geometry it fell
  inside the ramp, and the measured result was zero pixels of `#141A24` in the
  caption region, the darkest glyph reading `#3A404A` â€” the ink composited with
  14â€“33 % canvas. **Ruling: the ramp overlays the *artwork* and lies *below the
  copy*.** The artwork and the ramp are one draw layer; the copy is a sibling
  composable above it. This is what section 7.1(e) means by "dissolving to canvas
  **below the copy**", and it is also what Addendum cycle 1's Finding 1 ruling
  requires â€” a ramp drawn over the caption is a translucency over the copy, the
  same defect as the scrim that ruling rejected. The ramp's geometry, stops,
  colours and `Brush` count (exactly two in `aurora/`) are unchanged, the asset
  is still never tinted, filtered or re-coloured, and the caption's glyph cores
  are the pure ink again â€” 1 615 exact-`#141A24` pixels. **The Reviewer must
  assert that assertion directly**: that the caption region contains pure-ink
  pixels and that no alpha compositing touches the copy.

- **Addendum 2.5 â€” B's complete-state control carries a 12 dp engraved index
  line in inscription ink, and the non-text contrast of that line is recorded so
  it is not re-raised.** Brief B1 says nothing about the interior of the control,
  so this mark is an Architect derivation. **Ruling:** the line is **12 dp long,
  2 dp wide**, in `palette.inscription` `#3C4245`, **centred on the control's
  horizontal diameter** â€” the cycle-1 phrase "across its horizontal diameter" is
  withdrawn as imprecise, because a 12 dp line does not span a 48 dp diameter.
  Incomplete state remains the 1 px `#3C4245` outline ring at 48 dp.
  **Recorded contrast:** `#3C4245` on the `#C2571A` filled disc is **2.27:1**.
  This is **below** the 3:1 non-text threshold of WCAG 1.4.11 and is **accepted
  deliberately**: the line is decorative and carries **no** information that the
  disc's own fill does not already carry, and it is not body text under the
  design system's 4.5:1 rule. It is recorded here so that Round 2 and the
  Reviewer do not re-derive it as a new finding. Note also that the incomplete
  state is a plain 1 px ring with **no sweep and no numeric percentage**, so it
  is not the "progress ring or circular percentage" that Brief B1's MUST NOT
  (line 3316) forbids, and the package still contains **zero** `drawArc` call
  sites.

- **Addendum 2.6 â€” C's control carries two named states; C1's three named states
  are states of the weft band, and the control's extra in-progress state is
  confirmed.** Brief C1 (lines 4919â€“4922) names three states â€” "three tight and
  dyed a deep madder red, one visibly loose and paler, one **not yet laid**" â€”
  and those are unambiguously states of the **full-width horizontal band of five
  short woven picks**, which C1 mandates separately and which is implemented. The
  row control's own requirement in C1 is only "a 48 dp **circular** control"
  (line 4925), with no state list attached. **Ruling:** the control's two states
  are the plan's â€” a **madder-dyed disc carrying C's 12 dp warp grid** (a dyed
  pick) for complete, and a **1 px bare-warp circle** showing the ground's warp
  lines for incomplete. The **loose pick rendered inside the first incomplete
  control** is confirmed as a third, in-progress state: it is a superset of the
  two named states, it uses the existing `loosePick` role rather than
  orphaning it, it is distinguished from madder by both hue and alpha
  (`#C9786A` at `alpha 0.85` against `#A6372A`), and it introduces no red error
  semantic, which C1's MUST NOT forbids. **One binding constraint:** the
  in-control loose pick must stay **paler and gapped** â€” three visible segments
  with gaps, never a second solid pick â€” or it will read as a completion.

## ADR-41 - TASK-011 toolchain: AGP 9.0.0 + built-in Kotlin 2.2.10 + KSP2 `2.2.10-2.0.2` + Gradle 9.1.0 + Room 2.7.1; the skill's Kotlin/KSP pins are superseded by the AGP 9 floor

- **Decision:**
  - Adopt the toolchain the installed skill requires **at the versions AGP 9
    actually permits**: `agp = 9.0.0`, Gradle wrapper **9.1.0**, `kotlin =
    2.2.10`, `ksp = 2.2.10-2.0.2` (KSP2), `room = 2.7.1`, `coroutines =
    1.10.2`, `datastore = 1.1.7`, `jvmTarget = 17` unchanged.
  - **Remove the `org.jetbrains.kotlin.android` plugin** from the catalog and
    `app/build.gradle.kts`. AGP 9's built-in Kotlin owns the Kotlin compilation;
    applying KGP's Android plugin alongside it fails ("Cannot add extension with
    name 'kotlin'"). `kotlin-kapt` is likewise incompatible (none is applied â€”
    KSP stays the only processor). The `org.jetbrains.kotlin.plugin.compose`
    and `org.jetbrains.kotlin.plugin.serialization` Gradle plugins remain and
    are set to **2.2.10** (marker POMs verified to exist).
  - **Remove the `kotlinOptions { jvmTarget }` block.** Built-in Kotlin derives
    `jvmTarget` from `compileOptions.targetCompatibility` (both 17).
  - `android.builtInKotlin = false` (the documented opt-out) is **not** used.
  - `buildFeatures.buildConfig = true` and `room { schemaDirectory(...) }` are
    retained (Settings reads `BuildConfig.VERSION_NAME`; schemas stay committed).
- **Options:**
  - (a) Keep the skill's Kotlin 2.1.20 + KSP `2.1.20-1.0.32` / `2.1.20-2.0.1`
    pins under AGP 9 â€” impossible: AGP 9 release notes force KGP â‰¥ 2.2.10
    (Gradle auto-upgrades lower KGP) and KSP â‰¥ 2.2.10-2.0.2; Maven Central has
    **no KSP1 build for Kotlin 2.2.10** (only `2.2.10-2.0.2` and RCs exist), so
    neither skill pin can resolve at the forced floor.
  - (b) Set `android.builtInKotlin = false` to stay on Kotlin 2.1.20 â€” rejected:
    it keeps the deprecated plugin path the skill's AGP 9 target removes, and
    buys nothing Momentum needs.
  - (c) **Chosen:** accept the AGP 9 floor â€” Kotlin 2.2.10 + KSP2
    `2.2.10-2.0.2`, plugin changes above. Skill minimums (Kotlin 2.0+/K2, KSP
    not kapt, AGP 9.0+) remain satisfied.
- **Context:** TASK-011 Â§7 Conflict 1. Every version claim above was checked
  against AGP 9.0.x release notes, `services.gradle.org` distributions (9.1.0
  exists) and Maven Central/Google Maven metadata before deciding. Room moves
  2.6.1 â†’ 2.7.1 because KSP2-era Room starts at 2.7.x; no schema or entity
  change is planned, so the `identityHash` of existing installs is untouched
  (the pre-existing identityHash issue stays reported, not fixed).
- **Consequences:** `gradle/libs.versions.toml`,
  `gradle/wrapper/gradle-wrapper.properties` and `app/build.gradle.kts` change
  in Phase A only; no Kotlin source changes in that phase. First
  `:app:assembleDebug` under Gradle 9.1.0 + KSP2 + Room 2.7.1 is the gate for
  the whole task; Hilt (ADR-43) and nav3 (ADR-42) load later and get their own
  gates. Risks: JDK 21 on PATH with `jvmTarget 17` (fine for AGP 9), one-time
  downloads of Gradle 9.1.0 and Build-Tools 36.0.0 (AGP 9 minimum).

## ADR-42 - Navigation 3 is adopted as `androidx.navigation3` 1.1.0 + `@Serializable` NavKeys; the skill's `navigation-compose:2.9.0` pin is Nav2 and is superseded; Compose BOM 2026.01.01 and Lifecycle 2.10.0 are forced by the nav3 floors

- **Decision:**
  - Artifacts: `androidx.navigation3:navigation3-runtime:1.1.0`,
    `androidx.navigation3:navigation3-ui:1.1.0`,
    `androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0` (entry-scoped
    ViewModels), `kotlinx-serialization-json:1.8.1` + serialization Gradle
    plugin `2.2.10`. `navigation-compose` is removed from the catalog in
    Phase C.
  - Routes become `@Serializable` `NavKey`s with **ID-only arguments**
    (`activityId: Long` etc.), replacing the string routes
    `edit?activityId={activityId}` / `detail/{activityId}`. One back-stack +
    `NavDisplay` + `entryProvider` replaces `NavHost`; nested graph structure,
    the ADR-22 four-tab bottom bar, tab re-select (= old `launchSingleTop`),
    back behavior, `DetailTypeGate`, the create flow and the
    `MainActivity.EXTRA_OPEN_ACTIVITY` intent seed are behavior-preserved.
  - Bridge: Phase A raises `navigation-compose` 2.7.0 â†’ **2.9.0** (the skill's
    own pin, ref-07-era API) so the compose-BOM jump is not applied to a
    two-year-old nav artifact; Phase C deletes it.
  - Compose BOM **2026.01.01** (ui/animation/runtime 1.10.2, material3 1.4.0)
    and `lifecycle = 2.10.0`.
- **Options:**
  - nav3 **1.0.0**: oldest stable (compose floor 1.9.5, BOM â‰¥ 2025.11.01) â€”
    rejected: superseded by 1.1.0 fixes for no cost.
  - nav3 **1.2.0**: hard-requires `navigation3-runtime` 1.11.2 â†’ Compose BOM â‰¥
    2026.06.01 (compose 1.11.x) â€” rejected: a much larger jump for no feature
    Momentum needs.
  - **Chosen:** nav3 **1.1.0** â€” its verified POM floors are compose 1.10.0,
    lifecycle-runtime 2.10.0, savedstate 1.4.0, navigationevent 1.0.2; BOM
    2026.01.01 satisfies ui/runtime/animation 1.10.2 > 1.10.0 and lands in the
    same calendar line as AGP 9.0.0 / Kotlin 2.2.10.
  - Staying on Nav2 â€” rejected: Navigation 3 is a REQUIRED skill target and
    explicit task scope (Â§3, Â§7 Conflict 4).
- **Context:** TASK-011 Â§7 Conflict 4. The skill's artifact extraction is
  internally inconsistent: `navigation-compose:2.9.0` is Nav2's type-safe route
  API (ref 07 describes `rememberNavController`/`NavHost`/`toRoute<T>`), while
  the same skill marks `NavKey`/`NavBackStack`/`NavDisplay` REQUIRED. Task
  authority resolves this in favor of genuine Nav3. `lifecycle 2.9.0` (skill
  RECOMMENDED) is superseded because nav3 depends on lifecycle-runtime 2.10.0
  and `lifecycle-viewmodel-navigation3` does not exist below 2.10.0.
- **Consequences:** `Routes.kt`, `MomentumNavGraph.kt`, `DetailTypeGate.kt`,
  `CreationNav.kt`, `MainActivity.kt` and bottom-bar call sites change in
  Phase C; compile gates cannot prove navigation parity, so tab re-select,
  back/predictive-back, create flow, detail open/close and the notification
  intent seed are runtime-smoked by the Reviewer. `@Serializable` route classes
  need R8 keep coverage (Phase F). `hilt-navigation-compose 1.2.0` (ADR-43)
  pulls an unused `navigation-compose 2.5.1` transitively â€” accepted as dead
  weight, R8-stripped; no Nav2 API may be used. material3 1.3.0 â†’ 1.4.0 comes
  with any BOM â‰¥ 2025.09 and carries an explicit Phase E visual-regression
  watch (no redesign permitted either way).
- **Implementation-forced consequences (recorded post-implementation, Phase C):**
  - **`compileSdk` 35 â†’ 36** (targetSdk 35 and minSdk 26 unchanged). The
    approved nav3 1.1.0 artifacts (`navigation3-runtime-android:1.1.0`,
    `navigation3-ui-android:1.1.0`, `lifecycle-viewmodel-navigation3-android:
    2.10.0`) declare `aarMetadata.minCompileSdk = 36`, so at compileSdk 35
    `:app:checkDebugAarMetadata` fails and the ADR-42 artifact set cannot be
    consumed. Verified independently by the Reviewer from the cached AAR
    metadata. This is a forced consequence of the approved artifacts, not a
    new decision.
  - **Local retaining saveable-state NavEntry decorator** is the approved
    means of preserving Nav2 `restoreState` tab-scroll behavior: a copy of
    nav3's stock `SaveableStateHolderNavEntryDecorator` with its pop-removal
    (`removeState`) call no-op'd, so a tab's scroll position survives tab
    switching; `rememberViewModelStoreNavEntryDecorator` still clears
    per-entry ViewModels on pop (matching Nav2: popped tab kept state, lost
    its VM). The Reviewer assessed this as conforming to ADR-42's
    behavior-preserved intent; it is recorded here as part of this decision.
    The stock decorator remains available if a later runtime pass prefers it.

## ADR-43 - Hilt replaces `AppContainer` (ADR-09 superseded in part); version 2.60.1 (adopted â€” 2.57.2's Hilt Gradle Plugin fails under AGP 9; 2.57.2 was the minimum for Kotlin 2.2 metadata, the skill's 2.56.2 pin is superseded)

- **Decision:**
  - Adopt Hilt as Momentum's DI: `@HiltAndroidApp MomentumApp`, `di/` modules
    mirroring what `AppContainer` assembles (database/DAOs, repository
    `@Binds`, use cases, settings DataStore, `ReminderScheduler` binding),
    `@HiltViewModel + @Inject constructor(...)` for all 10 ViewModels,
    `hiltViewModel()` replacing `momentumViewModel`/`rememberContainer`
    (26 call sites; `presentation/ViewModels.kt` deleted), `@Singleton`
    `AlarmReminderScheduler`, `@HiltWorker` for `FireReminderWorker`/
    `RescheduleWorker` (androidx.hilt:hilt-work 1.2.0; `MomentumApp` implements
    WorkManager's `Configuration.Provider`), and `@AndroidEntryPoint` where
    injection actually occurs: `MainActivity` and `CompleteActionReceiver` â€”
    `ReminderReceiver` and `SystemEventReceiver` inject nothing (they only
    enqueue WorkManager), so no no-op `@AndroidEntryPoint` is applied to them
    (Hilt supports manifest-instantiated receivers when injection is needed).
    **A manifest change is required:** `androidx.work.WorkManagerInitializer`
    must be removed via `tools:node="remove"` â€” otherwise androidx.startup
    initializes WorkManager with the default factory and the injected
    `HiltWorkerFactory` never creates the `@HiltWorker`s.
    Default-category seeding moves from `AppContainer.init` to an idempotent
    call in `MomentumApp.onCreate` on an injected dependency. `AppContainer.kt`
    is deleted once no reference remains.
  - Version: **`hilt = 2.60.1`** (adopted; see Options â€” 2.57.2's Hilt Gradle
    Plugin fails under AGP 9, 2.60.1 was the pre-authorized fallback),
    `hilt-navigation-compose = 1.2.0`, `hilt-work = 1.2.0`.
- **Options:**
  - Keep `AppContainer` as a documented deviation â€” rejected: the skill marks
    `@HiltViewModel` + constructor injection its Mandatory Default, and task
    Â§7 Conflict 3 directs migration when the skill's target requires it.
  - Koin â€” rejected: not in the skill, a new dependency, no Momentum need.
  - Hilt **2.56.2** (skill pin) â€” rejected: Dagger 2.57 "unshades the Kotlinx
    Metadata to support Kotlin 2.2.0" and 2.57.1/2.57.2 stop the Hilt Gradle
    Plugin forcing Kotlin 2.2 on consumers; at Kotlin 2.2.10 (ADR-41) the
    skill's pin is the last version that cannot read the project's metadata.
  - **Chosen:** **2.57.2** (minimum that supports Kotlin 2.2); pre-authorized
    fallback **2.60.1** (latest stable) if the Phase B build fails.
    **Post-implementation record:** the Phase B build did fail â€” 2.57.2's Hilt
    Gradle Plugin errors under AGP 9 ("Android BaseExtension not found") â€” so
    the pre-authorized fallback **2.60.1 is the adopted version** (androidx.hilt
    1.2.0, KSP only, never kapt), exercised exactly as authorized here; no new
    decision was required.
- **Context:** TASK-011 Â§7 Conflict 3. ADR-09 (manual DI via `AppContainer`)
  said migration happens "only if graph pain justifies it, with a new ADR" â€”
  this ADR is that new ADR, triggered by the skill's Mandatory Default adopted
  as this task's direction, not by graph pain. Current call sites are bounded
  and enumerated (9 ViewModels + OnboardingViewModel + MainActivity + 3
  notification call sites + 26 `momentumViewModel` sites + 3 JVM test files).
- **Consequences:** Phase B touches `MomentumApp.kt`, `MainActivity.kt`,
  `AppContainer.kt` (deleted), `presentation/ViewModels.kt` (deleted), all
  ViewModel files, `OnboardingScreen.kt`, `notification/Reminders.kt`,
  `notification/ReminderScheduler.kt` and the 3 test files. JVM unit tests stay
  Hilt-free: the ViewModels take interface dependencies, so tests build them
  directly (the `reminderSchedulerOverride` seam becomes an ordinary
  constructor argument). Validation gate: `assembleDebug` +
  `testDebugUnitTest` + `compileDebugAndroidTestKotlin` before Phase C starts.

## ADR-44 - MVI contract only where the skill's own trigger fires (Today, Creation, Activities); MVVM retained for the other seven ViewModels; atomic state, `Channel` effects and `collectAsStateWithLifecycle` are unconditional

- **Decision:**
  - **Classification rule.** The full MVI contract (one public
    `StateFlow<UiState>`, one private holder mutated only via atomic `update`,
    sealed `UiEvent` through `onEvent`, sealed `UiEffect` via
    `Channel(BUFFERED)` + `receiveAsFlow()`) is required when **either** the
    UiState holds â‰¥ 3 interdependent *user-mutable* fields **or** the screen
    drives a multi-step user flow (wizard / dialog-chain / transient dialog
    state). Everything else keeps MVVM (derived `stateIn` state, public action
    methods, typed effects where they already exist).
  - **MVI required (3):** `TodayViewModel` (multi-step completion â†’
    target-entry dialog flow; the separate `_targetEntry` holder becomes a
    combine source so the screen collects one StateFlow),
    `CreationViewModel` (multi-step guided wizard, 10 atomic form updates),
    `ActivitiesViewModel` (query/tab/category/sort = 4 interdependent
    user-mutable fields, 9-action surface; its four private flows consolidate
    into one filter holder; `Channel<String>` becomes a typed
    `ActivitiesUiEffect`).
  - **MVVM retained (7):** `HabitsViewModel` (derived state, command surface;
    wire the existing dead sealed `HabitsEffect` in place of `Channel<String>`),
    `CalendarViewModel` (only 2 user-driven inputs â€” month and selection; the
    rest is derived; **closest call of the seven, rationale recorded here so it
    is not re-litigated**), `SettingsViewModel` (independent single-field
    setters; ref 01 explicitly routes Settings to MVVM),
    `ActivityDetailViewModel` / `HabitDetailViewModel` (state fully derived,
    3/2 commands, typed effects â€” their sealed `UiState`/`UiEffect` are
    **retained, not stripped**), `InsightsViewModel` (read-only derived),
    `OnboardingViewModel` (single command).
  - **Unconditional for all 10:** atomic mutation only (0 naked assignments
    exist today), exactly-once effect consumption, no repo/DAO access from
    composables, no resources in ViewModels, and the single
    `collectAsState()` in `MainActivity.kt` becomes
    `collectAsStateWithLifecycle()`.
- **Options:**
  - Full MVI everywhere (literal reading of the architecture sub-skill's "MVI
    Contract (Mandatory)") â€” rejected: it contradicts ref 01's explicit
    decision guide ("start MVVM; switch at 3+ interdependent fields / multi-step
    flows") and would rewrite Habits/Settings/Insights/Calendar state for zero
    behavioral gain.
  - MVVM with atomic updates everywhere (AGENTS.md status quo) â€” rejected: the
    same skill marks the MVI contract mandatory for qualifying screens, and
    Today/Creation/Activities already match its trigger while carrying
    multi-method action surfaces and (two of them) untyped message channels.
  - **Chosen:** the criteria-based split above â€” the skill's own criteria, not
    taste, decide screen by screen.
- **Context:** TASK-011 Â§7 Conflict 2, resolved against the actual code: counts
  at decision time were `_state.update` 0, `.update {}` 12, `Channel<` 8,
  `collectAsStateWithLifecycle` 25, `collectAsState()` 1, sealed UiState/Effect
  types 11, `stringResource` 0, literal `Text("` 53. The state layer is already
  consistent derived-flow MVVM with typed effects on five screens; the work is
  therefore *completing* contracts where the trigger fires, not a wholesale
  rewrite.
- **Consequences:** Phase D changes exactly the three MVI ViewModels plus their
  screen call sites, the Habits effect wiring, and their tests (Turbine 1.2.0
  adopted where it replaces hand-rolled flow collection â€” AGENTS Â§8
  justification recorded). Behavior and copy are unchanged; public action
  methods on MVVM screens keep their names. Verification: `testDebugUnitTest`
  + `assembleDebug`, with the Â§8.3 table as the Reviewer's checklist.

## ADR-45 - A dedicated `:baselineprofile` (test) module generates the profile; the generated profile is committed to `app/src/main/baseline-prof.txt` (`saveInSrc` + `mergeIntoMain`, `automaticGenerationDuringBuild = false`); release signing is untouched because the `androidx.baselineprofile` plugin signs its own synthetic build types

- **Decision:**
  - **Module.** A new `com.android.test` module `:baselineprofile` (namespace
    `com.momentum.baselineprofile`, no production code) applies
    `com.android.test` + `androidx.baselineprofile`, points
    `targetProjectPath` at `:app`, and uses
    `testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"`.
    It is the **producer**. `:app` applies the same
    `androidx.baselineprofile` plugin id and adds
    `baselineProfile(project(":baselineprofile"))`; it is the **consumer /
    app-target**. The plugin id is the same in both places because
    `androidx.baselineprofile` is a marker POM whose only dependency is
    `androidx.benchmark:benchmark-baseline-profile-gradle-plugin`, which
    dispatches to three internal sub-plugins (`producer`, `consumer`,
    `apptarget`) based on which AGP plugin is already applied.
  - **Versions.** `androidx-baselineprofile = 1.5.0` and
    `androidx-benchmark-macro-junit4 = 1.5.0` (+ `androidx-benchmark-junit4
    = 1.5.0` for the runner class, which macro-junit4 does **not** bring
    transitively, and `androidx-uiautomator = 2.3.0`, exactly the version
    macro-junit4 1.5.0 requests, + `androidx-test-ext-junit = 1.2.1`).
    `profileinstaller` stays at **1.4.1**: it is a separate release train whose
    latest is still 1.4.1, so there is no coupling to force a downgrade of the
    generation tooling. `androidx.baselineprofile:baseline-profile-junit4` is
    **not used â€” it does not exist** (see Context).
  - **No separate Kotlin plugin in the new module.** AGP 9's built-in Kotlin
    covers it: `TestComponentImpl extends ComponentImpl`, and
    `builtInKotlinSupportMode` is a member of `ComponentImpl`, so every
    `com.android.test` component gets it. ADR-41 therefore holds unchanged â€”
    `org.jetbrains.kotlin.android` is **not** reintroduced and no
    `android.builtInKotlin=false` opt-out is used.
  - **Signing / profileability â€” the decisive point.** The real `release`
    build type stays **unsigned and unmodified**. The
    `androidx.baselineprofile` plugin creates its own *extended* build types in
    `:app` (`BuildTypesKt.createExtendedBuildTypes` +
    `copySigningConfigIfNotSpecified`) and sets, on those synthetic types only,
    `signingConfig = <the module's debug signing config>`, `isProfileable` and
    `isDebuggable = false`. Momentum therefore needs **no `signingConfig`
    line, no keystore and no `benchmark` build type of its own**, and no
    temporary build-file edit that would have to be reverted. This satisfies
    AGENTS.md Â§12 (signing stays Human-in-the-Loop) literally: the shipped
    artifact is untouched, and the debug keystore is used only for a
    throwaway profiling build the plugin owns.
  - **The profile is a committed source file.**
    `baselineProfile { variants.configureEach { automaticGenerationDuringBuild
    = false; saveInSrc = true; mergeIntoMain = true } }` in `:app`.
    `automaticGenerationDuringBuild` stays **false** because it would make
    every `assembleRelease` require a booted, rooted device â€” unacceptable for
    a deterministic, offline build. `saveInSrc` + `mergeIntoMain` write the
    generated profile back into the app's own source tree, where AGP packages
    it into `assets/dexopt/baseline.prof` for **every** variant automatically.
    A build-artifact-only profile is rejected: it would never reach the shipped
    APK.
  - **Journey.** One `BaselineProfileGenerator` class with two
    `baselineProfileRule.collect(...)` blocks (official two-pass guidance):
    a warm-up journey that drives the full tab walk, then a startup journey
    that only cold-starts. The first-run **onboarding** is always present
    (`collect` reinstalls the app per block) and is dismissed with the
    `Skip onboarding` content description, which is present on all four pages
    â€” deterministic and independent of page count.
  - **DSL correction.** The producer-side `baselineProfile { }` block in 1.5.0
    exposes **only** `managedDevices` and `useConnectedDevices`.
    `targetVariant`, `automaticGenerationDuringBuild` and `saveInSrc` are
    *consumer*-side (`:app`) properties; `targetVariant` no longer exists at
    all. Only the two real properties are configured, and only on the side
    that owns them.
- **Options:**
  - Add a hand-written `benchmark` build type to `:app`
    (`initWith(release)` + `signingConfig = signingConfigs.debug` +
    `matchingFallbacks`) â€” **rejected**: it is now redundant (the plugin does
    the same thing on its own synthetic types) and it would be a *permanent*
    edit to the app's signing posture that the product owner never asked for.
  - Override `signingConfig` on the real `release` build type **temporarily**
    for the generation run and revert it afterwards â€” **rejected**: it
    manufactures a signing configuration on the shipping build type, it is a
    mutation that must be remembered to be undone, and it invites an
    invented keystore. The plugin removes the need entirely.
  - Create a keystore under `app/` â€” **rejected**: AGENTS.md Â§12 and the task
    guard forbid it; nothing needs one.
  - `automaticGenerationDuringBuild = true` (profile regenerated on every
    build) â€” **rejected**: requires a booted device at `assembleRelease` time
    and makes the release artifact non-reproducible.
  - Keep the profile as a build artifact under
    `app/build/outputs/â€¦` â€” **rejected**: it is never packaged, so
    `profileinstaller` would again install nothing. This is the exact gap
    TASK-012 exists to close.
  - Pin `androidx-baselineprofile` 1.4.1 to match `profileinstaller` 1.4.1 â€”
    **rejected**: the two are independent release trains, 1.5.0 is current
    stable (2026-09-09) and its own release notes reference "AGP/R8 9.1+", so
    1.5.0 is the generation tooling contemporary with AGP 9. 1.4.1 is recorded
    as the pre-authorized fallback if 1.5.0 rejects AGP 9.0.0.
- **Context:** TASK-012. Live evidence gathered 2026-09-30:
  - `https://dl.google.com/dl/android/maven2/androidx/baselineprofile/group-index.xml`
    â†’ the `androidx.baselineprofile` group contains **only**
    `androidx.baselineprofile.gradle.plugin`, latest/release `1.5.0`,
    `lastUpdated 20260909170753`.
  - The `androidx.baselineprofile.gradle.plugin:1.5.0` marker POM declares
    exactly one dependency: `androidx.benchmark:benchmark-baseline-profile-gradle-plugin:1.5.0`.
  - That plugin JAR's own classes prove the architecture:
    `BaselineProfileProducerExtension` exposes **only** `managedDevices` and
    `useConnectedDevices`; `BaselineProfileConsumerExtension` /
    `BaselineProfileVariantConfiguration` expose `automaticGenerationDuringBuild`,
    `saveInSrc`, `mergeIntoMain`, `filter`, `dexLayoutOptimization`;
    `BuildTypesKt.copySigningConfigIfNotSpecified` +
    `BaselineProfileAppTargetAgpPlugin` (`getSigningConfig`, `setProfileable`,
    `setDebuggable`, `debugSigningConfig`) implement the signing decision above.
  - `androidx.baselineprofile:baseline-profile-junit4` returns 404 for every
    version tried (1.1.0 â€¦ 1.5.0) on both `dl.google.com` and `maven.google.com`,
    and is absent from `group-index.xml` and from `master-index.xml`.
    `BaselineProfileRule` was located instead by unzipping
    `androidx.benchmark:benchmark-macro-junit4:1.5.0` â€” it ships as
    `androidx/benchmark/macro/junit4/BaselineProfileRule.class` in that AAR.
  - `benchmark-macro-junit4-1.5.0.aar` metadata: `minCompileSdk=34`,
    `minAndroidGradlePluginVersion=8.1.1`, `minSdkVersion=24` â€” all satisfied by
    Momentum's compileSdk 36 / AGP 9.0.0 / minSdk 26. Its POM pulls
    `kotlin-stdlib 2.1.20` (below the project's 2.2.10 floor, so no downgrade)
    and `uiautomator 2.3.0`.
  - `gradle-9.0.0.jar`: `com/android/build/api/component/impl/TestComponentImpl.class`
    extends `ComponentImpl`, which owns `builtInKotlinSupportMode`.
  - App-side facts confirmed by reading the code: `MainActivity` is the
    `MAIN`/`LAUNCHER` activity; the four bottom tabs are Home, Habits, Tasks,
    Insights (`R.string.nav_home` / `nav_habits` / `nav_tasks` / `nav_insights`,
    each a `Text` inside a clickable `LiquidGlassTab` column);
    `OnboardingGlassSkipButton` exposes `contentDescription =
    R.string.cd_skip_onboarding` = "Skip onboarding" on every page and calls
    `vm.finish(false, onDone)`, which is the only deterministic, single-step
    route to Today.
- **Consequences:** a `:baselineprofile` module, one new test source, and two
  small build-file edits (`:app` plugins + one dependency + one
  `baselineProfile { }` block, plus the version catalog). **No product
  behavior, UI, string, resource, navigation, DI, schema or ProGuard change**;
  `release` remains unsigned; no keystore; `git status` gains only the new
  module and `app/src/main/baseline-prof.txt`. A new build type family appears
  in `:app` (owned by the plugin, visible only in the IDE variant list) â€” it
  does not affect `assembleDebug` or `assembleRelease`. Because the profiled
  app variant is generated by the plugin, the Developer must **report the
  exact build-type name the plugin created** so the Reviewer can confirm the
  profiled artifact is a release-derived one and not a debug build. Profile
  freshness is now a deliberate act: re-run `:app:generateBaselineProfile` on a
  booted device whenever the startup/navigation journey changes, and commit the
  result â€” the same discipline as the R8 keep rules from TASK-011 Phase F.

## ADR-46 - The Phase 2B four-direction exploration is abandoned and deleted in full; the app's live design direction is Aurora-as-already-built in `presentation/**`, not `exploration/phase2b/aurora`

- **Supersedes:** ADR-40 and ADR-40 Addenda 1-2, in one respect only - the
  *deletion trigger*. ADR-40 said the prototypes "are deleted by removing two
  directories **when a direction is chosen**". The human has ruled that the four
  Taste-driven UI direction explorations (A *Press*, B *Instrument*, C *Loom*,
  D *Aurora Governed*) are obsolete and are removed **without a direction being
  chosen from among them**. Every other ADR-40 clause - package location,
  debug-source-set-only entry point, content-only shared fixture, per-direction
  typography, derived-not-quoted colour values, Light-only palettes, the
  `MOMENTUM_CONCEPT_EXPLORATION.md` authority, and `ISSUE-006` staying OPEN -
  stands unedited. This entry is **append-only**: no ADR-40 text is rewritten or
  removed.

- **Decision:**
  - **1. `exploration/phase2b/aurora/**` is exploration (i), not the current
    design direction (ii), and is deleted.** The name collision is resolved on
    package location, not on the word "Aurora". Evidence that the Round-1 D
    prototype is *not* the production design: it is reachable only from the
    debug-source-set `Phase2BPreviewActivity`; it is a **whole Home screen**
    (header, sections, bottom navigation - `AuroraHome.kt:69`) built on its
    **own** `AuroraTokens.kt` `CompositionLocal`, not on
    `presentation/theme/Color.kt`; it consumes `R.drawable.habits_hero_light`
    read-only at `AuroraHeader.kt:132`; and it is staged in git as `A ` (added
    in TASK-009, never released). The **production** Aurora direction is an
    entirely different set of files, none of which imports anything from
    `exploration/phase2b/**`: `presentation/aurora/MomentumAurora.kt`
    (`AuroraBackground`, `AuroraState`),
    `presentation/components/MomentumHeroArtwork.kt`
    (`momentumHeroArtworkRes`, `MomentumHeroArtwork`, lines 43/82),
    `presentation/screens/today/HomeHero.kt:109`,
    `presentation/screens/habits/HabitsHero.kt:70`,
    `presentation/theme/Color.kt` (`AuroraLight`/`AuroraDark` and the
    `theme_id = "aurora"` registry), plus the bundled
    `res/drawable/habits_hero_light.webp` and `habits_hero_dark.webp`.
    **All of that is RETAINED untouched.** ADR-40's own Addendum framing called
    D's "purple everywhere" treatment a **Round-1-only bounded exception** that
    had to be re-litigated if D shipped; D never shipped and is now deleted, so
    the re-litigation obligation dies with it. The current design direction
    survives because it lives outside the exploration package, not despite this
    deletion.
  - **2. Deletion is whole-package, plus the debug source set it required.**
    Delete the entire `app/src/main/java/com/momentum/app/presentation/exploration/`
    tree (15 files: all four directions + `fixture/` + `preview/`) and the entire
    `app/src/debug/` tree (`AndroidManifest.xml` +
    `Phase2BPreviewActivity.kt`). `app/src/debug/` was created *solely* for
    ADR-40 - verified: it contains nothing else - so removing it whole is what
    ADR-40's "and the debug source set" and `ISSUE-026`'s recorded fix already
    prescribe. AGP supplies an empty debug manifest when `app/src/debug` is
    absent; no Gradle change is required or permitted.
  - **3. No resource is deleted; there are none to delete.** ADR-40's optional
    `res/drawable-nodpi/phase2b/**` was **never created** (verified: no such
    directory exists). The only `res/` entries the prototypes touch are
    `R.font.manrope` (shared with `presentation/theme/Type.kt`) and
    `R.drawable.habits_hero_light` (a production asset also used by
    `MomentumHeroArtwork.kt` and `HabitsHero.kt`). This independently confirms
    the TASK-013 finding that `app/src/main/res` has zero unused resources:
    nothing there is exclusive to the prototypes, so nothing there may be
    removed.
  - **4. `proguard-rules.pro` needs no edit.** Lines 30-31 are a **comment**
    explaining that `exploration/phase2b/**` is intentionally *not* kept. A stale
    comment is not a defect, and R8 has no rule for a package that no longer
    exists. Correcting it is out of scope; report it separately if desired.
  - **5. `design/aurora-concept/**` and `MOMENTUM_CONCEPT_EXPLORATION.md` are
    RETAINED, untouched.** *(Verdict as of ADR-46. `design/aurora-concept/**`
    was subsequently removed from the repository by a later human ruling — see
    "ADR-46 Correction Note 2"; `MOMENTUM_CONCEPT_EXPLORATION.md` is still
    present and still retained.)* `ISSUE-024` is **CLOSED** with a *retain*
    resolution
    and the human's decision named neither artefact.
    `MOMENTUM_CONCEPT_EXPLORATION.md` remains, per ADR-40, the repository's sole
    source of visual truth for concept work, and `ISSUE-025` still cites it.
    Neither is a Phase 2B direction exploration.
  - **6. `docs/phase2b/round1/*.png` are RETAINED as historical evidence.** They
    are render evidence, not explorations, and they are the only proof backing
    `PROJECT_STATUS.md`'s `FULLY_VERIFIED` verdict for `F-CONCEPT-2B` and the
    findings cited by `ISSUES.md` (`ISSUE-025`, `ISSUE-026`, `ISSUE-027`).
    Deleting them would leave live citations dangling.
  - **7. Zero cross-package code references exist, so deletion cannot break the
    build.** A repository-wide search for `phase2b`, `Phase2B`, and every public
    symbol of the four directions (`PressHome`, `PressTokens`, `PressMaterials`,
    `PressDepthStrategy`, `InstrumentHome`, `InstrumentTokens`,
    `InstrumentChannel`, `LoomHome`, `LoomTokens`, `LoomWeave`, `AuroraHome`,
    `AuroraTokens`, `AuroraHeader`, `Phase2BFixture`, `ProtoDay`, `ProtoToday`,
    `ProtoActivity`, `ProtoCategory`, `ProtoTracking`, `ProtoDirection`) returns
    hits **only** inside `exploration/phase2b/**` itself, inside
    `app/src/debug/**`, and in prose (`ISSUES.md`, `PROJECT_STATUS.md`,
    `task.md`, `MODERNIZATION_AUDIT.md` *(an input working document, since
    deleted — see "ADR-46 Correction Note 2")*, `DECISIONS.md`, and the
    `proguard-rules.pro` comment). **No production source, no test, no resource,
    no manifest and no Gradle or catalog file references this package.**
    `presentation/theme/DesignSystemPreviews.kt` references
    `presentation.aurora.AuroraBackground`/`AuroraState` - production, not
    `phase2b` - and contains nothing that exists for these directions.
    (`presentation/components/HeroAuroraPreview.kt` declares a production preview
    named `HeroAuroraHeader`, a different symbol in a different package, retained
    here.)
  - **8. Issue consequences.** `ISSUE-026` (prototypes ship in release as dead
    code): its accepted cost is **eliminated** - recommended **CLOSED**;
    `isMinifyEnabled = false` no longer has unreachable prototype classes to
    carry. `ISSUE-027` (B renders `Read 20 pages - p - pages`): the defect lives
    only in `InstrumentHome.kt:217-230`, which is deleted - the issue has **no
    remaining subject**; recommended **CLOSED as resolved-by-removal**, not
    "fixed". `ISSUE-025` (every phase2b hex/`sp` is derived, not quoted): its
    verification criterion was "ratify the derived palette as real design tokens
    **when a direction is selected**". No direction is selected and the
    prototypes are gone, so the criterion can never be met - recommended
    **CLOSED as moot**, with the disclosure preserved as historical record
    rather than as a live production risk. `ISSUE-006` (Aurora artwork
    programmatic vs bundled): **unaffected and still OPEN**. ADR-40 had adopted
    the bundled side of that question *provisionally for the prototype only*;
    deleting D removes that provisional adoption with it. `ISSUE-006` now
    concerns **only** production `HomeHero.kt` / `HabitsHero.kt` /
    `MomentumHeroArtwork.kt` and still requires a human ruling.
    `PROJECT_STATUS.md`'s `F-CONCEPT-2B` row and the `ISSUES.md` registry are
    **Orchestrator-owned** and are not edited by this decision.
  - **9. No APK-size benefit is claimed.** Release is `isMinifyEnabled = false`,
    so the prototypes did ship in the release APK; but the 4,237,684 B APK
    measured in TASK-013 is dominated by four onboarding WebPs (1,519,994 B), two
    hero WebPs (372,250 B) and `manrope.ttf` (164,700 B). The Developer must
    **measure** before and after and report the real delta, including `0` if that
    is the result.

- **Context:** The human's ruling is the governing requirement (AGENTS.md preamble,
  §2 source-of-truth order). ADR-40 anticipated exactly this removal ("Removal
  cost when a direction is selected: delete two directories and the debug source
  set"); the only difference is the trigger, so an amendment - not a rewrite - is
  the correct instrument. `PROJECT_STATUS.md` records that no direction was
  selected, ranked or recommended and that Round 2 never started, so "abandoned"
  and "a direction chosen" coincide here without any production Aurora having
  been adopted from the prototypes.

- **Consequences:** Removed: `presentation/exploration/phase2b/**` (15 files),
  `app/src/debug/AndroidManifest.xml`, and
  `app/src/debug/**/Phase2BPreviewActivity.kt` (and therefore the whole
  `app/src/debug` source set). Unchanged and provably so: every production source
  file, `app/src/main/AndroidManifest.xml`, all of `app/src/main/res`,
  `presentation/theme/**`, `presentation/aurora/**`,
  `presentation/components/**`, all build and version-catalog files, the Room
  schema, the baseline profile, `docs/**`, `design/**`, and all test sources.
  Reintroducing a future direction exploration must be a **new** ADR that restores
  an isolation package, an entry-point strategy and a `res/` budget; this ADR
  does not pre-authorise any of that.

### ADR-46 Correction Note 1 — false release-minification premise (Architect, 2026-09-30)

- **Instrument:** this note is **append-only**. No ADR-40 text, no ADR-40 Addendum,
  no ADR-45 text and no ADR-46 text above is rewritten, deleted or silently
  restated. The incorrect sentences remain in place as the historical record of
  what was decided; this note supersedes their *stated reason* only.

- **What was wrong.** ADR-46 §8 and §9 restated, as current fact, the premise that
  `release` is `isMinifyEnabled = false` and that the prototype composables were
  therefore shipped in the release APK as live dead code. **That premise is
  false.** `app/build.gradle.kts:33-46` sets, in the `release` build type,
  `isMinifyEnabled = true` and `isShrinkResources = true` (comment: *"Phase F: R8
  full mode (AGP 8+ default) + resource shrinking"*), with
  `proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
  "proguard-rules.pro")`. Release has been minified and resource-shrunk **since
  TASK-011 Phase F** — `app/proguard-rules.pro:1` (*"Momentum R8 / ProGuard rules
  (Phase F)"*), and referenced by ADR-45 as *"the same discipline as the R8 keep
  rules from TASK-011 Phase F"*.

- **Direct evidence that the dead-code cost had already ceased.** TASK-011 Phase F
  verification, `task.md` §8, item 7: *"`phase2b/**` is deliberately not kept
  (ADR-40): **47 phase2b classes appear in the release `usage.txt` as removed and
  0 in `mapping.txt`**, which is the intended outcome."* R8 stripped the entire
  unreachable prototype graph from the release artifact at Phase F — before
  TASK-014 and before ADR-46 existed.

- **Corrected reading of ADR-46 §8.** `ISSUE-026` ("prototypes ship in release as
  unreachable dead code") describes a cost that was eliminated at TASK-011 Phase
  F, not by ADR-46. The reason its accepted cost is gone is R8, not deletion. What
  ADR-46's deletion removes is the **source** — 15 files under
  `presentation/exploration/phase2b/**` — and the **debug-only entry point**
  (`app/src/debug/**`). That is a real and permanent benefit: it removes the
  prototype *risk* (a design direction that reads as production, a
  `preview/`-reachable path back into an abandoned exploration) and the
  maintenance surface, rather than any release bytes.

- **Corrected reading of ADR-46 §9.** The conclusion *"No APK-size benefit is
  claimed"* **stands unchanged and remains correct.** Only its stated reason is
  wrong. The measured **0-byte** delta is not explained by "an unminified APK
  dominated by untouched onboarding/hero WebPs and `manrope.ttf`" — those asset
  sizes are real and unaffected, but they were never the operative variable. The
  0-byte delta is the **expected and correct** result precisely because R8 had
  already stripped the 47 classes, so there were no prototype bytes left to
  remove. `0` is therefore **confirmation of the corrected premise**, not an
  anomaly to be explained away, and must not be characterised either as a
  benefit or as a disappointment.

- **Why the error is not recorded as a defect against ADR-40.** ADR-40's
  "Accepted cost" bullet (the sentences stating that the prototypes "do ship
  inside the release APK as unreachable dead code, because … the release build
  has `isMinifyEnabled = false`") was **accurate when ADR-40 was written**
  (TASK-009, before TASK-011 Phase F turned R8 on) and was a truthful disclosure
  of a then-real constraint. It is stale, not wrong, and is left verbatim. This
  note is the amendment that closes the staleness: the cost it disclosed has not
  existed since Phase F.

- **Effect on the recommendation: none.** This correction changes the **reason**,
  not the **decision**. Everything else in ADR-46 stands exactly as written: the
  four-direction exploration is abandoned and deleted in full; no direction is
  selected and Round 2 does not proceed; `ISSUE-026` remains
  **CLOSED as resolved-by-removal**; `ISSUE-027` remains **CLOSED as
  resolved-by-removal**; `ISSUE-025` remains **CLOSED as moot** with the
  disclosure retained as historical record; `ISSUE-006` remains **OPEN**, still
  requiring a human ruling, now scoped only to production `HomeHero.kt` /
  `HabitsHero.kt` / `MomentumHeroArtwork.kt`; `ISSUE-024` and all retained
  artefacts (`docs/phase2b/round1/*.png`, `design/aurora-concept/**`,
  `MOMENTUM_CONCEPT_EXPLORATION.md`, `MODERNIZATION_AUDIT.md`) are unaffected.
  The Orchestrator-owned artifacts agree: `ISSUES.md` `ISSUE-026` already records
  the premise as invalidated at Phase F, and `PROJECT_STATUS.md` `F-CONCEPT-2B`
  already carries the same caveat. No `Orchestrator`-owned file is edited here.

- **No code, build, Gradle, ProGuard, resource or manifest change accompanies this
  correction.** It is a documentation correction only; the working tree after it
  contains no build-relevant delta.

### ADR-46 Correction Note 2 — deleted input working documents and the removed `design/aurora-concept/` pack (Architect, 2026-09-30)

- **Instrument:** this note is **append-only**. No ADR-40, ADR-41, ADR-42,
  ADR-43, ADR-45 or ADR-46 text above is rewritten, deleted or restated, and
  Correction Note 1 is left verbatim. Only two *additive* parenthetical
  annotations were placed in ADR-46 (§5 and §7), each pointing here, because
  those two lines would otherwise have read as false current claims. This note
  supersedes their **provenance framing only**.

- **What changed, and why nothing decided here changes.** Three artefacts that
  existed at decision time no longer exist. All three were **inputs to decisions
  that are already permanently recorded in ADRs**, never sources of truth in
  their own right, so no decision, no rationale and no recorded consequence is
  affected. Each was removed by explicit human ruling, not by an architecture
  decision, and no new ADR is created.

- **1. `MODERNIZATION_AUDIT.md` — DELETED (input working document).** It was
  self-declared *"audit and recommendation only"*. It was an **input** to
  TASK-011/ADR-41…ADR-46; its conclusions are permanently recorded in those ADRs
  and in `task.md` §8, which are the authority. Its recorded baseline (AGP
  8.7.3 / Navigation 2 / no Hilt / 253 tests) is **superseded** by the
  implemented modernization, committed as `f3fa73c`. Nothing in this file now
  depends on it. Reference sites: ADR-46 §7 (annotated in place above) and
  Correction Note 1's "retained artefacts" bullet (superseded by this note).

- **2. `TASTE_MODERNIZATION_TARGET.md` — DELETED (input working document).** It
  was self-declared *"EXTRACTION ONLY — nothing in this document has been
  implemented"* and was fully implemented thereafter. `DECISIONS.md` contains
  **no** reference to it at any point; this entry records that absence as
  deliberate so a later reader does not treat the deletion as a missing
  reference.

- **3. `design/aurora-concept/**` — REMOVED from the repository.** This
  directory existed when ADR-46 was decided and ADR-46 §5 correctly retained it
  *as of that decision*; it was removed afterwards, which is why §5 now carries
  the annotation above. Character of the pack, for the historical record: a
  non-runtime **web font concept pack** (`fonts.css` + seven `.woff2`, Manrope
  subsets plus one `Material Symbols Rounded` variable font), never referenced
  by any Kotlin, resource, Gradle, manifest or runtime path. It was **never
  consumed by the Android application at any point**, so its removal changes no
  runtime behaviour and no APK content. **Production Aurora is untouched**:
  `presentation/aurora/MomentumAurora.kt`,
  `presentation/components/MomentumHeroArtwork.kt`, `HomeHero.kt`,
  `HabitsHero.kt`, `presentation/theme/Color.kt` and the bundled
  `res/drawable/habits_hero_light/dark.webp` all remain, unchanged. The
  canonical status of `ISSUE-024` lives in the Orchestrator-owned `ISSUES.md`
  and is not restated or edited here.

- **Effect on ADR-46's decisions: none.** The four-direction exploration remains
  abandoned and deleted in full; no direction is selected and Round 2 does not
  proceed; `ISSUE-026` remains **CLOSED as resolved-by-removal**;
  `ISSUE-027` remains **CLOSED as resolved-by-removal**; `ISSUE-025` remains
  **CLOSED as moot**; `ISSUE-006` remains **OPEN** and still requires a human
  ruling, still scoped only to production `HomeHero.kt` / `HabitsHero.kt` /
  `MomentumHeroArtwork.kt`. Of the artefacts named in Correction Note 1's
  "retained artefacts" enumeration, `docs/phase2b/round1/*.png` and
  `MOMENTUM_CONCEPT_EXPLORATION.md` **are still present and still retained**;
  `design/aurora-concept/**` and `MODERNIZATION_AUDIT.md` **no longer exist**.
  The enumeration is superseded in those two entries only; the sentence's
  substance about the issues above stands.

- **No code, build, Gradle, ProGuard, resource, manifest or data change
  accompanies this note.** It is a documentation correction only; the working
  tree after it contains no build-relevant delta.

