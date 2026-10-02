package com.momentum.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.momentum.app.R

/**
 * Manrope (variable, bundled in `res/font/manrope.ttf` — offline, no remote
 * loading). Weights 400/500/600 only; the type scale avoids 800/900 (§4).
 *
 * The seven Design System tokens — Display, Headline, Title, Body,
 * Body Small, Label, Caption — are the source of truth; the remaining
 * Material slots are filled with the nearest token so every `Text` in the
 * app stays on Manrope:
 *
 * | Token | Slots |
 * |---|---|
 * | Display 40/48 600 | displayLarge, displayMedium |
 * | Headline 28/34 600 | displaySmall, headlineLarge |
 * | Title 20/26 600 | headlineMedium, headlineSmall, titleLarge |
 * | Body 16/24 400 | titleMedium, bodyLarge |
 * | Body Small 14/20 400 | titleSmall, bodyMedium |
 * | Label 13/18 500 | labelLarge |
 * | Caption 12/16 500 | bodySmall, labelMedium, labelSmall |
 *
 * Screens must not construct arbitrary TextStyles — read
 * [androidx.compose.material3.MaterialTheme.typography] slots instead.
 */
@OptIn(ExperimentalTextApi::class)
private val Manrope = FontFamily(
    Font(
        R.font.manrope,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        R.font.manrope,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        R.font.manrope,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
)

private val DisplayStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 40.sp,
    lineHeight = 48.sp,
)

/**
 * Numeric figure style (ADR-48 §2): the Display token plus tabular figures.
 *
 * Derived, not taken from the scale: the Display token (40/48, 600) is
 * specified (DS §4); `tnum` is a *demanded-not-specified* refinement. Manrope
 * is a variable font and its OpenType feature table **does** expose `tnum`
 * (verified in `res/font/manrope.ttf` by locating the feature tag in the
 * binary; `pnum` and `zero` are present too), so the setting takes effect.
 * No new weight is registered: 600 is the scale's Display weight.
 *
 * **No longer read by Home** (ADR-48 Amendment Note 5): the progress card's
 * fraction moved to [MomentumHeadlineCompact], which carries the same `tnum`
 * setting. The token **stays declared** — removing a public type token is out of
 * scope, and nothing else may adopt it silently.
 */
@OptIn(ExperimentalTextApi::class)
val MomentumFigureDisplay = DisplayStyle.copy(
    fontFeatureSettings = "tnum",
)

private val HeadlineStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
)

private val TitleStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
)

private val BodyStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

private val BodySmallStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

private val LabelStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 18.sp,
)

private val CaptionStyle = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
)

/**
 * Headline Compact (24/32 600) — the Stitch Aurora reference's `headline-md`.
 * Added by ADR-48 Amendment Note 5 for the horizontal progress card: the
 * fraction row and the "Today is complete" statement.
 *
 * It **carries `tnum`**, which is what preserves ADR-48 §2's rationale on the
 * new composition: the completed digit is three characters narrower at 24 sp
 * than the 40 sp Display figure it replaces, so without tabular figures the
 * fraction would visibly reflow as `done` advances 9 → 10 → 100. Manrope's
 * OpenType table exposes `tnum` (see [MomentumFigureDisplay]), so the setting
 * takes effect on this token too — no substitution, no escalation.
 */
@OptIn(ExperimentalTextApi::class)
val MomentumHeadlineCompact = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    fontFeatureSettings = "tnum",
)

/**
 * Headline Section (20/28 600) — the Stitch reference's `headline-sm`. Added by
 * ADR-48 Amendment Note 5 for the ring's percentage inside the progress card
 * and for Home's two section headings.
 */
val MomentumHeadlineSection = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
)

/**
 * Label Micro (11/14 600, +0.04em) — the Stitch reference's `label-sm`, the
 * design system's documented micro-scale treatment for text below 12 px. Added
 * by ADR-48 Amendment Note 5 for the chrome's date capsule, the ring's `done`
 * unit, and Home's two section status labels; reused by the bottom navigation's
 * tab labels (ADR-49 Amendment Note 1). The tracking is the reference's own
 * value and the reason the token exists rather than reusing Caption.
 */
val MomentumLabelMicro = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.04.em,
)

/**
 * Emphasised Body (16/22 600): the Body token's strong variant, used where a
 * card/streak title needs weight without leaving the type scale.
 */
val MomentumBodyStrong = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
)

/** Lead Body (17/26 400): immersive full-bleed copy that reads a touch larger. */
val MomentumBodyLead = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 26.sp,
)

val MomentumTypography = Typography(
    displayLarge = DisplayStyle,
    displayMedium = DisplayStyle,
    displaySmall = HeadlineStyle,
    headlineLarge = HeadlineStyle,
    headlineMedium = TitleStyle,
    headlineSmall = TitleStyle,
    titleLarge = TitleStyle,
    titleMedium = BodyStyle,
    titleSmall = BodySmallStyle,
    bodyLarge = BodyStyle,
    bodyMedium = BodySmallStyle,
    bodySmall = CaptionStyle,
    labelLarge = LabelStyle,
    labelMedium = CaptionStyle,
    labelSmall = CaptionStyle,
)
