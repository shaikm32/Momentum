package com.momentum.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.data.local.MomentumDatabase
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.Reminder
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * P1#1: the user-initiated reset must clear user data completely or not at
 * all, in a single transaction — never partially (e.g. activities gone but
 * orphan completions behind). Categories are intentionally kept.
 */
@RunWith(RobolectricTestRunner::class)
class UserDataResetTest {

    private lateinit var db: MomentumDatabase
    private lateinit var activities: RoomActivityRepository
    private lateinit var completions: RoomCompletionRepository
    private lateinit var reminders: RoomReminderRepository
    private lateinit var categories: RoomCategoryRepository
    private lateinit var reset: UserDataReset

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MomentumDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        activities = RoomActivityRepository(db.activityDao(), db.reminderDao(), db)
        completions = RoomCompletionRepository(db.completionDao())
        reminders = RoomReminderRepository(db.reminderDao())
        categories = RoomCategoryRepository(db.categoryDao())
        reset = UserDataReset(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `reset clears activities, completions and reminders but keeps categories`() = runTest {
        val id = activities.saveWithReminders(
            Activity(title = "Run", startDate = LocalDate.of(2026, 1, 5)),
            listOf(Reminder(activityId = 0, timeLocal = "07:00", enabled = true)),
        )
        completions.complete(id, "2026-01-05", 1.0)
        categories.upsert(Category(name = "Health", isDefault = true))

        reset.clearAllUserData()

        assertTrue(db.activityDao().observeAll().first().isEmpty())
        assertTrue(completions.recordsForActivity(id).isEmpty())
        assertTrue(reminders.forActivity(id).isEmpty())
        assertEquals(listOf("Health"), categories.all().map { it.name })
    }

    @Test
    fun `reset on an empty database is a no-op`() = runTest {
        reset.clearAllUserData()

        assertTrue(db.activityDao().observeAll().first().isEmpty())
    }
}
