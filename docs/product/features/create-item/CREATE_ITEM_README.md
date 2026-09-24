# Create Item

The Create Item flow is the entry point for creating Habits and Tasks in Momentum.

## Purpose

Create Item gives users a simple way to add something they want to:

- build or reduce consistently (Habit)
- get done (Task)

The flow intentionally keeps Habit and Task concepts distinct while allowing both to share common properties such as name, icon, notes, schedule, and reminders.

## Product Principles

- A Habit is a user-created entity.
- A Habit Template is only a starting point for creating a Habit.
- Templates pre-fill sensible configuration but do not create special habit types.
- A Task is fundamentally different from a Habit.
- Configuration should be progressive: only show fields relevant to the user's choices.
- Tracking should never become more work than the activity itself.
- Avoid exposing internal domain concepts or implementation details to users.

## Navigation

Home → `+` → Create Item → Habit or Task