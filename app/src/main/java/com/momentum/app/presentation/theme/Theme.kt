package com.momentum.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalMomentumColors = staticCompositionLocalOf { AuroraDark }

object MomentumThemeColors {
    val current: MomentumColors
        @Composable get() = LocalMomentumColors.current
}

/**
 * Phase 0 theme entry point. Wraps Material3 so the full design system
 * (Phase 1) has a single seam to extend. No hardcoded colors in screens —
 * read from [LocalMomentumColors].
 */
@Composable
fun MomentumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val momentum = if (darkTheme) AuroraDark else AuroraLight
    val material = if (darkTheme) {
        darkColorScheme(
            primary = momentum.primary,
            onPrimary = momentum.onPrimary,
            secondary = momentum.secondary,
            tertiary = momentum.accent,
            background = momentum.backgroundBottom,
            surface = momentum.surface,
            surfaceVariant = momentum.surfaceVariant,
            onBackground = momentum.textPrimary,
            onSurface = momentum.textPrimary,
            error = momentum.error,
        )
    } else {
        lightColorScheme(
            primary = momentum.primary,
            onPrimary = momentum.onPrimary,
            secondary = momentum.secondary,
            tertiary = momentum.accent,
            background = momentum.backgroundBottom,
            surface = momentum.surface,
            surfaceVariant = momentum.surfaceVariant,
            onBackground = momentum.textPrimary,
            onSurface = momentum.textPrimary,
            error = momentum.error,
        )
    }
    CompositionLocalProvider(LocalMomentumColors provides momentum) {
        MaterialTheme(
            colorScheme = material,
            typography = MomentumTypography,
            content = content,
        )
    }
}
