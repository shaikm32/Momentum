package com.momentum.app.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Momentum semantic color tokens (Design System v1.0 §3). Two first-class
 * environments — Light Aurora and Dark Aurora — share this exact contract.
 * Screens read tokens, never raw hex.
 *
 * Pre-Aurora slot names ([backgroundTop], [primary], [accent], [error], …)
 * survive as computed aliases so screens that have not yet been migrated to
 * the v1 token names keep compiling and rendering.
 */
data class MomentumColors(
    // —— Surfaces ——
    val background: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val surfaceElevated: Color,
    val surfaceInteractive: Color,

    // —— Text ——
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textDisabled: Color,

    // —— Status ——
    val success: Color,
    val warning: Color,
    val danger: Color,

    // —— Borders ——
    val borderSubtle: Color,
    val borderStrong: Color,

    // —— Progress ——
    val progressTrack: Color,

    // —— Aurora palette (also the accent family) ——
    val auroraBlue: Color,
    val auroraLavender: Color,
    val auroraViolet: Color,
    val auroraMint: Color,
    val auroraPeach: Color,

    /** Ink for content placed on an accent-filled control (contrast-checked). */
    val onAccent: Color,
) {
    val accentPrimary: Color get() = auroraBlue
    val accentSecondary: Color get() = auroraLavender
    val accentTertiary: Color get() = auroraViolet
    val progressFill: Color get() = auroraBlue
    val glow: Color get() = auroraBlue

    // Legacy pre-v1 slot aliases (removed as screens migrate).

    val backgroundTop: Color get() = background
    val backgroundBottom: Color get() = background
    val surfaceVariant: Color get() = surfaceSoft
    val primary: Color get() = accentPrimary
    val onPrimary: Color get() = onAccent
    val secondary: Color get() = accentSecondary
    val accent: Color get() = accentTertiary
    val error: Color get() = danger
}

/**
 * Light Aurora — bright paper canvas, soft tonal surfaces, Aurora Blue
 * primary. Light leans on tonal contrast and very soft shadows (§7).
 */
val AuroraLight = MomentumColors(
    background = Color(0xFFF7F8FC),
    surface = Color(0xFFFFFFFF),
    surfaceSoft = Color(0xFFF1F2FB),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceInteractive = Color(0xFFE8EBF7),
    textPrimary = Color(0xFF172033),
    textSecondary = Color(0xFF596579),
    textMuted = Color(0xFF8993A4),
    textDisabled = Color(0xFFB8BFCA),
    success = Color(0xFF1F8A70),
    warning = Color(0xFFB45309),
    danger = Color(0xFFC74545),
    borderSubtle = Color(0xFFE4E7F1),
    borderStrong = Color(0xFFC7CDDA),
    progressTrack = Color(0xFFE7EAF4),
    auroraBlue = Color(0xFF5C9FEF),
    auroraLavender = Color(0xFF9278E8),
    auroraViolet = Color(0xFF8064DD),
    auroraMint = Color(0xFF55C8AD),
    auroraPeach = Color(0xFFEAA477),
    onAccent = Color(0xFF172033),
)

/**
 * Dark Aurora — near-black ink canvas, quiet tonal surfaces, luminous
 * Aurora accents. Dark leans on tonal contrast, subtle borders, and
 * atmospheric glow rather than shadows (§7).
 */
val AuroraDark = MomentumColors(
    background = Color(0xFF080D18),
    surface = Color(0xFF111827),
    surfaceSoft = Color(0xFF151E30),
    surfaceElevated = Color(0xFF172033),
    surfaceInteractive = Color(0xFF1D2940),
    textPrimary = Color(0xFFF5F7FC),
    textSecondary = Color(0xFFAEB8CA),
    textMuted = Color(0xFF737F95),
    textDisabled = Color(0xFF4D586B),
    success = Color(0xFF76DFC4),
    warning = Color(0xFFFFC49B),
    danger = Color(0xFFFF8E8E),
    borderSubtle = Color(0xFF1C2740),
    borderStrong = Color(0xFF2A3854),
    progressTrack = Color(0xFF1D2940),
    auroraBlue = Color(0xFF78B7FF),
    auroraLavender = Color(0xFFB59CFF),
    auroraViolet = Color(0xFF9B7CFF),
    auroraMint = Color(0xFF76DFC4),
    auroraPeach = Color(0xFFFFC49B),
    onAccent = Color(0xFF080D18),
)

/** One theme definition: the same token contract in both environments. */
data class MomentumThemeDefinition(
    val id: String,
    val name: String,
    val light: MomentumColors,
    val dark: MomentumColors,
)

/**
 * The v1 registry holds the single Aurora environment. The legacy `theme_id`
 * DataStore value resolves through [themeById] to this entry, so persisted
 * user choices keep working without a data migration.
 */
val MomentumThemes = listOf(
    MomentumThemeDefinition("aurora", "Aurora", AuroraLight, AuroraDark),
)

fun themeById(id: String): MomentumThemeDefinition =
    MomentumThemes.firstOrNull { it.id == id } ?: MomentumThemes.first()

/** True for the Dark Aurora environment; drives glass border/highlight choices. */
val MomentumColors.isDark: Boolean
    get() = background.luminance() < 0.25f

/**
 * Muted jewel icon accents for the habit-creation cards. One consistent
 * rounded-badge treatment everywhere: the glyph carries the hue, the badge
 * carries it at low alpha, cards stay dark glass. Centralized here so
 * screens never hardcode colors.
 */
object HabitIconAccents {
    /** Warm amber/gold — reading. */
    val amber = Color(0xFFE3A63D)

    /** Energetic teal — exercise. */
    val teal = Color(0xFF4FB8A8)

    /** Soft lavender — meditation. */
    val lavender = Color(0xFFB39DDB)

    /** Cyan/blue — water, screen time. */
    val cyan = Color(0xFF5BB8E8)

    /** Golden yellow — learning, spending. */
    val golden = Color(0xFFE5C548)

    /** Peach/orange — primary accent family (Build, custom habits). */
    val peach = Color(0xFFF2A488)

    /** Muted plum — Reduce direction, social media. */
    val plum = Color(0xFFC08FB8)
}

/**
 * Accent hue for a persisted habit/activity icon key. Same muted-jewel
 * family as [HabitIconAccents], keyed by `iconKey` so list tiles, hero
 * tiles, and creation cards read as one icon language. Unknown keys fall
 * back to peach.
 */
fun habitIconAccent(iconKey: String): Color = when (iconKey) {
    "water", "home", "timer" -> HabitIconAccents.cyan
    "fitness", "health" -> HabitIconAccents.teal
    "mind", "meds", "sleep" -> HabitIconAccents.lavender
    "learning", "food", "mail" -> HabitIconAccents.amber
    "school", "work", "list", "date" -> HabitIconAccents.golden
    "people", "music" -> HabitIconAccents.plum
    "streak" -> HabitIconAccents.amber
    else -> HabitIconAccents.peach
}
