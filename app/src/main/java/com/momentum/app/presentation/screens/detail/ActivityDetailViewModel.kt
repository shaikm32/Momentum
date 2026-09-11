package com.momentum.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.usecase.ActivityDetailStats
import com.momentum.app.domain.usecase.toLocalDate
import java.time.LocalDate
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
    data class Success(
        val activity: Activity,
        val category: Category?,
        val stats: ActivityDetailStats,
        val reminders: List<Reminder>,
        val completedToday: Boolean,
    ) : DetailUiState
}

sealed interface DetailEffect {
    data class ShowMessage(val message: String) : DetailEffect
    data object Deleted : DetailEffect
}

class ActivityDetailViewModel(
    private val container: AppContainer,
    private val activityId: Long,
) : ViewModel() {

    private val effects = Channel<DetailEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private val refreshTick = MutableStateFlow(0)

    val state = combine(
        container.activityRepository.observeById(activityId),
        container.completionRepository.observeForActivity(activityId),
        container.reminderRepository.observeForActivity(activityId),
        container.categoryRepository.observeAll(),
        refreshTick,
    ) { activity, completions, reminders, categories, _ ->
        if (activity == null) return@combine DetailUiState.NotFound as DetailUiState
        val dates = completions.map { it.occurrenceDate.toLocalDate() }.toSet()
        val today = LocalDate.now()
        DetailUiState.Success(
            activity = activity,
            category = categories.firstOrNull { it.id == activity.categoryId },
            stats = container.computeActivityStats(activity, dates, today),
            reminders = reminders,
            completedToday = today in dates,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState.Loading)

    fun toggleToday() {
        viewModelScope.launch {
            // P2#2: a "done" tap records the full target (8/8), not 1.0.
            val target = container.activityRepository.getById(activityId)?.targetPerOccurrence ?: 1.0
            runCatching { container.toggleCompletion(activityId, LocalDate.now(), target) }
                .onFailure { effects.send(DetailEffect.ShowMessage("Couldn't save — try again.")) }
        }
    }

    fun setStatus(status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                container.activityRepository.setStatus(activityId, status)
                if (status != ActivityStatus.ACTIVE) container.reminderScheduler.cancelForActivity(activityId)
                else container.reminderScheduler.scheduleForActivity(activityId)
                refreshTick.value += 1
            }.onFailure { effects.send(DetailEffect.ShowMessage("Couldn't update — try again.")) }
        }
    }

    fun deleteHard() {
        viewModelScope.launch {
            runCatching {
                container.reminderScheduler.cancelForActivity(activityId)
                container.reminderRepository.deleteForActivity(activityId)
                container.activityRepository.deleteHard(activityId)
                effects.send(DetailEffect.Deleted)
            }.onFailure { effects.send(DetailEffect.ShowMessage("Couldn't delete — try again.")) }
        }
    }
}
