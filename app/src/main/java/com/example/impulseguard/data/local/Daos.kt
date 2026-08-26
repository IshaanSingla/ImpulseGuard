package com.example.impulseguard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedAppDao {
    @Query("SELECT * FROM watched_apps ORDER BY name")
    fun observeAll(): Flow<List<WatchedAppEntity>>

    @Query("SELECT * FROM watched_apps WHERE enabled = 1")
    suspend fun getEnabled(): List<WatchedAppEntity>

    @Query("SELECT * FROM watched_apps WHERE packageName = :packageName")
    suspend fun getByPackage(packageName: String): WatchedAppEntity?

    @Upsert
    suspend fun upsert(app: WatchedAppEntity)

    @Query("SELECT COUNT(*) FROM watched_apps")
    suspend fun count(): Int

    @Query("UPDATE watched_apps SET enabled = :enabled WHERE packageName = :packageName")
    suspend fun setEnabled(packageName: String, enabled: Boolean)

    @Query("UPDATE watched_apps SET weeklyLimit = :weeklyLimit WHERE packageName = :packageName")
    suspend fun setWeeklyLimit(packageName: String, weeklyLimit: Int)
}

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: SessionEntity): Long

    @Update
    suspend fun update(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE appId = :appId AND endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getOpenSession(appId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getAnyOpenSession(): SessionEntity?

    @Query("UPDATE sessions SET endTime = :endTime WHERE id = :sessionId")
    suspend fun closeSession(sessionId: Long, endTime: Long)

    @Query("UPDATE sessions SET overlayShown = 1 WHERE id = :sessionId")
    suspend fun markOverlayShown(sessionId: Long)

    @Query("SELECT COUNT(*) FROM sessions WHERE appId = :appId AND startTime >= :sinceMillis")
    suspend fun countSince(appId: String, sinceMillis: Long): Int

    @Query("SELECT * FROM sessions WHERE endTime IS NOT NULL ORDER BY endTime DESC LIMIT 1")
    suspend fun getMostRecentlyClosedSession(): SessionEntity?

    @Query(
        """
        SELECT * FROM sessions
        WHERE appId = :appId AND endTime IS NOT NULL
        ORDER BY endTime DESC LIMIT 1
        """,
    )
    suspend fun getLastClosedSession(appId: String): SessionEntity?

    @Query("SELECT COUNT(*) FROM sessions WHERE startTime >= :sinceMillis")
    fun observeCountSince(sinceMillis: Long): Flow<Int>

    @Query(
        """
        SELECT s.id as id, s.startTime as startTime,
               CASE WHEN p.sessionId IS NULL THEN 1 ELSE 0 END as won
        FROM sessions s
        LEFT JOIN purchase_log p ON p.sessionId = s.id
        WHERE s.endTime IS NOT NULL
        GROUP BY s.id
        ORDER BY s.startTime DESC
        LIMIT :limit
        """,
    )
    fun observeRecentOutcomes(limit: Int): Flow<List<SessionOutcome>>

    @Query("SELECT appId, COUNT(*) as cnt FROM sessions WHERE startTime >= :sinceMillis GROUP BY appId")
    fun observeOpensTodayByApp(sinceMillis: Long): Flow<List<AppOpenCount>>
}

data class SessionOutcome(val id: Long, val startTime: Long, val won: Boolean)
data class AppOpenCount(val appId: String, val cnt: Int)

@Dao
interface PurchaseLogDao {
    @Query("SELECT * FROM purchase_log ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 20): Flow<List<PurchaseLogEntity>>

    @Insert
    suspend fun insert(entry: PurchaseLogEntity): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM purchase_log WHERE timestamp >= :sinceMillis")
    fun observeSpentSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM purchase_log WHERE appId = :appId AND timestamp >= :sinceMillis")
    suspend fun sumSinceForApp(appId: String, sinceMillis: Long): Int

    @Query("SELECT AVG(amount) FROM purchase_log WHERE tag = 'IMPULSE'")
    suspend fun averageImpulseAmount(): Double?

    @Query("SELECT * FROM purchase_log WHERE appId = :appId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastForApp(appId: String): PurchaseLogEntity?
}
