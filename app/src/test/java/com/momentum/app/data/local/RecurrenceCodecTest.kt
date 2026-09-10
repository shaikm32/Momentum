package com.momentum.app.data.local

import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class RecurrenceCodecTest {

    @Test
    fun `round trips every recurrence type`() {
        val cases = listOf(
            Recurrence.Once,
            Recurrence.Daily,
            Recurrence.Weekdays,
            Recurrence.SelectedWeekdays(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY)),
            Recurrence.EveryNDays(3),
            Recurrence.Weekly,
            Recurrence.EveryNWeeks(2, setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)),
            Recurrence.Monthly(15),
        )
        cases.forEach { recurrence ->
            val decoded = RecurrenceCodec.decode(
                RecurrenceCodec.encodeType(recurrence),
                RecurrenceCodec.encodePayload(recurrence),
            )
            assertEquals(recurrence, decoded)
        }
    }

    @Test
    fun `unknown future type degrades to daily`() {
        assertEquals(Recurrence.Daily, RecurrenceCodec.decode("SOME_FUTURE_RULE", "{}"))
    }

    @Test
    fun `malformed payload falls back to defaults`() {
        assertEquals(
            Recurrence.SelectedWeekdays(setOf(DayOfWeek.MONDAY)),
            RecurrenceCodec.decode("SELECTED_WEEKDAYS", "not-json"),
        )
        assertEquals(Recurrence.EveryNDays(1), RecurrenceCodec.decode("EVERY_N_DAYS", "{}"))
    }
}
