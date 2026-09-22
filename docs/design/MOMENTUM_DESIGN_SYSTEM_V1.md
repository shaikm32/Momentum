# Momentum Design System v1.0

**Status:** Locked  
**Product:** Momentum  
**Design direction:** Soft Aurora + Motion-First  
**North star:** Calm at rest. Alive in motion.

---

## 1. Purpose

This document is the source of truth for Momentum's visual and interaction design system.

Product behavior and information architecture are already established separately. This document defines how those decisions are expressed visually, spatially, and through motion.

The design system should be implemented as reusable Compose primitives. Individual screens should compose these primitives rather than inventing new visual patterns.

---

## 2. Design principles

1. Content creates hierarchy; decoration reinforces it.
2. Premium comes from restraint, consistency, and thoughtful motion.
3. Progress is Momentum's visual language.
4. Motion has meaning.
5. Surfaces are earned; not every element becomes a card.
6. BUILD and REDUCE are equally valid directions and must not be represented as good/bad.
7. Completion should feel satisfying, not punitive.
8. Insights should explain behavior, not become a dashboard full of charts.
9. Light and Dark Aurora are two environments of the same design language.
10. Product behavior wins whenever visual treatment conflicts with established product decisions.

---

# 3. Theme

Momentum supports two first-class Aurora environments:

- Light Aurora
- Dark Aurora

Default theme behavior: follow the system, with manual Light/Dark override supported.

### Light Aurora

| Token | Value |
|---|---|
| Background | `#F7F8FC` |
| Surface | `#FFFFFF` |
| Soft Surface | `#F1F2FB` |
| Elevated | `#FFFFFF` |
| Primary Text | `#172033` |
| Secondary Text | `#596579` |
| Muted Text | `#8993A4` |
| Disabled | `#B8BFCA` |
| Aurora Blue | `#5C9FEF` |
| Aurora Lavender | `#9278E8` |
| Aurora Violet | `#8064DD` |
| Aurora Mint | `#55C8AD` |
| Aurora Peach | `#EAA477` |

### Dark Aurora

| Token | Value |
|---|---|
| Background | `#080D18` |
| Surface | `#111827` |
| Soft Surface | `#151E30` |
| Elevated | `#172033` |
| Interactive | `#1D2940` |
| Primary Text | `#F5F7FC` |
| Secondary Text | `#AEB8CA` |
| Muted Text | `#737F95` |
| Disabled | `#4D586B` |
| Aurora Blue | `#78B7FF` |
| Aurora Lavender | `#B59CFF` |
| Aurora Violet | `#9B7CFF` |
| Aurora Mint | `#76DFC4` |
| Aurora Peach | `#FFC49B` |

These raw values should be exposed through semantic tokens rather than used directly by screens.

### Semantic color tokens

```text
background
surface
surfaceElevated
surfaceInteractive

textPrimary
textSecondary
textMuted
textDisabled

accentPrimary
accentSecondary
accentTertiary

success
warning
danger

borderSubtle
borderStrong

progressTrack
progressFill
```

BUILD and REDUCE do not receive permanent semantic colors.

---

# 4. Typography

Typeface: **Manrope**

| Token | Size | Line height | Weight |
|---|---:|---:|---:|
| Display | 40sp | 48sp | 600 |
| Headline | 28sp | 34sp | 600 |
| Title | 20sp | 26sp | 600 |
| Body | 16sp | 24sp | 400 |
| Body Small | 14sp | 20sp | 400 |
| Label | 13sp | 18sp | 500 |
| Caption | 12sp | 16sp | 500 |

Avoid 800/900 weights.

Numbers are first-class UI elements. Progress counts, streaks, targets, durations, percentages, and trends should receive deliberate numeric hierarchy.

---

# 5. Spacing

Base grid: **4dp**

```text
4dp   micro
8dp   tight
12dp  compact
16dp  standard
20dp  screen/content padding
24dp  section
32dp  major section
40dp  large separation
48dp  hero
64dp  exceptional
```

Default horizontal screen padding: **20dp**.

Avoid arbitrary spacing values unless implementation requires a specific exception.

---

# 6. Shapes

Use a small, consistent shape vocabulary:

```text
8dp   small controls
12dp  buttons / chips
16dp  standard surfaces
20dp  feature surfaces
28dp  hero / special progress elements
```

No random corner radii.

---

# 7. Surfaces and depth

Visual hierarchy:

```text
Canvas
  -> Surface
  -> Elevated Surface
  -> Interactive Surface
```

Dark Aurora relies primarily on tonal contrast, subtle borders, and atmospheric glow.

Light Aurora relies primarily on tonal contrast, very soft shadows, and subtle borders.

Avoid heavy Material elevation shadows.

### Glass

Glass is a special effect, not the foundation.

Allowed for:
- Progress Hero
- focused/active states
- floating controls
- milestone moments
- selected insight visualizations

Not allowed as a default treatment for:
- every card
- every row
- navigation
- entire screens
- every button

---

# 8. Iconography

Primary icon family: **Material Symbols Rounded**.

Sizes:

```text
20dp  secondary/action
24dp  primary UI/navigation
28dp  prominent contextual icons
```

Navigation:
- inactive: outlined
- active: filled

Do not mix icon families.

Habit icons should use a curated, consistent set of Material Symbols Rounded.

---

# 9. Buttons

Three levels only.

### Primary
Aurora-toned filled button.

### Secondary
Tonal surface button.

### Tertiary
Text or icon action.

Rules:
- minimum 48dp touch target
- 12dp radius
- clear pressed state
- subtle touch-scale feedback

Avoid unnecessary pill buttons.

---

# 10. Inputs and forms

Use soft tonal fields rather than generic Material outlined fields everywhere.

Focus state:
- subtle Aurora border
- subtle Aurora glow
- stable label/text hierarchy

Create/edit flows should be focused and low-friction. Secondary decisions can be presented in bottom sheets.

---

# 11. Navigation

Bottom navigation is fixed:

1. Home
2. Habits
3. Tasks
4. Insights

Settings is **not** a bottom navigation destination.

Settings is accessed from the top-right/appropriate app-bar action.

Bottom navigation should be visually integrated with the screen rather than becoming a large floating Material component.

---

# 12. App bars

Keep app bars lightweight.

Home example:

```text
Good morning                              Settings
```

Secondary screen example:

```text
Back       Habits
```

Avoid oversized toolbars.

---

# 13. Home design language

Home has one job:

**How am I doing today?**

And secondarily:

**What do I still need to do today?**

Home should not become:
- a habit library
- an analytics dashboard
- a calendar
- a history screen
- a detailed habit-management screen

Home uses compact activity rows more than large cards.

---

# 14. Progress Hero

Progress Hero is Momentum's signature component.

Primary communication:

```text
3 / 5
completed today

[progress visualization]

2 remaining
```

Rules:
- large numeric hierarchy
- subtle Aurora
- smooth progress transitions
- no excessive decoration
- no long-term analytics

At 100%:

**Today is complete**

The completion state is calm and rewarding rather than loud.

---

# 15. Activity Row

Activity Row is the primary action component.

It should communicate:
- icon
- title
- relevant supporting information
- current state
- completion/control affordance

Rules:
- title dominates
- supporting metadata is secondary
- descriptions should not unnecessarily repeat the title
- completion is obvious
- row transforms visibly when completed
- avoid decorative clutter

---

# 16. Habit Card

Habit Cards are for understanding/summary, not rapid daily action.

Used primarily in:
- Habits
- habit detail
- relevant Insights contexts

Possible information:
- habit icon
- name
- BUILD/REDUCE
- target
- streak
- trend
- progress summary

Do not cram all information into every card.

---

# 17. BUILD visual language

BUILD communicates:

**I am developing this behavior.**

Visual concepts:
- forward movement
- accumulation
- progress
- continuity

Avoid making BUILD synonymous with green or "success."

---

# 18. REDUCE visual language

REDUCE communicates:

**I am managing this behavior.**

It must not visually communicate failure or shame.

REDUCE is divided into two established semantics:

### LIMIT

Quantitative control.

Example:

```text
23 / 30 min
████████████░░░
```

### COMPLETE

Binary completion.

Example:

```text
Not completed  ->  Completed
```

LIMIT uses quantity/progress visualization. COMPLETE uses the shared binary completion interaction.

---

# 19. Completion interaction

Completion is a meaningful state transition:

```text
Idle
 -> Pressed
 -> Completing
 -> Completed
 -> Settled
```

Expected response:
1. control responds
2. completion indicator forms
3. today's progress updates
4. subtle Aurora response occurs
5. UI settles

Normal completion does not trigger confetti.

---

# 20. Streaks

Streaks are supporting information, not the emotional center.

Prefer:

**21 day streak**

over highly gamified fire/emoji treatments.

A broken streak should not be visually punitive.

---

# 21. Milestones

Milestones may use stronger expression than ordinary completion.

Allowed:
- stronger Aurora
- subtle light burst
- restrained particles
- richer motion

Routine completion remains quiet.

---

# 22. Tasks

Tasks share Momentum's visual grammar with habits:
- typography
- spacing
- rows
- completion interaction
- Aurora
- motion

Tasks remain semantically distinct through metadata, iconography, grouping, and behavior rather than an unrelated visual language.

---

# 23. Bottom sheets

Use bottom sheets for focused decisions such as:
- frequency
- target/unit
- icon selection
- LIMIT/COMPLETE
- schedule choices

Sheets should be short, focused, and easy to dismiss.

---

# 24. Dialogs

Use dialogs sparingly.

Primary use:
- destructive actions
- irreversible actions
- important confirmations

Avoid confirmation dialogs for routine interactions.

---

# 25. Feedback

### Snackbar
Use only when visual state change is insufficient.

### Loading
Prefer skeleton/content-aware loading and subtle shimmer over generic spinners.

### Empty state
Calm, useful, and action-oriented.

### Error state
Clear, restrained, and actionable.

---

# 26. Insights

Insights contains:
- historical progress
- calendar/history
- trends
- habit analysis
- patterns

Every visualization should answer a question.

Preferred visualization vocabulary:
- line
- bar
- progress
- calendar grid
- distribution

Avoid rainbow charts, 3D charts, and decorative visualization.

Hierarchy:

```text
Question/headline
 -> Key number
 -> Visualization
 -> Interpretation
```

Calendar/history remains inside Insights and is not a separate navigation destination.

---

# 27. Aurora system

Aurora is state-responsive, not decorative wallpaper.

Conceptual Compose primitive:

```kotlin
AuroraBackground(
    intensity = ...,
    state = ...,
    animated = ...
)
```

States:

```text
REST
PROGRESS
COMPLETION
MILESTONE
```

Intensity should be:
- lowest at rest
- slightly stronger during progress
- visibly responsive at completion
- strongest at milestones

Aurora should be programmatically generated in Compose wherever practical.

---

# 28. Motion system

Motion tokens:

```text
Fast       ~180ms
Standard   ~280ms
Emphasis   ~450ms
Reward     ~650ms
Ambient    very slow
```

Use:
- ease-out for entering
- ease-in-out for state transitions
- spring only where physical response adds meaning

Motion should never delay the user.

### Reduced motion

When enabled:
- remove decorative Aurora movement
- remove particles
- reduce transition movement
- preserve meaningful state changes and feedback

---

# 29. Haptics and sound

Haptics:
- completion
- milestone
- important selection

Do not vibrate for every tap.

Sound is not required for the core experience and should remain optional if introduced later.

---

# 30. Accessibility

System-wide requirements:
- minimum 48dp touch targets
- sufficient contrast
- scalable typography
- meaningful content descriptions
- state communicated without color alone
- reduced-motion support
- accessible chart summaries
- correct TalkBack hierarchy
- sensible focus order

---

# 31. Responsive/adaptive behavior

Mobile-first.

On larger phones:
- preserve comfortable content width
- do not stretch everything to edges

On tablets/large screens:
- use a centered content column with sensible max width
- adapt layout intentionally rather than simply scaling a phone UI

Portrait is primary. Landscape remains usable but does not require a separate product design unless implementation reveals a real need.

---

# 32. Asset strategy

Prefer Compose-generated assets for:
- Aurora
- progress visuals
- charts
- calendar visualization
- animation
- glows
- gradients

Material Symbols Rounded for standard and habit icons.

Custom assets only where they provide genuine brand value:
- app icon
- logo/wordmark
- selective onboarding/milestone artwork
- exceptional empty-state illustration when justified

Do not use stock imagery throughout the product.

---

# 33. Explicitly rejected patterns

Unless implementation demonstrates a genuine need:

- giant cards everywhere
- excessive gradients
- purple everywhere
- green/red BUILD vs REDUCE
- emoji as UI
- fire-streak gamification
- routine confetti
- glass everywhere
- mixed icon families
- giant app bars
- excessive button variants
- heavy shadows
- decorative charts
- generic Material forms everywhere
- separate calendar navigation
- Settings bottom-nav item
- full habit library on Home
- detailed analytics on Home

---

# 34. Compose architecture

Target structure:

```text
MomentumTheme
├── MomentumColors
├── MomentumTypography
├── MomentumSpacing
├── MomentumShapes
├── MomentumElevation
├── MomentumIcons
├── MomentumMotion
├── MomentumAurora
└── MomentumComponents
    ├── Foundation
    ├── Interaction
    ├── Navigation
    ├── Progress
    ├── Habits
    ├── Tasks
    ├── Insights
    └── Feedback
```

Screens consume these primitives.

Screens should not directly invent:
- colors
- typography
- radii
- spacing
- animation durations
- icon families
- arbitrary surface treatments

---

# 35. Design-system acceptance criteria

The design system is considered correctly implemented when:

- Light and Dark Aurora feel like the same product.
- Screens share the same spacing, typography, surfaces, and interaction language.
- BUILD and REDUCE are visually distinct without good/bad color coding.
- Home remains focused on today's progress.
- Habits, Tasks, and Insights remain clearly differentiated.
- Completion feels rewarding but restrained.
- Aurora and motion respond to meaningful state changes.
- Insights feels analytical without becoming a dashboard wall.
- Accessibility does not depend on color alone.
- New screens can be assembled from existing Momentum components without inventing new visual patterns.

---

## Design lock

**Design System v1.0 is locked.**

From this point forward, implementation should proceed screen-by-screen against this document.

If the real emulator exposes something that feels wrong, we will treat that as a design-system validation issue, discuss it, and deliberately amend the system rather than introducing one-off exceptions.
