# Momentum — Personal Progress Companion

> Small steps, unstoppable momentum.

Momentum is a premium, **offline-first** Android app for tracking anything you want
to complete consistently: habits, routines, recurring tasks, one-time tasks,
medicines & supplements, exercise, reading, learning, meditation, and personal goals.

One unified concept — **Activity** — instead of separate silos for habits vs. tasks.
Completing small things feels rewarding: instant tactile feedback, streaks over
scheduled occurrences, local reminders, insights from real history, and 8
data-driven themes with intentional light + dark modes.

## Key features

- **Today hero** — greeting, progress orb, scheduled activities, motivational messaging
- **Activities** — search, status tabs (active/paused/archived), category filters
- **Fast creation** — icon/category pickers, full recurrence editor, optional
  target+unit (e.g. 8 glasses), priority, local reminder
- **Reliable recurrence** — once, daily, weekdays, custom days, every N days,
  weekly, every N weeks, monthly; pure-Kotlin engine, DST-safe `LocalDate` math
- **Honest streaks** — counted over *scheduled* occurrences only (M/W/F streaks
  ignore Tuesdays); completion history is append-style truth in Room
- **Insights** — 7/30-day rates, weekly bars, category performance, most consistent
- **Calendar** — heatmap month + per-day scheduled/completed detail, quick-complete
- **Local reminders** — AlarmManager exact fire + WorkManager resync + reboot
  recovery, mark-done notification action; fully offline
- **Themes** — Aurora, Ocean, Sunset, Forest, Lavender, Midnight, Cosmic, Arctic
- **Privacy** — no account, no backend, no tracking; all data stays on-device

## Technology

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · Room (KSP) ·
DataStore Preferences · Coroutines/Flow · Lifecycle ViewModel · WorkManager
(local reminders only). Single `:app` module, Clean Architecture + MVVM, manual
DI via `AppContainer`. See `docs/ARCHITECTURE.md` and `docs/DECISIONS.md`.

Offline-first is a hard requirement: `AndroidManifest.xml` must never gain
`INTERNET`. Permissions are limited to notifications/alarms (`POST_NOTIFICATIONS`,
`RECEIVE_BOOT_COMPLETED`, `SCHEDULE_EXACT_ALARM`).

## Build

Open in **Android Studio** (provisions JDK 17 + SDK 35 on first sync), then:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Minimum supported: `minSdk 26`, `targetSdk/compileSdk 35`.
Versions live only in `gradle/libs.versions.toml`.

## Tests

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Unit tests cover the correctness core: recurrence engine (all types, DST-safe
monthly clamp, start/end gating), streaks over scheduled occurrences, analytics
(trends, consistency), the recurrence storage codec, and use cases
(schedule ordering, toggle semantics).

## Project structure

```
app/src/main/java/com/momentum/app/
├── presentation/   Compose screens, components, theme registry, navigation
├── domain/         models, repository contracts, use cases, pure-Kotlin engines
├── data/           Room entities/DAOs/database, DataStore, repository impls
└── notification/   local reminder scheduler, receivers, workers
docs/               product, architecture, data model, design system, decisions…
```

## Product philosophy

- Never shame missed activities; encourage the next small win.
- Real simple functionality beats fake sophisticated functionality — every UI
  control is wired to the real repository or explicitly labeled otherwise.
- Responsiveness wins over visual effects; 60 fps baseline, reduced-motion respected.
- Privacy is a feature: your data stays on your device.

## Roadmap

See `docs/ROADMAP.md`. Future possibilities (cloud sync, widgets, Wear OS) stay
out of v1 unless they serve the current spec.

## License

License to be decided before publication (placeholder — do not publish without one).
