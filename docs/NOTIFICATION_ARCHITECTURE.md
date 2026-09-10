# Momentum — Notification Architecture (Phase 9, implemented)

> Local-only reminders. No FCM, no network, no account. ADR-07.

## Components (`com.momentum.app.notification`)

| Piece | Role |
|---|---|
| `ReminderScheduler` (+ `AlarmReminderScheduler`) | Exact fire via `AlarmManager`; inexact fallback when `canScheduleExactAlarms()` is false |
| `ReminderReceiver` | Alarm entry point; offloads to WorkManager (`goAsync` + `FireReminderWorker`) |
| `FireReminderWorker` | Loads activity, posts notification with **Mark done** action, re-arms next fire |
| `CompleteActionReceiver` | `MARK_COMPLETE` action → `ToggleCompletionUseCase(today)` → dismisses notification |
| `BootReceiver` + `RescheduleWorker` | `BOOT_COMPLETED` → 30 s delayed re-arm of every enabled reminder |
| `ensureReminderChannel` | Creates `momentum_reminders` channel (called from `MomentumApp.onCreate`) |
| `MainActivity` launch re-arm | Idempotent re-schedule of all enabled reminders on every cold start |

## Behavior contracts

- **One reminder per activity in v1 UI** (time `HH:mm`, follows the activity
  schedule). The `reminders` table supports N rows; the editor grows later.
- **Edits re-key**: every activity save calls `scheduleForActivity` (cancel +
  re-arm). Pause/archive/delete call `cancelForActivity` first so nothing fires
  for dead rows.
- **Exactness**: `setExactAndAllowWhileIdle` when the exact-alarm grant holds;
  otherwise `setAndAllowWhileIdle` (still reminds, Doze-friendly). The app never
  demands the grant — it degrades gracefully.
- **Permission**: `POST_NOTIFICATIONS` requested once on first launch (API 33+);
  denial is sticky — Settings links to the system notification screen instead of
  nagging. Reminder copy is friendly and rate-limited by the schedule itself.
- **Reboot**: alarms don't survive restarts; `BootReceiver` + launch re-arm cover it.
- **Process safety**: receivers do no DB work on the main thread; all Room
  access runs in workers/IO scope.

## Manifest

Permissions: `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`,
`SCHEDULE_EXACT_ALARM`. Receivers: `ReminderReceiver` / `CompleteActionReceiver`
(`exported=false`), `BootReceiver` (`exported=true` + `BOOT_COMPLETED` filter).
No `INTERNET` — enforced by review, never add it for notifications.
