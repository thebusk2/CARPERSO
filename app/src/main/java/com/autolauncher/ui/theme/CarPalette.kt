package com.autolauncher.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Genera esquemas de color derivados del color primario elegido por el usuario.
 * Implementa Material3 color roles manualmente para evitar dependencia de API 31+.
 */
object CarPalette {

    fun darkScheme(primary: Color): ColorScheme {
        val primaryDark = primary.darken(0.15f)
        val primaryContainer = primary.darken(0.5f)
        val onPrimary = if (primary.luminance() > 0.4f) Color(0xFF000000) else Color(0xFFFFFFFF)
        val surface = Color(0xFF121212)
        val surfaceVariant = Color(0xFF1E1E2E)
        val onSurface = Color(0xFFE0E0E0)

        return darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = Color(0xFFE0E0FF),
            secondary = primary.lighten(0.2f),
            onSecondary = Color(0xFF000000),
            secondaryContainer = primaryDark,
            onSecondaryContainer = Color(0xFFCCCCFF),
            tertiary = primary.hueShift(60f),
            background = Color(0xFF0A0A0F),
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = Color(0xFFB0B0C0),
            outline = Color(0xFF555566),
            error = Color(0xFFCF6679),
            onError = Color(0xFF000000),
        )
    }

    fun lightScheme(primary: Color): ColorScheme {
        val primaryLight = primary.lighten(0.1f)
        val surface = Color(0xFFF5F5FF)

        return lightColorScheme(
            primary = primary,
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = primary.lighten(0.5f),
            onPrimaryContainer = primary.darken(0.5f),
            secondary = primaryLight,
            onSecondary = Color(0xFFFFFFFF),
            background = surface,
            surface = surface,
            onSurface = Color(0xFF1A1A2E),
            surfaceVariant = Color(0xFFE8E8F0),
            onSurfaceVariant = Color(0xFF44446A),
            outline = Color(0xFFAAAAAA),
            error = Color(0xFFB00020),
            onError = Color(0xFFFFFFFF),
        )
    }

    // Helpers de color
    private fun Color.luminance(): Float {
        val r = red.toLinear()
        val g = green.toLinear()
        val b = blue.toLinear()
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }

    private fun Float.toLinear(): Float =
        if (this <= 0.04045f) this / 12.92f
        else Math.pow(((this + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()

    private fun Color.darken(amount: Float): Color {
        return Color(
            red = (red * (1f - amount)).coerceIn(0f, 1f),
            green = (green * (1f - amount)).coerceIn(0f, 1f),
            blue = (blue * (1f - amount)).coerceIn(0f, 1f),
            alpha = alpha
        )
    }

    private fun Color.lighten(amount: Float): Color {
        return Color(
            red = (red + (1f - red) * amount).coerceIn(0f, 1f),
            green = (green + (1f - green) * amount).coerceIn(0f, 1f),
            blue = (blue + (1f - blue) * amount).coerceIn(0f, 1f),
            alpha = alpha
        )
    }

    private fun Color.hueShift(degrees: Float): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            (red * 255).toInt(),
            (green * 255).toInt(),
            (blue * 255).toInt(),
            hsv
        )
        hsv[0] = (hsv[0] + degrees) % 360f
        val shifted = android.graphics.Color.HSVToColor(hsv)
        return Color(shifted)
    }
}
