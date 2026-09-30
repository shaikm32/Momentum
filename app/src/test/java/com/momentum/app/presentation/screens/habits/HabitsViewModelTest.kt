package com.momentum.app.presentation.screens.habits

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.momentum.app.TestGraph
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.Reminder
import com.momentum.app.habitsViewModel
import com.momentum.app.notification.ReminderScheduler
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
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
        val vm = container.habitsViewModel()
        // Initial state has isEmpty=false + empty items; wait for the real emission.
        val state = awaitState(vm.state) { it.isEmpty }
        assertTrue(state.items.isEmpty())
        assertTrue(state.isEmpty)
    }

    @Test
    fun `shows active habits with the data its metadata row is built from`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        saveHabit("Paused one", status = ActivityStatus.PAUSED)
        saveHabit("Active target", target = 30.0, unit = "pages", reminder = "21:30")
        saveHabit("Active binary")

        val vm = container.habitsViewModel()
        val state = awaitState(vm.state) { it.items.size == 3 }

        assertFalse(state.isEmpty)
        // Same-priority ties break alphabetically after Active-before-Paused.
        assertEquals(listOf("Active binary", "Active target", "Paused one"), state.items.map { it.activity.title })
        // The `Frequency · Target · Reminder` line is composed by the screen
        // (Phase E1b), so the ViewModel hands over the structured inputs only.
        assertEquals(Recurrence.Daily, state.items[0].activity.recurrence)
        assertEquals(1.0, state.items[0].activity.targetPerOccurrence, 0.0)
        assertEquals("", state.items[0].activity.unit)
        assertEquals(30.0, state.items[1].activity.targetPerOccurrence, 0.0)
        assertEquals("pages", state.items[1].activity.unit)
        assertNull(state.items[0].reminderTime)
        assertEquals("21:30", state.items[1].reminderTime)
    }

    @Test
    fun `the metadata row is composed in the screen from a format resource`() {
        val screen = File("src/main/java/com/momentum/app/presentation/screens/habits/HabitsScreen.kt")
            .readText()
        val strings = File("src/main/res/values/strings.xml").readText()
        // Three fixed slots so every card stays aligned, joined by a middot.
        assertTrue(
            "strings.xml must still carry <string name=\"habits_metadata\">%1\$s · %2\$s · %3\$s</string>",
            strings.contains("<string name=\"habits_metadata\">%1\$s · %2\$s · %3\$s</string>"),
        )
        assertTrue(
            "the card must read the frequency, target and reminder through one resource",
            screen.contains("R.string.habits_metadata") &&
                screen.contains("recurrenceLabel(activity.recurrence)") &&
                screen.contains("formatAmount(activity.targetPerOccurrence)") &&
                screen.contains("item.reminderTime"),
        )
        val viewModel =
            File("src/main/java/com/momentum/app/presentation/screens/habits/HabitsViewModel.kt").readText()
        assertFalse(
            "the ViewModel must not build the metadata line or call display logic",
            viewModel.contains("recurrenceLabel") || viewModel.contains("formatAmount"),
        )
    }

    @Test
    fun `pause then resume updates status without deleting`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = saveHabit("Go away")
        val vm = container.habitsViewModel()
        // Wait for a real emission that includes the habit before pausing —
        // the stateIn initial value is empty and would never match PAUSED.
        awaitState(vm.state) { s -> s.items.any { it.activity.id == id } }

        vm.pause(id)
        // Room writes land on its query executor; wait for the list state
        // to re-emit with PAUSED (advanceUntilIdle alone can return early).
        awaitState(vm.state) { s ->
            s.items.any { it.activity.id == id && it.activity.status == ActivityStatus.PAUSED }
        }
        assertEquals(ActivityStatus.PAUSED, container.activityRepository.getById(id)!!.status)

        vm.resume(id)
        awaitState(vm.state) { s ->
            s.items.any { it.activity.id == id && it.activity.status == ActivityStatus.ACTIVE }
        }
        assertEquals(ActivityStatus.ACTIVE, container.activityRepository.getById(id)!!.status)
    }

    @Test
    fun `deleteHard removes the habit`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = saveHabit("Doomed")
        val vm = container.habitsViewModel()
        // Land on a real emission that includes the habit first — the
        // stateIn initial value is empty and would satisfy `none` too early.
        awaitState(vm.state) { s -> s.items.any { it.activity.id == id } }

        vm.deleteHard(id)
        awaitState(vm.state) { s -> s.items.none { it.activity.id == id } }

        assertNull(container.activityRepository.getById(id))
    }
}

/**
 * Awaits a [HabitsUiState] matching [predicate] through Turbine (Phase D) —
 * replaces the hand-rolled `state.first { … }` collection.
 */
private suspend fun awaitState(
    flow: StateFlow<HabitsUiState>,
    predicate: (HabitsUiState) -> Boolean,
): HabitsUiState {
    var matched: HabitsUiState? = null
    flow.test {
        var item = awaitItem()
        while (!predicate(item)) item = awaitItem()
        matched = item
    }
    return requireNotNull(matched) { "state flow completed without a matching emission" }
}
