package com.momentum.app.presentation.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Reminder
import com.momentum.app.presentation.components.formatAmount
import com.momentum.app.presentation.components.recurrenceLabel
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** One row on the Habits management list. */
data class HabitListItem(
    val activity: Activity,
    /** Enabled reminder `HH:mm`, or null (card shows `—`). */
    val reminderTime: String?,
    /**
     * `Frequency · Target · Reminder` metadata with three fixed slots —
     * missing values render as `—` so every card stays aligned.
     * No streak/progress/history on list cards.
     */
    val metadata: String,
)

data class HabitsUiState(
    val items: List<HabitListItem> = emptyList(),
    val isEmpty: Boolean = false,
)

sealed interface HabitsEffect {
    data class ShowMessage(val message: String) : HabitsEffect
}

/**
 * Habits management list (Phase 2B): ACTIVE + PAUSED habits only, ordered
 * Active then Paused (Decision UI §1). No search/filter, no completion.
 */
class HabitsViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val messagesChannel = Channel<String>(Channel.BUFFERED)
    val messages = messagesChannel.receiveAsFlow()

    private val refreshTick = MutableStateFlow(0)

    val state = combine(
        container.activityRepository.observeAll(),
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
            habit.id to runCatching { container.reminderRepository.forActivity(habit.id) }
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
                    metadata = habitMetadata(habit, reminder?.timeLocal),
                )
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitsUiState())

    fun pause(id: Long) = updateStatus(id, ActivityStatus.PAUSED)
    fun resume(id: Long) = updateStatus(id, ActivityStatus.ACTIVE)

    fun deleteHard(id: Long) {
        viewModelScope.launch {
            runCatching {
                container.reminderScheduler.cancelForActivity(id)
                container.reminderRepository.deleteForActivity(id)
                container.activityRepository.deleteHard(id)
                refreshTick.value += 1
            }.onFailure { messagesChannel.send("Couldn't delete — try again.") }
        }
    }

    private fun updateStatus(id: Long, status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                container.activityRepository.setStatus(id, status)
                if (status != ActivityStatus.ACTIVE) {
                    container.reminderScheduler.cancelForActivity(id)
                } else {
                    container.reminderScheduler.scheduleForActivity(id)
                }
                refreshTick.value += 1
            }.onFailure { messagesChannel.send("Couldn't update — try again.") }
        }
    }

    private fun habitMetadata(habit: Activity, reminderTime: String?): String {
        val frequency = recurrenceLabel(habit.recurrence)
        val target = if (habit.targetPerOccurrence != 1.0 || habit.unit.isNotBlank()) {
            "${formatAmount(habit.targetPerOccurrence)} ${habit.unit}".trim()
        } else {
            "—"
        }
        val reminder = reminderTime ?: "—"
        return "$frequency · $target · $reminder"
    }
}
