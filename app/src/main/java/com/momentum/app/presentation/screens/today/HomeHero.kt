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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.R
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumHeroArtwork
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.isDark
import kotlin.math.roundToInt

/**
 * Home Hero — the primary visual element of Home, and **one Aurora-backed
 * surface** (DASHBOARD_SPECS §6–§8, DECISIONS §4/§7/§8/§9, ADR-30/ADR-31).
 *
 * **One surface, one background.** Date, greeting, Settings, progress ring,
 * completion count and message are all children of a single clipped Box whose
 * *first* layer is the shared Aurora artwork covering the Hero's whole bounds.
 * There is no separate header surface above the art and no un-backed region
 * below it — the Habits header is the reference composition and works because
 * its copy sits inside the artwork's own Box (ADR-28).
 *
 * **No gaps, no dead space, no hard edges.** The artwork is cover-cropped and
 * bottom-anchored and is at *full strength* from the Hero's top edge — the date,
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
 * into [HeroTopInset]. Because the Hero is a plain item of the list, the copy
 * then scrolls *up* with it; a screen-space fade on the foreground layer alone
 * keeps it out of the status-bar icons and the camera cutout on the way (the
 * artwork and veil stay full-bleed). Nothing else on screen moves.
 *
 * **Readable without flattening the art.** [HeroReadabilityVeil] is a single
 * sampled vertical gradient tinted with the page background: transparent at the
 * top edge, rising only to reach the date, full behind the greeting, dipping in
 * the open band, back up behind ring/count/message, transparent again on the
 * same tail the artwork dies on. The artwork therefore stays visible across the
 * whole Hero — the previous full-width blanket veil left only a bare strip of
 * Aurora and is gone for good (its peaks are asserted to stay ≤ 0.65).
 *
 * **Geometry** is a pure function of the *measured* foreground blocks (header
 * and content), never of [collapseProgress], so the collapse range cannot feed
 * back into itself mid-drag. Compact is exactly header + content + insets, so
 * nothing is clipped and dynamic type simply makes a taller Hero. The content
 * block itself is rigid at every collapse value: only the height and the ring's
 * scale change, so the copy always travels exactly with the list.
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
    val statusBarDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topInset = statusBarDp + HeroTopInset

    // The Hero's top edge in root coordinates, in px. Written by layout and read
    // *only* inside the foreground's draw layer below, so the safe-area fade
    // tracks the scroll one draw pass at a time — it never recomposes anything
    // and never feeds back into layout (it is an observation, not an input).
    var heroTopPx by remember { mutableIntStateOf(0) }

    // Compact is derived from the measured blocks alone — a range that moved
    // with scroll would jitter mid-drag. There is no screen-percentage floor:
    // the Hero is exactly its content plus the space the collapse travels
    // through, so it can never end in a band of empty background.
    val compactHeight = topInset + headerDp + HeaderContentGap + contentDp + HeroBottomInset
    val expandedHeight = compactHeight + CollapseHeadroom
    val currentHeight = lerp(expandedHeight, compactHeight, p)

    val rangePx = with(density) { (expandedHeight - compactHeight).toPx() }
    val reportRange by rememberUpdatedState(onCollapseRangePx)
    LaunchedEffect(rangePx) { reportRange(rangePx) }

    // "Hero content may scale slightly" — applied to the ring only, so text
    // never renders at a scale other than 1. The ring is laid out inside its
    // reserved [RingBounds] box, so the growth is absorbed by that box and can
    // never eat into the spacing below it: count and message stay exactly where
    // they are, at every collapse value.
    //
    // This and the height change are the *only* things the collapse drives in
    // the content (Phase 2C second pass). The earlier translate + lift of the
    // description is gone: it made the content block's height function of the
    // scroll (a hole opened under the lifted copy, up to 130 dp of void below
    // the message), it put the description in relative motion against the ring
    // (the overlap reports), and it meant copy and list travelled at different
    // rates. The block now moves rigidly, 1:1 with the list.
    val ringScale = if (reduceMotion) 1f else 1f + RingScaleHeadroom * (1f - p)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clipToBounds()
            .testTag(HomeHeroTag)
            .onSizeChanged { size -> heroPx = size.height }
            .onGloballyPositioned { coords -> heroTopPx = coords.positionInRoot().y.roundToInt() },
    ) {
        // Layer 1 — the Hero's background: shared Aurora artwork, cover-cropped
        // to the Hero's full bounds, theme-selected (ADR-30). Its own bottom
        // ramp is finished by an eased alpha profile that starts at
        // [BottomBlendStart] and takes the artwork to zero *on* the Hero's
        // bottom edge ([BottomBlendTail] = 0), so the fade runs out exactly
        // where the Hero does and the artwork/content boundary has no edge, no
        // slope kink and no colour step (Phase 2C). It multiplies the artwork's
        // alpha only (DstIn, in the artwork's own offscreen layer) — the
        // artwork is never covered.
        MomentumHeroArtwork(
            alignment = Alignment.BottomCenter,
            topDissolve = TopDissolve,
            bottomDissolve = BottomDissolve,
            zoom = if (reduceMotion) 1f else ArtworkScale,
            // Moves only with scroll, never on its own.
            parallax = if (reduceMotion) 0.dp else ParallaxTravel * (1f - p),
            modifier = Modifier
                .fillMaxSize()
                .testTag(HeroArtworkTag)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithCache {
                    val h = size.height
                    if (h <= 0f) {
                        onDrawBehind { }
                    } else {
                        val start = (h - BottomBlendStart.toPx()).coerceAtLeast(0f)
                        val end = (h - BottomBlendTail.toPx()).coerceIn(start + 1f, h)
                        val span = end - start
                        // Eased S-curve: zero slope where the artwork is still
                        // at full strength and zero slope where it reaches 0,
                        // so neither end of the blend reads as a line.
                        val brush = Brush.verticalGradient(
                            colorStops = buildList {
                                add(0f to Color.White)
                                add(start / h to Color.White)
                                for (i in 1 until BottomBlendSteps) {
                                    val u = i / BottomBlendSteps.toFloat()
                                    add((start + span * u) / h to Color.White.copy(alpha = 1f - ease01(u)))
                                }
                                // With [BottomBlendTail] = 0 the band ends on
                                // the edge itself, where the trailing stop
                                // already sits — one zero stop, not two.
                                if (end < h) add(end / h to Color.Transparent)
                                add(1f to Color.Transparent)
                            }.toTypedArray(),
                            startY = 0f,
                            endY = h,
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = brush, blendMode = BlendMode.DstIn)
                        }
                    }
                },
        )
        // Layer 2 — readability, shaped around the copy instead of a plate.
        HeroReadabilityVeil(
            heroHeightPx = heroPx,
            topInsetDp = topInset,
            headerHeightDp = headerDp,
            contentHeightDp = contentDp,
            modifier = Modifier.fillMaxSize(),
        )
        // Layer 3 — the complete foreground, every part of it on the artwork.
        // The Hero is a plain item of the list, so this block scrolls up under
        // the status bar with the rest of the content. A screen-space fade
        // keeps the *copy* out of the status-bar icons and the camera cutout:
        // the mask's band is fixed on screen (from [statusBarDp] through the
        // same [HeroTopInset] the date is inset by) while the block moves
        // beneath it, so at rest nothing is touched — the date starts exactly
        // where the fade ends and stays fully opaque. Only the copy is masked;
        // the artwork and the veil below stay edge-to-edge (ADR-33), and no
        // layout, bounds or scroll behaviour changes.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MomentumSpacing.screen)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawContent()
                    val h = size.height
                    // Local y of the band's edges: screenY = localY + heroTopPx.
                    val y0 = statusBarDp.toPx() - heroTopPx
                    val y1 = y0 + HeroTopInset.toPx()
                    when {
                        // The whole block sits below the band: nothing fades.
                        y1 <= 0f -> Unit
                        // The whole block sits above the band: fully faded out.
                        y0 >= h -> drawRect(Color.Transparent, blendMode = BlendMode.DstIn)
                        else -> {
                            val s1 = y0.coerceIn(0f, h) / h
                            val s2 = y1.coerceIn(0f, h) / h
                            // Stops are strictly ordered by construction (s2 > s1
                            // in every reachable branch), so no stop is ever
                            // clamped into its neighbour.
                            val mask = Brush.verticalGradient(
                                colorStops = buildList {
                                    add(0f to Color.Transparent)
                                    if (s1 > 0f) add(s1 to Color.Transparent)
                                    add(s2 to Color.White)
                                    if (s2 < 1f) add(1f to Color.White)
                                }.toTypedArray(),
                                startY = 0f,
                                endY = h,
                            )
                            drawRect(mask, blendMode = BlendMode.DstIn)
                        }
                    }
                },
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
                onSizeChanged = { size -> contentPx = size.height },
            )
            // The artwork's bottom ramp lives here, below the message, and is
            // the only thing between the copy and the activity section.
            Spacer(Modifier.height(HeroBottomInset))
        }
    }
}

/**
 * Home's app bar, inside the Hero surface: date above greeting, Settings at the
 * top-right (DECISIONS §5/§12). It reports its measured height so the Hero can
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
            // DECISIONS §5: concise date above the greeting, so it gives
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
            contentDescription = stringResource(R.string.cd_settings),
            onClick = onOpenSettings,
        )
    }
}

/**
 * Ring, completion count and motivational message (DECISIONS §4/§9). Reports
 * its measured size (both axes) for the same reason as the header: the height
 * of the Hero and the veil are shaped around what is actually measured.
 *
 * The ring and its count are hidden when nothing is eligible, so an empty day
 * shows no misleading 0% — and with the empty-day line retired (Phase 2C) an
 * empty day renders **no copy at all**, so the artwork runs straight into the
 * No-Activity card below the Hero.
 *
 * The block is **rigid**: it is laid out identically at every collapse value
 * (Phase 2C second pass). [ringScale] is the one decorative interpolation, and
 * it is drawn inside the ring's reserved [RingBounds] box so even the scale
 * cannot reach the count. Nothing here translates, so the block can never
 * overlap itself and always travels exactly with the list.
 */
@Composable
private fun HeroContent(
    progress: Float,
    doneCount: Int,
    totalCount: Int,
    message: String,
    ringScale: Float,
    reduceMotion: Boolean,
    onSizeChanged: (IntSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(HeroContentTag)
            .onSizeChanged { size -> onSizeChanged(size) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
    ) {
        if (totalCount > 0) {
            // Reserved bounds for the ring: the scale-up is absorbed here, so
            // the spacing around the ring is constant at every collapse value
            // and the orb can never grow into the count below it.
            Box(
                modifier = Modifier.size(RingBounds),
                contentAlignment = Alignment.Center,
            ) {
                ProgressOrb(
                    progress = progress,
                    size = HeroRingSize,
                    glowEnabled = !reduceMotion,
                    animate = !reduceMotion,
                    modifier = Modifier.graphicsLayer {
                        scaleX = ringScale
                        scaleY = ringScale
                    },
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
            ) {
                Text(
                    text = stringResource(R.string.hero_progress_count, doneCount, totalCount),
                    style = MaterialTheme.typography.titleMedium,
                    color = c.textPrimary,
                    textAlign = TextAlign.Center,
                )
                if (message.isNotBlank()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else if (message.isNotBlank()) {
            // No ring to coordinate with (nothing eligible today): a lone line
            // stays exactly where it is.
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
 * The Hero's readability treatment — **not** a plate, and not a wash.
 *
 * One vertical gradient tinted with the page background whose stops follow the
 * measured copy. It is fully transparent at the Hero's **top edge** — so the
 * Aurora is untouched where the artwork meets the page background, with no band
 * of its own — rises only to reach the date, holds behind the greeting, dips
 * across the open band (so the ribbons read there), comes back up behind
 * ring/count/message, and returns to transparent on exactly the boundary the
 * artwork's own blend dies on ([BottomBlendTail] from the Hero's edge).
 *
 * Phase 2C second pass — the shape is now built from [VeilSteps] samples of a
 * piecewise profile whose every junction is eased, instead of eight raw stops
 * joined by straight segments. Two defects are gone with that:
 *  - every stop pair used to meet at a nonzero slope (date ramp, open-band
 *    valley, content-band rise, tail) and each of those kinks read as a faint
 *    horizontal edge across the artwork;
 *  - the stops were ordered by *clamping*, so whenever the measured blocks put
 *    `contentTop` above the open-band midpoint the ordering collapsed and the
 *    gradient jumped a whole alpha step in a few pixels.
 * The anchors are now derived first and guaranteed monotone, the open-band dip
 * scales with the band's own width (a 20 dp collapsed gap hosts no valley, so
 * there is nothing to step through), and both the artwork and the veil reach
 * zero with the same eased profile at the same [BottomBlendTail] boundary —
 * one compositing strategy, two multiplicative masks, no seam.
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
                val header = headerHeightDp.toPx()
                val content = contentHeightDp.toPx()
                // Mirrors the Column: the only weighted spacer is above the
                // content block, so the copy always ends HeroBottomInset above
                // the Hero's bottom edge whatever the collapse value is.
                val contentTop = (h - HeroBottomInset.toPx() - content).coerceIn(0f, h)
                val contentEnd = (contentTop + content).coerceIn(contentTop, h)
                // Anchors, in px, ordered by construction: date ramp, header
                // hold, the open band's valley (four tenths in, six tenths
                // out), the content hold, and the shared clear tail.
                val rise = topInset.coerceIn(0f, contentTop)
                val hold = (topInset + header).coerceIn(rise, contentTop)
                val band = contentTop - hold
                val dip1 = hold + band * 0.4f
                val dip2 = hold + band * 0.6f
                // The valley is only as deep as the band is wide: the collapsed
                // 20 dp gap has no room for it, so the dip fades out with the
                // band's own width and only reaches [VeilOpen] once the
                // expanded headroom gives the ribbons somewhere to show.
                val dip = (band / OpenBandWidth.toPx()).coerceIn(0f, 1f)
                val dipFloor = headerAlpha - dip * dip * (headerAlpha - VeilOpen)
                val clearAt = (h - BottomBlendTail.toPx()).coerceIn(contentEnd, h)

                fun alphaAt(y: Float): Float = when {
                    y <= rise -> headerAlpha * ease01(y / rise.coerceAtLeast(1f))
                    y <= hold -> headerAlpha
                    y <= dip1 -> headerAlpha + (dipFloor - headerAlpha) *
                        ease01((y - hold) / (dip1 - hold).coerceAtLeast(1f))
                    y <= dip2 -> dipFloor
                    y <= contentTop -> dipFloor + (contentAlpha - dipFloor) *
                        ease01((y - dip2) / (contentTop - dip2).coerceAtLeast(1f))
                    y <= contentEnd -> contentAlpha
                    y <= clearAt -> contentAlpha *
                        (1f - ease01((y - contentEnd) / (clearAt - contentEnd).coerceAtLeast(1f)))
                    else -> 0f
                }

                // Same hue as the surface it veils, so a zero-alpha stop never
                // bleeds a dark halo into the artwork. Sampled once per measured
                // height, never per frame.
                val stops = buildList {
                    for (i in 0..VeilSteps) {
                        val u = i / VeilSteps.toFloat()
                        add(u to c.background.copy(alpha = alphaAt(u * h)))
                    }
                }.toTypedArray()
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        Brush.verticalGradient(
                            colorStops = stops,
                            startY = 0f,
                            endY = h,
                        ),
                    )
                }
            }
        },
    )
}

/**
 * Layout-contract tags for the Home Hero's layers. They carry no accessibility
 * meaning — they exist so the composition ("the artwork is the background of the
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
 * Bottom inset: the breathing room between the message and the Hero's bottom
 * edge. The artwork's own [BottomDissolve] may run past it — the eased
 * [BottomBlendStart] band owns the last strip and reaches zero exactly at the
 * Hero's edge — so the activity section continues straight out of the fade
 * instead of after a void (ISSUE-018: 16 dp of inset + the list's
 * `MomentumSpacing.item` = ≈ 28 dp total, most of it still artwork).
 */
private val HeroBottomInset = 16.dp

/** Minimum breathing room between the header and the ring/count/message block. */
private val HeaderContentGap = 20.dp

/**
 * No top ramp on Home: the artwork keeps full strength right up to the Hero's
 * top edge, so the date, greeting and Settings sit directly on Aurora with no
 * band of plain page background above them. (Habits keeps its 44 dp ramp — that
 * is a per-screen choice, not a component default.)
 */
private val TopDissolve = 0.dp

/** Bottom ramp: artwork melts into the page background below the message. */
private val BottomDissolve = 40.dp

/**
 * Eased bottom blend (Phase 2C): the artwork's alpha starts easing out
 * [BottomBlendStart] above the Hero's bottom edge and reaches zero at the edge
 * itself ([BottomBlendTail] = 0). Because the profile is an S-curve sampled
 * in [BottomBlendSteps] steps, neither end of the blend has a slope
 * discontinuity, and because it reaches zero exactly on the edge, the artwork
 * and the section below share no visible boundary — the fade runs out as the
 * Hero does. The band starts above [BottomDissolve] so it takes over from the
 * artwork's own linear ramp instead of competing with it, and it begins
 * 32 dp above the message (48 − 16), so the last thing the copy sits on is
 * Aurora melting away rather than page background (ISSUE-018).
 */
private val BottomBlendStart = 48.dp

/**
 * Flat, artwork-free strip at the very bottom of the Hero. Kept at 0 so the
 * blend dies **on** the Hero's edge and the fade continues straight into the
 * activity section (ISSUE-018).
 */
private val BottomBlendTail = 0.dp

/** Samples of the eased S-curve — enough to be slope-smooth on screen. */
private const val BottomBlendSteps = 24

/** Samples of the veil's profile — the same slope-smoothness budget. */
private const val VeilSteps = 48

/**
 * Width of the open band the veil dips into when the Hero is expanded. The dip
 * fades in with the band's own width, so the collapsed 20 dp gap stays a plain
 * eased rise (no valley to step through) and only the expanded band reaches
 * [VeilOpen].
 */
private val OpenBandWidth = 72.dp

/**
 * The room the collapse travels through while expanded, and therefore the
 * height of the open Aurora band between the greeting and the ring. All of the
 * Hero's extra height lives there, never below the message.
 */
private val CollapseHeadroom = 72.dp

/** Progress ring diameter inside the Hero. */
private val HeroRingSize = 96.dp

/** Ring growth while expanded — the one content scale in the Hero. */
private const val RingScaleHeadroom = 0.12f

/**
 * Layout box the ring scale is drawn into: [HeroRingSize] grown by the maximum
 * [RingScaleHeadroom]. The orb is centred in it, so the expanded-state growth
 * is absorbed by empty box instead of eating the spacing below the ring.
 */
private val RingBounds = HeroRingSize * (1f + RingScaleHeadroom)

/**
 * Unit S-curve (zero slope at both ends), shared by the artwork's bottom
 * blend, the readability veil and the mask. Used only for alpha profiles —
 * never to draw artwork (the Hero owns no procedural landscape, ADR-30).
 */
private fun ease01(t: Float): Float {
    val u = t.coerceIn(0f, 1f)
    return u * u * (3f - 2f * u)
}

/** Artwork overscan that gives the parallax somewhere to travel. */
private const val ArtworkScale = 1.12f

/** Artwork drift across a full collapse; frozen under reduced motion. */
private val ParallaxTravel = 12.dp

/**
 * Veil anchors (ISSUE-015). Deliberately far below a flattening 0.86 blanket
 * *and* deliberately close to each other: the old anchors jumped from 0.10 in
 * the open band to 0.58/0.62 behind the ring block, and that level change
 * landed exactly between the greeting and the ring, which is what read as a
 * card edge. The open band and the content plateau are now within 0.12 of each
 * other in both themes, with [VeilHeaderDark]/[VeilHeaderLight] at the
 * midpoint between them, so the artwork stays visibly textured behind the ring
 * block instead of dissolving into the page colour there. The mechanism is
 * untouched (ADR-35's sampled piecewise profile); only these constants moved.
 * Body text still clears 4.5:1 because the veil is the page background itself
 * (AGENTS §9) — if a theme ever fails that, the fix is the text token, not
 * another dose of veil.
 *
 * [VeilOpen] is 0.20 rather than 0.18 because the content cap in Dark (0.32)
 * minus 0.18 would be a 0.14 step, over the 0.12 budget; 0.20 puts Dark at
 * exactly 0.12 and Light at 0.10.
 */
private const val VeilHeaderDark = 0.26f
private const val VeilHeaderLight = 0.25f
private const val VeilOpen = 0.20f
private const val VeilContentDark = 0.32f
private const val VeilContentLight = 0.30f

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
            // An empty day carries no line at all (Phase 2C).
            message = "",
            collapseProgress = 0f,
            reduceMotion = true,
            onOpenSettings = {},
            onCollapseRangePx = {},
        )
    }
}
