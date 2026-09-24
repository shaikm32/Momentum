package com.momentum.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.isDark

/**
 * Liquid Glass primitives. Every glass surface in the app is one of three
 * hierarchy levels rendered by [liquidGlass]: translucent theme fill, a
 * hairline light border, a top-edge illumination streak, and (for floating
 * layers only) a soft tinted shadow.
 *
 * No backdrop blur is used anywhere: minSdk 26 has no reliable cross-device
 * blur API and no fragile dependency is worth it. Depth comes from
 * translucency over the ambient [AppBackground] + highlight + shadow, so the
 * UI stays beautiful with blur unavailable.
 */
enum class GlassLevel(val fillAlpha: Float, val elevation: Dp) {
    /** Floating layers: hero, nav bar, dialogs, primary buttons. */
    HERO(MomentumGlass.heroFill, MomentumGlass.heroElevation),

    /** Standard cards, rows, panels, sheets. */
    CARD(MomentumGlass.cardFill, Dp.Hairline),

    /** Chips, badges, tiles, small controls. */
    CONTROL(MomentumGlass.controlFill, Dp.Hairline),
}

/**
 * Applies the glass treatment: optional soft shadow, translucent vertical
 * wash (bright top edge fading into the fill), hairline light border.
 * Shadows stay whisper-quiet (Twilight principle: light comes from the
 * environment, components never glow on their own).
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape,
    level: GlassLevel = GlassLevel.CARD,
): Modifier {
    val c = LocalMomentumColors.current
    val dark = c.isDark
    val fill = c.surface.copy(alpha = level.fillAlpha)
    val topLight = if (dark) {
        Color.White.copy(alpha = MomentumGlass.highlight)
    } else {
        Color.White.copy(alpha = 0.55f)
    }
    val border = if (dark) {
        Color.White.copy(alpha = MomentumGlass.borderDark)
    } else {
        c.textPrimary.copy(alpha = MomentumGlass.borderLight)
    }
    val shadowed = if (level.elevation > Dp.Hairline) {
        this.shadow(
            elevation = level.elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.35f),
            spotColor = c.primary.copy(alpha = 0.10f),
        )
    } else {
        this
    }
    return shadowed
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                0f to topLight,
                0.38f to fill,
                1f to fill,
            ),
            shape = shape,
        )
        .border(BorderStroke(Dp.Hairline, border), shape)
}

/**
 * Subtle press scale for tactile glass controls. Disabled entirely under
 * Reduce Motion (tap still works instantly — only the flourish is removed).
 */
@Composable
fun Modifier.glassPress(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.97f,
): Modifier {
    val reduceMotion = rememberReduceMotion(null)
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduceMotion) pressedScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "glassPress",
    )
    return this.scale(scale)
}

/** Primary glass card. Column content, standard rhythm. */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    level: GlassLevel = GlassLevel.CARD,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(MomentumRadii.card)
    val base = modifier
        .fillMaxWidth()
        .liquidGlass(shape, level)
        .glassPress(interaction)
    val clickable = if (onClick != null) {
        base.clickable(
            interactionSource = interaction,
            indication = null,
            role = Role.Button,
            onClick = onClick,
        )
    } else {
        base
    }
    Column(
        modifier = clickable.padding(MomentumSpacing.card),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        content = content,
    )
}

/** Primary CTA: confident glass button with a soft glow shadow. */
@Composable
fun LiquidGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    secondary: Boolean = false,
    destructive: Boolean = false,
) {
    val c = LocalMomentumColors.current
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(MomentumRadii.control)
    val container = when {
        destructive -> c.danger.copy(alpha = 0.90f)
        secondary -> c.surface.copy(alpha = 0.6f)
        else -> c.primary.copy(alpha = 0.92f)
    }
    val contentColor = when {
        destructive -> c.onAccent
        secondary -> c.primary
        else -> c.onPrimary
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = MomentumHeights.button)
            .then(
                if (!secondary && !destructive) {
                    Modifier.shadow(
                        elevation = 10.dp,
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = c.primary.copy(alpha = 0.35f),
                    )
                } else if (destructive) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = c.danger.copy(alpha = 0.35f),
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = if (secondary || destructive) 0.06f else 0.22f),
                    0.45f to container,
                    1f to container,
                ),
                shape = shape,
            )
            .border(
                BorderStroke(
                    Dp.Hairline,
                    if (secondary) {
                        c.textSecondary.copy(alpha = 0.25f)
                    } else if (destructive) {
                        Color.White.copy(alpha = 0.22f)
                    } else {
                        Color.White.copy(alpha = 0.28f)
                    },
                ),
                shape,
            )
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = MomentumSpacing.card),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) contentColor else c.textSecondary,
        )
    }
}

/** Round glass icon button with an active state. */
@Composable
fun LiquidGlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    tint: Color? = null,
) {
    val c = LocalMomentumColors.current
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(MomentumHeights.iconTarget)
            .liquidGlass(CircleShape, GlassLevel.CONTROL)
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint ?: if (active) c.primary else c.textSecondary,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** Floating glass bottom navigation bar. */
@Composable
fun LiquidGlassBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(shape, GlassLevel.HERO)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** Single tab inside [LiquidGlassBottomBar]: icon + tiny label, soft active glow. */
@Composable
fun RowScope.LiquidGlassTab(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val c = LocalMomentumColors.current
    val reduceMotion = rememberReduceMotion(null)
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(18.dp)
    val motionSpec = tween<Float>(durationMillis = MomentumMotion.fast, easing = MomentumMotion.easeOut)
    val colorSpec = tween<Color>(durationMillis = MomentumMotion.fast, easing = MomentumMotion.easeOut)
    val selectedBg by animateColorAsState(
        targetValue = if (selected) c.primary.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = colorSpec,
        label = "tabSelectedBg",
    )
    val selectedBorder by animateColorAsState(
        targetValue = if (selected) c.primary.copy(alpha = 0.40f) else Color.Transparent,
        animationSpec = colorSpec,
        label = "tabSelectedBorder",
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) c.primary else c.textSecondary,
        animationSpec = colorSpec,
        label = "tabIconTint",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected && !reduceMotion) 1.06f else 1f,
        animationSpec = motionSpec,
        label = "tabIconScale",
    )
    Column(
        modifier = Modifier
            .weight(1f)
            .heightIn(min = MomentumHeights.controlMin)
            .clip(shape)
            .background(selectedBg, shape)
            .border(BorderStroke(Dp.Hairline, selectedBorder), shape)
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier
                .size(26.dp)
                .scale(iconScale),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = iconTint,
        )
    }
}
