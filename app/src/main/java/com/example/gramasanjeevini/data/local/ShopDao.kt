package com.example.gramasanjeevini.data.local

import androidx.room.*
import com.example.gramasanjeevini.model.Shop
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops")
    fun getAllShops(): Flow<List<Shop>>

    @Query("SELECT * FROM shops WHERE shopId = :shopId")
    suspend fun getShopById(shopId: String): Shop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shops: List<Shop>)
}
