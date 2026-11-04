package com.autolauncher.ui.home

import com.autolauncher.data.AppItem
import com.autolauncher.data.VehicleModel

/**
 * Estado completo de la pantalla principal del launcher.
 */
data class HomeUiState(
    // ── Configuración visual ──
    val interfaceColor: Long = 0xFF1565C0,
    val isDarkTheme: Boolean = true,

    // ── Vehículo ──
    val selectedVehicle: VehicleModel? = null,
    val selectedVehicleColorIndex: Int = 0,

    // ── Aplicaciones ──
    val apps: List<AppItem> = emptyList(),
    val pinnedApps: List<AppItem> = emptyList(),
    val isEditingApps: Boolean = false,

    // ── Paneles de personalización ──
    val activePanel: Panel = Panel.NONE,

    // ── Tiempo ──
    val currentTimeMs: Long = System.currentTimeMillis(),

    // ── Scroll de categorías de vehículo ──
    val vehicleCategoryFilter: String = "Todos"
)

enum class Panel {
    NONE,            // Pantalla principal normal
    VEHICLE_PICKER,  // Selector de modelo de vehículo
    COLOR_PICKER,    // Selector de color de interfaz
    APP_DRAWER,      // Cajón de todas las apps
    SETTINGS         // Ajustes generales
}

/**
 * Eventos del usuario hacia el ViewModel.
 */
sealed interface HomeEvent {
    data class SelectVehicle(val vehicleId: String) : HomeEvent
    data class SelectVehicleColor(val colorIndex: Int) : HomeEvent
    data class SelectInterfaceColor(val color: Long) : HomeEvent
    data class LaunchApp(val packageName: String) : HomeEvent
    data class ReorderApps(val from: Int, val to: Int) : HomeEvent
    data class PinApp(val packageName: String) : HomeEvent
    data class UnpinApp(val packageName: String) : HomeEvent
    data class ReorderPinnedApps(val from: Int, val to: Int) : HomeEvent
    data class ShowPanel(val panel: Panel) : HomeEvent
    data object DismissPanel : HomeEvent
    data object ToggleEditApps : HomeEvent
    data object RefreshApps : HomeEvent
}
