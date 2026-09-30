package com.momentum.app.presentation.screens.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.repository.SettingsRepository
import com.momentum.app.domain.usecase.ComputeHabitOverviewUseCase
import com.momentum.app.domain.usecase.HabitOverview
import com.momentum.app.domain.usecase.toLocalDate
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.UiText
import com.momentum.app.presentation.ViewModelArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
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
    @Immutable
    data class Success(
        val activity: Activity,
        val overview: HabitOverview,
        val reminders: List<Reminder>,
        /** Enabled reminder time for the key-config chip, or null (`—`). */
        val reminderTime: String?,
    ) : HabitDetailUiState
}

sealed interface HabitDetailEffect {
    data class ShowMessage(val message: UiText) : HabitDetailEffect
    data object Deleted : HabitDetailEffect
}

/**
 * Habit Details (Phase 2B): configuration + weekly completion + streaks.
 * No daily completion controls (Decision 23) — Home owns toggling.
 */
@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository,
    private val completionRepository: CompletionRepository,
    private val reminderRepository: ReminderRepository,
    private val settingsRepository: SettingsRepository,
    private val computeHabitOverview: ComputeHabitOverviewUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    /** Row to observe; passed via [ViewModelArgs.ACTIVITY_ID] (unknown id → NotFound). */
    private val activityId: Long =
        savedStateHandle.get<String>(ViewModelArgs.ACTIVITY_ID)?.toLongOrNull() ?: -1L

    private val effects = Channel<HabitDetailEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private val refreshTick = MutableStateFlow(0)

    val state = combine(
        activityRepository.observeById(activityId),
        completionRepository.observeForActivity(activityId),
        reminderRepository.observeForActivity(activityId),
        settingsRepository.observe(),
        refreshTick,
    ) { activity, completions, reminders, settings, _ ->
        if (activity == null) return@combine HabitDetailUiState.NotFound as HabitDetailUiState
        // Streaks and weekly completion count only *successful* opportunities:
        // a retained below-target partial is progress, not a completed day.
        val valuesByDate = completions.associate { it.occurrenceDate.toLocalDate() to it.value }
        val dates = CompletionEngine.successfulDates(activity, valuesByDate)
        HabitDetailUiState.Success(
            activity = activity,
            overview = computeHabitOverview(activity, dates, LocalDate.now(), settings.weekStart),
            reminders = reminders,
            reminderTime = reminders.firstOrNull { it.enabled }?.timeLocal,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitDetailUiState.Loading)

    fun setStatus(status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                activityRepository.setStatus(activityId, status)
                if (status != ActivityStatus.ACTIVE) {
                    reminderScheduler.cancelForActivity(activityId)
                } else {
                    reminderScheduler.scheduleForActivity(activityId)
                }
                refreshTick.value += 1
            }.onFailure { effects.send(HabitDetailEffect.ShowMessage(UiText(R.string.error_couldnt_update))) }
        }
    }

    fun deleteHard() {
        viewModelScope.launch {
            runCatching {
                reminderScheduler.cancelForActivity(activityId)
                reminderRepository.deleteForActivity(activityId)
                activityRepository.deleteHard(activityId)
                effects.send(HabitDetailEffect.Deleted)
            }.onFailure { effects.send(HabitDetailEffect.ShowMessage(UiText(R.string.error_couldnt_delete))) }
        }
    }
}
