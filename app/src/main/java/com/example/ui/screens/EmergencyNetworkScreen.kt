package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.EmergencyEventEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EmergencyNetworkScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val emergencyEvents by viewModel.emergencyEvents.collectAsState()
    val patient by viewModel.selectedPatient.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("emergency_network_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardioRed))
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
                                    .background(CardioRed, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Emergency Network",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Cardio Emergency Network",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Immediate 112 Triage & Casualty Alert Queue",
                                    fontSize = 11.sp,
                                    color = CardioRedLight
                                )
                            }
                        }

                        Surface(
                            color = CardioRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ACTIVE GATEWAY",
                                color = CardioRedLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Deterministic Emergency Protocol: Potential emergencies trigger immediate instruction to dial 112 in India. Hospital alert queues and designated caregiver SMS dispatches occur simultaneously.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:112")
                            }
                            context.startActivity(dialIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardioRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dial_112_direct"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call 112",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DIAL 112 (INDIA EMERGENCY HELPLINE)", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Active Alert Queue
        item {
            Text(
                text = "Hospital Emergency Alert Queue (${emergencyEvents.size} Incidents)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        }

        if (emergencyEvents.isEmpty()) {
            item {
                Surface(
                    color = MedicalNavySurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Safe",
                            tint = EmeraldHealthLight,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No active emergency alerts recorded. All vitals and symptoms stable.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        } else {
            items(emergencyEvents) { event ->
                EmergencyEventCard(
                    event = event,
                    dateFormat = dateFormat,
                    onAcknowledge = { viewModel.acknowledgeEmergencyEvent(event) }
                )
            }
        }
    }
}

@Composable
fun EmergencyEventCard(
    event: EmergencyEventEntity,
    dateFormat: SimpleDateFormat,
    onAcknowledge: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_card_${event.eventId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (event.hospitalQueueStatus == "DELIVERED") CardioRed else EmeraldHealth
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = CardioRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "INCIDENT ID: ${event.eventId}",
                        color = CardioRedLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateFormat.format(Date(event.timestamp)),
                    fontSize = 11.sp,
                    color = TextTertiaryDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Symptoms: ${event.symptomsSummary}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = MedicalNavySurface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "112 Protocol: ${event.emergency112Status}",
                        fontSize = 11.sp,
                        color = PulseCyanLight,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Casualty Queue: ${event.hospitalQueueStatus}",
                        fontSize = 11.sp,
                        color = if (event.hospitalQueueStatus == "DELIVERED") WarningOrange else EmeraldHealthLight,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Designated Caregiver Notification: ${if (event.caregiverNotified) "DELIVERED VIA SMS" else "FAILED"}",
                        fontSize = 11.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            if (event.acknowledgedByStaff != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Acknowledged by ${event.acknowledgedByStaff} at ${dateFormat.format(Date(event.acknowledgedAt ?: event.timestamp))}",
                    fontSize = 11.sp,
                    color = EmeraldHealthLight
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAcknowledge,
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalNavySurface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Acknowledge",
                        tint = PulseCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Staff Acknowledge Incident", fontSize = 12.sp, color = PulseCyan)
                }
            }
        }
    }
}
