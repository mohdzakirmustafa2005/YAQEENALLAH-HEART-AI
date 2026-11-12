package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val bloodGroup: String,
    val city: String = "Hyderabad, Telangana",
    val hospitalId: String = "NIMS-HYD",
    val hospitalName: String = "Nizam's Institute of Medical Sciences",
    val registeredDate: Long = System.currentTimeMillis(),
    val allergies: String = "No known drug allergies",
    val medicalHistorySummary: String = "Hypertension (Stage 2), Mild CAD",
    val smokingHistory: String = "Non-smoker",
    val familyHistory: String = "Paternal history of MI at age 52",
    val consentGranted: Boolean = true,
    val caregiverName: String = "Farhan Mustafa (Son)",
    val caregiverPhone: String = "+91 98490 12345"
)
