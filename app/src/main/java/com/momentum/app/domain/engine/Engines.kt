package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Phase 0 recurrence stub. The real engine lands in Phase 3 and MUST be a
 * pure-Kotlin, independently unit-testable module with no Android imports.
 *
 * Contract preview: given a recurrence + window, return scheduled dates.
 * Streaks are computed over scheduled occurrences only (unscheduled days
 * never break a streak).
 */
object RecurrenceEngine {

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
            is Recurrence.SelectedWeekdays -> generateSequence(maxOf(anchor, rangeStart)) { it.plusDays(1) }
                .takeWhile { !it.isAfter(rangeEnd) }
                .filter { it.dayOfWeek in recurrence.days }
                .toList()
            is Recurrence.EveryNDays -> {
                require(recurrence.n >= 1) { "n must be >= 1" }
                generateSequence(anchor) { it.plusDays(recurrence.n.toLong()) }
                    .dropWhile { it.isBefore(rangeStart) }
                    .takeWhile { !it.isAfter(rangeEnd) }
                    .toList()
            }
            is Recurrence.EveryNWeeks -> {
                require(recurrence.n >= 1) { "n must be >= 1" }
                generateSequence(maxOf(anchor, rangeStart)) { it.plusDays(1) }
                    .takeWhile { !it.isAfter(rangeEnd) }
                    .filter { date ->
                        val weeksSinceAnchor = java.time.temporal.ChronoUnit.WEEKS.between(anchor, date)
                        weeksSinceAnchor >= 0 &&
                            weeksSinceAnchor % recurrence.n == 0L &&
                            date.dayOfWeek in recurrence.days
                    }
                    .toList()
            }
            is Recurrence.Monthly -> generateSequence(anchor) { nextMonthly(it, recurrence.dayOfMonth) }
                .dropWhile { it.isBefore(rangeStart) }
                .takeWhile { !it.isAfter(rangeEnd) }
                .toList()
        }
    }

    private fun nextMonthly(date: LocalDate, dayOfMonth: Int): LocalDate {
        val next = date.plusMonths(1)
        val safeDay = dayOfMonth.coerceIn(1, next.lengthOfMonth())
        return next.withDayOfMonth(safeDay)
    }
}

/** Placeholder so Phase 6 has a named home. */
object StreakEngine {
    /**
     * Counts current streak over [scheduled] dates (ascending) given the set of
     * completed occurrence dates. Unscheduled days are absent from [scheduled],
     * so they can never break the streak.
     */
    fun currentStreak(scheduled: List<LocalDate>, completed: Set<LocalDate>): Int {
        var streak = 0
        for (date in scheduled.asReversed()) {
            if (date in completed) streak++ else break
        }
        return streak
    }

    @Suppress("unused")
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
