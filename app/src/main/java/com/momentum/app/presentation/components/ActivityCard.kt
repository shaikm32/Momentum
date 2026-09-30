package com.momentum.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import com.momentum.app.R
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import java.time.LocalDate

/**
 * Compact activity row: icon tile, title, metadata line, streak pill,
 * completion affordance. Completed rows quiet down (a lower glass fill and the
 * success-tinted tile) without collapsing and stay fully actionable —
 * completion can be reversed from this control.
 *
 * The row never dims its own text: fading the whole card dropped body-text
 * contrast below 4.5:1 in the light theme (DASHBOARD_SPECS §21, AGENTS §9), so
 * "quieter" is expressed through the surface instead of an alpha layer.
 *
 * [scheduleLabel] is the card's optional metadata line (Home passes the
 * configured time and/or target progress; other screens pass the schedule).
 * An empty label omits the row entirely instead of reserving dead space
 * (DECISIONS §29). Titles wrap to at most two lines (DECISIONS §28).
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
    val reduceMotion = rememberReduceMotion(null)
    val tileTint by animateColorAsState(
        targetValue = if (completed) c.success else c.primary,
        animationSpec = if (reduceMotion) tween(0) else spring(
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "tileTint",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                RoundedCornerShape(MomentumRadii.card),
                if (completed) GlassLevel.CONTROL else GlassLevel.CARD,
            )
            .clickable(onClick = onOpen)
            .padding(MomentumSpacing.row),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(MomentumRadii.small))
                .background(
                    if (completed) {
                        c.success.copy(alpha = 0.18f)
                    } else {
                        tileTint.copy(alpha = 0.16f)
                    },
                )
                .border(
                    BorderStroke(
                        Dp.Hairline,
                        if (completed) {
                            c.success.copy(alpha = 0.35f)
                        } else {
                            tileTint.copy(alpha = 0.3f)
                        },
                    ),
                    RoundedCornerShape(MomentumRadii.small),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = momentumIcon(activity.iconKey),
                contentDescription = null,
                tint = tileTint,
                modifier = Modifier.size(MomentumIconSizes.tile),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = c.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (scheduleLabel.isNotBlank()) {
                Text(
                    text = scheduleLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (streak > 1) {
                StreakBadge(streak = streak)
            }
        }
        CompletionButton(
            completed = completed,
            onToggle = onToggle,
            contentDescription = completionActionLabel(activity, completed),
        )
    }
}

/**
 * Human schedule line for cards/details ("Daily", "Mon · Wed · Fri", "Every 3 days", …).
 *
 * A `@Composable` because the wording lives in `strings.xml`: it is display
 * logic and must only be called from the presentation layer — a ViewModel
 * passes the [Recurrence] through in its state instead.
 */
@Composable
fun recurrenceLabel(recurrence: Recurrence): String = when (recurrence) {
    is Recurrence.Once -> stringResource(R.string.pattern_once)
    is Recurrence.Daily -> stringResource(R.string.recurrence_daily)
    is Recurrence.Weekdays -> stringResource(R.string.preset_weekdays)
    is Recurrence.SelectedWeekdays -> recurrence.days.sorted()
        .joinToString(" · ") { it.name.lowercase().replaceFirstChar(Char::titlecase).take(3) }
    is Recurrence.EveryNDays -> if (recurrence.n == 1) {
        stringResource(R.string.recurrence_daily)
    } else {
        stringResource(R.string.recurrence_every_n_days, recurrence.n)
    }
    is Recurrence.Weekly -> stringResource(R.string.pattern_weekly)
    is Recurrence.EveryNWeeks -> if (recurrence.n == 1) {
        stringResource(R.string.pattern_weekly)
    } else {
        stringResource(R.string.recurrence_every_n_weeks, recurrence.n)
    }
    is Recurrence.Monthly -> stringResource(R.string.pattern_monthly)
}

/** Compact amount without a trailing ".0" ("8" not "8.0", "2.5" stays). */
fun formatAmount(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

/**
 * P2#2: "5/8 glasses" suffix for activities with an explicit target, else null.
 * Data exposure only — plain activities (target 1 × no unit) show nothing extra.
 */
fun targetProgressLabel(activity: Activity, value: Double): String? {
    if (!CompletionEngine.isTargetBased(activity)) return null
    val unit = if (activity.unit.isBlank()) "" else " ${activity.unit}"
    return "${formatAmount(value)}/${formatAmount(activity.targetPerOccurrence)}$unit"
}

/**
 * Screen-reader action for the completion control (DASHBOARD_SPECS §21,
 * DECISIONS §34): the control must announce what a tap *does*, carrying the
 * activity's own title — "Complete Workout" / "Mark Workout incomplete".
 *
 * "Reach a Target" activities open the value dialog rather than flipping
 * directly, so their label says that instead of promising an instant toggle.
 * This is label copy only; the completion decision itself always lives in
 * [CompletionEngine].
 *
 * Pure and resource-free (unit-testable); [completionActionLabel] is the
 * `@Composable` that resolves it for the two call sites.
 */
@StringRes
fun completionActionLabelRes(activity: Activity, completed: Boolean): Int = when {
    CompletionEngine.isTargetBased(activity) && completed -> R.string.cd_edit_progress
    CompletionEngine.isTargetBased(activity) -> R.string.cd_enter_progress
    completed -> R.string.cd_mark_incomplete
    else -> R.string.cd_complete_activity
}

@Composable
fun completionActionLabel(activity: Activity, completed: Boolean): String =
    stringResource(completionActionLabelRes(activity, completed), activity.title)

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
