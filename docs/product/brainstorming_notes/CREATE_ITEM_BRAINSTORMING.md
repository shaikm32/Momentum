# Create Item — Brainstorming Notes

> This document captures product exploration, alternatives, rejected ideas, edge cases, and reasoning behind the final Create Item decisions.
>
> It is intentionally less formal than the production specification.

---

## 1. Goal

Create Item is the entry point for adding something to Momentum.

The user should be able to quickly decide:

- Am I building/reducing something consistently?
- Or do I simply need to get something done?

This led to the fundamental split:

**Habit vs Task**

---

# 2. Habit vs Task

## Initial distinction

### Habit

Something the user is trying to make consistent.

Examples:

- Exercise regularly
- Read every day
- Meditate
- Drink enough water

### Task

Something the user wants to get done.

Examples:

- Submit report
- Pay a bill
- Book an appointment
- Buy something

Core principle:

> Habit = behavioral commitment  
> Task = outcome/action to complete

---

## Why the distinction matters

Habits need concepts such as:

- streaks
- completion history
- recurring schedules
- progress toward habit goals
- habit insights

Tasks instead need:

- completion
- due dates
- due times
- reminders
- one-time or recurring occurrences

We explicitly decided not to make Tasks inherit Habit concepts merely because the underlying implementation could support them.

---

# 3. Habit Templates

## Idea

Provide predefined habits so users don't have to configure everything from scratch.

Initial proposed templates:

1. Sleep
2. Drink Water
3. Exercise
4. Read
5. Meditate

Plus:

**Create custom habit**

---

## Important terminology discussion

We initially referred to these predefined habits as "habits."

This created a conceptual problem.

A predefined "Exercise" item is not fundamentally a different kind of domain object from a custom "Practice Guitar" habit.

Therefore:

> Habit = actual user-created entity  
> Habit Template = starting configuration

Templates should pre-fill sensible values and provide an optimized setup flow.

After creation, the resulting item is simply a normal Habit.

---

## Rejected approach

Do not create separate domain entities such as:

- SleepHabit
- WaterHabit
- ExerciseHabit
- ReadingHabit
- MeditationHabit

These would make the product and architecture unnecessarily specialized.

Instead:

**Template → configuration → normal Habit**

---

# 4. Custom Habit

## Initial concern

A completely open-ended custom habit could become a giant configuration form.

We wanted flexibility without forcing users to understand internal concepts such as:

- measurement types
- goal semantics
- recurrence models
- tracking modes

---

## Basic custom habit information

Final decision:

- Name — required
- Icon — required
- Notes — optional

There is intentionally only one free-text field.

### Notes

Notes are free-form.

Users can use them as:

- descriptions
- instructions
- reminders
- context
- personal information

Example:

> Practice chord transitions and scales.

We rejected having both "Description" and "Notes."

---

# 5. Build vs Reduce/Avoid

We wanted custom habits to support both:

### Build

> Something I want to do consistently.

Examples:

- Read
- Practice guitar
- Exercise

### Reduce / Avoid

> Something I want to reduce or avoid.

Examples:

- Limit social media
- Avoid junk food
- Stay smoke-free

This led to the two-direction model:

**Build**
- Binary
- Target

**Reduce/Avoid**
- Avoid completely
- Limit

---

## Why avoidance is different

We rejected the idea that an avoidance habit should simply require the user to tap:

> "I successfully avoided this today."

That creates unnecessary interaction.

For example:

**Stay smoke-free**

should not require daily confirmation merely to record success.

The system should understand that the desired state is zero occurrences.

---

# 6. Build Tracking

Two tracking modes were considered sufficient.

## Binary

The user only cares whether they did it.

Example:

> Meditate → Complete

No measurement is required.

## Target

The user wants to reach a measurable amount.

Example:

> Read → 30 minutes

Potential units discussed:

- Minutes
- Hours
- Pages
- Times
- Glasses
- Litres
- Steps

---

## Rejected for initial release

Custom arbitrary units.

Reason:

They appear flexible but introduce questions around:

- analytics
- comparisons
- aggregation
- formatting
- validation
- meaningful progress calculations

This can be revisited if actual user behavior demonstrates a need.

---

# 7. Reduce/Avoid Tracking

Two modes were considered sufficient.

## Avoid completely

Goal:

> Remain at zero.

Example:

> Stay smoke-free

The user does not need to explicitly log every successful day.

## Limit

Goal:

> Stay at or below a defined amount.

Example:

> Social media ≤ 60 minutes/day

This is different from a Build target because the desired state is **at or below** the threshold.

---

# 8. Progressive Disclosure

A major UX principle emerged during brainstorming.

Do not show every configuration field immediately.

Instead, reveal options based on previous choices.

Example:

If the user selects:

**Build → Just complete it**

there is no reason to show:

- target
- unit
- measurement controls

If the user selects:

**Build → Reach a target**

then target configuration becomes relevant.

Similarly, recurrence options should not overwhelm the initial screen.

---

# 9. Predefined Habit Details

## Sleep

Sleep required the most discussion because it crosses midnight.

Example:

Sleep at 11 PM → wake at 7 AM.

A normal calendar-day completion model can become confusing because the behavior starts on one day and finishes on another.

### Proposed Sleep goals

A Sleep habit can have:

- Sleep by target time
- Minimum sleep duration
- Both

Example:

> Sleep by 10:00 PM  
> Sleep for at least 8 hours

### Important distinction

Completion/recording is not the same as goal success.

Example:

Actual:

> 11:15 PM → 7:30 AM

The system should still record the sleep.

The bedtime goal may be missed while the duration goal may be satisfied.

### Explicitly rejected

Sleep v1 is not a sleep tracker.

Out of scope:

- sleep quality
- sleep stages
- night awakenings
- naps
- sleep consistency scores
- bedtime ranges
- wake-up ranges
- weekday/weekend-specific goals

---

# 10. Drink Water

Water was identified as a natural quantity-based habit.

### Presets discussed

- 6 glasses
- 8 glasses
- 10 glasses
- 12 glasses
- Custom

The user may use supported units such as:

- glasses
- ml
- litres

The presets are convenience choices, not medical recommendations.

### Progress

Example:

> 5 / 8 glasses

Water is accumulated throughout the day.

The Home completion control should remain simple.

Detailed progress can be handled through the habit detail/progress interaction.

---

# 11. Exercise

Exercise initially raised an important modeling question.

## First idea

Allow one Exercise habit to contain multiple activities:

- Walking
- Running
- Cycling
- Yoga

Then, on completion, ask which activity was performed.

### Problem discovered

This effectively turns one habit into a container for multiple independent commitments.

For example:

> Morning Run  
> Evening Walk

would become one Exercise habit even though they may have different:

- schedules
- goals
- streaks
- histories

That was considered undesirable.

---

## Final Exercise model

One Exercise Habit = one exercise activity.

Initial presets:

- Walking
- Running
- Cycling
- Yoga
- Workout
- Custom

Example:

> Morning Run  
> Running  
> 30 minutes  
> Mon/Wed/Fri

Another habit can be:

> Evening Walk  
> Walking  
> 20 minutes  
> Every day

These remain independent Habits.

### Exercise logging

Duration/time logging can be optional.

Fast path:

> Complete → done

Detailed path:

> Complete → optionally log timing/duration → done

The user should never be forced to log timing.

---

## Rejected Exercise scope

Momentum should not become a workout tracker.

Out of scope:

- calories burned
- workout plans
- sets/reps
- detailed workout sessions
- fitness metrics

Momentum tracks consistency, not the entire exercise activity.

---

# 12. Read

Reading was modeled as a measurable Habit.

Possible targets:

- Time
- Pages

Examples:

> Read 30 minutes/day

or

> Read 10 pages/day

Momentum tracks the reading habit, not the user's books.

### Rejected scope

- book database
- ISBN lookup
- chapters
- authors
- reading library
- Goodreads-like functionality

---

# 13. Meditate

Meditation was deliberately kept simple.

Typical template:

> Meditate  
> 10 minutes/day

Duration can optionally be recorded.

No specialized meditation ecosystem is required.

### Rejected scope

- meditation quality scores
- guided meditation
- breathing analysis
- meditation library
- detailed meditation types

---

# 14. Task Creation

Tasks were intentionally kept simpler than Habits.

## Basic information

- Name
- Icon
- Notes

## Optional scheduling information

- Due date
- Due time
- Reminder
- Repeat

### Due date

Possible choices:

- Today
- Tomorrow
- Pick a date
- No due date

A due date is not mandatory.

---

## Due time vs Reminder

These are separate concepts.

**Due time**

> When the task is actually due.

**Reminder**

> When Momentum should remind the user.

Example:

> Due: 8:00 PM  
> Reminder: 6:00 PM

---

# 15. Recurring Tasks

Tasks may repeat.

Examples:

> Pay rent — monthly

> Take car for service — every 6 months

A recurring Task remains a Task.

It does not become a Habit.

Each recurrence represents an independent occurrence.

---

# 16. Task Completion

Task completion should be immediate.

Tap:

> ✓

→ Task is completed.

No additional logging is required.

Tasks do not have:

- streaks
- habit targets
- habit progress
- habit trends

---

# 17. What We Deliberately Avoided

During brainstorming, we repeatedly rejected feature creep.

We do not want Create Item to become a generic:

> "Configure every possible thing about your life"

system.

Avoided for initial release:

- categories
- tags
- difficulty
- motivation fields
- rewards
- arbitrary measurement units
- separate Description field
- complex health tracking
- full workout tracking
- full sleep tracking
- book tracking
- unnecessary task analytics
- excessive configuration steps

The guiding principle is:

> Only ask the user for information that Momentum needs to correctly understand and track the commitment.

---

# 18. Core Product Principles Established

### One habit = one behavioral commitment

Example:

> Morning Run

should not also contain:

> Evening Walk

Create another Habit.

---

### Templates accelerate creation

Templates are not separate habit types.

> Template → sensible defaults → normal Habit

---

### Tracking should be proportional to the activity

If the user just wants to mark something done:

> Let them mark it done.

Do not force measurement.

---

### Progressive disclosure

Only expose configuration when it becomes relevant.

---

### Momentum is the consistency layer

Momentum should track:

> Did I follow through?

It should not attempt to become:

- a fitness platform
- a sleep tracker
- a reading platform
- a meditation platform
- a health-monitoring platform

---

# 19. Current Create Item Mental Model

```text
Create Item
│
├── Habit
│   │
│   ├── Sleep template
│   ├── Drink Water template
│   ├── Exercise template
│   ├── Read template
│   ├── Meditate template
│   └── Custom Habit
│
└── Task
    └── Custom Task

Underlying principle:

Habit Template
      ↓
Pre-filled configuration
      ↓
Normal Habit

There are no special permanent Habit subtypes created by the templates.

---

# 20. Open Questions

These should be resolved before implementation where they affect the domain model:

Exact overnight occurrence semantics for Sleep.
Exact supported measurement units and conversion rules.
How target progress is represented for historical occurrences.
How Reduce/Avoid violations are recorded.
Whether recurring Tasks need historical occurrence navigation in Day Review.
Exact reminder behavior for measurable habits.
How editing a Habit's target affects historical data.

These are intentionally left open rather than prematurely decided.

---