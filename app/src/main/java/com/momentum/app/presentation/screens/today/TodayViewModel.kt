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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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
        /**
         * The state-specific encouragement line (ADR-48 Amendment Note 1), or
         * null when the day's state or the Settings toggle says there is no
         * line. Null means the Hero renders no line and reserves no space.
         */
        val encouragement: UiText?,
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
    /**
     * The one-shot 0 → 1 milestone signal (ADR-48 §3). Payload-free: the
     * screen maps it to the transient Aurora/reward/haptic moment and shows
     * no snackbar — the Hero already states "Today is complete".
     */
    data object Celebrate : TodayEffect
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

    /**
     * True while a retry from the error branch is fetching. It makes the state
     * stream emit a real [TodayUiState.Loading] before the retry's result.
     *
     * Without that transition a retry can strand the screen: an error is
     * structurally equal to the previous error, so the state flow de-duplicates
     * it, and a presentation-local "retrying" flag would never be reset (or
     * would stick the skeleton over a settled error, or the error over the
     * skeleton). The Loading value is distinct, so Error → Loading → Error (or
     * Success) always emits and the screen always leaves the retry state.
     */
    private val retrying = MutableStateFlow(false)

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
        // This is not a retry: it must not flash the skeleton.
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
            val data = combine(
                activityRepository.observeByStatus(listOf(ActivityStatus.ACTIVE)),
                completionRepository.observeInRange(from, today.toIso()),
                settingsRepository.observe(),
            ) { activities, completions, settings ->
                buildState(today, activities, completions, settings)
            }
            // A retry emits the skeleton first (see [retrying]); a payload
            // refresh — a completion, the midnight tick — never does.
            if (retrying.value) data.onStart { emit(TodayUiState.Loading) } else data
        },
        _targetEntry,
    ) { base, targetEntry ->
        if (base is TodayUiState.Success) base.copy(targetEntry = targetEntry) else base
    }
        .onEach { if (it !is TodayUiState.Loading) retrying.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState.Loading)

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

    /**
     * Re-derives today's plan. A retry from the error branch also flips
     * [retrying] so the retry produces a genuine state transition (Loading
     * first); an initial load and a foreground refresh stay silent.
     */
    private fun refresh() {
        retrying.value = state.value is TodayUiState.Error
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
                effects.send(TodayEffect.Celebrate)
            }
            TodayUiState.Success(
                date = today,
                // DECISIONS §5: concise date above the greeting — "FRI, SEP 25".
                dateLabel = today
                    .format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()))
                    .uppercase(Locale.getDefault()),
                greeting = greeting(),
                sections = sections,
                encouragement = encouragementFor(sections, settings.motivationalMessagesEnabled),
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
     * The Hero's state-specific encouragement line (ADR-48 Amendment Note 1 /
     * refinement item 8). ON rules, first match wins; the Settings toggle
     * removes the line entirely (the Hero then reserves no space for it):
     *
     * 1. `allComplete || isEmpty` → no line — the Hero statement / empty state
     *    already says it.
     * 2. A remaining item with `currentStreak >= 2` → "Keep your {n}-day
     *    streak going.", with `{n}` the greatest streak among `remaining`
     *    (tie → first in schedule order). This is Home's only streak
     *    reference; rows carry none (ADR-48 §5).
     * 3. Otherwise the named next item → "Next up: {title}".
     *
     * Neither form carries a progress number, so ADR-48 §1's one
     * representation still holds.
     */
    private fun encouragementFor(sections: TodaySections, motivational: Boolean): UiText? {
        if (!motivational) return null
        if (sections.allComplete || sections.isEmpty) return null
        val best = sections.remaining.maxByOrNull { it.currentStreak }
        if (best != null && best.currentStreak >= 2) {
            return UiText(R.string.today_encouragement_streak, listOf(best.currentStreak))
        }
        return UiText(
            R.string.today_encouragement_next,
            listOf(sections.remaining.first().activity.title),
        )
    }
}

/** Shared failure copy for every completion/completion-record write. */
private val CouldntSave = UiText(R.string.error_couldnt_save)
