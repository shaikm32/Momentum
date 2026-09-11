package com.momentum.app.presentation.screens.activityedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.Reminder
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RecurrenceChoice { ONCE, DAILY, WEEKDAYS, CUSTOM_DAYS, EVERY_N_DAYS, WEEKLY, EVERY_N_WEEKS, MONTHLY }

data class EditForm(
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val iconKey: String = "spark",
    val categoryId: Long? = null,
    val recurrenceChoice: RecurrenceChoice = RecurrenceChoice.DAILY,
    val days: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
    val interval: Int = 2,
    val dayOfMonth: Int = 1,
    val startDate: LocalDate = LocalDate.now(),
    val hasEndDate: Boolean = false,
    val endDate: LocalDate = LocalDate.now().plusMonths(1),
    val target: String = "1",
    val unit: String = "",
    val priority: Int = 0,
    val notes: String = "",
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "08:00",
    val isNew: Boolean = true,
    val isSaving: Boolean = false,
)

sealed interface EditEffect {
    data class Saved(val activityId: Long) : EditEffect
    data class Error(val message: String) : EditEffect
}

class ActivityEditViewModel(
    private val container: AppContainer,
    private val activityId: Long?,
) : ViewModel() {

    private val mutableForm = MutableStateFlow(EditForm(isNew = activityId == null))
    val form: StateFlow<EditForm> = mutableForm.asStateFlow()

    private val effects = Channel<EditEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()

    val categories = container.categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Category>())

    init {
        if (activityId != null) {
            viewModelScope.launch {
                val existing = container.activityRepository.getById(activityId)
                if (existing == null) {
                    effects.send(EditEffect.Error("Activity not found."))
                } else {
                    val reminders = container.reminderRepository.forActivity(activityId)
                    mutableForm.update { toForm(existing, reminders.firstOrNull()) }
                }
            }
        }
    }

    fun update(transform: (EditForm) -> EditForm) {
        mutableForm.update(transform)
    }

    fun save() {
        val current = mutableForm.value
        val title = current.title.trim()
        if (title.isEmpty()) {
            mutableForm.update { it.copy(titleError = "Give it a name.") }
            return
        }
        if (title.length > 120) {
            mutableForm.update { it.copy(titleError = "Keep it under 120 characters.") }
            return
        }
        if (current.hasEndDate && current.endDate.isBefore(current.startDate)) {
            viewModelScope.launch { effects.send(EditEffect.Error("End date can't be before start date.")) }
            return
        }
        val target = current.target.toDoubleOrNull()?.takeIf { it > 0 } ?: 1.0
        mutableForm.update { it.copy(isSaving = true, titleError = null) }
        viewModelScope.launch {
            try {
                val recurrence = toRecurrence(current)
                val now = System.currentTimeMillis()
                val activity = Activity(
                    id = activityId ?: 0,
                    title = title,
                    description = current.description.trim(),
                    iconKey = current.iconKey,
                    categoryId = current.categoryId,
                    startDate = current.startDate,
                    endDate = current.endDate.takeIf { current.hasEndDate },
                    recurrence = recurrence,
                    targetPerOccurrence = target,
                    unit = current.unit.trim(),
                    priority = current.priority.coerceIn(0, 3),
                    notes = current.notes.trim(),
                    status = container.activityRepository.getById(activityId ?: -1)?.status
                        ?: ActivityStatus.ACTIVE,
                )
                val savedId = container.activityRepository.saveWithReminders(
                    // v1: a single local reminder per activity (schema supports N; UI adds more later).
                    activity,
                    if (current.reminderEnabled) {
                        listOf(Reminder(timeLocal = current.reminderTime, enabled = true, activityId = activityId ?: 0))
                    } else {
                        emptyList()
                    },
                )
                container.reminderScheduler.scheduleForActivity(savedId)
                effects.send(EditEffect.Saved(savedId))
            } catch (t: Throwable) {
                mutableForm.update { it.copy(isSaving = false) }
                effects.send(EditEffect.Error("Couldn't save — try again."))
            }
        }
    }

    private fun toRecurrence(f: EditForm): Recurrence = when (f.recurrenceChoice) {
        RecurrenceChoice.ONCE -> Recurrence.Once
        RecurrenceChoice.DAILY -> Recurrence.Daily
        RecurrenceChoice.WEEKDAYS -> Recurrence.Weekdays
        RecurrenceChoice.CUSTOM_DAYS -> Recurrence.SelectedWeekdays(f.days.ifEmpty { setOf(DayOfWeek.MONDAY) })
        RecurrenceChoice.EVERY_N_DAYS -> Recurrence.EveryNDays(f.interval.coerceAtLeast(1))
        RecurrenceChoice.WEEKLY -> Recurrence.Weekly
        RecurrenceChoice.EVERY_N_WEEKS ->
            Recurrence.EveryNWeeks(f.interval.coerceAtLeast(1), f.days.ifEmpty { setOf(DayOfWeek.MONDAY) })
        RecurrenceChoice.MONTHLY -> Recurrence.Monthly(f.dayOfMonth.coerceIn(1, 31))
    }

    private fun toForm(activity: Activity, reminder: Reminder?): EditForm {
        val (choice, days, interval, dayOfMonth) = when (val r = activity.recurrence) {
            is Recurrence.Once -> Quad(RecurrenceChoice.ONCE, null, null, null)
            is Recurrence.Daily -> Quad(RecurrenceChoice.DAILY, null, null, null)
            is Recurrence.Weekdays -> Quad(RecurrenceChoice.WEEKDAYS, null, null, null)
            is Recurrence.SelectedWeekdays -> Quad(RecurrenceChoice.CUSTOM_DAYS, r.days, null, null)
            is Recurrence.EveryNDays -> Quad(RecurrenceChoice.EVERY_N_DAYS, null, r.n, null)
            is Recurrence.Weekly -> Quad(RecurrenceChoice.WEEKLY, null, null, null)
            is Recurrence.EveryNWeeks -> Quad(RecurrenceChoice.EVERY_N_WEEKS, r.days, r.n, null)
            is Recurrence.Monthly -> Quad(RecurrenceChoice.MONTHLY, null, null, r.dayOfMonth)
        }
        return EditForm(
            title = activity.title,
            description = activity.description,
            iconKey = activity.iconKey,
            categoryId = activity.categoryId,
            recurrenceChoice = choice,
            days = days ?: setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            interval = interval ?: 2,
            dayOfMonth = dayOfMonth ?: activity.startDate.dayOfMonth.coerceIn(1, 28),
            startDate = activity.startDate,
            hasEndDate = activity.endDate != null,
            endDate = activity.endDate ?: activity.startDate.plusMonths(1),
            target = trimTarget(activity.targetPerOccurrence),
            unit = activity.unit,
            priority = activity.priority,
            notes = activity.notes,
            reminderEnabled = reminder?.enabled == true,
            reminderTime = reminder?.timeLocal ?: "08:00",
            isNew = false,
        )
    }

    private fun trimTarget(v: Double): String =
        if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()

    private data class Quad(
        val choice: RecurrenceChoice,
        val days: Set<DayOfWeek>?,
        val interval: Int?,
        val dayOfMonth: Int?,
    )
}
