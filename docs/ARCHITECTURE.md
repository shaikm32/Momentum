# Momentum — Architecture

> Status: v1 implemented. Single `app` module, Clean Architecture + MVVM.

## 1. Module strategy

One module (`:app`) until a measured reason forces a split. Multi-module adds
build complexity with zero benefit at this size. Revisit only via ADR.

## 2. Layers & packages (`com.momentum.app`)

```
presentation/        Compose screens, components, theme, navigation — no business logic
  screens/today|activities|insights|calendar|settings|activityedit|themegallery
  components/        design-system composables (ActivityCard, ProgressOrb, …)
  theme/             MomentumColors, typography, theme registry
  navigation/        Routes, MomentumNavGraph
domain/
  model/             Activity, CompletionRecord, Category, Reminder, AppSettings, Recurrence
  repository/        interfaces (ActivityRepository, …) — domain owns the contract
  usecase/           CompleteActivity, GetTodaySchedule, ComputeStreaks, …
  engine/            RecurrenceEngine, StreakEngine, AnalyticsEngine (pure Kotlin)
data/
  local/             Room entities, DAOs, MomentumDatabase, type converters
  datastore/         Theme/settings DataStore
  repository/        *Impl classes
notification/        ReminderScheduler, receivers, notification builder (Phase 9)
```

## 3. Data flow (unidirectional)

```
Compose UI ──event──▶ ViewModel ──▶ UseCase ──▶ Repository ──▶ Room/DataStore
    ▲                      │                                                │
    └──── StateFlow<UiState> ◀── Flow ◀─────────────────────────────────────┘
```

- UI emits events (e.g. `TodayEvent.ToggleComplete(id, date)`).
- ViewModel owns one `StateFlow<UiState>` (sealed: Loading/Success/Error + Empty).
- Repositories expose cold `Flow`s from Room; ViewModels `stateIn(…, WhileSubscribed(5000))`.
- Composables never touch repositories/DAOs. Business logic never lives in composables.

## 4. Domain models

`Activity` (id, title, description, iconKey, categoryId, colorArgb?, startDate,
endDate?, recurrence, targetPerOccurrence, unit, priority, notes, status,
createdAt/updatedAt) · `CompletionRecord` (activityId, occurrenceDate `yyyy-MM-dd`,
completedAtEpochMs, value, note) · `Category` · `Reminder` · `AppSettings`.
Full schema in `docs/DATA_MODEL.md`.

## 5. Repositories

Interfaces in `domain/repository`, e.g.:

```kotlin
interface ActivityRepository {
    fun observeActive(): Flow<List<Activity>>
    suspend fun getById(id: Long): Activity?
    suspend fun upsert(activity: Activity): Long
    suspend fun setStatus(id: Long, status: ActivityStatus)
    fun observeCompletions(activityId: Long, from: String, to: String): Flow<List<CompletionRecord>>
    suspend fun complete(activityId: Long, occurrenceDate: String, value: Double): Long
}
```

Implementations map Room entities ↔ domain models; no Android classes leak into `domain`.

## 6. Use cases (thin orchestrators)

Examples: `GetTodayScheduleUseCase(activityRepo, recurrenceEngine)`,
`ToggleCompletionUseCase`, `ComputeActivityStatsUseCase(analyticsEngine)`.
One public `operator fun invoke(…)` each; inject engines + repositories via constructor.

## 7. ViewModels

- One per screen, created with a simple `ViewModelProvider.Factory` from `AppContainer`.
- Own `StateFlow<UiState>` + one-shot navigation/effect channel.
- Example: `TodayViewModel(getTodaySchedule: GetTodayScheduleUseCase, toggle: ToggleCompletionUseCase)`.

## 8. Compose UI

Screens are `state + lambdas` functions; reusable pieces live in `components/`
with light+dark `@Preview`s. Navigation: single `NavHost` (Today, Activities,
Insights, Calendar, Settings) + secondary graph (add/edit/details/theme picker).

## 9. Persistence

- **Room** for activities/completions/categories/reminders. Entities mirror
  `docs/DATA_MODEL.md`; DAOs return `Flow`/suspend; schema JSON exported to
  `app/schemas/` and committed; migrations per change.
- **DataStore (Preferences)** for theme selection, onboarding flags, settings keys.

## 10. Recurrence engine (Phase 3)

Pure-Kotlin object (`domain/engine`), zero Android imports:

```kotlin
fun occurrencesInRange(recurrence, anchor, startDate, endDate, rangeStart, rangeEnd): List<LocalDate>
```

- Computes scheduled dates from rules; never materializes future rows in DB.
- DST-safe: `LocalDate`-only arithmetic, no instants.
- Phase 0 ships a working subset (see `Engines.kt`); Phase 3 completes + hardens it.
- Independently unit-testable at ~100% branch coverage.

## 11. Analytics engine (Phase 6)

Pure-Kotlin: completion %, current/longest streak over **scheduled** occurrences,
missed lists, consistency score, trends. Inputs are date lists + completion sets —
no DB, no Android. Same coverage bar as recurrence.

## 12. Theme system

`MomentumColors` data class + `LocalMomentumColors` CompositionLocal (Phase 0).
Phase 1 expands the registry; Phase 10 makes it data-driven (theme id in
DataStore, full gallery). Screens never hardcode colors.

## 13. Notification scheduling (Phase 9)

`ReminderScheduler` interface → `AlarmManager` for exact fire + `WorkManager`
for resilient resync + `BOOT_COMPLETED` receiver. Details in `docs/DECISIONS.md`
(ADR-07). No FCM, ever, without an ADR.

## 14. DI

Manual `AppContainer` owned by `MomentumApp`. No Hilt/Koin unless an ADR
justifies the migration cost.
