package com.momentum.app.presentation.screens.calendar

import com.momentum.app.domain.model.WeekStart
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P2#1: the persisted WeekStart must drive calendar presentation (header order
 * and grid alignment). Pure java.time helpers — no Android dependencies.
 */
class CalendarWeekStartTest {

    @Test
    fun `monday start orders monday first`() {
        assertEquals(
            listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY,
            ),
            orderedWeekDays(WeekStart.MONDAY),
        )
    }

    @Test
    fun `sunday start orders sunday first`() {
        val days = orderedWeekDays(WeekStart.SUNDAY)
        assertEquals(DayOfWeek.SUNDAY, days.first())
        assertEquals(DayOfWeek.SATURDAY, days.last())
        assertEquals(7, days.toSet().size)
    }

    @Test
    fun `september 2026 starts tuesday`() {
        // Sep 1 2026 is a Tuesday: Monday grid backs to Aug 31, Sunday grid to Aug 30.
        val month = YearMonth.of(2026, 9)
        assertEquals(LocalDate.of(2026, 8, 31), gridStartForMonth(month, WeekStart.MONDAY))
        assertEquals(LocalDate.of(2026, 8, 30), gridStartForMonth(month, WeekStart.SUNDAY))
    }

    @Test
    fun `month starting monday needs no backup for monday start`() {
        // Jun 1 2026 is a Monday.
        val month = YearMonth.of(2026, 6)
        assertEquals(LocalDate.of(2026, 6, 1), gridStartForMonth(month, WeekStart.MONDAY))
        assertEquals(LocalDate.of(2026, 5, 31), gridStartForMonth(month, WeekStart.SUNDAY))
    }

    @Test
    fun `month starting sunday needs no backup for sunday start`() {
        // Nov 1 2026 is a Sunday.
        val month = YearMonth.of(2026, 11)
        assertEquals(LocalDate.of(2026, 10, 26), gridStartForMonth(month, WeekStart.MONDAY))
        assertEquals(LocalDate.of(2026, 11, 1), gridStartForMonth(month, WeekStart.SUNDAY))
    }

    @Test
    fun `grid always starts on the configured first weekday`() {
        val months = listOf(
            YearMonth.of(2026, 1), YearMonth.of(2026, 2), YearMonth.of(2026, 6),
            YearMonth.of(2026, 9), YearMonth.of(2026, 11), YearMonth.of(2027, 2),
        )
        for (month in months) {
            val first = month.atDay(1)
            val mondayGrid = gridStartForMonth(month, WeekStart.MONDAY)
            assertEquals(DayOfWeek.MONDAY, mondayGrid.dayOfWeek)
            assertTrue(!mondayGrid.isAfter(first))
            assertTrue(ChronoUnit.DAYS.between(mondayGrid, first) < 7)

            val sundayGrid = gridStartForMonth(month, WeekStart.SUNDAY)
            assertEquals(DayOfWeek.SUNDAY, sundayGrid.dayOfWeek)
            assertTrue(!sundayGrid.isAfter(first))
            assertTrue(ChronoUnit.DAYS.between(sundayGrid, first) < 7)
        }
    }

    @Test
    fun `grid covers exactly 42 consecutive dates`() {
        for (start in WeekStart.entries) {
            val gridStart = gridStartForMonth(YearMonth.of(2026, 9), start)
            val cells = (0..41).map { gridStart.plusDays(it.toLong()) }
            assertEquals(42, cells.toSet().size)
            assertEquals(gridStart.plusDays(41), cells.last())
        }
    }
}
