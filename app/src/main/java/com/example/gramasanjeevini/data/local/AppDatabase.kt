package com.example.gramasanjeevini.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gramasanjeevini.model.Medicine
import com.example.gramasanjeevini.model.Shop
import com.example.gramasanjeevini.model.UserProfile

@Database(entities = [Medicine::class, Shop::class, UserProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun shopDao(): ShopDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grama_sanjeevini_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
