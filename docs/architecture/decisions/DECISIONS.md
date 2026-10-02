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

## ADR-47 - Home is a focused Today surface; the Home Hero is simplified in place (one Aurora-backed surface, one constant readability scrim, one bottom hand-off, content-driven height)

- **Decision:**
  - **1. Home's one job is Today.** Home answers exactly one question - "what
    matters for me today, and what should I do next?" - as a single scroll of
    today's progress plus today's actionable rows. It carries no analytics, no
    history, no habit library, no management controls and no calendar. This
    restates the locked design language (`MOMENTUM_DESIGN_SYSTEM_V1.md` §13) and
    is the test every later Home change must pass. Home is *not* a dashboard.
  - **2. The Hero keeps ADR-31's identity and outcomes but drops the ADR-34/35
    machinery.** Home remains **one Aurora-backed surface** (ADR-31) whose
    artwork owns the physical window top edge (ADR-33/37/39), inside **one
    scroll surface** with the clamped-px + `NestedScrollConnection` collapse
    (ADR-28/29), and the date/greeting stay top-anchored per the approved
    product decision (`DECISIONS` §5) and DS §12. Within that, the five-anchor
    48-sample piecewise readability veil (ADR-35) is replaced by **one constant
    readability scrim**: a single page-background gradient, transparent at the
    Hero's top edge, rising to one capped peak behind the header and the
    progress block, then handing off to the artwork's bottom dissolve. Removed:
    the ring's scroll-driven scale, the artwork parallax/overscan, the open-band
    valley, the `Veil*` anchor constants, the 24-sample bottom-blend S-curve, the
    `VeilSteps`/`BottomBlendSteps` sample counts, and the fixed empty-day
    `CollapseHeadroom`/weighted spacer that produced the void. **Retained**: the
    screen-space top mask (ADR-35; it prevents copy entering the camera cutout as
    the Hero scrolls under the status bar) and the artwork's bottom hand-off
    (ADR-34/39; it prevents a visible seam). Each retained layer still prevents
    an *observed* defect; each removed layer only re-implemented a readability or
    seam outcome that the single scrim + dissolve already provide.
  - **3. The empty day is a compact Aurora header, not a void.** When
    `TodaySections.totalCount == 0` the Hero renders date + greeting + Settings
    only - no progress block, no collapse headroom, no weighted spacer - so the
    artwork band is content-height instead of ~300 dp (audit §8.3). The
    no-activity state (existing copy + one add CTA) sits immediately beneath it,
    and the Quick Add FAB is suppressed on the empty day so there is exactly one
    add affordance. `HomeEmptyState` drops its `Star` glyph (the same glyph is
    `momentumIcon`'s corrupt-key fallback, audit §17.3h); the empty state renders
    without a Material glyph and lets the CTA carry the action.
  - **4. Home's relationship to Tasks and Insights is documented, not redrawn.**
    Home is the *act today* surface; the Tasks tab stays the shared
    management/filter surface (`ActivitiesScreen`); Insights stays
    history/analysis. Bottom-nav composition (ISSUE-002) and the existence of the
    Tasks tab (audit Q8, a **product** question) are **not** changed here.
  - **5. The loading state is content-aware.** The bare
    `Text("Loading today…")` (`TodayScreen.kt`) is replaced by a static skeleton
    of the Hero header + two ghost rows built from existing theme surface tokens
    (DS §25). It is decorative to TalkBack and carries a single "Loading today"
    label. No shimmer is required, so it costs no animation.
  - **6. One FAB.** `QuickAddFab` (Home) and `AddHabitFab` (Habits) - two
    identical-purpose controls with opposite materials and glyph sizes (audit
    §9.4) - become one shared `MomentumFab`: the solid Aurora-filled disc with
    the `prominent` (28 dp) glyph, matching DS §9's primary-button language and
    DS §7's allowance of glass for *floating controls only*. This is a
    strictly-required shared-component contract and the **only** cross-screen
    touch in this task; `HabitsScreen`'s FAB call site adopts it and nothing else
    on Habits changes.
  - **7. ISSUE-020 is fixed at the ink, not the veil.** Hero copy uses
    `textPrimary` throughout; the Hero places **no** `textSecondary` over
    artwork. This follows the recorded remedy (the text token, not another dose
    of veil, audit §7.20/§8.2 KDoc note) and clears the 4.5:1 floor by
    construction in both themes.
  - **8. Hero spacing is expressed with existing tokens.** The Hero's private
    layout vocabulary is reduced to only the constants that cannot map to
    `MomentumSpacing`; every remaining value carries a one-line derivation
    comment. This answers audit §8.1's "the Hero invented a private spacing and
    alpha vocabulary" without a global token expansion.
- **Context:** `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` §8.1-§8.3, §7.22 (Home severity
  `REIMAGINE`), §9.4, §9.5, §17.3h, and HO-4; the Home Hero's own KDoc names
  `HabitsHero` "the reference composition" and the audit confirms it (222 lines
  vs 771, one dissolve, no masks, no anchors). Soft Aurora, the locked design
  system, and AGENTS §5/§9 all require that readability be achieved without
  flattening the artwork and without inventing per-screen constants. The task's
  binding requirement is the human brief in `task.md` §12, which explicitly asks
  for the Hero contract to be decided and the empty-day void removed.
- **Options:** (A) keep the 771-line Hero untouched and only fix the number;
  (B) adopt `HabitsHero` wholesale, moving all Home copy to a single
  bottom-anchored block and deleting the scrim and the screen-space mask; (C)
  simplify in place - keep Home's approved top header + progress-below structure,
  replace the sampled veil with one constant scrim, and keep the two layers that
  still prevent observed defects (chosen); (D) withhold the artwork entirely and
  use a solid header.
- **Chosen:** **C**. (A) leaves the audit's central Home finding unresolved and
  carries a 0.12-alpha safety margin; (B) conflicts with the approved product
  decision `DECISIONS` §5 and DS §12 that keep the greeting at the top, and
  deleting the screen-space mask would reintroduce the camera-cutout defect
  ADR-35 fixed; (D) discards the product's signature atmosphere and the
  `MomentumHeroArtwork` pipeline for no measured benefit.
- **Consequences:** Changed: `presentation/screens/today/HomeHero.kt` (reduced
  to the constant-scrim composition, empty-day content height, no ring scale /
  parallax / valley / anchors / S-curve), `TodayScreen.kt` (empty-day rule,
  loading skeleton, hidden FAB on empty, no star glyph), new
  `presentation/components/MomentumFab.kt`, `HabitsScreen.kt` (FAB call site
  only), `res/values/strings.xml` (loading/empty-mark strings). **Supersedes in
  mechanism** ADR-34's dispersion of readability and ADR-35's sampled piecewise
  veil; **preserves in outcome** ADR-31 (one Aurora-backed surface), ADR-32/34
  (no dead space below the copy; artwork-to-section hand-off), ADR-33/37/39
  (artwork at the window edge, no top fade, full-strength head through the
  status-bar band). No domain, repository, schema, notification, dependency or
  navigation change. Contract tests that pinned the removed machinery
  (`HomeHeroAssetsTest`, `HomeHeroCompositionTest`) must be rewritten to the
  retained outcomes (one surface, no top band of page background, no dead space,
  one bottom hand-off) rather than deleted.

## ADR-48 - Home shows today's progress exactly once, and the 100% moment is a real (restrained) reward

- **Decision:**
  - **1. One progress representation, at a size that reflects its importance.**
    Today's progress appears **once** on Home: the fraction `done / total`,
    rendered in a numeric figure style based on the existing **Display token
    (40/48, 600)** - the largest token in the locked scale - with the caption
    **"completed today"** beneath it (DS §14). `ProgressOrb` becomes the
    visualization only: it is composed with `showPercent = false`, so there is no
    second percentage. At 100% the figure is replaced by the statement **"Today
    is complete"** (DS §14) with the ring full, so nothing is shown twice. On the
    empty day there is no progress block at all (ADR-47). This ends the current
    inversion where the sentence that answers the screen's own question is body
    text, smaller than the greeting above it (audit CP-1/§8.2).
  - **2. A derived figure style, recorded as derived.** A single
    `MomentumFigureDisplay` `TextStyle` is added to the type layer
    (`presentation/theme/Type.kt`) so screens do not construct arbitrary
    `TextStyle`s (DS §34). It is the Display token plus
    `FontFeatureSettings("tnum")` when Manrope exposes tabular figures;
    otherwise it is the Display token unchanged and the figure is centred, so a
    changing value does not visibly reflow. **Derivation basis:** the Display
    token (40/48/600) is specified in DS §4; tabular figures are a
    *demanded-not-specified* refinement whose availability must be **verified at
    implementation** (Manrope is a variable font; its OpenType feature tables
    have not been inspected). No new font weight is registered: 600 is the
    scale's Display weight. The 700-weight-registration question belongs to the
    separate numeric-register decision (audit HO-1) and is *not* taken here.
  - **3. The completion moment uses the hooks that already exist.** On the
    `0 -> 1` transition, Home raises the transient `AuroraState.MILESTONE` for
    `MomentumMotion.reward` (650 ms), runs a one-shot ring glow for the same
    duration, and fires **one** `HapticFeedbackType.Confirm`. The snackbar
    ("All done - momentum secured.") is removed - the Hero is already stating
    "Today is complete", and the same news twice within 200 px is noise (audit
    CP-2/§7.8.13). `AuroraState.MILESTONE` and `MomentumMotion.reward` become
    reachable; the steady 100% state is `AuroraState.COMPLETION`. No particles,
    no confetti, no sound (DS §19/§21/§33); the moment is "calm and rewarding,
    not loud". Under reduced motion the transient is skipped and the figure /
    statement change instantly, while the haptic still fires (feedback, not
    motion).
  - **4. The motivational filler slot is removed from Home.** The Hero carries
    one idea: today's progress. The six `today_message_*` branches - four of
    which describe a *bucket* of state and one of which is the product tagline
    used as a daily line (audit §17.3f) - are removed from
    `TodayViewModel.messageFor`, and the Home Hero renders no supporting line.
    The **list**, not a sentence, is the "what next".
  - **5. Home rows do not carry streaks.** Streaks are supporting information
    (DS §20); `StreakBadge`'s fire glyph is a rejected pattern (DS §33,
    ISSUE-005). Home's rows pass `streak = 0`, suppressing the badge through the
    component's own parameter, so the rejected mark leaves Home with no new
    component work; streaks remain on Habit Detail/Insights, where ISSUE-005's
    visual treatment is decided separately. (Not in scope: changing
    `MomentumBadges.kt` itself.)
  - **6. Motion is change-triggered, not wall-clock.** Home runs **no**
    permanent infinite animation: the Aurora is static (state is expressed
    through its intensity) and `ProgressOrb` is composed with
    `glowEnabled = false` except during the one-shot milestone. Row reflow keeps
    `Modifier.animateItem`; the progress sweep settles on value change. Home
    adopts `MomentumMotion.standard` for reflow/sweep and `MomentumMotion.reward`
    for the milestone rather than the current inline literals (audit §7.15.2/4).
    Reduced motion nulls every decorative spec, exactly as Home already does
    (ISSUE-022 remains a whole-app gap; Home stays correct).
- **Context:** `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` CP-1, CP-2, §7.8.13, §7.13,
  §17.3f, §19.11, §20 (Home weakness 1/2/4/6/8/10), HO-1, HO-3; DS §4/§14/§19/
  §20/§21/§25/§27/§28; AGENTS §9 (meaningful completion micro-interactions,
  never force animation). The human brief (`task.md` §12) names the inverted
  numeric hierarchy, the snackbar 100% moment and the filler line as concrete
  defects the concept must resolve.
- **Options:** (a) keep the percentage in the ring and the fraction at 16 sp
  (status quo); (b) make the percentage the sole headline and drop the fraction;
  (c) make the fraction the single Display-size headline with the ring numeric
  removed (chosen); (d) express the day as a shape rather than a number (audit
  §19.8 - deferred, it needs a new mark primitive, HO-2).
- **Chosen:** **c**. (a) is the defect; (b) makes the user count "3 of 5"
  themselves and loses the DS §14 fraction; (d) is a component-family decision
  (HO-2) that would touch Insights/Calendar/Habit Detail and is out of this
  task's scope.
- **Consequences:** Changed: `HomeHero.kt` (figure/label/statement,
  `ProgressOrb(showPercent = false, glowEnabled = false)`, milestone transient),
  `TodayScreen.kt` (milestone mapping, `AuroraState` mapping, no snackbar, no
  message, streak = 0 on rows, token adoption),
  `TodayViewModel.kt` (remove `messageFor` filler and the message from
  `TodayUiState.Success`), `presentation/theme/Type.kt`
  (`MomentumFigureDisplay`), `res/values/strings.xml` (add "completed today" /
  "Today is complete"; remove the `today_message_*` and
  `today_celebrate_all_done` strings that are no longer referenced).
  `TodayEffect.Celebrate` is retained as the one-shot signal but its payload is
  no longer surfaced as a snackbar. No domain, repository or schema change.
- **Flagged product conflict (must reach the Orchestrator).** Removing the Home
  message slot makes the Settings toggle **"Motivational messages - Gentle
  encouragement on Today"** inert: `AppSettings.motivationalMessagesEnabled`
  would no longer affect Home. This ADR **does not** change Settings (out of
  scope); the conflict is recorded so the disposition of that setting - repurpose
  its copy, move it, or remove it - is a **product** decision for the
  Orchestrator, exactly as the reduce-motion copy was handled under CP-3. Until
  it is resolved the toggle must not be presented as affecting Home.

### ADR-48 Amendment Note 1 - the encouragement line is retained (human ruling) with state-specific content; `MomentumMotion.reward` is added (Architect, 2026-09-30)

- **Instrument:** this note is **append-only**. No ADR-47 or ADR-48 text above is
  rewritten, deleted or restated. This note supersedes ADR-48 **§4 only** (the
  removal of the Home message slot) and corrects one factual premise in ADR-48
  §3 (`MomentumMotion.reward`). Every other ADR-48 clause stands exactly as
  written.

- **What changed, and why.** The human ruling recorded in `task.md` §7 (product
  ruling, 2026-09-30) is the governing requirement (AGENTS.md source-of-truth
  order). It directs that the Home encouragement line is **retained**, **ON by
  default**, controlled by the existing `AppSettings.motivationalMessagesEnabled`
  toggle, and that when disabled it is removed **completely with natural layout
  reflow - no reserved/empty space and no placeholder**; the ON and OFF
  compositions must each be independently intentional. ADR-48 §4 chose the
  audit's "remove the slot" option; the ruling chooses the audit's other offered
  option ("a message slot that can only be filled with something specific") and
  therefore **supersedes §4**. ADR-48 §4's diagnosis - the six generic
  `today_message_*` branches are filler and one is the product tagline used as a
  daily line - remains correct and is the reason the content is redesigned, not
  restored.

- **Redesigned content (state-specific; the ruling's actual requirement).** The
  line is now a pure function of the day's live state and has exactly two forms:
  1. `sections.allComplete || sections.isEmpty` -> **no line** (the Hero headline
     "Today is complete" / the empty state already state it).
  2. Any remaining item with `currentStreak >= 2` -> **"Keep your {n}-day streak
     going."**, where `{n}` is the greatest `currentStreak` among
     `TodaySections.remaining` (tie -> first in schedule order).
  3. Otherwise (`remaining.isNotEmpty()`) -> **"Next up: {title}"**, where
     `{title}` is `remaining.first().activity.title` (the highest-priority
     remaining item, i.e. the schedule order `GetTodayScheduleUseCase` already
     produces).
  No branch describes a bucket of state, repeats the product tagline, or
  duplicates the DS §14 fraction. Because Home rows no longer carry
  `StreakBadge` (ADR-48 §5), form 2 is the **only** streak reference on Home and
  appears only when a streak is genuinely at stake - which is the design system's
  "streaks are supporting information, not the emotional center" (§20) expressed
  once instead of N times. Form 3 directly answers the screen's second question
  ("what should I do next?") by naming the next item.

- **"One representation" is preserved.** ADR-48 §1 is untouched: neither form
  carries a progress number (a streak count is a different quantity; an item
  title is not a count), so the fraction remains the single progress
  representation and the ring still carries no numeric (`showPercent = false`).

- **The previously flagged product conflict is resolved by the ruling.** The
  Settings toggle is functional again; `AppSettings.motivationalMessagesEnabled`
  once more changes Home. **No Settings change is required or made** - the
  toggle, its copy and its persistence already exist and are now truthful. The
  §"Flagged product conflict" bullet above is closed by this note; its historical
  text is left verbatim.

- **Factual correction to ADR-48 §3 (token existence).** ADR-48 §3 and the
  audit's CP-2/HO-3 refer to `MomentumMotion.reward` (650 ms) as an existing
  unused token. **It does not exist.** A search of
  `app/src/main/java/com/momentum/app` for `reward`/`Reward` returns **zero**
  matches; `presentation/theme/Motion.kt` currently declares only `fast` (180),
  `standard` (280), `emphasis` (450), `ambient` (6000) and `easeOut`.
  `AuroraState.MILESTONE` **does** exist (`defaultIntensity = 1f`) and is
  unreachable in production, as the audit says. Therefore the completion moment
  requires **adding** `MomentumMotion.reward = 650`, derived from the locked
  design system §28's "Reward ~650 ms" token - not inventing a value. This is a
  token addition, not a new architectural decision, so no new ADR is created.
  The same caution applies to `HapticFeedbackType.Confirm`: the repository uses
  only `LongPress` and `TextHandleMove` today, so its availability in the pinned
  Compose version must be verified at implementation; if it is unavailable, the
  milestone keeps `LongPress` (the existing semantic) rather than inventing a
  new one.

- **What still stands in ADR-48:** §1 (one progress representation at Display
  scale), §2 (`MomentumFigureDisplay`, derived), §3 (transient
  `AuroraState.MILESTONE` for `MomentumMotion.reward`, one haptic, no snackbar,
  no particles/confetti/sound, reduced-motion skip), §5 (Home rows pass
  `streak = 0`), §6 (change-triggered motion; no permanent infinite animation on
  Home).

- **Consequences:** `TodayViewModel` keeps a message path, renamed and narrowed
  to `encouragementFor(sections, motivationalMessagesEnabled)` returning
  `UiText?`; `TodayUiState.Success.message: UiText?` becomes
  `encouragement: UiText?`; two strings are added
  (`today_encouragement_streak` = "Keep your %1$d-day streak going.",
  `today_encouragement_next` = "Next up: %1$s") and the six generic
  `today_message_*` strings are **replaced** rather than retained verbatim;
  `today_celebrate_all_done` is still removed (the snackbar removal stands).
  `TodayEffect.Celebrate` still fires on the `0 -> 1` transition and still does
  not surface a snackbar. No domain, repository, Room, DataStore, Settings or
  navigation change accompanies this note.

- **No code accompanies this note.** It is a documentation amendment only; the
  working tree after it contains no build-relevant delta beyond this file.

### ADR-47 Amendment Note 1 - the AC 9/AC 17 readability acceptance measurement is the ISSUE-020 region-sample method; the R3 peaks (0.26 Light / 0.22 Dark) stand final (Architect, 2026-09-30)

- **Instrument:** this note is **append-only**. No ADR-47 or ADR-48 text above is
  rewritten, deleted or restated. It amends only the **acceptance measurement**
  of ADR-47 §2's readability outcome (AC 9 / AC 17) and confirms R3's "final by
  measurement" step. The scrim mechanism, its four stops, its per-theme peaks,
  the [0.15, 0.35] envelope and the ISSUE-020 text-token remedy all stand
  exactly as approved.

- **The measured conflict.** The implementation report measured the scrim with a
  **per-pixel minimum** over the copy bands: Light @ 0.26 measured a worst copy
  pixel of **5.07:1** against `textPrimary` (#172033), but Dark @ 0.22 measured
  as low as **1.25:1** against #F5F7FC where the bottom-anchored crop places the
  expanded header over the dark artwork's sparse bright upper ribbon (p95
  ~4.6:1, mean ~9.7:1 over that band). No peak inside the approved
  [0.15, 0.35] envelope clears a per-pixel minimum there (the ribbon pixels
  would need ~0.53), so the Developer escalated instead of silently tuning
  (AGENTS.md §10).

- **Why the per-pixel minimum is not the criterion.** (1) It is not the repo's
  method: ISSUE-020 - the only recorded readability measurement - is a **region
  sample**, one background sample over a rectangular region at the glyphs'
  location measured against the text token (`(181,183,246)` sampled over
  `x 270..340, y 178..224`). (2) The approved refinement already names the
  method: R3's derivation states "the audit's ceiling is 0.65 and **ISSUE-020's
  measurement method is the acceptance test**" (`task.md` §8 "Refinement" item
  7). (3) A per-pixel minimum over high-frequency photographic detail (with
  lossy-WebP ringing) is not a WCAG-defined sample of a background color and is
  unsatisfiable inside the approved envelope without flattening the artwork to
  an opaque plate - contrary to ADR-47 §2's "readability without flattening".

- **Decision.** AC 9 (`task.md` §8 §20.9) and AC 17 (refinement §12.17) keep
  their wording; their measurement is the **ISSUE-020 region-sample method**:
  for each Hero copy element (date, greeting, figure, caption, encouragement; a
  wrapped line is sampled per line), take the text line's rectangle; composite
  the Hero as rendered (the shared artwork with its `DstIn` bottom dissolve over
  the screen backdrop, then the one 4-stop scrim; source-over per sRGB channel,
  per-theme peak); reduce the rectangle to one effective background color
  (per-channel mean); compute the WCAG 2.x contrast ratio against the element's
  token (`textPrimary`); the **minimum** region ratio across the measured sweep
  (heights 104-480 dp; widths 360/412 dp; 1-2 line header; 0-2 line
  encouragement; both theme artworks) governs and is reported per element and
  per theme. The per-pixel minimum is **informational only**. Full method:
  `task.md` §8 "Refinement" item 13. Where a device render exists, the same
  rectangles may be sampled from the screenshot with ink pixels excluded
  (ISSUE-020 sampled an adjacent same-band rectangle).

- **Final values (final by measurement).** `ScrimPeakLight = 0.26f` and
  `ScrimPeakDark = 0.22f` stand; the stops stand (`0f -> 0`, `0.20f -> peak`,
  `0.80f -> peak`, `1f -> 0`). Light clears even the stricter per-pixel sweep
  (5.07:1), so every region sample clears. In Dark the progress block (quiet
  lower band) is ~16.9:1 and the expanded-header band's distribution (p95
  ~4.6:1, mean ~9.7:1; worst pixel 1.25:1) establishes that the region means
  clear 4.5:1 with margin; the bright ribbon pixels remain artwork, not a
  flattened plate. The Developer re-measures and records the per-element region
  values in the handoff and `task.md` §9.

- **Why no stop or peak change.** The region sample clears the floor at the
  approved profile, so moving the plateau start earlier or raising
  `ScrimPeakDark` inside the envelope would darken the Aurora without a measured
  failure. The header's top edge receiving part of the peak through the 0-0.20
  ramp (the retired ADR-35 veil held 0.26 there) is a property of the approved
  fixed profile; the acceptance measurement is taken at the copy's actual
  location, so the ramp is accounted for rather than compensated. Changing a
  stop, exceeding [0.15, 0.35], or adding a layer/artwork is outside this note
  and needs the normal approval path.

- **Authority.** This note applies the acceptance method the approved
  refinement already names; it changes **no** approved numeric (the peaks are
  the spec's own candidates, now confirmed by measurement) and does not exit the
  envelope. **No human approval is required.** Human approval would be required
  for any stop change, any peak outside [0.15, 0.35], or new artwork/layers.

- **What stands unchanged.** ADR-47 §2 (one constant scrim, one bottom
  hand-off, retained screen-space top mask), the four-stop profile, the
  per-theme peaks, the envelope, ADR-47 §7 (no `textSecondary` over artwork),
  ADR-48 and its Amendment Note 1, the artwork pair and the
  `MomentumHeroArtwork` contract, and every motion/performance constraint in
  `task.md` §8.

- **Consequences.** No application behavior change: no constant, stop, layer,
  crop, token or dependency change. The Developer re-measures both themes with
  the region method, records the per-element values in the handoff and
  `task.md` §9, updates the `HomeHero.kt` constants KDoc (comment only,
  replacing the "reported for an Architect decision" residual note), and the
  Reviewer reproduces the measurement. No build, dependency, navigation, domain
  or schema change.

### ADR-47 Amendment Note 2 — the Home Hero's height becomes artwork-driven: the copy is one top-anchored module and the flexible height is an artwork crown above it (Architect, TASK-016, pending human approval)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note amends ADR-47 **§2** (one clause only) and
  **§3** (the empty-day composition), and records the reversals of TASK-015's
  "content-driven Hero height" model required by the on-device defect report of
  2026-09-30. ADR-47 Amendment Note 1 (the region-sample acceptance
  measurement) stands exactly as written.

- **Root cause (measured from the current source, 360 dp window, 24 dp status
  bar).** The Hero's height was a sum of its **copy**: `topInset + headerDp +
  (progress block) + HeroBottomInset` plus a `CollapseHeadroom`. The shared
  artwork is cover-cropped by **width** (`ContentScale.Crop`,
  `Alignment.BottomCenter`), so at 360 dp the scaled illustration is 360 × 540
  and the visible slice is a function of the container's height/width ratio
  alone. Consequences:
  - **Empty day:** Hero ≈ `40 + 56 + 16` = **112 dp** → the window shows the
    bottom **21 %** of the illustration (its deliberately featureless lower
    third), and the shared **absolute** `bottomDissolve = 88.dp` computes
    `bottomStart = 1 − 88/112 = 0.215`, dissolving **78 %** of the window. The
    artwork is arithmetically invisible — a flat tint indistinguishable from
    the page background. This is the whole of the reported "artwork does not
    render".
  - **Populated:** the extra 72 dp of `CollapseHeadroom` was placed
    **between** the header and the progress block, so the greeting→progress
    distance varied `20…92 dp` with scroll inside a region that (because of the
    first bullet) carried no visible artwork — a void, not an "open Aurora
    band".
  There is no place inside a top-anchored/bottom-anchored copy arrangement for
  flexible height that is both visible and not a gap between copy blocks. The
  correction is therefore structural, not numeric.

- **Decision (this note).**
  1. **The Hero's foreground is one top-anchored module.** Its internal
     geometry is **scroll-invariant**: `topInset → artwork crown → date +
     greeting + Settings → MomentumSpacing.section → progress block +
     encouragement → HeroBottomInset`. The greeting→progress distance is a
     fixed `MomentumSpacing.section` (24 dp) at every collapse value. Nothing
     weighted may sit **between** the header and the progress block, and
     nothing weighted may sit **below** the last copy.
  2. **The Hero's flexible height is an artwork crown placed above the copy**,
     between the status-bar reserve and the date:
     `ArtworkCrown = HeroRingSize + MomentumSpacing.standard` = **96 dp**.
     Derivation: at least one progress-orb diameter (80 dp, the largest opaque
     element the Hero already owns) of clear, copy-free artwork plus one
     standard gap; below 96 dp the bottom-anchored crop falls back to the
     illustration's quiet lower third and the artwork stops reading as an
     image.
  3. **Height model.** `compactHeight = topInset + headerDp + (if
     (hasProgress) HeaderProgressGap + contentDp else 0.dp) + HeroBottomInset`;
     `expandedHeight = if (hasProgress) compactHeight + ArtworkCrown else
     compactHeight`; the crown itself is `lerp(ArtworkCrown, 0.dp, p)` so it
     shrinks 1:1 with the clipped box. The collapse therefore **reclaims the
     artwork sky**, never space between two copy blocks. The reported collapse
     range is still a pure function of the measured content, never of `p`.
  4. **The empty day is no longer a slim header.** It keeps the same crown, so
     the Hero is `topInset + ArtworkCrown + header + HeroBottomInset` (208 dp at
     360 dp, against ≈ 112 dp before) and the artwork reads in both states.
     The empty day still has **no** progress block, **no** encouragement, **no**
     collapse travel and **no** reserved space for either.
  5. **The progress figure and the orb are one group.** `Modifier.weight(1f)` is
     removed from the figure column; the figure is content-width and followed by
     `HeroOrbGap = MomentumSpacing.standard` (16 dp) before the orb, so both sit
     on the Hero's 20 dp screen axis. The previous weighted column stretched
     the figure to 240 dp while its text stayed start-aligned, which pushed the
     orb to the trailing edge and produced a digit-dependent 100–200 dp gap.
  6. **Everything else stands.** One Aurora-backed surface (ADR-31), artwork at
     the physical window top edge (ADR-33/37/39), one scroll surface with the
     clamped-px collapse (ADR-28/29), one constant page-background scrim with
     the same stops and the same `[0.15, 0.35]` peak envelope, the retained
     screen-space top mask (ADR-35), the artwork's bottom hand-off, `textPrimary`
     throughout and no `textSecondary` over artwork (ADR-47 §7), the
     content-summed compact height that cannot clip large fonts, and the
     `MomentumHeroArtwork` call unchanged (`Alignment.BottomCenter`,
     `fillMaxSize()`, shared `topDissolve`/`bottomDissolve`, no zoom, no
     parallax). **No domain, schema, ViewModel, navigation, dependency or build
     change.**
  7. **The scrim is re-measured, not assumed.** Because the artwork window and
     the Hero height changed, TASK-015's contrast measurements do **not**
     transfer. The canonical ISSUE-020 **region-sample** method (ADR-47
     Amendment Note 1) is re-run for both themes across heights {208, 216, 312}
     dp and widths {360, 412} dp. Every region sample must clear **4.5:1**. If
     one fails, the peak moves **inside the existing envelope** (Light toward
     `0.32f`, Dark toward `0.30f`) and the sweep is repeated; if it still fails
     it is escalated, and the ink is never changed to compensate.

- **Options.** (a) Raise the empty-day Hero height only — leaves N2's
  scroll-variant gap and N1's cropped artwork untouched. (b) Keep the headroom
  between the copy blocks and rely on the artwork filling it — provably cannot
  work, because the region that grows is exactly the region the crop empties.
  (c) Move all Hero copy to a single bottom-anchored block (the `HabitsHero`
  arrangement) — conflicts with the approved product decision (`DECISIONS` §5,
  DS §12) that keeps the greeting at the top. (d) One top-anchored copy module
  with the flexible height as an artwork crown above it (chosen).
- **Chosen: (d).** It is the only option that makes the artwork visible in every
  state, keeps the greeting top-anchored as approved, makes the Hero's internal
  composition scroll-invariant, and adds no state, animation, layer or artwork.
- **Consequences.** Changed: `presentation/screens/today/HomeHero.kt`
  (`ArtworkCrown` replaces `CollapseHeadroom`; `HeaderProgressGap` becomes
  `MomentumSpacing.section`; new `HeroOrbGap`; the progress row's `weight(1f)`
  removed; new `onHeightChanged` parameter mirroring `onCollapseRangePx`;
  re-measured `ScrimPeak*` KDoc),
  `presentation/screens/today/TodayScreen.kt` (the empty branch's centring
  geometry — see ADR-48 Amendment Note 2 §2). Contract tests that pinned the
  removed model are rewritten to the retained outcomes, not deleted:
  `HomeHeroAssetsTest`, `HomeAccessibilityTest`, `HomeHeroCompositionTest`.
  `HomeScrollSurfaceTest` requires no change (still one `LazyColumn`, one
  `HomeHero(`). `MomentumHeroArtwork`, `MomentumFab`, `ProgressOrb`, `HabitsHero`
  and the theme layer are untouched.
- **Approval.** **Not approved. This note records an Architect decision that
  requires explicit human approval before implementation** (AGENTS.md §4/§21,
  `task.md` §8.13), because it reverses an approved TASK-015 clause.

### ADR-48 Amendment Note 2 — the Home FAB is present in every state, the empty state's CTA is removed, and the scroll-driven orb transformation is rejected for this pass (Architect, TASK-016, pending human approval)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note amends **ADR-47 §3**'s two Home clauses (the
  suppressed FAB and the single add CTA) and records the reversal of TASK-015
  R9/R10 required by the human brief of 2026-09-30. ADR-48 §1–§3, §5, §6 and
  ADR-48 Amendment Note 1 stand exactly as written.

- **Root cause (E3/N3).** `AnimatedVisibility(visible = !state.isEmpty)` made
  the FAB's **presence** a function of the day's state, and
  `HomeEmptyState(onAdd = onAdd)` gave the empty state a second add affordance.
  A control whose visibility is a state change reads as an event that arrives,
  not as a control that is always available — which is what was reported as
  "disconnected". The FAB's *material* was not the cause and is not changed.

- **Decision (this note).**
  1. **One add affordance, and it is the FAB.** `MomentumFab` is composed
     unconditionally on Home, in every state. `HomeEmptyState` renders the
     shared `EmptyState` with **no action**; `EmptyState`'s `actionLabel` /
     `onAction` become optional (`String? = null`, `(() -> Unit)? = null`,
     inline conditional `TextButton`) — an additive default, source-compatible
     with the three other call sites, which all pass named arguments. The now
     unreferenced `today_empty_action` string is deleted. ADR-47 §3's "the
     Quick Add FAB is suppressed on the empty day so there is exactly one add
     affordance" is superseded: the same outcome (exactly one affordance) is now
     reached by **removing the card CTA**, not by suppressing the control.
  2. **The empty state is centred by measured geometry, not by padding.** Its
     `LazyColumn` slot takes
     `Modifier.heightIn(min = viewportHeight − heroHeight − EmptyDayBottomReserve)`
     with a `Box(contentAlignment = Alignment.Center)` inside, so the free space
     is divided equally above and below the card in the region between the
     Hero's bottom edge and the top of the bottom navigation. `heroHeight` is
     measured and reported by the Hero; the reserve is the same single named
     constant the `LazyColumn` already uses as `contentPadding.bottom`.
     Arbitrary `Spacer` constants and percentage paddings are rejected by
     construction — they are not size-independent and the human brief forbids
     them.
  3. **One FAB treatment in both states.** `MomentumFab` (ADR-47 §6 — the solid
     Aurora-filled disc, `prominent` glyph, white hairline, soft shadow) is
     **unchanged**. Placement is tightened to the design system's rhythm:
     `end = MomentumSpacing.screen`, `bottom = MomentumSpacing.section` (24 dp,
     so the floating control cannot read as part of the glass bottom bar), and
     the list reservation becomes
     `MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section` (96 dp),
     which guarantees the last row clears the FAB by a full section gap. Any
     material change would be a new visual language and needs its own ADR.
  4. **The scroll-driven transformation is rejected for this pass.** The idea
     under evaluation — on scroll, the orb moves left and the progress
     information aligns right — is **not adopted and not authorized here**. The
     crown (ADR-47 Amendment Note 2) already makes the Hero re-compose with the
     drag through the already-approved single px source of truth and one
     `NestedScrollConnection`, with no new state, no clock and correct reduced
     motion. A second independent orb interpolation would add a second reduced
     motion semantic, a second thing that can desynchronise from the collapse,
     and would reverse the approved top-anchored greeting mid-scroll. It is not
     needed to resolve any of E1–E4 / N1–N3. It may be raised again as a new
     proposal with its own rationale, cost analysis and motion spec, and would
     then require its own human approval. **It must not be introduced silently
     during implementation.**

- **What stands unchanged.** ADR-48 §1 (one progress representation, Display
  figure, `showPercent = false`), §2 (`MomentumFigureDisplay`), §3 (the 100 %
  reward: transient `AuroraState.MILESTONE` for `MomentumMotion.reward`, one
  ring glow, exactly one `HapticFeedbackType.Confirm`, no snackbar) and
  Amendment Note 1 (the state-specific encouragement line), ADR-47 §4/§5/§6/§7
  and §8, the FAB's `cd_quick_add` label and its Create Item destination, the
  56 dp target, both themes, reduced motion, TalkBack, and the offline-only
  guarantee. Removing the FAB's enter/exit transition **removes** motion rather
  than adding it. No domain, schema, ViewModel, navigation, dependency or build
  change.
- **Consequences.** Changed: `presentation/screens/today/TodayScreen.kt`
  (unconditional `MomentumFab`, FAB placement, list reservation, `BoxWithConstraints`
  root, measured hero height, centred empty item, `HomeEmptyState` without
  `onAdd`), `presentation/components/MomentumBadges.kt` (optional CTA only —
  the one cross-boundary change, justified because a local copy of the card
  would fork its material), `res/values/strings.xml` (delete
  `today_empty_action`). `QuickAddTest` is rewritten to the new outcome;
  `HomeScrollSurfaceTest`, `HomeStatesTest`, `HomeHeroStateTest` and
  `HomeSectionMotionTest` require no change.
- **Approval.** **Not approved. This note records an Architect decision that
  requires explicit human approval before implementation** (AGENTS.md §4/§21,
  `task.md` §8.13), because it reverses approved TASK-015 decisions.

### ADR-47 Amendment Note 3 — the Home Hero is three layers (BACKGROUND artwork / ATMOSPHERE / FOREGROUND copy), the crown is transparent space, and the continuous bottom merge is verified with two derived failure bounds (Architect, TASK-016, pending human review of this amendment)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **adds to** ADR-47 Amendment Note 2 (the
  artwork-driven height + crown model) and does not change any approved number.
  Decisions 1–3 of TASK-016 are **approved** (human ruling, 2026-09-30); this note
  records the ruling's binding *layer / atmosphere* clarification and still
  requires human review before implementation.

- **Decision (this note).**
  1. **Layer contract and z-order.** The Hero's clipped `Box` draws exactly
     three layers, in this order, and nothing else:
     - **L0 BACKGROUND** — `MomentumHeroArtwork(Alignment.BottomCenter,
       Modifier.fillMaxSize())`, full-bleed over the Hero's entire box, top edge
       = the physical window top edge (ADR-33/39), clipped by the Hero's
       `clipToBounds()`, merging into the page background at the Hero's
       **bottom** edge.
     - **L1 ATMOSPHERE** — `HeroScrim(Modifier.fillMaxSize())`, the unchanged
       four-stop page-background gradient (0 → clear, 0.20 → peak, 0.80 → peak,
       1 → clear) at peak 0.26 Light / 0.22 Dark. A **tint**, never a cover.
     - **L2 FOREGROUND** — the copy column: status-bar reserve, crown, header,
       section gap, progress block, bottom inset.
     The Hero contains **no card, panel, `LiquidGlass` surface, divider or scrim
     rectangle behind any copy.** The only sub-surface is the existing
     `LiquidGlassIconButton` (Settings) inside the header row.
  2. **The crown is transparent space, not a layer.** A crown band contains **no
     child** — no image, card, gradient band, divider, rule or glyph. Its only
     purpose is to reserve the band in which L0 and L1 are the sole visible
     content, so the artwork is seen unobstructed. Anything drawn in the crown is
     a defect. This is the binding clarification that the artwork is the Hero's
     **background**, not a stacked illustration band above the content.
  3. **Foreground dominance is achieved with type hierarchy and negative space,
     never with an opaque cover.** `MomentumFigureDisplay` > greeting > date and
     caption, all `textPrimary` (ADR-47 §7), plus the crown and the 24 dp section
     gap as negative space. Nothing is boxed; no surface is added behind copy.
  4. **The artwork is never replaced, re-authored, re-tinted, re-cropped, zoomed
     or parallaxed.** The `MomentumHeroArtwork` call is unchanged, which keeps
     ADR-30/ADR-39 and `task.md` §8.9's boundaries intact.
  5. **The encouragement line is over the artwork** in every state where it
     exists. It must not be lifted onto the page background, because a solid
     surface between the artwork and the page background would create exactly the
     hard boundary the ruling forbids. `maxLines = 2` + ellipsis and
     `AnimatedVisibility` with no reserved space are unchanged; because the Hero's
     height is a measured content sum, its presence changes the Hero's height and
     re-fires `onHeightChanged` automatically.

- **Continuous bottom merge — verified from the component, not assumed. Verdict:
  the existing shared 88 dp `bottomDissolve` + the four-stop scrim already give a
  continuous merge with no hard boundary, in both themes, at 208 / 216 / 312 dp
  and at the content-dependent ~368 / ~272 dp variants. No correction is required
  and none is made.** Evidence:
  1. `MomentumHeroArtwork.HeroArtworkImage` builds
     `Brush.verticalGradient(0f → White, bottomStart → White, 1f → Transparent,
     startY = 0f, endY = h)` and applies it with `BlendMode.DstIn`, with
     `bottomStart = (1f − 88.dp / h).coerceIn(0.10f, 0.95f)` = 0.577 / 0.593 /
     0.718 for h = 208 / 216 / 312. The last stop is at `endY = h` with full
     transparency, so the artwork contributes **nothing** at the Hero's bottom
     edge, and no ramp is ever truncated or degenerated.
  2. Theme-independent: `DstIn` reads **alpha only**, the profile runs
     `White → Transparent`, and the mask colour carries no hue. Light and Dark
     share one identical alpha profile.
  3. The scrim also reaches **exactly 0** at that edge (`1f to clear`). This is
     **load-bearing**: `HeroScrim` is a `Box(fillMaxSize)` inside the clipped
     Hero, so a scrim ending at its peak would leave a full-width band of
     `c.background` at 0.26 / 0.22 alpha against the un-tinted Aurora field
     below — a visible hard edge across the whole width. The four-stop profile
     and `ScrimPeakEnd = 0.80f` are therefore **retained exactly**, and a
     contrast tweak may move the peak **value** inside `[0.15, 0.35]` but must
     never alter the profile or the stops.
  4. Nothing below the Hero changes identity: the same `AuroraBackground` runs
     behind the whole `LazyColumn`, and the first list item draws over it with its
     own surface.
  5. **No top boundary is ever created, so ADR-39's "no top fade" remains correct
     and no top fade is added.** `clipToBounds()` clips at the Hero's top edge,
     but that edge is pinned to `y = 0` of the scroll surface; the Hero is the
     first `LazyColumn` item, so collapsing shrinks its height from its **bottom**
     and moves the dissolve hand-off upward while the top edge stays at the
     window top; once the list scrolls, the Hero's top leaves through the
     **window** edge, which is not a page-background boundary.
  6. The only profile discontinuity is a first-derivative kink at each ramp start
     — value-continuous, never a hard boundary.

- **Recorded finding, deliberately not corrected.** The scrim's bottom ramp
  `[0.80h → h]` **overlaps** the artwork's dissolve `[h − 88 → h]` by 41.6 dp at
  h = 208, 43.2 at 216, 62.4 at 312 and 73.6 at 368, so the artwork's apparent
  hand-off is compounded rather than a single 88 dp ramp. This is not a hard edge
  and a clean, slightly faster hand-off is desirable (ADR-47 requires the
  hand-off), but it is the mechanism by which artwork presence could regress
  toward the E1 failure, so it is the stated derivation for BOUND-1 below and for
  the §8.6 sweep heights. Tiling the ramps was considered and **rejected**: it
  would require duplicating the component's 88 dp default into `HomeHero` as a
  second source of truth (violating ADR-30's caller contract and `task.md` §8.9)
  and would invalidate the contrast baseline ADR-47 Amendment Note 1 fixed as
  final, for a benefit the ruling does not ask for.

- **The two failure bounds (derived, with Reviewer assertions).**
  - **BOUND-1 — the artwork must not become invisible (E1 regression guard).**
    *Derivation:* the artwork's fully-opaque region inside the Hero is exactly
    `[0, h − 88]`; the remaining 88 dp is the hand-off and is meant to fade into
    the page background. Requirement 4 is satisfied if every state presents at
    least one `HeroRingSize` (80 dp) of unobstructed, fully-opaque artwork, the
    smallest band that can hold a recognizable fragment of the illustration at
    phone scale and the same element the crown derives from. Window check:
    `h − 88` = 120 / 128 / 224 / 280 / 184 dp for h = 208 / 216 / 312 / 368 / 272,
    all ≥ 80. Crown check (the binding one): `ArtworkCrown ≥ HeroRingSize`; 96 =
    80 + 16 ✔. The artwork window must **never** be derived from `HeroRingSize`.
    *Assertions:* (a) hero height − 88 dp ≥ `HeroRingSize` in empty / expanded /
    collapsed, tolerance 1 dp; (b) `artworkBounds == heroBounds` in every state;
    (c) the crown rectangle contains **no** text node, no content-description node
    and no semantics node; (d) both scrim peaks still parse inside
    `[0.15, 0.35]`. *On-device only, not claimable as a test here (`adb`
    unavailable):* the illustration must be recognisable inside the 96 dp crown in
    **both** themes.
  - **BOUND-2 — the foreground must not be washed out and the artwork must not be
    covered.** *Derivation:* requirement 5 forbids an opaque cover and
    ADR-47 §2 with Amendment Note 1 already bounds the scrim peak to
    `[0.15, 0.35]`; the enforceable ceiling is therefore exactly that existing
    envelope, and the floor is the existing ISSUE-020 acceptance floor of
    **≥ 4.5:1**. *Assertions:* (a) the §8.6 region-sample sweep over both themes ×
    heights **{208, 216, 272, 312, 368} dp** × widths {360, 412} dp ×
    {Fraction, Complete} × encouragement {on, off} clears 4.5:1 per copy element,
    now including the encouragement line and both collapsed states; (b) no
    `textSecondary` in `HomeHero.kt`; (c) no `LiquidGlass`, `Card`, `Surface` or
    `background(` between `HeroHeader(` and `HeroProgressBlock(`; (d) every copy
    node's bounds lie inside `artworkBounds` in every state and both themes.
    *On-device only:* the illustration must still be visible behind and around the
    copy — the copy's backing must not read as a flat plate.

- **Consequences.** **No new application code beyond ADR-47 Amendment Note 2's
  geometry.** This note changes **no** constant, no layer, no crop and no
  component: `MomentumHeroArtwork.kt` and the `HeroScrim` profile are **not**
  touched, and `task.md` §8.9's boundaries stand. It adds the layer contract the
  Developer must implement against, the verification that the existing shared
  dissolve and scrim already satisfy the continuous-merge requirement, the two
  derived bounds with runnable assertions, and one new
  `HomeHeroCompositionTest` case (`artworkIsPresentButNotCovered`). The §8.6
  sweep is widened to the collapsed and encouragement heights.

- **Explicit non-goal.** `HeroScrim`'s and `MomentumHeroArtwork`'s `drawWithCache`
  blocks re-run per frame during the collapse drag (96 dp of travel; two `Brush`
  allocations per frame). This is pre-existing and already accepted by TASK-015
  as the residual of a height that must change layout. It must **not** be
  optimised in this pass.

- **Approval.** Decisions 1–3 are approved by the human ruling of 2026-09-30. This
  note records the ruling's binding layer/atmosphere clarification and **still
  requires human review and acceptance before any implementation is authorized**
  (`task.md` §8.13).

### ADR-48 Amendment Note 3 — the shared `MomentumFab` hairline becomes the design system's theme-resolved edge; RISK-1 is closed by ruling (Architect, TASK-016, pending human review of this amendment)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **adds to** ADR-48 Amendment Note 2 (the FAB is
  present in every state; the empty-state CTA is removed; §8.5's placement and
  reservation) and to **supersedes §8.5's "material NOT changed"**, exactly as the
  human ruling of 2026-09-30 directs. ADR-47 §6's identification of `MomentumFab`
  as the one shared Solid-Aurora disc stands; only its edge changes.

- **Diagnosis (from the current `MomentumFab.kt`, against Home's surroundings,
  both themes).** Home's FAB floats between two things that both define
  themselves with an **edge** — the Aurora artwork/page behind it and the glass
  bottom navigation bar below it — and both use the design system's edge
  vocabulary. `MomentumFab` does not, and the mismatch is theme-asymmetric:
  1. **Light — effectively no edge.** The hairline is
     `Color.White.copy(alpha = 0.28f)`: white at 28 % on a light-violet `primary`
     disc over a near-white page. `MomentumGlass.borderLight`'s own KDoc states
     "white is invisible" — which is why every other light surface
     (`EmptyState`, `LiquidGlass`, the bottom bar) separates with an **ink**
     hairline. The FAB is the only light surface with a white edge, so in Light it
     reads as a soft blob whose only separator is a black ambient shadow.
  2. **Dark — the shadow cannot separate anything and the edge over-strengths.**
     `shadow(elevation = 12.dp, ambientColor = Color.Black.copy(alpha = 0.30f))`
     over `c.background` in Dark is black on near-black: invisible by
     construction, and Dark surfaces in this app are separated by edges, not
     shadows. Meanwhile the FAB's 0.28 white hairline is the strongest white
     edge in the dark theme, where the bar, cards and dialogs all use
     `MomentumGlass.borderDark` = 0.16.
  3. **Both themes — the elevation is a magic number.** The literal `12.dp`
     duplicates `MomentumGlass.heroElevation = 12.dp`, which exists for floating
     layers.
  The disconnection is therefore an **edge-vocabulary** problem, not a fill,
  shape, size, glyph or behaviour problem.

- **Decision (this note).**
  1. **Placement and availability remain the primary fix** and stand exactly as
     ADR-48 Amendment Note 2 / `task.md` §8.5 specify: the FAB is composed
     unconditionally in every Home state, `end = MomentumSpacing.screen`,
     `bottom = MomentumSpacing.section`, and the list reservation is
     `MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section` (96 dp).
  2. **The hairline becomes the design system's theme-resolved edge**, using the
     pair `EmptyState` already uses:
     `Color.White.copy(alpha = MomentumGlass.borderDark)` (0.16) in Dark and
     `c.textPrimary.copy(alpha = MomentumGlass.borderLight)` (0.10 ink) in Light.
     Light therefore gains a real, theme-correct edge — **the integration fix** —
     and Dark adopts the same white edge value as every other dark surface.
  3. **`shadow(elevation = 12.dp, …)` becomes
     `shadow(elevation = MomentumGlass.heroElevation, …)`** — an identical
     value, so there is **no visual change**; ambient and spot colours are
     unchanged. In Dark the shadow is explicitly **not** relied on as the
     separator; the edge is.
  4. **Unchanged:** the fill gradient and its stops, `CircleShape`,
     `MomentumFabSize = 56.dp`, `MomentumIcons.add`, `MomentumIconSizes.prominent`,
     `glassPress` press feedback, `role = Role.Button`, and the
     `contentDescription` parameter. **No new token, no new value, no new
     component, no new API, no new visual language, no hard-coded colour added**
     — the change removes the last hard-coded hairline alpha.
  5. **Explicitly rejected:** placement-only (it cannot repair the Light-theme
     invisible edge, and the ruling authorises the repair); a glass or gradient
     FAB or a gradient ring (a new visual system, forbidden); and adding an
     optional `border`/`emphasised` parameter so Home could differ from Habits
     (new component architecture, forbidden — which is why the shared consequence
     below is put to the human rather than engineered around).

- **Shared call-site consequence — requires explicit human approval.**
  `MomentumFab.kt` is shared with `HabitsScreen` (`cd_create_habit`), so this
  change affects **Habits' FAB** as well: in Light it gains the same ink hairline,
  in Dark its white hairline becomes 0.16. Nothing else on Habits changes —
  same component, size, glyph, placement and behaviour. This is the direct,
  unavoidable consequence of fixing a shared component and makes the two screens
  consistent with each other and with every other surface; it cannot be scoped to
  Home without forbidden changes. If the human refuses it, the only lawful
  outcomes are to accept placement-only and re-open RISK-1, or to defer the whole
  matter to a future task that owns `MomentumFab` as a component. The Architect
  recommends approval.

- **Assertions.** *Runnable (source-level, `QuickAddTest`):* `MomentumFab.kt`
  contains `MomentumGlass.borderDark`, `MomentumGlass.borderLight` and
  `MomentumGlass.heroElevation`, and **no longer** contains
  `Color.White.copy(alpha = 0.28f)` — replacing the existing literal assertion;
  `CircleShape`, the add glyph, `prominent`, `Brush.verticalGradient`, `shadow(`,
  `glassPress(interaction)`, `Role.Button`, `MomentumFabSize = 56.dp` and
  `contentDescription: String` remain asserted unchanged. *Runnable
  (placement, `TodayScreen.kt`):* `end = MomentumSpacing.screen`,
  `bottom = MomentumSpacing.section`, the 96 dp reservation, and no
  `AnimatedVisibility` at the FAB call site. *On-device only, not claimable as a
  test here (`adb` unavailable):* in **Light** the FAB reads as a defined disc
  against the near-white page with an edge of the same weight as the bottom bar's;
  in **Dark** its rim is visible without the page behind it being brighter.

- **Consequences.** Changed: `presentation/components/MomentumFab.kt` (two
  expressions — the hairline and the elevation token), plus the corresponding
  `QuickAddTest` assertion. This is the only component outside
  `presentation/screens/today/**` this pass may touch besides `EmptyState`'s
  optional CTA (ADR-48 Amendment Note 2). No domain, schema, ViewModel,
  navigation, dependency or build change; no change to Habits' layout, copy or
  behaviour.

- **Approval.** Recorded under the human ruling of 2026-09-30 (Decision 4), which
  authorized a narrowly scoped existing-token integration adjustment and closed
  RISK-1. This note **still requires human review and acceptance of §8.15.3's
  shared call-site consequence before any implementation is authorized**
  (`task.md` §8.13).

### ADR-47 Amendment Note 4 — Home's Aurora becomes one persistent screen-level atmosphere; the Hero box and the crown are deleted; date and greeting become scrollable content and only Settings is fixed chrome (Architect, TASK-017, pending human approval)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **supersedes ADR-47 Amendment Note 2 in full**
  and **supersedes ADR-47 §3**'s "compact Aurora header" clause, **amends ADR-47
  §2**, **amends ADR-47 §6**, **re-issues ADR-47 Amendment Note 3 against the new
  atmosphere**, and **retires ADR-35 on Home**. ADR-48 and its Amendment Notes 1–3
  stand exactly as written except where ADR-48 Amendment Note 4 (immediately
  below) amends ADR-48 §1.

- **Root cause, established from the code (not from taste).** TASK-016's
  `heightIn(min = …)` / `onHeightChanged` empty-state geometry is confirmed as the
  mechanism, and it is **one** defect: `HomeHero.kt:205` and `HomeHero.kt:214`
  disagree in the `!hasProgress` branch.

  ```kotlin
  val expandedHeight = if (hasProgress) compactHeight + ArtworkCrown else compactHeight  // :205
  val crown          = if (hasProgress) lerp(ArtworkCrown, 0.dp, p) else ArtworkCrown   // :214
  ```

  On an empty day the 96 dp crown is **charged to no height but is still laid
  out**. The Hero `Box` has a fixed `.height(currentHeight)`; its copy `Column` is
  `.fillMaxSize()` and measures children sequentially against the remaining space,
  and `SizeElement` coerces a child that exceeds it. Trace at scale 1.0 with a
  24 dp status bar and `headerPx == 0`: `compactHeight = 56 dp`; `Spacer(topInset
  40)` consumes 40; `Spacer(crown 96)` is measured with `maxHeight = 16` and is
  coerced to 16, leaving **0 dp**; `HeroHeader` is therefore measured with
  `maxHeight = 0` and **collapses to zero height** — the date, the greeting and the
  48 dp Settings button all draw nothing — and it reports `headerPx = 0` back, so
  the height sum never self-corrects and the state is **stable at 56 dp**. The
  artwork, `fillMaxSize()` inside a `clipToBounds()` 56 dp box, computes
  `bottomStart = (1 − 88/56).coerceIn(0.10f, 0.95f) = 0.10f`, so its shared 88 dp
  dissolve ramps to transparent from 10 % of the window and **~90 % of the
  illustration is dissolved away**. The observed result — no date, no greeting, no
  Settings, no visible artwork — is exactly this, and it is a **regression**, not a
  crop or legibility defect.

  The KDoc's declared invariant that "the height model and the foreground layout
  can never drift apart" is **false in exactly this branch**. The instrumented
  guard `HomeHeroCompositionTest.emptyDayHeroStillShowsTheArtworkCrown` asserts
  `height >= statusBarTop + HeroTopInset + ArtworkCrown` and would have failed on
  its first run; no instrumented suite has ever been executed in this project
  (`task.md` §7), so the one contract that could have caught this was never run.
  That, not the geometry itself, is the process root cause.

  The same defect inverted explains the non-empty finding: with progress the date
  sits `topInset + crown` = **136 dp** below the window top. Because the date's
  distance from the top **is** the crown, no crown value is ever correct — only
  removing the crown removes the gap.

- **Decision (this note).**
  1. **Aurora is the atmosphere and has no height of its own.** The artwork becomes
     a persistent screen-level backdrop of Home's content area — behind the scroll
     surface, behind the persistent chrome, behind the FAB, and visible around and
     through the Liquid Glass foreground. §4.3's `AURORA = atmosphere ·
     LIQUID GLASS = interface · CONTENT = hierarchy` is the governing principle.
  2. **There is ONE atmosphere, composed in four layers**, in this z-order:
     **(1a)** the ambient Aurora field (`AuroraBackground`, `animated = false`);
     **(1b)** the shared `MomentumHeroArtwork` in a derived window;
     **(1c)** one readability scrim spanning the **whole atmosphere**;
     **(1d)** the foreground. Layers 1a and 1b are two **regions of one
     environment** joined by the shared 88 dp dissolve, not an image with a fade.
  3. **The artwork window is one derived constant, never a Hero height:**

     ```kotlin
     ArtworkWindow = HeroRingSize + MomentumSpacing.standard + ArtworkHandOff
     //               80 dp              16 dp                88 dp        = 184 dp
     ```

     Three facts, all already canonical: `HeroRingSize` (80 dp — BOUND-1's
     recognition band), one standard gap, and the shared `MomentumHeroArtwork`
     dissolve (ADR-39). It is **identical in every state, on every device, at every
     font scale**, and is never a function of content. **No further Hero-height
     value is authorized.**
  4. **The window reserves nothing.** The artwork occupies the top 184 dp of the
     environment **and the copy floats on it** from the first line. The crown
     existed only to reserve copy-free space for the artwork, which is precisely
     the "artwork as decorative banner" abstraction ADR-47 §2's outcomes and §4.3
     forbid. With no reservation, nothing competes for the artwork's space, so
     **BOUND-1 becomes structural** (`ArtworkWindow − 88 = 96 dp ≥ HeroRingSize` in
     every state, no per-state measurement) and the artwork can never be squeezed
     out.
  5. **ADR-47 Amendment Note 3's layer contract is re-issued** as 1a → 1b → 1c →
     1d above. Its §"Continuous bottom merge" analysis is re-run and becomes
     *simpler and stronger*: the scrim's extent is constant, the four-stop profile
     and `ScrimPeakStart = 0.20f` / `ScrimPeakEnd = 0.80f` are **unchanged**, and
     because the scrim reaches exactly 0 at the atmosphere's bottom edge — the
     content area's bottom edge, behind the floating glass bar — the failure ADR-47
     Amendment Note 3 §3.3 identified is structurally impossible. Peaks remain
     **0.26 Light / 0.22 Dark**, inside the approved `[0.15, 0.35]` envelope.
  6. **`MomentumHeroArtwork.kt` is NOT modified** and the `MomentumHeroArtwork`
     call keeps its current form (no `bottomDissolve` override, no `zoom`, no
     `parallax`, no `ColorFilter`). ADR-30's caller contract and ADR-39 stand.
  7. **Layer classification of every element** (exactly one class each):
     artwork → **(1) atmosphere**; ambient Aurora field → **(1)**; scrim → **(1)**;
     **Settings → (2) persistent Home chrome**; **date → (3) scrollable content**;
     **greeting → (3)**; progress figure / total / caption / ring / encouragement →
     **(3)**; section headers and rows → **(3)**; empty-state card → **(3)**; FAB →
     **(4) fixed action**; bottom navigation → **(5) fixed navigation**.
  8. **Date and greeting become scrollable content. Only Settings is fixed.** The
     rationale, since this contradicts a task hypothesis and must be reviewable:
     a permanently-fixed per-day statement sits above a scrolling list with nothing
     between them, re-creating the "spatially disconnected" defect in a new form;
     the greeting is a **headline about today**, not a system control, and a
     permanent headline permanently asserts the screen's top region as its most
     important; a fixed band grows from ~88 dp to 130+ dp at font scale 2.0 and
     permanently consumes a third to a half of the viewport on every screen of every
     day; and decisively, a fixed band permanently reduces the maximum list height,
     so on a small phone at large font the screen cannot answer "what should I do
     next" — its own second question — without first scrolling past a header that
     said nothing new. Settings is fixed **because it must be**: it is a global
     control unrelated to today and Home is its only route.
  9. **The invariant that replaces the fixed-header assumption:** *what floats is
     glass, and what floats is what must always be reachable.* Settings is the only
     foreground element permitted to sit directly on the atmosphere, and it is
     legible there precisely because it is a Liquid Glass control rather than bare
     ink — so §4.3's "Liquid Glass surfaces must let Aurora remain perceptible
     through/around them" is satisfied rather than traded away.
  10. **The empty state is inside the same environment.** Its centring stays
      **measured** — `emptyRegion = viewport − (statusBars + HomeTopInset +
      measuredHeaderHeight) − EmptyDayBottomReserve`, applied as
      `Box(Modifier.heightIn(min = emptyRegion), contentAlignment =
      Alignment.Center)` — with the input now structurally correct instead of
      self-desynchronising. **No CTA** (ADR-48 Amendment Note 2) and **centred card**
      (ADR-48 Amendment Note 2) both stand. The FAB remains the single creation
      affordance.
  11. **The collapse is deleted, and no motion replaces it.** `HomeHero.kt` is
      deleted; `collapsePx`, `maxCollapsePx`, `heroNestedScroll`,
      `onCollapseRangePx` and the ADR-35 screen-space top mask all go with it —
      there is no box left to resize and the mask's stated purpose (copy entering
      the camera cutout as the Hero scrolled under the status bar) cannot recur.
      **No scroll-driven orb choreography and no parallax is introduced**; the
      artwork stays present because it moved *out* of the scroll surface, not
      because something animates it. All other Home motion (row `animateItem`,
      Aurora intensity tween, branch `AnimatedContent`, encouragement
      `AnimatedVisibility`, one-shot milestone glow) is unchanged and remains
      reduced-motion-nulled. **The clamped-px collapse pattern (ADR-28/29) is not
      condemned and survives on Habits.**
  12. **Performance improves and no regression is accepted.** Deleting the Hero
      eliminates the residual ADR-47 Amendment Note 3 §"Explicit non-goal" recorded
      (per-frame `drawWithCache` brush rebuilds during the 96 dp collapse drag), the
      per-frame recomposition caused by reading `collapsePx.value` in composition,
      and the whole-screen `CompositingStrategy.Offscreen` + per-draw
      `Brush.verticalGradient` status-bar mask. Afterwards **nothing in Home's
      scroll surface is invalidated by scrolling at all**: the atmosphere reads
      only `AuroraState`/intensity/theme and the list only `TodayUiState.Success`.
      One `LazyColumn` remains, with `key` + `contentType` and no nested scroller.

- **What stands unchanged.** ADR-47 §1 (Home's one job is Today), §4, §5, §7 (no
  `textSecondary` over artwork), §8 (tokens, derivations); ADR-47 Amendment Note 1
  (the ISSUE-020 region-sample **method** and the `[0.15, 0.35]` ceiling);
  ADR-47 Amendment Note 3's BOUND-2 requirements, whose sweep **must be re-run from
  scratch** (its baseline was measured against Hero heights that no longer exist);
  ADR-28/29 (the pattern), ADR-30/ADR-39 (the shared artwork contract),
  ADR-31/ADR-32/ADR-33/ADR-37 (artwork at the window edge, no top fade,
  full-strength head through the status-bar band, no dead space below the copy) —
  **preserved in outcome** by a more direct mechanism; ISSUE-020's remedy.

- **Contrast: the baseline is superseded, the method is not.** The artwork window,
  the scrim's vertical extent and every copy position change, so the ADR-47
  Amendment Note 1 sweep is **re-run from scratch**: both themes × `ArtworkWindow`
  and `ArtworkWindow ± MomentumSpacing.section` × widths {360, 412} dp ×
  `AuroraState.defaultIntensity` {0.35, 0.55, 0.75, 1.0} × bands {date, greeting,
  **the completed value inside the ring (ring stroke included in the composited
  background)**, total, caption, complete statement, merged description,
  encouragement, both section headers, `ActivityCard` titles and metadata,
  `EmptyState` title/body}. **Minimum region ratio across the sweep governs**;
  floor **≥ 4.5:1** per band. A failing band may move the **peak** inside
  `[0.15, 0.35]` — **never** the four-stop profile or `ScrimPeakStart/End`.

- **Accessibility consequences.** Reading order becomes Settings → date → greeting
  → figure → total → caption → encouragement → Remaining heading → rows → Completed
  heading → rows → FAB → navigation, and the **greeting gains
  `Modifier.semantics { heading() }`** so heading navigation, not a permanently
  visible band, is the user's orientation tool. One merged `contentDescription`
  over the progress lockup is preserved, so the figure/total split creates no
  second announcement. Settings 48 dp, FAB 56 dp, completion control 48 dp — all
  unchanged. **Large-font behaviour strictly improves**: every foreground band is
  inside the scroll surface and **no foreground element is height-constrained by an
  atmosphere**, which is exactly the D1 failure class, so clipping by font scale is
  unreachable. Reduced motion reaches fewer call sites and adds nothing.

- **Consequences.** **No domain, repository, Room, DataStore, ViewModel,
  navigation, dependency, manifest or build change.** Changed: `TodayScreen.kt`
  (collapse machinery and the Hero item deleted; atmosphere backdrop + Settings
  chrome added; `HomeHeader` becomes a scroll item; `heroHeightPx` →
  `chromeHeightPx`), `HomeHero.kt` **deleted**, new `HomeAtmosphere.kt`,
  `HomeHeader.kt` and `HomeProgress.kt`, and `res/values/strings.xml`
  (`today_hero_fraction` → `"%1$d"`; new `today_hero_fraction_total` →
  `"/ %1$d"`). Unmodified: `MomentumHeroArtwork.kt`, `MomentumAurora.kt`,
  `LiquidGlass.kt`, `ProgressOrb.kt`, `MomentumNavGraph.kt`, `TodayViewModel.kt`,
  every theme file. Contract tests that pinned the crown/collapse model
  (`HomeHeroAssetsTest`, `HomeScrollSurfaceTest`, `HomeAccessibilityTest`,
  `HomeHeroCompositionTest`, `HomeHeroStateTest`, `QuickAddTest`,
  `HomeSectionMotionTest`) **must be rewritten to the retained outcomes, not
  deleted**; `HomeStatesTest` is unaffected and must stay green.

- **Approval.** Recorded as the Architect's TASK-017 proposal under the human brief
  of 2026-10-01. **This note requires human approval in full — including the
  rejection of the crown model, the scrollable date/greeting decision, and the
  `ArtworkWindow` derivation — before any implementation is authorized.** No code
  accompanies this note; `adb` is unavailable, so no instrumented or on-device
  verification is claimed for it.

### ADR-48 Amendment Note 4 — the progress lockup: the ring frames the completed value and the total becomes subordinate; the FAB's fill becomes glass-over-`primary` at `MomentumGlass.heroFill` (Architect, TASK-017, pending human approval)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **adds to** ADR-47 Amendment Note 4 (the
  persistent atmosphere and the fixed/scrollable classification) and **amends
  ADR-48 §1 for the `Fraction` branch only** and **ADR-47 §6's word "solid"**.
  ADR-48 §2, §3, §4, §5, §6 and ADR-48 Amendment Notes 1, 2 and 3 stand exactly as
  written.

- **Diagnosis (from the current code, both themes).** `today_hero_fraction =
  "%1$d / %2$d"` is **one** `Text` at **one** style (`MomentumFigureDisplay`,
  Display 40/48/600), so the completed value and the total have **identical size,
  weight and colour** and there is no hierarchy between them at all. `ProgressOrb`
  is a `Row` sibling separated by `HeroOrbGap` and vertically centred, so its
  position is decided by **available width rather than by meaning**, and with
  `showPercent = false` it carries **no information** — a ring whose only role is
  to be a ring. That is the reported incoherence, and it is structural rather than
  tonal.

- **Decision (this note).**
  1. **One representation, still.** ADR-48 §1's core invariant — a single progress
     representation, no second percentage, `showPercent = false` — is untouched.
  2. **The ring frames the completed value instead of accompanying it.**
     `ProgressOrb` keeps its exact drawing and its exact `HeroRingSize = 80 dp`
     (unchanged: one Display line height 48 dp + 2 × `MomentumSpacing.standard`),
     and the completed value is composed **inside** it. The ring is now positioned
     by meaning — it is the progress frame of the figure — and its position is
     invariant at every width, digit count and font scale, which is the direct
     answer to the requirement that it must not be placed beside the number
     because horizontal space happens to be available. `HeroOrbGap` and the `Row`
     are deleted.
  3. **The fraction becomes two typographic levels, not one string.**
     `today_hero_fraction` → `"%1$d"`; new `today_hero_fraction_total` →
     `"/ %1$d"`. The completed value is at `MomentumFigureDisplay`; the total sits
     directly beneath it at the design system's **Title** token. Both are
     `textPrimary`: the subordinate level is carried by **size and weight only**,
     never by opacity or `textSecondary`, per ADR-47 §7 and ISSUE-020's remedy.
     ADR-48 §2's `FontFeatureSettings("tnum")` is what makes the split safe — the
     completed digit does not change width as it advances.
  4. **The caption `completed today` stays**, `labelLarge`, `textPrimary`, beneath
     the total on the same 20 dp screen axis. No copy is invented, removed or
     reworded.
  5. **One merged `contentDescription`** (`today_hero_progress_desc`) covers the
     whole lockup, so the split is never double-read and the ring stays decorative.
     Reading order: completed value → total → caption.
  6. **The `Complete` branch is unchanged**: the ring full at the screen's left
     axis with "Today is complete" beside it. ADR-48 §1's "ring full" clause
     **stands**; only the `Fraction` branch — `0 / 1` beside an empty ring — was
     incoherent. The `Hidden` branch still renders nothing at all.
  7. **The Architect fixes the relationship, not the pixels.** Exact insets and radii
     are the Developer's choice within the existing token grid.
  8. **`MomentumFab`'s fill becomes glass-over-`primary`.** The two hard-coded fill
     alphas (`0.95f` / `0.92f`) become **`MomentumGlass.heroFill` (0.68)** applied to
     `c.primary`, with the top-edge illumination streak and the theme-resolved edge
     the `GlassLevel.HERO` path already uses. **No new token, no new value, no new
     component, no new API, no hard-coded colour added.** ADR-47 §6's word "solid"
     is what changes. **Unchanged:** `CircleShape`, `MomentumFabSize = 56.dp`,
     `MomentumIcons.add`, `MomentumIconSizes.prominent`, `glassPress`,
     `Role.Button`, the `contentDescription` parameter, ADR-48 Amendment Note 3's
     edge and `MomentumGlass.heroElevation`, and the placement and 96 dp reservation
     of ADR-48 Amendment Note 2.

- **Why the FAB changes (and why not its colour).** Home's entire foreground
  vocabulary is Liquid Glass — the bottom bar at `heroFill`, Settings at
  `controlFill`, cards at `cardFill`. `MomentumFab` was the only foreground object
  with a hard, fully-opaque, saturated boundary, and the only object floating on
  bare atmosphere with nothing relating it to the content above or the bar below.
  ADR-48 Amendment Note 3 correctly diagnosed this as an **edge**-vocabulary
  problem and fixed only the edge; the **fill** is what still separates it, because
  under `AURORA = atmosphere · LIQUID GLASS = interface` the foreground must be
  glass over atmosphere and the atmosphere must remain perceptible *through and
  around* it — which a 0.95-alpha disc is the one element that prevents. At
  `heroFill` the disc keeps the theme's `primary` identity and remains the
  unmistakable primary action while joining one material family with the glass bar
  below and the glass Settings control above, so Home's three floating objects form
  one depth stack.

- **Explicitly rejected.** A saturated solid disc (current — opaque, breaks
  atmospheric continuity); a gradient or aurora FAB and a gradient ring (new visual
  language, forbidden); **full `GlassLevel.HERO` glass with a `c.primary` glyph**
  (loses primary-action salience against the atmosphere — offered to the human as
  the alternative in `task.md` §8.16 item 6, with glass-over-`primary`
  recommended); an optional `border` / `emphasised` parameter so only Home could
  differ (new component architecture, forbidden by ADR-47 §6); and moving the FAB
  into the bottom bar (placement is confirmed correct on device).

- **Shared call-site consequence — requires explicit human approval, exactly as
  ADR-48 Amendment Note 3 recorded.** `MomentumFab.kt` is shared with
  `HabitsScreen` (`cd_create_habit`), so Habits' FAB becomes translucent instead of
  solid: same component, size, glyph, placement and behaviour. Nothing else on
  Habits changes. This cannot be scoped to Home without forbidden changes. The
  Architect recommends approval; if the human refuses it, the only lawful outcomes
  are to accept the disconnection and re-open D6, or to defer `MomentumFab` to a
  future task that owns it as a component.

- **Consequences.** Changed: `presentation/screens/today/HomeProgress.kt` (new),
  `presentation/components/MomentumFab.kt` (**two expressions**),
  `res/values/strings.xml` (one string changed, one added). No domain, schema,
  ViewModel, navigation, dependency or build change. `QuickAddTest`'s
  `Brush.verticalGradient` assertion and its FAB-material assertions must be
  rewritten to the retained outcomes; its placement and 48 dp assertions stand.

- **Approval.** Recorded as the Architect's TASK-017 proposal under the human brief
  of 2026-10-01. **This note requires human approval — including the shared
  Habits call-site consequence and the choice between glass-over-`primary` and full
  glass — before any implementation is authorized.** No code accompanies this note;
  `adb` is unavailable, so no instrumented or on-device verification is claimed for
  it.

### ADR-47 Amendment Note 5 — the Home top chrome is persistent: Date + Greeting + Settings are fixed, the artwork's datum is the window top, and the scroll boundary is layout (Architect, TASK-017, human ruling 2026-10-01 §4.10.4 — approved)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **supersedes one clause of ADR-47 Amendment
  Note 4** — its §8 element classification (which placed **date** and **greeting**
  in the scrollable content) — and **withdraws its §9 placement invariant** ("what
  floats is glass, and what floats is what must always be reachable"), which
  survives only as the reason *Settings* is glass rather than bare ink. Every other
  clause of ADR-47 Amendment Note 4 and ADR-48 Amendment Note 4 stands exactly as
  written, including the 184 dp `ArtworkWindow`, its derivation, the full-atmosphere
  four-stop scrim, the progress lockup, the FAB treatment, the deletion of the
  collapse, and the performance result.

- **Ruling recorded.** The human ruling of 2026-10-01 §4 approved the TASK-017
  proposal and **reversed its date/greeting classification**. The binding behaviour
  contract is now `Date → fixed · Greeting → fixed · Settings → fixed`, forming
  **one coherent persistent Home top chrome**, against
  `Progress / Encouragement / Activities → scrollable`. Scrollable content must
  **never** overlap the chrome and must have **a proper scrollable region below
  it**, achieved with **no** collapse, parallax or scroll choreography. The chrome
  must read as coherent with the Aurora environment and **not** as an unrelated
  opaque header. The architect retains ownership of the visual composition.

- **Decision (this note).**
  1. **The layering tension is resolved: the chrome is drawn *over* the artwork,
     and the artwork's datum is the physical window top.** Ruling §3 ("the artwork
     forms the upper visual region of the Aurora atmosphere") and §4 ("Aurora /
     artwork below the chrome") are **not** in conflict: §3 is a *geometry* statement
     and §4 is a *z-order* statement, and both hold. The atmosphere spans the whole
     content area; the artwork occupies its upper 184 dp measured from the window
     top; the chrome floats over that region; the scrollable region begins at the
     chrome's measured bottom edge.
  2. **Why the datum must be the window top, and why this is not a preference.**
     `MomentumHeroArtwork` generates **no top fade**: `topDissolve` produces no
     stop and the profile is fully opaque from the container's top edge, because
     its KDoc and ADR-39 both state that the container's top edge *is* the
     physical window top edge, so there is no page background above it to blend
     into. Starting the artwork **below** the chrome would put its top edge against
     page background, forcing either a **hard horizontal edge** (forbidden) or a
     **new top fade** — which that component explicitly reserves as a future ADR
     decision "for a container whose top edge borders page background", for which
     "no such caller exists today". Creating that caller is precisely the change
     ADR-39 deferred, and the component file is not to be modified. The artwork's
     top edge must therefore stay the window's top edge, which **forces** the
     chrome to be drawn over the artwork.
  3. **The four coherence guarantees that make the chrome part of the environment
     rather than a header.** (a) **No plate** — the chrome contains no `Surface`,
     `Card`, `LiquidGlass`, `.background(`, `HorizontalDivider`, `shadow(` or
     `graphicsLayer` between its own edges; its copy sits on the raw atmosphere +
     scrim exactly as the scrollable progress copy does. The only permitted
     sub-surface is the 48 dp `LiquidGlassIconButton` (Settings), copy-adjacent
     chrome rather than a copy backdrop — the same exception the old Hero already
     documented. (b) **No divider and no edge** at the chrome's bottom edge;
     separation is by negative space alone. (c) **One continuous scrim** spanning
     the whole content area with **no stop at the chrome's bottom edge**, so chrome
     and content sit under the same gradient. (d) **One axis, one ink, one type
     scale, one material** — the same 20 dp screen axis, the same `textPrimary`,
     the same design-system type scale, and the same `GlassLevel.CONTROL` glass as
     every other control.
  4. **`ArtworkWindow` remains 184 dp with the window top as its datum**, derived
     exactly as ADR-47 Amendment Note 4 §3 recorded
     (`HeroRingSize + MomentumSpacing.standard + ArtworkHandOff`). The chrome's
     height is **neither added to it nor subtracted from it**: the window does not
     grow when the chrome grows. It remains **constant in every state** and is
     **never** a function of activity count, empty/non-empty state, copy height,
     the chrome's measured height, encouragement presence or scroll position. Only
     theme and width affect the *crop*, never the height. BOUND-1 therefore remains
     structural (`ArtworkWindow − 88 dp = 96 dp ≥ HeroRingSize`, no measurement).
     The chrome's bottom edge is expected to fall inside the artwork's opaque
     region at font scale 1.0; at font scale 2.0 it may extend past the window,
     which is **acceptable and must not be "fixed"** by altering `ArtworkWindow` —
     the scrim spans the whole content area precisely so copy below the artwork
     window stays readable.
  5. **The scroll boundary is produced by ordinary layout, not by scroll
     observation.** The chrome and the scrollable region are **siblings in one
     `Box`**, the region's top edge being the chrome's bottom edge via a
     `padding(top = chromeHeightDp)` derived from a single `onSizeChanged` on the
     chrome. Overlap is therefore **unrepresentable rather than merely prevented**.
     `clipToBounds()` on the region is belt-and-braces for the ruling's
     "appropriate clipping / content bounds". `contentPadding.top` stays `0.dp` —
     any top padding would open a band of atmosphere between the chrome and the
     first item, which is the disconnection the ruling forbids.
     **There is no `NestedScrollConnection`, no scroll-state read, no per-frame
     observation, no `derivedStateOf`, no parallax and no sticky header.** The one
     measurement is a pure function of the chrome's content, window insets and
     width — it changes on font-scale, configuration and greeting-length change and
     **never on scroll** — so the write is idempotent and cannot feed back into
     layout.
  6. **Child declaration order is part of the contract.** Compose composes and
     paints a `Box`'s children in declaration order, and the merged semantics tree
     follows composition order. One order satisfies both: **atmosphere, chrome,
     scroll region, FAB**. Because the chrome and the region are geometrically
     disjoint, paint order between them is irrelevant; the only binding paint
     constraints are that the atmosphere is first and the FAB (which overlays the
     list's tail) is last. Reordering would push the persistent Home identity after
     the content in the traversal order, which is wrong.
  7. **Empty state: the centring region now accounts for the chrome and the
     atmosphere, and §4.10.7's two guarantees are structural, not arithmetic.**

     ```kotlin
     emptyRegion = viewportDp - chromeHeightDp - EmptyDayBottomReserve   // 96 dp, unchanged
     // Box(Modifier.heightIn(min = emptyRegion), contentAlignment = Alignment.Center)
     ```

     `EmptyDayBottomReserve` is unchanged and still used both as the list's
     `contentPadding.bottom` and in this subtraction, so the reserve the list
     honours and the region the card is centred in cannot drift apart. Because the
     atmosphere is a **sibling backdrop** sized to the whole content area and the
     chrome is a **sibling above** the region, the empty item **cannot collapse
     either** — there is no shared box for it to squeeze. This makes the TASK-016
     D1 regression class unreachable. The empty card carries **no CTA** and the
     FAB remains the single creation affordance.
  8. **Accessibility.** The chrome's three elements are **three separate, unmerged
     semantics nodes**: the chrome must **not** use `clearAndSetSemantics` or
     `mergeDescendants`, because a merged chrome would swallow the Settings button
     and make a persistent control unreachable by screen reader. Group visually,
     never semantically. The greeting carries `heading()` and is the screen's first
     heading; the date is plain text; both section headers keep theirs, so heading
     navigation yields **greeting → Remaining → Completed**. `Settings` keeps
     `Role.Button`, its `cd_settings` label, the 48 dp minimum and its
     `c.textSecondary` icon tint — the one place a secondary token is legal,
     because the glyph sits on its own `GlassLevel.CONTROL` glass fill rather than
     as bare ink on the atmosphere (ADR-47 §7 governs *copy over artwork*, and the
     sweep measures this band). The progress lockup keeps exactly one merged
     `contentDescription`. The atmosphere contributes no semantics. **Accessibility
     is never traded for composition**: if font scale 2.0 cannot be satisfied
     without clipping, the fix is the chrome's composition, never semantics, never
     a sub-48 dp target and never a removed heading.
  9. **Contrast: the band list moves; the floor does not.** The ISSUE-020
     region-sample method is unchanged and the **≥ 4.5:1 floor is not weakened**.
     Because the chrome is fixed, its bands move with it, so the sweep gains two
     bands: the **`done` digit inside the ring** (with the ring's
     `textPrimary @ 0.55` stroke included in the composited background) and a
     **seam band** asserting the atmosphere immediately below the chrome's bottom
     edge shows **no hard edge** — the region-sample difference across that seam
     must be within the scrim's own local gradient ramp. A failing band may move
     only the **peak**, inside `[0.15, 0.35]`; the four-stop profile and
     `ScrimPeakStart` / `ScrimPeakEnd` are load-bearing and must not change.
  10. **ADR-35's retirement is unchanged and now better founded.** The status-bar
      top mask is retired on Home: there is no Hero, the chrome is **fixed** and
      therefore never crosses the status bar, and scrollable content begins below
      the chrome's measured bottom edge — so the camera-cutout failure it
      prevented cannot recur. The clamped-px collapse **pattern** (ADR-28/29) is
      not condemned and survives on Habits.
  11. **Tag and symbol mapping.** `HomeHeroTag`, `HeroArtworkTag`, `HeroHeaderTag`
      and `HeroContentTag` are deleted with `HomeHero.kt` and are replaced by
      `HomeAtmosphereTag` + `HomeScrollRegionTag`, `AtmosphereArtworkTag`,
      `HomeChromeTag` and `HomeProgressTag`. Because the instrumented suites look
      tags up by string, a renamed tag is a silent test failure rather than a
      compile error, so this mapping is part of the decision. `HeroFigureState`
      and `HeroRingSize` move to `HomeProgress.kt`; `HeroScrim`, `ScrimPeak*`,
      `ScrimPeakStart`, `ScrimPeakEnd` and `ArtworkWindow` move to
      `HomeAtmosphere.kt`; `HomeTopChrome` and `ChromeBottomInset` (a named alias
      for `MomentumSpacing.section`, the only spacing value introduced) live in
      `HomeChrome.kt`. `TodayLoadingSkeleton` is updated to the new silhouette,
      keeping its single accessible label and its no-spinner / no-shimmer contract.

- **Explicitly rejected in this note.** A collapse, parallax, or any
  scroll-driven chrome treatment (ruling §4.10.1/§4.10.4/§4.10.5). A `Surface`,
  glass panel, plate, divider or shadow behind the chrome (§4.10.4). Starting the
  artwork below the chrome, which would require a hard edge or a new top fade
  (§8.17.2's derivation). `contentPadding(top = chromeHeight)` on a full-height
  list, which lets later items scroll up underneath the chrome and so requires
  either a forbidden plate or re-introduces the overlap problem. A sticky/pinned
  duplicate of the date and greeting. Any spacer, offset or padding literal added
  to make the composition line up. The three crown tests are **deleted because the
  crown no longer exists** and are **replaced** by bounds tests on the atmosphere,
  the chrome, the scroll boundary, the constant artwork window, BOUND-1 and the
  empty-card centring — retained outcomes, not removed coverage.

- **Consequences.** Changed: `TodayScreen.kt` (four declared children in the order
  above; collapse machinery and `BoxWithConstraints` centring arithmetic replaced
  by the measured-chrome layout; `emptyRegion` recomputed from the chrome height),
  `HomeHero.kt` **deleted without ever being edited** (§4.10.13 forbids reshaping
  it), new `HomeAtmosphere.kt` / `HomeChrome.kt` / `HomeProgress.kt`,
  `TodayLoadingSkeleton` updated, `MomentumFab.kt` (glass-over-`primary`, per
  ADR-48 Amendment Note 4), `res/values/strings.xml` (fraction split). Unmodified:
  `MomentumHeroArtwork.kt`, `MomentumAurora.kt`, `LiquidGlass.kt`,
  `ProgressOrb.kt`, `MomentumNavGraph.kt`, `TodayViewModel.kt`, every theme file,
  every non-Home screen, and all domain, schema, repository, navigation, dependency
  and build configuration. Seven Home test files are rewritten to the retained
  outcomes; `HomeStatesTest` is unaffected.

- **Approval.** Recorded under the human ruling of 2026-10-01 §4.10, which approved
  the TASK-017 proposal, accepted the shared `HabitsScreen` FAB consequence (§4.10.8)
  and authorized implementation. This note **specifies** the §4.10.4 amendment;
  it introduces no new decision requiring a further ruling. **No code accompanies
  this note.** `adb` is unavailable in this environment, so no instrumented or
  on-device verification is claimed or implied for any part of TASK-017.

### ADR-47 Amendment Note 6 — the ISSUE-020 contrast re-measurement for the TASK-017 composition is on record: 0 of 1664 samples below 4.5:1, governing minimum Light 4.93:1 · Dark 9.00:1, both peaks unchanged (Architect, TASK-017, 2026-10-01)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **records a measurement result only** — it
  supersedes no clause and changes no decision. It closes the acceptance
  measurement called for by ADR-47 Amendment Note 4 (the ISSUE-020 baseline is
  superseded **by addition of this note**, never by rewrite), by ADR-47 Amendment
  Note 5 §9, and by `task.md` §8.15 / §8.17.4. **Every approved value stands
  exactly as recorded above.**

- **Origin and why it is recorded here.** The Developer ran the measurement and
  correctly **escalated rather than substituting an implementation file as the
  ADR record**: AGENTS.md §21 makes this file the Architect's to own, §22 forbids
  an agent widening its own permission boundary, and the Developer's grant does
  not extend to `docs/**`. The result was held in `task.md` §9.5 and in
  `HomeAtmosphere.kt`'s `ScrimPeak*` KDoc; this note is the canonical record.

- **Method — unchanged and re-run from scratch.** The canonical ISSUE-020
  **region-sample** methodology of ADR-47 Amendment Note 1, applied to the
  TASK-017 composition: composite page background + Aurora field + artwork
  (cover-crop `Alignment.BottomCenter`, shared 88 dp `DstIn` dissolve) + the
  **full-content-area** four-stop scrim, reduce each band's text rectangle to one
  mean background colour per channel, compute the WCAG 2.x ratio against the band's
  ink token, and let the **minimum region ratio across the sweep govern**. A
  per-pixel minimum over the artwork's high-frequency detail remains informational
  only. **TASK-016's table was deliberately not carried forward** (§4.10.12).

- **Sweep.** 2 themes × widths {360, 412} dp × content-area heights {560, 720} dp
  (the scrim's extent *is* the content area, so both are swept) ×
  `AuroraState.defaultIntensity` {0.35 REST, 0.55 PROGRESS, 0.75 COMPLETION, 1.0
  MILESTONE} × the chrome's bottom edge at font scale {1.0, 2.0} × 3 day states
  {empty, populated, complete} × encouragement {on, off} × all 13 bands of
  `task.md` §8.17.4 — including the two bands added for the fixed-chrome
  composition: **`done`-inside-the-ring** (the ring's `textPrimary @ 0.55` stroke
  composited into the sampled background) and the **seam** band.
  → **1536 region samples + 128 seam samples. 0 below 4.5:1.**

- **Worst case per band across the sweep** (twelve contrast bands; the thirteenth is
  the seam, reported separately below).

  | Band | Light @ 0.26 | Dark @ 0.22 |
  |---|---|---|
  | date | 10.06:1 | 16.90:1 |
  | greeting | 10.09:1 | 16.93:1 |
  | **Settings icon tint** (`textSecondary` on its own `CONTROL` glass) | **4.93:1** | 9.01:1 |
  | **`done` inside the ring** (ring stroke in the background) | 9.26:1 | **9.00:1** |
  | `/ total` | 13.92:1 | 14.89:1 |
  | `completed today` caption | 14.22:1 | 15.71:1 |
  | `Today is complete` statement | 12.68:1 | 14.01:1 |
  | merged `today_hero_progress_desc` | 9.26:1 | 9.00:1 |
  | encouragement | 14.30:1 | 16.18:1 |
  | section headers | 14.00:1 | 15.34:1 |
  | `ActivityCard` titles/metadata | 15.04:1 | 15.65:1 |
  | `EmptyState` title/body | 15.04:1 | 15.65:1 |

- **Result.** **Governing minimum: Light 4.93:1 · Dark 9.00:1** — both clear the
  **≥ 4.5:1** floor with margin. The floor was **not** weakened to accommodate the
  new composition (§4.10.12). The **weakest band is the Settings glyph**, which is
  the one place a secondary token is lawful under ADR-47 Amendment Note 5 §8
  (`task.md` §8.17.6 rule 5): the glyph sits on its own `GlassLevel.CONTROL` glass
  fill rather than as bare ink on the atmosphere, so it clears the body-text floor
  regardless. No copy band over the atmosphere is close to the floor.

- **Band 13 — the seam shows no hard edge, with one recorded method deviation.**
  The atmosphere's step across `chromeBottom` is **0.96** channel-sum per dp worst
  case, against a **median 0.64** and a **p99 1.50** for the artwork's own per-dp
  gradient over the same span; the scrim's own contribution there is
  **0.005/dp**. The step is therefore **artwork-driven, not composition-driven**,
  and the seam is indistinguishable from the illustration's existing local ramp.
  **Deviation, method wording only:** `task.md` §8.17.4 band 13 was written as "no
  step larger than the scrim's own per-dp derivative there". Taken literally that
  is unsatisfiable for illustrated content at any approved peak — the artwork's own
  local gradient alone is roughly 30× the scrim's derivative — so it cannot
  discriminate a composition edge from artwork detail. The measurement therefore
  used the discriminable form: the step at the seam versus the artwork's **own**
  local per-dp step distribution across the same span. **The acceptance intent
  ("no hard edge at `chromeBottom`") is met, and the underlying geometry is
  structural rather than measured** — the scrim spans the whole content area and
  generates no stop at the chrome's boundary (ADR-47 Amendment Note 5 §3c), so no
  composition edge exists there to find. This deviation is surfaced here for the
  Reviewer rather than buried in an implementation file.

- **Escalation: not required. Every approved value stands unchanged.** No band
  failed, so **both peaks remain `ScrimPeakLight = 0.26f` / `ScrimPeakDark =
  0.22f`**, inside the approved `[0.15, 0.35]` envelope. `ScrimPeakStart = 0.20f`,
  `ScrimPeakEnd = 0.80f` and the four-stop profile were **not** touched — only the
  peak value was ever in scope. **The floor, the scrim profile, the peak stops and
  every ink token are untouched**; ADR-47 §7's prohibition on `textSecondary` over
  the atmosphere is unaffected, its one carve-out being the Settings glyph on its
  own glass. BOUND-1 remains structural (`ArtworkWindow − 88 dp = 96 dp ≥
  HeroRingSize`), unaffected by this measurement.

- **Verification status — stated plainly.** This is a **computational contrast
  measurement**, not an instrumented or on-device result. `adb` is unavailable in
  this environment and **no instrumented suite has been executed, and no screenshot,
  pixel or device claim is made or implied** for any part of TASK-017. The figures
  above are the Developer's, recorded here as reported; a **Reviewer is
  independently re-verifying them** against the same method. The device-matrix
  verification of `task.md` §8.15 remains with the human.

### ADR-47 Amendment Note 7 — the artwork window becomes reference-derived and width-proportional; BOUND-1 is re-derived and the Note 6 contrast baseline is superseded (Architect, TASK-018, pending human approval)

- **Instrument.** Append-only. No ADR-47 or ADR-48 text above is rewritten,
  deleted or restated. This note **amends** ADR-47 Amendment Note 4 §3 and
  ADR-47 Amendment Note 5 §4 — the single clause that made `ArtworkWindow` a
  constant independent of everything, including width — and **supersedes** ADR-47
  Amendment Note 6's *baseline by addition of this note*, never by rewrite.
  ADR-47 §1/§4/§5/§7/§8, Amendment Note 1 (the region-sample **method** and the
  `[0.15, 0.35]` ceiling), Amendment Note 3's BOUND-2 requirements,
  Amendment Note 5's §1–§3/§5–§11, ADR-28/29, ADR-30, ADR-31/32/33/37/39 and
  ADR-48 stand exactly as written.

- **Root cause (from the approved reference, not from taste).** The Stitch Home
  reference renders as a **640 × 1600** image authored in CSS px at
  `1rem = 16px`, i.e. a **320 × 800 dp viewport at 2×**. Its Aurora band is
  `h-[460px]` = **230 dp of a 320 dp viewport = 0.71875 × width**, and it
  visibly continues behind the progress card. Momentum's
  `ArtworkWindow = HeroRingSize + MomentumSpacing.standard + ArtworkHandOff`
  = **184 dp**, which is 0.511 × width at 360 dp — the reference's atmosphere is
  visibly a broader sky, and at the reference's own width Momentum's band is 46 dp
  shorter than the design it must reproduce. Amendment Note 5 §4's
  "the window does not grow when the chrome grows … constant in every state …
  only theme and width affect the *crop*, never the height" was correct for the
  composition it governed and is now **incomplete**: it forbids *content*,
  *state* and *scroll* dependence (all of which are still forbidden, verbatim)
  but says nothing that makes a width-proportional band wrong — it simply had no
  reason to allow one.

- **Decision (this note).**
  1. **`ArtworkWindow` becomes one derived, reference-derived value:**

     ```kotlin
     ArtworkBandFraction = 460f / 640f                        // reference band / viewport width
     ArtworkWindow        = ArtworkBandFraction * windowWidth  // 230 dp @320 · 258.75 dp @360 · 296.1 dp @412
     ```

     `windowWidth` is `BoxWithConstraints.maxWidth` read from the **existing**
     root — one read, never scroll-dependent, no new window-size observer.
  2. **Every independence clause survives verbatim.** The window is still never a
     function of activity count, empty/non-empty state, copy height, the chrome's
     measured height, encouragement presence or scroll position, and it is still
     identical on an empty day. **Width is the only newly permitted input.**
  3. **The scrim is untouched.** Stitch's own fade mask —
     `rgba(page, 0) 0% → 0.15 50% → 0.75 80% → page 100%` — is
     structurally identical to Momentum's existing four-stop scrim, so
     `ScrimPeakStart = 0.20f`, `ScrimPeakEnd = 0.80f`, the four-stop profile,
     `ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f` and the `drawWithCache`
     brush all stand. Nothing about the readability mechanism changes.
  4. **BOUND-1 is re-derived**, because `HeroRingSize` is no longer the largest
     opaque element the atmosphere carries (ADR-48 Amendment Note 5 replaces it
     with a 96 dp ring):

     ```
     ArtworkWindow - ArtworkHandOff  >=  ProgressCardRingSize + 2 * MomentumSpacing.standard   // >= 128 dp
     142.75 dp @360 · 142 dp @320 · 208 dp @412
     ```

     *Assertions:* (a) the window is proportional to width and equal at equal
     width in every day state, both themes and at both font scales; (b) the
     inequality holds at 320/360/412 dp, tolerance 1 dp; (c) the window is never
     derived from `ProgressCardRingSize`, `chromeHeight`, `state.isEmpty`,
     `totalCount`, `doneCount`, `viewportDp` or scroll; (d) `ArtworkHandOff`
     stays `88.dp` — the shared component's own value, restated as a
     measurement, because ADR-30's caller contract lets callers pass band amounts
     only and ADR-47 Amendment Note 3 already rejected duplicating it.
  5. **The contrast baseline is re-run from scratch.** A wider artwork band puts
     more high-frequency illustration under the same scrim, so ADR-47 Amendment
     Note 6's figures do not transfer. The ISSUE-020 **region-sample method**
     (ADR-47 Amendment Note 1) is re-run for the TASK-018 composition: both
     themes × widths {360, 412} dp × content-area heights {560, 720} dp ×
     `AuroraState.defaultIntensity` {0.35, 0.55, 0.75, 1.0} × chrome bottom edge
     at font scale {1.0, 2.0} × 3 day states × encouragement {on, off} × the
     band list of `task.md` §8.15, **including the new glass-card bands**. The
     floor is **≥ 4.5:1**, unweakened; the minimum region ratio governs; a
     failing band may move **only the peak**, inside `[0.15, 0.35]` — never the
     four-stop profile, the stops, the ink or the artwork.
  6. **Everything else stands unchanged:** the artwork's datum is the physical
     window top and there is still no top fade (ADR-39); the chrome floats over
     it; the scrim spans the whole content area with no stop at the chrome's
     bottom edge; the atmosphere is a sibling backdrop of the whole content area;
     the scroll boundary is layout, not observation; nothing is added to
     `MomentumHeroArtwork.kt`; no artwork is created, replaced, re-tinted,
     re-cropped, zoomed or parallaxed.

- **Options.** (a) Keep 184 dp and accept a visibly narrower atmosphere than the
  approved reference — a reinterpretation of the design; (b) make the window a
  function of the chrome or of content — forbidden by Amendment Note 4 §3 and the
  source of the TASK-016 defect; (c) a fixed larger constant (e.g. 259 dp) — one
  number that is correct at exactly one width and wrong everywhere else;
  (d) proportional to width, every other clause intact (chosen).

- **Approval.** **Requires explicit human approval before implementation**
  (AGENTS.md §4/§21), because it amends a clause of a human-ruled note
  (Amendment Note 5 §4, human ruling 2026-10-01 §4.10.4) and supersedes a
  recorded contrast baseline.

### ADR-47 Amendment Note 8 — the artwork window becomes the complete artwork's intrinsic 1536/1024 ratio, width-proportional (1.5 × width); AN7's `460/640` band ratio, its derived window values and its BOUND-1 arithmetic are superseded, and AN6's contrast baseline is now formally superseded (Architect, TASK-018, 2026-10-02 — human ruling `task.md` §4.8/§4.9, closeout §13.3)

- **Instrument.** Append-only. No ADR text above is rewritten, deleted or
  restated. This note **supersedes only Amendment Note 7's artwork-band
  decision** — the `ArtworkBandFraction = 460f / 640f` reference band ratio,
  every derived `ArtworkWindow` value AN7 stated (230 / 258.75 / 296.13 dp at
  320 / 360 / 412 dp), and the BOUND-1 arithmetic computed from them — **by
  addition of this note**. It **retains, verbatim**, AN7's independence clauses,
  its untouched four-stop scrim, profile and peaks (`ScrimPeakStart = 0.20f`,
  `ScrimPeakEnd = 0.80f`, `ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f`
  inside `[0.15, 0.35]`), its re-derived BOUND-1 **structure** against
  `ProgressCardRingSize`, its Decision-item-6 unchanged-clause list, and every
  other statement AN7 made. AN7's own Instrument clause stays in force: it
  still amends ADR-47 Amendment Note 4 §3 and Amendment Note 5 §4's "184 dp
  constant" clause, because the window is still one derived value — never a
  measured one, never a constant — and it is still `BoxWithConstraints.maxWidth`
  that derives it, with no new observer.

- **Governing decision (newer, and it wins where it differs).** The human's
  **Device Finding #1** (`task.md` §4.8, human ruling 2026-10-01; verified PASS
  and background frozen in `task.md` §4.9) is the governing decision for the
  artwork window. The **complete** existing artwork (`habits_hero_light` /
  `habits_hero_dark`, 1024 × 1536) renders in full — waves, glow, sparkles and
  stars, nothing important cropped — **top-aligned**, scaled proportionally to
  the screen width, over the unchanged lavender / dark atmospheric continuation,
  as one fixed, non-scrolling composition:

  ```kotlin
  ArtworkIntrinsicRatio      = 1536f / 1024f                                     // 1.5
  artworkWindow(windowWidth) = (ArtworkIntrinsicRatio * windowWidth.value).dp    // 480 / 540 / 618 dp @ 320 / 360 / 412
  ```

  `ContentScale.Fit`, **no crop**: no object-cover, no vertical or horizontal
  crop, no zoom, no parallax, no collapse, no scroll observation, no per-frame
  background movement, and no scrim that obscures the Aurora. The shared 88 dp
  bottom dissolve, ADR-39's window-top datum with no top fade, the four-stop
  scrim over the whole content area and the layout-produced scroll boundary all
  stand. This is a width-proportional window, so AN7's withdrawal of the
  184 dp constant clause and its width-only-permitted-input clause both survive.

- **BOUND-1 on the new geometry (structure retained, numbers superseded).**
  `artworkWindow(w) - ArtworkHandOff >= ProgressCardRingSize + 2 *
  MomentumSpacing.standard` (>= 128 dp) — the AN7 structure and its
  `ArtworkHandOff = 88.dp` unchanged — yields **392 / 452 / 530 dp** at
  320 / 360 / 412 dp. The window grows, so the inequality holds with more
  margin than the withdrawn band.

- **Closure condition satisfied — AN6's contrast baseline is formally
  superseded and AN7's Decision-item-5 contrast re-run is complete.**
  `ISSUE-020` is **CLOSED by TASK-018** (`task.md` §8.20 Home-scoped remediation
  approved under §4.11; `ISSUES.md`; `PROJECT_STATUS.md` F-HOME is
  `FULLY_VERIFIED`). The note deferred while ISSUE-020 FAILed
  (`task.md` §10.11, §13.0) is therefore unblocked. On record: the
  final-geometry sweep **FAILed** and was remediated by targeted local
  foreground/material corrections, after which the **bounded region-sample
  re-measurement PASSES in Light and Dark with zero FAIL rows** at unchanged
  floors (4.5:1 text / 3.0:1 non-text), with the ring arc decorative under
  §4.11 CHANGE 5; thin margins cleared at emerald boundary 3.09, ring track
  3.24, ring-% Dark 4.78, check glyph Light 4.84. **No peak moved** —
  `ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f` remain inside
  `[0.15, 0.35]` — and no scrim stop, ink token, artwork or theme token
  (`Tokens.kt`, `Color.kt`) was changed. Gradle validation: **293/293 unit
  tests, 42 suites, 0 failures** (`--rerun-tasks`), `assembleDebug` and
  `assembleDebugAndroidTest` exit 0; **device verification PASS** in Light and
  Dark. The exact per-band figures live in `task.md` (§9.A, §9.B, §13.3), not
  here.

- **Recorded limitation (not a defect).** Device verification ran at **412 dp**
  only (emulator, Light and Dark). **320 dp and 360 dp widths and font scale 2.0
  were not device-tested**, and the instrumented suites were not executed. This
  is a limitation of coverage, not a defect: the window's width-proportionality,
  the BOUND-1 inequality at all three widths, and `Fit`-not-`Crop` on Home's
  call site are pinned by assertions in `HomeAtmosphereAssetsTest` and
  `HomeCompositionTest`.

- **What remains historically valid.** Nothing earlier is rewritten. AN7's
  `460/640` ratio, its 230 / 258.75 / 296.13 dp values and its BOUND-1
  arithmetic remain on record as history and are **obsolete for implementation**:
  `task.md` §4.9 identifies the band ratio as "ADR-47 AN7 §2's obsolete band
  ratio", and the contrast figures measured against the withdrawn band must not
  be inherited or re-quoted as current. Standing unchanged and authoritative:
  ADR-47 §1/§4/§5/§7/§8, Amendment Note 1's region-sample **method** and the
  `[0.15, 0.35]` peak ceiling, Amendment Note 3's BOUND-2, Amendment Note 5
  §1–§3/§5–§11, AN7's independence clauses / scrim / peaks / re-derived BOUND-1
  structure, and ADR-28/29, ADR-30, ADR-31/32/33/37/39.

### ADR-48 Amendment Note 5 — the Home progress card is the horizontal Aurora lockup: text block left, gradient ring with the percentage inside right, breakdown row beneath a divider (Architect, TASK-018, pending human approval)

- **Instrument.** Append-only. No ADR-48 text above is rewritten, deleted or
  restated. This note **supersedes ADR-48 §1's "no second percentage" and ADR-48
  Amendment Note 4 §2's "the ring frames the completed value"** **for Home
  only**, and amends ADR-47 Amendment Note 5 §9's band list. ADR-48 §2, §3, §4,
  §5, §6 and Amendment Notes 1–3, and ADR-47 §1/§7/§8 stand exactly as written.

- **Why the reversal (the requirement, not a preference).** The approved Stitch
  Home reference and the human brief (`task.md` §4.2 region C) specify a
  **horizontal** composition — "text block left, ring right, breakdown row beneath
  a divider", "circular percentage ring **with the percentage inside**",
  "Ring + textual metric read as one cohesive unit" — and **explicitly reject**
  the TASK-017 vertical "ring above the count" layout. That is the binding
  requirement under AGENTS.md §2.1.

- **Decision (this note).**
  1. **One lockup, four regions.** The card is
     `liquidGlass(RoundedCornerShape(MomentumRadii.control), GlassLevel.HERO, hue = c.auroraLavender)`,
     padded `MomentumSpacing.standard`, containing (a) a `Row` whose left column
     is `weight(1f)` and carries the fraction + its subordinate suffix on one
     baseline and the encouragement/statement beneath, and whose right member is
     a `size(ProgressCardRingSize = 96.dp)` `Box` containing `ProgressOrb` with the
     percentage and the word `done` composed **inside** it; (b) a hairline
     `HorizontalDivider` at `c.textPrimary.copy(alpha = 0.08f)`; (c) a
     `Spacer(MomentumSpacing.compact)`; (d) a `SpaceBetween` breakdown row of two
     8 dp dots with their labels.
  2. **The ring carries the percentage; `showPercent = false` stays.** The
     percent is composed by `HomeProgress.kt` from the **target** progress, not the
     animated sweep, so the number is stable while the arc sweeps under it. The
     ring is `96.dp` (the reference's `w-24 h-24`); `ProgressOrb`'s stroke is
     `size × 0.075` = 7.2 dp, so **no stroke parameter is added**.
  3. **The arc is a gradient, which is the one genuinely new drawing
     capability.** `ProgressOrb` gains a single additive parameter
     `arcBrush: Brush? = null`; when non-null it replaces the solid `c.primary`
     base arc and **nothing else changes** (track, halo, lobes, glow and the
     `showPercent` behaviour are identical, and `null` is byte-identical to today
     for every existing caller). Home passes
     `Brush.linearGradient(listOf(c.success, c.auroraBlue, c.auroraViolet))` —
     **three existing theme tokens, no new colour.**
  4. **The ring's track keeps `c.textPrimary @ 0.55`.** The reference's
     `rgba(15,23,42,0.08)` track would be invisible over the Aurora; ISSUE-014's
     ≥ 3:1 requirement outranks the reference's value and is unchanged.
  5. **The ring's positional invariant is preserved, and is what makes the
     reversal safe.** The text column is `weight(1f)` and the ring is
     size-constrained, so the ring is at the same position at **every** width,
     digit count and font scale. ADR-48 Amendment Note 4's stated defect — a
     horizontal figure whose companion was placed by *available width rather than
     by meaning* — cannot recur: here the arrangement is the approved composition,
     not a consequence of leftover space. `HeroRingSize`, the vertical lockup and
     the `Row`/`HeroOrbGap` arrangement are deleted; `MomentumFigureDisplay` stays
     declared but becomes unused on Home (removing a public token is out of scope).
  6. **The `Complete` branch keeps ADR-48 §1's statement.** Ring full
     (`100%`); the fraction row is replaced by `today_hero_complete`
     ("Today is complete") in the new 24 sp headline token; the encouragement is
     absent. The reference specifies no completion state, so this is a retained
     approved decision, not a gap in the design.
  7. **One merged `contentDescription`** (`today_hero_progress_desc`) now covers
     the whole merged group — fraction, percentage, `done` and the breakdown — so
     the two-level split is never double-read and the ring stays decorative. The
     encouragement stays outside the merged group and keeps its own
     announcement.
  8. **What is explicitly preserved:** ADR-48 §3's 100 % moment (transient
     `AuroraState.MILESTONE` for `MomentumMotion.reward`, one
     `HapticFeedbackType.Confirm`, one-shot ring glow, no snackbar, no
     particles, reduced-motion skip), ADR-48 §5 (Home rows carry no streak),
     ADR-48 §6 (Home runs **no** permanent infinite animation), ADR-48
     Amendment Note 1 (the state-specific encouragement line and its Settings
     toggle), and ADR-47 §7 (`textPrimary` for copy over the atmosphere — the
     `textSecondary` used inside the progress card sits on the card's own glass
     fill, which is the established `ActivityCard` vocabulary, and every such
     band is in the `task.md` §8.15 sweep).
  9. **Rejected:** keeping the vertical lockup (explicitly rejected by the
     requirement); making the percentage the sole headline and dropping the
     fraction (`code.html`'s structure and the brief both keep both); a solid
     single-hue ring (the gradient is the reference's); a second `Text` for the
     percentage supplied by `ProgressOrb` itself (its built-in percent is
     `headlineMedium` + no unit label, so composing it in `HomeProgress.kt`
     reuses the existing `clearAndSetSemantics` structure and adds **no**
     parameter to `ProgressOrb` beyond `arcBrush`); and shrinking the ring to
     `HeroRingSize` 80 dp (the reference is 96 dp and the 20/28 percentage plus
     the 11/14 unit label need the room).

- **Approval.** **Requires explicit human approval before implementation**
  (AGENTS.md §4/§21): it trades ADR-48 §1's "one progress representation"
  invariant for the approved horizontal composition, and it adds one parameter to
  a shared component.

### ADR-49 — Stitch Aurora Home: composition, component boundaries, glass extension, tokens and the recorded conflict rulings (Architect, TASK-018, pending human approval)

- **Instrument.** Append-only. No ADR above is rewritten, deleted or restated.
  This ADR **adds to** ADR-47 Amendment Notes 4–7 and ADR-48 Amendment Notes 1–5;
  where it touches an existing decision it names it.

- **Context.** TASK-018 implements an **already-approved** external design
  (`screen.png` primary, `code.html` secondary, `DESIGN.md` and the Aurora PRD
  supporting) into the existing Home surface. The Stitch design is approved and
  may not be redesigned, reinterpreted or simplified; Momentum is authoritative
  for all functionality and all real data. `AGENTS.md` §2 source-of-truth order,
  §4 (architecture), §9 (scope discipline), §10 (product vs technical) and §13
  (act when clear) govern. The full component-level plan is `task.md` §8.

- **Decision.**

  1. **The reference viewport is 320 × 800 dp.** `screen.png` is 640 × 1600 and
     `code.html` is authored in CSS px with `1rem = 16px`, so the render is
     **2×**. Every dp value in `code.html` is read as a dp value, which is how
     the ring is 96 dp, the tile 48 dp, `gap-space-sm` 8 dp and
     `gap-space-lg` 24 dp.
  2. **Typography maps to Manrope only; no font dependency is added.** Plus
     Jakarta Sans and Inter are both mapped to the single bundled variable font.
     Roles matching Momentum's existing scale are **reused** — greeting
     `headlineLarge` 28/34 600; `body-sm` `bodyMedium` 14/20 + a per-`Text`
     `FontWeight.Medium` (the precedent `ActivityCard` already sets); card title
     `titleSmall` 14/20 + `SemiBold`; `label-md` `labelMedium` 12/16 500; icons
     `prominent` / `secondary` / `primary`. Exactly **three** new tokens are
     required and named exactly:
     **`MomentumHeadlineCompact` 24/32 600** (the reference's `headline-md`, used
     by the progress fraction and the completion statement),
     **`MomentumHeadlineSection` 20/28 600** (the reference's `headline-sm`, used
     by the ring percentage and both section headings) and
     **`MomentumLabelMicro` 11/14 600 @ letterSpacing 0.04em** (the reference's
     `label-sm`, used by the date capsule, the ring's `done` unit and the section
     status labels). Each carries a one-line derivation comment. No arbitrary
     size substitution anywhere.
  3. **One optional parameter on the shared glass modifier.**
     `Modifier.liquidGlass(shape, level, hue: Color? = null)`. When `hue` is
     non-null the body wash becomes a 135° `Brush.linearGradient` — the reference's
     chromatic card wash — while the top streak, hairline and shadow are
     unchanged; `null` reproduces today's rendering byte-for-byte, so all existing
     callers are unaffected. `hue` is passed a **theme token**
     (`c.auroraLavender` on the progress card), never a literal.
  4. **`backdrop-filter: blur()` is not expressible and is not added.** The
     existing `LiquidGlass` KDoc already ruled it out (minSdk 26, no reliable
     cross-device API, no fragile dependency worth taking), and that ruling is
     not reopened. Blur is a **recorded non-adoption**; depth continues to come
     from translucency + top streak + hairline + shadow, exactly as the existing
     system already declares. No second glass system is introduced.
  5. **Home's card and section header are screen-local composables.**
     `HomeProgressCard`, `HomeSectionHeader` and `HomeActivityCard` live in a new
     `presentation/screens/today/HomeCards.kt`. This is required, not preferred:
     `ActivityCard.kt` has **3** call sites (Today, Activities, Calendar) and
     `MomentumSectionHeader` has **13** (Today, Activities, Calendar, Insights,
     Settings, detail, creation, Create Item), and re-styling either to the
     approved Stitch composition would redesign screens this task is forbidden to
     touch (`task.md` §4.6). The new composables **compose the existing shared
     primitives** — `Modifier.liquidGlass`, `glassPress`, `momentumIcon`,
     `habitIconAccent`, `CompletionButton` — so the card does not fork a material
     (the reasoning ADR-48 Amendment Note 2 applied to `EmptyState`), and
     `ActivityCard.kt` / `MomentumComponents.kt` / `MomentumBadges.kt` are **not
     modified**.
  6. **`CompletionButton` gains two additive, default-preserving parameters:**
     `accent: CompletionAccent = CompletionAccent.THEME` (`THEME` is the existing
     `primary → secondary` gradient, used by all existing call sites; `SUCCESS` is
     the reference's emerald treatment, used by Home) and
     `showIdleGlyph: Boolean = true` (`false` gives the reference's empty ring).
     One shared control therefore keeps one accessible path — 48 dp target,
     `Role.Checkbox`, `completionActionLabel`, the `LongPress` haptic and the
     spring pop — on every screen, and no completion control is forked. **The
     completed card stays fully actionable**: the reference's emerald badge is
     non-interactive, but Momentum's completed row must remain reversible
     (`DECISIONS §12`), so the badge keeps its button role and label. That is a
     deliberate, recorded deviation.
  7. **The fixed/scrollable boundary, the four-child z-order, the single
     `LazyColumn` and the layout-produced scroll boundary are unchanged** from
     ADR-47 Amendment Note 5 §5/§6 — `padding(top = chromeHeight)` from one
     `onSizeChanged`, `contentPadding.top = 0.dp`, `clipToBounds()`,
     declaration order atmosphere → chrome → region → FAB. The only structural
     addition is that the region's `verticalArrangement` becomes
     `MomentumSpacing.tight` (8 dp), matching the reference's `space-sm` gaps.
  8. **The date capsule becomes a glass pill**, which **amends ADR-47 Amendment
     Note 5 §3(a)**: the "one permitted sub-surface" in the fixed chrome is now
     the 48 dp `LiquidGlassIconButton` **plus** the date capsule
     (`GlassLevel.CONTROL`, `RoundedCornerShape(MomentumRadii.pill)`, padded
     `MomentumSpacing.compact` × `MomentumSpacing.micro` — the reference's 5 × 14
     px is off the 4 dp grid and is mapped onto it rather than invented). The
     pill is copy-adjacent chrome, not a copy backdrop, so the note's purpose is
     preserved. The date label already renders as `"EEE, MMM d".uppercased()`,
     which is character-identical to the reference's `THU, OCT 1`.
  9. **Section heading copy changes** from "Remaining today" / "Completed today"
     to "Pending" / "Completed", with new trailing status labels
     "Scheduled Today" and "Finished" (the latter emerald with an additive
     `MomentumIcons.verified` glyph from the already-bundled
     `material-icons-extended`). A new greeting support line
     ("Ready to conquer your day?") is added because `task.md` §4.2 region B
     requires the element and the model has none; it is a **fixed** line and
     deliberately **not** the toggle-controlled encouragement, so
     `AppSettings.motivationalMessagesEnabled` semantics are untouched. All of
     this is copy, i.e. a **product** surface, and is put to the human.
  10. **Motion: only what exists in `code.html`, translated once.** The
      `fade-slide-up` stagger becomes **one** finite 900 ms clock
      (`MomentumMotion.enter = 900`, the reference's total stagger span) driving
      **four** derived `entranceProgress` values — chrome 0 ms, progress card
      100 ms, `Pending` header 200 ms, `Completed` header 300 ms — eased with
      the existing `MomentumMotion.easeOut`. **Per-row stagger is not
      implemented**: a `LazyColumn` row has no "first composition" identity, so a
      per-item entrance would re-fire on every scroll-in; the reference's
      whole-section unit has no exact Momentum equivalent, and its header is the
      closest honest one. `progress-fill`, `check-pop` and the card/control press
      compression map onto existing springs and `glassPress`.
      **`aurora-glow-pulse` (6 s), `flame-pulse`/`bolt-pulse` (2–2.2 s) and
      `fab-glow` (3 s) are rejected** — they are permanent infinite animations,
      which ADR-48 §6 forbids on Home and `HomeSectionMotionTest` asserts. The
      `:hover` rules are n/a on touch. `MomentumMotion.easeOut` stands in for the
      reference's `cubic-bezier(0.16, 1, 0.3, 1)`; no new easing token is added.
  11. **Light/Dark.** The reference is **light** and `DESIGN.md` + the Tailwind
      config's dark `#0b1326` surface palette is **not imported** — no M3 slot
      names, no Tailwind or Material-Symbols glyphs, no `#0b1326`. Light follows
      the reference on Momentum's own `AuroraLight`; **Dark is Momentum's
      existing `AuroraDark` with the identical composition and hierarchy** through
      theme-aware tokens — no separate Dark redesign and **no new colour token
      anywhere in this task**. The one theme-agnostic adoption is the scrim's ramp
      *shape*, which the existing four-stop scrim already has.
  12. **Recorded conflict rulings** (`task.md` §8.12). C1 the progress headline
      reads **"items done"**, not the reference's "habits done": the denominator
      is every eligible `Activity` (habits **and** tasks), and `task.md` §4.2
      region C specifies "X of Y items done". C2 **streak chips are not
      implemented** — `code.html` has none, `task.md` §4.2 region D enumerates the
      card without one, and the PRD removed streak badges on purpose
      ("Presence Over Streaks") while ADR-48 §5 has Home rows passing
      `streak = 0`; adding them would need a new component and would create a
      second streak representation beside the encouragement line. C3 the secondary
      line stays `homeMetadataLabel(item)` (localized `dueTime` +
      `targetProgressLabel`) with **no row at all** when blank — the reference's
      `15 mins` / `500ml` / `3 entries` need a duration field and an entry-count
      field that `Activity` does not have, and closing that gap is a schema change
      outside this task. C8b icon tiles are **2-stop** 135°
      (`White@0.20 → accent@0.42`) at the correct 48 dp / 16 dp geometry, because
      Momentum maps one `iconKey` to one accent and a faithful 3-hue tile would
      require a per-icon colour table — new tokens and a new visual language. C9
      Momentum's 20 dp screen axis is kept and the reference's 16 dp side padding
      is mapped to `MomentumSpacing.standard` for in-component padding only. C10
      the bottom navigation is **unchanged** — it is a `Scaffold` sibling shared by
      all four destinations, and matching its radius and margins to the reference
      would redesign the other three screens. C13 `ArtworkHandOff` stays 88 dp per
      ADR-30. C14 `MomentumFigureDisplay` stays declared but unused on Home.
  13. **The empty state is not specified by the reference** (there is none), so
      Momentum's existing contract is retained **verbatim**: `EmptyState` with the
      existing title/body, `icon = null`, **no CTA** (ADR-48 Amendment Note 2),
      centred by measured geometry in `emptyRegion`, with the FAB as the single
      creation affordance and both sections hidden. `EmptyState.kt` is not
      modified. This is flagged as a reference gap, not a design choice.
  14. **Nothing else moves.** No domain, repository, Room, DataStore, ViewModel,
      Settings, navigation, dependency, manifest or build change; no artwork is
      created, replaced or re-tinted; no parallax, no scroll choreography, no
      second glass system, no second glass token ladder.

- **Options considered and rejected.** (a) Restyle `ActivityCard` and
  `MomentumSectionHeader` in place — redesigns 15 call sites across five screens,
  which §4.6 forbids; (b) fork a Home-only copy of each — forks the material and
  the accessible path, the outcome ADR-48 Amendment Note 2 already rejected for
  `EmptyState`; (c) add real `RenderEffect` blur behind the glass — a fragile
  minSdk-26 dependency the existing KDoc already declined, and a new
  infrastructure dependency requiring its own ADR; (d) bundle Plus Jakarta Sans /
  Inter — a font dependency, forbidden and unnecessary because Manrope covers
  every role; (e) import the `DESIGN.md` dark palette and import Tailwind's colour
  slots — contradicts `task.md` §4.4 and the precedence order.

- **Approval.** **Requires explicit human approval before implementation**
  (AGENTS.md §4/§21): items 2, 3, 5, 6, 8, 9 and 10 each add a token, a shared
  parameter or copy, item 8 amends a human-ruled coherence guarantee, item 11
  resolves a source conflict between artifacts, and item 12 records rejections of
  primary-source elements. **Granted by the human on 2026-10-01** (`task.md` §4.7),
  with two explicit reversals recorded as ADR-49 Amendment Note 1 (item 12's C10,
  the bottom navigation) and ADR-48 Amendment Note 6 (item 10, the motion set).

### ADR-49 Amendment Note 1 — the bottom navigation is re-rendered to the Stitch visual, in place, for all four destinations; behaviour is unchanged (Architect, TASK-018 A1, human ruling 2026-10-01 §4.7 r1 — approved)

- **Instrument:** append-only. No ADR text above is rewritten, deleted or
  restated. This note **supersedes** ADR-49 §12's C10 ruling and `task.md` §8.10
  as originally written; it **adds to** ADR-49 §1 (the px-reads-as-dp convention),
  §4 (blur is a documented non-adoption), §3 (`hue` is the only glass extension)
  and §11 (Light/Dark via theme-aware tokens, no new colour token). The *human's*
  ruling withdrew the Architect's earlier refusal; this note records the resulting
  decision, not a reopening of it.

- **Context.** ADR-22 locked the four top-level destinations; ADR-49 §12 C10
  declined to restyle the shared bar because doing so would change all four
  destinations and §4.6 forbids redesigning Habits / Tasks / Insights. The human
  overrode that: *"Do NOT preserve the current Momentum bottom-navigation styling
  merely because it is shared. … VISUAL = Stitch, BEHAVIOR = Momentum."*
  `MomentumNavGraph.kt:223-232` is the **only** call site of `LiquidGlassBottomBar`
  and of `RowScope.LiquidGlassTab`, so a single shared implementation reaches all
  four destinations identically and they cannot desynchronise.

- **Decision.**
  1. **No Home-only fork.** The two existing shared composables are re-rendered in
     place. Forking would create the second implementation that the single call
     site exists to prevent, and the human forbade it. `MomentumNavGraph.kt`
     changes **only** the `bottomBar` slot's padding: `start`/`end`
     `MomentumSpacing.screen` → `MomentumSpacing.standard` (16 dp = `left-4`
     `right-4`) and `bottom` `MomentumSpacing.compact` →
     `MomentumSpacing.screen` (20 dp = `bottom-5`). `WindowInsets.navigationBars
     .only(WindowInsetsSides.Bottom)` is retained and remains Momentum's
     `env(safe-area-inset-bottom)`. Nav3, `backStack.selectTab`, the `showBar`
     gate, `Scaffold`, `contentWindowInsets` and the four routes are untouched.
  2. **Bar.** `rounded-full` → `CircleShape` (was `RoundedCornerShape(26.dp)`);
     `h-16` → `Modifier.heightIn(min = 64.dp)` (**not** `height(64.dp)`, so the
     bar grows at large font scales instead of clipping); `px-space-md` →
     `padding(horizontal = MomentumSpacing.standard)` with the vertical padding
     removed; `justify-around items-center` → `Arrangement.SpaceAround` +
     `CenterVertically`. `GlassLevel.HERO` is retained: the reference's
     `rgba(255,255,255,0.8)` background, `1px solid rgba(255,255,255,0.95)` border
     and three-part shadow map onto the existing floating-layer material, and
     `backdrop-filter: blur(24px) saturate(180%)` stays a **recorded
     non-adoption** (ADR-49 §4) — the Stitch look is produced by the glass
     material itself, with the atmosphere reading through the 0.68 wash.
  3. **Tab.** `Modifier.weight(1f)` is removed in favour of
     `Modifier.defaultMinSize(minWidth = 62.dp / 56.dp, minHeight = MomentumHeights
     .controlMin)` — Stitch's own per-state `min-w`, and §19's 48 dp floor
     (Stitch's inactive `min-h-[44px]` is below it and is **raised to 48 dp**; at
     font scale 1.0 the natural heights of 50/52 dp already exceed it, so only the
     guarantee changes). `py-1` → 4 dp, `mt-0.5` → a 2 dp `spacedBy`. Active:
     a `CircleShape` capsule padded `16 × 6` dp filled with a 135° two-stop
     gradient `lerp(c.auroraBlue, c.surface, 0.55f)@0.75f →
     lerp(c.auroraLavender, c.surface, 0.50f)@0.85f`, `shadow(2.dp, spotColor =
     c.primary.copy(alpha = 0.2f))`, `border(1.dp, Color.White.copy(alpha = 0.9f))`,
     a 22 dp icon at `c.textPrimary`. Inactive: a 36 dp `CircleShape` slot
     (`group-hover:bg-white/40` not implemented), a 23 dp icon at
     `c.textSecondary`. Labels use the already-approved `MomentumLabelMicro`
     11/14 token, `Bold` when selected and `SemiBold` otherwise, in
     `c.textPrimary` / `c.textSecondary`. **No new colour token, no new type token
     and no new spacing token are introduced.**
  4. **Motion.** The existing `animateColorAsState` colour transitions are kept
     and retargeted to the new inks; the capsule is driven by **one** added
     `animateFloatAsState(0f → 1f)` whose value **scales the alphas** (never
     `lerp`s toward `Color.Transparent`, which would darken through
     premultiplication); and the selected scale **1.06 is removed**, because a
     1.06 scale enlarges the glyph inside a capsule whose padding is Stitch-exact
     and is a flourish the reference does not have. `Role.Tab`, the icon's
     `contentDescription = label` and `glassPress` are all retained — press
     feedback is not in the human's A1 list, so no affordance may be removed.
  5. **Safe area and FAB — resolved by measurement, not by assumption.** The bar's
     own height is **unchanged at 64 dp** (today's measured value is also 64 dp:
     6 dp padding + 52 dp tab). Only the bottom margin moves, 12 → 20 dp, so the
     `Scaffold` `bottomBar` slot grows by exactly **8 dp**, and `Scaffold` folds
     that into `innerPadding.bottom` for the whole `NavDisplay`. `MomentumFab`
     lives inside that content area, so it is carried up automatically; its own
     clearance is `bottom = MomentumSpacing.section` (24 dp) from the top of the
     bottom-bar slot, which is *larger* than the reference's 8 px
     (`bottom: 5.75rem` − `bottom-5` − `h-16`), so **collision is unreachable**.
     `EmptyDayBottomReserve` (96 dp) is **unchanged**: it is measured from the
     region's bottom, already inset by `innerPadding`, so the +8 dp is absorbed
     there. Recorded deviation: the bar's ends now sit 4 dp proud of the content's
     20 dp screen axis (ADR-49 §12's C9 stands for content).

- **Recorded deviations.** blur not adopted · bar background/border/shadow kept at
  `GlassLevel.HERO`'s existing alphas and elevation (the reference's are more
  opaque) · inactive `min-h` 44 → 48 dp · `h-16` → `heightIn(min = 64.dp)` ·
  `#312e81`/`#1e1b4b`/`#64748b` → `c.textPrimary`/`c.textPrimary`/
  `c.textSecondary` · capsule `rgba(99,102,241,0.2)` → `c.primary@0.2f` ·
  capsule border `rgba(255,255,255,0.9)` → `Color.White.copy(alpha = 0.9f)` (the
  same construction `Modifier.liquidGlass` already uses in this file) · the
  capsule's two literal pastel stops → two `lerp(aurora…, c.surface, …)` stops at
  the reference's own `0.75f`/`0.85f` alphas, which keeps it pale in Light and
  deep in Dark so one `c.textPrimary` ink is legible in both · the active capsule
  is 34 dp against the inactive 36 dp slot, so the active label sits ~1 dp higher,
  and the inactive icon (23 dp) is 1 dp larger than the active one (22 dp) — both
  are the reference's own asymmetries, reproduced deliberately.

- **Consequences.** Changed: `components/LiquidGlass.kt` (`LiquidGlassBottomBar`,
  `RowScope.LiquidGlassTab`), `navigation/MomentumNavGraph.kt` (three padding
  values). **Affected screens: Home, Habits, Tasks and Insights**, identically, by
  construction. The only layout consequence is **+8 dp `innerPadding.bottom`** on
  each. No behavioural, routing, domain, persistence or dependency change.
  **No existing test asserted on the navigation bar** (verified: zero references
  to `LiquidGlassBottomBar`, `LiquidGlassTab`, `nav_home`, `RoundedCornerShape(26`
  or `bottomBar` in `src/test` or `src/androidTest`), so nothing is invalidated;
  new coverage is a source-reading `BottomNavStitchVisualTest`, androidTest bounds
  assertions for the slot height and the 48 dp targets, and four new contrast-sweep
  bands. `QuickAddTest`'s two `MomentumNavGraph.kt` assertions survive unchanged.

### ADR-48 Amendment Note 6 — Home's motion becomes ambient: three approved loops from one shared clock, draw-phase only, entirely absent under reduced motion (Architect, TASK-018 A2, human ruling 2026-10-01 §4.7 r2 — approved)

- **Instrument:** append-only. No ADR text above is rewritten, deleted or
  restated. This note **supersedes ADR-48 §6 for Home**, supersedes ADR-49 §10's
  rejection of `aurora-glow-pulse` / `bolt-pulse` / `fab-glow`, and **adds to**
  ADR-47's static-Aurora clause for Home's `HomeAtmosphere` only (ADR-49 §7, §8.2
  and §14 are untouched apart from the three rows below). It **also** amends
  `task.md` §8.3, §8.7, §8.9, §8.11, §8.14 and §8.15.

- **Context.** ADR-48 §6 ("Motion is change-triggered, not wall-clock") forbade
  any permanent infinite animation on Home, and `HomeSectionMotionTest` asserted
  it. The human reversed that rejection: the approved Stitch composition puts a
  live Aurora inside the progress card and a glowing primary FAB over the
  atmosphere, and those motions are part of the approved design. The human's
  reversal was bounded — *no invented animation, no excessive motion, every loop
  nulled under reduced motion* — and this note is that bound.

- **Decision.**
  1. **ADR-48 §6 is superseded for Home by a narrower rule, not a wider one.**
     Home may run **ambient** loops: slow (≥ 2.2 s), tiny-amplitude, purely
     decorative, driven from **one** `rememberInfiniteTransition`, consumed in the
     **draw/layer phase only**, never owned by a `LazyColumn` item, and **entirely
     absent** under reduced motion (the transition is never composed, and nothing
     is merely shortened). **Change-triggered motion remains change-triggered:**
     `animateItem` reflow, the ring sweep, `check-pop`, press compression, the
     900 ms entrance clock and the one-shot milestone transient are unchanged.
     **Rationale:** §6 was written when Home's Aurora was static and its progress
     card carried no atmosphere of its own, so a permanent loop was pure battery
     drain with nothing to animate. The approved composition changed that premise;
     a blanket ban would now forbid reproducing the approved design. What §6 was
     actually protecting — reduced motion, no invented motion, no motion that
     costs composition — is preserved verbatim in the rule above.
  2. **Exactly three loops are approved, each with `code.html`'s own numbers.**
     *M1 — Aurora drift.* `code.html`'s `aurora-float` is **defined but never
     bound** (verified: one occurrence, inside the `@keyframes` block), so it is
     **left unbound**; the human's required "Aurora subtle movement" is instead
     delivered by enabling `AuroraBackground`'s **existing** drift, whose
     `MomentumMotion.ambient` = **6000 ms** matches `aurora-float`'s 6 s exactly and
     whose ±3 % w / ±2.5 % h oscillation is ≈ the reference's own 8 px amplitude.
     That requires `HomeAtmosphere`'s `animated = false` → `true`; `AuroraBackground`
     nulls its own phase under reduced motion, so no new gate is added.
     *M2 — `aurora-glow-pulse` (6 s) on the progress card's two aura blobs.*
     `code.html` has them bound (`anim-aurora-blob`, 3 occurrences). The keyframe's
     `filter: blur(32px) → blur(40px)` is expressed as an animated radial-gradient
     radius (32 → 40 dp) via `canvas.scale` over a `drawWithCache`-cached brush —
     one brush, **zero** per-frame allocation — because a wider blur radius *is* a
     wider falloff. `filter: blur()` itself remains a **recorded non-adoption**
     (ADR-49 §4). Opacity scales 0.35 ↔ 0.6 exactly as the reference does.
     *M3 — `fab-glow` (3 s) on `MomentumFab`.* The component's **existing** single
     shadow animates: elevation 12 → 16 dp and `c.primary` spot alpha 0.40 → 0.55,
     the reference's own figures. `MomentumFab` gains `glowPulse: Boolean = false`
     and `rotateGlyphOnPress: Boolean = false`; **`HabitsScreen`'s FAB is
     byte-identical** because it does not opt in.
     *M4 — `bolt-pulse` (2.2 s) on the `verified` glyph.* `graphicsLayer` only
     (`scale 1 → 1.15`, `rotationZ 0 → −4°`), read from the shared clock; the
     `drop-shadow` half is not implemented. *M5 — FAB press rotation* is the press
     analogue of the reference's `hover:rotate-90`, using the reference's 90° and
     the existing `fast` / `easeOut` tokens; hover itself remains n/a (C12).
  3. **Two keyframes are not implemented, on verified evidence.**
     `flame-pulse` and `aurora-float` each occur exactly once in `code.html` —
     the keyframe block and the class definition respectively — so **both are
     unbound in the reference itself**. `flame-pulse` additionally belongs to the
     streak chip removed by the same human ruling. No binding is invented.
  4. **One clock, and a reduced-motion path with no transition at all.**
     `TodayScreen` composes exactly one `rememberInfiniteTransition(label =
     "homeAmbient")` guarded by `if (reduceMotion) null else`, and derives
     `ambientPulse` (6000 ms), `fabPulse` (`MomentumMotion.fabGlow` = 3000) and
     `boltPulse` (`MomentumMotion.verifiedPulse` = 2200) from it, all with
     `LinearEasing` (the reference's `ease-in-out`). Every consumer reads
     `ambient?.let { … }?.value ?: 0f`, and `0f` is each keyframe's own `0%/100%`
     resting value, so the static frame is a valid frame and nothing jumps.
     `MomentumMotion` gains **exactly two** tokens (`fabGlow`, `verifiedPulse`),
     each with a derivation comment; **no new easing token**.
  5. **Per-frame cost and the composition guarantee.** Each loop is consumed in
     `drawWithCache`, `graphicsLayer` or `Modifier.shadow`; none is read by a
     composable body, so no loop recomposes its subtree; no loop is owned by a
     `LazyColumn` item; and no loop adds a scroll observer. ADR-47 AN5's
     "scrolling recomposes nothing" therefore holds **as a stated guarantee**: a
     fling touches no loop input and, because no list item recomposes, no list
     item's layer can be invalidated by a scroll. The FAB's shadow redraw is the
     most expensive of the three and is confined to one fixed 56 dp element
     outside the list.
  6. **Recorded deviation.** `bolt-pulse` is applied inside a `LazyColumn` item
     through `graphicsLayer`. The literal rule was "never animate anything inside a
     `LazyColumn` item"; moving the `Completed` header out of the list would break
     ADR-47 AN5 §5/§6's scroll architecture, which ADR-49 §7 and `task.md` §8.2
     require to stand. The rule's **intent** is fully met — no per-item animation
     instance, no per-frame recomposition, no scroll-driven work, one transition in
     exactly one place — and the deviation is recorded rather than resolved
     silently.

- **Consequences.** Changed: `screens/today/TodayScreen.kt` (one shared ambient
  clock + the FAB's two opt-ins), `screens/today/HomeAtmosphere.kt` (`animated =
  true`), `screens/today/HomeCards.kt` (the two aura blobs + the `verified` glyph's
  `graphicsLayer`), `components/MomentumFab.kt` (two additive default-`false`
  parameters), `theme/Motion.kt` (two tokens). **Two currently-green assertions are
  corrected, not deleted**: `HomeSectionMotionTest.Home runs no permanent infinite
  animation` becomes `Home's only infinite motion is the three approved ambient
  loops`, re-asserting the one-clock rule, the three exact periods, the enabled
  Aurora drift and the two surviving rejections; `HomeAccessibilityTest`'s
  `"no shimmer currency on Home"` is re-anchored to `TodayLoadingSkeleton`'s own
  body so the no-shimmer invariant survives. `QuickAddTest` gains the opt-in
  assertions, `HomeSectionMotionTest` gains an assertion that `HomeCards.kt`
  contains no transition of its own, and `HomeAtmosphereAssetsTest`'s planned
  `fab-glow`-absent negative is inverted into positive blob assertions. No domain,
  repository, ViewModel, schema, dependency, manifest or build change.

### ADR-49 Amendment Note 2 — the ISSUE-020 sweep for the TASK-018 composition is on record; one band failed and is re-inked to `c.textSecondary`, and three chrome bands stay OPEN (Architect, TASK-018, recorded — Orchestrator adjudication 2026-10-01 §10.1)

- **Instrument.** Append-only. No ADR text above is rewritten, deleted or
  restated. This note **records a measurement result plus one ink correction**. It
  **supersedes ADR-47 Amendment Note 6's baseline by addition of this note**,
  never by rewrite — the instrument AN7 itself declared — and it amends
  `task.md` §8.7 and §8.8 **only** where named below: the encouragement /
  completion-statement ink and the `Finished` label ink. ADR-47 §7, ADR-47
  Amendment Notes 1–7, ADR-48 Amendment Notes 1–6, ADR-49 and ADR-49 Amendment
  Note 1 stand exactly as written apart from those two inks.

- **Why a re-measurement was required.** ADR-47 Amendment Note 7 replaced
  `ArtworkWindow` with the reference-derived, width-proportional
  `ArtworkBandFraction × windowWidth` = `460f / 640f × windowWidth` — **230 dp at
  320 dp, 258.75 dp at 360 dp, 296.13 dp at 412 dp** — which is a direct
  consequence of the human-approved width-proportional window (`task.md` §4.7,
  "Approved as planned"). A wider band places more high-frequency illustration
  under the same scrim, so **AN6's entire table is superseded by addition of this
  note and must never be rewritten.** The region-sample **method**, the **≥ 4.5:1**
  floor and the `[0.15, 0.35]` peak envelope are AN1's and AN6's and are unchanged
  here.

- **Method — unchanged, re-run from scratch for the TASK-018 geometry.** The
  canonical ISSUE-020 **region-sample** method of ADR-47 Amendment Note 1: composite
  page background + Aurora field + artwork (cover-crop `Alignment.BottomCenter`,
  shared 88 dp `DstIn` dissolve) + the **full-content-area** four-stop scrim,
  reduce each band's text rectangle to one mean background colour per channel,
  compute the WCAG 2.x ratio against the band's ink token, and let the **minimum
  region ratio across the sweep govern**.
  **Sweep.** 2 themes × widths {360, 412} dp × content-area heights {560, 720} dp ×
  `AuroraState.defaultIntensity` {0.35, 0.55, 0.75, 1.0} × the chrome bottom edge
  at font scale {1.0, 2.0} × 3 day states {empty, populated, complete} ×
  encouragement {on, off} × the full band list of `task.md` §8.15 — fraction ·
  suffix · encouragement/statement · ring percentage (with the ring's 7.2 dp
  `textPrimary @ 0.55` track composited into the sampled background) · ring `done`
  label · `n Completed` / `n Pending` · the divider · both section headings and
  both status labels · the completed card's strikethrough title · the completed
  card's emerald control (its `c.success` fill **is** the sampled background) · the
  pending card's empty ring · **plus the four nav bands added by ADR-49 Amendment
  Note 1**: inactive label and inactive icon over the composited `GlassLevel.HERO`
  bar, and active label and active icon over the animated capsule at both pulse
  extremes.

- **Why every glass band is computable without the artwork — the lower-bound
  argument.** Each band sitting on glass was evaluated as a **provable lower
  bound** on contrast: in **Light** the worst case is the glass composited over
  **pure white** (no backdrop is lighter, and a lighter background can only lower
  dark-ink contrast), in **Dark** the glass over **pure black**. This is
  **conservative for dark ink in both themes**, and it is the only reason the
  sweep is possible without decoding the illustration.

- **Measured result — every band that could be computed.** Light worst cases, as
  reported, by sampled surface (`GlassLevel` in brackets):

  | Sampled surface | `textPrimary` | `textSecondary` | `textMuted` |
  |---|---|---|---|
  | progress card `#F0ECFC` [`HERO`] | **14.05:1** ✓ | **5.09:1** ✓ | **3.10:1** ✓ |
  | pending card / nav bar, over white [`CARD` / `HERO`] | **16.27:1** ✓ | **5.90:1** ✓ | **3.10:1** ✓ |
  | completed card, over white [`CONTROL`] | **16.27:1** ✓ | **5.90:1** ✓ | — |
  | nav active capsule `#C8C1F4` | **9.60:1** ✓ | — | — |

  - The `textMuted` **dot** and the completed **emerald control** are **non-text**
    surfaces carrying their own **≥ 3:1** obligation, which both clear: the
    control's `c.success` fill measures **4.26:1** against its surroundings and its
    check ink reads **3.82:1** on that fill. The `3.10:1` dot is likewise reported
    against the **non-text** obligation, never against the body-text floor.
  - **Dark is comfortable throughout**: the worst text band is **6.04:1**, the nav
    active capsule. Per-band Dark minima were not reported separately, so none are
    tabulated here — the governing Dark figure is the one on record, and the
    Reviewer re-verifies it independently (the ADR-47 AN6 precedent).

- **One band failed, and the approved ink cannot legally ship as specified.**
  - The **encouragement / completion-statement line**, specified in §8.7 in
    `c.success`, measures **3.68:1** in Light **on its own card** — below the
    **≥ 4.5:1** floor for a 14 sp Medium body line. The **`Finished`** status label
    in `c.success` (§8.8) measures **≈ 4.26:1** on the bare Light atmosphere for
    the same reason: `AuroraLight.success` `#1F8A70` (luminance 0.1964) reaches
    only 4.26:1 **even against pure white**, so it cannot carry body or micro text
    in this theme.
  - **Independently verified** by the Orchestrator (`task.md` §10.1; WCAG relative
    luminance computed straight from the sRGB values in `Color.kt` and
    `code.html`): `c.success` `#1F8A70` on `#F0ECFC` = **3.72:1 — FAIL**;
    `c.textSecondary` `#596579` = **5.08:1 — pass**; `c.textPrimary` `#172033` =
    **17.71:1 — pass**. The Developer's **3.68:1** is the **governing** figure for
    that band because it samples the region **as composited** — including the two
    `clipToBounds` aura blobs beneath the content (§8.19 M2), which darken it —
    where 3.72:1 is the token against the plain fill. **Both fail, and the ruling
    below holds under either number.**
  - **No in-envelope remedy exists.** The only knob the sweep may move is the
    scrim **peak**, bounded by `[0.15, 0.35]`, and it cannot reach either band: both
    sit on their own glass fill, or on a backdrop already at the peak. Moving it
    would be a no-op for them.

- **Root cause — a token-mapping error against the authoritative reference, not an
  unavoidable constraint.** `code.html` does **not** colour either band with the
  emerald success colour. Its encouragement line is `text-[#00687a]` (dark teal,
  **5.55:1** on `#F0ECFC`) and its `Finished` label is `text-[#005236]` (dark
  green, **8.19:1**) — both verified in the reference and both clearing the floor.
  §8.7/§8.8's mapping of those two bands to `c.success` was a **semantic
  substitution Momentum made**, and `screen.png` / `code.html` are authoritative
  for concrete values (`task.md` §4.7 r1). The failure is therefore a **mapping
  error**, which is correctable; it is not evidence that the approved composition
  cannot carry the band.

- **Decision — the ink follows the reference's role, in the closest existing
  token.** **Both bands use `c.textSecondary`.**
  - It clears the floor in **both** themes (**5.08:1** on the progress card's
    `#F0ECFC` in Light), and it is the **closest existing token in role** to
    Stitch's two dark inks: the reference's own secondary inks are slates
    (`#475569` for the suffix, `done` and `n Pending`), and `AuroraLight.
    textSecondary` `#596579` is the same slate family. §8.7/§8.8 already specify
    `c.textSecondary` for all three of those sibling secondary bands on the same
    card, so after this correction **four** secondary bands on the progress card
    read in one token and one value covers both band types.
  - **No new colour token** is introduced (§4.7 r5 / ADR-49 §11), and **no scrim
    peak moves**: `ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f` stand, as do
    `ScrimPeakStart = 0.20f`, `ScrimPeakEnd = 0.80f` and the four-stop profile.
  - **Recorded deviation from the reference's literal hex.** `#00687a` and
    `#005236` have **no Momentum token**, and introducing them would breach §4.7
    r5's no-new-colour-token rule, while the **≥ 4.5:1** floor in §4.7 r6 / §19 is
    **non-negotiable and is not weakened**. The deviation is recorded here rather
    than absorbed silently.
  - **The emerald signal is preserved** on the `verified` glyph tint and the emerald
    completion control — both **non-text** surfaces with their own ≥ 3:1
    obligation, both already measured passing.
  - **ADR-47 §7 is not breached.** Its prohibition is on `textSecondary` **over
    artwork** in the Hero. Both corrected bands sit on `HERO` glass or far below the
    artwork window on the page background, and neither is Hero chrome; the fixed
    chrome's own copy stays `textPrimary` (see the OPEN bands below).

- **Structural consequence: `HomeSectionHeader`'s `accent` becomes glyph-only.**
  `accent` currently does two jobs — it tints the `verified` glyph **and** supplies
  the status label's `metaColor` (`accent ?: c.textSecondary`). Under this
  correction the label's colour is `c.textSecondary` **unconditionally**, while
  `accent = c.success` **remains** at the `Completed` call site, because that
  argument is what renders the `verified` glyph — and that glyph is the sole
  consumer of ADR-48 Amendment Note 6's **M4 `bolt-pulse` (2.2 s)** loop. Removing
  the accent would delete an approved band and orphan an approved loop. `Scheduled
  Today` and `Finished` therefore share one label token and differ **only** by the
  glyph. `HomeSectionHeader`'s KDoc must be corrected to state that split, and
  nothing is deleted.

- **The `c.success` usages that legitimately remain — do not "fix" these.** Four
  usages are **non-text surfaces** with their own **≥ 3:1** obligation and all four
  pass: the ring's `arcBrush` gradient (`success → auroraBlue → auroraViolet`), the
  breakdown row's success dot, the `verified` glyph tint, and the emerald completion
  control (**4.26:1**, check ink **3.82:1**). **This note changes only the two text
  bands.**

- **Three bands left OPEN — unmeasured, not assumed-passing.** The **date capsule**,
  the **greeting** and the **greeting support line** resolve against the composited
  artwork rather than against any solid token surface (the capsule through its
  `CONTROL`-glass pill, ADR-49 §8; the other two as bare ink under ADR-47 §7), so
  the lower-bound argument above does not apply and the region sample **cannot be
  computed** in this environment.
  - **Why unmeasured:** the shared illustration is a **lossy VP8 WebP** and this
    environment has **no decoder** for it — no Python, no ImageMagick/ffmpeg, no
    npm packages, and the JDK's `ImageIO` has no WebP reader.
  - **Why ADR-47 AN6's figures for these exact bands do not transfer:** the window
    grew **74.75 dp at 360 dp**, so `ContentScale.Crop` with `Alignment.BottomCenter`
    samples **different illustration rows at the same y**. AN6's date/greeting
    figures are therefore not comparable and are not carried forward.
  - **Status: OPEN.** Closing them requires a **WebP-capable environment or the
    device** (`task.md` §8.16) and belongs to the human and the Reviewer.
    **No measurement may be invented, extrapolated or assumed.** They are the
    residual readability risk of TASK-018 and are recorded as such.

- **Options considered.** (a) The Developer's option (a) — return the
  encouragement to `textPrimary` and `Finished` to `textSecondary` — was refined
  rather than adopted whole, because one token for both bands matches the reference's
  single slate role and the card's existing secondary hierarchy; (b) a darker Light
  `success` — a **new colour token**, forbidden by §4.7 r5; (c) accept 3.68:1 for a
  14 sp Medium line — breaches the §4.7 r6 floor; (d) move the scrim peak — proven
  above to be a no-op for both bands; (e) **`c.textSecondary` for both** (chosen).

- **Verification status — stated plainly.** These are **computational contrast
  figures**, not instrumented or on-device results. `adb` is unavailable in the
  implementation environment, **no instrumented suite was executed, and no
  screenshot, pixel, bounds or device claim is made or implied.** The Light figures
  are the Developer's as reported; the two failing-band figures were additionally
  verified by the Orchestrator. A **Reviewer independently re-verifies the sweep
  and re-checks the ≥ 4.5:1 floor for both corrected bands** after the change.

- **Consequences.** Changed: `presentation/screens/today/HomeCards.kt` only — the
  `HomeEncouragementLine` colour (today `if (isComplete) c.textPrimary else
  c.success`) becomes an unconditional `c.textSecondary`, and
  `HomeSectionHeader`'s label colour stops deriving from `accent` while `accent`
  keeps its glyph role (plus that composable's KDoc). `TodayScreen.kt` keeps
  `accent = c.success`. No domain, repository, ViewModel, schema, dependency,
  manifest, build, artwork, typography, motion or navigation change. Re-run
  `:app:testDebugUnitTest`, and **confirm no test pinned the `c.success` ink**;
  `HomeAtmosphereAssetsTest`'s currently-green `the completed status is emerald`
  assertion must be **re-pointed at the glyph, not deleted**, and the
  Reviewer re-checks the floor for both bands.

### ADR-49 Amendment Note 3 — the ISSUE-020 sweep's method was inverted for the chromatic progress card; both bands take `c.textPrimary`, three sibling card bands are newly at risk, and AN2's figures and ruling are superseded (Architect, TASK-018, recorded — `task.md` §10.7)

- **Instrument.** Append-only; nothing above is rewritten, deleted or restated.
  This note **supersedes ADR-49 Amendment Note 2's lower-bound argument, its
  `textSecondary` table row for the progress card, and its `c.textSecondary`
  ruling**, by reference and not by restatement. AN2 and everything above it
  stand as written.

- **AN2's method was inverted for this card — the defect.** AN2 bounded every
  glass band by compositing over **pure white** in Light ("a lighter background
  can only lower dark-ink contrast"). That is valid for a **solid** fill. It is
  **backwards** for the Home progress card, whose `Modifier.liquidGlass(hue = …)`
  (ADR-49 §4, the one approved glass extension) paints a **chromatic wash** —
  `lerp(c.surface, auroraLavender, HueMix = 0.55)` at
  `MomentumGlass.heroFill = 0.68` — **composited over the backdrop**, not painted
  into it. Dark ink **gains** contrast as its background lightens, so the
  governing case is the card's **darkest** composited stop over the **darkest**
  backdrop, not the plain stop over white.
  - **AN2's band list also mixed points.** It reported `textMuted 3.10:1` — the
    *lightest* stop — beside `textSecondary 5.09:1` — the *middle* stop `#EFEDFB`.
    **The governing minimum was never taken**, so AN2's `textSecondary` rows are
    not valid governing values in either direction.

- **Corrected figures.** Recomputed from the sRGB values in `Color.kt`, modelling
  the wash exactly (`HueMix = 0.55`, `heroFill = 0.68`, tinted stop `#C3B5F2` =
  `lerp(#FFFFFF, AuroraLight.auroraLavender #9278E8, 0.55)`).
  `AuroraLight.textSecondary` `#596579` is **L 0.12812** and requires a composited
  backdrop of **≥ 0.75154** for 4.5:1. `AuroraLight.textPrimary` `#172033` is
  **L 0.01454** and requires **≥ 0.24043**.

  | composited backdrop | card's tinted stop | `c.textSecondary` | `c.textPrimary` |
  |---|---|---|---|
  | `#FFFFFF` pure white | `#D6CDF6` (L 0.6461) | **3.91:1 FAIL** | **10.79:1 pass** |
  | `#F7F8FC` Light page | `#D4CAF5` (L 0.6283) | **3.81:1 FAIL** | **10.51:1 pass** |
  | `#E0E0F0` | `#CCC3F1` (L 0.5822) | **3.55:1 FAIL** | **9.79:1 pass** |
  | `#B0B0D8` dark artwork region | `#BDB3EA` (L 0.4900) | **3.03:1 FAIL** | **8.37:1 pass** |

  Every composited luminance above falls below `textSecondary`'s 0.75154
  requirement, so `c.textSecondary` **fails the floor on this card at every point
  of the sweep**. The true worst case is **at or below** the `#B0B0D8` row,
  because the real backdrop is the unmeasurable artwork composite.
  `c.textPrimary` clears the floor at every stop over every backdrop above, with
  wide margin.

- **Third record-accuracy correction — two further wrong numbers.** AN2's
  §10.7-era `textPrimary` figures (**L 0.0011**, derived threshold **−0.045**) are
  wrong. `AuroraLight.success` `#1F8A70` at **L 0.1964** is correct and stands.
  The two threshold intermediates supplied alongside the table above
  (**0.5266** and **0.01544**) were checked on preparing this note and are
  **also wrong** — both omit the `+0.05` on the ink's own luminance. Correct:
  **`0.75154`** and **`0.24043`**; the table's ratios reproduce unchanged from them.
  **None of these wrong numbers reached any code or KDoc, and none changed the
  ruling.** They are recorded so no future reader derives a non-reproducible
  threshold from this note.

- **Ruling — supersedes AN2's `c.textSecondary` decision.**
  - The **encouragement / completion-statement** line → **`c.textPrimary`**.
  - The **`Finished`** status label → **`c.textPrimary`**. Neither status label
    sits on a solid fill; both are bare text over the composited atmosphere, the
    same compositing situation whose inverted measurement direction invalidated
    `textSecondary` on the card.
  - **`Scheduled Today`** (the `Pending` meta) **stays `c.textSecondary`**. It
    sits on the **bare page atmosphere**, not on the card's wash, so AN2's
    measurement for it is unaffected and it remains the default.
  - **Why `c.textSecondary` had to be abandoned on the card specifically.** It is
    a **legitimate** token — §8.7/§8.12 already use it for the sibling secondary
    bands — but at **L 0.12812** its luminance is too high to survive the
    chromatic wash. `c.textPrimary` is the ink TASK-017 already used for this
    copy, matches the reference's own darkest ink family (`#0f172a`), introduces
    **no new colour token** (§4.7 r5), and moves **no scrim peak**
    (`ScrimPeakLight = 0.26f` / `ScrimPeakDark = 0.22f` stand).

- **NEW finding — three sibling secondary bands on the same card are also at
  risk. Not passed, not decided here.** The same **≥ 4.5:1** floor applies to the
  other `c.textSecondary` bands, and three of the four sit **inside** the wash
  modelled above: the **`items done`** suffix, the ring's **`done`** unit, and
  **`n Pending`**. Each therefore fails by the same computation, at every stop,
  by the margins tabulated above. Only **`Scheduled Today`** is unaffected, being
  on the bare page. **No corrective ink is chosen for these three bands in this
  note** — that is a product-ruling decision reserved to the human, and the
  canonical issue registry is Orchestrator-owned. Carried as an open finding, not
  silently passed.

- **Structural consequence — the actual shipped mechanism, superseding AN2's
  "accent becomes glyph-only" account.** The correction did **not** make the label
  colour stop deriving from `accent`. `HomeSectionHeader` gained one **additive,
  default-`null`** parameter `labelColor: Color? = null` and computes
  `val statusInk = labelColor ?: c.textSecondary`, so `Finished`
  (`c.textPrimary`) and `Scheduled Today` (`c.textSecondary`) can differ.
  `accent = c.success` **remains** at the `Completed` call site and is now
  **glyph-only**, still gating the `verified` glyph — the **sole** consumer of
  ADR-48 Amendment Note 6's M4 `bolt-pulse` (2.2 s). Nothing was deleted, and the
  composable's KDoc states the split.

- **Unchanged and still true in AN2 — not to be re-litigated.** The
  re-measurement requirement and AN6's supersession-by-addition; the region-sample
  method and the **≥ 4.5:1** floor (**not weakened**); the `[0.15, 0.35]` peak
  envelope and all four scrim stops; the four legitimate remaining `c.success`
  **non-text** usages — ring `arcBrush`, breakdown dot, `verified` glyph tint, and
  the emerald completion control (**4.26:1** fill, **3.82:1** check ink) — each
  carrying its own **≥ 3:1** obligation; the **root cause**, a semantic
  substitution against `code.html`'s `#00687a` / `#005236`, neither of which has a
  Momentum token; ADR-47 §7 **not** being breached; and the **three OPEN bands**
  (date capsule, greeting, greeting support line) together with every band that
  resolves against the **real** composited artwork — including this card's true
  backdrop stop — still **unmeasured**, still requiring a **WebP-capable
  environment or the device** (`task.md` §8.16), and **never to be assumed**.

- **Verification status — stated plainly.** These are **computational** contrast
  figures, not instrumented or on-device results. `adb` is unavailable:
  **no instrumented suite ran, and no screenshot, pixel, bounds or device claim is
  made or implied.** The Reviewer re-reviewed and returned **PASS** on `task.md`
  §10.9, independently reproducing the wash computation and confirming
  `textPrimary` clears the floor. **Visual fidelity to `screen.png` remains
  unverified and belongs to the human.**

- **Consequences.** Supersedes AN2's ink ruling, its lower-bound argument, and its
  `textSecondary` row for the progress card. Adds one finding AN2 did not carry:
  three sibling secondary bands on that card now fail the same floor. The shipped
   code already matches this note; the three sibling bands remain `c.textSecondary`
   pending the human's ruling. No domain, repository, ViewModel, schema,
   dependency, manifest, build, artwork, typography, motion or navigation change
   is required.

### ADR-49 Amendment Note 4 - the ISSUE-020 remediation plan: glass-wash material fixes, four per-band ink decisions, the ring-% band correction, and the ring arc's decorative exemption (Architect, TASK-018, recorded - `task.md` §8.20)

- **Scope.** Implements the §4.10 A-F requirement for the 16 failing bands of
  §10.11. The full plan (region rulings, change list, untouched passing regions,
  verification) lives in `task.md` §8.20; this Note records the decisions that
  amend or supersede earlier entries. All figures are computational, produced by
  the §10.11 harness under one winning configuration; the Reviewer re-run is
  pending, and no instrumented or on-device claim is made.

- **Material (supersedes no prior ink ruling; enables most bands).** Non-hue
  glass wash restructured to a solid fill layer with a fading top highlight
  (`LiquidGlass.kt`, `hue == null` branch only; the `hue = auroraLavender`
  progress-card wash keeps its shape); `HueWashMidAlpha 0.5f -> 0.8f`; and
  `MomentumGlass` fill raises `heroFill 0.68 -> 0.85`, `cardFill 0.55 -> 0.90`,
  `controlFill 0.45 -> 0.90`. Empirical necessity: without the wash restructure,
  raised fills collapse worst-case Dark samples (pending row 1.79, nav 3.00).

- **Ink decisions (per-band, not a blanket conversion) - four reversals:**
  1. `Scheduled Today` meta: §13.1 and AN3's `c.textSecondary` default is
     superseded - measured 3.24 Light / 2.45 Dark against the dissolve backdrop,
     where no foreground treatment other than ink can pass (backdrop frozen).
     It takes `labelColor = c.textPrimary` at the Pending header call site,
     matching its own title and `Finished`; verified 4.57 / 8.72.
  2. `verified` glyph tint: AN3's "`accent = c.success` **remains** at the
     `Completed` call site" is superseded - measured 2.34 Light. `accent` becomes
     `c.textPrimary` (glyph-only per the KDoc); verified 4.57 / 8.72.
  3. Breakdown dots: this Note-series' list of legitimate `c.success` /
     `c.textMuted` non-text usages is narrowed - the success dot (2.23 Light)
     and the textMuted dot (1.62 / 1.20) both become `c.textPrimary`
     (verified 4.78 / 8.44). The ring's `arcBrush` gradient and the emerald
     completion control remain the `c.success` surfaces (the arc under 4(b)
     below, the emerald under 3 below).
  4. Row-title ink is **unchanged** (`if (completed) c.textSecondary else
     c.textPrimary`, pinned by `HomeAccessibilityTest.kt:264`); rows pass on
     material alone (5.59 / 6.00).

- **Emerald completion control (supersedes AN3's figures).** The gradient stops
  move from `success -> success⊕mint@0.5` to the near-flat window
  `[lerp(success, auroraMint, 0.30f), lerp(success, auroraMint, 0.34f)]`, which
  is the only window where the dark check glyph (needs stop L >= 0.252) and the
  Light fill boundary (needs stop L <= 0.267) both clear; glyph ink stays
  `c.onPrimary`. AN3's "4.26:1 fill, 3.82:1 check ink" are superseded by
  3.09 / 7.49 boundary and 4.84 / 12.14 glyph. The idle-ring border alpha rises
  `0.4f -> 0.8f` (empty-ring boundary 1.68 / 1.24 -> 3.42 / 3.65).

- **Ring-% band model corrected (amends §8.15's measurement definition, not its
  floor).** The percent text cannot overlap the track (96dp ring, 7.2dp stroke,
  40.8dp inner radius vs ~33dp text half-diagonal; `HomeCompositionTest` bounds
  it), so the band measures `c.textPrimary` against the card, not a
  track-composited backdrop. Floor stays 4.5; verified 4.78 / 8.44. The
  conservative composite was also algebraically unsatisfiable (shared hue family
  with the track).

- **Ring arc exempted as decorative (amends AN3's "each carrying its own ≥3:1
  obligation" for `arcBrush` only).** `showPercent = false`, the KDoc's "the ring
  stays decorative", and the adjacent `%` text at 4.5:1 make the arc redundant
  with verified text; and at the remediated card composites a ≥3:1 arc would
  have to become an ink arc (stops at L <= 0.122 Light / 0.057 Dark), reversing
  ADR-48 AN5's approved `success -> auroraBlue -> auroraViolet` gradient for no
  informational gain. The gradient is kept unchanged; the HomeCards KDoc's false
  "both already pass" claim is corrected. §4.10: "decorative boundaries must not
  be modified blindly."

- **Scrim and scope.** No scrim adjustment is used or justified: every text and
  state band passes on foreground/token treatment alone, so the §4.10 scrim rule
  is never engaged; all four scrim stops and the `[0.15, 0.35]` peak are
  untouched. No new tokens (§4.10 D: not needed). Passing regions are untouched.
  Source-assertion tests pinned to superseded expressions
  (`HomeAtmosphereAssetsTest.kt`, `BottomNavStitchVisualTest.kt`) are updated in
  the same change per `task.md` §8.20.2 - they are corrections of pinned
  expressions, not weakenings of accessibility intent.

### ADR-49 Amendment Note 5 — the ISSUE-020 remediation is re-scoped to Home: a default-`null` `fillAlpha` glass opt-in, two Home-only material constants, one Home-only empty-state ink, and the navigation/idle-ring shared blockers held for the human (Architect, TASK-018, 2026-10-02 — `task.md` §4.11)

- **Instrument.** Append-only. No ADR text above is rewritten, deleted or
  restated. This note **supersedes ADR-49 Amendment Note 4's "Material" section in
  full** (the global non-hue two-layer wash, `HueWashMidAlpha 0.5 -> 0.8` as a
  global constant, and the global `MomentumGlass` fill raises `0.68/0.55/0.45 ->
  0.85/0.90/0.90`), never by rewrite, and **supersedes AN4's per-band treatment
  for the empty-state body** (fill raise -> ink). AN4's four per-band ink
  decisions, its ring-% band-model correction, its ring-arc decorative exemption,
  its emerald window, and its scrim-not-required finding **stand exactly as
  approved** and are re-affirmed here where re-scoped. The human's `task.md`
  §4.11 ruling is the authority and outranks AN4 wherever they differ.

- **Why the re-scope (the human's ruling, not a preference).** AN4 passed every
  §10.11 band by raising three shared fills and restructuring the shared wash
  globally. The human rejected that ("unacceptable blast radius across Home,
  Habits, Tasks, Insights and Settings", "conflicts with Aurora remaining
  perceptible") and required a **Home-scoped** first pass: shared-API changes are
  acceptable only as default-preserving optional parameters, and shared components
  with no Home path (the bottom navigation) must be **flagged, not silently
  changed** (§4.11 CHANGE 1; the navigation blocker).

- **Decision — the Home-scoped material mechanism.**
  1. **One additive, default-`null` parameter on the shared modifier.**
     `Modifier.liquidGlass(shape, level, hue = null, fillAlpha: Float? = null)`.
     `null` (every existing caller) is byte-identical to today — `effectiveFillAlpha
     = level.fillAlpha` and the legacy single `topLight -> fill -> fill` vertical
     gradient. Non-null (Home) sets `effectiveFillAlpha = fillAlpha` and switches the
     non-hue wash to a **two-layer** treatment — a solid `surface @ fillAlpha`
     underlayer with a fading top streak — so a single opt-in reproduces, on Home
     alone, both AN4's E1 (two-layer) and E3 (raised fill). There is still **one**
     glass implementation and no Home fork; rollback is deleting the argument.
  2. **`HueWashMidAlpha 0.5 -> 0.8` is Home-only by construction**, not by a new
     scope rule: `hue` is non-null at exactly one production call site (the Home
     progress card), so `HueWashMidAlpha` and `HueMix` are reached only there.
     (§4.11 CHANGE 3.)
  3. **Home call sites pass the fills the rejected E3 would have applied
     globally:** progress card `fillAlpha = 0.85f`; `HomeActivityCard`
     `fillAlpha = 0.90f` for both `CARD` (pending) and `CONTROL` (completed).
     `Tokens.kt` is **unchanged** — `MomentumGlass.heroFill/cardFill/controlFill`
     stay `0.68/0.55/0.45` for every non-Home caller.
  4. **Two Home-only constants** (single production caller, no global token):
     `TrackAlpha 0.55 -> 0.7` (`ProgressOrb`, Home sole production caller) and the
     `CompletionAccent.SUCCESS` gradient window
     `success -> success⊕mint@0.5` -> `[lerp(success, auroraMint, 0.30f),
     lerp(success, auroraMint, 0.34f)]` (`HomeCards` sole `SUCCESS` caller), with
     the check glyph ink unchanged at `c.onPrimary`.

- **Decision — ink, per band (re-affirms AN4 as approved).** `Scheduled Today` ->
  `labelColor = c.textPrimary` (Pending header); the `verified` glyph `accent` ->
  `c.textPrimary`; the two breakdown dots -> `c.textPrimary`. The empty-state body
  becomes `c.textPrimary` through one additive `EmptyState(bodyColor: Color? =
  null)` parameter (Home passes `textPrimary`; Activities/Insights/Habits keep the
  `textSecondary` default) — replacing AN4's rejected fill-raise (the band's
  measured backdrop is the raw atmosphere, not the card fill, so material alone
  cannot fix it). No blanket `textSecondary -> textPrimary` conversion.

- **Ring-% and ring arc — re-affirmed unchanged.** The ring-% band is a
  measurement-model correction (the `%` text cannot overlap the 7.2 dp track:
  inner radius 40.8 dp vs ~33 dp half-diagonal), ink already `textPrimary`; the
  ring arc remains classified **decorative** under §4.11 CHANGE 5's five
  conditions (redundant with the ≥4.5 `%` text; forcing 3:1 forces an ink arc at
  L ≤ 0.122/0.057 that reverses ADR-48 AN5's gradient).

- **Shared-component blockers — held for the human, not changed here.**
  (1) The bottom navigation's inactive label/icon (Dark 3.34) and Light capsule
  boundary (1.11) have **no Home-only path** (one `Scaffold.bottomBar` call site
  across four destinations, screen-independent backdrop). Smallest shared
  mechanism: Light capsule border -> `c.textPrimary` and a nav-scoped `fillAlpha`
  on the bar's own `liquidGlass(CircleShape, HERO)` (which is *more* faithful to
  the reference's `rgba(255,255,255,0.8)` bar). (2) The `CompletionButton` idle
  ring boundary (`textSecondary @ 0.4`) is a shared constant across every
  completion toggle; its smallest change is `0.4 -> 0.8` (or a Home-only
  `idleRingBorderAlpha` parameter). Neither is authorized until the human rules.

- **Verification status — stated plainly.** These are computational contrast
  figures from §10.11 and AN4's X2 configuration, carried forward **only where the
  Home-scoped mechanism reproduces X2's Home treatment exactly**; no new ratio is
  invented, and the Reviewer's re-run plus the device matrix remain with the human
  and the Reviewer (`adb` unavailable). No scrim, artwork, dissolve, geometry,
  threshold, dependency, motion, `Tokens.kt` or `Color.kt` change is proposed; no
  new colour, type or motion token is introduced.

- **Consequences.** Changed (on approval): `components/LiquidGlass.kt` (`fillAlpha`
  param + two-layer non-hue wash, `HueWashMidAlpha 0.8`), `components/ProgressOrb.kt`
  (`TrackAlpha 0.7`), `components/CompletionButton.kt` (`SUCCESS` window `[0.30,
  0.34]`), `components/MomentumBadges.kt` (`EmptyState.bodyColor`),
  `screens/today/HomeCards.kt` (dots -> `textPrimary`, `fillAlpha`, KDoc),
  `screens/today/TodayScreen.kt` (`labelColor`, `accent`, `bodyColor`), re-pointed
  source-assertion tests, and this note. **Unchanged:** `theme/Tokens.kt`,
  `theme/Color.kt` (and every other theme file), `navigation/MomentumNavGraph.kt`
  (behaviour), `ActivityCard.kt`, `MomentumComponents.kt`, the frozen background,
  all non-Home screens, and the whole domain/data/repository/ViewModel layer.

### ADR-49 Amendment Note 6 — the ISSUE-020 Home-scoped remediation is implemented, and the three shared blockers held by AN5 are approved and changed (Human ruling + Developer, TASK-018, 2026-10-02 — `task.md` §8.20)

- **Instrument.** Append-only. No ADR text above is rewritten, deleted or
  restated. This note records the human's approval of the three shared
  components AN5 §"Shared-component blockers" held pending a ruling, and the
  Developer's implementation of AN5's Home-scoped plan. AN5's Home-scoped
  mechanism and per-band ink decisions stand; this note only resolves the
  blocker and records the as-built state.

- **Decision — the shared blockers are approved with these exact mechanisms.**
  1. **Bottom-navigation capsule boundary.** `RowScope.LiquidGlassTab`'s active
     capsule hairline becomes **theme-resolved**: Light uses
     `c.textPrimary.copy(alpha = CapsuleBorderAlpha * capsuleProgress)`, Dark
     keeps `Color.White.copy(alpha = CapsuleBorderAlpha * capsuleProgress)` —
     mirroring the generic `Modifier.liquidGlass` hairline (the white hairline
     measured 1.11:1 on the near-white Light capsule). No layout, behaviour,
     routing, padding or interaction change.
  2. **Bottom-navigation inactive label/icon.** The bar's own
     `liquidGlass(CircleShape, GlassLevel.HERO)` call opts into a nav-scoped
     `fillAlpha = 0.85f` (a two-layer solid fill, raising it toward the
     reference's own `rgba(255,255,255,0.8)`). The inactive ink **stays**
     `c.textSecondary`; the selected/inactive hierarchy is preserved, never
     flattened to `c.textPrimary`. Navigation behaviour is unchanged.
  3. **CompletionButton idle ring.** `CompletionButton` gains one additive,
     default-`null` parameter `idleRingBorderAlpha: Float? = null`. `null`
     keeps the shared `0.4` idle border alpha exactly, so every non-Home
     completion control is byte-identical; Home opts into `0.8` for its more
     solid row fills. The `0.4 → 0.8` change is **not** global.

- **As built (AN5 §"Consequences", now realised).** `Modifier.liquidGlass`
  gains `fillAlpha: Float? = null` and the Home-only two-layer non-hue wash;
  `HueWashMidAlpha = 0.8f`; `ProgressOrb.TrackAlpha = 0.7f`;
  `CompletionAccent.SUCCESS` gradient window
  `[lerp(success, auroraMint, 0.30f), lerp(success, auroraMint, 0.34f)]` with the
  check glyph still `c.onPrimary`; `EmptyState(bodyColor: Color? = null)`; the
  Home progress card `fillAlpha = 0.85f`, Home rows `fillAlpha = 0.90f`, the two
  breakdown dots `c.textPrimary`, Pending `Scheduled Today` and Completed
  `Finished` `labelColor = c.textPrimary`, the `verified` glyph `accent =
  c.textPrimary`, and Home's empty-state body `bodyColor = c.textPrimary`.

- **Preserved, not changed.** `theme/Tokens.kt` and `theme/Color.kt` (and every
  other theme file) are untouched — `MomentumGlass.heroFill/cardFill/controlFill`
  stay `0.68/0.55/0.45`; `navigation/MomentumNavGraph.kt` behaviour, the four
  destinations, routing, `selectTab` and the `showBar` gate are unchanged; the
  frozen background (§4.9) is untouched; `ActivityCard.kt`,
  `MomentumComponents.kt`, `MomentumIcons.kt`, `MomentumFab.kt` and the whole
  domain/data/repository/ViewModel layer are unchanged.

- **Verification status.** The bounded ISSUE-020 re-measurement is re-run against
  the changed bands only (Home progress card, Home rows, empty body, status inks,
  dots, ring track and percentage, emerald window, plus the nav bands), Light and
  Dark, with the floors unchanged (4.5:1 text / 3.0:1 non-text) and the ring arc
  decorative under §4.11 CHANGE 5. The default/`null` paths are confirmed
  unchanged on Habits/Tasks/Insights (the shared nav bar is one call site across
  all four destinations; every non-Home completion control keeps `0.4`). The
  on-device matrix remains with the human (`adb` unavailable).

