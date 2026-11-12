package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartAiDao {
    // Patients
    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatient(id: String): Flow<PatientEntity?>

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    // Medical Records (Heart Memory)
    @Query("SELECT * FROM medical_records WHERE patientId = :patientId ORDER BY dateMillis DESC")
    fun getMedicalRecords(patientId: String): Flow<List<MedicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(record: MedicalRecordEntity)

    // Health Measurements
    @Query("SELECT * FROM health_measurements WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getMeasurements(patientId: String): Flow<List<HealthMeasurementEntity>>

    @Query("SELECT * FROM health_measurements WHERE patientId = :patientId AND type = :type ORDER BY timestamp DESC")
    fun getMeasurementsByType(patientId: String, type: String): Flow<List<HealthMeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: HealthMeasurementEntity)

    // Care Plans (Cardio Continuity)
    @Query("SELECT * FROM care_plans WHERE patientId = :patientId")
    fun getCarePlan(patientId: String): Flow<CarePlanEntity?>

    @Query("SELECT * FROM care_plans")
    fun getAllCarePlans(): Flow<List<CarePlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarePlan(plan: CarePlanEntity)

    @Update
    suspend fun updateCarePlan(plan: CarePlanEntity)

    // Unresolved Signals Tracker
    @Query("SELECT * FROM unresolved_signals ORDER BY priority ASC, dueDate ASC")
    fun getAllUnresolvedSignals(): Flow<List<UnresolvedSignalEntity>>

    @Query("SELECT * FROM unresolved_signals WHERE patientId = :patientId ORDER BY dueDate ASC")
    fun getSignalsForPatient(patientId: String): Flow<List<UnresolvedSignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: UnresolvedSignalEntity)

    @Update
    suspend fun updateSignal(signal: UnresolvedSignalEntity)

    // Emergency Events
    @Query("SELECT * FROM emergency_events ORDER BY timestamp DESC")
    fun getAllEmergencyEvents(): Flow<List<EmergencyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyEvent(event: EmergencyEventEntity)

    @Update
    suspend fun updateEmergencyEvent(event: EmergencyEventEntity)

    // Symptom Assessments
    @Query("SELECT * FROM symptom_assessments WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getAssessments(patientId: String): Flow<List<SymptomAssessmentEntity>>

    @Query("SELECT * FROM symptom_assessments ORDER BY timestamp DESC")
    fun getAllAssessments(): Flow<List<SymptomAssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: SymptomAssessmentEntity)

    // Settings
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: AppSettingsEntity)
}
