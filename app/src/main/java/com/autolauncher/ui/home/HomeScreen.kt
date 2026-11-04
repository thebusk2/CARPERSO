package com.autolauncher.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.autolauncher.ui.apps.AppDrawerPanel
import com.autolauncher.ui.apps.DockBar
import com.autolauncher.ui.customize.ColorPickerPanel
import com.autolauncher.ui.customize.VehiclePickerPanel
import kotlinx.coroutines.delay

/**
 * Pantalla principal del launcher.
 * Layout landscape-first pensado para pantallas de 1024×600 a 2000×1200.
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │  ClockWidget (izq)  │  VehicleDisplay (centro)  │ ... │
 * │─────────────────────────────────────────────────────────│
 * │                   DockBar (abajo)                       │
 * └─────────────────────────────────────────────────────────┘
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Tick cada segundo para el reloj
    var tickMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            tickMs = System.currentTimeMillis()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Cuerpo principal (reloj + vehículo + botones de acceso rápido) ──
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // deja espacio para el dock
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Panel izquierdo: reloj + fecha + botones de personalización
            ClockPanel(
                timeMs = tickMs,
                onOpenVehiclePicker = { onEvent(HomeEvent.ShowPanel(Panel.VEHICLE_PICKER)) },
                onOpenColorPicker = { onEvent(HomeEvent.ShowPanel(Panel.COLOR_PICKER)) },
                onOpenApps = { onEvent(HomeEvent.ShowPanel(Panel.APP_DRAWER)) },
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
            )

            // Centro: visualización del vehículo seleccionado
            VehicleDisplay(
                vehicle = uiState.selectedVehicle,
                colorIndex = uiState.selectedVehicleColorIndex,
                accentColor = androidx.compose.ui.graphics.Color(uiState.interfaceColor),
                onTap = { onEvent(HomeEvent.ShowPanel(Panel.VEHICLE_PICKER)) },
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
            )
        }

        // ── Dock de apps ancladas (siempre visible abajo) ──
        DockBar(
            pinnedApps = uiState.pinnedApps,
            isEditing = uiState.isEditingApps,
            onLaunch = { pkg -> onEvent(HomeEvent.LaunchApp(pkg)) },
            onUnpin = { pkg -> onEvent(HomeEvent.UnpinApp(pkg)) },
            onReorder = { from, to -> onEvent(HomeEvent.ReorderPinnedApps(from, to)) },
            onToggleEdit = { onEvent(HomeEvent.ToggleEditApps) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        // ── Paneles superpuestos (slide-in desde abajo) ──
        AnimatedVisibility(
            visible = uiState.activePanel == Panel.VEHICLE_PICKER,
            enter = slideInVertically(tween(300)) { it },
            exit = slideOutVertically(tween(250)) { it }
        ) {
            VehiclePickerPanel(
                currentVehicleId = uiState.selectedVehicle?.id ?: "",
                currentColorIndex = uiState.selectedVehicleColorIndex,
                onSelectVehicle = { id -> onEvent(HomeEvent.SelectVehicle(id)) },
                onSelectColor = { idx -> onEvent(HomeEvent.SelectVehicleColor(idx)) },
                onDismiss = { onEvent(HomeEvent.DismissPanel) },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = uiState.activePanel == Panel.COLOR_PICKER,
            enter = slideInVertically(tween(300)) { it },
            exit = slideOutVertically(tween(250)) { it }
        ) {
            ColorPickerPanel(
                currentColor = uiState.interfaceColor,
                onSelectColor = { color -> onEvent(HomeEvent.SelectInterfaceColor(color)) },
                onDismiss = { onEvent(HomeEvent.DismissPanel) },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = uiState.activePanel == Panel.APP_DRAWER,
            enter = slideInVertically(tween(300)) { it },
            exit = slideOutVertically(tween(250)) { it }
        ) {
            AppDrawerPanel(
                apps = uiState.apps,
                pinnedApps = uiState.pinnedApps,
                isEditing = uiState.isEditingApps,
                onLaunch = { pkg -> onEvent(HomeEvent.LaunchApp(pkg)) },
                onPin = { pkg -> onEvent(HomeEvent.PinApp(pkg)) },
                onUnpin = { pkg -> onEvent(HomeEvent.UnpinApp(pkg)) },
                onReorder = { from, to -> onEvent(HomeEvent.ReorderApps(from, to)) },
                onToggleEdit = { onEvent(HomeEvent.ToggleEditApps) },
                onDismiss = { onEvent(HomeEvent.DismissPanel) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
