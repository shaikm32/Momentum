# Momentum — Roadmap

> Checkboxes flip as phases land. Each phase ends with build + tests green.

- [x] **PHASE 0 — Project foundation and architecture**
  - [x] Gradle + version catalog + `:app` module, manifest (no `INTERNET`)
  - [x] `AGENTS.md`, `docs/` (6 docs), `.gitignore`, initial commit
  - [x] Minimal Compose shell: theme foundation, nav foundation, Today placeholder
  - [x] Testing skeleton: engine unit tests (recurrence subset, streak)
  - [ ] Verify: open in Android Studio, `gradle wrapper`, `assembleDebug` + `testDebugUnitTest` green

- [x] **PHASE 1 — Design system and visual identity**
  - [x] `MomentumColors` 14-slot tokens; 8 theme concepts × light/dark (Aurora,
    Ocean, Sunset, Forest, Lavender, Midnight, Cosmic, Arctic)
  - [x] Core components: `ActivityCard`, `ProgressOrb`, `CompletionButton`,
    `StatCard`, `EmptyState`, `CategoryChip`, `StreakBadge` + previews (light/dark)
  - [x] Typography scale; reduced-motion hooks (`rememberReduceMotion`)
  - [ ] Verify in Android Studio (build + visual pass, contrast audit)

- [x] **PHASE 2 — Activity domain + Room persistence**
  - [x] Entities/DAOs/database per `DATA_MODEL.md` (v1 schema, unique
    `(activityId, occurrenceDate)`); repository impls; hand-written
    `RecurrenceCodec` (ADR-11)
  - [x] DataStore settings; `AppContainer` wiring; `momentumViewModel` factories
  - [x] Codec + mapper-adjacent unit tests (DB harness still open — see Testing Strategy)

- [x] **PHASE 3 — Recurrence engine**
  - [x] All 8 recurrence types (`Once/Daily/Weekdays/SelectedWeekdays/EveryNDays/Weekly/EveryNWeeks/Monthly`);
    `isScheduled` (status × window × rule); `scheduledInRange`; DST-safe
    `LocalDate` math; monthly clamp semantics documented + tested
  - [ ] Property-style tests over random windows (open)

- [x] **PHASE 4 — Today experience**
  - [x] `GetTodaySchedule` + `ToggleCompletion` use cases; Today ViewModel +
    Loading/Success/Error; orb + cards + greeting + motivational message;
    instant toggle, empty/error states, celebration effect

- [x] **PHASE 5 — Activity creation/editing**
  - [x] Add/edit form (validation, icon/category pickers, recurrence editor,
    date pickers, target/unit, priority, notes, reminder); detail screen with
    stats + history dots + pause/archive/delete (confirmed hard delete);
    forward-only edit semantics (history never rewritten)

- [x] **PHASE 6 — Streak and analytics engine**
  - [x] Pure-Kotlin stats (%, current/longest streak, missed, consistency,
    week-over-week trend); scheduled-occurrences-only rule; unit-tested

- [x] **PHASE 7 — Insights dashboard**
  - [x] 7/30-day rates, completions, best streak, custom weekly bars (no chart
    lib), category performance, most-consistent list — all from real Room data

- [x] **PHASE 8 — Calendar**
  - [x] Heatmap month grid, day detail with working toggles, return-to-today

- [x] **PHASE 9 — Notifications/reminders**
  - [x] `AlarmManager` exact (+inexact fallback) + `WorkManager` resync +
    `BOOT_COMPLETED` receiver; mark-done action; re-key on edit; cancel on
    pause/archive/delete; permission handling + Settings deep-link
  - [ ] On-device validation (install, fire, reboot, Doze — needs device/Studio)

- [x] **PHASE 10 — Theme gallery**
  - [x] Data-driven registry, gallery UI with previews, DataStore persistence,
    System/Light/Dark modes

- [x] **PHASE 11 — Accessibility and performance (first pass)**
  - [x] 48 dp+ targets, content descriptions, dynamic-type-safe layouts,
    reduced-motion support, bounded queries, single-Canvas visuals,
    `LazyColumn` keys, no main-thread DB
  - [ ] TalkBack traversal pass + contrast audit on device; cold-start measure;
    baseline profiles before 1.0

- [ ] **PHASE 12 — Testing and production hardening**
  - [ ] Full suite green in Android Studio (`assembleDebug` + `testDebugUnitTest`)
  - [ ] Manual pass: onboarding → create → complete → streak → edit → archive →
    delete → theme switch → reminder fire → reboot → offline airplane-mode run
  - [ ] DB test harness (Robolectric/instrumented) for DAO/repository behavior
  - [ ] Release build, versioning, store-readiness checklist, license decision

## Deferred (post-v1, no distraction)

- Cloud sync / backup-import-export (needs ADR; architecture stays local-first)
- Widgets, Wear OS, advanced recurrence combinators, richer gamification,
  additional themes, multi-reminder editor UI, drag reordering
