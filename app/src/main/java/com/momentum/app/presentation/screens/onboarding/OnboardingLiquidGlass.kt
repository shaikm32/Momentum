package com.momentum.app.presentation.screens.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumDurations
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing

/**
 * Onboarding-only Liquid Glass implementation.
 *
 * Photographic onboarding needs brighter, more luminous glass than the
 * in-app system (which tints from theme surfaces): low-alpha white/cyan
 * fills, luminous white borders, top highlights and cyan-tinted glow
 * shadows, tuned to sit over cinematic photography — not over app chrome.
 * Kept in this file so the language stays scoped to onboarding.
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

/** Frosted illustration panel (Today preview, finale quote card). */
@Composable
fun OnboardingGlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(OnboardingGlass.panelCorner)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onboardingGlass(shape, glow = Color(0xFF7DD3FC)),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        content()
    }
}

/**
 * Luminous pill CTA: translucent white/cyan glass, bright edge, dark text,
 * soft shadow. 56 dp tall for a comfortable thumb target.
 */
@Composable
fun OnboardingGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
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
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color(0xFF7DD3FC).copy(alpha = 0.4f),
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.55f),
                    0.5f to Color(0xFFD6ECFF).copy(alpha = OnboardingGlass.ctaFillAlpha + 0.12f),
                    1f to Color(0xFFB9CFF5).copy(alpha = OnboardingGlass.ctaFillAlpha),
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
                onClick = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
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

/** Floating glass feature tile (~64 dp) with a luminous icon. */
@Composable
fun OnboardingGlassIconTile(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tileSize: Dp = 64.dp,
    label: String? = null,
) {
    val shape = RoundedCornerShape(OnboardingGlass.tileCorner)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(tileSize)
                .onboardingGlass(shape, glow = Color(0xFF7DD3FC)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.88f),
            )
        }
    }
}

/**
 * Central glass orb: layered translucent sphere with a top-left refraction
 * highlight, cyan glow and a soft grounding shadow. [content] floats inside
 * (e.g. the growing plant).
 */
@Composable
fun OnboardingGlassOrb(
    modifier: Modifier = Modifier,
    orbSize: Dp = 190.dp,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.size(orbSize), contentAlignment = Alignment.Center) {
        // Grounding shadow.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(orbSize * 0.62f)
                .height(orbSize * 0.10f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                        ),
                    ),
                    CircleShape,
                ),
        )
        // Glass sphere.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        0f to Color.White.copy(alpha = 0.30f),
                        0.35f to Color(0xFFBFE3FF).copy(alpha = 0.14f),
                        0.7f to Color(0xFF7DD3FC).copy(alpha = 0.08f),
                        1f to Color(0xFF3B5B8C).copy(alpha = 0.22f),
                    ),
                    shape = CircleShape,
                )
                .border(
                    BorderStroke(Dp.Hairline, Color.White.copy(alpha = 0.5f)),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
        // Refraction crescent, upper left.
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = orbSize * 0.16f, top = orbSize * 0.12f)
                .size(width = orbSize * 0.30f, height = orbSize * 0.14f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent,
                        ),
                    ),
                    RoundedCornerShape(MomentumRadii.pill),
                ),
        )
    }
}

/** Five-dot page indicator: active dot brighter and wider, softly animated. */
@Composable
fun OnboardingPageIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val active = i == current
            val targetWidth = if (active) 22.dp else 8.dp
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
