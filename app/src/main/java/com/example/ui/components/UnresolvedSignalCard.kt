package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UnresolvedSignalEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UnresolvedSignalCard(
    signal: UnresolvedSignalEntity,
    onAcknowledge: () -> Unit,
    onResolve: (notes: String, evidence: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showResolveDialog by remember { mutableStateOf(false) }
    var closureNotes by remember { mutableStateOf("") }
    var closureEvidence by remember { mutableStateOf("Follow-up consultation conducted; vitals normalized.") }
    var showAuditHistory by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val isCritical = signal.priority == "CRITICAL"
    val isHigh = signal.priority == "HIGH"

    val borderColor = when {
        isCritical -> CardioRed
        isHigh -> WarningOrange
        else -> MedicalNavyBorder
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("signal_card_${signal.signalId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Priority & Signal ID & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriorityBadge(priority = signal.priority)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = signal.signalId,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PulseCyanLight
                    )
                }

                SignalStatusBadge(status = signal.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = signal.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = signal.description,
                fontSize = 12.sp,
                color = TextSecondaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MedicalNavySurface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Owner: ${signal.assignedOwner}",
                        fontSize = 11.sp,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Due: ${dateFormat.format(Date(signal.dueDate))}",
                        fontSize = 11.sp,
                        color = if (signal.dueDate < System.currentTimeMillis() && signal.status != "RESOLVED") CardioRedLight else TextSecondaryDark
                    )
                }
            }

            if (signal.closureNotes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Closure Notes: ${signal.closureNotes}",
                    fontSize = 11.sp,
                    color = EmeraldHealthLight,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            if (signal.status != "RESOLVED") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (signal.status == "OPEN") {
                        Button(
                            onClick = onAcknowledge,
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalNavySurface),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("ack_signal_${signal.signalId}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Acknowledge",
                                tint = PulseCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Acknowledge", fontSize = 12.sp, color = PulseCyan)
                        }
                    }

                    Button(
                        onClick = { showResolveDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldHealth),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("resolve_signal_${signal.signalId}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Resolve",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Resolve Signal", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            // Audit history toggle
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { showAuditHistory = !showAuditHistory },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (showAuditHistory) "Hide Audit Trail" else "View Audit Trail & Provenance",
                    fontSize = 11.sp,
                    color = TextTertiaryDark
                )
            }

            if (showAuditHistory) {
                Surface(
                    color = MedicalNavyDark,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = signal.auditHistory,
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    if (showResolveDialog) {
        AlertDialog(
            onDismissRequest = { showResolveDialog = false },
            title = {
                Text(
                    text = "Close Health Signal: ${signal.signalId}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter clinical closure verification and evidence:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = closureNotes,
                        onValueChange = { closureNotes = it },
                        label = { Text("Closure Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Patient contacted, medication resumed") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = closureEvidence,
                        onValueChange = { closureEvidence = it },
                        label = { Text("Closure Evidence") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResolve(closureNotes.ifBlank { "Care continuity task completed by clinician." }, closureEvidence)
                        showResolveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldHealth)
                ) {
                    Text("Confirm Resolution")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MedicalNavyCard
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (color, text) = when (priority) {
        "CRITICAL" -> Pair(CardioRed, "CRITICAL")
        "HIGH" -> Pair(WarningOrange, "HIGH")
        "MEDIUM" -> Pair(WarningAmber, "MEDIUM")
        else -> Pair(PulseCyan, "LOW")
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SignalStatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "OPEN" -> Pair(CardioRed.copy(alpha = 0.15f), CardioRedLight)
        "ACKNOWLEDGED" -> Pair(WarningAmber.copy(alpha = 0.2f), WarningAmber)
        "RESOLVED" -> Pair(EmeraldHealth.copy(alpha = 0.2f), EmeraldHealthLight)
        else -> Pair(MedicalNavySurface, TextSecondaryDark)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
