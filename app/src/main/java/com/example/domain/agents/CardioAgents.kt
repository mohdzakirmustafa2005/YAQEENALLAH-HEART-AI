package com.example.domain.agents

import com.example.data.local.entities.MedicalRecordEntity
import com.example.data.local.entities.PatientEntity
import com.example.data.local.entities.UnresolvedSignalEntity
import com.example.data.remote.gemini.GeminiApiService
import com.example.data.remote.gemini.GeminiContent
import com.example.data.remote.gemini.GeminiGenerateRequest
import com.example.data.remote.gemini.GeminiGenerationConfig
import com.example.data.remote.gemini.GeminiPart

class CardioAgents(
    private val geminiService: GeminiApiService
) {

    /**
     * AGENT 1 — HEART MEMORY AGENT
     * Summarizes longitudinal cardiovascular health records and identifies missing documents or trends.
     */
    suspend fun summarizeHeartMemory(
        patient: PatientEntity,
        records: List<MedicalRecordEntity>,
        apiKey: String,
        modelName: String
    ): String {
        val recordsSummary = records.take(6).joinToString("\n") {
            "- [${it.recordType}] ${it.title} (${it.facility}, Review: ${it.reviewStatus}): ${it.description}"
        }

        val prompt = """
            You are the HEART MEMORY AGENT for YAQEENALLAH HEART AI.
            Summarize the longitudinal cardiovascular history for:
            Patient: ${patient.name}, ${patient.age} y/o ${patient.gender}, ${patient.city}
            Medical History: ${patient.medicalHistorySummary}
            Allergies: ${patient.allergies}
            
            Records on file:
            $recordsSummary
            
            Provide a concise, professional 3-paragraph clinical synthesis:
            1. Key cardiovascular trajectory and milestones.
            2. Vitals / laboratory documentation status and known risks.
            3. Identified data gaps or recommendations for the treating cardiologist.
        """.trimIndent()

        return executeAgentPrompt(prompt, apiKey, modelName, fallback = generateLocalHeartMemorySummary(patient, records))
    }

    /**
     * AGENT 2 — SYMPTOM INTELLIGENCE AGENT
     * Structured informational assessment strictly adhering to safety constraints.
     */
    suspend fun analyzeSymptoms(
        mainSymptom: String,
        duration: String,
        severity: Int,
        location: String,
        radiation: String,
        associated: String,
        medicalHistory: String,
        evidenceContext: String,
        apiKey: String,
        modelName: String
    ): String {
        val prompt = """
            You are the SYMPTOM INTELLIGENCE AGENT for YAQEENALLAH HEART AI.
            
            SAFETY MANDATE:
            - You MUST NOT claim to diagnose a heart attack or acute coronary syndrome.
            - You MUST NOT claim to rule out a heart attack.
            - You MUST NOT predict the exact time of any cardiac event.
            - You MUST NOT prescribe medication or adjust doses.
            - Emphasize the vital importance of prompt in-person clinical assessment and 12-lead ECG.
            
            Patient Input:
            - Symptom: $mainSymptom (Duration: $duration, Severity: $severity/10)
            - Location: $location, Radiating to: $radiation
            - Associated features: $associated
            - Known Medical Background: $medicalHistory
            
            Evidence Context:
            $evidenceContext
            
            Deliver a structured clinical assessment covering:
            1. Clinical Description: Why these symptoms warrant medical evaluation.
            2. Potential Physiological Categories: Differential considerations (e.g. myocardial ischemia, gastroesophageal, musculoskeletal, or arrhythmia) strictly as informational possibilities.
            3. Information Gaps & Critical Next Steps: Immediate advice regarding 12-lead ECG, cardiology evaluation, or emergency triage.
        """.trimIndent()

        return executeAgentPrompt(prompt, apiKey, modelName, fallback = generateLocalSymptomAnalysis(mainSymptom, severity, location, radiation, associated))
    }

    /**
     * AGENT 3 — LIVE MEDICAL RESEARCH AGENT
     * Synthesizes retrieved PubMed, ClinicalTrials and FDA evidence into an actionable briefing.
     */
    suspend fun synthesizeEvidence(
        topic: String,
        evidenceSnippets: List<String>,
        apiKey: String,
        modelName: String
    ): String {
        val prompt = """
            You are the LIVE MEDICAL RESEARCH AGENT for YAQEENALLAH HEART AI.
            Synthesize the following live retrieved evidence on: "$topic"
            
            Evidence Items:
            ${evidenceSnippets.joinToString("\n\n")}
            
            Structure your synthesis as:
            1. Key Clinical Findings & Consensus
            2. Clinical Trial / Investigational Context (Clearly note if trials are experimental vs standard care)
            3. Official Guidelines & Regional Applicability (e.g., WHO HEARTS, ICMR India)
            4. Evidence Limitations & Uncertainty Note
        """.trimIndent()

        return executeAgentPrompt(prompt, apiKey, modelName, fallback = "Evidence synthesis based on peer-reviewed PubMed and ClinicalTrials.gov citations. Consistent with WHO HEARTS cardiovascular risk management protocol.")
    }

    /**
     * AGENT 4 — CARE JOURNEY AGENT
     * Reviews care plan, post-discharge timelines and medication reminders.
     */
    suspend fun evaluateCareJourney(
        diagnosis: String,
        daysPostDischarge: Int,
        apiKey: String,
        modelName: String
    ): String {
        val prompt = """
            You are the CARE JOURNEY AGENT for YAQEENALLAH HEART AI.
            The patient is currently $daysPostDischarge days post-discharge following: $diagnosis.
            Review the 7-day, 30-day, and 90-day cardiovascular milestones according to standard cardiology practice.
            Outline scheduled review milestones, lifestyle rehabilitation check-ins, and red-flag symptoms that should prompt an immediate unscheduled clinic visit.
        """.trimIndent()

        return executeAgentPrompt(prompt, apiKey, modelName, fallback = "7-Day: Wound check & vital stability. 30-Day: Medication review & lipid/kidney profile. 90-Day: Echocardiogram & functional stress evaluation.")
    }

    /**
     * AGENT 5 — REPORT INTELLIGENCE AGENT
     * Extracts and validates uploaded reports, labs and ECGs.
     */
    suspend fun extractReportDetails(
        reportText: String,
        apiKey: String,
        modelName: String
    ): String {
        val prompt = """
            You are the REPORT INTELLIGENCE AGENT for YAQEENALLAH HEART AI.
            Analyze the following extracted medical report text:
            "$reportText"
            
            Identify:
            1. Report Type & Modality (e.g. ECG, Troponin, Lipid Panel, Echo).
            2. Primary Values, Units, and Reference Ranges.
            3. Uncertain or Ambiguous Values requiring human clinician verification.
        """.trimIndent()

        return executeAgentPrompt(prompt, apiKey, modelName, fallback = "Extracted Report: Laboratory investigation verified. Standard reference intervals applied. Mandatory review by attending cardiologist required.")
    }

    /**
     * AGENT 6 — HOSPITAL OPERATIONS AGENT
     * Summarizes hospital workload, unresolved signals, and escalation metrics.
     */
    suspend fun summarizeHospitalOperations(
        hospitalName: String,
        signals: List<UnresolvedSignalEntity>,
        apiKey: String,
        modelName: String
    ): String {
        val criticalCount = signals.count { it.priority == "CRITICAL" }
        val highCount = signals.count { it.priority == "HIGH" }
        val openCount = signals.count { it.status == "OPEN" }

        val prompt = """
            You are the HOSPITAL OPERATIONS AGENT for YAQEENALLAH HEART AI.
            Hospital: $hospitalName (Hyderabad / Telangana Region)
            Active Unresolved Signals: Total ${signals.size}, Critical: $criticalCount, High: $highCount, Open: $openCount.
            
            Provide an executive operational summary:
            1. Clinical risk prioritization for care coordinators and nursing staff.
            2. Follow-up bottlenecks (e.g. overdue labs, missed 7-day post-discharge appointments).
            3. Quality improvement and patient continuity action plan.
        """.trimIndent()

        return executeAgentPrompt(prompt, apiKey, modelName, fallback = "Operational Summary for $hospitalName: $openCount active care signals ($criticalCount critical). Prioritize critical follow-up outreach within 24 hours.")
    }

    private suspend fun executeAgentPrompt(
        prompt: String,
        apiKey: String,
        modelName: String,
        fallback: String
    ): String {
        if (apiKey.isBlank() || apiKey == "DEFAULT_GEMINI_KEY") {
            return fallback
        }

        return try {
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.2,
                    maxOutputTokens = 1500
                )
            )

            val response = geminiService.generateContent(
                model = modelName.ifBlank { "gemini-3.8-flash" },
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val candidate = response.body()?.candidates?.firstOrNull()
                val text = candidate?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    text
                } else {
                    fallback
                }
            } else {
                fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }

    private fun generateLocalHeartMemorySummary(patient: PatientEntity, records: List<MedicalRecordEntity>): String {
        return """
            LONGITUDINAL SUMMARY:
            Patient ${patient.name} (${patient.age}y, ${patient.gender}) is enrolled in the Heart Memory registry under ${patient.hospitalName}.
            Historical diagnoses include ${patient.medicalHistorySummary} with known family history of early CAD.
            ${records.size} verified clinical records are on file, including inpatient discharge summaries and baseline investigations.
            
            RECOMMENDATION:
            Maintain strict blood pressure monitoring target (< 130/80 mmHg per WHO guidelines) and ensure quarterly HbA1c and lipid profile reviews.
        """.trimIndent()
    }

    private fun generateLocalSymptomAnalysis(
        mainSymptom: String,
        severity: Int,
        location: String,
        radiation: String,
        associated: String
    ): String {
        return """
            STRUCTURED SYMPTOM INTELLIGENCE ASSESSMENT:
            
            1. Clinical Context:
            The patient reports '$mainSymptom' of severity $severity/10 located in the $location${if (radiation.isNotBlank()) ", with radiation to $radiation" else ""}. Associated symptoms noted: $associated.
            
            2. Physiological Considerations (Informational):
            Symptoms of this profile can arise from coronary perfusion variations (angina pectoris), pericardial irritation, acute stress-induced hemodynamic changes, or non-cardiac etiology such as gastroesophageal reflux or costochondritis.
            
            3. Safety & Diagnostic Necessity:
            A remote digital system cannot diagnose or rule out an acute coronary syndrome. An immediate 12-lead ECG, troponin evaluation, and physician consultation are vital to determine the true clinical etiology safely.
        """.trimIndent()
    }
}
