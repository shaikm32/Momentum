# Momentum — Roadmap

> Checkboxes flip as phases land. Each phase ends with build + tests green.

- [x] **PHASE 0 — Project foundation and architecture** (this session)
  - [x] Gradle + version catalog + `:app` module, manifest (no `INTERNET`)
  - [x] `AGENTS.md`, `docs/` (6 docs), `.gitignore`, initial commit
  - [x] Minimal Compose shell: theme foundation, nav foundation, Today placeholder
  - [x] Testing skeleton: engine unit tests (recurrence subset, streak)
  - [ ] Verify: open in Android Studio, `gradle wrapper`, `assembleDebug` + `testDebugUnitTest` green

- [ ] **PHASE 1 — Design system and visual identity**
  - Aurora theme polish; `MomentumColors` for all 12 theme concepts (or token schema first)
  - Core components: `ActivityCard`, `ProgressOrb`, `CompletionButton`, `StatCard`,
    `EmptyState`, `CategoryChip`, `StreakBadge` + previews (light/dark)
  - Typography scale finalization; reduced-motion hooks

- [ ] **PHASE 2 — Activity domain + Room persistence**
  - Entities/DAOs/database per `DATA_MODEL.md`; schema export; repository impls
  - DataStore settings; `AppContainer` wiring; ViewModel factories
  - DB tests (Robolectric or instrumented harness) + mapper tests

- [ ] **PHASE 3 — Recurrence engine**
  - Complete all recurrence types; DST/leap edge cases; ~100% branch coverage
  - Property-style tests over random windows; benchmark sanity

- [ ] **PHASE 4 — Today experience**
  - `GetTodaySchedule` use case; Today ViewModel + states; schedule list UI
  - Complete/uncomplete with feedback; empty/loading/error states

- [ ] **PHASE 5 — Activity creation/editing**
  - Add/edit form, validation, icon/category pickers, recurrence selector
  - Detail screen; archive (soft delete) flow; forward-only edit semantics

- [ ] **PHASE 6 — Streak and analytics engine**
  - Pure-Kotlin stats: %, streaks, missed, consistency, trends; ~100% coverage

- [ ] **PHASE 7 — Insights dashboard**
  - Stat cards, animated charts, category performance, trends UI

- [ ] **PHASE 8 — Calendar**
  - Heatmap month, day detail, navigation across months

- [ ] **PHASE 9 — Notifications/reminders**
  - Scheduler (AlarmManager + WorkManager), actions incl. mark-complete,
    reboot recovery, reschedule-on-edit

- [ ] **PHASE 10 — Theme gallery**
  - Data-driven themes, picker UI, DataStore persistence, dark-mode modes

- [ ] **PHASE 11 — Accessibility and performance**
  - TalkBack pass, contrast audit, 48 dp targets, dynamic type
  - Cold-start < 1 s, 60 fps, baseline profiles, query paging audit

- [ ] **PHASE 12 — Testing and production hardening**
  - Full suite green, release build, versioning, store-readiness checklist
