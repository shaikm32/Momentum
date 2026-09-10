# Momentum — Data Model (Room, v1 draft)

> Status: Phase 0 design. Implemented in Phase 2. Schema exports → `app/schemas/`.

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
| `description` | TEXT = "" | |
| `iconKey` | TEXT = "spark" | curated set |
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

Pre-1.0: destructive fallback allowed but must be a conscious, documented choice
per change. Post-1.0: real `Migration`s + schema JSON + migration tests.
