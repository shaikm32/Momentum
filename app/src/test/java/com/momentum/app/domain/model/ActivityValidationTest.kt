package com.momentum.app.domain.model

import java.time.LocalDate
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Constructor invariants of the [Activity] domain model
 * (`app/src/main/java/com/momentum/app/domain/model/Activity.kt`).
 *
 * Pure Kotlin, no Android/Room types, no clock: the model rejects invalid
 * field values eagerly in its `init` block, so every rule can be pinned
 * deterministically here.
 */
class ActivityValidationTest {

    private val start = LocalDate.of(2026, 1, 5)

    private fun activity(
        title: String = "Read",
        priority: Int = 0,
        target: Double = 1.0,
        endDate: LocalDate? = null,
        dueTime: String? = null,
    ) = Activity(
        title = title,
        startDate = start,
        priority = priority,
        targetPerOccurrence = target,
        endDate = endDate,
        dueTime = dueTime,
    )

    @Test
    fun `blank title is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { activity(title = "") }
        assertThrows(IllegalArgumentException::class.java) { activity(title = "   ") }
    }

    @Test
    fun `title is capped at 120 chars`() {
        val atLimit = "a".repeat(120)
        assertConstructs(atLimit)
        assertThrows(IllegalArgumentException::class.java) { activity(title = "a".repeat(121)) }
    }

    @Test
    fun `priority must stay within 0 to 3`() {
        listOf(0, 1, 2, 3).forEach { accepted ->
            assertConstructs(priority = accepted)
        }
        assertThrows(IllegalArgumentException::class.java) { activity(priority = -1) }
        assertThrows(IllegalArgumentException::class.java) { activity(priority = 4) }
    }

    @Test
    fun `target must be greater than zero`() {
        assertThrows(IllegalArgumentException::class.java) { activity(target = 0.0) }
        assertThrows(IllegalArgumentException::class.java) { activity(target = -1.0) }
    }

    @Test
    fun `endDate may equal startDate but never precede it`() {
        assertConstructs(endDate = start)
        assertThrows(IllegalArgumentException::class.java) { activity(endDate = start.minusDays(1)) }
    }

    @Test
    fun `dueTime must be a zero padded 24 hour clock time`() {
        listOf("00:00", "09:30", "23:59").forEach { accepted ->
            assertConstructs(dueTime = accepted)
        }
        listOf("9:30", "09:5", "09:30:00", " 09:30", "0930", "noon").forEach { rejected ->
            assertThrows(IllegalArgumentException::class.java) { activity(dueTime = rejected) }
        }
    }

    @Test
    fun `dueTime hour and minute must be in range`() {
        assertThrows(IllegalArgumentException::class.java) { activity(dueTime = "24:00") }
        assertThrows(IllegalArgumentException::class.java) { activity(dueTime = "12:60") }
    }

    @Test
    fun `null dueTime is accepted`() {
        assertConstructs(dueTime = null)
    }

    /** Asserts the given field combination constructs without throwing. */
    private fun assertConstructs(
        title: String = "Read",
        priority: Int = 0,
        endDate: LocalDate? = null,
        dueTime: String? = null,
    ) {
        activity(title = title, priority = priority, endDate = endDate, dueTime = dueTime)
    }
}
