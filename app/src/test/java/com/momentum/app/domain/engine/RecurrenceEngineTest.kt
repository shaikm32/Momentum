package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
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
}
