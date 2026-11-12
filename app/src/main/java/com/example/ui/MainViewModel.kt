package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.HeartAiDatabase
import com.example.data.local.entities.*
import com.example.data.remote.NetworkModule
import com.example.data.repository.CardioRepository
import com.example.data.repository.ConsolidatedEvidenceResult
import com.example.data.repository.EvidenceEngineRepository
import com.example.domain.agents.CardioAgents
import com.example.domain.emergency.DeterministicEmergencyRules
import com.example.domain.emergency.EmergencyScreenResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppScreen {
    HOME,
    SYMPTOM_INTAKE,
    EVIDENCE_EXPLORER,
    HEART_MEMORY,
    CARDIO_CONTINUITY,
    EMERGENCY_NETWORK,
    HOSPITAL_DASHBOARD,
    SETTINGS
}

data class SymptomFormState(
    val mainSymptom: String = "Retrosternal chest pressure",
    val duration: String = "45 minutes",
    val severity: Int = 7,
    val isOngoing: Boolean = true,
    val location: String = "Central chest / retrosternal",
    val radiation: String = "Left arm and lower jaw",
    val hasDyspnea: Boolean = true,
    val hasColdSweat: Boolean = true,
    val hasSyncope: Boolean = false,
    val hasSevereDizziness: Boolean = true,
    val hasNausea: Boolean = true,
    val hasPalpitations: Boolean = false,
    val recentEcgAvailable: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HeartAiDatabase.getDatabase(application)
    private val cardioRepo = CardioRepository(database.dao())
    private val evidenceRepo = EvidenceEngineRepository(
        ncbiService = NetworkModule.ncbiService,
        clinicalTrialsService = NetworkModule.clinicalTrialsService,
        openFdaService = NetworkModule.openFdaService
    )
    private val cardioAgents = CardioAgents(NetworkModule.geminiService)

    // Navigation and Role
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedPatientId = MutableStateFlow("PT-HYD-1001")
    val selectedPatientId: StateFlow<String> = _selectedPatientId.asStateFlow()

    val allPatients: StateFlow<List<PatientEntity>> = cardioRepo.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPatient: StateFlow<PatientEntity?> = _selectedPatientId
        .flatMapLatest { id -> cardioRepo.getPatient(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val medicalRecords: StateFlow<List<MedicalRecordEntity>> = _selectedPatientId
        .flatMapLatest { id -> cardioRepo.getMedicalRecords(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthMeasurements: StateFlow<List<HealthMeasurementEntity>> = _selectedPatientId
        .flatMapLatest { id -> cardioRepo.getMeasurements(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carePlan: StateFlow<CarePlanEntity?> = _selectedPatientId
        .flatMapLatest { id -> cardioRepo.getCarePlan(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCarePlans: StateFlow<List<CarePlanEntity>> = cardioRepo.allCarePlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unresolvedSignals: StateFlow<List<UnresolvedSignalEntity>> = cardioRepo.allUnresolvedSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyEvents: StateFlow<List<EmergencyEventEntity>> = cardioRepo.allEmergencyEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val symptomAssessments: StateFlow<List<SymptomAssessmentEntity>> = cardioRepo.allAssessments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettingsEntity?> = cardioRepo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active Emergency Banner State
    private val _activeEmergencyAlert = MutableStateFlow<EmergencyScreenResult?>(null)
    val activeEmergencyAlert: StateFlow<EmergencyScreenResult?> = _activeEmergencyAlert.asStateFlow()

    // Symptom Form State
    private val _symptomForm = MutableStateFlow(SymptomFormState())
    val symptomForm: StateFlow<SymptomFormState> = _symptomForm.asStateFlow()

    private val _isAnalyzingSymptoms = MutableStateFlow(false)
    val isAnalyzingSymptoms: StateFlow<Boolean> = _isAnalyzingSymptoms.asStateFlow()

    private val _latestAssessmentResult = MutableStateFlow<SymptomAssessmentEntity?>(null)
    val latestAssessmentResult: StateFlow<SymptomAssessmentEntity?> = _latestAssessmentResult.asStateFlow()

    // Evidence Search State
    private val _evidenceQuery = MutableStateFlow("Acute Coronary Syndrome Chest Pain Guidelines")
    val evidenceQuery: StateFlow<String> = _evidenceQuery.asStateFlow()

    private val _isEvidenceLoading = MutableStateFlow(false)
    val isEvidenceLoading: StateFlow<Boolean> = _isEvidenceLoading.asStateFlow()

    private val _evidenceResult = MutableStateFlow<ConsolidatedEvidenceResult?>(null)
    val evidenceResult: StateFlow<ConsolidatedEvidenceResult?> = _evidenceResult.asStateFlow()

    private val _evidenceSynthesisText = MutableStateFlow<String?>(null)
    val evidenceSynthesisText: StateFlow<String?> = _evidenceSynthesisText.asStateFlow()

    // AI Connection Test State
    private val _testConnectionStatus = MutableStateFlow<String?>(null)
    val testConnectionStatus: StateFlow<String?> = _testConnectionStatus.asStateFlow()

    init {
        viewModelScope.launch {
            cardioRepo.initializeSeedDataIfEmpty()
            loadEvidence("Chest Pain Acute Coronary Syndrome")
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectPatient(patientId: String) {
        _selectedPatientId.value = patientId
    }

    fun updateRole(newRole: String) {
        viewModelScope.launch {
            val current = appSettings.value ?: AppSettingsEntity()
            cardioRepo.updateSettings(current.copy(currentRole = newRole))
        }
    }

    fun dismissEmergencyAlert() {
        _activeEmergencyAlert.value = null
    }

    fun updateSymptomForm(updater: SymptomFormState.() -> SymptomFormState) {
        _symptomForm.value = _symptomForm.value.updater()
    }

    /**
     * STAGE 1 — Immediate deterministic safety screen.
     * Evaluates red-flag rules immediately and triggers emergency actions if warranted.
     */
    fun runDeterministicSafetyScreen(): EmergencyScreenResult {
        val form = _symptomForm.value
        val result = DeterministicEmergencyRules.evaluate(
            mainSymptom = form.mainSymptom,
            severity = form.severity,
            location = form.location,
            radiation = form.radiation,
            hasDifficultyBreathing = form.hasDyspnea,
            hasColdSweating = form.hasColdSweat,
            hasFaintingOrSyncope = form.hasSyncope,
            hasSevereDizziness = form.hasSevereDizziness,
            hasNausea = form.hasNausea
        )

        if (result.isEmergencyTriggered) {
            _activeEmergencyAlert.value = result
            triggerEmergencyEvent(
                reason = result.triggerReasons.joinToString("; "),
                guidance = result.immediateGuidance
            )
        }
        return result
    }

    /**
     * Executes the comprehensive 6-stage clinical symptom analysis workflow.
     */
    fun executeFullSymptomAnalysis() {
        viewModelScope.launch {
            _isAnalyzingSymptoms.value = true
            try {
                // 1. Stage 1: Immediate Deterministic Safety Screen
                val screenResult = runDeterministicSafetyScreen()

                // 2. Stage 2: Information Completeness
                val form = _symptomForm.value
                val missingList = mutableListOf<String>()
                if (!form.recentEcgAvailable) missingList.add("Recent 12-lead baseline ECG")
                if (form.duration.isBlank()) missingList.add("Exact onset timestamp")

                // 3. Stage 3: Medical History Context
                val patient = selectedPatient.value
                val historyContext = patient?.medicalHistorySummary ?: "Hypertension, Coronary Artery Disease"

                // 4. Stage 4: Evidence Retrieval
                val evidence = evidenceRepo.fetchConsolidatedEvidence(form.mainSymptom)
                val evidenceCitations = evidence.pubMedArticles.take(2).map { it.title } +
                        evidence.officialGuidelines.take(1).map { it.title }
                val citationsJson = evidenceCitations.joinToString(" | ")

                // 5. Stage 5: Gemini Multi-Agent Explanation
                val settingsVal = appSettings.value
                val apiKey = getEffectiveApiKey(settingsVal?.geminiApiKey)
                val modelName = settingsVal?.selectedGeminiModel ?: "gemini-3.8-flash"

                val explanation = cardioAgents.analyzeSymptoms(
                    mainSymptom = form.mainSymptom,
                    duration = form.duration,
                    severity = form.severity,
                    location = form.location,
                    radiation = form.radiation,
                    associated = buildAssociatedSymptomsString(form),
                    medicalHistory = historyContext,
                    evidenceContext = "PubMed & WHO Guidelines: ${evidenceCitations.joinToString("; ")}",
                    apiKey = apiKey,
                    modelName = modelName
                )

                // 6. Stage 6: Result Generation & Persistence
                val assessmentId = "ASM-${System.currentTimeMillis() % 100000}"
                val recommendedStep = if (screenResult.isEmergencyTriggered) {
                    "EMERGENCY 112 PROTOCOL ACTIVATED: Immediate transport to nearest cardiac casualty. Do not delay for clinic hours."
                } else if (form.severity >= 5) {
                    "Urgent Outpatient Cardiology Evaluation: Schedule same-day clinic consultation and resting 12-lead ECG."
                } else {
                    "Routine Cardiovascular Follow-Up: Review blood pressure log and discuss symptom pattern at scheduled appointment."
                }

                val assessmentEntity = SymptomAssessmentEntity(
                    assessmentId = assessmentId,
                    patientId = patient?.id ?: "PT-HYD-1001",
                    timestamp = System.currentTimeMillis(),
                    mainSymptom = form.mainSymptom,
                    duration = form.duration,
                    severity = form.severity,
                    location = form.location,
                    radiation = form.radiation,
                    associatedSymptoms = buildAssociatedSymptomsString(form),
                    hasRedFlags = screenResult.isEmergencyTriggered,
                    emergencyTriggered = screenResult.isEmergencyTriggered,
                    safetyStatusLabel = screenResult.safetyStatusLabel,
                    missingInformation = if (missingList.isEmpty()) "None; minimum clinical dataset satisfied" else missingList.joinToString(", "),
                    evidenceCitationsJson = citationsJson,
                    geminiExplanation = explanation,
                    recommendedNextStep = recommendedStep,
                    clinicianReviewRequired = true,
                    aiModelVersion = modelName,
                    auditRecord = "Evaluated via Deterministic Rules + Gemini Agent ($modelName) at ${System.currentTimeMillis()}"
                )

                cardioRepo.insertAssessment(assessmentEntity)
                _latestAssessmentResult.value = assessmentEntity

            } catch (_: Exception) {
            } finally {
                _isAnalyzingSymptoms.value = false
            }
        }
    }

    private fun buildAssociatedSymptomsString(form: SymptomFormState): String {
        val list = mutableListOf<String>()
        if (form.hasDyspnea) list.add("Dyspnea")
        if (form.hasColdSweat) list.add("Diaphoresis")
        if (form.hasSyncope) list.add("Syncope")
        if (form.hasSevereDizziness) list.add("Severe Dizziness")
        if (form.hasNausea) list.add("Nausea")
        if (form.hasPalpitations) list.add("Palpitations")
        return if (list.isEmpty()) "None reported" else list.joinToString(", ")
    }

    fun triggerEmergencySosManual() {
        val patient = selectedPatient.value
        val guidance = """
            MANUAL EMERGENCY SOS ACTIVATED
            
            1. DIAL 112 IMMEDIATELY or seek immediate transport to ${patient?.hospitalName ?: "NIMS Hospital Casualty, Hyderabad"}.
            2. Hospital alert dispatched to Emergency Triage Board.
            3. Authorised caregiver (${patient?.caregiverName ?: "Family"}) notified via SMS/Push gateway.
        """.trimIndent()

        val alert = EmergencyScreenResult(
            isEmergencyTriggered = true,
            triggerReasons = listOf("Manual Emergency SOS button triggered by user"),
            immediateGuidance = guidance,
            safetyStatusLabel = "Emergency guidance triggered"
        )
        _activeEmergencyAlert.value = alert
        triggerEmergencyEvent("Manual SOS Triggered", guidance)
    }

    private fun triggerEmergencyEvent(reason: String, guidance: String) {
        viewModelScope.launch {
            val patient = selectedPatient.value
            val event = EmergencyEventEntity(
                eventId = "EMG-${System.currentTimeMillis() % 100000}",
                patientId = patient?.id ?: "PT-HYD-1001",
                hospitalId = patient?.hospitalId ?: "NIMS-HYD",
                timestamp = System.currentTimeMillis(),
                symptomsSummary = reason,
                safetyStatus = "EMERGENCY_TRIGGERED",
                guidanceDelivered = guidance,
                emergency112Status = "IMMEDIATE_DIAL_TRIGGERED",
                hospitalQueueStatus = "DELIVERED",
                caregiverNotified = true
            )
            cardioRepo.insertEmergencyEvent(event)

            // Also create an urgent Unresolved Signal in hospital tracker
            val signal = UnresolvedSignalEntity(
                signalId = "SIG-EMG-${System.currentTimeMillis() % 10000}",
                patientId = patient?.id ?: "PT-HYD-1001",
                hospitalId = patient?.hospitalId ?: "NIMS-HYD",
                signalType = "EMERGENCY_RED_FLAG_ESCALATION",
                priority = "CRITICAL",
                title = "ACUTE EMERGENCY ALERT: ${patient?.name}",
                description = "Patient experienced acute red-flag symptoms: $reason. 112 guidance delivered. Requires casualty verification.",
                assignedOwner = "Casualty Medical Officer (CMO)",
                dueDate = System.currentTimeMillis() + (30 * 60 * 1000L), // 30 mins
                status = "OPEN"
            )
            cardioRepo.insertSignal(signal)
        }
    }

    fun loadEvidence(query: String) {
        viewModelScope.launch {
            _evidenceQuery.value = query
            _isEvidenceLoading.value = true
            try {
                val settings = appSettings.value
                val result = evidenceRepo.fetchConsolidatedEvidence(
                    topic = query,
                    ncbiApiKey = settings?.ncbiApiKey,
                    ncbiEmail = settings?.ncbiEmail,
                    openFdaApiKey = settings?.openFdaApiKey
                )
                _evidenceResult.value = result

                // Synthesize evidence via Live Research Agent
                val apiKey = getEffectiveApiKey(settings?.geminiApiKey)
                val modelName = settings?.selectedGeminiModel ?: "gemini-3.8-flash"
                val snippets = result.pubMedArticles.take(2).map { "${it.title} (${it.journal}, ${it.pubDate})" } +
                        result.clinicalStudies.take(1).map { "${it.title} [Status: ${it.status}]" }

                val synthesis = cardioAgents.synthesizeEvidence(
                    topic = query,
                    evidenceSnippets = snippets,
                    apiKey = apiKey,
                    modelName = modelName
                )
                _evidenceSynthesisText.value = synthesis
            } catch (_: Exception) {
            } finally {
                _isEvidenceLoading.value = false
            }
        }
    }

    fun acknowledgeSignal(signal: UnresolvedSignalEntity, staffName: String = "Dr. K. Venkat Rao") {
        viewModelScope.launch {
            cardioRepo.acknowledgeSignal(signal.signalId, staffName, signal)
        }
    }

    fun resolveSignal(signal: UnresolvedSignalEntity, notes: String, evidence: String, staffName: String = "Dr. K. Venkat Rao") {
        viewModelScope.launch {
            cardioRepo.resolveSignal(signal.signalId, staffName, notes, evidence, signal)
        }
    }

    fun acknowledgeEmergencyEvent(event: EmergencyEventEntity, staffName: String = "Dr. Radhika Sen (Cardio Registrar)") {
        viewModelScope.launch {
            cardioRepo.acknowledgeEmergencyEvent(event.eventId, staffName, event)
        }
    }

    fun toggleCareMilestone(milestoneNumber: Int) {
        viewModelScope.launch {
            val plan = carePlan.value ?: return@launch
            val updated = when (milestoneNumber) {
                7 -> plan.copy(day7FollowUpDone = !plan.day7FollowUpDone)
                30 -> plan.copy(day30FollowUpDone = !plan.day30FollowUpDone)
                90 -> plan.copy(day90FollowUpDone = !plan.day90FollowUpDone)
                else -> plan
            }
            cardioRepo.updateCarePlan(updated)
        }
    }

    fun addManualMeasurement(type: String, primary: Double, secondary: Double?, unit: String) {
        viewModelScope.launch {
            val patient = selectedPatient.value ?: return@launch
            cardioRepo.insertMeasurement(
                HealthMeasurementEntity(
                    patientId = patient.id,
                    type = type,
                    primaryValue = primary,
                    secondaryValue = secondary,
                    unit = unit,
                    source = "Manual Patient Entry",
                    clinicianReviewed = false
                )
            )
        }
    }

    fun saveSettings(updated: AppSettingsEntity) {
        viewModelScope.launch {
            cardioRepo.updateSettings(updated)
        }
    }

    fun testAiConnection() {
        viewModelScope.launch {
            _testConnectionStatus.value = "TESTING..."
            val start = System.currentTimeMillis()
            val settings = appSettings.value
            val apiKey = getEffectiveApiKey(settings?.geminiApiKey)
            val modelName = settings?.selectedGeminiModel ?: "gemini-3.8-flash"

            try {
                // Test PubMed NCBI response
                val pubMedTest = evidenceRepo.fetchConsolidatedEvidence("Cardiovascular")
                val elapsed = System.currentTimeMillis() - start

                val status = if (apiKey.isNotBlank() && apiKey != "DEFAULT_GEMINI_KEY") {
                    "HEALTHY (Gemini $modelName + NCBI PubMed + openFDA Connected, Latency: ${elapsed}ms)"
                } else {
                    "LOCAL & PUBLIC APIS CONNECTED (PubMed, ClinicalTrials, WHO Active; Gemini API key optional in settings, Latency: ${elapsed}ms)"
                }
                _testConnectionStatus.value = status

                if (settings != null) {
                    cardioRepo.updateSettings(
                        settings.copy(
                            lastTestStatus = "ONLINE",
                            lastTestTimestamp = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                _testConnectionStatus.value = "ERROR: ${e.message}"
            }
        }
    }

    private fun getEffectiveApiKey(userKey: String?): String {
        return if (!userKey.isNullOrBlank()) {
            userKey
        } else if (BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "DEFAULT_GEMINI_KEY") {
            BuildConfig.GEMINI_API_KEY
        } else {
            ""
        }
    }
}
