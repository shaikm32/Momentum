package com.momentum.app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.isDark

/**
 * Shared Momentum Aurora hero artwork — the single artwork system behind every
 * major Hero/Header surface (DECISIONS ADR-30).
 *
 * Selection is **theme only**: Light renders [R.drawable.habits_hero_light],
 * Dark renders [R.drawable.habits_hero_dark]. The device clock takes no part,
 * so artwork changes exactly when the theme flips Light ↔ Dark and at no other
 * moment. Both files are the bundled 1024×1536 Aurora illustrations, consumed
 * exactly as supplied — no tint, no filter, no re-colour, no generated art.
 *
 * The artwork is **atmospheric background content**: it is never the message,
 * it must not compete with the foreground, and it must blend into whatever
 * screen carries it without a visible edge or gap. Callers place their copy
 * above it and own the readability treatment for that copy.
 */
@DrawableRes
fun momentumHeroArtworkRes(dark: Boolean): Int =
    if (dark) R.drawable.habits_hero_dark else R.drawable.habits_hero_light

/**
 * Draws the shared Momentum hero artwork.
 *
 * **Crop.** Cover-crop anchored to [alignment] (bottom by default), so the
 * illustration's quiet lower third — the same pixels a Hero's ring and copy
 * sit on at every height — stays pinned under the content while the crop
 * recedes upward as the container shrinks. There is no re-layout and no
 * interpolated layout state; height is layout, not flourish.
 *
 * **Edges (ADR-39, D0 rule 4 re-stated).** An offscreen `DstIn` alpha profile
 * dissolves [bottomDissolve] into whatever sits behind the artwork (screen
 * background + ambient Aurora), so the artwork never ends on a hard horizontal
 * edge at the bottom and never leaves a dead band where it is expected to be
 * present. The mask colour carries no hue — `DstIn` reads only its alpha.
 * There is **no top fade**: the profile is opaque (α = 1) from the container's
 * top edge down to the bottom blend. On both Hero routes the container's top
 * edge *is* the physical window top (ADR-33), so there is no page background
 * above it to blend into — full-strength artwork behind the transparent bar is
 * what the product requirements and `DASHBOARD_SPECS.md` §6 lock.
 *
 * **`topDissolve` (ADR-39).** The parameter stays for ADR-30's caller contract
 * (callers pass only band amounts and cannot position the artwork) but
 * generates **no stop**: it is a *declared* band, absorbed at the window edge
 * by the full-strength head — Habits' 44.dp means "artwork stays at full
 * strength through the declared band", never a fade start, a dip depth or an
 * offset. The status-bar safe area is the foreground copy's job (D0 rule 3 /
 * ADR-35); the component itself reads no insets. A genuine top fade would be a
 * new decision (ADR) for a container whose top edge borders page background —
 * no such caller exists today.
 *
 * **Motion.** [zoom] and [parallax] are decorative, so callers must freeze
 * both at their compact values under reduced motion. They sit *inside* the
 * masked layer: the artwork drifts while the dissolve stays pinned to the
 * container's own bounds.
 */
@Composable
fun MomentumHeroArtwork(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    topDissolve: Dp = DefaultTopDissolve,
    bottomDissolve: Dp = DefaultBottomDissolve,
    zoom: Float = 1f,
    parallax: Dp = 0.dp,
) {
    val dark = LocalMomentumColors.current.isDark
    // ADR-39: `topDissolve` is deliberately not read here — the declared top
    // band is absorbed at the window edge by the full-strength head.
    HeroArtworkImage(
        res = momentumHeroArtworkRes(dark),
        alignment = alignment,
        bottomDissolve = bottomDissolve,
        zoom = zoom,
        parallax = parallax,
        modifier = modifier,
    )
}

/** The bundled illustration for [res], dissolved at the bottom, as supplied. */
@Composable
private fun HeroArtworkImage(
    @DrawableRes res: Int,
    alignment: Alignment,
    bottomDissolve: Dp,
    zoom: Float,
    parallax: Dp,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = res),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = alignment,
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithCache {
                val h = size.height
                if (size.width <= 0f || h <= 0f) {
                    onDrawBehind { }
                } else {
                    // ADR-39 (§7.7): one stop list for every caller — opaque
                    // (α = 1) from the container's top edge to the bottom
                    // blend, then the caller's bottom dissolve. No top fade is
                    // generated from `topDissolve`; the bottom band keeps its
                    // bounds (floor 0.10, ceiling 0.95) exactly as before.
                    val bottomStart =
                        (1f - bottomDissolve.toPx() / h).coerceIn(0.10f, 0.95f)
                    val dissolve = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.White,
                            bottomStart to Color.White,
                            1f to Color.Transparent,
                        ),
                        startY = 0f,
                        endY = h,
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush = dissolve, blendMode = BlendMode.DstIn)
                    }
                }
            }
            .graphicsLayer {
                scaleX = zoom
                scaleY = zoom
                translationY = parallax.toPx()
            },
    )
}

/**
 * Declared top band: absorbed at the window edge — the artwork stays at full
 * strength through it and no mask stop is generated from it (ADR-39). Kept in
 * the signature for the ADR-30 caller contract (callers pass band amounts only).
 */
private val DefaultTopDissolve = 44.dp

/** Bottom dissolve band: artwork and content melt into the list below. */
private val DefaultBottomDissolve = 88.dp

@Preview(name = "Shared hero light", showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun MomentumHeroArtworkLightPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        MomentumHeroArtwork(modifier = Modifier.fillMaxWidth().height(300.dp))
    }
}

@Preview(name = "Shared hero dark", showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun MomentumHeroArtworkDarkPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        MomentumHeroArtwork(modifier = Modifier.fillMaxWidth().height(300.dp))
    }
}
