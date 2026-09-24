package com.momentum.app.presentation.screens.today

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.aurora.AuroraBackground
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.components.targetProgressLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumShapes
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors

@Composable
fun TodayRoute(
    onOpenActivity: (Long) -> Unit,
    onAddActivity: () -> Unit,
    onOpenSettings: () -> Unit,
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
            onOpenSettings = onOpenSettings,
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
        Modifier.fillMaxSize().padding(MomentumSpacing.section),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = c.textSecondary, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(MomentumSpacing.compact))
        MomentumButton(text = "Try again", onClick = onRetry, secondary = true)
    }
}

/**
 * Home (Design System §13): one job — "How am I doing today?" and
 * "What do I still need to do today?" Aurora responds to today's state
 * (REST → PROGRESS → COMPLETION), never permanent wallpaper.
 */
@Composable
fun TodayScreen(
    state: TodayUiState.Success,
    reduceMotion: Boolean,
    onToggle: (Long) -> Unit,
    onOpen: (Long) -> Unit,
    onAdd: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val remaining = remember(state.items) { state.items.filter { !it.completed } }
    val finished = remember(state.items) { state.items.filter { it.completed } }
    val auroraState = when {
        state.items.isEmpty() -> AuroraState.REST
        state.progress >= 1f -> AuroraState.COMPLETION
        state.progress > 0f -> AuroraState.PROGRESS
        else -> AuroraState.REST
    }
    Box(Modifier.fillMaxSize()) {
        AuroraBackground(
            state = auroraState,
            animated = !reduceMotion,
            modifier = Modifier.fillMaxSize(),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = MomentumSpacing.screen,
                vertical = MomentumSpacing.card,
            ),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
        ) {
            item {
                HomeHeader(
                    greeting = state.greeting,
                    dateLabel = state.dateLabel,
                    onOpenSettings = onOpenSettings,
                )
            }
            if (state.items.isEmpty()) {
                // No progress to report — the empty state carries the screen.
                item {
                    HomeEmptyState(onAdd = onAdd)
                }
            } else {
                item {
                    TodayProgressHero(
                        progress = state.progress,
                        doneCount = state.doneCount,
                        total = state.items.size,
                        reduceMotion = reduceMotion,
                    )
                }
                item {
                    TodaySummary(message = state.message)
                }
                if (remaining.isNotEmpty()) {
                    item {
                        MomentumSectionHeader(title = "Up next · ${remaining.size}")
                    }
                }
                items(remaining, key = { it.activity.id }) { item ->
                    TodayActivityRow(
                        item = item,
                        completed = false,
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
                    TodayActivityRow(
                        item = item,
                        completed = true,
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
}

/** Lightweight Home app bar: greeting + date, Settings at the top-right (§12). */
@Composable
private fun HomeHeader(
    greeting: String,
    dateLabel: String,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MomentumSpacing.group),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.standard),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                color = c.textPrimary,
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodySmall,
                color = c.textSecondary,
            )
        }
        LiquidGlassIconButton(
            icon = MomentumIcons.settings,
            contentDescription = "Settings",
            onClick = onOpenSettings,
        )
    }
}

/**
 * Today's Progress Hero — the signature component (§14): large numeric
 * hierarchy, restrained Aurora response through the ring, calm completion
 * copy. Visually important, never overwhelming.
 */
@Composable
private fun TodayProgressHero(
    progress: Float,
    doneCount: Int,
    total: Int,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val complete = total > 0 && doneCount >= total
    val remaining = total - doneCount
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(MomentumShapes.hero), GlassLevel.HERO)
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.section),
    ) {
        ProgressOrb(
            progress = progress,
            size = 96.dp,
            glowEnabled = !reduceMotion,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
        ) {
            Text(
                text = "$doneCount / $total",
                style = MaterialTheme.typography.displayLarge,
                color = c.textPrimary,
                maxLines = 1,
            )
            Text(
                text = "completed today",
                style = MaterialTheme.typography.labelLarge,
                color = c.textSecondary,
            )
            Text(
                text = if (complete) "Today is complete" else "$remaining remaining",
                style = MaterialTheme.typography.labelLarge,
                color = if (complete) c.success else c.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** One-line daily status from the ViewModel — the calm bridge into the list. */
@Composable
private fun TodaySummary(message: String, modifier: Modifier = Modifier) {
    val c = MomentumThemeColors.current
    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = c.textSecondary,
        modifier = modifier.fillMaxWidth(),
    )
}

/** Today's list row — the shared Activity Row (§15), compact action surface. */
@Composable
private fun TodayActivityRow(
    item: com.momentum.app.domain.usecase.TodayItem,
    completed: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
) {
    ActivityCard(
        activity = item.activity,
        completed = completed,
        streak = item.currentStreak,
        scheduleLabel = todayScheduleLabel(item),
        onToggle = onToggle,
        onOpen = onOpen,
    )
}

/** Calm, useful, action-oriented empty state (§3, §25): no guilt, no streak-loss language. */
@Composable
private fun HomeEmptyState(onAdd: () -> Unit) {
    EmptyState(
        title = "Nothing scheduled today",
        body = "When you add a habit or task it shows up here. One small step is enough.",
        actionLabel = "Add an activity",
        onAction = onAdd,
        icon = Icons.Rounded.Star,
    )
}

private fun todayScheduleLabel(item: com.momentum.app.domain.usecase.TodayItem): String = buildString {
    append(recurrenceLabel(item.activity.recurrence))
    // P2#2: show stored progress ("5/8 glasses", "0/8 glasses").
    val shown = if (item.completed) item.completionValue else 0.0
    targetProgressLabel(item.activity, shown)?.let { append(" · $it") }
}

@Preview(name = "Home hero light", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun TodayProgressHeroLightPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        Box(Modifier.padding(MomentumSpacing.screen)) {
            TodayProgressHero(progress = 0.6f, doneCount = 3, total = 5, reduceMotion = true)
        }
    }
}

@Preview(name = "Home hero dark", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun TodayProgressHeroDarkPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        Box(Modifier.padding(MomentumSpacing.screen)) {
            TodayProgressHero(progress = 1f, doneCount = 5, total = 5, reduceMotion = true)
        }
    }
}
