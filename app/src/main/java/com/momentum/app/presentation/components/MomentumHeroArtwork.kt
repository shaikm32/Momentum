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
 * **Edges.** An offscreen `DstIn` alpha profile dissolves [topDissolve] and
 * [bottomDissolve] bands into whatever sits behind the artwork (screen
 * background + ambient Aurora), so the artwork never ends on a hard horizontal
 * edge and never leaves a dead band where it is expected to be present. The
 * mask colour carries no hue — `DstIn` reads only its alpha. A band of `0.dp`
 * means "no fade on this edge": the artwork then keeps full strength right up to
 * it, which is what a Hero whose copy starts at its very top edge needs, while
 * a caller that wants the artwork to emerge from the background (Habits) passes
 * a real band and gets the full profile.
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
    HeroArtworkImage(
        res = momentumHeroArtworkRes(dark),
        alignment = alignment,
        topDissolve = topDissolve,
        bottomDissolve = bottomDissolve,
        zoom = zoom,
        parallax = parallax,
        modifier = modifier,
    )
}

/** The bundled illustration for [res], dissolved top and bottom, as supplied. */
@Composable
private fun HeroArtworkImage(
    @DrawableRes res: Int,
    alignment: Alignment,
    topDissolve: Dp,
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
                    // A zero band means "no fade here": the artwork keeps full
                    // strength right up to this edge. Callers that want the
                    // artwork to emerge from the background (Habits) pass a band
                    // and keep exactly the previous profile.
                    val topEnd = if (topDissolve <= 0.dp) {
                        0f
                    } else {
                        (topDissolve.toPx() / h).coerceIn(0.02f, 0.30f)
                    }
                    val bottomStart =
                        (1f - bottomDissolve.toPx() / h).coerceIn(topEnd + 0.10f, 0.95f)
                    val dissolve = Brush.verticalGradient(
                        colorStops = buildList {
                            if (topEnd > 0f) {
                                add(0f to Color.Transparent)
                                add(topEnd to Color.White)
                            }
                            add(bottomStart to Color.White)
                            add(1f to Color.Transparent)
                        }.toTypedArray(),
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

/** Top dissolve band: artwork emerges from the screen background. */
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
