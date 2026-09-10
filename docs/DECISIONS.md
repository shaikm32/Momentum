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
