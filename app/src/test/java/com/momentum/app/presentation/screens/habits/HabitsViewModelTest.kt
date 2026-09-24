package com.momentum.app.presentation.screens.habits

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.Reminder
import com.momentum.app.notification.ReminderScheduler
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class HabitsViewModelTest {

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

    private suspend fun saveHabit(
        title: String,
        status: ActivityStatus = ActivityStatus.ACTIVE,
        target: Double = 1.0,
        unit: String = "",
        reminder: String? = null,
    ): Long {
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = title,
                activityType = ActivityType.HABIT,
                startDate = LocalDate.of(2026, 1, 5),
                status = status,
                targetPerOccurrence = target,
                unit = unit,
            ),
            reminder?.let { listOf(Reminder(timeLocal = it, enabled = true, activityId = 0)) } ?: emptyList(),
        )
        return id
    }

    @Test
    fun `empty state when no habits exist`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        // Seed only a task — habits list must stay empty.
        container.activityRepository.saveWithReminders(
            Activity(
                title = "Solo task",
                activityType = ActivityType.TASK,
                startDate = LocalDate.of(2026, 1, 5),
                recurrence = Recurrence.Once,
            ),
            emptyList(),
        )
        val vm = HabitsViewModel(container)
        // Initial state has isEmpty=false + empty items; wait for the real emission.
        val state = vm.state.first { it.isEmpty }
        assertTrue(state.items.isEmpty())
        assertTrue(state.isEmpty)
    }

    @Test
    fun `shows active habits with metadata row and ordered active before paused`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        saveHabit("Paused one", status = ActivityStatus.PAUSED)
        saveHabit("Active target", target = 30.0, unit = "pages", reminder = "21:30")
        saveHabit("Active binary")

        val vm = HabitsViewModel(container)
        val state = vm.state.first { it.items.size == 3 }

        assertFalse(state.isEmpty)
        // Same-priority ties break alphabetically after Active-before-Paused.
        assertEquals(listOf("Active binary", "Active target", "Paused one"), state.items.map { it.activity.title })
        assertEquals("Daily · — · —", state.items[0].metadata)
        assertEquals("Daily · 30 pages · 21:30", state.items[1].metadata)
        assertNull(state.items[0].reminderTime)
        assertEquals("21:30", state.items[1].reminderTime)
    }

    @Test
    fun `pause then resume updates status without deleting`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = saveHabit("Go away")
        val vm = HabitsViewModel(container)
        // Wait for a real emission that includes the habit before pausing —
        // the stateIn initial value is empty and would never match PAUSED.
        vm.state.first { s -> s.items.any { it.activity.id == id } }

        vm.pause(id)
        // Room writes land on its query executor; wait for the list state
        // to re-emit with PAUSED (advanceUntilIdle alone can return early).
        vm.state.first { s ->
            s.items.any { it.activity.id == id && it.activity.status == ActivityStatus.PAUSED }
        }
        assertEquals(ActivityStatus.PAUSED, container.activityRepository.getById(id)!!.status)

        vm.resume(id)
        vm.state.first { s ->
            s.items.any { it.activity.id == id && it.activity.status == ActivityStatus.ACTIVE }
        }
        assertEquals(ActivityStatus.ACTIVE, container.activityRepository.getById(id)!!.status)
    }

    @Test
    fun `deleteHard removes the habit`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = saveHabit("Doomed")
        val vm = HabitsViewModel(container)
        // Land on a real emission that includes the habit first — the
        // stateIn initial value is empty and would satisfy `none` too early.
        vm.state.first { s -> s.items.any { it.activity.id == id } }

        vm.deleteHard(id)
        vm.state.first { s -> s.items.none { it.activity.id == id } }

        assertNull(container.activityRepository.getById(id))
    }
}
