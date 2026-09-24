package com.momentum.app.data.local

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.CompletionRecord
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.usecase.toIso
import com.momentum.app.domain.usecase.toLocalDate

fun ActivityEntity.toDomain() = Activity(
    id = id,
    title = title,
    activityType = runCatching { ActivityType.valueOf(activityType) }.getOrDefault(ActivityType.HABIT),
    // Legacy "AVOID" (v3) folds into REDUCE; its zero-target measure derives
    // COMPLETE via Activity.reduceMode(), so old rows keep their meaning.
    habitIntent = when (habitIntent) {
        "AVOID" -> HabitIntent.REDUCE
        else -> runCatching { HabitIntent.valueOf(habitIntent) }.getOrDefault(HabitIntent.BUILD)
    },
    dueTime = dueTime,
    description = description,
    iconKey = iconKey,
    habitTemplateId = habitTemplateId,
    categoryId = categoryId,
    colorArgb = colorArgb,
    startDate = startDate.toLocalDate(),
    endDate = endDate?.toLocalDate(),
    recurrence = RecurrenceCodec.decode(recurrenceType, recurrencePayload),
    targetPerOccurrence = targetPerOccurrence,
    unit = unit,
    priority = priority,
    notes = notes,
    status = runCatching { ActivityStatus.valueOf(status) }.getOrDefault(ActivityStatus.ACTIVE),
    createdAtEpochMs = createdAt,
    updatedAtEpochMs = updatedAt,
)

fun Activity.toEntity(nowMs: Long) = ActivityEntity(
    id = id,
    title = title.trim(),
    activityType = activityType.name,
    habitIntent = habitIntent.name,
    dueTime = dueTime,
    description = description,
    iconKey = iconKey,
    habitTemplateId = habitTemplateId,
    categoryId = categoryId,
    colorArgb = colorArgb,
    startDate = startDate.toIso(),
    endDate = endDate?.toIso(),
    recurrenceType = RecurrenceCodec.encodeType(recurrence),
    recurrencePayload = RecurrenceCodec.encodePayload(recurrence),
    targetPerOccurrence = targetPerOccurrence,
    unit = unit,
    priority = priority,
    notes = notes,
    status = status.name,
    createdAt = createdAtEpochMs.takeIf { it != 0L } ?: nowMs,
    updatedAt = nowMs,
)

fun CompletionEntity.toDomain() = CompletionRecord(
    id = id,
    activityId = activityId,
    occurrenceDate = occurrenceDate,
    completedAtEpochMs = completedAt,
    value = value,
    note = note,
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    colorArgb = colorArgb,
    sortOrder = sortOrder,
    isDefault = isDefault,
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    iconKey = iconKey,
    colorArgb = colorArgb,
    sortOrder = sortOrder,
    isDefault = isDefault,
)

fun ReminderEntity.toDomain() = Reminder(
    id = id,
    activityId = activityId,
    timeLocal = timeLocal,
    daysMask = daysMask,
    enabled = enabled,
)

fun Reminder.toEntity() = ReminderEntity(
    id = id,
    activityId = activityId,
    timeLocal = timeLocal,
    daysMask = daysMask,
    enabled = enabled,
)
