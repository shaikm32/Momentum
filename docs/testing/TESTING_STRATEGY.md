# Momentum — Testing Strategy

## What we test and why

The correctness core is **pure Kotlin with zero Android imports**, so it runs as
fast local unit tests (`app/src/test`):

| Area | File | Bar |
|---|---|---|
| Recurrence (all 8 types, monthly clamp, start/end gating, `isScheduled`, paused/archived exclusion) | `RecurrenceEngineTest` | ~100% branch coverage |
| Streaks over scheduled occurrences, longest run, missed lists | `StreakEngineTest` | ~100% |
| Analytics (rates, consistency, week-over-week trend incl. insufficient-data) | `AnalyticsEngineTest` (in `StreakEngineTest.kt`) | ~100% |
| Storage codec round-trips, unknown-future-type degradation, malformed payloads | `RecurrenceCodecTest` | full |
| Use cases (schedule ordering by priority, M/W/F streak semantics, toggle flip) | `UseCasesTest` (fake `CompletionRepository`, `runTest`) | full |

## Conventions

- Tests assert **real behavior** (engine outputs, state transitions), never
  mocked-UI existence.
- Fakes are hand-written and minimal (`FakeCompletionRepository`); no mock
  framework in v1 (dependency discipline).
- DB/DAO tests: the harness (Robolectric or instrumented) is the next testing
  investment — until then, mappers stay trivial (field copies + codec, both
  unit-tested) and repository logic stays thin.
- Critical UI flows (onboarding → create → complete → streak → theme switch →
  reminder config) are verified manually per `docs/ROADMAP.md` Phase 12 until
  a Compose UI-test pass is wired.

## Commands

```powershell
.\gradlew.bat :app:testDebugUnitTest            # unit tests
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest   # full gate before finishing a phase
```

This bootstrap machine has no JDK/SDK — builds run in Android Studio or CI.
If you cannot execute a build, say so explicitly and list what to run.
