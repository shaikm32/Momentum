package com.momentum.app.data.repository

import com.momentum.app.data.local.ActivityDao
import com.momentum.app.data.local.CategoryDao
import com.momentum.app.data.local.CompletionDao
import com.momentum.app.data.local.CompletionEntity
import com.momentum.app.data.local.ReminderDao
import com.momentum.app.data.local.toDomain
import com.momentum.app.data.local.toEntity
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.CompletionRecord
import com.momentum.app.domain.model.Reminder
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.CompletionRepository
import com.momentum.app.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomActivityRepository(private val dao: ActivityDao) : ActivityRepository {
    override fun observeByStatus(statuses: List<ActivityStatus>): Flow<List<Activity>> =
        dao.observeByStatus(statuses.map { it.name }).map { list -> list.map { it.toDomain() } }

    override fun observeAll(): Flow<List<Activity>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Activity?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): Activity? = dao.getById(id)?.toDomain()

    /**
     * P0#1: insert-vs-update (never REPLACE). Editing must UPDATE the existing
     * row so the id is unchanged and the ON DELETE CASCADE children
     * (completion_records, reminders) are never touched. Only id == 0 inserts.
     * createdAt is preserved from the stored row — an edit must not rewrite it.
     */
    override suspend fun upsert(activity: Activity): Long {
        val nowMs = System.currentTimeMillis()
        if (activity.id == 0L) return dao.insert(activity.toEntity(nowMs))
        val existing = dao.getById(activity.id)
        val entity = activity.toEntity(nowMs).copy(createdAt = existing?.createdAt ?: nowMs)
        // Row vanished underneath us (e.g. after a data reset): re-insert with
        // the requested id instead of silently dropping the save.
        return if (dao.update(entity) == 0) dao.insert(entity) else entity.id
    }

    override suspend fun setStatus(id: Long, status: ActivityStatus) =
        dao.setStatus(id, status.name, System.currentTimeMillis())

    override suspend fun deleteHard(id: Long) = dao.deleteHard(id)

    override suspend fun clearAll() = dao.clearAll()
}

class RoomCompletionRepository(private val dao: CompletionDao) : CompletionRepository {
    override fun observeForActivity(activityId: Long): Flow<List<CompletionRecord>> =
        dao.observeForActivity(activityId).map { list -> list.map { it.toDomain() } }

    override fun observeInRange(fromIso: String, toIso: String): Flow<List<CompletionRecord>> =
        dao.observeInRange(fromIso, toIso).map { list -> list.map { it.toDomain() } }

    override suspend fun recordsForActivity(activityId: Long): List<CompletionRecord> =
        dao.forActivity(activityId).map { it.toDomain() }

    override suspend fun complete(activityId: Long, occurrenceDateIso: String, value: Double): Long {
        val now = System.currentTimeMillis()
        val rowId = dao.insertIgnore(
            CompletionEntity(activityId = activityId, occurrenceDate = occurrenceDateIso, completedAt = now, value = value),
        )
        if (rowId == -1L) dao.updateValue(activityId, occurrenceDateIso, value, now)
        return rowId
    }

    override suspend fun uncomplete(activityId: Long, occurrenceDateIso: String) {
        dao.delete(activityId, occurrenceDateIso)
    }

    override suspend fun isCompleted(activityId: Long, occurrenceDateIso: String): Boolean =
        dao.count(activityId, occurrenceDateIso) > 0

    override suspend fun clearAll() = dao.clearAll()
}

class RoomCategoryRepository(private val dao: CategoryDao) : CategoryRepository {
    override fun observeAll(): Flow<List<Category>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun all(): List<Category> = dao.all().map { it.toDomain() }

    override suspend fun getById(id: Long): Category? = dao.getById(id)?.toDomain()

    override suspend fun upsert(category: Category): Long = dao.upsert(category.toEntity())

    override suspend fun delete(id: Long) = dao.delete(id)
}

class RoomReminderRepository(private val dao: ReminderDao) : ReminderRepository {
    override fun observeForActivity(activityId: Long): Flow<List<Reminder>> =
        dao.observeForActivity(activityId).map { list -> list.map { it.toDomain() } }

    override suspend fun forActivity(activityId: Long): List<Reminder> =
        dao.forActivity(activityId).map { it.toDomain() }

    override suspend fun enabledAll(): List<Reminder> = dao.enabledAll().map { it.toDomain() }

    override suspend fun upsert(reminder: Reminder): Long = dao.upsert(reminder.toEntity())

    override suspend fun delete(id: Long) = dao.delete(id)

    override suspend fun deleteForActivity(activityId: Long) = dao.deleteForActivity(activityId)

    override suspend fun clearAll() = dao.clearAll()
}

/** Seeded default categories (user-renameable, user-extensible). */
val DefaultCategories = listOf(
    Category(name = "Health", iconKey = "health", sortOrder = 0, isDefault = true),
    Category(name = "Fitness", iconKey = "fitness", sortOrder = 1, isDefault = true),
    Category(name = "Mind", iconKey = "mind", sortOrder = 2, isDefault = true),
    Category(name = "Learning", iconKey = "learning", sortOrder = 3, isDefault = true),
    Category(name = "Work", iconKey = "work", sortOrder = 4, isDefault = true),
    Category(name = "Home", iconKey = "home", sortOrder = 5, isDefault = true),
)
