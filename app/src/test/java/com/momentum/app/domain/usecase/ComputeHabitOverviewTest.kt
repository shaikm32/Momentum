package com.momentum.app.domain.usecase

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.WeekStart
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ComputeHabitOverviewTest {

    private val useCase = ComputeHabitOverviewUseCase()
    // Monday 2026-01-05.
    private val monday = LocalDate.of(2026, 1, 5)
    private val sunday = LocalDate.of(2026, 1, 11)

    private fun daily(
        startDate: LocalDate = monday,
        endDate: LocalDate? = null,
        status: ActivityStatus = ActivityStatus.ACTIVE,
    ) = Activity(
        title = "Habit",
        startDate = startDate,
        endDate = endDate,
        recurrence = Recurrence.Daily,
        status = status,
    )

    @Test
    fun `monday-first week window for Monday week start`() {
        val today = LocalDate.of(2026, 1, 8) // Thursday
        val overview = useCase(daily(), emptySet(), today, WeekStart.MONDAY)
        assertEquals(monday, overview.week.days.first().date)
        assertEquals(sunday, overview.week.days.last().date)
        assertEquals(7, overview.week.days.size)
    }

    @Test
    fun `sunday-first week window for Sunday week start`() {
        val today = LocalDate.of(2026, 1, 7) // Wednesday
        val overview = useCase(daily(), emptySet(), today, WeekStart.SUNDAY)
        // Week containing Wed Jan 7 starting Sunday is Jan 4–10.
        assertEquals(LocalDate.of(2026, 1, 4), overview.week.days.first().date)
        assertEquals(LocalDate.of(2026, 1, 10), overview.week.days.last().date)
    }

    @Test
    fun `weekly fraction uses scheduled days as denominator not calendar 7`() {
        // MWF habit; week Jan 5–11 → scheduled Mon/Wed/Fri = 3.
        val mwf = Activity(
            title = "MWF",
            startDate = monday,
            recurrence = Recurrence.SelectedWeekdays(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
        )
        val completed = setOf(
            LocalDate.of(2026, 1, 5), // Mon
            LocalDate.of(2026, 1, 7), // Wed
        )
        val overview = useCase(mwf, completed, LocalDate.of(2026, 1, 8), WeekStart.MONDAY)

        val notScheduled = overview.week.days.count {
            it.state == HabitDayState.NOT_SCHEDULED
        }
        assertEquals(4, notScheduled) // Tue, Thu, Sat, Sun
        assertEquals(3, overview.week.scheduledCount)
        assertEquals(2, overview.week.completedCount)
        assertEquals(2.0 / 3.0, overview.week.fraction, 1e-9)
    }

    @Test
    fun `future scheduled days later this week count in denominator`() {
        val overview = useCase(daily(), emptySet(), monday, WeekStart.MONDAY)
        // Full week of a daily habit: 7 scheduled even though only Monday has passed.
        assertEquals(7, overview.week.scheduledCount)
        assertEquals(0, overview.week.completedCount)
    }

    @Test
    fun `paused habit keeps status-independent schedule and history`() {
        val paused = daily(status = ActivityStatus.PAUSED)
        val completed = setOf(LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 6))
        val overview = useCase(paused, completed, LocalDate.of(2026, 1, 7), WeekStart.MONDAY)

        // Status-independent schedule: full week Mon–Sun stays scheduled (7).
        assertEquals(7, overview.week.scheduledCount)
        assertEquals(2, overview.week.completedCount)
        // Jan 5–6 done, Jan 7 not completed → current 0, longest 2.
        assertEquals(0, overview.currentStreak)
        assertEquals(2, overview.longestStreak)
    }

    @Test
    fun `current streak counts consecutive completed scheduled days`() {
        val overview = useCase(
            daily(),
            setOf(LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7)),
            LocalDate.of(2026, 1, 7),
            WeekStart.MONDAY,
        )
        assertEquals(3, overview.currentStreak)
        assertEquals(3, overview.longestStreak)
    }

    @Test
    fun `end date caps schedule so days after end are not scheduled`() {
        val endsWed = daily(endDate = LocalDate.of(2026, 1, 7))
        val overview = useCase(endsWed, emptySet(), LocalDate.of(2026, 1, 8), WeekStart.MONDAY)
        // Mon 5, Tue 6, Wed 7 scheduled; Thu–Sun after end not scheduled.
        assertEquals(3, overview.week.scheduledCount)
        assertEquals(4, overview.week.days.count { it.state == HabitDayState.NOT_SCHEDULED })
    }

    @Test
    fun `nothing scheduled yields zero fraction and zero streaks`() {
        val once = Activity(
            title = "One-off",
            startDate = LocalDate.of(2025, 12, 1),
            recurrence = Recurrence.Once,
        )
        val overview = useCase(once, emptySet(), monday, WeekStart.MONDAY)
        assertEquals(0, overview.week.scheduledCount)
        assertEquals(0.0, overview.week.fraction, 1e-9)
        assertEquals(0, overview.currentStreak)
        assertEquals(0, overview.longestStreak)
    }
}
