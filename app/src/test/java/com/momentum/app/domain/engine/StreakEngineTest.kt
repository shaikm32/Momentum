package com.momentum.app.domain.engine

import java.time.LocalDate
import org.junit.Assert.assertEquals
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
}
