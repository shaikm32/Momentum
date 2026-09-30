package com.momentum.app

import android.content.Context
import androidx.lifecycle.SavedStateHandle
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
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.repository.SettingsRepository
import com.momentum.app.domain.usecase.ComputeActivityStatsUseCase
import com.momentum.app.domain.usecase.ComputeHabitOverviewUseCase
import com.momentum.app.domain.usecase.GetTodayScheduleUseCase
import com.momentum.app.domain.usecase.SetActualValueUseCase
import com.momentum.app.domain.usecase.ToggleCompletionUseCase
import com.momentum.app.notification.AlarmGateway
import com.momentum.app.notification.AlarmReminderScheduler
import com.momentum.app.notification.RealAlarmGateway
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.ViewModelArgs
import com.momentum.app.presentation.screens.creation.CreationViewModel
import com.momentum.app.presentation.screens.detail.HabitDetailViewModel
import com.momentum.app.presentation.screens.habits.HabitsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Test-only object graph wired exactly like the retired manual service
 * locator (ADR-09 → ADR-43), so persistence tests keep driving the real
 * Room/DataStore repositories while staying clear of the Hilt graph
 * (Hilt is exercised by the production wiring, not by unit tests).
 *
 * The [reminderSchedulerOverride] seam is the former test seam: tests that
 * must not touch AlarmManager pass a no-op (or throwing) scheduler here.
 */
class TestGraph(
    private val context: Context,
    /**
     * Test seam: when non-null, used instead of the real scheduler —
     * the same override the retired service locator accepted.
     */
    private val reminderSchedulerOverride: ReminderScheduler? = null,
) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * P1#2: no destructive-migration fallback (schema version 4; see
     * [com.momentum.app.di.DatabaseModule]). Room fails fast on an unmigrated
     * schema instead of destroying data.
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
    val setActualValue: SetActualValueUseCase by lazy { SetActualValueUseCase(completionRepository) }

    val reminderScheduler: ReminderScheduler by lazy {
        reminderSchedulerOverride ?: defaultScheduler()
    }

    /** Same wiring the production scheduler module provides (ADR-43). */
    private fun defaultScheduler(): ReminderScheduler {
        val gateway: AlarmGateway = RealAlarmGateway(context)
        return AlarmReminderScheduler(
            context = context,
            activityRepository = activityRepository,
            reminderRepository = reminderRepository,
            gateway = gateway,
        )
    }

    val userDataReset: UserDataReset by lazy {
        UserDataReset(database)
    }

    init {
        // Seed default categories once (idempotent: only when the table is
        // empty) — same seeding the production app class performs.
        appScope.launch {
            runCatching {
                if (categoryRepository.all().isEmpty()) {
                    DefaultCategories.forEach { categoryRepository.upsert(it) }
                }
            }
        }
    }
}

/** Habit list screen ViewModel, wired from this graph's interface members. */
fun TestGraph.habitsViewModel(): HabitsViewModel =
    HabitsViewModel(activityRepository, reminderRepository, reminderScheduler)

/** Habit detail ViewModel with its row id supplied the way production does. */
fun TestGraph.habitDetailViewModel(activityId: Long): HabitDetailViewModel =
    HabitDetailViewModel(
        SavedStateHandle(mapOf(ViewModelArgs.ACTIVITY_ID to activityId.toString())),
        activityRepository,
        completionRepository,
        reminderRepository,
        settingsRepository,
        computeHabitOverview,
        reminderScheduler,
    )

/**
 * Creation-flow ViewModel mirroring the previous test call shape:
 * `creationViewModel(activityId, activityType, habitIntent)` with the same
 * defaults (HABIT / BUILD) production reads from [ViewModelArgs].
 */
fun TestGraph.creationViewModel(
    activityId: Long?,
    activityType: ActivityType = ActivityType.HABIT,
    habitIntent: HabitIntent? = null,
): CreationViewModel = CreationViewModel(
    SavedStateHandle().apply {
        if (activityId != null) set(ViewModelArgs.ACTIVITY_ID, activityId.toString())
        set(ViewModelArgs.ACTIVITY_TYPE, activityType.name)
        habitIntent?.let { set(ViewModelArgs.HABIT_INTENT, it.name) }
    },
    activityRepository,
    categoryRepository,
    reminderRepository,
    reminderScheduler,
)
