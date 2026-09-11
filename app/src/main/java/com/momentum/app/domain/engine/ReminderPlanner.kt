package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import java.time.LocalDateTime

/**
 * P0#2: schedule-aware reminder fire-time calculation. Pure Kotlin, zero
 * Android imports — the unit-testable source of truth for "when should this
 * reminder fire next".
 *
 * Rules:
 * - The returned datetime is always on a date for which
 *   [RecurrenceEngine.isScheduled] is true (daily, weekdays, monthly,
 *   every-X-days, weekly, one-time — whatever the rule says).
 * - The returned datetime is strictly after [now], so a time that already
 *   passed today rolls to the next *scheduled* date, not merely tomorrow.
 * - Non-ACTIVE activities (paused/archived) and activities past their end
 *   date yield null: no alarm. Deleted activities never reach this planner.
 * - [daysMask] follows the Reminder contract (Mon=bit0..Sun=bit6, 0 = follow
 *   the activity schedule). A non-zero mask additionally constrains the date;
 *   it never widens the recurrence rule.
 * - Malformed [timeLocal] degrades to 00:00 instead of crashing (same
 *   leniency the scheduler historically had).
 * - Bounded by [horizonDays] so a rule with no future occurrence (e.g. a
 *   one-time activity whose date passed) terminates with null.
 */
object ReminderPlanner {

    fun nextFire(
        activity: Activity,
        timeLocal: String,
        daysMask: Int = 0,
        now: LocalDateTime,
        horizonDays: Long = 366,
    ): LocalDateTime? {
        if (activity.status != ActivityStatus.ACTIVE) return null
        val (hour, minute) = parseTime(timeLocal)
        var date = now.toLocalDate()
        repeat(horizonDays.coerceAtLeast(0).toInt() + 1) {
            if (RecurrenceEngine.isScheduled(activity, date) && maskAllows(daysMask, date.dayOfWeek.value)) {
                val candidate = date.atTime(hour, minute)
                if (candidate.isAfter(now)) return candidate
            }
            date = date.plusDays(1)
        }
        return null
    }

    private fun parseTime(timeLocal: String): Pair<Int, Int> {
        val parts = timeLocal.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return hour.coerceIn(0, 23) to minute.coerceIn(0, 59)
    }

    private fun maskAllows(daysMask: Int, isoDayOfWeek: Int): Boolean {
        if (daysMask == 0) return true
        return (daysMask and (1 shl (isoDayOfWeek - 1))) != 0
    }
}
