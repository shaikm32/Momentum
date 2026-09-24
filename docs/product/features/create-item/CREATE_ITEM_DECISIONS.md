# Create Item Decisions

## Decision 01 — Habit vs Habit Template

A Habit is the actual user-created entity.

A Habit Template is only a predefined starting point.

Templates populate configuration and may provide specialized setup UX, but the resulting object is always a normal Habit.

---

## Decision 02 — Custom Habit Direction

Custom Habits support two directions:

- Build
- Reduce / Avoid

This allows Momentum to support both positive routines and behaviors the user wants to reduce.

---

## Decision 03 — Habit Tracking

Build Habits support:

- Binary completion
- Target-based tracking

Reduce / Avoid Habits support:

- Avoid completely
- Limit

---

## Decision 04 — Exercise

An Exercise Habit represents exactly one exercise activity.

Initial presets:

- Walking
- Running
- Cycling
- Yoga
- Workout
- Custom

Multiple Exercise Habits are allowed.

Example:

Morning Run and Evening Walk are separate Habits.

---

## Decision 05 — Notes

Habits and Tasks have one optional free-form Notes field.

There is no separate Description field.

Notes may contain descriptions, instructions, context, or any other user-provided information.

---

## Decision 06 — Task Semantics

Tasks are independent from Habits.

Tasks may be one-time or recurring but never have Habit streaks or Habit analytics.

Tasks focus on completion, due dates, reminders, and occurrences.

---

## Decision 07 — Progressive Disclosure

Creation forms reveal configuration progressively based on user choices.

The UI should not expose internal tracking concepts unnecessarily.

The goal is fast creation with optional depth.