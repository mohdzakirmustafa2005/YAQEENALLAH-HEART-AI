package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val recordType: String, // ADMISSION, EVENT, DISCHARGE_SUMMARY, ECG_REPORT, BLOOD_TEST, DOCTOR_NOTE
    val title: String,
    val description: String,
    val facility: String,
    val clinicianName: String,
    val dateMillis: Long,
    val reviewStatus: String = "APPROVED", // APPROVED, PENDING_REVIEW, UNREVIEWED
    val provenanceSource: String = "Hospital EHR / ABDM FHIR",
    val documentUrlOrPath: String? = null
)
