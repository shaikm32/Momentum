# Momentum — UX / Design Specification

> Companion to `docs/DESIGN_SYSTEM.md` (tokens/components) — this page covers
> screen-level UX, flows, and interaction contracts.

## Principles

- **Hierarchy > decoration.** Every screen answers one question first
  (Today: "what matters today?").
- **Motion communicates state** (completion pop, orb sweep, card weight change),
  never decorates. 150–300 ms eases; spring for completion; no animation blocks
  input or delays data. System reduced-motion (`ANIMATOR_DURATION_SCALE == 0`)
  or the Settings override disables glow pulse/count-ups.
- **Supportive, never shaming.** Empty states and missed days use encouraging
  copy. No streak-freeze anxiety mechanics, no leaderboards.
- **Instant + tactile + rewarding** completion: state flips immediately in Room,
  then spring pop + haptic + progress update. Quick-complete outside Today
  (Activities list, Calendar day) toggles the real occurrence and says
  "Not scheduled today." when the rule doesn't match — never a fake toggle.

## Flows

### First run
Onboarding (3 short pages, skippable) → "Start with examples" seeds 3 daily
activities (water / reading / walk) or "Start fresh" → Today. `onboardingDone`
persists in DataStore.

### Create (seconds for basics)
Title → icon → category → schedule → dates → target/unit → priority →
notes → reminder → Save. Sensible defaults (Daily, today, no reminder).
Validation is inline (blank title, over-long title, end < start).

### Complete
Tap the ring on Today / detail / calendar day → immediate Room upsert →
streak + orb update → haptic. Re-tap uncompletes. First 100% day fires a
one-shot celebration (haptic + snackbar), not a blocking modal.

### Manage
Activities screen: search + Active/Paused/Archived tabs + category chips.
Pause hides from Today (alarms cancelled, history kept). Archive is soft-delete.
Hard delete requires typed confirmation dialog and cascades completions +
reminders while cancelling alarms.

### Detail
Header (icon, title, schedule, complete ring) → streak stat cards → 30-day
rate → schedule/target/reminder rows → 14-day history dots → pause/resume,
archive, delete.

### Insights
7-day + 30-day rates, 30-day completions, best streak, custom-drawn weekly bars
(no chart lib), category rates with progress bars, top-5 consistent activities.
All computed locally from Room snapshots; empty state until data exists.

### Calendar
Monday-first month grid (follows week-start setting for the header order in a
later pass — currently Monday-first). Intensity: full = success, partial =
primary wash, none = surface, unscheduled = faint. Selecting a day lists that
day's real schedule with working toggles; "Today" jumps back.

### Themes
Gallery of 8 concepts × light/dark with gradient + palette-dot previews.
Selection persists; app re-themes via `LocalMomentumColors` (no hardcoded
colors in screens). Dark mode: System / Light / Dark.

### Settings
Appearance (theme, mode, reduce motion), notification deep-link to system
settings, week start, motivational toggle, clear-all-data (alarms cancelled
first, double-confirmed), about + privacy statement.

## Accessibility contracts

- ≥48 dp touch targets (completion ring is 56 dp); icon-only buttons always
  carry content descriptions including the activity title.
- Dynamic type: no fixed-height text clipping; cards grow with content.
- Body contrast ≥ 4.5:1 in all 8 themes × light/dark (chosen palettes audited
  at authoring; re-audit when adding themes).
- TalkBack order: header → progress → list in DOM order; decorative dots carry
  no semantics (`contentDescription = null` on purely visual icons).

## Copy tone

Short, warm, calm. Examples: "Small steps, unstoppable momentum.",
"First win banked. Keep going.", "One more — finish strong."
Reminder copy is friendly, never aggressive, and never shames.
