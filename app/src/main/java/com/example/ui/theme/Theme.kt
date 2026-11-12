package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CardioRed,
    onPrimary = Color.White,
    primaryContainer = CardioRedDark,
    onPrimaryContainer = Color.White,
    secondary = PulseCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003F5C),
    onSecondaryContainer = PulseCyanLight,
    tertiary = EmeraldHealth,
    onTertiary = Color.White,
    background = MedicalNavyDark,
    onBackground = TextPrimaryDark,
    surface = MedicalNavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = MedicalNavySurface,
    onSurfaceVariant = TextSecondaryDark,
    outline = MedicalNavyBorder,
    error = CardioRedLight,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CardioRedDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = CardioRedDark,
    secondary = Color(0xFF0077B6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = Color(0xFF006064),
    tertiary = EmeraldHealth,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    error = CardioRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep clinical dark mode for high-contrast cardiology charts
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
