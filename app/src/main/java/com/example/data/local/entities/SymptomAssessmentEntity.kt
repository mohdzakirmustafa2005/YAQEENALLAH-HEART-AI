package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptom_assessments")
data class SymptomAssessmentEntity(
    @PrimaryKey val assessmentId: String, // e.g. ASM-2026-882
    val patientId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mainSymptom: String,
    val duration: String,
    val severity: Int, // 1 to 10
    val location: String,
    val radiation: String,
    val associatedSymptoms: String,
    val hasRedFlags: Boolean,
    val emergencyTriggered: Boolean,
    val safetyStatusLabel: String, // Emergency guidance triggered / Prompt medical assessment recommended / Clinician review recommended / General educational information
    val missingInformation: String,
    val evidenceCitationsJson: String,
    val geminiExplanation: String,
    val recommendedNextStep: String,
    val clinicianReviewRequired: Boolean = true,
    val aiModelVersion: String = "gemini-3.8-flash",
    val auditRecord: String
)
