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
