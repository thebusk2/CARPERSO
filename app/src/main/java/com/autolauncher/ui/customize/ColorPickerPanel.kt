package com.autolauncher.ui.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.autolauncher.data.Preferences.Companion.PRESET_COLORS

/**
 * Panel de selección de color de la interfaz.
 * Ofrece colores predefinidos y un deslizador HSV para personalización total.
 */
@Composable
fun ColorPickerPanel(
    currentColor: Long,
    onSelectColor: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColor by remember { mutableStateOf(Color(currentColor)) }
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(0.8f) }
    var brightness by remember { mutableFloatStateOf(0.8f) }

    // Inicializar sliders desde el color actual
    LaunchedEffect(currentColor) {
        val c = android.graphics.Color.valueOf(Color(currentColor).value.toLong().toInt())
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            (c.red() * 255).toInt(),
            (c.green() * 255).toInt(),
            (c.blue() * 255).toInt(),
            hsv
        )
        hue = hsv[0]; saturation = hsv[1]; brightness = hsv[2]
        selectedColor = Color(currentColor)
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ── Barra de título ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Color de la Interfaz",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = {
                        onSelectColor(
                            selectedColor.value.toLong().let { raw ->
                                // Asegurar que sea un Long con alpha 0xFF
                                (raw and 0xFFFFFFFFL).let { if (it ushr 24 == 0L) it or 0xFF000000L else it }
                            }
                        )
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Check, "Aplicar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // ── Preview del color seleccionado ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(selectedColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vista previa de la interfaz",
                    color = if (selectedColor.luminance() > 0.4f) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Colores predefinidos ──
            Text(
                "Colores predefinidos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(PRESET_COLORS) { preset ->
                    val presetColor = Color(preset)
                    val isSelected = selectedColor.red == presetColor.red &&
                        selectedColor.green == presetColor.green &&
                        selectedColor.blue == presetColor.blue
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(presetColor)
                            .then(
                                if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            )
                            .clickable {
                                selectedColor = presetColor
                                val hsv = FloatArray(3)
                                android.graphics.Color.RGBToHSV(
                                    (presetColor.red * 255).toInt(),
                                    (presetColor.green * 255).toInt(),
                                    (presetColor.blue * 255).toInt(), hsv
                                )
                                hue = hsv[0]; saturation = hsv[1]; brightness = hsv[2]
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Sliders HSV ──
            Text("Personalizado (HSV)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))

            ColorSlider(label = "Tono", value = hue, range = 0f..360f, color = selectedColor) { v ->
                hue = v
                selectedColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness)))
            }
            ColorSlider(label = "Saturación", value = saturation, range = 0f..1f, color = selectedColor) { v ->
                saturation = v
                selectedColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness)))
            }
            ColorSlider(label = "Brillo", value = brightness, range = 0f..1f, color = selectedColor) { v ->
                brightness = v
                selectedColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness)))
            }

            Spacer(Modifier.height(16.dp))

            // ── Botón aplicar ──
            Button(
                onClick = {
                    val hsv = floatArrayOf(hue, saturation, brightness)
                    val argb = android.graphics.Color.HSVToColor(hsv)
                    onSelectColor(argb.toLong() and 0xFFFFFFFFL or 0xFF000000L)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Aplicar color", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                if (range.endInclusive > 1f) "%.0f°".format(value) else "%.0f%%".format(value * 100),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}

private fun Color.luminance(): Float {
    val r = (red * 255).toInt() / 255f
    val g = (green * 255).toInt() / 255f
    val b = (blue * 255).toInt() / 255f
    return 0.299f * r + 0.587f * g + 0.114f * b
}
