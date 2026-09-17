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