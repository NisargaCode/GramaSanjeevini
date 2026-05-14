package com.example.gramasanjeevini.repository

import android.util.Log
import com.example.gramasanjeevini.data.local.MedicineDao
import com.example.gramasanjeevini.data.local.ShopDao
import com.example.gramasanjeevini.data.local.UserDao
import com.example.gramasanjeevini.model.Medicine
import com.example.gramasanjeevini.model.Shop
import com.example.gramasanjeevini.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach

class MedicineRepository(
    private val medicineDao: MedicineDao,
    private val shopDao: ShopDao,
    private val userDao: UserDao
) {
    private val TAG = "GramaDB"

    fun searchMedicines(query: String, maxDistance: Double = 20.0): Flow<List<Pair<Medicine, Shop>>> {
        return combine(
            medicineDao.searchMedicines(query),
            shopDao.getAllShops()
        ) { medicines, shops ->
            medicines.mapNotNull { med ->
                val shop = shops.find { it.shopId == med.shopId }
                if (shop != null && shop.distanceKm <= maxDistance) med to shop else null
            }.sortedBy { it.second.distanceKm }
        }
    }

    fun getEmergencyMedicines(maxDistance: Double = 50.0): Flow<List<Pair<Medicine, Shop>>> {
        return combine(
            medicineDao.getEmergencyMedicines(),
            shopDao.getAllShops()
        ) { medicines, shops ->
            medicines.mapNotNull { med ->
                val shop = shops.find { it.shopId == med.shopId }
                if (shop != null && shop.distanceKm <= maxDistance) med to shop else null
            }.sortedBy { it.second.distanceKm }
        }
    }

    fun getPharmacistMedicines(shopId: String): Flow<List<Medicine>> = 
        medicineDao.getMedicinesByShop(shopId)

    suspend fun addMedicine(medicine: Medicine) {
        Log.d(TAG, "Inserting medicine: ${medicine.name} to Room")
        medicineDao.insertMedicine(medicine)
    }

    // User Profile logic
    fun getUserProfile(): Flow<UserProfile?> = userDao.getUserProfile().onEach {
        Log.d(TAG, "Current user in Room: ${it?.name ?: "None"}")
    }

    suspend fun saveUserProfile(name: String, role: String) {
        Log.d(TAG, "Saving user $name as $role to Room user_profile table")
        userDao.insertProfile(UserProfile(name = name, role = role))
    }

    suspend fun clearSession() {
        Log.d(TAG, "Clearing Room user_profile (Logout)")
        userDao.clearProfile()
    }
    
    suspend fun preseedDatabase(shops: List<Shop>, medicines: List<Medicine>) {
        Log.d(TAG, "Seeding database with initial shops and medicines")
        shopDao.insertAll(shops)
        medicineDao.insertAll(medicines)
    }
}
