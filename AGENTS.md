# AGENTS.md — Permanent instructions for AI coding agents working on Momentum

> This file is the source of truth for how work gets done in this repository.
> Read it at the start of every session. If it conflicts with a user prompt,
> follow the user prompt for that session but flag the conflict explicitly.

## 1. Project purpose

Momentum (`com.momentum.app`) is a premium, **offline-first** Android app: a personal
progress companion for habits, routines, tasks, medicines/supplements, learning
activities, and personal goals. The fundamental domain concept is **Activity** —
one unified system, not separate silos for habits vs. tasks. See
`docs/PRODUCT_SPEC.md`.

**Do not turn this project into a generic Material Design CRUD application.**
Every screen must serve the product vision (consistency, momentum, reward) and the
design language in `docs/DESIGN_SYSTEM.md`. Default-looking lists, checkbox rows,
FAB-centric layouts, and gray cards are regressions, not progress.

## 2. Hard requirements (non-negotiable)

- **All core functionality must work completely offline.** No internet, backend,
  account, login, cloud, Firebase, Supabase, REST API, auth, analytics SDKs, ads,
  or tracking. User data stays on-device (Room + DataStore). Future sync needs an
  ADR in `docs/DECISIONS.md` first and must not reshape current architecture.
- **Never implement fake persistence merely to make a screen appear functional.**
  A screen is either wired to the real repository (Room/DataStore) with real
  loading/empty/error states, or it is an explicit, labeled placeholder stating
  which phase implements it. No in-memory stubs masquerading as data, no
  hardcoded "demo activities" outside of `@Preview` functions.
- Privacy: no account, no backend, minimal permissions (notifications/alarms
  only), no tracking. See `docs/PRODUCT_SPEC.md` § Privacy.

## 3. Architecture — Clean Architecture + MVVM (single `app` module)

```
UI (Compose) → ViewModel → Use Case → Repository → Local Data Source → Room/DataStore
```

- Layers (packages under `com.momentum.app`):
  - `presentation/` — Compose screens, components, theme, navigation. No business logic.
  - `domain/` — `model/`, `repository/` (interfaces), `usecase/`, `engine/`
    (recurrence, streak/analytics — pure Kotlin, zero Android imports).
  - `data/` — Room entities/DAOs, DataStore, repository implementations.
  - `notification/` — reminder scheduling (Phase 9).
- Data flow is unidirectional: UI emits events → ViewModel updates `StateFlow` →
  Compose collects state. Composables never call repositories/DAOs directly.
- **Business logic lives in use cases and engines, never in composables.**
  A composable containing `if` chains that compute streaks, dates, or percentages
  is a bug in layering — extract it.
- Dependency injection is manual via `AppContainer` (`MomentumApp.kt`). Do NOT
  introduce Hilt/Koin/multi-module splits without an ADR justifying it.
- Do not create unnecessary abstractions: no generic `BaseViewModel`,
  `BaseRepository`, `UseCase<I, O>` interfaces, or extra modules without a clear,
  stated benefit. Prefer a concrete class that does one thing well.
- **Never replace working architecture without justification.** Propose the change,
  record it in `docs/DECISIONS.md`, then implement.

## 4. Kotlin conventions

- Kotlin style: official `kotlin.code.style=official`. 100-col soft limit.
- Null-safety over `!!` — `!!` is banned except in tests with a comment.
- Coroutines + `Flow`/`StateFlow` for async. No `GlobalScope`, no blocking calls
  on the main thread. Expose suspend functions from repositories, flows for streams.
- Use `java.time` (`LocalDate`) for all date logic. Store dates as ISO-8601
  `yyyy-MM-dd` strings at layer boundaries; epoch-ms (`Long`) for timestamps.
- Sealed interfaces/classes for UI state (`TodayUiState.Loading/Success/Error`)
  and for domain variants (`Recurrence`).
- Immutability by default (`val`, immutable collections, `data class.copy()`).

## 5. Compose conventions

- Material 3 + the Momentum design system (`presentation/theme`, `presentation/components`).
  **No hardcoded colors in screens** — read from `LocalMomentumColors`.
  No `Color.Gray`, no `MaterialTheme.colorScheme.surfaceVariant`-as-decoration hacks;
  use semantic Momentum colors.
- State hoisting: screens take `state: XUiState` + event lambdas; ViewModels own state.
  Collect with `collectAsStateWithLifecycle()`.
- Keep composables small and previewable. Every reusable component gets an
  `@Preview` (light + dark) using fake data local to the preview.
- Lists: `LazyColumn`/`LazyVerticalGrid` with stable `key`s. Never nest
  scrollables without justification.
- Side effects only via `LaunchedEffect`/`remember*`; navigation events via a
  one-shot channel/shared-flow consumed once, never as state.

## 6. State management

- One `StateFlow<UiState>` per ViewModel, updated from a single thread-safe path
  (viewModelScope). Loading/empty/error are first-class states, not afterthoughts.
- Repositories expose cold `Flow`s from Room; ViewModels convert with
  `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initial)`.
- No state in singletons outside `AppContainer`; no static mutable state.

## 7. Database rules (Room)

- Entities mirror `docs/DATA_MODEL.md`. Schema changes require: entity update +
  migration (or documented destructive fallback during pre-1.0 dev) + schema JSON
  export kept in `app/schemas/` (committed).
- DAOs return `Flow` for observed queries, `suspend` for one-shots. No DB access
  on the main thread; Room enforces this — don't fight it with `allowMainThreadQueries`.
- Completion history is append-style truth: **never rewrite or delete
  `CompletionRecord`s when an activity is edited** — edits apply forward from the
  edit date (see `docs/DATA_MODEL.md`). Deleting an activity soft-deletes
  (`ARCHIVED`) by default in v1; hard delete needs explicit UX + ADR.
- Indexes on all foreign keys and date columns. Recurrence stored as a
  type-tagged rule (see `docs/DATA_MODEL.md`), NOT as one row per future occurrence.

## 8. Offline-first + notifications

- Zero network dependency: the app must compile and fully function with all
  network SDKs absent. `AndroidManifest.xml` must never gain `INTERNET`.
- Reminders are local-only: `AlarmManager` for exact timing + `WorkManager` for
  resilient rescheduling + `BOOT_COMPLETED` receiver. No FCM. Details in
  `docs/DECISIONS.md` (ADR-07).

## 9. UI/UX, animation, accessibility, performance

- UX bar: expressive typography, layered gradient surfaces, glow used sparingly,
  meaningful micro-interactions (completion feedback, streak celebration),
  rewarding empty states. Follow `docs/DESIGN_SYSTEM.md`; generic Material
  defaults are the floor, never the ceiling.
- Animation standards: 150–300 ms eases for micro-interactions; shared-element /
  container-transform for screen transitions where it aids continuity; always
  respect reduced-motion (`AccessibilityManager` / `isReduceMotionEnabled`
  equivalent — never force animation on users who disable it). No animation that
  blocks input or delays data.
- Accessibility: minimum 48 dp touch targets, content descriptions for all
  icon-only buttons, dynamic-type-safe layouts (no fixed heights clipping large
  text), contrast ≥ 4.5:1 for body text in both light and dark themes, full
  TalkBack traversal order on every new screen.
- Performance: cold start < 1 s on mid-range devices, 60 fps scrolling,
  no allocations in composition hot paths, baseline-profiled before 1.0.
  Database queries paged/limited; never `SELECT *` unbounded into Compose state.

## 10. Testing requirements

- New engine/use-case logic ships with unit tests (`app/src/test`). The
  recurrence and streak engines must stay at ~100% branch coverage — they are
  the correctness core of the product.
- New DAO/repository behavior ships with instrumented or Robolectric DB tests
  (Phase 2 wires the harness; until then, unit-test mappers/engines).
- Run the relevant tests after every significant change:
  `./gradlew :app:testDebugUnitTest` at minimum; full
  `./gradlew :app:assembleDebug :app:testDebugUnitTest` before finishing a phase.
- This bootstrap machine has no JDK/SDK, so builds run in Android Studio or CI.
  If you cannot execute a build, say so explicitly and list the exact commands
  the user should run.

## 11. Dependency rules

- Version catalog (`gradle/libs.versions.toml`) is the only place for versions.
  No hardcoded versions in module build files.
- Approved stack: Kotlin, Compose (BOM), Material3, Navigation Compose, Room,
  DataStore, Coroutines/Flow, Lifecycle ViewModel, WorkManager (local reminders
  only). Anything else (Coil, accompanist, Hilt, testing libs beyond JUnit/Turbine)
  needs a one-paragraph justification in the PR/session summary + `docs/DECISIONS.md`
  if architectural.
- Banned without ADR: any networking (Retrofit/OkHttp), Firebase/Supabase/Amp,
  analytics/crash SDKs, ads, cross-platform runtimes.

## 12. Git / change management

- Small, focused commits with conventional messages (`feat:`, `fix:`, `chore:`,
  `docs:`, `test:`, `refactor:`). One logical change per commit.
- Never commit secrets, keystores, `google-services.json`, or `local.properties`.
- Do not push, set remotes, or rewrite history unless the user explicitly asks.
- Keep `docs/DECISIONS.md` append-only: new decisions are new entries, never
  silent edits to old ones. Update `docs/ROADMAP.md` checkboxes as phases land.

## 13. Session workflow (every task)

1. **Inspect before modifying:** read the files you will touch (and their tests)
   plus the relevant `docs/` page. Never scaffold a parallel implementation next
   to an existing one — extend it.
2. **Plan briefly:** state which phase/layer the change belongs to and what you
   will touch. For multi-step work, track it with a todo list.
3. **Implement thin slices:** smallest working increment, wired to real data,
   with tests. No drive-by refactors of untouched code.
4. **Verify:** build + run relevant tests after significant changes (see §10).
   Report exact results (pass/fail, commands run). If you could not run them,
   say why and what to run.
5. **Document:** update affected `docs/` pages and `docs/DECISIONS.md` when you
   make a decision future sessions need to know.
