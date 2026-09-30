package com.momentum.app.di

import com.momentum.app.domain.repository.ActivityRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Escape hatch for the two non-ViewModel, non-Activity call sites that used
 * the retired manual-container helper ([com.momentum.app.presentation.navigation.DetailTypeGate]
 * and the edit route): they only need to read one row from the repository.
 * Prefer `hiltViewModel()` wherever screen state is involved.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun activityRepository(): ActivityRepository
}
