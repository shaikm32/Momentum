package com.momentum.app.data.local

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.domain.model.ReduceMode
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HabitIntentMapperTest {

    private val date = LocalDate.of(2026, 1, 5)

    @Test
    fun `BUILD round-trips through the entity`() {
        val activity = Activity(
            title = "Read",
            activityType = ActivityType.HABIT,
            habitIntent = HabitIntent.BUILD,
            startDate = date,
        )

        val entity = activity.toEntity(nowMs = 1000)
        assertEquals("BUILD", entity.habitIntent)

        val decoded = entity.toDomain()
        assertEquals(HabitIntent.BUILD, decoded.habitIntent)
    }

    @Test
    fun `REDUCE round-trips with its limit`() {
        val activity = Activity(
            title = "Social media",
            activityType = ActivityType.HABIT,
            habitIntent = HabitIntent.REDUCE,
            startDate = date,
            targetPerOccurrence = 60.0,
            unit = "min",
        )

        val decoded = activity.toEntity(nowMs = 1000).toDomain()
        assertEquals(HabitIntent.REDUCE, decoded.habitIntent)
        assertEquals(ReduceMode.LIMIT, decoded.reduceMode())
        assertEquals(60.0, decoded.targetPerOccurrence, 0.0)
        assertEquals("min", decoded.unit)
    }

    @Test
    fun `REDUCE COMPLETE round-trips with zero target`() {
        val activity = Activity(
            title = "Smoking",
            activityType = ActivityType.HABIT,
            habitIntent = HabitIntent.REDUCE,
            startDate = date,
        )

        val entity = activity.toEntity(nowMs = 1000)
        assertEquals("REDUCE", entity.habitIntent)

        val decoded = entity.toDomain()
        assertEquals(HabitIntent.REDUCE, decoded.habitIntent)
        assertEquals(ReduceMode.COMPLETE, decoded.reduceMode())
    }

    @Test
    fun `legacy AVOID string maps to REDUCE COMPLETE instead of crashing`() {
        val entity = ActivityEntity(
            title = "Smoke-free",
            habitIntent = "AVOID",
            startDate = "2026-01-05",
            recurrenceType = "DAILY",
        )

        val decoded = entity.toDomain()
        assertEquals(HabitIntent.REDUCE, decoded.habitIntent)
        assertEquals(ReduceMode.COMPLETE, decoded.reduceMode())
    }

    @Test
    fun `dueTime round-trips for tasks`() {
        val activity = Activity(
            title = "Pay rent",
            activityType = ActivityType.TASK,
            startDate = date,
            recurrence = Recurrence.Once,
            dueTime = "20:00",
        )

        val entity = activity.toEntity(nowMs = 1000)
        assertEquals("20:00", entity.dueTime)

        val decoded = entity.toDomain()
        assertEquals("20:00", decoded.dueTime)
    }

    @Test
    fun `null dueTime round-trips`() {
        val activity = Activity(title = "No time", startDate = date)

        val decoded = activity.toEntity(nowMs = 1000).toDomain()
        assertNull(decoded.dueTime)
    }

    @Test
    fun `unknown stored intent degrades to BUILD`() {
        val entity = ActivityEntity(
            title = "Legacy",
            habitIntent = "FUTURE_INTENT",
            startDate = "2026-01-05",
            recurrenceType = "DAILY",
        )

        assertEquals(HabitIntent.BUILD, entity.toDomain().habitIntent)
    }

    @Test
    fun `entity defaults cover pre-v3 rows`() {
        val entity = ActivityEntity(
            title = "Legacy",
            startDate = "2026-01-05",
            recurrenceType = "DAILY",
        )

        assertEquals("BUILD", entity.habitIntent)
        assertEquals(HabitIntent.BUILD, entity.toDomain().habitIntent)
        assertNull(entity.dueTime)
        assertNull(entity.toDomain().dueTime)
    }

    @Test
    fun `task rows carry but ignore intent`() {
        val activity = Activity(
            title = "Pay rent",
            activityType = ActivityType.TASK,
            startDate = date,
            recurrence = Recurrence.Once,
        )

        val decoded = activity.toEntity(nowMs = 1000).toDomain()
        assertEquals(ActivityType.TASK, decoded.activityType)
        assertEquals(HabitIntent.BUILD, decoded.habitIntent)
    }
}
