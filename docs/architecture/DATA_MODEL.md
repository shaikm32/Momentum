# Momentum — Data Model (Room, v4)

> Status: v4 schema (Room version 4) implemented. Schema exports → `app/schemas/`.

## Conventions

- Dates at layer boundaries: ISO-8601 `yyyy-MM-dd` strings (`LocalDate` in Kotlin).
- Timestamps: epoch-ms `Long` (completion instants, created/updated).
- All FK + date columns indexed. No `SELECT *` unbounded into UI — page/limit.
- Timezone: schedule in **device-local dates**. Recurrence math is `LocalDate`-only,
  so DST shifts can't move an occurrence. Completion instant stored as epoch-ms
  for audit; occurrence identity is the local date string.

## Tables

### `activities`

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AI | |
| `title` | TEXT NOT NULL | ≤120 chars (enforced in use case) |
| `activityType` | TEXT NOT NULL = "HABIT" | `HABIT/TASK` (v2; v1 rows migrate to HABIT). Never inferred from recurrence — see ADR-19 |
| `habitIntent` | TEXT NOT NULL = "BUILD" | `BUILD/REDUCE/AVOID` (v3; pre-v3 rows migrate to BUILD). Ignored for TASKs — see ADR-20 |
| `dueTime` | TEXT NULL | Optional task due time `HH:mm` (v3; null = no specific time). Display-only; reminders stay separate |
| `description` | TEXT = "" | |
| `iconKey` | TEXT = "spark" | curated set |
| `habitTemplateId` | TEXT NULL | v4; predefined template id for the icon lock after rename (Decision 11). Null = custom/pre-template; legacy rows fall back to `matchTemplate` on edit |
| `categoryId` | INTEGER NULL → `categories(id)` ON DELETE SET NULL | index |
| `colorArgb` | INTEGER NULL | null = inherit |
| `startDate` | TEXT NOT NULL (`yyyy-MM-dd`) | index; first schedulable date |
| `endDate` | TEXT NULL | null = indefinite; index |
| `recurrenceType` | TEXT NOT NULL | `ONCE/DAILY/WEEKDAYS/SELECTED_WEEKDAYS/EVERY_N_DAYS/WEEKLY/EVERY_N_WEEKS/MONTHLY/CUSTOM` |
| `recurrencePayload` | TEXT NOT NULL (JSON, default `{}`) | e.g. `{"days":[1,3,5],"interval":2,"dayOfMonth":15}` |
| `targetPerOccurrence` | REAL = 1.0 | |
| `unit` | TEXT = "" | "glasses", "pages", "min" |
| `priority` | INTEGER = 0 | 0–3 |
| `notes` | TEXT = "" | |
| `status` | TEXT = ACTIVE | `ACTIVE/PAUSED/ARCHIVED` (soft delete = ARCHIVED), index |
| `createdAt` / `updatedAt` | INTEGER (epoch-ms) | |

Type-tagged recurrence rule (`recurrenceType` + JSON payload) — **not** one row
per future occurrence. Adding a recurrence type = new tag + engine branch +
payload schema + tests + picker UI. See ADR-06.

### `completion_records`

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AI | |
| `activityId` | INTEGER NOT NULL → `activities(id)` ON DELETE CASCADE | index |
| `occurrenceDate` | TEXT NOT NULL (`yyyy-MM-dd`) | index |
| `completedAt` | INTEGER (epoch-ms) | |
| `value` | REAL = 1.0 | partial credit |
| `note` | TEXT = "" | |

UNIQUE(`activityId`, `occurrenceDate`) — re-completing is an upsert, never a duplicate.

### `categories`

| Column | Notes |
|---|---|
| `id` PK AI · `name` UNIQUE NOT NULL · `iconKey` · `colorArgb` · `sortOrder` · `isDefault` (seeded, user-editable) |

Seeded defaults (user can rename): Health, Fitness, Mind, Learning, Work, Home.

### `reminders`

| Column | Notes |
|---|---|
| `id` PK AI · `activityId` FK CASCADE (index) · `timeLocal` TEXT `HH:mm` · `daysMask` INTEGER (bitmask, 0 = follows activity schedule) · `enabled` BOOL · `lastScheduledHash` TEXT (dirty-check for rescheduler) |

### `app_settings` (DataStore, not Room)

`themeId`, `darkMode` (system/light/dark), `weekStart` (Mon/Sun), `onboardingDone`,
`reduceMotionOverride?` (default: follow system).

## Relationships

- Activity N→1 Category; Activity 1→N CompletionRecords; Activity 1→N Reminders.
- Deleting an activity = `status=ARCHIVED` (history preserved). Hard delete is an
  explicit user action (Phase 5+, needs ADR) and cascades completions+reminders.
- Archiving is reversible: **Restore** (detail Actions / Archived tab) sets
  `status` back to `ACTIVE` via the same `setStatus` update — completions,
  reminders, and `createdAt` are untouched, no migration (ADR-25).

## Edit semantics for recurring activities (hard rule)

- `CompletionRecord`s are immutable truth: **edits never rewrite or delete them.**
- Edits to title/meta apply to the whole activity (history display uses current title).
- Edits to `recurrence`/`startDate`/`endDate`/`target` apply **forward from the edit
  date**: occurrences before the edit keep the old schedule for streak math
  (store `recurrenceChangeLog` — Phase 2 stretch — or, v1 minimum, document that
  pre-edit streaks are computed under the new rule and flag it as a known limitation
  to fix with the change log; decision recorded in Phase 2).
- Pause (`PAUSED`) excludes the activity from Today; past completions untouched.

## Start/end date × recurrence

- Occurrence is scheduled iff `startDate ≤ date ≤ endDate?` AND recurrence matches.
- `endDate` in the past ⇒ nothing scheduled; row + history retained.
- `ONCE` uses `startDate` as the due date (`endDate` ignored).

## Migrations

Destructive fallback is removed and must never return: without it, Room fails
fast on a schema mismatch instead of silently wiping user data. Migrations:
v1 → v2 adds `activities.activityType` (default HABIT); v2 → v3 adds
`activities.habitIntent` (default BUILD) and nullable `activities.dueTime`
(`MIGRATION_2_3`, proven by `Migration2To3Test`); v3 → v4 adds nullable
`activities.habitTemplateId` for the predefined icon lock (`MIGRATION_3_4`,
proven by `Migration3To4Test`). Every future schema change ships: version
bump + real `Migration` + schema JSON + migration test proving activities,
completions (with values), reminders, recurrence payloads, categories,
statuses, types and intents survive.
