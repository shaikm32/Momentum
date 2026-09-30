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
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Unarchive
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
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
import com.momentum.app.presentation.components.completionActionLabel
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.ViewModelArgs
import com.momentum.app.presentation.hiltViewModelWithArgs
import com.momentum.app.presentation.resolve
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
    val vm: ActivityDetailViewModel = hiltViewModelWithArgs {
        putString(ViewModelArgs.ACTIVITY_ID, activityId.toString())
    }
    val state by vm.state.collectAsStateWithLifecycle()
    var confirmDelete by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is DetailEffect.ShowMessage -> snackbar.showSnackbar(effect.message.resolve(context))
                is DetailEffect.Deleted -> onDeleted()
            }
        }
    }
    when (val s = state) {
        is DetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.loading), color = MomentumThemeColors.current.textSecondary)
        }
        is DetailUiState.NotFound -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.detail_activity_not_found), color = MomentumThemeColors.current.textSecondary)
        }
        is DetailUiState.Success -> ActivityDetailScreen(
            state = s,
            onBack = onBack,
            onEdit = { onEdit(activityId) },
            onToggle = { vm.toggleToday() },
            onPause = { vm.setStatus(ActivityStatus.PAUSED) },
            onResume = { vm.setStatus(ActivityStatus.ACTIVE) },
            onArchive = { vm.setStatus(ActivityStatus.ARCHIVED) },
            onRestore = { vm.setStatus(ActivityStatus.ACTIVE) },
            onDelete = { confirmDelete = true },
        )
    }
    if (confirmDelete) {
        MomentumDialog(
            title = stringResource(R.string.dialog_delete_permanently_title),
            body = stringResource(R.string.dialog_delete_activity_body),
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { confirmDelete = false; vm.deleteHard() },
            dismissLabel = stringResource(R.string.action_keep),
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
    onRestore: () -> Unit,
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
                        icon = Icons.Rounded.Edit,
                        contentDescription = stringResource(R.string.cd_edit_activity, activity.title),
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
                            stringResource(R.string.detail_today),
                            style = MaterialTheme.typography.labelMedium,
                            color = c.textSecondary,
                        )
                        Text(
                            if (state.completedToday) {
                                stringResource(R.string.detail_status_completed)
                            } else {
                                stringResource(R.string.detail_status_not_yet_done)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state.completedToday) c.success else c.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    CompletionButton(
                        completed = state.completedToday,
                        onToggle = onToggle,
                        contentDescription = completionActionLabel(activity, state.completedToday),
                    )
                }
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_statistics))
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group), modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        value = "${stats.currentStreak}",
                        label = stringResource(R.string.stat_current_streak),
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        value = "${stats.longestStreak}",
                        label = stringResource(R.string.stat_best_streak),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group), modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        value = "${(stats.completionRate * 100).roundToInt()}%",
                        label = stringResource(R.string.stat_last_30_days),
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        value = "${stats.completedCount}",
                        label = stringResource(R.string.stat_completions),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_details))
        }
        item {
            MomentumCard {
                DetailRow(
                    stringResource(R.string.detail_row_schedule),
                    buildString {
                        append(recurrenceLabel(activity.recurrence))
                        val fmt = DateTimeFormatter.ofPattern("MMM d yyyy")
                        append(stringResource(R.string.detail_schedule_from, activity.startDate.format(fmt)))
                        activity.endDate?.let {
                            append(stringResource(R.string.detail_schedule_to, it.format(fmt)))
                        }
                    },
                )
                MomentumDivider()
                if (activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank()) {
                    DetailRow(stringResource(R.string.detail_row_target), "${trimTarget(activity.targetPerOccurrence)} ${activity.unit}".trim())
                    MomentumDivider()
                }
                if (state.reminders.isNotEmpty()) {
                    DetailRow(
                        stringResource(R.string.detail_row_reminders),
                        state.reminders.filter { it.enabled }.joinToString { it.timeLocal },
                    )
                    MomentumDivider()
                }
                if (activity.notes.isNotBlank()) {
                    DetailRow(stringResource(R.string.detail_row_notes), activity.notes)
                    MomentumDivider()
                }
                DetailRow(
                    stringResource(R.string.detail_row_trend),
                    state.stats.trend.name.lowercase().replaceFirstChar(Char::titlecase).replace('_', ' '),
                )
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_recent_history))
        }
        item {
            val recent = state.stats.scheduled.takeLast(14).reversed()
            if (recent.isEmpty()) {
                Text(stringResource(R.string.detail_no_occurrences), color = c.textSecondary)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                    items(recent, key = { it }, contentType = { "historyDay" }) { date ->
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
            MomentumSectionHeader(title = stringResource(R.string.section_actions))
        }
        item {
            MomentumCard {
                if (activity.status == ActivityStatus.ACTIVE) {
                    DetailActionRow(
                        icon = Icons.Rounded.Pause,
                        label = stringResource(R.string.action_pause),
                        supporting = stringResource(R.string.action_pause_supporting),
                        onClick = onPause,
                    )
                    MomentumDivider()
                }
                if (activity.status == ActivityStatus.PAUSED) {
                    DetailActionRow(
                        icon = Icons.Rounded.PlayArrow,
                        label = stringResource(R.string.action_resume),
                        supporting = stringResource(R.string.action_resume_supporting),
                        onClick = onResume,
                    )
                    MomentumDivider()
                }
                if (activity.status == ActivityStatus.ARCHIVED) {
                    DetailActionRow(
                        icon = Icons.Rounded.Unarchive,
                        label = stringResource(R.string.action_restore),
                        supporting = stringResource(R.string.action_restore_supporting),
                        onClick = onRestore,
                    )
                    MomentumDivider()
                }
                if (activity.status != ActivityStatus.ARCHIVED) {
                    DetailActionRow(
                        icon = Icons.Rounded.Archive,
                        label = stringResource(R.string.action_archive),
                        supporting = stringResource(R.string.action_archive_supporting),
                        onClick = onArchive,
                    )
                    MomentumDivider()
                }
                DetailActionRow(
                    icon = Icons.Rounded.Delete,
                    label = stringResource(R.string.action_delete_permanently),
                    supporting = stringResource(R.string.action_delete_permanently_supporting),
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
            // One announcement for the action row: label + supporting copy are
            // merged in, so TalkBack reads the action once instead of reading
            // the row and then each of its texts.
            .semantics(mergeDescendants = true) {}
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
