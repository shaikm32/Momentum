package com.momentum.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumTheme

/**
 * Big satisfying completion toggle (≥48 dp). Pops with a spring + haptic on
 * completion; the data state flips immediately — the animation never blocks it.
 */
@Composable
fun CompletionButton(
    completed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = if (completed) "Mark as not done" else "Mark as done",
) {
    val c = LocalMomentumColors.current
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (completed) 1f else 0.92f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "completionPop",
    )
    val background = if (completed) {
        Brush.linearGradient(listOf(c.primary, c.secondary))
    } else {
        Brush.linearGradient(listOf(c.surfaceVariant, c.surfaceVariant))
    }
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(background)
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
            imageVector = Icons.Filled.Check,
            contentDescription = contentDescription,
            tint = if (completed) c.onPrimary else c.textSecondary,
            modifier = Modifier.size(28.dp),
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
