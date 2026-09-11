package com.momentum.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.momentum.app.MainActivity
import com.momentum.app.MomentumApp
import com.momentum.app.R
import com.momentum.app.domain.model.ActivityStatus
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val MOMENTUM_CHANNEL_ID = "momentum_reminders"
const val ACTION_MARK_COMPLETE = "com.momentum.app.MARK_COMPLETE"

/** Friendly, non-aggressive reminder copy pool. */
val ReminderCopy = listOf(
    "Time for your %s session.",
    "Your %s is waiting — one small win.",
    "One small win before the day ends: %s.",
    "Gentle nudge: %s.",
)

fun ensureReminderChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NotificationManager::class.java) ?: return
    if (manager.getNotificationChannel(MOMENTUM_CHANNEL_ID) != null) return
    manager.createNotificationChannel(
        NotificationChannel(
            MOMENTUM_CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = context.getString(R.string.reminder_channel_desc) },
    )
}

/** Fires when an AlarmManager reminder triggers: shows the notification, reschedules next fire. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val activityId = intent.getLongExtra(EXTRA_ACTIVITY_ID, -1)
        if (activityId == -1L) return
        val pending = goAsync()
        val worker = OneTimeWorkRequestBuilder<FireReminderWorker>()
            .setInputData(
                androidx.work.Data.Builder()
                    .putLong(EXTRA_ACTIVITY_ID, activityId)
                    .build(),
            )
            .build()
        WorkManager.getInstance(context).enqueue(worker)
        pending.finish()
    }

    companion object {
        const val EXTRA_ACTIVITY_ID = "activity_id"
    }
}

/** Builds + posts the notification off the main thread; re-arms the activity's reminders. */
class FireReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val activityId = inputData.getLong(ReminderReceiver.EXTRA_ACTIVITY_ID, -1)
        if (activityId == -1L) return@withContext Result.failure()
        val container = (applicationContext as MomentumApp).container
        val activity = runCatching { container.activityRepository.getById(activityId) }.getOrNull()
            ?: return@withContext Result.failure()
        if (activity.status != ActivityStatus.ACTIVE) return@withContext Result.success()
        ensureReminderChannel(applicationContext)

        val openIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_OPEN_ACTIVITY, activityId)
        }
        val openPending = PendingIntent.getActivity(
            applicationContext, activityId.toInt(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val doneIntent = Intent(applicationContext, CompleteActionReceiver::class.java).apply {
            action = ACTION_MARK_COMPLETE
            putExtra(ReminderReceiver.EXTRA_ACTIVITY_ID, activityId)
        }
        val donePending = PendingIntent.getBroadcast(
            applicationContext, 20_000 + activityId.toInt(), doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val title = ReminderCopy.random().format(activity.title)
        val notification = NotificationCompat.Builder(applicationContext, MOMENTUM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(applicationContext.getString(R.string.reminder_tap_to_open))
            .setContentIntent(openPending)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_notification,
                applicationContext.getString(R.string.action_mark_done),
                donePending,
            )
            .build()
        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        runCatching { manager?.notify(30_000 + activityId.toInt(), notification) }
        // Re-arm the next occurrence (daily cadence derived from reminder time).
        runCatching { container.reminderScheduler.scheduleForActivity(activityId) }
        Result.success()
    }
}

/** "Mark done" notification action: completes today's occurrence, dismisses. */
class CompleteActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_MARK_COMPLETE) return
        val activityId = intent.getLongExtra(ReminderReceiver.EXTRA_ACTIVITY_ID, -1)
        if (activityId == -1L) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val container = (context.applicationContext as MomentumApp).container
                container.toggleCompletion(activityId, LocalDate.now())
                val manager = context.getSystemService(NotificationManager::class.java)
                manager?.cancel(30_000 + activityId.toInt())
            } finally {
                pending.finish()
            }
        }
    }
}

/**
 * P2#3: system-event entry point for reminder recovery. Handles reboot
 * (alarms don't survive restarts) and timezone/manual-clock changes
 * (RTC_WAKEUP wall-clock alarms shift meaning when local time changes).
 * Every path enqueues the central [RescheduleWorker] — no scheduling logic is
 * duplicated here. The worker re-arms per activity through the idempotent,
 * recurrence-aware [ReminderScheduler.scheduleForActivity], so recovery can
 * never duplicate alarms and paused/archived/deleted activities stay silent.
 *
 * Device verification still required (see final report): change the device
 * timezone with an armed reminder and confirm it still fires at the correct
 * local wall-clock time, exactly once.
 */
class SystemEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!shouldResyncOnSystemBroadcast(intent.action)) return
        val builder = OneTimeWorkRequestBuilder<RescheduleWorker>()
        // Boot needs settling time; clock changes should re-arm promptly.
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            builder.setInitialDelay(30, TimeUnit.SECONDS)
        }
        WorkManager.getInstance(context).enqueue(builder.build())
    }
}

/**
 * Pure dispatch predicate (unit-tested): only these system broadcasts invalidate
 * armed wall-clock alarms. DATE_CHANGED (midnight rollover) is excluded — it
 * does not move already-armed RTC instants.
 */
fun shouldResyncOnSystemBroadcast(action: String?): Boolean = action == Intent.ACTION_BOOT_COMPLETED ||
    action == Intent.ACTION_TIMEZONE_CHANGED ||
    action == Intent.ACTION_TIME_CHANGED

/** Re-reads all enabled reminders from Room and re-arms them. Idempotent. */
class RescheduleWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val container = (applicationContext as MomentumApp).container
        val reminders = runCatching { container.reminderRepository.enabledAll() }.getOrDefault(emptyList())
        reminders.groupBy { it.activityId }.keys.forEach { activityId ->
            runCatching { container.reminderScheduler.scheduleForActivity(activityId) }
        }
        Result.success()
    }
}
