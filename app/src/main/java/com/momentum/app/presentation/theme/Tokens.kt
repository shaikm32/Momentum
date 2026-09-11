package com.momentum.app.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Central design tokens. Screens and components read rhythm from here —
 * no scattered arbitrary dp values in composables.
 */
object MomentumSpacing {
    /** Horizontal screen margin used by every screen. */
    val screen = 20.dp

    /** Vertical gaps between major sections on a screen. */
    val section = 20.dp

    /** Gaps between cards / list items. */
    val item = 12.dp

    /** Tight gaps inside compound controls (chips, rows). */
    val group = 8.dp

    /** Internal padding of cards. */
    val card = 16.dp

    /** Compact internal padding (setting rows, list rows). */
    val row = 12.dp
}

object MomentumRadii {
    /** Default card corner radius — calm, not bubbly. */
    val card = 16.dp

    /** Controls: buttons, text fields, pickers. */
    val control = 12.dp

    /** Small surfaces: icon tiles, day cells, thumbs. */
    val small = 10.dp

    /** Dialogs and sheets. */
    val dialog = 20.dp

    /** Pills only: filter chips, badges. */
    val pill = 999.dp
}

object MomentumHeights {
    /** Primary buttons and CTA rows. */
    val button = 52.dp

    /** Minimum touch height for chips and rows (accessibility). */
    val controlMin = 48.dp

    /** Icon-only touch targets. */
    val iconTarget = 48.dp
}

object MomentumIconSizes {
    val small = 18.dp
    val medium = 20.dp
    val large = 24.dp
    val hero = 28.dp
    val tile = 22.dp
}

/** Animation durations in milliseconds — short, smooth, purposeful. */
object MomentumDurations {
    const val fast = 150
    const val medium = 250
    const val slow = 350
}

/**
 * Liquid Glass translucency + light tokens. Glass fills let the ambient
 * background breathe through; borders and top highlights sell the depth.
 * Values are alphas applied to theme slots — never hardcoded colors.
 */
object MomentumGlass {
    /** Primary hero surfaces (progress hero, dialogs, nav bar). */
    const val heroFill = 0.68f

    /** Standard cards, rows, panels. */
    const val cardFill = 0.55f

    /** Chips, badges, tiles, controls. */
    const val controlFill = 0.45f

    /** Hairline border highlight (white in dark mode). */
    const val borderDark = 0.16f

    /** Hairline border (ink) in light mode, where white is invisible. */
    const val borderLight = 0.10f

    /** Top-edge illumination streak. */
    const val highlight = 0.10f

    /** Ambient light field opacity over the base background. */
    const val ambientDark = 0.13f
    const val ambientLight = 0.16f

    /** Soft shadow elevation for floating layers (dialogs, nav, hero). */
    val dialogElevation = 16.dp
    val heroElevation = 12.dp

    /**
     * True backdrop blur is deliberately NOT used: no reliable cross-device
     * API exists on our minSdk, and no fragile dependency is worth it.
     * Depth comes from translucency + highlight + shadow + ambient light,
     * so every screen stays beautiful with blur unavailable.
     */
    val backdropBlur = 0.dp
}
