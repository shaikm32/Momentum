# Momentum Home Dashboard --- Product Specification

**Status:** Finalized\
**Feature:** Home / Dashboard\
**Product:** Momentum\
**Design language:** Momentum --- Twilight Journey

------------------------------------------------------------------------

# 1. Overview

The Momentum Home screen is the user's daily command surface.

Its purpose is to answer immediately:

> **How am I doing today, and what still needs my attention?**

Home is strictly focused on the current day.

Historical analytics, habit trends, and broader activity history belong
in Insights or activity-specific detail screens.

------------------------------------------------------------------------

# 2. Goals

## Primary goals

1.  Give the user an immediate understanding of today's completion
    progress.
2.  Show only today's incomplete activities requiring attention.
3.  Make completing an activity fast and satisfying.
4.  Provide a calm, atmospheric experience consistent with Twilight
    Journey.
5.  Make activity creation immediately accessible.
6.  Provide a clear path from an activity to its relevant
    details/history.

## Non-goals

Home should not become:

-   A generic analytics dashboard
-   A calendar
-   A health/wellness metrics dashboard
-   A generic streak tracker
-   A repository of historical information
-   A gamification layer

------------------------------------------------------------------------

# 3. Navigation

Momentum uses four primary destinations:

``` text
Home | Habits | Insights | Settings
```

There is no Calendar tab.

Historical/date-based information is accessed through Insights and
activity-specific history.

------------------------------------------------------------------------

# 4. Home Layout

The intended hierarchy is:

``` text
HOME

SAT, SEP 13
Good morning

┌─────────────────────────────────┐
│                                 │
│     [ 67% ring ]   3 of 5       │
│                    complete      │
│                                 │
│                    "You're      │
│                     off to a    │
│                     good start" │
│                                 │
└─────────────────────────────────┘


Remaining today

🏃  Complete workout              ✓
    7:00 AM

💧  Drink water                   ✓

📖  Read for 20 minutes           ✓


                             (+)

─────────────────────────────────
Home       Habits   Insights   Settings
```

Exact spacing, typography, and component dimensions are
implementation/design-system concerns, but the information hierarchy is
fixed.

------------------------------------------------------------------------

# 5. Header

The header contains only:

1.  Current date
2.  Dynamic greeting

Example:

``` text
SAT, SEP 13
Good morning
```

## Greeting behavior

The greeting changes according to local time.

Examples:

-   Good morning
-   Good afternoon
-   Good evening
-   Good night

The exact time boundaries should be centralized in a time-of-day utility
so the behavior is consistent throughout the app.

No profile/avatar/notification UI is required in the initial Home
specification.

------------------------------------------------------------------------

# 6. Hero Progress Card

The hero is the primary visual component on Home.

## Required information

The hero displays:

-   Completion percentage in the ring
-   X of Y complete
-   Motivational message
-   Current date context via the header/hero treatment

Example:

``` text
[ 67% ring ]     2 of 3 complete

                 A fresh day.
                 Take the first step.
```

## Progress ring

The ring contains **only the percentage**.

Example:

> 67%

The ring should animate when progress changes.

## Completion status

Displayed separately:

> 2 of 3 complete

This avoids overcrowding the ring.

------------------------------------------------------------------------

# 7. Time-of-Day Background

The Home visual background belongs to the Twilight Journey theme.

The scene is the same world throughout the day, with
lighting/environment changing based on local time.

## Time windows

  Local time     Scene
  -------------- ---------------------------
  05:00--07:59   Sunrise
  08:00--16:59   Daylight with visible sun
  17:00--18:29   Sunset
  18:30--04:59   Moon/night

The background should use the user's local time.

## Visual continuity

The mountain composition should remain consistent between time states.

The scenic area should visually support the hero and then blend into the
app's Twilight base color below.

There should be no obvious hard boundary between image and theme
background.

------------------------------------------------------------------------

# 8. Motivational Message

The hero contains a short message that responds to both:

-   Time of day
-   Current completion progress

The message is intended to encourage completion of remaining activities.

It must remain:

-   Short
-   Calm
-   Encouraging
-   Contextual

It should avoid:

-   Excessive positivity
-   Cheesy motivational language
-   Competitive language
-   Rewards/badges
-   Guilt/shame

## Example message states

  -----------------------------------------------------------------------
  Situation                           Example
  ----------------------------------- -----------------------------------
  Morning / low progress              A fresh day. Take the first step.

  Morning / good progress             You're off to a good start. Keep it
                                      going.

  Afternoon / low progress            There's still time to make today
                                      count.

  Afternoon / high progress           You're close. Finish what you
                                      started.

  Evening / low progress              A little more effort can close the
                                      day strong.

  Evening / high progress             Just one more step.

  All complete                        You're all done. Well earned.
  -----------------------------------------------------------------------

The final copy set can be expanded during content design.

------------------------------------------------------------------------

# 9. Remaining Today

This section contains:

> **All incomplete activities scheduled for today.**

Completed activities are removed from this list after the completion
animation.

## Item contents

Each activity displays:

1.  User-selected activity icon
2.  Activity name
3.  Optional configured time
4.  Completion tick action

Example:

``` text
🏃  Complete workout
    7:00 AM                         ✓
```

Without a time:

``` text
📖  Read for 20 minutes            ✓
```

## Activity type labels

Do not display:

-   Habit
-   Task
-   Daily
-   One-time
-   Other classification labels

The Home list is intentionally simple.

------------------------------------------------------------------------

# 10. Activity Completion

Completion occurs only through the tick action.

Swipe-to-complete is not supported.

## Animation sequence

### Phase 1 --- Immediate acknowledgment

Tick changes to a checkmark with a subtle scale/bounce.

### Phase 2 --- Glow

A restrained Twilight-style glow/pulse briefly emanates from the
completion control.

### Phase 3 --- Progress

The hero ring smoothly animates to the new completion percentage.

The completion count updates simultaneously.

Example:

``` text
40% → 60%
2 of 5 → 3 of 5
```

### Phase 4 --- List removal

After approximately 700--1000 ms, the completed item:

-   Fades
-   Moves subtly
-   Collapses
-   Allows remaining items to reposition smoothly

### Phase 5 --- Message update

The motivational message may update according to the new progress state.

------------------------------------------------------------------------

# 11. Final Completion

When the user completes the final pending activity:

1.  Run the normal completion acknowledgment.
2.  Animate progress to 100%.
3.  Update X/Y to Y/Y.
4.  Remove the final activity.
5.  Transform the Remaining Today area into the completion state.
6.  Reveal a Twilight-themed illustration.
7.  Reveal an encouraging completion message.

Example:

``` text
100%
5 of 5 complete

[ Twilight illustration ]

You're all done.

You showed up for yourself today.
```

The hero remains spatially stable.

The lower content transforms rather than navigating away.

------------------------------------------------------------------------

# 12. No-Activity State

If there are no activities scheduled for today, show a dedicated empty
state.

It should include:

-   Twilight-themed illustration
-   Short message
-   Gentle call to action to add an activity

Example:

> Nothing planned for today.\
> A quiet day is a good place to start something.

The `+` action remains available.

This state is distinct from the 100% completed state.

------------------------------------------------------------------------

# 13. Quick Add

The Home screen contains a compact `+` action:

> Bottom-right, immediately above the bottom navigation.

Tapping it navigates to **Create Item**.

Create Item first asks the user to select:

``` text
Habit
Task
```

The creation flow then continues according to the selected type.

The Home `+` does not directly create an item and does not expose a
multi-purpose action menu.

------------------------------------------------------------------------

# 14. See All

The Home screen shows all pending activities unless the list becomes
large.

## Rule

-   1--10 pending activities: show all; hide See all.
-   11+ pending activities: show all; display See all.

The list remains scrollable.

The threshold counts only currently incomplete activities scheduled for
today.

------------------------------------------------------------------------

# 15. Activity Tap

Tapping the activity body opens its activity detail screen.

The activity body must not complete the activity.

Completion is exclusively performed through the tick button.

## Habit detail

A habit's detail screen may contain:

-   Icon/name
-   Schedule
-   Current streak
-   Longest streak
-   Completion percentage
-   Historical completion
-   Weekly/monthly trends
-   Calendar/heatmap history
-   Date range filtering
-   Edit/manage actions

## Task detail

A task's detail screen contains task information, such as:

-   Name
-   Description
-   Due date/time
-   Status
-   Other task-specific metadata
-   Edit/manage actions

Tasks do not show:

-   Streaks
-   Habit trends
-   Habit analytics

------------------------------------------------------------------------

# 16. Edge Case Rules

## 0 activities

Show no-activity state.

## 1 activity

Normal layout.

## 2--10 activities

Show all without See all.

## 11+ activities

Show all with See all.

## Long names

Allow up to two lines; truncate with ellipsis when necessary.

## No configured time

Omit the time row.

## Same-time activities

Display normally without grouping.

## Past-due tasks

Do not silently move prior-day tasks into today's Home.

Past/overdue handling belongs to the relevant activity/history
experience.

## Large lists

Use normal scrolling. Do not introduce pagination unless future evidence
requires it.

------------------------------------------------------------------------

# 17. Accessibility

The implementation should support:

-   Screen readers
-   Adequate touch targets
-   Dynamic text sizing
-   Sufficient contrast
-   Reduced-motion behavior

Animations must never be required to understand whether an activity was
completed.

The tick action must have an accessible label such as:

> Complete Workout

and its completed state should be announced appropriately.

------------------------------------------------------------------------

# 18. State Model

At a conceptual level, Home has these major states:

``` text
LOADING
   ↓
TODAY WITH PENDING ITEMS
   ↓
ITEM COMPLETION
   ↓
UPDATED TODAY
   ↓
ALL COMPLETE
```

Alternative initial state:

``` text
LOADING
   ↓
NO ACTIVITIES TODAY
```

The Home UI should derive its display from today's activity state rather
than maintaining an independent duplicate state.

------------------------------------------------------------------------

# 19. Success Criteria

The Home dashboard is successful if a user can:

1.  Open Momentum and immediately understand today's progress.
2.  Identify every incomplete activity for today.
3.  Complete an activity with one deliberate tap.
4.  See progress update without navigating away.
5.  Understand when the entire day is complete.
6.  Add a new activity quickly.
7.  Open an activity to inspect its relevant details/history.
8.  Avoid being distracted by irrelevant historical metrics.

------------------------------------------------------------------------

# 20. Design Principle

The Home experience should follow:

> **Look → understand → act → move on.**

Home is not where Momentum explains everything.

It is where Momentum helps the user move through today.
