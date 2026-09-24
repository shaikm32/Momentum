package com.momentum.app.domain.usecase

import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.engine.StreakEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.WeekStart
import java.time.DayOfWeek
import java.time.LocalDate

/** Per-day state for the Habit Details weekly completion view (Decision 19). */
enum class HabitDayState { COMPLETED, INCOMPLETE, NOT_SCHEDULED }

/** One day inside the weekly view. */
data class HabitDay(
    val date: LocalDate,
    val state: HabitDayState,
)

/**
 * Seven consecutive days of the week containing [today], ordered by
 * [weekStart] (Monday or Sunday first). Future scheduled days inside the
 * window are included so the denominator matches "scheduled this week".
 */
data class HabitWeekCompletion(
    val days: List<HabitDay>,
    /** Days with a completion record. */
    val completedCount: Int,
    /** Scheduled days in the window (status-independent rule × start/end). */
    val scheduledCount: Int,
) {
    /** `completed / scheduled`; 0.0 when nothing was scheduled. */
    val fraction: Double
        get() = if (scheduledCount == 0) 0.0 else completedCount.toDouble() / scheduledCount
}

/** Habit Details overview: weekly completion + streaks (Decisions 19–20). */
data class HabitOverview(
    val week: HabitWeekCompletion,
    val currentStreak: Int,
    val longestStreak: Int,
)

/**
 * Computes Habit Details overview for one habit.
 *
 * Pure orchestration: week window + schedule classification + [StreakEngine].
 * Schedule for the week and streaks is computed **status-independent** (rule
 * + start/end window only) so a paused habit keeps its history visible;
 * callers filter which habits appear (list shows ACTIVE + PAUSED only).
 */
class ComputeHabitOverviewUseCase {
    operator fun invoke(
        activity: Activity,
        completionDates: Set<LocalDate>,
        today: LocalDate,
        weekStart: WeekStart = WeekStart.MONDAY,
    ): HabitOverview {
        val weekDates = weekWindow(today, weekStart)
        val scheduledThisWeek = scheduleInRange(activity, weekDates.first(), weekDates.last())
        val days = weekDates.map { date ->
            val state = when {
                date !in scheduledThisWeek -> HabitDayState.NOT_SCHEDULED
                date in completionDates -> HabitDayState.COMPLETED
                else -> HabitDayState.INCOMPLETE
            }
            HabitDay(date, state)
        }
        val scheduledCount = days.count { it.state != HabitDayState.NOT_SCHEDULED }
        val completedCount = days.count { it.state == HabitDayState.COMPLETED }

        // Streaks from startDate..today, status-independent schedule so a
        // paused habit retains its longest run; current streak follows the
        // existing StreakEngine semantics (0 until the latest scheduled day
        // is completed).
        val streakScheduled = scheduleInRange(activity, activity.startDate, today)
            .filter { !it.isAfter(today) }
        return HabitOverview(
            week = HabitWeekCompletion(
                days = days,
                completedCount = completedCount,
                scheduledCount = scheduledCount,
            ),
            currentStreak = StreakEngine.currentStreak(streakScheduled, completionDates),
            longestStreak = StreakEngine.longestStreak(streakScheduled, completionDates),
        )
    }

    /**
     * Rule × start/end window without the ACTIVE status gate used by
     * [RecurrenceEngine.scheduledInRange].
     */
    private fun scheduleInRange(
        activity: Activity,
        rangeStart: LocalDate,
        rangeEnd: LocalDate,
    ): List<LocalDate> {
        if (rangeEnd.isBefore(rangeStart)) return emptyList()
        val from = maxOf(rangeStart, activity.startDate)
        val to = activity.endDate?.let { minOf(rangeEnd, it) } ?: rangeEnd
        if (to.isBefore(from)) return emptyList()
        return RecurrenceEngine.occurrencesInRange(activity.recurrence, activity.startDate, from, to)
    }

    private fun weekWindow(today: LocalDate, weekStart: WeekStart): List<LocalDate> {
        val firstDay: DayOfWeek = when (weekStart) {
            WeekStart.MONDAY -> DayOfWeek.MONDAY
            WeekStart.SUNDAY -> DayOfWeek.SUNDAY
        }
        val daysSince = (today.dayOfWeek.value - firstDay.value + 7) % 7
        val start = today.minusDays(daysSince.toLong())
        return (0L..6L).map { start.plusDays(it) }
    }
}
