package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.ClinicalStudyCard
import com.example.ui.components.OfficialGuidelineCard
import com.example.ui.components.OpenFdaReportCard
import com.example.ui.components.PubMedArticleCard
import com.example.ui.theme.*

@Composable
fun EvidenceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.evidenceQuery.collectAsState()
    val isLoading by viewModel.isEvidenceLoading.collectAsState()
    val evidenceResult by viewModel.evidenceResult.collectAsState()
    val synthesisText by viewModel.evidenceSynthesisText.collectAsState()

    var searchQueryText by remember { mutableStateOf(query) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("PubMed / NCBI", "ClinicalTrials.gov", "openFDA Data", "WHO & India Protocols")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("evidence_screen"),
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
                                .background(PulseCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Evidence",
                                tint = PulseCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Live Medical Evidence Engine",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "NCBI PubMed • ClinicalTrials v2 • openFDA • WHO HEARTS",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQueryText,
                            onValueChange = { searchQueryText = it },
                            placeholder = { Text("e.g. Acute Coronary Syndrome, Hypertension, Statin") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("evidence_search_input"),
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PulseCyan)
                            }
                        )

                        Button(
                            onClick = { viewModel.loadEvidence(searchQueryText) },
                            colors = ButtonDefaults.buttonColors(containerColor = PulseCyan),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("evidence_search_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Search Live",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset search chips
                    Text(
                        text = "Quick Clinical Queries:",
                        fontSize = 11.sp,
                        color = TextTertiaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Acute Coronary", "Chest Pain Triage", "Dual Antiplatelet").forEach { chip ->
                            SuggestionChip(
                                onClick = {
                                    searchQueryText = chip
                                    viewModel.loadEvidence(chip)
                                },
                                label = { Text(chip, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Live Research Agent Synthesis
        if (!synthesisText.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PulseCyan.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Agent 3: Live Medical Research Synthesis",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseCyanLight
                            )
                            Surface(
                                color = EmeraldHealth.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = evidenceResult?.status ?: "LIVE RETRIEVED",
                                    color = EmeraldHealthLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = synthesisText!!,
                            fontSize = 12.sp,
                            color = TextPrimaryDark,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MedicalNavyCard,
                contentColor = PulseCyan,
                edgePadding = 0.dp
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

        // Tab Content
        val result = evidenceResult
        when (selectedTab) {
            0 -> {
                if (result != null && result.pubMedArticles.isNotEmpty()) {
                    items(result.pubMedArticles) { article ->
                        PubMedArticleCard(article = article)
                    }
                } else {
                    item {
                        EmptyEvidencePlaceholder("No PubMed articles loaded yet. Tap search or select a query.")
                    }
                }
            }
            1 -> {
                if (result != null && result.clinicalStudies.isNotEmpty()) {
                    items(result.clinicalStudies) { study ->
                        ClinicalStudyCard(study = study)
                    }
                } else {
                    item {
                        EmptyEvidencePlaceholder("No active clinical trials loaded for this condition.")
                    }
                }
            }
            2 -> {
                if (result != null && result.fdaReports.isNotEmpty()) {
                    items(result.fdaReports) { report ->
                        OpenFdaReportCard(report = report)
                    }
                } else {
                    item {
                        EmptyEvidencePlaceholder("No openFDA adverse reaction events found.")
                    }
                }
            }
            3 -> {
                if (result != null && result.officialGuidelines.isNotEmpty()) {
                    items(result.officialGuidelines) { doc ->
                        OfficialGuidelineCard(doc = doc)
                    }
                } else {
                    item {
                        EmptyEvidencePlaceholder("No official guidelines available.")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyEvidencePlaceholder(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        color = MedicalNavySurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = TextSecondaryDark,
                fontSize = 12.sp
            )
        }
    }
}
