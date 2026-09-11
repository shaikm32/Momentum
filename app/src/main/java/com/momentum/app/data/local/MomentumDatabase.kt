package com.momentum.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * P1#2 migration policy: schema is at version 1 since inception and the app
 * is unreleased, so no historical schema exists to migrate — deliberately no
 * Migration objects and no destructive fallback. A schema change MUST bump
 * [Database.version], add a real `Migration`, export the schema JSON, and add
 * a `MigrationTestHelper` test proving user data (activities, completions
 * with values, reminders, recurrence payloads, categories, statuses) survives.
 */
@Database(
    entities = [ActivityEntity::class, CompletionEntity::class, CategoryEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class MomentumDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun completionDao(): CompletionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
}
