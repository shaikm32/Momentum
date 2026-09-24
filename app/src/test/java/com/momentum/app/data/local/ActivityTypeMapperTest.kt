package com.momentum.app.data.local

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Recurrence
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ActivityTypeMapperTest {

    private val date = LocalDate.of(2026, 1, 5)

    @Test
    fun `HABIT round-trips through the entity`() {
        val activity = Activity(title = "Read", activityType = ActivityType.HABIT, startDate = date)

        val entity = activity.toEntity(nowMs = 1000)
        assertEquals("HABIT", entity.activityType)

        val decoded = entity.toDomain()
        assertEquals(ActivityType.HABIT, decoded.activityType)
        assertEquals("Read", decoded.title)
        assertEquals(Recurrence.Daily, decoded.recurrence)
    }

    @Test
    fun `TASK round-trips through the entity`() {
        val activity = Activity(
            title = "Pay rent",
            activityType = ActivityType.TASK,
            startDate = date,
            recurrence = Recurrence.Once,
        )

        val entity = activity.toEntity(nowMs = 1000)
        assertEquals("TASK", entity.activityType)

        val decoded = entity.toDomain()
        assertEquals(ActivityType.TASK, decoded.activityType)
        assertEquals(Recurrence.Once, decoded.recurrence)
    }

    @Test
    fun `unknown stored type degrades to HABIT`() {
        val entity = ActivityEntity(
            title = "Legacy",
            activityType = "FUTURE_KIND",
            startDate = "2026-01-05",
            recurrenceType = "DAILY",
        )

        assertEquals(ActivityType.HABIT, entity.toDomain().activityType)
    }

    @Test
    fun `entity default is HABIT for pre-v2 rows`() {
        val entity = ActivityEntity(
            title = "Legacy",
            startDate = "2026-01-05",
            recurrenceType = "DAILY",
        )

        assertEquals("HABIT", entity.activityType)
        assertEquals(ActivityType.HABIT, entity.toDomain().activityType)
    }
}
