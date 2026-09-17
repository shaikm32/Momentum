package com.momentum.app.presentation.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.momentum.app.R

/**
 * Cinematic onboarding backdrops — exactly four full-screen Twilight
 * artworks, each already containing its scene's illustrations/icons.
 * Rendered edge-to-edge with [ContentScale.Crop] (minimum-magnification
 * cover: no bars, no distortion); Compose only controls scaling,
 * positioning and the readability treatment — the files are never modified.
 *
 * A subtle bottom readability gradient keeps headline/body legible while
 * leaving the artwork dominant ("UI floating over beautiful artwork").
 * No opaque scrims, no procedural art.
 */
enum class OnboardingScene {
    INTENT,
    TRACK,
    GROWTH,
    BEGIN,
}

/** Maps each scene to its authoritative supplied background asset. */
@DrawableRes
internal fun OnboardingScene.backgroundRes(): Int = when (this) {
    OnboardingScene.INTENT -> R.drawable.onboarding_screen_01
    OnboardingScene.TRACK -> R.drawable.onboarding_screen_02
    OnboardingScene.GROWTH -> R.drawable.onboarding_screen_03
    OnboardingScene.BEGIN -> R.drawable.onboarding_screen_04
}

/**
 * Where the unavoidable cover-crop lands, chosen from the actual artwork
 * composition (fractional biases, -1..1; only matters when the screen aspect
 * differs from the asset's):
 * - INTENT: centered sun over the lake; near-symmetric, protect the glow.
 * - TRACK: icon constellation in the upper sky; protect it over the hills.
 * - GROWTH: glass progress cards in the upper half; protect them over the
 *   staircase foreground (which sits behind the text gradient anyway).
 * - BEGIN: hiker left of center, sun right; protect the hiker side slightly.
 */
internal fun OnboardingScene.subjectAlignment(): Alignment = when (this) {
    OnboardingScene.INTENT -> BiasAlignment(0f, 0.1f)
    OnboardingScene.TRACK -> BiasAlignment(0f, -0.25f)
    OnboardingScene.GROWTH -> BiasAlignment(0f, -0.2f)
    OnboardingScene.BEGIN -> BiasAlignment(-0.15f, 0f)
}

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
        // `parallax` is a tiny static zoom per page (1.00–1.02); applied as a
        // GPU layer transform, never touching the artwork itself.
        Image(
            painter = painterResource(id = scene.backgroundRes()),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = parallax, scaleY = parallax),
            // Cover is the minimum-magnification scale that still reaches
            // every edge (no bars, no distortion); subjectAlignment() decides
            // which edges absorb the unavoidable crop on mismatched aspects.
            contentScale = ContentScale.Crop,
            alignment = scene.subjectAlignment(),
        )
        // Readability treatment: transparent upper region so the artwork
        // breathes; gently darkened lower region behind headline/body/CTA.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.42f to Color.Transparent,
                        0.62f to PhotoShade.copy(alpha = 0.32f),
                        0.82f to PhotoShade.copy(alpha = 0.58f),
                        1f to PhotoShade.copy(alpha = 0.74f),
                    ),
                ),
        )
        content()
    }
}
