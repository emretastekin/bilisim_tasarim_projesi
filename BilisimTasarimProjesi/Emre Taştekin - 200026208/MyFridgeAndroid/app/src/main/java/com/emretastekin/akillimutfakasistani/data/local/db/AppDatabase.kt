package com.emretastekin.akillimutfakasistani.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emretastekin.akillimutfakasistani.data.local.dao.InventoryDao
import com.emretastekin.akillimutfakasistani.data.local.entity.UrunEntity

@Database(entities = [UrunEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mutfak_asistani_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}