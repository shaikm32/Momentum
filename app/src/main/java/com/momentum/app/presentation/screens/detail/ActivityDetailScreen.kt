package com.momentum.app.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.presentation.components.CompletionButton
import com.momentum.app.presentation.components.StatCard
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun ActivityDetailRoute(
    activityId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: ActivityDetailViewModel = momentumViewModel { ActivityDetailViewModel(it, activityId) }
    val state by vm.state.collectAsStateWithLifecycle()
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is DetailEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
                is DetailEffect.Deleted -> onDeleted()
            }
        }
    }
    when (val s = state) {
        is DetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading…", color = MomentumThemeColors.current.textSecondary)
        }
        is DetailUiState.NotFound -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Activity not found.", color = MomentumThemeColors.current.textSecondary)
        }
        is DetailUiState.Success -> ActivityDetailScreen(
            state = s,
            onBack = onBack,
            onEdit = { onEdit(activityId) },
            onToggle = { vm.toggleToday() },
            onPause = { vm.setStatus(ActivityStatus.PAUSED) },
            onResume = { vm.setStatus(ActivityStatus.ACTIVE) },
            onArchive = { vm.setStatus(ActivityStatus.ARCHIVED) },
            onDelete = { confirmDelete = true },
        )
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete permanently?") },
            text = { Text("Completion history and reminders are deleted too. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; vm.deleteHard() }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Keep") } },
        )
    }
}

@Composable
fun ActivityDetailScreen(
    state: DetailUiState.Success,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val activity = state.activity
    val stats = state.stats.stats
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit ${activity.title}")
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape)
                        .background(c.primary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        momentumIcon(activity.iconKey), contentDescription = null,
                        tint = c.primary, modifier = Modifier.size(36.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(activity.title, style = MaterialTheme.typography.headlineMedium, color = c.textPrimary)
                    Text(
                        buildString {
                            append(recurrenceLabel(activity.recurrence))
                            state.category?.let { append(" · ${it.name}") }
                        },
                        style = MaterialTheme.typography.bodyMedium, color = c.textSecondary,
                    )
                    if (activity.description.isNotBlank()) {
                        Text(activity.description, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                    }
                }
                CompletionButton(completed = state.completedToday, onToggle = onToggle)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    value = "${state.stats.stats.currentStreak}",
                    label = "Current streak",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${stats.longestStreak}",
                    label = "Best streak",
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    value = "${(stats.completionRate * 100).roundToInt()}%",
                    label = "Last 30 days",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${stats.completedCount}",
                    label = "Completions",
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            DetailRow(
                "Schedule",
                buildString {
                    append(recurrenceLabel(activity.recurrence))
                    append(" · from ${activity.startDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"))}")
                    activity.endDate?.let { append(" to ${it.format(DateTimeFormatter.ofPattern("MMM d yyyy"))}") }
                },
            )
            if (activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank()) {
                DetailRow("Target", "${trimTarget(activity.targetPerOccurrence)} ${activity.unit}".trim())
            }
            if (state.reminders.isNotEmpty()) {
                DetailRow("Reminders", state.reminders.filter { it.enabled }.joinToString { it.timeLocal })
            }
            if (activity.notes.isNotBlank()) DetailRow("Notes", activity.notes)
            DetailRow("Trend", state.stats.trend.name.lowercase().replaceFirstChar(Char::titlecase).replace('_', ' '))
        }
        item {
            Text("Recent history", style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            Spacer(Modifier.height(8.dp))
            val recent = state.stats.scheduled.takeLast(14).reversed()
            if (recent.isEmpty()) {
                Text("No scheduled occurrences yet.", color = c.textSecondary)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recent) { date ->
                        val done = date in state.stats.completed
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(if (done) c.success else c.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "${date.dayOfMonth}",
                                    color = if (done) c.backgroundBottom else c.textSecondary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Text(
                                date.dayOfWeek.name.take(1),
                                style = MaterialTheme.typography.bodyMedium,
                                color = c.textSecondary,
                            )
                        }
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (activity.status == ActivityStatus.ACTIVE) {
                    TextButton(onClick = onPause) {
                        Icon(Icons.Filled.Pause, contentDescription = null)
                        Text("Pause")
                    }
                }
                if (activity.status == ActivityStatus.PAUSED) {
                    TextButton(onClick = onResume) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Text("Resume")
                    }
                }
                if (activity.status != ActivityStatus.ARCHIVED) {
                    TextButton(onClick = onArchive) { Text("Archive") }
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = c.error)
                    Text("Delete", color = c.error)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val c = MomentumThemeColors.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = c.textSecondary, modifier = Modifier.weight(1f))
        Text(value, color = c.textPrimary, fontWeight = FontWeight.Medium)
    }
}

private fun trimTarget(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
