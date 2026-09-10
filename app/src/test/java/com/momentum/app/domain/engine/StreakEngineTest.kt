package com.momentum.app.domain.engine

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StreakEngineTest {

    @Test
    fun `unscheduled days never break streak`() {
        // Activity scheduled Mon/Wed/Fri only; Tue/Thu/Sun absent from schedule.
        val scheduled = listOf(
            LocalDate.of(2026, 1, 5), // Mon done
            LocalDate.of(2026, 1, 7), // Wed done
            LocalDate.of(2026, 1, 9), // Fri done
        )
        val completed = scheduled.toSet()
        assertEquals(3, StreakEngine.currentStreak(scheduled, completed))
    }

    @Test
    fun `missing scheduled occurrence breaks current streak`() {
        val scheduled = listOf(
            LocalDate.of(2026, 1, 5),
            LocalDate.of(2026, 1, 6),
            LocalDate.of(2026, 1, 7),
        )
        val completed = setOf(
            LocalDate.of(2026, 1, 5),
            LocalDate.of(2026, 1, 6),
            // Jan 7 missed
        )
        assertEquals(0, StreakEngine.currentStreak(scheduled, completed))
        assertEquals(2, StreakEngine.longestStreak(scheduled, completed))
    }

    @Test
    fun `longest streak finds best run`() {
        val scheduled = (5..12).map { LocalDate.of(2026, 1, it) }
        val completed = setOf(
            LocalDate.of(2026, 1, 5),
            LocalDate.of(2026, 1, 6),
            // 7 missed
            LocalDate.of(2026, 1, 8),
            LocalDate.of(2026, 1, 9),
            LocalDate.of(2026, 1, 10),
            // 11, 12 missed
        )
        assertEquals(0, StreakEngine.currentStreak(scheduled, completed))
        assertEquals(3, StreakEngine.longestStreak(scheduled, completed))
        assertEquals(
            listOf(LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 12)),
            StreakEngine.missed(scheduled, completed),
        )
    }

    @Test
    fun `empty schedule yields zeros`() {
        assertEquals(0, StreakEngine.currentStreak(emptyList(), emptySet()))
        assertEquals(0, StreakEngine.longestStreak(emptyList(), emptySet()))
    }
}

class AnalyticsEngineTest {

    @Test
    fun `activity stats combine streaks and rate`() {
        val scheduled = (5..9).map { LocalDate.of(2026, 1, it) } // 5 days
        val completed = setOf(
            LocalDate.of(2026, 1, 5),
            LocalDate.of(2026, 1, 6),
            LocalDate.of(2026, 1, 7),
            // 8 missed
            LocalDate.of(2026, 1, 9),
        )
        val stats = AnalyticsEngine.activityStats(scheduled, completed)
        assertEquals(5, stats.scheduledCount)
        assertEquals(4, stats.completedCount)
        assertEquals(1, stats.missedCount)
        assertEquals(0.8, stats.completionRate, 1e-9)
        assertEquals(1, stats.currentStreak)
        assertEquals(3, stats.longestStreak)
        assertEquals(0.8, stats.consistencyScore!!, 1e-9)
    }

    @Test
    fun `empty schedule has null consistency`() {
        val stats = AnalyticsEngine.activityStats(emptyList(), emptySet())
        assertEquals(0.0, stats.completionRate, 1e-9)
        assertNull(stats.consistencyScore)
    }

    @Test
    fun `trend compares recent window to previous`() {
        val today = LocalDate.of(2026, 1, 14)
        // Previous week (Jan 7–13): 1/7 done. Current week (Jan 8–14 below overlaps;
        // use explicit dates so windows are exact.
        val scheduled = (1..14).map { LocalDate.of(2026, 1, it) }
        val completed = setOf(
            LocalDate.of(2026, 1, 1), // previous window only
            LocalDate.of(2026, 1, 8),
            LocalDate.of(2026, 1, 9),
            LocalDate.of(2026, 1, 10),
            LocalDate.of(2026, 1, 11),
            LocalDate.of(2026, 1, 12),
            LocalDate.of(2026, 1, 13),
            LocalDate.of(2026, 1, 14), // current window 7/7
        )
        // Previous window Jan 1–7: 1/7; current Jan 8–14: 7/7 → improving.
        assertEquals(Trend.IMPROVING, AnalyticsEngine.trend(scheduled, completed, today, 7))

        val declining = setOf(LocalDate.of(2026, 1, 1)) + (2..7).map { LocalDate.of(2026, 1, it) }
        // Previous 7/7, current 0/7 → declining.
        assertEquals(Trend.DECLINING, AnalyticsEngine.trend(scheduled, declining, today, 7))
    }

    @Test
    fun `trend needs both windows scheduled`() {
        val today = LocalDate.of(2026, 1, 14)
        val scheduled = listOf(today)
        assertEquals(
            Trend.INSUFFICIENT_DATA,
            AnalyticsEngine.trend(scheduled, setOf(today), today, 7),
        )
    }
}
