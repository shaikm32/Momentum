package com.momentum.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Phase 0 theme foundation. Phase 1 builds the full gallery;
 * Phase 10 makes this data-driven + persisted via DataStore.
 *
 * Aurora is the default launch theme: deep indigo-violet canvas,
 * mint-cyan primary, warm coral accent. Calm, premium, expressive.
 */
data class MomentumColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val glow: Color,
)

val AuroraDark = MomentumColors(
    backgroundTop = Color(0xFF141433),
    backgroundBottom = Color(0xFF0A0A1A),
    surface = Color(0xFF1E1E42),
    surfaceVariant = Color(0xFF2A2A55),
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF062A26),
    secondary = Color(0xFFA78BFA),
    accent = Color(0xFFFF8A7A),
    textPrimary = Color(0xFFF4F2FF),
    textSecondary = Color(0xFFB8B4D4),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFF5EEAD4),
)

val AuroraLight = MomentumColors(
    backgroundTop = Color(0xFFF0EDFF),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEDE9FE),
    primary = Color(0xFF0D9488),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF7C3AED),
    accent = Color(0xFFF4543A),
    textPrimary = Color(0xFF191736),
    textSecondary = Color(0xFF5B5878),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFF0D9488),
)
