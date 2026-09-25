# Momentum — Habits Management UI Specification

## Status
Finalized visual direction for Phase 2B.

## Design Theme
**Soft Aurora**

The Habits experience uses the finalized Momentum Aurora design language:
- soft pastel lavender, blue, and pink atmosphere
- clean, calm surfaces
- rounded cards with subtle elevation
- restrained gradients and glow
- strong readable typography
- minimal visual noise
- consistent bottom navigation
- light and dark theme support

Habits is intentionally calmer and more functional than Home. It is a
management surface, not a dashboard.

## 1. Habits Screen

### Header — collapsing Soft Aurora hero
One cohesive unit above the list:

- **Artwork:** two bundled Aurora illustrations — `habits_hero_light.webp`
  (light theme) and `habits_hero_dark.webp` (dark theme) — chosen by the
  active Material theme and used exactly as supplied (no tint, filter,
  recolor, or brightness change). The artwork spans the full screen width with
  a bottom-anchored cover crop, and dissolves top and bottom into the screen
  background so it reads as part of the environment, never as a pasted
  rectangle. No programmatic/generated artwork.
- **Title:** **Habits**, centered inside the lower portion of the artwork.
- **Supporting text:** **Manage the things you're building and reducing.**,
  centered directly below the title.
- **Scroll collapse:** scrolling the list down collapses the artwork into a
  compact header; scrolling up restores the full hero. Collapse is
  continuous, bounded, and driven directly by scroll position — one clamped
  px source of truth, no snapping, no jumps, no settle animation, no
  Boolean expanded/collapsed states.
- **Title and description never disappear:** they stay visible in the
  collapsed state in theme ink. Both artwork variants are built for their
  theme, so text color never changes during the scroll.
- **Reduced motion:** the header height, crop, and dissolve still track the
  scroll 1:1 (the hero has no time-based animation); only the decorative text
  inset interpolation is frozen.
- The hero is not interactive; creation entry stays the single floating
  action (see §3).
- No search or filter controls in the initial release.

### Habit List
One continuous list.

Ordering:
1. Active habits
2. Paused habits

Do not create separate Active / Paused section headers.

### Habit Card

```text
[Icon]  Habit Name                  ● Active   ⋮
        Frequency · Target · Reminder
```

Requirements:
- icon
- user-defined habit name
- explicit Active or Paused status
- overflow action menu
- frequency
- target / measurement
- reminder
- compact vertical height
- generous touch targets
- no progress, streak, completion, history, notes, start/end dates, or
  template classification

Status is text plus a subtle indicator. Do not use icon-only status.

### Paused Card
Paused habits remain visually present and usable.

Use:
- `● Paused`
- slightly reduced visual emphasis

Do not gray out or disable the entire card.

### Card Interaction
- Tapping the card opens Habit Details.
- Tapping `⋮` opens management actions.

Active menu:
- Pause Habit
- Delete Habit

Paused menu:
- Resume Habit
- Delete Habit

Delete requires confirmation.

## 2. Empty State

When no habits exist:
- small calm Aurora illustration
- **No habits yet**
- short supporting copy
- primary **Create Habit** action

The empty state should feel inviting, not like an error.

## 3. Creation Entry

The header `+` opens the existing Create Item flow.

Do not introduce a second floating action button.

## 4. Bottom Navigation

Use the finalized primary navigation:

**Home | Habits | Tasks | Insights**

Habits is visually selected when the user is on the Habits destination.

**Settings is NOT a bottom-navigation destination.**

Settings is accessed from the **Home screen header**, using the existing
application shell behavior.

Do not redesign or change the existing Tasks destination as part of Phase 2B.

The navigation remains consistent with the existing Momentum app shell.

## 5. Habit Details

Habit Details uses the same Soft Aurora visual language but provides deeper
information than the Habits list.

Recommended information hierarchy:

1. Habit identity
2. Key configuration
3. Weekly completion
4. Streak
5. Full configuration / notes
6. Management actions

### Header / Identity
Show:
- back navigation
- Habit icon
- Habit name
- Active / Paused status

For predefined Habits, the icon is visually treated as fixed and is not
editable.

For Custom Habits, the icon remains editable.

Do not place a management `⋮` in the top-right.

### Key Configuration
Use compact Aurora cards/chips for:
- Frequency
- Target / measurement
- Reminder

Keep these concise and scannable.

## 6. Weekly Completion

Do **not** use a bar chart for the primary weekly progress view.

A Habit can be completed at most once per day, so use a seven-day visual:

```text
This Week                         4/7 completed

   ✓      ✓      ✓      ○      ○      ○      ○
   M      T      W      T      F      S      S
```

States:
- completed
- incomplete
- not scheduled

Not-scheduled days must be visually distinct from missed/incomplete days.

For example, a Mon/Wed/Fri Habit must not make Tue/Thu/Sat/Sun look missed.

The weekly view is historical information and belongs on Habit Details.

## 7. Streak

Place a compact streak summary directly below or adjacent to the weekly
completion view.

Show:
- current streak
- longest streak

Example:

```text
🔥 12 day streak
Longest: 28 days
```

Keep the component compact and secondary to the weekly completion view.

Streak does not appear on Habit cards in the Habits list.

## 8. Full Configuration

Use progressive sections/cards for:
- Habit direction
- Tracking mode
- Target / measurement
- Frequency
- Start date
- End date
- Reminder
- Notes

Do not present every field with equal visual weight.

The Details screen should remain readable even when the Habit has extensive
configuration.

## 9. Edit Habit

Editing should feel like configuring the **existing Habit**, not creating a
new one.

### Predefined Habit
Editable:
- user-owned name
- Notes
- applicable configuration
- frequency
- reminder
- target / measurement where supported

Locked:
- predefined Habit icon
- underlying template/type
- Habit identity

### Custom Habit
Editable:
- name
- icon
- Notes
- supported configuration
- frequency
- reminder
- target / measurement where applicable

### Important Boundary

The Edit flow must not allow the user to transform one Habit into another
Habit concept.

Example:

```text
Exercise Habit
→ edit Exercise configuration
→ cannot convert to Meditation
```

The Habit ID remains unchanged after edits so history and Insights remain
attached to the same Habit.

## 10. Bottom Management Actions

Replace the top-right `⋮` on Habit Details with a centered bottom floating
action group.

Actions:

```text
[ Pause/Resume ]   [ Edit Habit ]   [ Delete ]
```

Design:
- centered near the bottom
- large touch targets
- Edit is the primary action
- Pause/Resume is secondary
- Delete uses the destructive visual treatment
- visually separated enough to prevent accidental deletion

The action group should be reachable comfortably with one hand.

Delete opens a confirmation surface before permanent deletion.

## 11. Delete Confirmation

Use a calm Aurora confirmation bottom sheet/dialog.

Clearly state that deletion permanently removes the Habit and its associated
data.

Actions:
- **Delete** — destructive
- **Cancel** — secondary

Do not hide the destructive consequence.

## 12. Paused Habit Details

Paused Details uses the same structure as Active Details.

Differences:
- status changes to `● Paused`
- primary management action becomes **Resume**
- visual emphasis is slightly reduced
- historical information remains visible

Do not make the paused screen look disabled.

## 13. Visual Boundaries

Habits should communicate:

> What am I managing as an ongoing behavior?

Habit Details should communicate:

> How has this Habit performed over time?

Habits should NOT become:
- a daily completion screen
- a generic progress dashboard
- a chart-heavy analytics screen

Home owns daily action. Habit Details / Insights own historical analysis.

## 14. Design Principle

**Habits = manage.  
Home = act today.  
Details / Insights = understand over time.**

Avoid adding visual elements merely because they are attractive. Every element
must support scanning, management, historical understanding, or navigation.
