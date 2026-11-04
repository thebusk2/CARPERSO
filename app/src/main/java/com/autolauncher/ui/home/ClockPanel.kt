package com.autolauncher.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Panel izquierdo de la pantalla principal:
 * - Reloj digital grande
 * - Fecha
 * - Botones de acceso rápido a personalización
 */
@Composable
fun ClockPanel(
    timeMs: Long,
    onOpenVehiclePicker: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onOpenApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val date = remember(timeMs) { Date(timeMs) }
    val timeStr = remember(timeMs) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }
    val dateStr = remember(timeMs) {
        SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(date).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Reloj ──
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeStr,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = dateStr,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Botones de acceso rápido ──
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionButton(
                icon = Icons.Default.DirectionsCar,
                label = "Mi Auto",
                onClick = onOpenVehiclePicker
            )
            QuickActionButton(
                icon = Icons.Default.ColorLens,
                label = "Color",
                onClick = onOpenColorPicker
            )
            QuickActionButton(
                icon = Icons.Default.Apps,
                label = "Aplicaciones",
                onClick = onOpenApps
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
