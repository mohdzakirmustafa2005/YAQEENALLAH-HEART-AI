package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.local.entities.CarePlanEntity
import com.example.ui.MainViewModel
import com.example.ui.components.UnresolvedSignalCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CardioContinuityScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val carePlan by viewModel.carePlan.collectAsState()
    val signals by viewModel.unresolvedSignals.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Care Plan & Milestones", "Unresolved Health Signals (${signals.count { it.status == "OPEN" }})")

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("continuity_screen"),
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
                                .background(WarningOrange.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LinearScale,
                                contentDescription = "Continuity",
                                tint = WarningOrange
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cardio Continuity OS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Post-Discharge 7/30/90-Day Trajectory & Signal Tracker",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MedicalNavyCard,
                contentColor = PulseCyan
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Care Plan Content
            if (carePlan != null) {
                val plan = carePlan!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Plan ID: ${plan.id}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PulseCyanLight
                                )
                                Surface(
                                    color = EmeraldHealth.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = plan.status,
                                        color = EmeraldHealthLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Primary Diagnosis: ${plan.primaryDiagnosis}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Discharged: ${dateFormat.format(Date(plan.dischargeDate))} • Attending: ${plan.assignedDoctor}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Post-Discharge Follow-Up Milestones:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 7-Day Milestone
                            MilestoneRow(
                                label = "Day-7 Follow-Up (Early Hemodynamic Stability & Wound Review)",
                                isCompleted = plan.day7FollowUpDone,
                                onToggle = { viewModel.toggleCareMilestone(7) }
                            )

                            // 30-Day Milestone
                            MilestoneRow(
                                label = "Day-30 Follow-Up (Dual Antiplatelet Adherence & Metabolic Panel)",
                                isCompleted = plan.day30FollowUpDone,
                                onToggle = { viewModel.toggleCareMilestone(30) }
                            )

                            // 90-Day Milestone
                            MilestoneRow(
                                label = "Day-90 Follow-Up (Echocardiogram & Cardiac Rehabilitation)",
                                isCompleted = plan.day90FollowUpDone,
                                onToggle = { viewModel.toggleCareMilestone(90) }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                color = MedicalNavySurface,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Clinician-Approved Care & Medication Instructions:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarningAmber
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = plan.approvedCareInstructions,
                                        fontSize = 12.sp,
                                        color = TextPrimaryDark,
                                        lineHeight = 17.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Next Scheduled Investigation: ${plan.nextInvestigation} (${dateFormat.format(Date(plan.nextAppointmentDate))})",
                                fontSize = 11.sp,
                                color = PulseCyanLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                item {
                    Text("No active care plan assigned for this patient.", color = TextSecondaryDark)
                }
            }
        } else {
            // Unresolved Health Signals List
            item {
                Text(
                    text = "Unresolved Health Signal Tracker (Mandatory Resolution)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }

            if (signals.isEmpty()) {
                item {
                    Text("No unresolved signals on file. Care continuity healthy.", color = EmeraldHealthLight)
                }
            } else {
                items(signals) { signal ->
                    UnresolvedSignalCard(
                        signal = signal,
                        onAcknowledge = { viewModel.acknowledgeSignal(signal) },
                        onResolve = { notes, evidence -> viewModel.resolveSignal(signal, notes, evidence) }
                    )
                }
            }
        }
    }
}

@Composable
fun MilestoneRow(
    label: String,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = if (isCompleted) EmeraldHealth.copy(alpha = 0.1f) else MedicalNavySurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = EmeraldHealth)
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = if (isCompleted) EmeraldHealthLight else TextPrimaryDark
                )
            }
        }
    }
}
