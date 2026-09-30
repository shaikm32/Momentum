package com.momentum.app.presentation.screens.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumDivider
import com.momentum.app.presentation.components.MomentumLinearProgress
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.StatCard
import com.momentum.app.presentation.components.momentumIcon
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun InsightsRoute(onAddActivity: () -> Unit, onOpenCalendar: () -> Unit) {
    val vm: InsightsViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    InsightsScreen(state = state, onAdd = onAddActivity, onOpenCalendar = onOpenCalendar)
}

@Composable
fun InsightsScreen(state: InsightsUiState, onAdd: () -> Unit, onOpenCalendar: () -> Unit) {
    val c = MomentumThemeColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = MomentumSpacing.screen,
            vertical = MomentumSpacing.group,
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        item {
            MomentumTopBar(
                title = stringResource(R.string.insights_title),
                subtitle = stringResource(R.string.insights_subtitle),
                actions = {
                    // Calendar/history lives inside Insights (§26) — never a tab.
                    LiquidGlassIconButton(
                        icon = Icons.Rounded.CalendarMonth,
                        contentDescription = stringResource(R.string.cd_open_calendar_history),
                        onClick = onOpenCalendar,
                    )
                },
            )
        }
        if (state.isEmpty) {
            item {
                EmptyState(
                    title = stringResource(R.string.insights_empty_title),
                    body = stringResource(R.string.insights_empty_body),
                    actionLabel = stringResource(R.string.action_create_activity),
                    onAction = onAdd,
                    icon = Icons.Rounded.BarChart,
                )
            }
            return@LazyColumn
        }
        item {
            MomentumCard(level = GlassLevel.HERO) {
                val delta = ((state.weekRate - state.monthRate) * 100).roundToInt()
                Text(
                    stringResource(R.string.insights_last_7_days),
                    style = MaterialTheme.typography.labelMedium,
                    color = c.textSecondary,
                )
                Text(
                    "${(state.weekRate * 100).roundToInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = c.textPrimary,
                )
                Text(
                    text = when {
                        delta > 0 -> stringResource(R.string.insights_delta_up, delta)
                        delta < 0 -> stringResource(R.string.insights_delta_down, delta)
                        else -> stringResource(R.string.insights_delta_even)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (delta >= 0) c.success else c.warning,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    value = "${state.bestStreak}",
                    label = stringResource(R.string.stat_best_streak),
                    sublabel = state.bestStreakTitle.takeIf { it.isNotBlank() },
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${state.totalCompletions}",
                    label = stringResource(R.string.insights_completions_30d),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.insights_this_week))
        }
        item {
            MomentumCard {
                WeeklyBars(days = state.last7Days)
            }
        }
        if (state.categories.isNotEmpty()) {
            item {
                MomentumSectionHeader(title = stringResource(R.string.insights_by_category))
            }
            item {
                MomentumCard {
                    state.categories.forEachIndexed { index, cat ->
                        if (index > 0) MomentumDivider(modifier = Modifier.padding(horizontal = 0.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                cat.name ?: stringResource(R.string.insights_uncategorized),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = c.textPrimary,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "${(cat.rate * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = c.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            MomentumLinearProgress(progress = cat.rate.toFloat())
                            Text(
                                stringResource(R.string.insights_category_complete, cat.completed, cat.scheduled),
                                style = MaterialTheme.typography.bodySmall,
                                color = c.textSecondary,
                            )
                        }
                    }
                }
            }
        }
        if (state.topActivities.isNotEmpty()) {
            item {
                MomentumSectionHeader(title = stringResource(R.string.insights_most_consistent))
            }
            item {
                MomentumCard {
                    state.topActivities.forEachIndexed { index, perf ->
                        if (index > 0) MomentumDivider(modifier = Modifier.padding(horizontal = 0.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(MomentumRadii.small))
                                    .background(
                                        Brush.verticalGradient(
                                            0f to Color.White.copy(alpha = 0.12f),
                                            1f to c.primary.copy(alpha = 0.16f),
                                        ),
                                    )
                                    .border(
                                        BorderStroke(
                                            Dp.Hairline,
                                            c.primary.copy(alpha = 0.3f),
                                        ),
                                        RoundedCornerShape(MomentumRadii.small),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = momentumIcon(perf.activity.iconKey),
                                    contentDescription = null,
                                    tint = c.primary,
                                    modifier = Modifier.size(MomentumIconSizes.medium),
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    perf.activity.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = c.textPrimary,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                )
                                Text(
                                    stringResource(
                                        R.string.insights_rate_in_a_row,
                                        (perf.rate * 100).roundToInt(),
                                        perf.streak,
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = c.textSecondary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Slim weekly bars — clean, minimal, no chart library. */
@Composable
private fun WeeklyBars(days: List<DayRate>) {
    val c = MomentumThemeColors.current
    val track = c.surfaceVariant.copy(alpha = 0.55f)
    val fill = c.primary
    Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(96.dp)) {
            if (days.isEmpty()) return@Canvas
            val gap = 6.dp.toPx()
            val barWidth = (size.width - gap * (days.size - 1)) / days.size
            days.forEachIndexed { i, day ->
                val x = i * (barWidth + gap)
                drawRoundRect(
                    color = track,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(barWidth / 2, barWidth / 2),
                )
                val h = size.height * day.rate.toFloat()
                if (h > 1f) {
                    drawRoundRect(
                        color = fill,
                        topLeft = Offset(x, size.height - h),
                        size = Size(barWidth, h),
                        cornerRadius = CornerRadius(barWidth / 2, barWidth / 2),
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEach { day ->
                Text(
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium,
                    color = c.textSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
