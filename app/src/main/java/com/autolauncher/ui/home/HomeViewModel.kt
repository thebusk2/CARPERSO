package com.autolauncher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.autolauncher.data.AppItem
import com.autolauncher.data.AppRepository
import com.autolauncher.data.Preferences
import com.autolauncher.data.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prefs: Preferences,
    private val appRepo: AppRepository,
    private val vehicleRepo: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadApps()
        observePreferences()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val allApps = appRepo.loadApps()
            _uiState.update { it.copy(apps = allApps) }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                prefs.interfaceColor,
                prefs.vehicleId,
                prefs.vehicleColorIndex,
                prefs.appOrderJson,
                prefs.pinnedAppsJson
            ) { color, vehicleId, colorIdx, orderJson, pinnedJson ->
                Tuple5(color, vehicleId, colorIdx, orderJson, pinnedJson)
            }.collect { (color, vehicleId, colorIdx, orderJson, pinnedJson) ->
                val vehicle = vehicleRepo.findById(vehicleId)
                val allApps = _uiState.value.apps.ifEmpty { appRepo.loadApps() }

                // Aplicar orden
                val orderedPackages = appRepo.jsonToPackages(orderJson)
                val orderedApps = appRepo.applyOrder(allApps, orderedPackages)

                // Apps ancladas en el dock
                val pinnedPackages = appRepo.jsonToPackages(pinnedJson)
                val pinnedItems = pinnedPackages.mapNotNull { pkg ->
                    orderedApps.firstOrNull { it.packageName == pkg }
                }

                _uiState.update {
                    it.copy(
                        interfaceColor = color,
                        selectedVehicle = vehicle,
                        selectedVehicleColorIndex = colorIdx,
                        apps = orderedApps,
                        pinnedApps = pinnedItems
                    )
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectVehicle -> viewModelScope.launch {
                prefs.setVehicleId(event.vehicleId)
                _uiState.update { it.copy(activePanel = Panel.NONE) }
            }
            is HomeEvent.SelectVehicleColor -> viewModelScope.launch {
                prefs.setVehicleColorIndex(event.colorIndex)
            }
            is HomeEvent.SelectInterfaceColor -> viewModelScope.launch {
                prefs.setInterfaceColor(event.color)
            }
            is HomeEvent.LaunchApp -> appRepo.launch(event.packageName)
            is HomeEvent.ReorderApps -> {
                val current = _uiState.value.apps.toMutableList()
                if (event.from < 0 || event.to < 0 ||
                    event.from >= current.size || event.to >= current.size) return
                val item = current.removeAt(event.from)
                current.add(event.to, item)
                _uiState.update { it.copy(apps = current) }
                viewModelScope.launch {
                    prefs.setAppOrderJson(
                        appRepo.packagesToJson(current.map { it.packageName })
                    )
                }
            }
            is HomeEvent.PinApp -> {
                val current = _uiState.value.pinnedApps.toMutableList()
                val app = _uiState.value.apps.firstOrNull { it.packageName == event.packageName }
                if (app != null && current.none { it.packageName == event.packageName }) {
                    current.add(app)
                    _uiState.update { it.copy(pinnedApps = current) }
                    viewModelScope.launch {
                        prefs.setPinnedAppsJson(
                            appRepo.packagesToJson(current.map { it.packageName })
                        )
                    }
                }
            }
            is HomeEvent.UnpinApp -> {
                val current = _uiState.value.pinnedApps.toMutableList()
                current.removeAll { it.packageName == event.packageName }
                _uiState.update { it.copy(pinnedApps = current) }
                viewModelScope.launch {
                    prefs.setPinnedAppsJson(
                        appRepo.packagesToJson(current.map { it.packageName })
                    )
                }
            }
            is HomeEvent.ReorderPinnedApps -> {
                val current = _uiState.value.pinnedApps.toMutableList()
                if (event.from < 0 || event.to < 0 ||
                    event.from >= current.size || event.to >= current.size) return
                val item = current.removeAt(event.from)
                current.add(event.to, item)
                _uiState.update { it.copy(pinnedApps = current) }
                viewModelScope.launch {
                    prefs.setPinnedAppsJson(
                        appRepo.packagesToJson(current.map { it.packageName })
                    )
                }
            }
            is HomeEvent.ShowPanel -> _uiState.update { it.copy(activePanel = event.panel) }
            is HomeEvent.DismissPanel -> _uiState.update { it.copy(activePanel = Panel.NONE) }
            is HomeEvent.ToggleEditApps -> _uiState.update { it.copy(isEditingApps = !it.isEditingApps) }
            is HomeEvent.RefreshApps -> loadApps()
        }
    }

    // Tupla auxiliar para combine de 5 flujos
    private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

    class Factory(
        private val prefs: Preferences,
        private val appRepo: AppRepository,
        private val vehicleRepo: VehicleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(prefs, appRepo, vehicleRepo) as T
    }
}
