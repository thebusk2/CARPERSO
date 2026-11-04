package com.autolauncher.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.autolauncher.data.VehicleModel

/**
 * Visualización central del vehículo seleccionado.
 * Dibuja el modelo del vehículo en Canvas con el color elegido,
 * con efectos de reflejo, sombra y acento de la interfaz.
 */
@Composable
fun VehicleDisplay(
    vehicle: VehicleModel?,
    colorIndex: Int,
    accentColor: Color,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (vehicle == null) return

    val vehicleColor = vehicle.availableColors.getOrElse(colorIndex) {
        vehicle.availableColors.firstOrNull()
    } ?: return

    val animatedVehicleColor by animateColorAsState(
        targetValue = vehicleColor.color,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "vehicleColor"
    )
    val animatedAccent by animateColorAsState(
        targetValue = accentColor,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "accentColor"
    )

    Box(
        modifier = modifier
            .clickable(onClick = onTap)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Nombre del vehículo ──
            Text(
                text = "${vehicle.brand} ${vehicle.displayName}",
                style = MaterialTheme.typography.headlineMedium,
                color = animatedAccent,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = vehicle.category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ── Foto real del vehículo con fallback a silueta Canvas ──
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .aspectRatio(2.4f),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = vehicle.photoUri,
                    contentDescription = "${vehicle.brand} ${vehicle.displayName}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawVehicleSilhouette(
                                category = vehicle.category,
                                bodyColor = animatedVehicleColor,
                                accentColor = animatedAccent,
                                windowColor = Color(0xFF88BBDD).copy(alpha = 0.6f)
                            )
                        }
                    },
                    error = {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawVehicleSilhouette(
                                category = vehicle.category,
                                bodyColor = animatedVehicleColor,
                                accentColor = animatedAccent,
                                windowColor = Color(0xFF88BBDD).copy(alpha = 0.6f)
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Indicador de color seleccionado ──
            Text(
                text = vehicleColor.nameEs,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            // ── Paleta de colores del vehículo (mini dots) ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                vehicle.availableColors.forEachIndexed { idx, vc ->
                    val isSelected = idx == colorIndex
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 20.dp else 14.dp)
                            .clip(CircleShape)
                            .background(vc.color)
                            .then(
                                if (isSelected) Modifier.background(
                                    color = Color.Transparent,
                                    shape = CircleShape
                                ) else Modifier
                            )
                    )
                }
            }
        }
    }
}

/**
 * Dibuja la silueta vectorial del vehículo en Canvas.
 * Cada categoría tiene su propia forma característica.
 */
private fun DrawScope.drawVehicleSilhouette(
    category: com.autolauncher.data.VehicleCategory,
    bodyColor: Color,
    accentColor: Color,
    windowColor: Color
) {
    val w = size.width
    val h = size.height

    // Sombra del suelo
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent),
            center = Offset(w / 2f, h * 0.92f),
            radius = w * 0.45f
        ),
        topLeft = Offset(w * 0.05f, h * 0.80f),
        size = Size(w * 0.90f, h * 0.22f)
    )

    when (category) {
        com.autolauncher.data.VehicleCategory.SEDAN -> drawSedan(w, h, bodyColor, accentColor, windowColor)
        com.autolauncher.data.VehicleCategory.SUV -> drawSuv(w, h, bodyColor, accentColor, windowColor)
        com.autolauncher.data.VehicleCategory.PICKUP -> drawPickup(w, h, bodyColor, accentColor, windowColor)
        com.autolauncher.data.VehicleCategory.HATCHBACK -> drawHatchback(w, h, bodyColor, accentColor, windowColor)
        com.autolauncher.data.VehicleCategory.COUPE -> drawCoupe(w, h, bodyColor, accentColor, windowColor)
        com.autolauncher.data.VehicleCategory.VAN -> drawVan(w, h, bodyColor, accentColor, windowColor)
    }
}

/** Silueta de sedán clásico */
private fun DrawScope.drawSedan(w: Float, h: Float, body: Color, accent: Color, window: Color) {
    val bodyBottom = h * 0.80f
    val bodyTop = h * 0.42f
    val roofTop = h * 0.15f

    // Carrocería principal
    val bodyPath = Path().apply {
        moveTo(w * 0.04f, bodyBottom)
        lineTo(w * 0.04f, bodyTop)
        lineTo(w * 0.20f, bodyTop)
        cubicTo(w * 0.25f, bodyTop, w * 0.28f, roofTop, w * 0.33f, roofTop)
        lineTo(w * 0.67f, roofTop)
        cubicTo(w * 0.72f, roofTop, w * 0.75f, bodyTop, w * 0.80f, bodyTop)
        lineTo(w * 0.96f, bodyTop)
        lineTo(w * 0.96f, bodyBottom)
        close()
    }
    drawPath(bodyPath, body)

    // Reflejo superior (highlight)
    val highlightPath = Path().apply {
        moveTo(w * 0.20f, bodyTop - 2f)
        cubicTo(w * 0.28f, bodyTop, w * 0.30f, roofTop + 5, w * 0.35f, roofTop + 2)
        lineTo(w * 0.65f, roofTop + 2)
        cubicTo(w * 0.70f, roofTop + 5, w * 0.72f, bodyTop, w * 0.80f, bodyTop - 2f)
        lineTo(w * 0.80f, bodyTop + 8)
        cubicTo(w * 0.72f, bodyTop + 8, w * 0.70f, roofTop + 12, w * 0.65f, roofTop + 10)
        lineTo(w * 0.35f, roofTop + 10)
        cubicTo(w * 0.30f, roofTop + 12, w * 0.28f, bodyTop + 8, w * 0.20f, bodyTop + 8)
        close()
    }
    drawPath(highlightPath, Color.White.copy(alpha = 0.18f))

    // Ventanas
    val winPath = Path().apply {
        moveTo(w * 0.22f, bodyTop + 4)
        cubicTo(w * 0.26f, bodyTop, w * 0.29f, roofTop + 4, w * 0.34f, roofTop + 4)
        lineTo(w * 0.49f, roofTop + 4)
        lineTo(w * 0.49f, bodyTop + 2)
        close()
    }
    drawPath(winPath, window)

    val winPath2 = Path().apply {
        moveTo(w * 0.51f, roofTop + 4)
        lineTo(w * 0.66f, roofTop + 4)
        cubicTo(w * 0.71f, roofTop + 4, w * 0.74f, bodyTop, w * 0.78f, bodyTop + 4)
        lineTo(w * 0.51f, bodyTop + 2)
        close()
    }
    drawPath(winPath2, window)

    // Ruedas
    drawCircle(Color(0xFF222222), radius = h * 0.17f, center = Offset(w * 0.21f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.10f, center = Offset(w * 0.21f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.04f, center = Offset(w * 0.21f, bodyBottom))

    drawCircle(Color(0xFF222222), radius = h * 0.17f, center = Offset(w * 0.78f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.10f, center = Offset(w * 0.78f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.04f, center = Offset(w * 0.78f, bodyBottom))

    // Luces
    drawRect(accent.copy(alpha = 0.9f), topLeft = Offset(w * 0.04f, bodyTop + 4), size = Size(w * 0.04f, h * 0.08f))
    drawRect(Color(0xFFFFDD88).copy(alpha = 0.85f), topLeft = Offset(w * 0.92f, bodyTop + 4), size = Size(w * 0.04f, h * 0.08f))
}

/** Silueta de SUV */
private fun DrawScope.drawSuv(w: Float, h: Float, body: Color, accent: Color, window: Color) {
    val bodyBottom = h * 0.80f
    val bodyTop = h * 0.38f
    val roofTop = h * 0.10f

    val bodyPath = Path().apply {
        moveTo(w * 0.04f, bodyBottom)
        lineTo(w * 0.04f, bodyTop)
        lineTo(w * 0.15f, bodyTop)
        lineTo(w * 0.18f, roofTop)
        lineTo(w * 0.82f, roofTop)
        lineTo(w * 0.85f, bodyTop)
        lineTo(w * 0.96f, bodyTop)
        lineTo(w * 0.96f, bodyBottom)
        close()
    }
    drawPath(bodyPath, body)
    drawPath(Path().apply {
        moveTo(w * 0.15f, bodyTop + 2); lineTo(w * 0.18f, roofTop + 2)
        lineTo(w * 0.49f, roofTop + 2); lineTo(w * 0.49f, bodyTop + 2); close()
    }, window)
    drawPath(Path().apply {
        moveTo(w * 0.51f, roofTop + 2); lineTo(w * 0.82f, roofTop + 2)
        lineTo(w * 0.85f, bodyTop + 2); lineTo(w * 0.51f, bodyTop + 2); close()
    }, window)
    drawPath(Path().apply {
        moveTo(w * 0.15f, bodyTop); lineTo(w * 0.18f, roofTop); lineTo(w * 0.82f, roofTop)
        lineTo(w * 0.85f, bodyTop); lineTo(w * 0.80f, bodyTop + 10); lineTo(w * 0.84f, roofTop + 8)
        lineTo(w * 0.16f, roofTop + 8); lineTo(w * 0.20f, bodyTop + 10); close()
    }, Color.White.copy(alpha = 0.15f))
    drawCircle(Color(0xFF222222), radius = h * 0.19f, center = Offset(w * 0.20f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.11f, center = Offset(w * 0.20f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.045f, center = Offset(w * 0.20f, bodyBottom))
    drawCircle(Color(0xFF222222), radius = h * 0.19f, center = Offset(w * 0.80f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.11f, center = Offset(w * 0.80f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.045f, center = Offset(w * 0.80f, bodyBottom))
    drawRect(accent.copy(alpha = 0.9f), topLeft = Offset(w * 0.04f, bodyTop + 4), size = Size(w * 0.04f, h * 0.09f))
    drawRect(Color(0xFFFFDD88).copy(alpha = 0.85f), topLeft = Offset(w * 0.92f, bodyTop + 4), size = Size(w * 0.04f, h * 0.09f))
}

/** Silueta de pickup */
private fun DrawScope.drawPickup(w: Float, h: Float, body: Color, accent: Color, window: Color) {
    val bodyBottom = h * 0.80f
    val bodyTop = h * 0.40f
    val cabTop = h * 0.14f

    // Cajón
    drawRect(body, topLeft = Offset(w * 0.50f, bodyTop), size = Size(w * 0.46f, bodyBottom - bodyTop))
    // Separador de cajón
    drawRect(Color.Black.copy(alpha = 0.3f), topLeft = Offset(w * 0.50f, bodyTop), size = Size(h * 0.02f, bodyBottom - bodyTop))
    // Cabina
    val cabPath = Path().apply {
        moveTo(w * 0.04f, bodyBottom); lineTo(w * 0.04f, bodyTop)
        lineTo(w * 0.20f, bodyTop); lineTo(w * 0.22f, cabTop)
        lineTo(w * 0.52f, cabTop); lineTo(w * 0.52f, bodyTop)
        lineTo(w * 0.52f, bodyBottom); close()
    }
    drawPath(cabPath, body)
    drawPath(Path().apply {
        moveTo(w * 0.22f, cabTop + 2); lineTo(w * 0.49f, cabTop + 2)
        lineTo(w * 0.49f, bodyTop); lineTo(w * 0.22f, bodyTop); close()
    }, window)
    // Ruedas
    drawCircle(Color(0xFF222222), radius = h * 0.19f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.11f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.045f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(Color(0xFF222222), radius = h * 0.19f, center = Offset(w * 0.78f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.11f, center = Offset(w * 0.78f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.045f, center = Offset(w * 0.78f, bodyBottom))
    drawRect(accent.copy(alpha = 0.9f), topLeft = Offset(w * 0.04f, bodyTop + 4), size = Size(w * 0.04f, h * 0.09f))
    drawRect(Color(0xFFFFDD88).copy(alpha = 0.85f), topLeft = Offset(w * 0.93f, bodyTop + 4), size = Size(w * 0.03f, h * 0.07f))
}

/** Silueta de hatchback */
private fun DrawScope.drawHatchback(w: Float, h: Float, body: Color, accent: Color, window: Color) {
    val bodyBottom = h * 0.80f
    val bodyTop = h * 0.42f
    val roofTop = h * 0.12f

    val bodyPath = Path().apply {
        moveTo(w * 0.05f, bodyBottom); lineTo(w * 0.05f, bodyTop)
        lineTo(w * 0.22f, bodyTop)
        cubicTo(w * 0.26f, bodyTop, w * 0.28f, roofTop, w * 0.32f, roofTop)
        lineTo(w * 0.74f, roofTop)
        cubicTo(w * 0.78f, roofTop, w * 0.82f, bodyTop, w * 0.85f, bodyTop)
        lineTo(w * 0.95f, bodyTop); lineTo(w * 0.95f, bodyBottom); close()
    }
    drawPath(bodyPath, body)
    drawPath(Path().apply {
        moveTo(w * 0.24f, bodyTop); cubicTo(w * 0.27f, bodyTop, w * 0.30f, roofTop + 2, w * 0.33f, roofTop + 2)
        lineTo(w * 0.49f, roofTop + 2); lineTo(w * 0.49f, bodyTop); close()
    }, window)
    drawPath(Path().apply {
        moveTo(w * 0.51f, roofTop + 2); lineTo(w * 0.73f, roofTop + 2)
        cubicTo(w * 0.76f, roofTop + 2, w * 0.79f, bodyTop, w * 0.83f, bodyTop)
        lineTo(w * 0.51f, bodyTop); close()
    }, window)
    drawCircle(Color(0xFF222222), radius = h * 0.17f, center = Offset(w * 0.22f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.10f, center = Offset(w * 0.22f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.04f, center = Offset(w * 0.22f, bodyBottom))
    drawCircle(Color(0xFF222222), radius = h * 0.17f, center = Offset(w * 0.78f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.10f, center = Offset(w * 0.78f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.04f, center = Offset(w * 0.78f, bodyBottom))
    drawRect(accent.copy(alpha = 0.9f), topLeft = Offset(w * 0.05f, bodyTop + 4), size = Size(w * 0.04f, h * 0.08f))
    drawRect(Color(0xFFFFDD88).copy(alpha = 0.85f), topLeft = Offset(w * 0.91f, bodyTop + 4), size = Size(w * 0.04f, h * 0.08f))
}

/** Silueta de coupé */
private fun DrawScope.drawCoupe(w: Float, h: Float, body: Color, accent: Color, window: Color) {
    val bodyBottom = h * 0.80f
    val bodyTop = h * 0.46f
    val roofTop = h * 0.14f

    val bodyPath = Path().apply {
        moveTo(w * 0.03f, bodyBottom); lineTo(w * 0.03f, bodyTop)
        lineTo(w * 0.15f, bodyTop)
        cubicTo(w * 0.22f, bodyTop, w * 0.26f, roofTop, w * 0.33f, roofTop)
        lineTo(w * 0.70f, roofTop)
        cubicTo(w * 0.80f, roofTop, w * 0.90f, bodyTop + 10, w * 0.97f, bodyTop)
        lineTo(w * 0.97f, bodyBottom); close()
    }
    drawPath(bodyPath, body)
    drawPath(Path().apply {
        moveTo(w * 0.17f, bodyTop); cubicTo(w * 0.24f, bodyTop, w * 0.27f, roofTop + 2, w * 0.34f, roofTop + 2)
        lineTo(w * 0.49f, roofTop + 2); lineTo(w * 0.49f, bodyTop); close()
    }, window)
    drawPath(Path().apply {
        moveTo(w * 0.51f, roofTop + 2); lineTo(w * 0.69f, roofTop + 2)
        cubicTo(w * 0.78f, roofTop + 2, w * 0.88f, bodyTop + 8, w * 0.95f, bodyTop)
        lineTo(w * 0.51f, bodyTop); close()
    }, window)
    drawCircle(Color(0xFF222222), radius = h * 0.17f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.10f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.04f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(Color(0xFF222222), radius = h * 0.17f, center = Offset(w * 0.80f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.10f, center = Offset(w * 0.80f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.04f, center = Offset(w * 0.80f, bodyBottom))
    drawRect(accent.copy(alpha = 0.9f), topLeft = Offset(w * 0.03f, bodyTop + 4), size = Size(w * 0.04f, h * 0.08f))
    drawRect(Color(0xFFFFDD88).copy(alpha = 0.85f), topLeft = Offset(w * 0.93f, bodyTop + 2), size = Size(w * 0.04f, h * 0.08f))
}

/** Silueta de van/furgoneta */
private fun DrawScope.drawVan(w: Float, h: Float, body: Color, accent: Color, window: Color) {
    val bodyBottom = h * 0.80f
    val bodyTop = h * 0.35f
    val roofTop = h * 0.08f

    val bodyPath = Path().apply {
        moveTo(w * 0.04f, bodyBottom); lineTo(w * 0.04f, bodyTop)
        lineTo(w * 0.12f, bodyTop); lineTo(w * 0.14f, roofTop)
        lineTo(w * 0.86f, roofTop); lineTo(w * 0.88f, bodyTop)
        lineTo(w * 0.96f, bodyTop); lineTo(w * 0.96f, bodyBottom); close()
    }
    drawPath(bodyPath, body)
    drawRect(window, topLeft = Offset(w * 0.14f, roofTop + 2), size = Size(w * 0.35f, bodyTop - roofTop - 4))
    drawRect(window, topLeft = Offset(w * 0.52f, roofTop + 2), size = Size(w * 0.34f, bodyTop - roofTop - 4))
    drawCircle(Color(0xFF222222), radius = h * 0.19f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.11f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.045f, center = Offset(w * 0.19f, bodyBottom))
    drawCircle(Color(0xFF222222), radius = h * 0.19f, center = Offset(w * 0.81f, bodyBottom))
    drawCircle(Color(0xFF444444), radius = h * 0.11f, center = Offset(w * 0.81f, bodyBottom))
    drawCircle(accent.copy(alpha = 0.8f), radius = h * 0.045f, center = Offset(w * 0.81f, bodyBottom))
    drawRect(accent.copy(alpha = 0.9f), topLeft = Offset(w * 0.04f, bodyTop + 4), size = Size(w * 0.04f, h * 0.09f))
    drawRect(Color(0xFFFFDD88).copy(alpha = 0.85f), topLeft = Offset(w * 0.92f, bodyTop + 4), size = Size(w * 0.04f, h * 0.09f))
}
