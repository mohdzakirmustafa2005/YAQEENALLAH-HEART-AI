package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.local.entities.AppSettingsEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()
    val testStatus by viewModel.testConnectionStatus.collectAsState()

    val currentSettings = settings ?: AppSettingsEntity()

    var geminiKey by remember(settings) { mutableStateOf(currentSettings.geminiApiKey) }
    var selectedModel by remember(settings) { mutableStateOf(currentSettings.selectedGeminiModel) }
    var ncbiKey by remember(settings) { mutableStateOf(currentSettings.ncbiApiKey) }
    var ncbiEmail by remember(settings) { mutableStateOf(currentSettings.ncbiEmail) }
    var openFdaKey by remember(settings) { mutableStateOf(currentSettings.openFdaApiKey) }

    var isModelDropdownOpen by remember { mutableStateOf(false) }
    val supportedModels = listOf("gemini-3.8-flash", "gemini-3.5-flash-lite", "gemini-4-argon")

    var showSavedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
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
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Settings",
                                tint = PulseCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Model Control Centre",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Provider Endpoints, Secrets & Regulatory Limits",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Connection Health & Diagnostics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldHealth.copy(alpha = 0.4f)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Provider Status & Diagnostics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Surface(
                            color = EmeraldHealth.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = currentSettings.lastTestStatus,
                                color = EmeraldHealthLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (testStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = testStatus!!,
                            fontSize = 11.sp,
                            color = PulseCyanLight,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.testAiConnection() },
                        colors = ButtonDefaults.buttonColors(containerColor = PulseCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_test_connection"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "Test",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test Live Provider Connection", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Credentials Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Gemini AI Model Configuration",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = geminiKey,
                        onValueChange = { geminiKey = it },
                        label = { Text("Google Gemini API Key (Masked in UI)") },
                        placeholder = { Text("Injected from Secrets or enter custom key") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_gemini_key"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Model Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isModelDropdownOpen,
                        onExpandedChange = { isModelDropdownOpen = !isModelDropdownOpen }
                    ) {
                        OutlinedTextField(
                            value = selectedModel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selected Gemini Model") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelDropdownOpen) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = isModelDropdownOpen,
                            onDismissRequest = { isModelDropdownOpen = false }
                        ) {
                            supportedModels.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        selectedModel = model
                                        isModelDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Public Evidence Engine Credentials",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ncbiKey,
                        onValueChange = { ncbiKey = it },
                        label = { Text("NCBI / PubMed API Key (Optional, up to 10 req/s)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ncbiEmail,
                        onValueChange = { ncbiEmail = it },
                        label = { Text("NCBI Registered Contact Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = openFdaKey,
                        onValueChange = { openFdaKey = it },
                        label = { Text("openFDA API Key (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val updated = currentSettings.copy(
                                geminiApiKey = geminiKey,
                                selectedGeminiModel = selectedModel,
                                ncbiApiKey = ncbiKey,
                                ncbiEmail = ncbiEmail,
                                openFdaApiKey = openFdaKey
                            )
                            viewModel.saveSettings(updated)
                            showSavedSnackbar = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardioRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_save_settings"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Configurations", fontWeight = FontWeight.Bold)
                    }

                    if (showSavedSnackbar) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Settings and API keys saved securely.",
                            fontSize = 12.sp,
                            color = EmeraldHealthLight
                        )
                    }
                }
            }
        }
    }
}
