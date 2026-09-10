package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecurrenceEngineTest {

    private val anchor = LocalDate.of(2026, 1, 5) // a Monday

    @Test
    fun `daily generates every date in range`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.Daily, anchor,
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 7),
        )
        assertEquals(3, out.size)
    }

    @Test
    fun `selected weekdays skips unscheduled days`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.SelectedWeekdays(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
            anchor,
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 11),
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 7),
                LocalDate.of(2026, 1, 9),
            ),
            out,
        )
    }

    @Test
    fun `once outside range is empty`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.Once, anchor,
            LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28),
        )
        assertEquals(emptyList<LocalDate>(), out)
    }

    @Test
    fun `weekdays preset skips weekends`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.Weekdays, anchor,
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 11),
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 6),
                LocalDate.of(2026, 1, 7),
                LocalDate.of(2026, 1, 8),
                LocalDate.of(2026, 1, 9),
            ),
            out,
        )
    }

    @Test
    fun `weekly repeats on anchor weekday`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.Weekly, anchor,
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 26),
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 12),
                LocalDate.of(2026, 1, 19),
                LocalDate.of(2026, 1, 26),
            ),
            out,
        )
    }

    @Test
    fun `every N days anchors at start`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.EveryNDays(3), anchor,
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 8),
                LocalDate.of(2026, 1, 11),
                LocalDate.of(2026, 1, 14),
            ),
            out,
        )
    }

    @Test
    fun `every N weeks respects interval and days`() {
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.EveryNWeeks(2, setOf(DayOfWeek.MONDAY)),
            anchor,
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 9),
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 19),
                LocalDate.of(2026, 2, 2),
            ),
            out,
        )
    }

    @Test
    fun `monthly clamps to month length`() {
        val jan31 = LocalDate.of(2026, 1, 31)
        val out = RecurrenceEngine.occurrencesInRange(
            Recurrence.Monthly(31), jan31,
            LocalDate.of(2026, 1, 31), LocalDate.of(2026, 4, 30),
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 2, 28),
                // Anchor drifts after a clamp (Feb has no 31st); the rule keeps
                // firing monthly, which is the documented v1 semantic.
                LocalDate.of(2026, 3, 31),
                LocalDate.of(2026, 4, 30),
            ),
            out,
        )
    }

    private fun activity(
        recurrence: Recurrence = Recurrence.Daily,
        status: ActivityStatus = ActivityStatus.ACTIVE,
        startDate: LocalDate = anchor,
        endDate: LocalDate? = null,
    ) = Activity(title = "T", startDate = startDate, endDate = endDate, recurrence = recurrence, status = status)

    @Test
    fun `isScheduled gates on status and window`() {
        val daily = activity()
        assertTrue(RecurrenceEngine.isScheduled(daily, anchor))
        assertFalse(RecurrenceEngine.isScheduled(daily, anchor.minusDays(1)))
        assertFalse(RecurrenceEngine.isScheduled(daily.copy(status = ActivityStatus.PAUSED), anchor))
        assertFalse(RecurrenceEngine.isScheduled(daily.copy(status = ActivityStatus.ARCHIVED), anchor))
        val bounded = activity(endDate = anchor.plusDays(2))
        assertTrue(RecurrenceEngine.isScheduled(bounded, anchor.plusDays(2)))
        assertFalse(RecurrenceEngine.isScheduled(bounded, anchor.plusDays(3)))
        // M/W/F activity is not scheduled on Tuesday.
        val mwf = activity(Recurrence.SelectedWeekdays(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)))
        assertFalse(RecurrenceEngine.isScheduled(mwf, LocalDate.of(2026, 1, 6)))
        assertTrue(RecurrenceEngine.isScheduled(mwf, LocalDate.of(2026, 1, 7)))
    }

    @Test
    fun `once uses startDate as due date`() {
        val once = activity(Recurrence.Once)
        assertTrue(RecurrenceEngine.isScheduled(once, anchor))
        assertFalse(RecurrenceEngine.isScheduled(once, anchor.plusDays(1)))
    }

    @Test
    fun `scheduledInRange returns empty for paused`() {
        val paused = activity(status = ActivityStatus.PAUSED)
        assertEquals(
            emptyList<LocalDate>(),
            RecurrenceEngine.scheduledInRange(paused, anchor, anchor.plusDays(7)),
        )
    }
}
