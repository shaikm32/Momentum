package com.momentum.app.di

import android.content.Context
import androidx.room.Room
import com.momentum.app.data.local.MIGRATION_1_2
import com.momentum.app.data.local.MIGRATION_2_3
import com.momentum.app.data.local.MIGRATION_3_4
import com.momentum.app.data.local.MomentumDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Room wiring, mirroring what the retired manual service locator (ADR-09 →
 * ADR-43) assembled.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * P1#2: no destructive-migration fallback. Schema is at version 4
     * (v1 → v2 adds activities.activityType, default HABIT; v2 → v3 adds
     * activities.habitIntent, default BUILD, plus nullable activities.dueTime;
     * v3 → v4 adds nullable activities.habitTemplateId for the predefined
     * icon lock after rename). A future schema change MUST bump the version
     * and add a real Migration plus a migration test; without one, Room fails
     * fast here instead of destroying user data.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MomentumDatabase =
        Room.databaseBuilder(context, MomentumDatabase::class.java, "momentum.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
}
