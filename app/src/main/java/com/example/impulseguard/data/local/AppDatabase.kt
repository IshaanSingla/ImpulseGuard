package com.example.impulseguard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WatchedAppEntity::class, SessionEntity::class, PurchaseLogEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchedAppDao(): WatchedAppDao
    abstract fun sessionDao(): SessionDao
    abstract fun purchaseLogDao(): PurchaseLogDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "impulseguard.db",
                ).build().also { instance = it }
            }
    }
}
