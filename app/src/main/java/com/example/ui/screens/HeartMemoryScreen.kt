package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.local.entities.MedicalRecordEntity
import com.example.data.local.entities.PatientEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HeartMemoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val patient by viewModel.selectedPatient.collectAsState()
    val allPatients by viewModel.allPatients.collectAsState()
    val records by viewModel.medicalRecords.collectAsState()
    val measurements by viewModel.healthMeasurements.collectAsState()

    var showAddMeasurementDialog by remember { mutableStateOf(false) }
    var newSysBp by remember { mutableStateOf("126") }
    var newDiaBp by remember { mutableStateOf("82") }
    var newHr by remember { mutableStateOf("72") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("heart_memory_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Patient Header & Demographics
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
                                    .size(38.dp)
                                    .background(EmeraldHealth.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderShared,
                                    contentDescription = "Heart Memory",
                                    tint = EmeraldHealth
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Heart Memory AI",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Longitudinal Cardiovascular Electronic Record",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        Surface(
                            color = EmeraldHealth.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Consent: GRANTED",
                                color = EmeraldHealthLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (patient != null) {
                        val p = patient!!
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = p.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Patient ID: ${p.id} • ${p.age} years / ${p.gender} • Blood Group: ${p.bloodGroup}",
                            fontSize = 12.sp,
                            color = PulseCyanLight
                        )
                        Text(
                            text = "Hospital: ${p.hospitalName} (${p.city})",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MedicalNavySurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Cardiovascular History & Baseline:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WarningAmber
                                )
                                Text(
                                    text = p.medicalHistorySummary,
                                    fontSize = 12.sp,
                                    color = TextPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Allergies: ${p.allergies}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = "Caregiver: ${p.caregiverName} (${p.caregiverPhone})",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showAddMeasurementDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseCyan),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_log_vital"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Vital",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Blood Pressure / HR", fontSize = 12.sp)
                }
            }
        }

        // Timeline Section
        item {
            Text(
                text = "Chronological Medical History & Reports",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        }

        if (records.isEmpty()) {
            item {
                Surface(
                    color = MedicalNavySurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No medical records loaded yet.",
                        color = TextSecondaryDark,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(records) { record ->
                MedicalRecordTimelineCard(record = record, dateFormat = dateFormat)
            }
        }
    }

    if (showAddMeasurementDialog) {
        AlertDialog(
            onDismissRequest = { showAddMeasurementDialog = false },
            title = {
                Text(
                    text = "Log Clinical Measurement",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newSysBp,
                        onValueChange = { newSysBp = it },
                        label = { Text("Systolic BP (mmHg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDiaBp,
                        onValueChange = { newDiaBp = it },
                        label = { Text("Diastolic BP (mmHg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newHr,
                        onValueChange = { newHr = it },
                        label = { Text("Resting Heart Rate (bpm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sys = newSysBp.toDoubleOrNull() ?: 120.0
                        val dia = newDiaBp.toDoubleOrNull() ?: 80.0
                        val hr = newHr.toDoubleOrNull() ?: 72.0
                        viewModel.addManualMeasurement("BP", sys, dia, "mmHg")
                        viewModel.addManualMeasurement("HEART_RATE", hr, null, "bpm")
                        showAddMeasurementDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseCyan)
                ) {
                    Text("Save to Heart Memory")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMeasurementDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MedicalNavyCard
        )
    }
}

@Composable
fun MedicalRecordTimelineCard(
    record: MedicalRecordEntity,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("record_card_${record.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = when (record.recordType) {
                        "EVENT" -> CardioRed.copy(alpha = 0.2f)
                        "ECG_REPORT" -> PulseCyan.copy(alpha = 0.2f)
                        "BLOOD_TEST" -> EmeraldHealth.copy(alpha = 0.2f)
                        else -> MedicalNavySurface
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = record.recordType,
                        color = when (record.recordType) {
                            "EVENT" -> CardioRedLight
                            "ECG_REPORT" -> PulseCyanLight
                            "BLOOD_TEST" -> EmeraldHealthLight
                            else -> TextPrimaryDark
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateFormat.format(Date(record.dateMillis)),
                    fontSize = 11.sp,
                    color = TextTertiaryDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = record.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = record.description,
                fontSize = 12.sp,
                color = TextSecondaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${record.clinicianName} • ${record.facility}",
                    fontSize = 11.sp,
                    color = TextTertiaryDark
                )

                Surface(
                    color = EmeraldHealth.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = record.reviewStatus,
                        color = EmeraldHealthLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            if (record.documentUrlOrPath != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Source Document: ${record.documentUrlOrPath}",
                    fontSize = 10.sp,
                    color = PulseCyanLight
                )
            }
        }
    }
}
