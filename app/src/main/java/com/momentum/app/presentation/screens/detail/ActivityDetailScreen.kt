package com.momentum.app.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.presentation.components.CompletionButton
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumDivider
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.StatCard
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
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
        MomentumDialog(
            title = "Delete permanently?",
            body = "Completion history and reminders are deleted too. This can't be undone.",
            confirmLabel = "Delete",
            onConfirm = { confirmDelete = false; vm.deleteHard() },
            dismissLabel = "Keep",
            onDismiss = { confirmDelete = false },
            destructive = true,
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
        contentPadding = PaddingValues(
            horizontal = MomentumSpacing.screen,
            vertical = MomentumSpacing.group,
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        item {
            MomentumTopBar(
                title = activity.title,
                subtitle = buildString {
                    append(recurrenceLabel(activity.recurrence))
                    state.category?.let { append(" · ${it.name}") }
                    append(" · ${activity.status.name.lowercase().replaceFirstChar(Char::titlecase)}")
                },
                onBack = onBack,
                actions = {
                    LiquidGlassIconButton(
                        icon = Icons.Filled.Edit,
                        contentDescription = "Edit ${activity.title}",
                        onClick = onEdit,
                    )
                },
            )
        }
        if (activity.description.isNotBlank()) {
            item {
                Text(activity.description, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
            }
        }
        item {
            MomentumCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(MomentumRadii.small))
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.White.copy(alpha = 0.14f),
                                    1f to c.primary.copy(alpha = 0.16f),
                                ),
                            )
                            .border(
                                BorderStroke(
                                    Dp.Hairline,
                                    c.primary.copy(alpha = 0.35f),
                                ),
                                RoundedCornerShape(MomentumRadii.small),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            momentumIcon(activity.iconKey), contentDescription = null,
                            tint = c.primary, modifier = Modifier.size(MomentumIconSizes.large),
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Today",
                            style = MaterialTheme.typography.labelMedium,
                            color = c.textSecondary,
                        )
                        Text(
                            if (state.completedToday) "Completed" else "Not yet done",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state.completedToday) c.success else c.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    CompletionButton(completed = state.completedToday, onToggle = onToggle)
                }
            }
        }
        item {
            MomentumSectionHeader(title = "Statistics")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group), modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        value = "${stats.currentStreak}",
                        label = "Current streak",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        value = "${stats.longestStreak}",
                        label = "Best streak",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group), modifier = Modifier.fillMaxWidth()) {
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
        }
        item {
            MomentumSectionHeader(title = "Details")
        }
        item {
            MomentumCard {
                DetailRow(
                    "Schedule",
                    buildString {
                        append(recurrenceLabel(activity.recurrence))
                        append(" · from ${activity.startDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"))}")
                        activity.endDate?.let { append(" to ${it.format(DateTimeFormatter.ofPattern("MMM d yyyy"))}") }
                    },
                )
                MomentumDivider()
                if (activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank()) {
                    DetailRow("Target", "${trimTarget(activity.targetPerOccurrence)} ${activity.unit}".trim())
                    MomentumDivider()
                }
                if (state.reminders.isNotEmpty()) {
                    DetailRow("Reminders", state.reminders.filter { it.enabled }.joinToString { it.timeLocal })
                    MomentumDivider()
                }
                if (activity.notes.isNotBlank()) {
                    DetailRow("Notes", activity.notes)
                    MomentumDivider()
                }
                DetailRow("Trend", state.stats.trend.name.lowercase().replaceFirstChar(Char::titlecase).replace('_', ' '))
            }
        }
        item {
            MomentumSectionHeader(title = "Recent history")
        }
        item {
            val recent = state.stats.scheduled.takeLast(14).reversed()
            if (recent.isEmpty()) {
                Text("No scheduled occurrences yet.", color = c.textSecondary)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                    items(recent) { date ->
                        val done = date in state.stats.completed
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(MomentumRadii.small))
                                    .background(
                                        if (done) {
                                            c.success.copy(alpha = 0.9f)
                                        } else {
                                            c.surface.copy(alpha = MomentumGlass.controlFill)
                                        },
                                    )
                                    .border(
                                        BorderStroke(
                                            Dp.Hairline,
                                            if (done) {
                                                Color.White.copy(alpha = 0.3f)
                                            } else {
                                                c.textSecondary.copy(alpha = 0.22f)
                                            },
                                        ),
                                        RoundedCornerShape(MomentumRadii.small),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "${date.dayOfMonth}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (done) c.backgroundBottom else c.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Text(
                                date.dayOfWeek.name.take(1),
                                style = MaterialTheme.typography.bodySmall,
                                color = c.textSecondary,
                            )
                        }
                    }
                }
            }
        }
        item {
            MomentumSectionHeader(title = "Actions")
        }
        item {
            MomentumCard {
                if (activity.status == ActivityStatus.ACTIVE) {
                    DetailActionRow(
                        icon = Icons.Filled.Pause,
                        label = "Pause",
                        supporting = "Hide from Today, keep history",
                        onClick = onPause,
                    )
                    MomentumDivider()
                }
                if (activity.status == ActivityStatus.PAUSED) {
                    DetailActionRow(
                        icon = Icons.Filled.PlayArrow,
                        label = "Resume",
                        supporting = "Show on Today again",
                        onClick = onResume,
                    )
                    MomentumDivider()
                }
                if (activity.status != ActivityStatus.ARCHIVED) {
                    DetailActionRow(
                        icon = Icons.Filled.Archive,
                        label = "Archive",
                        supporting = "Retire without deleting history",
                        onClick = onArchive,
                    )
                    MomentumDivider()
                }
                DetailActionRow(
                    icon = Icons.Filled.Delete,
                    label = "Delete permanently",
                    supporting = "Erases history and reminders",
                    destructive = true,
                    onClick = onDelete,
                )
            }
        }
    }
}

@Composable
private fun DetailActionRow(
    icon: ImageVector,
    label: String,
    supporting: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    val c = MomentumThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MomentumRadii.control))
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick)
            .padding(
                horizontal = MomentumSpacing.row,
                vertical = MomentumSpacing.group,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (destructive) c.error else c.textSecondary,
            modifier = Modifier.size(MomentumIconSizes.medium),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = if (destructive) c.error else c.textPrimary,
            )
            Text(supporting, style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
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
