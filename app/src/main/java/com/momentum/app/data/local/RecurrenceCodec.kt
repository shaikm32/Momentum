package com.momentum.app.data.local

import com.momentum.app.domain.model.Recurrence
import java.time.DayOfWeek

/**
 * Pure-Kotlin codec between [Recurrence] and its type-tagged storage form
 * (recurrenceType + JSON recurrencePayload, see docs/DATA_MODEL.md).
 *
 * The payload subset we emit is always `{"days":[...],"interval":N,"dayOfMonth":M}`.
 * Parsing is intentionally lenient: unknown/malformed payloads fall back to
 * rule defaults so one bad row can never break the whole schedule.
 */
object RecurrenceCodec {

    fun encodeType(recurrence: Recurrence): String = when (recurrence) {
        is Recurrence.Once -> "ONCE"
        is Recurrence.Daily -> "DAILY"
        is Recurrence.Weekdays -> "WEEKDAYS"
        is Recurrence.SelectedWeekdays -> "SELECTED_WEEKDAYS"
        is Recurrence.EveryNDays -> "EVERY_N_DAYS"
        is Recurrence.Weekly -> "WEEKLY"
        is Recurrence.EveryNWeeks -> "EVERY_N_WEEKS"
        is Recurrence.Monthly -> "MONTHLY"
    }

    fun encodePayload(recurrence: Recurrence): String = when (recurrence) {
        is Recurrence.SelectedWeekdays -> """{"days":[${recurrence.days.sorted().joinToString(",") { it.value.toString() }}]}"""
        is Recurrence.EveryNDays -> """{"interval":${recurrence.n}}"""
        is Recurrence.EveryNWeeks -> """{"days":[${recurrence.days.sorted().joinToString(",") { it.value.toString() }}],"interval":${recurrence.n}}"""
        is Recurrence.Monthly -> """{"dayOfMonth":${recurrence.dayOfMonth}}"""
        else -> "{}"
    }

    fun decode(type: String, payload: String): Recurrence = when (type) {
        "ONCE" -> Recurrence.Once
        "DAILY" -> Recurrence.Daily
        "WEEKDAYS" -> Recurrence.Weekdays
        "SELECTED_WEEKDAYS" -> Recurrence.SelectedWeekdays(readDays(payload).ifEmpty { setOf(DayOfWeek.MONDAY) })
        "EVERY_N_DAYS" -> Recurrence.EveryNDays(readInt(payload, "interval", 1).coerceAtLeast(1))
        "WEEKLY" -> Recurrence.Weekly
        "EVERY_N_WEEKS" -> Recurrence.EveryNWeeks(
            readInt(payload, "interval", 1).coerceAtLeast(1),
            readDays(payload).ifEmpty { setOf(DayOfWeek.MONDAY) },
        )
        "MONTHLY" -> Recurrence.Monthly(readInt(payload, "dayOfMonth", 1).coerceIn(1, 31))
        // Forward-compat: unknown future tags degrade to Daily, never crash.
        else -> Recurrence.Daily
    }

    private fun readDays(payload: String): Set<DayOfWeek> {
        val body = payload.substringAfter("\"days\":[", missingDelimiterValue = "").substringBefore("]")
        if (body.isBlank()) return emptySet()
        return body.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..7 }
            .map { DayOfWeek.of(it) }
            .toSet()
    }

    private fun readInt(payload: String, key: String, default: Int): Int {
        val tail = payload.substringAfter("\"$key\":", missingDelimiterValue = "")
        if (tail.isEmpty()) return default
        val digits = tail.takeWhile { it.isDigit() || it == '-' }
        return digits.toIntOrNull() ?: default
    }
}
