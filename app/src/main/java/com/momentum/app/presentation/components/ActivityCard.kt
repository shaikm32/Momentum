package com.momentum.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumTheme
import java.time.LocalDate

/**
 * Rich activity row: icon tile, title, schedule line, streak, complete affordance.
 * Completed state has reduced visual weight + success wash — meaningfully
 * different from incomplete, never just a checkbox flip.
 */
@Composable
fun ActivityCard(
    activity: Activity,
    completed: Boolean,
    streak: Int,
    scheduleLabel: String,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    val cardAlpha by animateFloatAsState(
        targetValue = if (completed) 0.62f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardWeight",
    )
    val tileTint by animateColorAsState(
        targetValue = if (completed) c.success else c.primary,
        label = "tileTint",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (completed) {
                    Brush.linearGradient(
                        listOf(c.success.copy(alpha = 0.22f), c.surface.copy(alpha = 0.72f)),
                    )
                } else {
                    Brush.linearGradient(
                        listOf(c.surface.copy(alpha = 0.85f), c.surface.copy(alpha = 0.72f)),
                    )
                },
            )
            .clickable(onClick = onOpen)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(tileTint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = momentumIcon(activity.iconKey),
                contentDescription = null,
                tint = tileTint,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = c.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = scheduleLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = c.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (streak > 1) {
                StreakBadge(streak = streak)
            }
        }
        CompletionButton(completed = completed, onToggle = onToggle)
    }
}

/** Human schedule line for cards/details ("Daily", "Mon · Wed · Fri", "Every 3 days", …). */
fun recurrenceLabel(recurrence: Recurrence): String = when (recurrence) {
    is Recurrence.Once -> "One-time"
    is Recurrence.Daily -> "Daily"
    is Recurrence.Weekdays -> "Weekdays"
    is Recurrence.SelectedWeekdays -> recurrence.days.sorted()
        .joinToString(" · ") { it.name.lowercase().replaceFirstChar(Char::titlecase).take(3) }
    is Recurrence.EveryNDays -> if (recurrence.n == 1) "Daily" else "Every ${recurrence.n} days"
    is Recurrence.Weekly -> "Weekly"
    is Recurrence.EveryNWeeks -> if (recurrence.n == 1) "Weekly" else "Every ${recurrence.n} weeks"
    is Recurrence.Monthly -> "Monthly"
}

@Preview(name = "ActivityCard light")
@Composable
private fun ActivityCardLightPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        Box(Modifier.background(Color.Transparent).padding(16.dp)) {
            ActivityCard(
                activity = Activity(title = "Morning run", startDate = LocalDate.of(2026, 1, 5)),
                completed = false,
                streak = 6,
                scheduleLabel = "Daily · 6 day streak",
                onToggle = {},
                onOpen = {},
            )
        }
    }
}

@Preview(name = "ActivityCard dark done")
@Composable
private fun ActivityCardDarkPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.DARK) {
        Box(Modifier.background(Color.Transparent).padding(16.dp)) {
            ActivityCard(
                activity = Activity(title = "Read 20 pages", startDate = LocalDate.of(2026, 1, 5)),
                completed = true,
                streak = 12,
                scheduleLabel = "Daily · 12 day streak",
                onToggle = {},
                onOpen = {},
            )
        }
    }
}
