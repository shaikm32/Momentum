package com.momentum.app.presentation.screens.creation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.AppContainer
import com.momentum.app.data.local.ActivityEntity
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.ReduceMode
import com.momentum.app.notification.ReminderScheduler
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

/** No-op scheduler for ViewModel/persistence tests (see setUp). */
private class NoopReminderScheduler : ReminderScheduler {
    override suspend fun scheduleForActivity(activityId: Long) = Unit
    override suspend fun cancelForActivity(activityId: Long) = Unit
}

/** Deterministic save-pipeline failure injector (see the save-failure test). */
private class ThrowingReminderScheduler : ReminderScheduler {
    override suspend fun scheduleForActivity(activityId: Long): Unit =
        throw IllegalStateException("simulated scheduler failure")
    override suspend fun cancelForActivity(activityId: Long) = Unit
}

/**
 * Creation-flow tests through the real persistence path (Robolectric +
 * real AppContainer/Room, mirroring
 * [com.momentum.app.data.local.ActivityHistoryPreservedTest]).
 *
 * Covers the whole guided flow: habit intents (Build/Reduce, with Reduce
 * Limit/Completely modes), habit templates, habit configuration
 * persistence, task creation, ActivityType/Intent persistence, edit
 * preservation (no Habit ↔ Task conversion, no data loss), validation,
 * defaults, and reminder behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CreationViewModelTest {

    private lateinit var container: AppContainer

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase("momentum.db")
        // The real scheduler drives AlarmManager/PendingIntent through the app
        // container — framework surface these persistence/effect tests must not
        // depend on. Scheduling logic stays covered by AlarmPlanningTest's
        // fake-gateway tests.
        container = AppContainer(context, reminderSchedulerOverride = NoopReminderScheduler())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ——— Defaults ———

    @Test
    fun `build habit creation defaults to HABIT BUILD DAILY binary`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)

        assertTrue(vm.form.value.isNew)
        assertEquals(ActivityType.HABIT, vm.form.value.activityType)
        assertEquals(HabitIntent.BUILD, vm.form.value.habitIntent)
        assertEquals(RecurrenceChoice.DAILY, vm.form.value.recurrenceChoice)
        assertTrue(vm.form.value.buildBinary)
    }

    @Test
    fun `reduce habit creation defaults to REDUCE DAILY`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.REDUCE)

        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals(RecurrenceChoice.DAILY, vm.form.value.recurrenceChoice)
    }

    @Test
    fun `reduce habit creation defaults to REDUCE LIMIT mode`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.REDUCE)

        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals(ReduceMode.LIMIT, vm.form.value.reduceMode)
        assertEquals(RecurrenceChoice.DAILY, vm.form.value.recurrenceChoice)
    }

    @Test
    fun `task creation defaults to TASK ONCE due today without repeat`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val vm = CreationViewModel(container, null, ActivityType.TASK)

        assertTrue(vm.form.value.isNew)
        assertEquals(ActivityType.TASK, vm.form.value.activityType)
        assertEquals(RecurrenceChoice.ONCE, vm.form.value.recurrenceChoice)
        assertEquals(DueOption.TODAY, vm.form.value.dueOption)
        assertEquals(LocalDate.now(), vm.form.value.dueDate)
        assertFalse(vm.form.value.repeatEnabled)
        assertFalse(vm.form.value.hasDueTime)
        assertNull(vm.form.value.dueTime)
    }

    // ——— Habit creation persistence ———

    @Test
    fun `saving binary build habit persists BUILD with default target`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update { it.copy(title = "Meditate") }

        vm.save()
        advanceUntilIdle()

        val effect = vm.effect.first()
        assertTrue(effect is CreationEffect.Saved)
        val stored = container.activityRepository.getById((effect as CreationEffect.Saved).activityId)!!
        assertEquals("Meditate", stored.title)
        assertEquals(ActivityType.HABIT, stored.activityType)
        assertEquals(HabitIntent.BUILD, stored.habitIntent)
        assertEquals(Recurrence.Daily, stored.recurrence)
        assertEquals(1.0, stored.targetPerOccurrence, 0.0)
        assertEquals("", stored.unit)
    }

    @Test
    fun `saving target build habit persists amount and unit`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update { it.copy(title = "Read", buildBinary = false, amount = "30", unit = "pages") }

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository
            .getById((vm.effect.first() as CreationEffect.Saved).activityId)!!
        assertEquals(HabitIntent.BUILD, stored.habitIntent)
        assertEquals(30.0, stored.targetPerOccurrence, 0.0)
        assertEquals("pages", stored.unit)
    }

    @Test
    fun `saving reduce habit persists REDUCE with limit`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.REDUCE)
        vm.update {
            it.copy(
                title = "Social media",
                amount = "60",
                unit = "min",
                recurrenceChoice = RecurrenceChoice.DAILY,
                reminderEnabled = true,
                reminderTime = "21:00",
            )
        }

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository
            .getById((vm.effect.first() as CreationEffect.Saved).activityId)!!
        assertEquals(ActivityType.HABIT, stored.activityType)
        assertEquals(HabitIntent.REDUCE, stored.habitIntent)
        assertEquals(60.0, stored.targetPerOccurrence, 0.0)
        assertEquals("min", stored.unit)
        val reminders = container.reminderRepository.forActivity(stored.id)
        assertEquals(1, reminders.size)
        assertEquals("21:00", reminders.single().timeLocal)
    }

    @Test
    fun `saving reduce completely habit persists REDUCE with zero target`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.REDUCE)
        vm.update { it.copy(title = "Smoking", reduceMode = ReduceMode.COMPLETE) }

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository
            .getById((vm.effect.first() as CreationEffect.Saved).activityId)!!
        assertEquals(HabitIntent.REDUCE, stored.habitIntent)
        assertEquals(ReduceMode.COMPLETE, stored.reduceMode())
        assertEquals(1.0, stored.targetPerOccurrence, 0.0)
        assertEquals("", stored.unit)
        assertEquals(Recurrence.Daily, stored.recurrence)
    }

    @Test
    fun `saving build habit with custom recurrence persists the rule`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update {
            it.copy(
                title = "Gym",
                recurrenceChoice = RecurrenceChoice.CUSTOM_DAYS,
                days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            )
        }

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository
            .getById((vm.effect.first() as CreationEffect.Saved).activityId)!!
        assertEquals(
            Recurrence.SelectedWeekdays(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
            stored.recurrence,
        )
    }

    // ——— Task creation persistence ———

    @Test
    fun `saving one-time task persists TASK with due date and no due time`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.TASK)
        vm.update { it.copy(title = "Pay rent", reminderEnabled = true, reminderTime = "18:30") }

        vm.save()
        advanceUntilIdle()

        val effect = vm.effect.first()
        assertTrue(effect is CreationEffect.Saved)
        val stored = container.activityRepository.getById((effect as CreationEffect.Saved).activityId)!!
        assertEquals(ActivityType.TASK, stored.activityType)
        assertEquals(Recurrence.Once, stored.recurrence)
        assertEquals(LocalDate.now(), stored.startDate)
        assertNull(stored.dueTime)
        val reminders = container.reminderRepository.forActivity(stored.id)
        assertEquals(1, reminders.size)
        assertEquals("18:30", reminders.single().timeLocal)
    }

    @Test
    fun `saving task with due time and repeat persists both`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.TASK)
        val due = LocalDate.now().plusDays(3)
        vm.update {
            it.copy(
                title = "Take car for service",
                dueOption = DueOption.PICK_DATE,
                dueDate = due,
                hasDueTime = true,
                dueTimePicker = "09:00",
                repeatEnabled = true,
                recurrenceChoice = RecurrenceChoice.MONTHLY,
                dayOfMonth = 15,
            )
        }

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository
            .getById((vm.effect.first() as CreationEffect.Saved).activityId)!!
        assertEquals(ActivityType.TASK, stored.activityType)
        assertEquals(due, stored.startDate)
        assertEquals("09:00", stored.dueTime)
        assertEquals(Recurrence.Monthly(15), stored.recurrence)
    }

    @Test
    fun `saving without reminder stores no reminder rows`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update { it.copy(title = "No nudge") }

        vm.save()
        advanceUntilIdle()

        val effect = vm.effect.first() as CreationEffect.Saved
        assertTrue(container.reminderRepository.forActivity(effect.activityId).isEmpty())
    }

    // ——— Validation ———

    @Test
    fun `blank title shows inline error and saves nothing`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.TASK)
        vm.update { it.copy(title = "   ") }

        vm.save()
        advanceUntilIdle()

        assertEquals("Give it a name.", vm.form.value.titleError)
        assertFalse(vm.form.value.isSaving)
    }

    @Test
    fun `overlong title shows inline error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update { it.copy(title = "x".repeat(121)) }

        vm.save()
        advanceUntilIdle()

        assertEquals("Keep it under 120 characters.", vm.form.value.titleError)
        assertFalse(vm.form.value.isSaving)
    }

    @Test
    fun `target build with bad amount shows measure error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update { it.copy(title = "Read", buildBinary = false, amount = "zero", unit = "pages") }

        vm.save()
        advanceUntilIdle()

        assertEquals("Enter an amount greater than 0.", vm.form.value.measureError)
        assertFalse(vm.form.value.isSaving)
    }

    @Test
    fun `reduce without unit shows measure error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.REDUCE)
        vm.update { it.copy(title = "Sugar", amount = "2", unit = "") }

        vm.save()
        advanceUntilIdle()

        assertEquals("Pick a unit.", vm.form.value.measureError)
        assertFalse(vm.form.value.isSaving)
    }

    @Test
    fun `end before start emits error effect`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val today = LocalDate.of(2026, 1, 5)
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update {
            it.copy(
                title = "Bad window",
                hasEndDate = true,
                startDate = today,
                endDate = today.minusDays(1),
            )
        }

        vm.save()
        advanceUntilIdle()

        assertTrue(vm.effect.first() is CreationEffect.Error)
    }

    // ——— CTA enabled state ———

    @Test
    fun `isSubmittable mirrors validation`() {
        val base = CreationForm(title = "Read")
        assertTrue(base.isSubmittable())
        assertFalse(base.copy(title = "  ").isSubmittable())
        assertFalse(base.copy(title = "x".repeat(121)).isSubmittable())
        val target = base.copy(buildBinary = false, amount = "30", unit = "min")
        assertTrue(target.isSubmittable())
        assertFalse(target.copy(amount = "0").isSubmittable())
        assertFalse(target.copy(unit = "").isSubmittable())
        val reduce = base.copy(habitIntent = HabitIntent.REDUCE, amount = "60", unit = "min")
        assertTrue(reduce.isSubmittable())
        assertFalse(reduce.copy(unit = "").isSubmittable())
        val complete = base.copy(habitIntent = HabitIntent.REDUCE, reduceMode = ReduceMode.COMPLETE)
        assertTrue(complete.isSubmittable())
        assertFalse(
            base.copy(hasEndDate = true, endDate = base.startDate.minusDays(1)).isSubmittable(),
        )
    }

    // ——— Editing: habits ———

    @Test
    fun `editing reduce habit loads and preserves intent and configuration`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Social media",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.REDUCE,
                startDate = LocalDate.of(2026, 1, 5),
                recurrence = Recurrence.Weekdays,
                targetPerOccurrence = 60.0,
                unit = "min",
                priority = 2,
                notes = "Weekdays only",
            ),
            emptyList(),
        )

        // Default creation args are HABIT/BUILD — the persisted REDUCE must win.
        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Social media" }
        assertEquals(ActivityType.HABIT, vm.form.value.activityType)
        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals(RecurrenceChoice.WEEKDAYS, vm.form.value.recurrenceChoice)
        assertEquals("60", vm.form.value.amount)
        assertEquals("min", vm.form.value.unit)

        vm.update { it.copy(title = "Less social media") }
        vm.save()
        advanceUntilIdle()

        val effect = vm.effect.first()
        assertTrue(effect is CreationEffect.Saved)
        assertEquals(id, (effect as CreationEffect.Saved).activityId)
        val stored = container.activityRepository.getById(id)!!
        assertEquals("Less social media", stored.title)
        assertEquals(ActivityType.HABIT, stored.activityType)
        assertEquals(HabitIntent.REDUCE, stored.habitIntent)
        assertEquals(60.0, stored.targetPerOccurrence, 0.0)
        assertEquals("min", stored.unit)
        assertEquals(Recurrence.Weekdays, stored.recurrence)
        assertEquals(2, stored.priority)
        assertEquals("Weekdays only", stored.notes)
    }

    // ——— Templates ———

    @Test
    fun `build template selection populates defaults`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)

        vm.selectTemplate(BuildTemplates.first { it.id == "build_read" })

        assertEquals("build_read", vm.form.value.templateId)
        assertEquals("Read books", vm.form.value.title)
        assertEquals("learning", vm.form.value.iconKey)
        assertFalse(vm.form.value.buildBinary)
        assertEquals("30", vm.form.value.amount)
        assertEquals("pages", vm.form.value.unit)
    }

    @Test
    fun `reduce template selection populates defaults`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)

        vm.selectIntent(HabitIntent.REDUCE)
        vm.selectTemplate(ReduceTemplates.first { it.id == "reduce_social" })

        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals("reduce_social", vm.form.value.templateId)
        assertEquals("Social media", vm.form.value.title)
        assertEquals("people", vm.form.value.iconKey)
        assertEquals(ReduceMode.LIMIT, vm.form.value.reduceMode)
        assertEquals("60", vm.form.value.amount)
        assertEquals("min", vm.form.value.unit)
    }

    @Test
    fun `custom habit selection clears the name for free-form entry`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.selectTemplate(BuildTemplates.first { it.id == "build_read" })

        vm.selectTemplate(BuildTemplates.first { it.isCustom })

        assertEquals(CUSTOM_BUILD_TEMPLATE_ID, vm.form.value.templateId)
        assertEquals("", vm.form.value.title)
        assertEquals("spark", vm.form.value.iconKey)
    }

    @Test
    fun `switching intent clears the template selection but keeps typed input`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.selectTemplate(BuildTemplates.first { it.id == "build_read" })

        vm.selectIntent(HabitIntent.REDUCE)

        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals(null, vm.form.value.templateId)
        assertEquals("Read books", vm.form.value.title)
    }

    @Test
    fun `editing reduce limit habit preserves LIMIT mode`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Social media",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.REDUCE,
                startDate = LocalDate.of(2026, 1, 5),
                targetPerOccurrence = 60.0,
                unit = "min",
            ),
            emptyList(),
        )

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Social media" }
        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals(ReduceMode.LIMIT, vm.form.value.reduceMode)
        assertEquals("60", vm.form.value.amount)
        assertEquals("min", vm.form.value.unit)

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository.getById(id)!!
        assertEquals(HabitIntent.REDUCE, stored.habitIntent)
        assertEquals(ReduceMode.LIMIT, stored.reduceMode())
        assertEquals(60.0, stored.targetPerOccurrence, 0.0)
        assertEquals("min", stored.unit)
    }

    @Test
    fun `matchTemplate falls back to custom for free-form habits`() {
        assertEquals("build_read", matchTemplate(HabitIntent.BUILD, "Read books", "learning"))
        assertEquals(CUSTOM_BUILD_TEMPLATE_ID, matchTemplate(HabitIntent.BUILD, "My own thing", "spark"))
        assertEquals(CUSTOM_REDUCE_TEMPLATE_ID, matchTemplate(HabitIntent.REDUCE, "My own thing", "spark"))
    }

    @Test
    fun `saving from a predefined template persists habitTemplateId`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = CreationViewModel(container, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.selectTemplate(BuildTemplates.first { it.id == "build_read" })
        // Rename before save — identity must still be the template id.
        vm.update { it.copy(title = "Night reading") }

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository
            .getById((vm.effect.first() as CreationEffect.Saved).activityId)!!
        assertEquals("build_read", stored.habitTemplateId)
        assertEquals("Night reading", stored.title)
    }

    @Test
    fun `renaming a predefined habit keeps its icon lock via persisted templateId`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Read books",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.BUILD,
                iconKey = "learning",
                habitTemplateId = "build_read",
                startDate = LocalDate.of(2026, 1, 5),
            ),
            emptyList(),
        )

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Read books" }
        // Rename no longer matches matchTemplate(title, icon) — persisted id must win.
        vm.update { it.copy(title = "Evening pages") }
        vm.save()
        // Await the Saved effect — advanceUntilIdle can return before Room's
        // query executor commits the write.
        assertTrue(vm.effect.first() is CreationEffect.Saved)

        val stored = container.activityRepository.getById(id)!!
        assertEquals("Evening pages", stored.title)
        assertEquals("build_read", stored.habitTemplateId)
        // Reload: templateId still non-custom → showIconEditor stays false.
        val reloaded = CreationViewModel(container, id)
        reloaded.form.first { it.title == "Evening pages" }
        assertEquals("build_read", reloaded.form.value.templateId)
        assertEquals(
            false,
            templateById(HabitIntent.BUILD, reloaded.form.value.templateId)?.isCustom == true,
        )
    }

    @Test
    fun `legacy pre-v4 rows fall back to matchTemplate when habitTemplateId is null`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Read books",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.BUILD,
                iconKey = "learning",
                habitTemplateId = null,
                startDate = LocalDate.of(2026, 1, 5),
            ),
            emptyList(),
        )

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Read books" }
        assertEquals("build_read", vm.form.value.templateId)
    }

    @Test
    fun `legacy avoid rows load as REDUCE COMPLETE without crashing`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        // Raw v3 row as written before AVOID was folded into REDUCE.
        val id = container.database.activityDao().insert(
            ActivityEntity(
                title = "Smoke-free",
                activityType = "HABIT",
                habitIntent = "AVOID",
                startDate = "2026-01-05",
                recurrenceType = "DAILY",
            ),
        )

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Smoke-free" }
        assertEquals(HabitIntent.REDUCE, vm.form.value.habitIntent)
        assertEquals(ReduceMode.COMPLETE, vm.form.value.reduceMode)

        vm.update { it.copy(notes = "One day at a time") }
        vm.save()
        advanceUntilIdle()

        val effect = vm.effect.first()
        assertTrue(effect is CreationEffect.Saved)
        val stored = container.activityRepository.getById(id)!!
        assertEquals(HabitIntent.REDUCE, stored.habitIntent)
        assertEquals(ReduceMode.COMPLETE, stored.reduceMode())
        assertEquals("One day at a time", stored.notes)
    }

    @Test
    fun `editing build target habit keeps binary off with its values`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Read",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.BUILD,
                startDate = LocalDate.of(2026, 1, 5),
                targetPerOccurrence = 30.0,
                unit = "pages",
            ),
            emptyList(),
        )

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Read" }
        assertFalse(vm.form.value.buildBinary)
        assertEquals("30", vm.form.value.amount)
        assertEquals("pages", vm.form.value.unit)

        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository.getById(id)!!
        assertEquals(30.0, stored.targetPerOccurrence, 0.0)
        assertEquals("pages", stored.unit)
    }

    @Test
    fun `editing habit never converts to task`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Read",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.BUILD,
                startDate = LocalDate.of(2026, 1, 5),
            ),
            emptyList(),
        )

        // A TASK creation seed must not leak into the loaded habit.
        val vm = CreationViewModel(container, id, ActivityType.TASK)
        vm.form.first { it.title == "Read" }
        assertEquals(ActivityType.HABIT, vm.form.value.activityType)

        vm.update { it.copy(title = "Read more") }
        vm.save()
        advanceUntilIdle()

        assertEquals(ActivityType.HABIT, container.activityRepository.getById(id)!!.activityType)
    }

    @Test
    fun `editing habit preserves description and history`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Run",
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.BUILD,
                description = "legacy description",
                startDate = LocalDate.of(2026, 1, 5),
                recurrence = Recurrence.Daily,
            ),
            emptyList(),
        )
        container.completionRepository.complete(id, "2026-01-05", 1.0)
        container.completionRepository.complete(id, "2026-01-06", 0.5)

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Run" }
        vm.update { it.copy(title = "Morning run") }
        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository.getById(id)!!
        assertEquals("legacy description", stored.description)
        assertEquals(2, container.completionRepository.recordsForActivity(id).size)
    }

    // ——— Editing: tasks ———

    @Test
    fun `editing task loads and preserves type configuration and due time`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Old task",
                activityType = ActivityType.TASK,
                dueTime = "20:00",
                startDate = LocalDate.of(2026, 1, 5),
                recurrence = Recurrence.Once,
            ),
            emptyList(),
        )

        // Default creation args are HABIT/BUILD — the persisted TASK must win.
        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Old task" }
        assertEquals(ActivityType.TASK, vm.form.value.activityType)
        assertEquals(DueOption.PICK_DATE, vm.form.value.dueOption)
        assertEquals(LocalDate.of(2026, 1, 5), vm.form.value.dueDate)
        assertTrue(vm.form.value.hasDueTime)
        assertEquals("20:00", vm.form.value.dueTimePicker)
        assertFalse(vm.form.value.repeatEnabled)

        vm.update { it.copy(title = "Renamed task") }
        vm.save()
        advanceUntilIdle()

        val effect = vm.effect.first()
        assertTrue(effect is CreationEffect.Saved)
        assertEquals(id, (effect as CreationEffect.Saved).activityId)
        val stored = container.activityRepository.getById(id)!!
        assertEquals("Renamed task", stored.title)
        assertEquals(ActivityType.TASK, stored.activityType)
        assertEquals("20:00", stored.dueTime)
        assertEquals(Recurrence.Once, stored.recurrence)
    }

    @Test
    fun `editing repeating task preserves its rule`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Pay rent",
                activityType = ActivityType.TASK,
                startDate = LocalDate.of(2026, 2, 1),
                recurrence = Recurrence.Monthly(1),
            ),
            emptyList(),
        )

        val vm = CreationViewModel(container, id)
        vm.form.first { it.title == "Pay rent" }
        assertTrue(vm.form.value.repeatEnabled)
        assertEquals(RecurrenceChoice.MONTHLY, vm.form.value.recurrenceChoice)

        vm.update { it.copy(notes = "Transfer on the 1st") }
        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository.getById(id)!!
        assertEquals(ActivityType.TASK, stored.activityType)
        assertEquals(Recurrence.Monthly(1), stored.recurrence)
    }

    @Test
    fun `editing task never converts to habit and preserves legacy target`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val id = container.activityRepository.saveWithReminders(
            Activity(
                title = "Measured task",
                activityType = ActivityType.TASK,
                startDate = LocalDate.of(2026, 1, 5),
                recurrence = Recurrence.Once,
                targetPerOccurrence = 5.0,
                unit = "km",
            ),
            emptyList(),
        )

        val vm = CreationViewModel(container, id, ActivityType.HABIT, HabitIntent.REDUCE)
        vm.form.first { it.title == "Measured task" }
        assertEquals(ActivityType.TASK, vm.form.value.activityType)

        vm.update { it.copy(title = "Measured task v2") }
        vm.save()
        advanceUntilIdle()

        val stored = container.activityRepository.getById(id)!!
        assertEquals(ActivityType.TASK, stored.activityType)
        assertEquals(5.0, stored.targetPerOccurrence, 0.0)
        assertEquals("km", stored.unit)
    }

    @Test
    fun `save failure emits error and resets saving flag`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        // Inject the failure through the scheduler seam: closing the database
        // does not fail a save (Room reopens the file on next access), so a
        // throwing scheduler is the deterministic way to exercise save()'s
        // catch path and the isSaving reset. Assertions unchanged.
        val failing = AppContainer(
            ApplicationProvider.getApplicationContext(),
            reminderSchedulerOverride = ThrowingReminderScheduler(),
        )
        val vm = CreationViewModel(failing, null, ActivityType.HABIT, HabitIntent.BUILD)
        vm.update { it.copy(title = "Doomed") }

        vm.save()
        advanceUntilIdle()

        assertTrue(vm.effect.first() is CreationEffect.Error)
        assertFalse(vm.form.value.isSaving)
    }
}
