package com.example.gramasanjeevini.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class Shop(
    @PrimaryKey val shopId: String,
    val shopName: String,
    val latitude: Double,
    val longitude: Double,
    val contact: String,
    val distanceKm: Double = 0.0
)

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey val medicineId: String,
    val name: String,
    val shopId: String,
    val quantity: Int,
    val expiryDate: String,
    val isEmergency: Boolean,
    val price: Double,
    val discountPrice: Double? = null
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val role: String // "villager" or "pharmacist"
)
