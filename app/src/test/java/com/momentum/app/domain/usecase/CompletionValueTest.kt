package com.momentum.app.domain.usecase

import com.momentum.app.domain.engine.AnalyticsEngine
import com.momentum.app.domain.engine.StreakEngine
import com.momentum.app.domain.model.Activity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * P2#2: stored completion values must flow through instead of being flattened
 * to 1.0. Presence in the completion map still defines "completed" (and hence
 * streak/analytics membership) — the value only describes *how much*.
 */
class CompletionValueTest {

    private val monday = LocalDate.of(2026, 1, 5)
    private val useCase = GetTodayScheduleUseCase()

    private fun water(target: Double = 1.0, unit: String = "") = Activity(
        id = 3,
        title = "Water",
        startDate = monday,
        targetPerOccurrence = target,
        unit = unit,
    )

    @Test
    fun `no target completed reports 1_0`() {
        val items = useCase(monday, listOf(water()), mapOf(3L to mapOf(monday to 1.0)))
        assertTrue(items.single().completed)
        assertEquals(1.0, items.single().completionValue, 0.0)
    }

    @Test
    fun `target with full completion reports target value`() {
        val items = useCase(
            monday,
            listOf(water(target = 8.0, unit = "glasses")),
            mapOf(3L to mapOf(monday to 8.0)),
        )
        assertTrue(items.single().completed)
        assertEquals(8.0, items.single().completionValue, 0.0)
    }

    @Test
    fun `target with partial completion reports stored value but still completed`() {
        val items = useCase(
            monday,
            listOf(water(target = 8.0, unit = "glasses")),
            mapOf(3L to mapOf(monday to 5.0)),
        )
        assertTrue(items.single().completed)
        assertEquals(5.0, items.single().completionValue, 0.0)
    }

    @Test
    fun `zero state reports 0_0 and not completed`() {
        val items = useCase(
            monday,
            listOf(water(target = 8.0, unit = "glasses")),
            emptyMap(),
        )
        assertFalse(items.single().completed)
        assertEquals(0.0, items.single().completionValue, 0.0)
    }

    @Test
    fun `analytics counts partial completions`() {
        val scheduled = listOf(monday, monday.plusDays(1))
        val stats = AnalyticsEngine.activityStats(scheduled, setOf(monday))
        assertEquals(0.5, stats.completionRate, 1e-9)
        assertEquals(0.5, stats.consistencyScore!!, 1e-9)
        assertEquals(1, stats.completedCount)
        assertEquals(1, stats.missedCount)
    }

    @Test
    fun `streak counts partial completions`() {
        // A logged 5/8 keeps the streak exactly like a full completion.
        val scheduled = listOf(monday, monday.plusDays(1), monday.plusDays(2))
        val completed = setOf(monday, monday.plusDays(1))
        // Most recent day (Wed) missed -> current streak 0, but the two logged
        // days form a longest streak of 2 regardless of their values.
        assertEquals(0, StreakEngine.currentStreak(scheduled, completed))
        assertEquals(2, StreakEngine.longestStreak(scheduled, completed))
        // And when the latest day is also logged, the current streak counts it.
        assertEquals(
            3,
            StreakEngine.currentStreak(scheduled, completed + monday.plusDays(2)),
        )
    }
}
