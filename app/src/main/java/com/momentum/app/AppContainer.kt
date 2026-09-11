package com.momentum.app

import android.content.Context
import androidx.room.Room
import com.momentum.app.data.datastore.DataStoreSettingsRepository
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
class AppContainer(private val context: Context) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * P1#2: no destructive-migration fallback. The schema is at version 1 since
     * inception and the app is unreleased, so no historical schema exists to
     * migrate — and none may ever be silently wiped. A future schema change
     * MUST bump the version and add a real Migration plus a migration test;
     * without one, Room fails fast here instead of destroying user data.
     */
    val database: MomentumDatabase by lazy {
        Room.databaseBuilder(context, MomentumDatabase::class.java, "momentum.db")
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
    val toggleCompletion: ToggleCompletionUseCase by lazy { ToggleCompletionUseCase(completionRepository) }

    val reminderScheduler: ReminderScheduler by lazy {
        ReminderScheduler.create(context)
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
