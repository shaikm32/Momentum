package com.momentum.app.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumTheme
import kotlin.math.cos
import kotlin.math.roundToInt

/**
 * Refined progress ring: thin track, solid accent arc, emphasized number.
 * Compact by default — progress should inform, not dominate. Single efficient
 * Canvas draw; sweep animates with a spring, glow only when [glowEnabled]
 * (both disabled under reduced-motion via [animate] / [glowEnabled]).
 *
 * [showPercent] is turned off when there is nothing eligible to report, so the
 * ring never implies a percentage that would be misleading
 * (DASHBOARD_SPECS §7).
 *
 * [arcBrush] is the **one** additive parameter (ADR-48 Amendment Note 5): a
 * gradient arc for the horizontal progress card, whose approved ring gradient
 * runs `#00a572 → #06b6d4 → #7c3aed` — three *existing* Aurora theme tokens
 * (`success`, `auroraBlue`, `auroraViolet`). It replaces only the solid arc
 * colour; the track, the stroke width, the halo, the lobes and the draw order
 * are untouched, and `null` is exactly the previous solid `c.primary` arc, so
 * all existing call sites are unchanged.
 *
 * **Aurora glow (Phase 2C second pass).** The glow is now a *living* light
 * with two parts driven by the one [rememberGlowPhase] clock:
 *  - a wide halo under the arc whose alpha breathes once per cycle
 *    ([GlowHaloBase] ± [GlowHaloBreath]), so the ring reads as illuminated
 *    rather than painted;
 *  - two soft lobes orbiting the completed arc — one lifted from
 *    [MomentumColors.primary], one from [MomentumColors.secondary], both the
 *    same aurora tints the theme already uses — so brightness *and* hue vary
 *    around the ring, which is what makes it read as aurora instead of a
 *    travelling highlight.
 * Nothing about the geometry moves: the track, the sweep and the number are
 * exactly what they were, the lobes are painted only where the completed arc
 * already is, and 0% draws no glow at all. The draw order is **halo → lobes →
 * primary arc** (ISSUE-014), so the glow is a layer *under* the arc and can
 * only ever extend past it — never veil the completed sweep. The brush is
 * remembered (a ShaderBrush rebuilds only on theme change) and the breathing
 * alpha is a `Color` value-class copy, so the loop allocates nothing per
 * frame. Under reduced motion the glow is not composed at all and the ring is
 * static.
 */
@Composable
fun ProgressOrb(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    glowEnabled: Boolean = true,
    animate: Boolean = true,
    showPercent: Boolean = true,
    arcBrush: Brush? = null,
) {
    val c = LocalMomentumColors.current
    val target = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "orbSweep",
    )
    val sweep = if (animate) animated else target
    // The glow only exists where there is something completed to glow, so 0%
    // never pays for (or renders) an animation.
    val glowPhase = rememberGlowPhase(glowEnabled && sweep > 0.001f)
    // Two lobes, both theme-derived: the arc colour and its aurora sibling,
    // each lifted toward white — a light travelling along the ring, never a
    // colour shift outside the palette. Built once per theme.
    val lobePrimary = remember(c.primary) { lerp(c.primary, Color.White, GlowHighlightMix) }
    val lobeSecondary = remember(c.secondary) { lerp(c.secondary, Color.White, GlowHighlightMix) }
    val glowBrush = remember(lobePrimary, lobeSecondary) {
        Brush.sweepGradient(
            0f to lobePrimary.copy(alpha = GlowHighlightAlpha),
            GlowPeakEnd to Color.Transparent,
            0.5f - GlowPeakEnd to Color.Transparent,
            0.5f to lobeSecondary.copy(alpha = GlowHighlightAlpha),
            0.5f + GlowPeakEnd to Color.Transparent,
            1f - GlowPeakEnd to Color.Transparent,
            1f to lobePrimary.copy(alpha = GlowHighlightAlpha),
        )
    }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val stroke = size.toPx() * 0.075f
            val diameter = size.toPx() - stroke
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            // Track: a *foreground*-derived tint, so it keeps its 3:1 against
            // the Aurora in both themes. A surface token (surfaceVariant) read
            // as a stroke over artwork — it was ~1:1 against the pale light
            // art, i.e. no visible track at all (ISSUE-014, AGENTS §5).
            drawArc(
                color = c.textPrimary.copy(alpha = TrackAlpha),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
            if (sweep > 0.001f) {
                if (glowEnabled) {
                    // Breathing halo: one slow swell per ambient cycle. A
                    // value-class Color copy — no object is allocated per frame.
                    val breath = 0.5f - 0.5f * cos(glowPhase * GlowTau)
                    drawArc(
                        color = c.glow.copy(alpha = GlowHaloBase + GlowHaloBreath * breath),
                        startAngle = -90f,
                        sweepAngle = 360f * sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(stroke * GlowHaloWidth, cap = StrokeCap.Round),
                    )
                }
                if (glowEnabled) {
                    // The brush's two lobes sit at 3 and 9 o'clock; the canvas
                    // is rotated so they orbit, while the arc's local start
                    // angle is counter-shifted so the drawn geometry stays
                    // exactly on the completed sweep. One revolution per cycle:
                    // on a partial ring the lobes sweep in across the finished
                    // arc and leave it again (nothing is drawn outside, so the
                    // loop never pops), and on a full ring it is one
                    // uninterrupted circuit.
                    val spot = -90f + 360f * glowPhase
                    rotate(degrees = spot) {
                        drawArc(
                            brush = glowBrush,
                            startAngle = -90f - spot,
                            sweepAngle = 360f * sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(stroke * 1.35f, cap = StrokeCap.Round),
                        )
                    }
                }
                // Drawn LAST: halo → lobes → arc (ISSUE-014), so the crisp
                // arc is never painted over by a glow layer and the glow can
                // only ever extend *beyond* the arc stroke, never veil it.
                //
                // `arcBrush` is the one additive parameter (ADR-48 Amendment
                // Note 5): the horizontal progress card's approved gradient arc.
                // `null` is `SolidColor(c.primary)` — byte-identical to the
                // pre-TASK-018 call, and every existing caller passes nothing.
                drawArc(
                    brush = arcBrush ?: SolidColor(c.primary),
                    startAngle = -90f,
                    sweepAngle = 360f * sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(stroke, cap = StrokeCap.Round),
                )
            }
        }
        if (showPercent) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(sweep * 100).roundToInt()}%",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = c.textPrimary,
                )
            }
        }
    }
}

/**
 * Position of the orbiting glow, 0..1, on one slow seamless loop. Only
 * composed while [enabled]: a disabled glow costs no animation at all, which is
 * how reduced motion and 0% are kept completely static.
 */
@Composable
private fun rememberGlowPhase(enabled: Boolean): Float =
    if (!enabled) {
        0f
    } else {
        rememberInfiniteTransition(label = "orbGlow").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = MomentumMotion.ambient, easing = LinearEasing),
            ),
            label = "orbGlowPhase",
        ).value
    }

/**
 * Alpha of the track, applied to the foreground ink token
 * (`MomentumThemeColors.textPrimary`) so the stroke is derived from the copy's
 * ink rather than from a surface token used as a line over artwork.
 *
 * The binding requirement is ≥ 3:1 against the Aurora in *both* themes
 * (ISSUE-014). At the suggested 0.25 the light theme measures only ~1.6:1
 * against the veiled artwork (WCAG relative luminance: light artwork plus the
 * 0.30 content veil sits around L 0.62, and a 25 % ink wash over it lands
 * around L 0.42), so the alpha is raised until the ratio clears 3:1: 0.7
 * measures ≈ 3.24:1 in Light (worst case, brightest artwork) and ≈ 4.3:1 in
 * Dark. Note that `ProgressOrb` has a **single production caller** (Home's
 * progress card), so this is a Home-only change in effect (ADR-49 Amendment
 * Note 5). Same token, same thin stroke — only the alpha differs, because the
 * criterion (3:1), not the example number, is what has to hold.
 */
private const val TrackAlpha = 0.7f

/** Peak alpha of each orbiting lobe — a lift, not a neon flare. */
private const val GlowHighlightAlpha = 0.42f

/** How far the lobes lift their aurora colour toward white. */
private const val GlowHighlightMix = 0.62f

/**
 * Fraction of the sweep at which a lobe has faded to nothing on either side of
 * its peak, i.e. each lobe is about 130 deg wide with a clear gap between the
 * two.
 */
private const val GlowPeakEnd = 0.18f

/** Resting alpha of the halo that sits under the completed arc. */
private const val GlowHaloBase = 0.16f

/** Half-amplitude of the halo's breath over one ambient cycle (0.16 – 0.26). */
private const val GlowHaloBreath = 0.10f

/** Halo stroke width as a multiple of the track stroke. */
private const val GlowHaloWidth = 1.9f

/** 2π, for the halo's breathing phase. */
private const val GlowTau = 6.2831853f

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
