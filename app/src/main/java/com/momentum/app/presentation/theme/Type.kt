package com.momentum.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
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
