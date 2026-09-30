package com.momentum.app.di

import android.content.Context
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.notification.AlarmGateway
import com.momentum.app.notification.AlarmReminderScheduler
import com.momentum.app.notification.RealAlarmGateway
import com.momentum.app.notification.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Reminder scheduling (ADR-07 local-only): the AlarmManager-backed scheduler,
 * bound to the [ReminderScheduler] interface the retired manual service
 * locator exposed (including its test-seam type).
 */
@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {

    @Provides
    fun provideAlarmGateway(@ApplicationContext context: Context): AlarmGateway =
        RealAlarmGateway(context)

    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context,
        activityRepository: ActivityRepository,
        reminderRepository: ReminderRepository,
        gateway: AlarmGateway,
    ): ReminderScheduler = AlarmReminderScheduler(
        context = context,
        activityRepository = activityRepository,
        reminderRepository = reminderRepository,
        gateway = gateway,
    )
}
