package com.example

import com.example.domain.emergency.DeterministicEmergencyRules
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAcuteChestPainWithRadiation_triggersImmediateEmergency() {
        val result = DeterministicEmergencyRules.evaluate(
            mainSymptom = "Crushing chest tightness",
            severity = 8,
            location = "Retrosternal chest",
            radiation = "Left arm and jaw",
            hasDifficultyBreathing = true,
            hasColdSweating = true,
            hasFaintingOrSyncope = false,
            hasSevereDizziness = true,
            hasNausea = true
        )

        assertTrue("Emergency must be triggered for acute chest pain with radiation", result.isEmergencyTriggered)
        assertEquals("Emergency guidance triggered", result.safetyStatusLabel)
        assertTrue("Immediate guidance must instruct to call 112", result.immediateGuidance.contains("112"))
        assertTrue("Triggers must identify chest discomfort and radiation", result.triggerReasons.size >= 3)
    }

    @Test
    fun testMildNonCardiacSymptom_doesNotTriggerEmergency() {
        val result = DeterministicEmergencyRules.evaluate(
            mainSymptom = "Mild leg muscle soreness after walking",
            severity = 2,
            location = "Calf muscles",
            radiation = "None",
            hasDifficultyBreathing = false,
            hasColdSweating = false,
            hasFaintingOrSyncope = false,
            hasSevereDizziness = false,
            hasNausea = false
        )

        assertFalse("Mild muscle soreness must not trigger emergency", result.isEmergencyTriggered)
        assertEquals("General educational information", result.safetyStatusLabel)
    }

    @Test
    fun testSyncopeAlone_triggersEmergencyRule() {
        val result = DeterministicEmergencyRules.evaluate(
            mainSymptom = "Sudden loss of consciousness",
            severity = 9,
            location = "General",
            radiation = "None",
            hasDifficultyBreathing = false,
            hasColdSweating = true,
            hasFaintingOrSyncope = true,
            hasSevereDizziness = true,
            hasNausea = false
        )

        assertTrue("Syncope must trigger deterministic emergency", result.isEmergencyTriggered)
        assertTrue(result.triggerReasons.any { it.contains("Syncope") || it.contains("fainting") })
    }
}
