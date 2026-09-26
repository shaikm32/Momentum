package com.momentum.app.presentation.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.TodaySections
import com.momentum.app.domain.usecase.partitionTodayItems
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface TodayUiState {
    data object Loading : TodayUiState
    /**
     * Derived view of today (DASHBOARD_SPECS §22): eligibility → completion →
     * sections → Hero progress. [sections] is the only completion-derived
     * state the UI reads; there is no second completion flag in Compose.
     */
    data class Success(
        val date: LocalDate,
        val dateLabel: String,
        val greeting: String,
        val sections: TodaySections,
        val message: String,
        /** Null = follow the system animator setting (resolved in the composable). */
        val reduceMotionOverride: Boolean?,
    ) : TodayUiState {
        val remaining: List<TodayItem> get() = sections.remaining
        val completed: List<TodayItem> get() = sections.completed
        val progress: Float get() = sections.progress
        val doneCount: Int get() = sections.doneCount
        val totalCount: Int get() = sections.totalCount
        val isEmpty: Boolean get() = sections.isEmpty
        val allComplete: Boolean get() = sections.allComplete
    }
    data class Error(val message: String) : TodayUiState
}

sealed interface TodayEffect {
    data class Celebrate(val message: String) : TodayEffect
    data class ShowMessage(val message: String) : TodayEffect
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
class TodayViewModel(private val container: AppContainer) : ViewModel() {

    private val refreshTick = MutableStateFlow(0)
    private val effects = Channel<TodayEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private var lastProgress: Float? = null

    private val _targetEntry = MutableStateFlow<TargetEntryState?>(null)
    val targetEntry: StateFlow<TargetEntryState?> = _targetEntry.asStateFlow()

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = refreshTick.flatMapLatest {
        val today = LocalDate.now()
        val from = today.minusDays(730).toIso()
        combine(
            container.activityRepository.observeByStatus(listOf(ActivityStatus.ACTIVE)),
            container.completionRepository.observeInRange(from, today.toIso()),
            container.settingsRepository.observe(),
        ) { activities, completions, settings ->
            buildState(today, activities, completions, settings)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState.Loading)

    fun refresh() {
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
    fun onCompletionTap(activityId: Long) {
        val today = LocalDate.now()
        viewModelScope.launch {
            val activity = runCatching { container.activityRepository.getById(activityId) }.getOrNull()
            if (activity == null) {
                effects.send(TodayEffect.ShowMessage("Couldn't save — try again."))
                return@launch
            }
            if (!CompletionEngine.isTargetBased(activity)) {
                runCatching { container.toggleCompletion(activityId, today, activity.targetPerOccurrence) }
                    .onFailure { effects.send(TodayEffect.ShowMessage("Couldn't save — try again.")) }
                return@launch
            }
            val iso = today.toIso()
            val stored = runCatching {
                container.completionRepository.recordsForActivity(activityId)
            }.getOrNull()?.firstOrNull { it.occurrenceDate == iso }?.value ?: 0.0
            _targetEntry.value = TargetEntryState(
                activityId = activityId,
                title = activity.title,
                target = activity.targetPerOccurrence,
                unit = activity.unit,
                currentActual = stored,
                completed = CompletionEngine.isComplete(activity, stored),
            )
        }
    }

    /**
     * Persists the entered actual value. The record (not a boolean flag) is
     * the source of truth, so 5 → 8 completes and 8 → 5 reverses, with the
     * partial value retained either way.
     */
    fun saveTargetEntry(activityId: Long, actual: Double) {
        viewModelScope.launch {
            runCatching { container.setActualValue(activityId, LocalDate.now(), actual) }
                .onSuccess { _targetEntry.value = null }
                .onFailure { effects.send(TodayEffect.ShowMessage("Couldn't save — try again.")) }
        }
    }

    /** Explicit reversal from the dialog: drop today's record entirely. */
    fun clearTargetEntry(activityId: Long) = saveTargetEntry(activityId, 0.0)

    fun dismissTargetEntry() {
        _targetEntry.value = null
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
                container.getTodaySchedule(today, activities, byActivity),
            )
            val progress = sections.progress
            val prev = lastProgress
            lastProgress = progress
            if (prev != null && prev < 1f && progress == 1f && sections.totalCount > 0) {
                effects.send(TodayEffect.Celebrate("All done — momentum secured."))
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
            TodayUiState.Error("Couldn't load today's plan.")
        }
    }

    /** DECISIONS §6: four greetings, chosen from the device's local time. */
    private fun greeting(): String = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }

    /**
     * The Hero's short contextual line (DECISIONS §9). It is never a random
     * quote — it always describes the user's current state. When motivational
     * messages are switched off the line is dropped entirely, because the
     * Hero's completion count already states progress and repeating it would
     * only add noise.
     */
    private fun messageFor(total: Int, done: Int, motivational: Boolean): String {
        if (total == 0) return "A fresh page — add your first activity."
        if (done == total) return "Everything complete. Rest easy."
        if (!motivational) return ""
        val remaining = total - done
        return when {
            done == 0 -> if (remaining == 1) "One small win starts everything." else "Small steps, unstoppable momentum."
            remaining == 1 -> "One more — finish strong."
            done == 1 -> "First win banked. Keep going."
            else -> "You're building momentum."
        }
    }
}
