package com.autolauncher.ui.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.autolauncher.data.VehicleCategory
import com.autolauncher.data.VehicleCatalog
import com.autolauncher.data.VehicleModel

/**
 * Panel de selección de modelo y color del vehículo.
 */
@Composable
fun VehiclePickerPanel(
    currentVehicleId: String,
    currentColorIndex: Int,
    onSelectVehicle: (String) -> Unit,
    onSelectColor: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<VehicleCategory?>(null) }
    val categories = VehicleCategory.entries.toList()
    val filteredVehicles = remember(selectedCategory) {
        if (selectedCategory == null) VehicleCatalog.ALL
        else VehicleCatalog.byCategory(selectedCategory!!)
    }
    val currentVehicle = VehicleCatalog.findById(currentVehicleId)

    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ── Título ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Seleccionar Vehículo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Cerrar")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // ── Filtros de categoría ──
            Text(
                "Categoría",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todos") }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text(cat.displayName) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Grid de vehículos ──
            Text(
                "${filteredVehicles.size} modelos disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Agrupar de 3 en fila
                val rows = filteredVehicles.chunked(3)
                items(rows) { rowVehicles ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowVehicles.forEach { vehicle ->
                            VehicleCard(
                                vehicle = vehicle,
                                isSelected = vehicle.id == currentVehicleId,
                                currentColorIndex = if (vehicle.id == currentVehicleId) currentColorIndex else 0,
                                onSelect = { onSelectVehicle(vehicle.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Rellenar con espacios vacíos si la fila tiene menos de 3
                        repeat(3 - rowVehicles.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Selector de color (si hay vehículo seleccionado) ──
            if (currentVehicle.availableColors.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    "Color: ${currentVehicle.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(currentVehicle.availableColors.size) { idx ->
                        val vehicleColor = currentVehicle.availableColors[idx]
                        val isSelected = idx == currentColorIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onSelectColor(idx) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(vehicleColor.color)
                                    .then(
                                        if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                vehicleColor.nameEs,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(60.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: VehicleModel,
    isSelected: Boolean,
    currentColorIndex: Int,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicleColor = vehicle.availableColors.getOrElse(currentColorIndex) { vehicle.availableColors.first() }

    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End).size(16.dp)
                )
            } else {
                Spacer(Modifier.height(16.dp))
            }

            // Mini silueta del vehículo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawVehicleMiniSilhouette(vehicle.category, vehicleColor.color)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    vehicle.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    vehicle.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

/** Silueta minimalista para las tarjetas del selector */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawVehicleMiniSilhouette(
    category: VehicleCategory,
    bodyColor: Color
) {
    val w = size.width
    val h = size.height
    val path = androidx.compose.ui.graphics.Path()

    when (category) {
        VehicleCategory.SEDAN, VehicleCategory.HATCHBACK -> {
            path.moveTo(w * 0.05f, h * 0.85f); path.lineTo(w * 0.05f, h * 0.55f)
            path.lineTo(w * 0.22f, h * 0.55f); path.cubicTo(w * 0.28f, h * 0.55f, w * 0.30f, h * 0.18f, w * 0.36f, h * 0.18f)
            path.lineTo(w * 0.65f, h * 0.18f); path.cubicTo(w * 0.70f, h * 0.18f, w * 0.74f, h * 0.55f, w * 0.78f, h * 0.55f)
            path.lineTo(w * 0.95f, h * 0.55f); path.lineTo(w * 0.95f, h * 0.85f); path.close()
        }
        VehicleCategory.SUV -> {
            path.moveTo(w * 0.05f, h * 0.85f); path.lineTo(w * 0.05f, h * 0.50f)
            path.lineTo(w * 0.15f, h * 0.50f); path.lineTo(w * 0.18f, h * 0.12f)
            path.lineTo(w * 0.82f, h * 0.12f); path.lineTo(w * 0.85f, h * 0.50f)
            path.lineTo(w * 0.95f, h * 0.50f); path.lineTo(w * 0.95f, h * 0.85f); path.close()
        }
        VehicleCategory.PICKUP -> {
            path.moveTo(w * 0.05f, h * 0.85f); path.lineTo(w * 0.05f, h * 0.50f)
            path.lineTo(w * 0.20f, h * 0.50f); path.lineTo(w * 0.22f, h * 0.15f)
            path.lineTo(w * 0.52f, h * 0.15f); path.lineTo(w * 0.52f, h * 0.50f)
            path.lineTo(w * 0.95f, h * 0.50f); path.lineTo(w * 0.95f, h * 0.85f); path.close()
        }
        VehicleCategory.COUPE -> {
            path.moveTo(w * 0.03f, h * 0.85f); path.lineTo(w * 0.03f, h * 0.58f)
            path.lineTo(w * 0.15f, h * 0.58f); path.cubicTo(w * 0.22f, h * 0.58f, w * 0.26f, h * 0.18f, w * 0.33f, h * 0.18f)
            path.lineTo(w * 0.70f, h * 0.18f); path.cubicTo(w * 0.80f, h * 0.18f, w * 0.90f, h * 0.58f, w * 0.97f, h * 0.58f)
            path.lineTo(w * 0.97f, h * 0.85f); path.close()
        }
        VehicleCategory.VAN -> {
            path.moveTo(w * 0.05f, h * 0.85f); path.lineTo(w * 0.05f, h * 0.45f)
            path.lineTo(w * 0.12f, h * 0.45f); path.lineTo(w * 0.14f, h * 0.08f)
            path.lineTo(w * 0.86f, h * 0.08f); path.lineTo(w * 0.88f, h * 0.45f)
            path.lineTo(w * 0.95f, h * 0.45f); path.lineTo(w * 0.95f, h * 0.85f); path.close()
        }
    }
    drawPath(path, bodyColor)
    // Rueditas mini
    drawCircle(Color(0xFF333333), radius = h * 0.14f, center = androidx.compose.ui.geometry.Offset(w * 0.21f, h * 0.85f))
    drawCircle(Color(0xFF333333), radius = h * 0.14f, center = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.85f))
}
