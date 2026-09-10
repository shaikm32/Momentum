package com.momentum.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

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
