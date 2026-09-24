package com.momentum.app

import android.content.Context
import androidx.room.Room
import com.momentum.app.data.datastore.DataStoreSettingsRepository
import com.momentum.app.data.local.MIGRATION_1_2
import com.momentum.app.data.local.MIGRATION_2_3
import com.momentum.app.data.local.MIGRATION_3_4
import com.momentum.app.data.local.MomentumDatabase
import com.momentum.app.data.repository.DefaultCategories
import com.momentum.app.data.repository.RoomActivityRepository
import com.momentum.app.data.repository.RoomCategoryRepository
import com.momentum.app.data.repository.RoomCompletionRepository
import com.momentum.app.data.repository.RoomReminderRepository
import com.momentum.app.data.repository.UserDataReset
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.repository.SettingsRepository
import com.momentum.app.domain.usecase.ComputeActivityStatsUseCase
import com.momentum.app.domain.usecase.ComputeHabitOverviewUseCase
import com.momentum.app.domain.usecase.GetTodayScheduleUseCase
import com.momentum.app.domain.usecase.ToggleCompletionUseCase
import com.momentum.app.notification.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manual service locator (ADR-09). Explicit, zero dependencies, no reflection.
 * Room / DataStore / repositories / use cases are wired here.
 */
class AppContainer(
    private val context: Context,
    /**
     * Test seam (ADR-09): when non-null, used instead of the real scheduler.
     * Production always leaves this null and gets the real AlarmManager-backed
     * scheduler. Lets ViewModel/persistence tests avoid the framework alarm
     * surface without touching production behavior.
     */
    private val reminderSchedulerOverride: ReminderScheduler? = null,
) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * P1#2: no destructive-migration fallback. Schema is at version 4
     * (v1 → v2 adds activities.activityType, default HABIT; v2 → v3 adds
     * activities.habitIntent, default BUILD, plus nullable activities.dueTime;
     * v3 → v4 adds nullable activities.habitTemplateId for the predefined
     * icon lock after rename). A future schema change MUST bump the version
     * and add a real Migration plus a migration test; without one, Room fails
     * fast here instead of destroying user data.
     */
    val database: MomentumDatabase by lazy {
        Room.databaseBuilder(context, MomentumDatabase::class.java, "momentum.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }

    val activityRepository: ActivityRepository by lazy {
        RoomActivityRepository(database.activityDao(), database.reminderDao(), database)
    }

    val completionRepository: CompletionRepository by lazy {
        RoomCompletionRepository(database.completionDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        RoomCategoryRepository(database.categoryDao())
    }

    val reminderRepository: ReminderRepository by lazy {
        RoomReminderRepository(database.reminderDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        DataStoreSettingsRepository(context)
    }

    val getTodaySchedule: GetTodayScheduleUseCase by lazy { GetTodayScheduleUseCase() }
    val computeActivityStats: ComputeActivityStatsUseCase by lazy { ComputeActivityStatsUseCase() }
    val computeHabitOverview: ComputeHabitOverviewUseCase by lazy { ComputeHabitOverviewUseCase() }
    val toggleCompletion: ToggleCompletionUseCase by lazy { ToggleCompletionUseCase(completionRepository) }

    val reminderScheduler: ReminderScheduler by lazy {
        reminderSchedulerOverride ?: ReminderScheduler.create(context)
    }

    val userDataReset: UserDataReset by lazy {
        UserDataReset(database)
    }

    init {
        // Seed default categories once (idempotent: only when the table is empty).
        appScope.launch {
            runCatching {
                if (categoryRepository.all().isEmpty()) {
                    DefaultCategories.forEach { categoryRepository.upsert(it) }
                }
            }
        }
    }
}
