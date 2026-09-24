package com.momentum.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * "Momentum — Twilight Journey" onboarding palette: the exact hexes the
 * supplied onboarding artwork was composed against, kept as constants so
 * onboarding copy and glass continue to match the art. Not a theme — the
 * v1 theme system is Light/Dark Aurora (see [AuroraLight]/[AuroraDark]).
 */
object Twilight {
    // Cool dark structure.
    val MidnightNavy = Color(0xFF080D27)
    val DeepIndigo = Color(0xFF11183D)

    // Violet depth.
    val DeepViolet = Color(0xFF30204F)
    val VioletHaze = Color(0xFF50356F)
    val MutedPurple = Color(0xFF6D4D83)

    // Warm atmosphere (accents only).
    val DuskyRose = Color(0xFFA75D78)
    val SunsetCoral = Color(0xFFD8796B)
    val WarmPeach = Color(0xFFF3A276)
    val GoldenAmber = Color(0xFFF7C878)

    // Light.
    val Moonlight = Color(0xFFFFF4E5)

    /** Muted secondary text on dark (pale mauve-gray, contrast-safe). */
    val PaleMauve = Color(0xFFCBB9C4)

    /** Deep warm bark: `onPrimary` ink over peach/gold actions. */
    val EmberInk = Color(0xFF2A1408)

    /** Deep plum ink: light-theme body text. */
    val PlumInk = Color(0xFF2A1A33)

    /** Warm bronze: light-theme primary actions (contrast-safe on paper). */
    val WarmBronze = Color(0xFFB4532A)
}
