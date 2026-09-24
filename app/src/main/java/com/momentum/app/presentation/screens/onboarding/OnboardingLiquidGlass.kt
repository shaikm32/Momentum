package com.momentum.app.presentation.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumDurations
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.Twilight

/**
 * Onboarding-only Liquid Glass implementation (Twilight Journey).
 *
 * Photographic onboarding needs brighter, more luminous glass than the
 * in-app system (which tints from theme surfaces): low-alpha white/warm
 * fills, luminous white borders, top highlights and a whisper of golden
 * shadow, tuned to sit over cinematic twilight photography — not over app
 * chrome. No cyan/purple neon glow. Kept in this file so the language stays
 * scoped to onboarding.
 *
 * White is used here as light itself (fill/border/highlight alphas), never
 * as an opaque background; text always sits over the readability gradient
 * in [OnboardingBackdrop].
 */
object OnboardingGlass {
    /** Panel / tile fill: white 10–18%. */
    const val fillAlpha = 0.14f

    /** Luminous hairline border: white 30–55%. */
    const val borderAlpha = 0.42f

    /** Top-edge illumination streak: white 15–25%. */
    const val highlightAlpha = 0.20f

    /** Cyan accent glow on hero objects: 10–25%. */
    const val glowAlpha = 0.18f

    /** CTA glass: brighter luminous pill. */
    const val ctaFillAlpha = 0.28f
    const val ctaBorderAlpha = 0.55f

    val tileCorner = 22.dp
    val panelCorner = 24.dp
}

/**
 * Base glass treatment: translucent fill, luminous top wash, hairline
 * border, soft shadow. One call site for the whole onboarding language.
 */
@Composable
private fun Modifier.onboardingGlass(
    shape: Shape,
    fillAlpha: Float = OnboardingGlass.fillAlpha,
    borderAlpha: Float = OnboardingGlass.borderAlpha,
    glow: Color? = null,
): Modifier {
    val shadowed = if (glow != null) {
        this.shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.35f),
            spotColor = glow.copy(alpha = OnboardingGlass.glowAlpha),
        )
    } else {
        this
    }
    return shadowed
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                0f to Color.White.copy(alpha = OnboardingGlass.highlightAlpha),
                0.42f to Color.White.copy(alpha = fillAlpha),
                1f to Color(0xFFBFE3FF).copy(alpha = fillAlpha * 0.8f),
            ),
            shape = shape,
        )
        .border(BorderStroke(Dp.Hairline, Color.White.copy(alpha = borderAlpha)), shape)
}

/**
 * Luminous pill CTA: genuine liquid glass — a translucent frosted surface the
 * artwork stays visible through, with a top-edge light streak, a luminous
 * hairline border and soft depth. Dark navy text + arrow. 56 dp tall.
 *
 * Layering (all translucent, artwork flows through): soft shadow → glass
 * fill → hairline border → inner top highlight → content.
 */
@Composable
fun OnboardingGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.AutoMirrored.Rounded.ArrowForward,
) {
    val interaction = remember { MutableInteractionSource() }
    val reduceMotion = rememberReduceMotion(null)
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduceMotion) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ctaPress",
    )
    val shape = RoundedCornerShape(MomentumRadii.pill)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.30f),
                spotColor = Twilight.GoldenAmber.copy(alpha = 0.25f),
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.34f),
                    0.45f to Twilight.Moonlight.copy(alpha = 0.26f),
                    1f to Twilight.WarmPeach.copy(alpha = 0.24f),
                ),
                shape = shape,
            )
            .border(
                BorderStroke(Dp.Hairline, Color.White.copy(alpha = OnboardingGlass.ctaBorderAlpha)),
                shape,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = text,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) { contentDescription = text },
        contentAlignment = Alignment.Center,
    ) {
        // Inner top light: the streak that sells the glass thickness.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .align(Alignment.TopCenter)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.34f),
                        1f to Color.White.copy(alpha = 0f),
                    ),
                ),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0A1B2E),
            )
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0A1B2E),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/**
 * Compact liquid-glass capsule for Skip: the same glass language as the CTA
 * (translucent fill with a top-light wash, hairline border, faint glow) in
 * ONE single surface at a lightweight scale, with near-white semibold text
 * for readability over any part of the artwork. Minimum 48 dp touch target.
 *
 * NOTE: this button must stay wrap-content. Do NOT add a `fillMaxWidth()`
 * child (e.g. a highlight overlay) — inside a wrap-content Box such a child
 * measures against the incoming max constraints and stretches the whole
 * pill (and its glass background) to full screen width.
 */
@Composable
fun OnboardingGlassSkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val reduceMotion = rememberReduceMotion(null)
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduceMotion) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "skipPress",
    )
    val shape = RoundedCornerShape(MomentumRadii.pill)
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Twilight.GoldenAmber.copy(alpha = 0.18f),
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.34f),
                    0.5f to Color.White.copy(alpha = 0.16f),
                    1f to Twilight.WarmPeach.copy(alpha = 0.14f),
                ),
                shape = shape,
            )
            .border(
                BorderStroke(Dp.Hairline, Color.White.copy(alpha = 0.45f)),
                shape,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = "Skip",
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) { contentDescription = "Skip onboarding" }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Skip",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Twilight.Moonlight,
        )
    }
}

/** Five-dot page indicator: active page is an elongated white pill. */
@Composable
fun OnboardingPageIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val active = i == current
            val targetWidth = if (active) 24.dp else 8.dp
            val width by animateDpAsState(
                targetValue = targetWidth,
                animationSpec = tween(if (reduceMotion) 0 else MomentumDurations.medium),
                label = "dotWidth",
            )
            val targetAlpha = if (active) 0.95f else 0.35f
            val alpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = tween(if (reduceMotion) 0 else MomentumDurations.medium),
                label = "dotAlpha",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = alpha)),
            )
        }
    }
}
