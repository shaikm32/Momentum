package com.momentum.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumTheme
import kotlin.math.roundToInt

/**
 * Refined progress ring: thin track, solid accent arc, emphasized number.
 * Compact by default — progress should inform, not dominate. Single efficient
 * Canvas draw; sweep animates with a spring, glow only when [glowEnabled]
 * (disabled under reduced-motion).
 */
@Composable
fun ProgressOrb(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    glowEnabled: Boolean = true,
) {
    val c = LocalMomentumColors.current
    val sweep by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "orbSweep",
    )
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val stroke = size.toPx() * 0.075f
            val diameter = size.toPx() - stroke
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            // Track.
            drawArc(
                color = c.surfaceVariant.copy(alpha = 0.6f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
            if (sweep > 0.001f) {
                if (glowEnabled) {
                    drawArc(
                        color = c.glow.copy(alpha = 0.22f),
                        startAngle = -90f,
                        sweepAngle = 360f * sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(stroke * 1.8f, cap = StrokeCap.Round),
                    )
                }
                drawArc(
                    color = c.primary,
                    startAngle = -90f,
                    sweepAngle = 360f * sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(stroke, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(sweep * 100).roundToInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = c.textPrimary,
            )
        }
    }
}

@Preview(name = "Orb light", showBackground = true)
@Composable
private fun ProgressOrbLightPreview() {
    MomentumTheme(themeId = "aurora", darkMode = com.momentum.app.domain.model.DarkModeSetting.LIGHT) {
        ProgressOrb(progress = 0.62f, glowEnabled = false)
    }
}

@Preview(name = "Orb dark", showBackground = true)
@Composable
private fun ProgressOrbDarkPreview() {
    MomentumTheme(themeId = "aurora", darkMode = com.momentum.app.domain.model.DarkModeSetting.DARK) {
        ProgressOrb(progress = 1f)
    }
}
