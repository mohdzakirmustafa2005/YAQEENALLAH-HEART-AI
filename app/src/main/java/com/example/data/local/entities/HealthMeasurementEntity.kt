package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_measurements")
data class HealthMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val type: String, // BP, HEART_RATE, WEIGHT, BMI, HBA1C, TOTAL_CHOLESTEROL, LDL, HDL, TRIGLYCERIDES
    val primaryValue: Double,
    val secondaryValue: Double? = null, // e.g. diastolic for BP
    val unit: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "Digital Sphygmomanometer",
    val clinicianReviewed: Boolean = true
)
