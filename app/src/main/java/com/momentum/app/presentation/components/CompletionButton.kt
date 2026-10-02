package com.momentum.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumTheme

/**
 * Which accent a [CompletionButton]'s **done** state wears (ADR-48 Amendment
 * Note 5). Shared by every call site so the two treatments cannot drift into
 * two implementations.
 *
 * - [THEME] is Momentum's existing `primary → secondary` gradient: what Tasks,
 *   Calendar, Activities and every other surface already ship.
 * - [SUCCESS] is the approved Home completion control: an **emerald** gradient
 *   (`success` lifted half-way to `auroraMint`) with a white hairline and a
 *   `success`-spotted glow — the reference's completed badge, expressed in
 *   theme tokens.
 *
 * The **idle** state is never re-tinted by this parameter; only the done fill,
 * its hairline and its shadow are accent-dependent, so a call site that passes
 * nothing is byte-identical to before.
 */
enum class CompletionAccent { THEME, SUCCESS }

/**
 * Restrained completion toggle (48 dp touch target). Idle: quiet hairline ring;
 * done: solid accent fill. Pops with a spring + haptic on completion; the data
 * state flips immediately — the animation never blocks it.
 *
 * [contentDescription] names the *action*, not the widget (DASHBOARD_SPECS
 * §21), so TalkBack hears "Complete Workout" / "Mark Workout incomplete".
 * With reduced motion the state still changes visibly (fill, border, icon)
 * but the pop is skipped.
 *
 * [accent] and [showIdleGlyph] are the two additive parameters
 * (ADR-48 Amendment Note 5); both default to today's behaviour, so all
 * pre-TASK-018 call sites are unchanged.
 *
 * [idleRingBorderAlpha] is the third additive parameter (ADR-49 Amendment
 * Note 6, human ruling 2026-10-02 §8.20): `null` keeps the shared `0.4` idle
 * ring alpha exactly, so every non-Home completion control is unchanged; Home
 * opts into `0.8` so its idle ring boundary clears the 3:1 non-text floor over
 * the more solid Home row fills. Only the idle border alpha changes — the
 * done fill, the hairline colour, the target, the role, the label, the haptic
 * and the pop are all untouched.
 *
 * [showIdleGlyph] suppresses the check glyph **only while idle** — never in the
 * done state. The approved reference's pending control is an **empty ring**
 * (`code.html`'s `border-2` circle) while its completed control carries the
 * **check** inside the filled emerald disc (`code.html:96`, and all three
 * completed cards in `screen.png`), so `false` must not produce a silent,
 * mark-less disc. `true` is therefore the default and draws the glyph in both
 * states, exactly as before.
 */
@Composable
fun CompletionButton(
    completed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = if (completed) {
        stringResource(R.string.cd_mark_as_not_done)
    } else {
        stringResource(R.string.cd_mark_as_done)
    },
    accent: CompletionAccent = CompletionAccent.THEME,
    showIdleGlyph: Boolean = true,
    idleRingBorderAlpha: Float? = null,
) {
    val c = LocalMomentumColors.current
    val haptics = LocalHapticFeedback.current
    val reduceMotion = rememberReduceMotion(null)
    val label = contentDescription
    // The glyph is the control's label carrier whenever it is drawn; when it is
    // not drawn the label moves onto the control itself. Exactly one of the two
    // carries it in every state — never both (a double announcement), never
    // neither (a silent control). `showIdleGlyph` suppresses the glyph while
    // **idle** only; the done state always shows the check (ADR-48 Amendment
    // Note 5, `task.md` §10.8 Ruling A).
    val glyphShown = showIdleGlyph || completed
    val scale by animateFloatAsState(
        targetValue = if (completed) 1f else 0.94f,
        animationSpec = if (reduceMotion) tween(0) else spring(
            stiffness = Spring.StiffnessMedium,
        ),
        label = "completionPop",
    )
    Box(
        modifier = modifier
            .size(MomentumHeights.controlMin)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = if (completed) {
                    when (accent) {
                        CompletionAccent.THEME -> Brush.linearGradient(
                            colors = listOf(c.primary, c.secondary),
                        )
                        CompletionAccent.SUCCESS -> Brush.linearGradient(
                            colors = listOf(
                                lerp(c.success, c.auroraMint, SuccessMintStart),
                                lerp(c.success, c.auroraMint, SuccessMintEnd),
                            ),
                        )
                    }
                } else {
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.10f),
                        1f to c.surface.copy(alpha = MomentumGlass.controlFill),
                    )
                },
                shape = CircleShape,
            )
            .border(
                BorderStroke(
                    Dp.Hairline,
                    if (completed) {
                        when (accent) {
                            CompletionAccent.THEME -> Color.White.copy(alpha = 0.35f)
                            CompletionAccent.SUCCESS -> Color.White.copy(alpha = MomentumGlass.borderDark)
                        }
                    } else {
                        c.textSecondary.copy(alpha = idleRingBorderAlpha ?: IdleRingBorderAlpha)
                    },
                ),
                CircleShape,
            )
            .then(
                if (completed) {
                    when (accent) {
                        CompletionAccent.THEME -> Modifier.shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.3f),
                            spotColor = c.primary.copy(alpha = 0.45f),
                        )
                        CompletionAccent.SUCCESS -> Modifier.shadow(
                            elevation = MomentumGlass.heroElevation,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.3f),
                            spotColor = c.success.copy(alpha = SuccessSpotAlpha),
                        )
                    }
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Checkbox,
                onClick = {
                    if (!completed) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggle()
                },
            )
            // Reversal is preserved on every surface: the control keeps its
            // checkbox role and its action label (ADR-47 §21). The label is
            // carried by whichever of the control and the icon currently exists —
            // the icon whenever the glyph is drawn, the control itself when the
            // glyph is suppressed — so the announcement is the same, exactly
            // once, and never silent.
            .then(
                if (glyphShown) {
                    Modifier
                } else {
                    Modifier.semantics { this.contentDescription = label }
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (glyphShown) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = contentDescription,
                tint = if (completed) c.onPrimary else c.textSecondary,
                modifier = Modifier.size(MomentumIconSizes.medium),
            )
        }
    }
}

/** How far the emerald accent's start stop is lifted toward Aurora mint. */
private const val SuccessMintStart = 0.30f

/** How far the emerald accent's end stop is lifted toward Aurora mint. */
private const val SuccessMintEnd = 0.34f

/** Peak alpha of the emerald control's glow spot — the FAB's own glow value. */
private const val SuccessSpotAlpha = 0.40f

/** The shared idle-ring border alpha; `null` [idleRingBorderAlpha] keeps it. */
private const val IdleRingBorderAlpha = 0.4f

@Preview(name = "Completion light")
@Composable
private fun CompletionButtonPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        Box(Modifier.background(Color.Transparent)) {
            CompletionButton(completed = true, onToggle = {})
        }
    }
}
