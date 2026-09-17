package com.momentum.app.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Restrained semantic palettes: deep near-black backgrounds, quietly elevated
 * surfaces, muted secondary text with solid contrast. Accent hues carry each
 * theme's identity; brightness is reserved for progress and state.
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
    backgroundTop = Color(0xFF101024),
    backgroundBottom = Color(0xFF0A0A18),
    surface = Color(0xFF171731),
    surfaceVariant = Color(0xFF222244),
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF062A26),
    secondary = Color(0xFFA78BFA),
    accent = Color(0xFFFF8A7A),
    textPrimary = Color(0xFFF4F2FF),
    textSecondary = Color(0xFFC2BEDB),
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
    backgroundTop = Color(0xFF051E2B),
    backgroundBottom = Color(0xFF020D15),
    surface = Color(0xFF0A2C3D),
    surfaceVariant = Color(0xFF0F3A4E),
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF032635),
    secondary = Color(0xFF2DD4BF),
    accent = Color(0xFFFBBF24),
    textPrimary = Color(0xFFF0F9FF),
    textSecondary = Color(0xFFB4CFDD),
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
    backgroundTop = Color(0xFF2C1025),
    backgroundBottom = Color(0xFF120710),
    surface = Color(0xFF3A1A2E),
    surfaceVariant = Color(0xFF4C2439),
    primary = Color(0xFFFFB25E),
    onPrimary = Color(0xFF3A1C00),
    secondary = Color(0xFFFF6B8B),
    accent = Color(0xFFFFD166),
    textPrimary = Color(0xFFFFF3EA),
    textSecondary = Color(0xFFE2C4BC),
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
    backgroundTop = Color(0xFF0A2117),
    backgroundBottom = Color(0xFF050F0B),
    surface = Color(0xFF102B1E),
    surfaceVariant = Color(0xFF173D28),
    primary = Color(0xFF86EFAC),
    onPrimary = Color(0xFF052E1B),
    secondary = Color(0xFFA3E635),
    accent = Color(0xFFFBBF24),
    textPrimary = Color(0xFFF0FDF4),
    textSecondary = Color(0xFFB7D4C1),
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
    backgroundTop = Color(0xFF211A37),
    backgroundBottom = Color(0xFF0F0B1A),
    surface = Color(0xFF2C2347),
    surfaceVariant = Color(0xFF3A2F5C),
    primary = Color(0xFFC4B5FD),
    onPrimary = Color(0xFF2E1065),
    secondary = Color(0xFFF0ABFC),
    accent = Color(0xFF5EEAD4),
    textPrimary = Color(0xFFF5F0FF),
    textSecondary = Color(0xFFCCC3E8),
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
    backgroundTop = Color(0xFF0C1322),
    backgroundBottom = Color(0xFF04060B),
    surface = Color(0xFF141C2E),
    surfaceVariant = Color(0xFF1E2942),
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF0B1B33),
    secondary = Color(0xFF67E8F9),
    accent = Color(0xFFF0ABFC),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFAEBDD4),
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
    backgroundTop = Color(0xFF1C1035),
    backgroundBottom = Color(0xFF0A0512),
    surface = Color(0xFF281A47),
    surfaceVariant = Color(0xFF36245C),
    primary = Color(0xFFF0ABFC),
    onPrimary = Color(0xFF3B0764),
    secondary = Color(0xFF67E8F9),
    accent = Color(0xFFFBBF24),
    textPrimary = Color(0xFFFAF5FF),
    textSecondary = Color(0xFFCFC1EA),
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
    backgroundTop = Color(0xFF0E262D),
    backgroundBottom = Color(0xFF051115),
    surface = Color(0xFF15353E),
    surfaceVariant = Color(0xFF1E4650),
    primary = Color(0xFF99F6E4),
    onPrimary = Color(0xFF04211B),
    secondary = Color(0xFFBAE6FD),
    accent = Color(0xFFFDE68A),
    textPrimary = Color(0xFFECFEFF),
    textSecondary = Color(0xFFB2CFD7),
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

/** Launch gallery (9 concepts × light/dark). New theme = one data entry + previews. */
val MomentumThemes = listOf(
    MomentumThemeDefinition("twilight", "Twilight", TwilightLight, TwilightDark),
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

/** True for the deep dark palettes; drives glass border/highlight choices. */
val MomentumColors.isDark: Boolean
    get() = backgroundBottom.luminance() < 0.25f
