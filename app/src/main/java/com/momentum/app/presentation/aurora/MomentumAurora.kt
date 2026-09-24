package com.momentum.app.presentation.aurora

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.isDark
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Aurora state (§27). Aurora is state-responsive, not decorative wallpaper:
 * intensity is lowest at rest and strongest at milestones.
 */
enum class AuroraState { REST, PROGRESS, COMPLETION, MILESTONE }

/** Intensity floor/ceiling per state; an explicit `intensity` overrides it. */
val AuroraState.defaultIntensity: Float
    get() = when (this) {
        AuroraState.REST -> 0.35f
        AuroraState.PROGRESS -> 0.55f
        AuroraState.COMPLETION -> 0.75f
        AuroraState.MILESTONE -> 1f
    }

/**
 * Programmatically generated Aurora field (§27, §32): three diffuse radial
 * light blobs drawn from the theme's Aurora palette — no bitmap assets.
 * Brushes are cached and only the blob centers drift (very slow ambient,
 * [MomentumMotion.ambient]); static under reduced motion or
 * `animated = false`. Decorative by nature: no semantics, invisible to
 * TalkBack.
 *
 * @param state conceptual state driving default intensity
 * @param intensity optional 0..1 override of [AuroraState.defaultIntensity]
 * @param animated ambient drift; automatically off under reduced motion
 */
@Composable
fun AuroraBackground(
    state: AuroraState = AuroraState.REST,
    intensity: Float? = null,
    animated: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val colors = LocalMomentumColors.current
    val reduceMotion = rememberReduceMotion(null)
    val effectiveIntensity = (intensity ?: state.defaultIntensity).coerceIn(0f, 1f)
    val animate = animated && !reduceMotion
    val phase: State<Float> = if (animate) {
        rememberInfiniteTransition(label = "aurora").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = MomentumMotion.ambient, easing = LinearEasing),
            ),
            label = "auroraPhase",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }
    val baseAlpha = (if (colors.isDark) 0.20f else 0.16f) * effectiveIntensity
    val blue = colors.auroraBlue
    val lavender = colors.auroraLavender
    val peach = colors.auroraPeach

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val w = size.width
                    val h = size.height
                    val r1 = w * 0.80f
                    val r2 = w * 0.65f
                    val r3 = w * 0.85f
                    fun brush(color: Color, alpha: Float, radius: Float): Brush =
                        Brush.radialGradient(
                            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                            center = Offset.Zero,
                            radius = radius.coerceAtLeast(1f),
                        )
                    val brushBlue = brush(blue, baseAlpha, r1)
                    val brushLavender = brush(lavender, baseAlpha * 0.85f, r2)
                    val brushPeach = brush(peach, baseAlpha * 0.55f, r3)
                    onDrawBehind {
                        if (w <= 0f || h <= 0f) return@onDrawBehind
                        val p = phase.value
                        val driftX = (sin(p * 2.0 * PI) * w * 0.03).toFloat()
                        val driftY = (cos(p * 2.0 * PI) * h * 0.025).toFloat()
                        val canvas = drawContext.canvas
                        canvas.save()
                        canvas.translate(w * 0.15f + driftX, h * 0.10f + driftY)
                        drawCircle(brush = brushBlue, radius = r1, center = Offset.Zero)
                        canvas.restore()
                        canvas.save()
                        canvas.translate(w * 0.88f - driftX * 0.7f, h * 0.30f + driftY)
                        drawCircle(brush = brushLavender, radius = r2, center = Offset.Zero)
                        canvas.restore()
                        canvas.save()
                        canvas.translate(w * 0.50f, h * 0.98f - driftY)
                        drawCircle(brush = brushPeach, radius = r3, center = Offset.Zero)
                        canvas.restore()
                    }
                },
        )
        content()
    }
}
