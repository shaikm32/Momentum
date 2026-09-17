package com.momentum.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * "Momentum — Twilight Journey" palette. Source of truth for the twilight
 * visual identity: cinematic deep-indigo/violet structure (≈80% cool/dark)
 * with restrained sunset warmth (≈20% luminous accents).
 *
 * Core principle: the environment is expressive, the UI is quiet. Warm hues
 * are atmospheric accents (glows, progress light, CTA energy); cool darks
 * carry structure and depth. Nothing neon, nothing gamified.
 *
 * Semantic mapping onto [MomentumColors] slots (see [TwilightDark]):
 * - backgroundPrimary/Secondary → backgroundTop/Bottom
 * - surfaceGlass/surfaceGlassElevated → surface/surfaceVariant
 * - textPrimary/textSecondary → Moonlight / PaleMauve
 * - accentWarm/accentGold/accentRose → primary/accent/secondary family
 * - accentViolet → muted-purple depth (surfaceVariant, secondary)
 * - iconPrimary/iconSecondary → textPrimary/textSecondary
 * - success/warning/error → unchanged functional hues
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

/** Twilight Journey, dark (hero experience): indigo structure, peach light. */
val TwilightDark = MomentumColors(
    backgroundTop = Twilight.DeepIndigo,
    backgroundBottom = Twilight.MidnightNavy,
    surface = Twilight.DeepIndigo,
    surfaceVariant = Twilight.DeepViolet,
    primary = Twilight.WarmPeach,
    onPrimary = Twilight.EmberInk,
    secondary = Twilight.MutedPurple,
    accent = Twilight.SunsetCoral,
    textPrimary = Twilight.Moonlight,
    textSecondary = Twilight.PaleMauve,
    success = Color(0xFF4ADE80),
    warning = Twilight.GoldenAmber,
    error = Color(0xFFF87171),
    glow = Twilight.WarmPeach,
)

/** Twilight Journey, light (warm paper): bronze structure, plum ink. */
val TwilightLight = MomentumColors(
    backgroundTop = Color(0xFFF7EEDF),
    backgroundBottom = Color(0xFFFFF9F1),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF3E4CF),
    primary = Twilight.WarmBronze,
    onPrimary = Color(0xFFFFFFFF),
    secondary = Twilight.MutedPurple,
    accent = Color(0xFFC05B4D),
    textPrimary = Twilight.PlumInk,
    textSecondary = Color(0xFF7C6370),
    success = Color(0xFF16A34A),
    warning = Color(0xFFB45309),
    error = Color(0xFFDC2626),
    glow = Twilight.WarmBronze,
)
