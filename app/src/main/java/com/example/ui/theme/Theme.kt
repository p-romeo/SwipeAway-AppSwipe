package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = White,
    primaryContainer = MutedBlueSurface,
    onPrimaryContainer = ElectricBlue,
    secondary = BrightCyan,
    onSecondary = DeepNavy,
    tertiary = HotPink,
    onTertiary = White,
    background = White,
    onBackground = DeepNavy,
    surface = White,
    onSurface = DeepNavy,
    surfaceVariant = MutedBlueSurface,
    onSurfaceVariant = TextGray,
    outline = BorderGray,
    error = DangerRed,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = White,
    primaryContainer = DeepNavy,
    onPrimaryContainer = ElectricBlue,
    secondary = BrightCyan,
    onSecondary = DeepNavy,
    tertiary = HotPink,
    onTertiary = White,
    background = DeepNavy,
    onBackground = White,
    surface = Color(0xFF0D1C44), // cohesive dark slate navy
    onSurface = White,
    surfaceVariant = Color(0xFF14285D),
    onSurfaceVariant = BorderGray,
    outline = TextGray,
    error = DangerRed,
    onError = White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We default dynamicColor to true to embrace Material You and dynamic theming on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) androidx.compose.material3.dynamicDarkColorScheme(context) else androidx.compose.material3.dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
