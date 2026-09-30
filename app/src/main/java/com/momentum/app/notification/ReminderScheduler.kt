package com.momentum.app.notification

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.ReminderRepository
import java.time.LocalDateTime

/**
 * Local-only reminder scheduling (ADR-07): exact fire via AlarmManager,
 * resilient resync via WorkManager ([RescheduleWorker]), reboot/timezone
 * recovery via [SystemEventReceiver]. No FCM — fully offline.
 *
 * P0#2/P0#3: fire times come from [ReminderPlanner] (recurrence-aware), and
 * every (re)schedule is cancel-then-set through [syncActivityAlarms] against
 * the deterministic per-activity identity ([alarmRequestCode]), so edits,
 * resyncs and recovery converge to exactly one alarm and can never duplicate.
 */
interface ReminderScheduler {
    /** (Re)schedule all enabled reminders of one activity. Call after every edit. */
    suspend fun scheduleForActivity(activityId: Long)

    /** Cancel every pending alarm of one activity. Call on archive/delete. */
    suspend fun cancelForActivity(activityId: Long)
}

/**
 * Provided by [com.momentum.app.di.SchedulerModule] as a process-wide
 * singleton (ADR-43). Repositories are constructor-injected — the retired
 * service-locator lookup is gone.
 */
internal class AlarmReminderScheduler(
    private val context: Context,
    private val activityRepository: ActivityRepository,
    private val reminderRepository: ReminderRepository,
    private val gateway: AlarmGateway,
) : ReminderScheduler {

    override suspend fun scheduleForActivity(activityId: Long) {
        val activity = activityRepository.getById(activityId)
        val reminders = reminderRepository.forActivity(activityId)
        syncActivityAlarms(activity, reminders, LocalDateTime.now(), exactAllowed(), gateway)
    }

    override suspend fun cancelForActivity(activityId: Long) {
        // Identity is derived from the activity id alone — no repository read,
        // so cancellation works regardless of call ordering (even after rows
        // were already deleted) and can never miss a stale alarm.
        gateway.cancelAlarm(alarmRequestCode(activityId), activityId)
    }

    private fun exactAllowed(): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }
}
