package com.momentum.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.usecase.HabitOverview
import com.momentum.app.domain.usecase.toLocalDate
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HabitDetailUiState {
    data object Loading : HabitDetailUiState
    data object NotFound : HabitDetailUiState
    data class Success(
        val activity: Activity,
        val overview: HabitOverview,
        val reminders: List<Reminder>,
        /** Enabled reminder time for the key-config chip, or null (`—`). */
        val reminderTime: String?,
    ) : HabitDetailUiState
}

sealed interface HabitDetailEffect {
    data class ShowMessage(val message: String) : HabitDetailEffect
    data object Deleted : HabitDetailEffect
}

/**
 * Habit Details (Phase 2B): configuration + weekly completion + streaks.
 * No daily completion controls (Decision 23) — Home owns toggling.
 */
class HabitDetailViewModel(
    private val container: AppContainer,
    private val activityId: Long,
) : ViewModel() {

    private val effects = Channel<HabitDetailEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private val refreshTick = MutableStateFlow(0)

    val state = combine(
        container.activityRepository.observeById(activityId),
        container.completionRepository.observeForActivity(activityId),
        container.reminderRepository.observeForActivity(activityId),
        container.settingsRepository.observe(),
        refreshTick,
    ) { activity, completions, reminders, settings, _ ->
        if (activity == null) return@combine HabitDetailUiState.NotFound as HabitDetailUiState
        // Streaks and weekly completion count only *successful* opportunities:
        // a retained below-target partial is progress, not a completed day.
        val valuesByDate = completions.associate { it.occurrenceDate.toLocalDate() to it.value }
        val dates = CompletionEngine.successfulDates(activity, valuesByDate)
        HabitDetailUiState.Success(
            activity = activity,
            overview = container.computeHabitOverview(activity, dates, LocalDate.now(), settings.weekStart),
            reminders = reminders,
            reminderTime = reminders.firstOrNull { it.enabled }?.timeLocal,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitDetailUiState.Loading)

    fun setStatus(status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                container.activityRepository.setStatus(activityId, status)
                if (status != ActivityStatus.ACTIVE) {
                    container.reminderScheduler.cancelForActivity(activityId)
                } else {
                    container.reminderScheduler.scheduleForActivity(activityId)
                }
                refreshTick.value += 1
            }.onFailure { effects.send(HabitDetailEffect.ShowMessage("Couldn't update — try again.")) }
        }
    }

    fun deleteHard() {
        viewModelScope.launch {
            runCatching {
                container.reminderScheduler.cancelForActivity(activityId)
                container.reminderRepository.deleteForActivity(activityId)
                container.activityRepository.deleteHard(activityId)
                effects.send(HabitDetailEffect.Deleted)
            }.onFailure { effects.send(HabitDetailEffect.ShowMessage("Couldn't delete — try again.")) }
        }
    }
}
