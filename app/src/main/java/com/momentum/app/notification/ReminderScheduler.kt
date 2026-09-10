package com.momentum.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.momentum.app.domain.model.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Local-only reminder scheduling (ADR-07): exact fire via AlarmManager,
 * resilient resync via WorkManager ([RescheduleWorker]), reboot recovery via
 * [BootReceiver]. No FCM — fully offline.
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
) : ReminderScheduler {

    override suspend fun scheduleForActivity(activityId: Long) {
        val container = (context.applicationContext as com.momentum.app.MomentumApp).container
        cancelForActivity(activityId)
        val activity = container.activityRepository.getById(activityId) ?: return
        if (activity.status != com.momentum.app.domain.model.ActivityStatus.ACTIVE) return
        val reminders = container.reminderRepository.forActivity(activityId).filter { it.enabled }
        reminders.forEach { schedule(it) }
    }

    override suspend fun cancelForActivity(activityId: Long) {
        val container = (context.applicationContext as com.momentum.app.MomentumApp).container
        val reminders = container.reminderRepository.forActivity(activityId)
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        reminders.forEach { reminder ->
            alarmManager.cancel(pendingIntent(reminder, cancel = true))
        }
    }

    private fun schedule(reminder: Reminder) {
        val (hour, minute) = reminder.timeLocal.split(":").map { it.toIntOrNull() ?: 0 }
        val nextFire = nextOccurrence(hour, minute)
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        val triggerAt = nextFire.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (canExact) {
            runCatching {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(reminder, cancel = false),
                )
            }
        } else {
            // Without the exact-alarm grant we still remind, just inexact (Doze-friendly).
            runCatching {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(reminder, cancel = false),
                )
            }
        }
    }

    private fun nextOccurrence(hour: Int, minute: Int): LocalDateTime {
        val now = LocalDateTime.now()
        val today = LocalDate.now().atTime(LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59)))
        return if (!today.isAfter(now)) today.plusDays(1) else today
    }

    private fun pendingIntent(reminder: Reminder, cancel: Boolean): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderReceiver.EXTRA_ACTIVITY_ID, reminder.activityId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        // Distinct requestCode per reminder so multiple reminders coexist.
        return PendingIntent.getBroadcast(context, 10_000 + reminder.id.toInt(), intent, flags)
    }
}
