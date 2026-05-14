package com.example.gramasanjeevini

import android.app.Application
import com.example.gramasanjeevini.data.local.AppDatabase
import com.example.gramasanjeevini.repository.MedicineRepository

class GramaSanjeeviniApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { 
        MedicineRepository(
            database.medicineDao(), 
            database.shopDao(), 
            database.userDao()
        ) 
    }
}
