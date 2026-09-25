package com.momentum.app.presentation.screens.habits

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.isDark

/**
 * Collapsing Soft Aurora header for the Habits screen — one cohesive unit:
 * the bundled Aurora illustration (`habits_hero_light` / `habits_hero_dark`)
 * with the title and description anchored inside the artwork's lower edge.
 *
 * Geometry: the hero is a clipped box whose height tracks [progress]
 * (0 = expanded, 1 = collapsed) continuously. The artwork is bottom-anchored
 * ([ContentScale.Crop], [Alignment.BottomCenter]), so the quiet lower third of
 * the illustration — the same pixels the text sits on at every height — stays
 * pinned under the copy while the crop recedes upward as the box shrinks.
 * There is no re-crop zoom, no re-layout, and no interpolated layout state.
 *
 * Blending: the artwork dissolves top and bottom into whatever is behind it
 * (screen background + ambient Aurora) through an alpha-profile mask, so it
 * never ends on a hard edge and no tinted scrim rectangle is painted over it.
 *
 * Text: theme ink ([MomentumThemeColors.textPrimary] / `textSecondary`) at all
 * times — both artwork variants were built for their theme, so the copy needs
 * no color lerp and never switches mid-scroll.
 *
 * Reduced motion: the hero has no time-based animation; height, crop, and
 * dissolve stay 1:1 with scroll, and the one decorative interpolation (the
 * text's hero inset) is frozen at its compact value.
 *
 * The parent measures the collapse range through [onCollapseRangePx] (a pure
 * function of the measured text block — never of [progress] itself) and owns
 * the single clamped px state + `NestedScrollConnection` behind [progress].
 */
@Composable
fun HabitsHero(
    progress: Float,
    reduceMotion: Boolean,
    onCollapseRangePx: (Float) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Habits",
    description: String = "Manage the things you're building and reducing.",
) {
    val colors = MomentumThemeColors.current
    val p = progress.coerceIn(0f, 1f)

    var textBlockPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val textBlockDp = with(density) { textBlockPx.toDp() }

    // Reported range is a pure function of the measured text block: it never
    // depends on `p`, so progress cannot feed back into the range that
    // produced it (a range that moved with scroll would jitter mid-drag).
    val compactHeight = MomentumSpacing.standard + textBlockDp + CompactTextInset
    val expandedHeight = maxOf(ExpandedFloor, compactHeight + CollapseHeadroom)
    val currentHeight = lerp(expandedHeight, compactHeight, p)

    val rangePx = with(density) { (expandedHeight - compactHeight).toPx() }
    val reportRange by rememberUpdatedState(onCollapseRangePx)
    LaunchedEffect(rangePx) { reportRange(rangePx) }

    val textInset = if (reduceMotion) {
        CompactTextInset
    } else {
        lerp(HeroTextInset, CompactTextInset, p)
    }
    val artworkRes = if (colors.isDark) {
        R.drawable.habits_hero_dark
    } else {
        R.drawable.habits_hero_light
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clipToBounds(),
    ) {
        HabitsHeroArtwork(
            res = artworkRes,
            modifier = Modifier.fillMaxSize(),
        )
        HeroTextBlock(
            title = title,
            description = description,
            titleStyle = MaterialTheme.typography.headlineLarge,
            bodyStyle = MaterialTheme.typography.bodyMedium,
            bottomInset = textInset,
            onHeightChanged = { textBlockPx = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/** Title + description, bottom-anchored in the hero; reports its content height. */
@Composable
private fun HeroTextBlock(
    title: String,
    description: String,
    titleStyle: TextStyle,
    bodyStyle: TextStyle,
    bottomInset: Dp,
    onHeightChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MomentumThemeColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MomentumSpacing.majorSection)
            .padding(bottom = bottomInset)
            .onSizeChanged { size -> onHeightChanged(size.height) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
    ) {
        Text(
            text = title,
            style = titleStyle,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = description,
            style = bodyStyle,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * The single hero illustration for the active theme — [res] is either bundled
 * Aurora variant, used as supplied (no tint, no filter, no re-coloring).
 *
 * Bottom-anchored cover-crop keeps the artwork's quiet lower third under the
 * copy at every height; an offscreen [BlendMode.DstIn] alpha mask dissolves
 * the top and bottom edges into the screen background, so the artwork reads
 * as part of the environment instead of a pasted rectangle. The mask color
 * carries no hue — `DstIn` reads only its alpha.
 */
@Composable
private fun HabitsHeroArtwork(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = res),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.BottomCenter,
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithCache {
                val h = size.height
                if (size.width <= 0f || h <= 0f) {
                    onDrawBehind { }
                } else {
                    val topEnd = (TopDissolve.toPx() / h).coerceIn(0.05f, 0.30f)
                    val bottomStart =
                        (1f - BottomDissolve.toPx() / h).coerceIn(topEnd + 0.10f, 0.95f)
                    val dissolve = Brush.verticalGradient(
                        0f to Color.Transparent,
                        topEnd to Color.White,
                        bottomStart to Color.White,
                        1f to Color.Transparent,
                        startY = 0f,
                        endY = h,
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush = dissolve, blendMode = BlendMode.DstIn)
                    }
                }
            },
    )
}

/** Expanded hero height floor; the hero never starts shorter than this. */
private val ExpandedFloor = 300.dp

/** Extra height above the compact header the collapse has to travel through. */
private val CollapseHeadroom = 60.dp

/** Description → hero bottom edge while expanded. */
private val HeroTextInset = 24.dp

/** Description → hero bottom edge while compact (also the reduced-motion inset). */
private val CompactTextInset = 12.dp

/** Top dissolve band: artwork emerges from the screen background. */
private val TopDissolve = 44.dp

/** Bottom dissolve band: artwork, copy, and cards melt into one background. */
private val BottomDissolve = 88.dp

@Preview(name = "Habits hero expanded light", showBackground = true, widthDp = 360)
@Composable
private fun HabitsHeroExpandedLightPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        HabitsHero(progress = 0f, reduceMotion = false, onCollapseRangePx = {})
    }
}

@Preview(name = "Habits hero collapsed light", showBackground = true, widthDp = 360)
@Composable
private fun HabitsHeroCollapsedLightPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        HabitsHero(progress = 1f, reduceMotion = false, onCollapseRangePx = {})
    }
}

@Preview(name = "Habits hero expanded dark", showBackground = true, widthDp = 360)
@Composable
private fun HabitsHeroExpandedDarkPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.DARK) {
        HabitsHero(progress = 0f, reduceMotion = false, onCollapseRangePx = {})
    }
}

@Preview(name = "Habits hero collapsed dark", showBackground = true, widthDp = 360)
@Composable
private fun HabitsHeroCollapsedDarkPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.DARK) {
        HabitsHero(progress = 1f, reduceMotion = false, onCollapseRangePx = {})
    }
}

@Preview(name = "Habits hero reduced motion", showBackground = true, widthDp = 360)
@Composable
private fun HabitsHeroReducedMotionPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        HabitsHero(progress = 0.5f, reduceMotion = true, onCollapseRangePx = {})
    }
}
