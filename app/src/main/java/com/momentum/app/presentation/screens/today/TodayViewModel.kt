package com.momentum.app.presentation.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TodayUiState {
    data object Loading : TodayUiState
    data class Success(
        val date: LocalDate,
        val dateLabel: String,
        val greeting: String,
        val items: List<TodayItem>,
        val progress: Float,
        val doneCount: Int,
        val message: String,
        /** Null = follow the system animator setting (resolved in the composable). */
        val reduceMotionOverride: Boolean?,
    ) : TodayUiState
    data class Error(val message: String) : TodayUiState
}

sealed interface TodayEffect {
    data class Celebrate(val message: String) : TodayEffect
    data class ShowMessage(val message: String) : TodayEffect
}

/** Hero experience: what matters today, wired to the real repository. */
class TodayViewModel(private val container: AppContainer) : ViewModel() {

    private val refreshTick = MutableStateFlow(0)
    private val effects = Channel<TodayEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()
    private var lastProgress: Float? = null

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

    fun toggle(activityId: Long) {
        val today = LocalDate.now()
        viewModelScope.launch {
            runCatching { container.toggleCompletion(activityId, today) }
                .onFailure { effects.send(TodayEffect.ShowMessage("Couldn't save — try again.")) }
        }
    }

    private suspend fun buildState(
        today: LocalDate,
        activities: List<com.momentum.app.domain.model.Activity>,
        completions: List<com.momentum.app.domain.model.CompletionRecord>,
        settings: AppSettings,
    ): TodayUiState {
        return try {
            val byActivity = completions.groupBy { it.activityId }
                .mapValues { (_, recs) -> recs.map { it.occurrenceDate.toLocalDate() }.toSet() }
            val items = container.getTodaySchedule(today, activities, byActivity)
            val done = items.count { it.completed }
            val progress = if (items.isEmpty()) 0f else done.toFloat() / items.size
            val prev = lastProgress
            lastProgress = progress
            if (prev != null && prev < 1f && progress == 1f && items.isNotEmpty()) {
                effects.send(TodayEffect.Celebrate("All done — momentum secured."))
            }
            TodayUiState.Success(
                date = today,
                dateLabel = today.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())),
                greeting = greeting(),
                items = items,
                progress = progress,
                doneCount = done,
                message = messageFor(items.size, done, settings.motivationalMessagesEnabled),
                reduceMotionOverride = settings.reduceMotionOverride,
            )
        } catch (t: Throwable) {
            TodayUiState.Error("Couldn't load today's plan.")
        }
    }

    private fun greeting(): String = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Up late"
    }

    private fun messageFor(total: Int, done: Int, motivational: Boolean): String {
        if (total == 0) return "A fresh page — add your first activity."
        if (done == total) return "Everything complete. Rest easy."
        if (!motivational) return "$done of $total complete."
        val remaining = total - done
        return when {
            done == 0 -> if (remaining == 1) "One small win starts everything." else "Small steps, unstoppable momentum."
            remaining == 1 -> "One more — finish strong."
            done == 1 -> "First win banked. Keep going."
            else -> "You're building momentum."
        }
    }
}
