# Momentum — Habits Management
## Product Decisions & Design Constraints

**Status:** Finalized  
**Scope:** Habits list / management and Habit Details experience  
**Phase:** 2B

This document records the finalized product decisions for the Habits
management experience. These decisions should be treated as product
constraints unless explicitly revisited.

---

# 1. Habits Screen Role

The Habits screen is a **management screen**.

It is not a daily tracking or completion screen.

The Habits screen allows the user to:

- View their Habits
- Open Habit details
- Edit Habit configuration
- Pause or resume a Habit
- Permanently delete a Habit

Daily Habit completion belongs to Home.

Historical Habit analysis belongs to Habit Details / Insights.

---

# 2. Habit Lifecycle

The Habit lifecycle is:

```text
Active ↔ Paused → Permanently Deleted
```

There is no Archive concept in the Habit management model.

A paused Habit remains in the user's Habit list and can be resumed.

Deletion is permanent and requires confirmation.

---

# 3. Habit Card

Each Habit is represented by a compact management card.

The card contains:

- Habit icon
- Habit name
- Active / Paused status
- Action menu
- Frequency
- Target / measurement
- Reminder

Conceptually:

```text
┌──────────────────────────────────────────────┐
│ [icon]  Habit Name                 ● Active ⋮│
│                                              │
│         Frequency · Target · Reminder        │
└──────────────────────────────────────────────┘
```

For a paused Habit:

```text
[icon]  Habit Name                 ● Paused ⋮
```

---

# 4. Habit Card Information Hierarchy

The card should be optimized for quick scanning.

### Primary information

- Icon
- Habit name
- Active / Paused status

### Secondary information

- Frequency
- Target / measurement
- Reminder

The card must not become a miniature Habit Details screen.

---

# 5. Information Explicitly Excluded From Habit Cards

Do not display:

- Streak
- Completion status
- Completion progress
- Historical information
- Start date
- End date
- Notes / description
- Predefined vs custom classification

These belong to other parts of the product.

---

# 6. Habit Status

Status is displayed as:

```text
● Active
```

or

```text
● Paused
```

Use text plus a subtle visual indicator rather than an icon-only status.

The status is positioned alongside the Habit name, before the action menu.

---

# 7. Habit Action Menu

The `⋮` action menu provides management actions from the **Habits list**.

For an Active Habit:

- Pause
- Delete

For a Paused Habit:

- Resume
- Delete

Daily completion must not be exposed through this menu.

Habit Details uses the bottom management actions defined in Section 19.

---

# 8. Habit Name

Habit name is user-owned identity information.

A predefined Habit Template must **not automatically populate the editable
Habit Name during creation**.

For example:

```text
Template: Exercise
Name: [user enters name]
```

The user may therefore create:

```text
Morning Workout
```

or

```text
Evening Run
```

from the Exercise template.

The Habit's internal identity must be independent of its name so that renaming
a Habit does not break its history or Insights.

Conceptually:

```text
Habit ID: 8472
Template/Type: EXERCISE
Name: Morning Workout
```

Templates are starting points only. Once created, the result is a normal
user-created Habit.

---

# 9. Notes

There is no Description field.

A Habit has one optional free-form **Notes** field.

Notes may contain:

- Description
- Instructions
- Context
- Personal information

Notes are edited from Habit Details and are not displayed on the Habit card.

---

# 10. Predefined vs Custom Habits

Predefined and Custom Habits use the same Habit card.

The card must not visually distinguish them.

The distinction exists during creation/configuration.

Predefined Habit Templates:

- Provide sensible defaults
- May provide specialized configuration
- May provide domain-specific measurement choices

Custom Habits:

- Allow the user to define their own commitment
- Remain flexible within Momentum's supported configuration model

Templates are not separate Habit domain entities.

---

# 11. Predefined Habit Icon Lock

For a Habit created from a predefined Habit Template:

- The template's icon is locked after creation.
- The user cannot replace that icon from Habit Details or Edit Habit.

For a Custom Habit:

- The user may edit the selected icon.

The lock applies to the icon only; it does not prevent editing the
configuration properties that are valid for that Habit.

---

# 12. Habit Frequency

Frequency is required.

Supported conceptual choices are:

- Every day
- Selected days
- Custom recurrence

The creation UI should remain simple even though the underlying recurrence
system may support more complex rules.

For the Habit card:

### Every day

Display:

```text
Daily
```

### Selected days

Display the selected days concisely:

```text
Mon · Wed · Fri
```

Frequency is always represented in the card metadata row.

---

# 13. Start Date

Start date is optional.

If the user provides a start date:

- The Habit begins on that date.

If the user does not provide one:

- Default to Today.

Start date is configuration information and is not displayed on the Habit card.

---

# 14. End Date

End date is optional.

If provided:

- The Habit ends on that date.

If omitted:

```text
No end date
```

An end date cannot precede the start date.

End date is configuration information and is not displayed on the Habit card.

---

# 15. Habit Tracking Model

The Habit tracking model is determined during creation.

## Build

Build Habits support:

- Binary completion
- Target-based tracking

Binary example:

```text
Meditate → Complete
```

Target example:

```text
Read → 30 minutes
```

## Reduce / Avoid

Reduce / Avoid Habits support:

- Avoid completely
- Limit

Avoid completely example:

```text
Stay smoke-free
```

Limit example:

```text
Social media → ≤ 60 minutes/day
```

These are finalized Create Item decisions and should not be reinterpreted
by the Habits management UI.

---

# 16. Target / Measurement

Target is mandatory whenever the selected Habit tracking mode is
target-based.

Target-based tracking consists of:

- Numeric value
- Supported unit

Initial supported units include:

- Minutes
- Hours
- Pages
- Times
- Glasses
- Litres
- Steps

Custom units are excluded from the initial release.

Predefined Habit Templates may provide domain-appropriate measurement
choices and sensible presets.

Examples:

```text
Drink Water → Glasses / ml / Litres
Exercise → duration
Read → Time / Pages
Meditate → duration
```

The Habit card displays the resulting target/measurement concisely.

---

# 17. Reminder

Reminder is optional.

Supported states:

```text
Off
```

or

```text
Specific reminder time
```

If configured, the reminder time appears in the Habit card metadata.

If not configured, the card uses:

```text
—
```

The metadata row therefore remains structurally consistent:

```text
Frequency · Target · Reminder
```

Additional reminder complexity is not part of the initial release.

---

# 18. Habit Details

The Habit Details screen is the place for full Habit configuration,
management, and historical information.

It may contain:

- Icon
- Name
- Notes
- Habit direction
- Tracking mode
- Target / measurement
- Frequency
- Start date
- End date
- Reminder
- Weekly completion view
- Current streak
- Longest streak
- Completion history
- Trends
- Calendar / heatmap history
- Date filtering
- Pause / Resume
- Edit
- Delete

The management card should not duplicate this information.

---

# 19. Habit Details Weekly Completion View

Because a Habit can be completed at most once per day, Habit Details must
not use a bar chart as the primary short-range progress visualization.

Instead, show the days of the selected week:

```text
This Week                         4/7 completed

   ✓      ✓      ✓      ○      ○      ○      ○
   M      T      W      T      F      S      S
```

The visual state must distinguish:

- **Completed** — completed on the scheduled day
- **Incomplete** — scheduled day with no completion
- **Not scheduled** — day outside the Habit's frequency

For example, a Mon/Wed/Fri Habit should not show Tue/Thu/Sat/Sun as
missed completions.

The week view is historical information and belongs on Habit Details.

---

# 20. Habit Details Streak

Habit Details must display a compact streak summary.

At minimum:

- Current streak
- Longest streak

Example:

```text
🔥 12 day streak
Longest: 28 days
```

Streak is historical information and must not appear on the Habits list card.

---

# 21. Editing Existing Habits

**Edit means configure the existing Habit; it does not convert the Habit
into a different Habit.**

When editing:

- The user may change configuration properties that are valid for that
  particular Habit.
- The user may rename the Habit.
- The user may edit Notes.
- The user may change supported frequency, reminder, target/measurement,
  and other applicable configuration.
- A predefined Habit's locked icon cannot be changed.
- A Custom Habit's icon may be changed.

The Edit flow must **not** offer an operation that changes the Habit into a
different template/type or otherwise transforms it into a different Habit
concept.

Examples:

```text
Exercise Habit
→ edit its Exercise-related configuration
→ cannot convert it into a Meditation Habit
```

```text
Custom "Read every night"
→ edit its supported configuration
→ cannot switch its underlying Habit identity into another predefined type
```

The Habit ID remains unchanged through editing so historical data and
Insights remain attached to the same Habit.

---

# 22. Habit Details Management Actions

The Habit Details screen uses accessible **floating management buttons
centered at the bottom** rather than a top-right `⋮` menu.

The action group contains:

- Pause / Resume
- Edit Habit
- Delete Habit

The **Edit Habit** action is the primary action.

Delete uses the destructive visual treatment and requires confirmation.

The bottom action group should remain easy to reach with one hand.

The top area of Habit Details therefore does not need a management `⋮`
button.

---

# 23. Daily Completion Boundary

The Habits screen and Habit Details must not provide daily
completion/uncompletion controls.

Completion belongs to Home.

Home follows:

```text
Tick → Complete
Activity body → Details
```

This keeps management and daily execution separate.

---

# 24. Creation UX Principle

Habit creation follows:

> **Minimum typing, maximum selection.**

Prefer:

- Buttons
- Chips
- Pickers
- Dropdowns
- Predefined options
- Sensible template defaults

Use text fields only where user-provided text is genuinely necessary.

Custom Habits must remain flexible, but should still use structured controls
where practical.

The creation flow uses progressive disclosure: only configuration relevant
to the user's choices should be revealed.

---

# 25. Product Boundary

The product should maintain a clear separation:

```text
Home
→ What needs my attention today?

Habits
→ What am I managing as an ongoing behavior?

Habit Details / Insights
→ How has this Habit performed over time?

Create Item
→ What commitment am I creating?
```

The Habits screen should therefore remain:

- Clean
- Scannable
- Management-focused
- Low-noise

It should not become another dashboard.

---

# 26. Decision Revisit Rule

These decisions are considered finalized.

They should only be revisited when:

1. User research identifies a meaningful usability problem.
2. Analytics demonstrate a significant behavior problem.
3. Accessibility requires a change.
4. Technical constraints make the current design impractical.
5. A broader product decision invalidates the assumption.

When a finalized decision is changed, document:

- Original decision
- Evidence for reconsideration
- Alternatives considered
- New decision
- Date
- Impacted screens/components
