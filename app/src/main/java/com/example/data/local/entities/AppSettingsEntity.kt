package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val geminiApiKey: String = "",
    val selectedGeminiModel: String = "gemini-3.8-flash",
    val ncbiApiKey: String = "",
    val ncbiEmail: String = "cardio.intelligence@yaqeenallah.org",
    val openFdaApiKey: String = "",
    val modelTemperature: Double = 0.2,
    val maxOutputTokens: Int = 2048,
    val requestTimeoutSeconds: Int = 30,
    val dailyUsageCount: Int = 14,
    val dailyUsageLimit: Int = 500,
    val lastTestStatus: String = "INITIALIZED",
    val lastTestTimestamp: Long = System.currentTimeMillis(),
    val currentRole: String = "PATIENT" // PATIENT, DOCTOR, NURSE, ADMIN
)
