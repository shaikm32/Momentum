package com.momentum.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.isDark

/**
 * Atmospheric canvas for every screen: a deep theme base with a whisper of
 * vertical depth, washed with three large, diffuse ambient light fields
 * (primary / secondary / accent at very low opacity). Calm, not colorful —
 * the light mainly shows through glass surfaces layered above it. One static
 * draw; zero animation cost.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val c = MomentumThemeColors.current
    val ambientAlpha = if (c.isDark) MomentumGlass.ambientDark else MomentumGlass.ambientLight
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to c.backgroundTop,
                    1f to c.backgroundBottom,
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Soft cyan-violet wash, upper left.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        c.primary.copy(alpha = ambientAlpha),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.12f, h * 0.06f),
                    radius = w * 0.85f,
                ),
                radius = w * 0.85f,
                center = Offset(w * 0.12f, h * 0.06f),
            )
            // Violet depth, upper right.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        c.secondary.copy(alpha = ambientAlpha * 0.9f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.92f, h * 0.22f),
                    radius = w * 0.75f,
                ),
                radius = w * 0.75f,
                center = Offset(w * 0.92f, h * 0.22f),
            )
            // Faint warm anchor near the bottom.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        c.accent.copy(alpha = ambientAlpha * 0.55f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.5f, h * 1.02f),
                    radius = w * 0.9f,
                ),
                radius = w * 0.9f,
                center = Offset(w * 0.5f, h * 1.02f),
            )
        }
        content()
    }
}
