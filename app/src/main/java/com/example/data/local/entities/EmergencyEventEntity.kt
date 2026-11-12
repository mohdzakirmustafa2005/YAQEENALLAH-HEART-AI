package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_events")
data class EmergencyEventEntity(
    @PrimaryKey val eventId: String, // e.g. EMG-2026-904
    val patientId: String,
    val hospitalId: String = "NIMS-HYD",
    val timestamp: Long = System.currentTimeMillis(),
    val symptomsSummary: String,
    val safetyStatus: String = "EMERGENCY_TRIGGERED", // EMERGENCY_TRIGGERED, PROMPT_EVALUATION
    val guidanceDelivered: String,
    val emergency112Status: String = "IMMEDIATE_DIAL_TRIGGERED",
    val hospitalQueueStatus: String = "DELIVERED", // DELIVERED, ACKNOWLEDGED, ESCALATED
    val caregiverNotified: Boolean = true,
    val acknowledgedByStaff: String? = null,
    val acknowledgedAt: Long? = null,
    val escalationNotes: String? = null
)
