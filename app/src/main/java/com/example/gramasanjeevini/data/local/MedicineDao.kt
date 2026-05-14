package com.example.gramasanjeevini.data.local

import androidx.room.*
import com.example.gramasanjeevini.model.Medicine
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE name LIKE '%' || :query || '%'")
    fun searchMedicines(query: String): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE isEmergency = 1")
    fun getEmergencyMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE shopId = :shopId")
    fun getMedicinesByShop(shopId: String): Flow<List<Medicine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medicines: List<Medicine>)

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)
}
