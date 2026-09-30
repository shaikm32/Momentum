package com.momentum.app.di

import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.usecase.ComputeActivityStatsUseCase
import com.momentum.app.domain.usecase.ComputeHabitOverviewUseCase
import com.momentum.app.domain.usecase.GetTodayScheduleUseCase
import com.momentum.app.domain.usecase.SetActualValueUseCase
import com.momentum.app.domain.usecase.ToggleCompletionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Use-case wiring (stateless engines stay unscoped; the two with a repository
 * dependency receive the same singleton the retired service locator used).
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetTodaySchedule(): GetTodayScheduleUseCase = GetTodayScheduleUseCase()

    @Provides
    fun provideComputeActivityStats(): ComputeActivityStatsUseCase = ComputeActivityStatsUseCase()

    @Provides
    fun provideComputeHabitOverview(): ComputeHabitOverviewUseCase = ComputeHabitOverviewUseCase()

    @Provides
    fun provideToggleCompletion(completions: CompletionRepository): ToggleCompletionUseCase =
        ToggleCompletionUseCase(completions)

    @Provides
    fun provideSetActualValue(completions: CompletionRepository): SetActualValueUseCase =
        SetActualValueUseCase(completions)
}
