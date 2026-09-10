package com.momentum.app.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate
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
)

class CalendarViewModel(private val container: AppContainer) : ViewModel() {

    private val monthOffset = MutableStateFlow(0)
    private val selected = MutableStateFlow(LocalDate.now())

    private val messagesChannel = Channel<String>(Channel.BUFFERED)
    val messages = messagesChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = monthOffset.flatMapLatest { offset ->
        val month = YearMonth.now().plusMonths(offset.toLong())
        val gridStart = month.atDay(1).minusDays(((month.atDay(1).dayOfWeek.value - 1) % 7).toLong())
        val gridEnd = gridStart.plusDays(41)
        combine(
            container.activityRepository.observeAll(),
            container.completionRepository.observeInRange(gridStart.toIso(), gridEnd.toIso()),
            selected,
        ) { activities, completions, sel ->
            buildState(month, activities, completions, sel, gridStart)
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
            runCatching { container.toggleCompletion(activityId, date) }
                .onFailure { messagesChannel.send("Couldn't save — try again.") }
        }
    }

    private fun buildState(
        month: YearMonth,
        activities: List<Activity>,
        completions: List<com.momentum.app.domain.model.CompletionRecord>,
        sel: LocalDate,
        gridStart: LocalDate,
    ): CalendarUiState {
        val live = activities.filter { it.status != ActivityStatus.ARCHIVED }
        val byActivity = completions.groupBy { it.activityId }
            .mapValues { (_, recs) -> recs.map { it.occurrenceDate.toLocalDate() }.toSet() }
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
        return CalendarUiState(month, LocalDate.now(), cells, sel, selectedItems)
    }
}
