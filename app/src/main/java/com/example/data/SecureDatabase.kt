package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ActivationEntity::class, VaultEntity::class, TaskLogEntity::class], version = 1, exportSchema = false)
abstract class SecureDatabase : RoomDatabase() {
    abstract fun secureDao(): SecureDao

    companion object {
        @Volatile
        private var INSTANCE: SecureDatabase? = null

        fun getDatabase(context: Context): SecureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecureDatabase::class.java,
                    "secure_portal_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
