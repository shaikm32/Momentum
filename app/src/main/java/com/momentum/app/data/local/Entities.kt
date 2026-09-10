package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "activities",
    indices = [
        Index("categoryId"),
        Index("startDate"),
        Index("endDate"),
        Index("status"),
    ],
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val iconKey: String = "spark",
    val categoryId: Long? = null,
    val colorArgb: Int? = null,
    /** ISO-8601 yyyy-MM-dd. */
    val startDate: String,
    val endDate: String? = null,
    val recurrenceType: String,
    val recurrencePayload: String = "{}",
    val targetPerOccurrence: Double = 1.0,
    val unit: String = "",
    val priority: Int = 0,
    val notes: String = "",
    val status: String = "ACTIVE",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
)

@Entity(
    tableName = "completion_records",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("activityId"),
        Index("occurrenceDate"),
        Index(value = ["activityId", "occurrenceDate"], unique = true),
    ],
)
data class CompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityId: Long,
    /** ISO-8601 yyyy-MM-dd of the scheduled occurrence. */
    val occurrenceDate: String,
    val completedAt: Long,
    val value: Double = 1.0,
    val note: String = "",
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconKey: String = "folder",
    val colorArgb: Int? = null,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false,
)

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("activityId")],
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityId: Long,
    /** Local time HH:mm. */
    val timeLocal: String,
    val daysMask: Int = 0,
    val enabled: Boolean = true,
)

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE status IN (:statuses) ORDER BY priority DESC, title ASC")
    fun observeByStatus(statuses: List<String>): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY priority DESC, title ASC")
    fun observeAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getById(id: Long): ActivityEntity?

    @Query("SELECT * FROM activities WHERE id = :id")
    fun observeById(id: Long): Flow<ActivityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ActivityEntity): Long

    @Query("UPDATE activities SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setStatus(id: Long, status: String, updatedAt: Long)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteHard(id: Long)

    @Query("DELETE FROM activities")
    suspend fun clearAll()
}

@Dao
interface CompletionDao {
    @Query("SELECT * FROM completion_records WHERE activityId = :activityId ORDER BY occurrenceDate ASC")
    fun observeForActivity(activityId: Long): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completion_records WHERE occurrenceDate BETWEEN :fromIso AND :toIso")
    fun observeInRange(fromIso: String, toIso: String): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completion_records WHERE activityId = :activityId ORDER BY occurrenceDate ASC")
    suspend fun forActivity(activityId: Long): List<CompletionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: CompletionEntity): Long

    @Query("UPDATE completion_records SET value = :value, completedAt = :completedAt WHERE activityId = :activityId AND occurrenceDate = :iso")
    suspend fun updateValue(activityId: Long, iso: String, value: Double, completedAt: Long)

    @Query("DELETE FROM completion_records WHERE activityId = :activityId AND occurrenceDate = :iso")
    suspend fun delete(activityId: Long, iso: String)

    @Query("SELECT COUNT(*) FROM completion_records WHERE activityId = :activityId AND occurrenceDate = :iso")
    suspend fun count(activityId: Long, iso: String): Int

    @Query("DELETE FROM completion_records")
    suspend fun clearAll()
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    suspend fun all(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CategoryEntity): Long

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE activityId = :activityId")
    fun observeForActivity(activityId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE activityId = :activityId")
    suspend fun forActivity(activityId: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE enabled = 1")
    suspend fun enabledAll(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ReminderEntity): Long

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM reminders WHERE activityId = :activityId")
    suspend fun deleteForActivity(activityId: Long)

    @Query("DELETE FROM reminders")
    suspend fun clearAll()
}
