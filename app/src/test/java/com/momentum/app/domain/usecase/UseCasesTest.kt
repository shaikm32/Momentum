package com.momentum.app.domain.usecase

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.repository.CompletionRepository
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTodayScheduleUseCaseTest {

    private val monday = LocalDate.of(2026, 1, 5)
    private val useCase = GetTodayScheduleUseCase()

    @Test
    fun `only scheduled active activities appear, ordered by priority`() {
        val daily = Activity(id = 1, title = "B daily", startDate = monday)
        val mwf = Activity(
            id = 2,
            title = "A mwf",
            startDate = monday,
            recurrence = Recurrence.SelectedWeekdays(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
            priority = 3,
        )
        val paused = Activity(id = 3, title = "paused", startDate = monday, status = ActivityStatus.PAUSED)
        val tuesday = LocalDate.of(2026, 1, 6) // mwf not scheduled
        val items = useCase(tuesday, listOf(daily, mwf, paused), emptyMap())
        assertEquals(listOf(1L), items.map { it.activity.id })

        val wednesday = LocalDate.of(2026, 1, 7)
        val both = useCase(wednesday, listOf(daily, mwf, paused), emptyMap())
        // Priority 3 first despite title sort.
        assertEquals(listOf(2L, 1L), both.map { it.activity.id })
    }

    @Test
    fun `streak counts scheduled occurrences only`() {
        val mwf = Activity(
            id = 7,
            title = "mwf",
            startDate = monday,
            recurrence = Recurrence.SelectedWeekdays(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
        )
        val friday = LocalDate.of(2026, 1, 9)
        val done = mapOf(7L to setOf(monday, LocalDate.of(2026, 1, 7), friday))
        val items = useCase(friday, listOf(mwf), done)
        assertEquals(1, items.size)
        assertTrue(items[0].completed)
        assertEquals(3, items[0].currentStreak)
    }
}

private class FakeCompletionRepository : CompletionRepository {
    private val store = mutableMapOf<Pair<Long, String>, Double>()
    override fun observeForActivity(activityId: Long): Flow<List<com.momentum.app.domain.model.CompletionRecord>> =
        flowOf(emptyList())
    override fun observeInRange(fromIso: String, toIso: String): Flow<List<com.momentum.app.domain.model.CompletionRecord>> =
        flowOf(emptyList())
    override suspend fun recordsForActivity(activityId: Long): List<com.momentum.app.domain.model.CompletionRecord> =
        emptyList()
    override suspend fun complete(activityId: Long, occurrenceDateIso: String, value: Double): Long {
        store[activityId to occurrenceDateIso] = value
        return 1
    }
    override suspend fun uncomplete(activityId: Long, occurrenceDateIso: String) {
        store.remove(activityId to occurrenceDateIso)
    }
    override suspend fun isCompleted(activityId: Long, occurrenceDateIso: String): Boolean =
        (activityId to occurrenceDateIso) in store
    override suspend fun clearAll() {
        store.clear()
    }
}

class ToggleCompletionUseCaseTest {

    @Test
    fun `toggle flips state`() = runTest {
        val repo = FakeCompletionRepository()
        val toggle = ToggleCompletionUseCase(repo)
        val date = LocalDate.of(2026, 1, 5)
        assertTrue(toggle(1, date))
        assertTrue(repo.isCompleted(1, "2026-01-05"))
        assertFalse(toggle(1, date))
        assertFalse(repo.isCompleted(1, "2026-01-05"))
    }
}
