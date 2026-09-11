package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.components.targetProgressLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors

@Composable
fun TodayRoute(
    onOpenActivity: (Long) -> Unit,
    onAddActivity: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: TodayViewModel = momentumViewModel { TodayViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(Unit) { vm.refresh() }
    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is TodayEffect.Celebrate -> {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    snackbar.showSnackbar(effect.message)
                }
                is TodayEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    when (val s = state) {
        is TodayUiState.Loading -> TodayLoading()
        is TodayUiState.Error -> TodayError(s.message, onRetry = { vm.refresh() })
        is TodayUiState.Success -> TodayScreen(
            state = s,
            reduceMotion = rememberReduceMotion(s.reduceMotionOverride),
            onToggle = { vm.toggle(it) },
            onOpen = onOpenActivity,
            onAdd = onAddActivity,
        )
    }
}

@Composable
private fun TodayLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Loading today…", color = MomentumThemeColors.current.textSecondary)
    }
}

@Composable
private fun TodayError(message: String, onRetry: () -> Unit) {
    val c = MomentumThemeColors.current
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = c.textSecondary)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun TodayScreen(
    state: TodayUiState.Success,
    reduceMotion: Boolean,
    onToggle: (Long) -> Unit,
    onOpen: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val remaining = remember(state.items) { state.items.filter { !it.completed } }
    val finished = remember(state.items) { state.items.filter { it.completed } }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = MomentumSpacing.screen,
            vertical = MomentumSpacing.card,
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(state.greeting, style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.displaySmall,
                    color = c.textPrimary,
                )
                Text(state.dateLabel, style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
            }
        }
        item {
            MomentumCard(level = GlassLevel.HERO) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
                ) {
                    ProgressOrb(
                        progress = state.progress,
                        size = 84.dp,
                        glowEnabled = !reduceMotion,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            "Today's progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = c.textSecondary,
                        )
                        Text(
                            text = if (state.items.isEmpty()) {
                                "Nothing scheduled"
                            } else {
                                "${state.doneCount} of ${state.items.size} complete"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = c.textPrimary,
                        )
                        Text(
                            text = if (state.items.isEmpty()) {
                                "Create an activity to begin your streak."
                            } else {
                                state.message
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = c.textSecondary,
                        )
                    }
                }
            }
        }
        if (state.items.isEmpty()) {
            item {
                EmptyState(
                    title = "Your momentum starts here",
                    body = "Track anything — habits, tasks, medicines, reading. Small steps daily.",
                    actionLabel = "Create your first activity",
                    onAction = onAdd,
                    icon = Icons.Filled.Star,
                )
            }
        } else {
            if (remaining.isNotEmpty()) {
                item {
                    MomentumSectionHeader(title = "Up next · ${remaining.size}")
                }
            }
            items(remaining, key = { it.activity.id }) { item ->
                ActivityCard(
                    activity = item.activity,
                    completed = false,
                    streak = item.currentStreak,
                    scheduleLabel = todayScheduleLabel(item),
                    onToggle = { onToggle(item.activity.id) },
                    onOpen = { onOpen(item.activity.id) },
                )
            }
            if (finished.isNotEmpty()) {
                item {
                    MomentumSectionHeader(title = "Completed · ${finished.size}")
                }
            }
            items(finished, key = { it.activity.id }) { item ->
                ActivityCard(
                    activity = item.activity,
                    completed = true,
                    streak = item.currentStreak,
                    scheduleLabel = todayScheduleLabel(item),
                    onToggle = { onToggle(item.activity.id) },
                    onOpen = { onOpen(item.activity.id) },
                )
            }
            item {
                MomentumButton(
                    text = "+ New activity",
                    onClick = onAdd,
                    secondary = true,
                )
            }
        }
    }
}

private fun todayScheduleLabel(item: com.momentum.app.domain.usecase.TodayItem): String = buildString {
    append(recurrenceLabel(item.activity.recurrence))
    // P2#2: show stored progress ("5/8 glasses", "0/8 glasses").
    val shown = if (item.completed) item.completionValue else 0.0
    targetProgressLabel(item.activity, shown)?.let { append(" · $it") }
}
