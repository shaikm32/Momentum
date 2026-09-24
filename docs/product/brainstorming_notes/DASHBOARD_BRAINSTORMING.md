# Momentum Dashboard --- Brainstorming Notes

## Purpose

These notes capture the reasoning, alternatives, challenges, and
evolution of the Momentum Home/Dashboard concept.

They are intentionally more narrative than the product specification.
The goal is to preserve **why** decisions were made, not only **what**
was decided.

------------------------------------------------------------------------

## 1. Original Product Question

The dashboard should answer one question immediately:

> **How am I doing today, and what still needs my attention?**

This led to a core principle:

> **Home is Today.**

The dashboard should not become a general analytics screen, calendar, or
productivity command center.

------------------------------------------------------------------------

## 2. Information Architecture Exploration

### Initial possibilities considered

We considered showing various dashboard metrics such as:

-   Overall streak
-   Focus
-   Sleep
-   Wellness
-   Water
-   Health
-   Other manually entered metrics
-   Historical information
-   Calendar information

### Problem identified

These metrics introduce cognitive load and, in several cases, require
data that Momentum does not naturally collect.

An overall streak also creates ambiguity:

> "Streak of what?"

Momentum contains multiple activities, so a generic streak does not
necessarily communicate anything meaningful.

### Result

The dashboard was reduced to three priorities:

1.  **Progress** --- how much of today's work is complete
2.  **Action** --- what still needs attention
3.  **Escape hatch** --- quick add and broader activity access

Historical information belongs elsewhere.

------------------------------------------------------------------------

## 3. Dashboard Mental Model

The four primary areas evolved into:

### Home

> What needs my attention today?

### Habits

> What am I building or removing over time?

### Insights

> How am I doing over time?

### Settings

> How does Momentum work for me?

This gives every primary navigation destination a distinct
responsibility.

------------------------------------------------------------------------

## 4. Hero Progress Concept

The progress hero became the primary visual element.

The original concept was a progress ring with completion information and
a motivational quote.

Several compositions were considered:

-   Date above the ring
-   Date inside/around the ring
-   Progress centered with text underneath
-   Progress ring beside completion count and message

The chosen composition is:

``` text
Date
Greeting

┌─────────────────────────────┐
│                             │
│   [ % ring ]   X of Y       │
│                complete      │
│                             │
│                motivational  │
│                message       │
└─────────────────────────────┘
```

### Important refinement

The ring contains **only the percentage**.

The detailed completion status is placed beside it:

> `3 of 5 complete`

This keeps the ring visually clean.

------------------------------------------------------------------------

## 5. Date and Greeting

The date was initially considered as part of the progress area.

The final hierarchy is:

``` text
SAT, SEP 13
Good morning

[Hero progress card]
```

The date is displayed first, with the greeting below it.

The date should remain concise rather than using a verbose format such
as:

> Sunday, September 13, 2026

The exact rendered format can follow the app's localization strategy,
but the intended visual treatment is short.

------------------------------------------------------------------------

## 6. Dynamic Time-of-Day Experience

The visual background is not a separate theme for different times.

Momentum remains within the same **Twilight Journey** visual identity.

Only the environmental lighting changes.

### Time mapping

  Local time           Background
  -------------------- -----------------------------
  5:00 AM -- 8:00 AM   Sunrise
  8:00 AM -- 5:00 PM   Full daylight / visible sun
  5:00 PM -- 6:30 PM   Sunset
  6:30 PM -- 5:00 AM   Moon / night

The mountain composition should remain visually consistent across
states.

This creates the feeling that the same world is progressing through the
day with the user.

------------------------------------------------------------------------

## 7. Background Blending

A key visual concern was avoiding a hard boundary between:

-   Scenic mountain background
-   Lower Twilight-themed dashboard area

The scenic background should extend only through the hero area and then
gradually blend into the application's base theme.

The transition must feel intentional and seamless rather than like two
separate images stacked vertically.

------------------------------------------------------------------------

## 8. Dynamic Motivational Message

The motivational message should not be a random inspirational quote.

It should respond to:

1.  Time of day
2.  Current completion progress

Examples:

### Morning / early progress

> A fresh day. Take the first step.

### Morning / already progressing

> You're off to a good start. Keep it going.

### Afternoon / several items remaining

> There's still time to make today count.

### Afternoon / nearly complete

> You're close. Finish what you started.

### Evening / several items remaining

> A little more effort can close the day strong.

### Evening / nearly complete

> Just one more step.

### All complete

> You're all done. Well earned.

The copy should remain calm and encouraging rather than cheesy or
gamified.

------------------------------------------------------------------------

## 9. Pending Items

The section below the hero is the user's action area.

The list contains:

> **Only incomplete activities for today.**

Completed activities leave the pending list after the completion
interaction.

Each item contains:

-   User-selected icon
-   Activity name
-   Optional time
-   Completion action

Example:

``` text
🏃  Complete workout             ✓
    7:00 AM

📖  Read for 20 minutes          ✓
```

If no time was configured, the time line is omitted.

------------------------------------------------------------------------

## 10. Habit vs Task Visibility

We explicitly decided **not** to display "Habit" or "Task" labels in the
Home list.

The user should see the activity, not its database classification.

For example:

``` text
🏃 Complete workout
```

rather than:

``` text
🏃 Complete workout
Habit · Daily
```

The distinction remains important internally and in relevant detail
screens, but it does not need to add visual noise to Home.

------------------------------------------------------------------------

## 11. Completion Interaction

Swipe gestures were rejected.

Completion happens through an explicit tick action.

The intended interaction sequence is:

### Step 1 --- Tap

User taps the tick.

### Step 2 --- Acknowledge

The tick changes from an empty state to a checkmark.

A subtle scale/bounce and checkmark-drawing animation provide immediate
feedback.

### Step 3 --- Twilight glow

A restrained glow/pulse emanates from the completion control.

No excessive confetti or gamification.

### Step 4 --- Progress update

The progress ring smoothly animates to the new percentage.

The completion count updates:

> `2 of 5` → `3 of 5`

### Step 5 --- Activity exits

After approximately 0.7--1 second, the completed item fades and
collapses out of the pending list.

Remaining items naturally move into place.

### Step 6 --- Message can update

The motivational message may change to reflect the user's new progress
state.

------------------------------------------------------------------------

## 12. Final Completion State

The final pending item is handled differently.

Sequence:

``` text
Last item tapped
      ↓
Completion animation
      ↓
Progress ring reaches 100%
      ↓
X of Y becomes Y of Y
      ↓
Pending list transforms
      ↓
Twilight illustration appears
      ↓
Completion message appears
```

The hero itself should remain spatially stable.

The content below it transforms rather than the entire screen being
replaced.

The final state communicates accomplishment without becoming a game
reward.

Example:

> **You're all done.**\
> You showed up for yourself today.

The illustration should fit the Twilight Journey world, such as a
mountain/path/soft light motif.

------------------------------------------------------------------------

## 13. No-Activity State

This is different from "everything completed."

### No activities scheduled today

Show:

-   Twilight illustration
-   Gentle message
-   Clear nudge to add an activity

Example:

> Nothing planned for today.\
> A quiet day is a good place to start something.

The `+` action remains the obvious next step.

------------------------------------------------------------------------

## 14. Quick Add

The dashboard has a compact `+` button positioned:

> **Bottom-right, immediately above the bottom navigation.**

Tapping it takes the user to the Create Item screen.

The Create Item flow first asks the user to choose:

-   Habit
-   Task

The `+` always means "create something"; it should not open an ambiguous
collection of unrelated actions.

------------------------------------------------------------------------

## 15. See All

The Home list normally shows all pending activities.

A "See all" action is only useful when the list becomes large.

Final rule:

-   1--10 pending items → show all; do not show "See all"
-   11+ pending items → show all pending items and show "See all"

The list remains scrollable.

The threshold is based on **pending items**, not total
historical/completed activities.

------------------------------------------------------------------------

## 16. Activity Tap

The activity body is tappable, but it does not complete the activity.

The tick action completes it.

Tapping the activity opens its details.

### Habit

Habit detail includes:

-   Habit details
-   Current streak
-   Longest streak
-   Completion percentage
-   Completion history
-   Trends / historical insights
-   Relevant habit analytics

### Task

Task detail includes details only.

It should not expose:

-   Streaks
-   Trends
-   Habit-style analytics

This follows the conceptual distinction:

> Habits accumulate behavioral history.\
> Tasks are things to accomplish.

------------------------------------------------------------------------

## 17. Interaction Model

The final Home interaction model is deliberately simple:

  Interaction               Result
  ------------------------- ------------------------------------
  Tap tick                  Complete activity
  Tap activity              Open activity details
  Swipe activity            Not supported
  Tap See all               Open broader activity/history view
  Tap +                     Create Item
  Complete final activity   Show 100% completion state

------------------------------------------------------------------------

## 18. Edge Cases

### 0 activities

Show the no-activity state.

### 1 activity

Use the normal list.

### 2--10 activities

Show all; no "See all."

### 11+ activities

Show all in a scrollable list and provide "See all."

### Long activity name

Limit visual disruption. Use a maximum of two lines with
truncation/ellipsis where necessary.

### Activity without time

Do not show a placeholder such as "No time" or "Anytime."

### Multiple activities at the same time

Do not introduce grouping initially. Display them as normal activities.

### Past-due task

Do not silently move yesterday's task into today's Home.
Historical/overdue handling belongs in the appropriate activity/history
experience.

### Very large number of activities

Allow natural scrolling. Avoid premature pagination or complicated
grouping.

------------------------------------------------------------------------

## 19. Product Philosophy Emerging from the Brainstorm

The dashboard should not make the user manage Momentum.

It should let them:

> **Look → understand → act → move on.**

The design should feel:

-   Calm
-   Cinematic
-   Atmospheric
-   Restrained
-   Useful
-   Encouraging

Not:

-   Gamified
-   Metric-heavy
-   Noisy
-   Overly productive-looking
-   Calendar-centric

------------------------------------------------------------------------

## 20. Final Home Mental Model

> **Home tells me what to do today.**\
> **Habits tells me what I'm building.**\
> **Insights tells me how I'm doing.**\
> **Settings lets me control the experience.**
