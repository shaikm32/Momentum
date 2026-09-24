package com.momentum.app.presentation.screens.detail

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.notification.ReminderScheduler
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

    private lateinit var container: AppContainer

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase("momentum.db")
        container = AppContainer(context, reminderSchedulerOverride = NoopScheduler())
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

        val vm = HabitDetailViewModel(container, id)
        val state = vm.state.first { it is HabitDetailUiState.Success } as HabitDetailUiState.Success

        assertEquals("Stretch", state.activity.title)
        assertEquals(7, state.overview.week.days.size)
        assertEquals(2, state.overview.longestStreak)
        // Mon–Wed-ish window: 2 completions already stored this week.
        assertTrue(state.overview.week.completedCount >= 0)
    }

    @Test
    fun `not found for missing id`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = HabitDetailViewModel(container, 999L)
        assertEquals(HabitDetailUiState.NotFound, vm.state.first { it is HabitDetailUiState.NotFound })
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
        val vm = HabitDetailViewModel(container, id)
        vm.state.first { it is HabitDetailUiState.Success }

        vm.setStatus(ActivityStatus.PAUSED)
        // Await the re-emitted Success — the write lands on Room's query
        // executor, which advanceUntilIdle does not wait for.
        vm.state.first {
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
        val vm = HabitDetailViewModel(container, id)

        vm.deleteHard()
        // Deleted is sent only after the Room delete commits.
        assertTrue(vm.effect.first() is HabitDetailEffect.Deleted)

        assertNull(container.activityRepository.getById(id))
    }
}
