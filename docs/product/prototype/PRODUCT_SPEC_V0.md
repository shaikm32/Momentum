# Momentum — Product Specification

> Owner: product vision. Status: v1 implemented (Phases 1–11 landed; Phase 12 device/build verification open).

## 1. Concept

**Momentum** is a beautiful personal progress companion that helps users
consistently complete the activities that matter to them. It rewards consistency,
surfaces progress, and makes showing up feel good — premium, playful, calming.

Tagline candidates: *"Small steps, unstoppable momentum."*

## 2. The Activity — fundamental domain concept

There is **one unified concept — Activity** — instead of separate systems for
habits, tasks, medicines, etc. An activity is anything the user wants to
complete over time. Examples:

- Habits (drink water, no sugar, make bed)
- Recurring tasks (take out trash Tuesdays, weekly review)
- One-time tasks (book dentist, renew passport)
- Medicines & supplements (Vitamin D 8am, antibiotic course)
- Exercise (run, gym, yoga, walk 10k steps)
- Reading / studying / meditation / journaling
- Learning activities (practice guitar 20 min, Duolingo)
- Work & personal routines (standup prep, shutdown ritual)
- Goals (ship portfolio, 30-day challenge)

Differentiation between these comes from **category + recurrence + target**,
not from separate entity types.

## 3. Activity fields (v1)

| Field | Type | Notes |
|---|---|---|
| `title` | String (required, ≤120) | e.g. "Morning run" |
| `description` | String | Optional supporting text |
| `icon` | key (String) | From curated icon set, Phase 5 picker |
| `category` | FK → Category | Health, Learning, Mind, Work, Home, Fitness, … user-extensible |
| `color/theme` | ARGB override or null | Null = inherit category/theme accent |
| `startDate` | LocalDate | First date it can be scheduled |
| `endDate` | LocalDate? | Null = indefinite. Past end ⇒ no longer scheduled, history kept |
| `recurrence` | rule (see §4) | Defines scheduled occurrences |
| `target` | Double + `unit` | e.g. 8 glasses, 20 pages, 30 min. Default 1 × "time" |
| `priority` | 0–3 | Affects Today ordering |
| `notes` | String | Free text |
| `reminders` | 0..n | Local reminders (Phase 9) |
| `status` | ACTIVE / PAUSED / ARCHIVED | Paused = hidden from Today but kept; archived = soft-deleted |

## 4. Recurrence

Supported (engine in Phase 3, picker UI in Phase 5):

- `once` — one-time task with due date
- `daily`
- `weekdays` (Mon–Fri preset)
- `selectedWeekdays` (any subset)
- `everyNDays` (e.g. every 2/3 days)
- `weekly` (preset: same as `everyNWeeks(n=1)`)
- `everyNWeeks`
- `monthly` (day-of-month; clamp to month length)
- `custom` — reserved extension point for future rule combinators

Full contract in `docs/ARCHITECTURE.md` § Recurrence engine.

## 5. Completion history

- Stored as **`CompletionRecord`** rows, one per completed occurrence
  (activityId + occurrenceDate + completedAt + value + note).
- History is **append-style truth**: editing an activity never rewrites past
  records (see `docs/DATA_MODEL.md`).
- Partial completion supported via `value` vs `target` (e.g. 4/8 glasses = 50%).

## 6. Statistics & analytics (engine Phase 6, UI Phase 7)

Per activity, category, and globally:

- Daily / weekly / monthly completion (counts + %)
- Completion percentage over arbitrary windows
- Current streak (consecutive **scheduled** occurrences completed)
- Longest streak
- Total completions
- Missed occurrences (scheduled but not completed)
- Category performance comparison
- Consistency score (e.g. % of scheduled occurrences hit in last 30 days)
- Trends (week-over-week direction)

Streak rule (hard): **only scheduled occurrences count.** An M/W/F activity
with Tuesday missing is still a perfect streak.

## 7. Offline-first (HARD REQUIREMENT)

The app works fully without internet, backend, account, login, or cloud:

- Create / edit / delete / complete activities
- Statistics, history, dashboard
- Theme changes
- Reminder scheduling (local)

No network dependency may exist. No Firebase, Supabase, REST, auth, analytics
or ads SDKs. Future sync requires an ADR and must not reshape the architecture.
`AndroidManifest.xml` must never gain `INTERNET`.

## 8. Privacy

- No account, no backend, data stays on-device (Room + DataStore).
- Permissions limited to notifications/alarms (exact alarms, boot-completed).
- No tracking, no advertising, no analytics SDKs.
- Export/backup (optional, later) is a user-initiated local file, never a sync.

## 9. Non-goals (v1)

- Cloud sync / multi-device / sharing
- Social features, leaderboards
- AI coaching (may revisit post-1.0 with on-device only constraint)
- Wear OS / widgets beyond stretch goals (revisit Phase 12)

## 10. Success criteria (v1)

- Cold start < 1 s on mid-range devices; 60 fps scrolling.
- Full offline functionality; zero crashes on rotation, reboot, DST change.
- Recurrence + streak engines at ~100% branch coverage.
- Accessibility: TalkBack-traversable core flows, 4.5:1 body contrast, 48 dp targets.
