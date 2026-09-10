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
 * Theme entry point. Screens read semantic tokens from [LocalMomentumColors] —
 * never hardcode colors. Theme selection persists via DataStore (Phase 10);
 * [themeId] + [darkMode] come from [com.momentum.app.domain.model.AppSettings].
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
