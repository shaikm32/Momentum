package com.momentum.app.notification

import android.app.AlarmManager
import android.content.Context
import android.os.Build
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

    companion object {
        fun create(context: Context): ReminderScheduler = AlarmReminderScheduler(context)
    }
}

internal class AlarmReminderScheduler(
    private val context: Context,
    private val gateway: AlarmGateway = RealAlarmGateway(context),
) : ReminderScheduler {

    override suspend fun scheduleForActivity(activityId: Long) {
        val container = (context.applicationContext as com.momentum.app.MomentumApp).container
        val activity = container.activityRepository.getById(activityId)
        val reminders = container.reminderRepository.forActivity(activityId)
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
