package com.momentum.app.domain.usecase

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Recurrence
import java.io.File
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §9/§20/§22: Home is one derived projection of today's
 * completion state — Remaining today precedes Completed today, empty sections
 * are knowably empty, and progress is `completed / eligible` (never a fake
 * 100% when nothing is scheduled).
 *
 * These tests pin the projection itself so no screen may re-derive it.
 */
class TodaySectionsTest {

    private val monday = LocalDate.of(2026, 1, 5)

    private fun activity(id: Long) = Activity(
        id = id,
        title = "Activity $id",
        startDate = monday,
        recurrence = Recurrence.Daily,
        targetPerOccurrence = 1.0,
        unit = "",
    )

    private fun item(id: Long, completed: Boolean, value: Double = 0.0) = TodayItem(
        activity = activity(id),
        completed = completed,
        completionValue = value,
        currentStreak = 0,
    )

    @Test
    fun `nothing scheduled today is the no-activity state, not a fake 100 percent`() {
        val sections = partitionTodayItems(emptyList())

        assertTrue("no eligible activity should hide both sections", sections.isEmpty)
        assertFalse("an empty day must not read as all-complete", sections.allComplete)
        assertEquals(0, sections.totalCount)
        assertEquals(0, sections.doneCount)
        assertEquals("0/0 must never divide to NaN or 100%", 0f, sections.progress, 0f)
    }

    @Test
    fun `every remaining item precedes every completed item`() {
        val sections = partitionTodayItems(
            listOf(
                item(1, completed = true),
                item(2, completed = false),
                item(3, completed = true),
                item(4, completed = false),
            ),
        )

        assertEquals(listOf(2L, 4L), sections.remaining.map { it.activity.id })
        assertEquals(listOf(1L, 3L), sections.completed.map { it.activity.id })
    }

    @Test
    fun `relative order inside each section follows the schedule order`() {
        val sections = partitionTodayItems(
            listOf(
                item(3, completed = false),
                item(1, completed = false),
                item(9, completed = true),
                item(7, completed = true),
            ),
        )

        assertEquals(listOf(3L, 1L), sections.remaining.map { it.activity.id })
        assertEquals(listOf(9L, 7L), sections.completed.map { it.activity.id })
    }

    @Test
    fun `progress is completed divided by eligible`() {
        val sections = partitionTodayItems(
            listOf(item(1, completed = true), item(2, completed = true), item(3, completed = false)),
        )

        assertEquals(3, sections.totalCount)
        assertEquals(2, sections.doneCount)
        assertEquals(2f / 3f, sections.progress, 0.0001f)
        assertFalse("one item is still open", sections.allComplete)
        assertFalse(sections.isEmpty)
    }

    @Test
    fun `all complete keeps the denominator and hides the remaining section`() {
        val sections = partitionTodayItems(listOf(item(1, completed = true), item(2, completed = true)))

        assertTrue(sections.allComplete)
        assertTrue("the empty section must be knowably empty", sections.remaining.isEmpty())
        assertEquals(2, sections.completed.size)
        assertEquals(2, sections.totalCount)
        assertEquals(1f, sections.progress, 0f)
    }

    @Test
    fun `a single eligible activity still divides correctly`() {
        assertFalse(partitionTodayItems(listOf(item(1, completed = false))).progress.isNaN())
        assertEquals(1f, partitionTodayItems(listOf(item(1, completed = true))).progress, 0f)
        assertEquals(0f, partitionTodayItems(listOf(item(1, completed = false))).progress, 0f)
    }

    @Test
    fun `completed items keep the value needed by the card label`() {
        val sections = partitionTodayItems(listOf(item(1, completed = true, value = 8.0)))

        assertEquals(8.0, sections.completed.single().completionValue, 0.0)
        assertEquals(0.0, sections.remaining.singleOrNull()?.completionValue ?: 0.0, 0.0)
    }

    @Test
    fun `the projection of a real schedule matches its completion flags`() {
        val items = GetTodayScheduleUseCase()(
            monday,
            listOf(activity(1), activity(2)),
            mapOf(1L to mapOf(monday to 1.0)),
        )
        val sections = partitionTodayItems(items)

        assertEquals(2, sections.totalCount)
        assertEquals(1, sections.doneCount)
        assertEquals(listOf(2L), sections.remaining.map { it.activity.id })
        assertEquals(listOf(1L), sections.completed.map { it.activity.id })
    }
}

/**
 * Slicing guard: Home must *read* the shared projection, never re-derive it.
 * A composable filtering today's items itself is a second source of truth.
 */
class HomeSectionsSourceGuardTest {

    private fun source(name: String): String =
        File("src/main/java/com/momentum/app/presentation/screens/today/$name").readText()

    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    @Test
    fun `the home view model is the only place that partitions today`() {
        val vm = codeOf(source("TodayViewModel.kt"))

        assertTrue(
            "TodayViewModel must derive sections with partitionTodayItems",
            "partitionTodayItems(" in vm,
        )
        assertFalse("do not hand-roll the split", "count { it.completed }" in vm)
    }

    @Test
    fun `the home screen consumes sections instead of filtering items`() {
        val screen = codeOf(source("TodayScreen.kt"))

        assertFalse("state.items no longer exists", "state.items" in screen)
        assertFalse("never re-filter completion state in Compose", "filter { !it.completed }" in screen)
        assertTrue("must read the Remaining section", "state.remaining" in screen)
        assertTrue("must read the Completed section", "state.completed" in screen)
    }
}
