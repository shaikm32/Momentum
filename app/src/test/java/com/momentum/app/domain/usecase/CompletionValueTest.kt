package com.momentum.app.domain.usecase

import com.momentum.app.domain.engine.AnalyticsEngine
import com.momentum.app.domain.engine.CompletionEngine
import com.momentum.app.domain.engine.StreakEngine
import com.momentum.app.domain.model.Activity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Stored completion values must flow through instead of being flattened to
 * 1.0, and — per DASHBOARD_SPECS §10.2 — the *value* decides completion:
 * `actual >= target` completes, anything below stays incomplete while the
 * partial progress is still reported.
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
    fun `target with partial completion reports stored value and stays incomplete`() {
        val items = useCase(
            monday,
            listOf(water(target = 8.0, unit = "glasses")),
            mapOf(3L to mapOf(monday to 5.0)),
        )
        // Progress 5/8 is retained, but 5 < 8 is NOT a completion.
        assertFalse(items.single().completed)
        assertEquals(5.0, items.single().completionValue, 0.0)
    }

    @Test
    fun `above target completes with the value left intact`() {
        val items = useCase(
            monday,
            listOf(water(target = 8.0, unit = "glasses")),
            mapOf(3L to mapOf(monday to 10.0)),
        )
        assertTrue(items.single().completed)
        assertEquals(10.0, items.single().completionValue, 0.0)
    }

    @Test
    fun `editing 5 to 8 completes and 8 to 5 reverses with the partial retained`() {
        val activity = water(target = 8.0, unit = "glasses")
        fun stateAt(value: Double?) = useCase(
            monday,
            listOf(activity),
            if (value == null) emptyMap() else mapOf(3L to mapOf(monday to value)),
        ).single()

        assertFalse(stateAt(5.0).completed) // 5 → incomplete
        assertTrue(stateAt(8.0).completed) // 5 → 8 completes
        assertFalse(stateAt(5.0).completed) // 8 → 5 incomplete again
        assertEquals(5.0, stateAt(5.0).completionValue, 0.0) // partial survives
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
    fun `analytics counts successful opportunities`() {
        val scheduled = listOf(monday, monday.plusDays(1))
        val stats = AnalyticsEngine.activityStats(scheduled, setOf(monday))
        assertEquals(0.5, stats.completionRate, 1e-9)
        assertEquals(0.5, stats.consistencyScore!!, 1e-9)
        assertEquals(1, stats.completedCount)
        assertEquals(1, stats.missedCount)
    }

    @Test
    fun `streak follows successful opportunities rather than raw records`() {
        val activity = water(target = 8.0, unit = "glasses")
        val scheduled = listOf(monday, monday.plusDays(1), monday.plusDays(2))
        val logged = mapOf(
            monday to 8.0, // success
            monday.plusDays(1) to 5.0, // below target → unsuccessful opportunity
            monday.plusDays(2) to 8.0, // success
        )
        val successful = CompletionEngine.successfulDates(activity, logged)
        assertEquals(setOf(monday, monday.plusDays(2)), successful)
        // Tuesday's partial breaks the run; Wednesday starts a fresh one.
        assertEquals(1, StreakEngine.currentStreak(scheduled, successful))
        assertEquals(1, StreakEngine.longestStreak(scheduled, successful))
    }
}
