package com.momentum.app.data.repository

import androidx.room.withTransaction
import com.momentum.app.data.local.MomentumDatabase

/**
 * P1#1 transaction boundary for the user-initiated full reset ("Clear all
 * local data" in Settings).
 *
 * Clearing touches three tables (reminders → completions → activities, child
 * order first as defence-in-depth alongside the FK cascades). All three
 * deletes run in a single Room transaction: a failure between them can never
 * leave the database partially cleared (e.g. activities gone but orphan
 * completion rows behind). Categories are intentionally kept — they are
 * seedable reference data, not user history.
 *
 * AlarmManager cancellation is NOT part of this transaction (Android-side,
 * non-transactional by nature): callers must cancel all alarms BEFORE
 * invoking this, and the launch-time resync re-arms whatever is still stored.
 */
class UserDataReset(private val db: MomentumDatabase) {

    suspend fun clearAllUserData() {
        db.withTransaction {
            db.reminderDao().clearAll()
            db.completionDao().clearAll()
            db.activityDao().clearAll()
        }
    }
}
