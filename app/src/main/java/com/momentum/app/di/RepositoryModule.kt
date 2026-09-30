package com.momentum.app.di

import com.momentum.app.data.local.MomentumDatabase
import com.momentum.app.data.repository.RoomActivityRepository
import com.momentum.app.data.repository.RoomCategoryRepository
import com.momentum.app.data.repository.RoomCompletionRepository
import com.momentum.app.data.repository.RoomReminderRepository
import com.momentum.app.data.repository.UserDataReset
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository bindings: one instance per process, exactly as the retired
 * manual service locator's lazy members behaved (ADR-09 → ADR-43).
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideActivityRepository(database: MomentumDatabase): ActivityRepository =
        RoomActivityRepository(database.activityDao(), database.reminderDao(), database)

    @Provides
    @Singleton
    fun provideCompletionRepository(database: MomentumDatabase): CompletionRepository =
        RoomCompletionRepository(database.completionDao())

    @Provides
    @Singleton
    fun provideCategoryRepository(database: MomentumDatabase): CategoryRepository =
        RoomCategoryRepository(database.categoryDao())

    @Provides
    @Singleton
    fun provideReminderRepository(database: MomentumDatabase): ReminderRepository =
        RoomReminderRepository(database.reminderDao())

    @Provides
    @Singleton
    fun provideUserDataReset(database: MomentumDatabase): UserDataReset = UserDataReset(database)
}
