# Momentum — Design System

> Status: Phase 0 direction. Components built in Phase 1.

## 1. Personality

Premium · modern · playful · calming · expressive · fluid · fast · rewarding.
Fresh identity with the polish and fluidity of premium iOS apps, but natively
Android (Material 3 as the floor, never the ceiling). Do NOT copy Apple UI.

Anti-patterns (regressions, not progress): checkbox lists, boring Material cards,
default FAB-centric layouts, generic dashboards, default progress indicators,
excessive gray surfaces.

## 2. Foundations

- **Typography (expressive):** large display headlines (Today greeting, % heroes),
  semibold section titles, tabular numerals for stats. System fonts in Phase 0;
  bundled display font evaluated in Phase 1 (offline asset, no downloads).
- **Color:** per-theme 12-slot palette (`MomentumColors`): background pair,
  surface + variant, primary, secondary, accent, text pair, success/warning/error,
  glow. 12 launch themes: Aurora (default), Ocean, Sunset, Midnight, Lavender,
  Forest, Cosmic, Arctic, Cherry Blossom, Neon, Monochrome, Desert. Light + dark
  each; body contrast ≥ 4.5:1 both modes.
- **Surfaces:** layered gradients (vertical canvas wash), translucent layered
  cards (`surface` @ ~72% over gradient), subtle depth via tonal elevation, **glow
  used sparingly** (progress orbs, active streaks — never body text). Tasteful
  glass/blur only where performant (and disabled under reduced-motion/low-end).
- **Icons:** rounded, filled, friendly. Curated set mapped by `iconKey`; no emoji
  as icons in production UI.
- **Shape:** 20–28 dp cards, full-round pills for chips/buttons, circular orbs.

## 3. Motion

- Micro-interactions 150–300 ms eases: completion pop, streak count-up,
  progress-ring sweep, theme crossfade.
- Shared-element / container-transform for Today ↔ details continuity.
- Never force animation: respect system reduced-motion (disable glow pulse,
  count-ups, blur). No animation blocks input or delays data.

## 4. Component strategy (build in Phase 1; use everywhere after)

| Component | Role |
|---|---|
| `AppBackground` ✅ (Phase 0) | gradient canvas every screen sits on |
| `ActivityCard` | rich row: icon tile, title, streak flame, progress, complete affordance |
| `ProgressOrb` | glowing ring w/ % hero |
| `CompletionButton` | big satisfying toggle (≥48 dp), haptic + pop |
| `StatCard` / `InsightCard` | metric tiles, trend callouts |
| `ThemePreviewCard` | mini palette + gradient preview in gallery |
| `AnimatedChart` | weekly bars / trend line (custom draw, no chart lib in v1) |
| `CalendarHeatmap` | month grid intensity |
| `CategoryChip` | filter pills |
| `StreakBadge` | flame + count, celebratory at milestones |
| `EmptyState` | rewarding illustration-less empty w/ CTA (never a blank list) |
| `IconPicker` / `RecurrenceSelector` / `ReminderSelector` | creation-flow pickers (Phase 5) |

Rules: reusable component → `presentation/components` + light/dark `@Preview`
with preview-local fake data. Screens compose components; components hold zero
business logic. **No hardcoded colors** — `LocalMomentumColors` only.

## 5. Screens (visual intent)

- **Today:** greeting + date hero, gradient "momentum" banner, progress orb,
  activity cards ordered by priority/schedule, celebration on 100%.
- **Activities:** grouped by category/status, search/filter chips, not a plain list.
- **Insights:** stat cards row, animated weekly chart, trends, category breakdown.
- **Calendar:** heatmap month + selected-day detail sheet.
- **Settings:** theme entry, week-start, reduce-motion, about/privacy.
