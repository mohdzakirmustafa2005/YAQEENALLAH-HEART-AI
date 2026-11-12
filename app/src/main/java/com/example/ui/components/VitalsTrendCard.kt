package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.HealthMeasurementEntity
import com.example.ui.theme.*

@Composable
fun VitalsTrendCard(
    measurements: List<HealthMeasurementEntity>,
    modifier: Modifier = Modifier
) {
    val bpList = measurements.filter { it.type == "BP" }.sortedBy { it.timestamp }
    val hrList = measurements.filter { it.type == "HEART_RATE" }.sortedBy { it.timestamp }

    val latestBp = bpList.lastOrNull()
    val previousBp = if (bpList.size > 1) bpList[bpList.size - 2] else null

    val latestHr = hrList.lastOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vitals_trend_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CardioRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Vitals",
                            tint = CardioRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Cardiovascular Vitals Trend",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Surface(
                    color = EmeraldHealth.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "WHO Guideline Active",
                        color = EmeraldHealthLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Latest stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // BP Box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MedicalNavySurface,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "BLOOD PRESSURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (latestBp != null) "${latestBp.primaryValue.toInt()}/${latestBp.secondaryValue?.toInt() ?: "--"}" else "--/--",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (latestBp != null && latestBp.primaryValue >= 140) WarningOrange else PulseCyan
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "mmHg",
                                fontSize = 11.sp,
                                color = TextTertiaryDark,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        if (previousBp != null) {
                            Text(
                                text = "Prev: ${previousBp.primaryValue.toInt()}/${previousBp.secondaryValue?.toInt() ?: "--"}",
                                fontSize = 10.sp,
                                color = TextTertiaryDark
                            )
                        }
                    }
                }

                // HR Box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MedicalNavySurface,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "RESTING HEART RATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (latestHr != null) "${latestHr.primaryValue.toInt()}" else "--",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldHealthLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "bpm",
                                fontSize = 11.sp,
                                color = TextTertiaryDark,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "Target: 60 - 80 bpm",
                            fontSize = 10.sp,
                            color = TextTertiaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Trend Graph
            if (bpList.size >= 2) {
                Text(
                    text = "Systolic BP Trajectory (Past Readings)",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(MedicalNavyDark, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxVal = 160f
                        val minVal = 100f
                        val widthStep = size.width / (bpList.size - 1)

                        // Target line 130
                        val targetY = size.height * (1f - (130f - minVal) / (maxVal - minVal))
                        drawLine(
                            color = WarningAmber.copy(alpha = 0.4f),
                            start = Offset(0f, targetY),
                            end = Offset(size.width, targetY),
                            strokeWidth = 2f
                        )

                        val path = Path()
                        bpList.forEachIndexed { index, item ->
                            val x = index * widthStep
                            val y = size.height * (1f - ((item.primaryValue.toFloat() - minVal) / (maxVal - minVal)).coerceIn(0f, 1f))
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                            drawCircle(
                                color = PulseCyan,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawPath(
                            path = path,
                            color = PulseCyan,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = TextTertiaryDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reference: WHO Hypertension Protocol (Target < 130/80 mmHg in CAD)",
                    fontSize = 11.sp,
                    color = TextTertiaryDark
                )
            }
        }
    }
}
