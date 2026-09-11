package com.momentum.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.momentum.app.domain.engine.ReminderPlanner
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Reminder
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * P0#3: deterministic alarm identity + testable scheduling decisions.
 *
 * Identity rule: one stable alarm per activity, keyed ONLY by [Activity.id]
 * ([alarmRequestCode]). The id never changes across edits, so cancel and
 * re-schedule can never diverge — unlike the previous row-id-based identity,
 * which leaked ghost alarms whenever reminder rows were recreated.
 *
 * v1 supports a single reminder per activity (ADR-12); if multi-reminder UI
 * ever lands, this identity scheme must be extended to (activityId, reminderId)
 * pairs and the edit flow must preserve reminder row ids.
 *
 * [planActivityAlarms] and [syncActivityAlarms] are pure (no Android calls)
 * and are the seam the P0#3 regression tests execute against a fake gateway.
 * The only Android-touching parts are [RealAlarmGateway] and the single
 * [alarmPendingIntent] construction site shared by set and cancel paths.
 */
data class PlannedAlarm(
    val activityId: Long,
    val requestCode: Int,
    val triggerAtMillis: Long,
)

private const val ALARM_REQUEST_CODE_BASE = 10_000

/** Stable across edits, distinct per activity. Pure int math — unit-testable. */
fun alarmRequestCode(activityId: Long): Int = (ALARM_REQUEST_CODE_BASE + activityId).toInt()

/**
 * Decides what should be scheduled for one activity right now.
 * Empty list means "nothing may be armed" (cancel only): paused, archived,
 * deleted (null), disabled reminder, or no future scheduled occurrence.
 */
fun planActivityAlarms(
    activity: Activity?,
    reminders: List<Reminder>,
    now: LocalDateTime,
): List<PlannedAlarm> {
    if (activity == null || activity.status != ActivityStatus.ACTIVE) return emptyList()
    return reminders
        .filter { it.enabled && it.activityId == activity.id }
        .mapNotNull { reminder ->
            val fire = ReminderPlanner.nextFire(activity, reminder.timeLocal, reminder.daysMask, now)
                ?: return@mapNotNull null
            PlannedAlarm(
                activityId = activity.id,
                requestCode = alarmRequestCode(activity.id),
                triggerAtMillis = fire.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            )
        }
}

/**
 * Applies the plan through [gateway]: always cancels the activity's alarm
 * first, then arms exactly what [planActivityAlarms] decided. Running this N
 * times (edits, resyncs, reboot recovery) converges to the same single alarm —
 * alarms can never accumulate.
 */
fun syncActivityAlarms(
    activity: Activity?,
    reminders: List<Reminder>,
    now: LocalDateTime,
    exactAllowed: Boolean,
    gateway: AlarmGateway,
) {
    val activityId = activity?.id ?: reminders.firstOrNull()?.activityId ?: return
    gateway.cancelAlarm(alarmRequestCode(activityId), activityId)
    planActivityAlarms(activity, reminders, now).forEach { planned ->
        gateway.setAlarm(planned.requestCode, planned.activityId, planned.triggerAtMillis, exactAllowed)
    }
}

/** Smallest testable seam over AlarmManager. Production: [RealAlarmGateway]. Tests: fake. */
interface AlarmGateway {
    fun setAlarm(requestCode: Int, activityId: Long, triggerAtMillis: Long, exactAllowed: Boolean)
    fun cancelAlarm(requestCode: Int, activityId: Long)
}

class RealAlarmGateway(private val context: Context) : AlarmGateway {
    override fun setAlarm(requestCode: Int, activityId: Long, triggerAtMillis: Long, exactAllowed: Boolean) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pending = alarmPendingIntent(context, activityId, requestCode)
        runCatching {
            if (exactAllowed) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            } else {
                // Without the exact-alarm grant we still remind, just inexact (Doze-friendly).
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            }
        }
    }

    override fun cancelAlarm(requestCode: Int, activityId: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        runCatching { alarmManager.cancel(alarmPendingIntent(context, activityId, requestCode)) }
    }
}

/**
 * Single construction site for the alarm PendingIntent, shared by the set and
 * cancel paths so the two can never address different alarms.
 */
fun alarmPendingIntent(context: Context, activityId: Long, requestCode: Int): PendingIntent {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(ReminderReceiver.EXTRA_ACTIVITY_ID, activityId)
    }
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    return PendingIntent.getBroadcast(context, requestCode, intent, flags)
}
