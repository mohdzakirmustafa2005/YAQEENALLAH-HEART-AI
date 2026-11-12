package com.example.domain.emergency

data class EmergencyScreenResult(
    val isEmergencyTriggered: Boolean,
    val triggerReasons: List<String>,
    val immediateGuidance: String,
    val emergencyNumberIndia: String = "112",
    val ambulanceStatusDisclaimer: String = "Ambulance dispatch must be confirmed directly with emergency services (112) or the nearest hospital casualty.",
    val safetyStatusLabel: String
)

object DeterministicEmergencyRules {
    /**
     * STAGE 1 — IMMEDIATE SAFETY SCREEN
     * Executes deterministic red-flag rules before any AI model or internet retrieval.
     */
    fun evaluate(
        mainSymptom: String,
        severity: Int,
        location: String,
        radiation: String,
        hasDifficultyBreathing: Boolean,
        hasColdSweating: Boolean,
        hasFaintingOrSyncope: Boolean,
        hasSevereDizziness: Boolean,
        hasNausea: Boolean
    ): EmergencyScreenResult {
        val triggers = mutableListOf<String>()

        val lowerSymptom = mainSymptom.lowercase()
        val lowerLocation = location.lowercase()
        val lowerRadiation = radiation.lowercase()

        // 1. Chest pressure / pain / tightness
        val isChestRelated = lowerSymptom.contains("chest") ||
                lowerSymptom.contains("pressure") ||
                lowerSymptom.contains("tightness") ||
                lowerSymptom.contains("squeezing") ||
                lowerLocation.contains("chest") ||
                lowerLocation.contains("substernal") ||
                lowerLocation.contains("retrosternal")

        if (isChestRelated && severity >= 4) {
            triggers.add("Acute chest discomfort or pressure (Severity $severity/10)")
        }

        // 2. Radiation of pain to left arm, shoulder, jaw, neck, or back
        val hasRadiation = lowerRadiation.contains("arm") ||
                lowerRadiation.contains("shoulder") ||
                lowerRadiation.contains("jaw") ||
                lowerRadiation.contains("neck") ||
                lowerRadiation.contains("back")

        if (hasRadiation) {
            triggers.add("Pain radiating to $radiation")
        }

        // 3. Associated acute red flags
        if (hasDifficultyBreathing) {
            triggers.add("Acute difficulty breathing or shortness of breath (Dyspnea)")
        }
        if (hasColdSweating) {
            triggers.add("Sudden cold sweating (Diaphoresis)")
        }
        if (hasFaintingOrSyncope) {
            triggers.add("Loss of consciousness or fainting (Syncope)")
        }
        if (hasSevereDizziness && severity >= 7) {
            triggers.add("Severe incapacitating dizziness")
        }

        // High severity combined with nausea/palpitations in cardiac context
        if (isChestRelated && hasNausea) {
            triggers.add("Chest discomfort associated with nausea/vomiting")
        }

        val isTriggered = triggers.isNotEmpty()

        val label = if (isTriggered) {
            "Emergency guidance triggered"
        } else if (severity >= 6 || hasSevereDizziness) {
            "Prompt medical assessment recommended"
        } else if (severity >= 3) {
            "Clinician review recommended"
        } else {
            "General educational information"
        }

        val guidance = if (isTriggered) {
            """
            POTENTIAL CARDIOVASCULAR EMERGENCY DETECTED
            
            1. CALL 112 IMMEDIATELY or have someone drive you to the nearest Emergency Department (Casualty).
            2. Stop all physical exertion immediately. Sit down in a comfortable, upright position.
            3. Do NOT drive yourself to the hospital.
            4. Loosen tight clothing around your chest and neck.
            5. If you have been previously prescribed Nitroglycerin/Sorbitrate by your cardiologist for acute angina, take it strictly as directed by your physician.
            6. Keep doors unlocked so emergency responders or relatives can enter immediately.
            """.trimIndent()
        } else {
            """
            INFORMATIONAL GUIDANCE
            
            Based on the information provided, no immediate deterministic red-flag emergency pattern was detected, but this does NOT rule out a cardiovascular issue.
            Please consult your cardiologist or primary care physician for a formal clinical evaluation and ECG examination.
            """.trimIndent()
        }

        return EmergencyScreenResult(
            isEmergencyTriggered = isTriggered,
            triggerReasons = triggers,
            immediateGuidance = guidance,
            safetyStatusLabel = label
        )
    }
}
