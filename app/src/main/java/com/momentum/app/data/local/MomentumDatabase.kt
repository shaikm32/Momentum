package com.momentum.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Slice 1 (v1 → v2): adds `activities.activityType` (HABIT/TASK). Existing
 * rows default to HABIT — every pre-type activity was created by the generic
 * editor whose schedule-first semantics match habits.
 *
 * P1#2 migration policy: deliberately no destructive fallback. A schema
 * change MUST bump [Database.version], add a real `Migration`, export the
 * schema JSON, and add a migration test proving user data (activities,
 * completions with values, reminders, recurrence payloads, categories,
 * statuses, activity types) survives.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE activities ADD COLUMN activityType TEXT NOT NULL DEFAULT 'HABIT'")
    }
}

/**
 * Slice 2 (v2 → v3): adds `activities.habitIntent` (BUILD/REDUCE,
 * default BUILD — every pre-intent habit was created schedule-first) and
 * `activities.dueTime` (nullable `HH:mm`, task-only display concept).
 * Legacy "AVOID" strings that may exist in v3 rows are mapped to REDUCE on
 * read (COMPLETE mode derives from their zero-target measure) — no
 * destructive rewrite, no new migration. See ADR-20.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE activities ADD COLUMN habitIntent TEXT NOT NULL DEFAULT 'BUILD'")
        db.execSQL("ALTER TABLE activities ADD COLUMN dueTime TEXT")
    }
}

/**
 * Slice 3 (v3 → v4): adds `activities.habitTemplateId` (nullable). Stores the
 * predefined template id so a renamed predefined habit keeps its icon lock
 * (Decision 11) — title+icon matching alone breaks after rename. Null =
 * custom or pre-template rows; those keep the existing matchTemplate fallback
 * on edit.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE activities ADD COLUMN habitTemplateId TEXT")
    }
}

@Database(
    entities = [ActivityEntity::class, CompletionEntity::class, CategoryEntity::class, ReminderEntity::class],
    version = 4,
    exportSchema = true,
)
abstract class MomentumDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun completionDao(): CompletionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
}
