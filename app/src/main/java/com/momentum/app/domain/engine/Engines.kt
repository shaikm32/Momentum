package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Pure-Kotlin recurrence engine (zero Android imports).
 *
 * Rules:
 * - All math is [LocalDate]-only, so DST shifts can never move an occurrence.
 * - [anchor] is the activity's startDate; occurrences are computed from it,
 *   never materialized as DB rows (ADR-06).
 * - Callers apply start/end gating via [isScheduled]; [occurrencesInRange]
 *   intentionally computes the raw rule so tests stay focused.
 */
object RecurrenceEngine {

    private val MondayToFriday = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
    )

    fun occurrencesInRange(
        recurrence: Recurrence,
        anchor: LocalDate,
        rangeStart: LocalDate,
        rangeEnd: LocalDate,
    ): List<LocalDate> {
        require(!rangeEnd.isBefore(rangeStart)) { "rangeEnd must be >= rangeStart" }
        return when (recurrence) {
            is Recurrence.Once ->
                if (!anchor.isBefore(rangeStart) && !anchor.isAfter(rangeEnd)) listOf(anchor)
                else emptyList()
            is Recurrence.Daily -> generateSequence(maxOf(anchor, rangeStart)) { it.plusDays(1) }
                .takeWhile { !it.isAfter(rangeEnd) }
                .toList()
            is Recurrence.Weekdays -> generateSequence(maxOf(anchor, rangeStart)) { it.plusDays(1) }
                .takeWhile { !it.isAfter(rangeEnd) }
                .filter { it.dayOfWeek in MondayToFriday }
                .toList()
            is Recurrence.SelectedWeekdays -> weekdaysInRange(anchor, recurrence.days, rangeStart, rangeEnd)
            is Recurrence.EveryNDays -> {
                generateSequence(anchor) { it.plusDays(recurrence.n.toLong()) }
                    .dropWhile { it.isBefore(rangeStart) }
                    .takeWhile { !it.isAfter(rangeEnd) }
                    .toList()
            }
            is Recurrence.Weekly -> everyNWeeksInRange(anchor, 1, setOf(anchor.dayOfWeek), rangeStart, rangeEnd)
            is Recurrence.EveryNWeeks ->
                everyNWeeksInRange(anchor, recurrence.n, recurrence.days, rangeStart, rangeEnd)
            is Recurrence.Monthly -> generateSequence(anchor) { nextMonthly(it, recurrence.dayOfMonth) }
                .dropWhile { it.isBefore(rangeStart) }
                .takeWhile { !it.isAfter(rangeEnd) }
                .toList()
        }
    }

    /**
     * Full scheduling predicate: status × start/end window × recurrence rule.
     * PAUSED/ARCHIVED activities are never scheduled; ONCE uses startDate as due date.
     */
    fun isScheduled(activity: Activity, date: LocalDate): Boolean {
        if (activity.status != ActivityStatus.ACTIVE) return false
        if (date.isBefore(activity.startDate)) return false
        val end = activity.endDate
        if (end != null && date.isAfter(end)) return false
        return occurrencesInRange(activity.recurrence, activity.startDate, date, date).isNotEmpty()
    }

    /**
     * Scheduled occurrences of [activity] inside rangeStart..rangeEnd,
     * with start/end window applied. ARCHIVED/PAUSED ⇒ empty.
     */
    fun scheduledInRange(activity: Activity, rangeStart: LocalDate, rangeEnd: LocalDate): List<LocalDate> {
        require(!rangeEnd.isBefore(rangeStart)) { "rangeEnd must be >= rangeStart" }
        if (activity.status != ActivityStatus.ACTIVE) return emptyList()
        val from = maxOf(rangeStart, activity.startDate)
        val to = activity.endDate?.let { minOf(rangeEnd, it) } ?: rangeEnd
        if (to.isBefore(from)) return emptyList()
        return occurrencesInRange(activity.recurrence, activity.startDate, from, to)
    }

    private fun weekdaysInRange(
        anchor: LocalDate,
        days: Set<DayOfWeek>,
        rangeStart: LocalDate,
        rangeEnd: LocalDate,
    ): List<LocalDate> = generateSequence(maxOf(anchor, rangeStart)) { it.plusDays(1) }
        .takeWhile { !it.isAfter(rangeEnd) }
        .filter { it.dayOfWeek in days }
        .toList()

    private fun everyNWeeksInRange(
        anchor: LocalDate,
        n: Int,
        days: Set<DayOfWeek>,
        rangeStart: LocalDate,
        rangeEnd: LocalDate,
    ): List<LocalDate> = generateSequence(maxOf(anchor, rangeStart)) { it.plusDays(1) }
        .takeWhile { !it.isAfter(rangeEnd) }
        .filter { date ->
            val weeksSinceAnchor = ChronoUnit.WEEKS.between(anchor, date)
            weeksSinceAnchor >= 0 &&
                weeksSinceAnchor % n == 0L &&
                date.dayOfWeek in days
        }
        .toList()

    private fun nextMonthly(date: LocalDate, dayOfMonth: Int): LocalDate {
        val next = date.plusMonths(1)
        return next.withDayOfMonth(dayOfMonth.coerceIn(1, next.lengthOfMonth()))
    }
}

/** Streaks over **scheduled** occurrences only — unscheduled days never break a streak. */
object StreakEngine {
    /**
     * Counts the current streak ending at the most recent scheduled date.
     * Pass only occurrences with date <= today; future dates must be excluded
     * by the caller so an upcoming occurrence never breaks the streak.
     */
    fun currentStreak(scheduled: List<LocalDate>, completed: Set<LocalDate>): Int {
        var streak = 0
        for (date in scheduled.asReversed()) {
            if (date in completed) streak++ else break
        }
        return streak
    }

    fun longestStreak(scheduled: List<LocalDate>, completed: Set<LocalDate>): Int {
        var best = 0
        var run = 0
        for (date in scheduled) {
            if (date in completed) {
                run++
                if (run > best) best = run
            } else {
                run = 0
            }
        }
        return best
    }

    /** Dates that were scheduled but never completed (for "missed" stats). */
    fun missed(scheduled: List<LocalDate>, completed: Set<LocalDate>): List<LocalDate> =
        scheduled.filter { it !in completed }
}

enum class Trend { IMPROVING, STEADY, DECLINING, INSUFFICIENT_DATA }

data class ActivityStats(
    val scheduledCount: Int,
    val completedCount: Int,
    val completionRate: Double,
    val currentStreak: Int,
    val longestStreak: Int,
    val missedCount: Int,
    /** % of scheduled occurrences hit; null when nothing was scheduled. */
    val consistencyScore: Double?,
)

/**
 * Pure-Kotlin analytics engine (zero Android imports).
 * Inputs are date lists + completion sets — no DB, independently testable.
 */
object AnalyticsEngine {

    fun activityStats(scheduled: List<LocalDate>, completed: Set<LocalDate>): ActivityStats {
        val done = scheduled.count { it in completed }
        val rate = if (scheduled.isEmpty()) 0.0 else done.toDouble() / scheduled.size
        return ActivityStats(
            scheduledCount = scheduled.size,
            completedCount = done,
            completionRate = rate,
            currentStreak = StreakEngine.currentStreak(scheduled, completed),
            longestStreak = StreakEngine.longestStreak(scheduled, completed),
            missedCount = scheduled.size - done,
            consistencyScore = if (scheduled.isEmpty()) null else rate,
        )
    }

    /**
     * Week-over-week trend: compares completion rate of the most recent
     * [windowDays] against the preceding [windowDays]. Needs at least one
     * scheduled occurrence in each window, else INSUFFICIENT_DATA.
     */
    fun trend(
        scheduled: List<LocalDate>,
        completed: Set<LocalDate>,
        today: LocalDate,
        windowDays: Long = 7,
    ): Trend {
        require(windowDays >= 1) { "windowDays must be >= 1" }
        val currentStart = today.minusDays(windowDays - 1)
        val previousStart = currentStart.minusDays(windowDays)
        val previousEnd = currentStart.minusDays(1)
        val current = scheduled.filter { !it.isBefore(currentStart) && !it.isAfter(today) }
        val previous = scheduled.filter { !it.isBefore(previousStart) && !it.isAfter(previousEnd) }
        if (current.isEmpty() || previous.isEmpty()) return Trend.INSUFFICIENT_DATA
        val cr = current.count { it in completed }.toDouble() / current.size
        val pr = previous.count { it in completed }.toDouble() / previous.size
        val delta = cr - pr
        return when {
            delta > 0.05 -> Trend.IMPROVING
            delta < -0.05 -> Trend.DECLINING
            else -> Trend.STEADY
        }
    }
}
