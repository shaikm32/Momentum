package com.momentum.app.domain.usecase

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §17–§19 / DECISIONS §23–§25:
 * today's eligibility is the device's **local calendar date**, gated by the
 * configured start/end range and weekdays, and streaks run over *successful
 * scheduled opportunities* — unscheduled days neither appear nor break a run.
 *
 * Dates: 2026-01-05 is a Monday, so the week runs
 * Mon 5 · Tue 6 · Wed 7 · Thu 8 · Fri 9 · Sat 10 · Sun 11.
 */
class TodaySchedulingTest {

    private val monday = LocalDate.of(2026, 1, 5)
    private val useCase = GetTodayScheduleUseCase()

    private fun daily(
        id: Long = 1,
        startDate: LocalDate = monday,
        endDate: LocalDate? = null,
        target: Double = 1.0,
        unit: String = "",
        status: ActivityStatus = ActivityStatus.ACTIVE,
    ) = Activity(
        id = id,
        title = "Habit",
        startDate = startDate,
        endDate = endDate,
        recurrence = Recurrence.Daily,
        targetPerOccurrence = target,
        unit = unit,
        status = status,
    )

    private fun monThuSat(id: Long = 2) = Activity(
        id = id,
        title = "Three days",
        startDate = monday,
        recurrence = Recurrence.SelectedWeekdays(
            setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY),
        ),
    )

    private fun ids(date: LocalDate, vararg activities: Activity, done: Map<Long, Map<LocalDate, Double>> = emptyMap()) =
        useCase(date, activities.toList(), done).map { it.activity.id }

    // ——— eligibility: range + weekdays ———

    @Test
    fun `daily schedule is eligible every day inside its range`() {
        for (day in 0L..6L) {
            assertEquals(
                "expected an item on ${monday.plusDays(day)}",
                listOf(1L),
                ids(monday.plusDays(day), daily()),
            )
        }
    }

    @Test
    fun `monday thursday saturday is eligible only on those weekdays`() {
        val habit = monThuSat()
        assertEquals(listOf(2L), ids(monday, habit)) // Mon
        assertEquals(emptyList<Long>(), ids(monday.plusDays(1), habit)) // Tue
        assertEquals(emptyList<Long>(), ids(monday.plusDays(2), habit)) // Wed
        assertEquals(listOf(2L), ids(monday.plusDays(3), habit)) // Thu
        assertEquals(emptyList<Long>(), ids(monday.plusDays(4), habit)) // Fri
        assertEquals(listOf(2L), ids(monday.plusDays(5), habit)) // Sat
        assertEquals(emptyList<Long>(), ids(monday.plusDays(6), habit)) // Sun
    }

    @Test
    fun `activity is not eligible before its start date`() {
        val habit = daily(startDate = monday.plusDays(2))
        assertEquals(emptyList<Long>(), ids(monday, habit))
        assertEquals(emptyList<Long>(), ids(monday.plusDays(1), habit))
        assertEquals(listOf(1L), ids(monday.plusDays(2), habit))
    }

    @Test
    fun `activity is not eligible after its end date`() {
        val habit = daily(endDate = monday.plusDays(2))
        assertEquals(listOf(1L), ids(monday, habit))
        assertEquals(listOf(1L), ids(monday.plusDays(2), habit))
        assertEquals(emptyList<Long>(), ids(monday.plusDays(3), habit))
    }

    @Test
    fun `an unscheduled day creates no completion opportunity and never shows as incomplete`() {
        val habit = monThuSat()
        val tuesday = monday.plusDays(1)
        // No row at all: it is neither completed nor waiting in Remaining today.
        assertTrue(useCase(tuesday, listOf(habit), emptyMap()).isEmpty())
        // Even a stray record for an unscheduled date does not surface it.
        assertTrue(useCase(tuesday, listOf(habit), mapOf(2L to mapOf(tuesday to 1.0))).isEmpty())
    }

    @Test
    fun `paused activity is never eligible`() {
        assertEquals(emptyList<Long>(), ids(monday, daily(status = ActivityStatus.PAUSED)))
    }

    // ——— streaks over scheduled opportunities ———

    @Test
    fun `a successful completion continues the streak`() {
        val done = mapOf(
            1L to mapOf(monday to 1.0, monday.plusDays(1) to 1.0, monday.plusDays(2) to 1.0),
        )
        val items = useCase(monday.plusDays(2), listOf(daily()), done)
        assertTrue(items.single().completed)
        assertEquals(3, items.single().currentStreak)
    }

    @Test
    fun `a failed scheduled opportunity breaks the streak`() {
        // Mon completed, Tue missed, Wed completed → the missed Tue ends the run.
        val done = mapOf(1L to mapOf(monday to 1.0, monday.plusDays(2) to 1.0))
        val items = useCase(monday.plusDays(2), listOf(daily()), done)
        assertTrue(items.single().completed)
        assertEquals(1, items.single().currentStreak)
    }

    @Test
    fun `unscheduled days never break the streak`() {
        // Mon/Wed/Fri habit: Tue, Thu, Sat and Sun are absent from the schedule.
        val habit = Activity(
            id = 7,
            title = "MWF",
            startDate = monday,
            recurrence = Recurrence.SelectedWeekdays(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
        )
        val done = mapOf(
            7L to mapOf(monday to 1.0, monday.plusDays(2) to 1.0, monday.plusDays(4) to 1.0),
        )
        val items = useCase(monday.plusDays(4), listOf(habit), done)
        assertEquals(3, items.single().currentStreak)
    }

    @Test
    fun `target completion decides whether the streak continues`() {
        val water = daily(id = 9, target = 8.0, unit = "glasses")
        val tue = monday.plusDays(1)
        val wed = monday.plusDays(2)

        // Tue logged at 5/8 → below target → unsuccessful opportunity.
        val partial = useCase(wed, listOf(water), mapOf(9L to mapOf(monday to 8.0, tue to 5.0, wed to 8.0)))
        assertTrue(partial.single().completed) // today (Wed) reached target
        assertEquals(1, partial.single().currentStreak) // Tue's partial broke the run

        // Tue logged at 8/8 → successful → the whole run survives.
        val full = useCase(wed, listOf(water), mapOf(9L to mapOf(monday to 8.0, tue to 8.0, wed to 8.0)))
        assertTrue(full.single().completed)
        assertEquals(3, full.single().currentStreak)

        // Today itself below target → incomplete today and the run stops here.
        val todayPartial = useCase(wed, listOf(water), mapOf(9L to mapOf(monday to 8.0, tue to 8.0, wed to 5.0)))
        assertFalse(todayPartial.single().completed)
        assertEquals(5.0, todayPartial.single().completionValue, 0.0)
        assertEquals(0, todayPartial.single().currentStreak)
    }

    @Test
    fun `completion reversal recalculate the streak`() {
        val all = mapOf(1L to mapOf(monday to 1.0, monday.plusDays(1) to 1.0, monday.plusDays(2) to 1.0))
        val wednesday = monday.plusDays(2)
        assertEquals(3, useCase(wednesday, listOf(daily()), all).single().currentStreak)

        // Reversing today's completion drops the run immediately.
        val reversedToday = mapOf(1L to mapOf(monday to 1.0, monday.plusDays(1) to 1.0))
        assertEquals(0, useCase(wednesday, listOf(daily()), reversedToday).single().currentStreak)

        // Reversing a middle day splits the run rather than erasing it.
        val reversedMiddle = mapOf(1L to mapOf(monday to 1.0, wednesday to 1.0))
        assertEquals(1, useCase(wednesday, listOf(daily()), reversedMiddle).single().currentStreak)
    }

    @Test
    fun `progress counts eligible activities only`() {
        val habit = monThuSat()
        // Tuesday: nothing eligible → no misleading 0% row, no phantom item.
        assertTrue(useCase(monday.plusDays(1), listOf(habit), emptyMap()).isEmpty())
        // Monday: one eligible, incomplete → progress 0/1.
        val mondayItems = useCase(monday, listOf(habit), emptyMap())
        assertEquals(1, mondayItems.size)
        assertFalse(mondayItems.single().completed)
    }
}

/**
 * DASHBOARD_SPECS §19 / DECISIONS §25: the device's local calendar date is the
 * source of truth for "today" and Home must never use a UTC boundary. Source
 * guard (same style as HabitsHeroAssetsTest) so a future refactor cannot
 * silently swap the clock.
 */
class HomeLocalDateGuardTest {

    private fun vmSource(): String {
        val file = File("src/main/java/com/momentum/app/presentation/screens/today/TodayViewModel.kt")
        assertTrue("missing TodayViewModel source: ${file.absolutePath}", file.isFile)
        // Scan executable lines only — comments are allowed to mention UTC.
        return file.readText().lineSequence()
            .filterNot { it.trimStart().startsWith("//") }
            .joinToString("\n")
    }

    @Test
    fun `home derives today from the device local calendar date`() {
        val code = vmSource()
        assertTrue("expected LocalDate.now()", code.contains("LocalDate.now()"))
        listOf("ZoneOffset", ".UTC", "Instant.now()", "toInstant(").forEach { token ->
            assertFalse("Home must not use a UTC date boundary: $token", code.contains(token))
        }
    }

    @Test
    fun `home re-evaluates when the local date rolls over`() {
        val code = vmSource()
        assertTrue("expected a local-midnight wait", code.contains("atStartOfDay"))
        assertTrue("expected a refresh after midnight", code.contains("refreshTick.value += 1"))
    }
}
