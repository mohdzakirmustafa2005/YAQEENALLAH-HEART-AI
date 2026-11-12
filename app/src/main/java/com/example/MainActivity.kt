package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.EmergencyBanner
import com.example.ui.screens.*
import com.example.ui.theme.CardioRed
import com.example.ui.theme.MedicalNavyCard
import com.example.ui.theme.MedicalNavyDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.TextPrimaryDark

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val patient by viewModel.selectedPatient.collectAsState()
                val activeEmergency by viewModel.activeEmergencyAlert.collectAsState()
                val signals by viewModel.unresolvedSignals.collectAsState()
                val openSignalsCount = signals.count { it.status == "OPEN" }

                // System BackHandler
                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    viewModel.navigateTo(AppScreen.HOME)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MedicalNavyDark,
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = when (currentScreen) {
                                            AppScreen.HOME -> "YAQEENALLAH HEART AI"
                                            AppScreen.SYMPTOM_INTAKE -> "Symptom Intelligence"
                                            AppScreen.EVIDENCE_EXPLORER -> "Medical Evidence Engine"
                                            AppScreen.HEART_MEMORY -> "Heart Memory AI"
                                            AppScreen.CARDIO_CONTINUITY -> "Cardio Continuity OS"
                                            AppScreen.EMERGENCY_NETWORK -> "Cardio Emergency Network"
                                            AppScreen.HOSPITAL_DASHBOARD -> "Hospital AI Intelligence"
                                            AppScreen.SETTINGS -> "AI Model Control Centre"
                                        },
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = patient?.hospitalName ?: "Hyderabad Health Region",
                                        fontSize = 11.sp,
                                        color = PulseCyan
                                    )
                                }
                            },
                            navigationIcon = {
                                if (currentScreen != AppScreen.HOME) {
                                    IconButton(
                                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                                        modifier = Modifier.testTag("top_bar_back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.triggerEmergencySosManual() },
                                    modifier = Modifier.testTag("top_bar_sos_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "SOS",
                                        tint = CardioRed
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                                    modifier = Modifier.testTag("top_bar_settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MedicalNavyCard
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MedicalNavyCard,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.HOME,
                                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                                icon = {
                                    Icon(Icons.Default.Home, contentDescription = "Home")
                                },
                                label = { Text("Home", fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_item_home")
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.SYMPTOM_INTAKE,
                                onClick = { viewModel.navigateTo(AppScreen.SYMPTOM_INTAKE) },
                                icon = {
                                    Icon(Icons.Default.MedicalServices, contentDescription = "Symptoms")
                                },
                                label = { Text("Symptoms", fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_item_symptoms")
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.EVIDENCE_EXPLORER,
                                onClick = { viewModel.navigateTo(AppScreen.EVIDENCE_EXPLORER) },
                                icon = {
                                    Icon(Icons.Default.Science, contentDescription = "Evidence")
                                },
                                label = { Text("Evidence", fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_item_evidence")
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.HEART_MEMORY,
                                onClick = { viewModel.navigateTo(AppScreen.HEART_MEMORY) },
                                icon = {
                                    Icon(Icons.Default.Favorite, contentDescription = "Heart Memory")
                                },
                                label = { Text("Memory", fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_item_memory")
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.CARDIO_CONTINUITY,
                                onClick = { viewModel.navigateTo(AppScreen.CARDIO_CONTINUITY) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (openSignalsCount > 0) {
                                            Badge(containerColor = CardioRed) {
                                                Text("$openSignalsCount")
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Default.LinearScale, contentDescription = "Continuity")
                                    }
                                },
                                label = { Text("Continuity", fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_item_continuity")
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Global Emergency Banner when alert is active
                        if (activeEmergency != null) {
                            EmergencyBanner(
                                alert = activeEmergency!!,
                                onDismiss = { viewModel.dismissEmergencyAlert() }
                            )
                        }

                        // Main Screen Display
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                patient = patient,
                                onNavigate = { s -> viewModel.navigateTo(s) }
                            )
                            AppScreen.SYMPTOM_INTAKE -> SymptomScreen(viewModel = viewModel)
                            AppScreen.EVIDENCE_EXPLORER -> EvidenceScreen(viewModel = viewModel)
                            AppScreen.HEART_MEMORY -> HeartMemoryScreen(viewModel = viewModel)
                            AppScreen.CARDIO_CONTINUITY -> CardioContinuityScreen(viewModel = viewModel)
                            AppScreen.EMERGENCY_NETWORK -> EmergencyNetworkScreen(viewModel = viewModel)
                            AppScreen.HOSPITAL_DASHBOARD -> HospitalIntelligenceScreen(
                                viewModel = viewModel,
                                onNavigate = { s -> viewModel.navigateTo(s) }
                            )
                            AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
