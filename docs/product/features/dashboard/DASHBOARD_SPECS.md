
# Momentum Home Dashboard — Product Specification

**Status:** Finalized
**Feature:** Home / Dashboard
**Product:** Momentum
**Design language:** Momentum — Twilight Journey

------------------------------------------------------------------------

## 1. Overview

The Home screen is the primary daily surface of Momentum.

Its purpose is to answer three questions immediately:

1. How am I doing today?
2. What still needs to be done today?
3. What have I already completed today?

Home is focused strictly on the current day.

Historical progress, long-term trends, analytics, and other metrics belong to Insights and are not displayed on Home.

---

# 2. Goals

The Home screen must allow the user to:

- Understand today's overall progress immediately.
- See every activity that is still incomplete today.
- See activities already completed today.
- Complete an activity quickly.
- Enter actual progress for target-based activities.
- Reverse a completion when necessary.
- Understand the difference between pending and completed activities.
- Add a new activity quickly.
- Open an activity's details.
- See progress update immediately after completion.
- Understand when the entire day is complete.
- Experience a calm, visually engaging interface that changes naturally throughout the day.

Home should feel like a daily command center, not an analytics dashboard.

---

# 3. Navigation

Momentum uses four primary destinations:

1. Home
2. Habits
3. Insights
4. Settings

Home is the default destination when the application is opened.

The bottom navigation remains visible while navigating within the Home surface unless a destination explicitly requires otherwise.

---

# 4. Home Layout

The Home screen is a single vertically scrollable content surface.

The overall structure is:

```text
Header
↓
Hero
↓
Remaining today
↓
Completed today
Quick Add is positioned as a floating action button above the bottom navigation.
There is no nested scrolling container for the activity list.
The Hero and activity content belong to the same vertical scrolling surface.

# 5. Header

The Home header contains:
- Current date.
- Contextual greeting.

The date is based on the device's local calendar date.
The greeting may change according to the current time of day.
The header should remain visually lightweight and should not compete with the Hero.

# 6. Home Hero
The Hero is the primary visual element of Home.
It communicates:
- Today's overall progress.
- A short contextual or motivational message.
- The current time-of-day atmosphere.

The Hero should occupy approximately 35–40% of the initial screen height.
The Hero uses a soft aurora-inspired visual system combined with time-of-day environmental imagery.
The visual treatment must support both Light and Dark themes.

6.1 Time-of-Day Hero States
The Hero changes according to the device's local time.
Dawn — 05:00–07:59
Visual direction:
- Soft dawn aurora.
- Subtle warm horizon glow.
- Gentle early-morning atmosphere.
Day — 08:00–16:59
Visual direction:
- Brighter daytime aurora.
- Airy sky.
- Gentle sunlight.
- Clear and energetic atmosphere.
Sunset — 17:00–18:29
Visual direction:
- Warm sunset aurora.
- Richer purple/orange transition.
- Calm evening atmosphere.
Night — 18:30–04:59
Visual direction:
- Deep night aurora.
- Stars.
- Moon.
- Soft luminous aurora.

All four states should feel like members of the same visual family.
The composition, hierarchy, and visual language should remain consistent while the atmosphere changes.

7. Hero Progress

The Hero contains a centered circular progress indicator.
The progress represents today's completed eligible activities.
Conceptually:
completed eligible activities
----------------------------
total eligible activities
If there are no eligible activities for today, the Hero does not present a misleading progress percentage.
If all eligible activities are complete:
100%
The progress indicator updates immediately when an activity's completion state changes.
For target-based activities, an activity counts as completed only when:
actual >= target
For "Just Mark it Done" activities, the activity counts as completed when the user marks it complete.

8. Hero Scroll Behavior

The Hero is part of the same vertical scroll surface as the activity content.
As the user scrolls upward:
- The Hero smoothly collapses.
- Hero content may scale slightly.
- Hero imagery may use subtle parallax.
- Secondary Hero content may fade appropriately.
The transition must feel continuous rather than like a hard layout switch.
As the user scrolls back toward the top, the Hero expands naturally.
Animations should remain subtle.
Reduced-motion preferences must be respected.
The Hero must never interfere with access to the complete activity list.

9. Today's Activities

Today's activities are divided into two sections.
Remaining today
This section contains:
All eligible activities scheduled for today that are currently incomplete.

These activities appear first.
They are the primary action surface on Home.
Each activity remains visible until the user successfully completes it.
For target-based activities, an activity remains in this section when:
actual < target
even if a partial value has already been entered.
Completed today
This section contains:
All eligible activities scheduled for today that are currently completed.

Completed activities appear after all remaining activities.
Completed activity cards are visually quieter than incomplete activity cards.
They must remain fully accessible because the user can reverse a completion.
A completed activity must not disappear from Home.
Section Visibility
Do not display an empty section.
Examples:
Some incomplete + some complete
Remaining today
[Incomplete activity]
[Incomplete activity]

Completed today
[Completed activity]
[Completed activity]
Everything complete
Completed today
[Completed activity]
[Completed activity]
[Completed activity]
The Remaining today section is hidden.
The Hero shows 100% progress.
Nothing scheduled today
Display the No-Activity State instead of empty activity sections.

10. Activity Completion

Completion behavior depends on the activity's configured completion method.

10.1 Just Mark it Done

The user taps the completion control.
The activity is immediately marked complete.
The system must:
1. Persist today's completion.
2. Update Home progress.
3. Update the activity's streak.
4. Update completion history.
5. Move the activity from Remaining today to Completed today.
No confirmation dialog is required.

10.2 Reach a Target

The user taps the completion control.
An interactive target-entry dialog is displayed.
The dialog shows:
- Target value.
- Unit.
- Input for the user's actual value.
The user enters the actual value.
Completion is determined by:
actual >= target → Completed
actual < target  → Incomplete
Above-target values are valid.
For example:
Target: 8 glasses
Actual: 10 glasses

Result: Completed
Partial progress is retained.
For example:
Target: 8 glasses
Actual: 5 glasses

Result: Incomplete
Progress: 5 / 8
The actual value must remain editable.
Changing the actual value recalculates:
- Completion state.
- Daily progress.
- Streak state.
- Relevant completion history.
- Target progress.
If the user changes:
5 → 8
the activity becomes complete.
If the user changes:
8 → 5
the activity becomes incomplete again.

11. Completion Animation

When an incomplete activity is successfully completed:
1. Completion control acknowledges the action.
2. A checkmark/state change is shown.
3. A subtle glow or emphasis may appear.
4. Hero progress updates.
5. The activity smoothly transitions from Remaining today to Completed today.
The transition should feel like the activity has moved between sections rather than disappearing.
The animation must not be required to understand the new state.
For reduced-motion users, the same state transition must remain clear without relying on animation.

12. Final Completion

When the final remaining activity is completed:
- Remaining today disappears.
- Completed today remains visible.
- Hero progress reaches 100%.
- The Home screen communicates that today's activities are complete.
The completed activities remain accessible.
The final-completion state must not hide or remove the activity list.

13. No-Activity State

If there are no eligible activities scheduled for today:
- Do not display empty activity sections.
- Display a dedicated no-activity state.
The state should communicate that there is nothing scheduled for today.
It should remain visually consistent with Momentum's design language.
Quick Add remains available.

14. Quick Add

Quick Add is represented by a circular + floating action button.
Position:
Bottom-right
Immediately above bottom navigation
Tapping Quick Add opens Create Item.
Create Item then asks the user whether they want to create:
- Habit
- Task
Quick Add does not open a multi-purpose action menu.

15. Activity List Scrolling

The Home activity content is naturally scrollable.
Ordering:
Hero
↓
Remaining today
↓
Completed today
Incomplete activities always appear before completed activities.
All eligible activities must be accessible through normal scrolling.
There is no artificial visible-item limit.
There is no pagination requirement.
There is no See All control.
Activity content scrolls as part of the same Home content surface as the Hero.
The Hero collapses naturally as the user scrolls upward.

16. Activity Tap

Tapping the activity body opens the activity's detail screen.
The activity body itself does not complete or uncomplete the activity.
The completion control is the dedicated action for changing completion state.
Activity cards must not display > chevrons or other navigation symbols.
The card itself communicates that it is an activity without requiring a navigation indicator.

17. Habit Scheduling & Eligibility

An activity is eligible to appear on Home today only when all applicable scheduling conditions are satisfied.
Eligibility is based on the device's local calendar date.
For a scheduled habit:
1. Today's date must be within the configured start/end date range, when such a range exists.
2. Today's weekday must be one of the configured weekdays.
Example:
Habit X
Monday–Sunday
Start: January 1
End: December 31
Habit X is eligible every day within the configured date range.
Example:
Habit Y
Monday / Thursday / Saturday
Habit Y is eligible only on those weekdays within its configured date range.
An unscheduled day:
- Does not create a completion opportunity.
- Does not appear as an incomplete activity.
- Does not break a streak.

18. Streak Calculation

Streaks are based on successful scheduled completion opportunities.
A streak does not require completion on every calendar day.
Instead:
Scheduled opportunity
        ↓
Successfully completed?
        ↓
Yes → Continue streak
No  → Break streak
Unscheduled days are ignored.
They do not break the streak.
For Just Mark it Done:
Marked complete → Successful opportunity
For Reach a Target:
actual >= target → Successful opportunity
actual < target  → Unsuccessful opportunity
A scheduled opportunity that is not successfully completed breaks the streak.
Streak calculations must use the same scheduling rules as activity eligibility.

19. Date & Time Boundary

The device's local calendar date is the source of truth for "today."
Home must not use UTC date boundaries for determining today's activities.
At local midnight, the application should transition to the new calendar date and reevaluate:
- Activity eligibility.
- Completion state for the new day.
- Daily progress.
- Streaks.
- Hero time-of-day state.
The time-of-day Hero state is also based on the device's local time.

20. Edge Case Rules

Completed activities
Completed activities remain visible under Completed today.
They are not removed from Home entirely.
Completion transition
When an incomplete activity is completed:
Remaining today
        ↓
Completed today
When a completed activity is marked incomplete:
Completed today
        ↓
Remaining today
The ordering must update immediately.
Empty sections
Do not display empty Remaining today or Completed today sections.
If all eligible activities are completed:
- Hide Remaining today.
- Show Completed today.
- Progress displays 100%.
If no eligible activities exist:
- Display the No-Activity State.
Partial target progress
A target-based activity may have partial progress without being complete.
Example:
Target: 10 km
Actual: 6 km
The activity remains incomplete and stays under Remaining today.
The actual value must be retained.
Above-target progress
Actual values may exceed the target.
Example:
Target: 30 minutes
Actual: 45 minutes
The activity is complete.
The system must not clamp the actual value to the target.
Completion reversal
The user may freely reverse completion.
The system must recalculate all dependent state.
This includes:
- Daily progress.
- Completion percentage.
- Streak.
- Completion history.
- Target progress where applicable.
Long activity names
Long names must remain readable.
Text should wrap naturally rather than being unnecessarily truncated.
The completion control must remain accessible.
Missing configured time
If an activity does not have a configured time, the activity card must still render correctly without reserving unnecessary space for a missing time.
Multiple activities with the same time
Activities with identical configured times must remain independently identifiable and actionable.
Past-due tasks
Past-due tasks must remain distinguishable through their task state without breaking the Home layout.
Large activity lists
Home must remain usable when many activities are eligible for the current day.
The activity list must remain naturally scrollable.
No artificial visible-item limit should be introduced.

21. Accessibility

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
Target-entry dialogs must be accessible.
The completion state must be understandable without relying on animation, color, or visual effects alone.
Icons must have appropriate accessibility descriptions when they communicate meaning.
Decorative imagery should not create unnecessary screen-reader noise.

22. State Model

The Home screen should derive its UI from the current activity state rather than maintaining separate duplicated UI state.
Conceptually:
Activities
    ↓
Eligibility for today
    ↓
Completion state
    ↓
Today's activity groups
    ├── Remaining today
    └── Completed today
    ↓
Hero progress
The Home screen must react immediately to changes in:
- Completion state.
- Actual target values.
- Scheduling eligibility.
- Current date.
- Current local time.
The source of truth remains the underlying persisted activity/completion data.
UI state must not become the authoritative source of completion.

23. Success Criteria

Phase 2C Home is successful when a user can:
1. Open Momentum and immediately understand today's progress.
2. Identify every incomplete activity scheduled for today.
3. Clearly distinguish remaining activities from completed activities.
4. Complete a Just Mark it Done activity with one tap.
5. Enter an actual value for a Reach a Target activity.
6. Understand whether the entered target was successfully reached.
7. See partial target progress when the target has not been reached.
8. Change an activity's completion state.
9. See a completed activity move from Remaining today to Completed today.
10. See an uncompleted activity move from Completed today back to Remaining today.
11. See Hero progress update immediately.
12. Understand when all eligible activities for today are complete.
13. Add a new activity using Quick Add.
14. Open an activity's details.
15. Scroll naturally through all today's activities.
16. Experience the Hero expanding and collapsing smoothly with scrolling.
17. See the Hero atmosphere change according to local time.
18. Use Home comfortably with Light or Dark theme.
19. Use Home with accessibility features enabled.
20. Avoid seeing historical or unrelated analytics on Home.

24. Design Principle

Home should answer:
"What do I need to do today, and how am I doing?"

It should not attempt to answer:
"How have I performed over the last month?"

Daily execution belongs on Home.
Historical analysis belongs in Insights.
The interface should remain focused, calm, and actionable while still giving the user complete visibility into today's activities.