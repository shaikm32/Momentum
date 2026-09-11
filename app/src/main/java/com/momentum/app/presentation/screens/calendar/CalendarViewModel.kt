package com.momentum.app.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.WeekStart
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DayCell(
    val date: LocalDate,
    val inMonth: Boolean,
    val scheduled: Int,
    val completed: Int,
) {
    val fraction: Float = if (scheduled == 0) -1f else completed.toFloat() / scheduled
}

data class CalendarUiState(
    val month: YearMonth = YearMonth.now(),
    val today: LocalDate = LocalDate.now(),
    val cells: List<DayCell> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedItems: List<TodayItem> = emptyList(),
    val weekStart: WeekStart = WeekStart.MONDAY,
    /** The 7 weekdays in display order, starting at [weekStart]. */
    val weekDays: List<DayOfWeek> = orderedWeekDays(WeekStart.MONDAY),
)

/**
 * P2#1: pure calendar-presentation helpers (no Android dependencies, unit-tested).
 * Only ordering changes with [WeekStart]; dates keep their meaning.
 */

/** Monday..Sunday or Sunday..Saturday, starting at [weekStart]. */
fun orderedWeekDays(weekStart: WeekStart): List<DayOfWeek> {
    val start = if (weekStart == WeekStart.SUNDAY) DayOfWeek.SUNDAY else DayOfWeek.MONDAY
    return (0..6).map { start.plus(it.toLong()) }
}

/** First visible grid date for [month]: backs up to the nearest [weekStart] day. */
fun gridStartForMonth(month: YearMonth, weekStart: WeekStart): LocalDate {
    val first = month.atDay(1)
    val back = if (weekStart == WeekStart.SUNDAY) {
        first.dayOfWeek.value % 7 // Mon->1 … Sat->6, Sun->0
    } else {
        first.dayOfWeek.value - 1 // Mon->0 … Sun->6
    }
    return first.minusDays(back.toLong())
}

class CalendarViewModel(private val container: AppContainer) : ViewModel() {

    private val monthOffset = MutableStateFlow(0)
    private val selected = MutableStateFlow(LocalDate.now())

    private val messagesChannel = Channel<String>(Channel.BUFFERED)
    val messages = messagesChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = combine(
        monthOffset,
        container.settingsRepository.observe(),
    ) { offset, settings -> offset to settings.weekStart }
        .flatMapLatest { (offset, weekStart) ->
            val month = YearMonth.now().plusMonths(offset.toLong())
            // Grid bounds derive from the persisted preference (single source
            // of truth: DataStore), so a WeekStart change re-queries immediately
            // and survives restarts. Dates keep their meaning; only order shifts.
            val gridStart = gridStartForMonth(month, weekStart)
            val gridEnd = gridStart.plusDays(41)
            combine(
                container.activityRepository.observeAll(),
                container.completionRepository.observeInRange(gridStart.toIso(), gridEnd.toIso()),
                selected,
            ) { activities, completions, sel ->
                buildState(month, activities, completions, sel, gridStart, weekStart)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun shiftMonth(delta: Long) {
        monthOffset.update { it + delta.toInt() }
    }

    fun goToday() {
        val now = YearMonth.now()
        monthOffset.value = 0
        selected.value = LocalDate.now()
    }

    fun select(date: LocalDate) {
        selected.value = date
    }

    /** Toggles the selected day's occurrence (never a future-or-past fake). */
    fun toggle(activityId: Long, date: LocalDate) {
        viewModelScope.launch {
            // P2#2: a "done" tap records the full target (8/8), not 1.0.
            val target = container.activityRepository.getById(activityId)?.targetPerOccurrence ?: 1.0
            runCatching { container.toggleCompletion(activityId, date, target) }
                .onFailure { messagesChannel.send("Couldn't save — try again.") }
        }
    }

    private fun buildState(
        month: YearMonth,
        activities: List<Activity>,
        completions: List<com.momentum.app.domain.model.CompletionRecord>,
        sel: LocalDate,
        gridStart: LocalDate,
        weekStart: WeekStart,
    ): CalendarUiState {
        val live = activities.filter { it.status != ActivityStatus.ARCHIVED }
        val byActivity = completions.groupBy { it.activityId }
            .mapValues { (_, recs) -> recs.associate { it.occurrenceDate.toLocalDate() to it.value } }
        val cells = (0..41).map { back ->
            val date = gridStart.plusDays(back.toLong())
            var scheduled = 0
            var done = 0
            live.forEach { a ->
                if (!date.isBefore(a.startDate) && (a.endDate == null || !date.isAfter(a.endDate)) &&
                    RecurrenceEngine.occurrencesInRange(a.recurrence, a.startDate, date, date).isNotEmpty()
                ) {
                    scheduled++
                    if (date in byActivity[a.id].orEmpty()) done++
                }
            }
            DayCell(date, date.month == month.month, scheduled, done)
        }
        val selectedItems = container.getTodaySchedule(sel, live.filter { it.status == ActivityStatus.ACTIVE }, byActivity)
        return CalendarUiState(
            month = month,
            today = LocalDate.now(),
            cells = cells,
            selectedDate = sel,
            selectedItems = selectedItems,
            weekStart = weekStart,
            weekDays = orderedWeekDays(weekStart),
        )
    }
}
