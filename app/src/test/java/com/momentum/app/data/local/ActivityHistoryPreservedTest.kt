package com.momentum.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.data.repository.RoomActivityRepository
import com.momentum.app.data.repository.RoomCompletionRepository
import com.momentum.app.data.repository.RoomReminderRepository
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.Reminder
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * P0#1 regression: editing an activity must UPDATE the row in place.
 *
 * The previous implementation used INSERT OR REPLACE, which deletes the
 * conflicting activity row — firing the ON DELETE CASCADE foreign keys and
 * silently wiping completion history, streaks and reminders on every edit.
 * These tests run against the real Room database (in-memory) through the real
 * repositories, so they fail if the DAO ever destroys child rows on edit.
 */
@RunWith(RobolectricTestRunner::class)
class ActivityHistoryPreservedTest {

    private lateinit var db: MomentumDatabase
    private lateinit var activities: RoomActivityRepository
    private lateinit var completions: RoomCompletionRepository
    private lateinit var reminders: RoomReminderRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MomentumDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        activities = RoomActivityRepository(db.activityDao())
        completions = RoomCompletionRepository(db.completionDao())
        reminders = RoomReminderRepository(db.reminderDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `new activity inserts with generated id`() = runTest {
        val id = activities.upsert(Activity(title = "Read", startDate = LocalDate.of(2026, 1, 5)))

        assertTrue(id > 0)
        val stored = activities.getById(id)!!
        assertEquals(id, stored.id)
        assertEquals("Read", stored.title)
        assertTrue(stored.createdAtEpochMs > 0)
    }

    @Test
    fun `editing activity keeps id and all completion history`() = runTest {
        val id = activities.upsert(
            Activity(title = "Run", startDate = LocalDate.of(2026, 1, 5), recurrence = Recurrence.Daily),
        )
        completions.complete(id, "2026-01-05", 1.0)
        completions.complete(id, "2026-01-06", 0.5)
        completions.complete(id, "2026-01-07", 1.0)
        assertEquals(3, completions.recordsForActivity(id).size)

        val before = activities.getById(id)!!
        val editedId = activities.upsert(
            before.copy(
                title = "Morning run",
                recurrence = Recurrence.SelectedWeekdays(
                    setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                ),
                priority = 2,
                notes = "Easy pace",
            ),
        )

        // Same row, updated fields.
        assertEquals(id, editedId)
        val stored = activities.getById(id)!!
        assertEquals(id, stored.id)
        assertEquals("Morning run", stored.title)
        assertEquals(2, stored.priority)
        assertEquals("Easy pace", stored.notes)

        // Every previous completion still exists, untouched.
        val records = completions.recordsForActivity(id)
        assertEquals(3, records.size)
        assertEquals(
            listOf("2026-01-05", "2026-01-06", "2026-01-07"),
            records.map { it.occurrenceDate },
        )
        assertEquals(listOf(1.0, 0.5, 1.0), records.map { it.value })
    }

    @Test
    fun `editing activity keeps its reminders`() = runTest {
        val id = activities.upsert(
            Activity(title = "Meds", startDate = LocalDate.of(2026, 1, 5)),
        )
        reminders.upsert(Reminder(activityId = id, timeLocal = "08:00", enabled = true))
        assertEquals(1, reminders.forActivity(id).size)

        val before = activities.getById(id)!!
        activities.upsert(before.copy(title = "Morning meds"))

        val rows = reminders.forActivity(id)
        assertEquals(1, rows.size)
        assertEquals("08:00", rows.single().timeLocal)
    }

    @Test
    fun `editing activity preserves createdAt`() = runTest {
        val id = activities.upsert(Activity(title = "Journal", startDate = LocalDate.of(2026, 1, 5)))
        val createdAt = activities.getById(id)!!.createdAtEpochMs

        // The edit form rebuilds the model without timestamps (createdAt = 0);
        // the repository must not let that rewrite history metadata.
        activities.upsert(Activity(id = id, title = "Evening journal", startDate = LocalDate.of(2026, 1, 5)))

        assertEquals(createdAt, activities.getById(id)!!.createdAtEpochMs)
    }
}
