package com.example.impulseguard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "watched_apps")
data class WatchedAppEntity(
    @PrimaryKey val packageName: String,
    val name: String,
    val category: String,
    val initial: String,
    val colorRole: String,
    val weeklyLimit: Int,
    val enabled: Boolean,
)

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = WatchedAppEntity::class,
            parentColumns = ["packageName"],
            childColumns = ["appId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("appId"), Index("startTime")],
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val overlayShown: Boolean = false,
)

@Entity(
    tableName = "purchase_log",
    foreignKeys = [
        ForeignKey(
            entity = WatchedAppEntity::class,
            parentColumns = ["packageName"],
            childColumns = ["appId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("appId"), Index("timestamp")],
)
data class PurchaseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appId: String,
    val sessionId: Long?,
    val amount: Int,
    val tag: String,
    val timestamp: Long,
    val note: String? = null,
    val source: String,
)
