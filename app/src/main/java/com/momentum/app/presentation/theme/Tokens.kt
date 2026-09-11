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
