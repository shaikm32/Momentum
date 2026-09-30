package com.momentum.app.presentation.screens.habits

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.UiText
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

/** One row on the Habits management list. */
@Immutable
data class HabitListItem(
    val activity: Activity,
    /** Enabled reminder `HH:mm`, or null (card shows `—`). */
    val reminderTime: String?,
)

@Immutable
data class HabitsUiState(
    val items: List<HabitListItem> = emptyList(),
    val isEmpty: Boolean = false,
)

sealed interface HabitsEffect {
    data class ShowMessage(val message: UiText) : HabitsEffect
}

/**
 * Habits management list (Phase 2B): ACTIVE + PAUSED habits only, ordered
 * Active then Paused (Decision UI §1). No search/filter, no completion.
 */
@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val effects = Channel<HabitsEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()

    private val refreshTick = MutableStateFlow(0)

    val state = combine(
        activityRepository.observeAll(),
        refreshTick,
    ) { activities, _ ->
        val habits = activities
            .filter {
                it.activityType == ActivityType.HABIT &&
                    (it.status == ActivityStatus.ACTIVE || it.status == ActivityStatus.PAUSED)
            }
            .sortedWith(
                compareByDescending<Activity> { it.status == ActivityStatus.ACTIVE }
                    .thenByDescending { it.priority }
                    .thenBy { it.title.lowercase() },
            )
        val remindersByActivity: Map<Long, List<Reminder>> = habits.associate { habit ->
            habit.id to runCatching { reminderRepository.forActivity(habit.id) }
                .getOrDefault(emptyList())
        }
        HabitsUiState(
            isEmpty = habits.isEmpty(),
            items = habits.map { habit ->
                val reminder = remindersByActivity[habit.id].orEmpty()
                    .firstOrNull { it.enabled }
                HabitListItem(
                    activity = habit,
                    reminderTime = reminder?.timeLocal,
                )
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitsUiState())

    fun pause(id: Long) = updateStatus(id, ActivityStatus.PAUSED)
    fun resume(id: Long) = updateStatus(id, ActivityStatus.ACTIVE)

    fun deleteHard(id: Long) {
        viewModelScope.launch {
            runCatching {
                reminderScheduler.cancelForActivity(id)
                reminderRepository.deleteForActivity(id)
                activityRepository.deleteHard(id)
                refreshTick.value += 1
            }.onFailure { effects.send(HabitsEffect.ShowMessage(UiText(R.string.error_couldnt_delete))) }
        }
    }

    private fun updateStatus(id: Long, status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                activityRepository.setStatus(id, status)
                if (status != ActivityStatus.ACTIVE) {
                    reminderScheduler.cancelForActivity(id)
                } else {
                    reminderScheduler.scheduleForActivity(id)
                }
                refreshTick.value += 1
            }.onFailure { effects.send(HabitsEffect.ShowMessage(UiText(R.string.error_couldnt_update))) }
        }
    }
}
