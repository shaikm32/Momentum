package com.momentum.app.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.ReduceMode
import com.momentum.app.domain.usecase.HabitDay
import com.momentum.app.domain.usecase.HabitDayState
import com.momentum.app.domain.usecase.HabitOverview
import com.momentum.app.domain.usecase.HabitWeekCompletion
import com.momentum.app.presentation.aurora.AuroraBackground
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.HabitIconTile
import com.momentum.app.presentation.components.LiquidGlassButton
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumDivider
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.formatAmount
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.screens.creation.matchTemplate
import com.momentum.app.presentation.screens.creation.templateById
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.habitIconAccent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val HabitDateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

/**
 * Habit Details: Soft Aurora environment, centered identity hero, key-config
 * meta cards, weekly completion + streak, read-only Details rows, and a
 * floating Pause/Resume · Edit · Delete group (Decisions 18–23, UI spec §5–12).
 * No top-right ⋮; no daily completion (Decision 23).
 */
@Composable
fun HabitDetailRoute(
    activityId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: HabitDetailViewModel = momentumViewModel { HabitDetailViewModel(it, activityId) }
    val state by vm.state.collectAsStateWithLifecycle()
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is HabitDetailEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
                is HabitDetailEffect.Deleted -> onDeleted()
            }
        }
    }

    when (val s = state) {
        HabitDetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading…", color = MomentumThemeColors.current.textSecondary)
        }
        HabitDetailUiState.NotFound -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Habit not found.", color = MomentumThemeColors.current.textSecondary)
        }
        is HabitDetailUiState.Success -> HabitDetailScreen(
            state = s,
            onBack = onBack,
            onEdit = { onEdit(activityId) },
            onPause = { vm.setStatus(ActivityStatus.PAUSED) },
            onResume = { vm.setStatus(ActivityStatus.ACTIVE) },
            onDelete = { confirmDelete = true },
        )
    }

    if (confirmDelete) {
        MomentumDialog(
            title = "Delete habit permanently?",
            body = "This removes the habit and its completion history. This can't be undone.",
            confirmLabel = "Delete",
            onConfirm = { confirmDelete = false; vm.deleteHard() },
            dismissLabel = "Cancel",
            onDismiss = { confirmDelete = false },
            destructive = true,
        )
    }
}

@Composable
fun HabitDetailScreen(
    state: HabitDetailUiState.Success,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val activity = state.activity
    val paused = activity.status == ActivityStatus.PAUSED
    val reduceMotion = rememberReduceMotion(null)

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground(
            state = AuroraState.REST,
            animated = !reduceMotion,
            modifier = Modifier.fillMaxSize(),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = MomentumSpacing.screen,
                end = MomentumSpacing.screen,
                top = MomentumSpacing.section + MomentumHeights.iconTarget,
                // Clearance so the floating action group never covers the last row.
                bottom = 168.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.compact),
        ) {
            item {
                Box(modifier = Modifier.alpha(if (paused) 0.85f else 1f)) {
                    IdentityHero(activity = activity, paused = paused, isCustom = isCustomHabit(activity))
                }
            }
            item {
                MetaCardsRow(
                    frequency = recurrenceLabel(activity.recurrence),
                    target = targetLabel(activity),
                    reminder = state.reminderTime ?: "—",
                    modifier = Modifier.alpha(if (paused) 0.85f else 1f),
                )
            }
            item { WeeklyCompletionCard(overview = state.overview) }
            item {
                StreakSummary(
                    current = state.overview.currentStreak,
                    longest = state.overview.longestStreak,
                )
            }
            item { MomentumSectionHeader(title = "Details") }
            item {
                MomentumCard {
                    InfoRow("Direction", directionLabel(activity))
                    MomentumDivider()
                    InfoRow("Tracking", trackingLabel(activity))
                    MomentumDivider()
                    InfoRow("Start date", activity.startDate.format(HabitDateFmt))
                    MomentumDivider()
                    InfoRow(
                        "End date",
                        activity.endDate?.format(HabitDateFmt) ?: "No end date",
                    )
                    if (activity.notes.isNotBlank()) {
                        MomentumDivider()
                        InfoRow("Notes", activity.notes)
                    }
                }
            }
        }

        // Overlay chrome: back (UI spec §5) + custom-icon edit affordance.
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = MomentumSpacing.tight, top = MomentumSpacing.tight)
                .size(MomentumHeights.iconTarget),
        ) {
            Icon(
                imageVector = MomentumIcons.arrowBack,
                contentDescription = "Back",
                tint = c.textPrimary,
            )
        }
        if (isCustomHabit(activity)) {
            LiquidGlassIconButton(
                icon = MomentumIcons.edit,
                contentDescription = "Edit habit icon",
                onClick = onEdit,
                active = true,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = MomentumSpacing.tight, top = MomentumSpacing.tight),
            )
        }

        // Centered bottom management group (Decision 22 / UI spec §10).
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = MomentumSpacing.screen, vertical = MomentumSpacing.card)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                MomentumSpacing.group,
                Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiquidGlassButton(
                text = if (paused) "Resume" else "Pause",
                onClick = { if (paused) onResume() else onPause() },
                secondary = true,
                modifier = Modifier.weight(1f),
            )
            LiquidGlassButton(
                text = "Edit Habit",
                onClick = onEdit,
                modifier = Modifier.weight(1.2f),
            )
            LiquidGlassButton(
                text = "Delete",
                onClick = onDelete,
                destructive = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Custom-habit detection mirrors `HabitConfigScreen`'s icon-editor gate:
 * persisted template id, else title+icon match, then `isCustom`.
 */
private fun isCustomHabit(activity: Activity): Boolean =
    templateById(
        activity.habitIntent,
        activity.habitTemplateId
            ?: matchTemplate(activity.habitIntent, activity.title, activity.iconKey),
    )?.isCustom == true

private fun directionLabel(activity: Activity): String = when (activity.habitIntent) {
    HabitIntent.BUILD -> "Build"
    HabitIntent.REDUCE -> "Reduce"
}

private fun targetLabel(activity: Activity): String = when {
    activity.habitIntent == HabitIntent.REDUCE &&
        activity.reduceMode() == ReduceMode.COMPLETE -> "Zero is the goal"
    activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank() ->
        "${formatAmount(activity.targetPerOccurrence)} ${activity.unit}".trim()
    else -> "Just mark it done"
}

private fun trackingLabel(activity: Activity): String = when {
    activity.habitIntent == HabitIntent.REDUCE &&
        activity.reduceMode() == ReduceMode.COMPLETE -> "Completely"
    activity.habitIntent == HabitIntent.REDUCE -> "Limit"
    activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank() -> "Target amount"
    else -> "Just mark it done"
}

/** Centered Soft Aurora identity: hero tile, name, Active/Paused status. */
@Composable
private fun IdentityHero(activity: Activity, paused: Boolean, isCustom: Boolean) {
    val c = MomentumThemeColors.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            HabitIconTile(
                iconKey = activity.iconKey,
                size = 72.dp,
                iconSize = MomentumIconSizes.prominent,
                deEmphasized = paused,
            )
            if (isCustom) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(c.surface.copy(alpha = 0.92f))
                        .border(
                            BorderStroke(1.dp, habitIconAccent(activity.iconKey).copy(alpha = 0.55f)),
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = MomentumIcons.edit,
                        contentDescription = "Icon is editable",
                        tint = habitIconAccent(activity.iconKey),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        Text(
            text = activity.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = c.textPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        StatusPill(paused = paused)
    }
}

/** Text + subtle status dot (never icon-only). */
@Composable
private fun StatusPill(paused: Boolean) {
    val c = MomentumThemeColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (paused) c.warning else c.success),
        )
        Text(
            text = if (paused) "Paused" else "Active",
            style = MaterialTheme.typography.labelLarge,
            color = if (paused) c.warning else c.success,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * Three equal-height key-config cards (Frequency · Target · Reminder);
 * values use `titleSmall` SemiBold with up to three lines.
 */
@Composable
private fun MetaCardsRow(
    frequency: String,
    target: String,
    reminder: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        MetaCard(
            label = "Frequency",
            value = frequency,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        MetaCard(
            label = "Target",
            value = target,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        MetaCard(
            label = "Reminder",
            value = reminder,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun MetaCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    Column(
        modifier = modifier
            .liquidGlass(RoundedCornerShapeCard(), GlassLevel.CARD)
            .padding(MomentumSpacing.compact),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = c.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = c.textPrimary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RoundedCornerShapeCard() =
    androidx.compose.foundation.shape.RoundedCornerShape(MomentumRadii.card)

/** Seven-day completion strip + `n/m completed` fraction (Decision 19). */
@Composable
private fun WeeklyCompletionCard(overview: HabitOverview) {
    val c = MomentumThemeColors.current
    MomentumCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "This Week",
                style = MaterialTheme.typography.titleMedium,
                color = c.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "${overview.week.completedCount}/${overview.week.scheduledCount} completed",
                style = MaterialTheme.typography.labelLarge,
                color = c.textSecondary,
            )
        }
        Spacer(Modifier.height(MomentumSpacing.group))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            overview.week.days.forEach { day ->
                DayCell(day = day)
            }
        }
    }
}

@Composable
private fun DayCell(day: HabitDay) {
    val c = MomentumThemeColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(36.dp),
    ) {
        when (day.state) {
            HabitDayState.COMPLETED -> Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(c.primary, c.secondary),
                        ),
                        shape = CircleShape,
                    )
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = c.onPrimary,
                    modifier = Modifier.size(MomentumIconSizes.medium),
                )
            }
            HabitDayState.INCOMPLETE -> Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, c.textSecondary.copy(alpha = 0.4f)), CircleShape),
            )
            HabitDayState.NOT_SCHEDULED -> Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(c.textMuted.copy(alpha = 0.45f)),
                )
            }
        }
        Text(
            day.date.dayOfWeek.name.take(1),
            style = MaterialTheme.typography.labelSmall,
            color = if (day.state == HabitDayState.NOT_SCHEDULED) {
                c.textMuted
            } else {
                c.textSecondary
            },
        )
    }
}

/** Compact streak card: amber streak tile + current/longest counts (Decision 20). */
@Composable
private fun StreakSummary(current: Int, longest: Int) {
    val c = MomentumThemeColors.current
    MomentumCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HabitIconTile(iconKey = "streak")
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "$current day${if (current == 1) "" else "s"} streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = c.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Longest: $longest day${if (longest == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textSecondary,
                )
            }
        }
    }
}

/** Stacked read-only label/value row for the Details card. */
@Composable
private fun InfoRow(label: String, value: String) {
    val c = MomentumThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = c.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = c.textPrimary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun fakeWeek(): HabitWeekCompletion {
    val mon = LocalDate.of(2026, 9, 21)
    return HabitWeekCompletion(
        days = (0L..6L).map { offset ->
            val date = mon.plusDays(offset)
            HabitDay(
                date = date,
                state = when (offset) {
                    0L, 1L, 2L -> HabitDayState.COMPLETED
                    3L, 4L -> HabitDayState.INCOMPLETE
                    else -> HabitDayState.NOT_SCHEDULED
                },
            )
        },
        completedCount = 3,
        scheduledCount = 5,
    )
}

@Preview(name = "Habit detail light", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun HabitDetailPreviewLight() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        HabitDetailScreen(
            state = HabitDetailUiState.Success(
                activity = Activity(
                    id = 1,
                    title = "Read books",
                    iconKey = "learning",
                    habitTemplateId = "build_read",
                    startDate = LocalDate.of(2026, 1, 5),
                    recurrence = Recurrence.Daily,
                    targetPerOccurrence = 30.0,
                    unit = "pages",
                    notes = "Evening wind-down.",
                ),
                overview = HabitOverview(week = fakeWeek(), currentStreak = 12, longestStreak = 28),
                reminders = emptyList(),
                reminderTime = "21:30",
            ),
            onBack = {},
            onEdit = {},
            onPause = {},
            onResume = {},
            onDelete = {},
        )
    }
}

@Preview(name = "Habit detail dark", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun HabitDetailPreviewDark() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.DARK) {
        HabitDetailScreen(
            state = HabitDetailUiState.Success(
                activity = Activity(
                    id = 2,
                    title = "Social media",
                    iconKey = "people",
                    habitIntent = HabitIntent.REDUCE,
                    habitTemplateId = "reduce_social",
                    startDate = LocalDate.of(2026, 3, 1),
                    recurrence = Recurrence.Daily,
                    targetPerOccurrence = 60.0,
                    unit = "min",
                    status = ActivityStatus.PAUSED,
                ),
                overview = HabitOverview(week = fakeWeek(), currentStreak = 4, longestStreak = 21),
                reminders = emptyList(),
                reminderTime = null,
            ),
            onBack = {},
            onEdit = {},
            onPause = {},
            onResume = {},
            onDelete = {},
        )
    }
}
