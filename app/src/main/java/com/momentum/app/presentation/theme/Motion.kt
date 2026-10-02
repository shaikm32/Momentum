package com.momentum.app.presentation.theme

import androidx.compose.animation.core.CubicBezierEasing

/**
 * Motion tokens (§28). Motion has meaning and never delays the user:
 * ease-out for entering, ease-in-out for state transitions, spring only
 * where physical response adds meaning.
 *
 * Combine durations with `rememberReduceMotion` (or a Settings override)
 * — when reduced motion is enabled, remove decorative movement and keep
 * only meaningful state changes.
 */
object MomentumMotion {
    /** Fast: micro-interactions (press feedback, small state flips). */
    const val fast = 180

    /** Standard: screen transitions and content movement. */
    const val standard = 280

    /** Emphasis: meaningful state changes (completion forming). */
    const val emphasis = 450

    /** Ambient: very slow decorative loops (Aurora drift). */
    const val ambient = 6000

    /**
     * Enter: Home's single entrance clock (ADR-48 Amendment Note 4 /
     * DECISIONS §28). Derived from the reference's own `fade-slide-up` stagger:
     * four blocks at 0 / 100 / 200 / 300 ms, so the whole reveal spans 900 ms.
     * Added by TASK-018 — it did not exist before.
     */
    const val enter = 900

    /**
     * FAB glow: the reference's `fab-glow` box-shadow pulse, 3 s `ease-in-out`
     * (ADR-48 Amendment Note 6). Added by TASK-018 — it did not exist before.
     */
    const val fabGlow = 3000

    /**
     * Verified glyph: the reference's `bolt-pulse`, 2.2 s `ease-in-out`
     * (ADR-48 Amendment Note 6). Added by TASK-018 — it did not exist before.
     */
    const val verifiedPulse = 2200

    /**
     * Reward: the one-shot completion moment (Home's 100% milestone).
     * Derived from the design system §28 "Reward ~650 ms". Added by the
     * TASK-015 refinement (ADR-48 Amendment Note 1) — it did not exist
     * before.
     */
    const val reward = 650

    /** Entering: smooth decelerate. */
    val easeOut = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
}

/**
 * Legacy duration names — delegate to [MomentumMotion] so unmigrated call
 * sites stay on the single token source.
 */
object MomentumDurations {
    val fast: Int get() = MomentumMotion.fast
    val medium: Int get() = MomentumMotion.standard
}
