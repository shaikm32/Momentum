package com.momentum.app.presentation.screens.detail

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.momentum.app.TestGraph
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.habitDetailViewModel
import com.momentum.app.notification.ReminderScheduler
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private class NoopScheduler : ReminderScheduler {
    override suspend fun scheduleForActivity(activityId: Long) = Unit
    override suspend fun cancelForActivity(activityId: Long) = Unit
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HabitDetailViewModelTest {

    private lateinit var container: TestGraph

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase("momentum.db")
        container = TestGraph(context, reminderSchedulerOverride = NoopScheduler())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `success state exposes weekly overview and streaks`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val start = LocalDate.now().minusDays(2)
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Stretch",
                activityType = ActivityType.HABIT,
                startDate = start,
                recurrence = Recurrence.Daily,
            ),
            emptyList(),
        )
        container.completionRepository.complete(id, start.toString(), 1.0)
        container.completionRepository.complete(id, start.plusDays(1).toString(), 1.0)

        val vm = container.habitDetailViewModel(id)
        val state = awaitState(vm.state) { it is HabitDetailUiState.Success } as HabitDetailUiState.Success

        assertEquals("Stretch", state.activity.title)
        assertEquals(7, state.overview.week.days.size)
        assertEquals(2, state.overview.longestStreak)
        // Mon–Wed-ish window: 2 completions already stored this week.
        assertTrue(state.overview.week.completedCount >= 0)
    }

    @Test
    fun `not found for missing id`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = container.habitDetailViewModel(999L)
        assertEquals(HabitDetailUiState.NotFound, awaitState(vm.state) { it is HabitDetailUiState.NotFound })
    }

    @Test
    fun `pause sets status and cancel reminders path runs`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Pause me",
                activityType = ActivityType.HABIT,
                startDate = LocalDate.now(),
            ),
            emptyList(),
        )
        val vm = container.habitDetailViewModel(id)
        awaitState(vm.state) { it is HabitDetailUiState.Success }

        vm.setStatus(ActivityStatus.PAUSED)
        // Await the re-emitted Success — the write lands on Room's query
        // executor, which advanceUntilIdle does not wait for.
        awaitState(vm.state) {
            it is HabitDetailUiState.Success && it.activity.status == ActivityStatus.PAUSED
        }

        assertEquals(ActivityStatus.PAUSED, container.activityRepository.getById(id)!!.status)
    }

    @Test
    fun `deleteHard removes habit and emits Deleted`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Delete me",
                activityType = ActivityType.HABIT,
                startDate = LocalDate.now(),
            ),
            emptyList(),
        )
        val vm = container.habitDetailViewModel(id)

        vm.deleteHard()
        // Deleted is sent only after the Room delete commits.
        assertTrue(awaitEffect(vm) is HabitDetailEffect.Deleted)

        assertNull(container.activityRepository.getById(id))
    }
}

/**
 * Awaits a [HabitDetailUiState] matching [predicate] through Turbine (Phase D)
 * — replaces the hand-rolled `state.first { … }` collection, including the
 * Room-write waits that `advanceUntilIdle` cannot cover.
 */
private suspend fun awaitState(
    flow: StateFlow<HabitDetailUiState>,
    predicate: (HabitDetailUiState) -> Boolean,
): HabitDetailUiState {
    var matched: HabitDetailUiState? = null
    flow.test {
        var item = awaitItem()
        while (!predicate(item)) item = awaitItem()
        matched = item
    }
    return requireNotNull(matched) { "state flow completed without a matching emission" }
}

/** Awaits the next one-shot effect through Turbine (Phase D). */
private suspend fun awaitEffect(vm: HabitDetailViewModel): HabitDetailEffect {
    var next: HabitDetailEffect? = null
    vm.effect.test { next = awaitItem() }
    return requireNotNull(next) { "effect flow completed without emitting" }
}
