package com.momentum.app.presentation.screens.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.StatCard
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun InsightsRoute(onAddActivity: () -> Unit) {
    val vm: InsightsViewModel = momentumViewModel { InsightsViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    InsightsScreen(state = state, onAdd = onAddActivity)
}

@Composable
fun InsightsScreen(state: InsightsUiState, onAdd: () -> Unit) {
    val c = MomentumThemeColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Insights", style = MaterialTheme.typography.displayLarge, color = c.textPrimary)
            Text(
                "Real numbers from your actual history.",
                style = MaterialTheme.typography.bodyMedium,
                color = c.textSecondary,
            )
        }
        if (state.isEmpty) {
            item {
                EmptyState(
                    title = "No data yet",
                    body = "Complete activities for a few days and your trends will appear here.",
                    actionLabel = "Create activity",
                    onAction = onAdd,
                )
            }
            return@LazyColumn
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    value = "${(state.weekRate * 100).roundToInt()}%",
                    label = "Last 7 days",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${(state.monthRate * 100).roundToInt()}%",
                    label = "Last 30 days",
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    value = "${state.totalCompletions}",
                    label = "Completions (30d)",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${state.bestStreak}",
                    label = "Best streak",
                    sublabel = state.bestStreakTitle.takeIf { it.isNotBlank() },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Text("This week", style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            WeeklyBars(days = state.last7Days)
        }
        if (state.categories.isNotEmpty()) {
            item {
                Text("By category", style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            }
            items(state.categories) { cat ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(c.surface.copy(alpha = 0.72f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row {
                        Text(cat.name, color = c.textPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text(
                            "${(cat.rate * 100).roundToInt()}%",
                            color = c.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { cat.rate.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)),
                        color = c.primary,
                        trackColor = c.surfaceVariant,
                        strokeCap = StrokeCap.Round,
                    )
                    Text(
                        "${cat.completed} of ${cat.scheduled} complete",
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.textSecondary,
                    )
                }
            }
        }
        if (state.topActivities.isNotEmpty()) {
            item {
                Text("Most consistent", style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            }
            items(state.topActivities, key = { it.activity.id }) { perf ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(c.surface.copy(alpha = 0.72f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(perf.activity.title, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${(perf.rate * 100).roundToInt()}% · ${perf.streak} in a row",
                            style = MaterialTheme.typography.bodyMedium,
                            color = c.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

/** Custom-drawn weekly bars — no chart library in v1 (dependency discipline). */
@Composable
private fun WeeklyBars(days: List<DayRate>) {
    val c = MomentumThemeColors.current
    val track = c.surfaceVariant
    val fill = c.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(c.surface.copy(alpha = 0.72f))
            .padding(16.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            if (days.isEmpty()) return@Canvas
            val gap = 8.dp.toPx()
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
