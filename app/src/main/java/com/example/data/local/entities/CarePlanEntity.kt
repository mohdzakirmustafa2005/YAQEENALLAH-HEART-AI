package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "care_plans")
data class CarePlanEntity(
    @PrimaryKey val id: String, // e.g. CP-HYD-2026-04
    val patientId: String,
    val primaryDiagnosis: String,
    val dischargeDate: Long,
    val day7FollowUpDone: Boolean = false,
    val day30FollowUpDone: Boolean = false,
    val day90FollowUpDone: Boolean = false,
    val assignedDoctor: String,
    val assignedCoordinator: String,
    val approvedCareInstructions: String,
    val nextAppointmentDate: Long,
    val nextInvestigation: String,
    val status: String = "ACTIVE" // ACTIVE, COMPLETED, OVERDUE_ACTION
)
