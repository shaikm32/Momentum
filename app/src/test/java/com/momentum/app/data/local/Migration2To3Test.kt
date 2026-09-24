package com.momentum.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.data.repository.RoomActivityRepository
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.Recurrence
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Creation-flow migration test (v2 → v3): a database created with the exact
 * v2 schema (no `habitIntent`/`dueTime` columns) migrates cleanly — existing
 * rows keep every field, gain `habitIntent = BUILD` and null `dueTime`, and
 * children (completions, reminders) survive. New intent/task rows round-trip
 * through the migrated schema.
 *
 * Uses raw SQLite for the v2 fixture (no room-testing dependency — consistent
 * with the existing Robolectric + real-Room test setup).
 */
@RunWith(RobolectricTestRunner::class)
class Migration2To3Test {

    private val dbName = "migration-2-3-test.db"

    private fun context() = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        context().deleteDatabase(dbName)
    }

    @After
    fun tearDown() {
        context().deleteDatabase(dbName)
    }

    @Test
    fun `v2 rows migrate with BUILD default null dueTime and intact data`() = runTest {
        createV2DatabaseWithSampleData()

        val db = Room.databaseBuilder(context(), MomentumDatabase::class.java, dbName)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
        try {
            val activities = RoomActivityRepository(db.activityDao(), db.reminderDao(), db)

            // Migrated habit: every v2 field intact, intent defaulted to BUILD.
            val stored = activities.getById(1)!!
            assertEquals("Morning run", stored.title)
            assertEquals(ActivityType.HABIT, stored.activityType)
            assertEquals(HabitIntent.BUILD, stored.habitIntent)
            assertNull(stored.dueTime)
            assertEquals(LocalDate.of(2026, 1, 5), stored.startDate)
            assertEquals(
                Recurrence.SelectedWeekdays(
                    setOf(java.time.DayOfWeek.MONDAY, java.time.DayOfWeek.WEDNESDAY, java.time.DayOfWeek.FRIDAY),
                ),
                stored.recurrence,
            )
            assertEquals(5.0, stored.targetPerOccurrence, 0.0)
            assertEquals("km", stored.unit)
            assertEquals(2, stored.priority)
            assertEquals("Easy pace", stored.notes)
            assertEquals("desc", stored.description)
            assertEquals(1000L, stored.createdAtEpochMs)

            // Migrated task: type kept, intent defaulted (ignored for tasks).
            val task = activities.getById(2)!!
            assertEquals(ActivityType.TASK, task.activityType)
            assertEquals(HabitIntent.BUILD, task.habitIntent)
            assertNull(task.dueTime)
            assertEquals(Recurrence.Once, task.recurrence)

            // Children survived the migration.
            assertEquals(listOf(5.0), db.completionDao().forActivity(1).map { it.value })
            assertEquals("07:30", db.reminderDao().forActivity(1).single().timeLocal)
            assertEquals("Health", db.categoryDao().all().single().name)

            // The migrated schema stores new intent + due-time rows correctly.
            val reduceId = activities.upsert(
                Activity(
                    title = "Social media",
                    activityType = ActivityType.HABIT,
                    habitIntent = HabitIntent.REDUCE,
                    startDate = LocalDate.of(2026, 2, 1),
                    targetPerOccurrence = 60.0,
                    unit = "min",
                ),
            )
            assertTrue(reduceId > 0)
            val reduce = activities.getById(reduceId)!!
            assertEquals(HabitIntent.REDUCE, reduce.habitIntent)
            assertEquals(60.0, reduce.targetPerOccurrence, 0.0)

            val timedId = activities.upsert(
                Activity(
                    title = "Call landlord",
                    activityType = ActivityType.TASK,
                    startDate = LocalDate.of(2026, 2, 3),
                    recurrence = Recurrence.Once,
                    dueTime = "20:00",
                ),
            )
            assertEquals("20:00", activities.getById(timedId)!!.dueTime)
        } finally {
            db.close()
        }
    }

    /** Creates a v2 database (exact v2 DDL from `schemas/.../2.json`) with one of everything. */
    private fun createV2DatabaseWithSampleData() {
        val raw = context().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null)
        try {
            raw.execSQL(
                "CREATE TABLE IF NOT EXISTS `activities` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`title` TEXT NOT NULL, `activityType` TEXT NOT NULL, `description` TEXT NOT NULL, " +
                    "`iconKey` TEXT NOT NULL, `categoryId` INTEGER, `colorArgb` INTEGER, " +
                    "`startDate` TEXT NOT NULL, `endDate` TEXT, `recurrenceType` TEXT NOT NULL, " +
                    "`recurrencePayload` TEXT NOT NULL, `targetPerOccurrence` REAL NOT NULL, " +
                    "`unit` TEXT NOT NULL, `priority` INTEGER NOT NULL, `notes` TEXT NOT NULL, " +
                    "`status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)",
            )
            raw.execSQL("CREATE INDEX IF NOT EXISTS `index_activities_categoryId` ON `activities` (`categoryId`)")
            raw.execSQL("CREATE INDEX IF NOT EXISTS `index_activities_startDate` ON `activities` (`startDate`)")
            raw.execSQL("CREATE INDEX IF NOT EXISTS `index_activities_endDate` ON `activities` (`endDate`)")
            raw.execSQL("CREATE INDEX IF NOT EXISTS `index_activities_status` ON `activities` (`status`)")
            raw.execSQL(
                "CREATE TABLE IF NOT EXISTS `completion_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`activityId` INTEGER NOT NULL, `occurrenceDate` TEXT NOT NULL, `completedAt` INTEGER NOT NULL, " +
                    "`value` REAL NOT NULL, `note` TEXT NOT NULL, " +
                    "FOREIGN KEY(`activityId`) REFERENCES `activities`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            )
            raw.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_completion_records_activityId_occurrenceDate` " +
                    "ON `completion_records` (`activityId`, `occurrenceDate`)",
            )
            raw.execSQL(
                "CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`activityId` INTEGER NOT NULL, `timeLocal` TEXT NOT NULL, `daysMask` INTEGER NOT NULL, " +
                    "`enabled` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`activityId`) REFERENCES `activities`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            )
            raw.execSQL(
                "CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT NOT NULL, `iconKey` TEXT NOT NULL, `colorArgb` INTEGER, " +
                    "`sortOrder` INTEGER NOT NULL, `isDefault` INTEGER NOT NULL)",
            )
            raw.execSQL(
                "INSERT INTO activities (title, activityType, description, iconKey, categoryId, colorArgb, " +
                    "startDate, endDate, recurrenceType, recurrencePayload, targetPerOccurrence, unit, " +
                    "priority, notes, status, createdAt, updatedAt) VALUES " +
                    "('Morning run', 'HABIT', 'desc', 'run', NULL, NULL, '2026-01-05', NULL, " +
                    "'SELECTED_WEEKDAYS', '{\"days\":[1,3,5]}', 5.0, 'km', 2, 'Easy pace', 'ACTIVE', 1000, 2000), " +
                    "('Pay rent', 'TASK', '', 'list', NULL, NULL, '2026-02-01', NULL, " +
                    "'ONCE', '{}', 1.0, '', 1, '', 'ACTIVE', 1000, 2000)",
            )
            raw.execSQL(
                "INSERT INTO completion_records (activityId, occurrenceDate, completedAt, value, note) " +
                    "VALUES (1, '2026-01-05', 3000, 5.0, '')",
            )
            raw.execSQL(
                "INSERT INTO reminders (activityId, timeLocal, daysMask, enabled) " +
                    "VALUES (1, '07:30', 0, 1)",
            )
            raw.execSQL(
                "INSERT INTO categories (name, iconKey, colorArgb, sortOrder, isDefault) " +
                    "VALUES ('Health', 'health', NULL, 0, 1)",
            )
            raw.version = 2
        } finally {
            raw.close()
        }
    }
}
