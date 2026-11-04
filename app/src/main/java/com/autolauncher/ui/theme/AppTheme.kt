package com.autolauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.autolauncher.ui.theme.CarPalette

@Composable
fun AutoLauncherTheme(
    seedColor: Long = 0xFF1565C0,
    darkTheme: Boolean = true, // Los launchers de coche usan dark por defecto
    content: @Composable () -> Unit
) {
    val primary = Color(seedColor)
    val colorScheme = if (darkTheme) {
        CarPalette.darkScheme(primary)
    } else {
        CarPalette.lightScheme(primary)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CarTypography,
        content = content
    )
}
