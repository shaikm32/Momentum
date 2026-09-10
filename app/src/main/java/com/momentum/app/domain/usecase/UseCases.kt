package com.momentum.app.domain.usecase

import com.momentum.app.domain.engine.ActivityStats
import com.momentum.app.domain.engine.AnalyticsEngine
import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.engine.StreakEngine
import com.momentum.app.domain.engine.Trend
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.repository.CompletionRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val IsoDate: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun LocalDate.toIso(): String = format(IsoDate)
fun String.toLocalDate(): LocalDate = LocalDate.parse(this, IsoDate)

/** One row of the Today schedule: activity + today's completion state + streak. */
data class TodayItem(
    val activity: Activity,
    val completed: Boolean,
    val completionValue: Double,
    val currentStreak: Int,
)

/**
 * Builds today's schedule from active activities whose rule matches [date].
 * Pure orchestration over repository snapshots — the date math stays in the engine.
 */
class GetTodayScheduleUseCase {
    operator fun invoke(
        date: LocalDate,
        activities: List<Activity>,
        completionsByActivity: Map<Long, Set<LocalDate>>,
    ): List<TodayItem> {
        val iso = date.toIso()
        return activities
            .filter { it.status == ActivityStatus.ACTIVE && RecurrenceEngine.isScheduled(it, date) }
            .map { activity ->
                val completedSet = completionsByActivity[activity.id].orEmpty()
                val scheduledToDate = RecurrenceEngine.scheduledInRange(
                    activity, activity.startDate, date,
                )
                TodayItem(
                    activity = activity,
                    completed = date in completedSet,
                    completionValue = 1.0,
                    currentStreak = StreakEngine.currentStreak(scheduledToDate, completedSet),
                )
            }
            .sortedWith(compareByDescending<TodayItem> { it.activity.priority }.thenBy { it.activity.title })
    }
}

/** Toggles completion for one occurrence; re-completing is an upsert, never a duplicate. */
class ToggleCompletionUseCase(
    private val completions: CompletionRepository,
) {
    suspend operator fun invoke(activityId: Long, date: LocalDate, value: Double = 1.0): Boolean {
        val iso = date.toIso()
        return if (completions.isCompleted(activityId, iso)) {
            completions.uncomplete(activityId, iso)
            false
        } else {
            completions.complete(activityId, iso, value)
            true
        }
    }
}

data class ActivityDetailStats(
    val stats: ActivityStats,
    val trend: Trend,
    val scheduled: List<LocalDate>,
    val completed: Set<LocalDate>,
)

/** Computes detail-screen stats for one activity over [rangeStart..today]. */
class ComputeActivityStatsUseCase {
    operator fun invoke(
        activity: Activity,
        completionDates: Set<LocalDate>,
        today: LocalDate,
        rangeStart: LocalDate = today.minusDays(29),
    ): ActivityDetailStats {
        val scheduled = RecurrenceEngine.scheduledInRange(activity, rangeStart, today)
        return ActivityDetailStats(
            stats = AnalyticsEngine.activityStats(scheduled, completionDates),
            trend = AnalyticsEngine.trend(scheduled, completionDates, today),
            scheduled = scheduled,
            completed = completionDates,
        )
    }
}
