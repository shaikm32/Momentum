package com.momentum.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.momentum.app.data.repository.RoomActivityRepository
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
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
 * Slice 1 migration test (v1 → v2, now chained through v3): a database
 * created with the exact v1 schema (no `activityType` column) migrates
 * cleanly — existing rows keep every field, gain `activityType = HABIT`
 * (plus the v3 `habitIntent = BUILD` / null `dueTime` defaults from the
 * chained step), and children (completions, reminders) survive. New TASK
 * rows round-trip through the migrated schema.
 *
 * Uses raw SQLite for the v1 fixture (no room-testing dependency — consistent
 * with the existing Robolectric + real-Room test setup).
 */
@RunWith(RobolectricTestRunner::class)
class Migration1To2Test {

    private val dbName = "migration-1-2-test.db"

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
    fun `v1 activity migrates with HABIT default and intact data`() = runTest {
        createV1DatabaseWithSampleData()

        // The database is now at v4: a real v1 install migrates 1 → 2 → 3 → 4,
        // so this test chains all migrations and asserts each step's defaults.
        val db = Room.databaseBuilder(context(), MomentumDatabase::class.java, dbName)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
        try {
            val activities = RoomActivityRepository(db.activityDao(), db.reminderDao(), db)

            // Migrated row: every v1 field intact, type defaulted to HABIT.
            val stored = activities.getById(1)!!
            assertEquals("Morning run", stored.title)
            assertEquals(ActivityType.HABIT, stored.activityType)
            // Chained v3 step: intent defaulted to BUILD, no due time.
            assertEquals(com.momentum.app.domain.model.HabitIntent.BUILD, stored.habitIntent)
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

            // Children survived the migration.
            assertEquals(listOf(5.0), db.completionDao().forActivity(1).map { it.value })
            assertEquals("07:30", db.reminderDao().forActivity(1).single().timeLocal)
            assertEquals("Health", db.categoryDao().all().single().name)

            // The migrated schema stores new TASK rows correctly.
            val taskId = activities.upsert(
                Activity(
                    title = "Pay rent",
                    activityType = ActivityType.TASK,
                    startDate = LocalDate.of(2026, 2, 1),
                    recurrence = Recurrence.Once,
                ),
            )
            assertTrue(taskId > 0)
            val task = activities.getById(taskId)!!
            assertEquals(ActivityType.TASK, task.activityType)
            assertEquals(Recurrence.Once, task.recurrence)
        } finally {
            db.close()
        }
    }

    /** Creates a v1 database (exact v1 DDL from `schemas/.../1.json`) with one of everything. */
    private fun createV1DatabaseWithSampleData() {
        val raw = context().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null)
        try {
            raw.execSQL(
                "CREATE TABLE IF NOT EXISTS `activities` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`title` TEXT NOT NULL, `description` TEXT NOT NULL, `iconKey` TEXT NOT NULL, " +
                    "`categoryId` INTEGER, `colorArgb` INTEGER, `startDate` TEXT NOT NULL, `endDate` TEXT, " +
                    "`recurrenceType` TEXT NOT NULL, `recurrencePayload` TEXT NOT NULL, " +
                    "`targetPerOccurrence` REAL NOT NULL, `unit` TEXT NOT NULL, `priority` INTEGER NOT NULL, " +
                    "`notes` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, " +
                    "`updatedAt` INTEGER NOT NULL)",
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
                "INSERT INTO activities (title, description, iconKey, categoryId, colorArgb, startDate, endDate, " +
                    "recurrenceType, recurrencePayload, targetPerOccurrence, unit, priority, notes, status, " +
                    "createdAt, updatedAt) VALUES " +
                    "('Morning run', 'desc', 'run', NULL, NULL, '2026-01-05', NULL, " +
                    "'SELECTED_WEEKDAYS', '{\"days\":[1,3,5]}', 5.0, 'km', 2, 'Easy pace', 'ACTIVE', 1000, 2000)",
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
            raw.version = 1
        } finally {
            raw.close()
        }
    }
}
