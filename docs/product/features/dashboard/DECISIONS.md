# Momentum Dashboard --- Product Decisions & Decision Log

**Status:** Finalized\
**Scope:** Home / Dashboard\
**Last consolidated:** September 2026

This document records decisions that should be treated as product
constraints unless explicitly revisited.

------------------------------------------------------------------------

# 1. Core Dashboard Principle

### Decision

Home is focused strictly on **today**.

### Rationale

The dashboard must answer:

> "How am I doing today, and what still needs my attention?"

Historical information belongs in Insights or activity-specific history.

### Consequence

Do not add general historical charts, yesterday/tomorrow content, or
unrelated metrics to Home.

------------------------------------------------------------------------

# 2. Primary Navigation

### Decision

Momentum has exactly four primary tabs:

``` text
Home | Habits | Insights | Settings
```

### Rejected

Calendar as a primary tab.

### Rationale

Date/history exploration is a function of Insights and activity history
rather than a top-level destination.

------------------------------------------------------------------------

# 3. Generic Dashboard Metrics

### Decision

Do not show generic dashboard cards for:

-   Overall streak
-   Focus
-   Sleep
-   Wellness
-   Health
-   Water
-   Other metrics without meaningful native data

### Rationale

Metrics should not exist merely to fill dashboard space.

A generic streak is ambiguous because Momentum contains multiple
activities.

------------------------------------------------------------------------

# 4. Hero Progress

### Decision

The hero progress visualization is the primary Home element.

It contains:

-   Percentage inside the ring
-   X of Y complete beside it
-   Short motivational message

### Decision detail

The ring contains only the percentage.

### Rationale

Separating percentage and count creates a cleaner visual hierarchy.

------------------------------------------------------------------------

# 5. Date

### Decision

Show a concise date above the greeting.

Intended hierarchy:

``` text
SAT, SEP 13
Good morning
```

### Rationale

The date provides context without competing with the hero.

------------------------------------------------------------------------

# 6. Dynamic Greeting

### Decision

Greeting changes based on local time.

Examples:

-   Good morning
-   Good afternoon
-   Good evening
-   Good night

### Rationale

The Home screen should feel connected to the user's current moment.

------------------------------------------------------------------------

# 7. Dynamic Background

### Decision

Use the same Twilight Journey visual world throughout the day.

Time changes environmental lighting rather than changing the theme.

### Time mapping

``` text
05:00–08:00    Sunrise
08:00–17:00    Daylight / sun
17:00–18:30    Sunset
18:30–05:00    Moon / night
```

### Important

The mountain composition should remain consistent.

The scenic area should blend into the Twilight base background below the
hero.

------------------------------------------------------------------------

# 8. Motivational Copy

### Decision

Messages are dynamic and depend on:

-   Time of day
-   Current completion progress

### Rejected

Purely random inspirational quotes.

### Rationale

The message should motivate the user toward completing today's remaining
activities rather than functioning as decorative content.

------------------------------------------------------------------------

# 9. Pending List

### Decision

Home displays only today's incomplete activities.

### Activity fields

-   User-selected icon
-   Name
-   Optional time
-   Tick action

### Rejected

Showing activity type labels.

Example rejected UI:

``` text
Workout
Habit · Daily
```

### Rationale

The classification is useful internally and on detail screens but adds
noise to Home.

------------------------------------------------------------------------

# 10. Completion Interaction

### Decision

Completion happens through the tick action only.

### Rejected

Swipe-to-complete.

### Rationale

An explicit completion control is predictable and avoids accidental
completion.

------------------------------------------------------------------------

# 11. Completion Animation

### Decision

Completion should be subtle and atmospheric.

Sequence:

``` text
Tap
→ Checkmark
→ subtle scale/bounce
→ Twilight glow
→ progress ring animation
→ item fades/collapses
```

### Rejected

Large confetti/celebration effects.

### Rationale

Momentum should feel rewarding without becoming gamified or visually
noisy.

------------------------------------------------------------------------

# 12. Final Completion

### Decision

Completing the final pending item transitions Home into a dedicated "all
complete" state.

### Required elements

-   100% progress
-   Y of Y complete
-   Twilight-themed illustration
-   Beautiful completion message

### Example

> You're all done.\
> You showed up for yourself today.

### Rationale

The final completion represents the emotional payoff of the day's
actions and deserves more acknowledgement than a normal item completion.

------------------------------------------------------------------------

# 13. No-Activity State

### Decision

If there are no activities scheduled for today, show:

-   Twilight illustration
-   Encouraging message
-   Nudge to add an activity

### Important distinction

No activities scheduled ≠ all activities completed.

These states must have different copy and visual treatment.

------------------------------------------------------------------------

# 14. Quick Add

### Decision

Place a compact `+` button at the bottom-right, immediately above
navigation.

### Behavior

``` text
Tap +
   ↓
Create Item
   ↓
Choose Habit or Task
```

### Rationale

The action remains obvious while avoiding a large dashboard button.

------------------------------------------------------------------------

# 15. See All

### Decision

"See all" appears only when there are more than 10 pending activities.

``` text
1–10 pending     → no See all
11+ pending      → show See all
```

### Important

All pending activities are still available in the scrollable list.

### Rationale

"See all" is unnecessary when the list is already short.

------------------------------------------------------------------------

# 16. Activity Tap

### Decision

Tapping an activity opens its details.

It does not complete it.

### Completion

Only the tick completes the activity.

### Rationale

Separating "inspect" and "complete" prevents accidental completion and
creates a clear interaction model.

------------------------------------------------------------------------

# 17. Habit Detail

### Decision

Habit details include relevant historical insights.

Potential information:

-   Current streak
-   Longest streak
-   Completion percentage
-   Completion history
-   Trends
-   Calendar/heatmap history
-   Date filtering

### Rationale

Habits are ongoing behaviors whose value is partly understood through
accumulated history.

------------------------------------------------------------------------

# 18. Task Detail

### Decision

Tasks show details only.

### Explicitly excluded

-   Streak
-   Trend analytics
-   Habit-style insights

### Rationale

A task is something to accomplish, not a repeated behavior that needs
behavioral analytics.

------------------------------------------------------------------------

# 19. Long Activity Names

### Decision

Allow a maximum of two visual lines and truncate when necessary.

### Rationale

The activity list must remain scannable and structurally stable.

------------------------------------------------------------------------

# 20. Missing Time

### Decision

If an activity has no configured time, omit the time row.

### Rejected

Displaying placeholders such as:

-   No time
-   Anytime
-   Unscheduled

### Rationale

Absence of information does not need a UI label.

------------------------------------------------------------------------

# 21. Multiple Same-Time Activities

### Decision

Display activities normally without special grouping.

### Rationale

Grouping adds complexity without a demonstrated user benefit.

------------------------------------------------------------------------

# 22. Past-Due Tasks

### Decision

Do not silently move yesterday's task into today's Home.

### Rationale

Home represents today's scheduled state. Historical/overdue handling
belongs in the broader activity/history experience.

------------------------------------------------------------------------

# 23. Large Lists

### Decision

Allow Home to scroll naturally.

### Rejected

Artificially limiting the dashboard to a fixed number of items simply to
preserve visual compactness.

### Rationale

The user needs access to every pending activity and each has a direct
completion action.

------------------------------------------------------------------------

# 24. Tap vs Swipe

### Decision

No swipe interaction.

``` text
Tick     → Complete
Activity → Details
```

### Rationale

Two clear actions are preferable to hidden gesture behavior.

------------------------------------------------------------------------

# 25. Product Philosophy

The dashboard should not make users manage Momentum.

It should help them:

> **Look → understand → act → move on.**

The dashboard is intentionally:

-   Calm
-   Focused
-   Cinematic
-   Contextual
-   Restrained

It is intentionally not:

-   Metric-heavy
-   Calendar-centric
-   Gamified
-   Noisy

------------------------------------------------------------------------

# 26. Decision Revisit Rules

A finalized decision should only be revisited when one of these occurs:

1.  User research shows a meaningful usability problem.
2.  Analytics demonstrate a significant behavior issue.
3.  Accessibility requirements require a change.
4.  Technical constraints make the current design impractical.
5.  A broader product decision invalidates the assumption.

When revisiting a decision, record:

-   Original decision
-   Evidence prompting reconsideration
-   Alternatives considered
-   New decision
-   Date
-   Impacted documents/components

This prevents design drift and undocumented reversals.
