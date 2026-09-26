# Momentum Dashboard — Product Decisions & Decision Log

**Status:** Finalized
**Scope:** Home / Dashboard
**Last consolidated:** September 2026

This document records decisions that should be treated as product
constraints unless explicitly revisited.

---

# 1. Core Dashboard Principle

### Decision

Home is focused strictly on **today**.

### Rationale

The dashboard must answer:

> "How am I doing today, and what still needs my attention?"

Historical information belongs in Insights or activity-specific history.

### Consequence

Do not add:

- General historical charts.
- Yesterday/tomorrow content.
- Unrelated metrics.
- Long-term analytics.

to Home.

---

# 2. Primary Navigation

### Decision

Momentum has exactly four primary tabs:

```text
Home | Habits | Insights | Settings
Rejected
Calendar as a primary tab.
Rationale
Date/history exploration is a function of Insights and activity history
rather than a top-level destination.

# 3. Generic Dashboard Metrics

Decision
Do not show generic dashboard cards for:
- Overall streak
- Focus
- Sleep
- Wellness
- Health
- Water
- Other metrics without meaningful native data
Rationale
Metrics should not exist merely to fill dashboard space.
A generic streak is ambiguous because Momentum contains multiple activities.

4. Hero Progress

Decision
The Hero progress visualization is the primary Home element.
It communicates today's overall completion progress.
Content
The Hero contains:
- Percentage inside the progress ring.
- Completion count.
- Short contextual/motivational message.
Decision detail
The ring contains only the percentage.
Rationale
Separating percentage and count creates a cleaner visual hierarchy.
5. Date
Decision
Show a concise date above the greeting.
Intended hierarchy:
SAT, SEP 13
Good morning
Rationale
The date provides context without competing with the Hero.
6. Dynamic Greeting
Decision
The greeting changes based on local time.
Examples:
- Good morning
- Good afternoon
- Good evening
- Good night
Rationale
The Home screen should feel connected to the user's current moment.
7. Theme-Based Hero Environment
Decision
The Hero environment follows the active application theme (Light or Dark).
The device clock never selects artwork: the greeting (§6) remains the only
time-aware element on Home.
Theme mapping
Light theme    Light aurora artwork
Dark theme     Dark aurora artwork
Light theme
- Airy aurora.
- Subtle warm horizon glow.
- Bright and calm.
Dark theme
- Deep night aurora.
- Stars.
- Soft luminous aurora.
Important
The visual family and composition stay identical across both themes —
only the atmosphere changes.
Requirements
- The two bundled illustrations are the shared hero artwork pair: the Home
  Hero and the Habits header draw from the same Light/Dark set, selected by
  theme.
- The artwork is the background of the whole Hero surface — date, greeting,
  Settings, ring, count and message all sit on it. The Habits header is the
  reference composition: artwork first, copy above it, no plate.
- Artwork is decorative background: cover-cropped, bottom-anchored, and full
  bleed across the Hero, never a strip inside it.
- Readability is a smooth gradient shaped around the copy, never a blanket
  scrim: the open band between the greeting and the ring stays Aurora.
- The only fades are the Hero's own edges — a short blend into the page
  background above the date, and a blend into the page background below the
  message.
- The artwork is at full strength across the Hero so it still reads as
  Aurora artwork, and body text still clears 4.5:1 in both themes.
- Reduced-motion preferences freeze the zoom and parallax treatment.
8. Hero Scroll Behavior
Decision
The Hero and today's activity content exist within the same vertical
scroll surface.
As the user scrolls upward:
- Hero smoothly collapses.
- Hero content may subtly scale.
- Hero imagery may use subtle parallax.
- Secondary Hero content may fade.
As the user scrolls back toward the top, the Hero expands naturally.
Requirements
- No nested scrolling.
- No hard visual jump between expanded and collapsed states.
- Reduced-motion preferences must be respected.
- Hero behavior must never prevent access to activities.
9. Motivational Copy
Decision
Hero messaging may respond to:
- Time of day.
- Current completion progress.
- Today's remaining work.
Rejected
Purely random inspirational quotes.
Rationale
The message should support the user's current daily state rather than
function as decorative content.
10. Today's Activity Organization
Decision
Home displays all activities that are eligible for today.
They are divided into two sections:
Remaining today
↓
Completed today
Remaining today
Contains all eligible activities that are currently incomplete.
Completed today
Contains all eligible activities that are currently complete.
Completed activities remain visible.
They are visually quieter than incomplete activities but remain fully
accessible.
Rationale
The user should be able to understand both:
- What still needs attention.
- What has already been accomplished.
Completed activities must not disappear from Home because completion
can be reversed.
11. Activity Card
Decision
The Home activity card contains:
- User-selected icon.
- Activity name.
- Optional configured time.
- Completion control.
- Target progress when applicable.
Rejected
Displaying activity type labels such as:
Workout
Habit · Daily
Rationale
Classification is useful internally and on detail screens but adds
unnecessary noise to Home.
12. Completion Interaction
Decision
Completion happens through the dedicated completion control only.
Completion control → Complete / uncomplete
Activity body       → Open details
Rejected
Swipe-to-complete.
Rationale
An explicit completion control is predictable and avoids accidental
completion.
13. Habit Completion Methods
Decision
Momentum supports two established completion methods.
Just Mark it Done
Tapping the completion control immediately completes the activity.
No confirmation dialog is required.
Reach a Target
Tapping the completion control opens a target-entry interaction.
The user enters the actual value.
Completion is determined by:
actual >= target → Complete
actual < target  → Incomplete
Important
Above-target values are valid.
Example:
Target: 8 glasses
Actual: 10 glasses

Result: Complete
Partial values are retained.
Example:
Target: 8 glasses
Actual: 5 glasses

Result: Incomplete
Progress: 5 / 8
14. Target Progress Editing
Decision
Actual target values remain editable.
Changing the actual value recalculates all dependent state.
For example:
5 → 8
changes the activity from incomplete to complete.
Likewise:
8 → 5
changes the activity from complete to incomplete.
Dependent state
The system must recalculate:
- Completion state.
- Daily progress.
- Streak.
- Completion history.
- Target progress.
15. Completion Reversal
Decision
Users can freely complete and uncomplete activities.
The system must handle both directions correctly.
Incomplete → Complete
Complete   → Incomplete
Consequences
Changing completion state must update:
- Hero progress.
- Completion percentage.
- Activity ordering.
- Streak state.
- Relevant history.
- Target progress where applicable.
16. Completion Transition
Decision
When an incomplete activity becomes complete:
Remaining today
        ↓
Completed today
When a completed activity becomes incomplete:
Completed today
        ↓
Remaining today
The transition should be smooth and visually understandable.
The activity must not simply disappear.
17. Completion Animation
Decision
Completion feedback should be subtle and atmospheric.
Sequence may include:
Tap
→ Checkmark/state acknowledgement
→ subtle scale/bounce
→ Twilight glow
→ progress update
→ activity moves to Completed today
Rejected
Large confetti or celebration effects.
Rationale
Momentum should feel rewarding without becoming noisy or overly
gamified.
Accessibility
Animation must not be required to understand the completion state.
Reduced-motion preferences must be respected.
18. Final Completion
Decision
When every eligible activity for today is complete:
- Remaining today is hidden.
- Completed today remains visible.
- Hero progress reaches 100%.
Rationale
The user should clearly understand that today's scheduled work is
complete while still retaining access to completed activities.
Important
Completed activities must remain available so the user can reverse a
completion if necessary.
19. No-Activity State
Decision
If there are no eligible activities scheduled for today, show a
dedicated No-Activity State.
It may contain:
- Twilight illustration.
- Encouraging message.
- Nudge to add an activity.
Important distinction
No activities scheduled
        ≠
All activities completed
These states must have different copy and visual treatment.
20. Quick Add
Decision
Place a compact circular + button at the bottom-right, immediately
above navigation.
Behavior
Tap +
   ↓
Create Item
   ↓
Choose Habit or Task
Rejected
A multi-purpose action menu.
Rationale
The action remains obvious while avoiding unnecessary interaction.
21. Activity List Scrolling
Decision
Home activity content scrolls naturally.
Ordering:
Hero
↓
Remaining today
↓
Completed today
Rules
- Incomplete activities always appear before completed activities.
- All eligible activities must be accessible.
- No artificial visible-item limit.
- No pagination requirement.
- No See All control.
Rationale
The activity list itself is already scrollable.
A separate See All action adds unnecessary navigation.
22. Activity Tap
Decision
Tapping the activity body opens its details.
It does not complete the activity.
Completion
Only the dedicated completion control changes completion state.
Rejected
Navigation chevrons such as:
>
on activity cards.
Rationale
The card should remain visually clean while maintaining a clear
separation between inspecting and completing an activity.
23. Habit Scheduling & Eligibility
Decision
An activity is eligible to appear on Home today only when its scheduling
rules allow it.
Eligibility is based on the device's local calendar date.
For a scheduled habit:
1. Today's date must be within its configured start/end range, when
   applicable.
2. Today's weekday must be configured for the habit.
Example:
Habit X
Monday–Sunday
is eligible every day within its configured date range.
Example:
Habit Y
Monday / Thursday / Saturday
is eligible only on those weekdays within its configured date range.
Important
An unscheduled day:
- Does not create a completion opportunity.
- Does not appear as an incomplete activity.
- Does not break a streak.
24. Streak Calculation
Decision
Streaks are based on successful scheduled completion opportunities.
They are not based on consecutive calendar dates.
Conceptually:
Scheduled opportunity
        ↓
Successfully completed?
        ↓
Yes → Continue streak
No  → Break streak
Unscheduled days are ignored.
Just Mark it Done
Marked complete → Successful opportunity
Reach a Target
actual >= target → Successful opportunity
actual < target  → Unsuccessful opportunity
A scheduled opportunity that is not successfully completed breaks the
streak.
25. Date & Time Boundary
Decision
The device's local calendar date is the source of truth for "today."
Home must not use UTC date boundaries for today's activity eligibility.
At local midnight, the application must reevaluate:
- Today's activity eligibility.
- Daily completion state.
- Daily progress.
- Streaks.
26. Habit Detail
Decision
Habit details include relevant historical insights.
Potential information:
- Current streak.
- Longest streak.
- Completion percentage.
- Completion history.
- Trends.
- Calendar/heatmap history.
- Date filtering.
Rationale
Habits are ongoing behaviors whose value is partly understood through
accumulated history.
27. Task Detail
Decision
Tasks show relevant task details.
Explicitly excluded
- Streak.
- Habit-style trend analytics.
- Habit-style behavioral insights.
Rationale
A task is something to accomplish rather than a repeated behavior that
requires habit analytics.
28. Long Activity Names
Decision
Activity names may occupy a maximum of two visual lines.
Long names are truncated when necessary.
Rationale
The activity list must remain scannable and structurally stable.
29. Missing Time
Decision
If an activity has no configured time, omit the time row.
Rejected
Displaying placeholders such as:
- No time.
- Anytime.
- Unscheduled.
Rationale
Absence of information does not require a UI label.
30. Multiple Same-Time Activities
Decision
Activities with the same configured time are displayed normally.
No special grouping is required.
Rationale
Grouping adds complexity without a demonstrated user benefit.
31. Past-Due Tasks
Decision
Do not silently move yesterday's task into today's Home.
Rationale
Home represents today's scheduled state.
Historical and overdue handling belongs in the broader activity/history
experience.
32. Large Lists
Decision
Home must support naturally scrolling through large numbers of eligible
activities.
Rejected
Artificially limiting the number of activities shown simply to preserve
visual compactness.
Rationale
The user needs access to every eligible activity and each has a direct
completion action.
33. Tap vs Swipe
Decision
No swipe interaction for completion.
Completion control → Complete / uncomplete
Activity body      → Details
Rationale
Two explicit actions are preferable to hidden gesture behavior.
34. Accessibility
Decision
Home must support:
- Screen readers.
- Accessible labels.
- Minimum touch target sizes.
- Dynamic text sizing.
- Sufficient contrast.
- Reduced motion.
The completion control must communicate its current action/state.
Examples:
Complete Workout
Mark Workout incomplete
Target-entry interactions must be accessible.
Completion state must remain understandable without relying on:
- Animation.
- Color alone.
- Visual effects alone.
Decorative imagery should not create unnecessary screen-reader noise.
35. Product Philosophy
The dashboard should not make users manage Momentum.
It should help them:
Look → understand → act → move on.

The dashboard is intentionally:
- Calm.
- Focused.
- Cinematic.
- Contextual.
- Restrained.
It is intentionally not:
- Metric-heavy.
- Calendar-centric.
- Gamified.
- Noisy.
36. Decision Revisit Rules
A finalized decision should only be revisited when one of these occurs:
1. User research shows a meaningful usability problem.
2. Analytics demonstrate a significant behavior issue.
3. Accessibility requirements require a change.
4. Technical constraints make the current design impractical.
5. A broader product decision invalidates the assumption.
When revisiting a decision, record:
- Original decision.
- Evidence prompting reconsideration.
- Alternatives considered.
- New decision.
- Date.
- Impacted documents/components.
This prevents design drift and undocumented reversals.
37. Reach a Target Dialog Surface
Decision
The Reach a Target dialog sits on an opaque Momentum surface
(GlassLevel.DIALOG). The layer behind it is pushed back with a window dim
rather than by darkening the dialog.
Rationale
Translucent Hero glass let the Home Hero artwork and activity rows show
through the dialog, competing with the target value, input, and actions.
Requirements
- The dialog surface fill is opaque (>= 0.98) in both Light and Dark themes.
- The brightest edge of the surface stays opaque as well; highlights are baked
  into the stop instead of painted translucent over the backdrop.
- Title, target, input, and actions keep >= 4.5:1 contrast.
- Only this dialog changes. Other Momentum dialogs keep their current surface
  until they are reviewed individually.