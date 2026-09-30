package com.momentum.app.presentation.screens.insights

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.engine.StreakEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DayRate(val date: LocalDate, val rate: Double, val scheduled: Int)
/**
 * [name] is the user's own category name (not UI copy); null = no category,
 * which the screen labels from resources.
 */
data class CategoryPerformance(val name: String?, val rate: Double, val completed: Int, val scheduled: Int)
data class ActivityPerformance(val activity: Activity, val rate: Double, val streak: Int)

@Immutable
data class InsightsUiState(
    val isEmpty: Boolean = true,
    val weekRate: Double = 0.0,
    val monthRate: Double = 0.0,
    val totalCompletions: Int = 0,
    val bestStreak: Int = 0,
    val bestStreakTitle: String = "",
    val last7Days: List<DayRate> = emptyList(),
    val categories: List<CategoryPerformance> = emptyList(),
    val topActivities: List<ActivityPerformance> = emptyList(),
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val completionRepository: CompletionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val state = run {
        val today = LocalDate.now()
        val from = today.minusDays(60).toIso()
        combine(
            activityRepository.observeAll(),
            completionRepository.observeInRange(from, today.toIso()),
            categoryRepository.observeAll(),
        ) { activities, completions, categories ->
            buildState(today, activities, completions, categories)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState())
    }

    private fun buildState(
        today: LocalDate,
        activities: List<Activity>,
        completions: List<com.momentum.app.domain.model.CompletionRecord>,
        categories: List<com.momentum.app.domain.model.Category>,
    ): InsightsUiState {
        val live = activities.filter {
            it.status == ActivityStatus.ACTIVE || it.status == ActivityStatus.PAUSED
        }
        if (live.isEmpty()) return InsightsUiState()
        val byActivity = completions.groupBy { it.activityId }
            .mapValues { (_, recs) -> recs.map { it.occurrenceDate.toLocalDate() }.toSet() }
        val names = categories.associate { it.id to it.name }

        fun windowRate(from: LocalDate, to: LocalDate): Pair<Double, Int> {
            var scheduled = 0
            var done = 0
            live.forEach { a ->
                val s = RecurrenceEngine.scheduledInRange(a, maxOf(from, a.startDate), to)
                val c = byActivity[a.id].orEmpty()
                scheduled += s.size
                done += s.count { it in c }
            }
            return (if (scheduled == 0) 0.0 else done.toDouble() / scheduled) to done
        }

        val (weekRate) = windowRate(today.minusDays(6), today)
        val (monthRate, monthDone) = windowRate(today.minusDays(29), today)
        val last7 = (0..6).map { back ->
            val date = today.minusDays(back.toLong())
            var scheduled = 0
            var done = 0
            live.forEach { a ->
                if (RecurrenceEngine.isScheduled(a, date)) {
                    scheduled++
                    if (date in byActivity[a.id].orEmpty()) done++
                }
            }
            DayRate(date, if (scheduled == 0) 0.0 else done.toDouble() / scheduled, scheduled)
        }.reversed()

        val catPerf = live.groupBy { it.categoryId }.map { (catId, acts) ->
            var scheduled = 0
            var done = 0
            acts.forEach { a ->
                val s = RecurrenceEngine.scheduledInRange(a, maxOf(today.minusDays(29), a.startDate), today)
                val c = byActivity[a.id].orEmpty()
                scheduled += s.size
                done += s.count { it in c }
            }
            CategoryPerformance(
                // No category, or one that was deleted while the row is
                // rendered: either way the screen falls back to its own
                // resource label. The name is user data, so it stays literal.
                name = catId?.let { names[it] },
                rate = if (scheduled == 0) 0.0 else done.toDouble() / scheduled,
                completed = done,
                scheduled = scheduled,
            )
        }.filter { it.scheduled > 0 }.sortedByDescending { it.rate }

        val top = live.map { a ->
            val s = RecurrenceEngine.scheduledInRange(a, maxOf(today.minusDays(29), a.startDate), today)
            val c = byActivity[a.id].orEmpty()
            ActivityPerformance(
                activity = a,
                rate = if (s.isEmpty()) 0.0 else s.count { it in c }.toDouble() / s.size,
                streak = StreakEngine.currentStreak(s, c),
            )
        }.filter { it.activity.let { a -> RecurrenceEngine.scheduledInRange(a, maxOf(today.minusDays(29), a.startDate), today).isNotEmpty() } }
            .sortedWith(compareByDescending<ActivityPerformance> { it.rate }.thenByDescending { it.streak })
            .take(5)

        val best = top.maxByOrNull { it.streak }
        return InsightsUiState(
            isEmpty = false,
            weekRate = weekRate,
            monthRate = monthRate,
            totalCompletions = monthDone,
            bestStreak = best?.streak ?: 0,
            bestStreakTitle = best?.activity?.title.orEmpty(),
            last7Days = last7,
            categories = catPerf,
            topActivities = top,
        )
    }
}
