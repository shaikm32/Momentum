package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(state.greeting, style = MaterialTheme.typography.bodyLarge, color = c.textSecondary)
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 44.sp),
                    color = c.textPrimary,
                )
                Text(state.dateLabel, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(c.primary, c.secondary, c.accent)))
                    .padding(24.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = state.message,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = if (state.items.isEmpty()) {
                            "Create an activity to begin your streak."
                        } else {
                            "${state.doneCount} of ${state.items.size} complete"
                        },
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        if (state.items.isNotEmpty()) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ProgressOrb(progress = state.progress, glowEnabled = !reduceMotion)
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
                )
            }
        } else {
            items(state.items, key = { it.activity.id }) { item ->
                ActivityCard(
                    activity = item.activity,
                    completed = item.completed,
                    streak = item.currentStreak,
                    scheduleLabel = buildString {
                        append(recurrenceLabel(item.activity.recurrence))
                        if (item.currentStreak > 1) append(" · ${item.currentStreak} in a row")
                    },
                    onToggle = { onToggle(item.activity.id) },
                    onOpen = { onOpen(item.activity.id) },
                )
            }
            item {
                Button(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = c.surface.copy(alpha = 0.85f),
                        contentColor = c.primary,
                    ),
                ) { Text("+ Add activity") }
            }
        }
    }
}
