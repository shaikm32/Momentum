# Create Item Specification

## 1. Overview

Create Item allows the user to create either a Habit or a Task.

Flow:

Create Item
→ Habit / Task
→ Configure
→ Review
→ Create

---

# 2. Habit

## 2.1 Definition

A Habit is something the user is trying to build consistently or reduce/avoid over time.

A Habit may be created from a predefined Habit Template or from scratch.

Once created, a Habit is a normal user-created Habit regardless of its origin.

Templates must not create separate domain entities such as SleepHabit, WaterHabit, or ExerciseHabit.

---

## 2.2 Habit Templates

Initial predefined templates:

1. Sleep
2. Drink Water
3. Exercise
4. Read
5. Meditate

Templates provide sensible defaults and a specialized creation experience where necessary.

The user can modify the configuration before creating the Habit.

Templates are starting points, not permanent classifications.

---

## 2.3 Custom Habit

Custom Habit starts with:

- Name — required
- Icon — required
- Notes — optional free-form text

Notes are intentionally unstructured. Users may use them for descriptions, instructions, context, or any other useful information.

There is no separate Description field.

---

## 2.4 Habit Direction

The user chooses one of two directions:

### Build

The user wants to consistently do something.

Examples:

- Read
- Exercise
- Practice guitar

### Reduce / Avoid

The user wants to reduce or avoid something.

Examples:

- Limit social media
- Avoid junk food
- Stay smoke-free

---

## 2.5 Build Tracking

Build habits support two tracking modes.

### Binary

The user only needs to indicate whether the habit was completed.

Example:

Meditate → Complete

### Target

The user wants to reach a measurable target.

Example:

Read → 30 minutes

The target consists of:

- numeric value
- supported unit

Initial supported units may include:

- Minutes
- Hours
- Pages
- Times
- Glasses
- Litres
- Steps

Custom units are intentionally excluded from the initial release.

---

## 2.6 Reduce / Avoid Tracking

Reduce / Avoid habits support two modes.

### Avoid completely

The goal is to remain at zero occurrences.

Example:

Stay smoke-free.

The user should not need to explicitly record a successful avoidance every day.

### Limit

The goal is to remain at or below a defined limit.

Example:

Social media → ≤ 60 minutes/day

The limit consists of:

- numeric value
- supported unit

---

## 2.7 Habit Schedule

Habits support:

- Every day
- Selected days
- Custom recurrence

The creation UI should remain simple even though the underlying recurrence system may support more complex rules.

---

## 2.8 Habit Reminder

Reminder is optional.

Options:

- Off
- Specific reminder time

Additional reminder complexity is not required for the initial release.

---

# 3. Predefined Habit Templates

## 3.1 Sleep

Sleep is a specialized Habit Template because it crosses midnight.

Sleep supports:

- Sleep by target time
- Minimum sleep duration
- Either goal independently
- Both goals together

Completion/recording and goal success are separate concepts.

Recording sleep should remain possible even when one or more goals were missed.

Sleep is not intended to become a full sleep-tracking system.

Out of scope for initial release:

- Sleep quality
- Night awakenings
- Nap tracking
- Sleep stages
- Sleep consistency scoring
- Bedtime/wake-time ranges
- Weekday/weekend-specific goals

---

## 3.2 Drink Water

Water is a quantity-based Habit Template.

Preset targets:

- 6 glasses
- 8 glasses
- 10 glasses
- 12 glasses
- Custom

The user may use supported units such as:

- Glasses
- ml
- Litres

The presets are convenience options, not medical recommendations.

Water supports accumulated progress during the day.

---

## 3.3 Exercise

Exercise is a Habit Template.

Each Exercise Habit represents one specific exercise activity.

Initial presets:

- Walking
- Running
- Cycling
- Yoga
- Workout
- Custom

Only one exercise activity may be selected for a single Exercise Habit.

Users may create multiple Exercise Habits.

Example:

- Morning Run — Running — 30 min
- Evening Walk — Walking — 20 min

These are independent Habits with independent schedules, histories, streaks, and insights.

Exercise may optionally record duration/time when completing the Habit.

Exercise does not become a workout tracker.

Out of scope:

- Calories burned
- Workout plans
- Exercise sets/reps
- Full workout logging
- Fitness metrics

---

## 3.4 Read

Reading is a Habit Template.

Supported target styles:

- Time
- Pages

Example:

- Read — 30 minutes/day
- Read — 10 pages/day

Momentum tracks the Habit, not books or a reading library.

Out of scope:

- Book databases
- ISBN lookup
- Chapters
- Authors
- Reading libraries
- Goodreads-style functionality

---

## 3.5 Meditate

Meditation is a simple duration-based Habit Template.

Example:

Meditate — 10 minutes/day

The user may complete the Habit directly or optionally record duration.

Out of scope:

- Meditation quality scores
- Guided meditation
- Breathing analysis
- Meditation libraries
- Detailed meditation types

---

# 4. Task

## 4.1 Definition

A Task is something the user wants to get done.

Tasks are distinct from Habits.

A Task may be:

- one-time
- recurring

A recurring Task remains a Task and does not become a Habit.

---

## 4.2 Task Basic Details

Required:

- Name
- Icon

Optional:

- Notes

Notes are free-form and may contain descriptions, instructions, or context.

---

## 4.3 Task Due Date

Due date is optional.

Options:

- Today
- Tomorrow
- Pick a date
- No due date

---

## 4.4 Task Time

Optional.

A Task may have:

- No specific time
- Specific due time

Due time represents when the Task is due.

---

## 4.5 Task Reminder

Optional.

The reminder is separate from the due time.

Examples:

- Task due at 8:00 PM
- Reminder at 6:00 PM

---

## 4.6 Task Recurrence

Tasks may optionally repeat.

Examples:

- Pay rent — monthly
- Take car for service — every 6 months

Recurring Tasks generate independent occurrences.

---

## 4.7 Task Completion

Tapping the completion control should complete the Task immediately.

Tasks do not have:

- Streaks
- Habit goals
- Habit progress
- Habit trends
- Habit analytics

Task detail focuses on the task and its occurrences.

---

# 5. Shared Principles

Habits and Tasks may share:

- Name
- Icon
- Notes
- Schedule
- Reminder
- Completion
- Active/paused/archived lifecycle

However, shared fields do not mean shared product semantics.

The distinction must remain clear:

Habit:
> What am I consistently building or reducing?

Task:
> What do I need to get done?

---

# 6. Creation UX Principle

Use progressive disclosure.

Do not show every possible configuration field immediately.

Example:

A binary custom Habit should not show target/unit controls.

A target-based Habit should reveal target configuration only after the user chooses target tracking.

A Task should not show recurrence configuration unless the user chooses to repeat it.

The creation flow should feel lightweight while still supporting advanced configuration when needed.

---

# 7. Final Mental Model

Home:
> What needs my attention today?

Habit:
> What am I trying to build or reduce consistently?

Task:
> What am I trying to get done?

Habit Template:
> Give me a sensible starting configuration.

Custom Habit:
> Let me define my own commitment.