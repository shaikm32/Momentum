package com.momentum.app.domain.usecase

import com.momentum.app.domain.engine.ActivityStats
import com.momentum.app.domain.engine.AnalyticsEngine
import com.momentum.app.domain.engine.CompletionEngine
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

/**
 * One row of the Today schedule: activity + today's completion state + streak.
 *
 * [completed] is the *derived* result of `actual >= target`
 * ([CompletionEngine.isComplete]) — never "a record exists". [completionValue]
 * is the stored actual for today (0.0 when nothing is logged) so partial
 * progress such as 5/8 survives an incomplete state.
 */
data class TodayItem(
    val activity: Activity,
    val completed: Boolean,
    val completionValue: Double,
    val currentStreak: Int,
)

/**
 * Home's two sections (DASHBOARD_SPECS §9, DECISIONS §10), derived purely
 * from completion state — never maintained as a second source of truth.
 *
 * Ordering is fixed: every remaining item precedes every completed item, and
 * the relative order inside each section is the caller's schedule order.
 * Empty sections stay present in the model; deciding whether a section is
 * *rendered* is the UI's job (§20 "do not display an empty section").
 */
data class TodaySections(
    val remaining: List<TodayItem>,
    val completed: List<TodayItem>,
) {
    /** Eligible activities for today — the Hero denominator. */
    val totalCount: Int get() = remaining.size + completed.size
    val doneCount: Int get() = completed.size

    /** completed / eligible. 0f when nothing is eligible — never a fake 100%. */
    val progress: Float get() = if (totalCount == 0) 0f else doneCount.toFloat() / totalCount

    /** No eligible activities today → the No-Activity State, not empty sections. */
    val isEmpty: Boolean get() = totalCount == 0

    /** Every eligible activity is done → Remaining today hides, Hero reads 100%. */
    val allComplete: Boolean get() = totalCount > 0 && remaining.isEmpty()
}

/** Splits today's eligible items into Remaining today / Completed today. */
fun partitionTodayItems(items: List<TodayItem>): TodaySections = TodaySections(
    remaining = items.filter { !it.completed },
    completed = items.filter { it.completed },
)

/**
 * Builds today's schedule from active activities whose rule matches [date].
 * Pure orchestration over repository snapshots — the date math stays in the
 * engine and the completion rule stays in [CompletionEngine].
 *
 * Completions carry their stored values (date -> value), so a 5/8 partial
 * flows through as 5.0 instead of being flattened to 1.0. Whether the row is
 * **completed** is decided by [CompletionEngine.isComplete] (`actual >=
 * target`), not by the mere presence of a record — a partial is retained as
 * progress but stays incomplete and therefore stays in Remaining today.
 * Streaks consume [CompletionEngine.successfulDates] so only genuinely
 * successful scheduled opportunities continue a run.
 */
class GetTodayScheduleUseCase {
    operator fun invoke(
        date: LocalDate,
        activities: List<Activity>,
        completionsByActivity: Map<Long, Map<LocalDate, Double>>,
    ): List<TodayItem> {
        return activities
            .filter { it.status == ActivityStatus.ACTIVE && RecurrenceEngine.isScheduled(it, date) }
            .map { activity ->
                val completedValues = completionsByActivity[activity.id].orEmpty()
                val scheduledToDate = RecurrenceEngine.scheduledInRange(
                    activity, activity.startDate, date,
                )
                TodayItem(
                    activity = activity,
                    completed = CompletionEngine.isComplete(activity, completedValues[date]),
                    completionValue = completedValues[date] ?: 0.0,
                    currentStreak = StreakEngine.currentStreak(
                        scheduledToDate,
                        CompletionEngine.successfulDates(activity, completedValues),
                    ),
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

/**
 * Persists a **Reach a Target** actual value for one occurrence.
 *
 * The value is stored verbatim — above-target values are valid and are never
 * clamped to the target, and a below-target value is retained as partial
 * progress. `actual <= 0` clears the record so "no progress logged" and
 * "explicitly cleared" agree on the same state.
 *
 * Completion itself is never stored: it is always re-derived from the stored
 * value by [CompletionEngine.isComplete], so editing 5 → 8 flips the activity
 * to complete and 8 → 5 flips it back, recalcating every dependent state.
 */
class SetActualValueUseCase(
    private val completions: CompletionRepository,
) {
    suspend operator fun invoke(activityId: Long, date: LocalDate, actual: Double): Double {
        val iso = date.toIso()
        return if (actual <= 0.0) {
            completions.uncomplete(activityId, iso)
            0.0
        } else {
            completions.complete(activityId, iso, actual)
            actual
        }
    }
}

data class ActivityDetailStats(
    val stats: ActivityStats,
    val trend: Trend,
    val scheduled: List<LocalDate>,
    val completed: Set<LocalDate>,
)

/** Computes detail-screen stats for one activity over rangeStart..today. */
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
