package com.momentum.app.presentation.screens.today

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.SettingsRepository
import com.momentum.app.domain.usecase.GetTodayScheduleUseCase
import com.momentum.app.domain.usecase.SetActualValueUseCase
import com.momentum.app.domain.usecase.ToggleCompletionUseCase
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.TodaySections
import com.momentum.app.domain.usecase.partitionTodayItems
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate
import com.momentum.app.presentation.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface TodayUiState {
    /**
     * Open **Reach a Target** dialog (null = none), folded into the single
     * state stream as a combine source so the screen collects ONE
     * `StateFlow<TodayUiState>` (ADR-44). Transient UI state only — the
     * authoritative completion state stays in Room.
     */
    val targetEntry: TargetEntryState?

    data object Loading : TodayUiState {
        override val targetEntry: TargetEntryState? get() = null
    }
    /**
     * Derived view of today (DASHBOARD_SPECS §22): eligibility → completion →
     * sections → Hero progress. [sections] is the only completion-derived
     * state the UI reads; there is no second completion flag in Compose.
     */
    @Immutable
    data class Success(
        val date: LocalDate,
        val dateLabel: String,
        /** Time-of-day greeting, resolved by the screen (DECISIONS §6). */
        val greeting: UiText,
        val sections: TodaySections,
        /** Null = the Hero says nothing at all (see [messageFor]). */
        val message: UiText?,
        /** Null = follow the system animator setting (resolved in the composable). */
        val reduceMotionOverride: Boolean?,
        override val targetEntry: TargetEntryState? = null,
    ) : TodayUiState {
        val remaining: List<TodayItem> get() = sections.remaining
        val completed: List<TodayItem> get() = sections.completed
        val progress: Float get() = sections.progress
        val doneCount: Int get() = sections.doneCount
        val totalCount: Int get() = sections.totalCount
        val isEmpty: Boolean get() = sections.isEmpty
        val allComplete: Boolean get() = sections.allComplete
    }
    data class Error(val message: UiText) : TodayUiState {
        override val targetEntry: TargetEntryState? get() = null
    }
}

sealed interface TodayEffect {
    data class Celebrate(val message: UiText) : TodayEffect
    data class ShowMessage(val message: UiText) : TodayEffect
}

/** MVI events (ADR-44): the screen's five public actions, dispatched via `onEvent`. */
sealed interface TodayEvent {
    /** Re-derives today's plan: initial load, error retry, explicit refresh. */
    data object Refresh : TodayEvent
    /** Completion control tap — completes, or opens the target-entry dialog. */
    data class CompletionTap(val activityId: Long) : TodayEvent
    /** Persists the entered actual value from the target-entry dialog. */
    data class SaveTargetEntry(val activityId: Long, val actual: Double) : TodayEvent
    /** Explicit reversal from the dialog: drops today's record. */
    data class ClearTargetEntry(val activityId: Long) : TodayEvent
    /** Closes the target-entry dialog without writing. */
    data object DismissTargetEntry : TodayEvent
}

/**
 * Open **Reach a Target** entry for one activity. Transient UI state — the
 * authoritative completion state stays in Room; this only carries which card
 * opened the dialog and the value it should start from.
 */
data class TargetEntryState(
    val activityId: Long,
    val title: String,
    val target: Double,
    val unit: String,
    val currentActual: Double,
    val completed: Boolean,
)

/** Hero experience: what matters today, wired to the real repository. */
@HiltViewModel
class TodayViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val completionRepository: CompletionRepository,
    private val settingsRepository: SettingsRepository,
    private val toggleCompletion: ToggleCompletionUseCase,
    private val setActualValue: SetActualValueUseCase,
    private val getTodaySchedule: GetTodayScheduleUseCase,
) : ViewModel() {

    private val refreshTick = MutableStateFlow(0)
    private val effects = Channel<TodayEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private var lastProgress: Float? = null

    /** Which target-entry dialog is open (ADR-44: mutated via atomic `update` only). */
    private val _targetEntry = MutableStateFlow<TargetEntryState?>(null)

    init {
        // DASHBOARD_SPECS §19 / DECISIONS §25: at local midnight the screen
        // re-evaluates eligibility, completion state, progress and streaks.
        // Today is always the device's local calendar date — never a UTC
        // boundary — so the wait is measured against the system zone and a
        // DST shift only moves the wake-up instant, never the date itself.
        viewModelScope.launch {
            while (isActive) {
                val zone = ZoneId.systemDefault()
                val nextMidnight = LocalDate.now(zone).plusDays(1).atStartOfDay(zone)
                val waitMs = Duration.between(ZonedDateTime.now(zone), nextMidnight).toMillis()
                delay(waitMs.coerceAtLeast(1L))
                refreshTick.value += 1
            }
        }
    }

    /**
     * One state stream (ADR-44): the day's derived plan combined with the
     * target-entry holder, so the screen collects a single
     * `StateFlow<TodayUiState>`.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<TodayUiState> = combine(
        refreshTick.flatMapLatest {
            val today = LocalDate.now()
            val from = today.minusDays(730).toIso()
            combine(
                activityRepository.observeByStatus(listOf(ActivityStatus.ACTIVE)),
                completionRepository.observeInRange(from, today.toIso()),
                settingsRepository.observe(),
            ) { activities, completions, settings ->
                buildState(today, activities, completions, settings)
            }
        },
        _targetEntry,
    ) { base, targetEntry ->
        if (base is TodayUiState.Success) base.copy(targetEntry = targetEntry) else base
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState.Loading)

    /** Single entry point for every user action on this screen (ADR-44). */
    fun onEvent(event: TodayEvent) {
        when (event) {
            TodayEvent.Refresh -> refresh()
            is TodayEvent.CompletionTap -> onCompletionTap(event.activityId)
            is TodayEvent.SaveTargetEntry -> saveTargetEntry(event.activityId, event.actual)
            is TodayEvent.ClearTargetEntry -> clearTargetEntry(event.activityId)
            TodayEvent.DismissTargetEntry -> dismissTargetEntry()
        }
    }

    private fun refresh() {
        refreshTick.value += 1
    }

    /**
     * Single entry point for the completion control (DECISIONS §12).
     *
     * - **Just Mark it Done** completes/reverses immediately, no dialog.
     * - **Reach a Target** opens the target-entry dialog (DASHBOARD_SPECS
     *   §10.2); completion is derived from the value the user saves.
     *
     * The branch lives here, not in Compose — the UI only reports the tap.
     */
    private fun onCompletionTap(activityId: Long) {
        val today = LocalDate.now()
        viewModelScope.launch {
            val activity = runCatching { activityRepository.getById(activityId) }.getOrNull()
            if (activity == null) {
                effects.send(TodayEffect.ShowMessage(CouldntSave))
                return@launch
            }
            if (!CompletionEngine.isTargetBased(activity)) {
                runCatching { toggleCompletion(activityId, today, activity.targetPerOccurrence) }
                    .onFailure { effects.send(TodayEffect.ShowMessage(CouldntSave)) }
                return@launch
            }
            val iso = today.toIso()
            val stored = runCatching {
                completionRepository.recordsForActivity(activityId)
            }.getOrNull()?.firstOrNull { it.occurrenceDate == iso }?.value ?: 0.0
            _targetEntry.update {
                TargetEntryState(
                    activityId = activityId,
                    title = activity.title,
                    target = activity.targetPerOccurrence,
                    unit = activity.unit,
                    currentActual = stored,
                    completed = CompletionEngine.isComplete(activity, stored),
                )
            }
        }
    }

    /**
     * Persists the entered actual value. The record (not a boolean flag) is
     * the source of truth, so 5 → 8 completes and 8 → 5 reverses, with the
     * partial value retained either way.
     */
    private fun saveTargetEntry(activityId: Long, actual: Double) {
        viewModelScope.launch {
            runCatching { setActualValue(activityId, LocalDate.now(), actual) }
                .onSuccess { _targetEntry.update { null } }
                .onFailure { effects.send(TodayEffect.ShowMessage(CouldntSave)) }
        }
    }

    /** Explicit reversal from the dialog: drop today's record entirely. */
    private fun clearTargetEntry(activityId: Long) = saveTargetEntry(activityId, 0.0)

    private fun dismissTargetEntry() {
        _targetEntry.update { null }
    }

    private suspend fun buildState(
        today: LocalDate,
        activities: List<com.momentum.app.domain.model.Activity>,
        completions: List<com.momentum.app.domain.model.CompletionRecord>,
        settings: AppSettings,
    ): TodayUiState {
        return try {
            val byActivity = completions.groupBy { it.activityId }
                .mapValues { (_, recs) -> recs.associate { it.occurrenceDate.toLocalDate() to it.value } }
            val sections = partitionTodayItems(
                getTodaySchedule(today, activities, byActivity),
            )
            val progress = sections.progress
            val prev = lastProgress
            lastProgress = progress
            if (prev != null && prev < 1f && progress == 1f && sections.totalCount > 0) {
                effects.send(
                    TodayEffect.Celebrate(UiText(R.string.today_celebrate_all_done)),
                )
            }
            TodayUiState.Success(
                date = today,
                // DECISIONS §5: concise date above the greeting — "FRI, SEP 25".
                dateLabel = today
                    .format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()))
                    .uppercase(Locale.getDefault()),
                greeting = greeting(),
                sections = sections,
                message = messageFor(
                    sections.totalCount,
                    sections.doneCount,
                    settings.motivationalMessagesEnabled,
                ),
                reduceMotionOverride = settings.reduceMotionOverride,
            )
        } catch (t: Throwable) {
            TodayUiState.Error(UiText(R.string.today_error_load_plan))
        }
    }

    /** DECISIONS §6: four greetings, chosen from the device's local time. */
    private fun greeting(): UiText = when (LocalTime.now().hour) {
        in 5..11 -> UiText(R.string.today_greeting_morning)
        in 12..16 -> UiText(R.string.today_greeting_afternoon)
        in 17..21 -> UiText(R.string.today_greeting_evening)
        else -> UiText(R.string.today_greeting_night)
    }

    /**
     * The Hero's short contextual line (DECISIONS §9). It is never a random
     * quote — it always describes the user's current state. When motivational
     * messages are switched off the line is dropped entirely, because the
     * Hero's completion count already states progress and repeating it would
     * only add noise.
     *
     * An empty day carries **no** line at all: with nothing to report the Hero
     * says nothing, and the artwork runs straight into the No-Activity card
     * below it instead of being interrupted by copy (Phase 2C). Null is that
     * "say nothing" case — the screen renders it as no text at all.
     */
    private fun messageFor(total: Int, done: Int, motivational: Boolean): UiText? {
        if (total == 0) return null
        if (done == total) return UiText(R.string.today_message_all_complete)
        if (!motivational) return null
        val remaining = total - done
        return when {
            done == 0 -> UiText(
                if (remaining == 1) R.string.today_message_one_small_win
                else R.string.today_message_small_steps,
            )
            remaining == 1 -> UiText(R.string.today_message_one_more)
            done == 1 -> UiText(R.string.today_message_first_win)
            else -> UiText(R.string.today_message_building)
        }
    }
}

/** Shared failure copy for every completion/completion-record write. */
private val CouldntSave = UiText(R.string.error_couldnt_save)
