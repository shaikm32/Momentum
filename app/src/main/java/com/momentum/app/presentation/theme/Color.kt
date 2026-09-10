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

private val OceanDark = MomentumColors(
    backgroundTop = Color(0xFF062A3A),
    backgroundBottom = Color(0xFF03121C),
    surface = Color(0xFF0B3548),
    surfaceVariant = Color(0xFF11455C),
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF032635),
    secondary = Color(0xFF2DD4BF),
    accent = Color(0xFFFBBF24),
    textPrimary = Color(0xFFF0F9FF),
    textSecondary = Color(0xFFA5C4D4),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFF38BDF8),
)

private val OceanLight = MomentumColors(
    backgroundTop = Color(0xFFE0F2FE),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFD9EDFA),
    primary = Color(0xFF0284C7),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0D9488),
    accent = Color(0xFFD97706),
    textPrimary = Color(0xFF082F49),
    textSecondary = Color(0xFF4A6B80),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFF0284C7),
)

private val SunsetDark = MomentumColors(
    backgroundTop = Color(0xFF3A1630),
    backgroundBottom = Color(0xFF180A14),
    surface = Color(0xFF4A2139),
    surfaceVariant = Color(0xFF5E2C48),
    primary = Color(0xFFFFB25E),
    onPrimary = Color(0xFF3A1C00),
    secondary = Color(0xFFFF6B8B),
    accent = Color(0xFFFFD166),
    textPrimary = Color(0xFFFFF3EA),
    textSecondary = Color(0xFFD8B4AC),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFFD166),
    error = Color(0xFFF87171),
    glow = Color(0xFFFF8A5C),
)

private val SunsetLight = MomentumColors(
    backgroundTop = Color(0xFFFFE8D6),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFBE0CE),
    primary = Color(0xFFC2410C),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFDB2777),
    accent = Color(0xFFB45309),
    textPrimary = Color(0xFF431407),
    textSecondary = Color(0xFF8A5A44),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFFEA580C),
)

private val ForestDark = MomentumColors(
    backgroundTop = Color(0xFF0E2A1D),
    backgroundBottom = Color(0xFF071610),
    surface = Color(0xFF143624),
    surfaceVariant = Color(0xFF1C4A31),
    primary = Color(0xFF86EFAC),
    onPrimary = Color(0xFF052E1B),
    secondary = Color(0xFFA3E635),
    accent = Color(0xFFFBBF24),
    textPrimary = Color(0xFFF0FDF4),
    textSecondary = Color(0xFFA9C7B4),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFF86EFAC),
)

private val ForestLight = MomentumColors(
    backgroundTop = Color(0xFFDCFCE7),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFD2EDD9),
    primary = Color(0xFF15803D),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF4D7C0F),
    accent = Color(0xFFB45309),
    textPrimary = Color(0xFF052E16),
    textSecondary = Color(0xFF4D6B58),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFF15803D),
)

private val LavenderDark = MomentumColors(
    backgroundTop = Color(0xFF2A2145),
    backgroundBottom = Color(0xFF141021),
    surface = Color(0xFF372C59),
    surfaceVariant = Color(0xFF473A70),
    primary = Color(0xFFC4B5FD),
    onPrimary = Color(0xFF2E1065),
    secondary = Color(0xFFF0ABFC),
    accent = Color(0xFF5EEAD4),
    textPrimary = Color(0xFFF5F0FF),
    textSecondary = Color(0xFFBEB3E0),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFFC4B5FD),
)

private val LavenderLight = MomentumColors(
    backgroundTop = Color(0xFFEDE9FE),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2D9FB),
    primary = Color(0xFF7C3AED),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFA21CAF),
    accent = Color(0xFF0D9488),
    textPrimary = Color(0xFF2E1065),
    textSecondary = Color(0xFF6D6390),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFF7C3AED),
)

private val MidnightDark = MomentumColors(
    backgroundTop = Color(0xFF101828),
    backgroundBottom = Color(0xFF05070D),
    surface = Color(0xFF1A2438),
    surfaceVariant = Color(0xFF243152),
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF0B1B33),
    secondary = Color(0xFF67E8F9),
    accent = Color(0xFFF0ABFC),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF9FB0C9),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFF60A5FA),
)

private val MidnightLight = MomentumColors(
    backgroundTop = Color(0xFFE2E8F0),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFD8E0EC),
    primary = Color(0xFF1D4ED8),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0E7490),
    accent = Color(0xFFA21CAF),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF55677F),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFF2563EB),
)

private val CosmicDark = MomentumColors(
    backgroundTop = Color(0xFF251544),
    backgroundBottom = Color(0xFF0D0718),
    surface = Color(0xFF322058),
    surfaceVariant = Color(0xFF422C70),
    primary = Color(0xFFF0ABFC),
    onPrimary = Color(0xFF3B0764),
    secondary = Color(0xFF67E8F9),
    accent = Color(0xFFFBBF24),
    textPrimary = Color(0xFFFAF5FF),
    textSecondary = Color(0xFFC3B2E3),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFFE879F9),
)

private val CosmicLight = MomentumColors(
    backgroundTop = Color(0xFFFAE8FF),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1D4FA),
    primary = Color(0xFFA21CAF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0E7490),
    accent = Color(0xFFD97706),
    textPrimary = Color(0xFF3B0764),
    textSecondary = Color(0xFF7A6391),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFFC026D3),
)

private val ArcticDark = MomentumColors(
    backgroundTop = Color(0xFF12303A),
    backgroundBottom = Color(0xFF07171C),
    surface = Color(0xFF1A414C),
    surfaceVariant = Color(0xFF24535F),
    primary = Color(0xFF99F6E4),
    onPrimary = Color(0xFF04211B),
    secondary = Color(0xFFBAE6FD),
    accent = Color(0xFFFDE68A),
    textPrimary = Color(0xFFECFEFF),
    textSecondary = Color(0xFFA3C3CC),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    glow = Color(0xFF5EEAD4),
)

private val ArcticLight = MomentumColors(
    backgroundTop = Color(0xFFCFFAFE),
    backgroundBottom = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFC4E9F1),
    primary = Color(0xFF0E7490),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0369A1),
    accent = Color(0xFFB45309),
    textPrimary = Color(0xFF083344),
    textSecondary = Color(0xFF4A6E78),
    success = Color(0xFF16A34A),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    glow = Color(0xFF0E7490),
)

/** One data-driven theme concept: same token slots, genuinely distinct palettes. */
data class MomentumThemeDefinition(
    val id: String,
    val name: String,
    val light: MomentumColors,
    val dark: MomentumColors,
)

/** Launch gallery (8 concepts × light/dark). New theme = one data entry + previews. */
val MomentumThemes = listOf(
    MomentumThemeDefinition("aurora", "Aurora", AuroraLight, AuroraDark),
    MomentumThemeDefinition("ocean", "Ocean", OceanLight, OceanDark),
    MomentumThemeDefinition("sunset", "Sunset", SunsetLight, SunsetDark),
    MomentumThemeDefinition("forest", "Forest", ForestLight, ForestDark),
    MomentumThemeDefinition("lavender", "Lavender", LavenderLight, LavenderDark),
    MomentumThemeDefinition("midnight", "Midnight", MidnightLight, MidnightDark),
    MomentumThemeDefinition("cosmic", "Cosmic", CosmicLight, CosmicDark),
    MomentumThemeDefinition("arctic", "Arctic", ArcticLight, ArcticDark),
)

fun themeById(id: String): MomentumThemeDefinition =
    MomentumThemes.firstOrNull { it.id == id } ?: MomentumThemes.first()
