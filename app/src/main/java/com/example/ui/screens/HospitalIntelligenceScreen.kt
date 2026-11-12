package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun HospitalIntelligenceScreen(
    viewModel: MainViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val patients by viewModel.allPatients.collectAsState()
    val signals by viewModel.unresolvedSignals.collectAsState()
    val emergencies by viewModel.emergencyEvents.collectAsState()
    val carePlans by viewModel.allCarePlans.collectAsState()

    val openSignals = signals.filter { it.status == "OPEN" }
    val criticalSignals = signals.filter { it.priority == "CRITICAL" && it.status != "RESOLVED" }
    val day7Completed = carePlans.count { it.day7FollowUpDone }
    val day7Total = carePlans.size.coerceAtLeast(1)
    val followUpRate = (day7Completed * 100) / day7Total

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("hospital_intelligence_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = "Hospital Intelligence",
                                tint = Color(0xFFA78BFA)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hospital AI Intelligence",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Multi-Hospital SaaS • Hyderabad & Telangana Health Region",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Executive Workload Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "UNRESOLVED SIGNALS",
                    value = "${openSignals.size}",
                    subtext = "${criticalSignals.size} Critical",
                    color = if (criticalSignals.isNotEmpty()) CardioRed else WarningAmber,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "7-DAY FOLLOW-UP",
                    value = "$followUpRate%",
                    subtext = "$day7Completed / $day7Total Completed",
                    color = EmeraldHealthLight,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "CASUALTY ALERTS",
                    value = "${emergencies.size}",
                    subtext = "Active 112 Triage",
                    color = PulseCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Agent 6 Operational Assessment
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF8B5CF6).copy(alpha = 0.4f)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Agent 6: Hospital Operations AI Summary",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA78BFA)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Operations telemetry across Hyderabad cardiology network shows ${openSignals.size} active care continuity signals. Attention required for post-discharge Day-30 compliance tracking. Casualty alert queue actively bridged to 112 India dispatch logs.",
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Regional Hospitals in Network
        item {
            Text(
                text = "Participating Hospitals (Hyderabad & Telangana)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HospitalNetworkItem(
                    name = "Nizam's Institute of Medical Sciences (NIMS)",
                    location = "Punjagutta, Hyderabad",
                    capacity = "Primary Government Tertiary Cardiology Centre",
                    activePatients = "Enrolled Patients: 142 • Active Beds: 85"
                )
                HospitalNetworkItem(
                    name = "Apollo Hospitals",
                    location = "Jubilee Hills, Hyderabad",
                    capacity = "Multispecialty Coronary Care Unit",
                    activePatients = "Enrolled Patients: 98 • Active Beds: 60"
                )
                HospitalNetworkItem(
                    name = "CARE Hospitals",
                    location = "Banjara Hills, Hyderabad",
                    capacity = "Advanced Interventional Cardiology",
                    activePatients = "Enrolled Patients: 76 • Active Beds: 45"
                )
            }
        }

        // Patient Directory in Registry
        item {
            Text(
                text = "Enrolled Patients Directory",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        }

        items(patients) { p ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectPatient(p.id)
                        onNavigate(AppScreen.HEART_MEMORY)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
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
                        Text(
                            text = p.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${p.id} • ${p.age}y / ${p.gender} (${p.bloodGroup}) • ${p.hospitalName}",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View",
                        tint = PulseCyan
                    )
                }
            }
        }
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MedicalNavyCard,
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = TextTertiaryDark
            )
        }
    }
}

@Composable
fun HospitalNetworkItem(
    name: String,
    location: String,
    capacity: String,
    activePatients: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MedicalNavyCard,
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Text(
                text = "$location • $capacity",
                fontSize = 11.sp,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = activePatients,
                fontSize = 10.sp,
                color = PulseCyanLight
            )
        }
    }
}
