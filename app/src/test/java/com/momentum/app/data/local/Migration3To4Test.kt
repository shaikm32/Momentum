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
 * v3 → v4 migration: existing rows gain a null `habitTemplateId` (legacy
 * matchTemplate fallback still applies); new rows persist the template id
 * for the predefined icon lock after rename (Decision 11).
 */
@RunWith(RobolectricTestRunner::class)
class Migration3To4Test {

    private val dbName = "migration-3-4-test.db"

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
    fun `v3 rows migrate with null habitTemplateId and intact data`() = runTest {
        createV3DatabaseWithSampleData()

        val db = Room.databaseBuilder(context(), MomentumDatabase::class.java, dbName)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
        try {
            val activities = RoomActivityRepository(db.activityDao(), db.reminderDao(), db)

            val stored = activities.getById(1)!!
            assertEquals("Morning run", stored.title)
            assertEquals(ActivityType.HABIT, stored.activityType)
            assertEquals(HabitIntent.BUILD, stored.habitIntent)
            assertNull(stored.habitTemplateId)
            assertEquals(LocalDate.of(2026, 1, 5), stored.startDate)
            assertEquals(
                Recurrence.SelectedWeekdays(
                    setOf(java.time.DayOfWeek.MONDAY, java.time.DayOfWeek.WEDNESDAY, java.time.DayOfWeek.FRIDAY),
                ),
                stored.recurrence,
            )
            assertEquals("Easy pace", stored.notes)
            assertEquals(1000L, stored.createdAtEpochMs)

            // Children survived.
            assertEquals(listOf(5.0), db.completionDao().forActivity(1).map { it.value })
            assertEquals("07:30", db.reminderDao().forActivity(1).single().timeLocal)

            // Migrated schema stores a template id correctly.
            val templatedId = activities.upsert(
                Activity(
                    title = "Read books",
                    activityType = ActivityType.HABIT,
                    habitIntent = HabitIntent.BUILD,
                    iconKey = "learning",
                    habitTemplateId = "build_read",
                    startDate = LocalDate.of(2026, 2, 1),
                    targetPerOccurrence = 30.0,
                    unit = "pages",
                ),
            )
            assertTrue(templatedId > 0)
            assertEquals("build_read", activities.getById(templatedId)!!.habitTemplateId)
        } finally {
            db.close()
        }
    }

    /** Exact v3 activities DDL (post ADR-20 schema) with sample data. */
    private fun createV3DatabaseWithSampleData() {
        val raw = context().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null)
        try {
            raw.execSQL(
                "CREATE TABLE IF NOT EXISTS `activities` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`title` TEXT NOT NULL, `activityType` TEXT NOT NULL, `habitIntent` TEXT NOT NULL, " +
                    "`dueTime` TEXT, `description` TEXT NOT NULL, `iconKey` TEXT NOT NULL, " +
                    "`categoryId` INTEGER, `colorArgb` INTEGER, " +
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
                "INSERT INTO activities (title, activityType, habitIntent, dueTime, description, iconKey, " +
                    "categoryId, colorArgb, startDate, endDate, recurrenceType, recurrencePayload, " +
                    "targetPerOccurrence, unit, priority, notes, status, createdAt, updatedAt) VALUES " +
                    "('Morning run', 'HABIT', 'BUILD', NULL, 'desc', 'run', NULL, NULL, '2026-01-05', NULL, " +
                    "'SELECTED_WEEKDAYS', '{\"days\":[1,3,5]}', 1.0, '', 2, 'Easy pace', 'ACTIVE', 1000, 2000)",
            )
            raw.execSQL(
                "INSERT INTO completion_records (activityId, occurrenceDate, completedAt, value, note) " +
                    "VALUES (1, '2026-01-05', 3000, 5.0, '')",
            )
            raw.execSQL("INSERT INTO reminders (activityId, timeLocal, daysMask, enabled) VALUES (1, '07:30', 0, 1)")
            raw.version = 3
        } finally {
            raw.close()
        }
    }
}
