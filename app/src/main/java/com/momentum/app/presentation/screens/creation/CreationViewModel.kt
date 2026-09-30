package com.momentum.app.presentation.screens.creation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.ReduceMode
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.UiText
import com.momentum.app.presentation.ViewModelArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Recurrence picker value. Habits surface a simple preset row
 * (Every day / Weekdays / Specific days / Custom) over this full set;
 * tasks reuse it only when repeat is enabled.
 */
enum class RecurrenceChoice {
    ONCE, DAILY, WEEKDAYS, CUSTOM_DAYS, EVERY_N_DAYS, WEEKLY, EVERY_N_WEEKS, MONTHLY
}

/** Task due-date shortcut. NO_DATE maps to today (see ADR-20). */
enum class DueOption { TODAY, TOMORROW, PICK_DATE, NO_DATE }

/** Curated units (CREATE_ITEM_SPECS §2.5). Custom free text is also accepted. */
val SupportedUnits = listOf("min", "h", "pages", "times", "glasses", "ml", "L", "steps")

/**
 * Single form for the whole guided creation flow (ADR-20): habit create
 * (any intent), task create, and editing of either kind. Only the fields
 * relevant to the current type/intent are shown; everything else is
 * preserved untouched so edits never lose data and Habit ↔ Task conversion
 * is impossible (type/intent have no editor in edit mode).
 */
@Immutable
data class CreationForm(
    val activityId: Long? = null,
    val isNew: Boolean = true,
    /** Semantic kind. Seeded at creation; locked afterwards. */
    val activityType: ActivityType = ActivityType.HABIT,
    /** Habit intent. Seeded at creation (changeable while creating); locked afterwards. */
    val habitIntent: HabitIntent = HabitIntent.BUILD,
    /**
     * REDUCE mode. LIMIT = stay at/below a measurable amount; COMPLETE =
     * zero is the target (same measurable Reduce mechanism, default
     * target with no unit). Only read when [habitIntent] is REDUCE.
     */
    val reduceMode: ReduceMode = ReduceMode.LIMIT,
    /** Selected template id (see `HabitTemplates`); null = none picked yet. */
    val templateId: String? = null,
    // ——— What ———
    val title: String = "",
    /** Inline validation copy, resolved by the screen (null = no error). */
    val titleError: UiText? = null,
    val iconKey: String = "spark",
    /** Legacy free-text field: preserved on edit, never edited here (single Notes field per Decision 05). */
    val description: String = "",
    val notes: String = "",
    val categoryId: Long? = null,
    val priority: Int = 0,
    // ——— Habit measure (Build target / Reduce limit share these; Binary & Complete ignore them) ———
    /** Build only: true = just mark it done, false = reach a target amount. */
    val buildBinary: Boolean = true,
    val amount: String = "30",
    val unit: String = "",
    /** Inline validation copy, resolved by the screen (null = no error). */
    val measureError: UiText? = null,
    // ——— Habit schedule ———
    val recurrenceChoice: RecurrenceChoice = RecurrenceChoice.DAILY,
    val days: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
    val interval: Int = 2,
    val dayOfMonth: Int = 1,
    val startDate: LocalDate = LocalDate.now(),
    val hasEndDate: Boolean = false,
    val endDate: LocalDate = LocalDate.now().plusMonths(1),
    // ——— Task due / repeat ———
    val dueOption: DueOption = DueOption.TODAY,
    val dueDate: LocalDate = LocalDate.now(),
    val hasDueTime: Boolean = false,
    val dueTimePicker: String = "18:00",
    /** Loaded/stored due time (habits preserve it untouched; tasks write from [dueTimePicker]). */
    val dueTime: String? = null,
    val repeatEnabled: Boolean = false,
    // ——— Reminder (single local reminder, ADR-12) ———
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "08:00",
    val isSaving: Boolean = false,
)

sealed interface CreationEffect {
    data class Saved(val activityId: Long) : CreationEffect
    data class Error(val message: UiText) : CreationEffect
}

/** MVI events (ADR-44): the flow's four public actions, dispatched via `onEvent`. */
sealed interface CreationEvent {
    /** Applies one atomic form mutation (field edits, toggles, pickers). */
    data class Update(val transform: (CreationForm) -> CreationForm) : CreationEvent
    /** Switches the top-level intent while creating (Build ↔ Reduce). */
    data class SelectIntent(val intent: HabitIntent) : CreationEvent
    /** Applies a template's defaults to the form. */
    data class SelectTemplate(val template: HabitTemplate) : CreationEvent
    /** Validates and persists the form. */
    data object Save : CreationEvent
}

/**
 * Mirrors [CreationViewModel.save]'s validation for the CTA enabled state.
 * Pure and unit-tested; save() itself re-checks and reports exact errors.
 */
internal fun CreationForm.isSubmittable(): Boolean {
    if (title.isBlank() || title.trim().length > 120) return false
    val isTask = activityType == ActivityType.TASK
    val needsAmount = !isTask &&
        ((habitIntent == HabitIntent.REDUCE && reduceMode == ReduceMode.LIMIT) ||
            (habitIntent == HabitIntent.BUILD && !buildBinary))
    if (needsAmount) {
        if (amount.toDoubleOrNull()?.takeIf { it > 0 } == null) return false
        if (unit.isBlank() || unit.trim().length > 24) return false
    }
    if (!isTask && hasEndDate && endDate.isBefore(startDate)) return false
    return true
}

@HiltViewModel
class CreationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository,
    private val categoryRepository: CategoryRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    /** Row to edit (null = create), via [ViewModelArgs.ACTIVITY_ID]. */
    private val activityId: Long? = savedStateHandle.get<String>(ViewModelArgs.ACTIVITY_ID)?.toLongOrNull()

    /** Semantic kind of the flow, via [ViewModelArgs.ACTIVITY_TYPE] (default habit). */
    private val creationType: ActivityType =
        savedStateHandle.get<String>(ViewModelArgs.ACTIVITY_TYPE)?.let { ActivityType.valueOf(it) }
            ?: ActivityType.HABIT

    /** Habit intent while creating, via [ViewModelArgs.HABIT_INTENT] (default build). */
    private val creationIntent: HabitIntent =
        savedStateHandle.get<String>(ViewModelArgs.HABIT_INTENT)?.let { HabitIntent.valueOf(it) }
            ?: HabitIntent.BUILD

    private val mutableForm = MutableStateFlow(
        CreationForm(
            activityId = activityId,
            isNew = activityId == null,
            activityType = creationType,
            habitIntent = creationIntent,
            // Tasks start one-time, due today; habits keep the daily default.
            recurrenceChoice = if (activityId == null && creationType == ActivityType.TASK) {
                RecurrenceChoice.ONCE
            } else {
                RecurrenceChoice.DAILY
            },
        ),
    )
    val form: StateFlow<CreationForm> = mutableForm.asStateFlow()

    private val effects = Channel<CreationEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()

    val categories = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Category>())

    init {
        if (activityId != null) {
            viewModelScope.launch {
                val existing = activityRepository.getById(activityId)
                if (existing == null) {
                    effects.send(
                        CreationEffect.Error(UiText(R.string.creation_error_activity_not_found)),
                    )
                } else {
                    val reminders = reminderRepository.forActivity(activityId)
                    mutableForm.update { toForm(existing, reminders.firstOrNull()) }
                }
            }
        }
    }

    /** Single entry point for every user action on this flow (ADR-44). */
    fun onEvent(event: CreationEvent) {
        when (event) {
            is CreationEvent.Update -> update(event.transform)
            is CreationEvent.SelectIntent -> selectIntent(event.intent)
            is CreationEvent.SelectTemplate -> selectTemplate(event.template)
            CreationEvent.Save -> save()
        }
    }

    private fun update(transform: (CreationForm) -> CreationForm) {
        mutableForm.update(transform)
    }

    /**
     * Switches the top-level intent while creating. Clears the template
     * selection (the other intent's templates apply) but keeps the typed
     * title/icon/measure so nothing the user entered is lost.
     */
    private fun selectIntent(intent: HabitIntent) {
        mutableForm.update {
            it.copy(habitIntent = intent, templateId = null, titleError = null, measureError = null)
        }
    }

    /**
     * Applies a template's sensible defaults. Everything stays editable
     * afterwards; Custom clears the name/icon for a free-form habit while
     * keeping any measure the user already typed.
     */
    private fun selectTemplate(template: HabitTemplate) {
        mutableForm.update {
            when {
                template.isCustom -> it.copy(
                    templateId = template.id,
                    title = "",
                    iconKey = template.iconKey,
                    titleError = null,
                    measureError = null,
                )
                template.intent == HabitIntent.BUILD && template.amount == null -> it.copy(
                    templateId = template.id,
                    title = template.defaultTitle,
                    iconKey = template.iconKey,
                    buildBinary = true,
                    titleError = null,
                    measureError = null,
                )
                template.intent == HabitIntent.BUILD -> it.copy(
                    templateId = template.id,
                    title = template.defaultTitle,
                    iconKey = template.iconKey,
                    buildBinary = false,
                    amount = template.amount ?: it.amount,
                    unit = template.unit,
                    titleError = null,
                    measureError = null,
                )
                else -> it.copy(
                    templateId = template.id,
                    title = template.defaultTitle,
                    iconKey = template.iconKey,
                    reduceMode = template.reduceMode,
                    amount = template.amount ?: it.amount,
                    unit = template.unit,
                    titleError = null,
                    measureError = null,
                )
            }
        }
    }

    private fun save() {
        val current = mutableForm.value
        val title = current.title.trim()
        if (title.isEmpty()) {
            mutableForm.update { it.copy(titleError = UiText(R.string.creation_error_title_required)) }
            return
        }
        if (title.length > 120) {
            mutableForm.update { it.copy(titleError = UiText(R.string.creation_error_title_too_long)) }
            return
        }
        val isTask = current.activityType == ActivityType.TASK
        val needsAmount = !isTask &&
            ((current.habitIntent == HabitIntent.REDUCE && current.reduceMode == ReduceMode.LIMIT) ||
                (current.habitIntent == HabitIntent.BUILD && !current.buildBinary))
        val parsedAmount = current.amount.toDoubleOrNull()?.takeIf { it > 0 }
        if (needsAmount && parsedAmount == null) {
            mutableForm.update { it.copy(measureError = UiText(R.string.creation_error_amount_required)) }
            return
        }
        if (needsAmount && current.unit.isBlank()) {
            mutableForm.update { it.copy(measureError = UiText(R.string.creation_error_unit_required)) }
            return
        }
        if (needsAmount && current.unit.trim().length > 24) {
            mutableForm.update { it.copy(measureError = UiText(R.string.creation_error_unit_too_long)) }
            return
        }
        // Only habits expose an end date; tasks preserve whatever is stored.
        if (!isTask && current.hasEndDate && current.endDate.isBefore(current.startDate)) {
            viewModelScope.launch {
                effects.send(
                    CreationEffect.Error(UiText(R.string.creation_error_end_before_start)),
                )
            }
            return
        }
        mutableForm.update { it.copy(isSaving = true, titleError = null, measureError = null) }
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val activity = Activity(
                    id = activityId ?: 0,
                    title = title,
                    activityType = current.activityType,
                    habitIntent = current.habitIntent,
                    // Tasks keep the reminder-independent due time; habits
                    // preserve the stored value (no habit UI edits it).
                    dueTime = if (isTask) {
                        current.dueTimePicker.takeIf { current.hasDueTime }
                    } else {
                        current.dueTime
                    },
                    description = current.description,
                    iconKey = current.iconKey,
                    habitTemplateId = current.templateId,
                    categoryId = current.categoryId,
                    startDate = if (isTask) current.dueDate else current.startDate,
                    // Tasks have no end-date UI (repeat indefinitely); the
                    // stored value is preserved untouched on edit.
                    endDate = current.endDate.takeIf { current.hasEndDate },
                    recurrence = if (isTask && !current.repeatEnabled) {
                        Recurrence.Once
                    } else {
                        toRecurrence(current)
                    },
                    targetPerOccurrence = when {
                        isTask -> current.amount.toDoubleOrNull()?.takeIf { it > 0 } ?: 1.0
                        current.habitIntent == HabitIntent.REDUCE &&
                            current.reduceMode == ReduceMode.COMPLETE -> 1.0
                        current.habitIntent == HabitIntent.BUILD && current.buildBinary -> 1.0
                        else -> parsedAmount ?: 1.0
                    },
                    unit = when {
                        isTask -> current.unit
                        current.habitIntent == HabitIntent.REDUCE &&
                            current.reduceMode == ReduceMode.COMPLETE -> ""
                        current.habitIntent == HabitIntent.BUILD && current.buildBinary -> ""
                        else -> current.unit.trim()
                    },
                    priority = current.priority.coerceIn(0, 3),
                    notes = current.notes.trim(),
                    status = activityRepository.getById(activityId ?: -1)?.status
                        ?: ActivityStatus.ACTIVE,
                )
                val savedId = activityRepository.saveWithReminders(
                    // v1: a single local reminder per activity (schema supports N; UI adds more later).
                    activity,
                    if (current.reminderEnabled) {
                        listOf(
                            Reminder(
                                timeLocal = current.reminderTime,
                                enabled = true,
                                activityId = activityId ?: 0,
                            ),
                        )
                    } else {
                        emptyList()
                    },
                )
                reminderScheduler.scheduleForActivity(savedId)
                effects.send(CreationEffect.Saved(savedId))
            } catch (t: Throwable) {
                mutableForm.update { it.copy(isSaving = false) }
                effects.send(CreationEffect.Error(UiText(R.string.error_couldnt_save)))
            }
        }
    }

    private fun toRecurrence(f: CreationForm): Recurrence = when (f.recurrenceChoice) {
        RecurrenceChoice.ONCE -> Recurrence.Once
        RecurrenceChoice.DAILY -> Recurrence.Daily
        RecurrenceChoice.WEEKDAYS -> Recurrence.Weekdays
        RecurrenceChoice.CUSTOM_DAYS -> Recurrence.SelectedWeekdays(
            f.days.ifEmpty { setOf(DayOfWeek.MONDAY) },
        )
        RecurrenceChoice.EVERY_N_DAYS -> Recurrence.EveryNDays(f.interval.coerceAtLeast(1))
        RecurrenceChoice.WEEKLY -> Recurrence.Weekly
        RecurrenceChoice.EVERY_N_WEEKS -> Recurrence.EveryNWeeks(
            f.interval.coerceAtLeast(1),
            f.days.ifEmpty { setOf(DayOfWeek.MONDAY) },
        )
        RecurrenceChoice.MONTHLY -> Recurrence.Monthly(f.dayOfMonth.coerceIn(1, 31))
    }

    private fun toForm(activity: Activity, reminder: Reminder?): CreationForm {
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
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        return CreationForm(
            activityId = activity.id,
            isNew = false,
            // Persisted kind wins: no UI edits these, so Habit ↔ Task
            // conversion and intent changes on edit are impossible.
            activityType = activity.activityType,
            habitIntent = activity.habitIntent,
            // REDUCE mode derives from the stored measure (LIMIT carries a
            // real amount/unit; COMPLETE is the zero-target default), so
            // legacy rows and edits both land on the correct mode.
            reduceMode = activity.reduceMode(),
            // Persisted template id wins; title+icon matching is the legacy
            // fallback for pre-v4 rows so their icon lock still works after
            // migration until the next save stamps the column.
            templateId = activity.habitTemplateId
                ?: matchTemplate(activity.habitIntent, activity.title, activity.iconKey),
            title = activity.title,
            iconKey = activity.iconKey,
            description = activity.description,
            notes = activity.notes,
            categoryId = activity.categoryId,
            priority = activity.priority,
            // Binary ⟺ default target with no unit (BUILD only; REDUCE rows
            // always show their stored value). Amount/unit always load so
            // flipping Binary → Target mid-edit restores the previous value
            // instead of blanking it.
            buildBinary = if (activity.habitIntent == HabitIntent.BUILD) {
                activity.targetPerOccurrence == 1.0 && activity.unit.isBlank()
            } else {
                true
            },
            amount = trimTarget(activity.targetPerOccurrence),
            unit = activity.unit,
            recurrenceChoice = choice,
            days = days ?: setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            interval = interval ?: 2,
            dayOfMonth = dayOfMonth ?: activity.startDate.dayOfMonth.coerceIn(1, 28),
            startDate = activity.startDate,
            hasEndDate = activity.endDate != null,
            endDate = activity.endDate ?: activity.startDate.plusMonths(1),
            dueOption = when (activity.startDate) {
                today -> DueOption.TODAY
                tomorrow -> DueOption.TOMORROW
                else -> DueOption.PICK_DATE
            },
            dueDate = activity.startDate,
            hasDueTime = activity.dueTime != null,
            dueTimePicker = activity.dueTime ?: "18:00",
            dueTime = activity.dueTime,
            repeatEnabled = activity.recurrence !is Recurrence.Once,
            reminderEnabled = reminder?.enabled == true,
            reminderTime = reminder?.timeLocal ?: "08:00",
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
