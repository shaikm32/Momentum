package com.momentum.app.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarRoute(
    onOpenActivity: (Long) -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: CalendarViewModel = momentumViewModel { CalendarViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(vm) {
        vm.messages.collect { snackbar.showSnackbar(it) }
    }
    CalendarScreen(
        state = state,
        onPrev = { vm.shiftMonth(-1) },
        onNext = { vm.shiftMonth(1) },
        onToday = { vm.goToday() },
        onSelect = { vm.select(it) },
        onOpen = onOpenActivity,
        onToggle = { id -> vm.toggle(id, state.selectedDate) },
    )
}

@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onSelect: (LocalDate) -> Unit,
    onOpen: (Long) -> Unit,
    onToggle: (Long) -> Unit,
) {
    val c = MomentumThemeColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    state.month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.headlineMedium,
                    color = c.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onPrev) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                }
                TextButton(onClick = onToday) { Text("Today") }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { d ->
                    Text(
                        d, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                        color = c.textSecondary, style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(320.dp),
                userScrollEnabled = false,
            ) {
                items(state.cells, key = { it.date }) { cell ->
                    DayDot(cell = cell, isToday = cell.date == state.today, isSelected = cell.date == state.selectedDate, onSelect = { onSelect(cell.date) })
                }
            }
        }
        item {
            Text(
                state.selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())),
                style = MaterialTheme.typography.titleLarge,
                color = c.textPrimary,
            )
            val done = state.selectedItems.count { it.completed }
            Text(
                if (state.selectedItems.isEmpty()) "Nothing scheduled."
                else "$done of ${state.selectedItems.size} complete",
                style = MaterialTheme.typography.bodyMedium,
                color = c.textSecondary,
            )
        }
        items(state.selectedItems, key = { it.activity.id }) { item ->
            ActivityCard(
                activity = item.activity,
                completed = item.completed,
                streak = item.currentStreak,
                scheduleLabel = recurrenceLabel(item.activity.recurrence),
                onToggle = { onToggle(item.activity.id) },
                onOpen = { onOpen(item.activity.id) },
            )
        }
    }
}

@Composable
private fun DayDot(
    cell: DayCell,
    isToday: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val bg = when {
        cell.fraction < 0 -> Color.Transparent
        cell.fraction >= 1f -> c.success
        cell.fraction > 0f -> c.primary.copy(alpha = 0.35f + 0.45f * cell.fraction)
        else -> c.surfaceVariant
    }
    Column(
        modifier = Modifier
            .alpha(if (cell.inMonth) 1f else 0.35f)
            .clickable(onClick = onSelect)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(if (isSelected || isToday) RoundedCornerShape(12.dp) else CircleShape)
                .background(if (isSelected) c.primary.copy(alpha = 0.35f) else bg)
                .padding(2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "${cell.date.dayOfMonth}",
                color = if (cell.fraction >= 1f && !isSelected) c.backgroundBottom else c.textPrimary,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            )
        }
        if (isToday) {
            Text("•", color = c.accent, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
