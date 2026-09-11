package com.momentum.app.presentation.screens.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import kotlin.math.sin

/**
 * Cinematic background art for onboarding.
 *
 * Why procedural instead of .webp photos: the five screens need one cohesive
 * photographic world (same palette, same light, no watermarks, no embedded
 * text), available fully offline with zero licensing risk and negligible APK
 * cost. Each scene is a deterministic layered Canvas composition sharing one
 * palette family — deep midnight blues, cyan/violet light, one warm accent —
 * so every screen feels like the same place at a different hour.
 *
 * All art is static (single draw, no animation); page-change motion is a cheap
 * GPU scale on the whole layer in [OnboardingBackdrop].
 */
enum class OnboardingScene { WELCOME, FEATURES, PROGRESS, BENEFITS, FINAL }

/** Deep navy used only for photographic readability — not a theme surface. */
private val PhotoShade = Color(0xFF060B18)

@Composable
fun OnboardingBackdrop(
    scene: OnboardingScene,
    modifier: Modifier = Modifier,
    parallax: Float = 0f,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // `parallax` is a tiny static zoom per page (1.00–1.03); applied as a
        // GPU layer transform, never a Canvas redraw.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = parallax, scaleY = parallax),
        ) {
            SceneArt(scene = scene)
        }
        // Layered treatment: atmosphere tint, bottom readability gradient,
        // top status-bar gradient, subtle vignette. Photo stays dominant.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to PhotoShade.copy(alpha = 0.30f),
                        0.18f to Color.Transparent,
                        0.48f to PhotoShade.copy(alpha = 0.28f),
                        0.74f to PhotoShade.copy(alpha = 0.62f),
                        1f to PhotoShade.copy(alpha = 0.80f),
                    ),
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, PhotoShade.copy(alpha = 0.30f)),
                    center = Offset(size.width / 2f, size.height * 0.46f),
                    radius = maxOf(size.width, size.height) * 0.72f,
                ),
                size = size,
            )
        }
        content()
    }
}

@Composable
private fun SceneArt(scene: OnboardingScene) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        when (scene) {
            OnboardingScene.WELCOME -> drawSunriseLake()
            OnboardingScene.FEATURES -> drawSoftClouds()
            OnboardingScene.PROGRESS -> drawAuroraMist()
            OnboardingScene.BENEFITS -> drawNightGarden()
            OnboardingScene.FINAL -> drawTwilightPeak()
        }
    }
}

// ---------------------------------------------------------------------------
// Shared paint helpers
// ---------------------------------------------------------------------------

/** Deterministic starfield confined to the upper [skyFraction] of the canvas. */
private fun DrawScope.stars(count: Int, skyFraction: Float, maxAlpha: Float = 0.7f) {
    repeat(count) { i ->
        val x = ((i * 197) % 1000) / 1000f * size.width
        val y = ((i * 311) % 1000) / 1000f * size.height * skyFraction
        val r = 0.8f + (i % 3) * 0.55f
        drawCircle(
            color = Color.White.copy(alpha = 0.2f + (i % 4) * 0.13f * maxAlpha),
            radius = r,
            center = Offset(x, y),
        )
    }
}

/** Soft mist band: three nested ellipses fake a feathered edge cheaply. */
private fun DrawScope.mistBand(yCenter: Float, thickness: Float, alpha: Float) {
    val w = size.width
    listOf(1f to 0.3f, 0.8f to 0.55f, 0.6f to 1f).forEach { (wScale, aScale) ->
        drawOval(
            color = Color.White.copy(alpha = alpha * aScale),
            topLeft = Offset(w * (1f - wScale) / 2f, yCenter - thickness * wScale / 2f),
            size = Size(w * wScale, thickness * wScale),
        )
    }
}

/** Mountain silhouette from fractional (x, peakHeight) ridge points. */
private fun DrawScope.ridge(
    baseY: Float,
    peaks: List<Pair<Float, Float>>,
    color: Color,
) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(-4f, h + 4f)
        lineTo(-4f, baseY)
        peaks.forEach { (fx, peakY) -> lineTo(fx * w, peakY) }
        lineTo(w + 4f, baseY)
        lineTo(w + 4f, h + 4f)
        close()
    }
    drawPath(path, color)
}

/** Sun disc with a wide diffuse glow. */
private fun DrawScope.sun(center: Offset, discRadius: Float, core: Color, glowAlpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(core.copy(alpha = glowAlpha), Color.Transparent),
            center = center,
            radius = discRadius * 7f,
        ),
        radius = discRadius * 7f,
        center = center,
    )
    drawCircle(color = core, radius = discRadius, center = center)
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = discRadius * 0.55f,
        center = center,
    )
}

/** Simple pine silhouettes along one edge. */
private fun DrawScope.pines(edgeX: Float, baseY: Float, heights: List<Float>, color: Color) {
    heights.forEachIndexed { i, h ->
        val x = edgeX + (i * 37 % 90)
        val wHalf = h * 0.28f
        val path = Path().apply {
            moveTo(x, baseY - h)
            lineTo(x - wHalf, baseY - h * 0.45f)
            lineTo(x - wHalf * 0.45f, baseY - h * 0.45f)
            lineTo(x - wHalf * 1.15f, baseY)
            lineTo(x + wHalf * 1.15f, baseY)
            lineTo(x + wHalf * 0.45f, baseY - h * 0.45f)
            lineTo(x + wHalf, baseY - h * 0.45f)
            close()
        }
        drawPath(path, color)
    }
}

// ---------------------------------------------------------------------------
// The five scenes — one world, five hours
// ---------------------------------------------------------------------------

/** Screen 1 — mountain lake at sunrise: blue night top, warm horizon, reflection. */
private fun DrawScope.drawSunriseLake() {
    val w = size.width
    val h = size.height
    val horizon = h * 0.62f
    // Night-blue sky warming toward the horizon.
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF0A1230),
            0.35f to Color(0xFF1B2A5E),
            0.52f to Color(0xFF4A4A8C),
            0.60f to Color(0xFFE8935C),
            0.62f to Color(0xFFFFD9A0),
        ),
        size = Size(w, horizon),
    )
    stars(count = 60, skyFraction = 0.42f)
    // Far hazy ridge, near dark ridge.
    ridge(horizon + 2f, listOf(0.05f to h * 0.50f, 0.3f to h * 0.42f, 0.55f to h * 0.52f, 0.8f to h * 0.44f, 1.0f to h * 0.53f), Color(0xFF2A2A55).copy(alpha = 0.85f))
    // Sun sitting on the horizon, right of center.
    sun(Offset(w * 0.62f, horizon - h * 0.005f), w * 0.035f, Color(0xFFFFE3B0), 0.5f)
    // Lake: mirrored dusk gradient.
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFFF0B070),
            0.12f to Color(0xFF7A5A8C),
            0.45f to Color(0xFF23234D),
            1f to Color(0xFF0B0F26),
        ),
        topLeft = Offset(0f, horizon),
        size = Size(w, h - horizon),
    )
    // Sun reflection streak + ripples.
    val rx = w * 0.62f
    drawOval(
        color = Color(0xFFFFD9A0).copy(alpha = 0.5f),
        topLeft = Offset(rx - w * 0.035f, horizon + 2f),
        size = Size(w * 0.07f, (h - horizon) * 0.5f),
    )
    repeat(7) { i ->
        val y = horizon + (h - horizon) * (0.12f + i * 0.12f)
        val ww = w * (0.05f + 0.02f * sin(i * PI / 3f).toFloat() + i * 0.012f)
        drawOval(
            color = Color.White.copy(alpha = 0.10f),
            topLeft = Offset(rx - ww / 2f, y),
            size = Size(ww, 2.5f),
        )
    }
    mistBand(horizon - h * 0.02f, h * 0.05f, 0.16f)
    mistBand(horizon + (h - horizon) * 0.25f, h * 0.04f, 0.10f)
    // Near shoreline pines, left edge.
    ridge(h + 2f, listOf(0f to h * 0.86f, 0.12f to h * 0.80f, 0.2f to h * 0.90f), Color(0xFF080C1E))
    pines(-6f, h * 0.92f, listOf(h * 0.16f, h * 0.11f, h * 0.13f), Color(0xFF080C1E))
}

/** Screen 2 — soft dreamy clouds; calm backdrop so glass tiles dominate. */
private fun DrawScope.drawSoftClouds() {
    val w = size.width
    val h = size.height
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF16224D),
            0.35f to Color(0xFF3C4C93),
            0.62f to Color(0xFF8A7FC0),
            0.8f to Color(0xFFE8A87C),
            1f to Color(0xFF2A2145),
        ),
        size = size,
    )
    // Diffuse sun glow, upper right.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFE9C4).copy(alpha = 0.5f), Color.Transparent),
            center = Offset(w * 0.8f, h * 0.2f),
            radius = w * 0.7f,
        ),
        radius = w * 0.7f,
        center = Offset(w * 0.8f, h * 0.2f),
    )
    // Layered cloud puffs.
    val clouds = listOf(
        Triple(0.2f, 0.18f, 0.30f), Triple(0.7f, 0.12f, 0.36f), Triple(0.45f, 0.34f, 0.42f),
        Triple(0.15f, 0.52f, 0.38f), Triple(0.8f, 0.48f, 0.34f), Triple(0.5f, 0.66f, 0.44f),
    )
    clouds.forEachIndexed { i, (fx, fy, s) ->
        val cx = fx * w
        val cy = fy * h
        val r = s * w
        repeat(5) { k ->
            val ox = ((i * 53 + k * 97) % 100) / 100f * r * 1.4f - r * 0.7f
            drawCircle(
                color = Color.White.copy(alpha = 0.07f + (k % 3) * 0.025f),
                radius = r * (0.42f - k * 0.045f),
                center = Offset(cx + ox, cy + (k % 2) * r * 0.12f),
            )
        }
    }
    mistBand(h * 0.78f, h * 0.06f, 0.14f)
    // Faint far ridge to ground the scene.
    ridge(h + 2f, listOf(0f to h * 0.88f, 0.4f to h * 0.82f, 0.75f to h * 0.9f, 1f to h * 0.84f), Color(0xFF141A38).copy(alpha = 0.9f))
}

/** Screen 3 — aurora mist over a dark ridge. */
private fun DrawScope.drawAuroraMist() {
    val w = size.width
    val h = size.height
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF05081A),
            0.5f to Color(0xFF0E1440),
            0.75f to Color(0xFF1D1B4E),
            1f to Color(0xFF0A0C24),
        ),
        size = size,
    )
    stars(count = 90, skyFraction = 0.6f)
    // Three aurora ribbons sweeping down-right.
    val ribbons = listOf(
        Triple(Color(0xFF4ADE80), 0.13f, 0.02f),
        Triple(Color(0xFF22D3EE), 0.15f, -0.06f),
        Triple(Color(0xFFF0ABFC), 0.11f, -0.14f),
    )
    ribbons.forEach { (color, alpha, shift) ->
        val path = Path().apply {
            moveTo(w * -0.05f, h * (0.30f + shift))
            cubicTo(
                w * 0.3f, h * (0.18f + shift), w * 0.55f, h * (0.42f + shift),
                w * 1.05f, h * (0.28f + shift),
            )
        }
        drawPath(path, color.copy(alpha = alpha), style = Stroke(width = w * 0.16f, cap = StrokeCap.Round))
        drawPath(path, color.copy(alpha = alpha * 1.4f), style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))
    }
    mistBand(h * 0.62f, h * 0.07f, 0.12f)
    ridge(h + 2f, listOf(0f to h * 0.78f, 0.25f to h * 0.70f, 0.5f to h * 0.80f, 0.78f to h * 0.72f, 1f to h * 0.82f), Color(0xFF070B1E))
    mistBand(h * 0.86f, h * 0.05f, 0.12f)
}

/** Screen 4 — deep blue-teal night garden; calm center for the orb. */
private fun DrawScope.drawNightGarden() {
    val w = size.width
    val h = size.height
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF04121E),
            0.4f to Color(0xFF0A2A3D),
            0.7f to Color(0xFF123A4E),
            1f to Color(0xFF050D16),
        ),
        size = size,
    )
    stars(count = 50, skyFraction = 0.4f, maxAlpha = 0.5f)
    // Moon-glow behind the orb zone (upper-middle).
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF9BE8FF).copy(alpha = 0.30f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.30f),
            radius = w * 0.65f,
        ),
        radius = w * 0.65f,
        center = Offset(w * 0.5f, h * 0.30f),
    )
    // Layered hills.
    ridge(h + 2f, listOf(0f to h * 0.72f, 0.35f to h * 0.64f, 0.7f to h * 0.74f, 1f to h * 0.66f), Color(0xFF0A2030).copy(alpha = 0.9f))
    ridge(h + 2f, listOf(0f to h * 0.84f, 0.3f to h * 0.78f, 0.65f to h * 0.86f, 1f to h * 0.80f), Color(0xFF04101B))
    // Firefly bokeh.
    repeat(26) { i ->
        val x = ((i * 173) % 1000) / 1000f * w
        val y = h * 0.35f + ((i * 269) % 1000) / 1000f * h * 0.5f
        drawCircle(
            color = if (i % 3 == 0) Color(0xFF9BE8FF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.18f),
            radius = 1.5f + (i % 4),
            center = Offset(x, y),
        )
    }
    mistBand(h * 0.7f, h * 0.05f, 0.10f)
}

/** Screen 5 — twilight peak: violet dusk, ember horizon, dark ridge. */
private fun DrawScope.drawTwilightPeak() {
    val w = size.width
    val h = size.height
    val horizon = h * 0.68f
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF0B1030),
            0.3f to Color(0xFF2B1B5E),
            0.5f to Color(0xFF7A3B8C),
            0.62f to Color(0xFFE8765A),
            0.68f to Color(0xFFFFC98A),
        ),
        size = Size(w, horizon),
    )
    stars(count = 70, skyFraction = 0.35f)
    // Low sun melting into the ridge.
    sun(Offset(w * 0.5f, horizon - h * 0.01f), w * 0.045f, Color(0xFFFFD9A0), 0.55f)
    // Valley haze below.
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF4A2A55),
            0.3f to Color(0xFF1B1440),
            1f to Color(0xFF070A1C),
        ),
        topLeft = Offset(0f, horizon),
        size = Size(w, h - horizon),
    )
    // Twin-peak ridge with a high vantage on the right.
    ridge(horizon + 2f, listOf(0f to h * 0.52f, 0.3f to h * 0.40f, 0.55f to h * 0.55f, 0.82f to h * 0.38f, 1f to h * 0.50f), Color(0xFF120E2E))
    mistBand(horizon + (h - horizon) * 0.3f, h * 0.05f, 0.12f)
}
