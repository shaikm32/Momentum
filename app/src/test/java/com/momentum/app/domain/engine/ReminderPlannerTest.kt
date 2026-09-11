package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P0#2 regression: reminder fire times must respect the recurrence rule.
 *
 * Every non-null result is additionally asserted against
 * [RecurrenceEngine.isScheduled] — the test proves the calculated date is a
 * genuinely scheduled occurrence, not just the next clock time.
 */
class ReminderPlannerTest {

    private val mon = LocalDate.of(2026, 1, 5) // a Monday

    private fun activity(
        recurrence: Recurrence = Recurrence.Daily,
        status: ActivityStatus = ActivityStatus.ACTIVE,
        startDate: LocalDate = mon,
        endDate: LocalDate? = null,
    ) = Activity(title = "T", startDate = startDate, endDate = endDate, recurrence = recurrence, status = status)

    private fun mwf() = activity(
        Recurrence.SelectedWeekdays(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
    )

    private fun assertScheduled(activity: Activity, fire: LocalDateTime) {
        assertTrue(
            "planner returned $fire but it is not scheduled",
            RecurrenceEngine.isScheduled(activity, fire.toLocalDate()),
        )
    }

    @Test
    fun `mwf activity on tuesday fires wednesday, not tomorrow-blind`() {
        val a = mwf()
        val fire = ReminderPlanner.nextFire(a, "19:00", 0, LocalDateTime.of(2026, 1, 6, 18, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 7, 19, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `next valid date skips unscheduled tomorrow`() {
        val a = mwf() // Wed 20:00: Thu unscheduled -> Fri 19:00
        val fire = ReminderPlanner.nextFire(a, "19:00", 0, LocalDateTime.of(2026, 1, 7, 20, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 9, 19, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `daily fires today when time still ahead`() {
        val a = activity()
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 6, 7, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 6, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `daily rolls to tomorrow when time passed`() {
        val a = activity()
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 6, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 7, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `monthly respects engine monthly semantics`() {
        val a = activity(Recurrence.Monthly(15), startDate = LocalDate.of(2026, 1, 15))
        val fire = ReminderPlanner.nextFire(a, "09:00", 0, LocalDateTime.of(2026, 1, 20, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 2, 15, 9, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `every-x-days respects interval and anchor`() {
        val a = activity(Recurrence.EveryNDays(3)) // Jan 5, 8, 11, ...
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 6, 18, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 8, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `weekly fires on anchor weekday`() {
        val a = activity(Recurrence.Weekly) // anchor Monday
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 6, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 12, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `every-n-weeks respects selected weekdays`() {
        val a = activity(Recurrence.EveryNWeeks(1, setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)))
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 5, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 6, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `weekdays preset skips the weekend`() {
        val a = activity(Recurrence.Weekdays)
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 9, 18, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 12, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `one-time fires on its due date only`() {
        val a = activity(Recurrence.Once, startDate = LocalDate.of(2026, 1, 10))
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 9, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 10, 8, 0), fire)
        assertScheduled(a, fire)
        assertNull(ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 10, 20, 0)))
    }

    @Test
    fun `paused and archived activities get no alarm`() {
        assertNull(
            ReminderPlanner.nextFire(
                activity(status = ActivityStatus.PAUSED), "08:00", 0, LocalDateTime.of(2026, 1, 6, 10, 0),
            ),
        )
        assertNull(
            ReminderPlanner.nextFire(
                activity(status = ActivityStatus.ARCHIVED), "08:00", 0, LocalDateTime.of(2026, 1, 6, 10, 0),
            ),
        )
    }

    @Test
    fun `end date bounds the fire time`() {
        val a = activity(endDate = LocalDate.of(2026, 1, 7))
        val fire = ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 6, 18, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 7, 8, 0), fire)
        assertScheduled(a, fire)
        // Past the end date there is nothing left to arm.
        assertNull(ReminderPlanner.nextFire(a, "08:00", 0, LocalDateTime.of(2026, 1, 7, 20, 0)))
    }

    @Test
    fun `daysMask constrains but never widens the schedule`() {
        val a = activity() // daily
        val mondayBit = 1 shl (DayOfWeek.MONDAY.value - 1)
        val fire = ReminderPlanner.nextFire(a, "08:00", mondayBit, LocalDateTime.of(2026, 1, 6, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 12, 8, 0), fire)
        assertScheduled(a, fire)
    }

    @Test
    fun `malformed time degrades instead of crashing`() {
        val a = activity()
        val fire = ReminderPlanner.nextFire(a, "evening", 0, LocalDateTime.of(2026, 1, 6, 10, 0))!!
        assertEquals(LocalDateTime.of(2026, 1, 7, 0, 0), fire)
    }
}
