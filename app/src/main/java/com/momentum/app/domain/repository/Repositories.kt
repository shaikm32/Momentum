package com.momentum.app.domain.repository

import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.CompletionRecord
import com.momentum.app.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun observeByStatus(statuses: List<ActivityStatus>): Flow<List<Activity>>
    fun observeAll(): Flow<List<Activity>>
    fun observeById(id: Long): Flow<Activity?>
    suspend fun getById(id: Long): Activity?
    /**
     * Returns the row id. Enforces title/priority/target validation via domain model.
     * Insert-vs-update: id == 0 inserts; otherwise the existing row is UPDATED in
     * place (id and completion history untouched). Never deletes/recreates.
     */
    suspend fun upsert(activity: Activity): Long
    /**
     * Atomically saves the activity and replaces its reminder set (v1: 0..1 rows).
     * Runs in a single Room transaction: activity insert/update + reminder
     * delete + reminder inserts either all commit or all roll back. Reminder
     * rows are stamped with the saved activity id. AlarmManager work stays
     * outside the transaction (callers schedule/cancel alarms after this returns).
     */
    suspend fun saveWithReminders(activity: Activity, reminders: List<Reminder>): Long
    suspend fun setStatus(id: Long, status: ActivityStatus)
    /**
     * Intentional hard delete (explicit user confirmation in UI): removes the
     * activity row; completions cascade via FK. The activity's reminders are
     * deleted in the same Room transaction so no orphan reminder rows survive
     * to re-arm alarms for a deleted activity. Callers cancel alarms first.
     */
    suspend fun deleteHard(id: Long)
    /** Deletes everything (user-initiated reset). Categories are kept. */
    suspend fun clearAll()
}

interface CompletionRepository {
    fun observeForActivity(activityId: Long): Flow<List<CompletionRecord>>
    fun observeInRange(fromIso: String, toIso: String): Flow<List<CompletionRecord>>
    suspend fun recordsForActivity(activityId: Long): List<CompletionRecord>
    /** Upsert by (activityId, occurrenceDate): re-completing never duplicates. Returns row id. */
    suspend fun complete(activityId: Long, occurrenceDateIso: String, value: Double): Long
    suspend fun uncomplete(activityId: Long, occurrenceDateIso: String)
    suspend fun isCompleted(activityId: Long, occurrenceDateIso: String): Boolean
    suspend fun clearAll()
}

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    suspend fun all(): List<Category>
    suspend fun getById(id: Long): Category?
    suspend fun upsert(category: Category): Long
    suspend fun delete(id: Long)
}

interface ReminderRepository {
    fun observeForActivity(activityId: Long): Flow<List<Reminder>>
    suspend fun forActivity(activityId: Long): List<Reminder>
    suspend fun enabledAll(): List<Reminder>
    suspend fun upsert(reminder: Reminder): Long
    suspend fun delete(id: Long)
    suspend fun deleteForActivity(activityId: Long)
    suspend fun clearAll()
}

interface SettingsRepository {
    fun observe(): Flow<AppSettings>
    suspend fun current(): AppSettings
    suspend fun update(transform: (AppSettings) -> AppSettings)
}
