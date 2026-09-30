package com.momentum.app.presentation.screens.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.usecase.ActivityDetailStats
import com.momentum.app.domain.usecase.ComputeActivityStatsUseCase
import com.momentum.app.domain.usecase.ToggleCompletionUseCase
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

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data object NotFound : DetailUiState
    @Immutable
    data class Success(
        val activity: Activity,
        val category: Category?,
        val stats: ActivityDetailStats,
        val reminders: List<Reminder>,
        val completedToday: Boolean,
    ) : DetailUiState
}

sealed interface DetailEffect {
    data class ShowMessage(val message: UiText) : DetailEffect
    data object Deleted : DetailEffect
}

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository,
    private val completionRepository: CompletionRepository,
    private val reminderRepository: ReminderRepository,
    private val categoryRepository: CategoryRepository,
    private val computeActivityStats: ComputeActivityStatsUseCase,
    private val toggleCompletion: ToggleCompletionUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    /** Row to observe; passed via [ViewModelArgs.ACTIVITY_ID] (unknown id → NotFound). */
    private val activityId: Long =
        savedStateHandle.get<String>(ViewModelArgs.ACTIVITY_ID)?.toLongOrNull() ?: -1L

    private val effects = Channel<DetailEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private val refreshTick = MutableStateFlow(0)

    val state = combine(
        activityRepository.observeById(activityId),
        completionRepository.observeForActivity(activityId),
        reminderRepository.observeForActivity(activityId),
        categoryRepository.observeAll(),
        refreshTick,
    ) { activity, completions, reminders, categories, _ ->
        if (activity == null) return@combine DetailUiState.NotFound as DetailUiState
        // One completion record per (activity, date); the stored value decides
        // whether that date was a *successful* opportunity (actual >= target).
        val valuesByDate = completions.associate { it.occurrenceDate.toLocalDate() to it.value }
        val dates = CompletionEngine.successfulDates(activity, valuesByDate)
        val today = LocalDate.now()
        DetailUiState.Success(
            activity = activity,
            category = categories.firstOrNull { it.id == activity.categoryId },
            stats = computeActivityStats(activity, dates, today),
            reminders = reminders,
            completedToday = CompletionEngine.isComplete(activity, valuesByDate[today]),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState.Loading)

    fun toggleToday() {
        viewModelScope.launch {
            // P2#2: a "done" tap records the full target (8/8), not 1.0.
            val target = activityRepository.getById(activityId)?.targetPerOccurrence ?: 1.0
            runCatching { toggleCompletion(activityId, LocalDate.now(), target) }
                .onFailure { effects.send(DetailEffect.ShowMessage(UiText(R.string.error_couldnt_save))) }
        }
    }

    fun setStatus(status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                activityRepository.setStatus(activityId, status)
                if (status != ActivityStatus.ACTIVE) reminderScheduler.cancelForActivity(activityId)
                else reminderScheduler.scheduleForActivity(activityId)
                refreshTick.value += 1
            }.onFailure { effects.send(DetailEffect.ShowMessage(UiText(R.string.error_couldnt_update))) }
        }
    }

    fun deleteHard() {
        viewModelScope.launch {
            runCatching {
                reminderScheduler.cancelForActivity(activityId)
                reminderRepository.deleteForActivity(activityId)
                activityRepository.deleteHard(activityId)
                effects.send(DetailEffect.Deleted)
            }.onFailure { effects.send(DetailEffect.ShowMessage(UiText(R.string.error_couldnt_delete))) }
        }
    }
}
