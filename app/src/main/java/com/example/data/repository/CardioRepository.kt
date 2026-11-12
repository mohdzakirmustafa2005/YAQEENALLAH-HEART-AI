package com.example.data.repository

import com.example.data.local.dao.HeartAiDao
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CardioRepository(
    private val dao: HeartAiDao
) {
    val allPatients: Flow<List<PatientEntity>> = dao.getAllPatients()
    val allCarePlans: Flow<List<CarePlanEntity>> = dao.getAllCarePlans()
    val allUnresolvedSignals: Flow<List<UnresolvedSignalEntity>> = dao.getAllUnresolvedSignals()
    val allEmergencyEvents: Flow<List<EmergencyEventEntity>> = dao.getAllEmergencyEvents()
    val allAssessments: Flow<List<SymptomAssessmentEntity>> = dao.getAllAssessments()
    val settings: Flow<AppSettingsEntity?> = dao.getSettings()

    fun getPatient(id: String): Flow<PatientEntity?> = dao.getPatient(id)
    fun getMedicalRecords(patientId: String): Flow<List<MedicalRecordEntity>> = dao.getMedicalRecords(patientId)
    fun getMeasurements(patientId: String): Flow<List<HealthMeasurementEntity>> = dao.getMeasurements(patientId)
    fun getCarePlan(patientId: String): Flow<CarePlanEntity?> = dao.getCarePlan(patientId)
    fun getSignalsForPatient(patientId: String): Flow<List<UnresolvedSignalEntity>> = dao.getSignalsForPatient(patientId)
    fun getAssessmentsForPatient(patientId: String): Flow<List<SymptomAssessmentEntity>> = dao.getAssessments(patientId)

    suspend fun insertPatient(patient: PatientEntity) = dao.insertPatient(patient)
    suspend fun insertMedicalRecord(record: MedicalRecordEntity) = dao.insertMedicalRecord(record)
    suspend fun insertMeasurement(measurement: HealthMeasurementEntity) = dao.insertMeasurement(measurement)
    suspend fun insertCarePlan(plan: CarePlanEntity) = dao.insertCarePlan(plan)
    suspend fun updateCarePlan(plan: CarePlanEntity) = dao.updateCarePlan(plan)
    suspend fun insertSignal(signal: UnresolvedSignalEntity) = dao.insertSignal(signal)
    suspend fun updateSignal(signal: UnresolvedSignalEntity) = dao.updateSignal(signal)
    suspend fun insertEmergencyEvent(event: EmergencyEventEntity) = dao.insertEmergencyEvent(event)
    suspend fun updateEmergencyEvent(event: EmergencyEventEntity) = dao.updateEmergencyEvent(event)
    suspend fun insertAssessment(assessment: SymptomAssessmentEntity) = dao.insertAssessment(assessment)
    suspend fun updateSettings(settings: AppSettingsEntity) = dao.updateSettings(settings)

    suspend fun acknowledgeSignal(signalId: String, staffName: String, existing: UnresolvedSignalEntity) {
        val updated = existing.copy(
            status = "ACKNOWLEDGED",
            acknowledgementTime = System.currentTimeMillis(),
            auditHistory = "${existing.auditHistory}\n[ACK] by $staffName at ${System.currentTimeMillis()}"
        )
        dao.updateSignal(updated)
    }

    suspend fun resolveSignal(signalId: String, staffName: String, notes: String, evidence: String, existing: UnresolvedSignalEntity) {
        val updated = existing.copy(
            status = "RESOLVED",
            closureNotes = notes,
            closureEvidence = evidence,
            auditHistory = "${existing.auditHistory}\n[RESOLVED] by $staffName: $notes"
        )
        dao.updateSignal(updated)
    }

    suspend fun acknowledgeEmergencyEvent(eventId: String, staffName: String, existing: EmergencyEventEntity) {
        val updated = existing.copy(
            hospitalQueueStatus = "ACKNOWLEDGED",
            acknowledgedByStaff = staffName,
            acknowledgedAt = System.currentTimeMillis()
        )
        dao.updateEmergencyEvent(updated)
    }

    suspend fun initializeSeedDataIfEmpty() {
        val existingPatients = dao.getAllPatients().firstOrNull().orEmpty()
        if (existingPatients.isEmpty()) {
            val primaryPatient = PatientEntity(
                id = "PT-HYD-1001",
                name = "Mohammed Zakir Mustafa",
                age = 58,
                gender = "Male",
                bloodGroup = "B+",
                city = "Hyderabad, Telangana",
                hospitalId = "NIMS-HYD",
                hospitalName = "Nizam's Institute of Medical Sciences (NIMS)",
                allergies = "Penicillin (Mild urticaria)",
                medicalHistorySummary = "Coronary Artery Disease (s/p DES to LAD 2024), Hypertension Stage 2, Dyslipidemia",
                smokingHistory = "Ex-smoker (Quit 6 years ago)",
                familyHistory = "Father suffered myocardial infarction at age 52; Maternal uncle diabetic",
                caregiverName = "Farhan Mustafa (Son)",
                caregiverPhone = "+91 98490 12345"
            )

            val secondPatient = PatientEntity(
                id = "PT-HYD-1002",
                name = "Lakshmi Devi N.",
                age = 63,
                gender = "Female",
                bloodGroup = "O+",
                city = "Secunderabad, Telangana",
                hospitalId = "APOLLO-JUBILEE",
                hospitalName = "Apollo Hospitals Jubilee Hills",
                allergies = "None reported",
                medicalHistorySummary = "Type 2 Diabetes Mellitus (HbA1c 7.9%), Mild Ischemic Cardiomyopathy (LVEF 45%)",
                smokingHistory = "Never smoked",
                familyHistory = "Strong maternal history of diabetes and stroke",
                caregiverName = "Ravi Kumar (Brother)",
                caregiverPhone = "+91 94401 56789"
            )

            dao.insertPatient(primaryPatient)
            dao.insertPatient(secondPatient)

            // Seed Medical Records for Heart Memory
            val now = System.currentTimeMillis()
            val day = 86400000L

            dao.insertMedicalRecord(
                MedicalRecordEntity(
                    patientId = primaryPatient.id,
                    recordType = "EVENT",
                    title = "Acute Coronary Syndrome (NSTEMI) Admission",
                    description = "Presented with retrosternal chest tightness; elevated hs-Troponin I (1420 ng/L). Diagnostic coronary angiography performed.",
                    facility = "NIMS Cardiology Department, Hyderabad",
                    clinicianName = "Prof. Dr. K. Venkat Rao, MD, DM (Cardio)",
                    dateMillis = now - (180 * day),
                    reviewStatus = "APPROVED",
                    provenanceSource = "ABDM FHIR Discharge Document",
                    documentUrlOrPath = "https://abdm.gov.in/fhir/DocumentReference/NIMS-2024-998"
                )
            )

            dao.insertMedicalRecord(
                MedicalRecordEntity(
                    patientId = primaryPatient.id,
                    recordType = "ECG_REPORT",
                    title = "12-Lead Standard Rest ECG",
                    description = "Sinus rhythm, HR 74 bpm. T-wave inversions in V3-V5 consistent with anterolateral ischemia. No acute ST-elevation.",
                    facility = "NIMS Electrocardiology Lab",
                    clinicianName = "Dr. Radhika Sen, Senior Registrar",
                    dateMillis = now - (60 * day),
                    reviewStatus = "APPROVED",
                    provenanceSource = "Hospital PACS / Digital ECG",
                    documentUrlOrPath = "NIMS-ECG-1001-V3V5.pdf"
                )
            )

            dao.insertMedicalRecord(
                MedicalRecordEntity(
                    patientId = primaryPatient.id,
                    recordType = "BLOOD_TEST",
                    title = "Comprehensive Lipid Profile & HbA1c",
                    description = "Total Cholesterol: 184 mg/dL, LDL: 98 mg/dL, HDL: 42 mg/dL, Triglycerides: 170 mg/dL, HbA1c: 6.8%.",
                    facility = "Central Biochemistry Laboratory, NIMS",
                    clinicianName = "Dr. Anand V.",
                    dateMillis = now - (14 * day),
                    reviewStatus = "APPROVED",
                    provenanceSource = "NIMS Laboratory Information System",
                    documentUrlOrPath = "LAB-NIMS-2026-088.pdf"
                )
            )

            // Seed Measurements
            val bpReadings = listOf(
                Triple(138.0, 88.0, now - (14 * day)),
                Triple(134.0, 84.0, now - (10 * day)),
                Triple(142.0, 90.0, now - (7 * day)),
                Triple(130.0, 82.0, now - (3 * day)),
                Triple(128.0, 80.0, now - (1 * day))
            )

            bpReadings.forEach { (sys, dia, time) ->
                dao.insertMeasurement(
                    HealthMeasurementEntity(
                        patientId = primaryPatient.id,
                        type = "BP",
                        primaryValue = sys,
                        secondaryValue = dia,
                        unit = "mmHg",
                        timestamp = time,
                        source = "Omron HEM-7120 Digital Monitor",
                        clinicianReviewed = true
                    )
                )
            }

            val hrReadings = listOf(
                Pair(78.0, now - (14 * day)),
                Pair(74.0, now - (10 * day)),
                Pair(82.0, now - (7 * day)),
                Pair(72.0, now - (3 * day)),
                Pair(70.0, now - (1 * day))
            )

            hrReadings.forEach { (hr, time) ->
                dao.insertMeasurement(
                    HealthMeasurementEntity(
                        patientId = primaryPatient.id,
                        type = "HEART_RATE",
                        primaryValue = hr,
                        unit = "bpm",
                        timestamp = time,
                        source = "Pulse Oximeter / Sphygmomanometer",
                        clinicianReviewed = true
                    )
                )
            }

            // Seed Care Plan
            dao.insertCarePlan(
                CarePlanEntity(
                    id = "CP-NIMS-2026-081",
                    patientId = primaryPatient.id,
                    primaryDiagnosis = "Post-PCI to LAD, Controlled Essential Hypertension",
                    dischargeDate = now - (25 * day),
                    day7FollowUpDone = true,
                    day30FollowUpDone = false,
                    day90FollowUpDone = false,
                    assignedDoctor = "Prof. Dr. K. Venkat Rao",
                    assignedCoordinator = "Care Coordinator Sister Sunita (NIMS OPD)",
                    approvedCareInstructions = "1. Tab. Aspirin 75mg once daily post lunch.\n2. Tab. Clopidogrel 75mg once daily.\n3. Tab. Atorvastatin 40mg at bedtime.\n4. Tab. Telmisartan 40mg once daily in morning.\n5. Low sodium diet (<2g sodium/day), 30 min daily brisk walking as tolerated.\n6. Immediate 112 emergency escalation if rest angina reoccurs.",
                    nextAppointmentDate = now + (5 * day),
                    nextInvestigation = "Serum Creatinine, Electrolytes & Resting 12-Lead ECG",
                    status = "ACTIVE"
                )
            )

            // Seed Unresolved Health Signals
            dao.insertSignal(
                UnresolvedSignalEntity(
                    signalId = "SIG-2026-104",
                    patientId = primaryPatient.id,
                    hospitalId = "NIMS-HYD",
                    signalType = "POST_DISCHARGE_CHECKIN",
                    priority = "HIGH",
                    title = "Day-30 Post-Discharge Clinical Review Pending",
                    description = "Patient due for mandatory 30-day post-PCI functional evaluation and dual-antiplatelet adherence verification.",
                    assignedOwner = "Sister Sunita (Cardio Coordinator)",
                    createdDate = now - (2 * day),
                    dueDate = now + (3 * day),
                    status = "OPEN",
                    auditHistory = "Generated automatically by Cardio Continuity OS on Day-28 post-discharge."
                )
            )

            dao.insertSignal(
                UnresolvedSignalEntity(
                    signalId = "SIG-2026-105",
                    patientId = primaryPatient.id,
                    hospitalId = "NIMS-HYD",
                    signalType = "ELEVATED_BP_TREND",
                    priority = "MEDIUM",
                    title = "Isolated Systolic BP Elevation (142/90 mmHg)",
                    description = "Log on Day-7 post discharge exceeded target (<130/80). Correlate with sodium intake or medication compliance.",
                    assignedOwner = "Dr. Radhika Sen",
                    createdDate = now - (7 * day),
                    dueDate = now - (1 * day),
                    status = "ACKNOWLEDGED",
                    acknowledgementTime = now - (6 * day),
                    auditHistory = "Alert acknowledged by Dr. Radhika Sen; telephonic advice given to reduce salt intake."
                )
            )

            // Seed App Settings
            val initialSettings = AppSettingsEntity(
                id = 1,
                geminiApiKey = "",
                selectedGeminiModel = "gemini-3.8-flash",
                ncbiApiKey = "",
                ncbiEmail = "cardio.intelligence@yaqeenallah.org",
                openFdaApiKey = "",
                modelTemperature = 0.2,
                maxOutputTokens = 2048,
                requestTimeoutSeconds = 30,
                dailyUsageCount = 12,
                dailyUsageLimit = 500,
                lastTestStatus = "ONLINE",
                lastTestTimestamp = now,
                currentRole = "PATIENT"
            )
            dao.updateSettings(initialSettings)
        }
    }
}
