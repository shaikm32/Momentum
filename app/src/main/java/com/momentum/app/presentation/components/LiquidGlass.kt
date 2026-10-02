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
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumColors
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumLabelMicro
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
    /** Floating layers: hero, nav bar, primary buttons. */
    HERO(MomentumGlass.heroFill, MomentumGlass.heroElevation),

    /**
     * Modal dialogs. The fill is opaque — a dialog has to read as a distinct
     * layer above whatever it covers, so titles, values, input fields and
     * actions keep their contrast regardless of the content behind them.
     */
    DIALOG(MomentumGlass.dialogFill, MomentumGlass.dialogElevation),

    /** Standard cards, rows, panels, sheets. */
    CARD(MomentumGlass.cardFill, Dp.Hairline),

    /** Chips, badges, tiles, small controls. */
    CONTROL(MomentumGlass.controlFill, Dp.Hairline),
}

/**
 * Applies the glass treatment: optional soft shadow, translucent body wash,
 * hairline light border.
 * Shadows stay whisper-quiet (Twilight principle: light comes from the
 * environment, components never glow on their own).
 *
 * Solid levels ([GlassLevel.DIALOG]) blend the top-edge streak *into* an
 * opaque stop instead of painting translucent light over the fill, so the
 * brightest part of the surface never reveals the layer behind it.
 *
 * **[hue] is the one additive parameter** (ADR-48 Amendment Note 5, the
 * smallest documented extension of the glass system the approved reference
 * needs). When non-null, the body wash becomes the reference's 135° chromatic
 * gradient — `lerp(surface, hue, 0.55) @ fillAlpha` → the same tint at half that
 * alpha → the plain fill — so one `Brush` paints the diagonal and no second
 * glass system exists. The hairline and the shadow are untouched, and the hue
 * is a theme token (`auroraLavender` on Home), so the wash is theme-aware by
 * construction.
 *
 * `hue = null` (the default) is **byte-identical** to the pre-TASK-018
 * modifier for all sixteen existing call sites — it is the same vertical
 * `topLight → fill → fill` wash, the same hairline, the same shadow.
 *
 * **[fillAlpha] is the second additive parameter** (ADR-49 Amendment Notes 4/5,
 * the ISSUE-020 Home-scoped remediation). `null` — every existing caller — is
 * byte-identical to today: `effectiveFillAlpha = level.fillAlpha` and the legacy
 * single `topLight → fill → fill` vertical gradient. When non-null, the
 * effective fill becomes [fillAlpha] and the non-hue wash switches to a
 * **two-layer** treatment — a solid `surface @ fillAlpha` underlayer with a
 * fading top streak — so Home alone can read more solid without touching
 * `MomentumGlass` or any other screen. There is still one glass implementation,
 * no Home fork, and rollback is deleting the argument.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape,
    level: GlassLevel = GlassLevel.CARD,
    hue: Color? = null,
    fillAlpha: Float? = null,
): Modifier {
    val c = LocalMomentumColors.current
    val dark = c.isDark
    val solid = level.fillAlpha >= 1f
    val effectiveFillAlpha = fillAlpha ?: level.fillAlpha
    val fill = c.surface.copy(alpha = effectiveFillAlpha)
    val topLight = when {
        solid && dark -> lerp(c.surface, Color.White, MomentumGlass.highlight)
        solid -> c.surface
        dark -> Color.White.copy(alpha = MomentumGlass.highlight)
        else -> Color.White.copy(alpha = 0.55f)
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
    val clipped = shadowed.clip(shape)
    val body = when {
        // `Offset.Zero → Offset.Infinite` is Compose's diagonal gradient, i.e.
        // the reference's `135deg`.
        hue != null && !solid -> {
            val tinted = lerp(c.surface, hue, HueMix)
            clipped.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        tinted.copy(alpha = effectiveFillAlpha),
                        tinted.copy(alpha = effectiveFillAlpha * HueWashMidAlpha),
                        fill,
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
                shape = shape,
            )
        }

        // Home-scoped opt-in: a solid underlayer plus a fading top streak.
        fillAlpha != null -> clipped
            .background(color = fill, shape = shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to topLight,
                    0.38f to Color.Transparent,
                ),
                shape = shape,
            )

        // The default path — every existing caller — unchanged.
        else -> clipped.background(
            brush = Brush.verticalGradient(
                0f to topLight,
                0.38f to fill,
                1f to fill,
            ),
            shape = shape,
        )
    }
    return body.border(BorderStroke(Dp.Hairline, border), shape)
}

/** How far a [Modifier.liquidGlass] `hue` is mixed into the surface token. */
private const val HueMix = 0.55f

/**
 * The mid-stop alpha of the chromatic wash, as a fraction of the fill alpha.
 * Reached **only** by the non-null `hue` branch, i.e. by Home's progress card
 * alone, so raising it is Home-only by construction (ADR-49 Amendment Note 5).
 */
private const val HueWashMidAlpha = 0.8f

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

/**
 * Floating glass bottom navigation bar, rendered to the approved reference's
 * visual (ADR-49 Amendment Note 1, human ruling 2026-10-01 §4.7 r1):
 * **VISUAL = the reference, BEHAVIOUR = Momentum.**
 *
 * `CircleShape` (the reference's `rounded-full`) replaces the previous 26 dp
 * radius, the inner row's `heightIn(min = 64.dp)` is its `h-16`, the padding is
 * `px-space-md` (16 dp) with **no** vertical padding, and `SpaceAround` with
 * vertically centred children is its `justify-around items-center`.
 *
 * `heightIn(min = …)`, never `height(64.dp)`: at font scale 1.0 this renders
 * exactly 64 dp, and beyond it the bar **grows** rather than clipping (§19).
 *
 * The material is unchanged — `GlassLevel.HERO` — and so is every behaviour:
 * this composable and [LiquidGlassTab] have **exactly one call site each**, in
 * `navigation/MomentumNavGraph.kt`'s `Scaffold.bottomBar`, so all four
 * destinations move together and cannot desynchronise. There is no Home-only
 * fork. No blur is added: the reference's `backdrop-filter: blur(24px)
 * saturate(180%)` remains a documented non-adoption (ADR-49 §4), and the
 * reference's `rgba(255,255,255,0.8)` fill and its white `1px` border are
 * deliberately expressed by Momentum's own translucent wash and its
 * theme-resolved hairline — a white border is invisible on a near-white bar,
 * which is exactly why ADR-48 Amendment Note 3 moved the FAB to an ink hairline
 * in Light.
 *
 * ADR-49 Amendment Note 6 (human ruling 2026-10-02 §8.20): the bar opts into the
 * nav-scoped [NavBarFillAlpha] `fillAlpha`, a two-layer solid fill that lifts
 * the Dark inactive label/icon band above 4.5:1 while the inactive ink stays
 * `c.textSecondary` — the selected/inactive hierarchy is preserved, not
 * flattened. Behaviour, routing, padding and the single call site are unchanged.
 */
@Composable
fun LiquidGlassBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(CircleShape, GlassLevel.HERO, fillAlpha = NavBarFillAlpha)
            .heightIn(min = NavBarHeight)
            .padding(horizontal = MomentumSpacing.standard),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/**
 * Single tab inside [LiquidGlassBottomBar] — icon + micro label, with the
 * reference's active icon capsule (ADR-49 Amendment Note 1).
 *
 * | reference | here |
 * |---|---|
 * | `min-w-[62px]` / `min-w-[56px]` | `defaultMinSize`, same values |
 * | `min-h-[48px]` / `min-h-[44px]` | `MomentumHeights.controlMin` (**48 dp for both** — §19 targets are non-negotiable and Stitch's 44 px is below the floor; at font scale 1.0 both natural heights already exceed 48 dp, so the rendered composition is unchanged) |
 * | active `px-4 py-1.5 rounded-full` | `16 dp` horizontal / `6 dp` vertical on a `CircleShape` box |
 * | capsule `linear-gradient(135deg, #c8e1ff 75%, #dcd2ff 85%)` | two **theme-token** stops carrying the reference's exact alphas and angle |
 * | capsule `border: 1px rgba(255,255,255,0.9)` | a literal **alpha**, not a literal colour — the same construction this file already uses for its light-mode top streak. ADR-49 Amendment Note 6 (human ruling 2026-10-02 §8.20): **Light** resolves the hairline against `c.textPrimary` (white measured 1.11:1 on the near-white Light capsule), **Dark** keeps white; the alpha is always scaled by `capsuleProgress`. |
 * | capsule `box-shadow: 0 2px 10px rgba(99,102,241,0.2)` | `shadow(2.dp, …, spotColor = c.primary.copy(alpha = 0.2f))` — the same glow-spot substitution `MomentumFab` and `Modifier.liquidGlass` already document (`#6366F1` is not a Momentum token) |
 * | active `text-[22px]` FILL 1 / inactive `text-[23px]` FILL 0 | `22 dp` on the rounded (filled) icon / `23 dp` on the outlined one — the reference's own 1 dp asymmetry, reproduced deliberately |
 * | inactive `w-9 h-9 rounded-full` wrapper | `36 dp` + `CircleShape`; its `group-hover:bg-white/40` is **not** implemented (no hover on touch) |
 * | labels `font-label-sm`, bold when active | [MomentumLabelMicro], `Bold` / `SemiBold` |
 *
 * The reference's `transition-all` on the ink is the retained
 * `animateColorAsState` pair; its `transition-all` on the active capsule is the
 * one added `animateFloatAsState`, whose progress **scales the capsule's
 * alphas** — never a `lerp` toward `Color.Transparent`, which would darken
 * through premultiplication.
 *
 * The reference's selected-tab scale (1.06) and its whole-tab background plate
 * are **removed**: with the approved capsule a 1.06 scale enlarges the glyph
 * inside a capsule whose padding is reference-exact, and the plate is superseded
 * by the capsule. What stays is Momentum's own interaction affordance: the 18 dp
 * press/clip shape, `glassPress`, `Role.Tab` and the icon's
 * `contentDescription = label`.
 */
@Composable
fun RowScope.LiquidGlassTab(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val c = LocalMomentumColors.current
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(TabPressRadius)
    val colorSpec = tween<Color>(durationMillis = MomentumMotion.fast, easing = MomentumMotion.easeOut)
    val motionSpec = tween<Float>(durationMillis = MomentumMotion.fast, easing = MomentumMotion.easeOut)
    val iconTint by animateColorAsState(
        targetValue = if (selected) c.textPrimary else c.textSecondary,
        animationSpec = colorSpec,
        label = "tabIconTint",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) c.textPrimary else c.textSecondary,
        animationSpec = colorSpec,
        label = "tabLabelColor",
    )
    // The one added float: the reference's active-capsule transition. Its
    // resting value is 0f, so an unselected tab paints no capsule, no hairline
    // and no glow.
    val capsuleProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = motionSpec,
        label = "tabCapsule",
    )
    Column(
        modifier = Modifier
            .defaultMinSize(
                minWidth = if (selected) ActiveTabMinWidth else InactiveTabMinWidth,
                minHeight = MomentumHeights.controlMin,
            )
            .clip(shape)
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = MomentumSpacing.micro),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TabIconGap),
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .then(
                        if (capsuleProgress > 0f) {
                            Modifier.shadow(
                                elevation = CapsuleElevation,
                                shape = CircleShape,
                                spotColor = c.primary.copy(alpha = CapsuleSpotAlpha * capsuleProgress),
                            )
                        } else {
                            Modifier
                        },
                    )
                    .clip(CircleShape)
                    .background(capsuleBrush(c, capsuleProgress))
                    .border(
                        BorderStroke(
                            CapsuleBorder,
                            // ADR-49 Amendment Note 6: the reference's white hairline
                            // is invisible on the near-white Light capsule (measured
                            // 1.11:1), so Light resolves it against the ink token,
                            // mirroring the generic hairline above; Dark keeps white.
                            if (c.isDark) {
                                Color.White.copy(alpha = CapsuleBorderAlpha * capsuleProgress)
                            } else {
                                c.textPrimary.copy(alpha = CapsuleBorderAlpha * capsuleProgress)
                            },
                        ),
                        CircleShape,
                    )
                    .padding(
                        horizontal = MomentumSpacing.standard,
                        vertical = CapsulePadding,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = c.textPrimary,
                    modifier = Modifier.size(ActiveTabIconSize),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(InactiveIconSlotSize)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(InactiveTabIconSize),
                )
            }
        }
        Text(
            text = label,
            style = MomentumLabelMicro,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = labelColor,
        )
    }
}

/**
 * The active capsule's two stops, each an existing Aurora token lifted toward
 * the theme's surface and carrying the reference's own alpha. Because both
 * lerp toward `c.surface`, the capsule is pale over Light and deep over Dark —
 * one `c.textPrimary` ink is legible in both, which is the reference's
 * theme-agnostic composition rather than a Dark redesign.
 */
private fun capsuleBrush(c: MomentumColors, progress: Float): Brush {
    val stops = listOf(
        lerp(c.auroraBlue, c.surface, CapsuleStopOneMix).copy(alpha = CapsuleStopOneAlpha),
        lerp(c.auroraLavender, c.surface, CapsuleStopTwoMix).copy(alpha = CapsuleStopTwoAlpha),
    )
    return Brush.linearGradient(
        colors = stops.map { it.copy(alpha = it.alpha * progress) },
        start = Offset.Zero,
        end = Offset.Infinite,
    )
}

/** The reference's inner bar row height (`h-16`), as a floor, not a ceiling. */
private val NavBarHeight = 64.dp

/**
 * The nav-scoped `fillAlpha` opt-in (ADR-49 Amendment Note 6, human ruling
 * 2026-10-02 §8.20): a two-layer solid fill that raises the bar toward the
 * reference's own `rgba(255,255,255,0.8)` opacity. It lifts the Dark inactive
 * label/icon band (measured 3.34:1 at `heroFill`) above the 4.5:1 text floor
 * while preserving the selected/inactive ink hierarchy — the inactive ink stays
 * `c.textSecondary`, never flattened to `c.textPrimary`.
 */
private const val NavBarFillAlpha = 0.85f

/** Momentum's own press/clip shape for the whole tab — not a reference surface. */
private val TabPressRadius = 18.dp

private val ActiveTabMinWidth = 62.dp
private val InactiveTabMinWidth = 56.dp
private val ActiveTabIconSize = 22.dp
private val InactiveTabIconSize = 23.dp
private val InactiveIconSlotSize = 36.dp
private val TabIconGap = 2.dp

/** The reference's `py-1.5` on the active capsule. */
private val CapsulePadding = 6.dp

/** The reference's `0 2px 10px` capsule glow. */
private val CapsuleElevation = 2.dp
private const val CapsuleSpotAlpha = 0.2f
private val CapsuleBorder = 1.dp
private const val CapsuleBorderAlpha = 0.9f
private const val CapsuleStopOneMix = 0.55f
private const val CapsuleStopOneAlpha = 0.75f
private const val CapsuleStopTwoMix = 0.50f
private const val CapsuleStopTwoAlpha = 0.85f
