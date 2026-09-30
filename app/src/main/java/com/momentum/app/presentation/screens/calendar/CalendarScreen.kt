package com.momentum.app.presentation.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.components.targetProgressLabel
import com.momentum.app.presentation.resolve
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarRoute(
    onOpenActivity: (Long) -> Unit,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: CalendarViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(vm) {
        vm.messages.collect { snackbar.showSnackbar(it.resolve(context)) }
    }
    CalendarScreen(
        state = state,
        onBack = onBack,
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
    onBack: () -> Unit,
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
        contentPadding = PaddingValues(
            horizontal = MomentumSpacing.screen,
            vertical = MomentumSpacing.group,
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        item {
            MomentumTopBar(
                // Compact month+year: with the back affordance the full
                // "September 2026" no longer fits beside the month controls.
                title = state.month.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())),
                onBack = onBack,
                actions = {
                    LiquidGlassIconButton(
                        icon = Icons.Rounded.ChevronLeft,
                        contentDescription = stringResource(R.string.cd_previous_month),
                        onClick = onPrev,
                    )
                    LiquidGlassIconButton(
                        icon = Icons.Rounded.ChevronRight,
                        contentDescription = stringResource(R.string.cd_next_month),
                        onClick = onNext,
                    )
                    Box(
                        modifier = Modifier
                            .heightIn(min = MomentumHeights.controlMin)
                            .clip(RoundedCornerShape(MomentumRadii.control))
                            .clickable(role = Role.Button, onClick = onToday)
                            .padding(
                                horizontal = MomentumSpacing.row,
                                vertical = MomentumSpacing.group,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            stringResource(R.string.action_today),
                            style = MaterialTheme.typography.labelLarge,
                            color = c.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
            )
        }
        item {
            MomentumCard {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Same single-letter glyphs as before; order follows WeekStart.
                    state.weekDays.forEach { day ->
                        Text(
                            day.name.take(1),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = c.textSecondary,
                        )
                    }
                }
                Spacer(Modifier.height(MomentumSpacing.group))
                // Plain rows (not lazy): 42 fixed cells compose cheaply and size
                // responsively, with no nested-scroll measurement hazards.
                state.cells.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { cell ->
                            Box(modifier = Modifier.weight(1f)) {
                                DayDot(
                                    cell = cell,
                                    isToday = cell.date == state.today,
                                    isSelected = cell.date == state.selectedDate,
                                    onSelect = { onSelect(cell.date) },
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            MomentumSectionHeader(
                title = state.selectedDate.format(
                    DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()),
                ),
            )
            Text(
                if (state.selectedItems.isEmpty()) stringResource(R.string.calendar_nothing_scheduled)
                else {
                    val done = state.selectedItems.count { it.completed }
                    stringResource(R.string.calendar_day_complete, done, state.selectedItems.size)
                },
                style = MaterialTheme.typography.bodySmall,
                color = c.textSecondary,
            )
        }
        items(state.selectedItems, key = { it.activity.id }, contentType = { "activityRow" }) { item ->
            ActivityCard(
                activity = item.activity,
                completed = item.completed,
                streak = item.currentStreak,
                scheduleLabel = buildString {
                    append(recurrenceLabel(item.activity.recurrence))
                    // P2#2: show stored progress ("5/8 glasses", "0/8 glasses").
                    val shown = if (item.completed) item.completionValue else 0.0
                    targetProgressLabel(item.activity, shown)?.let { append(" · $it") }
                },
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
    val cellShape = RoundedCornerShape(MomentumRadii.small)
    // Intensity wash painted over the glass tile: selected and full days go
    // confident, partial days glow proportionally, quiet days stay glassy.
    val wash = when {
        isSelected -> c.primary.copy(alpha = 0.88f)
        cell.fraction >= 1f -> c.success.copy(alpha = 0.88f)
        cell.fraction > 0f -> c.primary.copy(alpha = 0.16f + 0.38f * cell.fraction)
        else -> Color.Transparent
    }
    val description = buildString {
        append(cell.date.format(DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())))
        if (cell.scheduled > 0) {
            append(stringResource(R.string.calendar_day_complete_suffix, cell.completed, cell.scheduled))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .alpha(if (cell.inMonth) 1f else 0.35f)
            .clip(cellShape)
            .liquidGlass(cellShape, GlassLevel.CONTROL)
            .background(wash, cellShape)
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(
                        BorderStroke(Dp.Hairline.times(2), c.primary),
                        cellShape,
                    )
                } else {
                    Modifier
                },
            )
            .semantics(mergeDescendants = true) {
                contentDescription = description
                role = Role.Button
                onClick { onSelect(); true }
            }
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "${cell.date.dayOfMonth}",
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> c.onPrimary
                cell.fraction >= 1f -> c.backgroundBottom
                else -> c.textPrimary
            },
            fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
