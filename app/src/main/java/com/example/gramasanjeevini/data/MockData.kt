package com.example.gramasanjeevini.data

import com.example.gramasanjeevini.model.Medicine
import com.example.gramasanjeevini.model.Shop

object MockData {
    val shops = listOf(
        Shop("s1", "Grama Medical Store", 12.9716, 77.5946, "+91 9876543210", 2.5),
        Shop("s2", "Village Health Pharmacy", 12.9850, 77.6050, "+91 9123456789", 12.0),
        Shop("s3", "Sanjeevini Drugs", 12.9600, 77.5800, "+91 9988776655", 18.5),
        Shop("s4", "Jan Aushadhi Kendra", 12.9300, 77.5500, "+91 9000011122", 5.0)
    )

    val medicines = mutableListOf(
        Medicine("m1", "Paracetamol", "s1", 50, "2025-12-30", false, 20.0),
        Medicine("m2", "Insulin", "s2", 10, "2024-11-15", true, 500.0),
        Medicine("m3", "Amoxicillin", "s1", 30, "2025-06-20", false, 45.0),
        Medicine("m4", "Adrenaline", "s3", 5, "2024-10-10", true, 150.0),
        Medicine("m5", "Cough Syrup", "s2", 25, "2026-01-01", false, 60.0),
        Medicine("m6", "Aspirin", "s3", 100, "2025-05-15", true, 5.0),
        Medicine("m7", "Paracetamol", "s2", 40, "2025-11-30", false, 18.0),
        Medicine("m8", "Paracetamol", "s3", 20, "2024-05-01", false, 15.0, 10.0), // Near expiry, discounted
        Medicine("m9", "Metformin", "s4", 60, "2025-08-15", false, 35.0),
        Medicine("m10", "Atropine", "s4", 8, "2024-12-20", true, 200.0)
    )
}
