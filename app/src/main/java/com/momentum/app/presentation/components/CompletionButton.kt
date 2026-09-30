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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
 * Restrained completion toggle (48 dp touch target). Idle: quiet hairline ring;
 * done: solid accent fill. Pops with a spring + haptic on completion; the data
 * state flips immediately — the animation never blocks it.
 *
 * [contentDescription] names the *action*, not the widget (DASHBOARD_SPECS
 * §21), so TalkBack hears "Complete Workout" / "Mark Workout incomplete".
 * With reduced motion the state still changes visibly (fill, border, icon)
 * but the pop is skipped.
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
) {
    val c = LocalMomentumColors.current
    val haptics = LocalHapticFeedback.current
    val reduceMotion = rememberReduceMotion(null)
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
                    Brush.linearGradient(
                        colors = listOf(c.primary, c.secondary),
                    )
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
                        Color.White.copy(alpha = 0.35f)
                    } else {
                        c.textSecondary.copy(alpha = 0.4f)
                    },
                ),
                CircleShape,
            )
            .then(
                if (completed) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = c.primary.copy(alpha = 0.45f),
                    )
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
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = contentDescription,
            tint = if (completed) c.onPrimary else c.textSecondary,
            modifier = Modifier.size(MomentumIconSizes.medium),
        )
    }
}

@Preview(name = "Completion light")
@Composable
private fun CompletionButtonPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        Box(Modifier.background(Color.Transparent)) {
            CompletionButton(completed = true, onToggle = {})
        }
    }
}
