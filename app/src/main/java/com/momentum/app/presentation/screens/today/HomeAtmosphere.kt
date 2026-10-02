package com.momentum.app.presentation.screens.today

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.aurora.AuroraBackground
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.aurora.defaultIntensity
import com.momentum.app.presentation.components.MomentumHeroArtwork
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.isDark

/**
 * Home's **one atmosphere** (ADR-47 Amendment Notes 4, 5 and 7; ADR-48
 * Amendment Note 5).
 *
 * Aurora is the *environment*, so it has no height of its own and nothing to
 * desynchronise from: it is the backdrop of Home's **whole content area** — a
 * `fillMaxSize()` sibling behind the persistent top chrome, the scrollable
 * Today content, the FAB and the bottom bar. Three regions are drawn, in this
 * order, and they are three regions of *one* continuous field rather than an
 * image band:
 *
 * - **1a — the ambient Aurora field** ([HomeAurora]): the generated field whose
 *   intensity answers the day (REST → PROGRESS → COMPLETION, plus the transient
 *   MILESTONE) and whose drift is **on** — the approved reference's "Aurora
 *   subtle movement" is delivered by `AuroraBackground`'s own existing loop
 *   (`MomentumMotion.ambient`, 6000 ms = the reference's own `aurora-float`
 *   period, ±3 % of width / ±2.5 % of height ≈ ±8 px at a 320 dp viewport, run
 *   in `drawWithCache.onDrawBehind` with pre-built brushes, and **nulled inside
 *   `AuroraBackground` itself** under reduced motion). No new token, keyframe or
 *   component was added for it, and `code.html`'s `aurora-float` rule stays
 *   unbound because it is defined-but-unbound there too.
 * - **1b — the shared artwork** in the [artworkWindow] region: the bundled
 *   Aurora illustration rendered **complete** at its own intrinsic 1024 × 1536
 *   ratio (`ContentScale.Fit` — never object-cover), top-aligned and scaled to
 *   the window width, with the shared 88 dp bottom dissolve. This region is the
 *   atmosphere's *upper region*, measured **from the physical window top**
 *   (ADR-39's no-top-fade contract is load-bearing here: the window's top edge
 *   *is* the window's top edge, so the artwork is full strength behind the
 *   status bar and the persistent chrome floats over it). Beneath it the
 *   ambient field and the page background continue as the same lavender
 *   atmosphere — no separate rectangle, gradient or scrim is drawn for the
 *   transition (`task.md` §4.8, Device Finding #1).
 * - **1c — the one readability scrim** ([HeroScrim]): a single four-stop
 *   `c.background` gradient spanning the **whole** content area, so the chrome
 *   and the scrollable content sit under the *same* gradient and there is no
 *   stop, plate or seam at the chrome's bottom edge.
 *
 * **The window reserves nothing.** Nothing competes for the artwork's space, so
 * it can never be squeezed out and BOUND-1 is structural rather than measured:
 * `artworkWindow(width) − ArtworkHandOff ≥ ProgressCardRingSize + 2 ×
 * MomentumSpacing.standard` at every swept width (392 dp @320 · 452 dp @360 ·
 * 530 dp @412, against a 128 dp requirement).
 *
 * @param state the day's Aurora state, mapped by `homeAuroraState`
 * @param reduceMotion nulls the intensity tween (`snap`, not a short tween)
 * @param windowWidth the root's own `BoxWithConstraints.maxWidth`, read once per
 *   composition for [artworkWindow] and never scroll-dependent
 */
@Composable
fun HomeAtmosphere(
    state: AuroraState,
    reduceMotion: Boolean,
    windowWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        HomeAurora(
            state = state,
            reduceMotion = reduceMotion,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(artworkWindow(windowWidth))
                .align(Alignment.TopStart),
        ) {
            MomentumHeroArtwork(
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(AtmosphereArtworkTag),
            )
        }
        HeroScrim(Modifier.fillMaxSize())
    }
}

/**
 * Layout-contract tags for the atmosphere and its regions. They carry no
 * accessibility meaning — they exist so the composition ("the atmosphere is the
 * backdrop of the whole content area, and the artwork is its upper region") can
 * be asserted at bounds level instead of taken on trust.
 */
internal const val HomeAtmosphereTag = "homeAtmosphere"
internal const val AtmosphereArtworkTag = "homeAtmosphereArtwork"

/**
 * The shared [MomentumHeroArtwork]'s bottom dissolve, restated as a **measurement**
 * rather than a parameter. The component's own constant is `private` to
 * `MomentumHeroArtwork.kt`, which this composition must not modify, and its
 * caller contract (ADR-30) lets a caller pass only band amounts — so the 88 dp
 * hand-off is named here and asserted against BOUND-1.
 */
internal val ArtworkHandOff: Dp = 88.dp

/**
 * The artwork's **intrinsic height ratio**: 1536 / 1024 = 1.5, the bundled
 * `habits_hero_light` / `habits_hero_dark` pair's own dimensions (both pinned
 * at 1024 × 1536 by `HabitsHeroAssetsTest`).
 *
 * `task.md` §4.8 (Device Finding #1, binding human ruling) withdraws the former
 * `460/640` band: Home renders the **complete** Aurora — waves, glow, sparkles
 * and stars — top-aligned and scaled to the window width, so the window's
 * height is this ratio × the width and nothing is cropped away.
 *
 * This is a **ratio**, not a dp value: nothing here may be tuned, because
 * raising or lowering it changes the artwork, not the layout.
 */
internal val ArtworkIntrinsicRatio: Float = 1536f / 1024f

/**
 * The artwork's window at [windowWidth] — the atmosphere's upper region,
 * measured **from the physical window top**, carrying the **complete** artwork
 * at its intrinsic ratio. It is **width-proportional** (the one input ADR-47
 * Amendment Note 7 newly allows) and still **constant in every state**: empty,
 * populated or complete, at any font scale and at any scroll position. It is
 * never a function of activity count, copy height, the chrome's measured height
 * or scroll.
 *
 * ```
 * artworkWindow(320.dp) = 480.00.dp
 * artworkWindow(360.dp) = 540.00.dp
 * artworkWindow(412.dp) = 618.00.dp
 * ```
 *
 * Predecessors: the fixed 184 dp (`HeroRingSize + MomentumSpacing.standard +
 * ArtworkHandOff`), then the withdrawn 460/640 band (258.75 dp @360) — both
 * cover-cropped the illustration. §4.8 supersedes them with the intrinsic
 * ratio above.
 */
internal fun artworkWindow(windowWidth: Dp): Dp = (ArtworkIntrinsicRatio * windowWidth.value).dp

/**
 * The one constant readability scrim (R3): a single vertical gradient tinted
 * with the page background, transparent at the content area's top edge, rising
 * to one peak across the middle band, and reaching alpha 0 again exactly at the
 * content area's bottom edge — behind the floating glass bar — so no band of
 * veiled `c.background` ever sits against un-tinted Aurora. It spans the whole
 * atmosphere with **no stop at the chrome's bottom edge**, which is what makes
 * the persistent chrome read as part of this one environment rather than as an
 * unrelated header. Drawn with one cached brush per measured size — no per-frame
 * allocation, and nothing here reads scroll state.
 */
@Composable
private fun HeroScrim(modifier: Modifier = Modifier) {
    val c = MomentumThemeColors.current
    val peak = if (c.isDark) ScrimPeakDark else ScrimPeakLight
    Box(
        modifier = modifier.drawWithCache {
            val h = size.height
            if (h <= 0f) {
                onDrawBehind { }
            } else {
                // Zero-alpha stops keep the tint's own hue, so the transparent
                // ends never bleed a dark halo into the artwork.
                val clear = c.background.copy(alpha = 0f)
                val tinted = c.background.copy(alpha = peak)
                val brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to clear,
                        ScrimPeakStart to tinted,
                        ScrimPeakEnd to tinted,
                        1f to clear,
                    ),
                    startY = 0f,
                    endY = h,
                )
                onDrawBehind { drawRect(brush) }
            }
        },
    )
}

/** Start of the scrim's constant peak, as a fraction of the content area. */
private const val ScrimPeakStart = 0.20f

/** End of the scrim's constant peak, as a fraction of the content area. */
private const val ScrimPeakEnd = 0.80f

/**
 * Scrim peaks, re-measured **from scratch** for the TASK-018 composition
 * (ADR-47 Amendment Note 7 §5, human ruling 2026-10-01 §4.7 r6). The acceptance
 * measurement is the ISSUE-020 **region-sample** method (ADR-47 Amendment Note 1):
 * each band's text rectangle is sampled from the composited page background +
 * Aurora field + artwork (the complete intrinsic-ratio composition rendered
 * with `ContentScale.Fit`, plus the shared 88 dp bottom dissolve) + this scrim
 * over the **whole content area**, reduced to one mean background colour, and
 * measured against its ink token; the **minimum region ratio across the sweep
 * governs**. A per-pixel minimum over the
 * artwork's high-frequency detail is informational only — in Dark it would need
 * a peak near 0.53, outside the approved `[0.15, 0.35]` envelope and against
 * "readability without flattening".
 *
 * **Superseded baselines.** ADR-47 Amendment Note 6's figures were taken over a
 * 184 dp band and are **not** carried forward; neither is the 460/640 band
 * sweep, since `task.md` §4.8 (Device Finding #1) withdrew that band and now
 * renders the **complete** artwork at its intrinsic ratio (1.5 × width) — a
 * taller opaque region than either predecessor under the same scrim. The sweep
 * described above therefore predates the §4.8 geometry: the peaks below stand
 * unchanged inside the approved `[0.15, 0.35]` envelope, and re-evaluating the
 * foreground against the final artwork is ISSUE-020's, not this correction's.
 *
 * **Both peaks stand unchanged.** `ScrimPeakLight = 0.26f` /
 * `ScrimPeakDark = 0.22f` both remain inside the approved `[0.15, 0.35]`
 * envelope, `ScrimPeakStart` / `ScrimPeakEnd` and the four-stop profile are
 * untouched, and no ink token or the artwork itself was changed. Only the peak
 * value was ever in scope.
 */
private const val ScrimPeakLight = 0.26f
private const val ScrimPeakDark = 0.22f

/**
 * Home's Aurora field wrapper (R4): the **drift is on** and the intensity is
 * tweened on state change over `MomentumMotion.emphasis`, so the field responds
 * to the day instead of jumping and still breathes like the approved design's.
 * Reduced motion changes the intensity instantly (`snap`, not a shortened
 * tween) and `AuroraBackground` nulls its own drift phase. The intensity tween is
 * local to this composable, so only it recomposes during the one-shot change —
 * and because the atmosphere reads no scroll state at all, nothing in Home
 * recomposes while the user flings.
 */
@Composable
private fun HomeAurora(
    state: AuroraState,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    val intensity by animateFloatAsState(
        targetValue = state.defaultIntensity,
        animationSpec = if (reduceMotion) {
            snap()
        } else {
            tween(durationMillis = MomentumMotion.emphasis, easing = MomentumMotion.easeOut)
        },
        label = "homeAuroraIntensity",
    )
    AuroraBackground(
        state = state,
        intensity = intensity,
        // ADR-48 Amendment Note 6 (M1): the reference's `aurora-float` is
        // defined-but-unbound there, so Momentum's own existing 6 s drift is the
        // delivery mechanism for the human-ruled "Aurora subtle movement".
        // AuroraBackground gates it again on reduced motion.
        animated = true,
        modifier = modifier,
    )
}

@Preview(name = "Home atmosphere light", showBackground = true, widthDp = 360, heightDp = 480)
@Composable
private fun HomeAtmosphereLightPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        HomeAtmosphere(
            state = AuroraState.PROGRESS,
            reduceMotion = false,
            windowWidth = 360.dp,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(name = "Home atmosphere dark milestone", showBackground = true, widthDp = 360, heightDp = 480)
@Composable
private fun HomeAtmosphereDarkPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        HomeAtmosphere(
            state = AuroraState.MILESTONE,
            reduceMotion = false,
            windowWidth = 360.dp,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
