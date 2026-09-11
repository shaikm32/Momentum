package com.momentum.app.notification

import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.Reminder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P0#3 regression: exactly one stable alarm per activity across the whole
 * lifecycle. Runs the production decision path ([syncActivityAlarms] +
 * [planActivityAlarms] + [alarmRequestCode]) against a fake gateway, so the
 * cancel-before-set sequencing and the no-accumulation guarantee actually
 * execute without touching Android's AlarmManager.
 */
class AlarmPlanningTest {

    private val tue10 = LocalDateTime.of(2026, 1, 6, 10, 0) // a Tuesday

    private fun activity(
        id: Long = 7,
        recurrence: Recurrence = Recurrence.Daily,
        status: ActivityStatus = ActivityStatus.ACTIVE,
    ) = Activity(id = id, title = "T", startDate = LocalDate.of(2026, 1, 5), recurrence = recurrence, status = status)

    private fun reminder(
        activityId: Long = 7,
        time: String = "08:00",
        enabled: Boolean = true,
    ) = Reminder(id = 1, activityId = activityId, timeLocal = time, enabled = enabled)

    private class FakeGateway : AlarmGateway {
        data class SetCall(val requestCode: Int, val activityId: Long, val triggerAtMillis: Long, val exact: Boolean)

        sealed interface Event {
            data class Cancel(val requestCode: Int) : Event
            data class Set(val requestCode: Int, val triggerAtMillis: Long) : Event
        }

        val sets = mutableListOf<SetCall>()
        val cancels = mutableListOf<Pair<Int, Long>>()
        val events = mutableListOf<Event>()
        val armed = LinkedHashMap<Int, Long>()

        override fun setAlarm(requestCode: Int, activityId: Long, triggerAtMillis: Long, exactAllowed: Boolean) {
            sets.add(SetCall(requestCode, activityId, triggerAtMillis, exactAllowed))
            events.add(Event.Set(requestCode, triggerAtMillis))
            armed[requestCode] = triggerAtMillis
        }

        override fun cancelAlarm(requestCode: Int, activityId: Long) {
            cancels.add(requestCode to activityId)
            events.add(Event.Cancel(requestCode))
            armed.remove(requestCode)
        }
    }

    private fun millis(fire: LocalDateTime): Long =
        fire.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private fun sync(
        gateway: FakeGateway,
        activity: Activity?,
        reminders: List<Reminder>,
        now: LocalDateTime = tue10,
        exact: Boolean = true,
    ) = syncActivityAlarms(activity, reminders, now, exact, gateway)

    @Test
    fun `create reminder arms exactly one alarm`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder()))

        assertEquals(1, gateway.armed.size)
        // Tue 10:00 is past 08:00 -> next daily fire is Wed 08:00.
        assertEquals(millis(LocalDateTime.of(2026, 1, 7, 8, 0)), gateway.armed.values.single())
        // Cancel-then-set sequencing, even on first schedule.
        assertEquals(listOf(gateway.cancels.single().first), listOf(gateway.sets.single().requestCode))
    }

    @Test
    fun `edit reminder time replaces the alarm`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder(time = "08:00")))
        val firstTrigger = gateway.armed.values.single()

        sync(gateway, activity(), listOf(reminder(time = "20:00")))

        assertEquals(1, gateway.armed.size)
        assertEquals(millis(LocalDateTime.of(2026, 1, 6, 20, 0)), gateway.armed.values.single())
        assertNotEquals(firstTrigger, gateway.armed.values.single())
        // Strict cancel-then-set ordering on one stable identity, twice.
        val code = alarmRequestCode(7)
        assertEquals(
            listOf(
                FakeGateway.Event.Cancel(code),
                FakeGateway.Event.Set(code, firstTrigger),
                FakeGateway.Event.Cancel(code),
                FakeGateway.Event.Set(code, gateway.armed.values.single()),
            ),
            gateway.events,
        )
    }

    @Test
    fun `edit recurrence replaces the alarm on a scheduled date`() {
        val gateway = FakeGateway()
        val mwf = activity(
            recurrence = Recurrence.SelectedWeekdays(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
        )
        sync(gateway, mwf, listOf(reminder(time = "19:00")))

        assertEquals(1, gateway.armed.size)
        val trigger = gateway.armed.values.single()
        val triggerDate = java.time.Instant.ofEpochMilli(trigger)
            .atZone(ZoneId.systemDefault()).toLocalDate()
        assertEquals(LocalDate.of(2026, 1, 7), triggerDate) // Wed, not Tue/Thu
        assertTrue(RecurrenceEngine.isScheduled(mwf, triggerDate))
    }

    @Test
    fun `repeated edits never accumulate alarms`() {
        val gateway = FakeGateway()
        val a = activity()
        repeat(5) { i ->
            sync(gateway, a, listOf(reminder(time = "0${6 + i}:30")))
        }
        assertEquals(1, gateway.armed.size)
        assertEquals(5, gateway.cancels.size) // one cancel per sync (incl. first)
        assertEquals(5, gateway.sets.size)
    }

    @Test
    fun `disabled reminder arms zero alarms`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder()))
        assertEquals(1, gateway.armed.size)

        sync(gateway, activity(), listOf(reminder(enabled = false)))
        assertEquals(0, gateway.armed.size)
    }

    @Test
    fun `pause cancels the alarm`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder()))
        assertEquals(1, gateway.armed.size)

        sync(gateway, activity(status = ActivityStatus.PAUSED), listOf(reminder()))
        assertEquals(0, gateway.armed.size)
    }

    @Test
    fun `archive cancels the alarm`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder()))
        assertEquals(1, gateway.armed.size)

        sync(gateway, activity(status = ActivityStatus.ARCHIVED), listOf(reminder()))
        assertEquals(0, gateway.armed.size)
    }

    @Test
    fun `delete cancels without needing database rows`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder()))
        assertEquals(1, gateway.armed.size)

        // Activity gone (and its reminder rows possibly already deleted):
        // cancellation is by deterministic identity, so it still lands.
        sync(gateway, null, listOf(reminder()))
        assertEquals(0, gateway.armed.size)
        assertEquals(alarmRequestCode(7), gateway.cancels.last().first)
    }

    @Test
    fun `re-enable schedules exactly one valid alarm`() {
        val gateway = FakeGateway()
        sync(gateway, activity(status = ActivityStatus.PAUSED), listOf(reminder()))
        assertEquals(0, gateway.armed.size)

        val a = activity()
        sync(gateway, a, listOf(reminder()))
        assertEquals(1, gateway.armed.size)
        val triggerDate = java.time.Instant.ofEpochMilli(gateway.armed.values.single())
            .atZone(ZoneId.systemDefault()).toLocalDate()
        assertTrue(RecurrenceEngine.isScheduled(a, triggerDate))
    }

    @Test
    fun `alarm identity is stable per activity and distinct across activities`() {
        assertEquals(alarmRequestCode(7), alarmRequestCode(7))
        assertNotEquals(alarmRequestCode(7), alarmRequestCode(8))
    }

    @Test
    fun `resync is idempotent`() {
        val gateway = FakeGateway()
        val a = activity()
        val reminders = listOf(reminder())
        sync(gateway, a, reminders)
        sync(gateway, a, reminders)
        assertEquals(1, gateway.armed.size)
        assertEquals(gateway.sets[0].triggerAtMillis, gateway.sets[1].triggerAtMillis)
    }

    @Test
    fun `exact flag follows device capability`() {
        val gateway = FakeGateway()
        sync(gateway, activity(), listOf(reminder()), exact = false)
        assertEquals(false, gateway.sets.single().exact)
    }
}
