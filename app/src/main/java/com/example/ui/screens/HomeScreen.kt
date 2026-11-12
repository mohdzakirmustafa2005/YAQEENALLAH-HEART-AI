package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.PatientEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.VitalsTrendCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    patient: PatientEntity?,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val measurements = viewModel.healthMeasurements.value
    val signals = viewModel.unresolvedSignals.value
    val openSignals = signals.filter { it.status == "OPEN" }
    val criticalCount = signals.count { it.priority == "CRITICAL" && it.status != "RESOLVED" }
    val currentRole = viewModel.appSettings.value?.currentRole ?: "PATIENT"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // App Header & Branding
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                                    .size(40.dp)
                                    .background(CardioRed, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Heart Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "YAQEENALLAH HEART AI",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimaryDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Real-Time Cardiovascular Intelligence",
                                    fontSize = 11.sp,
                                    color = PulseCyanLight
                                )
                            }
                        }

                        Surface(
                            color = MedicalNavySurface,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Hyderabad & Telangana",
                                fontSize = 10.sp,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "“Real-Time Heart Intelligence. Connected Care. Better-Informed Decisions.”",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Switcher Chips
                    Text(
                        text = "Active Portal View:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("PATIENT", "DOCTOR", "NURSE", "ADMIN").forEach { role ->
                            val isSelected = currentRole == role
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateRole(role) },
                                label = { Text(role, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CardioRed,
                                    selectedLabelColor = Color.White,
                                    containerColor = MedicalNavySurface,
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }
            }
        }

        // Emergency SOS Quick Button
        item {
            Button(
                onClick = { viewModel.triggerEmergencySosManual() },
                colors = ButtonDefaults.buttonColors(containerColor = CardioRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("sos_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "EMERGENCY CARDIAC SOS — 112 INDIA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "One-tap acute red-flag triage & hospital casualty alert",
                            fontSize = 10.sp,
                            color = Color(0xFFFFCDD2)
                        )
                    }
                }
            }
        }

        // Active Patient Context Card
        if (patient != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MedicalNavySurface,
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = patient.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = PulseCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${patient.age}y / ${patient.gender} (${patient.bloodGroup})",
                                        fontSize = 10.sp,
                                        color = PulseCyanLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${patient.hospitalName} • ABHA/FHIR Connected",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }

                        IconButton(
                            onClick = { onNavigate(AppScreen.HEART_MEMORY) },
                            modifier = Modifier.testTag("view_patient_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open Profile",
                                tint = PulseCyan
                            )
                        }
                    }
                }
            }
        }

        // Vitals Trend Card
        item {
            VitalsTrendCard(measurements = measurements)
        }

        // Platform Pillars Grid
        item {
            Text(
                text = "Intelligence & Care Modules",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleNavCard(
                        title = "AI Symptom Intelligence",
                        subtitle = "23-point clinical intake & red-flag safety screen",
                        icon = Icons.Default.MedicalServices,
                        accentColor = CardioRed,
                        badge = "Deterministic 112",
                        onClick = { onNavigate(AppScreen.SYMPTOM_INTAKE) },
                        modifier = Modifier.weight(1f),
                        testTag = "nav_symptoms"
                    )

                    ModuleNavCard(
                        title = "Live Medical Evidence",
                        subtitle = "PubMed, Trials, openFDA & WHO guidelines",
                        icon = Icons.Default.Science,
                        accentColor = PulseCyan,
                        badge = "Live E-utilities",
                        onClick = { onNavigate(AppScreen.EVIDENCE_EXPLORER) },
                        modifier = Modifier.weight(1f),
                        testTag = "nav_evidence"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleNavCard(
                        title = "Heart Memory AI",
                        subtitle = "Longitudinal records, ECG & lab timeline",
                        icon = Icons.Default.FolderShared,
                        accentColor = EmeraldHealth,
                        badge = "FHIR Provenance",
                        onClick = { onNavigate(AppScreen.HEART_MEMORY) },
                        modifier = Modifier.weight(1f),
                        testTag = "nav_heart_memory"
                    )

                    ModuleNavCard(
                        title = "Cardio Continuity OS",
                        subtitle = "7/30/90-day follow-ups & signal tracker",
                        icon = Icons.Default.LinearScale,
                        accentColor = WarningOrange,
                        badge = "${openSignals.size} Open Signals",
                        onClick = { onNavigate(AppScreen.CARDIO_CONTINUITY) },
                        modifier = Modifier.weight(1f),
                        testTag = "nav_continuity"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleNavCard(
                        title = "Emergency Network",
                        subtitle = "112 escalation & hospital alert queue",
                        icon = Icons.Default.NotificationsActive,
                        accentColor = if (criticalCount > 0) CardioRed else PulseCyan,
                        badge = if (criticalCount > 0) "$criticalCount CRITICAL" else "Active",
                        onClick = { onNavigate(AppScreen.EMERGENCY_NETWORK) },
                        modifier = Modifier.weight(1f),
                        testTag = "nav_emergency"
                    )

                    ModuleNavCard(
                        title = "Hospital Intelligence",
                        subtitle = "Hyderabad cardiology workload & metrics",
                        icon = Icons.Default.LocalHospital,
                        accentColor = Color(0xFF8B5CF6),
                        badge = "Multi-Hospital",
                        onClick = { onNavigate(AppScreen.HOSPITAL_DASHBOARD) },
                        modifier = Modifier.weight(1f),
                        testTag = "nav_hospital"
                    )
                }
            }
        }

        // AI Control Settings shortcut
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AppScreen.SETTINGS) }
                    .testTag("nav_settings_banner"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = PulseCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Model Control Centre",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Gemini 3.8-Flash • NCBI E-utilities • openFDA API",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Settings",
                        tint = TextTertiaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badge: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondaryDark,
                lineHeight = 15.sp,
                maxLines = 2
            )
        }
    }
}
