package com.momentum.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.momentum.app.domain.model.DarkModeSetting

val LocalMomentumColors = staticCompositionLocalOf { AuroraDark }

object MomentumThemeColors {
    val current: MomentumColors
        @Composable get() = LocalMomentumColors.current
}

/**
 * Theme entry point (Design System v1.0 §3). Two first-class environments —
 * Light Aurora and Dark Aurora — selected by [darkMode]
 * (system / light / dark). Screens read semantic tokens from
 * [LocalMomentumColors] — never hardcode colors. [themeId] comes from
 * [com.momentum.app.domain.model.AppSettings] and resolves through
 * [themeById].
 */
@Composable
fun MomentumTheme(
    themeId: String = "aurora",
    darkMode: DarkModeSetting = DarkModeSetting.SYSTEM,
    systemDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val definition = themeById(themeId)
    val darkTheme = when (darkMode) {
        DarkModeSetting.SYSTEM -> systemDark
        DarkModeSetting.LIGHT -> false
        DarkModeSetting.DARK -> true
    }
    val momentum = if (darkTheme) definition.dark else definition.light
    val material = if (darkTheme) {
        darkColorScheme(
            primary = momentum.accentPrimary,
            onPrimary = momentum.onAccent,
            secondary = momentum.accentSecondary,
            onSecondary = momentum.onAccent,
            tertiary = momentum.accentTertiary,
            background = momentum.background,
            onBackground = momentum.textPrimary,
            surface = momentum.surface,
            onSurface = momentum.textPrimary,
            surfaceVariant = momentum.surfaceSoft,
            onSurfaceVariant = momentum.textSecondary,
            error = momentum.danger,
            onError = momentum.background,
            outline = momentum.borderStrong,
            outlineVariant = momentum.borderSubtle,
        )
    } else {
        lightColorScheme(
            primary = momentum.accentPrimary,
            onPrimary = momentum.onAccent,
            secondary = momentum.accentSecondary,
            onSecondary = momentum.onAccent,
            tertiary = momentum.accentTertiary,
            background = momentum.background,
            onBackground = momentum.textPrimary,
            surface = momentum.surface,
            onSurface = momentum.textPrimary,
            surfaceVariant = momentum.surfaceSoft,
            onSurfaceVariant = momentum.textSecondary,
            error = momentum.danger,
            onError = momentum.surfaceElevated,
            outline = momentum.borderStrong,
            outlineVariant = momentum.borderSubtle,
        )
    }
    CompositionLocalProvider(LocalMomentumColors provides momentum) {
        MaterialTheme(
            colorScheme = material,
            typography = MomentumTypography,
            shapes = MomentumShapes.material,
            content = content,
        )
    }
}
