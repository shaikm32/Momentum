package com.momentum.app.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.isDark

/** Shared FAB diameter: circular, clear of the 48 dp minimum touch target. */
internal val MomentumFabSize = 56.dp

/**
 * The one floating add action (ADR-47 §6, glass-over-`primary` per ADR-48
 * Amendment Note 4): a `primary` disc at the design system's **floating-layer**
 * glass alpha, with the `prominent` (28 dp) glyph, shared by Home
 * (`cd_quick_add`) and Habits (`cd_create_habit`). It replaces the two divergent
 * private FABs — the glass `QuickAddFab` and the gradient `AddHabitFab` — with a
 * single material and glyph size.
 *
 * **Glass-over-`primary`, not a solid disc.** Under Momentum's
 * `AURORA = atmosphere · LIQUID GLASS = interface` contract the foreground is
 * glass over the atmosphere, and the atmosphere must stay perceptible *through
 * and around* it. A 0.95-alpha saturated disc was the one foreground object on
 * Home with a hard, fully-opaque boundary, which is what left the FAB reading as
 * disconnected; at [MomentumGlass.heroFill] the disc keeps the theme's `primary`
 * identity — so it is still unmistakably the primary action and the only
 * `primary` object on the screen — while joining one material family with the
 * glass bottom bar below it and the glass Settings control above it, so Home's
 * three floating objects form **one depth stack**.
 *
 * `MomentumGlass.heroFill` is an existing token: no new token, no new value, no
 * new component and no new API. Because `MomentumFab.kt` is shared, **Habits' FAB
 * changes too** — same component, size, glyph, placement and behaviour,
 * translucent instead of solid. That shared consequence was explicitly approved
 * (human ruling §4.10.8); nothing else on Habits changes.
 *
 * It owns its glyph and material so call sites pass only placement (via
 * [modifier]), the label, the action, and the two opt-in behaviours below. Press
 * feedback is [glassPress] (disabled under reduced motion by the modifier
 * itself).
 *
 * **[glowPulse] and [rotateGlyphOnPress] are the two additive, default-`false`
 * parameters** (ADR-48 Amendment Note 6). Both are opt-ins into the approved
 * reference's own motion, both are gated on motion being allowed, and both are
 * passed by **Home only** — so Habits' FAB is byte-identical to its pre-TASK-018
 * rendering, and the default argument is what every other call site gets:
 *
 * - `glowPulse` breathes the component's **existing single** shadow over
 *   [MomentumMotion.fabGlow]: its elevation rises
 *   `MomentumGlass.heroElevation → +4.dp` and its `primary` spot alpha
 *   `0.40 → 0.55`. Those are the reference's own numbers (`10px → 14px`,
 *   `.32 → .55`). The shadow is drawn by an explicit `graphicsLayer` block —
 *   the very layer `Modifier.shadow` creates — because the ambient phase must be
 *   read in the **layer/draw phase** and not in this composable's body, which
 *   would recompose the FAB on every frame. Material, diameter, glyph, hairline,
 *   press, role, label and placement are unchanged; the reference's three-part
 *   shadow list collapses into Compose's one elevation shadow, which is already
 *   Momentum's documented substitution.
 * - `rotateGlyphOnPress` turns the **add** glyph 90° while pressed, over the
 *   existing [MomentumMotion.fast] and [MomentumMotion.easeOut] — the press
 *   analogue of the reference's `group-hover:rotate-90`, since there is no hover
 *   on touch. It adds no easing token and no new easing.
 *
 * Under reduced motion **neither transition is composed**: the glow's phase is
 * the resting `0f` frame (the low shadow) and the rotation resolves instantly,
 * so both are absent rather than shortened.
 */
@Composable
fun MomentumFab(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    glowPulse: Boolean = false,
    rotateGlyphOnPress: Boolean = false,
) {
    val c = MomentumThemeColors.current
    val reduceMotion = rememberReduceMotion(null)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = CircleShape
    val shadowAmbient = Color.Black.copy(alpha = 0.30f)
    // The ambient glow is composed only when it is both opted into and allowed,
    // so the resting value is each keyframe's own 0%/100% frame: nothing jumps.
    val glowPhase: State<Float> = if (glowPulse && !reduceMotion) {
        rememberInfiniteTransition(label = "fabGlow").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = MomentumMotion.fabGlow, easing = LinearEasing),
            ),
            label = "fabGlowPhase",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }
    val glyphRotation by animateFloatAsState(
        targetValue = if (rotateGlyphOnPress && pressed && !reduceMotion) GlyphPressRotation else 0f,
        animationSpec = tween(durationMillis = MomentumMotion.fast, easing = MomentumMotion.easeOut),
        label = "fabGlyphRotation",
    )
    Box(
        modifier = modifier
            .size(MomentumFabSize)
            // ADR-48 Amendment Note 6 §3/§4 (M3): the component's **existing
            // single** shadow, breathing in the **layer phase**. `Modifier.shadow`
            // is itself a `BlockGraphicsLayerModifier` whose block sets exactly
            // these four properties, so this is the same layer, the same shape
            // and the same colours — written out so the ambient phase is read
            // *inside* the layer block, exactly as `HomeCards.kt` reads
            // `ambientPulse` and `boltPulse`. Reading it in the composable body
            // instead would recompose the FAB on every frame, which is the thing
            // §8.14's zero-recomposition guarantee and §8.19.4's M3 row forbid.
            .graphicsLayer {
                val phase = glowPhase.value
                this.shadowElevation = glowShadowElevation(phase).toPx()
                this.shape = CircleShape
                // `Modifier.shadow`'s own default: clip whenever the elevation is
                // positive, which this token always is.
                this.clip = true
                this.ambientShadowColor = shadowAmbient
                this.spotShadowColor = c.primary.copy(alpha = glowSpotAlpha(phase))
            }
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.22f),
                    // ADR-48 Amendment Note 4: the floating-layer glass alpha,
                    // so the Aurora reads through the disc and the FAB shares one
                    // material family with the bottom bar and the Settings
                    // control. Both stops use the same token — the disc is one
                    // even wash under its top-edge streak, not two alphas.
                    0.45f to c.primary.copy(alpha = MomentumGlass.heroFill),
                    1f to c.primary.copy(alpha = MomentumGlass.heroFill),
                ),
                shape = shape,
            )
            // ADR-48 Amendment Note 3: the design system's own theme-resolved
            // edge, exactly as `EmptyState` uses it. A hard-coded white 0.28
            // hairline is invisible on a light page (the reason every other
            // light surface separates with an *ink* hairline) and is stronger
            // than any other dark surface in the theme. Nothing else about the
            // FAB changes: same gradient shape, same 56 dp circle, `prominent`
            // glyph, press feedback and `Role.Button`.
            .border(
                BorderStroke(
                    Dp.Hairline,
                    if (c.isDark) {
                        Color.White.copy(alpha = MomentumGlass.borderDark)
                    } else {
                        c.textPrimary.copy(alpha = MomentumGlass.borderLight)
                    },
                ),
                shape,
            )
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
            imageVector = MomentumIcons.add,
            contentDescription = contentDescription,
            tint = c.onPrimary,
            modifier = Modifier
                .size(MomentumIconSizes.prominent)
                .rotate(glyphRotation),
        )
    }
}

/** The glowing shadow's elevation at [phase] (0f = rest, 1f = peak). */
private fun glowShadowElevation(phase: Float): Dp =
    MomentumGlass.heroElevation + GlowElevationRise * phase

/** The glowing shadow's `primary` spot alpha at [phase]. */
private fun glowSpotAlpha(phase: Float): Float =
    GlowSpotRest + (GlowSpotPeak - GlowSpotRest) * phase

/** The reference's `10px → 14px` glow rise, on the existing elevation token. */
private val GlowElevationRise = 4.dp

/** The reference's own glow-spot alphas: `.32` is floored by the token's 0.40. */
private const val GlowSpotRest = 0.40f
private const val GlowSpotPeak = 0.55f

/** The reference's `group-hover:rotate-90`, adopted as the press analogue. */
private const val GlyphPressRotation = 90f
