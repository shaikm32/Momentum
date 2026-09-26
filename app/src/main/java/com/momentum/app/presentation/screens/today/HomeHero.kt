package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumHeroArtwork
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.isDark

/**
 * Home Hero â€” the primary visual element of Home, and **one Aurora-backed
 * surface** (DASHBOARD_SPECS Â§6â€“Â§8, DECISIONS Â§4/Â§7/Â§8/Â§9, ADR-30/ADR-31).
 *
 * **One surface, one background.** Date, greeting, Settings, progress ring,
 * completion count and message are all children of a single clipped Box whose
 * *first* layer is the shared Aurora artwork covering the Hero's whole bounds.
 * There is no separate header surface above the art and no un-backed region
 * below it â€” the Habits header is the reference composition and works because
 * its copy sits inside the artwork's own Box (ADR-28).
 *
 * **No gaps, no dead space, no hard edges.** The artwork is cover-cropped and
 * bottom-anchored and is at *full strength* from the Hero's top edge â€” the date,
 * greeting and Settings sit directly on Aurora, with no band of plain page
 * background above them and no top ramp to fade through. The single fade is at
 * the bottom, below the message, where the artwork melts into the list.
 *
 * **Ends with its content.** The Hero's height is its own content plus
 * [CollapseHeadroom]; that headroom becomes the open Aurora band *between* the
 * greeting and the ring, never space below the message, so the activity section
 * continues straight out of the fade instead of after a void.
 *
 * **Reaches the status bar.** The Hero's box starts at the physical top edge
 * (the window is edge-to-edge, ADR-33): the artwork draws behind the status bar
 * while the copy is held below the status-bar icons by the safe inset folded
 * into [HeroTopInset]. Nothing else on screen moves.
 *
 * **Readable without flattening the art.** [HeroReadabilityVeil] is a single
 * vertical gradient tinted with the page background: transparent at the top
 * edge, rising only to reach the date, full behind the greeting, dropping to
 * [VeilOpen] in the open band, back up behind ring/count/message, transparent
 * again at the bottom. The artwork therefore stays visible across the whole
 * Hero â€” the previous full-width blanket veil left only a bare strip of Aurora
 * and is gone for good (its peaks are asserted to stay â‰¤ 0.65).
 *
 * **Geometry** is a pure function of the *measured* foreground blocks (header
 * and content), never of [collapseProgress], so the collapse range cannot feed
 * back into itself mid-drag. Compact is exactly header + content + insets, so
 * nothing is clipped and dynamic type simply makes a taller Hero.
 *
 * **Reduced motion** freezes every decorative interpolation (ring scale,
 * artwork zoom and parallax); height still tracks scroll 1:1 because that is
 * layout, not flourish.
 */
@Composable
fun HomeHero(
    dateLabel: String,
    greeting: String,
    progress: Float,
    doneCount: Int,
    totalCount: Int,
    message: String,
    collapseProgress: Float,
    reduceMotion: Boolean,
    onOpenSettings: () -> Unit,
    onCollapseRangePx: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = collapseProgress.coerceIn(0f, 1f)
    val density = LocalDensity.current

    var heroPx by remember { mutableIntStateOf(0) }
    var headerPx by remember { mutableIntStateOf(0) }
    var contentPx by remember { mutableIntStateOf(0) }
    val headerDp = with(density) { headerPx.toDp() }
    val contentDp = with(density) { contentPx.toDp() }

    // Edge-to-edge (ADR-33): the Hero's box starts at the physical top edge and
    // the artwork runs behind the status bar, so the foreground's top inset is
    // the status-bar safe inset PLUS the [HeroTopInset] breathing room the
    // composition always had. Date, greeting and Settings therefore keep their
    // exact on-screen position (below the status-bar icons), and so does every
    // section under the Hero — only the artwork gains the strip behind the bar.
    val topInset =
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + HeroTopInset

    // Compact is derived from the measured blocks alone â€” a range that moved
    // with scroll would jitter mid-drag. There is no screen-percentage floor:
    // the Hero is exactly its content plus the space the collapse travels
    // through, so it can never end in a band of empty background.
    val compactHeight = topInset + headerDp + HeaderContentGap + contentDp + HeroBottomInset
    val expandedHeight = compactHeight + CollapseHeadroom
    val currentHeight = lerp(expandedHeight, compactHeight, p)

    val rangePx = with(density) { (expandedHeight - compactHeight).toPx() }
    val reportRange by rememberUpdatedState(onCollapseRangePx)
    LaunchedEffect(rangePx) { reportRange(rangePx) }

    // "Hero content may scale slightly" â€” applied to the ring only, so text
    // never renders at a scale other than 1.
    val ringScale = if (reduceMotion) 1f else 1f + RingScaleHeadroom * (1f - p)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clipToBounds()
            .testTag(HomeHeroTag)
            .onSizeChanged { size -> heroPx = size.height },
    ) {
        // Layer 1 â€” the Hero's background: shared Aurora artwork, cover-cropped
        // to the Hero's full bounds, theme-selected (ADR-30).
        MomentumHeroArtwork(
            alignment = Alignment.BottomCenter,
            topDissolve = TopDissolve,
            bottomDissolve = BottomDissolve,
            zoom = if (reduceMotion) 1f else ArtworkScale,
            // Moves only with scroll, never on its own.
            parallax = if (reduceMotion) 0.dp else ParallaxTravel * (1f - p),
            modifier = Modifier
                .fillMaxSize()
                .testTag(HeroArtworkTag),
        )
        // Layer 2 â€” readability, shaped around the copy instead of a plate.
        HeroReadabilityVeil(
            heroHeightPx = heroPx,
            topInsetDp = topInset,
            headerHeightDp = headerDp,
            contentHeightDp = contentDp,
            modifier = Modifier.fillMaxSize(),
        )
        // Layer 3 â€” the complete foreground, every part of it on the artwork.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MomentumSpacing.screen),
        ) {
            Spacer(Modifier.height(topInset))
            HeroHeader(
                dateLabel = dateLabel,
                greeting = greeting,
                onOpenSettings = onOpenSettings,
                onHeightChanged = { headerPx = it },
            )
            Spacer(Modifier.height(HeaderContentGap))
            // The only weighted spacer, and it sits *above* the content block:
            // the Hero's extra height becomes the open Aurora band between the
            // greeting and the ring, while the message always ends
            // HeroBottomInset above the Hero's bottom edge. No trailing void.
            Spacer(Modifier.weight(1f))
            HeroContent(
                progress = progress,
                doneCount = doneCount,
                totalCount = totalCount,
                message = message,
                ringScale = ringScale,
                reduceMotion = reduceMotion,
                onHeightChanged = { contentPx = it },
            )
            // The artwork's bottom ramp lives here, below the message, and is
            // the only thing between the copy and the activity section.
            Spacer(Modifier.height(HeroBottomInset))
        }
    }
}

/**
 * Home's app bar, inside the Hero surface: date above greeting, Settings at the
 * top-right (DECISIONS Â§5/Â§12). It reports its measured height so the Hero can
 * size itself and shape its veil around the copy.
 */
@Composable
private fun HeroHeader(
    dateLabel: String,
    greeting: String,
    onOpenSettings: () -> Unit,
    onHeightChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(HeroHeaderTag)
            .onSizeChanged { size -> onHeightChanged(size.height) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.standard),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
        ) {
            // DECISIONS Â§5: concise date above the greeting, so it gives
            // context without competing with the Hero below.
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelLarge,
                color = c.textSecondary,
            )
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                color = c.textPrimary,
            )
        }
        LiquidGlassIconButton(
            icon = MomentumIcons.settings,
            contentDescription = "Settings",
            onClick = onOpenSettings,
        )
    }
}

/**
 * Ring, completion count and motivational message (DECISIONS Â§4/Â§9). Reports its
 * measured height for the same reason as the header. The ring and its count are
 * hidden when nothing is eligible, so an empty day shows no misleading 0%.
 */
@Composable
private fun HeroContent(
    progress: Float,
    doneCount: Int,
    totalCount: Int,
    message: String,
    ringScale: Float,
    reduceMotion: Boolean,
    onHeightChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(HeroContentTag)
            .onSizeChanged { size -> onHeightChanged(size.height) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
    ) {
        if (totalCount > 0) {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = ringScale
                    scaleY = ringScale
                },
            ) {
                ProgressOrb(
                    progress = progress,
                    size = HeroRingSize,
                    glowEnabled = !reduceMotion,
                    animate = !reduceMotion,
                )
            }
            Text(
                text = "$doneCount of $totalCount completed",
                style = MaterialTheme.typography.titleMedium,
                color = c.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
        if (message.isNotBlank()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = c.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * The Hero's readability treatment â€” **not** a plate, and not a wash.
 *
 * One vertical gradient tinted with the page background whose stops follow the
 * measured copy. It is fully transparent at the Hero's **top edge** â€” so the
 * Aurora is untouched where the artwork meets the page background, with no band
 * of its own â€” rises only to reach the date, holds behind the greeting, drops
 * to [VeilOpen] across the open band so the ribbons read at full strength
 * there, comes back up behind ring/count/message, and returns to transparent
 * where the artwork's own bottom ramp takes over. A single smooth shape means
 * there is no boundary to see anywhere inside the Hero, and the low peaks keep
 * the artwork as artwork.
 */
@Composable
private fun HeroReadabilityVeil(
    heroHeightPx: Int,
    topInsetDp: Dp,
    headerHeightDp: Dp,
    contentHeightDp: Dp,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val dark = c.isDark
    val headerAlpha = if (dark) VeilHeaderDark else VeilHeaderLight
    val contentAlpha = if (dark) VeilContentDark else VeilContentLight
    Box(
        modifier = modifier.drawWithCache {
            val h = heroHeightPx.toFloat()
            if (h <= 0f) {
                onDrawBehind { }
            } else {
                val topInset = topInsetDp.toPx()
                val bottomInset = HeroBottomInset.toPx()
                val header = headerHeightDp.toPx()
                val content = contentHeightDp.toPx()
                // Mirrors the Column: the only weighted spacer is above the
                // content block, so the copy always ends bottomInset above the
                // Hero's bottom edge whatever the collapse value is.
                val contentTop = (h - bottomInset - content).coerceIn(0f, h)
                val contentEnd = (contentTop + content).coerceIn(contentTop, h)
                val headerStart = (topInset / h).coerceIn(0f, 1f)
                val headerEnd = ((topInset + header) / h).coerceIn(headerStart, 1f)
                val openMid = (headerEnd + (contentTop - headerEnd) / 2f).coerceIn(0f, 1f)
                var previous = 0f
                fun stop(fraction: Float): Float =
                    fraction.coerceIn(previous, 1f).also { previous = it }
                // Same hue as the surface it veils, so a zero-alpha stop never
                // bleeds a dark halo into the artwork.
                val clear = c.background.copy(alpha = 0f)
                onDrawBehind {
                    drawRect(
                        Brush.verticalGradient(
                            0f to clear,
                            stop(headerStart) to c.background.copy(alpha = headerAlpha),
                            stop(headerEnd) to c.background.copy(alpha = headerAlpha),
                            stop(openMid) to c.background.copy(alpha = VeilOpen),
                            stop(contentTop) to c.background.copy(alpha = contentAlpha),
                            stop(contentEnd) to c.background.copy(alpha = contentAlpha),
                            1f to clear,
                        ),
                    )
                }
            }
        },
    )
}

/**
 * Layout-contract tags for the Home Hero's layers. They carry no accessibility
 * meaning â€” they exist so the composition ("the artwork is the background of the
 * whole Hero, and every piece of copy sits on it") can be asserted at bounds
 * level instead of being taken on trust.
 */
internal const val HomeHeroTag = "homeHero"
internal const val HeroArtworkTag = "homeHeroArtwork"
internal const val HeroHeaderTag = "homeHeroHeader"
internal const val HeroContentTag = "homeHeroContent"
/**
 * Top inset: breathing room between the Hero's top edge and the date. It sits
 * **on the artwork**, not above it, so it costs no Aurora. At runtime it is
 * added on top of the status-bar safe inset, because the Hero's box starts at
 * the physical top edge and its artwork runs behind the status bar (ADR-33).
 */
private val HeroTopInset = 16.dp

/**
 * Bottom inset: holds the artwork's [BottomDissolve] below the message, so the
 * Hero ends shortly after the copy and the activity section continues straight
 * out of the fade.
 */
private val HeroBottomInset = 40.dp

/** Minimum breathing room between the header and the ring/count/message block. */
private val HeaderContentGap = 20.dp

/**
 * No top ramp on Home: the artwork keeps full strength right up to the Hero's
 * top edge, so the date, greeting and Settings sit directly on Aurora with no
 * band of plain page background above them. (Habits keeps its 44 dp ramp â€” that
 * is a per-screen choice, not a component default.)
 */
private val TopDissolve = 0.dp

/** Bottom ramp: artwork melts into the page background below the message. */
private val BottomDissolve = 40.dp

/**
 * The room the collapse travels through while expanded, and therefore the
 * height of the open Aurora band between the greeting and the ring. All of the
 * Hero's extra height lives there, never below the message.
 */
private val CollapseHeadroom = 72.dp

/** Progress ring diameter inside the Hero. */
private val HeroRingSize = 96.dp

/** Ring growth while expanded â€” the one content scale in the Hero. */
private const val RingScaleHeadroom = 0.12f

/** Artwork overscan that gives the parallax somewhere to travel. */
private const val ArtworkScale = 1.12f

/** Artwork drift across a full collapse; frozen under reduced motion. */
private val ParallaxTravel = 12.dp

/**
 * Veil peaks. Deliberately far below a flattening 0.86 blanket: the artwork
 * behind the copy stays plainly visible, and the open band is nearly untouched.
 * Body text still clears 4.5:1 because the veil is the page background itself
 * (AGENTS Â§9).
 */
private const val VeilHeaderDark = 0.34f
private const val VeilHeaderLight = 0.30f
private const val VeilOpen = 0.10f
private const val VeilContentDark = 0.58f
private const val VeilContentLight = 0.62f

@Preview(name = "Home hero light", showBackground = true, widthDp = 360, heightDp = 440)
@Composable
private fun HomeHeroLightPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        HomeHero(
            dateLabel = "FRI, SEP 25",
            greeting = "Good morning",
            progress = 0.4f,
            doneCount = 2,
            totalCount = 5,
            message = "Small steps, unstoppable momentum.",
            collapseProgress = 0f,
            reduceMotion = false,
            onOpenSettings = {},
            onCollapseRangePx = {},
        )
    }
}

@Preview(name = "Home hero dark", showBackground = true, widthDp = 360, heightDp = 440)
@Composable
private fun HomeHeroDarkPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        HomeHero(
            dateLabel = "FRI, SEP 25",
            greeting = "Good night",
            progress = 1f,
            doneCount = 5,
            totalCount = 5,
            message = "Everything complete. Rest easy.",
            collapseProgress = 0f,
            reduceMotion = false,
            onOpenSettings = {},
            onCollapseRangePx = {},
        )
    }
}

@Preview(name = "Home hero collapsed", showBackground = true, widthDp = 360, heightDp = 440)
@Composable
private fun HomeHeroCollapsedPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        HomeHero(
            dateLabel = "FRI, SEP 25",
            greeting = "Good evening",
            progress = 0.6f,
            doneCount = 3,
            totalCount = 5,
            message = "You're building momentum.",
            collapseProgress = 1f,
            reduceMotion = false,
            onOpenSettings = {},
            onCollapseRangePx = {},
        )
    }
}

@Preview(name = "Home hero nothing scheduled", showBackground = true, widthDp = 360, heightDp = 440)
@Composable
private fun HomeHeroEmptyPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        HomeHero(
            dateLabel = "FRI, SEP 25",
            greeting = "Good morning",
            progress = 0f,
            doneCount = 0,
            totalCount = 0,
            message = "A fresh page â€” add your first activity.",
            collapseProgress = 0f,
            reduceMotion = true,
            onOpenSettings = {},
            onCollapseRangePx = {},
        )
    }
}
