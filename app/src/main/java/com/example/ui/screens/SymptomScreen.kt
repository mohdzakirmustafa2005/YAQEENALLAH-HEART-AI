package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.symptomForm.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingSymptoms.collectAsState()
    val latestResult by viewModel.latestAssessmentResult.collectAsState()
    val patient by viewModel.selectedPatient.collectAsState()

    var showPresetMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("symptom_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardioRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = "Symptom Analysis",
                                    tint = CardioRed
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Symptom Intelligence",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "23-Point Structured Assessment & Stage 1 Safety Screen",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        // Clinical Preset Helper Button
                        Box {
                            IconButton(
                                onClick = { showPresetMenu = true },
                                modifier = Modifier.testTag("presets_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Clinical Presets",
                                    tint = PulseCyan
                                )
                            }

                            DropdownMenu(
                                expanded = showPresetMenu,
                                onDismissRequest = { showPresetMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Load: Acute Angina / ACS (Red Flag)") },
                                    onClick = {
                                        viewModel.updateSymptomForm {
                                            copy(
                                                mainSymptom = "Crushing retrosternal chest pressure",
                                                duration = "35 minutes",
                                                severity = 8,
                                                isOngoing = true,
                                                location = "Substernal chest",
                                                radiation = "Left shoulder, inner arm & jaw",
                                                hasDyspnea = true,
                                                hasColdSweat = true,
                                                hasSyncope = false,
                                                hasSevereDizziness = true,
                                                hasNausea = true,
                                                hasPalpitations = true,
                                                recentEcgAvailable = true
                                            )
                                        }
                                        showPresetMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Load: Exertional Dyspnea (Moderate)") },
                                    onClick = {
                                        viewModel.updateSymptomForm {
                                            copy(
                                                mainSymptom = "Shortness of breath on walking 100 meters",
                                                duration = "3 days intermittent",
                                                severity = 5,
                                                isOngoing = false,
                                                location = "Chest tightness on exertion",
                                                radiation = "None",
                                                hasDyspnea = true,
                                                hasColdSweat = false,
                                                hasSyncope = false,
                                                hasSevereDizziness = false,
                                                hasNausea = false,
                                                hasPalpitations = false,
                                                recentEcgAvailable = true
                                            )
                                        }
                                        showPresetMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFF2B0A0D),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Safety Protocol: Deterministic red-flag screening runs first. Potential emergencies trigger immediate 112 guidance independently of AI. This tool does not claim to diagnose or rule out a heart attack.",
                            fontSize = 11.sp,
                            color = Color(0xFFFFCDD2),
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Intake Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Patient Reported Symptoms",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = formState.mainSymptom,
                        onValueChange = { s -> viewModel.updateSymptomForm { copy(mainSymptom = s) } },
                        label = { Text("1. Primary Symptom / Discomfort") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_main_symptom")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.duration,
                            onValueChange = { d -> viewModel.updateSymptomForm { copy(duration = d) } },
                            label = { Text("2. Duration / Onset") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = formState.location,
                            onValueChange = { l -> viewModel.updateSymptomForm { copy(location = l) } },
                            label = { Text("3. Location") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formState.radiation,
                        onValueChange = { r -> viewModel.updateSymptomForm { copy(radiation = r) } },
                        label = { Text("4. Pain Radiation (e.g. Left arm, neck, jaw, back)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Severity Slider (1 to 10)
                    Text(
                        text = "5. Severity Scale: ${formState.severity} / 10 ${if (formState.severity >= 7) "(Severe)" else if (formState.severity >= 4) "(Moderate)" else "(Mild)"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (formState.severity >= 7) CardioRedLight else if (formState.severity >= 4) WarningAmber else EmeraldHealthLight
                    )
                    Slider(
                        value = formState.severity.toFloat(),
                        onValueChange = { v -> viewModel.updateSymptomForm { copy(severity = v.toInt()) } },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = CardioRed,
                            activeTrackColor = CardioRed
                        ),
                        modifier = Modifier.testTag("severity_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "6. Associated Warning Signs & Symptoms:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = formState.hasDyspnea,
                                onCheckedChange = { c -> viewModel.updateSymptomForm { copy(hasDyspnea = c) } }
                            )
                            Text("Difficulty breathing", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = formState.hasColdSweat,
                                onCheckedChange = { c -> viewModel.updateSymptomForm { copy(hasColdSweat = c) } }
                            )
                            Text("Cold sweating", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = formState.hasSyncope,
                                onCheckedChange = { c -> viewModel.updateSymptomForm { copy(hasSyncope = c) } }
                            )
                            Text("Fainting / Syncope", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = formState.hasSevereDizziness,
                                onCheckedChange = { c -> viewModel.updateSymptomForm { copy(hasSevereDizziness = c) } }
                            )
                            Text("Severe dizziness", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = formState.hasNausea,
                                onCheckedChange = { c -> viewModel.updateSymptomForm { copy(hasNausea = c) } }
                            )
                            Text("Nausea / Vomiting", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = formState.hasPalpitations,
                                onCheckedChange = { c -> viewModel.updateSymptomForm { copy(hasPalpitations = c) } }
                            )
                            Text("Palpitations", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.runDeterministicSafetyScreen() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CardioRed),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_stage1_screen"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Run Stage 1 Safety Screen", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.executeFullSymptomAnalysis() },
                            colors = ButtonDefaults.buttonColors(containerColor = CardioRed),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_full_analysis"),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isAnalyzing
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Full 6-Stage Analysis", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Assessment Result Card
        if (latestResult != null) {
            val res = latestResult!!
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("assessment_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (res.emergencyTriggered) CardioRed else EmeraldHealth
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Assessment Result: ${res.assessmentId}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseCyanLight
                            )
                            Surface(
                                color = if (res.emergencyTriggered) CardioRed.copy(alpha = 0.2f) else EmeraldHealth.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = res.safetyStatusLabel,
                                    color = if (res.emergencyTriggered) CardioRedLight else EmeraldHealthLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Reported Profile: ${res.mainSymptom} (Duration: ${res.duration}, Severity: ${res.severity}/10)",
                            fontSize = 13.sp,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MedicalNavySurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "CLINICAL RECOMMENDATION & CARE NAVIGATION:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningAmber
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.recommendedNextStep,
                                    fontSize = 13.sp,
                                    color = TextPrimaryDark,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Gemini Multi-Agent Explanation:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PulseCyanLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = res.geminiExplanation,
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Evidence Citations: ${res.evidenceCitationsJson}",
                            fontSize = 11.sp,
                            color = TextTertiaryDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Audit: ${res.auditRecord}",
                            fontSize = 10.sp,
                            color = TextTertiaryDark
                        )
                    }
                }
            }
        }
    }
}
