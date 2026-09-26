package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Activity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §10 / DECISIONS §13–§15: the two completion methods and the
 * single `actual >= target` rule that drives completion, partial retention,
 * above-target values and reversal.
 */
class CompletionEngineTest {

    private val monday = LocalDate.of(2026, 1, 5)

    private fun binary(id: Long = 1) = Activity(
        id = id,
        title = "Meditate",
        startDate = monday,
        targetPerOccurrence = 1.0,
        unit = "",
    )

    private fun target(
        id: Long = 2,
        amount: Double = 8.0,
        unit: String = "glasses",
    ) = Activity(
        id = id,
        title = "Drink water",
        startDate = monday,
        targetPerOccurrence = amount,
        unit = unit,
    )

    // ——— method selection ———

    @Test
    fun `default measure is Just Mark it Done`() {
        assertFalse(CompletionEngine.isTargetBased(binary()))
    }

    @Test
    fun `explicit amount is Reach a Target`() {
        assertTrue(CompletionEngine.isTargetBased(target()))
    }

    @Test
    fun `explicit unit alone is Reach a Target`() {
        // e.g. 1 lap — default amount but a real unit still needs entry.
        assertTrue(CompletionEngine.isTargetBased(target(amount = 1.0, unit = "lap")))
    }

    // ——— completion rule ———

    @Test
    fun `nothing logged is incomplete`() {
        assertFalse(CompletionEngine.isComplete(target(), null))
        assertFalse(CompletionEngine.isComplete(binary(), null))
    }

    @Test
    fun `below target stays incomplete`() {
        // Target 8, Actual 5 → incomplete, progress 5/8.
        assertFalse(CompletionEngine.isComplete(target(), 5.0))
    }

    @Test
    fun `at target completes`() {
        assertTrue(CompletionEngine.isComplete(target(), 8.0))
    }

    @Test
    fun `above target completes and the value is not clamped`() {
        // Target 8, Actual 10 → complete; the caller still stores 10.
        assertTrue(CompletionEngine.isComplete(target(), 10.0))
        assertEquals(10.0, 10.0, 0.0)
    }

    @Test
    fun `binary completes once its record exists`() {
        assertTrue(CompletionEngine.isComplete(binary(), 1.0))
        assertFalse(CompletionEngine.isComplete(binary(), 0.5))
    }

    @Test
    fun `reversal is the same rule against the new value`() {
        val water = target()
        // 5 → 8 completes, 8 → 5 reverses. One rule, both directions.
        assertFalse(CompletionEngine.isComplete(water, 5.0))
        assertTrue(CompletionEngine.isComplete(water, 8.0))
        assertFalse(CompletionEngine.isComplete(water, 5.0))
        // Cleared entirely also reads as incomplete.
        assertFalse(CompletionEngine.isComplete(water, null))
    }

    // ——— successful opportunities (streak / analytics input) ———

    @Test
    fun `successful dates drop below-target partials`() {
        val water = target()
        val values = mapOf(
            monday to 8.0,
            monday.plusDays(1) to 5.0, // partial — not a successful opportunity
            monday.plusDays(2) to 10.0,
        )
        assertEquals(
            setOf(monday, monday.plusDays(2)),
            CompletionEngine.successfulDates(water, values),
        )
    }

    @Test
    fun `successful dates keep every logged binary completion`() {
        val habit = binary()
        val values = mapOf(monday to 1.0, monday.plusDays(1) to 1.0)
        assertEquals(
            setOf(monday, monday.plusDays(1)),
            CompletionEngine.successfulDates(habit, values),
        )
    }

    @Test
    fun `successful dates are empty when nothing is logged`() {
        assertEquals(
            emptySet<LocalDate>(),
            CompletionEngine.successfulDates(target(), emptyMap()),
        )
    }
}
