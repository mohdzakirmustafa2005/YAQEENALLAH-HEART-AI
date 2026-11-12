package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unresolved_signals")
data class UnresolvedSignalEntity(
    @PrimaryKey val signalId: String, // e.g. SIG-2026-104
    val patientId: String,
    val hospitalId: String = "NIMS-HYD",
    val signalType: String, // MISSED_APPOINTMENT, OVERDUE_LAB, ELEVATED_BP_TREND, MEDICATION_GAP, POST_DISCHARGE_CHECKIN
    val priority: String, // CRITICAL, HIGH, MEDIUM, LOW
    val title: String,
    val description: String,
    val assignedOwner: String, // Dr. K. Rao (Cardiology), Nurse Sunita
    val createdDate: Long = System.currentTimeMillis(),
    val dueDate: Long,
    val status: String = "OPEN", // OPEN, ACKNOWLEDGED, RESOLVED, ESCALATED
    val acknowledgementTime: Long? = null,
    val closureNotes: String? = null,
    val closureEvidence: String? = null,
    val auditHistory: String = "Created by Cardio Continuity Engine"
)
